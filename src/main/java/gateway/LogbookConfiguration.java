package gateway;

import org.springframework.cloud.gateway.config.HttpClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.netty.LogbookClientHandler;
import reactor.netty.http.client.HttpClient;

/**
 * Configures beans required for plain-text wiretapping incoming and outgoing HTTP requests and
 * responses.
 */
@Configuration
public class LogbookConfiguration {
  /**
   * Configures the global {@link HttpClient} so outgoing requests and incoming responses are logged
   * by {@link Logbook}.
   */
  @Bean
  public HttpClientCustomizer logbookClientCustomizer(Logbook logbook) {
    return httpClient -> httpClient
        .doOnConnected(conn -> conn.addHandlerLast(new LogbookClientHandler(logbook)));
  }
}
