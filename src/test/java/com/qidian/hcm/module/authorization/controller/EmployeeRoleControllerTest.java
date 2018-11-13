package com.qidian.hcm.module.authorization.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.qidian.hcm.BaseTest;
import com.qidian.hcm.common.utils.ResultCode;
import com.qidian.hcm.module.employee.entity.Employee;
import com.qidian.hcm.module.employee.enums.EmployeeStatus;
import com.qidian.hcm.module.employee.repository.EmployeeRepository;
import com.qidian.hcm.utils.ClientUtils;
import com.qidian.hcm.utils.MethodType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SuppressWarnings("PMD")
public class EmployeeRoleControllerTest extends BaseTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    private Long employeeId;

    @BeforeMethod
    public void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(this.context)
                .addFilter(jwtAuthenticationTokenFilter).build();//集成web环境测试
        client = new ClientUtils(mvc);

    }

    @AfterMethod
    public void tearDown() {
    }


    @Test
    public void testGetEmployeeRolesList() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("status", EmployeeStatus.probation.name());
        params.add("pageNo", "1");
        params.add("pageSize", "20");

        JSONObject response = client.createRequest(MethodType.get, "/api/authorization/employees")
                .setData(params)
                .sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is(ResultCode.SUCCESS.getCode()));
    }

    @Test
    public void testGetEmployeeRoleList_SearchKeyword() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("status", EmployeeStatus.probation.name());
        params.add("keyword", "1");
        params.add("pageNo", "1");
        params.add("pageSize", "20");

        JSONObject response = client.createRequest(MethodType.get, "/api/authorization/employees")
                .setData(params)
                .sendRequset().getResponse();
        assertThat("响应code检查", response.getString("code"), is(ResultCode.SUCCESS.getCode()));
    }

    @Test(dependsOnMethods = "testGetEmployeeRolesList")
    public void testGetEmployeeRole() {
        PageRequest pageRequest = PageRequest.of(0, 20, Sort.by(Sort.Order.desc("id")));
        Page<Employee> employees = employeeRepository.findByStatusNot(EmployeeStatus.former, pageRequest);
        employeeId = employees.getContent().get(0).getId();

        JSONObject response = client.createRequest(MethodType.get,
                "/api/authorization/employees/" + employeeId + "/roles")
                .sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is(ResultCode.SUCCESS.getCode()));
    }

    @Test(dependsOnMethods = "testGetEmployeeRolesList")
    public void testGetEmployeeRole_EmployeeNotExist() {
        JSONObject response = client.createRequest(MethodType.get,
                "/api/authorization/employees/0/roles")
                .sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is(ResultCode.EMPLOYEE_NOT_EXISTS.getCode()));
    }

    @Test(dependsOnMethods = "testGetEmployeeRole")
    public void testUpdateEmployeeRole() {
        JSONObject roleData = client.createRequest(MethodType.get, "/api/authorization/roles")
                .sendRequset().getResponse();
        Long roleId = roleData.getJSONArray("data").getJSONObject(0).getLong("id");

        List<Long> roles = Lists.newArrayList(roleId);
        JSONObject response = client.createRequest(MethodType.post,
                "/api/authorization/employees/" + employeeId + "/roles")
                .setData(roles)
                .sendRequset().getResponse();
        assertThat("响应code检查", response.getString("code"), is(ResultCode.SUCCESS.getCode()));
    }

    @Test(dependsOnMethods = "testGetEmployeeRole")
    public void testUpdateEmployeeRole_RoleNotExist() {
        List<Long> roles = Lists.newArrayList(0L);
        JSONObject response = client.createRequest(MethodType.post,
                "/api/authorization/employees/" + employeeId + "/roles")
                .setData(roles)
                .sendRequset().getResponse();
        assertThat("响应code检查", response.getString("code"), is(ResultCode.ROLE_NOT_EXISTS.getCode()));
    }
}