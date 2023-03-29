package server.api;

import commons.Card;
import commons.SubTask;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
}
