import com.fasterxml.classmate.TypeResolver;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;

import springfox.documentation.builders.PathSelectors;

import springfox.documentation.builders.RequestHandlerSelectors;

import springfox.documentation.service.ApiInfo;

import springfox.documentation.spi.DocumentationType;

import springfox.documentation.spring.web.plugins.Docket;

 

@Configuration

public class SwaggerConfig {

 

    @SuppressWarnings("unused")

    @Autowired

    private TypeResolver typeResolver;

 

    @Bean

    public Docket productApi() {

        return new Docket(DocumentationType.SWAGGER_2)

                .select().apis(RequestHandlerSelectors.basePackage("com.tenniscourts"))

                .paths(PathSelectors.ant("/*"))

                .build().apiInfo(apiInfo());

    }

 

    private ApiInfo apiInfo() {

        return new ApiInfoBuilder().title("Tennis Courts").description(

                "Tennis Courts is a backend API for a simple reservation platform for tennis players")

                .version("1.0").build();

    }

}
