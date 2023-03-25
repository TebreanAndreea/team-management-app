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
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import server.database.BoardRepository;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
public class BoardSavingController {


    private final BoardRepository repo;
    private SimpMessagingTemplate msgs;


    public BoardSavingController(BoardRepository repo, SimpMessagingTemplate msgs) {
        this.repo = repo;
        this.msgs = msgs;
    }

    @GetMapping(path = { "", "/" })
    public List<Board> getAll() {
        return repo.findAll();
    }

//        THIS TEMPLATE NEEDS TO BE ADDED TO EVERY POSTMAPPING BEFORE REPO.SAVE
//        msgs.convertAndSend("/topic/quotes", quote);
    @PostMapping(path = {"", "/"})
    public ResponseEntity<Board> add(@RequestBody Board board) {
        msgs.convertAndSend("/topic/boards", board);
        Board save = repo.save(board);
        save.setAccessKey();
        save = repo.save(save);
        return ResponseEntity.ok(save);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Board> getById(@PathVariable("id") long id) {
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repo.findById(id).get());
    }
}