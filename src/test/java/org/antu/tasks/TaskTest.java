package org.antu.tasks;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;

import io.vavr.control.Try;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import org.antu.tasks.reactor.BaseParallelTask;
import org.antu.tasks.reactor.IndependentTask;
import org.junit.Test;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

public class TaskTest {

  public static class StringLengthTask extends IndependentTask<String, Integer> {

    public StringLengthTask() {
      super(request -> {
        Thread.sleep(100);
        System.out.println("time: " + System.currentTimeMillis() + ", Thread: " + Thread.currentThread().getName());
        return request.orElse("").length();
      }, Schedulers.fromExecutorService(Executors.newFixedThreadPool(5)));
    }
  }

  public static class StringLengthTask1 extends IndependentTask<String, Integer> {

    public StringLengthTask1() {
      super(request -> {
        Thread.sleep(100);
        System.out.println("time: " + System.currentTimeMillis() + ", Thread: " + Thread.currentThread().getName());
        return 5;
      }, Schedulers.fromExecutorService(Executors.newFixedThreadPool(5)));
    }
  }

  public static class StringLengthTask2 extends IndependentTask<String, Integer> {

    public StringLengthTask2() {
      super(request -> {
        Thread.sleep(100);
        System.out.println("time: " + System.currentTimeMillis() + ", Thread: " + Thread.currentThread().getName());
        return 5;
      }, Schedulers.fromExecutorService(Executors.newFixedThreadPool(5)));
    }
  }

  public static class SumTask extends BaseParallelTask<String, Integer, Integer, Integer> {

    public SumTask(List<Task<String, Integer>> dependencyTasks,
                   TaskAggregator<Integer, Integer> aggregator,
                   FailStrategy failStrategy,
                   Scheduler scheduler) {
      super(request -> request.get(), dependencyTasks, aggregator, failStrategy, scheduler);
    }
  }

  @Test
  public void testSuccess() {
    SumTask task = new SumTask(Arrays.asList(new StringLengthTask1(), new StringLengthTask2()),
        (requests) -> {
          int sum = 0;
          for (Try<Integer> val: requests) {
            if (val.isSuccess()) {
              sum += val.get();
            } else {
              return Try.failure(new IllegalArgumentException());
            }
          }
          return Try.success(sum);
        },
        BaseParallelTask.FailStrategy.IGNORE,
        Schedulers.immediate());
    StepVerifier.create(task.execute("abc"))
        .assertNext(result -> {
          assertThat(result.isSuccess(), equalTo(true));
          assertThat(result.get(), equalTo(10));
        }).verifyComplete();
  }

  @Test
  public void testFail() {
    SumTask task = new SumTask(Arrays.asList(new StringLengthTask()),
        (requests) -> {
          int sum = 0;
          for (Try<Integer> val: requests) {
            if (val.isSuccess()) {
              sum += val.get();
            } else {
              return Try.failure(new IllegalArgumentException());
            }
          }
          return Try.success(sum);
        },
        BaseParallelTask.FailStrategy.IGNORE,
        Schedulers.immediate());
    StepVerifier.create(task.execute(Try.success(null)))
        .assertNext(result -> {
          assertThat(result.isSuccess(), equalTo(false));
          assertThat(result.getCause(), instanceOf(NullPointerException.class));
        }).verifyComplete();
  }
}
