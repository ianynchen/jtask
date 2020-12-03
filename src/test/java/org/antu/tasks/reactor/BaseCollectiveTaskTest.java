package org.antu.tasks.reactor;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;

import io.vavr.control.Try;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Executors;
import org.junit.Test;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

public class BaseCollectiveTaskTest {

  public static class StringLengthTask extends IndependentTask<String, Integer> {

    public StringLengthTask() {
      super((request) -> {
        Thread.sleep(100);
        System.out.println("time: " + System.currentTimeMillis() + ", Thread: " + Thread.currentThread().getName());
        return request.orElse("").length();
      }, FailStrategy.FAIL_ALL);
    }
  }

  public static class StringLengthSumTask extends BaseCollectiveTask<String, Integer, Integer, Integer> {

    public StringLengthSumTask() {
      super((request) -> request.orElse(0),
          new StringLengthTask(),
          (requests) -> {
            int sum = 0;
            for (Try<Integer> response: requests) {
              if (response.isSuccess()) {
                sum += response.get();
              }
            }
            return Try.success(sum);
          },
          FailStrategy.IGNORE,
          Schedulers.fromExecutorService(Executors.newFixedThreadPool(3)));
    }
  }

  @Test
  public void shouldSucceed() {
    StringLengthSumTask task = new StringLengthSumTask();
    StepVerifier.create(task.execute(Arrays.asList("abc", "d", "ef")))
        .assertNext(response -> {
          assertThat(response.isSuccess(), equalTo(true));
          assertThat(response.get(), equalTo(6));
        }).verifyComplete();
  }

  @Test
  public void shouldSucceed2() {
    StringLengthSumTask task = new StringLengthSumTask();
    StepVerifier.create(task.execute(Arrays.asList("abc", "d", "ef", "ghi", "jkl", "opq")))
        .assertNext(response -> {
          assertThat(response.isSuccess(), equalTo(true));
          assertThat(response.get(), equalTo(15));
        }).verifyComplete();
  }

  @Test
  public void shouldSucceed3() {
    StringLengthSumTask task = new StringLengthSumTask();
    StepVerifier.create(task.execute(Arrays.asList(null, "abc", "d", "ef", "ghi", "jkl", "opq")))
        .assertNext(response -> {
          assertThat(response.isSuccess(), equalTo(true));
          assertThat(response.get(), equalTo(15));
        }).verifyComplete();
  }

  @Test
  public void emptyInputShouldThrowIllegalArgument() {
    StringLengthSumTask task = new StringLengthSumTask();
    StepVerifier.create(task.execute(new ArrayList<>()))
        .assertNext(response -> {
          assertThat(response.isSuccess(), equalTo(false));
          assertThat(response.getCause(), instanceOf(IllegalArgumentException.class));
        }).verifyComplete();
  }
}
