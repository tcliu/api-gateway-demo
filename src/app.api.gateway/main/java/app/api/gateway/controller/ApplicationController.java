package app.api.gateway.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.security.Principal;

@Api(value = "Application Controller", description = "Application Controller")
@RestController
@RequestMapping("/api")
public class ApplicationController {

    @Value("${server.port}")
    private int port;

    @PreAuthorize("#oauth2.hasScope('read') and hasRole('ROLE_APP') and hasRole('ROLE_USER')")
    @ApiOperation(value = "Test get", response = String.class)
    @GetMapping("/test")
    public String userGet(@ApiIgnore Principal principal) {
        return String.format("Hello world from %s at port %s!",
                principal == null ? "anonymous" : principal.getName(), port);
    }

    @PreAuthorize("#oauth2.hasScope('read') and hasRole('ROLE_APP') and hasRole('ROLE_ADMIN')")
    @ApiOperation(value = "Test get for admin", response = String.class)
    @GetMapping("/atest")
    public String adminGet(@ApiIgnore Principal principal) {
        return String.format("Hello world from %s at port %s!",
                principal == null ? "anonymous" : principal.getName(), port);
    }
}
