package server.api;

import commons.Listing;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.ListingRepository;

import java.util.List;


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
    @GetMapping(path = { "", "/" })
    public List<Listing> getAll() {
        return repo.findAll();
    }
}
