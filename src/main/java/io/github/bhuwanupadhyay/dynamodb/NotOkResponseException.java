package io.github.bhuwanupadhyay.dynamodb;

public class NotOkResponseException extends RuntimeException {

  public NotOkResponseException(String message) {
    super(message);
  }
}
