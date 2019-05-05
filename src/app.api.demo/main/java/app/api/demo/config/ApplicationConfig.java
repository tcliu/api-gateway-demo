package app.api.demo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class ApplicationConfig {

    @Primary
    @Bean
    public ThreadPoolExecutor threadPoolExecutor() {
        ThreadPoolExecutor tpe = new ThreadPoolExecutor(8, 8, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>());
        log.info("Created {}", tpe);
        return tpe;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper om = new ObjectMapper();
        om.findAndRegisterModules();
        om.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        return om;
    }
}
