package drfn.chart.graph;

import android.graphics.Canvas;

import java.util.Vector;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

public class VolumenPriceAccumulatorGraph extends AbstractGraph{
	double[] graphData;
    int[][] data;//계산 전 데이터
    Vector<DrawTool> tool;
    public VolumenPriceAccumulatorGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        tool = getDrawTool();//드로우 툴을 구한다
        definition="Volome&PriceAccumulator";

        m_strDefinitionHtml = "volume___price_accumulator.html";    //2018.05.02 by lyj 보조지표 설명/활용법 추가(상세설정창)
    }
    //========================================
    //DEMA : 
    //========================================
    public void FormulateData(){
    	graphData = getVolumePriceAC();
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
    private double[] getVolumePriceAC() 
    {
    	double[] closeData = _cdm.getSubPacketData("종가");
    	double[] open = _cdm.getSubPacketData("시가");
    	double[] high = _cdm.getSubPacketData("고가");
    	double[] volData = _cdm.getSubPacketData("기본거래량");
    	if(closeData == null || open == null || high == null || volData == null)	return null;
    	int dLen = closeData.length;
    	double[] dUpPriceVol = new double[dLen]; 
    	double[] dDownPriceVol = new double[dLen];
    	double[] dPriceVol = new double[dLen]; 
    	double[] retData = new double[dLen]; 
        
        for(int i=1;i<dLen;i++){
        	if(closeData[i] > closeData[i-1] && volData[i] > volData[i-1])
        		dUpPriceVol[i] = 1;
        	else
        		dUpPriceVol[i] = 0;
        	if(closeData[i] < closeData[i-1] && volData[i] < volData[i-1])
        		dDownPriceVol[i] = 1;
        	else
        		dDownPriceVol[i] = 0;
        	dPriceVol[i] = dUpPriceVol[i] - dDownPriceVol[i];
        }
        for(int i=1;i<dLen;i++){
        	retData[i] = retData[i-1] + dPriceVol[i];
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
        return "Volume & Price Accumulator";
    }
}