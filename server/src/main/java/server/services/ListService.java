package server.services;

import commons.Board;
import commons.Card;
import commons.Listing;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import server.database.CardRepository;
import server.database.ListingRepository;

import java.util.List;

@Service
public class ListService {


    private final ListingRepository repo;
    private final CardRepository cardRepo;
    private final SimpMessageSendingOperations msgs;

    private Board board;

    /**
     * Constructor for the list controller.
     *
     * @param repo     - attribute for list repository
     * @param cardRepo - attribute for card repository
     * @param msgs     - messages for communication
     */
    public ListService(ListingRepository repo, CardRepository cardRepo, SimpMessageSendingOperations msgs) {
        this.repo = repo;
        this.cardRepo = cardRepo;
        this.msgs = msgs;
    }

    /**
     * Post method that updates a list.
     *
     * @param list - list to be updated
     * @return saved list
     */
    public ResponseEntity<Listing> update(Listing list) {
        list.setBoard(board);
        msgs.convertAndSend("/topic/lists", list);
        Listing saved = repo.save(list);
        return ResponseEntity.ok(saved);
    }

    /**
     * Post method that adds a list to the DB.
     *
     * @param list - list to be saved into the database
     * @return saved list
     */
    public ResponseEntity<Listing> add(Listing list) {
        if (list == null) return ResponseEntity.badRequest().build();
        list.setBoard(board);
        msgs.convertAndSend("/topic/lists", list);
        Listing saved = repo.save(list);
        return ResponseEntity.ok(saved);
    }

    /**
     * Get method that fetches all lists from DB.
     *
     * @return - a list containing all lists from the database
     */
    public List<Listing> getAll() {
        return repo.findAll();
    }

    /**
     * Get method that fetches a list by given id.
     *
     * @param id - id to search for into DB
     * @return query result - list with given id
     */
    public ResponseEntity<Listing> getById(long id) {
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repo.findById(id).get());
    }

    /**
     * Method that deletes a list from DB.
     *
     * @param id - id corresponding to the list to be deleted
     * @return status of query
     */
    public ResponseEntity<Listing> delete(Long id) {
        Listing list = repo.findById(id).orElse(null);
        if (list == null) {
            return ResponseEntity.notFound().build();
        }

        msgs.convertAndSend("/topic/lists", list);

        repo.deleteById(id);
        return ResponseEntity.ok().build();
    }

    /**
     * A post method that assigns the board to the list.
     *
     * @param board - the board that we are saving
     * @return board
     */
    public ResponseEntity<Board> getBoard(Board board) {
        this.board = board;
        return ResponseEntity.ok(board);
    }

    /**
     * A post method that edits the list.
     *
     * @param listing - the updated list
     * @return list
     */
    public ResponseEntity<Listing> editList(Listing listing) {
        for (Card c : listing.getCards()) {
            c.setList(listing);
            cardRepo.save(c);
        }
        listing.setBoard(board);
        listing = repo.save(listing);
        msgs.convertAndSend("/topic/lists", listing);
        return ResponseEntity.ok(listing);
    }
}

