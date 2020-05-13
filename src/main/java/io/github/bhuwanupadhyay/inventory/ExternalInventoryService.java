package io.github.bhuwanupadhyay.inventory;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import io.github.bhuwanupadhyay.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExternalInventoryService {

  private final RestTemplate restTemplate;
  private final AppProperties appProperties;

  @HystrixCommand(
      commandKey = "fetchItemCommand",
      ignoreExceptions = {RetryableException.class})
  @Retryable(
      value = {RetryableException.class, HystrixRuntimeException.class},
      maxAttemptsExpression = "${fetchItemCommand.retry.maxAttempts}",
      backoff =
          @Backoff(
              delayExpression = "${fetchItemCommand.retry.backoff.delayInMilliseconds}",
              multiplierExpression = "${fetchItemCommand.retry.backoff.multiplier}"))
  public ItemPrice fetchItem(String itemCode) {
    try {
      return invokeApi(itemCode).getBody();
    } catch (RestClientException e) {
      /**
       * {@link RestClientException} is a base class for exceptions thrown by {@link RestTemplate}
       * in case a request fails because of a server error response.
       *
       * <p>On this case only we generates custom retryable exception for retry.
       */
      throw new RetryableException(e);
    }
  }

  private ResponseEntity<ItemPrice> invokeApi(String itemCode) {
    URI url = URI.create(appProperties.getInventoryBaseUrl() + "/api/v1/inventories/" + itemCode);

    // setting up the HTTP Basic Authentication header value
    String authorizationHeader = "Basic <auth_token>";

    HttpHeaders requestHeaders = new HttpHeaders();
    // set up HTTP Basic Authentication Header
    requestHeaders.add("Authorization", authorizationHeader);
    requestHeaders.add("Accept", MediaType.APPLICATION_JSON_VALUE);

    // request entity is created with request headers
    HttpEntity<Object> requestEntity = new HttpEntity<>(requestHeaders);

    return restTemplate.exchange(url, HttpMethod.GET, requestEntity, ItemPrice.class);
  }
}
