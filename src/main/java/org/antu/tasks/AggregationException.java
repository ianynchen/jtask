package org.antu.tasks;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AggregationException extends RuntimeException {

  private List<Throwable> previousExceptions;

  public AggregationException(Throwable t) {
    super(t);
  }

  public AggregationException(String message, Throwable t) {
    super(message, t);
  }
}
