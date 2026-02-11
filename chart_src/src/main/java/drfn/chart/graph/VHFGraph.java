package drfn.chart.graph;

import android.graphics.Canvas;
import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;
import drfn.chart.util.MinMax;

public class VHFGraph extends AbstractGraph{
    int[][] data;
    double[] upra;
    public VHFGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        definition="아담 화이트(Adam White)에 의해 개발된 VHF지표는 n기간중 가장 큰 종가와 가장 작은 종가간의 변동성을 이용하여 추세구간(Trending phase)과 횡보구간(Trading range)을 표시하는 지표입니다. MACD, 이동평균선 같은 지표는 추세구간에서 뛰어난 성능을 발휘하며, 횡보구간일 경우는 RSI나 Stochastic같은 Oscillator지표를 이용하는 것이 유용합니다. 이와 같이 VHF지표는 추세에 따라 어느 지표를 이용할 것인지에 대한 도움을 줍니다. 일간보다는 주간에서 추세를 파악하는데 유용합니다.";
        m_strDefinitionHtml = "VHF.html";	//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)
    }
    //========================================
    //VHF : 분자 = 기간중 최고값 - n기간 중 최저값의 절대값
    //      분모 = 전일종가 - 금일종가의 절대값을 n기간의 합
    // VHF = 분자 / 분모
    //========================================
    public void FormulateData() {
        double[] closeData = _cdm.getSubPacketData("종가");
        if(closeData==null) return;
        upra = VHF(closeData, interval[0]);
        for(int i=0;i<tool.size();i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            _cdm.setSubPacketData(dt.getPacketTitle(),upra);
            _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
        }
        formulated = true;
    }
    public double[] VHF(double[] data, int interval) {
//    	double[] highData = _cdm.getSubPacketData("고가");
//    	double[] lowData = _cdm.getSubPacketData("저가");
    	double[] ratio = new double[data.length];
//        for(int i = interval ; i < ratio.length ; i++) {
        for(int i = 1 ; i < ratio.length ; i++) {
//        	double l=MinMax.getRangeMin(lowData,i,interval);
//            double h=MinMax.getRangeMax(highData,i,interval);
        	double l=MinMax.getRangeMin(data,i+1,interval);
        	double h=MinMax.getRangeMax(data,i+1,interval);
            double sum = 0;
            for(int j= i ; j>i-interval ; j--) {
            	if(j-1 < 0) {
            		break;
            	}
            	sum += Math.abs(data[j-1] - data[j]);
            }
            if(sum == 0) ratio[i] = 0;
            else  ratio[i] = (Math.abs(h-l)/sum);
        }
        return ratio;
    }

//    public double[] VHF(double[] data, int interval) {
//        double[] highData = _cdm.getSubPacketData("고가");
//        double[] lowData = _cdm.getSubPacketData("저가");
//        double[] ratio = new double[data.length];
//        for(int i = interval-1 ; i < ratio.length ; i++) {
//            double l=MinMax.getRangeMin(lowData,i+1,interval);
//            double h=MinMax.getRangeMax(highData,i+1,interval);
//
//            int sum = 0;
//            for(int j= i ; j>i-interval ; j--) {
//                if(j-1 < 0) {
//                    break;
//                }
//                sum += Math.abs(data[j-1] - data[j]);
//            }
//            if(sum == 0) ratio[i] = 0;
//            else  ratio[i] = (Math.abs(h-l)/sum);
//        }
//        return ratio;
//    }
    public void reFormulateData() {
        FormulateData();
        formulated = true;
    }
    public void drawGraph(Canvas g){
        if(!formulated)FormulateData();                       //저장되어 있지 않다면 계산을 새로 한다        

        double[] drawData=null;
        //2012. 7. 2   기준선 크기
        //g.glLineWidth(COMUtil.graphLineWidth);
        DrawTool t=(DrawTool)tool.elementAt(0);
        drawData=_cdm.getSubPacketData(t.getPacketTitle());

        _cvm.useJipyoSign=true;
        t.plot(g,drawData);

        //2012. 7. 2   기준선 크기
        //g.glLineWidth(COMUtil.graphLineWidth2);
//        for(int i=0;i<base.length;i++){
////            g.setColor(base_col[i]);
//            t.draw(g,base[i]);
//        }
        //2013. 9. 5 지표마다 기준선 설정 추가>>
        drawBaseLine(g);
        //2013. 9. 5 지표마다 기준선 설정 추가>>
        /*
        if(base!=null){
            for(int i=0;i<base.length;i++)t.draw(g,base[i]);
        }*/
    }
    public void drawGraph_withSellPoint(Canvas g){
    }

    public String getName(){
        return "VHF";
    }

}