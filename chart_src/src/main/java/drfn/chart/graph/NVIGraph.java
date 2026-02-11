package drfn.chart.graph;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

public class NVIGraph extends AbstractGraph{
    String[] datakind = {"종가","기본거래량"};
    double[] nviData;
    double[] signal;
    int[][]data;

    public NVIGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        definition="Norman Fosback에 의하면 NVI 값이 1년 이동평균 위에 있을때 강세 시장의 확률은 95∼100% 정도로 분석하고, NVI 값이 1년 이동평균 아래에 있을때 강세 시장의 확률은 50/50 정도로 분석 하기때문에 NVI 값은 강세시장 지표로 가장 유용한 지표로 볼 수가 있습니다. 결국 NVI 값이 255일 이동평균 위로 돌파되면 매수시점, 아래로 돌파되면 매도 시점으로 분석합니다";
        m_strDefinitionHtml = "NVI.html";	//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)
    }
    //==============================
    // NVI계산법
    // if(당일거래량<전일거래량) NVI=전일 NVI+((당일종가-전일종가)/전일 종가)*전일 NVI
    // else NVI=전일 NVI
    //==============================
    public void FormulateData(){
        //2012. 8. 8 NVI Signal 색굵기 없음 : I68
        nviData = makeNVI();
        if(nviData==null) return;
        //2019.10.08 by JJH - 보조지표 값 수정(하나금투 HTS기준) >>
//        signal = exponentialAverage(nviData,interval[0]);
        signal = makeAverageD(nviData,interval[0]);
        //2019.10.08 by JJH - 보조지표 값 수정(하나금투 HTS기준) >>
        for(int i=0;i<tool.size();i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            if(i==0)_cdm.setSubPacketData(dt.getPacketTitle(),nviData);
            else _cdm.setSubPacketData(dt.getPacketTitle(),signal);
            _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
        }
        formulated = true;
    }
    public void reFormulateData() {
        FormulateData();
        formulated = true;
    }
    private double[] makeNVI(){
        double[] closeData = _cdm.getSubPacketData("종가");
        double[] volData = _cdm.getSubPacketData("기본거래량");
        if(closeData==null) return null;
        int dLen = closeData.length;
        double[] retData = new double[dLen];
        double preNVI=0;
//        retData[0]=100;
        retData[0]=0;
        for(int i=1; i<dLen; i++){
            preNVI=retData[i-1];

            if(volData[i] < volData[i-1] ){
                //retData[i]=( preNVI + (( (closeData[i] - closeData[i-1]) / closeData[i-1] ) * 100) );
                retData[i]=( preNVI + (( (closeData[i] - closeData[i-1]) / closeData[i-1] ) ) );
            }else{
                retData[i] = preNVI;
            }
        }
        return retData;
    }

    public void drawGraph(Canvas g){
        if(!formulated)FormulateData();                       //저장되어 있지 않다면 계산을 새로 한다


        double[] drawData=null;
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
    }
    public void drawGraph_withSellPoint(Canvas g){
    }

    public String getName(){
        return "NVI";
    }
}

