package drfn.chart.base;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

import drfn.chart.block.Block;
import drfn.chart.draw.DrawTool;
import drfn.chart.graph.AbstractGraph;
import drfn.chart.util.COMUtil;

public class JipyoListViewByLongTouch extends View {

	RelativeLayout jipyoListLayout;
	RelativeLayout layout;
	View jipyoListView = null;
	Context context = null;
	ListView lvJipyo = null;
	ArrayList<JipyoChoiceItem> m_itemsType, m_itemsOverlay, m_itemsJipyo, m_itemsResult;
	ArrayList<String> m_itemsType_type, m_itemsOverlay_type, m_itemsJipyo_type, m_itemsResult_type;

	public JipyoListViewByLongTouch(Context context, RelativeLayout layout) {
		super(context);

		this.context = context;
		this.layout = layout;

		int resId = context.getResources().getIdentifier("chart_jipyolistbylongtouch", "layout", context.getPackageName());

		jipyoListLayout = new RelativeLayout(context);

		//2018.06.28 by sdm >> 지표설정 화면 디자인 변경 Start
//		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams((int)COMUtil.getPixel(296), (int)COMUtil.getPixel(473));
		//RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		LayoutParams params = new LayoutParams((int)COMUtil.getPixel_W(320), (int)COMUtil.getPixel_H(450));

//		Configuration config = COMUtil.apiView.getContext().getResources().getConfiguration();
//		if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//
//			params.leftMargin = (int) COMUtil.getPixel(120);
//			params.rightMargin = (int) COMUtil.getPixel(120);
////			params.topMargin = (int) COMUtil.getPixel(5);
////			params.bottomMargin = (int) COMUtil.getPixel(5);
//		} else {
//			params.leftMargin = (int) COMUtil.getPixel(32);
//			params.rightMargin = (int) COMUtil.getPixel(32);
////			params.topMargin = (int) COMUtil.getPixel(84);
////			params.bottomMargin = (int) COMUtil.getPixel(84);
//		}
//		//2018.06.28 by sdm >> 지표설정 화면 디자인 변경 End
		jipyoListLayout.setLayoutParams(params);

		LayoutInflater factory = LayoutInflater.from(context);
		//2013. 8. 2 롱터치 지표리스트 새로운 UI 적용>>
//		jipyoListView = (RelativeLayout) factory.inflate(resId, null);
		jipyoListView = (LinearLayout) factory.inflate(resId, null);
		//2013. 8. 2 롱터치 지표리스트 새로운 UI 적용>>
//		jipyoListView.setLayoutParams(params);	//2018.06.28 by sdm >> 지표설정 화면 디자인 변경
		jipyoListView.setTag("baseline");
		initJipyoList();

		jipyoListLayout.addView(jipyoListView);
		layout.addView(jipyoListLayout);

		COMUtil.setGlobalFont(layout);
	}

	private void initJipyoList()
	{
		if(lvJipyo == null)
		{
			int layoutResId = context.getResources().getIdentifier("jipyoList", "id", context.getPackageName());
			lvJipyo = (ListView)jipyoListView.findViewById(layoutResId);

			m_itemsType_type = new ArrayList<String>();
			m_itemsOverlay_type = new ArrayList<String>();
			m_itemsJipyo_type = new ArrayList<String>();
			m_itemsResult_type = new ArrayList<String>();

			m_itemsType = FindDataManager.getUserChartTypeItem();
			makeItemsChartType();

			m_itemsOverlay = FindDataManager.getUserJipyoItems2();
			makeItems("overlay");

			m_itemsJipyo = FindDataManager.getUserJipyoItems();
			makeItems("jipyo");

			m_itemsResult = new ArrayList<JipyoChoiceItem>();
			m_itemsResult.addAll(m_itemsType);
			m_itemsResult.addAll(m_itemsOverlay);
			m_itemsResult.addAll(m_itemsJipyo);

			m_itemsResult_type.addAll(m_itemsType_type);
			m_itemsResult_type.addAll(m_itemsOverlay_type);
			m_itemsResult_type.addAll(m_itemsJipyo_type);

			JipyoListAdapter Adapter = new JipyoListAdapter(context, m_itemsResult, m_itemsResult_type);

			lvJipyo.setAdapter(Adapter);
		}

	}

	public void reload() {
		lvJipyo = null;
		initJipyoList();
	}

	private void makeItemsChartType() {
//		for(int i=0; i<COMUtil.getChartListTag().size(); i++) {
//			Vector<Hashtable<String, String>> v = COMUtil.getChartListTag();
//			Hashtable<String, String> item = (Hashtable<String, String>)v.get(i);
//			if(tag.equals(item.get("tag"))) {
//				rtnVal.add(item.get("name"));
//				rtnVal.add(item.get("type"));
//	//			rtnVal = item;
//				break;
//			}
//		}
		//2018.05.28 by sdm >> 설정상세 수정(이동 버튼, 차트유형 설정) Start
//		JipyoChoiceItem item1 = new JipyoChoiceItem("20001", "캔들", true);
		String jipyoItemName = "캔들";
		String jipyoItemTag = "20001";

		for(int i=0; i<COMUtil.getChartListTag().size(); i++) {
			Hashtable<String, String> item = (Hashtable<String, String>)COMUtil.getChartListTag().get(i);

			boolean chkState=false;
			if(getChartTypeName().equals(item.get("name"))) {
				jipyoItemName = item.get("name");
				jipyoItemTag = item.get("tag");
				break;
			}
		}

		JipyoChoiceItem item1 = new JipyoChoiceItem(jipyoItemTag, jipyoItemName, true);		// 보여줄 선택된 차트유형 매핑
		//2018.05.28 by sdm >> 설정상세 수정(이동 버튼, 차트유형 설정) End
		if(m_itemsType!=null) m_itemsType.clear();
		m_itemsType.add(item1);
		if(m_itemsType_type!=null) m_itemsType_type.clear();
		m_itemsType_type.add("차트유형");
	}

	private void makeItems(String type) {
//		String currentType = type;
		Vector<String> list = COMUtil._mainFrame.mainBase.baseP._chart.getGraphList();

		//2015. 1. 13 by lyk - 동일지표 추가 (추가리스트 적용)
		//Vector<Hashtable<String, String>> v = COMUtil.getJipyoMenu();
		Vector<Hashtable<String, String>>  v = (Vector<Hashtable<String, String>>)COMUtil.getJipyoMenu().clone();
		Vector<Hashtable<String, String>> addItems = COMUtil.getAddJipyoList();
		for(int i=0; i<addItems.size(); i++) {
			v.add(addItems.get(i));
		}
		//2015. 1. 13 by lyk - 동일지표 추가 (추가리스트 적용) end

		int jCnt = v.size();
		int nIndiCnt = jCnt - 9;
		int inx = 0;
		int cnt = 0;
		if(type.equals("overlay")) {
			if(m_itemsOverlay!=null) m_itemsOverlay.clear();
			inx = nIndiCnt;
			cnt = jCnt;
		} else if(type.equals("jipyo")) {
			if(m_itemsJipyo!=null) m_itemsJipyo.clear();
			inx = 0;
			cnt = jCnt-9;
		} else {
			if(m_itemsJipyo!=null) m_itemsJipyo.clear();
			inx = 0;
			cnt = jCnt;
		}
		for(int i=inx; i<cnt; i++) {
			Hashtable<String, String> item = (Hashtable<String, String>)v.get(i);
			boolean chkState=false;;
			int listLen = list.size();
			for(int k=0; k<listLen; k++) {
				String cmp = (String)item.get("name");
				chkState=false;
				if(cmp.equals((String)list.get(k))) {
					chkState = true;
					break;
				}
			}


			JipyoChoiceItem item1 = new JipyoChoiceItem((String)item.get("tag"), (String)item.get("name"), chkState);

			if(type.equals("overlay")) {
				if(chkState)
				{
					m_itemsOverlay.add(item1);
					m_itemsOverlay_type.add("오버레이");
				}
			} else {
				if(chkState)
				{
					m_itemsJipyo.add(item1);
					m_itemsJipyo_type.add("보조지표");
				}
			}
		}
	}

	public void showDetailView(View v, String strCase, String tag) {
		int position = v.getId();
		ArrayList<JipyoChoiceItem> datas = null;
		//2012. 8. 6 오버레이 / 보조지표가 isShown 되는 것에 따라서 구별하면  datas 의 값이 잘못 들어갈 수도 있어서 구분자 파라미터인 String strCase 로 구분처리
//		if(jipyolist.isShown()) {
//			datas = m_itemsArr;
//		} else if(overlaylist.isShown()){
//			datas = m_itemsArr2;
//		} else {
//			return;
//		}

//		if(strCase.equals("보조지표"))
//		{
//			datas = m_itemsJipyo;
//		}
//		else
//		{
//			datas = m_itemsOverlay;
//		}
//
		datas = m_itemsResult;
		JipyoChoiceItem oneItem = datas.get(position);

//		String textTochange = "지표상세 설정화면이동";
//    	COMUtil.showMessage(this.getContext(), textTochange); //Context, String msg

		RelativeLayout simpleLayout = new RelativeLayout(COMUtil._mainFrame.getContext());

		DetailJipyoController view = new DetailJipyoController(context,simpleLayout);
		view.setParent(this);

		//2013. 6. 10 (미래에셋) 태블릿 차트화면 설정버튼 눌러서 상세설정 팝업창 띄울 죽던 현상 수정 >>
		if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
		{
			simpleLayout.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

			//전체길이 - (왼쪽마진 + 오른쪽마진) = Width
			COMUtil._neoChart.jipyoListDetailPopup = new PopupWindow(simpleLayout, (int)COMUtil.getPixel(326), (int)COMUtil.getPixel(544), true);
			COMUtil._neoChart.jipyoListDetailPopup.setOutsideTouchable(true); // 이부분을 설정해주어야 팝업이 떳을때 다른부분에 이벤트를 줄수있습니다.
			COMUtil._neoChart.jipyoListDetailPopup.setBackgroundDrawable(new BitmapDrawable());  // 이부분에 이벤트가 들어오게됩니다.
			COMUtil._neoChart.jipyoListDetailPopup.showAtLocation(COMUtil.apiView.getRootView(), Gravity.NO_GRAVITY, (int)COMUtil.getPixel(924), (int)COMUtil.getPixel(91));
		}
		else
		{
			COMUtil._neoChart.jipyoListDetailPopup = new PopupWindow(simpleLayout, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
			COMUtil._neoChart.jipyoListDetailPopup.setOutsideTouchable(true); // 이부분을 설정해주어야 팝업이 떳을때 다른부분에 이벤트를 줄수있습니다.
			COMUtil._neoChart.jipyoListDetailPopup.setBackgroundDrawable(new BitmapDrawable());  // 이부분에 이벤트가 들어오게됩니다.
			COMUtil._neoChart.jipyoListDetailPopup.showAtLocation(COMUtil.apiView.getRootView(), Gravity.NO_GRAVITY, 0, 0);
		}
		//2013. 6. 10 (미래에셋) 태블릿 차트화면 설정버튼 눌러서 상세설정 팝업창 띄울 죽던 현상 수정 <<

//    	String tag = (String)v.getTag();
		view.isPopupState = true;
		view.setTitle(tag);
		view.setUI();
		if(strCase.equals("캔들"))
		{
			view.setInitGraph("일본식봉");
		}
		else
		{
			view.setInitGraph(oneItem.getName());
		}

		//2012. 7. 19  DetailJipyoView 의 setUI 만 실행되도 상세설정창이 뜨는데 불필요하게 DetailJipyoView가 addView 하는 것 수정
//    	RelativeLayout.LayoutParams params =new RelativeLayout.LayoutParams(
//        		LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
//		params.leftMargin=0;
//		params.topMargin=0;
//		view.setLayoutParams(params);
//    	this.layout.addView(view);

	}

	class JipyoListAdapter extends BaseAdapter {

		// 생성자 내부에서 초기화
		private Context context;
		private ViewWrapper wrapper = null;
		ArrayList<JipyoChoiceItem> items = new ArrayList<JipyoChoiceItem>();
		ArrayList<String> m_itemsResult_type;

		// 생성자
		JipyoListAdapter(Context context, ArrayList<JipyoChoiceItem> inputItems, ArrayList<String> _m_itemsResult_type) {
			items = inputItems;
			m_itemsResult_type = _m_itemsResult_type;
			// instance 변수(this.context)를 생성자 호출시 전달받은 지역 변수(context)로 초기화.
			this.context = context;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return items.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return items.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		// ListView에서 각 행(row)을 화면에 표시하기 전 호출됨.

		public View getView(final int position, View convertView, ViewGroup parent){
			View row = convertView;
//	        try {
//			if(row == null) {
			// LayoutInflater의 객체 inflater를 현재 context와 연결된 inflater로 초기화.
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();

			row = (View)inflater.inflate(context.getResources().getIdentifier("jipyolistbylongtouch_celltype_b", "layout", context.getPackageName()), null);

			wrapper = new ViewWrapper(row);
			row.setTag(wrapper);
//			}
//			else {
//				wrapper = (ViewWrapper)row.getTag();
//			}

			//2018.06.28 by sdm >> 지표설정 화면 디자인 변경 Start
			if(position == 1) {
				wrapper.getSubJipyoTitle().setVisibility(View.VISIBLE);
			} else {
				wrapper.getSubJipyoTitle().setVisibility(View.GONE);
			}

			final JipyoChoiceItem oneItem = items.get(position);
//			wrapper.getJipyoName().setText(oneItem.getName());
//			wrapper.getJipyoType().setText("["+m_itemsResult_type.get(position)+"]");

			AbstractGraph graph = COMUtil._neoChart.getGraph(oneItem.getName());
			//2014. 6. 3 설정상세의 캔들 누르면 맨마지막 지표가 상세창으로 뜨는 현상>>

			//2018.05.28 by sdm >> 설정상세 수정(이동 버튼, 차트유형 설정) - 차트유형일 경우에도 graph 받게 변경 Start
			//if(oneItem.getName().equals("캔들"))
			String itemType = m_itemsResult_type.get(position);
			if(itemType.equals("차트유형")) {
				graph = COMUtil._neoChart.getGraph("일본식봉");
				wrapper.getJipyoName().setText("차트유형");
				wrapper.getJipyoName().setTextColor(Color.rgb(92, 96, 136));
			} else {
				wrapper.getJipyoName().setText(oneItem.getName());
				wrapper.getJipyoName().setTextColor(Color.rgb(120, 120, 120));

			}

			if(graph==null) {
				return row;
			}//2014. 6. 3 설정상세의 캔들 누르면 맨마지막 지표가 상세창으로 뜨는 현상<<
			String strValueOfJipyoName = "";

			int[] interval = graph.interval;
			String[] s_interval = graph.s_interval;

			int count = 0;
			if(interval != null && s_interval != null)
			{
				int nRoofCnt;

				if(graph.getGraphTitle().equals("주가이동평균"))
				{
					nRoofCnt = graph.getDrawTool().size();

					for(int i = 0; i < nRoofCnt; i++)
					{
						DrawTool dt = (DrawTool)graph.getDrawTool().get(i);
						if(dt.isVisible()) {
							if(count >= 5) {
								break;
							} else if(count != 0) {
								strValueOfJipyoName += "\n";
							}

							strValueOfJipyoName += s_interval[i] + " [" + String.valueOf(interval[i]) + "] ";
							count++;
						}
					}
				}
				else
				{
					nRoofCnt = interval.length;

					for(int i = 0; i < nRoofCnt; i++)
					{
						//2017.07.24 by LYH 설정상세 지표값 소수점 처리 >>
						String graphTitle = graph.getGraphTitle();
						//2019.08.20 by LYH >> Parabolic 소수점 3자리 처리 Start
//						if( 	(graphTitle.equals("Parabolic SAR") && (0 == i || 1 == i)) ||
						if( 	(graphTitle.equals("Parabolic SAR") && (0 == i || 1 == i)))
						{
							strValueOfJipyoName += s_interval[i]+"["+String.format("%.3f", (float)graph.interval[i]/100)+"] ";
						}
						else if (
							//2019.08.20 by LYH >> Parabolic 소수점 3자리 처리 End
							//2017.05.11 by LYH << 전략(신호, 강약) 추가
								(graphTitle.equals("Parabolic신호") && (0 == i || 1 == i)) ||
										(graphTitle.equals("Parabolic강세약세") && (0 == i || 1 == i)) ||
										//2017.05.11 by LYH << 전략(신호, 강약) 추가 end
										(graphTitle.equals("Bollinger Band") && 1 == i) ||
										(graphTitle.equals("Envelope") && (0!=i)))	//2017.11 by pjm Envelope 소수점 처리 >>
						{
							if(count >= 5) {
								break;
							} else if(count != 0) {
								strValueOfJipyoName += "\n";
							}

							strValueOfJipyoName += s_interval[i]+" ["+String.format("%.2f", (float)interval[i]/100)+"] ";
							count++;
						}
						//2017.07.24 by LYH 설정상세 지표값 소수점 처리 <<
						else {
							if(count >= 5) {
								break;
							} else if(count != 0) {
								strValueOfJipyoName += "\n";
							}

							strValueOfJipyoName += s_interval[i] + " [" + String.valueOf(interval[i]) + "] ";
							count++;
						}
					}
				}
			}

			if(graph.getGraphTitle().equals("거래량")) {
				ArrayList<JipyoChoiceItem> volumeList = new ArrayList<JipyoChoiceItem>();
				volumeList.add(new JipyoChoiceItem("0", "일반", false));
				volumeList.add(new JipyoChoiceItem("3", "상승/하락(거래량)", true));
				volumeList.add(new JipyoChoiceItem("4", "캔들색과 같이", false));
				volumeList.add(new JipyoChoiceItem("1", "전일종가대비 상승/하락", false));
				volumeList.add(new JipyoChoiceItem("2", "전일고가대비 상승/하락", false));

				int nTag = graph._cvm.getVolDrawType();
				for (int i = 0; i < volumeList.size(); i++) {
					JipyoChoiceItem item = volumeList.get(i);
					if (Integer.parseInt(item.getTag()) == nTag) {
						strValueOfJipyoName = item.getName();
						break;
					}
				}
			}

			if(itemType.equals("차트유형")) {
				wrapper.getValueName().setText(oneItem.getName());

				LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) wrapper.getJipyoName().getLayoutParams();
				param.topMargin = (int)COMUtil.getPixel(4);
				wrapper.getJipyoName().setLayoutParams(param);

				LinearLayout.LayoutParams param2 = (LinearLayout.LayoutParams) wrapper.getDetailSetting().getLayoutParams();
				param2.topMargin = (int)COMUtil.getPixel(5);
				wrapper.getDetailSetting().setLayoutParams(param2);
			} else {
				wrapper.getValueName().setText(strValueOfJipyoName);
			}
			//2018.06.28 by sdm >> 지표설정 화면 디자인 변경 End

			row.setId(position);
			//	        String tag = null;
			//2018.05.28 by sdm >> 설정상세 수정(이동 버튼, 차트유형 설정) Start
			/*
				row.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//2014. 6. 3 설정상세의 캔들 누르면 맨마지막 지표가 상세창으로 뜨는 현상>>
					if(oneItem.getName().equals("캔들"))
					{
						showDetailView(v, "캔들", oneItem.getName());
					}
					else
					{
						showDetailView(v, m_itemsResult_type.get(position), oneItem.getName());
					}
					//2014. 6. 3 설정상세의 캔들 누르면 맨마지막 지표가 상세창으로 뜨는 현상<<
				}
			});
			*/

			// 편집 버튼 (차트유형에만 보이게)
			final Button btnMoveJipyoSetting = wrapper.getjipyoSetting();
			if (itemType.equals("차트유형")) {
				btnMoveJipyoSetting.setVisibility(View.VISIBLE);

				btnMoveJipyoSetting.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						showJipyoChartTypeSetting();
					}
				});
			} else {
				btnMoveJipyoSetting.setVisibility(View.GONE);
			}

			// 상세설정 버튼 (차트유형은 캔들과 역사계곡선만 보임)
			final Button btnMoveDetailSetting = wrapper.getDetailSetting();
			if(!itemType.equals("차트유형")
					|| (itemType.equals("차트유형") && oneItem.getName().equals("캔들"))
					|| (itemType.equals("차트유형") && oneItem.getName().equals("역시계곡선"))) {

				btnMoveDetailSetting.setVisibility(View.VISIBLE);

				btnMoveDetailSetting.setTag(row);
				btnMoveDetailSetting.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						View parent = (View) btnMoveDetailSetting.getTag();

						if (oneItem.getName().equals("캔들")) {
							showDetailView(parent, "캔들", oneItem.getName());
						} else if (oneItem.getName().equals("역시계곡선")) {
							showDetailView(parent, "역시계곡선", oneItem.getName());
						} else {
							showDetailView(parent, m_itemsResult_type.get(position), oneItem.getName());
						}
					}
				});
			} else {
				btnMoveDetailSetting.setVisibility(View.INVISIBLE);
			}
			//2018.05.28 by sdm >> 설정상세 수정(이동 버튼, 차트유형 설정) End
			//2013. 8. 2 롱터치 지표리스트 새로운 UI 적용>>
			//	        row.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, (int)COMUtil.getPixel(60)));
			row.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));        //2018.06.28 by sdm >> 지표설정 화면 디자인 변경
			//2013. 8. 2 롱터치 지표리스트 새로운 UI 적용>>
//	        } catch(Exception e) {
//
//	        }

			COMUtil.setGlobalFont((ViewGroup) row);

			return row;

		}
	}
	class ViewWrapper {
		private View base;
		private LinearLayout ctlSubJipyoTitle;      //2018.06.28 by sdm >> 지표설정 화면 디자인 변경
		private TextView  ctlJipyoName, ctlValueName, ctlJipyoType;
		private Button ctlJipyoSetting, ctlDetailSetting;

		ViewWrapper(View base) {
			this.base = base;
		}

		//2018.06.28 by sdm >> 지표설정 화면 디자인 변경 Start
		LinearLayout getSubJipyoTitle() {
			if(ctlJipyoName == null) {
				int layoutResId = this.base.getContext().getResources().getIdentifier("subjipyo_title", "id", this.base.getContext().getPackageName());
				ctlSubJipyoTitle = (LinearLayout)base.findViewById(layoutResId);
			}
			return ctlSubJipyoTitle;
		}
		//2018.06.28 by sdm >> 지표설정 화면 디자인 변경 End
		TextView getJipyoName() {
			if(ctlJipyoName == null) {
				int layoutResId = this.base.getContext().getResources().getIdentifier("jipyoname", "id", this.base.getContext().getPackageName());
				ctlJipyoName = (TextView)base.findViewById(layoutResId);
			}
			return ctlJipyoName;
		}

		TextView getValueName() {
			if(ctlValueName == null) {
				int layoutResId = this.base.getContext().getResources().getIdentifier("valuename", "id", this.base.getContext().getPackageName());
				ctlValueName = (TextView)base.findViewById(layoutResId);
			}
			return ctlValueName;
		}
		Button getjipyoSetting() {
			if(ctlJipyoSetting == null) {
				int layoutResId = this.base.getContext().getResources().getIdentifier("btn_move_jipyo_setting", "id", this.base.getContext().getPackageName());
				ctlJipyoSetting = (Button)base.findViewById(layoutResId);
			}
			return ctlJipyoSetting;
		}
		Button getDetailSetting() {
			if(ctlDetailSetting == null) {
				int layoutResId = this.base.getContext().getResources().getIdentifier("btn_move_detail", "id", this.base.getContext().getPackageName());
				ctlDetailSetting = (Button)base.findViewById(layoutResId);
			}
			return ctlDetailSetting;
		}
	}

	class JipyoItem
	{
		String jipyoName;
		String valueName;
		String jipyoType;
		public String getJipyoName() {
			return jipyoName;
		}
		public void setJipyoName(String jipyoName) {
			this.jipyoName = jipyoName;
		}
		public String getValueName() {
			return valueName;
		}
		public void setValueName(String valueName) {
			this.valueName = valueName;
		}
		public void setJipyoType(String jipyoType) {
			this.jipyoType = jipyoType;
		}
	}

	//2018.05.28 by sdm >> 설정상세 수정(이동 버튼, 차트유형 설정) Start
	// 지표설정 팝업 생성
	public void showJipyoChartTypeSetting() {
		Hashtable<String, Object> dic = new Hashtable<String, Object>();
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		params.leftMargin=0;
		params.topMargin=0;

		int triXpos=-1; //팝업창의 삼각형 이미지 위치.

		if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB)) {
			triXpos = 225;
		} else {
			Configuration config = COMUtil.apiView.getContext().getResources().getConfiguration();
			if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
				params =new RelativeLayout.LayoutParams(COMUtil.g_nDisWidth, COMUtil.g_nDisHeight);
			} else {
				params = new RelativeLayout.LayoutParams(COMUtil.g_nDisWidth, COMUtil.g_nDisHeight - (int) COMUtil.getPixel(25));
			}

			params.leftMargin=0;
			params.topMargin=0;
		}

		dic.put("tag", Integer.valueOf(COMUtil._TAG_INDICATOR_CONFIG));
		dic.put("triXpos", String.valueOf(triXpos));
		dic.put("open_status", "popup");		// 팝업형태로 열기 위한 flag
		dic.put("reload_target_view", this);	// 차트유형 설정 후 재갱신을 위한 객체 전달

		if(params!=null) dic.put("frame", params);
		if( COMUtil._mainFrame != null ) COMUtil._mainFrame.selectChartMenuFromParent(dic);
	}

	//2015. 1. 13 차트유형 추가>>
	/**
	 * 현재 차트가 어느 타입인지 문자열로 반환한다.
	 * @return  현재 차트타입  (ex: 캔들, 바, 봉, P&F...)
	 * */
	protected String getChartTypeName()
	{
		String strChart = null;
		boolean bIsStand = COMUtil._mainFrame.mainBase.baseP._chart._cvm.isStandGraph();
		if(bIsStand)
		{
			int btype = Block.STAND_BLOCK;
			Block stand = COMUtil._mainFrame.mainBase.baseP._chart.getChartBlockByType(btype);
			if(stand != null)
			{
				strChart = stand.getTitle();
			}
		}
		else
		{
			strChart = COMUtil._mainFrame.mainBase.baseP._chart.m_strCandleType;
		}

		//2014. 3. 27 차트 아무것도 조회 안되어 있을 때 설정 누르면 죽는 현상>>
		if(null == strChart)	return "캔들";
		else					return strChart;
		//2014. 3. 27 차트 아무것도 조회 안되어 있을 때 설정 누르면 죽는 현상<<
	}
	//2018.05.28 by sdm >> 설정상세 수정(이동 버튼, 차트유형 설정) End

	public void closePopupViews() {
//		if (detailJipyoView != null) {
//			detailJipyoView.closePopupViews();
//			detailJipyoView = null;
//		}
	}
}
