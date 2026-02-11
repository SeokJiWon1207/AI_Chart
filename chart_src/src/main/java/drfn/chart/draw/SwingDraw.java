package drfn.chart.draw;

import android.graphics.Canvas;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;
import drfn.chart.util.COMUtil;
import drfn.chart.util.CoSys;
import drfn.chart.util.MinMax;

/**
 Swing차트를 그리기 위한 드로우 툴
 */
public class SwingDraw extends DrawTool{
    int type=0;
    boolean isdot;

    public SwingDraw(ChartViewModel cvm, ChartDataModel cdm){
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
    //int viewH = 20;
    public void draw(Canvas gl, double[] data){
        int viewH = (int)COMUtil.getPixel(20);
        if(data==null)return;
//        boolean tp=false;
//        boolean up=true;//상향
        //전환가격은 주가의 2%정도로 본다
//        int format_org=_cdm.getDataFormat("종가");

        double[] highData = _cdm.getSubPacketData("고가");
        double[] lowData = _cdm.getSubPacketData("저가");
        double max_data=MinMax.getIntMaxT(highData);
        double min_data=MinMax.getIntMinT(lowData);
        //int unit = (int)(((max_data-min_data)*2.36)/50);
        int unit = (int)(max_data-min_data)/23/2; //0.0115
        //int unit = 4342;
//        int nRect = 3;
//	    int p = 0;

        double dBaseData= data[0];


        //       double last=data[0];

        float x = getBounds().left;
        float y = calcy(data[0]);
        //       double val;
        int dataCnt = data.length;
        if(dataCnt<=1)
            return;

        double[] fSwingData = new double[dataCnt];
        //2015.06.25 by lyk - Kagi 자료일자 처리
        String[] dates = _cdm.getStringData("자료일자");
        String[] kagiDates = new String[dataCnt];
        String sEndDate = dates[0];
        //2015.06.25 by lyk - Kagi 자료일자 처리 end

        int nPnfCount = 0;
        fSwingData[nPnfCount] = dBaseData;
        kagiDates[nPnfCount] = dates[0];
        nPnfCount++;
        short nSignal = 0;	// 현재의 Swing Data 추세를 설정한다. -1: 하락, 0: 보합(처음시작시), 1: 상승
        for (int i=1; i<dataCnt; i++) {
            double dEndPrice = data[i];
            sEndDate = dates[i];
            // 1. 처음 추세 결정용.
            if(0 == nSignal)
            {
                // 1.0. Data는 앞서 while문 앞에서 Insert를 했다.
                //     추세만 결정하면 된다.

                // 1.1. 현재종가가 기준가+최소반전폭 이상일때.
                if(dBaseData <= dEndPrice)
                {
                    // 1.1.1. 새로운 BaseData설정
                    dBaseData = dEndPrice;

                    // 1.1.2. 상승추세
                    nSignal = 1;
                }
                // 1.2. 현재종가가 기준가+최소반전폭 이하일때.
                else if(dBaseData >= dEndPrice)
                {
                    // 1.2.1. 새로운 BaseData설정
                    dBaseData = dEndPrice;
                    // 1.2.2. 하락추세
                    nSignal = -1;
                }
                kagiDates[nPnfCount] = sEndDate;
            }
            // 2. 상승추세일때...
            else if(1 == nSignal)
            {
                // 2.1. 기준가-최소반전폭 이하로 종가가 내려가면
                if(dBaseData - unit > dEndPrice)
                {
                    // 2.1.1. 추세반전
                    nSignal = -1;
                    fSwingData[nPnfCount] = dBaseData;
                    nPnfCount++;
                    kagiDates[nPnfCount] = sEndDate;
                    // 2.1.3. 새로운 BaseData썰정
                    dBaseData = dEndPrice;
                }
                // 2.2. 기준가+한칸값이상 상승이면 새로운 BaseData설정.
                else if( dBaseData < dEndPrice)
                {
                    dBaseData = dEndPrice;
                }
                // 2.3. 기준가+한칸값미만 상승이거나 기준가-최소반전폭 미만의 하락
                // 2.3.1 아무것도 하지 않는다. ^^;
            }
            // 3. 하락추세일때...
            else if(-1 == nSignal)
            {
                // 3.1. 기준가+최소반전폭 이상으로 종가가 올라가면
                if( dBaseData + unit < dEndPrice)
                {
                    // 3.1.1. 추세반전
                    nSignal = 1;
                    fSwingData[nPnfCount] = dBaseData;
                    nPnfCount++;
                    kagiDates[nPnfCount] = sEndDate;
                    // 3.1.3. 새로운 BaseData설정
                    dBaseData = dEndPrice;
                }
                // 3.2. 기준가-한칸값이하로  하락이면 새로운 BaseData설정.
                else if( dBaseData > dEndPrice)
                {
                    dBaseData = dEndPrice;
                }
                // 3.3. 기준가+한칸값미만 상승이거나 기준가-최소반전폭미만의 하락
                // 3.3.1 아무것도 하지 않는다.
            }
        }
        fSwingData[nPnfCount] = dBaseData;

        //2015.06.25 by lyk - kagi date, data 설정
        String[] dest = new String[nPnfCount+1];
        try{
            if(data!=null)System.arraycopy(kagiDates,0,dest,0,nPnfCount+1);
        }catch(ArrayIndexOutOfBoundsException e){

        }
        _cdm.setSubPacketData("variable_자료일자", dest);

        double[] destSwing = new double[nPnfCount+1];
        try{
            if(data!=null)System.arraycopy(fSwingData,0,destSwing,0,nPnfCount+1);
        }catch(ArrayIndexOutOfBoundsException e){

        }
        _cdm.setSubPacketData("variable_close", destSwing);
        //2015.06.25 by lyk - kagi date, data 설정 end

        x = getBounds().left;
        y = calcy(data[0]);
        float y1;
        float xw = (float)getBounds().width() / (float)(nPnfCount+1); //2015.06.25 by lyk - int형에서 float형으로 변경
        for (int i=0; i<nPnfCount; i++) {
            y1 = calcy(fSwingData[i]);
            //2023.06.02 by SJW - 다크테마 차트 색상 변경 >>
//            _cvm.drawLine(gl, (int)x,(int)y,(int)x,(int)y1, CoSys.CHART_COLORS[0] ,1.0f);
//            _cvm.drawLine(gl, (int)x,(int)y1,(int)(x+xw),(int)y1, CoSys.CHART_COLORS[0] ,1.0f);
            if(_cvm.getSkinType() == COMUtil.SKIN_BLACK) {
                _cvm.drawLine(gl, (int)x,(int)y,(int)x,(int)y1, CoSys.CHART_COLOR_UP_NIGHT ,1.0f);
                _cvm.drawLine(gl, (int)x,(int)y1,(int)(x+xw),(int)y1, CoSys.CHART_COLOR_UP_NIGHT ,1.0f);
            } else {
                _cvm.drawLine(gl, (int)x,(int)y,(int)x,(int)y1, CoSys.CHART_COLORS[0] ,1.0f);
                _cvm.drawLine(gl, (int)x,(int)y1,(int)(x+xw),(int)y1, CoSys.CHART_COLORS[0] ,1.0f);
            }
            //2023.06.02 by SJW - 다크테마 차트 색상 변경 <<
            y=y1;
            x+=xw;
            if(i==nPnfCount-1)
            {
                y1 = calcy(data[dataCnt-1]);
//                _cvm.drawLine(gl, (int) x, (int) y, (int) x, (int) y1, CoSys.CHART_COLORS[0], 1.0f);
                if(_cvm.getSkinType() == COMUtil.SKIN_BLACK) {
                    _cvm.drawLine(gl, (int) x, (int) y, (int) x, (int) y1, CoSys.CHART_COLOR_UP_NIGHT, 1.0f);
                } else {
                    _cvm.drawLine(gl, (int) x, (int) y, (int) x, (int) y1, CoSys.CHART_COLORS[0], 1.0f);
                }

            }
        }


        //_cvm.drawFillRect(gl, getBounds().right-(int)COMUtil.getPixel(80),(int)COMUtil.getPixel(20),(int)COMUtil.getPixel(80),COMUtil.getPixel(viewH), CoSys.DKGRAY, 0.5f);

        //2014. 3. 27 독립차트에서 삼선전환도 이외에 차트유형의 오른쪽 데이터박스가 위로 튀는 현상>>
//      _cvm.drawFillRect(gl, getBounds().right-(int)COMUtil.getPixel(75),COMUtil.getPixel(10),(int)COMUtil.getPixel(75),viewH, CoSys.DKGRAY, 0.5f);
        _cvm.drawFillRect(gl, getBounds().right-(int)COMUtil.getPixel(75),(int)COMUtil.getPixel(3)+_cvm.Margin_T,(int)COMUtil.getPixel(75),viewH, CoSys.DKGRAY, 0.5f);
        //2014. 3. 27 독립차트에서 삼선전환도 이외에 차트유형의 오른쪽 데이터박스가 위로 튀는 현상<<

        //_cvm.drawFillRect(gl, getBounds().right-145,10+viewH,145,50+10+viewH, CoSys.BLACK, 0.5f);

        //_cvm.drawString(gl, CoSys.WHITE, getBounds().right-(int)COMUtil.getPixel(75),(int)COMUtil.getPixel(30), "BOX : "+unit);       //1칸의 값.

        //2014. 3. 27 독립차트에서 삼선전환도 이외에 차트유형의 오른쪽 데이터박스가 위로 튀는 현상>>
//    	_cvm.drawString(gl, CoSys.WHITE, getBounds().right-(int)COMUtil.getPixel(70),viewH, "BOX : "+unit);       //1칸의 값.
        _cvm.drawString(gl, CoSys.WHITE, getBounds().right-(int)COMUtil.getPixel(70),(int)COMUtil.getPixel(13)+_cvm.Margin_T, "BOX : "+unit);       //1칸의 값.
        //2014. 3. 27 독립차트에서 삼선전환도 이외에 차트유형의 오른쪽 데이터박스가 위로 튀는 현상>>

        //_cvm.drawString(gl, CoSys.WHITE, getBounds().right-135,50+viewH, "3BOX change");//3칸 전환.
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
    public float drawPnf(Canvas gl, float x, float y, float xw, float yw, boolean b,double price){
        float priceY = calcy(price);
        if(b){
            while(y>priceY){
                _cvm.drawLine(gl, (int)x,(int)y,(int)(x+xw),(int)(y-yw), CoSys.CHART_COLORS[0] ,1.0f);
                _cvm.drawLine(gl, (int)x,(int)(y-yw),(int)(x+xw),(int)y, CoSys.CHART_COLORS[0] ,1.0f);
                y-=yw;
            }
        }else{
            while(y<priceY-yw){
                //_cvm.drawCircle(gl,(int)(x+xw/2), (int)y, (float)xw, (float)yw, false, CoSys.CHART_COLORS[1]);
                _cvm.drawCircle(gl,(int)x, (int)y, x+xw, y+yw, false, CoSys.CHART_COLORS[1]);
                y+=yw;
            }
        }
        return y;
    }
}