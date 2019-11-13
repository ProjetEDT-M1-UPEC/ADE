package modeles;

<<<<<<< HEAD
import java.net.URL;
=======
import java.io.File;
>>>>>>> f523031855d6b31500f605d79c0ce5f5881854c9
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.JFileChooser;

import org.controlsfx.control.textfield.TextFields;

import code.barbot.Creneaux;
import controleur.MainScreenControleur;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;

public class Version {
	private static Version rootVersion = null;
<<<<<<< HEAD
	private static String rootName = "";
	public static Set<Version> versions = new HashSet<>();

	
=======
	private static String rootName = "saveFileVersion";

>>>>>>> f523031855d6b31500f605d79c0ce5f5881854c9
	private Long timestamp;
	private String name;
	private Map<Long, Version> alternativeVersions = new TreeMap<>();
	private ArrayList<Creneaux> creneauxList;
	private Version parent;

	public Version(Version parent, Long t, String str, ArrayList<Creneaux> l) {
		this.parent = parent;
		timestamp = t;
		name = str;
		creneauxList = l;
		
	}

<<<<<<< HEAD
	
=======
>>>>>>> f523031855d6b31500f605d79c0ce5f5881854c9
	public static String getRootName() {
		return rootName;
	}
	public static void setRootName(String str) {
		rootName = str;
	}
	public String getName() {
		return name;
	}
	public void setName(String str) {
		name = str;
	}
	public Long getTimestamp() {
		return timestamp;
	}
	public Map<Long, Version> getAlternativeVersions(){
		return alternativeVersions;
	}
	public void setAlternativeVersions(Map<Long, Version> map){
		alternativeVersions = map;
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
			versions.add(rootVersion);
			//System.out.println(versions);
		} else {
			Version parent = rootVersion.getVersion(new Long(MainScreenControleur.getSelectedTabVersionId()));
			parent.addAltVer(key, value);
		}
		MainScreenControleur.setSelectedTabVerID(value, key.longValue());
	}

	private void addAltVer(Long t, String str) {
		Version ver = new Version(this, t, str, MainScreenControleur.getCreneauxList());
		versions.add(ver);
		//System.out.println(versions);
		
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
			MainScreenControleur.setNewTabForVersionning(wantedVersion.getCreneauxList(), wantedVersion.name, wantedVersion.timestamp);
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

	// comparaison de la list de crenaux d'une version a une autre
	public boolean compareCreneaux(Version o) {
		boolean isEqual = this.creneauxList.equals(o.creneauxList);
		return isEqual;
	}

	public static void saveRoot(JFileChooser fileChooser) {
		JsonFileManager.getInstance().saveVersion(toVersion2(rootVersion), (fileChooser.getSelectedFile().getAbsolutePath() + "/"+ rootName));
	}

	public static void loadRoot(File file){
		rootVersion = Version2.toVersion(null, JsonFileManager.getInstance().loadVersion(file));
	}
	

}
