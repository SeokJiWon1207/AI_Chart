package drfn.chart.graph;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

public class VMAOGraph extends AbstractGraph{
    //int[] base = null;
    int[][] data;
    double[] vmao;
    double[] signal;
    public VMAOGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        definition="거래량의 장단기 이동평균의 차이를 표시한 것으로서, MACD와 비슷한 개념이라고 생각하면 됩니다.  Volume Oscillator라고도 하고 OSCV라고 표시하기도 합니다. 0선을 기준으로 상향돌파하면 매수시점, 하향돌파하면 매도시점으로 가정합니다";
        m_strDefinitionHtml = "Volume_Oscillator.html";	//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)
    }
    //-------------------------------------------
    // VMAO
    //  1) 포인트를 사용할 경우 : 단기 거래량 이동평균값 - 장기 거래량 이동평균 값
    //  2) 비율(%)을 사용할 경우 : (단기 거래량 이동평균 - 장기 거래량 이동평균)/단기 거래량 이동평균 *100
    //-------------------------------------------
    public void FormulateData() {
        double[] volData = _cdm.getSubPacketData("기본거래량");
        if(volData==null) return;
        int dLen = volData.length;
        vmao = new double[dLen];
        double[] shortave= makeAverage(volData,interval[0]);
        double[] highave = makeAverage(volData,interval[1]);
        for(int i=0;i<dLen;i++){

//            if(i<interval[0]+interval[1]) {
            if(i<interval[1]-1) {
                vmao[0]=0;
                continue;
            }

            double dvmao=0;
            if(shortave[i] != 0)
                dvmao=((shortave[i]-highave[i])/shortave[i]*100);
            vmao[i] = dvmao;
//        	vmao[i]=(shortave[i]-highave[i])/shortave[i] * 100;
        }
        signal= exponentialAverage(vmao,interval[2],interval[0]);
        for(int i=0;i<tool.size();i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            if(i==0) {
                _cdm.setSubPacketData(dt.getPacketTitle(), vmao);
            }else{
                _cdm.setSubPacketData(dt.getPacketTitle(),signal);
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
        //2012. 8. 8 제목다름 : I76
        //return "Volume OSC";
    	return "Volume Oscillator";
    }
}