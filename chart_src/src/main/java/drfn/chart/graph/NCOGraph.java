package drfn.chart.graph;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

public class NCOGraph extends AbstractGraph{
    double[] nco;
    double[] signal;
    int[][] data;
    //int[] base=null;

    public NCOGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        String[] datakind = {"고가","저가","종가"};
        _dataKind = datakind;
        definition="NCO는 일종의 모멘텀을 이용한 시장분석 방법으로 단기추세를 확인하는데 사용되는 유용한 지표이다.";
        m_strDefinitionHtml = "nco.html";	//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)
    }
    /*
     *  Comments		: {{{ NCO = 당일종가 / n일 전 종가 * 100 }}}
     */
    public void FormulateData(){
        double[] closeData = _cdm.getSubPacketData("종가");
        if(closeData==null) return;
        int dLen = closeData.length;
        nco = new double[dLen];

        for(int i=interval[0]; i<dLen; i++){
            nco[i] = this.NCO(i, interval[0], closeData);
        }

        signal= exponentialAverage(nco,interval[1],interval[0]);

        for(int i=0;i<tool.size();i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            if(i==0)_cdm.setSubPacketData(dt.getPacketTitle(),nco);
            else  _cdm.setSubPacketData(dt.getPacketTitle(),signal);

            _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
        }
        formulated = true;
    }

    private double NCO(int lIndex, int lPeriod, double[] closeData) {
        if(closeData==null) return 0;
        double ldRetVal = 0;

        double ldClose;
        double ldCloseOld;
        if (lIndex < lPeriod || lPeriod < 1)
        {
            return 0;
        }
        ldClose = closeData[lIndex];
        ldCloseOld = closeData[lIndex - lPeriod];
//    	ldRetVal = ( (ldClose - ldCloseOld) / ldCloseOld ) * 100.; //_MIRAE
        if(ldCloseOld == 0)
            ldRetVal = 0;
        else
//            ldRetVal = (ldClose / ldCloseOld) * 100.;
            ldRetVal = ldClose - ldCloseOld;	//Daewoo, 신한

        return ldRetVal;
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
        for(int i=0;i<tool.size();i++){
            DrawTool t=(DrawTool)tool.elementAt(i);
            drawData=_cdm.getSubPacketData(t.getPacketTitle());
            if(i==0) _cvm.useJipyoSign=true;
            else _cvm.useJipyoSign=false;
            t.plot(g,drawData);
        }
//          if(base!=null&&i<base.length)t.draw(g,base[i]);
            //2013. 9. 5 지표마다 기준선 설정 추가>>
            drawBaseLine(g);
            //2013. 9. 5 지표마다 기준선 설정 추가>>
//        }


        //2014. 9. 15 매매 신호 보기 기능 추가>>
//        DrawTool t=(DrawTool)tool.elementAt(0);
//        if(isSellingSignalShow)
//        	t.drawSignal(g, pdiData, ndiData);
        //2014. 9. 15 매매 신호 보기 기능 추가<<
    }
    public void drawGraph_withSellPoint(Canvas g){
    }

    public String getName(){
        return "NCO";
    }
}
