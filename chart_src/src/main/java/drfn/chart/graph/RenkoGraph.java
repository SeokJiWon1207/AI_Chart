package drfn.chart.graph;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartPacketDataModel;
import drfn.chart.model.ChartViewModel;
import drfn.chart.util.MinMax;


public class RenkoGraph extends AbstractGraph{
	String[] datakind = {"종가"};//그래프에 사용될 데이터
	DrawTool dt; //그래프에 사용될 드로우툴
	int sub_margin;
	int[][] data;
	double[] endPrices;
	double[][] thirdData;
	double[][] thirdDataResult;
	int count = 0;
	public RenkoGraph(ChartViewModel cvm, ChartDataModel cdm){
		super(cvm,cdm);
		definition="시간개념이 없는 비시계열 차트의 일종입니다.  또한 거래량도 반영되어 있지 않습니다.  매매시점 파악보다는 주가의 장기추세의 전환시점 확인용으로 사용됩니다.양전환시 매수, 음전환시 매도합니다.양선과 음선이 교차하여 나타나는 횡보장세에서는 매매를 유보합니다";
	}

	//===================================
	// 공식 계산 -- 데이터가 바뀌기 전까지는 한번만 한다
	//===================================
	public void FormulateData(){
//		double[] closeData = _cdm.getSubPacketData("종가");
//		if(closeData==null) return;
//		int dLen = closeData.length;
//		endPrices = new double[dLen];
//		for(int i=0;i<dLen;i++){
//			endPrices[i] = closeData[i];
//		}
//		DrawTool dt = (DrawTool)tool.elementAt(0);
//		_cdm.setSubPacketData(dt.getPacketTitle(),endPrices);
        double[] closeData = _cdm.getSubPacketData("종가");
        if(closeData==null) return;
		makeData(closeData);
		formulated=true;
	}
	private void makeData(double[] data){
		count =0;
		int		i=0, nListCount=0;				// nListCount-종가 리스트 갯수
		double	dDataPrice = 0;				// 종가 데이터
		double	dOpenPrice = 0, dClosePrice = 0;	// RenKoPacket의 Open/Close Price
		double	dBrickOpen = 0, dBrickClose = 0;	// 벽돌의 Open/Close Price
		nListCount = data.length;

		if(nListCount<=1)
			return;

		//thirdData = new double[nListCount][3];

		dDataPrice = data[0];	// 첫 종가
		dOpenPrice = dClosePrice = 0;				// 변수값 세팅
		dBrickOpen = dBrickClose = dDataPrice;
		double[] highData = _cdm.getSubPacketData("고가");
		double[] lowData = _cdm.getSubPacketData("저가");
		double max_data=MinMax.getIntMaxT(highData);
		double min_data=MinMax.getIntMinT(lowData);
		//double dBrickCondition = ((max_data-min_data)*2.36)/50;
//		double dBrickCondition = (max_data-min_data)/23/2;
        double dBrickCondition = (max_data-min_data)*0.02;

//        int blockSize = 40;
//        double blockRate = blockSize/(max_data-min_data);
//        double dBrickCondition = (max_data-min_data)*blockRate;

//        double dBrickCondition = 40;
//        double bk = 40/(max_data-min_data);
//        System.out.println("bk:"+bk);

//        double firstData = 0;	
//	    int indexV = 0;
//	    double compareData = 0;
		//double[] minMax = new double[3];
		double[] TotMinMax = new double[2];

//		for( i = 0; i < nListCount; i++)
//		{
//			while( dDataPrice >= dBrickOpen + dBrickCondition && dDataPrice >= dBrickClose + dBrickCondition)
//			{
//                System.out.println("count1 "+i+" "+count);
//				dBrickClose = dBrickClose < dBrickOpen ? dBrickOpen : dBrickClose;
//
//	            /* Up Type Brick 생성 */
//				dOpenPrice = dBrickClose;
//				dClosePrice = dBrickClose + dBrickCondition;
//
//				count++;
//
//				dBrickOpen = dOpenPrice; /* ReSetting BrickData */
//				dBrickClose = dClosePrice;
//			}
//			while( dDataPrice <= dBrickClose - dBrickCondition && dDataPrice <= dBrickOpen - dBrickCondition)
//			{
//                System.out.println("count2 "+i+" "+count);
//				dBrickClose = dBrickClose < dBrickOpen ? dBrickClose : dBrickOpen;
//		        /* Down Type Brick 생성 */
//				dOpenPrice = dBrickClose;
//				dClosePrice = dBrickClose - dBrickCondition;
//
//				count++;
//				// ReSetting BrickData
//				dBrickOpen = dOpenPrice;
//				dBrickClose = dClosePrice;
//			}
//
//			if(i<nListCount-1)
//				dDataPrice = data[i+1];	// Next 종가
//			//nSkipCount++;
//		}
////		count++;
//        System.out.println("count : "+count);
        count = 10000;
		thirdData = new double[count][3];
        double fCloseData[]  = new double[count];

        int indexV = 0;
        String[] dates = _cdm.getStringData("자료일자");
        String[] dateData = new String[count];

		count = 0;
		dDataPrice = data[0];	// 첫 종가
		dOpenPrice = dClosePrice = 0;				// 변수값 세팅
		dBrickOpen = dBrickClose = dDataPrice;
		for( i = 0; i < nListCount; i++)
		{
			while( dDataPrice >= dBrickOpen + dBrickCondition && dDataPrice >= dBrickClose + dBrickCondition)
			{
				dBrickClose = dBrickClose < dBrickOpen ? dBrickOpen : dBrickClose;

	            /* Up Type Brick 생성 */
				dOpenPrice = dBrickClose;
				dClosePrice = dBrickClose + dBrickCondition;

				if(thirdData.length<=count)
					break;
				thirdData[count][0] = 1;
				thirdData[count][1] = dOpenPrice;
				thirdData[count][2] = dClosePrice;
                dateData[count] = dates[i];
                fCloseData[count] = data[i];
				count++;

				dBrickOpen = dOpenPrice; /* ReSetting BrickData */
				dBrickClose = dClosePrice;
			}
			while( dDataPrice <= dBrickClose - dBrickCondition && dDataPrice <= dBrickOpen - dBrickCondition)
			{
				dBrickClose = dBrickClose < dBrickOpen ? dBrickClose : dBrickOpen;
		        /* Down Type Brick 생성 */
				dOpenPrice = dBrickClose;
				dClosePrice = dBrickClose - dBrickCondition;

				if(thirdData.length<=count)
					break;
				thirdData[count][0] = -1;
				thirdData[count][1] = dClosePrice;
				thirdData[count][2] = dOpenPrice;

                dateData[count] = dates[i];
                fCloseData[count] = data[i];
				count++;
				// ReSetting BrickData
				dBrickOpen = dOpenPrice;
				dBrickClose = dClosePrice;
			}

			if(i<nListCount-1)
				dDataPrice = data[i+1];	// Next 종가
			//nSkipCount++;
		}
//		count++;
		TotMinMax[0] = min_data;
		TotMinMax[1] = max_data;

		//count = thirdData.length;

		thirdDataResult = new double[count][3];
		System.arraycopy(thirdData, 0, thirdDataResult, 0, count);

		DrawTool t = (DrawTool)tool.get(0);
		_cdm.setSubPacketData(t.getPacketTitle(), TotMinMax);


        double[] closeDataResult = new double[count];
        System.arraycopy(fCloseData, 0, closeDataResult, 0, count);
        _cdm.setSubPacketData("variable_close", closeDataResult);

		ChartPacketDataModel cpdm = _cdm.getChartPacket("variable_close");
		if(cpdm != null) {
			cpdm.nTradeMulti = _cdm.nTradeMulti;
			cpdm.nDispScale = _cdm.nDispScale;
			cpdm.nLogDisp = _cdm.nLogDisp;
			if(_cdm.nTradeMulti > 0) {
				cpdm.format = 16;
			}
		}

        String[] dateDataResult = new String[count];
        System.arraycopy(dateData, 0, dateDataResult, 0, count);
        _cdm.setSubPacketData("variable_자료일자", dateDataResult);

        int nDataCount;
        int[] nAverageInterval = {5,10,20};
        for(i=0;i<tool.size()-1;i++){
            DrawTool dt = (DrawTool)tool.elementAt(i+1);
            _cdm.setSubPacketData(dt.getPacketTitle(),makeAverage(closeDataResult,nAverageInterval[i]));

			cpdm = _cdm.getChartPacket(dt.getPacketTitle());
			if(cpdm != null) {
				cpdm.nTradeMulti = _cdm.nTradeMulti;
				cpdm.nDispScale = _cdm.nDispScale;
				cpdm.nLogDisp = _cdm.nLogDisp;
				if(_cdm.nTradeMulti > 0) {
					cpdm.format = 16;
				}
			}
        }
	}

	public void reFormulateData(){
		FormulateData();
		formulated = true;
	}
	public void drawGraph(Canvas gl){
		//if(!formulated)FormulateData();                       //저장되어 있지 않다면 계산을 새로 한다
		DrawTool t = (DrawTool)tool.elementAt(0);
		//System.out.println(" count : " + count);
//        int[][] data = new int[count][3];
//        System.arraycopy(thirdData, 0, data, 0, count);

		t.plot(gl, thirdDataResult);

        double[] drawData=null;
        for(int i=0;i<tool.size();i++){
            t=(DrawTool)tool.elementAt(i);
            try{
                //if(t.isVisible()){
                //if(_cvm.average_state[i]){
                drawData=_cdm.getSubPacketData(t.getPacketTitle());
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
		return "렌코";
	}
}