package application;

import java.io.File;
import java.io.IOException;

import controleur.AddVersionController;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import modeles.Constants;
import modeles.StateManager;

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

		try {

			root = FXMLLoader.load(getClass().getResource(Constants.MAIN_SCREEN));
			w = bounds.getWidth();
			h = bounds.getHeight();
			Scene scene = new Scene(root, w, h);
			primaryStage.setScene(scene);

			scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
				final KeyCombination keyComb = new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN);

				public void handle(KeyEvent ke) {
					if (keyComb.match(ke)) {
						openAddVersionForm();
						ke.consume();
					}
				}
			});

			primaryStage.setTitle("ADE FX TEST");
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

	private void openAddVersionForm() {

		final Stage popUp = new Stage();

		popUp.setTitle("Ajouter une version de l'agenda");
		popUp.initModality(Modality.APPLICATION_MODAL);

		AnchorPane root;

		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(Constants.ADDVERSION_POPUP));
			AddVersionController controller = new AddVersionController();
			fxmlLoader.setController(controller);
			root = fxmlLoader.load();
			Scene scene = new Scene(root);
			popUp.setScene(scene);
			popUp.initOwner(Main.mainStage);
			popUp.show();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

	public static void main(String[] args) {
		
		launch(args);
	}
}
