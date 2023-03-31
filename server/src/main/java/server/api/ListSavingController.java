package server.api;

import commons.Board;
import commons.Listing;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;
import server.database.CardRepository;
import server.database.ListingRepository;
import server.services.ListService;

import java.util.List;


@RestController
@RequestMapping("api/lists")
public class ListSavingController {

//    private final ListingRepository repo;
//    private final CardRepository cardRepo;
//    private final SimpMessageSendingOperations msgs;
//
//    private Board board;
    private ListService listService;

    /**
     * Constructor for the list controller.
     *
     * @param repo - attribute for list repository
     * @param cardRepo - attribute for card repository
     * @param msgs - messages for communication
     */
    public ListSavingController(ListingRepository repo, CardRepository cardRepo, SimpMessageSendingOperations msgs) {
        this.listService = new ListService(repo, cardRepo, msgs);
    }

    /**
     * Post method that updates a list.
     *
     * @param list - list to be updated
     * @return saved list
     */
    @PutMapping (path = { "", "/" })
    public ResponseEntity<Listing> update(@RequestBody Listing list) {
//        list.setBoard(board);
//        msgs.convertAndSend("/topic/lists", list);
//        Listing saved = repo.save(list);
//        return ResponseEntity.ok(saved);
        return listService.update(list);
    }

    /**
     * Post method that adds a list to the DB.
     *
     * @param list - list to be saved into the database
     * @return saved list
     */
    @PostMapping(path = { "", "/" })
    public ResponseEntity<Listing> add(@RequestBody Listing list) {
//        if(list == null)  return ResponseEntity.badRequest().build();
//        list.setBoard(board);
//        msgs.convertAndSend("/topic/lists", list);
//        Listing saved = repo.save(list);
//        return ResponseEntity.ok(saved);
        return listService.add(list);
    }

    /**
     * Get method that fetches all lists from DB.
     *
     * @return - a list containing all lists from the database
     */
    @GetMapping(path = { "", "/" })
    public List<Listing> getAll() {
//        return repo.findAll();
        return listService.getAll();
    }

    /**
     * Get method that fetches a list by given id.
     *
     * @param id - id to search for into DB
     * @return query result - list with given id
     */
    @GetMapping("/{id}")
    public ResponseEntity<Listing> getById(@PathVariable("id") long id) {
//        if (id < 0 || !repo.existsById(id)) {
//            return ResponseEntity.badRequest().build();
//        }
//        return ResponseEntity.ok(repo.findById(id).get());
        return listService.getById(id);
    }

    /**
     * Method that deletes a list from DB.
     *
     * @param id - id corresponding to the list to be deleted
     * @return status of query
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Listing> delete(@PathVariable("id") Long id) {
        return listService.delete(id);
    }

    /**
     * A post method that assigns the board to the list.
     * @param board - the board that we are saving
     * @return board
     */
    @PostMapping(path = {"/setBoard"})
    public ResponseEntity<Board> getBoard(@RequestBody Board board) {
        return listService.getBoard(board);
    }

    /**
     * A post method that edits the list.
     *
     * @param listing - the updated list
     * @return list
     */
    @PostMapping(path = {"/edit"})
    public ResponseEntity<Listing> editList(@RequestBody Listing listing) {
        return listService.editList(listing);
    }
}