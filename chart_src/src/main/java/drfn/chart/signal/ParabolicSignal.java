package drfn.chart.signal;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.graph.AbstractGraph;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartPacketDataModel;
import drfn.chart.model.ChartViewModel;

public class ParabolicSignal extends AbstractGraph{
	int[][] data;
	int[] sar;
	//int[] base=null;
	double af = 0.02D;//가속도 승수
	String[] datakind = {"고가","저가","종가"};//그래프에 사용될 데이터
	public ParabolicSignal(ChartViewModel cvm, ChartDataModel cdm){
		super(cvm,cdm);
		setDatakind(datakind);
		definition="Welles Wilder에 의해 개발된 지표로서 추세전환의 신호를 포물선의 형태로 표현해 주는 지표입니다.  일반적으로 매매신호가 늦지만 발생했을 경우 확실한 신호를 줍니다.  특히 추세시장에서 유용합니다.  사용자 입력수치는 가속변수라고도 부르는 AF값의 최대값과 증가단위입니다.  일반적으로 AF최대값은 0.2, AF증가분은 0.02를 사용합니다";
		m_strDefinitionHtml = "parabolic_sar.html";	//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)
	}

	//============================================
	// parabolicSAR :
	//    _interval:n일
	//    calcData[i]:금일종가
	//    calcData[i-_interval]:n일전종가
	//    highist[]:고가
	//    AF(ACceleration Factor):가속도 승수(d2,d3)
	//    EP(Extreme Price):중요가격
	//내일의 SAR = 오늘의 SAR + 가속도 * (중요가격 - 오늘의 SAR)
	//============================================
	public void FormulateData() {
		double[] highData = _cdm.getSubPacketData("고가");
		double[] lowData = _cdm.getSubPacketData("저가");
		double[] closeData = _cdm.getSubPacketData("종가");
		if(closeData==null) return;
		int dLen = closeData.length;
		double[] sar = new double[dLen];

        double ldRetVal = 0;
        double ldHigh;
        double ldLow;
        double ldClose;
        int ldpLastIndex=0;
        double ldpLastResult=0;
        double ldpBeforeResult=0;
        double ldpBeforeHighValue=0;
        double ldpBeforeLowValue=0;
        double ldpLastHighValue=0;
        double ldpLastLowValue=0;
        double ldpLastPos=0;
        double ldpBeforePos=0;
        double ldpBeforeAF=0;
        double ldpLastAF=0;
        int lIndex;

        //        double DAFMax = 0.2;
        //        double DAFIncrease = 0.02;
        double dMaxAF = (double)interval[0]/100.0;
        double dAF = (double)interval[1]/100;
        //double dAF = dMaxAF ;		// 가속도
        for( lIndex=0; lIndex<dLen; lIndex++ ){
            if (lIndex < 5)
            {
                ldHigh = highData[lIndex];
                ldLow = lowData[lIndex];

                if (lIndex == 0)
                {
                    ldpBeforeHighValue = ldHigh;
                    ldpBeforeLowValue = ldLow;
                    ldpLastHighValue = ldHigh;
                    ldpLastLowValue = ldLow;

                    ldpBeforeAF = dAF;
                    ldpLastAF = dAF;
                }
                else
                {
                    if ( lIndex != ldpLastIndex)
                    {
                        ldpBeforeHighValue = ldpLastHighValue;
                        ldpBeforeLowValue = ldpLastLowValue;
                    }
                    else
                    {
                        ldpLastHighValue = ldpBeforeHighValue;
                        ldpLastLowValue = ldpBeforeLowValue;
                    }

                    if (ldHigh > ldpBeforeHighValue) ldpLastHighValue = ldHigh;
                    if (ldLow < ldpBeforeLowValue) ldpLastLowValue = ldLow;
                }

                ldpLastIndex = lIndex;
                ldpLastResult = ldpLastHighValue;
                ldpLastPos = -1.;
                ldpBeforeResult = ldpLastResult;
                ldpBeforePos = ldpLastPos;

                if (lIndex == 4)
                {
                    sar[lIndex] = ldpLastResult;
                }
                else
                {
                    sar[lIndex] = 0;
                }
                continue;
            }

            if (lIndex != ldpLastIndex)
            {
                ldpBeforeResult = ldpLastResult;
                ldpBeforePos = ldpLastPos;
                ldpBeforeHighValue = ldpLastHighValue;
                ldpBeforeLowValue = ldpLastLowValue;
                ldpBeforeAF = ldpLastAF;
            }
            else
            {
                ldpLastHighValue = ldpBeforeHighValue;
                ldpLastLowValue = ldpBeforeLowValue;
                ldpLastAF = ldpBeforeAF;
            }

            ldHigh = highData[lIndex];
            ldLow = lowData[lIndex];
            ldClose = closeData[lIndex];

            if (ldpLastPos == 1. && ldLow <= ldpBeforeResult)
            {
                ldpLastPos = -1.;
            }
            else if (ldpLastPos == -1. && ldHigh >= ldpBeforeResult)
            {
                ldpLastPos = 1.;
            }

            if (ldpLastPos == 1.)
            {
                if (ldpBeforePos != 1.)
                {
                    ldpLastResult = ldpLastLowValue;
                    ldpLastAF = dAF;
                    ldpLastHighValue = ldHigh;
                    ldpLastLowValue = ldLow;
                }
                else
                {
                    ldpLastResult = ldpBeforeResult + ldpLastAF *
                            (ldpLastHighValue - ldpBeforeResult);

                    if (ldHigh > ldpLastHighValue)
                    {
                        ldpLastHighValue = ldHigh;
                    }

                    if (ldpLastHighValue > ldpBeforeHighValue &&
                            ldpLastAF < dMaxAF)
                    {
                        ldpLastAF = ldpLastAF +
                                (dAF < (dMaxAF - ldpLastAF) ? dAF : (dMaxAF - ldpLastAF));
                    }
                }

                if (ldpLastResult > ldLow &&
                        ldpBeforeResult < lowData[lIndex - 1])
                {
                    ldpLastResult = ldpLastHighValue;
                    ldpLastPos = -1.;
                    ldpLastAF = dAF;
                    ldpLastHighValue = ldHigh;
                    ldpLastLowValue = ldLow;
                }
            }
            else
            {
                if (ldpBeforePos != -1.)
                {
                    ldpLastResult = ldpLastHighValue;
                    ldpLastAF = dAF;
                    ldpLastHighValue = ldHigh;
                    ldpLastLowValue = ldLow;
                }
                else
                {
                    ldpLastResult = ldpBeforeResult + ldpLastAF *
                            (ldpLastLowValue - ldpBeforeResult);

                    if (ldLow < ldpLastLowValue)
                    {
                        ldpLastLowValue = ldLow;
                    }

                    if (ldpLastLowValue < ldpBeforeLowValue &&
                            ldpLastAF < dMaxAF)
                    {
                        ldpLastAF = ldpLastAF +
                                (dAF < (dMaxAF - ldpLastAF) ? dAF : (dMaxAF - ldpLastAF));
                    }
                }

                if (ldpLastResult < ldHigh &&
                        ldpBeforeResult > highData[lIndex - 1])
                {
                    ldpLastResult = ldpLastLowValue;
                    ldpLastPos = 1.;
                    ldpLastAF = dAF;
                    ldpLastHighValue = ldHigh;
                    ldpLastLowValue = ldLow;
                }
            }
            sar[lIndex] = ldpLastResult;
        }

//        double dPAR=0., dAF=0., dEP=0./*, dMax=0., dMin=0.*/;
//	    int nCU, nCD;
//	    int i=0;
//	    double DAFMax = (double)interval[0]/100.0;
//	    double DAFIncrease = (double)interval[1]/100.0;
//	    dAF = DAFMax ;		// 가속도
//	    nCU = nCD = 0 ;		// 상승/하락 지속일
////	    dMax = highData[0]; // 최고 중요값
////	    dMin = lowData[0] ; // 최저 중요값
//	    for( i=0; i<dLen; i++ ){
//	    	if(i<5){//최초의 sar를 구한다
//                if(i==0){
//                    dPAR = highData[i];
//                    dEP = lowData[i];
//                    //nCU++ ;
//                }else {
//                    if(dPAR <highData[i])
//                        dPAR = highData[i];
//                    if(dEP >lowData[i])
//                        dEP = lowData[i];
//                }
//                if(i<4)
//                {
//                	sar[i] = 0;
//                }
//                else {
//                	sar[i] = dPAR;
//                    if(closeData[i] > closeData[i-1]){
//                        dEP = dPAR;
//                        nCU = 1;
//                        nCD = 0 ;
//                        dAF = DAFIncrease;
//                    }else { 	//if ( closeData[i] < closeData[i-1] ){
//                        nCD = 1;
//                        nCU = 0 ;
//                        dAF = DAFIncrease;
//
//                    }
//                }
//		    }else{
//		        if ( nCU != 0 && dPAR > lowData[i]){// 하락추세일때 sar가 저가보다 큰경우
//			        nCU = 0;
//			        nCD = 1;
//			        dAF = DAFIncrease;
//			        dPAR = dEP;
//			        dEP = lowData[i];
//		        }else if ( nCD != 0 && dPAR < highData[i] ){//상승추세일때 sar가 고가보다 작은경우
//			        nCU = 1;
//			        nCD = 0;
//			        dAF = DAFIncrease;
//			        dPAR = dEP;
//			        dEP = highData[i];
//		        }else{
//			        dPAR = dPAR + dAF * ( dEP - dPAR ) ;
//			        if(nCU!=0){//하락추세이면서 sar가 저가보다 작은경우
//			        	if(dPAR >= lowData[i]){// 하락추세일때 sar가 저가보다 큰경우
//                            nCU = 0;
//                            nCD = 1;
//                            dAF = DAFIncrease;
//                            dPAR = dEP;
//                            dEP = lowData[i];
//                        }
//			        	else if(dEP<highData[i]){
//					        dEP = highData[i];
//					        //dAF += DAFIncrease ;
//					        dAF = dAF + ((DAFMax-dAF)>DAFIncrease? DAFIncrease:(DAFMax-dAF)) ;
//				        }
//			        }else if ( nCD != 0 ){//상승추세이면서 sar가 고가보다 큰경우
//                        if(dPAR <= highData[i] ){//상승추세일때 sar가 고가보다 작은경우
//                            nCU = 1;
//                            nCD = 0;
//                            dAF = DAFIncrease;
//                            dPAR = dEP;
//                            dEP =highData[i];
//                        }
//                        else if(dEP>lowData[i]){
//					        dEP = lowData[i];
//					        //dAF += DAFIncrease ;
//					        dAF = dAF + ((DAFMax-dAF)>DAFIncrease? DAFIncrease:(DAFMax-dAF)) ;
//				        }
//			        }
//			        if ( dAF > DAFMax )dAF = DAFMax ;
//		        }
//		    }
//		    sar[i] = dPAR ;
//	    }

		//double	dPAR = 0.0, dAF, dEP = 0.0, dMax, dMin;
//		double	dPAR = 0.0, dAF, dEP = 0.0;
//		int		nCU, nCD;
//
//		double dAFMax = (double)interval[0]/100.0;
//		double dAFIncrease = (double)interval[1]/100.0;
//		dAF = dAFMax ;		// 가속도
//		nCU = nCD = 0 ;		// 상승/하락 지속일
//		int nStart = 0;
////		dMax = highData[nStart];	// 최고 중요값
////		dMin = lowData[nStart];	// 최저 중요값
//
//		double dClose = 0.0;
//		double dPrevClose = 0.0;
//		double dPaVal = 0.0;
//
//		int i = 0;
//		for(i=nStart+1;i<dLen-1;i++)
//		{
//			dClose = closeData[i];
//			dPrevClose = closeData[i-1];
//			if ( dClose > dPrevClose )
//			{
//				dEP = highData[i];
//				dPaVal = lowData[i] ;			// 파라볼릭 값
//				sar[i] = dPaVal;
//				dPAR = lowData[i] ;
//				nCU++ ;
//				nCD = 0 ;
//				break;
//			}
//			else if ( dClose < dPrevClose )
//			{
//				dEP = lowData[i] ;
//				dPaVal = highData[i];			// 파라볼릭 값
//				sar[i] = dPaVal;
//				dPAR = highData[i] ;
//				nCD++ ;
//				nCU = 0 ;
//				break;
//			}
//		}
//
//		i++;
//		for ( ; i < dLen ; i++ )
//		{
//			if ( nCU != 0 && dPAR > lowData[i] )	// 포지션이 short으로 전환
//			{
//				nCU = 0 ;
//				nCD = 1 ;
//				dAF = dAFIncrease ;
//				dPAR = dEP ;
//				dEP = lowData[i] ;
//			}
//			else if ( nCD != 0 && dPAR < highData[i])//포지션이 long으로 전환
//			{
//				nCU = 1 ;
//				nCD = 0 ;
//				dAF = dAFIncrease ;
//				dPAR = dEP ;
//				dEP = highData[i];
//			}
//			else
//			{
//				dPAR = dPAR + dAF * ( dEP - dPAR ) ;
//
//				if ( nCU != 0 )
//				{
//					if ( dEP < highData[i] )
//					{
//						dEP = highData[i];
//						dAF += dAFIncrease ;
//					}
//				}
//				else if ( nCD != 0 )
//				{
//					if ( dEP > lowData[i])
//					{
//						dEP = lowData[i];
//						dAF += dAFIncrease ;
//					}
//				}
//
//				if ( dAF > dAFMax )
//					dAF = dAFMax;
//			}
//			sar[i] =dPAR;
//		}
		for(int i=0;i<tool.size();i++){
			DrawTool dt = (DrawTool)tool.elementAt(i);
			_cdm.setSubPacketData(dt.getPacketTitle(),sar);
			//2014.03.18 by LYH << 지표 소수점 자리수 가격과 동일하게 처리.
			if(_cdm.nTradeMulti>0)
				_cdm.setSyncPriceFormat(dt.getPacketTitle());
			else
			{
				ChartPacketDataModel cpdm =_cdm.getChartPacket("종가");
				if(cpdm.getPacketFormat() == 14)
					_cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
			}
			//2014.03.18 by LYH << 지표 소수점 자리수 가격과 동일하게 처리.
		}
		formulated = true;
	}
	public void reFormulateData() {
		FormulateData();
		formulated = true;
	}
	public void drawGraph(Canvas gl) {
        if (!formulated) FormulateData();

        if(tool.size()>0) {
            DrawTool t = tool.get(0);
            double[] baseData = _cdm.getSubPacketData("종가");
            double[] signalData = _cdm.getSubPacketData(t.getPacketTitle());
            if(m_nStrategyType == 1)
                t.plotStrategyStrongWeak(gl,baseData,signalData);
            else
                t.plotStrategy(gl,baseData,signalData);
        }
    }
	public void drawGraph_withSellPoint(Canvas g){
	}

	public String getName(){

        if(m_nStrategyType == 1)
            return "Parabolic SAR 강세약세";
        else
            return "Parabolic SAR 신호";
	}
}