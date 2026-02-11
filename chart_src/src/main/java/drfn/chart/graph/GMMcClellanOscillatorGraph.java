package drfn.chart.graph;

import android.graphics.Canvas;

import java.util.Vector;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

public class GMMcClellanOscillatorGraph extends AbstractGraph{
	double[] graphData;
    int[][] data;//계산 전 데이터
    Vector<DrawTool> tool;
    public GMMcClellanOscillatorGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        tool = getDrawTool();//드로우 툴을 구한다
        definition="GMMcClellanOscillator";

        m_strDefinitionHtml = "gm_mcclellan_oscillator.html";    //2018.05.02 by lyj 보조지표 설명/활용법 추가(상세설정창)
    }
    //========================================
    //DEMA : 
    //========================================
    public void FormulateData(){
    	graphData = getGMMcClellanOsc(interval[0]);
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
    private double[] getGMMcClellanOsc(int interval)	//13
    {
    	double[] closeData = _cdm.getSubPacketData("종가");
    	double[] high = _cdm.getSubPacketData("고가");
    	double[] open = _cdm.getSubPacketData("시가");
    	double[] volData = _cdm.getSubPacketData("기본거래량");
    	if(closeData == null || high == null || open == null || volData == null)	return null;
    	int dLen = closeData.length;
    	double[] dValue = new double[dLen];
    	double[] retData = new double[dLen];
        
    	for(int i = 0; i < dLen; i++)
    	{
    		dValue[i] = (open[i]*closeData[i])-(high[i]*volData[i]*100);
    	}
    	double[] gmFast = exponentialAverage(dValue, interval);
    	double[] gmSlow = exponentialAverage(dValue, interval*3);
    	for(int i = interval*3-1; i < dLen; i++)
    	{
    		retData[i] = gmFast[i]-gmSlow[i];
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
        return "GM McClellan Oscillator";
    }
}