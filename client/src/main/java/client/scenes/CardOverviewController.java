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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

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

    private Board board = new Board("test", "", "");
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

    /**
     * Setter for the server.
     *
     * @param server - the server to assign this card to
     */
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
        refresh();
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
        refresh();
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
                saveSubtaskDB(newSubTask, card);
            } else {
                Alert emptyField = new Alert(Alert.AlertType.ERROR);
                emptyField.setContentText("Name field was submitted empty, please enter a name");
                emptyField.showAndWait();
                addSubTask();
            }
        });

        refresh();
    }

    /**
     * Editing a subtask.
     *
     * @param actionEvent the action event
     * @param subTask the subtask to be edited
     */
    private void editSubTask(ActionEvent actionEvent, SubTask subTask) {
        Card card = server.getCardsById(cardId);
        //   SubTask subTask = server.getSubtaskById(subtaskId);
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("SubTask new name");
        dialog.setHeaderText("Please enter the new name of the subtask");
        dialog.showAndWait().ifPresent(name -> {
            if(!name.isEmpty()) {
                try {
                    server.sendCard(subTask.getCard());
                    subTask.setTitle(name);
                    server.updateSubtask(subTask, name);
                }catch (WebApplicationException e) {
                    var alert = new Alert(Alert.AlertType.ERROR);
                    alert.initModality(Modality.APPLICATION_MODAL);
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                }

            } else {
                Alert emptyField = new Alert(Alert.AlertType.ERROR);
                emptyField.setContentText("Name field was submitted empty, please enter a name");
                emptyField.showAndWait();
                editSubTask(actionEvent, subTask);
            }
        });
        refresh();
    }


    /**
     * A method that saves the subtask into the database.
     *
     * @param subTask - the subtask that needs saving
     * @param card - the card that has the subtask
     * @return - saved subtask
     */
    public SubTask saveSubtaskDB(SubTask subTask, Card card) {
        try {
            server.sendCard(card);
            return server.saveSubtask(subTask);
        } catch (WebApplicationException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }

        return null;
    }



    /**
     * Method that displays the subtask of current card.
     *
     * @param subTask - subtask to be displayed
     */
    public void showSubTaskList(SubTask subTask){

        CheckBox checkBox = new CheckBox(subTask.getTitle());
        checkBox.setStyle("-fx-font-size: 12px;");

        checkBox.setSelected(subTask.isDone());

        checkBox.selectedProperty().addListener((obs, old, newVal) -> {
            subTask.setDone(newVal);
            server.sendCard(subTask.getCard());
            server.editSubTask(subTask);
        });
        //checkBox.setOnAction(this::markDone);
        HBox hBoxCB = new HBox(checkBox);

        Button editST = new Button("\uD83D\uDD89");
        editST.setStyle("-fx-font-size: 10px;");
        editST.setOnAction(event -> {
            editSubTask(event, subTask);
        });
        Button deleteST = new Button("\uD83D\uDDD9");
        deleteST.setStyle("-fx-font-size: 10px;");
        deleteST.setOnAction(event -> {
            deleteSubTask(event, subTask);
        });
        HBox hBoxButtons = new HBox(editST,deleteST);

        HBox hBox = new HBox();
        hBox.setSpacing(100);
        hBox.getChildren().addAll(hBoxCB,hBoxButtons);

        vBox.getChildren().add(hBox);
    }

    /**
     * Deleting a subtask from database.
     *
     * @param actionEvent the action event
     * @param subtask the subtask to be deleted
     */

    private void deleteSubTask(ActionEvent actionEvent, SubTask subtask) {
        HBox clicked = (HBox)((Button) actionEvent.getSource()).getParent();
        HBox subtsk = (HBox) clicked.getParent();
        VBox vbox = (VBox) subtsk.getParent();


        vbox.getChildren().remove(subtsk);
        try {
            server.deleteSubtask(subtask);
        } catch (WebApplicationException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }


    /**
     * Method that refreshes all card components.
     */
    public void refresh() {
        refreshSubTasks();
        refreshCardDetails();
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

    /**
     *  Refreshing the subtasks of current card.
     */
    public void refreshSubTasks(){
        Card card = server.getCardsById(cardId);

        vBox.getChildren().clear();
        vBox.setSpacing(5);
        vBox.setAlignment(Pos.TOP_CENTER);

        for(SubTask subTask : card.getSubTasks()) {
            subTask.setCard(card);
            showSubTaskList(subTask);
        }
    }
}
