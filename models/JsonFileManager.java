package models;

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
/**
 * Cette classe s'occupe de la sauvegarde et de l'ouverture de l'emploi du temps et du versionnage
 * @author Pionan
 *
 */
public class JsonFileManager implements FileManager {
	static ObjectMapper objectMapper;
	static JsonFileManager jsonFileManager;
	public static int lastId = 0;
	private String MismatchFile = "Format de fichier invalid";

	/*
	 * utilisation de design singleton dans Constructeur pour l assuré une
	 * seule instociation de JsonFileManage et ObjectMapper
	 */
	public static JsonFileManager getInstance() {
		if (jsonFileManager == null)
			jsonFileManager = new JsonFileManager();
		if (objectMapper == null)
			objectMapper = new ObjectMapper();
		return jsonFileManager;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see models.FileManager#load(java.io.File)
	 */
	@Override
	public TimeTable load(File file) {
		TimeTableV2 tmv2;
		try {
			tmv2 = objectMapper.readValue(file, TimeTableV2.class);
		} catch (MismatchedInputException e1) {
			JOptionPane.showMessageDialog(null, models.Constants.errLoadFile + MismatchFile, models.Constants.errMssg,
					JOptionPane.ERROR_MESSAGE);
			return null;
		} catch (IOException e) {

			JOptionPane.showMessageDialog(null, models.Constants.errLoadFile + e.getMessage(),
					models.Constants.errMssg, JOptionPane.ERROR_MESSAGE);
			return null;
		}
		return tmv2.toTimeTable();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see models.FileManager#save(models.TimeTable, java.lang.String)
	 */
	@Override
	public boolean save(TimeTable tm, String path) {
		TimeTableV2 listCr = tm.toTimeTableV2();

		try {
			objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
			objectMapper.writeValue(new File(path + ".json"), listCr);
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, models.Constants.errSaveFile + e.getMessage(),
					models.Constants.errMssg, JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}

	public boolean saveVersion(Version2 tm, String path) {

		try {
			objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
			objectMapper.writeValue(new File(path + ".json"), tm);
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, models.Constants.errSaveFile + e.getMessage(),
					models.Constants.errMssg, JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}

	public Version2 loadVersion(File file) {
		Version2 v2;
		try {
			v2 = objectMapper.readValue(file, Version2.class);
		} catch (MismatchedInputException e1) {
			JOptionPane.showMessageDialog(null, models.Constants.errLoadFile + MismatchFile, models.Constants.errMssg,
					JOptionPane.ERROR_MESSAGE);
			return null;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, models.Constants.errLoadFile + e.getMessage(),
					models.Constants.errMssg, JOptionPane.ERROR_MESSAGE);
			return null;
		}
		return v2;
	}
}
