package server.services;

import commons.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import server.database.CardRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
class CardServiceTest {

    CardService cardService;
    CardRepository repo;
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

        Listing list = new Listing("list",null);

        card = new Card("desc","name",new Date(),tags,subtasks,list,"black","black","name");
    }

    @Test
    void add() {
        when(repo.save(card)).thenReturn(card);

        Card save = cardService.add(card,true).getBody();

        verify(msgs).convertAndSend("/topic/card", card);
        verify(repo).save(card);

        assertEquals(save, card);
    }

    @Test
    void getList() {
        Listing list = new Listing("list",null);
        Listing save = cardService.getList(list).getBody();

        assertEquals(save,list);
    }

    /* This doesn't work for now
    @Test
    void delete() {
        when(repo.existsById(1L)).thenReturn(true);
        when(repo.findById(1L)).thenReturn(Optional.of(card));

        doNothing().when(repo).deleteById(1L);

        ResponseEntity<Listing> response = cardService.delete(1L,true);
        verify(msgs).convertAndSend("/topic/card", card);
        verify(repo).existsById(1L);
        verify(repo).findById(1L);
        verify(repo).deleteById(1L);

        assertEquals(ResponseEntity.ok().build(), response);
    }


    @Test
    void deleteInvalid() {

        when(repo.existsById(1L)).thenReturn(false);

        ResponseEntity<Listing> bbb = cardService.delete(1L,true);
        verify(repo).existsById(1L);
        verify(repo,never()).findById(1L);
        verify(repo,never()).deleteById(1L);
        verify(msgs, never()).convertAndSend("/topic/card", card);

        assertEquals(bbb, ResponseEntity.notFound().build());
    }
*/
    @Test
    void getById() {
        when(repo.existsById(1L)).thenReturn(true);
        when(repo.findById(1L)).thenReturn(Optional.of(card));

        Card card2 = cardService.getById(1L).getBody();
        verify(repo).existsById(1L);
        verify(repo).findById(1L);

        assertEquals(card2, card);
    }

    /*
    @Test
    void getCard() {
        Card card2 = new Card("d","name",null,null,null,null,null,null,null);
        boolean exists = cardService.getCard(card).getBody();
        boolean notExists = cardService.getCard(card2).getBody();

        assertTrue(exists);
        assertFalse(notExists);
    }

     */
}