package org.jono.medicalmodelsservice;

import lombok.extern.slf4j.Slf4j;
import org.jono.medicalmodelsservice.config.OpenSearchClientConfig;
import org.jono.medicalmodelsservice.model.IndexData;
import org.jono.medicalmodelsservice.service.OpenSearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.IndexRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.indices.CreateIndexRequest;
import org.opensearch.client.opensearch.indices.DeleteIndexRequest;
import org.opensearch.client.opensearch.indices.DeleteIndexResponse;
import org.opensearch.client.opensearch.indices.IndexSettings;
import org.opensearch.client.opensearch.indices.PutIndicesSettingsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@Slf4j
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@ComponentScan(basePackages = "org.jono.medicalmodelsservice")
class MedicalModelsServiceApplicationTests {

  @Autowired
  private OpenSearchService openSearchService;

  @Autowired
  private OpenSearchClientConfig openSearchClientConfig;

  private static OpenSearchClient client;

  @BeforeEach
  void beforeEach() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
    client = this.openSearchClientConfig.openSearchClient();
  }

  @Test
  void secondTest() throws NoSuchAlgorithmException, KeyStoreException, IOException, KeyManagementException {
    openSearchService.runOpenSearchStuff();
  }

  @Test
  void thirdTest() throws IOException {
    // ------ Creating an Index --------
    log.info("----- Creating an index -----");
    final String index = "sample-index3";
    final CreateIndexRequest createIndexRequest = new CreateIndexRequest.Builder().index(index).build();
    client.indices().create(createIndexRequest);

    final IndexSettings indexSettings = new IndexSettings.Builder().autoExpandReplicas("0-all").build();
    final PutIndicesSettingsRequest putIndicesSettingsRequest = new PutIndicesSettingsRequest.Builder()
        .index(index)
        .settings(indexSettings)
        .build();
    client.indices().putSettings(putIndicesSettingsRequest);

    // ----- Indexing Data ----
    log.info("----- Indexing Data -----");
    final IndexData indexData = new IndexData("first_name", "Bruce");
    final IndexRequest<IndexData> indexRequest = new IndexRequest.Builder<IndexData>().index(index).id("1").document(indexData).build();
    client.index(indexRequest);

    // Search for the document
    log.info("----- Searching for a document -----");
    final SearchResponse<IndexData> searchResponse = client.search(s -> s.index(index), IndexData.class);
    for (int i = 0; i < searchResponse.hits().hits().size(); i++) {
      System.out.println(searchResponse.hits().hits().get(i).source());
    }

    // Delete the document
    log.info("----- Deleting the document -----");
    client.delete(b -> b.index(index).id("1"));

    // Delete the index
    log.info("----- Deleting the index -----");
    final DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest.Builder().index(index).build();
    final DeleteIndexResponse deleteIndexResponse = client.indices().delete(deleteIndexRequest);
    log.info(String.valueOf(deleteIndexResponse.acknowledged()));
  }
}
