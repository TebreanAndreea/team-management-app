package client.scenes;

import client.MyFXML;
import client.MyModule;
import client.utils.ServerUtils;
import com.google.inject.Injector;
import commons.*;
import jakarta.ws.rs.WebApplicationException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.google.inject.Guice.createInjector;

public class CardOverviewController {
    private Stage primaryStage;
    private Scene overview;
    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);
    private long cardId;
    private ServerUtils server;

    public VBox vBox;
    public HBox hbox;
    public Listing list;
    @FXML
    public Label cardLabel;
    @FXML
    public TextArea description;
    @FXML
    public VBox colorSchemes;

    private Board board = new Board("test", "", "");
    private String fileName = "user_files/temp.txt";

    public void initialize() {
        server.registerForUpdatesSubtask(subTask -> Platform.runLater(this::refresh));
        server.registerForUpdatesTag(tag -> Platform.runLater(this::refresh));
        server.registerForUpdatesCard(card -> Platform.runLater(() -> {
            boolean check = server.checkCard(card);
            System.out.println("First: " + check);

            if (check) {
                System.out.println(check);
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Card has been deleted.");
                alert.setContentText("We are very sorry, but the card you are currently editing has been deleted by another user");
                alert.showAndWait();
                switchBoard((Stage) cardLabel.getScene().getWindow());
                server.stop();
            } else {
                refresh();
            }
        }));
    }

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

    public CardOverviewController() {

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
     * @param event the event triggering the function
     * @throws IOException the exception which might be caused
     */

    public void switchToBoardScene(javafx.event.Event event) throws IOException {
        switchBoard((Stage) ((Node) event.getSource()).getScene().getWindow());
    }

    public void switchBoard(Stage stage) {
        var cardOverview = FXML.load(BoardOverviewController.class, "client", "scenes", "BoardOverview.fxml");
        cardOverview.getKey().setFileName(fileName);
        cardOverview.getKey().setBoard(board);
        cardOverview.getKey().refresh();
        primaryStage = stage;
        overview = new Scene(cardOverview.getValue());
        primaryStage.setScene(overview);
        primaryStage.show();
        server.stop();
    }

    /**
     * Adding/Updating a task description.
     *
     * @param actionEvent the event
     */
    public void addDescription(javafx.event.ActionEvent actionEvent) {
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
    public void updateName(javafx.event.ActionEvent actionEvent) {
        Card card = server.getCardsById(cardId);
        TextInputDialog dialog = new TextInputDialog(card.getName());
        dialog.setTitle("Change the name of the card");
        dialog.setHeaderText("Please enter the new name for the card:");
        dialog.showAndWait().ifPresent(name -> {

            if (!name.isEmpty()) {
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
    public void addSubTask() {
        Card card = server.getCardsById(cardId);

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("SubTask name");
        dialog.setHeaderText("Please enter the name of the subtask");
        dialog.showAndWait().ifPresent(name -> {

            if (!name.isEmpty()) {
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
     * @param subTask     the subtask to be edited
     */
    private void editSubTask(ActionEvent actionEvent, SubTask subTask) {
        Card card = server.getCardsById(cardId);
        //   SubTask subTask = server.getSubtaskById(subtaskId);
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("SubTask new name");
        dialog.setHeaderText("Please enter the new name of the subtask");
        dialog.showAndWait().ifPresent(name -> {
            if (!name.isEmpty()) {
                try {
                    server.sendCard(subTask.getCard());
                    subTask.setTitle(name);
                    server.updateSubtask(subTask, name);
                } catch (WebApplicationException e) {
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
     * @param card    - the card that has the subtask
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
    public void showSubTaskList(SubTask subTask) {
        Button up = new Button("\u2191");
        up.setStyle("-fx-font-size: 10px;");
        up.setOnAction(event -> moveUp(event, subTask, this.cardId));

        Button down = new Button("\u2193");
        down.setStyle("-fx-font-size: 10px;");
        down.setOnAction(event -> moveDown(event, subTask, this.cardId));

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
        editST.setOnAction(event -> editSubTask(event, subTask));
        Button deleteST = new Button("\uD83D\uDDD9");
        deleteST.setStyle("-fx-font-size: 10px;");
        deleteST.setOnAction(event -> deleteSubTask(event, subTask));
        HBox hBoxButtons = new HBox(editST, deleteST, up, down);

        HBox hBox = new HBox();
        hBox.setSpacing(100);
        hBox.getChildren().addAll(hBoxCB, hBoxButtons);

        vBox.getChildren().add(hBox);
    }

    /**
     * Moving a subtask down to mark lower priority.
     *
     * @param event   the event
     * @param subTask the subtask
     * @param cardid  the card of the subtask
     */
    private void moveDown(ActionEvent event, SubTask subTask, long cardid) {
        List<SubTask> subTasks = new ArrayList<>();
        Card card = server.getCardsById(cardId);
        int idx = card.getSubTasks().indexOf(subTask);
        System.out.println(idx);
        if (idx < card.getSubTasks().size() - 1) {
            for (int i = 0; i < card.getSubTasks().size(); i++) {
                subTasks.add(card.getSubTasks().get(i));
                server.deleteSubtask(card.getSubTasks().get(i));
            }
            vBox.getChildren().clear();
            subTasks.set(idx, card.getSubTasks().get(idx + 1));
            subTasks.set(idx + 1, subTask);
            for (SubTask task : subTasks) {
                try {
                    server.sendCard(card);
                    server.saveSubtask(task);
                } catch (WebApplicationException e) {
                    var alert = new Alert(Alert.AlertType.ERROR);
                    alert.initModality(Modality.APPLICATION_MODAL);
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                }
            }
//             server.sendList(this.list);
//            server.saveCard(card);
            refresh();
        }
    }

    /**
     * Moving a subtask up to mark higher priority.
     *
     * @param event   the event
     * @param subTask the subtask
     * @param cardid  the card of the subtask
     */

    private void moveUp(ActionEvent event, SubTask subTask, long cardid) {
        List<SubTask> subTasks = new ArrayList<>();
        Card card = server.getCardsById(cardId);
        int idx = card.getSubTasks().indexOf(subTask);
        System.out.println(idx);
        if (idx > 0) {
            for (int i = 0; i < card.getSubTasks().size(); i++) {
                subTasks.add(card.getSubTasks().get(i));
                server.deleteSubtask(card.getSubTasks().get(i));
            }
            vBox.getChildren().clear();
            subTasks.set(idx, card.getSubTasks().get(idx - 1));
            subTasks.set(idx - 1, subTask);
            for (SubTask task : subTasks) {
                try {
                    server.sendCard(card);
                    server.saveSubtask(task);
                } catch (WebApplicationException e) {
                    var alert = new Alert(Alert.AlertType.ERROR);
                    alert.initModality(Modality.APPLICATION_MODAL);
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                }
            }
//             server.sendList(this.list);
//            server.saveCard(card);
            refresh();
        }
    }
    //}

    /**
     * Deleting a subtask from database.
     *
     * @param actionEvent the action event
     * @param subtask     the subtask to be deleted
     */

    private void deleteSubTask(ActionEvent actionEvent, SubTask subtask) {
        HBox clicked = (HBox) ((Button) actionEvent.getSource()).getParent();
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
        refreshTags();
        refreshSchemes();
    }

    public void refreshSchemes() {
        colorSchemes.getChildren().clear();
        Card card = server.getCardsById(cardId);
        List<ColorScheme> schemes = list.getBoard().getSchemes();
        for (ColorScheme s : schemes) {
            HBox hBox = new HBox(10);
            hBox.setMinSize(150, 20);
            hBox.setMaxSize(150, 20);

            Label name = new Label(s.getName());
            Label back = new Label("B");
            Rectangle backColor = new Rectangle(15, 15);
            backColor.setFill(Color.web(s.getBackgroundColor()));
            Label font = new Label("F");
            Rectangle fontColor = new Rectangle(15, 15);
            fontColor.setFill(Color.web(s.getFontColor()));
            Button apply = new Button("\u2713");
            if (card.getFontColor().equals(s.getFontColor()) && card.getBackgroundColor().equals(s.getBackgroundColor())) {
                name.setStyle("-fx-font-weight: bold");
                apply.setVisible(false);
            }
            setUpApplyButton(apply, s);
            hBox.getChildren().add(name);
            hBox.getChildren().add(back);
            hBox.getChildren().add(backColor);
            hBox.getChildren().add(font);
            hBox.getChildren().add(fontColor);
            hBox.getChildren().add(apply);
            colorSchemes.getChildren().add(hBox);
        }
    }

    private void setUpApplyButton(Button apply, ColorScheme scheme) {
        apply.setMaxSize(20, 20);
        apply.setMinSize(20, 20);

        apply.setAlignment(Pos.CENTER);
        apply.setStyle("-fx-background-color: white; -fx-text-fill: green; -fx-font-size: 8 px");
        apply.setOnMouseEntered(event -> apply.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-font-size: 8 px"));
        apply.setOnMouseExited(event -> apply.setStyle("-fx-background-color: white; -fx-text-fill: green; -fx-font-size: 8 px"));
        apply.setOnAction(event -> {
            Card card = server.getCardsById(cardId);
            card.setBackgroundColor(scheme.getBackgroundColor());
            card.setFontColor(scheme.getFontColor());
            server.sendList(list);
            server.saveCard(card);
            refresh();
        });
    }


    private void refreshTags() {
        Card card = server.getCardsById(cardId);
        hbox.getChildren().clear();
        for (Tag tag : card.getTags()) {
            Label tagLabel = new Label(tag.getTitle());
            Color color = Color.web(tag.getColor());
            Background background = new Background(new BackgroundFill(color, null, null));
            tagLabel.setBackground(background);
            tagLabel.setAlignment(Pos.CENTER);
            //  tagLabel.setStyle("-fx-background-radius: 20;");
            tagLabel.setMinSize(100, 40);
            hbox.getChildren().add(tagLabel);
            hbox.setSpacing(10);
        }
    }

    /**
     * Refreshing a card's details.
     */
    public void refreshCardDetails() {
        Card card = server.getCardsById(cardId);
        cardLabel.setText(card.getName());
        description.setText(card.getDescription());
    }

    /**
     * Refreshing the subtasks of current card.
     */
    public void refreshSubTasks() {
        Card card = server.getCardsById(cardId);

        vBox.getChildren().clear();
        vBox.setSpacing(5);
        vBox.setAlignment(Pos.TOP_CENTER);

        for (SubTask subTask : card.getSubTasks()) {
            subTask.setCard(card);
            showSubTaskList(subTask);
        }
    }

    /**
     * Switching to the scene to choose your tags.
     *
     * @param actionEvent the action event
     * @throws IOException possible error
     */
    public void switchToChooseTagScene(javafx.event.ActionEvent actionEvent) throws IOException {
        var chooseTagOverview = FXML.load(ChooseTagController.class, "client", "scenes", "ChooseTag.fxml");
        chooseTagOverview.getKey().setFileName(fileName);
        chooseTagOverview.getKey().setBoard(board);
        chooseTagOverview.getKey().setCardId(cardId);
        chooseTagOverview.getKey().setList(list);
        chooseTagOverview.getKey().refresh();
        primaryStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        overview = new Scene(chooseTagOverview.getValue());
        primaryStage.setScene(overview);
        primaryStage.show();
    }

    /**
     * Checks if the pressed key was ESCAPE and returns to the board if it is.
     *
     * @param keyEvent the event of the key being pressed
     */
    public void exitIfEscape(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ESCAPE)) {
            try {
                switchToBoardScene(keyEvent);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
