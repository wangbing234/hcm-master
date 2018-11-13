package com.qidian.hcm.module.employee.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qidian.hcm.BaseTest;
import com.qidian.hcm.common.enums.YesNo;
import com.qidian.hcm.common.utils.ResultCode;
import com.qidian.hcm.module.employee.dto.*;
import com.qidian.hcm.module.employee.entity.Employee;
import com.qidian.hcm.module.employee.entity.EmployeePosition;
import com.qidian.hcm.module.employee.enums.*;
import com.qidian.hcm.module.employee.repository.*;
import com.qidian.hcm.module.employee.request.*;
import com.qidian.hcm.module.organization.entity.PositionEntity;
import com.qidian.hcm.module.organization.enums.OrganizationEnums;
import com.qidian.hcm.module.organization.repository.OrganizationRepository;
import com.qidian.hcm.module.organization.repository.PositionRepository;
import com.qidian.hcm.module.organization.response.OrganizationTreeResponse;
import com.qidian.hcm.utils.ClientUtils;
import com.qidian.hcm.utils.MethodType;
import com.qidian.hcm.utils.TestUtils;
import com.qidian.hcm.utils.TokenType;
import org.assertj.core.util.Lists;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;

@SuppressWarnings("PMD")
public class EmployeeControllerTest extends BaseTest {

    private JSONObject response;

    private Long employeeId;

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private PositionRepository positionRepository;
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private EmployeePositionRepository employeePositionRepository;

    @Autowired
    private EmployeeContractRepository employeeContractRepository;

    @Autowired
    private EmployeeIdentityRepository employeeIdentityRepository;

    @Autowired
    private EmployeeContactRepository employeeContactRepository;

    @Autowired
    private EmployeeEmergencyContactRepository employeeEmergencyContactRepository;

    @Autowired
    private EmployeeEducationRepository employeeEducationRepository;

    @Autowired
    private EmployeeWorkExperienceRepository employeeWorkExperienceRepository;

    @BeforeMethod
    public void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(this.context)
                .addFilter(jwtAuthenticationTokenFilter).build();//集成web环境测试
        client = new ClientUtils(mvc);
    }

    @AfterMethod
    public void tearDown() {
    }

    @Test(description = "查询员工列表", dependsOnMethods = "testUpdateEmployeeBasicInfo",
            dataProvider = "defaultPermissionData")
    public void testListEmployees(TokenType tokenType, String code) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("status", EmployeeStatus.formal.name());
        params.add("pageNo", "1");
        params.add("pageSize", "20");
        this.response = client.createRequest(MethodType.get, "/api/employees")
                .setData(params).setToken(tokenType).sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), Matchers.is(code));
    }

    @Test(description = "员工入职")
    public void testCreateEmployee() {
        EmployeeRequest request1 = this.createEmployeeRequest();

        this.response = client.createRequest(MethodType.post, "/api/employees")
                .setData(request1).sendRequset().getResponse();

        assertThat("响应状态断言", response, hasEntry("code", ResultCode.SUCCESS.getCode()));
        this.employeeId = employeeRepository.findByEmployeeNo(request1.getEmployeeNo()).get().getId();

    }

    @Test(description = "创建已存在编号的员工", dependsOnMethods = "testCreateEmployee")
    public void testCreateEmployeeWithExistNo() {
        EmployeeRequest request = this.createEmployeeRequest();
        request.setEmployeeNo(employeeRepository.findById(super.employeeId_NoPrtmission).get().getEmployeeNo());
        this.response = client.createRequest(MethodType.post, "/api/employees")
                .setData(request).sendRequset().getResponse();

        assertThat("响应状态断言", response, hasEntry("code", ResultCode.EMPLOYEE_NO_EXISTS.getCode()));
    }


    @Test(description = "更新员工基本信息", dependsOnMethods = "testCreateEmployee")
    public void testUpdateEmployeeBasicInfo() {
        EmployeeBasicRequest request = new EmployeeBasicRequest();
        request.setName(TestUtils.getChineseName());
        request.setEmployeeNo("No." + System.currentTimeMillis());
        request.setAvatar("10392971803496448");
        request.setAvatarName("batman.png");
        request.setMobile(TestUtils.getPhone());
        request.setGender(Gender.male);
        request.setBirthday(new Date());

        this.response = client.createRequest(MethodType.put, "/api/employees/" + employeeId + "/basic")
                .setData(request).sendRequset().getResponse();

        assertThat("响应状态断言", response, hasEntry("code", ResultCode.SUCCESS.getCode()));
        assertThat("校验数据库状态", employeeRepository
                .findById(employeeId).get().getName(), is(request.getName()));

        //更新员工基本信息,不带头像信息
        request.setAvatar(null);
        request.setAvatarName(null);
        this.response = client.createRequest(MethodType.put, "/api/employees/" + employeeId + "/basic")
                .setData(request).sendRequset().getResponse();
        assertThat("响应状态断言", response, hasEntry("code", ResultCode.SUCCESS.getCode()));


        //更新已存在编号员工基本信息
        request.setEmployeeNo(employeeRepository.findById(super.employeeId_NoPrtmission).get().getEmployeeNo());
        this.response = client.createRequest(MethodType.put, "/api/employees/" + employeeId + "/basic")
                .setData(request).sendRequset().getResponse();

        assertThat("响应状态断言", response, hasEntry("code", ResultCode.EMPLOYEE_NO_EXISTS.getCode()));
    }

    @Test(description = "更新员工自定义表单信息", dependsOnMethods = {"testCreateEmployee"})
    public void testUpdateEmployeeCustomizedInfo() {
        EmployeeCustomizedFormsDTO employeeCustomizedFormsDTO = new EmployeeCustomizedFormsDTO();
        employeeCustomizedFormsDTO.setFormId(0L);
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("1", "10");
        jsonArray.add(jsonObject);
        employeeCustomizedFormsDTO.setFormData(jsonArray);

        this.response = client.createRequest(MethodType.put, "/api/employees/" + employeeId + "/customize")
                .setData(employeeCustomizedFormsDTO).sendRequset().getResponse();
        assertThat("响应状态断言", response, hasEntry("code", ResultCode.CUSTOMIZED_FORM_IS_NULL.getCode()));

        employeeCustomizedFormsDTO.setFormId(1L);
        this.response = client.createRequest(MethodType.put, "/api/employees/0/customize")
                .setData(employeeCustomizedFormsDTO).sendRequset().getResponse();
        assertThat("响应状态断言", response, hasEntry("code", ResultCode.EMPLOYEE_NOT_EXISTS.getCode()));

        this.response = client.createRequest(MethodType.put, "/api/employees/" + employeeId + "/customize")
                .setData(employeeCustomizedFormsDTO).sendRequset().getResponse();

        assertThat("响应状态断言", response, hasEntry("code", ResultCode.SUCCESS.getCode()));
    }

    @Test(description = "更新员工岗位信息", dependsOnMethods = {"testCreateEmployee"})
    public void testUpdateEmployeePositionInfo() {
        Long leaderId = 1L;
        List<EmployeePositionDTO> employeePositionDTOList = getEmployeePositionInfo().getPosition();
        employeePositionDTOList.forEach(employeePositionDTO -> employeePositionDTO.setLeaderId(leaderId));
        this.response = client.createRequest(MethodType.put, "/api/employees/" + employeeId + "/position")
                .setData(employeePositionDTOList).sendRequset().getResponse();

        assertThat("响应状态断言", response, hasEntry("code", ResultCode.SUCCESS.getCode()));
        EmployeePosition ep = employeePositionRepository.findAllByEmployeeId(employeeId).get(0);
        assertThat("校验数据库状态", ep.getLeaderId(), is(leaderId));
    }

    @Test(description = "更新员工工作信息", dependsOnMethods = {"testCreateEmployee"})
    public void testUpdateEmployeeJobInfo() {
        EmployeeJobDTO jobDTO = getEmployeeBasicInfo().getJob();
        jobDTO.setType(EmployeeType.laborContract);
        this.response = client.createRequest(MethodType.put, "/api/employees/" + employeeId + "/job")
                .setData(jobDTO).sendRequset().getResponse();

        Employee employee = employeeRepository.findById(employeeId).get();
        assertThat("响应状态断言", response, hasEntry("code", ResultCode.SUCCESS.getCode()));
        assertThat("校验数据库员工类型", employee.getType(), is(jobDTO.getType()));
    }

    @Test(description = "更新员工合同信息", dependsOnMethods = {"testCreateEmployee"})
    public void testUpdateEmployeeContractInfo() {
        List<EmployeeContractDTO> employeeContractDTOList = getEmployeeBasicInfo().getContract();
        String signUnit = "武汉有招";
        employeeContractDTOList.forEach(employeeContractDTO -> employeeContractDTO.setSignUnit(signUnit));
        this.response = client.createRequest(MethodType.put, "/api/employees/" + employeeId + "/contract")
                .setData(employeeContractDTOList).sendRequset().getResponse();

        assertThat("响应状态断言", response, hasEntry("code", "0"));
        assertThat("校验数据库员工签约单位",
                employeeContractRepository.findAllByEmployeeId(employeeId).get(0).getSignUnit(), is(signUnit));
    }

    @Test(description = "更新员工证件信息", dependsOnMethods = {"testCreateEmployee"})
    public void testUpdateEmployeeIdentityInfo() {
        List<EmployeeIdentityDTO> employeeIdentityDTOS = getEmployeeBasicInfo().getIdentity();
        employeeIdentityDTOS.forEach(employeeIdentityDTO -> employeeIdentityDTO.setType(IdentityType.passport));
        this.response = client.createRequest(MethodType.put, "/api/employees/" + employeeId + "/identity")
                .setData(employeeIdentityDTOS).sendRequset().getResponse();

        assertThat("响应状态断言", response, hasEntry("code", "0"));
        assertThat("校验数据库员工证件类型",
                employeeIdentityRepository.findAllByEmployeeId(employeeId).get(0).getType(), is(IdentityType.passport));
    }

    @Test(description = "更新员工联系信息", dependsOnMethods = {"testCreateEmployee"})
    public void testUpdateEmployeeContactInfo() {
        List<EmployeeContactDTO> employeeContactDTOS = getEmployeeOtherInfo().getContact();
        employeeContactDTOS.forEach(employeeIdentityDTO -> {
            employeeIdentityDTO.setType(ContactType.residenceAddress);
            employeeIdentityDTO.setContent(TestUtils.getRoad());
        });
        this.response = client.createRequest(MethodType.put, "/api/employees/" + employeeId + "/contact")
                .setData(employeeContactDTOS).sendRequset().getResponse();

        assertThat("响应状态断言", response, hasEntry("code", "0"));
        assertThat("校验数据库员工联系方式",
                employeeContactRepository.findAllByEmployeeId(employeeId).get(0).getType(),
                is(ContactType.residenceAddress));
    }

    @Test(description = "更新员工紧急联系信息", dependsOnMethods = {"testCreateEmployee"})
    public void testUpdateEmployeeEmergencyContactInfo() {
        List<EmployeeEmergencyContactDTO> list = getEmployeeOtherInfo().getEmergencyContact();
        String relationship = "兄弟";
        list.forEach(dto -> dto.setRelationship(relationship));
        this.response = client.createRequest(MethodType.put, "/api/employees/" + employeeId + "/emergency_contact")
                .setData(list).sendRequset().getResponse();

        assertThat("响应状态断言", response, hasEntry("code", "0"));
        assertThat("校验数据库员工紧急联系方式",
                employeeEmergencyContactRepository.findAllByEmployeeId(employeeId).get(0).getRelationship(),
                is(relationship));
    }

    @Test(description = "更新员工教育信息", dependsOnMethods = {"testCreateEmployee"})
    public void testUpdateEmployeeEducationInfo() {
        List<EmployeeEducationDTO> list = getEmployeeOtherInfo().getEducation();
        String school = "华科";
        list.forEach(dto -> dto.setSchool(school));
        this.response = client.createRequest(MethodType.put, "/api/employees/" + employeeId + "/education")
                .setData(list).sendRequset().getResponse();

        assertThat("响应状态断言", response, hasEntry("code", "0"));
        assertThat("校验数据库员工教育信息",
                employeeEducationRepository.findAllByEmployeeId(employeeId).get(0).getSchool(), is(school));
    }

    @Test(description = "更新员工工作经历信息", dependsOnMethods = {"testCreateEmployee"})
    public void testUpdateEmployeeWorkExperienceInfo() {
        List<EmployeeWorkExperienceDTO> list = getEmployeeOtherInfo().getWorkExperience();
        String workUnit = "滴滴打车";
        list.forEach(dto -> dto.setWorkUnit(workUnit));
        this.response = client.createRequest(MethodType.put, "/api/employees/" + employeeId + "/work_experience")
                .setData(list).sendRequset().getResponse();

        assertThat("响应状态断言", response, hasEntry("code", "0"));
        assertThat("校验数据库员工工作经历",
                employeeWorkExperienceRepository.findAllByEmployeeId(employeeId).get(0).getWorkUnit(), is(workUnit));
    }

    @Test(description = "更新员工详情信息", dependsOnMethods = {"testCreateEmployee"})
    public void testGetEmployeeDetailInfo() {
        this.response = client.createRequest(MethodType.get, "/api/employees/" + employeeId)
                .sendRequset().getResponse();

        assertThat("响应状态断言", response, hasEntry("code", "0"));
        assertThat("校验数据库员工联系方式",
                employeeRepository.findById(employeeId).orElse(null), notNullValue());
    }

    @Test(description = "员工转正,未匹配到合同", dependsOnMethods = {"testCreateEmployee"})
    public void testQualifyEmployee_NotMatchContract() {
        DateTime dateTime = new DateTime();

        QualifyEmployeeRequest request = new QualifyEmployeeRequest();
        request.setDate(dateTime.plusYears(10).toDate());
        request.setAttachment("1212121");
        request.setFileName("转正函");
        this.response = client.createRequest(MethodType.put, "/api/employees/" + employeeId + "/qualify")
                .setData(request).sendRequset().getResponse();

        assertThat("响应状态断言", response, hasEntry("code", "13112"));
    }

    @Test(description = "员工转正", dependsOnMethods = {"testQualifyEmployee_NotMatchContract"})
    public void testQualifyEmployee() {
        DateTime probationEndDate = new DateTime().plusMonths(2);

        QualifyEmployeeRequest request = new QualifyEmployeeRequest();
        request.setDate(probationEndDate.toDate());
        request.setAttachment("1212121");
        request.setFileName("转正函");
        this.response = client.createRequest(MethodType.put, "/api/employees/" + employeeId + "/qualify")
                .setData(request).sendRequset().getResponse();

        assertThat("响应状态断言", response, hasEntry("code", "0"));
        assertThat("校验数据库员工联系方式",
                employeeRepository.findById(employeeId).get().getStatus(), is(EmployeeStatus.formal));

        client.createRequest(MethodType.put, "/api/employees/" + employeeId + "/qualify")
                .setData(request).sendRequset().getResponse();
    }

    @Test(description = "员工转岗", dependsOnMethods = {"testQualifyEmployee"})
    public void testTransferEmployee() {
        TransferEmployeeRequest request = new TransferEmployeeRequest();
        List<OrganizationTreeResponse> departments =
                organizationRepository.findCompanyOrDepartmentTree(OrganizationEnums.department);
        OrganizationTreeResponse department = departments.get(0);

        List<PositionEntity> positions =
                positionRepository.findAllByDepartmentIdAndEnableAndDeleted(department.getId(),
                        YesNo.YES, YesNo.NO);

        int beforeCount = employeePositionRepository.findAll().size();

        request.setCompanyId(department.getParentId());
        request.setDepartmentId(department.getId());
        request.setPositionId(positions.get(positions.size() - 1).getId());
        request.setMasterId(employeeId);
        request.setReason("职位晋升");
        request.setDate(new Date());
        this.response = client.createRequest(MethodType.put, "/api/employees/" + employeeId + "/transfer")
                .setData(request).sendRequset().getResponse();

        assertThat("响应状态断言", response, hasEntry("code", "0"));
        int afterCount = employeePositionRepository.findAll().size();
        assertThat("校验数据库员工岗位个数", afterCount, is(beforeCount + 1));
    }

    @Test(description = "员工离职", dependsOnMethods = {"testTransferEmployee"})
    public void testResignEmployee() {
        ResignEmployeeRequest request = new ResignEmployeeRequest();
        request.setDate(new Date());
        request.setAttachment("1212121");
        request.setFileName("离职函");
        request.setHandoverManId(super.employeeId_NoPrtmission);
        this.response = client.createRequest(MethodType.put, "/api/employees/" + employeeId + "/resignation")
                .setData(request).sendRequset().getResponse();

        assertThat("响应状态断言", response, hasEntry("code", "0"));
        assertThat("校验数据库员工状态",
                employeeRepository.findById(employeeId).get().getStatus(), is(EmployeeStatus.former));
    }

    @Test(description = "获取员工离职记录", dependsOnMethods = {"testCreateEmployee", "testResignEmployee"})
    public void testGetEmployeeResign() {
        this.response = client.createRequest(MethodType.get, "/api/employees/" + employeeId + "/resignation")
                .sendRequset().getResponse();

        assertThat("响应状态断言", response, hasEntry("code", "0"));
    }


    @Test(description = "获取我的个人信息")
    public void testGetEmployeeMeInfo() {
        this.response = client.createRequest(MethodType.get, "/api/employees/me")
                .sendRequset().getResponse();

        assertThat("响应状态断言", response, hasEntry("code", "0"));
        boolean superAdmin = response.getJSONObject("data").getBoolean("superAdmin");
        assertThat("响应code检查", superAdmin, Matchers.is(true));
    }

    @Test(description = "获取员工权限信息", dependsOnMethods = {"testCreateEmployee"})
    public void testGetEmployeePermissionInfo() {
        this.response = client.createRequest(MethodType.get, "/api/employees/me/permission")
                .sendRequset().getResponse();

        assertThat("响应状态断言", response, hasEntry("code", "0"));
    }

    @Test(description = "获取员工历史信息", dependsOnMethods = {"testCreateEmployee"})
    public void testGetEmployeeHistoryInfo() {
        this.response = client.createRequest(MethodType.get, "/api/employees/" + employeeId + "/history")
                .sendRequset().getResponse();

        assertThat("响应状态断言", response, hasEntry("code", "0"));
    }

    @Test(description = "获取员工列表选项")
    public void testGetEmployeeOptionInfo() {
        this.response = client.createRequest(MethodType.get, "/api/employees/option")
                .sendRequset().getResponse();

        assertThat("响应状态断言", response, hasEntry("code", "0"));
    }

    private void initSession() {
        client.createRequest(MethodType.get, "/api/employees/me")
                .sendRequset().getResponse();
    }

    private EmployeeRequest createEmployeeRequest() {
        EmployeeRequest request = new EmployeeRequest();
        request.setName(TestUtils.getChineseName());
        request.setEmployeeNo("No." + System.currentTimeMillis());
        request.setAvatar("10392971803496448");
        request.setAvatarName("batman.png");
        request.setMobile(TestUtils.getPhone());
        request.setGender(Gender.male);
        request.setBirthday(new Date());

        EmployeeBasicInfoDTO basicInfo = getEmployeeBasicInfo();
        EmployeePositionInfoDTO positionInfo = getEmployeePositionInfo();
        EmployeeOtherInfoDTO otherInfo = getEmployeeOtherInfo();
        request.setBasicInfo(basicInfo);
        request.setPositionInfo(positionInfo);
        request.setOtherInfo(otherInfo);
        return request;
    }

    private EmployeeBasicInfoDTO getEmployeeBasicInfo() {
        EmployeeBasicInfoDTO basicInfo = new EmployeeBasicInfoDTO();
        EmployeeJobDTO job = new EmployeeJobDTO();
        job.setHireDate(new Date());
        job.setStatus(EmployeeStatus.probation);
        job.setType(EmployeeType.labor);
        job.setWorkDate(new Date());
        basicInfo.setJob(job);

        DateTime start = new DateTime();
        DateTime end = start.plusYears(1);
        DateTime probationEndDate = start.plusMonths(3);

        EmployeeContractDTO contract = new EmployeeContractDTO();
        contract.setStartDate(start.toDate());
        contract.setEndDate(end.toDate());
        contract.setSignUnit("有招武汉");
        contract.setProbationEndDate(probationEndDate.toDate());
        contract.setPeriod(ContractPeriod.year1);
        basicInfo.setContract(Lists.newArrayList(contract));

        EmployeeIdentityDTO identity = new EmployeeIdentityDTO();
        identity.setType(IdentityType.identity);
        identity.setCode("4202811989786765544");
        basicInfo.setIdentity(Lists.newArrayList(identity));

        return basicInfo;
    }

    private EmployeePositionInfoDTO getEmployeePositionInfo() {
        initSession();//建立回话，目的是定位到具体的租户库
        EmployeePositionInfoDTO positionInfo = new EmployeePositionInfoDTO();
        EmployeePositionDTO position = new EmployeePositionDTO();

        position.setCompanyId(companyId);
        position.setDepartmentId(departmentId);
        position.setPositionId(positionId);
        position.setStartDate(new Date());
        position.setLeaderId(1L);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("1", 1);
        position.setCustomizedFields(jsonObject);
        positionInfo.setPosition(Lists.newArrayList(position));
        return positionInfo;
    }

    private EmployeeOtherInfoDTO getEmployeeOtherInfo() {
        EmployeeOtherInfoDTO otherInfo = new EmployeeOtherInfoDTO();
        EmployeeContactDTO contact = new EmployeeContactDTO();
        contact.setType(ContactType.email);
        contact.setContent(TestUtils.getEmail(10, 14));
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
        education.setEducation(Education.master);
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
}
