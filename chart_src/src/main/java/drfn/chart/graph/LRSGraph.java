package drfn.chart.graph;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

public class LRSGraph extends AbstractGraph{
    double[] lrs;
    double[] signal;
    int[][] data;//계산 전 데이터
    //int[] base = null;
    public LRSGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        definition="LRS는 과거 일정기간을 대상으로 매일 직선회귀선을 계산하고 각 일자의 직선회귀 기울기를 이어서 지표화한 것입니다.";
        m_strDefinitionHtml = "LRS.html";	//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)
    }

    public void FormulateData(){

        lrs = getLRS(interval[0]);
        if(lrs==null) return;
        signal= exponentialAverage(lrs,interval[1],interval[0]);
        for(int i=0;i<tool.size();i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            if(i==0){
                _cdm.setSubPacketData(dt.getPacketTitle(),lrs);
//                _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
            }
            else {
                _cdm.setSubPacketData(dt.getPacketTitle(),signal);
//                _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
            }
            //2018.10.30 by LYH >> 지표 소수점 자리수 가격과 동일하게 처리.
            if(_cdm.nTradeMulti>0)
                _cdm.setSyncPriceFormat(dt.getPacketTitle());
            else
                _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
            //2018.10.30 by LYH << 지표 소수점 자리수 가격과 동일하게 처리.
        }

        formulated = true;
    }
    public void reFormulateData() {
        FormulateData();
        formulated = true;
    }
    private double[] getLRS(int interval)
    {
        double[] closeData = _cdm.getSubPacketData("종가");
        if(closeData == null)	return null;
        int dLen = closeData.length;
        double[] dValue1 = new double[dLen];
        double[] dValue2 = new double[dLen];
        double[] dValue3 = new double[dLen];
        double[] dValue4 = new double[dLen];
        double[] dValue5 = new double[dLen];
        double[] dValue6 = new double[dLen];
        double[] retData = new double[dLen];

        for(int i = 0; i < dLen; i++)
        {
            dValue1[i] = i+1;
        }

        for(int i = interval-1; i < dLen; i++)
        {
            for(int j = i; j > i-interval; j--)
            {
                dValue2[i] += dValue1[j];
                dValue3[i] += closeData[j];
                dValue4[i] += dValue1[j]*closeData[j];
                dValue5[i] += dValue1[j]*dValue1[j];
            }
            dValue6[i] = (interval* dValue5[i] - (dValue2[i] * dValue2[i]));
            retData[i] = (interval * dValue4[i] - dValue2[i] * dValue3[i]) / dValue6[i];
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
        }
        //2013. 9. 5 지표마다 기준선 설정 추가>>
        drawBaseLine(g);
        //2013. 9. 5 지표마다 기준선 설정 추가>>
    }
    public void drawGraph_withSellPoint(Canvas g){
    }

    public String getName(){
        return "LRS";
    }
}