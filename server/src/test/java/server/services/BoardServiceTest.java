package server.services;

import commons.Board;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import server.database.BoardRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
class BoardServiceTest {

    BoardService boardService;
    BoardRepository repo;
    @Mock
    SimpMessageSendingOperations msgs;

    Board board;

    @BeforeEach
    public void setup()
    {
        repo = mock(BoardRepository.class);
        boardService = new BoardService(repo,msgs);
        board = new Board("Hello", "I am", "");

    }

    @Test
    void getAll() {
        Board boardB = new Board("", "","dqwo");
        Board boardC = new Board("A", "A","nw");

        List<Board> list = new ArrayList<>();
        list.add(board);
        list.add(boardB);
        list.add(boardC);

        when(repo.findAll()).thenReturn(list);
        List result = boardService.getAll();
        verify(repo).findAll();

        assertEquals(result, list);

    }

    @Test
    void add() {
        when(repo.save(board)).thenReturn(board);

        Board save = boardService.add(board).getBody();

        verify(msgs).convertAndSend("/topic/boards", board);
        verify(repo).save(board);

        assertEquals(save, board);
    }

    @Test
    void getById() {
        when(repo.existsById(1L)).thenReturn(true);
        when(repo.findById(1L)).thenReturn(Optional.of(board));

        Board bbb = boardService.getById(1L).getBody();
        verify(repo).existsById(1L);
        verify(repo).findById(1L);

        assertEquals(bbb, board);
    }

    @Test
    void delete() {
        when(repo.findById(1L)).thenReturn(Optional.of(board));
        when(repo.existsById(1L)).thenReturn(true);
        doNothing().when(repo).deleteById(1L);

        ResponseEntity<Board> response = boardService.delete(1L);
        verify(msgs).convertAndSend("/topic/boards", board);
        verify(repo).existsById(1L);
        verify(repo).findById(1L);
        verify(repo).deleteById(1L);

        assertEquals(ResponseEntity.ok().build(), response);
    }

    @Test
    void getByIdInvalid()
    {
        when(repo.existsById(1L)).thenReturn(false);

        ResponseEntity<Board> bbb = boardService.getById(1L);
        verify(repo).existsById(1L);
        verify(repo,never()).findById(1L);

        assertEquals(bbb, ResponseEntity.badRequest().build());
    }

    @Test
    void deleteInvalid()
    {

        when(repo.existsById(1L)).thenReturn(false);
        ResponseEntity<Board> bbb = boardService.delete(1L);
        verify(repo).existsById(1L);
        verify(repo,never()).findById(1L);
        verify(repo,never()).deleteById(1L);
        verify(msgs, never()).convertAndSend("/topic/boards", board);

        assertEquals(bbb, ResponseEntity.notFound().build());
    }


}