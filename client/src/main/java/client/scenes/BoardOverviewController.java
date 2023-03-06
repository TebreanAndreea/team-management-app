package client.scenes;

import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;

public class BoardOverviewController {
    public HBox hBox;
    public TitledPane TODO;

    //TODO make button inside titled pane top-center aligned
    public void addList() {
        Button tempButton = new Button("+");
        TitledPane titledPane = new TitledPane("new list", tempButton);
        titledPane.setPrefHeight(TODO.getPrefHeight());
        titledPane.setMinWidth(TODO.getMinWidth());
        titledPane.setAnimated(false);
        titledPane.setContentDisplay(ContentDisplay.TOP);
        titledPane.getContent().setStyle("-fx-alignment: top-center;");
        hBox.getChildren().add(titledPane);
    }
}
