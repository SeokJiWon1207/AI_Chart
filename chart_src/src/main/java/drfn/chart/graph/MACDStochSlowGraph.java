package drfn.chart.graph;

import android.graphics.Canvas;

import java.util.Vector;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;
import drfn.chart.util.MinMax;

public class MACDStochSlowGraph extends AbstractGraph {
    //int[] interval = {12,5,5};
    int[][] data;
    int[] slow_k;
    int[] slow_d;
    Vector<DrawTool> tool;
    public MACDStochSlowGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        String[] datakind = {"고가","저가","종가"};
        _dataKind = datakind;
        tool = getDrawTool();//드로우 툴을 구한다
        definition="Fast Stochastics는 지표의 등락이 상당히 심한 경우가 있기 때문에 Fast Stochastics의 값을 다시한번 이동평균하여 평활시킨 것이 Slow Stochastics입니다.  사용자 입력수치는 Fast %K를 구하기 위한 기간값과 Slow %K (=Fast %D)를 구하기 위한 이동평균기간, Slow %D값을 구하기 위한 이동평균기간입니다.";

        m_strDefinitionHtml = "macd+stochastic.html";    //2018.05.02 by lyj 보조지표 설명/활용법 추가(상세설정창)
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
        double[] stoch_k = new double[dLen];
        double[] macd = new double[dLen];
        double[] shortAve = exponentialAverage(closeData, interval[0]);
        double[] longAve = exponentialAverage(closeData, interval[1]);
        macd = new double[dLen];
        for(int i=interval[1]-1;i<dLen;i++)
        {
            macd[i] = (shortAve[i] - longAve[i]);
        }
        //MACD+Stochastic = MACD값을 구한 후 Stochastic %K 에 대입할 때 종가 대신 MACD값을 넣음
        for(int i=1;i<dLen;i++){
        	double l= MinMax.getRangeMin(macd,i+1,interval[2]);
            double h= MinMax.getRangeMax(macd,i+1,interval[2]);
            if(h==l)
            	stoch_k[i] = 0;
            else
            	stoch_k[i]= ((macd[i]-l)*100)/(h-l);
        }
        //2013.04.05 by LYH >> 고해상도 처리
        double[] stoch_d = exponentialAverage(stoch_k,interval[3], interval[2]-1);
        double[] slow_d = exponentialAverage(stoch_d,interval[4],interval[2]+interval[3]-2);
//        double[] stoch_d = makeAverage(stoch_k,interval[1]);
//        double[] slow_d = makeAverage(stoch_d,interval[2]);
        //2013.04.05 by LYH <<
        for(int i=0;i<tool.size();i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            if(i==0){
            	_cdm.setSubPacketData(dt.getPacketTitle(),stoch_d);
            	_cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
            }
            else {
            	_cdm.setSubPacketData(dt.getPacketTitle(),slow_d);
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
        double[] baseData=null;
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
            
            //2017. 3. 21 매매 신호 보기 기능 추가>>
            if(isSellingSignalShow) {
            	if(i==0) 
            		baseData = drawData;
            	else if(i==1)            	
            		t.drawSignal(gl, baseData, drawData);
            }
            //2017. 3. 21 매매 신호 보기 기능 추가<<
        }
        
        //2017. 3. 21 지표마다 기준선 설정 추가>>
        drawBaseLine(gl);
        //2017. 3. 21 지표마다 기준선 설정 추가>>
        
        //2012. 7. 2   기준선 크기
        //gl.glLineWidth(COMUtil.graphLineWidth2);
//        for(int i=0;i<base.length;i++){
//            DrawTool t=(DrawTool)tool.elementAt(0);
////            g.setColor(base_col[i]);
//            t.draw(gl,base[i]);
//        }
    }
    public void drawGraph_withSellPoint(Canvas g){
    }
    public String getName(){
        return "MACD+Stochastic";
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

    private double[] getLRS(int interval)
    {
        double[] closeData = _cdm.getSubPacketData("종가");
        if(closeData == null)	return null;
        int dLen = closeData.length;
        double[] dValue1 = new double[dLen];
        double[] dValue2 = new double[dLen];
        double[] dValue3 = new double[dLen];
        double[] dValue4 = new double[dLen];
        double[] dValue5 = new double[dLen];
        double[] dValue6 = new double[dLen];
        double[] retData = new double[dLen];

        for(int i = 0; i < dLen; i++)
        {
            dValue1[i] = i+1;
        }

        for(int i = interval-1; i < dLen; i++)
        {
            for(int j = i; j > i-interval; j--)
            {
                dValue2[i] += dValue1[j];
                dValue3[i] += closeData[j];
                dValue4[i] += dValue1[j]*closeData[j];
                dValue5[i] += dValue1[j]*dValue1[j];
            }
            dValue6[i] = (interval* dValue5[i] - (dValue2[i] * dValue2[i]));
            retData[i] = (interval * dValue4[i] - dValue2[i] * dValue3[i]) / dValue6[i];
        }

        return retData;
    }
}
