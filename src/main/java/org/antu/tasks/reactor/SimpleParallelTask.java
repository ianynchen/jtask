package org.antu.tasks.reactor;

import java.util.List;
import lombok.NonNull;
import org.antu.tasks.Task;
import org.antu.tasks.TaskAggregator;
import reactor.core.scheduler.Scheduler;

public class SimpleParallelTask<RequestT, DependencyResponseT, ResponseT>
    extends BaseParallelTask<RequestT, DependencyResponseT, ResponseT, ResponseT>{

  public SimpleParallelTask(List<Task<RequestT, DependencyResponseT>> dependencyTasks,
                            TaskAggregator<DependencyResponseT, ResponseT> aggregator) {
    super(req -> req.get(), dependencyTasks, aggregator);
  }

  public SimpleParallelTask(List<Task<RequestT, DependencyResponseT>> dependencyTasks,
                            TaskAggregator<DependencyResponseT, ResponseT> aggregator,
                            FailStrategy failStrategy) {
    super(req -> req.get(), dependencyTasks, aggregator, failStrategy);
  }

  public SimpleParallelTask(List<Task<RequestT, DependencyResponseT>> dependencyTasks,
                            TaskAggregator<DependencyResponseT, ResponseT> aggregator,
                            FailStrategy failStrategy,
                            Scheduler scheduler) {
    super(req -> req.get(), dependencyTasks, aggregator, failStrategy, scheduler);
  }
}
