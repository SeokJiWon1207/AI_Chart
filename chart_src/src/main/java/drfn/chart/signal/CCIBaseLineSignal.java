package drfn.chart.signal;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.graph.AbstractGraph;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

public class CCIBaseLineSignal extends AbstractGraph{
    int[][] data;
    double[] cci;
    double[] signal;
    //int[] base=null;
    public CCIBaseLineSignal(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
//        m_strDefinitionHtml = "CCI.html";	//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)
    }

    //============================================
    //  CCI : 주가 평균과 현재주가 사이의 편차를 측정하는 지표
    //        CCI = (M-m)/d*0.015
    //        M : (고가+저가+종가)/3.
    //        m : M의 n일 단순 이동평균
    //        d : 절대값 (|M-m|)의 n일의 단순 이동평균
    //
    //============================================
    public void FormulateData() {
//        data = getData(1);
        double[] highData = _cdm.getSubPacketData("고가");
        double[] lowData = _cdm.getSubPacketData("저가");
        double[] closeData = _cdm.getSubPacketData("종가");
        if(closeData==null) return;
        int dLen = closeData.length;
        double[] m = new double[dLen];
        double[] m_ma = new double[dLen];
        for(int i=0;i<dLen;i++){
            m[i]=(highData[i]+lowData[i]+closeData[i])/3;
        }
        double[] ma = makeAverage(m,interval[0]);
        double sum = 0;
        for(int i=0;i<dLen;i++){
            sum=0;
            for(int k=0; k<interval[0]; k++) {
                if((i-k)<0) break;
                sum += Math.abs(m[i-k]-ma[i]);
            }
            m_ma[i] = sum/interval[0];
        }
//        int[] d = makeAverage(m_ma,interval[0]);
        cci = new double[dLen];
        for(int i=interval[0]-1;i<dLen;i++){
            if(m_ma[i]!=0) {
                cci[i] = ((m[i]-ma[i])/(m_ma[i]*0.015));
            } else {
                cci[i] = ((m[i]-ma[i])/0.015);
            }
        }

        for(int i=0;i<tool.size();i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            if(i==0){
                _cdm.setSubPacketData(dt.getPacketTitle(),cci);
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
        drawStrategyBaseGraph(g,tool,0.0,0.0);
    }
    public void drawGraph_withSellPoint(Canvas g){
    }
    public String getName(){
        if(m_nStrategyType == 1)
            return "CCIBaseLine강세약세";
        else
            return "CCIBaseLine신호";
    }
}