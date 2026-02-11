package drfn.chart.graph;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

public class MACDOscillatorGraph extends AbstractGraph{
    //int[] interval = {12,26,9};
    int[] stand = {25,75};
    int[][] data;
    double[] macd_oscillator;
    public MACDOscillatorGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        definition="MACD Osc는 MACD와 MACD의 이동평균인 Signal Line의 관계를 통해 매매시점을 파악하는 지표입니다.  0선분석을 통해 매매시점을 잡아낼 수 있습니다";
        m_strDefinitionHtml = "MACD_Oscillator.html";	//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)
    }
    //===============================
    // MACD & Signal  
    //                     1) MACD  :  단기 지수 이동 평균(12 일) - 장기 지수 이동 평균(26일)    (빨강)
    //                     2) Siganl  :  1)의 값을 (9일) 지수 이동 평균    (파랑)    

    // MACD Osillator  
    //                           MACD & Signal 에서 MACD와 Siganl의 값을 
    //                           다시 Osillator로 산출
    //                                       MACD - Signal    
    //===============================
    public void FormulateData() {
//        data = getData(1);
        double[] price = _cdm.getSubPacketData("종가");
        if (price == null) return;
        double[] shortAve = exponentialAverage(price, interval[0]);
        double[] longAve = exponentialAverage(price, interval[1]);
        int dLen = price.length;
        double[] MACD = new double[dLen];
        macd_oscillator = new double[dLen];
//        for(int i=interval[1]-1;i<dLen;i++){
//            MACD[i] = shortAve[i] - longAve[i];
//        }
        for (int i = 0; i < dLen; i++) {
            if (i == 0 || shortAve == null || longAve == null)
                MACD[i] = 0;
            else
                MACD[i] = shortAve[i] - longAve[i];
        }
        //2014.03.21 by LYH >> 첫번째 데이터 이상 해결.<<
        //double[] MACDsig = exponentialAverage(MACD, interval[2],interval[1]);
        double[] MACDsig = exponentialAverage(MACD, interval[2], interval[1] - 1);
//        for(int i=interval[1]+interval[2]-2;i<dLen;i++){
//            macd_oscillator[i] = MACD[i]-MACDsig[i];
//        }
        for(int i=0; i<dLen; i++){
            if(i==0)
                macd_oscillator[i] = 0;
            else
                macd_oscillator[i] = MACD[i]-MACDsig[i];
        }
        DrawTool dt = (DrawTool)tool.elementAt(0);
        _cdm.setSubPacketData(dt.getPacketTitle(),macd_oscillator);
        //2014.03.18 by LYH << 지표 소수점 자리수 가격과 동일하게 처리.
        if(_cdm.nTradeMulti>0)
            _cdm.setSyncPriceFormat(dt.getPacketTitle());
        else
            _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
        //2014.03.18 by LYH << 지표 소수점 자리수 가격과 동일하게 처리.
        formulated = true;
    }
    public void reFormulateData() {
        FormulateData();
        formulated = true;
    }
    public void drawGraph(Canvas g){
        if(!formulated)FormulateData();                       //저장되어 있지 않다면 계산을 새로 한다

        double[] drawData=null;
        for(int i=0;i<tool.size();i++){
            DrawTool t=(DrawTool)tool.elementAt(i);
            try{
                drawData=_cdm.getSubPacketData(t.getPacketTitle());
            }catch(ArrayIndexOutOfBoundsException e){
                return;
            }
            if(i==0) _cvm.useJipyoSign=true;
            else _cvm.useJipyoSign=false;
            t.plot(g,drawData);
        }
    }
    public void drawGraph_withSellPoint(Canvas g){
    }

    public String getName(){
        return "MACD OSC";
    }
}