package org.antu.tasks.reactor;

import com.google.common.base.Preconditions;
import java.util.List;
import org.antu.tasks.Task;
import org.antu.tasks.TaskAggregator;
import org.antu.tasks.TaskProcessor;
import org.apache.commons.collections4.CollectionUtils;
import reactor.core.scheduler.Scheduler;

/**
 * A {@link BaseChainedTask} is a task with dependency tasks. All dependency tasks are executed
 * before executing login within current task. The output of the dependency tasks are used as the
 * input to actual task execution logic.
 * @param <RequestT> Input type to task
 * @param <DependencyRequestT> Input type to dependency task
 * @param <DependencyResponseT> Output type from dependency task
 * @param <AggregatorResponseT> Output type from aggregator
 * @param <ResponseT> Response type of task
 * @author yinichen
 */
public abstract class BaseChainedTask<RequestT, DependencyRequestT, DependencyResponseT,
    AggregatorResponseT, ResponseT>
    extends BaseTask<RequestT, AggregatorResponseT, ResponseT> {

  protected List<Task<DependencyRequestT, DependencyResponseT>> dependencyTasks;

  protected TaskAggregator<DependencyResponseT, AggregatorResponseT> aggregator;

  protected BaseChainedTask(TaskProcessor<AggregatorResponseT, ResponseT> processor,
                            List<Task<DependencyRequestT, DependencyResponseT>> dependencyTasks,
                            TaskAggregator<DependencyResponseT, AggregatorResponseT> aggregator,
                            FailStrategy failStrategy,
                            Scheduler scheduler) {
    super(processor, failStrategy, scheduler);
    Preconditions.checkArgument(CollectionUtils.isNotEmpty(dependencyTasks));
    Preconditions.checkArgument(aggregator != null);
    this.dependencyTasks = dependencyTasks;
    this.aggregator = aggregator;
  }
}
