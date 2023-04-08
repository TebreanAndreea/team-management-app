package server.api;

import commons.Board;
import commons.ColorScheme;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;
import server.database.ColorSchemeRepository;
import server.services.ColorService;

@RestController
@RequestMapping("/api/color")
public class ColorSavingController {

    private ColorService colorService;

    /**
     * Constructor for the color controller.
     *
     * @param repo - card repository
     * @param msgs - messages for communication
     */
    public ColorSavingController(ColorSchemeRepository repo, SimpMessageSendingOperations msgs) {
        colorService = new ColorService(repo,msgs);
    }

    /**
     * A post method that saves the color into the DB.
     *
     * @param color - the color that we are saving
     * @return card
     */
    @PostMapping(path = {"", "/"})
    public ResponseEntity<ColorScheme> add(@RequestBody ColorScheme color) {
        return colorService.add(color);
    }

    /**
     * Post method that sets a board for this color.
     *
     * @param board - board to assign to this card
     * @return the saved board
     */
    @PostMapping(path = {"/setBoard"})
    public ResponseEntity<Board> getBoard(@RequestBody Board board) {
        return colorService.getBoard(board);
    }

    /**
     * Method that deletes a color by given id.
     *
     * @param id - corresponding to the color to be deleted
     * @return  tag corresponding to the operation
     */
    @DeleteMapping(path = {"delete/{id}"})
    public ResponseEntity<ColorScheme> delete(@PathVariable long id) {
        return colorService.delete(id);
    }
}
