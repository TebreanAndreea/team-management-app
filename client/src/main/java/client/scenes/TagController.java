package client.scenes;

import client.MyFXML;
import client.MyModule;
import client.utils.ServerUtils;
import com.google.inject.Injector;
import commons.Board;
import commons.Card;


import commons.Tag;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.WebApplicationException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

import static com.google.inject.Guice.createInjector;

public class TagController {

    private Stage primaryStage;
    private Scene overview;
    @FXML
    VBox vBox;

    private Board board;
    private ServerUtils server;
    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);
    private String fileName = "user_files/temp.txt";


    /**
     * Setter for the file name.
     *
     * @param fileName the name of the file where the user's boards are stored
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
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
     * @param board the board of this tag
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
                Tag tag = new Tag(name,board);
                saveTagDB(tag,board);

                Button tagButton = new Button(tag.getTitle());
                tagButton.setMinSize(200, 50);

                Button editButton = new Button("edit");
                editButton.setOnAction(event -> editTagName(event, tag));
                // editButton.setOnAction(this::editTagName);

                Button deleteButton = new Button("delete");
                deleteButton.setOnAction(event -> deleteTag(event, tag));

                ColorPicker colorPicker = new ColorPicker();
                colorPicker.setMaxSize(10, 10);
                HBox hbox = new HBox(tagButton,editButton,deleteButton, colorPicker);
                colorPicker.setOnAction(event -> {
                    Color color = colorPicker.getValue();
                    tagButton.setBackground(new Background(new BackgroundFill(color, null, null)));
                    tag.setColor(color.toString());
                    saveTagDB(tag, this.board);
                    //  refresh();
                });

                if(tag.getColor() != null) {
                    colorPicker.setBackground(new Background(new BackgroundFill(Color.web(tag.getColor()), null, null)));
                    Color color = Color.web(tag.getColor());
                    tagButton.setBackground(new Background(new BackgroundFill(color, null, null)));
                }

                hbox.setSpacing(10);
                vBox.setSpacing(10);

                vBox.getChildren().add(hbox);
            }
            else
            {
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
     * @param board the board of this tag
     */
    public void saveTagDB(Tag tag, Board board){
        try {
            server.sendBoardToTag(board);
            server.saveTag(tag);
        } catch (WebApplicationException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }



    /**
     * Method which opens a dialog box for changing the tag name.
     * @param actionEvent the action event
     * @param tag the tag to be edited
     */
    public void editTagName(javafx.event.ActionEvent actionEvent, Tag tag){
        Button editButton = (Button) actionEvent.getSource();
        HBox hBox = (HBox) editButton.getParent();
        Button tagButton = (Button) hBox.getChildren().get(0);
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Tag name");
        dialog.setHeaderText("Please enter the name of the tag");
        dialog.showAndWait().ifPresent(name -> {
            if(!name.isEmpty()) {
                tagButton.setText(name);
                tag.setTitle(name);
                server.sendBoardToTag(this.board);
                server.saveTag(tag);
            } else {
                //sends alert and return to the input dialog after
                Alert emptyField = new Alert(Alert.AlertType.ERROR);
                emptyField.setContentText("Name field was submitted empty, please enter a name");
                emptyField.showAndWait();
            }

        });
    }

    /**
     * Going back to the board overview.
     *
     * @param actionEvent the event
     * @throws IOException the exception
     */
    public void switchToBoardScene(javafx.event.ActionEvent actionEvent) throws IOException {
        var boardOverview = FXML.load(BoardOverviewController.class, "client", "scenes", "BoardOverview.fxml");
        boardOverview.getKey().setFileName(fileName);
        boardOverview.getKey().setBoard(board);
        boardOverview.getKey().refresh();
        primaryStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        overview = new Scene(boardOverview.getValue());
        primaryStage.setScene(overview);
        primaryStage.show();
    }

    /**
     * Refreshing all the available card.
     *
     */

    public void refresh(){
        long id = board.getBoardId();
        vBox.getChildren().clear();
        if (id != 0) {
            try {
                board = server.getBoardByID(id);
            } catch (BadRequestException e) {
                Label noBoard = new Label("The board you are trying to access may have been deleted or does not exist.");
                noBoard.setWrapText(true);
                noBoard.setTextAlignment(TextAlignment.CENTER);
                noBoard.setPrefWidth(500);
                noBoard.setFont(new Font(20));
                return;
            }
        }
        List<Tag> tags = board.getTags();
       // map = new HashMap<>();
        for (Tag tag : tags)
        {
            // construct the vbox from frontend, just to see the layout
            Button tagButton = new Button(tag.getTitle());
            tagButton.setMinSize(200, 50);

            Button editButton = new Button("edit");
            editButton.setOnAction(event -> editTagName(event, tag));
           // editButton.setOnAction(this::editTagName);

            Button deleteButton = new Button("delete");
            deleteButton.setOnAction(event -> deleteTag(event, tag));

            ColorPicker colorPicker = new ColorPicker();
            colorPicker.setMaxSize(10, 10);
            HBox hbox = new HBox(tagButton,editButton,deleteButton, colorPicker);
            colorPicker.setOnAction(event -> {
                Color color = colorPicker.getValue();
                tagButton.setBackground(new Background(new BackgroundFill(color, null, null)));
                tag.setColor(color.toString());
                saveTagDB(tag, this.board);
              //  refresh();
            });

            if(tag.getColor() != null) {
                colorPicker.setBackground(new Background(new BackgroundFill(Color.web(tag.getColor()), null, null)));
                Color color = Color.web(tag.getColor());
                tagButton.setBackground(new Background(new BackgroundFill(color, null, null)));
            }

            hbox.setSpacing(10);
            vBox.setSpacing(10);

            vBox.getChildren().add(hbox);
        }
    }

    /**
     * Deleting a tag from the database.
     *
     * @param event the event
     * @param tag the tag to be deleted
     */

    private void deleteTag(ActionEvent event, Tag tag) {
        HBox clicked = (HBox)((Button) event.getSource()).getParent();
        VBox vbox = (VBox)clicked.getParent();
        vbox.getChildren().remove(clicked);
        try {
            tag.removeTagFromCards();
            server.sendBoard(board);
            server.deleteTag(tag.getTagId(), tag);
        } catch (WebApplicationException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}
