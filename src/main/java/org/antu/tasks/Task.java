package org.antu.tasks;

import io.vavr.control.Try;
import java.util.Optional;
import org.antu.tasks.reactor.BaseParallelTask;
import reactor.core.publisher.Mono;

public interface Task<RequestT, ResponseT> {

  /**
   * What strategy to take if this task fails as a dependency of another {@link BaseParallelTask}
   */
  enum FailStrategy {
    FAIL_ALL,
    IGNORE
  }

  Mono<Try<ResponseT>> execute(Optional<RequestT> request);

  default Mono<Try<ResponseT>> execute(RequestT request) {
    return execute(Optional.ofNullable(request));
  }

  default Mono<Try<ResponseT>> execute(Try<Optional<RequestT>> request) {
    if (request.isFailure()) {
      return Mono.just(Try.failure(request.getCause()));
    }
    return execute(request.get());
  }

  default Mono<Try<ResponseT>> executeAsync(Mono<Optional<RequestT>> request) {
    return request.flatMap(req -> execute(req));
  }

  default Mono<Try<ResponseT>> executeAsyncTry(Mono<Try<Optional<RequestT>>> request) {
    return request.flatMap(requestTry -> {
      if (requestTry.isFailure()) {
        return Mono.just(Try.failure(requestTry.getCause()));
      }
      return execute(requestTry.get());
    });
  }

  FailStrategy getFailStrategy();
}
