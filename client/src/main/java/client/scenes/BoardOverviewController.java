package client.scenes;


import commons.SubTask;
import javafx.application.Platform;

import client.MyFXML;
import client.MyModule;
import com.google.inject.Injector;
import javafx.event.ActionEvent;

import javafx.event.EventTarget;

import client.utils.ServerUtils;
import commons.Board;
import commons.Card;
import commons.Listing;
import jakarta.ws.rs.WebApplicationException;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.inject.Inject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import static com.google.inject.Guice.createInjector;


public class BoardOverviewController {

    private Stage primaryStage;
    private Scene overview;
    public HBox hBox;
    public TextField accessKey;
    public Label boardName;
    public Button renameBoardButton;
    private ServerUtils server;
    private ListController listController;
    private EventTarget target;

    // A map that will keep track of all dependencies between
    // the lists in the UI and the lists we have in the DB
    private Map<VBox, Listing> map = new HashMap<>();
    private Map<HBox, Card> cardMap = new HashMap<>();

    Board board = new Board("test", "", "");
    private String fileName = "temp.txt";

    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);


    /**
     * Constructor which initialize the server.
     *
     * @param server         the server instance used for communication
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
        server.registerForMessages("/topic/boards", Board.class, q -> Platform.runLater(this::refresh));
        server.registerForMessages("/topic/lists", Listing.class, q -> {
            System.out.println("listing");
            Platform.runLater(this::refresh);
        });
        server.registerForMessages("/topic/card", Card.class, q -> Platform.runLater(this::refresh));
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

            if(!name.isEmpty()) {
                VBox vBox = (VBox) addCardButton.getParent().getParent();


                Button newCard = new Button(name);

                // make this card draggable
                newCard.setOnMousePressed(event -> {
                    target = newCard.getParent(); // this is the hbox that needs to be dropped
                });

                newCard.setOnMouseReleased(this::handleDropping);
                setupButton(newCard);
                Button edit = new Button("\uD83D\uDD89");
                edit.setOnAction(this::editCard); // an event happens when the button is clicked
                setupButton(edit);
                Button delete = new Button("\uD83D\uDDD9");
                delete.setOnAction(this::deleteCard); // an events happens when the button is clicked
                setupButton(delete);
                HBox buttonList = new HBox();
                buttonList.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(10), BorderWidths.DEFAULT)));
                buttonList.setAlignment(Pos.CENTER);
                buttonList.getChildren().addAll(newCard, edit, delete);


                HBox deleteListBox = (HBox) vBox.getChildren().remove(vBox.getChildren().size() - 1);
                HBox plusBox = (HBox) vBox.getChildren().remove(vBox.getChildren().size() - 1);

                vBox.getChildren().addAll(buttonList, plusBox, deleteListBox);

                Listing curList = map.get(vBox);
                Card curCard = new Card("", name, null, new ArrayList<>(), new ArrayList<>(), curList);
                Card updatedCard = saveCardDB(curCard, curList);
                curList.getCards().add(updatedCard);
                cardMap.put(buttonList, updatedCard);

            } else {

                Alert emptyField = new Alert(Alert.AlertType.ERROR);
                emptyField.setContentText("Name field was submitted empty, please enter a name");
                emptyField.showAndWait();
                addCard(actionEvent);
            }

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
        TextInputDialog dialog = new TextInputDialog(currentCard.getName());
        dialog.setTitle("Change the name of the card");
        dialog.setHeaderText("Please enter the new name for the card:");
        dialog.showAndWait().ifPresent(name -> {

            if(!name.isEmpty()) {
                cardButton.setText(name);
                server.updateCard(currentCard.getCardId(), name);
            } else {
                Alert emptyField = new Alert(Alert.AlertType.ERROR);
                emptyField.setContentText("Name field was submitted empty, please enter a name");
                emptyField.showAndWait();
                editCard(actionEvent);
            }

        });
    }

    /**
     * Edit a List by changing its name.
     *
     * @param actionEvent the action event
     * @param list        the list to be edited
     */
    public void editList(javafx.event.ActionEvent actionEvent, Listing list) {
        //listController.setBoard(board);
        listController.editList(actionEvent, list);
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
     * @param list        the list to be deleted
     */
    public void deleteList(javafx.event.ActionEvent actionEvent, Listing list) {
        listController.deleteList(actionEvent, list);
    }

    /**
     * Function that enable you to go back to HomePage.
     *
     * @param actionEvent the event used
     * @throws IOException the exemption it might be caused
     */
    public void switchToInitialOverviewScene(javafx.event.ActionEvent actionEvent) throws IOException {
        var initialOverview = FXML.load(InitialOverviewController.class, "client", "scenes", "InitialOverview.fxml");
        initialOverview.getKey().setFileName(fileName);
        initialOverview.getKey().refresh();
        primaryStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        overview = new Scene(initialOverview.getValue());
        primaryStage.setScene(overview);

        primaryStage.show();
    }

    /**
     * Function that goes to the card details.
     *
     * @param actionEvent the action event on the button
     * @param cardID the id of a card
     * @param list the list of the card
     * @throws IOException the exception which might be caused
     */
    public void switchToCardScene(MouseEvent actionEvent, long cardID, Listing list) throws IOException {
        var cardOverview = FXML.load(CardOverviewController.class, "client", "scenes", "CardOverview.fxml");
        cardOverview.getKey().setCardId(cardID);
        cardOverview.getKey().setFileName(fileName);
        cardOverview.getKey().setBoard(board);
        cardOverview.getKey().setList(list);
        cardOverview.getKey().refresh();
        primaryStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        overview = new Scene(cardOverview.getValue());
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

        int dim = hBox.getChildren().size();

        for (int i = 0; i < dim; i++) { // check if mouse is inside this vbox
            TitledPane titledPane = (TitledPane) hBox.getChildren().get(i);
            VBox vBox = (VBox) titledPane.getContent();

            Bounds vboxBounds = vBox.getLayoutBounds();
            Point2D coordinates = vBox.localToScreen(vboxBounds.getMinX(), vboxBounds.getMinY());
            double x1 = coordinates.getX();
            double y1 = coordinates.getY();

            double x2 = x1 + vBox.getWidth();
            double y2 = y1 + vBox.getHeight();

            if (mouseX >= x1 && mouseX <= x2 && mouseY >= y1 && mouseY <= y2) { // the mouse is inside this vbox

                Card card = cardMap.get((HBox) target);
                server.deleteCard(card.getCardId()); // delete the card from its initial list
                vBox.getChildren().remove((HBox) target); // this is for duplicate children

                Listing list = map.get(vBox);
                Card updatedCard = saveCardDB(card, list);  // add this card to this list
                list.getCards().add(updatedCard);
                cardMap.put((HBox) target, updatedCard);

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
                if (!foundPlace) // add at the end
                    vBox.getChildren().add(nrCards, (HBox) target);

                for (int j = 0; j < nrCards + 1; j++) { // we delete all the cards from this list
                    HBox hBox = (HBox) vBox.getChildren().get(j);
                    Card card2 = cardMap.get(hBox);
                    server.deleteCard(card2.getCardId());
                }

                for (int j = 0; j < nrCards + 1; j++) { // we have all the cards in good order, we add them to the list
                    HBox hBox = (HBox) vBox.getChildren().get(j);
                    Card card2 = cardMap.get(hBox);
                    Card updated = saveCardDB(card2, list);
                    list.getCards().add(updated);
                    cardMap.put(hBox, updated);
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
        setupAddCardButton(addCardButton);
        Button editListButton = new Button("Edit");
        editListButton.setOnAction(event -> editList(event, listing));
        setupAddCardButton(editListButton);
        Button deleteListButton = new Button("delete list");
        // deleteListButton.setOnAction(this::deleteList);
        deleteListButton.setOnAction(event -> deleteList(event, listing));
        setupDeleteListButton(deleteListButton);
        VBox vBox = new VBox();
        vBox.setSpacing(20);
        vBox.setAlignment(Pos.TOP_CENTER);
        for (Card c : listing.getCards()) {
            addCard(c,vBox,listing);
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
     * Adds a card to the vBox List.
     * @param c - the card we add
     * @param vBox - the vBox which contains the list
     * @param listing - the list the card is in
     */
    public void addCard (Card c, VBox vBox, Listing listing)
    {
        Button newCard;
        VBox vBox1 = new VBox();
        int totalSubtaks = c.getSubTasks().size();
        int doneSubtasks = 0;
        for(SubTask s : c.getSubTasks()) {
            if(s.isDone() == true) doneSubtasks++;
        }
        vBox1.getChildren().addAll(new Label(String.format("(%d/%d)", doneSubtasks, totalSubtaks)));
        vBox1.setAlignment(Pos.BOTTOM_RIGHT);
        Label nameCard = new Label(c.getName());
        if(!c.getDescription().equals("")) {
            Label markDescription = new Label("\u2630");
            markDescription.setStyle("-fx-font-size: 5px;");
            nameCard.setStyle("-fx-font-size: 15x;");
            HBox hbox = new HBox(markDescription, nameCard, vBox1);
            hbox.setSpacing(8);
            newCard = new Button();
            newCard.setGraphic(hbox);
        } else {
            newCard = new Button();
            HBox hbox = new HBox(nameCard, vBox1);
            hbox.setSpacing(8);
            newCard.setGraphic(hbox);
        }
        newCard.setPrefWidth(100);
        newCard.setPrefHeight(100);
        newCard.setUserData(c.getCardId());
        setupButton(newCard);
        // make this card draggable
        newCard.setOnMousePressed(event -> {
            target = newCard.getParent(); // this is the hBox that needs to be dropped
        });

        newCard.setOnMouseReleased(this::handleDropping);

        Button edit = new Button("\uD83D\uDD89");
        //edit.setOnAction(this::editCard); // an event happens when the button is clicked
        edit.setOnMousePressed(event -> {
            if(event.getClickCount() == 2) {
                try {
                    switchToCardScene(event, c.getCardId(), listing);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        setupButton(edit);
        Button delete = new Button("\uD83D\uDDD9");
        delete.setOnAction(this::deleteCard); // an events happens when the button is clicked
        setupButton(delete);
        HBox buttonList = new HBox();
        buttonList.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(10), BorderWidths.DEFAULT)));
        buttonList.getChildren().addAll(newCard, edit, delete);
        buttonList.setAlignment(Pos.CENTER);
        vBox.getChildren().add(buttonList);
        cardMap.put(buttonList, c);
    }

    /**
     * fetches the listings from the JSON file and displays them.
     */
    public void refresh() {
        long id = board.getBoardId();
        if (id != 0)
            board = server.getBoardByID(id);
        listController.setBoard(board);
        String boardTitle = board.getTitle();
        Text boardText = new Text(boardTitle);
        boardText.setFont(Font.font("System Bold", 19.0));
        boardName.setText(boardTitle);
        renameBoardButton.setLayoutX(boardName.getLayoutX() + boardText.getLayoutBounds().getWidth() + 10.0);
        accessKey.setText("Access key: " + board.getAccessKey());
        List<Listing> listings = board.getLists();
        hBox.getChildren().clear();
        map = new HashMap<>();
        for (Listing listing : listings)
            addListWithListing(listing);
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
     * This method is called when the user clicks on the "Leave board" button.
     * It will delete the board from the user's own stored boards file.
     *
     * @param actionEvent the event that triggered this method
     */
    public void leaveBoard(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Are you sure you want to leave this board?");
        alert.setContentText("You will not be able to access this board again.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            long id = board.getBoardId();
            File file = new File(fileName);
            StringBuilder content = new StringBuilder();
            try {
                Scanner scanner = new Scanner(file);
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (!line.startsWith(Long.toString(id))) {
                        content.append(line).append("\n");
                    }
                }
                FileOutputStream outputStream = new FileOutputStream(file);
                outputStream.write(content.toString().getBytes());
                outputStream.flush();
                outputStream.close();
                switchToInitialOverviewScene(actionEvent);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * Sets up the delete list button.
     * @param button the button to set up
     */
    private void setupDeleteListButton(Button button){
        button.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(5), BorderWidths.DEFAULT)));
        button.setStyle("-fx-background-color: transparent;");
        button.setTextFill(Color.RED);
        button.setOnMouseEntered(event -> {
            button.setStyle("-fx-background-color: RED");
            button.setTextFill(Color.WHITE);
        });
        button.setOnMouseExited(event -> {
            button.setStyle("-fx-background-color: transparent;");
            button.setTextFill(Color.RED);
        });
    }

    /**
     * Sets up the add card button.
     * @param button the button to set up
     */
    private void setupAddCardButton(Button button){
        button.setBorder(new Border(new BorderStroke(Color.GREEN, BorderStrokeStyle.SOLID, new CornerRadii(5), BorderWidths.DEFAULT)));
        button.setStyle("-fx-background-color: transparent;");
        button.setTextFill(Color.GREEN);
        button.setOnMouseEntered(event -> {
            button.setStyle("-fx-background-color: GREEN");
            button.setTextFill(Color.WHITE);
        });
        button.setOnMouseExited(event -> {
            button.setStyle("-fx-background-color: transparent;");
            button.setTextFill(Color.GREEN);
        });
    }
    /**
     * Sets up the buttons contained in the lists.
     * @param button the button to set up
     */
    private void setupButton(Button button){
        HBox.setHgrow(button,Priority.ALWAYS);
        button.setStyle("-fx-background-color: transparent");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setMinWidth(Button.USE_PREF_SIZE);
        button.setOnMouseEntered(event -> button.setStyle("-fx-background-color: rgba(0, 0, 0, 0.1);"));
        button.setOnMouseExited(event -> button.setStyle("-fx-background-color: transparent;"));
    }

    /**
     * Copies the board's access key to the clipboard.
     * @param mouseEvent the event that triggered this method
     */
    public void copyToClipboard(MouseEvent mouseEvent) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(board.getAccessKey());
        clipboard.setContent(content);
    }

    /**
     * Renames the board.
     *
     * @param mouseEvent the event that triggered this method
     */
    public void renameBoard(MouseEvent mouseEvent) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Change the name of the board");
        dialog.setHeaderText("Please enter a name for the board:");
        dialog.showAndWait().ifPresent(name -> {

            if(!name.isEmpty()) {
                board.setTitle(name);
                server.updateBoard(board.getBoardId(), name);
            } else {
                Alert emptyField = new Alert(Alert.AlertType.ERROR);
                emptyField.setContentText("Name field was submitted empty, please enter a name");
                emptyField.showAndWait();
                renameBoard(mouseEvent);
            }

        });
    }
}
