package drfn.chart.graph;

import android.graphics.Canvas;
import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

public class CCIGraph extends AbstractGraph{
    int[][] data;
    double[] cci;
    double[] signal;
    //int[] base=null;
    public CCIGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        definition="CCI(Commodity Channel Index):주가평균과 현재주가 사이의 편차를 측정하는 지표입니다";
        m_strDefinitionHtml = "CCI.html";	//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)
    }

    //============================================
    //  CCI : 주가 평균과 현재주가 사이의 편차를 측정하는 지표
    //        CCI = (M-m)/d*0.015
    //        M : (고가+저가+종가)/3.
    //        m : M의 n일 단순 이동평균
    //        d : 절대값 (|M-m|)의 n일의 단순 이동평균
    //
    //============================================
    public void FormulateData() {
//        data = getData(1);
        double[] highData = _cdm.getSubPacketData("고가");
        double[] lowData = _cdm.getSubPacketData("저가");
        double[] closeData = _cdm.getSubPacketData("종가");
        if(closeData==null) return;
        int dLen = closeData.length;
        double[] m = new double[dLen];
        double[] m_ma = new double[dLen];
        for(int i=0;i<dLen;i++){
            m[i]=(highData[i]+lowData[i]+closeData[i])/3;
        }
        double[] ma = makeAverage(m,interval[0]);
        double sum = 0;
        for(int i=0;i<dLen;i++){
            sum=0;
            for(int k=0; k<interval[0]; k++) {
                if((i-k)<0) break;
                sum += Math.abs(m[i-k]-ma[i]);
            }
            m_ma[i] = sum/interval[0];
        }
//        int[] d = makeAverage(m_ma,interval[0]);
        cci = new double[dLen];
        for(int i=interval[0]-1;i<dLen;i++){
            if(m_ma[i]!=0) {
                cci[i] = ((m[i]-ma[i])/(m_ma[i]*0.015));
            } else {
                cci[i] = ((m[i]-ma[i])/0.015);
            }
        }
        signal= exponentialAverage(cci,interval[1],interval[0]);
        for(int i=0;i<tool.size();i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            if(i==0){
                _cdm.setSubPacketData(dt.getPacketTitle(),cci);
                _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
            }
            else {
                _cdm.setSubPacketData(dt.getPacketTitle(),signal);
                _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
            }
        }
        formulated = true;
    }
    public void reFormulateData() {
        FormulateData();
        formulated = true;
    }
    public void drawGraph(Canvas g){
        if(!formulated)FormulateData();

        double[] drawData=null;
        for(int i=0;i<tool.size();i++){
            DrawTool t=(DrawTool)tool.elementAt(i);
            drawData=_cdm.getSubPacketData(t.getPacketTitle());
            if(i==0) _cvm.useJipyoSign=true;
            else _cvm.useJipyoSign=false;
            t.plot(g,drawData);
            //if(base!=null&&i<base.length)t.draw(g,base[i]);
        }
//        if(base!=null){
//            DrawTool t=(DrawTool)tool.elementAt(0);
//            for(int i=0;i<base.length;i++){
//              t.draw(g,base[i]);
//            }
//        }
        //2013. 9. 5 지표마다 기준선 설정 추가>>
        drawBaseLine(g);
        //2013. 9. 5 지표마다 기준선 설정 추가>>

        //2014. 9. 15 매매 신호 보기 기능 추가>>
        DrawTool t=(DrawTool)tool.elementAt(0);
        if(isSellingSignalShow)
            t.drawSignal(g, cci, signal);
        //2014. 9. 15 매매 신호 보기 기능 추가<<
    }
    public void drawGraph_withSellPoint(Canvas g){
    }
    public String getName(){
        return "CCI";
    }
}