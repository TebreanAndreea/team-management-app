package client.scenes;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;

public class MainController {

    private Stage primaryStage;
    private BoardOverviewController overviewController;
    private Scene overview;

    /**
     * This method initializes the primary stage and displays the stage
     * @param primaryStage
     * @param overview
     */
    public void initialize(Stage primaryStage, Pair<BoardOverviewController, Parent> overview){
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