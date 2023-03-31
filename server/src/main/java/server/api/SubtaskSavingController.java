package server.api;

import commons.Card;
import commons.Listing;
import commons.SubTask;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import server.database.SubTaskRepository;

@RestController
@RequestMapping("api/subtask")
public class SubtaskSavingController {
    private final SubTaskRepository repo;
    private final SimpMessagingTemplate msgs;

    private Card card;

    /**
     * Constructor for subtask controller.
     *
     * @param repo - subtask repository
     * @param msgs - messages for communication
     */
    public SubtaskSavingController(SubTaskRepository repo, SimpMessagingTemplate msgs) {
        this.repo = repo;
        this.msgs = msgs;
    }

    /**
     * A method that saves a subtask into the DB.
     *
     * @param subTask - the subtask we are saving
     * @return subtask
     */
    @PostMapping(path = {"", "/"})
    public ResponseEntity<SubTask> add(@RequestBody SubTask subTask) {
        subTask.setCard(card);
        msgs.convertAndSend("/topic/subtask", subTask);
        SubTask save = repo.save(subTask);
        return ResponseEntity.ok(save);
    }

    /**
     * Post method for assigning the subtask to a card.
     *
     * @param card - the card to assign subtask to
     * @return the saved card
     */
    @PostMapping(path = {"/setCard"})
    public ResponseEntity<Card> getCard(@RequestBody Card card) {

        this.card = card;
        return ResponseEntity.ok(card);
    }

    /**
     * Editing a subtask.
     *
     * @param subTask the subtask to be edited
     * @return the updated subtask
     */
    @PostMapping(path = { "/edit" })
    public ResponseEntity<SubTask> updateSubtask(@RequestBody SubTask subTask) {
        subTask.setCard(card);
        subTask = repo.save(subTask);
        msgs.convertAndSend("/topic/subtask", subTask);
        return ResponseEntity.ok(subTask);
    }

    /**
     * Deleting a subtask.
     *
     * @param id the id of the subtask
     * @return tag corresponding to the operation
     */
    @DeleteMapping(path = {"delete/{id}"})
    public ResponseEntity<Listing> delete(@PathVariable long id) {
        SubTask subTask = repo.findById(id).orElse(null);
        if (subTask == null) {
            return ResponseEntity.notFound().build();
        }
        msgs.convertAndSend("/topic/subtask", subTask);
        repo.deleteById(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Getting a subtask with a given id.
     *
     * @param id of the subtask
     * @return tag corresponding to the operation
     */
    @GetMapping("/{id}")
    public ResponseEntity<SubTask> getById(@PathVariable("id") long id) {
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repo.findById(id).get());
    }
}
