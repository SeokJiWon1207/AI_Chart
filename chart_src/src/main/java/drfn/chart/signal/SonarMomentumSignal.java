package drfn.chart.signal;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.graph.AbstractGraph;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

public class SonarMomentumSignal extends AbstractGraph{

    int[][] data;
    double[] sonar;
    double[] sonar_ma;
    public SonarMomentumSignal(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
//        m_strDefinitionHtml = "Sonar.html";	//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)
    }

    //-------------------------------------
    //  SONAR : 기울기의 변화를 통해 주가의 상승 하락의 강도를 사전에 알려준다
    //          금일 지수 이동평균 = 전일 지수 이동평균 + a*(금일 종가지수 - 전일 지수 이동평균)
    //          SONAR = 금일 지수 이동평균 - n일전 지수 이동평균
    //-------------------------------------
    public void FormulateData() {
    	double[] closeData = _cdm.getSubPacketData("종가");
    	if(closeData==null) return;
        int dLen = closeData.length;
        sonar = new double[dLen];
        double[] ema= exponentialAverage(closeData,interval[0]);
        int eLen = ema.length;
//        for(int i=interval[1]+interval[0];i<eLen;i++){
//        	sonar[i] = ema[i] - ema[i-interval[1]];
//        }
        for(int i=0; i<dLen; i++){
            if(i<interval[1]) {
                sonar[i] = 0;
                continue;
            }
            sonar[i] = ema[i] - ema[i-interval[1]];
        }
        for(int i=0;i<tool.size();i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            if(i==0){
            	_cdm.setSubPacketData(dt.getPacketTitle(),sonar);
            	_cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
            }
        }
        formulated = true;
    }    
//    public void FormulateData() {
//        double[] closeData = _cdm.getSubPacketData("종가");
//        if(closeData==null) return;
//        int dLen = closeData.length;
//        sonar = new double[dLen];
//        //double[] ema= exponentialAverage(closeData,interval[0]);
//        double[] ema= makeAverage(closeData,interval[0]);
//        int eLen = ema.length;
//        //for(int i=interval[1]+interval[0];i<eLen;i++){
//        for(int i=interval[0];i<eLen;i++){
//            //sonar[i] = ema[i] - ema[i-interval[1]];
//            if(ema[i] != 0)
//                sonar[i] = (ema[i] - ema[i-1])/ema[i]*100;
//        }
//        //단순이평
//        //sonar_ma=makeAverageD(sonar,interval[2],interval[0]+interval[1]);
//        //지수이평 
//        //sonar_ma= exponentialAverage(sonar,interval[2],interval[0]+interval[1]);
//        sonar_ma= exponentialAverage(sonar,interval[1],interval[0]);
//        for(int i=0;i<tool.size();i++){
//            DrawTool dt = (DrawTool)tool.elementAt(i);
//            if(i==0){
//                _cdm.setSubPacketData(dt.getPacketTitle(),sonar);
//            }
//            else {
//                _cdm.setSubPacketData(dt.getPacketTitle(),sonar_ma);
//            }
//            //2014.03.18 by LYH << 지표 소수점 자리수 가격과 동일하게 처리.
//            if(_cdm.nTradeMulti>0)
//                _cdm.setSyncPriceFormat(dt.getPacketTitle());
//            else
//                _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
//            //2014.03.18 by LYH << 지표 소수점 자리수 가격과 동일하게 처리.
//        }
//        formulated = true;
//    }
    public void reFormulateData() {
        FormulateData();
        formulated = true;
    }
    public void drawGraph(Canvas g){
        drawStrategyBaseGraph(g,tool,0.0,0.0);
    }
    public void drawGraph_withSellPoint(Canvas g){
    }

    public String getName(){
        if(m_nStrategyType == 1)
            return "SonarMomentum강세약세";
        else
            return "SonarMomentum신호";
    }
}