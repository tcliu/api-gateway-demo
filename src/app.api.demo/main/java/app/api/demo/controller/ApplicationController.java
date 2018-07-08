package app.api.demo.controller;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.slf4j.LoggerFactory.getLogger;

@RestController
@RequestMapping("/api")
public class ApplicationController {

    private static final Logger LOGGER = getLogger(ApplicationController.class);

    @Value("${server.port}")
    private int port;

    @GetMapping("/test")
    public String testGet() {
        return String.format("Hello world from port %s!", port);
    }

    @PostMapping("/test")
    public Object testPost(@RequestBody Object payload) {
        LOGGER.info("Posted {} from port {}", payload, port);
        return payload;
    }
}
