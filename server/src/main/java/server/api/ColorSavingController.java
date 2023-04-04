package server.api;

import commons.Board;
import commons.ColorScheme;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;
import server.database.ColorSchemeRepository;

@RestController
@RequestMapping("/api/color")
public class ColorSavingController {

    private final ColorSchemeRepository repo;
    private final SimpMessageSendingOperations msgs;

    private static Board board;

    /**
     * Constructor for the color controller.
     *
     * @param repo - card repository
     * @param msgs - messages for communication
     */
    public ColorSavingController(ColorSchemeRepository repo, SimpMessageSendingOperations msgs) {
        this.repo = repo;
        this.msgs = msgs;
    }

    /**
     * A post method that saves the color into the DB.
     *
     * @param color - the color that we are saving
     * @return card
     */
    @PostMapping(path = {"", "/"})
    public ResponseEntity<ColorScheme> add(@RequestBody ColorScheme color) {
        if (color == null) return ResponseEntity.badRequest().build();

        color.setBoard(board);

        ColorScheme save = repo.save(color);
        return ResponseEntity.ok(save);
    }

    /**
     * Post method that sets a board for this color.
     *
     * @param board - board to assign to this card
     * @return the saved board
     */
    @PostMapping(path = {"/setBoard"})
    public ResponseEntity<Board> getBoard(@RequestBody Board board) {

        this.board = board;
        return ResponseEntity.ok(board);
    }

    /**
     * Method that deletes a color by given id.
     *
     * @param id - corresponding to the color to be deleted
     * @return  tag corresponding to the operation
     */
    @DeleteMapping(path = {"delete/{id}"})
    public ResponseEntity<ColorScheme> delete(@PathVariable long id) {
        ColorScheme color = repo.findById(id).orElse(null);
        if (color == null) {
            return ResponseEntity.notFound().build();
        }
        repo.deleteById(id);
        return ResponseEntity.ok().build();
    }





}
