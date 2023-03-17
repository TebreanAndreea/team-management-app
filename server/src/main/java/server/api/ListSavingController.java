package server.api;

import commons.Listing;
import commons.Quote;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.database.ListingRepository;
import server.database.QuoteRepository;

import java.util.Random;
@RestController
@RequestMapping("api/lists")
public class ListSavingController {

    private final ListingRepository repo;

    public ListSavingController(ListingRepository repo) {
        this.repo = repo;
    }

    @PostMapping(path = { "", "/" })
    public ResponseEntity<Listing> add(@RequestBody Listing list) {
      //  if (list.getTitle()== null) {
      //      return ResponseEntity.badRequest().build();
       // }
        Listing saved = repo.save(list);
        return ResponseEntity.ok(saved);
    }
}
