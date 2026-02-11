package drfn.chart.graph;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;
import drfn.chart.util.MinMax;

public class SROCGraph extends AbstractGraph{
    double[] sroc;
    double[] signal;
    int[][] data;
    //int[] base=null;

    public SROCGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        String[] datakind = {"고가","저가","종가"};
        _dataKind = datakind;
        definition="이 지표는 Roc는 당일의 주가를 특정일의 주가로 나눈 것으로 추세의 속도를 측정한다.";
        m_strDefinitionHtml = "sroc.html";	//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)
    }
    /*
     *  Comments		: {{{ SROC = 당일 EMA / n일전EMA*100 }}}
     */
    public void FormulateData(){
        double[] closeData = _cdm.getSubPacketData("종가");
        if(closeData==null) return;
        int dLen = closeData.length;
        sroc = new double[dLen];

        double[] dDayEMA = exponentialAverage(closeData,interval[0]); //지수 이동평균 
        //2019.10.08 by JJH - 보조지표 값 수정 (하나금투 HTS기준) >>
        double[] dDayEMA_Cur = exponentialAverage_DI(closeData,interval[0],interval[1]); //지수 이동평균 현재봉

        for(int i=0; i<dLen; i++){
            if(i<interval[0] || i-interval[1]<0 || dDayEMA[i-interval[1]]==0) {
                sroc[i] = 0;
            } else {
//                sroc[i] = (dDayEMA[i] / dDayEMA[i-interval[1]]) * 100;
                sroc[i] = (dDayEMA_Cur[i] / dDayEMA[i-interval[1]]) * 100;
            }
        }

        signal= exponentialAverage(sroc,interval[2]);

        for(int i=0;i<2;i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            if(i==0)_cdm.setSubPacketData(dt.getPacketTitle(),sroc);
            else if(i==1) _cdm.setSubPacketData(dt.getPacketTitle(),signal);
            _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
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
        for(int i=0;i<2;i++){
            DrawTool t=(DrawTool)tool.elementAt(i);
            try{
                drawData=_cdm.getSubPacketData(t.getPacketTitle());
            }catch(ArrayIndexOutOfBoundsException e){
                return;
            }

            if(i==0) _cvm.useJipyoSign=true;
            else _cvm.useJipyoSign=false;

            t.plot(g,drawData);
//          if(base!=null&&i<base.length)t.draw(g,base[i]);
            //2013. 9. 5 지표마다 기준선 설정 추가>>
            drawBaseLine(g);
            //2013. 9. 5 지표마다 기준선 설정 추가>>
        }


        //2014. 9. 15 매매 신호 보기 기능 추가>>
//        DrawTool t=(DrawTool)tool.elementAt(0);
//        if(isSellingSignalShow)
//        	t.drawSignal(g, pdiData, ndiData);
        //2014. 9. 15 매매 신호 보기 기능 추가<<
    }
    public void drawGraph_withSellPoint(Canvas g){
    }

    public String getName(){
        return "SROC";
    }
}
