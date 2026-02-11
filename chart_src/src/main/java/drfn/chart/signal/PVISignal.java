package drfn.chart.signal;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.graph.AbstractGraph;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

public class PVISignal extends AbstractGraph{
    String[] datakind = {"종가","기본거래량"};
    double[][] data;
    double[] pviData;
    public PVISignal(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        definition="PVI의 해석방법은 먼저 거래량이 증가하는 날은 정보에 미흡한 군중들이 시장에 참여한다고 가정하고, 반대로 거래량이 감소하는 날은 기금 형태의 자금이 주로 매매를 한다고 가정을 하여 해석하는데, 이 결과로 PVI는 정보에 미흡한 군중들의 움직임 형태를 파악한 것입니다.이 지표는 통상 255일 이동평균을 사용하거나 52주 이동평균을 사용하는데, NVI와 마찬가지로 PVI 지표가 이동평균을 상향하면 매수 시점, 하향하면 매도 시점으로 분석을 합니다";
        m_strDefinitionHtml = "PVI.html";	//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)
    }
    public void FormulateData(){
//        data = getData(1);
        pviData = makePVI();
        if(pviData==null)
            return;

        DrawTool dt = (DrawTool)tool.elementAt(0);

        //전략
        _cdm.setSubPacketData(this.graphTitle,pviData);
        _cdm.setPacketFormat(this.graphTitle, "× 0.01");
        double[] signal =exponentialAverage(pviData,interval[0]); //signal
        _cdm.setSubPacketData(this.graphTitle+"_Signal",signal);

        formulated = true;
    }
    public void reFormulateData() {
        FormulateData();
        formulated = true;
    }

    //2016. 08. 05 by hyh - 전략지표 계산식 HTS와 동기화 >>
    private double[] makePVI(){
        double[] closeData = _cdm.getSubPacketData("종가");
        double[] volData = _cdm.getSubPacketData("기본거래량");

        if(closeData==null || volData==null) {
            return null;
        }
        int dLen = closeData.length;
        double[] retData = new double[dLen];
        double prePVI=0;
        retData[0]=100;
        for(int i=1; i<dLen; i++){
            prePVI=retData[i-1];

            if(volData[i] > volData[i-1] && closeData[i-1] != 0){
                retData[i]=( prePVI + ( ((double)closeData[i] - (double)closeData[i-1]) / (double)closeData[i-1] ) * 100 );
            }else{
                retData[i] = prePVI;
            }
        }

        return retData;
    }
    public void drawGraph(Canvas gl){
        drawStrategyGraph(gl,tool);
    }
    public void drawGraph_withSellPoint(Canvas g){
    }

    public String getName(){

        if(m_nStrategyType == 1)
            return "PVI 크로스 강세약세";
        else
            return "PVI 크로스 신호";
    }
}