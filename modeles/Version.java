package modeles;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JFileChooser;

import code.barbot.Creneaux;
import controleur.MainScreenControleur;
import javafx.scene.control.TreeItem;

public class Version {
	private static Version currentVersion = null;
	private static Version rootVersion = null;
	private static String rootName = "";

	private Long timestamp;
	private String name;
	private Map<Long, Version> alternativeVersions = new TreeMap<>();
	private ArrayList<Creneaux> creneauxList;
	private Version parent;
	// TODO en plus de l'emploi du temps class TimeTable

	public Version(Version parent, Long t, String str, ArrayList<Creneaux> l) {
		this.parent = parent;
		timestamp = t;
		name = str;
		creneauxList = l;
	}
	
	public static String getRootName() {
		return rootName;
	}
	public String getName() {
		return name;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	private static Long nowStamp() {
		return new Timestamp(System.currentTimeMillis()).getTime();
	}

	public static void addNewVersion(String value) {
		Long key = nowStamp();

		if (rootVersion == null) {
			currentVersion = new Version(null, key, value, MainScreenControleur.getCreneauxList());
			rootVersion = currentVersion;
		} else {
			currentVersion = currentVersion.addAltVer(key, value);
		}
		// String v = value + "@" + key;
		// System.out.println(v);
		MainScreenControleur.setSelectedTabVerID(value, key.longValue());
	}

	private Version addAltVer(Long t, String str) {
		Version ver = new Version(this, t, str, MainScreenControleur.getCreneauxList());
		alternativeVersions.put(t, ver);
		return ver;
	}

	public Version getVersion(Long t) {
		if (timestamp.compareTo(t) == 0)
			return this;
		Iterator<Version> i = alternativeVersions.values().iterator();
		Version v;
		while (i.hasNext()) {
			v = i.next().getVersion(t);
			if (v != null)
				return v;
		}
		return null;
	}

	public static void changeVersion(Long t) {
		Version wantedVersion = rootVersion.getVersion(t);
		if (wantedVersion != null) {
			currentVersion = wantedVersion;
			MainScreenControleur.setNewTabForVersionning(currentVersion.getCreneauxList(), currentVersion.name, currentVersion.timestamp);
		}
	}

	public static boolean dupliVersion(Long t) {
		Version v = rootVersion.getVersion(t);
		if (v.parent == null)
			return false;
		else {
			Long time = nowStamp();
			Version vDupli = new Version(v.parent, time, v.name, v.getCreneauxList());
			v.parent.alternativeVersions.put(vDupli.timestamp, vDupli);
			currentVersion = vDupli;
			MainScreenControleur.setNewTabForVersionning(currentVersion.getCreneauxList(), currentVersion.name, currentVersion.timestamp);
			return true;
		}
	}

	public ArrayList<Creneaux> getCreneauxList() {
		ArrayList<Creneaux> clone = new ArrayList<>();
		for (Creneaux o : creneauxList) {
			clone.add((Creneaux) o.clone());
		}
		return clone;
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
		TreeItem<String> tree = new TreeItem<>(name + " @ " + s);
		if (alternativeVersions != null && !alternativeVersions.isEmpty()) {
			alternativeVersions.values().forEach(alt -> {
				tree.getChildren().add(alt.toTreeItemString());
			});
		}
		return tree;
	}

	public static TreeItem<String> getTreeItem() {
		if (rootVersion == null)
			return new TreeItem<String>("Aucune version enregistrée");
		return rootVersion.toTreeItemString();
	}

	// comparaison de la list de crenaux d'une version a une autre
	public boolean compareCreneaux(Version o) {
		boolean isEqual = this.creneauxList.equals(o.creneauxList);
		return isEqual;
	}
	
	public static void update(long id) {
		if(id==0 || rootVersion==null) {
			System.out.println("loupé");
			return;
		}
		
		Version v = rootVersion.getVersion(id);
		if(v != null)
			v.creneauxList = MainScreenControleur.getCreneauxList();
	}
	
	public static void saveRoot(JFileChooser fileChooser) {
		JsonFileManager.getInstance().saveVersion(rootVersion, (fileChooser.getSelectedFile().getAbsolutePath() + "/"));
	}
}
