package drfn.chart.draw;

import java.util.ArrayList;

import android.R.drawable;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import drfn.chart.base.BaseLineView;
import drfn.chart.base.JipyoControlSetUI;
import drfn.chart.comp.AnalToolSettingViewController;
import drfn.chart.util.COMUtil;
import drfn.chart.base.CompareSettingController;
import drfn.chart_src.R;

/**
 * AlertDialog.Builder 를 이용해서 메인엑티비티에서 Dialog 를 띄우니까 가로영역의 길이가 고정되 여백이 생기는 현상이
 * 있었습니다. 그래서 Dialog 객체를 상속받은 Custom Dialog 를 이용하였습니다.
 * 
 * 2012. 5. 30 Developed by 김승환
 * */

public class LineDialog extends Dialog implements OnClickListener {
	Context mContext;
	Object _parent;
	CompareSettingController _compparent;
	// layout에서 구현해둔 TableLayout
	TableLayout table;
	// layout에서 구현해둔 ScrollView
	ScrollView scrollview;
	// 버튼들을 저장하는 컨테이너(ArrayList)
	ArrayList<Button> arButtons;
	ArrayList<ImageView> arSelChk;
	// 선택된버튼들을 저장하는 컨테이너(ArrayList)
	ArrayList<TextView> arTextView;
	ArrayList<TableRow> arTableRow;
	
	String chartname;

	ImageView closebtn;

	Drawable currentLineDrawable;

	int nRow, nCol;

	// 메인액티비티에서 넘겨진 색상을 변경할 TextView 입니다.
	TextView tvLine;
	LinearLayout ll;
	
	private int nSelectedIndex = -1;

	BaseLineView _baseParent; // 2016.05.31 기준선 대비, 색상 굵기

	public LineDialog(Context context, int nRow, int nCol, TextView tvLine, int selectedTag) {
		super(context,context.getResources().getIdentifier("alert_layout_bottom", "style", context.getPackageName()));
		mContext = context;

		// 타이틀바가 없는 다이얼로그로 세팅합니다.
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		LayoutInflater factory = LayoutInflater.from(context);
		int layoutResId = context.getResources().getIdentifier(
				"thickchangepalette", "layout", context.getPackageName());
		ll = (LinearLayout) factory.inflate(layoutResId, null);
		layoutResId = context.getResources().getIdentifier(
				"color_change_scrollview", "id", context.getPackageName());

		scrollview = (ScrollView) ll.findViewById(layoutResId);
		scrollview.setBackgroundColor(Color.WHITE);
//		setContentView(ll);
		//2020.05.08 by JJH >> 가로모드 작업 (굵기 설정 팝업) start
//		setContentView(ll, new ViewGroup.LayoutParams(COMUtil.g_nDisWidth, (int) COMUtil.getPixel(383)));	//size change

		Display display = COMUtil._chartMain.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);

		Configuration config = COMUtil.apiView.getContext().getResources().getConfiguration();
		if(config.orientation == Configuration.ORIENTATION_LANDSCAPE)
		{
			//가로
//			setContentView(ll, new ViewGroup.LayoutParams((int) COMUtil.getPixel(360), ViewGroup.LayoutParams.WRAP_CONTENT));	//size change
			setContentView(ll, new ViewGroup.LayoutParams((int) COMUtil.getPixel(324), size.y - (int) COMUtil.getPixel(51)));	//size change
		}else{
			//세로
			setContentView(ll, new ViewGroup.LayoutParams((int) COMUtil.getPixel(324), (int) COMUtil.getPixel(374)));	//size change
		}
		//2020.05.08 by JJH >> 가로모드 작업 (굵기 설정 팝업) end

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

//		layoutResId = this
//				.getContext()
//				.getResources()
//				.getIdentifier("btn_close", "id",
//						this.getContext().getPackageName());
//		ImageView btnBack = (ImageView) findViewById(layoutResId);
//
//		btnBack.setOnClickListener(new ImageView.OnClickListener() {
//			public void onClick(View v) {
//				try {
//					// 이전화면으로 이동.
//					Message msg = new Message();
//					dismiss();
//				} catch (Exception e) {
//				}
//			}
//		});

		this.nRow = nRow;
		this.nCol = nCol;
		this.tvLine = tvLine;
//		this.m_pttv = pttv;
		this.nSelectedIndex = selectedTag;

		initTable();

		COMUtil.setGlobalFont(ll);
	}

	private void initButtons() // 라인 굵기
	{
		// Button 들의 컨테이너로 쓰일 ArrayList 초기화합니다.
		arButtons = new ArrayList<Button>();
		arSelChk = new ArrayList<ImageView>();

		
		// 버튼과 텍스트 10개 생성후 ArrayList 에 넣습니다.
		for (int i = 0; i < nRow * nCol; i++) {
			Button btnAdd = new Button(mContext);
			ImageView ivSel = new ImageView(mContext);
			
			// 버튼의 id를 정해줍니다.안 정해줄시 nullpointer 예외 발생하므로 필수입니다.
			btnAdd.setId(i);
			ivSel.setId(i+100);

			// 버튼의 색상을 세팅합니다.
			if( i == (nSelectedIndex-1))
			    selButtonLine(btnAdd, i);
			else
			    setButtonLine(btnAdd, i);

			int left;
			int right;
			// 버튼의 가로 세로 길이를 지정합니다.
//			if(i % 2 == 0){
//				btnAdd.setWidth((int) COMUtil.getPixel(80));
//			}
//			else{
				btnAdd.setWidth((int) COMUtil.getPixel_W(120));
//			}
			btnAdd.setHeight((int) COMUtil.getPixel_H(52));
			// 클릭 리스너 지정
			btnAdd.setOnClickListener(this);

			// 버튼의 Margin을 지정합니다.
			TableRow.LayoutParams trLayout = new TableRow.LayoutParams();
			trLayout.setMargins((int)COMUtil.getPixel_W(18), 0, 0, 0);

			btnAdd.setLayoutParams(trLayout);

			TableRow.LayoutParams ivSelParam = new TableRow.LayoutParams();
			ivSelParam.width = (int) COMUtil.getPixel_H(32);
			ivSelParam.height = (int) COMUtil.getPixel_H(32);
			ivSelParam.setMargins((int)COMUtil.getPixel_W(136), 0, 0, 0);
			ivSelParam.gravity = Gravity.CENTER_VERTICAL;

			ivSel.setLayoutParams(ivSelParam);
			ivSel.setBackgroundResource(this.getContext().getResources().getIdentifier("ico_thick_checked", "drawable", this.getContext().getPackageName()));

			// ArrayList 에 버튼을 저장합니다.
			arButtons.add(btnAdd);
			arSelChk.add(ivSel);
		}
	}

	int[] Lines = {
			this.getContext().getResources().getIdentifier("line_width01", "drawable", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("line_width02", "drawable", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("line_width03", "drawable", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("line_width04", "drawable", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("line_width05", "drawable", this.getContext().getPackageName()),
/*			drfn.chart.R.drawable.line_width06*/ };

	int[] Lines_drop = { this.getContext().getResources().getIdentifier("line_width01_drop_nor", "drawable", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("line_width02_drop_nor", "drawable", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("line_width03_drop_nor", "drawable", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("line_width04_drop_nor", "drawable", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("line_width05_drop_nor", "drawable", this.getContext().getPackageName())};

	int[] SelectLines_drop = { this.getContext().getResources().getIdentifier("line_width01_drop_sel", "drawable", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("line_width02_drop_sel", "drawable", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("line_width03_drop_sel", "drawable", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("line_width04_drop_sel", "drawable", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("line_width05_drop_sel", "drawable", this.getContext().getPackageName())};


//	private void setButtonLine(Button btn, int i) {
//		// 일반 버튼 값 저장
//		int index;
//		btn.setTag(String.valueOf(i + 1));
//		if(i % 2 == 0 ){
//			index = i / 2 ;
//			btn.setBackgroundResource(Lines[index]);
//		}
//		else
//		{
//			index = i / 2 +1;
//			String strBtnText = String.format("%d pt", index);
//			btn.setBackgroundColor(Color.WHITE);
//			btn.setTextColor(Color.BLACK);
//			btn.setText(strBtnText);
//		}
//
//	}
	private void setButtonLine(Button btn, int i) {
		// 일반 버튼 값 저장
		btn.setTag(String.valueOf(i + 1));
//		btn.setBackgroundResource(Lines[i]);
		btn.setBackgroundResource(Lines_drop[i]);
//		TableRow tr = (TableRow)ll.findViewWithTag(1000+i);
//		tr.setBackgroundColor(Color.WHITE);
	}

//	private void selButtonLine(Button btn, int i) {
//		// 선택된 버튼 체크이미지
//		int index;
//		if(i % 2 == 0 ){
//			index = i / 2 ;
//			btn.setBackgroundResource(Lines[index]);
//		}
//		else
//		{
//			index = i / 2 +1;
//			String strBtnText = String.format("%d pt", index);
//			btn.setBackgroundColor(Color.WHITE);
//			btn.setTextColor(Color.BLACK);
//			btn.setText(strBtnText);
//		}
//	}

	private void selButtonLine(Button btn, int i) {
		// 선택된 버튼 체크이미지
//		btn.setTag(String.valueOf(i + 1));
//		if(COMUtil.getSkinType() == COMUtil.SKIN_BLACK){
//			btn.setBackgroundResource(Lines[i]);
//		}else{
//			btn.setBackgroundResource(Lines[i]);
//		}
		btn.setBackgroundResource(SelectLines_drop[i]);
//		TableRow tr = (TableRow) ll.findViewWithTag(1000+i);
//		tr.setBackgroundColor(Color.rgb(249,249,250));
	}

	private void initTable() // 테이블 초기화
	{
		// 테이블을 불러와서 초기화합니다.
		/*int layoutResId = this
				.getContext()
				.getResources()
				.getIdentifier("line_change_table", "id",
						this.getContext().getPackageName());
		table = (TableLayout) findViewById(layoutResId);*/

		int layoutResId = this
				.getContext()
				.getResources()
				.getIdentifier("color_change_tablelayout", "id",
						this.getContext().getPackageName());
		table = (TableLayout) findViewById(layoutResId);

		arTableRow = new ArrayList<TableRow>();


		// 기존 데이터가 있을시 리셋합니다,
		if (table.getChildCount() > 0) {
			table.removeAllViews();
		}

		// 팔레트 라인 버튼들을 초기화합니다.
		initButtons();

		// 불러올 ArrayList 의 인덱스로 사용될 변수입니다.
		int nArrayListIdx = 0;

		for (int i = 0; i < nRow; i++) {
			// TableLayout 에 들어가는 TableRow 를 생성합니다.
			final TableRow tr = new TableRow(mContext);
			final int idx = i;
			// TableRow 에 들어갈 팔레트라인(버튼) 을 넣습니다.
			for (int j = 0; j < nCol; j++) {
				if (nArrayListIdx < nRow * nCol) {
					tr.addView(arButtons.get(nArrayListIdx++));

				} else
					break;
			}
			// 세팅이 끝난 TableRow 를 TableLayout 에 넣어서 한 행을 완성합니다.
			if(i==nSelectedIndex-1)
				tr.addView(arSelChk.get(nSelectedIndex-1));

			tr.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					arButtons.get(idx).performClick();
				}
			});
//			arTableRow.add(tr);
			table.addView(tr);
		}
	}

	public void setParent(Object parent, String chartname) {
		this.chartname = chartname;
		_parent = parent;
	}

	public void setParent(JipyoControlSetUI parent) {
		chartname = "Jipyo";
		_parent = parent;
	}

	@Override
	public void onClick(View v) {
		if( nSelectedIndex >= 0 )
			setButtonLine(arButtons.get(nSelectedIndex-1), nSelectedIndex-1);
		Button btnSelected = (Button) findViewById(v.getId());
		// 그 버튼의 Drawable 객체를 알아옵니다.
		currentLineDrawable = btnSelected.getBackground();
		String lineThick = (String) btnSelected.getTag();
		nSelectedIndex = (Integer.parseInt(lineThick));
		String tag = (String) btnSelected.getTag();
		// TextView의 부모 태그값을 이용해 Shape형 TextView,일반 TextView 구별.
		if (tag != null) {
			if (tag.equals("shape_linear")) {
				tvLine.invalidate();
			} else {
//				tvLine.setBackgroundDrawable(currentLineDrawable);
				tvLine.setBackgroundResource(Lines[nSelectedIndex-1]);
			}
		} else {
			tvLine.setBackgroundDrawable(currentLineDrawable);
		}
		selButtonLine(btnSelected, nSelectedIndex-1);
		tvLine.setTag(btnSelected.getTag());

		if (chartname.equals("Jipyo")) {
			((JipyoControlSetUI) _parent).updateValue(null);
		}
		else if (chartname.equals("Anal")) {
			((AnalToolSettingViewController) _parent).updateValue();
		}
		// 2016.05.31 기준선 대비, 색상 굵기 >>
		else if (chartname.equals("Base")) {
			_baseParent.updateValue();
		}
		// 2016.05.31 기준선 대비, 색상 굵기 <<
		else {
			_compparent.updateLineValue();
		}
		 cancel();
	}

	public void setParent(CompareSettingController compparent) {
		// TODO Auto-generated method stub
		chartname = "Compare";
		_compparent = compparent;
	}

	// 2016.05.31 기준선 대비, 색상 굵기 >>
	public void setParent(BaseLineView compparent) {
		// TODO Auto-generated method stub
		chartname = "Base";
		_baseParent = compparent;
	}
	// 2016.05.31 기준선 대비, 색상 굵기 <<


}
