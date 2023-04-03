package client.scenes;

import client.MyFXML;
import client.MyModule;
import client.utils.ServerUtils;
import com.google.inject.Injector;
import commons.Board;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.inject.Inject;

import static com.google.inject.Guice.createInjector;

public class CustomizationOverviewController {
    private Stage primaryStage;
    private Scene overview;
    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);
    private ServerUtils server;
    private Board board=new Board();
    private String fileName = "user_files/temp.txt";

    public ColorPicker boardBackground;
    public ColorPicker boardFont;
    public ColorPicker listBackground;
    public ColorPicker listFont;

    /**
     * Setter for the file name.
     *
     * @param fileName the name of the file where the user's boards are stored
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Inject
    public CustomizationOverviewController(ServerUtils server) {
        this.server = server;
    }

    /**
     * Initializes the controller.
     */
    public void initialize() {
        refresh();
    }

    /**
     * refreshes the overview.
     */
    public void refresh() {
        System.out.println(board.getBoardId());
        boardBackground.setValue(Color.valueOf(board.getBackgroundColor()));
        boardFont.setValue(Color.valueOf(board.getTextColor()));
        listBackground.setValue(Color.valueOf(board.getListBackgroundColor()));
        listFont.setValue(Color.valueOf(board.getListTextColor()));
    }

    /**
     * Resets the colors of the list.
     */
    public void resetListColors(){
        listBackground.setValue(Color.valueOf(board.getListBackgroundColorDefault()));
        listFont.setValue(Color.valueOf(board.getListTextColorDefault()));
    }

    /**
     * Resets the colors of the board.
     */
    public void resetBoardColors(){
        boardBackground.setValue(Color.valueOf(board.getBackgroundColorDefault()));
        boardFont.setValue(Color.valueOf(board.getTextColorDefault()));
    }

    /**
     * Setter for the board.
     * @param board - the board which is being customized
     */
    public void setBoard (Board board) {
        this.board = board;
    }

    /**
     * The method switches to one board which was chosen.
     *
     * @param actionEvent - the event that was triggered when a user decided which board he wants
     */
    public void switchToBoard(ActionEvent actionEvent) {
        Button goToBoard = (Button) actionEvent.getSource();
        var boardOverview = FXML.load(BoardOverviewController.class, "client", "scenes", "BoardOverview.fxml");
        boardOverview.getKey().setFileName(fileName);
        boardOverview.getKey().setBoard(board);
        boardOverview.getKey().refresh();
        primaryStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        overview = new Scene(boardOverview.getValue());
        primaryStage.setScene(overview);
    }

    /**
     * Updates the colors of the board.
     * @param actionEvent - the event that was triggered
     */
    public void updateColors(ActionEvent actionEvent) {
        board.setBackgroundColor("#"+boardBackground.getValue().toString().substring(2));
        board.setTextColor("#"+boardFont.getValue().toString().substring(2));
        board.setListBackgroundColor("#"+listBackground.getValue().toString().substring(2));
        board.setListTextColor("#"+listFont.getValue().toString().substring(2));
        server.addBoard(board);
        refresh();
    }
}
