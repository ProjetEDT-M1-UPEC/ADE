package application;

import java.io.File;
import java.io.IOException;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import models.Constants;
import models.StateManager;

public class Main extends Application {

	public final static String PROPERTY_FILE = "src/remember.properties";
	/**
	 * the main stage of app
	 */
	public static Stage mainStage;

	public static boolean checkPropertiesFile() {
		File f = new File(PROPERTY_FILE);
		if (f.exists()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void start(Stage primaryStage) {
		Screen screen = Screen.getPrimary();
		Rectangle2D bounds = screen.getVisualBounds();
		Parent root = null;
		double h = 0;
		double w = 0;
		mainStage = primaryStage;
		
		primaryStage.getIcons().add(new Image(Constants.PICS_LOGO));
		try {

			root = FXMLLoader.load(getClass().getResource(Constants.MAIN_SCREEN));
			w = bounds.getWidth();
			h = bounds.getHeight();
			
			primaryStage.setScene(new Scene(root, w, h));

			primaryStage.setTitle("AGENDA FX V2");
			primaryStage.setMaximized(true);
			primaryStage.show();

			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

				@Override
				public void handle(WindowEvent event) {
					// Save App state
					StateManager.getInstance().saveState();
					primaryStage.close();
				}
			});

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		launch(args);
	}
}
