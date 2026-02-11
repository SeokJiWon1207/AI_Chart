package drfn.chart.graph;

import android.graphics.Canvas;

import java.util.Vector;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

public class ElderRayBearPowerGraph extends AbstractGraph{
	double[] graphData;
    int[][] data;//계산 전 데이터
    Vector<DrawTool> tool;
    public ElderRayBearPowerGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        tool = getDrawTool();//드로우 툴을 구한다
        definition="ElderRayBearPower";

        m_strDefinitionHtml = "elder-ray_bear_power.html";    //2018.05.02 by lyj 보조지표 설명/활용법 추가(상세설정창)
    }
    //========================================
    //DEMA : 
    //========================================
    public void FormulateData(){
    	graphData = getElderRayBear(interval[0]);
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
    private double[] getElderRayBear(int interval)
    {
    	double[] closeData = _cdm.getSubPacketData("종가");
    	double[] low = _cdm.getSubPacketData("저가");
    	if(closeData == null || low == null)	return null;
    	int dLen = closeData.length;
    	double[] retData = new double[dLen];
    	double[] dEMA = exponentialAverage(closeData, interval);
    	for(int i = 0; i < dLen; i++)
    	{
    		retData[i] = low[i]-dEMA[i];
    	}
    	
        return retData;
    }
    public void drawGraph(Canvas g){
        if(!formulated)FormulateData();                       //저장되어 있지 않다면 계산을 새로 한다

        double[] drawData=null;
        DrawTool t=(DrawTool)tool.elementAt(0);
        drawData=_cdm.getSubPacketData(t.getPacketTitle());
        _cvm.useJipyoSign=true;

        t.plot(g,drawData);
        if(base!=null){
            for(int i=0;i<base.length;i++)t.draw(g,base[i]);
        }
    }
    public void drawGraph_withSellPoint(Canvas g){
    }
    public String getName(){
        return "Elder_Ray Bear Power";
    }
}