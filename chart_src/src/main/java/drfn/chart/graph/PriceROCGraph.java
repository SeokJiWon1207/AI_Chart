package drfn.chart.graph;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

public class PriceROCGraph extends AbstractGraph{
    //int[] interval = {12,5,5};
    int[][] data;
    int[] slow_k;
    int[] slow_d;
    double[] signal;
    public PriceROCGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        String[] datakind = {"고가","저가","종가"};
        _dataKind = datakind;
        definition="Fast Stochastics는 지표의 등락이 상당히 심한 경우가 있기 때문에 Fast Stochastics의 값을 다시한번 이동평균하여 평활시킨 것이 Slow Stochastics입니다.  사용자 입력수치는 Fast %K를 구하기 위한 기간값과 Slow %K (=Fast %D)를 구하기 위한 이동평균기간, Slow %D값을 구하기 위한 이동평균기간입니다.";
        m_strDefinitionHtml = "price_roc.html";	//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)
    }

    //------------------------------------
    // Stochastics  
    //    fast %K = {(오늘의 종가 - 최근 n일중 장중 최저가)/(최근 n일중 장중 최고가 - 최근 n일중 장중 최저가)}*100
    //    fast %D = {(오늘의 종가 - 최근 n일중 장중 최저가)의 3일 이동평균 *100}
    //              /{(최근 n일중 장중최고가 - 최근 n일중 장중 최저가의 3일 이동평균}
    //    
    //------------------------------------

    public void FormulateData() {
        double[] closeData = _cdm.getSubPacketData("종가");
        if(closeData==null) return;
        int dLen = closeData.length;
        double lPrePrice;
        double[] cPriceROC = new double[dLen];

        for(int k=0;k<dLen;k++){
            if(k<interval[0])
                cPriceROC[k] = 0;
            else
            {
                lPrePrice = closeData[k-interval[0]];
                if(lPrePrice != 0)
                    cPriceROC[k] = ((closeData[k]-lPrePrice)/lPrePrice) * 100.0;
                else
                    cPriceROC[k] = 0;
            }
        }

        signal= exponentialAverage(cPriceROC,interval[1],interval[0]);

        for(int i=0;i<tool.size();i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            if(i==0){
                _cdm.setSubPacketData(dt.getPacketTitle(),cPriceROC);
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
    public void drawGraph(Canvas gl){
        if(!formulated)FormulateData();                       //저장되어 있지 않다면 계산을 새로 한다


        double[] drawData=null;
        //2012. 7. 2   기준선 크기
        //gl.glLineWidth(COMUtil.graphLineWidth);
        for(int i=0;i<tool.size();i++){
            DrawTool t=(DrawTool)tool.elementAt(i);
            try{
                drawData=_cdm.getSubPacketData(t.getPacketTitle());
            }catch(ArrayIndexOutOfBoundsException e){
                return;
            }
            if(i==0) _cvm.useJipyoSign=true;
            else _cvm.useJipyoSign=false;
            t.plot(gl,drawData);
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

    }
    public void drawGraph_withSellPoint(Canvas g){
    }

    public String getName(){
        return "Price ROC";
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