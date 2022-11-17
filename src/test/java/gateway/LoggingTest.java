package gateway;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.zalando.logbook.Logbook;

final class LoggingTest {
  @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
      properties = {"httpbin=http://localhost:${wiremock.server.port}"})
  @AutoConfigureWireMock(port = 0)
  static class BaseLoggingTest {

    @Autowired
    private WebTestClient webClient;

    @Test
    void contextLoads() throws Exception {
      // get Logback Logger
      Logger logbookLogger = (Logger) LoggerFactory.getLogger(Logbook.class);

      // create and start a ListAppender
      ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
      listAppender.start();

      // add the appender to the logger
      logbookLogger.addAppender(listAppender);

      stubFor(get(urlEqualTo("/get"))
          .willReturn(aResponse()
              .withBody("{\"headers\":{\"Hello\":\"World\"}}")
              .withHeader("Content-Type", "application/json")));

      webClient
          .get().uri("/get")
          .exchange()
          .expectStatus().isOk()
          .expectBody()
          .jsonPath("$.headers.Hello").isEqualTo("World");

      // wait until logs are written
      Awaitility.await()
          .untilAsserted(() -> assertThat(listAppender.list).hasSize(4));

      assertThat(listAppender.list)
          .allSatisfy(logEvent -> {
            assertThat(logEvent.getMDCPropertyMap()).containsKeys("spanId", "traceId");
          });
    }
  }

  @Nested
  class WithoutFix extends BaseLoggingTest {

  }

  @Nested
  @ActiveProfiles("fixTracing")
  class WithFix extends BaseLoggingTest {

  }
}
