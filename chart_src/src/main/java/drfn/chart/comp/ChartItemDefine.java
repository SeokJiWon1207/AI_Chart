package drfn.chart.comp;

class SignalItem {
	private String mstrStrName;
	private String mstrItemName;
	
	public SignalItem(String strTime, String strItemName){
		mstrStrName = strTime;
		mstrItemName = strItemName;
	}
	
	public String getStrTime() {
		return mstrStrName;
	}
	
	public String getItemName() {
		return mstrItemName;
	}
}