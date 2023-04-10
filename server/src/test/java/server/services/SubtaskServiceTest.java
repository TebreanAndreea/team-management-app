package server.services;

import commons.Card;
import commons.Listing;
import commons.SubTask;
import commons.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.context.request.async.DeferredResult;
import server.database.SubTaskRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class SubtaskServiceTest {

    SubtaskService subtaskService;
    SubTaskRepository repo;

    DeferredResult<ResponseEntity<SubTask>> result;
    @Mock
    SimpMessageSendingOperations msgs;
    Card card;
    SubTask subTask;

    @BeforeEach
    void setUp() {
        repo = mock(SubTaskRepository.class);
        msgs = mock(SimpMessageSendingOperations.class);
        subtaskService = new SubtaskService(repo,msgs);

        // setup a card
        List<Tag> tags = new ArrayList<>();
        Tag tag1 = new Tag("tag1");
        tags.add(tag1);

        List<SubTask> subtasks = new ArrayList<>();
        subTask = new SubTask("s1",card);
        subtasks.add(subTask);

        result = subtaskService.getUpdatesSubtasks();
        Listing list = new Listing("list",null);

        card = new Card("desc","name",new Date(),tags,subtasks,list,"black","black","name");
    }

    @Test
    void add() {
        when(repo.save(subTask)).thenReturn(subTask);

        SubTask save = subtaskService.add(subTask).getBody();

        verify(msgs).convertAndSend("/topic/subtask", subTask);
        verify(repo).save(subTask);
        ResponseEntity<SubTask> response = (ResponseEntity<SubTask>) result.getResult();


        assertEquals(response.getBody(),  subTask);
        assertEquals(save, subTask);
    }

    @Test
    void addInvalid() {

        ResponseEntity<SubTask> response= subtaskService.add(null);

        verify(msgs, never()).convertAndSend("/topic/subtask", subTask);
        verify(repo, never()).save(subTask);


        assertEquals(response, ResponseEntity.badRequest().build());
    }

    @Test
    void getCard() {
        Card card1 = subtaskService.getCard(card).getBody();
        assertEquals(card1,card);
    }

    @Test
    void updateSubtask() {
        when(repo.save(subTask)).thenReturn(subTask);

        SubTask save = subtaskService.updateSubtask(subTask).getBody();

        verify(msgs).convertAndSend("/topic/subtask", subTask);
        verify(repo).save(subTask);
        ResponseEntity<SubTask> response = (ResponseEntity<SubTask>) result.getResult();


        assertEquals(response.getBody(),  subTask);
        assertEquals(save, subTask);
    }

    @Test
    void updateInvalid() {

        ResponseEntity<SubTask> response= subtaskService.updateSubtask(null);

        verify(msgs, never()).convertAndSend("/topic/subtask", subTask);
        verify(repo, never()).save(subTask);


        assertEquals(response, ResponseEntity.badRequest().build());
    }

    @Test
    void delete() {

        when(repo.findById(0L)).thenReturn(Optional.of(subTask));

        doNothing().when(repo).deleteById(0L);

        ResponseEntity<Listing> response = subtaskService.delete(0L);
        verify(msgs).convertAndSend("/topic/subtask", subTask);
        verify(repo).findById(0L);
        verify(repo).deleteById(0L);
        ResponseEntity<SubTask> responseI = (ResponseEntity<SubTask>) result.getResult();


        assertEquals(responseI.getBody(),  subTask);
        assertEquals(ResponseEntity.ok().build(), response);
    }

    @Test
    void deleteInvalid() {

        when(repo.findById(0L)).thenReturn(Optional.empty());
        ResponseEntity<Listing> bbb = subtaskService.delete(0L);
        verify(repo).findById(0L);
        verify(repo,never()).deleteById(0L);
        verify(msgs, never()).convertAndSend("/topic/subtask", subTask);

        assertEquals(bbb, ResponseEntity.notFound().build());
    }

    @Test
    void getById() {
        when(repo.existsById(1L)).thenReturn(true);
        when(repo.findById(1L)).thenReturn(Optional.of(subTask));

        SubTask subTask1 = subtaskService.getById(1L).getBody();
        verify(repo).existsById(1L);
        verify(repo).findById(1L);

        assertEquals(subTask1, subTask);
    }

    @Test
    void getByIdInvalid() {
        when(repo.existsById(1L)).thenReturn(false);

        SubTask subTask1 = subtaskService.getById(1L).getBody();
        verify(repo).existsById(1L);
        verify(repo, never()).findById(1L);

        assertNull(subTask1);
    }

    @Test
    void getUpdatesSubtasks() {
        result = subtaskService.getUpdatesSubtasks();
        assertNull(result.getResult());
    }
}