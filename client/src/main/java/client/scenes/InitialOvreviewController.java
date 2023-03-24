package client.scenes;

import client.MyFXML;
import client.MyModule;
import client.utils.ServerUtils;
import com.google.inject.Injector;
import commons.Board;
import javafx.event.ActionEvent;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
//import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
//import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.inject.Guice.createInjector;

public class InitialOvreviewController {

    private Stage primaryStage;
    private Scene overview;
    public HBox hBox;

    @FXML
    public VBox vBoxBoard;
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
    public void switchtoBoard (ActionEvent actionEvent)
    {
        Button goToBoard = (Button) actionEvent.getSource();
        var boardOverview = FXML.load(BoardOverviewController.class, "client", "scenes", "BoardOverview.fxml");
        boardOverview.getKey().setBoard(boardsMap.get(goToBoard));
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
    }

    /**
     * A method that refreshes and puts each board in the UI.
     */
    public void refresh ()
    {
        vBoxBoard.getChildren().clear();
        List<Board> boards = server.getBoardsFromDB();
        HBox hbox = new HBox();
        for (int i = 0; i < boards.size(); i++ )
        {
            if (i % 5 == 0) {
                hbox = new HBox();
                vBoxBoard.getChildren().add(hbox);
                hbox.setPrefHeight(70);
                hbox.setSpacing(20);
            }
            Button newBoard = new Button();
            newBoard.setPrefHeight(70);
            newBoard.setPrefWidth(80);
            newBoard.setText(boards.get(i).getTitle());
            newBoard.setOnAction(this::switchtoBoard);
            hbox.getChildren().add(newBoard);
            boardsMap.put(newBoard, boards.get(i));
        }

    }
}
