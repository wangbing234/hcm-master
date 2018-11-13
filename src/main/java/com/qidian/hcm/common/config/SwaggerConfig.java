package com.qidian.hcm.common.config;

import com.qidian.hcm.common.constants.SystemConstant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * Swagger2构建RESTful APIs
 *
 * @author AndyBao
 * @version 4.0, 2016年12月20日 下午2:00:30
 */
@Configuration
@EnableWebMvc
@EnableSwagger2
public class SwaggerConfig {

    /**
     * 创建api熟悉
     *
     * @return
     */
    @Bean
    public Docket createRestApi() {
        //增加head参数
        ParameterBuilder ticketPar = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<>();
        ticketPar.name(SystemConstant.TOKEN).description("user ticket")
                .modelRef(new ModelRef("string")).parameterType("header")
                .required(false).build(); //header中的token参数非必填，传空也可以
        pars.add(ticketPar.build());    //根据每个方法名也知道当前方法在设置什么参数


        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.qidian.hcm"))
//                .apis(RequestHandlerSelectors.basePackage("org.activiti.rest"))
                .paths(PathSelectors.any())
                .build().globalOperationParameters(pars);
    }

    /**
     * API描述
     *
     * @return
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("HCM在线文档")
                .description("在线文档：http://106.15.136.173/doc/swagger-ui.html")
                .termsOfServiceUrl("http://106.15.136.173/doc/swagger-ui.html")
                .version("0.1")
                .build();
    }

}