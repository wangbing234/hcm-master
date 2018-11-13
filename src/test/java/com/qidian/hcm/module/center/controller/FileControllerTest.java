package com.qidian.hcm.module.center.controller;

import com.alibaba.fastjson.JSONObject;
import com.qidian.hcm.BaseTest;
import com.qidian.hcm.module.center.request.UploadFileRequest;
import com.qidian.hcm.module.employee.repository.AttachmentRepository;
import com.qidian.hcm.utils.MethodType;
import com.qidian.hcm.utils.TestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;


@SuppressWarnings("PMD")
public class FileControllerTest extends BaseTest {

    @Autowired
    private AttachmentRepository attachmentRepository;

    private JSONObject response;

    private Long fileId;


    @AfterMethod
    public void tearDown() {
    }

    @Test(description = "文件上传")
    public void testUploadFile() throws Exception {
        UploadFileRequest uploadFileRequest = new UploadFileRequest();
        uploadFileRequest.setFileBase64Str(Base64.getEncoder()
                .encodeToString("测试上传".getBytes(StandardCharsets.UTF_8.name())));
        uploadFileRequest.setFileName("test.txt");

        response = client.createRequest(MethodType.post, "/api/file")
                .setData(uploadFileRequest).sendRequset().getResponse();

        assertThat("上传文件校验响应状态", response.getString("code"), is("0"));
        assertThat("上传文件时间校验", response.getJSONObject("data").getString("createTime"),
                is(TestUtils.getDate(TestUtils.DATE_FORMAT2)));
        assertThat("oss文件名非空校验", response.getJSONObject("data").getString("fileNameOnOss"),
                notNullValue());

        this.fileId = response.getJSONObject("data").getLong("fileId");
        assertThat("数据库记录检查", attachmentRepository.findByFileId(fileId).get(), notNullValue());
    }

    @Test(dependsOnMethods = {"testUploadFile"})
    public void testGetTempUrl() {
        response = client.createRequest(MethodType.get, "/api/file/get_temp_url/" + fileId)
                .sendRequset().getResponse();

        assertThat("响应状态校验", response.getString("code"), is("0"));
        assertThat("文件地址校验", response.getString("data"), notNullValue());
    }

    @Test(dependsOnMethods = {"testUploadFile"}, description = "下载文件--fileId存在")
    public void testDownloadFile_C001() {
        client.createRequest(MethodType.get, "/api/file/download/" + fileId)
                .addTextContent().sendRequset();
        assertThat("响应码校验", client.getStatusCode(), equalTo(200));
    }

    @Test(description = "下载文件--fileId不存在")
    public void testDownloadFile_C002() {
        try {
            client.createRequest(MethodType.get, "/api/file/download/" + 1L)
                    .addTextContent().sendRequset();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}