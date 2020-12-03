package org.antu.tasks.reactor;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;

import io.vavr.control.Try;
import java.util.concurrent.Executors;
import org.junit.Test;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

public class IndependentTaskTest {

  public static class SumTask extends IndependentTask<Integer, Integer> {

    public SumTask() {
      super(request -> {
        Thread.sleep(100);
        System.out.println("Time: " + System.currentTimeMillis() + " Thread: " + Thread.currentThread().getName());
        return request.get() + 2;
      }, Schedulers.fromExecutorService(Executors.newFixedThreadPool(3)));
    }
  }

  public static class DivideTask extends IndependentTask<Double, Double> {

    public DivideTask() {
      super(number -> {
        Thread.sleep(100);
        System.out.println("Time: " + System.currentTimeMillis() + " Thread: " + Thread.currentThread().getName());
        throw new RuntimeException();
      }, Schedulers.fromExecutorService(Executors.newFixedThreadPool(3)));
    }
  }

  @Test
  public void testSuccess() {
    SumTask task = new SumTask();
    StepVerifier.create(task.execute(10))
        .assertNext(result -> {
          assertThat(result.isSuccess(), equalTo(true));
          assertThat(result.get(), equalTo(12));
        }).verifyComplete();
  }

  @Test
  public void testFailure() {
    SumTask task = new SumTask();
    StepVerifier.create(task.execute(Try.failure(new RuntimeException())))
        .assertNext(result -> {
          assertThat(result.isSuccess(), equalTo(false));
          assertThat(result.getCause(), instanceOf(RuntimeException.class));
        }).verifyComplete();
  }

  @Test
  public void testInternalFailure() {
    DivideTask task = new DivideTask();
    StepVerifier.create(task.execute(10.0))
        .assertNext(result -> {
          assertThat(result.isSuccess(), equalTo(false));
          assertThat(result.getCause(), instanceOf(RuntimeException.class));
        }).verifyComplete();
  }
}
