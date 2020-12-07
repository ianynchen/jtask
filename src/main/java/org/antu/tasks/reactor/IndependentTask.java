package org.antu.tasks.reactor;

import io.vavr.control.Try;
import java.util.Optional;
import org.antu.tasks.Task;
import org.antu.tasks.TaskAggregator;
import org.antu.tasks.TaskProcessor;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

/**
 * An {@link IndependentTask} is a task without any dependencies, and hence requires no
 * {@link TaskAggregator}.
 *
 * @param <RequestT> Input type to {@link Task}
 * @param <ResponseT> Output type from {@link Task}
 * @author ianynchen
 */
public abstract class IndependentTask<RequestT, ResponseT> extends AbstractTask<RequestT, RequestT, ResponseT> {

  /**
   * Constructs an {@link IndependentTask} with fail strategy set to ignore, and uses
   * calling thread to execute the task.
   */
  public IndependentTask(TaskProcessor<RequestT, ResponseT> processor) {
    super(processor, null, null);
  }

  /**
   * Constructs an {@link IndependentTask} with specified fail strategy, and uses
   * calling thread to execute the task.
   * @param failStrategy fail strategy
   */
  public IndependentTask(TaskProcessor<RequestT, ResponseT> processor, FailStrategy failStrategy) {
    super(processor, failStrategy);
  }

  /**
   * Constructs an {@link IndependentTask} with fail strategy set to ignore, and uses
   * specified {@link Scheduler} to run the task.
   * @param scheduler specified {@link Scheduler}
   */
  public IndependentTask(TaskProcessor<RequestT, ResponseT> processor, Scheduler scheduler) {
    super(processor,null, scheduler);
  }

  /**
   * Constructs an {@link IndependentTask} with fail strategy set to ignore, and uses
   * specified {@link Scheduler} to run the task.
   * @param failStrategy fail strategy
   * @param scheduler specified {@link Scheduler}
   */
  public IndependentTask(TaskProcessor<RequestT, ResponseT> processor,
                         FailStrategy failStrategy,
                         Scheduler scheduler) {
    super(processor, failStrategy, scheduler);
  }

  @Override
  public Mono<Try<ResponseT>> execute(Optional<RequestT> request) {
    return Mono.<Try<ResponseT>>create(sink -> {
      Try<ResponseT> response = Try.of(() -> processor.process(request));
      sink.success(response);
    }).subscribeOn(scheduler());
  }
}
