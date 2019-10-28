package controleur;

import java.net.URL;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jfoenix.controls.JFXButton;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import modeles.Version;

public class ViewVersionController implements Initializable {

	@FXML
	private JFXButton btnCharge, btnCancel, btnDelete;

	private TreeView<String> treeView = new TreeView<>(); 
	private String selectedVersion = null;
	
	public ViewVersionController() {
		treeView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>(){
			@Override
            public void handle(MouseEvent e) {
				String str = e.getTarget().toString();
				Pattern pattern = Pattern.compile("@ (.*?)\"", Pattern.DOTALL);
				Matcher matcher = pattern.matcher(str);
				if (matcher.find()) {
				    selectedVersion = matcher.group(1);
				    System.out.println(selectedVersion);
				}
				else {
					selectedVersion = null;
				}
				
				
            }
        });
	}
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
	}
	
	private TreeItem<String> getTree(){
		Version ver = AddVersionController.rootVersion;
		if(ver == null) return new TreeItem<String>("Aucune version enregistrée");
		return ver.toTreeItemString();
	}
	
	@FXML
	private void charge(ActionEvent e) {
		treeView.setRoot(getTree());
		JFXButton btnImport = new JFXButton ("Choisir cette version");
		Stage primaryStage = new Stage();
		BorderPane b = new BorderPane();
		btnImport.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				if(selectedVersion!=null) {
					try {
					      DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy à HH:mm:ss.SSS");
					      Date date = formatter.parse(selectedVersion);
					      Timestamp timeStampDate = new Timestamp(date.getTime());
					      AddVersionController.changeVersion(timeStampDate.getTime());
					    } catch (Exception excep) {
					      System.out.println("Exception btnImport :" + excep);
					    }
					primaryStage.close();
					cancel(null);
				}
			}
		});

		b.setTop(btnImport);
		b.setCenter(treeView);
		primaryStage.setScene(new Scene(b, 600, 400));
		primaryStage.setTitle("Folder View");
		primaryStage.show();

	}

	@FXML
	private void compare(ActionEvent e) {

	}

	@FXML
	private void cancel(ActionEvent e) {
		Stage stage = (Stage) btnCancel.getScene().getWindow();
		stage.close();
	}

	@FXML
	private void selectVersion(ActionEvent e) {

	}

}
