package io.nikio.tie.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.nikio.tie.domain.Board;
import io.nikio.tie.service.BoardService;
import io.nikio.tie.web.rest.errors.BadRequestAlertException;
import io.nikio.tie.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Board.
 */
@RestController
@RequestMapping("/api")
public class BoardResource {

    private final Logger log = LoggerFactory.getLogger(BoardResource.class);

    private static final String ENTITY_NAME = "board";

    private final BoardService boardService;

    public BoardResource(BoardService boardService) {
        this.boardService = boardService;
    }

    /**
     * POST  /boards : Create a new board.
     *
     * @param board the board to create
     * @return the ResponseEntity with status 201 (Created) and with body the new board, or with status 400 (Bad Request) if the board has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/boards")
    @Timed
    public ResponseEntity<Board> createBoard(@RequestBody Board board) throws URISyntaxException {
        log.debug("REST request to save Board : {}", board);
        if (board.getId() != null) {
            throw new BadRequestAlertException("A new board cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Board result = boardService.save(board);
        return ResponseEntity.created(new URI("/api/boards/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /boards : Updates an existing board.
     *
     * @param board the board to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated board,
     * or with status 400 (Bad Request) if the board is not valid,
     * or with status 500 (Internal Server Error) if the board couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/boards")
    @Timed
    public ResponseEntity<Board> updateBoard(@RequestBody Board board) throws URISyntaxException {
        log.debug("REST request to update Board : {}", board);
        if (board.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Board result = boardService.save(board);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, board.getId().toString()))
            .body(result);
    }

    /**
     * GET  /boards : get all the boards.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of boards in body
     */
    @GetMapping("/boards")
    @Timed
    public List<Board> getAllBoards() {
        log.debug("REST request to get all Boards");
        return boardService.findAll();
    }

    /**
     * GET  /boards/:id : get the "id" board.
     *
     * @param id the id of the board to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the board, or with status 404 (Not Found)
     */
    @GetMapping("/boards/{id}")
    @Timed
    public ResponseEntity<Board> getBoard(@PathVariable Long id) {
        log.debug("REST request to get Board : {}", id);
        Optional<Board> board = boardService.findOne(id);
        return ResponseUtil.wrapOrNotFound(board);
    }

    /**
     * DELETE  /boards/:id : delete the "id" board.
     *
     * @param id the id of the board to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/boards/{id}")
    @Timed
    public ResponseEntity<Void> deleteBoard(@PathVariable Long id) {
        log.debug("REST request to delete Board : {}", id);
        boardService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
