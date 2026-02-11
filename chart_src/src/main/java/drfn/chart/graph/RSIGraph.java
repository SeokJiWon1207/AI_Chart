package drfn.chart.graph;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

public class RSIGraph extends AbstractGraph{

    int[][] data;
    double[] rsi;
    double[] signal;
    public RSIGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        definition="Welles Wilder에 의해서 개발된 지표입니다.  시장가격의 변동폭 중에서 상승폭이 차지하는 비중이 어느정도인가를 파악하여 추세의 강도가 어느 정도인가를 측정하는 지표입니다.RSI의 수치가 70 이상이면 과열국면으로 판단하며 RSI의 수치가 30 이하이면 침체국면으로 판단합니다. 과열침체권에서는 신뢰도가 있습니다 ";
        m_strDefinitionHtml = "RSI.html";	//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)
    }
    //========================================
    //Rsi : -오늘의 종가 > 전일 이전의 종가 : 주가 상승분 = 오늘의 종가 - 전일 이전의 종가,주가하락분 = 0
    //      -오늘의 종가 < 전일 이전의 종가 : 주가 하락분 = 전일 이전의 종가 - 오늘의 종가,주가상승분 = 0
    //      -오늘의 종가 = 전일 이전의 종가 : 주가 상승분 = 주가 하락분 = 0
    // 상대 모멘텀 : (주가 상승분의 14일 단순 이동평균)/(주가 하락분의 14일 단순이동평균)
    // RSI = 100-(100/(1+상대모멘텀))
    //========================================
    public void FormulateData() {
        try{
//            Log.d("[Chart]-RSI", "-- Start");

            double[] closeData = _cdm.getSubPacketData("종가");
            if(closeData==null)
                return;
            int dLen = closeData.length;
            rsi = new double[dLen];

            String[] strData_Date = _cdm.getStringData("자료일자");

            int nIndex = 0;
            double dDownAmt, dUpAmt, dUpSum, dDownSum, dUpAvg, dDownAvg;
            double dPreUpAvg, dPreDownAvg;
            dDownAmt = dUpAmt = dUpSum = dDownSum = dUpAvg = dDownAvg = dPreUpAvg = dPreDownAvg = 0;

            int nVal_Avg = interval[0];
            int nStart = nVal_Avg - 1;
//            Log.d("[Chart]-RSI : - Cnt : ",  String.valueOf(dLen));

            ////////////////////////////////////////////////////////////////////
            // First RS
            ////////////////////////////////////////////////////////////////////
            dUpSum = 0.0;
            dDownSum = 0.0;
            if(dLen >= nVal_Avg) {

                for (nIndex = 0; nIndex < nVal_Avg; nIndex++) {
                    if (nStart - nIndex == 0)
                        dUpAmt = closeData[nStart - nIndex];
                    else
                        dUpAmt = closeData[nStart - nIndex] - closeData[nStart - nIndex - 1];

                    //Log.d("[Chart]-RSI : Date ", strData_Date[nIndex]);

                    if (dUpAmt >= 0)
                        dDownAmt = 0;
                    else {
                        dDownAmt = -dUpAmt;
                        dUpAmt = 0;
                    }
                    dUpSum = dUpSum + dUpAmt;
                    dDownSum = dDownSum + dDownAmt;
                }
                dPreUpAvg = dUpAvg = dUpSum / nVal_Avg;
                dPreDownAvg = dDownAvg = dDownSum / nVal_Avg;

//            Log.d("[Chart]-RSI", "dUpAvg : " + String.valueOf(dUpAvg));
//            Log.d("[Chart]-RSI", "dDownAvg : " + String.valueOf(dDownAvg));

                if ((dUpAvg + dDownAvg) != 0)
                    rsi[nStart] = 100 * dUpAvg / (dUpAvg + dDownAvg);
                else
                    rsi[nStart] = 0;

//            Log.d("[Chart]-RSI", "RSI First Data : " + String.valueOf(rsi[nStart]));

                ////////////////////////////////////////////////////////////////////
                // Smoothed RS
                ////////////////////////////////////////////////////////////////////
                for (nIndex = nStart + 1; nIndex < dLen; nIndex++) {
                    dUpAmt = closeData[nIndex] - closeData[nIndex - 1];

                    if (dUpAmt >= 0)
                        dDownAmt = 0;
                    else {
                        dDownAmt = -dUpAmt;
                        dUpAmt = 0;
                    }
                    dUpAvg = (dPreUpAvg * (nVal_Avg - 1) + dUpAmt) / nVal_Avg;
                    dDownAvg = (dPreDownAvg * (nVal_Avg - 1) + dDownAmt) / nVal_Avg;

                    dPreUpAvg = dUpAvg;
                    dPreDownAvg = dDownAvg;

                    if ((dUpAvg + dDownAvg) != 0)
                        rsi[nIndex] = 100 * dUpAvg / (dUpAvg + dDownAvg);
                    else
                        rsi[nIndex] = 0;
                }
            }
            if (interval.length == 2) {
                signal = exponentialAverage(rsi, interval[1], interval[0]);
            }
            for(int i=0; i<dLen; i++)
            {
                if(i<interval[0]-1) {
                    rsi[i] = 0;
                    signal[i] = 0;
                }
            }
            for(int i=0;i<tool.size();i++){
                DrawTool dt = (DrawTool)tool.elementAt(i);
                if(i==0){
                    _cdm.setSubPacketData(dt.getPacketTitle(),rsi);
                    _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
                }
                else {
                    _cdm.setSubPacketData(dt.getPacketTitle(),signal);
                    _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
                }
            }
            formulated = true;
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
//    public void FormulateData() {
//        double[] closeData = _cdm.getSubPacketData("종가");
//        if(closeData==null) return;
//        int dLen = closeData.length;
//        rsi = new double[dLen];
//        double[] up_data = new double[dLen];
//        double[] dn_data = new double[dLen];
//
//        for(int i=0 ; i < dLen-1 ; i++){
//            if(i==0)
//                up_data[0] = closeData[0];	//Daewoo
//
//            up_data[i+1] =(closeData[i+1]-closeData[i]>0)?closeData[i+1]-closeData[i]:0;
//            dn_data[i+1] =(closeData[i+1]-closeData[i]<0)?closeData[i]-closeData[i+1]:0;
//        }
//        //double[] upEMA = makeAverageD(up_data,interval[0]);
//        //double[] downEMA = makeAverageD(dn_data,interval[0]);
//        double[] upEMA = makeAverageDaewoo(up_data,interval[0], interval[0]-1);
//        double[] downEMA = makeAverageDaewoo(dn_data,interval[0], interval[0]-1);
//        for(int j=interval[0]; j < dLen ; j++){
//            rsi[j]=upEMA[j]*100/(downEMA[j]+upEMA[j]);
//        }
//        signal= exponentialAverage(rsi,interval[1],interval[0]);
//        for(int i=0;i<tool.size();i++){
//            DrawTool dt = (DrawTool)tool.elementAt(i);
//            if(i==0){
//                _cdm.setSubPacketData(dt.getPacketTitle(),rsi);
//                _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
//            }
//            else {
//                _cdm.setSubPacketData(dt.getPacketTitle(),signal);
//                _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
//            }
//        }
//        formulated = true;
//    }
    public void reFormulateData() {
        FormulateData();
        formulated = true;
    }
    public void drawGraph(Canvas gl){
        if(!formulated)FormulateData();                       //저장되어 있지 않다면 계산을 새로 한다
        if(tool==null || tool.size()==0) return;
        double[] drawData=null;
        //2012. 7. 2   기준선 크기
        //gl.glLineWidth(COMUtil.graphLineWidth);
        for(int i=0;i<tool.size();i++){
            DrawTool t=(DrawTool)tool.elementAt(i);
            drawData=_cdm.getSubPacketData(t.getPacketTitle());
            if(i==0) _cvm.useJipyoSign=true;
            else _cvm.useJipyoSign=false;
            t.plot(gl,drawData);
            //if(base!=null&&i<base.length)t.draw(g,base[i]);
        }

        //2012. 7. 2   기준선 크기
        //gl.glLineWidth(COMUtil.graphLineWidth2);
//        for(int i=0;i<base.length;i++){
//            DrawTool t=(DrawTool)tool.elementAt(0);
////            g.setColor(base_col[i]);
//            t.draw(gl,base[i]);
//        }
        //2013. 9. 5 지표마다 기준선 설정 추가>>
        drawBaseLine(gl);
        //2013. 9. 5 지표마다 기준선 설정 추가>>

        //2014. 9. 15 매매 신호 보기 기능 추가>>
        DrawTool t=(DrawTool)tool.elementAt(0);
        if(isSellingSignalShow)
            t.drawSignal(gl, rsi, signal);
        //2014. 9. 15 매매 신호 보기 기능 추가<<
    }
    public void drawGraph_withSellPoint(Canvas g){
    }

    public String getName(){
        return "RSI";
    }
}