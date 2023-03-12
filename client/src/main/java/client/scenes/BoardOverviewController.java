package client.scenes;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;

public class BoardOverviewController {

    private Stage primaryStage;
    private Scene overview;
    public HBox hBox;


    /**
     * Adds a new list with no contents, beside the 'add' button with a title.
     *
     * @param title the title for the new list
     */
    public void addList(String title) {
        Button addCardButton = new Button("+");

        // when the + button is clicked, a dialog pops up, and we can enter the card title
        addCardButton.setOnAction(event -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Card title");
            dialog.setHeaderText("Please enter a name for the card:");
            dialog.showAndWait().ifPresent(name -> {
                addCard(addCardButton,name);
            });
        });


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
        TitledPane titledPane = new TitledPane(title, vBox);
        titledPane.setPrefHeight(253); // TODO: refactor the dimensions of the lists
        titledPane.setMinWidth(135);
        titledPane.setAnimated(false);
        hBox.getChildren().add(titledPane);
    }

    /**
     * Method to create an area to add a title for a new created list.
     *
     */
    public void addTitleForList() {
        TextArea textArea = new TextArea();
        textArea.setPromptText("Enter title");
        Button createButton = new Button("Create");
        VBox vbox = new VBox(textArea, createButton);
        hBox.getChildren().add(vbox);
        vbox.setMinWidth(70);
        vbox.setPrefHeight(100);
        textArea.setPrefHeight(5);
        textArea.setMinWidth(1);
        vbox.setAlignment(Pos.CENTER);
        createButton.setOnAction(event -> {
            String titleText = textArea.getText().trim();
            Pane parent = (Pane) vbox.getParent();
            parent.getChildren().remove(vbox);
            addList(titleText);
        });

    }
    /**
     * <h3>Adds a (placeholder, as of now) card to its assigned list.</h3>
     * <p>The method gets the button causing the action, and generates another button to place above it.</p>
     *
     */


    /**
     * <h3>Adds a card to its assigned list.</h3>
     * <p>The method gets the button causing the action, and generates another button to place above it.</p>
     * @param clickedButton the "add" button for adding a card
     * @param cardName the title of the card which will be inserted
     */
    public void addCard(Button clickedButton, String cardName) {
        //Button clickedButton = (Button) actionEvent.getSource();
        VBox vBox = (VBox) clickedButton.getParent().getParent();

        Button newCard = new Button(cardName);
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