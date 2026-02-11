package drfn.chart.draw;

import android.graphics.Canvas;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;
import drfn.chart.util.COMUtil;
import drfn.chart.util.ChartUtil;
import drfn.chart.util.CoSys;

/**
 P&F차트를 그리기 위한 드로우 툴
 */
public class PnFDraw extends DrawTool{
    int type=0;
    int topMargin = (int) COMUtil.getPixel_H(10); //2021.07.09 by hanjun.Kim - kakaopay
    int leftMargin = (int)COMUtil.getPixel_W(18);
    boolean isdot;

    public PnFDraw(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm, cdm);
        setIndex(getDrawType2());
    }
    public void setDotLine(boolean b){
        isdot = b;
    }
    public void setIndex(int idx){
        type = idx;
    }
    public void draw(Canvas gl, double y){
    }
    public void drawDefault(Canvas gl, double[] data){
    }
    public void draw(Canvas gl, double[] data, double[] stand){
    }
    public double makeUnit(int[] data){
        double sum=0;
        for(int i=0;i<data.length;i++){
            sum+=(double)data[i];
        }
        return sum/data.length;
    }
    public void draw(Canvas gl, double[] data){
        int viewH = (int)COMUtil.getPixel_H(35);
        if(data==null || data.length<2)return;
        boolean tp=false;
        boolean up=true;//상향
        //전환가격은 주가의 2%정도로 본다
        int format_org=_cdm.getDataFormat("종가");
        int nFloat = 1;
        if(format_org == 14 || format_org == 15 || format_org == 16)
            nFloat = 100;

        //2014.03.28 by LYH >> 소수점 데이터 P&F차트 개선.
        if(format_org > 1000)
        {
            nFloat = (int)Math.pow(10, (int)format_org%1000);
        }
        //2014.03.28 by LYH << 소수점 데이터 P&F차트 개선.
        int unit = (int)Math.max(((double)((max_data*nFloat-min_data*nFloat)*10/50)/10),10.0);
        //int unit = 4342;
        int nRect = 3;
        int p = 0;

        double dBaseData= data[0]*nFloat;
        double SecondData = data[1]*nFloat;
        if(SecondData >= dBaseData + unit)up = true;
        else if(SecondData <= dBaseData - unit)up = false;

        ////////////////////////////////////////////////////
        float w =(yfactor * unit)/nFloat;
        //float w=calcy(min_data)-calcy(min_data+unit);
        double last=data[0]*nFloat;

        float x = getBounds().left;
        //float y = calcy(data[0]*nFloat);
        float y = (float)data[0];
        double val;
        int dLen = data.length;
        int nPnfCount = 0;

        //2018.12 특수차트일때 수치조회창 수치 안나오는 오류 Start

        String[] dates = _cdm.getStringData("자료일자");
        String[] dateData = new String[dLen];
        double[] openData = new double[dLen];
        double fCloseData[]  = new double[dLen];
        if(dLen>0)
        {
            dateData[nPnfCount] = dates[0];
            fCloseData[nPnfCount] = data[0];
            openData[nPnfCount] = data[0];
        }
        //2018.12 특수차트일때 수치조회창 수치 안나오는 오류 End

        for(int i=1;i<dLen;i++){
            double idx = data[i]*nFloat-last;

            val=(double)(idx/unit);
            p=(int)Math.abs(val);

            if(idx>=unit && up){//상승추세에서 가격차가 단위가격보다 클때
                tp = true;
                last = data[i]*nFloat;
            }else if(idx>=unit && !up){//하락추세에서 가격차가 단위가격보다 상승했을때
                if(p>=nRect){
                    tp = true;
                    up = true;
                    nPnfCount++;
                    last = data[i]*nFloat;
                    //2018.12 특수차트일때 수치조회창 수치 안나오는 오류 Start
                    dateData[nPnfCount] = dates[i];
                    //2018.12 특수차트일때 수치조회창 수치 안나오는 오류 End
                }
            }else if(idx<=-unit && !up){
                tp = false;
                last = data[i]*nFloat;
            }else if(idx<=-unit && up){
                if(p>=nRect){
                    tp = false;
                    up = false;
                    nPnfCount++;
                    last = data[i]*nFloat;

                    //2018.12 특수차트일때 수치조회창 수치 안나오는 오류 Start
                    dateData[nPnfCount] = dates[i];
                    //2018.12 특수차트일때 수치조회창 수치 안나오는 오류 End
                }
            }
//            if(p!=0) y=drawPnf(gl,x,y,w,w,tp,data[i]);
        }
        tp=false;
        up=true;
        if(SecondData >= dBaseData + unit) {
            up = true;
        } else if(SecondData <= dBaseData-unit) {
            up = false;
        }
        last = data[0]*nFloat;
        x = getBounds().left;
        y = calcy(data[0]);
        double yPrice = data[0];

//        int xw = getBounds().width() / (nPnfCount+1);
        float xw = (float)getBounds().width() / (float)(nPnfCount+1); //2015.06.25 by lyk - int형에서 float형으로 변경
        nPnfCount = 0;
        for(int i=1;i<dLen;i++){
            double idx = data[i]*nFloat-last;

            val=(double)(idx/unit);
            p=(int)Math.abs(val);

            if(idx>=unit && up){//상승추세에서 가격차가 단위가격보다 클때
                tp = true;
                last = data[i]*nFloat;
            }else if(idx>=unit && !up){//하락추세에서 가격차가 단위가격보다 상승했을때
                if(p>=nRect){
                    tp = true;
                    up = true;
                    x+=xw;
                    y-=w;
                    fCloseData[nPnfCount] = yPrice;
                    nPnfCount++;
                    yPrice+=unit/nFloat;    //2021.12.01 by LYH - 소수점 데이터 P&F 시가 종가 가격 오류 수정
                    openData[nPnfCount] = yPrice;
                    last = data[i]*nFloat;
                }else{
                    p = 0;
                }
            }else if(idx<=-unit && !up){
                tp = false;
                last = data[i]*nFloat;
            }else if(idx<=-unit && up){
                if(p>=nRect){
                    tp = false;
                    up = false;
                    x+=xw;
                    y+=w;
                    fCloseData[nPnfCount] = yPrice;
                    nPnfCount++;
                    yPrice-=unit/nFloat;    //2021.12.01 by LYH - 소수점 데이터 P&F 시가 종가 가격 오류 수정
                    openData[nPnfCount] = yPrice;
                    last = data[i]*nFloat;
                }else{
                    p = 0;
                }
            }else{
                p = 0;
            }
            if(p!=0) {
                int nCount = drawPnf(gl, x, y, xw, w, tp, data[i]);
                y+=nCount*w;
                yPrice-=nCount*unit/nFloat;     //2021.12.01 by LYH - 소수점 데이터 P&F 시가 종가 가격 오류 수정
            }

            if(i==dLen-1)
            {
                fCloseData[nPnfCount] = yPrice;
            }
        }

        //2018.12 특수차트일때 수치조회창 수치 안나오는 오류 Start

        double[] openDataResult = new double[nPnfCount+1];
        double[] closeDataResult = new double[nPnfCount+1];

        System.arraycopy(openData, 0, openDataResult, 0, nPnfCount+1);
        _cdm.setSubPacketData("variable_open", openDataResult);

        System.arraycopy(fCloseData, 0, closeDataResult, 0, nPnfCount+1);
        _cdm.setSubPacketData("variable_close", closeDataResult);

        String[] dateDataResult = new String[nPnfCount+1];
        System.arraycopy(dateData, 0, dateDataResult, 0, nPnfCount+1);
        _cdm.setSubPacketData("variable_자료일자", dateDataResult);
        //2018.12 특수차트일때 수치조회창 수치 안나오는 오류 End

        //2014. 3. 27 독립차트에서 삼선전환도 이외에 차트유형의 오른쪽 데이터박스가 위로 튀는 현상>>
//      _cvm.drawFillRect(gl, getBounds().right-(int)COMUtil.getPixel(75),(int)COMUtil.getPixel(10),(int)COMUtil.getPixel(75),viewH, CoSys.DKGRAY, 0.5f);
        _cvm.drawFillRect(gl, getBounds().left+(int)COMUtil.getPixel_W(70) + leftMargin,(int)COMUtil.getPixel(03)+_cvm.Margin_T ,(int)COMUtil.getPixel_W(85),viewH, CoSys.DKGRAY, 0.5f);
        //2014. 3. 27 독립차트에서 삼선전환도 이외에 차트유형의 오른쪽 데이터박스가 위로 튀는 현상<<

//        _cvm.drawFillRect(gl, getBounds().right-(int)COMUtil.getPixel(75),(int)COMUtil.getPixel(10),(int)COMUtil.getPixel(75),viewH, CoSys.BLACK, 0.5f);	//2014. 3. 27 PNF 데이터박스 색상  다른 독립차트와 같게 연하게 색상변경
        double dData = ((double)unit)/nFloat;
        String strPrice = String.format("%.8f", dData);//한칸의 값.
        strPrice = ChartUtil.getFormatedData(strPrice, _cdm.getPriceFormat(), _cdm);
        _cvm.drawString(gl, CoSys.WHITE, getBounds().left+(int)COMUtil.getPixel_W(75) + leftMargin,(int)COMUtil.getPixel_H(3)+_cvm.Margin_T + topMargin, "1칸 : "+strPrice);       //1칸의 값.

//        if(nFloat==100)
//        {
//
//            String str1 = String.format("%.2f", dData);//한칸의 값.
//
//            //2014. 3. 27 독립차트에서 삼선전환도 이외에 차트유형의 오른쪽 데이터박스가 위로 튀는 현상>>
////          _cvm.drawString(gl, CoSys.WHITE, getBounds().right-(int)COMUtil.getPixel(70),viewH-(int)COMUtil.getPixel(10), "1BOX : "+str1);       //1칸의 값.
//            _cvm.drawString(gl, CoSys.WHITE, getBounds().left+(int)COMUtil.getPixel_W(50),(int)COMUtil.getPixel_H(20)+_cvm.Margin_T, "1칸 : "+str1);       //1칸의 값.
//            //2014. 3. 27 독립차트에서 삼선전환도 이외에 차트유형의 오른쪽 데이터박스가 위로 튀는 현상<<
//        }
//        else
//        {
//            //2014. 3. 27 독립차트에서 삼선전환도 이외에 차트유형의 오른쪽 데이터박스가 위로 튀는 현상>>
////    		_cvm.drawString(gl, CoSys.WHITE, getBounds().right-(int)COMUtil.getPixel(70),viewH-(int)COMUtil.getPixel(10), "1BOX : "+unit);       //1칸의 값.
//            _cvm.drawString(gl, CoSys.WHITE, getBounds().left+(int)COMUtil.getPixel_W(50),(int)COMUtil.getPixel_H(20)+_cvm.Margin_T, "1칸 : "+unit);       //1칸의 값.
//            //2014. 3. 27 독립차트에서 삼선전환도 이외에 차트유형의 오른쪽 데이터박스가 위로 튀는 현상<<
//        }

        //2014. 3. 27 독립차트에서 삼선전환도 이외에 차트유형의 오른쪽 데이터박스가 위로 튀는 현상>>
//        _cvm.drawString(gl, CoSys.WHITE, getBounds().right-(int)COMUtil.getPixel(70),viewH, "3BOX change");//3칸 전환.
        _cvm.drawString(gl, CoSys.WHITE, getBounds().left+(int)COMUtil.getPixel_W(75) + leftMargin,(int)COMUtil.getPixel_H(18)+_cvm.Margin_T + topMargin, "3칸 전환");//3칸 전환.
        //2014. 3. 27 독립차트에서 삼선전환도 이외에 차트유형의 오른쪽 데이터박스가 위로 튀는 현상<<
    }
    public void drawVolumeForSale(Canvas gl, double[] stand){//대기매물용	
    }
    public void draw(Canvas gl, double[][] data){//기준가 없이 그리는 바
    }
    public void draw(Canvas gl, double[][] data, double[] stand){
    }
    //P&F를 그리고 y좌표를 리턴한다
    //x,y : 초기 x, y좌표
    //xw,yw : x,y의 폭
    //b : O 인지 X인지
    //unit: 전환값
    //cnt: 몇칸전환인지..
    public int drawPnf(Canvas gl, float x, float y, float xw, float yw, boolean b,double price){
        float priceY = calcy(price);
        int nCount=0;
        if(b){
            while(y>priceY){
                //2023.06.02 by SJW - 다크테마 차트 색상 변경 >>
//                _cvm.drawLine(gl, (int) x, (int) y, (int) (x + xw), (int) (y - yw), CoSys.STANDGRAPH_BASE_COLOR, 1.0f);
//                _cvm.drawLine(gl, (int) x, (int) (y - yw), (int) (x + xw), (int) y, CoSys.STANDGRAPH_BASE_COLOR, 1.0f);
                if(_cvm.getSkinType() == COMUtil.SKIN_BLACK) {
                    _cvm.drawLine(gl, (int) x, (int) y, (int) (x + xw), (int) (y - yw), CoSys.CHART_COLOR_UP_NIGHT, 1.0f);
                    _cvm.drawLine(gl, (int) x, (int) (y - yw), (int) (x + xw), (int) y, CoSys.CHART_COLOR_UP_NIGHT, 1.0f);
                } else {
                    _cvm.drawLine(gl, (int) x, (int) y, (int) (x + xw), (int) (y - yw), CoSys.STANDGRAPH_BASE_COLOR, 1.0f);
                    _cvm.drawLine(gl, (int) x, (int) (y - yw), (int) (x + xw), (int) y, CoSys.STANDGRAPH_BASE_COLOR, 1.0f);
                }
                //2023.06.02 by SJW - 다크테마 차트 색상 변경 <<
                y-=yw;
                nCount--;
            }
        }else{
            while(y<priceY-yw){
                //_cvm.drawCircle(gl,(int)(x+xw/2), (int)y, (float)xw, (float)yw, false, CoSys.CHART_COLORS[1]);
                //2023.06.02 by SJW - 다크테마 차트 색상 변경 >>
//                _cvm.drawCircle(gl,(int)x, (int)y, x+xw, y+yw, false, CoSys.STANDGRAPH_BASE_COLOR1);
                if(_cvm.getSkinType() == COMUtil.SKIN_BLACK) {
                    _cvm.drawCircle(gl, (int) x, (int) y, x + xw, y + yw, false, CoSys.CHART_COLOR_DOWN_NIGHT);
                } else {
                    _cvm.drawCircle(gl, (int) x, (int) y, x + xw, y + yw, false, CoSys.STANDGRAPH_BASE_COLOR1);
                }
                //2023.06.02 by SJW - 다크테마 차트 색상 변경 <<
                y+=yw;
                nCount++;
            }
        }
        return nCount;
    }
}