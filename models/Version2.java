package models;

import java.util.ArrayList;

import code.barbot.Creneaux;
/**
 * Il s'agit d'une classe qui représente la classe Version, elle est faite pour la sauvegarde
 * @author Pionan
 *
 */
public class Version2 {
	private Long timestamp;
	private ArrayList<CreneauxVersion2> creneauxsList;
	private ArrayList<Version2> listVersion2;
	String name;

	public Long getTimestamp() {
		return timestamp;
	}

	public static Version toVersion(Version parent, Version2 v2) {
		ArrayList<Version> listVer = new ArrayList<>();
		Version v1 = new Version(parent, v2.timestamp, v2.name, Creneaux.toCreneaux(v2.creneauxsList), listVer);
		v2.listVersion2.forEach(alt -> {
			listVer.add(toVersion(v1, alt));
		});
		return v1;
	}

	public void setTimestamp(Long timev2) {
		this.timestamp = timev2;
	}

	public ArrayList<Version2> getVersion2List() {
		return listVersion2;
	}

	public void setVersion2List(ArrayList<Version2> newlistVersion2) {
		this.listVersion2 = newlistVersion2;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<CreneauxVersion2> getCreneauxsList() {
		return creneauxsList;
	}

	public void setCreneauxsList(ArrayList<CreneauxVersion2> creneauxsList) {
		this.creneauxsList = creneauxsList;
	}

}
