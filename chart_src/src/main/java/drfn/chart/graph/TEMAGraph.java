package drfn.chart.graph;

import android.graphics.Canvas;

import java.util.Vector;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

public class TEMAGraph extends AbstractGraph {
	double[] graphData;
    int[][] data;//계산 전 데이터
    Vector<DrawTool> tool;
    public TEMAGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        tool = getDrawTool();//드로우 툴을 구한다
        definition="TEMA";

        m_strDefinitionHtml = "tema.html";    //2018.05.02 by lyj 보조지표 설명/활용법 추가(상세설정창)
    }
    //========================================
    //TEMA :
    //========================================
    public void FormulateData(){
    	graphData = getTEMA(interval[0]);
	    if(graphData==null) return;
        DrawTool dt = (DrawTool)tool.elementAt(0);
        _cdm.setSubPacketData(dt.getPacketTitle(),graphData);
        _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
	    formulated = true;
    }
    public void reFormulateData() {
        FormulateData();
        formulated = true;
    }
    private double[] getTEMA(int interval)
    {
    	double[] closeData = _cdm.getSubPacketData("종가");
    	if(closeData == null)	return null;
    	int dLen = closeData.length;
        
    	double[] dTEMA1 = exponentialAverage(closeData, interval);
    	double[] dTEMA = exponentialAverage(dTEMA1, interval);
    	double[] retData = exponentialAverage(dTEMA, interval);
        for(int i=0; i<closeData.length; i++)
        {
            retData[i] = dTEMA1[i]*3-dTEMA[i]*3+retData[i];
        }

        return retData;
    }
    public void drawGraph(Canvas g){
        if(!formulated)FormulateData();                       //저장되어 있지 않다면 계산을 새로 한다

        double[] drawData=null;
        DrawTool t=(DrawTool)tool.elementAt(0);
        drawData=_cdm.getSubPacketData(t.getPacketTitle());
        _cvm.useJipyoSign=false;
        t.plot(g,drawData);
        if(base!=null){
            for(int i=0;i<base.length;i++)t.draw(g,base[i]);
        }
    }
    public void drawGraph_withSellPoint(Canvas g){
    }
    public String getName(){
        return "TEMA";
    }
}