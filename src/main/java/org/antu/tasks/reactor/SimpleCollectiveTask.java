package org.antu.tasks.reactor;

import java.util.Optional;
import org.antu.tasks.Task;
import org.antu.tasks.TaskAggregator;
import reactor.core.scheduler.Scheduler;

/**
 * A simple collective task is a task, whose aggregator output is the task output. In
 * this case, a task processor is not required.
 * @param <RequestT> task input type
 * @param <DependencyResponseT> dependency task output type
 * @param <ResponseT> task output type.
 *
 * @author ianynchen
 */
public class SimpleCollectiveTask<RequestT, DependencyResponseT, ResponseT>
    extends BaseCollectiveTask<RequestT, DependencyResponseT, ResponseT, ResponseT> {

  public SimpleCollectiveTask(Task<RequestT, DependencyResponseT> dependency,
                              TaskAggregator<DependencyResponseT, ResponseT> aggregator) {
    super(Optional::get, dependency, aggregator);
  }

  public SimpleCollectiveTask(Task<RequestT, DependencyResponseT> dependency,
                              TaskAggregator<DependencyResponseT, ResponseT> aggregator,
                              FailStrategy failStrategy) {
    super(Optional::get, dependency, aggregator, failStrategy);
  }

  public SimpleCollectiveTask(Task<RequestT, DependencyResponseT> dependencyTask,
                              TaskAggregator<DependencyResponseT, ResponseT> aggregator,
                              FailStrategy failStrategy,
                              Scheduler scheduler) {
    super(Optional::get, dependencyTask, aggregator, failStrategy, scheduler);
  }
}
