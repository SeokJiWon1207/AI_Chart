
package drfn.chart.graph;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

public class VarianceGraph extends AbstractGraph{
	int sub_margin;
	//	double[][] data;
	double[] reverseData;	//int[][]
	double[] price;	//int[]
	double[] trade;	//int[]
	double[] end;	//int[]
//	int nInterval=25; //25

	public VarianceGraph(ChartViewModel cvm , ChartDataModel cdm){
		super(cvm,cdm);
		tool = getDrawTool();
		m_strDefinitionHtml="inverseline.html";
	}

	public void FormulateData(){
		double[] closeData = _cdm.getSubPacketData("종가");
		double[] volData = _cdm.getSubPacketData("기본거래량");
		if(closeData==null) return;

		DrawTool dt = tool.get(0);
//		trade = new double[closeData.length];
//		end = new double[closeData.length];

//		trade = makeAverage(closeData,interval[0]);
//		end = makeAverage(volData,interval[0]);
		_cdm.setSubPacketData(dt.getPacketTitle(),closeData);
		
//		String[] sEndData = new String[end.length];
//		for(int i=0; i<end.length; i++) {
//			sEndData[i] = String.valueOf(end[i]);
//		}
		_cdm.setSubPacketData(dt.getPacketTitle()+"_거래량스트링", volData);
		_cdm.setSubPacketData(dt.getPacketTitle()+"_거래량", volData);
		formulated=true;
	}

	public void reFormulateData() {
		FormulateData();
		formulated = true;
	}
	public void drawGraph(Canvas g){

		DrawTool t = tool.get(0);
		double[] drawData=null;
		drawData=_cdm.getSubPacketData(t.getPacketTitle());
		if(drawData!=null)t.plot(g,drawData);
	}
	public void drawGraph_withSellPoint(Canvas g){
	}
	public String getName(){
		return "분산형";
	}
}