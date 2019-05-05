package app.api.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableResourceServer
public class OAuth2ResourceServerJwtConfig extends ResourceServerConfigurerAdapter {

    @Autowired
    private DefaultTokenServices defaultTokenServices;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and()
                .authorizeRequests()
                .antMatchers("/actuator/**").hasAuthority("ADMIN")
                .anyRequest()
                .permitAll();
                //.accessDecisionManager(accessDecisionManager());
    }

    public AccessDecisionManager accessDecisionManager() {
        List<AccessDecisionVoter<?>> voters = Arrays.asList(
                new RoleVoter(),
                new AuthenticatedVoter(),
                new GatewayAccessDecisionVoter()
        );
        return new UnanimousBased(voters);
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer config) {
        config.tokenServices(defaultTokenServices);
    }
}
