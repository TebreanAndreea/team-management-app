package client.scenes;

import client.MyFXML;
import client.MyModule;
import client.utils.ServerUtils;
import com.google.inject.Injector;
import commons.Board;
import commons.Card;
import commons.Listing;
import jakarta.ws.rs.WebApplicationException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.SubScene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import javafx.stage.Modality;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.google.inject.Guice.createInjector;

public class AdminOverviewController {
    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);
    private final ServerUtils server;
    private Map<Button, Board> buttonBoardMap = new HashMap<>();
    private Board selectedBoard;
    @FXML
    private javafx.scene.control.ScrollPane previewPane;
    @FXML
    private VBox boardBox;
    @FXML
    private Button deleteButton;

    /**
     * Constructor which initializes the server.
     *
     * @param server the server instance used for communication
     */

    @Inject
    public AdminOverviewController(ServerUtils server) {
        this.server = server;
    }

    /**
     * Initializes the controller.
     */
    public void initialize() {
        server.registerForMessages("/topic/boards", Board.class, q -> Platform.runLater(this::refresh));
        refresh();
        deleteButton.setVisible(false);
    }

    /**
     * refreshes the overview.
     */
    public void refresh() {
        boardBox.getChildren().clear();
        buttonBoardMap = new HashMap<>();
        List<Board> boards = server.getBoardsFromDB();
        for (Board board : boards) {
            Button button = new Button(board.getTitle());
            setupButton(button, board);
        }
    }

    /**
     * Finds a board by a given title.
     *
     * @param keyEvent the event that triggered the method
     */
    public void findByText(KeyEvent keyEvent) {
        search(((javafx.scene.control.TextField) keyEvent.getSource()).getText().trim());
    }

    /**
     * Searches for a board with a given title.
     *
     * @param searchCriteria the title to search for
     */
    private void search(String searchCriteria) {
        boardBox.getChildren().clear();
        buttonBoardMap = new HashMap<>();
        List<Board> boards = server.getBoardsFromDB();
        for (Board board : boards) {
            if (board.getTitle().contains(searchCriteria)) {
                Button button = new Button(board.getTitle());
                setupButton(button, board);
            }
        }
    }

    /**
     * Sets up a button for a board.
     *
     * @param button the button to set up
     * @param board  the board to set up the button for
     */
    private void setupButton(Button button, Board board) {
        button.setOnAction(this::seePreview);
        button.setStyle("-fx-background-color: #FFFFFF;");
        button.setPrefSize(133, 60);
        button.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(10), BorderWidths.DEFAULT)));
        buttonBoardMap.put(button, board);
        boardBox.getChildren().add(button);
    }

    /**
     * Shows a preview of the selected board.
     *
     * @param actionEvent the event that triggered the method
     */
    private void seePreview(ActionEvent actionEvent) {
        var root = FXML.load(BoardOverviewController.class, "client", "scenes", "BoardOverview.fxml");
        root.getKey().setBoard(buttonBoardMap.get((Button) actionEvent.getSource()));
        root.getKey().setAdminControl(true);
        root.getKey().refresh();
        SubScene subScene = new SubScene(root.getValue(), 600, 400);
        previewPane.setContent(subScene);
        double scaleFactor = Math.min(400 / subScene.getWidth(), 300 / subScene.getHeight());
        Scale scale = new Scale(scaleFactor, scaleFactor);
        subScene.getTransforms().clear();
        subScene.getTransforms().add(scale);
        previewPane.setFitToHeight(true);
        previewPane.setFitToWidth(true);
        previewPane.setHbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);
        previewPane.setVbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);
        deleteButton.setVisible(true);
        selectedBoard = buttonBoardMap.get((Button) actionEvent.getSource());
    }

    /**
     * Deletes the selected board from the database.
     * If a user is using the board, the user will be shown a message that the board has been deleted.
     */
    public void delete() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete board");
        alert.setHeaderText("Are you sure you want to delete this board?");
        alert.setContentText("This action cannot be undone.\n Board name: " + selectedBoard.getTitle());
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            for (Listing list : selectedBoard.getLists()) {
                for (int i = 0; i < list.getCards().size(); i++) {
                    Card card = list.getCards().get(i);
                    try {
                        server.deleteCard(card.getCardId());
                    } catch (WebApplicationException e) {
                        var alertError = new Alert(Alert.AlertType.ERROR);
                        alertError.initModality(Modality.APPLICATION_MODAL);
                        alertError.setContentText(e.getMessage());
                        alertError.showAndWait();
                        return;
                    }
                }

                try {
                    server.deleteList(list.getListId());
                } catch (WebApplicationException e) {
                    var alertError = new Alert(Alert.AlertType.ERROR);
                    alertError.initModality(Modality.APPLICATION_MODAL);
                    alertError.setContentText(e.getMessage());
                    alertError.showAndWait();
                }
            }

            try {
                SubScene subScene = (SubScene) previewPane.getContent();
                subScene.setRoot(new Label("When selecting a board, a preview will be shown here."));
                server.deleteBoard(selectedBoard.getBoardId());
            } catch (WebApplicationException e) {
                var alertError = new Alert(Alert.AlertType.ERROR);
                alertError.initModality(Modality.APPLICATION_MODAL);
                alertError.setContentText(e.getMessage());
                alertError.showAndWait();
            }
            deleteButton.setVisible(false);
            refresh();
        }
    }

}
