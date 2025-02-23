package org.jono.medicalmodelsservice;

import lombok.extern.slf4j.Slf4j;
import org.jono.medicalmodelsservice.config.OpenSearchClientConfig;
import org.jono.medicalmodelsservice.model.IndexData;
import org.jono.medicalmodelsservice.service.OpenSearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.IndexRequest;
import org.opensearch.client.opensearch.core.IndexResponse;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.indices.Alias;
import org.opensearch.client.opensearch.indices.ClearCacheResponse;
import org.opensearch.client.opensearch.indices.CreateIndexRequest;
import org.opensearch.client.opensearch.indices.CreateIndexResponse;
import org.opensearch.client.opensearch.indices.DeleteIndexRequest;
import org.opensearch.client.opensearch.indices.DeleteIndexResponse;
import org.opensearch.client.transport.endpoints.BooleanResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@ComponentScan(basePackages = "org.jono.medicalmodelsservice")
class OpenSearchCreateIndexTest {

    @Autowired
    private OpenSearchService openSearchService;

    @Autowired
    private OpenSearchClientConfig openSearchClientConfig;

    private static OpenSearchClient client;

    private static final String TEST_INDEX_NAME = "sample-index3";

    @BeforeEach
    void beforeEach() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        client = this.openSearchClientConfig.openSearchClient();
        deleteIndexIfPresent(TEST_INDEX_NAME);
    }

    private void deleteIndexIfPresent(String indexName) throws IOException {
        BooleanResponse exists = client.indices().exists(b -> b.index(indexName));
        if (exists.value()) {
            DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest.Builder().index(TEST_INDEX_NAME).build();
            DeleteIndexResponse deleteIndexResponse = client.indices().delete(deleteIndexRequest);
            log.info(String.valueOf(deleteIndexResponse.acknowledged()));
        }
    }

    @Test
    void createIndex() throws IOException {
        CreateIndexRequest createIndexRequest = new CreateIndexRequest.Builder().index(TEST_INDEX_NAME).build();
        CreateIndexResponse createIndexResponse = client.indices().create(createIndexRequest);

        assertThat(createIndexResponse.acknowledged()).isEqualTo(true);
        assertThat(createIndexResponse.index()).isEqualTo(TEST_INDEX_NAME);
        assertThat(createIndexResponse.shardsAcknowledged()).isEqualTo(true);
    }

    @Test
    void createIndexThenClearCache() throws IOException {
        CreateIndexRequest createIndexRequest = new CreateIndexRequest.Builder().index(TEST_INDEX_NAME).build();
        client.indices().create(createIndexRequest);

        ClearCacheResponse clearCacheResponse = client.indices().clearCache();

        assertThat(clearCacheResponse.shards().failed()).isEqualTo(0);
        assertThat(clearCacheResponse.shards().failures()).hasSize(0);
        assertThat(clearCacheResponse.shards().skipped()).isEqualTo(null);
        assertThat(clearCacheResponse.shards().successful()).isEqualTo(7);
        assertThat(clearCacheResponse.shards().total()).isEqualTo(9);
    }

    @Test
    void createIndexThenCheckIfItExists() throws IOException {
        CreateIndexRequest createIndexRequest = new CreateIndexRequest.Builder().index(TEST_INDEX_NAME).build();
        client.indices().create(createIndexRequest);

        BooleanResponse exists = client.indices().exists(b -> b.index(TEST_INDEX_NAME));
        assertThat(exists.value()).isEqualTo(true);
    }

    @Test
    void createIndexWithAlias() throws IOException {
        CreateIndexRequest createIndexRequest = new CreateIndexRequest.Builder()
                .index(TEST_INDEX_NAME)
                .aliases("FirstAliasKey", new Alias.Builder().build())
                .build();

        client.indices().create(createIndexRequest);

        BooleanResponse exists = client.indices().existsAlias(b -> b.name("FirstAliasKey"));
        assertThat(exists.value()).isEqualTo(true);
    }

    @Test
    void createIndexThenAddDocuments() throws IOException {
        CreateIndexRequest createIndexRequest = new CreateIndexRequest.Builder()
                .index(TEST_INDEX_NAME)
                .build();

        client.indices().create(createIndexRequest);

        // ----- Indexing Data ----
        IndexRequest<IndexData> indexRequest1 = createIndexRequest("1", "Bruce", "Buffer");
        IndexResponse indexResponse1 = client.index(indexRequest1);
        assertThat(indexResponse1.id()).isEqualTo("1");
        assertThat(indexResponse1.index()).isEqualTo(TEST_INDEX_NAME);
        assertThat(indexResponse1.seqNo()).isEqualTo(0L);

        IndexRequest<IndexData> indexRequest2 = createIndexRequest("2", "Tim", "Poole");
        IndexResponse indexResponse2 = client.index(indexRequest2);
        assertThat(indexResponse2.id()).isEqualTo("2");
        assertThat(indexResponse2.seqNo()).isEqualTo(1L);
    }

    @Test
    void createIndexWithMappings() throws IOException {
        CreateIndexRequest createIndexRequest = new CreateIndexRequest.Builder()
                .index(TEST_INDEX_NAME)
                .mappings(b -> b) // TODO: Figure out some mappings to add
                .build();

        client.indices().create(createIndexRequest);

        // ----- Indexing Data ----
        IndexRequest<IndexData> indexRequest1 = createIndexRequest("1", "Bruce", "Buffer");
        client.index(indexRequest1);
        IndexRequest<IndexData> indexRequest2 = createIndexRequest("2", "Tim", "Poole");
        client.index(indexRequest2);

        //Search for the document
        SearchResponse<IndexData> searchResponse = client.search(s -> s.index(TEST_INDEX_NAME), IndexData.class);
        for (int i = 0; i< searchResponse.hits().hits().size(); i++) {
            System.out.println(searchResponse.hits().hits().get(i).source());
        }

        //Delete the document
        client.delete(b -> b.index(TEST_INDEX_NAME).id("1"));
    }

    private IndexRequest<IndexData> createIndexRequest(String id, String firstName, String lastName) {
        return new IndexRequest.Builder<IndexData>().index(TEST_INDEX_NAME).id(id).document(new IndexData(firstName, lastName)).build();
    }
}
