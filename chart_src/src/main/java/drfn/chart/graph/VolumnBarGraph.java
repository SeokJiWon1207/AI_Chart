package drfn.chart.graph;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

public class VolumnBarGraph extends AbstractGraph{

    String[] datakind = {"기본거래량"};
    int[][] data;
    boolean isvolume;//기본거래량인경우에는 0이상
    public VolumnBarGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        setDatakind(datakind);
        m_strDefinitionHtml = "volume.html";
    }
    public void FormulateData(){
        formulated = true;
    }
    public void reFormulateData(){
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
            if(i==0) _cvm.useJipyoSign=true;
            else _cvm.useJipyoSign=false;
            t.plot(gl,drawData);
        }
    }

    public void drawGraph_withSellPoint(Canvas g){
    }

    public String getName(){
        return "거래량";
    }
}