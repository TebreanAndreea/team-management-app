package server.services;

import commons.Board;
import commons.ColorScheme;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import server.database.ColorSchemeRepository;

@Service
public class ColorService {

    private final ColorSchemeRepository repo;
    private final SimpMessageSendingOperations msgs;

    private static Board board;
    /**
     * Constructor for the color service.
     *
     * @param repo - card repository
     * @param msgs - messages for communication
     */
    public ColorService(ColorSchemeRepository repo, SimpMessageSendingOperations msgs) {
        this.repo = repo;
        this.msgs = msgs;
    }

    /**
     * A post method that saves the color into the DB.
     *
     * @param color - the color that we are saving
     * @return card
     */
    public ResponseEntity<ColorScheme> add(ColorScheme color) {
        if (color == null) return ResponseEntity.badRequest().build();
        color.setBoard(board);

        ColorScheme save = repo.save(color);
        msgs.convertAndSend("/topic/colors", save);

        return ResponseEntity.ok(save);
    }

    /**
     * Post method that sets a board for this color.
     *
     * @param board - board to assign to this card
     * @return the saved board
     */
    public ResponseEntity<Board> getBoard(Board board) {
        this.board = board;
        return ResponseEntity.ok(board);
    }

    /**
     * Method that deletes a color by given id.
     *
     * @param id - corresponding to the color to be deleted
     * @return  tag corresponding to the operation
     */
    public ResponseEntity<ColorScheme> delete(long id) {
        ColorScheme color = repo.findById(id).orElse(null);
        if (color == null)
            return ResponseEntity.notFound().build();

        repo.deleteById(id);
        msgs.convertAndSend("/topic/colors", color);
        return ResponseEntity.ok().build();
    }

}
