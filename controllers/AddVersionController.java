package controllers;

import java.net.URL;
<<<<<<< HEAD
import java.util.ArrayList;
=======
import java.nio.charset.Charset;
import java.util.Random;
>>>>>>> 149f45ebf5605ea1e1b173c81fe69858e312eed0
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
		byte[] array = new byte[7];
		new Random().nextBytes(array);
		String generatedString = new String(array, Charset.forName("UTF-8"));
		int hash = generatedString.hashCode();
		
		String value = nameVersionField.getText() + hash;
		ArrayList<Creneaux> kikou;
		if(Version.rootIsEmpty())
			kikou = sc.getCreneauxList() ;
		else 
			kikou = sc.getCreneauxModified();
		// int i = 0;
		// while (i <5) {
		String id = Version.addNewVersion(value, kikou, sc.getSelectedTabVersionId());
		if(id != null)
			sc.setSelectedTabVerID(value, id);

		// i++;
		// }
		this.close();
	}

}
