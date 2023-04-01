package client.scenes;

import client.utils.ServerUtils;
import commons.Board;
import commons.Card;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javax.inject.Inject;

public class TagController {

    @FXML
    VBox vBox;

    private Board board;
    private ServerUtils server;

    /**
     * Constructor which injects the server.
     * @param server the ServerUtils instance
     */
    @Inject
    public TagController(ServerUtils server){
        this.server = server;
    }

    public TagController(){}

    /**
     * This method initializes the websockets.
     */
    public void initialize(){
        server.registerForMessages("/topic/tag", Card.class, q -> Platform.runLater(this::refresh));
    }


    /**
     * Sets the board for these tags.
     * @param board
     */
    public void setBoard(Board board){
        this.board = board;
    }

    /**
     * This method opens a dialog box for setting the name of the tag.
     * @param actionEvent the action events
     */
    public void addTag(javafx.event.ActionEvent actionEvent){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Tag name");
        dialog.setHeaderText("Please enter the name of the tag");
        dialog.showAndWait().ifPresent(name -> {

            //verifies if the input field was submitted empty or not
            if(!name.isEmpty()) {

                //saving the tag into the database
                //Tag tag = new Tag(name,board);
                //saveTagDB(tag);

                // construct the vbox from frontend, just to see the layout
                Button tagButton = new Button(name);

                Button editButton = new Button("edit");
                editButton.setOnAction(this::editTagName);

                Button deleteButton = new Button("delete");

                HBox hbox = new HBox(tagButton,editButton,deleteButton);
                hbox.setSpacing(100);

                vBox.getChildren().add(hbox);

            } else {
                //sends alert and return to the input dialog after
                Alert emptyField = new Alert(Alert.AlertType.ERROR);
                emptyField.setContentText("Name field was submitted empty, please enter a name");
                emptyField.showAndWait();
            }
        });
    }

    /**
     * This method saves a tag in the database.
     * @param tag the tag
     */
    /*public void saveTagDB(Tag tag){
        try {
            //server.sendBoard(board);
            server.saveTag(tag);
        } catch (WebApplicationException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

     */

    /**
     * Method which opens a dialog box for changing the tag name.
     * @param actionEvent the action event
     */
    public void editTagName(javafx.event.ActionEvent actionEvent){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Tag name");
        dialog.setHeaderText("Please enter the name of the tag");
        dialog.showAndWait().ifPresent(name -> {

            //verifies if the input field was submitted empty or not
            if(!name.isEmpty()) {

            } else {
                //sends alert and return to the input dialog after
                Alert emptyField = new Alert(Alert.AlertType.ERROR);
                emptyField.setContentText("Name field was submitted empty, please enter a name");
                emptyField.showAndWait();
            }
        });
    }


    public void refresh(){

    }
}
