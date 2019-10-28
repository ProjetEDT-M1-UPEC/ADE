package controleur;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class ViewVersionController implements Initializable{

	@FXML
	private Label jsp;

	@FXML
	private JFXButton btnCharge,btnCancel,btnDelete;

	@FXML
	private JFXListView<String> branchList;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
	}

	 public TreeItem<String> getNodesForDirectory(File directory) { //Returns a TreeItem representation of the specified directory
	        TreeItem<String> root = new TreeItem<String>(directory.getName());
	        for(File f : directory.listFiles()) {
	         System.out.println("Loading " + f.getName());
	         if(f.isDirectory()) { //Then we call the function recursively
	          root.getChildren().add(getNodesForDirectory(f));
	         } else {
	          root.getChildren().add(new TreeItem<String>(f.getName()));
	         }
	        }
	        return root;
	       }

		@FXML
		private void charge(ActionEvent e) {
			TreeView<String> a = new TreeView<String>();
			Stage primaryStage= (Stage) btnCharge.getScene().getWindow();
			 BorderPane b = new BorderPane();
			btnCharge.setOnAction(new EventHandler<ActionEvent>() {
			      @Override public void handle(ActionEvent e) {
			       DirectoryChooser dc = new DirectoryChooser();
			       dc.setInitialDirectory(new File(System.getProperty("user.home")));
			       File choice = dc.showDialog(primaryStage);
			       if(choice == null || ! choice.isDirectory()) {
			        Alert alert = new Alert(AlertType.ERROR);
			        alert.setHeaderText("Could not open directory");
			        alert.setContentText("The file is invalid.");

			        alert.showAndWait();
			       } else {
			        a.setRoot(getNodesForDirectory(choice));
			       }
			      }
			     });

		     b.setTop(btnCharge);
		     b.setCenter(a);
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
