package io.github.bhuwanupadhyay.customer;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import io.github.bhuwanupadhyay.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExternalCustomerService {

  private final RestTemplate restTemplate;
  private final AppProperties appProperties;

  @HystrixCommand(
      commandKey = "fetchCustomerCommand",
      ignoreExceptions = {HttpClientErrorException.class})
  public Customer fetchCustomer(String customerId) {
    return invokeApi(customerId).getBody();
  }

  private ResponseEntity<Customer> invokeApi(String customerId) {
    URI url = URI.create(appProperties.getCustomerBaseUrl() + "/api/v1/customers/" + customerId);

    // setting up the HTTP Basic Authentication header value
    String authorizationHeader = "Basic <auth_token>";

    HttpHeaders requestHeaders = new HttpHeaders();
    // set up HTTP Basic Authentication Header
    requestHeaders.add("Authorization", authorizationHeader);
    requestHeaders.add("Accept", MediaType.APPLICATION_JSON_VALUE);

    // request entity is created with request headers
    HttpEntity<Object> requestEntity = new HttpEntity<>(requestHeaders);

    return restTemplate.exchange(url, HttpMethod.GET, requestEntity, Customer.class);
  }
}
