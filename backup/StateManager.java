package backup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import controllers.MainScreenControleur;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import models.State;
import models.TimeTableV2;
import models.Version;
/***
 * Cette classe sauvegarde l'�tat de l'application lorsque l'on la quitte et restaure l'�tat de l'application lorsque l'on la r�ouvre
 * @author Pionan
 *
 */
public class StateManager {

	/**
	 * une classe utiliser pour sauvegrder l'�tat de syst�me apr�s que
	 * l'utilisateur ferme l'application et recharger l'�tat � la prochaine
	 * ouverture
	 */

	static ObjectMapper objectMapper;
	static StateManager stateManager;
	private static final String path = "StateFileSave";

	/**
	 * utilisation de design singleton dans Constructeur pour l assur� une seule
	 * instociation de StateManager et ObjectMapper
	 * @return Renvoie le stateManager actuel
	 */
	public static StateManager getInstance() {
		if (stateManager == null)
			stateManager = new StateManager();
		if (objectMapper == null)
			objectMapper = new ObjectMapper();
		return stateManager;
	}

	/**
	 * r�cup�rer et sauvegarder l'�tat de syst�me dans un fichier
	 */
	public void saveState() {

		ArrayList<TimeTableV2> list = new ArrayList<TimeTableV2>();
		ObservableList<Tab> tabs = MainScreenControleur.tabPaneV2.getTabs();

		for (int i = 0; i < tabs.size() - 1; i++) {
			view.Tab tt = (view.Tab) tabs.get(i);
			list.add(tt.getAgenda().getTimeTable().toTimeTableV2());
		}

		State state = new State();
		state.setList(list);
		state.setTabNumber(MainScreenControleur.tabPaneV2.getSelectionModel().getSelectedIndex());
		Version.putRootInState(state);

		try {
			objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
			objectMapper.writeValue(new File(path + ".json"), state);
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, models.Constants.errSaveFile + e.getMessage(),
					models.Constants.errMssg, JOptionPane.ERROR_MESSAGE);
		}

	}

	/**
	 * recharger l'�tat de syst�me en lisant le fichier
	 * @return Renvoie l'�tat du syst�me actuel
	 */
	public State load() {

		State st;

		try {
			st = objectMapper.readValue(new File(path + ".json"), State.class);
		} catch (IOException e) {
			return null;
		}

		return st;
	}

}
