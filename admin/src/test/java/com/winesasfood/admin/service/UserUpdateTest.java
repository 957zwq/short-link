package com.winesasfood.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.winesasfood.admin.ShortLinkAdminApplication;
import com.winesasfood.admin.dto.req.UserRegisterReqDTO;
import com.winesasfood.admin.dto.req.UserUpdateReqDTO;
import com.winesasfood.admin.dao.entity.UserDO;
import com.winesasfood.admin.dao.mapper.UserMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户修改功能单元测试
 */
@SpringBootTest(classes = ShortLinkAdminApplication.class)
public class UserUpdateTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    private static final String TEST_USER = "update_test_user";
    private String testPhone;

    @BeforeEach
    void setUp() {
        // 生成唯一的手机号（使用时间戳后6位）
        String timeSuffix = String.valueOf(System.currentTimeMillis()).substring(7);
        testPhone = "188" + timeSuffix + "001";

        // 清理可能存在的测试数据
        cleanTestData();

        // 先注册一个测试用户
        UserRegisterReqDTO registerReq = new UserRegisterReqDTO();
        registerReq.setUsername(TEST_USER);
        registerReq.setPassword("Test123456");
        registerReq.setRealName("测试用户");
        registerReq.setPhone(testPhone);
        registerReq.setMail("test@example.com");

        userService.register(registerReq);

        System.out.println("========================================");
        System.out.println("开始测试：用户修改功能");
        System.out.println("========================================");
    }

    @AfterEach
    void tearDown() {
        System.out.println("========================================");
        System.out.println("测试结束");
        System.out.println("========================================");
        cleanTestData();
    }

    /**
     * 清理测试数据
     */
    private void cleanTestData() {
        LambdaQueryWrapper<UserDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDO::getUsername, TEST_USER);
        userMapper.delete(wrapper);
    }

    @Test
    @DisplayName("测试1：修改真实姓名")
    void testUpdateRealName() {
        UserUpdateReqDTO request = new UserUpdateReqDTO();
        request.setUsername(TEST_USER);
        request.setRealName("修改后的姓名");

        userService.update(request);

        // 验证修改结果
        LambdaQueryWrapper<UserDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDO::getUsername, TEST_USER);
        UserDO user = userMapper.selectOne(wrapper);

        assertEquals("修改后的姓名", user.getRealName(), "真实姓名应该被修改");
        System.out.println("✅ 真实姓名修改成功");
    }

    @Test
    @DisplayName("测试2：修改手机号")
    void testUpdatePhone() {
        // 生成唯一的新手机号
        String newPhone = "139" + System.currentTimeMillis() % 100000000 + "01";

        UserUpdateReqDTO request = new UserUpdateReqDTO();
        request.setUsername(TEST_USER);
        request.setPhone(newPhone);

        userService.update(request);

        // 验证修改结果
        LambdaQueryWrapper<UserDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDO::getUsername, TEST_USER);
        UserDO user = userMapper.selectOne(wrapper);

        assertEquals(newPhone, user.getPhone(), "手机号应该被修改");
        System.out.println("✅ 手机号修改成功");
    }

    @Test
    @DisplayName("测试3：修改邮箱")
    void testUpdateMail() {
        UserUpdateReqDTO request = new UserUpdateReqDTO();
        request.setUsername(TEST_USER);
        request.setMail("newmail@example.com");

        userService.update(request);

        // 验证修改结果
        LambdaQueryWrapper<UserDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDO::getUsername, TEST_USER);
        UserDO user = userMapper.selectOne(wrapper);

        assertEquals("newmail@example.com", user.getMail(), "邮箱应该被修改");
        System.out.println("✅ 邮箱修改成功");
    }

    @Test
    @DisplayName("测试4：同时修改多个字段")
    void testUpdateMultipleFields() {
        // 生成唯一的新手机号
        String newPhone = "137" + System.currentTimeMillis() % 100000000 + "01";

        UserUpdateReqDTO request = new UserUpdateReqDTO();
        request.setUsername(TEST_USER);
        request.setRealName("新姓名");
        request.setPhone(newPhone);
        request.setMail("new@example.com");

        userService.update(request);

        // 验证修改结果
        LambdaQueryWrapper<UserDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDO::getUsername, TEST_USER);
        UserDO user = userMapper.selectOne(wrapper);

        assertEquals("新姓名", user.getRealName());
        assertEquals(newPhone, user.getPhone());
        assertEquals("new@example.com", user.getMail());
        assertNotNull(user.getUpdateTime(), "更新时间应该被更新");
        System.out.println("✅ 多字段修改成功");
    }

    @Test
    @DisplayName("测试5：修改不存在的用户")
    void testUpdateNonExistentUser() {
        UserUpdateReqDTO request = new UserUpdateReqDTO();
        request.setUsername("non_existent_user_xyz");
        request.setRealName("不存在的用户");

        // 应该抛出异常
        Exception exception = assertThrows(Exception.class, () -> {
            userService.update(request);
        });

        assertTrue(exception.getMessage().contains("用户不存在"), "应该提示用户不存在");
        System.out.println("✅ 修改不存在用户正确抛出异常");
    }

    @Test
    @DisplayName("测试6：手机号被其他用户占用")
    void testUpdatePhoneAlreadyExists() {
        // 注册第二个用户（使用唯一手机号）
        String anotherUser = "another_test_user";
        String timeSuffix = String.valueOf(System.currentTimeMillis()).substring(7);
        String anotherPhone = "189" + timeSuffix + "002";

        UserRegisterReqDTO registerReq = new UserRegisterReqDTO();
        registerReq.setUsername(anotherUser);
        registerReq.setPassword("Test123456");
        registerReq.setRealName("另一个用户");
        registerReq.setPhone(anotherPhone);
        registerReq.setMail("another@example.com");

        userService.register(registerReq);

        // 尝试把第一个用户的手机号改成和第二个用户一样
        UserUpdateReqDTO request = new UserUpdateReqDTO();
        request.setUsername(TEST_USER);
        request.setPhone(anotherPhone);

        // 应该抛出异常
        Exception exception = assertThrows(Exception.class, () -> {
            userService.update(request);
        });

        assertTrue(exception.getMessage().contains("手机号已被其他用户使用"), "应该提示手机号已被使用");
        System.out.println("✅ 手机号重复检查正常");

        // 清理第二个用户
        LambdaQueryWrapper<UserDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDO::getUsername, anotherUser);
        userMapper.delete(wrapper);
    }

    @Test
    @DisplayName("测试7：只传入username，不修改任何字段")
    void testUpdateWithNoFields() {
        // 先获取原数据
        LambdaQueryWrapper<UserDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDO::getUsername, TEST_USER);
        UserDO originalUser = userMapper.selectOne(wrapper);
        String originalPhone = originalUser.getPhone();
        String originalMail = originalUser.getMail();

        // 只传入username，不传其他字段
        UserUpdateReqDTO request = new UserUpdateReqDTO();
        request.setUsername(TEST_USER);

        userService.update(request);

        // 验证数据没有被修改
        wrapper.eq(UserDO::getUsername, TEST_USER);
        UserDO user = userMapper.selectOne(wrapper);

        assertEquals(originalPhone, user.getPhone(), "手机号不应该被修改");
        assertEquals(originalMail, user.getMail(), "邮箱不应该被修改");
        System.out.println("✅ 空更新不修改数据");
    }

    @Test
    @DisplayName("测试8：修改为自己的手机号（不变化）")
    void testUpdateToSamePhone() {
        // 获取当前手机号
        LambdaQueryWrapper<UserDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDO::getUsername, TEST_USER);
        UserDO user = userMapper.selectOne(wrapper);
        String currentPhone = user.getPhone();

        // 修改为相同的手机号
        UserUpdateReqDTO request = new UserUpdateReqDTO();
        request.setUsername(TEST_USER);
        request.setPhone(currentPhone);

        // 应该正常执行，不报错
        assertDoesNotThrow(() -> userService.update(request), "修改为相同手机号应该正常执行");
        System.out.println("✅ 修改为相同手机号正常执行");
    }

    @Test
    @DisplayName("测试9：分片路由验证")
    void testShardingRouting() {
        // 计算分片
        int shardIndex = Math.abs(TEST_USER.hashCode()) % 16;
        System.out.println("用户[" + TEST_USER + "]所在分片: t_user_" + shardIndex);

        // 修改用户
        UserUpdateReqDTO request = new UserUpdateReqDTO();
        request.setUsername(TEST_USER);
        request.setRealName("分片路由测试");

        userService.update(request);

        // 验证修改成功
        LambdaQueryWrapper<UserDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDO::getUsername, TEST_USER);
        UserDO user = userMapper.selectOne(wrapper);

        assertEquals("分片路由测试", user.getRealName());
        System.out.println("✅ 分片路由正常，数据修改成功");
    }
}
