package server.services;

import commons.Board;
import commons.Card;
import commons.Listing;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import server.database.CardRepository;
import server.database.ListingRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
class ListServiceTest {

    ListService listService;


    ListingRepository repo;
    @Mock
    SimpMessageSendingOperations msgs;

    CardRepository cardRepository;
    Board board;
    Listing list;

    @BeforeEach
    public void setup()
    {
        repo = mock(ListingRepository.class);
        cardRepository = mock(CardRepository.class);
        listService = new ListService(repo,cardRepository,msgs);
        board = new Board("Hello", "I am", "");
        list = new Listing("title", board);
    }
    @Test
    void update() {


        when(repo.save(list)).thenReturn(list);
        Listing listing = listService.update(list).getBody();

        verify(msgs).convertAndSend("/topic/lists", list);
        verify(repo).save(list);

        assertEquals(listing,list);
    }

    @Test
    void add() {
        when(repo.save(list)).thenReturn(list);
        Listing listing = listService.add(list).getBody();

        verify(msgs).convertAndSend("/topic/lists", list);
        verify(repo).save(list);

        assertEquals(listing,list);
    }

    @Test
    void addInvalid()
    {
        list = null;
        ResponseEntity<Listing> res = listService.add(list);
        verify(msgs, never()).convertAndSend("/topic/lists", list);
        verify(repo, never()).save(list);

        assertEquals(res, ResponseEntity.badRequest().build());
    }

    @Test
    void getAll() {
        Listing listA = new Listing("a", board);
        Listing listB = new Listing("b", board);


        List<Listing> lists = new ArrayList<>();
        lists.add(list);
        lists.add(listA);
        lists.add(listB);

        when(repo.findAll()).thenReturn(lists);
        List result = listService.getAll();
        verify(repo).findAll();

        assertEquals(result, lists);
    }

    @Test
    void getById() {
        when(repo.existsById(1L)).thenReturn(true);
        when(repo.findById(1L)).thenReturn(Optional.of(list));
        Listing result = listService.getById(1L).getBody();

        verify(repo).existsById(1L);
        verify(repo).findById(1L);

        assertEquals(list, result);
    }

    @Test
    void getByIdInvalid()
    {
        when(repo.existsById(1L)).thenReturn(false);
        ResponseEntity<Listing> result = listService.getById(1L);

        verify(repo).existsById(1L);
        verify(repo,never()).findById(1L);

        assertEquals(ResponseEntity.badRequest().build(), result);
    }

    @Test
    void delete() {
        when(repo.findById(1L)).thenReturn(Optional.of(list));
        doNothing().when(repo).deleteById(1L);

        ResponseEntity<Listing> response = listService.delete(1L);
        verify(msgs).convertAndSend("/topic/lists", list);
        verify(repo).findById(1L);
        verify(repo).deleteById(1L);

        assertEquals(ResponseEntity.ok().build(), response);
    }

    @Test
    void deleteInvaild(){
        when(repo.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Listing> response = listService.delete(1L);
        verify(msgs,never()).convertAndSend("/topic/lists", list);
        verify(repo).findById(1L);
        verify(repo,never()).deleteById(1L);

        assertEquals(ResponseEntity.notFound().build(), response);
    }

    @Test
    void getBoard() {
        Board bbb = listService.getBoard(board).getBody();
        assertEquals(bbb, board);
    }

    @Test
    void editList() {
        Card c1 = new Card("aaa", "a", new Date(), new ArrayList<>(), new ArrayList<>(), list, "","");
        Card c2 = new Card("bbb", "b", new Date(), new ArrayList<>(), new ArrayList<>(), list, "","");
        Card c3 = new Card("ccc", "c", new Date(), new ArrayList<>(), new ArrayList<>(), list, "","");

        list.getCards().add(c1);
        list.getCards().add(c2);
        list.getCards().add(c3);

        when(cardRepository.save(c1)).thenReturn(c1);
        when(cardRepository.save(c2)).thenReturn(c2);
        when(cardRepository.save(c3)).thenReturn(c3);
        when(repo.save(list)).thenReturn(list);
        Listing result = listService.editList(list).getBody();
        verify(msgs).convertAndSend("/topic/lists", list);
        verify(repo).save(list);
        verify(cardRepository).save(c1);
        verify(cardRepository).save(c2);
        verify(cardRepository).save(c3);

        assertEquals(result, list);
    }
}