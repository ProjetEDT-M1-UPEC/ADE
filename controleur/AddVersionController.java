package controleur;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import modeles.Version;

public class AddVersionController implements Initializable {
	

	@FXML
	TextField nameVersionField;

	public AddVersionController() {
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	@FXML
	private void close() {
		Stage stage = (Stage) nameVersionField.getScene().getWindow();
		stage.close();
	}

	@FXML
	private void add() {
		Version.addNewVersion(nameVersionField.getText());
		this.close();
	}

}
