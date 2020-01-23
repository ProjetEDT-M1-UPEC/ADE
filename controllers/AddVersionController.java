package controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import code.barbot.Creneaux;
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
		ArrayList<Creneaux> kikou;
		if(Version.rootIsEmpty())
			kikou = sc.getCreneauxList() ;
		else 
			kikou = sc.getCreneauxModified();
		String id = Version.addNewVersion(value, kikou, sc.getSelectedTabVersionId());
		if(id != null)
			sc.setSelectedTabVerID(value, id);
		this.close();
	}

}
