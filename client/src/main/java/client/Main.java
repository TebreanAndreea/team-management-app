/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client;

import static com.google.inject.Guice.createInjector;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;

import client.scenes.*;
import com.google.inject.Injector;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);

    public static void main(String[] args) throws URISyntaxException, IOException {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        var overview = FXML.load(HomePageOverviewController.class, "client", "scenes", "HomePageOverview.fxml");
        var boardOverview = FXML.load(BoardOverviewController.class, "client", "scenes", "BoardOverview.fxml");
        var cardOverview = FXML.load(CardOverviewController.class, "client", "scenes", "CardOverview.fxml");
        var initialOverview = FXML.load(InitialOverviewController.class, "client", "scenes", "InitialOverview.fxml");
        var adminOverview = FXML.load(AdminOverviewController.class, "client", "scenes", "AdminOverview.fxml");
        //var add = FXML.load(AddQuoteCtrl.class, "client", "scenes", "AddQuote.fxml");

        var mainController = INJECTOR.getInstance(MainController.class);
        mainController.initialize(primaryStage, overview, boardOverview, cardOverview, initialOverview);
    }
    /**
     * This method generates the required files and returns the admin password.
     * <p>NOTE: the pathing of the files changes depending of where the projects is run from.</p>
     * @return the admin password
     * @throws IOException if the file is not found
     */
    private void fileGeneration() throws IOException {
        File test = new File("build.gradle");
        String pathname = "";
        if (!test.getAbsolutePath().contains("client"))
            pathname += "client/";
        pathname += "user_files/temp.txt";
        File file = new File(pathname);
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
    }
}