package server.api;

import commons.Card;
import commons.Listing;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;


import static org.junit.jupiter.api.Assertions.assertEquals;

public class CardSavingControllerTest {
    private CardSavingController controller;
    private TestCardRepository cardRepository;
    private TestSimpMessageTemplate msgs;

    @BeforeEach
    public void setup() {
        cardRepository = new TestCardRepository();
        msgs = new TestSimpMessageTemplate();
        controller = new CardSavingController(cardRepository, msgs);
    }
    @Test
    public void getByNegativeIdTest() {
        long id = -1;
        Card card =  controller.getById(id).getBody();
        assertEquals(null, card);
    }
    @Test
    public void getByIdTest() {
        Card card = new Card();
        cardRepository.save(card);
        long id = card.getCardId();
        assertEquals(ResponseEntity.ok(card), controller.getById(id));
    }
    @Test
    public void addTest() {
        Card card = new Card();
        controller.add(card, true);
        assertEquals(card, cardRepository.findAll().get(0));
    }
    @Test
    public void addNullTest() {
        var result = controller.add(null, true);
        assertEquals(ResponseEntity.badRequest().build(), result);
    }
    @Test
    public void deleteNotExistingListTest() {
        Card card = new Card();
        assertEquals(ResponseEntity.notFound().build(), controller.delete(card.getCardId(), true));
    }
    @Test
    public void deleteListTest() {
        Card card = new Card();
        cardRepository.save(card);
        controller.delete(card.getCardId(), true);
        assertEquals(0, cardRepository.findAll().size());
    }
    @Test
    public void getListTest() {
        Listing list = new Listing();
        assertEquals(ResponseEntity.ok(list), controller.getList(list));

    }
    @Test
    public void getCardTest() {
        Card card = new Card();
        assertEquals(ResponseEntity.ok(true), controller.getCard(card));
    }

}
