package io.nikio.tie.repository.search;

import io.nikio.tie.domain.Board;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Board entity.
 */
public interface BoardSearchRepository extends ElasticsearchRepository<Board, Long> {
}
