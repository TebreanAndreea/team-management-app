package client.scenes;

//import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
//import java.awt.event.ActionEvent;
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

    //TODO make button inside titled pane top-center aligned
    public void addList(String title) {
        Button tempButton = new Button("+");
        TitledPane titledPane = new TitledPane(title, tempButton);
        titledPane.setPrefHeight(TODO.getPrefHeight());
        titledPane.setMinWidth(TODO.getMinWidth());
        titledPane.setAnimated(false);
        titledPane.setContentDisplay(ContentDisplay.TOP);
        titledPane.getContent().setStyle("-fx-alignment: top-center;");
        hBox.getChildren().add(titledPane);
    }
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
            vbox.getChildren().removeAll(textArea, createButton);
            addList(titleText);
        });

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