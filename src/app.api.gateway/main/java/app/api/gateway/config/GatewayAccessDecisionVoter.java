package app.api.gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GatewayAccessDecisionVoter implements AccessDecisionVoter<Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GatewayAccessDecisionVoter.class);

    @Override
    public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {
        int result = ACCESS_ABSTAIN;
        if (object instanceof FilterInvocation) {
            FilterInvocation filterInvocation = (FilterInvocation) object;
            HttpServletRequest request = filterInvocation.getRequest();

            String httpMethod = "GET";
            String antPattern = "/app*/**";
            Set<String> acceptedAuths = Stream.of("ROLE_USER", "ROLE_APP").collect(Collectors.toSet());

            AntPathRequestMatcher requestMatcher = new AntPathRequestMatcher(antPattern, httpMethod);
            boolean requestMatched = requestMatcher.matches(request);
            if (requestMatched) {
                boolean matched = authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .anyMatch(acceptedAuths::contains);
                LOGGER.info("User [{}] {} to {} {}", authentication.getPrincipal(),
                        matched ? "authorized" : "not authorized",
                        request.getMethod(), request.getRequestURI());
                if (matched) {
                    result = ACCESS_GRANTED;
                }
            }
        }
        return result;
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

}
