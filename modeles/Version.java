package modeles;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import code.barbot.Creneaux;
import javafx.scene.control.TreeItem;

public class Version implements Comparable<Version> { // TODO Mettre
														// comparable<Version>
														// plus tard
	private Long timestamp;
	private String name;
	private Map<Long, Version> alternativeVersions = new TreeMap<>();
	private List<Creneaux> creneauxList = new ArrayList<>();
	// TODO en plus de l'emploi du temps class TimeTable

	public Version(Long t, String str) {
		timestamp = t;
		name = str;
	}

	public String getName() {
		return name;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public Version addAltVer(Long t, String str) {
		Version ver = new Version(t, str);
		alternativeVersions.put(t, ver);
		return ver;
	}

	public boolean containsAltVer(Long t) { // Utile ou pas ?
		return alternativeVersions.containsKey(t);
	}

	public Version getVersion(Long t) {
		if (timestamp.compareTo(t) == 0)
			return this;
		Iterator<Version> i = alternativeVersions.values().iterator();
		Version v;
		while(i.hasNext()) {
			v = i.next().getVersion(t);
			if(v!=null)
				return v;
		}
		return null;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(name + "\n");
		alternativeVersions.values().forEach(v -> {
			sb.append(name + "->" + v.toString());
		});
		return sb.toString();
	}
	
	public TreeItem<String> toTreeItemString() {
		Date date = new Date(timestamp);
		String s = new SimpleDateFormat(Constants.DATE_FORMAT).format(date);
		TreeItem<String> tree = new TreeItem<>(name+" @ "+s);
		if (alternativeVersions!=null && !alternativeVersions.isEmpty()) {
			alternativeVersions.values().forEach(alt -> {
				tree.getChildren().add(alt.toTreeItemString());
			});
		}
		return tree;
	}
	
	@Override
	public int compareTo(Version o) {
		return 0;
	}
	//comparaison de la list de crenaux d'une version a une autre
	public boolean compareCreneaux(Version o){
		  boolean isEqual = this.creneauxList.equals(o.creneauxList);
		  return isEqual;
	}

}
