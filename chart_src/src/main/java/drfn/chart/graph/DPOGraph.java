package drfn.chart.graph;

import android.graphics.Canvas;

import java.util.Vector;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;


public class DPOGraph extends AbstractGraph{
	double[] graphData;
	double[] signal;
    int[][] data;//계산 전 데이터
    Vector<DrawTool> tool;
    public DPOGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        tool = getDrawTool();//드로우 툴을 구한다
        definition="Force Index";

        m_strDefinitionHtml = "dpo.html";    //2018.05.02 by lyj 보조지표 설명/활용법 추가(상세설정창)
    }
    //========================================
    //DEMA : 
    //========================================
    public void FormulateData(){
        graphData = getDPO(interval[0]);
	    if(graphData==null) return;
	    //2017.08.14 by pjm 지표 수정 >>
//        DrawTool dt = (DrawTool)tool.elementAt(0);
//        _cdm.setSubPacketData(dt.getPacketTitle(),graphData);
//        _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
	    signal = exponentialAverage(graphData,interval[1]);
	    for(int i=0;i<tool.size();i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            if(i==0)_cdm.setSubPacketData(dt.getPacketTitle(),graphData);
            else _cdm.setSubPacketData(dt.getPacketTitle(),signal);
            _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
          //2017.08.14 by pjm 지표 수정 <<
        }
	    formulated = true;
    }
    public void reFormulateData() {
        FormulateData();
        formulated = true;
    }
    private double[] getDPO(int interval) //signal 포함
    {
        double[] closeData = _cdm.getSubPacketData("종가");
        double[] volData = _cdm.getSubPacketData("기본거래량");
        if(closeData == null || volData == null)	return null;
        int dLen = closeData.length;
        double[] dpo = new double[dLen];
        //double[] ma = makeAverage(closeData, interval-((interval/2)+1));
        double[] ma = makeAverage(closeData, interval);
        int nIndex = 0;
//        for(int i = 0;i<dLen;i++){
//            //dpo = close - ((n/2)+1)일 전의 n일 SMA
//            nIndex = (int)((interval/2.) + 0.5 +1.);
//            if((i-nIndex)>=0)
//                dpo[i] = closeData[i] - ma[i-nIndex];
//            else
//                dpo[i] = 0;
//        }
        for(int i = 0;i<dLen;i++){
            //dpo = close - ((n/2)+1)일 전의 n일 SMA
            nIndex = (int)Math.round((interval/2.) +1.);
            if(i+nIndex>=dLen)
            {
                dpo[i] = 0;
                continue;
            }

            if((i-nIndex)>=0)
                dpo[i] = closeData[i] - ma[i+nIndex];
            else
                dpo[i] = 0;
        }
        return dpo;
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
    public String getName() {
        return "DPO";
    }
}