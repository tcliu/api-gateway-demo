package app.api.gateway.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApplicationController {

    @Value("${server.port}")
    private int port;

    @PreAuthorize("#oauth2.hasScope('read')")
    @GetMapping("/test")
    public String testGet() {
        return String.format("Hello world from port %s!", port);
    }
}
