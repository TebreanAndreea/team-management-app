package server;

import commons.Card;
import commons.Listing;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import server.database.CardRepository;
import server.database.ListingRepository;

import java.util.ArrayList;

@Controller
@RequestMapping("/")
public class SomeController {

    private final CardRepository repo;
    private final ListingRepository lrepo;

    public SomeController(CardRepository repo, ListingRepository lrepo) {
        this.repo = repo;
        this.lrepo = lrepo;
    }


    @GetMapping("/")
    @ResponseBody
    public String index() {
        return "Hello world!";
    }

    /**
     * Small test to see if the saving works. You need an already saved
     * list in the DB in order to save a card with a proper list
     * @return String
     */
    @GetMapping("/save")
    @ResponseBody
    public String saving() {
        Listing list = new Listing("HardCode1", null);
        Card card = new Card("", "HardcodedCard", null, new ArrayList<>(), new ArrayList<>(), list);
        lrepo.save(list);
        repo.save(card);
        return "Saved the list and the card!";
    }
}