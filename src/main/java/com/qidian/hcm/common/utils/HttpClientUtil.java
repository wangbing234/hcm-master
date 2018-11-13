package com.qidian.hcm.common.utils;

import com.alibaba.fastjson.JSON;
import com.qidian.hcm.common.constants.SystemConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/*
 * 利用HttpClient进行post请求的工具类
 */
@Slf4j
public final class HttpClientUtil {

    public static final String MSG = "调用失败";

    private HttpClientUtil() {
    }

//    /**
//     * @param url
//     * @param map   参数
//     * @param token
//     */
//    public static String doPostMap(String url, Map<String, String> map, String token) {
//        String result = null;
//        try (CloseableHttpClient httpClient = HttpClients.createSystem()){
//            HttpPost httpPost = new HttpPost(url);
//            if (!StringUtils.isEmpty(token)) {
//                httpPost.addHeader(SystemConstant.TOKEN, token);
//            }
//            //设置参数
//            List<NameValuePair> list = new ArrayList<NameValuePair>();
//            Iterator iterator = map.entrySet().iterator();
//            while (iterator.hasNext()) {
//                Entry<String, String> elem = (Entry<String, String>) iterator.next();
//                list.add(new BasicNameValuePair(elem.getKey(), elem.getValue()));
//            }
//            if (list.size() > 0) {
//                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, CHARSET);
//                httpPost.setEntity(entity);
//            }
//
//            HttpResponse response = httpClient.execute(httpPost);
//            if (response != null) {
//                HttpEntity resEntity = response.getEntity();
//                if (resEntity != null) {
//                    result = EntityUtils.toString(resEntity, CHARSET);
//                }
//            }
//        } catch (IOException ex) {
//            log.error(MSG, ex);
//        }
//        return result;
//    }


    /**
     * post请求
     * @param url
     * @param obj
     */
    public static String doPostObject(String url, Object obj, String token) {
        try (CloseableHttpClient httpClient = HttpClients.createSystem()) {
            HttpPost request = new HttpPost(url);
            request.addHeader("content-type", "application/json");
            request.addHeader("Accept", "application/json");
            if (!StringUtils.isEmpty(token)) {
                request.addHeader(SystemConstant.TOKEN, token);
            }

            if (null != obj) {
                String str = JSON.toJSONString(obj);
                StringEntity params = new StringEntity(str, StandardCharsets.UTF_8.name());
                request.setEntity(params);
            }

            HttpResponse response = httpClient.execute(request);
            if (response != null) {
                String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8.name());
                log.info("返回json：{} ", responseBody);
                return responseBody;
            }
        } catch (IOException ex) {
            log.error(MSG, ex);
        }
        return null;
    }
}  