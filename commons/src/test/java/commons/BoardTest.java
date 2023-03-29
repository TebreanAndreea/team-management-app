package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    private Board board;
    private List<Listing> lists;

    @BeforeEach
    void setUp() {

        this.board = new Board("board","accesskey","password");
        this.lists = new ArrayList<>();

        Listing list1 = new Listing("list1", board);
        this.lists.add(list1);

        Listing list2 = new Listing("list2", board);
        this.lists.add(list2);
    }

    @Test
    void getBoardId() { // the id is auto generated
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
        assertEquals("accesskey", board.getAccessKey());
    }

    @Test
    void setAccessKey() {
        board.setAccessKey();
        // check if the access key is null
        assertNotNull(board.getAccessKey());

        // check for access key length
        assertTrue(board.getAccessKey().length() == 10);
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
    void testEquals() {
        Board board2 = new Board("board","accesskey","password");
        assertTrue(board.equals(board2));
    }

    @Test
    void testNotEquals(){
        Board board2 = new Board("","accesskey","password");
        assertTrue(board.equals(board2) == false);
    }

    @Test
    void testHashCode() {
        Board board2 = new Board("board","accesskey","password");
        assertEquals(board.hashCode(),board2.hashCode());
    }
}