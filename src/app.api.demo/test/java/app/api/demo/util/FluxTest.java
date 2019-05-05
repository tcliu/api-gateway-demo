package app.api.demo.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.stream.Collectors.toList;

@Slf4j
public class FluxTest {

    @Test
    public void run() throws InterruptedException {
        AtomicInteger writeCount = new AtomicInteger(0);
        Flux<Integer> flux = Flux.<Integer>create(sink -> {
            for (int i=1; i<=100; i++) {
                sink.next(i);
                log.info("Wrote {}", i);
                writeCount.incrementAndGet();
            }
        });
        List<List<Integer>> workerResults = new ArrayList<>();
        List<CompletableFuture<?>> futList = IntStream.rangeClosed(1, 10)
            .mapToObj(i -> runAsync(() -> {
                List<Integer> nums = new ArrayList<>();
                workerResults.add(nums);
                Disposable d = flux.subscribe(n -> {
                    log.info("Worker {} received {}", i, n);
                    nums.add(n);
                });
            })).collect(toList());
        futList.forEach(CompletableFuture::join);
        log.info("Write count: {}", writeCount);
        for (int i=0; i<workerResults.size(); i++) {
            log.info("Worker {} collected {} nums.", i+1, workerResults.get(i).size());
        }
        //Thread.sleep(10000L);
    }

}
