package drfn.chart.graph;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

public class RCIGraph extends AbstractGraph{
	double[] rci1;
	double[] rci2;
	double[] rci3;
	//double[] rci4;
	int[][] data;
	//int[] base=null;

	public RCIGraph(ChartViewModel cvm, ChartDataModel cdm){
		super(cvm,cdm);
		String[] datakind = {"고가","저가","종가"};
		_dataKind = datakind;
		definition="이 지표는 현재의 가격이 일정기간의 고가와 저가 사이에 어디에 위치하는 가를 나타내는 스토캐스틱과 유사하며, 분석기법도 같은 방법을 사용하면 될것 같습니다.단, -100부터 +100 사이에서 움직이므로 이를 기준으로하여 과열과 침체 영역을 정해야 합니다.";
		m_strDefinitionHtml = "rci.html";	//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)
	}
	public void FormulateData(){
		double[] closeData = _cdm.getSubPacketData("종가");
		if(closeData==null) return;
		int dLen = closeData.length;
		rci1 = new double[dLen];
		rci2 = new double[dLen];
		rci3 = new double[dLen];
		//rci4 = new double[dLen];

		for(int i=1; i<dLen; i++){
			rci1[i] = this.RCI(i, interval[0], closeData);
			rci2[i] = this.RCI(i, interval[1], closeData);
			rci3[i] = this.RCI(i, interval[2], closeData);
			//rci4[i] = this.RCI(i, interval[3], closeData);
		}

		for(int i=0;i<3;i++){
			DrawTool dt = (DrawTool)tool.elementAt(i);
			if(i==0)_cdm.setSubPacketData(dt.getPacketTitle(),rci1);
			else if(i==1) _cdm.setSubPacketData(dt.getPacketTitle(),rci2);
			else if(i==2) _cdm.setSubPacketData(dt.getPacketTitle(),rci3);
			//else if(i==3) _cdm.setSubPacketData(dt.getPacketTitle(),rci4);
			_cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
		}
		formulated = true;
	}

//	private double RCI(int lIndex, int lPeriod, double[] closeData) {
//		int RCI_MAX_CNT = 400;
//
//		if(closeData==null) return 0;
//		int i, j;
//		double ldRetVal = 0;
//		double[][] ltpRCIList; //0:ltpRCIListPriceIdx, 1:ltpRCIListDateIdx, 2:ltpRCIListValue
//		double ldRCIIndex = 0.;
//
//		if (lPeriod < 1 || lIndex + 1 < lPeriod)
//		{
//			return ldRetVal;
//		}
//
//		if (lPeriod > RCI_MAX_CNT && lIndex >= RCI_MAX_CNT)
//		{
//			ltpRCIList = new double[3][lPeriod];
//		}
//		else
//		{
//			ltpRCIList = new double[3][RCI_MAX_CNT];
//		}
//
//		int nTemp = lPeriod - 1;
//		for (i = lIndex, j = 0;  j < lPeriod && i >= 0; i--, j++)
//		{
//			ltpRCIList[0][j] =  j + 1;
//			ltpRCIList[2][j] = closeData[i];
//		}
//
//		//RCI_ORDER_ASC:1
//		ltpRCIList = RCISorting(ltpRCIList, lPeriod, 0);
//
//		double dStandardValue = ltpRCIList[2][0];
//		double dTempValue = 0.;
//		double dIndex = 0.;
//		boolean bDoneAverageIndex=false;
//
//		for( j = 0; j < lPeriod; j++ )
//		{
//			if(j == 0)
//			{
//				dIndex = j + 1.;
//				ltpRCIList[1][j] = dIndex;
//				continue;
//			}
//			dTempValue = ltpRCIList[2][j];
//			// 소팅된 리스트 순서대로 종가값 찾기
//			if( dStandardValue == dTempValue )
//			{
//				if(!bDoneAverageIndex)
//				{
//					bDoneAverageIndex = true;
//					int nCount = 0;
//					double dAverageIndex = 0.;
//
//					for(int nIdx = j -1; nIdx < lPeriod; ++nIdx)
//					{
//						dTempValue = ltpRCIList[2][nIdx];
//						if( dStandardValue == dTempValue )
//						{
//							dAverageIndex += (nIdx + 1);
//							nCount++;
//						}
//						else
//							break;
//					}
//					dIndex = dAverageIndex / nCount;
//					ltpRCIList[1][j-1] = dIndex;
//				}
//			}
//			else if( dStandardValue > dTempValue)
//			{
//				bDoneAverageIndex = false;
//				dIndex = j + 1.;
//				dStandardValue = dTempValue;
//			}
//			ltpRCIList[1][j] = dIndex;
//		}
//
//		// RCI지표 (Rank Correlation Index)
//		//                   6 * d
//		//( 1 - ------------------------------------ ) * 100
//		//       기간날짜 * ( 기간날짜의 제곱 - 1 )
//
//		double dDiff;
//		for(j = 0; j < lPeriod; j++)
//		{
//			dDiff = ltpRCIList[0][j] - ltpRCIList[1][j];
//			ldRCIIndex += (dDiff * dDiff);
//		}
//
//		ldRetVal = (1. - ((6 * ldRCIIndex) / (lPeriod * (lPeriod * lPeriod - 1)))) * 100.;
//
//		return ldRetVal;
//	}
//
//	private double[][] RCISorting(double[][] ptpRCIList, int piCnt, int piOrder)
//	{
//		int i, j;
//		double[][] ltRCITmp = new double[3][ptpRCIList[0].length];
//
//		if (piOrder == 1) //RCI_ORDER_ASC
//		{
//			for (i = 0; i < piCnt - 1; i++)
//			{
//				for (j = i + 1; j < piCnt; j++)
//				{
//					if (ptpRCIList[2][i] > ptpRCIList[2][j])
//					{
//						ltRCITmp[0][i] = ptpRCIList[0][i];
//						ltRCITmp[1][i] = ptpRCIList[1][i];
//						ltRCITmp[2][i] = ptpRCIList[2][i];
//
//						ptpRCIList[0][i] = ptpRCIList[0][j];
//						ptpRCIList[1][i] = ptpRCIList[1][j];
//						ptpRCIList[2][i] = ptpRCIList[2][j];
//
//
//						ptpRCIList[0][j] = ltRCITmp[0][i];
//						ptpRCIList[1][j] = ltRCITmp[1][i];
//						ptpRCIList[2][j] = ltRCITmp[2][i];
//					}
//				}
//			}
//		}
//		else
//		{
//			for (i = 0; i < piCnt - 1; i++)
//			{
//				for (j = i + 1; j < piCnt; j++)
//				{
//					if (ptpRCIList[2][i] < ptpRCIList[2][j])
//					{
//						ltRCITmp[0][i] = ptpRCIList[0][i];
//						ltRCITmp[1][i] = ptpRCIList[1][i];
//						ltRCITmp[2][i] = ptpRCIList[2][i];
//
//						ptpRCIList[0][i] = ptpRCIList[0][j];
//						ptpRCIList[1][i] = ptpRCIList[1][j];
//						ptpRCIList[2][i] = ptpRCIList[2][j];
//
//
//						ptpRCIList[0][j] = ltRCITmp[0][i];
//						ptpRCIList[1][j] = ltRCITmp[1][i];
//						ptpRCIList[2][j] = ltRCITmp[2][i];
//					}
//				}
//			}
//		}
//
//		return ptpRCIList;
//	}

	private double RCI(int lIndex, int lPeriod, double[] closeData) {
		int RCI_MAX_CNT = 400;

		if(closeData==null) return 0;
		int i, j;
		double ldRetVal = 0;
		double[][] ltpRCIList; //0:ltpRCIListPriceIdx, 1:ltpRCIListDateIdx, 2:ltpRCIListValue
		double ldRCIIndex = 0.;

		if (lPeriod < 1 || lIndex + 1 < lPeriod)
		{
			return ldRetVal;
		}

		if (lPeriod > RCI_MAX_CNT && lIndex >= RCI_MAX_CNT)
		{
			ltpRCIList = new double[3][lPeriod];
		}
		else
		{
			ltpRCIList = new double[3][RCI_MAX_CNT];
		}

		int nTemp = lPeriod - 1;
		for (i = lIndex, j = 0;  j < lPeriod && i >= 0; i--, j++)
		{
			ltpRCIList[1][nTemp - j] = nTemp - j + 1;
			ltpRCIList[2][nTemp - j] = closeData[i];
		}

		//RCI_ORDER_ASC:1
		ltpRCIList = RCISorting(ltpRCIList, lPeriod, 1);

		double dStandardValue = 0.;
		double dTempValue = 0.;
		double dTempIndex = 0.;
		double dIndex = 0.;
		for (j = 0; j < lPeriod; j++)
		{
			for( i = 0; i < lPeriod; i++ )
			{
				dTempIndex = ltpRCIList[1][i];
				// 소팅된 리스트 순서대로 종가값 찾기
				if( j + 1 == dTempIndex )
				{
					// 종가 가격 구하기
					dStandardValue = ltpRCIList[2][i];

					for( int nIndex = 0; nIndex < lPeriod; nIndex++ )
					{
						dTempValue = ltpRCIList[2][nIndex];

						if( dTempValue == dStandardValue )
						{
							dIndex = nIndex + 1.;
							ltpRCIList[0][j] = dIndex;
						}

					}
				}
			}
		}

		// RCI지표 (Rank Correlation Index)
		//                   6 * d
		//( 1 - ------------------------------------ ) * 100
		//       기간날짜 * ( 기간날짜의 제곱 - 1 )

		double dDiff;
		for(j = 0; j < lPeriod; j++)
		{
			dDiff = j + 1 - ltpRCIList[0][j];
			ldRCIIndex += (dDiff * dDiff);
		}

		ldRetVal = (1. - (6 * ldRCIIndex / (lPeriod * (lPeriod * lPeriod - 1)))) * 100.;

		return ldRetVal;
	}

	private double[][] RCISorting(double[][] ptpRCIList, int piCnt, int piOrder)
	{
		int i, j;
		double[][] ltRCITmp = new double[3][ptpRCIList[0].length];

		if (piOrder == 1) //RCI_ORDER_ASC
		{
			for (i = 0; i < piCnt - 1; i++)
			{
				for (j = i + 1; j < piCnt; j++)
				{
					if (ptpRCIList[2][i] > ptpRCIList[2][j])
					{
						ltRCITmp[0][i] = ptpRCIList[0][i];
						ltRCITmp[1][i] = ptpRCIList[1][i];
						ltRCITmp[2][i] = ptpRCIList[2][i];

						ptpRCIList[0][i] = ptpRCIList[0][j];
						ptpRCIList[1][i] = ptpRCIList[1][j];
						ptpRCIList[2][i] = ptpRCIList[2][j];


						ptpRCIList[0][j] = ltRCITmp[0][i];
						ptpRCIList[1][j] = ltRCITmp[1][i];
						ptpRCIList[2][j] = ltRCITmp[2][i];
					}
				}
			}
		}
		else
		{
			for (i = 0; i < piCnt - 1; i++)
			{
				for (j = i + 1; j < piCnt; j++)
				{
					if (ptpRCIList[2][i] < ptpRCIList[2][j])
					{
						ltRCITmp[0][i] = ptpRCIList[0][i];
						ltRCITmp[1][i] = ptpRCIList[1][i];
						ltRCITmp[2][i] = ptpRCIList[2][i];

						ptpRCIList[0][i] = ptpRCIList[0][j];
						ptpRCIList[1][i] = ptpRCIList[1][j];
						ptpRCIList[2][i] = ptpRCIList[2][j];


						ptpRCIList[0][j] = ltRCITmp[0][i];
						ptpRCIList[1][j] = ltRCITmp[1][i];
						ptpRCIList[2][j] = ltRCITmp[2][i];
					}
				}
			}
		}

		return ptpRCIList;
	}

	public void reFormulateData() {
		FormulateData();
		formulated = true;
	}
	public void drawGraph(Canvas g){
		if(!formulated)FormulateData();                       //저장되어 있지 않다면 계산을 새로 한다

		double[] drawData=null;
		for(int i=0;i<3;i++){
			DrawTool t=(DrawTool)tool.elementAt(i);
			try{
				drawData=_cdm.getSubPacketData(t.getPacketTitle());
			}catch(ArrayIndexOutOfBoundsException e){
				return;
			}

			if(i==0) _cvm.useJipyoSign=true;
			else _cvm.useJipyoSign=false;

			t.plot(g,drawData);
//          if(base!=null&&i<base.length)t.draw(g,base[i]);
			//2013. 9. 5 지표마다 기준선 설정 추가>>
			drawBaseLine(g);
			//2013. 9. 5 지표마다 기준선 설정 추가>>
		}


		//2014. 9. 15 매매 신호 보기 기능 추가>>
//        DrawTool t=(DrawTool)tool.elementAt(0);
//        if(isSellingSignalShow)
//        	t.drawSignal(g, pdiData, ndiData);
		//2014. 9. 15 매매 신호 보기 기능 추가<<
	}
	public void drawGraph_withSellPoint(Canvas g){
	}

	public String getName(){
		return "RCI";
	}
}
