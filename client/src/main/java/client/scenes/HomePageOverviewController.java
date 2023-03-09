package client.scenes;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HomePageOverviewController {
    private Stage primaryStage;
    private Scene overview;


    /**
     * Function that goes from the homepage back to the board.
     *
     * @param actionEvent the action event on the button
     * @throws IOException the exception which might be caused
     */
    public void switchToBoard(javafx.event.ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("BoardOverview.fxml"));
        primaryStage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        overview = new Scene(root);
        primaryStage.setScene(overview);
        primaryStage.show();
    }
}