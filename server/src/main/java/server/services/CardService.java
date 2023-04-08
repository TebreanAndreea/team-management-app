package server.services;

import commons.Card;
import commons.Listing;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;
import server.database.CardRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Service
public class CardService {

    private final CardRepository repo;
    private final SimpMessageSendingOperations msgs;

    private Map<Object, Consumer<Card>> listenings = new HashMap<>();

    private Listing list;

    /**
     * Constructor for the card controller.
     *
     * @param repo - card repository
     * @param msgs - messages for communication
     */
    public CardService(CardRepository repo, SimpMessageSendingOperations msgs) {
        this.repo = repo;
        this.msgs = msgs;
    }

    /**
     * A post method that saves the card into the DB.
     * @param insertedIntoList  checks if the card has been added from a list
     * @param card - the card that we are saving
     * @return card
     */
    public ResponseEntity<Card> add (Card card, boolean insertedIntoList){
        if (card == null) return ResponseEntity.badRequest().build();

        card.setList(list);

        msgs.convertAndSend("/topic/card", card);
        Card save = repo.save(card);
        if(!insertedIntoList) {
            if (!listenings.isEmpty()) {
                listenings.forEach((k, s) -> {  s.accept(save); });
            }
        }
        return ResponseEntity.ok(save);
    }


    /**
     * Post method that sets a list for this card.
     * @param list - list to assign to this card
     * @return the saved list
     */
    public ResponseEntity<Listing> getList(Listing list) {
        this.list = list;
        return ResponseEntity.ok(list);
    }


    /**
     * Method that deletes a card by given id.
     * @param permanentDeletion checks if it has been deleted permanently
     * @param id - corresponding to the card to be deleted
     * @return tag corresponding to the operation
     */
    public ResponseEntity<Listing> delete(long id, boolean permanentDeletion) {
        System.out.println("delete");
        Card card = repo.findById(id).orElse(null);
        if (card == null)
            return ResponseEntity.notFound().build();

        msgs.convertAndSend("/topic/card", card);
        repo.deleteById(id);

        if (permanentDeletion) {
            if (!listenings.isEmpty()) {
                listenings.forEach((k, s) -> {
                    s.accept(card);
                });
            }
        }
        return ResponseEntity.ok().build();
    }

    /**
     * A get method that searches for a card via its ID.
     * @param id the card's ID
     * @return The response entity, containing the desired card
     */
    public ResponseEntity<Card> getById(long id) {
        if (id < 0 || !repo.existsById(id))
            return ResponseEntity.ok(null);
        return ResponseEntity.ok(repo.findById(id).get());
    }


    public DeferredResult<ResponseEntity<Card>> getUppdatesCards() {
        var noContent = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        var result = new DeferredResult<ResponseEntity<Card>>(1000L, noContent);
        var key = new Object();

        listenings.put(key, s -> {
            result.setResult(ResponseEntity.ok(s));
        });

        result.onCompletion(() -> {
            listenings.remove(key);
        });
        return result;
    }

    /**
     * This method checks if a card is in the database.
     * @param card the card
     * @return a response entity boolean
     */
    public ResponseEntity<Boolean> getCard(Card card) {
        Optional<Card> card1 = repo.findById(card.getCardId());
        if (card1.isEmpty())
            return ResponseEntity.ok(true);
        return ResponseEntity.ok(false);
    }

}
