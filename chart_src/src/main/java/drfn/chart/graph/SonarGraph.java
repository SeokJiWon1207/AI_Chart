package drfn.chart.graph;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

public class SonarGraph extends AbstractGraph{

    int[][] data;
    double[] sonar;
    double[] sonar_ma;
    public SonarGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        definition="소나의 기본개념은 주가곡선의 접선의 기울기를 지표화한 것입니다.  그러나 실제로는 매매신호가 너무 많이 발생하는 것을 방지하기 위해 지수이동평균의 n일전 대비 상승률을 지표화하고 있습니다. '0'선을 중심으로 상향돌파하면 매수시점, 하향돌파하면 매도시점으로 가정합니다";
        m_strDefinitionHtml = "Sonar.html";	//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)
    }

    //-------------------------------------
    //  SONAR : 기울기의 변화를 통해 주가의 상승 하락의 강도를 사전에 알려준다
    //          금일 지수 이동평균 = 전일 지수 이동평균 + a*(금일 종가지수 - 전일 지수 이동평균)
    //          SONAR = 금일 지수 이동평균 - n일전 지수 이동평균
    //-------------------------------------
    public void FormulateData() {
        double[] closeData = _cdm.getSubPacketData("종가");
        if(closeData==null) return;
        int dLen = closeData.length;
        sonar = new double[dLen];
        //double[] ema= exponentialAverage(closeData,interval[0]);
        double[] ema= makeAverage(closeData,interval[0]);
        int eLen = ema.length;
        //for(int i=interval[1]+interval[0];i<eLen;i++){
        for(int i=0;i<eLen;i++){
            //sonar[i] = ema[i] - ema[i-interval[1]];
            if(i<interval[0] || ema == null) {
                sonar[i] = 0;
                continue;
            }
            if(ema[i] != 0 && i-interval[1]>=0 && ema[i - interval[1]] != 0) {
                //sonar[i] = (ema[i] - ema[i-1])/ema[i]*100;
                sonar[i] = (ema[i] - ema[i - interval[1]]) / ema[i - interval[1]] * 100;
            } else {
                sonar[i] = 0;
            }
        }
        //단순이평
        //sonar_ma=makeAverageD(sonar,interval[2],interval[0]+interval[1]);
        //지수이평
        //sonar_ma= exponentialAverage(sonar,interval[2],interval[0]+interval[1]);
        //sonar_ma= exponentialAverage(sonar,interval[1],interval[0]);

        //단순이평
        //sonar_ma = makeAverage(sonar,interval[1]);
        sonar_ma = makeAverage(sonar,interval[2]);

        for(int i=0;i<tool.size();i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            if(i==0){
                _cdm.setSubPacketData(dt.getPacketTitle(),sonar);
                _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
            }
            else {
                _cdm.setSubPacketData(dt.getPacketTitle(),sonar_ma);
                _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
            }
            //2014.03.18 by LYH << 지표 소수점 자리수 가격과 동일하게 처리.
            //if(_cdm.nTradeMulti>0)
            //    _cdm.setSyncPriceFormat(dt.getPacketTitle());
            //else
            //    _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
            //2014.03.18 by LYH << 지표 소수점 자리수 가격과 동일하게 처리.
        }
        formulated = true;
    }    
//    public void FormulateData() {
//        double[] closeData = _cdm.getSubPacketData("종가");
//        if(closeData==null) return;
//        int dLen = closeData.length;
//        sonar = new double[dLen];
//        //double[] ema= exponentialAverage(closeData,interval[0]);
//        double[] ema= makeAverage(closeData,interval[0]);
//        int eLen = ema.length;
//        //for(int i=interval[1]+interval[0];i<eLen;i++){
//        for(int i=interval[0];i<eLen;i++){
//            //sonar[i] = ema[i] - ema[i-interval[1]];
//            if(ema[i] != 0)
//                sonar[i] = (ema[i] - ema[i-1])/ema[i]*100;
//        }
//        //단순이평
//        //sonar_ma=makeAverageD(sonar,interval[2],interval[0]+interval[1]);
//        //지수이평 
//        //sonar_ma= exponentialAverage(sonar,interval[2],interval[0]+interval[1]);
//        sonar_ma= exponentialAverage(sonar,interval[1],interval[0]);
//        for(int i=0;i<tool.size();i++){
//            DrawTool dt = (DrawTool)tool.elementAt(i);
//            if(i==0){
//                _cdm.setSubPacketData(dt.getPacketTitle(),sonar);
//            }
//            else {
//                _cdm.setSubPacketData(dt.getPacketTitle(),sonar_ma);
//            }
//            //2014.03.18 by LYH << 지표 소수점 자리수 가격과 동일하게 처리.
//            if(_cdm.nTradeMulti>0)
//                _cdm.setSyncPriceFormat(dt.getPacketTitle());
//            else
//                _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
//            //2014.03.18 by LYH << 지표 소수점 자리수 가격과 동일하게 처리.
//        }
//        formulated = true;
//    }
    public void reFormulateData() {
        FormulateData();
        formulated = true;
    }
    public void drawGraph(Canvas g){
        if(!formulated)FormulateData();

        double[] drawData=null;
        //2012. 7. 2   기준선 크기
        //g.glLineWidth(COMUtil.graphLineWidth);
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
        //2012. 7. 2   기준선 크기
        //g.glLineWidth(COMUtil.graphLineWidth2);
//        for(int i=0;i<base.length;i++){
//            DrawTool t=(DrawTool)tool.elementAt(0);
////            g.setColor(base_col[i]);
//            t.draw(g,base[i]);
//        }
        //2013. 9. 5 지표마다 기준선 설정 추가>>
        drawBaseLine(g);
        //2013. 9. 5 지표마다 기준선 설정 추가>>

        //2014. 9. 15 매매 신호 보기 기능 추가>>
        DrawTool t=(DrawTool)tool.elementAt(0);
        if(isSellingSignalShow)
            t.drawSignal(g, sonar, sonar_ma);
        //2014. 9. 15 매매 신호 보기 기능 추가<<
    }
    public void drawGraph_withSellPoint(Canvas g){
    }

    public String getName(){
        return "Sonar";
    }
}