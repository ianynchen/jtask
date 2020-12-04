package org.antu.tasks.reactor;

import io.vavr.control.Try;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.antu.tasks.Task;
import org.antu.tasks.TaskAggregator;
import org.antu.tasks.TaskProcessor;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

/**
 * Factory to creating tasks.
 *
 * @author yinichen
 */
public class TaskFactory {

  private TaskFactory() {}

  public static <RequestT, ResponseT> IndependentTask<RequestT, ResponseT> independentTask(TaskProcessor<RequestT, ResponseT> processor) {
    return independentTask(processor, Task.FailStrategy.IGNORE, null);
  }

  public static <RequestT, ResponseT> IndependentTask<RequestT, ResponseT> independentTask(TaskProcessor<RequestT, ResponseT> processor,
                                                                                           Task.FailStrategy strategy) {
    return independentTask(processor, strategy, null);
  }

  public static <RequestT, ResponseT> IndependentTask<RequestT, ResponseT> independentTask(TaskProcessor<RequestT, ResponseT> processor,
                                                                                           Scheduler scheduler) {
    return independentTask(processor, Task.FailStrategy.IGNORE, scheduler);
  }

  public static <RequestT, ResponseT> IndependentTask<RequestT, ResponseT> independentTask(TaskProcessor<RequestT, ResponseT> processor,
                                                                                           Task.FailStrategy strategy,
                                                                                           Scheduler scheduler) {
    return new IndependentTask<RequestT, ResponseT>(processor, strategy, scheduler) {
      @Override
      public Mono<Try<ResponseT>> execute(Optional<RequestT> request) {
        return super.execute(request);
      }
    };
  }

  public static <RequestT, DependencyResponseT, ResponseT>
  SimpleCollectiveTask<RequestT, DependencyResponseT, ResponseT> collectiveTask(Task<RequestT, DependencyResponseT> dependency,
                                                                                TaskAggregator<DependencyResponseT, ResponseT> aggregator) {
    return collectiveTask(dependency, aggregator, Task.FailStrategy.IGNORE, null);
  }

  public static <RequestT, DependencyResponseT, ResponseT>
  SimpleCollectiveTask<RequestT, DependencyResponseT, ResponseT> collectiveTask(Task<RequestT, DependencyResponseT> dependency,
                                                                                TaskAggregator<DependencyResponseT, ResponseT> aggregator,
                                                                                Task.FailStrategy strategy) {
    return collectiveTask(dependency, aggregator, Task.FailStrategy.IGNORE, null);
  }

  public static <RequestT, DependencyResponseT, ResponseT>
  SimpleCollectiveTask<RequestT, DependencyResponseT, ResponseT> collectiveTask(Task<RequestT, DependencyResponseT> dependency,
                                                                                TaskAggregator<DependencyResponseT, ResponseT> aggregator,
                                                                                Scheduler scheduler) {
    return collectiveTask(dependency, aggregator, Task.FailStrategy.IGNORE, scheduler);
  }

  public static <RequestT, DependencyResponseT, ResponseT>
  SimpleCollectiveTask<RequestT, DependencyResponseT, ResponseT> collectiveTask(Task<RequestT, DependencyResponseT> dependency,
                                                                                TaskAggregator<DependencyResponseT, ResponseT> aggregator,
                                                                                Task.FailStrategy strategy,
                                                                                Scheduler scheduler) {
    return new SimpleCollectiveTask<RequestT, DependencyResponseT, ResponseT>(dependency, aggregator, strategy, scheduler) {
      @Override
      public Mono<Try<ResponseT>> execute(Optional<Collection<RequestT>> request) {
        return super.execute(request);
      }
    };
  }

  public static <RequestT, DependencyResponseT, AggregatorResponseT, ResponseT>
  BaseCollectiveTask<RequestT, DependencyResponseT, AggregatorResponseT, ResponseT> collectiveTask(TaskProcessor<AggregatorResponseT, ResponseT> processor,
                                                                                                   Task<RequestT, DependencyResponseT> dependency,
                                                                                                   TaskAggregator<DependencyResponseT, AggregatorResponseT> aggregator) {
    return collectiveTask(processor, dependency, aggregator, Task.FailStrategy.IGNORE, null);
  }

  public static <RequestT, DependencyResponseT, AggregatorResponseT, ResponseT>
  BaseCollectiveTask<RequestT, DependencyResponseT, AggregatorResponseT, ResponseT> collectiveTask(TaskProcessor<AggregatorResponseT, ResponseT> processor,
                                                                                                   Task<RequestT, DependencyResponseT> dependency,
                                                                                                   TaskAggregator<DependencyResponseT, AggregatorResponseT> aggregator,
                                                                                                   Task.FailStrategy strategy) {
    return collectiveTask(processor, dependency, aggregator, Task.FailStrategy.IGNORE, null);
  }

  public static <RequestT, DependencyResponseT, AggregatorResponseT, ResponseT>
  BaseCollectiveTask<RequestT, DependencyResponseT, AggregatorResponseT, ResponseT> collectiveTask(TaskProcessor<AggregatorResponseT, ResponseT> processor,
                                                                                                   Task<RequestT, DependencyResponseT> dependency,
                                                                                                   TaskAggregator<DependencyResponseT, AggregatorResponseT> aggregator,
                                                                                                   Scheduler scheduler) {
    return collectiveTask(processor, dependency, aggregator, Task.FailStrategy.IGNORE, scheduler);
  }

  public static <RequestT, DependencyResponseT, AggregatorResponseT, ResponseT>
  BaseCollectiveTask<RequestT, DependencyResponseT, AggregatorResponseT, ResponseT> collectiveTask(TaskProcessor<AggregatorResponseT, ResponseT> processor,
                                                                                                   Task<RequestT, DependencyResponseT> dependency,
                                                                                                   TaskAggregator<DependencyResponseT, AggregatorResponseT> aggregator,
                                                                                                   Task.FailStrategy strategy,
                                                                                                   Scheduler scheduler) {
    return new BaseCollectiveTask<RequestT, DependencyResponseT, AggregatorResponseT, ResponseT>(processor, dependency, aggregator, strategy, scheduler) {
      @Override
      public Mono<Try<ResponseT>> execute(Optional<Collection<RequestT>> request) {
        return super.execute(request);
      }
    };
  }

  public static <RequestT, DependencyResponseT, ResponseT>
  SimpleParallelTask<RequestT, DependencyResponseT, ResponseT> parallelTask(List<Task<RequestT, DependencyResponseT>> dependencyTasks,
                                                                            TaskAggregator<DependencyResponseT, ResponseT> aggregator) {
    return parallelTask(dependencyTasks, aggregator, Task.FailStrategy.IGNORE, null);
  }

  public static <RequestT, DependencyResponseT, ResponseT>
  SimpleParallelTask<RequestT, DependencyResponseT, ResponseT> parallelTask(List<Task<RequestT, DependencyResponseT>> dependencyTasks,
                                                                            TaskAggregator<DependencyResponseT, ResponseT> aggregator,
                                                                            Task.FailStrategy strategy) {
    return parallelTask(dependencyTasks, aggregator, strategy, null);
  }

  public static <RequestT, DependencyResponseT, ResponseT>
  SimpleParallelTask<RequestT, DependencyResponseT, ResponseT> parallelTask(List<Task<RequestT, DependencyResponseT>> dependencyTasks,
                                                                            TaskAggregator<DependencyResponseT, ResponseT> aggregator,
                                                                            Scheduler scheduler) {
    return parallelTask(dependencyTasks, aggregator, Task.FailStrategy.IGNORE, scheduler);
  }

  public static <RequestT, DependencyResponseT, ResponseT>
  SimpleParallelTask<RequestT, DependencyResponseT, ResponseT> parallelTask(List<Task<RequestT, DependencyResponseT>> dependencyTasks,
                                                                            TaskAggregator<DependencyResponseT, ResponseT> aggregator,
                                                                            Task.FailStrategy strategy,
                                                                            Scheduler scheduler) {
    return new SimpleParallelTask<RequestT, DependencyResponseT, ResponseT>(dependencyTasks, aggregator, strategy, scheduler) {
      @Override
      public Mono<Try<ResponseT>> execute(Optional<RequestT> request) {
        return super.execute(request);
      }
    };
  }


  public static <RequestT, DependencyResponseT, AggregatorResponseT, ResponseT>
  BaseParallelTask<RequestT, DependencyResponseT, AggregatorResponseT, ResponseT> parallelTask(TaskProcessor<AggregatorResponseT, ResponseT> processor,
                                                                                               List<Task<RequestT, DependencyResponseT>> dependencyTasks,
                                                                                               TaskAggregator<DependencyResponseT, AggregatorResponseT> aggregator) {
    return parallelTask(processor, dependencyTasks, aggregator, Task.FailStrategy.IGNORE, null);
  }

  public static <RequestT, DependencyResponseT, AggregatorResponseT, ResponseT>
  BaseParallelTask<RequestT, DependencyResponseT, AggregatorResponseT, ResponseT> parallelTask(TaskProcessor<AggregatorResponseT, ResponseT> processor,
                                                                                               List<Task<RequestT, DependencyResponseT>> dependencyTasks,
                                                                                               TaskAggregator<DependencyResponseT, AggregatorResponseT> aggregator,
                                                                                               Task.FailStrategy strategy) {
    return parallelTask(processor, dependencyTasks, aggregator, strategy, null);
  }

  public static <RequestT, DependencyResponseT, AggregatorResponseT, ResponseT>
  BaseParallelTask<RequestT, DependencyResponseT, AggregatorResponseT, ResponseT> parallelTask(TaskProcessor<AggregatorResponseT, ResponseT> processor,
                                                                                               List<Task<RequestT, DependencyResponseT>> dependencyTasks,
                                                                                               TaskAggregator<DependencyResponseT, AggregatorResponseT> aggregator,
                                                                                               Scheduler scheduler) {
    return parallelTask(processor, dependencyTasks, aggregator, Task.FailStrategy.IGNORE, scheduler);
  }

  public static <RequestT, DependencyResponseT, AggregatorResponseT, ResponseT>
  BaseParallelTask<RequestT, DependencyResponseT, AggregatorResponseT, ResponseT> parallelTask(TaskProcessor<AggregatorResponseT, ResponseT> processor,
                                                                                               List<Task<RequestT, DependencyResponseT>> dependencyTasks,
                                                                                               TaskAggregator<DependencyResponseT, AggregatorResponseT> aggregator,
                                                                                               Task.FailStrategy strategy,
                                                                                               Scheduler scheduler) {
    return new BaseParallelTask<RequestT, DependencyResponseT, AggregatorResponseT, ResponseT>(processor,
        dependencyTasks, aggregator, strategy, scheduler) {
      @Override
      public Mono<Try<ResponseT>> execute(Optional<RequestT> request) {
        return super.execute(request);
      }
    };
  }
}
