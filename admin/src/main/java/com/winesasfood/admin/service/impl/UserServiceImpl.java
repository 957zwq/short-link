package com.winesasfood.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.winesasfood.admin.common.errorcode.BaseErrorCode;
import com.winesasfood.admin.common.exception.ClientException;
import com.winesasfood.admin.dto.req.UserLoginReqDTO;
import com.winesasfood.admin.dto.req.UserRegisterReqDTO;
import com.winesasfood.admin.dto.req.UserUpdateReqDTO;
import com.winesasfood.admin.dto.resp.UserActualRespDTO;
import com.winesasfood.admin.dto.resp.UserLoginRespDTO;
import com.winesasfood.admin.dto.resp.UserRespDTO;
import com.winesasfood.admin.dao.entity.User;
import com.winesasfood.admin.dao.mapper.UserMapper;
import com.winesasfood.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final String LOGIN_TOKEN_KEY_PREFIX = "short-link:user:login:token:";
    private static final long TOKEN_EXPIRE_DAYS = 7;

    private final UserMapper userMapper;
    private final RBloomFilter<String> userRegisterBloomFilter;
    private final RedissonClient redissonClient;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public UserRespDTO getUserByUsername(String username) {
        LambdaQueryWrapper<User> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new ClientException("用户不存在");
        }
        UserRespDTO dto = new UserRespDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRealName(user.getRealName());
        dto.setPhone(user.getPhone());
        dto.setMail(user.getMail());
        return dto;
    }

    @Override
    public UserActualRespDTO getActualUserByUsername(String username) {
        LambdaQueryWrapper<User> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new ClientException("用户不存在");
        }
        UserActualRespDTO dto = new UserActualRespDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRealName(user.getRealName());
        dto.setPhone(user.getPhone());
        dto.setMail(user.getMail());
        return dto;
    }

    @Override
    public boolean isUsernameExists(String username) {
        // 先通过布隆过滤器快速判断
        return userRegisterBloomFilter.contains(username);
    }

    @Override
    public void register(UserRegisterReqDTO request) {
        // 布隆过滤器判断用户名是否存在（可能存在误判）
        if (userRegisterBloomFilter.contains(request.getUsername())) {
            // 布隆过滤器显示可能存在，再查数据库确认
            LambdaQueryWrapper<User> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.eq(User::getUsername, request.getUsername());
            Long count = userMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new ClientException(BaseErrorCode.USER_NAME_EXIST_ERROR);
            }
        }

        // 检查手机号是否已存在
        LambdaQueryWrapper<User> phoneQuery = Wrappers.lambdaQuery();
        phoneQuery.eq(User::getPhone, request.getPhone());
        Long phoneCount = userMapper.selectCount(phoneQuery);
        if (phoneCount > 0) {
            throw new ClientException("手机号已被注册");
        }

        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setMail(request.getMail());
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.setDelFlag(0);

        int inserted = userMapper.insert(user);
        if (inserted > 0) {
            // 注册成功，将用户名加入布隆过滤器
            userRegisterBloomFilter.add(request.getUsername());
        }
    }

    @Override
    public void update(UserUpdateReqDTO request) {
        // 查询用户是否存在
        LambdaQueryWrapper<User> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(User::getUsername, request.getUsername());
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new ClientException("用户不存在");
        }

        // 如果修改了手机号，检查新手机号是否已被其他用户使用
        if (StringUtils.hasText(request.getPhone()) && !request.getPhone().equals(user.getPhone())) {
            LambdaQueryWrapper<User> phoneQuery = Wrappers.lambdaQuery();
            phoneQuery.eq(User::getPhone, request.getPhone())
                    .ne(User::getUsername, request.getUsername());
            Long phoneCount = userMapper.selectCount(phoneQuery);
            if (phoneCount > 0) {
                throw new ClientException("手机号已被其他用户使用");
            }
        }

        // 更新非空字段
        if (StringUtils.hasText(request.getRealName())) {
            user.setRealName(request.getRealName());
        }
        if (StringUtils.hasText(request.getPhone())) {
            user.setPhone(request.getPhone());
        }
        if (StringUtils.hasText(request.getMail())) {
            user.setMail(request.getMail());
        }
        user.setUpdateTime(new Date());

        userMapper.updateById(user);
    }

    @Override
    public UserLoginRespDTO login(UserLoginReqDTO request) {
        // 查询用户
        LambdaQueryWrapper<User> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(User::getUsername, request.getUsername());
        User user = userMapper.selectOne(queryWrapper);

        if (user == null) {
            throw new ClientException(BaseErrorCode.USER_LOGIN_NOT_EXIST);
        }

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ClientException(BaseErrorCode.USER_LOGIN_PASSWORD_ERROR);
        }

        // 生成token
        String token = UUID.randomUUID().toString().replace("-", "");

        // 存储token到Redis，key格式: short-link:user:login:token:{username}
        String tokenKey = LOGIN_TOKEN_KEY_PREFIX + request.getUsername();
        RBucket<String> bucket = redissonClient.getBucket(tokenKey);
        bucket.set(token, TOKEN_EXPIRE_DAYS, TimeUnit.DAYS);

        return UserLoginRespDTO.builder()
                .token(token)
                .username(request.getUsername())
                .build();
    }

    @Override
    public boolean checkLogin(String username, String token) {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(token)) {
            return false;
        }

        String tokenKey = LOGIN_TOKEN_KEY_PREFIX + username;
        RBucket<String> bucket = redissonClient.getBucket(tokenKey);

        if (!bucket.isExists()) {
            return false;
        }

        String storedToken = bucket.get();
        return token.equals(storedToken);
    }

    @Override
    public void logout(String username, String token) {
        String tokenKey = LOGIN_TOKEN_KEY_PREFIX + username;
        RBucket<String> bucket = redissonClient.getBucket(tokenKey);

        if (bucket.isExists()) {
            bucket.delete();
        }
    }
}
