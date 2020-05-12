package io.github.bhuwanupadhyay.inventory;

public class RetryableException extends RuntimeException {

  public RetryableException(Throwable throwable) {
    super(throwable);
  }
}
