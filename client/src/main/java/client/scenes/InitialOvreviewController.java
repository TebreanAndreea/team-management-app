package client.scenes;

import client.MyFXML;
import client.MyModule;
import client.utils.ServerUtils;
import com.google.inject.Injector;
import commons.Board;
import javafx.event.ActionEvent;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
//import javafx.geometry.Bounds;
import javafx.scene.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
//import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
//import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
//import javafx.scene.transform.Scale;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;


import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.inject.Guice.createInjector;

public class InitialOvreviewController {

    private Stage primaryStage;
    private Scene overview;

    @FXML
    public VBox vBoxBoard;

    @FXML
    public ScrollPane scrollPane;

    @FXML
    public ScrollPane masterScrollPane;
    private ServerUtils server;

    private EventTarget target;

    private Map<Button, Board> boardsMap;

    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);

    private int curPlacedBoards;

    @Inject
    public InitialOvreviewController(ServerUtils server) {
        this.server = server;
        curPlacedBoards = 0;
        boardsMap = new HashMap<>();
    }

    /**
     * The initial method to load up all boards.
     */
    public void initialize()
    {
        refresh();
    }

    /**
     * The method switches to one board which was chosen.
     * @param actionEvent - the event that was triggered when a user decided which board he wants
     */
    public void switchToBoard (ActionEvent actionEvent)
    {
        Button goToBoard = (Button) actionEvent.getSource();
        var boardOverview = FXML.load(BoardOverviewController.class, "client", "scenes", "BoardOverview.fxml");
        boardOverview.getKey().setBoard(boardsMap.get(goToBoard));
        boardOverview.getKey().refresh();
        primaryStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        overview = new Scene(boardOverview.getValue());
        primaryStage.setScene(overview);
    }



    /**
     * A method that adds a board to the db and UI.
     * @param actionEvent - the event that was triggered by the add board button
     */
    public void addBoard(ActionEvent actionEvent) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Board title");
        dialog.setHeaderText("Please enter a name for the board:");
        dialog.showAndWait().ifPresent(name -> {
            server.addBoard(new Board(name,"",""));
            refresh();
        });
    }

    /**
     * Join a board by the access key.
     * @param actionEvent - the event triggered
     */
    public void searchViaKey(ActionEvent actionEvent) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Access key");
        dialog.setHeaderText("Please enter your access key:");
        dialog.showAndWait().ifPresent(key -> {
            if (key.length() < 10 || key.length() > 10)
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Input Error");
                alert.setHeaderText(null);
                alert.setContentText("Invalid access key.");
                alert.showAndWait();
            }

            List<Board> boards = server.getBoardsFromDB();
            for (Board b : boards)
            {
                if (key.equals(b.getAccessKey()))
                {
                    var boardOverview = FXML.load(BoardOverviewController.class, "client", "scenes", "BoardOverview.fxml");
                    boardOverview.getKey().setBoard(b);
                    boardOverview.getKey().refresh();
                    primaryStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                    overview = new Scene(boardOverview.getValue());
                    primaryStage.setScene(overview);
                    return;
                }
            }

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Input Error");
            alert.setHeaderText(null);
            alert.setContentText("Invalid access key.");
            alert.showAndWait();
        });

    }

    /**
     * A method that refreshes and puts each board in the UI.
     */
    public void refresh ()
    {
        vBoxBoard.getChildren().clear();
        masterScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);


        List<Board> boards = server.getBoardsFromDB();
        HBox hbox = new HBox();
        for (int i = 0; i < boards.size(); i++ )
        {
            if (i % 3 == 0) {
                hbox = new HBox();
                vBoxBoard.getChildren().add(hbox);
                hbox.setPrefHeight(70);
                hbox.setSpacing(20);
            }
            Button newBoard = new Button();
            newBoard.setMaxSize(90, 60);
            newBoard.setMinSize(90,60);
            newBoard.setText(boards.get(i).getTitle());
            newBoard.setOnAction(this::switchToBoard);
            normalStyle(newBoard);

            boardsMap.put(newBoard, boards.get(i));
            newBoard.setOnMouseEntered(event -> {
                var root = FXML.load(BoardOverviewController.class, "client", "scenes", "BoardOverview.fxml");
                root.getKey().setBoard(boardsMap.get(newBoard));
                root.getKey().refresh();
                SubScene subScene = new SubScene(root.getValue(), 600, 400);
                scrollPane.setContent(subScene);
                double scaleFactor = Math.min(250 / subScene.getWidth(), 238 / subScene.getHeight());
                Scale scale = new Scale(scaleFactor, scaleFactor);
                subScene.getTransforms().clear();
                subScene.getTransforms().add(scale);
                hoverStyle(newBoard);
            });
            newBoard.setOnMouseExited(event -> {
                scrollPane.setContent(null);
                normalStyle(newBoard);

            });
            hbox.getChildren().add(newBoard);
        }

    }

    public void hoverStyle (Button button)
    {
        button.setStyle("-fx-border-width: 5px;" +
            "-fx-background-color: white;" +
            "-fx-border-color: #656565;" +
            "-fx-text-fill: #4a4ad5;" +
            "-fx-font-family: 'Segoe Script';" +
            "-fx-font-size: 10 px;" +
            "-fx-rotate: 350;" +
            "-fx-font-weight: bolder");
    }

    public void normalStyle (Button button)
    {
        button.setStyle("-fx-border-width: 3px;" +
            "-fx-background-color: white;" +
            "-fx-border-color: gray;" +
            "-fx-text-fill: #4a4ad5;" +
            "-fx-font-family: 'Segoe Script';" +
            "-fx-font-size: 10 px;");
    }
}
