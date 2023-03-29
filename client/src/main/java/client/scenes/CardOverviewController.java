package client.scenes;

import client.MyFXML;
import client.MyModule;
import client.utils.ServerUtils;
import com.google.inject.Injector;
import commons.Board;
import commons.Card;
import commons.Listing;
import commons.SubTask;
import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import  javafx.scene.control.Label;
import  javafx.scene.control.TextArea;

import javax.inject.Inject;
import java.io.IOException;

import static com.google.inject.Guice.createInjector;

public class CardOverviewController {
    private Stage primaryStage;
    private Scene overview;
    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);
    private long cardId;
    private ServerUtils server;

    public VBox vBox;
    public Listing list;
    @FXML
    public Label cardLabel;
    @FXML
    public TextArea description;

    Board board = new Board("test", "", "");
    private String fileName = "user_files/temp.txt";

    /**
     * Setter for the list.
     *
     * @param list the list of the card
     */
    public void setList(Listing list) {
        this.list = list;
    }

    /**
     * Setter for the board.
     *
     * @param board the board to be set
     */
    public void setBoard(Board board) {
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

    @Inject
    public CardOverviewController(ServerUtils server) {
        this.server = server;
    }
    public CardOverviewController(){

    }

    /**
     * Setter for the current card.
     *
     * @param cardId the card's id
     */
    public void setCardId(long cardId) {
        this.cardId = cardId;
    }

    /**
     * Function that goes from the card details back to the board.
     *
     * @param actionEvent the action event on the button
     * @throws IOException the exception which might be caused
     */

    public void switchToBoardScene(javafx.event.ActionEvent actionEvent) throws IOException {
        var cardOverview = FXML.load(BoardOverviewController.class, "client", "scenes", "BoardOverview.fxml");
        cardOverview.getKey().setFileName(fileName);
        cardOverview.getKey().setBoard(board);
        cardOverview.getKey().refresh();
        primaryStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        overview = new Scene(cardOverview.getValue());
        primaryStage.setScene(overview);
        primaryStage.show();
    }

    /**
     * Adding/Updating a task description.
     *
     * @param actionEvent the event
     */
    public void addDescription(javafx.event.ActionEvent actionEvent){
        Card card = server.getCardsById(cardId);
        String text = description.getText();
        card.setDescription(text);
        server.sendList(list);
        server.updateCardDescription(cardId, text);
        refreshCardDetails();
    }

    /**
     * Updating the card's name.
     *
     * @param actionEvent the event
     */
    public void updateName(javafx.event.ActionEvent actionEvent){
        Card card = server.getCardsById(cardId);
        TextInputDialog dialog = new TextInputDialog(card.getName());
        dialog.setTitle("Change the name of the card");
        dialog.setHeaderText("Please enter the new name for the card:");
        dialog.showAndWait().ifPresent(name -> {

            if(!name.isEmpty()) {
                card.setName(name);
                server.sendList(list);
                server.updateCard(cardId, name);
            } else {
                Alert emptyField = new Alert(Alert.AlertType.ERROR);
                emptyField.setContentText("Name field was submitted empty, please enter a name");
                emptyField.showAndWait();
                updateName(actionEvent);
            }

        });
        refreshCardDetails();
    }

    /**
     * Method for addSubTask button in Card Details scene.
     */
    public void addSubTask(){
        Card card = server.getCardsById(cardId);

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("SubTask name");
        dialog.setHeaderText("Please enter the name of the subtask");
        dialog.showAndWait().ifPresent(name -> {

            if(!name.isEmpty()){
                SubTask newSubTask = new SubTask(name, card);
                System.out.println("Subtask added: " + newSubTask.getTitle());
                saveSubtaskDB(newSubTask);
            } else {
                Alert emptyField = new Alert(Alert.AlertType.ERROR);
                emptyField.setContentText("Name field was submitted empty, please enter a name");
                emptyField.showAndWait();
                addSubTask();
            }
        });
    }

    /**
     * A method that saves the subtask into the database.
     *
     * @param subTask - the subtask that needs saving
     */
    public void saveSubtaskDB(SubTask subTask) {
        try {
            server.saveSubtask(subTask);
        } catch (WebApplicationException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }


    /**
     * Refreshing a card's details.
     *
     */
    public void refreshCardDetails(){
        Card card = server.getCardsById(cardId);
        cardLabel.setText(card.getName());
        description.setText(card.getDescription());
    }
}
