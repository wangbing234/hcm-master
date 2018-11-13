package com.qidian.hcm.utils;

import com.alibaba.fastjson.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.MultiValueMap;
import org.springframework.util.PathMatcher;
import org.testng.Reporter;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("PMD")
public class ClientUtils {

    private MockMvc mvc;

    private MethodType method;

    private String uri;

    private MockHttpServletRequestBuilder builder = null;

    private MvcResult result;

    private boolean changeDefaultContent = false;//默认发送json格式

    private int statusCode;

    private Exception error;

    private TokenType type = TokenType.MANAGER;


    public ClientUtils(MockMvc mvc, MethodType method, String uri) {
        this.mvc = mvc;
        this.method = method;
        this.uri = uri;
    }

    public ClientUtils(MockMvc mvc) {
        this.mvc = mvc;
    }

    private ClientUtils buildRequest() {
        switch (method) {
            case get:
                builder = MockMvcRequestBuilders.get(uri);
                break;
            case post:
                builder = MockMvcRequestBuilders.post(uri);
                break;
            case put:
                builder = MockMvcRequestBuilders.put(uri);
                break;
            case delete:
                builder = MockMvcRequestBuilders.delete(uri);
                break;
            default:
                break;
        }
        return this;
    }

    public ClientUtils addHeader(String name, String value) {
        builder.header(name, value);
        return this;
    }

    public ClientUtils addHeaders(HttpHeaders httpHeaders) {
        builder.headers(httpHeaders);
        return this;
    }

    public ClientUtils defaultContent() {
        builder.contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8);
        return this;
    }

    public ClientUtils addTextContent() {
        changeDefaultContent = true;
        builder.contentType(MediaType.TEXT_PLAIN)
                .characterEncoding(StandardCharsets.UTF_8.name())
                .accept(MediaType.TEXT_PLAIN);
        return this;
    }


    public ClientUtils createRequest(MethodType method, String uri) {
        this.method = method;
        this.uri = uri;
        buildRequest();
        return this;
    }

    public ClientUtils setData(String params) {
        builder.content(params);
        return this;
    }

    public ClientUtils setData(JSONObject params) {
        builder.content(params.toJSONString());
        return this;
    }

    public ClientUtils setData(Object params) {
        builder.content(TestUtils.toJSON(params));
        return this;
    }

    public ClientUtils setData(MultiValueMap<String, String> params) {
        builder.params(params);
        return this;
    }

    public void addToken(TokenType token) {
        //判断是否是不验证的url
        if (!isMatchUrl(uri)) {
            addHeader("Authorization", TestUtils.readToken(token));
        }
    }

    public ClientUtils setToken(TokenType token) {
        type = token;
        return this;
    }

    public ClientUtils sendRequset() {
        addToken(type);
        if (!changeDefaultContent) {
            defaultContent();
        }

        long startTime = System.currentTimeMillis();
        try {
            result = mvc.perform(builder).andReturn();
            this.statusCode = result.getResponse().getStatus();
            long endTime = System.currentTimeMillis();
            log("请求地址: " + result.getRequest().getRequestURL());
            log("请求方式: " + result.getRequest().getMethod());
            if (result.getRequest().getContentAsString() != null) {
                log("请求参数: " + result.getRequest().getContentAsString());
            }
            log("响应内容: " + result.getResponse().getContentAsString());
            log("消耗时间: " + (endTime - startTime) + "毫秒");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            error = e;
        }
        return this;
    }

    public void log(String msg) {
        Reporter.log(msg, true);
    }

    public MvcResult getResult() {
        return result;
    }

    public JSONObject getResponse() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(result.getResponse().getContentAsString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public Exception getException() {
        return error;
    }

    public void getHeaders() {
        result.getRequest().getRequestURL();
    }

    public int getStatusCode() {
        return statusCode;
    }

    private static final List<String> AUTH_LIST = Arrays.asList(
            "/api/user/phoneCode/*",
            "/api/user/login/**",
            "/api/user/register",
            "/api/user/phone",
            "/api/user/add_not_active_user",
            "/api/user/base_info/*",
            "/api/salaries/export");

    private PathMatcher pathMatcher = new AntPathMatcher();

    /**
     * 排除url
     *
     * @param uri
     * @return
     */
    private boolean isMatchUrl(String uri) {
        for (String pattern : AUTH_LIST) {
            if (pathMatcher.match(pattern, uri)) {
                System.out.println("exclude url:" + uri);
                return true;
            }
        }
        return false;
    }
}
