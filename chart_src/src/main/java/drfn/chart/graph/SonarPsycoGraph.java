package drfn.chart.graph;

import android.graphics.Canvas;

import java.util.Vector;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

public class SonarPsycoGraph extends AbstractGraph {
    int[][] data;
    double[] upra;
    double[] sonar;
    Vector<DrawTool> tool;
    public SonarPsycoGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        tool = getDrawTool();//드로우 툴을 구한다
        definition="일정기간 대비 주가가 오른 날의 비율을 구함으로써 투자자들의 투자심리를 미루어 짐작하고자 하는 지표입니다.보통 75% 이상이면 과열권,25% 이하이면 침체권으로 가정합니다. 본 지표만을 가지고 투자하는 것은 적합하지 않다고 생각되며 여타의 지표에 대한 참고지표 정도로 고려하는 것이 좋습니다";

        m_strDefinitionHtml = "sonar+psyco.html";    //2018.05.02 by lyj 보조지표 설명/활용법 추가(상세설정창)
    }
   
    public void FormulateData() {
    	double[] closeData = _cdm.getSubPacketData("종가");
    	if(closeData==null) return;
        int dLen = closeData.length;
        sonar = new double[dLen];
        double[] ema= exponentialAverage(closeData,interval[0]);
        int eLen = ema.length;
        for(int i=interval[1]+interval[0];i<eLen;i++){
            sonar[i] = ema[i] - ema[i-interval[1]];
        }

        upra = upRatio(sonar, interval[2]);
        for(int i=0;i<tool.size();i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            _cdm.setSubPacketData(dt.getPacketTitle(),upra);
        }
        formulated = true;
    }
    public void reFormulateData() {
        FormulateData();
        formulated = true;
    }
    public void drawGraph(Canvas g){
        if(!formulated)FormulateData();                       //저장되어 있지 않다면 계산을 새로 한다        

        double[] drawData=null;
        //2012. 7. 2   기준선 크기
        //g.glLineWidth(COMUtil.graphLineWidth);
        DrawTool t=(DrawTool)tool.elementAt(0);
        drawData=_cdm.getSubPacketData(t.getPacketTitle());
        _cvm.useJipyoSign=true;
        
        //2012. 7. 2   기준선 크기
        //g.glLineWidth(COMUtil.graphLineWidth2);
        for(int i=0;i<base.length;i++){
//            g.setColor(base_col[i]);
            t.draw(g,base[i]);
        }

        t.plot(g,drawData);

        /*
        if(base!=null){
            for(int i=0;i<base.length;i++)t.draw(g,base[i]);
        }*/
    }
    public void drawGraph_withSellPoint(Canvas g){
    }
    public String getName(){
        return "Sonar+심리도";
    }
    public double[]upRatio(double[] data, int interval) {
    	double[] ratio = new double[data.length];
        for(int i = interval ; i < ratio.length ; i++) {
            int upNum = 0;
            for(int j= i ; j>i-interval ; j--) {
                if(data[j-1] < data[j]) upNum++;
            }
            if(upNum == 0) ratio[i] = 0;
            else  ratio[i] = (int)((upNum*100)/interval);  
        }
        return ratio;   	
    }   
}