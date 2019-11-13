package modeles;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JFileChooser;

import code.barbot.Creneaux;
import controleur.MainScreenControleur;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;

public class Version {
	private static Version rootVersion = null;
	private static String rootName = "saveFileVersion";

	private final Long timestamp;
	private final String name;
	private final ArrayList<Creneaux> creneauxList;
	private final Version parent;
	private final Map<Long, Version> alternativeVersions;

	public Version(Version parent, Long t, String str, ArrayList<Creneaux> l, Map<Long, Version> map) {
		this.parent = parent;
		timestamp = t;
		name = str;
		creneauxList = l;
		alternativeVersions = map;
	}

	public Version(Version parent, Long t, String str, ArrayList<Creneaux> l) {
		this(parent, t, str, l, new TreeMap<>());
	}

	public static String getRootName() {
		return rootName;
	}

	public static void setRootName(String str) {
		rootName = str;
	}

	public String getName() {
		return name;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public Map<Long, Version> getAlternativeVersions() {
		return alternativeVersions;
	}

	public static Version2 toVersion2(Version v1) {
		Version2 v2 = new Version2();
		ArrayList<Version2> list = new ArrayList<>();
		v1.alternativeVersions.entrySet().forEach(set -> {
			list.add(toVersion2(set.getValue()));
		});
		v2.setCreneauxsList(Creneaux.toCreneauxVersion2(v1.creneauxList));
		v2.setVersion2List(list);
		v2.setTimestamp(v1.timestamp);
		v2.setName(v1.name);

		return v2;
	}

	private static Long nowStamp() {
		return new Timestamp(System.currentTimeMillis()).getTime();
	}

	public static void addNewVersion(String value) {
		Long key = nowStamp();

		if (rootVersion == null) {
			rootVersion = new Version(null, key, value, MainScreenControleur.getCreneauxList());
		} else {
			Version parent = rootVersion.getVersion(new Long(MainScreenControleur.getSelectedTabVersionId()));
			parent.addAltVer(key, value);
		}
		MainScreenControleur.setSelectedTabVerID(value, key.longValue());
	}

	private void addAltVer(Long t, String str) {
		Version ver = new Version(this, t, str, MainScreenControleur.getCreneauxList());
		alternativeVersions.put(t, ver);
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
			MainScreenControleur.setNewTabForVersionning(wantedVersion.getCreneauxList(), wantedVersion.name,
					wantedVersion.timestamp);
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
			MainScreenControleur.setNewTabForVersionning(vDupli.getCreneauxList(), vDupli.name, vDupli.timestamp);
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
		ImageView imageVersion = new ImageView(Constants.PICS_VERSION);

		String s = new SimpleDateFormat(Constants.DATE_FORMAT).format(date);
		TreeItem<String> tree = new TreeItem<>(name + " @ " + s, imageVersion);
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

	public static boolean rootIsEmpty() {
		return rootVersion == null;
	}

	public static void saveRoot(JFileChooser fileChooser) {
		JsonFileManager.getInstance().saveVersion(toVersion2(rootVersion),
				(fileChooser.getSelectedFile().getAbsolutePath() + "/" + rootName));
	}

	public static void loadRoot(File file) {
		rootVersion = Version2.toVersion(null, JsonFileManager.getInstance().loadVersion(file));
	}

	private static void fillNames(Version v, Map<String, Long> result) {
		result.put(v.name, v.timestamp);
		v.alternativeVersions.values().forEach(alt -> fillNames(alt, result));
	}

	public static Map<String, Long> getMapNames() {
		Map<String, Long> result = new HashMap<>();
		if (!rootIsEmpty())
			fillNames(rootVersion, result);
		return result;
	}

	// comparaison de la list de crenaux d'une version a une autre
	@SuppressWarnings("unused")
	private boolean compareCreneaux(Version o) {
		boolean isEqual = this.creneauxList.equals(o.creneauxList);
		return isEqual;
	}
}
