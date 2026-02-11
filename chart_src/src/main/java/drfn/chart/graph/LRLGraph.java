package drfn.chart.graph;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

public class LRLGraph extends AbstractGraph{
	double[] lrl;
	double[] signal;
	int[][] data;//계산 전 데이터
	//int[] base = null;
	public LRLGraph(ChartViewModel cvm, ChartDataModel cdm){
		super(cvm,cdm);
		definition="LRL는 정해진 기간의 선형회귀 분석선입니다.";
		m_strDefinitionHtml = "LRL.html";	//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)
	}

	public void FormulateData(){

		lrl = getLRL(interval[0]);
		if(lrl==null) return;
//        signal= exponentialAverage(lrs,interval[1],interval[0]);
		for(int i=0;i<tool.size();i++){
			DrawTool dt = (DrawTool)tool.elementAt(i);
			if(i==0){
				_cdm.setSubPacketData(dt.getPacketTitle(),lrl);
				_cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
			}
//            else {
//            	_cdm.setSubPacketData(dt.getPacketTitle(),signal);
//            	_cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
//            }
		}

		formulated = true;
	}
	public void reFormulateData() {
		FormulateData();
		formulated = true;
	}
	/*
     *  a1= 봉위치
        a2 = 봉위치제곱
        a3 = 봉위치 Period 기간 평균
        a4 = 봉위치제곱 Period 기간 평균
        a5 = 봉위치 Period 기간 평균 제곱
        a6 = 종가*봉위치
        a7 = value6의 Period 기간 이동평균
        a8 = 종가 Period 기간 이동평균
        LRL = (a7 - a3 * a8) / (a4 - a5) * (a1 - a3) + a8
        [Reference] : CAILAB, 「중앙투자연구소」 http://www.kospi200futures.co.kr/xe/?mid=systemtrading&document_srl=33965&sort_index=title&order_type=asc.
     */
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

//		for(int i = (interval-1)*2; i < dLen; i++)
		for(int i = interval-1; i < dLen; i++)
		{
			retData[i] = (dValue7[i]-dValue3[i]*dValue8[i])/(dValue4[i]-dValue5[i])*(dValue1[i]-dValue3[i])+dValue8[i];
		}

		return retData;
	}
	public void drawGraph(Canvas g){
		if(!formulated)FormulateData();                       //저장되어 있지 않다면 계산을 새로 한다

		double[] drawData=null;
		for(int i=0;i<tool.size();i++){
			DrawTool t=(DrawTool)tool.elementAt(i);
			drawData=_cdm.getSubPacketData(t.getPacketTitle());
			if(i==0) _cvm.useJipyoSign=true;
			else _cvm.useJipyoSign=false;
			t.plot(g,drawData);
		}
		//2013. 9. 5 지표마다 기준선 설정 추가>>
		drawBaseLine(g);
		//2013. 9. 5 지표마다 기준선 설정 추가>>
	}
	public void drawGraph_withSellPoint(Canvas g){
	}

	public String getName(){
		return "LRL";
	}
}