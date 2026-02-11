package drfn.chart.graph;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

public class PnfGraph extends AbstractGraph{
    int sub_margin;
    int[][] data;
    double[] pnf;
    int dLen;
    public PnfGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        definition="PnF의 활용방법으로 비슷한 가격대에 O표시가 많으면 지지구간으로, 비슷한 가격대에 X표시가 많으면 저항구간으로 볼수 있습니다. 전고점을 돌파하는 최초의 X표시가 매수시점입니다.전저점을 돌파하는 최초의 O표시가 매도시점입니다";
        m_strDefinitionHtml = "pnf.html";	//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)
    }

    //===================================
    // 공식 계산 -- 데이터가 바뀌기 전까지는 한번만 한다
    //===================================
    public void FormulateData(){
        double[] closeData = _cdm.getSubPacketData("종가");
        if(closeData==null) return;
        dLen = closeData.length;
        pnf = new double[dLen];
        for(int i=0;i<dLen;i++){
            pnf[i] = closeData[i];
        }
        DrawTool dt = (DrawTool)tool.elementAt(0);
        _cdm.setSubPacketData(dt.getPacketTitle(),pnf);
        formulated=true;
    }
    public void reFormulateData(){
        FormulateData();
        formulated = true;
    }
    public void drawGraph(Canvas gl){
        //if(!formulated)FormulateData();                       //저장되어 있지 않다면 계산을 새로 한다
        int num=_cvm.getViewNum();        //화면에 그릴 데이터 수
        int index=_cvm.getIndex();        //화면에 그리기 시작할 인덱스
        int margin=_cdm.getMargine();     //전체 데이터 마진
        int mar_index = margin-(dLen-index);
        if(mar_index<=0) mar_index=0;
        double[] drawData=null;
        DrawTool t=(DrawTool)tool.elementAt(0);
        drawData=_cdm.getSubPacketData(t.getPacketTitle(), index, num, mar_index);
        t.plot(gl,drawData);


    }
    public void drawGraph_withSellPoint(Canvas g){
    }

    public String getName(){
        return "PnF";
    }
}