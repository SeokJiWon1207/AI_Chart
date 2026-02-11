package drfn.chart.signal;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.graph.AbstractGraph;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

public class DisparitySignal extends AbstractGraph{
    String[] datakind = {"종가"};
    int[][] data;
    public DisparitySignal(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
//        m_strDefinitionHtml = "Disparity.html";	//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)
    }

    //====================================
    // 이격도
    // 종가/n일 종가 이동평균 *100
    //====================================
    public void FormulateData(){
//        data = getData(1);
        double[] closeData = _cdm.getSubPacketData("종가");
        if(closeData==null) return;
        int dLen = closeData.length;
        int tLen = tool.size();
        for(int i=0;i<tLen;i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            double[] ma = makeAverage(closeData,interval[i]);
            double[] disp= new double[dLen];

            for(int j=0;j<dLen;j++){
                if(j<interval[i]) {
                    disp[j]=0;
                    continue;
                }
                if(ma[j]!=0)disp[j] = (closeData[j]*100)/ma[j];
                else disp[j]=closeData[j]*100;
            }
            if(i == 0) {
                _cdm.setSubPacketData(dt.getPacketTitle(), disp);
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
        drawStrategyBaseGraph(g,tool,(double)interval[2],(double)interval[1]);
    }
    public void drawGraph_withSellPoint(Canvas g){
    }

    public String getName(){
        if(m_nStrategyType == 1)
            return "Disparity강세약세";
        else
            return "Disparity신호";
    }
}