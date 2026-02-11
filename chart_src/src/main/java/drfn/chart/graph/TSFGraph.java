package drfn.chart.graph;

import android.graphics.Canvas;

import java.util.Vector;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;


public class TSFGraph extends AbstractGraph{
	double[] graphData;
	double[] signal;
    int[][] data;//계산 전 데이터
    double[] ma; //중심선
    double[] ma1;//상한선
    double[] ma2;//하한선
    Vector<DrawTool> tool;
    public TSFGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        tool = getDrawTool();//드로우 툴을 구한다
        definition="TSF";

        m_strDefinitionHtml = "tsf.html";	//2018.05.02 by lyj 보조지표 설명/활용법 추가(상세설정창)
    }
    //========================================
    //DEMA : 
    //========================================
    public void FormulateData(){
        graphData = getTSF(interval[0]);
	    if(graphData==null) return;
	    //2017.08.14 by pjm 지표 수정 >>
//        DrawTool dt = (DrawTool)tool.elementAt(0);
//        _cdm.setSubPacketData(dt.getPacketTitle(),graphData);
//        _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
	    signal = exponentialAverage(graphData,interval[1]);
	    for(int i=0;i<tool.size();i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            if (i == 0) {
                _cdm.setSubPacketData(dt.getPacketTitle(), graphData);
            }
            else {
                _cdm.setSubPacketData(dt.getPacketTitle(), signal);
            }
            _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
            //2017.08.14 by pjm 지표 수정 <<
        }
	    formulated = true;
    }
    public void reFormulateData() {
        FormulateData();
        formulated = true;
    }

    private double[] getTSF(int interval) //signal 포함
    {
        double[] closeData = _cdm.getSubPacketData("종가");
        double[] volData = _cdm.getSubPacketData("기본거래량");
        if(closeData == null || volData == null)	return null;
        int dLen = closeData.length;
        double[] tsf = new double[dLen];
        double[] lrl = new double[dLen];
        double[] lrs = new double[dLen];
        lrl = getLRL(interval);
        lrs = getLRS(interval);
        for(int i = interval-1;i<dLen;i++){
            tsf[i] =  lrl[i]+lrs[i];
        }
        return tsf;
    }


    public void drawGraph(Canvas g){
        if(!formulated)FormulateData();                       //저장되어 있지 않다면 계산을 새로 한다

        double[] drawData=null;
        //2017.08.14 by pjm 지표 수정 >>
//        DrawTool t=(DrawTool)tool.elementAt(0);
//        drawData=_cdm.getSubPacketData(t.getPacketTitle());
//        _cvm.useJipyoSign=true;
//
//        t.plot(g,drawData);
//        if(base!=null){
//            for(int i=0;i<base.length;i++)t.draw(g,base[i]);
//        }
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
        //2017.08.14 by pjm 지표 수정 <<
    }
    public void drawGraph_withSellPoint(Canvas g){
    }
    public String getName(){
        return "TSF";
    }

    private double[] getLRL(int interval)
    {
        double[] closeData = _cdm.getSubPacketData("종가");
        if(closeData == null)	return null;
        int dLen = closeData.length;
        double[] dValue1 = new double[dLen]; //봉위치
        double[] dValue2 = new double[dLen]; //봉위치 제곱
        double[] dValue3 = new double[dLen]; //봉위치 Period 기간 평균
        double[] dValue4 = new double[dLen]; //봉위치 제곱 period 기간 평균
        double[] dValue5 = new double[dLen]; //봉위치 period 기간 평균 제곱
        double[] dValue6 = new double[dLen]; //종가*봉위치
        double[] dValue7 = new double[dLen]; //value6의 Period 기간 이동평균
        double[] dValue8 = new double[dLen]; //종가 period 기간 이동평균
        double[] retData = new double[dLen];

        for(int i = 0; i < dLen; i++)
        {
            dValue1[i] = i;
            dValue2[i] = dValue1[i] * dValue1[i];
        }

        dValue3 = this.makeAverage(dValue1, interval);
        dValue4 = this.makeAverage(dValue2, interval);

        for(int i = 0; i < dLen; i++)
        {
            dValue5[i] = dValue3[i] * dValue3[i];
            dValue6[i] = closeData[i] * i;
        }

        dValue7 = this.makeAverage(dValue6, interval);
        dValue8 = this.makeAverage(closeData, interval);

        for(int i = interval-1; i < dLen; i++)
        {
            retData[i] = (dValue7[i]-dValue3[i]*dValue8[i])/(dValue4[i]-dValue5[i])*(dValue1[i]-dValue3[i])+dValue8[i];
        }

        return retData;
    }

    private double[] getLRS(int interval)
    {
        double[] closeData = _cdm.getSubPacketData("종가");
        if(closeData == null)	return null;
        int dLen = closeData.length;
        double[] dValue1 = new double[dLen];
        double[] dValue2 = new double[dLen];
        double[] dValue3 = new double[dLen];
        double[] dValue4 = new double[dLen];
        double[] dValue5 = new double[dLen];
        double[] dValue6 = new double[dLen];
        double[] retData = new double[dLen];

        for(int i = 0; i < dLen; i++)
        {
            dValue1[i] = i+1;
        }

        for(int i = interval-1; i < dLen; i++)
        {
            for(int j = i; j > i-interval; j--)
            {
                dValue2[i] += dValue1[j];
                dValue3[i] += closeData[j];
                dValue4[i] += dValue1[j]*closeData[j];
                dValue5[i] += dValue1[j]*dValue1[j];
            }
            dValue6[i] = (interval* dValue5[i] - (dValue2[i] * dValue2[i]));
            retData[i] = (interval * dValue4[i] - dValue2[i] * dValue3[i]) / dValue6[i];
        }

        return retData;
    }
}
