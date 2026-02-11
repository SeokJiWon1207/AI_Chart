package drfn.chart.graph;

import android.graphics.Canvas;

import java.util.Vector;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

public class MovingBalanceIndicatorGraph extends AbstractGraph{
	double[] graphData;
    int[][] data;//계산 전 데이터
    Vector<DrawTool> tool;
    public MovingBalanceIndicatorGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        tool = getDrawTool();//드로우 툴을 구한다
        definition="MovingBalanceIndicator";

        m_strDefinitionHtml = "moving_balance_indicator.html";    //2018.05.02 by lyj 보조지표 설명/활용법 추가(상세설정창)
    }
    //========================================
    //DEMA : 
    //========================================
    public void FormulateData(){
    	graphData = getMovingBalance(interval[0]);
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
    private double[] getMovingBalance(int interval)	//10
    {
    	double[] closeData = _cdm.getSubPacketData("종가");
    	double[] high = _cdm.getSubPacketData("고가");
    	double[] open = _cdm.getSubPacketData("시가");
    	double[] volData = _cdm.getSubPacketData("기본거래량");
    	if(closeData == null || high == null || open == null || volData == null)	return null;
    	int dLen = closeData.length;
    	double[] mb1 = new double[dLen];
    	double[] mb2 = new double[dLen];
    	double[] mb3 = new double[dLen];
    	double[] retData = new double[dLen];
    	
    	double[] dHighAverage = makeAverage(high, interval);
    	double[] dOpenAverage = makeAverage(open, interval);
        
    	for(int i = interval; i < dLen; i++)
    	{
            if(volData[i]>0) {
                mb1[i] = (1.0 / ((open[i] * high[i]) / (closeData[i] / (volData[i] * 100.0)))) * 10.0;
                mb2[i] = closeData[i] / (closeData[i] + (volData[i] * 100.0)) * 100.0;
                mb3[i] = (dOpenAverage[i] / dHighAverage[i]) * 10.0;
            }
    	}
    	mb1 = makeAverage(mb1, interval);
    	mb2 = makeAverage(mb2, interval);
    	for(int i = interval; i < dLen; i++)
    	{
    		retData[i] = mb1[i] + mb2[i] + mb3[i];
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
        return "Moving Balance Indicator";
    }
}