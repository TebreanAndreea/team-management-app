package client.utils.test;

import client.utils.ServerUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import commons.Board;
import commons.Card;
import commons.Listing;
import commons.SubTask;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.verify.VerificationTimes.once;

public class ServerUtilsTest {
    private static final int MOCK_SERVER_PORT = 8080;
    private static ClientAndServer mockServer;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private ServerUtils serverUtils;

    @BeforeAll
    public static void openConnection() {
        mockServer = startClientAndServer(MOCK_SERVER_PORT);
    }

    @AfterAll
    public static void closeConnection() {
        mockServer.stop();
    }

    @BeforeEach
    public void setUp() {
        serverUtils = new ServerUtils();
        serverUtils.setSERVER("http://localhost:" + MOCK_SERVER_PORT);
    }

    /**
     * Test for the getBoardsFromDB method.
     *
     * @throws JsonProcessingException - if the object cannot be converted to JSON
     */
    @Test
    public void testGetBoardsFromDB() throws JsonProcessingException {
        List<Board> expectedBoards = List.of(
                new Board("Board1", "123", "456"),
                new Board("Board2", "789", "101"),
                new Board("Board3", "112", "131")
        );
        mockServer.when(request().withMethod("GET").withPath("/api/boards"))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(expectedBoards)));
        List<Board> actualBoards = serverUtils.getBoardsFromDB();
        assertEquals(expectedBoards, actualBoards);
    }

    /**
     * Test for the getBoardByID method.
     *
     * @throws JsonProcessingException - if the object cannot be converted to JSON
     */
    @Test
    public void testGetBoardByID() throws JsonProcessingException {
        Board expectedBoard = new Board("Board1", "123", "456");
        mockServer.when(request().withMethod("GET").withPath("/api/boards/1"))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(expectedBoard)));
        Board actualBoard = serverUtils.getBoardByID(1);
        assertEquals(expectedBoard, actualBoard);
    }

    /**
     * Test for the getCardsById method.
     *
     * @throws JsonProcessingException - if the object cannot be converted to JSON
     */
    @Test
    public void testGetCardByID() throws JsonProcessingException {
        Card card = new Card("description", "name", Date.from(Instant.EPOCH),
                new ArrayList<>(), new ArrayList<>(), null, "", "");
        mockServer.when(request().withMethod("GET").withPath("/api/card/1"))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(card)));
        Card actualCard = serverUtils.getCardsById(1);
        assertEquals(card, actualCard);
    }

    /**
     * Test for the getListingsById method.
     *
     * @throws JsonProcessingException - if the object cannot be converted to JSON
     */
    @Test
    public void testGetListingsByID() throws JsonProcessingException {
        Listing listing = new Listing("title", null);
        mockServer.when(request().withMethod("GET").withPath("/api/lists/1"))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(listing)));
        Listing actualListing = serverUtils.getListingsById(1);
        assertEquals(listing, actualListing);
    }

    @Test
    public void testDeleteBoard() {
        // Setup
        mockServer.when(request()
                        .withMethod("DELETE")
                        .withPath("/api/boards/123"))
                .respond(response()
                        .withStatusCode(200));

        // Method call
        serverUtils.deleteBoard(123L);

        // Verification
        mockServer.verify(request()
                .withMethod("DELETE")
                .withPath("/api/boards/123"), once());
    }

    @Test
    public void testDeleteListing() {
        // Setup
        mockServer.when(request()
                        .withMethod("DELETE")
                        .withPath("/api/lists/123"))
                .respond(response()
                        .withStatusCode(200));

        // Method call
        serverUtils.deleteList(123L);

        // Verification
        mockServer.verify(request()
                .withMethod("DELETE")
                .withPath("/api/lists/123"), once());
    }

    /**
     * Test for the deleteCard method.
     * <p> This test verifies that the deleteCard method makes a DELETE request to the correct path. </p>
     */
    @Test
    public void testDeleteCard() {
        // Setup
        mockServer.when(request()
                        .withMethod("DELETE")
                        .withPath("/api/card/delete/123"))
                .respond(response()
                        .withStatusCode(200));

        // Method call
        serverUtils.deleteCard(123);

        // Verification
        mockServer.verify(request()
                .withMethod("DELETE")
                .withPath("/api/card/delete/123"), once());
    }

    @Test
    public void deleteSubtask() {
        // Setup
        mockServer.when(request()
                        .withMethod("DELETE")
                        .withPath("/api/subtask/delete/123"))
                .respond(response()
                        .withStatusCode(200));

        // Method call
        SubTask subTask = new SubTask();
        subTask.setStId(123);
        serverUtils.deleteSubtask(subTask);

        // Verification
        mockServer.verify(request()
                .withMethod("DELETE")
                .withPath("/api/subtask/delete/123"), once());
    }

}