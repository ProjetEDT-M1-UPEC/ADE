package modeles;

import java.util.ArrayList;

/**
 * une classe reprsente un état de notre systéme
 */
public class State {

	ArrayList<Version2> root;
	ArrayList<TimeTableV2> list;
	int TabNumber;

	public void setVersion2(Version2 v2) {
		root = new ArrayList<>();
		root.add(v2);
	}

	public Version2 getVersion2() {
		if (root == null || root.isEmpty())
			return null;
		return root.get(0);
	}

	public void setRoot(ArrayList<Version2> v2) {
		root = v2;
	}

	public ArrayList<Version2> getRoot() {
		return root;
	}

	public ArrayList<TimeTableV2> getList() {
		return list;
	}

	public void setList(ArrayList<TimeTableV2> list) {
		this.list = list;
	}

	public int getTabNumber() {
		return TabNumber;
	}

	public void setTabNumber(int tabNumber) {
		TabNumber = tabNumber;
	}

}
