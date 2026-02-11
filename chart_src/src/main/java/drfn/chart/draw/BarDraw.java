package drfn.chart.draw;
import android.app.AlertDialog;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.RectF;
import android.view.Gravity;

import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;
import drfn.chart.scale.AREA;
import drfn.chart.util.COMUtil;
import drfn.chart.util.ChartUtil;
import drfn.chart.util.CoSys;
import drfn.chart.util.MinMax;

public class BarDraw extends DrawTool{
    int type=0;
    double[] price;
    double[] sprice;
    //2012. 8. 30 고가 추가 : C16
    double[] hprice;

    public final static int OSC_UP = 1;
    public final static int OSC_DOWN = 2;
    public BarDraw(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm, cdm);
    }
    public void setIndex(int idx){
        type = idx;
        //0: 수직식, 1: 수평식, 2: 상하식, 3: 빗형식 5: 도트형
    }
    public void draw(Canvas gl, double data){//기준가 없이 그리는 바
    }
    public void drawDefault(Canvas gl, double[] data){
    }
    //대기매물과 같이 수평식으로 바를 그리는 경우
    //2013.04.01 by LYH >> 대기매물 개선
    //2021.11.10 by LYH - 매물대 차트 타이틀 봉 위에 보이도록 수정 >>
//    public void drawVolumeForSale(Canvas gl, double[] stand){
    public void drawVolumeForSale(Canvas gl, double[] stand){
        drawVolumeForSale(gl, stand, true);
    }
    public void drawVolumeForSale(Canvas gl, double[] stand, boolean bDrawRect){
        //2021.11.10 by LYH - 매물대 차트 타이틀 봉 위에 보이도록 수정 <<
        //2013.04.01 by LYH >> 대기매물 개선
        double[] closeData = _cdm.getSubPacketData("종가");
        //2024.03.12 by SJW - 2.27.5 crash 오류 수정 >>
//        if(closeData.length<1)
        if(closeData == null || closeData.length<1)
        //2024.03.12 by SJW - 2.27.5 crash 오류 수정 <<
            return;
        int nTotCnt = _cdm.getCount();
        int index=_cvm.getIndex();        //화면에 그리기 시작할 인덱스
        //2024.01.04 by sJW - 크래시로그 수정 >>
//        int num=_cvm.getViewNum()+_cvm.futureMargin;        //화면에 그릴 데이터 수
        int num = Math.min(_cvm.getViewNum() + _cvm.futureMargin, closeData.length - index);
        //2024.01.04 by sJW - 크래시로그 수정 <<
        if(index+num>nTotCnt)
            num = nTotCnt-index;
        double minData = MinMax.getRangeMin(closeData, index+num, num);
        double maxData = MinMax.getRangeMax(closeData, index+num, num);
        //2013.04.01 by LYH <<

        float x = getBounds().left;
        //2013.04.01 by LYH >> 대기매물 개선
        //int y=max_view+5;
        //float h = yfactor;
        float y = calcy(minData);
        float h = (calcy(minData)-calcy(maxData))/stand.length;
        //2013.04.01 by LYH <<

        float w,sw;
        double[] stand_vol=_cdm.getSubPacketData("매물대거래량");
//        double[] stand_vol=_cdm.getSubPacketData("대기매거래량");
        if(stand_vol==null) return;
        int format_org = _cdm.getDataFormat_org("기본거래량");
        int sLen = stand.length;
        int nMaxVolIndex = -1;
        int nCurVolIndex = -1;
        double dMaxVol = -1;
        if(index+num-1>0)
        {
            //2024.01.04 by sJW - 크래시로그 수정 >>
//            float yPrice = calcy(closeData[index+num-1]);
            float yPrice = (num > 0) ? calcy(closeData[index + num - 1]) : 0;
            //2024.01.04 by sJW - 크래시로그 수정 <<
            for(int i=0;i<sLen;i++){
                if(dMaxVol<stand[i])
                {
                    dMaxVol = stand[i];
                    nMaxVolIndex = i;
                }
                float term=(h*(i+1));
                if((int)(yPrice*100)>=(int)((y-term)*100) && nCurVolIndex<0)
                    nCurVolIndex = i;
            }
        }
        for(int i=0;i<sLen;i++){
            double vol2 = Math.round(((stand[i])* 100.0)) / 100.0;
            String strM = "";
            String strT = "";
            if(stand_vol[i]!=0.0) {
                if(stand_vol[i]>0 && stand_vol[i]<1) {
//                    stand_vol[i] = stand_vol[i]*1000;

                    strT = ChartUtil.getFormatedData(stand_vol[i],11);
                } else {
//                    strM = ChartUtil.getFormatedData(stand_vol[i],11)+"M";
//                    strT = ChartUtil.getFormatedData(stand_vol[i],11)+"T";
                    //2014.09.03 by LYK - 주,월 매물대는 천단위 처리
                    if(_cdm.codeItem.strDataType.equals("3") || _cdm.codeItem.strDataType.equals("4"))
                        strT = ChartUtil.getFormatedData(stand_vol[i],11)+"K";
                        //2014.09.03 by LYK - 년 매물대는 백만단위 처리
                    else if(_cdm.codeItem.strDataType.equals("5"))
                        strT = ChartUtil.getFormatedData(stand_vol[i],11)+"M";
                    else
                        strT = ChartUtil.getFormatedData(stand_vol[i],11);
                }

            }
            String strVol2M = "";
            String strVol2T = "";
//            if(!strM.equals("")) {
//                strVol2M = vol2+"%[" + strM+"]";
//            }
//            if(!strT.equals("")) {
//                strVol2T = vol2+"%[" + strT+"]";
//            }
            if(!strM.equals("")) {
                strVol2M = vol2+"%";
            }
            if(!strT.equals("")) {
                strVol2T = vol2+"%";
            }

            String vol=(format_org==12)?strVol2M:strVol2T;
            w=(float)(xfactor*stand[i]);
            sw = _cvm.GetTextLength(vol);
            //float term=(yfactor*(i+1));
            float term=(h*(i+1));
            //2012. 8. 9 대기매물 채움색 변경 : I90
            //2012. 8. 13 매물대분석의 영역이 Y축 기준선 밖으로 삐져나가는 현상 -> width 조절함.   : I96

            //2012.10.25 by LYH >> 대기매물 색상 변경.
//            _cvm.drawFillRect(gl, x, (float)(y-term), (x+Math.abs(w)-(int)COMUtil.getPixel(8)), (h), CoSys.CHART_COLORS[1], 0.5f);
//            _cvm.drawRect(gl, x, (float)(y-term), (x+Math.abs(w)-(int)COMUtil.getPixel(8)), (h), CoSys.DKGRAY);
//            int[] fillColor = {245, 225, 245};
//            int[] lineColor = {225, 196, 225};
//            int[] textColor = {221, 121, 221};
//            _cvm.drawFillRect(gl, x, (float)(y-term), (x+Math.abs(w)-(int)COMUtil.getPixel(8)), (h), fillColor, 0.5f);
//            _cvm.drawRect(gl, x, (float)(y-term), (x+Math.abs(w)-(int)COMUtil.getPixel(8)), (h), lineColor);
            int[] lineColor = upColor;
            if(nCurVolIndex==i)
            {
                lineColor = sameColor;
            }
            else if(nMaxVolIndex == i)
            {
                lineColor = downColor;
            }

            float[] corners = new float[]{
                    0, 0,
                    COMUtil.getPixel(100.0f), COMUtil.getPixel(100.0f),
                    COMUtil.getPixel(100.0f), COMUtil.getPixel(100.0f),
                    0,0
            };
            //2021.05.21 by hanjun.Kim - kakaopay - 매물대 색상변경 >>
            lineColor = standVolColor;

            //_cvm.drawFillRect(gl, x, (float)(y-term), (x+Math.abs(w)-(int)COMUtil.getPixel(4)), (h), lineColor, 0.3f);
            //_cvm.drawRect(gl, x, (float)(y-term), (x+Math.abs(w)-(int)COMUtil.getPixel(4)), (h), lineColor);
//            if(x<(x+Math.abs(w)-(int)COMUtil.getPixel(4)))
//            if(Math.abs(w)>0)
            if(Math.abs(w)>0 && bDrawRect)  //2021.11.10 by LYH - 매물대 차트 타이틀 봉 위에 보이도록 수정
                _cvm.drawFillRect(gl, x, (float)(y-term), Math.abs(w), (h-COMUtil.getPixel(1)), lineColor, 0.1f);
//                _cvm.drawFillRoundedRect(gl, x, (float)(y-term), Math.abs(w), (h-COMUtil.getPixel(1)), lineColor, 0.5f, corners);

            //2012. 8. 9 대기매물 텍스트 색상 변경 : I91
            //2013. 9. 5 매물대설정 값 문자열 보이기 여부 처리>>
            if(bStandScaleLabelShow)
            {
                int xpos = 0;
                //2021.05.21 by hanjun.Kim - kakaopay - 매물대 색상변경 >>
                lineColor = standVolTextColor;
//                lineColor = new int[] {136, 136, 136};
//                if(nCurVolIndex==i)
//                {
//                    lineColor = new int[] {255, 255, 255};
//                }
//                else if(nMaxVolIndex == i)
//                {
//                    lineColor = new int[] {255, 255, 255};
//                }
                if (w-sw < x) {
                    //_cvm.drawString(gl, CoSys.WHITE, (int)x,(int)(y-term+12), vol);
                    //_cvm.drawString(gl, textColor, (int)x,(int)(y-term+12), vol);
                    xpos = (int)x - (int)COMUtil.getPixel_W(6);
                    if(xpos < 0)
                        xpos = (int)COMUtil.getPixel_W(2);
                    _cvm.drawString(gl, lineColor, xpos,(int)(y-term-COMUtil.getPixel(2) + (term/2)/(i+1)), vol);
                } else {
                    xpos = (int)(w-sw) - (int)COMUtil.getPixel_W(6);
                    if(xpos < 0)
                        xpos = (int)COMUtil.getPixel_W(2);
                    //_cvm.drawString(gl, CoSys.WHITE, (int)(w-sw),(int)(y-term+12), vol);
//                    _cvm.drawString(gl, lineColor, xpos,(int)(y-term-COMUtil.getPixel(2) + (term/2)/(i+1)), vol);
                    _cvm.drawStringWithSizeFont(gl, lineColor, xpos,(int)(y-term-COMUtil.getPixel(2) + (term/2)/(i+1)), COMUtil.nFontSize_paint, vol,COMUtil.typefaceBold);
                    //_cvm.drawString(gl, textColor, (int)(w-sw),(int)(y-term+12), vol);
                }
            }
            //2013. 9. 5 매물대설정 값 문자열 보이기 여부 처리>>
            //2012.10.25 by LYH <<
        }
    }
    public void draw(Canvas gl, double[] data){
//        upColor = CoSys.CHART_COLORS[0];
//        upColor2 = CoSys.CHART_COLORS[0];
//        downColor = CoSys.CHART_COLORS[1];
//        downColor2 = CoSys.CHART_COLORS[1];
//        sameColor = CoSys.CHART_COLORS[2];

        //2019. 03. 12 by hyh - 투자자 업종 가로 바차트 추가 >>
        if (!_cvm.bIsUpdownChart && data == null) {
            return;
        }

        if(_cvm.getAssetType() == _cvm.ASSET_UPDOWN_BAR) {
            drawUpRectWithText(gl, data,"");
            return;
        }

        //2019.08.12 by LYH >> 업종 그리드 바차트 추가 Start
        if(this.getDrawType2() == 8) { //투자자 봉으로 그리기 추가 2015.05.19 by lyk
            drawGridBarGraph(gl, data);
            return;
        }
        //2019.08.12 by LYH >> 업종 그리드 바차트 추가 End
        if(this.getDrawType2() == 9) { //투자자 봉으로 그리기 추가 2015.05.19 by lyk
            drawGridFutOptGraph(gl);
            return;
        }
        if (_cvm.bIsUpdownChart) {
            if (this.getDrawType2() == 3) {
                drawUpDownRect(gl, data);
                return;
            }
            else {
                drawBongGraph(gl);
            }
            return;
        }
        //2019. 03. 12 by hyh - 투자자 업종 가로 바차트 추가 <<
        //2020.04.20 roundedBar3 차트 추가 - hjw >>
        if(_cvm.m_nChartType != 0) {
            if (_cvm.m_nChartType == ChartViewModel.CHART_LINE_ROUNDED_BAR) {
                drawBongGraph2(gl);
            } else if (_cvm.m_nChartType == ChartViewModel.CHART_THREE_ROUNDED_BAR) {
                drawBongGraph3(gl);
            } else if (_cvm.m_nChartType == ChartViewModel.CHART_HORIZONTAL_ROUNDED_BAR) {
                drawBongGraph4(gl);
            }
            return;
        }
        //2020.04.20 roundedBar3 차트 추가 - hjw <<

        //투자자 커스텀 상하식
        if(_cvm.bIsInnerText) {
            drawMemberBar(gl, data);
            return;
        }

        this.data = data;
        sprice = _cdm.getSubPacketData("시가");
        //2012. 8. 30 고가 추가 : C16
        hprice = _cdm.getSubPacketData("고가");
        price = _cdm.getSubPacketData("종가");
        //if(price==null) return;

        float x=getBounds().left+xw;
        float sp = max_view;
        //int[] cColor=null;
        int startIndex = _cvm.getIndex();
        int nTotCnt = _cdm.getCount();
        if(startIndex>=nTotCnt)
        {
//            startIndex = 0;   //2024.01.03 by LYH - 일목균형표 줌인, 줌아웃, 스크롤 개선
            return; //2024.01.03 by LYH - 일목균형표 줌인, 줌아웃, 스크롤 개선
        }
        //2024.01.03 by LYH - 일목균형표 줌인, 줌아웃, 스크롤 개선 >>
        if(startIndex<0)
        {
            startIndex = 0;
        }
        //2024.01.03 by LYH - 일목균형표 줌인, 줌아웃, 스크롤 개선 <<
        //20120621 by LYH >> 일목균형 스크롤 처리
        //int dataLen = startIndex + _cvm.getViewNum();
        int dataLen = startIndex + _cvm.getViewNum() + _cvm.futureMargin;
        //20120621 by LYH <<
        if(dataLen>data.length)
            dataLen= data.length;
//        float fWidth = (xw*2);
        float fWidth = (xw * 0.65f); //2021.06.07 by lyk - kakaopay - 바 그래프 넓이 수정 >>
        if(fWidth<1) {
            //2020. 06. 08 by hyh - 우측으로 쏠린 머리, 꼬리 위치 가운데로 조정. Bar >>
            fWidth -= COMUtil.getPixel(1);
            //fWidth = 1;
            //2020. 06. 08 by hyh - 우측으로 쏠린 머리, 꼬리 위치 가운데로 조정. Bar <<
        }

//        if(xw<1&&getDrawType2()==0){
//            for(int i=startIndex;i<dataLen;i++){
//            	float y = calcy(data[i]);
//                y=(y>sp)?sp:y;
//                switch(_cvm.getVolDrawType()){ 
//                    case 0:
//                        cColor=upColor;
//                    break; 
//                    case 1:
//                        if(price==null||i==0){
//                        	 cColor=upColor;
//                        }
//                        else{
//                            if(price[i-1]>price[i]) {
//                            	 cColor=downColor;
//                            }
//                            else {
//                            	cColor=upColor;
//                            }
//                        }
//                    break;
//                    case 2:
//                        if(i==0){
//                        	cColor=upColor;
//                        }
//                        else{
//                            if(data[i-1]>data[i]){
//                            	cColor=downColor;
//                            }
//                            else {
//                            	cColor=upColor;
//                            }
//                        }
//                    break;
//                    case 3:
//                        if(price==null||sprice==null){
//                        	cColor=upColor;
//                        }
//                        else{
//                            if(price[i]>sprice[i]){
//                            	cColor=upColor;
//                            }
//                            else {
//                            	cColor=downColor;
//                            }
//                        }
//                    break;
//                    
//                }
//                _cvm.drawLine(gl, (int)x,y,(int)x,sp, cColor ,1.0f);
//                x+=xfactor;
//            }   
//        }else{
//            if(!getTitle().equals("기본거래량")){
//                for(int i=startIndex;i<dataLen;i++){
//                	float y = calcy(data[i]);
//                    y=(y>sp)?sp:y;
//                    float spy = sp-y;
//                    
//                    drawBar(gl,(int)(x-xw),(int)y,(int)(2*xw),(int)spy-(int)y,upColor);
//                    if(isSelected()&(i%5==0)){
//
//                    	_cvm.drawRect(gl, (int)x-2,y,(int)5,5, CoSys.YELLOW);
//
//                    }
//                    x+=xfactor;
//                }                
//            }else{
        float y = 0;

        int totLen = (dataLen-startIndex)*4*3;
        if(totLen<=0)
            return;
        float[] rectPositions = new float[totLen];
        float[] rectPositionsUp = new float[totLen];
        float[] rectPositionsDown = new float[totLen];
        int nRectIndex = 0;
        int nRectIndexUp = 0;
        int nRectIndexDown = 0;
        int nVolDrawType = _cvm.getVolDrawType();
        int nTypeOscUpDown = -1;
        if(this.getTitle().equals("기본거래량"))
        {
            //2012. 8. 30   기본거래량(거래량차트) 일때 강제로 2번째(전일대비고가의 상승하락) 으로 세팅 주석처리 : C17
            //nVolDrawType = 2;
        }
        else if(this.getTitle().equals("매수매도거래량"))
        {
            nVolDrawType = 5;
        }
        else {
            if(this.getDrawType2()==2) {
                nVolDrawType = 10;
            } else {
                nVolDrawType = 0;
            }
        }
        double[] dSellVolume = null;
        if(nVolDrawType == 5)
            dSellVolume = _cdm.getSubPacketData("매도거래량");
        float ySell=0;
        boolean bUp = false;
        float iWidth = (xw*2);
//        if(iWidth>COMUtil.getPixel_W(5))
//        {
//            //fWidth = 1.0;
//            if(iWidth>COMUtil.getPixel_W(8))
//                nWidth = (int)Math.round((xw*2)-COMUtil.getPixel_W(3));
//            else
//                nWidth = (int)Math.round((xw*2)-COMUtil.getPixel_W(2));
//            if((int)nWidth%2==0 && nWidth > 2)
//                nWidth -=1;
//        }
        //2024.01.03 by LYH - 일목균형표 줌인, 줌아웃, 스크롤 개선 >>
        dataLen = startIndex + _cvm.getViewNum();
        if(dataLen>data.length)
            dataLen= data.length;
        //2024.01.03 by LYH - 일목균형표 줌인, 줌아웃, 스크롤 개선 <<
        for(int i=startIndex;i<dataLen;i++){
            try {
                y = calcy(data[i]);
                if(dSellVolume!=null)
                    ySell = calcy(dSellVolume[i]);
            } catch(Exception e) {
//                		System.out.println(e.getMessage());
            }
            if(nVolDrawType!= 10) {
                y=(y>sp)?sp:y;
            }
            float spy = sp-y;
            if(spy<0) {
                spy=0;
            }
            //2020. 06. 08 by hyh - 우측으로 쏠린 머리, 꼬리 위치 가운데로 조정. Bar >>
//            float fStart = x - xw;
            float fStart = x - xw + fWidth; //2021.06.07 by lyk - kakaopay - 바 그래프 넓이 수정 >>
            //float fStart = x - xw - 1;
            //2020. 06. 08 by hyh - 우측으로 쏠린 머리, 꼬리 위치 가운데로 조정. Bar <<
            float fEnd = fStart + fWidth;
            //2020.07.06 by LYH >> 캔들볼륨 >>
            AREA area = _cvm.getArea(i-startIndex);
            if(area!=null)
            {
                fStart = area.getLeft();
                fEnd = area.getRight();
            }
            //2020.07.06 by LYH >> 캔들볼륨 <<
//            if(iWidth>COMUtil.getPixel_W(5))
//            {
//                if(iWidth>COMUtil.getPixel_W(8))
//                    fStart = (int)Math.round(x-(float)nWidth/2.0f-COMUtil.getPixel_W(1.5f));
//                else
//                    fStart = (int)Math.round(x-(float)nWidth/2.0f-COMUtil.getPixel_W(1.0f));
//                nEnd = (int)(fStart + nWidth);
//            }
            if(nVolDrawType!=10) {
                if(spy<=1 && data[i] !=0) {//h가 0인 경우 그래프를 보이기 위함.
                    y-=(2-spy);
                }

                if(dSellVolume!=null)
                {
                    if((sp-ySell)<=1 && dSellVolume[i] !=0) {//h가 0인 경우 그래프를 보이기 위함.
                        ySell-=(2-(sp-ySell));
                    }
                }
            }
            //2012. 8. 30 거래량의 선택된 라디오 버튼에 따라서 처리기능 구현 : I98
            switch(nVolDrawType){
                case 0:
                    //drawBar(gl,(int)(x-xw),(int)y,(int)(2*xw),sp-(int)y,upColor);
                    if(spy<=1) {//h가 0인 경우 그래프를 보이기 위함.
                        y-=(2-spy);
                    }
                    addRectPosition(rectPositions, nRectIndex, fStart, y, fEnd, sp);
                    nRectIndex += 4;
                    break;
                case 1:
                    if(price==null||i==0)
                    {
//                            	drawBar(gl,(int)(x-xw),(int)y,(int)(2*xw),sp-(int)y,upColor);
                        addRectPosition(rectPositionsUp, nRectIndexUp, fStart, y, fEnd, sp);
                        nRectIndexUp += 4;
                    }
                    else{
                        if(price[i-1]>price[i]){
//                                    drawBar(gl,(int)(x-xw),(int)y,(int)(2*xw),sp-(int)y,downColor);
                            addRectPosition(rectPositionsDown, nRectIndexDown, fStart, y, fEnd, sp);
                            nRectIndexDown += 4;
                        }else if(price[i-1]<price[i]){
//                                    drawBar(gl,(int)(x-xw),(int)y,(int)(2*xw),sp-(int)y,upColor);
                            addRectPosition(rectPositionsUp, nRectIndexUp, fStart, y, fEnd, sp);
                            nRectIndexUp += 4;
                        }
                        else
                        {
                            addRectPosition(rectPositions, nRectIndex, fStart, y, fEnd, sp);
                            nRectIndex += 4;
                        }
                    }
                    break;
                case 2:
                    if(hprice==null||i==0)
                    {
                        addRectPosition(rectPositionsUp, nRectIndexUp, fStart, y, fEnd, sp);
                        nRectIndexUp += 4;
//                            		drawBar(gl,(int)(x-xw),(int)y,(int)(2*xw),sp-(int)y,upColor);
                    }
                    else{
                        if(hprice[i-1]>hprice[i]){
                            addRectPosition(rectPositionsDown, nRectIndexDown, fStart, y, fEnd, sp);
                            nRectIndexDown += 4;
//                                    drawBar(gl,(int)(x-xw),(int)y,(int)(2*xw),sp-(int)y,downColor);
                        }else if(hprice[i-1]<hprice[i]){
                            addRectPosition(rectPositionsUp, nRectIndexUp, fStart, y, fEnd, sp);
                            nRectIndexUp += 4;
//                                    drawBar(gl,(int)(x-xw),(int)y,(int)(2*xw),sp-(int)y,upColor);
                        }
                        else
                        {
                            addRectPosition(rectPositions, nRectIndex, fStart, y, fEnd, sp);
                            nRectIndex += 4;
                        }
                    }
                    break;
                case 3: // 상승/하락(거래량)
                    if(i==0)
                    {
                        addRectPosition(rectPositionsUp, nRectIndexUp, fStart, y, fEnd, sp);
                        nRectIndexUp += 4;
                        //drawBar(gl,(int)(x-xw),(int)y,(int)(2*xw),sp-(int)y,upColor);
                    }
                    else{
                        if(data[i-1]>data[i])
                        {
                            addRectPosition(rectPositionsDown, nRectIndexDown, fStart, y, fEnd, sp);
                            nRectIndexDown += 4;
                            //drawBar(gl,(int)(x-xw),(int)y,(int)(2*xw),sp-(int)y,downColor);
                        }
                        else if(data[i-1]<data[i])
                        {
                            addRectPosition(rectPositionsUp, nRectIndexUp, fStart, y, fEnd, sp);
                            nRectIndexUp += 4;
                            //drawBar(gl,(int)(x-xw),(int)y,(int)(2*xw),sp-(int)y,upColor);
                        }
                        else
                        {
                            addRectPosition(rectPositions, nRectIndex, fStart, y, fEnd, sp);
                            nRectIndex += 4;
                        }
                    }
                    break;
                case 4:
                    if(price==null||sprice==null)
                    {
                        addRectPosition(rectPositionsUp, nRectIndexUp, fStart, y, fEnd, sp);
                        nRectIndexUp += 4;
//                            	drawBar(gl,(int)(x-xw),(int)y,(int)(2*xw),sp-(int)y,upColor);
                    }
                    else{
                        if(price[i]>sprice[i])
                        {
                            addRectPosition(rectPositionsUp, nRectIndexUp, fStart, y, fEnd, sp);
                            nRectIndexUp += 4;
                            bUp = true;
                            //                            		drawBar(gl,(int)(x-xw),(int)y,(int)(2*xw),sp-(int)y,upColor);
                        }
                        else if(price[i]<sprice[i])
                        {
                            addRectPosition(rectPositionsDown, nRectIndexDown, fStart, y, fEnd, sp);
                            nRectIndexDown += 4;
                            bUp = false;
                            //                                	drawBar(gl,(int)(x-xw),(int)y,(int)(2*xw),sp-(int)y,downColor);
                        }
                        else
                        {
//                            addRectPosition(rectPositions, nRectIndex, fStart, y, fEnd, sp);
                            if(_cvm.getCandle_sameColorType() == 1)//상승/하락기준 - 전일종가 기준 {
                            {
                                if(i>0) {
                                    if (price[i - 1] < price[i]) {
                                        bUp = true;
                                    } else if (price[i - 1] >price[i]) {
                                        bUp = false;
                                    }
                                    if(bUp)
                                    {
                                        addRectPosition(rectPositionsUp, nRectIndexUp, fStart, y, fEnd, sp);
                                        nRectIndexUp += 4;
                                    }
                                    else
                                    {
                                        addRectPosition(rectPositionsDown, nRectIndexDown, fStart, y, fEnd, sp);
                                        nRectIndexDown += 4;
                                    }
                                }
                            }
                            else
                            {
                                addRectPosition(rectPositions, nRectIndex, fStart, y, fEnd, sp);
                                nRectIndex += 4;
                            }
                        }
                    }
                    break;
                case 5:
                    //drawBar(gl,(int)(x-xw),(int)y,(int)(2*xw),sp-(int)y,upColor);
//                    if(spy<=1) {//h가 0인 경우 그래프를 보이기 위함.
//                        y-=(2-spy);
//                    }
                    addRectPosition(rectPositions, nRectIndex, fStart, y, fEnd, sp);
                    nRectIndex += 4;

                    addRectPosition(rectPositionsDown, nRectIndexDown, fStart, ySell, fEnd, sp);
                    nRectIndexDown += 4;
                    break;
                case 10:
                {
                    float sp1 = calcy(stand[0]);
                    float y1 = y;
                    if(sp1>max_view)sp1=max_view;
                    else if(sp1 < min_view)	sp1 = min_view;	//2015. 3. 3 오실레이터 차트 확대시 지표영역 넘어서 그림

                    if(data[i] >= stand[0]) {
                        if(sp1==y1) {
                            y1--;
                        }
                        addRectPosition(rectPositionsUp, nRectIndexUp, fStart, y1, fEnd, sp1);
                        nRectIndexUp += 4;
                        nTypeOscUpDown = OSC_UP;
                    } else {
                        if(sp1==y1) {
                            y1++;
                        }
                        //2013.04.05 by LYH >> 젤리빈 4.2.2이상 버전 오실레이터에 0 이하 바 안 그려지던 오류 수정.
                        //addRectPosition(rectPositionsDown, nRectIndexDown, fStart, y1, fEnd, sp1);
                        addRectPosition(rectPositionsDown, nRectIndexDown, fStart, sp1, fEnd, y1);
                        //2013.04.05 by LYH <<
                        nRectIndexDown += 4;
                        nTypeOscUpDown = OSC_DOWN;
                    }

                }
            }
//                    if(isSelected()&(i%5==0)){
//
//                        _cvm.drawRect(gl, (int)x-2,y,(int)5,5, CoSys.YELLOW);
//
//                    }
            x+=xfactor;
        }
        //2012. 8. 30  거래량 상세설정에서 초기화시 색상이 초기화 되지 않는 현상 수정 : I98
        if(nRectIndex>0)
        {   float[] tmp = new float[nRectIndex];
            System.arraycopy(rectPositions,0,tmp,0,nRectIndex);
            if(_cvm.bIsLineFillChart )
            {
                int[] colorLine = new int[3];
                colorLine[0] = 31;
                colorLine[1] = 76;
                colorLine[2] = 115;
                _cvm.drawFillRects(gl, tmp, colorLine ,1.0f);
            }
            else if(_cvm.bStandardLine)
            {
                _cvm.drawFillRects(gl, tmp, CoSys.STANDARD_VOL ,1.0f);
            }
            else
            {
                //2013.10.08 by LYH >> 거래량 일반 색상 적용.
//	                	if(nVolDrawType == 0 && !this.getTitle().equals("기본거래량"))
//	                	{
//	                		_cvm.drawFillRects(gl, tmp, upColor ,1.0f);
//	                	}
//	                	else
//	                		_cvm.drawFillRects(gl, tmp, sameColor,1.0f);            			
                if(this.getTitle().equals("기본거래량"))
                {
//                    if(nVolDrawType == 0)
                    if(nVolDrawType != 4) // '캔들색과 같이' 가 아닐 경우
                    {
                        _cvm.drawFillRects(gl, tmp, sameColor,1.0f);
                    }
                    else
                    {
                        //2013.02.12 by LYH >> 블랙에서 보합색 변경
                        if(_cvm.getSkinType() == COMUtil.SKIN_BLACK) {
                            _cvm.drawFillRects(gl, tmp, CoSys.CHART_COLORS[7],1.0f);
                        }
                        else
                        {
                            _cvm.drawFillRects(gl, tmp, CoSys.CHART_COLORS[2],1.0f);
                        }
                        //2013.02.12 by LYH << 블랙에서 보합색 변경
                    }
                }
                else
                {
                    _cvm.drawFillRects(gl, tmp, upColor ,1.0f);
                }
                //2013.10.08 by LYH <<
            }
        }
        if(nRectIndexUp>0)
        {   float[] tmp = new float[nRectIndexUp];
            System.arraycopy(rectPositionsUp,0,tmp,0,nRectIndexUp);
            if(_cvm.bIsLineFillChart )
            {
                int[] colorLine = new int[3];
                colorLine[0] = 31;
                colorLine[1] = 76;
                colorLine[2] = 115;
                _cvm.drawFillRects(gl, tmp, colorLine ,1.0f);
            }
            else
            {
                if(nVolDrawType == 10) //오실레이터
                {
                    if(_cvm.bInvestorChart) {
                        if(getDrawType2() == 2)
                            _cvm.drawFillRects(gl, tmp, CoSys.CHART_COLORS[0], 1.0f);
                        else
                            _cvm.drawFillRects(gl, tmp, CoSys.CHART_COLORS[14], 1.0f);
                    }
                    else
                        _cvm.drawFillRects(gl, tmp, CoSys.CHART_COLORS[5],1.0f);
                }
                else
                    _cvm.drawFillRects(gl, tmp, upColor,1.0f);
            }
        }
        if(nRectIndexDown>0)
        {   float[] tmp = new float[nRectIndexDown];
            System.arraycopy(rectPositionsDown,0,tmp,0,nRectIndexDown);
            if(_cvm.bIsLineFillChart )
            {
                int[] colorLine = new int[3];
                colorLine[0] = 31;
                colorLine[1] = 76;
                colorLine[2] = 115;
                _cvm.drawFillRects(gl, tmp, colorLine ,1.0f);
            }
            else
            {
                if(_cvm.bInvestorChart)
                {
                    if (getDrawType2() == 2 && nVolDrawType == 10)
                        _cvm.drawFillRects(gl, tmp, CoSys.CHART_COLORS[1],1.0f);
                    else
                        _cvm.drawFillRects(gl, tmp, CoSys.CHART_COLORS[15],1.0f);
                }
                else
                    _cvm.drawFillRects(gl, tmp, downColor ,1.0f);
            }
        }
//            }
//        }

        //지표 현재값 표시.
        //지표 현재값 표시.
        if(_cvm.useJipyoSign==true && !_cvm.bIsLineFillChart && !_cvm.bStandardLine && !_cvm.bIsInnerTextVertical && !_cvm.bIsLine2Chart && !_cvm.bIsMiniBongChart) {
            double curVal = data[dataLen-1];

            //2012. 10. 19 거래량 yscale 빨간색 표시되던 현상 수정 : C23
//	        if(getTitle().equals("기본거래량")) {//거래량은 YScale에서 표시함.
//	        	return;
//	        }
            String curStr = getFormatData(dataLen-1);
            //2020.12.22 by LYH >> 시장 지표 데이터 마지막 2개 이상 0일때 타이틀 처리 Start
            if(bMarketData && dataLen > 1)
            {
                for(int k=dataLen-1; k>=0; k--) {
                    if(data[k] != 0)
                    {
                        curVal=data[k];
                        curStr = getFormatData(k);
                        break;
                    }
                }
            }
            //2020.12.22 by LYH >> 시장 지표 데이터 마지막 2개 이상 0일때 타이틀 처리 End
            if(getTitle().equals("기본거래량")||getTitle().equals("거래대금"))
            {
//                if(max_data >= 10000000 && max_data < 1000000000)
                if (COMUtil._mainFrame.mainBase.baseP.nMarketType == 1 && (getTitle().equals("기본거래량"))) {
                    double dData = curVal;
                    String strUnit = "";
                    if(getTitle().equals("거래대금")) { //현재 사용안함
                        strUnit = "백만";
                    } else {
                        strUnit = "천";
                    }
                    String strCur = "" + (int) (dData);
                    curStr = ChartUtil.getFormatedData(strCur, 11) + strUnit;
                } else {
                    //2016. 2. 18 거래량 현재가 M표시>>
                    if(max_data >= 100000000000.0)
                    {
                        double dData = curVal;

                        if(dData >= 10000 && ((int)(dData/100000))%10 > 5)
                        {
                            dData += 500000;
                        }

                        String strCur = ""+(int)(dData/1000000);
                        curStr = ChartUtil.getFormatedData(strCur, 11)+"백만";
                    }
                    //2016. 2. 18 거래량 현재가 M표시<<
                    else if (max_data >= 10000000) {
                        double dData = curVal;

                        //2016. 2. 18 거래량 반올림 처리>>
                        if (dData >= 10000 && ((int) (dData / 100)) % 10 > 5) {
                            dData += 500;
                        }
                        //2016. 2. 18 거래량 반올림 처리<<

                        //2024.03.20 by SJW - 거래량이 1,000 미만인 경우 '0천'이 아닌 해당 값으로 노출 >>
//                        String strCur = "" + (int) (dData / 1000);
//                        curStr = ChartUtil.getFormatedData(strCur, 11) + "천";
                        if (dData >= 1000) {
                            String strCur = "" + (int) (dData / 1000);
                            curStr = ChartUtil.getFormatedData(strCur, 11) + "천";
                        } else {
                            curStr = String.valueOf((int) dData);
                        }
                        //2024.03.20 by SJW - 거래량이 1,000 미만인 경우 '0천'이 아닌 해당 값으로 노출 <<
                    }

                }
            }
            //int curStrLen = _cvm.tf.GetTextLength(curStr)+10;

            float yp = calcy(curVal);
            float xpos = this.getBounds().right;

            xpos = _cvm.getBounds().left + _cvm.getBounds().width() - _cvm.Margin_R +(int)COMUtil.getPixel(1);
            int pw = _cvm.Margin_R;

            int[] textColor = null;
            //2012. 10. 19 거래량 yscale 빨간색 표시되던 현상 수정  : C23

            if(yp+(int)COMUtil.getPixel_H(18)/2 > getBounds().bottom)
                yp = getBounds().bottom-(int)COMUtil.getPixel_H(18)/2;
            
            if(nTypeOscUpDown==OSC_UP)
            {
//                _cvm.drawFillTri(gl, xpos,yp,COMUtil.getPixel(4),(int)COMUtil.getPixel(14), CoSys.CHART_COLORS[14]);
//                _cvm.drawFillRect(gl, xpos+(int)COMUtil.getPixel(4), yp-(int)COMUtil.getPixel(7), pw, (int)COMUtil.getPixel(14), CoSys.CHART_COLORS[14], 1.0f);
                //2020.05.29 by LYH>> 투자자지표 스케일 타이틀 색상 타이틀 색으로 >>
                //_cvm.drawCurrentPriceBox(gl, xpos, yp-(int)COMUtil.getPixel_H(21)/2, pw-(int)COMUtil.getPixel(2), (int)COMUtil.getPixel_H(21), CoSys.CHART_COLORS[14]);
                _cvm.drawCurrentPriceBox(gl, xpos, yp-(int)COMUtil.getPixel_H(18)/2, pw-(int)COMUtil.getPixel(4), (int)COMUtil.getPixel_H(18), upColor);
                //2020.05.29 by LYH>> 투자자지표 스케일 타이틀 색상 타이틀 색으로 <<
                textColor = CoSys.WHITE;
            }
            else if(nTypeOscUpDown==OSC_DOWN)
            {
//                _cvm.drawFillTri(gl, xpos,yp,COMUtil.getPixel(4),(int)COMUtil.getPixel(14), CoSys.CHART_COLORS[15]);
//                _cvm.drawFillRect(gl, xpos+(int)COMUtil.getPixel(4), yp-(int)COMUtil.getPixel(7), pw, (int)COMUtil.getPixel(14), CoSys.CHART_COLORS[15], 1.0f);
                //2020.05.29 by LYH>> 투자자지표 스케일 타이틀 색상 타이틀 색으로 >>
                //_cvm.drawCurrentPriceBox(gl, xpos, yp-(int)COMUtil.getPixel_H(21)/2, pw-(int)COMUtil.getPixel(2), (int)COMUtil.getPixel_H(21), CoSys.CHART_COLORS[15]);
                _cvm.drawCurrentPriceBox(gl, xpos, yp-(int)COMUtil.getPixel_H(18)/2, pw-(int)COMUtil.getPixel(4), (int)COMUtil.getPixel_H(18), upColor);
                //2020.05.29 by LYH>> 투자자지표 스케일 타이틀 색상 타이틀 색으로 <<
                textColor = CoSys.WHITE;
            }
            else if(getTitle().equals("기본거래량") || getTitle().equals("거래대금")) {//거래량은 YScale에서 표시함.
//                _cvm.drawFillTri(gl, xpos,yp,COMUtil.getPixel(4),(int)COMUtil.getPixel(14), CoSys.CHART_COLORS[2]);
//                _cvm.drawFillRect(gl, xpos+(int)COMUtil.getPixel(4), yp-(int)COMUtil.getPixel(7), pw, (int)COMUtil.getPixel(14), CoSys.CHART_COLORS[2], 1.0f);
//                pw = (int)COMUtil.getPixel(58);
//                if(_cvm.bIsTodayLineChart)
//                    pw = (int)COMUtil.getPixel(50);
//                _cvm.drawFillTri(gl, xpos, yp, COMUtil.getPixel(4), (int) COMUtil.getPixel(14), sameColor);
//                _cvm.drawFillRect(gl, xpos + (int) COMUtil.getPixel(4), yp - (int) COMUtil.getPixel(7), pw, (int) COMUtil.getPixel(14), sameColor, 1.0f);

                double price = data[dataLen-1];
                double prePrice = price;
                if(dataLen>1) {
                    prePrice = data[dataLen-2];
                }

                int[] backColor = CoSys.CHART_COLORS[2];
                if (price < prePrice) {
                    backColor = CoSys.CHART_COLORS[1];
                } else if (price > prePrice) {
                    backColor = CoSys.CHART_COLORS[0];
                }
                _cvm.drawCurrentPriceBox(gl, xpos, yp-(int)COMUtil.getPixel_H(18)/2, pw-(int)COMUtil.getPixel(4), (int)COMUtil.getPixel_H(18), backColor);
//                textColor = CoSys.GREY990_N_DARK;
                if(COMUtil.currentTheme == COMUtil.SKIN_BLACK) {
                    textColor = CoSys.GREY990;
                } else {
                    textColor = CoSys.GREY0_WHITE;
                }
            }
            else
            {
//                _cvm.drawFillTri(gl, xpos,yp,COMUtil.getPixel(4),(int)COMUtil.getPixel(14), upColor);
//                _cvm.drawFillRect(gl, xpos+(int)COMUtil.getPixel(4), yp-(int)COMUtil.getPixel(7), pw, (int)COMUtil.getPixel(14), upColor, 1.0f);
                _cvm.drawCurrentPriceBox(gl, xpos, yp-COMUtil.getPixel_H(18)/2, pw-(int)COMUtil.getPixel(4), (int)COMUtil.getPixel_H(18), upColor);
                textColor = CoSys.WHITE;
            }


            //_cvm.drawString(gl, CoSys.BLACK, (int)xpos, (int)yp, ChartUtil.getFormatedData(curStr, _cdm.getPriceFormat()));
            //_cvm.drawString(gl, CoSys.WHITE, (int)xpos, (int)yp, String.format("%.2f", curVal));
            //2013.03.27 by LYH >> 스케일 가격 오른쪽 정렬
            //_cvm.drawString(gl, CoSys.WHITE, (int)xpos, (int)yp, curStr);
            int w = _cvm.GetTextLength(curStr);
            //_cvm.drawString(gl, CoSys.WHITE, (int)xpos+pw-w-(int)COMUtil.getPixel(3), (int)yp, curStr);

            //2021.07.12 by hanjun.Kim - kakaopay - 가격표 좌측정렬 >>
//            _cvm.drawString(gl, textColor, (int)xpos+(int)COMUtil.getPixel(12), (int)yp + (int) COMUtil.getPixel(1), curStr);
            _cvm.drawScaleString(gl, textColor, (int)xpos+(int)COMUtil.getPixel(3), yp, curStr);

            //2013.03.27 by LYH <<
            _cvm.useJipyoSign=false;
        }
    }
    public void draw(Canvas gl, double[] data, double[] stand){
        if(data==null)return;
        float x=getBounds().left+xw;
        float sp=0 ;//기준가의 픽셀좌표를 얻는다
        for(int i=0;i<stand.length;i++){
            sp = calcy(stand[i]);

            _cvm.drawLine(gl, (int)(x-xw), sp,getBounds().width(),sp, _cvm.CST ,1.0f);
        }
        if(xw<1){
            for(int i=0;i<data.length;i++){
                float y = calcy(data[i]);
                y=(y>sp)?sp:y;
                if(y<sp){

                    _cvm.drawLine(gl, (int)(x),y,(int)(x),sp-y, upColor ,1.0f);
                }else{

                    _cvm.drawLine(gl, (int)(x),sp,(int)(x),y-sp, downColor ,1.0f);
                }
                x+=xfactor;
            }
        }else{
            for(int i=0;i<data.length;i++){
                float y = calcy(data[i]);
                y=(y>sp)?sp:y;
                if(y<sp){

                    _cvm.drawRect(gl, (int)(x-xw),y,(int)(2*xw),sp-y, upColor);

                    _cvm.drawRect(gl, (int)(x-xw),y,(int)(2*xw),sp-y, upColor);
                }else{

                    _cvm.drawRect(gl,(int)(x-xw),sp,(int)(2*xw),y-sp, downColor);

                    _cvm.drawRect(gl,(int)(x-xw),sp,(int)(2*xw),y-sp, downColor);
                }
                x+=xfactor;
            }
        }
    }
    public void draw(Canvas gl, double[][] data){
        if(data==null)return;

        float x=getBounds().left+xw;
        float sp = max_view;

        _cvm.drawLine(gl,(int)x,sp,(int)x+getBounds().width()-(int)xw,sp, CoSys.GRAY ,1.0f);

        if(xw<1&&getDrawType2()==0){
            for(int i=0;i<data.length;i++){
                float y = calcy(data[i][0]);
                y=(y>sp)?sp:y;

                _cvm.drawRect(gl,(int)x,y,(int)x,sp,upColor);

                x+=xfactor;
            }
        }else{
            for(int i=0;i<data.length;i++){
                float y = calcy(data[i][0]);
                y=(y>sp)?sp:y;
                drawBar(gl,(int)(x-xw),(int)y,(int)(2*xw),sp-(int)y,upColor);
                x+=xfactor;
            }
        }
    }
    public void draw(Canvas gl, double[][] data, double[] stand){
        if(data==null)return;

        float x=getBounds().left+xw;
        float sp=0 ;//기준가의 픽셀좌표를 얻는다
        for(int i=0;i<stand.length;i++){
            sp = calcy(stand[i]);
            _cvm.drawLine(gl,(int)(x-xw), sp,getBounds().width(),sp, _cvm.CST ,1.0f);
        }
        if(xw<1){
            for(int i=0;i<data.length;i++){
                float y = calcy(data[i][0]);
                y=(y>sp)?sp:y;
                if(y<sp){
                    _cvm.drawRect(gl,(int)(x),y,(int)(x),sp-y, upColor);
                }else{
                    _cvm.drawRect(gl,(int)(x),sp,(int)(x),y-sp, downColor);
                }
                x+=xfactor;
            }
        }else{
            for(int i=0;i<data.length;i++){
                float y = calcy(data[i][0]);
                y=(y>sp)?sp:y;
                if(y<sp){
                    _cvm.drawRect(gl,(int)(x-xw),y,(int)(2*xw),sp-y, upColor);
                }else{
                    _cvm.drawRect(gl,(int)(x-xw),sp,(int)(2*xw),y-sp, downColor);
                }
                x+=xfactor;
            }
        }
    }
    //상하식
    private void drawOscillator(Canvas gl, float x, float y, float w, float h, float sp){
        //y=(y>sp)?sp:y;
        w=(w>1)?w:1;
        _cvm.drawLine(gl,x,sp,x+w,sp, _cvm.CST ,1.0f);

        if(y<sp){
            _cvm.drawFillRect(gl,x,y,w,sp-y, CoSys.CHART_COLORS[0], 1.0f);
        }else{
            if(sp<min_view)sp=min_view;
            _cvm.drawFillRect(gl,x,sp,w,y-sp, CoSys.CHART_COLORS[1], 1.0f);
        }
    }
    //빗형식
    private void drawUpDownRect(Canvas gl, float x, float y, float w, float h, float sp){

        _cvm.drawRect(gl,x,y,w,sp-y, upColor);

        _cvm.drawRect(gl,x,sp,w,max_view-sp, downColor);

    }

    //2019. 03. 12 by hyh - 투자자 업종 가로 바차트 추가 >>
    private void drawBongGraph(Canvas gl) {
        String[] nameDatas = _cdm.accrueNames;
        double[] openDatas = _cdm.accrueOpens;
        double[] highDatas = _cdm.accrueHighs;
        double[] lowDatas = _cdm.accrueLows;
        double[] datas = _cdm.accrueCloses;
        double[] baseDatas = _cdm.accrueBases;

        if(datas==null) {
            return;

        }
        if(nameDatas.length != datas.length)
        {
            return;
        }

        RectF chart_bounds = getBounds();

        int nTotCnt = datas.length;
        double[] dDatas = new double[nTotCnt];
        double[] dOpenDatas = new double[nTotCnt];
        double[] dHighDatas = new double[nTotCnt];
        double[] dLowDatas = new double[nTotCnt];


        double minData = 100;
        double maxData = -100;

        for(int i=0; i<datas.length; i++) {
            try {
                dDatas[i] = ((datas[i]*100-baseDatas[i]*100)/baseDatas[i]);
                if(openDatas[i]==0)
                {
                    dOpenDatas[i] = dDatas[i];
                    dHighDatas[i] = dDatas[i];
                    dLowDatas[i] = dDatas[i];
                }
                else
                {
                    dOpenDatas[i] = ((openDatas[i]*100-baseDatas[i]*100)/baseDatas[i]);
                    dHighDatas[i] = ((highDatas[i]*100-baseDatas[i]*100)/baseDatas[i]);
                    dLowDatas[i] = ((lowDatas[i]*100-baseDatas[i]*100)/baseDatas[i]);
                }
                if(dLowDatas[i]<minData)
                    minData = dLowDatas[i];
                if(dHighDatas[i]>maxData)
                    maxData = dHighDatas[i];
            } catch(Exception e) {
                dDatas[i] = 0;
            }
        }

        if(Math.abs(maxData)>Math.abs(minData))
            minData = -1*maxData;
        else
            maxData = -1*minData;

        if(maxData==0)
        {
            maxData = 1;
            minData = -1;
        }
//        float nHeight = (max_view-min_view);
//        float yfactor = (nHeight*1.0f)/(float)(maxData-minData);
        float nWidth = getBounds().width()-COMUtil.getPixel(145); //2018. 12. 11 by hyh - 업종/섹트 가로선 위치 조정. 115->145
        float xfactor = (nWidth*1.0f)/(float)(maxData-minData);

        //차트 그리기
        //기준선 그리기
//		float cHRate = (float)(Math.abs(maxData) / (Math.abs(minData) + Math.abs(maxData)));
        float cLRate = (float)(Math.abs(minData) / (Math.abs(minData) + Math.abs(maxData)));
        float gijunX = COMUtil.getPixel(135) + nWidth - cLRate * nWidth; //2018. 12. 11 by hyh - 업종/섹트 가로선 위치 조정. 105->135

        int gab = (int)COMUtil.getPixel(14);
        int yPos = (int)chart_bounds.top + (int)COMUtil.getPixel(4);

        _cvm.setLineWidth_Fix(COMUtil.getPixel(1));
        _cvm.drawLine(gl, 0, 0, chart_bounds.height(), COMUtil.getPixel(1), CoSys.HEADER_LN_COLOR, 1.0f);
        _cvm.setLineWidth(1);
        //_cvm.drawLineWidth(gl,xPos,gijunY,chart_bounds.width(),gijunY, CoSys.GRAY, 0.5f, 1);
        _cvm.drawDashLine(gl, gijunX, 0, gijunX, chart_bounds.height(), CoSys.LIST_LN_COLOR, 1.0f);

        //2018. 12. 11 by hyh - 업종/섹트 가로선 위치 조정. 100->130
        _cvm.drawLine(gl, COMUtil.getPixel(130), 0, COMUtil.getPixel(130), chart_bounds.height(), CoSys.LIST_LN_COLOR, 1.0f);
//        //drawDashLine
//        _cvm.drawDashLine(gl, xPos,-yfactor*(float)maxData,chart_bounds.width(),-yfactor*(float)maxData, CoSys.CHART_COLORS[0], 0.5f);
//        //하한
//        _cvm.drawDashLine(gl, xPos,yfactor*(float)minData,chart_bounds.width(),yfactor*(float)minData, CoSys.CHART_COLORS[1], 0.5f);

        if(nTotCnt<=0)
            return;

        //int unitW = (chart_bounds.width()-gab*(dDatas.length-2))/nTotCnt;
        int unitH = (int)COMUtil.getPixel(19);
        int dLen = dDatas.length;
        for(int i=0; i<dLen; i++) {
            int nIndex = dLen-i -1;

            //2018. 10. 30 by hyh - 업종 섹터 차트 타이틀 잘리는 현상 최소화 >>
            _cvm.drawStringWithSize(gl, _cvm.CST, chart_bounds.left+_cvm.Margin_L+(int)COMUtil.getPixel(8),(int)(yPos+COMUtil.getPixel(11)), (int)COMUtil.getPixel((float)14.7), nameDatas[nIndex]);

            //_cvm.drawStringWithSize(gl, _cvm.CST, chart_bounds.left+_cvm.Margin_L+(int)COMUtil.getPixel(8),(int)(COMUtil.getPixel(18)+i*COMUtil.getPixel(33)), (int)COMUtil.getPixel((float)14.7), nameDatas[nIndex]);
            //2018. 10. 30 by hyh - 업종 섹터 차트 타이틀 잘리는 현상 최소화 <<

            int color[];
            if(dOpenDatas[nIndex]<dDatas[nIndex]) {
                color = CoSys.CHART_COLORS[0];
            } else if(dOpenDatas[nIndex]>dDatas[nIndex]){
                color = CoSys.CHART_COLORS[1];
            } else{
                color = CoSys.CHART_COLORS[2];
            }
            float xClose = gijunX;
            if(dDatas[nIndex] != 0)
                xClose += (float)(xfactor*(float)dDatas[nIndex]);
            float xOpen = gijunX;
            if(dOpenDatas[nIndex] != 0)
                xOpen += (float)(xfactor*(float)dOpenDatas[nIndex]);
            float xHigh = gijunX;
            if(dHighDatas[nIndex] != 0)
                xHigh += (float)(xfactor*(float)dHighDatas[nIndex]);
            float xLow = gijunX;
            if(dLowDatas[nIndex] != 0)
                xLow += (float)(xfactor*(float)dLowDatas[nIndex]);

            float xPos;
            String strValue = "("+COMUtil.format(String.valueOf(dDatas[nIndex]), 2, 3)+"%)";
            if(Math.abs(xHigh-gijunX)<Math.abs(xLow-gijunX))
                xPos = xHigh+(int)COMUtil.getPixel(2);
            else {
                xPos = xLow - (_cvm.getFontWidth(strValue, (int)COMUtil.getPixel(10)) + (int) COMUtil.getPixel(2));
            }

            if(dDatas[nIndex]<0) {
                nWidth = xOpen - xClose;
                if(nWidth <1)
                    nWidth = 1;
                _cvm.drawFillRect(gl, xClose, (float)yPos , nWidth, (float)unitH, color, 1);
                _cvm.drawLine(gl, xHigh, (float)yPos+unitH/2, xLow, (float)yPos+unitH/2, color ,1.0f);

                _cvm.drawStringWithSize(gl, CoSys.CHART_COLORS[1], xPos,(int)yPos+unitH/2, (int)COMUtil.getPixel(10), strValue);
            } else if(dDatas[nIndex]>0) {
                nWidth = xClose - xOpen;
                if(nWidth <1)
                    nWidth = 1;
                _cvm.drawFillRect(gl, xOpen, (float)yPos, nWidth, (float)unitH, color, 1);
                _cvm.drawLine(gl, xHigh, (float)yPos+unitH/2, xLow, (float)yPos+unitH/2, color ,1.0f);

                _cvm.drawStringWithSize(gl, CoSys.CHART_COLORS[0], xPos,(int)yPos+unitH/2, (int)COMUtil.getPixel(10), strValue);
            }
            else
            {
                nWidth = xClose - xOpen;
                if(nWidth <1)
                    nWidth = 1;
                _cvm.drawFillRect(gl, xOpen, (float)yPos, nWidth, (float)unitH, color, 1);
                _cvm.drawLine(gl, xHigh, (float)yPos+unitH/2, xLow, (float)yPos+unitH/2, color ,1.0f);

                _cvm.drawStringWithSize(gl, CoSys.CHART_COLORS[2], xPos,(int)yPos+unitH/2, (int)COMUtil.getPixel(10), strValue);
            }
            //xPos = xPos+unitW+gab;
            _cvm.drawLine(gl, chart_bounds.left, (float)yPos+unitH+gab/2, chart_bounds.right, (float)yPos+unitH+gab/2, CoSys.LIST_LN_COLOR ,1.0f);
            yPos = yPos+unitH+gab;
        }
    }

    /* 상하 바차트 그리기 */
    private void drawUpDownRect(Canvas gl, double[] dDatas) {
        if (dDatas == null) {
            return;
        }

        boolean isInnerText = _cvm.bIsInnerText;
        RectF chart_bounds = getBounds();
        int nTotCnt = dDatas.length;
        if(min_data>0)
        {
            max_view = _cvm.getBounds().height()-COMUtil.getPixel_H(30);
        }
        double minData = min_data;
        double maxData = max_data;
        float nHeight = (max_view - min_view);
        float yfactor = (nHeight * 1.0f) / (float) (maxData - minData);

        //차트 그리기
        //기준선 그리기
        float cLRate = (float) (Math.abs(minData) / (Math.abs(minData) + Math.abs(maxData)));
        float gijunY = nHeight - cLRate * nHeight + chart_bounds.top;

        //2014.06.01 by LYH >> updown차트 음수만 있을 경우, 양수만 있을 경우 개선.
//		if(gijunY >= chart_bounds.height()) {
//			gijunY = chart_bounds.height();
//		}
        if (minData == 0) {
            gijunY -= COMUtil.getPixel(5);
        }
        if (maxData == 0) {
            gijunY += COMUtil.getPixel(5);
        }

        if (minData == 0 || maxData == 0) {
            nHeight -= COMUtil.getPixel(5);
            yfactor = (nHeight * 1.0f) / (float) (maxData - minData);
        }
        //2014.06.01 by LYH << updown차트 음수만 있을 경우, 양수만 있을 경우 개선.
        int gab = (int) ((COMUtil.getPixel_W(40) * _cvm.getBounds().width())/COMUtil.getPixel_W(360));
        //float xPos = chart_bounds.left;
        float xPos = (int) ((COMUtil.getPixel_W(50) * _cvm.getBounds().width())/COMUtil.getPixel_W(360));
        float xMargin = (int) ((COMUtil.getPixel_W(18) * _cvm.getBounds().width())/COMUtil.getPixel_W(360));
        float unitW = (int) ((COMUtil.getPixel_W(60) * _cvm.getBounds().width())/COMUtil.getPixel_W(360));

        int font = (int) COMUtil.getPixel(13);
        int titleFont = (int) COMUtil.getPixel(14);
        if(_cvm.getBounds().width()<COMUtil.getPixel_W(200))
        {
            font = (int) COMUtil.getPixel(9);
            titleFont = (int) COMUtil.getPixel(12);
        }
        _cvm.setLineWidth(1);

//        if (_cvm.getSkinType() == COMUtil.SKIN_BLACK) {
//            int[] GRAY = {102, 102, 102};
//            _cvm.drawLine(gl, COMUtil.getPixel_W(18), gijunY, chart_bounds.width(), gijunY, GRAY, 0.5f);
//        }
//        else {
            int[] GRAY = {221, 221, 221};
            _cvm.drawLine(gl, xMargin, gijunY, chart_bounds.width()-xMargin, gijunY, GRAY, 1.0f);
//        }

//        //상한
//        _cvm.drawDashLine(gl, xPos, -yfactor * (float) maxData, chart_bounds.width(), -yfactor * (float) maxData, CoSys.CHART_COLORS[0], 0.5f);
//        //하한
//        _cvm.drawDashLine(gl, xPos, yfactor * (float) minData, chart_bounds.width(), yfactor * (float) minData, CoSys.CHART_COLORS[1], 0.5f);

        if (nTotCnt <= 0)
            return;

//        float unitW = (chart_bounds.width() - gab * (dDatas.length - 2)) / nTotCnt;


        int[] arTextColor = {17,17,17};
        float strWidth = 0;
        int strXPos = 0;

        for (int i = 0; i < dDatas.length; i++) {

            if(_cdm.accrueNames != null) {
                String strTitle = _cdm.accrueNames[i];
                strWidth = _cvm.getFontWidth(strTitle, titleFont);
                strXPos = (int) (unitW / 2 - strWidth / 2);
                _cvm.drawStringWithSize(gl, arTextColor, xPos + strXPos, (int) (chart_bounds.bottom + COMUtil.getPixel_H(34)),  titleFont, strTitle);
            }

            //xPos 계산
            String strValue = COMUtil.format(String.valueOf(dDatas[i]), 0, 3);
            strWidth = _cvm.getFontWidth(strValue, font);

            strXPos = (int) ((unitW / 2) - (strWidth / 2));

            if (dDatas[i] > 0) {
                float gHeight = (float) (yfactor * (float) dDatas[i]);
                if (gHeight < 1)
                    gHeight = 1;
                _cvm.drawFillRect(gl, (float) xPos, gijunY - gHeight, (float) unitW, gHeight, CoSys.CHART_COLORS[0], 1);
                if (isInnerText) {
                    _cvm.drawStringWithSize(gl, CoSys.CHART_COLORS[0], xPos + strXPos, (int) gijunY - gHeight - (int) COMUtil.getPixel_H(8), font, strValue);
                }
            }
            else if (dDatas[i] < 0) {
                float gHeight = yfactor * (float) Math.abs(dDatas[i]);

                if (gHeight < 1)
                    gHeight = 1;
                _cvm.drawFillRect(gl, (float) xPos, gijunY, (float) unitW, gHeight, CoSys.CHART_COLORS[1], 1);
                if (isInnerText) {
                    _cvm.drawStringWithSize(gl, CoSys.CHART_COLORS[1], xPos + strXPos, (int) gijunY + gHeight + (int) COMUtil.getPixel_H(12), font, strValue);
                }
            }

            xPos = xPos + unitW + gab;
        }
    }
    //2019. 03. 12 by hyh - 투자자 업종 가로 바차트 추가 <<

    /* 상하 바차트 그리기 */
    private void drawMemberBar(Canvas gl, double[] dDatas){
        if(dDatas==null) {
            return;

        }

        RectF chart_bounds = getBounds();
        int nTotCnt = dDatas.length;
        double minData = min_data;
        double maxData = max_data;
        if(maxData==0 && minData==0)
            return;

        float nHeight = (max_view-min_view);
        float yfactor = (nHeight*1.0f)/(float)(maxData-minData);

        //차트 그리기
        //기준선 그리기
//        float cLRate = (float)(Math.abs(minData) / (Math.abs(minData) + Math.abs(maxData)));
//        float gijunY = nHeight - cLRate * nHeight + chart_bounds.top;
        float gijunY = nHeight + chart_bounds.top;

        //2014.06.01 by LYH >> updown차트 음수만 있을 경우, 양수만 있을 경우 개선.
//		if(gijunY >= chart_bounds.height()) {
//			gijunY = chart_bounds.height();
//		}
        if(minData == 0)
        {
            gijunY -= COMUtil.getPixel(5);
        }
        if(maxData == 0)
        {
            gijunY += COMUtil.getPixel(5);
        }

        if(minData == 0 || maxData == 0)
        {
            nHeight -= COMUtil.getPixel(5);
            yfactor = (nHeight*1.0f)/(float)(maxData-minData);
        }
        //2014.06.01 by LYH << updown차트 음수만 있을 경우, 양수만 있을 경우 개선.
//        int gab = (int)COMUtil.getPixel(5);
        float gab = (float)COMUtil.getPixel(16);
        float xPos = chart_bounds.left;

        _cvm.setLineWidth(1);

//        if(_cvm.getSkinType() == COMUtil.SKIN_BLACK) {
//            int[] GRAY = {102,102,102};
//            _cvm.drawLineWidth(gl,xPos,gijunY,chart_bounds.width(),gijunY, GRAY, 0.5f, 1);
//        } else {
//            int[] GRAY = {192,192,192};
////        	_cvm.drawLineWidth(gl,xPos,gijunY,chart_bounds.width(),gijunY, GRAY, 0.5f, 1);
//        }

//        //상한
//        _cvm.drawDashLine(gl, xPos,-yfactor*(float)maxData,chart_bounds.width(),-yfactor*(float)maxData, CoSys.CHART_COLORS[0], 0.5f);
        //하한
//		_cvm.drawDashLine(gl, xPos,yfactor*(float)minData,chart_bounds.width(),yfactor*(float)minData, CoSys.CHART_COLORS[1], 0.5f);

        if(nTotCnt<=0)
            return;

        float unitW = (float)(chart_bounds.width()-gab*(dDatas.length))/nTotCnt;
        int barColor[][] = {{51, 118, 191}, {51, 118, 191}, {51, 118, 191}, {51, 118, 191}, {51, 118, 191}, {237, 79, 52}, {237, 79, 52}, {237, 79, 52}, {237, 79, 52}, {237, 79, 52}};
        float barColorAlpha[] = {0.6f, 0.7f, 0.8f, 0.9f, 1.0f, 1.0f, 0.9f, 0.8f, 0.7f, 0.6f};
        for(int i=0; i<_cdm.accrueNames.length; i++) {
            if(i>10)
                break;
            //xPos 계산
            String strValue;
            if(!_cdm.accrueNames[i].equals(""))
                strValue = _cdm.accrueNames[i].substring(0,2);
            else
                strValue = "";

            int nFontSize = (int)COMUtil.getPixel(12);
            float strWidth = _cvm.getFontWidth(String.valueOf(strValue), nFontSize);
            float strXPos = 0;
            if(strWidth >= unitW)
                strXPos = 0;
            else {
                strXPos = (int)(unitW/2) - (int)(strWidth/2);
            }
            if(dDatas[i]>=0) {
                float gHeight = (float)(yfactor*(float)dDatas[i]);
//                if(gHeight<1)
//                    gHeight = 1;
//        		_cvm.drawFillRect(gl, (float)xPos, gijunY-gHeight, (float)unitW, gHeight, CoSys.CHART_COLORS[0], 1);

                //2016. 3. 11 막대 바차트 주어진 타이틀에 따라 각각 색상 다르게 하기>>
                _cvm.drawFillRect(gl, (float)xPos+gab/2, gijunY-gHeight, (float)unitW, gHeight, barColor[i] , barColorAlpha[i]);

                //2016. 3. 11 막대 바차트 주어진 타이틀에 따라 각각 색상 다르게 하기<<
                int[] textColor = {17, 17, 17};

                _cvm.drawStringWithSize(gl, textColor, xPos+gab/2+unitW/2-strWidth/2-(int)COMUtil.getPixel(1), (int)(gijunY-gHeight)-(int)COMUtil.getPixel(10), nFontSize, strValue);

            }

            xPos = (int)(xPos+unitW+gab);
        }

    }

    private void drawDot(Canvas gl, float x, float y, float w){
        //_cvm.drawCircle(gl, x+2,y-w,w-4, false, upColor);
        _cvm.drawCircle(gl, x+2,y-w,x+w-2, x+w-2, false, upColor);
    }

    AlertDialog alert;
    private void drawBar(Canvas gl, float x, float y, float w, float h,int[] col){
        if(h==0) {//h가 0인 경우 그래프를 보이기 위함.
            y-=2;
            h=2;
        }
        if(h==1) {//h가 0인 경우 그래프를 보이기 위함.
            y-=1;
            h=2;
        }
        switch(getDrawType2()){
            case 0://일반식
                _cvm.drawFillRect(gl,x,y,w,h,col, 1.0f);
                break;
            case 2://상하식
                float sp = calcy(stand[0]);
                if(sp>max_view)sp=max_view;
                drawOscillator(gl,x,y,w,h,(int)sp);
                break;
            case 3://빗형식
                drawUpDownRect(gl,x,y,w,h,(int)calcy(stand[0]));
                break;
            case 5://도트형
                drawDot(gl,x,y,w);
                break;

        }
    }
    public boolean isSelected(Point p, int index){
        if(data==null) return false;
        int idx= _cvm.getIndex();
        int curIndex = index-idx;
        if(curIndex >= data.length||curIndex<0) return false;
        float curY = calcy(data[curIndex]);
        switch(getDrawType2()){
            case 0:
            case 3:
            case 4:
                if( p.y>=curY )return true;
                return false;
            case 2://상하식
                float x=getBounds().left+xw;
                float sp=max_view;
                x+=(xfactor*curIndex);
                return isSelectedOscillator((int)(x-xw),(int)curY,(int)(2*xw),sp-(int)curY,(int)calcy(0),p);
        }
        return false;
    }
    private boolean isSelectedOscillator(float x, float y, float w, float h, float sp,Point p){
        RectF bound;
        w=(w>1)?w:1;
        if(y<sp){
            bound=new RectF(x-1,y,w+2,sp-y);
        }else{
            bound=new RectF(x-1,sp,w+2,y-sp);
        }
        if(bound.contains(p.x, p.y))return true;
        else return false;
    }
    private void drawBongGraph2(Canvas gl){
//        String[] titles = _cdm.accrueNames;
        int count = 0;
//        if(titles != null)
//            count = titles.length;
//
//        if (count <= 0) {
//            return;
//        }

        double[] dataArr1 = _cdm.getSubPacketData("data1");
        double[] dataArr2 = _cdm.getSubPacketData("data2");

        if (dataArr1 == null || dataArr2 == null) {
            return;

        }

//        if (count != dataArr1.length || count != dataArr2.length) {
//            return;
//        }

        count = dataArr1.length;

        double maxData = Double.NEGATIVE_INFINITY;
        double minData = Double.POSITIVE_INFINITY;

        for (int i = 0; i < count; i++) {
            if (dataArr1[i] > maxData) {
                maxData = dataArr1[i];
            }

            if (dataArr2[i] > maxData) {
                maxData = dataArr2[i];
            }


            if (dataArr1[i] < minData) {
                minData = dataArr1[i];
            }

            if (dataArr2[i] < minData) {
                minData = dataArr2[i];
            }
        }

        RectF bounds = getBounds();
        RectF bounds2 = _cvm.getBounds();

        float titleAreaHeight = COMUtil.getPixel(30);
        float barAreaHeight = bounds2.height() - titleAreaHeight;
        float yfactor = (float) (barAreaHeight / maxData);
        float xScaleY = bounds2.height() - titleAreaHeight;
        float fBaseLineY = xScaleY;

        if (minData < 0) {
            yfactor = (float) (barAreaHeight / (maxData - minData));
            fBaseLineY = (float) (xScaleY - yfactor * Math.abs(minData));
        }

        if (minData < 0) {
            if (maxData < 0) {
                yfactor = barAreaHeight / (float) Math.abs(minData);
                fBaseLineY = 0; //xScaleY - (float) (yfactor * Math.abs(minData));
            }
            else {
                yfactor = barAreaHeight / (float) (maxData - minData);
                fBaseLineY = (float) (xScaleY - yfactor * Math.abs(minData));
            }
        }

        //Draw Bar
        int barWidth = (int) COMUtil.getPixel(7);
        float xPos = bounds.left + xw - barWidth/2;
        int barHeight = 0;



        //Rounded Rect Corners
        float[] corners = new float[]{
                COMUtil.getPixel(100.f), COMUtil.getPixel(100.f),
                COMUtil.getPixel(100.f), COMUtil.getPixel(100.f),
                0, 0,
                0, 0
        };

        for (int i = 0; i < count; i++) {
            //Bar1
            int[] barColor1 = upColor;

            barHeight = (int) (dataArr1[i] * yfactor);
            _cvm.drawFillRoundedRect(gl, xPos, fBaseLineY, barWidth, -barHeight, barColor1, 1, corners);

            xPos = xPos + xfactor;
        }

        //Draw Base Line
        _cvm.setLineWidth(1);
        _cvm.drawLine(gl, bounds.left, fBaseLineY, bounds.right, fBaseLineY, new int[]{230, 230, 230} ,1.0f);

        //Draw XScale Line
//        _cvm.setLineWidth(1);
//        _cvm.drawLine(gl, bounds.left, xScaleY, bounds.right, xScaleY, new int[]{215, 215, 215} ,1.0f);
    }
    private void drawBongGraph3(Canvas gl){
        if (_cdm.accrueNames == null)
            return;
        String[] titles = _cdm.accrueNames;
        int count = titles.length;

        if (count <= 0) {
            return;
        }

        double[] dataArr1 = _cdm.getSubPacketData("data1");
        double[] dataArr2 = _cdm.getSubPacketData("data2");
        double[] dataArr3 = _cdm.getSubPacketData("data3");

        if (dataArr1 == null || dataArr2 == null || dataArr3 == null) {
            return;
        }

        if (count != dataArr1.length || count != dataArr2.length || count != dataArr3.length) {
            return;
        }

        double maxData = Double.NEGATIVE_INFINITY;
        double minData = Double.POSITIVE_INFINITY;

        for (int i = 0; i < count; i++) {
            if (dataArr1[i] > maxData) {
                maxData = dataArr1[i];
            }

            if (dataArr2[i] > maxData) {
                maxData = dataArr2[i];
            }

            if (dataArr3[i] > maxData) {
                maxData = dataArr3[i];
            }

            if (dataArr1[i] < minData) {
                minData = dataArr1[i];
            }

            if (dataArr2[i] < minData) {
                minData = dataArr2[i];
            }

            if (dataArr3[i] < minData) {
                minData = dataArr3[i];
            }
        }

        RectF bounds = _cvm.getBounds();
        RectF bounds2 = getBounds();

        float titleAreaHeight = COMUtil.getPixel(22);
        float barAreaHeight = bounds.height() - titleAreaHeight - bounds2.top;
        float yfactor = (float) (barAreaHeight / maxData);
        float xScaleY = bounds.height() - titleAreaHeight;
        float fBaseLineY = xScaleY;

        if (minData < 0) {
            yfactor = (float) (barAreaHeight / (maxData - minData));
            fBaseLineY = (float) (xScaleY - yfactor * Math.abs(minData));
        }

        if (minData < 0) {
            if (maxData < 0) {
                yfactor = barAreaHeight / (float) Math.abs(minData);
                fBaseLineY = 0; //xScaleY - (float) (yfactor * Math.abs(minData));
            }
            else {
                yfactor = barAreaHeight / (float) (maxData - minData);
                fBaseLineY = (float) (xScaleY - yfactor * Math.abs(minData));
            }
        }


        //Draw Bar
        int barWidth = (int) COMUtil.getPixel(5);
        float marginRight = COMUtil.getPixel(28);
        float xPos = bounds2.left + xw - barWidth/3;
        int barHeight = 0;
        float barGab = COMUtil.getPixel(3);
        float groupGab = (bounds2.width() - marginRight - (((barWidth * 3) + barGab*2) * count)) / count;

        int[] fontColor = new int[]{171, 171, 171};
        int[] barColor1_nor = new int[]{0, 149, 160};
        int[] barColor2_nor = new int[]{81, 193, 241};
        int[] barColor3_nor = new int[]{66, 121, 214};

        int fontSize = (int) COMUtil.getPixel(11);

        //Rounded Rect Corners
        float[] corners = new float[]{
                COMUtil.getPixel(100.f), COMUtil.getPixel(100.f),
                COMUtil.getPixel(100.f), COMUtil.getPixel(100.f),
                0, 0,
                0, 0
        };
        float[] corners2 = new float[]{
                0, 0,
                0, 0,
                COMUtil.getPixel(100.f), COMUtil.getPixel(100.f),
                COMUtil.getPixel(100.f), COMUtil.getPixel(100.f)
        };

        for (int i = 0; i < count; i++) {
            String strTitle = _cdm.accrueNames[i];
            float stringWidth = _cvm.getFontWidth(strTitle, fontSize);
            int stringPos = (int)((barWidth * 3 + barGab*2) / 2 - stringWidth / 2);
            _cvm.drawStringWithSize(gl, fontColor, xPos + stringPos, (int) (xScaleY + COMUtil.getPixel(16)), fontSize, strTitle);

            //Bar1
            int[] barColor1 = barColor1_nor;

            barHeight = (int) (dataArr1[i] * yfactor);
            if (barHeight<0)
                _cvm.drawFillRoundedRect(gl, xPos, fBaseLineY, barWidth, -barHeight, barColor1, 1, corners2);
            else
                _cvm.drawFillRoundedRect(gl, xPos, fBaseLineY, barWidth, -barHeight, barColor1, 1, corners);

            xPos = xPos + barWidth + barGab;

            //Bar2
            int[] barColor2 = barColor2_nor;

            barHeight = (int) (dataArr2[i] * yfactor);
            if (barHeight<0)
                _cvm.drawFillRoundedRect(gl, xPos, fBaseLineY, barWidth, -barHeight, barColor2, 1, corners2);
            else
                _cvm.drawFillRoundedRect(gl, xPos, fBaseLineY, barWidth, -barHeight, barColor2, 1, corners);

            xPos = xPos + barWidth + barGab;

            //Bar3
            int[] barColor3 = barColor3_nor;

            barHeight = (int) (dataArr3[i] * yfactor);
            if (barHeight<0)
                _cvm.drawFillRoundedRect(gl, xPos, fBaseLineY, barWidth, -barHeight, barColor3, 1, corners2);
            else
                _cvm.drawFillRoundedRect(gl, xPos, fBaseLineY, barWidth, -barHeight, barColor3, 1, corners);

            xPos = xPos + barWidth + groupGab;
        }

        //Draw Base Line
        _cvm.setLineWidth(1);
//        _cvm.drawLine(gl, bounds2.left, COMUtil.getPixel(5), bounds2.right, COMUtil.getPixel(5), new int[]{230, 230, 230} ,1.0f);
        _cvm.drawLine(gl, bounds2.left, fBaseLineY, bounds2.right, fBaseLineY, new int[]{230, 230, 230} ,1.0f);

        //Draw XScale Line
//        _cvm.setLineWidth(1);
//        _cvm.drawLine(gl, bounds.left, xScaleY, bounds.right, xScaleY, new int[]{215, 215, 215} ,1.0f);
    }

    private void drawBongGraph4(Canvas gl){
        if (_cdm.accrueNames == null)
            return;
        String[] titles = _cdm.accrueNames;
        int count = titles.length;

        if (count <= 0) {
            return;
        }

        double[] dataArr1 = _cdm.getSubPacketData("data1");

        if (dataArr1 == null) {
            return;

        }

        if (count != dataArr1.length) {
            return;
        }

        double maxData = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < count; i++) {
            if (dataArr1[i] > maxData) {
                maxData = dataArr1[i];
            }
        }

        RectF bounds = _cvm.getBounds();

        float titleAreaWidth = COMUtil.getPixel(20);
        float titleAreaHeight = COMUtil.getPixel(20);
        float marginRight = COMUtil.getPixel(20) + COMUtil.getPixel(45);
        float marginBottom = COMUtil.getPixel(20) ;
        float barAreaWidth = bounds.width() - titleAreaWidth - marginRight;
        float barAreaHeight = bounds.height() - titleAreaHeight - marginBottom;
        float xfactor = (float) (barAreaWidth / maxData);
        float yfactor = (float) (barAreaHeight / count);

        float yPos = titleAreaHeight;
        float gab = COMUtil.getPixel(4);
        float groupGab = 0.57f*yfactor;
        float unitTextH = COMUtil.getPixel(18);
        float unitH = 0.21f*yfactor;

        if(count > 8)
        {
            groupGab = 0.45f*yfactor;
            unitH = 0.125f*yfactor;
        }

        if(count>3)
            yfactor -= (unitTextH+unitH-(10-count)*COMUtil.getPixel_H(2));   //2020.06.12 by LYH >> 침범 오류

        float fundBarWidth = 0;
        float marketBarWidth = 0;

        int[] fontColor = new int[]{119, 119, 119};
        int[] fontColor2 = new int[]{0, 0, 0};
        int[] barColor1 = new int[]{72, 201, 207};

        //Rounded Rect Corners
        float[] corners = new float[]{
                0, 0,
                COMUtil.getPixel(100.f), COMUtil.getPixel(100.f),
                COMUtil.getPixel(100.f), COMUtil.getPixel(100.f),
                0, 0
        };

        for (int i = 0; i < count; i++) {
            //Title - Y+(n)의 n은 텍스트 크기에 따라 변경될 수 있음
            _cvm.drawStringWithSize(gl, fontColor, titleAreaWidth, (int) yPos, (int) COMUtil.getPixel(12), titles[i]);

            yPos = yPos + unitTextH;

            //rate
            fundBarWidth = (float) (dataArr1[i] * xfactor);
            float stringHeight = _cvm.getFontHeight(dataArr1[i]+"%",(int) COMUtil.getPixel(12));
            _cvm.drawStringWithSize(gl, fontColor2, titleAreaWidth + fundBarWidth + (int) COMUtil.getPixel(6),(int)(yPos+gab+unitH/2-stringHeight/2), (int) COMUtil.getPixel(12),dataArr1[i]+"%");

//            yPos = yPos + gab;

            //Bar1

            _cvm.drawFillRoundedRect(gl, titleAreaWidth, yPos, fundBarWidth, unitH, barColor1, 1, corners);

//            yPos = yPos + unitH + groupGab;
            yPos = yPos + unitH + yfactor;
        }
    }

    /**
     *  상단에만 텍스트 표시하고, 첫번째 바만 색상이 다른 바차트
     *  @param gl 그릴때 사용할 canvas
     *  @param dDatas 그래프를 그릴 데이터
     *  @param strUnitWord 그래프 상단 문자열 마지막에 표시할 단위 (ex: %, 원 등등..)
     *  */
    private void drawUpRectWithText(Canvas gl, double[] dDatas, String strUnitWord){
        if(dDatas==null) {
            return;
        }

        RectF chart_bounds = getBounds();
        int nTotCnt = dDatas.length;
        if(nTotCnt<3)
            return;

        float nPreMaxView = max_view;
        double dStandardValue = dDatas[0];
        for(int i=1; i<nTotCnt; i++) {
            if(dDatas[1]<dStandardValue)
            {
                max_view -= COMUtil.getPixel_H(25);
                break;
            }
        }
        float nHeight = (max_view-min_view);
        //float yfactor = (nHeight*1.0f)/(float)(max_data-min_data);
        yfactor = (nHeight*1.0f)/(float)(max_data-min_data);

        float gijunY = calcy(dStandardValue);

        int gab = (int)COMUtil.getPixel_W(8);
        int xPos = (int)COMUtil.getPixel_W(45);
        int unitW = (int)COMUtil.getPixel_W(8);

        _cvm.setLineWidth_Fix(COMUtil.getPixel(1));
        int[] lineColor={238, 238, 238};//스케일 텍스트 칼라
        _cvm.drawLine(gl, xPos, gijunY, xPos+(int)COMUtil.getPixel_W(280), gijunY, lineColor ,1.0f);
        _cvm.drawLine(gl, xPos+(int)COMUtil.getPixel_W(280-12.8f), gijunY-(int)COMUtil.getPixel_W(7.5f), xPos+(int)COMUtil.getPixel_W(280), gijunY, lineColor ,1.0f);
        _cvm.drawLine(gl, xPos+(int)COMUtil.getPixel_W(280-12.8f), gijunY+(int)COMUtil.getPixel_W(7.5f), xPos+(int)COMUtil.getPixel_W(280), gijunY, lineColor ,1.0f);

        int[] lineColorScale={245, 245, 245};
        if(max_view != nPreMaxView)
        {
            int nRowHeight = (int)(COMUtil.getPixel(25)+nHeight)/7;
            for(int i=0; i<7; i++)
            {
                if(gijunY-(nRowHeight*(i+1)) > min_view-1)
                    _cvm.drawLine(gl, xPos, gijunY-(nRowHeight*(i+1)), xPos+(int)COMUtil.getPixel_W(280), gijunY-(nRowHeight*(i+1)), lineColorScale ,0.5f);
                if(gijunY+(nRowHeight*(i+1)) < nPreMaxView+1)
                    _cvm.drawLine(gl, xPos, gijunY+(nRowHeight*(i+1)), xPos+(int)COMUtil.getPixel_W(280), gijunY+(nRowHeight*(i+1)), lineColorScale ,0.5f);
            }
        }
        else
        {
            for(int i=0; i<7; i++)
            {
                _cvm.drawLine(gl, xPos, gijunY-(nHeight/7*(i+1)), xPos+(int)COMUtil.getPixel_W(280), gijunY-(nHeight/7*(i+1)), lineColorScale ,0.5f);
            }
        }

        //Rounded Rect Corners
        float[] corners = new float[]{
                COMUtil.getPixel(100.f), COMUtil.getPixel(100.f),
                COMUtil.getPixel(100.f), COMUtil.getPixel(100.f),
                0, 0,
                0, 0
        };
        float[] corners2 = new float[]{
                0, 0,
                0, 0,
                COMUtil.getPixel(100.f), COMUtil.getPixel(100.f),
                COMUtil.getPixel(100.f), COMUtil.getPixel(100.f)
        };
        int nFontSize = 10;
        float strNameWidth = 0;
        String strValue = "";
        for(int i=0; i<dDatas.length; i++) {
            strValue = COMUtil.format(String.valueOf(dDatas[i]), 0, 3)+strUnitWord;
            //xPos 계산
            float strWidth = _cvm.getFontWidth(strValue, (int)COMUtil.getPixel(14));
            int strXPos = xPos - (int)(strWidth-unitW)/2;
            if(strXPos < 0)
                strXPos = 0;

            int nXScaleTextPos = xPos - (int)COMUtil.getPixel_W(12);
            int[] scaleTextColor={45, 45, 45};

            float gHeight;
            int[] arBongColor;
            int[] arTextColor = CoSys.CHART_COLORS[19];

            if(i==0 || i==2 || dDatas[i]>=dStandardValue) {
                if(i==0)
                {
                    arTextColor = CoSys.BLACK;
                    strXPos = xPos;
                    nXScaleTextPos = xPos;
                }
                else if(i==2)
                {
                    int[] color={13, 178, 169};
                    arTextColor = color;
//                    strXPos = xPos - (int)(strWidth-unitW);
                }
                else
                {
                    arTextColor = CoSys.CHART_COLORS[0];
                }
                arBongColor = arTextColor;

                gHeight = (float)(yfactor*(float)(dDatas[i]-dStandardValue));
                if(gHeight<1)
                    gHeight = 1;

                _cvm.drawFillRoundedRect(gl, (float)xPos, gijunY-gHeight, (float)unitW, gHeight, arBongColor, 1, corners);
                _cvm.drawStringWithSizeFont(gl, arTextColor, strXPos,(int)(gijunY-gHeight)-(int)COMUtil.getPixel_H(12), (int)COMUtil.getPixel(15), strValue, COMUtil.numericTypeface);
                strValue = _cdm.accrueNames[i];
                _cvm.drawStringWithSize(gl, scaleTextColor, nXScaleTextPos,(int)chart_bounds.bottom+(int)COMUtil.getPixel_H(18), (int)COMUtil.getPixel(12), strValue);
                if(i==0 && _cdm.accrueNames.length>nTotCnt)
                {
                    int[] color={169, 169, 169};
                    arTextColor = color;
                    strValue = _cdm.accrueNames[nTotCnt];
                    strWidth = _cvm.getFontWidth(_cdm.accrueNames[i], (int)COMUtil.getPixel(12));
                    _cvm.drawStringWithSize(gl, arTextColor, nXScaleTextPos+strWidth+COMUtil.getPixel_W(5),(int)chart_bounds.bottom+(int)COMUtil.getPixel_H(18), (int)COMUtil.getPixel(12), strValue);
                }
            }
            else if(dDatas[i]<dStandardValue) {

                //첫번째 봉만 색깔 다르게 처리
                arTextColor = CoSys.CHART_COLORS[1];
                arBongColor = arTextColor;
                gHeight = (float)(yfactor*(float)Math.abs(dDatas[i]-dStandardValue));
                if(gHeight<1)
                    gHeight = 1;

                _cvm.drawFillRoundedRect(gl, (float)xPos, gijunY, (float)unitW, gHeight, arBongColor, 1, corners2);
                _cvm.drawStringWithSizeFont(gl, arTextColor, strXPos,(int)(gijunY+gHeight)+(int)COMUtil.getPixel_H(12), (int)COMUtil.getPixel(15), strValue, COMUtil.numericTypeface);
                strValue = _cdm.accrueNames[i];
                _cvm.drawStringWithSize(gl, scaleTextColor, nXScaleTextPos,(int)chart_bounds.bottom+(int)COMUtil.getPixel_H(18), (int)COMUtil.getPixel(12), strValue);
            }

            if(i==0)
                xPos += COMUtil.getPixel_W(128)+unitW;
            else if(i==1)
                xPos += COMUtil.getPixel_W(90)+unitW;
        }
        max_view = nPreMaxView;
    }
    //2015. 9. 11 자산관리 퍼센트/바 디자인<<

    public float getDateToX(int nIndex) {
        int nStart = _cvm.getIndex();

        return (((nIndex-nStart) * xfactor)+this.getBounds().left+xw);
    }
    //2017.09.25 by LYH >> 자산 차트 적용 end
    //2019.08.12 by LYH >> 업종 그리드 바차트 추가 Start
    private void drawGridBarGraph(Canvas gl, double[] dDatas1){
        String[] nameDatas = _cdm.accrueNames;
        if(nameDatas==null) {
            return;

        }
        double[] dDatas = _cdm.getSubPacketData("data1");
        if(nameDatas.length != dDatas.length)
        {
            return;
        }

        RectF chart_bounds = getBounds();

        int nTotCnt = dDatas.length;

        double minData = Integer.MAX_VALUE;
        double maxData = Integer.MIN_VALUE;

        for (int i = 0; i < dDatas.length; i++) {
            //dDatas[i] = datas[i];

            if (dDatas[i] < minData)
                minData = dDatas[i];
            if (dDatas[i] > maxData)
                maxData = dDatas[i];
        }

        if(Math.abs(maxData)>Math.abs(minData))
            minData = -1*maxData;
        else
            maxData = -1*minData;

        if(maxData==0)
        {
            maxData = 1;
            minData = -1;
        }

        int nTitleWidth = (int)COMUtil.getPixel_W(106);
        int nMargin = (int)COMUtil.getPixel_W(8);
        float nWidth = getBounds().width()-nTitleWidth+nMargin*2;
        float xfactor = (nWidth*1.0f)/(float)(maxData-minData);

        int[] barColor = {8, 172, 145};
        int[] colorLine = {238, 238, 238};
        int[] colorText = {17, 17, 17};
        //차트 그리기
        //기준선 그리기
        float fHorizontalMargin = COMUtil.getPixel(0);
        float cLRate = (float)(Math.abs(minData) / (Math.abs(minData) + Math.abs(maxData)));
        float gijunX = nTitleWidth+nMargin + nWidth - cLRate * nWidth;
        if(_cvm.bGreenType)
        {
            minData = 0;
            nWidth = getBounds().width()-nTitleWidth+COMUtil.getPixel_W(80);
            xfactor = (nWidth*1.0f)/(float)(maxData-minData);
            gijunX = nTitleWidth;
        }

        _cvm.setLineWidth_Fix(COMUtil.getPixel(1));
        _cvm.drawLine(gl, 0, 0, _cvm.getBounds().width(), 0, colorLine, 1.0f);

        _cvm.setLineWidth(1);
        _cvm.drawLine(gl, nTitleWidth, 0, nTitleWidth, chart_bounds.height(), colorLine, 1.0f);

        if(!_cvm.bGreenType) {
            float fLineWidth = (gijunX - nTitleWidth) / 3;
            for (int i = 0; i < 5; i++) {

                if (i == 2)
                    _cvm.drawLine(gl, gijunX + (i - 2) * fLineWidth, 0, gijunX + (i - 2) * fLineWidth, chart_bounds.height(), colorLine, 1.0f);
//                else
//                    _cvm.drawDashDotDotLine(gl, gijunX + (i - 2) * fLineWidth, 0, gijunX + (i - 2) * fLineWidth, chart_bounds.height(), colorLine, 1.0f);
            }
        }

        if(nTotCnt<=0)
            return;

        float xPos;
        float fBongWidth;

        int yGap = (int)COMUtil.getPixel_H(20);
        int yPos = (int)(chart_bounds.top + COMUtil.getPixel_H(10));
        int unitH = (int)COMUtil.getPixel_H(20);

        int nNameTextSize = (int) COMUtil.getPixel(13);
        for(int i=0; i<nTotCnt; i++) {
            int nIndex = i;
            //_cvm.drawStringWithSize(gl, _cvm.CST, chart_bounds.left+_cvm.Margin_L+(int)COMUtil.getPixel(8),(int)(COMUtil.getPixel(18)+i*COMUtil.getPixel(33)), (int)COMUtil.getPixel(15), nameDatas[nIndex]);
            if(_cvm.m_titlePos == Gravity.CENTER) {
                int nCenterX = (int)(chart_bounds.left + (nTitleWidth-_cvm.getFontWidth(nameDatas[nIndex], (int)COMUtil.getPixel(14)))/2);
                _cvm.drawStringWithSize(gl, colorText, nCenterX, (int) (yPos + COMUtil.getPixel(8)), (int) COMUtil.getPixel(14), nameDatas[nIndex]);
            }
            else
                _cvm.drawStringWithSize(gl, colorText, chart_bounds.left+_cvm.Margin_L+(int)COMUtil.getPixel(8),(int)(yPos+COMUtil.getPixel(8)), (int)COMUtil.getPixel(14), nameDatas[nIndex]);
            int color[];

            float xClose = gijunX;
            if (dDatas[nIndex] != 0)
                xClose += (float) (xfactor * (float) dDatas[nIndex]);

            if(_cvm.bGreenType) {
                xPos = fHorizontalMargin + gijunX;
                fBongWidth = xClose - gijunX;
            }
            else
            {
                if (dDatas[nIndex] <= 0) {
                    barColor = CoSys.CHART_COLORS[1];
                    xPos = fHorizontalMargin + xClose;
                    fBongWidth = gijunX - xClose;
                } else {
                    barColor = CoSys.CHART_COLORS[0];
                    xPos = fHorizontalMargin + gijunX;
                    fBongWidth = xClose - gijunX;
                }
            }

            if (fBongWidth < 1) {
                fBongWidth = 1;
            }

            String strValue;
            int nDataFormat = _cdm.getDataFormat("data1");
            if(nDataFormat==14) {
                if(_cvm.bGreenType)
                    strValue = COMUtil.format(String.valueOf(dDatas[nIndex]), 2, 3) + "%";
                else
                    strValue = COMUtil.format(String.valueOf(dDatas[nIndex]), 2, 3);
            }
            else
                strValue = COMUtil.format(String.valueOf(dDatas[nIndex]), 0, 3);
//            if(dDatas[nIndex]>0)
//            {
//                strValue = "+"+strValue;
//            }
            float fNameTextWidth = _cvm.getFontWidth(strValue, nNameTextSize);
            _cvm.drawFillRect(gl, xPos, yPos, fBongWidth, unitH, barColor, 1);

            if(_cvm.bGreenType)
            {
                _cvm.drawStringWithSize(gl, colorText, (int) (xPos+fBongWidth+COMUtil.getPixel_W(8)), (int) yPos + COMUtil.getPixel_H(8), nNameTextSize, strValue);
            }
            else {
                if (dDatas[nIndex] > 0)
                    _cvm.drawStringWithSize(gl, CoSys.CHART_COLORS[0], (int) (gijunX - fNameTextWidth - COMUtil.getPixel(10)), (int) yPos + COMUtil.getPixel_H(8), nNameTextSize, strValue);
                else
                    _cvm.drawStringWithSize(gl, CoSys.CHART_COLORS[1], (int) (gijunX + COMUtil.getPixel(10)), (int) yPos + COMUtil.getPixel_H(8), nNameTextSize, strValue);
            }
            _cvm.drawLine(gl, chart_bounds.left, (float) yPos + unitH + yGap / 2, chart_bounds.right, (float) yPos + unitH + yGap / 2, colorLine, 1.0f);
            yPos = yPos+unitH+yGap;
        }
        _cvm.drawLine(gl, 0, chart_bounds.height(), _cvm.getBounds().width(), chart_bounds.height(), CoSys.LIST_LN_COLOR, 1.0f);
    }
    //2019.08.12 by LYH >> 업종 그리드 바차트 추가 End

    private void drawGridFutOptGraph(Canvas gl) {
        String[] nameDatas = _cdm.accrueNames;
        double[] dDatas1 = _cdm.getSubPacketData("data1");
        double[] dDatas2 = _cdm.getSubPacketData("data2");
        double[] dDatas3 = _cdm.getSubPacketData("data3");

        int nDataFormat = _cdm.getDataFormat("data1");
//        double[] openDatas = _cdm.accrueOpens;
//        double[] highDatas = _cdm.accrueHighs;
//        double[] lowDatas = _cdm.accrueLows;
//        double[] datas = _cdm.accrueCloses;
//        double[] baseDatas = _cdm.accrueBases;

        if(nameDatas == null || dDatas1==null) {
            return;
        }
        if(nameDatas.length != dDatas1.length)
        {
            return;
        }

        RectF chart_bounds = getBounds();

        int nTotCnt = dDatas1.length;


        double minData1 = Integer.MAX_VALUE;
        double maxData1 = Integer.MIN_VALUE;
        double minData2 = Integer.MAX_VALUE;
        double maxData2 = Integer.MIN_VALUE;

        for (int i = 0; i < dDatas1.length; i++) {

            if (dDatas1[i] < minData1)
                minData1 = dDatas1[i];
            if (dDatas1[i] > maxData1)
                maxData1 = dDatas1[i];

            if (dDatas2[i] < minData2)
                minData2 = dDatas2[i];
            if (dDatas2[i] > maxData2)
                maxData2 = dDatas2[i];
        }

        if(Math.abs(maxData1)>Math.abs(minData1))
            minData1 = -1*maxData1;
        else
            maxData1 = -1*minData1;

        if(maxData1==0)
        {
            maxData1 = 1;
            minData1 = -1;
        }
        if(Math.abs(maxData2)>Math.abs(minData2))
            minData2 = -1*maxData2;
        else
            maxData2 = -1*minData2;

        if(maxData2==0)
        {
            maxData2 = 1;
            minData2 = -1;
        }
        minData1 = 0;
        minData2 = 0;
//        float nHeight = (max_view-min_view);
//        float yfactor = (nHeight*1.0f)/(float)(maxData-minData);
        float nTitleWidth = COMUtil.getPixel_W(60);
        float nWidth = (getBounds().width()-nTitleWidth*3)/2-COMUtil.getPixel(12); //2018. 12. 11 by hyh - 업종/섹트 가로선 위치 조정. 115->145
        float xfactor1 = (nWidth*1.0f)/(float)(maxData1-minData1);
        float xfactor2 = (nWidth*1.0f)/(float)(maxData2-minData2);

        //차트 그리기
        //기준선 그리기
//		float cHRate = (float)(Math.abs(maxData) / (Math.abs(minData) + Math.abs(maxData)));
//        float cLRate = (float)(Math.abs(minData1) / (Math.abs(minData1) + Math.abs(maxData1)));
//        float gijunX = COMUtil.getPixel(135) + nWidth - cLRate * nWidth; //2018. 12. 11 by hyh - 업종/섹트 가로선 위치 조정. 105->135

        int gab = (int)COMUtil.getPixel_H(30);
        int unitH = (int)COMUtil.getPixel_H(10);
        int yPos = (int)chart_bounds.top+(int)COMUtil.getPixel_H(14);

        _cvm.setLineWidth_Fix(COMUtil.getPixel(1));
//        _cvm.drawLine(gl, 0, 0, chart_bounds.height(), COMUtil.getPixel(1), CoSys.HEADER_LN_COLOR, 1.0f);
        _cvm.setLineWidth(1);

        if(nTotCnt<=0)
            return;

        int dLen = dDatas1.length;
        int nTitleXPos = (int)(chart_bounds.left+_cvm.Margin_L+chart_bounds.width()/2-(int)nTitleWidth/2);

        int[] colorBack = {250, 250, 250};
        int[] colorBackATM = {255,238, 157};
        int[] colorText = {17, 17, 17};
        int[] colorLine = {238, 238, 238};
        int nFontSize = (int)COMUtil.getPixel((float)13);
        for(int nIndex=0; nIndex<dLen; nIndex++) {
            String strValue;
            if(nDataFormat==14)
                strValue = COMUtil.format(String.valueOf(dDatas1[nIndex]), 2, 3);
            else
                strValue = COMUtil.format(String.valueOf(dDatas1[nIndex]), 0, 3);
             _cvm.drawStringWithSize(gl, colorText, chart_bounds.left+_cvm.Margin_L+(int)COMUtil.getPixel(8),(int)(yPos+COMUtil.getPixel_H(6)), nFontSize, strValue);
            //2018. 10. 30 by hyh - 업종 섹터 차트 타이틀 잘리는 현상 최소화 >>
            if(dDatas3 !=null && dDatas3[nIndex] == 1)
                _cvm.drawFillRect(gl, nTitleXPos, (float)yPos-gab/2+COMUtil.getPixel_H(1) , nTitleWidth, (float)unitH+gab-COMUtil.getPixel_H(1), colorBackATM, 1);
            else
                _cvm.drawFillRect(gl, nTitleXPos, (float)yPos-gab/2+COMUtil.getPixel_H(1) , nTitleWidth, (float)unitH+gab-COMUtil.getPixel_H(1), colorBack, 1);
            _cvm.drawStringWithSize(gl, colorText, nTitleXPos+(int)COMUtil.getPixel(8),(int)(yPos+COMUtil.getPixel_H(6)), nFontSize, nameDatas[nIndex]);

            if(nDataFormat==14)
                strValue = COMUtil.format(String.valueOf(dDatas2[nIndex]), 2, 3);
            else
                strValue = COMUtil.format(String.valueOf(dDatas2[nIndex]), 0, 3);
            int xPos = (int)(chart_bounds.right - (_cvm.getFontWidth(strValue, (int)COMUtil.getPixel(14)) + (int) COMUtil.getPixel(10)));
            _cvm.drawStringWithSize(gl, colorText, xPos,(int)(yPos+COMUtil.getPixel_H(6)), nFontSize, strValue);
            //_cvm.drawStringWithSize(gl, _cvm.CST, chart_bounds.left+_cvm.Margin_L+(int)COMUtil.getPixel(8),(int)(COMUtil.getPixel(18)+i*COMUtil.getPixel(33)), (int)COMUtil.getPixel((float)14.7), nameDatas[nIndex]);
            //2018. 10. 30 by hyh - 업종 섹터 차트 타이틀 잘리는 현상 최소화 <<

            int color[] = {8, 172, 145};
            if(nDataFormat == 14)
            {
                if(dDatas1[nIndex]>=0) {
                    color = CoSys.CHART_COLORS[0];
                }
                else
                {
                    color = CoSys.CHART_COLORS[1];
                }
            }
            float xData1 = nTitleXPos;
            if(dDatas1[nIndex] != 0)
                xData1 -= (float)(xfactor1*Math.abs(dDatas1[nIndex]));

            nWidth = nTitleXPos - xData1;
            if(nWidth <1)
                nWidth = 1;
            _cvm.drawFillRect(gl, xData1, (float)yPos , nWidth, (float)unitH, color, 1);

            if(nDataFormat == 14)
            {
                if(dDatas2[nIndex]>=0) {
                    color = CoSys.CHART_COLORS[0];
                }
                else
                {
                    color = CoSys.CHART_COLORS[1];
                }
            }
            xData1 = nTitleXPos+nTitleWidth;
            if(dDatas2[nIndex] != 0)
                xData1 += (float)(xfactor2*Math.abs(dDatas2[nIndex]));


            nWidth = xData1 - (nTitleXPos+nTitleWidth);
            if(nWidth <1)
                nWidth = 1;
            _cvm.drawFillRect(gl, nTitleXPos+nTitleWidth, (float)yPos , nWidth, (float)unitH, color, 1);

            _cvm.drawLine(gl, chart_bounds.left, (float)yPos+unitH+gab/2, chart_bounds.right, (float)yPos+unitH+gab/2, colorLine ,1.0f);
            yPos = yPos+unitH+gab;
        }
        _cvm.drawLine(gl, nTitleXPos, 0, nTitleXPos, chart_bounds.height(), CoSys.LIST_LN_COLOR, 1.0f);
        _cvm.drawLine(gl, nTitleXPos+nTitleWidth, 0, nTitleXPos+nTitleWidth, chart_bounds.height(), colorLine, 1.0f);
    }
}