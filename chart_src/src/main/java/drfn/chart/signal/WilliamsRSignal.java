package drfn.chart.signal;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.graph.AbstractGraph;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;
import drfn.chart.util.MinMax;

public class WilliamsRSignal extends AbstractGraph{
    int[][] data;
    double[] william;
    double[] signal;
    public WilliamsRSignal(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
//        m_strDefinitionHtml = "William_s__R.html";	//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)
    }

    //============================================
    //  William's %R
    //            n일 고가 중 최고가 - 금일 종가
    //     ------------------------------------- * (-100)           (n : 14)
    //      n일 고가 중 최고가 - n일 저가 중 최저가
    //============================================
    public void FormulateData() {
        double[] highData = _cdm.getSubPacketData("고가");
        double[] lowData = _cdm.getSubPacketData("저가");
        double[] closeData = _cdm.getSubPacketData("종가");
        if(closeData==null) return;
        int dLen = closeData.length;
        william = new double[dLen+1];
        double high;
        double low;
        int dLen2 = dLen+1;
//        for(int i=interval[0];i<dLen2;i++){
        for(int i=0;i<dLen+1;i++){
            if(i<2){
                william[i]=0;
                continue;
            }
            high=MinMax.getRangeMax(highData,i,interval[0]);
            low = MinMax.getRangeMin(lowData,i,interval[0]);
            //william[i-1] = ((high-closeData[i-1])*(100.))/(high-low);
            if(high-low == 0)
                william[i-1] = 0;
            else
                william[i-1] = ((closeData[i-1]-high)*(100.))/(high-low);
        }
        //단순이평
        //signal= makeAverageD(william,interval[1],interval[0]);
        //지수이평 
//        signal= exponentialAverage(william,interval[1],interval[0]);
        for(int i=0;i<tool.size();i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            if(i==0) {
                _cdm.setSubPacketData(dt.getPacketTitle(), william);
                _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
            }
        }
        formulated = true;
    }
    public void reFormulateData() {
        FormulateData();
        formulated = true;
    }
    public void drawGraph(Canvas g){
        drawStrategyBaseGraph(g,tool,-20.0,-80.0);
    }
    public void drawGraph_withSellPoint(Canvas g){
    }

    public String getName(){
        //2012. 8. 8  제목다름  : I89
        if(m_nStrategyType == 1)
            return "WilliamsR강세약세";
        else
            return "WilliamsR신호";
    }
}