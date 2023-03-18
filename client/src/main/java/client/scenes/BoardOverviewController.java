package client.scenes;

import client.utils.ServerUtils;
import commons.Card;
import commons.Listing;
import commons.SubTask;
import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
//import java.util.List;
import java.util.Map;

public class BoardOverviewController {

    private Stage primaryStage;
    private Scene overview;
    public HBox hBox;
    private ServerUtils server;

    //A map that will keep track of all dependencies between
    // the lists in the UI and the lists we have in the DB
    private Map<VBox, Listing> map = new HashMap<>();



    /**
     * Constructor which initialize the server.
     * @param server the server instance used for communication
     */

    @Inject
    public BoardOverviewController (ServerUtils server){
        this.server = server;
    }
    public BoardOverviewController ( ){
    }

    public void setServer(ServerUtils server) {
        this.server = server;
    }
    /**
     * Adds a new list with no contents, beside the 'add' button with a title.
     */
    public void addList() {

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("List name");
        dialog.setHeaderText("Please enter the name of the list");
        dialog.showAndWait().ifPresent(name -> {
            Button addCardButton = new Button("+");

            addCardButton.setOnAction(this::addCard);

            Button deleteListButton = new Button("delete list");
            deleteListButton.setOnAction(this::deleteList);

            VBox vBox = new VBox();
            vBox.setSpacing(20);
            vBox.setAlignment(Pos.TOP_CENTER);

            //saving the list into the database
            Listing newList = new Listing(name, null);
            saveListDB(newList);

            map.put(vBox, newList);


            // add the "Add card" button below the cards
            HBox addCardButtonRow = new HBox();
            addCardButtonRow.setAlignment(Pos.CENTER);
            addCardButtonRow.getChildren().add(addCardButton);
            vBox.getChildren().add(addCardButtonRow);

            // add the "Delete list button at the bottom of this list
            HBox deleteListButtonRow = new HBox();
            deleteListButtonRow.setAlignment(Pos.BOTTOM_RIGHT);
            deleteListButtonRow.getChildren().add(deleteListButton);
            vBox.getChildren().add(deleteListButtonRow);



            // set up the list itself
            TitledPane titledPane = new TitledPane(name, vBox);
            titledPane.setPrefHeight(253); // TODO: refactor the dimensions of the lists
            titledPane.setMinWidth(135);
            titledPane.setAnimated(false);
            hBox.getChildren().add(titledPane);
        });
    }

    /**
     * Saving the list into the database.
     *
     * @param list the list
     */
    public void saveListDB(Listing list) {
        try {
            server.saveList(list);
        } catch (WebApplicationException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }

       // clearFields();
      //  MainController.showOverview();
    }

    /**
     * Saves the card into db.
     * @param card - the card we need to save
     * @param list - the list that has the card
     */
    public void saveCardDB(Card card, Listing list) {
        try {
            //server.sendList(list);
            server.saveCard(card);
        } catch (WebApplicationException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * A method that saves the subtask.
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
     * <h3>Adds a card to its assigned list.</h3>
     * <p>The method gets the button causing the action, and generates another button to place above it.</p>
     * @param actionEvent the action event.
     */
    public void addCard(javafx.event.ActionEvent actionEvent) {

        // when the + button is clicked, a dialog pops up, and we can enter the card title
        Button addCardButton = (Button) actionEvent.getSource();

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Card title");
        dialog.setHeaderText("Please enter a name for the card:");
        dialog.showAndWait().ifPresent(name -> {
            VBox vBox = (VBox) addCardButton.getParent().getParent();



            Button newCard = new Button(name);
            Button edit = new Button("Edit");
            edit.setOnAction(this::editCard); // an event happens when the button is clicked

            Button delete = new Button("x");
            delete.setOnAction(this::deleteCard); // an events happens when the button is clicked

            HBox buttonList = new HBox();
            buttonList.getChildren().addAll(newCard,edit,delete);

            HBox deleteListBox = (HBox) vBox.getChildren().remove(vBox.getChildren().size()-1);
            HBox plusBox = (HBox) vBox.getChildren().remove(vBox.getChildren().size()-1);


            vBox.getChildren().add(buttonList);
            vBox.getChildren().add(plusBox);
            vBox.getChildren().add(deleteListBox);

            Listing curList = map.get(vBox);
            Card curCard = new Card("", name, null, new ArrayList<>(), new ArrayList<>(), curList);
            saveCardDB(curCard, curList);
            curList.getCards().add(curCard);
        });
    }

    /**
     * This method allows the user to change the name of a card.
     * @param actionEvent the action event
     */
    public void editCard(javafx.event.ActionEvent actionEvent){
        Button editButton = (Button)actionEvent.getSource();
        HBox hBox = (HBox) editButton.getParent();
        Button cardButton = (Button) hBox.getChildren().get(0);

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Change the name of the card");
        dialog.setHeaderText("Please enter the new name for the card:");
        dialog.showAndWait().ifPresent(name -> {
            cardButton.setText(name);
        });
    }

    /**
     * <h3>Deletes the card on which the button is clicked.</h3>
     * @param actionEvent the action  event that caused this method to be called
     */
    public void deleteCard(javafx.event.ActionEvent actionEvent) {
        HBox clicked = (HBox)((Button) actionEvent.getSource()).getParent();
        VBox vBox = (VBox) clicked.getParent();
        vBox.getChildren().remove(clicked);
    }


    /**
     * Deletes a list when the "delete button" is clicked.
     * @param actionEvent the action event that caused this method to be called
     */
    public void deleteList(javafx.event.ActionEvent actionEvent){
        HBox clicked = (HBox)((Button) actionEvent.getSource()).getParent();
        VBox vbox = (VBox)clicked.getParent();

        TitledPane titledPane = (TitledPane) vbox.getParent().getParent();

        HBox mainHBox = (HBox) titledPane.getParent();
        mainHBox.getChildren().remove(titledPane);
    }

    /**
     * Function that enable you to go back to HomePage.
     *
     * @param actionEvent the event used
     * @throws IOException the exemption it might be caused
     */
    public void switchToHomePageScene(javafx.event.ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("HomePageOverview.fxml"));
        primaryStage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        overview = new Scene(root);
        primaryStage.setScene(overview);
        primaryStage.show();
    }

    /**
     * Function that goes to the card details.
     *
     * @param actionEvent the action event on the button
     * @throws IOException the exception which might be caused
     */
    public void switchToCardScene(javafx.event.ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("CardOverview.fxml"));
        primaryStage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        overview = new Scene(root);
        primaryStage.setScene(overview);
        primaryStage.show();
    }
}