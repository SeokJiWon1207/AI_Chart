package drfn.chart.base;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import drfn.chart.NeoChart2;
import drfn.chart.block.Block;
import drfn.chart.graph.AbstractGraph;
import drfn.chart.util.COMUtil;

public class GraphSetUI extends View{

	RelativeLayout layout = null;
	private Context context = null;

	public GraphSetUI(Context context, RelativeLayout layout) {
		super(context);
		this.context = context;
		this.layout = layout;

	}
	//2012. 8. 30 캔들, 거래량, 일반상세설정에 따라서  Class 다르게 로드 : I97, I98
	JipyoControlSetUI contUI = null;
	public void setUI(String type) {

		if(type.equals("candle"))
		{
			contUI = new CandleControlSetUI(context, this.layout);
		}
		else if(type.equals("volume"))
		{
			contUI = new VolumeControlSetUI(context, this.layout);
		}
		else if(type.equals("jipyo"))
		{
			contUI = new JipyoControlSetUI(context, this.layout);
		}
		else if(type.equals("period"))
		{
			contUI = new PeriodSettingView(context, this.layout);
		}

		//6.26 다른 곳에서 JipyoControlSetUI 를 중복 addView 하고있어서 주석처리 
//		this.layout.addView(contUI);

		if(type.equals("overlay")) {
			setInitGraph("주가이동평균");
		} else if (type.equals("jipyo")) {
			setInitGraph("거래량");
		}
	}

	//2012. 8. 30  상세설정창 변수명 변경 : I97, I98
	public void setInitGraph(String name) {
		if(contUI!=null) {
//			contUI.init(COMUtil._neoChart.getGraph(name));
			contUI.init(getGraph(name));
		}
	}

	public void setDefault() {
		if(contUI!=null) {
			contUI.reSetOriginal();
			if(!(contUI instanceof PeriodSettingView))
			{
				contUI.reSetJipyo();
				COMUtil._neoChart.makeGraphData();
				COMUtil._neoChart.repaintAll();
			}

		}
	}

	//2013. 2. 8 체크안된 상세설정 오픈 : I114
	public AbstractGraph getGraph(String name)
	{
		AbstractGraph tmpGraph = COMUtil._neoChart.getGraph(name);

		if(tmpGraph == null)
		{
			NeoChart2 neoChart = COMUtil._neoChart;
			tmpGraph = neoChart.getUnChkGraph(name);

			if(tmpGraph == null)
			{
				Block block = new Block(neoChart,  neoChart._cvm, neoChart._cdm, 0, 0, name);
				tmpGraph = block.createGraph(name, neoChart._cvm, neoChart._cdm, Block.STAND_BLOCK);
				neoChart.addUnChkGraph(tmpGraph);
			}
		}

		return tmpGraph;
	}

	public void destroy() {
		if(contUI != null)
		{

			if(contUI.periodPopup != null)
			{
				contUI.periodPopup.dismiss();
				contUI.periodPopup = null;
			}
			contUI.destroy();
		}
	}
}
