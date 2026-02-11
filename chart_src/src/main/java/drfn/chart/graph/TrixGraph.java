package drfn.chart.graph;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

public class TrixGraph extends AbstractGraph{
    int[][] data;//계산 전 데이터
    double[] trix;
    double[] signal;
    public TrixGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        definition="TRIX (Triple Smoothed Moving Averages)종가의 지수이동평균을 세번 평활시켜 그 변화비율을 퍼센트로 나타낸 모멘텀 지표로서 세차례의 평활과정을 통해 불필요한 Whipsaw현상을 없앤 것입니다 ";
        m_strDefinitionHtml = "TRIX.html";	//2014. 11. 20 각 보조지표 설명/활용법 추가(상세설정창)
    }
    //----------------------------------
    //
    // TRIX : 이동평균의 후행성이라는 단점을 극복하기 위해 고안
    //        주가의 지수 이동평균선을 이용하여 세 차례 거듭 평활한 다음 그 값의 변화율을 다시 구하는 지표
    //        EMA1 = 종가의 n일 지수 이동평균
    //        EMA2 = EMA1의 n일 지수 이동평균
    //        EMA3 = EMA2의 n일 지수 이동평균
    //        TRIX = (금일의 EMA3의 값- 전일의 EMA3값)/전일의 EMA3값
    //        TRIX Signal = TRIX의 m일 지수 이동평균
    //        (n = 보통 12일에서 25사이의 값,n값이 너무 클 경우 후행성 문제 발생)
    //        (m = MACD와 마찬가지로 9일전후 적당)
    //
    //----------------------------------    
    public void FormulateData() {
        double[] closeData = _cdm.getSubPacketData("종가");
        if(closeData==null) return;
        int dLen = closeData.length;
        trix = new double[dLen];
//        signal = new double[dLen];
//        double EMA1;
//        double EMA2;
//        double EMA3;
//        double PREEMA;
//
//        EMA1=EMA2=EMA3=PREEMA=0.;
//
//        double TRIX =EMA1;
//        double SIGNAL =EMA1;
//
//        for(int i=0;i<dLen;i++){
//            // if(i==interval[0])EMA2=EMA1;
//            //   if(i==interval[0]*2-1)EMA3=EMA2;
//            if(interval[0] !=-1){
//                EMA1= 2./(interval[0]+1)*(closeData[i]-EMA1)+EMA1;
//                EMA2= 2./(interval[0]+1)*(EMA1-EMA2)+EMA2;
//                EMA3= 2./(interval[0]+1)*(EMA2-EMA3)+EMA3;
//            }
//            if(PREEMA ==0)TRIX =0;
//            else TRIX = (EMA3-PREEMA)/PREEMA*100;
//            PREEMA = EMA3;
//            if(i>=(interval[0]*3-2)){
//                trix[i] = (TRIX);
//            }
//            //if(i==1)SIGNAL=TRIX;
//            if(interval[1] !=-1)SIGNAL = 2./(interval[1]+1)*(TRIX-SIGNAL)+SIGNAL;
//            if(i>=interval[0]*3+interval[1]-3){
//                signal[i] = (SIGNAL);
//            }
//        }
        //2019.07.18 by LYH >> HTS와 지표 데이터 맞춤 Start
        double PREEMA=0;
        double TRIX =0;
        double[] ema1 = exponentialAverage(closeData,interval[0]);  //전일봉
        double[] ema2 = exponentialAverage(ema1,interval[0]);
        double[] ema3 = exponentialAverage(ema2,interval[0]);
        double[] ema1_Cur = exponentialAverage_DI(closeData,interval[0],1); //현재봉에 대한 이평
        double[] ema2_Cur = exponentialAverage_DI(ema1_Cur,interval[0],1);
        double[] ema3_Cur = exponentialAverage_DI(ema2_Cur,interval[0],1);
        for(int i=0;i<dLen;i++){
            if(i==0){
                trix[i]=0;
                PREEMA = ema3[i];
                continue;
            }
            if(PREEMA ==0)TRIX =0;
            else TRIX = (ema3_Cur[i]-PREEMA)/PREEMA*100;
            PREEMA = ema3[i];
            if(i>0){
                trix[i] = (TRIX);
            }
            else
                trix[i] = 0;
        }

        signal= exponentialAverage(trix,interval[1],interval[0]);
        for(int i=0;i<tool.size();i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            if(i==0)_cdm.setSubPacketData(dt.getPacketTitle(),trix);
            else _cdm.setSubPacketData(dt.getPacketTitle(),signal);
            _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
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
            t.drawSignal(g, trix, signal);
        //2014. 9. 15 매매 신호 보기 기능 추가<<
    }
    public void drawGraph_withSellPoint(Canvas g){
    }

    public String getName(){
        return "TRIX";
    }
}