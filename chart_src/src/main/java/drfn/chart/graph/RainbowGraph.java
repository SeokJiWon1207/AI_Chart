package drfn.chart.graph;

import java.util.Vector;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

/**
 * 가격 이동평균 그래프
 */
public class RainbowGraph extends AbstractGraph{
    int[][] data;
    Vector<double[]> v;
    public RainbowGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        definition="";
        m_strDefinitionHtml = "rainbow.html";	//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)
    }
    public void FormulateData(){
        if(!formulated){
            v = new Vector<double[]>();
            double[] closeData = _cdm.getSubPacketData("종가");
            if(closeData==null) return;
            //2016.12.08 by LYH >> 레인보우 차트 30개까지 라인 색상 굵기 설정 가능하도록 처리
            int nCnt = interval[2];
            int nStart = interval[0];
            int nInterval = interval[1];
            if(nCnt>30)
                nCnt = 30;
            DrawTool dt;
            for(int i=1;i<=nCnt;i++){
                double[] nsdata = makeAverage(closeData,nStart+(i*nInterval));
                v.addElement(nsdata);
                dt=tool.elementAt(i-1);
                _cdm.setSubPacketData(dt.getPacketTitle(),nsdata);
            }
            //2016.12.08 by LYH >> 레인보우 차트 30개까지 라인 색상 굵기 설정 가능하도록 처리 end
            formulated = true;
        }
    }
    public void reFormulateData(){
        formulated = false;
        FormulateData();
        formulated = true;
    }
    public void drawGraph(Canvas gl){
        if(!formulated)FormulateData();

        DrawTool t=(DrawTool)tool.elementAt(0);
        for(int i=0;i<v.size();i++){
            double[] drawData = (double[])v.elementAt(i);
            t=tool.elementAt(i);
            t.plot(gl,drawData);
        }
    }
    public void drawGraph_withSellPoint(Canvas g){
    }

    public String getName(){
        return "그물차트";
    }
}