package drfn.chart.comp;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import drfn.chart.util.COMUtil;

public class TrendDelAlertDialog extends Dialog implements 	View.OnClickListener{

	private OnClickListener m_listenerYes = null;
	private OnClickListener m_listenerNo = null;
	private OnClickListener m_listenerOK = null;

	CheckBox[] btnChk = new CheckBox[2];
	TextView[] tvTitle = new TextView[2];

	LinearLayout m_alertLayout;
	public RelativeLayout layout=null;

	int m_nBtnYesId, m_nBtnNoId, m_nBtnOkId;

	public TrendDelAlertDialog(Context context) {
//		super(context);
		super(context, context.getResources().getIdentifier("alert_layout", "style", context.getPackageName()));


		initDialog(context);
	}

	private void initDialog(Context context)
	{
		LayoutInflater factory = LayoutInflater.from(context);
		m_alertLayout = (LinearLayout)factory.inflate(context.getResources().getIdentifier("trenddelalertdialog", "layout", context.getPackageName()), null);

//		setView(m_alertLayout);
		setContentView(m_alertLayout);

		int[] ids = {
				context.getResources().getIdentifier("chk_del", "id", context.getPackageName()),
				context.getResources().getIdentifier("chk_Alldel", "id", context.getPackageName())
		};
		int[] ids1 = {
				context.getResources().getIdentifier("textType1", "id", context.getPackageName()),
				context.getResources().getIdentifier("textType2", "id", context.getPackageName())
		};

		for(int i = 0 ; i <  ids.length  ; i++){
			CheckBox btnCheckBox = (CheckBox)m_alertLayout.findViewById(ids[i]);

			btnChk[i] = btnCheckBox;

			btnCheckBox.setOnClickListener( new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					CheckBox chk = (CheckBox) v;
					for (int i = 0 ; i < 2 ; i++)
					{
						if(btnChk[i] != chk)
						{
                            btnChk[i].setChecked(false);
						}
                        else
                        {
                            btnChk[i].setChecked(true);
                        }
					}
				}
			});

		};


		for(int i = 0 ; i < ids1.length  ; i++){
			TextView tv = (TextView)m_alertLayout.findViewById(ids1[i]);
			final CheckBox btnCheckBox = (CheckBox)m_alertLayout.findViewById(ids[i]);

			tvTitle[i] = tv;

			tv.setOnClickListener( new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					for (int i = 0 ; i < 2 ; i++)
					{
						if(tvTitle[i] != v)
						{
							CheckBox tmp = btnChk[i];
							tmp.setChecked(false);
						}
						else
						{
							CheckBox tmp = btnChk[i];
							tmp.setChecked(true);
						}
					}
				}
			});
		};


		// Dialog 사이즈 조절 하기
		LayoutParams params = getWindow().getAttributes();
		//2020.05.14 by JJH >> 추세선 설정 팝업 UI 작업 start
		params.width = (int)COMUtil.getPixel(300);
//		params.height = (int)COMUtil.getPixel(210);
		//2020.05.14 by JJH >> 추세선 설정 팝업 UI 작업 end
		getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

		COMUtil.setGlobalFont(m_alertLayout);
	}

	public void setTitle(String strTitle)
	{
		int layoutResId = COMUtil.apiView.getContext().getResources().getIdentifier("alert_title", "id", COMUtil.apiView.getContext().getPackageName());
		TextView alert_title  = (TextView)m_alertLayout.findViewById(layoutResId);
		alert_title.setText(strTitle);
	}

	public void setMessage(String strMessage)
	{
		int layoutResId = COMUtil.apiView.getContext().getResources().getIdentifier("alert_message", "id", COMUtil.apiView.getContext().getPackageName());
		TextView alert_message  = (TextView)m_alertLayout.findViewById(layoutResId);
		alert_message.setText(strMessage);
	}

	public void setYesButton(String strText, OnClickListener listener)
	{
		m_nBtnYesId = COMUtil.apiView.getContext().getResources().getIdentifier("alert_btn_yes", "id", COMUtil.apiView.getContext().getPackageName());
		Button alert_btn_yes = (Button)m_alertLayout.findViewById(m_nBtnYesId);
		alert_btn_yes.setVisibility(View.VISIBLE);
		m_listenerYes = listener;
		alert_btn_yes.setOnClickListener(this);
	}

	public void setNoButton(String strText, OnClickListener listener)
	{
		m_nBtnNoId = COMUtil.apiView.getContext().getResources().getIdentifier("alert_btn_no", "id", COMUtil.apiView.getContext().getPackageName());
		Button alert_btn_no = (Button)m_alertLayout.findViewById(m_nBtnNoId);
		alert_btn_no.setVisibility(View.VISIBLE);
		alert_btn_no.setTypeface(COMUtil.typefaceMid);
		alert_btn_no.setOnClickListener(this);
	}

	public void setOkButton(String strText, OnClickListener listener)
	{
		m_nBtnOkId = COMUtil.apiView.getContext().getResources().getIdentifier("alert_btn_ok", "id", COMUtil.apiView.getContext().getPackageName());
		Button alert_btn_ok = (Button)m_alertLayout.findViewById(m_nBtnOkId);
		alert_btn_ok.setVisibility(View.VISIBLE);

		alert_btn_ok.setOnClickListener(this);
	}

    public boolean isAllDelete() {
		if(btnChk[1].isChecked())
			return true;

		return false;
	}

    @Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int nId = v.getId();

		if (nId == m_nBtnYesId) {
			if(m_listenerYes != null) m_listenerYes.onClick(this, BUTTON_POSITIVE);
			else dismiss();
		} else if (nId == m_nBtnNoId) {
			if(m_listenerNo != null) m_listenerNo.onClick(this, BUTTON_NEGATIVE);
			else dismiss();
		}
		else if (nId == m_nBtnOkId) {
			if(m_listenerOK != null) m_listenerOK.onClick(this, BUTTON_NEUTRAL);
			else dismiss();
		}
	}
}
