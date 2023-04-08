package client.scenes;

import client.MyFXML;
import client.MyModule;
import client.utils.ServerUtils;
import com.google.inject.Injector;
import commons.Board;
import commons.Card;
import commons.ColorScheme;
import commons.Listing;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

import static com.google.inject.Guice.createInjector;

public class CustomizationOverviewController {
    public Label LVII;
    public Label LI;
    public Label LII;
    public Label LIII;
    public Label LIV;
    public Label LV;
    public Label LVI;
    public Button resetBoardColor;
    public Button resetListColor;
    public Button addSchemeButton;
    public Button saveButton;
    public Button backButton;
    public AnchorPane pane;
    public ScrollPane scrollPane;
    private Stage primaryStage;
    private Scene overview;
    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);
    private ServerUtils server;
    private Board board = new Board();
    private String fileName = "user_files/temp.txt";

    public ColorPicker boardBackground;
    public ColorPicker boardFont;
    public ColorPicker listBackground;
    public ColorPicker listFont;

    public VBox vBox;

    private Map<HBox, ColorScheme> map = new HashMap<>();

    /**
     * Setter for the file name.
     *
     * @param fileName the name of the file where the user's boards are stored
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Inject
    public CustomizationOverviewController(ServerUtils server) {
        this.server = server;
    }

    /**
     * Initializes the controller.
     */
    public void initialize() {
        server.registerForMessages("/topic/boards", Board.class, q -> Platform.runLater(() -> {
            refresh();
        }));
        server.registerForMessages("/topic/colors", ColorScheme.class, q -> Platform.runLater(() -> {
            refresh();
        }));

        server.registerForMessages("/topic/card", Card.class, q -> Platform.runLater(this::refresh));

        //refresh();
    }


    public void print(ColorScheme scheme) {
        System.out.println(scheme);
    }

    /**
     * refreshes the overview.
     */
    public void refresh() {
        System.out.println("refreshed customization");
        board = server.getBoardByID(board.getBoardId());
        boardBackground.setValue(Color.valueOf(board.getBackgroundColor()));
        boardFont.setValue(Color.valueOf(board.getTextColor()));
        listBackground.setValue(Color.valueOf(board.getListBackgroundColor()));
        listFont.setValue(Color.valueOf(board.getListTextColor()));
        loadSchemes();
        colorEverything();
    }

    /**
     * Sets up the colors of a board.
     */
    private void colorEverything() {
        Color background = Color.web(board.getBackgroundColor());
        Color font = Color.web(board.getTextColor());
        colorButton(resetBoardColor);
        colorButton(resetListColor);
        colorButton(addSchemeButton);
        colorButton(backButton);
        colorButton(saveButton);
        pane.setBackground(new Background(new BackgroundFill(background, new CornerRadii(0), new Insets(0) )));
        LI.setTextFill(font);
        LII.setTextFill(font);
        LIII.setTextFill(font);
        LIV.setTextFill(font);
        LV.setTextFill(font);
        LVI.setTextFill(font);
        LVII.setTextFill(font);
        scrollPane.setStyle("-fx-background-color: " + background);
    }

    /**
     * Colors the button.
     * @param button - the button that needs coloring
     */
    private void colorButton(Button button){
        button.setStyle("-fx-background-color:" + board.getBackgroundColor());
        button.setTextFill(Color.web(board.getTextColor()));
        button.setBorder(new Border(new BorderStroke(Color.web(board.getTextColor()), BorderStrokeStyle.SOLID, new CornerRadii(5), BorderWidths.DEFAULT)));
        button.setOnMouseEntered(event -> {
            button.setStyle("-fx-background-color:" + board.getTextColor());
            button.setTextFill(Color.web(board.getBackgroundColor()));
        });
        button.setOnMouseExited(event -> {
            button.setStyle("-fx-background-color:" + board.getBackgroundColor());
            button.setTextFill(Color.web(board.getTextColor()));
        });
    }

    /**
     * Resets the colors of the list.
     */
    public void resetListColors() {
        listBackground.setValue(Color.valueOf(board.getListBackgroundColorDefault()));
        listFont.setValue(Color.valueOf(board.getListTextColorDefault()));
    }

    /**
     * Resets the colors of the board.
     */
    public void resetBoardColors() {
        boardBackground.setValue(Color.valueOf(board.getBackgroundColorDefault()));
        boardFont.setValue(Color.valueOf(board.getTextColorDefault()));
    }

    /**
     * Setter for the board.
     *
     * @param board - the board which is being customized
     */
    public void setBoard(Board board) {
        this.board = board;
    }

    /**
     * The method switches to one board which was chosen.
     *
     * @param actionEvent - the event that was triggered when a user decided which board he wants
     */
    public void switchToBoard(ActionEvent actionEvent) {
        Button goToBoard = (Button) actionEvent.getSource();
        var boardOverview = FXML.load(BoardOverviewController.class, "client", "scenes", "BoardOverview.fxml");
        boardOverview.getKey().setFileName(fileName);
        boardOverview.getKey().setBoard(board);
        boardOverview.getKey().refresh();
        primaryStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        overview = new Scene(boardOverview.getValue());
        primaryStage.setScene(overview);
    }

    /**
     * Updates the colors of the board.
     *
     * @param actionEvent - the event that was triggered
     */
    public void updateColors(ActionEvent actionEvent) {
        board.setBackgroundColor("#" + boardBackground.getValue().toString().substring(2));
        board.setTextColor("#" + boardFont.getValue().toString().substring(2));
        board.setListBackgroundColor("#" + listBackground.getValue().toString().substring(2));
        board.setListTextColor("#" + listFont.getValue().toString().substring(2));
        server.addBoard(board);
        refresh();
    }

    public void loadSchemes() {
        vBox.getChildren().clear();
        for (ColorScheme scheme : board.getSchemes()) {
            HBox hBox = new HBox(5);
            hBox.minHeight(40);
            hBox.maxHeight(40);
            VBox nameDefault = new VBox();
            nameDefault.setStyle("-fx-background-color: transparent");
            nameDefault.setMinSize(90, 40);
            nameDefault.setMaxSize(90, 40);
            Label name = new Label(scheme.getName());
            name.setTextFill(Color.web(board.getTextColor()));
            name.setOnMouseClicked(event -> updateName(event, name));
            HBox defHbox = new HBox();
            defHbox.maxHeight(20);
            defHbox.minHeight(20);
            defHbox.minWidth(90);
            defHbox.maxWidth(90);
            defHbox.setStyle("-fx-background-color: transparent");
            Label def = new Label("Default");
            def.setTextFill(Color.web(board.getTextColor()));
            nameDefault.getChildren().add(name);
            defHbox.getChildren().add(def);
            nameDefault.getChildren().add(defHbox);
            if (scheme.isDef()) {
                name.setStyle("-fx-font-weight: bold");
                def.setStyle("-fx-font-weight: bold");

            } else {
                Button setDef = new Button("V");
                setDefButton(setDef);
                defHbox.getChildren().add(setDef);
            }
            hBox.getChildren().add(nameDefault);
            Label font = new Label("F");
            font.setAlignment(Pos.CENTER);
            font.setMinHeight(40);
            font.setTextFill(Color.web(board.getTextColor()));
            ColorPicker colorFont = new ColorPicker(Color.web(scheme.getFontColor()));
            Label back = new Label("B");
            back.setAlignment(Pos.CENTER);
            back.setMinHeight(40);
            back.setTextFill(Color.web(board.getTextColor()));
            ColorPicker colorBack = new ColorPicker(Color.web(scheme.getBackgroundColor()));
            setColorControll(colorFont);
            setColorControll(colorBack);
            hBox.getChildren().add(back);
            hBox.getChildren().add(colorBack);
            hBox.getChildren().add(font);
            hBox.getChildren().add(colorFont);
            Button delete = new Button("X");
            setDeleteButton(delete);
            hBox.getChildren().add(delete);
            hBox.setStyle("-fx-background-color: transparent;");
            vBox.getChildren().add(hBox);

            map.put(hBox, scheme);
        }
    }

    private void setDefButton(Button setDef) {
        setDef.setMaxSize(20, 20);
        setDef.setMinSize(20, 20);
        setDef.setAlignment(Pos.CENTER);
//        setDef.setStyle("-fx-background-color: white; -fx-text-fill: green;");
//        setDef.setOnMouseEntered(event -> {
//            setDef.setStyle("-fx-background-color: green; -fx-text-fill: white;");
//        });
//        setDef.setOnMouseExited(event -> {
//            setDef.setStyle("-fx-background-color: white; -fx-text-fill: green;");
//        });
        setDef.setOnAction(this::checkBoxEvent);
        colorButton(setDef);
        setDef.setFont(new Font(8));
    }

    private void setColorControll(ColorPicker colorPicker) {
        colorPicker.setMaxSize(6, 40);
        colorPicker.setMaxSize(40, 40);
        colorPicker.setStyle("-fx-background-color: transparent");
        colorPicker.setOnAction(event -> {
            HBox hBox = (HBox) colorPicker.getParent();
            ColorScheme scheme = map.get(hBox);
            ColorScheme oldScheme = new ColorScheme(scheme.getName(), scheme.getBackgroundColor(), scheme.getFontColor(), board);
            ColorPicker back = (ColorPicker) hBox.getChildren().get(2);
            ColorPicker font = (ColorPicker) hBox.getChildren().get(4);
            String backString = String.format("#%02X%02X%02X",
                    (int) (back.getValue().getRed() * 255),
                    (int) (back.getValue().getGreen() * 255),
                    (int) (back.getValue().getBlue() * 255));
            String fontString = String.format("#%02X%02X%02X",
                    (int) (font.getValue().getRed() * 255),
                    (int) (font.getValue().getGreen() * 255),
                    (int) (font.getValue().getBlue() * 255));


            if (scheme.getBackgroundColor().equals(backString) && scheme.getFontColor().equals(fontString)) {
                return;
            }
            if (!scheme.getBackgroundColor().equals(backString)) {
                scheme.setBackgroundColor(backString);
                if (scheme.isDef()) {
                    board.setCardBackgroundColor(backString);
                    server.addBoard(board);
                }
            }
            if (!scheme.getFontColor().equals(fontString)) {
                scheme.setFontColor(fontString);
                if (scheme.isDef()) {
                    board.setCardFontColor(fontString);
                    server.addBoard(board);
                }
            }

            updateCardColors(scheme, oldScheme);
            server.sendBoardToScheme(board);
            server.saveColorScheme(scheme);
            refresh();
        });
    }

    private void updateCardColors(ColorScheme newScheme, ColorScheme oldScheme) {
        for (Listing l : board.getLists()) {
            for (Card c : l.getCards()) {
                if (c.getSchemeName().equals(oldScheme.getName())) {
                    c.setSchemeName(newScheme.getName());
                    c.setBackgroundColor(newScheme.getBackgroundColor());
                    c.setFontColor(newScheme.getFontColor());
                    server.sendList(l);
                    server.saveCard(c, false);
                }
            }
        }
    }

    private void updateName(javafx.scene.input.MouseEvent event, Label name) {
        if (event.getClickCount() == 2) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Scheme name");
            dialog.setHeaderText("Please enter the new name of the scheme");
            dialog.showAndWait().ifPresent(text -> {

                if (!text.isEmpty()) {
                    ColorScheme scheme = map.get(name.getParent().getParent());
                    scheme.setName(text);
                    server.sendBoardToScheme(board);
                    scheme = server.saveColorScheme(scheme);

                } else {
                    Alert emptyField = new Alert(Alert.AlertType.ERROR);
                    emptyField.setContentText("Name field was submitted empty, please enter a name");
                    emptyField.showAndWait();
                    addScheme();
                }
            });
            refresh();
        }
    }


    public void checkBoxEvent(ActionEvent event) {

        HBox hBox = (HBox) ((Button) event.getSource()).getParent().getParent().getParent();
        ColorScheme newDef = map.get(hBox);
        for (ColorScheme c : board.getSchemes()) {
            if (c.isDef()) {
                c.setDef(false);
                server.sendBoardToScheme(board);
                server.saveColorScheme(c);
                break;
            }
        }
        newDef.setDef(true);
        board.setCardBackgroundColor(newDef.getBackgroundColor());
        board.setCardFontColor(newDef.getFontColor());
        server.addBoard(board);
        server.sendBoardToScheme(board);
        server.saveColorScheme(newDef);

        refresh();
    }

    public void setDeleteButton(Button delete) {
        delete.setMaxSize(30, 30);
        delete.setMinSize(30, 30);
        delete.setAlignment(Pos.CENTER);
//        delete.setStyle("-fx-background-color: white; -fx-text-fill: red;");
//        delete.setOnMouseEntered(event -> {
//            delete.setStyle("-fx-background-color: red; -fx-text-fill: white;");
//        });
//        delete.setOnMouseExited(event -> {
//            delete.setStyle("-fx-background-color: white; -fx-text-fill: red;");
//        });
        colorButton(delete);
        delete.setOnAction(this::deleteScheme);
    }

    public void deleteScheme(ActionEvent event) {

        ColorScheme scheme = map.get((HBox) ((Button) event.getSource()).getParent());
        if (scheme.isDef()) {
            Alert defaultScheme = new Alert(Alert.AlertType.ERROR);
            defaultScheme.setContentText("You can't change your default color scheme, please change it or add a new one");
            defaultScheme.showAndWait();
            return;
        }
        ColorScheme def = scheme;
        for (ColorScheme s : board.getSchemes()) {
            if (s.isDef()) {
                def = s;
                break;
            }
        }
        updateCardColors(def, scheme);
        map.remove((HBox) ((Button) event.getSource()).getParent());
        server.deleteScheme(scheme.getSchemeId());
        board.getSchemes().remove(scheme);
        refresh();
    }


    public void addScheme() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Scheme name");
        dialog.setHeaderText("Please enter the name of the scheme");
        dialog.showAndWait().ifPresent(name -> {

            if (!name.isEmpty()) {
                ColorScheme scheme = new ColorScheme(name, "#ffffff", "#000000", board);
                server.sendBoardToScheme(board);
                scheme = server.saveColorScheme(scheme);
                board.getSchemes().add(scheme);

            } else {
                Alert emptyField = new Alert(Alert.AlertType.ERROR);
                emptyField.setContentText("Name field was submitted empty, please enter a name");
                emptyField.showAndWait();
                addScheme();
            }
        });
        refresh();
    }


}
