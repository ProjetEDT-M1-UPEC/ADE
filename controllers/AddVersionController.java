package controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.UUID;

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
		byte[] array = new byte[7];
		new Random().nextBytes(array);
		String generatedString = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
		// String generatedString = new String(array, Charset.forName("UTF-8"));
		// int hash = generatedString.hashCode();

		String value = nameVersionField.getText() + "[" + generatedString + "]";
		ArrayList<Creneaux> crList;
		if (Version.rootIsEmpty())
			crList = sc.getCreneauxList();
		else
			crList = sc.getCreneauxModified();
		// int i = 0;
		// while (i <5) {
		String id = Version.addNewVersion(value, crList, sc.getSelectedTabVersionId());
		if (id != null)
			sc.setSelectedTabVerID(value, id);

		// i++;
		// }
		this.close();
	}

}
