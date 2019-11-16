package modeles;

import java.util.ArrayList;
/**
 * une classe reprsente un état de notre systéme 
 */
public class State { 
	
	Version2 root;
	ArrayList<TimeTableV2> list;
	int TabNumber;
	public void setRoot(Version2 v2) {
		root = v2;
	}
	public Version2 getVersion2 () {
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
