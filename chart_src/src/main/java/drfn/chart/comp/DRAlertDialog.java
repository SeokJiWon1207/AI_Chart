package drfn.chart.comp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import drfn.chart.util.COMUtil;
import drfn.chart_src.R;

public class DRAlertDialog extends Dialog implements View.OnClickListener, View.OnTouchListener {

	private DialogInterface.OnClickListener m_listenerYes = null;
	private DialogInterface.OnClickListener m_listenerNo = null;
	private DialogInterface.OnClickListener m_listenerOK = null;
	private View.OnTouchListener onTouchListener = null;
	private Context context = null;
	LinearLayout m_alertLayout;
	private TextView m_alert_message;

	View divideView = null;
	int m_nBtnYesId, m_nBtnNoId, m_nBtnOkId,layoutResId;
	public AppCompatButton alert_btn_no, alert_btn_yes, btn_cancle;

	LinearLayout m_ll_Top;	//2020.05.14 by JJH >> 알림 설정 팝업 UI 작업

	public DRAlertDialog(Context context) {
//		super(context);
		super(context, context.getResources().getIdentifier("alert_layout", "style", context.getPackageName()));

		initDialog(context);
	}

	private void initDialog(Context context)
	{
		LayoutInflater factory = LayoutInflater.from(context);
		if (COMUtil.getSkinType()!= COMUtil.SKIN_BLACK) {
			//2023.11.16 by CYJ - kakaopay 폴더블 해상도 대응 >>
			if(COMUtil.checkFolded()) {
				m_alertLayout = (LinearLayout) factory.inflate(context.getResources().getIdentifier("alertdialogcustom_folded", "layout", context.getPackageName()), null);
			} else {
				m_alertLayout = (LinearLayout) factory.inflate(context.getResources().getIdentifier("alertdialogcustom", "layout", context.getPackageName()), null);
			}
			//2023.11.16 by CYJ - kakaopay 폴더블 해상도 대응 <<
		} else {
			if(COMUtil.checkFolded()) {
				m_alertLayout = (LinearLayout) factory.inflate(context.getResources().getIdentifier("alertdialogcustom_folded_dark", "layout", context.getPackageName()), null);
			} else {
				m_alertLayout = (LinearLayout) factory.inflate(context.getResources().getIdentifier("alertdialogcustom_dark", "layout", context.getPackageName()), null);
			}
			//2023.11.16 by CYJ - kakaopay 폴더블 해상도 대응 <<
		}

//		setView(m_alertLayout);
		setContentView(m_alertLayout);

		// Dialog 사이즈 조절 하기
		LayoutParams params = getWindow().getAttributes();
		params.width = (int)COMUtil.getPixel(320);
		if(COMUtil.checkFolded()) {
			params.width = (int)COMUtil.getPixel(216);
		}
		params.height = LayoutParams.WRAP_CONTENT;	//2020.05.14 by JJH >> 알림 설정 팝업 UI 작업
		getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
		getWindow().setWindowAnimations(R.style.CustomDialogAnimation); //2023.11.10 by CYJ - kakaopay 인터렉션 다이어로그 애니메이션 추가
//
//		COMUtil.setGlobalFont(m_alertLayout);
		//2023.12.18 by CYJ - 변경사항에 대한 적용 여부 팝업창 >>
		m_nBtnNoId = COMUtil.apiView.getContext().getResources().getIdentifier("alert_btn_no", "id", COMUtil.apiView.getContext().getPackageName());
		alert_btn_no = (AppCompatButton)m_alertLayout.findViewById(m_nBtnNoId);
		m_nBtnYesId = COMUtil.apiView.getContext().getResources().getIdentifier("alert_btn_yes", "id", COMUtil.apiView.getContext().getPackageName());
		alert_btn_yes = (AppCompatButton)m_alertLayout.findViewById(m_nBtnYesId);

		layoutResId = COMUtil.apiView.getContext().getResources().getIdentifier("btn_cancle", "id", COMUtil.apiView.getContext().getPackageName());
		btn_cancle = (AppCompatButton)m_alertLayout.findViewById(layoutResId);
		btn_cancle.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		btn_cancle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dismiss();
			}
		});
		//2023.12.18 by CYJ - 변경사항에 대한 적용 여부 팝업창 <<
	}

	public void setTitle(String strTitle)
	{


		int layoutResId = COMUtil.apiView.getContext().getResources().getIdentifier("alert_title", "id", COMUtil.apiView.getContext().getPackageName());
		TextView alert_title  = (TextView)m_alertLayout.findViewById(layoutResId);
		alert_title.setText(strTitle);

		layoutResId = COMUtil.apiView.getContext().getResources().getIdentifier("savetop_init", "id", COMUtil.apiView.getContext().getPackageName());
		RelativeLayout rl_Top = (RelativeLayout) m_alertLayout.findViewById(layoutResId);

		layoutResId = COMUtil.apiView.getContext().getResources().getIdentifier("savemid_init", "id", COMUtil.apiView.getContext().getPackageName());
		m_ll_Top = (LinearLayout) m_alertLayout.findViewById(layoutResId);	//2020.05.14 by JJH >> 알림 설정 팝업 UI 작업

		if(strTitle.equals(""))
		{
			rl_Top.setVisibility(View.GONE);
		}
		else
		{
			rl_Top.setVisibility(View.VISIBLE);
			//m_ll_Top.setBackgroundColor(Color.WHITE);	//2020.05.14 by JJH >> 알림 설정 팝업 UI 작업
		}
	}

	public void setMessage(String strMessage)
	{
		int layoutResId = COMUtil.apiView.getContext().getResources().getIdentifier("alert_message", "id", COMUtil.apiView.getContext().getPackageName());
		m_alert_message  = (TextView)m_alertLayout.findViewById(layoutResId);
		m_alert_message.setText(strMessage);

		//2020.05.14 by JJH >> 알림 설정 팝업 UI 작업 start
		layoutResId = COMUtil.apiView.getContext().getResources().getIdentifier("savemid_init", "id", COMUtil.apiView.getContext().getPackageName());
		m_ll_Top = (LinearLayout) m_alertLayout.findViewById(layoutResId);

//		int nHeight = 0;
//		if (m_alert_message.getText().length() >= 22){
//			nHeight = (int)COMUtil.getPixel(122);
//			m_alert_message.setGravity(Gravity.LEFT);
//		}else{
//			nHeight = (int)COMUtil.getPixel(94);
//			m_alert_message.setGravity(Gravity.CENTER);
//		}
//		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) COMUtil.getPixel(320), nHeight);
//		m_ll_Top.setLayoutParams(params);
		//2020.05.14 by JJH >> 알림 설정 팝업 UI 작업 end
	}

	public void setYesButton(String strText, DialogInterface.OnClickListener listener)
	{
		m_nBtnYesId = COMUtil.apiView.getContext().getResources().getIdentifier("alert_btn_yes", "id", COMUtil.apiView.getContext().getPackageName());
		Button alert_btn_yes = (AppCompatButton) m_alertLayout.findViewById(m_nBtnYesId);
		alert_btn_yes.setText(strText);
		alert_btn_yes.setVisibility(View.VISIBLE);
		alert_btn_yes.setOnTouchListener((View.OnTouchListener) this);
		alert_btn_yes.setOnClickListener(this);
		m_listenerYes = listener;
	}

	public void setNoButton(String strText, DialogInterface.OnClickListener listener)
	{
		m_nBtnNoId = COMUtil.apiView.getContext().getResources().getIdentifier("alert_btn_no", "id", COMUtil.apiView.getContext().getPackageName());
		Button alert_btn_no = (AppCompatButton)m_alertLayout.findViewById(m_nBtnNoId);
		alert_btn_no.setText("취소");	//2020.05.14 by JJH >> 알림 설정 팝업 UI 작업
		alert_btn_no.setVisibility(View.VISIBLE);
		alert_btn_no.setOnTouchListener((View.OnTouchListener) this);
		alert_btn_no.setOnClickListener(this);
	}

	//2023.12.18 by CYJ - 변경사항에 대한 적용 여부 팝업창 >> 버튼 색상
	public void setNoButton(String strText) {
		alert_btn_no.setText(strText);
		int redColor = 0;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
			redColor = ContextCompat.getColor(getContext(), R.color.kfit_red);
		}
		alert_btn_no.setTextColor(redColor);

		Drawable drawable = null;
		drawable = ContextCompat.getDrawable(getContext(), R.drawable.dialog_button_background2);
		alert_btn_no.setBackground(drawable);
	}
	public void setYesButton(String strText)  {
		alert_btn_yes.setText(strText);
		int blackColor = 0;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
			//2023.12.20 by CYJ - 수정내용 반영여부 팝업창 버튼 다크모드 대응 >>
			if(COMUtil.getSkinType() == COMUtil.SKIN_BLACK)
				blackColor = ContextCompat.getColor(getContext(), R.color.kfit_inverse_high__emphasis);
			else
				blackColor = ContextCompat.getColor(getContext(), R.color.kfit_high__emphasis);
			//2023.12.20 by CYJ - 수정내용 반영여부 팝업창 버튼 다크모드 대응 <<
		}
		alert_btn_yes.setTextColor(blackColor);

		Drawable drawable = null;
		drawable = ContextCompat.getDrawable(getContext(), R.drawable.button_background2);
		alert_btn_yes.setBackground(drawable);
		//2023.12.18 by CYJ - 변경사항에 대한 적용 여부 팝업창 << 버튼 색상
	}

	public void setOkButton(String strText, DialogInterface.OnClickListener listener)
	{
		m_nBtnOkId = COMUtil.apiView.getContext().getResources().getIdentifier("alert_btn_ok", "id", COMUtil.apiView.getContext().getPackageName());
		Button alert_btn_ok = (AppCompatButton)m_alertLayout.findViewById(m_nBtnOkId);
		alert_btn_ok.setText("확인");	//2020.05.14 by JJH >> 알림 설정 팝업 UI 작업
		alert_btn_ok.setVisibility(View.VISIBLE);
		m_nBtnYesId = COMUtil.apiView.getContext().getResources().getIdentifier("alert_btn_yes", "id", COMUtil.apiView.getContext().getPackageName());
		Button alert_btn_yes = (AppCompatButton)m_alertLayout.findViewById(m_nBtnYesId);
		alert_btn_yes.setVisibility(View.GONE);
		m_nBtnNoId = COMUtil.apiView.getContext().getResources().getIdentifier("alert_btn_no", "id", COMUtil.apiView.getContext().getPackageName());
		Button alert_btn_no = (AppCompatButton)m_alertLayout.findViewById(m_nBtnNoId);
		alert_btn_no.setVisibility(View.GONE);
		alert_btn_ok.setOnClickListener(this);
		alert_btn_ok.setOnTouchListener((View.OnTouchListener) this);
	}
	public void setMessageGravity(String strGravity) {
		if (m_alert_message != null) {
			if (strGravity.equals("center"))
				m_alert_message.setGravity(Gravity.CENTER);
			else if (strGravity.equals("right"))
				m_alert_message.setGravity(Gravity.RIGHT);
			else if (strGravity.equals("left"))
				m_alert_message.setGravity(Gravity.LEFT);
			else
				m_alert_message.setGravity(Gravity.NO_GRAVITY);
		}
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
	//2023.11.10 by CYJ - kakaopay 인터렉션 >>
	Animation reduce = AnimationUtils.loadAnimation(this.getContext(), R.anim.animation_reduce);
	Animation enlarge = AnimationUtils.loadAnimation(this.getContext(), R.anim.animation_enlarge);
	public boolean onTouch(final View v, final MotionEvent e) {
		switch (e.getAction()) {
			case MotionEvent.ACTION_DOWN:
				v.startAnimation(reduce);

				Button alert_btn_yes = (Button)m_alertLayout.findViewById(m_nBtnYesId);
				if(v == alert_btn_yes) {
					if(COMUtil.getSkinType() != COMUtil.SKIN_BLACK) {
						v.getBackground().setColorFilter(Color.parseColor("#FF7E7E"), PorterDuff.Mode.SRC_IN);
					} else {
						v.getBackground().setColorFilter(Color.parseColor("#C25C2629"), PorterDuff.Mode.SRC_IN);
					}
				} else if(v == m_listenerNo) {
					v.getBackground().setColorFilter(Color.parseColor("#5C2629"), PorterDuff.Mode.SRC_IN);
				}
				v.invalidate();
				break;

			case MotionEvent.ACTION_UP:
				Animation.AnimationListener animationListener = new Animation.AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {
						// Animation start
					}

					@Override
					public void onAnimationEnd(Animation animation){
						dismiss();
					}
					@Override
					public void onAnimationRepeat(Animation animation) {
						// Animation repeat
					}
				};

				v.startAnimation(enlarge);
				v.getBackground().clearColorFilter();
				v.invalidate();
				enlarge.setAnimationListener(animationListener);
		}
		return false;
	}
	//2023.11.10 by CYJ - kakaopay 인터렉션 <<
}
