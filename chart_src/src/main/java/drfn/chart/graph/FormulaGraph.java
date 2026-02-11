package drfn.chart.graph;

import android.graphics.Canvas;

import java.util.Vector;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;


public class FormulaGraph extends AbstractGraph{
	double[] graphData;
	double[] signal;
    int[][] data;//계산 전 데이터
    double[] ma; //중심선
    double[] ma1;//상한선
    double[] ma2;//하한선
    Vector<DrawTool> tool;
    public FormulaGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        tool = getDrawTool();//드로우 툴을 구한다
        definition="Force Index";

        m_strDefinitionHtml = "formula.html";    //2018.05.02 by lyj 보조지표 설명/활용법 추가(상세설정창)
    }
    //========================================
    //DEMA : 
    //========================================
    public void FormulateData(){
//    	graphData = getForceIndex(interval[0]);
        graphData = getFormula(interval[0]);
	    if(graphData==null) return;
	    //2017.08.14 by pjm 지표 수정 >>
//        DrawTool dt = (DrawTool)tool.elementAt(0);
//        _cdm.setSubPacketData(dt.getPacketTitle(),graphData);
//        _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
//	    signal = exponentialAverage(graphData,interval[0]);
	    for(int i=0;i<tool.size();i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            if(i==0)_cdm.setSubPacketData(dt.getPacketTitle(),graphData);
//            else _cdm.setSubPacketData(dt.getPacketTitle(),signal);
            _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
          //2017.08.14 by pjm 지표 수정 <<
        }
	    formulated = true;
    }
    public void reFormulateData() {
        FormulateData();
        formulated = true;
    }

    private double[] getFormula(int interval) //signal 포함
    {
        double[] closeData = _cdm.getSubPacketData("종가");
        if(closeData == null)	return null;
        int dLen = closeData.length;
        double[] formula = new double[dLen];

        ma = makeAverage(closeData,interval);

        for(int i=1;i<dLen;i++){
            //formula = [(당일의 5일이평-전일의 5일이평)*180]/3.14
            formula[i] =  Math.atan(ma[i]-ma[i-1])*180.0 / 3.14;
        }
        return formula;
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
        }
        //2017.08.14 by pjm 지표 수정 <<
    }
    public void drawGraph_withSellPoint(Canvas g){
    }
    public String getName(){
        return "Formula";
    }
}
