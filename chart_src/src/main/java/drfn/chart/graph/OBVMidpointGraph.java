package drfn.chart.graph;

import android.graphics.Canvas;

import java.util.Vector;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

public class OBVMidpointGraph extends AbstractGraph{
	double[] graphData;
    int[][] data;//계산 전 데이터
    Vector<DrawTool> tool;
    public OBVMidpointGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        tool = getDrawTool();//드로우 툴을 구한다
        definition="OBVMidpoint";

        m_strDefinitionHtml = "obv(midpoint).html";    //2018.05.02 by lyj 보조지표 설명/활용법 추가(상세설정창)
    }
    //========================================
    //DEMA : 
    //========================================
    public void FormulateData(){
    	graphData = getOBVMidPoint();
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
    private double[] getOBVMidPoint()
    {
    	double[] closeData = _cdm.getSubPacketData("종가");
    	double[] high = _cdm.getSubPacketData("고가");
    	double[] low = _cdm.getSubPacketData("저가");
    	double[] volData = _cdm.getSubPacketData("기본거래량");
    	if(closeData == null || high == null || low == null || volData == null)	return null;
    	int dLen = closeData.length;
    	double[] retData = new double[dLen];
    	
        
        for(int i=1;i<dLen;i++){
        	double comp = (high[i]+low[i])/2-(high[i-1]+low[i-1])/2;
            if(comp==0){
            	retData[i] = retData[i-1];
            }else{
            	retData[i] = (comp>0)?retData[i-1]+(volData[i]):retData[i-1]-(volData[i]);
            }
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
        return "OBV[Midpoint]";
    }
}