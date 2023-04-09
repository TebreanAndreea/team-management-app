package server.services;

import commons.Board;
import commons.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.context.request.async.DeferredResult;
import server.database.TagRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class TagServiceTest {

    TagService tagService;
    TagRepository repo;

    DeferredResult<ResponseEntity<Tag>> result;
    @Mock
    SimpMessageSendingOperations msgs;
    Tag tag;

    Board board;

    @BeforeEach
    void setUp() {
        repo = mock(TagRepository.class);
        msgs = mock(SimpMessageSendingOperations.class);
        tagService = new TagService(repo,msgs);

        tag = new Tag("tag1");
        board = new Board("Hello", "I am", "");


        result = tagService.getUpdatesTag();
    }

    @Test
    void addTag() {
        when(repo.save(tag)).thenReturn(tag);

        Tag save = tagService.addTag(tag).getBody();

        verify(msgs).convertAndSend("/topic/tag", tag);
        verify(repo).save(tag);
        ResponseEntity<Tag> response = (ResponseEntity<Tag>) result.getResult();


        assertEquals(response.getBody(),  tag);
        assertEquals(save, tag);
    }

    @Test
    void addInvalid() {

        ResponseEntity<Tag> response= tagService.addTag(null);

        verify(msgs, never()).convertAndSend("/topic/tag", tagService);
        verify(repo, never()).save(tag);


        assertEquals(response, ResponseEntity.badRequest().build());
    }

    @Test
    void setBoardToTag() {
        Board bbb = tagService.setBoardToTag(board).getBody();
        assertEquals(bbb, board);
    }

    @Test
    void delete() {
        when(repo.findById(0L)).thenReturn(Optional.of(tag));

        doNothing().when(repo).deleteById(0L);

        ResponseEntity<Tag> response = tagService.delete(0L);
        verify(msgs).convertAndSend("/topic/tag", tag);
        verify(repo).findById(0L);
        verify(repo).deleteById(0L);
        ResponseEntity<Tag> responseI = (ResponseEntity<Tag>) result.getResult();


        assertEquals(responseI.getBody(),  tag);
        assertEquals(ResponseEntity.ok().build(), response);
    }

    @Test
    void deleteInvalid() {

        when(repo.findById(0L)).thenReturn(Optional.empty());
        ResponseEntity<Tag> bbb = tagService.delete(0L);
        verify(repo).findById(0L);
        verify(repo,never()).deleteById(0L);
        verify(msgs, never()).convertAndSend("/topic/tag", tag);

        assertEquals(bbb, ResponseEntity.notFound().build());
    }

    @Test
    void getUpdatesTag() {
        result = tagService.getUpdatesTag();
        assertNull(result.getResult());
    }
}