package server.api;

import commons.Card;
import commons.SubTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SubTaskSavingControllerTest {
    private SubtaskSavingController controller;
    private TestSubTaskRepository subTaskRepositor;
    private TestSimpMessageTemplate msgs;

    @BeforeEach
    public void setup() {
        subTaskRepositor = new TestSubTaskRepository();
        msgs = new TestSimpMessageTemplate();
        controller = new SubtaskSavingController(subTaskRepositor, msgs);
    }
    @Test
    public void addNullTest() {
        var result = controller.add(null);
        assertEquals(ResponseEntity.badRequest().build(), result);
    }
    @Test
    public void addTest() {
        SubTask subtask = new SubTask();
        controller.add(subtask);
        assertEquals(subtask, subTaskRepositor.findAll().get(0));
    }
    @Test
    public void getCardTest() {
        Card card = new Card();
        assertEquals(ResponseEntity.ok(card), controller.getCard(card));
    }
    @Test
    public void deleteNotExistingListTest() {
        SubTask subTask = new SubTask();
        assertEquals(ResponseEntity.notFound().build(), controller.delete(subTask.getStId()));
    }
    @Test
    public void deleteListTest() {
        SubTask subTask = new SubTask();
        subTaskRepositor.save(subTask);
        controller.delete(subTask.getStId());
        assertEquals(0, subTaskRepositor.findAll().size());
    }
    @Test
    public void getByIdTest() {
        SubTask subTask = new SubTask();
        subTaskRepositor.save(subTask);
        long id = subTask.getStId();
        assertEquals(ResponseEntity.ok(subTask), controller.getById(id));
    }
    @Test
    public void getByBadIdTest() {
        assertEquals(ResponseEntity.badRequest().build(), controller.getById(-1));
    }
    @Test
    public void updateSubTaskTest() {
        SubTask subTask = new SubTask();
        subTaskRepositor.save(subTask);
        assertEquals(ResponseEntity.ok(subTask), controller.updateSubtask(subTask));
    }
    @Test
    public void updateSubTaskNullTest() {
        assertEquals(ResponseEntity.badRequest().build(), controller.updateSubtask(null));
    }

    @Test
    public void getUpdates()
    {
        assertNull(controller.getUpdatesSubtasks().getResult());
    }
}
