package drfn.chart.graph;

import android.graphics.Canvas;
import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

/**
 * BollingerBand 그래프
 */
public class SigmaGraph extends AbstractGraph{
    String[] datakind = {"종가"};
    int[][] data;
    double[] ma; //중심선
    double[] sigma;//sigma
    double[] signal;
    public SigmaGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        definition="주가가 어떤 밴드를 돌파하면 돌파된 밴드는 지지선의 역할을 하고, 그 상위 밴드는 저항선의 역할을 한다. 특히 중간밴드는 하락추세에서는 저항선의, 상승추세에서는 지지선의 역할을 한다. 밴드의 폭이 좁아진 후에는 주가가 급격하게 움직이는 경향이 있다. 주가가 상하한 밴드 폭 밖으로 이탈하면 현재의 추세가 유지되는 경향이 있다.이탈된 주가가 밴드 폭 안으로 들어올 때 추세가 전환된 것으로 판단한다";
        m_strDefinitionHtml = "sigma.html";	//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)
    }
    //=====================================
    //Boolinger Bands  :  해당 기간(20일간)의 이동 평균 M, 표준 편차 a
    //                    1) 상위  :  M+2a   (파랑)
    //                    2) 중심  :  M   (빨강)
    //                    3) 하위  :  M-2a   (파랑)    
    //=====================================
    public void FormulateData(){
        double[] closeData = _cdm.getSubPacketData("종가");
        if(closeData==null) return;
        int dLen = closeData.length;
        ma = makeAverage(closeData,interval[0]);
        sigma = new double[dLen];
        double[] a= getStandardDeviation(closeData,ma,interval[0],0);
        for(int i=interval[0]-1;i<dLen;i++){
            sigma[i] = (closeData[i]-ma[i])/a[i];
        }
       // signal= exponentialAverage(sigma,interval[1]);
        for(int i=0;i<tool.size();i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
//            switch(i){
//                case 0:
                    _cdm.setSubPacketData(dt.getPacketTitle(),sigma);
                    //2013.05.27 by LYH >> 소수점 둘째자리로 수정. <<
                    _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
//                    break;
//                case 1:
//                    _cdm.setSubPacketData(dt.getPacketTitle(),signal);
//                    //2013.05.27 by LYH >> 소수점 둘째자리로 수정. <<
//                    _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
//                    break;
//            }
        }
        formulated = true;
    }
    public void reFormulateData() {
        FormulateData();
        formulated = true;
    }
    public void drawGraph(Canvas gl){
        if(!formulated)FormulateData();                       //저장되어 있지 않다면 계산을 새로 한다        

        double[] drawData=null;
        for(int i=0;i<tool.size();i++){
            DrawTool t=(DrawTool)tool.elementAt(i);
            try{
                drawData=_cdm.getSubPacketData(t.getPacketTitle());
            }catch(ArrayIndexOutOfBoundsException e){
                return;
            }
            if(i==0) _cvm.useJipyoSign=true;
            else _cvm.useJipyoSign=false;
            t.plot(gl,drawData);
        }
        drawBaseLine(gl);
    }
    //============================
    // 표준편차를 구하는 메쏘드 (볼린저밴드 그래프에서 이동됨)
    //============================
    public double[] getStandardDeviation(double[] data, double[] average, int interval,int col) {
        if( (data == null) || (data.length < interval)) {
            return null;
        }
        double ldVal1;
        double[] stDevia = new double[data.length];
        for(int i = interval -1 ; i<stDevia.length ; i++) {
            ldVal1 = 0;
            for(int j= 0 ; j<interval ; j++) {
                ldVal1 = ldVal1 + Math.pow( (data[i-j]-average[i]), 2);
            }
            stDevia[i] = Math.sqrt(ldVal1/interval);
        }
        return stDevia;
    }
    public void drawGraph_withSellPoint(Canvas g){
    }

    public String getName(){
        return "Sigma";
    }

}