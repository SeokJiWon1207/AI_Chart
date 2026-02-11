package drfn.chart.graph;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

public class MarketGraph extends AbstractGraph{
    //int[] interval = {10};
    int[][] data;
    double[] reverse;
    //int[] base=null;
    public MarketGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        definition="";
    }
    //============================================
    // 외국인보유비중 등 ... :  
    //
    //============================================
    public void FormulateData() {
        String[] dataKind = {graphTitle};
        this.setDatakind(dataKind);
        double[] closeData = _cdm.getSubPacketData("종가");
        if(closeData==null) return;
//        if(this.graphTitle.equals("BaseMarket"))
//            _cdm.setSyncPriceFormat(this.graphTitle);
//        int dLen = closeData.length;

        if(this.graphTitle.equals("팔때"))
            _cdm.setSyncPriceFormat(this.graphTitle);

        formulated = true;
    }
    public void reFormulateData() {
        FormulateData();
        formulated = true;
    }
    public void drawGraph(Canvas g){
        if(!formulated)FormulateData();

        double[] drawData=null;

        int tCnt = tool.size();
        for(int i=0; i<tCnt; i++) {
            DrawTool t=(DrawTool)tool.elementAt(i);
            drawData=_cdm.getSubPacketData(t.getPacketTitle());
            if(i==0 && (getGraphType() != 2 || this.graphTitle.equals("BaseMarket") || this.graphTitle.equals("팔때") || this.graphTitle.equals("매수"))) {
                _cvm.useJipyoSign=true;
            } else {
                _cvm.useJipyoSign=false;
            }
            if(this.graphTitle.equals("신용잔고") || this.graphTitle.equals("신용잔고율")) {
                _cvm.isCredigJipyo = true;
            } else {
                _cvm.isCredigJipyo = false;
            }
            t.plot(g,drawData);
        }

//        if(base!=null){
//            for(int i=0;i<base.length;i++) {
//            	DrawTool t=(DrawTool)tool.elementAt(0);
//            	t.draw(g,base[i]);
//            }
//        }
        //2013. 9. 5 지표마다 기준선 설정 추가>>
        drawBaseLine(g);
        //2013. 9. 5 지표마다 기준선 설정 추가>>
    }
    public void drawGraph_withSellPoint(Canvas g){
    }

    public String getName(){
        return graphTitle;
    }
}