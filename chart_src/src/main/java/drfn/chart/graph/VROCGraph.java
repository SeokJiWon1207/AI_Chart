package drfn.chart.graph;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

public class VROCGraph extends AbstractGraph{
    double[] roc1;
    double[] roc2;
    double[] signal;
    int[][] data;
    //int[] base=null;

    public VROCGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        String[] datakind = {"고가","저가","종가"};
        _dataKind = datakind;
        definition="이 지표는 Roc는 당일의 주가를 특정일의 주가로 나눈 것으로 추세의 속도를 측정한다.";
        m_strDefinitionHtml = "vroc.html";	//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)
    }
    /*
     *  Comments		: {{{ ROC = (당일종가 / n일전종가) * 100 }}}
 	 *  Comments		: 미래에셋
				  			{{{ ROC = (당일종가 / n일전종가) * 100 }}}
     */
    public void FormulateData(){
//    	double[] closeData = _cdm.getSubPacketData("종가");
        double[] volData = _cdm.getSubPacketData("기본거래량");
        if(volData==null) return;
        int dLen = volData.length;
        roc1 = new double[dLen];
        roc2 = new double[dLen];

        for(int i=interval[0]; i<dLen; i++){
            roc1[i] = this.VROC(i, interval[0], volData);
        }

//        signal= exponentialAverage(roc1,interval[1],interval[0]);	 

        for(int i=0;i<1;i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            if(i==0)_cdm.setSubPacketData(dt.getPacketTitle(),roc1);
//            else if(i==1) _cdm.setSubPacketData(dt.getPacketTitle(),signal);
            _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
        }
        formulated = true;
    }

    private double VROC(int lIndex, int lPeriod, double[] volData) {
        if(volData==null) return 0;
        double ldRetVal = 0;

        double ldClose;
        double ldCloseOld;
        if (lIndex < lPeriod || lPeriod < 1)
        {
            return 0;
        }
        ldClose = volData[lIndex];
        ldCloseOld = volData[lIndex - lPeriod];
        //2019.10.08 by JJH - 보조지표 값 수정 (하나금투 HTS기준) >>
        if(ldCloseOld == 0)
            ldRetVal = 0; //16.09.01 hantu
        else
            ldRetVal = ( (ldClose - ldCloseOld) / ldCloseOld ) * 100.; //_MIRAE
        //2019.10.08 by JJH - 보조지표 값 수정 (하나금투 HTS기준) <<
//    	System.out.println("Debug_ldCloseOld:"+ldCloseOld);
//    	ldRetVal = (ldClose / ldCloseOld) * 100.;

        return ldRetVal;
    }

    public void reFormulateData() {
        FormulateData();
        formulated = true;
    }
    public void drawGraph(Canvas g){
        if(!formulated)FormulateData();                       //저장되어 있지 않다면 계산을 새로 한다

        double[] drawData=null;
        for(int i=0;i<1;i++){
            DrawTool t=(DrawTool)tool.elementAt(i);
            try{
                drawData=_cdm.getSubPacketData(t.getPacketTitle());
            }catch(ArrayIndexOutOfBoundsException e){
                return;
            }

            if(i==0) _cvm.useJipyoSign=true;
            else _cvm.useJipyoSign=false;

            t.plot(g,drawData);
//          if(base!=null&&i<base.length)t.draw(g,base[i]);
            //2013. 9. 5 지표마다 기준선 설정 추가>>
            drawBaseLine(g);
            //2013. 9. 5 지표마다 기준선 설정 추가>>
        }


        //2014. 9. 15 매매 신호 보기 기능 추가>>
//        DrawTool t=(DrawTool)tool.elementAt(0);
//        if(isSellingSignalShow)
//        	t.drawSignal(g, pdiData, ndiData);
        //2014. 9. 15 매매 신호 보기 기능 추가<<
    }
    public void drawGraph_withSellPoint(Canvas g){
    }

    public String getName(){
        return "VROC";
    }
}
