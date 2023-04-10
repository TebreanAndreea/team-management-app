package server.services;

import commons.Board;
import commons.ColorScheme;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import server.database.ColorSchemeRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ColorServiceTest {

    ColorService colorService;
    ColorSchemeRepository repo;
    @Mock
    SimpMessageSendingOperations msgs;

    ColorScheme color;

    Board board;

    @BeforeEach
    public void setup()
    {
        repo = mock(ColorSchemeRepository.class);
        msgs = mock(SimpMessageSendingOperations.class);
        colorService = new ColorService(repo,msgs);
        board = new Board("Hello", "I am", "");
        color = new ColorScheme("name", "","",board);

    }

    @Test
    void add() {

        when(repo.save(color)).thenReturn(color);

        ColorScheme save = colorService.add(color).getBody();

        verify(msgs).convertAndSend("/topic/colors", color);
        verify(repo).save(color);

        assertEquals(save, color);
    }

    @Test
    void getBoard() {
        Board result = colorService.getBoard(board).getBody();
        assertEquals(result, board);
    }



    @Test
    void delete() {
        when(repo.findById(1L)).thenReturn(Optional.of(color));
        doNothing().when(repo).deleteById(1L);

        ResponseEntity<ColorScheme> response = colorService.delete(1L);
        verify(msgs).convertAndSend("/topic/colors", color);
        verify(repo).findById(1L);
        verify(repo).deleteById(1L);

        assertEquals(ResponseEntity.ok().build(), response);
    }

    @Test
    void deleteInvalid()
    {

        when(repo.findById(1L)).thenReturn(Optional.empty());
        ResponseEntity<ColorScheme> bbb = colorService.delete(1L);
        verify(repo).findById(1L);
        verify(repo,never()).deleteById(1L);
        verify(msgs, never()).convertAndSend("/topic/colors", color);

        assertEquals(bbb, ResponseEntity.notFound().build());
    }

}