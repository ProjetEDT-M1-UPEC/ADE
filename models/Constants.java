package models;
/**
 * Cette classe contient les constantes utilis�es dans tous les packages
 * @author Pionan
 *
 */
public class Constants {

	public final static String DISPLAY_COOKIE_PART = "-displaysav52="
			+ "1057855+true+true+true+true+5+5+true+false+false+true+true+true+true+true+true+true+true+true+true+true+true+true+true+true+true+true+true+true+true+true+true+no"
			+ "+false+true+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false+true+true+true+false+false+false+true+true"
			+ "+true" // Date
			+ "+false+true+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false"
			+ "+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false"
			+ "+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false"
			+ "+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false"
			+ "+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false+false+1+1+22;";

	public final static int NotConnected = 0;
	public final static int Connected = 1;
	public final static int Login = 2;
	public final static int Project = 3;
	public final static int Branche = 4;
	public final static int filiere = 5;
	public final static int level = 6;
	public final static int groupe = 7;

	public final static String url = "https://ade.u-pec.fr/jsp/";
	public final static String Projectspath = "standard/projects.jsp";

	public static final String PropertyCookie = "Cookie";

	public static final String JSESSIONID = "JSESSIONID=";

	public static final String PropertyReferer = "Referer";

	public static final String indexPath = "index.jsp";

	public static final String COOKIESPATTERN = "JSESSIONID=([0-9ABCDEF]*);";

	public static final String SET_COOKIES = "Set-Cookie";

	public static final String ParseFormat = "ISO-8859-15";

	public static final String value = "value";

	public static final String PostMethod = "POST";

	public static final String PropertyContentType = "content-type";

	public static final String LogPage = "standard/gui/interface.jsp";

	public static final String FormLoging = "login=";

	public static final String FormPass = "password=";

	public static String valueContentType = "application/x-www-form-urlencoded";

	public static String cherSet = "8859_1";

	public static String errcertificate = "Impossible de parser le site \n" + "probl�me de certificat\n";
	
	public static String errMssg = "Message D'erreur";
	
	public static String infoMssg = "Message D'information";
	
	public static String allRight = "L'arborescence a bel et bien �t� ouverte, consultez-la avec la commande F4.";
	
	public static String allRight2 = "La branche a bel et bien �t� charg�e, consultez-le avec la commande F4.";

	public static String errConnection = "Impossible de parser le site \n" + "probl�me de Conection\n";

	public static String errPath = "Lien Invalid !\n";

	public static String errLogin = "Impossible de se connecter\n";

	public static String errLogin0rPassword = "Login ou mot de passe invalid \n";

	public static String setProjectPath = "standard/gui/interface.jsp";

	public static String errChoise = "Choix invalide";

	public static String pathSetPlannings = "standard/gui/set_tree.jsp?href=/custom/modules/plannings/plannings.jsp";

	public static String showPlannings = "custom/modules/plannings/plannings.jsp";

	public static String rootPath = "standard/gui/tree.jsp";

	public static String errcategory = "Impossible de charger les cat�gories";
	public static String errBranche = "Impossible de charger les Branches";

	public static String openCategoryURL = "standard/gui/tree.jsp";

	public static String errNotExist = "Cette Categorie N'est exit pas ";

	public static CharSequence leaf = "check(";

	public static String errBranchNotExist = "Cette Branch   N'est exit pas ";

	public static String planningPath = "custom/modules/plannings/info.jsp?";

	public static String nbsp = "&nbsp;";
	public static final String IMPORT_ADE_FXML = "/view/ImportADE.fxml";
	public static final String PopUpCR_FXML = "/view/PopUpCR.fxml";
	public static final String PICS_ADD_TAB = "pics/add.png";

	public static String errSaveFile = "Error when saving file : ";

	public static String errLoadFile = "Error when Loading file :";
	public static final String Attname = "class";
	public static final String Value = "treeline";
	public static final String ADDPOPUP_FXML = "/view/AddPopUp.fxml";

	public static final String FILTER_POPUP = "/view/FilterPopUp.fxml";

	public static final String LOGIN = "/view/Login.fxml";

	public static final String TACHE_LIST = "/view/taskList.fxml";

	public static final String FAV_POPUP = "/view/FavPopUp.fxml";

	public static final String ADDVERSION_POPUP = "/view/AddVersionForm.fxml";

	public static final String MAIN_SCREEN = "/view/MainScreen.fxml";

	public static final String getMethod = "GET";

	public static final String ISO = "ISO-8859-15";

	public static final String BOUNDS = "custom/modules/plannings/bounds.jsp?";

	public static final String PIANOWEEK = "custom/modules/plannings/pianoWeeks.jsp";

	public static final String CLOSE_ALL = "Fermer tout";

	public static final String CLOSE_RIGHT = "Fermer Les Onglets De  Droit";

	public static final String CLOSE = "Fermer";

	public static final String EDIT_SHORTCUT = "Modifier le raccourci";

	public static final String ADD_SHORTCUT = "Ajouter le raccourci";

	public static final String PICS_SHORTUCT = "pics/shortuct1.png";

	public static final String PICS_DELETE = "pics/delete_icon.png";

	public static final String PICS_EDIT = "pics/edit_icon.png";

	public static final String PICS_ADD = "pics/add_icon.png";

	public static final String PICS_UNDO = "pics/undo_simple.png";

	public static final String PICS_REDO = "pics/redo_simple.png";

	public static final String PICS_OPEN = "pics/open_simple.png";

	public static final String PICS_OPEN_hover = "pics/open_hover.png";

	public static final String PICS_SAVE = "pics/save_simple.png";

	public static final String PICS_SAVE_hover = "pics/save_hover.png";
	
	public static final String PICS_VERSION = "pics/version_icon.png";

	public static final String PICS_DIFF = "pics/diff_icon.png";

	public final static String SHORTUCT_FILE = "Shortcuts.txt";
	public final static String Shortcut_FILE = "Shortcuts.txt";

	final public static String IMPORT_ADE_PopUp = "Importer � partir d'ADE";

	public static final String NAME = "Nom:";

	public static final String SAVE_SHORTCUT = "Enregistrer le raccourci";

	public static final String SHORTCUT = "Raccourci_";

	public static final String SAVE_FILE = "Save Time Table";
	
	public static final String SAVE_VERSION = "Save Versionning";

	public static final String REP_OPEN_FILECHOSER = ".";

	public static final String FORMAT_JSON = ".json";

	public static final String NEW_TAB = "Nouveau Tab ";

	public static final String NEW_TASKFXML = "/view/newTask.fxml";

	public static final String DATE_FORMAT = "EEEE, d MMMM yyyy, HH:mm:ss.SSS, zzz";
	
	public static final String PICS_LOGO = "/pics/logo.png";
	
	public static final String errOpenVer = "Votre fichier n'est pas du bon format.";
	
	public static final String errSaveVer = "Un probl�me est survenu lors de la sauvegarde.";
	
	public static final String errSelect = "Un probl�me est survenu lors de la s�lection d'une version.";
	
	public static final int SIZE_BTN = 20;
	
	public static final String EMPTY_TREE = "Aucune version enregistr�e";
	
	public static final String WRONG_TREE = "Votre arborescence ne correspond pas � cette branche";
}
