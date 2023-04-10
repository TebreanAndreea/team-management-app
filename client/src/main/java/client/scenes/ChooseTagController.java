package client.scenes;

import client.MyFXML;
import client.MyModule;
import client.utils.ServerUtils;
import com.google.inject.Injector;
import commons.Board;
import commons.Card;
import commons.Listing;
import commons.Tag;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

import static com.google.inject.Guice.createInjector;

public class ChooseTagController {

    public VBox vbox;
    private Stage primaryStage;
    private Scene overview;
    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);
    private ServerUtils server;
    public Listing list;
    private long cardId;
    private Card curCard;
    private Board board = new Board("test", "", "");
    private String fileName = "user_files/temp.txt";

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
     * Setter for the list.
     *
     * @param list the list of the card
     */
    public void setList(Listing list) {
        this.list = list;
    }

    /**
     * Setter for the current card.
     *
     * @param cardId the card's id
     */
    public void setCardId(long cardId) {
        this.cardId = cardId;
        this.curCard = server.getCardsById(cardId);
    }

    public void initialize() {
        server.registerForUpdatesCard(q -> Platform.runLater(() ->
        {
            board = server.getBoardByID(board.getBoardId());
            List<Listing> lists = board.getLists();
            for (Listing l : lists) {
                for (Card c : l.getCards()) {
                    if (c.getName().equals(curCard.getName())) {
                        setList(l);
                        setCardId(c.getCardId());
                    }
                }
            }
            refresh();
        }));
        server.registerForMessages("/topic/tag", Tag.class, q -> Platform.runLater(this::refresh));
    }

    /**
     * Controler for the class.
     *
     * @param server the server
     */

    @Inject
    public ChooseTagController(ServerUtils server) {
        this.server = server;
    }

    /**
     * Empty controler for class.
     */
    public ChooseTagController() {

    }

    /**
     * Going back to the card details scene.
     *
     * @param actionEvent the action event
     * @throws IOException possible exception
     */
    public void switchToCardScene(ActionEvent actionEvent) throws IOException {
        server.stop();
        var cardOverview = FXML.load(CardOverviewController.class, "client", "scenes", "CardOverview.fxml");
        cardOverview.getKey().setCardId(cardId);
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
     * Refreshing the available tags.
     */
    public void refresh() {
        vbox.getChildren().clear();
        board = server.getBoardByID(board.getBoardId());
        List<Tag> tags = board.getTags();
        Card card = server.getCardsById(cardId);
        for (Tag tag : tags) {
            CheckBox checkBox = new CheckBox(tag.getTitle());
            // checkBox.setStyle("-fx-font-size: 40px;");
            if (tag.getColor() != null) {
                Color color = Color.web(tag.getColor());
                Color bg = color.deriveColor(1, 1, 1, 0.5);
                Background background = new Background(new BackgroundFill(bg, new CornerRadii(5), null));
                checkBox.setBackground(background);
                checkBox.setBorder(new Border(new BorderStroke(color, BorderStrokeStyle.SOLID, new CornerRadii(5), new BorderWidths(5))));
            }
            checkBox.setMinSize(200, 50);
            checkBox.setAlignment(Pos.CENTER);
            vbox.setAlignment(Pos.CENTER);
            vbox.setSpacing(20);
            vbox.getChildren().add(checkBox);

            checkBox.setSelected(card.getTags().contains(tag));
            checkBox.selectedProperty().addListener((obs, old, newVal) -> {
                server.sendList(list);
                if (newVal == true) {
                    server.addTag(card, tag);
                    //  tag.getCards().add(card);
                } else {
                    server.removeTag(card, tag);
                    //  tag.getCards().remove(card);
                }
                //  server.sendList(list);
                //server.saveCard(card);

            });
        }
    }

}
