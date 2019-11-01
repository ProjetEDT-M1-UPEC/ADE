package controleur;

import java.io.File;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;

import com.jfoenix.controls.JFXButton;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import modeles.Constants;
import modeles.JsonFileManager;
import modeles.Version;

public class ViewVersionController implements Initializable {

	@FXML
	private JFXButton btnView, btnCharge, btnCancel;

	private TreeView<String> treeView = new TreeView<>();
	private String selectedVersion = null;

	public ViewVersionController() {
		treeView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				String str = e.getTarget().toString();
				Pattern pattern = Pattern.compile("@ (.*?)\"", Pattern.DOTALL);
				Matcher matcher = pattern.matcher(str);
				if (matcher.find()) {
					selectedVersion = matcher.group(1);
					System.out.println(selectedVersion);
				} else {
					selectedVersion = null;
				}

			}
		});
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
	}

	private TreeItem<String> getTree() {
		Version ver = AddVersionController.rootVersion;
		if (ver == null)
			return new TreeItem<String>("Aucune version enregistrée");
		return ver.toTreeItemString();
	}

	@FXML
	private void view(ActionEvent e) {
		treeView.setRoot(getTree());
		JFXButton btnSelect = new JFXButton("Choisir cette version");
		JFXButton btnDuplicate = new JFXButton("Dupliquer cette version");
		JFXButton btnSave = new JFXButton("Sauvegarder cette version");
		Stage primaryStage = new Stage();
		BorderPane b = new BorderPane();
		btnSelect.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				if (selectedVersion != null) {
					try {
						DateFormat formatter = new SimpleDateFormat(Constants.DATE_FORMAT);
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
		btnDuplicate.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				if (selectedVersion != null) {
					try {
						Timestamp timestamp = new Timestamp(System.currentTimeMillis());
						Long time = timestamp.getTime();
						Version v=AddVersionController.dupliVersion(getTimeStamp(selectedVersion));
                        Version vDupli=new Version(v.getParent(),time,v.getName());
                        v.getParent().addAltVer(vDupli);

					} catch (Exception excep) {
						System.out.println("Exception btnImport :" + excep);
					}
					primaryStage.close();
					cancel(null);
				}
			}
		});
		btnSave.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				if (selectedVersion != null) {
					try {
						Timestamp timestamp = new Timestamp(System.currentTimeMillis());
						Long time = timestamp.getTime();
						Version v=AddVersionController.dupliVersion(getTimeStamp(selectedVersion));

						JFileChooser fileChooser = new JFileChooser(new File(Constants.REP_OPEN_FILECHOSER));

						fileChooser.setDialogTitle(Constants.SAVE_FILE);
						fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

						if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
							JsonFileManager.getInstance().saveVersion(v,
									(fileChooser.getSelectedFile().getAbsolutePath() + "/"));
						}


					} catch (Exception excep) {
						System.out.println("Exception btnImport :" + excep);
					}
					primaryStage.close();
					cancel(null);
				}
			}
		});
b.setBottom(btnSave);
		b.setTop(btnSelect);
		b.setRight(btnDuplicate);
		b.setCenter(treeView);
		primaryStage.setScene(new Scene(b, 600, 400));
		primaryStage.setTitle("Folder View");
		primaryStage.show();

	}



	private Long getTimeStamp(String selectedVersion) throws ParseException {
		DateFormat formatter = new SimpleDateFormat(Constants.DATE_FORMAT);
		Date date = formatter.parse(selectedVersion);
		Timestamp timeStampDate = new Timestamp(date.getTime());
		return timeStampDate.getTime();
	}

	public TreeItem<String> getNodesForDirectory(File directory) {
		TreeItem<String> root = new TreeItem<String>(directory.getName());
		for (File f : directory.listFiles()) {
			System.out.println("Loading " + f.getName());
			if (f.isDirectory()) { // Then we call the function recursively
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
		Stage primaryStage = new Stage();
		BorderPane b = new BorderPane();

		DirectoryChooser dc = new DirectoryChooser();
		dc.setInitialDirectory(new File(System.getProperty("user.home")));
		File choice = dc.showDialog(primaryStage);
		if (choice == null || !choice.isDirectory()) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setHeaderText("Could not open directory");
			alert.setContentText("The file is invalid.");
			alert.showAndWait();
		} else {
			a.setRoot(getNodesForDirectory(choice));
			b.setCenter(a);
			primaryStage.setScene(new Scene(b, 600, 400));
			primaryStage.setTitle("Folder View");
			primaryStage.show();
		}
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
