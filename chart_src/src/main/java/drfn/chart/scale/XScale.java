package drfn.chart.scale;

import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;
import drfn.chart.util.COMUtil;
import drfn.chart.util.CoSys;
import drfn.chart.util.MinMax;

public class XScale
{
    public final static int YYYYMMDD=0;
    public final static int YYMMDD=1;
    public final static int YYYYMM=2;
    public final static int YYMM=3;
    public final static int MMDD=4;
    public final static int DDHH=5;
    public final static int DDHHMM=6;
    public final static int DDHHMMSS=7;
    public final static int HHMMSS=8;
    public final static int YYYY=100;
    public final static int YY=101;
    public final static int MM=102;
    public final static int DD=103;
    public final static int HH=104;
    public final static int HHMM=19;
    public final static int MMDDHHMM=20;
    public final static int MMSS=106;
    public final static int TEXT=107;

    int type=1;//일,주,월,년,분,틱
    int dataformat;//서버에서 내려주는 데이터의 포맷(일타입도 세분화 되어 있으므로  type과는 구분된다)
    RectF bounds;
    RectF Outbounds;
    public ChartDataModel _cdm;
    public ChartViewModel _cvm;
    String datakind;//자료일자
    String p1;//스케일 구분자1
    String p2;//스케일 구분자2
    String[] date=null;
    boolean formulated = false;//데이터를 받아오도록 하는 변수
    boolean viewchanged = false;
    View indicatorview = null;

    String strEmpty = " ";
    String strHour = "시";
    String strMinute = "분";
    String strDay = "일";
    String strMonth = "월";
    String strYear = "년";

    public int view;
    public int index;
    public float xfactor;
    int xw;
    boolean lineDraw = false;
    float topPos = 2;

    private double max_data = 0;
    private double min_data = 0;

    public XScale(ChartDataModel cdm, ChartViewModel cvm){
        _cdm = cdm;
        _cvm = cvm;
        if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB)) {
            topPos = 6;
        }
    }

    public void draw(Canvas gl){
        if( _cvm.bIsMiniBongChart || _cvm.bIsHideXYscale || _cvm.bIsLine2Chart)   //2019.04.30 by lyj 라인차트 xscale 삭제
            return;
        if(_cdm==null||_cvm==null||bounds==null)return;
        if(_cdm.getCount()<1)return;

        //2015.06.25 by lyk - Kagi 차트 데이터 사용
        String[] date_org = null;
        if(isSpecialDraw()) {
            date_org = _cdm.getStringData("variable_"+datakind);
        } else if(_cvm.getStandGraphName().equals("역시계곡선")) {
            date_org = _cdm.getStringData(_cvm.getStandGraphName()+"_거래량스트링");
            double[] volData = _cdm.getSubPacketData("역시계곡선"+"_거래량");
            max_data=MinMax.getIntMaxT(volData);
            min_data=MinMax.getIntMinT(volData);
        } else {
            date_org = _cdm.getStringData(datakind);
        }
        //2015.06.25 by lyk - Kagi 차트 데이터 사용 end

        if(_cdm.accrueNames!=null) {
            view = _cdm.accrueNames.length;
            index = 0;
            date = _cdm.accrueNames;
            type = 6;
            xfactor = (bounds.width()*1.0f)/(view);
            //xw = 2;
            xw = (int)((xfactor-1)/2);
        } else {
            if (date_org == null) return;

            //2015.06.25 by lyk - Kagi 차트 데이터 사용
            if (isSpecialDraw() || _cvm.getStandGraphName().equals("역시계곡선")) {
                if (date_org != null)
                    view = date_org.length;

            } else {
                view = _cvm.getViewNum();
            }
            if(view>_cdm.getCount())
                view = _cdm.getCount();
            //2015.06.25 by lyk - Kagi 차트 데이터 사용 end

            index = _cvm.getIndex();
            //2015. 2. 16 가상매매연습기 데이터없을때 날짜 표시>>
//            if (date_org.length < view) {
//                view = date_org.length - index;
//            }
            //2015. 2. 16 가상매매연습기 데이터없을때 날짜 표시<<

            date = new String[view];
            //if (date_org.length < index + view || index < 0) return;
            if (index < 0) return;
            
            int nDataCount = view;
            if (date_org.length < index + view)
            	nDataCount = date_org.length;

            //2024.01.03 by LYH - 일목균형표 줌인, 줌아웃, 스크롤 개선 >>
//            if (date_org.length < index + view)
//            	nDataCount = date_org.length;
            if (date_org.length < index + view) {
                nDataCount = date_org.length - index;
                if(nDataCount<1)
                    return;
                date = new String[nDataCount];
            }
            //2024.01.03 by LYH - 일목균형표 줌인, 줌아웃, 스크롤 개선 <<

            try {
                System.arraycopy(date_org, index, date, 0, nDataCount);
            } catch (Exception e) {
            }
            //xfactor = _cvm.getXFactor(bounds.right);
            //2015.06.25 by lyk - Kagi 차트 데이터 사용
            if (isSpecialDraw() || _cvm.getStandGraphName().equals("역시계곡선")) {
                xfactor = _cvm.getXFactorWithCnt(bounds.width(), view);
            } else {
                xfactor = _cvm.getXFactor(bounds.width());
            }
            //2015.06.25 by lyk - Kagi 차트 데이터 사용 end

            //xw = (int)(xfactor/2)-1;
            xw = (int) ((xfactor - 1) / 2);
        }

        viewchanged = true;

        if(date==null||date.length<1)return;

        if(_cvm.getStandGraphName().equals("역시계곡선")) {
            drawText(gl,date);
        } else {
            switch (type) {
                case 1://일
                    if (this.dataformat == 10) drawText(gl, date);
                    else drawDay(gl, date);
                    break;
                case 2://주
                    drawWeek(gl, date);
                    break;
                case 3://월
                    drawMonth(gl, date);
                    break;
                case 4://분
                    //2012. 11. 26  마운틴차트의 구분선을 '시' 단위로 그리기위해서 조건 추가 : C35
                    //if( (_cdm.getTerm()>=600000&&dataformat==DDHHMMSS) || _cvm.bIsLineFillChart) {
                    if ((_cdm.getTerm() >= 600000 && dataformat == DDHHMMSS)) {
                        drawHour(gl, date);
                    } else {
                        drawMin(gl, date);
                        drawPreAfterMarketArea(gl,date); //2021.05.21 by HJW - 프리애프터 적용
                    }
                    break;
                case 5://틱
                    drawTic(gl, date);
                    drawPreAfterMarketArea(gl,date); //2021.05.21 by HJW - 프리애프터 적용
                    break;
                case 6://text
                    drawText(gl, date);
                    break;
                case 7://초
                    drawSecond(gl, date);
                    break;
                case 8://년
                    drawYear(gl, date);
                    break;
            }
        }
    }
    //날짜포맷이 아니라 문자열인경우 
    //LG와 하나로 컴포넌트가 서로 틀림
    public void drawText(Canvas gl, String[] date){
        /*
        //LG방식 세로열로 쓰는 로직은 없앰
        */
        if (_cvm.XSCALE_H == 0) {
            return;
        }

        if (_cvm.bIsInnerTextVertical) {
            String[] datas = _cdm.accrueNames;
            int gab = (int) COMUtil.getPixel(5);
            float unitW = (getBounds().width() - gab * (datas.length - 2)) / date.length;

            float x = bounds.left + unitW / 2 - COMUtil.getPixel(4);
            float h = bounds.top + (int) COMUtil.getPixel(10);

            for (int i = 0; i < date.length; i++) {
                drawVertText(gl, (int) x, h, date[i]);

                x += unitW + gab;
            }
        }
        else {
	        float x= bounds.left+xw;
            float h=bounds.top;
	        int i=0;
            if(_cvm.getStandGraphName().equals("역시계곡선")) {
                int dLen = date.length;
                int startIndex=0;
                for (int k = 0; k < dLen; k++) {
                    double volData = 0.0;
                    try {
                        volData = Double.parseDouble(date[k]);
                    } catch (Exception e) {

                    }
                    if (date.length > k && volData != 0.0) {
                        startIndex = k;
                        break;
                    }
                }
                YScale yscale = COMUtil._neoChart.basic_block.getYScale()[0];

                //drawScale
                double term = (max_data - min_data) / 5;
                for(double k=min_data; k>=min_data && k<=max_data; k+=term){
                    float fx = priceToX(k);
                    drawVertLine(gl, fx);
                    _cvm.drawString(gl, _cvm.CST, (int)(fx),h+16, yscale.makeFormatedString(String.valueOf(k), 11, max_data, min_data));
                }
            } else {

                for (i = 0; i < date.length; i++) {
                    _cvm.drawLine(gl, (int) (x), h, (int) (x), h + 3, _cvm.CSL, 1.0f);

                    //2011.08.05 by LYH >> 스테틱 변수가 아니라 차트별로 높이 주어 계산하도록 수정 <<
                    _cvm.drawString(gl, _cvm.CST, (int) (x), h + 16, date[i]);
                    //drawVertLine(gl, x);
                    x += xfactor;
                }
            }
    	}
    }
    
    private void drawVertText(Canvas gl, float x, float y, String text) {
    	int fontHeight = (int)COMUtil.getPixel(13);

    	y += (int)COMUtil.getPixel(2);
        for(char c: text.trim().toCharArray()) {
        	_cvm.drawString(gl, _cvm.CST, (int)(x),y, ""+c);
            y += fontHeight;
        }
    }
    
    //====================================
    // 일간데이터는 월단위 표기
    // 년도가 바뀌는 경우 bold, 
    // 데이터 간격이 조밀한 경우, 분기별 표시하고 년표시만 한다
    //====================================
    public void drawDay(Canvas gl, String[] date){
        if(date==null || date.length < 1) return;
        String td=date[0];//오늘 날짜
        if(td==null || td.equals("")) return;
        //String ld;//이전 날짜

        float x= bounds.left;
//        ld = date[0];
        double date1=0;
        double date2=0;
        boolean bDateLine = false;
        try
        {
            date1 = Double.parseDouble(date[0]);
            date2 = Double.parseDouble(date[date.length-1]);
            if(date2-date1<200)
            {
                bDateLine = true;
            }
        }
        catch(Exception e)
        {

        }
        int nYear, nMon, nDay;
        nYear = ((int)date1%1000000)/10000;
        nMon = ((int)date1%10000)/100;
        nDay = (int)date1%100;
//        String start =makeScaleFormat(td,YYMMDD);
        String start = String.format("%02d%s%02d%s%02d", nYear, p1, nMon, p1, nDay);
        //2017.09.25 by LYH >> 자산 차트 적용
        if(_cvm.getAssetType()>0)
        {
            x += (xfactor/2);

            for(int i=0; i<date.length; i++) {
                x = this.getDateToX(i);
                if(x>=bounds.left && (i==0 || i==date.length/2 || i==date.length-1)) {
                    String strDate = makeScaleFormat(date[i],YYMMDD);
                    float strWidth = _cvm.GetTextLength(strDate);

                    //drawVertLine(gl, (int)x);

//            		if(x+strWidth > bounds.right) {
//            			x = bounds.right - strWidth;
//            		}

                    int[] assetTextColor = {171, 171, 171};
                    if(i==0)
                        _cvm.drawString(gl, assetTextColor, (int)x,bounds.top+topPos, strDate);
                    else if(i==date.length-1)
                        _cvm.drawString(gl, assetTextColor, (int)x-(int)strWidth,bounds.top+topPos, strDate);
                    else
                        _cvm.drawString(gl, assetTextColor, (int)x-(int)(strWidth/2),bounds.top+topPos, strDate);
                }
                x+=xfactor;
            }
            return;
        }
        //2017.09.25 by LYH >> 자산 차트 적용 end
        if(_cvm.bIsOneQStockChart && _cvm.bIsLineFillChart) {
            for(int i=0; i<date.length; i++) {
                x = this.getDateToX(i);
                if(x>=bounds.left && (i==0 || i==date.length/2 || i==date.length-1)) {
                    String strDate = makeScaleFormat_oneQ(date[i],YYMM);
                    Paint pnt = new Paint();
                    pnt.setTextSize(COMUtil.getPixel(12));

                    float strWidth = pnt.measureText(strDate);

                    int[] oneQTextColor = {169, 169, 169};
                    if(i==0)
                        _cvm.drawStringWithSize(gl, oneQTextColor, (int)COMUtil.getPixel_W(20),bounds.top+topPos, COMUtil.getPixel(12), strDate);
                    else if(i==date.length-1)
                        _cvm.drawStringWithSize(gl, oneQTextColor, bounds.right-(int)strWidth - (int)COMUtil.getPixel_W(20),bounds.top+topPos, COMUtil.getPixel(12), strDate);
                    else
                        _cvm.drawStringWithSize(gl, oneQTextColor, (int)(bounds.right-strWidth)/2,bounds.top+topPos, COMUtil.getPixel(12), strDate);
                }
            }
            return;
        }

        if(date.length<2000) {
            drawFix(gl, date.length);
            return;
        }
//        _cvm.drawLine(gl, (int)x,bounds.top,x,bounds.top+topPos,CoSys.GRAY, 1.0f);
        _cvm.drawString(gl, _cvm.CST, (int)x+3,bounds.top+topPos, start);

        float fx = _cvm.GetTextLength(start)+x;
        float fx2 = 0;

        x+=xfactor;

        //x+=xfactor;
//        int blockWidth = bounds.right+_cvm.Margin_R;

//        int gab = 1;
//        if(date.length>=600) gab=60;
//        else if(date.length>=720) gab=90;
//        else if(date.length>=840) gab=120;
//        else gab=1;
        int dLen = date.length;

        int lYear = nYear;
        int lMon = nMon;
        int dateiVal = 0;
        int lDay = -1;
        if(nDay>=15)
            lDay = nMon;

        int nMonthPeriod = 1;
        if(dLen>0)
            nMonthPeriod = calcMonth(td, date[dLen-1]);
        boolean bDrawVertLine = false;

        AREA area;  //2020.07.06 by LYH >> 캔들볼륨
        for(int i=1;i<dLen;i++){
//            td = date[i];
//            String year =getYear(td,YY);
//            String mon = getMonth(td);
//            String lyear =getYear(ld,YY);
//            String lmon = getMonth(ld);
            //2012.11.29 by LYH >> 날짜 에러 처리.
            try
            {
                dateiVal = Integer.parseInt(date[i]);
            }
            catch(Exception e)
            {
                dateiVal = 0;
            }
            //2012.11.29 by LYH <<

            nMon = dateiVal % 10000/100;
            nDay = dateiVal % 100;

            //2020.07.06 by LYH >> 캔들볼륨 >>
            area = _cvm.getArea(i);
            if(area!=null)
            {
                x = area.getLeft();
            }
            //2020.07.06 by LYH >> 캔들볼륨 <<

            if(!_cvm.bIsNoScale && (nMon != lMon || (bDateLine && nDay>=15 && lDay!= lMon))){
                nYear = (dateiVal%1000000)/10000;

//                _cvm.drawLine(gl, x,bounds.top,x,bounds.top,_cvm.CST, 1.0f);
//                _cvm.drawLine(gl, x+ blockWidth,bounds.top,x+blockWidth,bounds.top,_cvm.CST, 1.0f);
                if(nYear == lYear){
                    if(nMonthPeriod < 36 && (nMonthPeriod < 12 || (nMonthPeriod >= 12 && nMon%3 == 1))) {
                        if (x > fx) {
                            if (x > fx2) {
                                //2014. 3. 28 일차트에서 차트 좁히고 공간이 좀 많이 남을 때 월만 보이는데, 이때는 날짜도 보여주기>>
                                //                    		String strDate = String.format("%02d", nMon);
                                //                    		if(bDateLine)
                                //                    			strDate = String.format("%02d/%02d", nMon, nDay);
                                String strDate = String.format("%02d", nMon);
                                if(nMonthPeriod<=3)
                                    strDate = String.format("%02d%s%02d", nMon, p1, nDay);
                                //2014. 3. 28 일차트에서 차트 좁히고 공간이 좀 많이 남을 때 월만 보이는데, 이때는 날짜도 보여주기<<

                                if (x < (bounds.right - _cvm.GetTextLength(strDate))) {
                                    _cvm.drawString(gl, _cvm.CST, (int) x + (int) COMUtil.getPixel(1), bounds.top + topPos, strDate);
                                    //	                    	_cvm.drawString(gl, _cvm.CST, (int)x+3,bounds.top+topPos, makeScaleFormat(td,MM));
                                    //	                    	_cvm.drawString(gl, _cvm.CST, (int)x+3+blockWidth,bounds.top+topPos, makeScaleFormat(td,MM));

                                    //fx2 = x + 30;
                                }
                                fx2 = x + _cvm.GetTextLength(strDate) + (int) COMUtil.getPixel(2);
                            }
                        }
                        bDrawVertLine = true;
                    }
                }else{
                    if(x>fx){
                        if(x>fx2) {
                            String strDate = String.format("%d%s%02d",(dateiVal/10000)%100, p1, nMon);
                            
                            if(_cvm.bIsNewsChart)
                            {
                                strDate = strDate = String.format("%d%s%02d%s%02d",(dateiVal/10000)%100, p1, nMon, p1, nDay);
                            }

                            if(x < (bounds.right - _cvm.GetTextLength(strDate)))
                            {
                                _cvm.drawString(gl, _cvm.CST, (int)x-(int)COMUtil.getPixel(2),bounds.top+topPos, strDate);
                                //                        _cvm.drawString(gl, CoSys.xscaleTextColor, (int)x-5,bounds.top+topPos, makeScaleFormat(td,YYYYMM));
                                //                        _cvm.drawString(gl, CoSys.xscaleTextColor, (int)x-5+blockWidth,bounds.top+topPos, makeScaleFormat(td,YYYYMM));

                            }
                            fx2 = x + _cvm.GetTextLength(strDate);                            
                        }
                    }
                    bDrawVertLine = true;
                    lYear = nYear;
                }
                if(bDrawVertLine)
                {
                    _cvm.drawLine(gl, x,bounds.top,x,bounds.top,_cvm.CST, 1.0f);

                    drawVertLine(gl, x);

                    bDrawVertLine = false;
                }
                if(!_cvm.bIsNewsChart)
                {
                	_cvm.drawLine(gl, x,bounds.top,x,bounds.top,_cvm.CST, 1.0f);
                }

//                drawVertLine(gl, x);

                lMon = nMon;
                if(nDay>=15)
                    lDay = lMon;
            }
            if(i==dLen-1)
            {
                String strDate = String.format("%02d%s%02d", nMon, p1, nDay);
                int w = _cvm.GetTextLength(strDate);
                if(_cvm.bIsNoScale) //2016.12.01 by LYH >> 스케일 없슴 옵션 추가(테마분석 차트)
                    _cvm.drawString(gl, _cvm.CST, bounds.right+_cvm.PADDING_RIGHT - w - (int)COMUtil.getPixel(4),bounds.top+topPos, strDate); //2016.09.08 by LYH >> 오른쪽 여백 설정 기능
                else
                    _cvm.drawString(gl, _cvm.CST, bounds.right+_cvm.PADDING_RIGHT+(int)COMUtil.getPixel(1),bounds.top+topPos, strDate); //2016.09.08 by LYH >> 오른쪽 여백 설정 기능
            }
            //x=(xfactor*(i+gab));
            x=bounds.left + (xfactor*(i+1));
//            ld = td;
            lMon = nMon;

        }
    }

    public void drawWeek(Canvas gl, String[] date){
        if(date==null || date.length < 1) return;
        double date1=0;
        double date2=0;
        boolean bMonthLine = false;
        try
        {
            date1 = Double.parseDouble(date[0]);
            date2 = Double.parseDouble(date[date.length-1]);
            if(date2-date1>=20000)
            {
                bMonthLine = true;
            }
        }
        catch(Exception e)
        {

        }
        if(bMonthLine)
        {
            drawMonth(gl, date);
            return;
        }

        String td=date[0];//오늘 날짜
        float x= getBounds().left;

        if(_cvm.bIsOneQStockChart && _cvm.bIsLineFillChart) {
            for(int i=0; i<date.length; i++) {
                x = this.getDateToX(i);
                if(x>=bounds.left && (i==0 || i==date.length/2 || i==date.length-1)) {
                    String strDate = makeScaleFormat_oneQ(date[i],YYMM);
                    Paint pnt = new Paint();
                    pnt.setTextSize(COMUtil.getPixel(12));

                    float strWidth = pnt.measureText(strDate);

                    int[] oneQTextColor = {169, 169, 169};
                    if(i==0)
                        _cvm.drawStringWithSize(gl, oneQTextColor, (int)COMUtil.getPixel_W(20),bounds.top+topPos, COMUtil.getPixel(12), strDate);
                    else if(i==date.length-1)
                        _cvm.drawStringWithSize(gl, oneQTextColor, bounds.right-(int)strWidth - (int)COMUtil.getPixel_W(20),bounds.top+topPos, COMUtil.getPixel(12), strDate);
                    else
                        _cvm.drawStringWithSize(gl, oneQTextColor, (int)(bounds.right-strWidth)/2,bounds.top+topPos, COMUtil.getPixel(12), strDate);
                }
            }
            return;
        }

        if(date.length<2000) {
            drawFix(gl, date.length);
            return;
        }

        String start =makeScaleFormat(td,YYMMDD);
//        _cvm.drawLine(gl, (int)x,getBounds().top,(int)x,getBounds().top,_cvm.CST, 1.0f);
        _cvm.drawString(gl, _cvm.CST, (int)x+3,bounds.top+topPos, start);
        x+=xfactor;


        float fx = _cvm.GetTextLength(start)+x;

        int nMon, lMon=0;
        nMon = ((int)date1%10000)/100;

        String strDate = "";
        float fx2=0;
        int dateiVal = 0;
        for(int i=1;i<date.length;i++){
            try
            {
                dateiVal = Integer.parseInt(date[i]);
            }
            catch(Exception e)
            {
                dateiVal = 0;
            }
            //2012.11.29 by LYH <<

            nMon = dateiVal % 10000/100;
            td = date[i];
            if(nMon%3==1 && lMon!=nMon){
                _cvm.drawLine(gl, (int)x,getBounds().top,(int)x,getBounds().top,_cvm.CST, 1.0f);
                if(x>fx && x>fx2) {
                    if(nMon==1)
                        strDate = String.format("%d%s%02d",dateiVal/10000, p1, nMon);
                    else
                        strDate = String.format("%02d",nMon);
                    if(x < (bounds.right - _cvm.GetTextLength(strDate)))
                    {
                        _cvm.drawString(gl, _cvm.CST, (int)x+3,bounds.top+topPos, strDate);
                        fx2 = x + _cvm.GetTextLength(strDate);
                    }
                }
                drawVertLine(gl, (int)x);
            }
            if(i==date.length-1)
            {
                _cvm.drawString(gl, _cvm.CST, bounds.right+_cvm.PADDING_RIGHT+(int)COMUtil.getPixel(1),bounds.top+topPos, makeScaleFormat(td,MMDD));
            }
            x+=xfactor;
            lMon = nMon;
        }
    }
    public void drawMonth(Canvas gl, String[] date){
        if(date==null || date.length < 1) return;

        if(date.length<2000) {
            drawFix(gl, date.length);
            return;
        }

        String td;//오늘 날짜
        String ld;//이전 날짜
        float x= getBounds().left;
        ld = date[0];
        String start =makeScaleFormat(ld,YYYYMM);
//        _cvm.drawLine(gl,(int)x,getBounds().top,(int)x,getBounds().top,_cvm.CST, 1.0f);
        _cvm.drawString(gl, _cvm.CST, (int)x+3,bounds.top+topPos, start, 0.48f);
        x+=xfactor;


        String year = null;
        String lyear = null;
        float fx = _cvm.GetTextLength(start)+x;

        //Vertline 계산
        //x=xfactor;
        int gab = 1;
//        if(date.length>=120 && date.length<240) gab=6;
//        else if(date.length>=240 && date.length<360) gab=12;
//        else if(date.length>=360 && date.length<480) gab=24;
//        else if(date.length>=480 && date.length<600) gab=30;
//        else if(date.length>=600 && date.length<720) gab=60;
//        else if(date.length>=720) gab=120;
//        else gab=1;

        //for(int i=1;i<date.length;i++){
        String strDate="";
        float fx2 = 0;
        for(int i=1;i<date.length;i+=gab){
            td = date[i];
            year = getYear(td,YYYY);
            if(lyear==null) {
                lyear = getYear(ld,YYYY);
            }
            if(!(year.equals(lyear))){
                _cvm.drawLine(gl,(int)x,getBounds().top,(int)x,getBounds().top,_cvm.CST, 1.0f);
                if(x>fx && x>fx2) {
                    strDate = makeScaleFormat(year,YYYY);
                    if(x < (bounds.right - _cvm.GetTextLength(strDate)))
                    {
                        _cvm.drawString(gl, _cvm.CST, (int)x+3,bounds.top+topPos, strDate, 0.48f);
                        fx2 = x + _cvm.GetTextLength(strDate);
                    }
                }
                drawVertLine(gl, (int)x);
            }
            if(i==date.length-1)
            {
                _cvm.drawString(gl, _cvm.CST, bounds.right+_cvm.PADDING_RIGHT+(int)COMUtil.getPixel(1),bounds.top+topPos, makeScaleFormat(td,YYYYMM), 0.48f);
            }
            //x=(xfactor*(i+gab));
            x=bounds.left + (xfactor*(i+gab));
//            ld = td;
            lyear = year;
        }
    }
    public void drawYear(Canvas gl, String[] date){
        if(date==null || date.length < 1) return;

        if(date.length<2000) {
            drawFix(gl, date.length);
            return;
        }

        float x= getBounds().left;
//        String start = date[0];
        String ld = date[0];
        String start =makeScaleFormat(ld,YYYY);
//        _cvm.drawLine(gl,(int)x,getBounds().top,(int)x,getBounds().top,_cvm.CST, 1.0f);
        _cvm.drawString(gl, _cvm.CST, (int)x+3,bounds.top+topPos, start);
        x+=xfactor;

        float fx = _cvm.GetTextLength(start)+x;
        int gab = 5;
        if(date.length>=50) gab=15;
        else if(date.length>=30) gab=10;

        String strDate;
        for(int i=1;i<date.length;i+=gab){

            if(x>fx) {
                ld = date[i];
                strDate =makeScaleFormat(ld,YYYY);
                if(x < (bounds.right - _cvm.GetTextLength(strDate))) {
                    _cvm.drawString(gl, _cvm.CST, (int) x + 3, bounds.top + topPos, strDate);
                    _cvm.drawLine(gl, (int) x, getBounds().top, (int) x, getBounds().top, _cvm.CST, 1.0f);
                    drawVertLine(gl, (int) x);
                    fx = x + _cvm.GetTextLength(strDate);
                }
            }

            //x=(xfactor*(i+gab));
            x=bounds.left + (xfactor*(i+gab));
        }
        if(date.length>0)
        {
            ld = date[date.length-1];
            strDate =makeScaleFormat(ld,YYYY);
            _cvm.drawString(gl, _cvm.CST, bounds.right+_cvm.PADDING_RIGHT+(int)COMUtil.getPixel(1),bounds.top+topPos, strDate);
        }
    }

    public void drawHour(Canvas gl, String[] date){//날짜가 바뀌면 표시
        if(date.length<2000) {
            drawFix(gl, date.length);
            return;
        }

        String td=date[0];//오늘 날짜
        String ld;//이전 날짜
        float x= bounds.left;
        ld = date[0];
        //2012. 11. 27  마운틴차트에서는 시작xscale 값이 날짜만 나오도록. 
//      String start =makeScaleFormat(td,DDHHMM);
        String start =getDay(td);

        _cvm.drawLine(gl,(int)x,bounds.top,(int)x,bounds.top,_cvm.CST, 1.0f);
        _cvm.drawString(gl, _cvm.CST, (int)x+3,bounds.top+topPos, start);

        float fx = _cvm.GetTextLength(start)+x;
        x+=xfactor;

        String day = null;
        String lday = null;

        //float blockWidth = bounds.right+_cvm.Margin_R;
        for(int i=1;i<date.length;i++){
            td = date[i];
            day = getDay(td);
            if(lday==null) {
                lday=getDay(ld);
            }
            if(!(day.equals(lday))){

                _cvm.drawLine(gl,(int)x,bounds.top,(int)x,bounds.top,_cvm.CST, 1.0f);
//                _cvm.drawLine(gl,(int)x+ blockWidth,bounds.top,(int)x+blockWidth,getBounds().top,_cvm.CST, 1.0f);
                if(x>fx) {
                    _cvm.drawString(gl, _cvm.CST, (int)x+3,bounds.top+topPos, day);
                }

                drawVertLine(gl, (int)x);
            }
            x+=xfactor;
//            ld = td;
            lday = day;
        }
    }
    public void drawHour_day(Canvas gl, String[] date){//날짜가 바뀌면 표시
        //2013. 9. 3 분틱 날짜구분선 보이기 여부 처리 >>
        if(!COMUtil.isDayDivisionLineShow())
        {
            return;
        }
        //2013. 9. 3 분틱 날짜구분선 보이기 여부 처리 >>

        String td=date[0];//오늘 날짜
        String ld;//이전 날짜
        float x= bounds.left;
        ld = date[0];
        //2012. 11. 27  마운틴차트에서는 시작xscale 값이 날짜만 나오도록. 
//      String start =makeScaleFormat(td,DDHHMM);
        //String start =getDay(td);

//        _cvm.drawLine(gl,(int)x,bounds.top,(int)x,bounds.top,_cvm.CST, 1.0f);
//        _cvm.drawString(gl, _cvm.CST, (int)x+3,bounds.top+topPos, start);

        //float fx = _cvm.GetTextLength(start)+x;
        x+=xfactor;

        String day = null;
        String lday = null;

        //float blockWidth = bounds.right+_cvm.Margin_R;
        //2014.05.23 by LYH >> 날짜 구분선 시작 시간 포함.
        String strOpenTime = _cdm.getOpenTime();
        int nOpenTime = -1;
        if(strOpenTime != null && strOpenTime.length() >= 5)
        {
            try
            {
                nOpenTime = Integer.parseInt(strOpenTime)/10000;
            }
            catch(Exception e)
            {

            }
        }
        if(nOpenTime>0)
        {
            String hour = null;
            int nHour = -1;
            for(int i=1;i<date.length;i++){
                td = date[i];
                day = getDay(td);
//	            if(lday==null) {
//	            	lday=getDay(ld);
//	            }
                hour=getHour(td);
                try{
                    nHour = Integer.parseInt(hour);
                }catch(Exception e)
                {
                    nHour = -1;
                }
                if (lday==null) {
                    if(nHour>nOpenTime)
                        lday=getDay(ld);
                }
                //System.out.println("day "+ day + "lday "+lday+"nHour " + nHour+"nOpenTime " + nOpenTime);
                if(!(day.equals(lday)) && nHour>=nOpenTime){

                    //_cvm.drawLine(gl, x, _cvm.Margin_T, x, Outbounds.top+ Outbounds.height()-bounds.height(), color, 0.5f);
//	                _cvm.drawLine(gl, x, _cvm.Margin_T, x, bounds.top- (int)COMUtil.getPixel(5), CoSys.RED, 0.5f);
                    //_cvm.drawLine(gl, x, _cvm.Margin_T, x, Outbounds.top+ Outbounds.height()-bounds.height(), CoSys.RED, 0.5f);
                    _cvm.drawDashLine(gl, x, _cvm.Margin_T, x, Outbounds.top+ Outbounds.height()-bounds.height(), CoSys.RED, 0.5f);
                }
                x+=xfactor;
                if(nHour>=nOpenTime)
                    lday = day;
            }
        }
        else
        //2014.05.23 by LYH << 날짜 구분선 시작 시간 포함.
        {
            for(int i=1;i<date.length;i++){
                td = date[i];
                day = getDay(td);
                if(lday==null) {
                    lday=getDay(ld);
                }
                if(!(day.equals(lday))){

                    //_cvm.drawLine(gl, x, _cvm.Margin_T, x, Outbounds.top+ Outbounds.height()-bounds.height(), color, 0.5f);
                    //_cvm.drawLine(gl, x, _cvm.Margin_T, x, bounds.top- (int)COMUtil.getPixel(5), CoSys.RED, 0.5f);
                    _cvm.drawDashLine(gl, x, _cvm.Margin_T, x, bounds.top- (int)COMUtil.getPixel(5), CoSys.RED, 0.5f);
                }
                x+=xfactor;
//            ld = td;
                lday = day;
            }
        }
    }
    public void drawMin(Canvas gl, String[] date){
//        if(_cvm.bIsLineChart)
//            return;
        String td;//현재 시간
        String ld;//이전 시간
        //141424
        float x= getBounds().left;
        ld = date[0];

        //if(_cvm.bIsOneQStockChart && _cvm.bIsLineFillChart) {
        if(_cvm.bIsOneQStockChart && _cvm.bIsLineFillChart) {
            int nUnit = _cdm.getTerm();
            String strDate = "";
            Calendar calendar = new GregorianCalendar(Locale.KOREA);
            int nYear = 0;
            String strMMDD = "";
            String startMonth = "";
            String midMonth = "";
            String endMonth = "";
            for(int i=0; i<date.length; i++) {
                x = this.getDateToX(i);
                nYear = calendar.get(Calendar.YEAR);
                if(x>=bounds.left && (i==0 || i==date.length/2 || i==date.length-1)) {

                    if (nUnit < 7)
                        strDate = makeScaleFormat_oneQ(date[i],HHMM);
                    else if (nUnit > 6 && nUnit < 31) {
                        strDate = date[i];
                        if(i==0) {
                            if (strDate.length() >3) {
                                startMonth = strDate.substring(0,2);
                                strMMDD = strDate.substring(0,4);
                            }
                        }
                        else if(i==date.length-1) {
                            if (strDate.length() >3) {
                                endMonth = strDate.substring(0,2);
                                strMMDD = strDate.substring(0,4);
                            }
                        }
                        else {
                            if (strDate.length() >3) {
                                midMonth = strDate.substring(0,2);
                                strMMDD = strDate.substring(0,4);
                            }
                        }
                        if(i==0) {
                            if((startMonth.equals("12")  && midMonth.equals("01")) || (startMonth.equals("12")  && endMonth.equals("01"))) {
                                nYear -= 1;
                            }
                        }
                        else if(i!=0 && i!=date.length-1) {
                            if(midMonth.equals("12")  && endMonth.equals("01")) {
                                nYear -= 1;
                            }
                        }

                        strDate = makeScaleFormat_oneQ(nYear+strMMDD, DD);
                    }
                    else
                        strDate = makeScaleFormat_oneQ(date[i],MMDD);

                    Paint pnt = new Paint();
                    pnt.setTextSize(COMUtil.getPixel(12));

                    float strWidth = pnt.measureText(strDate);

                    int[] oneQTextColor = {169, 169, 169};
                    if(i==0)
                        _cvm.drawStringWithSize(gl, oneQTextColor, (int)COMUtil.getPixel_W(20),bounds.top+topPos, COMUtil.getPixel(12), strDate);
                    else if(i==date.length-1)
                        _cvm.drawStringWithSize(gl, oneQTextColor, bounds.right-(int)strWidth - (int)COMUtil.getPixel_W(20),bounds.top+topPos, COMUtil.getPixel(12), strDate);
                    else
                        _cvm.drawStringWithSize(gl, oneQTextColor, (int)(bounds.right-strWidth)/2,bounds.top+topPos, COMUtil.getPixel(12), strDate);
                }
            }
            return;
        }

//        if(date.length<2000) {
//            drawFix(gl, date.length);
//            return;
//        }
        //첫 데이터의 시간 표시
        String start= makeScaleFormat(ld,MMDDHHMM);
        if(_cvm.bStandardLine || _cvm.bIsLineFillChart || _cvm.bIsLineChart || _cvm.bIsLine2Chart || _cvm.bInvestorChart)
            start= makeScaleFormat(ld,HHMM);
        else
        {
            if(date.length<2000) {
                drawFix(gl, date.length);
                return;
            }
        }

        if(!_cvm.bIsLineFillChart)
            _cvm.drawLine(gl,(int)x,getBounds().top,(int)x,getBounds().top,_cvm.CST, 1.0f);
        _cvm.drawString(gl, _cvm.CST, (int)x+3,bounds.top+topPos, start);
        x+=xfactor;

        String ld_StartDate = makeScaleFormat(ld,MMDD);		//2014. 3. 28 분차트에서 동일한 날짜일 때는 날짜빼고 시간만 보여주기

        float w = _cvm.GetTextLength(start)+x+2;
        float fx=w+(int)x;
        int dLen = date.length;
        String lhour = null;
        String hour = null;
        int nLineFillIndex = 0;
        for(int i=1;i<dLen;i++){
            td = date[i];

            String td_Date = makeScaleFormat(td, MMDD);		//2014. 3. 28 분차트에서 동일한 날짜일 때는 날짜빼고 시간만 보여주기

//            String min = getMin(td);
//            String sec = getSec(td);
            hour=getHour(td);
            if(lhour==null) {
                lhour= getHour(ld);
            }
            /*
            if(min.equals("30")&&sec.equals("00")){
                g.setColor(_cvm.CST);
                g.drawLine((int)x,getBounds().y,(int)x,getBounds().y+3);
            }
            */
            String min = getMin(td);
            if(!hour.equals(lhour) && !min.equals("99")){
//            	_cvm.drawLine(gl,(int)x,getBounds().top,(int)x,getBounds().top+5,_cvm.CST, 1.0f);
                nLineFillIndex++;
                if(x>fx){
//                    _cvm.drawLine(gl,(int)x,getBounds().top,(int)x,getBounds().top+5,_cvm.CST, 1.0f);
                    String strDate;
                    if(_cvm.bStandardLine || _cvm.bIsLineFillChart || _cvm.bIsLineChart || _cvm.bIsLine2Chart || _cvm.bInvestorChart)
                        strDate = makeScaleFormat(td,HHMM);

                    else
                    {
                        //2014. 3. 28 분차트에서 동일한 날짜일 때는 날짜빼고 시간만 보여주기>>
                        if(td_Date.equals(ld_StartDate))	strDate = makeScaleFormat(td,HHMM);
                        else
                        {
                            strDate = makeScaleFormat(td,MMDDHHMM);
                            ld_StartDate = td_Date;
                        }
                        //2014. 3. 28 분차트에서 동일한 날짜일 때는 날짜빼고 시간만 보여주기<<
                    }
                    if(x < (bounds.right - _cvm.GetTextLength(strDate) + (int)COMUtil.getPixel(2)))
                    {
                        if(_cvm.bIsLineFillChart)
                        {
                            if(nLineFillIndex == 3) {   //3시간 간격 찍기
                                float strWidth = _cvm.GetTextLength(strDate);
                                _cvm.drawString(gl, _cvm.CST, x - (strWidth + xfactor) / 2, bounds.top + topPos, strDate);
                            }
                        }
                        else
                            _cvm.drawString(gl, _cvm.CST, (int)x+(int)COMUtil.getPixel(1),bounds.top+topPos, strDate);
                    }
                    fx=(int)x+w;
                    if(!_cvm.bIsLineFillChart)
                        drawVertLine(gl, (int)x);
                }else{
                    //_cvm.drawLine(gl,(int)x,getBounds().top,(int)x,getBounds().top+5,_cvm.CST, 1.0f);
                }
                //drawVertLine(gl, (int)x);
            }
            if(i==date.length-1)
            {
                String strDate = makeScaleFormat(td,HHMM);
                if(_cvm.bIsLineFillChart) {
                    float strWidth = _cvm.GetTextLength(strDate);
                    float xPos = bounds.right - strWidth - COMUtil.getPixel_W(2);
                    _cvm.drawString(gl, _cvm.CST, (int) xPos, bounds.top + topPos, strDate);
                }
                else {
                    if (!strDate.startsWith("88") && !strDate.startsWith("99"))
                        _cvm.drawString(gl, _cvm.CST, bounds.right + _cvm.PADDING_RIGHT + (int) COMUtil.getPixel(1), bounds.top + topPos, strDate);
                }
            }
            x+=xfactor;
            lhour = hour;
        }

        if(_cvm.chartType != COMUtil.COMPARE_CHART && !_cvm.bIsLineFillChart && !_cvm.bIsLineChart && !_cvm.bIsLineChart && !_cvm.bInvestorChart)
            drawHour_day(gl, date);
    }

    public void drawSecond(Canvas gl, String[] date){
        if(date.length<2000) {
            drawFix(gl, date.length);
            return;
        }
        String td;//현재 시간
        String ld;//이전 시간
        //141424
        float x= getBounds().left;
        ld = date[0];
        //첫 데이터의 시간 표시
        String start= makeScaleFormat(ld,DDHHMMSS);

        _cvm.drawLine(gl,(int)x,getBounds().top,(int)x,getBounds().top,_cvm.CST, 1.0f);
        _cvm.drawString(gl, _cvm.CST, (int)x+3,bounds.top+topPos, start);
        x+=xfactor;

        float w = _cvm.GetTextLength(start)+x+2;
        float fx=w+(int)x;
        int dLen = date.length;
        String lhour = null;
        String hour = null;
        //2016.12.26 by LYH >> 초차트 처리 및 초주기 설정 추가
//        for(int i=1;i<dLen;i++){
//            td = date[i];
//
////            String min = getMin(td);
////            String sec = getSec(td);
//            hour=getHour(td);
//            if(lhour==null) {
//                lhour= getHour(ld);
//            }
//            /*
//            if(min.equals("30")&&sec.equals("00")){
//                g.setColor(_cvm.CST);
//                g.drawLine((int)x,getBounds().y,(int)x,getBounds().y+3);
//            }
//            */
//            if(!hour.equals(lhour)){
////            	_cvm.drawLine(gl,(int)x,getBounds().top,(int)x,getBounds().top+5,_cvm.CST, 1.0f);
//                if(x>fx){
//                    _cvm.drawLine(gl,(int)x,getBounds().top,(int)x,getBounds().top+5,_cvm.CST, 1.0f);
//                    _cvm.drawString(gl, _cvm.CST, (int)x+3,bounds.top+topPos, makeScaleFormat(td,DDHHMMSS));
//                    fx=(int)x+w;
//                }else{
//                    _cvm.drawLine(gl,(int)x,getBounds().top,(int)x,getBounds().top+5,_cvm.CST, 1.0f);
//                }
//                drawVertLine(gl, (int)x);
//            }
//            x+=xfactor;
//            lhour = hour;
//        }

        for(int i=1;i<dLen;i++){
            if(i==dLen/2){
                _cvm.drawLine(gl,(int)x,getBounds().top,(int)x,getBounds().top,_cvm.CST, 1.0f);
                //String strDate = makeScaleFormat(date[i], dataformat);
                String strDate = makeScaleFormat(date[i],HHMM);
                if(x < (bounds.right - _cvm.GetTextLength(strDate) + (int)COMUtil.getPixel(2))) {
                    _cvm.drawString(gl, _cvm.CST, (int) x + 3, bounds.top + topPos, strDate);
                }
                td=date[i];
                drawVertLine(gl, (int)x);
            }
//                term+=dgap;
            x+=xfactor;
        }
        if(date.length>0)
        {
            td=date[date.length-1];
            String strDate = makeScaleFormat(td,HHMMSS);
            if(!strDate.startsWith("88")&&!strDate.startsWith("99"))
                _cvm.drawString(gl, _cvm.CST, bounds.right+_cvm.PADDING_RIGHT+(int)COMUtil.getPixel(1),bounds.top+topPos, strDate);
        }

        if(_cvm.chartType != COMUtil.COMPARE_CHART && dataformat != 8)
            drawHour_day(gl, date);
        //2016.12.26 by LYH >> 초차트 처리 및 초주기 설정 추가 end
    }
    public void drawTic(Canvas gl, String[] date){
        if(date.length<2000) {
            drawFix(gl, date.length);
            return;
        }
        String td = date[0];//오늘 날짜

        //2019. 07. 11 by hyh - 데이터가 없을 때 X축 그리다가 죽는 에러 수정 >>
        if (td == null) {
            return;
        }
        //2019. 07. 11 by hyh - 데이터가 없을 때 X축 그리다가 죽는 에러 수정 <<

        String fd=makeScaleFormat(td,dataformat);

        //2014. 1. 29 틱차트 장마감 봉에서 시간이 88:88:88 로 표시되는 현상>>
        if(fd.contains("88:88:88"))
        {
            fd = fd.replace("88:88:88", "장마감");
        }
        //2014. 1. 29 틱차트 장마감 봉에서 시간이 88:88:88 로 표시되는 현상<<

        float fw = _cvm.GetTextLength(fd);
        float gap = fw*1;//픽셀
        int cnt = (int)(getBounds().right/gap);
        if(cnt==0) return;
        float x= getBounds().left;
        int dateLen = date.length;
        if(dateLen<cnt){
            //첫 데이터의 시간 표시
            _cvm.drawLine(gl,(int)x,getBounds().top,(int)x,getBounds().top,_cvm.CST, 1.0f);
            _cvm.drawString(gl, _cvm.CST, (int)x+3,bounds.top+topPos, fd);
        }else{
            //첫 데이터의 시간 표시
            _cvm.drawLine(gl,(int)x,getBounds().top,(int)x,getBounds().top,_cvm.CST, 1.0f);
            _cvm.drawString(gl, _cvm.CST, (int)x+3,bounds.top+topPos, fd);
            float dgap=dateLen/cnt;//데이터 간격

//            int term=0;
            String min = getMin(td);
            for(int i=0;i<dateLen;i++){
//                if(!min.equals(getMin(date[term]))){
//                    _cvm.drawLine(gl,(int)x,getBounds().top,(int)x,getBounds().top,_cvm.CST, 1.0f);
//                    _cvm.drawString(gl, _cvm.CST, (int)x+3,bounds.top+topPos, makeScaleFormat(date[term],dataformat));
//                    td=date[term];
//                    drawVertLine(gl, (int)x);
//                    min = getMin(date[term]);
//                }
//                term+=dgap;
//                x+=xfactor*dgap;
                if(i==dateLen/2){
                    _cvm.drawLine(gl,(int)x,getBounds().top,(int)x,getBounds().top,_cvm.CST, 1.0f);
                    String strDate = makeScaleFormat(date[i], dataformat);
                    if(x < (bounds.right - _cvm.GetTextLength(strDate) + (int)COMUtil.getPixel(2))) {
                        _cvm.drawString(gl, _cvm.CST, (int) x + 3, bounds.top + topPos, strDate);
                    }
                    td=date[i];
                    drawVertLine(gl, (int)x);
                    min = getMin(date[i]);
                }
//                term+=dgap;
                x+=xfactor;
            }
            if(date.length>0)
            {
                td=date[date.length-1];
                String strDate = makeScaleFormat(td,HHMMSS);
                if(!strDate.startsWith("88")&&!strDate.startsWith("99"))
                    _cvm.drawString(gl, _cvm.CST, bounds.right+_cvm.PADDING_RIGHT+(int)COMUtil.getPixel(1),bounds.top+topPos, strDate);
            }
        }
        if(_cvm.chartType != COMUtil.COMPARE_CHART && dataformat != 8)
            drawHour_day(gl, date);
    }
    public void setIndicatorView(View view) {
        indicatorview = view;
    }
    public void setBounds(float left, float top, float right, float bottom){
        /** right = width , bottom = height**/
        if(_cvm.isVerticalMode()) bounds = new RectF(left,top,(right-_cvm.Margin_R)/2,bottom-top);
        else bounds = new RectF(left,top,right,bottom);
        topPos = bounds.height()/2;
        if ( indicatorview!=null ) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) indicatorview.getLayoutParams();
            layoutParams.setMargins(0, (int) bottom, 0, (int) bottom);
            indicatorview.setLayoutParams(layoutParams);
        }
    }
    public void setOuterBounds(float left, float top, float right, float bottom){
        Outbounds = new RectF(left,top,right,bottom);
    }

    public RectF getBounds(){
        return bounds;
    }
    public void setProperties(String s, String p1,String p2){
        datakind = s;
        if(datakind==null||datakind.equals(""))datakind="자료일자";
        this.p1 = p1;
        this.p2 = p2;
    }
    //날짜타입을 구한다
    public void setDayType(int type){
        this.type = type;
    }
    public String getYear(String s, int format){
        if(s==null || s.equals(""))return "";
        switch(dataformat){
            case 0://YYYYMMDD
            case 2://YYYYMM
                if(s.length()<4)return s;
                if(format==YYYY)
                    return new String(s.substring(0,4));
                else
                    return new String(s.substring(2,4));
            case 1://YYMMDD
            case 3://YYMM                
                return new String(s.substring(0,2));
            default:
                return s;
        }
    }
    public String getMonth(String s){
        if(s==null || s.equals(""))return "";
        switch(dataformat){
            case 0://YYYYMMDD
            case 2://YYYYMM
                if(s.length()<6) {
                } else {
                    s = new String(s.substring(4,6));
                }
                break;
            case 1://YYMMDD
            case 3://YYMM
                s = new String(s.substring(2,4));
                break;
            case 4://MMDD
            case 20://MMDDHHMM
                s = new String(s.substring(0,2));
                break;
            default:
        }

        //2021.08.27 by lyk - kakaopay - 월,일이 한 자리 숫자일때 십의 자리에 0을 쓰지않는 규칙 적용 >>
        if(s.length() == 2 && s.charAt(0) == '0') {
            s = COMUtil.removeString(s, "0");
        }
        //2021.08.27 by lyk - kakaopay - 월,일이 한 자리 숫자일때 십의 자리에 0을 쓰지않는 규칙 적용 <<

        return s;
    }
    public String getDay(String s){
        if(s==null||s.length()<2)return "";
        switch(dataformat){
            case 0://YYYYMMDD
                if(s.length()<8) {
                } else {
                    s = new String(s.substring(6,8));
                }
                break;
            case 1://YYMMDD
                s = new String(s.substring(4,6));
                break;
            case 4://MMDD
            case 20://MMDDHHMM
                if(s.length()==7) {
                    s = new String(s.substring(1, 3));
                } else {
                    s = new String(s.substring(2,4));
                }
                break;
            case 5://DDHH
            case 6://DDHHMM
            case 7://DDHHMMSS
                if(s.length()==7) {
                    s = new String(s.substring(0, 1));
                } else {
                    s = new String(s.substring(0, 2));
                }
                break;
            default:
        }

        //2021.08.27 by lyk - kakaopay - 월,일이 한 자리 숫자일때 십의 자리에 0을 쓰지않는 규칙 적용 >>
        if(s.length() == 2 && s.charAt(0) == '0') {
            s = COMUtil.removeString(s, "0");
        }
        //2021.08.27 by lyk - kakaopay - 월,일이 한 자리 숫자일때 십의 자리에 0을 쓰지않는 규칙 적용 <<

        return s;
    }
    public String getHour(String s){
        if(s != null && s.length()%2 != 0)	//2013. 10. 23 데이터 없이 흰화면일 때 실시간 받으면 봉 그리기 처리
            s = "0"+s;

        if(s==null||s.length()<2)return "";
        switch(dataformat){
            case 5://DDHH
            case 6://DDHHMM
            case 7://DDHHMMSS
                if(s.length()<4) return "00"; //2015.03.06 by LYH >> 일주월분틱 빠르게 누르면 다운되는 현상수정.
                return new String(s.substring(2,4));
            case 8://HHMMSS
            case 9://HHMMSSNN
                return new String(s.substring(0,2));
            case MMDDHHMM://MMDDHHMM
                try {
                    if(s.length()<8) return "00";
                    return new String(s.substring(4,6));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

            default:
                return s;
        }
    }
    public String getMin(String s){
        if(s==null || s.equals(""))return "";
        if(s.length()%2 != 0)
            s = "0"+s;

        switch(dataformat){
            case 6://DDHHMM
            case 7://DDHHMMSS
//                if(s.length()<8)return "00";
                //2015.03.06 by LYH >> 일주월분틱 빠르게 누르면 다운되는 현상수정.
                if(s.length()>=6)
                    return new String(s.substring(4,6));
                else
                {
                    return "";
                }
                //2015.03.06 by LYH << 일주월분틱 빠르게 누르면 다운되는 현상수정.
            case 8://HHMMSS
            case 9://HHMMSSNN
//                if(s.length()<8)return "00";
                if (s.length() >= 4) {
                    return new String(s.substring(2, 4));
                }
                else {
                    return s;
                }
            case MMDDHHMM://MMDDHHMM
//                if(s.length()<8)return "00";
                try {
                    if(s!=null && s.length() >=8)
                        return new String(s.substring(6,8));
                    else
                        return "";
                } catch (Exception e) {
                    System.out.println(s + "####" +e.getMessage());
                    return "";
                }
            default:
                return s;
        }
    }
    public String getSec(String s){
        if(s==null || s.equals(""))return "";
        if(s.length()%2!=0)s="0"+s;
        switch(dataformat){
            case 7://DDHHMMSS:
                if(s.length()<8)return "00";
                else return new String(s.substring(6,8));
            case 8://HHMMSS
            case 9://HHMMSSNN
                if(s.length()<6)return "00";
                return new String(s.substring(4,6));
            case 20://HHMM
                return "00";
            default:
                return s;
        }
    }
    StringBuffer buf = new StringBuffer();
    public String makeScaleFormat(String d,int subcase){
        //2019. 06. 07 by hyh - 틱 00시 데이터 처리 >>
        if (dataformat == HHMMSS) {
            if (d.length() == 1) {
                d = "00000" + d;
            } else if (d.length() == 2) {
                d = "0000" + d;
            } else if (d.length() == 3) {
                d = "000" + d;
            }
        }
        //2019. 06. 07 by hyh - 틱 00시 데이터 처리 <<

        if(d==null)return "0";
        d = d.trim();
        try
        {
            if(d.length()<4 || (Integer.parseInt(d) == 0 && dataformat != HHMMSS))
                return "0";
        }catch(Exception e)
        {
            return "0";
        }
        if(d.length()%2!=0)d="0"+d;

        if(buf.length()>0) buf.delete(0, buf.length());
        switch(subcase){
            case YYYYMM:
                buf.append(getYear(d,YYYY));
                buf.append(p1);
                buf.append(getMonth(d));
                buf.append(p1);
                break;
            case YYMM:
                buf.append(getYear(d,YY));
                buf.append(p1);
                buf.append(getMonth(d));
                break;
            case YYMMDD:
                try {
                    buf.append(getYear(d,YY));
                    buf.append(p1);
                    buf.append(getMonth(d));
                    buf.append(p1);
                    buf.append(getDay(d));
                }catch(Exception e) {
                    System.out.println(e.getMessage());
                }
                break;
            case MMDD:
                buf.append(getMonth(d));
                buf.append(p1);
                buf.append(getDay(d));
                buf.append(p1);
                break;
            case YYYY:
                buf.append(getYear(d,YYYY));
                break;
            case YY:
                buf.append(getYear(d,YY));
                break;
            case MM:
                buf.append(getMonth(d));
                break;
            case DDHH:
                buf.append(getDay(d));
                buf.append(p1);
                buf.append(getHour(d));
                break;
            case DDHHMM:
                buf.append(getDay(d));
                buf.append(p1);
                buf.append(getHour(d));
                buf.append(p2);
                buf.append(getMin(d));
                break;
            case DDHHMMSS:
                buf.append(getDay(d));
                buf.append(".");
                buf.append(getHour(d));
                buf.append(p2);
                buf.append(getMin(d));
                buf.append(p2);
                buf.append(getSec(d));
                break;
            case HH:
                buf.append(getHour(d));
                break;
            case MMDDHHMM:
                buf.append(getMonth(d));
                buf.append(p1);
                buf.append(getDay(d));
                buf.append(p1);
                buf.append(getHour(d));
                buf.append(p2);
                buf.append(getMin(d));
                break;
            case HHMMSS:
                buf.append(getHour(d));
                buf.append(p2);
                buf.append(getMin(d));
                buf.append(p2);
                buf.append(getSec(d));
                break;
            case MMSS:
                buf.append(getMin(d));
                buf.append(p2);
                buf.append(getSec(d));
                break;
            case HHMM:
                buf.append(getHour(d));
                buf.append(p2);
                buf.append(getMin(d));
                break;
            case TEXT:
                buf.append(d);
                break;
        }
        return buf.toString();
    }

    public String makeScaleFormat_oneQ(String d,int subcase){
        //2019. 06. 07 by hyh - 틱 00시 데이터 처리 >>
        if (dataformat == HHMMSS) {
            if (d.length() == 1) {
                d = "00000" + d;
            } else if (d.length() == 2) {
                d = "0000" + d;
            } else if (d.length() == 3) {
                d = "000" + d;
            }
        }
        //2019. 06. 07 by hyh - 틱 00시 데이터 처리 <<

        if(d==null)return "0";
        d = d.trim();
        try
        {
            if(d.length()<4 || (Integer.parseInt(d) == 0 && dataformat != HHMMSS))
                return "0";
        }catch(Exception e)
        {
            return "0";
        }
        if(d.length()%2!=0)d="0"+d;

        if(buf.length()>0) buf.delete(0, buf.length());
        switch(subcase){
            case YYYYMM:
                buf.append(getYear(d,YYYY));
                buf.append(p1);
                buf.append(getMonth(d));
                break;
            case YYMM:
                buf.append(Integer.parseInt(getYear(d,YY))+"");
                buf.append(strYear);
                buf.append(strEmpty);
                buf.append(Integer.parseInt(getMonth(d))+"");
                buf.append(strMonth);
                break;
            case YYMMDD:
                try {
                    buf.append(getYear(d,YY));
                    buf.append(p1);
                    buf.append(getMonth(d));
                    buf.append(p1);
                    buf.append(getDay(d));
                }catch(Exception e) {
                    System.out.println(e.getMessage());
                }
                break;
            case MMDD:
                buf.append(Integer.parseInt(getMonth(d))+"");
                buf.append(strMonth);
                buf.append(strEmpty);
                buf.append(Integer.parseInt(getDay(d))+"");
                buf.append(strDay);
                break;
            case YYYY:
                buf.append(getYear(d,YYYY));
                break;
            case YY:
                buf.append(getYear(d,YY));
                break;
            case MM:
                buf.append(getMonth(d));
                break;
            case DD:
                if(d.length()>7)
                    buf.append(Integer.parseInt(d.substring(6,8))+"");
                buf.append("일 ("+COMUtil.getDateDay(d, "yyyyMMdd")+")");
                break;
            case DDHH:
                buf.append(getDay(d));
                buf.append(p1);
                buf.append(getHour(d));
                break;
            case DDHHMM:
            case DDHHMMSS:
                buf.append(getDay(d));
                buf.append("-");
                buf.append(getHour(d));
                buf.append(p2);
                buf.append(getMin(d));
                buf.append(p2);
                buf.append(getSec(d));
                break;
            case HH:
                buf.append(getHour(d));
                break;
            case MMDDHHMM:
                buf.append(getMonth(d));
                buf.append(p1);
                buf.append(getDay(d));
                buf.append(" ");
                buf.append(getHour(d));
                buf.append(p2);
                buf.append(getMin(d));
                break;
            case HHMMSS:
                buf.append(getHour(d));
                buf.append(p2);
                buf.append(getMin(d));
                buf.append(p2);
                buf.append(getSec(d));
                break;
            case MMSS:
                buf.append(getMin(d));
                buf.append(p2);
                buf.append(getSec(d));
                break;
            case HHMM:
                buf.append(Integer.parseInt(getHour(d))+"");
                buf.append(strHour);
                buf.append(strEmpty);
                buf.append(Integer.parseInt(getMin(d))+"");
                buf.append(strMinute);
                break;
            case TEXT:
                buf.append(d);
                break;
        }
        return buf.toString();
    }

    public void setScaleLineType(int style){
        if(style==0)lineDraw = true;
        else lineDraw = false;
    }
    public void dataChanged(){
        dataformat =_cdm.getDateFormat();
        setDayType(_cdm.getDateType());
    }
    private void drawVertLine_Section(Canvas gl, float x) {
        color = CoSys.vertLineColor_xscale;
        _cvm.drawLine(gl, x, Outbounds.top+ Outbounds.height()-bounds.height() - _cvm.EVENT_BADGE_H, x, Outbounds.top+ Outbounds.height()-bounds.height() - _cvm.EVENT_BADGE_H + COMUtil.getPixel(3), color, 1.0f);
    }

    private void drawVertLine(Canvas gl, float x){
        if (!COMUtil.isShowXScaleLine())
            return;
//        drawDotLine(gl, x, this.Outbounds.height()-_cvm.XSCALE_H*2);
        //skin에 따라 알파값 적용.
        //_cvm.setLineWidth_Fix(COMUtil.getPixel_W(1));
        _cvm.setLineWidth_Fix(COMUtil.getPixel_H(0.5f));
        if(_cvm.bIsLine2Chart || _cvm.bIsTodayLineChart || _cvm.bInvestorChart)
            return;
    	if(_cvm.bIsNewsChart || isSpecialDraw())
    	{
//    		int[] nLineCol = {255, 255, 255};
            int[] nLineCol = {200, 200, 200};
            //2021.05.21 by hanjun.Kim - kakaopay - 점선디자인적용
//    		_cvm.drawLine(gl, x, _cvm.Margin_T, x, Outbounds.top+ Outbounds.height()-bounds.height(), nLineCol, 0.3f); // 이전설정(21.05.21)
            _cvm.drawDashDotDotLine(gl, x, _cvm.Margin_T, x, Outbounds.top+ Outbounds.height()-bounds.height() - _cvm.EVENT_BADGE_H, nLineCol, 0.3f);

//    		_cvm.drawLine(gl, x, Outbounds.top+ Outbounds.height()-bounds.height()+COMUtil.getPixel(1), x, Outbounds.top+ Outbounds.height()-bounds.height()+COMUtil.getPixel(5), nLineCol, 0.3f);
    		return;
    	}
        color = CoSys.vertLineColor_xscale;
//        if(_cvm.getSkinType() != COMUtil.SKIN_WHITE) {
//            color = CoSys.vertLineColor_black;
//        }
        //2021.05.21 by hanjun.Kim - kakaopay - 점선디자인적용;
//        _cvm.drawLine(gl, x, _cvm.Margin_T, x, Outbounds.top+ Outbounds.height()-bounds.height(), color, 0.5f);
//        _cvm.drawLine(gl, x, _cvm.Margin_T, x, Outbounds.top+ Outbounds.height()-bounds.height(), color, 1.0f); // 이전설정(21.05.21)
        _cvm.drawDashDotDotLine(gl, x, _cvm.Margin_T, x, Outbounds.top+ Outbounds.height()-bounds.height() - _cvm.EVENT_BADGE_H, color, 1.0f);

//        if(_cvm.getSkinType() == COMUtil.SKIN_WHITE)
//        {
//            int[] nLineCol = {164, 170, 179};
//            _cvm.drawLine(gl, x, Outbounds.top+ Outbounds.height()-bounds.height()+COMUtil.getPixel(1), x, Outbounds.top+ Outbounds.height()-bounds.height()+COMUtil.getPixel(5), nLineCol, 0.5f);
//        }
//        else
//        {
//            int[] nLineCol = {79, 79, 79};
//            _cvm.drawLine(gl, x, Outbounds.top+ Outbounds.height()-bounds.height()+COMUtil.getPixel(1), x, Outbounds.top+ Outbounds.height()-bounds.height()+COMUtil.getPixel(5), nLineCol, 0.5f);
//        }
    }
    private int[] color={70,70,70};
    //    private void drawDotLine(Canvas gl,float  x, int height){
//    	int totLen = (height-_cvm.Margin_T)*4/8 +4;
//    	if(totLen<0) return;//-값이 들어오는 경우 처리 
//    	float[] positions = new float[totLen];
//    	int nIndex = 0;	
//        for(int i=height;i>_cvm.Margin_T;i-=8){
//            //_cvm.drawLine(gl, x, i, x, i+3, color, 0.5f);
//        	positions[nIndex++] = x; 
//        	positions[nIndex++] = i;
//        	positions[nIndex++] = x; 
//        	positions[nIndex++] = i+3;
//        }
//        if(nIndex>0)
//        	_cvm.drawLines(gl, positions, color , 0.5f);
//    }    
    public int getXToDate(float x) {
        int index = _cvm.getIndex();
//    	float xfactor = _cvm.getDataWidth();
        //2020.07.06 by LYH >> 캔들볼륨 >>
        if(_cvm.m_arrArea.size()>0)
        {
            for(int i=0; i<_cvm.m_arrArea.size(); i++)
            {
                AREA area = _cvm.getArea(i);
                if(i==0 && x >= 0 && x < area.getLeft())
                    return 0;
                if(x>=area.getLeft() && x<=area.getRight_Tot())
                    return index+i;
                if(i==_cvm.m_arrArea.size()-1)
                    return index+i;
            }
            return -1;
        }
        //2020.07.06 by LYH >> 캔들볼륨 <<

        //2015.06.25 by lyk - Kagi 차트 데이터 사용
        if(!isSpecialDraw()) {
            xfactor = _cvm.getDataWidth();
        }
        //2015.06.25 by lyk - Kagi 차트 데이터 사용 end

        int tmp = (int)(index+((x-bounds.left)/xfactor));
        int num = 0;
        //2015.06.25 by lyk - Kagi 차트 데이터 사용
        if(isSpecialDraw()) {
            num = view;
        } else {
            //2021.09.02 by JJH >> 일목균형표 선행스팬영역에서 스크롤이 가능하도록 변경 start
//            num = _cdm.getCount();
            num = _cdm.getCount() + _cvm.futureMargin;
            //2021.09.02 by JJH >> 일목균형표 선행스팬영역에서 스크롤이 가능하도록 변경 end
        }
        if(tmp<0) tmp = 0;
        else if(tmp>num-1) tmp=num-1;

        return tmp;
    }
    //2020.07.06 by LYH >> 캔들볼륨 >>
    public int getXToDate_ByViewNum(float x) {
        int index = _cvm.getIndex();
//    	float xfactor = _cvm.getDataWidth();

        //2015.06.25 by lyk - Kagi 차트 데이터 사용
        if(!isSpecialDraw()) {
            xfactor = _cvm.getDataWidth();
        }
        //2015.06.25 by lyk - Kagi 차트 데이터 사용 end

        int tmp = (int)(index+((x-bounds.left)/xfactor));
        int num = 0;
        //2015.06.25 by lyk - Kagi 차트 데이터 사용
        if(isSpecialDraw()) {
            num = view;
        } else {
            //2021.09.02 by JJH >> 일목균형표 선행스팬영역에서 스크롤이 가능하도록 변경 start
//            num = _cdm.getCount();
            num = _cdm.getCount() + _cvm.futureMargin;
            //2021.09.02 by JJH >> 일목균형표 선행스팬영역에서 스크롤이 가능하도록 변경 end

        }
        if(tmp<0) tmp = 0;
        else if(tmp>num-1) tmp=num-1;

        return tmp;
    }
    //2020.07.06 by LYH >> 캔들볼륨 <<
    public int getXToDateWithCount(float x, int num) {
//    	int index = _cvm.getIndex();
//    	float xfactor = _cvm.getXFactorWithCount(bounds.width(), num);
        //2015.06.25 by lyk - Kagi 차트 데이터 사용
        if(!isSpecialDraw() && !_cvm.getStandGraphName().equals("역시계곡선")) {
            xfactor = ((float)(getBounds().width())/(float)(num+1));
        }
        //2015.06.25 by lyk - Kagi 차트 데이터 사용 end
        int tmp = (int)(index+((x-bounds.left)/xfactor));

//    	 System.out.println("Debug_getXToDate_DataWidth:"+xfactor);
//    	int num = _cdm.getCount();
//    	if(tmp<0) tmp = 0;
//    	else if(tmp>num-1) tmp=num-1;

        return tmp;
    }
    public float getDateToX(int nIndex) {
        int nStart = _cvm.getIndex();
        //2020.07.06 by LYH >> 캔들볼륨 >>
        if(_cvm.m_arrArea.size()>0)
        {
            int nIndex1 = nIndex-nStart;
            if(nIndex1<0)
                nIndex1 = 0;
            AREA area = _cvm.getArea(nIndex1);
            if(area != null)
                return area.getCenter();
        }
        //2020.07.06 by LYH >> 캔들볼륨 <<
        return (((nIndex-nStart) * xfactor)+bounds.left+xw);
    }

    public float priceToX(double price) {
        double p, min, max;
//        if(log){
//            p = (Math.log(price)*1000);
//            min = (Math.log(mm_data[0])*1000);
//            max = (Math.log(mm_data[1])*1000);
//        }else{
        p = price;
        min = min_data;
        max = max_data;
//        }
        double t1 = p-min;
        double t2 = max-min;
        double t= bounds.width()*(t1/t2);

        return (float)t;
    }

    public boolean isSpecialDraw()
    {
        if(_cvm.getStandGraphName().equals("Kagi") || _cvm.getStandGraphName().equals("스윙") || _cvm.getStandGraphName().equals("렌코") || _cvm.getStandGraphName().equals("삼선전환도") || _cvm.getStandGraphName().equals("PnF"))
            return true;

        return false;
    }

    public int calcMonth(String strtDate, String endDate)
    {
        if(endDate==null || strtDate==null)
            return 0;
        int nMonthPeriod = 0;
        if(strtDate.length()>=6 && endDate.length()>=6)
        {
            int strtYear = Integer.parseInt(strtDate.substring(0,4));
            int strtMonth = Integer.parseInt(strtDate.substring(4,6));

            int endYear = Integer.parseInt(endDate.substring(0,4));
            int endMonth = Integer.parseInt(endDate.substring(4,6));

            nMonthPeriod = (endYear - strtYear)* 12 + (endMonth - strtMonth) + 1;
        }
        return nMonthPeriod;
    }

    //2020.05.18 by LYH >> XScale 고정 개수 Start
    public void drawFix(Canvas gl, int dLen)
    {
        float x;
        float xPos;
        boolean bDrawXScale = true;
        //2020.08.14 by LYH >> 분데이터 날짜 표기 Start
        boolean bDayDraw = false;

        //2021.09.29 by lyk - kakaopay - date null 값 방어코드 작성 >>
        try {
            if (type == 4 && _cdm.getTerm() >= 10 && dLen > 1 && date[0].length() >= 7) {
                String strStart = makeScaleFormat(date[0], MMDD);
                String strEnd = makeScaleFormat(date[dLen - 1], MMDD);
                if (!strStart.equals(strEnd)) {
                    bDayDraw = true;
                }
            }
        } catch (Exception e) {
            return;
        }
        //2021.09.29 by lyk - kakaopay - date null 값 방어코드 작성 <<

        //2020.08.14 by LYH >> 분데이터 날짜 표기 End
        for(int i=0; i<dLen; i++) {
            x = this.getDateToX(_cvm.getIndex()+i);
            if(x>=bounds.left && (i==0 || (dLen<12 && i==dLen/2) || (dLen>=12 && (i==dLen/3 || i==(dLen/3)*2)) || i==dLen-1)) {
                String strDate;// = makeScaleFormat(date[i],MMDD);
                if(date[i] != null) {
                    if (type == 3)//월
                        strDate = makeScaleFormat(date[i], YYMM);
                    else if (type == 8)//년
                        strDate = makeScaleFormat(date[i], YYYY);
                    else if (type == 5 || type == 7) //틱, 초
                    {
                        if (dLen > 100 && date[i].length() >= 8)
                            strDate = makeScaleFormat(date[i], DDHHMMSS);
                        else
                            strDate = makeScaleFormat(date[i], HHMMSS);
                    } else if (type == 4)  //분
                    {
                        if (!_cvm.bIsTodayLineChart && !_cvm.bIsLine2Chart && dLen > 100 && date[i].length() >= 8)
                            strDate = makeScaleFormat(date[i], DDHHMM);
                            //else if((dLen>30 && _cdm.getTerm()>=30) || (dLen>60 && _cdm.getTerm()>=10))
                        else if ((dLen > 30 && _cdm.getTerm() >= 30) || (dLen > 60 && _cdm.getTerm() >= 10) || bDayDraw)   //2020.08.14 by LYH >> 분데이터 날짜 표기
                            strDate = makeScaleFormat(date[i], DDHHMM);
                        else
                            strDate = makeScaleFormat(date[i], HHMM);
                    } else {
                        if (dLen > 100)
                            strDate = makeScaleFormat(date[i], YYMMDD);
                        else
                            strDate = makeScaleFormat(date[i], MMDD);
                    }
                }else
                {
                    strDate = "0";
                }

                float strWidth = _cvm.GetTextLength(strDate);

                if(i==0) {
                    //2023.02.06 by SJW - 내비게이션 바(스와이프제스처) 설정 시 왼쪽 하단 잘려 보이는 현상 수정 >>
//                    xPos = bounds.left + COMUtil.getPixel_W(2);
                    if (COMUtil.apiView != null) { //2024.01.10 by SJW - Crashlytics(2.26.0) 수정
                        Configuration config = COMUtil.apiView.getContext().getResources().getConfiguration();
                        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                            xPos = bounds.left + COMUtil.getPixel_W(10);
                        } else {
                            xPos = bounds.left + COMUtil.getPixel_W(2);
                        }
                        //2023.02.06 by SJW - 내비게이션 바(스와이프제스처) 설정 시 왼쪽 하단 잘려 보이는 현상 수정 <<
                        _cvm.drawString(gl, _cvm.CST, (int) xPos, bounds.top + topPos, strDate, 0.48f);
                    }
                }
                else if(i==dLen-1) {
                    //2024.01.10 by SJW - 일목균형표 Xscale 위치 변경 >>
//                    xPos = bounds.right - strWidth - COMUtil.getPixel_W(2);
                    if(_cvm.futureMargin != 0) {
                        xPos = x-strWidth/2;
                        float xLastPos = bounds.right - strWidth - COMUtil.getPixel_W(2);
                        if(xPos<bounds.left + COMUtil.getPixel_W(2)+strWidth)
                            xPos = bounds.left + COMUtil.getPixel_W(5)+strWidth;
                        if(xLastPos-(strWidth+xfactor)/2<xPos)
                            xPos = xLastPos;
                    } else {
                        xPos = bounds.right - strWidth - COMUtil.getPixel_W(2);
                    }
                    //2024.01.10 by SJW - 일목균형표 Xscale 위치 변경 <<
                    _cvm.drawString(gl, _cvm.CST, (int)xPos,bounds.top+topPos, strDate, 0.48f);
                }
                else{
                    //2021.11.24 by lky - kakaopay - 날짜 위치를 봉 한가운데로 조정 >>
//                    xPos = x-(strWidth+xfactor)/2;
                    xPos = x-(strWidth)/2;
                    //2021.11.24 by lky - kakaopay - 날짜 위치를 봉 한가운데로 조정 <<
                    if(dLen <4)
                        xPos = x-strWidth/2;
                    if(xPos < bounds.left+ COMUtil.getPixel_W(2)+strWidth)
                        bDrawXScale = false;
                    if(bDrawXScale)
                    {
                        //2021.11.24 by lky - kakaopay - 날짜 위치를 봉 한가운데로 조정 >>
//                        drawVertLine(gl, x - xfactor / 2);
//                        drawVertLine_Section(gl, x - xfactor / 2);
                        drawVertLine(gl, x);
                        drawVertLine_Section(gl, x);
                        //2021.11.24 by lky - kakaopay - 날짜 위치를 봉 한가운데로 조정 <<
                        _cvm.drawString(gl, _cvm.CST, (int) xPos, bounds.top + topPos, strDate, 0.48f);
                    }
                }
            }
        }

        if((type == 4 || type == 5 || type == 7) && _cvm.chartType != COMUtil.COMPARE_CHART && !_cvm.bIsLineFillChart && !_cvm.bIsLineChart && !_cvm.bIsLineChart && !_cvm.bInvestorChart)
            drawHour_day(gl, date);
    }
    //2020.05.18 by LYH >> XScale 고정 개수 End
    //2020.07.06 by LYH >> 캔들볼륨 >>
    public void calcVolumeScale()
    {
        _cvm.m_arrArea.clear();
        double[] volData = _cdm.getSubPacketData("기본거래량");
        int index = _cvm.getIndex();
        view = _cvm.getViewNum();
        int nEnd = index+view;
        if(nEnd > _cdm.getCount())
            nEnd = _cdm.getCount();
        double dTotVolume = 0;
        for(int i = index; i<nEnd; i++)
        {
            dTotVolume += volData[i];
        }

        if(dTotVolume>0)
        {
            double dLeft = bounds.left;
            double dCenter = 0;
            double dRight = 0;
            double dWidth = 0;
            double dDrawWidth = bounds.width();
            int nFutureCount = _cvm.futureMargin - (_cdm.getCount()-index-view);
            if(_cvm.futureMargin != 0)
            {
                if(nFutureCount>=0) {
                    dDrawWidth = dDrawWidth - (dDrawWidth / (view + _cvm.futureMargin)) * nFutureCount;
                    if (nFutureCount != _cvm.futureMargin) {
                        dTotVolume = 0;
                        nEnd += _cvm.futureMargin - nFutureCount;
                        for (int i = index; i < nEnd; i++) {
                            dTotVolume += volData[i];
                        }
                    }
                }
                else {
                    dTotVolume = 0;
                    nEnd += _cvm.futureMargin;
                    for (int i = index; i < nEnd; i++) {
                        dTotVolume += volData[i];
                    }
                }
            }
            for(int i = index; i<nEnd; i++)
            {
                dWidth = (dDrawWidth*volData[i])/dTotVolume;
                dRight = dLeft+dWidth;
                if(i != _cdm.getCount()-1) {
                    if (dWidth >= COMUtil.getPixel(5))
                        dRight -= COMUtil.getPixel(2);
                    else if (dWidth >= COMUtil.getPixel(3))
                        dRight -= COMUtil.getPixel(1);
                }

                dCenter = (dLeft+dRight)/2;

                _cvm.m_arrArea.add(new AREA((float)dLeft, (float)dCenter, (float)dRight, (float)dWidth));
                dLeft += dWidth;
            }
        }
    }
    //2020.07.06 by LYH >> 캔들볼륨 <<

    //2021.05.21 by HJW - 프리애프터 적용 >>
    public void drawPreAfterMarketArea(Canvas gl, String[] date) {
        if (!_cvm.bIsPreAfterAreaVisible) {
            return;
        }

        int dateLength = date.length;
        if (dateLength <= 0) {
            return;
        }

        String strOpenTime = _cvm.strRegularOpenTime;
        String strCloseTime = _cvm.strRegularCloseTime;
        if (strOpenTime.length() < 6 || strCloseTime.length() < 6) {
            return;
        }

        int nOpenTime;
        int nCloseTime;
        boolean bIsRegularHour;

        try {
            strOpenTime = strOpenTime.substring(0, 6);
            strCloseTime = strCloseTime.substring(0, 6);

            nOpenTime = Integer.parseInt(strOpenTime);
            nCloseTime = Integer.parseInt(strCloseTime);

            int nCurrTime = getTime(date[0]);
            bIsRegularHour = (nOpenTime <= nCurrTime && nCurrTime < nCloseTime);
        } catch (Exception ignored) {
            return;
        }

        int[] areaColor = new int[]{0, 0, 0};
        if(COMUtil.currentTheme == COMUtil.SKIN_BLACK) {
            areaColor = new int[]{255, 255, 255};
        }
        float rectTop = _cvm.Margin_T;
        float rectHeight = Outbounds.top + Outbounds.height() - bounds.height() - _cvm.EVENT_BADGE_H;
        float markYp = bounds.top + topPos;

        float xp = getBounds().left;
        float startXp = xp;
        //2023.03.15 by SJW - 애프터마켓 추가 >>
//        for (int i = 1; i < dateLength; i++) {
//            int nCurrTime = getTime(date[i]);
//
//            if (nOpenTime <= nCurrTime && nCurrTime <= nCloseTime) {
//                if (!bIsRegularHour) {
//                    //프리애프터장 -> 정규장
//                    float areaWidth = xp - startXp + xfactor;
//                    _cvm.drawFillRect(gl, startXp, rectTop, areaWidth, rectHeight, areaColor, 0.04f);
//
////                    int markXp = (int) (xp + xfactor);
////                    _cvm.drawString(gl, _cvm.CST, markXp, markYp, "☀"); //☼☀
//
//                    startXp = -1;
//                }
//
//                bIsRegularHour = true;
//            }
//            else {
//                if (bIsRegularHour) {
//                    //정규장 -> 프리애프터장
//                    startXp = xp + xfactor;
//
////                    int markXp = (int) (xp + xfactor);
////                    _cvm.drawString(gl, _cvm.CST, markXp, markYp, "☾"); //☾☽
//                }
//
//                bIsRegularHour = false;
//            }
//
//            xp += xfactor;
//        }
//
//        //우측 처리
//        int nLastTime = getTime(date[dateLength - 1]);
//        bIsRegularHour = (nOpenTime <= nLastTime && nLastTime <= nCloseTime);
//
//        if (!bIsRegularHour && 0 < startXp) {
//            float areaWidth = xp - startXp + xfactor;
//            _cvm.drawFillRect(gl, startXp, rectTop, areaWidth, rectHeight, areaColor, 0.04f);
//        }

        double[] sessionIds = _cdm.getSubPacketData("sessionIds");
        for (int i = 0; i < dateLength; i++) {
            if(index < 0 || sessionIds.length <= (index + i)) break;
            double sessionId = sessionIds[index+i];

            if (sessionId == 1) { // 1: 프리마켓
            //2023.03.23 by SJW - 프리, 애프터 색상 변경 >>
//                _cvm.drawFillRect(gl, xp, rectTop, xfactor, rectHeight, areaColor, 0.02f);
                _cvm.drawFillRect(gl, xp, rectTop, xfactor, rectHeight, CoSys.PREMARKET_AREA_COLOR, 0.08f);
            //2023.03.23 by SJW - 프리, 애프터 색상 변경 <<
            } else if (sessionId == 2) { // 2: 애프터마켓
            //2023.03.23 by SJW - 프리, 애프터 색상 변경 >>
//                _cvm.drawFillRect(gl, xp, rectTop, xfactor, rectHeight, areaColor, 0.04f);
                _cvm.drawFillRect(gl, xp, rectTop, xfactor, rectHeight, CoSys.AFTERMARKET_AREA_COLOR, 0.06f);
            //2023.03.23 by SJW - 프리, 애프터 색상 변경 <<
            //2024.01.31 by SJW - 데이마켓 추가 >>
            } else if (sessionId == 4) { // 4: 데이마켓
                _cvm.drawFillRect(gl, xp, rectTop, xfactor, rectHeight, CoSys.DAYMARKET_AREA_COLOR, 0.06f);
            }
            //2024.01.31 by SJW - 데이마켓 추가 <<
            xp += xfactor;
        }
        //2023.03.15 by SJW - 애프터마켓 추가 <<
    }

    //MMHHSS
    private int getTime(String date) {
        int nTime = 0;

        String strHour = getHour(date);
        String strMin = getMin(date);
        String strSec = getSec(date);

        try {
            int nHour = Integer.parseInt(strHour);
            int nMin = Integer.parseInt(strMin);
            int nSec = Integer.parseInt(strSec);

            nTime = nHour * 10000 + nMin * 100 + nSec;
        } catch (Exception ignored) {
        }

        return nTime;
    }
    //2021.05.21 by HJW - 프리애프터 적용 <<
}