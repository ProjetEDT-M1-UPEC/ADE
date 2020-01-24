package models;

import java.util.ArrayList;

import code.barbot.Creneaux;
import models.TimeTable.TYPE;

public class TimeTableV2 {

	private String path;
	private ArrayList<CreneauxVersion2> creneauxsList;
	private ArrayList<CreneauxVersion2> diffList;
	private Boolean isValide;
	private String hashCode;
	private TYPE type;
	private String versionId;
	String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersionId() {
		return versionId;
	}

	public void setVersionId(String newVersionId) {
		this.versionId = newVersionId;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public ArrayList<CreneauxVersion2> getCreneauxsList() {
		return creneauxsList;
	}

	public void setCreneauxsList(ArrayList<CreneauxVersion2> creneauxsList) {
		this.creneauxsList = creneauxsList;
	}
	
	public ArrayList<CreneauxVersion2> getDiffList() {
		return diffList;
	}

	public void setDiffList(ArrayList<CreneauxVersion2> diff) {
		this.diffList = diff;
	}
	
	public Boolean getIsValide() {
		return isValide;
	}

	public void setIsValide(Boolean isValide) {
		this.isValide = isValide;
	}

	public String getHashCode() {
		return hashCode;
	}

	public void setHashCode(String hashCode) {
		this.hashCode = hashCode;
	}

	public TimeTable toTimeTable() {
		TimeTable tt = new TimeTable(name, path, Creneaux.toCreneaux(creneauxsList), isValide, hashCode, type);
		tt.setVersionId(versionId);
		tt.setCreneauxModified(Creneaux.toCreneaux(diffList));
		return tt;
	}

	public TYPE getType() {
		return type;
	}

	public void setType(TYPE type) {
		this.type = type;
	}

}
