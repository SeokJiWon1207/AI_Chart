package drfn.chart.graph;

import java.util.Vector;

import android.graphics.Canvas;
import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

public class VolumeSellBuyGraph extends AbstractGraph{

    String[] datakind = {"매도거래량","매수거래량","매수매도거래량"};
    Vector<double[]> v;
    int[][] data;
    boolean isvolume;//기본거래량인경우에는 0이상
    public VolumeSellBuyGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        setDatakind(datakind);
        m_strDefinitionHtml = "buy_sell_volume.html";
    }
    public void FormulateData(){
        if(!formulated){
            v = new Vector<double[]>();
            double[] sellData = _cdm.getSubPacketData("매도거래량");
            double[] buyData = _cdm.getSubPacketData("매수거래량");

            if(sellData==null || buyData==null) return;
            int dLen = buyData.length;
            double[] buySellData = new double[dLen];
            for(int i=0;i<dLen;i++){
                buySellData[i] = sellData[i] + buyData[i];
            }
            DrawTool dt = (DrawTool)tool.elementAt(0);
            _cdm.setSubPacketData(dt.getPacketTitle(),buySellData);
            _cdm.setPacketFormat(dt.getPacketTitle(), "× 1");
        }
        formulated = true;
    }
    public void reFormulateData(){
        formulated = false;
        FormulateData();
        formulated = true;
    }
    public void drawGraph(Canvas gl){
        if(!formulated)FormulateData();

        double[] drawData=null;
        for(int i=0;i<tool.size();i++){
            DrawTool t=(DrawTool)tool.elementAt(i);
            try{
                drawData=_cdm.getSubPacketData(t.getPacketTitle());
                if(drawData==null) return;
            }catch(ArrayIndexOutOfBoundsException e){
                //System.out.println("... exception : " + e.toString());
                return;
            }
//            if(i==0) _cvm.useJipyoSign=true;
//            else _cvm.useJipyoSign=false;
            t.plot(gl,drawData);
        }
    }

    public void drawGraph_withSellPoint(Canvas g){
    }

    public String getName(){
        return "매수매도 거래량";
    }
}