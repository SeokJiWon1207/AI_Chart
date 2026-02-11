package drfn.chart.base;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import java.util.ArrayList;

import drfn.chart.util.COMUtil;

//라이브러리 용 
public class PeriodSettingView extends JipyoControlSetUI{
	ArrayList<EditText> arMinEdit;
	ArrayList<EditText> arTikEdit;

	public PeriodSettingView(Context context, RelativeLayout layout) {
		super(context, layout);
	}

	public void setUI()
	{
		jipyoui = (LinearLayout)layout.getChildAt(layout.getChildCount() - 1);

		//2012. 9. 14 태블릿에서 분틱차트 설정창 따로 팝업띄우기
		if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
		{
//			LayoutInflater factory = LayoutInflater.from(context);
//			int layoutResId = this.getContext().getResources().getIdentifier("periodsetting_tab", "layout", this.getContext().getPackageName());
//			jipyoui = factory.inflate(layoutResId, null);
//			LinearLayout popupView = jipyoui.getChildAt(0);

			//분틱차트설정에 해당되는 레이아웃은  "detailLayout" 이라는 tag를 갖는 LinearLayout 에 감싸져있고, 이 "detailLayout" 은 차트의 layout에 addview 되어있음. 
			//popupwindow에 위 레이아웃을 그냥 추가하면 The specified child already has a parent 에러를 호출하면서 죽는다.  layout을 이미 부모로 둬서 그런 것으로 추정됨.
			//그래서 "detailLayout"  의 자식인  실제 분틱차트 레이아웃만 가져와서 이를 다시 부모가 없는 LinearLayout 으로 감싸줌. 
			LinearLayout popupLayout = (LinearLayout)layout.findViewWithTag("detailLayout");
			layout.removeView(popupLayout);
			jipyoui = (LinearLayout)popupLayout.findViewWithTag("period_tab");
			popupLayout.removeView(jipyoui);

			periodPopup = new PopupWindow(jipyoui, COMUtil._mainFrame.indicatorParams.width, (int)COMUtil.getPixel(385), true);
			periodPopup.setOutsideTouchable(true); // 이부분을 설정해주어야 팝업이 떳을때 다른부분에 이벤트를 줄수있습니다.
			periodPopup.setBackgroundDrawable(new BitmapDrawable());  // 이부분에 이벤트가 들어오게됩니다.
			periodPopup.showAtLocation(COMUtil.apiView.getRootView(), Gravity.TOP|Gravity.LEFT, COMUtil._mainFrame.indicatorParams.leftMargin-(int)COMUtil.getPixel(300), COMUtil._mainFrame.indicatorParams.topMargin);

			COMUtil._mainFrame.indicatorPopup.dismiss();
			COMUtil._mainFrame.indicatorPopup = null;
		}

		//2012. 9. 11  세로모드일 화면터치하면 키패드 숨기기 
		int layoutResId = context.getResources().getIdentifier("periodscrollview", "id", context.getPackageName());
		ScrollView scroll = (ScrollView)jipyoui.findViewById(layoutResId);

		Configuration config = getResources().getConfiguration();

		if(config.orientation == Configuration.ORIENTATION_PORTRAIT)
		{
			scroll.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent e) {
					hideKeyPadAll();

					return true;
				}
			});
		}


		//분틱 에디트텍스트를 가지고 있는  arraylist 
		arMinEdit = new ArrayList<EditText>();
		arTikEdit = new ArrayList<EditText>();

		//분틱 에디트텍스트 세팅
		setMinEdit(jipyoui);
		setTikEdit(jipyoui);
	}

	public void resetUI() {
	}

	public void reSetOriginal()
	{
		Base11 base = (Base11)COMUtil._mainFrame.mainBase.baseP;

		//분차트 값  초기화
		for(int i = 0; i < 7; i++)
		{
			arMinEdit.get(i).setText(String.valueOf(base.astrMinDefaultData[i]));
			base.astrMinData[i] = base.astrMinDefaultData[i];
		}

		//틱차트 값  초기화
		for(int i = 0; i < 6; i++)
		{
			arTikEdit.get(i).setText(String.valueOf(base.astrTikDefaultData[i]));
			base.astrTikData[i] = base.astrTikDefaultData[i];
		}
		COMUtil.sendTR(""+COMUtil._TAG_SET_PERIOD_UNITS);
	}

	public void reSetJipyo() {

	}

	public void setMinEdit(View v)
	{
		//xml 의 에디트텍스트 Id 의 앞쪽 공통된 부분
		String strID_preText = "periodsetting_minedit";

		int layoutResId;
		Base11 base = (Base11)COMUtil._mainFrame.mainBase.baseP;

		for(int i = 0; i < 7; i++)
		{
			String strID = strID_preText + String.valueOf(i+1);  // 아이디 예시 : periodsetting_minedit1    등등
			layoutResId = context.getResources().getIdentifier(strID, "id", context.getPackageName());
			final EditText edTmp = (EditText)v.findViewById(layoutResId);

			//각 Edittext 의 키 입력 리스너
			edTmp.setOnEditorActionListener(new OnEditorActionListener()
			{
				public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
				{
//					System.out.println("endname actionid = " + actionId);
					if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE)
					{
						//2012. 12. 21  입력칸 빈칸일때의 처리
						if(edTmp.getText().toString().equals(""))
						{
							edTmp.setText("1");
						}
						hideKeyPad(edTmp);
					}
					return true;
				}
			});

			//최초실행시는 astrMinData 라는 분차트값이 없으므로 세팅해준다
			if(base.astrMinData[i] == -1)
			{
				edTmp.setText(String.valueOf(base.astrMinDefaultData[i]));
				base.astrMinData[i] = base.astrMinDefaultData[i];
			}
			else
			{
				edTmp.setText(String.valueOf(base.astrMinData[i]));
			}

			//arraylist 에 추가
			arMinEdit.add(edTmp);
		}
	}
	public void setTikEdit(View v)
	{
		//xml 의 에디트텍스트 Id 의 앞쪽 공통된 부분
		String strID_preText = "periodsetting_tikedit";

		int layoutResId;
		Base11 base = (Base11)COMUtil._mainFrame.mainBase.baseP;

		for(int i = 0; i < 6; i++)
		{
			String strID = strID_preText + String.valueOf(i+1);  // 아이디 예시 : periodsetting_minedit1    등등
			layoutResId = COMUtil.apiView.getContext().getResources().getIdentifier(strID, "id", COMUtil.apiView.getContext().getPackageName());
			final EditText edTmp = (EditText)v.findViewById(layoutResId);

			//각 Edittext 의 키 입력 리스너
			edTmp.setOnEditorActionListener(new OnEditorActionListener()
			{
				public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
				{
//					System.out.println("endname actionid = " + actionId);
					if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE)
					{
						//2012. 12. 21  입력칸 빈칸일때의 처리
						if(edTmp.getText().toString().equals(""))
						{
							edTmp.setText("1");
						}
						hideKeyPad(edTmp);
					}
					return true;
				}
			});

			//최초실행시는 astrTikData 라는 틱차트값이 없으므로 세팅해준다
			if(base.astrTikData[i] == -1)
			{
				edTmp.setText(String.valueOf(base.astrTikDefaultData[i]));
				base.astrTikData[i] = base.astrTikDefaultData[i];
			}
			else
			{
				edTmp.setText(String.valueOf(base.astrTikData[i]));
			}

			//arraylist 에 추가
			arTikEdit.add(edTmp);
		}
	}

	public void acceptMinTikData()
	{
		Base11 base = (Base11)COMUtil._mainFrame.mainBase.baseP;

		//분차트 값  적용
		for(int i = 0; i < 7; i++)
		{
			String strGetTextByIdx = arMinEdit.get(i).getText().toString();
			base.astrMinData[i] = Integer.parseInt(strGetTextByIdx);
		}

		//틱차트 값  적용
		for(int i = 0; i < 6; i++)
		{
			String strGetTextByIdx = arTikEdit.get(i).getText().toString();
			base.astrTikData[i] = Integer.parseInt(strGetTextByIdx);
		}
		COMUtil.sendTR(""+COMUtil._TAG_SET_PERIOD_UNITS);
	}

	//문자 분석툴 사용시 키패드 감추기 위함
	public void hideKeyPad(EditText edit)
	{
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);
		edit.clearFocus();
		edit.requestFocus();
	}

	//  	public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
// 	{
// 		//2012. 8. 14   
// 		if( (event != null && actionId == event.getAction()) || actionId == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE)
// 		{
// 			hideKeyPadAll();
// 	 		return true;
// 		}
// 		else
// 		{
// 			return false;
// 		}
// 	}
	public void destroy() {
		super.destroy();
		//2012. 9. 11  X 버튼 누르면 분틱차트 값 저장 
		acceptMinTikData();

		//2012. 9. 11  창 닫힐때 키패드가 열려있으면 닫기 
		hideKeyPadAll();

		invalidate();
	}

	//2012. 9. 11  EditText전체를 조사하면서 해당 키패드 찾아서 닫기 
	private void hideKeyPadAll()
	{
		for(int i = 0; i < arMinEdit.size(); i++)
		{
			if(arMinEdit.get(i).isFocused())
				hideKeyPad(arMinEdit.get(i));
		}
		for(int i = 0; i < arTikEdit.size(); i++)
		{
			if(arTikEdit.get(i).isFocused())
				hideKeyPad(arTikEdit.get(i));
		}
	}
}
