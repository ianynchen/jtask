package org.antu.tasks;

import io.vavr.Tuple2;
import io.vavr.control.Try;
import java.util.List;
import java.util.stream.Collectors;
import org.antu.tasks.reactor.BaseParallelTask;

/**
 * A {@link TaskAggregator} is responsible for aggregating the return values from a set of {@link BaseParallelTask}s
 * @param <TaskResponseT> output type from {@linke Task}
 * @param <AggregatorResponseT> output type from {@link TaskAggregator}
 */
@FunctionalInterface
public interface TaskAggregator<TaskResponseT, AggregatorResponseT> {

  /**
   * Aggregates exceptions from previous tasks. Throws exception if all tasks return error or if one of
   * the tasks with TERMINATE fail strategy returns error. Throws an {@link AggregationException} with an
   * embedded exception of the first exception of tasks with TERMINATE fail strategy, or the first task
   * with an exception.
   * @param requesterRequestPairs
   * @return
   */
  default Try<AggregatorResponseT> aggregate(List<Tuple2<Task.FailStrategy, Try<TaskResponseT>>> requesterRequestPairs) {

    boolean hasTerminalErrors = false;
    int errorCount = 0;
    Throwable t = null;

    for (Tuple2<Task.FailStrategy, Try<TaskResponseT>> tuple: requesterRequestPairs) {
      if (tuple._2().isFailure()) {
        if (t == null) {
          t = tuple._2().getCause();
        }
        // count number of total failures
        errorCount++;
        // if dependency task is set to FAIL_ALL, terminate condition satisfies
        if (tuple._1() == Task.FailStrategy.TERMINATE) {
          if (hasTerminalErrors == false) {
            t = tuple._2().getCause();
          }
          hasTerminalErrors = true;
        }
      }
    }
    // if all dependency tasks failed, or at least 1 FAIL_ALL task failed
    // fail with exceptions recorded.
    if (errorCount == requesterRequestPairs.size() || hasTerminalErrors) {
      // Need to include the exceptions
      return Try.failure(new AggregationException(t));
    }
    // otherwise, aggregate responses from dependency tasks.
    return process(requesterRequestPairs.stream()
        .map(pair -> pair._2()).collect(Collectors.toList()));
  }

  /**
   * If all dependencies response satisfy requirements, aggregate final response from
   * dependency responses.
   *
   * @param requests
   * @return
   */
  Try<AggregatorResponseT> process(List<Try<TaskResponseT>> requests);
}
