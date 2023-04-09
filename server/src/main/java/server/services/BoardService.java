package server.services;

import commons.Board;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import server.database.BoardRepository;

import java.util.List;

@Service
public class BoardService {

    private final BoardRepository repo;
    private SimpMessageSendingOperations msgs;

    /**
     * Constructor for Board controller.
     *
     * @param repo - board repository
     * @param msgs - messages for communication
     */
    public BoardService(BoardRepository repo, SimpMessageSendingOperations msgs) {
        this.repo = repo;
        this.msgs = msgs;
    }

    /**
     * Get method for fetching all boards from DB.
     *
     * @return - a list of boards from DB
     */
    public List<Board> getAll() {
        return repo.findAll();
    }

    /**
     * Post method that adds a board into the DB.
     *
     * @param board - board to be added into the database
     * @return the saved board
     */
    public ResponseEntity<Board> add(Board board) {
        if(board == null) return ResponseEntity.badRequest().build();
        msgs.convertAndSend("/topic/boards", board);
        Board save = board;
        save.setAccessKey();
        save = repo.save(save);
        return ResponseEntity.ok(save);
    }

    /**
     * Get method for fetching a board by id.
     *
     * @param id - to search for board into DB
     * @return the query result - board corresponding to id
     */
    public ResponseEntity<Board> getById(long id) {
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repo.findById(id).get());
    }

    /**
     * Method that deletes a board from DB.
     *
     * @param id - id corresponding to the board to be deleted
     * @return status of query
     */
    public ResponseEntity<Board> delete(Long id) {
        if (!repo.existsById(id))
            return ResponseEntity.notFound().build();

        msgs.convertAndSend("/topic/boards", repo.findById(id).get());

        repo.deleteById(id);
        return ResponseEntity.ok().build();
    }

}
