package application;

import java.io.IOException;

import application.server.Server;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
	
	
	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/application/ui/Main.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add("application.css");
			primaryStage.getIcons().add(new Image("/images/icons8_Return_Book_48px.png"));

			primaryStage.setScene(scene);
			primaryStage.setTitle("Vratkovač");
			primaryStage.setResizable(false);
			primaryStage.setOnCloseRequest(e -> {
				try {
					Server.closeAll();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			});

			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
