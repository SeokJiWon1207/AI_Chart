package drfn.chart.graph;

import android.graphics.Canvas;

import java.util.Vector;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

public class LFIGraph extends AbstractGraph{
	double[] graphData;
    int[][] data;//계산 전 데이터
    Vector<DrawTool> tool;
    public LFIGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        tool = getDrawTool();//드로우 툴을 구한다
        definition="LFI";

        m_strDefinitionHtml = "lfi.html";    //2018.05.02 by lyj 보조지표 설명/활용법 추가(상세설정창)
    }
    //========================================
    //DEMA : 
    //========================================
    public void FormulateData(){
    	graphData = getLFI();
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
    private double[] getLFI() 
    {
    	double[] closeData = _cdm.getSubPacketData("종가");
    	double[] open = _cdm.getSubPacketData("시가");
    	if(closeData == null || open == null)	return null;
    	int dLen = closeData.length;
    	double[] dValue1 = new double[dLen]; 
    	double[] dValue2 = new double[dLen]; 
    	double[] retData = new double[dLen]; 
        
        for(int i=3;i<dLen;i++){
        	dValue1[i] = closeData[i] - open[i];
        	if(dValue1[i] > 0)
        		dValue2[i] = dValue1[i] * 3.0;
        	if(dValue1[i] < 0)
        		dValue2[i] = dValue1[i] * 2.0;
        	if(dValue2[i]==0)
        		dValue2[i]=dValue2[i-1];
        	retData[i] = retData[i-1]+dValue2[i];
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
        return "LFI";
    }
}