package server.api;

import commons.Board;
import commons.ColorScheme;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ColorSavingControllerTest {
    private ColorSavingController controller;
    private TestColorSchemeRepository colorSchemeRepository;
    private TestSimpMessageTemplate msgs;

    @BeforeEach
    public void setup() {
        colorSchemeRepository = new TestColorSchemeRepository();
        msgs = new TestSimpMessageTemplate();
        controller = new ColorSavingController(colorSchemeRepository, msgs);
    }

    @Test
    public void addNullTest() {
        var result = controller.add(null);
        assertEquals(ResponseEntity.badRequest().build(), result);
    }
    @Test
    public void addTest() {
        ColorScheme colorScheme = new ColorScheme();
        controller.add(colorScheme);
        assertEquals(colorScheme, colorSchemeRepository.findAll().get(0));
    }
    @Test
    public void getBoardTest() {
        Board board = new Board();
        assertEquals(ResponseEntity.ok(board), controller.getBoard(board));
    }
    @Test
    public void deleteNotExistingColorSchemeTest() {
        ColorScheme colorScheme = new ColorScheme();
        assertEquals(ResponseEntity.notFound().build(), controller.delete(colorScheme.getSchemeId()));
    }
    @Test
    public void deleteListTest() {
        ColorScheme colorScheme = new ColorScheme();
        colorSchemeRepository.save(colorScheme);
        controller.delete(colorScheme.getSchemeId());
        assertEquals(0, colorSchemeRepository.findAll().size());
    }
}
