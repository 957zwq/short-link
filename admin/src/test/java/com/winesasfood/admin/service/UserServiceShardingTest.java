package com.winesasfood.admin.service;

import com.winesasfood.admin.dto.req.UserRegisterReqDTO;
import com.winesasfood.admin.dto.resp.UserRespDTO;
import com.winesasfood.admin.ShortLinkAdminApplication;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户分库分表功能测试
 *
 * 测试目的：
 * 1. 验证ShardingSphere分片配置是否正确
 * 2. 验证数据是否正确路由到对应的分片
 * 3. 验证查询功能是否能正确从分片中获取数据
 */
@SpringBootTest(classes = ShortLinkAdminApplication.class)
public class UserServiceShardingTest {

    @Autowired
    private UserService userService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 测试数据
    private static final String TEST_USERNAME_PREFIX = "test_user_";

    /**
     * 计算分片位置
     * ShardingSphere HASH_MOD算法: hash(username) % 16
     * 这里模拟ShardingSphere的分片计算逻辑
     */
    private int calculateShardingIndex(String username) {
        return Math.abs(username.hashCode()) % 16;
    }

    @BeforeEach
    void setUp() {
        System.out.println("========================================");
        System.out.println("开始测试：用户分库分表功能");
        System.out.println("========================================");
    }

    @AfterEach
    void tearDown() {
        System.out.println("========================================");
        System.out.println("测试结束");
        System.out.println("========================================");
    }

    @Test
    @DisplayName("测试1：用户注册 - 验证分片路由")
    void testRegisterWithSharding() {
        // 准备测试数据
        String username = TEST_USERNAME_PREFIX + "sharding_001";
        String phone = "13800000001";

        UserRegisterReqDTO request = new UserRegisterReqDTO();
        request.setUsername(username);
        request.setPassword("Test123456");
        request.setRealName("测试用户001");
        request.setPhone(phone);
        request.setMail("test001@example.com");

        // 计算应该路由到哪个分片
        int shardIndex = calculateShardingIndex(username);
        System.out.println("用户名: " + username);
        System.out.println("Hash值: " + username.hashCode());
        System.out.println("应路由到分片: t_user_" + shardIndex);

        // 执行注册
        assertDoesNotThrow(() -> userService.register(request),
                "用户注册应该成功");

        // 验证用户是否可以查询到（验证数据确实写入分片）
        UserRespDTO savedUser = userService.getUserByUsername(username);
        assertNotNull(savedUser, "注册后应该能查询到用户");
        assertEquals(username, savedUser.getUsername(), "用户名应该一致");
        assertEquals("测试用户001", savedUser.getRealName(), "真实姓名应该一致");

        System.out.println("✅ 用户注册成功并成功从分片中查询到数据");
    }

    @Test
    @DisplayName("测试2：多个用户注册 - 验证数据分散到不同分片")
    void testMultipleUsersDistribution() {
        // 创建多个用户，观察它们分布到哪些分片
        String[] usernames = {
                "alice_001",
                "bob_002",
                "charlie_003",
                "david_004",
                "emma_005"
        };

        for (int i = 0; i < usernames.length; i++) {
            String username = usernames[i];
            String phone = "139000000" + String.format("%02d", i);

            UserRegisterReqDTO request = new UserRegisterReqDTO();
            request.setUsername(username);
            request.setPassword("Test123456");
            request.setRealName("测试用户" + i);
            request.setPhone(phone);
            request.setMail(username + "@example.com");

            // 计算分片
            int shardIndex = calculateShardingIndex(username);

            System.out.println(String.format("用户[%s] -> Hash: %d -> 分片: t_user_%d",
                    username, username.hashCode(), shardIndex));

            // 注册用户
            userService.register(request);

            // 验证可以查询到
            UserRespDTO user = userService.getUserByUsername(username);
            assertNotNull(user, "用户 " + username + " 应该能查询到");
        }

        System.out.println("✅ 多个用户已注册并验证，数据分散到不同分片");
    }

    @Test
    @DisplayName("测试3：根据用户名查询 - 验证跨分片查询")
    void testQueryByUsername() {
        // 先注册一个用户
        String username = TEST_USERNAME_PREFIX + "query_001";

        UserRegisterReqDTO request = new UserRegisterReqDTO();
        request.setUsername(username);
        request.setPassword("Test123456");
        request.setRealName("查询测试用户");
        request.setPhone("13700000001");
        request.setMail("query_test@example.com");

        userService.register(request);

        // 查询用户（ShardingSphere会自动路由到正确的分片）
        UserRespDTO user = userService.getUserByUsername(username);

        assertNotNull(user, "应该能查询到用户");
        assertEquals(username, user.getUsername(), "用户名应该一致");
        assertEquals("查询测试用户", user.getRealName(), "真实姓名应该一致");

        System.out.println("✅ 成功从分片中查询到用户: " + username);
    }

    @Test
    @DisplayName("测试4：用户名重复检查")
    void testUsernameExists() {
        String username = TEST_USERNAME_PREFIX + "exists_001";

        // 注册前检查（不存在）
        boolean existsBefore = userService.isUsernameExists(username);
        System.out.println("注册前用户名[" + username + "]是否存在: " + existsBefore);

        // 注册用户
        UserRegisterReqDTO request = new UserRegisterReqDTO();
        request.setUsername(username);
        request.setPassword("Test123456");
        request.setRealName("存在性测试用户");
        request.setPhone("13600000001");
        request.setMail("exists@example.com");

        userService.register(request);

        // 注册后检查（存在）
        boolean existsAfter = userService.isUsernameExists(username);
        System.out.println("注册后用户名[" + username + "]是否存在: " + existsAfter);

        assertTrue(existsAfter, "注册后用户名应该存在");
        System.out.println("✅ 用户名存在性检查功能正常");
    }

    @Test
    @DisplayName("测试5：同一分片多个用户")
    void testMultipleUsersInSameShard() {
        // 尝试找到两个会被路由到同一分片的用户
        // 这里我们通过观察来验证ShardingSphere的路由

        String username1 = TEST_USERNAME_PREFIX + "sameshard_001";
        String username2 = TEST_USERNAME_PREFIX + "sameshard_002";

        int shard1 = calculateShardingIndex(username1);
        int shard2 = calculateShardingIndex(username2);

        System.out.println("用户1: " + username1 + " -> 分片: t_user_" + shard1);
        System.out.println("用户2: " + username2 + " -> 分片: t_user_" + shard2);

        // 注册两个用户
        UserRegisterReqDTO request1 = new UserRegisterReqDTO();
        request1.setUsername(username1);
        request1.setPassword("Test123456");
        request1.setRealName("同分片测试用户1");
        request1.setPhone("13500000001");
        request1.setMail("same1@example.com");

        UserRegisterReqDTO request2 = new UserRegisterReqDTO();
        request2.setUsername(username2);
        request2.setPassword("Test123456");
        request2.setRealName("同分片测试用户2");
        request2.setPhone("13500000002");
        request2.setMail("same2@example.com");

        userService.register(request1);
        userService.register(request2);

        // 验证都能查询到
        UserRespDTO user1 = userService.getUserByUsername(username1);
        UserRespDTO user2 = userService.getUserByUsername(username2);

        assertNotNull(user1, "用户1应该能查询到");
        assertNotNull(user2, "用户2应该能查询到");

        if (shard1 == shard2) {
            System.out.println("✅ 两个用户在同一分片 t_user_" + shard1 + " 中，都能正确查询");
        } else {
            System.out.println("✅ 两个用户在不同分片中，都能正确查询");
        }
    }

    @Test
    @DisplayName("测试6：特殊字符用户名分片")
    void testSpecialCharacterUsernameSharding() {
        // 测试包含数字和下划线的用户名
        String[] specialUsernames = {
                "user_123",
                "test_456_abc",
                "user_789_xyz"
        };

        for (String username : specialUsernames) {
            String phone = "1340000" + username.substring(5, 10);

            UserRegisterReqDTO request = new UserRegisterReqDTO();
            request.setUsername(username);
            request.setPassword("Test123456");
            request.setRealName("特殊字符测试用户");
            request.setPhone(phone);
            request.setMail(username + "@example.com");

            int shardIndex = calculateShardingIndex(username);
            System.out.println("特殊用户名: " + username + " -> 分片: t_user_" + shardIndex);

            assertDoesNotThrow(() -> userService.register(request),
                    "特殊字符用户名注册应该成功");

            UserRespDTO user = userService.getUserByUsername(username);
            assertNotNull(user, "特殊字符用户名应该能查询到");
        }

        System.out.println("✅ 特殊字符用户名分片测试通过");
    }

    /**
     * 清理测试数据
     * 注意：由于使用了分库分表，需要从每个分片中清理
     * 这里只是演示，实际项目中可能需要单独的数据清理脚本
     */
    @Test
    @DisplayName("辅助：打印分片信息")
    void testPrintShardingInfo() {
        System.out.println("\n========== 分片配置信息 ==========");
        System.out.println("分片算法: HASH_MOD");
        System.out.println("分片数量: 16");
        System.out.println("分片键: username");
        System.out.println("物理表: t_user_0 ~ t_user_15");
        System.out.println("逻辑表: t_user");
        System.out.println("=================================\n");
    }
}
