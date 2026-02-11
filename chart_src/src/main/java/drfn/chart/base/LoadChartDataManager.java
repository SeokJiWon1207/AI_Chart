package drfn.chart.base;

import java.util.ArrayList;

public class LoadChartDataManager {
	public static ArrayList<LoadCellItem> s_userItems = new ArrayList<LoadCellItem>();
	public static int chartHeight;
	
	public static ArrayList<LoadCellItem> getUserItems() {
        return s_userItems;        
    }
	
	public static int getHeight() {
		return chartHeight;
	}	
}