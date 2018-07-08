package app.api.oauth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.Arrays;

@Configuration
@EnableAuthorizationServer
public class OAuth2AuthorizationServerJwtConfig extends AuthorizationServerConfigurerAdapter {

    @Value("${oauth2.signing-key:}")
    private String signingKey;

    @Autowired
    @Qualifier("authenticationManagerBean")
    private AuthenticationManager authenticationManager;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtAccessTokenConverter jwtAccessTokenConverter;

    @Autowired
    private TokenStore tokenStore;

    @Bean
    public TokenEnhancer tokenEnhancer() {
        return new CustomTokenEnhancer();
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer(), jwtAccessTokenConverter));
        endpoints.tokenStore(tokenStore).tokenEnhancer(tokenEnhancerChain)
                .authenticationManager(authenticationManager);
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
            .withClient("user")
            .secret(passwordEncoder.encode("user"))
            .authorizedGrantTypes("password", "authorization_code", "refresh_token")
            .scopes("user", "read", "write")
            .accessTokenValiditySeconds(3600) // 1 hour
            .refreshTokenValiditySeconds(3600 * 24 * 30) // 30 days
            // .redirectUris("http://localhost:8089/");

            .and()

            .withClient("op")
            .secret(passwordEncoder.encode("op"))
            .authorizedGrantTypes("password", "authorization_code", "refresh_token")
            .scopes("operation", "read", "write")
            .accessTokenValiditySeconds(3600) // 1 hour
            .refreshTokenValiditySeconds(3600 * 24 * 30) // 30 days
        ;
    }

}
