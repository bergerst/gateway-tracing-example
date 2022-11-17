package gateway;

import brave.http.HttpTracing;
import org.springframework.boot.web.embedded.netty.NettyServerCustomizer;
import org.springframework.cloud.gateway.config.HttpClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import reactor.netty.http.brave.ReactorNettyHttpTracing;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.server.HttpServer;

@Configuration
@Profile("fixTracing")
public class FixTracingConfiguration {
  /**
   * Creates a tracing object for adding spanIds and traceIds to {@link HttpClient} and
   * {@link HttpServer} logs.
   */
  @Bean
  public ReactorNettyHttpTracing reactorTracing(HttpTracing httpTracing) {
    return ReactorNettyHttpTracing.create(httpTracing);
  }

  /**
   * Without this, traceIds and spanIds are missing in "Incoming Response" logs.
   */
  @Bean
  public HttpClientCustomizer fixTracingClientCustomizer(
      ReactorNettyHttpTracing reactorTracing) {
    return reactorTracing::decorateHttpClient;
  }

  /**
   * Without this, traceIds and spanIds are missing in "Outgoing Response" logs.
   */
  @Bean
  public NettyServerCustomizer fixTracingServerCustomizer(
      ReactorNettyHttpTracing reactorTracing) {
    return reactorTracing::decorateHttpServer;
  }
}
