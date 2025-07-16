package org.jono.medicalmodelsservice.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManager;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.ClientTlsStrategyBuilder;
import org.apache.hc.core5.function.Factory;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.nio.ssl.TlsStrategy;
import org.apache.hc.core5.reactor.ssl.TlsDetails;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.OpenSearchTransport;
import org.opensearch.client.transport.httpclient5.ApacheHttpClient5TransportBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Configuration
public class OpenSearchClientConfig {

  @Value("${opensearch.username}")
  private String username;

  @Value("${opensearch.password}")
  private String password;

  @Bean
  public OpenSearchClient openSearchClient() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
    log.info("----- JavaClientService script starting -----");
    System.setProperty("javax.net.ssl.trustStore", "/full/path/to/keystore");
    System.setProperty("javax.net.ssl.trustStorePassword", "password-to-keystore");
    log.info("----- Properties set -----");

    final HttpHost host = new HttpHost("https", "localhost", 9200);
    final BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    log.info("----- Host: {} -----", host.toHostString());
    // Only for demo purposes. Don't specify your credentials in code.
    credentialsProvider.setCredentials(new AuthScope(host), new UsernamePasswordCredentials(username, password.toCharArray()));

    final SSLContext sslcontext = SSLContextBuilder
        .create()
        .loadTrustMaterial(null, (chains, authType) -> true)
        .build();

    final ApacheHttpClient5TransportBuilder builder = ApacheHttpClient5TransportBuilder.builder(host);
    builder.setHttpClientConfigCallback(httpClientBuilder -> {
      final TlsStrategy tlsStrategy = ClientTlsStrategyBuilder.create()
          .setSslContext(sslcontext)
          // See https://issues.apache.org/jira/browse/HTTPCLIENT-2219
          .setTlsDetailsFactory(new Factory<SSLEngine, TlsDetails>() {
            @Override
            public TlsDetails create(final SSLEngine sslEngine) {
              return new TlsDetails(sslEngine.getSession(), sslEngine.getApplicationProtocol());
            }
          })
          .build();

      final PoolingAsyncClientConnectionManager connectionManager = PoolingAsyncClientConnectionManagerBuilder
          .create()
          .setTlsStrategy(tlsStrategy)
          .build();

      return httpClientBuilder
          .setDefaultCredentialsProvider(credentialsProvider)
          .setConnectionManager(connectionManager);
    });

    final OpenSearchTransport transport = builder.build();
    return new OpenSearchClient(transport);
  }
}
