package controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Version;

public class AddVersionController implements Initializable {
	

	@FXML
	TextField nameVersionField;
	
	private MainScreenControleur sc;

	public AddVersionController(MainScreenControleur sc) {
		this.sc = sc;
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
		String value = nameVersionField.getText();
		String id = Version.addNewVersion(value, sc.getCreneauxList(), sc.getSelectedTabVersionId());
		if(id != null)
			sc.setSelectedTabVerID(value, id);
		this.close();
	}

}
