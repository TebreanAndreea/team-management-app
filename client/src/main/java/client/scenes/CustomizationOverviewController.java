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
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

import static com.google.inject.Guice.createInjector;

public class CustomizationOverviewController {
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
        server.registerForMessages("/topic/boards", Board.class, q -> Platform.runLater( ()->{
            refresh();
        }));
        server.registerForMessages("/topic/colors", ColorScheme.class, q -> Platform.runLater( ()->{
            refresh();
        }));

        server.registerForMessages("/topic/card", Card.class, q -> Platform.runLater(this::refresh));

        //refresh();
    }


    public void print( ColorScheme scheme){
        System.out.println(scheme);
    }
    /**
     * refreshes the overview.
     */
    public void refresh() {
        System.out.println(board.getBoardId());
        board = server.getBoardByID(board.getBoardId());
        boardBackground.setValue(Color.valueOf(board.getBackgroundColor()));
        boardFont.setValue(Color.valueOf(board.getTextColor()));
        listBackground.setValue(Color.valueOf(board.getListBackgroundColor()));
        listFont.setValue(Color.valueOf(board.getListTextColor()));
        loadSchemes();
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
            nameDefault.setMinSize(90, 40);
            nameDefault.setMaxSize(90, 40);
            Label name = new Label(scheme.getName());
            name.setOnMouseClicked(event -> updateName(event, name));
            HBox defHbox = new HBox();
            defHbox.maxHeight(20);
            defHbox.minHeight(20);
            defHbox.minWidth(90);
            defHbox.maxWidth(90);
            Label def = new Label("Default");
            CheckBox check = new CheckBox();
            check.maxHeight(20);
            check.maxWidth(20);
            nameDefault.getChildren().add(name);
            defHbox.getChildren().add(def);
            nameDefault.getChildren().add(defHbox);
            check.setOnAction(e -> checkBoxEvent(check));
            if (scheme.isDef()) {
                name.setStyle("-fx-font-weight: bold");
                def.setStyle("-fx-font-weight: bold");

            } else {
                defHbox.getChildren().add(check);
            }
            hBox.getChildren().add(nameDefault);
            Label font = new Label("F");
            ColorPicker colorFont = new ColorPicker(Color.web(scheme.getFontColor()));
            Label back = new Label("B");
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
            vBox.getChildren().add(hBox);

            map.put(hBox, scheme);
        }
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
                if (scheme.isDef())
                {
                    board.setCardBackgroundColor(backString);
                    server.addBoard(board);
                }
            }
            if (!scheme.getFontColor().equals(fontString)){
                scheme.setFontColor(fontString);
                if (scheme.isDef())
                {
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
        for (Listing l : board.getLists())
        {
            for(Card c :l.getCards())
            {
                if(c.getSchemeName().equals(oldScheme.getName()))
                {
                    c.setSchemeName(newScheme.getName());
                    c.setBackgroundColor(newScheme.getBackgroundColor());
                    c.setFontColor(newScheme.getFontColor());
                    server.sendList(l);
                    server.saveCard(c);
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


    public void checkBoxEvent(CheckBox check) {

        if (check.isSelected()) {
            HBox hBox = (HBox) (check.getParent().getParent().getParent());
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
            server.sendBoard(board);
            server.saveColorScheme(newDef);

            refresh();
        }


    }

    public void setDeleteButton(Button delete) {
        delete.setMaxSize(30, 30);
        delete.setMinSize(30, 30);
        delete.setAlignment(Pos.CENTER);
        delete.setStyle("-fx-background-color: white; -fx-text-fill: red;");
        delete.setOnMouseEntered(event -> {
            delete.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        });
        delete.setOnMouseExited(event -> {
            delete.setStyle("-fx-background-color: white; -fx-text-fill: red;");
        });
        delete.setOnAction(event -> {
            deleteScheme(event);
        });
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
        for (ColorScheme s : board.getSchemes())
        {
            if(s.isDef()) {
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
