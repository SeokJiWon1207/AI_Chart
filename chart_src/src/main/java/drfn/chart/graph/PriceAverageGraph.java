package drfn.chart.graph;

import android.graphics.Canvas;
import android.util.Log;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartPacketDataModel;
import drfn.chart.model.ChartViewModel;
import drfn.chart.util.COMUtil;
/**
 * 가격 이동평균 그래프
 */
public class PriceAverageGraph extends AbstractGraph{
	int[][] data;
	String[] datakind = {"종가"};
	int m_dataCnt;
	public PriceAverageGraph(ChartViewModel cvm, ChartDataModel cdm){
		super(cvm,cdm);
		setDatakind(datakind);
		definition="이동평균선의 활용방법으로는 1.주가와 이동평균선의 관계를 분석하는 방법,2. 단기 이동평균선과 장기 이동평균선의 관계를 분석하는 방법,3. 그랜빌의 정리를 이용하는 방법등이 있습니다.◇ 종목별로 가장 적당한 이동평균선을 결정하는 방법 : 종목의 최고점과 최저점 사이의 기간을 2로 나눈 후 1을 더합니다.  이 방법으로 일간이동평균선 기간을 구했으면, 주간 이동평균선은 일간/5 , 월간 이동평균선은 일간/21로 구합니다. ";
		m_strDefinitionHtml = "price_movingaverage.html";	//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)
		//2016.07.28 by LYH >> 데이터기준, 이평선타입별(단순,가중,지수,기하) 계산
		m_nDataType = ChartViewModel.AVERAGE_DATA_CLOSE;
		m_nAverageCalcType = ChartViewModel.AVERAGE_GENERAL;
		//2016.07.28 by LYH << 데이터기준, 이평선타입별(단순,가중,지수,기하) 계산
	}
	public void FormulateData(){
		if(!formulated){
//			double[] price = _cdm.getSubPacketData("종가");
//			if(price==null) return;
			double[] price;
			Object moveAverageData = null;
			String strPacketTitle;
			ChartPacketDataModel cpdm =_cdm.getChartPacket("종가");
			for(int i=0;i<tool.size();i++){
				DrawTool dt = (DrawTool)tool.elementAt(i);

				//2020.03.20 주가이동평균 가격, 이평선 타입 수정 - hjw >>
				m_nDataType = dt.getDataType();
				m_nAverageCalcType = dt.getAverageCalcType();

//				m_nDataType = dataTypeAverage;
//				m_nAverageCalcType = calcTypeAverage;
				//2020.03.20 주가이동평균 가격, 이평선 타입 수정 - hjw <<

				price = getData_Type();
				if(price != null)
				{
					m_dataCnt = price.length;
					//2016.07.28 by LYH >> 데이터기준, 이평선타입별(단순,가중,지수,기하) 계산
					moveAverageData = makeAverage_Type(price,interval[i], -1);
					//_cdm.setSubPacketData(dt.getPacketTitle(),makeAverage(price,interval[i]));
					strPacketTitle = dt.getPacketTitle() + i;
					_cdm.setSubPacketData(strPacketTitle,moveAverageData);
					//2016.07.28 by LYH << 데이터기준, 이평선타입별(단순,가중,지수,기하) 계산

                    //2014.03.18 by LYH << 지표 소수점 자리수 가격과 동일하게 처리.
                    if(_cdm.nTradeMulti>0)
                    {
                        _cdm.setSyncPriceFormat(strPacketTitle);
                    }else {
                        if (cpdm.getPacketFormat() == 14)
                            _cdm.setPacketFormat(strPacketTitle, "× 0.01");
                        else if (cpdm.getPacketFormat() == 15)
                            _cdm.setPacketFormat(strPacketTitle, "× 0.001");
                        else if (cpdm.getPacketFormat() == 16)
                            _cdm.setPacketFormat(strPacketTitle, "× 0.0001");
                    }
				}
			}
			formulated = true;
		}
	}
//	public void reFormulateData(){
//		formulated = false;
//		double price[] = _cdm.getSubPacketData("종가");
//		if(price==null) return;
//		double moveAverageData[];
//		if(price!=null) {
//			m_dataCnt = price.length;
//			for(int i=0;i<tool.size();i++){
//				DrawTool dt = (DrawTool)tool.elementAt(i);
//				moveAverageData = _cdm.getSubPacketData(dt.getPacketTitle());
//				if(moveAverageData==null)
//				{
//					FormulateData();	//2015. 2. 16  가상매매연습기 데이터없을때 이평그리기 처리
//					return;
//				}
//				if(COMUtil.isRealTicState) {
//					int avgLen = moveAverageData.length-1;
//					//2013. 10. 23 데이터 없이 흰화면일 때 실시간 받으면 봉 그리기 처리>> : 데이터 추가시 이평 처리
//					if(moveAverageData.length<m_dataCnt)
//					{
//						FormulateData();
//						return;
////	            		double[] tmp = new double[m_dataCnt];
////	            		System.arraycopy(moveAverageData, 0, tmp, 0, avgLen);
////	            		moveAverageData = tmp;
//					}
//					else
//					{
//						System.arraycopy(moveAverageData, 1, moveAverageData, 0, avgLen);
//					}
//					//2013. 10. 23 데이터 없이 흰화면일 때 실시간 받으면 봉 그리기 처리<<
//					moveAverageData[m_dataCnt-1] = getEndAverage(price, interval[i]);
//				} else {
//					if(moveAverageData.length<m_dataCnt) {
//						FormulateData();
//						return;
//					}
//					moveAverageData[m_dataCnt-1] = getEndAverage(price, interval[i]);
//				}
//				_cdm.setSubPacketData(dt.getPacketTitle(),moveAverageData);
//			}
//		}
//		formulated = true;
//	}
public void reFormulateData(){
	formulated = false;
	//2016.07.28 by LYH >> 데이터기준, 이평선타입별(단순,가중,지수,기하) 계산
	//double price[] = _cdm.getSubPacketData("종가");
//    	double[] price = getData_Type();
//    	//2016.07.28 by LYH << 데이터기준, 이평선타입별(단순,가중,지수,기하) 계산
//    	if(price==null) return;
	double[] price;
	double moveAverageData[];
	String strPacketTitle;
	for(int i=0;i<tool.size();i++){
		DrawTool dt = (DrawTool)tool.elementAt(i);
		//2020.03.20 주가이동평균 가격, 이평선 타입 수정 - hjw >>
		m_nDataType = dt.getDataType();
		m_nAverageCalcType = dt.getAverageCalcType();
//		m_nDataType = dataTypeAverage;
//		m_nAverageCalcType = calcTypeAverage;
		//2020.03.20 주가이동평균 가격, 이평선 타입 수정 - hjw <<

		price = getData_Type();
		if(price!=null) {
			m_dataCnt = price.length;
			strPacketTitle = dt.getPacketTitle() + i;
			moveAverageData = _cdm.getSubPacketData(strPacketTitle);
			if(moveAverageData==null || m_nAverageCalcType == ChartViewModel.AVERAGE_EXPONENTIAL || m_nAverageCalcType == ChartViewModel.AVERAGE_GEOMETIC)
			{
				FormulateData();	//2015. 2. 16  가상매매연습기 데이터없을때 이평그리기 처리
				return;
			}
			if(COMUtil.isRealTicState) {
				int avgLen = moveAverageData.length-1;
				//2013. 10. 23 데이터 없이 흰화면일 때 실시간 받으면 봉 그리기 처리>> : 데이터 추가시 이평 처리
				if(moveAverageData.length<m_dataCnt)
				{
					FormulateData();
					return;
//	            		double[] tmp = new double[m_dataCnt];
//	            		System.arraycopy(moveAverageData, 0, tmp, 0, avgLen);
//	            		moveAverageData = tmp;
				}
				else
				{
					System.arraycopy(moveAverageData, 1, moveAverageData, 0, avgLen);
				}
				//2013. 10. 23 데이터 없이 흰화면일 때 실시간 받으면 봉 그리기 처리<<
				//2016.07.28 by LYH >> 데이터기준, 이평선타입별(단순,가중,지수,기하) 계산
				//moveAverageData[m_dataCnt-1] = getEndAverage(price, interval[i]);
				if (m_nAverageCalcType == ChartViewModel.AVERAGE_WEIGHT){
					moveAverageData[m_dataCnt-1] = getEndWeightAverage(price, interval[i]);
				}else
				{
					moveAverageData[m_dataCnt-1] = getEndAverage(price, interval[i]);
				}
				//2016.07.28 by LYH << 데이터기준, 이평선타입별(단순,가중,지수,기하) 계산
			} else {
				//2016.07.28 by LYH >> 데이터기준, 이평선타입별(단순,가중,지수,기하) 계산
				//moveAverageData[m_dataCnt-1] = getEndAverage(price, interval[i]);
				if (m_nAverageCalcType == ChartViewModel.AVERAGE_GENERAL){
                    moveAverageData[m_dataCnt-1] = getEndAverage(price, interval[i]);
				}
				else if (m_nAverageCalcType == ChartViewModel.AVERAGE_WEIGHT){
                    moveAverageData[m_dataCnt-1] = getEndWeightAverage(price, interval[i]);
				}
				//2016.07.28 by LYH << 데이터기준, 이평선타입별(단순,가중,지수,기하) 계산
			}
			_cdm.setSubPacketData(strPacketTitle,moveAverageData);
		}
	}
	formulated = true;
}
	private double getEndAverage(double[] data, int nInterval) {
		if(data==null || nInterval<=0) return 0;

		int nEnd = data.length-1;
		int nStart = nEnd - nInterval;
		if(nStart<0) {
			return 0;
		}
		double subTotal = 0;
		for(int j=nEnd; j>nStart; j--) {
			subTotal += data[j];
		}

		return (subTotal/nInterval);
	}

	//2016.07.28 by LYH >> 데이터기준, 이평선타입별(단순,가중,지수,기하) 계산
	private double getEndWeightAverage(double[] data, int nInterval) {
		if(data==null || nInterval<=0) return 0;

		int nEnd = data.length-1;
		int nStart = nEnd - nInterval;
		if(nStart<0) {
			return 0;
		}
		double subTotal = 0;
		int nAdd = nInterval;
		int nAddSum = 0;
		for(int j=nEnd; j>nStart; j--) {
			subTotal += data[j]*nAdd;
			nAddSum += nAdd;
			nAdd--;
		}
//    	Log.i("drfn", "getEndWeightAverage (subTotal/nAddSum) : " + String.valueOf((subTotal/nAddSum)));
		return (subTotal/nAddSum);
	}
	//2016.07.28 by LYH << 데이터기준, 이평선타입별(단순,가중,지수,기하) 계산

	public void drawGraph(Canvas gl){
		Log.d("kfits","PriceAverageGraph "+_cvm.isMovingAverageLine);
		if(!_cvm.isMovingAverageLine) {
			return;
		}
		if(!formulated)FormulateData();
		double[] drawData=null;

		//gl.glLineWidth(2.0f);
		String strPacketTitle;
		for(int i=0;i<tool.size();i++){
			DrawTool t=(DrawTool)tool.elementAt(i);
			try{
				//if(t.isVisible()){
				//if(_cvm.average_state[i]){
//				drawData=_cdm.getSubPacketData(t.getPacketTitle());
//				_cvm.useJipyoSign=false;//지표값을 yscale에 보여줄지 여부.
//				t.plot(gl,drawData);
				strPacketTitle = t.getPacketTitle() + i;
				drawData=_cdm.getSubPacketData(strPacketTitle);
				_cvm.useJipyoSign=false;//지표값을 yscale에 보여줄지 여부.
				t.plot(gl,drawData);
				//}
			}catch(ArrayIndexOutOfBoundsException e){
				return;
			}catch(NullPointerException e){
				return;
			}

		}
	}


	public void drawGraph_withSellPoint(Canvas g){
	}

	public String getName(){
		return "주가이동평균";
	}
}