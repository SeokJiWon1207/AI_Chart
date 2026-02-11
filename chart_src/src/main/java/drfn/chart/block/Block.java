package drfn.chart.block;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.view.ViewCompat;

import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.khronos.opengles.GL10;

import drfn.chart.NeoChart2;
import drfn.chart.base.IndicatorConfigView;
import drfn.chart.comp.DRAlertDialog;
import drfn.chart.comp.ViewNumTextViewController;
import drfn.chart.draw.BarDraw;
import drfn.chart.draw.BongDraw;
import drfn.chart.draw.DrawTool;
import drfn.chart.draw.KagiDraw;
import drfn.chart.draw.LineDraw;
import drfn.chart.draw.PnFDraw;
import drfn.chart.draw.RenkoDraw;
import drfn.chart.draw.ReverseClockDraw;
import drfn.chart.draw.SignalDraw;
import drfn.chart.draw.SpecialLineDraw;
import drfn.chart.draw.SwingDraw;
import drfn.chart.draw.ThirdChangeDraw;
import drfn.chart.draw.VarianceDraw;
import drfn.chart.event.ViewChangedListener;
import drfn.chart.event.ViewEvent;
import drfn.chart.graph.*;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;
import drfn.chart.scale.XScale;
import drfn.chart.scale.YScale;
import drfn.chart.signal.ADXRStrategySignal;
import drfn.chart.signal.CCIBaseLineSignal;
import drfn.chart.signal.CCIOverSoldOverBoughtSignal;
import drfn.chart.signal.DMISignal;
import drfn.chart.signal.DisparitySignal;
import drfn.chart.signal.GoldenDeadCrossEMASignal;
import drfn.chart.signal.GoldenDeadCrossMA_EMASignal;
import drfn.chart.signal.GoldenDeadCrossMA_WMASignal;
import drfn.chart.signal.GoldenDeadCrossSignal;
import drfn.chart.signal.GoldenDeadCross_MultiSignal;
import drfn.chart.signal.MACDBaseLineSignal;
import drfn.chart.signal.MACDSignal;
import drfn.chart.signal.MAOBaseLineSignal;
import drfn.chart.signal.PVISignal;
import drfn.chart.signal.ParabolicSignal;
import drfn.chart.signal.SonarMomentumSignal;
import drfn.chart.signal.SonarMomentumSignalSignal;
import drfn.chart.signal.StochasticsK_DSignal;
import drfn.chart.signal.WilliamsRSignal;
import drfn.chart.util.COMUtil;
import drfn.chart.util.ChartUtil;
import drfn.chart.util.CoSys;
import drfn.chart.util.MinMax;

public class Block implements ViewChangedListener{
    public static final int SUB_BLOCK=0;
    public static final int BASIC_BLOCK=1;
    public static final int STAND_BLOCK=2;

    public static final int BLOCK_TOP_MARGIN=28;
    public static final int BLOCK_BOTTOM_MARGIN=10;

    public int W_YSCALE = 0;//세로 스케일의 폭
    private int BLOCK_TYPE=0;//basic,sub,독립

    public ChartDataModel _cdm;//데이터 모델
    public ChartViewModel _cvm;//차트의 뷰모델

    private Vector<AbstractGraph> graphs;
    private Vector<VertScaleGroup> scalegroups;
    private XScale xscale;
    private YScale[] scale;

    //2019. 01. 12 by hyh - 블록병합 처리
    public Vector<String> arrMergedGraphTitles = new Vector<String>();
    public Vector<String> arrMergedGraphValues = new Vector<String>();

    private int index;//뷰에 insert되는 블럭의 순서 indexf
//    private int cindex;//블럭의 컬럼 인덱스

    private int margineR=(int)COMUtil.getPixel(10);      //블럭 마진(오른쪽)
//    private int margineT=(int)COMUtil.getPixel(15);      //블럭 마진(상단)
    public int margineT=(int)COMUtil.getPixel(10);      //블럭 마진(상단)
    public int margineB=(int)COMUtil.getPixel(0);      //블럭 마진(하단)
    //private int margineL=(int)COMUtil.getPixel(2);      //블럭 마진(왼쪽)
    private int margineL=(int)COMUtil.getPixel(0);      //블럭 마진(왼쪽)

    private RectF upbounds;
    private RectF dnbounds;
    private RectF bounds;
    private RectF graph_bounds;//그래프 바운드
    private RectF out_graph_bounds;//그래프 바운드의 테두리
//    private Rect org_bounds;//오리지널 바운드

    private int H_UPBOUNDS=0;
    private int H_DNBOUNDS=0;
    private int ypos=0;
    private String title;
    private int nGraphNum = 0;
    private int nSelGraph = 0;

    Button delButton =  null;
    LinearLayout delButtonLayout = null;

    Button changeBlockButton =  null;
    LinearLayout changeBlockButtonLayout = null;

    //2014. 3. 20 Y축 봉갯수 누르면 봉입력창 뜨고 봉갯수변경>>
    TextView tvViewNum =  null;
    LinearLayout tvViewNumLayout = null;
    //2014. 3. 20 Y축 봉갯수 누르면 봉입력창 뜨고 봉갯수변경<<

    boolean bStart = false;
    public boolean stateSize = false;
    public NeoChart2 parentView = null;
    private int m_nPaddingRight = 0;	//2013. 10. 29 1분선차트, 마운틴차트 제외한 차트 오른쪽 패딩 2->8 정도로 넓게 수정
    //private int m_nOrgMargineT=(int)COMUtil.getPixel(15);      //2017.06.21 by LYH >> 타이틀 3줄까지 타이틀 영역 밑으로 그래프 영역 잡도록 수정
    private int m_nOrgMargineT=(int)COMUtil.getPixel_H(10);      //2017.06.21 by LYH >> 타이틀 3줄까지 타이틀 영역 밑으로 그래프 영역 잡도록 수정

    int LEFT_MARGIN_TITLE = (int)COMUtil.getPixel_W(8);
//    Bitmap changeBlockBtnImg = null;
//    Paint mPaint = null;

//    public Block(NeoChart2 chart, ChartViewModel cvm, ChartDataModel cdm,int cindex, int index){
//        this.parentView = chart;
//        _cdm = cdm;
//        _cvm = cvm;
//        _cvm.addViewChangedListener(this);
//        this.index = index;
////        this.cindex =cindex;
//
//        W_YSCALE = (int)COMUtil.getPixel(50);
//
//        margineR=(int)COMUtil.getPixel(10);      //블럭 마진(오른쪽)
//        //this.margineT=margineT;      //블럭 마진(상단)
//        margineB=0;      //블럭 마진(하단)
//
//        //2013. 10. 29 1분선차트, 마운틴차트 제외한 차트 오른쪽 패딩 2->8 정도로 넓게 수정>>
//        if(_cvm.bIsNewsChart)   //2016. 1. 14 뉴스차트 핸들러
//        {
//            m_nPaddingRight = (int)COMUtil.getPixel(12);
//        }
//        else if(_cvm.bStandardLine || _cvm.bIsLineFillChart || _cvm.bIsLineChart || _cvm.bIsLine2Chart || _cvm.bIsInnerText)
//        {
//            m_nPaddingRight = (int)COMUtil.getPixel(1);
//        }
//        else
//        {
//            m_nPaddingRight = (int)COMUtil.getPixel(8);
//        }
//        _cvm.PADDING_RIGHT = m_nPaddingRight;
//        //2013. 10. 29 1분선차트, 마운틴차트 제외한 차트 오른쪽 패딩 2->8 정도로 넓게 수정<<
//    }
    public Block(NeoChart2 chart, ChartViewModel cvm, ChartDataModel cdm,int cindex, int index, String title){
        this.parentView = chart;
        this.title = title;
        _cdm = cdm;
        _cvm = cvm;
        _cvm.addViewChangedListener(this);
        this.index = index;
//        this.cindex =cindex;

        W_YSCALE = (int)COMUtil.getPixel(55);
        if(_cvm.m_bCurrentChart)
        {
            margineL=(int)COMUtil.getPixel(7);      //블럭 마진(왼쪽)
        }

//        if(!COMUtil.deviceMode.equals(COMUtil.HONEYCOMB) && !_cvm.bIsMiniBongChart && _cvm.chartType != COMUtil.COMPARE_CHART) {
//        	margineL=(int)COMUtil.getPixel(5);      //블럭 마진(왼쪽)
//        }
//        margineR=(int)COMUtil.getPixel(10);      //블럭 마진(오른쪽)
//        margineT=margineT;      //블럭 마진(상단)
//        margineB=0;      //블럭 마진(하단)
//        margineL=(int)COMUtil.getPixel(2);      //블럭 마진(왼쪽)
//        mPaint = new Paint();
//        mPaint.setAlpha(120);

//        int layoutResId = chart.getResources().getIdentifier("ico_sort", "drawable", chart.getContext().getPackageName());
//        changeBlockBtnImg = BitmapFactory.decodeResource(chart.getResources(), layoutResId);
//        Drawable drawable = (Drawable)(new BitmapDrawable(changeBlockBtnImg));
//        drawable.setAlpha(50);
//        drawable.setBounds(0, 0, (int)COMUtil.getPixel(12), (int)COMUtil.getPixel(12));
        //2013. 10. 29 1분선차트, 마운틴차트 제외한 차트 오른쪽 패딩 2->8 정도로 넓게 수정>>
        if(_cvm.bIsNewsChart)   //2016. 1. 14 뉴스차트 핸들러
        {
            m_nPaddingRight = (int)COMUtil.getPixel(12);
        }
        else if(_cvm.bIsMiniBongChart || _cvm.bStandardLine || _cvm.bIsLineFillChart || _cvm.bIsLine2Chart)
        {
            m_nPaddingRight = (int)COMUtil.getPixel(1);
        }
        else if(_cvm.bIsLineChart)
        {
            m_nPaddingRight = (int)COMUtil.getPixel(5);
        }
        else if(_cvm.bInvestorChart || _cvm.chartType == COMUtil.COMPARE_CHART)
        {
            m_nPaddingRight = (int)COMUtil.getPixel(8);
        }
        else
        {
            //m_nPaddingRight = (int)COMUtil.getPixel(8);
            if(COMUtil.isUsePaddingRight())
                m_nPaddingRight = (int)COMUtil.getPixel(COMUtil.getPaddingRight());   //2016.09.08 by LYH >> 오른쪽 여백 설정 기능
            else
                m_nPaddingRight = 0;
        }
        _cvm.PADDING_RIGHT = m_nPaddingRight;
        //2013. 10. 29 1분선차트, 마운틴차트 제외한 차트 오른쪽 패딩 2->8 정도로 넓게 수정<<
    }
    public void setMarginB(int margin_b){
        this.margineB = margin_b;
    }
    public void setMarginR(int margin_r){
        this.margineR = margin_r;
    }
    public void setMarginL(int margin_l){
        this.margineL = margin_l;
    }
    public void setMarginT(int margin_t){
        this.margineT = margin_t;
        m_nOrgMargineT = margin_t;  //2017.06.21 by LYH >> 타이틀 3줄까지 타이틀 영역 밑으로 그래프 영역 잡도록 수정
    }

    public void setStateSize(boolean state) {
        stateSize = state;
    }
    //======================================
    // 속성 설정
    // 1. y스케일데이터
    // 2. y스케일 위치
    // 3. y스케일의 스케일 라인 종류
    //======================================
    public void setProperties(String datakind, int pos, int ltype){
        String yscale_data = datakind;
        ypos = pos;
        int yslt = ltype;
        switch(ypos){
            case 0://왼쪽
            case 1://오른쪽
                scale = new YScale[1];
                scale[0] = new YScale(_cdm,_cvm);
                scale[0].setProperties(ypos,yscale_data,yslt);
                if(this.isBasicBlock()&&!_cvm.isSimpleMenuStyle())scale[0].setShowCurrPrice(true);
                break;
            case 2://양쪽
                scale = new YScale[2];
                scale[0] = new YScale(_cdm,_cvm);
                scale[0].setProperties(0,yscale_data,yslt);
                scale[1] = new YScale(_cdm,_cvm);
                scale[1].setProperties(1,yscale_data,2);
                break;
        }
        if(scale != null)
        {
	        for(int i=0;i<scale.length;i++){
	            scale[i].setLineColor(_cvm.CSL);
	            scale[i].setTextColor(_cvm.CST);
	            scale[i].setBackColor(_cvm.getBackColor());
	        }
        }
    }
    public YScale[] getYScale(){
        return scale;
    }
    public int getYScalePos(){
        return ypos;
    }

    //2013. 2. 8 체크안된 상세설정 오픈 : I114
    //2015. 1. 13 - by lyk 동일지표 이름 처리 (name 중에 COMUtil.JIPYO_ADD_REMARK가 있으면 showGraphName 사용)
    public AbstractGraph createGraph(String graphtitle, ChartViewModel cvm, ChartDataModel cdm, int standBlock)
    {

        //2015. 1. 13 - by lyk 동일지표 이름 처리 (name 중에 COMUtil.JIPYO_ADD_REMARK가 있으면 제거). graph만들 때 setProperties에 showGraphName으로 넘기기
        String showGraphName = graphtitle;
        graphtitle = COMUtil.getAddJipyoTitle(graphtitle);
        //2015. 1. 13 - by lyk 동일지표 이름 처리 (name 중에 COMUtil.JIPYO_ADD_REMARK가 있으면 제거) end

        AbstractGraph graph = null;
        DrawTool dt;
        if(graphtitle.equals("가격차트")||graphtitle.equals("일본식봉")){
            graph = new JapanBongGraph(cvm,cdm);
            graph.setProperties(0,"일본식봉",ChartUtil.JBONG);
            dt = new BongDraw(cvm,cdm);
            dt.setProperties(0,0,"종가", CoSys.CHART_COLORS[0]);
            dt.setDownColor(CoSys.CHART_COLORS[1]);
            dt.setSameColor(CoSys.CHART_COLORS[2]);
            graph.add(dt);

            //2015.07.10 by lyk - 주기별 저장 옵션 상태에서 기간 조회가 될 경우 뷰갯수가 초기화 되지 않도록 수정
            if (!COMUtil.getSendTrType().equals("storageType") &&
                    COMUtil.divideStorageType==false && !COMUtil.isPeriodConfigSave())
            //2015.07.10 by lyk - 주기별 저장 옵션 상태에서 기간 조회가 될 경우 뷰갯수가 초기화 되지 않도록 수정 end
            {
                if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
                    cvm.setViewNum(100);
                else {
//                    if(_cvm.bIsNewsChart)
//                        cvm.setViewNum(80);
//                    else
                        cvm.setViewNum(40);
                }
            }
        }
        else if(graphtitle.equals("투명캔들")){
            graph = new JapanBongGraph(cvm,cdm);
            graph.setProperties(0,"투명캔들",ChartUtil.JBONG_TRANSPARENCY);
            dt = new BongDraw(cvm,cdm);
            dt.setProperties(0,0,"종가", CoSys.CHART_COLORS[0]);
            dt.setDownColor(CoSys.CHART_COLORS[1]);
            dt.setSameColor(CoSys.CHART_COLORS[2]);
            graph.add(dt);

            //2015.07.10 by lyk - 주기별 저장 옵션 상태에서 기간 조회가 될 경우 뷰갯수가 초기화 되지 않도록 수정
            if (!COMUtil.getSendTrType().equals("storageType") &&
                    COMUtil.divideStorageType==false && !COMUtil.isPeriodConfigSave())
            //2015.07.10 by lyk - 주기별 저장 옵션 상태에서 기간 조회가 될 경우 뷰갯수가 초기화 되지 않도록 수정 end
            {
                if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
                    cvm.setViewNum(100);
                else {
//                    if(_cvm.bIsNewsChart)
//                        cvm.setViewNum(80);
//                    else
                    cvm.setViewNum(40);
                }
            }
        }
        else if(graphtitle.equals("라인차트")){
            graph = new PriceGraph(cvm,cdm);
            graph.setProperties(1,"라인차트",ChartUtil.PLINE);
            dt = new LineDraw(cvm,cdm);
            dt.setProperties(1,0,"종가", CoSys.CHART_COLORS[0]);
            dt.setLineT(3);
            dt.setLog(cvm.isLog);
            graph.add(dt);
        }
        else if(graphtitle.equals("거래량")){
            graph = new VolumnBarGraph(cvm,cdm);
            graph.setProperties(1,showGraphName,ChartUtil.VOLUME);
            String[] data = {"기본거래량"};
            graph.setDatakind(data);
            dt = new BarDraw(cvm,cdm);
            //2012. 8. 30 upColor 의 색상이 빨간색이 아니라 녹색이 되던 현상 수정 ; C15
            dt.setProperties(2,0,"기본거래량", CoSys.CHART_COLORS[0]);
            //2013.10.08 by LYH >> 거래량 일반 색상 적용. <<
//            dt.setSameColor(CoSys.CHART_COLORS[7]);
            dt.setSameColor(CoSys.CHART_COLORS[3]);
            graph.add(dt);
        }
        else if(graphtitle.equals("주가이동평균")){
            graph = new PriceAverageGraph(cvm,cdm);
            graph.setProperties(2,showGraphName,ChartUtil.PAVERAGE);
            for(int i=0; i<graph.interval.length; i++){
                dt = new LineDraw(cvm,cdm);
                dt.setShowZeroValue(false);
                //dt.setVisible(_cvm.average_state[i]);
                dt.setProperties(1,0,"이평 "+graph.interval[i], CoSys.CHART_COLORS[4+i] );
                if(cvm.average_state[i]) {
                    dt.setViewTitle(graph.interval[i] + "");
                }
                else
                {
                    dt.setViewTitle(graph.interval[i]+"");
                    dt.setVisible(false);
                }
                dt.setLog(cvm.isLog);
                //2012. 8. 8 상세설정창 굵기 기본값을 2->1로 변경 : I87
                dt.setLineT(2); //2023.11.08 by CYJ - kakaopay 주가이동평균 기본 굵기 변경
                graph.add(dt);
            }
//            for(int i=0; i<cvm.average_title.length; i++){
//                dt = new LineDraw(cvm,cdm);
//                dt.setShowZeroValue(false);
//                //dt.setVisible(_cvm.average_state[i]);
//                dt.setProperties(1,0,"이평 "+cvm.average_title[i], CoSys.CHART_COLORS[4+i] );
//                if(cvm.average_state[i])
//                    dt.setViewTitle(cvm.average_title[i]+"");
//                else
//                {
//                    dt.setViewTitle(cvm.average_title[i]+"");
//                    dt.setVisible(false);
//                }
//                dt.setLog(cvm.isLog);
//                //2012. 8. 8 상세설정창 굵기 기본값을 2->1로 변경 : I87
//                dt.setLineT(1);
//                graph.add(dt);
//            }
        }
        else if(graphtitle.equals("거래량이동평균")){
            graph = new TradeVolumnAverageGraph(_cvm,_cdm);
            graph.setProperties(2,showGraphName,ChartUtil.VAVERAGE);
            String[] data = {"기본거래량"};
            graph.setDatakind(data);
            for(int i=0; i<graph.interval.length; i++){
                dt = new LineDraw(_cvm,_cdm);
                dt.setShowZeroValue(false);
                //dt.setProperties(1,0,"거래량이평"+graph.interval[i], CoSys.CHART_COLORS[4+i] );
                dt.setProperties(1,0,"거래량이평"+graph.interval[i], CoSys.CHART_COLORS[5+i] );
                dt.setViewTitle(graph.interval+"");
                if(!cvm.vol_average_state[i])
                {
                    dt.setVisible(false);
                }
                graph.add(dt);
            }
//            for(int i=0; i<cvm.vol_average_title.length; i++){
//                dt = new LineDraw(_cvm,_cdm);
//                dt.setShowZeroValue(false);
//                dt.setProperties(1,0,"거래량이평"+cvm.vol_average_title[i], CoSys.CHART_COLORS[4+i] );
//                dt.setViewTitle(cvm.vol_average_title[i]+"");
//                if(!cvm.vol_average_state[i])
//                {
//                    dt.setVisible(false);
//                }
//                graph.add(dt);
//            }
        }
        else if(graphtitle.equals("신용잔고")){
            graph = new MarketGraph(cvm,cdm);
            //2012. 8. 8  변수설정 삭제 : I79
            graph.setProperties(0,showGraphName,ChartUtil.PIVOT);
            dt = new LineDraw(cvm,cdm);
            //2012. 8. 8  선굵기색상  : I79
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
            dt.setShowZeroValue(false);
            dt.setTitle(graphtitle);
            graph.add(dt);
        }
        else if(graphtitle.equals("신용잔고율")){
            graph = new MarketGraph(cvm,cdm);
            //2012. 8. 8  변수설정 삭제 : I77
            graph.setProperties(0,showGraphName,ChartUtil.PIVOT);
            dt = new LineDraw(cvm,cdm);
            dt.setShowZeroValue(true);
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
            dt.setTitle(graphtitle);
            graph.add(dt);
        }
        else if(graphtitle.equals("외국인지분율")){
            graph = new MarketGraph(cvm,cdm);
            //2012. 8. 8  변수설정 삭제 : I77
            graph.setProperties(0,showGraphName,ChartUtil.PIVOT);
            dt = new LineDraw(cvm,cdm);
            dt.setShowZeroValue(true);
            dt.setProperties(1,0,graphtitle,_cvm.getLineDrawColor());
            dt.setTitle(graphtitle);
            graph.add(dt);
        }
        else if(graphtitle.equals("외국인순매수")){
            graph = new MarketGraph(cvm,cdm);
            //2012. 8. 8  변수설정 삭제 : I78
            graph.setProperties(0,showGraphName,ChartUtil.PIVOT);
            dt = new BarDraw(cvm,cdm);
            //2012. 8. 8  선굵기색상  : I78
            dt.setProperties(2,2,graphtitle, _cvm.getLineDrawColor());
            dt.setTitle(graphtitle);
            graph.add(dt);
        }
        else if(graphtitle.equals("외국인보유수량")){
            graph = new MarketGraph(cvm,cdm);
            //2012. 8. 8  변수설정 삭제 : I79
            graph.setProperties(0,showGraphName,ChartUtil.PIVOT);
            dt = new LineDraw(cvm,cdm);
            //2012. 8. 8  선굵기색상  : I79
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
            dt.setTitle(graphtitle);
            graph.add(dt);
        }
        else if(graphtitle.equals("기관누적")){
            graph = new MarketGraph(cvm,cdm);
            //2012. 8. 8  변수설정 삭제 : I79
            graph.setProperties(0,showGraphName,ChartUtil.PIVOT);
            dt = new LineDraw(cvm,cdm);
            //2012. 8. 8  선굵기색상  : I79
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
            dt.setTitle(graphtitle);
            graph.add(dt);
        }
        else if(graphtitle.equals("기관순매수")){
            graph = new MarketGraph(cvm,cdm);
            //2012. 8. 8  변수설정 삭제 : I80
            graph.setProperties(0,showGraphName,ChartUtil.PIVOT);
            dt = new BarDraw(cvm,cdm);
            //2012. 8. 8  선굵기색상  : I80
            dt.setProperties(2,2,graphtitle, _cvm.getLineDrawColor());
            dt.setTitle(graphtitle);
            graph.add(dt);
        }
        else if(graphtitle.equals("미결제약정")){
            graph = new MarketGraph(cvm,cdm);
            //2012. 8. 8  데이터 삭제 : I82
            graph.setProperties(0,showGraphName,ChartUtil.PIVOT);
            String[] data = {"미결제약정"};
            graph.setDatakind(data);
            dt = new BarDraw(cvm,cdm);
            dt.setShowZeroValue(true);
            //2012. 8. 8  선굵기색상  : I82
//            dt.setProperties(2,0,graphtitle, CoSys.CHART_COLORS[7]);
            dt.setProperties(2,0,graphtitle, _cvm.getLineDrawColor());
            dt.setTitle(graphtitle);
            graph.add(dt);
        }
        else if(graphtitle.equals("매수매도 거래량")){
            graph = new VolumeSellBuyGraph(cvm,cdm);
            graph.setProperties(1,showGraphName,ChartUtil.VOLUME_SELLBUY);
            String[] data = {"매수거래량","매도거래량"};
            graph.setDatakind(data);
            dt = new BarDraw(cvm,cdm);
            //2012. 8. 30 upColor 의 색상이 빨간색이 아니라 녹색이 되던 현상 수정 ; C15
            dt.setProperties(2,0,"매수매도거래량", CoSys.CHART_COLORS[0]);
            //2013.10.08 by LYH >> 거래량 일반 색상 적용. <<
            dt.setSameColor(CoSys.CHART_COLORS[3]);
            graph.add(dt);
        }
        else if(graphtitle.equals("거래대금")){
            graph = new MarketGraph(cvm,cdm);
            //2012. 8. 8  데이터 삭제 : I82
            graph.setProperties(0,showGraphName,ChartUtil.PIVOT);
            String[] data = {"거래대금"};
            graph.setDatakind(data);
            dt = new BarDraw(cvm,cdm);
            dt.setShowZeroValue(true);
            //2012. 8. 8  선굵기색상  : I82
//            dt.setProperties(2,0,graphtitle, CoSys.CHART_COLORS[7]);
            dt.setProperties(2,0,graphtitle, _cvm.getLineDrawColor());
            dt.setTitle(graphtitle);
            graph.add(dt);
        }
        //2015. 2. 13 미결제약정 지표 추가>>
//        else if(graphtitle.equals("미결제약정")){
        else if(graphtitle.equals("미결제약정(선물옵션)")){
            //2015. 2. 13 미결제약정 지표 추가<<
            graph = new MarketGraph(cvm,cdm);
            //2015. 2. 13 미결제약정 지표 추가>>
//            graph.setProperties(0,showGraphName,ChartUtil.PLINE);
            graph.setProperties(0,showGraphName,ChartUtil.PIVOT);
//            dt = new BarDraw(cvm,cdm);
            dt = new LineDraw(cvm,cdm);
            //2015. 2. 13 미결제약정 지표 추가<<
            dt.setShowZeroValue(true);
            //2015. 2. 13 미결제약정 지표 추가>>
//            dt.setProperties(2,0,graphtitle, CoSys.CHART_COLORS[3]);
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
            //2015. 2. 13 미결제약정 지표 추가<<
            dt.setTitle(graphtitle);
            graph.add(dt);
        }
        else if(graphtitle.equals("미결제약정(국내선물)")){
            graph = new MarketGraph(cvm, cdm);
            graph.setProperties(0, showGraphName, ChartUtil.PIVOT);
            String[] data = {"미결제약정"};
            graph.setDatakind(data);

            //2019. 08. 20 by hyh - 미결제약정 라인으로 변경 >>
            dt = new LineDraw(cvm,cdm);
            //dt = new BarDraw(cvm,cdm);
            //2019. 08. 20 by hyh - 미결제약정 라인으로 변경 <<

            //2019. 12. 26 by hyh - 미결제약정 최대최소 계산방법 변경 >>
            dt.setProperties(1, 0, "미결제약정", _cvm.getLineDrawColor());
            //dt.setProperties(2, 0, "미결제약정", CoSys.CHART_COLORS[0]);
            //2019. 12. 26 by hyh - 미결제약정 최대최소 계산방법 변경 <<

            dt.setShowZeroValue(true);
            dt.setSameColor(CoSys.CHART_COLORS[3]);
            graph.add(dt);
        }
        else if(graphtitle.equals("개인누적")){
            graph = new MarketGraph(cvm,cdm);
            //2012. 8. 8  변수설정 삭제 : I81
            graph.setProperties(0,showGraphName,ChartUtil.PIVOT);
            dt = new LineDraw(cvm,cdm);
            //2012. 8. 8  선굵기색상  : I81
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
            dt.setTitle(graphtitle);
            graph.add(dt);
        }
        //2019. 04. 08 by hyh - FX마진 데이터 조회 및 실시간 처리 >>
        else if(graphtitle.equals("매수")){
            graph = new MarketGraph(cvm,cdm);
            graph.setProperties(2,showGraphName,ChartUtil.PIVOT);
            dt = new LineDraw(cvm,cdm);
            dt.setShowZeroValue(false);
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
            dt.setTitle(graphtitle);
            dt.setLog(cvm.isLog);
            graph.add(dt);
        }
        //2019. 04. 08 by hyh - FX마진 데이터 조회 및 실시간 처리 <<
        //2012.08.29 by LYH >> ELW 기초자산 추가.
        else if(graphtitle.equals("BaseMarket")){
            graph = new MarketGraph(cvm,cdm);
            graph.setProperties(2,showGraphName,ChartUtil.PIVOT);
            dt = new LineDraw(cvm,cdm);
            dt.setShowZeroValue(false);
            dt.setProperties(1,0,graphtitle, CoSys.baseMarketColor);
            dt.setTitle(graphtitle);
            dt.setLog(cvm.isLog);
            graph.add(dt);
        }
        //2012.08.29 by LYH <<

        else if(graphtitle.equals("Bollinger Band")){
            graph = new BollingerBandGraph(cvm,cdm);
            graph.setProperties(2,showGraphName,ChartUtil.BOLLINGER);
            //2012. 8. 8  선굵기 설정의 각 이름 다름 : I84
            String[] lineNames = {"BB 상한","BB 중간","BB 하한"};
            int nColorIndex[] = {5,4,6};
            for(int i=0; i<lineNames.length; i++){
                dt = new LineDraw(cvm,cdm);
                dt.setShowZeroValue(false);
                dt.setProperties(1,0,lineNames[i], CoSys.CHART_COLORS[(nColorIndex[i])] );
                dt.setLineT(2);
                dt.setLog(cvm.isLog);
                graph.add(dt);
            }
        }
        else if(graphtitle.equals("Bollinger Band [보조]")){
            graph = new BollingerBandGraph(_cvm,_cdm);
            graph.setProperties(2,showGraphName,ChartUtil.BOLLINGER_2);
            //2012. 8. 8  선굵기 설정의 각 이름 다름 : I84
            String[] lineNames = {"BB_상한","BB_중간","BB_하한"};
            int nColorIndex[] = {5,4,6};
            for(int i=0; i<lineNames.length; i++){
                dt = new LineDraw(_cvm,_cdm);
                dt.setShowZeroValue(false);
                dt.setProperties(1,0,lineNames[i], CoSys.CHART_COLORS[(nColorIndex[i])] );
                //dt.setLog(_cvm.isLog);
                graph.add(dt);
            }
        }
        else if(graphtitle.equals("Envelope")){
            graph = new EnvelopeGraph(cvm,cdm);
            graph.setProperties(2,showGraphName,ChartUtil.ENVELOPE);
            //2012. 8. 8 색굵기 라인의 제목 다름 : I85
            String[] lineNames = {"E_상한","E_중심","E_하한"};
            int nColorIndex[] = {5,4,6};
            for(int i=0; i<lineNames.length; i++){
                dt = new LineDraw(cvm,cdm);
                dt.setShowZeroValue(false);
                dt.setProperties(1,0,lineNames[i], CoSys.CHART_COLORS[(nColorIndex[i])] );
                dt.setLog(cvm.isLog);
                dt.setLineT(2);
                graph.add(dt);
            }
        }
        else if(graphtitle.equals("일목균형표")){
            graph = new GlanceBalanceGraph(cvm,cdm);
            graph.setProperties(2,showGraphName,ChartUtil.GLANCE_BALANCE);
            cvm.futureMargin=graph.interval[2];
            String[] lineNames = {"전환선","기준선","후행스팬"};
            int nColorIndex[] = {2,7,4,5,6};
            for(int i=0; i<lineNames.length; i++){
                dt = new LineDraw(cvm,cdm);
                dt.setShowZeroValue(false);
                dt.setProperties(1,0,lineNames[i], CoSys.CHART_COLORS[(nColorIndex[i])] );
                dt.setLog(cvm.isLog);
                graph.add(dt);
            }
            //2016.07.28 by LYH >> 일목균형 선행스팬1, 선행스팬2 라인 굵기 색상 적용.
            DrawTool dt1 = new SpecialLineDraw(cvm,cdm);
            //2012. 8. 8 선형스팬 색상 다름  :  I83
            //2017.01.03 by LYH >> 일목균형 선행스팬1, 2 타이틀 개선
            //dt1.setProperties(1,2,"선행스팬1", CoSys.CHART_COLORS[1] );
            dt1.setProperties(1,2,"선행스팬1", CoSys.CHART_COLORS[0] );
            //2017.01.03 by LYH >> 일목균형 선행스팬1, 2 타이틀 개선 end
            dt1.setShowZeroValue(false);
            dt1.setLog(cvm.isLog);
            graph.add(dt1);
            DrawTool dt2 = new SpecialLineDraw(cvm,cdm);
            //2017.01.03 by LYH >> 일목균형 선행스팬1, 2 타이틀 개선
            //dt2.setProperties(1,2,"선행스팬2", CoSys.CHART_COLORS[0] );
            dt2.setProperties(1,2,"선행스팬2", CoSys.CHART_COLORS[1] );
            //2017.01.03 by LYH >> 일목균형 선행스팬1, 2 타이틀 개선 end
            dt2.setShowZeroValue(false);
            dt2.setLog(cvm.isLog);
            graph.add(dt2);
            //2016.07.28 by LYH << 일목균형 선행스팬1, 선행스팬2 라인 굵기 색상 적용.
        }else if(graphtitle.equals("Parabolic SAR")){
            graph = new ParabolicSARGraph(cvm,cdm);
            graph.setProperties(2,showGraphName,ChartUtil.PARABOLIC);
            String[] data = {"고가","저가","종가"};
            graph.setDatakind(data);
            dt = new LineDraw(cvm,cdm);
            dt.setShowZeroValue(false);
            dt.setProperties(1,5,"PSAR", CoSys.CHART_COLORS[5]);
            dt.setLog(cvm.isLog);
            graph.add(dt);
        }
        else if(graphtitle.equals("그물차트")){
            graph = new RainbowGraph(cvm,cdm);
            graph.setProperties(2,showGraphName,ChartUtil.RAINBOW);
//            dt = new LineDraw(cvm,cdm);
//            dt.setShowZeroValue(false);
//            dt.setProperties(1,4,graphtitle, CoSys.CHART_COLORS[11]);
//            dt.setLog(cvm.isLog);
//            graph.add(dt);
            //2016.12.08 by LYH >> 레인보우 차트 30개까지 라인 색상 굵기 설정 가능하도록 처리
            for(int i=0;i<30; i++)
            {
                dt = new LineDraw(cvm,cdm);
                dt.setShowZeroValue(false);
                dt.setProperties(1,4,"그물망"+(i+1), CoSys.CHART_COLORS[11]);
                if(i>0)
                    dt.setViewTitle("");
                dt.setLog(cvm.isLog);
                graph.add(dt);
            }
            //2016.12.08 by LYH >> 레인보우 차트 30개까지 라인 색상 굵기 설정 가능하도록 처리 end
        }
        else if(graphtitle.equals("Pivot")){
            graph = new PivotGraph(cvm, cdm);
            graph.setProperties(2, showGraphName, ChartUtil.PIVOT);
            //2012. 8. 8  색굵기 색상 다름 : I86
            int[][] p_col = {CoSys.CHART_COLORS[4], CoSys.CHART_COLORS[5], CoSys.CHART_COLORS[6], CoSys.CHART_COLORS[7], CoSys.CHART_COLORS[8], CoSys.CHART_COLORS[9], CoSys.CHART_COLORS[10]};
            String[] lineNames = {"1차저항", "2차저항", "3차저항", "1차지지", "2차지지", "3차지지", "Pivot"};
            for (int i = 0; i < lineNames.length; i++) {
                dt = new LineDraw(cvm, cdm);
                dt.setShowZeroValue(false);
                dt.setProperties(1, 0, lineNames[i], p_col[i]);
                dt.setLog(cvm.isLog);
                dt.setLineT(2);
                graph.add(dt);
            }
        }
        else if(graphtitle.equals("Pivot전봉기준")){
            graph = new PivotPrevGraph(cvm, cdm);
            graph.setProperties(2,showGraphName,ChartUtil.PIVOT);
            //2012. 8. 8  색굵기 색상 다름 : I86
            int[][] p_col={CoSys.CHART_COLORS[4],CoSys.CHART_COLORS[9],CoSys.CHART_COLORS[8],CoSys.CHART_COLORS[7],CoSys.CHART_COLORS[6]};
            String[] lineNames = {"2차저항+","1차저항+","Pivot+","1차지지+","2차지지+"};
            for(int i=0; i<lineNames.length; i++){
                dt = new LineDraw(cvm,cdm);
                dt.setShowZeroValue(false);
                dt.setProperties(1,0,lineNames[i], p_col[i]);
                dt.setLog(cvm.isLog);
                dt.setLineT(2);
                graph.add(dt);
            }
        }
        else if(graphtitle.equals("MACD")){
            graph = new MACDGraph(cvm,cdm);
            graph.setProperties(0,showGraphName,ChartUtil.MACD);
            dt = new LineDraw(cvm,cdm);
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
            graph.add(dt);
            DrawTool dt1 = new LineDraw(cvm,cdm);
            dt1.setProperties(1,0,"MACD Signal", _cvm.getLineDrawColor());
            graph.add(dt1);
        }
        else if(graphtitle.equals("MACD+OSC")){
            graph = new MACD_OSCGraph(cvm,cdm);
            graph.setProperties(0,showGraphName,ChartUtil.MACD);
            dt = new LineDraw(cvm,cdm);
            dt.setProperties(1,0,"MACD+",_cvm.getLineDrawColor());
            graph.add(dt);
            DrawTool dt1 = new LineDraw(cvm,cdm);
            dt1.setProperties(1,0,"MACD+ Signal", _cvm.getLineDrawColor());
            graph.add(dt1);
            DrawTool dt2 = new BarDraw(cvm,cdm);
            dt2.setProperties(2,2,"OSC", _cvm.getLineDrawColor());
//            dt2.setDownColor(CoSys.CHART_COLORS[6]);
            graph.add(dt2);
        }
        else if(graphtitle.equals("A/B Ratio")){
            graph = new ABRatioGraph(cvm,cdm);
            graph.setProperties(0,showGraphName,ChartUtil.AB_Ratio);
            dt = new LineDraw(cvm,cdm);
            dt.setShowZeroValue(false);
            dt.setProperties(1,0,"A Ratio", _cvm.getLineDrawColor());
            graph.add(dt);
            DrawTool dt1 = new LineDraw(cvm,cdm);
            dt1.setShowZeroValue(false);
            dt1.setProperties(1,0,"B Ratio", _cvm.getLineDrawColor());
            graph.add(dt1);
        }
        //2015. 1. 13 ADLine 지표 추가>>
        else if(graphtitle.equals("AD Line")){
            graph = new ADLineGraph(cvm, cdm);
            graph.setProperties(0,showGraphName,ChartUtil.ADLINE);
            dt = new LineDraw(cvm,cdm);
            dt.setProperties(1,0,showGraphName, _cvm.getLineDrawColor());
            graph.add(dt);
            DrawTool dt1 = new LineDraw(cvm,cdm);
            dt1.setProperties(1,0,"ADLine Signal", _cvm.getLineDrawColor());
            graph.add(dt1);
        }
        //2015. 1. 13 ADLine 지표 추가<<
        else if(graphtitle.equals("ADX")){
            graph = new ADXGraph(cvm,cdm);
            graph.setProperties(0,showGraphName,ChartUtil.ADX);
            dt = new LineDraw(cvm,cdm);
            dt.setProperties(1,0,showGraphName, _cvm.getLineDrawColor());
            graph.add(dt);
            DrawTool dt1 = new LineDraw(cvm,cdm);
            dt1.setProperties(1,0,"ADX_PDI", _cvm.getLineDrawColor());
            graph.add(dt1);
            DrawTool dt2 = new LineDraw(cvm,cdm);
            dt2.setProperties(1,0,"ADX_MDI", _cvm.getLineDrawColor());
            graph.add(dt2);
        }else if(graphtitle.equals("MACD OSC")){
            graph = new MACDOscillatorGraph(cvm,cdm);
            graph.setProperties(0,showGraphName,ChartUtil.MACD_OSC);
            dt = new BarDraw(cvm,cdm);
            dt.setProperties(2,2,graphtitle, _cvm.getLineDrawColor());
//            dt.setDownColor(CoSys.CHART_COLORS[6]);
            graph.add(dt);
        }
        else if(graphtitle.equals("Stochastic Slow")){
            graph = new StochSlowGraph(cvm,cdm);
            graph.setProperties(0,showGraphName,ChartUtil.STOCH_SLW);
            dt = new LineDraw(cvm,cdm);
            dt.setProperties(1,0,"Stochastic Slow%K", _cvm.getLineDrawColor());
            graph.add(dt);
            DrawTool dt1 = new LineDraw(cvm,cdm);
            //2012. 8. 8 색상 아이폰과 맞춤 : I66 
            dt1.setProperties(1,0,"Stochastic Slow%D", _cvm.getLineDrawColor());
            graph.add(dt1);
        }
        else if(graphtitle.equals("Stochastics Oscillator")){
            graph = new StochOscillatorGraph(cvm,cdm);
            graph.setProperties(0,showGraphName,ChartUtil.STOCH_OSC);
            //2012. 8. 8  선굵기 설정의 각 이름 다름 : I84
            dt = new BarDraw(cvm,cdm);
            dt.setProperties(2,2,graphtitle, _cvm.getLineDrawColor());
//            dt.setLineT(2);
            graph.add(dt);
        }
        else if(graphtitle.equals("Price ROC")){
            graph = new PriceROCGraph(cvm,cdm);
            graph.setProperties(0,showGraphName,ChartUtil.PROC);
            dt = new LineDraw(cvm,cdm);
            dt.setShowZeroValue(true);
            //2012. 8. 8  심리도 색/굵기 설정의 이름 및 색상 : I71
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
            graph.add(dt);
            DrawTool dt1 = new LineDraw(cvm,cdm);
            dt1.setProperties(1,0,"PROC Signal", _cvm.getLineDrawColor());
            graph.add(dt1);
            //2012. 8. 8 제목다름 :  I89
        }else if(graphtitle.equals("EOM")){
            graph = new EOMGraph(cvm,cdm);
            graph.setProperties(0,showGraphName,ChartUtil.EOM);
            dt = new LineDraw(cvm,cdm);
            dt.setShowZeroValue(true);
            //2012. 8. 8  심리도 색/굵기 설정의 이름 및 색상 : I71
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
            graph.add(dt);
            DrawTool dt1 = new LineDraw(cvm,cdm);
            dt1.setProperties(1,0,"EOM Signal", _cvm.getLineDrawColor());
            graph.add(dt1);
            //2012. 8. 8 제목다름 :  I89
        }else if(graphtitle.equals("신심리도")){
            graph = new NewPsycoGraph(cvm,cdm);
            graph.setProperties(0,showGraphName,ChartUtil.NEWPSYCO);
            dt = new LineDraw(cvm,cdm);
            dt.setShowZeroValue(true);
//            dt.setProperties(1,0,graphtitle, CoSys.CHART_COLORS[5]);
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
            graph.add(dt);
            //2012. 8. 8 제목다름 :  I89
        }
        else if(graphtitle.equals("DMI")){
            graph = new DMIGraph(cvm,cdm);
            graph.setProperties(0,showGraphName,ChartUtil.DMI);
            dt = new LineDraw(cvm,cdm);
            dt.setProperties(1,0,"UpDI", _cvm.getLineDrawColor());
            graph.add(dt);
            DrawTool dt1 = new LineDraw(cvm,cdm);
            dt1.setProperties(1,0,"DownDI", _cvm.getLineDrawColor());
            graph.add(dt1);
        }
        //2019. 06. 28 by hyh - TSF 지표 추가 >>
        else if(graphtitle.equals("TSF")){
            graph = new TSFGraph(_cvm,_cdm);
            graph.setProperties(2,showGraphName,ChartUtil.TSF);
            //2012. 8. 8  선굵기 설정의 각 이름 다름 : I84
            dt = new LineDraw(_cvm,_cdm);
            dt.setShowZeroValue(false);
            dt.setProperties(1,0,"TSF", _cvm.getLineDrawColor());
            graph.add(dt);
            DrawTool dt1 = new LineDraw(_cvm,_cdm);
            dt1.setShowZeroValue(true);
            dt1.setProperties(1,0,"TSF_Signal", _cvm.getLineDrawColor());
            graph.add(dt1);
        }
        //2019. 06. 28 by hyh - TSF 지표 추가 <<
        //2019. 10. 16 by hyh - MAO 지표 추가 >>
        else if(graphtitle.equals("MAO")){
            graph = new MAOGraph(_cvm,_cdm);
            graph.setProperties(0,showGraphName,ChartUtil.MAO);
            dt = new BarDraw(_cvm,_cdm);
            dt.setProperties(2,2,graphtitle, _cvm.getLineDrawColor());
            graph.add(dt);
        }
        //2019. 10. 16 by hyh - MAO 지표 추가 <<
        //2015. 2. 13 LRS 지표 추가>>
        else if(graphtitle.equals("LRS")){
            graph = new LRSGraph(_cvm,_cdm);
            graph.setProperties(0,showGraphName,ChartUtil.LRS);
            dt = new LineDraw(_cvm,_cdm);
            dt.setShowZeroValue(true);
            dt.setProperties(1,0,graphtitle, CoSys.CHART_COLORS[9]);
            graph.add(dt);
            DrawTool dt1 = new LineDraw(_cvm,_cdm);
            dt1.setProperties(1,0,"LRS Signal", CoSys.CHART_COLORS[4]);
            graph.add(dt1);
        }
        //2015. 2. 13 LRS 지표 추가<<
        else if(graphtitle.equals("RSI")){
            graph = new RSIGraph(cvm,cdm);
            graph.setProperties(0,showGraphName,ChartUtil.RSI);
            dt = new LineDraw(cvm,cdm);
            dt.setShowZeroValue(true);
//            dt.setProperties(1,0,graphtitle, CoSys.CHART_COLORS[9]);
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
            graph.add(dt);
            DrawTool dt1 = new LineDraw(cvm,cdm);
            dt1.setShowZeroValue(false);
            dt1.setProperties(1,0,"RSI Signal", _cvm.getLineDrawColor());
            graph.add(dt1);
        }else if(graphtitle.equals("OBV")){
            graph = new OBVGraph(cvm,cdm);
            graph.setProperties(0,showGraphName,ChartUtil.OBV);
            dt = new LineDraw(cvm,cdm);
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
            graph.add(dt);
            //2012. 8. 8 OBV Signal 색굵기 없음 : I69
            DrawTool dt1 = new LineDraw(cvm,cdm);
            dt1.setProperties(1,0,"OBV Signal", _cvm.getLineDrawColor());
            graph.add(dt1);
        }else if(graphtitle.equals("Reverse")){
            graph = new ReverseGraph(cvm,cdm);
            graph.setProperties(0,showGraphName,ChartUtil.REVERSE);
            dt = new LineDraw(cvm,cdm);
            dt.setProperties(1,0,"Reverse Short", _cvm.getLineDrawColor());
            graph.add(dt);
            DrawTool dt1 = new LineDraw(cvm,cdm);
            dt1.setProperties(1,0,"Reverse Long", _cvm.getLineDrawColor());
            graph.add(dt1);
        }else if(graphtitle.equals("Sonar")){
            graph = new SonarGraph(cvm,cdm);
            graph.setProperties(0,showGraphName,ChartUtil.SONAR);
            dt = new LineDraw(cvm,cdm);
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
            graph.add(dt);
            DrawTool dt1 = new LineDraw(cvm,cdm);
            dt1.setProperties(1,0,"Sonar Signal", _cvm.getLineDrawColor());
            graph.add(dt1);
        }else if(graphtitle.equals("Standard Deviation")){
            graph = new StDevGraph(cvm,cdm);
            graph.setProperties(0,showGraphName,ChartUtil.STDEV);
            dt = new LineDraw(cvm,cdm);
            dt.setShowZeroValue(false);
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
            graph.add(dt);
        }else if(graphtitle.equals("Stochastic Fast")){
            graph = new StochFastGraph(cvm,cdm);
            graph.setProperties(0,showGraphName,ChartUtil.STOCH_FST);
            dt = new LineDraw(cvm,cdm);
            dt.setProperties(1,0,"Stochastic Fast%K", _cvm.getLineDrawColor());
            graph.add(dt);
            DrawTool dt1 = new LineDraw(cvm,cdm);
            dt1.setProperties(1,0,"Stochastic Fast%D", _cvm.getLineDrawColor());
            graph.add(dt1);
        }else if(graphtitle.equals("VR")){
            graph = new VRGraph(cvm,cdm);
            graph.setProperties(0,showGraphName,ChartUtil.VR);
            dt = new LineDraw(cvm,cdm);
            dt.setShowZeroValue(false);
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
            graph.add(dt);
        }else if(graphtitle.equals("이격도")){
            graph = new DisparityGraph(cvm,cdm);
            graph.setProperties(0,showGraphName,ChartUtil.DISPARITY);
            dt = new LineDraw(cvm,cdm);
            dt.setShowZeroValue(false);
            dt.setProperties(1,0,"이격도1", _cvm.getLineDrawColor());
            graph.add(dt);
            DrawTool dt1 = new LineDraw(cvm,cdm);
            dt1.setShowZeroValue(false);
            dt1.setProperties(1,0,"이격도2", _cvm.getLineDrawColor());
            graph.add(dt1);
            DrawTool dt2 = new LineDraw(cvm,cdm);
            dt2.setShowZeroValue(false);
            dt2.setProperties(1,0,"이격도3", _cvm.getLineDrawColor());
            graph.add(dt2);
            DrawTool dt3 = new LineDraw(cvm,cdm);
            dt3.setShowZeroValue(false);
            dt3.setProperties(1,0,"이격도4", _cvm.getLineDrawColor());
            graph.add(dt3);
        }else if(graphtitle.equals("심리도")){
            graph = new PsycoGraph(cvm,cdm);
            graph.setProperties(0,showGraphName,ChartUtil.PSYCO);
            dt = new LineDraw(cvm,cdm);
            dt.setShowZeroValue(true);
            //2012. 8. 8  심리도 색/굵기 설정의 이름 및 색상 : I71
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
            graph.add(dt);
            DrawTool dt1 = new LineDraw(cvm,cdm);
            dt1.setProperties(1,0,"심리도 Signal", _cvm.getLineDrawColor());
            graph.add(dt1);
            //2012. 8. 8 제목다름 :  I89
        }else if(graphtitle.equals("Williams R")){
            graph = new WilliamGraph(cvm,cdm);
            //2012. 7. 3  Williams 상세설정화면 안뜨는 문제 해결    기존. 'Williams %R' 로 되어있음 
            graph.setProperties(0,showGraphName,ChartUtil.WILLIAMS);
            dt = new LineDraw(cvm,cdm);
            //2012. 7. 3  Williams 상세설정화면 안뜨는 문제 해결     기존. 'Williams %R' 로 되어있음
            //2012. 8. 8 Williams 첫번째 이름 다름 : I70
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
            graph.add(dt);
            DrawTool dt1 = new LineDraw(cvm,cdm);
            dt1.setProperties(1,0,"R Signal", _cvm.getLineDrawColor());
            graph.add(dt1);
        }else if(graphtitle.equals("Momentum")){
            graph = new MomentumGraph(cvm,cdm);
            graph.setProperties(0,showGraphName,ChartUtil.Momentum);
            dt = new LineDraw(cvm,cdm);
            dt.setShowZeroValue(false);
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
            DrawTool dt1 = new LineDraw(cvm,cdm);
            dt1.setShowZeroValue(false);
            dt1.setProperties(1,0,"Momentum Signal", _cvm.getLineDrawColor());
            graph.add(dt);
            graph.add(dt1);
        }else if(graphtitle.equals("CCI")){
            graph = new CCIGraph(_cvm,_cdm);
            graph.setProperties(0,showGraphName,ChartUtil.CCI);
            dt = new LineDraw(_cvm,_cdm);
            dt.setShowZeroValue(true);
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
            graph.add(dt);
            DrawTool dt1 = new LineDraw(_cvm,_cdm);
            dt1.setProperties(1,0,"CCI Signal", _cvm.getLineDrawColor());
            graph.add(dt1);
        }else if(graphtitle.equals("AB Ratio")){
            graph = new ABRatioGraph(cvm,cdm);
            graph.setProperties(0,showGraphName,ChartUtil.AB_Ratio);
            dt = new LineDraw(cvm,cdm);
            dt.setProperties(1,0,"A Ratio", _cvm.getLineDrawColor());

            DrawTool dt1 = new LineDraw(cvm,cdm);
            dt1.setShowZeroValue(false);
            dt1.setProperties(1,0,"B Ratio", _cvm.getLineDrawColor());
            graph.add(dt);
            graph.add(dt1);
        }
        //2012. 8. 8 이름다름  : I73
        else if(graphtitle.equals("Chaikins OSC")){//Chaikin's OSC
            graph = new COGraph(cvm,cdm);
            graph.setProperties(0,showGraphName,ChartUtil.CO);
            dt = new LineDraw(cvm,cdm);
            dt.setProperties(1,0,"CO", _cvm.getLineDrawColor());
            graph.add(dt);
            DrawTool dt1 = new LineDraw(cvm,cdm);
            dt1.setProperties(1,0,"CO Signal", _cvm.getLineDrawColor());
            graph.add(dt1);
        }else if(graphtitle.equals("MAC")){
            graph = new MACGraph(cvm,cdm);
            graph.setProperties(2,showGraphName,ChartUtil.MAC);
            dt = new LineDraw(cvm,cdm);
            dt.setShowZeroValue(false);
            dt.setLog(cvm.isLog);
            dt.setLineT(2);
            dt.setProperties(1,0,"MAC Upper", _cvm.getLineDrawColor());
            graph.add(dt);
            DrawTool dt1 = new LineDraw(cvm,cdm);
            dt1.setShowZeroValue(false);
            dt1.setLog(cvm.isLog);
            dt1.setLineT(2);
            dt1.setProperties(1,0,"High MA", _cvm.getLineDrawColor());
            graph.add(dt1);
            DrawTool dt2 = new LineDraw(cvm,cdm);
            dt2.setShowZeroValue(false);
            dt2.setLog(cvm.isLog);
            dt2.setLineT(2);
            dt2.setProperties(1,0,"Low MA", _cvm.getLineDrawColor());
            graph.add(dt2);
            DrawTool dt3 = new LineDraw(cvm,cdm);
            dt3.setShowZeroValue(false);
            dt3.setLog(cvm.isLog);
            dt3.setLineT(2);
            dt3.setProperties(1,0,"MAC Lower", _cvm.getLineDrawColor());
            graph.add(dt3);
        }else if(graphtitle.equals("MFI")){
            graph = new MFIGraph(_cvm,_cdm);
            graph.setProperties(0,showGraphName,ChartUtil.MFI);
            dt = new LineDraw(_cvm,_cdm);
            dt.setShowZeroValue(true);
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
            graph.add(dt);
            DrawTool dt1 = new LineDraw(_cvm,_cdm);
            dt1.setProperties(1,0,"MFI Signal", _cvm.getLineDrawColor());
            graph.add(dt1);
        }else if(graphtitle.equals("NVI")){
            graph = new NVIGraph(cvm,cdm);
            graph.setProperties(0,showGraphName,ChartUtil.NVI);
            dt = new LineDraw(cvm,cdm);
            dt.setShowZeroValue(false);
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
            graph.add(dt);
            //2012. 8. 8 NVI Signal 색굵기 없음 해결 : I68
            DrawTool dt1 = new LineDraw(cvm,cdm);
            dt1.setShowZeroValue(false);
            dt1.setProperties(1,0,"NVI Signal", _cvm.getLineDrawColor());
            graph.add(dt1);
        }else if(graphtitle.equals("TRIX")){
            graph = new TrixGraph(cvm,cdm);
            graph.setProperties(0,showGraphName,ChartUtil.TRIX);
            dt = new LineDraw(cvm,cdm);
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
            DrawTool dt1 = new LineDraw(cvm,cdm);
            dt1.setProperties(1,0,"TRIX Signal", _cvm.getLineDrawColor());
            graph.add(dt);
            graph.add(dt1);
            //2012. 8. 8 제목다름 : I74
        }else if(graphtitle.equals("Mass Index")){
            graph = new MIGraph(cvm,cdm);
            graph.setProperties(0,showGraphName,ChartUtil.MI);
            dt = new LineDraw(cvm,cdm);
            dt.setShowZeroValue(false);
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
            graph.add(dt);
            DrawTool dt1 = new LineDraw(cvm,cdm);
            dt1.setProperties(1,0,"MI Signal", _cvm.getLineDrawColor());
            graph.add(dt1);
            //2012. 8. 8  선굵기 색상 다름 : I76
        }else if(graphtitle.equals("Volume Oscillator")){//VMAO=OSCP
            graph = new VMAOGraph(cvm,cdm);
            graph.setProperties(0,showGraphName,ChartUtil.VMAO);
            dt = new BarDraw(cvm,cdm);
            dt.setProperties(2,2,graphtitle, _cvm.getLineDrawColor());
            graph.add(dt);
            DrawTool dt1 = new LineDraw(cvm,cdm);
            dt1.setProperties(1,0,"Volume Oscillator Signal", _cvm.getLineDrawColor());
            graph.add(dt1);
        }
        else if(graphtitle.equals("매물대")){
            graph = new VolumeforSaleGraph(cvm,cdm);
            graph.setProperties(2, showGraphName,ChartUtil.STANDSCALE);
            String[] data = {"종가","기본거래량"};
            graph.setDatakind(data);
            dt = new BarDraw(cvm,cdm);
            //2013.04.01 by LYH >> 대기매물 개선 
            //dt.setProperties(2,1,"대기매물", CoSys.CHART_COLORS[5]);
            //dt.setProperties(2,1,"대기매물", CoSys.CHART_COLORS[0]);

            dt.setProperties(2,1,graphtitle, CoSys.VOLUMESCALE_BASE);
            dt.setDownColor(CoSys.VOLUMESCALE_MAX);
            dt.setSameColor(CoSys.VOLUMESCALE_CUR);

            //2013.04.01 by LYH <<
            graph.add(dt);

            //2013. 9. 5 매물대설정 값 문자열 보이기 여부 처리>>
            //매물대 추가될 때는  수치표시 상태가  참. 
            graph.getDrawTool().get(0).setStandScaleLabelShow(true);
            //2013. 9. 5 매물대설정 값 문자열 보이기 여부 처리>>
        }
        else if(graphtitle.equals("PnF")){
            setBlockType(standBlock);
            graph = new PnfGraph(cvm,cdm);
            graph.setProperties(3, showGraphName,ChartUtil.PNF);
            dt = new PnFDraw(cvm,cdm);
            dt.setProperties(4,2,graphtitle, CoSys.STANDGRAPH_BASE_COLOR);
            graph.add(dt);
        }
        else if(graphtitle.equals("분산형")){
            setBlockType(STAND_BLOCK);

            graph = new VarianceGraph(cvm,cdm);
            graph.setProperties(3,"분산형",ChartUtil.VARIANCE);
            String[] data = {"종가","기본거래량"};
            graph.setDatakind(data);
            dt = new VarianceDraw(_cvm,_cdm);
            dt.setProperties(4,1,"분산형", _cvm.getLineDrawColor());
            graph.add(dt);
        }
        else if(graphtitle.equals("역시계곡선")){
            setBlockType(STAND_BLOCK);

            graph = new ReverseClockGraph(cvm,cdm);
            graph.setProperties(3,"역시계곡선",ChartUtil.REVERSE_CLOCK);
            String[] data = {"종가","기본거래량"};
            graph.setDatakind(data);
            dt = new ReverseClockDraw(_cvm,_cdm);
            dt.setProperties(4,1,"역시계곡선", _cvm.getLineDrawColor());
            graph.add(dt);
        }
        else if(graphtitle.equals("삼선전환도")){
            setBlockType(standBlock);
            graph = new ThirdChangeGraph(cvm,cdm);
            graph.setProperties(3,showGraphName,ChartUtil.TCHANGE);
            dt = new ThirdChangeDraw(cvm,cdm);
            dt.setProperties(4,0,graphtitle, CoSys.CHART_COLORS[4]);
            graph.add(dt);
        }
        else if(graphtitle.equals("스윙")){
            setBlockType(STAND_BLOCK);
            graph = new SwingGraph(cvm,cdm);
            graph.setProperties(3,showGraphName,ChartUtil.TCHANGE);
            dt = new SwingDraw(cvm,cdm);
            dt.setProperties(4,0,graphtitle, CoSys.STANDGRAPH_BASE_COLOR);
            graph.add(dt);
        }
        //2015.06.23 by lyk - 차트 유형 추가
        else if(graphtitle.equals("Kagi")){
            setBlockType(STAND_BLOCK);
            graph = new KagiGraph(cvm,cdm);
            graph.setProperties(3,showGraphName,ChartUtil.TCHANGE);
            dt = new KagiDraw(cvm,cdm);
            dt.setProperties(4,0,graphtitle, CoSys.STANDGRAPH_BASE_COLOR);
            graph.add(dt);
        }
        //2015.06.23 by lyk - 차트 유형 추가 end
        else if(graphtitle.equals("렌코")){
            setBlockType(STAND_BLOCK);
            graph = new RenkoGraph(cvm,cdm);
            graph.setProperties(3,showGraphName,ChartUtil.RENKO);
            dt = new RenkoDraw(cvm,cdm);
            dt.setProperties(4,0,graphtitle, CoSys.STANDGRAPH_BASE_COLOR);
            graph.add(dt);

            DrawTool dt1 = new LineDraw(cvm,cdm);
            dt1.setShowZeroValue(false);
//            dt1.setLineT(2);
            dt1.setProperties(1,0,"렌코이평5", CoSys.STANDGRAPH_MOVE_COLOR1);
            graph.add(dt1);

            DrawTool dt2 = new LineDraw(cvm,cdm);
            dt2.setShowZeroValue(false);
//            dt2.setLineT(2);
            dt2.setProperties(1,0,"렌코이평10", CoSys.STANDGRAPH_MOVE_COLOR2);
            graph.add(dt2);

            DrawTool dt3 = new LineDraw(cvm,cdm);
            dt3.setShowZeroValue(false);
//            dt3.setLineT(2);
            dt3.setProperties(1,0,"렌코이평20", CoSys.STANDGRAPH_MOVE_COLOR3);
            graph.add(dt3);
        }
        else if(graphtitle.equals("Heikin-Ashi")){
            graph = new JapanBongGraph(cvm,cdm);
            graph.setProperties(0,graphtitle,ChartUtil.JBONG);
            dt = new BongDraw(cvm,cdm);
            dt.setProperties(0,2,graphtitle, CoSys.CHART_COLORS[0]);
            dt.setDownColor(CoSys.CHART_COLORS[1]);
            dt.setSameColor(CoSys.CHART_COLORS[2]);
            graph.add(dt);

            if (!COMUtil.getSendTrType().equals("storageType") &&
                    COMUtil.divideStorageType==false)
            {
                if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
                    cvm.setViewNum(100);
                else {
                    if(_cvm.bIsNewsChart)
                        cvm.setViewNum(80);
                    else
                        cvm.setViewNum(60);
                }
            }
        }
        else if(graphtitle.equals("Multi")){
            graph = new MultiGraph(cvm,cdm);
            graph.setProperties(2,"Multi",ChartUtil.ENVELOPE);
//            String[] lineNames = {"업종1Rate","업종2Rate","종목1Rate", "종목2Rate", "종목3Rate"};
//            for(int i=0; i<lineNames.length; i++){
//                DrawTool dt = new LineDraw(_cvm,_cdm);
//                dt.setShowZeroValue(false);
//                dt.setProperties(1,0,lineNames[i], CoSys.CHART_COLOR[4+i] );
//                if(i>1)dt.setLineT(3);
//                graph.add(dt);
//            }
        }
        else if(graphtitle.equals("Zig Zag")){
            graph = new ZigzagGraph(cvm,cdm);
            graph.setProperties(2,showGraphName,ChartUtil.ZIGZAG);
            dt = new LineDraw(cvm,cdm);
            dt.setShowZeroValue(false);
            dt.setProperties(1,6,"Zig Zag", _cvm.getLineDrawColor());
            dt.setLineT(2);
            dt.setLog(cvm.isLog);
            graph.add(dt);
        }
        else if(graphtitle.equals("Demark")){
            graph = new DemarkGraph(cvm,cdm);
            graph.setProperties(2,showGraphName,ChartUtil.DEMARK);
            dt = new LineDraw(cvm,cdm);
            dt.setShowZeroValue(false);
            dt.setLog(cvm.isLog);
            dt.setLineT(2);
            dt.setProperties(1,0,"Demark 저항", CoSys.CHART_COLORS[4]);
            DrawTool dt1 = new LineDraw(cvm,cdm);
            dt1.setShowZeroValue(false);
            dt1.setLog(cvm.isLog);
            dt1.setLineT(2);
            dt1.setProperties(1,0,"Demark 기준", CoSys.CHART_COLORS[6]);
            DrawTool dt2 = new LineDraw(cvm,cdm);
            dt2.setShowZeroValue(false);
            dt2.setLog(cvm.isLog);
            dt2.setLineT(2);
            dt2.setProperties(1,0,"Demark 지지", CoSys.CHART_COLORS[10]);
            graph.add(dt);
            graph.add(dt1);
            graph.add(dt2);
        }
        //2014.01.11 by LYH >> Price Channel 지표 추가
        else if(graphtitle.equals("Price Channel")){
            graph = new PriceChannelGraph(cvm,cdm);
            graph.setProperties(2,showGraphName,ChartUtil.PRICE_CHANNEL);
            //2012. 8. 8 색굵기 라인의 제목 다름 : I85
            String[] lineNames = {"High","Low"};
            int nColorIndex[] = {4,6};
            for(int i=0; i<lineNames.length; i++){
                dt = new LineDraw(cvm,cdm);
                dt.setShowZeroValue(false);
                dt.setProperties(1,0,lineNames[i], CoSys.CHART_COLORS[(nColorIndex[i])] );
                dt.setLog(cvm.isLog);
                dt.setLineT(2);
                graph.add(dt);
            }
        }
        //2014.01.11 by LYH << Price Channel 지표 추가
        //2015.06.08 by lyk - 신규지표 추가
        else if(graphtitle.equals("RCI")){
            graph = new RCIGraph(cvm,cdm);
            graph.setProperties(0,showGraphName,ChartUtil.RCI);
            dt = new LineDraw(cvm,cdm);
            dt.setProperties(1,0,"RCI1", _cvm.getLineDrawColor());
            graph.add(dt);
            DrawTool dt1 = new LineDraw(cvm,cdm);
            dt1.setProperties(1,0,"RCI2", _cvm.getLineDrawColor());
            graph.add(dt1);
            DrawTool dt2 = new LineDraw(cvm,cdm);
            dt2.setProperties(1,0,"RCI3", _cvm.getLineDrawColor());
            graph.add(dt2);
//            DrawTool dt3 = new LineDraw(cvm,cdm);
//            dt3.setProperties(1,0,"RCI4", CoSys.CHART_COLORS[7]);
//            graph.add(dt3);
        }
        else if(graphtitle.equals("ROC")){
            graph = new ROCGraph(cvm,cdm);
            graph.setProperties(0,showGraphName,ChartUtil.ROC);
            dt = new LineDraw(cvm,cdm);
            dt.setProperties(1,0,showGraphName, _cvm.getLineDrawColor());
            graph.add(dt);
            DrawTool dt1 = new LineDraw(cvm,cdm);
            dt1.setProperties(1,0,"ROC Signal", _cvm.getLineDrawColor());
            graph.add(dt1);
        }
        else if(graphtitle.equals("SROC")){
            graph = new SROCGraph(cvm,cdm);
            graph.setProperties(0,showGraphName,ChartUtil.SROC);
            dt = new LineDraw(cvm,cdm);
            dt.setProperties(1,0,showGraphName, _cvm.getLineDrawColor());
            graph.add(dt);
            DrawTool dt1 = new LineDraw(cvm,cdm);
            dt1.setProperties(1,0,"SROC Signal", _cvm.getLineDrawColor());
            graph.add(dt1);
        }
        else if(graphtitle.equals("VROC")){
            graph = new VROCGraph(cvm,cdm);
            graph.setProperties(0,showGraphName,ChartUtil.VROC);
            dt = new LineDraw(cvm,cdm);
            dt.setProperties(1,0,showGraphName, _cvm.getLineDrawColor());
            graph.add(dt);
        }
        else if(graphtitle.equals("NCO")){
            graph = new NCOGraph(cvm,cdm);
            graph.setProperties(0,showGraphName,ChartUtil.NCO);
            dt = new LineDraw(cvm,cdm);
            dt.setProperties(1,0,showGraphName, _cvm.getLineDrawColor());
            graph.add(dt);
            DrawTool dt1 = new LineDraw(cvm,cdm);
            dt1.setProperties(1,0,"NCO Signal", _cvm.getLineDrawColor());
            graph.add(dt1);
        }
        else if(graphtitle.equals("Chaikins Volatility")){
            graph = new CVGraph(cvm,cdm);
            graph.setProperties(0,showGraphName,ChartUtil.CV);
            dt = new LineDraw(cvm,cdm);
            dt.setProperties(1,0,showGraphName, _cvm.getLineDrawColor());
            graph.add(dt);
            DrawTool dt1 = new LineDraw(cvm,cdm);
            dt1.setProperties(1,0,"CV Signal", _cvm.getLineDrawColor());
            graph.add(dt1);
        }
        else if(graphtitle.equals("RMI")){
            graph = new RMIGraph(cvm,cdm);
            graph.setProperties(0,showGraphName,ChartUtil.RMI);
            dt = new LineDraw(cvm,cdm);
            dt.setShowZeroValue(true);
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
            graph.add(dt);
//            DrawTool dt1 = new LineDraw(cvm,cdm);
//            dt1.setShowZeroValue(true);
//            dt1.setProperties(1,0,"RMI Signal", CoSys.CHART_COLORS[6]);
//            graph.add(dt1);
        }
        else if(graphtitle.equals("VHF")){
            graph = new VHFGraph(cvm,cdm);
            graph.setProperties(0,showGraphName,ChartUtil.VHF);
            dt = new LineDraw(cvm,cdm);
            dt.setShowZeroValue(true);
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
            graph.add(dt);
//            DrawTool dt1 = new LineDraw(cvm,cdm);
//            dt1.setShowZeroValue(true);
//            dt1.setProperties(1,0,"RMI Signal", CoSys.CHART_COLORS[6]);
//            graph.add(dt1);
        }
        else if(graphtitle.equals("Sigma")){
            graph = new SigmaGraph(cvm,cdm);
            graph.setProperties(2,showGraphName,ChartUtil.SIGMA);
            //2012. 8. 8  선굵기 설정의 각 이름 다름 : I84
            dt = new LineDraw(cvm,cdm);
            dt.setShowZeroValue(false);
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
//            dt.setLineT(2);
            graph.add(dt);
        }
        else if(graphtitle.equals("ATR")){
            graph = new ATRGraph(cvm,cdm);
            graph.setProperties(2,showGraphName,ChartUtil.ATR);
            //2012. 8. 8  선굵기 설정의 각 이름 다름 : I84
            dt = new LineDraw(cvm,cdm);
            dt.setShowZeroValue(false);
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
//            dt.setLineT(2);
            graph.add(dt);
            DrawTool dt1 = new LineDraw(cvm,cdm);
            dt1.setProperties(1,0,"ATR Signal", _cvm.getLineDrawColor());
            graph.add(dt1);
        }
        else if(graphtitle.equals("Price Oscillator")){
            graph = new OSCPGraph(cvm,cdm);
            graph.setProperties(0,showGraphName,ChartUtil.OSCP);
            //2012. 8. 8  선굵기 설정의 각 이름 다름 : I84
            dt = new LineDraw(cvm,cdm);
            dt.setShowZeroValue(false);
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
//            dt.setLineT(2);
            graph.add(dt);
            DrawTool dt1 = new LineDraw(cvm,cdm);
            dt1.setProperties(1,0,"OSCP Signal", _cvm.getLineDrawColor());
            graph.add(dt1);
        }
//        else if(graphtitle.equals("Volume Oscillator")){
//            graph = new OSCVGraph(cvm,cdm);
//            graph.setProperties(2,showGraphName,ChartUtil.OSCV);
//            //2012. 8. 8  선굵기 설정의 각 이름 다름 : I84
//            dt = new LineDraw(cvm,cdm);
//            dt.setShowZeroValue(false);
//            dt.setProperties(1,0,graphtitle, CoSys.CHART_COLORS[5] );
////            dt.setLineT(2);
//            dt.setLog(cvm.isLog);
//            graph.add(dt);
//        }
        else if(graphtitle.equals("PVT")){
            graph = new PVTGraph(cvm,cdm);
            graph.setProperties(0,showGraphName,ChartUtil.PVT);
            dt = new LineDraw(cvm,cdm);
            dt.setProperties(1,0,showGraphName, _cvm.getLineDrawColor());
            graph.add(dt);
            DrawTool dt1 = new LineDraw(cvm,cdm);
            dt1.setProperties(1,0,"PVT Signal", _cvm.getLineDrawColor());
            graph.add(dt1);
        }
        else if(graphtitle.equals("LRL")){
            graph = new LRLGraph(_cvm,_cdm);
            graph.setProperties(0,showGraphName,ChartUtil.LRL);
            dt = new LineDraw(_cvm,_cdm);
            dt.setShowZeroValue(false);
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
            graph.add(dt);
//            DrawTool dt1 = new LineDraw(_cvm,_cdm);
//            dt1.setProperties(1,0,"LRL Signal", CoSys.CHART_COLORS[4]);
//            graph.add(dt1);
        }
        else if(graphtitle.equals("뉴스건수")){
            graph = new MarketGraph(_cvm,_cdm);
            graph.setProperties(0,showGraphName,ChartUtil.LRL);
            dt = new BarDraw(_cvm,_cdm);
            dt.setShowZeroValue(true);
//            dt.setProperties(1,0,graphtitle, CoSys.CHART_COLORS[7]);
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
            graph.add(dt);
        }
        //2015.06.08 by lyk - 신규지표 추가 end
        else if(graphtitle.equals("Band %B")){
            graph = new BandBGraph(_cvm,_cdm);
            graph.setProperties(2,showGraphName,ChartUtil.BANDB);
            //2012. 8. 8  선굵기 설정의 각 이름 다름 : I84
            dt = new LineDraw(_cvm,_cdm);
            dt.setShowZeroValue(false);
            dt.setProperties(1,0,"Band%B", _cvm.getLineDrawColor());
//            dt.setLineT(2);
//            dt.setLog(_cvm.isLog);
            graph.add(dt);
            DrawTool dt1 = new LineDraw(_cvm,_cdm);
            dt1.setShowZeroValue(true);
            dt1.setProperties(1,0,"Band%B_Signal", _cvm.getLineDrawColor());
            graph.add(dt1);
        }
        //2017.05.11 by LYH >> 전략(신호, 강약) 추가
        else if(graphtitle.equals("ADXRStrategy신호") || graphtitle.equals("ADXRStrategy강세약세")){
            graph = new ADXRStrategySignal(_cvm,_cdm);
            graph.setProperties(4,showGraphName,ChartUtil.INDICATOR_ADXRSTRATEGY);
            dt = new SignalDraw(_cvm, _cdm);
//            dt.setShowZeroValue(true);
//            dt.setProperties(1,0,graphtitle, CoSys.CHART_COLORS[7]);
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
            graph.add(dt);
        }
        else if(graphtitle.equals("CCIBaseLine신호") || graphtitle.equals("CCIBaseLine강세약세")){
            graph = new CCIBaseLineSignal(_cvm,_cdm);
            graph.setProperties(4,showGraphName,ChartUtil.INDICATOR_CCIBASELINE);
            dt = new SignalDraw(_cvm, _cdm);
//            dt.setShowZeroValue(true);
//            dt.setProperties(1,0,graphtitle, CoSys.CHART_COLORS[7]);
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
            graph.add(dt);
        }
        else if(graphtitle.equals("CCIOverSoldOverBought신호") || graphtitle.equals("CCIOverSoldOverBought강세약세")){
            graph = new CCIOverSoldOverBoughtSignal(_cvm,_cdm);
            graph.setProperties(4,showGraphName,ChartUtil.INDICATOR_CCIOVERSOLDOVERBOUGHT);
            dt = new SignalDraw(_cvm, _cdm);
//            dt.setShowZeroValue(true);
//            dt.setProperties(1,0,graphtitle, CoSys.CHART_COLORS[7]);
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
            graph.add(dt);
        }
        else if(graphtitle.equals("Disparity신호") || graphtitle.equals("Disparity강세약세")){
            graph = new DisparitySignal(_cvm,_cdm);
            graph.setProperties(4,showGraphName,ChartUtil.INDICATOR_DISPARITYSIGNAL);
            dt = new SignalDraw(_cvm, _cdm);
//            dt.setShowZeroValue(true);
//            dt.setProperties(1,0,graphtitle, CoSys.CHART_COLORS[7]);
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
            graph.add(dt);
        }
        else if(graphtitle.equals("DMI신호") || graphtitle.equals("DMI강세약세")){
            graph = new DMISignal(_cvm,_cdm);
            graph.setProperties(4,showGraphName,ChartUtil.INDICATOR_DMISIGNAL);
            dt = new SignalDraw(_cvm, _cdm);
//            dt.setShowZeroValue(true);
//            dt.setProperties(1,0,graphtitle, CoSys.CHART_COLORS[7]);
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
            graph.add(dt);
        }
        else if(graphtitle.equals("GoldenDeadCrossEMA신호") || graphtitle.equals("GoldenDeadCrossEMA강세약세")){
            graph = new GoldenDeadCrossEMASignal(_cvm,_cdm);
            graph.setProperties(4,showGraphName,ChartUtil.INDICATOR_GOLDENDEADCROSSEMA);
            dt = new SignalDraw(_cvm, _cdm);
//            dt.setShowZeroValue(true);
//            dt.setProperties(1,0,graphtitle, CoSys.CHART_COLORS[7]);
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
            graph.add(dt);
        }
        //else if(graphtitle.equals("GoldenDeadCross신호") || graphtitle.equals("GoldenDeadCross강세약세")){
        else if(graphtitle.equals("이동평균 크로스 신호") || graphtitle.equals("이동평균 크로스 강세약세")){
            graph = new GoldenDeadCrossSignal(_cvm,_cdm);
            graph.setProperties(4,showGraphName,ChartUtil.INDICATOR_GOLDENDEADCROSS);
            dt = new SignalDraw(_cvm, _cdm);
//            dt.setShowZeroValue(true);
//            dt.setProperties(1,0,graphtitle, CoSys.CHART_COLORS[7]);
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
            graph.add(dt);
        }
        else if(graphtitle.equals("MACDBaseLine신호") || graphtitle.equals("MACDBaseLine강세약세")){
            graph = new MACDBaseLineSignal(_cvm,_cdm);
            graph.setProperties(4,showGraphName,ChartUtil.INDICATOR_MACDBASELINE);
            dt = new SignalDraw(_cvm, _cdm);
//            dt.setShowZeroValue(true);
//            dt.setProperties(1,0,graphtitle, CoSys.CHART_COLORS[7]);
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
            graph.add(dt);
        }
        else if(graphtitle.equals("Parabolic SAR 신호") || graphtitle.equals("Parabolic SAR 강세약세")){
            graph = new ParabolicSignal(_cvm,_cdm);
            graph.setProperties(4,showGraphName,ChartUtil.INDICATOR_PARABOLICSIGNAL);
            dt = new SignalDraw(_cvm, _cdm);
//            dt.setShowZeroValue(true);
//            dt.setProperties(1,0,graphtitle, CoSys.CHART_COLORS[7]);
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
            graph.add(dt);
        }
        else if(graphtitle.equals("SonarMomentum신호") || graphtitle.equals("SonarMomentum강세약세")){
            graph = new SonarMomentumSignal(_cvm,_cdm);
            graph.setProperties(4,showGraphName,ChartUtil.INDICATOR_SONARMOMENTUM);
            dt = new SignalDraw(_cvm, _cdm);
//            dt.setShowZeroValue(true);
//            dt.setProperties(1,0,graphtitle, CoSys.CHART_COLORS[7]);
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
            graph.add(dt);
        }
        else if(graphtitle.equals("SonarMomentumSignal신호") || graphtitle.equals("SonarMomentumSignal강세약세")){
            graph = new SonarMomentumSignalSignal(_cvm,_cdm);
            graph.setProperties(4,showGraphName,ChartUtil.INDICATOR_SONARMOMENTUMSIGNAL);
            dt = new SignalDraw(_cvm, _cdm);
//            dt.setShowZeroValue(true);
//            dt.setProperties(1,0,graphtitle, CoSys.CHART_COLORS[7]);
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
            graph.add(dt);
        }
        else if(graphtitle.equals("Stochastics 신호") || graphtitle.equals("Stochastics 강세약세")){
            graph = new StochasticsK_DSignal(_cvm,_cdm);
            graph.setProperties(4,showGraphName,ChartUtil.INDICATOR_STOCHASTICSKD);
            dt = new SignalDraw(_cvm, _cdm);
//            dt.setShowZeroValue(true);
//            dt.setProperties(1,0,graphtitle, CoSys.CHART_COLORS[7]);
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
            graph.add(dt);
        }
        else if(graphtitle.equals("WilliamsR신호") || graphtitle.equals("WilliamsR강세약세")){
            graph = new WilliamsRSignal(_cvm,_cdm);
            graph.setProperties(4,showGraphName,ChartUtil.INDICATOR_WILLIAMSR);
            dt = new SignalDraw(_cvm, _cdm);
//            dt.setShowZeroValue(true);
//            dt.setProperties(1,0,graphtitle, CoSys.CHART_COLORS[7]);
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
            graph.add(dt);
        }
        //2017.05.11 by LYH >> 전략(신호, 강약) 추가 end
        else if(graphtitle.equals("단순지수 크로스 신호") || graphtitle.equals("단순지수 크로스 강세약세")){
            graph = new GoldenDeadCrossMA_EMASignal(_cvm,_cdm);
            graph.setProperties(4,showGraphName,ChartUtil.INDICATOR_GOLDENDEADCROSSMA_EMA);
            dt = new SignalDraw(_cvm, _cdm);
//            dt.setShowZeroValue(true);
//            dt.setProperties(1,0,graphtitle, CoSys.CHART_COLORS[7]);
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
            graph.add(dt);
        }
        else if(graphtitle.equals("단순가중 크로스 신호") || graphtitle.equals("단순가중 크로스 강세약세")){
            graph = new GoldenDeadCrossMA_WMASignal(_cvm,_cdm);
            graph.setProperties(4,showGraphName,ChartUtil.INDICATOR_GOLDENDEADCROSSMA_WMA);
            dt = new SignalDraw(_cvm, _cdm);
//            dt.setShowZeroValue(true);
//            dt.setProperties(1,0,graphtitle, CoSys.CHART_COLORS[7]);
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
            graph.add(dt);
        }
        else if(graphtitle.equals("멀티 이평크로스 신호") || graphtitle.equals("멀티 이평크로스 강세약세")){
            graph = new GoldenDeadCross_MultiSignal(_cvm,_cdm);
            graph.setProperties(4,showGraphName,ChartUtil.INDICATOR_GOLDENDEADCROSS_MULTI);
            dt = new SignalDraw(_cvm, _cdm);
//            dt.setShowZeroValue(true);
//            dt.setProperties(1,0,graphtitle, CoSys.CHART_COLORS[7]);
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
            graph.add(dt);
        }
        else if(graphtitle.equals("MACD 크로스 신호") || graphtitle.equals("MACD 크로스 강세약세")){
            graph = new MACDSignal(_cvm,_cdm);
            graph.setProperties(4,showGraphName,ChartUtil.INDICATOR_MACDSIGNAL);
            dt = new SignalDraw(_cvm, _cdm);
//            dt.setShowZeroValue(true);
//            dt.setProperties(1,0,graphtitle, CoSys.CHART_COLORS[7]);
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
            graph.add(dt);
        }
        else if(graphtitle.equals("MAO 지수크로스 신호") || graphtitle.equals("MAO 지수크로스 강세약세")){
            graph = new MAOBaseLineSignal(_cvm,_cdm);
            graph.setProperties(4,showGraphName,ChartUtil.INDICATOR_MAOBASELINE);
            dt = new SignalDraw(_cvm, _cdm);
//            dt.setShowZeroValue(true);
//            dt.setProperties(1,0,graphtitle, CoSys.CHART_COLORS[7]);
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
            graph.add(dt);
        }
        else if(graphtitle.equals("PVI 크로스 신호") || graphtitle.equals("PVI 크로스 강세약세")){
            graph = new PVISignal(_cvm,_cdm);
            graph.setProperties(4,showGraphName,ChartUtil.INDICATOR_PVISIGNAL);
            dt = new SignalDraw(_cvm, _cdm);
//            dt.setShowZeroValue(true);
//            dt.setProperties(1,0,graphtitle, CoSys.CHART_COLORS[7]);
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
            graph.add(dt);
        }
        //2017.06.07 해외선옵 FX 매수+매도 차트 >>
        else if(graphtitle.equals("팔때")){
            graph = new MarketGraph(cvm,cdm);
            graph.setProperties(2,showGraphName,ChartUtil.PIVOT);
            dt = new LineDraw(cvm,cdm);
            dt.setShowZeroValue(false);
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
            dt.setTitle(graphtitle);
            dt.setLineT(2);
            dt.setLog(cvm.isLog);
            graph.add(dt);
        }
        //2017.06.07 해외선옵 FX 매수+매도 차트 <<
        else if(graphtitle.equals("가격 & Box")){
            graph = new PriceBoxGraph(cvm,cdm);
            graph.setProperties(2,showGraphName,ChartUtil.PRICEBOX);
            //2012. 8. 8 색굵기 라인의 제목 다름 : I85
            String[] lineNames = {"Box_상한","Box_중심","Box_하한"};
            int nColorIndex[] = {5,4,6};
            for(int i=0; i<lineNames.length; i++){
                dt = new LineDraw(cvm,cdm);
                dt.setShowZeroValue(false);
                dt.setProperties(1,0,lineNames[i], CoSys.CHART_COLORS[(nColorIndex[i])] );
                dt.setLog(cvm.isLog);
                dt.setLineT(2);
                graph.add(dt);
            }
        }
        else if(graphtitle.equals("%B+Stochastic")){
            graph = new BandBStochGraph(_cvm,_cdm);
            graph.setProperties(2,showGraphName,ChartUtil.BANDBSTOCH);
            //2012. 8. 8  선굵기 설정의 각 이름 다름 : I84
            dt = new LineDraw(_cvm,_cdm);
            dt.setShowZeroValue(false);
            dt.setProperties(1,0,"%B+Slow%K", _cvm.getLineDrawColor());
            graph.add(dt);
            DrawTool dt1 = new LineDraw(_cvm,_cdm);
            dt1.setShowZeroValue(true);
            dt1.setProperties(1,0,"%B+Slow%D", _cvm.getLineDrawColor());
            graph.add(dt1);
        }
        else if(graphtitle.equals("LRS+Stochastic")){
            graph = new LRSStochSlowGraph(_cvm,_cdm);
            graph.setProperties(2,showGraphName,ChartUtil.LRSSTOCH);
            //2012. 8. 8  선굵기 설정의 각 이름 다름 : I84
            dt = new LineDraw(_cvm,_cdm);
            dt.setShowZeroValue(false);
            dt.setProperties(1,0,"LRS+Slow%K", _cvm.getLineDrawColor());
            graph.add(dt);
            DrawTool dt1 = new LineDraw(_cvm,_cdm);
            dt1.setShowZeroValue(true);
            dt1.setProperties(1,0,"LRS+Slow%D", _cvm.getLineDrawColor());
            graph.add(dt1);
        }
        else if(graphtitle.equals("MACD+Stochastic")){
            graph = new MACDStochSlowGraph(_cvm,_cdm);
            graph.setProperties(2,showGraphName,ChartUtil.MACDSTOCH);
            //2012. 8. 8  선굵기 설정의 각 이름 다름 : I84
            dt = new LineDraw(_cvm,_cdm);
            dt.setShowZeroValue(false);
            dt.setProperties(1,0,"MACD+Slow%K", _cvm.getLineDrawColor());
            graph.add(dt);
            DrawTool dt1 = new LineDraw(_cvm,_cdm);
            dt1.setShowZeroValue(true);
            dt1.setProperties(1,0,"MACD+Slow%D", _cvm.getLineDrawColor());
            graph.add(dt1);
        }
        else if(graphtitle.equals("Momentum+Stochastic")){
            graph = new MomentumStochSlowGraph(_cvm,_cdm);
            graph.setProperties(2,showGraphName,ChartUtil.MOMENTUMSTOCH);
            //2012. 8. 8  선굵기 설정의 각 이름 다름 : I84
            dt = new LineDraw(_cvm,_cdm);
            dt.setShowZeroValue(false);
            dt.setProperties(1,0,"Momen+Slow%K", _cvm.getLineDrawColor());
            graph.add(dt);
            DrawTool dt1 = new LineDraw(_cvm,_cdm);
            dt1.setShowZeroValue(true);
            dt1.setProperties(1,0,"Momen+Slow%D", _cvm.getLineDrawColor());
            graph.add(dt1);
        }
        else if(graphtitle.equals("OBV+Momentum")){
            graph = new OBVMomentumGraph(_cvm,_cdm);
            graph.setProperties(2,showGraphName,ChartUtil.OBVMOMENTUM);
            //2012. 8. 8  선굵기 설정의 각 이름 다름 : I84
            dt = new LineDraw(_cvm,_cdm);
            dt.setShowZeroValue(false);
            dt.setProperties(1,0,"OBVMomentum", _cvm.getLineDrawColor());
            graph.add(dt);
            DrawTool dt1 = new LineDraw(_cvm,_cdm);
            dt1.setShowZeroValue(true);
            dt1.setProperties(1,0,"OBVMomentum_Signal", _cvm.getLineDrawColor());
            graph.add(dt1);
        }
        else if(graphtitle.equals("OBV+Stochastic")){
            graph = new OBVStochSlowGraph(_cvm,_cdm);
            graph.setProperties(2,showGraphName,ChartUtil.OBVSTOCH);
            //2012. 8. 8  선굵기 설정의 각 이름 다름 : I84
            dt = new LineDraw(_cvm,_cdm);
            dt.setShowZeroValue(false);
            dt.setProperties(1,0,"OBV+Slow%K", _cvm.getLineDrawColor());
            graph.add(dt);
            DrawTool dt1 = new LineDraw(_cvm,_cdm);
            dt1.setShowZeroValue(true);
            dt1.setProperties(1,0,"OBV+Slow%D", _cvm.getLineDrawColor());
            graph.add(dt1);
        }
        else if(graphtitle.equals("ROC+Stochastic")){
            graph = new ROCStochSlowGraph(_cvm,_cdm);
            graph.setProperties(2,showGraphName,ChartUtil.ROCSTOCH);
            //2012. 8. 8  선굵기 설정의 각 이름 다름 : I84
            dt = new LineDraw(_cvm,_cdm);
            dt.setShowZeroValue(false);
            dt.setProperties(1,0,"ROC+Slow%K", _cvm.getLineDrawColor());
            graph.add(dt);
            DrawTool dt1 = new LineDraw(_cvm,_cdm);
            dt1.setShowZeroValue(true);
            dt1.setProperties(1,0,"ROC+Slow%D", _cvm.getLineDrawColor());
            graph.add(dt1);
        }
        else if(graphtitle.equals("RSI+MACD")){
            graph = new RSIMACDGraph(_cvm,_cdm);
            graph.setProperties(2,showGraphName,ChartUtil.RSIMACD);
            //2012. 8. 8  선굵기 설정의 각 이름 다름 : I84
            dt = new LineDraw(_cvm,_cdm);
            dt.setShowZeroValue(false);
            dt.setProperties(1,0,"RSI+MACD", _cvm.getLineDrawColor());
            graph.add(dt);
            DrawTool dt1 = new LineDraw(_cvm,_cdm);
            dt1.setShowZeroValue(true);
            dt1.setProperties(1,0,"RSI+MACD_Signal", _cvm.getLineDrawColor());
            graph.add(dt1);
        }
        else if(graphtitle.equals("RSI+Stochastic")){
            graph = new RSIStochSlowGraph(_cvm,_cdm);
            graph.setProperties(2,showGraphName,ChartUtil.RSISTOCH);
            //2012. 8. 8  선굵기 설정의 각 이름 다름 : I84
            dt = new LineDraw(_cvm,_cdm);
            dt.setShowZeroValue(false);
            dt.setProperties(1,0,"RSI+Slow%K", _cvm.getLineDrawColor());
            graph.add(dt);
            DrawTool dt1 = new LineDraw(_cvm,_cdm);
            dt1.setShowZeroValue(true);
            dt1.setProperties(1,0,"RSI+Slow%D", _cvm.getLineDrawColor());
            graph.add(dt1);
        }
        else if(graphtitle.equals("Sonar+심리도")){
            graph = new SonarPsycoGraph(_cvm,_cdm);
            graph.setProperties(2,showGraphName,ChartUtil.SONARPSYCO);
            //2012. 8. 8  선굵기 설정의 각 이름 다름 : I84
            dt = new LineDraw(_cvm,_cdm);
            dt.setShowZeroValue(true);
            dt.setProperties(1,0,"Sonar+심리도", _cvm.getLineDrawColor());
            graph.add(dt);
        }
        else if(graphtitle.equals("Stochastic+RSI")){
            graph = new StochasticRSIGraph(_cvm,_cdm);
            graph.setProperties(2,showGraphName,ChartUtil.STOCHRSI);
            //2012. 8. 8  선굵기 설정의 각 이름 다름 : I84
            dt = new LineDraw(_cvm,_cdm);
            dt.setShowZeroValue(false);
            dt.setProperties(1,0,"StochRSI", _cvm.getLineDrawColor());
            graph.add(dt);
            DrawTool dt1 = new LineDraw(_cvm,_cdm);
            dt1.setShowZeroValue(true);
            dt1.setProperties(1,0,"StochRSI_Signal", _cvm.getLineDrawColor());
            graph.add(dt1);
        }
        else if (graphtitle.equals("DEMA")) {
            graph = new DEMAGraph(_cvm,_cdm);
            graph.setProperties(2,showGraphName,ChartUtil.DEMA);
            dt = new LineDraw(_cvm,_cdm);
            dt.setShowZeroValue(false);
            dt.setProperties(1,0,"DEMA", _cvm.getLineDrawColor());
            dt.setLineT(2);
            dt.setLog(cvm.isLog);
            graph.add(dt);
        }
        else if (graphtitle.equals("TEMA")) {
            graph = new TEMAGraph(_cvm, _cdm);
            graph.setProperties(2,showGraphName,ChartUtil.TEMA);
            dt = new LineDraw(_cvm, _cdm);
            dt.setShowZeroValue(false);
            dt.setProperties(1,0, "TEMA", _cvm.getLineDrawColor());
            dt.setLineT(2);
            dt.setLog(cvm.isLog);
            graph.add(dt);
        }
        else if(graphtitle.equals("Starc Bands")){
            graph = new StarcBandsGraph(_cvm,_cdm);
            graph.setProperties(2,showGraphName,ChartUtil.STARC_BANDS);
            //2012. 8. 8 색굵기 라인의 제목 다름 : I85
            String[] lineNames = {"Starc 상한","Starc 중심","Starc 하한"};
            int nColorIndex[] = {5,4,6};
            for(int i=0; i<lineNames.length; i++){
                dt = new LineDraw(_cvm,_cdm);
                dt.setShowZeroValue(false);
                dt.setProperties(1,0,lineNames[i], CoSys.CHART_COLORS[(nColorIndex[i])] );
                dt.setLog(_cvm.isLog);
                dt.setInvertScale(_cvm.isInverse);	//2017.07.12 by LYH >> 거꾸로차트 기능 추가.
                graph.add(dt);
            }
        }
        else if(graphtitle.equals("ADXR")){
            graph = new ADXRGraph(_cvm,_cdm);
            graph.setProperties(2,showGraphName,ChartUtil.ADXR);
            dt = new LineDraw(_cvm,_cdm);
            dt.setShowZeroValue(true);
            dt.setProperties(1,0,"ADXR_ADX", _cvm.getLineDrawColor());
            graph.add(dt);
            DrawTool dt1 = new LineDraw(_cvm,_cdm);
            dt1.setShowZeroValue(true);
            dt1.setProperties(1,0,"ADXR", _cvm.getLineDrawColor());
            graph.add(dt1);
        }
        else if(graphtitle.equals("Band Width")){
            graph = new BandWidthGraph(_cvm,_cdm);
            graph.setProperties(2,showGraphName,ChartUtil.BANDWIDTH);
            dt = new LineDraw(_cvm,_cdm);
            dt.setShowZeroValue(false);
            dt.setProperties(1,0,"Band_Width", _cvm.getLineDrawColor());
            graph.add(dt);
        }
        else if(graphtitle.equals("BPDL Short Trend")){
            graph = new BPDLShortTrendGraph(_cvm,_cdm);
            graph.setProperties(2,showGraphName,ChartUtil.BPDL_SHORT_TREND);
            //2012. 8. 8  선굵기 설정의 각 이름 다름 : I84
            dt = new LineDraw(_cvm,_cdm);
            dt.setShowZeroValue(true);
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
//            dt.setLineT(2);
//            dt.setLog(_cvm.isLog);
            graph.add(dt);
        }
        else if(graphtitle.equals("BPDL Stochastic")){
            graph = new BPDLStochasticGraph(_cvm,_cdm);
            graph.setProperties(2,showGraphName,ChartUtil.BPDL_STOCH);
            //2012. 8. 8  선굵기 설정의 각 이름 다름 : I84
            dt = new LineDraw(_cvm,_cdm);
            dt.setShowZeroValue(false);
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
//            dt.setLineT(2);
//            dt.setLog(_cvm.isLog);
            graph.add(dt);
        }
        else if(graphtitle.equals("CMF")){
            graph = new CMFGraph(_cvm,_cdm);
            graph.setProperties(2,showGraphName,ChartUtil.CMF);
            //2012. 8. 8  선굵기 설정의 각 이름 다름 : I84
            dt = new LineDraw(_cvm,_cdm);
            dt.setShowZeroValue(false);
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
//            dt.setLineT(2);
//            dt.setLog(_cvm.isLog);
            graph.add(dt);
        }
        else if(graphtitle.equals("이격도[지수]")){
            //2020. 03. 26 by hyh - 이격도 2개에서 4개로 변경 >>
            graph = new DisparityIndexGraph(_cvm,_cdm);
            graph.setProperties(0,showGraphName,ChartUtil.DISPARITYINDEX);
            dt = new LineDraw(_cvm,_cdm);
            dt.setShowZeroValue(false);
            dt.setProperties(1,0,"이격률(지수)1", _cvm.getLineDrawColor());
            graph.add(dt);

            DrawTool dt1 = new LineDraw(_cvm,_cdm);
            dt1.setShowZeroValue(false);
            dt1.setProperties(1,0,"이격률2", _cvm.getLineDrawColor());
            graph.add(dt1);

            dt1 = new LineDraw(_cvm,_cdm);
            dt1.setShowZeroValue(false);
            dt1.setProperties(1,0,"이격률3", _cvm.getLineDrawColor());
            graph.add(dt1);

            dt1 = new LineDraw(_cvm,_cdm);
            dt1.setShowZeroValue(false);
            dt1.setProperties(1,0,"이격률4", _cvm.getLineDrawColor());
            graph.add(dt1);
        }
        else if(graphtitle.equals("DPO")){
            graph = new DPOGraph(_cvm,_cdm);
            graph.setProperties(2,showGraphName,ChartUtil.DPO);
            //2012. 8. 8  선굵기 설정의 각 이름 다름 : I84
            dt = new LineDraw(_cvm,_cdm);
            dt.setShowZeroValue(false);
            dt.setProperties(1,0,"DPO", _cvm.getLineDrawColor());
//            dt.setLineT(2);
//            dt.setLog(_cvm.isLog);
            graph.add(dt);
            DrawTool dt1 = new LineDraw(_cvm,_cdm);
            dt1.setShowZeroValue(true);
            dt1.setProperties(1,0,"DPO_Signal", _cvm.getLineDrawColor());
            graph.add(dt1);
        }
        else if(graphtitle.equals("DRF")){
            graph = new DRFGraph(_cvm,_cdm);
            graph.setProperties(2,showGraphName,ChartUtil.DRF);
            //2012. 8. 8  선굵기 설정의 각 이름 다름 : I84
            dt = new LineDraw(_cvm,_cdm);
            dt.setShowZeroValue(false);
            dt.setProperties(1,0,"DRF", _cvm.getLineDrawColor());
//            dt.setLineT(2);
//            dt.setLog(_cvm.isLog);
            graph.add(dt);
            DrawTool dt1 = new LineDraw(_cvm,_cdm);
            dt1.setShowZeroValue(true);
            dt1.setProperties(1,0,"DRF_Signal", _cvm.getLineDrawColor());
            graph.add(dt1);
        }
        else if(graphtitle.equals("Elder_Ray Bear Power")){
            graph = new ElderRayBearPowerGraph(_cvm,_cdm);
            graph.setProperties(2,showGraphName,ChartUtil.ELDER_RAY_BEAR_POWER);
            //2012. 8. 8  선굵기 설정의 각 이름 다름 : I84
            dt = new BarDraw(_cvm,_cdm);
            dt.setShowZeroValue(false);
            dt.setProperties(2,2,graphtitle, _cvm.getLineDrawColor());
//            dt.setLineT(2);
//            dt.setLog(_cvm.isLog);
            graph.add(dt);
        }
        else if(graphtitle.equals("Elder_Ray Bull Power")){
            graph = new ElderRayBullPowerGraph(_cvm,_cdm);
            graph.setProperties(2,showGraphName,ChartUtil.ELDER_RAY_BULL_POWER);
            //2012. 8. 8  선굵기 설정의 각 이름 다름 : I84
            dt = new BarDraw(_cvm,_cdm);
            dt.setShowZeroValue(false);
            dt.setProperties(2,2,graphtitle, _cvm.getLineDrawColor());
//            dt.setLineT(2);
//            dt.setLog(_cvm.isLog);
            graph.add(dt);
        }
        else if(graphtitle.equals("Force Index")){
            graph = new ForceIndexGraph(_cvm,_cdm);
            graph.setProperties(2,showGraphName,ChartUtil.FORCE_INDEX);
            //2012. 8. 8  선굵기 설정의 각 이름 다름 : I84
            dt = new LineDraw(_cvm,_cdm);
            dt.setShowZeroValue(false);
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
//            dt.setLineT(2);
//            dt.setLog(_cvm.isLog);
            graph.add(dt);
            DrawTool dt1 = new LineDraw(_cvm,_cdm);
            dt1.setProperties(1,0,"FI Signal", _cvm.getLineDrawColor());
            graph.add(dt1);
        }
        else if(graphtitle.equals("Force Index Long Term")){
            graph = new ForceIndexLongTermGraph(_cvm,_cdm);
            graph.setProperties(2,showGraphName,ChartUtil.FORCE_INDEX_LONG_TERM);
            //2012. 8. 8  선굵기 설정의 각 이름 다름 : I84
            dt = new LineDraw(_cvm,_cdm);
            dt.setShowZeroValue(false);
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
//            dt.setLineT(2);
//            dt.setLog(_cvm.isLog);
            graph.add(dt);
        }
        else if(graphtitle.equals("Force Index Short Term")){
            graph = new ForceIndexShortTermGraph(_cvm,_cdm);
            graph.setProperties(2,showGraphName,ChartUtil.FORCE_INDEX_SHORT_TERM);
            //2012. 8. 8  선굵기 설정의 각 이름 다름 : I84
            dt = new LineDraw(_cvm,_cdm);
            dt.setShowZeroValue(false);
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
//            dt.setLineT(2);
//            dt.setLog(_cvm.isLog);
            graph.add(dt);
        }
        else if(graphtitle.equals("Formula")){
            graph = new FormulaGraph(_cvm,_cdm);
            graph.setProperties(2,showGraphName,ChartUtil.FORMULA);
            //2012. 8. 8  선굵기 설정의 각 이름 다름 : I84
            dt = new LineDraw(_cvm,_cdm);
            dt.setShowZeroValue(false);
            dt.setProperties(1,0,"Formula", _cvm.getLineDrawColor());
//            dt.setLineT(2);
//            dt.setLog(_cvm.isLog);
            graph.add(dt);
        }
        else if(graphtitle.equals("GM McClellan Oscillator")){
            graph = new GMMcClellanOscillatorGraph(_cvm,_cdm);
            graph.setProperties(2,showGraphName,ChartUtil.GM_McCLELAN_OSC);
            //2012. 8. 8  선굵기 설정의 각 이름 다름 : I84
            dt = new LineDraw(_cvm,_cdm);
            dt.setShowZeroValue(false);
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
//            dt.setLineT(2);
//            dt.setLog(_cvm.isLog);
            graph.add(dt);
        }
        else if(graphtitle.equals("GM McClellan Summation")){
            graph = new GMMcClellanSummationGraph(_cvm,_cdm);
            graph.setProperties(2,showGraphName,ChartUtil.GM_McCLELAN_SUM);
            //2012. 8. 8  선굵기 설정의 각 이름 다름 : I84
            dt = new LineDraw(_cvm,_cdm);
            dt.setShowZeroValue(false);
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
//            dt.setLineT(2);
//            dt.setLog(_cvm.isLog);
            graph.add(dt);
        }
        else if(graphtitle.equals("LFI")){
            graph = new LFIGraph(_cvm,_cdm);
            graph.setProperties(2,showGraphName,ChartUtil.LFI);
            //2012. 8. 8  선굵기 설정의 각 이름 다름 : I84
            dt = new LineDraw(_cvm,_cdm);
            dt.setShowZeroValue(false);
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
//            dt.setLineT(2);
//            dt.setLog(_cvm.isLog);
            graph.add(dt);
        }
        else if(graphtitle.equals("Moving Balance Indicator")){
            graph = new MovingBalanceIndicatorGraph(_cvm,_cdm);
            graph.setProperties(2,showGraphName,ChartUtil.MOVING_BALANCE_INDICATOR);
            //2012. 8. 8  선굵기 설정의 각 이름 다름 : I84
            dt = new LineDraw(_cvm,_cdm);
            dt.setShowZeroValue(true);
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
//            dt.setLineT(2);
//            dt.setLog(_cvm.isLog);
            graph.add(dt);
        }
        else if(graphtitle.equals("NDI")){
            graph = new NDIGraph(_cvm,_cdm);
            graph.setProperties(2,showGraphName,ChartUtil.NDI);
            //2012. 8. 8  선굵기 설정의 각 이름 다름 : I84
            dt = new LineDraw(_cvm,_cdm);
            dt.setShowZeroValue(false);
            dt.setProperties(1,0,"NDI", _cvm.getLineDrawColor());
//            dt.setLineT(2);
//            dt.setLog(_cvm.isLog);
            graph.add(dt);
            DrawTool dt1 = new LineDraw(_cvm,_cdm);
            dt1.setShowZeroValue(true);
            dt1.setProperties(1,0,"NDI_Signal", _cvm.getLineDrawColor());
            graph.add(dt1);
        }
        else if(graphtitle.equals("OBV[Midpoint]")){
            graph = new OBVMidpointGraph(_cvm,_cdm);
            graph.setProperties(2,showGraphName,ChartUtil.OBV_MIDPOINT);
            //2012. 8. 8  선굵기 설정의 각 이름 다름 : I84
            dt = new LineDraw(_cvm,_cdm);
            dt.setShowZeroValue(false);
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
//            dt.setLineT(2);
//            dt.setLog(_cvm.isLog);
            graph.add(dt);
        }
        else if(graphtitle.equals("OBV Oscillator")){
            graph = new OBVOscillatorGraph(_cvm,_cdm);
            graph.setProperties(2,showGraphName,ChartUtil.OBV_OSC);
            //2012. 8. 8  선굵기 설정의 각 이름 다름 : I84
            dt = new LineDraw(_cvm,_cdm);
            dt.setShowZeroValue(false);
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
//            dt.setLineT(2);
//            dt.setLog(_cvm.isLog);
            graph.add(dt);
        }
        else if(graphtitle.equals("OBV with Average Volume")){
            graph = new OBVwithAverageVolumeGraph(_cvm,_cdm);
            graph.setProperties(2,showGraphName,ChartUtil.OBV_WITH_AVERAGE_VOLUME);
            //2012. 8. 8  선굵기 설정의 각 이름 다름 : I84
            dt = new LineDraw(_cvm,_cdm);
            dt.setShowZeroValue(false);
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
//            dt.setLineT(2);
//            dt.setLog(_cvm.isLog);
            graph.add(dt);
        }
        else if(graphtitle.equals("PVI")){
            graph = new PVIGraph(_cvm,_cdm);
            graph.setProperties(2,showGraphName,ChartUtil.PVI);
            //2012. 8. 8  선굵기 설정의 각 이름 다름 : I84
            dt = new LineDraw(_cvm,_cdm);
            dt.setShowZeroValue(false);
            dt.setProperties(1,0,"PVI", _cvm.getLineDrawColor());
//            dt.setLineT(2);
//            dt.setLog(_cvm.isLog);
            graph.add(dt);
            DrawTool dt1 = new LineDraw(_cvm,_cdm);
            dt1.setShowZeroValue(true);
            dt1.setProperties(1,0,"PVI_Signal", _cvm.getLineDrawColor());
            graph.add(dt1);
        }
        else if(graphtitle.equals("Qstic")){
            graph = new QsticGraph(_cvm,_cdm);
            graph.setProperties(2,showGraphName,ChartUtil.QSTIC);
            //2012. 8. 8  선굵기 설정의 각 이름 다름 : I84
            dt = new LineDraw(_cvm,_cdm);
            dt.setShowZeroValue(false);
            dt.setProperties(1,0,"Qstic", _cvm.getLineDrawColor());
//            dt.setLineT(2);
//            dt.setLog(_cvm.isLog);
            graph.add(dt);
            DrawTool dt1 = new LineDraw(_cvm,_cdm);
            dt1.setShowZeroValue(true);
            dt1.setProperties(1,0,"Qstic_Signal", _cvm.getLineDrawColor());
            graph.add(dt1);
        }
        else if(graphtitle.equals("TRIN[Inverted]")){
            graph = new TRINInvertedGraph(_cvm,_cdm);
            graph.setProperties(2,showGraphName,ChartUtil.TRIN_INVERTED);
            //2012. 8. 8  선굵기 설정의 각 이름 다름 : I84
            dt = new LineDraw(_cvm,_cdm);
            dt.setShowZeroValue(true);
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
//            dt.setLineT(2);
//            dt.setLog(_cvm.isLog);
            graph.add(dt);
        }
        else if(graphtitle.equals("VA Oscillator")){
            graph = new VAOscillatorGraph(_cvm,_cdm);
            graph.setProperties(0,showGraphName,ChartUtil.VA_OSC);
            dt = new LineDraw(_cvm,_cdm);
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
            graph.add(dt);
            //2012. 8. 8 NVI Signal 색굵기 없음 해결 : I68
            DrawTool dt1 = new LineDraw(_cvm,_cdm);
            dt1.setProperties(1,0,"VA Signal", _cvm.getLineDrawColor());
            graph.add(dt1);
            //2017.08.14 by pjm 지표 수정 >>
        }
        else if(graphtitle.equals("Volume & Price Accumulator")){
            graph = new VolumenPriceAccumulatorGraph(_cvm,_cdm);
            graph.setProperties(2,showGraphName,ChartUtil.VMP_ACC);
            //2012. 8. 8  선굵기 설정의 각 이름 다름 : I84
            dt = new LineDraw(_cvm,_cdm);
            dt.setShowZeroValue(false);
            dt.setProperties(1,0,graphtitle, _cvm.getLineDrawColor());
//            dt.setLineT(2);
//            dt.setLog(_cvm.isLog);
            graph.add(dt);
        }
        else if(graphtitle.equals("외국인/기관/개인 추세")){
            graph = new MarketGraph(cvm, cdm);
            graph.setProperties(0, showGraphName, ChartUtil.PIVOT);

            String[] lineNames = {"외국인","기관","개인"};
            int nColorIndex[] = {5,4,6};
            String dataName = "";
            for(int i=0; i<lineNames.length; i++){
                if(lineNames[i].equals("외국인")) {
                    dataName = "외국인/기관/개인 추세"; //"외국인/기관/개인 추세"
                } else {
                    dataName = lineNames[i] + " 순매수";
                }
                String strTitle = dataName;
                String strViewTitle = lineNames[i];

                dt = new LineDraw(_cvm,_cdm);
                dt.setShowZeroValue(true);
                dt.setBMarketData(true);
                dt.setProperties(1,0,strTitle, _cvm.getLineDrawColor());
                dt.setTitle(strTitle);
                dt.setViewTitle(strViewTitle);
//                dt.setLog(_cvm.isLog);
                dt.setInvertScale(_cvm.isInverse);	//2017.07.12 by LYH >> 거꾸로차트 기능 추가.
                graph.add(dt);
            }
        }
        else
        {
            for (int i = 0; i < ChartUtil.strMarkets.length; i++) {
                if (graphtitle.equals(ChartUtil.strMarkets[i])) {
                    graph = new MarketGraph(cvm, cdm);
                    graph.setProperties(0, showGraphName, ChartUtil.PIVOT);

                    //2016. 08. 10 by hyh - 기타지표 설정 >>
                    if (graphtitle.startsWith("기관외인")) {
                        String strTitle;

                        strTitle = graphtitle + "_기관";
                        dt = new LineDraw(cvm, cdm);
                        dt.setShowZeroValue(true);
                        dt.setProperties(1, 0, strTitle, _cvm.getLineDrawColor());
                        dt.setTitle(strTitle);
                        graph.add(dt);

                        strTitle = graphtitle + "_외국인";
                        dt = new LineDraw(cvm, cdm);
                        dt.setShowZeroValue(true);
                        dt.setProperties(1, 0, strTitle, _cvm.getLineDrawColor());
                        dt.setTitle(strTitle);
                        graph.add(dt);
                    } else if (!graphtitle.contains("누적순매수") && (graphtitle.contains("순매수") || graphtitle.contains("신용융자 수량") || graphtitle.contains("일별 공매도")
                            || graphtitle.contains("체결수량"))) {
                        dt = new BarDraw(cvm, cdm);
                        dt.setBMarketData(true);
                        if(graphtitle.startsWith("기관"))
                            dt.setProperties(2, 2, graphtitle, _cvm.getLineDrawColor());
                        else if(graphtitle.startsWith("개인"))
                            dt.setProperties(2, 2, graphtitle, _cvm.getLineDrawColor());
                        else
                            dt.setProperties(2, 2, graphtitle, _cvm.getLineDrawColor());
                        dt.setTitle(graphtitle);
                        graph.add(dt);
                    } else {
                        dt = new LineDraw(cvm, cdm);
                        dt.setShowZeroValue(false);
                        dt.setBMarketData(true);
                        dt.setProperties(1, 0, graphtitle, _cvm.getLineDrawColor());

                        dt.setTitle(graphtitle);
                        graph.add(dt);
                    }
                    //2016. 08. 10 by hyh - 기타지표 설정 <<
                }
            }
        }

        if(graph != null && graphtitle.endsWith("강세약세"))
        {
            graph.m_nStrategyType = 1;
        }

        return graph;
    }
    //2015. 1. 13 - by lyk 동일지표 이름 처리 (name 중에 COMUtil.JIPYO_ADD_REMARK가 있으면 showGraphName 사용) end

    //2013. 2. 8 체크안된 상세설정 오픈 : I114
    public void add(String graphtitle)
    {
        AbstractGraph graph = createGraph(graphtitle, _cvm, _cdm, STAND_BLOCK);
        if(graph == null)	return;

        //  	addGraph(graph);
        add(graph);

        //2016.11.04 by LHY << 지표설정시 기본값으로 저장 처리
        if(COMUtil.isInsertJipyo && COMUtil.indicatorPref != null) {
            String graphValue = COMUtil.indicatorPref.getString(graphtitle, "");
            if (graphValue != null && graphValue != "") {
                String[] strValues = graphValue.split("=");
                if (strValues.length > 0) {
                    int[] graphValues = new int[strValues.length];
                    for (int m = 0; m < strValues.length; m++) {
                        graphValues[m] = Integer.parseInt(strValues[m]);
                    }
                    graph.changeControlValue(graphValues);
                }
            }
        }//2016.11.04 by LHY << 지표설정시 기본값으로 저장 처리 end
    }

    public void init(){
    }
    public void change(){
    }
    //===============================
    //추가될 그래프의 타이틀
    //추가될 그래프가 이전 그래프와 스케일을 같이 쓸것인지.... 
    //type --> 0: 한 블럭에 하나의 그래프(일반적인 그래프)
    //         2: 기존의 블럭에 더하여 그리는 그래프((예)추세그래프,세로스케일을 기존의 블럭에 있는 그래프와 join)
    //         3: 전체블럭을 차지하여 그리는 그래프((예)삼선전환도,P&F)
    //         4: 대기매물
    //추가될 그래프에서 사용하는 데이터 종류
    //라인의 굵기
    //라인색상 인덱스
    //===============================
    public void add_userGraph_NoTitle(String title, int graphtype,String[] datakind,int lineT,int lineCIndex, boolean bUseJipyoSign){
        PriceGraph graph = new PriceGraph(_cvm,_cdm);
        graph.setProperties(graphtype,title,ChartUtil.PLINE);
        graph.setDatakind(datakind);
        graph.m_bUseJipyoSign = bUseJipyoSign;
        if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
            _cvm.setViewNum(100);
        else
            _cvm.setViewNum(40);
        DrawTool dt = new LineDraw(_cvm,_cdm);
        //0:그래프 타입 1:그래프 스타일 2: 타이틀 3 색
        dt.setProperties(1,0,title, CoSys.CHART_COLORS[lineCIndex]);
        //0데이터를 보여줄 것인지 설정
        dt.setShowZeroValue(true);  //2016.12.01 by LYH >> 스케일 없슴 옵션 추가(테마분석 차트)
        dt.setLineT(lineT);
        graph.add(dt);
        graph.setParent(this);
        add(graph);
    }

    public void add_userGraph_jisu(String title, int graphtype,String[] datakind,int lineT,int lineCIndex){
//        if(title.length()>10)
//            title = title.substring(0,10);
        AbstractGraph graph = new PriceGraph(_cvm,_cdm);
        graph.setProperties(graphtype,title,ChartUtil.PLINE);
        graph.setDatakind(datakind);
        if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
            _cvm.setViewNum(100);
        else
            _cvm.setViewNum(40);
        DrawTool dt = new LineDraw(_cvm,_cdm);
        //0:그래프 타입 1:그래프 스타일 2: 타이틀 3 색
        dt.setProperties(1,0,title, CoSys.CHART_COLORS[lineCIndex]);
        //0데이터를 보여줄 것인지 설정
        //dt.setShowZeroValue(false);
        dt.setShowZeroValue(false);
        dt.setLineT(lineT);
        graph.add(dt);
        graph.setParent(this);
        add(graph);
    }

    public void add_userGraph(String title, int graphtype,String[] datakind,int lineT,int lineCIndex){
//        if(title.length()>10)
//            title = title.substring(0,10);
        AbstractGraph graph = new PriceGraph(_cvm,_cdm);
        graph.setProperties(graphtype,title,ChartUtil.PLINE);
        graph.setDatakind(datakind);
        if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
            _cvm.setViewNum(100);
        else
            _cvm.setViewNum(40);
        DrawTool dt = new LineDraw(_cvm,_cdm);
        //0:그래프 타입 1:그래프 스타일 2: 타이틀 3 색
        dt.setProperties(1,0,title, CoSys.CHART_COLORS[lineCIndex]);
        //0데이터를 보여줄 것인지 설정
        //dt.setShowZeroValue(false);
        dt.setShowZeroValue(true);
        dt.setLineT(lineT);
        graph.add(dt);
        graph.setParent(this);
        add(graph);
    }

    public void add_userGraphBar(String title, int graphtype,String[] datakind,int lineCIndex){
//        AbstractGraph graph = new VolumnBarGraph(_cvm,_cdm);
//        graph.setProperties(graphtype,title,ChartUtil.VOLUME);
//        graph.setDatakind(datakind);
//        DrawTool dt = new BarDraw(_cvm,_cdm);
//        //0:그래프 타입 1:그래프 스타일 2: 타이틀 3 색
//        dt.setProperties(2,0,title, CoSys.CHART_COLORS[lineCIndex]);
//        graph.add(dt);
//        graph.setParent(this);
//        add(graph);
//        
        AbstractGraph graph = new PriceGraph(_cvm,_cdm);
        //2012. 8. 8  변수설정 삭제 : I78
        graph.setProperties(graphtype,title,ChartUtil.PLINE);
        graph.setDatakind(datakind);
        DrawTool dt = new BarDraw(_cvm,_cdm);
        //2012. 8. 8  선굵기색상  : I78
        dt.setProperties(2,0,title, CoSys.CHART_COLORS[lineCIndex]);
        graph.add(dt);
        graph.setParent(this);
        add(graph);
    }

    //2013.07.22 by LYH >> 투자자 차트 바 타입 추가.
    public void add_userGraphBar_Osc(String title, int graphtype,String[] datakind,int lineT ,int lineCIndex){
        AbstractGraph graph = new PriceGraph(_cvm,_cdm);
        graph.setProperties(graphtype,title,ChartUtil.PLINE);
        graph.setDatakind(datakind);

        DrawTool dt = new BarDraw(_cvm,_cdm);
        dt.setProperties(2,2,title, CoSys.CHART_COLORS[lineCIndex]);
        dt.setDownColor(CoSys.CHART_COLORS[6]);
        graph.add(dt);
        //0:그래프 타입 1:그래프 스타일 2: 타이틀 3 색
//        dt.setProperties(2,2,title, CoSys.CHART_COLORS[4]);
        //graph.add(dt);
        //graph.setParent(this);
        add(graph);
    }
    //2013.07.22 by LYH <<

    //2015.01.08 by LYH >> 3일차트 추가
    public void add_userGraphBar_Bar(String title, int graphtype,String[] datakind,int lineT ,int lineCIndex){
        AbstractGraph graph = new PriceGraph(_cvm,_cdm);
        graph.setProperties(graphtype,title,ChartUtil.PLINE);
        graph.setDatakind(datakind);

        DrawTool dt = new BarDraw(_cvm,_cdm);
        if(_cdm.m_bRealUpdate)
        {
            dt.setProperties(2,0,title, CoSys.STANDARD_VOL);
        }
        else
        {
            dt.setProperties(2,0,title, CoSys.CHART_COLORS[lineCIndex]);
        }

        dt.setDownColor(CoSys.CHART_COLORS[6]);
        graph.add(dt);
        //0:그래프 타입 1:그래프 스타일 2: 타이틀 3 색
//        dt.setProperties(2,2,title, CoSys.CHART_COLORS[4]);
        //graph.add(dt);
        //graph.setParent(this);
        add(graph);
    }
    //2015.01.08 by LYH << 3일차트 추가
    //2020.04.20 line+roundedBar 차트 추가 - hjw >>
    public void add_userGraphRoundedBar(String title, int graphtype,String[] datakind,int lineCIndex){

        AbstractGraph graph = new PriceGraph(_cvm,_cdm);
        graph.setProperties(graphtype,title,ChartUtil.PLINE);
        graph.setDatakind(datakind);
        DrawTool dt = new BarDraw(_cvm,_cdm);
        dt.setProperties(2,ChartViewModel.CHART_LINE_ROUNDED_BAR,title, CoSys.CHART_COLORS[lineCIndex]);
        graph.add(dt);
        graph.setParent(this);
        add(graph);
    }
    //2020.04.20 line+roundedBar 차트 추가 - hjw <<
    //2020.04.20 roundedBar3 차트 추가 - hjw >>
    public void add_userGraphRoundedBar2(String title, int graphtype,String[] datakind,int lineCIndex){

        AbstractGraph graph = new PriceGraph(_cvm,_cdm);
        graph.setProperties(graphtype,title,ChartUtil.PLINE);
        graph.setDatakind(datakind);
        DrawTool dt = new BarDraw(_cvm,_cdm);
        dt.setProperties(2,1,title, CoSys.CHART_COLORS[lineCIndex]);
        graph.add(dt);
        graph.setParent(this);
        add(graph);
    }
    //===============================
    // 로그 설정
    //===============================
    public void setLog(boolean b){
        for(int i=0; i<scale.length; i++)
            scale[i].setLog(b);
    }
    //===============================
    // 해당 title을 가진 그래프 삭제
    //===============================
    public void removeGraph(String str){
        AbstractGraph graph;
        for(int i=0;i<scalegroups.size();i++){
            VertScaleGroup vsg = (VertScaleGroup)scalegroups.elementAt(i);
            vsg.remove(str);

            //2019. 01. 12 by hyh - 블록병합 처리 >>
            if (vsg.graphs.size() == 0) {
                scalegroups.remove(i);
            }
            //2019. 01. 12 by hyh - 블록병합 처리 <<
        }

        //2019. 04. 08 by hyh - FX마진 데이터 조회 및 실시간 처리 >>
        if (str.equals("매수") && scalegroups.size() > 1 && _cvm.nFxMarginType == -1) {
            for (int nIndex = 0; nIndex < scalegroups.size(); nIndex++) {
                VertScaleGroup vsg = scalegroups.get(nIndex);
                boolean bIsContainJipyo = false;

                for (int nIndexForGraph = 0; nIndexForGraph < vsg.graphs.size(); nIndexForGraph++) {
                    AbstractGraph ag = vsg.graphs.get(nIndexForGraph);

                    if (ag.equals(str)) {
                        bIsContainJipyo = true;
                        break;
                    }
                }

                if (bIsContainJipyo) {
                    scalegroups.remove(nIndex);
                }
            }
        }
        //2019. 04. 08 by hyh - FX마진 데이터 조회 및 실시간 처리 <<

        //2012.08.29 by LYH >> ELW 기초자산 추가.
        if(str.equals("BaseMarket") && scalegroups.size()>1)
        {
            scalegroups.remove(1);
        }
        //2012.08.29 by LYH <<
        for(int i=0; i<graphs.size(); i++){
            graph = (AbstractGraph)graphs.elementAt(i);
            if(graph.getGraphTitle().equals(str)){
                graph.destroy();
                graphs.removeElementAt(i);
                return;
            }
        }
    }
    
    public void setVisibleUserGraph(String str, String sVisible){
        boolean visible = true;
        if(sVisible.equals("1"))
            visible = true;
        else
            visible = false;

        AbstractGraph graph;
        for(int i=0; i<graphs.size(); i++){
            graph = (AbstractGraph)graphs.elementAt(i);
            if(graph.getGraphTitle().equals(str)){
                graph.setLineVisible(visible, 0);
            }
        }
    }
    
    //===============================
    // title의 이름을 가진 그래프에서 dtTitle의 이름을 가진 드로우툴을 삭제
    //=============================== 
    public void removeDrawTool(String title, String dtTitle){
        AbstractGraph graph;
        for(int i=0; i<graphs.size(); i++){
            graph = (AbstractGraph)graphs.elementAt(i);
            if(graph.getGraphTitle().equals(title)){
                graph.removeDrawTool(dtTitle);
                return;
            }
        }
    }
    //=============================
    // 그래프 추가한다. 그래프의 속성에 따라 별도의 VertScaleGroup을 생성하기도 하고, 
    // 기존의 스케일그룹에 add하기도 한다
    //=============================
    public void add(AbstractGraph g){
        if(graphs==null){
            graphs = new Vector<AbstractGraph>();
            scalegroups = new Vector<VertScaleGroup>();
            VertScaleGroup vsg = new VertScaleGroup();
            vsg.add(g);
            scalegroups.addElement(vsg);

        }else{
            //2012.08.29 by LYH >> ELW 기초자산 추가.
            if(g.getGraphTitle().equals("BaseMarket"))
            {
                VertScaleGroup vsg = new VertScaleGroup();
                vsg.add(g);
                scalegroups.addElement(vsg);
            }
            //2012.08.29 by LYH <<
//            else if(g.getGraphType()!=2){//독립된 스케일그룹을 사용하는 경우(추세가 아닌경우)
            else if(g.getGraphType()!=2 && g.getGraphType()!=4){  //2017.05.11 by LYH >> 전략(신호, 강약) 추가
                VertScaleGroup vsg = new VertScaleGroup();
                vsg.add(g);
                scalegroups.addElement(vsg);
            }else{
                VertScaleGroup vsg=(VertScaleGroup)scalegroups.elementAt(0);
                vsg.add(g);
            }
        }
        graphs.addElement(g);
    }
    public Vector<AbstractGraph> getGraphs(){
        return graphs;
    }

    //=============================
    // 블럭 속성 설정 
    //=============================
    public void setBlockType(int type){
        BLOCK_TYPE = type;
    }
    public int getBlockType(){
        return BLOCK_TYPE;
    }
    public boolean isBasicBlock(){
        if(BLOCK_TYPE==1)return true;
        else return false;
    }
    //=============================
    // 뷰에 insert된 순서 인덱스
    //=============================
    public void setIndex(int index){
        this.index = index;
    }
    public int getIndex(){
        return this.index;
    }
    //=============================
    // 그래프 데이터 계산
    //=============================
    public void makeGraphData(){
        AbstractGraph ag;
        for(int i=0;i<graphs.size();i++){
            ag = (AbstractGraph)graphs.elementAt(i);
            ag.formulated=false;
            ag.FormulateData();
        }
    }
    public void makeGraphDataReal(){
        AbstractGraph ag;
        if(graphs == null)
            return;
        for(int i=0;i<graphs.size();i++){
            ag = (AbstractGraph)graphs.elementAt(i);
            if(!ag.graphTitle.equals("렌코"))
                ag.reFormulateData();
        }
    }
    //=============================
    // 블럭 위치
    //=============================
    public void setBounds(float left, float top, float right, float bottom,boolean b){

        //안드로이드에선 Rect(left, top, right, bottom);
        bounds = new RectF(left,top,right,bottom);
//        System.out.println("Block.setBounds:"+bounds);
        if(b){
            H_UPBOUNDS=(int)COMUtil.getPixel(26);
            H_DNBOUNDS=(int)COMUtil.getPixel(12);
        }

        setDnBounds((int)COMUtil.getPixel(10),bottom-H_DNBOUNDS,right-(int)COMUtil.getPixel(10),H_DNBOUNDS*2);
        setBounds(left,top,right,bottom);

        //블럭이동버튼 터치 영역 처리
        //2016.09.08 by LYH >> 오른쪽 여백 설정 기능 >>
        //setUpBounds(this.getGraphBounds().right,top+(int)COMUtil.getPixel(1),H_UPBOUNDS+(int)COMUtil.getPixel(1),H_UPBOUNDS+(int)COMUtil.getPixel(1));
        setUpBounds(this.getGraphBounds().right+_cvm.PADDING_RIGHT+W_YSCALE-(int)COMUtil.getPixel(14),top+(int)COMUtil.getPixel(1),H_UPBOUNDS+(int)COMUtil.getPixel(1),H_UPBOUNDS+(int)COMUtil.getPixel(1));
        //2016.09.08 by LYH >> 오른쪽 여백 설정 기능 <<
    }
    //=============================
    // 블럭에 삽입된 각 그래프의 타이틀 바운드 reset
    //=============================
    public void resetTitleBounds(){
        if(_cvm.bIsInnerText || _cvm.bIsMiniBongChart)   //2017.07.10 by pjm 거래원 Bar 차트 안그려지는 오류 처리.
            return;

        int title_len=0;
        AbstractGraph g;

        //2015. 7. 10 차트 상단 지표명 2줄이상 개행시키기>>
        int nTitleYOffset = 0;
        int nPreMargineT = margineT;
        //margineT = (int)COMUtil.getPixel(25);
        margineT = m_nOrgMargineT;  //2017.06.21 by LYH >> 타이틀 3줄까지 타이틀 영역 밑으로 그래프 영역 잡도록 수정
        _cvm.setBlockMarginT(margineT);
        //graph_bounds.top = getBounds().top + margineT;
        //2015. 7. 10 차트 상단 지표명 2줄이상 개행시키기<<

        float nAddTitleHeight = 0;    //2017.06.21 by LYH >> MAC와 다른 지표 타이틀 겹치는 현상 수정
        int nLeftTitleMargin = LEFT_MARGIN_TITLE;
        for(int i=0;i<graphs.size();i++){
            g = (AbstractGraph)graphs.elementAt(i);
            if(!_cvm.isMovingAverageLine) {
                if(g.getClass().equals(PriceAverageGraph.class))
                    continue;;
            }
            g.blockType = this.getBlockType();
//            if(g.getName().equals("일본식봉") || g.getName().equals("거래량"))
//                nLeftTitleMargin = 0;
            //2015. 7. 10 차트 상단 지표명 2줄이상 개행시키기>>
//            g.setGraphTitleBounds(getX()+_cvm.Margin_L+(int)COMUtil.getPixel(5)+title_len,getY());
//      	title_len+=g.getGraphTitleBounds().right;

            //두줄일때는 첫번째 줄로 setGraphTitleBounds 를 수행
            nAddTitleHeight = g.setGraphTitleBounds(getX()+_cvm.Margin_L+nLeftTitleMargin+title_len,getY()+nTitleYOffset);  //2017.06.21 by LYH >> MAC와 다른 지표 타이틀 겹치는 현상 수정

            int nGraphTitleLen = g.getDrawToolsTitleLength();
//          	if( 	isBasicBlock() &&
//          			((getX()+_cvm.Margin_L+(int)COMUtil.getPixel(5)+title_len+nGraphTitleLen) > (getBounds().right-_cvm.Margin_R-m_nPaddingRight-getBounds().left)) )
            if( 	isBasicBlock() &&
                    ((getX()+_cvm.Margin_L+nLeftTitleMargin+title_len+nGraphTitleLen) > graph_bounds.right) )
            {
                //그다음에 개행된 x좌표 시작위치
                title_len = 0;
                //2017.06.21 by LYH >> MAC와 다른 지표 타이틀 겹치는 현상 수정
                if(nAddTitleHeight>0)
                    nTitleYOffset += nAddTitleHeight;
                else
                    //2017.06.21 by LYH >> MAC와 다른 지표 타이틀 겹치는 현상 수정 end
                    nTitleYOffset += (int)COMUtil.getPixel_H(15);
                //margineT += (int)COMUtil.getPixel(15);
                //_cvm.setBlockMarginT(margineT);
                //graph_bounds.top = getBounds().top + margineT;
                //2017.06.21 by LYH >> 타이틀 3줄까지 타이틀 영역 밑으로 그래프 영역 잡도록 수정
                if(nTitleYOffset<=(int)COMUtil.getPixel_H(30)) {
                    //2017.06.21 by LYH >> MAC와 다른 지표 타이틀 겹치는 현상 수정
                    if(nAddTitleHeight>0) {
                        margineT += nAddTitleHeight;
                    } else {
                        //2017.06.21 by LYH >> MAC와 다른 지표 타이틀 겹치는 현상 수정 end
                        margineT += (int) COMUtil.getPixel_H(15);
                    }

                    //2021.10.21 by JHY - 타이틀 4줄이상시 y축 더 하단 >>
                    if((int) COMUtil.getPixel_H(30) >= nTitleYOffset && nTitleYOffset <= (int) COMUtil.getPixel_H(40)){
                        margineT += 15;
                    }
                    //2021.10.21 by JHY - 타이틀 4줄이상시 y축 더 하단 <<
                }
                graph_bounds.top = getBounds().top + margineT;
                //2017.06.21 by LYH >> 타이틀 3줄까지 타이틀 영역 밑으로 그래프 영역 잡도록 수정 end
            }

            if(!g.isNewLineNextStep())
            {
                title_len+=g.getGraphTitleBounds().right;
            }
            //2015. 7. 10 차트 상단 지표명 2줄이상 개행시키기<<

            //2017.07.24 by LYH 오버레이 지표명 개행시 그래프 영역 이동한 만큼 y축 영역 동일하게 처리 >>
            if(isBasicBlock() && ( stateSize == true || margineT != m_nOrgMargineT || margineT != nPreMargineT))
            {
                graph_bounds.top = getBounds().top + margineT;
                if(scale!=null && ypos == 1){
                    margineR+=W_YSCALE;
                    for(int j=0;j<scale.length;j++){
                        scale[j].setBounds(
                                getBounds().right-margineR,
                                getBounds().top+margineT,
                                W_YSCALE+getBounds().right-margineR,
                                getBounds().bottom-margineB
                        );
                    }
                }
                margineR-=W_YSCALE;
            }
            //2017.07.24 by LYH 오버레이 지표명 개행시 그래프 영역 이동한 만큼 y축 영역 동일하게 처리 end <<
        }
//        //2015. 7. 10 차트 상단 지표명 2줄이상 개행시키기>>
//        if(scale!=null){
//            for(int j=0;j<scale.length;j++){
//                scale[j].setBounds(
//                        getBounds().right-_cvm.Margin_R,
//                        getBounds().top+margineT,
//                        W_YSCALE+getBounds().right-_cvm.Margin_R,
//                        getBounds().bottom-margineB
//                );
//                scale[j].setOuterBounds(graph_bounds.left,graph_bounds.top,graph_bounds.right, graph_bounds.bottom);
//                scale[j].setOrg_bounds(bounds);
//                scale[j].setMargine(_cvm.Margin_L, _cvm.Margin_R, margineT, _cvm.Margin_B);
//            }
//        }
//        //2015. 7. 10 차트 상단 지표명 2줄이상 개행시키기<<
    }
    //=============================
    // 그래프의 setBounds, y스케일 setBounds
    //=============================
    public void setBounds(float sx, float sy, float width, float height){
        bounds = new RectF(sx,sy,width,height);
        int title_len=0;
        //2015. 7. 10 차트 상단 지표명 2줄이상 개행시키기>>
        int nTitleYOffset = 0;
//        margineT = (int)COMUtil.getPixel(25);
        //2015. 7. 10 차트 상단 지표명 2줄이상 개행시키기<<
        AbstractGraph g;
        if(graphs == null) return;
        for(int i=0;i<graphs.size();i++){
            g = (AbstractGraph)graphs.elementAt(i);
            switch(this.ypos){
                case 0://왼쪽
                    W_YSCALE = (int)COMUtil.getPixel(52);
                    //2020.04.20 roundedBar3 차트 추가 - hjw >>
                    if(_cvm.m_nChartType != 0)
                    {
                        if(_cvm.m_nChartType == ChartViewModel.CHART_THREE_ROUNDED_BAR) {
                            W_YSCALE = (int) COMUtil.getPixel(44);
                        }
                    }
                    //2020.04.20 roundedBar3 차트 추가 - hjw <<
                    margineR = (int)COMUtil.getPixel(0);
                    margineL+=W_YSCALE;
                    graph_bounds = new RectF(
                            getBounds().left+margineL+(int)COMUtil.getPixel(1),
                            getBounds().top+margineT,
                            //getBounds().right-margineR-margineL-3,
                            getBounds().right-margineR-(int)COMUtil.getPixel(3),
                            getBounds().bottom-margineB
                    );
                    _cvm.setMarginL(margineL);
                    _cvm.setMarginR(margineR);
                    _cvm.setBlockMarginB(margineB);
                    _cvm.setBlockMarginT(margineT);
                    ////
                    g.setBounds(graph_bounds.left,graph_bounds.top,graph_bounds.right,graph_bounds.bottom);
                    g.setGraphTitleBounds(getX()+margineL+LEFT_MARGIN_TITLE+title_len,getY());
                    title_len+=g.getGraphTitleBounds().right;
                    out_graph_bounds = new RectF(
                            getBounds().left+margineL,
                            getBounds().top,
                            getBounds().right-margineR-(int)COMUtil.getPixel(1),
                            getBounds().bottom
                    );
                    ////            
                    if(scale!=null){
                        for(int j=0;j<scale.length;j++){
                            scale[j].setBounds(
                                    getBounds().left+margineL,
                                    getBounds().top+margineT,
                                    W_YSCALE,
                                    getBounds().bottom-margineB
                            );
                            scale[j].setOuterBounds(graph_bounds.left,graph_bounds.top,graph_bounds.right, graph_bounds.bottom);
                        }
                    }
                    margineL-=W_YSCALE;

                    break;
                case 1://오른쪽
                    //투자자 매매동향

                    if(_cvm.getAssetType() >0) {
                        W_YSCALE = 0;

                        if(_cvm.getAssetType() != ChartViewModel.ASSET_LINE_FILL)
                        {
                            margineR = 0;
                            margineT = (int) COMUtil.getPixel(25);
                        }
                    }
                    else if(COMUtil.isTrendType) {
                        W_YSCALE = (int)COMUtil.getPixel(30);
                    } else if(_cvm.bIsLineChart || _cvm.bIsLineFillChart) {
                        W_YSCALE = 0;
                        margineR = 0;
                        if(_cvm.bIsNewsChart)
                        {
                            margineT = (int)COMUtil.getPixel(10);
                            W_YSCALE = (int)COMUtil.getPixel(50);
                            setHideChangeBlockButton(true); //블럭이동버튼 숨김.
                        }
//                        else if(_cvm.bIsLineFillChart)
//                            margineT = (int)COMUtil.getPixel(10);
//                        else
//                            margineT = 0;
                        setHideViewNumTextView(true);	//2014. 3. 20 Y축 봉갯수 누르면 봉입력창 뜨고 봉갯수변경
                    } else {
                        if(width>0)
                        {
                            if(getBounds().width()<=COMUtil.getPixel(170) || _cvm.bIsLine2Chart || _cvm.bIsMiniBongChart || (!COMUtil.isyScaleShow() && _cvm.chartType != COMUtil.COMPARE_CHART && !_cvm.m_bCurrentChart))
//                            if(getBounds().width()<=COMUtil.getPixel(170) || _cvm.bIsMiniBongChart || (!COMUtil.isyScaleShow() && _cvm.chartType != COMUtil.COMPARE_CHART && !_cvm.m_bCurrentChart))
                            {
                                W_YSCALE = 0;
                                margineR = 0;

                                setHideChangeBlockButton(true); //블럭이동버튼 숨김.
                                setHideViewNumTextView(true);	//2014. 3. 20 Y축 봉갯수 누르면 봉입력창 뜨고 봉갯수변경
                            }
                            else {
                                if(_cvm.m_bCurrentChart)
                                {
                                    //2014. 1. 7 yscale 크기 변경>>
                                    //W_YSCALE = (int)COMUtil.getPixel(54);
                                    W_YSCALE = (int)COMUtil.getPixel(55);
                                    //2014. 1. 7 yscale 크기 변경<<
                                    margineR = (int)COMUtil.getPixel(10);
                                }
                                else if(_cvm.bIsLineFillChart)
                                {
                                    W_YSCALE = (int)COMUtil.getPixel(43);
                                    margineR = (int)COMUtil.getPixel(5);
                                }
                                else
                                {
                                    //2013.09.23 by LYH >> Y스케일 여백 줄임.
//		                    		W_YSCALE = (int)COMUtil.getPixel(50);
//		                    		margineR = (int)COMUtil.getPixel(10);
                                    //2014. 1. 7 yscale 크기 변경>>
//		                    		W_YSCALE = (int)COMUtil.getPixel(52);
                                    W_YSCALE = (int)COMUtil.getPixel(55); //2021.06.21 by lyk - kakaopay - YSCALE 넓이
                                    //2014. 1. 7 yscale 크기 변경<<
                                    margineR = (int)COMUtil.getPixel(4); //2021.06.21 by lyk - kakaopay - 블럭 우측 마진
//                                    if(!_cvm.bIsTodayLineChart)
////                                        margineT = (int)COMUtil.getPixel(40);
//                                        margineT = (int)COMUtil.getPixel(30);
//                                    else
                                        margineT = (int)COMUtil.getPixel(25);
                                    //2013.09.23 by LYH <<
                                }

                                if(_cvm.isStandGraph()) {
                                    setHideChangeBlockButton(true); //블럭이동버튼 숨김.
                                    setHideViewNumTextView(true);	//2014. 3. 20 Y축 봉갯수 누르면 봉입력창 뜨고 봉갯수변경
                                } else {
                                    if(		parentView.rotate_blocks != null &&
                                            parentView.rotate_blocks.size() > 0 &&
                                            parentView.rotate_blocks.contains(this))
                                    {
                                        //현재 블록이 회전블록 (rotate_block)에 속해있는 블록이라면, 블록이동버튼을 숨겨준다
                                        setHideChangeBlockButton(true);
                                        setHideViewNumTextView(true);	//2014. 3. 20 Y축 봉갯수 누르면 봉입력창 뜨고 봉갯수변경
                                    }
                                    else
                                    {
                                        setHideChangeBlockButton(false); //블럭이동버튼 보임.
                                        setHideViewNumTextView(false);	//2014. 3. 20 Y축 봉갯수 누르면 봉입력창 뜨고 봉갯수변경
                                    }
                                }
                            }
                        }
                    }

                    margineR+=W_YSCALE;
                    _cvm.setMarginL(margineL);
                    _cvm.setMarginR(margineR);
                    _cvm.setBlockMarginB(margineB);
                    _cvm.setBlockMarginT(margineT);

                    graph_bounds = new RectF(
                            getBounds().left+margineL+(int)COMUtil.getPixel(2),
                            getBounds().top+margineT,
                            //getBounds().right-margineR-margineL-(int)COMUtil.getPixel(2)-getBounds().left,
                            //getBounds().right-margineR-(int)COMUtil.getPixel(2)-getBounds().left,
                            getBounds().right-margineR-m_nPaddingRight-getBounds().left,	//2013. 10. 29 1분선차트, 마운틴차트 제외한 차트 오른쪽 패딩 2->8 정도로 넓게 수정
                            getBounds().bottom-margineB
                    );

//                    org_bounds = new Rect(getBounds().left, getBounds().top, getBounds().right, getBounds().bottom);

                    out_graph_bounds = new RectF(
                            getBounds().left+margineL,
                            getBounds().top,
                            //getBounds().right-margineL-margineR,
                            //getBounds().right-(int)COMUtil.getPixel(2)-margineR,
                            getBounds().right-margineR,
                            getBounds().bottom
                    );
                    ////

                    //2015. 7. 10 차트 상단 지표명 2줄이상 개행시키기>>
                    //drawtool의 bounds 가 없을때가 있다. 이건 abstractgraph 의 setbounds 가 안되있을 경우
                    g.setBounds(graph_bounds.left,graph_bounds.top,graph_bounds.right,graph_bounds.bottom);
                    //두줄일때는 첫번째 줄로 setGraphTitleBounds 를 수행
                    g.setGraphTitleBounds(getX()+margineL+LEFT_MARGIN_TITLE+title_len,getY()+nTitleYOffset);

                    int nGraphTitleLen = g.getDrawToolsTitleLength();
                    float nX = (getX()+margineL+LEFT_MARGIN_TITLE+title_len+nGraphTitleLen);
//                	int nYScale = (getBounds().right-margineR-m_nPaddingRight-getBounds().left);
                    float nYScale = graph_bounds.right;

                    if( isBasicBlock() && nYScale > 0 && nX > nYScale )
                    {
                        //그다음에 개행된 x좌표 시작위치
                        title_len = 0;
                        if(nGraphTitleLen < nYScale) {
                            nTitleYOffset += (int) COMUtil.getPixel_H(15);
                        }
                        //margineT += (int)COMUtil.getPixel(15);
                        //_cvm.setBlockMarginT(margineT);
                        //graph_bounds.top = getBounds().top + margineT;
                    }

                    if(g.getGraphTitleBounds()==null) return;

                    //해당 지표의 지표명을 그렸을때 오른쪽끝이 y축에 매우 인접한경우는 다음번 지표를 그릴때 강제개행을 해주어야한다.
                    //현재 AS-IS는 y축에 매우 인접한경우, 위 조건대로 y축 침범여부를 Block의 setbounds 를 따졌을 때는 margin값이 있어서 그런지 y축을 넘었다고 나온다.
                    //하지만 AbstractGraph 의 setGraphTitleBounds 에서 따지면 마지막 drawtool의 title의 오른쪽 끝 x값이 y축경계선 위치보다 적다.
                    //따라서, 이 경우에만 위에서 개행되어 초기화한 title_len의 값을 변경하지 않음으로써 개행후 맨처음 x 위치에서 시작되도록 한다.
                    if(!g.isNewLineNextStep())
                    {
                        title_len+=g.getGraphTitleBounds().right;
                    }
                    //2015. 7. 10 차트 상단 지표명 2줄이상 개행시키기<<

                    ////
                    if(scale!=null){
                        for(int j=0;j<scale.length;j++){
                            scale[j].setBounds(
                                    getBounds().right-margineR,
                                    getBounds().top+margineT,
                                    W_YSCALE+getBounds().right-margineR,
                                    getBounds().bottom-margineB
                            );
                            scale[j].setOuterBounds(graph_bounds.left,graph_bounds.top,graph_bounds.right, graph_bounds.bottom);
                            scale[j].setOrg_bounds(bounds);
                            scale[j].setMargine(margineL, margineR, margineT, margineB);
                        }
                    }
                    margineR-=W_YSCALE;
                    break;
                case 2://양쪽

                    margineL+=W_YSCALE;
                    margineR+=W_YSCALE;
                    _cvm.setMarginL(margineL);
                    _cvm.setMarginR(margineR);
                    _cvm.setBlockMarginB(margineB);
                    _cvm.setBlockMarginT(margineT);

                    graph_bounds = new RectF(
                            getBounds().left+margineL+2,
                            getBounds().top+margineT,
                            getBounds().right-margineR-3,
                            getBounds().bottom-margineB
                    );
                    out_graph_bounds = new RectF(
                            getBounds().left+margineL,
                            getBounds().top,
                            getBounds().right-margineR,
                            getBounds().bottom
                    );
                    ////
                    //2015. 7. 10 차트 상단 지표명 2줄이상 개행시키기>>
                    //drawtool의 bounds 가 없을때가 있다. 이건 abstractgraph 의 setbounds 가 안되있을 경우
                    g.setBounds(graph_bounds.left,graph_bounds.top,graph_bounds.right,graph_bounds.bottom);
                    //두줄일때는 첫번째 줄로 setGraphTitleBounds 를 수행
                    g.setGraphTitleBounds(getX()+margineL+(int)COMUtil.getPixel_W(5)+title_len,getY()+nTitleYOffset);
                    //g.setGraphTitleBounds(getX()+margineL+(int)COMUtil.getPixel(5)+title_len,getY());
                    if(g.getGraphTitleBounds()==null) return;
                    title_len+=g.getGraphTitleBounds().right;
                    ////
                    if(scale!=null){
                        for(int j=0;j<scale.length;j++){
                            if(scale[j].getPos()==0){
                                scale[j].setBounds(
                                        getBounds().left+margineL,
                                        getBounds().top+margineT,
                                        W_YSCALE,
                                        getBounds().bottom-margineB
                                );
                                scale[j].setOuterBounds(graph_bounds.left,graph_bounds.top,graph_bounds.right, graph_bounds.bottom);
                            }else{
                                scale[j].setBounds(
                                        getBounds().right-margineR,
                                        getBounds().top+margineT,
                                        W_YSCALE+getBounds().right-margineR,
                                        getBounds().bottom-margineB
                                );
                                scale[j].setOuterBounds(graph_bounds.left,graph_bounds.top,graph_bounds.right, graph_bounds.bottom);
                                scale[j].setMargine(margineL, margineR, margineT, margineB);
                            }
                        }
                    }
                    margineL-=W_YSCALE;
                    margineR-=W_YSCALE;
                    break;
                case 3://숨김
                case 4://없슴
                    //margineT-=18;
                    if(_cvm.bIsInnerTextVertical || _cvm.bIsInnerText || _cvm.bIsNoScale)
                        margineR = 0;
                    _cvm.setMarginL(margineL);
                    _cvm.setMarginR(margineR);
                    _cvm.setBlockMarginB(margineB);
                    _cvm.setBlockMarginT(margineT);
                    graph_bounds = new RectF(
                            getBounds().left+margineL,
                            getBounds().top+margineT,
                            getBounds().right-margineR-margineL,
                            getBounds().bottom-margineB
                    );
                    //2016.12.01 by LYH >> 스케일 없슴 옵션 추가(테마분석 차트)
                    if(_cvm.bIsNoScale) {
                        g.setBounds(graph_bounds.left, graph_bounds.top, graph_bounds.right, graph_bounds.bottom);
                        g.setGraphTitleBounds(getX() + margineL + LEFT_MARGIN_TITLE + title_len, getY());
                        title_len += g.getGraphTitleBounds().right;
                    }
                    //2016.12.01 by LYH >> 스케일 없슴 옵션 추가(테마분석 차트) end

                    out_graph_bounds = new RectF(
                            getBounds().left,
                            getBounds().top,
                            getBounds().right-1,
                            getBounds().bottom
                    );
                    break;
                default:
                    break;
            }
            //g.setBounds(graph_bounds.x,graph_bounds.y,graph_bounds.width,graph_bounds.height);
            if(pivotType) {
                g.setBounds(graph_bounds.left,graph_bounds.top,graph_bounds.right-graph_bounds.bottom/pivotGab,graph_bounds.bottom);
            } else {
                g.setBounds(graph_bounds.left,graph_bounds.top,graph_bounds.right,graph_bounds.bottom);
            }
        }
    }
    //피봇이 설정되면 그래프 bounds변경
    private boolean pivotType = false;
    private int pivotGab = 4;
    public void setBounds_Pivot(boolean type) {
        if(graphs==null) return;
        pivotType = type;
        AbstractGraph g;
        for(int i=0;i<graphs.size();i++){
            g = (AbstractGraph)graphs.elementAt(i);
            if(type) {
                g.setBounds(graph_bounds.left,graph_bounds.top,graph_bounds.right-graph_bounds.right/pivotGab,graph_bounds.bottom);
            } else {
                g.setBounds(graph_bounds.left,graph_bounds.top,graph_bounds.right,graph_bounds.bottom);
            }
        }
    }
    public float getPivotGab() {
        return graph_bounds.right/pivotGab;
    }

    public RectF getBounds(){
        return bounds;
    }
    public RectF getOutBounds(){
        return out_graph_bounds;
    }
    public RectF getGraphBounds(){
        return graph_bounds;
    }
    private void setUpBounds(float sx, float sy, float w, float h){
        upbounds = new RectF(sx,sy,sx+w,sy+h); //right, bottom 좌표로 바꿔줌
    }
    public RectF getUpBounds(){
        return upbounds;
    }
    private void setDnBounds(float sx, float sy, float w, float h){
        dnbounds = new RectF(sx,sy,sx+w,sy+h);
    }
    public RectF getDnBounds(){
        return dnbounds;
    }
    public void setHBounds(float sy, float h){
        RectF bounds = this.getBounds();
//        float fBottom = bounds.bottom;
        this.setBounds(bounds.left,sy,bounds.left+bounds.right,sy+h,true);

        //2021.04.28 by lyk - kakaopay - xscale이 차트 가장 하단에 붙을 경우 주석처리 >>
//        if(getBlockType() == BASIC_BLOCK && fBottom != this.getBounds().bottom && xscale != null)
//        {
//            RectF xScaleBound = xscale.getBounds();
//            xScaleBound.top = getBounds().bottom - _cvm.XSCALE_H;
//            xscale.setBounds(xScaleBound.left, xScaleBound.top, xScaleBound.right, xScaleBound.top+_cvm.XSCALE_H);
//        }
        //2021.04.28 by lyk - kakaopay - xscale이 차트 가장 하단에 붙을 경우 주석처리 <<
    }
    public void setLocation(int sx, int sy){
        this.setBounds(sx,sy,getBounds().right,getBounds().bottom,true);
    }
    public float getY(){
        return bounds.top;
    }
    public float getX(){
        return bounds.left;
    }
    public float getHeight(){
        return bounds.height();
    }
    public float getRight(){
        return bounds.right;
    }
    public float getBottom(){
        return bounds.bottom;
    }
    //=============================
    //블럭의 기본 그래프에서 사용하는 데이터kind를 리턴한다
    //=============================
    public String[] getBasicDataKind(){
        if(graphs==null) return null;
        AbstractGraph graph = (AbstractGraph)graphs.elementAt(0);
        return graph.getGraphDatakind();
    }
    //=============================
    // 스케일 그룹을 나누기 위해서
    //=============================
    class VertScaleGroup{
        Vector<AbstractGraph> graphs;
        void add(AbstractGraph graph){
            if(graphs==null)graphs = new Vector<AbstractGraph>();
            graphs.addElement(graph);
        }
        AbstractGraph getGraph(int index){
            if(graphs != null && graphs.size()>index)
                return (AbstractGraph)graphs.elementAt(index);
            else
                return null;
        }
        void remove(String str){
            for(int i=0; i<graphs.size(); i++){
                AbstractGraph graph = (AbstractGraph)graphs.elementAt(i);
                if(graph.getGraphTitle().equals(str)){
                    graphs.removeElementAt(i);
                    return;
                }
            }
        }
        double[] setMinMax(){
            synchronized (this) {
                AbstractGraph[] ag= new AbstractGraph[graphs.size()];
                graphs.copyInto(ag);
                return setMinMax(ag);
            }
        }
        void draw(Canvas gl){
            AbstractGraph ag;
            AbstractGraph priceChart = null;
            //2017.06.12 by LYH >> 전략 강약 중복 추가 기능 처리
            DrawTool.g_StrongWeekCount = 0;
            DrawTool.g_StrongWeekIndex = -1;

            for(int i=0; i<graphs.size(); i++) {
                ag = (AbstractGraph)graphs.elementAt(i);
                if(ag.m_nStrategyType == 1)
                {
                    DrawTool.g_StrongWeekCount++;
                }
            }
            //2017.06.12 by LYH >> 전략 강약 중복 추가 기능 처리 end

            for(int i=0;i<graphs.size();i++){
                ag = (AbstractGraph)graphs.elementAt(i);

                //2015. 1. 13 by lyk - 동일지표인 경우 타이틀 처리
                String strGraphTitle = ag.getName();
                if(ag.getGraphTitle()!=null && ag.getGraphTitle().contains(COMUtil.JIPYO_ADD_REMARK)) {
                    strGraphTitle = ag.getGraphTitle();
                }

                if(!strGraphTitle.equals("일본식봉") && !strGraphTitle.equals("Heikin-Ashi"))
                //2015. 1. 13 by lyk - 동일지표인 경우 타이틀 처리 end
                {
                    //2013. 10. 23 데이터 없이 흰화면일 때 실시간 받으면 봉 그리기 처리>>
                    //if(!ag.formulated)return;
                    if(!ag.formulated)continue;
                    //2013. 10. 23 데이터 없이 흰화면일 때 실시간 받으면 봉 그리기 처리<<
                    //2017.06.12 by LYH >> 전략 강약 중복 추가 기능 처리
                    if(ag.m_nStrategyType == 1)
                        DrawTool.g_StrongWeekIndex++;
                    //2017.06.12 by LYH >> 전략 강약 중복 추가 기능 처리 end
                    ag.draw(gl);
                }
                else {
                    priceChart = ag;
                }
            }
            if(priceChart != null)
            {
                if(!priceChart.formulated)return;
                priceChart.draw(gl);
            }

            //2021.11.10 by LYH - 매물대 차트 타이틀 봉 위에 보이도록 수정 >>
            for(int i=0;i<graphs.size();i++){
                ag = (AbstractGraph)graphs.elementAt(i);
                if(ag.getName().equals("대기매물"))
                {
                    if(!ag.formulated)break;
                    VolumeforSaleGraph vag = (VolumeforSaleGraph)ag;
                    vag.drawVolumeForScaleTitle(gl);
                }
            }
            //2021.11.10 by LYH - 매물대 차트 타이틀 봉 위에 보이도록 수정 <<
        }
        //=============================
        // 스케일그룹에 포함되어 있는 모든 그래프에서의 데이터를 가지고 minmax를 구한다
        //=============================
        //Vector dt;
        DrawTool t;
        double[] bong_minmax=new double[2];
        double[] data=null;
        double[] minmax = new double[2];;
        double[] setMinMax(AbstractGraph[] ag){
            if(ag==null) return null;
            synchronized (this) {
                //20120621 by LYH >> 일목균형 스크롤 처리
                //int num=_cvm.getViewNum();        //화면에 그릴 데이터 수
                int num=_cvm.getViewNum()+_cvm.futureMargin;
                if(num>_cdm.getCount())
                    num = _cdm.getCount();
                //20120621 by LYH <<
                int index=_cvm.getIndex();        //화면에 그리기 시작할 인스
                minmax[0]=Double.MAX_VALUE;
                //2015. 1. 13 ADLine 지표 추가>>
//	            minmax[1]=Double.MIN_VALUE;
                minmax[1]=Integer.MIN_VALUE;	//2014.06.05 by LYH >> Double.MIN_VALUE값이 0보다 크게 잡혀 Integer.MIN_VALUE로 변경
                //2015. 1. 13 ADLine 지표 추가<<
                if(index<0||num<1)return minmax;
                int agLen = ag.length;

                Vector<DrawTool> dt;
                for(int i=0;i<agLen;i++){
                    if(ag[i].getGraphTitle().equals("일목균형표"))
                    {
                        index=_cvm.getIndex();
                        if((_cdm.getCount()-_cvm.getViewNum()-_cvm.getIndex()) <_cvm.futureMargin)
                            num=_cvm.getViewNum()+_cvm.futureMargin-(_cdm.getCount()-_cvm.getViewNum()-_cvm.getIndex());
                        else
                            num=_cvm.getViewNum();
                    }
                    else {
                        num=_cvm.getViewNum()+_cvm.futureMargin;
                        if(num>_cdm.getCount())
                            num = _cdm.getCount();
                        index=_cvm.getIndex();
                    }
                    dt=ag[i].getDrawTool();
                    int dtLen = dt.size();
                    //2017.05.11 by LYH >> 전략(신호, 강약) 추가
                    if(ag[i].getGraphType() == 4)
                        continue;
                    //2017.05.11 by LYH >> 전략(신호, 강약) 추가 end
                    for(int j=0;j<dtLen;j++){
                        try {
                            t = (DrawTool)dt.elementAt(j);
                        } catch (Exception e) {
                            break;
                        }
                        //2015.01.08 by LYH >> 3일차트 추가
                        if(_cvm.bStandardLine || _cvm.bIsLineFillChart || _cvm.bIsLineChart  || (_cvm.bIsLine2Chart && !t.getPacketTitle().equals("전일주가")))
                        {
                            data = _cdm.getSubPacketData(t.getPacketTitle());
                            if(t.getDrawType1()==2&&t.getDrawType2()==0){//기본거래량과 동일한 스타일인경우
                                if(data!=null) {
                                    if(_cvm.getScaleMode()){
                                        minmax[0]=0;
                                        minmax[1]=MinMax.getRangeMax(data, index+num,num);
                                    }
                                }

                            }else{
                                if(_cvm.getScaleMode()){
                                    minmax[0]=bong_minmax[0]=MinMax.getRangeMin_NoZero(data, index+num,num);
                                    minmax[1]=bong_minmax[1]=MinMax.getRangeMax(data, index+num,num);
                                }
                                if(t.getDrawType1()==0){//봉인경우 2차원
//		                    		t.setBongMinMax(bong_minmax);
                                }
                                if(_cvm.bIsLineChart || _cvm.bIsLineFillChart) {
                                    if(bong_minmax[0]==Double.MAX_VALUE)
                                        bong_minmax[0]=0;
                                    t.setBongMinMax(bong_minmax);
                                }
                            }
                        }
                        //2015.01.08 by LYH << 3일차트 추가
                        else if(t.getDrawType1()==0){//봉인경우 2차원

                            if(_cvm.getScaleMode()){
                                if(t.getDrawType2() == 8)
                                {
                                    minmax[0] = bong_minmax[0] = MinMax.getMIN(minmax[0], MinMax.getRangeMin_NoZero(_cdm.getSubPacketData("종가"), index + num, num));
                                    minmax[1] = bong_minmax[1] = MinMax.getMAX(minmax[1], MinMax.getRangeMax(_cdm.getSubPacketData("종가"), index + num, num));
                                }
                                else {
                                    if (COMUtil.bIsForeignFuture) //2017.05.26 by PJM 해외선물옵션 일때 0값 yscale에 안나타내도록 처리 >>
                                        minmax[0] = bong_minmax[0] = MinMax.getMIN(minmax[0], MinMax.getRangeMin_NoZero(_cdm.getSubPacketData("저가"), index + num, num));
                                    else
                                        minmax[0] = bong_minmax[0] = MinMax.getMIN(minmax[0], MinMax.getRangeMin(_cdm.getSubPacketData("저가"), index + num, num));
                                    minmax[1] = bong_minmax[1] = MinMax.getMAX(minmax[1], MinMax.getRangeMax(_cdm.getSubPacketData("고가"), index + num, num));
                                }
                            }
                            t.setBongMinMax(bong_minmax);
                        }else if(t.getDrawType1()==2&&t.getDrawType2()==0){//기본거래량과 동일한 스타일인경우
                            data = _cdm.getSubPacketData(t.getPacketTitle());
                            if(data!=null) {
                                if(_cvm.getScaleMode()){
                                    //minmax[0]=MinMax.getRangeMin(data, index+num,num);
                                    minmax[0]=0;
                                    minmax[1]=MinMax.getRangeMax(data, index+num,num);
                                }else{
                                    //                        	minmax[0]=MinMax.getMIN(minmax[0],MinMax.getIntMinT(data));
                                    //                            minmax[1]=MinMax.getMAX(minmax[1],MinMax.getIntMaxT(data));
                                }
                            }

                        }
                        //2020.04.20 roundedBar3 차트 추가 - hjw >>
                        else if(_cvm.m_nChartType == ChartViewModel.CHART_THREE_ROUNDED_BAR) {
                            double[] dataArr1 = _cdm.getSubPacketData("data1");
                            double[] dataArr2 = _cdm.getSubPacketData("data2");
                            double[] dataArr3 = _cdm.getSubPacketData("data3");

                            int nCount = dataArr1.length;

                            if(nCount > 0) {

                                double maxData = Double.NEGATIVE_INFINITY;
                                double minData = Double.POSITIVE_INFINITY;

                                for (int k = 0; k < nCount; k++) {
                                    if (dataArr1[k] > maxData) {
                                        maxData = dataArr1[k];
                                    }

                                    if (dataArr2[k] > maxData) {
                                        maxData = dataArr2[k];
                                    }

                                    if (dataArr3[k] > maxData) {
                                        maxData = dataArr3[k];
                                    }

                                    if (dataArr1[k] < minData) {
                                        minData = dataArr1[k];
                                    }

                                    if (dataArr2[k] < minData) {
                                        minData = dataArr2[k];
                                    }

                                    if (dataArr3[k] < minData) {
                                        minData = dataArr3[k];
                                    }
                                }
                                minmax[0] = minData;
                                minmax[1] = maxData;
                            }
                        }
                        //2020.04.20 roundedBar3 차트 추가 - hjw <<
                        else{
                            int idx = t.getPacketTitle().indexOf("매물대");
                            if(idx==-1){
                                if(ag[i].graphTitle.equals("Multi")){
                                    //                                data = _cdm.getSubPacketData(t.getPacketTitle(), index+num,num);
                                    //String strTitle = t.getPacketTitle()+"_1";
                                	String strTitle;
                                	if(_cvm.nCompareType == 2)
                                		strTitle = t.getPacketTitle();
                                	else
                                		strTitle = t.getPacketTitle()+"_1";
 
                                    data = _cdm.getSubPacketData(strTitle);
                                }else{
                                    if(ag[i].getGraphTitle().equals("주가이동평균")){
                                        data = _cdm.getSubPacketData(t.getPacketTitle()+j);
                                    }else {
                                        data = _cdm.getSubPacketData(t.getPacketTitle());
                                    }
                                }
                                idx = t.getPacketTitle().indexOf("삼선전환도");
                                if(idx!=-1 && data!=null) {
                                    minmax[0]=data[0];
                                    if(data.length<2)
                                        minmax[1]=data[0];
                                    else
                                        minmax[1]=data[1];
                                } else {
                                    if(data!=null){
                                        if(!t.getShowZeroValue()||BLOCK_TYPE==2){
                                            if(t.isVisible()){
                                            	//if((_cvm.getIsCandleMinMax() && isBasicBlock() && !_cvm.m_bCurrentChart))
                                                if((_cvm.getIsCandleMinMax() && isBasicBlock() && !_cvm.m_bCurrentChart) || _cvm.bIsMiniBongChart)
                                                    continue;
                                                double min=0, max = 0;
                                                if(_cvm.getScaleMode()) {
                                                    min = MinMax.getRangeMin_NoZero(data, index+num, num);
                                                    max = MinMax.getRangeMax(data, index+num, num);
                                                } else {
                                                    //		                                        min = MinMax.getIntMinT(data);
                                                    //		                                        max = MinMax.getIntMaxT(data);
                                                }
                                                if(min==0)minmax[0]=minmax[0];
                                                else minmax[0]=MinMax.getMIN(minmax[0],min);
                                                minmax[1]=MinMax.getMAX(minmax[1],max);
                                            }
                                        }else{
                                            double min=0, max=0;
                                            if(_cvm.getScaleMode()) {
                                                if(_cvm.bIsLine2Chart)
                                                    min = MinMax.getRangeMin_NoZero(data, index+num, num);
                                                else
                                                    min = MinMax.getRangeMinCompare(data, index+num, num);
                                                max = MinMax.getRangeMax(data, index+num, num);
                                            } else {
                                                //	                                        min = MinMax.getIntMinT(data);
                                                //	                                        max = MinMax.getIntMaxT(data);
                                            }
                                            minmax[0]=MinMax.getMIN(minmax[0],min);
                                            minmax[1]=MinMax.getMAX(minmax[1],max);
                                            //2014.03.18 by LYH >> 오실레이터 음수만 나올때 안 그리던 문제 해결.
                                            if(t.getDrawType2()==2 && min<0 && minmax[1]==Double.MIN_VALUE)
                                                minmax[1] = 0;
                                            //2014.03.18 by LYH << 오실레이터 음수만 나올때 안 그리던 문제 해결.
                                        }
                                    }
                                    //실시간 차트의 최대/최소 지정.
                                    String dataTypeName = _cdm.codeItem.strDataType;
                                    if(dataTypeName!=null) {
//                                        if(dataTypeName.equals("0") &&
//                                                t.getPacketTitle().equals("종가")) {
//                                            //2014.04.03 by LYH >> 가격차트가 선차트인 경우 최고가/최저가 표시 안 되던 문제 해결.
//                                            //t.setBongMinMax(minmax);
//                                            if(_cvm.getScaleMode()){
//                                                bong_minmax[0]=MinMax.getMIN(minmax[0],MinMax.getRangeMin(_cdm.getSubPacketData("저가"), index+num,num));
//                                                if(bong_minmax[0] == 0)
//                                                	bong_minmax[0]=MinMax.getMIN(minmax[0],MinMax.getRangeMin(_cdm.getSubPacketData("종가"), index+num,num));
//                                                bong_minmax[1]=MinMax.getMAX(minmax[1],MinMax.getRangeMax(_cdm.getSubPacketData("고가"), index+num,num));
//                                            }
//                                            //2014.04.03 by LYH << 가격차트가 선차트인 경우 최고가/최저가 표시 안 되던 문제 해결.
//                                            t.setBongMinMax(bong_minmax);
//                                        }
                                        //2016.11.21 by LYH >> 가격 라인차트 타입 최고, 최저값 표시
                                        if(t.getPacketTitle().equals("종가")) {
                                            if (_cvm.getScaleMode()) {
                                                bong_minmax[0] = MinMax.getMIN(minmax[0], MinMax.getRangeMin(_cdm.getSubPacketData("종가"), index + num, num));
                                                bong_minmax[1] = MinMax.getMAX(minmax[1], MinMax.getRangeMax(_cdm.getSubPacketData("종가"), index + num, num));
                                                t.setBongMinMax(bong_minmax);
                                            }
                                        }
                                        //2016.11.21 by LYH >> 가격 라인차트 타입 최고, 최저값 표시 end
                                    } else {
//		                            	System.out.println("dataTypenName:"+dataTypeName);
                                    }
                                }
                            }else{
                                scale[0].setVolumeSaleData(_cdm.getSubPacketData(t.getPacketTitle()));
                            }
                        }
                    }
                }

                if(minmax[0]==Double.MAX_VALUE)
                {
                    minmax[0]=-1;
                    //2016.02.11 by LYH >> 라인 비율 차트 0만 있는 경우 가운데 처리
                    if(minmax[1]==0 && _cvm.bInvestorChart)
                    {
                        minmax[1]=1;
                    }
                    //2016.02.11 by LYH << 라인 비율 차트 0만 있는 경우 가운데 처리
                }
                if(minmax[1]==Double.MIN_VALUE)
                {
                    minmax[0]=1;
                }
                try {
                    //2022.05.02 by lyk - min, max 값이 같을 경우 차트 중앙에 라인을 표시하도록 수정 >>
                    String strMin = String.format("%." + _cdm.nTradeMulti + "f", minmax[0]);
                    String strMax = String.format("%." + _cdm.nTradeMulti + "f", minmax[1]);
                    if (minmax[0] == minmax[1] || strMin.equals(strMax)) {
                        //2022.05.02 by lyk - min, max 값이 같을 경우 차트 중앙에 라인을 표시하도록 수정 <<
                        //                if(minmax[0]==minmax[1]){
                        if (minmax[0] == 0) {
                            minmax[1] += 1;
                        } else {
                            minmax[0] -= 1;
                            minmax[1] += 1;
                        }
                    }
                } catch (Exception e) {
                    Log.d("chart", "setMinMax");
                }
                for(int i=0;i<ag.length;i++){
                    ag[i].setMinMax(minmax);
                }
                return minmax;
            }
        }
    }

    //=============================
    // 블럭 그리기
    //=============================

    public void draw(Canvas gl){
        synchronized (this) {
            int i=0;
            VertScaleGroup vsg;
            AbstractGraph ag;
            double[] minmax;
            String[] graphDatakind;
            int strindex;
            if(scalegroups == null)
                return;
            int sgLen = scalegroups.size();
            for(i=0;i<sgLen;i++){
                vsg = (VertScaleGroup)scalegroups.elementAt(i);
                minmax=vsg.setMinMax();
                if(minmax==null) return;
                ag =(AbstractGraph)vsg.getGraph(0);
                if(ag == null)
                    return;
                graphDatakind =ag.getGraphDatakind();

                strindex = graphDatakind[0].indexOf(",");
                if(scale!=null)
                {
	                if(scale.length>i && scale[i]!=null){
	
	                    if(strindex!=-1) scale[i].setProperties2(_cdm.getDataFormat(new String(graphDatakind[0].substring(0,strindex))),graphDatakind[0]);
	                    else{
	                        if(ag.getGraphTitle().equals("Multi")){
	                            //scale[i].setProperties2(_cdm.getPriceFormat(),graphDatakind[0]);
	                            scale[i].setProperties2(11,graphDatakind[0]);
	                        }else{
	                            if(_cvm.bInvestorChart)
                                    scale[i].setProperties2(_cdm.getDataFormat(ag.getGraphTitle()),ag.getGraphTitle());
	                            else
	                                scale[i].setProperties2(_cdm.getDataFormat(ag.getGraphDatakind()[0]),ag.getGraphDatakind()[0]);
	                        }
	                    }
	                    scale[i].setMinMax(minmax);
	                }
                }
            }
            if(scale!=null){
                for(i=0;i<scale.length;i++){
                    scale[i].draw(gl);
                }
            }

            if(scalegroups==null) {
//	        	System.out.println("scalegroups : "+scalegroups);
                return;
            }
            drawBound(gl);

            if(_cvm.bInvestorChart && _cvm.getAssetType() != ChartViewModel.ASSET_LINE_MOUNTAIN)
            {
                for(i=0; i<scalegroups.size(); i++) {
                    vsg = (VertScaleGroup)scalegroups.elementAt(i);
                    vsg.draw(gl);
                }
            }
            else
            {
                for(i=scalegroups.size()-1; i>=0; i--) {
                    vsg = (VertScaleGroup)scalegroups.elementAt(i);
                    vsg.draw(gl);
                }
            }

            if (scale != null) {
                for (i = 0; i < scale.length; i++) {
                    if (scale[i].getScale_pos()==1 && scale[i].getShowCurrPrice()) {
                        //                    scale[i].draw(gl);
                        if (COMUtil.isyJonggaShow())
                            scale[i].drawBuyAveragePrice(gl); //2021.02.18 by HJW - 매입평균선 추가
                    }
                }
            }

            drawScrollRect(gl);

//            //2014. 3. 20 Y축 봉갯수 누르면 봉입력창 뜨고 봉갯수변경>>
//            if(null != tvViewNum)
//            {
//                tvViewNum.setText(String.valueOf(_cvm.getViewNum()));
//            }
//            //2014. 3. 20 Y축 봉갯수 누르면 봉입력창 뜨고 봉갯수변경<<
        }
    }
    public void drawBound(Canvas gl){
        //2019. 03. 12 by hyh - 투자자 업종 가로 바차트 추가. || _cvm.bIsUpdownChart
        if(_cvm.bIsMiniBongChart || _cvm.bIsLineChart || _cvm.bIsLine2Chart || _cvm.bIsLineFillChart || _cvm.bIsUpdownChart || _cvm.m_nChartType != 0 || (_cvm.bIsLineFillChart&&_cvm.bIsOneQStockChart) || (_cvm.bInvestorChart && _cvm.bIsOneQStockChart))
            return;
        //gl.glLineWidth(1.0f);
        int[] rectLineColor = CoSys.rectLineColor;
        if(_cvm.getSkinType()==COMUtil.SKIN_WHITE)
            rectLineColor = CoSys.rectLineColorWhiteSkin;

        //2013.03.26 by LYH >> 디자인 적용.
        float x1 = out_graph_bounds.left;
//        float y1 = out_graph_bounds.top;
//        float x2 = out_graph_bounds.left+out_graph_bounds.width();
        float x2 = bounds.left + bounds.right;
        float y2 = out_graph_bounds.top+out_graph_bounds.height()+(int)COMUtil.getPixel(1);
        //2013.03.26 by LYH <<

        boolean bContainsJipyo = false;

        if(_cvm.bIsLine2Chart) {
            _cvm.setLineWidth(1);
            _cvm.drawLine(gl, x1,y2,x2,y2, CoSys.XScaleLineColor ,1.0f);
            return;
        }

        if(_cvm.bIsTodayLineChart) {
            Vector<String> strGraphs = COMUtil._mainFrame.mainBase.baseP._chart.getGraphList();
            if (strGraphs != null) {
                for (int i = 0; i < strGraphs.size(); i++) {
                    String strGraph = (String) strGraphs.elementAt(i);
                    if (strGraph.equals("매물대")) {
                        bContainsJipyo = true;
                        break;
                    }
                }
            }
            if(bContainsJipyo) {
                _cvm.drawLine(gl, x1,y2,x2,y2, CoSys.XScaleLineColor ,1.0f);
                return;
            }
        }

        if(title == "가격차트" ||
                title == "삼선전환도" ||
                title == "P&F차트" ||
                title == "스윙" ||
                title == "렌코" ||
                title == "역시계곡선" ||
                title == "Heikin-Ashi" )  {
//        	System.out.println("drawbounds"+out_graph_bounds);
            //2013.03.26 by LYH >> 디자인 적용.
            x2 = bounds.left + bounds.right;
            if(_cvm.m_bCurrentChart)
            {
                _cvm.setLineWidth_Fix(COMUtil.getPixel_H(1));
                rectLineColor[0] =211;
                rectLineColor[1] =211;
                rectLineColor[2] =211;
                _cvm.drawRect(gl, x1,out_graph_bounds.top+(int)COMUtil.getPixel(1),out_graph_bounds.width(),out_graph_bounds.height(), rectLineColor);
                _cvm.setLineWidth(1);
            }
            else
            {
//                _cvm.setLineWidth_Fix(COMUtil.getPixel_H(1));
                _cvm.setLineWidth_Fix(COMUtil.getPixel_H(1f));
                _cvm.drawLine(gl, x1,y2,x2,y2, rectLineColor ,0.2f);
//                //==========================================
//                // 차트 하단 스크롤 영역 표시 처리 (by lyk 2014.05.07)
//                //==========================================
//                if(!_cvm.bIsLineFillChart && _cvm.XSCALE_H> 10) {
//                    int sWidth = (int)(_cvm.getDataWidth() * _cdm.getCount());
//                    int vX = (int)(_cvm.getDataWidth() * _cvm.getViewNum());
//                    int sX = (int)(_cvm.getDataWidth() * _cvm.getIndex());
//                    if(vX==0) return;
//                    float ratio = (float)vX/(float)sWidth;
//                    float x = graph_bounds.left+sX * ratio ;
//                    float h = (int)COMUtil.getPixel(3);
//                    float y = out_graph_bounds.bottom-h;
//                    float w = vX * ratio;
//
//                    //Rect rectScroll = new Rect((int)x, (int)y, (int)w+(int)x, (int)h+(int)y);
//                    //g.drawRect(rectScroll, pnt);
//                    _cvm.drawFillRect(gl, getBounds().left, y, getBounds().left+ out_graph_bounds.width(), h, CoSys.scrollBackLineColor, 1.0f);
//                    _cvm.drawFillRect(gl, x, y, w, h, CoSys.scrollLineColor, 1.0f);
//                    //_cvm.drawLine(gl, this.out_graph_bounds.left, y+h+(int)COMUtil.getPixel(1), this.out_graph_bounds.width(), y+h+(int)COMUtil.getPixel(1), CoSys.rectLineColor ,1.0f);
//                }
                _cvm.setLineWidth(1f);
            }

        } else {
            if(_cvm.bIsTodayLineChart)
                x2 = bounds.left + bounds.right;
            _cvm.setLineWidth_Fix(COMUtil.getPixel_H(0.5f));
            _cvm.drawLine(gl, x1 ,y2, x2, y2, rectLineColor ,0.2f); //보조지표 블럭 하단 가로 라인
            _cvm.setLineWidth(1f);

            //2013.03.26 by LYH >> 디자인 적용.
            //_cvm.drawRect(gl, this.out_graph_bounds.left, this.out_graph_bounds.top+(int)COMUtil.getPixel(1), this.out_graph_bounds.width(), this.out_graph_bounds.height(), rectLineColor);
            //_cvm.drawLine(gl, x1,y2,x2,y2, rectLineColor ,1.0f);
            //_cvm.drawLine(gl, x2,y1,x2,y2, rectLineColor ,1.0f);
            //2013.03.26 by LYH <<

            //2021.07.22 by hanjun.Kim - kakaopay - 로테이트 블럭 갯수 표시 안함
//            drawTitleNum(gl);

////        	_cvm.drawFillRect(gl, upbounds.left,upbounds.top,26,26, CoSys.scrollLineColor, 1.0f);
//        	if(changeBlockBtnImg!=null) {
////        		gl.drawBitmap(changeBlockBtnImg, getBounds().right-W_YSCALE-margineR,upbounds.top, mPaint);
//        		gl.drawBitmap(changeBlockBtnImg, null, new Rect( getBounds().right-this.W_YSCALE-margineR,upbounds.top+(int)COMUtil.getPixel(2), getBounds().right-this.W_YSCALE-margineR+(int)COMUtil.getPixel(12), upbounds.top+(int)COMUtil.getPixel(2)+(int)COMUtil.getPixel(12)), mPaint);
////        		isChangeBlockBtnDraw = false;
//        	}
        }

    }

    public void drawScrollRect(Canvas gl)
    {
        if(_cvm.bIsMiniBongChart || _cvm.bIsLineChart || _cvm.bIsLine2Chart || _cvm.bIsUpdownChart || _cvm.bStandardLine || _cvm.m_nChartType != 0 || (_cvm.bIsLineFillChart&&_cvm.bIsOneQStockChart) || (_cvm.bInvestorChart && _cvm.bIsOneQStockChart))
            return;
        if(title == "가격차트" ||
                title == "삼선전환도" ||
                title == "P&F차트" ||
                title == "스윙" ||
                title == "렌코" ||
                title == "역시계곡선" ||
                title == "Heikin-Ashi" ) {
            if (!_cvm.bIsLineFillChart && _cvm.XSCALE_H > 10) {
                //2024.01.03 by LYH - 일목균형표 줌인, 줌아웃, 스크롤 개선 >>
//                int sWidth = (int) (_cvm.getDataWidth() * _cdm.getCount());
                int sWidth = (int) (_cvm.getDataWidth() * (_cdm.getCount()+_cvm.futureMargin));
                //2024.01.03 by LYH - 일목균형표 줌인, 줌아웃, 스크롤 개선 <<
                int vX = (int) (_cvm.getDataWidth() * _cvm.getViewNum());
                int sX = (int) (_cvm.getDataWidth() * _cvm.getIndex());
                if (vX == 0) return;
                float ratio = (float) vX / (float) sWidth;
                float x = graph_bounds.left + sX * ratio;
                float h = (int) COMUtil.getPixel_H(2);
//                float y = out_graph_bounds.bottom - h - _cvm.XSCALE_H; //2021.04.28 by lyk - kakaopay - 스크롤바 위치가 주블럭 하단일 경우 처리
                float y = out_graph_bounds.bottom - h - (int) COMUtil.getPixel_H(1); //2021.04.28 by lyk - kakaopay - 스크롤바 위치가 차트 하단일 경우 처리
                float w = vX * ratio;

                //Rect rectScroll = new Rect((int)x, (int)y, (int)w+(int)x, (int)h+(int)y);
                //g.drawRect(rectScroll, pnt);
//                if(_cvm.getSkinType()==COMUtil.SKIN_BLACK)
//                {
//                    _cvm.drawFillRect(gl, getBounds().left, y, getBounds().left + out_graph_bounds.width(), h, CoSys.scrollBackLineColor_black, 1.0f);
//                }
//                else
//                {
//                    _cvm.drawFillRect(gl, getBounds().left, y, getBounds().left + out_graph_bounds.width(), h, CoSys.scrollBackLineColor, 1.0f);
//                }
                _cvm.drawFillRect(gl, x, y, w, h, CoSys.scrollLineColor, 1.0f);

                //_cvm.drawLine(gl, this.out_graph_bounds.left, y+h+(int)COMUtil.getPixel(1), this.out_graph_bounds.width(), y+h+(int)COMUtil.getPixel(1), CoSys.rectLineColor ,1.0f);
            }
        }
    }
    public void drawBoundLine(Canvas gl){
//        if(_cvm.bIsLineChart)
//            return;
//        //gl.glLineWidth(1.0f);
//        int[] rectLineColor = CoSys.rectLineColor;
//        if(_cvm.getSkinType()!=COMUtil.SKIN_BLACK)
//            rectLineColor = CoSys.rectLineColorWhiteSkin;
//
//        //2013.03.26 by LYH >> 디자인 적용.
//        float x1 = out_graph_bounds.left;
//        float y1 = out_graph_bounds.top;
//        float x2 = out_graph_bounds.left+out_graph_bounds.width();
//        float y2 = out_graph_bounds.top+out_graph_bounds.height();
//        //2013.03.26 by LYH <<
//        if(title == "가격차트" ||
//                title == "삼선전환도" ||
//                title == "P&F차트" ||
//                title == "스윙" ||
//                title == "Kagi" || //2015.06.23 by lyk - 차트 유형 추가
//                title == "렌코" ||
//                title == "역시계곡선" ||
//                title == "Heikin-Ashi" ||
//                _cvm.chartType == COMUtil.COMPARE_CHART)  {
//
//        } else {
//            //2013.03.26 by LYH >> 디자인 적용.
//            //_cvm.drawRect(gl, this.out_graph_bounds.left, this.out_graph_bounds.top+(int)COMUtil.getPixel(1), this.out_graph_bounds.width(), this.out_graph_bounds.height(), rectLineColor);
//            _cvm.drawLine(gl, x1,y2,x2,y2, rectLineColor ,1.0f);
//            _cvm.drawLine(gl, x2,y1,x2,y2, rectLineColor ,1.0f);
//            //2013.03.26 by LYH <<
//        }

    }
    public String getTitle(){
        return title;
    }
    //private int[] textures = null;
    public void loadGLTextureFromResource(GL10 gl, Context context) {
//		InputStream is = null;
//		gl.glGenTextures(2, textures, 0);
//		int layoutResId;
//        for (int i = 0; i < 2; i++) {
//            switch (i) {
//            case 0:
//            	layoutResId = context.getResources().getIdentifier("icons_dot", "drawable", context.getPackageName());
//            	is = context.getResources().openRawResource(layoutResId);
//                break;
//            case 1:
//            	layoutResId = context.getResources().getIdentifier("icons_dot_s", "drawable", context.getPackageName());
//            	is = context.getResources().openRawResource(layoutResId);
//                break;
//            }
//
//            
//            Bitmap bitmap = null;
//            Bitmap src = null;
//            try {
////            	BitmapFactory.Options options = new BitmapFactory.Options();
////            	options.inSampleSize = 1;
//
//                // BitmapFactory is an Android graphics utility for images
//            	src = BitmapFactory.decodeStream(is);
//               // bitmap = Bitmap.createScaledBitmap(src, chart_bounds.width(), chart_bounds.height(), true);
//            	 bitmap = Bitmap.createScaledBitmap(src, 4, 4, false);
//            	 src.recycle();
//
//            } finally {
//                // Always clear and close
//                try {
//                    is.close();
//                    is = null;
//                } catch (IOException e) {
//                }
//            }
//            // Create Linear Filtered Texture and bind it to texture
//            
//            gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[i]);
//            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
//                    GL10.GL_LINEAR);
//            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
//                    GL10.GL_LINEAR);
//            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
//
//            // Clean up
//            bitmap.recycle();
//        }
    }
    //	public void loadGLTextureFromResource(GL10 gl, Context context) {
//		InputStream is = null;
//		gl.glGenTextures(2, textures, 0);
//        for (int i = 0; i < 2; i++) {
//            Bitmap bitmap = null;
//            Bitmap src = null;
//            
//            switch (i) {
//            case 0:
//            	//is = context.getResources().openRawResource(R.drawable.icons_dot);
//            	bitmap = BitmapFactory.decodeResource(context.getResources(),
//                        R.drawable.icons_dot);
//                break;
//            case 1:
//            	//is = context.getResources().openRawResource(R.drawable.icons_dot_s);
//            	bitmap = BitmapFactory.decodeResource(context.getResources(),
//                        R.drawable.icons_dot_s);
//                break;
//            }
//       
////
////            try {
////            	BitmapFactory.Options options = new BitmapFactory.Options();
////            	options.inSampleSize = 1;
////
////                // BitmapFactory is an Android graphics utility for images
////            	src = BitmapFactory.decodeStream(is);
////               // bitmap = Bitmap.createScaledBitmap(src, chart_bounds.width(), chart_bounds.height(), true);
////            	 bitmap = Bitmap.createScaledBitmap(src, 256, 256, true);
////            	 src.recycle();
////
////            } finally {
////                // Always clear and close
////                try {
////                    is.close();
////                    is = null;
////                } catch (IOException e) {
////                }
////            }
//            // Create Linear Filtered Texture and bind it to texture
//            
//            gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[i]);
//            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
//                    GL10.GL_LINEAR);
//            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
//                    GL10.GL_LINEAR);
//            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
//
//            // Clean up
//            bitmap.recycle();
//        }
//	}
    public void setTitleNumber(int x, int y) {
        nGraphNum = x;
        nSelGraph = y;
    }
    static int crop[] = { 0, 4, 4, -4 };
    public void drawTitleNum(Canvas gl) {
//    	if(textures==null) {
//    		textures = new int[2];
//    		loadGLTextureFromResource(gl, COMUtil._chartMain); //image load.
//    	}

//		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
//    	gl.glEnable(GL10.GL_TEXTURE_2D);
//    	gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        float nXPos = out_graph_bounds.left+out_graph_bounds.width()/2-(10*(nGraphNum-1))/2;
        float nYPos = out_graph_bounds.top+(int)COMUtil.getPixel(4);
        int[] color=new int[3];
        for(int i=0; i<nGraphNum; i++) {
            if(i != nSelGraph) {
//    			gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
                color[0]=150;
                color[1]=150;
                color[2]=150;
                if(_cvm.getSkinType()!=COMUtil.SKIN_BLACK) {
                    color[0]=70;
                    color[1]=70;
                    color[2]=70;
                }
                //_cvm.drawCircle(gl,nXPos+i*10, nYPos, 6, 6, true, color);
                _cvm.drawCircle(gl,nXPos+i*(int)COMUtil.getPixel(5), nYPos, nXPos+i*(int)COMUtil.getPixel(5)+(int)COMUtil.getPixel(3)
                        , nYPos+(int)COMUtil.getPixel(3), true, color);
            } else {
//    			gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[1]);
                color[0]=255;
                color[1]=255;
                color[2]=255;
                if(_cvm.getSkinType()!=COMUtil.SKIN_BLACK) {
                    color[0]=255;
                    color[1]=0;
                    color[2]=0;
                }
                //_cvm.drawCircle(gl,nXPos+i*10, nYPos, 6, 6, true, color);
                _cvm.drawCircle(gl,nXPos+i*(int)COMUtil.getPixel(5), nYPos, nXPos+i*(int)COMUtil.getPixel(5)+(int)COMUtil.getPixel(3)
                        , nYPos+(int)COMUtil.getPixel(3), true, color);
            }
//			// Set crop area
//			((GL11) gl).glTexParameteriv(GL10.GL_TEXTURE_2D, GL11Ext.GL_TEXTURE_CROP_RECT_OES,crop,0);
//			// Draw texture
//			((GL11Ext) gl).glDrawTexfOES(nXPos+i*10, nYPos,0.0f,6,6);

        }
//		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
//    	gl.glDisable(GL10.GL_TEXTURE_2D);
    }
    public void setTitle(String title){
        this.title = title;
    }
    public void destroy(){
        if(graphs!=null){
            for(int i=0; i<graphs.size(); i++){
                AbstractGraph graph = (AbstractGraph)graphs.elementAt(i);
                graph.destroy();
            }
            graphs.removeAllElements();
            graphs = null;
        }
        for(int i=0; i<scalegroups.size(); i++){
            VertScaleGroup vsg = (VertScaleGroup)scalegroups.elementAt(i);
            vsg.graphs.removeAllElements();
        }
        scalegroups.removeAllElements();
        scalegroups = null;

        //블럭삭제버튼 제거하기.
        //final LinearLayout ll = (LinearLayout)COMUtil._chartMain.layout.findViewWithTag(this.delButton);
        //if(delButtonLayout==null) return;
        //delButtonLayout.removeAllViews();
        parentView.layout.removeView(delButtonLayout);
        COMUtil.unbindDrawables(delButtonLayout);
        parentView.layout.removeView(changeBlockButtonLayout);
        COMUtil.unbindDrawables(changeBlockButtonLayout);
        //2014. 3. 20 Y축 봉갯수 누르면 봉입력창 뜨고 봉갯수변경>>
        parentView.layout.removeView(tvViewNumLayout);
        COMUtil.unbindDrawables(tvViewNumLayout);
        //2014. 3. 20 Y축 봉갯수 누르면 봉입력창 뜨고 봉갯수변경<<

//        if(changeBlockBtnImg!=null) {
//	        changeBlockBtnImg.recycle();
//	        changeBlockBtnImg = null;
//        }
        if(delButtonLayout != null)
        {
            delButtonLayout = null;
            delButton = null;
        }
        if(changeBlockButtonLayout != null)
        {
            changeBlockButtonLayout = null;
            changeBlockButton = null;
        }
        //2014. 3. 20 Y축 봉갯수 누르면 봉입력창 뜨고 봉갯수변경>>
        if(tvViewNumLayout != null)
        {
            tvViewNumLayout = null;
            tvViewNum = null;
        }
        //2014. 3. 20 Y축 봉갯수 누르면 봉입력창 뜨고 봉갯수변경<<

        //System.gc();
    }

    public void ViewIndexChanged(ViewEvent e){
        //인덱스 스크롤에 의해 바뀌었을때
    }
    public void ViewNumChanged(ViewEvent e){
        //한화면에 표시할 데이터의 수가 바뀌었을때
        //2014. 3. 20 Y축 봉갯수 누르면 봉입력창 뜨고 봉갯수변경>>
        if(null != tvViewNum)
        {
            tvViewNum.setText(String.valueOf(_cvm.getViewNum()));
        }
        //2014. 3. 20 Y축 봉갯수 누르면 봉입력창 뜨고 봉갯수변경<<
    }
    //스케일 라인
    public void ViewModeChanged(ViewEvent e){
        if(scale!=null){
            scale[0].setScaleLineType(_cvm.getScaleLineType());
        }
    }
    public void ViewBackColorChanged(ViewEvent e){
        //배경색 변환
        if(scale!=null){
            for(int i=0;i<scale.length;i++){
                scale[i].setLineColor(_cvm.CSL);
                scale[i].setTextColor(_cvm.CST);
                scale[i].setBackColor(_cvm.getBackColor());

            }
        }
    }
    public XScale getXScale(){
        return this.xscale;
    }
    public void setXScale(XScale xscale){
        this.xscale = xscale;
    }

    public void setChangeBlockBtn(float x, float y) {
        //2019. 09. 30 by hyh - 가격 블럭 이동 막음 >>
        if (isBasicBlock() || _cvm.bIsTodayLineChart)
            return;
        //2019. 09. 30 by hyh - 가격 블럭 이동 막음 <<

        boolean bStart = false;
        //블럭 이동버튼 추가
        if(changeBlockButtonLayout == null) {
            bStart = true;
            //changeBlockButtonLayout = new LinearLayout(parentView.getContext());
            //changeBlockButton = new Button(parentView.getContext());
            LayoutInflater factory = LayoutInflater.from(parentView.getContext());

            int layoutResId = parentView.getContext().getResources().getIdentifier(
                    "changebuttonview", "layout", parentView.getContext().getPackageName());
            changeBlockButtonLayout = (LinearLayout) factory.inflate(layoutResId, null);
            //delButton = new Button(parentView.getContext());
            layoutResId = parentView.getContext().getResources().getIdentifier(
                    "changebutton", "id", parentView.getContext().getPackageName());
            changeBlockButton = (Button)changeBlockButtonLayout.findViewById(layoutResId);

            changeBlockButtonLayout.setDuplicateParentStateEnabled(false);
            changeBlockButton.setDuplicateParentStateEnabled(false);

            changeBlockButtonLayout.setClickable(false);
            changeBlockButton.setClickable(false);
//    		changeBlockButton.setTouchDelegate(delegate)
        }

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int)COMUtil.getPixel_W(18), (int)COMUtil.getPixel_W(18));
        //2021.06.24 by hanjun.Kim - kakaopay - 블럭 이동 버튼 위치 변경 및 이동영역 재설정 >>
//        params.leftMargin=(int)(x-margineR-this.W_YSCALE);
//        params.leftMargin=(int)(x-COMUtil.getPixel_W(35));
        params.leftMargin=(int)(x-margineR-COMUtil.getPixel_W(16));
        params.topMargin=(int)(y+COMUtil.getPixel_H(2));
        changeBlockButtonLayout.setLayoutParams(params); 
        setUpBounds(params.leftMargin,params.topMargin,H_UPBOUNDS+(int)COMUtil.getPixel(1),H_UPBOUNDS+(int)COMUtil.getPixel(1));

//        Log.d("changeBlockButton:", bStart+"");

        if(!bStart) {
            return;
        }
        String strSortBtnId = null;

        ColorStateList colorResId;
        int layoutResId;

        if(COMUtil.currentTheme == COMUtil.SKIN_WHITE)
        {
//            strSortBtnId = "ico_sort";
            colorResId = ColorStateList.valueOf(Color.parseColor("#060b11"));
        }
        else
        {
//            strSortBtnId = "ico_sort_black";
            colorResId = ColorStateList.valueOf(Color.parseColor("#7afcfcfc"));
        }

//        layoutResId = this.parentView.getContext().getResources().getIdentifier(strSortBtnId, "drawable", this.parentView.getContext().getPackageName());
        layoutResId = this.parentView.getContext().getResources().getIdentifier("ico_sort", "drawable", this.parentView.getContext().getPackageName());
        changeBlockButton.setBackgroundResource(layoutResId);
        ViewCompat.setBackgroundTintList(changeBlockButton,  colorResId);

        changeBlockButtonLayout.setGravity(Gravity.CENTER);

        //2012. 7. 17  보조지표 X버튼이 지표설정창 위로 오지 않게 수정
        if(parentView.layout.getChildAt(parentView.layout.getChildCount() - 1) instanceof IndicatorConfigView)
        {
            parentView.layout.addView(changeBlockButtonLayout, parentView.layout.getChildCount() - 2);
        }
        else
        {
            parentView.layout.addView(changeBlockButtonLayout);
        }
        if(getBounds().width()<=COMUtil.getPixel(180))
        {
            setHideChangeBlockButton(true); //블럭이동버튼 숨김.
        }
        else {
            setHideChangeBlockButton(false); //블럭이동버튼 보임.
        }
    }
    public void setBlockBtn(float x, float y, String title) {
        //2021.05.10 by lyk - kakaopay - 블럭 삭제버튼 사용안함
        if(true)
            return;

        if(isBasicBlock()|| _cvm.bIsMiniBongChart || _cvm.chartType == COMUtil.COMPARE_CHART || _cvm.bIsTodayLineChart)
            return;
        boolean bStart = false;

        if(delButtonLayout == null) {
            bStart = true;
            //delButtonLayout = new LinearLayout(parentView.getContext());
            LayoutInflater factory = LayoutInflater.from(parentView.getContext());

            int layoutResId = parentView.getContext().getResources().getIdentifier(
                    "delbuttonview", "layout", parentView.getContext().getPackageName());
            delButtonLayout = (LinearLayout) factory.inflate(layoutResId, null);
            //delButton = new Button(parentView.getContext());
            layoutResId = parentView.getContext().getResources().getIdentifier(
                    "delbutton", "id", parentView.getContext().getPackageName());
            delButton = (Button)delButtonLayout.findViewById(layoutResId);
            delButton.setClickable(false);
            //2012. 7. 26 (아이폰) 라이브러리 변경 및 delButton 액션 처리.   적용
            delButtonLayout.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DRAlertDialog alert = new DRAlertDialog(COMUtil.apiView.getContext());
                    alert.setMessage("지표를 삭제하시겠습니까?");
                    alert.setNoButton("취소", null);
                    alert.setYesButton("삭제",
                            new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog,int which) {

                                    handleDelTap();
                                    //알림창 닫기
                                    dialog.dismiss();
                                }
                            });
                    alert.show();
                    COMUtil.g_chartDialog = alert;
                }
            });
            // delButton 중복 클릭 리스너 삭제
        }
        String deviceType = COMUtil.deviceMode;
//    		RelativeLayout.LayoutParams paramChart = (RelativeLayout.LayoutParams)parentView.getLayoutParams();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int)COMUtil.getPixel_W(35), (int)COMUtil.getPixel_W(35));
//    		RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams)parentView.getLayoutParams();
        params.leftMargin=(int)(x-COMUtil.getPixel_W(35));
        params.topMargin=(int)y;
        //delButtonLayout.setTag(delButton);
        int buttonpadding = (int) COMUtil.getPixel_W(17);
        delButton.setPadding(buttonpadding,buttonpadding,buttonpadding,buttonpadding);
        delButtonLayout.setGravity(Gravity.TOP|Gravity.RIGHT);
        delButtonLayout.setLayoutParams(params);
        if(!bStart) {
            return;
        }
        int tag = 0;

        //2015. 1. 13 by lyk - 동일지표 추가 (추가리스트 적용)
        Vector<Hashtable<String, String>>  v = (Vector<Hashtable<String, String>>)COMUtil.getJipyoMenu().clone();
        Vector<Hashtable<String, String>> addItems = COMUtil.getAddJipyoList();
        for(int i=0; i<addItems.size(); i++) {
            v.add(addItems.get(i));
        }
        int len = v.size();
//    		int len = COMUtil.getJipyoMenu().size();
        //2015. 1. 13 by lyk - 동일지표 추가 (추가리스트 적용) end

        for(int k=0; k<len; k++) {
            //2015. 1. 13 by lyk - 동일지표 추가 (추가리스트 적용)
//    			Hashtable<String, String> item = (Hashtable<String, String>)COMUtil.getJipyoMenu().get(k);
            Hashtable<String, String> item = (Hashtable<String, String>)v.get(k);
            //2015. 1. 13 by lyk - 동일지표 추가 (추가리스트 적용) end

            String cmp = (String)item.get("name");
            if(cmp.equals(title)) {
                tag = Integer.parseInt((String)item.get("tag"));
                delButton.setTag(tag);
                break;
            }
        }
        //delButton.setBackgroundResource(R.drawable.jipyo_close);
//	        if(COMUtil.skinType == COMUtil.SKIN_BLACK) {
//	        	int layoutResId = this.parentView.getContext().getResources().getIdentifier("close", "drawable", this.parentView.getContext().getPackageName());
//	        	delButton.setBackgroundResource(layoutResId);
//	        	delButton.setWidth((int)COMUtil.getPixel(23));
//	        	delButton.setHeight((int)COMUtil.getPixel(23));
//	        }else{
        int layoutResId = this.parentView.getContext().getResources().getIdentifier("close_white", "drawable", this.parentView.getContext().getPackageName());
//	        	delButton.setBackgroundResource(layoutResId);
        //Bitmap image = BitmapFactory.decodeResource(this.parentView.getContext().getResources(), layoutResId);
        delButton.setBackgroundResource(layoutResId);
//            delButton.getBackground().setAlpha(80);
        //Drawable drawable = (Drawable)(new BitmapDrawable(image));
        //drawable.setAlpha(80);
//	        	//delButton.setBackgroundDrawable(drawable);
//	        	delButton.setWidth(13);
//	        	delButton.setHeight(13);
//	        }


        delButtonLayout.setGravity(Gravity.CENTER);
//        delButtonLayout.addView(delButton);
        //2012. 7. 17  보조지표 X버튼이 지표설정창 위로 오지 않게 수정
        if(parentView.layout.getChildAt(parentView.layout.getChildCount() - 1) instanceof IndicatorConfigView)
        {
            parentView.layout.addView(delButtonLayout, parentView.layout.getChildCount() - 2);
        }
        else
        {
            parentView.layout.addView(delButtonLayout);
        }

        //2012. 11. 1  가격차트가 보조지표 아래에 위치한 경우 삭제버튼 표시하지 않기
        if(this.isBasicBlock() || _cvm.bIsMiniBongChart)
        {
            delButtonLayout.setVisibility(LinearLayout.GONE);
        }
    }
    //2014. 3. 20 Y축 봉갯수 누르면 봉입력창 뜨고 봉갯수변경>>
    /**
     * YScale 에 차트 봉갯수(viewNum)을 표시하고, 터치가능한 TextView 를 넣는다. 
     * @param x, y : x,y  좌표
     * */
    public void setViewNumTextViewInYScale(float x, float y) {
        //2021.05.10 by lyk - kakaopay - 봉 갯수 설정 사용안함
        if(true)
         return;

        boolean bStart = false;
        //레이아웃은 한번만 세팅됨.
        if(tvViewNumLayout == null) {
            bStart = true;

            //xml로드
            LayoutInflater factory = LayoutInflater.from(parentView.getContext());
            tvViewNumLayout = (LinearLayout) factory.inflate(parentView.getContext().getResources().getIdentifier("viewnumtextview", "layout", parentView.getContext().getPackageName()), null);
            COMUtil.setGlobalFont(tvViewNumLayout);

            //textview 로드
            tvViewNum = (TextView)tvViewNumLayout.findViewById(parentView.getContext().getResources().getIdentifier("tv_viewnum", "id", parentView.getContext().getPackageName()));
            tvViewNum.getBackground().setAlpha(179);
            if (_cvm.getSkinType() == COMUtil.SKIN_BLACK){
            	tvViewNum.setTextColor(Color.rgb(255, 255, 255));
            }else {
                tvViewNum.setTextColor(Color.rgb(17, 17, 17));
            }



            //textview 터치 이벤트 처리
            tvViewNum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("dev_kimsh", "봉갯수 텍스트뷰 터치됨");
                    ViewNumTextViewController viewNumDialog = new ViewNumTextViewController(parentView.getContext(), _cvm, _cdm, parentView);
                    viewNumDialog.show();
                    COMUtil.g_chartDialog = viewNumDialog;
                }
            });

            //현재 뷰갯수를 viewnum에 표시
            tvViewNum.setText(String.valueOf(_cvm.getViewNum()));
        }

        //뷰의 위치 및 크기 조절.
//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int)COMUtil.getPixel(40), (int)COMUtil.getPixel(25));
////        params.leftMargin=(int)(x-margineR-COMUtil.getPixel(38));
////        params.topMargin=(int)y;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int)COMUtil.getPixel(38), (int)COMUtil.getPixel_H(18));
        params.leftMargin=(int)(x-margineR-COMUtil.getPixel(38) - (W_YSCALE-COMUtil.getPixel(38))/2);
        params.topMargin=(int)COMUtil.getPixel_H(4);
        tvViewNumLayout.setLayoutParams(params);

        if(!bStart) {
            return;
        }

        //지표설정창 위로 뷰가 오지 않게 조정
        if(parentView.layout.getChildAt(parentView.layout.getChildCount() - 1) instanceof IndicatorConfigView)
        {
            parentView.layout.addView(tvViewNumLayout, parentView.layout.getChildCount() - 2);
        }
        else
        {
            parentView.layout.addView(tvViewNumLayout);
        }

        if(getBounds().width()<=COMUtil.getPixel(180))
        {
            setHideViewNumTextView(true); //봉갯수 텍스트뷰 숨김.
        }
        else {
            setHideViewNumTextView(false); //봉갯수 텍스트뷰 보임.
        }
    }

    /**
     * YScale 차트 봉갯수(viewNum) TextView 의 보임/숨김 설정 
     * @param bHide : 보임 숨김 여부
     * */
    public void setHideViewNumTextView(final boolean bHide) {
        if(tvViewNumLayout != null)
        {
            if(!bHide && COMUtil.isBongCntShow())
            {
                if(bHide || _cvm.chartType == COMUtil.COMPARE_CHART)	tvViewNumLayout.setVisibility(View.GONE);
                else if(bounds != null && getBounds().width()<=COMUtil.getPixel(180))
                {
                    tvViewNumLayout.setVisibility(View.GONE);
                }
                else		tvViewNumLayout.setVisibility(View.VISIBLE);
            }
            else if(!bHide && _cvm.m_bFXChart)
                tvViewNumLayout.setVisibility(View.VISIBLE);
            else
            {
                tvViewNumLayout.setVisibility(View.GONE);
            }
        }
    }
    //2014. 3. 20 Y축 봉갯수 누르면 봉입력창 뜨고 봉갯수변경<<
    public void disableBlock(View v) {
        if(graphs==null) {
            return;
        }
        COMUtil._chartMain.runOnUiThread(new Runnable() {
            public void run() {
                AbstractGraph ag = (AbstractGraph)graphs.get(0);

                //2015. 1. 13 by lyk - 동일지표인 경우 타이틀 처리
                String strGraphTitle = ag.getName();
                if(ag.getGraphTitle()!=null && ag.getGraphTitle().contains(COMUtil.JIPYO_ADD_REMARK)) {
                    strGraphTitle = ag.getGraphTitle();
                }

                //2017. 7. 10 by hyh - Bollinger Band 보조지표에 추가. 삭제 안되는 에러 수정 >>
                if(ag.getGraphTitle().startsWith("Bollinger Band [보조]")) {
                    strGraphTitle = ag.getGraphTitle();
                }
                //2017. 7. 10 by hyh - Bollinger Band 보조지표에 추가. 삭제 안되는 에러 수정 <<

                (parentView).closeButtonClicked(strGraphTitle);
//        		(parentView).closeButtonClicked(ag.getName());
                //2015. 1. 13 by lyk - 동일지표인 경우 타이틀 처리 end
            }
        });
    }
    public void setGraphDataTitle(int nIndex) {
        int title_len=0;
        AbstractGraph g;
        int len = graphs.size();
        for(int i=0; i<len; i++) {
            g=(AbstractGraph)graphs.get(i);
            g.setGraphDataTitle(this.getX()+margineL+LEFT_MARGIN_TITLE+title_len, this.getY(), nIndex);
            title_len+=g.getGraphTitleBounds().width();
        }
    }
    public void setHideDelButton(final boolean bHide) {
        COMUtil._chartMain.runOnUiThread(new Runnable() {
            public void run() {
                if(delButtonLayout != null)
                {
                    if(bHide)
                        delButtonLayout.setVisibility(View.GONE);
                    else
                        delButtonLayout.setVisibility(View.VISIBLE);
                }

                if(bounds != null && getBounds().width()<=COMUtil.getPixel(180))
                {
                    setHideChangeBlockButton(true); //블럭이동버튼 숨김.
                }
                else {
                    setHideChangeBlockButton(bHide);
                }

                //2012. 7. 10 보조지표 타이틀 숨기기/보이기
                AbstractGraph g;
                for(int i = 0; i < graphs.size(); i++)
                {
                    g = graphs.get(i);
                    g.setHideTitleButton(bHide);
                }
            }
        });

    }
    public void setHideChangeBlockButton(final boolean bHide) {
//    	COMUtil._chartMain.runOnUiThread(new Runnable() {
//    		public void run() {
        if(changeBlockButtonLayout != null)
        {
            if(bHide ||  _cvm.chartType == COMUtil.COMPARE_CHART)
                changeBlockButtonLayout.setVisibility(View.GONE);
            else
                changeBlockButtonLayout.setVisibility(View.VISIBLE);
        }
//    		}
//    	});

    }
    public void addCompGraph(String strCode, String strName) {
        if(graphs.size()<1) {
            return;
        }
        AbstractGraph graph = (AbstractGraph)graphs.get(0);
        int nCnt = graph.getDrawTool().size();

        DrawTool dt = new LineDraw(_cvm, _cdm);
        dt.setShowZeroValue(true);
        dt.setViewTitle(strName);
        //2016. 1. 29 by hyh - 비교차트 컬러 수정
        dt.setProperties(1, 0, strCode, CoSys.COMPARE_CHART_COLORS[nCnt+1]);
//    	dt.setLineT(2);
        graph.add(dt);
        graph.setBounds(graph_bounds.left, graph_bounds.top, graph_bounds.right, graph_bounds.bottom);
    }

    public void delCompGraph(String strName) {
        if(graphs.size()<1) {
            return;
        }
        AbstractGraph graph = (AbstractGraph)graphs.get(0);
//    	int nCnt = graph.getDrawTool().size();
        Vector<DrawTool> arrDrawTool = graph.getDrawTool();
        for(int i=0; i<arrDrawTool.size(); i++) {
            DrawTool dt = (DrawTool)arrDrawTool.get(i);
            if(dt.getPacketTitle().equals(strName)) {
                String strRate = strName+"_1";
                _cdm.removePacket(strRate);
                _cdm.removePacket(strName);
                arrDrawTool.remove(dt);
            }
        }
    }

    public void clearCompareData(String strName) {
        if(graphs.size()<1) {
            return;
        }
        AbstractGraph graph = (AbstractGraph)graphs.get(0);
        Vector<DrawTool> arrDrawTool = graph.getDrawTool();
        for(int i=0; i<arrDrawTool.size(); i++) {
            DrawTool dt = (DrawTool)arrDrawTool.get(i);
            if(dt.getPacketTitle().equals(strName)) {
                String strRate = strName+"_1";
                _cdm.removePacket(strRate);
                _cdm.removePacket(strName);
            }
        }
        graph.reFormulateData();
    }

    public void setSkinType(int nSkinType)
    {
        for(int i=0; i<scale.length; i++) {
            //[[scale objectAtIndex:i] setLineColor:[_cvm CSL]];
            scale[i].setTextColor(_cvm.CST);
            //[[scale objectAtIndex:i] setBackColor:[_cvm getBackColor]];
        }
//        if(delButton != null)
//        {
////	        if(nSkinType == COMUtil.SKIN_BLACK)
////	        {
////	        	int layoutResId = this.parentView.getContext().getResources().getIdentifier("close", "drawable", this.parentView.getContext().getPackageName());
////	        	delButton.setBackgroundResource(layoutResId);
////	        	delButton.setWidth((int)COMUtil.getPixel(23));
////	        	delButton.setHeight((int)COMUtil.getPixel(23));
////	        }
////	        else
////	        {
//	        	int layoutResId = this.parentView.getContext().getResources().getIdentifier("close_white", "drawable", this.parentView.getContext().getPackageName());
////	        	delButton.setBackgroundResource(layoutResId);
//	        	Bitmap image = BitmapFactory.decodeResource(this.parentView.getContext().getResources(), layoutResId);	
//	        	Drawable drawable = (Drawable)(new BitmapDrawable(image));
//	        	drawable.setAlpha(80);
//	        	delButton.setBackgroundDrawable(drawable);
//	        	delButton.setWidth((int)COMUtil.getPixel(13));
//	        	delButton.setHeight((int)COMUtil.getPixel(13));
////	        }
//        }
    }
    //2012. 7. 26 (아이폰) 라이브러리 변경 및 delButton 액션 처리.   적용  
    public void handleDelTap()
    {
        //CGPoint p = [tgs locationInView:tgs.view];
        disableBlock(delButton);
        disableBlock(delButtonLayout);
    }

    public void setVisible(String strName, boolean bVisible) {
        for(int i=0; i<graphs.size(); i++)
        {
            AbstractGraph graph = (AbstractGraph)graphs.get(i);
            Vector<DrawTool> arrDrawTool = graph.getDrawTool();
            for(int j=0; j<arrDrawTool.size(); j++) {
                DrawTool dt = (DrawTool)arrDrawTool.get(j);
                if(dt.getPacketTitle().equals(strName))
                    dt.setVisible(bVisible);
            }
        }
    }

    public boolean getVisible(String strName) {
        boolean rtnVal = false;
        for(int i=0; i<graphs.size(); i++) {
            AbstractGraph graph = (AbstractGraph)graphs.get(i);
            Vector<DrawTool> arrDrawTool = graph.getDrawTool();
            for(int j=0; j<arrDrawTool.size(); j++) {
                DrawTool dt = (DrawTool)arrDrawTool.get(j);
                if(dt.getPacketTitle().equals(strName)) {
                    rtnVal = dt.isVisible();
                    break;
                }
            }
        }

        return rtnVal;
    }

    public void setPaddingRight(int nPaddingRight)
    {
        m_nPaddingRight = (int)COMUtil.getPixel(nPaddingRight);
    }

    //2019. 03. 12 by hyh - 투자자 업종 가로 바차트 추가 >>
    public void add_userGraphBar_UpDownBong(String title, int graphtype, String[] datakind, int lineT, int lineCIndex) {
        AbstractGraph graph = new PriceGraph(_cvm, _cdm);
        graph.setProperties(graphtype, title, ChartUtil.PLINE);
        graph.setDatakind(datakind);

        DrawTool dt = new BarDraw(_cvm, _cdm);
        dt.setProperties(2, 7, title, CoSys.CHART_COLORS[lineCIndex]); //graphType 타입 7로 설정
        dt.setDownColor(CoSys.CHART_COLORS[6]);
        graph.add(dt);
        //0:그래프 타입 1:그래프 스타일 2: 타이틀 3 색
//        dt.setProperties(2,2,title, CoSys.CHART_COLORS[4]);
        //graph.add(dt);
        //graph.setParent(this);
        add(graph);
    }

    public void add_userGraphBar_UpDown(String title, int graphtype, String[] datakind, int lineT, int lineCIndex) {
        AbstractGraph graph = new PriceGraph(_cvm, _cdm);
        graph.setProperties(graphtype, title, ChartUtil.PLINE);
        graph.setDatakind(datakind);

        DrawTool dt = new BarDraw(_cvm, _cdm);
        dt.setProperties(2, 3, title, CoSys.CHART_COLORS[lineCIndex]);
        dt.setDownColor(CoSys.CHART_COLORS[6]);
        graph.add(dt);
        //0:그래프 타입 1:그래프 스타일 2: 타이틀 3 색
//        dt.setProperties(2,2,title, CoSys.CHART_COLORS[4]);
        //graph.add(dt);
        //graph.setParent(this);
        add(graph);
    }
    //2019. 03. 12 by hyh - 투자자 업종 가로 바차트 추가 <<

    //2019. 01. 12 by hyh - 블록병합 처리 >>
    public void resetMergedGraphs() {
        Vector<String> arrMergedGraphTitles = new Vector<String>();
        Vector<String> arrMergedGraphValues = new Vector<String>();
        Vector<AbstractGraph> graphs = getGraphs();

        for (int nGraphIndex=0; nGraphIndex < graphs.size(); nGraphIndex++) {
            AbstractGraph ag = graphs.get(nGraphIndex);
            String strTitle = ag.getName();

            //중복지표 병합처리
            if (ag.getGraphTitle() != null && ag.getGraphTitle().contains(COMUtil.JIPYO_ADD_REMARK)) {
                strTitle = ag.getGraphTitle();
            }

            String strValues = parentView.getGraphValue2(ag, true);

            arrMergedGraphTitles.add(strTitle);
            arrMergedGraphValues.add(strValues);
        }

        if (arrMergedGraphTitles.size() > 0) {
            this.arrMergedGraphTitles = arrMergedGraphTitles;
            this.arrMergedGraphValues = arrMergedGraphValues;
        }
        else {
            this.arrMergedGraphTitles = null;
            this.arrMergedGraphValues = null;
        }
    }
    //2019. 01. 12 by hyh - 블록병합 처리 <<
    public void add_userGraphMountain(String title, int graphtype,String[] datakind,int lineT,int lineCIndex){
        AbstractGraph graph = new PriceGraph(_cvm,_cdm);
        graph.setProperties(graphtype,title,ChartUtil.PLINE);
        graph.setDatakind(datakind);

        DrawTool dt = new LineDraw(_cvm,_cdm);
        //0:그래프 타입 1:그래프 스타일 2: 타이틀 3 색
        dt.setProperties(1,8,title, CoSys.CHART_COLORS[lineCIndex]);
        //0데이터를 보여줄 것인지 설정
        dt.setShowZeroValue(true);
        dt.setLineT(lineT);
        graph.add(dt);
        graph.setParent(this);
        add(graph);
    }
    //2019.08.12 by LYH >> 업종 그리드 바차트 추가 Start
    public void add_userGraphBar_UpDownGrid(String title, int graphtype,String[] datakind,int
            lineT ,int lineCIndex){
        AbstractGraph graph = new PriceGraph(_cvm,_cdm);
        graph.setProperties(graphtype,title,ChartUtil.PLINE);
        graph.setDatakind(datakind);

        DrawTool dt = new BarDraw(_cvm,_cdm);
        //0:그래프 타입 1:그래프 스타일 2: 타이틀 3 색
        dt.setProperties(2,8,title, CoSys.CHART_COLORS[lineCIndex]); //graphType 타입 8로 설정
        dt.setDownColor(CoSys.CHART_COLORS[6]);
        graph.add(dt);
        add(graph);
    }

    //2019. 03. 12 by hyh - 투자자 업종 가로 바차트 추가 >>
    public void add_userGraphBar_FutOptGrid(String title, int graphtype, String[] datakind, int lineT, int lineCIndex) {
        AbstractGraph graph = new PriceGraph(_cvm, _cdm);
        graph.setProperties(graphtype, title, ChartUtil.PLINE);
        graph.setDatakind(datakind);

        DrawTool dt = new BarDraw(_cvm, _cdm);
        dt.setProperties(2, 9, title, CoSys.CHART_COLORS[lineCIndex]); //graphType 타입 7로 설정
        dt.setDownColor(CoSys.CHART_COLORS[6]);
        graph.add(dt);
        //0:그래프 타입 1:그래프 스타일 2: 타이틀 3 색
//        dt.setProperties(2,2,title, CoSys.CHART_COLORS[4]);
        //graph.add(dt);
        //graph.setParent(this);
        add(graph);
    }

    //2016.05.16 by LYH >> 1분선차트 애니메이션 추가.
    public void setInitMode(boolean bInit) {
        for(int i=0; i<graphs.size(); i++)
        {
            AbstractGraph graph = (AbstractGraph)graphs.get(i);
            Vector<DrawTool> arrDrawTool = graph.getDrawTool();
            for(int j=0; j<arrDrawTool.size(); j++) {
                DrawTool dt = (DrawTool)arrDrawTool.get(j);
                dt.setInitMode(bInit);
            }
        }
    }
    //2016.05.16 by LYH << 1분선차트 애니메이션 추가.
}
