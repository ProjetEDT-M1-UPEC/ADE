package modeles;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.JFileChooser;

import code.barbot.Creneaux;
import controleur.MainScreenControleur;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;

public class Version {
	private static Version rootVersion = null;
	public final static String rootName = "saveFileVersion";

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
		String id;

		if (rootVersion == null) {
			rootVersion = new Version(null, key, value, MainScreenControleur.getCreneauxList());
			id = rootVersion.getNameTimestamp();
		} else {
			Version parent = rootVersion.getVersion(MainScreenControleur.getSelectedTabVersionId());
			if (parent == null)
				return;
			id = parent.addAltVer(key, value);
		}
		MainScreenControleur.setSelectedTabVerID(value, id);
	}

	private String addAltVer(Long t, String str) {
		Version ver = new Version(this, t, str, MainScreenControleur.getCreneauxList());
		alternativeVersions.put(t, ver);
		return ver.getNameTimestamp();
	}

	private Version searchVersion(String str) {
		if (str.equals(getNameTimestamp()))
			return this;
		Iterator<Version> i = alternativeVersions.values().iterator();
		Version v;
		while (i.hasNext()) {
			v = i.next().searchVersion(str);
			if (v != null)
				return v;
		}
		return null;
	}
	
	private Version getVersion(String id) {
		if(id==null || id.isEmpty() || rootIsEmpty())
			return null;
		return searchVersion(id);
	}

	public static Version changeVersion(String id) {
		return rootVersion.getVersion(id);
	}

	public static Version dupliVersion(String str) {
		Version v = rootVersion.getVersion(str);
		if (v == null || v.parent == null)
			return null;
		else {
			Version vDupli = new Version(v.parent, nowStamp(), v.name, v.getCreneauxList());
			v.parent.alternativeVersions.put(vDupli.timestamp, vDupli);
			return vDupli;
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
		ImageView imageVersion = new ImageView(Constants.PICS_VERSION);
		TreeItem<String> tree = new TreeItem<>(getNameTimestamp(), imageVersion);
		if (alternativeVersions != null && !alternativeVersions.isEmpty()) {
			alternativeVersions.values().forEach(alt -> {
				tree.getChildren().add(alt.toTreeItemString());
			});
		}
		return tree;
	}

	public static TreeItem<String> getTreeItem() {
		if (rootIsEmpty())
			return new TreeItem<String>(Constants.EMPTY_TREE);
		return rootVersion.toTreeItemString();
	}

	public static boolean rootIsEmpty() {
		return rootVersion == null;
	}

	public static void saveRoot(JFileChooser fileChooser) {
		if (rootIsEmpty())
			return;
		JsonFileManager.getInstance().saveVersion(toVersion2(rootVersion),
				(fileChooser.getSelectedFile().getAbsolutePath() + "/" + rootName));
	}

	public static void loadRoot(File file) {
		Version2 v2 = JsonFileManager.getInstance().loadVersion(file);
		if(v2 != null)
			rootVersion = Version2.toVersion(null, v2);
	}
	
	public static void putRootInState(State state) {
		if(!rootIsEmpty())
			state.setRoot(toVersion2(rootVersion));
	}
	private static void fillNames(Version v, Set<String> result) {
		result.add(v.getNameTimestamp());
		v.alternativeVersions.values().forEach(alt -> fillNames(alt, result));
	}

	public static Set<String> getSetNames() {
		Set<String> result = new HashSet<>();
		if (!rootIsEmpty())
			fillNames(rootVersion, result);
		return result;
	}

	public String getNameTimestamp() {
		Date date = new Date(timestamp);
		return name + " " + new SimpleDateFormat(Constants.DATE_FORMAT).format(date);
	}

	// comparaison de la list de crenaux d'une version a une autre
	@SuppressWarnings("unused")
	private boolean compareCreneaux(Version o) {
		boolean isEqual = this.creneauxList.equals(o.creneauxList);
		return isEqual;
	}
}
