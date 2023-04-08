package server.services;

import commons.Card;
import commons.Listing;
import commons.SubTask;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import server.database.SubTaskRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Service
public class SubtaskService {

    private final SubTaskRepository repo;
    private final SimpMessageSendingOperations msgs;

    private Map<Object, Consumer<SubTask>> listenings = new HashMap<>();

    private Card card;

    /**
     * Constructor for the subtask service.
     *
     * @param repo - subtask repository
     * @param msgs - messages for communication
     */
    public SubtaskService(SubTaskRepository repo, SimpMessageSendingOperations msgs) {
        this.repo = repo;
        this.msgs = msgs;
    }

    /**
     * A method that saves a subtask into the DB.
     *
     * @param subTask - the subtask we are saving
     * @return subtask
     */
    public ResponseEntity<SubTask> add(SubTask subTask) {
        subTask.setCard(card);
        msgs.convertAndSend("/topic/subtask", subTask);
        SubTask save = repo.save(subTask);
        listenings.forEach((k,s) -> {
            s.accept(save);
        });
        return ResponseEntity.ok(save);
    }

    /**
     * Post method for assigning the subtask to a card.
     *
     * @param card - the card to assign subtask to
     * @return the saved card
     */
    public ResponseEntity<Card> getCard(Card card) {
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
    public ResponseEntity<SubTask> updateSubtask(SubTask subTask) {
        subTask.setCard(card);
        subTask = repo.save(subTask);
        msgs.convertAndSend("/topic/subtask", subTask);
        SubTask sb = subTask;
        listenings.forEach((k,s) -> {
            s.accept(sb);
        });
        return ResponseEntity.ok(subTask);
    }

    /**
     * Deleting a subtask.
     *
     * @param id the id of the subtask
     * @return tag corresponding to the operation
     */
    public ResponseEntity<Listing> delete(long id) {
        SubTask subTask = repo.findById(id).orElse(null);
        if (subTask == null) {
            return ResponseEntity.notFound().build();
        }
        msgs.convertAndSend("/topic/subtask", subTask);
        repo.deleteById(id);
        listenings.forEach((k,s) -> {
            s.accept(subTask);
        });
        return ResponseEntity.ok().build();
    }

    /**
     * Getting a subtask with a given id.
     *
     * @param id of the subtask
     * @return tag corresponding to the operation
     */
    public ResponseEntity<SubTask> getById(long id) {
        if (id < 0 || !repo.existsById(id))
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(repo.findById(id).get());
    }

    public DeferredResult<ResponseEntity<SubTask>> getUpdatesSubtasks () {
        var noContent = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        var result = new DeferredResult<ResponseEntity<SubTask>>(1000L, noContent);
        var key = new Object();

        listenings.put(key, s ->{
            System.out.println("Reached");
            result.setResult(ResponseEntity.ok(s));
        });

        result.onCompletion(() -> {
            listenings.remove(key);
        });
        return result;
    }
}
