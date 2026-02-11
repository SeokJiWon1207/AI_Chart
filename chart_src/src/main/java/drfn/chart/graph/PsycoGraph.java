package drfn.chart.graph;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

public class PsycoGraph extends AbstractGraph{
    int[][] data;
    double[] upra;
    double[] signal;
    public PsycoGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        definition="일정기간 대비 주가가 오른 날의 비율을 구함으로써 투자자들의 투자심리를 미루어 짐작하고자 하는 지표입니다.보통 75% 이상이면 과열권,25% 이하이면 침체권으로 가정합니다. 본 지표만을 가지고 투자하는 것은 적합하지 않다고 생각되며 여타의 지표에 대한 참고지표 정도로 고려하는 것이 좋습니다";
        m_strDefinitionHtml = "psychology.html";	//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)
    }

    public void FormulateData() {

        double[] closeData = _cdm.getSubPacketData("종가");
        if(closeData==null) return;
        upra = upRatio(closeData, interval[0]);
        signal= exponentialAverage(upra,interval[1],interval[0]);
        for(int i=0;i<tool.size();i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            if(i==0) {
                _cdm.setSubPacketData(dt.getPacketTitle(), upra);
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
        //2012. 7. 2   기준선 크기
        //g.glLineWidth(COMUtil.graphLineWidth);
//        DrawTool t=(DrawTool)tool.elementAt(0);
//        drawData=_cdm.getSubPacketData(t.getPacketTitle());

//        _cvm.useJipyoSign=true;
//        t.plot(g,drawData);
        for(int i=0;i<tool.size();i++){
            DrawTool t=(DrawTool)tool.elementAt(i);
            drawData=_cdm.getSubPacketData(t.getPacketTitle());
            if(i==0) _cvm.useJipyoSign=true;
            else _cvm.useJipyoSign=false;
            t.plot(g,drawData);
        }


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
        return "심리도";
    }
    public double[]upRatio(double[] data, int interval) {
        double[] ratio = new double[data.length];
//        for(int i = interval ; i < ratio.length ; i++) {
        for(int i = 1 ; i < ratio.length ; i++) {
            int upNum = 0;
            for(int j= i ; j>i-interval ; j--) {
                if(j<1) break;
                if(data[j-1] < data[j]) upNum++;
            }
            if(upNum == 0) ratio[i] = 0;
            else  ratio[i] = (int)((upNum*100)/interval);
        }
        return ratio;
    }
}