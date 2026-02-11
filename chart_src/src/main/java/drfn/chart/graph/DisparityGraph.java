package drfn.chart.graph;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

public class DisparityGraph extends AbstractGraph{
    String[] datakind = {"종가"};
    int[][] data;
    public DisparityGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        definition="일반적으로, 단기이동평균선을 사용했을 경우,상승추세일 경우 98% 이하 매수시점, 106% 이상 매도시점.하락추세일 경우 92% 이하 매수시점, 102% 이상 매도시점이며,장기이동평균선을 사용했을 경우,상승추세일 경우 98% 이하 매수시점, 110% 이상 매도시점.하락추세일 경우 88% 이하 매수시점, 104% 이상 매도시점으로 봅니다. 실전에서는 종목에 따라 적당한 수치를 찾는 것이 중요합니다";
        m_strDefinitionHtml = "Disparity.html";	//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)
    }

    //====================================
    // 이격도
    // 종가/n일 종가 이동평균 *100
    //====================================
    public void FormulateData(){
//        data = getData(1);
        double[] closeData = _cdm.getSubPacketData("종가");
        if(closeData==null) return;
        int dLen = closeData.length;
        int tLen = tool.size();
        for(int i=0;i<tLen;i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            double[] ma = makeAverage(closeData,interval[i]);
            double[] disp= new double[dLen];

            for(int j=0;j<dLen;j++){
                if(j<interval[i]) {
                    disp[j]=0;
                    continue;
                }
                if(ma[j]!=0)disp[j] = (closeData[j]*100)/ma[j];
                else disp[j]=closeData[j]*100;
            }
            _cdm.setSubPacketData(dt.getPacketTitle(),disp);
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
        DrawTool t1=(DrawTool)tool.elementAt(1);
        double[] t_data = _cdm.getSubPacketData(t.getPacketTitle());
        double[] t1_data = _cdm.getSubPacketData(t1.getPacketTitle());
        if(isSellingSignalShow)
            t.drawSignal(g, t_data, t1_data);
        //2014. 9. 15 매매 신호 보기 기능 추가<<
    }
    public void drawGraph_withSellPoint(Canvas g){
    }

    public String getName(){
        return "이격도";
    }
}