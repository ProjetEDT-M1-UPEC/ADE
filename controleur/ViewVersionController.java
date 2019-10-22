package controleur;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
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
	
	@FXML
	private void charge(ActionEvent e) {
		
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
