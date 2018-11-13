package com.qidian.hcm.module.center.controller;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.OSSObject;
import com.qidian.hcm.common.config.HCMConfig;
import com.qidian.hcm.module.center.service.FileService;
import com.qidian.hcm.module.center.request.UploadFileRequest;
import com.qidian.hcm.common.exception.BizException;
import com.qidian.hcm.common.utils.Result;
import com.qidian.hcm.common.utils.ResultCode;
import com.qidian.hcm.module.employee.entity.Attachment;
import com.qidian.hcm.module.employee.repository.AttachmentRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Optional;

import static com.qidian.hcm.common.utils.ResultGenerator.genSuccessResult;

@Controller
@Slf4j
@RequestMapping("/api/file")
@Api(tags = "公用服务--文件服务")
public class FileController {
    @Autowired
    private FileService fileService;

    @Autowired
    private HCMConfig hcmConfig;

    @Autowired
    private AttachmentRepository attachmentRepository;

    /**
     * 文件上传
     *
     * @param uploadFileRequest
     * @return
     */
    @ApiOperation(value = "上传文件", notes = "上传文件")
    @PostMapping
    @ResponseBody
    public Result uploadFile(@RequestBody UploadFileRequest uploadFileRequest) {
        Attachment attachment = fileService.uploadFileToOSS(
                uploadFileRequest.getFileBase64Str(), uploadFileRequest.getFileName());
        return genSuccessResult(attachment);
    }

    /**
     * 生成临时访问的url
     *
     * @param fileId
     * @return
     */
    @ApiOperation(value = "根据fileId生成临时访问url", notes = "根据fileId生成临时访问url")
    @GetMapping("/get_temp_url/{fileId}")
    @ResponseBody
    public Result getTempUrl(@PathVariable(value = "fileId") Long fileId) {
        return genSuccessResult(fileService.getTempUrl(fileId));
    }

    /**
     * 文件下载
     *
     * @param response
     * @throws IOException
     */
    @GetMapping(value = "/download/{fileId}")
    public void downloadFile(@PathVariable(value = "fileId") Long fileId,
                             HttpServletResponse response) throws IOException {
        Optional<Attachment> attachmentOptional = attachmentRepository.findByFileId(fileId);
        if (!attachmentOptional.isPresent()) {
            throw new BizException(ResultCode.FILE_NOT_FOUND);
        }
        Attachment attachment = attachmentOptional.get();
        // 创建OSSClient实例。
        OSSClient ossClient = new OSSClient(hcmConfig.getAliyunOssEndpoint(),
                hcmConfig.getAliyunOssAccessKeyId(), hcmConfig.getAliyunOssAccessKeySecret());
        // ossObject包含文件所在的存储空间名称、文件名称、文件元信息以及一个输入流。
        OSSObject ossObject = ossClient.getObject(hcmConfig.getAliyunOssBucketName(), attachment.getFileNameOnOss());

        InputStream objectContent = ossObject.getObjectContent();
        response.setContentType("application/force-download");
        response.setHeader("Content-Disposition", "attachment;fileName=" + attachment.getOriginName());
        //前端协议
        response.setHeader("type", "download");
        byte[] buffer = new byte[1024];
        InputStream fis = null; //文件输入流
        BufferedInputStream bis = null;
        OutputStream os = null; //输出流
        try {
            os = response.getOutputStream();
            fis = objectContent;
            bis = new BufferedInputStream(fis);
            int bytesRead = 0;
            while ((bytesRead = ossObject.getObjectContent().read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            log.error(ExceptionUtils.getMessage(e));
        } finally {
            if (bis != null) {
                bis.close();
            }
            if (fis != null) {
                fis.close();
            }
        }
    }

    /**
     * 上传认证
     *
     * @param request
     * @param response
     */
//    public void authorization(HttpServletRequest request, HttpServletResponse response) {
//        String dir = "user-dir";
//        String host = "http://" + hcmConfig.getAliyunOssBucketName() + "." + hcmConfig.getAliyunOssEndpoint();
//        OSSClient client = new OSSClient(hcmConfig.getAliyunOssEndpoint(),
//                hcmConfig.getAliyunOssAccessKeyId(), hcmConfig.getAliyunOssAccessKeySecret());
//
//        long expireTime = hcmConfig.getAliyunOssTempUrlExpire();
//        long expireEndTime = System.currentTimeMillis() + expireTime;
//        Date expiration = new Date(expireEndTime);
//        PolicyConditions policyConds = new PolicyConditions();
//        policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
//        policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);
//        String postPolicy = client.generatePostPolicy(expiration, policyConds);
//        try {
//            byte[] binaryData = postPolicy.getBytes("utf-8");
//            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
//            String postSignature = client.calculatePostSignature(postPolicy);
//            Map<String, String> respMap = new HashMap<>();
//            respMap.put("accessid", hcmConfig.getAliyunOssAccessKeyId());
//            respMap.put("policy", encodedPolicy);
//            respMap.put("signature", postSignature);
//            respMap.put("dir", dir);
//            respMap.put("host", host);
//            respMap.put("expire", String.valueOf(expireEndTime / 1000));
//            response.setHeader("Access-Control-Allow-Origin", "*");
//            response.setHeader("Access-Control-Allow-Methods", "GET, POST");
//            // response(request, response, JSONObject.toJSONString(respMap));
//        } catch (IOException e) {
//            log.error(ExceptionUtils.getMessage(e));
//        } finally {
//            log.info("");
//        }
//    }

//    private void response(HttpServletRequest request, HttpServletResponse response,
//                          String results) throws IOException {
//        String callbackFunName = request.getParameter("callback");
//        if (callbackFunName == null || callbackFunName.equalsIgnoreCase("")) {
//            response.getWriter().println(results);
//        } else {
//            response.getWriter().println(callbackFunName + "( " + results + " )");
//        }
//        response.setStatus(HttpServletResponse.SC_OK);
//        response.flushBuffer();
//    }

}
