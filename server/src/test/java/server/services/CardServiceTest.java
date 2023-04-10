package server.services;

import commons.Card;
import commons.Listing;
import commons.SubTask;
import commons.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.context.request.async.DeferredResult;
import server.database.CardRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
class CardServiceTest {

    CardService cardService;
    CardRepository repo;

    DeferredResult<ResponseEntity<Card>> result;
    @Mock
    SimpMessageSendingOperations msgs;
    Card card;

    @BeforeEach
    void setUp() {
        repo = mock(CardRepository.class);
        cardService = new CardService(repo,msgs);

        // setup a card
        List<Tag> tags = new ArrayList<>();
        Tag tag1 = new Tag("tag1");
        tags.add(tag1);

        List<SubTask> subtasks = new ArrayList<>();
        SubTask subtask1 = new SubTask("s1",card);
        subtasks.add(subtask1);

        result = cardService.getUppdatesCards();
        Listing list = new Listing("list",null);

        card = new Card("desc","name",new Date(),tags,subtasks,list,"black","black","name");
    }

    @Test
    void add() {
        when(repo.save(card)).thenReturn(card);

        Card save = cardService.add(card,false).getBody();

        verify(msgs).convertAndSend("/topic/card", card);
        verify(repo).save(card);
        ResponseEntity<Card> response = (ResponseEntity<Card>) result.getResult();


        assertEquals(response.getBody(),  card);
        assertEquals(save, card);
    }

    @Test
    void addIntoList() {
        when(repo.save(card)).thenReturn(card);

        Card save = cardService.add(card,true).getBody();

        verify(msgs).convertAndSend("/topic/card", card);
        verify(repo).save(card);


        assertNull(result.getResult());
        assertEquals(save, card);
    }

    @Test
    void addInvalid() {

        ResponseEntity<Card> response= cardService.add(null,true);

        verify(msgs, never()).convertAndSend("/topic/card", card);
        verify(repo, never()).save(card);


        assertEquals(response, ResponseEntity.badRequest().build());
    }

    @Test
    void getList() {
        Listing list = new Listing("list",null);
        Listing save = cardService.getList(list).getBody();

        assertEquals(save,list);
    }

    @Test
    void delete() {
        when(repo.findById(0L)).thenReturn(Optional.of(card));

        doNothing().when(repo).deleteById(0L);

        ResponseEntity<Listing> response = cardService.delete(0L,true);
        verify(msgs).convertAndSend("/topic/card", card);
        verify(repo).findById(0L);
        verify(repo).deleteById(0L);
        ResponseEntity<Card> responseI = (ResponseEntity<Card>) result.getResult();


        assertEquals(responseI.getBody(),  card);
        assertEquals(ResponseEntity.ok().build(), response);
    }


    @Test
    void deleteInvalid() {

        when(repo.findById(0L)).thenReturn(Optional.empty());
        ResponseEntity<Listing> bbb = cardService.delete(0L,true);
        verify(repo).findById(0L);
        verify(repo,never()).deleteById(0L);
        verify(msgs, never()).convertAndSend("/topic/card", card);

        assertEquals(bbb, ResponseEntity.notFound().build());
    }

    @Test
    void getById() {
        when(repo.existsById(1L)).thenReturn(true);
        when(repo.findById(1L)).thenReturn(Optional.of(card));

        Card card2 = cardService.getById(1L).getBody();
        verify(repo).existsById(1L);
        verify(repo).findById(1L);

        assertEquals(card2, card);
    }
    @Test
    void getByIdInvalid() {
        when(repo.existsById(1L)).thenReturn(false);

        Card card2 = cardService.getById(1L).getBody();
        verify(repo).existsById(1L);
        verify(repo, never()).findById(1L);

        assertNull(card2);
    }

    @Test
    void update()
    {
        result = cardService.getUppdatesCards();
        assertNull(result.getResult());
    }




}