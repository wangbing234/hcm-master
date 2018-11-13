package com.qidian.hcm.module;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.qidian.hcm.BaseTest;
import com.qidian.hcm.common.enums.YesNo;
import com.qidian.hcm.common.exception.BizException;
import com.qidian.hcm.common.interceptor.TenantThreadHelper;
import com.qidian.hcm.common.utils.CommonUtils;
import com.qidian.hcm.common.utils.ResultCode;
import com.qidian.hcm.module.authorization.dto.ConditionDTO;
import com.qidian.hcm.module.authorization.dto.PermissionDTO;
import com.qidian.hcm.module.authorization.dto.PermissionMetaDataDTO;
import com.qidian.hcm.module.authorization.dto.RolePermissionDTO;
import com.qidian.hcm.module.authorization.entity.Role;
import com.qidian.hcm.module.authorization.enums.ActionType;
import com.qidian.hcm.module.authorization.enums.MenuCode;
import com.qidian.hcm.module.authorization.enums.MenuType;
import com.qidian.hcm.module.authorization.enums.PlatformType;
import com.qidian.hcm.module.authorization.request.RoleEditRequest;
import com.qidian.hcm.module.center.entity.User;
import com.qidian.hcm.module.center.request.AddNotActiveUserRequest;
import com.qidian.hcm.module.center.request.LoginRequest;
import com.qidian.hcm.module.center.request.RegisterRequest;
import com.qidian.hcm.module.employee.dto.*;
import com.qidian.hcm.module.employee.entity.Employee;
import com.qidian.hcm.module.employee.enums.*;
import com.qidian.hcm.module.employee.request.CustomizedFormsFieldsRequest;
import com.qidian.hcm.module.employee.request.CustomizedFormsRequest;
import com.qidian.hcm.module.employee.request.EmployeeRequest;
import com.qidian.hcm.module.employee.request.QualifyEmployeeRequest;
import com.qidian.hcm.module.organization.enums.OrganizationEnums;
import com.qidian.hcm.module.organization.request.CreateCompanyRequest;
import com.qidian.hcm.module.organization.request.CreateDepartmentRequest;
import com.qidian.hcm.module.organization.request.CreateGradeRequest;
import com.qidian.hcm.module.organization.request.CreatePositionRequest;
import com.qidian.hcm.utils.MethodType;
import com.qidian.hcm.utils.TestUtils;
import com.qidian.hcm.utils.TokenType;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.util.Lists;
import org.testng.annotations.Test;
import java.util.*;

import static com.qidian.hcm.module.organization.enums.OrganizationEnums.company;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * 初始化测试数据
 */
@SuppressWarnings("PMD")
public class InitDataTest extends BaseTest {


    private String randomStr = TestUtils.getDate(TestUtils.DATE_FORMAT4);

    private String companyCode = RandomStringUtils.randomNumeric(8);

    private String departmentCode = RandomStringUtils.randomNumeric(8);

    private String positionCode = RandomStringUtils.randomNumeric(8);

    private String gradeCode = RandomStringUtils.randomNumeric(8);

    private JSONObject response;

    private Long gradeId;

    private Long roleId;

    /**
     * 创建管理员
     */
    @Test(description = "创建", priority = 0, groups = "init")
    public void testRegister() {
        RegisterRequest registerRequest = new RegisterRequest();

        registerRequest.setUsername(super.username);
        registerRequest.setPassword(super.password);
        registerRequest.setGroupName(UUID.randomUUID().toString().replace("-", ""));
        registerRequest.setPhone(super.phone);

        response = client.createRequest(MethodType.post, "/api/user/register")
                .setData(registerRequest).sendRequset().getResponse();
        assertThat("检查注册用户信息", userRepository.findByUsername(super.username).getPhone(), equalTo(super.phone));
    }

    @Test(description = "管理员登陆", priority = 1, groups = "init")
    public void testLoginByPassword() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(super.phone);
        loginRequest.setPassword(super.password);

        response = client.createRequest(MethodType.post, "/api/user/login/password")
                .setData(loginRequest).sendRequset().getResponse();

        assertThat("响应状态断言", response, hasEntry("code", "0"));
        TestUtils.saveToken(response.getString("data"), TokenType.MANAGER);
    }

    /**
     * 创建公司
     */
    @Test(description = "创建公司", groups = "init", priority = 2)
    public void createCompany() {
        CreateCompanyRequest createCompanyRequest = new CreateCompanyRequest();
        createCompanyRequest.setAddress("湖北省武汉市洪山区");
        createCompanyRequest.setAlias("中国湖北");
        createCompanyRequest.setCode(companyCode);
        JSONObject customField = new JSONObject();
        customField.put("自定义字段测试", "测试值");
        createCompanyRequest.setCustomField(customField);
        createCompanyRequest.setEnableTime(new Date());
        createCompanyRequest.setLegalPerson("法人测试");
        createCompanyRequest.setRegisterAddress("中国湖北省武汉市");
        createCompanyRequest.setParentId(1L);
        createCompanyRequest.setName("测试公司" + randomStr);
        createCompanyRequest.setMaster("");

        response = client.createRequest(MethodType.post, "/api/company")
                .setData(createCompanyRequest)
                .sendRequset().getResponse();

        this.companyId = organizationRepository.findByCodeAndType(companyCode, company)
                .get().getId();//取出公司ID

        assertThat("响应状态断言", response, hasEntry("code", "0"));
    }

    /**
     * 创建部门
     */
    @Test(priority = 3, groups = "init")
    public void createDepartment() {
        CreateDepartmentRequest createDepartmentRequest = new CreateDepartmentRequest();
        createDepartmentRequest.setAlias("中国湖北");
        createDepartmentRequest.setCode(departmentCode);
        createDepartmentRequest.setEnable(YesNo.YES);
        createDepartmentRequest.setEnableTime(new Date());
        JSONObject customField = new JSONObject();
        customField.put("部门自定义字段", "自定义值");
        createDepartmentRequest.setCustomField(customField);
        createDepartmentRequest.setFormation(10);
        createDepartmentRequest.setMaster("部门负责人");
        createDepartmentRequest.setName("测试部门" + randomStr);
        createDepartmentRequest.setParentId(1L);

        response = client.createRequest(MethodType.post, "/api/department/")
                .setData(createDepartmentRequest)
                .sendRequset().getResponse();

        this.departmentId = organizationRepository.findByCodeAndType(createDepartmentRequest.getCode(),
                OrganizationEnums.department).get().getId();
        assertThat("响应状态断言", response, hasEntry("code", "0"));

        createDepartmentRequest.setCode(RandomStringUtils.randomNumeric(8));
        createDepartmentRequest.setParentId(departmentId);
        response = client.createRequest(MethodType.post, "/api/department/")
                .setData(createDepartmentRequest)
                .sendRequset().getResponse();
        assertThat("响应状态断言", response, hasEntry("code", "0"));
    }

    /**
     * 创建岗位
     */
    @Test(priority = 5, groups = "init")
    public void createPosition() {
        JSONObject customField = new JSONObject();
        customField.put("创建岗位", "23");
        customField.put("测试", "49");

        CreatePositionRequest createPositionRequest = new CreatePositionRequest(
                "黄金开发组长" + randomStr, positionCode, "开发" + randomStr,
                departmentId, -1L, gradeId, new Date(), customField);
        response = client.createRequest(MethodType.post, "/api/position/")
                .setData(createPositionRequest)
                .sendRequset().getResponse();

        this.positionId = positionRepository.findByCode(positionCode).orElseThrow(() ->
                new BizException(ResultCode.POSITION_NOT_EXISTS)).getId();

        assertThat("响应状态断言", response, hasEntry("code", "0"));
    }

    /**
     * 创建职级
     */
    @Test(priority = 4, groups = "init")
    public void createGrade_C001() {
        CreateGradeRequest createGradeRequest = new CreateGradeRequest();
        createGradeRequest.setName("高级技术经理" + randomStr);
        createGradeRequest.setAlias("技术经理" + randomStr);
        createGradeRequest.setCode(gradeCode);
        createGradeRequest.setRank("T1");
        createGradeRequest.setEnableTime(new Date());
        JSONObject customField = new JSONObject();
        customField.put("职级自定义字段", "职级值");
        createGradeRequest.setCustomField(customField);

        this.response = client.createRequest(MethodType.post, "/api/grade/")
                .setData(createGradeRequest)
                .sendRequset().getResponse();
        this.gradeId = gradeRepository.findByCode(createGradeRequest.getCode()).get().getId();

        assertThat("响应状态断言", response, hasEntry("code", "0"));
    }

    /**
     * 创建职级
     */
    @Test(priority = 4, groups = "init")
    public void createGrade_C002() {
        CreateGradeRequest createGradeRequest = new CreateGradeRequest();
        createGradeRequest.setName("c中级技术经理" + randomStr);
        createGradeRequest.setAlias("技术经理" + randomStr);
        createGradeRequest.setCode(RandomStringUtils.randomNumeric(8));
        createGradeRequest.setRank("T2");
        createGradeRequest.setEnableTime(new Date());
        JSONObject customField = new JSONObject();
        customField.put("职级自定义字段", "职级值");
        createGradeRequest.setCustomField(customField);

        this.response = client.createRequest(MethodType.post, "/api/grade/")
                .setData(createGradeRequest)
                .sendRequset().getResponse();

        assertThat("响应状态断言", response, hasEntry("code", "0"));
    }

    /**
     * 创建正式员工1
     */
    @Test(priority = 6, groups = "init")
    public void createEmployee_C001() {
        EmployeeRequest request = new EmployeeRequest();
        request.setName(TestUtils.getChineseName());
        request.setEmployeeNo("No." + TestUtils.getDate(TestUtils.DATE_FORMAT3));
        request.setAvatar("10392971803496448");
        request.setAvatarName("batman.png");
        request.setMobile(TestUtils.getPhone());
        request.setGender(Gender.male);
        request.setBirthday(TestUtils.getDate(-7302));

        EmployeeBasicInfoDTO basicInfo = getEmployeeBasicInfo();
        EmployeePositionInfoDTO positionInfo = getEmployeePositionInfo();
        EmployeeOtherInfoDTO otherInfo = getEmployeeOtherInfo();
        request.setBasicInfo(basicInfo);
        request.setPositionInfo(positionInfo);
        request.setOtherInfo(otherInfo);

        this.response = client.createRequest(MethodType.post, "/api/employees")
                .setData(request).sendRequset().getResponse();

        super.employeeId_NoPrtmission = employeeRepository.findByEmployeeNo(request.getEmployeeNo()).get().getId();
    }

    /**
     * 创建正式员工2
     */
    @Test(priority = 6, groups = "init")
    public void createEmployee_C002() {
        EmployeeRequest request = new EmployeeRequest();
        request.setName(TestUtils.getChineseName());
        request.setEmployeeNo("No." + System.currentTimeMillis());
        request.setAvatar(System.currentTimeMillis() + "");
        request.setAvatarName("batman2.png");
        request.setMobile(TestUtils.getPhone());
        request.setGender(Gender.male);
        request.setBirthday(TestUtils.getDate(-7300));

        EmployeeBasicInfoDTO basicInfo = getEmployeeBasicInfo();
        EmployeePositionInfoDTO positionInfo = getEmployeePositionInfo();
        EmployeeOtherInfoDTO otherInfo = getEmployeeOtherInfo();
        request.setBasicInfo(basicInfo);
        request.setPositionInfo(positionInfo);
        request.setOtherInfo(otherInfo);

        this.response = client.createRequest(MethodType.post, "/api/employees")
                .setData(request).sendRequset().getResponse();

        super.employeeId_HavePrtmission = employeeRepository.findByEmployeeNo(request.getEmployeeNo()).get().getId();
    }

    public EmployeeBasicInfoDTO getEmployeeBasicInfo() {
        EmployeeBasicInfoDTO basicInfo = new EmployeeBasicInfoDTO();
        EmployeeJobDTO job = new EmployeeJobDTO();
        job.setHireDate(TestUtils.getDate(-1));//入职日期
        job.setStatus(EmployeeStatus.probation);
        job.setType(EmployeeType.labor);
        job.setWorkDate(TestUtils.getDate(0));//开始工作日期
        basicInfo.setJob(job);

        EmployeeContractDTO contract = new EmployeeContractDTO();
        contract.setStartDate(TestUtils.getDate(-1));//合同开始日期
        contract.setEndDate(TestUtils.getDate(365));//合同结束日期
        contract.setSignUnit("有招武汉");
        contract.setProbationEndDate(TestUtils.getDate(10));//使用结束日期
        contract.setPeriod(ContractPeriod.year1);
        basicInfo.setContract(Lists.newArrayList(contract));

        EmployeeIdentityDTO identity = new EmployeeIdentityDTO();
        identity.setType(IdentityType.passport);
        identity.setCode("HZ" + TestUtils.getNum(10000, 99999));
        basicInfo.setIdentity(Lists.newArrayList(identity));

        return basicInfo;
    }

    public EmployeePositionInfoDTO getEmployeePositionInfo() {
        EmployeePositionInfoDTO positionInfo = new EmployeePositionInfoDTO();
        EmployeePositionDTO position = new EmployeePositionDTO();
        position.setCompanyId(companyId);
        position.setDepartmentId(departmentId);
        position.setPositionId(positionId);
        position.setStartDate(new Date());
        position.setLeaderId(1L);
        positionInfo.setPosition(Lists.newArrayList(position));
        return positionInfo;
    }

    public EmployeeOtherInfoDTO getEmployeeOtherInfo() {
        EmployeeOtherInfoDTO otherInfo = new EmployeeOtherInfoDTO();
        EmployeeContactDTO contact = new EmployeeContactDTO();
        contact.setType(ContactType.email);
        contact.setContent(TestUtils.getEmail(10, 15));
        otherInfo.setContact(Lists.newArrayList(contact));

        EmployeeEmergencyContactDTO emergencyContact = new EmployeeEmergencyContactDTO();
        emergencyContact.setMobile(TestUtils.getPhone());
        emergencyContact.setRelationship("朋友");
        emergencyContact.setName(TestUtils.getChineseName());
        otherInfo.setEmergencyContact(Lists.newArrayList(emergencyContact));

        EmployeeEducationDTO education = new EmployeeEducationDTO();
        education.setStartTime(new Date());
        education.setEndTime(new Date());
        education.setSchool("武汉大学");
        education.setEducation(Education.undergraduate);
        education.setFullTime(true);
        education.setHighest(true);
        education.setDegree(Degree.bachelor);
        otherInfo.setEducation(Lists.newArrayList(education));

        EmployeeWorkExperienceDTO workExperience = new EmployeeWorkExperienceDTO();
        workExperience.setStartTime(new Date());
        workExperience.setEndTime(new Date());
        workExperience.setLeaveReason("钱太少");
        workExperience.setWorkUnit("某外包公司");
        workExperience.setJobPosition("码农");
        otherInfo.setWorkExperience(Lists.newArrayList(workExperience));
        return otherInfo;
    }

    /**
     * 员工转正
     */
    @Test(priority = 7, groups = "init")
    public void qualifyEmployee_C001() {
        QualifyEmployeeRequest request = new QualifyEmployeeRequest();
        request.setDate(new Date());
        request.setAttachment("1212121");
        request.setFileName("转正函");
        this.response = client.createRequest(MethodType.put, "/api/employees/"
                + super.employeeId_HavePrtmission + "/qualify")
                .setData(request).sendRequset().getResponse();

        assertThat("响应状态断言", response, hasEntry("code", "0"));
    }

    /**
     * 员工转正
     */
    @Test(priority = 7, groups = "init")
    public void qualifyEmployee_C002() {
        QualifyEmployeeRequest request = new QualifyEmployeeRequest();
        request.setDate(new Date());
        request.setAttachment("1212122");
        request.setFileName("转正函");
        this.response = client.createRequest(MethodType.put, "/api/employees/"
                + super.employeeId_NoPrtmission + "/qualify")
                .setData(request).sendRequset().getResponse();

        assertThat("响应状态断言", response, hasEntry("code", "0"));
    }

    /**
     * 员工自定义字段
     */
    @Test(priority = 8, groups = "init")
    public void createEmployeeCustomForm() {
        CustomizedFormsRequest request1 = new CustomizedFormsRequest();
        request1.setType(FormType.basic);
        request1.setMultiRecord(false);
        request1.setOnBoard(true);
        request1.setRequired(true);
        request1.setTitle("自动化测试1");
        List<CustomizedFormsFieldsRequest> fields = new ArrayList<>();
        request1.setFields(fields);

        this.response = client.createRequest(MethodType.post, "/api/employees/customized_forms")
                .setData(request1).sendRequset().getResponse();

        request1.setType(FormType.position);
        request1.setMultiRecord(true);
        request1.setRequired(false);
        request1.setTitle("自动化测试2");

        this.response = client.createRequest(MethodType.post, "/api/employees/customized_forms")
                .setData(request1).sendRequset().getResponse();

        request1.setType(FormType.other);
        request1.setTitle("自动化测试3");
        this.response = client.createRequest(MethodType.post, "/api/employees/customized_forms")
                .setData(request1).sendRequset().getResponse();
        assertThat("响应状态断言", response, hasEntry("code", "0"));
    }

    @Test(description = "管理员录入员工数据之后在center.user表中插入一条待激活记录", priority = 9, groups = "init")
    public void testAddNotActiveUser_C001() {
        Employee employee = employeeRepository.findById(super.employeeId_HavePrtmission).get();
        //List<GroupConfig> groups = groupConfigRepository.findAll();
        AddNotActiveUserRequest request = new AddNotActiveUserRequest();
        request.setUserName(employee.getName());
        request.setPhone(employee.getMobile());
        request.setTenantName(TenantThreadHelper.getToken().getTenantName());
        response = client.createRequest(MethodType.post, "/api/user/add_not_active_user")
                .setData(request).sendRequset().getResponse();

        assertThat("验证响应状态", response.getString("code"), is(ResultCode.SUCCESS.getCode()));

        User generalUser = userRepository.findByPhone(employee.getMobile()).get();
        generalUser.setPassword(CommonUtils.encoderByMd5(password));
        userRepository.save(generalUser);
    }

    @Test(description = "管理员录入员工数据之后在center.user表中插入一条待激活记录", priority = 9, groups = "init")
    public void testAddNotActiveUser_C002() {
        TestUtils.switchToTenant(client);
        Employee employee = employeeRepository.findById(super.employeeId_NoPrtmission).get();
        //List<GroupConfig> groups = groupConfigRepository.findAll();
        AddNotActiveUserRequest request = new AddNotActiveUserRequest();
        request.setUserName(employee.getName());
        request.setPhone(employee.getMobile());
        request.setTenantName(TenantThreadHelper.getToken().getTenantName());
        response = client.createRequest(MethodType.post, "/api/user/add_not_active_user")
                .setData(request).sendRequset().getResponse();

        assertThat("验证响应状态", response.getString("code"), is(ResultCode.SUCCESS.getCode()));

        User generalUser = userRepository.findByPhone(employee.getMobile()).get();
        generalUser.setPassword(CommonUtils.encoderByMd5(password));
        userRepository.save(generalUser);
    }


    @Test(description = "创建角色", priority = 10, groups = "init")
    public void testCreateRole() {
        RoleEditRequest request = new RoleEditRequest();
        request.setName(TestUtils.getChineseName());

        JSONObject response = client.createRequest(MethodType.post, "/api/authorization/roles")
                .setData(request).sendRequset().getResponse();


        assertThat("响应code检查", response.getString("code"), is(ResultCode.SUCCESS.getCode()));
        Role role = roleRepository.findByName(request.getName());
        assertThat("角色名称检查", request.getName(), is(role.getName()));
        roleId = role.getId();

    }

    @Test(description = "保存角色权限", priority = 11, groups = "init", dependsOnMethods = "testCreateRole")
    public void testSaveRolePermission() {
        RolePermissionDTO dto = this.createRolePermissionDTO();
        JSONObject response = client.createRequest(MethodType.post,
                "/api/authorization/roles/" + roleId + "/permissions").setData(dto)
                .sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is(ResultCode.SUCCESS.getCode()));
    }

    @Test(description = "更新员工角色", priority = 12, groups = "init", dependsOnMethods = "testSaveRolePermission")
    public void testUpdateEmployeeRole() {
        JSONObject roleData = client.createRequest(MethodType.get, "/api/authorization/roles")
                .sendRequset().getResponse();
        Long roleId = roleData.getJSONArray("data").getJSONObject(0).getLong("id");

        List<Long> roles = com.google.common.collect.Lists.newArrayList(roleId);
        JSONObject response = client.createRequest(MethodType.post,
                "/api/authorization/employees/" + employeeId_HavePrtmission + "/roles")
                .setData(roles)
                .sendRequset().getResponse();
        assertThat("响应code检查", response.getString("code"), is(ResultCode.SUCCESS.getCode()));
    }


    @Test(description = "有权限普通用户登陆", priority = 13, groups = "init")
    public void testLoginByPassword_C002() {
        Employee employee = employeeRepository.findById(super.employeeId_HavePrtmission).get();

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(employee.getMobile());
        loginRequest.setPassword(password);

        response = client.createRequest(MethodType.post, "/api/user/login/password")
                .setData(loginRequest).sendRequset().getResponse();

        assertThat("响应状态断言", response, hasEntry("code", "0"));
        TestUtils.saveToken(response.getString("data"), TokenType.HAVE_PERMISSION);
    }

    @Test(description = "无权限普通用户登陆", priority = 13, groups = "init")
    public void testLoginByPassword_C003() {
        TestUtils.switchToTenant(client);

        Employee employee = employeeRepository.findById(super.employeeId_NoPrtmission).get();

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(employee.getMobile());
        loginRequest.setPassword(password);

        response = client.createRequest(MethodType.post, "/api/user/login/password")
                .setData(loginRequest).sendRequset().getResponse();

        assertThat("响应状态断言", response, hasEntry("code", "0"));
        TestUtils.saveToken(response.getString("data"), TokenType.NO_PERMISSION);
    }

    private RolePermissionDTO createRolePermissionDTO() {
        String orgId = companyId.toString();

        Map<MenuType, List<MenuCode>> map = Maps.newHashMap();
        MenuType catalogue = MenuType.catalogue;
        List<MenuCode> menuCode1 = Lists.newArrayList(MenuCode.organizationManage, MenuCode.employeeManage);
        map.put(catalogue, menuCode1);

        MenuType menu = MenuType.menu;
        List<MenuCode> menuCode2 = Lists.newArrayList(MenuCode.organization, MenuCode.company, MenuCode.department,
                MenuCode.position, MenuCode.grade);
        map.put(menu, menuCode2);

        RolePermissionDTO dto = new RolePermissionDTO();
        List<PermissionMetaDataDTO> list = Lists.newArrayList();
        for (Map.Entry<MenuType, List<MenuCode>> entry : map.entrySet()) {
            for (MenuCode menuCode : entry.getValue()) {
                PermissionMetaDataDTO pmd = new PermissionMetaDataDTO();

                pmd.setPlatformType(PlatformType.backend);
                pmd.setType(entry.getKey());
                pmd.setCode(menuCode);

                //读权限
                PermissionDTO permissionGet = new PermissionDTO();
                permissionGet.setAction(ActionType.GET);
                ConditionDTO conditionGet = new ConditionDTO();
                conditionGet.setField("organizationId");
                conditionGet.setOp("include");
                conditionGet.setValue(orgId);
                permissionGet.setConditions(Lists.newArrayList(conditionGet));
                //写权限
                PermissionDTO permissionEdit = new PermissionDTO();
                permissionEdit.setAction(ActionType.EDIT);
                ConditionDTO conditionEdit = new ConditionDTO();
                conditionEdit.setField("organizationId");
                conditionEdit.setOp("include");
                conditionEdit.setValue(orgId);
                permissionEdit.setConditions(Lists.newArrayList(conditionEdit));

                List<PermissionDTO> permissions = Lists.newArrayList(permissionGet, permissionEdit);
                pmd.setPermissions(permissions);

                list.add(pmd);
            }
        }
        dto.setBackend(list);
        return dto;
    }

}