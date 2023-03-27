package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Board;
import commons.Card;
import commons.Listing;
import jakarta.ws.rs.WebApplicationException;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

public class ListController {

    private ServerUtils server;
    private Board board;

    @Inject
    public ListController(ServerUtils server) {
        this.server = server;
    }

    /**
     * Adds a new list with no contents, besides the 'add' button with a title.
     */
    public void addList() {

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("List name");
        dialog.setHeaderText("Please enter the name of the list");
        dialog.showAndWait().ifPresent(name -> {

            //verifies if the input field was submitted empty or not
            if(!name.isEmpty()) {
                //saving the list into the database
                Listing newList = new Listing(name, board);
                saveListDB(newList, board);
            } else {
                //sends alert and return to the input dialog after
                Alert emptyField = new Alert(Alert.AlertType.ERROR);
                emptyField.setContentText("Name field was submitted empty, please enter a name");
                emptyField.showAndWait();
                addList();
            }

        });
    }
    /**
     * Sets the board.
     *
     * @param board the new board
     */
    public void setBoard(Board board) {
        this.board = board;
    }

    /**
     * Saving the list into the database.
     * @param board the board
     * @param list the list
     */
    public void saveListDB(Listing list,Board board) {
        try {
            server.sendBoard(board);
            server.saveList(list);
        } catch (WebApplicationException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }


    /**
     * Edit a List by changing its name.
     *
     * @param actionEvent the action event
     * @param list the list to be edited
     */
    public void editList(javafx.event.ActionEvent actionEvent, Listing list) {
        HBox clicked = (HBox)((Button) actionEvent.getSource()).getParent();
        VBox vbox = (VBox)clicked.getParent();
        TitledPane titledPane = (TitledPane) vbox.getParent().getParent();
        TextInputDialog dialog = new TextInputDialog(list.getTitle());
        dialog.setTitle("Change the name of the list");
        dialog.setHeaderText("Please enter the new name of the list");
        dialog.showAndWait().ifPresent(name -> {

            if(!name.isEmpty()) {
                titledPane.setText(name);
                server.sendBoard(list.getBoard());
                server.updateList(list.getListId(), name);
            } else {
                Alert emptyField = new Alert(Alert.AlertType.ERROR);
                emptyField.setContentText("Name field was submitted empty, please enter a name");
                emptyField.showAndWait();
                editList(actionEvent, list);
            }

        });
    }


    /**
     * Deletes a list when the "delete button" is clicked with all its task.
     *
     * @param actionEvent the action event that caused this method to be called
     * @param list the list to be deleted
     */

    public void deleteList(javafx.event.ActionEvent actionEvent, Listing list){

        HBox clicked = (HBox)((Button) actionEvent.getSource()).getParent();
        VBox vbox = (VBox)clicked.getParent();

        TitledPane titledPane = (TitledPane) vbox.getParent().getParent();

        HBox mainHBox = (HBox) titledPane.getParent();
        mainHBox.getChildren().remove(titledPane);
        for(int i = 0; i < list.getCards().size(); i++) {
            Card card = list.getCards().get(i);
            try {
                server.deleteCard(card.getCardId());
            } catch (WebApplicationException e) {
                var alert = new Alert(Alert.AlertType.ERROR);
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.setContentText(e.getMessage());
                alert.showAndWait();
                return;
            }
        }

        try {
            server.deleteList(list.getListId());
        } catch (WebApplicationException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}
