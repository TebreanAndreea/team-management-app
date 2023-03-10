package client.scenes;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;

public class BoardOverviewController {

    private Stage primaryStage;
    private Scene overview;
    public HBox hBox;
    public TitledPane TODO;


    /**
     * Function that goes to add a card.
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

    /**
     * <h3>Adds a new list with no contents, beside the 'add' button.</h3>
     */
    public void addList() {
        Button addCardButton = new Button("+");
        addCardButton.setOnAction(this::addCard);

        Button deleteListButton = new Button("delete list");
        deleteListButton.setOnAction(this::deleteList);

        VBox vBox = new VBox();
        vBox.setSpacing(20);
        vBox.setAlignment(Pos.TOP_CENTER);

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
        TitledPane titledPane = new TitledPane("new list", vBox);
        titledPane.setPrefHeight(TODO.getPrefHeight());
        titledPane.setMinWidth(TODO.getMinWidth());
        titledPane.setAnimated(false);
        hBox.getChildren().add(titledPane);
    }

    /**
     * <h3>Adds a (placeholder, as of now) card to its assigned list.</h3>
     * <p>The method gets the button causing the action, and generates another button to place above it.</p>
     * @param actionEvent the action event that caused this method to be called.
     */
    public void addCard(javafx.event.ActionEvent actionEvent) {
        Button clickedButton = (Button) actionEvent.getSource();
        VBox vBox = (VBox) clickedButton.getParent().getParent();

        Button newCard = new Button("New Card");
        Button edit = new Button("Edit");
        Button delete = new Button("x");
        delete.setOnAction(this::deleteCard);

        HBox buttonList = new HBox();
        buttonList.getChildren().addAll(newCard,edit,delete);

        HBox deleteListBox = (HBox) vBox.getChildren().remove(vBox.getChildren().size()-1);
        HBox plusBox = (HBox) vBox.getChildren().remove(vBox.getChildren().size()-1);


        vBox.getChildren().add(buttonList);
        vBox.getChildren().add(plusBox);
        vBox.getChildren().add(deleteListBox);
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

        HBox mainhbox = (HBox) titledPane.getParent();
        mainhbox.getChildren().remove(titledPane);
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

}
