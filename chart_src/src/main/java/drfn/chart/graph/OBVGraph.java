package drfn.chart.graph;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

public class OBVGraph extends AbstractGraph{
    double[] obv;
    //2012. 8. 8 OBV Signal 색굵기 없음 : I69
    double[] signal;
    int[][] data;
    public OBVGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        String[] datakind = {"종가","기본거래량"};
        _dataKind = datakind;
        definition=" 절대수치는 의미가 없고, 추세를 봅니다.또한 한 방향으로 추세를 형성하면 상당기간 지속되는 경향이 있으며 한 방향으로 추세를 형성하고 있던 OBV선이 다른 방향으로 추세가 전환되면 매매시점으로 볼 수 있습니다.";
        m_strDefinitionHtml = "OBV.html";	//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)
    }
    //====================================
    // 이격도
    // 당일종가>전일종가: OBV = 전일OBV + 당일 거래량
    // 당일종가<전일종가: OBV = 전일OBV - 당일 거래량
    // 당일종가=전일종가: OBV = 전일OBV 
    //====================================
    public void FormulateData(){
        double[] closeData = _cdm.getSubPacketData("종가");
        double[] volData = _cdm.getSubPacketData("기본거래량");
        if(closeData==null) return;
        int dLen = closeData.length;
        obv = new double[dLen];
        double dobv=0.;
        //obv[0] = data[0][0];
        obv[0]=0;
        for(int i=1;i<dLen;i++){
            double comp = closeData[i]-closeData[i-1];
            if(comp==0){
                obv[i] = obv[i-1];
            } else if(comp>0){
                obv[i] = obv[i-1] + volData[i];
            }else{
                obv[i] = obv[i-1] - volData[i];
//                //obv[i] = (comp>0)?obv[i-1]+(data[i][1]/100):obv[i-1]-(data[i][1]/100);
//                dobv=(comp>0)?dobv+(double)volData[i]:dobv-(double)volData[i]; //2016. 08. 05 by hyh - 지표 계산식 HTS와 동기화
//                //dobv=(comp>0)?dobv+(double)volData[i]/1000.:dobv-(double)volData[i]/1000.;	//Daewoo
//                obv[i-1]=dobv;
            }
        }
        //2012. 8. 8 OBV Signal 색굵기 없음 : I69
        //단순이평
        signal = makeAverageD(obv,interval[0]); //2016. 08. 05 by hyh - 지표 계산식 HTS와 동기화

        //지수이평
        //signal = exponentialAverage(obv,interval[0]);

        for(int i=0;i<tool.size();i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            if(i==0) {
                _cdm.setSubPacketData(dt.getPacketTitle(),obv);
                _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01"); //2016. 08. 05 by hyh - 지표 계산식 HTS와 동기화
            }
            else
            {
                _cdm.setSubPacketData(dt.getPacketTitle(),signal);
                _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
            }
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
        for(int i=0;i<tool.size();i++){
            DrawTool t=(DrawTool)tool.elementAt(i);
            try{
                drawData=_cdm.getSubPacketData(t.getPacketTitle());
            }catch(ArrayIndexOutOfBoundsException e){
                return;
            }
            if(i==0) _cvm.useJipyoSign=true;
            else _cvm.useJipyoSign=false;
            t.plot(g,drawData);
        }
//        if(base!=null){
//            DrawTool t=(DrawTool)tool.elementAt(0);
//            for(int i=0;i<base.length;i++){
//                t.draw(g,base[i]);
//            }
//        }
        //2013. 9. 5 지표마다 기준선 설정 추가>>
        drawBaseLine(g);
        //2013. 9. 5 지표마다 기준선 설정 추가>>

        //2014. 9. 15 매매 신호 보기 기능 추가>>
        DrawTool t=(DrawTool)tool.elementAt(0);
        if(isSellingSignalShow)
            t.drawSignal(g, obv, signal);
        //2014. 9. 15 매매 신호 보기 기능 추가<<
    }
    public void drawGraph_withSellPoint(Canvas g){
    }

    public String getName(){
        return "OBV";
    }
}