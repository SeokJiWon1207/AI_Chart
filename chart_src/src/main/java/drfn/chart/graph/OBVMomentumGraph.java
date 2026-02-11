package drfn.chart.graph;

import android.graphics.Canvas;

import java.util.Vector;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

public class OBVMomentumGraph extends AbstractGraph {
    int[][] data;
    double[] mo;
    double[] obv;
    double[] signal;
    Vector<DrawTool> tool;
    //int[] base = null;
    public OBVMomentumGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        tool = getDrawTool();
        definition="주가의 상대적인 가격 속성을 이용하여 주가 추세의 속도가 증가하는지 감소하는지를 측정하는 지표입니다. 모멘텀은 현재 주가에서 일정 기간 이전의 주가를 차감해서 계산합니다";

        m_strDefinitionHtml = "obv+momentum.html";    //2018.05.02 by lyj 보조지표 설명/활용법 추가(상세설정창)
    }
    //========================================================
    // Momemtum  :  금일 종가/n일 전 종가 * 100       (n :5일)
    // Momemtum  의 이동 평균 
    //========================================================
    public void FormulateData(){
    	double[] closeData = _cdm.getSubPacketData("종가");
        double[] volData = _cdm.getSubPacketData("기본거래량");
    	if(closeData==null) return;
   	    int dLen = closeData.length;
	    mo=new double[dLen];
        obv = new double[dLen];
        double dobv=0.;
        //OBV+Momentum = OBV 값을 구한 후 Momentum 에 대입할 때 종가 대신 OBV 값을 넣어서 구하는 방식.
        obv[0]=0;
        for(int i=1;i<dLen;i++){
            double comp = closeData[i]-closeData[i-1];
            if(comp==0){
                obv[i] = obv[i-1];
            }else{
//                obv[i] = (comp>0)?obv[i-1]+(volData[i]/100):obv[i-1]-(volData[i]/100);
                obv[i] = (comp>0)?obv[i-1]+(volData[i]):obv[i-1]-(volData[i]);
            }
        }

	    for(int i=interval[0]; i<dLen;i++){
	    	mo[i] = obv[i]-obv[i-interval[0]];
	    }
	    signal = exponentialAverage(mo,interval[1],interval[0]);
	    for(int i=0;i<tool.size();i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            if(i==0)_cdm.setSubPacketData(dt.getPacketTitle(),mo);
            else _cdm.setSubPacketData(dt.getPacketTitle(),signal);
        }
        formulated = true;
    }
    public void reFormulateData() {
        FormulateData();
        formulated = true;
    }
    public void drawGraph(Canvas g){
        if(!formulated)FormulateData();                       //저장되어 있지 않다면 계산을 새로 한다
        
        double[] drawData=null;   
        for(int i=0;i<tool.size();i++){
            DrawTool t=(DrawTool)tool.elementAt(i);
            drawData=_cdm.getSubPacketData(t.getPacketTitle());
            if(i==0) _cvm.useJipyoSign=true;
            else _cvm.useJipyoSign=false;
            t.plot(g,drawData);
        }/*
        if(base!=null){
            for(int i=0;i<base.length;i++)t.draw(g,base[i]);
        }*/
        
    }
    public void drawGraph_withSellPoint(Canvas g){
    }
    public String getName(){
        return "OBV+Momentum";
    }
}