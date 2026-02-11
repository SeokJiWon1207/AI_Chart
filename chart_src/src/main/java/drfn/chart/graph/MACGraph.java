package drfn.chart.graph;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;
/**
 * Moving Average Channels
 */
public class MACGraph extends AbstractGraph{
    boolean fomulated=false;

    double[] high, low, upper, lower;
    int[][] data;//계산 전 데이터
    int dLen = 0;
    public MACGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        definition="";
        m_strDefinitionHtml = "MAC.html";	//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)
    }
    //==========================
    // 제 1 선 : 해당일의 이동평균선의 고점*(1+설정비율)
    // 제 2 선 : 해당일 이동평균의 고점
    // 제 3 선 : 해당일 이동평균의 저점
    // 제 4 선 : 해당일 이동평균의 저점 *(1-설정비율)
    //==========================
    public void FormulateData(){
//        data = getData(1);
        if(!formulated) {
            double[] highData = _cdm.getSubPacketData("고가");
            double[] lowData = _cdm.getSubPacketData("저가");
            if(lowData==null) return;
            dLen = highData.length;

            high = makeAverage(highData,interval[0]);
            low = makeAverage(lowData,interval[0]);

            upper = new double[dLen];
            lower = new double[dLen];
            for(int i=0;i<dLen;i++){
                upper[i]=high[i]*(1+interval[1]/100.);
                lower[i]=low[i]*(1-interval[2]/100.);
            }
        }
        for(int i=0;i<tool.size();i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            if(i==0)_cdm.setSubPacketData(dt.getPacketTitle(),upper);
            else if(i==1)_cdm.setSubPacketData(dt.getPacketTitle(),high);
            else if(i==2)_cdm.setSubPacketData(dt.getPacketTitle(),low);
            else _cdm.setSubPacketData(dt.getPacketTitle(),lower);
            //2014.03.18 by LYH << 지표 소수점 자리수 가격과 동일하게 처리.
            if(_cdm.nTradeMulti>0)
                _cdm.setSyncPriceFormat(dt.getPacketTitle());
            else
                _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
            //2014.03.18 by LYH << 지표 소수점 자리수 가격과 동일하게 처리.
        }
        formulated = true;
    }
    public void reFormulateData() {
        formulated = false;
        this.FormulateData();
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
                System.out.println(e.getMessage());
                return;
            }
            _cvm.useJipyoSign=false;
            t.plot(g,drawData);
        }

    }
    public void drawGraph_withSellPoint(Canvas g){
    }

    public String getName(){
        return "MAC";
    }
}