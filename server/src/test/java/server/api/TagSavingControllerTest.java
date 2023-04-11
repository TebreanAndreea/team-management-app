package server.api;

import commons.Board;
import commons.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TagSavingControllerTest {
    private TagSavingController controller;
    private TestTagRepository tagRepository;
    private TestSimpMessageTemplate msgs;

    @BeforeEach
    public void setup() {
        tagRepository = new TestTagRepository();
        msgs = new TestSimpMessageTemplate();
        controller = new TagSavingController(tagRepository, msgs);
    }
    @Test
    public void addTest() {
        Tag tag = new Tag();
        controller.addTag(tag);
        assertEquals(tag, tagRepository.findAll().get(0));
    }
    @Test
    public void addNullTest() {
        var result = controller.addTag(null);
        assertEquals(ResponseEntity.badRequest().build(), result);
    }
    @Test
    public void deleteNotExistingListTest() {
        Tag tag = new Tag();
        assertEquals(ResponseEntity.notFound().build(), controller.delete(tag.getTagId()));
    }
    @Test
    public void deleteListTest() {
        Tag tag = new Tag();
        tagRepository.save(tag);
        controller.delete(tag.getTagId());
        assertEquals(0, tagRepository.findAll().size());
    }
    @Test
    public void setBoardToTagTest() {
        Board board = new Board();
        controller.setBoardToTag(board);
        assertEquals(ResponseEntity.ok(board), controller.setBoardToTag(board));
    }
    @Test
    public void getUpdates()
    {
        assertNull(controller.getUpdatesTag().getResult());
    }



}
