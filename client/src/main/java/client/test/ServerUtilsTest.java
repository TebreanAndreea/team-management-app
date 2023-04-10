package client.test;

import client.utils.ServerUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import commons.Tag;
import commons.*;
import org.junit.jupiter.api.*;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.JsonBody;
import org.mockserver.verify.VerificationTimes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockserver.model.JsonBody.json;
import static org.mockserver.verify.VerificationTimes.once;

public class ServerUtilsTest {
    private static final int MOCK_SERVER_PORT = 8080;
    private static ClientAndServer mockServer;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private ServerUtils serverUtils;

    @BeforeAll
    public static void openConnection() {
        mockServer = ClientAndServer.startClientAndServer(MOCK_SERVER_PORT);
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

    // --------------------- TESTS FOR THE GET METHODS --------------------------------

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
        mockServer.when(HttpRequest.request().withMethod("GET").withPath("/api/boards"))
                .respond(HttpResponse.response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(expectedBoards)));
        List<Board> actualBoards = serverUtils.getBoardsFromDB();
        Assertions.assertEquals(expectedBoards, actualBoards);
    }

    /**
     * Test for the getBoardByID method.
     *
     * @throws JsonProcessingException - if the object cannot be converted to JSON
     */
    @Test
    public void testGetBoardByID() throws JsonProcessingException {
        Board expectedBoard = new Board("Board1", "123", "456");
        mockServer.when(HttpRequest.request().withMethod("GET").withPath("/api/boards/1"))
                .respond(HttpResponse.response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(expectedBoard)));
        Board actualBoard = serverUtils.getBoardByID(1);
        Assertions.assertEquals(expectedBoard, actualBoard);
    }

    /**
     * Test for the getCardsById method.
     *
     * @throws JsonProcessingException - if the object cannot be converted to JSON
     */
    @Test
    public void testGetCardByID() throws JsonProcessingException {
        Card card = new Card("description", "name", Date.from(Instant.EPOCH),
                new ArrayList<>(), new ArrayList<>(), null, "", "", null);
        mockServer.when(HttpRequest.request().withMethod("GET").withPath("/api/card/1"))
                .respond(HttpResponse.response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(card)));
        Card actualCard = serverUtils.getCardsById(1);
        Assertions.assertEquals(card, actualCard);
    }

    /**
     * Test for the getListingsById method.
     *
     * @throws JsonProcessingException - if the object cannot be converted to JSON
     */
    @Test
    public void testGetListingsByID() throws JsonProcessingException {
        Listing listing = new Listing("title", null);
        mockServer.when(HttpRequest.request().withMethod("GET").withPath("/api/lists/1"))
                .respond(HttpResponse.response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(listing)));
        Listing actualListing = serverUtils.getListingsById(1);
        Assertions.assertEquals(listing, actualListing);
    }

    // --------------------- TESTS FOR THE DELETE METHODS --------------------------------

    /**
     * Test for the deleteBoard method.
     * <p> This test verifies that the deleteBoard method makes a DELETE request to the correct path. </p>
     */
    @Test
    public void testDeleteBoard() {
        // Setup
        mockServer.when(HttpRequest.request()
                        .withMethod("DELETE")
                        .withPath("/api/boards/123"))
                .respond(HttpResponse.response()
                        .withStatusCode(200));

        // Method call
        serverUtils.deleteBoard(123L);

        // Verification
        mockServer.verify(HttpRequest.request()
                .withMethod("DELETE")
                .withPath("/api/boards/123"), once());
    }

    /**
     * Test for the deleteListing method.
     * <p> This test verifies that the deleteListing method makes a DELETE request to the correct path. </p>
     */
    @Test
    public void testDeleteListing() {
        // Setup
        mockServer.when(HttpRequest.request()
                        .withMethod("DELETE")
                        .withPath("/api/lists/123"))
                .respond(HttpResponse.response()
                        .withStatusCode(200));

        // Method call
        serverUtils.deleteList(123L);

        // Verification
        mockServer.verify(HttpRequest.request()
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
        mockServer.when(HttpRequest.request()
                        .withMethod("DELETE")
                        .withPath("/api/card/delete/123/true"))
                .respond(HttpResponse.response()
                        .withStatusCode(200));

        // Method call
        serverUtils.deleteCard(123,true);

        // Verification
        mockServer.verify(HttpRequest.request()
                .withMethod("DELETE")
                .withPath("/api/card/delete/123/true"), once());
    }

    /**
     * Test for the deleteSubtask method.
     * <p> This test verifies that the deleteSubtask method makes a DELETE request to the correct path. </p>
     */
    @Test
    public void deleteSubtask() {
        // Setup
        mockServer.when(HttpRequest.request()
                        .withMethod("DELETE")
                        .withPath("/api/subtask/delete/123"))
                .respond(HttpResponse.response()
                        .withStatusCode(200));

        // Method call
        SubTask subTask = new SubTask();
        subTask.setStId(123);
        serverUtils.deleteSubtask(subTask);

        // Verification
        mockServer.verify(HttpRequest.request()
                .withMethod("DELETE")
                .withPath("/api/subtask/delete/123"), once());
    }

    /**
     * Test for the deleteTag method.
     * <p> This test verifies that the deleteTag method makes a DELETE request to the correct path. </p>
     */
    @Test
    public void testDeleteTag() {
        // Setup
        mockServer.when(HttpRequest.request()
                        .withMethod("DELETE")
                        .withPath("/api/tag/delete/123"))
                .respond(HttpResponse.response()
                        .withStatusCode(200));

        // Method call
        Tag tag = new Tag();
        tag.setTagId(123);
        serverUtils.deleteTag(123, tag);

        // Verification
        mockServer.verify(HttpRequest.request()
                .withMethod("DELETE")
                .withPath("/api/tag/delete/123"), once());
    }

    /**
     * Test for the deleteScheme method.
     * <p> This test verifies that the deleteScheme method makes a DELETE request to the correct path. </p>
     */
    @Test
    public void testDeleteScheme() {
        // Setup
        mockServer.when(HttpRequest.request()
                        .withMethod("DELETE")
                        .withPath("/api/color/delete/123"))
                .respond(HttpResponse.response()
                        .withStatusCode(200));

        // Method call
        serverUtils.deleteScheme(123L);

        // Verification
        mockServer.verify(HttpRequest.request()
                .withMethod("DELETE")
                .withPath("/api/color/delete/123"), once());
    }

    // --------------------- TESTS FOR THE POST METHODS --------------------------------

    /**
     * Test for the saveBoard method.
     * <p> This test verifies that the saveBoard method makes a POST request to the correct path. </p>
     */
    @Test
    public void testSaveBoard() {
        // Setup
        Board board = new Board("board1", null, null);
        mockServer.when(HttpRequest.request()
                        .withMethod("POST")
                        .withPath("/api/boards")
                        .withBody(json(board)))
                .respond(HttpResponse.response()
                        .withStatusCode(201));

        // Method call
        serverUtils.addBoard(board);

        // Verification
        mockServer.verify(HttpRequest.request()
                .withMethod("POST")
                .withPath("/api/boards")
                .withBody(json(board)), once());
    }

    /**
     * Test for the saveListing method.
     * <p> This test verifies that the saveListing method makes a POST request to the correct path. </p>
     */
    @Test
    public void testSaveListing() {
        // Setup
        Listing listing = new Listing("listing1", null);
        mockServer.when(HttpRequest.request()
                        .withMethod("POST")
                        .withPath("/api/lists")
                        .withBody(json(listing)))
                .respond(HttpResponse.response()
                        .withStatusCode(201));

        // Method call
        serverUtils.saveList(listing);

        // Verification
        mockServer.verify(HttpRequest.request()
                .withMethod("POST")
                .withPath("/api/lists")
                .withBody(json(listing)), once());
    }

    /**
     * Test for the saveCard method.
     * <p> This test verifies that the saveCard method makes a POST request to the correct path. </p>
     */
    @Test
    public void testSaveCard() {
        // Setup
        Card card = new Card("description", "name", Date.from(Instant.EPOCH),
                new ArrayList<>(), new ArrayList<>(), null, "", "", null);
        mockServer.when(HttpRequest.request()
                        .withMethod("POST")
                        .withPath("/api/card/false")
                        .withBody(json(card)))
                .respond(HttpResponse.response()
                        .withStatusCode(201));

        // Method call
        serverUtils.saveCard(card,false);

        // Verification
        mockServer.verify(HttpRequest.request()
                .withMethod("POST")
                .withPath("/api/card/false")
                .withBody(JsonBody.json(card)), VerificationTimes.once());
    }

    /**
     * Test for the saveSubtask method.
     * <p> This test verifies that the saveSubtask method makes a POST request to the correct path. </p>
     */
    @Test
    public void testSaveSubtask() {
        // Setup
        SubTask subTask = new SubTask("subtask1", null);
        mockServer.when(HttpRequest.request()
                        .withMethod("POST")
                        .withPath("/api/subtask")
                        .withBody(JsonBody.json(subTask)))
                .respond(HttpResponse.response()
                        .withStatusCode(201));

        // Method call
        serverUtils.saveSubtask(subTask);

        // Verification
        mockServer.verify(HttpRequest.request()
                .withMethod("POST")
                .withPath("/api/subtask")
                .withBody(JsonBody.json(subTask)), VerificationTimes.once());
    }

    /**
     * Test for the saveTag method.
     * <p> This test verifies that the saveTag method makes a POST request to the correct path. </p>
     */
    @Test
    public void testSaveTag() {
        // Setup
        Tag tag = new Tag("tag1", null);
        mockServer.when(HttpRequest.request()
                        .withMethod("POST")
                        .withPath("/api/tag")
                        .withBody(JsonBody.json(tag)))
                .respond(HttpResponse.response()
                        .withStatusCode(201));

        // Method call
        serverUtils.saveTag(tag);

        // Verification
        mockServer.verify(HttpRequest.request()
                .withMethod("POST")
                .withPath("/api/tag")
                .withBody(JsonBody.json(tag)), VerificationTimes.once());
    }

    /**
     * Test for the saveColorScheme method.
     * <p> This test verifies that the saveColorScheme method makes a POST request to the correct path. </p>
     */
    @Test
    public void testSaveColorScheme() {
        // Setup
        ColorScheme colorScheme = new ColorScheme("scheme1", "", "", null);
        mockServer.when(HttpRequest.request()
                        .withMethod("POST")
                        .withPath("/api/color")
                        .withBody(JsonBody.json(colorScheme)))
                .respond(HttpResponse.response()
                        .withStatusCode(201));

        // Method call
        serverUtils.saveColorScheme(colorScheme);

        // Verification
        mockServer.verify(HttpRequest.request()
                .withMethod("POST")
                .withPath("/api/color")
                .withBody(JsonBody.json(colorScheme)), VerificationTimes.once());
    }

    /**
     * Test for the sendBoard method.
     * <p> This test verifies that the sendBoard method makes a POST request to the correct path. </p>
     */
    @Test
    public void testSendBoard() {
        // Setup
        Board board = new Board("board1", null, null);
        mockServer.when(HttpRequest.request()
                        .withMethod("POST")
                        .withPath("/api/lists/setBoard")
                        .withBody(JsonBody.json(board)))
                .respond(HttpResponse.response()
                        .withStatusCode(201));

        // Method call
        serverUtils.sendBoard(board);

        // Verification
        mockServer.verify(HttpRequest.request()
                .withMethod("POST")
                .withPath("/api/lists/setBoard")
                .withBody(JsonBody.json(board)), VerificationTimes.once());
    }

    /**
     * Test for the sendBoardToTag method.
     * <p> This test verifies that the sendBoardToTag method makes a POST request to the correct path. </p>
     */
    @Test
    public void testSendBoardToTag() {
        // Setup
        Board board = new Board("board1", null, null);
        mockServer.when(HttpRequest.request()
                        .withMethod("POST")
                        .withPath("/api/tag/setBoard")
                        .withBody(JsonBody.json(board)))
                .respond(HttpResponse.response()
                        .withStatusCode(201));

        // Method call
        serverUtils.sendBoardToTag(board);

        // Verification
        mockServer.verify(HttpRequest.request()
                .withMethod("POST")
                .withPath("/api/tag/setBoard")
                .withBody(JsonBody.json(board)), VerificationTimes.once());
    }

    /**
     * Test for the sendBoardToScheme method.
     * <p> This test verifies that the sendBoardToScheme method makes a POST request to the correct path. </p>
     */
    @Test
    public void testSendBoardToScheme() {
        // Setup
        Board board = new Board("board1", null, null);
        mockServer.when(HttpRequest.request()
                        .withMethod("POST")
                        .withPath("/api/color/setBoard")
                        .withBody(JsonBody.json(board)))
                .respond(HttpResponse.response()
                        .withStatusCode(201));

        // Method call
        serverUtils.sendBoardToScheme(board);

        // Verification
        mockServer.verify(HttpRequest.request()
                .withMethod("POST")
                .withPath("/api/color/setBoard")
                .withBody(JsonBody.json(board)), VerificationTimes.once());
    }
    /**
     * Test for the sendList method.
     * <p> This test verifies that the sendList method makes a POST request to the correct path. </p>
     */
    @Test
    public void testSendList(){
        // Setup
        Listing listing = new Listing("listing1", null);
        mockServer.when(HttpRequest.request()
                        .withMethod("POST")
                        .withPath("/api/card/setList")
                        .withBody(JsonBody.json(listing)))
                .respond(HttpResponse.response()
                        .withStatusCode(201));

        // Method call
        serverUtils.sendList(listing);

        // Verification
        mockServer.verify(HttpRequest.request()
                .withMethod("POST")
                .withPath("/api/card/setList")
                .withBody(JsonBody.json(listing)), VerificationTimes.once());
    }

    /**
     * Test for the editList method.
     * <p> This test verifies that the editList method makes a POST request to the correct path. </p>
     */
    @Test
    public void testEditList(){
        // Setup
        Listing listing = new Listing("listing1", null);
        mockServer.when(HttpRequest.request()
                        .withMethod("POST")
                        .withPath("/api/lists/edit")
                        .withBody(JsonBody.json(listing)))
                .respond(HttpResponse.response()
                        .withStatusCode(201));

        // Method call
        serverUtils.editList(listing);

        // Verification
        mockServer.verify(HttpRequest.request()
                .withMethod("POST")
                .withPath("/api/lists/edit")
                .withBody(JsonBody.json(listing)), VerificationTimes.once());
    }

    /**
     * Test for the updateSubtask method.
     * <p> This test verifies that the updateSubtask method makes a POST request to the correct path. </p>
     */
    @Test
    public void testUpdateSubtask(){
        // Setup
        SubTask subTask = new SubTask("subtask1", null);
        mockServer.when(HttpRequest.request()
                        .withMethod("POST")
                        .withPath("/api/subtask/edit")
                        .withBody(JsonBody.json(subTask)))
                .respond(HttpResponse.response()
                        .withStatusCode(201));

        // Method call
        serverUtils.updateSubtask(subTask, "subtask1");

        // Verification
        mockServer.verify(HttpRequest.request()
                .withMethod("POST")
                .withPath("/api/subtask/edit")
                .withBody(JsonBody.json(subTask)), VerificationTimes.once());
    }


}