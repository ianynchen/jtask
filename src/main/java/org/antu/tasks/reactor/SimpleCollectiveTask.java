package org.antu.tasks.reactor;

import lombok.NonNull;
import org.antu.tasks.Task;
import org.antu.tasks.TaskAggregator;
import reactor.core.scheduler.Scheduler;

public class SimpleCollectiveTask<RequestT, DependencyResponseT, ResponseT>
    extends BaseCollectiveTask<RequestT, DependencyResponseT, ResponseT, ResponseT> {

  public SimpleCollectiveTask(Task<RequestT, DependencyResponseT> dependency,
                              TaskAggregator<DependencyResponseT, ResponseT> aggregator) {
    super(req -> req.get(), dependency, aggregator);
  }

  public SimpleCollectiveTask(Task<RequestT, DependencyResponseT> dependency,
                              TaskAggregator<DependencyResponseT, ResponseT> aggregator,
                              FailStrategy failStrategy) {
    super(req -> req.get(), dependency, aggregator, failStrategy);
  }

  public SimpleCollectiveTask(Task<RequestT, DependencyResponseT> dependencyTask,
                              @NonNull TaskAggregator<DependencyResponseT, ResponseT> aggregator,
                              @NonNull FailStrategy failStrategy,
                              Scheduler scheduler) {
    super(req -> req.get(), dependencyTask, aggregator, failStrategy, scheduler);
  }
}
