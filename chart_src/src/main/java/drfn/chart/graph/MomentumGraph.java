package drfn.chart.graph;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartPacketDataModel;
import drfn.chart.model.ChartViewModel;

public class MomentumGraph extends AbstractGraph{
    int[][] data;
    double[] mo;
    double[] signal;
    //int[] base = null;
    public MomentumGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        definition="주가의 상대적인 가격 속성을 이용하여 주가 추세의 속도가 증가하는지 감소하는지를 측정하는 지표입니다. 모멘텀은 현재 주가에서 일정 기간 이전의 주가를 차감해서 계산합니다";
        m_strDefinitionHtml = "Momentum.html";	//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)	
    }
    //========================================================
    // Momemtum  :  금일 종가/n일 전 종가 * 100       (n :5일)
    // Momemtum  의 이동 평균 
    //========================================================
    public void FormulateData(){
        double[] closeData = _cdm.getSubPacketData("종가");
        if(closeData==null) return;
        int dLen = closeData.length;
        mo=new double[dLen];
        for(int i=interval[0]; i<dLen;i++){
            mo[i] = closeData[i]/closeData[i-interval[0]]*100;
//            mo[i] = closeData[i]-closeData[i-interval[0]]; //Daewoo
        }

        //단순이평
        //signal = makeAverageD(mo,interval[1],interval[0]);
        //지수이평 
        signal= exponentialAverage(mo,interval[1],interval[0]);

        ChartPacketDataModel cpdm =_cdm.getChartPacket("종가");
        for(int i=0;i<tool.size();i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            if(i==0)
            {
                _cdm.setSubPacketData(dt.getPacketTitle(),mo);
                //2014.03.18 by LYH << 지표 소수점 자리수 가격과 동일하게 처리.
                if(_cdm.nTradeMulti>0)
                {
                    _cdm.setSyncPriceFormat(dt.getPacketTitle());
                }else {
                    if (cpdm.getPacketFormat() == 14)
                        _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
                    else if (cpdm.getPacketFormat() == 15)
                        _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.001");
                    else if (cpdm.getPacketFormat() == 16)
                        _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.0001");
                    else
                        _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
                }
                //2014.03.18 by LYH << 지표 소수점 자리수 가격과 동일하게 처리.
            }
            else
            {
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
//        if(base!=null){
//            DrawTool t=(DrawTool)tool.elementAt(0);
//            for(int i=0;i<base.length;i++){
//                t.draw(g,base[i]);
//            }
//        }
        //2013. 9. 5 지표마다 기준선 설정 추가>>
        drawBaseLine(g);
        //2013. 9. 5 지표마다 기준선 설정 추가>>

        //2014. 9. 15 매매 신호 보기 기능 추가>>
        DrawTool t=(DrawTool)tool.elementAt(0);
        if(isSellingSignalShow)
            t.drawSignal(g, mo, signal);
        //2014. 9. 15 매매 신호 보기 기능 추가<<
    }
    public void drawGraph_withSellPoint(Canvas g){
    }

    public String getName(){
        return "Momentum";
    }
}