package drfn.chart.graph;

import android.graphics.Canvas;

import java.util.Vector;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

public class StarcBandsGraph extends AbstractGraph{
	double[] graphData;
    int[][] data;//계산 전 데이터
    Vector<DrawTool> tool;
    public StarcBandsGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        tool = getDrawTool();//드로우 툴을 구한다
        definition="StarcBands";

        m_strDefinitionHtml = "starc_bands.html";    //2018.05.02 by lyj 보조지표 설명/활용법 추가(상세설정창)
    }
    //========================================
    //DEMA : 
    //========================================
    public void FormulateData(){
    	//2017.08.14 by pjm StarcBands 수정 >>
//    	graphData = getStarcBands(interval[0], interval[1]);
//	    if(graphData==null) return;
//        DrawTool dt = (DrawTool)tool.elementAt(0);
//        _cdm.setSubPacketData(dt.getPacketTitle(),graphData);
//        _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
//	    formulated = true;
    	double[] closeData = _cdm.getSubPacketData("종가");
    	double[] high = _cdm.getSubPacketData("고가");
    	double[] low = _cdm.getSubPacketData("저가");
    	if(closeData == null || high == null || low == null)	return;
    	int dLen = closeData.length;
    	double[] atr = new double[dLen]; 
    	double[] retDataUp = new double[dLen];
    	double[] retDataDown = new double[dLen]; 
    	double[] dCenter = makeAverage(closeData, interval[1]);
    	for(int i=0; i<dLen; i++){
        	atr[i] = this.ATR(i, interval[0], high, low, closeData);
        }
    	atr = makeAverage(atr, interval[0]);
        for(int i=interval[0];i<dLen;i++){
        	retDataUp[i] = dCenter[i] + (2.0 * atr[i]);
        	retDataDown[i] = dCenter[i] - (2.0 * atr[i]);
        }
        for(int i=0;i<tool.size();i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            switch(i){
                case 0:
                    _cdm.setSubPacketData(dt.getPacketTitle(),retDataUp);
                    break;
                case 1:
                    _cdm.setSubPacketData(dt.getPacketTitle(),dCenter);
                    break;
                case 2:
                    _cdm.setSubPacketData(dt.getPacketTitle(),retDataDown);
                    break;
            }
            //2014.03.18 by LYH >> 지표 소수점 자리수 가격과 동일하게 처리.
            if(_cdm.nTradeMulti>0)
                _cdm.setSyncPriceFormat(dt.getPacketTitle());
            else
                _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
            //2014.03.18 by LYH << 지표 소수점 자리수 가격과 동일하게 처리.

        }
        formulated = true;
      //2017.08.14 by pjm StarcBands 수정 <<
    }
    public void reFormulateData() {
        FormulateData();
        formulated = true;
    }
  //2017.08.14 by pjm StarcBands 수정 >>
//    private double[] getStarcBands(int interval, int interval2)
//    {
//    	double[] closeData = _cdm.getSubPacketData("종가");
//    	double[] high = _cdm.getSubPacketData("고가");
//    	double[] low = _cdm.getSubPacketData("저가");
//    	if(closeData == null || high == null || low == null)	return null;
//    	int dLen = closeData.length;
////    	double[] dUpPriceVol = new double[dLen]; 
////    	double[] dDownPriceVol = new double[dLen];
//    	double[] atr = new double[dLen]; 
//    	double[] retDataUp = new double[dLen];
//    	double[] retDataDown = new double[dLen]; 
//        
//    	double[] dCenter = makeAverage(closeData, interval2);
//    	for(int i=0; i<dLen; i++){
//        	atr[i] = this.ATR(i, interval, high, low, closeData);
//        }
//    	atr = makeAverage(atr, interval);
//        for(int i=interval;i<dLen;i++){
//        	retDataUp[i] = dCenter[i] + (2.0 * atr[i]);
//        	retDataDown[i] = dCenter[i] - (2.0 * atr[i]);
//        }
//        return retData;
//    }
  //2017.08.14 by pjm StarcBands 수정 <<
    public void drawGraph(Canvas g){
        if(!formulated)FormulateData();                       //저장되어 있지 않다면 계산을 새로 한다

        double[] drawData=null;
      //2017.08.14 by pjm StarcBands 수정 >>
//        DrawTool t=(DrawTool)tool.elementAt(0);
//        drawData=_cdm.getSubPacketData(t.getPacketTitle());
//        _cvm.useJipyoSign=true;
//
//        t.plot(g,drawData);
//        if(base!=null){
//            for(int i=0;i<base.length;i++)t.draw(g,base[i]);
//        }
        for(int i=0;i<tool.size();i++){
            DrawTool t=(DrawTool)tool.elementAt(i);
            try{
                drawData=_cdm.getSubPacketData(t.getPacketTitle());
            }catch(ArrayIndexOutOfBoundsException e){
                return;
            }
            _cvm.useJipyoSign=false;
            t.plot(g,drawData);
        }
      //2017.08.14 by pjm StarcBands 수정 <<
    }
    public void drawGraph_withSellPoint(Canvas g){
    }
    public String getName(){
        return "Starc Bands";
    }
    private double ATR(int lIndex, int lPeriod, double[] highData, double[] lowData, double[] closeData) {

    	if(closeData==null) return 0;
//    	double ldRetVal = 0;
    	double ldHigh;
    	double ldLow;
    	double ldClose;
    	double ldCloseOld;
    	double ldVal1;
    	double ldVal2;
    	double ldVal3;
    	double ldVal4;

    	if( lPeriod < 1 || lIndex < 1)
    	{
    		return 0;
    	}

    	ldHigh = highData[lIndex];
    	ldLow = lowData[lIndex];
    	ldClose = closeData[lIndex];
    	ldCloseOld = closeData[lIndex-1];

    	ldVal1 = ldHigh - ldLow;
    	ldVal2 = Math.abs(ldCloseOld - ldHigh);
    	ldVal3 = Math.abs(ldCloseOld - ldLow);

    	ldVal4 = (ldVal1 > ldVal2 ? (ldVal1 > ldVal3 ? ldVal1 : ldVal3) : (ldVal2 > ldVal3 ? ldVal2 : ldVal3));

    	return ldVal4;
    }
}