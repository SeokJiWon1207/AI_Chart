package drfn.chart.graph;

import android.graphics.Canvas;

import java.util.Vector;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

public class RSIMACDGraph extends AbstractGraph {
//    int[] interval={12,26,9};
    int sub_margin;
    int[][] data;
    double[] macd;
    double[] rsi;
    double[] signal;
    Vector<DrawTool> tool;
    public RSIMACDGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        String[] datakind = {"종가"};
        _dataKind = datakind;
        tool = getDrawTool();
        definition="MACD는 주가의 장,단기 이동평균선의 관계를 보여주는 지표입니다.  MACD는 단순이동평균을 사용하지 않고 지수이동평균 (Exponential Moving Average)을 사용합니다.  교차분석, 과매도/과매수 분석 등을 통해 매매시점을 잡아낼 수 있습니다.  사용자 입력수치는 단기 이동평균기간, 장기이동평균기간, Signal Line을 구하는 이동평균기간입니다.  추천하는 기간값은 단기 12, 장기 26, 시그널라인 9입니다.  MACD에서 발전한 지표가 MACD OSC입니다";

        m_strDefinitionHtml = "rsi+macd.html";    //2018.05.02 by lyj 보조지표 설명/활용법 추가(상세설정창)
    }
    public void FormulateData(){
    	double[] price = _cdm.getSubPacketData("종가");
    	if(price==null) return;
	    int dLen = price.length;
	    macd = new double[dLen];
        rsi = new double[dLen];
        double[] up_data = new double[dLen];
        double[] dn_data = new double[dLen];

        for(int i=0 ; i < dLen-1 ; i++){
            up_data[i+1] =(price[i+1]-price[i]>0)?price[i+1]-price[i]:0;
            dn_data[i+1] =(price[i+1]-price[i]<0)?price[i]-price[i+1]:0;
        }
        double[] upEMA = makeAverageD(up_data,interval[0]);
        double[] downEMA = makeAverageD(dn_data,interval[0]);
        for(int j=interval[0]; j < dLen ; j++){
            //2015. 9. 18 특정종목에서 RSI 그리지 않음>>
            if( (downEMA[j]+upEMA[j]) > 0 )
            {
                rsi[j]=upEMA[j]*100/(downEMA[j]+upEMA[j]);
            }
            else
            {
                rsi[j] = 0;
            }
            //2015. 9. 18 특정종목에서 RSI 그리지 않음<<
        }
        double[] shortAve = exponentialAverage(rsi, interval[1]);
        double[] longAve = exponentialAverage(rsi, interval[2]);

	    for(int i=interval[2]-1;i<dLen;i++)macd[i] = (shortAve[i] - longAve[i]);
	    signal= exponentialAverage(macd, interval[3]);
	    for(int i=0;i<tool.size();i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            if(i==0) {
            	_cdm.setSubPacketData(dt.getPacketTitle(),macd);
            	_cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
            }
            else {
            	_cdm.setSubPacketData(dt.getPacketTitle(),signal);
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
        if(!formulated)FormulateData();
        
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
            //if(base!=null&&i<base.length)t.draw(g,base[i]);
        }
        if(tool.size()==0) return;
        //2012. 7. 2   기준선 크기
        //gl.glLineWidth(COMUtil.graphLineWidth2);
//        for(int i=0;i<base.length;i++){
//            DrawTool t=(DrawTool)tool.elementAt(0);
//            t.draw(gl,base[i]);
//        }
        
        //2017. 3. 21 지표마다 기준선 설정 추가>>
        drawBaseLine(gl);
        //2017. 3. 21 지표마다 기준선 설정 추가>>

        //2017. 3. 21 매매 신호 보기 기능 추가>>
        DrawTool t=(DrawTool)tool.elementAt(0);
        if(isSellingSignalShow)
            t.drawSignal(gl, macd, signal);
        //2017. 3. 21 매매 신호 보기 기능 추가<<
    }
    public void drawGraph_withSellPoint(Canvas g){
    }
    public String getName(){
        return "RSI+MACD";
    }
}