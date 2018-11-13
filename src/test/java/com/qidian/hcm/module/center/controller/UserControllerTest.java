package com.qidian.hcm.module.center.controller;

import com.alibaba.fastjson.JSONObject;
import com.qidian.hcm.BaseTest;
import com.qidian.hcm.common.constants.RedisPrefixConstant;
import com.qidian.hcm.common.redis.RedisService;
import com.qidian.hcm.common.utils.ResultCode;
import com.qidian.hcm.module.center.entity.User;
import com.qidian.hcm.module.center.request.*;
import com.qidian.hcm.module.center.service.VerificationService;
import com.qidian.hcm.utils.ClientUtils;
import com.qidian.hcm.utils.MethodType;
import com.qidian.hcm.utils.TestUtils;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SuppressWarnings("PMD")
public class UserControllerTest extends BaseTest {

    @Autowired
    @Spy
    private VerificationService verificationService;

    @Autowired
    @InjectMocks
    private UserController userController;

    @Autowired
    private RedisService redisService;

    private JSONObject response;

    private String verificationCode;

    @BeforeMethod
    public void setupMockMvc() {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.webAppContextSetup(this.context)
                .addFilter(jwtAuthenticationTokenFilter).build();//集成web环境测试
        client = new ClientUtils(mvc);
    }

/*    @Test(description = "用户注册", groups = "init")
    public void testRegister_C001() {
        RegisterRequest registerRequest = new RegisterRequest();

        registerRequest.setUsername(super.username);
        registerRequest.setPassword(super.password);
        registerRequest.setGroupName(UUID.randomUUID().toString().replace("-", ""));
        registerRequest.setPhone(super.phone);

        response = client.createRequest(MethodType.post, "/api/user/register")
                .setData(registerRequest).sendRequset().getResponse();
        assertThat("检查注册用户信息", userRepository.findByUsername(super.username).getPhone(), equalTo(super.phone));
    }*/

    @Test(description = "重复注册", groups = "user")
    public void testRegister_C002() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername(super.username);
        registerRequest.setPassword("tester");
        registerRequest.setGroupName("有招");
        registerRequest.setPhone(super.phone);

        response = client.createRequest(MethodType.post, "/api/user/register")
                .setData(registerRequest).sendRequset().getResponse();

        assertThat("检查注册用户信息", response.getString("code"), equalTo("11008"));
    }

    @DataProvider(name = "loginData")
    public Object[][] dataProvider2() {
        return new Object[][]{{"17612169055", "tester", "用户名不存在！"},
                {super.phone, "test123", "密码错误！"},
                {super.phone, "tester '", "密码错误！"},
                {"17612169056 '", "tester", "用户名不存在！"}};
    }

    @Test(description = "异常登陆测试", dataProvider = "loginData")
    public void testLoginByPassword_C001(String username, String password, String msg) {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);

        response = client.createRequest(MethodType.post, "/api/user/login/password")
                .setData(loginRequest).sendRequset().getResponse();
        assertThat("异常登录测试", response.getString("message"), is(msg));
    }

    @Test(description = "向未注册用户发送短信")
    public void testGetVerificationCode_C001() {
        response = client.createRequest(MethodType.post, "/api/user/phoneCode/13100000000")
                .sendRequset().getResponse();

        assertThat("异常登录测试", response.getString("message"), is("用户名不存在！"));
    }

    @Test(description = "向已注册用户发送短信验证码")
    public void testGetVerificationCode_C002() throws Exception {
        mvc = MockMvcBuilders.standaloneSetup(userController).addFilter(jwtAuthenticationTokenFilter).build();//独立测试
        client = new ClientUtils(mvc);
        Mockito.doNothing().when(verificationService)
                .sendMessage(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());

        response = client.createRequest(MethodType.post, "/api/user/phoneCode/" + super.phone)
                .sendRequset().getResponse();

        assertThat("检查数据库中已存在短信", redisService.get(RedisPrefixConstant.VERIFICATION_CODE + super.phone),
                notNullValue());
    }

    @Test(dependsOnMethods = {"testGetVerificationCode_C002"}, description = "使用正确的短信验证码登录", enabled = false)
    public void testLoginByVerification_C001() {
        commonLoginByVerification();
        assertThat("短信登录", response.getString("code"), is(SUCCESS));
    }

    @Test(dependsOnMethods = {"testLoginByVerification_C001"}, description = "使用验证码重复登陆", enabled = false)
    public void testLoginByVerification_C003() {
        commonLoginByVerification();
        assertThat("重复短信登录", response.getString("code"),
                is(ResultCode.VERIFICATION_CODE_OVER_DUE.getCode()));
    }

    private void commonLoginByVerification() {
        SMSLoginRequest smsLoginRequest = new SMSLoginRequest();
        smsLoginRequest.setPhone(super.phone);
        String key = RedisPrefixConstant.VERIFICATION_CODE + super.phone;
        logger.warn("###key=" + key);
        verificationCode = redisService.get(key).toString();
        logger.warn("###code=" + verificationCode);
        smsLoginRequest.setCode(verificationCode);
        response = client.createRequest(MethodType.post, "/api/user/login/verification")
                .setData(smsLoginRequest).sendRequset().getResponse();
    }



    @Test(description = "使用错误的短信验证码登录")
    public void testLoginByVerification_C002() {
        SMSLoginRequest smsLoginRequest = new SMSLoginRequest();
        smsLoginRequest.setPhone(super.phone);
        smsLoginRequest.setCode("12345");

        response = client.createRequest(MethodType.post, "/api/user/login/verification")
                .setData(smsLoginRequest).sendRequset().getResponse();

        assertThat("短信登录", response.getString("code"), is(ResultCode.WRONG_VERIFICATION_CODE.getCode()));
    }

    @Test(description = "首次登录修改基本信息")
    public void testUpdateBaseInfo() {
        BaseInfoRequest request = new BaseInfoRequest();
        request.setEmail(TestUtils.getEmail(10, 11));
        request.setPassword("123456");
        List<User> users = userRepository.findAll();

        response = client.createRequest(MethodType.put,
                "/api/user/base_info/" + users.get(TestUtils.getNum(0, users.size() - 1)).getId())
                .setData(request).sendRequset().getResponse();

        assertThat("验证响应状态", response.getString("code"), is(ResultCode.SUCCESS.getCode()));
    }

    @Test(description = "更新Employee中的手机号码之后同步更新center.user中的phone")
    public void testUpdateUserPhone() {
        List<User> users = userRepository.findAll();
        Long id = users.get(TestUtils.getNum(0, users.size() - 1)).getId();
        String phone = TestUtils.getPhone();

        UpdateUserPhoneRequest request = new UpdateUserPhoneRequest();
        request.setPhone(phone);
        request.setUserId(id);
        response = client.createRequest(MethodType.put, "/api/user/phone")
                .setData(request).sendRequset().getResponse();
        assertThat("验证响应状态", response.getString("code"), is(ResultCode.SUCCESS.getCode()));
    }
}