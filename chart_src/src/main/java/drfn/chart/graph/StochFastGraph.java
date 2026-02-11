package drfn.chart.graph;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;
import drfn.chart.util.MinMax;

public class StochFastGraph extends AbstractGraph{
    //int[] interval = {12,5,5};
    int[][] data;

    //2014. 9. 15 매매 신호 보기 기능 추가>>
    double[] stoch_k;
    double[] stoch_d;
    //2014. 9. 15 매매 신호 보기 기능 추가<<

    public StochFastGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        String[] datakind = {"고가","저가","종가"};
        _dataKind = datakind;
        definition="현재의 주가가 일정기간의 최고가와 최저가의 범위 중 어느 정도의 수준에 있는지를 보여주는 지표입니다.  Stochastics 지수가 높을수록 현재주가가 해당기간 중 최고가 부근에 있는 것입니다.  사용자 입력수치는 Fast %K를 구하기 위한 해당기간 값과 Fast %D값을 구하기 위한 이동평균기간입니다";
        m_strDefinitionHtml = "Stochastic_Fast.html";	//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)

    }

    //------------------------------------
    // Stochastics  
    //    fast %K = {(오늘의 종가 - 최근 n일중 장중 최저가)/(최근 n일중 장중 최고가 - 최근 n일중 장중 최저가)}*100
    //    fast %D = {(오늘의 종가 - 최근 n일중 장중 최저가)의 3일 이동평균 *100}
    //              /{(최근 n일중 장중최고가 - 최근 n일중 장중 최저가의 3일 이동평균}
    //    
    //------------------------------------

    public void FormulateData() {
        double[] highData = _cdm.getSubPacketData("고가");
        double[] lowData = _cdm.getSubPacketData("저가");
        double[] closeData = _cdm.getSubPacketData("종가");
        if(closeData==null) return;
        int dLen = closeData.length;
        //2014. 9. 15 매매 신호 보기 기능 추가>>
//      double[] stoch_k = new double[dLen];
        stoch_k = new double[dLen];
        //2014. 9. 15 매매 신호 보기 기능 추가<<
        for(int i=1;i<dLen;i++){
//        for(int i=0;i<dLen;i++){
            double l=MinMax.getRangeMin(lowData,i+1,interval[0]);
            double h=MinMax.getRangeMax(highData,i+1,interval[0]);
            if(h==l)
                stoch_k[i] = 0;
            else
                stoch_k[i]= ((closeData[i]-l)*100)/(h-l);
        }
        //단순이평
        //double[] stoch_d = makeAverageD(stoch_k,interval[1], interval[0]-1);
        //지수이평 
        //2014. 9. 15 매매 신호 보기 기능 추가>>
//      double[] stoch_d = exponentialAverage(stoch_k,interval[1], interval[0]-1);
//        stoch_d = exponentialAverage(stoch_k,interval[1], interval[0]-1);
        //2014. 9. 15 매매 신호 보기 기능 추가<<
        //2019.10.08 by JJH - 보조지표 값 수정 (하나금투 HTS기준) >>
        stoch_d = exponentialAverage_DI(stoch_k,interval[1], interval[0]-1);

        for(int j=0;j<dLen;j++)
        {
            if(j<interval[0]-1)
                stoch_k[j]=0;
        }
        //2019.10.08 by JJH - 보조지표 값 수정 (하나금투 HTS기준) <<

        for(int i=0;i<tool.size();i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            if(i==0){
                _cdm.setSubPacketData(dt.getPacketTitle(),stoch_k);
                _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
            }
            else {
                _cdm.setSubPacketData(dt.getPacketTitle(),stoch_d);
                _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
            }
        }
        formulated = true;
    }
    public void reFormulateData() {
        FormulateData();
        formulated = true;
    }
    public void drawGraph(Canvas gl){
        if(!formulated)FormulateData();                       //저장되어 있지 않다면 계산을 새로 한다

        double[] drawData=null;
        //2012. 7. 2   기준선 크기
        //gl.glLineWidth(COMUtil.graphLineWidth);
        for(int i=0;i<tool.size();i++){
            DrawTool t=(DrawTool)tool.elementAt(i);
            try{
                drawData=_cdm.getSubPacketData(t.getPacketTitle());
            }catch(ArrayIndexOutOfBoundsException e){
                return;
            }
            if(i==0) _cvm.useJipyoSign=true;
            else _cvm.useJipyoSign=false;
            t.plot(gl,drawData);
        }

        //2012. 7. 2   기준선 크기
        //gl.glLineWidth(COMUtil.graphLineWidth2);
//        for(int i=0;i<base.length;i++){
//        	try {
//            DrawTool t=(DrawTool)tool.elementAt(0);
//            t.draw(gl,base[i]);
//        	} catch(Exception e) {
//        		System.out.println(e.getMessage());
//        	}
////            g.setColor(base_col[i]);
//            
//        }
        //2013. 9. 5 지표마다 기준선 설정 추가>>
        drawBaseLine(gl);
        //2013. 9. 5 지표마다 기준선 설정 추가>>

        //2014. 9. 15 매매 신호 보기 기능 추가>>
        DrawTool t=(DrawTool)tool.elementAt(0);
        if(isSellingSignalShow)
            t.drawSignal(gl, stoch_k, stoch_d);
        //2014. 9. 15 매매 신호 보기 기능 추가<<
    }
    public void drawGraph_withSellPoint(Canvas g){
    }

    public String getName(){
        return "Stochastic Fast";
    }
    public int[] upRatio(int[][] data, int interval) {
        int[] ratio = new int[data.length];
        for(int i = interval ; i < ratio.length ; i++) {
            int upNum = 0;
            for(int j= i ; j>i-interval ; j--) {
                if(data[j-1][0] < data[j][0]) upNum++;
            }
            if(upNum == 0) ratio[i] = 0;
            else  ratio[i] = (int)((upNum*100)/interval);
        }
        return ratio;
    }
}