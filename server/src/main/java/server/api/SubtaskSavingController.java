package server.api;

import commons.Card;
import commons.Listing;
import commons.SubTask;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import server.database.SubTaskRepository;
import server.services.SubtaskService;

@RestController
@RequestMapping("api/subtask")
public class SubtaskSavingController {

    private SubtaskService subtaskService;

    /**
     * Constructor for subtask controller.
     *
     * @param repo - subtask repository
     * @param msgs - messages for communication
     */

    public SubtaskSavingController(SubTaskRepository repo, SimpMessageSendingOperations msgs) {
        subtaskService = new SubtaskService(repo,msgs);
    }

    /**
     * A method that saves a subtask into the DB.
     *
     * @param subTask - the subtask we are saving
     * @return subtask
     */
    @PostMapping(path = {"", "/"})
    public ResponseEntity<SubTask> add(@RequestBody SubTask subTask) {
        return subtaskService.add(subTask);
    }

    /**
     * Post method for assigning the subtask to a card.
     *
     * @param card - the card to assign subtask to
     * @return the saved card
     */
    @PostMapping(path = {"/setCard"})
    public ResponseEntity<Card> getCard(@RequestBody Card card) {
        return subtaskService.getCard(card);
    }

    /**
     * Editing a subtask.
     *
     * @param subTask the subtask to be edited
     * @return the updated subtask
     */
    @PostMapping(path = { "/edit" })
    public ResponseEntity<SubTask> updateSubtask(@RequestBody SubTask subTask) {
        return subtaskService.updateSubtask(subTask);
    }

    /**
     * Deleting a subtask.
     *
     * @param id the id of the subtask
     * @return tag corresponding to the operation
     */
    @DeleteMapping(path = {"delete/{id}"})
    public ResponseEntity<Listing> delete(@PathVariable long id) {
        return subtaskService.delete(id);
    }

    /**
     * Getting a subtask with a given id.
     *
     * @param id of the subtask
     * @return tag corresponding to the operation
     */
    @GetMapping("/{id}")
    public ResponseEntity<SubTask> getById(@PathVariable("id") long id) {
        return subtaskService.getById(id);
    }

    @GetMapping("/updates")
    public DeferredResult<ResponseEntity<SubTask>> getUpdatesSubtasks () {
        return subtaskService.getUpdatesSubtasks();
    }
}
