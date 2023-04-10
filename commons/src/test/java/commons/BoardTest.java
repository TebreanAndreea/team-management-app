package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    private Board board;

    @BeforeEach
    void setUp() {
        this.board = new Board("board","accessKey","password");
    }

    @Test
    void getBoardId() { // the id is auto generated
    }
    @Test
    void setBoardId() {
        board.setBoardId(2);
        assertEquals(2, board.getBoardId());
    }

    @Test
    void getTitle() {
        assertEquals("board", board.getTitle());
    }

    @Test
    void setTitle() {
        board.setTitle("new board");
        assertEquals("new board", board.getTitle());
    }

    @Test
    void getLists() {
        List<Listing> list2 = new ArrayList<>();
        assertEquals(list2, board.getLists());
    }

    @Test
    void setLists() {
        List<Listing> newLists = new ArrayList<>();

        Listing list1 = new Listing("list1", board);
        newLists.add(list1);

        Listing list2 = new Listing("list2", board);
        newLists.add(list2);

        board.setLists(newLists);
        assertEquals(newLists, board.getLists());
    }

    @Test
    void getAccessKey() {
        assertEquals("accessKey", board.getAccessKey());
    }

    @Test
    void setAccessKey() {
        board.setAccessKey();
        // check if the access key is null
        assertNotNull(board.getAccessKey());

        // check for access key length
        assertEquals(10, board.getAccessKey().length());
    }

    @Test
    void getPassword() {
        assertEquals("password",board.getPassword());
    }

    @Test
    void setPassword() {
        board.setPassword("password2");
        assertEquals("password2", board.getPassword());
    }

    @Test
    void getTags() {
        List<Tag> tags = new ArrayList<>();
        assertEquals(tags, board.getTags());
    }

    @Test
    void setTags() {
        List<Tag> newTags = new ArrayList<>();

        Tag tag1 = new Tag("tag1", board);
        Tag tag2 = new Tag("tag2", board);

        newTags.add(tag1);
        newTags.add(tag2);

        board.setTags(newTags);
        assertEquals(newTags, board.getTags());
    }

    @Test
    void testEquals() {
        Board board2 = new Board("board","accessKey","password");
        assertEquals(board, board2);
    }

    @Test
    void testNotEquals(){
        Board board2 = new Board("","accessKey","password");
        assertNotEquals(board, board2);
    }

    @Test
    void testHashCode() {
        Board board2 = new Board("board","accessKey","password");
        assertEquals(board.hashCode(),board2.hashCode());
    }

    @Test
    void getBackgroundColorDefault() {
        assertEquals("#ffffff", board.getBackgroundColorDefault());
    }

    @Test
    void getTextColorDefault() {
        assertEquals("#000000", board.getTextColorDefault());
    }

    @Test
    void getListBackgroundColorDefault() {
        assertEquals("#ffffff", board.getListBackgroundColorDefault());
    }

    @Test
    void getListTextColorDefault() {
        assertEquals("#000000", board.getListTextColorDefault());
    }

    @Test
    void getBackgroundColor() {
        assertEquals("#ffffff", board.getBackgroundColor());
    }

    @Test
    void setBackgroundColor() {
        board.setBackgroundColor("blue");
        assertEquals("blue", board.getBackgroundColor());
    }

    @Test
    void getTextColor() {
        assertEquals("#000000", board.getTextColor());
    }

    @Test
    void setTextColor() {
        board.setTextColor("blue");
        assertEquals("blue", board.getTextColor());
    }

    @Test
    void getListBackgroundColor() {
        assertEquals("#ffffff", board.getListBackgroundColor());
    }

    @Test
    void setListBackgroundColor() {
        board.setListBackgroundColor("blue");
        assertEquals("blue", board.getListBackgroundColor());
    }

    @Test
    void getListTextColor() {
        assertEquals("#000000", board.getListTextColor());
    }

    @Test
    void setListTextColor() {
        board.setListTextColor("blue");
        assertEquals("blue", board.getListTextColor());
    }

    @Test
    void getCardBackgroundColor() {
        assertEquals("#ffffff", board.getCardBackgroundColor());
    }

    @Test
    void setCardBackgroundColor() {
        board.setCardBackgroundColor("blue");
        assertEquals("blue", board.getCardBackgroundColor());
    }

    @Test
    void getCardFontColor() {
        assertEquals("#000000", board.getCardFontColor());
    }

    @Test
    void setCardFontColor() {
        board.setCardFontColor("blue");
        assertEquals("blue", board.getCardFontColor());
    }

    @Test
    void getSchemes() {
        ArrayList<ColorScheme> schemes = new ArrayList<>();
        assertEquals(schemes, board.getSchemes());
    }
}