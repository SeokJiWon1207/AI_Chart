package drfn.chart.base;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

import drfn.chart.util.COMUtil;

public class IndicatorDefaultConfigView extends View implements OnClickListener{
	RelativeLayout layout=null;
	ArrayList<JipyoChoiceItem> m_itemsArr;
	MyArrayAdapter m_scvAdapter;
	ListView jipyolist = null;
	private ArrayList<Boolean> itemChecked = new ArrayList<Boolean>();

	private int layoutResId01;
	private int layoutResId02;
	private int layoutResId03;
	private int layoutResId04;
	private int layoutResId05;
	private int layoutResId06;
	private int layoutResId07;

	public IndicatorDefaultConfigView(Context context, final RelativeLayout layout) {
		super(context);
		this.layout = layout;

		LinearLayout indicatorLayout = new LinearLayout(context);
		indicatorLayout.setTag(COMUtil.INDICATORVIEWDEFAULT_LAYOUT);
		RelativeLayout.LayoutParams params =new RelativeLayout.LayoutParams(
				(int)COMUtil.getPixel(400), (int)COMUtil.getPixel(300));
		params.addRule(RelativeLayout.CENTER_IN_PARENT);
		indicatorLayout.setLayoutParams(params);

		LayoutInflater factory = LayoutInflater.from(context);
//		View parentView = (View)factory.inflate(R.layout.indicatorviewfordefault, null);
		int layoutResId = context.getResources().getIdentifier("indicatorviewfordefault", "layout", context.getPackageName());
		View parentView = (View)factory.inflate(layoutResId, null);
//		View parentView = this.getContext().getResources().getIdentifier("indicatorviewfordefault", "layout", context.getPackageName());
		//이미지 줄이기.(OutOfMemory 해결)
//		Drawable drawable = COMUtil.getSmallBitmap(R.drawable.popupbg1);
//		ll.setBackgroundDrawable(drawable);
		parentView.setTag("indicatorviewfordefaultView");
		indicatorLayout.addView(parentView);

		layout.addView(indicatorLayout);

		//테마 
		layoutResId01 = context.getResources().getIdentifier("bns_gbn_black_radio_btn", "id", context.getPackageName());
		View view = parentView.findViewById(layoutResId01);
		if (view != null)
		{
			view.setOnClickListener(this);
		}
//		view = parentView.findViewById(R.id.bns_gbn_white_radio_btn);
		layoutResId02 = context.getResources().getIdentifier("bns_gbn_white_radio_btn", "id", context.getPackageName());
		view = parentView.findViewById(layoutResId02);
		if (view != null)
		{
			view.setOnClickListener(this);
		}
		//차트유형 이벤트 등록
//		view = parentView.findViewById(R.id.Button01);
		layoutResId03 = context.getResources().getIdentifier("Button01", "id", context.getPackageName());
		view = parentView.findViewById(layoutResId03);
		if (view != null)
		{
			view.setOnClickListener(this);
		}
//		view = parentView.findViewById(R.id.Button02);
		layoutResId04 = context.getResources().getIdentifier("Button02", "id", context.getPackageName());
		view = parentView.findViewById(layoutResId04);
		if (view != null)
		{
			view.setOnClickListener(this);
		}
//		view = parentView.findViewById(R.id.Button03);
		layoutResId05 = context.getResources().getIdentifier("Button03", "id", context.getPackageName());
		view = parentView.findViewById(layoutResId05);
		if (view != null)
		{
			view.setOnClickListener(this);
		}
//		view = parentView.findViewById(R.id.Button04);
		layoutResId06 = context.getResources().getIdentifier("Button04", "id", context.getPackageName());
		view = parentView.findViewById(layoutResId06);
		if (view != null)
		{
			view.setOnClickListener(this);
		}
//		view = parentView.findViewById(R.id.Button05);
		layoutResId07 = context.getResources().getIdentifier("Button05", "id", context.getPackageName());
		view = parentView.findViewById(layoutResId07);
		if (view != null)
		{
			view.setOnClickListener(this);
		}

//		jipyolist = (ListView)parentView.findViewById(R.id.jipyoList);
		layoutResId = context.getResources().getIdentifier("jipyoList", "id", context.getPackageName());
		jipyolist = (ListView)parentView.findViewById(layoutResId);
		m_itemsArr = FindDataManager.getUserJipyoItems();

		makeItems("");

		m_scvAdapter = new MyArrayAdapter(context, m_itemsArr);
		jipyolist.setAdapter(m_scvAdapter);
		jipyolist.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
				onListItemClick(arg0,arg1,arg2,arg3);

			}

		});

//		RadioButton radioWhite = (RadioButton) parentView.findViewById(R.id.bns_gbn_white_radio_btn);
		layoutResId = context.getResources().getIdentifier("bns_gbn_white_radio_btn", "id", context.getPackageName());
		RadioButton radioWhite = (RadioButton)parentView.findViewById(layoutResId);
		if(COMUtil.getSkinType() != COMUtil.SKIN_BLACK)
			radioWhite.setChecked(true);

	}

	//ArrayAdapter에서 상속받는 커스텀 ArrayAdapter 정의.
	class MyArrayAdapter extends ArrayAdapter<JipyoChoiceItem> {

		// 생성자 내부에서 초기화
		private Context context;
		private ViewWrapper wrapper = null;
		private ArrayList<JipyoChoiceItem> mitems;
//	    private static final int gnSigChoiceViewCellTypeID = R.layout.jipyo_celltype_b;	///< 화면의 layout ID. 

		// 생성자
		MyArrayAdapter(Context context, ArrayList<JipyoChoiceItem> items) {
			super(context, context.getResources().getIdentifier("jipyo_celltype_b", "layout", context.getPackageName()), items);

			// instance 변수(this.context)를 생성자 호출시 전달받은 지역 변수(context)로 초기화.
			this.context = context;
			this.mitems = items;
//	        for (int i = 0; i < this.getCount(); i++) {
//	            itemChecked.add(i, false); // initializes all items value with false
//	        }
		}
		// ListView에서 각 행(row)을 화면에 표시하기 전 호출됨.

		public View getView(final int position, View convertView, ViewGroup parent){
			View row = convertView;

			if(row == null) {
				// LayoutInflater의 객체 inflater를 현재 context와 연결된 inflater로 초기화.
				LayoutInflater inflater = ((Activity)context).getLayoutInflater();

				// inflator객체를 이용하여 \res\laout\cellsigview.xml 파싱
				row = (View)inflater.inflate(context.getResources().getIdentifier("jipyo_celltype_b", "layout", context.getPackageName()), null);

				wrapper = new ViewWrapper(row);
				row.setTag(wrapper);
			}
			else {
				wrapper = (ViewWrapper)row.getTag();
			}

			JipyoChoiceItem oneItem = mitems.get(position);
			wrapper.getCtrlCodeName().setText(oneItem.getName());
			wrapper.getCtrlVisible().setTag(oneItem.getTag());
			wrapper.getCtrlVisible().setId(position);
//	        wrapper.getCtrlVisible().setChecked(oneItem.getCheck());
			wrapper.getCtrlVisible().setChecked(itemChecked.get(position));
			wrapper.getCtrlVisible().setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					CheckBox cb = (CheckBox)v;
					boolean isChecked = cb.isChecked();
					int pos = v.getId();
					if (isChecked) {
						itemChecked.set(pos, true);

					} else if (!isChecked) {
						itemChecked.set(pos, false);
						// do some operations here
					}
//		        	try {
					COMUtil.setJipyo(cb);
				}
			});

			wrapper.getCtrlDetail().setId(position);
			wrapper.getCtrlDetail().setTag(oneItem.getName());
			wrapper.getCtrlDetail().setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					//2012.7.3 체크안된 보조지표의 상세지표설정창 오픈 안되게 변경
					if(itemChecked.get(v.getId()))
					{
						showDetailView(v);
					}
				}
			});

			// 커스터마이징 된 View 리턴.
			return row;

		}
	}

	//
	// Holder Pattern을 구현하는 ViewWrapper 클래스
	//
	class ViewWrapper {
		private View base;
		private TextView  ctlCodeName;
		private ImageView ctlDetail;
		private CheckBox ctlVisible;

		ViewWrapper(View base) {
			this.base = base;
		}

		// 멤버 변수가 null일때만 findViewById를 호출
		// null이 아니면 저장된 instance 리턴 -> Overhaed 줄임
//		 TextView getCtrlCode() {
//		     if(ctlCode == null) {
//		    	 ctlCode = (TextView)base.findViewById(R.id.fctb_TextViewB01);
//		     }          
//		     return ctlCode;
//		 }

		TextView getCtrlCodeName() {
			if(ctlCodeName == null) {
				int layoutResId = this.base.getContext().getResources().getIdentifier("fctb_TextViewA01", "id", this.base.getContext().getPackageName());
				ctlCodeName = (TextView)base.findViewById(layoutResId);
			}
			return ctlCodeName;
		}

		CheckBox getCtrlVisible() {
			if(ctlVisible == null) {
				int layoutResId = this.base.getContext().getResources().getIdentifier("ausf_CheckBoxB01", "id", this.base.getContext().getPackageName());
				ctlVisible = (CheckBox)base.findViewById(layoutResId);
			}
			return ctlVisible;
		}

		ImageView getCtrlDetail() {
			if(ctlDetail == null) {
				int layoutResId = this.base.getContext().getResources().getIdentifier("fctb_ImageViewA01", "id", this.base.getContext().getPackageName());
				ctlDetail = (ImageView)base.findViewById(layoutResId);
			}
			return ctlDetail;
		}
	}
	public void showDetailView(View v) {
		int position = v.getId();
		JipyoChoiceItem oneItem = m_itemsArr.get(position);

		String textTochange = "지표상세 설정화면이동";
		COMUtil.showMessage(this.getContext(), textTochange); //Context, String msg

		DetailJipyoController view = new DetailJipyoController(this.getContext(), this.layout);

		String tag = (String)v.getTag();
		view.setTitle(tag);
		view.setUI();
		view.setInitGraph(oneItem.getName());
		RelativeLayout.LayoutParams params =new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		params.leftMargin=0;
		params.topMargin=0;
		view.setLayoutParams(params);
		this.layout.addView(view);

	}
	private void makeItems(String type) {
		Vector<String> list = COMUtil._neoChart.getGraphList();
		Vector<Hashtable<String, String>> v = COMUtil.getJipyoMenu();
		int jCnt = v.size();
//		int nIndiCnt = jCnt - 9;
		int inx = 0;
		int cnt = 0;
		if(type.equals("jipyo")) {
			if(m_itemsArr!=null) m_itemsArr.clear();
			inx = 0;
			cnt = jCnt-9;
		} else {
			if(m_itemsArr!=null) m_itemsArr.clear();
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
			itemChecked.add(chkState);
			m_itemsArr.add(item1);
		}
	}
	public void onListItemClick(AdapterView<?> lv, View v, int position, long id) {
		//implement here for click item.
//		View pv = lv.getChildAt(position);
//		CheckBox cBox = (CheckBox)pv.findViewById(position);
//		if(cBox==null) return;
//		if(cBox.isChecked()) {
//			cBox.setChecked(false);
//		} else {
//			cBox.setChecked(true);
//		}
//		
//		COMUtil._chartMain.setJipyo(cBox);
	}

	@Override
	public void onClick(View v)
	{

//		switch (v.getId())
//		{
//		
//			case layoutResId01:
//			case layoutResId02:
//			case layoutResId03:
//			case layoutResId04:
//			case layoutResId05:
//			case layoutResId06:
//			case layoutResId07:
//
//				COMUtil.setChartFromXML(v);
//			
//			break;
//		}

		COMUtil.setChartFromXML(v);
	}
}
