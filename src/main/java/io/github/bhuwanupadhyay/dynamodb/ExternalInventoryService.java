package io.github.bhuwanupadhyay.dynamodb;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExternalInventoryService {

  private final RestTemplate restTemplate;

  private ResponseEntity<ItemPrice> invokeApi(String itemCode) {
    URI url = URI.create("http://localhost:8084/api/v1/inventories/" + itemCode);

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

  @HystrixCommand(fallbackMethod = "firstRetryFetchItem", commandKey = "fetchItemCommand1")
  public ItemPrice fetchItem(String itemCode) {

    ResponseEntity<ItemPrice> responseEntity = invokeApi(itemCode);

    if (responseEntity.getStatusCode() == HttpStatus.OK) {
      log.info("Response received {}.", responseEntity.getBody());
      return responseEntity.getBody();
    } else {
      log.error("No response received, error occurred.");
      throw new NotOkResponseException("First time, External api not responding properly.");
    }
  }

  @HystrixCommand( raiseHystrixExceptions = {}, fallbackMethod = "secondRetryFetchItem", commandKey = "fetchItemCommand2")
  public ItemPrice firstRetryFetchItem(String itemCode) {
    return this.fetchItem(itemCode);
  }

  @HystrixCommand(fallbackMethod = "raiseNotOkResponseException", commandKey = "fetchItemCommand3")
  public ItemPrice secondRetryFetchItem(String itemCode) {
    return this.fetchItem(itemCode);
  }

  public ItemPrice raiseNotOkResponseException(String itemCode) {
    log.error("Not able to recovery until 3 times retry.");
    throw new NotOkResponseException(
        "All retried not successful, operation aborted due to failure of external api.");
  }
}
