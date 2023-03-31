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
import server.services.BoardService;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
public class BoardSavingController {


//    private final BoardRepository repo;
//    private SimpMessageSendingOperations msgs;

    private BoardService boardService;

    /**
     * Constructor for Board controller.
     *
     * @param repo - board repository
     * @param msgs - messages for communication
     */
    public BoardSavingController(BoardRepository repo, SimpMessageSendingOperations msgs) {
        this.boardService = new BoardService(repo, msgs);
    }

    /**
     * Get method for fetching all boards from DB.
     *
     * @return - a list of boards from DB
     */
    @GetMapping(path = { "", "/" })
    public List<Board> getAll() {
        return boardService.getAll();
    }

    /**
     * Post method that adds a board into the DB.
     *
     * @param board - board to be added into the database
     * @return the saved board
     */
    @PostMapping(path = {"", "/"})
    public ResponseEntity<Board> add(@RequestBody Board board) {
        return boardService.add(board);
    }

    /**
     * Get method for fetching a board by id.
     *
     * @param id - to search for board into DB
     * @return the query result - board corresponding to id
     */
    @GetMapping("/{id}")
    public ResponseEntity<Board> getById(@PathVariable("id") long id) {
        return boardService.getById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Board> delete(@PathVariable("id") Long id) {
        Board board = repo.findById(id).orElse(null);
        if (board == null) {
            return ResponseEntity.notFound().build();
        }
        msgs.convertAndSend("/topic/boards", board);
        repo.deleteById(id);
        return ResponseEntity.ok().build();
    }
}