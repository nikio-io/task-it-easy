package io.nikio.tie.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of ItemSearchRepository to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class ItemSearchRepositoryMockConfiguration {

    @MockBean
    private ItemSearchRepository mockItemSearchRepository;

}
