package org.antu.tasks.reactor;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import io.vavr.control.Try;
import java.util.Arrays;
import java.util.concurrent.Executors;
import org.junit.Test;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

public class SimpleCollectiveTaskTest {

  public static class StringLengthTask extends IndependentTask<String, Integer> {

    public StringLengthTask() {
      super(str -> str.orElse("").length(), Schedulers.fromExecutorService(Executors.newFixedThreadPool(3)));
    }
  }

  public static class LetterCountTask extends SimpleCollectiveTask<String, Integer, Integer> {

    public LetterCountTask() {
      super(new StringLengthTask(), requests -> Try.success(requests.stream()
          .filter(t -> t.isSuccess()).mapToInt(t -> t.get()).sum()));
    }
  }

  @Test
  public void testSuccess() {
    LetterCountTask task = new LetterCountTask();
    StepVerifier.create(task.execute(Arrays.asList("a", "bc", "def", "ghij")))
        .assertNext(value -> {
          assertThat(value.isSuccess(), equalTo(true));
          assertThat(value.get(), equalTo(10));
        }).verifyComplete();
  }
}
