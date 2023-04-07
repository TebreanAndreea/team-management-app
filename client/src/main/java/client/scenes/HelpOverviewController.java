package client.scenes;

import client.MyFXML;
import client.MyModule;
import com.google.inject.Injector;
import commons.Board;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import static com.google.inject.Guice.createInjector;

public class HelpOverviewController {

    private Stage primaryStage;
    private Scene overview;

    private Board board;
    private String fileName = "user_files/temp.txt";
    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);

    /**
     * Setter for the board.
     * @param board the board
     */
    public void setBoard(Board board){
        this.board = board;
    }

    /**
     * Setter for the file name.
     *
     * @param fileName the name of the file where the user's boards are stored
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Going back to the board overview.
     * @param actionEvent the event
     * @throws IOException the exception
     */
    public void switchToBoardScene(javafx.event.ActionEvent actionEvent) throws IOException {
        var boardOverview = FXML.load(BoardOverviewController.class, "client", "scenes", "BoardOverview.fxml");
        boardOverview.getKey().setFileName(fileName);
        boardOverview.getKey().setBoard(board);
        boardOverview.getKey().refresh();
        primaryStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        overview = new Scene(boardOverview.getValue());
        primaryStage.setScene(overview);
        primaryStage.show();
    }
}
