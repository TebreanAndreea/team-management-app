package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ColorSchemeTest {

    ColorScheme colorScheme;
    @BeforeEach
    void setUp() {
        Board board = new Board("board","accessKey","password");
        this.colorScheme = new ColorScheme("name", "bColor", "fColor", board);
    }

    @Test
    void testDefaultConstructor() {
        colorScheme = new ColorScheme();
        assertNotNull(colorScheme);
    }

    @Test
    void getSchemeId() { // the id is auto generated
    }

    @Test
    void setSchemeId() {
        colorScheme.setSchemeId(1234321);
        assertEquals(1234321, colorScheme.getSchemeId());
    }

    @Test
    void getName() {
        assertEquals("name", colorScheme.getName());
    }

    @Test
    void setName() {
        colorScheme.setName("name2");
        assertEquals("name2", colorScheme.getName());
    }

    @Test
    void getBackgroundColor() {
        assertEquals("bColor", colorScheme.getBackgroundColor());
    }

    @Test
    void setBackgroundColor() {
        colorScheme.setBackgroundColor("bColor2");
        assertEquals("bColor2", colorScheme.getBackgroundColor());
    }

    @Test
    void getFontColor() {
        assertEquals("fColor", colorScheme.getFontColor());
    }

    @Test
    void setFontColor() {
        colorScheme.setFontColor("fColor2");
        assertEquals("fColor2", colorScheme.getFontColor());
    }

    @Test
    void isDef() {
        assertFalse(colorScheme.isDef());
    }

    @Test
    void setDef() {
        colorScheme.setDef(true);
        assertTrue(colorScheme.isDef());
    }

    @Test
    void getBoard() {
        Board board = new Board("board","accessKey","password");
        assertEquals(board, colorScheme.getBoard());
    }

    @Test
    void setBoard() {
        Board board = new Board("board2","accessKey","password");
        colorScheme.setBoard(board);
        assertEquals(board, colorScheme.getBoard());
    }

    @Test
    void testEquals() {
        Board board = new Board("board","accessKey","password");
        ColorScheme colorScheme2 = new ColorScheme("name", "bColor", "fColor", board);

        assertEquals(colorScheme2, colorScheme);
    }
    @Test
    void testNotEquals() {
        Board board = new Board("board2","accessKey","password");
        ColorScheme colorScheme2 = new ColorScheme("name", "bColor", "fColor", board);

        assertNotEquals(colorScheme2, colorScheme);
    }

    @Test
    void testHashCode() {
        Board board = new Board("board","accessKey","password");
        ColorScheme colorScheme2 = new ColorScheme("name", "bColor", "fColor", board);

        assertEquals(colorScheme2.hashCode(), colorScheme.hashCode());
    }
}