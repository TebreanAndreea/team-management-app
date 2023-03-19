package server.api;

import commons.SubTask;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.database.SubTaskRepository;

@RestController
@RequestMapping("api/subtask")
public class SubtaskSavingController {
    private final SubTaskRepository repo;


    public SubtaskSavingController(SubTaskRepository repo) {
        this.repo = repo;
    }

    /**
     * A method that saves a subtask into the db.
     * @param subTask - the subtask we are saving
     * @return subtask
     */
    @PostMapping(path = {"", "/"})
    public ResponseEntity<SubTask> add(SubTask subTask)
    {
        SubTask save = repo.save(subTask);
        return ResponseEntity.ok(save);
    }
}
