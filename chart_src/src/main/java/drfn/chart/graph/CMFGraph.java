package drfn.chart.graph;

import android.graphics.Canvas;

import java.util.Vector;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

public class CMFGraph extends AbstractGraph{
	double[] graphData;
    int[][] data;//계산 전 데이터
    Vector<DrawTool> tool;
    public CMFGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        tool = getDrawTool();//드로우 툴을 구한다
        definition="CMF";

        m_strDefinitionHtml = "cmf.html";    //2018.05.02 by lyj 보조지표 설명/활용법 추가(상세설정창)
    }
    //========================================
    //DEMA : 
    //========================================
    public void FormulateData(){
    	graphData = getCMF(interval[0]);
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
    private double[] getCMF(int interval) //소수점 4자리
    {
    	double[] closeData = _cdm.getSubPacketData("종가");
    	double[] high = _cdm.getSubPacketData("고가");
    	double[] low = _cdm.getSubPacketData("저가");
    	double[] volData = _cdm.getSubPacketData("기본거래량");
    	if(closeData == null || high == null || low == null || volData == null)	return null;
    	int dLen = closeData.length;
    	double[] dValue1 = new double[dLen];
    	double[] dValue2 = new double[dLen];
    	double[] dWeightVolume = new double[dLen];
    	double[] retData = new double[dLen];
    	
        
        for(int i=0;i<dLen;i++){
        	dValue1[i] = (closeData[i]-low[i]) - (high[i]-closeData[i]);
        	dValue2[i] = ((high[i]-low[i]) != 0) ? (high[i]-low[i]) : 1;
        	dWeightVolume[i] = dValue1[i] / dValue2[i] * volData[i];
        }
    	
        double[] dAD = yesAccumN(dWeightVolume, interval);
        double[] dAddVolume = yesAccumN(volData, interval);
        for(int i=0;i<dLen;i++){
        	retData[i] = dAD[i] / dAddVolume[i];
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
        return "CMF";
    }
    public double[] yesAccumN(double[]data, int interval){
    	if(data==null || interval<=0) return null;
        int dLen = data.length;
        double[] averageData = new double[dLen];
        //if(dLen <= interval)return averageData;
        for(int i = 0 ; i < dLen ; i++) {
            if(i==0)
                averageData[i] = data[i];
            else {
                double subTotal = 0.;
                for (int j = i; j > i - interval; j--) {
                    if(j<0)
                        break;
                    subTotal += data[j];
                }
                averageData[i] = subTotal;
            }
        }
        return averageData;
    }
}