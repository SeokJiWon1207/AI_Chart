package drfn.chart.graph;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;
/**
 * 거래량 이동평균 그래프
 */
public class TradeVolumnAverageGraph extends AbstractGraph{
    int[][] data;
    String[] datakind = {"종가"};
    int m_dataCnt;
    public TradeVolumnAverageGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        setDatakind(datakind);
        //definition="이동평균선의 활용방법으로는 1.주가와 이동평균선의 관계를 분석하는 방법,2. 단기 이동평균선과 장기 이동평균선의 관계를 분석하는 방법,3. 그랜빌의 정리를 이용하는 방법등이 있습니다.◇ 종목별로 가장 적당한 이동평균선을 결정하는 방법 : 종목의 최고점과 최저점 사이의 기간을 2로 나눈 후 1을 더합니다.  이 방법으로 일간이동평균선 기간을 구했으면, 주간 이동평균선은 일간/5 , 월간 이동평균선은 일간/21로 구합니다. ";
        //2023.06.27 by SJW - 거래량 이동평균선 "10일" 추가 >>
//        int[] interval1 = {5, 20,60,120,240,300};
        int[] interval1 = {5,10,20,60,120,240};
        //2023.06.27 by SJW - 거래량 이동평균선 "10일" 추가 <<
        interval = interval1;
        m_strDefinitionHtml = "amount_movingaverage.html";	//2014. 11. 20 각 보조지표 설명/활용법 추가(상세설정창)
    }
    public void FormulateData(){
        if(!formulated){
            double[] price = _cdm.getSubPacketData("기본거래량");
            if(price==null) return;
            for(int i=0;i<tool.size();i++){
                DrawTool dt = (DrawTool)tool.elementAt(i);
                m_dataCnt = price.length;
                _cdm.setSubPacketData(dt.getPacketTitle(),makeAverage(price,interval[i]));
            }
            formulated = true;
        }
    }
    public void reFormulateData(){
        formulated = false;
        FormulateData();
//    	double price[] = _cdm.getSubPacketData("종가");
//    	if(price==null) return;
//    	double moveAverageData[];
//        if(price!=null) {
//        	m_dataCnt = price.length;
//	        for(int i=0;i<tool.size();i++){
//	            DrawTool dt = (DrawTool)tool.elementAt(i);
//	            moveAverageData = _cdm.getSubPacketData(dt.getPacketTitle());
//	            if(moveAverageData==null) return;
//	            if(COMUtil.isRealTicState) {
//	            	int avgLen = moveAverageData.length-1;
//	            	System.arraycopy(moveAverageData, 1, moveAverageData, 0, avgLen);
//	            	moveAverageData[m_dataCnt-1] = getEndAverage(price, interval[i]);
//	            } else {
//	            	moveAverageData[m_dataCnt-1] = getEndAverage(price, interval[i]);
//	            }
//	            _cdm.setSubPacketData(dt.getPacketTitle(),moveAverageData);
//	        }
//        }
        formulated = true;
    }

    public void drawGraph(Canvas gl){
        if(!formulated)FormulateData();
        double[] drawData=null;

        //gl.glLineWidth(2.0f);
        for(int i=0;i<tool.size();i++){
            DrawTool t=(DrawTool)tool.elementAt(i);
            try{
                //if(t.isVisible()){
                //if(_cvm.average_state[i]){
                drawData=_cdm.getSubPacketData(t.getPacketTitle());
                _cvm.useJipyoSign=false;//지표값을 yscale에 보여줄지 여부.
                t.plot(gl,drawData);
                //}
            }catch(ArrayIndexOutOfBoundsException e){
                return;
            }catch(NullPointerException e){
                return;
            }

        }
    }
    public void drawGraph_withSellPoint(Canvas g){
    }
    public String getName(){
        return "거래량이동평균";
    }
}