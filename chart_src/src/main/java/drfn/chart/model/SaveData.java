package drfn.chart.model;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Vector;

public class SaveData implements Serializable {
	private Vector<Hashtable<String, String>> addList;
	public SaveData() {
		
	}
	
	public void setAddList(Vector<Hashtable<String, String>> item) {
		this.addList = item;
	}
	
	public Vector<Hashtable<String, String>> getAddList() {
		return this.addList;
	}
}
