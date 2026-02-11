package drfn.chart.graph;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

/**
 * BollingerBand 그래프
 */
public class BollingerBandGraph extends AbstractGraph{
    String[] datakind = {"종가"};
    int[][] data;
    double[] ma; //중심선
    double[] ma1;//상한선
    double[] ma2;//하한선
    public BollingerBandGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        definition="주가가 어떤 밴드를 돌파하면 돌파된 밴드는 지지선의 역할을 하고, 그 상위 밴드는 저항선의 역할을 한다. 특히 중간밴드는 하락추세에서는 저항선의, 상승추세에서는 지지선의 역할을 한다. 밴드의 폭이 좁아진 후에는 주가가 급격하게 움직이는 경향이 있다. 주가가 상하한 밴드 폭 밖으로 이탈하면 현재의 추세가 유지되는 경향이 있다.이탈된 주가가 밴드 폭 안으로 들어올 때 추세가 전환된 것으로 판단한다";
        m_strDefinitionHtml = "Bollinger_Bands.html";	//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)
    }
    //=====================================
    //Boolinger Bands  :  해당 기간(20일간)의 이동 평균 M, 표준 편차 a
    //                    1) 상위  :  M+2a   (파랑)
    //                    2) 중심  :  M   (빨강)
    //                    3) 하위  :  M-2a   (파랑)    
    //=====================================
    public void FormulateData(){
        double[] closeData = _cdm.getSubPacketData("종가");
        if(closeData==null) return;
        //2013.05.27 by LYH >> 중심선을 종가 이평이 아닌 (고가+저가+종가)/3의 이평으로 수정.
        //ma = makeAverage(closeData,interval[0]);
        int dLen = closeData.length;
        double[] highData = _cdm.getSubPacketData("고가");
        double[] lowData = _cdm.getSubPacketData("저가");

        //2019. 07. 04 by hyh - BollingerBand 기준가, 계산방법 추가 >>
        double[] price = null;

        if (dataTypeBollingerband == ChartViewModel.AVERAGE_DATA_OPEN){
            price = _cdm.getSubPacketData("시가");
        }
        else if (dataTypeBollingerband == ChartViewModel.AVERAGE_DATA_HIGH){
            price = _cdm.getSubPacketData("고가");
        }
        else if (dataTypeBollingerband == ChartViewModel.AVERAGE_DATA_LOW){
            price = _cdm.getSubPacketData("저가");
        }
        else if (dataTypeBollingerband == ChartViewModel.AVERAGE_DATA_CLOSE){
            price = _cdm.getSubPacketData("종가");
        }
        else if (dataTypeBollingerband == ChartViewModel.AVERAGE_DATA_HL2) {
            if(highData != null && lowData != null)
            {
                price = new double[highData.length];

                for(int i=0; i<highData.length; i++)
                {
                    price[i] = (highData[i] + lowData[i])/2;
                }
            }
        }
        else if (dataTypeBollingerband == ChartViewModel.AVERAGE_DATA_HLO3) {
            if(highData != null && lowData != null && closeData != null)
            {
                price = new double[highData.length];

                for(int i=0; i<highData.length; i++)
                {
                    price[i] = (highData[i] + lowData[i] + closeData[i])/3;
                }
            }
        }
        else {
            price = _cdm.getSubPacketData("종가");
        }
        //2019. 07. 04 by hyh - BollingerBand 기준가, 계산방법 추가 <<

        if(price==null) return;

        //2019. 07. 04 by hyh - BollingerBand 기준가, 계산방법 추가 >>
        //단순이평
        if (calcTypeBollingerband == ChartViewModel.AVERAGE_GENERAL){
            ma = makeAverage(price,interval[0]);
        }
        //가중이평
        else if (calcTypeBollingerband == ChartViewModel.AVERAGE_WEIGHT) {
            ma = makeWeightAverage(price,interval[0]);
        }
        //지수이평
        else if (calcTypeBollingerband == ChartViewModel.AVERAGE_EXPONENTIAL) {
            ma = exponentialAverage(price,interval[0]);
        }
        else {
            ma = makeAverage(price,interval[0]);
        }
        //2019. 07. 04 by hyh - BollingerBand 기준가, 계산방법 추가 <<

        ma1 = new double[dLen];
        ma2 = new double[dLen];
        double[] a= getStandardDeviation(price,ma,interval[0],0);
        double dStand = interval[1]/100.0;
        for(int i=interval[0]-1;i<dLen;i++){
            ma1[i] = ma[i]+(dStand*a[i]);
            ma2[i] = ma[i]-(dStand*a[i]);
        }
        for(int i=0;i<tool.size();i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            switch(i){
                case 0:
                    _cdm.setSubPacketData(dt.getPacketTitle(),ma1);
                    break;
                case 1:
                    _cdm.setSubPacketData(dt.getPacketTitle(),ma);
                    break;
                case 2:
                    _cdm.setSubPacketData(dt.getPacketTitle(),ma2);
                    break;
            }
            //2014.03.18 by LYH >> 지표 소수점 자리수 가격과 동일하게 처리.
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
    public void drawGraph(Canvas gl){
        if(!formulated)FormulateData();                       //저장되어 있지 않다면 계산을 새로 한다        

        double[] drawData=null;
        for(int i=0;i<tool.size();i++){
            DrawTool t=(DrawTool)tool.elementAt(i);
            try{
                drawData=_cdm.getSubPacketData(t.getPacketTitle());
            }catch(ArrayIndexOutOfBoundsException e){
                return;
            }
            _cvm.useJipyoSign=false;
            t.plot(gl,drawData);
        }
    }
    public void drawGraph_withSellPoint(Canvas g){
    }

    public String getName(){
        return "Bollinger Band";
    }
    //============================
    // 표준편차를 구하는 메쏘드
    //============================
    public double[] getStandardDeviation(double[] data, double[] average, int interval,int col) {
        if( (data == null) || (data.length < interval)) {
            return null;
        }
        double[] stDevia = new double[data.length];
        for(int i = interval -1 ; i<stDevia.length ; i++) {
            double[] deviation = new double[stDevia.length];
            for(int j= i ; j>i-interval ; j--) {
                deviation[i] += Math.pow( (data[j]-average[i]), 2);
            }
            stDevia[i] = (Math.sqrt(deviation[i]/(interval)));
        }
        return stDevia;
    }
}