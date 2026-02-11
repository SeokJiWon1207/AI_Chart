package drfn.chart.graph;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

public class MACD_OSCGraph extends AbstractGraph{
    //    int[] interval={12,26,9};
    int sub_margin;
    int[][] data;
    double[] macd;
    double[] signal;
    double[] macd_oscillator;
    public MACD_OSCGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        String[] datakind = {"종가"};
        _dataKind = datakind;
        definition="MACD는 주가의 장,단기 이동평균선의 관계를 보여주는 지표입니다.  MACD는 단순이동평균을 사용하지 않고 지수이동평균 (Exponential Moving Average)을 사용합니다.  교차분석, 과매도/과매수 분석 등을 통해 매매시점을 잡아낼 수 있습니다.  사용자 입력수치는 단기 이동평균기간, 장기이동평균기간, Signal Line을 구하는 이동평균기간입니다.  추천하는 기간값은 단기 12, 장기 26, 시그널라인 9입니다.  MACD에서 발전한 지표가 MACD OSC입니다";
        m_strDefinitionHtml = "MACD.html";	//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)
    }
    public void FormulateData(){
        double[] price = _cdm.getSubPacketData("종가");
        if(price==null) return;
        double[] shortAve = exponentialAverage(price, interval[0]);
        double[] longAve = exponentialAverage(price, interval[1]);
        int dLen = price.length;
        macd = new double[dLen];
        macd_oscillator = new double[dLen];

        //for(int i=interval[1]-1;i<dLen;i++) {
        for(int i=0;i<dLen;i++) {
            macd[i] = (shortAve[i] - longAve[i]);
        }
        signal= exponentialAverage(macd, interval[2]);
        for(int i=0; i<dLen; i++){
            if(i==0)
                macd_oscillator[i] = 0;
            else
                macd_oscillator[i] = macd[i]-signal[i];
        }
        for(int i=0;i<tool.size();i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            if(i==0) {
                _cdm.setSubPacketData(dt.getPacketTitle(),macd);
            }
            else if(i==1){
                _cdm.setSubPacketData(dt.getPacketTitle(),signal);
            }
            else
                _cdm.setSubPacketData(dt.getPacketTitle(),macd_oscillator);
            //2014.03.18 by LYH << 지표 소수점 자리수 가격과 동일하게 처리.
//            if(_cdm.nTradeMulti>0)
//                _cdm.setSyncPriceFormat(dt.getPacketTitle());
//            else
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
        if(!formulated)FormulateData();

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
            //if(base!=null&&i<base.length)t.draw(g,base[i]);
        }
        if(tool.size()==0) return;
        //2012. 7. 2   기준선 크기
        //gl.glLineWidth(COMUtil.graphLineWidth2);
//        for(int i=0;i<base.length;i++){
//            DrawTool t=(DrawTool)tool.elementAt(0);
//            t.draw(gl,base[i]);
//        }
        //2013. 9. 5 지표마다 기준선 설정 추가>>
        drawBaseLine(gl);
        //2013. 9. 5 지표마다 기준선 설정 추가>>

        //2014. 9. 15 매매 신호 보기 기능 추가>>
        DrawTool t=(DrawTool)tool.elementAt(0);
        if(isSellingSignalShow)
            t.drawSignal(gl, macd, signal);
        //2014. 9. 15 매매 신호 보기 기능 추가<<
    }
    public void drawGraph_withSellPoint(Canvas g){
    }

    public String getName(){
        return "MACD+OSC";
    }
}