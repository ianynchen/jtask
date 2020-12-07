package org.antu.tasks;

import io.vavr.control.Try;
import java.util.Optional;
import org.antu.tasks.reactor.BaseParallelTask;
import reactor.core.publisher.Mono;

/**
 * A {@link Task} is a unit of work that is usually completed in its own thread.
 * A task can have 0 or more dependency tasks, meaning these dependency tasks have
 * to be executed prior to executing current task. A task can be viewed as a mathematical
 * function, where an input is transformed into an output. The task is optimized to hide details
 * about concurrency and error handling.
 * @param <RequestT> Input type
 * @param <ResponseT> Output type
 *
 * @author ianynchen
 */
public interface Task<RequestT, ResponseT> {

  /**
   * What strategy to take if this task fails as a dependency of another {@link BaseParallelTask}
   */
  enum FailStrategy {
    TERMINATE,
    IGNORE
  }

  /**
   * Execute a task with optional input. The task can determine whether to
   * produce some default output or throw an exception when input is not present.
   * @param request optional input
   * @return a {@link Try} that includes either the success output or exception thrown.
   */
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

  default Mono<Try<ResponseT>> execute(Mono<Try<Optional<RequestT>>> request) {
    return request.flatMap(requestTry -> {
      if (requestTry.isFailure()) {
        return Mono.just(Try.failure(requestTry.getCause()));
      }
      return execute(requestTry.get());
    });
  }

  FailStrategy getFailStrategy();
}
