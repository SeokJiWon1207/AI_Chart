package drfn.chart.graph;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

public class COGraph extends AbstractGraph{
    //int[] base = null;
    int[][] data;
    double[] co;
    double[] signal;
    public COGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        definition="CO (Chaikin's Oscillator):AD 지표를 기초로 작성한 이동평균 오실레이터 지표로.CO값의 추세와 주가 추세를 서로 비교하여 괴리가 발생하면 CO값의 추세 방향대로 주가가 움직인다고 해석됩니다. ";
        m_strDefinitionHtml = "chaikin_osc.html";	//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)
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
        double[] closeData = _cdm.getSubPacketData("종가");
        double[] volData = _cdm.getSubPacketData("기본거래량");
        if(closeData==null) return;
        int dLen = closeData.length;
        co = new double[dLen];
        double[] AD = new double[dLen];
        //2019.10.08 by JJH - 보조지표 값 수정 (하나금투 HTS기준) >>
//        if(highData[0]!=lowData[0])AD[0] = (closeData[0]*2-lowData[0]-highData[0])*volData[0]/(highData[0]-lowData[0]);
//        else AD[0] =(closeData[0]*2-lowData[0]-highData[0])*volData[0];
        if(highData[0]!=lowData[0])
//            AD[0] = (closeData[0]*2-lowData[0]-highData[0])*volData[0]/(highData[0]-lowData[0]);
            AD[0] = ((closeData[0] - lowData[0]) - (highData[0] - closeData[0])) / (highData[0] - lowData[0]) * volData[0];
        else
//            AD[0] =(closeData[0]*2-lowData[0]-highData[0])*volData[0];
            AD[0] = 0;
        for(int i=1;i<dLen;i++){
//            if(highData[i]!=lowData[i])AD[i] = AD[i-1] +(closeData[i]*2-lowData[i]-highData[i])*volData[i]/(highData[i]-lowData[i]);
//            else AD[i] = AD[i-1];
            if(highData[i]!=lowData[i]) {
//                AD[i] = AD[i-1] +(closeData[i]*2-lowData[i]-highData[i])*volData[i]/(highData[i]-lowData[i]);
                AD[i] = ((closeData[i] - lowData[i]) - (highData[i] - closeData[i])) / (highData[i] - lowData[i]) * volData[i];
                if(i>1)
                    AD[i] += AD[i-1];
            }
            else
//                AD[i] = AD[i-1];
                AD[i] = 0;
        }
//        double[] EMA1 = exponentialAverage(AD,interval[0]);
//        double[] EMA2 = exponentialAverage(AD,interval[1]);
        double[] EMA1 = exponentialAverage_DI(AD,interval[0],1);
        double[] EMA2 = exponentialAverage_DI(AD,interval[1],1);
        for(int i=0;i<dLen;i++){
//            co[i] = (EMA1[i]-EMA2[i]);
            if(i<interval[1]){
                co[i] = 0;
                continue;
            }
            else {
                co[i] = (EMA1[i] - EMA2[i]);
            }
        }
        signal= exponentialAverage(co,interval[2],interval[0]);
        for(int i=0;i<tool.size();i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            if(i==0)
            _cdm.setSubPacketData(dt.getPacketTitle(),co);
            else
                _cdm.setSubPacketData(dt.getPacketTitle(),signal);
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

        }
        //            if(base!=null&&i<base.length)t.draw(g,base[i]);
        //2013. 9. 5 지표마다 기준선 설정 추가>>
        drawBaseLine(g);
        //2013. 9. 5 지표마다 기준선 설정 추가>>
    }
    public void drawGraph_withSellPoint(Canvas g){
    }

    public String getName(){
        //2012. 8. 8 제목다름 : I73
        return "Chaikins OSC";
    }
}