package org.antu.tasks.reactor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;

import org.antu.tasks.Task;
import org.junit.Test;
import reactor.core.scheduler.Schedulers;

public class TaskFactoryTest {

  @Test
  public void shouldProduceIndependentTasks() {
    assertThat(TaskFactory.<String, Integer>independentTask(str -> str.get().length()), notNullValue());
    assertThat(TaskFactory.<String, Integer>independentTask(str -> str.get().length(), Task.FailStrategy.IGNORE), notNullValue());
    assertThat(TaskFactory.<String, Integer>independentTask(str -> str.get().length(), Schedulers.boundedElastic()), notNullValue());
    assertThat(TaskFactory.<String, Integer>independentTask(str -> str.get().length(), Task.FailStrategy.IGNORE, Schedulers.boundedElastic()), notNullValue());
  }

  @Test
  public void shouldProduceChainedTasks() {

  }

  @Test
  public void shouldProduceParallelTasks() {

  }
}