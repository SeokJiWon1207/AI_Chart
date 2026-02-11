package drfn.chart.graph;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

public class StDevGraph extends AbstractGraph{
    int[][] data;
    double[] stDev;
    //int[] base=null;
    public StDevGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        definition="주가와 이동평균간 차이의 표준편차를 구한 것으로서, 변동성을 측정하는 지표입니다. 일반적으로 주가의 고점에서는 변동성이 크고, 주가의 저점에서는 변동성이 작다는 가정하여 활용합니다";
        m_strDefinitionHtml = "Standard_Deviation.html";	//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)
    }
    //============================================
    // StDev :   표준편차
    //
    //============================================
    //============================
    // 표준편차를 구하는 메쏘드
    //============================
    public double[] getStandardDeviation(double[] data, double[] average, int interval) {
        if( (data == null) || (data.length < interval)) {
            return null;
        }
        int dLen = data.length;
        double[] stDevia = new double[dLen];

        for(int i = interval-1 ; i<dLen ; i++) {
            double[] deviation = new double[dLen];
            for(int j= i ; j>i-interval ; j--) {
                if(j < 0)
                    break;
                deviation[i] += Math.pow( (data[j]-average[i]), 2);
            }
            stDevia[i] = Math.sqrt(deviation[i]/(interval));
        }
        return stDevia;
    }
    public void FormulateData() {
        double[] closeData = _cdm.getSubPacketData("종가");
        if(closeData==null) return;
        int i=0;
//        int j=0;
        double[] ma = makeAverage(closeData,interval[0]);
        stDev=getStandardDeviation(closeData,ma,interval[0]);
        for(i=0;i<tool.size();i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            _cdm.setSubPacketData(dt.getPacketTitle(),stDev);
            //2014.03.18 by LYH << 지표 소수점 자리수 가격과 동일하게 처리.
            if(_cdm.nTradeMulti>0)
                _cdm.setSyncPriceFormat(dt.getPacketTitle());
            else
                _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
            //2014.03.18 by LYH << 지표 소수점 자리수 가격과 동일하게 처리.
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
        DrawTool t=(DrawTool)tool.elementAt(0);
        drawData=_cdm.getSubPacketData(t.getPacketTitle());
        _cvm.useJipyoSign=true;
        t.plot(g,drawData);
//        if(base!=null){
//            for(int i=0;i<base.length;i++)t.draw(g,base[i]);
//        }
        //2013. 9. 5 지표마다 기준선 설정 추가>>
        drawBaseLine(g);
        //2013. 9. 5 지표마다 기준선 설정 추가>>
    }
    public void drawGraph_withSellPoint(Canvas g){
    }

    public String getName(){
        return "Standard Deviation";
    }
}