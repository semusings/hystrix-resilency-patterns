package io.github.bhuwanupadhyay.dynamodb;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWireMock(port = 0)
class ExternalInventoryServiceTest {

  @Autowired private ExternalInventoryService service;

  @BeforeEach
  void setUp() {}

  @Test
  void testAllRetryFailed() {
    NotOkResponseException okResponseException =
        assertThrows(
            NotOkResponseException.class,
            () -> {
              this.service.fetchItem("ITM#00001");
            });

    assertEquals(
        "All retried not successful, operation aborted due to failure of external api.",
        okResponseException.getMessage());
  }

  @Test
  void testSuccessOnFetchingItemPrice() {
    // Stubbing WireMock
    stubFor(
        get(urlEqualTo("http://localhost:8084/api/v1/inventories/.+"))
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBodyFile("item-price.json")));
    ItemPrice itemPrice = this.service.fetchItem("ITM#00001");
    assertEquals("USD 10.00", itemPrice.getPrice());
    assertEquals(Double.valueOf(0.0), itemPrice.getDiscountRate());
  }
}
