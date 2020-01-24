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
 * Il s'agit d'une classe qui repr�sente une version d'un emploi du temps de
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
	 * Cette fonction prend en param�tre une Version et retourne la copie de
	 * celle-ci pour obtenir une Version2 qui est par la suite utilis�e pour la
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
	 * Cette fonction cr�e une nouvelle version sous une version parente que l'on
	 * r�cup�re
	 *
	 * @param value            Le nom de la nouvelle version
	 * @param creneaux         La liste des cr�neaux de la nouvelle version
	 * @param currentVersionId L'identifiant de la version parente
	 * @return Renvoie l'identifiant de la nouvelle version cr��e
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
	 * Cette m�thode cherche une version � l'aide de l'identifiant repr�sentant son
	 * nom avec sa date de cr�ation
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
	 * V�rifie si l'identifiant est coh�rent
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
	 * @return Renvoie une copie de la liste de créneaux de cette version
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
	 * @return Construction de la liste de créneaux de cette version
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
	 * Cette m�thode construit une TreeItem pour l'affichage de l'arborescence, le
	 * parcours est r�curssif
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
	 * Cette fonction v�rifie si l'arborescence est vide avant de retourner une
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
	 * @return Renvoie un boolean pour v�rifier si l'arborescence est vide ou non
	 */
	public static boolean rootIsEmpty() {
		return rootVersion == null;
	}

	public boolean hasParent() {
		return parent != null;
	}

	/**
	 * Cette fonction cr�e une copie d'une version sans ses versions filles
	 *
	 * @param v Une version que l'on souhaite copier
	 * @return Renvoie une copie simplifi�e d'une version
	 */
	private static Version getSimpleCopiedVersion(Version v) {
		return new Version(v.parent, v.timestamp, v.name, v.creneauxList);
	}

	/**
	 * Cette fonction r�cursive trouve le parent d'une version pour construire sa
	 * branche d'origine
	 *
	 * @param wanted Une version s�lectionn�e
	 * @return Renvoie la branche de la version s�lectionn�e
	 */
	private static Version getSelectedBranch(Version wanted) {
		if (wanted.parent == null)
			return wanted;
		Version prevV = getSimpleCopiedVersion(wanted.parent);
		prevV.alternativeVersions.add(wanted);
		return getSelectedBranch(prevV);
	}

	/**
	 * Cette fonction sauvegarde une branche s�lectionn�e � partir d'un n�ud de
	 * version
	 *
	 * @param fileChooser est le chemin de sauvegarde
	 * @param wanted      est la version s�lectionn�e
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
	 * Cette fonction charge l'arborescence donn�e en param�tre
	 *
	 * @param v2 Version2
	 */
	public static void loadRoot(Version v) {
		if (v != null)
			rootVersion = v;
	}

	/***
	 * Cette fonction ajoute la branche � l'arborescence
	 *
	 * @param v2 Une branche r�cup�r�e
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
	 * Cette fonction r�curssive cherche dans la Version current la pr�sence de la
	 * version Branch et ajoute la branche une fois que la derni�re Version en
	 * commun a �t� trouv�e
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
	 * Cette fonction sauvegarde l'�tat de l'arborescence pour la sauvegarde
	 * automatique lorsque l'on quitte l'application
	 *
	 * @param state Repr�sente l'�tat du syst�me actuel
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
	 * @return Renvoie la combinaison du nom avec la date de cr�ation d'une version
	 */
	public String getNameTimestamp() {
		Date date = new Date(timestamp);
		return name + " " + new SimpleDateFormat(Constants.DATE_FORMAT).format(date);
	}

	/***
	 * Cette fonction compare l'identifiant de deux versions
	 *
	 * @param v Version compar�e
	 * @return Renvoie vrai si les deux versions sont �gales, faux sinon
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
