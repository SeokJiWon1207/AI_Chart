package drfn.chart.scale;

import android.graphics.Canvas;
import android.graphics.RectF;

import java.util.Vector;

import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;
import drfn.chart.util.COMUtil;
import drfn.chart.util.ChartUtil;
import drfn.chart.util.CoSys;

public class YScale {
    public RectF bounds;
    RectF Outbounds;
    RectF org_bounds;
    public double[] mm_data = new double[2];//최대 최소 데이터
    public static final int LEFT=0;
    public static final int RIGHT=1;
    public static final int LEFRIG=2;
    public static final int NONE=3;
    //    private int margineT = 0;
//    private int margineB = 0;    
//    private int margineL = 0;    
//    private int margineR = 0;    
    int scale_pos;
    private boolean showCurrPrice=false;
    private final double terms[] = {0.05,0.1,0.5,1,2,5,10,20,30,40,50,100,200,500,1000,2000,5000,10000,20000,50000,100000, 200000, 500000,1000000, 2000000, 5000000, 10000000, 20000000, 50000000, 100000000, 200000000, 500000000, 1000000000., 2000000000., 5000000000., 10000000000., 20000000000., 50000000000., 100000000000., 200000000000., 500000000000., 1000000000000., 2000000000000., 5000000000000., 10000000000000., 20000000000000., 50000000000000., 100000000000000., 200000000000000., 500000000000000., 1000000000000000., 2000000000000000., 5000000000000000., 10000000000000000., 20000000000000000., 50000000000000000.};
    double minValue=0;
    //2014.04.11 by LYH >> 10자리 이상되는 거래량 스케일 이상하게 나오던 문제 해결.
//    int term;
    double term;
    //2014.04.11 by LYH << 10자리 이상되는 거래량 스케일 이상하게 나오던 문제 해결.
    private int volsale=0;//0: 대기매물 아님, 1: 매물.. %, 2: 매물대
    private String ptitle;
    //    private String datakind;
    public int format_index;//y스케일의 스트링 포맷
    private String format="";
    double[] stand;//대기매물에 사용되는 데이터

    private int slt=0;//y스케일의 라인타입0:실선, 1:점선, 2: 없슴
    private int[] csl={200,200,200};//스케일 라인칼라
    private int[] back=CoSys.CHART_BACK_MAINCOLORS;
    private int[] cst=CoSys.CHART_BACK_MAINCOLORS;
    private int format_org;//바뀌기 전의 format
    boolean log = false;//default log chart
    private ChartDataModel _cdm;
    private ChartViewModel _cvm;
    //2012.11.27 by LYH >> 진법 및 승수 처리. <<
    private int nTradeMulti = -1;

    boolean bInverse = false;
    public void setInvertScale(boolean b){
        bInverse = b;
    }
    public boolean isInverse(){
        return bInverse;
    }

    public YScale(ChartDataModel cdm,ChartViewModel cvm){
        _cdm = cdm;
        _cvm = cvm;
        // this.setBackColor(CoSys.CHART_BACK_MAINCOLOR);
    }
    public void setLineColor(int[] col){
        csl = col;
    }
    public void setTextColor(int[] col){
        cst = col;
    }
    public void setBackColor(int[] col){
        back = col;
    }
    public void setShowCurrPrice(boolean b){
        showCurrPrice = b;
    }
    public Boolean getShowCurrPrice() {
        return showCurrPrice;
    }

    public void setMargine(int margineL, int margineR, int margineT, int margineB) {
//        this.margineL = margineL;
//        this.margineR = margineR;
//        this.margineT = margineT;
//        this.margineB = margineB;
    }
    public void setProperties2(int format,String packetTitle){
        if(format<0) return;
        ptitle = packetTitle;
        //2012.11.27 by LYH >> 진법 및 승수 처리.
        if(format >1000)
        {
            nTradeMulti = format%1000;
            format = format/1000;
        }
        //2012.11.27 by LYH <<
        format_index = format;
        format_org = format;
        try {
            this.format = ChartUtil.packet_field_format[format];
        } catch(Exception e) {
            this.format = "";
        }
    }
    public void setVolumeSaleData(double[] data){
        stand= data;
    }
    public void setVolumeScale(int b){
        volsale = b;
    }
    public void setScaleLineType(int type){
        slt = type;
    }
    public void setProperties(int pos, String datakind,int scaleline){
        scale_pos = pos;
//        this.datakind = datakind;
        slt = scaleline;
    }
    public int getScale_pos() {
        return scale_pos;
    }

    // 2013. 2. 12  상하한가바 표시
    public void drawPriceLimit(Canvas gl) {
        if (showCurrPrice && (_cvm.chartType != COMUtil.COMPARE_CHART) && COMUtil.isMinMaxShow()
        	/*	&& (_cdm.codeItem.strMarket.equals("0") || _cdm.codeItem.strMarket.equals("3")) */ ) {	//2015. 12. 16 일반설정 상하한가 바 표시 :시장설정 나중에 
            String strHighest = _cdm.codeItem.strHighest;
            String strLowest = _cdm.codeItem.strLowest;
            String strGijun = _cdm.codeItem.strGijun;

            double boxMax = 0.0;
            if(strHighest!=null && !strHighest.equals(""))
                boxMax = Double.parseDouble(strHighest);  //  상한가

            //2014. 2. 3 보조메세지에 상하한가 기준선이 없을 경우 처리 >>
//		    double boxJG = Double.parseDouble(strGijun);   //  기준가
            double boxJG = 0.0;		//  기준가
            if(strGijun!=null && !strGijun.equals(""))
            {
                boxJG = Double.parseDouble(strGijun);
            }
            //2014. 2. 3 보조메세지에 상하한가 기준선이 없을 경우 처리 <<

            double boxMin = 0.0;
            if(strLowest!=null && !strLowest.equals(""))
                boxMin = Double.parseDouble(strLowest); //  하한가

            float startY = bounds.top - COMUtil.getPixel(22);   //  시작점

            //  변환된 좌표
            if(format_index >= 14 && format_index <= 16)
            {
                if(!log)
                {
                    boxMax=boxMax*10000;
                    boxMin=boxMin*10000;
                    boxJG=boxJG*10000;
                }
            }
            float tBoxMax = priceToY(boxMax);
            float tBoxMin = priceToY(boxMin);
            float tBoxJG = priceToY(boxJG);
            float fBottom = bounds.bottom+COMUtil.getPixel(8);

            if(tBoxMin < startY || tBoxMax > fBottom)
                return;

            if(tBoxMax < startY)
                tBoxMax = startY;
            if(tBoxJG < startY)
                tBoxJG = startY;
            if(tBoxMin > fBottom)
                tBoxMin = fBottom;
            if(tBoxJG > fBottom)
                tBoxJG = fBottom;

            //  그리는데 사용될 좌표
            float maxY, minY;
            float maxH, minH;
            maxY = tBoxMax;
            maxH = tBoxJG-tBoxMax;
            minY = tBoxJG;
            minH = tBoxMin-tBoxJG;

//		    int[] anColorRed = {255, 0, 0};
//		    int[] anColorBlue = {0, 0, 255};
            int[] anColorRed = CoSys.CHART_COLORS[0];
            int[] anColorBlue = CoSys.CHART_COLORS[1];
            _cvm.drawFillRect(gl, bounds.left-COMUtil.getPixel_W(1), maxY, COMUtil.getPixel_W(1), maxH, anColorRed, 1.0f);

            _cvm.drawFillRect(gl, bounds.left-COMUtil.getPixel_W(1), minY, COMUtil.getPixel_W(1), minH, anColorBlue, 1.0f);
        }
    }

    //대기매물과 같이 %부호가 붙는 경우
    public void drawText(Canvas gl, double[] stand){

        float yPos=getBounds().left;
        float gab = getBounds().height()/stand.length;
        for(int i=stand.length-1;i>-1;i--){
            double vol2 = Math.round(((double)(stand[i])* 100.0)) / 100.0;
            drawScale(gl,yPos+20,""+vol2+"%",true);
            yPos+=gab;
        }
    }
    //특정 구간으로 나누어서 데이터를 표시
    public void draw(Canvas gl, int gab){
        float term = (float)(mm_data[1]-mm_data[0])/gab;
        float yPos;
        for(int i=1;i<gab;i++){
            double price =mm_data[0]+(term*i);
            yPos = priceToY(price);
            drawScaleLine(gl,4,(int)yPos, true);
            drawScale(gl,(int)yPos+5,""+price,true);
        }
    }
    int lineInc =1;
    public void draw(Canvas gl){
        y1=-1;

        if(bounds==null)return;
        if(scale_pos == 1 && bounds.width()<1) //오른쪽 정렬
            return;
        //if(org_bounds==null)return;
        if(mm_data==null)return;

        //2013. 2. 12 상하한가바 추가 
        //drawPriceLimit(gl);

        //대기매물 10으로 나눈 가격대를 표시	
        if(volsale==2) draw(gl,10);
            //대기매물 %로 스케일 표시
        else if(volsale==1&&stand!=null)drawText(gl,stand);
            //일반 가격대 표시
        else{
            float yPos;
            //sub line 간격
            //double minTerm = (priceToYD(minValue)-priceToYD(minValue+term))/5.0;
            float minTerm = (priceToY(minValue)-priceToY(minValue+term))/5.0f;

           // if(minTerm<=0) return;
//            double rawCnt = (mm_data[1]-minValue)/term;
//            if((int)rawCnt>0) {
//	            int unitH = Outbounds.height()/(int)rawCnt;
//	            if(unitH==0) {
//	            	term = (mm_data[1]-minValue)/5;
//	            } else if(unitH<5) {
//	            	term = term*4;
//	            } else if(unitH<10) {
//	            	term = term*2;
//	            }
//            }

            for(double i=minValue; i>=minValue && i<=mm_data[1]; i+=term){
                if(log) minTerm = (priceToY(i)-priceToY(i+term))/5.0f;
                yPos = priceToY(i);
                if(i==minValue){//최소값 밑 부분에 sub line 긋는다.
                    for(int j=1; j<5; j++){
                        if(yPos+(minTerm*j)>bounds.top+bounds.height())break;
                        else drawScaleLine(gl,0,(int)(yPos+(minTerm*j)), false);
                    }
                }
                for(int j=1; j<5; j++){//윗부분에 sub line 긋는다.
                    if(yPos-(minTerm*j)<bounds.top) break;
                    else drawScaleLine(gl,0,(int)(yPos-(minTerm*j)), false);
                }

                //2021.05.27 by hanjun.Kim - kakaopay - 가격표아래에 긋도록 수정 >>
                if(((i-minValue)/term)%lineInc==0 || slt==3)
                    drawScaleLine(gl,2, (int) (yPos), false);
                else  drawScaleLine(gl,2, (int) (yPos), false);
                if(!log)
                {
                    if(format_index == 14 || format_index == 15 || format_index == 16)
                        //drawScale(gl,(int)yPos,COMUtil.format((""+((double)i/10000)),2,3),true);
                        drawScale(gl,(int)yPos,""+((double)i/10000),true);
                    else
                    {
                        //2014.04.11 by LYH >> 10자리 이상되는 거래량 스케일 이상하게 나오던 문제 해결.
                        String strVal = String.format("%.0f", i);
                        drawScale(gl,(int)yPos,strVal,true);
                        //2014.04.11 by LYH << 10자리 이상되는 거래량 스케일 이상하게 나오던 문제 해결.
                    }
                }else {
                    //2020. 02. 18 by hyh - 로그차트 Y축 보이지 않는 에러 수정 >>
                    if (format_index == 14 || format_index == 15 || format_index == 16) {
                        drawScale(gl, (int) yPos, "" + ((double) i / 10000), true);
                    }
                    else {
                        String strVal = String.format("%.0f", i);
                        drawScale(gl, (int) yPos, strVal, true);
                    }

                    //drawScale(gl, (int) yPos, "" + (int) i, true);
                    //2020. 02. 18 by hyh - 로그차트 Y축 보이지 않는 에러 수정 <<
                }
            }
            switch(format_index){
                case 11:
                    //if(!format.equals("× 1"))drawScale(gl,bounds.top+bounds.height(),format,false);
                    if(!format.equals("× 1"))drawScale(gl,bounds.top,format,false);
                        //2013.10.02 by LYH >> 업종 거래량 단위 표시(TDC). <<
                    else if(_cdm.m_nVolumeUnit == 1000)drawScale(gl,bounds.top,format,false);
                    break;
                case 12:
                case 17:
                case 18:
                    //drawScale(gl,bounds.top+bounds.height(),format,false);
                    drawScale(gl,bounds.top,format,false);
                    break;
            }

            //2012. 10. 19 거래량 yscale 빨간색으로 표시되는 현상 수정 
            //거래량 표시.
//            if(this.ptitle!=null && this.ptitle.equals("기본거래량")) {
//            	double data = Double.NaN;
//            	try {
//            		data = _cdm.getLastData(this.ptitle);
//            	} catch(Exception e) {
//            		System.out.println(e.getMessage());
//            		return;
//            	}
//        		if(Double.isNaN(data)) return;            	
//            	int sPy = 0;
//            	int px = -2;
//            	int py = -6;
//            	int x = this.bounds.left+5;
//            	
//            	if(data>=mm_data[0] && data <= mm_data[1]) {
//            		sPy = (int)this.priceToY(data);
//            	} else if(data<mm_data[0]) {
//            		sPy = (int)this.priceToY(mm_data[0]);
//            	} else {
//            		sPy = (int)this.priceToY(mm_data[1]);
//            	}
//            	
//                px = this.bounds.left+6;
//            	int pw = (int)COMUtil.getPixel(51);
//            	int ph = (int)COMUtil.getPixel(28);
//            	
//            	_cvm.drawFillTri(gl, px-COMUtil.getPixel(4),sPy,COMUtil.getPixel(4),(int)COMUtil.getPixel(14), CoSys.CHART_COLORS[2]);
//            	_cvm.drawFillRect(gl, px,sPy-(int)COMUtil.getPixel(7),pw,(int)COMUtil.getPixel(14), CoSys.CHART_COLORS[2], 1.0f);
//            	//_cvm.drawFillRect(gl, x+4+px, sPy+py-2, 54, 14, CoSys.CHART_COLORS[2], 1);
//            	//_cvm.drawRect(gl, x+4+px, sPy+py-2, 54, 14, CoSys.GREEN);
//            	
//            	if(this.format.equals("× 1000")) {
//            		data = data/1000;
//            	}
//            	//String title = ChartUtil.getFormatedData(""+data, this.format);
//            	String title = ChartUtil.getFormatedData(data, this.format);
//            	_cvm.drawString(gl, CoSys.WHITE, px, sPy, title);
//            }

            if(scale_pos==1 && showCurrPrice){
                if(!_cvm.bInvestorChart && !_cvm.bIsLineFillChart && !_cvm.bIsLineChart && !_cvm.bStandardLine)
                {
                    String strCount = ""+_cvm.getViewNum()+"봉";
                    int w = _cvm.GetTextLength(strCount);
                    float py = _cvm.Margin_T+COMUtil.getPixel(8);
                    int[] textCol = CoSys.WHITE_TEXT;
                    if(_cvm.getSkinType()!=COMUtil.SKIN_BLACK) {
                        textCol = CoSys.BLACK_TEXT;
                    }

                    //2014. 3. 20 Y축 봉갯수 누르면 봉입력창 뜨고 봉갯수변경
//                	_cvm.drawString(gl, textCol, bounds.right-w+(int)COMUtil.getPixel(3),(int)py, strCount);
                }
//                if(COMUtil.isyJonggaShow())
//                    drawBuyAveragePrice(gl); //2021.02.18 by HJW - 매입평균선 추가
                    drawCurrentData(gl);
            }

//            if(_cvm.chartType == COMUtil.COMPARE_CHART) {
//                drawCompareCurrentData(gl);
//            }
        }
    }
    //왼쪽에 위치한 것인지 오른쪽에 위치한 것인지 구분하여... 그린다
    private void drawScale(Canvas gl, float y, String s,boolean b){
        float w=0;
        if(s.equals("× 1000")) {
            s = "x 1000"; //x가 font에서 지원하지 않아 변경하여 사용함.
        }

        int[] backCol = CoSys.BLACK;
        int[] textCol = CoSys.WHITE_TEXT;
        if(_cvm.getSkinType()!=COMUtil.SKIN_BLACK) {
            backCol = CoSys.WHITE;
            textCol = CoSys.BLACK_TEXT;
        }
        switch(scale_pos){
            case 0://왼쪽
//                if(b){
//                    String fs= makeFormatedString(s);    
//                    w = _cvm.tf.GetTextLength(fs) +5;
//                   
//                    _cvm.drawString(gl, cst, bounds.left-(int)w,y, fs);
//                }else {//if(s.equals("× 1000")){
//                    w = _cvm.tf.GetTextLength(s)+5;
//
//                    _cvm.drawFillRect(gl, bounds.left-w,y-20+2,w,20+3, back, 1.0f);
//                    _cvm.drawRect(gl, bounds.left-w,y-20+2,w,20-1, cst);
//                }
                if(b) {
                    String fs= makeFormatedString(s);
                    if(_cvm.chartType==COMUtil.COMPARE_CHART && _cvm.nCompareType != 2) {
                        fs = fs+"%";
                    }
//                    int index = fs.indexOf(".00");;
//                    if(index!=-1) fs = COMUtil.removeToDot(fs);

                    w = _cvm.GetTextLength(fs);

                    if (_cvm.m_nChartType == ChartViewModel.CHART_THREE_ROUNDED_BAR) {
                        float stringWidth = _cvm.getFontWidth(fs, (int)COMUtil.getPixel(11));
                        _cvm.drawString(gl, cst, bounds.right - (int) stringWidth - (int) COMUtil.getPixel(4), y, fs);
                    }
                    else
                        _cvm.drawString(gl, cst, bounds.right-(int)w-(int)COMUtil.getPixel(4),y, fs);
                }else {

                    //w = _cvm.GetTextLength(s)+5;
                    //_cvm.drawFillRect(gl, bounds.left+5,y-20+2,w+2,20-1, backCol, 0.8f);
                    //_cvm.drawRect(gl, bounds.left+5,y-21,w+2,20-1, textCol);
//                    _cvm.drawString(gl, cst, bounds.left+5,y-15, s);
                }
                break;
            case 2:
            case 1://오른쪽
                if(b){
                    String fs= makeFormatedString(s);
                    if(_cvm.chartType==COMUtil.COMPARE_CHART && _cvm.nCompareType != 2) {
                        fs = fs+"%";
                    }

                    //2022.05.04 by lyk - 지수차트 처리 >>
                    if(format_index == 11) {
                        if(ptitle.equals("기본거래량")||ptitle.equals("누적거래량")) {
                            if (COMUtil._mainFrame.mainBase.baseP.nMarketType == 1) {
                                fs += "천";
                            }
                        }
                    }
                    //2022.05.04 by lyk - 지수차트 처리 <<

                    if(format.equals("× 1000")) {
                        fs += "천";
                    } else if(format.equals("× 1000000")) {
                        fs += "백만";
                    } else if(format.equals("× 0.0001")) {
                        //2016. 2. 18 현재가 M표시, 반올림 처리>>
                        if(Math.abs(mm_data[1]/10000)>= 1000000000 || Math.abs(mm_data[0]/10000)>= 1000000000)
                        {
                            double dData = Double.parseDouble(s);

                            if(dData >= 10000 && ((int)(dData/100000))%10 > 5)
                            {
                                dData += 500000;
                            }

                            String strCur = ""+(int)(dData/1000000);
                            fs = ChartUtil.getFormatedData(strCur, 11)+"M";
                        }
                        else if(Math.abs(mm_data[1]/10000)>= 10000000 || Math.abs(mm_data[0]/10000)>= 10000000)
                        {
                            double dData = Double.parseDouble(s);

                            if(dData >= 10000 && ((int)(dData/100))%10 > 5)
                            {
                                dData += 500;
                            }

                            String strCur = ""+(int)(dData/1000);
                            fs = ChartUtil.getFormatedData(strCur, 11)+"K";
                        }
                        //2016. 2. 18 현재가 M표시, 반올림 처리>>
                    }
//                    int index = fs.indexOf(".00");;
//                    if(index!=-1) fs = COMUtil.removeToDot(fs);
                    w = _cvm.GetTextLength(fs);
                    //2013.03.27 by LYH >> 스케일 가격 오른쪽 정렬
                    //_cvm.drawString(gl, cst, bounds.left+5,y, fs);
                    if(bounds.width() > 0) {
                        if(_cvm.bIsNewsChart)
                            _cvm.drawString(gl, cst, bounds.left + (int) COMUtil.getPixel(5), y, fs);
                        else{
//                            _cvm.drawString(gl, cst, bounds.left+ (int) COMUtil.getPixel(12), y, fs);
                            //2021.07.12 by hanjun.Kim - kakaopay - 가격표 좌측정렬 >>
//                            _cvm.drawScaleString(gl, cst, bounds.left+ (int) COMUtil.getPixel(3), y, fs);
//                            if(COMUtil.currentTheme == COMUtil.SKIN_BLACK) {
//                                cst = CoSys.GREY990;
//                            } else {
//                                cst = CoSys.GREY0_WHITE;
//                            }
                            _cvm.drawScaleString(gl, cst, bounds.left + (int) COMUtil.getPixel(3), y - (int) COMUtil.getPixel(8), fs, 0.48f); //2021.07.23 by lyk - kakaopay - 가격 텍스트 위치 가로 구분선 위로 수정
//                            _cvm.drawString(gl, cst, bounds.left + (int) COMUtil.getPixel(3), y - (int) COMUtil.getPixel(8), fs, 0.48f); //2021.07.23 by lyk - kakaopay - 가격 텍스트 위치 가로 구분선 위로 수정
                        }

                    }
                    //2013.03.27 by LYH <<
                }else{// if(s.equals("× 1000")){
                    //2013.10.02 by LYH >> 업종 거래량 단위 표시(TDC).
//                    if(_cdm.m_nVolumeUnit == 1000)
//                    {
//                        if(s.equals("x 1000"))
//                            s = "x 1000000";
//                        else
//                            s = "x 1000";
//                    }
//                    //2013.10.02 by LYH <<
//                    w = _cvm.GetTextLength(s)+5;
////                    _cvm.drawFillRect(gl, bounds.left+5,y-20+2,w+2,20-1, backCol, 0.8f);
////                	_cvm.drawRect(gl, bounds.left+5,y-21,w+2,20-1, textCol);
//                    _cvm.drawString(gl, cst, bounds.left+5,y-15, s);
                }
                break;
        }
    }
    int y1=-1;
    private void drawScaleLine(Canvas gl, int w, int y, boolean lineDraw){
        if (!COMUtil.isShowYScaleLine())
            return;
        if(scale_pos==0){//왼쪽
            //_cvm.drawLine(gl, bounds.left-w,y,bounds.left,y, cst, 1.0f);
//            if(w != 0)
//            {
//                _cvm.setLineWidth_Fix(COMUtil.getPixel(1));
//                if(_cvm.getSkinType() == COMUtil.SKIN_WHITE)
//                {
//                    int[] nRightLineCol = {164, 170, 179};
//                    _cvm.drawLine(gl, bounds.right-(int)COMUtil.getPixel(3),y,bounds.right,y, nRightLineCol, 1.0f);
//                }
//                else
//                {
//                    int[] nRightLineCol = {79, 79, 79};
//                    _cvm.drawLine(gl, bounds.right-(int)COMUtil.getPixel(3),y,bounds.right,y, nRightLineCol, 1.0f);
//                }
//                _cvm.setLineWidth(1);
//            }
            if(w != 0) {
                //int[] nLeftLineCol = {240, 240, 240};
                int[] nRightLineCol = CoSys.vertLineColor;
                //_cvm.drawDashLine(gl, getOuterBounds().left, y, getOuterBounds().left + getOuterBounds().right, y, nLeftLineCol, 1.0f);
                //2021.05.21 by hanjun.Kim - kakaopay - 디자인 수정(점선)
//                _cvm.drawLine(gl, getOuterBounds().left, y, getOuterBounds().left + getOuterBounds().right, y, nRightLineCol, 1.0f); // 이전 설정(21.05.21)
                _cvm.drawDashDotDotLine(gl, getOuterBounds().left, y, getOuterBounds().left + getOuterBounds().right, y, nRightLineCol, 1.0f);
            }
        }else{//오른쪽
//        	if(w != 0)
//        		_cvm.drawLine(gl, bounds.left-1,y,bounds.left+w,y, cst, 1.0f);
            if(w != 0)
            {
                //_cvm.setLineWidth_Fix(COMUtil.getPixel(1));
                _cvm.setLineWidth_Fix(COMUtil.getPixel_H(1));
                int[] nRightLineCol = CoSys.vertLineColor;
//                if(_cvm.getSkinType() == COMUtil.SKIN_WHITE)
//                {
//                    //int[] nRightLineCol = {240, 240, 240};
//
////                    if(_cvm.bIsTodayLineChart)
////                        _cvm.drawLine(gl, getOuterBounds().left,y,bounds.left+(int)COMUtil.getPixel(3),y, nRightLineCol, 1.0f);
//
////                    else
////                        _cvm.drawLine(gl, bounds.left,y,bounds.left+(int)COMUtil.getPixel(3),y, nRightLineCol, 1.0f);
////                    if(COMUtil._mainFrame.strFileName != null && COMUtil._mainFrame.strFileName.length()>0) {
////                        _cvm.drawDashLine_interval(gl, getOuterBounds().left,y,bounds.left,y, nRightLineCol, 1.0f, COMUtil.getPixel_W(1), COMUtil.getPixel_W(2));
////                    } else {
////                        _cvm.drawDashLine(gl, getOuterBounds().left,y,bounds.left,y, nRightLineCol, 1.0f);
////                    }
//
//                    //2021.05.21 by hanjun.Kim - kakaopay - 디자인 수정(점선)
//                    //2021.07.14 by hanjun.Kim - kakaopay - 기준선 가격표 밑으로 >>
////                    _cvm.drawLine(gl, getOuterBounds().left,y,bounds.left,y, nRightLineCol, 1.0f); // 이전 설정(21.05.21)
//                    _cvm.drawDashDotDotLine(gl, getOuterBounds().left,y,_cvm.getBounds().right,y, nRightLineCol, 1.0f);
//                }
//                else
//                {
//                    //int[] nRightLineCol = {79, 79, 79};
//                    nRightLineCol = CoSys.vertLineColor;
//                    if(COMUtil._mainFrame.strFileName != null && COMUtil._mainFrame.strFileName.length()>0) {
//                        _cvm.drawDashLine_interval(gl, getOuterBounds().left,y,_cvm.getBounds().right,y, nRightLineCol, 1.0f, COMUtil.getPixel_W(1), COMUtil.getPixel_W(2));
//                    } else {
//                        _cvm.drawDashLine(gl, getOuterBounds().left,y,_cvm.getBounds().right,y, nRightLineCol, 1.0f);
//                    }
//                }
                _cvm.drawDashDotDotLine(gl, getOuterBounds().left,y,_cvm.getBounds().right,y, nRightLineCol, 1.0f);
                _cvm.setLineWidth(1);
            }
        }
        //if(w==3&&slt<2){//최종 y스케일의위치가 양쪽인 아닌경우에만
        if(lineDraw){
            switch(slt){
                case 0://실선
                    _cvm.drawLine(gl, Outbounds.left,y,Outbounds.left+Outbounds.width(),y, cst, 1.0f);
                    _cvm.drawLine(gl,Outbounds.left,y+1,Outbounds.left+Outbounds.width(),y+1, cst, 1.0f);
                    break;
                case 1://점선
                    //pnt.setColor(csl);
                    //g.drawPoint(Outbounds.left+Outbounds.right,y, pnt);
                    // this.drawDotLine(g,Outbounds.x,y,Outbounds.x+Outbounds.width);
                    break;
                case 2://없슴
                    break;
                case 3://두색 번갈아서 구분
                    if(y1>0){
                        _cvm.drawFillRect(gl,Outbounds.left,y,Outbounds.left+Outbounds.width()-Outbounds.left-1,y1-y, CoSys.GRAY, 1.0f);
                        y1=0;
                    }else if(y1<0){
                        _cvm.drawFillRect(gl,Outbounds.left,(float)y,Outbounds.left+Outbounds.width()-Outbounds.left-1,Outbounds.top+Outbounds.height()-y, CoSys.GRAY, 1.0f);
                        y1=0;
                    }else{
                        y1=y;
                    }
                    break;
            }
        }
    }
    private String makeFormatedString(String s){
        String tmp=s;

        switch(format_index){
            case 11://× 1
                if(format.equals("× 1000")){
                    if(s.length()>3)
                        tmp=COMUtil.format(new String(s.substring(0,s.length()-3)),0,3);
                    else
                        tmp = "0";
                }
                else if(format.equals("× 1000000")){
                    if(s.length()>6)
                        tmp=COMUtil.format(new String(s.substring(0,s.length()-6)),0,3);
                    else
                        tmp = "0";
                }
                else{
                    tmp=COMUtil.format(s,0,3);
                }
                break;
            case 12://× 1000
                if(format.equals("× 1000")){
                    if(s.length()<3) return s;
                    tmp=COMUtil.format(new String(s.substring(0,s.length()-3)),0,3);
                }else{
                    tmp=COMUtil.format(s,0,3);
                }
                //format = "×1000";
                break;
            case 18:
                tmp=COMUtil.format(s,0,3);
                break;
            case 13://× 0.1
                tmp=COMUtil.format(s,1,3);
                break;
            case 14://×0.01
                tmp=COMUtil.format(s,2,3);
                break;
            case 15://× 0.001
                tmp=COMUtil.format(s,3,3);
                break;
            case 16://× 0.0001
                //2012.11.27 by LYH >> 진법 및 승수 처리.
                if(nTradeMulti>=8)
                {
                    if(s==null ||s.length()<=0) return "";
                    double dData = 0;
                    try {
                        dData = Double.parseDouble(s);
                    } catch(Exception e) {
                        dData = 0;
                    }
                    //return ChartUtil.FormatCurrency_Notation(dData, _cdm.nDispScale, _cdm.nTradeMulti, _cdm.nLogDisp);
                    return ChartUtil.FormatCurrency_Notation(dData, _cdm.nDispScale);
                }
                else if(nTradeMulti>=0)
                    tmp=COMUtil.format(s,nTradeMulti,3);
                else
                    //2012.11.27 by LYH <<
                    tmp=COMUtil.format(s,4,3);
                break;
            case 17://%
                tmp=s+"%";
                break;

        }
        return tmp;

    }
    public void setOrg_bounds(RectF org_bounds) {
        this.org_bounds = org_bounds;
    }
    public void setBounds(float sx, float sy, float w, float h){
        bounds = new RectF(sx,sy,w,h);
    }
    public void setOuterBounds(float sx,float sy, float w, float h){
        Outbounds = new RectF(sx,sy,w,h);
    }
    public RectF getBounds(){
        return bounds;
    }
    public RectF getOuterBounds(){
        return Outbounds;
    }
    public void setPos(int pos){
        this.scale_pos = pos;
    }
    public int getPos(){
        return scale_pos;
    }
    public void setMinMax(double[] mmdata){

        this.mm_data[0] = mmdata[0];
        this.mm_data[1] = mmdata[1];
        if(mm_data[0]==mm_data[1]){
            mm_data[0]-=1;
            mm_data[1]+=1;
        }
        format_index = this.format_org;

        if(format_index<0)
            return;

        try {
            this.format = ChartUtil.packet_field_format[format_index];
        } catch(Exception e) {
            System.out.println("YScale exception : " + e.getMessage());
        }
        if(_cvm.chartType != COMUtil.COMPARE_CHART)
        {
            switch(format_index){
                case 11:
                    if(ptitle.equals("기본거래량")||ptitle.equals("누적거래량")){
                        //if(mm_data[1]>100000&&mm_data[0]>1000){
                        //2022.05.04 by lyk - 지수차트 처리 >>
                        if (COMUtil._mainFrame.mainBase.baseP.nMarketType == 1) {

                        } else {
                            if (mm_data[1] > 10000000 && mm_data[0] >= 0) {
                                format = "× 1000";
                                //2013.09.23 by LYH >> 한 번 x1000으로 세팅 된 이후에 다시 x1 로 안 돌아 오는 오류 개선.
                                //_cdm.setPacketFormat(ptitle, format);
                                //2013.09.23 by LYH <<
                            }
                        }
                        //2022.05.04 by lyk - 지수차트 처리 <<

//                        if(mm_data[1]>1000000000 && mm_data[0]>=0){
//                            format = "× 1000000";
//                        }
                    }
                    else
                    {
                        if(Math.abs(mm_data[1])>=10000000 || Math.abs(mm_data[0]) >= 10000000){
                            format = "× 1000";
                        }
                        if(Math.abs(mm_data[1])>=1000000000 || Math.abs(mm_data[0]) >= 1000000000){
                            format = "× 1000000";
                        }
                    }
                    break;
                case 9999:
                    if(ptitle.equals("기본거래량")||ptitle.equals("누적거래량")||ptitle.equals("거래대금") ) {
                    //if(m_MaxData > 100000 && m_MinData > 1000) {
                    if(mm_data[1] > 10000000 && mm_data[0] >= 0) {
                        format= "× 1000";
                    }
                }
                //2013. 7. 25 거래량 yscale 에서 천만 이상일 때  x1000 표시하면서 값 자릿수가 조정안되던 현상 수정>>
                break;
                case 14:
                case 15:
                case 16:
                    //2020. 02. 18 by hyh - 로그차트 Y축 보이지 않는 에러 수정 >>
                    mm_data[0] = mm_data[0]*10000;
                    mm_data[1] = mm_data[1]*10000;

                    //if(!log)
                    //{
                    //    mm_data[0] = mm_data[0]*10000;
                    //    mm_data[1] = mm_data[1]*10000;
                    //}
                    //2020. 02. 18 by hyh - 로그차트 Y축 보이지 않는 에러 수정 <<
                    break;
            }
        }
        //2019.02.14 by lyj 비교차트 0이하 종목 스케일 나오지 않는 오류
        else
        {
            switch (format_index){
                case 16:
                    //2020. 02. 18 by hyh - 로그차트 Y축 보이지 않는 에러 수정 >>
                    mm_data[0] = mm_data[0]*10000;
                    mm_data[1] = mm_data[1]*10000;

                    //if(!log)
                    //{
                    //    mm_data[0] = mm_data[0]*10000;
                    //    mm_data[1] = mm_data[1]*10000;
                    //}
                    //2020. 02. 18 by hyh - 로그차트 Y축 보이지 않는 에러 수정 <<
                    break;
            }
        }
        //2019.02.14 by lyj 비교차트 0이하 종목 스케일 나오지 않는 오류 end

        calcScale();
    }
    private void calcScale(){//스케일 계산하여 minValue와 term을 구한다.
        double gab = (double)(mm_data[1]-mm_data[0]);
        double interval = ((double)gab/7);
        int i=0;

        //2019.01.03 sdm >> 진법 틱 그래프의 Y Scale 이 중복되서 나오거나 안맞는 부분 수정 Start
        float intervalPixel = 0;
//        if(nTradeMulti == 32 || nTradeMulti == 64 || nTradeMulti == 128 || nTradeMulti == 256
//        || nTradeMulti == 132 || nTradeMulti == 232 || nTradeMulti == 332) {
//            double sosu = 0.003125 * 10000;  //32진법 기준으로 간격을 설정
//            int bunsu = nTradeMulti/32;
//            if(nTradeMulti == 132)  //64
//                bunsu = 2;
//            else if(nTradeMulti == 232) //128
//                bunsu = 4;
//            else if(nTradeMulti == 332) //256
//                bunsu = 8;
//
//            for (; i < terms.length; i++) {
//                double termCal = (sosu * i);
//                term = interval - termCal;
//                if (term < 0) {
//                    term = Math.floor(termCal * 10) / 10.0;
//                    break;
//                }
//            }
//
//            int gepTerm=1;
//            if((int)(gab/term)==0) gepTerm = 1;
//            else gepTerm = (int)(gab/term);
//            intervalPixel = bounds.height()/gepTerm;
//            if(intervalPixel<COMUtil.getPixel_H(10)){
//                term = (sosu * (i+2));
//            }
//            else if(intervalPixel<COMUtil.getPixel_H(30)) {
//                term = (sosu * (i+1));
//            }
//
//            if(intervalPixel>COMUtil.getPixel_H(70) && i>1) {
//                term = (sosu * (i-1));
//            }
//
//            term = term/bunsu;  //32진법 기준 간격에 분수부를 나눠서 64, 128, 256 진법에 맞게 조정
//
//            if (gab / term >= 12) lineInc = 3;
//            else if (gab / term >= 6) lineInc = 2;
//            else if (gab / term >= 5 && intervalPixel < COMUtil.getPixel_H(50)) lineInc = 2;
//            else lineInc = 1;
//
//            //2014.04.11 by LYH >> 10자리 이상되는 거래량 스케일 이상하게 나오던 문제 해결.
//            if(gab/term > 10)
//            {
//                while((gab/term)>10)
//                {
//                    term *= 2;
//                }
//                if(gab/term > 10)
//                {
//                    term *= 2;
//                }
//            }
//            //2014.04.11 by LYH << 10자리 이상되는 거래량 스케일 이상하게 나오던 문제 해결.
//
//            int iTemp = (int) ((mm_data[0] + term) / term);
//
//            if (mm_data[0] < 0) {
//                iTemp = (int) mm_data[0] / (int) term;
//            }
//            minValue = (double) iTemp * (double) term;
//
//            if (minValue < mm_data[0])
//                minValue = mm_data[0];
//        }
//        else {
            for(; i<terms.length; i++){
                term = interval - terms[i];
                if(term<0){
                    term=terms[i];

                    break;
                }
            }
            int gepTerm=1;
            if((int)(gab/term)==0) gepTerm = 1;
            else gepTerm = (int)(gab/term);
            intervalPixel = bounds.height()/gepTerm;
            if(intervalPixel<COMUtil.getPixel_H(10)){
                if(i+2<terms.length)term=terms[i+2];
            }
            else if(intervalPixel<COMUtil.getPixel_H(30))
                if(i<terms.length-1)term=terms[++i];

            if(intervalPixel>COMUtil.getPixel_H(70) && i!=0)
                term=terms[--i];
            if(gab/term>=12) lineInc=3;
            else if(gab/term>=6) lineInc=2;
            else if(gab/term>=5 && intervalPixel<COMUtil.getPixel_H(50)) lineInc=2;
            else lineInc=1;

            //2014.04.11 by LYH >> 10자리 이상되는 거래량 스케일 이상하게 나오던 문제 해결.
            if(gab/term > 10)
            {
                term *= ((int)(gab/term)/10)+1;
            }
            //2014.04.11 by LYH << 10자리 이상되는 거래량 스케일 이상하게 나오던 문제 해결.

            int iTemp = (int)((mm_data[0]+term)/term);

            if(mm_data[0]<0) {
                //2018.05.15 by pjm (int)term 이 0일때 죽는현상
                if((int)term == 0)
                    term = 1;
                //2018.05.15 by pjm (int)term 이 0일때 죽는현상 end
                iTemp = (int)mm_data[0]/(int)term;
            }
            minValue = (double)iTemp*(double)term;

            if(minValue<mm_data[0])
                minValue = mm_data[0];

        //2021.11.01 by HJW - 정수형 데이터인 종목에서 중복된 Scale 나오는 오류 수정 >>
        if (format_index == 0 && term < 1)
            term = 1;
        //2021.11.01 by HJW - 정수형 데이터인 종목에서 중복된 Scale 나오는 오류 수정 <<

            //2014.04.11 by LYH >> 10자리 이상되는 거래량 스케일 이상하게 나오던 문제 해결.
//        if(gab/term > 10)
//        	term *= 2;
            //2014.04.11 by LYH >> 10자리 이상되는 거래량 스케일 이상하게 나오던 문제 해결.
//        }
        //2019.01.03 sdm >> 진법 틱 그래프의 Y Scale 이 중복되서 나오거나 안맞는 부분 수정 End
    }
//    public double priceToYD(double price) {
//        if(mm_data==null || mm_data.length <2)
//            return 0;
//        double p, min, max;
//        if(log){
//            p = (Math.log(price)*1000);
//            min = (Math.log(mm_data[0])*1000);
//            max = (Math.log(mm_data[1])*1000);
//        }else{
//            p = price;
//            min = mm_data[0];
//            max = mm_data[1];
//        }
//        double t1 = p-min;
//        double t2 = max-min;
//        double t= bounds.height()*(t1/t2);
//        return (double)((bounds.top+bounds.height())-t);
//    }
    public float priceToY(double price) {
        if(mm_data==null || mm_data.length <2)
            return 0;
        double p, min, max;
        if(log){
            p = (Math.log(Math.abs(price))*1000);
            if(mm_data[0] == 0)
                min = mm_data[0];
            else
                min = (Math.log(Math.abs(mm_data[0]))*1000);
            if(mm_data[1] == 0)
                max = mm_data[1];
            else
                max = (Math.log(Math.abs(mm_data[1]))*1000);
        }else{
            p = price;
            min = mm_data[0];
            max = mm_data[1];
        }
        double t1 = p-min;
        double t2 = max-min;
        double t= bounds.height()*(t1/t2);
        if(isInverse()){
            return (float)(bounds.top+t);
        }
        return (float)((bounds.top+bounds.height())-t);
    }
//    public float priceToY(float price) {
//        double p, min, max;
//        if(log){
//            p = (Math.log(price)*1000);
//            min = (Math.log(mm_data[0])*1000);
//            max = (Math.log(mm_data[1])*1000);
//        }else{
//            p = price;
//            min = mm_data[0];
//            max = mm_data[1];
//        }
//        double t1 = p-min;
//        double t2 = max-min;
//        double t= bounds.height()*(t1/t2);
//        return (float)((bounds.top+bounds.height())-t);
//    }

    public void setLog(boolean log){
        this.log = log;
    }
    //====================
    // y스케일에 현재가를 표시한다
    // boolean up=true이면 상승 false이면 하락
    //====================
    private void drawCurrentData(Canvas gl){
        String tmpPrice = _cdm.codeItem.strPrice;
        //2012. 11. 28  yscale 현재가  - 붙어서 올때 처리 : C38
//        if(tmpPrice.startsWith("-"))
//        {
//            tmpPrice = tmpPrice.substring(1);
//        }
        if(tmpPrice==null) return;
        double price=0;
        try {
            price = Double.parseDouble(tmpPrice);
        } catch (Exception e) {
            System.out.println("YScale:"+e.getMessage());
        }

        String tmpSign = _cdm.codeItem.strSign;
        //2012. 10. 11 현재가표시 : C24
        //2020.06.08 by LYH >> 마지막 봉이 아니면 무조건 전봉 기준 Start
        int startPos = _cvm.getIndex();
        int dataLen = startPos + _cvm.getViewNum() + _cvm.futureMargin;
        int nTotCnt = _cdm.getCount();
        if(dataLen>nTotCnt)
        {
            dataLen = nTotCnt;
        }
        //2020.06.08 by LYH >> 마지막 봉이 아니면 무조건 전봉 기준 End
        //if(price == 0 || tmpSign==null || tmpSign.length()<1 || !COMUtil.isyJonggaCurrentPrice()  //2014.05.28 by LYH >> 눈금위 현재가 표시 방법 선택 추가
        //2023.06.09 by SJW - 미사용 변수 주석 처리 >>
//        if(price == 0 || tmpSign==null || tmpSign.length()<1 || !COMUtil.isyJonggaCurrentPrice() || (dataLen != nTotCnt)    //2020.06.08 by LYH >> 마지막 봉이 아니면 무조건 전봉 기준
//        || COMUtil._mainFrame.bIsTradeChart) //2019. 05. 30 by hyh - 매매연습차트 개발. 항상 전봉기준으로 보기
        if(price == 0 || tmpSign==null || tmpSign.length()<1 || !COMUtil.isyJonggaCurrentPrice() || (dataLen != nTotCnt)    //2020.06.08 by LYH >> 마지막 봉이 아니면 무조건 전봉 기준
        //2023.06.09 by SJW - 미사용 변수 주석 처리 <<
        ) //2019. 05. 30 by hyh - 매매연습차트 개발. 항상 전봉기준으로 보기
        {
            //2020.06.08 by LYH >> 마지막 봉이 아니면 무조건 전봉 기준 Start
//            int startPos = _cvm.getIndex();
//            int dataLen = startPos + _cvm.getViewNum() + _cvm.futureMargin;
//            int nTotCnt = _cdm.getCount();
//            if(dataLen>nTotCnt)
//            {
//                dataLen = nTotCnt;
//            }
            //2020.06.08 by LYH >> 마지막 봉이 아니면 무조건 전봉 기준 End
            //if(dataLen>0)
            if(dataLen>0 && (dataLen < nTotCnt || price >= 0))
            {
                double[] NSClose = _cdm.getSubPacketData("종가");
                if(NSClose == null)
                {
                    return;
                }
                double[] fClose = NSClose;
                double _price = fClose[dataLen-1];
                double prePrice = _price;
                if(dataLen > 1)
                    prePrice = fClose[dataLen - 2];
                
                //2023.06.09 by SJW - 미사용 변수 주석 처리 >>
                //2019. 05. 30 by hyh - 매매연습차트 개발. 항상 전봉기준으로 보기 >>
//                if (COMUtil._mainFrame.bIsTradeChart) {
//                    drawCurrentData_data(gl, String.format("%.8f", _price), String.format("%.8f", prePrice));
//                    return;
//                }
                //2019. 05. 30 by hyh - 매매연습차트 개발. 항상 전봉기준으로 보기 <<
                //2023.06.09 by SJW - 미사용 변수 주석 처리 <<

                //2020.06.08 by LYH >> 마지막 봉이 아니면 무조건 전봉 기준 Start
//                if(COMUtil.isyJonggaCurrentPrice() && _cdm.codeItem.strStandardPrice != null && _cdm.codeItem.strStandardPrice.length()>0 && dataLen == nTotCnt)
//                {
//                    try{
//                        //2015.01.08 by LYH >> 3일차트 추가 >>
//                        String[] arrStandard = _cdm.codeItem.strStandardPrice.split("\\|");
//                        if( arrStandard.length > 2 )
//                        {
//                            prePrice = Double.parseDouble(arrStandard[0]);
//                        }
//                        else
//                            prePrice = Double.parseDouble(_cdm.codeItem.strStandardPrice);
//                        //2015.01.08 by LYH >> 3일차트 추가 <<
//                    }
//                    catch(Exception e)
//                    {
//
//                    }
//                }
                //2020.06.08 by LYH >> 마지막 봉이 아니면 무조건 전봉 기준 End
                //drawCurrentData_data(gl, String.format("%.4f", _price), String.format("%.4f", prePrice));
                drawCurrentData_data(gl, String.format("%.8f", _price), String.format("%.8f", prePrice));
                return;
            }
            return;
        }


        int sign = 3;
        try{
            sign = Integer.parseInt(tmpSign);
        } catch(Exception e) {

        }
        String strPrice = "";
        try {
            strPrice = ChartUtil.getFormatedData(_cdm.codeItem.strPrice,_cdm.getPriceFormat(), _cdm);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        if(!log)
        {
            if(format_index == 14 || format_index == 15 || format_index == 16)
                price = price * 10000;
        }
        float py = 0;
//    	float cmpPrice=-1;
//        if(_cdm.getPriceFormat() == 14) { //x 0.01
//        	cmpPrice = Float.parseFloat(COMUtil.formatFl(""+price, 2, 0));
//        } else if(_cdm.getPriceFormat() == 16) { //x 0.0001
//        	cmpPrice = Float.parseFloat(COMUtil.formatFl(""+price, 4, 0));
//        } else {
//        	cmpPrice = price;
//        }

        if(price>=mm_data[0] && price <= mm_data[1]) {
            py = priceToY(price);
        } else if(price<mm_data[0]) {
            py = priceToY(mm_data[0]);
        } else {
            py = priceToY(mm_data[1]);
        }

        int[] color=null;
        int[] textColor = null;
        if(sign>3) {//하락.
            color = CoSys.CHART_COLORS[1];
            textColor = CoSys.WHITE;
        } else if(sign < 3) { //상승.
            color= CoSys.CHART_COLORS[0];
            textColor = CoSys.WHITE;
        } else {
            color = CoSys.CHART_COLORS[2];
//            textColor = CoSys.GREY990_N_DARK;
            textColor = CoSys.GREY0_WHITE; //2021.07.20 by hanjun.Kim - kakaopay - 보합때 글자색 변경
        }

        float px = this.bounds.left+COMUtil.getPixel(1);
        //int pw = (int)COMUtil.getPixel(51);
        int pw = _cvm.Margin_R;
        int ph = (int)COMUtil.getPixel_H(18); //2021.06.04

        //2019. 04. 08 by hyh - FX마진 데이터 조회 및 실시간 처리 >>
        if (_cvm.bIsLineFillChart || _cvm.nFxMarginType >= 0) {
            pw = _cvm.Margin_R ;
            ph = (int) COMUtil.getPixel_H(18);
//            _cvm.drawFillTri(gl, px, py - (int) COMUtil.getPixel(1), COMUtil.getPixel(4), ph, color);
//            _cvm.drawFillRect(gl, px + COMUtil.getPixel(4), py - (int) COMUtil.getPixel(8), pw, ph, color, 1.0f);
            _cvm.drawCurrentPriceBox(gl, px, py-ph/2, pw-COMUtil.getPixel(4), ph, color);

            int w = _cvm.GetTextLength(strPrice);
            _cvm.drawString(gl, textColor, bounds.left + (int) COMUtil.getPixel(12), (int) py - (int) COMUtil.getPixel(0), strPrice);

            return;
        }
        //2019. 04. 08 by hyh - FX마진 데이터 조회 및 실시간 처리 <<

        boolean bContainsJipyo = false;
        //2020.04.14 당일 라인차트 추가 - hjw >>
        if(_cvm.bIsTodayLineChart) {
            String strGraph = "";
            Vector<String> strGraphs = COMUtil._mainFrame.mainBase.baseP._chart.getGraphList();
            if (strGraphs != null) {
                for (int i = 0; i < strGraphs.size(); i++) {
                    strGraph = (String) strGraphs.elementAt(i);
                    if (strGraph.equals("매물대"))
                        bContainsJipyo = true;
                }
            }
            if(bContainsJipyo) {
                pw = _cvm.Margin_R;
                ph = (int) COMUtil.getPixel_H(30);

//            _cvm.drawFillTri(gl, px, py - (int) COMUtil.getPixel(1), COMUtil.getPixel(4), ph, color);
//            _cvm.drawFillRect(gl, px + COMUtil.getPixel(4), py - (int) COMUtil.getPixel(8), pw, ph, color, 1.0f);
                if(py<ph/2) //2020.06.02 by LYH >> pricebox 상단 잘림 처리
                    py = ph/2;
                _cvm.drawCurrentPriceBox(gl, px, py - ph / 2, pw - COMUtil.getPixel(4), ph, color);

                int w = _cvm.GetTextLength(strPrice);
                _cvm.drawString(gl, textColor, bounds.left + (int) COMUtil.getPixel(12), (int) py - (int) COMUtil.getPixel(5), strPrice);
                tmpPrice = _cdm.codeItem.strChgrate;
                if(tmpPrice==null) return;
                //2012. 11. 28  yscale 등락률   - 붙어서 올때 처리 : C38
                if(tmpPrice.startsWith("-"))
                {
                    tmpPrice = tmpPrice.substring(1);
                }

                if(sign>3) {//하락.
                    tmpPrice = "-"+tmpPrice;
                }

                _cvm.drawString(gl, textColor, bounds.left+(int)COMUtil.getPixel(12),(int)py+(int)COMUtil.getPixel(7), tmpPrice);

                return;
            }
        }
        //2020.04.14 당일 라인차트 추가 - hjw <<

//        _cvm.drawFillTri(gl, px,py,COMUtil.getPixel(4),ph, color);
//        _cvm.drawFillRect(gl, px+COMUtil.getPixel(4),py-(int)COMUtil.getPixel(14),pw,ph, color, 1.0f);
        if(py<ph/2) //2020.06.02 by LYH >> pricebox 상단 잘림 처리
            py = ph/2;
        _cvm.drawCurrentPriceBox(gl, px, py-ph/2, pw-(int) COMUtil.getPixel(4), ph, color);
        //2012. 11. 28  yscale 현재가  - 붙어서 올때 처리 : C38
//        if(strPrice.startsWith("-"))
//        {
//            strPrice = strPrice.substring(1);
//        }
        //2013.03.27 by LYH >> 스케일 가격 오른쪽 정렬
        //_cvm.drawString(gl, CoSys.WHITE, px+2,(int)py-(int)COMUtil.getPixel(8), strPrice);
        int w = _cvm.GetTextLength(strPrice);
        //2021.07.12 by hanjun.Kim - kakaopay - 가격표 좌측정렬 >>
        if(COMUtil.currentTheme == COMUtil.SKIN_BLACK) {
            textColor = CoSys.GREY990;
        } else {
            textColor = CoSys.GREY0_WHITE;
        }
        _cvm.drawScaleString(gl, textColor, bounds.left+(int)COMUtil.getPixel(3),py , strPrice);
        //2013.03.27 by LYH <<
        //2021.06.04 by lyk - kakaopay - 등락률 주석 처리 >>
//        tmpPrice = _cdm.codeItem.strChgrate;
//        if(tmpPrice==null) return;
//        //2012. 11. 28  yscale 등락률   - 붙어서 올때 처리 : C38
//        if(tmpPrice.startsWith("-"))
//        {
//            tmpPrice = tmpPrice.substring(1);
//        }
//
//        if(sign>3) {//하락.
//            tmpPrice = "-"+tmpPrice;
//        }
//        //2013.03.27 by LYH >> 스케일 가격 오른쪽 정렬
//        //_cvm.drawString(gl, CoSys.WHITE, px+2,(int)py+(int)COMUtil.getPixel(5), tmpPrice);
//        w = _cvm.GetTextLength(tmpPrice);
//        _cvm.drawString(gl, CoSys.WHITE, bounds.left+(int)COMUtil.getPixel(12),(int)py+(int)COMUtil.getPixel(6), tmpPrice);
        //2021.06.04 by lyk - kakaopay - 등락률 주석 처리 <<

        //2013.03.27 by LYH <<
//        _cvm.drawString(gl, CoSys.WHITE, px-9,(int)py, "<");

    }

    //2012. 10. 11 현재가표시    : C24
    private void drawCurrentData_data(Canvas g, String sPrice, String sPrePrice)
    {
        double price = 0.0;
        try {
            price = Double.parseDouble(sPrice);
        } catch (Exception e) {
            System.out.println("YScale:"+sPrice);
            return;
        }

        String strPrice = ChartUtil.getFormatedData(sPrice, _cdm.getPriceFormat(), _cdm);

        //2012. 10. 19  업종, 선옵차트의 yscale 정보값 색상이 빨간색으로만 나오던 현상 수정 : C25
        //텍스트 색상지정
        //open>price:up, open<price:down, open==price:unch
        int[] color = new int[3];
        int[] textColor = null;
        double prePrice = Double.parseDouble(sPrePrice);
        if(price<prePrice)
        {
            color = CoSys.CHART_COLORS[1];
            textColor = CoSys.WHITE;
        }
        else if(price>prePrice)
        {
            color = CoSys.CHART_COLORS[0];
            textColor = CoSys.WHITE;
        }
        else
        {
            color = CoSys.CHART_COLORS[2];
//            textColor = CoSys.GREY990_N_DARK;
            //2021.11.04 by lyk525 - kakaopay - 보합때 글자색 변경 >>
            if(_cvm.getSkinType() == COMUtil.SKIN_BLACK) {
                textColor = CoSys.GREY990;
            } else {
                textColor = CoSys.GREY0_WHITE;
            }
            //2021.11.04 by lyk525 - kakaopay - 보합때 글자색 변경 <<
        }

        if(!log)
        {
            if(format_index == 14 || format_index == 15 || format_index == 16)
            {
                price = price * 10000;
            }
        }
        float py = 0;
        if(price>=mm_data[0] && price <= mm_data[1])
        {
            py = priceToY(price);
        }
        else if(price<mm_data[0])
        {
            //종가가 최저가보다 작은 경우 하단에 표시.
            py = priceToY(mm_data[0]) - 10;
        }
        else  //종가가 최고가보다 작은 경우 하단에 표시.
        {
            py = priceToY(mm_data[1]) + 10;
        }


        //price
        float px = this.bounds.left+COMUtil.getPixel(1);
        //int pw = (int)COMUtil.getPixel(51);
        //2012. 11. 27  마운틴차트에서 yscale 현재가 도형 높이 조절
//    	int ph = (int)COMUtil.getPixel(28);
        int ph;

        //2012. 11. 27  마운틴차트에서 yscale 현재가 도형 높이 조절
//    	_cvm.drawFillTri(g, px-COMUtil.getPixel(4),py,COMUtil.getPixel(4),ph, color);
//    	_cvm.drawFillRect(g, px,py-(int)COMUtil.getPixel(14),pw,ph, color, 1.0f);
//    	_cvm.drawString(g, CoSys.WHITE, px+2,(int)py-(int)COMUtil.getPixel(7), strPrice);

        //2019. 04. 08 by hyh - FX마진 데이터 조회 및 실시간 처리 >>
        if (_cvm.bIsLineFillChart || _cvm.nFxMarginType >= 0) {
            int pw = _cvm.Margin_R;
            ph = (int) COMUtil.getPixel_H(18);
//            _cvm.drawFillTri(g, px, py - (int) COMUtil.getPixel(1), COMUtil.getPixel(4), ph, color);
//            _cvm.drawFillRect(g, px + COMUtil.getPixel(4), py - (int) COMUtil.getPixel(8), pw, ph, color, 1.0f);

            if(py<ph/2) //2020.06.02 by LYH >> pricebox 상단 잘림 처리
                py = ph/2;
            _cvm.drawCurrentPriceBox(g, px, py-ph/2, pw-COMUtil.getPixel(4), ph, color);
            int w = _cvm.GetTextLength(strPrice);
            _cvm.drawScaleString(g, textColor, bounds.right - w + (int) COMUtil.getPixel(2), (int) py - (int) COMUtil.getPixel(0), strPrice);

            return;
        }
        //2019. 04. 08 by hyh - FX마진 데이터 조회 및 실시간 처리 <<
        else
        {
            //2020.10.13 by HJW - 종가등락율 표시방법 수정 >>
            if(COMUtil._mainFrame.bIsyJonggaCurrentPrice) {
                String tmpPrice = COMUtil.removeFrontZero(_cdm.codeItem.strGijun); //전일종가
                if(tmpPrice.startsWith("-"))
                {
                    tmpPrice = tmpPrice.substring(1);
                }
                if(tmpPrice!=null && !tmpPrice.equals("")) {
                    try {
                        prePrice = Double.parseDouble(tmpPrice);
                    } catch (Exception e) {
                    }

                    if(!log)
                    {
                        if(format_index == 14 || format_index == 15 || format_index == 16)
                        {
                            prePrice = prePrice * 10000;
                        }
                    }
                }
                double chgrate=0;
                if(prePrice != 0)
                    chgrate = (price - prePrice)*100/prePrice;

                if(chgrate<0)
                {
                    color = CoSys.CHART_COLORS[1];
                    textColor = CoSys.WHITE;
                }
                else if(chgrate>0)
                {
                    color = CoSys.CHART_COLORS[0];
                    textColor = CoSys.WHITE;
                }
                else
                {
                    color = CoSys.CHART_COLORS[2];
//                    textColor = CoSys.GREY990_N_DARK;
                    textColor = CoSys.GREY0_WHITE; //2021.07.20 by hanjun.Kim - kakaopay - 보합때 글자색 변경
                }

            }
            //2020.10.13 by HJW - 종가등락율 표시방법 수정 <<
            ph = (int)COMUtil.getPixel_H(18); //2021.06.04 by lyk - kakaopay - 높이 수정
            int pw = _cvm.Margin_R;
//        _cvm.drawFillTri(g, px,py,COMUtil.getPixel(4),ph, color);
            if(py<ph/2) //2020.06.02 by LYH >> pricebox 상단 잘림 처리
                py = ph/2;
            _cvm.drawCurrentPriceBox(g, px, py-ph/2, pw-COMUtil.getPixel(4), ph, color);
//            _cvm.drawFillRect(g, px+COMUtil.getPixel(4),py-(int)COMUtil.getPixel(14),pw,ph, color, 1.0f);
            //2013.03.27 by LYH >> 스케일 가격 오른쪽 정렬
            //_cvm.drawString(g, CoSys.WHITE, px+2,(int)py-(int)COMUtil.getPixel(7), strPrice);
            int w = _cvm.GetTextLength(strPrice);
            if(_cvm.bIsNewsChart)
                _cvm.drawString(g, textColor, bounds.left+(int)COMUtil.getPixel(7),(int)py-(int)COMUtil.getPixel(7), strPrice);
            else {
//                if (w+COMUtil.getPixel(8) > _cvm.Margin_R)
//                    _cvm.drawStringWithSizeFont(g, CoSys.WHITE, bounds.left + (int) COMUtil.getPixel(12), (int) py - (int) COMUtil.getPixel(6), (int)COMUtil.getPixel(11), strPrice, COMUtil.numericTypefaceRegular);
//                else
                //2021.07.12 by hanjun.Kim - kakaopay - 가격표 좌측정렬 >>
//                _cvm.drawString(g, textColor, bounds.left + (int) COMUtil.getPixel(3), (int) py, strPrice);
                _cvm.drawScaleString(g, textColor, bounds.left+(int)COMUtil.getPixel(3),py , strPrice);
            }
            //2013.03.27 by LYH <<
        }
//        int pw = _cvm.Margin_R;
////        _cvm.drawFillTri(g, px,py,COMUtil.getPixel(4),ph, color);
//        _cvm.drawCurrentPriceBox(g, px, py-ph/2, pw-COMUtil.getPixel(2), ph, color);

        //2021.06.04 by lyk - kakaopay - 등락률 주석 처리 >>
//        if(sPrePrice.length() != 0)
//        {
//            if(!log)
//            {
//                if(format_index == 14 || format_index == 15 || format_index == 16)
//                {
//                    prePrice = prePrice * 10000;
//                }
//            }
//            double chgrate=0;
//            //2020.10.13 by HJW - 종가등락율 표시방법 수정 >>
//            if(COMUtil._mainFrame.bIsyJonggaCurrentPrice) {
//                String tmpPrice = COMUtil.removeFrontZero(_cdm.codeItem.strGijun); //전일종가
//                if(tmpPrice.startsWith("-"))
//                {
//                    tmpPrice = tmpPrice.substring(1);
//                }
//                if(tmpPrice!=null && !tmpPrice.equals("")) {
//                    try {
//                        prePrice = Double.parseDouble(tmpPrice);
//                    } catch (Exception e) {
//                    }
//
//                    if(!log)
//                    {
//                        if(format_index == 14 || format_index == 15 || format_index == 16)
//                        {
//                            prePrice = prePrice * 10000;
//                        }
//                    }
//                }
//                if(prePrice != 0)
//                    chgrate = (price - prePrice)*100/prePrice;
//
//                if(chgrate<0)
//                {
//                    color = CoSys.CHART_COLORS[1];
//                }
//                else if(chgrate>0)
//                {
//                    color = CoSys.CHART_COLORS[0];
//                }
//                else
//                {
//                    color = CoSys.CHART_COLORS[2];
//                }
//
//            } else {
//            //2020.10.13 by HJW - 종가등락율 표시방법 수정 <<
//                if (prePrice != 0)
//                    chgrate = (price - prePrice) * 100 / prePrice;
//                if (chgrate < 0) {
//                    chgrate *= -1;
//                }
//            }
//            String strChgrate = String.format("%.2f%%", chgrate);
//            //2012. 11. 27 마운틴차트일때 등락률 표시 안함
////    		_cvm.drawString(g, CoSys.WHITE, px+2,(int)py+(int)COMUtil.getPixel(5), strChgrate);
//            if(!_cvm.bIsLineFillChart && !_cvm.bIsLineChart)
//            {
//                //2013.03.27 by LYH >> 스케일 가격 오른쪽 정렬
//                //_cvm.drawString(g, CoSys.WHITE, px+2,(int)py+(int)COMUtil.getPixel(5), strChgrate);
//                int w = _cvm.GetTextLength(strChgrate);
//                _cvm.drawString(g, CoSys.WHITE, bounds.left+(int)COMUtil.getPixel(12),(int)py+(int)COMUtil.getPixel(6), strChgrate);
//                //2013.03.27 by LYH <<
//            }
//            if(_cvm.bIsNewsChart)
//                _cvm.drawString(g, CoSys.WHITE, bounds.left+(int)COMUtil.getPixel(12),(int)py+(int)COMUtil.getPixel(6), strChgrate);
//        }
        //2021.06.04 by lyk - kakaopay - 등락률 주석 처리 <<
    }

    private void drawCompareCurrentData(Canvas gl){
        //Vector<String> pTitles = _cdm._dataTitles;
        float px = this.bounds.left+5;
        int pw = 54;
        float py = 0;
        int[] color = CoSys.CHART_COLORS[2];
        //int nLocation = -1;
        int nIndex = 0;
//    	for(int i=0; i<pTitles.size(); i++) {
//    		String strTitle = (String)pTitles.get(i);
//    		if((nLocation=strTitle.indexOf("_1")) != -1) {
//    			double strRatio = _cdm.getLastData(strTitle);
//    			if(Double.isNaN(strRatio)) continue;
//    			String strData = _cdm.getFormatData(new String(strTitle.substring(0, nLocation)), _cdm.getCount()-1);
//    			double price=0;
//    			try {
//    				price = strRatio;
//    			} catch (Exception e) {
//    				System.out.println(e.getMessage());
//    			}
//    			color = CoSys.CHART_COLORS[nIndex+4];
//    			py = this.priceToY(price);
////    			_cvm.drawFillRect(gl, px,py-(int)COMUtil.getPixel(13),
////    					(int)COMUtil.getPixel(47),(int)COMUtil.getPixel(14), color, 1.0f);
//    			_cvm.drawFillTri(gl, px-(int)COMUtil.getPixel(4),py,
//    					(int)COMUtil.getPixel(4),(int)COMUtil.getPixel(14), color);
//    			_cvm.drawFillRect(gl, px,py-(int)COMUtil.getPixel(7),
//    					(int)COMUtil.getPixel(pw),(int)COMUtil.getPixel(14), color, 1.0f);
//    			_cvm.drawString(gl, CoSys.WHITE, px+(int)COMUtil.getPixel(2),(int)py-(int)COMUtil.getPixel(1), strData);
//    			
//    			nIndex++;
//    		}
//    	}
        for(int i=0; i<5; i++) {
            if(_cdm.compareCode[i].length()<1)
                break;
            if(!COMUtil.compareChecks[i]) {
                continue;
            }
            String strTitle = _cdm.compareCode[i];
            //if((nLocation=strTitle.indexOf("_1")) != -1) {
            double strRatio = _cdm.getLastData(strTitle+"_1");
            if(Double.isNaN(strRatio)) continue;
            String strData = _cdm.getFormatData(strTitle, _cdm.getCount()-1);
            double price=0;
            try {
                price = strRatio;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
//          color = CoSys.CHART_COLORS[nIndex+4];
          //2016. 1. 29 by hyh - 비교차트 컬러 수정
            color = CoSys.COMPARE_CHART_COLORS[i+1];
            py = this.priceToY(price);
//    			_cvm.drawFillRect(gl, px,py-(int)COMUtil.getPixel(13),
//    					(int)COMUtil.getPixel(47),(int)COMUtil.getPixel(14), color, 1.0f);
//            _cvm.drawFillTri(gl, px-(int)COMUtil.getPixel(4),py,
//                    (int)COMUtil.getPixel(4),(int)COMUtil.getPixel(14), color);
//            _cvm.drawFillRect(gl, px,py-(int)COMUtil.getPixel(7),
//                    (int)COMUtil.getPixel(pw),(int)COMUtil.getPixel(14), color, 1.0f);

            _cvm.drawCurrentPriceBox(gl, px-(int)COMUtil.getPixel(4), py-(int)COMUtil.getPixel(18)/2, pw, (int)COMUtil.getPixel(18), color);

            _cvm.drawString(gl, CoSys.WHITE, px+(int)COMUtil.getPixel(2),(int)py-(int)COMUtil.getPixel(1), strData);

            nIndex++;
            //}
        }

    }
    //2013.09.06 by LYH >> 터치한 y위치의 가격 리턴.
    public double getChartPrice(float y){
        //bounds = _ac.getGraphBounds();

        log= _cvm.isLog;

        if(mm_data==null) return 0;
        double min = mm_data[0];
        double max = mm_data[1];

        double rtnValue = 0;

        if(log){
            y = y-bounds.top;
            if(mm_data[0] == 0)
                min = mm_data[0];
            else
                min = (Math.log(Math.abs(mm_data[0]))*1000);
            if(mm_data[1] == 0)
                max = mm_data[1];
            else
                max = (Math.log(Math.abs(mm_data[1]))*1000);
            double b = 1.-(y/(double)(bounds.height()));
            rtnValue = Math.exp((min + (max-min)*b)/1000);
        }else{
            y = y-bounds.top;
            double b = 1.-(y/(double)(bounds.height()));
            if(format_index == 14 || format_index == 15 || format_index == 16)
                rtnValue = (min + (max-min)*b)/10000;
            else
                rtnValue = min + (max-min)*b;
        }

        Double d = 0.0;
        String sValue = String.valueOf(rtnValue);
        if(sValue.indexOf("e")!=-1 || sValue.indexOf("E")!=-1) { //지수형 데이터의 처리
            try{
                d = Double.valueOf(sValue.trim());
            } catch(Exception e) {
            }
        } else {
            try{
                d = Double.valueOf(sValue.trim());
            } catch(Exception e) {
            }
        }

        return d;
    }

    public String getChartPriceStr(float y){
        //bounds = _ac.getGraphBounds();

        log= _cvm.isLog;

        if(mm_data==null) return "0";
        double min = mm_data[0];
        double max = mm_data[1];

        if((min == -1 || min == -10000) && max <= Integer.MIN_VALUE) {
            return "";
        }

        double rtnValue = 0;

        if(log){
            y = y-bounds.top;
            if(mm_data[0] == 0)
                min = mm_data[0];
            else
                min = (Math.log(Math.abs(mm_data[0]))*1000);
            if(mm_data[1] == 0)
                max = mm_data[1];
            else
                max = (Math.log(Math.abs(mm_data[1]))*1000);
            double b = 1.-(y/(double)(bounds.height()));
            rtnValue = Math.exp((min + (max-min)*b)/1000);
        }else{
            y = y-bounds.top;
            double b = 1.-(y/(double)(bounds.height()));
            if(format_index == 14 || format_index == 15 || format_index == 16)
                rtnValue = (min + (max-min)*b)/10000;
            else
                rtnValue = min + (max-min)*b;
        }

        Double d = 0.0;
        String sValue = String.valueOf(rtnValue);
        if(sValue.indexOf("e")!=-1 || sValue.indexOf("E")!=-1) { //지수형 데이터의 처리
            try{
                d = Double.valueOf(sValue.trim());
                sValue = ""+d.intValue();
            } catch(Exception e) {
            }
        } else {
            try{
                d = Double.valueOf(sValue.trim());
                //2023.03.30 by SJW - 1달러 미맘 종목에서 인포윈도우 발생 시 해당 위치 현재가 "0.0000"으로 노출되던 현상 수정 >>
//                sValue = ""+d.intValue();
                sValue = ""+d;
                //2023.03.30 by SJW - 1달러 미맘 종목에서 인포윈도우 발생 시 해당 위치 현재가 "0.0000"으로 노출되던 현상 수정 <<
            } catch(Exception e) {
            }
        }

        return sValue;
    }

    //역시계곡선 x축 거래량 표시
    public String makeFormatedString(String s, int format_index, double max_data, double min_data){
        String tmp=s;
//        double[] volData = _cdm.getSubPacketData("역시계곡선"+"_거래량");
//        double max_data=MinMax.getIntMaxT(volData);
//        double min_data=MinMax.getIntMinT(volData);
        String sFormat = "";
        if(max_data>10000000 && min_data>=0){
            sFormat = "× 1000";
        }
        if(max_data>1000000000 && min_data>=0){
            sFormat = "× 1000000";
        }

        switch(format_index){
            case 11://× 1
                Double d = 0.0;
                if(s.indexOf("e")!=-1 || s.indexOf("E")!=-1) { //지수형 데이터의 처리
                    try{
                        d = Double.valueOf(s.trim());
                        s = ""+d.intValue();
                    } catch(Exception e) {
//                        s = "0";
                        //System.out.println(e);
                    }
                } else {
                    try{
                        d = Double.valueOf(s.trim());
                        s = ""+d.intValue();
                    } catch(Exception e) {
//                        s = "0";
                        //System.out.println(e);
                    }
                }

                if(sFormat.equals("× 1000")){
                    tmp=COMUtil.format(new String(s.substring(0,s.length()-3)),0,3)+"K";
                }
                else if(sFormat.equals("× 1000000")){
                    tmp=COMUtil.format(new String(s.substring(0,s.length()-6)),0,3)+"M";
                }
                else{
                    double tmpVal = Double.parseDouble(tmp);
                    if(tmpVal>1000) {
                        tmpVal = tmpVal/1000;
                        String sVal = String.format("%.0f", tmpVal);
                        double tmpVal2 = Double.parseDouble(sVal);
                        tmp=COMUtil.format(String.valueOf(tmpVal2*1000),0,3);
                    } else {
                        tmp = String.format("%.0f", tmpVal);
                    }
                }
                break;
            case 12://× 1000
                if(sFormat.equals("× 1000")){
                    if(s.length()<3) return s;
                    tmp=COMUtil.format(new String(s.substring(0,s.length()-3)),0,3);
                }else{
                    tmp=COMUtil.format(s,0,3);
                }
                //format = "×1000";
                break;
            case 18:
                tmp=COMUtil.format(s,0,3);
                break;
            case 13://× 0.1
                tmp=COMUtil.format(s,1,3);
                break;
            case 14://×0.01
                tmp=COMUtil.format(s,2,3);
                break;
            case 15://× 0.001
                tmp=COMUtil.format(s,3,3);
                break;
            case 16://× 0.0001
                //2012.11.27 by LYH >> 진법 및 승수 처리.
                if(nTradeMulti>=8)
                {
                    if(s==null ||s.length()<=0) return "";
                    double dData = 0;
                    try {
                        dData = Double.parseDouble(s);
                    } catch(Exception e) {
                        dData = 0;
                    }
                    //return ChartUtil.FormatCurrency_Notation(dData, _cdm.nDispScale, _cdm.nTradeMulti, _cdm.nLogDisp);
                    return ChartUtil.FormatCurrency_Notation(dData, _cdm.nDispScale);
                }
                else if(nTradeMulti>=0)
                    tmp=COMUtil.format(s,nTradeMulti,3);
                else
                    //2012.11.27 by LYH <<
                    tmp=COMUtil.format(s,4,3);
                break;
            case 17://%
                tmp=s+"%";
                break;

        }
        return tmp;

    }
    //2013.09.06 by LYH <<
    //2021.02.18 by HJW - 매입평균선 추가 >>
    public void drawBuyAveragePrice(Canvas gl) {

        if(!_cvm.isAvgBuyPriceFunc || _cdm.strAvgBuyPrice == null || _cdm.strAvgBuyPrice.equals("")) return;


        double price=0;
        try {
            price = Double.parseDouble(_cdm.strAvgBuyPrice);
        } catch (Exception e) {
            System.out.println("YScale:"+e.getMessage());
        }

        String strPrice = "";
        try {
            //2024.06.19 by SJW - 구매 평균 가격 국내 종목에서 소수점 버림 처리 >>
//            strPrice = ChartUtil.getFormatedData(_cdm.strAvgBuyPrice,_cdm.getPriceFormat());
            if (COMUtil._mainFrame.strFileName.equals("stock")) {
                price = Math.floor(Double.parseDouble(_cdm.strAvgBuyPrice));
                strPrice = ChartUtil.getFormatedData(String.valueOf(price),_cdm.getPriceFormat());
            } else {
                strPrice = ChartUtil.getFormatedData(_cdm.strAvgBuyPrice,_cdm.getPriceFormat());
            }
            //2024.06.19 by SJW - 구매 평균 가격 국내 종목에서 소수점 버림 처리 <<
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        if(!log)
        {
            if(format_index == 14 || format_index == 15 || format_index == 16)
                price = price * 10000;
        }
        float py = 0;

        if(price>=mm_data[0] && price <= mm_data[1]) {
            py = priceToY(price);


            int[] color = CoSys.GREY600;

            int px = (int) this.bounds.left + (int) COMUtil.getPixel(1);
            int pw = _cvm.Margin_R;     //2018.06.25 by LYH >> 디자인 가이드 처리
            int ph = (int) COMUtil.getPixel(18);
            //2023.12.06 by SJW - 구매/판매 인포윈도우 기획
//            String strbuyprice = "구매평균";
            String strbuyprice = "내 평균";
            //2023.12.05 by SJW - 구매/판매 디자인 변경 요청 <<
//            _cvm.drawFillRect(gl, px + COMUtil.getPixel(4), py - (int) COMUtil.getPixel(9), pw, ph, color, 1.0f);

            _cvm.drawCurrentPriceBox(gl, px, py-ph/2, pw-COMUtil.getPixel(4), ph, color);

//            int w = (int)_cvm.GetTextLength(strPrice);
            int w = (int)_cvm.getFontWidth(strPrice, (int) COMUtil.getPixel(11));
            //2021.07.12 by hanjun.Kim - kakaopay - 가격표 좌측정렬 >>
//            _cvm.drawStringWithSize(gl, CoSys.WHITE, bounds.left + (int) COMUtil.getPixel(12), (int) py ,(int) COMUtil.getPixel(11), strPrice);
            _cvm.drawScaleString(gl, CoSys.GREY0_WHITE, (int) (bounds.left + (int) COMUtil.getPixel(3)), (int) py , strPrice, 1.0f);

//            w = (int)_cvm.GetTextLength(strbuyprice);
//            _cvm.drawString(gl, CoSys.WHITE, bounds.right - w + (int) COMUtil.getPixel(4), (int) py + (int) COMUtil.getPixel(5), strbuyprice);

            //2021.06.11 by hanjun.Kim - kakaopay - 평단가(구매평균가격) 문구 표시 >>
            color = CoSys.GREY700;
            _cvm.drawFillCornerRoundedRect(gl, Outbounds.left+(int)COMUtil.getPixel(4),py - COMUtil.getPixel(9),_cvm.GetTextLength(strbuyprice)+(int)COMUtil.getPixel(4), COMUtil.getPixel(18), color, 0.65f); // 2016.05.31 기준선 대비, 색상 굵기
//            _cvm.drawString(gl, CoSys.WHITE, Outbounds.left+(int)COMUtil.getPixel(2), py, strbuyprice);
            _cvm.drawStringWithSize(gl, CoSys.WHITE, Outbounds.left+(int)COMUtil.getPixel(6), py, (float)COMUtil.getPixel(10), strbuyprice);
            //2021.06.11 by hanjun.Kim - kakaopay - 평단가(구매평균선) 문구 표시 <<
        }

    }
    //2021.02.18 by HJW - 매입평균선 추가 <<

    //====================
    // y스케일에 최대,최소값을 표시한다
    //====================
//    private void drawMinMaxData(Canvas gl, String data, int x, int y, int[] up){
//
//        float fh = _cvm.tf.GetTextHeight();
//        
//        _cvm.drawRect(gl, x,y-fh+3,70,20, up);
//        _cvm.drawString(gl, CoSys.WHITE, x+3,y, data);
//    }
}
