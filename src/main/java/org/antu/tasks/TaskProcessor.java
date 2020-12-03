package org.antu.tasks;

import java.util.Optional;

/**
 * Core processing logic for a {@link Task}.
 * @param <RequestT> Input type to {@link Task}
 * @param <ResponseT> Output type from {@link Task}
 */
@FunctionalInterface
public interface TaskProcessor<RequestT, ResponseT> {

  /**
   * Takes an input, and produces an output.
   *
   * @param request
   * @return
   * @throws Throwable
   */
  ResponseT process(Optional<RequestT> request) throws Throwable;
}
