package server.api;

import commons.Card;
import commons.Listing;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.database.CardRepository;
//import server.database.ListingRepository;


@RestController
@RequestMapping("api/card")
public class CardSavingController {

    private final CardRepository repo;
    private final SimpMessagingTemplate msgs;

    private Listing list;

    public CardSavingController(CardRepository repo, SimpMessagingTemplate msgs) {
        this.repo = repo;
        this.msgs = msgs;
    }

    /**
     * A post method that saves the card into the DB.
     *
     * @param card - the card that we are saving
     * @return card
     */
    @PostMapping(path = {"", "/"})
    public ResponseEntity<Card> add(@RequestBody Card card) {

        card.setList(list);

        msgs.convertAndSend("/topic/card", card);
        Card save = repo.save(card);
        return ResponseEntity.ok(save);
    }

    @PostMapping(path = {"/setList"})
    public ResponseEntity<Listing> getList(@RequestBody Listing list) {

        this.list = list;
        return ResponseEntity.ok(list);
    }

    @DeleteMapping(path = {"delete/{id}"})
    public void delete(@PathVariable long id) {
        repo.deleteById(id);
    }
}
