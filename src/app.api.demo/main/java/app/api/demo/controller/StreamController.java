package app.api.demo.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.concurrent.CompletableFuture.runAsync;

@Slf4j
@RestController
@RequestMapping("/stream")
public class StreamController {

    @Autowired
    private ExecutorService executor;

    @Autowired
    private ApplicationEventMulticaster eventMulticaster;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @PostConstruct
    private void init() {
        ExecutorService es = Executors.newFixedThreadPool(10);
        runAsync(() -> {
            for (int i = 1; ; i++) {
                PayloadApplicationEvent<Integer> event = new PayloadApplicationEvent<>(this, i);
                eventPublisher.publishEvent(event);
                log.info("Published Integer {}", i);
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, es);
        runAsync(() -> {
            for (int i = 1; ; i++) {
                PayloadApplicationEvent<String> event = new PayloadApplicationEvent<>(this, "A" + i);
                eventPublisher.publishEvent(event);
                log.info("Published String {}", "A" + i);
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, es);
    }

    @GetMapping(value = "/int")
    public void intStream(HttpServletRequest request, HttpServletResponse response, WebRequest webRequest) throws IOException, InterruptedException {
        doStream(response, Integer.class);
    }

    @GetMapping(value = "/str")
    public void strStream(HttpServletRequest request, HttpServletResponse response, WebRequest webRequest) throws IOException, InterruptedException {
        doStream(response, String.class);
    }

    private void doStream(HttpServletResponse response, Class<?> payloadClass) throws InterruptedException, IOException {
        response.setHeader("Content-Type", "text/event-stream;charset=UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Connection", "keep-alive");
        PrintWriter w = response.getWriter();
        AtomicReference<ApplicationListener> eventListenerRef = new AtomicReference<>();
        Semaphore semaphore = new Semaphore(0);
        ApplicationListener eventListener = event -> {
            if (w.checkError()) {
                semaphore.release();
                if (eventListenerRef.get() != null) {
                    eventMulticaster.removeApplicationListener(eventListenerRef.get());
                    log.info("Event listener {} removed", eventListenerRef.get());
                }
            } else {
                if (event instanceof PayloadApplicationEvent) {
                    PayloadApplicationEvent<?> payloadApplicationEvent = (PayloadApplicationEvent) event;
                    if (payloadApplicationEvent.getResolvableType().getGeneric(0).getRawClass() == payloadClass) {
                        Object payload = payloadApplicationEvent.getPayload();
                        w.printf("data: %s\n\n", payload);
                        w.flush();
                        log.info("{} received {} of type {}", eventListenerRef.get(), payload,
                            payloadApplicationEvent.getResolvableType());
                    }
                }
            }
        };
        eventListenerRef.set(eventListener);
        eventMulticaster.addApplicationListener(eventListener);
        semaphore.acquire();
    }

    @Data
    @AllArgsConstructor
    public static class IntWrapper {

        private Integer value;

    }
}
