package com.qidian.hcm.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.*;
import java.nio.file.Paths;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.newInputStream;
import static java.nio.file.Files.newOutputStream;

/**
 * base64工具类
 */
@Slf4j
public final class Base64Util {

    private Base64Util() {}

    /**
     * 本地文件转成base64字符串
     */
    public static String fileToBase64(String imgFile) {
        String base64Str;
        byte[] data = new byte[0];
        try (
                InputStream inputStream = newInputStream(Paths.get(imgFile))
        ) {
            data = new byte[inputStream.available()];
            inputStream.read(data);
        } catch (IOException e) {
            log.error(ExceptionUtils.getMessage(e));
        }

        base64Str = new String(Base64.encodeBase64(data), UTF_8);
        return base64Str;
    }

    /**
     * base64字符串转换成文件
     */
    public static boolean base64ToFile(String base64Str, String filePath) {
        // 对字节数组字符串进行Base64解码并生成图片
        if (StringUtils.isEmpty(base64Str)) {
            return false;
        }
        // Base64解码
        byte[] b = Base64.decodeBase64(base64Str);
        for (int i = 0; i < b.length; ++i) {
            if (b[i] < 0) {
                // 调整异常数据
                b[i] += 256;
            }
        }
        try (
                OutputStream outputStream = newOutputStream(Paths.get(filePath));
        ) {
            outputStream.write(b);
            outputStream.flush();
        } catch (IOException e) {
            log.error(ExceptionUtils.getMessage(e));
            return false;
        }
        return true;
    }
}
