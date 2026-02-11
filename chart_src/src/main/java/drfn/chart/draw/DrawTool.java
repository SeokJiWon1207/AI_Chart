package drfn.chart.draw;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.view.animation.AnimationUtils;

import java.util.ArrayList;

import drfn.chart.base.Dynamics;
import drfn.chart.comp.Text_View;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;
import drfn.chart.scale.AREA;
import drfn.chart.util.COMUtil;
import drfn.chart.util.ChartUtil;
import drfn.chart.util.CoSys;
import drfn.chart.util.MinMax;

import static drfn.chart.model.ChartViewModel.FX_BUYSELL;
//import drfn.chart.base.IndicatorConfigView;

public abstract class DrawTool{
    //2017.06.12 by LYH >> 전략 강약 중복 추가 기능 처리
    public static int g_StrongWeekCount = 0;
    public static int g_StrongWeekIndex = 0;
    //2017.06.12 by LYH >> 전략 강약 중복 추가 기능 처리 end
    public final int def_up=Color.RED;        //상승색
    public final int def_down=Color.BLUE;     //하락색
    public final int def_same=Color.GREEN;    //보합색

    int drawType1;                              //그래프형(봉,선...)
    int drawType2;                              //각각의 drawType1에 대한 세부타입

    int[] upColor = CoSys.CHART_COLORS[0];       //상승색
    int[] upColor2 = CoSys.CHART_COLORS[0];  //양봉(하락)색
    int[] downColor = CoSys.CHART_COLORS[1];     //하락색
    int[] downColor2 = CoSys.CHART_COLORS[1];  //음봉(상)색
    int[] sameColor = CoSys.CHART_COLORS[2];              //보합색
    int[] def_upColor = upColor;
    int[] standVolColor = CoSys.stand_vol_color;
    int[] standVolTextColor = CoSys.stand_vol_text_color;

    double[] data = null;
    boolean selected;                           //DrawTool선택상태
    boolean show_zero_val=true;                 //보통의 지표는 참이지만, 추세지표는 false이다
    public float xfactor;
    public float yfactor;
    double min_data;
    double max_data;
    public float min_view;
    public float max_view;
    public float min_view_width;
    public float max_view_width;
    double[] bong_minmax;//봉인경우의 minmax

    public String title, subTitle, viewTitle;  //타이틀(_cdm의 key가 된다) viewTitle:화면에 보이는 타이틀
    private boolean isVisible =true;
    private boolean fillCloud = true;  //2021.05.24 by hhk - 구름대 채우기
    private boolean fillUp = true;
    private boolean fillUp2 = true;
    private boolean fillDown = true;
    private boolean fillDown2 = true;
    boolean inverse;                            //지표 역으로 그리기 지원을 위해 만든 변수, 현재는 사용하지 않음
    private RectF bounds;
    RectF title_bound = new RectF();                      //타이틀 영역
    public int[] back;
    public int[] line;

    public int[] stand = new int[3];
    protected int line_thick=0;                 //라인의 굵기
    ChartViewModel _cvm;
    ChartDataModel _cdm;
    public boolean showDataTitle = false;

    public boolean bMarketData = false;
    //2012. 7. 4  한글라벨타이틀 추가
    //Context context = null;
    //Text_View lbTitle = null;

    //2013. 9. 5 매물대설정 값 문자열 보이기 여부 처리>>
    protected boolean bStandScaleLabelShow = false;
    //2013. 9. 5 매물대설정 값 문자열 보이기 여부 처리>>

    public int m_nDataType = 0;
    public int m_nAverageCalcType = 0;

    public Rect chart_bounds = new Rect();

    //2017.05.11 by LYH >> 전략(신호, 강약) 추가
    int[] def_downColor;
    boolean isUpVisible = true;
    boolean isDownVisible = true;
    protected int m_nDownThick=1;
    //2017.05.11 by LYH >> 전략(신호, 강약) 추가 end



    public DrawTool(ChartViewModel cvm, ChartDataModel cdm){
        _cvm = cvm;
        _cdm = cdm;
        //2012. 7. 4  한글라벨타이틀 추가
        //context = COMUtil._chartMain;
    }
    public void setShowZeroValue(boolean b){
        this.show_zero_val = b;
    }
    
    public void setBMarketData(boolean b){
        this.bMarketData = b;
    }

    public boolean getShowZeroValue(){
        return this.show_zero_val;
    }
    //0:그래프 타입 1:그래프 스타일 2: 타이틀 3 색
    public void setProperties(int type1, int type2, String title, int[] lineColor){
        drawType1 = type1;
        drawType2 = type2;
        this.title = subTitle = title;

        if(!_cvm.bInvestorChart) {
            //2015. 1. 13 by lyk - 동일지표 이름을 화면에 보여줄때는 COMUtil.JIPYO_ADD_REMARK 이전까지 이름을 사용한다.
            subTitle = COMUtil.getAddJipyoTitle(this.title);
            //2015. 1. 13 by lyk - 동일지표 이름을 화면에 보여줄때는 COMUtil.JIPYO_ADD_REMARK 이전까지 이름을 사용한다. end

            //2021.06.18 by lyk - kakaopay - "/" 들어간 지표명 잘리는 오류 수정 >>
//            if (!_cvm.bInvestorChart) {
//                if (title.indexOf("/") >= 0)
//                    subTitle = new String(title.substring(title.indexOf("/") + 1));
//            }
//            this.subTitle = COMUtil.jipyoNameToEng(subTitle);
            //2021.06.18 by lyk - kakaopay - "/" 들어간 지표명 잘리는 오류 수정 <<
        }

        //2021.08.02 by lyk - kakaopay - 기본거래량 -> 거래량으로 타이틀 변환
        if(subTitle.equals("기본거래량")) {
            subTitle = "거래량";
        }

        upColor = lineColor;
        def_upColor = upColor;
        def_downColor = downColor;  //2017.05.11 by LYH >> 전략(신호, 강약) 추가
    }

    //0:그래프 타입 1:그래프 스타일 2: 타이틀 3: 상승색 4:하락색 5:보합색
    //6:상승채움 7:하락채움 8:강조
    public void setProperties(String[] s){
        drawType1=Integer.parseInt(s[0]);
        drawType2=Integer.parseInt(s[1]);
        title = subTitle = s[2];
        if(title.indexOf("/")>=0) subTitle = new String(title.substring(title.indexOf("/")+1));
        this.subTitle = COMUtil.jipyoNameToEng(subTitle);
        upColor = CoSys.RED;
        downColor = CoSys.BLUE;
        sameColor = CoSys.GREEN;
        fillUp=(s[6].equals("0"))?false:true;
        fillDown=(s[7].equals("0"))?false:true;
    }


    public void reSet(){
    }
    //=====================================
    // 최종 그림을 그릴 영역을 정한다
    //=====================================
    public void setBounds(float sx, float sy, float right, float bottom){
        if(_cvm.bStandardLine && getTitle().equals("기본거래량") && _cvm.getBounds() != null)
        {
            bottom += _cvm.BMargin_B;
        }
        bounds = new RectF(sx,sy,right,bottom);
        max_view = bottom;
        min_view = sy;
        max_view_width = right;
        min_view_width = sx;

        //2013.07.31 >> 기준선 라인 차트 타입 추가
        if(_cvm.bStandardLine && getTitle().equals("기본거래량"))
        {
            min_view = sy + (int)((max_view-min_view)*0.2);
        }
        //2013.07.31 <<
    }
    public void setBounds(RectF bounds){
        if(bounds==null)return;
        this.bounds = bounds;
        max_view = bounds.bottom;
        min_view = bounds.top;
        max_view_width = bounds.right;
        min_view_width = bounds.left;
    }
    public RectF getBounds(){
        return bounds;
    }
    //=====================================
    // 최소,최대값을 set한다
    //=====================================
    public void setMinMax(double[] mm_data){
        if(log){
            if(mm_data[0]==0)
                min_data = mm_data[0];
            else
                min_data = (Math.log(Math.abs(mm_data[0]))*1000);
            if(mm_data[1]==0)
                max_data = mm_data[1];
            else
                max_data = (Math.log(Math.abs(mm_data[1]))*1000);
        }else{
            min_data = mm_data[0];
            max_data = mm_data[1];
        }
    }
    //봉 데이터의 최대/최소값
    public void setBongMinMax(double[] mm_data){
        bong_minmax=null;
        this.bong_minmax = mm_data;
    }
    public double getBongMin(){
        if(bong_minmax==null) return -1;
        return bong_minmax[0];
    }
    public double getBongMax(){
        if(bong_minmax==null) return -1;
        return bong_minmax[1];
    }
    //=====================================
    // 간격을 정한다, 폭의 여유가 있는 경우, 없는경우....
    //=====================================
    boolean draw_without_gab=false;
    boolean draw_without_w=false;
    public float xw=1.0f;
    public void setDrawGab(){
        _cvm.setDataWidth(xfactor);
        //2020. 06. 08 by hyh - 우측으로 쏠린 머리, 꼬리 위치 가운데로 조정 >>
        xw=((xfactor-COMUtil.getPixel(1))/2);
        //xw=((xfactor-1)/2);
        //2020. 06. 08 by hyh - 우측으로 쏠린 머리, 꼬리 위치 가운데로 조정 <<
        //xw = (int)(xfactor/2)-1;

        if(COMUtil.getPixel(1)>=3)
        {
            if(xw<2) {
                xw=2.0f;
            }
        }
        else
        {
            if(xw<1) {
                xw=1.0f;
            }
        }
    }

    Rect tmpRect = new Rect();

    public void drawTitle(Canvas gl) {
        //_cvm.drawRect(gl, (int)bounds.left,(int)bounds.top,bounds.width(),bounds.height(), CoSys.UP_LINE_COLORS);
        if (!_cvm.bIsShowTitle || _cvm.bIsTodayLineChart) { //2020.04.14 당일 라인차트 추가 - hjw
            return;
        }

//        if(_cvm.chartType == COMUtil.COMPARE_CHART || _cvm.bIsLineFillChart || _cvm.bIsLineChart || _cvm.bIsMiniBongChart) {
//            return;
//        }
        if (_cvm.bIsLineFillChart || _cvm.bIsLineChart || _cvm.bIsMiniBongChart || _cvm.bIsLine2Chart) {
            //2019. 04. 01 by hyh - 차트 타이틀 적용 >>
            if (!_cvm.strChartTitle.equals("")) {
                viewTitle = _cvm.strChartTitle;
            }
            //2019. 04. 01 by hyh - 차트 타이틀 적용 <<
            else {
                return;
            }
        }
        RectF bound = getTitleBounds();
        //_cvm.drawRect(gl, (int)bound.left,(int)bound.top,bound.width(),bound.height(), CoSys.DOWN_LINE_COLORS);
        int[] col = null;
        if (bound == null) return;//타이틀 영역이 잡혀 있지 않으므로 그리지 않는다
        //2012. 7. 23  선택했을때 title 왼쪽 네모모양 하얀색으로 되는 것 주석처리 
//        if(selected){
        //2012. 7. 5  텍스트뷰로 타이틀 표시로 타이틀 draw하는 부분 주석 처리  	
//        	_cvm.drawFillRect(gl, bound.left,bound.top+(int)COMUtil.getPixel(5),bound.width(),bound.top+bound.height()+(int)COMUtil.getPixel(2), upColor, 0.5f);
//        	col=CoSys.WHITE;
//        }else{
        //2013.07.31 >> 기준선 라인 차트 타입 추가
        //if (_cvm.bStandardLine && getTitle().equals("기본거래량")) {
        if (getTitle().equals("기본거래량")) {
//            col = CoSys.TEXT_GREY0;
            col = CoSys.GREY990;
        } else if (getTitle().equals("매물대")) {
            col = CoSys.stand_vol_text_color;
        } else {
            col = upColor;
        }

        //2019. 04. 01 by hyh - 차트 타이틀 적용 >>
        if (!_cvm.strChartTitleColor.equals("")) {
            String[] arrColor = _cvm.strChartTitleColor.split(";");

            try {
                int nRed = Integer.parseInt(arrColor[0]);
                int nGreen = Integer.parseInt(arrColor[1]);
                int nBlue = Integer.parseInt(arrColor[2]);

                col = new int[] {nRed, nGreen, nBlue};
            }
            catch (Exception e) {
                col = upColor;
            }
        }
        //2019. 04. 01 by hyh - 차트 타이틀 적용 <<

        //2019. 04. 08 by hyh - FX마진 데이터 조회 및 실시간 처리 >>
        if(_cvm.nFxMarginType == FX_BUYSELL) {
            if (viewTitle != null && viewTitle.equals("매도")) {
                col = downColor;
            }
        }
        //2019. 04. 08 by hyh - FX마진 데이터 조회 및 실시간 처리 <<

        float fontSize = COMUtil.nFontSize_paint;

        //2020.12.28 by HJW - 폰트 사이즈 옵션 추가 >>
        if(_cvm.g_nFontSizeBtn == 0)
            fontSize = COMUtil.nFontSize_paint-COMUtil.getPixel(2);
        else if(_cvm.g_nFontSizeBtn == 2)
            fontSize = COMUtil.nFontSize_paint+COMUtil.getPixel(2);
        //2020.12.28 by HJW - 폰트 사이즈 옵션 추가 <<

        //2021.07.14 by hanjun.Kim - kakaopay - title글자 색상 일괄 적용
        int textColor[] = CoSys.GREY990;

        if (viewTitle != null) {
            if (viewTitle.length() > 0) {//기본차트 영역(이동평균, 차트형태별 타이틀).
                if (viewTitle.contains("삼선전환도")) {
                    String strTitle = "";
                    double dPrice;
                    double[] closeData = _cdm.getSubPacketData("variable" + "_close");
                    double[] openData = _cdm.getSubPacketData("삼선전환도_open");
                    double[] highData = _cdm.getSubPacketData("삼선전환도_high");
                    int nIndexUp = 0;
                    int nIndexDown = 0;
                    if (closeData == null)
                        return;
                    for (int i = closeData.length - 1; i > 0; i--) {
                        if (closeData[i] > openData[i]) {
                            if (nIndexDown > 0) {
                                strTitle = _cdm.getFormatData("삼선전환도_high", i);
                                break;
                            }
                            nIndexUp++;
                        }
                        else {
                            if (nIndexUp > 0) {
                                strTitle = _cdm.getFormatData("삼선전환도_open", i);
                                break;
                            }
                            nIndexDown++;
                        }

                        if (nIndexUp > 2) {
                            strTitle = _cdm.getFormatData("삼선전환도_open", i);
                            break;
                        }
                        if (nIndexDown > 2) {
                            strTitle = _cdm.getFormatData("삼선전환도_high", i);
                            break;
                        }
                    }
                    strTitle = "삼선전환도(3칸, " + strTitle + ")";
//                    _cvm.drawFillRect(gl, bound.left, bound.top + (int) COMUtil.getPixel(8), (int) COMUtil.getPixel(6), (int) COMUtil.getPixel(2), col, 1.0f);
                    _cvm.drawCircle(gl,bound.left - COMUtil.getPixel(2) ,bound.top - COMUtil.getPixel(2) ,bound.left +COMUtil.getPixel(4) ,bound.top+ COMUtil.getPixel(4), true, col );

//       			 _cvm.drawFillRect(gl, bound.left+1,bound.top+(int)COMUtil.getPixel(7),(int)COMUtil.getPixel(6) ,(int)COMUtil.getPixel(6),col,1.0f);
                    _cvm.drawString(gl, textColor, bound.left + (int) COMUtil.getPixel(9), bound.top + (int) COMUtil.getPixel(9), strTitle, 0.48f);
                    return;
                }
                if (!showDataTitle || _cvm.isStandGraph()) {
//            		//2012. 7. 16  십자선데이터 패널 버튼을 누르지 않은 상태일때  보조지표의 viewTitle, subTitle 의 텍스트컬러 설정 
//            		COMUtil._chartMain.runOnUiThread(new Runnable() {
//                        public void run() {	
//                    		lbTitle.setColor(Color.rgb(upColor[0], upColor[1], upColor[2]));
//                        }});
////            		_cvm.drawFillRect(gl, bound.left+1,bound.top+(int)COMUtil.getPixel(5),(int)COMUtil.getPixel(6) ,(int)COMUtil.getPixel(6),col,1.0f);
//            		//2012. 8. 3 타이틀 왼쪽옆 네모박스의 y축 위치조절 
                    if (title_bound.left + title_bound.width() - COMUtil.getPixel(10) < bounds.left + bounds.width() || _cvm.bInvestorChart) {

                        if(viewTitle != null && viewTitle.equals("외국인/기관/개인 추세")) {
                            viewTitle = "외국인";
                        }

                        if (viewTitle.equals("가격")) {
                        }
                        else if (getTitle().equals("매수매도거래량")) {
//                            _cvm.drawFillRect(gl, bound.left, bound.top + (int) COMUtil.getPixel(7), (int) COMUtil.getPixel(6), (int) COMUtil.getPixel(6), downColor, 1.0f);
                            _cvm.drawCircle(gl,bound.left - COMUtil.getPixel(2) ,bound.top - COMUtil.getPixel(2) ,bound.left +COMUtil.getPixel(4) ,bound.top+ COMUtil.getPixel(4), true, col );
//                            _cvm.drawString(gl, downColor, bound.left + (int) COMUtil.getPixel(9), bound.top + (int) COMUtil.getPixel(9), "매도거래량");
//                            _cvm.drawString(gl, upColor, bound.right - (int) COMUtil.getPixel(9), bound.top + (int) COMUtil.getPixel(9), "매수거래량");
                            _cvm.drawString(gl, textColor, bound.left + (int) COMUtil.getPixel(9), bound.top + (int) COMUtil.getPixel(9), "매도거래량", 0.48f);
                            _cvm.drawString(gl, textColor, bound.right - (int) COMUtil.getPixel(9), bound.top + (int) COMUtil.getPixel(9), "매수거래량", 0.48f);
                        }
                        else {
//                            _cvm.drawFillRect(gl, bound.left,bound.top+(int)COMUtil.getPixel(7),(int)COMUtil.getPixel(6) ,(int)COMUtil.getPixel(6),col,1.0f);
//                            _cvm.drawString(gl, col, bound.left+(int)COMUtil.getPixel(9),bound.top+(int)COMUtil.getPixel(9), viewTitle);

                            // 2021.10.27 by JHY - 이격도 간소화 >>
                            if (getTitle().startsWith("이격률(지수)"))
                                _cvm.drawString(gl, textColor, bound.left - (int) COMUtil.getPixel(0), bound.top , "이격률(지수)", 0.48f);
                            else if(getTitle().startsWith("이격도1"))
                                _cvm.drawString(gl, textColor, bound.left - (int) COMUtil.getPixel(0), bound.top + (int) COMUtil.getPixel(1) , "이격도", 0.48f);
                            // 2021.10.27 by JHY - 이격도 간소화 <<

                            if((!viewTitle.contains("가격") || _cvm.bInvestorChart) && !viewTitle.equals("거래량"))
                            {
//                                _cvm.drawFillRect(gl, bound.left, bound.top + (int) COMUtil.getPixel_H(0), COMUtil.getPixel_H(5), COMUtil.getPixel_H(5), col, 1.0f);
                                //2021.05.20 by hanjun.Kim - kakaopay - 좌상단 이평아이콘 변경 >>
                                //2021.05.20 by hanjun.Kim - kakaopay - 기본거래량때는 아이콘없이

                                // 2021.10.27 by JHY - 이격도 간소화 >>
                                if (getTitle().startsWith("이격률"))
                                    _cvm.drawCircle(gl, bound.left + COMUtil.getPixel(60), bound.top - COMUtil.getPixel(2), bound.left + COMUtil.getPixel(66), bound.top + COMUtil.getPixel(4), true, col);
                                else if(getTitle().startsWith("이격도"))
                                    _cvm.drawCircle(gl, bound.left + COMUtil.getPixel(35), bound.top - COMUtil.getPixel(2), bound.left + COMUtil.getPixel(41), bound.top + COMUtil.getPixel(4), true, col);
                                // 2021.10.27 by JHY - 이격도 간소화 <<
                                // 2021.11.24 by JHY - 렌코이평 문구수정 >>
                                else if (getTitle().equals("렌코이평10")) {
                                    _cvm.drawCircle(gl, bound.left - COMUtil.getPixel(35), bound.top - COMUtil.getPixel(2), bound.left - COMUtil.getPixel(29), bound.top + COMUtil.getPixel(4), true, col);
                                }
                                else if (getTitle().equals("렌코이평20")) {
                                    _cvm.drawCircle(gl, bound.left - COMUtil.getPixel(71), bound.top - COMUtil.getPixel(2), bound.left - COMUtil.getPixel(65), bound.top + COMUtil.getPixel(4), true, col);
                                }
                                // 2021.11.24 by JHY - 렌코이평 문구수정 <<
                                else if (!getTitle().equals("기본거래량")) {
                                    _cvm.drawCircle(gl, bound.left - COMUtil.getPixel(2), bound.top - COMUtil.getPixel(2), bound.left + COMUtil.getPixel(4), bound.top + COMUtil.getPixel(4), true, col);
                                }

//                                if(_cvm.chartType == COMUtil.COMPARE_CHART)
//                                    //_cvm.drawCircle(gl,bound.left,bound.top +(int)COMUtil.getPixel(4) ,bound.left +COMUtil.getPixel(4) ,bound.top+ COMUtil.getPixel(8), true, col );
//                                    _cvm.drawFillRect(gl, bound.left, bound.top + (int) COMUtil.getPixel_H(0), COMUtil.getPixel_H(5), COMUtil.getPixel_H(5), col, 1.0f);
//                                else {
//                                    if(_cvm.getAssetType() == ChartViewModel.ASSET_LINE_MOUNTAIN) {
//                                        if(viewTitle.contains("수익률")) {
//                                            _cvm.drawFillRect(gl, bound.left, bound.top + (int) COMUtil.getPixel_H(4), COMUtil.getPixel_W(8), COMUtil.getPixel_H(2), col, 1.0f);
//                                        } else if(viewTitle.contains("펀드규모")) {
//                                            _cvm.drawFillRect(gl, bound.left+ (int) COMUtil.getPixel_W(10), bound.top + (int) COMUtil.getPixel_H(3), COMUtil.getPixel_W(6), COMUtil.getPixel_H(6), col, 1.0f);
//                                        } else {
//
//                                        }
//                                    } else {
////                                        if(_cvm.bInvestorChart)
////                                            //_cvm.drawCircle(gl,bound.left,bound.top +(int)COMUtil.getPixel(4) ,bound.left +COMUtil.getPixel(4) ,bound.top+ COMUtil.getPixel(8), true, col );
////                                            _cvm.drawFillRect(gl, bound.left, bound.top + (int) COMUtil.getPixel_H(4), COMUtil.getPixel_H(5), COMUtil.getPixel_H(5), col, 1.0f);
////                                        else
//////                                            _cvm.drawCircle(gl, bound.left, bound.top + (int) COMUtil.getPixel(4), bound.left + COMUtil.getPixel(9), bound.top + COMUtil.getPixel(13), true, col);
////                                            //_cvm.drawCircle(gl, bound.left, bound.top + (int) COMUtil.getPixel(1), bound.left + COMUtil.getPixel(8), bound.top + COMUtil.getPixel(9), true, col);
//                                            _cvm.drawFillRect(gl, bound.left, bound.top + (int) COMUtil.getPixel_H(0), COMUtil.getPixel_H(5), COMUtil.getPixel_H(5), col, 1.0f);
//                                    }
//                                }
                            }
                            //2018.06.25 by LYH >> 디자인 가이드 처리 <<
                            if(viewTitle.equals("PnF")) {

//                                _cvm.drawString(gl, col, bound.left+(int)COMUtil.getPixel(13),bound.top+(int)COMUtil.getPixel(9), "P&F");
//                                _cvm.drawString(gl, textColor, bound.left + (int) COMUtil.getPixel(10), bound.top + (int) COMUtil.getPixel(1), "P&F", 0.48f);
                                //2021.11.10 by JHY - 디자인 가이드 처리(주차트명과 동일하게) >>
                                _cvm.drawString(gl, textColor, bound.left + (int) COMUtil.getPixel(10), bound.top + (int) COMUtil.getPixel(1), "포인트 앤 피겨", 0.48f);
                                //2021.11.10 by JHY - 디자인 가이드 처리(주차트명과 동일하게) <<
//                                _cvm.drawString(gl, col, title_bound.left,title_bound.top+COMUtil.getPixel_H(2), "P&F");
                                //2018.06.25 by LYH >> 디자인 가이드 처리 >>
                                //2021.11.10 by JHY - 디자인 가이드 처리(주차트명과 동일하게) >>
                            } else if (viewTitle.equals("Kagi")) {
                                _cvm.drawString(gl, textColor, bound.left + (int) COMUtil.getPixel(10), bound.top + (int) COMUtil.getPixel(1), "카기", 0.48f);
                                //2021.11.10 by JHY - 디자인 가이드 처리(주차트명과 동일하게) <<
                            // 2021.11.24 by JHY - 렌코이평 문구수정 >>
                            } else if (viewTitle.equals("렌코이평5")) {
                                _cvm.drawString(gl, textColor, bound.left + (int) COMUtil.getPixel(10), bound.top + (int) COMUtil.getPixel(1), viewTitle.replaceAll("렌코이평", ""), 0.48f);
                            } else if (viewTitle.equals("렌코이평10")) {
                                _cvm.drawString(gl, textColor, bound.left - (int) COMUtil.getPixel(25), bound.top + (int) COMUtil.getPixel(1), viewTitle.replaceAll("렌코이평", ""), 0.48f);
                            } else if (viewTitle.equals("렌코이평20")) {
                                _cvm.drawString(gl, textColor, bound.left - (int) COMUtil.getPixel(61), bound.top + (int) COMUtil.getPixel(1), viewTitle.replaceAll("렌코이평", ""), 0.48f);
                            // 2021.11.24 by JHY - 렌코이평 문구수정 <<
                            } else{ //if((viewTitle.contains("가격") && !_cvm.bInvestorChart)|| viewTitle.equals("거래량")) {

//                                if(_cvm.getSkinType() != COMUtil.SKIN_BLACK)
//                                    col = new int [] { 0, 0, 0};
//                                else
//                                    col = new int [] { 255, 255, 255};

                                if(_cvm.chartType == COMUtil.COMPARE_CHART) {
//                                    col = new int [] { 136, 136, 136};
                                    //_cvm.drawStringWithSizeFont(gl, col, bound.left + (int) COMUtil.getPixel(10), bound.top + (int) COMUtil.getPixel(5), (int) COMUtil.getPixel(10), viewTitle, COMUtil.typeface);
                                    _cvm.drawStringWithSize(gl, textColor, bound.left + (int) COMUtil.getPixel_W(7), bound.top + (int) COMUtil.getPixel_H(2), fontSize, viewTitle);
                                }
                                else {
                                    if(viewTitle.equals("거래량"))
                                        _cvm.drawStringWithSize(gl, textColor, bound.left - COMUtil.getPixel(3), bound.top + (int) COMUtil.getPixel_H(1), fontSize, viewTitle, 0.48f);
                                    else if (viewTitle.equals("기본거래량"))
                                        _cvm.drawStringWithSize(gl, textColor, bound.left - COMUtil.getPixel(3), bound.top + (int) COMUtil.getPixel_H(1), fontSize, viewTitle, 0.48f);
                                        // 2021.10.27 by JHY - 이격도 간소화 >>
                                    else if(getTitle().startsWith("이격률"))
                                        _cvm.drawStringWithSize(gl, textColor, bound.left + (int) COMUtil.getPixel_W(70), bound.top + (int) COMUtil.getPixel_H(1), fontSize, viewTitle, 0.48f);
                                    else if(getTitle().startsWith("이격도"))
                                        _cvm.drawStringWithSize(gl, textColor, bound.left + (int) COMUtil.getPixel_W(45), bound.top + (int) COMUtil.getPixel_H(1), fontSize, viewTitle, 0.48f);
                                        // 2021.10.27 by JHY - 이격도 간소화 <<
                                    else
                                        _cvm.drawStringWithSize(gl, textColor, bound.left + (int) COMUtil.getPixel_W(8), bound.top + (int) COMUtil.getPixel_H(1), fontSize, viewTitle, 0.48f);
//                                    else
//                                        _cvm.drawStringWithSize(gl, col, bound.left + (int) COMUtil.getPixel_W(7), bound.top + (int) COMUtil.getPixel_H(2), fontSize, viewTitle);

//                                    if(_cvm.getAssetType() == ChartViewModel.ASSET_LINE_MOUNTAIN) {
////                                        col = new int [] { 118, 118, 118};
//                                        if(viewTitle.contains("펀드규모"))
//                                            _cvm.drawStringWithSizeFont(gl, col, bound.left + (int) COMUtil.getPixel(23), bound.top + (int) COMUtil.getPixel(5), (int) COMUtil.getPixel(11), viewTitle, COMUtil.typeface);
//                                        else
//                                            _cvm.drawStringWithSizeFont(gl, col, bound.left + (int) COMUtil.getPixel(13), bound.top + (int) COMUtil.getPixel(5), (int) COMUtil.getPixel(11), viewTitle, COMUtil.typeface);
//                                    } else {
////                                        if(_cvm.bInvestorChart)
////                                        {
//////                                            col = new int [] { 136, 136, 136};
////                                            _cvm.drawStringWithSizeFont(gl, col, bound.left + (int) COMUtil.getPixel(10), bound.top + (int) COMUtil.getPixel(5), (int) COMUtil.getPixel(10), viewTitle, COMUtil.typeface);
////
////                                        }
////                                        else
////                                            _cvm.drawStringWithSize(gl, col, bound.left + (int) COMUtil.getPixel(13), bound.top + (int) COMUtil.getPixel(9), (int) COMUtil.getPixel(12), viewTitle);
//                                        if(viewTitle.equals("거래량"))
//                                            _cvm.drawStringWithSize(gl, col, bound.left, bound.top + (int) COMUtil.getPixel_H(2), fontSize, viewTitle);
//                                        else
//                                            _cvm.drawStringWithSize(gl, col, bound.left + (int) COMUtil.getPixel_W(7), bound.top + (int) COMUtil.getPixel_H(2), fontSize, viewTitle);
//                                    }
                                }
                            }
                            //2018.06.25 by LYH >> 디자인 가이드 처리 <<
//                            else
//                                _cvm.drawString(gl, col, bound.left + (int) COMUtil.getPixel(9), bound.top + (int) COMUtil.getPixel(9), viewTitle);
                        }
                    }
                }
                else { //십자선 툴팁 선택시.
//            	//2012. 7. 16  십자선데이터 패널 버튼을 누르면  보조지표의 viewTitle, subTitle 의 텍스트컬러를 WHITE로 설정 
//            		COMUtil._chartMain.runOnUiThread(new Runnable() {
//                        public void run() {	
//                    		lbTitle.setColor(Color.WHITE);
//                        }});
//            		if(title_bound.left+title_bound.width() < bounds.left+bounds.width())
//                    {
//                    _cvm.drawFillRect(gl, bound.left, bound.top, (int) COMUtil.getPixel_H(5), (int) COMUtil.getPixel_H(5), col, 1.0f);
//                    _cvm.drawCircle(gl,bound.left - COMUtil.getPixel(2) ,bound.top - COMUtil.getPixel(2) ,bound.left +COMUtil.getPixel(4) ,bound.top+ COMUtil.getPixel(4), true, col );
//                    _cvm.drawFillRect(gl, bound.left + (int) COMUtil.getPixel_W(7), bound.top - (int) COMUtil.getPixel_H(4), bound.width() - (int) COMUtil.getPixel_W(8), (int) COMUtil.getPixel(14), CoSys.VIEWTITLE_BG, 0.65f);
                    //2015. 1. 13 by lyk - 동일지표 이름을 화면에 보여줄때는 COMUtil.JIPYO_ADD_REMARK 이전까지 이름을 사용한다.
//                    int textColor[] = {50, 50, 50};
//                    _cvm.drawString(gl, textColor, bound.left + (int) COMUtil.getPixel_W(8), bound.top + (int) COMUtil.getPixel_H(2), COMUtil.getAddJipyoTitle(viewTitle), 0.48f);
                    //2015. 1. 13 by lyk - 동일지표 이름을 화면에 보여줄때는 COMUtil.JIPYO_ADD_REMARK 이전까지 이름을 사용한다.end
//                    }

                    if (!getTitle().equals("기본거래량")) {
                        if (subTitle.equals("거래량이평5")) {
                            _cvm.drawCircle(gl, bound.left - COMUtil.getPixel(2), bound.top - COMUtil.getPixel(2), bound.left + COMUtil.getPixel(4), bound.top + COMUtil.getPixel(4), true, col);
                            _cvm.drawString(gl, textColor, bound.left + (int) COMUtil.getPixel_W(8), bound.top + (int) COMUtil.getPixel_H(1), COMUtil.getAddJipyoTitle(viewTitle), 0.48f);
                            title_bound.set(COMUtil.getPixel(-28),COMUtil.getPixel(0),COMUtil.getPixel(0),COMUtil.getPixel(0));

                        }
                        // 2021.10.27 by JHY - 이격도 간소화 >>
                        else if(getTitle().startsWith("이격")){
                            if (getTitle().startsWith("이격률(지수)")){
                                _cvm.drawString(gl, textColor, bound.left - (int) COMUtil.getPixel(0), bound.top , "이격률(지수)", 0.48f);
                                _cvm.drawCircle(gl, bound.left + COMUtil.getPixel(60), bound.top - COMUtil.getPixel(2), bound.left + COMUtil.getPixel(66), bound.top + COMUtil.getPixel(4), true, col);
                                _cvm.drawStringWithSize(gl, textColor, bound.left + (int) COMUtil.getPixel_W(70), bound.top + (int) COMUtil.getPixel_H(1), fontSize, viewTitle, 0.48f);
//                            title_bound.set(COMUtil.getPixel(-28),COMUtil.getPixel(0),COMUtil.getPixel(0),COMUtil.getPixel(0));
                            }else if(getTitle().startsWith("이격도1")){
                                _cvm.drawString(gl, textColor, bound.left - (int) COMUtil.getPixel(0), bound.top + (int) COMUtil.getPixel(1), "이격도", 0.48f);
                                _cvm.drawCircle(gl, bound.left + COMUtil.getPixel(35), bound.top - COMUtil.getPixel(2), bound.left + COMUtil.getPixel(41), bound.top + COMUtil.getPixel(4), true, col);
                                _cvm.drawStringWithSize(gl, textColor, bound.left + (int) COMUtil.getPixel_W(45), bound.top + (int) COMUtil.getPixel_H(1), fontSize, viewTitle, 0.48f);
//                            title_bound.set(COMUtil.getPixel(-28),COMUtil.getPixel(0),COMUtil.getPixel(0),COMUtil.getPixel(0));
                            }
                            else if(getTitle().startsWith("이격도")){
                                _cvm.drawCircle(gl, bound.left + COMUtil.getPixel(33), bound.top - COMUtil.getPixel(2), bound.left + COMUtil.getPixel(39), bound.top + COMUtil.getPixel(4), true, col);
                                _cvm.drawString(gl, textColor, bound.left + (int) COMUtil.getPixel_W(43), bound.top + (int) COMUtil.getPixel_H(1), COMUtil.getAddJipyoTitle(viewTitle), 0.48f);
                            }
                            else if(getTitle().startsWith("이격률")){
                                _cvm.drawCircle(gl, bound.left + COMUtil.getPixel(57), bound.top - COMUtil.getPixel(2), bound.left + COMUtil.getPixel(63), bound.top + COMUtil.getPixel(4), true, col);
                                _cvm.drawString(gl, textColor, bound.left + (int) COMUtil.getPixel_W(67), bound.top + (int) COMUtil.getPixel_H(1), COMUtil.getAddJipyoTitle(viewTitle), 0.48f);
                            }
                        }
                        // 2021.10.27 by JHY - 이격도 간소화 <<
                        else {
                            _cvm.drawCircle(gl, bound.left - COMUtil.getPixel(2), bound.top - COMUtil.getPixel(2), bound.left + COMUtil.getPixel(4), bound.top + COMUtil.getPixel(4), true, col);
                            _cvm.drawString(gl, textColor, bound.left + (int) COMUtil.getPixel_W(8), bound.top + (int) COMUtil.getPixel_H(1), COMUtil.getAddJipyoTitle(viewTitle), 0.48f);
                        }
                    } else {
                        _cvm.drawString(gl, textColor, bound.left - COMUtil.getPixel(3), bound.top + (int) COMUtil.getPixel_H(1), COMUtil.getAddJipyoTitle(viewTitle), 0.48f);
                    }
                }

            }
        }
        else {
            if (title_bound.left + title_bound.width() - COMUtil.getPixel(10) < bounds.left + bounds.width()) {
                if (subTitle.length() > 0) {
                    //2012. 8. 9 subtitle 옆 네모도형의 높이가 viewTitle 쪽 네모보다 위쪽으로 튀어있는 현상 수정 : C09
//                    _cvm.drawFillRect(gl, bound.left, bound.top, (int) COMUtil.getPixel_H(5), (int) COMUtil.getPixel_H(5), col, 1.0f);
                    _cvm.drawCircle(gl,bound.left - COMUtil.getPixel(2) ,bound.top - COMUtil.getPixel(2) ,bound.left +COMUtil.getPixel(4) ,bound.top+ COMUtil.getPixel(4), true, col );
                    //_cvm.drawCircle(gl, bound.left, bound.top + (int) COMUtil.getPixel(1), bound.left + COMUtil.getPixel(8), bound.top + COMUtil.getPixel(9), true, col);
                }
                //_cvm.drawString(gl, col, bound.left + (int) COMUtil.getPixel(13), bound.top + (int) COMUtil.getPixel(9), subTitle);
                _cvm.drawString(gl, textColor, bound.left + (int) COMUtil.getPixel_W(7), bound.top + (int) COMUtil.getPixel_H(1), subTitle, 0.48f);
            }
//            if (!subTitle.equals("PRICE") && _cvm.isCrosslineMode == true) {
//
////        		String value = this.getFormatData(_cvm.curIndex);
////        		int tfLen = COMUtil.tf.GetTextLength(value)+5;
//                //       		int subTitleLen = _cvm.tf.GetTextLength(subTitle)+5;
////
////        		_cvm.drawString(gl, col, bound.left,bound.top+(int)COMUtil.getPixel(7), subTitle);
////        		_cvm.drawString(gl, CoSys.GRAY, bound.left+subTitleLen,bound.top+(int)COMUtil.getPixel(7), value);
//            }
//            else {
//                if (title_bound.left + title_bound.width() - COMUtil.getPixel(10) < bounds.left + bounds.width()) {
//                    if (subTitle.length() > 0) {
//                        //2012. 8. 9 subtitle 옆 네모도형의 높이가 viewTitle 쪽 네모보다 위쪽으로 튀어있는 현상 수정 : C09
//                        _cvm.drawFillRect(gl, bound.left, bound.top, (int) COMUtil.getPixel_H(5), (int) COMUtil.getPixel_H(5), col, 1.0f);
//                        //_cvm.drawCircle(gl, bound.left, bound.top + (int) COMUtil.getPixel(1), bound.left + COMUtil.getPixel(8), bound.top + COMUtil.getPixel(9), true, col);
//                    }
//                    //_cvm.drawString(gl, col, bound.left + (int) COMUtil.getPixel(13), bound.top + (int) COMUtil.getPixel(9), subTitle);
//                    _cvm.drawString(gl, col, bound.left + (int) COMUtil.getPixel_W(7), bound.top + (int) COMUtil.getPixel_H(2), subTitle);
//                }
//            }
        }
    }
    //=====================================
    // 특정 기준가에 대해 그린다
    //=====================================
    public void plot(Canvas gl,double data){
        yfactor = ((max_view-min_view)*1.0f)/(float)(max_data-min_data);
//        if(yfactor>10)
//        	Log.d("ttt", max_data+"///"+min_data+"///"+yfactor );

        setDrawGab();
        if(isVisible()){
            drawTitle(gl);
            draw(gl,data);
        }
    }

    //=====================================
    // 일차원 배열의 데이터를 가지고 그리기 
    //=====================================
    public void plot(Canvas gl,double[] data){
        xfactor = _cvm.getXFactor(bounds.width());
        //xfactor = (bounds.width*1.0f)/(_cvm.getViewNum());
        yfactor = ((max_view-min_view)*1.0f)/(float)(max_data-min_data);
        setDrawGab();
        if(isVisible()){
            drawTitle(gl);
            draw(gl,data);
        }
    }

    public void plotDefault(Canvas gl,double[] data){
        xfactor = _cvm.getXFactor(bounds.width());
        yfactor = ((max_view-min_view)*1.0f)/(float)(max_data-min_data);
        setDrawGab();
        if(isVisible()){
            drawTitle(gl);
            drawDefault(gl,data);
        }
    }

    //    //=====================================
//    // 일차원 배열의 데이터를 가지고 그리기 
//    //=====================================
//    public void drawVolumeForSale(Canvas gl,double[] data){
//        xfactor = _cvm.getXFactor(bounds.width());
//        //xfactor = (bounds.width*1.0f)/(_cvm.getViewNum());
//        yfactor = ((max_view-min_view)*1.0f)/(float)(max_data-min_data);
//        setDrawGab();
//        if(isVisible()){
//            drawTitle(gl);
//            draw(gl,data);
//        }
//    }
    //=====================================
    // 일차원 배열의 데이터에 대해 그리고 기준선 표시
    //=====================================
    public void plot(Canvas gl, double[] data, double[] stand){
        xfactor = _cvm.getXFactor(bounds.width());
        yfactor = ((max_view-min_view)*1.0f)/(float)(max_data-min_data);
        setDrawGab();
        if(isVisible()){
            drawTitle(gl);
            draw(gl,data,stand);
        }
    }
    //=====================================
    // 다차원 배열 데이터를 가지고 그리기 (예: 봉차트)
    //=====================================
    public void plot(Canvas gl,double[][] data){
        xfactor = _cvm.getXFactor(bounds.width());
        yfactor = ((max_view-min_view)*1.0f)/(float)(max_data-min_data);
        setDrawGab();
        if(isVisible()){
            drawTitle(gl);
            draw(gl,data);
        }
    }

    //=====================================
    // 다차원 배열의 해당 col 데이터에 대해 그리고 기준선 표시
    //=====================================
    public void plot(Canvas gl, double[][] data, double[] stand){
        //xfactor = (bounds.width*1.0f)/(data.length);
        xfactor = _cvm.getXFactor(bounds.width());
        yfactor = ((max_view-min_view)*1.0f)/(float)(max_data-min_data);
        setDrawGab();

        if(isVisible()){
            drawTitle(gl);
            draw(gl,data,stand);
        }
    }
    //=====================================
    //  대기매물에서의 바와같이 %로 표현되는 것...
    //=====================================
    public void plotVolumeForSale(Canvas gl, double[] stand){
        if (stand == null) return; //2024.03.12 by SJW - 2.27.5 crash 오류 수정
        double[] mm_data = MinMax.getMinMax(stand);
        //2024.03.12 by SJW - 2.27.5 crash 오류 수정 >>
        if (mm_data == null || mm_data.length < 2) {
            return;
        }
        //2024.03.12 by SJW - 2.27.5 crash 오류 수정 <<
        xfactor =(float)( (bounds.width()-5*1.0f)/(mm_data[1]));
        //2013.04.01 by LYH >> 대기매물 개선 
        //yfactor = (float)( ((max_view-min_view)*1.0f)/(stand.length));
        yfactor = ((max_view-min_view)*1.0f)/(float)(max_data-min_data);
        //2013.04.01 by LYH <<

        if(isVisible()){
            drawTitle(gl);
            drawVolumeForSale(gl,stand);
        }
    }

    public void init(int[][] data){
    }
    //=====================================
    // y축의 픽셀 좌표를 계산하여 반환
    //=====================================
    boolean log = false;
    public void setLog(boolean log){
        this.log = log;
    }
    public boolean isLog(){
        return this.log;
    }
    public float calcy(double yp){
        if(log&&yp!=0) yp = (Math.log(Math.abs(yp))*1000);

        //20030519 ykLee modify.(거래량 0인값 안보이게 처리)
        /*
        if(yp==0){
            if(show_zero_val)return max_view;
            else return max_view+1;
        }
        */

        float ypnt = (float)((max_data-yp)*yfactor);
        //System.out.println(max_data+" "+yp+" "+yfactor);
        if(isInverse()){
            return max_view-ypnt;
        }else{
            //Log.d("ttt", min_view+":"+max_view+":"+yfactor);
            return min_view+ypnt;
        }
    }


    //2016.07.25 by lyk - x축이 날짜가 아닌 데이터로 처리할 경우 사용
    public float calcx(double xp, double maxData, double minData){
        if(log&&xp!=0) xp = (Math.log(Math.abs(xp))*1000);

        float xAxisfactor = ((max_view_width-min_view_width)*1.0f)/(float)(maxData-minData);

        //20030519 ykLee modify.(거래량 0인값 안보이게 처리)
        /*
        if(yp==0){
            if(show_zero_val)return max_view;
            else return max_view+1;
        }
        */

        float xpnt = (float)((maxData-xp)*xAxisfactor);
        //System.out.println(max_data+" "+yp+" "+yfactor);
        return max_view_width-xpnt;
//        if(isInverse()){
//            return max_view_width-xpnt;
//        }else{
//            //Log.d("ttt", min_view+":"+max_view+":"+yfactor);
//            return min_view_width+xpnt;
//        }
    }

    //=====================================
    // 절편을 구한다
    //=====================================
    public int getCrossX(float stand,float x1,float x2, float y1, float y2){
        float f = (float)((y2-y1)/(x1-x2));
        int x =(int)((stand-y2)/f);
        return (int)(x);
    }

    //=====================================
    // DrawTool을 상속받은 각 클래스에서 정의한다
    // stand: 기준가
    // interval :
    //=====================================
    public abstract void draw(Canvas gl, double data);
    public abstract void drawDefault(Canvas gl, double[] data);
    public abstract void draw(Canvas gl, double[] data);
    public abstract void draw(Canvas gl, double[] data, double[] stand);
    public abstract void draw(Canvas gl, double[][] data);
    public abstract void draw(Canvas gl, double[][] data, double[] stand);
    public abstract void drawVolumeForSale(Canvas gl, double[] stand);//대기매물용

    public void setSelected(boolean b){
        selected = b;
    }
    public void drawToolTip(Canvas gl, String data,int sx, int sy, boolean up,boolean fill,int fillcol){
        int[] x = new int[3];
        int[] y = new int[3];
        if(up){
            y[0] = sy;
            y[1] = sy;
            y[2] = sy+5;
        }else{
            y[0] = sy;
            y[1] = sy+5;
            y[2] = sy+11;
        }
        //int width=g.getFontMetrics().stringWidth(data.toString());
        int width = 40;
        int gab = 5;
        if(sx+width+10<getBounds().left+getBounds().width()){
            x[0] = sx;
            x[1] = sx+6+gab;
            x[2] = sx+6+gab;
            //sx += 6;
            sx += 6+gab;
        }else{
            x[0] = sx;
            x[1] = sx-6-gab;
            x[2] = sx-6-gab;
            //sx -= (width+6+10);
            sx -= (width+6+10+gab);
        }
        if(fill){
            _cvm.drawFillRect(gl, sx, sy, width+10, 18, CoSys.at_col, 1.0f);
        }

        _cvm.drawFillRect(gl, sx, sy, width+10, 18, CoSys.at_col, 1.0f);
        _cvm.drawString(gl, CoSys.WHITE,sx+5, sy+15, data.toString());

    }
    public boolean isSelected(){
        return selected;
    }
    public float getYPos(int pos){
        if(data!=null){
            return calcy(data[pos]);
        }
        return 0;
    }
    public String getFormatData(int pos){
        return _cdm.getFormatData(title, pos);
    }
    public String getFormatData(int pos, String title){
        return _cdm.getFormatData(title, pos);
    }
    public boolean isSelected(PointF p, int index){
        if(data==null || !isVisible()) return false;
        int idx= _cvm.getIndex();
        int curIndex = index-idx;
        if(curIndex >= data.length||curIndex<0) return false;
        float curY = calcy(data[curIndex]);
        if( (p.y>curY-5) && (p.y<curY+5 ))
            return true;

        int preIndex=curIndex-1, nextIndex=curIndex+1;
        if(preIndex<0 ) preIndex = 0;
        if(nextIndex >= data.length) nextIndex = data.length-1;
        float preY = calcy(data[preIndex]);
        float nextY = calcy(data[nextIndex]);

        RectF rect;
        if(preY>curY){
            rect = new RectF((int)(xfactor*preIndex+xfactor/2), (int)curY, (int)xfactor, (int)(preY-curY));
        }else{
            rect = new RectF((int)(xfactor*preIndex+xfactor/2), (int)preY, (int)xfactor, (int)(curY-preY));
        }
        if(rect.contains(p.x, p.y)) return true;

        if(curY>nextY){
            rect = new RectF((int)(xfactor*curIndex+xfactor/2), (int)nextY, (int)xfactor, (int)(curY-nextY));
        }else{
            rect = new RectF((int)(xfactor*curIndex+xfactor/2), (int)curY, (int)xfactor, (int)(nextY-curY));
        }
        if(rect.contains(p.x, p.y)) return true;
        return false;
    }

    public String getPacketTitle(){
        //if(viewTitle!=null) return viewTitle;
        return title;
    }
    public String getViewTitle(){
        return this.viewTitle;
    }
    public void setUpColor(int[] c){
        upColor = c;
    }
    public void setUpColor2(int[] c){
        upColor2 = c;
    }
    public void setBackColor(int[] c){
        back = c;
    }
    public void setDownColor(int[] c){
        downColor = c;
    }
    public void setDownColor2(int[] c){
        downColor2 = c;
    }
    public void setSameColor(int[] c){
        sameColor = c;
    }
    public void setStandVal(int val){
        stand[0] = val;
    }
    public void setStandVal(int[] val){
        stand=val;
    }
    public void setDrawType1(int type){
        drawType1 = type;
    }
    public void setDrawType2(int type){
        drawType2 = type;
    }
    public int getDrawType1(){
        return drawType1;
    }
    public int getDrawType2(){
        return drawType2;
    }
    public String getTitle(){
        return title;
    }
    public void setTitle(String s){
        title = s;
//        if(title.indexOf("/")>=0) subTitle = new String(title.substring(title.indexOf("/")+1));
//        else
            subTitle=s;
    }
    public void setViewTitle(String s){
        if(s==null) viewTitle = "";
        else viewTitle = s;
    }
    public void setLineT(int t){
        line_thick = t;//라인굵기 설정
    }
    public void setVisible(boolean b){
        isVisible= b;
        //setHideTitleButton(!b);
    }
    public boolean isVisible(){
        return isVisible;
    }
    //2012. 7. 2  라인굵기 설정 가져오기 
    public int getLineT()
    {
        return line_thick;
    }
    public int[] getUpColor(){
        return upColor;
    }
    public int[] getUpColor2(){
        return upColor2;
    }
    public int[] getDefUpColor(){
        return def_upColor;
    }
    public int[] getDownColor(){
        return downColor;
    }
    public int[] getDownColor2(){
        return downColor2;
    }
    public int[] getSameColor(){
        return sameColor;
    }
    public int[] getBackColor(){
        return back;
    }
    public boolean isFillUp(){
        return fillUp;
    }
    //2021.05.24 by hhk - 구름대 채우기 >>
    public boolean isFillCloud(){
        return fillCloud;
    }
    //2021.05.24 by hhk - 구름대 채우기 <<
    public boolean isFillUp2(){
        return fillUp2;
    }
    public boolean isFillDown(){
        return fillDown;
    }
    public boolean isFillDown2(){
        return fillDown2;
    }
    //2021.05.24 by hhk - 구름대 채우기 >>
    public void setFillCloud(boolean b){
        fillCloud = b;
    }
    //2021.05.24 by hhk - 구름대 채우기 <<
    public void setFillUp(boolean b){
        fillUp = b;
    }
    public void setFillUp2(boolean b){
        fillUp2 = b;
    }
    public void setFillDown(boolean b){
        fillDown = b;
    }
    public void setFillDown2(boolean b){
        fillDown2 = b;
    }
    public void setInvertScale(boolean b){
        inverse = b;
    }
    public boolean isInverse(){
        return inverse;
    }
    Paint pnt = new Paint();
    //2012. 7. 4 한글라벨타이틀 Text_View로  추가 
    Text_View tv;
    public void setTitleBounds(float sx, float sy){
        if (!_cvm.bIsShowTitle || _cvm.bIsTodayLineChart) {
            return;
        }

        float fontSize = COMUtil.nFontSize_paint;
        //2021.07.14 by hanjun.Kim - kakaopay - 좌상단 이평아이콘 위치조정 >>
        float marginTop = COMUtil.getPixel(0);
        float marginLeft = COMUtil.getPixel(0);
        sx = sx + marginLeft;
        sy = sy + marginTop;

        //2020.12.28 by HJW - 폰트 사이즈 옵션 추가 >>
        if(_cvm.g_nFontSizeBtn == 0)
            fontSize = COMUtil.nFontSize_paint-COMUtil.getPixel(2);
        else if(_cvm.g_nFontSizeBtn == 2)
            fontSize = COMUtil.nFontSize_paint+COMUtil.getPixel(2);
        //2020.12.28 by HJW - 폰트 사이즈 옵션 추가 <<
        //2021.07.14 by hanjun.Kim - kakaopay - 좌상단 이평아이콘 위치조정 <<
        if(viewTitle != null && viewTitle.equals("외국인/기관/개인 추세")) {
            viewTitle = "외국인";
        }
        int nWidth = (int) _cvm.getFontWidth(COMUtil.getAddJipyoTitle(viewTitle), (int)fontSize);
        int nHeight = (int)COMUtil.getPixel_H(17);
        int nMargin = (int) COMUtil.getPixel_W(7);
        int nGab = (int) COMUtil.getPixel_W(10);

        if (viewTitle != null) {
            if (viewTitle.equals("")) nWidth = 0;
            else if(viewTitle.equals("가격")) nWidth = (int)COMUtil.getPixel_W(0);
            //2021.09.15 by JHY >> 거래량과 숫자 사이 거리 조정
//            else if(viewTitle.equals("거래량")) nWidth += nGab;
            else if(viewTitle.equals("거래량")) nWidth += (int) COMUtil.getPixel_W(7);
            else if(viewTitle.equals("기본거래량")) nWidth += nGab;
            //2021.09.15 by JHY 보조지표 거래량 과 circle 사이 간격조절(차트클릭시)
            else if(subTitle.equals("거래량")&& !viewTitle.equals("거래량")) nWidth += nMargin;
            else {
                nWidth += nMargin + nGab;    //2015. 3. 3 차트 롱터치시 지표타이틀 안보임
            }
        }
        else {
            if (subTitle != null && subTitle.length() > 0) {
                nWidth = (int) _cvm.getFontWidth(subTitle, (int)fontSize) + nMargin + nGab;
            }
        }

        title_bound.set(sx, sy, sx + nWidth, sy + nHeight);
    }
    public RectF getTitleBounds(){
        return title_bound;
    }
    public void gradationDraw(Canvas g,int start, int end, int _cx, int _cy,int _w, int _h ,boolean vert) {

        int rC, gC, bC;
//        float rU, gU, bU, rectU, sign;

        rC = Color.red(end) - Color.red(start);
        gC = Color.green(end) - Color.green(start);
        bC = Color.blue(end) - Color.blue(start);

        int MaxC = Math.max(Math.abs(rC), Math.max(Math.abs(gC), Math.abs(bC)));

//        rectU = (vert)?((float)_h/(float)MaxC):((float)_w/(float)MaxC);
//        rU = (float)rC/(float)MaxC;
//        gU = (float)gC/(float)MaxC;
//        bU = (float)bC/(float)MaxC;

        rC = Color.red(start);
        gC = Color.green(start);
        bC = Color.blue(start);
        if(vert){
            for(int i=0 ; i<MaxC ; i++) {
//                pnt.setColor(Color.rgb((int)(rC+rU*i), (int)(gC+gU*i), (int)(bC+bU*i)));
//                pnt.setStyle(Paint.Style.FILL);
//                Rect r = new Rect(_cx,_cy+ (int)(rectU*i), _w+_cx ,(int)(rectU*i)+_cy);
//                g.drawRect(r, pnt);
            }
        }else{
            for(int i=0 ; i<MaxC ; i++) {
//                pnt.setColor(Color.rgb((int)(rC+rU*i), (int)(gC+gU*i), (int)(bC+bU*i)));
//                pnt.setStyle(Paint.Style.FILL);
//                Rect r = new Rect(_cx+(int)(rectU*i),_cy,(int)(rectU*i)+_cx+(int)(rectU*i), _h+_cy);
//                g.drawRect(r, pnt);
            }
        }
        if(MaxC==0) {
//            pnt.setColor(start);
//            pnt.setStyle(Paint.Style.FILL);
//            Rect r = new Rect(_cx,_cy, _w,_h);
//            g.drawRect(r, pnt);
        }
    }

    public String getCompareData(int pos) {
        String strTitle = title+"_1";
        return COMUtil.format(_cdm.getData(strTitle, pos),2,3);
    }
    public void addRectPosition(float[] array, int nIdx, float xStart, float yStart, float xEnd, float yEnd)
    {
        array[nIdx++]=xStart;
        array[nIdx++]=yStart;
        array[nIdx++]=xEnd;
        array[nIdx++]=yEnd;
//        array[nIdx++]=xStart;
//        array[nIdx++]=yStart;
//        array[nIdx++]=xStart;
//        array[nIdx++]=yEnd;
//        array[nIdx++]=xEnd;
//        array[nIdx++]=yEnd;
//        array[nIdx++]=xEnd;
//        array[nIdx++]=yStart;
//        array[nIdx++]=xEnd;
//        array[nIdx++]=yEnd;
//        array[nIdx++]=xStart;
//        array[nIdx++]=yStart;
    }
    public void addEmptyRectPosition(float[] array, int nIdx, float xStart, float yStart, float xEnd, float yEnd)
    {
        array[nIdx++]=xStart;
        array[nIdx++]=yStart;
        array[nIdx++]=xStart;
        array[nIdx++]=yEnd;
        array[nIdx++]=xEnd;
        array[nIdx++]=yEnd;
        array[nIdx++]=xEnd;
        array[nIdx++]=yStart;
        array[nIdx++]=xEnd;
        array[nIdx++]=yEnd;
        array[nIdx++]=xStart;
        array[nIdx++]=yEnd;
        array[nIdx++]=xEnd;
        array[nIdx++]=yStart;
        array[nIdx++]=xStart;
        array[nIdx++]=yStart;
    }
    //2012. 7. 4  보조지표 사라질때 title라벨 삭제 
    public void destroy()
    {
//    	if(lbTitle != null)
//        {
//    		_cdm._chart.layout.removeView(lbTitle);
//            COMUtil.unbindDrawables(lbTitle);
//        }

    }
    //2012. 7. 10  보조지표 토글시  타이틀 숨기기/보이기 
    public void setHideTitleButton(boolean bHide)
    {
//    	if(lbTitle != null)
//    	{
//    		if(bHide)
//    		{
//    			lbTitle.setVisibility(View.GONE);
//    		}
//    		else
//    		{
//    			lbTitle.setVisibility(View.VISIBLE);
//    		}
//    	}
    }

    //2013. 9. 5 매물대설정 값 문자열 보이기 여부 처리>>
    public void setStandScaleLabelShow(boolean bShow)
    {
        bStandScaleLabelShow = bShow;
    }

    public boolean isStandScaleLabelShow()
    {
        return bStandScaleLabelShow;
    }
    //2013. 9. 5 매물대설정 값 문자열 보이기 여부 처리>>


    //2014. 9. 15 매매 신호 보기 기능 추가>>
    public void drawSignal(Canvas gl, double[] data, double[] signalData)
    {
        if(data==null||data.length<1)return;

        int startIndex = _cvm.getIndex();
        int startPos=startIndex;
        if(startPos<0)
            startPos = 0;
        float xpos=0;
        int dataLen = startIndex + _cvm.getViewNum() + _cvm.futureMargin;
        if(dataLen>data.length)
            dataLen= data.length;
        xpos=getBounds().left+xw;
        for(int i=startPos;i<dataLen;i++){
            if(dataLen>i && data[i]!=0){
                startPos=i;
                xpos=(int)(xpos+((i-startIndex)*xfactor));
                break;
            }
        }

        if(startPos >= data.length)
            return;

        float y = calcy(data[startPos]);
        float y_signal = calcy(signalData[startPos]);
        AREA area;  //2020.07.06 by LYH >> 캔들볼륨
        for(int i=startPos+1;i<dataLen;i++){
            float y1 = calcy(data[i]);
            float y1_signal = calcy(signalData[i]);

            if((y>=min_view-1&&y1>=min_view-1) && (y<=max_view&&y1<=max_view)){
                if(y>=y_signal && y1<y1_signal)
                {
                    float x = (int)(xpos+xfactor);
                    //2020.07.06 by LYH >> 캔들볼륨 >>
                    area = _cvm.getArea(i-startIndex);
                    if(area!=null)
                    {
                        x = area.getCenter();
                    }
                    //2020.07.06 by LYH >> 캔들볼륨 <<
                    //_cvm.drawLine(gl, x, y1+COMUtil.getPixel(4), x, y1+COMUtil.getPixel(7), CoSys.CHART_COLORS[0] ,1.0f);
                    //_cvm.drawLine(gl, x-COMUtil.getPixel(1), y1+COMUtil.getPixel(4), x-COMUtil.getPixel(1), y1+COMUtil.getPixel(7), CoSys.CHART_COLORS[0] ,1.0f);
//                    _cvm.drawFillRect(gl,  x-COMUtil.getPixel(2), y1+COMUtil.getPixel(5), COMUtil.getPixel(2), COMUtil.getPixel(3), CoSys.CHART_COLORS[0], 1.0f);
//                    _cvm.drawFillTriangle(gl, x-COMUtil.getPixel(4),y1+COMUtil.getPixel(2),COMUtil.getPixel(6),(int)COMUtil.getPixel(3), CoSys.CHART_COLORS[0]);

                    //2021.07.23 by hanjun.Kim - kakaopay - 보조지표 화살표 이미지로 변경

                    //2021.09.29 by lyk - kakaopay - 보조지표 시그널 이미지 표시 부하 개선 >>
//                    Context context = COMUtil._chartMain;
//                    int layoutResId = context.getResources().getIdentifier("kp_mts_ic_common_assist_arrow_red", "drawable", context.getPackageName());
//                    Bitmap backgroundBitmap = BitmapFactory.decodeResource(COMUtil.apiView.getContext().getResources(), layoutResId);
//                    Bitmap panelBackImg_resize = Bitmap.createScaledBitmap(backgroundBitmap, (int) COMUtil.getPixel(8), (int) COMUtil.getPixel(8),true);
                    _cvm.drawImage(gl, x - COMUtil.getPixel(4), y1, COMUtil.getPixel(8), COMUtil.getPixel(8), _cvm.imgSignalBuy, 255);
                    //2021.09.29 by lyk - kakaopay - 보조지표 시그널 이미지 표시 부하 개선 <<
                }

                if(y<=y_signal && y1>y1_signal)
                {
                    float x = xpos + xfactor;
                    //2020.07.06 by LYH >> 캔들볼륨 >>
                    area = _cvm.getArea(i-startIndex);
                    if(area!=null)
                    {
                        x = area.getCenter();
                    }
                    //2020.07.06 by LYH >> 캔들볼륨 <<
                    //_cvm.drawLine(gl, x, y1-COMUtil.getPixel(5), x, y1-COMUtil.getPixel(8), CoSys.CHART_COLORS[1] ,1.0f);
                    //_cvm.drawLine(gl, x-COMUtil.getPixel(1), y1-COMUtil.getPixel(5), x-COMUtil.getPixel(1), y1-COMUtil.getPixel(8), CoSys.CHART_COLORS[1] ,1.0f);
//                    _cvm.drawFillRect(gl,  x-COMUtil.getPixel(2), y1-COMUtil.getPixel(8), COMUtil.getPixel(2), COMUtil.getPixel(3), CoSys.CHART_COLORS[1], 1.0f);
//                    _cvm.drawFillTriangle(gl, x-COMUtil.getPixel(4),y1-COMUtil.getPixel(5),COMUtil.getPixel(6),(int)COMUtil.getPixel(-3), CoSys.CHART_COLORS[1]);

                    //2021.07.23 by hanjun.Kim - kakaopay - 보조지표 화살표 이미지로 변경

                    //2021.09.29 by lyk - kakaopay - 보조지표 시그널 이미지 표시 부하 개선 >>
//                    Context context = COMUtil._chartMain;
//                    int layoutResId = context.getResources().getIdentifier("kp_mts_ic_common_assist_arrow_blue", "drawable", context.getPackageName());
//                    Bitmap backgroundBitmap = BitmapFactory.decodeResource(COMUtil.apiView.getContext().getResources(), layoutResId);
//                    Bitmap panelBackImg_resize = Bitmap.createScaledBitmap(backgroundBitmap, (int) COMUtil.getPixel(8), (int) COMUtil.getPixel(8),true);
                    _cvm.drawImage(gl, x - COMUtil.getPixel(4), y1 - COMUtil.getPixel(8), COMUtil.getPixel(8), COMUtil.getPixel(8), _cvm.imgSignalSell, 255);
                    //2021.09.29 by lyk - kakaopay - 보조지표 시그널 이미지 표시 부하 개선 <<
                }
            }
            y=y1;
            y_signal=y1_signal;
            xpos+=xfactor;
        }
    }
    //2014. 9. 15 매매 신호 보기 기능 추가<<

    //2015.01.08 by LYH >> 3일차트 추가
    public void drawStandardLine(Canvas gl, double[] data, String stdPrice){
        if(data==null||data.length<1)return;
        double dStdPrice = 0;
        try
        {
            dStdPrice = Double.parseDouble(stdPrice);
        }
        catch(Exception e)
        {
        }
        float yBasePos=(int)calcy(dStdPrice);
        this.data = data;
        float xpos=getBounds().left+xw;
        float ypos=0;
        float ypos1=0;

        double yInc;
        line_thick=2;
        int thick =line_thick, temp;
        int startIndex = _cvm.getIndex();
        int dataLen = startIndex + _cvm.getViewNum();
        if(dataLen>data.length)
            dataLen= data.length;

        int totLen = (dataLen-startIndex-1)*4;
        if(dataLen==1) {
            totLen = 4;
        }
        float[] positionsUp = new float[totLen];// = new float[dataLen];
        float[] positionsDown = new float[totLen];// = new float[dataLen];
        int nIndexUp = 0, nIndexDown = 0;

        if(dataLen==1) {
            ypos=(int)calcy(data[0]);
            _cvm.drawLine(gl, (int)xpos,(int)ypos,(int)(xpos+line_thick),(int)ypos, CoSys.UP_LINE_COLORS ,1.0f);
        } else {
            _cvm.setLineWidth(1);
            if(!_cvm.bIsLineFillChart)
            {
                if((yBasePos>=min_view&&yBasePos<=max_view)){
                    _cvm.drawLine(gl, (int)getBounds().left,(int)yBasePos,(int)getBounds().right,(int)yBasePos, CoSys.STANDARD ,1.0f);
                }
            }
            for(int i=startIndex; i<dataLen-1; i++){
                ypos=(int)calcy(data[i]);

                ypos1 = (int)calcy(data[i+1]);
                if(data[i]==0 || data[i+1]==0)
                {
                    xpos+=xfactor;
                    continue;
                }
                if((ypos<=max_view&&ypos1<=max_view)){
                    yInc = (double)(((double)ypos1-(double)ypos)/(double)(xfactor*2));
                    temp = (yInc<0)?Math.abs((int)(-1*(yInc-0.99))):(int)(yInc+0.99);
                    thick=(temp>thick)?temp:line_thick;

                    double xpos1 = xpos+xfactor;
                    double f = (double)((ypos1-ypos)/(xpos-xpos1));
                    double xMid = ((double)(yBasePos - ypos)/f);
                    if(xMid>0)
                        xMid =(xpos+xMid);
                    else
                        xMid = (xpos-xMid);
                    if(ypos<=yBasePos && ypos1<=yBasePos){
                        if(_cvm.bIsLineFillChart)
                        {
                            positionsUp[nIndexUp++] = xpos;
                            positionsUp[nIndexUp++] = ypos;
                            positionsUp[nIndexUp++] = xpos+xfactor;
                            positionsUp[nIndexUp++] = ypos1;
                        }
                        else
                            _cvm.drawLine(gl, (int)xpos,(int)ypos,(int)(xpos+xfactor),(int)ypos1, CoSys.UP_LINE_COLORS ,1.0f);
                    } else if(ypos>yBasePos && ypos1>yBasePos) {
                        if(_cvm.bIsLineFillChart)
                        {
                            positionsDown[nIndexDown++] = xpos;
                            positionsDown[nIndexDown++] = ypos;
                            positionsDown[nIndexDown++] = xpos+xfactor;
                            positionsDown[nIndexDown++] = ypos1;
                        }
                        else
                            _cvm.drawLine(gl, (int)xpos,(int)ypos,(int)(xpos+xfactor),(int)ypos1, CoSys.DOWN_LINE_COLORS ,1.0f);
                    } else if(ypos>yBasePos && ypos1<=yBasePos){
                        if(_cvm.bIsLineFillChart)
                        {
                            positionsDown[nIndexDown++] = xpos;
                            positionsDown[nIndexDown++] = ypos;
                            positionsDown[nIndexDown++] = (int)xMid;
                            positionsDown[nIndexDown++] = yBasePos;

                            positionsUp[nIndexUp++] = (int)xMid;
                            positionsUp[nIndexUp++] = yBasePos;
                            positionsUp[nIndexUp++] = xpos+xfactor;
                            positionsUp[nIndexUp++] = ypos1;
                        }
                        else
                        {
                            _cvm.drawLine(gl, (int)xpos,(int)ypos,(int)xMid,(int)yBasePos, CoSys.DOWN_LINE_COLORS ,1.0f);
                            _cvm.drawLine(gl, (int)xMid,(int)yBasePos,(int)(xpos+xfactor),(int)ypos1, CoSys.UP_LINE_COLORS ,1.0f);
                        }
                    } else if(ypos<=yBasePos && ypos1>yBasePos){
                        if(_cvm.bIsLineFillChart)
                        {
                            positionsUp[nIndexUp++] = (int)xpos;
                            positionsUp[nIndexUp++] = ypos;
                            positionsUp[nIndexUp++] = (int)xMid;
                            positionsUp[nIndexUp++] = yBasePos;

                            positionsDown[nIndexDown++] = (int)xMid;
                            positionsDown[nIndexDown++] = yBasePos;
                            positionsDown[nIndexDown++] = xpos+xfactor;
                            positionsDown[nIndexDown++] = ypos1;
                        }
                        else
                        {
                            _cvm.drawLine(gl, (int)xpos,(int)ypos,(int)xMid,(int)yBasePos, CoSys.UP_LINE_COLORS ,1.0f);
                            _cvm.drawLine(gl, (int)xMid,(int)yBasePos,(int)(xpos+xfactor),(int)ypos1, CoSys.DOWN_LINE_COLORS ,1.0f);
                        }
                    }
                    else{
                        _cvm.drawLine(gl, (int)xpos,(int)ypos,(int)(xpos+xfactor),(int)ypos1, CoSys.LAST_VALUE_SAME_BG ,1.0f);
                    }

                    if(isSelected()){
                        if(i%5==0){
                            _cvm.drawRect(gl, (int)xpos,(int)ypos,5,5, CoSys.UP_LINE_COLORS);
                        }
                    }
                }
                xpos+=xfactor;
            }
            _cvm.setLineWidth(1);
        }

        //2012. 11. 20 해외선물 5일 분차트면 gradient 형식으로 라인 아랫쪽을 그린다. : C31
        if(_cvm.bIsLineFillChart)
        {
            _cvm.setLineWidth_Fix(2);
            int[] colorLine = new int[3];
            int[] color0 = new int[3];

            if(yBasePos>max_view)
            {
                yBasePos = max_view;
            }
            if(yBasePos<min_view)
            {
                yBasePos = min_view;
            }
            if(nIndexUp>0)
            {
                colorLine[0] = 203;
                colorLine[1] = 29;
                colorLine[2] = 118;
                _cvm.drawLines(gl, positionsUp, colorLine ,1.0f);
                color0[0] = 203;
                color0[1] = 29;
                color0[2] = 118;
                _cvm.drawLineWithFillGradient(gl, positionsUp, yBasePos , color0, 200, nIndexUp, min_view);
            }
            if(nIndexDown>0)
            {
                colorLine[0] = 62;
                colorLine[1] = 100;
                colorLine[2] = 166;
                _cvm.drawLines(gl, positionsDown, colorLine ,1.0f);
                color0[0] = 62;
                color0[1] = 100;
                color0[2] = 166;
                _cvm.drawLineWithFillGradient(gl, positionsDown, yBasePos , color0, 200, nIndexDown, min_view);
            }
        }
        if((yBasePos>=min_view&&yBasePos<=max_view)){
            if(yBasePos+(int)COMUtil.getPixel(7)>=max_view)
                _cvm.drawString(gl, CoSys.STANDARD, (int)getBounds().left, yBasePos-(int)COMUtil.getPixel(7), ChartUtil.getFormatedData(stdPrice, _cdm.getPriceFormat(), _cdm));
            else
                _cvm.drawString(gl, CoSys.STANDARD, (int)getBounds().left, yBasePos+(int)COMUtil.getPixel(7), ChartUtil.getFormatedData(stdPrice, _cdm.getPriceFormat(), _cdm));
        }

        //지표 현재값 표시.
        if(_cvm.useJipyoSign==true) {
            double curVal = data[dataLen-1];
            String curStr = getFormatData(dataLen-1);

            //int curStrLen = _cvm.tf.GetTextLength(curStr)+20;
            float yp = calcy(curVal);
            //xpos = this.getBounds().right+6;
            //xpos = _cvm.getBounds().left + _cvm.getBounds().width() - _cvm.Margin_R + 6;
            xpos = _cvm.getBounds().left + _cvm.getBounds().width() - _cvm.Margin_R +(int)COMUtil.getPixel(1);
            int pw = _cvm.Margin_R;
            if(curVal>dStdPrice)
            {
//                _cvm.drawFillTri(gl, xpos,yp,COMUtil.getPixel(4),(int)COMUtil.getPixel(14), CoSys.UP_LINE_COLORS);
//                _cvm.drawFillRect(gl, xpos+(int)COMUtil.getPixel(4), yp-(int)COMUtil.getPixel(7), pw, (int)COMUtil.getPixel(14), CoSys.UP_LINE_COLORS, 1.0f);

                _cvm.drawCurrentPriceBox(gl, xpos, yp-(int)COMUtil.getPixel_H(18)/2, pw-(int)COMUtil.getPixel(2), (int)COMUtil.getPixel_H(18), CoSys.UP_LINE_COLORS);
            }
            else
            {
//                _cvm.drawFillTri(gl, xpos,yp,COMUtil.getPixel(4),(int)COMUtil.getPixel(14), CoSys.DOWN_LINE_COLORS);
//                _cvm.drawFillRect(gl, xpos+(int)COMUtil.getPixel(4), yp-(int)COMUtil.getPixel(7), pw, (int)COMUtil.getPixel(14), CoSys.DOWN_LINE_COLORS, 1.0f);
                _cvm.drawCurrentPriceBox(gl, xpos, yp-(int)COMUtil.getPixel_H(18)/2, pw-(int)COMUtil.getPixel(2), (int)COMUtil.getPixel_H(18), CoSys.DOWN_LINE_COLORS);
            }
            //_cvm.drawString(gl, CoSys.BLACK, (int)xpos, (int)yp, ChartUtil.getFormatedData(curVal, _cdm.getPriceFormat()));
            //_cvm.drawString(gl, CoSys.WHITE, (int)xpos, (int)yp, String.format("%.2f", curVal));
            //2013.03.27 by LYH >> 스케일 가격 오른쪽 정렬
            //_cvm.drawString(gl, CoSys.WHITE, (int)xpos, (int)yp, curStr);
            int w = _cvm.GetTextLength(curStr);
            //2021.07.12 by hanjun.Kim - kakaopay - 가격표 좌측정렬 >>
            _cvm.drawScaleString(gl, CoSys.WHITE, (int)xpos+(int)COMUtil.getPixel(3), (int)yp, curStr, 1.0f);
            //2013.03.27 by LYH <<
            _cvm.useJipyoSign=false;
        }
        _cvm.setLineWidth(1);
    }
    //2015.01.08 by LYH >> 3일차트 추가 <<

    public void setDataType(int type){
        m_nDataType = type;
    }
    public void setAverageCalcType(int type){

        m_nAverageCalcType = type;
    }
    public int getDataType(){

        return m_nDataType;
    }
    public int getAverageCalcType(){

        return m_nAverageCalcType;
    }
    //2017.05.11 by LYH >> 전략(신호, 강약) 추가
    public void plotStrategy(Canvas gl,double[] data,double[] signalData){
        xfactor = _cvm.getXFactor(bounds.width());
        yfactor = (float)(((max_view-min_view)*1.0)/(max_data-min_data));
        setDrawGab();
        if(isVisible){
            drawTitle(gl);
            drawStrategy(gl,data,signalData);
        }
    }
    public void drawStrategy(Canvas gl,double[] data,double[] signalData){
        if(data==null||data.length<1)return;

        int startIndex = _cvm.getIndex();
        int startPos=startIndex;
        if(startPos<0)
            startPos = 0;
        float xpos=0;
        int dataLen = startIndex + _cvm.getViewNum() + _cvm.futureMargin;
        if(dataLen>data.length)
            dataLen= data.length;
        xpos=getBounds().left+xw;
        for(int i=startPos;i<dataLen;i++){
            if(dataLen>i && data[i]!=0){
                startPos=i;
                xpos=(int)(xpos+((i-startIndex)*xfactor));
                break;
            }
        }

        if(startPos >= data.length)
            return;

        float y = (float)data[startPos];
        float y_signal = (float)signalData[startPos];

        double[] highData = _cdm.getSubPacketData("고가");
        double[] lowData = _cdm.getSubPacketData("저가");

        if(highData != null) {

            float yPos;
            AREA area;  //2020.07.06 by LYH >> 캔들볼륨

            for (int i = startPos + 1; i < dataLen; i++) {
                float y1 = (float)data[i];
                float y1_signal = (float)signalData[i];


                if (y<=y_signal && y1>y1_signal && isUpVisible) {
                    float x = xpos + xfactor;
                    //2020.07.06 by LYH >> 캔들볼륨 >>
                    area = _cvm.getArea(i-startIndex);
                    if(area!=null)
                    {
                        x = area.getCenter();
                    }
                    //2020.07.06 by LYH >> 캔들볼륨 <<
                    yPos = calcy(lowData[i]);
                    //_cvm.drawLine(gl, x, y1+COMUtil.getPixel(4), x, y1+COMUtil.getPixel(7), CoSys.CHART_COLORS[0] ,1.0f);
                    //_cvm.drawLine(gl, x-COMUtil.getPixel(1), y1+COMUtil.getPixel(4), x-COMUtil.getPixel(1), y1+COMUtil.getPixel(7), CoSys.CHART_COLORS[0] ,1.0f);
//                    _cvm.drawFillRect(gl, x - COMUtil.getPixel(1), yPos + COMUtil.getPixel(4), COMUtil.getPixel(2), COMUtil.getPixel(3), upColor, 1.0f);
//                    _cvm.drawFillTriangle(gl, x - COMUtil.getPixel(3), yPos + COMUtil.getPixel(2), COMUtil.getPixel(6), (int) COMUtil.getPixel(3), upColor);
                    _cvm.drawFillRect(gl, x - COMUtil.getPixel((line_thick+4)/2), yPos + COMUtil.getPixel(4+(line_thick*2)), COMUtil.getPixel(line_thick+2), COMUtil.getPixel(3+line_thick), upColor, 1.0f);
                    _cvm.drawFillTriangle(gl, x - COMUtil.getPixel((line_thick+3)), yPos + COMUtil.getPixel(2), COMUtil.getPixel(5 + line_thick*2) , (int) COMUtil.getPixel(3 + line_thick*2), upColor);



                }

                if (y>=y_signal && data[i]<signalData[i] && isDownVisible) {
                    float x = xpos + xfactor;
                    //2020.07.06 by LYH >> 캔들볼륨 >>
                    area = _cvm.getArea(i-startIndex);
                    if(area!=null)
                    {
                        x = area.getCenter();
                    }
                    //2020.07.06 by LYH >> 캔들볼륨 <<
                    yPos = calcy(highData[i]);
                    //_cvm.drawLine(gl, x, y1-COMUtil.getPixel(5), x, y1-COMUtil.getPixel(8), CoSys.CHART_COLORS[1] ,1.0f);
                    //_cvm.drawLine(gl, x-COMUtil.getPixel(1), y1-COMUtil.getPixel(5), x-COMUtil.getPixel(1), y1-COMUtil.getPixel(8), CoSys.CHART_COLORS[1] ,1.0f);
//                    _cvm.drawFillRect(gl, x - COMUtil.getPixel(1), yPos - COMUtil.getPixel(8), COMUtil.getPixel(2), COMUtil.getPixel(3), downColor, 1.0f);
//                    _cvm.drawFillTriangle(gl, x - COMUtil.getPixel(3), yPos - COMUtil.getPixel(5), COMUtil.getPixel(6), (int) COMUtil.getPixel(-3), downColor);
                    _cvm.drawFillRect(gl, x - COMUtil.getPixel((m_nDownThick+4)/2), yPos - COMUtil.getPixel(7+m_nDownThick*2+m_nDownThick), COMUtil.getPixel(m_nDownThick+2), COMUtil.getPixel(3+m_nDownThick), downColor, 1.0f);
                    _cvm.drawFillTriangle(gl, x - COMUtil.getPixel(m_nDownThick+3), yPos - COMUtil.getPixel(5+m_nDownThick*2), COMUtil.getPixel(5+m_nDownThick*2), (int) COMUtil.getPixel(-3-m_nDownThick*2), downColor);


                }

                y = y1;
                y_signal = y1_signal;
                xpos += xfactor;
            }
        }
    }
    public void plotStrategy(Canvas gl,double[] data,double dBaseData, boolean bUp){
        xfactor = _cvm.getXFactor(bounds.width());
        yfactor = (float)(((max_view-min_view)*1.0)/(max_data-min_data));
        setDrawGab();
        if(isVisible){
            drawTitle(gl);
            drawStrategy(gl,data,dBaseData,bUp);
        }
    }
    public void plotStrategy(Canvas gl,double[] data, double[] dataShort, double[] dataMid, double[] dataLong){
        xfactor = _cvm.getXFactor(bounds.width());
        yfactor = (float)(((max_view-min_view)*1.0)/(max_data-min_data));
        setDrawGab();
        if(isVisible){
            drawTitle(gl);
            drawStrategy(gl,data,dataShort,dataMid,dataLong);
        }
    }
    public void drawStrategy(Canvas gl,double[] data,double dBaseData, boolean bUp){
        if(data==null||data.length<1)return;

        if((!bUp && !isUpVisible) || (bUp && !isDownVisible))
            return;

        int startIndex = _cvm.getIndex();
        int startPos=startIndex;
        if(startPos<0)
            startPos = 0;
        float xpos=0;
        int dataLen = startIndex + _cvm.getViewNum() + _cvm.futureMargin;
        if(dataLen>data.length)
            dataLen= data.length;
        xpos=getBounds().left+xw;
        for(int i=startPos;i<dataLen;i++){
            if(dataLen>i && data[i]!=0){
                startPos=i;
                xpos=(int)(xpos+((i-startIndex)*xfactor));
                break;
            }
        }

        if(startPos >= data.length)
            return;

        float y = (float)data[startPos];
        float y_signal = (float)dBaseData;

        double[] highData = _cdm.getSubPacketData("고가");
        double[] lowData = _cdm.getSubPacketData("저가");

        if(highData != null) {

            float yPos;
            AREA area;  //2020.07.06 by LYH >> 캔들볼륨
            for (int i = startPos + 1; i < dataLen; i++) {
                float y1 = (float)data[i];
                float y1_signal = (float)dBaseData;

                if(bUp && y<=y_signal && y1>y1_signal){
                    float x = xpos + xfactor;
                    //2020.07.06 by LYH >> 캔들볼륨 >>
                    area = _cvm.getArea(i-startIndex);
                    if(area!=null)
                    {
                        x = area.getCenter();
                    }
                    //2020.07.06 by LYH >> 캔들볼륨 <<
                    yPos = calcy(lowData[i]);
                    //_cvm.drawLine(gl, x, y1+COMUtil.getPixel(4), x, y1+COMUtil.getPixel(7), CoSys.CHART_COLORS[0] ,1.0f);
                    //_cvm.drawLine(gl, x-COMUtil.getPixel(1), y1+COMUtil.getPixel(4), x-COMUtil.getPixel(1), y1+COMUtil.getPixel(7), CoSys.CHART_COLORS[0] ,1.0f);
//                    _cvm.drawFillRect(gl, x - COMUtil.getPixel(1), yPos + COMUtil.getPixel(5), COMUtil.getPixel(2), COMUtil.getPixel(3), upColor, 1.0f);
//                    _cvm.drawFillTriangle(gl, x - COMUtil.getPixel(3), yPos + COMUtil.getPixel(2), COMUtil.getPixel(6), (int) COMUtil.getPixel(3), upColor);
                    _cvm.drawFillRect(gl, x - COMUtil.getPixel((line_thick+4)/2), yPos + COMUtil.getPixel(4+(line_thick*2)), COMUtil.getPixel(1+line_thick), COMUtil.getPixel(3+line_thick), upColor, 1.0f);
                    _cvm.drawFillTriangle(gl, x - COMUtil.getPixel((line_thick+4)), yPos + COMUtil.getPixel(1), COMUtil.getPixel(5 + line_thick*2) , (int) COMUtil.getPixel(3 + line_thick*2), upColor);

                }

                if (!bUp && y>=y_signal && data[i]<dBaseData) {
                    float x = xpos + xfactor;
                    //2020.07.06 by LYH >> 캔들볼륨 >>
                    area = _cvm.getArea(i-startIndex);
                    if(area!=null)
                    {
                        x = area.getCenter();
                    }
                    //2020.07.06 by LYH >> 캔들볼륨 <<
                    yPos = calcy(highData[i]);
                    //_cvm.drawLine(gl, x, y1-COMUtil.getPixel(5), x, y1-COMUtil.getPixel(8), CoSys.CHART_COLORS[1] ,1.0f);
                    //_cvm.drawLine(gl, x-COMUtil.getPixel(1), y1-COMUtil.getPixel(5), x-COMUtil.getPixel(1), y1-COMUtil.getPixel(8), CoSys.CHART_COLORS[1] ,1.0f);
                    _cvm.drawFillRect(gl, x - COMUtil.getPixel((m_nDownThick+4)/2), yPos - COMUtil.getPixel(7+m_nDownThick*2+m_nDownThick), COMUtil.getPixel(m_nDownThick+2), COMUtil.getPixel(3+m_nDownThick), downColor, 1.0f);
                    _cvm.drawFillTriangle(gl, x - COMUtil.getPixel(m_nDownThick+3), yPos - COMUtil.getPixel(5+m_nDownThick*2), COMUtil.getPixel(5+m_nDownThick*2), (int) COMUtil.getPixel(-3-m_nDownThick*2), downColor);
                    //_cvm.drawFillRect(gl, x - COMUtil.getPixel((line_thick+4)/2), yPos - COMUtil.getPixel(5+line_thick*2), COMUtil.getPixel(1+line_thick), COMUtil.getPixel(-3-line_thick*2), downColor, 1.0f);
                   // _cvm.drawFillTriangle(gl, x - COMUtil.getPixel(line_thick+4), yPos - COMUtil.getPixel(5-line_thick*2), COMUtil.getPixel(5+line_thick*2), (int) COMUtil.getPixel(-3-line_thick*2), downColor);

                }

                y = y1;
                y_signal = y1_signal;
                xpos += xfactor;
            }
        }
    }

    public void drawStrategy(Canvas gl,double[] baseData, double[] signalDataShort, double[] signalDataMid, double[] signalDataLong){
        if(baseData==null||baseData.length<1)return;

        int startIndex = _cvm.getIndex();
        int startPos=startIndex;
        if(startPos<0)
            startPos = 0;
        float xpos=0;
        int dataLen = startIndex + _cvm.getViewNum() + _cvm.futureMargin;
        if(dataLen>baseData.length)
            dataLen= baseData.length;
        xpos=getBounds().left+xw;
        for(int i=startPos;i<dataLen;i++){
            if(dataLen>i && baseData[i]!=0){
                startPos=i;
                xpos=(int)(xpos+((i-startIndex)*xfactor));
                break;
            }
        }

        if(startPos >= baseData.length)
            return;

        float y = (float)baseData[startPos];
        double y_signalShort = signalDataShort[startPos];
        double y_signalMid = signalDataMid[startPos];
        double y_signalLong = signalDataLong[startPos];

        double[] highData = _cdm.getSubPacketData("고가");
        double[] lowData = _cdm.getSubPacketData("저가");

        if(highData != null) {

            float yPos;

            for (int i = startPos + 1; i < dataLen; i++) {
                float y1 = (float)baseData[i];
                double y1_signalShort=signalDataShort[i];
                double y1_signalMid=signalDataMid[i];
                double y1_signalLong=signalDataLong[i];

                if (y<=y_signalShort && y1>y1_signalShort && y<=y_signalMid && y1>y1_signalMid && y<=y_signalLong && y1>y1_signalLong && isUpVisible) {
                    int x = (int) (xpos + xfactor);
                    yPos = calcy(lowData[i]);
                    _cvm.drawFillRect(gl, x - COMUtil.getPixel((line_thick+4)/2), yPos + COMUtil.getPixel(4+(line_thick*2)), COMUtil.getPixel(line_thick+2), COMUtil.getPixel(3+line_thick), upColor, 1.0f);
                    _cvm.drawFillTriangle(gl, x - COMUtil.getPixel((line_thick+3)), yPos + COMUtil.getPixel(2), COMUtil.getPixel(5 + line_thick*2) , (int) COMUtil.getPixel(3 + line_thick*2), upColor);
                }

                if (y>=y_signalShort && y1<y1_signalShort && y>=y_signalMid && y1<y_signalMid &&y>=y_signalLong && y1<y_signalLong && isDownVisible) {
                    int x = (int) (xpos + xfactor);
                    yPos = calcy(highData[i]);
                    _cvm.drawFillRect(gl, x - COMUtil.getPixel((m_nDownThick+4)/2), yPos - COMUtil.getPixel(7+m_nDownThick*2+m_nDownThick), COMUtil.getPixel(m_nDownThick+2), COMUtil.getPixel(3+m_nDownThick), downColor, 1.0f);
                    _cvm.drawFillTriangle(gl, x - COMUtil.getPixel(m_nDownThick+3), yPos - COMUtil.getPixel(5+m_nDownThick*2), COMUtil.getPixel(5+m_nDownThick*2), (int) COMUtil.getPixel(-3-m_nDownThick*2), downColor);
                }

                y=y1;
                y_signalShort=y1_signalShort;
                y_signalMid=y1_signalMid;
                y_signalLong=y1_signalLong;
                xpos += xfactor;
            }
        }
    }

    public void plotStrategyStrongWeak(Canvas gl,double[] data,double[] signalData){
        xfactor = _cvm.getXFactor(bounds.width());
        yfactor = (float)(((max_view-min_view)*1.0)/(max_data-min_data));
        setDrawGab();
        if(isVisible){
            drawTitle(gl);
            drawStrategyStrongWeak(gl,data,signalData);
        }
    }
    public void drawStrategyStrongWeak(Canvas gl,double[] data,double[] signalData){
        if(data==null||data.length<1)return;

        int startIndex = _cvm.getIndex();
        int startPos=startIndex;
        if(startPos<0)
            startPos = 0;
        float xpos=0;
        int dataLen = startIndex + _cvm.getViewNum() + _cvm.futureMargin;
        if(dataLen>data.length)
            dataLen= data.length;
        xpos=getBounds().left+xw;
        for(int i=startPos;i<dataLen;i++){
            if(dataLen>i && data[i]!=0){
                startPos=i;
                xpos=(int)(xpos+((i-startIndex)*xfactor));
                break;
            }
        }

        if(startPos >= data.length)
            return;

        float y = (float)data[startPos];
        float y_signal = (float)signalData[startPos];

        //2017.06.12 by LYH >> 전략 강약 중복 추가 기능 처리
        float fHeight = (getBounds().bottom-getBounds().top)/g_StrongWeekCount;
        float fStartY = getBounds().top + fHeight*g_StrongWeekIndex;
        //2017.06.12 by LYH >> 전략 강약 중복 추가 기능 처리 end

        double[] highData = _cdm.getSubPacketData("고가");
        double[] lowData = _cdm.getSubPacketData("저가");

        if(highData != null) {

            float yPos;

            float xStart=getBounds().left+xw;
            int[] components = null;
            AREA area;  //2020.07.06 by LYH >> 캔들볼륨
            for (int i = startPos + 1; i < dataLen; i++) {
                float y1 = (float)data[i];
                float y1_signal = (float)signalData[i];


                if (y<=y_signal && y1>y1_signal) {
                    float x = xpos + xfactor;
                    yPos = calcy(lowData[i]);
                    //2020.07.06 by LYH >> 캔들볼륨 >>
                    area = _cvm.getArea(i-startIndex);
                    if(area!=null)
                    {
                        x = area.getLeft();
                        if (isDownVisible)
                            _cvm.drawFillRect(gl, xStart, fStartY, x - xStart, fHeight, downColor, 0.1f);
                    }
                    //2020.07.06 by LYH >> 캔들볼륨 <<
                    else {
                        if (isDownVisible)
                            _cvm.drawFillRect(gl, xStart - xfactor / 2, fStartY, x - xStart, fHeight, downColor, 0.1f);
                    }
                    xStart = x;
                    components = upColor;
                }

                if (y>=y_signal && data[i]<signalData[i]) {
                    float x = xpos + xfactor;
                    yPos = calcy(highData[i]);
                    //2020.07.06 by LYH >> 캔들볼륨 >>
                    area = _cvm.getArea(i-startIndex);
                    if(area!=null)
                    {
                        x = area.getLeft();
                        if (isUpVisible)
                            _cvm.drawFillRect(gl, xStart, fStartY, x - xStart, fHeight, upColor, 0.1f);
                    }
                    //2020.07.06 by LYH >> 캔들볼륨 <<
                    else {
                        if (isUpVisible)
                            _cvm.drawFillRect(gl, xStart - xfactor / 2, fStartY, x - xStart, fHeight, upColor, 0.1f);
                    }
                    xStart = x;
                    components = downColor;
                }

                y = y1;
                y_signal = y1_signal;
                xpos += xfactor;
                if(i==dataLen-1)
                {
                    if(components==null)
                    {
                        if(y1<y1_signal)
                            components = downColor;
                        else
                            components = upColor;
                    }
                    if(components == upColor && isUpVisible || components == downColor && isDownVisible)
                    {
                        int x = (int) (xpos + xfactor);
                        //2020.07.06 by LYH >> 캔들볼륨 >>
                        area = _cvm.getArea(i-startIndex);
                        if(area!=null)
                        {
                            x = (int)area.getRight_Tot();
                            _cvm.drawFillRect(gl, xStart, fStartY, x-xStart, fHeight, components, 0.1f);
                        }
                        //2020.07.06 by LYH >> 캔들볼륨 <<
                        else {
                            _cvm.drawFillRect(gl, xStart - xfactor / 2, fStartY, x - xStart, fHeight, components, 0.1f);
                        }
                    }
                }
            }
        }
    }
    public void plotStrategyStrongWeak(Canvas gl,double[] data,double dBaseData, boolean bUp){
        xfactor = _cvm.getXFactor(bounds.width());
        yfactor = (float)(((max_view-min_view)*1.0)/(max_data-min_data));
        setDrawGab();
        if(isVisible){
            drawTitle(gl);
            drawStrategyStrongWeak(gl,data,dBaseData,bUp);
        }
    }
    public void drawStrategyStrongWeak(Canvas gl,double[] data,double dBaseData, boolean bUp){
        if(data==null||data.length<1)return;

        if((!bUp && !isUpVisible) || (bUp && !isDownVisible))
            return;

        int startIndex = _cvm.getIndex();
        int startPos=startIndex;
        if(startPos<0)
            startPos = 0;
        float xpos=0;
        int dataLen = startIndex + _cvm.getViewNum() + _cvm.futureMargin;
        if(dataLen>data.length)
            dataLen= data.length;
        xpos=getBounds().left+xw;
        for(int i=startPos;i<dataLen;i++){
            if(dataLen>i && data[i]!=0){
                startPos=i;
                xpos=(int)(xpos+((i-startIndex)*xfactor));
                break;
            }
        }

        if(startPos >= data.length)
            return;

        float y = (float)data[startPos];
        float y_signal = (float)dBaseData;

        //2017.06.12 by LYH >> 전략 강약 중복 추가 기능 처리
        float fHeight = (getBounds().bottom-getBounds().top)/g_StrongWeekCount;
        float fStartY = getBounds().top + fHeight*g_StrongWeekIndex;
        //2017.06.12 by LYH >> 전략 강약 중복 추가 기능 처리 end

        double[] highData = _cdm.getSubPacketData("고가");
        double[] lowData = _cdm.getSubPacketData("저가");

        if(highData != null) {

            float yPos;

            float xStart=getBounds().left+xw;
            int[] components = null;
            AREA area;  //2020.07.06 by LYH >> 캔들볼륨
            for (int i = startPos + 1; i < dataLen; i++) {
                float y1 = (float)data[i];
                float y1_signal = (float)dBaseData;

                if (bUp) {
                    if(y<=y_signal && y1>y1_signal) {
                        float x = xpos + xfactor;
                        yPos = calcy(lowData[i]);
                        //2020.07.06 by LYH >> 캔들볼륨 >>
                        area = _cvm.getArea(i-startIndex);
                        if(area!=null)
                        {
                            x = area.getLeft();
                            _cvm.drawFillRect(gl, xStart, fStartY, x - xStart, fHeight, downColor, 0.1f);
                        }
                        //2020.07.06 by LYH >> 캔들볼륨 <<
                        else {
                            _cvm.drawFillRect(gl, xStart - xfactor / 2, fStartY, x - xStart, fHeight, downColor, 0.1f);
                        }
                    }
                    else if(y>=y_signal && data[i]<dBaseData)
                    {
                        float x = xpos + xfactor;
                        //2020.07.06 by LYH >> 캔들볼륨 >>
                        area = _cvm.getArea(i-startIndex);
                        if(area!=null)
                        {
                            x = area.getLeft();
                        }
                        //2020.07.06 by LYH >> 캔들볼륨 <<
                        xStart = x;
                        components = downColor;
                    }
                }
                else {
                    if (y >= y_signal && data[i] < dBaseData) {
                        float x = xpos + xfactor;
                        yPos = calcy(highData[i]);
                        //2020.07.06 by LYH >> 캔들볼륨 >>
                        area = _cvm.getArea(i-startIndex);
                        if(area!=null)
                        {
                            x = area.getLeft();
                            _cvm.drawFillRect(gl, xStart, fStartY, x - xStart, fHeight, upColor, 0.1f);
                        }
                        //2020.07.06 by LYH >> 캔들볼륨 <<
                        else {
                            _cvm.drawFillRect(gl, xStart - xfactor / 2, fStartY, x - xStart, fHeight, upColor, 0.1f);
                        }
                    }
                    else if(y<=y_signal && y1>y1_signal)
                    {
                        float x = xpos + xfactor;
                        //2020.07.06 by LYH >> 캔들볼륨 >>
                        area = _cvm.getArea(i-startIndex);
                        if(area!=null)
                        {
                            x = area.getLeft();
                        }
                        //2020.07.06 by LYH >> 캔들볼륨 <<
                        xStart = x;
                        components = upColor;
                    }

                }

                y = y1;
                y_signal = y1_signal;
                xpos += xfactor;
                if(i==dataLen-1)
                {
                    if((bUp && data[i]<dBaseData) || (!bUp && y1>y1_signal)) {
                        if(components == null)
                        {
                            if(bUp)
                                components = downColor;
                            else
                                components = upColor;
                        }
                        int x = (int) (xpos + xfactor);
                        //2020.07.06 by LYH >> 캔들볼륨 >>
                        area = _cvm.getArea(i-startIndex);
                        if(area!=null)
                        {
                            x = (int)area.getRight_Tot();
                            _cvm.drawFillRect(gl, xStart, fStartY, x - xStart, fHeight, components, 0.1f);
                        }
                        //2020.07.06 by LYH >> 캔들볼륨 <<
                        else {
                            _cvm.drawFillRect(gl, xStart - xfactor / 2, fStartY, x - xStart, fHeight, components, 0.1f);
                        }
                    }
                }
            }
        }
    }
    //2017.05.11 by LYH >> 전략(신호, 강약) 추가 end

    public void plotStrategyStrongWeak(Canvas gl,double[] data, double[] dataShort, double[] dataMid, double[] dataLong){
        xfactor = _cvm.getXFactor(bounds.width());
        yfactor = (float)(((max_view-min_view)*1.0)/(max_data-min_data));
        setDrawGab();
        if(isVisible){
            drawTitle(gl);
            drawStrategyStrongWeak(gl,data,dataShort,dataMid,dataLong);
        }
    }

    public void drawStrategyStrongWeak(Canvas gl,double[] baseData, double[] signalDataShort, double[] signalDataMid, double[] signalDataLong){
        if(baseData==null||baseData.length<1)return;

        int startIndex = _cvm.getIndex();
        int startPos=startIndex;
        if(startPos<0)
            startPos = 0;
        float xpos=0;
        int dataLen = startIndex + _cvm.getViewNum() + _cvm.futureMargin;
        if(dataLen>baseData.length)
            dataLen= baseData.length;
        xpos=getBounds().left+xw;
        for(int i=startPos;i<dataLen;i++){
            if(dataLen>i && baseData[i]!=0){
                startPos=i;
                xpos=(int)(xpos+((i-startIndex)*xfactor));
                break;
            }
        }

        if(startPos >= baseData.length)
            return;

        float y = (float)baseData[startPos];
        double y_signalShort = signalDataShort[startPos];
        double y_signalMid = signalDataMid[startPos];
        double y_signalLong = signalDataLong[startPos];

        //2017.06.12 by LYH >> 전략 강약 중복 추가 기능 처리
        float fHeight = (getBounds().bottom-getBounds().top)/g_StrongWeekCount;
        float fStartY = getBounds().top + fHeight*g_StrongWeekIndex;
        //2017.06.12 by LYH >> 전략 강약 중복 추가 기능 처리 end

        double[] highData = _cdm.getSubPacketData("고가");
        double[] lowData = _cdm.getSubPacketData("저가");

        if(highData != null) {

            float yPos;

            float xStart=getBounds().left+xw;
            int[] components = null;
            AREA area;  //2020.07.06 by LYH >> 캔들볼륨
            for (int i = startPos + 1; i < dataLen; i++) {
                float y1 = (float)baseData[i];
                double y1_signalShort=signalDataShort[i];
                double y1_signalMid=signalDataMid[i];
                double y1_signalLong=signalDataLong[i];

                //2020.07.28 by JJH >> 멀티 이평크로스 강세약세에서 불필요한 조건 변수 수정(매수,매도) start
//                if (y<=y_signalShort && y1>y1_signalShort && y<=y_signalMid && y1>y1_signalMid && y<=y_signalLong && y1>y1_signalLong && isUpVisible)
                if (y<=y_signalShort && y1>y1_signalShort && y<=y_signalMid && y1>y1_signalMid && y<=y_signalLong && y1>y1_signalLong) {
                //2020.07.28 by JJH >> 멀티 이평크로스 강세약세에서 불필요한 조건 변수 수정(매수,매도) end
                    float x = (int) (xpos + xfactor);
                    yPos = calcy(lowData[i]);
                    if(isDownVisible) {
                        //2020.07.06 by LYH >> 캔들볼륨 >>
                        area = _cvm.getArea(i-startIndex);
                        if(area!=null)
                        {
                            x = area.getLeft();
                            _cvm.drawFillRect(gl, xStart, fStartY, x - xStart, fHeight, downColor, 0.1f);
                        }
                        //2020.07.06 by LYH >> 캔들볼륨 <<
                        else {
                            _cvm.drawFillRect(gl, xStart - xfactor / 2, fStartY, x - xStart, fHeight, downColor, 0.1f);
                        }
                    }
                    xStart = x;
                    components = upColor;
                }

                if (y>=y_signalShort && y1<y1_signalShort && y>=y_signalMid && y1<y_signalMid &&y>=y_signalLong && y1<y_signalLong) {
                    float x = (int) (xpos + xfactor);
                    yPos = calcy(highData[i]);

                    if(isUpVisible) {
                        //2020.07.06 by LYH >> 캔들볼륨 >>
                        area = _cvm.getArea(i-startIndex);
                        if(area!=null)
                        {
                            x = area.getLeft();
                            _cvm.drawFillRect(gl, xStart, fStartY, x - xStart, fHeight, upColor, 0.1f);
                        }
                        //2020.07.06 by LYH >> 캔들볼륨 <<
                        else {
                            _cvm.drawFillRect(gl, xStart - xfactor / 2, fStartY, x - xStart, fHeight, upColor, 0.1f);
                        }
                    }
                    xStart = x;
                    components = downColor;
                }

                y=y1;
                y_signalShort=y1_signalShort;
                y_signalMid=y1_signalMid;
                y_signalLong=y1_signalLong;
                xpos += xfactor;

                if(i==dataLen-1)
                {
                    if(components==null)
                    {
                        //2020.07.28 by JJH >> 멀티 이평크로스 강세약세에서 신호가 없을 경우 아무것도 색을 입히지 않도록 수정 start
                        return;
//                        if(y1<y1_signalShort)
//                            components = downColor;
//                        else
//                            components = upColor;
                        //2020.07.28 by JJH >> 멀티 이평크로스 강세약세에서 신호가 없을 경우 아무것도 색을 입히지 않도록 수정 end
                    }
                    if(components == upColor && isUpVisible || components == downColor && isDownVisible)
                    {
                        int x = (int) (xpos + xfactor);
                        //2020.07.06 by LYH >> 캔들볼륨 >>
                        area = _cvm.getArea(i-startIndex);
                        if(area!=null)
                        {
                            x = (int)area.getRight_Tot();
                            _cvm.drawFillRect(gl, xStart, fStartY, x - xStart, fHeight, components, 0.1f);
                        }
                        //2020.07.06 by LYH >> 캔들볼륨 <<
                        else {
                            _cvm.drawFillRect(gl, xStart - xfactor / 2, fStartY, x - xStart, fHeight, components, 0.1f);
                        }
                    }
                }
            }
        }
    }

    //2017.05.11 by LYH >> 전략(신호, 강약) 추가
    public int[] getDefDownColor(){
        return def_downColor;
    }
    public void setIsUpVisible(boolean b){
        isUpVisible= b;
    }

    public boolean isUpVisible() {
        return isUpVisible;
    }
    public void setIsDownVisible(boolean b){
        isDownVisible=b;
    }
    public boolean isDownVisible() {
        return isDownVisible;
    }
    public void setDownLineT(int t){
        m_nDownThick = t;//라인굵기 설정
    }
    public int getDownLineT(){
        return m_nDownThick;
    }
    //2017.05.11 by LYH >> 전략(신호, 강약) 추가 end

    //2019. 01. 12 by hyh - 블록병합 처리. 지표 이동 >>
    public boolean isSelectedPoint(Point p, int index){
        if(data==null || !isVisible()) return false;
        int idx= _cvm.getIndex();
        int curIndex = index-idx;
        if(curIndex >= data.length||curIndex<0) return false;
        float curY = calcy(data[curIndex]);
        if( (p.y>curY-5) && (p.y<curY+5 ))
            return true;

        int preIndex=curIndex-1, nextIndex=curIndex+1;
        if(preIndex<0 ) preIndex = 0;
        if(nextIndex >= data.length) nextIndex = data.length-1;
        float preY = calcy(data[preIndex]);
        float nextY = calcy(data[nextIndex]);

        Rect rect;
        if(preY>curY){
            rect = new Rect((int)(xfactor*preIndex+xfactor/2), (int)curY, (int)xfactor, (int)(preY-curY));
        }else{
            rect = new Rect((int)(xfactor*preIndex+xfactor/2), (int)preY, (int)xfactor, (int)(curY-preY));
        }
        if(rect.contains(p.x, p.y)) return true;

        if(curY>nextY){
            rect = new Rect((int)(xfactor*curIndex+xfactor/2), (int)nextY, (int)xfactor, (int)(curY-nextY));
        }else{
            rect = new Rect((int)(xfactor*curIndex+xfactor/2), (int)curY, (int)xfactor, (int)(nextY-curY));
        }
        if(rect.contains(p.x, p.y)) return true;
        return false;
    }
    //2019. 01. 12 by hyh - 블록병합 처리. 지표 이동 <<

    //2011.09.27 by metalpooh  >> 기준선 추가
    public void drawBaseLine(Canvas gl) {
        //2013. 3. 6 기준선 다중선택
//  		System.out.println("gijunChartSetting drawBaseLine length: " + COMUtil.baseLineType.size());
//  		NeoChart2 chart = (NeoChart2)COMUtil._mainFrame.mainBase.baseP._chart;
        for(int type : _cvm.baseLineType)
        {
//  			System.out.println("gijunChartSetting drawBaseLine : " + String.valueOf(type));

            String baseLabel = "";
            int baseType = type;
            if(baseType < 1 || baseType > 12)
            {
                return;
            }
//  			String[] todayData = _cdm.getDatas(_cdm.getCount()-1);
            String[] todayData = new String[5];
            if(Double.parseDouble(_cdm.codeItem.strGiOpen) == 0 ||_cdm.codeItem.strGiOpen == null || _cdm.codeItem.strGiOpen.equals("") || _cdm.codeItem.strGiHigh.equals("") || _cdm.codeItem.strGiLow.equals("") || _cdm.codeItem.strPrice.equals("")) {
                todayData = _cdm.getDatas(_cdm.getCount()-1);
            } else {
                todayData[1] = _cdm.codeItem.strGiOpen;
                todayData[2] = _cdm.codeItem.strGiHigh;
                todayData[3] = _cdm.codeItem.strGiLow;
                todayData[4] = _cdm.codeItem.strPrice;
            }

//  			System.out.println("DEBUG_cdm.codeItem.strGiClose:"+_cdm.codeItem.strGiClose);

            String[] preDayData = null;
            //0,1,2,3,4,5(틱,분,일,주,월,년)
            //일봉에서만 전일종가 기준선을 그린다.
            String period = _cdm.codeItem.strDataType;
            if(period.equals("2"))
                preDayData = _cdm.getDatas(_cdm.getCount()-2);

            //2017.09.20 by pjm 전일 시고저종 안나오는 오류 수정 >>
            if(_cdm.codeItem.strGiPreOpen != null && !_cdm.codeItem.strGiPreOpen.equals("") && !_cdm.codeItem.strGiPreHigh.equals("") && !_cdm.codeItem.strGiPreLow.equals("") &&  !_cdm.codeItem.strGiPreClose.equals("")) {
                preDayData = new String[5];
                preDayData[1] = _cdm.codeItem.strGiPreOpen;
                preDayData[2] = _cdm.codeItem.strGiPreHigh;
                preDayData[3] = _cdm.codeItem.strGiPreLow;
                preDayData[4] = _cdm.codeItem.strGiPreClose;
            }
            //2017.09.20 by pjm 전일 시고저종 안나오는 오류 수정 <<

            float y=0;
            int colIndex=0;
            double tvalue = 0;

            //당일기준
            if (baseType==1) {
                tvalue = Double.parseDouble(todayData[1]);
                baseLabel="시가";
                colIndex=0;
            }else if(baseType==2) {
                tvalue = Double.parseDouble(todayData[2]);
                baseLabel="고가";
                colIndex=1;
            }else if(baseType==3) {
                tvalue = Double.parseDouble(todayData[3]);
                baseLabel="저가";
                colIndex=2;
            }else if(baseType==4) {
                tvalue = Double.parseDouble(todayData[4]);
                baseLabel="현재가";
                colIndex=3;
            }else if(baseType==5) { //상한가
                try{
                    tvalue = Double.parseDouble(_cdm.codeItem.strHighest);
                    baseLabel="상한가";
                    colIndex=4;
                }catch(Exception e){}
            }else if(baseType==6) { //하한가
                try{
                    tvalue = Double.parseDouble(_cdm.codeItem.strLowest);
                    baseLabel="하한가";
                    colIndex=5;
                }catch(Exception e){}
            }else if(baseType==7) { //시,고,저 / 3
                try{
                    double dOpen = Double.parseDouble(todayData[1]);
                    double dHigh = Double.parseDouble(todayData[2]);
                    double dLow = Double.parseDouble(todayData[3]);

                    tvalue = (dOpen+dHigh+dLow)/3;

                    baseLabel="시고저/3";
                    colIndex=6;
                }catch(Exception e){}
            }else if(baseType==8) { //전일시가
                if(preDayData==null)
                    return;
                try{
                    tvalue = Double.parseDouble(preDayData[1]);
                    baseLabel="전일시가";
                    colIndex=7;
                }catch(Exception e){}
            }else if(baseType==9) { //전일고가
                if(preDayData==null)
                    return;
                try{
                    tvalue = Double.parseDouble(preDayData[2]);
                    baseLabel="전일고가";
                    colIndex=8;
                }catch(Exception e){}
            }else if(baseType==10) { //전일저가
                if(preDayData==null)
                    return;
                try{
                    tvalue = Double.parseDouble(preDayData[3]);
                    baseLabel="전일저가";
                    colIndex=9;
                }catch(Exception e){}
            }else if(baseType==11) { //전일종가
                if (preDayData == null)
                    return;
                try {
                    tvalue = Double.parseDouble(preDayData[4]);
                    baseLabel = "전일종가";
                    colIndex = 10;
                } catch (Exception e) {
                }
            } else if(baseType == 12) {	//전일시고저/3
                if (preDayData == null)
                    return;
                try {
                    double dOpen = Double.parseDouble(preDayData[1]);
                    double dHigh = Double.parseDouble(preDayData[2]);
                    double dLow = Double.parseDouble(preDayData[3]);

                    tvalue = (dOpen+dHigh+dLow)/3;


                    baseLabel = "전일시고저/3";
                    colIndex = 11;
                } catch (Exception e) {
                }

            }  else  {
                return; //Not draw!
            }

            y= calcy(tvalue);

            //영역 체크
            RectF bounds = getBounds();
            if (y<bounds.top || y>bounds.top+bounds.height()) {
//	  			return;
                continue;
            }

            // 2016.05.31 기준선 대비, 색상 굵기 >>
            int[] color = {0,0,0};
            if(_cvm.baseLineColors.size() != 0){
                String strColor = _cvm.baseLineColors.get(colIndex);
                String[] strColors = strColor.split("=");
                for(int i = 0 ; i < strColors.length ; i++){
                    color[i] = Integer.parseInt(strColors[i]);
                }

            }else{
                color = CoSys.CHART_COLORS[colIndex];
            }

            int nThick = (int)COMUtil.graphLineWidth;
            if(_cvm.baseLineThicks.size() != 0){
                nThick = _cvm.baseLineThicks.get(colIndex);
            }

            _cvm.setLineWidth(nThick);
            // 2016.05.31 기준선 대비, 색상 굵기 <<


            String strBaseLabel = baseLabel + " (" + ChartUtil.getFormatedData(tvalue, _cdm.getPriceFormat(), _cdm) + ")";
            //drawLine
//			_cvm.drawLine(gl, bounds.left,y,bounds.width(),y, CoSys.CHART_COLORS[colIndex] ,1.0f);
            _cvm.drawLine(gl, bounds.left,y,bounds.width()+ (int)COMUtil.getPixel(COMUtil.getPaddingRight() + 2),y, color ,1.0f); // 2016.05.31 기준선 대비, 색상 굵기
            //2012. 8. 13   기준선그렸을때 라벨 뒤 배경길이가 글씨길이보다 작은 현상 수정 : C13
//			_cvm.drawFillRect(gl, bounds.left,y+1,_cvm.GetTextLength(baseLabel)+(int)COMUtil.getPixel(4),COMUtil.getPixel(14), CoSys.CHART_COLORS[colIndex], 1.0f);
            _cvm.drawFillRect(gl, bounds.left,y+1,_cvm.GetTextLength(strBaseLabel)+(int)COMUtil.getPixel(4),COMUtil.getPixel(14), color, 1.0f); // 2016.05.31 기준선 대비, 색상 굵기
            _cvm.drawString(gl, CoSys.CHART_BACK_COLOR[1], bounds.left+(int)COMUtil.getPixel(2), (int)(y +COMUtil.getPixel(8)), strBaseLabel);

//            _cvm.drawFillRect(gl,bounds.right + COMUtil.getPixel(COMUtil.getPaddingRight()), y-COMUtil.getPixel(7) , _cvm.Margin_R ,COMUtil.getPixel(14), color,1.0f);
//            _cvm.drawString(gl, CoSys.CHART_BACK_COLOR[1], bounds.right + (int) COMUtil.getPixel(COMUtil.getPaddingRight() + 2), (int) y, ChartUtil.getFormatedData(tvalue, _cdm.getPriceFormat(), _cdm));
        }
    }
    //2011.09.27 by metalpooh <<

    //2019.04. 03 by lyj - Pivot, Demark 기준선 추가
    public void drawPivotDemarkLine(Canvas gl){

        for(int type : _cvm.baseLineType)
        {
            String baseLabel = "";
            int baseType = type;
            if(baseType < 1 )
            {
                return;
            }

            String[] todayData = new String[5];

            if(_cdm.codeItem.strGiOpen == null ||  _cdm.codeItem.strGiOpen.equals("") || _cdm.codeItem.strGiHigh.equals("") || _cdm.codeItem.strGiLow.equals("") || _cdm.codeItem.strPrice.equals("")) {
                todayData = _cdm.getDatas(_cdm.getCount()-1);
            } else {
                // 2021.05. 11  by hanjun.Kim - kakaopay 방어코드 작성 >>
                // _cdm.codeItem.strGiOpen double형으로 형변환할때 빈문자값일경우 에러 수정.
                Double giopen = Double.valueOf(0);
                try {
                    giopen = Double.parseDouble(_cdm.codeItem.strGiOpen);
                } catch (Exception e) {
                    todayData = _cdm.getDatas(_cdm.getCount()-1);
                }

                if ( giopen ==  Double.valueOf(0)) {
                    todayData = _cdm.getDatas(_cdm.getCount()-1);
                } else {
                    todayData[1] = _cdm.codeItem.strGiOpen;
                    todayData[2] = _cdm.codeItem.strGiHigh;
                    todayData[3] = _cdm.codeItem.strGiLow;
                    todayData[4] = _cdm.codeItem.strPrice;
                }
                // 2021.05. 11  by hanjun.Kim - kakaopay 방어코드 작성 <<
            }

            String[] preDayData = null;
            //0,1,2,3,4,5(틱,분,일,주,월,년)
            //일봉에서만 전일종가 기준선을 그린다.
            //2021.09.29 by lyk - kakaopay - 지지선/저항선 주기 상관없이 모두 그리기 >>
//            String period = _cdm.codeItem.strDataType;
//            if(period.equals("2"))
            //2021.09.29 by lyk - kakaopay - 지지선/저항선 주기 상관없이 모두 그리기 <<

                preDayData = _cdm.getDatas(_cdm.getCount()-2);

            //2017.09.20 by pjm 전일 시고저종 안나오는 오류 수정 >>
            if(_cdm.codeItem.strGiPreOpen != null && !_cdm.codeItem.strGiPreOpen.equals("") && !_cdm.codeItem.strGiPreHigh.equals("") && !_cdm.codeItem.strGiPreLow.equals("") &&  !_cdm.codeItem.strGiPreClose.equals("")) {
                preDayData = new String[5];
                preDayData[1] = _cdm.codeItem.strGiPreOpen;
                preDayData[2] = _cdm.codeItem.strGiPreHigh;
                preDayData[3] = _cdm.codeItem.strGiPreLow;
                preDayData[4] = _cdm.codeItem.strGiPreClose;
            }
            //2017.09.20 by pjm 전일 시고저종 안나오는 오류 수정 <<

            float y=0;
            int colIndex=0;
            double dValue = 0;

            double dPreOpen = 0;
            double dPreHigh = 0;
            double dPreLow = 0;
            double dPreClose = 0;

            try {
                dPreOpen = Double.parseDouble(preDayData[1]);
                dPreHigh = Double.parseDouble(preDayData[2]);
                dPreLow = Double.parseDouble(preDayData[3]);
                dPreClose = Double.parseDouble(preDayData[4]);
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            if(baseType <18) {		//Pivot
                double dPivotStandard = (dPreClose + dPreHigh +dPreLow) / 3;

                if (baseType == 13) {
                    dValue = dPivotStandard + dPreHigh - dPreLow;
                    baseLabel = "2차 저항";
                    colIndex = 0;

                } else if (baseType == 14) {
                    dValue = dPivotStandard * 2 - dPreLow;
                    baseLabel = "1차 저항";
                    colIndex = 11;
                } else if (baseType == 15) {
                    dValue = dPivotStandard;
                    baseLabel = "기준선";
                    colIndex = 7;
                } else if (baseType == 16) {
                    dValue = dPivotStandard * 2 - dPreHigh;
                    baseLabel = "1차 지지";
                    colIndex = 1;
                } else if (baseType == 17) {
                    dValue = dPivotStandard - dPreHigh + dPreLow;
                    baseLabel = "2차 지지";
                    colIndex = 12;
                }
            }else {			//Demark

                double dDemarkStandard = 0;

                //2019. 04. 25 by hyh - Demark 계산식을 기존 지표와 동일하게 변경 >>
                if (dPreOpen < dPreClose) // 양봉일때
                    dDemarkStandard = (dPreHigh * 2 + dPreLow + dPreClose) / 2;
                else if (dPreOpen > dPreClose) // 음봉일때
                    dDemarkStandard = (dPreHigh + dPreLow * 2 + dPreClose) / 2;
                else
                    dDemarkStandard = (dPreHigh + dPreLow + dPreClose * 2) / 2;

                //if (dPreOpen < dPreClose)
                //	dDemarkStandard = (dPreOpen + dPreHigh * 2 + dPreClose) / 2;
                //else if (dPreOpen > dPreClose)
                //	dDemarkStandard = (dPreOpen + dPreHigh + dPreLow + dPreClose) / 2;
                //else
                //	dDemarkStandard = (dPreOpen + dPreHigh + dPreClose * 2) / 2;
                //2019. 04. 25 by hyh - Demark 계산식을 기존 지표와 동일하게 변경 <<

                if (baseType == 18) {
                    dValue = dDemarkStandard - dPreLow;
                    baseLabel = "목표 고가";
                    colIndex = 17;
                }
                else if (baseType == 19) {
                    dValue = dDemarkStandard - (dPreHigh + dPreLow) / 2;
                    baseLabel = "기준선";
                    colIndex = 18;
                }
                else if (baseType == 20) {
                    dValue = dDemarkStandard - dPreHigh;
                    baseLabel = "목표 저가";
                    colIndex = 19;
                }
            }

            y= calcy(dValue);

            //영역 체크
            RectF bounds = getBounds();
            if (y<bounds.top || y>bounds.top+bounds.height()) {
//	  			return;
                continue;
            }

            // 2016.05.31 기준선 대비, 색상 굵기 >>
            int[] color = {0,0,0};
            if(_cvm.baseLineColors.size() != 0){
                String strColor = _cvm.baseLineColors.get(colIndex);
                String[] strColors = strColor.split("=");
                for(int i = 0 ; i < strColors.length ; i++){
                    color[i] = Integer.parseInt(strColors[i]);
                }

            }else{
                color = CoSys.CHART_COLORS[colIndex];
            }

            int nThick = (int)COMUtil.graphLineWidth;
            if(_cvm.baseLineThicks.size() != 0){
                nThick = _cvm.baseLineThicks.get(colIndex);
            }

            _cvm.setLineWidth(nThick);
            // 2016.05.31 기준선 대비, 색상 굵기 <<

            //drawLine
            String strBaseLabel = baseLabel + " (" + ChartUtil.getFormatedData(dValue, _cdm.getPriceFormat(), _cdm) + ")";
            _cvm.drawLine(gl, bounds.left,y,bounds.width()+ (int)COMUtil.getPixel(COMUtil.getPaddingRight() + 2),y, color ,1.0f); // 2016.05.31 기준선 대비, 색상 굵기
            //2012. 8. 13   기준선그렸을때 라벨 뒤 배경길이가 글씨길이보다 작은 현상 수정 : C13
            _cvm.drawFillRect(gl, bounds.left,y+1,_cvm.GetTextLength(strBaseLabel)+(int)COMUtil.getPixel(2),COMUtil.getPixel(12), color, 1.0f); // 2016.05.31 기준선 대비, 색상 굵기
//            _cvm.drawString(gl, CoSys.CHART_BACK_COLOR[1], bounds.left+(int)COMUtil.getPixel(2), (int)(y +COMUtil.getPixel(8)), strBaseLabel);
            _cvm.drawStringWithSize(gl, CoSys.CHART_BACK_COLOR[1], bounds.left+(int)COMUtil.getPixel(2) ,(int)(y +COMUtil.getPixel(6)), COMUtil.getPixel(10), strBaseLabel);

////			_cvm.drawLine(gl, bounds.left,y,bounds.width(),y, CoSys.CHART_COLORS[colIndex] ,1.0f);
//            _cvm.drawLine(gl, bounds.left,y,bounds.width() +  COMUtil.getPixel(COMUtil.getPaddingRight() + 2),y, color ,1.0f); // 2016.05.31 기준선 대비, 색상 굵기
//            //2012. 8. 13   기준선그렸을때 라벨 뒤 배경길이가 글씨길이보다 작은 현상 수정 : C13
////			_cvm.drawFillRect(gl, bounds.left,y+1,_cvm.GetTextLength(baseLabel)+(int)COMUtil.getPixel(4),COMUtil.getPixel(14), CoSys.CHART_COLORS[colIndex], 1.0f);
//            _cvm.drawFillRect(gl, bounds.left,y+1,_cvm.GetTextLength(baseLabel)+(int)COMUtil.getPixel(4),COMUtil.getPixel(14), color, 1.0f); // 2016.05.31 기준선 대비, 색상 굵기
//            _cvm.drawString(gl, CoSys.CHART_BACK_COLOR[1], bounds.left+(int)COMUtil.getPixel(2), (int)(y +COMUtil.getPixel(8)), baseLabel);
//
//            _cvm.drawFillRect(gl,bounds.right + COMUtil.getPixel(COMUtil.getPaddingRight()), y-COMUtil.getPixel(7) , _cvm.Margin_R ,COMUtil.getPixel(14), color,1.0f);
//            _cvm.drawString(gl, CoSys.CHART_BACK_COLOR[1], bounds.right + (int) COMUtil.getPixel(COMUtil.getPaddingRight() + 2), (int) y, ChartUtil.getFormatedData(dValue, _cdm.getPriceFormat(), _cdm));
        }
    }

    //2017.11 by pjm 퇴직연금 애니매이션 차트 >>
    public boolean m_bInit = true;  //2017.11 by PJM >> 자산 차트 적용
    private final Handler purgeHandler = new Handler();
    boolean needNewFrame = false;
//    boolean m_bInit = true;

    /**
     * Allow a new delay before the automatic cache clear is done.
     */

    public void resetTimer() {
        purgeHandler.removeCallbacks(purger);
        purgeHandler.postDelayed(purger, 10);
    }

    private final Runnable purger = new Runnable() {
        public void run() {
            purgeHandler.removeCallbacks(purger);
            if(dynamicData == null)
                return;
            long now = AnimationUtils.currentAnimationTimeMillis();
            needNewFrame = false;
            for (Dynamics dynamics : dynamicData) {
                dynamics.update(now);
                if (!dynamics.isAtRest()) {
                    needNewFrame = true;
                }
            }
            if (needNewFrame) {
                purgeHandler.postDelayed(purger, 10);
            } else {
                _cvm.m_bWorkingAnimationTimer = false;
                m_bInit = false;
                if(_cvm.getAnimationLineChartListener() != null)
                    _cvm.getAnimationLineChartListener().onAnimationEnd();
            }

            //drawLines
            draw(canvas, dDatasAni);
        }
    };

    private Canvas canvas;
    public double[] dDatasAni;
    public ArrayList<Dynamics> dynamicData;

    public void startAnimation(Canvas gl, double[] data, double dStdPrice)
    {
        double[] fDatas = new double[data.length];
        //animation
        boolean isAllZero = true;
        long now = AnimationUtils.currentAnimationTimeMillis();
        if (dynamicData == null) {
            dynamicData = new ArrayList<Dynamics>();
        }
        if (dynamicData.size() != data.length) {
            //2016.05.23 by LYH >> 좌에서 우로 그려지는 애니메이션.
            if(_cvm.m_nAnimationDirection == ChartViewModel.RIGHT_DIRECTION)
            {
                //Dynamics dynamics = new Dynamics(40, 0.8f);
                Dynamics dynamics = new Dynamics(40, 0.5f);
                dynamics.setPosition((float)dStdPrice, now);
                float value = data.length-1;
                if(value==0)
                    value = 0.01f; // target값이 0이 되면 애니메이션 무한루프를 타기 때문에 최소값을 지정함
                dynamics.setTargetPosition(value, now);
                dynamicData.add(dynamics);
                isAllZero = false;
            }
            else
            //2016.05.23 by LYH << 좌에서 우로 그려지는 애니메이션.
            {
                for (int i=0; i<data.length; i++) {
                    float value = (float)data[i];
                    fDatas[i] = value;
                    if (value!=dStdPrice) {
                        isAllZero = false;
                    }

                    //                	float newValue = (float)newData[i];
//	                Dynamics dynamics = new Dynamics(40, 0.8f);
                    Dynamics dynamics = new Dynamics(40, 0.5f);
                    dynamics.setPosition((float)dStdPrice, now);
                    if(value==0)
                        value = 0.01f; // target값이 0이 되면 애니메이션 무한루프를 타기 때문에 최소값을 지정함
                    dynamics.setTargetPosition(value, now);
                    dynamicData.add(dynamics);
                }
            }
            _cvm.m_bWorkingAnimationTimer = true;
            drawWithAnimation(gl, fDatas);
        } else {
            //2016.05.23 by LYH >> 좌에서 우로 그려지는 애니메이션.
            if(_cvm.m_nAnimationDirection == ChartViewModel.RIGHT_DIRECTION)
            {
                dynamicData.get(0).setTargetPosition((float)data.length-1, now);
            }
            else
            //2016.05.23 by LYH << 좌에서 우로 그려지는 애니메이션.
            {
                for (int i = 0; i < dynamicData.size(); i++) {
                    dynamicData.get(i).setTargetPosition((float)data[i], now);
                }
            }
        }

        //모든 값이 0이면 애니메이션 실행하지 않음
        if (!isAllZero) {
            resetTimer(); //start animator
        }

        _cvm.m_bWorkingAnimationTimer = true;
    }

    public void drawWithAnimation(Canvas gl, double[] newDatas) {
        canvas = gl;
        dDatasAni = newDatas;
        draw(canvas, dDatasAni);
    }

    public void endAnimation() {
        if(dynamicData!=null)  {
            dynamicData.clear();
            dynamicData = null;
        }

        purgeHandler.removeCallbacks(purger);
        _cvm.m_bWorkingAnimationTimer = false;
        m_bInit = false;
        _cvm.getAnimationLineChartListener().onAnimationEnd();
    }
    public void setInitMode(boolean bInit) {
        if(dynamicData!=null)  {
            dynamicData.clear();
            dynamicData = null;
            purgeHandler.removeCallbacks(purger);
        }
        m_bInit = bInit;
    }
    //2017.11 by pjm 퇴직연금 애니매이션 차트 <<
}
