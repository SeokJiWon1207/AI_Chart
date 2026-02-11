package drfn.chart.graph;

import android.graphics.Canvas;

import java.util.Vector;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;
import drfn.chart.util.MinMax;

public class BandBStochGraph extends AbstractGraph {
    //int[] interval = {12,5,5};
    int[][] data;
    int[] slow_k;
    int[] slow_d;
    double[] ma; //중심선
    double[] ma1;//상한선
    double[] ma2;//하한선
    double[] bandB;
    Vector<DrawTool> tool;
    public BandBStochGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        String[] datakind = {"고가","저가","종가"};
        _dataKind = datakind;
        tool = getDrawTool();//드로우 툴을 구한다
        definition="Fast Stochastics는 지표의 등락이 상당히 심한 경우가 있기 때문에 Fast Stochastics의 값을 다시한번 이동평균하여 평활시킨 것이 Slow Stochastics입니다.  사용자 입력수치는 Fast %K를 구하기 위한 기간값과 Slow %K (=Fast %D)를 구하기 위한 이동평균기간, Slow %D값을 구하기 위한 이동평균기간입니다.";
        m_strDefinitionHtml = "_b+stochastic.html";    //2018.05.02 by lyj 보조지표 설명/활용법 추가(상세설정창)
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
        bandB = new double[dLen];
        bandB = getBandB(interval[0], interval[1]);
        //%B+Stochastic = %B값을 구한 후 Stochastic %K 에 대입할 때 종가 대신 %B값을 넣음
        for(int i=1;i<dLen;i++){
        	double l= MinMax.getRangeMin(bandB,i+1,interval[2]);
            double h= MinMax.getRangeMax(bandB,i+1,interval[2]);
            if(h==l)
            	stoch_k[i] = 0;
            else
            	stoch_k[i]= ((bandB[i]-l)*100)/(h-l);
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

    private double[] getBandB(int interval, int interval2) //signal 포함
    {
        //2019. 07. 04 by hyh - BollingerBand 기준가, 계산방법 추가 >>
        double[] price = null;

        if (dataTypeBollingerband == ChartViewModel.AVERAGE_DATA_OPEN){
            price = _cdm.getSubPacketData("시가");
        }
        else if (dataTypeBollingerband == ChartViewModel.AVERAGE_DATA_HIGH){
            price = _cdm.getSubPacketData("고가");
        }
        else if (dataTypeBollingerband == ChartViewModel.AVERAGE_DATA_LOW){
            price = _cdm.getSubPacketData("저가");
        }
        else if (dataTypeBollingerband == ChartViewModel.AVERAGE_DATA_CLOSE){
            price = _cdm.getSubPacketData("종가");
        }
        else if (dataTypeBollingerband == ChartViewModel.AVERAGE_DATA_HL2) {
            double[] highData = _cdm.getSubPacketData("고가");
            double[] lowData = _cdm.getSubPacketData("저가");
            if(highData != null && lowData != null)
            {
                price = new double[highData.length];

                for(int i=0; i<highData.length; i++)
                {
                    price[i] = (highData[i] + lowData[i])/2.0;
                }
            }
        }
        else if (dataTypeBollingerband == ChartViewModel.AVERAGE_DATA_HLO3) {
            double[] highData = _cdm.getSubPacketData("고가");
            double[] lowData = _cdm.getSubPacketData("저가");
            double[] closeData = _cdm.getSubPacketData("종가");

            if(highData != null && lowData != null && closeData != null)
            {
                price = new double[highData.length];

                for(int i=0; i<highData.length; i++)
                {
                    price[i] = (highData[i] + lowData[i] + closeData[i])/3.0;
                }
            }
        }
        else {
            price = _cdm.getSubPacketData("종가");
        }
        //2019. 07. 04 by hyh - BollingerBand 기준가, 계산방법 추가 <<

        if(price==null) return null;

        //2019. 07. 04 by hyh - BollingerBand 기준가, 계산방법 추가 >>
        //단순이평
        if (calcTypeBollingerband == ChartViewModel.AVERAGE_GENERAL){
            ma = makeAverage(price, interval);
        }
        //가중이평
        else if (calcTypeBollingerband == ChartViewModel.AVERAGE_WEIGHT) {
            ma = makeWeightAverage(price, interval);
        }
        //지수이평
        else if (calcTypeBollingerband == ChartViewModel.AVERAGE_EXPONENTIAL) {
            ma = exponentialAverage(price, interval);
        }
        else {
            ma = makeAverage(price, interval);
        }
        //2019. 07. 04 by hyh - BollingerBand 기준가, 계산방법 추가 <<

        int dLen = price.length;
        ma1 = new double[dLen];
        ma2 = new double[dLen];
        double[] bandb = new double[dLen];
        double[] closeData = _cdm.getSubPacketData("종가");

        double[] a= getStandardDeviation(price,ma,interval,0);
        double dStand = interval2/100.0;

        for(int i=interval-1;i<dLen;i++){
            ma1[i] = ma[i]+(dStand*a[i]);
            ma2[i] = ma[i]-(dStand*a[i]);
            if((ma1[i]-ma2[i])!=0)
                bandb[i] = (closeData[i]-ma2[i])/(ma1[i]-ma2[i])*100.0;
        }
        return bandb;
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
        return "%B+Stochastic";
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

    public double[] getStandardDeviation(double[] data, double[] average, int interval,int col) {
        if( (data == null) || (data.length < interval)) {
            return null;
        }
        double[] stDevia = new double[data.length];
        for(int i = interval -1 ; i<stDevia.length ; i++) {
            double[] deviation = new double[stDevia.length];
            for(int j= i ; j>i-interval ; j--) {
                deviation[i] += Math.pow( (data[j]-average[i]), 2);
            }
            stDevia[i] = (Math.sqrt(deviation[i]/(interval)));
        }
        return stDevia;
    }
}
