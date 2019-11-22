package modeles;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JFileChooser;

import code.barbot.Creneaux;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;

public class Version {
	private static Version rootVersion = null;

	private final Long timestamp;
	private final String name;
	private final ArrayList<Creneaux> creneauxList;
	private final Version parent;
	private final ArrayList<Version> alternativeVersions;

	public Version(Version parent, Long t, String str, ArrayList<Creneaux> l, ArrayList<Version> listVer) {
		this.parent = parent;
		timestamp = t;
		name = str;
		creneauxList = l;
		alternativeVersions = listVer;
	}

	public Version(Version parent, Long t, String str, ArrayList<Creneaux> l) {
		this(parent, t, str, l, new ArrayList<>());
	}

	public String getName() {
		return name;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public ArrayList<Version> getAlternativeVersions() {
		return alternativeVersions;
	}

	/**
	 * Cette fonction prend en param�tre une Version et retourne la copie de celle-ci pour obtenir une Version2 
	 * qui est par la suite utilis�e pour la sauvegarde
	 * @param v1 
	 * @return Renvoie une copie de v1
	 */
	public static Version2 toVersion2(Version v1) {
		Version2 v2 = new Version2();
		ArrayList<Version2> list = new ArrayList<>();
		v1.alternativeVersions.forEach(ver -> {
			list.add(toVersion2(ver));
		});
		v2.setCreneauxsList(Creneaux.toCreneauxVersion2(v1.creneauxList));
		v2.setVersion2List(list);
		v2.setTimestamp(v1.timestamp);
		v2.setName(v1.name);

		return v2;
	}

	/**
	 * 
	 * @return Renvoie la date actuelle en Long
	 */
	private static Long nowStamp() {
		return new Timestamp(System.currentTimeMillis()).getTime();
	}

	/**
	 * Cette fonction cr�e une nouvelle version sous une version parente que l'on r�cup�re
	 * @param value Le nom de la nouvelle version
	 * @param creneaux La liste des cr�neaux de la nouvelle version
	 * @param currentVersionId
	 */
	public static String addNewVersion(String value, ArrayList<Creneaux> creneaux, String currentVersionId) {
		String id = null;

		if (rootVersion == null) {
			rootVersion = new Version(null, nowStamp(), value, creneaux);
			id = rootVersion.getNameTimestamp();
		} else {
			Version parent = getVersion(currentVersionId);
			if (parent != null) {
				Version ver = new Version(parent, nowStamp(), value, creneaux);
				parent.alternativeVersions.add(ver);
				id = ver.getNameTimestamp();
			}
		}
		return id;
	}

	/**
	 * Cette m�thode cherche une version � l'aide de l'identifiant repr�sentant son nom avec sa date de cr�ation
	 * @param id L'identifiant de la version que l'on cherche est un String
	 * @return
	 */
	private Version searchVersion(String id) {
		if (id.equals(getNameTimestamp()))
			return this;
		Iterator<Version> i = alternativeVersions.iterator();
		Version v;
		while (i.hasNext()) {
			v = i.next().searchVersion(id);
			if (v != null)
				return v;
		}
		return null;
	}

	/**
	 * V�rifie si l'identifiant est coh�rent
	 * @see searchVersion
	 * @param id L'identifiant de la version que l'on va chercher
	 * @return
	 */
	public static Version getVersion(String id) {
		if (id == null || id.isEmpty() || rootIsEmpty())
			return null;
		return rootVersion.searchVersion(id);
	}

	/**
	 * 
	 * @return Renvoie une copie de la liste de cr�neaux de cette version
	 */
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
		alternativeVersions.forEach(v -> {
			sb.append(name + "->" + v.toString());
		});
		return sb.toString();
	}

	/**
	 * Cette m�thode construit une TreeItem pour l'affichage de l'arborescence, le parcours est r�curssif
	 * @return
	 */
	public TreeItem<String> toTreeItemString() {
		ImageView imageVersion = new ImageView(Constants.PICS_VERSION);
		TreeItem<String> tree = new TreeItem<>(getNameTimestamp(), imageVersion);
		if (alternativeVersions != null && !alternativeVersions.isEmpty()) {
			alternativeVersions.forEach(alt -> {
				tree.getChildren().add(alt.toTreeItemString());
			});
			tree.setExpanded(true);
		}
		return tree;
	}

	/**
	 * Cette fonction v�rifie si l'arborescence est vide avant de retourner une TreeItem 
	 * @see toTreeItemString
	 * @return
	 */
	public static TreeItem<String> getTreeItem() {
		if (rootIsEmpty())
			return new TreeItem<String>(Constants.EMPTY_TREE);
		return rootVersion.toTreeItemString();
	}

	/**
	 * 
	 * @return Renvoie un boolean pour v�rifier si l'arborescence est vide ou non
	 */
	public static boolean rootIsEmpty() {
		return rootVersion == null;
	}

	public boolean hasParent() {
		return parent != null;
	}

	/**
	 * Cette fonction sauvegarde l'arborescence en cours
	 * @param fileChooser
	 */
	public static void saveRoot(JFileChooser fileChooser) {
		if (rootIsEmpty())
			return;
		JsonFileManager.getInstance().saveVersion(toVersion2(rootVersion),
				(fileChooser.getSelectedFile().getAbsolutePath()));
	}

	/**
	 * Cette fonction charge l'arborescence donn�e en param�tre
	 * @param v2
	 */
	public static void loadRoot(Version2 v2) {
		if (v2 != null)
			rootVersion = Version2.toVersion(null, v2);
	}

	/**
	 * Cette fonction sauvegarde l'�tat de l'arborescence pour la sauvegarde automatique lorsque l'on quitte l'application
	 * @param state
	 */
	public static void putRootInState(State state) {
		if (!rootIsEmpty())
			state.setVersion2(toVersion2(rootVersion));
	}
	
	/**
	 * Cette fonction remplie la variable result en remplissant le nom de toutes les versions existantes
	 * @param v
	 * @param result Une liste de noms des versions
	 */
	private static void fillNames(Version v, Set<String> result) {
		result.add(v.getNameTimestamp());
		v.alternativeVersions.forEach(alt -> fillNames(alt, result));
	}
	
	/**
	 * @see fillNames
	 * @return Une liste de noms des versions
	 */
	public static Set<String> getSetNames() {
		Set<String> result = new HashSet<>();
		if (!rootIsEmpty())
			fillNames(rootVersion, result);
		return result;
	}

	/**
	 * 
	 * @return Renvoie la combinaison du nom avec la date de cr�ation d'une version
	 */
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

	/**
	 * Cette fonction vide l'arborescence en cours
	 */
	public static void clearRoot() {
		rootVersion = null;

	}
}
