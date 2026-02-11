package drfn.chart.graph;

import android.graphics.Canvas;

import java.util.Vector;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

public class VAOscillatorGraph extends AbstractGraph{
	double[] graphData;
	double[] signal;	//2017.08.14 by pjm 지표 수정 >>
    int[][] data;//계산 전 데이터
    Vector<DrawTool> tool;
    public VAOscillatorGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        tool = getDrawTool();//드로우 툴을 구한다
        definition="VA Oscillator";

        m_strDefinitionHtml = "va_oscillator.html";    //2018.05.02 by lyj 보조지표 설명/활용법 추가(상세설정창)
    }
    //========================================
    //DEMA : 
    //========================================
    public void FormulateData(){
    	graphData = getVAOsc();		//2017.08.14 by pjm 지표 수정 >>
	    if(graphData==null) return;
	    //2017.08.14 by pjm 지표 수정 >>
//        DrawTool dt = (DrawTool)tool.elementAt(0);
//        _cdm.setSubPacketData(dt.getPacketTitle(),graphData);
//        _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
	    signal = exponentialAverage(graphData,interval[0]);
        for(int i=0;i<tool.size();i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            if(i==0)_cdm.setSubPacketData(dt.getPacketTitle(),graphData);
            else _cdm.setSubPacketData(dt.getPacketTitle(),signal);
            _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
        }
        //2017.08.14 by pjm 지표 수정 <<
	    formulated = true;
    }
    public void reFormulateData() {
        FormulateData();
        formulated = true;
    }
    private double[] getVAOsc() //signal 포함 //2017.08.14 by pjm 지표 수정 >>
    {
    	double[] closeData = _cdm.getSubPacketData("종가");
    	double[] high = _cdm.getSubPacketData("고가");
    	double[] low = _cdm.getSubPacketData("저가");
    	if(closeData == null || high == null || low == null)	return null;
    	int dLen = closeData.length;
    	double[] dValue1 = new double[dLen];
    	double[] dValue2 = new double[dLen]; 
    	double[] retData = new double[dLen]; 
        
        for(int i=1;i<dLen;i++){
        	dValue1[i] = (closeData[i] < closeData[i-1] ? 
    				(closeData[i] - (closeData[i-1] > high[i] ? closeData[i-1] : high[i])) : 0.);
        	dValue2[i] = (closeData[i] > closeData[i-1] ? 
    				(closeData[i] - (closeData[i-1] < low[i] ? closeData[i-1] : low[i])) : dValue1[i]);
        }
        for(int i=1;i<dLen;i++){
        	retData[i] = retData[i-1] + dValue2[i];
        }
        return retData;
    }
    public void drawGraph(Canvas g){
        if(!formulated)FormulateData();                       //저장되어 있지 않다면 계산을 새로 한다

        double[] drawData=null;
      	//2017.08.14 by pjm 지표 수정 >>
//        DrawTool t=(DrawTool)tool.elementAt(0);
//        drawData=_cdm.getSubPacketData(t.getPacketTitle());
//        _cvm.useJipyoSign=true;
//
//        t.plot(g,drawData);
//        if(base!=null){
//            for(int i=0;i<base.length;i++)t.draw(g,base[i]);
//        }
        for(int i=0;i<tool.size();i++){
            DrawTool t=(DrawTool)tool.elementAt(i);
            try{
                drawData=_cdm.getSubPacketData(t.getPacketTitle());
            }catch(ArrayIndexOutOfBoundsException e){
                return;
            }
            if(i==0) _cvm.useJipyoSign=true;
            else _cvm.useJipyoSign=false;
            t.plot(g,drawData);
          	//2017.08.14 by pjm 지표 수정 >>
        }
    }
    public void drawGraph_withSellPoint(Canvas g){
    }
    public String getName(){
        return "VA Oscillator";
    }
}