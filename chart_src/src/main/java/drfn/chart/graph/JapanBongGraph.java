package drfn.chart.graph;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

//2011.09.27 by metalpooh >> 자동추세선은 여기서 상태를 봐서 그린다.
;
//2011.09.27 by metalpooh  <<

public class JapanBongGraph extends AbstractGraph{
    String[] datakind = {"시가","고가","저가","종가"};//그래프에 사용될 데이터
    DrawTool dt; //그래프에 사용될 드로우툴
    int sub_margin;
    double[][] data;
    AutoTrendGraph pAutoTrend = null;
    public JapanBongGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        setDatakind(datakind);
        //2011.09.27 by metalpooh  >> 자동추세선 초기화.
        pAutoTrend = new AutoTrendGraph(cvm,cdm);
        //2011.09.27 by metalpooh  << 


    }
    //===================================
    // 공식 계산 -- 데이터가 바뀌기 전까지는 한번만 한다
    //===================================
    public void FormulateData(){
        DrawTool dt = (DrawTool)tool.elementAt(0);

        double[] price = _cdm.getSubPacketData("종가");
        if(price==null) return;
        _cdm.setSubPacketData(dt.getPacketTitle(),price);

        formulated = true;
    }
    public void reFormulateData() {
        FormulateData();
        formulated = true;
    }
    int[][] drawData;
    public void drawGraph(Canvas gl){
        double[] price = _cdm.getSubPacketData("종가");

        int tLen = tool.size();
        DrawTool t=null;
        for(int i=0;i<tLen;i++){
            t=(DrawTool)tool.elementAt(i);
            //2012. 11. 21 마운틴차트 (해외선물 분차트 그라데이션) 호출을 위해 조건 추가 : C31
            if(t.getDrawType1()==1 || _cvm.bIsLineFillChart || _cvm.bStandardLine){ 	//2015.01.08 by LYH >> 3일차트 추가 <<
                t.plot(gl,price);
            }else {
                t.plot(gl,data);
                //2011.09.27 by metalpooh >> AutoTrendGraph 에서 실제 그리는 영역을 터치해준
                pAutoTrend.drawGraph(gl, t);
                //2011.09.27 by metalpooh <<
            }
        }

    }
    public void drawGraph_withSellPoint(Canvas g){
    }

    public String getName(){
        return "일본식봉";
    }
}