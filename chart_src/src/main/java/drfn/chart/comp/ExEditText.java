package drfn.chart.comp;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import drfn.chart.base.JipyoControlSetUI;
import drfn.chart.util.COMUtil;
import drfn.chart_src.R;

public class ExEditText extends EditText implements View.OnTouchListener {
	private OnTouchListener onTouchListener;
	public ImageView clearButton;
	private OnBackButtonListener l;
	
	public ExEditText(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}
	
	

	public ExEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}
	//2023.11.09 by CYJ - kakaopay edittext 초기화 버튼 >>
	public void init() {
//		clearButton = new ImageView(getContext());
//		clearButton.setMinimumWidth((int) COMUtil.getPixel_W(24));
//		clearButton.setMaxWidth((int) COMUtil.getPixel_W(24));
//		clearButton.setVisibility(GONE);
//		if (COMUtil.getSkinType()!= COMUtil.SKIN_BLACK) {
//			clearButton.setImageResource(R.drawable.button_fit_component); // Set the clear button icon
//		} else {
//			clearButton.setImageResource(R.drawable.button_fit_component_dark); // Set the clear button icon
//		}
//		super.setOnTouchListener(this);
	}
//	public void setVisibleClearBtn(Boolean b) {
// 		if(b) {
//			setCompoundDrawablesWithIntrinsicBounds(null, null, clearButton.getDrawable(), null);
//			clearButton.setVisibility(VISIBLE);
//		} else {
//			setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
//			clearButton.setVisibility(GONE);
//		}
//	}
	@Override
	public void setOnTouchListener(OnTouchListener onTouchListener) {
		this.onTouchListener = onTouchListener;
	}
	public boolean onTouch(final View view, final MotionEvent motionEvent) {
		final int x = (int) motionEvent.getX();
//		if (clearButton.getVisibility() == VISIBLE && x > getWidth() - getPaddingRight() - (COMUtil.getPixel_W(24))) {
//			if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
//				setError(null);
//				setText(null);
//			}
//			return true;
//		}

 		if (onTouchListener != null) {
			return onTouchListener.onTouch(view, motionEvent);
		}
		else {
			return false;
		}
		//2023.11.09 by CYJ - kakaopay edittext 초기화 버튼 <<
	}



	@Override
	public boolean onKeyPreIme(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		
		if(event.getAction() == KeyEvent.ACTION_DOWN)
		{
			if(keyCode == KeyEvent.KEYCODE_BACK)
			{
				if(l != null)
				{
					l.onBackButtonClick(this);
					//2024.01.09 by CYJ - 백버튼 입력 시 변경사항 체크 >>
					if(l == (JipyoControlSetUI) l) {
						((JipyoControlSetUI) l).bBack = true;
						((JipyoControlSetUI) l).reSetJipyo();
					}
					//2024.01.09 by CYJ - 백버튼 입력 시 변경사항 체크 <<
				}
				return super.onKeyPreIme(keyCode, event);
			}
		}
		return super.onKeyPreIme(keyCode, event);
	}
	
	public void setOnBackButtonListener(OnBackButtonListener l)
	{
		this.l = l;
	}
	
	public interface OnBackButtonListener
	{
		public void onBackButtonClick(EditText ed);
	}
}
