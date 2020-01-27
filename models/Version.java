package models;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JFileChooser;

import backup.JsonFileManager;
import code.barbot.Creneaux;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;

/**
 * Il s'agit d'une classe qui représente une version d'un emploi du temps de
 * l'agenda
 *
 * @author Pionan
 *
 */
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
	 * Cette fonction prend en paramètre une Version et retourne la copie de
	 * celle-ci pour obtenir une Version2 qui est par la suite utilisée pour la
	 * sauvegarde
	 *
	 * @param v1 en Version
	 * @return Renvoie une copie de v1 en Version2
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
	 * Cette fonction crée une nouvelle version sous une version parente que l'on
	 * récupère
	 *
	 * @param value            Le nom de la nouvelle version
	 * @param creneaux         La liste des créneaux de la nouvelle version
	 * @param currentVersionId L'identifiant de la version parente
	 * @return Renvoie l'identifiant de la nouvelle version créée
	 */

	/**
	 * Pour effectuer notre test on creer un arbre en largeur avec la fonction
	 * addnewVersion on pourra atteindre les 300000 version avant d'avoir l'erreur
	 * outOfMemory
	 */
	public static String addNewVersion(String value, ArrayList<Creneaux> creneaux, String currentVersionId) {
		String id = null;

		if (rootVersion == null) {
			rootVersion = new Version(null, nowStamp(), value, creneaux);
			id = rootVersion.getNameTimestamp();
		}

		else {
			Version parent = getVersion(currentVersionId);
			if (parent != null) {
//				int i = 0;
//				while (i < 350000) {
				Version ver = new Version(parent, nowStamp(), value, creneaux);
				parent.alternativeVersions.add(ver);
				id = ver.getNameTimestamp();
				// i++;
				// }
			}
		}
		return id;
	}

	/**
	 * Cette méthode cherche une version à l'aide de l'identifiant représentant son
	 * nom avec sa date de création
	 *
	 * @param id L'identifiant de la version que l'on cherche est un String
	 * @return Renvoie la version que l'on cherche
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
	 * Vérifie si l'identifiant est cohérent
	 *
	 * @see searchVersion
	 * @param id L'identifiant de la version que l'on va chercher
	 * @return Renvoie la version que l'on cherche
	 */
	public static Version getVersion(String id) {
		if (id == null || id.isEmpty() || rootIsEmpty())
			return null;
		return rootVersion.searchVersion(id);
	}

	/**
	 * 
	 * @return Renvoie une copie de la liste de crÃ©neaux de cette version
	 */
	private ArrayList<Creneaux> getCloningList() {
		ArrayList<Creneaux> clone = new ArrayList<>();
		for (Creneaux o : creneauxList) {
			clone.add((Creneaux) o.clone());
		}
		return clone;
	}

	/**
	 *
	 * @return Construction de la liste de crÃ©neaux de cette version
	 */
	public ArrayList<Creneaux> getCreneauxList() {
		if (parent == null)
			return getCloningList();
		else {
			ArrayList<Creneaux> crParentList = parent.getCreneauxList();
			for (Creneaux crDiff : getCloningList()) {
				switch (crDiff.getStatus()) {
				case Created:
					crParentList.add(crDiff);
					break;
				case Modified:
					crParentList.add(crDiff);
					break;
				case Deleted:
					applyDiff(crDiff, crParentList);
				case NoChange:
					break;
				}
			}
			return crParentList;
		}
	}

	private void applyDiff(Creneaux crDiff, ArrayList<Creneaux> crParentList) {
		Iterator<Creneaux> it = crParentList.iterator();
		Creneaux cr;
		while (it.hasNext()) {
			cr = it.next();
			if (crDiff.equals(cr)) {
				switch (crDiff.getStatus()) {
				case Deleted:
					it.remove();
					break;
				default:
					break;
				}
			}
		}
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
	 * Cette méthode construit une TreeItem pour l'affichage de l'arborescence, le
	 * parcours est récursif
	 *
	 * @return TreeItem
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
	 * Cette fonction vérifie si l'arborescence est vide avant de retourner une
	 * TreeItem
	 *
	 * @see toTreeItemString
	 * @return TreeItem
	 */
	public static TreeItem<String> getTreeItem() {
		if (rootIsEmpty())
			return new TreeItem<String>(Constants.EMPTY_TREE);
		return rootVersion.toTreeItemString();
	}

	/**
	 *
	 * @return Renvoie un boolean pour vérifier si l'arborescence est vide ou non
	 */
	public static boolean rootIsEmpty() {
		return rootVersion == null;
	}

	public boolean hasParent() {
		return parent != null;
	}

	/**
	 * Cette fonction crée une copie d'une version sans ses versions filles
	 *
	 * @param v Une version que l'on souhaite copier
	 * @return Renvoie une copie simplifiée d'une version
	 */
	private static Version getSimpleCopiedVersion(Version v) {
		return new Version(v.parent, v.timestamp, v.name, v.creneauxList);
	}

	/**
	 * Cette fonction récursive trouve le parent d'une version pour construire sa
	 * branche d'origine
	 *
	 * @param wanted Une version sélectionnée
	 * @return Renvoie la branche de la version sélectionnée
	 */
	private static Version getSelectedBranch(Version wanted) {
		if (wanted.parent == null)
			return wanted;
		Version prevV = getSimpleCopiedVersion(wanted.parent);
		prevV.alternativeVersions.add(wanted);
		return getSelectedBranch(prevV);
	}

	/**
	 * Cette fonction sauvegarde une branche sélectionnée à partir d'un nœud de
	 * version
	 *
	 * @param fileChooser est le chemin de sauvegarde
	 * @param wanted      est la version sélectionnée
	 */
	public static void saveBranch(JFileChooser fileChooser, Version wanted) {
		if (wanted == null)
			return;

		JsonFileManager.getInstance().saveVersion(toVersion2(getSelectedBranch(getSimpleCopiedVersion(wanted))),
				(fileChooser.getSelectedFile().getAbsolutePath()));
	}

	/**
	 * Cette fonction sauvegarde l'arborescence en cours
	 *
	 * @param fileChooser Contient le chemin dans lequel nous sauvegardons
	 *                    l'arborescence
	 */
	public static void saveRoot(JFileChooser fileChooser) {
		if (rootIsEmpty())
			return;
		JsonFileManager.getInstance().saveVersion(toVersion2(rootVersion),
				(fileChooser.getSelectedFile().getAbsolutePath()));
	}

	/**
	 * Cette fonction charge l'arborescence donnée en paramètre
	 *
	 * @param v2 Version2
	 */
	public static void loadRoot(Version v) {
		if (v != null)
			rootVersion = v;
	}

	/***
	 * Cette fonction ajoute la branche à l'arborescence
	 *
	 * @param v2 Une branche récupérée
	 * @throws Exception
	 */
	public static void loadBranch(Version branch) throws Exception {
		if (branch == null)
			throw new Exception(Constants.EMPTY_TREE);
		else if (rootVersion == null)
			rootVersion = branch;
		else if (!rootVersion.equals(branch))
			throw new Exception(Constants.WRONG_TREE);
		loadingBranch(rootVersion, branch.alternativeVersions.get(0));
	}

	/***
	 * Cette fonction récursive  cherche dans la Version current la présence de la
	 * version Branch et ajoute la branche une fois que la dernière Version en
	 * commun a été trouvée
	 *
	 * @param current
	 * @param branch
	 */
	private static void loadingBranch(Version current, Version branch) {
		if (current.alternativeVersions.stream().allMatch(alt -> {
			if (alt.equals(branch)) {
				if (!branch.alternativeVersions.isEmpty())
					loadingBranch(alt, branch.alternativeVersions.get(0));
				return false;
			}
			return true;
		})) {
			current.alternativeVersions.add(branch);
		}
	}

	/**
	 * Cette fonction sauvegarde l'état de l'arborescence pour la sauvegarde
	 * automatique lorsque l'on quitte l'application
	 *
	 * @param state Représente l'état du système actuel
	 */
	public static void putRootInState(State state) {
		if (!rootIsEmpty())
			state.setRoot(toVersion2(rootVersion));
	}

	/**
	 * Cette fonction remplie la variable result en remplissant le nom de toutes les
	 * versions existantes
	 *
	 * @param v      Version en cours que l'on parcourt
	 * @param result Contient l'ensemble des noms de toutes les versions
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
	 * @return Renvoie la combinaison du nom avec la date de création d'une version
	 */
	public String getNameTimestamp() {
		Date date = new Date(timestamp);
		return name + " " + new SimpleDateFormat(Constants.DATE_FORMAT).format(date);
	}

	/***
	 * Cette fonction compare l'identifiant de deux versions
	 *
	 * @param v Version comparée
	 * @return Renvoie vrai si les deux versions sont égales, faux sinon
	 */
	private boolean equals(Version v) {
		return getNameTimestamp().equals(v.getNameTimestamp());
	}

	/**
	 * Cette fonction vide l'arborescence en cours
	 */
	public static void clearRoot() {
		rootVersion = null;

	}
}
