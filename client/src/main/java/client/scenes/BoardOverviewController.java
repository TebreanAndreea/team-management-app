package client.scenes;

import javafx.event.EventTarget;

import client.utils.ServerUtils;
import commons.Board;
import commons.Card;
import commons.Listing;
import commons.SubTask;
import jakarta.ws.rs.WebApplicationException;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
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
import java.util.List;
import java.util.Map;


public class BoardOverviewController {

    private Stage primaryStage;
    private Scene overview;
    public HBox hBox;
    private ServerUtils server;
    private ListController listController;
    private EventTarget target;

    // A map that will keep track of all dependencies between
    // the lists in the UI and the lists we have in the DB
    private Map<VBox, Listing> map = new HashMap<>();
    private Map<HBox, Card> cardMap = new HashMap<>();


    /**
     * Constructor which initialize the server.
     * @param server the server instance used for communication
     * @param listController the controller for a list
     */

    @Inject
    public BoardOverviewController(ServerUtils server, ListController listController) {
        this.server = server;
        this.listController = listController;
    }

    public BoardOverviewController() {
    }


    /**
     * Initializes the controller and immediately fetches the lists from the database.
     */
    public void initialize() {
        server.registerForMessages("/topic/boards", Board.class, q -> {
            refresh();
        });
        server.registerForMessages("/topic/lists", Listing.class, q -> {
            System.out.println("listing");
            refresh();
        });
        server.registerForMessages("/topic/card", Card.class, q -> {
            refresh();
        });
        refresh();
    }

    /**
     * Adds a new list with no contents, besides the 'add' button with a title.
     */
    public void addList() {
        listController.addList();
        refresh();
    }


    /**
     * Saves the card into db.
     *
     * @param card - the card we need to save
     * @param list - the list that has the card
     * @return card
     */
    public Card saveCardDB(Card card, Listing list) {
        try {
            server.sendList(list);
            return server.saveCard(card);
        } catch (WebApplicationException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
        return null;
    }

    /**
     * A method that saves the subtask.
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
     * <h3>Adds a card to its assigned list.</h3>
     * <p>The method gets the button causing the action, and generates another button to place above it.</p>
     *
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

            // make this card draggable
            newCard.setOnMousePressed(event -> {
                target = newCard.getParent(); // this is the hbox that needs to be dropped
            });

            newCard.setOnMouseReleased(this::handleDropping);

            Button edit = new Button("\uD83D\uDD89");
            edit.setOnAction(this::editCard); // an event happens when the button is clicked

            Button delete = new Button("\uD83D\uDDD9");
            delete.setOnAction(this::deleteCard); // an events happens when the button is clicked

            HBox buttonList = new HBox();
            buttonList.setAlignment(Pos.CENTER);
            buttonList.getChildren().addAll(newCard, edit, delete);


            HBox deleteListBox = (HBox) vBox.getChildren().remove(vBox.getChildren().size() - 1);
            HBox plusBox = (HBox) vBox.getChildren().remove(vBox.getChildren().size() - 1);

            vBox.getChildren().addAll(buttonList,plusBox,deleteListBox);

            Listing curList = map.get(vBox);
            Card curCard = new Card("", name, null, new ArrayList<>(), new ArrayList<>(), curList);
            Card updatedCard = saveCardDB(curCard, curList);
            curList.getCards().add(updatedCard);
            cardMap.put(buttonList, updatedCard);
        });

    }

    /**
     * This method allows the user to change the name of a card.
     *
     * @param actionEvent the action event
     */
    public void editCard(javafx.event.ActionEvent actionEvent) {
        Button editButton = (Button) actionEvent.getSource();
        HBox hBox = (HBox) editButton.getParent();
        Button cardButton = (Button) hBox.getChildren().get(0);
        Card currentCard = cardMap.get(hBox);
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Change the name of the card");
        dialog.setHeaderText("Please enter the new name for the card:");
        dialog.showAndWait().ifPresent(name -> {
            cardButton.setText(name);
            server.updateCard(currentCard.getCardId(), name);
        });
    }

    /**
     * Edit a List by changing its name.
     *
     * @param actionEvent the action event
     * @param list the list to be edited
     */
    public void editList(javafx.event.ActionEvent actionEvent, Listing list) {
        listController.editList(actionEvent,list);
    }

    /**
     * <h3>Deletes the card on which the button is clicked.</h3>
     *
     * @param actionEvent the action  event that caused this method to be called
     */
    public void deleteCard(javafx.event.ActionEvent actionEvent) {
        HBox clicked = (HBox) ((Button) actionEvent.getSource()).getParent();
        VBox vBox = (VBox) clicked.getParent();
        Card card = cardMap.get(clicked);
        server.deleteCard(card.getCardId());
        vBox.getChildren().remove(clicked);
    }


    /**
     * Deletes a list when the "delete button" is clicked with all its task.
     *
     * @param actionEvent the action event that caused this method to be called
     * @param list the list to be deleted
     */
    public void deleteList(javafx.event.ActionEvent actionEvent, Listing list){
        listController.deleteList(actionEvent,list);
    }
    /**
     * Function that enable you to go back to HomePage.
     *
     * @param actionEvent the event used
     * @throws IOException the exemption it might be caused
     */
    public void switchToHomePageScene(javafx.event.ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("HomePageOverview.fxml"));
        primaryStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
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
        primaryStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        overview = new Scene(root);
        primaryStage.setScene(overview);
        primaryStage.show();
    }


    /**
     * This method handles dropping a hbox in another titledPane or within the same titledPane.
     *
     * @param mouseEvent the mouse event
     */
    private void handleDropping(MouseEvent mouseEvent) {
        double mouseX = mouseEvent.getScreenX();
        double mouseY = mouseEvent.getScreenY();


        for (int i = 0; i < hBox.getChildren().size(); i++) { // check if mouse is inside this vbox
            TitledPane titledPane = (TitledPane) hBox.getChildren().get(i);
            VBox vBox = (VBox) titledPane.getContent();

            Bounds vboxBounds = vBox.getLayoutBounds();
            Point2D coordinates = vBox.localToScreen(vboxBounds.getMinX(), vboxBounds.getMinY());
            double x1 = coordinates.getX();
            double y1 = coordinates.getY();

            double x2 = x1 + vBox.getWidth();
            double y2 = y1 + vBox.getHeight();

            if (mouseX >= x1 && mouseX <= x2 && mouseY >= y1 && mouseY <= y2) { // the mouse is inside this vbox

                Card card = cardMap.get((HBox)target);
                server.deleteCard(card.getCardId()); // delete the card from its initial list
                vBox.getChildren().remove((HBox) target); // this is for duplicate children

                Listing list = map.get(vBox);
                Card updatedCard = saveCardDB(card,list);  // add this card to this list
                list.getCards().add(updatedCard);
                cardMap.put((HBox) target,updatedCard);

                int nrCards = vBox.getChildren().size() - 2;
                boolean foundPlace = false;
                for (int j = 0; j < nrCards - 1; j++) { // check for collisions between cards to insert it in the correct place
                    HBox hBoxUp = (HBox) vBox.getChildren().get(j);

                    Bounds hboxBounds = hBoxUp.getLayoutBounds();
                    Point2D coord = hBoxUp.localToScreen(hboxBounds.getMinX(), hboxBounds.getMinY());

                    double yMiddleUp = (coord.getY() * 2 + hBoxUp.getHeight()) / 2;

                    HBox hBoxDown = (HBox) vBox.getChildren().get(j + 1);
                    hboxBounds = hBoxDown.getLayoutBounds();
                    coord = hBoxDown.localToScreen(hboxBounds.getMinX(), hboxBounds.getMinY());

                    double yMiddleDown = (coord.getY() * 2 + hBoxDown.getHeight()) / 2;

                    if (j == 0 && mouseY < yMiddleUp) {
                        vBox.getChildren().add(0, (HBox) target);
                        foundPlace = true;
                    } else {
                        if (mouseY >= yMiddleUp && mouseY < yMiddleDown) {
                            vBox.getChildren().add(j + 1, (HBox) target);
                            foundPlace = true;
                        }
                    }
                }

                if (foundPlace == false) // add at the end
                    vBox.getChildren().add(nrCards, (HBox) target);

                for (int j=0;j<nrCards+1;j++){ // we delete all the cards from this list
                    HBox hBox = (HBox) vBox.getChildren().get(j);
                    Card card2 = cardMap.get(hBox);
                    server.deleteCard(card2.getCardId());
                }

                for (int j=0;j<nrCards+1;j++){ // we have all the cards in good order, we add them to the list
                    HBox hBox = (HBox) vBox.getChildren().get(j);
                    Card card2 = cardMap.get(hBox);
                    Card updated = saveCardDB(card2,list);
                    list.getCards().add(updated);
                    cardMap.put(hBox,updated);
                }
            }
        }
    }


    /**
     * addList method which accepts a Listing as a parameter.
     * <h5>NOTE: The IDs of the cards are stored within their user data.</h5>
     *
     * @param listing the listing from which to create a listing
     */
    private void addListWithListing(Listing listing) {
        Button addCardButton = new Button("+");

        addCardButton.setOnAction(this::addCard);

        Button editListButton = new Button("Edit");
        editListButton.setOnAction(event -> {
            editList(event, listing);
        });

        Button deleteListButton = new Button("delete list");
        // deleteListButton.setOnAction(this::deleteList);
        deleteListButton.setOnAction(event -> {
            deleteList(event, listing);
        });

        VBox vBox = new VBox();
        vBox.setSpacing(20);
        vBox.setAlignment(Pos.TOP_CENTER);
        List<Card> cards = listing.getCards();
        for (Card c : cards) {
            Button newCard = new Button(c.getName());
            newCard.setUserData(c.getCardId());

            // make this card draggable
            newCard.setOnMousePressed(event -> {
                target = newCard.getParent(); // this is the hBox that needs to be dropped
            });

            newCard.setOnMouseReleased(this::handleDropping);
            Button edit = new Button("\uD83D\uDD89");
            edit.setOnAction(this::editCard); // an event happens when the button is clicked

            Button delete = new Button("\uD83D\uDDD9");
            delete.setOnAction(this::deleteCard); // an events happens when the button is clicked

            HBox buttonList = new HBox();
            buttonList.getChildren().addAll(newCard, edit, delete);
            buttonList.setAlignment(Pos.CENTER);
            vBox.getChildren().add(buttonList);
            cardMap.put(buttonList, c);
        }
        // add the "Add card" button below the cards
        HBox addCardButtonRow = new HBox();
        addCardButtonRow.setAlignment(Pos.CENTER);
        addCardButtonRow.getChildren().add(addCardButton);
        vBox.getChildren().add(addCardButtonRow);

        // add the "Delete list button at the bottom of this list
        HBox deleteListButtonRow = new HBox();
        deleteListButtonRow.setAlignment(Pos.BOTTOM_RIGHT);
        deleteListButtonRow.getChildren().add(editListButton);
        deleteListButtonRow.getChildren().add(deleteListButton);
        vBox.getChildren().add(deleteListButtonRow);

        map.put(vBox, listing);
        // set up the list itself
        TitledPane titledPane = new TitledPane(listing.getTitle(), vBox);
        titledPane.setUserData(listing.getListId());
        titledPane.setPrefHeight(253); // TODO: refactor the dimensions of the lists
        titledPane.setMinWidth(135);
        titledPane.setAnimated(false);
        hBox.getChildren().add(titledPane);
    }

    /**
     * fetches the listings from the JSON file and displays them.
     */
    public void refresh() {
        List<Listing> listings = server.getListings();
        hBox.getChildren().clear();
        map = new HashMap<>();
        for (Listing listing : listings)
            addListWithListing(listing);
    }

}