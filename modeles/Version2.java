package modeles;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import code.barbot.Creneaux;

public class Version2 {
	private Long timestamp;
	private ArrayList<CreneauxVersion2> creneauxsList;
	private ArrayList<Version2> listVersion2;
	String name;

	public Long getTimestamp() {
		return timestamp;
	}

	public static Version toVersion(Version2 v2) {
		Version v1 = new Version(null, v2.timestamp, v2.name, Creneaux.toCreneaux(v2.creneauxsList));
		Map<Long, Version> map = new TreeMap<>();
		v2.listVersion2.forEach(ver2 -> {
			map.put(v2.timestamp, toVersion(ver2));
		});
		v1.setAlternativeVersions(map);
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
