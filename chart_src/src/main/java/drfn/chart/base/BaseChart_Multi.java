package drfn.chart.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.RelativeLayout;

import java.util.Vector;

import drfn.chart.NeoChart2;
import drfn.chart.anal.AnalTool;
import drfn.chart.block.Block;
import drfn.chart.comp.ChartItemView;
import drfn.chart.draw.DrawTool;
import drfn.chart.graph.AbstractGraph;
import drfn.chart.util.COMUtil;

public class BaseChart_Multi extends NeoChart2 {
	boolean reverse;//데이터 거꾸로 보내기
	//public RelativeLayout layout;
	int m_nRotateIndex = -1;
	ChartItemView chartItem;
	private SharedPreferences m_prefConfig = null;

	public BaseChart_Multi(Context context , RelativeLayout layout) {
		super(context, layout);
		this.layout = layout;
		//m_prefConfig = context.getSharedPreferences("compareSetting", Context.MODE_WORLD_READABLE);
		m_prefConfig = context.getSharedPreferences("compareSetting", Context.MODE_PRIVATE);
	}
	Base11 parent;
	public void setParent(Base11 parent) {
		this.parent = parent;
	}
	public void init(String[][] datainfo,int startpos){
		setDataInfo(datainfo,startpos);
		super.init();
	}
	public void init(){
		setDataInfo(getDataInfo(),14);
		//COMUtil._chartMain.showStatus("차트 초기화 시작중입니다.");
		super.init();
	}
	public void initDataInfo(String data) {
		if(data.equals("requestAddData")) {

		} else {
//			String[] date = _cdm.getStringData("자료일자");
//			if(date==null) {
//				COMUtil._mainFrame.symbol = getCode(0); 
//				COMUtil.symbol =  getCode(0); 
//				COMUtil.codeName = getName(0);
//				COMUtil.market = getMarket(0);
//			}
			setDataInfo(getDataInfo(), 14);
		}
	}
	public String[][] getDataInfo() {
		String[][] data_info_DWM = null;
		String sCode = COMUtil._mainFrame.symbol;
//		double[] data = _cdm.getSubPacketData(sCode);
//		if(data==null) {
//			int blockCnt = blocks.size();
//			if(blockCnt == 1) {
//				Block block = (Block)blocks.get(0);
//				block.addCompGraph(sCode);
//				
//			}
//		}
		String[][] data_info = {
				{"자료일자","8","YYYYMMDD","유"},
				{COMUtil._mainFrame.symbol,"10","× 1","무"}
		};
		data_info_DWM = data_info;
		if(COMUtil.apCode.equals(COMUtil.TR_COMPARE_STOCK)) {
			data_info_DWM = data_info;
		} else if(COMUtil.apCode.equals(COMUtil.TR_COMPARE_UPJONG) ||
				COMUtil.apCode.equals(COMUtil.TR_COMPARE_FUTURE)) {
			String[][] data_info_upjong = {
					{"자료일자","8","YYYYMMDD","유"},
    		        {COMUtil._mainFrame.symbol,"10","× 0.01","무"}
			};
			data_info_DWM = data_info_upjong;
		} else {
			data_info_DWM = data_info;
		}

//		for(int i=0; i<arrCodes.size(); i++) {
//			String strCode = (String)arrCodes.get(i);
//			if(strCode.equals(sCode)) {
//				parent.chartItem.setData((String)arrNames.get(i), i);
//			}
//		}
		return data_info_DWM;
	}
	public void setReverse(boolean b){
		reverse = b;
	}
	private void setDataInfo(String[][] data_info,int startpos){
		String[] data = _cdm.getStringData("자료일자");
		if(data==null) {
			_cdm.setPacketData(data_info);
		} else {
			_cdm.setPacketData2(data_info);
		}

	}
	public void resetDataInfo(String[][] data_info,int startpos){
		_cdm.destroy();
		 setDataInfo(data_info,startpos);
	}

	@Override
	protected void onMeasure(int wMeasureSpec, int hMeasureSpec) {
//    	int measuredHeight = measureHeight(hMeasureSpec);         
//    	int measuredWidth = measureWidth(hMeasureSpec);                   
		// setMesasuredDimension을 반드시 호출해야만 한다.
		// 그렇지 않으면 컨트롤이 배치될 때
		// 런타임 예외가 발생할 것이다.
		setMeasuredDimension((int)chart_bounds.width(), (int)chart_bounds.height());
	}
	//    private int measureHeight(int measureSpec) {
////    	int specMode = MeasureSpec.getMode(measureSpec);         
//    	int specSize = MeasureSpec.getSize(measureSpec);                   
//    	// 뷰의 높이를 계산한다.         
//    	return specSize;     
//    }           
//    private int measureWidth(int measureSpec) {         
////    	int specMode = MeasureSpec.getMode(measureSpec);         
//    	int specSize = MeasureSpec.getSize(measureSpec);                   
//    	// 뷰의 폭을 계산한다.                   
//    	return specSize;     
//    }
	public void onSizeChanged(int w, int h, int oldw, int oldh) {
//		System.out.println("baseChart_onSizeChanged:"+w+" "+h+" "+oldw+" "+oldh);
		//super.onSizeChanged(w, h, oldw, oldh);
		super.onSizeChanged((int)chart_bounds.width(), (int)chart_bounds.height(), oldw, oldh);

	}

	public void setBasicUI(){//일반적인 봉과 거래량 UI (한화증권 차트 UI)
		int w = this.getWidth();
		int h = this.getHeight();

		Block cb1 = makeBlock(0,0);
		cb1.setProperties("지표 Data",1,_cvm.getScaleLineType());
		cb1.setBlockType(Block.BASIC_BLOCK);
		cb1.setTitle("Multi");
		cb1.setMarginT((int)COMUtil.getPixel(40));
		cb1.setMarginB((int)COMUtil.getPixel(10));
		cb1.add("Multi");
		cb1.setBounds(0,0,w,h,true);
		addBlock(cb1);


	}

	public void setUI(int blockCount, String[] graph_title, String[] properties){
		int w = getWidth();
		int h = getHeight();

		Block[] cb = new Block[blockCount];
		int gab=h/blockCount;
		for(int i=0;i<blockCount;i++){
			cb[i] = makeBlock(0,i,graph_title[i]);
			cb[i].setProperties("지표 Data",1,_cvm.getScaleLineType());
			addBlock(cb[i]);
			cb[i].setBounds(0,gab*i,w,gab,true);
		}


	}
	boolean start=false;
	byte[] buf;

	public void repaintRT(String mstVal, byte[] data){
		super.repaintRT(mstVal, data);
	}

	public boolean addCode(String strCode, String strName, String strMarket) {
		if(strCode==null) return false;
		if(strName==null) strName = COMUtil.codeName;
		if(strMarket==null) strMarket = COMUtil.market;
		for(int i=0; i<arrCodes.size(); i++) {
			String strExistCode = (String)arrCodes.get(i);
			if(strCode.equals(strExistCode)) {
				return false;
			}
		}

		if(arrMarkets==null)
			arrMarkets = new Vector<String>();

		if(arrCodes.size() < 5) {
			arrCodes.add(strCode);
			arrNames.add(strName);
			arrMarkets.add(strMarket);
		} else {
			Block block;
			for(int i=0; i<blocks.size(); i++)
	    	{
	    		block = blocks.get(i);
    			block.delCompGraph((String)arrCodes.get(4));
	    	}
			//Block block = (Block)blocks.get(0);
			
			arrCodes.set(4, strCode);
			arrNames.set(4, strName);
			arrMarkets.set(4, strMarket);
		}

		double[] data = _cdm.getSubPacketData(strCode);
		if(data==null) {
//			int blockCnt = blocks.size();
//			if(blockCnt == 1) {
//				Block block = (Block)blocks.get(0);
//				block.addCompGraph(strCode, strName);
//			}
		}
		 //resetTitleBoundsAll();
		//changeType(2);
		return true;
	}

	public void removeAllCodes() {
		//parent.chartItem.setData("" ,-1);
		_cdm.removeAllPacket();
		for(int i=0; i<arrCodes.size(); i++) {
			//Block block = (Block)blocks.get(0);
			Block block;
			for(int j=0; j<blocks.size(); j++)
	    	{
	    		block = blocks.get(j);
    			block.delCompGraph((String)arrCodes.get(i));
	    	}
			
		}
    	if(arrCodes!=null)
    		arrCodes.removeAllElements();
    	if(arrNames!=null)
    		arrNames.removeAllElements();
    	if(arrMarkets!=null) {
    		arrMarkets.removeAllElements();
    		arrMarkets = null;
    	}
	}

	public int getCodeCount() {
		return arrCodes.size();
	}

	public String getCode(int nIndex) {
		if(nIndex >= arrCodes.size()) {
			return null;
		}

		return (String)arrCodes.get(nIndex);
	}
	public String getName(int nIndex) {
		if(nIndex >= arrNames.size()) {
			return null;
		}

		return (String)arrNames.get(nIndex);
	}
	public String getMarket(int nIndex) {
		if(arrMarkets==null) return null;
		if(nIndex >= arrMarkets.size()) {
			return null;
		}

		return (String)arrMarkets.get(nIndex);
	}
	public void setBounds2(int left, int top, int right, int bottom) {
		super.setBounds2(left, top, right, bottom);

	}

	public void setSkinType(int nSkinType)
	{
		_cvm.setSkinType(nSkinType);
		for(int i=0; i<blocks.size(); i++)
		{
			Block block = blocks.get(i);
			block.setSkinType(nSkinType);
		}
		for(int i=0; i<rotate_blocks.size(); i++)
		{
			Block block = rotate_blocks.get(i);
			block.setSkinType(nSkinType);
		}
		//분석툴 스킨타입에 따른 색상 변경 (2012.08.04 by lyk)
		if(analTools!=null) {
			AnalTool at;
			int analToolCnt = analTools.size();
			for(int i=0; i<analToolCnt; i++) {
				at = (AnalTool)analTools.get(i);
				at.setSkinColor();
			}
		}
	}

	public void showChartItem(boolean bShow)
	{
	}

	public void clearCompareData() {
		//parent.chartItem.setData("" ,-1);
		_cdm.removeAllPacket();
		for(int i=0; i<arrCodes.size(); i++) {
			Block block = (Block)blocks.get(0);
			block.clearCompareData((String)arrCodes.get(i));
		}
		repaintAll();
	}
	
    public void setVisibleCompareDataIndex(boolean visible, int index) {
    	try {
    		COMUtil.compareChecks[index]= visible; 
    	} catch (Exception e) {
    		
    	}
    	repaintAll();
    }	
    
    public void applySetting(boolean bReset)
    {
    	String strType = getEnvString("compare_type", "0");
		int nType = Integer.parseInt(strType);
		// 종목명 Setting
		//int nSize= CompArr.size();
		String strColor = getEnvString("compare_color", "");
	    int nCompareColor[][] = {
//			{247, 94, 94}, //red
//			{21, 126, 232}, //blue
//			{0, 166, 81}, //green
//			{113, 110, 194}, //purple
//			{239, 174, 61} //yellow
			{0, 149, 160}, //청록색
			{81, 193, 241}, //하늘색
			{66, 121, 214}, //파란색
			{163, 114, 231}, //보라색
			{63, 197, 152} //녹색
	    };  
		if(strColor.length()>0)
		{
			String strColors[] = strColor.split("=");
			if(strColors.length>=15)
			{
				for(int i = 0 ; i < 5 ; i++)	{
					nCompareColor[i][0] = Integer.parseInt(strColors[i*3+0]);
					nCompareColor[i][1] = Integer.parseInt(strColors[i*3+1]);
					nCompareColor[i][2] = Integer.parseInt(strColors[i*3+2]);
				}
			}
		}
		
		String strThick = getEnvString("compare_thick", "");
	    int nCompareThick[] = { 2, 2, 2, 2, 2	};
		if(strThick.length()>0)
		{
			String strThicks[] = strThick.split("=");
			if(strThicks.length>=5)
			{
				for(int i = 0 ; i < 5 ; i++)	{
					nCompareThick[i] = Integer.parseInt(strThicks[i]);
				}
			}
		}

    	if(arrCodes.size() > 0 && (_cvm.nCompareType != nType || bReset))
    	{
	    	_cvm.nCompareType = nType;
	    	removeAllBlocks();
			float w = chart_bounds.width();
			float h = chart_bounds.height() - _cvm.XSCALE_H;
	
	        int blockCount = arrCodes.size();
	        Block[] cb = new Block[blockCount];
			float gab=h/blockCount;
	        for(int i=0;i<blockCount;i++){
	        	if((_cvm.nCompareType == 0 && i==0) || _cvm.nCompareType != 0)
	        	{
		            cb[i] = makeBlock(0,i);
		            cb[i].setProperties("지표 Data",1,_cvm.getScaleLineType());
		            if(i==0)
		            	cb[i].setBlockType(Block.BASIC_BLOCK);
		            cb[i].setTitle("Multi");
		            cb[i].setMarginT((int)COMUtil.getPixel(40));
		            cb[i].setMarginB((int)COMUtil.getPixel(10));
		            cb[i].add("Multi");
		    		addBlock(cb[i]);
		    		if(_cvm.nCompareType == 0)
		    			cb[i].setBounds(0,0,w,h,true);
		    		else
		    			cb[i].setBounds(0,gab*i,w,gab*i+gab,true);
	        	}
	        	if(_cvm.nCompareType == 0)
	        	{
	        		cb[0].makeGraphData();
	        		cb[0].addCompGraph(arrCodes.get(i), arrNames.get(i));
	        	}
	        	else
	        	{
	        		cb[i].addCompGraph(arrCodes.get(i), arrNames.get(i));
	        		cb[i].makeGraphData();
	        	}
	        }
	        resetTitleBoundsAll();
    	}
    	
    	Block block;
    	int nIndex = 0;
    	for(int i=0; i<blocks.size(); i++)
    	{
    		block = blocks.get(i);
    		for(int j=0; j<block.getGraphs().size(); j++)
    		{
    			AbstractGraph graph = (AbstractGraph)block.getGraphs().get(j);
//    	    	int nCnt = graph.getDrawTool().size();
    	        for(int k=0; k<graph.getDrawTool().size(); k++) {
    	            DrawTool dt = (DrawTool)graph.getDrawTool().get(k);
    	            dt.setUpColor(nCompareColor[nIndex]);
    	            dt.setLineT(nCompareThick[nIndex]);
    	            nIndex++;
    	        }
    		}
    	}
    	this.repaintAll();
    }
    
	public void setEnvString(String strKey, String strValue)
	{
		if(m_prefConfig == null) return;
		
		SharedPreferences.Editor editConfig = m_prefConfig.edit();
		editConfig.putString(strKey, strValue);
		editConfig.commit();
	}
	
	public String getEnvString(String strKey, String strDefault)
	{
		if(m_prefConfig == null) return strDefault;
		if(!m_prefConfig.contains(strKey)) return strDefault;
		
		try
		{
		    return m_prefConfig.getString(strKey, strDefault);
		}
		catch(Exception e)
		{
		    e.printStackTrace();
		    return strDefault;
		}
	}

}




