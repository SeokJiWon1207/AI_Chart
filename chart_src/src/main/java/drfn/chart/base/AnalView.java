package drfn.chart.base;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
//import drfn.chart.R;
import drfn.chart.util.COMUtil;

public class AnalView extends View {

	RelativeLayout layout;
	ScrollView contentsView;
	RelativeLayout.LayoutParams params;
	Context context;
	public AnalView(Context context, RelativeLayout layout) {
		super(context);
		this.context = context;
		this.layout = layout;

		//background
		RelativeLayout rlBack = new RelativeLayout(context);
		rlBack.setId(400);
		rlBack.setFocusable(true);
		//rlBack.setBackgroundResource(R.drawable.configbg);
		params =new RelativeLayout.LayoutParams(
				(int)COMUtil.getPixel(60), COMUtil.chartHeight-(int)COMUtil.getPixel(COMUtil.mainTopMargin));
		params.leftMargin=COMUtil.chartWidth-(int)COMUtil.getPixel(55+4);
		params.topMargin=(int)COMUtil.getPixel(COMUtil.mainTopMargin);
		rlBack.setLayoutParams(params);
		this.layout.addView(rlBack);
	}

	public void setUI() {

		String[] menuItem = {"기준", "자동", "분할", "비교"};
		int[] menuItemIndex = {8001, 8000, 8002, 8003};
		//int contentSize=0;

		RelativeLayout.LayoutParams params;
		RelativeLayout rl = new RelativeLayout(context);
		rl.setFocusable(true);
		rl.setId(1802);
		rl.layout(0, 0, COMUtil.chartWidth, COMUtil.chartHeight);

		for(int i=0; i<menuItem.length; i++) {
			//기간표시(일,주,월...)
			Button button = new Button(context);
			button.setTag(""+menuItemIndex[i]);
			//button.setBackgroundResource(R.drawable.config);
			button.setTextColor(Color.WHITE);
			button.setTextSize(12);
			button.setText(menuItem[i]);
			params =new RelativeLayout.LayoutParams(
					(int)COMUtil.getPixel(47), (int)COMUtil.getPixel(32));
			params.leftMargin=COMUtil.chartWidth-(int)COMUtil.getPixel(55);
			params.topMargin=(int)COMUtil.getPixel(COMUtil.mainTopMargin+35*i+7);
			button.setLayoutParams(params);

			button.setOnClickListener(new OnClickListener(){
				public void onClick(View v) {
					COMUtil.showMessage(context, "준비중입니다.");
				}
			});

			rl.addView(button);
			//contentSize += params.height+4;
		}

		layout.addView(rl.getRootView());

	}
}
