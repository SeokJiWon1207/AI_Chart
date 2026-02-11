package drfn.chart.graph;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

public class ReverseGraph extends AbstractGraph{
    //int[] interval = {10};
    int[][] data;
    double[] reverse,reverse1;
    //int[] base=null;
    public ReverseGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        definition="주가가 상승과정에서는 에너지를 소비하고 하락과정에서는 에너지가 축적된다는 전제를 가지고, 주가의 에너지의 수준을 보여주는 지표입니다.  본 지표는 주가의 방향과 역의 방향으로 움직이기 때문에 Reverse라는 이름을 갖게 되었습니다.  사용자 입력수치는 기간값입니다";
        m_strDefinitionHtml = "Reverse.html";	//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)
    }
    //============================================
    // Reverse :  
    //(n일간의 하락폭 합의 절대값 - n일간 상승폭 합의 절대값)/n일간 하락폭 합의 절대값과 n일간 상승폭합의 절대값 중 큰값
    //============================================
    public void FormulateData() {
        double[] closeData = _cdm.getSubPacketData("종가");
        if(closeData==null) return;
        int dLen = closeData.length;
        reverse = new double[dLen];
        reverse1 = new double[dLen];
        int i,j;
        for(i=interval[0];i<dLen;i++){
            float PlusSUM=0;
            float MinusSUM=0;
            for(j=i;j>i-interval[0];j--){
                double daebi = closeData[j]-closeData[j-1];
                if(daebi<0)MinusSUM+=Math.abs(daebi);
                else PlusSUM+=daebi;
            }
            reverse[i]=((MinusSUM-PlusSUM)*100/(Math.max(MinusSUM,PlusSUM)));
        }
        for(i=interval[1];i<dLen;i++){
            float PlusSUM=0;
            float MinusSUM=0;
            for(j=i;j>i-interval[1];j--){
                double daebi = closeData[j]-closeData[j-1];
                if(daebi<0)MinusSUM+=Math.abs(daebi);
                else PlusSUM+=daebi;
            }
            reverse1[i]=((MinusSUM-PlusSUM)*100/(Math.max(MinusSUM,PlusSUM)));
        }
        for(i=0;i<tool.size();i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            if(i==0) {
                _cdm.setSubPacketData(dt.getPacketTitle(), reverse);
            }else{
                _cdm.setSubPacketData(dt.getPacketTitle(), reverse1);
            }
            _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
        }
        formulated = true;
    }
    public void reFormulateData() {
        FormulateData();
        formulated = true;
    }
    public void drawGraph(Canvas g){
        if(!formulated)FormulateData();

        double[] drawData=null;
        for(int i=0;i<tool.size();i++) {
            DrawTool t = (DrawTool) tool.elementAt(i);
            try {
                drawData = _cdm.getSubPacketData(t.getPacketTitle());
            }catch(ArrayIndexOutOfBoundsException e){
                return;
            }
            if(i==0) _cvm.useJipyoSign = true;
            else _cvm.useJipyoSign=false;
            t.plot(g, drawData);
        }

//        if(base!=null){
//            for(int i=0;i<base.length;i++)t.draw(g,base[i]);
//        }
        //2013. 9. 5 지표마다 기준선 설정 추가>>
        drawBaseLine(g);
        //2013. 9. 5 지표마다 기준선 설정 추가>>
    }
    public void drawGraph_withSellPoint(Canvas g){
    }

    public String getName(){
        return "Reverse";
    }
}