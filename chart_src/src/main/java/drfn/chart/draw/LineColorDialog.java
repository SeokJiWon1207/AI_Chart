package drfn.chart.draw;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.media.Image;
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

import androidx.core.content.ContextCompat;

import java.util.ArrayList;

import drfn.chart.base.BaseLineView;
import drfn.chart.base.CompareSettingController;
import drfn.chart.base.JipyoControlSetUI;
import drfn.chart.comp.AnalToolSettingViewController;
import drfn.chart.util.COMUtil;
import drfn.chart.util.CoSys;
import drfn.chart_src.R;

/**
 * AlertDialog.Builder 를 이용해서 메인엑티비티에서 Dialog 를 띄우니까 가로영역의 길이가 고정되 여백이 생기는 현상이 있었습니다.
 * 그래서 Dialog 객체를 상속받은 Custom Dialog 를 이용하였습니다.
 *
 * 2012. 5. 30  Developed by 김승환
 * */

public class LineColorDialog extends Dialog implements OnClickListener{
    Context mContext;
    Object _parent;
    CompareSettingController _compParent;

    String strMapName;

    // layout에서 구현해둔 TableLayout
    TableLayout colorTable;
    TableLayout lineTable;
    // layout에서 구현해둔 ScrollView
    ScrollView scrollview;
    //버튼들을 저장하는 컨테이너(ArrayList)
    ArrayList<ImageButton> arButtons;
    ArrayList<ImageButton> arLineButtons;
    ArrayList<TableRow> arTableRow;
    private int nSelectedIndex = -1;

    //현재 선택한 색상을 저장하고 있는 변수
    Drawable currentColorDrawable;
    Drawable currentLineDrawable;
    int nRow, nCol, nLineRow, nLineCol;
    public TextView tvLine;
    String chartname;
    //메인액티비티에서 넘겨진 색상을 변경할 TextView 입니다.
    public TextView tvColor;

    //메인에서 가져온 색상
    public int colorTag;
    public int lineTag;

    BaseLineView _baseParent;  // 2016.05.31 기준선 대비, 색상 굵기
    Display display;

    public LineColorDialog(Context context, int nRow, int nCol, TextView tvColor, int colorTag, TextView tvLine, int lineTag)
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
        int layoutResId;
        if (COMUtil.getSkinType()!= COMUtil.SKIN_BLACK) {
            layoutResId = context.getResources().getIdentifier("linecolorchangepalette", "layout", this.getContext().getPackageName());
        } else {
            layoutResId = context.getResources().getIdentifier("linecolorchangepalette_dark", "layout", this.getContext().getPackageName());
        }

        LinearLayout ll = (LinearLayout)factory.inflate(layoutResId, null);
//		layoutResId = context.getResources().getIdentifier("palette_title_view", "id", context.getPackageName());
//		TextView tv = (TextView)ll.findViewById(layoutResId);
//		tv.setText("색상선택");

        layoutResId = context.getResources().getIdentifier("color_change_scrollview", "id", context.getPackageName());
        scrollview = (ScrollView)ll.findViewById(layoutResId);
        // scrollview.setBackgroundColor(Color.BLACK);
//		setContentView(ll);

        //2020.05.08 by JJH >> 가로모드 작업 (색상 설정 팝업) start
//		setContentView(ll, new ViewGroup.LayoutParams(COMUtil.g_nDisWidth, (int) COMUtil.getPixel(343)));	//size change

        layoutResId = context.getResources().getIdentifier("line_change_scrollview", "id", context.getPackageName());
        scrollview = (ScrollView)ll.findViewById(layoutResId);


        display = COMUtil._chartMain.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        Configuration config = COMUtil.apiView.getContext().getResources().getConfiguration();
        if(config.orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            //가로
            setContentView(ll, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));	//size change
            //setContentView(ll, new ViewGroup.LayoutParams((int) COMUtil.getPixel_W(324), size.y - (int) COMUtil.getPixel(51)));	//size change
//			setContentView(ll, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, size.y - (int) COMUtil.getPixel(51)));	//size change
        }else{
            //세로
            //setContentView(ll, new ViewGroup.LayoutParams((int) COMUtil.getPixel(324), (int) COMUtil.getPixel(288)));	//size change
//			setContentView(ll, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) COMUtil.getPixel(288)));	//size change
            setContentView(ll, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        layoutResId = this.getContext().getResources().getIdentifier("btn_confirm", "id", this.getContext().getPackageName());
        Button btnConfirm = (Button)findViewById(layoutResId);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save_change();
            }
        });

        layoutResId = this.getContext().getResources().getIdentifier("btn_popup_close", "id", this.getContext().getPackageName());
        Button btnClose = (Button)findViewById(layoutResId);
        btnClose.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                try {
                    //이전화면으로 이동.
                    //Message msg = new Message();
                    dismiss();
                }
                catch (Exception e) {
                }
            }
        });
        //2020.05.08 by JJH >> 가로모드 작업 (색상 설정 팝업) end

        this.nRow = nRow;
        this.nCol = nCol;
        this.nLineRow = 1;
        this.nLineCol = 4;
        this.tvColor = tvColor;
        this.colorTag = colorTag;
        this.tvLine = tvLine;
        this.lineTag = lineTag;

        currentColorDrawable = tvColor.getBackground();
        currentLineDrawable = tvLine.getBackground();

        initColorTable();
        initLineTable();

//        COMUtil.setGlobalFont(ll);
    }

//    int[] backColors = {
//            Color.rgb(250, 74, 106),
//            Color.rgb(255, 161, 39),
//            Color.rgb(115, 211, 2),
//            Color.rgb(16, 172, 211),
//            Color.rgb(67, 140, 240),
//            Color.rgb(136, 111, 217), //6
//            Color.rgb(247, 77, 153),
//            Color.rgb(255, 121, 40),
//            Color.rgb(51, 187, 62),
//            Color.rgb(59, 156, 207),
//            Color.rgb(72, 108, 209),
//            Color.rgb(166, 102, 226),  //12
//            Color.rgb(232, 48, 174),
//            Color.rgb(215, 96, 49),
//            Color.rgb(58 ,180, 127),
//            Color.rgb(16, 178, 161),
//            Color.rgb(92, 97, 176),
//            Color.rgb(195, 72, 231), //18
//            Color.rgb(255, 255, 255),
//            Color.rgb(208, 208, 208),
//            Color.rgb(159, 159, 159),
//            Color.rgb(105, 105, 105),
//            Color.rgb(57, 57, 57),
//            Color.rgb(17, 17, 17), //24
//            Color.rgb(17, 17, 17),
//            Color.rgb(17, 17, 17),
//            Color.rgb(17, 17, 17),
//            Color.rgb(17, 17, 17), // 28 //2023.10.18 by CYJ - 카카오페이 색상 팔레트 추가 임시 색상
//    };

    int[] backColors = {
            ContextCompat.getColor(getContext(), R.color.kfit_grey900), ContextCompat.getColor(getContext(), R.color.kfit_blue900), ContextCompat.getColor(getContext(), R.color.kfit_purple400), ContextCompat.getColor(getContext(), R.color.kfit_red900), ContextCompat.getColor(getContext(), R.color.kfit_yellow400), ContextCompat.getColor(getContext(), R.color.kfit_brown400), ContextCompat.getColor(getContext(), R.color.kfit_green500),
            ContextCompat.getColor(getContext(), R.color.kfit_grey700), ContextCompat.getColor(getContext(), R.color.kfit_blue700), ContextCompat.getColor(getContext(), R.color.kfit_purple300), ContextCompat.getColor(getContext(), R.color.kfit_red700), ContextCompat.getColor(getContext(), R.color.kfit_yellow300), ContextCompat.getColor(getContext(), R.color.kfit_brown300), ContextCompat.getColor(getContext(), R.color.kfit_green400),
            ContextCompat.getColor(getContext(), R.color.kfit_grey500), ContextCompat.getColor(getContext(), R.color.kfit_blue200), ContextCompat.getColor(getContext(), R.color.kfit_purple200), ContextCompat.getColor(getContext(), R.color.kfit_red300), ContextCompat.getColor(getContext(), R.color.kfit_yellow200), ContextCompat.getColor(getContext(), R.color.kfit_brown200), ContextCompat.getColor(getContext(), R.color.kfit_green300),
            ContextCompat.getColor(getContext(), R.color.kfit_grey300), ContextCompat.getColor(getContext(), R.color.kfit_blue100), ContextCompat.getColor(getContext(), R.color.kfit_purple100), ContextCompat.getColor(getContext(), R.color.kfit_red100), ContextCompat.getColor(getContext(), R.color.kfit_yellow100), ContextCompat.getColor(getContext(), R.color.kfit_brown100), ContextCompat.getColor(getContext(), R.color.kfit_green200),



//            CoSys.colorPalette[0],CoSys.colorPalette[1], CoSys.colorPalette[2], CoSys.colorPalette[3],CoSys.colorPalette[4],CoSys.colorPalette[5],CoSys.colorPalette[6],
//            CoSys.colorPalette[7], CoSys.colorPalette[8],CoSys.colorPalette[9],CoSys.colorPalette[10],CoSys.colorPalette[11],CoSys.colorPalette[12],CoSys.colorPalette[13],
//            CoSys.colorPalette[14],CoSys.colorPalette[15], CoSys.colorPalette[16],CoSys.colorPalette[17],CoSys.colorPalette[18],CoSys.colorPalette[19],CoSys.colorPalette[20],
//            CoSys.colorPalette[21],CoSys.colorPalette[22],CoSys.colorPalette[23], CoSys.colorPalette[24],CoSys.colorPalette[25],CoSys.colorPalette[26],CoSys.colorPalette[27],
    };

    //	{
//		/**
//		 * 각 팔레트색(버튼)에 컬러를 직접 세팅합니다.
//		 * **/
//		btn.setBackgroundColor(backColors[i]);
//		btn.setTag(""+backColors[i] );
//	}
    int[] LineSet = { this.getContext().getResources().getIdentifier("kfits_btn_line_width1", "drawable", this.getContext().getPackageName()),
            this.getContext().getResources().getIdentifier("kfits_btn_line_width2", "drawable", this.getContext().getPackageName()),
            this.getContext().getResources().getIdentifier("kfits_btn_line_width3", "drawable", this.getContext().getPackageName()),
            this.getContext().getResources().getIdentifier("kfits_btn_line_width4", "drawable", this.getContext().getPackageName())};

    int[] LineSet_dark = { this.getContext().getResources().getIdentifier("kfits_btn_line_width1_dark", "drawable", this.getContext().getPackageName()),
            this.getContext().getResources().getIdentifier("kfits_btn_line_width2_dark", "drawable", this.getContext().getPackageName()),
            this.getContext().getResources().getIdentifier("kfits_btn_line_width3_dark", "drawable", this.getContext().getPackageName()),
            this.getContext().getResources().getIdentifier("kfits_btn_line_width4_dark", "drawable", this.getContext().getPackageName())};
    int[] Lines = { this.getContext().getResources().getIdentifier("kfits_btn_line_width_nor1", "drawable", this.getContext().getPackageName()),
            this.getContext().getResources().getIdentifier("kfits_btn_line_width_nor2", "drawable", this.getContext().getPackageName()),
            this.getContext().getResources().getIdentifier("kfits_btn_line_width_nor3", "drawable", this.getContext().getPackageName()),
            this.getContext().getResources().getIdentifier("kfits_btn_line_width_nor4", "drawable", this.getContext().getPackageName())};

    int[] Lines_selected = { this.getContext().getResources().getIdentifier("kfits_btn_line_width_sel1", "drawable", this.getContext().getPackageName()),
            this.getContext().getResources().getIdentifier("kfits_btn_line_width_sel2", "drawable", this.getContext().getPackageName()),
            this.getContext().getResources().getIdentifier("kfits_btn_line_width_sel3", "drawable", this.getContext().getPackageName()),
            this.getContext().getResources().getIdentifier("kfits_btn_line_width_sel4", "drawable", this.getContext().getPackageName())};


    private void initColorTable()  //테이블 초기화
    {
        // 테이블을 불러와서 초기화합니다.
        int layoutResId = mContext.getResources().getIdentifier("color_change_tablelayout", "id", this.getContext().getPackageName());
        colorTable = (TableLayout)findViewById(layoutResId);

        //기존 데이터가 있을시 리셋합니다,
        if(colorTable.getChildCount() > 0)
        {
            colorTable.removeAllViews();
        }

        // 팔레트색 버튼들을 초기화합니다.
        initColorButtons();

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
            colorTable.addView(tr);
        }
    }


    private void initColorButtons()  //팔레트색(버튼) 초기화
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
            setColorButtonCheck(btnAdd, i);

            //버튼의 가로 세로 길이를 지정합니다.
//    		btnAdd.setWidth((int)COMUtil.getPixel(40));
//    		btnAdd.setHeight((int)COMUtil.getPixel(25));

            //클릭 리스너 지정
            btnAdd.setOnClickListener(this);

            // 버튼의 Margin을 지정합니다.
            int nColorBtnSize = (int)COMUtil.getPixel_W(32);
            TableRow.LayoutParams trLayout = new TableRow.LayoutParams(nColorBtnSize, nColorBtnSize);

            int nSideMargin = (int)COMUtil.getPixel_W(24);
            int nBtnMargin = (display.getWidth() - nSideMargin - nColorBtnSize * nCol) / nCol - 1;
            //trLayout.setMargins((int)COMUtil.getPixel(0), (int)COMUtil.getPixel(0), nBtnMargin, (int)COMUtil.getPixel(14));
            trLayout.setMargins(nBtnMargin/2, (int)COMUtil.getPixel(0), nBtnMargin/2, (int)COMUtil.getPixel(14));
//            int nBtnMargin = (display.getWidth() - nSideMargin - nColorBtnSize * nCol) / nCol - 1;
//            trLayout.setMargins(nBtnMargin/2, (int)COMUtil.getPixel_W(0), nBtnMargin/2, (int)COMUtil.getPixel_W(14));
            btnAdd.setLayoutParams(trLayout);
            //btnAdd.setPadding(0, 0, 0, 0);
            btnAdd.setPadding((int)COMUtil.getPixel_W(3), (int)COMUtil.getPixel_W(4), (int)COMUtil.getPixel_W(5),(int)COMUtil.getPixel_W(3));

            //ArrayList 에 버튼을 저장합니다.
            arButtons.add(btnAdd);
        }
    }

    private void setColorButtonCheck(ImageButton btn, int i)   // R, G, B 컬러를 버튼의 인덱스에 따 수동으로 지정하고 싶을경우
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
            if (backColors[i] == Color.rgb(238, 239, 240) && COMUtil.getSkinType() == COMUtil.SKIN_BLACK) {
                btn.setImageResource((mContext.getResources().getIdentifier("kfit_core_ic_common_checkbox_d", "drawable", this.getContext().getPackageName())));//shape_roundrect);
            } else {
                 btn.setImageResource((mContext.getResources().getIdentifier("kfit_core_ic_common_checkbox", "drawable", this.getContext().getPackageName())));//shape_roundrect);
            }
//            btn.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			btn.setScaleType(ImageView.ScaleType.FIT_XY);
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

    private void initLineTable()
    {
        int layoutResId = this
                .getContext()
                .getResources()
                .getIdentifier("line_change_tablelayout", "id",
                        this.getContext().getPackageName());
        lineTable = (TableLayout) findViewById(layoutResId);
        arTableRow = new ArrayList<TableRow>();

        // 기존 데이터가 있을시 리셋합니다,
        if (lineTable.getChildCount() > 0) {
            lineTable.removeAllViews();
        }

        // 팔레트 라인 버튼들을 초기화합니다.
        initLineButtons();

        // 불러올 ArrayList 의 인덱스로 사용될 변수입니다.
        int nArrayListIdx = 0;

        for (int i = 0; i < nLineRow; i++) {
            // TableLayout 에 들어가는 TableRow 를 생성합니다.
            final TableRow tr = new TableRow(mContext);
            final int idx = i;
            // TableRow 에 들어갈 팔레트라인(버튼) 을 넣습니다.
            for (int j = 0; j < nLineCol; j++) {
                if (nArrayListIdx < nLineRow * nLineCol)
                    tr.addView(arLineButtons.get(nArrayListIdx++));
                else
                    break;
            }

            tr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    arLineButtons.get(idx).performClick();
                }
            });
//			arTableRow.add(tr);
            lineTable.addView(tr);
        }
    }

    private void initLineButtons() // 라인 굵기
    {
        // Button 들의 컨테이너로 쓰일 ArrayList 초기화합니다.
        arLineButtons = new ArrayList<ImageButton>();

        int nLineCnt = 4;
        for (int i = 0; i < nLineCnt; i++) {
            ImageButton btnLineAdd = new ImageButton(mContext);

            // 버튼의 id를 정해줍니다.안 정해줄시 nullpointer 예외 발생하므로 필수입니다.
            btnLineAdd.setId(i);

            // 라인의 굵기를 세팅합니다.
            if( i == lineTag-1)
                selLineButton(btnLineAdd, lineTag - 1);
            else
                setLineButton(btnLineAdd, i);

            btnLineAdd.setOnClickListener(this);

            int nSideMargin = (int)COMUtil.getPixel_W(24);
            int nMargin = (int)COMUtil.getPixel_W(9);
            TableRow.LayoutParams trLayout = new TableRow.LayoutParams();
            trLayout.setMargins(0, 0, nMargin, 0);

//            trLayout.width = (int)COMUtil.getPixel_W(75);
            trLayout.width = (display.getWidth() - (nSideMargin * 2 + (nLineCnt - 1) * nMargin)) /nLineCnt;
            trLayout.height = (int)COMUtil.getPixel_H(60);

            btnLineAdd.setLayoutParams(trLayout);

            arLineButtons.add(btnLineAdd);
        }
    }

    private void setLineButton(ImageButton btn, int i) {
        // 일반 버튼 값 저장
        btn.setTag(String.valueOf(i + 1));
//        btn.setBackgroundResource(Lines_normal[i]);
        if (COMUtil.getSkinType()!= COMUtil.SKIN_BLACK) {
            btn.setBackgroundResource(LineSet[i]);
        } else {
            btn.setBackgroundResource(LineSet_dark[i]);
        }
        btn.setSelected(false);
    }

    private void selLineButton(ImageButton btn, int i) {
        btn.setTag(String.valueOf(i + 1));
//        btn.setBackgroundResource(Lines_selected[i]);
        if (COMUtil.getSkinType()!= COMUtil.SKIN_BLACK) {
            btn.setBackgroundResource(LineSet[i]);
        } else {
            btn.setBackgroundResource(LineSet_dark[i]);
        }
        btn.setSelected(true);
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

    @Override
    public void onClick(View v) {
        // 라인굵기 테이블 선택 시
        if(v.getParent().getParent().equals(lineTable)) {
            ImageButton btnSelected = (ImageButton)lineTable.findViewById(v.getId());
            currentLineDrawable = btnSelected.getBackground();
            String m_lineTag = (String) btnSelected.getTag();
            nSelectedIndex = (Integer.parseInt(m_lineTag));

//			if (m_lineTag != null) {
//				if (m_lineTag.equals("shape_linear")) {
//					tvLine.invalidate();
//				} else {
//					//tvLine.setBackgroundDrawable(currentLineDrawable);
//					tvLine.setBackgroundResource(Lines[nSelectedIndex - 1]);
//				}
//			} else {
//				tvLine.setBackgroundDrawable(currentLineDrawable);
//			}

            for(int i = 0; i < 4; i++) {
                ImageButton btnLineWidth = (ImageButton) lineTable.findViewById(i);
                if(i == nSelectedIndex - 1) {
                    selLineButton(btnSelected, nSelectedIndex - 1);
                } else {
                    setLineButton(btnLineWidth, i);
                }
            }

            //tvLine.setTag(btnSelected.getTag());
            lineTag = Integer.parseInt(btnSelected.getTag().toString());
        }


        // 라인컬러 테이블 선택 시
        if(v.getParent().getParent().equals(colorTable)) {
            //그 버튼의 Drawable 객체를 알아옵니다.
            ImageButton btnSelected = (ImageButton)colorTable.findViewById(v.getId());

            currentColorDrawable = btnSelected.getBackground();
            // 색을 바꿔줍다.
            //선택한 Integer RGB값을 받아옴
            String colorRGB = (String) btnSelected.getTag();
            //Integer형 RGB값을 16진수로 변형
            int r = Color.red(Integer.parseInt(colorRGB));
            int g = Color.green(Integer.parseInt(colorRGB));
            int b = Color.blue(Integer.parseInt(colorRGB));
            String hex = String.format("#%02x%02x%02x", r, g, b);
//
//		tvColor.setBackgroundDrawable(currentColorDrawable);
//		tvColor.setTag(btnSelected.getTag());

//			LinearLayout linear = (LinearLayout) tvColor.getParent();
//			String tag = (String) linear.getTag();
//			//TextView의 부모 태그값을 이용해 Shape형 TextView,일반 TextView 구별.
//			if (tag != null) {
//				if (tag.equals("shape_linear")) {
//					setButtonColorWithRound(tvColor, Color.parseColor(hex));
////				((GradientDrawable)tvColor.getBackground()).setColor(Color.parseColor(hex));
//				} else {
//					tvColor.setBackgroundDrawable(currentColorDrawable);
//				}
//			} else {
//				tvColor.setBackgroundDrawable(currentColorDrawable);
//			}

            colorTag = Integer.parseInt(btnSelected.getTag().toString());

            initColorTable();

            //라인버튼 클릭시 바로 적용 >>
//			tvColor.setTag(btnSelected.getTag());
//			if (strMapName.equals("Jipyo")) {
//				((JipyoControlSetUI) _parent).updateValue(null);
//			} else if (strMapName.equals("Anal")) {
//				((AnalToolSettingViewController) _parent).updateValue();
//			} else if (strMapName.equals("Base")) {
//				_baseParent.updateValue();
//			} else {
//				_compParent.updateValue();
//			}
            //라인버튼 클릭시 바로 적용 <<
        }

        //팔레트 다이얼로그를 닫습니다.
        //cancel();
    }

    public void save_change() {
        String strColorTag = String.valueOf(colorTag);
        tvColor.setTag(strColorTag);
        tvColor.setBackgroundDrawable(currentColorDrawable);

        String strLineTag = String.valueOf(lineTag);
        tvLine.setTag(strLineTag);
        //tvLine.setBackgroundResource(Lines[lineTag - 1]);
        tvLine.setText(lineTag + "px");

        if (strMapName.equals("Jipyo")) {
            //2023.11.15 by lyk - 카카오페이 캔들차트 개선 - "확인" 누를 경우에만 변경된 값 저장 >>
//            ((JipyoControlSetUI) _parent).updateValue(null);
            //2023.11.15 by lyk - 카카오페이 캔들차트 개선 - "확인" 누를 경우에만 변경된 값 저장 <<
        } else if (strMapName.equals("Anal")) {
            ((AnalToolSettingViewController) _parent).updateValue();
        } else if (strMapName.equals("Base")) {
            _baseParent.updateValue();
        } else {
            _compParent.updateValue();
        }
        dismiss();
    }
}
