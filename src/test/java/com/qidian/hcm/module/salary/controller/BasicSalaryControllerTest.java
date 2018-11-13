package com.qidian.hcm.module.salary.controller;

import com.alibaba.fastjson.JSONObject;
import com.qidian.hcm.BaseTest;
import com.qidian.hcm.common.utils.Base64Util;
import com.qidian.hcm.module.center.request.UploadFileRequest;
import com.qidian.hcm.utils.ClientUtils;
import com.qidian.hcm.utils.MethodType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SuppressWarnings("PMD")
public class BasicSalaryControllerTest extends BaseTest {

    @BeforeMethod
    public void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(this.context)
                .addFilter(jwtAuthenticationTokenFilter).build();//集成web环境测试
        super.client = new ClientUtils(mvc);
    }

    @AfterMethod
    public void tearDown() {
    }

    @Test(description = "获取核算状态 1-已核算|0-未核算")
    public void testGetAccountStatus() {
        JSONObject response = client.createRequest(MethodType.get, "/api/salaries/account_status")
                .sendRequset().getResponse();
        logger.info(response);
        assertThat("响应code检查", response.getString("code"), is("0"));
    }

    @Test(description = "薪酬核算")
    public void testAccountingSalary() {
        JSONObject response = client.createRequest(MethodType.put, "/api/salaries/accounting")
                .sendRequset().getResponse();
        logger.info(response);
        assertThat("响应code检查", response.getString("code"), is("0"));
    }

    @Test(description = "取消核算", dependsOnMethods = "testAccountingSalary")
    public void testAccountingCancel() {
        JSONObject response = client.createRequest(MethodType.put, "/api/salaries/accounting_cancel")
                .sendRequset().getResponse();
        logger.info(response);
        assertThat("响应code检查", response.getString("code"), is("0"));
    }

    @Test(description = "薪酬核算", dependsOnMethods = "testAccountingCancel")
    public void testAccountingSalary2() {
        JSONObject response = client.createRequest(MethodType.put, "/api/salaries/accounting")
                .sendRequset().getResponse();
        logger.info(response);
        assertThat("响应code检查", response.getString("code"), is("0"));
    }

    @Test(description = "归档", dependsOnMethods = "testAccountingSalary2")
    public void testSaveSalaryRecord() {
        JSONObject response = client.createRequest(MethodType.post, "/api/salaries/records")
                .sendRequset().getResponse();
        logger.info(response);
        assertThat("响应code检查", response.getString("code"), is("0"));
    }

    @Test(description = "获取当前计薪周期的开始时间和结束时间")
    public void testCurrentCycleTime() {
        JSONObject response = client.createRequest(MethodType.get, "/api/salaries/current_cycle_time")
                .sendRequset().getResponse();
        logger.info(response);
        assertThat("响应code检查", response.getString("code"), is("0"));
    }

    @Test
    public void testGetSalaryRecords() {
        JSONObject response = client.createRequest(MethodType.get, "/api/salaries/records?pageNo=1&pageSize=10")
                .sendRequset().getResponse();
        logger.info(response);
        assertThat("响应code检查", response.getString("code"), is("0"));
    }

    @Test
    public void testGetSalaryMonthReport() {
        String month = "2018-09";
        JSONObject response = client.createRequest(MethodType.get, "/api/salaries/report/" +
                month + "?pageNo=1&pageSize=10").sendRequset().getResponse();
        logger.info(response);
        assertThat("响应code检查", response.getString("code"), is("0"));
    }

    @Test(description = "导入薪资明细")
    public void testImportSalaryMonthExcel() throws IOException {
        UploadFileRequest request = new UploadFileRequest();
        request.setFileName("本月薪资明细.xls");
        File file = ResourceUtils.getFile("classpath:薪资明细模板.xls");
        request.setFileBase64Str(Base64Util.fileToBase64(file.getAbsolutePath()));
        JSONObject response = client.createRequest(MethodType.post, "/api/salaries/import")
                .setData(request).sendRequset().getResponse();
        logger.info(response);
        assertThat("响应code检查", response.getString("code"), is("0"));
    }

    @Test(description = "薪资项列表", dependsOnMethods = "testImportSalaryMonthExcel")
    public void testGetSalaryItems() {
        JSONObject response = client.createRequest(MethodType.get, "/api/salaries/salary_items/")
                .sendRequset().getResponse();
        logger.info(response);
        assertThat("响应code检查", response.getString("code"), is("0"));
    }

    @Test(description = "导出薪资明细模板", dependsOnMethods = "testImportSalaryMonthExcel")
    public void testExportSalaryMonthExcelTemplate() {
        MvcResult authorization = client.createRequest(MethodType.get, "/api/salaries/export_templet")
                .sendRequset().getResult();
        assertThat("检查文件二进制流", !StringUtils.isEmpty(authorization));
    }

    @Test(description = "导出薪资明细", dependsOnMethods = "testExportSalaryMonthExcelTemplate")
    public void testExportSalaryMonthExcel() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("month", "2018-09");
        MvcResult authorization = client.createRequest(MethodType.get, "/api/salaries/export")
                .setData(params).sendRequset().getResult();
        assertThat("检查文件二进制流", !StringUtils.isEmpty(authorization));
    }
}