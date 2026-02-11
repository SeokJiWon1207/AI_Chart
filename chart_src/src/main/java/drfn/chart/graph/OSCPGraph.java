package drfn.chart.graph;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

public class OSCPGraph extends AbstractGraph{
    double[] oscp;
    double[] ema1;
    double[] ema2;
    double[] signal;
    int[][] data;
    //int[] base=null;

    public OSCPGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        String[] datakind = {"고가","저가","종가"};
        _dataKind = datakind;
        definition="이 지표는 Roc는 당일의 주가를 특정일의 주가로 나눈 것으로 추세의 속도를 측정한다.";
        m_strDefinitionHtml = "price_oscillator.html";	//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)
    }
    /*
     *  Comments		: {{{ (단기이동평균-장기이동평균)/단기이동평균*100 }}}
     */
    public void FormulateData(){
        double[] closeData = _cdm.getSubPacketData("종가");
        if(closeData==null) return;
        int dLen = closeData.length;
        oscp = new double[dLen];
        ema1 = new double[dLen];
        ema2 = new double[dLen];

        //ema1= exponentialAverage(closeData,interval[0]);
        //ema2= exponentialAverage(closeData,interval[1]);

        ema1= makeAverage(closeData,interval[0]);
        ema2= makeAverage(closeData,interval[1]);

        //2019.10.08 by JJH - 보조지표 값 수정 (하나금투 HTS기준) >>
//        for(int i=interval[0]; i<dLen; i++){
        for(int i=interval[1]-1; i<dLen; i++){
            //2019.10.08 by JJH - 보조지표 값 수정 (하나금투 HTS기준) <<
            //oscp[i] = (ema1[i] - ema2[i]) / ema1[i] * 100.;
            oscp[i] = ema1[i] - ema2[i];    //신한
        }
        signal= exponentialAverage(oscp,interval[2],interval[0]);
	    for(int i=0;i<tool.size();i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            if(i==0){
                _cdm.setSubPacketData(dt.getPacketTitle(),oscp);
            }
            else {
                _cdm.setSubPacketData(dt.getPacketTitle(), signal);
            }

            _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
        }
	    formulated = true;
    }
//    public void FormulateData(){
//        double[] closeData = _cdm.getSubPacketData("종가");
//        if(closeData==null) return;
//        int dLen = closeData.length;
//        oscp = new double[dLen];
//        ema1 = new double[dLen];
//        ema2 = new double[dLen];
//
////        ema1= exponentialAverage(closeData,interval[0]);	 
////        ema2= exponentialAverage(closeData,interval[1]);	
//
//        ema1= makeAverage(closeData,interval[0]);
//        ema2= makeAverage(closeData,interval[1]);
//
//        for(int i=interval[0]+interval[1]-1; i<dLen; i++){
//            oscp[i] = (ema1[i] - ema2[i]) / ema1[i] * 100.;
//        }
//
//        for(int i=0;i<1;i++){
//            DrawTool dt = (DrawTool)tool.elementAt(i);
//            if(i==0)_cdm.setSubPacketData(dt.getPacketTitle(),oscp);
////            else if(i==1) _cdm.setSubPacketData(dt.getPacketTitle(),signal);
//            _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
//        }
//        formulated = true;
//    }

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
        }
//        for(int i=0;i<1;i++){
//            DrawTool t=(DrawTool)tool.elementAt(i);
//            try{
//                drawData=_cdm.getSubPacketData(t.getPacketTitle());
//            }catch(ArrayIndexOutOfBoundsException e){
//                return;
//            }
//
//            if(i==0) _cvm.useJipyoSign=true;
//            else _cvm.useJipyoSign=false;

//            t.plot(g,drawData);
//          if(base!=null&&i<base.length)t.draw(g,base[i]);
            //2013. 9. 5 지표마다 기준선 설정 추가>>
            drawBaseLine(g);
            //2013. 9. 5 지표마다 기준선 설정 추가>>
//        }

            DrawTool t=(DrawTool)tool.elementAt(0);
            if(isSellingSignalShow)
                t.drawSignal(g, oscp, signal);
        //2014. 9. 15 매매 신호 보기 기능 추가>>
//        DrawTool t=(DrawTool)tool.elementAt(0);
//        if(isSellingSignalShow)
//        	t.drawSignal(g, pdiData, ndiData);
        //2014. 9. 15 매매 신호 보기 기능 추가<<
    }
    public void drawGraph_withSellPoint(Canvas g){
    }

    public String getName(){
        return "Price Oscillator";
    }
}
