package client.scenes;

import client.MyFXML;
import client.MyModule;
import client.utils.ServerUtils;
import com.google.inject.Injector;
import commons.Board;
import commons.ColorScheme;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.scene.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;


import javax.inject.Inject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static com.google.inject.Guice.createInjector;

public class InitialOverviewController {

    private Stage primaryStage;
    private Scene overview;

    @FXML
    public VBox vBoxBoard;

    @FXML
    public ScrollPane scrollPane;

    @FXML
    public ScrollPane masterScrollPane;
    private ServerUtils server;
    private MainController maincontroller;

    private EventTarget target;

    private Map<Button, Board> boardsMap;

    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);
    private Map<Long, Boolean> accessMap = new HashMap<>();

    private String fileName="user_files/temp.txt";

    @Inject
    public InitialOverviewController(ServerUtils server, MainController mainController) {
        this.server = server;
        this.maincontroller = mainController;
        boardsMap = new HashMap<>();
    }

    /**
     * The initial method to load up all boards.
     */
    public void initialize() {
        File test = new File("build.gradle");
        if(!test.getAbsolutePath().contains("client"))
            fileName = "client/" + fileName;
        server.registerForMessages("/topic/boards", Board.class, q -> Platform.runLater(this::refresh));
        refresh();
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
        Board desireBoard = boardsMap.get(goToBoard);
        boardOverview.getKey().setBoard(desireBoard);
        boardOverview.getKey().setIsAdmin(false);
        boardOverview.getKey().setHasAccess(accessMap.get(desireBoard.getBoardId()));
        boardOverview.getKey().refresh();
        primaryStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        overview = new Scene(boardOverview.getValue());
        primaryStage.setScene(overview);
    }

    /**
     * This method is used for disconnecting a client from the server and switch back to the Connection page.
     *
     * @param actionEvent the action event used when pressing the button
     */
    public void switchToHomePageScene(ActionEvent actionEvent) {
        var homePageOverview = FXML.load(HomePageOverviewController.class, "client", "scenes", "HomePageOverview.fxml");
        primaryStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        overview = new Scene(homePageOverview.getValue());
        primaryStage.setScene(overview);
        primaryStage.setTitle("Connection");
    }


    /**
     * A method that adds a board to the db and UI.
     *
     * @param actionEvent - the event that was triggered by the add board button
     */
    public void addBoard(ActionEvent actionEvent) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Board title");
        dialog.setHeaderText("Please enter a name for the board:");
        dialog.showAndWait().ifPresent(name -> {

            if (!name.isEmpty()) {

                Board res = server.addBoard(new Board(name, "", ""));
                res.setAccessKey();
                ColorScheme scheme = new ColorScheme("Defualt", "#ffffff", "#000000", res);
                scheme.setDef(true);
                server.sendBoardToScheme(res);
                ColorScheme saved = server.saveColorScheme(scheme);
                res.getSchemes().add(saved);
                server.addBoard(res);
                writeNewBoardToFile(res, true);
                refresh();
            } else {
                Alert emptyField = new Alert(Alert.AlertType.ERROR);
                emptyField.setContentText("Name field was submitted empty, please enter a name");
                emptyField.showAndWait();
                addBoard(actionEvent);
            }
        });
    }

    /**
     * Join a board by the access key.
     *
     * @param actionEvent - the event triggered
     */
    public void searchViaKey(ActionEvent actionEvent) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Access key");
        dialog.setHeaderText("Please enter your access key:");
        dialog.showAndWait().ifPresent(key -> {
            if (key.length() != 10) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Input Error");
                alert.setHeaderText(null);
                alert.setContentText("Invalid access key.");
                alert.showAndWait();
            }

            List<Board> boards = server.getBoardsFromDB();
            for (Board b : boards) {
                if (key.equals(b.getAccessKey())) {
                    boolean access = b.getPassword().equals("");
                    writeNewBoardToFile(b, access);
                    var boardOverview = FXML.load(BoardOverviewController.class, "client", "scenes", "BoardOverview.fxml");
                    boardOverview.getKey().setBoard(b);
                    boardOverview.getKey().setFileName(fileName);
                    boardOverview.getKey().setHasAccess(access);
                    boardOverview.getKey().refresh();
                    primaryStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                    overview = new Scene(boardOverview.getValue());
                    primaryStage.setScene(overview);
                    return;
                }
            }

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Input Error");
            alert.setHeaderText(null);
            alert.setContentText("Invalid access key.");
            alert.showAndWait();
        });

    }

    /**
     * A method that refreshes and puts each board in the UI.
     */
    public void refresh() {
        vBoxBoard.getChildren().clear();
        masterScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);


        List<Board> boards = new ArrayList<>();
        String availableBoards = "";
        try (Scanner scanner = new Scanner(new File(fileName))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] lines = line.split(" ");
                long id = Long.parseLong(lines[0]);
                boolean hasAccess = Boolean.parseBoolean(lines[1]);
                try {
                    Board board = server.getBoardByID(id);
                    boards.add(board);
                    accessMap.put(id, hasAccess);
                    availableBoards += (line + "\n");
                } catch (Exception ignored) {

                }

            }
            scanner.close();
            FileOutputStream outputStream = new FileOutputStream(new File(fileName));
            outputStream.write(availableBoards.getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("No such file");
        }

        HBox hbox = new HBox();
        for (int i = 0; i < boards.size(); i++) {
            if (i % 3 == 0) {
                hbox = new HBox();
                vBoxBoard.getChildren().add(hbox);
                hbox.setPrefHeight(70);
                hbox.setSpacing(20);
            }
            Button newBoard = new Button();
            newBoard.setMaxSize(90, 60);
            newBoard.setMinSize(90, 60);
            newBoard.setText(boards.get(i).getTitle());
            newBoard.setOnAction(this::switchToBoard);
            normalStyle(newBoard);

            boardsMap.put(newBoard, boards.get(i));
            newBoard.setOnMouseEntered(event -> {
                var root = FXML.load(BoardOverviewController.class, "client", "scenes", "BoardOverview.fxml");
                root.getKey().setBoard(boardsMap.get(newBoard));
                root.getKey().refresh();
                SubScene subScene = new SubScene(root.getValue(), 600, 400);
                scrollPane.setContent(subScene);
                double scaleFactor = Math.min(250 / subScene.getWidth(), 238 / subScene.getHeight());
                Scale scale = new Scale(scaleFactor, scaleFactor);
                subScene.getTransforms().clear();
                subScene.getTransforms().add(scale);
                hoverStyle(newBoard);
            });
            newBoard.setOnMouseExited(event -> {
                scrollPane.setContent(null);
                normalStyle(newBoard);

            });
            hbox.getChildren().add(newBoard);
        }

    }

    public void hoverStyle(Button button) {
        button.setStyle("-fx-border-width: 5px;" +
                "-fx-background-color: white;" +
                "-fx-border-color: #656565;" +
                "-fx-text-fill: #4a4ad5;" +
                "-fx-font-family: 'Adobe Thai';" +
                "-fx-font-size: 14 px;" +
                "-fx-rotate: 350;" +
                "-fx-font-weight: bolder");
    }

    public void normalStyle(Button button) {
        button.setStyle("-fx-border-width: 3px;" +
                "-fx-background-color: white;" +
                "-fx-border-color: gray;" +
                "-fx-text-fill: #4a4ad5;" +
                "-fx-font-family: 'Adobe Thai';" +
                "-fx-font-size: 14 px;");
    }

    /**
     * Sets the file name.
     *
     * @param fileName - the file name
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * A method that writes a new board to the user's stored boards file.
     * @param hasAccess - checks if the user has editing access to the board
     * @param board - the board to be written
     */
    private void writeNewBoardToFile(Board board, boolean hasAccess) {
        try (Scanner scanner = new Scanner(new File(fileName))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] lines = line.split(" ");
                long id = Long.parseLong(lines[0]);
                if (id == board.getBoardId()) {
                    return;
                }
            }
        } catch (Exception e) {
            System.out.println("No such file");
        }
        try (FileWriter writer = new FileWriter(fileName, true)) {
            writer.write(board.getBoardId() + " " + hasAccess+" " +  board.getTitle()  +"\n");
        } catch (IOException e) {
            System.out.println("Error writing to file");
        }

    }
}
