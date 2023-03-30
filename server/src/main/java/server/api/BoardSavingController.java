/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package server.api;

import commons.Board;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;
import server.database.BoardRepository;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
public class BoardSavingController {


    private final BoardRepository repo;
    private SimpMessageSendingOperations msgs;

    /**
     * Constructor for Board controller.
     *
     * @param repo - board repository
     * @param msgs - messages for communication
     */
    public BoardSavingController(BoardRepository repo, SimpMessageSendingOperations msgs) {
        this.repo = repo;
        this.msgs = msgs;
    }

    /**
     * Get method for fetching all boards from DB.
     *
     * @return - a list of boards from DB
     */
    @GetMapping(path = { "", "/" })
    public List<Board> getAll() {
        return repo.findAll();
    }

    /**
     * Post method that adds a board into the DB.
     *
     * @param board - board to be added into the database
     * @return the saved board
     */
    @PostMapping(path = {"", "/"})
    public ResponseEntity<Board> add(@RequestBody Board board) {
        if(board == null) return ResponseEntity.badRequest().build();
        msgs.convertAndSend("/topic/boards", board);
        Board save = repo.save(board);
        save.setAccessKey();
        save = repo.save(save);
        return ResponseEntity.ok(save);
    }

    /**
     * Get method for fetching a board by id.
     *
     * @param id - to search for board into DB
     * @return the query result - board corresponding to id
     */
    @GetMapping("/{id}")
    public ResponseEntity<Board> getById(@PathVariable("id") long id) {
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repo.findById(id).get());
    }
}