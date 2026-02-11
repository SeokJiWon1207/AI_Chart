package drfn.chart.graph;

import android.graphics.Canvas;

import java.util.Vector;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;
import drfn.chart.util.MinMax;

public class BPDLStochasticGraph extends AbstractGraph{
	double[] graphData;
    int[][] data;//계산 전 데이터
    Vector<DrawTool> tool;
    public BPDLStochasticGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        tool = getDrawTool();//드로우 툴을 구한다
        definition="BPDLStochastic";
    }
    //========================================
    //DEMA : 
    //========================================
    public void FormulateData(){
    	graphData = getBPDLStochastic(interval[0]);
	    if(graphData==null) return;
        DrawTool dt = (DrawTool)tool.elementAt(0);
        _cdm.setSubPacketData(dt.getPacketTitle(),graphData);
        _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
	    formulated = true;

        m_strDefinitionHtml = "bpdl_stochastic.html";    //2018.05.02 by lyj 보조지표 설명/활용법 추가(상세설정창)
    }
    public void reFormulateData() {
        FormulateData();
        formulated = true;
    }
    private double[] getBPDLStochastic(int interval)	//14
    {
    	double[] closeData = _cdm.getSubPacketData("종가");
    	double[] high = _cdm.getSubPacketData("고가");
    	double[] low = _cdm.getSubPacketData("저가");
    	if(closeData == null || high == null || low == null)	return null;
    	int dLen = closeData.length;
    	double[] clRange = new double[dLen];
    	double[] hlRange = new double[dLen];
    	double[] retData = new double[dLen];
        
    	double max, min;
        for(int i = interval-1 ; i < dLen ; i++) {
            max = MinMax.getRangeMax(high, i+1, interval);
            min = MinMax.getRangeMin(low, i+1, interval);
            
            clRange[i] = closeData[i]-low[i-(interval-1)];
            hlRange[i] = max-min;
            retData[i] = (((clRange[i]/hlRange[i]) * 100) + 100) * 0.5;
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
        return "BPDL Stochastic";
    }
}