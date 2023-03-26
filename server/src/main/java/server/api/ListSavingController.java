package server.api;

import commons.Board;
import commons.Listing;
//import commons.Quote;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import server.database.ListingRepository;

import java.util.List;


@RestController
@RequestMapping("api/lists")
public class ListSavingController {

    private final ListingRepository repo;
    private final SimpMessagingTemplate msgs;

    private Board board;

    public ListSavingController(ListingRepository repo, SimpMessagingTemplate msgs) {
        this.repo = repo;
        this.msgs = msgs;
    }

    @PostMapping(path = { "", "/" })
    public ResponseEntity<Listing> add(@RequestBody Listing list) {
        list.setBoard(board);
        msgs.convertAndSend("/topic/lists", list);
        Listing saved = repo.save(list);
        return ResponseEntity.ok(saved);
    }
    @GetMapping(path = { "", "/" })
    public List<Listing> getAll() {
        return repo.findAll();
    }
    @GetMapping("/{id}")
    public ResponseEntity<Listing> getById(@PathVariable("id") long id) {
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repo.findById(id).get());
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Listing> delete(@PathVariable("id") Long id) {
        Listing list = repo.findById(id).orElse(null);
        if (list == null) {
            return ResponseEntity.notFound().build();
        }

        msgs.convertAndSend("/topic/lists", list);

        repo.deleteById(id);
        return ResponseEntity.ok().build();
    }

    /**
     * A post method that assignes the board to the list.
     * @param board - the board that we are saving
     * @return board
     */
    @PostMapping(path = {"/setBoard"})
    public ResponseEntity<Board> getBoard(@RequestBody Board board) {
        this.board = board;
        return ResponseEntity.ok(board);
    }
}