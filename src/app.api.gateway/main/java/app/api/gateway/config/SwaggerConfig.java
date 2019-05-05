package app.api.gateway.config;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Predicates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.GrantType;
import springfox.documentation.service.OAuth;
import springfox.documentation.service.ResourceOwnerPasswordCredentialsGrant;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.DocExpansion;
import springfox.documentation.swagger.web.ModelRendering;
import springfox.documentation.swagger.web.OperationsSorter;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SecurityConfigurationBuilder;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;
import springfox.documentation.swagger.web.TagsSorter;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.lang.reflect.WildcardType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static springfox.documentation.schema.AlternateTypeRules.newRule;

@Configuration
@EnableSwagger2
@Profile("!test")
public class SwaggerConfig implements WebMvcConfigurer {

    @Value("${security.oauth2.client.accessTokenUri:}")
    private String tokenUrl;

    @Autowired
    private TypeResolver typeResolver;

    @Bean
    public Docket apiDoc(@Value("${spring.application.name:Unknown}") String title,
                         @Value("${spring.application.env:Unknown}") String env,
                         @Value("${spring.application.description:${spring.application.name:Unknown}}") String description,
                         @Value("${spring.application.contact:Unknown}") String contact,
                         @Value("${spring.application.version:Unknown}") String version) {
        return new Docket(DocumentationType.SWAGGER_2)
            .apiInfo(new ApiInfoBuilder()
                .title(title + " (" + env + ")")
                .description(description)
                .contact(new Contact(contact, null, null))
                .version(version)
                .build())
            .directModelSubstitute(LocalDate.class, java.sql.Date.class)
            .directModelSubstitute(LocalDateTime.class, java.util.Date.class)
            .genericModelSubstitutes(ResponseEntity.class)
            .alternateTypeRules(
                newRule(typeResolver.resolve(DeferredResult.class,
                        typeResolver.resolve(ResponseEntity.class, WildcardType.class)),
                        typeResolver.resolve(WildcardType.class)))
            .useDefaultResponseMessages(false)

            .select()
            .apis(RequestHandlerSelectors.basePackage("app.api.gateway.controller"))
            .paths(Predicates.not(PathSelectors.regex("/error.*")))
            .build()
            .securitySchemes(securitySchemes())
            .securityContexts(securityContexts())
            .enableUrlTemplating(true);
    }

    @Bean
    public SecurityConfiguration securityConfig(
            @Value("${security.oauth2.client.clientId:}") String clientId,
            @Value("${security.oauth2.client.clientSecret:}") String clientSecret,
            @Value("${spring.application.name:Unknown}") String appName) {
        return SecurityConfigurationBuilder.builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .appName(appName)
                .build();
    }

    @Primary
    @Bean
    public SwaggerResourcesProvider swaggerResourcesProvider() {
        return () -> Arrays.asList(
            swaggerResource("default", "/v2/api-docs", "2.0"),
            swaggerResource("app", "/app/v2/api-docs", "2.0"),
            swaggerResource("app1", "/app1/v2/api-docs", "2.0"),
            swaggerResource("app2", "/app2/v2/api-docs", "2.0")
        );
    }

    @Bean
    public UiConfiguration uiConfig() {
        return UiConfigurationBuilder.builder()
                .deepLinking(true)
                .displayOperationId(false)
                .defaultModelsExpandDepth(1)
                .defaultModelExpandDepth(1)
                .defaultModelRendering(ModelRendering.EXAMPLE)
                .displayRequestDuration(false)
                .docExpansion(DocExpansion.NONE)
                .filter(false)
                .maxDisplayedTags(null)
                .operationsSorter(OperationsSorter.ALPHA)
                .showExtensions(false)
                .tagsSorter(TagsSorter.ALPHA)
                .supportedSubmitMethods(UiConfiguration.Constants.DEFAULT_SUBMIT_METHODS)
                .validatorUrl(null)
                .build();
    }

    private SwaggerResource swaggerResource(String name, String location, String version) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setLocation(location);
        swaggerResource.setSwaggerVersion(version);
        return swaggerResource;
    }

    private AuthorizationScope[] authorizationScopes() {
        return new AuthorizationScope[] {
            new AuthorizationScope("read", "read all"),
            new AuthorizationScope("write", "write all")
        };
    }

    private List<? extends SecurityScheme> securitySchemes() {
        List<GrantType> grantTypes = Collections.singletonList(
                new ResourceOwnerPasswordCredentialsGrant(tokenUrl));
        OAuth oauth = new OAuth("Authorization",
                Arrays.asList(authorizationScopes()), grantTypes);
        return Collections.singletonList(oauth);
    }

    private List<SecurityContext> securityContexts() {
        SecurityContext securityContext = SecurityContext.builder()
                .securityReferences(securityReferences())
                .forPaths(PathSelectors.any())
                .build();
        return Collections.singletonList(securityContext);
    }

    private List<SecurityReference> securityReferences() {
        SecurityReference securityReference = new SecurityReference(
                "Authorization", authorizationScopes());
        return Collections.singletonList(securityReference);
    }

}
