package org.antu.tasks.reactor;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Try;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.NonNull;
import org.antu.tasks.Task;
import org.antu.tasks.TaskAggregator;
import org.antu.tasks.TaskProcessor;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

/**
 * A {@link BaseParallelTask} is to
 * @param <RequestT> request type a task needs as input
 * @param <DependencyResponseT> response type produced by all its dependencies
 * @param <AggregatorResponseT> aggregator response type
 * @param <ResponseT> response type a task produces
 *
 * @author ianynchen
 */
public abstract class BaseParallelTask<RequestT, DependencyResponseT, AggregatorResponseT, ResponseT>
    extends BaseChainedTask<RequestT, RequestT, DependencyResponseT, AggregatorResponseT, ResponseT> {

  public BaseParallelTask(TaskProcessor<AggregatorResponseT, ResponseT> processor,
                          List<Task<RequestT, DependencyResponseT>> dependencyTasks,
                          TaskAggregator<DependencyResponseT, AggregatorResponseT> aggregator) {
    this(processor, dependencyTasks, aggregator, FailStrategy.IGNORE, null);
  }

  public BaseParallelTask(TaskProcessor<AggregatorResponseT, ResponseT> processor,
                          List<Task<RequestT, DependencyResponseT>> dependencyTasks,
                          TaskAggregator<DependencyResponseT, AggregatorResponseT> aggregator,
                          FailStrategy failStrategy) {
    this(processor, dependencyTasks, aggregator, failStrategy, null);
  }

  public BaseParallelTask(TaskProcessor<AggregatorResponseT, ResponseT> processor,
                          List<Task<RequestT, DependencyResponseT>> dependencyTasks,
                          TaskAggregator<DependencyResponseT, AggregatorResponseT> aggregator,
                          FailStrategy failStrategy,
                          Scheduler scheduler) {
    super(processor, dependencyTasks, aggregator, failStrategy, scheduler);
  }

  public Mono<Try<ResponseT>> execute(Optional<RequestT> request) {
    if (request == null) {
      return Mono.just(Try.failure(new NullPointerException("input to task is null")));
    }

    // if no schedulers specified, use current calling thread.
    return Mono.just(request).publishOn(scheduler())
        .flatMap(req -> {
          // get dependency task response Mono and form Tuple2<Task, Try<DependencyResponseT>>
          return Mono.zip(dependencyTasks.stream().map(dependencyTask -> dependencyTask.execute(request)
                  .map(response -> Tuple.of(dependencyTask.getFailStrategy(), response)))
              .collect(Collectors.toList()), tuples -> aggregator.aggregate(Arrays.stream(tuples).map(tuple -> (Tuple2<FailStrategy, Try<DependencyResponseT>>)tuple)
              .collect(Collectors.toList()))).map(aggregatorResponse -> {
            if (aggregatorResponse.isSuccess()) {
              return Try.of(() -> processor.process(Optional.ofNullable(aggregatorResponse.get())));
            }
            return Try.failure(aggregatorResponse.getCause());
          });
        });
  }
}
