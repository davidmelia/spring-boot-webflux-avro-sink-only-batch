package example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import uk.co.dave.consumer.fxrate.consumer.avro.AvroFxRateEvent;

@Component
@AllArgsConstructor
@Slf4j
public class StaticInstrumentEventBatchConsumer {

  private Map<String, Message<List<AvroFxRateEvent>>> cache = new HashMap<>();

  @Bean
  public Function<Flux<Message<List<AvroFxRateEvent>>>, Mono<Void>> fxRatesBatch() {
    return events -> events.flatMapSequential(event -> {
      return Mono.just(event).doOnNext(p -> log.info("Processing Batch: {}", p)).doOnNext(p -> cache.put("event", p))
          .then();
    }, 1).then();
  }

  public Map<String, Message<List<AvroFxRateEvent>>> getCache() {
    return cache;
  }
  

}
