package org.antu.tasks.reactor;

import static org.antu.tasks.Task.FailStrategy.IGNORE;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Try;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.NonNull;
import org.antu.tasks.Task;
import org.antu.tasks.TaskAggregator;
import org.antu.tasks.TaskProcessor;
import org.apache.commons.collections4.CollectionUtils;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

/**
 * A {@link BaseCollectiveTask} is one that works on a {@link Collection} of items. This task will
 * need a single dependency task that can process each item in the collection. If an item is null,
 * it will be skipped. If the dependency task is assigned a thread pool with more than one thread,
 * each item will be processed in an available thread, achieving parallelism on a multi-core CPU
 * machine.
 *
 * @param <RequestT> Input type to the task
 * @param <DependencyResponseT> Output type from the dependency {@link Task}
 * @param <AggregatorResponseT> Output type from the {@link TaskAggregator}
 * @param <ResponseT> Output type from this task.
 *
 * @author ianynchen
 */
public abstract class BaseCollectiveTask<RequestT, DependencyResponseT, AggregatorResponseT, ResponseT>
    extends BaseChainedTask<Collection<RequestT>, RequestT, DependencyResponseT, AggregatorResponseT, ResponseT> {

  /**
   * Constructs a {@link BaseCollectiveTask} with a dependency {@link Task} and a {@link TaskAggregator}.
   * Will use the calling thread to execute this task.
   * @param dependency dependency task
   * @param aggregator task aggregator to aggregate the outputs from the dependency task
   */
  public BaseCollectiveTask(TaskProcessor<AggregatorResponseT, ResponseT> processor,
                            Task<RequestT, DependencyResponseT> dependency,
                            TaskAggregator<DependencyResponseT, AggregatorResponseT> aggregator) {
    this(processor, dependency, aggregator, IGNORE, null);
  }

  /**
   * Constructs a {@link BaseCollectiveTask} with a dependency {@link Task}, a {@link TaskAggregator},
   * with specified {@link FailStrategy}. Will use the
   * calling thread to execute this task.
   * @param dependency dependency task
   * @param aggregator task aggregator to aggregate the outputs from the dependency task
   * @param failStrategy fail strategy
   */
  public BaseCollectiveTask(TaskProcessor<AggregatorResponseT, ResponseT> processor,
                            Task<RequestT, DependencyResponseT> dependency,
                            TaskAggregator<DependencyResponseT, AggregatorResponseT> aggregator,
                            FailStrategy failStrategy) {
    this(processor, dependency, aggregator, failStrategy, null);
  }

  /**
   * Constructs a {@link BaseCollectiveTask} with a dependency {@link Task}, a {@link TaskAggregator},
   * with specified {@link FailStrategy}. Will use the
   * specified {@link Scheduler} to run this task.
   * @param dependencyTask dependency task
   * @param aggregator task aggregator to aggregate the outputs from the dependency task
   * @param failStrategy fail strategy
   * @param scheduler the scheduler to run this task
   */
  public BaseCollectiveTask(TaskProcessor<AggregatorResponseT, ResponseT> processor,
                            Task<RequestT, DependencyResponseT> dependencyTask,
                            @NonNull TaskAggregator<DependencyResponseT, AggregatorResponseT> aggregator,
                            @NonNull FailStrategy failStrategy,
                            Scheduler scheduler) {
    super(processor, Arrays.asList(dependencyTask), aggregator, failStrategy, scheduler);
  }

  @Override
  public Mono<Try<ResponseT>> execute(Optional<Collection<RequestT>> request) {
    if (CollectionUtils.isEmpty(request.orElse(new ArrayList<>()))) {
      return Mono.just(Try.failure(new IllegalArgumentException("input to task is empty")));
    }

    Task<RequestT, DependencyResponseT> dependencyTask = dependencyTasks.get(0);

    List<Mono<Tuple2<FailStrategy, Try<DependencyResponseT>>>> dependencyResponse = request.get().stream()
        .filter(req -> req != null)
        .map(req -> Mono.just(Optional.ofNullable(req)).publishOn(scheduler())
            .flatMap(r ->dependencyTask.execute(Try.success(r))
                .map(response -> Tuple.of(dependencyTask.getFailStrategy(), response)))).collect(Collectors.toList());

    return Mono.zip(dependencyResponse, (responseTuples) -> {
      List<Tuple2<FailStrategy, Try<DependencyResponseT>>> taskResponses = new ArrayList<>(responseTuples.length);
      for (Object obj: responseTuples) {
        Tuple2<FailStrategy, Try<DependencyResponseT>> tuple =
            (Tuple2<FailStrategy, Try<DependencyResponseT>>)obj;
        taskResponses.add(tuple);
      }
      return taskResponses;
    }).publishOn(scheduler())
        .map(taskResponses -> aggregator.aggregate(taskResponses))
        .map(aggregatorResponse -> {
          if (aggregatorResponse.isFailure()) {
            return Try.failure(aggregatorResponse.getCause());
          }
          return Try.of(() -> processor.process(Optional.ofNullable(aggregatorResponse.get())));
        });
  }
}
