package drfn.chart.graph;

import android.graphics.Canvas;

import java.util.Vector;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

public class DisparityIndexGraph extends AbstractGraph{
    String[] datakind = {"종가"};
    int[][] data;
    Vector<DrawTool> tool;
    public DisparityIndexGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        tool = getDrawTool();
        definition="일반적으로, 단기이동평균선을 사용했을 경우,상승추세일 경우 98% 이하 매수시점, 106% 이상 매도시점.하락추세일 경우 92% 이하 매수시점, 102% 이상 매도시점이며,장기이동평균선을 사용했을 경우,상승추세일 경우 98% 이하 매수시점, 110% 이상 매도시점.하락추세일 경우 88% 이하 매수시점, 104% 이상 매도시점으로 봅니다. 실전에서는 종목에 따라 적당한 수치를 찾는 것이 중요합니다";

        m_strDefinitionHtml = "disparity_ema.html";    //2018.05.02 by lyj 보조지표 설명/활용법 추가(상세설정창)
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
            double[] ma = exponentialAverage(closeData,interval[i]);
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
        double[] baseData=null;
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

            //2020. 05. 29 by hyh - 이격도 2개에서 4개로 변경. 매매신호 제거 >>
//            //2017. 3. 21 매매 신호 보기 기능 추가>>
//            if(isSellingSignalShow) {
//                if(i==0)
//                    baseData = drawData;
//                else if(i==1)
//                    t.drawSignal(g, baseData, drawData);
//            }
//            //2017. 3. 21 매매 신호 보기 기능 추가<<
            //2020. 05. 29 by hyh - 이격도 2개에서 4개로 변경. 매매신호 제거 <<
        }
    }
    public void drawGraph_withSellPoint(Canvas g){
    }
    public String getName(){
        return "이격도[지수]";
    }
}
