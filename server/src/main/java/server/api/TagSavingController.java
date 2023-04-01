package server.api;

import commons.Board;
import commons.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.database.TagRepository;

@RestController
@RequestMapping("api/tag")
public class TagSavingController {
    private final TagRepository repo;
    private final SimpMessagingTemplate msgs;

    private Board board;

    /**
     * Constructor for tag controller.
     *
     * @param repo - tag repository
     * @param msgs - messages for communication
     */
    public TagSavingController(TagRepository repo, SimpMessagingTemplate msgs) {
        this.repo = repo;
        this.msgs = msgs;
    }

    /**
     * A method that saves a subtask into the database.
     * @param tag - the tag we are saving
     * @return tag
     */
    @PostMapping(path = {"", "/"})
    public ResponseEntity<Tag> addTag(@RequestBody Tag tag) {
        tag.setBoard(board);
        msgs.convertAndSend("/topic/tag", tag);
        Tag save = repo.save(tag);
        return ResponseEntity.ok(save);
    }

    /**
     * Set the board for this tag.
     * @param board the board
     * @return a Board object
     */
    @PostMapping("/setBoard")
    public ResponseEntity<Board> setBoardToTag(@RequestBody Board board){
        this.board = board;
        return ResponseEntity.ok(board);
    }
}
