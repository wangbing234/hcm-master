package com.qidian.hcm;

import com.qidian.hcm.common.security.JwtAuthenticationTokenFilter;
import com.qidian.hcm.common.utils.ResultCode;
import com.qidian.hcm.module.authorization.repository.RoleRepository;
import com.qidian.hcm.module.center.repository.UserRepository;
import com.qidian.hcm.module.employee.repository.EmployeeRepository;
import com.qidian.hcm.module.organization.repository.GradeRepository;
import com.qidian.hcm.module.organization.repository.OrganizationRepository;
import com.qidian.hcm.module.organization.repository.PositionRepository;
import com.qidian.hcm.utils.ClientUtils;
import com.qidian.hcm.utils.MethodType;
import com.qidian.hcm.utils.TestUtils;
import com.qidian.hcm.utils.TokenType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.*;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
//@Rollback
@SuppressWarnings("PMD")
//public class BaseTest extends AbstractTransactionalTestNGSpringContextTests {
public class BaseTest extends AbstractTestNGSpringContextTests {

    //管理员账号信息
    protected static final String username = TestUtils.getChineseName();

    protected static final String password = "123456";

    protected static final String phone = TestUtils.getPhone();

    //普通用户账号信息
    //protected static final String generalName = TestUtils.getChineseName();

    //protected static String generalPhone = TestUtils.getPhone();

    protected static Long employeeId_HavePrtmission; //普通用户有权限

    protected static Long employeeId_NoPrtmission; //普通用户无权限

    protected static Long companyId;

    protected static Long departmentId;

    protected static Long positionId;

    protected static final String SUCCESS = "0";

    protected static final String RESULT_CODE = "code";


    protected static final Map<String, Object> transValueMap = new HashMap();

    @Autowired
    public WebApplicationContext context;

    @Autowired
    public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    @Autowired
    public UserRepository userRepository;

    @Autowired
    public EmployeeRepository employeeRepository;

    @Autowired
    public OrganizationRepository organizationRepository;

    @Autowired
    public PositionRepository positionRepository;

    @Autowired
    public GradeRepository gradeRepository;

    @Autowired
    public RoleRepository roleRepository;

    public ClientUtils client;

    public MockMvc mvc;

    @DataProvider(name = "defaultPermissionData")
    public Object[][] defaultPermissionDataProvider() {
        return new Object[][]{
                {TokenType.MANAGER, SUCCESS},
                {TokenType.HAVE_PERMISSION, SUCCESS},
                {TokenType.NO_PERMISSION, ResultCode.DO_NOT_HAVE_ACCESS.getCode()}
        };
    }

    @BeforeSuite
    public void beforeSuite() {
        System.out.println("before suite");
    }

    @BeforeMethod
    public void beforeMethod() {
        System.out.println("before method");
        mvc = MockMvcBuilders.webAppContextSetup(this.context)
                .addFilter(jwtAuthenticationTokenFilter).build();//集成web环境测试
        client = new ClientUtils(mvc);
    }

    @BeforeTest
    public void beforeTest() {
        System.out.println("before test");
    }

    @Test(enabled = false)
    public void test() {
        System.out.println("1111");
    }

    @AfterSuite(alwaysRun = true)
    public void afterTest() {
        // Will delete all data created during testing
        // Comment this if you want to do some debug
        client.createRequest(MethodType.delete, "/api/user/destroy").sendRequset().getResponse();
        TestUtils.deleteToken();
    }
}