package drfn.chart.signal;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.graph.AbstractGraph;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

public class MACDSignal extends AbstractGraph{
    //    int[] interval={12,26,9};
    int sub_margin;
    int[][] data;
    double[] macd;
    double[] signal;
    public MACDSignal(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        String[] datakind = {"종가"};
        _dataKind = datakind;
//        m_strDefinitionHtml = "MACD.html";	//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)
    }
    public void FormulateData(){
        double[] price = _cdm.getSubPacketData("종가");
        if(price==null) return;
        double[] shortAve = exponentialAverage(price, interval[0]);
        double[] longAve = exponentialAverage(price, interval[1]);
        int dLen = price.length;
        macd = new double[dLen];

        //for(int i=interval[1]-1;i<dLen;i++) {
        for(int i=0;i<dLen;i++) {
            macd[i] = (shortAve[i] - longAve[i]);
        }
        signal= exponentialAverage(macd, interval[2]);
        for(int i=0;i<tool.size();i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            if(i==0) {
                _cdm.setSubPacketData(dt.getPacketTitle(),macd);
                _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
                String strSignal = dt.getPacketTitle()+"_Signal";
                _cdm.setSubPacketData(strSignal,signal);
            }
        }
        formulated = true;
    }
    public void reFormulateData() {
        FormulateData();
        formulated = true;
    }
    public void drawGraph(Canvas gl){
        drawStrategyGraph(gl,tool);
    }
    public void drawGraph_withSellPoint(Canvas g){
    }

    public String getName(){

        if(m_nStrategyType == 1)
            return "MACD 크로스 강세약세";
        else
            return "MACD 크로스 신호";
    }
}