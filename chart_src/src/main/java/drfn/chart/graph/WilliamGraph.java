package drfn.chart.graph;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;
import drfn.chart.util.MinMax;

public class WilliamGraph extends AbstractGraph{
    int[][] data;
    double[] william;
    double[] signal;
    public WilliamGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        definition="Williams' % R이 -80에서 -100% 사이에 있으면 시장은 과매도 상태로 볼 수 있고 0에서 -20% 범위는 과매수되고 있는 상태로 분석합니다. 과매수, 과매도 지표의 대다수가 그렇듯이 이 지표는 투자자가 방향설정에 앞서 반드시 주가 변화를 기다린 후 매매에 참여함이 바람직합니다";
        m_strDefinitionHtml = "William_s__R.html";	//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)
    }

    //============================================
    //  William's %R
    //            n일 고가 중 최고가 - 금일 종가
    //     ------------------------------------- * (-100)           (n : 14)
    //      n일 고가 중 최고가 - n일 저가 중 최저가
    //============================================
    public void FormulateData() {
        double[] highData = _cdm.getSubPacketData("고가");
        double[] lowData = _cdm.getSubPacketData("저가");
        double[] closeData = _cdm.getSubPacketData("종가");
        if(closeData==null) return;
        int dLen = closeData.length;
        william = new double[dLen];
        double high;
        double low;
        int dLen2 = dLen+1;
        for(int i=interval[0];i<dLen2;i++){
//        for(int i=0;i<dLen+1;i++){
//            if(i<2){
//                william[i]=0;
//                continue;
//            }
            high=MinMax.getRangeMax(highData,i,interval[0]);
            low = MinMax.getRangeMin(lowData,i,interval[0]);
            //william[i-1] = ((high-closeData[i-1])*(100.))/(high-low);
            if(high-low == 0)
                william[i-1] = 0;
            else
                william[i-1] = ((closeData[i-1]-high)*(100.))/(high-low);
        }
        //단순이평
        //signal= makeAverageD(william,interval[1],interval[0]);
        //지수이평 
        signal= exponentialAverage(william,interval[1],interval[0]);
        for(int i=0;i<tool.size();i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            if(i==0)_cdm.setSubPacketData(dt.getPacketTitle(),william);
            else _cdm.setSubPacketData(dt.getPacketTitle(),signal);

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
        //2012. 7. 3   Williams 마지막 선 두께 변경하면 기준선두께도 변경되는 문제 해결 
        //g.glLineWidth(COMUtil.graphLineWidth);
        for(int i=0;i<tool.size();i++){
            DrawTool t=(DrawTool)tool.elementAt(i);
            drawData=_cdm.getSubPacketData(t.getPacketTitle());
            if(i==0) _cvm.useJipyoSign=true;
            else _cvm.useJipyoSign=false;
            t.plot(g,drawData);
        }

//        if(base!=null){
//            DrawTool t=(DrawTool)tool.elementAt(0);
//            for(int i=0;i<base.length;i++){
//                t.draw(g,base[i]);
//            }
//        }
        //2013. 9. 5 지표마다 기준선 설정 추가>>
        drawBaseLine(g);
        //2013. 9. 5 지표마다 기준선 설정 추가>>

        //2014. 9. 15 매매 신호 보기 기능 추가>>
        DrawTool t=(DrawTool)tool.elementAt(0);
        if(isSellingSignalShow)
            t.drawSignal(g, william, signal);
        //2014. 9. 15 매매 신호 보기 기능 추가<<
    }
    public void drawGraph_withSellPoint(Canvas g){
    }

    public String getName(){
        //2012. 8. 8  제목다름  : I89
        return "Williams R";
    }
}