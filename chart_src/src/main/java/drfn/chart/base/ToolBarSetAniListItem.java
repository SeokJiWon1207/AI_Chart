package drfn.chart.base;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import drfn.chart.util.COMUtil;

//import com.hanawm.corelib.shared.InterestManager.IntrManager;
//import com.hanawm.corelib.util.ResourceManager;
//import com.hanawm.corelib.util.Util;
//import com.hanawm.phonelib.HMSmartActivity;
//import com.hanawm.phonelib.view.intr.IntrMotionList.OnMenuOpenChangedListener;
//import com.hanawm.phonelib.view.intr.IntrMotionList.OnSubClickListener;

public class ToolBarSetAniListItem extends LinearLayout{
	///FIELD
	public static final int TYPE_ITEM = 1;

	private final int DURATION = 200;
	public boolean m_isDataChange = false;

	//	private Button 			m_oBtnDel;
	private FrameLayout 	m_oFLayout;
	//	private RelativeLayout 	frontBack;
	private LinearLayout 	frontBack;//2013. 8. 30  도구설정 리스트 행  레이아웃 유형 변경 
	private LinearLayout 	m_LLayout;

	//private int		m_nType = TYPE_GROUP;

	///CONSTRUCTOR
	public ToolBarSetAniListItem(Context context) {
		super(context);
		setGravity(Gravity.CENTER);
		init();
	}

	///METHOD
	public void init(){
		//2012. 8. 16 자동추세창 레이아웃 크기 및 위치 조절 : T_tab10
		if(m_oFLayout==null) m_oFLayout = new FrameLayout(COMUtil.apiView.getContext());
		m_oFLayout.setForegroundGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
		m_oFLayout.setBackgroundColor(Color.TRANSPARENT);
		setGravity(Gravity.CENTER);
		if(m_oFLayout.getParent()==null) addView(m_oFLayout, new LayoutParams(LayoutParams.FILL_PARENT, (int)COMUtil.getPixel(56)));
		setFrontView();
	}

//	public void setMenuAni(boolean is){
//		setMenuAni(is, DURATION);
//	}
//	
//	public void setMenuAni(boolean is, int dur){
//		m_isOpen = is;
//	}

	public void setFrontView()
	{

		Context context = COMUtil.apiView.getContext();
		int	layoutResId = context.getResources().getIdentifier("analtool_change_row", "layout", COMUtil.apiView.getContext().getPackageName());
		LayoutInflater factory = LayoutInflater.from(context);
//		frontBack = (RelativeLayout)factory.inflate(layoutResId, null);	
		frontBack = (LinearLayout)factory.inflate(layoutResId, null);	//2013. 8. 30  도구설정 리스트 행  레이아웃 유형 변경


		m_oFLayout.addView(frontBack, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	}
//	
//	public void setOnIconChangedListener(OnIconChangedListener listener){
//		m_oIconListener = listener; 
//	}

	public View getFrontView(int idx){
		return frontBack.getChildAt(idx);
	}
	public View getSecondView(int idx){
		return m_LLayout.getChildAt(idx);
	}
	boolean m_isSelected = false;
	public void setSelected(boolean is){
		m_isSelected=is;
	}
	@Override
	protected void dispatchDraw(Canvas canvas) {
		if(m_isSelected){
			canvas.drawARGB(150, 100, 100, 100);// TODO Auto-generated method stub
		}
		super.dispatchDraw(canvas);
	}
}