package com.qidian.hcm.module.custom.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qidian.hcm.BaseTest;
import com.qidian.hcm.common.enums.YesNo;
import com.qidian.hcm.module.custom.enums.FieldType;
import com.qidian.hcm.module.custom.enums.TargetType;
import com.qidian.hcm.module.custom.repository.CustomizedFieldRespository;
import com.qidian.hcm.module.custom.request.OrgCustomizedFieldRequest;
import com.qidian.hcm.utils.ClientUtils;
import com.qidian.hcm.utils.MethodType;
import com.qidian.hcm.utils.TestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

@SuppressWarnings("PMD")
public class CustomizedFieldDefinitionControllerTest extends BaseTest {

    @Autowired
    private CustomizedFieldRespository customizedFieldRespository;

    private JSONObject response;

    private Long csfid;

    private String code = "zd" + TestUtils.getDate(TestUtils.DATE_FORMAT3);

    @BeforeMethod
    public void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(this.context)
                .addFilter(jwtAuthenticationTokenFilter).build();//集成web环境测试
        client = new ClientUtils(mvc);
    }

    @AfterMethod
    public void tearDown() {
    }

    @Test(description = "自定义字段列表查询", dependsOnMethods = "testAdd")
    public void testList() {
        this.response = client.createRequest(MethodType.get, "/api/organizations/customized_fields")
                .sendRequset().getResponse();
        assertThat("响应code检查", response.getString("code"), is("0"));
        assertThat("响应data检查", response.getJSONArray("data").size(), greaterThanOrEqualTo(1));

    }

    @Test(description = "增加自定义字段")
    public void testAdd() {
        OrgCustomizedFieldRequest request = new OrgCustomizedFieldRequest();
        request.setCode(code);
        request.setFieldType(FieldType.textarea);
        request.setLabel("测试");
        request.setLength(1000);
        request.setRequired(true);
        request.setIdx(0);
        request.setTargetType(TargetType.company);
        request.setPlaceholder("提示信息");
        request.setOptions(new ArrayList<>());
        this.response = client.createRequest(MethodType.post, "/api/organizations/customized_fields")
                .setData(JSON.toJSONString(request)).sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is("0"));
        this.csfid = customizedFieldRespository.findByTargetTypeAndCode(TargetType.company, code).getId();
    }

    @Test(description = "修改自定义字段", dependsOnMethods = "testAdd")
    public void testEdit() {
        OrgCustomizedFieldRequest request = new OrgCustomizedFieldRequest();
        request.setCode(code);
        request.setFieldType(FieldType.textarea);
        request.setLabel("测试");
        request.setLength(1000);
        request.setRequired(false);
        request.setIdx(1);
        request.setTargetType(TargetType.company);
        request.setPlaceholder("提示信息");
        request.setOptions(new ArrayList<>());

        this.response = client.createRequest(MethodType.put,
                "/api/organizations/customized_fields/" + csfid)
                .setData(JSON.toJSONString(request)).sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is("0"));
        assertThat("校验数据库字段", JSONObject.parseObject(customizedFieldRespository
                .findById(csfid).get().getAttribute()).getBoolean("required"), is(false));
        assertThat("校验数据库字段", customizedFieldRespository.findById(csfid)
                .get().getIdx(), is(1));
    }

    @Test(description = "自定义字段禁用", dependsOnMethods = "testEdit")
    public void testToggleActive_C001() {
        this.response = client.createRequest(MethodType.put,
                "/api/organizations/customized_fields/" + csfid + "/toggle_active")
                .sendRequset().getResponse();
        assertThat("响应code检查", response.getString("code"), is("0"));
        assertThat("校验数据库字段", customizedFieldRespository.findById(csfid)
                .get().getEnable(), is(YesNo.NO));
    }

    @Test(description = "自定义字段启用", dependsOnMethods = "testToggleActive_C001")
    public void testToggleActive_C002() {
        this.response = client.createRequest(MethodType.put,
                "/api/organizations/customized_fields/" + csfid + "/toggle_active")
                .sendRequset().getResponse();
        assertThat("响应code检查", response.getString("code"), is("0"));
        assertThat("校验数据库字段", customizedFieldRespository
                .findById(csfid).get().getEnable(), is(YesNo.YES));
    }

    @Test(description = "删除自定义字段", dependsOnMethods = "testToggleActive_C002")
    public void testDelete() {
        response = client.createRequest(MethodType.delete,
                "/api/organizations/customized_fields/" + csfid)
                .sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is("0"));
        assertThat("校验数据库记录", customizedFieldRespository
                .findByTargetTypeAndCode(TargetType.company, code), nullValue());
    }
}