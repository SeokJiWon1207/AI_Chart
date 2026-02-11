package drfn.chart.graph;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

public class MFIGraph extends AbstractGraph{
    double[] mfi;
    double[] signal;
    int[][] data;//계산 전 데이터
    //int[] base = null;
    public MFIGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        definition="MFI는 주식시장의 자금이 얼마만큼 유입과 유출되고 있는지 그 힘의 강도를 측정하는 모멘텀 지표로 RSI (Relative Strength Index)와 강도 측정면에서는 유사하나 RSI는 가격만을 사용하는 지표임에 반해 MFI는 거래량도 포함한 지표입니다.";
        m_strDefinitionHtml = "MFI.html";	//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)
    }
    //========================================
    //Rsi : -오늘의 종가 > 전일 이전의 종가 : 주가 상승분 = 오늘의 종가 - 전일 이전의 종가,주가하락분 = 0
    //      -오늘의 종가 < 전일 이전의 종가 : 주가 하락분 = 전일 이전의 종가 - 오늘의 종가,주가상승분 = 0
    //      -오늘의 종가 = 전일 이전의 종가 : 주가 상승분 = 주가 하락분 = 0
    // 상대 모멘텀 : (주가 상승분의 14일 단순 이동평균)/(주가 하락분의 14일 단순이동평균)
    // RSI = 100-(100/(1+상대모멘텀))
    //========================================
    public void FormulateData(){

        mfi = moneyFlowIndex(interval[0]);
        if(mfi==null) return;
        signal= exponentialAverage(mfi,interval[1],interval[0]);
        for(int i=0;i<tool.size();i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            if(i==0){
                _cdm.setSubPacketData(dt.getPacketTitle(),mfi);
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
    private double[] moneyFlowIndex(int interval){
        double[] highData = _cdm.getSubPacketData("고가");
        double[] lowData = _cdm.getSubPacketData("저가");
        double[] closeData = _cdm.getSubPacketData("종가");
        double[] volData = _cdm.getSubPacketData("기본거래량");
        if(closeData==null) return null;
        int dLen = closeData.length;
        double[] tempData = new double[dLen];
        double[] retData = new double[dLen];
        double posMF, negMF;
        for(int i=0; i<dLen; i++){
            tempData[i] = highData[i]+lowData[i]+closeData[i];
        }
        //2019.10.08 by JJH - 보조지표 값 수정 (하나금투 HTS기준)
        for(int i=interval+1; i<dLen; i++){
            posMF = 0; negMF=0;
            for(int j=i; j>i-interval; j--){
                if(tempData[j]>tempData[j-1])
                    posMF += tempData[j]*volData[j];	//나누기 3 생략
                else if(tempData[j]<tempData[j-1])
                    negMF += tempData[j]*volData[j];	//나누기 3 생략
            }
            //retData[i] = (100.0-(100/(1.0+posMF/negMF)));
            retData[i] = 100-(100/(1+(posMF+0.0001)/(negMF+0.0001)));
        }
        return retData;

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
            //if(base!=null&&i<base.length)t.draw(g,base[i]);
        }
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
            t.drawSignal(g, mfi, signal);
        //2014. 9. 15 매매 신호 보기 기능 추가<<
    }
    public void drawGraph_withSellPoint(Canvas g){
    }

    public String getName(){
        return "MFI";
    }
}