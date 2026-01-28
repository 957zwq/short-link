package com.winesasfood.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.winesasfood.admin.dto.req.UserRegisterReqDTO;
import com.winesasfood.admin.ShortLinkAdminApplication;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 用户分库分表API测试
 *
 * 测试目的：
 * 1. 验证分库分表后API接口是否正常工作
 * 2. 验证ShardingSphere是否正确路由SQL
 */
@SpringBootTest(classes = ShortLinkAdminApplication.class)
@AutoConfigureMockMvc
public class UserControllerShardingTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        System.out.println("========================================");
        System.out.println("开始测试：用户分库分表API");
        System.out.println("========================================");
    }

    @AfterEach
    void tearDown() {
        System.out.println("========================================");
        System.out.println("API测试结束");
        System.out.println("========================================");
    }

    @Test
    @DisplayName("API测试1：用户注册接口")
    void testRegisterApi() throws Exception {
        UserRegisterReqDTO request = new UserRegisterReqDTO();
        request.setUsername("api_test_user_001");
        request.setPassword("Test123456");
        request.setRealName("API测试用户");
        request.setPhone("13300000001");
        request.setMail("api_test@example.com");

        // 打印分片信息
        int shardIndex = Math.abs(request.getUsername().hashCode()) % 16;
        System.out.println("注册用户: " + request.getUsername() + " -> 应路由到分片: t_user_" + shardIndex);

        MvcResult result = mockMvc.perform(post("/api/shortlink/v1/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andReturn();

        System.out.println("✅ 用户注册API测试通过");
    }

    @Test
    @DisplayName("API测试2：根据用户名查询用户接口")
    void testGetUserByUsernameApi() throws Exception {
        // 先注册用户
        String username = "api_query_test_001";
        UserRegisterReqDTO registerRequest = new UserRegisterReqDTO();
        registerRequest.setUsername(username);
        registerRequest.setPassword("Test123456");
        registerRequest.setRealName("API查询测试用户");
        registerRequest.setPhone("13200000001");
        registerRequest.setMail("api_query@example.com");

        mockMvc.perform(post("/api/shortlink/v1/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        // 查询用户
        mockMvc.perform(get("/api/shortlink/v1/user/{username}", username))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.username").value(username))
                .andExpect(jsonPath("$.data.realName").value("API查询测试用户"));

        System.out.println("✅ 查询用户API测试通过");
    }

    @Test
    @DisplayName("API测试3：检查用户名是否存在接口")
    void testCheckUsernameExistsApi() throws Exception {
        String username = "api_exists_test_001";

        // 检查不存在的用户
        mockMvc.perform(get("/api/shortlink/v1/user/username/{username}/exists", username))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").value(false));

        // 注册用户
        UserRegisterReqDTO registerRequest = new UserRegisterReqDTO();
        registerRequest.setUsername(username);
        registerRequest.setPassword("Test123456");
        registerRequest.setRealName("API存在性测试用户");
        registerRequest.setPhone("13100000001");
        registerRequest.setMail("api_exists@example.com");

        mockMvc.perform(post("/api/shortlink/v1/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        // 再次检查（应该存在）
        mockMvc.perform(get("/api/shortlink/v1/user/username/{username}/exists", username))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").value(true));

        System.out.println("✅ 用户名存在性检查API测试通过");
    }

    @Test
    @DisplayName("API测试4：参数校验测试")
    void testValidationApi() throws Exception {
        // 测试用户名过短
        UserRegisterReqDTO request1 = new UserRegisterReqDTO();
        request1.setUsername("abc");
        request1.setPassword("Test123456");
        request1.setRealName("测试用户");
        request1.setPhone("13000000001");

        mockMvc.perform(post("/api/shortlink/v1/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // 测试手机号格式错误
        UserRegisterReqDTO request2 = new UserRegisterReqDTO();
        request2.setUsername("valid_user");
        request2.setPassword("Test123456");
        request2.setRealName("测试用户");
        request2.setPhone("invalid_phone");

        mockMvc.perform(post("/api/shortlink/v1/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        System.out.println("✅ 参数校验API测试通过");
    }

    @Test
    @DisplayName("API测试5：查询不存在的用户")
    void testGetNonExistentUserApi() throws Exception {
        mockMvc.perform(get("/api/shortlink/v1/user/{username}", "non_existent_user_xyz"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(not(0)));  // 业务异常，code不为0

        System.out.println("✅ 查询不存在用户API测试通过");
    }

    @Test
    @DisplayName("API测试6：批量注册和查询测试")
    void testBatchRegisterAndQueryApi() throws Exception {
        String[] usernames = {"batch_001", "batch_002", "batch_003", "batch_004", "batch_005"};

        // 批量注册
        for (int i = 0; i < usernames.length; i++) {
            String username = usernames[i];
            int shardIndex = Math.abs(username.hashCode()) % 16;

            System.out.println(String.format("注册用户[%s] -> 分片: t_user_%d", username, shardIndex));

            UserRegisterReqDTO request = new UserRegisterReqDTO();
            request.setUsername(username);
            request.setPassword("Test123456");
            request.setRealName("批量测试用户" + i);
            request.setPhone("1300000" + String.format("%02d", i));
            request.setMail(username + "@example.com");

            mockMvc.perform(post("/api/shortlink/v1/user/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(0));
        }

        // 批量查询验证
        for (String username : usernames) {
            mockMvc.perform(get("/api/shortlink/v1/user/{username}", username))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(0))
                    .andExpect(jsonPath("$.data.username").value(username));
        }

        System.out.println("✅ 批量注册和查询API测试通过");
    }
}
