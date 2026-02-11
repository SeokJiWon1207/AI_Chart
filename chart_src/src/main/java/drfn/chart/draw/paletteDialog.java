
package drfn.chart.draw;

import android.R.string;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
//import android.media.Image;
import android.os.Message;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

import drfn.chart.base.BaseLineView;
import drfn.chart.base.JipyoControlSetUI;
import drfn.chart.comp.AnalToolSettingViewController;
import drfn.chart.util.COMUtil;
import drfn.chart.util.CoSys;

//test 2016.04.01
import drfn.chart.base.CompareSettingController;
/**
 * AlertDialog.Builder 를 이용해서 메인엑티비티에서 Dialog 를 띄우니까 가로영역의 길이가 고정되 여백이 생기는 현상이 있었습니다. 
 * 그래서 Dialog 객체를 상속받은 Custom Dialog 를 이용하였습니다. 
 *
 * 2012. 5. 30  Developed by 김승환 
 * */

public class paletteDialog extends Dialog implements OnClickListener{
	Context mContext;
	Object _parent;
	CompareSettingController _compParent;
	
	String strMapName;

	// layout에서 구현해둔 TableLayout
	TableLayout table;
	// layout에서 구현해둔 ScrollView
	ScrollView scrollview;
	//버튼들을 저장하는 컨테이너(ArrayList)  
	ArrayList<ImageButton> arButtons;

	//현재 선택한 색상을 저장하고 있는 변수  
	Drawable currentColorDrawable;

	int nRow, nCol;

	//메인액티비티에서 넘겨진 색상을 변경할 TextView 입니다.  
	TextView tvColor;

	//메인에서 가져온 색상
	int colorTag;

	BaseLineView _baseParent;  // 2016.05.31 기준선 대비, 색상 굵기

	public paletteDialog(Context context, int nRow, int nCol, TextView tvColor, int colorTag)
	{
		super(context, context.getResources().getIdentifier("alert_layout_bottom", "style", context.getPackageName()));
		mContext = context;

		// 타이틀바가 없는 다이얼로그로 세팅합니다. 
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		//2012. 8. 29  상세설정창에서 팔레트의 배경이 검은색이 아닌 다른색으로 나오던 현상 수정 : I99
//		LayoutInflater factory = LayoutInflater.from(context);
//		int layoutResId = this.getContext().getResources().getIdentifier("colorchangepalette", "layout", this.getContext().getPackageName());
//		scrollview = (ScrollView)factory.inflate(layoutResId, null);
//		scrollview.setBackgroundColor(Color.BLACK);
//		setContentView(scrollview);
		LayoutInflater factory = LayoutInflater.from(context);
		int layoutResId = context.getResources().getIdentifier("colorchangepalette", "layout", this.getContext().getPackageName());
		LinearLayout ll = (LinearLayout)factory.inflate(layoutResId, null);
//		layoutResId = context.getResources().getIdentifier("palette_title_view", "id", context.getPackageName());
//		TextView tv = (TextView)ll.findViewById(layoutResId);
//		tv.setText("색상선택");

		layoutResId = context.getResources().getIdentifier("color_change_scrollview", "id", context.getPackageName());
		scrollview = (ScrollView)ll.findViewById(layoutResId);
		// scrollview.setBackgroundColor(Color.BLACK);
		scrollview.setBackgroundColor(Color.WHITE);
//		setContentView(ll);

		//2020.05.08 by JJH >> 가로모드 작업 (색상 설정 팝업) start
//		setContentView(ll, new ViewGroup.LayoutParams(COMUtil.g_nDisWidth, (int) COMUtil.getPixel(343)));	//size change

		Display display = COMUtil._chartMain.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);

		Configuration config = COMUtil.apiView.getContext().getResources().getConfiguration();
		if(config.orientation == Configuration.ORIENTATION_LANDSCAPE)
		{
			//가로
//			setContentView(ll, new ViewGroup.LayoutParams((int) COMUtil.getPixel(360), ViewGroup.LayoutParams.WRAP_CONTENT));	//size change
			setContentView(ll, new ViewGroup.LayoutParams((int) COMUtil.getPixel_W(324), size.y - (int) COMUtil.getPixel(51)));	//size change
		}else{
			//세로
			setContentView(ll, new ViewGroup.LayoutParams((int) COMUtil.getPixel(324), (int) COMUtil.getPixel(288)));	//size change
		}

//		layoutResId = this.getContext().getResources().getIdentifier("btn_close", "id", this.getContext().getPackageName());
//		Button btnBack = (Button)findViewById(layoutResId);
//
//		btnBack.setOnClickListener(new Button.OnClickListener() {
//			public void onClick(View v) {
//				try {
//					//이전화면으로 이동.
//					Message msg = new Message();
//					dismiss();
//				}
//				catch (Exception e) {
//				}
//			}
//		});
		//2020.05.08 by JJH >> 가로모드 작업 (색상 설정 팝업) end

		this.nRow = nRow;
		this.nCol = nCol;
		this.tvColor = tvColor;
		this.colorTag = colorTag;

		initTable();

		COMUtil.setGlobalFont(ll);
	}

	private void initButtons()  //팔레트색(버튼) 초기화  
	{
		//Button 들의 컨테이너로 쓰일  ArrayList 초기화합니다.
		arButtons = new ArrayList<ImageButton>();

		//CoSys  의   상승, 하락, 보합 색상 적용
//    	backColors[0] = Color.rgb(CoSys.CHART_COLORS[0][0], CoSys.CHART_COLORS[0][1], CoSys.CHART_COLORS[0][2]);
//    	backColors[1] = Color.rgb(CoSys.CHART_COLORS[1][0], CoSys.CHART_COLORS[1][1], CoSys.CHART_COLORS[1][2]);
//    	backColors[2] = Color.rgb(CoSys.CHART_COLORS[2][0], CoSys.CHART_COLORS[2][1], CoSys.CHART_COLORS[2][2]);
//		for(int i=0; i<4; i++)
//		{
//			backColors[i] = Color.rgb(CoSys.CHART_COLORS[i][0], CoSys.CHART_COLORS[i][1], CoSys.CHART_COLORS[i][2]);
//		}
//		for(int i=6; i<CoSys.CHART_COLORS.length+2; i++)
//		{
//			backColors[i] = Color.rgb(CoSys.CHART_COLORS[i-2][0], CoSys.CHART_COLORS[i-2][1], CoSys.CHART_COLORS[i-2][2]);
//		}

		// 버튼 20개 생성후  ArrayList 에 넣습니다.
		for(int i = 0; i < nRow*nCol; i++)
		{
			ImageButton btnAdd = new ImageButton(mContext);

			// 버튼의 id를 정해줍니다.안 정해줄시 nullpointer 예외 발생하므로 필수입니다.
			btnAdd.setId(i);

			//버튼의 색상을 세팅합니다.
			setButtonColor(btnAdd, i);

			//버튼의 가로 세로 길이를 지정합니다.
//    		btnAdd.setWidth((int)COMUtil.getPixel(40));
//    		btnAdd.setHeight((int)COMUtil.getPixel(25));

			//클릭 리스너 지정
			btnAdd.setOnClickListener(this);

			// 버튼의 Margin을 지정합니다.
			TableRow.LayoutParams trLayout = new TableRow.LayoutParams((int)COMUtil.getPixel(30), (int)COMUtil.getPixel(30));
			trLayout.setMargins((int)COMUtil.getPixel(0), (int)COMUtil.getPixel(0), (int)COMUtil.getPixel(20), (int)COMUtil.getPixel(20));
			btnAdd.setLayoutParams(trLayout);
			btnAdd.setPadding(0, 0, 0, 0);

			//ArrayList 에 버튼을 저장합니다.
			arButtons.add(btnAdd);
		}
	}

	int[] backColors = {
			Color.rgb(250, 74, 106),
			Color.rgb(255, 161, 39),
			Color.rgb(115, 211, 2),
			Color.rgb(16, 172, 211),
			Color.rgb(67, 140, 240),
			Color.rgb(136, 111, 217), //6
			Color.rgb(247, 77, 153),
			Color.rgb(255, 121, 40),
			Color.rgb(51, 187, 62),
			Color.rgb(59, 156, 207),
			Color.rgb(72, 108, 209),
			Color.rgb(166, 102, 226),  //12
			Color.rgb(232, 48, 174),
			Color.rgb(215, 96, 49),
			Color.rgb(58 ,180, 127),
			Color.rgb(16, 178, 161),
			Color.rgb(92, 97, 176),
			Color.rgb(195, 72, 231), //18
			Color.rgb(255, 255, 255),
			Color.rgb(208, 208, 208),
			Color.rgb(159, 159, 159),
			Color.rgb(105, 105, 105),
			Color.rgb(57, 57, 57),
			Color.rgb(17, 17, 17)
	};

//	{
//		/**
//		 * 각 팔레트색(버튼)에 컬러를 직접 세팅합니다.
//		 * **/
//		btn.setBackgroundColor(backColors[i]);
//		btn.setTag(""+backColors[i] );
//	}

	private void initTable()  //테이블 초기화 
	{
		// 테이블을 불러와서 초기화합니다. 
		int layoutResId = mContext.getResources().getIdentifier("color_change_tablelayout", "id", this.getContext().getPackageName());
		table = (TableLayout)findViewById(layoutResId);

		//기존 데이터가 있을시 리셋합니다, 
		if(table.getChildCount() > 0)
		{
			table.removeAllViews();
		}

		// 팔레트색 버튼들을 초기화합니다.
		initButtons();

		//불러올 ArrayList 의 인덱스로 사용될 변수입니다.
		int nArrayListIdx = 0;

		//6줄의 TableRow 에 5개씩의 팔레트색 (버튼)을 넣습니다.
		for(int i = 0; i < nRow; i++)
		{
			// TableLayout 에 들어가는 TableRow  를 생성합니다.
			TableRow tr = new TableRow(mContext);
			//2020.05.14 by JJH >> 색상 설정 팝업 색상 버튼 생성 위치 CENTER로 변경
			tr.setGravity(Gravity.CENTER_HORIZONTAL);

			// TableRow 에 들어갈 팔레트색(버튼) 을 넣습니다.
			for(int j = 0; j < nCol; j++)
			{
				if(nArrayListIdx < nRow*nCol)
				{
					tr.addView(arButtons.get(nArrayListIdx++));
				}
				else
					break;
			}
			//세팅이 끝난 TableRow 를 TableLayout 에 넣어서 한 행을 완성합니다.  
			table.addView(tr);
		}
	}

	public void setParent(Object parent, String strMapName)
	{
		this.strMapName = strMapName;
		_parent = parent;
	}

	public void setParent(JipyoControlSetUI parent)
	{
		strMapName = "Jipyo";
		_parent = parent;
	}

	private void setButtonColor(ImageButton btn, int i)   // R, G, B 컬러를 버튼의 인덱스에 따 수동으로 지정하고 싶을경우
	{
		/**
		 * 각 팔레트색(버튼)에 컬러를 직접 세팅합니다.
		 * **/
		//2015.12.16 palette 버튼 원모양으로 수정 by pjm
//		btn.setBackgroundColor(backColors[i]);

//		btn.setBackgroundResource(R.drawable.shape_roundrect);//shape_roundrect
		btn.setBackgroundResource(mContext.getResources().getIdentifier("shape_circle", "drawable", this.getContext().getPackageName()));//shape_roundrect
//		((GradientDrawable)btn.getBackground()).setColor(backColors[i]);
//		btn.setTag(""+backColors[i] );
		setButtonColorWithRound(btn, backColors[i]);

		if (this.colorTag == backColors[i]) {
			if (backColors[i]== Color.rgb(255, 255, 255))
				btn.setImageResource((mContext.getResources().getIdentifier("ico_colorpicker_selected_white", "drawable", this.getContext().getPackageName())));//shape_roundrect);
			else
				btn.setImageResource((mContext.getResources().getIdentifier("ico_colorpicker_selected", "drawable", this.getContext().getPackageName())));//shape_roundrect);
			btn.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//			btn.setScaleType(ScaleType.FIT_XY);
//			btn.setAdjustViewBounds(true);
		}

	}

	//2016. 6. 16 흰색 버튼 배경 테두리 설정 >>
	public void setButtonColorWithRound(View view, int nColor)
	{
		((GradientDrawable)view.getBackground()).setColor(nColor);
		view.setTag(""+nColor);
		if(nColor == Color.rgb(255, 255, 255))
			((GradientDrawable)view.getBackground()).setStroke((int)COMUtil.getPixel(1), Color.rgb(153, 153, 153));
		else
			((GradientDrawable)view.getBackground()).setStroke((int)COMUtil.getPixel(1), nColor);
	}
	//2016. 6. 16 흰색 버튼 배경 테두리 설정 <<

	@Override
	public void onClick(View v) {
		ImageButton btnSelected = (ImageButton)findViewById(v.getId());
		//그 버튼의 Drawable 객체를 알아옵니다. 
		currentColorDrawable = btnSelected.getBackground();
		// 색을 바꿔줍다.
		//선택한 Integer RGB값을 받아옴
		String colorRGB = (String)btnSelected.getTag();
		//Integer형 RGB값을 16진수로 변형
		int r = Color.red(Integer.parseInt(colorRGB));
		int g = Color.green(Integer.parseInt(colorRGB));
		int b = Color.blue(Integer.parseInt(colorRGB));
		String hex = String.format("#%02x%02x%02x", r, g, b);
//
//		tvColor.setBackgroundDrawable(currentColorDrawable);
//		tvColor.setTag(btnSelected.getTag());

		LinearLayout linear = (LinearLayout)tvColor.getParent();
		String tag = (String)linear.getTag();
		//TextView의 부모 태그값을 이용해 Shape형 TextView,일반 TextView 구별.
		if (tag != null){
			if (tag.equals("shape_linear")){
				setButtonColorWithRound(tvColor, Color.parseColor(hex));
//				((GradientDrawable)tvColor.getBackground()).setColor(Color.parseColor(hex));
			}else{
				tvColor.setBackgroundDrawable(currentColorDrawable);
			}
		}else{
			tvColor.setBackgroundDrawable(currentColorDrawable);
		}

		tvColor.setTag(btnSelected.getTag());

		if (strMapName.equals("Jipyo")) {
			((JipyoControlSetUI) _parent).updateValue(null);
		}
		else if (strMapName.equals("Anal")) {
			((AnalToolSettingViewController) _parent).updateValue();
		}
		else if (strMapName.equals("Base")) {
			_baseParent.updateValue();
		}
		else {
			_compParent.updateValue();
		}


		//팔레트 다이얼로그를 닫습니다.
		cancel();
	}

	public void setParent(CompareSettingController compareSettingController) {
		// TODO Auto-generated method stub
		strMapName = "Compare";
		_compParent = compareSettingController;
	}

	// 2016.05.31 기준선 대비, 색상 굵기 >>
	public void setParent(BaseLineView baseLineView) {
		// TODO Auto-generated method stub
		strMapName = "Base";
		_baseParent = baseLineView;
	}
	// 2016.05.31 기준선 대비, 색상 굵기 <<

}
