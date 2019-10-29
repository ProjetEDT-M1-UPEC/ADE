package controleur;

import java.net.URL;
import java.sql.Timestamp;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import modeles.Version;

public class AddVersionController implements Initializable{
	private Timestamp timestamp = new Timestamp(System.currentTimeMillis());
	
	private static Version currentVersion = null;
	public static Version rootVersion = null;
	
	
	
	@FXML
	TextField nameVersionField;
	
	
	public AddVersionController() {
		//VIDE CTRL V
	}
	

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//TODO ???
	}
	
	public void close() {
		Stage stage = (Stage) nameVersionField.getScene().getWindow();
		stage.close();
	}
	
	public void add() {
		Long key = timestamp.getTime();
		String value = nameVersionField.getText();
		if(rootVersion == null) {
			currentVersion = new Version(key, value);
			rootVersion = currentVersion;
		}
		else {
			currentVersion = currentVersion.addAltVer(key, value);
		}
		String v = value+"@"+key;
		//System.out.println(rootVersion);
		System.out.println(v);
		this.close();
	}
	
	public static void changeVersion(Long t) {
		System.out.println(t);
		Version wantedVersion = rootVersion.getVersion(t);
		if(wantedVersion!=null)
			currentVersion = wantedVersion;
	}
}
