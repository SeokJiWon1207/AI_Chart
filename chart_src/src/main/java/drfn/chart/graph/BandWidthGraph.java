package drfn.chart.graph;

import android.graphics.Canvas;

import java.util.Vector;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;


public class BandWidthGraph extends AbstractGraph{
	double[] graphData;
	double[] signal;
    int[][] data;//계산 전 데이터
    double[] ma; //중심선
    double[] ma1;//상한선
    double[] ma2;//하한선
    Vector<DrawTool> tool;
    public BandWidthGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        tool = getDrawTool();//드로우 툴을 구한다
        definition="Band Width";

        m_strDefinitionHtml = "band_width.html";    //2018.05.02 by lyj 보조지표 설명/활용법 추가(상세설정창)
    }
    //========================================
    //DEMA : 
    //========================================
    public void FormulateData(){
//    	graphData = getForceIndex(interval[0]);
        graphData = getBandWidth(interval[0], interval[1]);
	    if(graphData==null) return;
	    //2017.08.14 by pjm 지표 수정 >>
//        DrawTool dt = (DrawTool)tool.elementAt(0);
//        _cdm.setSubPacketData(dt.getPacketTitle(),graphData);
//        _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
//	    signal = exponentialAverage(graphData,interval[1]);
	    for(int i=0;i<tool.size();i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            if(i==0)_cdm.setSubPacketData(dt.getPacketTitle(),graphData);
//            else _cdm.setSubPacketData(dt.getPacketTitle(),signal);
            _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
          //2017.08.14 by pjm 지표 수정 <<
        }
	    formulated = true;
    }
    public void reFormulateData() {
        FormulateData();
        formulated = true;
    }

    private double[] getBandWidth(int interval, int interval2) //signal 포함
    {
        //2017. 7. 6 by hyh - Bollinger Band 기준가 기능 추가 >>
        //2017. 7. 20 by hyh - (고+저)/2, (고+저+종)/3 추가 >>
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
            double[] highData = _cdm.getSubPacketData("고가");
            double[] lowData = _cdm.getSubPacketData("저가");
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
            double[] highData = _cdm.getSubPacketData("고가");
            double[] lowData = _cdm.getSubPacketData("저가");
            double[] closeData = _cdm.getSubPacketData("종가");

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
        //2017. 7. 20 by hyh - (고+저)/2, (고+저+종)/3 추가 <<
        //2017. 7. 6 by hyh - Bollinger Band 기준가 기능 추가 <<

        if(price==null) return null;
        ma = makeAverage(price, interval);

        //2016.12.27 by hyh - Envelope, BollingerBand 계산방법 추가 >>
        //단순이평
        if (calcTypeBollingerband == ChartViewModel.AVERAGE_GENERAL){
            ma = makeAverage(price, interval);
        }
        //가중이평
        else if (calcTypeBollingerband == ChartViewModel.AVERAGE_WEIGHT) {
            ma = makeWeightAverage(price, interval);
        }
        //지수이평
        else if (calcTypeBollingerband == ChartViewModel.AVERAGE_EXPONENTIAL) {
            ma = exponentialAverage(price, interval);
        }
        else {
            ma = makeAverage(price, interval);
        }
        //2016.12.27 by hyh - Envelope, BollingerBand 계산방법 추가 <<

        int dLen = price.length;
        ma1 = new double[dLen];
        ma2 = new double[dLen];
        double[] bandwidth = new double[dLen];

        double[] a= getStandardDeviation(price,ma,interval,0);
        double dStand = interval2/100.0;

        for(int i=interval-1;i<dLen;i++){
            ma1[i] = ma[i]+(dStand*a[i]);
            ma2[i] = ma[i]-(dStand*a[i]);
            bandwidth[i] = (ma1[i]-ma2[i])/ma[i];
        }
        return bandwidth;
    }

    public void drawGraph(Canvas g){
        if(!formulated)FormulateData();                       //저장되어 있지 않다면 계산을 새로 한다

        double[] drawData=null;
        //2017.08.14 by pjm 지표 수정 >>
//        DrawTool t=(DrawTool)tool.elementAt(0);
//        drawData=_cdm.getSubPacketData(t.getPacketTitle());
//        _cvm.useJipyoSign=true;
//
//        t.plot(g,drawData);
//        if(base!=null){
//            for(int i=0;i<base.length;i++)t.draw(g,base[i]);
//        }
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
        //2017.08.14 by pjm 지표 수정 <<
    }
    public void drawGraph_withSellPoint(Canvas g){
    }
    public String getName(){
        return "Band Width";
    }

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
