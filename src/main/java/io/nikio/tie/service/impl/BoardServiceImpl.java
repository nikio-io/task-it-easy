package io.nikio.tie.service.impl;

import io.nikio.tie.service.BoardService;
import io.nikio.tie.domain.Board;
import io.nikio.tie.repository.BoardRepository;
import io.nikio.tie.repository.search.BoardSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Board.
 */
@Service
@Transactional
public class BoardServiceImpl implements BoardService {

    private final Logger log = LoggerFactory.getLogger(BoardServiceImpl.class);

    private final BoardRepository boardRepository;

    private final BoardSearchRepository boardSearchRepository;

    public BoardServiceImpl(BoardRepository boardRepository, BoardSearchRepository boardSearchRepository) {
        this.boardRepository = boardRepository;
        this.boardSearchRepository = boardSearchRepository;
    }

    /**
     * Save a board.
     *
     * @param board the entity to save
     * @return the persisted entity
     */
    @Override
    public Board save(Board board) {
        log.debug("Request to save Board : {}", board);
        Board result = boardRepository.save(board);
        boardSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the boards.
     *
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<Board> findAll() {
        log.debug("Request to get all Boards");
        return boardRepository.findAll();
    }


    /**
     * Get one board by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Board> findOne(Long id) {
        log.debug("Request to get Board : {}", id);
        return boardRepository.findById(id);
    }

    /**
     * Delete the board by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Board : {}", id);
        boardRepository.deleteById(id);
        boardSearchRepository.deleteById(id);
    }

    /**
     * Search for the board corresponding to the query.
     *
     * @param query the query of the search
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<Board> search(String query) {
        log.debug("Request to search Boards for query {}", query);
        return StreamSupport
            .stream(boardSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
