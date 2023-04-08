package server.services;

import commons.Board;
import commons.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;
import server.database.TagRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Service
public class TagService {
    private final TagRepository repo;
    private final SimpMessageSendingOperations msgs;

    private Board board;
    private Map<Object, Consumer<Tag>> listenings = new HashMap<>();

    /**
     * Constructor for tag service.
     *
     * @param repo - tag repository
     * @param msgs - messages for communication
     */
    public TagService(TagRepository repo, SimpMessageSendingOperations msgs) {
        this.repo = repo;
        this.msgs = msgs;
    }

    /**
     * A method that saves a subtask into the database.
     * @param tag - the tag we are saving
     * @return tag
     */
    public ResponseEntity<Tag> addTag(Tag tag) {
        if(tag == null)  return ResponseEntity.badRequest().build();
        tag.setBoard(board);
        msgs.convertAndSend("/topic/tag", tag);
        Tag save = repo.save(tag);
        listenings.forEach((k,s) -> {
            s.accept(save);
        });
        return ResponseEntity.ok(save);
    }

    /**
     * Set the board for this tag.
     * @param board the board
     * @return a Board object
     */
    public ResponseEntity<Board> setBoardToTag(Board board){
        this.board = board;
        return ResponseEntity.ok(board);
    }

    /**
     * Method that deletes a tag from DB.
     *
     * @param id - id corresponding to the tag to be deleted
     * @return status of query
     */
    public ResponseEntity<Tag> delete(long id) {
        Tag tag = repo.findById(id).orElse(null);
        if (tag == null)
            return ResponseEntity.notFound().build();

        msgs.convertAndSend("/topic/tag", tag);
        repo.deleteById(id);
        listenings.forEach((k,s) -> {
            s.accept(tag);
        });
        return ResponseEntity.ok().build();
    }

    public DeferredResult<ResponseEntity<Tag>> getUpdatesTag () {
        var noContent = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        var result = new DeferredResult<ResponseEntity<Tag>>(1000L, noContent);
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
