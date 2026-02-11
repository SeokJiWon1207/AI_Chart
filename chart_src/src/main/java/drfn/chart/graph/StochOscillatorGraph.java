package drfn.chart.graph;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;
import drfn.chart.util.MinMax;

public class StochOscillatorGraph extends AbstractGraph{
    //int[] interval = {12,5,5};
    int[][] data;
    int[] slow_k;
    int[] slow_d;
    public StochOscillatorGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        String[] datakind = {"고가","저가","종가"};
        _dataKind = datakind;
        definition="Fast Stochastics는 지표의 등락이 상당히 심한 경우가 있기 때문에 Fast Stochastics의 값을 다시한번 이동평균하여 평활시킨 것이 Slow Stochastics입니다.  사용자 입력수치는 Fast %K를 구하기 위한 기간값과 Slow %K (=Fast %D)를 구하기 위한 이동평균기간, Slow %D값을 구하기 위한 이동평균기간입니다.";
        m_strDefinitionHtml = "stochastics_oscillator.html";	//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)
    }

    //------------------------------------
    // Stochastics  
    //    fast %K = {(오늘의 종가 - 최근 n일중 장중 최저가)/(최근 n일중 장중 최고가 - 최근 n일중 장중 최저가)}*100
    //    fast %D = {(오늘의 종가 - 최근 n일중 장중 최저가)의 3일 이동평균 *100}
    //              /{(최근 n일중 장중최고가 - 최근 n일중 장중 최저가의 3일 이동평균}
    //    
    //------------------------------------

    public void FormulateData() {
        double[] highData = _cdm.getSubPacketData("고가");
        double[] lowData = _cdm.getSubPacketData("저가");
        double[] closeData = _cdm.getSubPacketData("종가");
        if(closeData==null) return;
        int dLen = closeData.length;
        double[] stoch_k = new double[dLen];

        for(int i=0;i<dLen;i++){
//            if(i<1)
//                stoch_k[i] = 0;
//            else{
                double l=MinMax.getRangeMin(lowData,i+1,interval[0]);
                double h=MinMax.getRangeMax(highData,i+1,interval[0]);

                if(h != l){
                    stoch_k[i]= ((closeData[i]-l)*100)/(h-l);
                }else{
                    stoch_k[i] = 0;
                }
//            }

        }
        double[] stoch_d = exponentialAverage(stoch_k,interval[1], interval[0]-1);
        double[] slow_d = exponentialAverage(stoch_d,interval[2],interval[0]+interval[1]-2);

        for(int i=0; i<dLen; i++){
            stoch_k[i] = stoch_d[i] - slow_d[i];
        }

        for(int i=0;i<tool.size();i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            if(i==0){
                _cdm.setSubPacketData(dt.getPacketTitle(),stoch_k);
                _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
            }
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
        //2012. 7. 2   기준선 크기
        //gl.glLineWidth(COMUtil.graphLineWidth);
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
        //2012. 7. 2   기준선 크기
        //gl.glLineWidth(COMUtil.graphLineWidth2);
//        for(int i=0;i<base.length;i++){
//            DrawTool t=(DrawTool)tool.elementAt(0);
////            g.setColor(base_col[i]);
//            t.draw(gl,base[i]);
//        }
        //2013. 9. 5 지표마다 기준선 설정 추가>>
        drawBaseLine(gl);
        //2013. 9. 5 지표마다 기준선 설정 추가>>

    }
    public void drawGraph_withSellPoint(Canvas g){
    }

    public String getName(){
        return "Stochastics Oscillator";
    }

}