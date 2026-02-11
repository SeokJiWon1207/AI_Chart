package drfn.chart.graph;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

public class ATRGraph extends AbstractGraph{
    double[] atr;
    double[] atr_temp;
    double[] signal;
    int[][] data;
    //int[] base=null;

    public ATRGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        String[] datakind = {"고가","저가","종가"};
        _dataKind = datakind;
        definition="주가의 변동폭을 나타내는 지표";
        m_strDefinitionHtml = "ATR.html";	//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)
    }
    /*
     *  Comments		: {{{ ROC = (당일종가 / n일전종가) * 100 }}}
 	 *  Comments		: 미래에셋
				  			{{{ ROC = (당일종가 / n일전종가) * 100 }}}
     */
    public void FormulateData(){
        double[] highData = _cdm.getSubPacketData("고가");
        double[] lowData = _cdm.getSubPacketData("저가");
        double[] closeData = _cdm.getSubPacketData("종가");
        if(closeData==null) return;
        int dLen = closeData.length;
        atr = new double[dLen];
        atr_temp = new double[dLen];

        for(int i=0; i<dLen; i++){
            atr_temp[i] = this.ATR(i, interval[0], highData, lowData, closeData);
        }

        //atr= exponentialAverage(atr_temp,interval[0]);
        atr= makeAverage(atr_temp,interval[0]);
        //2021.01.07 by LYH >> 신한 데이터 맞춤
        for(int i=interval[0]-1; i<dLen; i++){
            atr[i] = ((atr[i-1])*(interval[0]-1)+atr_temp[i])/interval[0];
        }
        //2021.01.07 by LYH << 신한 데이터 맞춤
        signal= exponentialAverage(atr,interval[1],interval[0]);
        for(int i=0;i<tool.size();i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            if(i==0){
                _cdm.setSubPacketData(dt.getPacketTitle(),atr);
            }
            else{
              _cdm.setSubPacketData(dt.getPacketTitle(),signal);
            }
            //2018.10.30 by LYH >> 지표 소수점 자리수 가격과 동일하게 처리.
            if(_cdm.nTradeMulti>0)
                _cdm.setSyncPriceFormat(dt.getPacketTitle());
            else
                _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
            //2018.10.30 by LYH << 지표 소수점 자리수 가격과 동일하게 처리.
        }
        formulated = true;
    }

    private double ATR(int lIndex, int lPeriod, double[] highData, double[] lowData, double[] closeData) {

        if(closeData==null) return 0;
        double ldRetVal = 0;
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

    public void reFormulateData() {
        FormulateData();
        formulated = true;
    }
    public void drawGraph(Canvas g){
        if(!formulated)FormulateData();                       //저장되어 있지 않다면 계산을 새로 한다

        double[] drawData=null;
//        for(int i=0;i<1;i++){
//            DrawTool t=(DrawTool)tool.elementAt(i);
//            try{
//                drawData=_cdm.getSubPacketData(t.getPacketTitle());
//            }catch(ArrayIndexOutOfBoundsException e){
//                return;
//            }
//
//            if(i==0) _cvm.useJipyoSign=true;
//            else _cvm.useJipyoSign=false;
//
//            t.plot(g,drawData);

//        }
        for(int i=0;i<tool.size();i++){
            DrawTool t=(DrawTool)tool.elementAt(i);
            drawData=_cdm.getSubPacketData(t.getPacketTitle());
            if(i==0) _cvm.useJipyoSign=true;
            else _cvm.useJipyoSign=false;
            t.plot(g,drawData);
        }
        //          if(base!=null&&i<base.length)t.draw(g,base[i]);
        //2013. 9. 5 지  표마다 기준선 설정 추가>>
        drawBaseLine(g);
        //2013. 9. 5 지표마다 기준선 설정 추가>>


        //2014. 9. 15 매매 신호 보기 기능 추가>>
//        DrawTool t=(DrawTool)tool.elementAt(0);
//        if(isSellingSignalShow)
//        	t.drawSignal(g, pdiData, ndiData);
        //2014. 9. 15 매매 신호 보기 기능 추가<<
    }
    public void drawGraph_withSellPoint(Canvas g){
    }

    public String getName(){
        return "ATR";
    }
}
