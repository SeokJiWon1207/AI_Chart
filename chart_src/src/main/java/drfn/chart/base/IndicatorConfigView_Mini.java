package drfn.chart.base;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import drfn.chart.base.JipyoTabViewController.TabType;
import drfn.chart.util.COMUtil;
import drfn.chart.util.COMUtil.OnPopupEventListener;

public class IndicatorConfigView_Mini{
    Button m_btn_showIndicator;
    Button m_btn_initChart;
    Button m_btn_close;

    String str_bgChartListClose, str_bgChartListOpen;
    Context context;
    RelativeLayout layout=null;
    LinearLayout ll=null;
    OnPopupEventListener m_miniViewEventListener;

    public IndicatorConfigView_Mini(Context context, final RelativeLayout layout, RelativeLayout.LayoutParams params, int triXpos) {
//		super(context, layout, triXpos);

//		if(COMUtil.skinType == COMUtil.SKIN_BLACK){
//			textColor = Color.rgb(255, 255, 255);
//			selectTextColor = Color.rgb(255, 255, 255);
//		}
//		else{
//			textColor =Color.rgb(46, 48, 51);
//			selectTextColor = Color.BLACK;
//		}
//
//		m_bIsMiniPopup = true;
        this.context = context;
        this.layout = layout;


//		LinearLayout indicatorLayout = new LinearLayout(context);
//		indicatorLayout.setTag(COMUtil.INDICATOR_LAYOUT);
//		RelativeLayout.LayoutParams bgparams =new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
//		indicatorLayout.setLayoutParams(bgparams);
//		indicatorLayout.setBackgroundColor(Color.BLACK);
//		indicatorLayout.getBackground().setAlpha(85);

//        if(COMUtil.skinType == COMUtil.SKIN_BLACK){
//            str_bgChartListClose = "bg_chart_list_close_black" ;
//            str_bgChartListOpen = "bg_chart_list_open_black" ;
//        }
//        else{
            str_bgChartListClose = "bg_chart_list_close" ;
            str_bgChartListOpen = "bg_chart_list_open" ;
//        }

        //indicatorLayout.setAlpha(0.2f);
//		indicatorLayout.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				close();
//			}
//		});

        LayoutInflater factory = LayoutInflater.from(context);
        int layoutResId = context.getResources().getIdentifier("indicatorview_mini", "layout", context.getPackageName());

        ll = (LinearLayout)factory.inflate(layoutResId, null);
        ll.setTag("indicatorView");
        ll.setLayoutParams(params);
//		layout.addView(indicatorLayout);
        layout.addView(ll);

        final Context fContext = context;
        //2017. 8. 1 by hyh - 지표설정 UI 변경 >>
        layoutResId = context.getResources().getIdentifier("ll_jipyo_tab_view", "id", context.getPackageName());
        final LinearLayout llJipyoTabView = (LinearLayout)ll.findViewById(layoutResId);

        new Handler().post(new Runnable() {
            public void run() {
                new JipyoTabViewController(fContext, llJipyoTabView, layout, TabType.MiniType);
            }
        });
        //2017. 8. 1 by hyh - 지표설정 UI 변경 <<

        //지표설정 버튼 : 전체지표설정창을 띄움
        layoutResId = context.getResources().getIdentifier("indicatormini_btn_showindicator", "id", context.getPackageName());
        m_btn_showIndicator = (Button) ll.findViewById(layoutResId);
        m_btn_showIndicator.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showIndicator();
            }
        });

        //초기화 버튼
        layoutResId = context.getResources().getIdentifier("indicatormini_btn_initchart", "id", context.getPackageName());
        m_btn_initChart = (Button) ll.findViewById(layoutResId);
        m_btn_initChart.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                initChart();
            }
        });

        //닫기 버튼
        layoutResId = context.getResources().getIdentifier("indicatormini_btn_close", "id", context.getPackageName());
        m_btn_close = (Button) ll.findViewById(layoutResId);
        m_btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });

        //비교차
        layoutResId = context.getResources().getIdentifier("btn_sub_tab1", "id", context.getPackageName());
        Button btn_compare = (Button) ll.findViewById(layoutResId);
        btn_compare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openScreen("비교");
            }
        });

        layoutResId = context.getResources().getIdentifier("btn_sub_tab2", "id", context.getPackageName());
        Button btn_draw = (Button) ll.findViewById(layoutResId);
        btn_draw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openScreen("그리기종목");
            }
        });

        COMUtil.setGlobalFont((ViewGroup) ll);
    }

    /**
     * 전체화면 지표설정창을 보여준다
     * */
    public void showIndicator()
    {
        close();
        m_miniViewEventListener.onMessage(COMUtil._POPUP_EVENT_MINI_OPENINDICATOR);
        //COMUtil.openIndicatorPopup();
    }

    /**
     * 차트 설정 초기화 확인 Alert을 보여준다.
     * */
    public void initChart() {
        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(COMUtil._chartMain);
        alert_confirm.setMessage("차트 설정을 초기화 하시겠습니까?").setCancelable(false);

        // 'YES'
        alert_confirm.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                COMUtil._mainFrame.bIsInitState = true;	//2020.09.02 상단 초기화 저장안되는 오류 수정 - jjh
                COMUtil._mainFrame.initChart(0);
            }
        });

        // 'No'
        alert_confirm.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });

        alert_confirm.create().show();
    }

    /**
     * 미니 지표설정창을 닫는다
     */
    public void close()
    {
//        COMUtil._mainFrame.closePopup();
        m_miniViewEventListener.onMessage(COMUtil._POPUP_EVENT_CLOSE);
    }

    public void setOnMiniSettingViewEventListener(OnPopupEventListener l) {
        this.m_miniViewEventListener = l;
    }

    /**
     * 차트 설정 초기화 확인 Alert을 보여준다.
     * */
    public void openScreen(String strScreen) {
        close();
        m_miniViewEventListener.onMessage(strScreen);
        //COMUtil.openIndicatorPopup();
    }
}
