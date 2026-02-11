package drfn.chart.signal;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.graph.AbstractGraph;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

/**
 * 가격 이동평균 그래프
 */
public class GoldenDeadCrossMA_WMASignal extends AbstractGraph{
	int[][] data;
	String[] datakind = {"종가"};
	int m_dataCnt;
	public GoldenDeadCrossMA_WMASignal(ChartViewModel cvm, ChartDataModel cdm){
		super(cvm,cdm);
		setDatakind(datakind);
//		m_strDefinitionHtml = "price_movingaverage.html";	//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)
		//2016.07.28 by LYH >> 데이터기준, 이평선타입별(단순,가중,지수,기하) 계산
		m_nDataType = ChartViewModel.AVERAGE_DATA_CLOSE;
		m_nAverageCalcType = ChartViewModel.AVERAGE_GENERAL;
		//2016.07.28 by LYH << 데이터기준, 이평선타입별(단순,가중,지수,기하) 계산
	}
	public void FormulateData(){
		if(!formulated){

			int tCnt = tool.size();
			int dLen = 0;
			DrawTool dt = null;

			double[] closeData = _cdm.getSubPacketData("종가");

			if(closeData != null){
				dLen = closeData.length;

				double[] moveAverageData = makeAverage(closeData, interval[0]);
				double[] moveAverageData2 = makeWeightAverage(closeData, interval[1]);

				dt = tool.get(0);
				_cdm.setSubPacketData(dt.getPacketTitle(),moveAverageData);
				String strSignal = dt.getPacketTitle()+"_Signal";
				_cdm.setSubPacketData(strSignal,moveAverageData2);
			}
			formulated = true;
		}
	}
	public void reFormulateData() {
		FormulateData();
		formulated = true;
	}

	public void drawGraph(Canvas gl){
		drawStrategyGraph(gl,tool);
	}


	public void drawGraph_withSellPoint(Canvas g){
	}

	public String getName(){
		if(m_nStrategyType == 1)
			return "단순가중 크로스 강세약세";
		else
			return "단순가중 크로스 신호";
	}
}