package org.antu.tasks.reactor;

import java.util.Optional;
import org.antu.tasks.Task;
import org.antu.tasks.TaskProcessor;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

/**
 * A base class for all kinds of {@link Task} using project reactor.
 * @param <RequestT> Input type into {@link Task}
 * @param <IntermediateT> Input type before entering {@link TaskProcessor}
 * @param <ResponseT> Output type from {@link Task}
 *
 * @author ianynchen
 */
public abstract class AbstractTask<RequestT, IntermediateT, ResponseT> implements Task<RequestT, ResponseT> {

  protected FailStrategy failStrategy;

  protected Optional<Scheduler> scheduler;

  protected TaskProcessor<IntermediateT, ResponseT> processor;

  protected AbstractTask(TaskProcessor<IntermediateT, ResponseT> processor,
                         FailStrategy failStrategy) {
    this(processor, failStrategy, null);
  }

  protected AbstractTask(TaskProcessor<IntermediateT, ResponseT> processor,
                         Scheduler scheduler) {
    this(processor, Task.FailStrategy.IGNORE, scheduler);
  }

  protected AbstractTask(TaskProcessor<IntermediateT, ResponseT> processor,
                         FailStrategy failStrategy,
                         Scheduler scheduler) {
    this.failStrategy = failStrategy == null ? FailStrategy.IGNORE : failStrategy;
    this.scheduler = Optional.ofNullable(scheduler);
    this.processor = processor;
  }

  protected Scheduler scheduler() {
    return scheduler.orElse(Schedulers.immediate());
  }

  @Override
  public FailStrategy getFailStrategy() {
    return this.failStrategy;
  }
}
