package app.api.demo.controller;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.security.Principal;
import java.util.concurrent.ExecutorService;

@Slf4j
@RestController
@RequestMapping("/api")
public class ApplicationController {

    @Autowired
    private ExecutorService executor;

    @Value("${server.port}")
    private int port;

    @GetMapping("/executor")
    public ExecutorService threadPoolExecutor() {
        return executor;
    }

    @ApiOperation(value = "Test get", response = String.class)
    @GetMapping("/test")
    public String testGet(@ApiIgnore Principal principal) {
        return String.format("Hello world from %s at port %s!",
                principal == null ? "anonymous" : principal.getName(), port);
    }

    @ApiOperation(value = "Test post", response = Object.class)
    @PostMapping("/test")
    public Object testPost(@RequestBody Object payload, @ApiIgnore Principal principal) {
        log.info("[{}] Posted {} from port {}",
                principal == null ? "anonymous" : principal.getName(),
                payload, port);
        return payload;
    }


}
