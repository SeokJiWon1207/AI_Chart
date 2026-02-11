package drfn.chart.graph;

import android.graphics.Canvas;

import java.util.Vector;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

public class BPDLShortTrendGraph extends AbstractGraph{
	double[] graphData;
    int[][] data;//계산 전 데이터
    Vector<DrawTool> tool;
    public BPDLShortTrendGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        tool = getDrawTool();//드로우 툴을 구한다
        definition="BPDLShortTrend";
    }
    //========================================
    //DEMA : 
    //========================================
    public void FormulateData(){
    	graphData = getBPDLShortTrend(interval[0], interval[1]);
	    if(graphData==null) return;
        DrawTool dt = (DrawTool)tool.elementAt(0);
        _cdm.setSubPacketData(dt.getPacketTitle(),graphData);
        _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
	    formulated = true;

        m_strDefinitionHtml = "bpdl_short_trend.html";    //2018.05.02 by lyj 보조지표 설명/활용법 추가(상세설정창)
    }
    public void reFormulateData() {
        FormulateData();
        formulated = true;
    }
    private double[] getBPDLShortTrend(int interval, int interval2)	//3,5
    {
    	double[] closeData = _cdm.getSubPacketData("종가");
    	if(closeData == null)	return null;
    	int dLen = closeData.length;
    	double[] dValue1 = new double[dLen];
    	double[] dValue2 = new double[dLen];
    	double[] retData = new double[dLen];

    	for(int i = interval; i < dLen; i++)
    	{
    		dValue1[i] = closeData[i]-closeData[i-interval];
    		dValue2[i] = Math.abs(closeData[i]-closeData[i-1]);
    	}
    	double[] dAverage = makeAverage(dValue2, interval2);
    	for(int i = interval2; i < dLen; i++)
    	{
            if(dAverage[i]!=0){
                retData[i] = dValue1[i] / dAverage[i];
            }
    	}
    	retData = exponentialAverage(retData, interval);
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
        return "BPDL Short Trend";
    }
}