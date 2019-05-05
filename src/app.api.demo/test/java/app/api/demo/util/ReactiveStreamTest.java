package app.api.demo.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

import static java.util.concurrent.CompletableFuture.runAsync;

@Slf4j
public class ReactiveStreamTest {

    public static class EndSubscriber<T> implements Flow.Subscriber<T> {

        private String name;

        private Flow.Subscription subscription;

        public EndSubscriber(String name) {
            this.name = name;
        }

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            this.subscription = subscription;
            log.info("[{}] onSubscribe {}", name, subscription);
            subscription.request(1);
        }

        @Override
        public void onNext(T item) {
            log.info("[{}] Read {}", name, item);
            subscription.request(1);
        }

        @Override
        public void onError(Throwable throwable) {
            log.error("[{}] Encountered error", name, throwable);
        }

        @Override
        public void onComplete() {
            log.info("[{}] Completed", name);
        }

    }

    @Test
    public void run() {
        SubmissionPublisher<Integer> publisher = new SubmissionPublisher<>();

        EndSubscriber<Integer> sub1 = new EndSubscriber<>("sub1");
        EndSubscriber<Integer> sub2 = new EndSubscriber<>("sub2");
        publisher.subscribe(sub1);

        CompletableFuture<?> cf = runAsync(() -> {
            for (int i = 0; i < 100; i++) {
                publisher.submit(i);
                log.info("Submitted {}", i);
                try {
                    Thread.sleep(10L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            Thread.sleep(100L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        publisher.subscribe(sub2);

        cf.join();
        publisher.close();
    }

}
