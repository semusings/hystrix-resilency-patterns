package io.github.bhuwanupadhyay.inventory;

import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import io.github.bhuwanupadhyay.AppProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.matching.RequestPatternBuilder.newRequestPattern;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWireMock(port = 6766)
class ExternalInventoryServiceTest {

  @Autowired private ExternalInventoryService service;
  @Autowired private AppProperties appProperties;

  @BeforeEach
  void setUp() {}

  @Test
  void testAllRetryFailed() {

    // given
    String itemCode = "ITM00001";
    UrlPattern urlPattern = urlEqualTo("/api/v1/inventories/" + itemCode);
    stubFor(
        get(urlPattern)
            .willReturn(
                notFound().withHeader("Content-Type", "application/json").withBody("Not found.")));

    // when
    IllegalStateException okResponseException =
        assertThrows(
            IllegalStateException.class,
            () -> {
              this.service.fetchItem(itemCode);
            });

    // then
    verify(5, newRequestPattern(RequestMethod.GET, urlPattern));
    assertEquals(
        "All retried not successful, operation aborted due to failure of external api.",
        okResponseException.getMessage());
  }

  @Test
  void testSuccessOnFetchingItemPrice() {
    // given
    String itemCode = "ITM00001";
    stubFor(
        get(urlEqualTo("/api/v1/inventories/" + itemCode))
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBodyFile("item-price.json")));

    // when
    ItemPrice itemPrice = this.service.fetchItem(itemCode);

    // then
    assertEquals("USD 10.00", itemPrice.getPrice());
    assertEquals(Double.valueOf(0.0), itemPrice.getDiscountRate());
  }
}
