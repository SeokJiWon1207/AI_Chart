package drfn.chart.graph;

import android.graphics.Canvas;
import android.graphics.RectF;

import java.util.Vector;

import drfn.chart.block.Block;
import drfn.chart.draw.DrawTool;
import drfn.chart.draw.SignalDraw;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;
import drfn.chart.util.COMUtil;
import drfn.chart.util.ChartUtil;

/**
 * 설명: 각 지표를 위한 추상 클래스
 *       1. 그리기 툴을 설정하고 리턴한다(그리기툴 : LineTool,BongTool,...)
 *       2. 그래프의 이름을 설정하고 리턴한다(예:일봉식봉)
 *       3. 설정된 그리기 툴에따라 그림을 그린다
 */
public abstract class AbstractGraph{
    public  ChartDataModel _cdm;
    public  ChartViewModel _cvm;
    public Block parent;
    public int blockType;
    public boolean formulated=false;//공식에 적용되었을 경우 true
    boolean isVisible=true;
    private Vector<DrawTool> _drawTool;//그리기 툴
    private Vector<String> _drawToolTitle;
    public String[] _dataKind = {"종가"};//사용할 데이터 정보
    private double[] mm_data;//최소값
    //    private int[] mm_view;
    private RectF title_bound;
    private int _graphKind;//그래프의 종류
    public String definition;//그래프의 정의

    protected String m_strDefinitionHtml = "";	//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)

    //2013. 9. 3 지표마다 기준선 설정 추가>>
    private boolean bShowBaseLine_1 = true;  //지표 기준선 1
    private boolean bShowBaseLine_2 = true;	//지표 기준선 2
    //2013. 9. 3 지표마다 기준선 설정 추가>>

    //=============================
    // GraphViewModel에 있던 속성
    //=============================
    public String graphTitle="";
    private String graphDatakind="";
    private int graphType=1;//선형이 디폴트
    public int[] interval=null;
    public int[] org_interval=null;
    public int[] org_base=null;		//2013. 9. 3 지표마다 기준선 설정 추가>>  : 기준선 기본값
    public String[] s_interval=null;
    public int[] base = null;
    public Vector<DrawTool> tool;
    //public Color[] base_col = {Color.gray,new Color(0,185,253),new Color(25,180,0)};
//    public Color[] base_col = {new Color(25,180,0),new Color(25,180,0),new Color(25,180,0)};//보조선의 색상을 변경하려면 이 배열수정

    protected boolean isSellingSignalShow = true;	//2014. 9. 11 매매 신호 보기 기능 추가
    protected boolean isFillCloud = true;	//2021.05.24 by hhk - 구름대 채우기

    protected boolean m_bIsNewLineNextStep = false;	//2015. 7. 10 차트 상단 지표명 2줄이상 개행시키기

    //2016.07.28 by LYH >> 데이터기준, 이평선타입별(단순,가중,지수,기하) 계산
    public int m_nDataType = 99;
    public int m_nAverageCalcType = 99;
    //2016.07.28 by LYH << 데이터기준, 이평선타입별(단순,가중,지수,기하) 계산

    //2017.05.15 신호,강세약세 플래그
    public int m_nStrategyType = 0;

    //2019. 07. 04 by hyh - BollingerBand 기준가, 계산방법 추가 >>
    public int calcTypeBollingerband=ChartViewModel.AVERAGE_GENERAL; //이평타입
    public int dataTypeBollingerband=ChartViewModel.AVERAGE_DATA_CLOSE; //종가기준
    //2021.09.14 by JHY >>개행했을때 다음줄 margin[첫째줄이 getPixel_W 값 8 인것같아 변경함(최상단 LEFT_MARGIN_TITLE 변수값을)]
    int LEFT_MARGIN_TITLE = (int)COMUtil.getPixel(8);

//    //2020.03.20 주가이동평균 가격, 이평선 타입 수정 - hjw >>
//    public int calcTypeAverage=ChartViewModel.AVERAGE_GENERAL; //이평타입
//    public int dataTypeAverage=ChartViewModel.AVERAGE_DATA_CLOSE; //종가기준
//    //2020.03.20 주가이동평균 가격, 이평선 타입 수정 - hjw <<

    public AbstractGraph(ChartViewModel cvm, ChartDataModel cdm){
        _cdm = cdm;
        _cvm = cvm;
        _drawToolTitle = new Vector<String>(10);
        tool = getDrawTool();//드로우 툴을 구한다
    }
    //=================================
    //그래프 속성
    //Graph형, 그래프 이름
    //=================================
    //type --> 0: 한 블럭에 하나의 그래프
    //         1: 
    //         2: 기존의 블럭에 더하여 그리는 그래프
    //         3: 전체블럭을 차지하여 그리는 그래프
    //         4: 대기매물
    public void setProperties(int type, String title, int graphkind){
        graphType=type;
        graphTitle = title;
        _graphKind = graphkind;
        initControlValue();
    }
    public void setParent(Block block){
        parent = block;
    }
    public void add(DrawTool dt){
        if(_drawTool==null)_drawTool = new Vector<DrawTool>();

        //2015. 1. 13 - by lyk 동일지표 이름 처리 (동일지표의 drawTool 명을 구분하기 위하여 처리함)
        int index = graphTitle.indexOf(COMUtil.JIPYO_ADD_REMARK);
        String addString = "";
        if(index>0 && (!dt.getTitle().equals("기본거래량"))) {
            addString = graphTitle.substring(index);
            dt.setTitle(dt.getTitle()+addString);
        }
        //2015. 1. 13 - by lyk 동일지표 이름 처리 (동일지표의 drawTool 명을 구분하기 위하여 처리함) end

        _drawTool.addElement(dt);
        _drawToolTitle.addElement(dt.getTitle());
    }

    StringBuffer buf= new StringBuffer();
    public String getGraphDrawTitle(){
        if(buf.length()>0) buf.delete(0, buf.length());
        for(int i=0;i<this._drawTool.size();i++){
            DrawTool dt = (DrawTool)_drawTool.elementAt(i);
            buf.append(" ");
            if(dt.isVisible())buf.append(dt.getTitle());
        }
        return buf.toString();
    }
    public void setBounds(float sx, float sy, float r, float b){
        for(int i=0;i<this._drawTool.size();i++){
            DrawTool dt = (DrawTool)_drawTool.elementAt(i);
            dt.setBounds(sx,sy,r,b);
        }
    }
    //public void setGraphTitleBounds(int sx,int sy){
    public float setGraphTitleBounds(float sx,float sy){  //2017.06.21 by LYH >> MAC와 다른 지표 타이틀 겹치는 현상 수정
        //sy += COMUtil.getPixel_H(15);
        sy += COMUtil.getPixel_H(10);
        int title_len=0;
        int dCnt = _drawTool.size();
        String viewTitle;
        int nIntervalCnt=0;
        if(interval!=null) nIntervalCnt = interval.length;
        if(_cvm.chartType != COMUtil.COMPARE_CHART)
        {
	        if(dCnt>0 && nIntervalCnt>0) {
	            if(graphTitle.equals("거래량이동평균")) {
	                String strTitle = "";
	                for(int i=0;i<dCnt;i++){
	                    DrawTool dt = (DrawTool)_drawTool.elementAt(i);
	                    strTitle = dt.subTitle.substring(5);
	                    dt.setViewTitle(strTitle);
	                    dt.showDataTitle=false;
	                }
	            }
	            else if(graphTitle.startsWith("투자자별") || graphTitle.startsWith("투자자매매동향"))
	            {
	                for(int i=0; i<dCnt; i++) {
	                    DrawTool dt = (DrawTool)_drawTool.get(i);
	
	                    //2015. 1. 13 - by lyk 동일지표 이름 처리 (구분자 제거)
	//    			dt.setViewTitle(dt.subTitle);
	                    if(dt.subTitle.endsWith("_기관")) {
	                        dt.setViewTitle("기관");
	                    }
	                    else if(dt.subTitle.endsWith("_외국인")){
	                        dt.setViewTitle("외국인");
	                    }
	                    else
	                        dt.setViewTitle(COMUtil.getAddJipyoTitle(dt.subTitle));
	                    //2015. 1. 13 - by lyk 동일지표 이름 처리 (구분자 제거) end
	
	                    dt.showDataTitle=false;
	                }
	            }
	            else if(!graphTitle.equals("주가이동평균")) {
	                for(int i=0;i<dCnt;i++){
	                    DrawTool dt = (DrawTool)_drawTool.elementAt(i);
	
	                    //2015. 1. 13 - by lyk 동일지표 이름 처리 (구분자 제거)
	                    String strSubTitle = COMUtil.getAddJipyoTitle(dt.subTitle);
	
	                    //2012. 7. 16  보조지표 subtitle 이 십자선모드였다가 해제될 때 배경색이 남는 현상 해결  -> 아래 if문 조건들이 대문자 및 영어로 되어있었음 
	//                    if(dt.subTitle.endsWith("SIGNAL") || dt.subTitle.equals("FAST %D") ||
	//                    		dt.subTitle.equals("SLOW %D") || dt.subTitle.equals("LONG DISPARITY") || dt.subTitle.equals("B RATIO")){
	                    if(strSubTitle.endsWith("Signal") || strSubTitle.equals("%D") ||
	                            strSubTitle.equals("Sto %D") || strSubTitle.equals("장기이격률") || strSubTitle.equals("B Ratio")
	                            || strSubTitle.equals("MDI")|| strSubTitle.equals("ADX_PDI")|| strSubTitle.equals("ADX_MDI") || strSubTitle.equals("DownDI")
                                || strSubTitle.equals("Reverse Long") || strSubTitle.equals("ADXR") || strSubTitle.equals("BB_중간") || strSubTitle.equals("BB_하한") || strSubTitle.contains("Slow%D")){
	                        if(strSubTitle.endsWith("Signal")) {
                                //2021.07.22 by hanjun.Kim - kakaopay - 차트 블럭타이틀 괄호 제거
//                                viewTitle = "Signal(" + interval[nIntervalCnt - 1] + ")";
                                viewTitle = "Signal " + interval[nIntervalCnt - 1];
                            } else {
                                //2014.10.06 - by lyk 동일지표 이름 처리 (구분자 제거)
                                //2021.07.22 by hanjun.Kim - kakaopay - 차트 블럭타이틀 괄호 제거
//	                            viewTitle = strSubTitle+"("+interval[nIntervalCnt-1]+")";
                                viewTitle = strSubTitle + " " + interval[nIntervalCnt - 1];
                            }
	                        //2014.10.06 - by lyk 동일지표 이름 처리 (구분자 제거) end
	
	                        dt.setViewTitle(viewTitle);
	                        dt.showDataTitle=false;
                            if(!strSubTitle.equals("ADX_PDI"))
	                            nIntervalCnt--;
	                        //break;
	                    }
                        else if(strSubTitle.equals("OSC"))
                        {
                            viewTitle = strSubTitle;
                            dt.setViewTitle(viewTitle);
                            dt.showDataTitle=false;
                        }
	                    else if(strSubTitle.startsWith("RCI")) {
                            //2021.07.22 by hanjun.Kim - kakaopay - 차트 블럭타이틀 괄호 제거
//	                        viewTitle = strSubTitle+"("+interval[i]+")";
                            viewTitle = strSubTitle+" "+interval[i];
	                        dt.setViewTitle(viewTitle);
	                        dt.showDataTitle=false;
	                        nIntervalCnt--;
	                    }
                        // 2021.10.27 by JHY - 이격 분기 >>
                        else if(strSubTitle.startsWith("이격")) {
//	                        viewTitle = strSubTitle+"("+interval[i]+")";
                            viewTitle = ""+interval[i];
                            dt.setViewTitle(viewTitle);
                            dt.showDataTitle=false;
                            nIntervalCnt--;
                        }
                        // 2021.10.27 by JHY - 이격 분기 >>
	                    else {
                            viewTitle = strSubTitle;
                            if(this.graphTitle.equals("외국인/기관/개인 추세")) {
                                if(viewTitle.equals("외국인/기관/개인 추세")) {
                                    viewTitle = "외국인";
                                } else if(viewTitle.equals("기관 순매수")) {
                                    viewTitle = "기관";
                                } else if(viewTitle.equals("개인 순매수")) {
                                    viewTitle = "개인";
                                }
                                dt.setViewTitle(viewTitle);
                                dt.showDataTitle=false;
                            }

                        }
	                    //2015. 1. 13 - by lyk 동일지표 이름 처리 (구분자 제거) end
	                }
	                DrawTool dt = (DrawTool)_drawTool.elementAt(0);
	                viewTitle = dt.subTitle;
	
	                //2015. 1. 13 - by lyk 동일지표 이름 처리 (구분자 제거)
	                String strSubTitle = COMUtil.getAddJipyoTitle(viewTitle);
	                //2015. 1. 13 - by lyk 동일지표 이름 처리 (구분자 제거) end
	
	                //2012.08.29 by LYH >> ELW 기초자산 추가.
	                if(dt.subTitle.equals("BaseMarket"))
	                {
	                    viewTitle = _cdm.codeItem.strBaseMarket;
	                }
	                //2012.08.29 by LYH <<
                    //2021.10.26 by JHY - 이격 일 경우 interval값 제외 >>
//	                else if(dt.subTitle.startsWith("RCI")||dt.subTitle.startsWith("이격")) {
//	                    viewTitle = COMUtil.getAddJipyoTitle(dt.subTitle)+"("+interval[0]+")";
//	                }
                    else if(dt.subTitle.startsWith("RCI")) {
                        viewTitle = COMUtil.getAddJipyoTitle(dt.subTitle)+"("+interval[0]+")";
                    }
                    else if(dt.subTitle.startsWith("이격")) {
//                        viewTitle = COMUtil.getAddJipyoTitle(dt.subTitle);
                        viewTitle = ""+interval[0];
                    }
                    //2021.10.26 by JHY - 이격 일 경우 interval값 제외 <<
	                else
	                {
	                    if(nIntervalCnt>0 && interval[0] != 0)
	                    {
	                        //2015. 1. 13 - by lyk 동일지표 이름 처리 (구분자 제거)
	                        //viewTitle = viewTitle+"(";
                            //2021.07.22 by hanjun.Kim - kakaopay - 차트 블럭타이틀 괄호 제거
	                        viewTitle = strSubTitle+" ";
	                        //2015. 1. 13 - by lyk 동일지표 이름 처리 (구분자 제거) end
	                        for(int i=0;i<nIntervalCnt;i++) {
	                            //2015. 1. 20 볼린저밴드 추가시 차트 graphtitle 표준편차승수 쪽 소숫점처리 >> : 지표 내부 계산로직이 /100을 한 소숫점 값이 아닌 정수값으로 함. 그래서 표시만 바꿈
	                            if( 	(graphTitle.equals("Parabolic SAR") && (0 == i || 1 == i)) ||
                                        //2017.05.11 by LYH << 전략(신호, 강약) 추가
                                        (graphTitle.equals("Parabolic SAR 신호") && (0 == i || 1 == i)) ||
                                        (graphTitle.equals("Parabolic SAR 강세약세") && (0 == i || 1 == i)) ||
                                        //2017.05.11 by LYH << 전략(신호, 강약) 추가 end
	                                    (graphTitle.equals("Bollinger Band") && 1 == i) ||
                                        (graphTitle.equals("Band %B") && 1 == i) ||
                                        (graphTitle.equals("Envelope") && (1 == i || 2 == i))) //2020.11.27 by HJW - Envelope 지표 소수점 처리
	                            {
	                                viewTitle = viewTitle+String.format("%.2f", (float)interval[i]/100);
	                            }
	                            else
	                            {
	                                viewTitle = viewTitle+interval[i];
	                            }
	                            //2015. 1. 20 볼린저밴드 추가시 차트 graphtitle 표준편차승수 쪽 소숫점처리 <<
	                            if(i!=nIntervalCnt-1) {
	                                viewTitle = viewTitle+",";
	                            }
	                        }
//	                        viewTitle = viewTitle+")";
	                    }
	                    else
	                        viewTitle = strSubTitle;	//2015. 3. 3  중복지표에서 괄호 있을 때 (ex: ADX) *숫자  표시가 뒤에 붙어서 나옴
	                }
	
	                //2015. 1. 13 - by lyk 동일지표 이름 처리 (구분자 제거)
	                dt.setViewTitle(viewTitle);
	//                dt.setViewTitle(strSubTitle);
	                //2015. 1. 13 - by lyk 동일지표 이름 처리 (구분자 제거) end
	                dt.showDataTitle=false;
	            }
	        } else {
	            for(int i=0; i<dCnt; i++) {
	                DrawTool dt = (DrawTool)_drawTool.get(i);
	
	                //2015. 1. 13 - by lyk 동일지표 이름 처리 (구분자 제거)
	//    			dt.setViewTitle(dt.subTitle);
	                dt.setViewTitle(COMUtil.getAddJipyoTitle(dt.subTitle));
	                //2015. 1. 13 - by lyk 동일지표 이름 처리 (구분자 제거) end
	
	                dt.showDataTitle=false;
	            }
	        }
        }

        //2015. 7. 10 차트 상단 지표명 2줄이상 개행시키기>>
        float fDrawtoolTitleX = sx;
        float fDrawtoolTitleY = sy;
        //2015. 7. 10 차트 상단 지표명 2줄이상 개행시키기<<
        for(int i=0;i<dCnt;i++){
            DrawTool dt = (DrawTool)_drawTool.elementAt(i);
            if(dt.isVisible()){
                //2015. 7. 10 차트 상단 지표명 2줄이상 개행시키기>>
//                dt.setTitleBounds(sx+title_len,sy);
                RectF bounds = dt.getBounds();

                //지표명의 길이
                String strTitle = null;
                strTitle = dt.getViewTitle();
                if(null == strTitle)
                {
                    strTitle = dt.getTitle();
                }
                if(strTitle.length()<1)
                    continue;

                if(strTitle.equals("외국인/기관/개인 추세")) {
                    strTitle = "외국인";
                }

                float fDrawtoolTitleLen = (int)_cvm.GetTextLength(strTitle)+(int)+(int)COMUtil.getPixel(13);

                //y축 넘어가는지 검사 
                float fX = fDrawtoolTitleX+title_len+fDrawtoolTitleLen;
                float fYScale = (bounds.left + bounds.width());
                //2021.11.23 by lyk - kakaopay - 보조지표인 경우 개행 규칙 사용 안함 >>
                if(fYScale > 0 && fX >= fYScale && this.blockType != 0)
                {
                    //명확한 개행조건. 이번에 그릴 지표명이 y축을 넘어갔음. 따라서 다음번 표시할 지표명은 개행된 맨 왼쪽 좌표에 표시
                    title_len = 0;
                    fX = fDrawtoolTitleX+title_len+fDrawtoolTitleLen;
                    fDrawtoolTitleX = LEFT_MARGIN_TITLE;
                    if(fDrawtoolTitleLen < fYScale-fDrawtoolTitleX) {
                        fDrawtoolTitleY += COMUtil.getPixel(13);
                    }
                }
                //2021.11.23 by lyk - kakaopay - 보조지표인 경우 개행 규칙 사용 안함 <<

//                if(nYScale > 0 && nYScale > nX && nYScale - nX < 17)
//                {
//                    //실제 해당 지표명들이 개행되지는 않았지만, y축에 매우 인접하여 다음 지표가 지표명을 표시할때 무조건 개행이 선행되야 하는 경우
//                    m_bIsNewLineNextStep = true;
//                }
//                else
//                {
//                    m_bIsNewLineNextStep = false;
//                }
                dt.setTitleBounds(fDrawtoolTitleX+title_len,fDrawtoolTitleY);
                //2015. 7. 10 차트 상단 지표명 2줄이상 개행시키기<<

                if(dt.getTitleBounds()==null) return 0; //2017.06.21 by LYH >> MAC와 다른 지표 타이틀 겹치는 현상 수정
                title_len+=dt.getTitleBounds().width();
            }
        }

        title_bound = new RectF(sx,sy,title_len,(int)COMUtil.getPixel(8));

        return fDrawtoolTitleY - sy;    //2017.06.21 by LYH >> MAC와 다른 지표 타이틀 겹치는 현상 수정
    }
    public void setGraphDataTitle(float sx, float sy, int nIndex) {
        sy += COMUtil.getPixel_H(10);
        int title_len = 0;
        int dCnt = _drawTool.size();
        String viewTitle;
        if(_cvm.chartType != COMUtil.COMPARE_CHART && !_cvm.isStandGraph()) {
            for (int i = 0; i < dCnt; i++) {
                DrawTool dt = (DrawTool) _drawTool.elementAt(i);
                String strData = dt.getFormatData(nIndex);
                //2021.11.24 by lyk - kakaopay - 지표 수치가 클 경우 천단위, 만단위로 변환하기 (지표 타이틀 텍스트가 Y축을 침범하는 이슈 수정) >>
//                String dtTitle = dt.subTitle;
//                if(this.graphTitle.equals("외국인/기관/개인 추세")) {
//                    if(dtTitle.equals("외국인/기관/개인 추세")) {
//                        dtTitle = "외국인";
//                    } else if(dtTitle.equals("기관 순매수")) {
//                        dtTitle = "기관";
//                    } else if(dtTitle.equals("개인 순매수")) {
//                        dtTitle = "개인";
//                    }
//                }

                if (dt.subTitle.startsWith("거래대금") || dt.subTitle.startsWith("거래량") || dt.subTitle.startsWith("거래량이평") || this.graphTitle.equals("외국인/기관/개인 추세")) {
                    String tmpData = strData.replace(",", "");
                    String orgData = tmpData;
                    //2022.06.14 by CYJ - 천미만 절삭 수정 >>
                    tmpData = tmpData.replace("+", "");
                    tmpData = tmpData.replace("-", "");
                    //2022.06.14 by CYJ - 천미만 절삭 수정 <<

                    try {
                        double dData = Double.parseDouble(orgData);
                        double rData = 0;
//                        //2022.05.24 by lyk - 지수 거래량이 1000단위 절사 데이터 이므로 "천"만 표기를 해준다.
//                        if (COMUtil._mainFrame.mainBase.baseP.nMarketType == 1 && (dt.subTitle.startsWith("거래량") || dt.subTitle.startsWith("거래량이평"))) { //2022.05.04 by lyk - 지수차트 처리
//                            rData = dData; //지수차트의 거래량은 천단위 절사된 데이터가 내려온다., 거래대금은 백만단위
//                            String ftData = COMUtil.format(rData, 2, 3);
//                            String strUnit = "";
//                            if(dt.subTitle.startsWith("거래대금")) { //현재 사용안함
//                                strUnit = "백만";
//                            } else {
//                                strUnit = "천";
//                            }
//                            try {
//                                String ftdData = COMUtil.removeString(ftData, ".00");
//                                strData = ftdData + strUnit;
//                            } catch (Exception e) {
//                                strData = COMUtil.format(rData, 2, 3) + strUnit;
//                            }
//                        } else {
//                            //                        if (tmpData.length() >= 9) {
//                            //                            rData = dData / 1000000; //M
//                            //                            strData = COMUtil.format(rData,2,3) + "M";
//                            //                        } else
//                            //                        if (tmpData.length() >= 7) {
//                            if (tmpData.length() >= 9) {
//                                rData = Math.floor(dData / 1000000); //K, 2022.05.04 by lyk - 소수점 이하값은 버리기 (카카오페이 기획 변경)
//                                String ftData = COMUtil.format(rData, 2, 3);
//                                try {
//                                    String ftdData = COMUtil.removeString(ftData, ".00");
//                                    strData = ftdData + "백만";
//                                } catch (Exception e) {
//                                    strData = COMUtil.format(rData, 2, 3) + "백만";
//                                }
//                            } else if (tmpData.length() > 3) {
//                                rData = Math.floor(dData / 1000); //K, 2022.05.04 by lyk - 소수점 이하값은 버리기 (카카오페이 기획 변경)
//                                String ftData = COMUtil.format(rData, 2, 3);
//                                try {
//                                    String ftdData = COMUtil.removeString(ftData, ".00");
//                                    strData = ftdData + "천";
//                                } catch (Exception e) {
//                                    strData = COMUtil.format(rData, 2, 3) + "천";
//                                }
//                            }
//                        }
                        String strUnit = "";
                        //2022.06.14 by CYJ - 천미만 절삭 수정 >>
                        if (COMUtil._mainFrame.mainBase.baseP.nMarketType == 1 && (dt.subTitle.startsWith("거래량") || dt.subTitle.startsWith("거래량이평"))) { //2022.05.04 by lyk - 지수차트 처리
                            rData = dData;
                            String ftData = COMUtil.format(rData, 2, 3);
                            if (dt.subTitle.equals("거래량")) {
                                strUnit = "(천) ";
                            }
                            strData = setCuttingNum (rData, strData, strUnit);
                        } else if (dt.subTitle.equals("거래대금") || (dt.subTitle.equals("거래량"))) {
                            if (tmpData.length() >= 9) {
                                rData = Math.floor(dData / 1000000);
                                strUnit = "(백만) ";
                                strData = setCuttingNum(rData, strData, strUnit);
                            } else if (tmpData.length() > 3) {
                                rData = Math.floor(dData / 1000);
                                strUnit = "(천) ";
                                strData = setCuttingNum(rData, strData, strUnit);
                            }
                        } else { //거래량이평
                            if (tmpData.length() >= 9) {
                                rData = Math.floor(dData / 1000000);
                                strData = setCuttingNum(rData, strData, strUnit);
                            } else if (tmpData.length() > 3) {
                                rData = Math.floor(dData / 1000);
                                strData = setCuttingNum(rData, strData, strUnit);
                            }
                        }
                        //2022.06.14 by CYJ - 천미만 절삭 수정 <<
                    } catch (Exception e) {

                    }
                }
                //2021.11.24 by lyk - kakaopay - 지표 수치가 클 경우 천단위, 만단위로 변환하기 (지표 타이틀 텍스트가 Y축을 침범하는 이슈 수정) <<

                //2012. 7. 16  보조지표 subtitle 이 십자선모드였다가 해제될 때 배경색이 남는 현상 해결
                if (COMUtil.getAddJipyoTitle(dt.subTitle).endsWith("Signal"))    //2015. 3. 3 차트 롱터치시 중복지표의 Signal 앞에 지표명이 붙음
                //2021.07.22 by hanjun.Kim - kakaopay - 차트 블럭타이틀 괄호 제거
                //                    viewTitle = "Signal(" + strData + ")";
                    viewTitle = "Signal " + strData;
                    //2021.10.27 by JHY - 이격 추가 >>
                else if (dt.subTitle.startsWith("거래량이평") || dt.subTitle.startsWith("이격")) {
                    viewTitle = strData;
                    //2021.10.27 by JHY - 이격 추가 <<
                }else if (dt.subTitle.startsWith("렌코"))
                    viewTitle = dt.subTitle;
                else if (dt.title.startsWith("매수매도거래량")) {
                    double sellData[] = _cdm.getSubPacketData("매도거래량");
                    if (sellData != null) {
                        String strSell = _cdm.getFormatData("매도거래량", nIndex);
                        String strBuy = _cdm.getFormatData("매수거래량", nIndex);
                        //2021.07.22 by hanjun.Kim - kakaopay - 차트 블럭타이틀 괄호 제거
//                        viewTitle = String.format("매도거래량(%s) 매수거래량(%s)", strSell, strBuy);
                        viewTitle = String.format("매도거래량 %s 매수거래량 %s", strSell, strBuy);
                    } else {
                        //2021.07.22 by hanjun.Kim - kakaopay - 차트 블럭타이틀 괄호 제거
//                        viewTitle = dt.subTitle + "(" + strData + ")";
                        viewTitle = dt.subTitle + " " + strData;
                    }
                } else if (dt.title.startsWith("역시계곡선")) {
                    //2021.07.22 by hanjun.Kim - kakaopay - 차트 블럭타이틀 괄호 제거
//                    viewTitle = dt.subTitle + "(" + interval[0] + ")";
                    viewTitle = dt.subTitle + " " + interval[0];
                } else {
                    if (dt.subTitle.endsWith("_기관")) {
                        //2021.07.22 by hanjun.Kim - kakaopay - 차트 블럭타이틀 괄호 제거
//                        viewTitle = "기관(" + strData + ")";
                        viewTitle = "기관 " + strData;
                    } else if (dt.subTitle.endsWith("_외국인")) {
                        //2021.07.22 by hanjun.Kim - kakaopay - 차트 블럭타이틀 괄호 제거
//                        viewTitle = "외국인(" + strData + ")";
                        viewTitle = "외국인 " + strData;
                    } else {
                        //2021.07.22 by hanjun.Kim - kakaopay - 차트 블럭타이틀 괄호 제거
                        String dtTitle = dt.subTitle;
                        if(this.graphTitle.equals("외국인/기관/개인 추세")) {
                            if(dtTitle.equals("외국인/기관/개인 추세")) {
                                dtTitle = "외국인";
                            } else if(dtTitle.equals("기관 순매수")) {
                                dtTitle = "기관";
                            } else if(dtTitle.equals("개인 순매수")) {
                                dtTitle = "개인";
                            }
                        }
                        viewTitle = dtTitle + " " + strData;
                    }
                }
                dt.setViewTitle(viewTitle);
                dt.showDataTitle = true;
                if (dt.isVisible()) {
                    dt.setTitleBounds(sx + title_len, sy);
                    title_len += dt.getTitleBounds().width();
                }
            }
        }
        //title_bound = new RectF(sx,sy,title_len,15);
        title_bound = new RectF(sx,sy,sx+title_len,15);
    }
    public RectF getGraphTitleBounds(){
        return title_bound;
    }
    public Vector<String> getDrawToolTitle(){
        return this._drawToolTitle;
    }
    public void setGraphType(int type){
        this.graphType = type;
    }
    public void setGraphTitle(String title){
    }
    public void setDatakind(String dk){
        this.graphDatakind = dk;
    }
    public int getGraphType(){
        return graphType;
    }
    public String getGraphTitle(){
        return graphTitle;
    }
    //=================================
    // 정수형으로 min,max를 구하여 set
    //=================================
    public void setMinMax(double[] mm_data){
        this.mm_data = mm_data;
        if(getDrawTool().size()>0){
            for(int i=0;i<getDrawTool().size();i++){
                DrawTool dt = (DrawTool)(getDrawTool().elementAt(i));
                dt.setMinMax(this.mm_data);
            }
        }
    }
    public Vector<DrawTool> getDrawTool(){

        if(_drawTool ==null)_drawTool = new Vector<DrawTool>();
        return _drawTool;
    }

    public void setVisible(boolean b){
        isVisible = b;
    }
    public double[][] makeData(int index, int num){
        double[] tmp ;
        if(_dataKind==null) return null;
        int kindLen = _dataKind.length;
        double[][] data = new double[num][kindLen];
        for(int i=0;i<kindLen;i++){
            tmp = _cdm.getData(_dataKind[i], index, num);
            if(tmp==null)break;
            int tmpLen = tmp.length;
            for(int j=0;j<tmpLen;j++){
                data[j][i] = tmp[j];
            }
        }
        return data;
    }
    public double[] getData(String datakind){
        int num = _cdm.getCount();
        double[] tmp = _cdm.getData(datakind,0,num);
        return tmp;
    }
    public String[] getGraphDatakind(){
        return _dataKind;
    }
    public void setDatakind(String[] datakind){
        int cnt=0;
        String[] tmp = new String[datakind.length];
        for(int i=0;i<datakind.length;i++){
            if(!datakind[i].equals("")){
                tmp[cnt]= datakind[i];
                cnt++;
            }
        }
        _dataKind = new String[cnt];
        System.arraycopy(tmp,0,_dataKind,0,cnt);
    }
    //=================================
    // index에 따라 데이터를 추출한다
    //=================================
    public double[][] getData(){
        int index=0;
        return getData(index);
    }
    public double[][] getData(int type){
        int num=0;
        int index=0;
//        int mm_num =0;
//        int mm_index = 0;
        switch(type){
            case 0://영역안의 데이터만 리턴
                num = _cvm.getViewNum();//join된 경우에 minmax산출에 사용하기 위한
                index = _cvm.getIndex();
                break;
            case 1://가지고 있는 모든 데이터를 리턴
                num = _cdm.getCount();
                index = 0;
                break;
        }
//        System.out.println("ABSGRaph:"+index+" "+num);
        double[][] _data = makeData(index, num);
        return _data;
    }
    public void draw(Canvas gl){
        if(_cdm.getCount()>0 || _cvm.bIsUpdownChart)
            drawGraph(gl);
    }
    public abstract void FormulateData();
    public abstract void reFormulateData();
    public abstract void drawGraph(Canvas gl);
    public abstract void drawGraph_withSellPoint(Canvas g);

    public double[] exponentialAverageD(double[] data, int interval,int startPos){
        if(data == null)return null;
        double D = 2.00/(interval+1.00);
        double[] dEMA = new double[data.length];
        int subTotal=0;
        if(data.length <= interval)return dEMA;
        for(int j=interval+startPos; j>-1; j--){
            subTotal += data[j];
        }
        dEMA[interval]= (int)(subTotal /interval);
        for(int i=interval+1+startPos ; i<data.length ; i++){
            dEMA[i] = (D*data[i]+(1-D)*dEMA[i-1]);
        }
        return dEMA;
    }
    //===========================
    // 지수 이동평균
    // 지수 이동평균의 시작인덱스를 주도록한다
    // 지수이동평균의 최초값은 단순이동평균값을 쓰도록 한다.(LG와 맞춤)
    //===========================
//    public double[] exponentialAverage(double[] data, int interval,int startPos){
//        if(data == null)return null;
//        int dLen = data.length;
//        double D = 2.00/(interval+1.00);
//        double[] dEMA = new double[dLen];
//        double[] EMA = new double[dLen];
//        if(dLen <= interval)return EMA;
//        EMA[0] = data[0];
//        dEMA[0] = data[0];
////	    int subTotal=0;
////        //최초값을 단순이동평균으로 구한다
////        for(int j=interval; j>-1; j--) {
////            subTotal += data[j];
////        }
////        dEMA[interval]= (subTotal /interval);
////        for(int i=interval+startPos+1 ; i<dLen ; i++) {
//        for(int i=1 ; i<dLen ; i++) {
//            dEMA[i]=dEMA[i-1]+D*((double)data[i]-dEMA[i-1]);
//            EMA[i] = (dEMA[i]);
//        }
//        for(int i=0 ; i<startPos+interval-1 ; i++) {
//            if(i<data.length)
//                EMA[i] = 0;
//        }
//        return EMA;
//    }
    public double[] exponentialAverage(double[] data, int interval,int startPos){
        if(data == null)return null;
        int dLen = data.length;
        double D = 2.00/(interval+1.00);
        double[] dEMA = new double[dLen];
        double[] EMA = new double[dLen];
        //if(dLen <= interval)return EMA;
        if(dLen < 1)return EMA;
        EMA[0] = data[0];
        dEMA[0] = data[0];
//	    int subTotal=0;
//        //최초값을 단순이동평균으로 구한다
//        for(int j=interval; j>-1; j--) {
//            subTotal += data[j];
//        }
//        dEMA[interval]= (subTotal /interval);
//        for(int i=interval+startPos+1 ; i<dLen ; i++) {
        for(int i=0 ; i<dLen ; i++) {
            //2019. 04. 22 by hyh - 시그널 에러 수정 >>
            double dData = data[i];

            if (Double.isNaN(dData) || Double.isInfinite(dData)) {
                dData = 0.0f;
            }
            //2019. 04. 22 by hyh - 시그널 에러 수정 <<

            if(i==0)
                dEMA[i]=dData;
            else
                dEMA[i]=dEMA[i-1]+D*(dData-dEMA[i-1]);
            EMA[i] = (dEMA[i]);
        }
//        for(int i=0 ; i<startPos+interval-1 ; i++) {
//            if(i<data.length)
//                EMA[i] = 0;
//        }
        return EMA;
    }

    public double[] makeAverageD(double[]data, int interval, int startPos){
        int dLen = data.length;
        double[] averageData = new double[dLen];
        if(dLen <= interval)return averageData;
        for(int i = interval ; i < dLen ; i++) {
            double subTotal = 0.;
            for(int j= i ; j>i-interval ; j--) {
                subTotal += data[j];
            }
            averageData[i] = (subTotal /interval);
        }
        return averageData;
    }

//    public double[] exponentialAverage(double[] data, int interval) {
//        if (data == null)  return null;
//        double D = 2.00/(interval+1.00);
//        double[] dEMA = new double[data.length];
//        double[] EMA = new double[data.length];
//        if(data.length <= interval)return EMA;
//        EMA[0] = data[0];
//        dEMA[0] = data[0];
//        //int subTotal=0;
//        //최초값을 단순이동평균으로 구한다
//        //for(int j=interval; j>-1; j--) {
//        //    subTotal += data[j];
//        //}
//        //dEMA[interval]= (subTotal /interval);
//        //for(int i=interval+1 ; i<data.length ; i++) {
//        for(int i=1 ; i<data.length ; i++) {
//            dEMA[i]=dEMA[i-1]+D*((double)data[i]-dEMA[i-1]);
//            EMA[i] = (dEMA[i]);
//        }
//        return EMA;
//    }

    public double[] exponentialAverage(double[] data, int interval) {
        if (data == null)  return null;
        double D = 2.00/(interval+1.00);
        double[] dEMA = new double[data.length];
        double[] EMA = new double[data.length];
        //if(data.length <= interval)return EMA;
        if(data.length < 1)return EMA;
        EMA[0] = data[0];
        dEMA[0] = data[0];
        //int subTotal=0;
        //최초값을 단순이동평균으로 구한다
        //for(int j=interval; j>-1; j--) {
        //    subTotal += data[j];
        //}
        //dEMA[interval]= (subTotal /interval);
        //for(int i=interval+1 ; i<data.length ; i++) {
        for(int i=0 ; i<data.length ; i++) {
            //2019. 04. 22 by hyh - 시그널 에러 수정 >>
            double dData = data[i];

            if (Double.isNaN(dData) || Double.isInfinite(dData)) {
                dData = 0.0f;
            }
            //2019. 04. 22 by hyh - 시그널 에러 수정 <<

            if(i==0)
                dEMA[i]=dData;
            else
                dEMA[i]=dEMA[i-1]+D*(dData-dEMA[i-1]);
            EMA[i] = (dEMA[i]);
        }
        return EMA;
    }

    public double[] exponentialAverage_DI(double[] data, int interval, int nStartIndex) {
        if (data == null)  return null;
        double D = 2.00/(interval+1.00);
        double[] dEMA = new double[data.length];
        double[] EMA = new double[data.length];
        //if(data.length <= interval)return EMA;
        if(data.length < 1)return EMA;
        EMA[0] = data[0];
        dEMA[0] = data[0];
        //int subTotal=0;
        //최초값을 단순이동평균으로 구한다
        //for(int j=interval; j>-1; j--) {
        //    subTotal += data[j];
        //}
        //dEMA[interval]= (subTotal /interval);
        //for(int i=interval+1 ; i<data.length ; i++) {
        for(int i=nStartIndex ; i<data.length ; i++) {
            //2019. 04. 22 by hyh - 시그널 에러 수정 >>
            double dData = data[i];

            if (Double.isNaN(dData) || Double.isInfinite(dData)) {
                dData = 0.0f;
            }
            //2019. 04. 22 by hyh - 시그널 에러 수정 <<

            if(i<nStartIndex)
                dEMA[i]=0;
            else if(i==nStartIndex)
                dEMA[i]=dData;
            else
                dEMA[i]=dEMA[i-1]+D*(dData-dEMA[i-1]);
            EMA[i] = (dEMA[i]);
        }
        return EMA;
    }
    //===============================
    // 지수이동평균선의 값을 일차원 배열로 리턴
    //===============================
    public int[] exponentialAverage(int[][] data, int interval) {
        if( (data == null)) {
            return null;
        }
        int dLen = data.length;
        double D = 2.00/(interval+1.00);
        double[] dEMA = new double[dLen];
        int[] EMA = new int[dLen];
        EMA[0] = data[0][0];
        int subTotal=0;
        if(dLen <= interval)return EMA;
        for(int j=interval; j>-1; j--) {
            subTotal += data[j][0];
        }
        dEMA[0]= (int)(subTotal /interval);
        //dEMA[0] = (double)data[0][0];
        for(int i=1 ; i<dLen ; i++) {
            dEMA[i] = (D*data[i][0]+(1-D)*dEMA[i-1]);
            EMA[i] = (int)dEMA[i];
        }
        return EMA;
    }
    //===============================
    // 해당 컬럼 지수이동평균선의 값을 일차원 배열로 리턴
    //===============================
    public int[] exponentialAverage(int[][] data, int index,int interval) {
        if( (data == null)) {
            return null;
        }
        int dLen = data.length;
        double D = 2.00/(interval+1.00);
        double[] dEMA = new double[dLen];
        int[] EMA = new int[dLen];
        EMA[0] = data[0][index];
        if(dLen <= interval)return EMA;
        //dEMA[0] = (double)data[0][index];
        //dEMA[0] = makeAveragePart(data,interval);
        int subTotal=0;
        for(int j=interval; j>-1; j--) {
            subTotal += data[j][index];
        }
        dEMA[0]= (int)(subTotal /interval);

        for(int i=1 ; i<dLen ; i++) {
            dEMA[i] = (D*data[i][index]+(1-D)*dEMA[i-1]);
            EMA[i] = (int)dEMA[i];
        }
        return EMA;
    }
    //===============================
    // 해당 컬럼 지수이동평균선의 값을 일차원 배열로 리턴
    //===============================
    public int exponentialAveragePart(int[][] data, int index,int interval) {
        if( (data == null)) {
            return 0;
        }
        double D = 2.00/(interval+1.00);
        double dEMA = 0;
        int EMA =0;
        int len = data.length-1;
        EMA = data[len][index];
        dEMA = (double)data[len][index];
        if(data.length <= interval)return EMA;
        for(int i= len; i>len-interval; i--) {
            dEMA = (D*data[i][index]+(1-D)*dEMA);
            EMA = (int)dEMA;
        }
        return EMA;
    }
    //===========================
    // 이동평균
    //===========================
//    public int makeAveragePart(int[] data, int interval){
//        int averageData=0;
//        double subTotal = 0.;
//        int len = data.length-1;
//        if(data.length <= interval)return averageData;
//        for(int j=len; j<len-interval ; j--) {
//            subTotal += data[j];
//        }
//        averageData= (int)((subTotal /interval)+0.5);
//        return averageData;
//    }
    public int[] makeAverage(int[] data, int interval,int startPos){
        int[] averageData = new int[data.length];
        if(data.length <= interval)return averageData;
        for(int i = interval+startPos-1 ; i < averageData.length ; i++) {
            //for(int i = interval+startPos ; i < averageData.length ; i++) {
            double subTotal = 0.;
            for(int j= i ; j>i-interval ; j--) {
                subTotal += data[j];
            }
            averageData[i] = (int)((subTotal /interval)+0.5);
        }
        return averageData;
    }
    public double[] makeAverage(double[] data, int interval){
        int dCnt = data.length;
        double[] averageData = new double[dCnt];

        //2015. 2. 3 볼린저밴드 값 0일때 그리지 않게>>
//        if(dCnt <= interval)return averageData;
        if(dCnt < interval)return averageData;
        //2015. 2. 3 볼린저밴드 값 0일때 그리지 않게<<
        double subTotal = 0.;
        for(int i = interval-1 ; i < dCnt ; i++) {
            if(i<interval-1) {
                averageData[i]=0;
            } else {
                subTotal=0;
                for(int j= i ; j>i-interval ; j--) {
                    subTotal += data[j];
                }
                averageData[i] = (subTotal /interval);
            }

        }
        return averageData;
    }
    public double[] makeAverage(double[] data, int interval,int startPos){
        int dCnt = data.length;
        double[] averageData = new double[dCnt];

        //2015. 2. 3 볼린저밴드 값 0일때 그리지 않게>>
//        if(dCnt <= interval)return averageData;
        if(dCnt < interval)return averageData;
        //2015. 2. 3 볼린저밴드 값 0일때 그리지 않게<<
        double subTotal = 0.;
        for(int i = interval+startPos-1 ; i < dCnt ; i++) {
            if(i<interval+startPos-1) {
                averageData[i]=0;
            } else {
                subTotal=0;
                for(int j= i ; j>i-interval ; j--) {
                    subTotal += data[j];
                }
                averageData[i] = (subTotal /interval);
            }

        }
        return averageData;
    }
    //     public int makeAveragePart(int[][] data, int interval){
//        int averageData=0;
//        double subTotal = 0.;
//        int len = data.length-1;
//        int end = len-interval;
//        if(data.length <= interval)return averageData;
//        if(end<0) return 0;
//        for(int j=len; j>end; j--) {
//            subTotal += data[j][0];
//        }
//        averageData= (int)((subTotal /interval)+0.5);
//        return averageData;
//    }
    public int[] makeAverage(int[][] data, int interval){
//        int[] averageData = new int[data.length];
//        if(data.length <= interval)return averageData;
//        int aLen = averageData.length;
//        for(int i = interval-1 ; i < aLen ; i++) {
//            double subTotal = 0.;
//            for(int j= i ; j>i-interval ; j--) {
//                subTotal += data[j][0];
//            }
//            averageData[i] = (int)((subTotal /interval)+0.5);
//        }
//        return averageData;

        int dLen = data.length;
        double subTotal = 0;
        int[] averageData = new int[dLen];
//        if(dLen <= interval)return averageData;
//        int aLen = averageData.length;
        for(int count = interval-1 ; count < dLen ; count++) {
            if(count<interval-1) {
                averageData[count]=0;
            } else {
                subTotal = 0;
                for(int j=count; j>count-interval; j--) {
                    subTotal += data[j][0];
                }
                averageData[count]=((int)subTotal/interval);
            }

//            int start = count+1-interval;
//            if((count+1)==interval || subTotal==0) {
//            	for(int i=start; i<(interval+start); i++) {
//            		subTotal = subTotal+data[i][0];
//            	}
//            } else {
//            	int bPredata = data[start-1][0];
//            	int bEnddata = data[interval+start-1][0];
//            	subTotal = subTotal-bPredata+bEnddata;
//            }
//            averageData[count] = (int)((subTotal /interval)+0.5);
        }
        return averageData;
    }
    public double[] makeAverageD(double[]data, int interval){
        int dLen = data.length;
        double[] averageData = new double[dLen];
        if(dLen <= interval)return averageData;
        for(int i = interval-1 ; i < dLen ; i++) {
            double subTotal = 0.;
            for(int j= i ; j>i-interval ; j--) {
                subTotal += data[j];
            }
            averageData[i] = (subTotal /interval);
        }
        return averageData;
    }

    public double[] yesAccumN(double[]data, int interval){
        int dLen = data.length;
        double[] averageData = new double[dLen];
        if(dLen <= interval)return averageData;
        for(int i = 0 ; i < dLen ; i++) {
            if(i==0)
                averageData[i] = data[i];
            else {
                double subTotal = 0.;
                for (int j = i; j > i - interval; j--) {
                    if(j<0)
                        break;
                    subTotal += data[j];
                }
                averageData[i] = subTotal;
            }
        }
        return averageData;
    }

    public int[] getControlValue(){
        return interval;
    }
    public void changeControlValue(int[] value){
        //2012. 8. 30 거래량 상승, 하락, 보합 색상 설정  : I98
//    	if(this.getGraphTitle().equals("거래량"))
        //2015. 1. 13 by lyk - 동일지표 이름을 화면에 보여줄때는 COMUtil.JIPYO_ADD_REMARK 이전까지 이름을 사용한다.
        if(this.getGraphTitle().equals("거래량") || this.getGraphTitle().indexOf("거래량"+COMUtil.JIPYO_ADD_REMARK)!=-1)
        //2015. 1. 13 by lyk - 동일지표 이름을 화면에 보여줄때는 COMUtil.JIPYO_ADD_REMARK 이전까지 이름을 사용한다. end
        {
            if(value.length <10)
                return;

            _cvm.setVolDrawType(value[0]);

            DrawTool dt = (DrawTool)_drawTool.elementAt(0);
            dt.subTitle = "거래량";

            int i=0;

            //2021.08.11 by lyk - kakaopay - 다크테마의 ColorSet이 적용되므로, 다크테마, 저장값을 사용하지 않도록 한다. >>
//            int[] upColor = {value[i*3+1],value[i*3+2],value[i*3+3]};
//            dt.setUpColor(upColor);
//
            i++;
//            int[] downColor = {value[i*3+1],value[i*3+2],value[i*3+3]};
//            dt.setDownColor(downColor);
//
            i++;
//            int[] sameColor = {value[i*3+1],value[i*3+2],value[i*3+3]};
//            dt.setSameColor(sameColor);
            //2021.08.11 by lyk - kakaopay - 다크테마의 ColorSet이 적용되므로, 다크테마, 저장값을 사용하지 않도록 한다. <<

            return;
        }
        if(this.getGraphTitle().indexOf("일본식봉")!=-1)
        {
            if(value.length <20)
                return;

            if(_drawTool==null)
                return;
            DrawTool dt = (DrawTool)_drawTool.elementAt(0);
            int i=0;

            //2021.08.11 by lyk - kakaopay - 다크테마의 ColorSet이 적용되므로, 다크테마, 저장값을 사용하지 않도록 한다. >>
            //팔레트
            //양봉상승
//            int[] upColor = {value[i*3+2],value[i*3+1+2],value[i*3+2+2]};
//            dt.setUpColor(upColor);
//
//            //양봉하락
            i++;
//            int[] upColor_2 = {value[i*3+2],value[i*3+1+2],value[i*3+2+2]};
//            dt.setUpColor2(upColor_2);
//
//            //음봉상승
            i++;
//            int[] downColor_2 = {value[i*3+2],value[i*3+1+2],value[i*3+2+2]};
//            dt.setDownColor2(downColor_2);
//
//            //음봉하락
            i++;
//            int[] downColor = {value[i*3+2],value[i*3+1+2],value[i*3+2+2]};
//            dt.setDownColor(downColor);
            //2021.08.11 by lyk - kakaopay - 다크테마의 ColorSet이 적용되므로, 다크테마, 저장값을 사용하지 않도록 한다. <<

            //checkbox 설정부터는  rgb 처럼 값이 여러개가 한세트가 아니므로 인덱스 맞춰줌 
            i = i*4+2;

            //체크박스
            dt.setFillUp(value[i++]==1?true:false);
            dt.setFillUp2(value[i++]==1?true:false);
            dt.setFillDown2(value[i++]==1?true:false);
            dt.setFillDown(value[i++]==1?true:false);
            //콤보박스
            _cvm.setCandle_basePrice(value[i++]);
            _cvm.setCandle_sameColorType(value[i++]);

            if(value.length >=22)
            {
                //2012. 11. 2 최대최소, 로그 체크박스 세팅  : I107
                _cvm.setIsCandleMinMax(value[i++]==1?true:false);
                _cvm.setIsLog(value[i++]==1?true:false);
                if(value.length >=23) {
                    _cvm.setIsInverse(value[i++] == 1 ? true : false);
                }
                //2016.12.14 by LYH >>갭보정 추가
                if(value.length >=24) {
                    _cvm.setIsGapRevision(value[i++] == 1 ? true : false);
                }
                //2016.12.14 by LYH >>갭보정 추가 end
            }
////            //2015. 3. 4 차트 테마 메인따라가기 추가>>
//            else {
//                COMUtil.bIsAutoTheme = false;
//                COMUtil.skinType = COMUtil.SKIN_WHITE;
//            }
////            //2015. 3. 4 차트 테마 메인따라가기 추가<<
//            return;
        }


        if(this.getGraphTitle().indexOf("Heikin-Ashi")!=-1)
        {
            if(value.length <20)
                return;

            DrawTool dt = (DrawTool)_drawTool.elementAt(0);
            int i=0;

            dt.subTitle = "Heikin-Ashi";

            //2021.08.11 by lyk - kakaopay - 다크테마의 ColorSet이 적용되므로, 다크테마, 저장값을 사용하지 않도록 한다. >>
            //팔레트
            //양봉상승
//            int[] upColor = {value[i*3+2],value[i*3+1+2],value[i*3+2+2]};
//            dt.setUpColor(upColor);
//
//            //양봉하락
            i++;
//            int[] upColor_2 = {value[i*3+2],value[i*3+1+2],value[i*3+2+2]};
//            dt.setUpColor2(upColor_2);
//
//            //음봉상승
            i++;
//            int[] downColor_2 = {value[i*3+2],value[i*3+1+2],value[i*3+2+2]};
//            dt.setDownColor2(downColor_2);
//
//            //음봉하락
            i++;
//            int[] downColor = {value[i*3+2],value[i*3+1+2],value[i*3+2+2]};
//            dt.setDownColor(downColor);
            //2021.08.11 by lyk - kakaopay - 다크테마의 ColorSet이 적용되므로, 다크테마, 저장값을 사용하지 않도록 한다. <<

            //checkbox 설정부터는  rgb 처럼 값이 여러개가 한세트가 아니므로 인덱스 맞춰줌
            i = i*4+2;

            //체크박스
            dt.setFillUp(value[i++]==1?true:false);
            dt.setFillUp2(value[i++]==1?true:false);
            dt.setFillDown2(value[i++]==1?true:false);
            dt.setFillDown(value[i++]==1?true:false);
            //콤보박스
            _cvm.setCandle_basePrice(value[i++]);
            _cvm.setCandle_sameColorType(value[i++]);

            if(value.length >=22)
            {
                //2012. 11. 2 최대최소, 로그 체크박스 세팅  : I107
                _cvm.setIsCandleMinMax(value[i++]==1?true:false);
                _cvm.setIsLog(value[i++]==1?true:false);
            }
//            //2015. 3. 4 차트 테마 메인따라가기 추가>>
//            else {
//                COMUtil.bIsAutoTheme = false;
//                COMUtil.skinType = COMUtil.SKIN_WHITE;
//            }
//            //2015. 3. 4 차트 테마 메인따라가기 추가<<
            return;
        }

        if(value==null || interval == null || value.length<interval.length || _drawTool == null )return;
        //2023.06.27 by SJW - 거래량 이동평균선 "10일" 추가 >>
        if (this.getGraphTitle().equals("거래량이동평균")) {
            boolean isOldVersion = (value[1] == 20);

            if (!isOldVersion) {
                value[1] = 10;
            }
        }
        //2023.06.27 by SJW - 거래량 이동평균선 "10일" 추가 <<
        boolean isAverageOldVersion = false;
        //2024.03.05 by SJW - 이동평균선 두번째 설정값 안먹는 현상 수정 >>
//        if (this.getGraphTitle().equals("주가이동평균")) {
//            isAverageOldVersion = (value[1] == 20);
//
//            if (isAverageOldVersion) {
//                value[32] = value[26];
//                value[26] = value[20];
//                value[20] = 0;
//            }
//        }
        //2024.03.05 by SJW - 이동평균선 두번째 설정값 안먹는 현상 수정 <<
        for(int i=0;i<interval.length;i++){
//            interval[i]= value[i];
            if(!isAverageOldVersion) {
                interval[i]= value[i];
            }

            //2020. 05. 27 by hyh - 일목균형표 선행스팬1이 미래영역에 적용 안되는 에러 수정 >>
            if (this.getGraphTitle().startsWith("일목균형")) {
                if (i == 2) {
                    _cvm.futureMargin = interval[2];
                }
            }
            //2020. 05. 27 by hyh - 일목균형표 선행스팬1이 미래영역에 적용 안되는 에러 수정 <<

            if(this.getGraphTitle().endsWith("이동평균")){
                if(this.getGraphTitle().indexOf("주가")!=-1)
                    _cvm.average_title[i]=value[i];
                else
                    _cvm.vol_average_title[i]=value[i];
            }
        }

        // jhy --test (이격률) >>
        if(this.getGraphTitle().contains("이격")){
            for(int i=0;i<_drawTool.size();i++){
                DrawTool dt = (DrawTool)_drawTool.elementAt(i);
                dt.setViewTitle(interval[i]+"");
            }
        }
        if(this.getGraphTitle().contains("이격도[지수]")){
            for(int i=0;i<_drawTool.size();i++){
                DrawTool dt = (DrawTool)_drawTool.elementAt(i);
                dt.setViewTitle(interval[i]+"");
            }
        }
        // jhy --test (이격률) <<

        if(this.getGraphTitle().endsWith("이동평균")){
            for(int i=0;i<_drawTool.size();i++){
                DrawTool dt = (DrawTool)_drawTool.elementAt(i);
                //dt.setVisible(_cvm.average_state[i]);
                //if(_cvm.average_state[i]){
                dt.setViewTitle(interval[i]+"");
                if(this.getGraphTitle().indexOf("주가")!=-1)
                    dt.setTitle("이평 "+interval[i]);
                else
                    dt.setTitle("거래량이평"+interval[i]);

                //}else{
                //    dt.setViewTitle("");
                //}
            }
        }
        if(this.getGraphTitle().indexOf("매물대")!=-1)
        {
            DrawTool dt = (DrawTool)_drawTool.elementAt(0);
            for (int i=0; i<3; i++) {

                if(value.length <= interval.length+i*5)
                {
                    break;
                }
                setLineThick(value[interval.length+i*5], i);

                //2021.08.11 by lyk - kakaopay - 다크테마의 ColorSet이 적용되므로, 다크테마, 저장값을 사용하지 않도록 한다. >>
                int[] color = {value[interval.length+i*5+1],value[interval.length+i*5+2],value[interval.length+i*5+3]};
                if(i==0)
                    dt.setUpColor(color);
                else if(i==1)
                    dt.setSameColor(color);
                else if(i==2)
                    dt.setDownColor(color);
                //2021.08.11 by lyk - kakaopay - 다크테마의 ColorSet이 적용되므로, 다크테마, 저장값을 사용하지 않도록 한다. <<

                setLineVisible((value[interval.length+i*5+4]==1)?true:false, i);
            }

            //2013. 9. 5 매물대설정 값 문자열 보이기 여부 처리>>
            dt.setStandScaleLabelShow((value[interval.length+3*5]==1)?true:false);
            //2013. 9. 5 매물대설정 값 문자열 보이기 여부 처리>>
        }
        //2015. 3. 2 라인바 적용 후 지표설정 저장값 꼬임>>
        else if(value.length >= interval.length+_drawTool.size()*6)	// 보조지표 bar 타입 유형 변경 기능  5->6
        {
            int n_DrawSettingDataLen;
            if(base == null)
            {
                n_DrawSettingDataLen = 6;	//색굵기설정에 포함되는 데이터가 몇개인지 (굵기, R, G, B, 보이기여부체크, drawType2)
                for (int i=0; i<_drawTool.size(); i++) {
                    if (!isAverageOldVersion) {
                        setLineThick(value[interval.length + i * n_DrawSettingDataLen], i);

                        //2021.08.11 by lyk - kakaopay - 다크테마의 ColorSet이 적용되므로, 다크테마, 저장값을 사용하지 않도록 한다. >>
                        //2023.11.07 by lyk - kakaopay - 이평설정 색상 적용 >>
                        int[] color = {value[interval.length+i*n_DrawSettingDataLen+1],value[interval.length+i*n_DrawSettingDataLen+2],value[interval.length+i*n_DrawSettingDataLen+3]};
                        //2024.01.16 by CYJ - kakaopay - 테마 변경시 이평색상 변경 적용 >> 하드 코딩... 아이폰과 통일(lyk)
                        if (graphTitle.equals("주가이동평균") && _drawTool.get(0).subTitle.contains("이평")) {
                            if (COMUtil.getSkinType() == COMUtil.SKIN_BLACK) {
                                if (color[0] == 25 && color[1] == 28 && color[2] == 32) {
                                    color = new int[]{238, 239, 240};
                                } else if (color[0] == 68 && color[1] == 75 && color[2] == 82) {
                                    color = new int[]{188, 192, 196};
                                } else if (color[0] == 135 && color[1] == 146 && color[2] == 156) {
                                    color = new int[]{118, 124, 130};
                                } else if (color[0] == 206 && color[1] == 212 && color[2] == 218) {
                                    color = new int[]{66, 70, 74};
                                } else if (color[0] == 179 && color[1] == 0 && color[2] == 0) {
                                    color = new int[]{197, 61, 61};
                                } else if (color[0] == 229 && color[1] == 14 && color[2] == 14) {
                                    color = new int[]{235, 71, 71};
                                }
                            } else {
                                if (color[0] == 238 && color[1] == 239 && color[2] == 240) {
                                    color = new int[]{25, 28, 32};
                                } else if (color[0] == 188 && color[1] == 192 && color[2] == 196) {
                                    color = new int[]{68, 75, 82};
                                } else if (color[0] == 118 && color[1] == 124 && color[2] == 130) {
                                    color = new int[]{135, 146, 156};
                                } else if (color[0] == 66 && color[1] == 70 && color[2] == 74) {
                                    color = new int[]{206, 212, 218};
                                } else if (color[0] == 197 && color[1] == 61 && color[2] == 61) {
                                    color = new int[]{179, 0, 0};
                                } else if (color[0] == 235 && color[1] == 71 && color[2] == 71) {
                                    color = new int[]{229, 14, 14};
                                }
                            }
                        }
                        //2024.01.16 by CYJ - kakaopay - 테마 변경시 이평색상 변경 적용 << 하드 코딩... 아이폰과 통일(lyk)
                        setLineColor(color,i);
                        //2023.11.07 by lyk - kakaopay - 이평설정 색상 적용 <<
                        //2021.08.11 by lyk - kakaopay - 다크테마의 ColorSet이 적용되므로, 다크테마, 저장값을 사용하지 않도록 한다. <<

                        //2017.05.11 by LYH >> 전략(신호, 강약) 추가
                        if (_drawTool.get(0) instanceof SignalDraw)
                            setUpVisible((value[interval.length + i * n_DrawSettingDataLen + 4] == 1) ? true : false, i);
                        else
                            //2017.05.11 by LYH >> 전략(신호, 강약) 추가 end
                            setLineVisible((value[interval.length + i * n_DrawSettingDataLen + 4] == 1) ? true : false, i);

                        setDrawType2(value[interval.length + i * n_DrawSettingDataLen + 5], i);
                    }//drawType2
                    //2015. 1. 13 보조지표 bar 타입 유형 변경 기능<<
                }

                //2016.07.28 by LYH >> 데이터기준, 이평선타입별(단순,가중,지수,기하) 계산
                int nIndex = interval.length+_drawTool.size()*6;
                if(value.length >= nIndex+20)
                {
                    //boolean bFirst = true;
                    //2016.07.28 by LYH << 데이터기준, 이평선타입별(단순,가중,지수,기하) 계산
                    for (int i=0; i<_drawTool.size(); i++) {
                        DrawTool dt = (DrawTool)_drawTool.elementAt(i);
                        dt.setDataType(value[nIndex+i*2]);
                        dt.setAverageCalcType(value[nIndex+1+i*2]);

//                        if(bFirst && dt.isVisible()) {
//                            if (dt.getAverageCalcType() == ChartViewModel.AVERAGE_GENERAL){
//                                dt.setTitle("이평 " + interval[i] + "/단순 MA");
//                            }
//                            //가중이평
//                            else if (dt.getAverageCalcType() == ChartViewModel.AVERAGE_WEIGHT) {
//                                dt.setTitle("이평 " + interval[i] + "/가중 MA");
//                            }
//                            //지수이평
//                            else if (dt.getAverageCalcType() == ChartViewModel.AVERAGE_EXPONENTIAL) {
//                                dt.setTitle("이평 " + interval[i] + "/지수 MA");
//                            }
//                            //기하이평
//                            else if (dt.getAverageCalcType() == ChartViewModel.AVERAGE_GEOMETIC) {
//                                dt.setTitle("이평 " + interval[i] + "/기하 MA");
//                            }
//                            bFirst = false;
//                        }
                    }
                }
            }
            else
            {
                //2015. 1. 13 보조지표 bar 타입 유형 변경 기능
                n_DrawSettingDataLen = 5;	//색굵기설정에 포함되는 데이터가 몇개인지 (굵기, R, G, B, 보이기여부체크, drawType2)
                if(value.length >=interval.length+_drawTool.size()*6+base.length*2+1)
                {
                    n_DrawSettingDataLen = 6;
                }
                for (int i=0; i<_drawTool.size(); i++) {
                    //2015. 1. 13 보조지표 bar 타입 유형 변경 기능>>
                    setLineThick(value[interval.length+i*n_DrawSettingDataLen], i);

                    //2021.08.11 by lyk - kakaopay - 다크테마의 ColorSet이 적용되므로, 다크테마, 저장값을 사용하지 않도록 한다. >>
                    int[] color = {value[interval.length+i*n_DrawSettingDataLen+1],value[interval.length+i*n_DrawSettingDataLen+2],value[interval.length+i*n_DrawSettingDataLen+3]};
                    setLineColor(color,i);
                    //2021.08.11 by lyk - kakaopay - 다크테마의 ColorSet이 적용되므로, 다크테마, 저장값을 사용하지 않도록 한다. <<
                    //2017.05.11 by LYH >> 전략(신호, 강약) 추가
                    if(_drawTool.get(0) instanceof SignalDraw)
                        setUpVisible((value[interval.length+i*n_DrawSettingDataLen+4]==1)?true:false, i);
                    else
                        //2017.05.11 by LYH >> 전략(신호, 강약) 추가 end
                        setLineVisible((value[interval.length+i*n_DrawSettingDataLen+4]==1)?true:false, i);

                    if(n_DrawSettingDataLen>5)
                        setDrawType2(value[interval.length+i*n_DrawSettingDataLen+5], i);	//drawType2
                    //2015. 1. 13 보조지표 bar 타입 유형 변경 기능<<
                }

                //2013. 9. 3 지표마다 기준선 설정 추가>>

                //2015. 1. 13 보조지표 bar 타입 유형 변경 기능>>
//            	int nDataLen = interval.length+_drawTool.size()*5;
                int nDataLen = interval.length+_drawTool.size()*n_DrawSettingDataLen;
                //2015. 1. 13 보조지표 bar 타입 유형 변경 기능<<
                int nIndex;

                boolean bSetBase = false;
                for(int i = 0; i<base.length;i++)
                {
                    nIndex = nDataLen+(i*2);
                    if(nIndex+1<value.length)
                    {
                        //기준선 가시성여부 (체크값) 세팅
                        //(체크박스, 기준선값)  두개가 한쌍이라서 인덱스를 2개씩 넘어가야 하므로  i*2 해줌
                        boolean b  = ((value[nIndex] == 1) ? true : false);

                        setBaseLineVisibility(i, b);

                        //기준선값 세팅
                        base[i] = value[nIndex+1];
                        if(b && !bSetBase)
                        {
                            setBaseLine(base[i]);
                            bSetBase = true;
                        }
                    }
                }
                if(!bSetBase)
                    setBaseLine(0);
                //2014. 9. 11 매매 신호 보기 기능 추가>>
                nDataLen += (base.length * 2);	//변수설정, 색굵기값, 기준선설정값을 다 지난 후의 인덱스

                if(value.length >=interval.length+_drawTool.size()*6+base.length*2+1)
                {
                    setSellingSignalShow((value[nDataLen] == 1) ? true : false);
                    //2016.07.28 by LYH >> 데이터기준, 이평선타입별(단순,가중,지수,기하) 계산
                    nDataLen += 1;	//매매 신호 보기 여부
                }
                else
                {
                    setSellingSignalShow(true);
                }
                //2014. 9. 11 매매 신호 보기 기능 추가<<
            }

            //2019. 07. 04 by hyh - BollingerBand 기준가, 계산방법 추가 >>
            if (value.length >= interval.length + _drawTool.size() * 6 + 1) {
                if (getGraphTitle().equals("Bollinger Band")
                || getGraphTitle().startsWith("Bollinger Band [보조]")
                || getGraphTitle().startsWith("Band %B")
                || getGraphTitle().equals("Band Width")
                || getGraphTitle().startsWith("%B")) {
                    if (value.length > (int) (interval.length + _drawTool.size() * 6) + 1) {
                        int nIndexDataTypeBollingerBand = (int) interval.length + _drawTool.size() * 6;
                        int nIndexCalcTypeBollingerBand = (int) interval.length + _drawTool.size() * 6 + 1;

                        int nDataTypeBollingerBand = (int) value[nIndexDataTypeBollingerBand];
                        int nCalcTypeBollingerBand = (int) value[nIndexCalcTypeBollingerBand];

                        this.dataTypeBollingerband = nDataTypeBollingerBand;
                        this.calcTypeBollingerband = nCalcTypeBollingerBand;
                    }
                }
            }
            //2019. 07. 04 by hyh - BollingerBand 기준가, 계산방법 추가 <<

            //2020.03.20 주가이동평균 가격, 이평선 타입 수정 - hjw >>
//            if (getGraphTitle().equals("주가이동평균")) {
//                try {
//                    int nDataTypeIndex = interval.length + _drawTool.size() * 8;
//                    if(value.length >= nDataTypeIndex+2) {
//                        this.dataTypeAverage = value[nDataTypeIndex];
//                        this.calcTypeAverage = value[nDataTypeIndex+1];
//                    }
////                    if((value.length - 3 > 2)) {
////                        this.dataTypeAverage = value[value.length-3];
////                        this.calcTypeAverage = value[value.length-2];
////                    }
//                } catch (IndexOutOfBoundsException e) {
//
//                }
//            }
            //2020.03.20 주가이동평균 가격, 이평선 타입 수정 - hjw <<

            //2013. 9. 3 지표마다 기준선 설정 추가>>

            //2017.05.11 by LYH >> 전략(신호, 강약) 추가
            if((_drawTool.get(0) instanceof SignalDraw) && value.length >= interval.length + _drawTool.size()*6+5)
            {
                int i=1;
                setDownLineT(value[interval.length+i*n_DrawSettingDataLen], 0);

                //2021.08.11 by lyk - kakaopay - 다크테마의 ColorSet이 적용되므로, 다크테마, 저장값을 사용하지 않도록 한다. >>
//                int[] color = {value[interval.length+i*n_DrawSettingDataLen+1],value[interval.length+i*n_DrawSettingDataLen+2],value[interval.length+i*n_DrawSettingDataLen+3]};
//                setDownColor(color, 0);
                //2021.08.11 by lyk - kakaopay - 다크테마의 ColorSet이 적용되므로, 다크테마, 저장값을 사용하지 않도록 한다. <<
                setDownVisible((value[interval.length+i*n_DrawSettingDataLen+4]==1)?true:false, 0);
            }
            //2017.05.11 by LYH >> 전략(신호, 강약) 추가 end

            //2020.10.06 by.lyj - 일목균형표 선행스팬 해제 시 영역 제거 >>
            if (this.getGraphTitle().indexOf("일목균형표") != -1) {
                DrawTool dt3 = _drawTool.get(3); //선행스팬1
                DrawTool dt4 = _drawTool.get(4); //선행스팬2

                if ((!dt3.isVisible() && !dt4.isVisible())) {
                    _cvm.futureMargin = 0;
                } else {
                    _cvm.futureMargin = interval[2];
                }

                //2021.05.24 by hhk - 구름대 채우기 >>
                int nDataLen = interval.length+_drawTool.size()*n_DrawSettingDataLen;

                if(value.length >=interval.length+_drawTool.size()*6+1)
                {
                    //2023.11.28 by SJW - 일목균형표 구름대 채우게 설정 >>
//                    boolean tmpIsFillCloud = (value[nDataLen] == 1) ? true : false;
                    boolean tmpIsFillCloud = true;
                    //2023.11.28 by SJW - 일목균형표 구름대 채우게 설정 <<
                    dt3.setFillCloud(tmpIsFillCloud);
                    dt4.setFillCloud(tmpIsFillCloud);
                    setFillCloud(tmpIsFillCloud);
                } else {
                    setFillCloud(false);
                }

                try
                {
                    setFillCloud((value[nDataLen] == 1) ? true : false);
                }
                catch(Exception e)
                {
                    setFillCloud(false);
                }
                //2021.05.24 by hhk - 구름대 채우기 <<
            }
            //2020.10.06 by.lyj - 일목균형표 선행스팬 해제 시 영역 제거 <<
        }
        else if(value.length >= interval.length+_drawTool.size()*5)
        {
            for (int i=0; i<_drawTool.size(); i++) {
                setLineThick(value[interval.length+i*5], i);

                //2021.08.11 by lyk - kakaopay - 다크테마의 ColorSet이 적용되므로, 다크테마, 저장값을 사용하지 않도록 한다. >>
//                int[] color = {value[interval.length+i*5+1],value[interval.length+i*5+2],value[interval.length+i*5+3]};
//                setLineColor(color,i);
                //2021.08.11 by lyk - kakaopay - 다크테마의 ColorSet이 적용되므로, 다크테마, 저장값을 사용하지 않도록 한다. <<

                setLineVisible((value[interval.length+i*5+4]==1)?true:false, i);
            }
            if(base != null)
            {
                int nDataLen = interval.length+_drawTool.size()*5;
                int nIndex;
                for(int i = 0; i<base.length;i++)
                {
                    nIndex = nDataLen+(i*2);
                    if(nIndex+1<value.length)
                    {
                        //기준선 가시성여부 (체크값) 세팅
                        //(체크박스, 기준선값)  두개가 한쌍이라서 인덱스를 2개씩 넘어가야 하므로  i*2 해줌
                        boolean b  = ((value[nIndex] == 1) ? true : false);

                        setBaseLineVisibility(i, b);

                        //기준선값 세팅
                        base[i] = value[nIndex+1];
                    }
                }

                nDataLen += (base.length * 2);

                try
                {
                    setSellingSignalShow((value[nDataLen] == 1) ? true : false);
                }
                catch(Exception e)
                {
                    setSellingSignalShow(true);
                }
            }
        }
        //2015. 3. 2 라인바 적용 후 지표설정 저장값 꼬임<<
    }

    public void setLineColor(int[] c, int nIndex)
    {
        for (int i=0; i<_drawTool.size(); i++) {
            if(i == nIndex)
            {
                DrawTool dt = (DrawTool)_drawTool.elementAt(i);
                dt.setUpColor(c);
                break;
            }
        }
    }

    public void setLineThick(int thick, int nIndex)
    {
        for (int i=0; i<_drawTool.size(); i++) {
            if(i == nIndex)
            {
                DrawTool dt = (DrawTool)_drawTool.elementAt(i);
                dt.setLineT(thick);
                break;
            }
        }
    }

    public void setLineVisible(boolean visible, int nIndex)
    {
        for (int i=0; i<_drawTool.size(); i++) {
            if(i == nIndex)
            {
                DrawTool dt = (DrawTool)_drawTool.elementAt(i);
                dt.setVisible(visible);
                break;
            }
        }
    }

    public void setDownLineT(int thick, int nIndex)
    {
        for (int i=0; i<_drawTool.size(); i++) {
            if(i == nIndex)
            {
                DrawTool dt = (DrawTool)_drawTool.elementAt(i);
                dt.setDownLineT(thick);
                break;
            }
        }
    }

    //2015. 1. 13 보조지표 bar 타입 유형 변경 기능>>
    /**
     * drawtool의 drawType2를 변경해준다
     * @param drawType2 : 적용할 drawType2 값
     * @param nIndex : 적용할 drawTool 의 위치(index)
     * */
    public void setDrawType2(int drawType2, int nIndex)
    {
        if(drawType2 == 1)	    //2015. 3. 2 라인바 적용 후 지표설정 저장값 꼬임
            return;
        for (int i=0; i<_drawTool.size(); i++) {
            if(i == nIndex)
            {
                DrawTool dt = (DrawTool)_drawTool.elementAt(i);
                if(1 == dt.getDrawType1() && (0 == dt.getDrawType2() || 7 == dt.getDrawType2()))	    //2015. 3. 2 라인바 적용 후 지표설정 저장값 꼬임
                {
                    dt.setDrawType2(drawType2);
                }
                break;
            }
        }
    }
    //2014. 9. 16 보조지표 bar 타입 유형 변경 기능<<

    //2015. 3. 2 라인바 적용 후 지표설정 저장값 꼬임>>
    public void setBaseLine(int base)
    {
        for (int i=0; i<_drawTool.size(); i++) {
            DrawTool dt = (DrawTool)_drawTool.elementAt(i);
            if(1 == dt.getDrawType1() && (0 == dt.getDrawType2() || 7 == dt.getDrawType2()))	//2015. 2. 27 MACD OSC 등 라인/바 추가 후 지표값변경하면 drawtype이 꼬임
            {
                dt.setStandVal(base);
            }
        }
    }
    //2015. 3. 2 라인바 적용 후 지표설정 저장값 꼬임<<

    //    private void initContolValue(){
//        switch(_graphKind){
//            case ChartUtil.JBONG:
//            break;
//            case ChartUtil.ABONG:
//            break;
//            case ChartUtil.PLINE:
//            break;
//            case ChartUtil.STANDSCALE:
//            	//2012. 7. 2 매물대분석 지표정보 추가 
//            	interval = new int[1];s_interval=new String[1];
//            	interval[0] =10;
//                s_interval[0]="매물대 개수";
//            break;
//            case ChartUtil.REVERSE_CLOCK:
//            break;
//            case ChartUtil.PNF:
//            break;
//            case ChartUtil.TCHANGE:
//            break;
//            case ChartUtil.VOLUME:
//            break;
//            case ChartUtil.MACD_OSC:
//            case ChartUtil.MACD:
//                interval = new int[3];s_interval=new String[3];
//                interval[0] = 12;interval[1] = 26;interval[2]=9;
//                s_interval[0]="단기이동평균";s_interval[1]="장기이동평균";s_interval[2]="Signal";
//                base= new int[1];
//                base[0]=0;
//            break;
//            case ChartUtil.STOCH_SLW:
//                interval = new int[3];s_interval=new String[3];base = new int[2];
//                //interval[0] = 15;interval[1] = 5;interval[2] = 3;
//                interval[0] = 5;interval[1] = 3;interval[2] = 3;
//                s_interval[0]="기간";s_interval[1]="Slow%K";s_interval[2]="Slow%D";
//                base[0] = 20;base[1]=80;
//            break;
//            case ChartUtil.STOCH_FST:
//                interval = new int[2];s_interval=new String[2];base = new int[2];
//                //interval[0] = 15;interval[1] = 5;
//                interval[0] = 5;interval[1] = 3;
//                s_interval[0]="Fast%K";s_interval[1]="Fast%D";
//                base[0] = 20;base[1]=80;
//            break;
//            case ChartUtil.DMI:
//                interval = new int[1];s_interval=new String[1];
//                interval[0] = 14;
//                s_interval[0]="기간";
//            break;
//            case ChartUtil.ADX:
//                interval = new int[2];s_interval=new String[2];
//                interval[0] = 14; interval[1] = 14;
//                s_interval[0]="DMI기간"; s_interval[1]="ADX기간";
//            break;
//            case ChartUtil.AB_Ratio:
//                interval = new int[1];s_interval=new String[1];
//                interval[0] = 25;
//                s_interval[0]="기간";
//            break;
//            case ChartUtil.RSI:
//                interval = new int[2];s_interval=new String[2];base = new int[2];
//                interval[0] = 10;interval[1]=5; 
//                s_interval[0]="기간";
//                s_interval[1]="signal";
//                base[0]=30; base[1]=70;
//                
//            break;
//            case ChartUtil.OBV:
//                //interval = new int[1];
//                //interval[0] = 5;
//            	//2012. 7. 2 OBV  값 추가
//            	interval = new int[1];s_interval=new String[1];
//            	interval[0] =5;
//                s_interval[0]="Signal";
//            break;
//            case ChartUtil.REVERSE:
//                interval = new int[1];s_interval=new String[1];
//                interval[0] =10;
//                s_interval[0]="기간";
//            break;
//            case ChartUtil.SONAR:
//                interval = new int[3];s_interval=new String[3];
//                interval[0] = 10;interval[1] = 10;interval[2] = 5;
//                s_interval[0]="EMA기간";s_interval[1]="SONAR기간";s_interval[2]="SONAR이동평균";
//                base= new int[1];
//                base[0]=0;
//            break;
//            case ChartUtil.STDEV:
//                interval = new int[1];s_interval=new String[1];
//                interval[0] = 20;
//                s_interval[0]="이동평균";
//            break;
//            case ChartUtil.VMAO:
//                interval = new int[2];s_interval=new String[2];
//                interval[0] = 5; interval[1] = 20;
//                s_interval[0]="단기이동평균";s_interval[1]="장기이동평균";
//            break;
//            case ChartUtil.VR:
//                interval = new int[1];s_interval=new String[1];base= new int[1];
//                interval[0] = 20;
//                s_interval[0]="기간";
//                base[0] = 200;
//            break;
//            case ChartUtil.DISPARITY:
//                interval = new int[2];s_interval=new String[2];
//                interval[0] = 20;interval[1] = 60;
//                s_interval[0]="단기이동평균";s_interval[1]="장기이동평균";
//            break;
//            case ChartUtil.PSYCO:
//                interval = new int[1];s_interval=new String[1];
//                interval[0] = 10;
//                s_interval[0]="기간";
//                base= new int[2];
//                base[0]=20;base[1]=80;
//            break;
//            case ChartUtil.WILLIAMS:
//                interval = new int[2];s_interval = new String[2];
//                interval[0] = 5;interval[1] = 5;
//                s_interval[0]="%R";s_interval[1]="Signal";
//                base= new int[2];
//                base[0]=20;base[1]=80;
//            break;
//            case ChartUtil.Momentum:
//                interval = new int[2];s_interval = new String[2];
//                interval[0] = 5;interval[1] = 5;
//                s_interval[0] = "기간";
//                s_interval[1] = "이동평균";
//            break;
//            case ChartUtil.CCI:
//                interval = new int[1];s_interval = new String[1];
//                interval[0] = 9;
//                s_interval[0] = "기간";
//                
//            break;
//            case ChartUtil.CO:
//                interval = new int[2];s_interval=new String[2];
//                //interval[0]= 10;interval[1]= 20;
//                interval[0]= 3;interval[1]= 10;
//                s_interval[0]="단기이동평균";s_interval[1]="장기이동평균";
//            break;
//            case ChartUtil.MAC:
//                interval = new int[1];s_interval = new String[1];
//                interval[0] = 5; s_interval[0] = "기간";
//            break;
//            case ChartUtil.MFI:
//                interval = new int[1];s_interval = new String[1];
//                interval[0]=14;s_interval[0] = "기간";
//            break;
//            case ChartUtil.MI:
//                interval = new int[1];s_interval = new String[1];
//                interval[0]=25;s_interval[0] = "기간";
//            break;
//            case ChartUtil.NVI:
//            	//2012. 7. 2 NVI 설정값 추가 
//            	interval = new int[1];s_interval = new String[1];
//            	interval[0]=14;s_interval[0] = "Signal";
//            break;
//            case ChartUtil.TRIX:
//                interval = new int[2];s_interval = new String[2];
//                interval[0]=5;interval[1]=3;
//                s_interval[0] = "단기이동평균";s_interval[1] = "Signal";
//                base= new int[1];
//                base[0]=0;
//            break;
//            case ChartUtil.PARABOLIC:
//                interval = new int[1];s_interval = new String[1];
//                interval[0] = 20;
//                s_interval[0] = "AF최대값";
//            break;
//            case ChartUtil.BOLLINGER:
//                interval = new int[1];s_interval = new String[1];
//                interval[0] = 20;s_interval[0] = "기간";
//            break;
//            case ChartUtil.ENVELOPE:
//                interval = new int[2];s_interval=new String[2];
//                //interval[0] = 20;interval[1] = 5;
//                interval[0] = 13;interval[1] = 8;
//                s_interval[0] = "기간";s_interval[1] = "가감값";
//            break;
//            case ChartUtil.GLANCE_BALANCE:
//                interval = new int[3];s_interval=new String[3];
//                interval[0] =9;interval[1] = 26;interval[2]=52;
//                s_interval[0] ="전환선기간";s_interval[1] = "기준,후행,선행1기간";s_interval[2]="선행스팬2기간";
//            break;
//            case ChartUtil.RAINBOW:
//                interval = new int[3];s_interval=new String[3];
//                interval[0]=1;
//                interval[1]=1;
//                interval[2]=20;
//                s_interval[0] ="이동평균 기간";s_interval[1] = "간격";s_interval[2]="개수";
//                
//            break;
//            case ChartUtil.PIVOT:
//            break;
//            case ChartUtil.PAVERAGE:
//                int num=_cvm.average_title.length;
//                interval = new int[num];s_interval=new String[num];
//                for(int i=0;i<num;i++){
//                    interval[i]=_cvm.average_title[i];
//                    s_interval[i]="이평"+(i+1)+"기간";
//                }
//            break;
//            case ChartUtil.VAVERAGE:
//                interval = new int[6];
//                interval[0] = 5;interval[1]=10;interval[2]=20;interval[3]=60;interval[4]=120;interval[5]=240;
//            break;
//            case ChartUtil.ZIGZAG:
//                interval = new int[1];s_interval=new String[1];
//                interval[0] =5;
//                s_interval[0]="변동폭(%)";
//            break;
//        }
//        if(interval!=null){
//            org_interval=new int[interval.length];
//            System.arraycopy(interval,0,org_interval,0,interval.length);
//        }
//    }
    public void initControlValue(){
        //2012. 7. 20  차트기본값 (변수값) 변경 (우투 이슈 #4897 ~ 4900).  일부 case 에 없거나 기존 interval, s_interval 인덱스 갯수가 다른 것은 해당되는 값만 수정 함
        switch(_graphKind){
            case ChartUtil.JBONG:
                break;
            case ChartUtil.ABONG:
                break;
            case ChartUtil.PLINE:
                break;
            case ChartUtil.STANDSCALE:
                //2012. 7. 2 매물대분석 지표정보 추가
                interval = new int[1];s_interval=new String[1];
                interval[0] =7;
                s_interval[0]="매물대 개수";
                break;
            case ChartUtil.REVERSE_CLOCK:
                interval = new int[1];s_interval=new String[1];
                interval[0] =20;
                s_interval[0]="이동평균";
                break;
            case ChartUtil.PNF:
                break;
            case ChartUtil.TCHANGE:
                break;
            case ChartUtil.RENKO:
//                interval = new int[3];s_interval=new String[3];
//                interval[0] = 5;interval[1] = 10;interval[2]=20;
//                s_interval[0]="이평기간1";s_interval[1]="이평기간2";s_interval[2]="이평기간3";
                break;
            case ChartUtil.VOLUME:
                break;
            case ChartUtil.MACD_OSC:
                interval = new int[3];s_interval=new String[3];
                interval[0] = 12;interval[1] = 26;interval[2]=9;
                s_interval[0]="단기이동평균";s_interval[1]="장기이동평균";s_interval[2]="Signal";
                break;
            case ChartUtil.MACD:
                interval = new int[3];s_interval=new String[3];
                interval[0] = 12;interval[1] = 26;interval[2]=9;
                s_interval[0]="단기이동평균";s_interval[1]="장기이동평균";s_interval[2]="Signal";
                base= new int[1];
                base[0]=0;
                break;
            case ChartUtil.STOCH_SLW:
                interval = new int[3];s_interval=new String[3];base = new int[2];
                //interval[0] = 15;interval[1] = 5;interval[2] = 3;
//            interval[0] = 5;interval[1] = 3;interval[2] = 3;
                //2012. 10. 22 변수값 수정  : I105
//          interval[0] = 14;interval[1] = 5;interval[2] = 3;
                interval[0] = 5;interval[1] = 3;interval[2] = 3;
                s_interval[0]="기간";s_interval[1]="SlowK";s_interval[2]="SlowD";
                base[0] = 20;base[1]=80;
                break;
            case ChartUtil.STOCH_FST:
                interval = new int[2];s_interval=new String[2];
                //interval[0] = 15;interval[1] = 5;
//            interval[0] = 5;interval[1] = 3;
                //2012. 10. 22 변수값 수정  : I105
//          interval[0] = 14;interval[1] = 3;
                interval[0] = 5;interval[1] = 3;
                s_interval[0]="FastK";s_interval[1]="FastD";
                base = new int[2];
                base[0] = 20;base[1]=80;
                break;
            case ChartUtil.DMI:
                interval = new int[1];s_interval=new String[1];
                interval[0] = 14;
                s_interval[0]="기간";
                base = new int[2];
                base[0] = 10;base[1]=30;
                break;
            //2019. 06. 28 by hyh - TSF 지표 추가 >>
            case ChartUtil.TSF:
                interval = new int[2];s_interval=new String[2];
                interval[0] = 14; interval[1] = 3;
                s_interval[0]="기간1"; s_interval[1]="기간2";
                break;
            //2019. 06. 28 by hyh - TSF 지표 추가 <<
            //2019. 10. 16 by hyh - MAO 지표 추가 >>
            case ChartUtil.MAO:
                interval = new int[2];s_interval=new String[2];
                interval[0] = 5; interval[1] = 20;
                s_interval[0]="단기이평"; s_interval[1]="장기이평";
                break;
            //2019. 10. 16 by hyh - MAO 지표 추가 <<
            //2015. 2. 13 LRS 지표 추가>>
            case ChartUtil.LRS:
                interval = new int[2];s_interval = new String[2];
                interval[0]=14;s_interval[0] = "기간";
                interval[1]=3;s_interval[1] = "Signal";
//                interval[1]=9;s_interval[1] = "Signal";
                //16.09.01 기준선 수정
//                base = new int[2];
//                base[0] = 20;base[1]=80;
                break;
            //2015. 2. 13 LRS 지표 추가<<
            case ChartUtil.ADX:
//                interval = new int[2];s_interval=new String[2];
//                interval[0] = 14; interval[1] = 14;
//                s_interval[0]="DMI기간"; s_interval[1]="ADX기간";
                interval = new int[1];s_interval=new String[1];
                interval[0] = 14;
                s_interval[0]="ADX기간";
                base = new int[1];
                base[0] = 20;
                break;
            //2015.06.08 by lyk - 신규지표 추가
            case ChartUtil.RCI:
                interval = new int[3];s_interval=new String[3];
                interval[0] = 9; interval[1] = 13; interval[2] = 18; //interval[3] = 26;
                s_interval[0]="RCI1"; s_interval[1]="RCI2"; s_interval[2]="RCI3";// s_interval[3]="RCI4";
                //16.09.01 기준선 수정
//                base = new int[1];
//                base[0] = 20;
                break;
            case ChartUtil.ROC:
                interval = new int[2];s_interval=new String[2];
                interval[0] = 12; interval[1]=9;
                s_interval[0]="기간"; s_interval[1]="Signal";
                base = new int[1];
                base[0] = 100;
                break;
            case ChartUtil.SROC:
                interval = new int[3];s_interval=new String[3];
                interval[0] = 10; interval[1] = 20; interval[2] = 8;
                s_interval[0]="이평기간"; s_interval[1]="기간"; s_interval[2]="signal";
                //16.09.01 기준선 수정
//                base = new int[1];
//                base[0] = 100;
                break;
            case ChartUtil.VROC:
                interval = new int[1];s_interval=new String[1];
                interval[0] = 12;
                s_interval[0]="기간";
                base = new int[1];
                base[0] = 0; //16.09.01 기준선 수정 100 -> 0
                break;
            case ChartUtil.NCO:
                interval = new int[2];s_interval=new String[2];
                interval[0] = 12;interval[1]=9;
                s_interval[0]="기간";s_interval[1]="Signal";
                base = new int[1];
                base[0] = 0;  //16.09.01 기준선 수정 0 -> 100
                break;
            case ChartUtil.CV:
                interval = new int[2];s_interval=new String[2];
                interval[0] = 14; interval[1] = 9;
                s_interval[0]="기간"; s_interval[1]="Signal";
                base = new int[1];
                base[0] = 100; //16.09.01 기준선 수정 100 -> 0
                break;
            case ChartUtil.RMI:
                interval = new int[2];s_interval=new String[2];
//            interval[0] = 10;interval[1]=5; 
                interval[0] = 14;interval[1]=5;
                s_interval[0]="적용기간";
                s_interval[1]="이평기간";
                base = new int[2];
                base[0]=30; base[1]=70;

                break;
            case ChartUtil.VHF:
                interval = new int[1];s_interval=new String[1];
//            interval[0] = 10;interval[1]=5; 
                interval[0] = 14;
                s_interval[0]="기간";
                base = new int[1];
                base[0]=30;
                break;
            case ChartUtil.SIGMA:
//                interval = new int[2];s_interval = new String[2];
//                interval[0] = 20;interval[1] = 9;s_interval[0] = "기간";s_interval[1] = "Signal";
                interval = new int[1];s_interval = new String[1];
                interval[0] = 20;s_interval[0] = "기간";
                base = new int[1];
                base[0] = 0;
                break;
            case ChartUtil.ATR:
                interval = new int[2];s_interval = new String[2];
                interval[0] = 14; interval[1]= 9;
                s_interval[0] = "기간"; s_interval[1]="Signal";
                break;
            case ChartUtil.OSCP:
                interval = new int[3];s_interval = new String[3];
                interval[0] = 10;interval[1] = 20;interval[2] = 9;s_interval[0] = "기간1";s_interval[1] = "기간2";s_interval[2] = "Signal";
                base = new int[1];
                base[0] = 0;
                break;
//            case ChartUtil.OSCV:
//                interval = new int[2];s_interval = new String[2];
//                interval[0] = 20;interval[1] = 9;s_interval[0] = "기간1";s_interval[1] = "기간2";
//                break;
            case ChartUtil.PVT:
                interval = new int[1];s_interval = new String[1];
                interval[0] = 10;s_interval[0] = "Signal";
                //16.09.01 기준선 수정
//                base = new int[1];
//                base[0] = 100;
                break;
            case ChartUtil.LRL:
                interval = new int[1];s_interval = new String[1];
                interval[0]=14;s_interval[0] = "기간";
                //16.09.01 기준선 수정
//                base = new int[2];
//                base[0] = 20;base[1]=80;
                break;
            //2015.06.08 by lyk - 신규지표 추가 end
            case ChartUtil.AB_Ratio:
                interval = new int[2];s_interval=new String[2];
                interval[0] = 20;interval[1] = 20;
                s_interval[0]="AR기간";s_interval[1]="BR기간";
                base = new int[1];
                base[0]=100;
                break;
            //2015. 1. 13 ADLine 지표 추가>>
            case ChartUtil.ADLINE:
                //2015. 3. 3 ADLine Signal 만 남기기
                interval = new int[1];s_interval=new String[1];
                interval[0] = 9;
                s_interval[0]="Signal";
                break;
            //2015. 1. 13 ADLine 지표 추가<<
            case ChartUtil.RSI:
                interval = new int[2];s_interval=new String[2];
//            interval[0] = 10;interval[1]=5; 
                interval[0] = 14;interval[1]=6;
                s_interval[0]="기간";
                s_interval[1]="signal";
                base = new int[2];
                base[0]=30; base[1]=70;

                break;
            case ChartUtil.OBV:
                //interval = new int[1];
                //interval[0] = 5;
                //2012. 7. 2 OBV  값 추가
                interval = new int[1];s_interval=new String[1];
//        	interval[0] =5;
                interval[0] =9;
                s_interval[0]="Signal";
                base = new int[1];
                base[0] = 0;
                break;
            case ChartUtil.REVERSE:
                interval = new int[2];s_interval=new String[2];
                //2012. 10. 22 변수값 수정  : I105
//            interval[0] =10;
                interval[0] =12;  interval[1] = 24;
                s_interval[0]="기간1"; s_interval[1] = "기간2";
                break;
            case ChartUtil.SONAR:
                interval = new int[3];s_interval=new String[3];
//                interval = new int[2];s_interval=new String[2];
                interval[0] = 20;interval[1] = 9;interval[2] = 9;
                //s_interval[0]="EMA기간";s_interval[1]="SONAR기간";s_interval[2]="SONAR이동평균";
                s_interval[0]="이동평균"; s_interval[1]="SONAR기간";s_interval[2]="Signal";
                base= new int[1];
                base[0]=0;
                break;
            case ChartUtil.STDEV:
                interval = new int[1];s_interval=new String[1];
                interval[0] = 20;
                s_interval[0]="이동평균";
                break;
            case ChartUtil.VMAO:
                interval = new int[3];s_interval=new String[3];
                //2012. 10. 22 변수값 수정  : I105
//          interval[0] = 10; interval[1] = 20;
                interval[0] = 5; interval[1] = 10;interval[2]=9;
                s_interval[0]="단기이동평균";s_interval[1]="장기이동평균";s_interval[2]="Signal";
                break;
            case ChartUtil.VR:
                interval = new int[1];s_interval=new String[1];
                interval[0] = 20;
                s_interval[0]="기간";
                base= new int[2];
                base[0]=100;base[1]=400;
                break;
            case ChartUtil.DISPARITY://이격도
                interval = new int[4];s_interval=new String[4];
                //2012. 10. 22 변수값 수정  : I105
//            interval[0] = 20;interval[1] = 60;
                interval[0] = 5;interval[1] = 10; interval[2] = 20; interval[3] = 60;
                s_interval[0]="이격1";s_interval[1]="이격2";s_interval[2]="이격3";s_interval[3]="이격4";
                base= new int[2];
                base[0]=90;base[1]=100;
                break;
            case ChartUtil.PSYCO: //심리도
                interval = new int[2];s_interval=new String[2];
                interval[0] = 10; interval[1] = 15;
                s_interval[0]="기간";s_interval[1] ="Signal";
                //16.09.01 기준선 수정
                base= new int[2];
                //base[0]=20;base[1]=80;
                base[0]=25;base[1]=75;
                break;
            case ChartUtil.WILLIAMS:
                interval = new int[2];s_interval = new String[2];
                //2012. 10. 22 변수값 수정  : I105
//            interval[0] = 14;interval[1] = 9;
                interval[0] = 14;interval[1] = 3;
                s_interval[0]="R";s_interval[1]="Signal";
                base= new int[2];
                base[0]=-20;base[1]=-80;
                break;
            //2016.09.09 신규지표 추가 >>

            case ChartUtil.STOCH_OSC:
                interval = new int[3];s_interval=new String[3];
                //interval[0] = 20;interval[1] = 5;
                interval[0] = 12;interval[1] = 26;interval[2] = 9;
                s_interval[0] = "기간1";s_interval[1] = "기간2";s_interval[2] = "기간3";
                break;
            case ChartUtil.PROC:
                interval = new int[2];s_interval=new String[2];
                //interval[0] = 20;interval[1] = 5;
                interval[0] = 12;interval[1] = 9;
                s_interval[0] = "구간수";s_interval[1] = "Signal";
                base = new int[1];
                base[0] = 0;
                break;
            case ChartUtil.EOM:
                interval = new int[2];s_interval=new String[2];
                //interval[0] = 20;interval[1] = 5;
                interval[0] = 10;interval[1] = 3;
                s_interval[0] = "기간";s_interval[1] = "Signal";
                base = new int[1];
                base[0] = 0;
                break;
            case ChartUtil.NEWPSYCO:
                interval = new int[1];s_interval=new String[1];
                //interval[0] = 20;interval[1] = 5;
                interval[0] = 6;
                s_interval[0] = "기간";
                break;

            //2016.09.09 신규지표 추가 <<
            case ChartUtil.Momentum:
                interval = new int[2];s_interval = new String[2];
                //2012. 10. 22 변수값 수정  : I105
//            interval[0] = 12;interval[1] = 9;
                interval[0] = 10;interval[1] = 9;
                s_interval[0] = "기간";
                s_interval[1] = "이동평균";
                base = new int[1];
                base[0] = 100;
                break;
            case ChartUtil.CCI:
                interval = new int[2];s_interval = new String[2];
                //2012. 10. 22 변수값 수정  : I105
                interval[0] = 20;

                s_interval[0] = "기간";
                interval[1] = 8;
                s_interval[1] = "Signal";
                base = new int[2];
                base[0] = -100;base[1]=100;
                break;
            case ChartUtil.CO: //chaikin's osc
                interval = new int[3];s_interval=new String[3];
                //interval[0]= 10;interval[1]= 20;
                interval[0]= 3;interval[1]= 10;interval[2]=9;
                s_interval[0]="단기이동평균";s_interval[1]="장기이동평균";
                s_interval[2]="Signal";
                base = new int[1];
                base[0] = 0;
                break;
            case ChartUtil.MAC:
                interval = new int[3];s_interval = new String[3];
                interval[0] = 10;  interval[1]=10;  interval[2]=10;
                s_interval[0] = "기간"; s_interval[1] = "상한율"; s_interval[2] = "하한율";
                break;
            case ChartUtil.MFI:
                interval = new int[2];s_interval = new String[2];
//          interval = new int[2];s_interval = new String[2];
                interval[0]=14;s_interval[0] = "기간";
                interval[1]=9;s_interval[1] = "Signal";
                base = new int[2];
                base[0] = 20;base[1]=80;
                break;
            case ChartUtil.MI: //Mass Index
                interval = new int[2];s_interval = new String[2];
                interval[0]=25;interval[1]=3;
                s_interval[0] = "기간";s_interval[1]="Signal";
                base = new int[1];
                base[0] = 25;
                break;
            case ChartUtil.NVI:
                //2012. 7. 2 NVI 설정값 추가
                interval = new int[1];s_interval = new String[1];
                //2012. 10. 22 변수값 수정  : I105
//        	interval[0]=14;s_interval[0] = "Signal";
                interval[0]=9;s_interval[0] = "Signal";
                break;
            case ChartUtil.TRIX:
                interval = new int[2];s_interval = new String[2];
                //2012. 10. 22 변수값 수정  : I105
//          interval[0]=12;interval[1]=5;
                interval[0]=12;interval[1]=9;
                s_interval[0] = "단기이동평균";s_interval[1] = "Signal";
                base= new int[1];
                base[0]=0;

//            interval = new int[3];s_interval = new String[3];
//            interval[0]=12;interval[1]=9;interval[2]=5;
//            s_interval[0] = "단기이동평균";s_interval[1]="장기이동평균";s_interval[2] = "Signal";
//            base= new int[1];
//            base[0]=0;
                break;
            case ChartUtil.PARABOLIC:
                interval = new int[2];s_interval = new String[2];
                interval[0] = 20;
                interval[1] = 2;
                s_interval[0] = "AF최대값";
                s_interval[1] = "AF증가분";
                break;
            case ChartUtil.BOLLINGER:
                interval = new int[2];s_interval = new String[2];
                interval[0] = 20;interval[1] = 200;s_interval[0] = "기간";s_interval[1] = "표준편차"; //2023.11.23 by CYJ - kakaopay 볼린저밴드 첫번쨰 설정값 이동평균 > 기간  //2023.11.27 by CYJ - kakaopay 볼린저밴드 두번째 설정값 표준편차 승수 > 표준편차
                break;
            case ChartUtil.BOLLINGER_2:
                interval = new int[2];s_interval = new String[2];
                interval[0] = 20;interval[1] = 200;s_interval[0] = "이동평균";s_interval[1] = "표준편차승수";
                break;
            case ChartUtil.ENVELOPE:
                interval = new int[3];s_interval=new String[3];
                //interval[0] = 20;interval[1] = 5;
                //2020.11.27 by HJW - Envelope 지표 소수점 처리 >>
//                interval[0] = 13;interval[1] = 8;interval[2] = 8;
                //2023.06.27 by SJW - 엔벨로프 지표 추가 >>
//                interval[0] = 13;interval[1] = 800;interval[2] = 800;
                interval[0] = 13;interval[1] = 800;interval[2] = 800;
                //2023.06.27 by SJW - 엔벨로프 지표 추가 <<
                //2020.11.27 by HJW - Envelope 지표 소수점 처리 <<
                s_interval[0] = "기간";s_interval[1] = "상승율(%)";s_interval[2] = "하락율(%)";
                break;
            case ChartUtil.GLANCE_BALANCE:
                interval = new int[5];s_interval=new String[5];
                interval[0]=26;interval[1] =9;interval[2] = 26;interval[3]=52;interval[4]=26;
                s_interval[0] ="기준기간";s_interval[1] ="전환기간";s_interval[2] = "선행1기간";s_interval[3] = "선행2기간";s_interval[4]="후행기간";
                break;
            case ChartUtil.RAINBOW:
                interval = new int[3];s_interval=new String[3];
                interval[0]=5;
                interval[1]=5;
                interval[2]=20;
                s_interval[0] ="이동평균 기간";s_interval[1] = "간격";s_interval[2]="개수";
                break;
            //2012. 8. 8  외국인보유비중등 변수설정 삭제 : I77 I78 I79 I80  I81  I82
            case ChartUtil.PIVOT:
                interval = new int[1];s_interval=new String[1];
                interval[0]=0;s_interval[0]="";
                break;
            case ChartUtil.PIVOTPRE:
                interval = new int[1];s_interval=new String[1];
                interval[0]=0;s_interval[0]="";
                break;
            case ChartUtil.PAVERAGE:
            {
                int num=_cvm.average_title.length;
                //{5,20,60,120, 200, 300, 400, 500, 600, 700}
                interval = new int[10];s_interval=new String[10];
                //2023.06.27 by SJW - 주가이동평균 라인("10","20","200") 추가 >>
//                interval[0] = 5;
//                interval[1] = 20;
//                interval[2] = 60;
//                interval[3] = 120;
//                interval[4] = 240;
//                interval[5] = 250;
//                interval[6] = 260;
//                interval[7] = 270;
//                interval[8] = 280;
//                interval[9] = 300;
                interval[0] = 5;
                interval[1] = 10;
                interval[2] = 20;
                interval[3] = 60;
                interval[4] = 120;
                interval[5] = 200;
                //2023.06.29 by SJW - 주가이동평균 라인 "240"일 추가 요청 >>
//                interval[6] = 250;
                interval[6] = 240;
                //2023.06.29 by SJW - 주가이동평균 라인 "240"일 추가 요청 <<
                interval[7] = 260;
                interval[8] = 270;
                interval[9] = 280;
                //2023.06.27 by SJW - 주가이동평균 라인("10","20","200") 추가 <<
                for(int i=0;i<10;i++){
                    //interval[i]=_cvm.average_title[i];
                    //s_interval[i]="이평"+(i+1)+"기간";
                    s_interval[i]="기간"; //2023.11.15 by CYJ - kakaopay 주가이동평균 설명변경 - 이평i기간 > 기간
                }
            }
            break;
            case ChartUtil.VAVERAGE:
            {
//                int num = _cvm.vol_average_title.length;
//                interval = new int[num];
//                s_interval=new String[num];
                interval = new int[4];s_interval=new String[4];
                //2023.06.27 by SJW - 거래량 이동평균선 "10일" 추가 >>
//                interval[0]=5;interval[1] =20;interval[2] = 60;interval[3]=120;
                interval[0]=5; interval[1]=10; interval[2]=20; interval[3]=60;
                //2023.06.27 by SJW - 거래량 이동평균선 "10일" 추가 <<
                for(int i=0;i<4;i++){
                    //interval[i]=_cvm.vol_average_title[i];
                    s_interval[i]="이평"+(i+1)+"기간";
                }
            }
            break;
            case ChartUtil.ZIGZAG:
                interval = new int[1];s_interval=new String[1];
                interval[0] =5;
                s_interval[0]="변동폭(%)";
                break;
            //2014.01.11 by LYH >> Price Channel 지표 추가
            case ChartUtil.DEMARK:
//                interval = new int[1];s_interval=new String[1];
//                interval[0] =5;
//                s_interval[0]="기간";
                interval = new int[1];s_interval=new String[1];
                interval[0]=0;s_interval[0]="";
                break;
            case ChartUtil.PRICE_CHANNEL:
                interval = new int[1];s_interval=new String[1];
                //interval[0] = 20;interval[1] = 5;
                interval[0] = 5;
                s_interval[0] = "기간";
                break;
            //2014.01.11 by LYH << Price Channel 지표 추가
            case ChartUtil.BANDB:
                interval = new int[3];s_interval=new String[3];
                interval[0] = 20; interval[1] = 200; interval[2] = 3;
                s_interval[0]="이평기간"; s_interval[1]="표준편차승수"; s_interval[2]="Signal";
                break;
            //2017.05.11 by LYH >> 전략(신호, 강약) 추가
            case ChartUtil.INDICATOR_ADXRSTRATEGY:
                interval = new int[2]; s_interval=new String[2];
                interval[0] = 14; interval[1] = 14;
                s_interval[0] = "ADX"; s_interval[1] = "ADXR";
                break;
            case ChartUtil.INDICATOR_CCIBASELINE:
                interval = new int[1]; s_interval=new String[1];
                interval[0] = 9;
                s_interval[0] = "CCI";
                break;
            case ChartUtil.INDICATOR_CCIOVERSOLDOVERBOUGHT:
                interval = new int[1]; s_interval=new String[1];
                interval[0] = 9;
                s_interval[0] = "CCI";
                break;
            case ChartUtil.INDICATOR_DISPARITYSIGNAL:
                interval = new int[3]; s_interval=new String[3];
                interval[0] = 20; interval[1] = 98; interval[2] = 106;
                s_interval[0] = "Disparity"; s_interval[1] = "LPercent"; s_interval[2] = "SPercent";
                break;
            case ChartUtil.INDICATOR_DMISIGNAL:
                interval = new int[1]; s_interval=new String[1];
                interval[0] = 14;
                s_interval[0] = "DMI";
                break;
            case ChartUtil.INDICATOR_GOLDENDEADCROSSEMA:
                interval = new int[2]; s_interval=new String[2];
                interval[0] = 5; interval[1] = 20;
                s_interval[0] = "단기"; s_interval[1] = "장기";
                break;
            case ChartUtil.INDICATOR_GOLDENDEADCROSS:
                interval = new int[2]; s_interval=new String[2];
                interval[0] = 5; interval[1] = 20;
                s_interval[0] = "단기"; s_interval[1] = "장기";
                break;
            case ChartUtil.INDICATOR_MACDBASELINE:
                interval = new int[2]; s_interval=new String[2];
                interval[0] = 12; interval[1] = 26;
                s_interval[0] = "단기"; s_interval[1] = "장기";
                break;
            case ChartUtil.INDICATOR_PARABOLICSIGNAL:
                interval = new int[2]; s_interval=new String[2];
                interval[0] = 20; interval[1] = 2;
                s_interval[0] = "AF최대값"; s_interval[1] = "AF증가분";
                break;
            case ChartUtil.INDICATOR_SONARMOMENTUM:
                interval = new int[2]; s_interval=new String[2];
                interval[0] = 20; interval[1] = 9;
                s_interval[0] = "적용기간1"; s_interval[1] = "적용기간2";
                break;
            case ChartUtil.INDICATOR_SONARMOMENTUMSIGNAL:
                interval = new int[3]; s_interval=new String[3];
                interval[0] = 20; interval[1] = 9; interval[2] = 9;
                s_interval[0] = "적용기간1"; s_interval[1] = "적용기간2"; s_interval[2] = "Signal";
                break;
            case ChartUtil.INDICATOR_STOCHASTICSKD:
                interval = new int[3]; s_interval=new String[3];
                interval[0] = 12; interval[1] = 3; interval[2] = 3;
                s_interval[0] = "FastK"; s_interval[1] = "SlowK"; s_interval[2] = "SlowD";
                break;
            case ChartUtil.INDICATOR_WILLIAMSR:
                interval = new int[1]; s_interval=new String[1];
                interval[0] = 14;
                s_interval[0] = "R";
                break;
            //2017.05.11 by LYH >> 전략(신호, 강약) 추가 end
            case ChartUtil.INDICATOR_GOLDENDEADCROSSMA_EMA:
                interval = new int[2]; s_interval=new String[2];
                interval[0] = 9; interval[1] = 9;
                s_interval[0] = "단순"; s_interval[1] = "지수";
                break;
            case ChartUtil.INDICATOR_GOLDENDEADCROSSMA_WMA:
                interval = new int[2]; s_interval=new String[2];
                interval[0] = 17; interval[1] = 2;
                s_interval[0] = "단순"; s_interval[1] = "가중";
                break;
            case ChartUtil.INDICATOR_GOLDENDEADCROSS_MULTI:
                interval = new int[3]; s_interval=new String[3];
                interval[0] = 10; interval[1] = 20; interval[2] = 60;
                s_interval[0] = "단기"; s_interval[1] = "중기"; s_interval[2] = "장기";
                break;
            case ChartUtil.INDICATOR_MACDSIGNAL:
                interval = new int[3];s_interval=new String[3];
                interval[0] = 12;interval[1] = 26;interval[2]=9;
                s_interval[0]="단기";s_interval[1]="장기";s_interval[2]="시그널";
                break;
            case ChartUtil.INDICATOR_MAOBASELINE:
                interval = new int[2]; s_interval=new String[2];
                interval[0] = 25; interval[1] = 4;
                s_interval[0] = "단기"; s_interval[1] = "장기";
                break;
            case ChartUtil.INDICATOR_PVISIGNAL:
                interval = new int[1];s_interval = new String[1];
                interval[0] = 9;
                s_interval[0] = "시그널";
                break;
            case ChartUtil.PRICEBOX:
                interval = new int[1];s_interval = new String[1];
                interval[0] = 24;;
                s_interval[0] = "기간";;
                break;
            case ChartUtil.BANDBSTOCH:
                interval = new int[5];s_interval=new String[5];
                interval[0] = 20; interval[1] = 200; interval[2] = 14; interval[3] = 3; interval[4] = 3;
                s_interval[0]="이평기간"; s_interval[1]="표준편차"; s_interval[2]="Slow%K 기간1"; s_interval[3]="Slow%K 기간2"; s_interval[4]="Slow%D 기간";
                break;
            case ChartUtil.LRSSTOCH:
                interval = new int[4];s_interval=new String[4];
                interval[0] = 14; interval[1] = 14; interval[2] = 3; interval[3] = 3;
                s_interval[0]="LRS 기간"; s_interval[1]="Slow%K 기간1"; s_interval[2]="Slow%K 기간2"; s_interval[3]="Slow%D 기간";
                break;
            case ChartUtil.MACDSTOCH:
                interval = new int[5];s_interval=new String[5];
                interval[0] = 12; interval[1] = 26; interval[2] = 14; interval[3] = 3; interval[4] = 3;
                s_interval[0]="단기이평"; s_interval[1]="장기이평"; s_interval[2]="Slow%K 기간1"; s_interval[3]="Slow%K 기간2"; s_interval[4]="Slow%D 기간";
                break;
            case ChartUtil.MOMENTUMSTOCH:
                interval = new int[4];s_interval=new String[4];
                interval[0] = 14; interval[1] = 14; interval[2] = 3; interval[3] = 3;
                s_interval[0]="모멘텀 기간"; s_interval[1]="Slow%K 기간1"; s_interval[2]="Slow%K 기간2"; s_interval[3]="Slow%D 기간";
                break;
            case ChartUtil.OBVMOMENTUM:
                interval = new int[2];s_interval=new String[2];
                interval[0] = 14; interval[1] = 9;
                s_interval[0]="모멘텀 기간"; s_interval[1]="모멘텀 Signal";
                break;
            case ChartUtil.OBVSTOCH:
                interval = new int[3];s_interval=new String[3];
                interval[0] = 5; interval[1] = 3; interval[2] = 3;
                s_interval[0]="Fast%K 기간"; s_interval[1]="Slow%K 기간"; s_interval[2]="Slow%D 기간";
                break;
            case ChartUtil.ROCSTOCH:
                interval = new int[4];s_interval=new String[4];
                interval[0] = 12; interval[1] = 14; interval[2] = 3; interval[3] = 3;
                s_interval[0]="ROC 기간"; s_interval[1]="Slow%K 기간1"; s_interval[2]="Slow%K 기간2"; s_interval[3]="Slow%D 기간";
                break;
            case ChartUtil.RSIMACD:
                interval = new int[4];s_interval=new String[4];
                interval[0] = 14; interval[1] = 12; interval[2] = 26; interval[3] = 9;
                s_interval[0]="RSI 기간"; s_interval[1]="단기이평"; s_interval[2]="장기이평"; s_interval[3]="Signal";
                break;
            case ChartUtil.RSISTOCH:
                interval = new int[4];s_interval=new String[4];
                interval[0] = 14; interval[1] = 14; interval[2] = 3; interval[3] = 3;
                s_interval[0]="RSI 기간"; s_interval[1]="Slow%K 기간1"; s_interval[2]="Slow%K 기간2"; s_interval[3]="Slow%D 기간";
                break;
            case ChartUtil.SONARPSYCO:
                interval = new int[3];s_interval=new String[3];
                interval[0] = 20; interval[1] = 9; interval[2] = 10;
                s_interval[0]="이평기간"; s_interval[1]="비교기간"; s_interval[2]="심리도기간";
                base = new int[2];
                base[0] = 25; base[1] = 75;
                break;
            case ChartUtil.STOCHRSI:
                interval = new int[4];s_interval=new String[4];
                interval[0] = 14; interval[1] = 3; interval[2] = 14; interval[3] = 3;
                s_interval[0]="Fast%K 기간"; s_interval[1]="Fast%D 기간"; s_interval[2]="RSI 기간"; s_interval[3]="Signal";
                break;
            case ChartUtil.DEMA:
                interval = new int[1];s_interval = new String[1];
                interval[0] = 20;;
                s_interval[0] = "기간";
                break;
            case ChartUtil.TEMA:
                interval = new int[1];s_interval = new String[1];
                interval[0] = 20;;
                s_interval[0] = "기간";
                break;
            case ChartUtil.STARC_BANDS:
                interval = new int[2];s_interval=new String[2];
                interval[0] = 15;interval[1] = 6;
                s_interval[0] = "기간";s_interval[1] = "이평기간";
                break;
            //2018.02.01 by HJW >> 유진 보조지표 추가 >>
            case ChartUtil.ADXR:
                interval = new int[2];s_interval=new String[2];
                interval[0] = 14; interval[1] = 14;
                s_interval[0]="ADX기간"; s_interval[1]="ADXR기간";
                break;
            case ChartUtil.BANDWIDTH:
                interval = new int[2];s_interval=new String[2];
                interval[0] = 20; interval[1] = 200;
                s_interval[0]="이평기간"; s_interval[1]="표준편차승수";
                break;
            case ChartUtil.BPDL_SHORT_TREND:
                interval = new int[2];s_interval=new String[2];
                interval[0] = 3;interval[1] = 5;
                s_interval[0] = "기간";s_interval[1] = "Signal";
                break;
            case ChartUtil.BPDL_STOCH:
                interval = new int[1];s_interval=new String[1];
                interval[0] = 14;
                s_interval[0]="기간";
                break;
            case ChartUtil.CMF:
                interval = new int[1];s_interval=new String[1];
                interval[0] = 21;
                s_interval[0]="기간";
                break;
            case ChartUtil.DISPARITYINDEX:
                //2020. 03. 26 by hyh - 이격도 2개에서 4개로 변경 >>
                interval = new int[4];
                s_interval = new String[4];
                interval[0] = 5;
                interval[1] = 10;
                interval[2] = 20;
                interval[3] = 60;
                s_interval[0] = "이격률(지수)1";
                s_interval[1] = "이격률2";
                s_interval[2] = "이격률3";
                s_interval[3] = "이격률4";
                break;
            case ChartUtil.DPO:
                interval = new int[2];s_interval=new String[2];
                interval[0] = 20; interval[1] = 3;
                s_interval[0]="기간"; s_interval[1]="Signal";
                break;
            case ChartUtil.DRF:
                interval = new int[1];s_interval=new String[1];
                interval[0] = 9;
                s_interval[0]="구간수";
                break;
            case ChartUtil.ELDER_RAY_BEAR_POWER:
                interval = new int[1];s_interval=new String[1];
                interval[0] = 13;
                s_interval[0]="기간";
                break;
            case ChartUtil.ELDER_RAY_BULL_POWER:
                interval = new int[1];s_interval=new String[1];
                interval[0] = 13;
                s_interval[0]="기간";
                break;
            case ChartUtil.FORCE_INDEX:
                //2017.08.14 by pjm 지표 수정 >>
                interval = new int[2];s_interval=new String[2];
                interval[0] = 13;interval[1] = 9;
                s_interval[0]="구간수";s_interval[1] = "Signal";
                //2017.08.14 by pjm 지표 수정 <<
                break;
            case ChartUtil.FORCE_INDEX_LONG_TERM:
                interval = new int[1];s_interval=new String[1];
                interval[0] = 13;
                s_interval[0]="기간";
                break;
            case ChartUtil.FORCE_INDEX_SHORT_TERM:
                interval = new int[1];s_interval=new String[1];
                interval[0] = 2;
                s_interval[0]="기간";
                //2018.01 by pjm 기준선 추가 >>
                base = new int[1];
                base[0] = 0;
                //2018.01 by pjm 기준선 추가 <<
                break;
            case ChartUtil.FORMULA:
                interval = new int[1];s_interval=new String[1];
                interval[0] = 5;
                s_interval[0]="기간";
                break;
            case ChartUtil.GM_McCLELAN_OSC:
                interval = new int[1];s_interval=new String[1];
                interval[0] = 13;
                s_interval[0]="기간";
                break;
            case ChartUtil.GM_McCLELAN_SUM:
                interval = new int[1];s_interval=new String[1];
                interval[0] = 13;
                s_interval[0]="기간";
                break;
            case ChartUtil.LFI:
                interval = new int[1];s_interval=new String[1];
                interval[0] = 0;
                s_interval[0]="기간";
                break;
            case ChartUtil.MOVING_BALANCE_INDICATOR:
                interval = new int[1];s_interval=new String[1];
                interval[0] = 10;
                s_interval[0]="기간";
                break;
            case ChartUtil.NDI:
                interval = new int[2];s_interval=new String[2];
                interval[0] = 14; interval[1] = 3;
                s_interval[0]="기간"; s_interval[1]="Signal";
                break;
            case ChartUtil.OBV_MIDPOINT:
                interval = new int[1];s_interval=new String[1];
                interval[0] = 0;
                s_interval[0]="기간";
                break;
            case ChartUtil.OBV_OSC:
                interval = new int[1];s_interval=new String[1];
                interval[0] = 20;
                s_interval[0]="기간";
                break;
            case ChartUtil.OBV_WITH_AVERAGE_VOLUME:
                interval = new int[1];s_interval=new String[1];
                interval[0] = 15;
                s_interval[0]="기간";
                break;
            case ChartUtil.PVI:
                interval = new int[1];s_interval=new String[1];
                interval[0] = 14;
                s_interval[0]="Signal";
                break;
            case ChartUtil.QSTIC:
                interval = new int[2];s_interval=new String[2];
                interval[0] = 4; interval[1] = 9;
                s_interval[0]="기간"; s_interval[1]="Signal";
                break;
            case ChartUtil.TRIN_INVERTED:
                interval = new int[1];s_interval=new String[1];
                interval[0] = 0;
                s_interval[0]="기간";
                break;
            case ChartUtil.VA_OSC:
                interval = new int[1];s_interval = new String[1];
                interval[0]=3;s_interval[0] = "Signal";
                break;
            case ChartUtil.VMP_ACC:
                interval = new int[1];s_interval=new String[1];
                interval[0] = 0;
                s_interval[0]="기간";
                break;

        }
        if(interval!=null){
            org_interval=new int[interval.length];
            System.arraycopy(interval,0,org_interval,0,interval.length);
        }
        //2013. 9. 3 지표마다 기준선 설정 추가>>  : 기준선 기본값
        if(base!=null){
            org_base=new int[base.length];
            System.arraycopy(base,0,org_base,0,base.length);
        }
        //2013. 9. 3 지표마다 기준선 설정 추가>>
    }
    public String getName() {
        return "";
    }
    public void removeDrawTool(String title){
        if(_drawTool!=null){
            for(int i=0; i<_drawTool.size(); i++){
                DrawTool dt = (DrawTool)_drawTool.elementAt(i);
                if(dt.getTitle().equals(title)){
                    _cdm.removePacket(dt.getTitle());
                    dt.setVisible(false);
                    //_drawTool.removeElement(dt);  
                    //dt = null;
                }
            }
        }
    }
    //2012. 7. 10  보조지표 타이틀 숨기기/보이기 
    public void setHideTitleButton(boolean bHide)
    {
        if(_drawTool != null)
        {
            for(int i = 0; i < _drawTool.size(); i++)
            {
                DrawTool dt = _drawTool.get(i);
                dt.setHideTitleButton(bHide);
            }
        }
    }
    public void destroy(){
        if(_drawTool!=null){
            for(int i=0; i<_drawTool.size(); i++){
                DrawTool dt = (DrawTool)_drawTool.elementAt(i);
                //2012. 7. 4 보조지표 사라지면 title라벨 사라지게 수정 
                dt.destroy();
                if(_cvm.chartType != COMUtil.COMPARE_CHART)
                	_cdm.removePacket(dt.getTitle());
            }
            _drawTool.removeAllElements();
            _drawTool = null;
        }
        _drawToolTitle.removeAllElements();
        _drawToolTitle = null;
    }

    //2013. 2. 8 체크안된 상세설정 오픈 : I114
    public void toolLabelRemoveFromSuperview()
    {
        DrawTool dt = null;
        for (int i = 0; i < tool.size(); i++) {
            dt = (DrawTool)tool.get(i);
            if (dt.getTitle() != null) {
                dt.setTitle("");
                dt.title = null;
            }
        }
    }

    //2013. 9. 3 지표마다 기준선 설정 추가>> 
    //기준선값 세팅 
    public void setBaseValue(int[] anBase)
    {
        for(int i = 0; i < base.length; i++)
        {
            base[i] = anBase[i];
        }
    }

    //기준선 배열을 얻어옴 
    public int[] getBaseValue()
    {
        return base;
    }
    public void setBaseLineVisibility(int idx, boolean bShow)
    {
        if(0 == idx)
        {
            bShowBaseLine_1 = bShow;		//기준선1의 보임 여부 처리
        }
        else if(1 == idx)
        {
            bShowBaseLine_2 = bShow;		//기준선2의 보임 여부 처리
        }
    }
    public boolean isBaseLineVisiility(int idx)
    {
        if(0 == idx)
        {
            return bShowBaseLine_1;
        }
        else if(1 == idx)
        {
            return bShowBaseLine_2;
        }

        return false;
    }

    public void drawBaseLine(Canvas gl)
    {
        //2021.07.23 by lyk - kakaopay - 기준선 삭제 >>
//        DrawTool t=(DrawTool)tool.elementAt(0);
//        if(base != null)
//        {
//            for(int i=0;i<base.length;i++){
//                if(isBaseLineVisiility(i)) 	//첫번째 체크박스가 true
//                {
//                    t.draw(gl,base[i]);
//                }
//            }
//        }
        //2021.07.23 by lyk - kakaopay - 기준선 삭제 <<
    }
    //2013. 9. 3 지표마다 기준선 설정 추가>>

    //2014. 9. 11 매매 신호 보기 기능 추가 >>
    /**
     * 매매신호를 보이는지 아닌지
     * */
    public boolean isSellingSignalShow()
    {
        return isSellingSignalShow;
    }
    /**
     * 매매신호를 보일지 여부를 설정한다.
     * @param bIsShow : 매매신호 보이는지 여부 (true/false)
     * */
    public void setSellingSignalShow(boolean bIsShow)
    {
        this.isSellingSignalShow = bIsShow;
    }
    //2014. 9. 11 매매 신호 보기 기능 추가 <<

    //2021.05.24 by hhk - 구름대 채우기 >>
    /**
     * 매매신호를 보이는지 아닌지
     * */
    public boolean isFillCloud()
    {
        return isFillCloud;
    }
    /**
     * 매매신호를 보일지 여부를 설정한다.
     * @param bIsFillCloud : 매매신호 보이는지 여부 (true/false)
     * */
    public void setFillCloud(boolean bIsFillCloud)
    {
        this.isFillCloud = bIsFillCloud;
    }
    //2021.05.24 by hhk - 구름대 채우기 <<

    //2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)>>
    /**
     * 각 지표별 설명이 들어가있는 Html파일 이름을 반환
     * @return 설명 html파일. (ex. AB_Ratio.html)
     * */
    public String getDefinitionHtmlString()
    {
        if(m_strDefinitionHtml== null || m_strDefinitionHtml.length()<1)
        {
            if(graphTitle.equals("거래대금"))
                return "tradingvalue.html";
            else if(graphTitle.equals("매수매도 거래량"))
                return "buy_sell_volume.html";
            else if(graphTitle.equals("외국인 보유비중"))
                return "foreign_holding_ratio.html";
            else if(graphTitle.equals("외국인 보유량"))
                return "foreign_holding_volume.html";
            else if(graphTitle.equals("외국인 순매수"))
                return "foreign_pure_buy.html";
            else if(graphTitle.equals("외국인 순매수 누적"))
                return "foreign_pure_buy_accrue.html";
            else if(graphTitle.equals("기관 순매수"))
                return "organ_pure_buy.html";
            else if(graphTitle.equals("기관 순매수 누적"))
                return "organ_pure_buy_accrue.html";
            else if(graphTitle.equals("개인 순매수"))
                return "person_pure_buy.html";
            else if(graphTitle.equals("개인 순매수 누적"))
                return "person_pure_buy_accrue.html";
            else if(graphTitle.equals("신용잔고율"))
                return "debit_balance.html";
            else if(graphTitle.equals("신용잔고"))
                return "debit_balance.html";
            else if(graphTitle.equals("고객예탁금"))
                return "customer_deposit.html";
            else if(graphTitle.equals("뉴스건수"))
                return "news.html";
            else if(graphTitle.equals("투자자별(거래소)"))
                return "investor_krx.html";
            else if(graphTitle.equals("투자자별(코스닥)"))
                return "investor_kosdaq.html";
            else if(graphTitle.equals("(선물옵션)미결제 약정"))
                return "open_interest.html";
            else if(graphTitle.equals("(선물옵션)미결제 증감"))
                return "open_interest_variation.html";
            else if(graphTitle.equals("(선물)베이시스"))
                return "basis.html";
        }
        return m_strDefinitionHtml;
    }
    //2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)<<

    //2015. 7. 10 차트 상단 지표명 2줄이상 개행시키기>>
    /**
     * 각 지표가 가지고 있는 drawTool의 타이틀 길이를 전부 더해서 반환
     * @return drawTool의 타이틀 길이 합
     * */
    public int getDrawToolsTitleLength()
    {
        int nTitleLen = 0;
        for(int i=0;i<_drawTool.size();i++){
            DrawTool dt = (DrawTool)_drawTool.elementAt(i);
            if(dt.isVisible()){
                if(dt.getTitleBounds()!=null)
                {
                    nTitleLen+=dt.getTitleBounds().width();
                }
            }
        }

        return nTitleLen;
    }

    /**
     * 차트 상단 지표명을 표시할 때 다음번에 무조건 개행을 해야하는지
     *  param 개행해야함(true) 개행안해도됨(false. 보통의 경우).
     * */
    public boolean isNewLineNextStep()
    {
        return m_bIsNewLineNextStep;
    }
    //2015. 7. 10 차트 상단 지표명 2줄이상 개행시키기<<

    public double[] makeAverageADX(double[] data, int interval, int nStart) {
        if (data == null)  return null;
        double[] dAvg = new double[data.length];
        if(data.length <= nStart)return dAvg;

        double subTotal=0;
        //최초값을 단순이동평균으로 구한다
        for(int j=nStart; j>nStart-interval*2; j--) {
            if(j<0)
                break;
            subTotal += data[j];
        }
        dAvg[nStart]= subTotal;
        for(int i=nStart+1 ; i<data.length ; i++) {
            //for(int i=1 ; i<data.length ; i++) {
            dAvg[i]=(dAvg[i-1]*(interval-1)+data[i])/(double)interval;
        }
        return dAvg;
    }

    public double[] makeAverageDaewoo(double[] data, int interval, int nStart) {
        if (data == null)  return null;
        double[] dAvg = new double[data.length];
        if(data.length <= interval)return dAvg;

        double subTotal=0;
        //최초값을 단순이동평균으로 구한다
        for(int j=nStart; j>nStart-interval; j--) {
            subTotal += data[j];
        }
        dAvg[nStart]= (double)(subTotal /interval);
        for(int i=nStart+1 ; i<data.length ; i++) {
            //for(int i=1 ; i<data.length ; i++) {
            dAvg[i]=(dAvg[i-1]*(interval-1)+data[i])/(double)interval;
        }
        return dAvg;
    }
    //2016.07.28 by LYH >> 데이터기준, 이평선타입별(단순,가중,지수,기하) 계산
    public double[] makeWeightAverage(double[] data, int interval){
        int dLen = data.length;
        double[] averageData = new double[dLen];
        if(dLen <= interval)return averageData;
        double subTotal=0;
        int nAdd = 0;
        int nAddSum = 0;
        for(int i = interval ; i < dLen ; i++) {
            subTotal = 0.;
            nAdd = interval;
            nAddSum = 0;
            for(int j= i ; j>i-interval ; j--) {
                subTotal += data[j]*nAdd;
                nAddSum += nAdd;
                nAdd--;
            }
            averageData[i] = (subTotal /nAddSum);
//	         Log.i("drfn", "makeWeightAverage averageData[i] : " + String.valueOf(averageData[i]));
        }
        return averageData;
    }

    public double[] makeGeometicAverage(double[] data, int interval){
        int dLen = data.length;
        double[] averageData = new double[dLen];
        if(dLen <= interval)return averageData;
        double subTotal=0;
        for(int i = interval ; i < dLen ; i++) {
            subTotal = 0.;
            for(int j= i ; j>i-interval ; j--) {
                subTotal += Math.log(data[j]);
            }
            averageData[i] = Math.pow(2.71828182845904, subTotal/interval);
//	         Log.i("drfn", "makGeometicAverage averageData[i] : " + String.valueOf(averageData[i]));
        }
        return averageData;
    }

    public double[] makeAverage_Type(double[] data, int interval, int interval2)
    {
        if(interval<=0)
            return data;
        //단순이평
        if (m_nAverageCalcType == ChartViewModel.AVERAGE_GENERAL){
            return makeAverage(data,interval);
        }
        //가중이평
        else if (m_nAverageCalcType == ChartViewModel.AVERAGE_WEIGHT) {
            return makeWeightAverage(data,interval);
        }
        //지수이평
        else if (m_nAverageCalcType == ChartViewModel.AVERAGE_EXPONENTIAL) {
            if(interval2<0)
                return exponentialAverage(data,interval);
            else
                return exponentialAverage(data,interval, interval2);
        }
        //기하이평
        else if (m_nAverageCalcType == ChartViewModel.AVERAGE_GEOMETIC) {
            return makeGeometicAverage(data,interval);
        }

        return makeAverage(data,interval);
    }
    public double[] getData_Type()
    {
        if (m_nDataType == ChartViewModel.AVERAGE_DATA_OPEN){
            return _cdm.getSubPacketData("시가");
        }
        else if (m_nDataType == ChartViewModel.AVERAGE_DATA_HIGH){
            return _cdm.getSubPacketData("고가");
        }
        else if (m_nDataType == ChartViewModel.AVERAGE_DATA_LOW){
            return _cdm.getSubPacketData("저가");
        }
        else if (m_nDataType == ChartViewModel.AVERAGE_DATA_CLOSE){
            return _cdm.getSubPacketData("종가");
        }
        else if (m_nDataType == ChartViewModel.AVERAGE_DATA_HL2){

            double[]closeData = _cdm.getSubPacketData("종가");
            if(closeData == null)
                return null;

            double[]retData = new double[closeData.length];
            double[] highData = _cdm.getSubPacketData("고가");
            double[] lowData = _cdm.getSubPacketData("저가");
            for(int i=0; i<highData.length; i++)
            {
                retData[i] = (highData[i] + lowData[i])/2;
            }
            return retData;
        }
        else if (m_nDataType == ChartViewModel.AVERAGE_DATA_HLO3){
            double[]closeData = _cdm.getSubPacketData("종가");
            if(closeData == null)
                return null;
            double[]retData = new double[closeData.length];
            double[] highData = _cdm.getSubPacketData("고가");
            double[] lowData = _cdm.getSubPacketData("저가");
            for(int i=0; i<highData.length; i++)
            {
                retData[i] = (highData[i] + lowData[i] + closeData[i])/3;
            }
            return retData;
        }
        else{
            return _cdm.getSubPacketData("종가");
        }
    }
    //2016.07.28 by LYH << 데이터기준, 이평선타입별(단순,가중,지수,기하) 계산
    public void drawStrategyGraph(Canvas g,Vector<DrawTool> drawTools){
        if(!formulated)FormulateData();

        if(drawTools.size()>0){
            DrawTool t=(DrawTool)tool.elementAt(0);
            String strSignal = t.getPacketTitle()+"_Signal";
            double[] baseData = _cdm.getSubPacketData(t.getPacketTitle());
            double[] signalData = _cdm.getSubPacketData(strSignal);
            if(m_nStrategyType == 1)
                t.plotStrategyStrongWeak(g,baseData,signalData);
            else
                t.plotStrategy(g,baseData,signalData);
        }
    }

    public void drawStrategyMultiGraph(Canvas g,Vector<DrawTool> drawTools){
        if(!formulated)FormulateData();

        if(drawTools.size()>0){
            DrawTool t=(DrawTool)tool.elementAt(0);
            String strSignal_Short = t.getPacketTitle()+"_Signal_단기";
            String strSignal_Mid = t.getPacketTitle()+"_Signal_중기";
            String strSignal_Long = t.getPacketTitle()+"_Signal_장기";
            double[] baseData = _cdm.getSubPacketData(t.getPacketTitle());
            double[] signalData_Short = _cdm.getSubPacketData(strSignal_Short);
            double[] signalData_Mid = _cdm.getSubPacketData(strSignal_Mid);
            double[] signalData_Long = _cdm.getSubPacketData(strSignal_Long);

            if(m_nStrategyType == 1)
                //2020.07.28 by JJH >> 멀티 이평크로스 강세약세에서 데이터가 잘못 나오는 오류 수정
                t.plotStrategyStrongWeak(g,baseData,signalData_Short,signalData_Mid,signalData_Long);
            else
                t.plotStrategy(g,baseData,signalData_Short,signalData_Mid,signalData_Long);
        }
    }

    public void drawStrategyBaseGraph(Canvas g, Vector<DrawTool> drawTools, double dUpBase, double dDownBase){
        if(!formulated)FormulateData();

        if(drawTools.size()>0){
            DrawTool t=(DrawTool)tool.elementAt(0);
            double[] baseData = _cdm.getSubPacketData(t.getPacketTitle());
            if(m_nStrategyType == 1){
                t.plotStrategyStrongWeak(g,baseData,dDownBase,true);
                t.plotStrategyStrongWeak(g,baseData,dUpBase,false);
            }
            else{
                t.plotStrategy(g,baseData,dDownBase,true);
                t.plotStrategy(g,baseData,dUpBase,false);
            }
        }
    }


    public void setDownColor(int[] c, int nIndex)
    {
        for (int i=0; i<_drawTool.size(); i++) {
            if(i == nIndex)
            {
                DrawTool dt = (DrawTool)_drawTool.elementAt(i);
                dt.setDownColor(c);
                break;
            }
        }
    }

    public void setUpVisible(boolean visible, int nIndex)
    {
        for (int i=0; i<_drawTool.size(); i++) {
            if(i == nIndex)
            {
                DrawTool dt = (DrawTool)_drawTool.elementAt(i);
                dt.setIsUpVisible(visible);
                break;
            }
        }
    }

    public void setDownVisible(boolean visible, int nIndex)
    {
        for (int i=0; i<_drawTool.size(); i++) {
            if(i == nIndex)
            {
                DrawTool dt = (DrawTool)_drawTool.elementAt(i);
                dt.setIsDownVisible(visible);
                break;
            }
        }
    }
    //2022.06.14 by CYJ - 천미만 절삭 수정 >>
    // 안드로이드에서 floor처리로 발생한 소수점 절삭을 위한 작업 중 천미만 절삭처리하지 않은 값(원래 거래량이 소수일 때)에 대해서도 절삭되는 경우를 방지하기 위한 함수
    private String setCuttingNum(double rData, String strData, String strUnit) {
        String ftData = COMUtil.format(rData, 2, 3);
        try {
            String ftdData = COMUtil.removeString(ftData, ".00");
            strData = strUnit + ftdData;
        } catch (Exception e) {
            strData = strUnit + COMUtil.format(rData, 2, 3);
        }
        return strData;
    }
    //2022.06.14 by CYJ - 천미만 절삭 수정 <<
}