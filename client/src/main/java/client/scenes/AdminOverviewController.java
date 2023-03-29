package client.scenes;

import client.MyFXML;
import client.MyModule;
import client.utils.ServerUtils;
import com.google.inject.Injector;
import commons.Board;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;

import javax.inject.Inject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.inject.Guice.createInjector;

public class AdminOverviewController {
    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);
    private ServerUtils server;
    private Map<Button, Board> buttonBoardMap = new HashMap<>();
    @FXML
    private javafx.scene.control.ScrollPane previewPane;
    @FXML
    private VBox boardBox;

    /**
     * Constructor which initialize the server.
     *
     * @param server the server instance used for communication
     */

    @Inject
    public AdminOverviewController(ServerUtils server) {
        this.server = server;
    }

    public void initialize() {
        refresh();
    }

    public void refresh() {
        boardBox.getChildren().clear();
        buttonBoardMap = new HashMap<>();
        List<Board> boards = server.getBoardsFromDB();
        for (Board board : boards) {
            Button button = new Button(board.getTitle());
            setupButton(button, board);
        }
    }

    public void findByText(KeyEvent keyEvent) {
        System.out.println(keyEvent.getSource().getClass());
        search(((javafx.scene.control.TextField) keyEvent.getSource()).getText().trim());
    }

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

    private void setupButton(Button button, Board board) {
        button.setOnAction(this::seePreview);
        button.setStyle("-fx-background-color: #FFFFFF;");
        button.setPrefSize(133, 60);
        button.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(10), BorderWidths.DEFAULT)));
        buttonBoardMap.put(button, board);
        boardBox.getChildren().add(button);
    }

    private void seePreview(ActionEvent actionEvent) {
        var root = FXML.load(BoardOverviewController.class, "client", "scenes", "BoardOverview.fxml");
        root.getKey().setBoard(buttonBoardMap.get(actionEvent.getSource()));
        root.getKey().refresh();
        SubScene subScene = new SubScene(root.getValue(), 600, 400);
        previewPane.setContent(subScene);
        double scaleFactor = Math.min(400 / subScene.getWidth(), 300 / subScene.getHeight());
        Scale scale = new Scale(scaleFactor, scaleFactor);
        subScene.getTransforms().clear();
        subScene.getTransforms().add(scale);
    }

}
