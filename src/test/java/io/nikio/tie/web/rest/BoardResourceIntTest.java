package io.nikio.tie.web.rest;

import io.nikio.tie.TieApp;

import io.nikio.tie.domain.Board;
import io.nikio.tie.repository.BoardRepository;
import io.nikio.tie.repository.search.BoardSearchRepository;
import io.nikio.tie.service.BoardService;
import io.nikio.tie.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;


import static io.nikio.tie.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the BoardResource REST controller.
 *
 * @see BoardResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TieApp.class)
public class BoardResourceIntTest {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private BoardService boardService;

    /**
     * This repository is mocked in the io.nikio.tie.repository.search test package.
     *
     * @see io.nikio.tie.repository.search.BoardSearchRepositoryMockConfiguration
     */
    @Autowired
    private BoardSearchRepository mockBoardSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restBoardMockMvc;

    private Board board;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final BoardResource boardResource = new BoardResource(boardService);
        this.restBoardMockMvc = MockMvcBuilders.standaloneSetup(boardResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Board createEntity(EntityManager em) {
        Board board = new Board()
            .title(DEFAULT_TITLE)
            .description(DEFAULT_DESCRIPTION);
        return board;
    }

    @Before
    public void initTest() {
        board = createEntity(em);
    }

    @Test
    @Transactional
    public void createBoard() throws Exception {
        int databaseSizeBeforeCreate = boardRepository.findAll().size();

        // Create the Board
        restBoardMockMvc.perform(post("/api/boards")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(board)))
            .andExpect(status().isCreated());

        // Validate the Board in the database
        List<Board> boardList = boardRepository.findAll();
        assertThat(boardList).hasSize(databaseSizeBeforeCreate + 1);
        Board testBoard = boardList.get(boardList.size() - 1);
        assertThat(testBoard.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testBoard.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);

        // Validate the Board in Elasticsearch
        verify(mockBoardSearchRepository, times(1)).save(testBoard);
    }

    @Test
    @Transactional
    public void createBoardWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = boardRepository.findAll().size();

        // Create the Board with an existing ID
        board.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restBoardMockMvc.perform(post("/api/boards")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(board)))
            .andExpect(status().isBadRequest());

        // Validate the Board in the database
        List<Board> boardList = boardRepository.findAll();
        assertThat(boardList).hasSize(databaseSizeBeforeCreate);

        // Validate the Board in Elasticsearch
        verify(mockBoardSearchRepository, times(0)).save(board);
    }

    @Test
    @Transactional
    public void getAllBoards() throws Exception {
        // Initialize the database
        boardRepository.saveAndFlush(board);

        // Get all the boardList
        restBoardMockMvc.perform(get("/api/boards?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(board.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }
    
    @Test
    @Transactional
    public void getBoard() throws Exception {
        // Initialize the database
        boardRepository.saveAndFlush(board);

        // Get the board
        restBoardMockMvc.perform(get("/api/boards/{id}", board.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(board.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingBoard() throws Exception {
        // Get the board
        restBoardMockMvc.perform(get("/api/boards/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBoard() throws Exception {
        // Initialize the database
        boardService.save(board);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockBoardSearchRepository);

        int databaseSizeBeforeUpdate = boardRepository.findAll().size();

        // Update the board
        Board updatedBoard = boardRepository.findById(board.getId()).get();
        // Disconnect from session so that the updates on updatedBoard are not directly saved in db
        em.detach(updatedBoard);
        updatedBoard
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION);

        restBoardMockMvc.perform(put("/api/boards")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedBoard)))
            .andExpect(status().isOk());

        // Validate the Board in the database
        List<Board> boardList = boardRepository.findAll();
        assertThat(boardList).hasSize(databaseSizeBeforeUpdate);
        Board testBoard = boardList.get(boardList.size() - 1);
        assertThat(testBoard.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testBoard.getDescription()).isEqualTo(UPDATED_DESCRIPTION);

        // Validate the Board in Elasticsearch
        verify(mockBoardSearchRepository, times(1)).save(testBoard);
    }

    @Test
    @Transactional
    public void updateNonExistingBoard() throws Exception {
        int databaseSizeBeforeUpdate = boardRepository.findAll().size();

        // Create the Board

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBoardMockMvc.perform(put("/api/boards")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(board)))
            .andExpect(status().isBadRequest());

        // Validate the Board in the database
        List<Board> boardList = boardRepository.findAll();
        assertThat(boardList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Board in Elasticsearch
        verify(mockBoardSearchRepository, times(0)).save(board);
    }

    @Test
    @Transactional
    public void deleteBoard() throws Exception {
        // Initialize the database
        boardService.save(board);

        int databaseSizeBeforeDelete = boardRepository.findAll().size();

        // Get the board
        restBoardMockMvc.perform(delete("/api/boards/{id}", board.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Board> boardList = boardRepository.findAll();
        assertThat(boardList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Board in Elasticsearch
        verify(mockBoardSearchRepository, times(1)).deleteById(board.getId());
    }

    @Test
    @Transactional
    public void searchBoard() throws Exception {
        // Initialize the database
        boardService.save(board);
        when(mockBoardSearchRepository.search(queryStringQuery("id:" + board.getId())))
            .thenReturn(Collections.singletonList(board));
        // Search the board
        restBoardMockMvc.perform(get("/api/_search/boards?query=id:" + board.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(board.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Board.class);
        Board board1 = new Board();
        board1.setId(1L);
        Board board2 = new Board();
        board2.setId(board1.getId());
        assertThat(board1).isEqualTo(board2);
        board2.setId(2L);
        assertThat(board1).isNotEqualTo(board2);
        board1.setId(null);
        assertThat(board1).isNotEqualTo(board2);
    }
}
