package drfn.chart.signal;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.graph.AbstractGraph;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;
import drfn.chart.util.MinMax;

public class StochasticsK_DSignal extends AbstractGraph{
    //int[] interval = {12,5,5};
    int[][] data;
    int[] slow_k;
    int[] slow_d;
    public StochasticsK_DSignal(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        String[] datakind = {"고가","저가","종가"};
        _dataKind = datakind;
//        m_strDefinitionHtml = "Stochastic_Slow.html";	//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)
    }

    //------------------------------------
    // Stochastics  
    //    fast %K = {(오늘의 종가 - 최근 n일중 장중 최저가)/(최근 n일중 장중 최고가 - 최근 n일중 장중 최저가)}*100
    //    fast %D = {(오늘의 종가 - 최근 n일중 장중 최저가)의 3일 이동평균 *100}
    //              /{(최근 n일중 장중최고가 - 최근 n일중 장중 최저가의 3일 이동평균}
    //    
    //------------------------------------

    public void FormulateData() {
        double[] highData = _cdm.getSubPacketData("고가");
        double[] lowData = _cdm.getSubPacketData("저가");
        double[] closeData = _cdm.getSubPacketData("종가");
        if(closeData==null) return;
        int dLen = closeData.length;
        double[] stoch_k = new double[dLen];
        //for(int i=1;i<dLen;i++){
        for(int i=0;i<dLen;i++){
            double l=MinMax.getRangeMin(lowData,i+1,interval[0]);
            double h=MinMax.getRangeMax(highData,i+1,interval[0]);
            if(h==l)
                stoch_k[i] = 0;
            else
                stoch_k[i]= ((closeData[i]-l)*100)/(h-l);
        }
        //단순이평
        //double[] stoch_d = makeAverageD(stoch_k,interval[1], interval[0]-1);
        //double[] slow_d = makeAverageD(stoch_d,interval[2],interval[0]+interval[1]-2);
        //지수이평 
//        double[] stoch_d = exponentialAverage(stoch_k,interval[1], interval[0]-1);
//        double[] slow_d = exponentialAverage(stoch_d,interval[2],interval[0]+interval[1]-2);

        for(int i=0;i<tool.size();i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            if(i==0){
                _cdm.setSubPacketData(dt.getPacketTitle(),stoch_k);
                _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
                String strSignal = dt.getPacketTitle()+"_Signal";
                double[] signalData = exponentialAverage(stoch_k, interval[2]);
                _cdm.setSubPacketData(strSignal,signalData);
            }
        }
        formulated = true;
    }
    public void reFormulateData() {
        FormulateData();
        formulated = true;
    }
    public void drawGraph(Canvas gl){
        drawStrategyGraph(gl,tool);
    }
    public void drawGraph_withSellPoint(Canvas g){
    }

    public String getName(){
        if(m_nStrategyType == 1)
            return "Stochastics 강세약세";
        else
            return "Stochastics 신호";
    }
    public int[] upRatio(int[][] data, int interval) {
        int[] ratio = new int[data.length];
        for(int i = interval ; i < ratio.length ; i++) {
            int upNum = 0;
            for(int j= i ; j>i-interval ; j--) {
                if(data[j-1][0] < data[j][0]) upNum++;
            }
            if(upNum == 0) ratio[i] = 0;
            else  ratio[i] = (int)((upNum*100)/interval);
        }
        return ratio;
    }
}