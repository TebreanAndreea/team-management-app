package server.api;

import commons.Board;
import commons.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import server.database.TagRepository;
import server.services.TagService;

@RestController
@RequestMapping("api/tag")
public class TagSavingController {
    private TagService tagService;

    /**
     * Constructor for tag controller.
     *
     * @param repo - tag repository
     * @param msgs - messages for communication
     */
    public TagSavingController(TagRepository repo, SimpMessageSendingOperations msgs) {
        this.tagService = new TagService(repo,msgs);
    }

    /**
     * A method that saves a subtask into the database.
     * @param tag - the tag we are saving
     * @return tag
     */
    @PostMapping(path = {"", "/"})
    public ResponseEntity<Tag> addTag(@RequestBody Tag tag) {
        return tagService.addTag(tag);
    }

    /**
     * Set the board for this tag.
     * @param board the board
     * @return a Board object
     */
    @PostMapping("/setBoard")
    public ResponseEntity<Board> setBoardToTag(@RequestBody Board board){
        return tagService.setBoardToTag(board);
    }

    /**
     * Method that deletes a tag from DB.
     *
     * @param id - id corresponding to the tag to be deleted
     * @return status of query
     */
    @DeleteMapping(path = {"delete/{id}"})
    public ResponseEntity<Tag> delete(@PathVariable long id) {
        return tagService.delete(id);
    }

    @GetMapping("/updates")
    public DeferredResult<ResponseEntity<Tag>> getUpdatesTag () {
        return tagService.getUpdatesTag();
    }
}
