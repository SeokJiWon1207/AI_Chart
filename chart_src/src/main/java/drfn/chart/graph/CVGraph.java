package drfn.chart.graph;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

public class CVGraph extends AbstractGraph{
    //int[] base = null;
    int[][] data;
    double[] cv;
    double[] signal;
    public CVGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        definition="CO (Chaikin's Oscillator):AD 지표를 기초로 작성한 이동평균 오실레이터 지표로.CO값의 추세와 주가 추세를 서로 비교하여 괴리가 발생하면 CO값의 추세 방향대로 주가가 움직인다고 해석됩니다. ";
        m_strDefinitionHtml = "chaikin_s_volatility.html";	//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)
    }
    //-------------------------------------------
    // CO : AD지표를 기초로 작성한 이동평균선 오실레이터 지표
    // 
    //      CO = AD지표의 3일 지수 이동평균 - AD지표의 10일 지수 이동평균
    //-------------------------------------------
    public void FormulateData() {
//        data = getData(1);
        double[] highData = _cdm.getSubPacketData("고가");
        double[] lowData = _cdm.getSubPacketData("저가");

        if(highData==null) return;
        int dLen = highData.length;
        cv = new double[dLen];
        double[] cvHighLow = new double[dLen];
        for(int i=0;i<dLen;i++){
            cvHighLow[i] = highData[i] - lowData[i];
        }
        //2019.10.08 by JJH - 보조지표 값 수정 (하나금투HTS기준) >>
//        double[] cv1 = exponentialAverage(cvHighLow,interval[0]);
        double[] cv1 = exponentialAverage_DI(cvHighLow,interval[0],interval[0]);
        for(int i=0; i<dLen; i++){
//            if(i<interval[0] || i-interval[1]<0 || cv1[i-interval[1]]==0) {
            if(i<interval[0]*2 || i-interval[0]<0 || cv1[i-interval[0]]==0) {
                //2019.10.08 by JJH - 보조지표 값 수정 (하나금투HTS기준) >>
                cv[i] = 0;
            } else {
                cv[i] = ((cv1[i] - cv1[i-interval[0]]) / cv1[i-interval[0]]) * 100;
            }
        }
        signal= exponentialAverage(cv,interval[1]);
        for(int i=0;i<tool.size();i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            if(i==0)_cdm.setSubPacketData(dt.getPacketTitle(),cv);
            else if(i==1) _cdm.setSubPacketData(dt.getPacketTitle(),signal);
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
//            if(base!=null&&i<base.length)t.draw(g,base[i]);
            //2013. 9. 5 지표마다 기준선 설정 추가>>
            drawBaseLine(g);
            //2013. 9. 5 지표마다 기준선 설정 추가>>
        }
    }
    public void drawGraph_withSellPoint(Canvas g){
    }

    public String getName(){
        //2012. 8. 8 제목다름 : I73
        return "Chaikins Volatility";
    }
}