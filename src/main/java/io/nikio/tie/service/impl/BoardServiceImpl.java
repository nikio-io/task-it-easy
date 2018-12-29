package io.nikio.tie.service.impl;

import io.nikio.tie.service.BoardService;
import io.nikio.tie.domain.Board;
import io.nikio.tie.repository.BoardRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing Board.
 */
@Service
@Transactional
public class BoardServiceImpl implements BoardService {

    private final Logger log = LoggerFactory.getLogger(BoardServiceImpl.class);

    private final BoardRepository boardRepository;

    public BoardServiceImpl(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
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
        return boardRepository.save(board);
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
    }
}
