package server.api;

import commons.Card;
import commons.Listing;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import server.database.CardRepository;
import server.services.CardService;


@RestController
@RequestMapping("api/card")
public class CardSavingController {

    private CardService cardService;

    /**
     * Constructor for the card controller.
     *
     * @param repo - card repository
     * @param msgs - messages for communication
     */
    public CardSavingController(CardRepository repo, SimpMessageSendingOperations msgs) {
        this.cardService = new CardService(repo,msgs);
    }

    /**
     * A post method that saves the card into the DB.
     * @param insertedIntoList  checks if the card has been added from a list
     * @param card - the card that we are saving
     * @return card
     */
    @PostMapping(path = {"/{insertedIntoList}"})
    public ResponseEntity<Card> add(@RequestBody Card card, @PathVariable boolean insertedIntoList) {
        return cardService.add(card,insertedIntoList);
    }

    /**
     * Post method that sets a list for this card.
     *
     * @param list - list to assign to this card
     * @return the saved list
     */
    @PostMapping(path = {"/setList"})
    public ResponseEntity<Listing> getList(@RequestBody Listing list) {
        return cardService.getList(list);
    }

    /**
     * Method that deletes a card by given id.
     * @param permanentDeletion checks if it has been deleted permanently
     * @param id - corresponding to the card to be deleted
     * @return tag corresponding to the operation
     */
    @DeleteMapping(path = {"delete/{id}/{permanentDeletion}"})
    public ResponseEntity<Listing> delete(@PathVariable long id, @PathVariable boolean permanentDeletion) {
        return cardService.delete(id,permanentDeletion);
    }

    /**
     * A get method that searches for a card via its ID.
     *
     * @param id the card's ID
     * @return The response entity, containing the desired card
     */
    @GetMapping("/{id}")
    public ResponseEntity<Card> getById(@PathVariable("id") long id) {
        return cardService.getById(id);
    }

    @GetMapping("/updates")
    public DeferredResult<ResponseEntity<Card>> getUppdatesCards() {
        return cardService.getUppdatesCards();
    }

    @PostMapping("/check")
    public ResponseEntity<Boolean> getCard(@RequestBody Card card) {
        return cardService.getCard(card);
    }
}
