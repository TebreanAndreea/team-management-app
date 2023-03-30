package server.api;

import commons.Board;
import commons.Listing;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ListSavingControllerTest {
    private ListSavingController controller;
    private TestListingRepository listingRepository;
    private TestCardRepository cardRepository;
    private TestSimpMessageTemplate msgs;

    @BeforeEach
    public void setup() {
        listingRepository = new TestListingRepository();
        cardRepository = new TestCardRepository();
        msgs = new TestSimpMessageTemplate();
        controller = new ListSavingController(listingRepository, cardRepository, msgs);
    }
    @Test
    public void getAllTest() {
        Board board1 = new Board("board1", "1234", "123aa");
        Board board2 = new Board("board2", "5678", "5678iii");

        Listing listing1 = new Listing("list1", board1);
        Listing listing2 = new Listing("list2", board2);

        listingRepository.save(listing1);
        listingRepository.save(listing2);
        List<Listing> lists = new ArrayList<>();
        lists.add(listing1);
        lists.add(listing2);
        assertEquals(lists, controller.getAll());
    }
    @Test
    public void getByNegativeIdTest() {
        long id = -1;
        assertEquals(ResponseEntity.badRequest().build(), controller.getById(id));
    }
    @Test
    public void getByIdTest() {
        Board board = new Board("aaa", "123", "123");
        Listing listing = new Listing("list", board);
        listingRepository.save(listing);
        long id = listing.getListId();
        assertEquals(ResponseEntity.ok(listing), controller.getById(id));
    }
    @Test
    public void addNullTest() {
        var result = controller.add(null);
        assertEquals(ResponseEntity.badRequest().build(), result);
    }
    @Test
    public void addTest() {
        Board board = new Board("aaa", "123", "123");
        Listing listing = new Listing("list", board);
        controller.add(listing);
        assertEquals(listing, listingRepository.findAll().get(0));
    }
    @Test
    public void updateTest() {
        Board board = new Board("aaa", "123", "123");
        Listing listing = new Listing("list", board);
        controller.update(listing);
        assertEquals(listing, listingRepository.findAll().get(0));
    }
    @Test
    public void editListTest() {
        Board board = new Board("aaa", "123", "123");
        Listing listing = new Listing("list", board);
        controller.editList(listing);
        assertEquals(listing, listingRepository.findAll().get(0));
    }
    @Test
    public void deleteNotExistingListTest() {
        Board board = new Board("aaa", "123", "123");
        Listing listing = new Listing("list", board);
        assertEquals(ResponseEntity.notFound().build(), controller.delete(listing.getListId()));
    }
    @Test
    public void deleteListTest() {
        Board board = new Board("aaa", "123", "123");
        Listing listing = new Listing("list", board);
        listingRepository.save(listing);
        controller.delete(listing.getListId());
        assertEquals(0, listingRepository.findAll().size());
    }
    @Test
    public void getBoardTest() {
        Board board = new Board("aaa", "123", "123");
        assertEquals(ResponseEntity.ok(board),  controller.getBoard(board));
    }

}
