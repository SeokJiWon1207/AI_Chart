package drfn.chart.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import drfn.piechart.views.HorizontalStackChartView;

/**
 * Pie Chart 용 Base 를 Stack형 차트로 변형
 * @author lyk of kfits
 * @since 2015. 8. 17
 * @version 1.0
 * */

//2020.04.13 가로 Stack형 차트 수정 - hjw
@SuppressLint("NewApi")
public class BaseHorizontalStackChart extends Base {
	private Context m_Context = null;

	private RelativeLayout m_Layout;
	private LinearLayout mStackChartLayout;
	private RelativeLayout.LayoutParams lpMain;

	private HorizontalStackChartView mChart = null;
	private List<String> m_sliceDatas = new ArrayList<String>();
	private List<Float> m_slicePercents = new ArrayList<Float>();
	private List<String> m_sliceNames = new ArrayList<String>();
	private List<Integer> m_sliceColors = new ArrayList<Integer>();

	private String alignType = "bottom";	//범례 타입 (top, bottom)
	private String valueType = "V";			//Value 타입 (V:Value형 펗센트로 변형해서 사용, P:퍼센트형 들어온 데이터 그대로씀)

	public BaseHorizontalStackChart(Context context , RelativeLayout layout) {
		super(context);

		m_Layout = layout;
		m_Context = context;

		//색상설정 API 예제
		List<Integer> sliceColors = new ArrayList<Integer>();
		sliceColors.add(Color.rgb(179, 71, 170));
		sliceColors.add(Color.rgb(231, 63, 24));
		sliceColors.add(Color.rgb(58, 48, 194));
		sliceColors.add(Color.rgb(186, 102, 69));
		sliceColors.add(Color.rgb(1, 97, 210));
		sliceColors.add(Color.rgb(203, 126, 0));
		sliceColors.add(Color.rgb(1, 122, 153));
		sliceColors.add(Color.rgb(255, 170, 36));
		sliceColors.add(Color.rgb(36, 181, 113));
		sliceColors.add(Color.rgb(237, 198, 2));
		sliceColors.add(Color.rgb(116, 179, 54));
		sliceColors.add(Color.rgb(175, 179, 61));
		sliceColors.add(Color.rgb(75, 160, 42));
		sliceColors.add(Color.rgb(51, 212, 201));
		sliceColors.add(Color.rgb(0, 168, 210));
		sliceColors.add(Color.rgb(30, 68, 150));
		sliceColors.add(Color.rgb(109, 78, 194));
		sliceColors.add(Color.rgb(64, 64, 64));
		setSliceColors(sliceColors);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

	@Override
	public void init() {
		super.init();

		createStackChart();
	}

	//2015. 8. 5 파이차트 수정사항>> : 파이차트 공개 API -> 데이터설정
	public void createStackChart()
	{
		LayoutInflater factory = LayoutInflater.from(m_Context.getApplicationContext());

		//2012. 8. 16 자동추세창 레이아웃 크기 및 위치 조절 : T_tab01
		RelativeLayout.LayoutParams lppieChartLayout = (RelativeLayout.LayoutParams)m_Layout.getLayoutParams();
		RelativeLayout.LayoutParams paramsChartLayout =new RelativeLayout.LayoutParams(
				lppieChartLayout.width, lppieChartLayout.height);
		paramsChartLayout.leftMargin=0;
		paramsChartLayout.topMargin=0;

		//Layout 설정
		mStackChartLayout = new LinearLayout(m_Context);
		mStackChartLayout.setLayoutParams(paramsChartLayout);

		lpMain = (RelativeLayout.LayoutParams)m_Layout.getLayoutParams();
		FrameLayout.LayoutParams params =new FrameLayout.LayoutParams(
				lpMain.width, lpMain.height);
		params.leftMargin=0;
		params.topMargin=0;

		mChart = new HorizontalStackChartView(m_Context);
		mChart.setLayoutParams(params);
		mChart.setBounds(0, 0, lpMain.width, lpMain.height);

		mChart.setParent(this);
		mChart.setDatas(m_sliceDatas, m_slicePercents, m_sliceNames, m_sliceColors);	//sdm tt

		mStackChartLayout.addView(mChart);
		this.m_Layout.addView(mStackChartLayout);
	}

	public void setSliceColors(List<Integer> colors)
	{
		m_sliceColors = colors;
		if(null == m_sliceColors || m_sliceColors.size() == 0)
		{
			m_sliceColors.add(Color.rgb(179, 71, 170));
			m_sliceColors.add(Color.rgb(231, 63, 24));
			m_sliceColors.add(Color.rgb(58, 48, 194));
			m_sliceColors.add(Color.rgb(186, 102, 69));
			m_sliceColors.add(Color.rgb(1, 97, 210));
			m_sliceColors.add(Color.rgb(203, 126, 0));
			m_sliceColors.add(Color.rgb(1, 122, 153));
			m_sliceColors.add(Color.rgb(255, 170, 36));
			m_sliceColors.add(Color.rgb(36, 181, 113));
			m_sliceColors.add(Color.rgb(237, 198, 2));
			m_sliceColors.add(Color.rgb(116, 179, 54));
			m_sliceColors.add(Color.rgb(175, 179, 61));
			m_sliceColors.add(Color.rgb(75, 160, 42));
			m_sliceColors.add(Color.rgb(51, 212, 201));
			m_sliceColors.add(Color.rgb(0, 168, 210));
			m_sliceColors.add(Color.rgb(30, 68, 150));
			m_sliceColors.add(Color.rgb(109, 78, 194));
			m_sliceColors.add(Color.rgb(64, 64, 64));
		}
	}

	public void setPacketData_hashtable(Hashtable<String, Object> data)
	{
		String strBackColor = (String)data.get("title");
		if(strBackColor!=null) {
			String[] strBackColors = strBackColor.split(",");
			mChart.setBackgroundColor(Color.rgb(Integer.parseInt(strBackColors[0]), Integer.parseInt(strBackColors[1]), Integer.parseInt(strBackColors[2])));
		}

		m_sliceDatas = (List<String>)data.get("sliceDatas");
		if(null == m_sliceDatas || m_sliceDatas.size() == 0) {
			m_sliceDatas.add("0");
		}

		//데이터를 퍼센트형으로 받을때 처리
		//Value 타입 (V:Value형 펗센트로 변형해서 사용, P:퍼센트형 들어온 데이터 그대로씀)
		if(data.containsKey("valueType")) {
			valueType = (String)data.get("valueType");
			mChart.setValueType(valueType);
		}

		float fTotal = 0;
		for(int i = 0; i < m_sliceDatas.size(); i++) {
			fTotal += Float.parseFloat(m_sliceDatas.get(i).trim());
		}

		//데이터 합이 0일 경우 처리
		if(fTotal==0.0) {
			mChart.clearDrawables();
			mChart.invalidate();
//			return;
		} else {
			//데이터 합이 0일 경우 처리 end

			//데이터를 퍼센트형으로 받을때 처리
			if (valueType.equals("P")) {
				m_slicePercents = new ArrayList<Float>();
				for (int i = 0; i < m_sliceDatas.size(); i++) {
					float fPercent = Float.parseFloat(m_sliceDatas.get(i).trim()) / (float) 100.0;
					m_slicePercents.add(fPercent);
				}
			} else {
				m_slicePercents = new ArrayList<Float>();
				for (int i = 0; i < m_sliceDatas.size(); i++) {
					float fPercent = Float.parseFloat(m_sliceDatas.get(i).trim()) / fTotal;
					m_slicePercents.add(fPercent);
				}
			}
		}

		m_sliceNames = (List<String>)data.get("sliceNames");
		if((null == m_sliceNames || m_sliceNames.size() == 0) && null == m_sliceDatas || m_sliceDatas.size() == 0) {
			m_sliceNames.add("데이터 입력 안됨");
			float fPercent = 100 / (float) 100.0;
			m_slicePercents.add(fPercent);
		} else if (m_sliceNames.get(0).equals("데이터 입력 안됨")) {
			float fPercent = 100 / (float) 100.0;
			m_slicePercents.add(fPercent);
		}

		String colorType = (String)data.get("colorType");
		int nColorType = 0;
		if(colorType!=null) {
			//2017.04.16 by LYH >> 파이차트 데이터가 하나도 없을 경우 처리
			if(m_sliceNames.size()==1 && m_sliceNames.get(0).trim().length() <1)
			{
				List<Integer> sliceColors = new ArrayList<Integer>();
				sliceColors.add(Color.rgb(179, 71, 170));
				setSliceColors(sliceColors);
			}
			//2017.04.16 by LYH << 파이차트 데이터가 하나도 없을 경우 처리
			else {
//				if (colorType.equals("1")) {
//					//색상설정 API 예제
//					List<Integer> sliceColors = new ArrayList<Integer>();
//					sliceColors.add(Color.rgb(179, 71, 170));
//					sliceColors.add(Color.rgb(231, 63, 24));
//					sliceColors.add(Color.rgb(58, 48, 194));
//					sliceColors.add(Color.rgb(186, 102, 69));
//					sliceColors.add(Color.rgb(1, 97, 210));
//					sliceColors.add(Color.rgb(203, 126, 0));
//					sliceColors.add(Color.rgb(1, 122, 153));
//					sliceColors.add(Color.rgb(255, 170, 36));
//					sliceColors.add(Color.rgb(36, 181, 113));
//					sliceColors.add(Color.rgb(237, 198, 2));
//					sliceColors.add(Color.rgb(116, 179, 54));
//					sliceColors.add(Color.rgb(175, 179, 61));
//					sliceColors.add(Color.rgb(75, 160, 42));
//					sliceColors.add(Color.rgb(51, 212, 201));
//					sliceColors.add(Color.rgb(0, 168, 210));
//					sliceColors.add(Color.rgb(30, 68, 150));
//					sliceColors.add(Color.rgb(109, 78, 194));
//					sliceColors.add(Color.rgb(64, 64, 64));
//					setSliceColors(sliceColors);
//				} else {
//					//색상설정 API 예제
//					List<Integer> sliceColors = new ArrayList<Integer>();
//					sliceColors.add(Color.rgb(179, 71, 170));
//					sliceColors.add(Color.rgb(231, 63, 24));
//					sliceColors.add(Color.rgb(58, 48, 194));
//					sliceColors.add(Color.rgb(186, 102, 69));
//					sliceColors.add(Color.rgb(1, 97, 210));
//					sliceColors.add(Color.rgb(203, 126, 0));
//					sliceColors.add(Color.rgb(1, 122, 153));
//					sliceColors.add(Color.rgb(255, 170, 36));
//					sliceColors.add(Color.rgb(36, 181, 113));
//					sliceColors.add(Color.rgb(237, 198, 2));
//					sliceColors.add(Color.rgb(116, 179, 54));
//					sliceColors.add(Color.rgb(175, 179, 61));
//					sliceColors.add(Color.rgb(75, 160, 42));
//					sliceColors.add(Color.rgb(51, 212, 201));
//					sliceColors.add(Color.rgb(0, 168, 210));
//					sliceColors.add(Color.rgb(30, 68, 150));
//					sliceColors.add(Color.rgb(109, 78, 194));
//					sliceColors.add(Color.rgb(64, 64, 64));
//					setSliceColors(sliceColors);
//				}
				if (colorType.equals("1")) { //계좌잔고(신탁형)
					List<Integer> sliceColors = new ArrayList<Integer>();
					//sliceColors.add(Color.rgb(224, 45, 35));
					sliceColors.add(Color.rgb(110, 185, 242));
					sliceColors.add(Color.rgb(255, 165, 236));
					sliceColors.add(Color.rgb(252, 199, 73));
					sliceColors.add(Color.rgb(137, 107, 215));
					sliceColors.add(Color.rgb(71, 199, 209));
					sliceColors.add(Color.rgb(139, 209, 136));

					sliceColors.add(Color.rgb(110, 185, 242));
					sliceColors.add(Color.rgb(255, 165, 236));
					sliceColors.add(Color.rgb(252, 199, 73));
					sliceColors.add(Color.rgb(137, 107, 215));
					sliceColors.add(Color.rgb(71, 199, 209));
					sliceColors.add(Color.rgb(139, 209, 136));

					setSliceColors(sliceColors);
				} else if (colorType.equals("2")) { //계좌잔고(일형)
					List<Integer> sliceColors = new ArrayList<Integer>();
					//sliceColors.add(Color.rgb(0, 120, 197));
					sliceColors.add(Color.rgb(255, 101, 101));
					sliceColors.add(Color.rgb(252, 163, 86));
					sliceColors.add(Color.rgb(252, 199, 73));
					sliceColors.add(Color.rgb(110, 185, 242));
					sliceColors.add(Color.rgb(71, 199, 209));
					sliceColors.add(Color.rgb(139, 209, 136));

					sliceColors.add(Color.rgb(255, 101, 101));
					sliceColors.add(Color.rgb(252, 163, 86));
					sliceColors.add(Color.rgb(252, 199, 73));
					sliceColors.add(Color.rgb(110, 185, 242));
					sliceColors.add(Color.rgb(71, 199, 209));
					sliceColors.add(Color.rgb(139, 209, 136));

					setSliceColors(sliceColors);
				} else if (colorType.equals("3")) {
					List<Integer> sliceColors = new ArrayList<Integer>();
//					sliceColors.add(Color.rgb(0, 120, 197));
//					sliceColors.add(Color.rgb(224, 45, 35));
					sliceColors.add(Color.rgb(51, 118, 191));
					sliceColors.add(Color.rgb(237, 79, 52));
					setSliceColors(sliceColors);
				}else {
					List<Integer> sliceColors = new ArrayList<Integer>();
					sliceColors.add(Color.rgb(0, 0, 0));
					sliceColors.add(Color.rgb(236, 236, 236));
					setSliceColors(sliceColors);
				}
			}

			try {
				nColorType = Integer.parseInt(colorType);
				mChart.setColorType(nColorType);
			} catch (Exception e) {
			}
		}

		//색상정보가 데이터 수보다 적을 경우 처리
		if(this.m_sliceColors.size()>0 && this.m_sliceColors.size() < m_sliceDatas.size()) {
			int diff = m_sliceDatas.size() - this.m_sliceColors.size();
			int colSize = this.m_sliceColors.size();
			for(int i=0; i<diff; i++) {
				m_sliceColors.add(this.m_sliceColors.get(i%colSize));
			}
		}
		//색상정보가 데이터 수보다 적을 경우 처리 end

		//범례 타입 (0=안보임, 1=하나씩 보여주는 범례, 2=색나열형 범례)
		if(data.containsKey("infoType")) {
			String infoType = (String) data.get("infoType");
			int nInfoType = 0;
			try {
				nInfoType = Integer.parseInt(infoType);
				mChart.setInfoType(nInfoType);
			} catch (Exception e) {
			}
		}

		//정렬
		if(data.containsKey("alignType")) {
			alignType = (String) data.get("alignType");
			mChart.setAlignType(alignType);
		}

		//데이터 입력
		if(mChart==null) {
			this.createStackChart();
		} else {
			try {
				mChart.setDatas(m_sliceDatas, m_slicePercents, m_sliceNames, m_sliceColors);
			} catch(Exception e) {
			}
		}
	}

	public void destroy() {
		mChart.setVisibility(View.GONE);
	}
}