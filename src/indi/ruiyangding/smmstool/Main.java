package indi.ruiyangding.smmstool;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;


import java.io.File;

public class Main extends Application {

    private Stage primaryStage;
    private Parent root;
    private Scene scene;
    @Override
    public void start(Stage primaryStage) throws Exception{
        this.primaryStage = primaryStage;
        this.root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        this.primaryStage.setTitle("Hello World");
        this.primaryStage.setScene(this.scene =  new Scene(root, 482, 330));
        this.primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
