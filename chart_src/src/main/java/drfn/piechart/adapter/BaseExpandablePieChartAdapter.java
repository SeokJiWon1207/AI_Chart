package drfn.piechart.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import drfn.piechart.views.CenterCircleInfoDrawable;
import drfn.piechart.views.PieChartView;
import drfn.piechart.views.PieSliceDrawable;

public abstract class BaseExpandablePieChartAdapter extends BaseExpandableListAdapter {

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		throw new RuntimeException("No child view required");
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		throw new RuntimeException("No group view required");
	}
	
	public abstract PieSliceDrawable getChildSlice(PieChartView parent, PieSliceDrawable convertDrawable, int groupPosition, int childPosition, float offset);
	public abstract PieSliceDrawable getGroupSlice(PieChartView parent, PieSliceDrawable convertDrawable, int groupPosition, float offset);

	public abstract void configureGroupInfo(CenterCircleInfoDrawable info, PieSliceDrawable slice, int groupPosition);
	public abstract void configureChildInfo(CenterCircleInfoDrawable info, PieSliceDrawable slice, int groupPosition, int childPosition);
	
	public abstract float getChildAmount(int groupPosition, int childPosition);
	public abstract float getGroupAmount(int groupPosition);
	
	public abstract int getChildColor(int groupPosition, int childPosition);
	public abstract int getGroupColor(int groupPosition);
}
