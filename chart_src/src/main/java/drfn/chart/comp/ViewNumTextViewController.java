package drfn.chart.comp;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import drfn.chart.NeoChart2;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;
import drfn.chart.util.COMUtil;
import java.util.Hashtable;

/**
 * Y축(Yscale) 봉갯수 변경 창
 * @author dev_kimsh
 * @since  2014. 3. 21 (First develop)
 * */

public class ViewNumTextViewController extends Dialog implements OnClickListener{
	/** 변수 **/

	/**차트의 봉갯수(ViewNum)*/
	int m_nViewNum;
	/**전달받은 context*/
	Context m_context;
	/**전달받은 ChartViewModel*/
	ChartViewModel _cvm;
	/**전달받은 ChartDataModel*/
	ChartDataModel _cdm;
	/**전달받은 부모차트*/
	NeoChart2 parentChart;

	/** 컨트롤 **/

	/**입력창*/
	EditText m_Ed_ViewNumSet;
	/**확인 버튼*/
	Button m_Btn_SendRequest;
	/**이 창의 xml (viewnumtextviewcontroller)*/
	LinearLayout m_thisLayout;

	public ViewNumTextViewController(Context context, ChartViewModel _cvm, ChartDataModel _cdm, NeoChart2 parentChart) {
		// Dialog 배경을 투명 처리 해준다.
//        super(context , android.R.style.Theme_Translucent_NoTitleBar);
		super(context, context.getResources().getIdentifier("alert_layout", "style", context.getPackageName()));

		this.m_context = context;
		this._cvm = _cvm;
		this._cdm = _cdm;
		this.parentChart = parentChart;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		initDialog();
	}

	/**다이얼로그 초기화*/
	private void initDialog()
	{
		//차트 봉갯수 기본은 40 
		m_nViewNum = _cvm.getViewNum();

		//다이얼로그 레이아웃
		m_thisLayout =  (LinearLayout)LayoutInflater.from(m_context).inflate(m_context.getResources().getIdentifier("viewnumtextviewcontroller", "layout", m_context.getPackageName()), null);
		//xml 을 컨텐츠뷰로 설정
		if(null != m_thisLayout)
	/*		this.setContentView(m_thisLayout, new ViewGroup.LayoutParams((int)COMUtil.getPixel(220), (int) COMUtil.getPixel(205)));*/
			this.setContentView(m_thisLayout, new ViewGroup.LayoutParams((int)COMUtil.getPixel(160), (int) COMUtil.getPixel(182)));

		//EditText
		m_Ed_ViewNumSet = (EditText)m_thisLayout.findViewById(m_context.getResources().getIdentifier("ed_viewnumset", "id", m_context.getPackageName()));
		m_Ed_ViewNumSet.setText(String.valueOf(m_nViewNum));
		m_Ed_ViewNumSet.setOnClickListener(this);
		m_Ed_ViewNumSet.setFilters(new InputFilterMinMax[]{new InputFilterMinMax("1", "9999")});

		//Button 
		m_Btn_SendRequest = (Button)m_thisLayout.findViewById(m_context.getResources().getIdentifier("btn_sendrequest", "id", m_context.getPackageName()));
		m_Btn_SendRequest.setOnClickListener(this);

		//커스텀 폰트 적용
		COMUtil.setGlobalFont(m_thisLayout);
	}

	/**다이얼로그 크기를 설정
	 * @param nW : 너비
	 * @param nH : 높이
	 * */
	public void setDialogSize(int nW, int nH)
	{
		if(null != m_thisLayout)
		{
			this.setContentView(m_thisLayout, new ViewGroup.LayoutParams(nW, nH));
		}
	}

	/**컨트롤 클릭시*/
	@Override
	public void onClick(View v) {
		if(v.getId() == m_context.getResources().getIdentifier("btn_sendrequest", "id", m_context.getPackageName()))
		{
			if (m_Ed_ViewNumSet.getText().toString().equals("") || m_Ed_ViewNumSet.getText().toString().equals("0")) {
				m_Ed_ViewNumSet.setText("" + _cvm.MIN_VIEW_NUM);
			}
			else if (Integer.parseInt(m_Ed_ViewNumSet.getText().toString()) > _cvm.MAX_VIEW_NUM) {
				//1000개까지만 설정할 수 있다.
				m_Ed_ViewNumSet.setText("" + _cvm.MAX_VIEW_NUM);
				Toast.makeText(m_context, "한번에 조회 가능한 숫자는 " + _cvm.MAX_VIEW_NUM + "까지입니다.", Toast.LENGTH_SHORT).show();
			}

			m_nViewNum = Integer.parseInt(m_Ed_ViewNumSet.getText().toString());

			//2019. 07. 30 by hyh - 최대 개수 이상 확대 불가 처리 >>
			if (m_nViewNum < _cvm.MIN_VIEW_NUM) {
				m_nViewNum = _cvm.MIN_VIEW_NUM;
			}

			if (m_nViewNum > _cvm.MAX_VIEW_NUM) {
				m_nViewNum = _cvm.MAX_VIEW_NUM;
			}
			//2019. 07. 30 by hyh - 최대 개수 이상 확대 불가 처리 <<

			//현재 데이터 갯수보다 입력값이 크면 추가 조회하고 보여준다.
			if(m_nViewNum > _cdm.getCount())
			{
				//m_nViewNum = _cdm.getCount();
				Hashtable<String, Object> dic = new Hashtable<String, Object>();
				dic.put("viewnum", String.valueOf(m_nViewNum));
				if(parentChart.userProtocol!=null) parentChart.userProtocol.requestInfo(COMUtil._TAG_REQUESTADD_BYNUMBER, dic);
			}

			//입력받은 봉갯수를 차트에(정확히는 차트의 ChartViewModel) 설정한다. 
			_cvm.setViewNum(m_nViewNum);

			//차트의 스크롤 인덱스를 설정한다. 
			int nChartScrollIndex = _cdm.getCount() - m_nViewNum;
			if(nChartScrollIndex < 0)	nChartScrollIndex = 0;
			_cvm.setIndex(nChartScrollIndex);

			//변경된 봉갯수와 인덱스 정보를 이용해서 차트를 다시 그린다. 
			parentChart.repaintAll();

			//창을 닫는다 
			this.cancel();
		}
		else
		{
			//입력창을 터치하면 전체선택
			m_Ed_ViewNumSet.selectAll();
//			System.out.println("ViewNumTextViewController.OnClick()");
		}
	}


}
