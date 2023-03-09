package client.scenes;

//import javafx.fxml.FXMLLoader;
//import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;

//import java.awt.event.ActionEvent;
//import java.io.IOException;

public class MainController {

    private Stage primaryStage;
    private HomePageOverviewController overviewController;
    private Scene overview;

    /**
     * This method initializes the primary stage and displays the stage.
     * @param primaryStage
     * @param overview
     */
    public void initialize(Stage primaryStage, Pair<HomePageOverviewController, Parent> overview){
        this.primaryStage = primaryStage;
        this.overviewController = overview.getKey();
        this.overview = new Scene(overview.getValue());

        showOverview();
        primaryStage.show();
    }

    public void showOverview(){
        primaryStage.setTitle("Board: Overview");
        primaryStage.setScene(overview);

        // to be implemented: refresh
        //overviewController.refresh();
    }

}