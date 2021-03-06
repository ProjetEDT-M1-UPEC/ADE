package controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.EmptyStackException;
import java.util.HashSet;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.jfoenix.controls.JFXSpinner;

import application.Main;
import code.barbot.Creneaux;
import code.barbot.Parseur;
import code.barbot.Creneaux.TYPE;
import impl.org.controlsfx.autocompletion.AutoCompletionTextFieldBinding;
import impl.org.controlsfx.autocompletion.SuggestionProvider;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import jfxtras.scene.control.agenda.Agenda;
import view.AgendaCustom;
import models.Branch;
import models.Category;
import models.Constants;
import backup.JsonFileManager;
import models.Project;
import models.Shortcut;
import models.State;
import backup.StateManager;
import view.Tab;
import models.TimeTable;
import models.TimeTableV2;
import models.Version;
import models.Version2;

/**
 * Il s'agit du contr�leur principal de l'application
 * 
 * @author Pionan
 *
 */
public class MainScreenControleur implements Initializable {

	private Screen screen = Screen.getPrimary();
	private Rectangle2D bounds = screen.getVisualBounds();

	public static final String Shortcut_FILE = "Shortcuts.txt";

	public static TabPane tabPaneV2;
	public static ArrayList<String> importedPathI;

	private Parseur parseur;
	private static MainScreenControleur me;

	@FXML
	private TabPane tabPane;

	@FXML
	private JFXSpinner spinner;

	@FXML
	private ComboBox<String> projetField;

	@FXML
	private ComboBox<String> categorieField;

	@FXML
	private Menu fav, myfavs, diff_fav;

	@FXML
	private DatePicker datePicker;

	@FXML
	private Button undoButton;

	@FXML
	private Button redoButton;

	@FXML
	private Button diffButton;

	@FXML
	private Button openButton;

	@FXML
	private Button saveButton;

	@FXML
	private TextField versionField;

	@FXML
	private MenuItem Create_Pro;

	@FXML
	private MenuItem Open_Pro;

	@FXML
	private MenuItem Save_Pro;

	@FXML
	private MenuItem Read_Pro;

	@FXML
	private MenuItem Add_Ver;

	@FXML
	private MenuItem Load_Bra;

	private Set<String> nomsVersions = new HashSet<>();
	SuggestionProvider<String> provider;
	public static Button undoButtonS, redoButtonS;
	private static int color = 0;
	private TreeView<String> treeView = new TreeView<>();
	private String selectedVersion = null;

	public MainScreenControleur() {
		treeView.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
			if (treeView.getSelectionModel().getSelectedItem() == null)
				return;
			selectedVersion = treeView.getSelectionModel().getSelectedItem().getValue();
			selectedVersion = selectedVersion.equals(Constants.EMPTY_TREE) ? null : selectedVersion;
		});
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		me = this;

		tabPaneV2 = tabPane;
		redoButtonS = redoButton;
		undoButtonS = undoButton;

		setFavDiffItems();
		setFavMenuItems();
		initState();

		setAddTabeHandler();
		initButtons();

		initMenuItem();

		listerVersions();
	}

	private void initMenuItem() {
		Create_Pro.setAccelerator(KeyCombination.keyCombination("F1"));

		Open_Pro.setAccelerator(KeyCombination.keyCombination("F2"));

		Save_Pro.setAccelerator(KeyCombination.keyCombination("F3"));

		Read_Pro.setAccelerator(KeyCombination.keyCombination("F4"));

		Add_Ver.setAccelerator(KeyCombination.keyCombination("F5"));

		Load_Bra.setAccelerator(KeyCombination.keyCombination("F6"));
	}

	// permet la suggestion des versions lors de la saisie dans recherche
	// version
	private void listerVersions() {
		provider = SuggestionProvider.create(nomsVersions);
		new AutoCompletionTextFieldBinding<>(versionField, provider);

		versionField.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				nomsVersions = Version.getSetNames();

				provider.clearSuggestions();
				provider.addPossibleSuggestions(nomsVersions);
			}
		});
	}

	@FXML
	private void searchButton(ActionEvent ae) {
		Version wanted = Version.getVersion(versionField.getText());
		if (wanted != null)
			setNewTabForVersionning(wanted.getCreneauxList(), wanted.getName(), wanted.getNameTimestamp());
	}

	/*
	 * Initialise les styles des boutons pr�sents sur l'�cran principal
	 */
	private void initButtons() {
		setHandleBtn(openButton, Constants.PICS_OPEN_hover, Constants.PICS_OPEN);
		setHandleBtn(saveButton, Constants.PICS_SAVE_hover, Constants.PICS_SAVE);

		setImageBtn(undoButton, Constants.PICS_UNDO);
		setImageBtn(redoButton, Constants.PICS_REDO);
	}

	private void setImageBtn(Button btn, String img) {
		ImageView pic = new ImageView(img);
		;
		pic.setFitHeight(Constants.SIZE_BTN);
		pic.setFitWidth(Constants.SIZE_BTN);
		btn.setGraphic(pic);
	}

	private void setHandleBtn(Button btn, String hov, String img) {
		ImageView pic = new ImageView(img);
		btn.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> btn.setGraphic(new ImageView(hov)));
		btn.addEventHandler(MouseEvent.MOUSE_EXITED, e -> btn.setGraphic(pic));
		btn.setGraphic(pic);
	}

	private void diff(TimeTable compared) {

		TimeTable comparing = getSelectedTab().getAgenda().getTimeTable();

		if (!comparing.equals(compared)) {

			System.out.println("Comparaison");

			compared.getCreneauxsList().forEach(creneau -> {
				if (!comparing.getCreneauxsList().contains(creneau)) {
					creneau.setAppointmentGroup(new Agenda.AppointmentGroupImpl().withStyleClass("group2"));
				}
			});

			AgendaCustom agenda = new AgendaCustom(compared);
			AgendaCustom agendadebase = getSelectedTab().getAgenda();

			if (agenda.getTimeTable().getCreneauxsList().size() > 0)
				agenda.setDisplayedLocalDateTime(
						agenda.getTimeTable().getCreneauxsList().get(0).getStartLocalDateTime());
			else {
				agenda.setDisplayedLocalDateTime(LocalDateTime.now());
			}

			agenda.disableAction();

			HBox hb = new HBox();
			VBox vb = new VBox();
			Button close = new Button();
			ImageView quitter = new ImageView(("pics/cancel.png"));
			quitter.setFitHeight(15);
			quitter.setFitWidth(15);
			close.setGraphic(quitter);

			hb.getChildren().addAll(close);
			hb.setAlignment(Pos.BASELINE_RIGHT);
			vb.getChildren().addAll(hb, agenda);
			HBox diffbox = new HBox();
			diffbox.getChildren().addAll(agendadebase, vb);
			getSelectedTab().setContent(diffbox);

			close.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					vb.getChildren().remove(agenda);
					hb.getChildren().remove(close);
				}

			});

		}

	}

	@FXML
	private void executeDiffFromFavoris(ActionEvent e) {
		System.out.println("Enter diff from favs");
		diff_fav.getItems().forEach(i -> {

			if (i.equals(e.getSource())) {

				MenuItem ie = (MenuItem) e.getSource();

				Shortcut.getShortucts().forEach(s -> {
					if (s.getShortcutName().equals(ie.getText())) {
						select(s.getPath());

						try {
							diff(getDataFromShortcut(s.getPath()));
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				});
			}
		});
	}

	@FXML
	public void executeDiffFromEmpty() {
		diff(new TimeTable(Constants.NEW_TAB + tabPane.getTabs().size(), "NoPath", new ArrayList<Creneaux>(),
				TimeTable.TYPE.EMPTY_BASED));
	}

	@FXML
	private void executeDiffFromFile() {

		System.out.println("Starting difference : ");

		JFileChooser fileChooser = new JFileChooser(new File("."));
		FileNameExtensionFilter filter = new FileNameExtensionFilter(Constants.FORMAT_JSON, "json");
		fileChooser.setFileFilter(filter);

		if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {

			File file = fileChooser.getSelectedFile();

			TimeTable compared = JsonFileManager.getInstance().load(file);
			diff(compared);
		}
	}

	@FXML
	public void executeDiffFromADE(ActionEvent ee) {

		final Stage popUp = new Stage();
		popUp.setTitle(Constants.IMPORT_ADE_PopUp);
		popUp.initModality(Modality.APPLICATION_MODAL);

		BorderPane root;

		try {

			root = (BorderPane) FXMLLoader.load(getClass().getResource(Constants.IMPORT_ADE_FXML));
			Scene scene = new Scene(root);
			popUp.setScene(scene);
			popUp.initOwner(Main.mainStage);
			popUp.setOnHidden(e -> {
				select(importedPathI);

				try {
					TimeTable newTimeTable = new TimeTable(importedPathI.get(importedPathI.size() - 1),
							importedPathI.toString(), parseur.getTimeTable(), TimeTable.TYPE.ADE_BASED);
					diff(newTimeTable);
				} catch (Exception e1) {
					e1.printStackTrace();
				}

			});
			popUp.show();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private TimeTable getDataFromShortcut(ArrayList<String> path) throws Exception, IOException {

		TimeTable newTimeTable = new TimeTable(path.get(path.size() - 1), path.toString(), parseur.getTimeTable(),
				TimeTable.TYPE.ADE_BASED);
		return newTimeTable;

	}

	private void initState() {
		// getState
		State state = StateManager.getInstance().load();

		if (state != null) {
			Version2 v2 = state.getRoot();
			if (v2 != null)
				Version.loadRoot(Version2.toVersion(null, v2));
			if (!state.getList().isEmpty()) {

				ArrayList<TimeTable> list = new ArrayList<TimeTable>();

				for (TimeTableV2 timeTableV2 : state.getList()) {
					list.add(timeTableV2.toTimeTable());
				}

				AgendaCustom agenda;
				for (TimeTable timeTable : list) {

					agenda = new AgendaCustom(timeTable);

					// agenda.appointments().addAll(timeTable.getCreneauxsList());
					agenda.setParent(new Tab(agenda, this));
					tabPane.getTabs().add(agenda.getparent());

				}

				tabPane.getSelectionModel().select(state.getTabNumber());
				updateUndoRedoStatic((Tab) tabPaneV2.getSelectionModel().getSelectedItem());
			}
		}
	}

	@FXML
	public void restoreAgenda(ActionEvent e) {
		getSelectedTab().getAgenda().refresh();
	}

	@FXML
	public void show_Filter_PopUp(ActionEvent ee) {
		final Stage popUp = new Stage();
		popUp.setTitle("Filtrer les donn�es");
		popUp.initModality(Modality.APPLICATION_MODAL);
		AnchorPane root;
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(Constants.FILTER_POPUP));
			FilterControleur controller = new FilterControleur(getSelectedTab());
			fxmlLoader.setController(controller);
			root = fxmlLoader.load();
			Scene scene = new Scene(root);
			popUp.setScene(scene);
			popUp.initOwner(Main.mainStage);

			popUp.show();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	@FXML
	private void next() {

		try {
			getSelectedTab().next();
			updateUndoRedoButtons();
		} catch (EmptyStackException e) {
			System.out.println("EmptyStackException");
		}

	}

	@FXML
	private void back() {

		try {
			getSelectedTab().back();
			updateUndoRedoButtons();
		} catch (EmptyStackException e) {
			System.out.println("EmptyStackException");
		}

	}

	public void updateUndoRedoButtons() {

		if (!getSelectedTab().hasNext())
			redoButton.setDisable(true);
		else
			redoButton.setDisable(false);

		if (!getSelectedTab().hasBack())
			undoButton.setDisable(true);
		else
			undoButton.setDisable(false);
	}

	@FXML
	private void initDatePicker() {
		// Calendar c = Calendar.getInstance();
		// c.set(datePicker.getValue().getYear(),
		// datePicker.getValue().getMonthValue() - 1,
		// datePicker.getValue().getDayOfMonth());
		//
		Agenda myagenda = getSelectedTab().getAgenda();
		myagenda.getDisplayedLocalDateTime();
		LocalDateTime time = LocalDateTime.of(datePicker.getValue().getYear(), datePicker.getValue().getMonthValue(),
				datePicker.getValue().getDayOfMonth(), 0, 0);
		myagenda.setDisplayedLocalDateTime(time);

		//
		// if (getSelectedTab().getAgenda().getTimeTable().getType() ==
		// (TimeTable.TYPE.ADE_BASED)) {
		//
		// try {
		// select(getSelectedTab().getAgenda().getTimeTable().getPathList());
		// parseur.setWeek(calculWeek(Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)),
		// false);
		// parseur.setWeek(calculWeek(c.get(Calendar.WEEK_OF_YEAR)), true);
		// ArrayList<Creneaux> newTimeTbale = parseur.getTimeTable();
		// ArrayList<Creneaux> oldTimeTbale =
		// getSelectedTab().getAgenda().getTimeTable().getCreneauxsList();
		// getSelectedTab().notifyChange(new
		// AgendaEvent(getSelectedTab().getAgenda(), AgendaEvent.TYPE.WEEK,
		// oldTimeTbale, newTimeTbale));
		// getSelectedTab().getAgenda().getTimeTable().setCreneauxsList(newTimeTbale);
		// getSelectedTab().getAgenda()
		// .setDisplayedLocalDateTime(LocalDateTime.of(datePicker.getValue(),
		// LocalTime.now()));
		// getSelectedTab().getAgenda().newTimeTable(getSelectedTab().getAgenda().getTimeTable());
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }

	}

	@FXML
	public void setButtonPrecedent() {
		AgendaCustom myagenda = getSelectedTab().getAgenda();
		LocalDateTime back = myagenda.getDisplayedLocalDateTime().minus(Period.ofWeeks(1));
		myagenda.setDisplayedLocalDateTime(back);
	}

	@FXML
	public void setButtonsuivant() {
		Agenda myagenda = getSelectedTab().getAgenda();
		LocalDateTime next = myagenda.getDisplayedLocalDateTime().plus(Period.ofWeeks(1));
		myagenda.setDisplayedLocalDateTime(next);
	}

	public int calculWeek(int week) {
		return (week + 18) % 52;
	}

	public static void setAddTabeHandler() {

		Tab newtab = new Tab("", me);
		newtab.setDisable(true);

		// ImageView addImage = new ImageView(Constants.PICS_ADD_TAB);

		// addImage.setFitWidth(20);
		// addImage.setFitHeight(20);
		// newtab.setGraphic(addImage);
		newtab.setClosable(false);
		// action event

		EventHandler<Event> event = new EventHandler<Event>() {

			public void handle(Event e) {
				if (newtab.isSelected()) {
					addNewTab(new AgendaCustom(new TimeTable(Constants.NEW_TAB + tabPaneV2.getTabs().size(), "Path",
							new ArrayList<Creneaux>(), TimeTable.TYPE.EMPTY_BASED)));
				}
			}
		};

		// set event handler to the tab
		newtab.setOnSelectionChanged(event);

		// add newtab
		tabPaneV2.getTabs().add(newtab);
		tabPaneV2.getSelectionModel().selectedItemProperty().addListener((ov, oldTab, nTab) -> {

			if (((Tab) nTab) != null)
				updateUndoRedoStatic((Tab) nTab);

		});
	}

	static void addNewTab(AgendaCustom agenda) {
		// create Tab
		Tab tab = new Tab(agenda, me);

		agenda.setParent(tab);

		// add tab
		tabPaneV2.getTabs().add(tabPaneV2.getTabs().size() - 1, tab);

		// select the last tab
		tabPaneV2.getSelectionModel().select(tabPaneV2.getTabs().size() - 2);

	}

	public static void updateUndoRedoStatic(Tab tab) {
		if (!tab.hasNext())
			redoButtonS.setDisable(true);
		else
			redoButtonS.setDisable(false);
		if (!tab.hasBack())
			undoButtonS.setDisable(true);
		else
			undoButtonS.setDisable(false);
	}

	public void taskList(ActionEvent event) throws IOException {
		Parent newParent = FXMLLoader.load(getClass().getResource(Constants.TACHE_LIST));
		Scene newScene = new Scene(newParent);
		Stage newStage = (Stage) (tabPane.getScene().getWindow());

		newStage.setScene(newScene);
		newStage.setWidth(bounds.getWidth());
		newStage.setHeight(bounds.getHeight());
		newStage.setMaximized(true);
		newStage.show();
	}

	public boolean Dialog_Import_ADE() {
		boolean k = false;

		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setContentText("Vous voulez charger cet emploi sur le m�me agenda");
		ButtonType oui = new ButtonType("Oui");
		ButtonType non = new ButtonType("Non");
		alert.getButtonTypes().setAll(oui, non);
		Optional<ButtonType> opt = alert.showAndWait();
		if (opt.get() == oui) {
			k = true;
		}

		return k;
	}

	@FXML
	public void Show_Import_ADE_PopUp(ActionEvent ee) {

		final Stage popUp = new Stage();

		popUp.setTitle(Constants.IMPORT_ADE_PopUp);
		popUp.initModality(Modality.APPLICATION_MODAL);

		BorderPane root;

		try {

			root = (BorderPane) FXMLLoader.load(getClass().getResource(Constants.IMPORT_ADE_FXML));

			Scene scene = new Scene(root);

			popUp.setScene(scene);
			popUp.initOwner(Main.mainStage);
			popUp.setOnHidden(e -> {
				select(importedPathI);

				try {
					if (!Dialog_Import_ADE()) {
						getDataShowData(importedPathI);
					} else {
						getDataShowDataInSameTab(importedPathI);
					}

				} catch (Exception e1) {
					e1.printStackTrace();
				}

			});

			popUp.show();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public void newTask(ActionEvent event) throws IOException {

		Parent newParent = FXMLLoader.load(getClass().getResource(Constants.NEW_TASKFXML));

		Scene newScene = new Scene(newParent);
		Stage newStage = (Stage) (tabPane.getScene().getWindow());

		newStage.setScene(newScene);
		newStage.show();
	}

	public void menuAction(ActionEvent e) {

		// Shortcut selectedShortcut;
		myfavs.getItems().forEach(i -> {

			if (i.equals(e.getSource())) {

				MenuItem ie = (MenuItem) e.getSource();

				Shortcut.getShortucts().forEach(s -> {
					if (s.getShortcutName().equals(ie.getText())) {
						select(s.getPath());

						try {
							getDataShowData(s.getPath());
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				});
			}
		});

	}

	private void setFavDiffItems() {
		ArrayList<MenuItem> favorites = new ArrayList<>();

		Shortcut.getShortucts().forEach(e -> {
			favorites.add(new MenuItem(e.getShortcutName()));
			favorites.add(new SeparatorMenuItem());
		});

		if (!favorites.isEmpty()) {
			favorites.remove(favorites.size() - 1);
		}

		for (MenuItem mi : favorites) {
			mi.setOnAction(a -> executeDiffFromFavoris(a));
		}

		diff_fav.getItems().addAll(favorites);
	}

	/**
	 * Add shortcuts to the menu ,
	 *
	 *
	 */
	private void setFavMenuItems() {
		ArrayList<MenuItem> favorites = new ArrayList<>();

		Shortcut.getShortucts().forEach(e -> {
			favorites.add(new MenuItem(e.getShortcutName()));
			favorites.add(new SeparatorMenuItem());
		});

		if (!favorites.isEmpty()) {
			favorites.remove(favorites.size() - 1);
		}

		for (MenuItem mi : favorites) {
			mi.setOnAction(a -> menuAction(a));
		}

		myfavs.getItems().addAll(favorites);
	}

	/**
	 * Afficher un planning vide et permettre les modifications
	 *
	 * @param e
	 */

	@FXML
	private void Show_EmptyAgenda_PopUp(ActionEvent e) {
		addNewTab(new AgendaCustom(new TimeTable(Constants.NEW_TAB + tabPane.getTabs().size(), "NoPath",
				new ArrayList<Creneaux>(), TimeTable.TYPE.EMPTY_BASED)));
	}

	/**
	 * get the selected time table and set the list of Creneaux
	 *
	 * @param selectedShortcut
	 */
	private void select(ArrayList<String> path) {

		parseur = null;
		Collection<String> set = PopUpAddController.getUFRS().values();
		ArrayList<String> list = new ArrayList<>();

		for (String b : set) {
			list.add(b);
		}

		if (path == null)
			return;

		for (int i = 1; i < path.size(); i++) {
			int index = 0;

			for (int j = 0; j < list.size(); j++) {
				if (list.get(j).equals(path.get(i))) {
					index = j;
				}
			}

			list = getSubBranchesList(index);
		}

	}

	private void getDataShowData(ArrayList<String> path) throws Exception, IOException {
		color = 0;
		TimeTable newTimeTable = new TimeTable(path.get(path.size() - 1), path.toString(), parseur.getTimeTable(),
				TimeTable.TYPE.ADE_BASED);
		AgendaCustom agenda = new AgendaCustom(newTimeTable);

		// adapter la date de l'agenda

		if (newTimeTable.getCreneauxsList().size() > 0)
			agenda.setDisplayedLocalDateTime(newTimeTable.getCreneauxsList().get(0).getStartLocalDateTime());
		else {
			agenda.setDisplayedLocalDateTime(LocalDateTime.now());
		}

		addNewTab(agenda);

	}

	private void getDataShowDataInSameTab(ArrayList<String> path) throws Exception, IOException {
		color++;
		TimeTable newTimeTable = new TimeTable(path.get(path.size() - 1), path.toString(), parseur.getTimeTable(),
				TimeTable.TYPE.ADE_BASED);
		Creneaux clone;

		for (Creneaux c : newTimeTable.getCreneauxsList()) {
			c.setParent(getSelectedTab().getAgenda().getparent());
			c.withAppointmentGroup(new Agenda.AppointmentGroupImpl().withStyleClass("group" + color));

			clone = (Creneaux) c.clone();
			clone.setStatus(TYPE.Created);
			getSelectedTab().getAgenda().getTimeTable().getCreneauxModified().add(clone);
			System.out.println("ajout� par ade");
		}
		getSelectedTab().getAgenda().getTimeTable().getCreneauxsList().addAll(newTimeTable.getCreneauxsList());
		getSelectedTab().getAgenda().refresh();

	}

	/**
	 * Supprimer tous les cr�neaux Ajouter la liste des cr�neaux � l'agenda
	 *
	 * @param timeTable
	 */
	private void setCalenderData(TimeTable timeTable) {

		if (timeTable.getCreneauxsList().size() > 0) {
			getSelectedTab().getAgenda()
					.setDisplayedLocalDateTime(timeTable.getCreneauxsList().get(0).getStartLocalDateTime());
		}

		AgendaCustom agenda = new AgendaCustom(timeTable);
		addNewTab(agenda);
	}

	ArrayList<String> getSubBranchesList(int branche) {

		if (parseur == null) {
			parseur = new Parseur(PopUpAddController.getUFRS().values().toArray()[branche].toString(), "");
			return Project.tableToString(parseur.getProjectList());
		}

		switch (parseur.ParseurLevel) {
		case PROJECT:
			parseur.selctProject(parseur.getProjectList().get(branche).getId() + "");
			return Category.tableToString(parseur.getProject().getCategorys());
		case CATEGORY:
			parseur.setCategory_getBranches(parseur.getProject().getCategorys().get(branche));
			return Branch.tableToString(parseur.getProject().getSelectedCategory().getBranches());

		case BRANCH:
			if (parseur.getProject().getSelectedCategory().getSelectedBranch() != null) {
				// leaf case
				if (parseur.getProject().getSelectedCategory().getSelectedBranch().isLeaf())
					return null;
				parseur.setBranch_getChild(
						parseur.getProject().getSelectedCategory().getSelectedBranch().getBranches().get(branche));
				return Branch
						.tableToString(parseur.getProject().getSelectedCategory().getSelectedBranch().getBranches());

			} else {
				parseur.setBranch_getChild(parseur.getProject().getSelectedCategory().getBranches().get(branche));
				return Branch
						.tableToString(parseur.getProject().getSelectedCategory().getSelectedBranch().getBranches());
			}

		default:
			return new ArrayList<>();
		}
	}

	public Stage showDialog() {

		Stage stage = new Stage();
		BorderPane g = new BorderPane();
		ProgressIndicator p1 = new ProgressIndicator();

		g.getChildren().add(p1);

		Scene scene = new Scene(g, 260, 80);
		stage.setScene(scene);
		stage.initOwner(Main.mainStage);

		return stage;
	}

	// file load save handler

	/**
	 * action graphique "menu" Lancer le selecteur de fichier pour choisir le
	 * fichier
	 *
	 * @param event Evenement
	 */
	@FXML
	public void open_file(ActionEvent event) {

		JFileChooser fileChooser = new JFileChooser(new File("."));
		FileNameExtensionFilter filter = new FileNameExtensionFilter(Constants.FORMAT_JSON, "json");
		fileChooser.setFileFilter(filter);

		if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();

			setCalenderData(JsonFileManager.getInstance().load(file));
			updateUndoRedoButtons();
		}
	}

	/**
	 * action graphique "menu" Lancer le s�lecteur de fichier pour choisir le chemin
	 * o� nous allons enregistrer le fichier
	 *
	 * @param event Evenement
	 */
	@FXML
	public void save_file(ActionEvent event) {

		JFileChooser fileChooser = new JFileChooser(new File(Constants.REP_OPEN_FILECHOSER));

		fileChooser.setDialogTitle(Constants.SAVE_FILE);
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
			JsonFileManager.getInstance().save(getSelectedTab().getAgenda().getTimeTable(),
					(fileChooser.getSelectedFile().getAbsolutePath() + "/" + getFileName()));
		}
	}

	/**
	 * Afficher le popup de gestion des favoris
	 *
	 * @param ae ActionEvent
	 */

	@FXML
	public void favPopUp(ActionEvent ae) {

		final Stage popUp = new Stage();

		popUp.setTitle("Gerer");
		popUp.initModality(Modality.APPLICATION_MODAL);

		AnchorPane root;

		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(Constants.FAV_POPUP));
			FavPopUpControleur controller = new FavPopUpControleur(myfavs, diff_fav, this);
			fxmlLoader.setController(controller);
			root = fxmlLoader.load();
			Scene scene = new Scene(root);
			popUp.setScene(scene);
			popUp.initOwner(Main.mainStage);
			popUp.show();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private Tab getSelectedTab() {
		return (Tab) tabPane.getSelectionModel().getSelectedItem();
	}

	public String getSelectedTabVersionId() {
		return ((Tab) tabPaneV2.getSelectionModel().getSelectedItem()).getVersionId();
	}

	public static void setNewTabForVersionning(ArrayList<Creneaux> list, String name, String id) {
		AgendaCustom agenda = new AgendaCustom(new TimeTable(name, "Path", list, TimeTable.TYPE.EMPTY_BASED, id));

		Tab tab = new Tab(agenda, me);
		agenda.setParent(tab);

		tabPaneV2.getTabs().add(tabPaneV2.getTabs().size() - 1, tab);
		tabPaneV2.getSelectionModel().select(tabPaneV2.getTabs().size() - 2);
	}

	public void setSelectedTabVerID(String name, String id) {
		Tab tab = ((Tab) tabPaneV2.getSelectionModel().getSelectedItem());
		tab.setVersionId(id);
		tab.setName(name);
		tab.setText(name);
		tab.getAgenda().getTimeTable().setName(name);
	}

	public ArrayList<Creneaux> getCreneauxList() {
		return ((Tab) tabPaneV2.getSelectionModel().getSelectedItem()).getAgenda().getTimeTable()
				.getCopiedCreneauxList();
	}

	public ArrayList<Creneaux> getCreneauxModified() {
		return ((Tab) tabPaneV2.getSelectionModel().getSelectedItem()).getAgenda().getTimeTable().getCreneauxModified();
	}

	private String getFileName() {

		TextInputDialog dialog = new TextInputDialog("");
		int id = JsonFileManager.lastId;

		dialog.setTitle("Nom du fichier");
		dialog.setContentText("Nom du fichier :");

		while (dialog.getEditor().getText().equals("")) {
			dialog.getEditor().setText("TimeTable_" + id);
			dialog.showAndWait();
		}
		return dialog.getEditor().getText();
	}

	private ContextMenu getContextMenu(Stage primaryStage) {
		ContextMenu contextMenu = new ContextMenu();

		MenuItem itemSelect = new MenuItem("Choisir cette version");
		itemSelect.setOnAction(e -> {

			try {
				Version wanted = Version.getVersion(selectedVersion);
				setNewTabForVersionning(wanted.getCreneauxList(), wanted.getName(), wanted.getNameTimestamp());
			} catch (Exception excep) {
				JOptionPane.showMessageDialog(null, Constants.errSelect, Constants.errMssg, JOptionPane.ERROR_MESSAGE);
			}
			primaryStage.close();

		});
		MenuItem itemBranch = new MenuItem("Sauvegarder cette branche");
		itemBranch.setOnAction(e -> {

			try {
				primaryStage.close();
				Version wanted = Version.getVersion(selectedVersion);
				JFileChooser fileChooser = new JFileChooser(new File(Constants.REP_OPEN_FILECHOSER));
				fileChooser.setDialogTitle(Constants.SAVE_VERSION);

				if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
					Version.saveBranch(fileChooser, wanted);
			} catch (Exception excep) {
				JOptionPane.showMessageDialog(null, Constants.errSelect, Constants.errMssg, JOptionPane.ERROR_MESSAGE);
			}

		});

		contextMenu.getItems().addAll(itemSelect, itemBranch);
		return contextMenu;
	}

	@FXML
	private void create_new_project(ActionEvent ae) {
		clearTabs();
		Version.clearRoot();
		addNewTab(new AgendaCustom(new TimeTable(Constants.NEW_TAB + tabPaneV2.getTabs().size(), "Path",
				new ArrayList<Creneaux>(), TimeTable.TYPE.EMPTY_BASED)));

	}

	@FXML
	private void open_project(ActionEvent ae) {
		JFileChooser fileChooser = new JFileChooser(new File("."));
		FileNameExtensionFilter filter = new FileNameExtensionFilter(Constants.FORMAT_JSON, "json");
		fileChooser.setFileFilter(filter);

		if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			try {
				clearTabs();
				Version.loadRoot(JsonFileManager.getInstance().loadVersion(fileChooser.getSelectedFile()));
				JOptionPane.showMessageDialog(null, Constants.allRight, Constants.infoMssg,
						JOptionPane.INFORMATION_MESSAGE);
			} catch (Exception excep) {
				JOptionPane.showMessageDialog(null, Constants.errOpenVer + " : " + excep, Constants.errMssg,
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	@FXML
	private void save_project(ActionEvent ae) {
		try {
			JFileChooser fileChooser = new JFileChooser(new File(Constants.REP_OPEN_FILECHOSER));

			fileChooser.setDialogTitle(Constants.SAVE_VERSION);
			// fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
				Version.saveRoot(fileChooser);
		} catch (Exception excep) {
			JOptionPane.showMessageDialog(null, Constants.errSaveVer + " : " + excep, Constants.errMssg,
					JOptionPane.ERROR_MESSAGE);
		}
	}

	@FXML
	private void read_project(ActionEvent ae) {
		treeView.setRoot(Version.getTreeItem());

		Stage primaryStage = new Stage();
		BorderPane b = new BorderPane();
		if (!Version.rootIsEmpty()) {
			ContextMenu contextMenu = getContextMenu(primaryStage);

			treeView.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
				@Override
				public void handle(ContextMenuEvent event) {
					contextMenu.show(treeView, event.getScreenX(), event.getScreenY());
				}
			});
		}

		b.setCenter(treeView);
		primaryStage.setScene(new Scene(b, 600, 400));
		primaryStage.setTitle("L'arborescence");
		primaryStage.show();
	}

	@FXML
	private void add_version(ActionEvent ae) {
		final Stage popUp = new Stage();

		popUp.setTitle("Ajouter une version de l'agenda");
		popUp.initModality(Modality.APPLICATION_MODAL);

		AnchorPane root;

		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(Constants.ADDVERSION_POPUP));
			AddVersionController controller = new AddVersionController(this);
			fxmlLoader.setController(controller);
			root = fxmlLoader.load();
			Scene scene = new Scene(root);
			popUp.setScene(scene);
			popUp.initOwner(Main.mainStage);
			popUp.show();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	@FXML
	private void load_branch(ActionEvent ae) {
		JFileChooser fileChooser = new JFileChooser(new File("."));
		FileNameExtensionFilter filter = new FileNameExtensionFilter(Constants.FORMAT_JSON, "json");
		fileChooser.setFileFilter(filter);

		if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			try {
				Version.loadBranch(JsonFileManager.getInstance().loadVersion(fileChooser.getSelectedFile()));
				JOptionPane.showMessageDialog(null, Constants.allRight2, Constants.infoMssg,
						JOptionPane.INFORMATION_MESSAGE);
			} catch (Exception excep) {
				JOptionPane.showMessageDialog(null, Constants.errOpenVer + " : " + excep, Constants.errMssg,
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void clearTabs() {
		tabPaneV2.getTabs().clear();
		setAddTabeHandler();
	}

	@SuppressWarnings("unused")
	private Long getTimeStamp(String selectedVersion) throws ParseException {
		DateFormat formatter = new SimpleDateFormat(Constants.DATE_FORMAT);
		Date date = formatter.parse(selectedVersion);
		Timestamp timeStampDate = new Timestamp(date.getTime());
		return timeStampDate.getTime();
	}
}
