package drfn.piechart.adapter;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.widget.RelativeLayout;
import android.widget.TextView;

import drfn.piechart.extra.UiUtils;
import drfn.piechart.views.PieChartView;
import drfn.piechart.views.PieSliceDrawable;

public class PieChartAdapter extends BasePieChartAdapter {

	public final String TAG = this.getClass().getSimpleName();

	private Context mContext;
	//2015. 8. 5 파이차트 수정사항>>
	private List<String> m_arDatas;
	private List<Float> m_arPercents;
	private List<String> m_arNames;
	private List<Integer> m_arColors;
	private List<Integer> m_arColorIndexes;
	//2015. 8. 5 파이차트 수정사항<<

	public PieChartAdapter(Context context, List<String> datas, List<Float> percents, List<String> names, List<Integer> colors, List<Integer> colorIndexes) {	//2015. 8. 5 파이차트 수정사항 : names 추가
		init(context, datas, percents, names, colors, colorIndexes);
	}

	@Override
	public int getCount() {
		return m_arDatas.size();
	}

	@Override
	public Object getItem(int position) {
		return m_arDatas.get(position);		//파이차트의 각 가격을 알아올때
	}

	private void init(Context context, List<String> datas, List<Float> percents, List<String> names, List<Integer> colors, List<Integer> colorIndexes) {	//2015. 8. 5 파이차트 수정사항 : names 추가

		mContext = context;

		//2015. 8. 5 파이차트 수정사항 : data, percent, names 추가
		m_arDatas = datas;
		m_arPercents = percents;
		m_arNames = names;
		m_arColors = colors;
		m_arColorIndexes = colorIndexes;
	}

	@Override
	public float getPercent(int position) {
		Float percent = m_arPercents.get(position);	//2015. 8. 5 파이차트 수정사항 : percent 가 원데이터였는데, 가격으로 바꿔서 새로 만든 퍼센트 array 사용

		return percent;
	}

	//2015. 8. 5 파이차트 수정사항>>
	public String getName(int position) {
		return m_arNames.get(position);
	}
	//2015. 8. 5 파이차트 수정사항<<

	@Override
	public PieSliceDrawable getSlice(PieChartView parent, PieSliceDrawable convertDrawable, int position, float offset) {

		PieSliceDrawable sliceView = convertDrawable;

		if (sliceView == null) {
			sliceView = new PieSliceDrawable(parent, mContext);
		}

//		sliceView.setSliceColor(UiUtils.getRandomColor(mContext, position));
		if(m_arColorIndexes != null && m_arColorIndexes.size() > position) {
			sliceView.setSliceColor(m_arColors.get(m_arColorIndexes.get(position)));
		}
		else
			sliceView.setSliceColor(m_arColors.get(position));

		sliceView.setPercent(m_arPercents.get(position));	//2015. 8. 5 파이차트 수정사항
		sliceView.setDegreeOffset(offset);

//		System.out.println("Debug_offset:"+offset);

		//2015. 8. 5 파이차트 수정사항>>
		sliceView.setName(m_arNames.get(position));
		try {
			sliceView.setPrice(this.format(Double.parseDouble(m_arDatas.get(position)), 2, 3));
		} catch (Exception e) {

		}
		//2015. 8. 5 파이차트 수정사항<<

//		TextView textView = new TextView(parent.getContext());
//		textView.setTextColor(Color.WHITE);
//		textView.setTextSize(20.0f);
//		textView.setLayoutParams(new RelativeLayout.LayoutParams((int)UiUtils.getDynamicPixels(parent.getContext(), 40), (int)UiUtils.getDynamicPixels(parent.getContext(), 40)));
//		
		return sliceView;
	}

	//세자리마다 콤마를 찍어주고 fl길이만큼 소수 표시
	private DecimalFormat df = null;       // 세자리 마다 ','를 추가
	public String format(double value, int fl, int comma) {
		value += 0.000000001;
		if(df == null)
		{
			df = new DecimalFormat();       // 세자리 마다 ','를 추가
			df.setGroupingSize(comma);
			df.setGroupingUsed(true);
			DecimalFormatSymbols symbol = new DecimalFormatSymbols();
			symbol.setGroupingSeparator(',');
			df.setDecimalFormatSymbols(symbol);
		}
		df.setMinimumFractionDigits(fl);
		df.setMaximumFractionDigits(fl);

		return df.format(value);
	}
}
