package client.scenes;

import client.utils.ServerUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import javax.inject.Inject;
import java.io.IOException;

public class HomePageOverviewController {
    private Stage primaryStage;
    private Scene overview;

    private ServerUtils server;

    /**
     * Constructor which initialize the server.
     * @param server the server instance used for communication
     */
    @Inject
    public HomePageOverviewController (ServerUtils server){
        this.server = server;
    }


    /**
     * Function that goes from the homepage back to the board.
     *
     * @param actionEvent the action event on the button
     * @throws IOException the exception which might be caused
     */
    public void switchToBoard(javafx.event.ActionEvent actionEvent) throws IOException {

        // before switching to the board scene, we need to validate the URL
        AnchorPane anchorPane = (AnchorPane) ((Button)actionEvent.getSource()).getParent();
        TextArea textArea = (TextArea) anchorPane.getChildren().get(1);
        String userUrl = textArea.getText().trim();

        if (checkConnection(userUrl)) {

            Parent root = FXMLLoader.load(getClass().getResource("BoardOverview.fxml"));
            primaryStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            overview = new Scene(root);
            primaryStage.setScene(overview);
            primaryStage.show();
        } else {
            // put a message in the text area
            textArea.setText("Invalid url");
        }
    }

    /**
     * This method checks if the url entered by the user is a valid one.
     * @param userUrl the string representing the ur;
     * @return true if the url is valid, or false otherwise
     */
    public boolean checkConnection(String userUrl){
        try {
            server.checkServer(userUrl);
            return true;
        } catch(Exception e){
            return false;
        }
    }
}
