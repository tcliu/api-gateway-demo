package app.api.demo.config;

import com.google.common.base.Predicates;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Configuration
@EnableSwagger2
@Profile("!test")
public class SwaggerConfig implements WebMvcConfigurer {

    @Value("${security.oauth2.token-url:}")
    private String tokenUrl;

    @Bean
    public Docket apiDoc(@Value("${spring.application.name:Unknown}") String title,
                         @Value("${spring.application.env:Unknown}") String env,
                         @Value("${spring.application.description:${spring.application.name:Unknown}}") String description,
                         @Value("${spring.application.contact:Unknown}") String contact,
                         @Value("${spring.application.version:Unknown}") String version) {
        return new Docket(DocumentationType.SWAGGER_2)
            .apiInfo(new ApiInfoBuilder()
                .title(title + " (" + env.toUpperCase() + ")")
                .description(description)
                .contact(new Contact(contact, null, null))
                .version(version)
                .build())
            .directModelSubstitute(LocalDate.class, java.sql.Date.class)
            .directModelSubstitute(LocalDateTime.class, java.util.Date.class)
            .select()
            .apis(RequestHandlerSelectors.basePackage("app.api.demo.controller"))
            .paths(Predicates.not(PathSelectors.regex("/error.*")))
            .build();
    }

}
