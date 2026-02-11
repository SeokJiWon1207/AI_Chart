package drfn.chart.graph;

import android.graphics.Canvas;

import java.util.Vector;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;
import drfn.chart.util.MinMax;

public class StochasticRSIGraph extends AbstractGraph {
    
    int[][] data;
    double[] rsi;
    double[] signal;
    Vector<DrawTool> tool;
    public StochasticRSIGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        tool = getDrawTool();//드로우 툴을 구한다
        definition="Welles Wilder에 의해서 개발된 지표입니다.  시장가격의 변동폭 중에서 상승폭이 차지하는 비중이 어느정도인가를 파악하여 추세의 강도가 어느 정도인가를 측정하는 지표입니다.RSI의 수치가 70 이상이면 과열국면으로 판단하며 RSI의 수치가 30 이하이면 침체국면으로 판단합니다. 과열침체권에서는 신뢰도가 있습니다 ";

        m_strDefinitionHtml = "stochrsi.html";    //2018.05.02 by lyj 보조지표 설명/활용법 추가(상세설정창)
    }
    
  //========================================
    //Rsi : -오늘의 종가 > 전일 이전의 종가 : 주가 상승분 = 오늘의 종가 - 전일 이전의 종가,주가하락분 = 0
    //      -오늘의 종가 < 전일 이전의 종가 : 주가 하락분 = 전일 이전의 종가 - 오늘의 종가,주가상승분 = 0
    //      -오늘의 종가 = 전일 이전의 종가 : 주가 상승분 = 주가 하락분 = 0
    // 상대 모멘텀 : (주가 상승분의 14일 단순 이동평균)/(주가 하락분의 14일 단순이동평균)
    // RSI = 100-(100/(1+상대모멘텀))
    //========================================
    public void FormulateData() {
    	double[] closeData = _cdm.getSubPacketData("종가");
        double[] highData = _cdm.getSubPacketData("고가");
        double[] lowData = _cdm.getSubPacketData("저가");
    	if(closeData==null) return;
        int dLen = closeData.length;
        rsi = new double[dLen];
        double[] up_data = new double[dLen];
        double[] dn_data = new double[dLen];

        double[] stoch_k = new double[dLen];
        for(int i=1;i<dLen;i++){
            double l= MinMax.getRangeMin(lowData,i+1,interval[0]);
            double h= MinMax.getRangeMax(highData,i+1,interval[0]);
            if(h==l)
                stoch_k[i] = 0;
            else
                stoch_k[i]= ((closeData[i]-l)*100)/(h-l);
        }
        //2013.04.05 by LYH >> 지수이평->단순이평으로 변경
        double[] stoch_d = exponentialAverage(stoch_k,interval[1], interval[0]-1);
//        double[] stoch_d = makeAverage(stoch_k,interval[1]);
        //2013.04.05 by LYH <<

        for(int i=0 ; i < dLen-1 ; i++){
            up_data[i+1] =(stoch_d[i+1]-stoch_d[i]>0)?stoch_d[i+1]-stoch_d[i]:0;
            dn_data[i+1] =(stoch_d[i+1]-stoch_d[i]<0)?stoch_d[i]-stoch_d[i+1]:0;
        }
        double[] upEMA = makeAverageD(up_data,interval[2]);
        double[] downEMA = makeAverageD(dn_data,interval[2]);
        for(int j=interval[2]; j < dLen ; j++){
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
        signal= exponentialAverage(rsi,interval[3],interval[2]);
        for(int i=0;i<tool.size();i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            if(i==0){
            	_cdm.setSubPacketData(dt.getPacketTitle(),rsi);
            	_cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
            }
            else {
            	_cdm.setSubPacketData(dt.getPacketTitle(),signal);
            	_cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
            }
        }
        formulated = true;
    }
    
//    //========================================
//    //Rsi : -오늘의 종가 > 전일 이전의 종가 : 주가 상승분 = 오늘의 종가 - 전일 이전의 종가,주가하락분 = 0
//    //      -오늘의 종가 < 전일 이전의 종가 : 주가 하락분 = 전일 이전의 종가 - 오늘의 종가,주가상승분 = 0
//    //      -오늘의 종가 = 전일 이전의 종가 : 주가 상승분 = 주가 하락분 = 0
//    // 상대 모멘텀 : (주가 상승분의 14일 단순 이동평균)/(주가 하락분의 14일 단순이동평균)
//    // RSI = 100-(100/(1+상대모멘텀))
//    //========================================
//    public void FormulateData() {
//    	double[] closeData = _cdm.getSubPacketData("종가");
//    	if(closeData == null) return;
//        int dLen = closeData.length;
//        rsi = new double[dLen];
//        
//        double[] diff_data = new double[dLen];
//        
//        for(int i = 0 ; i < dLen - 1 ; i++ )
//		    diff_data[i+1] = closeData[i+1] - closeData[i];    
//        
//   	   	double	t_up = 0, t_dn = 0;
//	    double  tmp_up = 0, tmp_dn = 0;
//	    
//	    for(int i = 1 ; i < interval[0]+1 ; i++ )
//	    {
//    		if( diff_data[i] > 0 )
//    			t_up += diff_data[i];
//    		else
//    			t_dn += (diff_data[i] * -1);
//	    }
//   	    
//	    t_up = t_up/interval[0];
//	    t_dn = t_dn/interval[0];
//	    
//	    for(int i = 0 ; i < dLen ; i++ )
//	    {
//    		if( i >= interval[0] )
//    		{
//    			tmp_up = 0;
//    			tmp_dn = 0;
//    
//    			if( diff_data[i] > 0 )
//    				tmp_up = diff_data[i];
//    			else if( diff_data[i] < 0 )
//    				tmp_dn = diff_data[i];
//    
//    			t_up = t_up * (interval[0]-1) + tmp_up;
//    			t_dn = t_dn * (interval[0]-1) - tmp_dn;
//    
//    			t_up /= interval[0];
//    			t_dn /= interval[0];
//    		}
//
//    		if( (t_up != 0) || (t_dn != 0) )
//    			rsi[i] = (double)( t_up * 10000 / (t_up + t_dn)+.5 ) / 100;
//    		else
//    			rsi[i] = 0;	// Avoid Divide By 0 Error
//	    }
//
//        signal = makeAverageD(rsi,interval[1]);
//        for(int i=0;i<tool.size();i++){
//            DrawTool dt = (DrawTool)tool.elementAt(i);
//            if(i==0){
//            	_cdm.setSubPacketData(dt.getPacketTitle(),rsi);
//            	_cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
//            }
//            else {
//            	_cdm.setSubPacketData(dt.getPacketTitle(),signal);
//            	_cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
//            }
//        }
//        
//        formulated = true;
//        
//        diff_data = null;
//    }
    
    public void reFormulateData() {
        FormulateData();
        formulated = true;
    }
    public void drawGraph(Canvas gl){
        if(!formulated)FormulateData();                       //저장되어 있지 않다면 계산을 새로 한다
        if(tool==null || tool.size()==0) return;
        double[] drawData=null;
        //2012. 7. 2   기준선 크기
        //gl.glLineWidth(COMUtil.graphLineWidth);
        for(int i=0;i<tool.size();i++){
            DrawTool t=(DrawTool)tool.elementAt(i);
            drawData=_cdm.getSubPacketData(t.getPacketTitle());
            if(i==0) _cvm.useJipyoSign=true;
            else _cvm.useJipyoSign=false;
            t.plot(gl,drawData);
            //if(base!=null&&i<base.length)t.draw(g,base[i]);
        }
        
        //2013. 9. 5 지표마다 기준선 설정 추가>>
        drawBaseLine(gl);
        //2013. 9. 5 지표마다 기준선 설정 추가>>

        //2014. 9. 15 매매 신호 보기 기능 추가>>
        DrawTool t=(DrawTool)tool.elementAt(0);
        if(isSellingSignalShow)
            t.drawSignal(gl, rsi, signal);
        //2014. 9. 15 매매 신호 보기 기능 추가<<
        
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
        return "Stochastic+RSI";
    }
}