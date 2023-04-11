package server.api;

import commons.Board;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BoardSavingControllerTest {

    private BoardSavingController controller;
    private TestBoardRepository boardRepository;
    private TestSimpMessageTemplate msgs;

    @BeforeEach
    public void setup() {
        boardRepository = new TestBoardRepository();
        msgs = new TestSimpMessageTemplate();
        controller = new BoardSavingController(boardRepository, msgs);
    }

    @Test
    public void getAllTest() {
        Board board1 = new Board("board1", "1234", "123aa");
        Board board2 = new Board("board2", "5678", "5678iii");
        boardRepository.save(board1);
        boardRepository.save(board2);
        List<Board> boards = new ArrayList<>();
        boards.add(board1);
        boards.add(board2);
        assertEquals(boards, controller.getAll());
    }
    @Test
    public void addNullTest() {
        var result = controller.add(null);
        assertEquals(ResponseEntity.badRequest().build(), result);
    }
    @Test
    public void addTest() {
        Board board = new Board("lkjdsahfa", "flksahfa", "shfdlak");
        controller.add(board);
        assertEquals(board, boardRepository.findAll().get(0));
    }
    @Test
    public void getByNegativeIdTest() {
        long id = -1;
        assertEquals(ResponseEntity.badRequest().build(), controller.getById(id));
    }
    @Test
    public void getByIdTest() {
        Board board = new Board("aaa", "123", "123");
        boardRepository.save(board);
        long id = board.getBoardId();
        assertEquals(ResponseEntity.ok(board), controller.getById(id));
    }

    @Test
    public void deleteBoardTest() {
        Board board = new Board();
        board = boardRepository.save(board);
        controller.delete(board.getBoardId());
        assertEquals(0, boardRepository.findAll().size());
    }
}
