package drfn.chart.graph;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

public class MIGraph extends AbstractGraph{
    double[] miData;
    //int[] base = null;
    double[] signal;
    int[][] data;

    public MIGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        definition="Mass Index는 주가의 9일 지수 이동평균선이 주로 반전우세가 매수 신호인지 매도 신호인지를 결정하는데 사용되는데, 만약 반전우세 현상이 발생되었을 때 9일 지수 이동평균선이 상승추세이면 매도 신호롤 보고 하락추세이면 매수 신호로 분석합니다";
        m_strDefinitionHtml = "Mass_Index.html";	//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)
    }
    //==============================
    // 1. 고가와 저가 차이의 9일 지수 이동평균을 계산
    // 2. 이 값의 9일 지수 이동평균 계산
    // 3. 2에서 계산된 값으로 1의 값을 나눔
    // 4. MI를 계산하기 위한 기간 값을 합하여 구함
    //==============================
    public void FormulateData(){
//        data = getData(1);
        double[] highData = _cdm.getSubPacketData("고가");
        double[] lowData = _cdm.getSubPacketData("저가");
        if(lowData==null) return;
        int dLen = lowData.length;
        miData = new double[dLen];

        double[] t1=new double[dLen];
        for(int i=0; i<dLen; i++){
            t1[i] = Math.abs(highData[i]- lowData[i]);
        }
        t1 = exponentialAverage(t1, 9);
        double[] t2 = exponentialAverage(t1, 9);
        double total=0.0;
        //2019.10.08 by JJH - 보조지표 값 수정 (하나금투 HTS기준) >>
//        for(int i=interval[0]+8+8; i<dLen; i++){
        for(int i=interval[0]+8+7; i<dLen; i++){
            //2019.10.08 by JJH - 보조지표 값 수정 (하나금투 HTS기준) <<
            total=0.0;
            for(int j= i ; j>i-interval[0] ; j--) {
                total += ((double)t1[j]/(double)t2[j]);
            }
            miData[i] = total;
        }
        signal= exponentialAverage(miData,interval[1],interval[0]);
        for(int i=0;i<tool.size();i++) {
            DrawTool dt = (DrawTool) tool.elementAt(i);
            if (i == 0) {
                _cdm.setSubPacketData(dt.getPacketTitle(), miData);
            } else {
                _cdm.setSubPacketData(dt.getPacketTitle(), signal);
            }

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
            drawData=_cdm.getSubPacketData(t.getPacketTitle());
            if(i==0) _cvm.useJipyoSign=true;
            else _cvm.useJipyoSign=false;
            t.plot(g,drawData);
        }
//        for(int i=0;i<tool.size();i++){
//            DrawTool t=(DrawTool)tool.elementAt(i);
//            try{
//                drawData=_cdm.getSubPacketData(t.getPacketTitle());
//            }catch(ArrayIndexOutOfBoundsException e){
//                return;
//            }
//            if(i==0) _cvm.useJipyoSign=true;
//            else _cvm.useJipyoSign=false;
//            t.plot(g,drawData);
//        }
//        if(base!=null){
//            DrawTool t=(DrawTool)tool.elementAt(0);
//            for(int i=0;i<base.length;i++){
//                t.draw(g,base[i]);
//            }
//        }
        //2013. 9. 5 지표마다 기준선 설정 추가>>
        drawBaseLine(g);
        //2013. 9. 5 지표마다 기준선 설정 추가>>
    }
    public void drawGraph_withSellPoint(Canvas g){
    }

    public String getName(){
        //2012. 8. 8 제목다름  : I74
        return "Mass Index";
    }
}