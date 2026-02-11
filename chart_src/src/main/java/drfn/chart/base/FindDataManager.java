package drfn.chart.base;

import java.util.ArrayList;

public class FindDataManager {
	public static ArrayList<JipyoChoiceItem> s_userItems = new ArrayList<JipyoChoiceItem>();
	public static ArrayList<JipyoChoiceItem> s_userItems2 = new ArrayList<JipyoChoiceItem>();
	public static ArrayList<MinSettingItem> s_userItems3 = new ArrayList<MinSettingItem>();
	public static ArrayList<JipyoChoiceItem> s_userItems4 = new ArrayList<JipyoChoiceItem>();
	public static ArrayList<JipyoChoiceItem> s_userItems5 = new ArrayList<JipyoChoiceItem>();
	public static ArrayList<JipyoChoiceItem> s_userItems6 = new ArrayList<JipyoChoiceItem>();
	public static int chartHeight;
	
	public static ArrayList<JipyoChoiceItem> getUserJipyoItems() {
        return s_userItems;        
    }
	
	public static ArrayList<JipyoChoiceItem> getUserJipyoItems2() {
        return s_userItems2;        
    }
	public static ArrayList<JipyoChoiceItem> getUserJipyoItems3() {
        return s_userItems4;        
    }

	public static ArrayList<JipyoChoiceItem> getUserJipyoItems4() {
        return s_userItems5;        
    }
	public static ArrayList<JipyoChoiceItem> getUserJipyoItems5() {
		return s_userItems6;
	}

	
	public static ArrayList<MinSettingItem> getUserMinSettingItems() {
        return s_userItems3;        
    }

	public static ArrayList<JipyoChoiceItem> getUserChartTypeItem() {
		return s_userItems4;
	}
	
	public static int getHeight() {
		return chartHeight;
	}	
}