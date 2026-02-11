package drfn.chart.draw;

import static drfn.chart.model.ChartViewModel.FX_AVERAGE;
import static drfn.chart.model.ChartViewModel.FX_BUY;
import static drfn.chart.model.ChartViewModel.FX_BUYSELL;
import static drfn.chart.model.ChartViewModel.FX_SELL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;
import drfn.chart.scale.AREA;
import drfn.chart.util.COMUtil;
import drfn.chart.util.ChartUtil;
import drfn.chart.util.CoSys;
import drfn.chart.util.MinMax;

/**
 *  1. 채움식인지 비채움식인지
 *  2. 입체효과를 내는지 아닌지
 *  3. 테두리선을 쓰는지 안쓰는지
 *  4. 미국식봉인지 일본봉인지
 *  5.
 *
 */
public class BongDraw extends DrawTool{
	int back = Color.rgb(196,212,209);
	double[][] bongData = null;
	String[] rights=null;
	private boolean minset=false;
	private boolean maxset=false;
	private ImageView blinkImg;
	private RelativeLayout.LayoutParams blinkParams;
//	private RelativeLayout blinkLayout;
	private int blinkImgW = (int)COMUtil.getPixel(15);
	private int blinkImgH = (int)COMUtil.getPixel(15);

	public BongDraw(ChartViewModel cvm, ChartDataModel cdm){
		super(cvm, cdm);
	}
	public void draw(Canvas gl, double data){//기준가 없이 그리는 바
	}
	public void draw(Canvas gl, double[] data, double[] stand){
	}
	public void drawDefault(Canvas gl, double[] data){
	}
	public void drawVolumeForSale(Canvas gl, double[] stand){//대기매물용
	}
	public void draw(Canvas gl, double[] data){
		if(data==null||data.length<1)return;
		//2013.07.31 >> 기준선 라인 차트 타입 추가 
		if (_cdm.codeItem.strStandardPrice != null && _cdm.codeItem.strStandardPrice.length() > 0 && getDrawType2() != 3)
		{
			//2015.01.08 by LYH >> 3일차트 추가
			String[] arrStandard = _cdm.codeItem.strStandardPrice.split("\\|");
			if( arrStandard.length > 2 )
			{
				draw(gl, data, arrStandard[0]);
			}
			else
				//2015.01.08 by LYH << 3일차트 추가
				draw(gl, data, _cdm.codeItem.strStandardPrice);
			return;
		}
		//2013.07.31 <<
		this.data = data;
		float xpos=getBounds().left+xw;
		float ypos=0;
		float ypos1=0;
		minset=maxset=false;
//        int x=0, y=0;
		float highXPos = -1;
		float highYPos = -1;
		float lowXPos = -1;
		float lowYPos = -1;

		double yInc;
		line_thick=2;
		int thick =line_thick, temp;
		int startIndex = _cvm.getIndex();
		int dataLen = startIndex + _cvm.getViewNum();
		if(dataLen>data.length)
			dataLen= data.length;
		int totLen = (dataLen-startIndex-1)*4;
		if(dataLen==1) {
			totLen = 4;
		}
		//2024.01.03 by LYH - 일목균형표 줌인, 줌아웃, 스크롤 개선 >>
		if(totLen <=0)
			return;
		//2024.01.03 by LYH - 일목균형표 줌인, 줌아웃, 스크롤 개선 <<
		float[] positions = new float[totLen];// = new float[dataLen];

		//2021.09.28 by lyk - kakaopay - 최고/최저 표시를 봉그래프 위에 그리도록 수정 >>
		float[] maxPositions = new float[4]; //x,y,index,boolean(up/down)
		float[] minPositions = new float[4]; //x,y,index,boolean(up/down)
		//2021.09.28 by lyk - kakaopay - 최고/최저 표시를 봉그래프 위에 그리도록 수정 <<

		int nIndex = 0;
		//2017.11 by pjm 퇴직연금 애니매이션 차트 >>
		boolean isAnimationEnd = false;
		float barValue, barValue1;
		double dStdPrice = 0;
		dStdPrice = min_data-1.0;
		//2017.11 by pjm 퇴직연금 애니매이션 차트 end
		if(dataLen==1) {
			ypos=(int)calcy(data[0]);
			positions[0] = xpos;
			positions[1] = ypos;
			positions[2] = xpos+line_thick;
			positions[3] = ypos;

			nIndex=3;
		} else {
			for(int i=startIndex; i<dataLen-1; i++){
				//2017.11 by pjm 퇴직연금 애니매이션 차트 >>
				if(_cvm.m_bUseAnimationLine && m_bInit) {
					if(dynamicData != null && dynamicData.size()>(i+1))
					{
						barValue = dynamicData.get(i).getPosition();
						barValue1 = dynamicData.get(i+1).getPosition();
						//애니메이션 쓰레드가 끝나는 시점 포착
						if(dDatasAni[i]>dStdPrice && barValue>=dDatasAni[i] &&  dDatasAni[i]!=dStdPrice) {
							//end ani
							isAnimationEnd = true;
						}
						if(dDatasAni[i]<dStdPrice && barValue<=dDatasAni[i] &&  dDatasAni[i]!=dStdPrice) {
							//end ani
							isAnimationEnd = true;
						}

						//2021.01.21 by LYH - 빈 부분 안 그리기 >>
						if(data[i] == -9999)
							ypos = calcy(data[i]);
						else {
							ypos = calcy(barValue);
							ypos=(ypos>max_view)?max_view:ypos;
							ypos=(ypos<min_view)?min_view:ypos; // y축 최소값 비교.
						}
						if(data[i+1] == -9999)
							ypos1 = calcy(data[i+1]);
						else {
							ypos1 = calcy(barValue1);
							ypos1=(ypos1>max_view)?max_view:ypos1;
							ypos1=(ypos1<min_view)?min_view:ypos1; // y축 최소값 비교.
						}
						//2021.01.21 by LYH - 빈 부분 안 그리기 <<
					}
					else
						break;
				}
				else
				{
					//2017.11 by pjm 퇴직연금 애니매이션 차트 <<
					ypos=(int)calcy(data[i]);

					ypos1 = (int)calcy(data[i+1]);
				}
				if((ypos<=max_view&&ypos1<=max_view)){
					yInc = (double)(((double)ypos1-(double)ypos)/(double)(xfactor*2));
					temp = (yInc<0)?Math.abs((int)(-1*(yInc-0.99))):(int)(yInc+0.99);
					thick=(temp>thick)?temp:line_thick;

					positions[nIndex++] = xpos;
					positions[nIndex++] = ypos;
					positions[nIndex++] = xpos+xfactor;
					positions[nIndex++] = ypos1;
					//	            if(ypos>ypos1){
					//	                _cvm.drawLine(gl, (int)xpos+line_thick-1,(int)ypos+line_thick,(int)(xpos+xfactor+line_thick-1),(int)ypos1+line_thick, CoSys.UP_LINE_COLORS ,1.0f);
					//	            } else if(ypos<ypos1) {
					//	            	_cvm.drawLine(gl, (int)xpos+line_thick-1,(int)ypos+line_thick,(int)(xpos+xfactor+line_thick-1),(int)ypos1+line_thick, CoSys.UP_LINE_COLORS ,1.0f);
					//	            }
					//            	else{
					//	                _cvm.drawLine(gl, (int)xpos+line_thick,(int)ypos+line_thick,(int)(xpos+xfactor+line_thick),(int)ypos1+line_thick, CoSys.UP_LINE_COLORS ,1.0f);
					//            	}

					if(isSelected()){
						if(i%5==0){
							_cvm.drawRect(gl, (int)xpos,(int)ypos,5,5, CoSys.UP_LINE_COLORS);
						}
					}
				}
				if(data[i]==getBongMax()){
					if(!maxset){
						if(!_cvm.bIsLineChart && !_cvm.bIsLine2Chart || _cvm.bIsHighLowSign) {
							//2021.09.28 by lyk - kakaopay - 최고/최저 표시를 봉그래프 위에 그리도록 수정 >>
//							drawMinMaxString(gl,(int)xpos,(int)ypos-(int)COMUtil.getPixel(1),i,true);
							//x,y,index,boolean(up/down)
							maxPositions[0] = xpos;
							maxPositions[1] = ypos-COMUtil.getPixel(1);
							maxPositions[2] = i;
							maxPositions[3] = 1;
							//2021.09.28 by lyk - kakaopay - 최고/최저 표시를 봉그래프 위에 그리도록 수정 <<
						}
						maxset=true;
						highXPos = xpos;
						highYPos = ypos;
					}
				}
                if(data[i]==getBongMin()){
					if(!minset){
						if(!_cvm.bIsLineChart && !_cvm.bIsLine2Chart || _cvm.bIsHighLowSign) {
							//2021.09.28 by lyk - kakaopay - 최고/최저 표시를 봉그래프 위에 그리도록 수정 >>
//							drawMinMaxString(gl, (int) xpos, (int) ypos+(int)COMUtil.getPixel(1), i, false);
							//x,y,index,boolean(up/down)
							minPositions[0] = xpos;
							minPositions[1] = ypos+COMUtil.getPixel(1);
							minPositions[2] = i;
							minPositions[3] = 0;
							//2021.09.28 by lyk - kakaopay - 최고/최저 표시를 봉그래프 위에 그리도록 수정 <<
						}
						minset=true;
						lowXPos = xpos;
						lowYPos = ypos;
					}
				}

                if(i== dataLen-2)
                {
                    if(data[i+1]==getBongMax()){
                        if(!maxset){
                            if(!_cvm.bIsLineChart && !_cvm.bIsLine2Chart || _cvm.bIsHighLowSign) {
								//2021.09.28 by lyk - kakaopay - 최고/최저 표시를 봉그래프 위에 그리도록 수정 >>
//							drawMinMaxString(gl,(int)(xpos+xfactor),(int)ypos1-(int)COMUtil.getPixel(1),i+1,true);
								//x,y,index,boolean(up/down)
								maxPositions[0] = xpos+xfactor;
								maxPositions[1] = ypos1-COMUtil.getPixel(1);
								maxPositions[2] = i+1;
								maxPositions[3] = 1;
								//2021.09.28 by lyk - kakaopay - 최고/최저 표시를 봉그래프 위에 그리도록 수정 <<
                            }
                            maxset=true;
                            highXPos = xpos+xfactor;
                            highYPos = ypos1;
                        }
                    }
                    if(data[i+1]==getBongMin()){
                        if(!minset){
                            if(!_cvm.bIsLineChart && !_cvm.bIsLine2Chart || _cvm.bIsHighLowSign) {
								//2021.09.28 by lyk - kakaopay - 최고/최저 표시를 봉그래프 위에 그리도록 수정 >>
//							drawMinMaxString(gl, (int)(xpos+xfactor), (int) ypos1+(int)COMUtil.getPixel(1), i+1, false);
								//x,y,index,boolean(up/down)
								minPositions[0] = xpos+xfactor;
								minPositions[1] = ypos1+COMUtil.getPixel(1);
								minPositions[2] = i+1;
								minPositions[3] = 0;
								//2021.09.28 by lyk - kakaopay - 최고/최저 표시를 봉그래프 위에 그리도록 수정 <<
                            }
                            minset=true;
                            lowXPos = xpos+xfactor;
                            lowYPos = ypos1;
                        }
                    }
                }
				xpos+=xfactor;
			}
		}

//		try {
//			if(data[dataLen-1]==getBongMax()) {
//				if (!maxset) {
//					ypos = (int) calcy(data[dataLen - 1]);
//					if (!_cvm.bIsLineChart && !_cvm.bIsLine2Chart || _cvm.bIsHighLowSign) {
//						drawMinMaxString(gl, (int) xpos, (int) ypos, dataLen - 1, true);
//					}
//					highXPos = xpos;
//					highYPos = ypos;
//				}
//			}
//			else if(data[dataLen - 1]==getBongMin()){
//				if(!minset){
//					ypos = (int) calcy(data[dataLen - 1]);
//					if(!_cvm.bIsLineChart && !_cvm.bIsLine2Chart || _cvm.bIsHighLowSign) {
//						drawMinMaxString(gl, (int) xpos, (int) ypos, dataLen - 1, false);
//					}
//					minset=true;
//					lowXPos = xpos;
//					lowYPos = ypos;
//				}
//			}
//		} catch (Exception e) {
//
//		}

		if(nIndex>0)
		{
            if(_cvm.bIsLine2Chart)
                _cvm.setLineWidth(1);
            else
			    _cvm.setLineWidth(2);
//        	_cvm.drawLines(gl, positions, upColor ,1.0f);
//        	_cvm.setLineWidth(1);

			//2012. 11. 20 해외선물 5일 분차트면 gradient 형식으로 라인 아랫쪽을 그린다. : C31
			if(_cvm.bIsLineFillChart || getDrawType2() == 3)
			{
				//_cvm.setLineWidth_Fix(2);
                _cvm.setLineWidth(1);
				int[] colorLine = new int[3];
				colorLine[0] = 138;
				colorLine[1] = 214;
				colorLine[2] = 104;
				_cvm.drawLines(gl, positions, colorLine ,1.0f);

				int[] color0 = new int[3];

				color0[0] = 137;
				color0[1] = 210;
				color0[2] = 105;

				//_cvm.drawLineWithFillGradient(gl, positions, this.max_view + (int)COMUtil.getPixel(8), color0, 127, nIndex);
				_cvm.drawLineWithFillGradient(gl, positions, this.max_view + (int)COMUtil.getPixel(8), color0, 127, nIndex, this.min_view);
			} else if(_cvm.bIsLineChart) {

				//기준선 가로 점선 그리기
				if (!_cvm.bIsHighLowSign) {
					//전일종가 기준선
					String prePrice = _cdm.codeItem.strGijun;
					try {
						ypos = (int)calcy(Double.parseDouble(prePrice));
						ypos = ypos-_cvm.XSCALE_H;
						if(ypos<0) {
							ypos = 0;
						}
					} catch (Exception e) {
						ypos = 0;
					}

					_cvm.setLineWidth_Fix(2);
					//_cvm.drawDashLine(gl, 0,ypos,getBounds().width(),ypos, _cvm.cLineColor, 0.5f);
				}
				//데이터 갯수에 따라 라인 굵기를 정함

//            	int line_thick = 3;
//				float nLineThick = 1.2f;
//				if(!_cvm.m_sLineThick.equals("")) {
//					try {
//						nLineThick = Integer.parseInt(_cvm.m_sLineThick);
//					} catch (Exception e) {
//
//					}
//				} else {
//					if(dataLen>180) {
//						nLineThick = 1;
//					}
//				}
//				_cvm.setLineWidth(nLineThick);
				_cvm.setLineWidth_Fix(COMUtil.getPixel(1));	//2020.05.15 by LYH >> 라인 두께 2로 수정
				if(_cvm.bIsHighLowSign)
					_cvm.drawLines(gl, positions, _cvm.cLineColor ,1.0f);
				else
					_cvm.drawLines(gl, positions, upColor ,1.0f);
			}
			else
			{
				int[] cColor = upColor;
				//시간외 차트 색상 처리
				if(COMUtil.isAfterType) {
					if(COMUtil.isUpAfter==1) {
						cColor = upColor;
					} else if(COMUtil.isUpAfter==0) {
						cColor = downColor;
					} else {
						cColor = sameColor;
					}
				}

				//2019. 04. 08 by hyh - FX마진 데이터 조회 및 실시간 처리 >>
				if (_cvm.nFxMarginType == FX_BUY) {
					cColor = upColor;
				}
				else if (_cvm.nFxMarginType == FX_SELL) {
					cColor = downColor;
				}
				else if (_cvm.nFxMarginType == FX_BUYSELL) {
					cColor = downColor;
				}
				else if (_cvm.nFxMarginType == FX_AVERAGE) {
					cColor = sameColor;
				}
				//2019. 04. 08 by hyh - FX마진 데이터 조회 및 실시간 처리 <<

				_cvm.drawLines(gl, positions, cColor ,1.0f);
				drawBuyAveragePriceLine(gl, (int)xpos); //2021.02.18 by HJW - 매입평균선 추가
			}

			if(_cvm.bIsHighLowSign) {
//				if (lowXPos==-1 && lowYPos==-1 && dataLen>1) {
//					ypos=(int)calcy(data[dataLen-1]);
//					drawMinMaxString(gl,(int)xpos-4,(int)ypos,dataLen-1,false);
//					lowXPos = xpos;
//					lowYPos = ypos;
//				}

				if((int)highXPos>=0 || (int)highYPos>0) {
					_cvm.drawCircle(gl, (int)highXPos-(int)COMUtil.getPixel(2),(int)highYPos-(int)COMUtil.getPixel(2), (int)highXPos+(int)COMUtil.getPixel(2),(int)highYPos+(int)COMUtil.getPixel(2), true, CoSys.UP_LINE_COLORS);
				}
				if((int)lowXPos>=0 || (int)lowYPos>0) {
					_cvm.drawCircle(gl, (int)lowXPos-(int)COMUtil.getPixel(2),(int)lowYPos-(int)COMUtil.getPixel(2), (int)lowXPos+(int)COMUtil.getPixel(2),(int)lowYPos+(int)COMUtil.getPixel(2), true, CoSys.DOWN_LINE_COLORS);
				}
			}
			_cvm.setLineWidth(1);
		}
		//2017.11 by pjm 퇴직연금 애니매이션 차트 >>
		if(_cvm.m_bUseAnimationLine && m_bInit) {

			if(_cvm.m_bWorkingAnimationTimer && dynamicData != null) {
				if(isAnimationEnd)
					endAnimation();
				else
					_cvm.getAnimationLineChartListener().postInvalidateToChart();
			} else {
				startAnimation(gl, data, dStdPrice);
			}
		}
		//2017.11 by pjm 퇴직연금 애니매이션 차트 <<

		//2021.09.28 by lyk - kakaopay - 최고/최저 표시를 봉그래프 위에 그리도록 수정 >>
		if(getBongMax() == getBongMin()) {
			drawMinMaxString(gl, (int) maxPositions[0], (int) maxPositions[1], (int) maxPositions[2], maxPositions[3] == 1 ? true : false);
		} else {
			if (maxset) {
				drawMinMaxString(gl, (int) maxPositions[0], (int) maxPositions[1], (int) maxPositions[2], maxPositions[3] == 1 ? true : false);
			}
			if (minset) {
				drawMinMaxString(gl, (int) minPositions[0], (int) minPositions[1], (int) minPositions[2], minPositions[3] == 1 ? true : false);
			}
		}
		//2021.09.28 by lyk - kakaopay - 최고/최저 표시를 봉그래프 위에 그리도록 수정 <<

		//종가 영역 blink 애니메이션 구현
//		if(blinkLayout == null) {
//			int layoutResId01 = COMUtil._chartMain.getResources().getIdentifier("blinking_animation", "layout", COMUtil._neoChart.getContext().getPackageName());
//			int layoutResId02 = COMUtil.apiView.getContext().getResources().getIdentifier("blink_animation", "anim", COMUtil._neoChart.getContext().getPackageName());
//			int layoutResId03 = COMUtil.apiView.getContext().getResources().getIdentifier("blinking_animation", "id", COMUtil._neoChart.getContext().getPackageName());
//
//			LayoutInflater factory = LayoutInflater.from(COMUtil._neoChart.getContext());
//			blinkLayout = (RelativeLayout) factory.inflate(layoutResId01, null);
//			blinkImg = blinkLayout.findViewById(layoutResId03);
//			Animation startAnimation = AnimationUtils.loadAnimation(COMUtil._neoChart.getContext(), layoutResId02);
//			blinkImg.startAnimation(startAnimation);
//			COMUtil._chartMain.runOnUiThread(new Runnable() {
//				public void run() {
//					COMUtil._neoChart.layout.addView(blinkLayout);
//				}
//			});
//			blinkParams =new RelativeLayout.LayoutParams(
//					blinkImgW, blinkImgH);
//
//		}
//		blinkParams.leftMargin=(int)xpos - blinkImgW/2;
//		blinkParams.topMargin=(int)ypos1 - blinkImgH/2;
//		blinkImg.setLayoutParams(blinkParams);
		//
		drawPivotDemarkLine(gl); //2023.05.24 by SJW - 라인차트에서 지지선/저항선 그리게끔 수정
	}

	public void drawStair(Canvas gl, double[] data){
		if(data==null||data.length<1)return;
		//2013.07.31 >> 기준선 라인 차트 타입 추가
		if (_cdm.codeItem.strStandardPrice != null && _cdm.codeItem.strStandardPrice.length() > 0 && getDrawType2() != 3)
		{
			//2015.01.08 by LYH >> 3일차트 추가
			String[] arrStandard = _cdm.codeItem.strStandardPrice.split("\\|");
			if( arrStandard.length > 2 )
			{
				draw(gl, data, arrStandard[0]);
			}
			else
				//2015.01.08 by LYH << 3일차트 추가
				draw(gl, data, _cdm.codeItem.strStandardPrice);
			return;
		}
		//2013.07.31 <<
		this.data = data;
		float xpos=getBounds().left+xw;
		float ypos=0;
		float ypos1=0;
		minset=maxset=false;
//        int x=0, y=0;
		float highXPos = -1;
		float highYPos = -1;
		float lowXPos = -1;
		float lowYPos = -1;

		double yInc;
		line_thick=2;
		int thick =line_thick, temp;
		int startIndex = _cvm.getIndex();
		int dataLen = startIndex + _cvm.getViewNum();
		if(dataLen>data.length)
			dataLen= data.length;
//		int totLen = (dataLen-startIndex-1)*4;
//		if(dataLen==1) {
//			totLen = 4;
//		}
//		float[] positions = new float[totLen];// = new float[dataLen];
		int nIndex = 0;
		if(dataLen==1) {
			ypos=(int)calcy(data[0]);
//			positions[0] = xpos;
//			positions[1] = ypos;
//			positions[2] = xpos+line_thick;
//			positions[3] = ypos;

			nIndex=3;
			_cvm.drawLine(gl, (int)xpos,ypos,(int)(xpos+xfactor),(int)ypos, upColor ,1.0f);
		} else {
			int[] lineColor = upColor;
			for(int i=startIndex; i<dataLen-1; i++){
				ypos=(int)calcy(data[i]);

				ypos1 = (int)calcy(data[i+1]);
				if((ypos<=max_view&&ypos1<=max_view)){
					yInc = (double)(((double)ypos1-(double)ypos)/(double)(xfactor*2));
					temp = (yInc<0)?Math.abs((int)(-1*(yInc-0.99))):(int)(yInc+0.99);
					thick=(temp>thick)?temp:line_thick;

//					positions[nIndex++] = xpos;
//					positions[nIndex++] = ypos;
//					positions[nIndex++] = xpos+xfactor;
//					positions[nIndex++] = ypos1;
					if(ypos>ypos1){
						_cvm.drawLine(gl, xpos,ypos,xpos+xfactor,ypos, lineColor ,1.0f);
						_cvm.drawLine(gl, xpos+xfactor,ypos,xpos+xfactor,ypos1, lineColor ,1.0f);
						lineColor = upColor;
					} else if(ypos<ypos1) {
						_cvm.drawLine(gl, xpos,ypos,xpos+xfactor,ypos, lineColor ,1.0f);
						_cvm.drawLine(gl, xpos+xfactor,ypos,xpos+xfactor,ypos1, lineColor ,1.0f);
						lineColor = downColor;
					}
					else{
						_cvm.drawLine(gl, xpos,ypos,xpos+xfactor,ypos, lineColor ,1.0f);
						_cvm.drawLine(gl, xpos+xfactor,ypos,xpos+xfactor,ypos1, lineColor ,1.0f);
					}

//					if(isSelected()){
//						if(i%5==0){
//							_cvm.drawRect(gl, (int)xpos,(int)ypos,5,5, CoSys.UP_LINE_COLORS);
//						}
//					}
				}
				if(data[i]==getBongMax()){
					if(!maxset){
						if(!_cvm.bIsLineChart && !_cvm.bIsLine2Chart || _cvm.bIsHighLowSign) {
							drawMinMaxString(gl,(int)xpos,(int)ypos-(int)COMUtil.getPixel(1),i,true);
						}
						maxset=true;
						highXPos = xpos;
						highYPos = ypos;
					}
				}
				if(data[i]==getBongMin()){
					if(!minset){
						if(!_cvm.bIsLineChart && !_cvm.bIsLine2Chart || _cvm.bIsHighLowSign) {
							drawMinMaxString(gl, (int) xpos, (int) ypos+(int)COMUtil.getPixel(1), i, false);
						}
						minset=true;
						lowXPos = xpos;
						lowYPos = ypos;
					}
				}

				if(i== dataLen-2)
				{
					if(data[i+1]==getBongMax()){
						if(!maxset){
							if(!_cvm.bIsLineChart && !_cvm.bIsLine2Chart || _cvm.bIsHighLowSign) {
								drawMinMaxString(gl,(int)(xpos+xfactor),(int)ypos1-(int)COMUtil.getPixel(1),i+1,true);
							}
							maxset=true;
							highXPos = xpos+xfactor;
							highYPos = ypos1;
						}
					}
					if(data[i+1]==getBongMin()){
						if(!minset){
							if(!_cvm.bIsLineChart && !_cvm.bIsLine2Chart || _cvm.bIsHighLowSign) {
								drawMinMaxString(gl, (int)(xpos+xfactor), (int) ypos1+(int)COMUtil.getPixel(1), i+1, false);
							}
							minset=true;
							lowXPos = xpos+xfactor;
							lowYPos = ypos1;
						}
					}
				}
				xpos+=xfactor;
			}
		}

		//2019. 11. 26 by hyh - 라인차트에 기준선 그리기 >>
		drawBaseLine(gl);
		drawPivotDemarkLine(gl);
		//2019. 11. 26 by hyh - 라인차트에 기준선 그리기 <<

//		try {
//			if(data[dataLen-1]==getBongMax()) {
//				if (!maxset) {
//					ypos = (int) calcy(data[dataLen - 1]);
//					if (!_cvm.bIsLineChart && !_cvm.bIsLine2Chart || _cvm.bIsHighLowSign) {
//						drawMinMaxString(gl, (int) xpos, (int) ypos, dataLen - 1, true);
//					}
//					highXPos = xpos;
//					highYPos = ypos;
//				}
//			}
//			else if(data[dataLen - 1]==getBongMin()){
//				if(!minset){
//					ypos = (int) calcy(data[dataLen - 1]);
//					if(!_cvm.bIsLineChart && !_cvm.bIsLine2Chart || _cvm.bIsHighLowSign) {
//						drawMinMaxString(gl, (int) xpos, (int) ypos, dataLen - 1, false);
//					}
//					minset=true;
//					lowXPos = xpos;
//					lowYPos = ypos;
//				}
//			}
//		} catch (Exception e) {
//
//		}
	}
	//2013.07.31 >> 기준선 라인 차트 타입 추가 
	public void draw(Canvas gl, double[] data, String stdPrice){
		if(data==null||data.length<1)return;

		if(_cvm.bIsLine2Chart)
		{
			_cvm.setLineWidth(2);
		}
		if(_cvm.bIsNewsChart)
		{
			stdPrice = String.format("%.0f", (max_data+min_data)/2);
		}

		double dStdPrice = 0;
		try
		{
			dStdPrice = Double.parseDouble(stdPrice);
		}
		catch(Exception e)
		{
		}
		float yBasePos=(int)calcy(dStdPrice);
		this.data = data;
		float xpos=getBounds().left+xw;
		float ypos=0;
		float ypos1=0;
		minset=maxset=false;
//        int x=0, y=0;
		double yInc;
		line_thick=2;
		int thick =line_thick, temp;
		int startIndex = _cvm.getIndex();
		int dataLen = startIndex + _cvm.getViewNum();
		if(dataLen>data.length)
			dataLen= data.length;

		int totLen = (dataLen-startIndex-1)*4;
		if(dataLen==1) {
			totLen = 4;
		}
		float[] positionsUp = new float[totLen];// = new float[dataLen];
		float[] positionsDown = new float[totLen];// = new float[dataLen];
		int nIndexUp = 0, nIndexDown = 0;

		int nType = 0;

		if(_cdm.codeItem.strPrice != null && _cdm.codeItem.strPrice.length()>0)
		{
			if(Double.parseDouble(_cdm.codeItem.strPrice)>dStdPrice)
				nType = 1;
			else if(Double.parseDouble(_cdm.codeItem.strPrice)<dStdPrice)
				nType = 2;
		}
		if(_cvm.bIsLineChart && max_data<=0)
		{
			return;
		}
		float fMinX = -1, fMinY = -1, fMinIndex = -1, fMaxX = -1,  fMaxY = -1, fMaxIndex = -1;
		float fLastXPos = xpos;
		if(dataLen==1) {
			ypos=(int)calcy(data[0]);
			_cvm.drawLine(gl, (int)xpos,(int)ypos,(int)(xpos+line_thick),(int)ypos, CoSys.UP_LINE_COLORS ,1.0f);
		} else {
			//if(!_cvm.bIsLineFillChart && !_cvm.bIsLineChart)	//2020.04.24 by LYH >> 라인차트 기준선 구분 그리도록 수정
			if(!_cvm.bIsLineFillChart)
			{
				if((yBasePos>=min_view&&yBasePos<=max_view)){
					//_cvm.drawLine(gl, (int)getBounds().left,(int)yBasePos,(int)getBounds().right,(int)yBasePos, CoSys.STANDARD ,1.0f);
//					_cvm.drawDashLine(gl, (int)getBounds().left,(int)yBasePos,(int)getBounds().right,(int)yBasePos, CoSys.STANDARD ,1.0f);
					_cvm.drawDashLine_interval(gl, (int)getBounds().left,(int)yBasePos,(int)getBounds().right,(int)yBasePos, CoSys.STANDARD ,1.0f,COMUtil.getPixel(1), COMUtil.getPixel(2));
				}
			}
			//2014.05.09 by LYH >> 마운틴차트 최고값, 최저값 표시.
			double[] highData = _cdm.getSubPacketData("고가");
			double[] lowData = _cdm.getSubPacketData("저가");
			//2014.05.09 by LYH << 마운틴차트 최고값, 최저값 표시.
			if (_cvm.bIsLineFillChart) {
				highData = _cdm.getSubPacketData("종가");
				lowData = _cdm.getSubPacketData("종가");
			}
			for(int i=startIndex; i<dataLen-1; i++){
				ypos=(int)calcy(data[i]);

				ypos1 = (int)calcy(data[i+1]);
				if((ypos<=max_view&&ypos1<=max_view)){
					yInc = (double)(((double)ypos1-(double)ypos)/(double)(xfactor*2));
					temp = (yInc<0)?Math.abs((int)(-1*(yInc-0.99))):(int)(yInc+0.99);
					thick=(temp>thick)?temp:line_thick;

					double xpos1 = xpos+xfactor;
					double f = (double)((ypos1-ypos)/(xpos-xpos1));
					double xMid = ((double)(yBasePos - ypos)/f);
					if(xMid>0)
						xMid =(xpos+xMid);
					else
						xMid = (xpos-xMid);

					if(ypos<=yBasePos && ypos1<=yBasePos){
						if(_cvm.bIsLineFillChart || _cvm.bIsLineChart)
						{
							positionsUp[nIndexUp++] = xpos;
							positionsUp[nIndexUp++] = ypos;
							positionsUp[nIndexUp++] = xpos+xfactor;
							positionsUp[nIndexUp++] = ypos1;
						}
						else
							_cvm.drawLine(gl, (int)xpos,(int)ypos,(int)(xpos+xfactor),(int)ypos1, CoSys.UP_LINE_COLORS ,1.0f);
					} else if(ypos>yBasePos && ypos1>yBasePos) {
						if(_cvm.bIsLineFillChart || _cvm.bIsLineChart)
						{
							positionsDown[nIndexDown++] = xpos;
							positionsDown[nIndexDown++] = ypos;
							positionsDown[nIndexDown++] = xpos+xfactor;
							positionsDown[nIndexDown++] = ypos1;
						}
						else
							_cvm.drawLine(gl, (int)xpos,(int)ypos,(int)(xpos+xfactor),(int)ypos1, CoSys.DOWN_LINE_COLORS ,1.0f);
					} else if(ypos>yBasePos && ypos1<=yBasePos){
						if(_cvm.bIsLineFillChart || _cvm.bIsLineChart)
						{
							positionsDown[nIndexDown++] = xpos;
							positionsDown[nIndexDown++] = ypos;
							positionsDown[nIndexDown++] = (int)xMid;
							positionsDown[nIndexDown++] = yBasePos;

							positionsUp[nIndexUp++] = (int)xMid;
							positionsUp[nIndexUp++] = yBasePos;
							positionsUp[nIndexUp++] = xpos+xfactor;
							positionsUp[nIndexUp++] = ypos1;
						}
						else
						{
							_cvm.drawLine(gl, (int)xpos,(int)ypos,(int)xMid,(int)yBasePos, CoSys.DOWN_LINE_COLORS ,1.0f);
							_cvm.drawLine(gl, (int)xMid,(int)yBasePos,(int)(xpos+xfactor),(int)ypos1, CoSys.UP_LINE_COLORS ,1.0f);
						}
					} else if(ypos<=yBasePos && ypos1>yBasePos){
						if(_cvm.bIsLineFillChart || _cvm.bIsLineChart)
						{
							positionsUp[nIndexUp++] = (int)xpos;
							positionsUp[nIndexUp++] = ypos;
							positionsUp[nIndexUp++] = (int)xMid;
							positionsUp[nIndexUp++] = yBasePos;

							positionsDown[nIndexDown++] = (int)xMid;
							positionsDown[nIndexDown++] = yBasePos;
							positionsDown[nIndexDown++] = xpos+xfactor;
							positionsDown[nIndexDown++] = ypos1;
						}
						else
						{
							_cvm.drawLine(gl, (int)xpos,(int)ypos,(int)xMid,(int)yBasePos, CoSys.UP_LINE_COLORS ,1.0f);
							_cvm.drawLine(gl, (int)xMid,(int)yBasePos,(int)(xpos+xfactor),(int)ypos1, CoSys.DOWN_LINE_COLORS ,1.0f);
						}
					}
					else{
						_cvm.drawLine(gl, (int)xpos,(int)ypos,(int)(xpos+xfactor),(int)ypos1, CoSys.LAST_VALUE_SAME_BG ,1.0f);
					}

					if(isSelected()){
						if(i%5==0){
							_cvm.drawRect(gl, (int)xpos,(int)ypos,5,5, CoSys.UP_LINE_COLORS);
						}
					}
					fLastXPos = xpos+xfactor;
				}
				if(highData[i]==getBongMax()){	//2014.05.09 by LYH >> 마운틴차트 최고값, 최저값 표시.
					if(!maxset){
						ypos=(int)calcy(highData[i]);	//2014.05.09 by LYH >> 마운틴차트 최고값, 최저값 표시.
						//drawMinMaxString(gl,(int)xpos,(int)ypos,i,true);
						fMaxX = xpos;
						fMaxY = ypos;
						fMaxIndex = i;
						maxset=true;
					}
				}
                if(lowData[i]==getBongMin()){	//2014.05.09 by LYH >> 마운틴차트 최고값, 최저값 표시.
					if(!minset){
						ypos=(int)calcy(lowData[i]);	//2014.05.09 by LYH >> 마운틴차트 최고값, 최저값 표시.
						//drawMinMaxString(gl,(int)xpos,(int)ypos,i,false);
						fMinX = xpos;
						fMinY = ypos;
						fMinIndex = i;
						minset=true;
					}
				}
				if(i == dataLen - 2) {
					if(highData[i+1]==getBongMax()){	//2014.05.09 by LYH >> 마운틴차트 최고값, 최저값 표시.
						if(!maxset){
							ypos=(int)calcy(highData[i+1]);	//2014.05.09 by LYH >> 마운틴차트 최고값, 최저값 표시.
							//drawMinMaxString(gl,(int)xpos,(int)ypos,i,true);
							fMaxX = xpos + xfactor;
							fMaxY = ypos;
							fMaxIndex = i+1;
							maxset=true;
						}
					}
					if(lowData[i+1]==getBongMin()){	//2014.05.09 by LYH >> 마운틴차트 최고값, 최저값 표시.
						if(!minset){
							ypos=(int)calcy(lowData[i+1]);	//2014.05.09 by LYH >> 마운틴차트 최고값, 최저값 표시.
							//drawMinMaxString(gl,(int)xpos,(int)ypos,i,false);
							fMinX = xpos + xfactor;
							fMinY = ypos;
							fMinIndex = i+1;
							minset=true;
						}
					}
				}
				xpos+=xfactor;
			}
			_cvm.setLineWidth(1);
		}

		//2012. 11. 20 해외선물 5일 분차트면 gradient 형식으로 라인 아랫쪽을 그린다. : C31
		/*if(_cvm.bIsLineChart)
		{
			_cvm.setLineWidth_Fix(2);
			int[] colorLine = new int[3];
			int[] color0 = new int[3];

			if(yBasePos>max_view)
			{
				yBasePos = max_view;
			}
			if(yBasePos<min_view)
			{
				yBasePos = min_view;
			}
			if(_cvm.bUseCurrentColor)
			{
				if(nType == 1)
				{
					color0[0] = 246;
					color0[1] = 158;
					color0[2] = 158;
					colorLine[0] = 255;
					colorLine[1] = 255;
					colorLine[2] = 255;
				}
				else if(nType == 2)
				{
					color0[0] = 123;
					color0[1] = 172;
					color0[2] = 229;
					colorLine[0] = 255;
					colorLine[1] = 255;
					colorLine[2] = 255;
				}
				else if(nType == 0)
				{
					color0[0] = 193;
					color0[1] = 196;
					color0[2] = 203;
					colorLine[0] = 193;
					colorLine[1] = 195;
					colorLine[2] = 203;
				}
			}
			else
			{
				color0[0] = 126;
				color0[1] = 139;
				color0[2] = 173;
				colorLine[0] = 255;
				colorLine[1] = 255;
				colorLine[2] = 255;
			}
			_cvm.setLineWidth_Fix(2);
			if(nIndexUp>0)
			{
				//_cvm.drawLineWithFillGradient(gl, positionsUp, yBasePos , color0, (int)(255*0.2), nIndexUp, 1);
				if(nIndexDown>0)
					_cvm.drawLineWithFillGradient(gl, positionsUp, yBasePos , color0, (int)(255*0.3), nIndexUp, 0);
				else {
					if(this.min_view==0)
						_cvm.drawLineWithFillGradient(gl, positionsUp, this.max_view + (int) COMUtil.getPixel(8), color0, (int) (255 * 0.3), nIndexUp, 1);
					else
						_cvm.drawLineWithFillGradient(gl, positionsUp, this.max_view + (int) COMUtil.getPixel(8), color0, (int) (255 * 0.3), nIndexUp, this.min_view);
				}

				_cvm.drawLines(gl, positionsUp, colorLine ,0.3f);
			}
			if(nIndexDown>0)
			{
				if(nIndexUp>0)
					_cvm.drawLineWithFillGradient(gl, positionsDown, yBasePos , color0, (int)(255*0.3), nIndexDown,0);
				else
					_cvm.drawLineWithFillGradient(gl, positionsDown, yBasePos , color0, (int)(255*0.3), nIndexDown,-1* this.max_view);
				_cvm.drawLines(gl, positionsDown, colorLine ,0.3f);
			}
		}
		else*/ 
		if(_cvm.bIsLineFillChart || _cvm.bIsLineChart)
		{
//			_cvm.setLineWidth_Fix(1);
            //_cvm.setLineWidth(2);
//			if(_cvm.bIsOneQStockChart)
//				_cvm.setLineWidth_Fix(COMUtil.getPixel(1.5f));	//2020.05.15 by LYH 라인두께 2로 수정
//			else
//				_cvm.setLineWidth_Fix(COMUtil.getPixel(2));	//2020.05.15 by LYH 라인두께 2로 수정
			_cvm.setLineWidth_Fix(COMUtil.getPixel(1));	//2020.05.15 by LYH 라인두께 2로 수정
			int[] colorLine = new int[3];
			int[] color0 = new int[4];

			if(yBasePos>max_view)
			{
				yBasePos = max_view;
			}
			if(yBasePos<min_view)
			{
				yBasePos = min_view;
			}
			if(nIndexUp>0)
			{
				float[] positionsUpTemp = new float[nIndexUp];
				for (int i = 0; i < nIndexUp; i++) {
					positionsUpTemp[i] = positionsUp[i];
				}
//				colorLine[0] = 218;
//				colorLine[1] = 63;
//				colorLine[2] = 66;
//
//				color0[0] = 253;
//				color0[1] = 240;
//				color0[2] = 241;
				if(_cvm.bIsLineFillChart) {
					if(_cvm.getSkinType() == COMUtil.SKIN_BLACK) {
						colorLine[0] = 48;
						colorLine[1] = 10;
						colorLine[2] = 0;

						color0[0] = 243;
						color0[1] = 164;
						color0[2] = 167;
					}
					else
					{
	//					colorLine[0] = 197;
	//					colorLine[1] = 70;
	//					colorLine[2] = 70;
						colorLine[0] = 214;
						colorLine[1] = 55;
						colorLine[2] = 65;

	//					color0[0] = 255;
	//					color0[1] = 224;
	//					color0[2] = 224;
//						color0[0] = 255;
//						color0[1] = 235;
//						color0[2] = 235;
						color0[0] = upColor[0];
						color0[1] = upColor[1];
						color0[2] = upColor[2];
						color0[3] = 38;
					}

					//2014.03.31 by LYH >> 마운틴차트 그리기 개선(그라데이션).
					//_cvm.drawLineWithFillGradient(gl, positionsUp, yBasePos , color0, 200, nIndexUp);
					if (nIndexDown > 0)
						//_cvm.drawLineWithFillGradient(gl, positionsUp, yBasePos, color0, 150, nIndexUp, this.min_view);
						_cvm.drawLineWithFillGradient(gl, positionsUp, yBasePos, color0, 255, nIndexUp, this.min_view);
					else
						//_cvm.drawLineWithFillGradient(gl, positionsUp, this.max_view + _cvm.BMargin_B, color0, 150, nIndexUp, this.min_view);
						_cvm.drawLineWithFillGradient(gl, positionsUp, this.max_view + _cvm.BMargin_B, color0, 255, nIndexUp, this.min_view);
					//2014.03.31 by LYH << 마운틴차트 그리기 개선(그라데이션).
					_cvm.drawLines(gl, positionsUpTemp, upColor ,1.0f);
				}
				else
				{
					_cvm.drawLines(gl, positionsUpTemp, upColor ,1.0f);
				}

			}
			if(nIndexDown>0)
			{
				float[] positionsDownTemp = new float[nIndexDown];
				for (int i = 0; i < nIndexDown; i++) {
					positionsDownTemp[i] = positionsDown[i];
				}
				if(_cvm.bIsLineFillChart) {
					//				colorLine[0] = 24;
					//				colorLine[1] = 112;
					//				colorLine[2] = 213;
					//
					//				color0[0] = 239;
					//				color0[1] = 244;
					//				color0[2] = 254;
					if (_cvm.getSkinType() == COMUtil.SKIN_BLACK) {
						colorLine[0] = 7;
						colorLine[1] = 46;
						colorLine[2] = 64;

						color0[0] = 68;
						color0[1] = 111;
						color0[2] = 191;
					} else {
						//					colorLine[0] = 108;
						//					colorLine[1] = 149;
						//					colorLine[2] = 248;
						colorLine[0] = 99;
						colorLine[1] = 142;
						colorLine[2] = 245;

						//					color0[0] = 224;
						//					color0[1] = 238;
						//					color0[2] = 255;
//						color0[0] = 235;
//						color0[1] = 244;
//						color0[2] = 255;
						color0[0] = downColor[0];
						color0[1] = downColor[1];
						color0[2] = downColor[2];
						color0[3] = 38;
					}

					//2014.03.31 by LYH >> 마운틴차트 그리기 개선(그라데이션).
					//_cvm.drawLineWithFillGradient(gl, positionsDown, yBasePos , color0, 200, nIndexDown);
					//_cvm.drawLineWithFillGradient(gl, positionsDown, yBasePos, color0, 150, nIndexDown, -1 * this.max_view);
					_cvm.drawLineWithFillGradient(gl, positionsDown, yBasePos, color0, 255, nIndexDown, -1 * this.max_view);
					//2014.03.31 by LYH << 마운틴차트 그리기 개선(그라데이션).

					_cvm.drawLines(gl, positionsDownTemp, downColor, 1.0f);
				}
				else
				{
					_cvm.drawLines(gl, positionsDownTemp, downColor ,1.0f);
				}


			}
		}
		if(_cvm.bIsLineFillChart)
		{
			drawPriceLine(gl, (int)xpos, stdPrice, fLastXPos);	//2020.02.09 by LYH >> 현재가 라인 표시
		}
		if((yBasePos>=min_view&&yBasePos<=max_view) && !_cvm.bIsLineChart){
			drawPriceLine(gl, (int)xpos, stdPrice, -1);
//			if((yBasePos+(int)COMUtil.getPixel(7)>=max_view) && !(_cvm.bIsOneQStockChart && _cvm.bIsLineFillChart)) {
//				_cvm.drawString(gl, CoSys.STANDARD, (int) getBounds().left, yBasePos - (int) COMUtil.getPixel(7), ChartUtil.getFormatedData(stdPrice, _cdm.getPriceFormat(), _cdm));
//			}
//			else {
//				if (_cvm.bIsOneQStockChart && _cvm.bIsLineFillChart) {
//					int[] nLeftLineCol = {158, 158, 158};
//					_cvm.setLineWidth(1);
//					_cvm.drawDashLine_interval(gl, getBounds().left, yBasePos, getBounds().left + getBounds().right, yBasePos, nLeftLineCol, 0.5f, COMUtil.getPixel(1), COMUtil.getPixel(4));
//				} else {
//					_cvm.drawString(gl, CoSys.STANDARD, (int) getBounds().left, yBasePos + (int) COMUtil.getPixel(7), ChartUtil.getFormatedData(stdPrice, _cdm.getPriceFormat(), _cdm));
//				}
//			}
		}

		if(fMaxX>=0) {
			drawMinMaxString(gl,(int)fMaxX,(int)fMaxY,(int)fMaxIndex,true);
		}
		if(fMinX>=0) {
			drawMinMaxString(gl,(int)fMinX,(int)fMinY,(int)fMinIndex,false);
		}

		_cvm.setLineWidth(2);

		//2019. 11. 26 by hyh - 라인차트에 기준선 그리기 >>
		drawBaseLine(gl);
		drawPivotDemarkLine(gl);
		//2019. 11. 26 by hyh - 라인차트에 기준선 그리기 <<
	}
	//2013.07.31 <<
	float[] yp = new float[4];
	public void draw(Canvas gl, double[][] data, double[] stand){
		if(data==null||data.length<1)return;
		float x = getBounds().left;
		float w = (getBounds().right%2==0)?(getBounds().right-1):getBounds().right;
		float xw = (w-1)/2;
		float y = calcy(stand[2]);
		_cvm.drawLine(gl, x,y,x+w,y, CoSys.GRAY ,1.0f);

		for(int i=0;i<yp.length;i++){
			yp[i] = calcy(data[0][i]);
		}
		if(data[0][0]>data[0][3]){//시가>종가
			_cvm.drawLine(gl, x+xw,yp[1],x+xw,yp[2],downColor ,1.0f);
			//gradationDraw(g,Color.white,downColor,x,yp[0],w-xw,yp[3]-yp[0],false);
		}else if(data[0][0]<data[0][3]){
			_cvm.drawLine(gl, x+xw,yp[1],x+xw,yp[2],upColor ,1.0f);
			//gradationDraw(g,Color.white,upColor,x,yp[3],w-xw,yp[0]-yp[3],false);
		}else{
			_cvm.drawLine(gl, x+xw,yp[1],x+xw,yp[2],sameColor ,1.0f);
			_cvm.drawLine(gl, x,yp[0],x+w,yp[0],sameColor ,1.0f);
		}
	}
	public void draw(Canvas gl, double[][] data){
//        if(data==null||data.length<1)return;
		bongData = data;
		if(_cvm.isShowingRightState()){
			rights=_cdm.getStringData("락구분",_cvm.getIndex(),_cvm.getViewNum());
		}
		switch(getDrawType2()){
			case 0:
			//2020.07.06 by LYH >> 캔들볼륨 >>
			case 6:	//Candle Volume
			case 7:	//Equi Volume
			//2020.07.06 by LYH >> 캔들볼륨 <<
				//if(isFillUp()){
				//   if(isFillDown()){
				drawJBong(gl,data);
				//    }else{
				// drawJBong_Upfill(g,data);
				//    }
				//}else {
				//drawJBong_Dnfill(g,data);
				//}
				break;
			case 1:
            case 2:
				drawABong(gl,data);
                break;
            case 3://영역라인
            {
                double[] price = _cdm.getSubPacketData("종가");
                if(price==null) return;
                draw(gl, price);
            }
				break;
			case 4://플로우형
				drawFlow(gl, data);
				break;
			case 5:
				drawHeikinAshiBong(gl,data);
				break;
			case 8://계단형
			{
				double[] price = _cdm.getSubPacketData("종가");
				if(price==null) return;
				drawStair(gl, price);
			}
			break;
		}
	}

	String[] rights_title={
			"",
			"권리락",
			"배당락",
			"권배락",
			"액면분할",
			"액면병합",
			"신규",
			"신주"
	};

	public void drawJBong(Canvas gl, double[][] data){

		//2013.02.12 by LYH >> 블랙에서 보합색 변경
		if(_cvm.getSkinType() == COMUtil.SKIN_BLACK) {
			//2023.06.02 by SJW - 다크테마 차트 색상 변경 >>
			this.setUpColor(CoSys.CHART_COLOR_UP_NIGHT);
			this.setUpColor2(CoSys.CHART_COLOR_UP_NIGHT);
			this.setDownColor(CoSys.CHART_COLOR_DOWN_NIGHT);
			this.setDownColor2(CoSys.CHART_COLOR_DOWN_NIGHT);
			//2023.06.02 by SJW - 다크테마 차트 색상 변경 <<
			this.setSameColor(CoSys.CHART_COLORS[7]);
		}
		else
		{
			this.setSameColor(CoSys.CHART_COLORS[2]);
		}
		//2013.02.12 by LYH << 블랙에서 보합색 변경

		float x=getBounds().left+xw;
		minset=maxset=false;
		int startIndex = _cvm.getIndex();
		//20120621 by LYH >> 일목균형 스크롤 처리
		//int dataLen = startIndex + _cvm.getViewNum();
		int dataLen = startIndex + _cvm.getViewNum() + _cvm.futureMargin;
		int nTotCnt = _cdm.getCount();
		if(dataLen>nTotCnt)
			dataLen = nTotCnt;
		//20120621 by LYH <<

		double[] openData = _cdm.getSubPacketData("시가");
		double[] highData = _cdm.getSubPacketData("고가");
		double[] lowData = _cdm.getSubPacketData("저가");
		double[] closeData = _cdm.getSubPacketData("종가");

		//2020.11.23 by JJH >> 권리락/배당락 추가 start
		double[] exRightTypeData = _cdm.getSubPacketData("락구분");
		double[] exRightRateData = _cdm.getSubPacketData("락비율");
		//2020.11.23 by JJH >> 권리락/배당락 추가 end

		if(closeData==null) return;
		if(dataLen>closeData.length)
			dataLen= closeData.length;
		float openY, highY, lowY, closeY;
		if(startIndex<0)
			startIndex = 0;
		int i=startIndex;

		int totLen = (dataLen-startIndex)*4;
		if(totLen <=0)
			return;
		float[] linePositions_same = new float[totLen*2];
		int nLineIndex_same = 0;

		float[] rectPositions_up = new float[totLen*6];
		float[] rectPositions_down = new float[totLen*6];
		float[] rectPositions_up2 = new float[totLen*6];
		float[] rectPositions_down2 = new float[totLen*6];

		//2021.09.28 by lyk - kakaopay - 최고/최저 표시를 봉그래프 위에 그리도록 수정 >>
		float[] maxPositions = new float[4]; //x,y,index,boolean(up/down)
		float[] minPositions = new float[4]; //x,y,index,boolean(up/down)
		//2021.09.28 by lyk - kakaopay - 최고/최저 표시를 봉그래프 위에 그리도록 수정 <<

		int nRectIndex_up = 0;
		int nRectIndex_down = 0;
		int nRectIndex_up2 = 0;
		int nRectIndex_down2 = 0;

		float fWidth = (xw*2);
		if(fWidth%2==0 && fWidth > 2) {
			//2020. 06. 08 by hyh - 우측으로 쏠린 머리, 꼬리 위치 가운데로 조정. 캔들, 캔들볼륨 >>
			fWidth -= COMUtil.getPixel(1);
			//nWidth -=1;
			//2020. 06. 08 by hyh - 우측으로 쏠린 머리, 꼬리 위치 가운데로 조정. 캔들, 캔들볼륨 <<
		}
		boolean bUp = false;
		int bSameUp = 1;  //0 : 상승   1 : 하락  2 : 보합
		float fLineWidth = 1;

		//2017.05.30 by LYH >> 보합 HTS와 색상 같게 수정
		boolean bSame = false;
		boolean bPreUp = false;
		//2017.05.30 by LYH >> 보합 HTS와 색상 같게 수정 <<
		//if(COMUtil.getPixel(1)>=3)
		if(COMUtil.getPixel(1)>2)	//2019.12.05 by LYH >> 고해상도 라이 두께 조정
		{
			fLineWidth = 2;
		}

//		if(iWidth>COMUtil.getPixel_W(5))
//		{
//			//fWidth = 1.0;
//			if(iWidth>COMUtil.getPixel_W(8))
//				nWidth = (int)Math.round((xw*2)-COMUtil.getPixel_W(3));
//			else
//				nWidth = (int)Math.round((xw*2)-COMUtil.getPixel_W(2));
//
//			if((int)nWidth%2==0 && nWidth > 2)
//				nWidth -=1;
//		}
		//2024.01.03 by LYH - 일목균형표 줌인, 줌아웃, 스크롤 개선 >>
		dataLen = startIndex + _cvm.getViewNum();
		if(dataLen>closeData.length)
			dataLen= closeData.length;
		//2024.01.03 by LYH - 일목균형표 줌인, 줌아웃, 스크롤 개선 <<
		for(;i<dataLen;i++){
			//2019. 06. 25 by hyh - 한궁렬 차장님 요청 : 일단은 시고저종 잘못 된 경우 종가의 납작한 봉으로 처리 >>
			if (lowData[i] > openData[i] || highData[i] < openData[i]) {
				openY = calcy(closeData[i]);
				highY = calcy(closeData[i]);
				lowY = calcy(closeData[i]);
				closeY = calcy(closeData[i]);
			}
			//2019. 06. 25 by hyh - 한궁렬 차장님 요청 : 일단은 시고저종 잘못 된 경우 종가의 납작한 봉으로 처리 >>
			//2019. 06. 26 by hyh - 최철순 과장님 요청 : 시고저종 잘못 된 경우 종가의 납작한 봉으로 처리 >>
			else if (lowData[i] > closeData[i] || highData[i] < closeData[i]) {
				openY = calcy(closeData[i]);
				highY = calcy(closeData[i]);
				lowY = calcy(closeData[i]);
				closeY = calcy(closeData[i]);
			}
			//2019. 06. 26 by hyh - 최철순 과장님 요청 : 시고저종 잘못 된 경우 종가의 납작한 봉으로 처리 <<
			else {
				openY = calcy(openData[i]);
				highY = calcy(highData[i]);
				lowY = calcy(lowData[i]);
				closeY = calcy(closeData[i]);

				//2020.07.06 by LYH >> 캔들볼륨 >>
				if(getDrawType2() == 7)    //Equi Volume
				{
					if (closeData[i] >= openData[i]) {
						openY = calcy(lowData[i]);
						closeY = calcy(highData[i]);
					} else {
						openY = calcy(highData[i]);
						closeY = calcy(lowData[i]);
					}
				}
				//2020.07.06 by LYH >> 캔들볼륨 <<
			}

			//2013.02.12 by LYH >> 종가값 0인경우 영역 침범하는 문제 해결.
			if((int)closeY>(int)max_view)
			{
				closeY = max_view;
			}
			if((int)openY>(int)max_view)
			{
				openY = max_view;
			}
			if((int)highY>(int)max_view)
			{
				highY = max_view;
			}
			if((int)lowY>(int)max_view)
			{
				lowY = max_view;
			}
			//2013.02.12 by LYH << 종가값 0인경우 영역 침범하는 문제 해결.

			//2020. 06. 08 by hyh - 우측으로 쏠린 머리, 꼬리 위치 가운데로 조정. 캔들, 캔들볼륨 >>
			float fStart = (x-xw);
			//float fStart = (x-xw-1);
			//2020. 06. 08 by hyh - 우측으로 쏠린 머리, 꼬리 위치 가운데로 조정. 캔들, 캔들볼륨 <<
			float fMid = (fStart + fWidth/2);
			float fEnd = (fStart + fWidth);

			//2020.07.06 by LYH >> 캔들볼륨 >>
			AREA area = _cvm.getArea(i-startIndex);
			if(area!=null)
			{
				fStart = area.getLeft();
				fMid = area.getCenter();
				fEnd = area.getRight();
			}
			//2020.07.06 by LYH >> 캔들볼륨 <<
//			if(iWidth>COMUtil.getPixel_W(5))
//			{
//				if(iWidth>COMUtil.getPixel_W(8))
//					nStart = (int)Math.round(x-(float)nWidth/2.0f-COMUtil.getPixel_W(1.5f));
//				else
//					nStart = (int)Math.round(x-(float)nWidth/2.0f-COMUtil.getPixel_W(1.0f));
//				nMid = (int)(nStart + (float)nWidth/2.0f);
//				nEnd = (int)(nStart + nWidth);
//			}
			//if(openData[i]>closeData[i]){//시가>종가
			if(i>0)
			{
				bSame = false;  //2017.05.30 by LYH >> 보합 HTS와 색상 같게 수정
				if(_cvm.getCandle_basePrice() == 1)//상승/하락기준 - 당일종가 기준
				{
					if(closeData[i]>closeData[i-1])
						bUp = true;
					else if(closeData[i]<closeData[i-1])
						bUp = false;
					else
						bSame = true;   //2017.05.30 by LYH >> 보합 HTS와 색상 같게 수정
				}
				else	//상승/하락기준 - 당일시가 기준
				{
					if(openData[i]>closeData[i-1])
						bUp = true;
					else if(openData[i]<closeData[i-1])
						bUp = false;
					else
						bSame = true;   //2017.05.30 by LYH >> 보합 HTS와 색상 같게 수정
				}

				if(_cvm.getCandle_sameColorType() == 0)//흑색
				{
					bSameUp = 2;  // 보합
				}
				else if(_cvm.getCandle_sameColorType() == 1)//상승/하락기준 - 전일종가 기준
				{
//					if(closeData[i]>closeData[i-1])
//						bSameUp = 0; // 상승
//					else
//						bSameUp = 1;  // 하락
					//2017.05.30 by LYH >> 보합 HTS와 색상 같게 수정
					if(bSame)
					{
						bUp = bPreUp;
					}
					//2017.05.30 by LYH >> 보합 HTS와 색상 같게 수정 end
                    if(bUp)
                        bSameUp = 0;    // 상승
                    else
                        bSameUp = 1;    // 하락
				}
//            	else if(_cvm.getCandle_sameColorType() == 1)//상승/하락기준 - 전일종가 기준
//            	{
//            			bSameUp = true;
//            	}
//            	else if(_cvm.getCandle_sameColorType() == 2)//상승/하락기준 - 전일종가 기준
//            	{
//            			bSameUp = false;
//            	}
			}
			//if(openData[i]>closeData[i]||(i!=0 &&(closeData[i] <= closeData[i-1])&& (openData[i] == closeData[i]))){//시가>종가
			if(openData[i]>closeData[i] || (bSameUp == 1 && (openData[i] == closeData[i]))){//시가>종가
				bPreUp = false; //2017.05.30 by LYH >> 보합 HTS와 색상 같게 수정
				//_cvm.drawLine(gl, x,highY,x,lowY, CoSys.DOWN_LINE_COLORS ,1.0f);
				if(bUp)
				{
					if(isFillDown2())
					{
						//addRectPosition(rectPositions_down2, nRectIndex_down2, nMid, highY, nMid+1, lowY);
						//2020. 06. 08 by hyh - 우측으로 쏠린 머리, 꼬리 위치 가운데로 조정. 캔들, 캔들볼륨 >>
						addRectPosition(rectPositions_down2, nRectIndex_down2, fMid-(fLineWidth/2), highY, fMid+(fLineWidth/2), lowY);
						//addRectPosition(rectPositions_down2, nRectIndex_down2, fMid, highY, fMid, lowY);
						//2020. 06. 08 by hyh - 우측으로 쏠린 머리, 꼬리 위치 가운데로 조정. 캔들, 캔들볼륨 <<
						nRectIndex_down2 += 4;
						if(isInverse()){
							addRectPosition(rectPositions_down2, nRectIndex_down2, fStart, closeY, fEnd, openY);
							nRectIndex_down2 += 4;
						}else{
							if(closeY-openY<1)
								closeY+=fLineWidth;
							addRectPosition(rectPositions_down2, nRectIndex_down2, fStart, openY, fEnd, closeY);
							nRectIndex_down2 += 4;
						}
					}
					else
					{
						rectPositions_down2[nRectIndex_down2++]=fMid;
						rectPositions_down2[nRectIndex_down2++]=highY;
						rectPositions_down2[nRectIndex_down2++]=fMid;
						rectPositions_down2[nRectIndex_down2++]=openY;
						rectPositions_down2[nRectIndex_down2++]=fMid;
						rectPositions_down2[nRectIndex_down2++]=closeY;
						rectPositions_down2[nRectIndex_down2++]=fMid;
						rectPositions_down2[nRectIndex_down2++]=lowY;
						if(isInverse()){
							//_cvm.drawFillRect(gl, (x-xw),closeY,(2*xw),openY-closeY, CoSys.DOWN_LINE_COLORS, 1.0f);
							addEmptyRectPosition(rectPositions_down2, nRectIndex_down2, fStart, closeY, fEnd, openY);
							nRectIndex_down2 += 16;
						}else{
							//_cvm.drawFillRect(gl, (x-xw),openY,(2*xw),closeY-openY, CoSys.DOWN_LINE_COLORS, 1.0f);
							addEmptyRectPosition(rectPositions_down2, nRectIndex_down2, fStart, openY, fEnd, closeY);
							nRectIndex_down2 += 16;
						}
					}
				}
				else
				{
					if(isFillDown())
					{
						//addRectPosition(rectPositions_down, nRectIndex_down, nMid, highY, nMid+1, lowY);
						//2020. 06. 08 by hyh - 우측으로 쏠린 머리, 꼬리 위치 가운데로 조정. 캔들, 캔들볼륨 >>
						addRectPosition(rectPositions_down, nRectIndex_down, fMid-(fLineWidth/2), highY, fMid+(fLineWidth/2), lowY);
						//addRectPosition(rectPositions_down, nRectIndex_down, fMid, highY, fMid+nLineWidth, lowY);
						//2020. 06. 08 by hyh - 우측으로 쏠린 머리, 꼬리 위치 가운데로 조정. 캔들, 캔들볼륨 <<
						nRectIndex_down += 4;
						if(isInverse()){
							addRectPosition(rectPositions_down, nRectIndex_down, fStart, closeY, fEnd, openY);
							nRectIndex_down += 4;
						}else{
							if(closeY-openY<1)
								closeY+=fLineWidth;
							addRectPosition(rectPositions_down, nRectIndex_down, fStart, openY, fEnd, closeY);
							nRectIndex_down += 4;
						}
					}
					else
					{
						rectPositions_down[nRectIndex_down++]=fMid;
						rectPositions_down[nRectIndex_down++]=highY;
						rectPositions_down[nRectIndex_down++]=fMid;
						rectPositions_down[nRectIndex_down++]=openY;
						rectPositions_down[nRectIndex_down++]=fMid;
						rectPositions_down[nRectIndex_down++]=closeY;
						rectPositions_down[nRectIndex_down++]=fMid;
						rectPositions_down[nRectIndex_down++]=lowY;
						if(isInverse()){
							addEmptyRectPosition(rectPositions_down, nRectIndex_down, fStart, closeY, fEnd, openY);
							nRectIndex_down += 16;
						}else{
							addEmptyRectPosition(rectPositions_down, nRectIndex_down, fStart, openY, fEnd, closeY);
							nRectIndex_down += 16;
						}
					}
				}
				//}else if(openData[i]<closeData[i]){
				//}else if((openData[i]<closeData[i]) ||(i==0 && (openData[i] == closeData[i])) || (i!=0 &&(closeData[i] > closeData[i-1])&& (openData[i] == closeData[i])) ){
			}else if((openData[i]<closeData[i]) || (bSameUp == 0 && (openData[i] == closeData[i])) ){
				bPreUp = true;  //2017.05.30 by LYH >> 보합 HTS와 색상 같게 수정
				if(bUp)
				{
					if(isFillUp())
					{
						//addRectPosition(rectPositions_up, nRectIndex_up, nMid, highY, nMid+1, lowY);
						//2020. 06. 08 by hyh - 우측으로 쏠린 머리, 꼬리 위치 가운데로 조정. 캔들, 캔들볼륨 >>
						addRectPosition(rectPositions_up, nRectIndex_up, fMid-(fLineWidth/2), highY, fMid+(fLineWidth/2), lowY);
						//addRectPosition(rectPositions_up, nRectIndex_up, fMid, highY, fMid+nLineWidth, lowY);
						//2020. 06. 08 by hyh - 우측으로 쏠린 머리, 꼬리 위치 가운데로 조정. 캔들, 캔들볼륨 <<
						nRectIndex_up += 4;
						if(isInverse()){
							addRectPosition(rectPositions_up, nRectIndex_up, fStart, openY, fEnd, closeY);
							nRectIndex_up += 4;
						}else{
							if(openY-closeY<1)
								openY+=fLineWidth;
							addRectPosition(rectPositions_up, nRectIndex_up, fStart, closeY, fEnd, openY);
							nRectIndex_up += 4;
						}
					}
					else
					{
						rectPositions_up[nRectIndex_up++]=fMid;
						rectPositions_up[nRectIndex_up++]=highY;
						rectPositions_up[nRectIndex_up++]=fMid;
						rectPositions_up[nRectIndex_up++]=closeY;
						rectPositions_up[nRectIndex_up++]=fMid;
						rectPositions_up[nRectIndex_up++]=openY;
						rectPositions_up[nRectIndex_up++]=fMid;
						rectPositions_up[nRectIndex_up++]=lowY;
						if(isInverse()){
							addEmptyRectPosition(rectPositions_up, nRectIndex_up, fStart, openY, fEnd, closeY);
							nRectIndex_up += 16;
						}else{
							addEmptyRectPosition(rectPositions_up, nRectIndex_up, fStart, closeY, fEnd, openY);
							nRectIndex_up += 16;
						}
					}
				}
				else if(bSameUp == 1 && (openData[i] == closeData[i]))
				{
					linePositions_same[nLineIndex_same++] = fMid;
					linePositions_same[nLineIndex_same++] = highY;
					linePositions_same[nLineIndex_same++] = fMid;
					linePositions_same[nLineIndex_same++] = lowY;
					linePositions_same[nLineIndex_same++] = fStart;
					linePositions_same[nLineIndex_same++] = openY;
					linePositions_same[nLineIndex_same++] = fEnd;
					linePositions_same[nLineIndex_same++] = openY;
				}
				else
				{
					if(isFillUp2())
					{
						//addRectPosition(rectPositions_up2, nRectIndex_up2, fMid, highY, fMid+1, lowY);
						//2020. 06. 08 by hyh - 우측으로 쏠린 머리, 꼬리 위치 가운데로 조정. 캔들, 캔들볼륨 >>
						addRectPosition(rectPositions_up2, nRectIndex_up2, fMid-(fLineWidth/2), highY, fMid+(fLineWidth/2), lowY);
						//addRectPosition(rectPositions_up2, nRectIndex_up2, fMid, highY, fMid+nLineWidth, lowY);
						//2020. 06. 08 by hyh - 우측으로 쏠린 머리, 꼬리 위치 가운데로 조정. 캔들, 캔들볼륨 <<
						nRectIndex_up2 += 4;
						if(isInverse()){
							addRectPosition(rectPositions_up2, nRectIndex_up2, fStart, openY, fEnd, closeY);
							nRectIndex_up2 += 4;
						}else{
							if(openY-closeY<1)
								openY+=fLineWidth;
							addRectPosition(rectPositions_up2, nRectIndex_up2, fStart, closeY, fEnd, openY);
							nRectIndex_up2 += 4;
						}
					}
					else
					{
						rectPositions_up2[nRectIndex_up2++]=fMid;
						rectPositions_up2[nRectIndex_up2++]=highY;
						rectPositions_up2[nRectIndex_up2++]=fMid;
						rectPositions_up2[nRectIndex_up2++]=closeY;
						rectPositions_up2[nRectIndex_up2++]=fMid;
						rectPositions_up2[nRectIndex_up2++]=openY;
						rectPositions_up2[nRectIndex_up2++]=fMid;
						rectPositions_up2[nRectIndex_up2++]=lowY;
						if(isInverse()){
							addEmptyRectPosition(rectPositions_up2, nRectIndex_up2, fStart, openY, fEnd, closeY);
							nRectIndex_up2 += 16;
						}else{
							addEmptyRectPosition(rectPositions_up2, nRectIndex_up2, fStart, closeY, fEnd, openY);
							nRectIndex_up2 += 16;
						}
					}
				}
			}else{
				linePositions_same[nLineIndex_same++] = fMid;
				linePositions_same[nLineIndex_same++] = highY;
				linePositions_same[nLineIndex_same++] = fMid;
				linePositions_same[nLineIndex_same++] = lowY;
				linePositions_same[nLineIndex_same++] = fStart;
				linePositions_same[nLineIndex_same++] = openY;
				linePositions_same[nLineIndex_same++] = fEnd;
				linePositions_same[nLineIndex_same++] = openY;
				//_cvm.drawLine(gl,x,highY,x,lowY, sameColor ,1.0f);
				//_cvm.drawLine(gl,(x-xw),openY,(x+xw),openY, sameColor ,1.0f);
			}
			if(highData[i]==getBongMax()){
				if(!maxset){
					//2021.09.28 by lyk - kakaopay - 최고/최저 표시를 봉그래프 위에 그리도록 수정 >>
					//					drawMinMaxString(gl,(int)x,(int)highY,i,true);
					//x,y,index,boolean(up/down)
					maxPositions[0] = x;
					maxPositions[1] = highY;
					maxPositions[2] = i;
					maxPositions[3] = 1;
					//2021.09.28 by lyk - kakaopay - 최고/최저 표시를 봉그래프 위에 그리도록 수정 <<
					maxset=true;
				}
			}
            if(lowData[i]==getBongMin()){
				if(!minset){
					//2021.09.28 by lyk - kakaopay - 최고/최저 표시를 봉그래프 위에 그리도록 수정 >>
					//					drawMinMaxString(gl,(int)x,(int)lowY+2,i,false);
					//x,y,index,boolean(up/down)
					minPositions[0] = x;
					minPositions[1] = lowY+2;
					minPositions[2] = i;
					minPositions[3] = 0;
					//2021.09.28 by lyk - kakaopay - 최고/최저 표시를 봉그래프 위에 그리도록 수정 <<
					minset=true;
				}
			}

			//2020.11.23 by JJH >> 권리락/배당락 추가 start
			if(_cvm.isExRightShow
					&& exRightTypeData != null && exRightRateData != null
					&& exRightTypeData.length > 0 && exRightRateData.length > 0
					&& (closeData.length == exRightTypeData.length)) {
				if(exRightTypeData[i] > 0.1f && exRightTypeData[i] < 100.0f) {
					drawExRights(gl, x, highY, exRightTypeData[i], exRightRateData[i]);
				}
			}
			//2020.11.23 by JJH >> 권리락/배당락 추가 end
//            //종가선 표시
//            y = calcy(data[data.length-1][3]);
//            _cvm.drawLine(gl,0, y, width, y, CoSys.LAST_LN ,1.0f);

			x+=xfactor;
		}
//        if(nLineIndex_up>0)
//        	_cvm.drawLines(gl, linePositions_up, upColor ,1.0f);
//        if(nLineIndex_down>0)
//        	_cvm.drawLines(gl, linePositions_down, downColor ,1.0f);
		if(nLineIndex_same>0)
			_cvm.drawLines(gl, linePositions_same, sameColor ,1.0f);
		if(nRectIndex_up>0)
		{
			float[] tmp = new float[nRectIndex_up];
			System.arraycopy(rectPositions_up,0,tmp,0,nRectIndex_up);
			if(isFillUp())
				_cvm.drawFillRects(gl, tmp, upColor ,1.0f);
			else
				_cvm.drawLines(gl, tmp, upColor ,1.0f);
		}

		if(nRectIndex_up2>0)
		{
			float[] tmp = new float[nRectIndex_up2];
			System.arraycopy(rectPositions_up2,0,tmp,0,nRectIndex_up2);
			if(isFillUp2())
				_cvm.drawFillRects(gl, tmp, upColor2 ,1.0f);
			else
				_cvm.drawLines(gl, tmp, upColor2 ,1.0f);
		}

		if(nRectIndex_down2>0)
		{   float[] tmp = new float[nRectIndex_down2];
			System.arraycopy(rectPositions_down2,0,tmp,0,nRectIndex_down2);
			if(isFillDown2())
				_cvm.drawFillRects(gl, tmp, downColor2 ,1.0f);
			else
				_cvm.drawLines(gl, tmp, downColor2 ,1.0f);
		}

		if(nRectIndex_down>0)
		{   float[] tmp = new float[nRectIndex_down];
			System.arraycopy(rectPositions_down,0,tmp,0,nRectIndex_down);
			if(isFillDown())
				_cvm.drawFillRects(gl, tmp, downColor ,1.0f);
			else
				_cvm.drawLines(gl, tmp, downColor ,1.0f);
		}

		//2021.09.28 by lyk - kakaopay - 최고/최저 표시를 봉그래프 위에 그리도록 수정 >>
		if(maxset) {
			drawMinMaxString(gl, (int) maxPositions[0], (int) maxPositions[1], (int)maxPositions[2], maxPositions[3] == 1 ? true : false);
		}
		if(minset) {
			drawMinMaxString(gl, (int) minPositions[0], (int) minPositions[1], (int)minPositions[2], minPositions[3] == 1 ? true : false);
		}
		//2021.09.28 by lyk - kakaopay - 최고/최저 표시를 봉그래프 위에 그리도록 수정 <<

		//2011.09.27 by metalpooh  >> 기준선 추가 <<
		drawBaseLine(gl);
		drawPivotDemarkLine(gl);
		drawBuyAveragePriceLine(gl, (int)(x-xfactor/2)); //2021.02.18 by HJW - 매입평균선 추가

		//2015. 1. 22 목표가 전달 메인으로부터 값을 받아 목표가 선 하나 그리기
		drawYScalePriceLine_forPaint(gl);
	}
	public void drawABong(Canvas gl, double[][] data){
		float x=getBounds().left+xw;
		minset=maxset=false;
//        int y=calcy(data[0][0]);
//        int[] yp = new int[data[0].length];
		int startIndex = _cvm.getIndex();
		int dataLen = startIndex + _cvm.getViewNum();
		double[] openData = _cdm.getSubPacketData("시가");
		double[] highData = _cdm.getSubPacketData("고가");
		double[] lowData = _cdm.getSubPacketData("저가");
		double[] closeData = _cdm.getSubPacketData("종가");

		if(closeData==null) return;
		if(dataLen>closeData.length)
			dataLen= closeData.length;

		float openY, highY, lowY, closeY;
		//2024.01.03 by LYH - 일목균형표 줌인, 줌아웃, 스크롤 개선 >>
		dataLen = startIndex + _cvm.getViewNum();
		if(dataLen>closeData.length)
			dataLen= closeData.length;
		//2024.01.03 by LYH - 일목균형표 줌인, 줌아웃, 스크롤 개선 <<
		for(int i=startIndex;i<dataLen;i++){
			//2019. 06. 25 by hyh - 한궁렬 차장님 요청 : 일단은 시고저종 잘못 된 경우 종가의 납작한 봉으로 처리 >>
			if (lowData[i] > openData[i] || highData[i] < openData[i]) {
				openY = calcy(closeData[i]);
				highY = calcy(closeData[i]);
				lowY = calcy(closeData[i]);
				closeY = calcy(closeData[i]);
			}
			//2019. 06. 25 by hyh - 한궁렬 차장님 요청 : 일단은 시고저종 잘못 된 경우 종가의 납작한 봉으로 처리 >>
			//2019. 06. 26 by hyh - 최철순 과장님 요청 : 시고저종 잘못 된 경우 종가의 납작한 봉으로 처리 >>
			else if (lowData[i] > closeData[i] || highData[i] < closeData[i]) {
				openY = calcy(closeData[i]);
				highY = calcy(closeData[i]);
				lowY = calcy(closeData[i]);
				closeY = calcy(closeData[i]);
			}
			//2019. 06. 26 by hyh - 최철순 과장님 요청 : 시고저종 잘못 된 경우 종가의 납작한 봉으로 처리 <<
			else {
				openY = calcy(openData[i]);
				highY = calcy(highData[i]);
				lowY = calcy(lowData[i]);
				closeY = calcy(closeData[i]);
			}

			if(openData[i]>closeData[i]){//시가>종가
				_cvm.drawLine(gl,(int)x,highY,(int)x,lowY, CoSys.DOWN_LINE_COLORS ,1.0f);
				//2015.04.30 by lyk - 바(시고저종) 유형 추가
				if(getDrawType2() == 2) {
					_cvm.drawLine(gl,(int)(x-xw),openY,(int)x,openY, CoSys.DOWN_LINE_COLORS ,1.0f);
				}
				//2015.04.30 by lyk - 바(시고저종) 유형 추가 end
				_cvm.drawLine(gl,(int)(x),closeY,(int)(x+xw),closeY, CoSys.DOWN_LINE_COLORS ,1.0f);

			}else if(openData[i]<closeData[i]){
				_cvm.drawLine(gl,(int)x,highY,(int)x,lowY, CoSys.UP_LINE_COLORS ,1.0f);
				//2015.04.30 by lyk - 바(시고저종) 유형 추가
				if(getDrawType2() == 2) {
					_cvm.drawLine(gl,(int)(x-xw),openY,(int)x,openY, CoSys.UP_LINE_COLORS ,1.0f);
				}
				//2015.04.30 by lyk - 바(시고저종) 유형 추가 end
				_cvm.drawLine(gl,(int)(x),closeY,(int)(x+xw),closeY, CoSys.UP_LINE_COLORS ,1.0f);
			}else{
				_cvm.drawLine(gl,(int)x,highY,(int)x,lowY, sameColor ,1.0f);
				//2015.04.30 by lyk - 바(시고저종) 유형 추가
				if(getDrawType2() == 2) {
					_cvm.drawLine(gl,(int)(x-xw),openY,(int)x,openY, sameColor ,1.0f);
				}
				//2015.04.30 by lyk - 바(시고저종) 유형 추가 end
			}

			if(highData[i]==getBongMax()){
				if(!maxset){
					drawMinMaxString(gl,(int)x,(int)highY,i,true);
					maxset=true;
				}
			}
            if(lowData[i]==getBongMin()){
				if(!minset){
					drawMinMaxString(gl,(int)x,(int)lowY+2,i,false);
					minset=true;
				}
			}

//            //종가선 표시
//            y = calcy(data[data.length-1][3]);
//            _cvm.drawLine(gl,0, y, getBounds().width(), y, CoSys.LAST_LN ,1.0f);

			x+=xfactor;
		}
		//2011.09.27 by metalpooh  >> 기준선 추가 <<
		drawBaseLine(gl);
		drawPivotDemarkLine(gl);
		drawBuyAveragePriceLine(gl, (int)(x-xfactor/2)); //2021.02.18 by HJW - 매입평균선 추가
	}
	private void drawMinMaxString(Canvas gl, float sx, float sy,int idx,boolean up){
		if (COMUtil.apiView == null) return; //2024.01.16 by CYJ - 방어코드 추가 (lyk)

		if (_cvm.bIsMiniBongChart || (!_cvm.bIsHighLowSign && _cvm.bIsLineFillChart) || (!_cvm.bIsHighLowSign && _cvm.bIsLineChart) || !COMUtil.isHighLowShow()) {
			return;
		}
//        int index = _cvm.getIndex();
		//2020.07.06 by LYH >> 캔들볼륨 >>
		int index = _cvm.getIndex();
		AREA area = _cvm.getArea(idx-index);
		if(area != null)
			sx = area.getCenter();
		//2020.07.06 by LYH >> 캔들볼륨 <<

		int[] color;
		float x1 = sx;
		float x2 = sx;
		if(_cvm.bIsHighLowSign) {
			x2 = sx+(int)COMUtil.getPixel(5);
		}
		float y1 = sy;
		//String dataBuf="";
		double data;
		//2012.11.30 by LYH >> 최고값, 최저값 화살표 제거. <<
		//String arrowSign = "-> ";
		//String arrowSign = "";
		if (up) {
			data = this.getBongMax();
			//2021.09.28 by lyk - kakaopay - 최고/최저 표시를 봉그래프 위에 그리도록 수정 >>
			color = CoSys.GREY990;
			//2021.09.28 by lyk - kakaopay - 최고/최저 표시를 봉그래프 위에 그리도록 수정 <<
			//2012. 11. 27  최고가 라벨 약간 위로 위치이동 : C36
			//2021.05.20 by hanjun.Kim - kakaopay - 최고최저 아이콘변경 관련 위치이동
			y1 = sy - (int) COMUtil.getPixel_H(2);
//			y1 = sy - (int) COMUtil.getPixel_H(10);
			if(_cvm.bIsOneQStockChart && _cvm.bIsLineFillChart)
				y1 = sy - (int) COMUtil.getPixel_H(12);
		} else {
			data = this.getBongMin();
			//2021.09.28 by lyk - kakaopay - 최고/최저 표시를 봉그래프 위에 그리도록 수정 >>
			color = CoSys.GREY990;
			//2021.09.28 by lyk - kakaopay - 최고/최저 표시를 봉그래프 위에 그리도록 수정 <<
			//2012. 11. 27  최저가 라벨 약간 아래로 위치이동 : C36
			//2021.05.20 by hanjun.Kim - kakaopay - 최고최저 아이콘변경 관련 위치이동
			y1 = sy - (int) COMUtil.getPixel_H(2);
//			y1 = sy + (int) COMUtil.getPixel_H(1);
			if(_cvm.bIsOneQStockChart && _cvm.bIsLineFillChart)
				y1 = sy + (int) COMUtil.getPixel_H(16);
		}

		//2018. 10. 22 by hyh - 최고가, 최저가 “가격 (등락률, MM/DD)” 형식으로 보이기 >>
		//등락률 획득
		String strChgrate = "";

		double[] closeData = _cdm.getSubPacketData("종가");
		double lastPrice = 0;
		double highLowPrice = data;

		if (closeData.length > 0) {
			int dCnt = closeData.length;

			lastPrice = closeData[dCnt - 1];
		}

		if (highLowPrice != 0) {
			double chgrate = (highLowPrice - lastPrice) * 100 / lastPrice;
			strChgrate = COMUtil.format(chgrate, 2, 3) + "%";
		}

//		//자료일자 획득
//		String strDate = _cdm.getData("자료일자", idx);
//		String period = _cdm.codeItem.strDataType;
//
//		if (strDate == null || strDate.length() == 0) {
//			return;
//		}
//
//		//0,1,2,3,4,5(틱,분,일,주,월,년)
//		if (period.equals("2") || period.equals("3")) { //일주
//			if (strDate.length() >= 8) { //2019. 11. 27 by hyh - 주기변경 연속처리 동시 실행 시 종료되는 에러 수정
//				strDate = strDate.substring(2, 4) + "." + strDate.substring(4, 6) + "." + strDate.substring(6, 8);
//			}
//		}
//		else if (period.equals("4")) { //월
//			if (strDate.length() >= 6) { //2019. 11. 27 by hyh - 주기변경 연속처리 동시 실행 시 종료되는 에러 수정
//				strDate = strDate.substring(0, 4) + "." + strDate.substring(4, 6);
//			}
//		}
//		else if (period.equals("5")) { //년
//			if (strDate.length() >= 4) { //2019. 11. 27 by hyh - 주기변경 연속처리 동시 실행 시 종료되는 에러 수정
//				strDate = strDate.substring(0, 4);
//			}
//		}
//		else if (period.equals("1")) { //분
//			if (strDate.length() == 8) {
//				strDate = strDate.substring(0, 2) + "." + strDate.substring(2, 4) + " " + strDate.substring(4, 6) + ":" + strDate.substring(6, 8);
//			}
//			else if (strDate.length() == 7) {
//				strDate = strDate.substring(0, 1) + "." + strDate.substring(2, 4) + " " + strDate.substring(3, 5) + ":" + strDate.substring(5, 7);
//			}
//		}
//		else if (period.equals("0")) { //틱
//			if (strDate.length() == 8) {
//				strDate = strDate.substring(0, 2) + " " + strDate.substring(2, 4) + ":" + strDate.substring(4, 6) + ":" + strDate.substring(6, 8);
//			}
//			else if (strDate.length() == 7) {
//				strDate = strDate.substring(0, 1) + " " + strDate.substring(1, 3) + ":" + strDate.substring(3, 5) + ":" + strDate.substring(5, 7);
//			}
//			else if (strDate.length() == 6) {
//				strDate = strDate.substring(0, 2) + ":" + strDate.substring(2, 4) + ":" + strDate.substring(4, 6);
//			}
//			else if (strDate.length() == 5) {
//				strDate = strDate.substring(0, 1) + ":" + strDate.substring(1, 3) + ":" + strDate.substring(3, 5);
//			}
//		}

		//가격 획득
		String strPrice = ChartUtil.getFormatedData(data, _cdm.getPriceFormat(), _cdm);

		//출력 문자열 생성
//		String strData = strPrice + "(" + strDate + ")" + ", " + strChgrate;
		String strData = strPrice + "(" + strChgrate + ")";

		//등락률, 날짜가 없는 경우 예외처리
//		if (strChgrate.length() == 0 || strDate.length() == 0 || (_cvm.bIsOneQStockChart && _cvm.bIsLineFillChart)) {
//			strData = strPrice;
//		}
		if (strChgrate.length() == 0 || (_cvm.bIsOneQStockChart && _cvm.bIsLineFillChart)) {
			strData = strPrice;
		}
		//2018. 10. 22 by hyh - 최고가, 최저가 “가격 (등락률, MM/DD)” 형식으로 보이기 <<

		int nTextSize = (int)COMUtil.nFontSize_paint;
		//2020.12.28 by HJW - 폰트 사이즈 옵션 추가 >>
		if(_cvm.g_nFontSizeBtn == 0)
			nTextSize = (int)COMUtil.nFontSize_paint-(int)COMUtil.getPixel(2);
		else if(_cvm.g_nFontSizeBtn == 1)
			nTextSize = (int)COMUtil.nFontSize_paint;
		else if(_cvm.g_nFontSizeBtn == 2)
			nTextSize = (int)COMUtil.nFontSize_paint+(int)COMUtil.getPixel(2);
		else
			nTextSize = (int)COMUtil.nFontSize_paint;
		//2020.12.28 by HJW - 폰트 사이즈 옵션 추가 <<

		int nTextWidth = (int)_cvm.getFontWidth_Mid(strData, nTextSize);
		if(_cvm.bIsTodayLineChart)
			nTextWidth = (int)_cvm.getFontWidth(strData, (int)COMUtil.getPixel(10));
		int nTextMargin = (int) COMUtil.getPixel_W(10);

		if (!_cvm.bIsHighLowSign) {
			if (this.getBounds().width() - sx < nTextWidth + nTextMargin) {    //2015. 1. 16 차트화면 고저가 문자열 날짜가 yscale영역 침범
				x1 = x1 - (int) COMUtil.getPixel(7); // 2021.05.20 by hanjun.Kim 기존 X 추가됨
				x2 = x1 - (nTextWidth + nTextMargin);
				int layoutResId;
				Bitmap image;
				//2021.05.20 by hanjun.Kim - kakaopay - 최고최저 아이콘변경 >>
//				if (up) {
//					layoutResId = COMUtil.apiView.getContext().getResources().getIdentifier("chart_arrow_l_pink", "drawable", COMUtil.apiView.getContext().getPackageName());
//					image = BitmapFactory.decodeResource(COMUtil.apiView.getContext().getResources(), layoutResId);
//				} else {
//					layoutResId = COMUtil.apiView.getContext().getResources().getIdentifier("chart_arrow_l_blue", "drawable", COMUtil.apiView.getContext().getPackageName());
//					image = BitmapFactory.decodeResource(COMUtil.apiView.getContext().getResources(), layoutResId);
//				}
				layoutResId = COMUtil.apiView.getContext().getResources().getIdentifier("kfit_mts_img_common_l", "drawable", COMUtil.apiView.getContext().getPackageName());
				image = BitmapFactory.decodeResource(COMUtil.apiView.getContext().getResources(), layoutResId);

				ColorFilter colorFilter;
				//2021.09.28 by lyk - kakaopay - 최고/최저 표시를 봉그래프 위에 그리도록 수정 >>
				if (up) {
					colorFilter = new PorterDuffColorFilter(Color.rgb(CoSys.GREY990[0],CoSys.GREY990[1],CoSys.GREY990[2]), PorterDuff.Mode.SRC_ATOP); //(color, mode)
				} else {
					colorFilter = new PorterDuffColorFilter(Color.rgb(CoSys.GREY990[0],CoSys.GREY990[1],CoSys.GREY990[2]), PorterDuff.Mode.SRC_ATOP); //(color, mode)
				}
				//2021.09.28 by lyk - kakaopay - 최고/최저 표시를 봉그래프 위에 그리도록 수정 <<

//				_cvm.drawImage(gl, x1 - (int) COMUtil.getPixel(5), y1, (int) COMUtil.getPixel(12), (int) COMUtil.getPixel(10), image, 255);
//				_cvm.drawImage(gl, x1 - (int) COMUtil.getPixel(5), y1, (int) COMUtil.getPixel(12), (int) COMUtil.getPixel(3), image, 255); // 기존값(21.05.20)
				_cvm.drawImage(gl, x1 - (int) COMUtil.getPixel(4), y1, (int) COMUtil.getPixel(12), (int) COMUtil.getPixel(3), image, 255, colorFilter);
//				if (up) {
//					_cvm.drawImage(gl, x1 - (int) COMUtil.getPixel(12), y1 + (int) COMUtil.getPixel(5), (int) COMUtil.getPixel(12), (int) COMUtil.getPixel(3), image, 255);
//				} else {
//					_cvm.drawImage(gl, x1 - (int) COMUtil.getPixel(12), y1 - (int) COMUtil.getPixel(2), (int) COMUtil.getPixel(12), (int) COMUtil.getPixel(3), image, 255);
//				}
				//2021.05.20 by hanjun.Kim - kakaopay - 최고최저 아이콘변경 <<
			} else {
//				x1 = x1;
//				x2 += (nTextMargin);
				x1 = x1 - (int) COMUtil.getPixel(0);
				x2 += (nTextMargin + (int) COMUtil.getPixel(7)); // 2021.05.20 by hanjun.Kim + COMUtil.getPixel(2)
				int layoutResId;
				Bitmap image;
				//2021.05.20 by hanjun.Kim - kakaopay - 최고최저 아이콘변경 >>
//				if (up) {
//					layoutResId = COMUtil.apiView.getContext().getResources().getIdentifier("chart_arrow_r_pink", "drawable", COMUtil.apiView.getContext().getPackageName());
//					image = BitmapFactory.decodeResource(COMUtil.apiView.getContext().getResources(), layoutResId);
//				} else {
//					layoutResId = COMUtil.apiView.getContext().getResources().getIdentifier("chart_arrow_r_blue", "drawable", COMUtil.apiView.getContext().getPackageName());
//					image = BitmapFactory.decodeResource(COMUtil.apiView.getContext().getResources(), layoutResId);
//				}
				layoutResId = COMUtil.apiView.getContext().getResources().getIdentifier("kfit_mts_img_common_r", "drawable", COMUtil.apiView.getContext().getPackageName());
				image = BitmapFactory.decodeResource(COMUtil.apiView.getContext().getResources(), layoutResId);

				ColorFilter colorFilter;
				//2021.09.28 by lyk - kakaopay - 최고/최저 표시를 봉그래프 위에 그리도록 수정 >>
				if (up) {
					colorFilter = new PorterDuffColorFilter(Color.rgb(CoSys.GREY990[0],CoSys.GREY990[1],CoSys.GREY990[2]), PorterDuff.Mode.SRC_ATOP); //(color, mode)
				} else {
					colorFilter = new PorterDuffColorFilter(Color.rgb(CoSys.GREY990[0],CoSys.GREY990[1],CoSys.GREY990[2]), PorterDuff.Mode.SRC_ATOP); //(color, mode)
				}
				//2021.09.28 by lyk - kakaopay - 최고/최저 표시를 봉그래프 위에 그리도록 수정 <<

//				_cvm.drawImage(gl, x1 - (int) COMUtil.getPixel(2), y1, (int) COMUtil.getPixel(12), (int) COMUtil.getPixel(10), image, 255);
				_cvm.drawImage(gl, x1, y1, (int) COMUtil.getPixel(13), (int) COMUtil.getPixel(4), image, 255, colorFilter);
//				if (up) {/					_cvm.drawImage(gl, x1 - (int) COMU
///til.getPixel(1), y1 + (int) COMUtil.getPixel(5), (int) COMUtil.getPixel(12), (int) COMUtil.getPixel(3), image, 255);
//				} else {
//					_cvm.drawImage(gl, x1 - (int) COMUtil.getPixel(1), y1 - (int) COMUtil.getPixel(2), (int) COMUtil.getPixel(12), (int) COMUtil.getPixel(3), image, 255);
//				}
				//2021.05.20 by hanjun.Kim - kakaopay - 최고최저 아이콘변경 <<
			}
		}

		if (_cvm.bIsHighLowSign) {
			nTextMargin = (int) COMUtil.getPixel(5);

			if(_cvm.bIsOneQStockChart && _cvm.bIsLineFillChart) {
				if(pnt == null) pnt = new Paint();
				pnt.setTextSize(COMUtil.getPixel(14));
				pnt.setTypeface(COMUtil.numericTypeface);
				nTextWidth = _cvm.getTextWidth(strData, pnt) + (int)COMUtil.getPixel(3);
				x2 = sx - nTextWidth / 2;
				_cvm.drawCircle(gl, sx-(int)COMUtil.getPixel(3), sy-(int)COMUtil.getPixel(3), sx+(int)COMUtil.getPixel(3), sy+(int)COMUtil.getPixel(3), true, color);

				if (this.getBounds().width() - sx < nTextWidth/2) {
					x2 -= nTextWidth/2 - (this.getBounds().width() - sx+ nTextMargin - (int)COMUtil.getPixel(15));
				}
			} else {
				if (this.getBounds().width() - sx < nTextWidth + nTextMargin) {
					x2 = x1 - (nTextWidth + nTextMargin);
				}
			}
		}

		if(x2<0)
			x2 = (int)COMUtil.getPixel(10);
		if(_cvm.bIsOneQStockChart && _cvm.bIsLineFillChart)
			_cvm.drawStringWithSize(gl, color, x2, y1,COMUtil.getPixel(14) , strData);
		else {
			if(_cvm.bIsTodayLineChart)
				_cvm.drawStringWithSizeFont(gl, color, x2, y1 + (int) COMUtil.getPixel_H(5), COMUtil.getPixel(10), strData, COMUtil.numericTypeface);
			else {
//				_cvm.drawStringWithSizeFont(gl, color, x2, y1 + (int) COMUtil.getPixel_H(6), COMUtil.getPixel(13), strData, COMUtil.numericTypefaceMid);
				if(strData.equals(strPrice))
					_cvm.drawStringWithSizeFont(gl, color, x2, y1 + (int) COMUtil.getPixel_H(3), nTextSize, strData, COMUtil.numericTypeface);
				else {
					_cvm.drawStringWithSizeFont(gl, color, x2, y1 + (int) COMUtil.getPixel_H(1), nTextSize, strPrice, COMUtil.numericTypefaceBold);
//					strData = "(" + strDate + ")" + " " + strChgrate;
					strData = "(" + strChgrate + ")";
					nTextWidth = (int)_cvm.getFontWidth_Mid(strPrice, nTextSize);
					_cvm.drawStringWithSizeFont(gl, color, x2+nTextWidth+COMUtil.getPixel(2), y1 + (int) COMUtil.getPixel_H(1), nTextSize, strData, COMUtil.numericTypeface);
				}
			}
		}
		//2015. 1. 13 캔들 상 고가/저가가 옆에 해당 날짜 표시<<
	}
	public boolean isSelected(Point p, int index){
		if(bongData==null) return false;
		int idx= _cvm.getIndex();
		int curIndex = index-idx;
		if(curIndex >= bongData.length||curIndex<0) return false;
		float maxY = calcy(bongData[curIndex][1]);
		float minY = calcy(bongData[curIndex][2]);

		if( (p.y>=maxY) && (p.y<=minY ))
			return true;

		return false;
	}
	public float getYPos(int pos){
		if(bongData!=null){
			return calcy(bongData[pos][3]);
		}
		return 0;
	}
	public String getData(int pos){
		return _cdm.getData("종가", pos);
	}

	//2015. 1. 22 목표가 전달 메인으로부터 값을 받아 목표가 선 하나 그리기>>
	public void drawYScalePriceLine_forPaint(Canvas gl)
	{
		//수평선을 그릴 가격이 있다면 수평선과 글자를 차트에 그린다.
		if(!_cvm.getDragPrice().equals("") && null != _cvm.getDragPrice())
		{
			RectF bounds = getBounds();
			float y= calcy(Double.parseDouble(_cvm.getDragPrice()));

			//색상 결정
			int[] color = {0, 0, 0};
			if(_cvm.getSkinType() == COMUtil.SKIN_BLACK) {
				color[0] = color[1] = color[2] = 255;
			}

			//drawLine
			_cvm.drawLine(gl, bounds.left,y,bounds.width(),y, color ,1.0f);
			_cvm.drawString(gl, color, bounds.left+(int)COMUtil.getPixel(2), (int)(y +COMUtil.getPixel(8)), "목표가 : " + _cvm.getDragPrice());
		}
	}
	//2015. 1. 22 목표가 전달 메인으로부터 값을 받아 목표가 선 하나 그리기<<

	public void drawFlow(Canvas gl, double[][] data){
//if(data==null||data.length<1)return;

		float x=getBounds().left+xw;
		float xpos=getBounds().left+xw;
		float ypos=0, yPosHigh, yPosLow;
		float ypos1=0, yPosHigh1, yPosLow1;

		minset=maxset=false;
//        int x=0, y=0;
		float highXPos = -1;
		float highYPos = -1;
		float lowXPos = -1;
		float lowYPos = -1;

		double yInc;
		line_thick=2;
		int thick =line_thick, temp;
		int startIndex = _cvm.getIndex();
		int dataLen = startIndex + _cvm.getViewNum() + _cvm.futureMargin;
		int nTotCnt = _cdm.getCount();
		if(dataLen>nTotCnt)
			dataLen = nTotCnt;
//20120621 by LYH <<

		double[] highData = _cdm.getSubPacketData("고가");
		double[] lowData = _cdm.getSubPacketData("저가");
		double[] closeData = _cdm.getSubPacketData("종가");
		int totLen = (dataLen-startIndex-1)*4;
		if(dataLen==1) {
			totLen = 4;
		}
		float[] positions = new float[totLen];// = new float[dataLen];
		float[] positionsHigh = new float[totLen];// = new float[dataLen];
		float[] positionsLow = new float[totLen];// = new float[dataLen];
		int nIndex = 0;
		if(dataLen==1) {
			ypos=(int)calcy(highData[0]);
			positionsHigh[0] = xpos-xw;
			positionsHigh[1] = ypos;
			positionsHigh[2] = xpos+xw;
			positionsHigh[3] = ypos;

			ypos=(int)calcy(lowData[0]);
			positionsLow[0] = xpos-xw;
			positionsLow[1] = ypos;
			positionsLow[2] = xpos+xw;
			positionsLow[3] = ypos;

			nIndex=4;
		} else {
			//2024.01.03 by LYH - 일목균형표 줌인, 줌아웃, 스크롤 개선 >>
			dataLen = startIndex + _cvm.getViewNum();
			if(dataLen>closeData.length)
				dataLen= closeData.length;
			//2024.01.03 by LYH - 일목균형표 줌인, 줌아웃, 스크롤 개선 <<
			for(int i=startIndex; i<dataLen-1; i++){
				//2019. 06. 26 by hyh - 최철순 과장님 요청 : 시고저종 잘못 된 경우 종가의 납작한 봉으로 처리 >>
				if (lowData[i] > closeData[i] || highData[i] < closeData[i]) {
					ypos = (int) calcy(closeData[i]);
					yPosHigh = (int) calcy(closeData[i]);
					yPosLow = (int) calcy(closeData[i]);
				}
				else {
					ypos = (int) calcy(closeData[i]);
					yPosHigh = (int) calcy(highData[i]);
					yPosLow = (int) calcy(lowData[i]);
				}

				if (lowData[i + 1] > closeData[i + 1] || highData[i + 1] < closeData[i + 1]) {
					ypos1 = (int) calcy(closeData[i + 1]);
					yPosHigh1 = (int) calcy(closeData[i + 1]);
					yPosLow1 = (int) calcy(closeData[i + 1]);
				}
				else {
					ypos1 = (int) calcy(closeData[i + 1]);
					yPosHigh1 = (int) calcy(highData[i + 1]);
					yPosLow1 = (int) calcy(lowData[i + 1]);
				}
				//2019. 06. 26 by hyh - 최철순 과장님 요청 : 시고저종 잘못 된 경우 종가의 납작한 봉으로 처리 <<

				if((ypos<=max_view&&ypos1<=max_view)){
					yInc = (double)(((double)ypos1-(double)ypos)/(double)(xfactor*2));
					temp = (yInc<0)?Math.abs((int)(-1*(yInc-0.99))):(int)(yInc+0.99);
					thick=(temp>thick)?temp:line_thick;

					positionsHigh[nIndex] = xpos;
					positionsLow[nIndex] = xpos;
					positions[nIndex++] = xpos;
					positionsHigh[nIndex] = yPosHigh;
					positionsLow[nIndex] = yPosLow;
					positions[nIndex++] = ypos;
					positionsHigh[nIndex] = xpos+xfactor;
					positionsLow[nIndex] = xpos+xfactor;
					positions[nIndex++] = xpos+xfactor;
					positionsHigh[nIndex] = yPosHigh1;
					positionsLow[nIndex] = yPosLow1;
					positions[nIndex++] = ypos1;

				}
				if(highData[i]==getBongMax()){
					if(!maxset){
						if(!_cvm.bIsLineChart && !_cvm.bIsLine2Chart || _cvm.bIsHighLowSign) {
							drawMinMaxString(gl,(int)xpos,(int)yPosHigh-(int)COMUtil.getPixel(1),i,true);
						}
						maxset=true;
						highXPos = xpos;
						highYPos = ypos;
					}
				}
				if(lowData[i]==getBongMin()){
					if(!minset){
						if(!_cvm.bIsLineChart && !_cvm.bIsLine2Chart || _cvm.bIsHighLowSign) {
							drawMinMaxString(gl, (int) xpos, (int) yPosLow+(int)COMUtil.getPixel(1), i, false);
						}
						minset=true;
						lowXPos = xpos;
						lowYPos = ypos;
					}
				}

				if(i== dataLen-2)
				{
					if(highData[i+1]==getBongMax()){
						if(!maxset){
							if(!_cvm.bIsLineChart && !_cvm.bIsLine2Chart || _cvm.bIsHighLowSign) {
								drawMinMaxString(gl,(int)(xpos+xfactor),(int)ypos1-(int)COMUtil.getPixel(1),i+1,true);
							}
							maxset=true;
							highXPos = xpos+xfactor;
							highYPos = ypos1;
						}
					}
					if(lowData[i+1]==getBongMin()){
						if(!minset){
							if(!_cvm.bIsLineChart && !_cvm.bIsLine2Chart || _cvm.bIsHighLowSign) {
								drawMinMaxString(gl, (int)(xpos+xfactor), (int) ypos1+(int)COMUtil.getPixel(1), i+1, false);
							}
							minset=true;
							lowXPos = xpos+xfactor;
							lowYPos = ypos1;
						}
					}
				}
				xpos+=xfactor;
			}
		}

		if(nIndex>0)
		{
			_cvm.setLineWidth(2);


//_cvm.setLineWidth_Fix(2);
			_cvm.setLineWidth(1);


			int[] color0 = new int[3];

			color0[0] = 137;
			color0[1] = 210;
			color0[2] = 105;

//_cvm.drawLineWithFillGradient(gl, positions, this.max_view + (int)COMUtil.getPixel(8), color0, 127, nIndex);
			_cvm.drawLineWithFillGradient_Flow(gl, positionsHigh, positionsLow, color0, 127, nIndex);


			int[] colorLine = new int[3];
			colorLine[0] = 138;
			colorLine[1] = 214;
			colorLine[2] = 104;
			_cvm.drawLines(gl, positions, upColor ,1.0f);

			_cvm.setLineWidth(1);
		}
		//2019. 11. 26 by hyh - 라인차트에 기준선 그리기 >>
		drawBaseLine(gl);
		drawPivotDemarkLine(gl);
		//2019. 11. 26 by hyh - 라인차트에 기준선 그리기 <<
	}

	public void drawHeikinAshiBong(Canvas gl, double[][] data){

		//2013.02.12 by LYH >> 블랙에서 보합색 변경
		if(_cvm.getSkinType() == COMUtil.SKIN_BLACK) {
			this.setSameColor(CoSys.CHART_COLORS[7]);
		}
		else
		{
			this.setSameColor(CoSys.CHART_COLORS[2]);
		}
		//2013.02.12 by LYH << 블랙에서 보합색 변경

		float x=getBounds().left+xw;
		minset=maxset=false;
		int startIndex = _cvm.getIndex();
		//20120621 by LYH >> 일목균형 스크롤 처리
		//int dataLen = startIndex + _cvm.getViewNum();
		int dataLen = startIndex + _cvm.getViewNum() + _cvm.futureMargin;
		int nTotCnt = _cdm.getCount();
		if(dataLen>nTotCnt)
			dataLen = nTotCnt;
		//20120621 by LYH <<

		double[] openDataOrg = _cdm.getSubPacketData("시가").clone();
		double[] highDataOrg = _cdm.getSubPacketData("고가").clone();
		double[] lowDataOrg = _cdm.getSubPacketData("저가").clone();
		double[] closeDataOrg = _cdm.getSubPacketData("종가").clone();

		double[] openData = new double[closeDataOrg.length];
		double[] highData = new double[closeDataOrg.length];
		double[] lowData = new double[closeDataOrg.length];
		double[] closeData = new double[closeDataOrg.length];

		if(closeData==null) return;
		if(dataLen>closeData.length)
			dataLen= closeData.length;
		float openY, highY, lowY, closeY;
		if(startIndex<0)
			startIndex = 0;
		int i=startIndex;

		int totLen = (dataLen-startIndex)*4;
		if(totLen <=0)
			return;
		float[] linePositions_same = new float[totLen*2];
		int nLineIndex_same = 0;

		float[] rectPositions_up = new float[totLen*6];
		float[] rectPositions_down = new float[totLen*6];
		float[] rectPositions_up2 = new float[totLen*6];
		float[] rectPositions_down2 = new float[totLen*6];

		int nRectIndex_up = 0;
		int nRectIndex_down = 0;
		int nRectIndex_up2 = 0;
		int nRectIndex_down2 = 0;

		float fWidth = (xw*2);
		if(fWidth%2==0 && fWidth > 2){
			//2020. 06. 08 by hyh - 우측으로 쏠린 머리, 꼬리 위치 가운데로 조정. Heikin-Ashi >>
			fWidth -= COMUtil.getPixel(1);
			//nWidth -=1;
			//2020. 06. 08 by hyh - 우측으로 쏠린 머리, 꼬리 위치 가운데로 조정. Heikin-Ashi <<
		}
		boolean bUp = false;
		int bSameUp = 1;  //0 : 상승   1 : 하락  2 : 보합
		int fLineWidth = 1;
		//if(COMUtil.getPixel(1)>=3)
		if(COMUtil.getPixel(1)>2)	//2019.12.05 by LYH >> 고해상도 라이 두께 조정
		{
			fLineWidth = 2;
		}

		double dOpen = 0, dClose=0, dHigh=0, dLow=0, dData = 0;
		boolean isReadOpen = false;

		float iWidth = (xw*2);
//		if(iWidth>COMUtil.getPixel_W(5))
//		{
//			//fWidth = 1.0;
//			if(iWidth>COMUtil.getPixel_W(8))
//				nWidth = (int)Math.round((xw*2)-COMUtil.getPixel_W(3));
//			else
//				nWidth = (int)Math.round((xw*2)-COMUtil.getPixel_W(2));
//			if((int)nWidth%2==0 && nWidth > 2)
//				nWidth -=1;
//		}
		//2024.01.03 by LYH - 일목균형표 줌인, 줌아웃, 스크롤 개선 >>
		dataLen = startIndex + _cvm.getViewNum();
		if(dataLen>closeData.length)
			dataLen= closeData.length;
		//2024.01.03 by LYH - 일목균형표 줌인, 줌아웃, 스크롤 개선 <<
		for(;i<dataLen;i++){

			//Heikin-Ashi change
			dClose = (closeDataOrg[i] + openDataOrg[i] + highDataOrg[i] + lowDataOrg[i]) / 4;
			if(i==0) {
				dOpen = openDataOrg[i];
			} else {
				if(!isReadOpen) {
					openData[i-1] = (openDataOrg[i-1] + closeDataOrg[i-1])/2;
					closeData[i-1] = (closeDataOrg[i-1]+openDataOrg[i-1]+highDataOrg[i-1]+lowDataOrg[i-1])/4;
				}
				dOpen = (openData[i-1]+closeData[i-1])/2;
			}

			double[] tmpMax = new double[2];
			tmpMax[0] = dOpen;
			tmpMax[1] = dClose;
			double[] resultMax = MinMax.getMinMax(tmpMax);
			dData = resultMax[1]; //max

			double[] tmpMax2 = new double[2];
			tmpMax2[0] = dData;
			tmpMax2[1] = highDataOrg[i];
			double[] resultMax2 = MinMax.getMinMax(tmpMax2);
			dHigh = resultMax2[1]; //max

			double[] tmpMax3 = new double[2];
			tmpMax3[0] = dData;
			tmpMax3[1] = lowDataOrg[i];
			double[] resultMax3 = MinMax.getMinMax(tmpMax3);
			dLow = resultMax3[0]; //min

			openData[i] = dOpen;
			highData[i] = dHigh;
			lowData[i] = dLow;
			closeData[i] = dClose;

			isReadOpen = true;

			//Heikin-Ashi change
			openY = calcy(openData[i]);
			highY = calcy(highData[i]);
			lowY = calcy(lowData[i]);
			closeY = calcy(closeData[i]);

			//2013.02.12 by LYH >> 종가값 0인경우 영역 침범하는 문제 해결.
			if(closeY>max_view)
			{
				openY = max_view;
				highY = max_view;
				lowY = max_view;
				closeY = max_view;
			}
			//2013.02.12 by LYH << 종가값 0인경우 영역 침범하는 문제 해결.

			//2020. 06. 08 by hyh - 우측으로 쏠린 머리, 꼬리 위치 가운데로 조정. Heikin-Ashi >>
			float fStart = (x-xw);
			//float fStart = (x-xw-1);
			//2020. 06. 08 by hyh - 우측으로 쏠린 머리, 꼬리 위치 가운데로 조정. Heikin-Ashi <<

			float fMid = (fStart + fWidth/2);
			float fEnd = (fStart + fWidth);
//			if(iWidth>COMUtil.getPixel_W(5))
//			{
//				if(iWidth>COMUtil.getPixel_W(8))
//					nStart = (int)Math.round(x-(float)nWidth/2.0f-COMUtil.getPixel_W(1.5f));
//				else
//					nStart = (int)Math.round(x-(float)nWidth/2.0f-COMUtil.getPixel_W(1.0f));
//				nMid = (int)(nStart + (float)nWidth/2.0f);
//				nEnd = (int)(nStart + nWidth);
//			}
			//if(openData[i]>closeData[i]){//시가>종가
			if(i>0)
			{
				if(_cvm.getCandle_basePrice() == 1)//상승/하락기준 - 당일종가 기준
				{
					if(closeData[i]>closeData[i-1])
						bUp = true;
					else
						bUp = false;
				}
				else	//상승/하락기준 - 당일시가 기준
				{
					if(openData[i]>closeData[i-1])
						bUp = true;
					else
						bUp = false;
				}

				if(_cvm.getCandle_sameColorType() == 0)//흑색
				{
					bSameUp = 2;  // 보합
				}
				else if(_cvm.getCandle_sameColorType() == 1)//상승/하락기준 - 전일종가 기준
				{
					if(closeData[i]>closeData[i-1])
						bSameUp = 0; // 상승
					else
						bSameUp = 1;  // 하락
				}
//            	else if(_cvm.getCandle_sameColorType() == 1)//상승/하락기준 - 전일종가 기준
//            	{
//            			bSameUp = true;
//            	}
//            	else if(_cvm.getCandle_sameColorType() == 2)//상승/하락기준 - 전일종가 기준
//            	{
//            			bSameUp = false;
//            	}
			}
			//if(openData[i]>closeData[i]||(i!=0 &&(closeData[i] <= closeData[i-1])&& (openData[i] == closeData[i]))){//시가>종가
			if(openData[i]>closeData[i] || (bSameUp == 1 && (openData[i] == closeData[i]))){//시가>종가
				//_cvm.drawLine(gl, x,highY,x,lowY, CoSys.DOWN_LINE_COLORS ,1.0f);
				if(bUp)
				{
					if(isFillDown2())
					{
						//addRectPosition(rectPositions_down2, nRectIndex_down2, nMid, highY, nMid+1, lowY);
						//2020. 06. 08 by hyh - 우측으로 쏠린 머리, 꼬리 위치 가운데로 조정. Heikin-Ashi >>
						addRectPosition(rectPositions_down2, nRectIndex_down2, fMid-fLineWidth/2, highY, fMid+fLineWidth/2, lowY);
						//addRectPosition(rectPositions_down2, nRectIndex_down2, nMid, highY, nMid+nLineWidth, lowY);
						//2020. 06. 08 by hyh - 우측으로 쏠린 머리, 꼬리 위치 가운데로 조정. Heikin-Ashi <<
						nRectIndex_down2 += 4;
						if(isInverse()){
							addRectPosition(rectPositions_down2, nRectIndex_down2, fStart, closeY, fEnd, openY);
							nRectIndex_down2 += 4;
						}else{
							if(closeY-openY<1)
								closeY+=fLineWidth;
							addRectPosition(rectPositions_down2, nRectIndex_down2, fStart, openY, fEnd, closeY);
							nRectIndex_down2 += 4;
						}
					}
					else
					{
						rectPositions_down2[nRectIndex_down2++]=fMid;
						rectPositions_down2[nRectIndex_down2++]=highY;
						rectPositions_down2[nRectIndex_down2++]=fMid;
						rectPositions_down2[nRectIndex_down2++]=openY;
						rectPositions_down2[nRectIndex_down2++]=fMid;
						rectPositions_down2[nRectIndex_down2++]=closeY;
						rectPositions_down2[nRectIndex_down2++]=fMid;
						rectPositions_down2[nRectIndex_down2++]=lowY;
						if(isInverse()){
							//_cvm.drawFillRect(gl, (x-xw),closeY,(2*xw),openY-closeY, CoSys.DOWN_LINE_COLORS, 1.0f);
							addEmptyRectPosition(rectPositions_down2, nRectIndex_down2, fStart, closeY, fEnd, openY);
							nRectIndex_down2 += 16;
						}else{
							//_cvm.drawFillRect(gl, (x-xw),openY,(2*xw),closeY-openY, CoSys.DOWN_LINE_COLORS, 1.0f);
							addEmptyRectPosition(rectPositions_down2, nRectIndex_down2, fStart, openY, fEnd, closeY);
							nRectIndex_down2 += 16;
						}
					}
				}
				else
				{
					if(isFillDown())
					{
						//addRectPosition(rectPositions_down, nRectIndex_down, fMid, highY, fMid+1, lowY);
						//2020. 06. 08 by hyh - 우측으로 쏠린 머리, 꼬리 위치 가운데로 조정. Heikin-Ashi >>
						addRectPosition(rectPositions_down, nRectIndex_down, fMid-(fLineWidth/2), highY, fMid+(fLineWidth/2), lowY);
						//addRectPosition(rectPositions_down, nRectIndex_down, fMid, highY, fMid+fLineWidth, lowY);
						//2020. 06. 08 by hyh - 우측으로 쏠린 머리, 꼬리 위치 가운데로 조정. Heikin-Ashi <<
						nRectIndex_down += 4;
						if(isInverse()){
							addRectPosition(rectPositions_down, nRectIndex_down, fStart, closeY, fEnd, openY);
							nRectIndex_down += 4;
						}else{
							if(closeY-openY<1)
								closeY+=fLineWidth;
							addRectPosition(rectPositions_down, nRectIndex_down, fStart, openY, fEnd, closeY);
							nRectIndex_down += 4;
						}
					}
					else
					{
						rectPositions_down[nRectIndex_down++]=fMid;
						rectPositions_down[nRectIndex_down++]=highY;
						rectPositions_down[nRectIndex_down++]=fMid;
						rectPositions_down[nRectIndex_down++]=openY;
						rectPositions_down[nRectIndex_down++]=fMid;
						rectPositions_down[nRectIndex_down++]=closeY;
						rectPositions_down[nRectIndex_down++]=fMid;
						rectPositions_down[nRectIndex_down++]=lowY;
						if(isInverse()){
							addEmptyRectPosition(rectPositions_down, nRectIndex_down, fStart, closeY, fEnd, openY);
							nRectIndex_down += 16;
						}else{
							addEmptyRectPosition(rectPositions_down, nRectIndex_down, fStart, openY, fEnd, closeY);
							nRectIndex_down += 16;
						}
					}
				}
				//}else if(openData[i]<closeData[i]){
				//}else if((openData[i]<closeData[i]) ||(i==0 && (openData[i] == closeData[i])) || (i!=0 &&(closeData[i] > closeData[i-1])&& (openData[i] == closeData[i])) ){
			}else if((openData[i]<closeData[i]) || (bSameUp == 0 && (openData[i] == closeData[i])) ){
				if(bUp)
				{
					if(isFillUp())
					{
						//addRectPosition(rectPositions_up, nRectIndex_up, fMid, highY, fMid+1, lowY);
						//2020. 06. 08 by hyh - 우측으로 쏠린 머리, 꼬리 위치 가운데로 조정. Heikin-Ashi >>
						addRectPosition(rectPositions_up, nRectIndex_up, fMid-(fLineWidth/2), highY, fMid+(fLineWidth/2), lowY);
						//addRectPosition(rectPositions_up, nRectIndex_up, fMid, highY, fMid+fLineWidth, lowY);
						//2020. 06. 08 by hyh - 우측으로 쏠린 머리, 꼬리 위치 가운데로 조정. Heikin-Ashi <<
						nRectIndex_up += 4;
						if(isInverse()){
							addRectPosition(rectPositions_up, nRectIndex_up, fStart, openY, fEnd, closeY);
							nRectIndex_up += 4;
						}else{
							if(openY-closeY<1)
								openY+=fLineWidth;
							addRectPosition(rectPositions_up, nRectIndex_up, fStart, closeY, fEnd, openY);
							nRectIndex_up += 4;
						}
					}
					else
					{
						rectPositions_up[nRectIndex_up++]=fMid;
						rectPositions_up[nRectIndex_up++]=highY;
						rectPositions_up[nRectIndex_up++]=fMid;
						rectPositions_up[nRectIndex_up++]=closeY;
						rectPositions_up[nRectIndex_up++]=fMid;
						rectPositions_up[nRectIndex_up++]=openY;
						rectPositions_up[nRectIndex_up++]=fMid;
						rectPositions_up[nRectIndex_up++]=lowY;
						if(isInverse()){
							addEmptyRectPosition(rectPositions_up, nRectIndex_up, fStart, openY, fEnd, closeY);
							nRectIndex_up += 16;
						}else{
							addEmptyRectPosition(rectPositions_up, nRectIndex_up, fStart, closeY, fEnd, openY);
							nRectIndex_up += 16;
						}
					}
				}
				else if(bSameUp == 1 && (openData[i] == closeData[i]))
				{
					linePositions_same[nLineIndex_same++] = fMid;
					linePositions_same[nLineIndex_same++] = highY;
					linePositions_same[nLineIndex_same++] = fMid;
					linePositions_same[nLineIndex_same++] = lowY;
					linePositions_same[nLineIndex_same++] = fStart;
					linePositions_same[nLineIndex_same++] = openY;
					linePositions_same[nLineIndex_same++] = fEnd;
					linePositions_same[nLineIndex_same++] = openY;
				}
				else
				{
					if(isFillUp2())
					{
						//addRectPosition(rectPositions_up2, nRectIndex_up2, fMid, highY, fMid+1, lowY);
						//2020. 06. 08 by hyh - 우측으로 쏠린 머리, 꼬리 위치 가운데로 조정. Heikin-Ashi >>
						addRectPosition(rectPositions_up2, nRectIndex_up2, fMid-(fLineWidth/2), highY, fMid+(fLineWidth/2), lowY);
						//2020. 06. 08 by hyh - 우측으로 쏠린 머리, 꼬리 위치 가운데로 조정. Heikin-Ashi <<
						nRectIndex_up2 += 4;
						if(isInverse()){
							addRectPosition(rectPositions_up2, nRectIndex_up2, fStart, openY, fEnd, closeY);
							nRectIndex_up2 += 4;
						}else{
							if(openY-closeY<1)
								openY+=fLineWidth;
							addRectPosition(rectPositions_up2, nRectIndex_up2, fStart, closeY, fEnd, openY);
							nRectIndex_up2 += 4;
						}
					}
					else
					{
						rectPositions_up2[nRectIndex_up2++]=fMid;
						rectPositions_up2[nRectIndex_up2++]=highY;
						rectPositions_up2[nRectIndex_up2++]=fMid;
						rectPositions_up2[nRectIndex_up2++]=closeY;
						rectPositions_up2[nRectIndex_up2++]=fMid;
						rectPositions_up2[nRectIndex_up2++]=openY;
						rectPositions_up2[nRectIndex_up2++]=fMid;
						rectPositions_up2[nRectIndex_up2++]=lowY;
						if(isInverse()){
							addEmptyRectPosition(rectPositions_up2, nRectIndex_up2, fStart, openY, fEnd, closeY);
							nRectIndex_up2 += 16;
						}else{
							addEmptyRectPosition(rectPositions_up2, nRectIndex_up2, fStart, closeY, fEnd, openY);
							nRectIndex_up2 += 16;
						}
					}
				}
			}else{
				linePositions_same[nLineIndex_same++] = fMid;
				linePositions_same[nLineIndex_same++] = highY;
				linePositions_same[nLineIndex_same++] = fMid;
				linePositions_same[nLineIndex_same++] = lowY;
				linePositions_same[nLineIndex_same++] = fStart;
				linePositions_same[nLineIndex_same++] = openY;
				linePositions_same[nLineIndex_same++] = fEnd;
				linePositions_same[nLineIndex_same++] = openY;
				//_cvm.drawLine(gl,x,highY,x,lowY, sameColor ,1.0f);
				//_cvm.drawLine(gl,(x-xw),openY,(x+xw),openY, sameColor ,1.0f);
			}

//			if(_cvm.bIsSignalChart) {
//			drawSignal(gl, (int)x, (int)highY, (int)lowY, i);
//			}

			if(highData[i]==getBongMax()){
				if(!maxset){
					drawMinMaxString(gl,(int)x,(int)highY,i,true);
					maxset=true;
				}
			}else if(lowData[i]==getBongMin()){
				if(!minset){
					drawMinMaxString(gl,(int)x,(int)lowY+2,i,false);
					minset=true;
				}
			}

//            //종가선 표시
//            y = calcy(data[data.length-1][3]);
//            _cvm.drawLine(gl,0, y, width, y, CoSys.LAST_LN ,1.0f);

			x+=xfactor;
		}
//        if(nLineIndex_up>0)
//        	_cvm.drawLines(gl, linePositions_up, upColor ,1.0f);
//        if(nLineIndex_down>0)
//        	_cvm.drawLines(gl, linePositions_down, downColor ,1.0f);
		if(nLineIndex_same>0)
			_cvm.drawLines(gl, linePositions_same, sameColor ,1.0f);
		if(nRectIndex_up>0)
		{
			float[] tmp = new float[nRectIndex_up];
			System.arraycopy(rectPositions_up,0,tmp,0,nRectIndex_up);
			if(isFillUp())
				_cvm.drawFillRects(gl, tmp, upColor ,1.0f);
			else
				_cvm.drawLines(gl, tmp, upColor ,1.0f);
		}

		if(nRectIndex_up2>0)
		{
			float[] tmp = new float[nRectIndex_up2];
			System.arraycopy(rectPositions_up2,0,tmp,0,nRectIndex_up2);
			if(isFillUp2())
				_cvm.drawFillRects(gl, tmp, upColor2 ,1.0f);
			else
				_cvm.drawLines(gl, tmp, upColor2 ,1.0f);
		}

		if(nRectIndex_down2>0)
		{   float[] tmp = new float[nRectIndex_down2];
			System.arraycopy(rectPositions_down2,0,tmp,0,nRectIndex_down2);
			if(isFillDown2())
				_cvm.drawFillRects(gl, tmp, downColor2 ,1.0f);
			else
				_cvm.drawLines(gl, tmp, downColor2 ,1.0f);
		}

		if(nRectIndex_down>0)
		{   float[] tmp = new float[nRectIndex_down];
			System.arraycopy(rectPositions_down,0,tmp,0,nRectIndex_down);
			if(isFillDown())
				_cvm.drawFillRects(gl, tmp, downColor ,1.0f);
			else
				_cvm.drawLines(gl, tmp, downColor ,1.0f);
		}

		//2011.09.27 by metalpooh  >> 기준선 추가 <<
		drawBaseLine(gl);
		//2019. 11. 26 by hyh - 라인차트에 기준선 그리기 >>
		drawPivotDemarkLine(gl);
		//2019. 11. 26 by hyh - 라인차트에 기준선 그리기 <<
		drawBuyAveragePriceLine(gl, (int)(x-xfactor/2)); //2021.02.18 by HJW - 매입평균선 추가
		//2015. 1. 22 목표가 전달 메인으로부터 값을 받아 목표가 선 하나 그리기
		drawYScalePriceLine_forPaint(gl);
	}

	//2020.11.23 by JJH >> 권리락/배당락 추가 start
	private void drawExRights(Canvas gl, float fX, float fY, double dType, double dRate) {
		if (_cvm.bIsLineFillChart) {
			return;
		}

		//======================
		// 텍스트
		//======================
		String strType = ChartUtil.getExRightString((int) dType);
		if(strType.equals("")) return;
//		double dCalculatedRate = dRate * 100 - 100;
//		String strText = strType + " (" + String.format("%.0f", dCalculatedRate) + "%)";
		String strText = strType + "(" + String.format("%.2f", dRate) + "%)";

		float fTextHeight = COMUtil.getPixel((int) 11.0f);
		//float fTextWidth = _cvm.getTextWidth(strText, fTextHeight);
		float fTextWidth = (int) _cvm.getFontWidth(strText, (int) COMUtil.getPixel(11));
		int[] textColor = {0, 183, 0};

		float fTopMargin = COMUtil.getPixel((int)20.0f);

		//========================
		// Draw Arrow
		//========================
		float fTriangleWidth = COMUtil.getPixel((int)8.0f);
		float fTriangleHeight = COMUtil.getPixel((int)-8.0f);
		float fGapFromBong = COMUtil.getPixel((int)1.0f);
		float fMinimumGap = COMUtil.getPixel((int)10.0f);

		//화살표 출력 영역이 모자라면 화살표 크기를 반으로 축소
		if (fTopMargin + fTextHeight + Math.abs(fTriangleHeight) + fMinimumGap >= fY) {
			fTriangleWidth = fTriangleWidth / 2;
			fTriangleHeight = fTriangleHeight / 2;
			fGapFromBong = fGapFromBong / 2;
		}

		//Triangle & Line Color
		int[] arrowColor = {0, 183, 0};
		_cvm.drawFillTriangle(gl, fX - fTriangleWidth / 2, fY - Math.abs(fTriangleHeight) - fGapFromBong, fTriangleWidth, fTriangleHeight, arrowColor);

		//선이 문자열 하단으로만 그려지도록 설정
		float fTextBottomY = fTopMargin + fTextHeight;
		float fTriangleTopY = fY - Math.abs(fTriangleHeight) - fGapFromBong;

		if (fTriangleTopY - fTextBottomY > 0) {
			fTextBottomY += (fTriangleTopY - fTextBottomY) / 3;
			fTopMargin = fTextBottomY - fTopMargin/2;
		}

		_cvm.drawStringWithSize(gl, textColor, (int) (fX - fTextWidth / 2.0f), (int) fTopMargin, fTextHeight, strText);

		if (fTextBottomY < fTriangleTopY) {
			_cvm.drawLine(gl, fX, (int) fTextBottomY, fX, fTriangleTopY, arrowColor, 1.0f);
		}
	}
	//2020.11.23 by JJH >> 권리락/배당락 추가 end

	//2021.02.09 by LYH >> 현재가 라인 표시 >>
	private void drawPriceLine(Canvas gl, int xpos, String strStandard, float fEnd)
	{
		double dPrice = _cdm.getLastData("종가");
		int[] color = new int[3];
		double dStandard = Double.parseDouble(strStandard);
		if(fEnd>0) {
			int yPircePos = (int) calcy(dPrice);

			if ((yPircePos >= min_view && yPircePos <= max_view)) {

				if (dPrice > dStandard) {//상승
					color = CoSys.CHART_COLORS[0];
				} else if (dPrice < dStandard) { //하락
					color = CoSys.CHART_COLORS[1];
				} else {
					return;
				}
				_cvm.setLineWidth_Fix(COMUtil.getPixel_H(0.5f));
				_cvm.drawLine(gl, getBounds().left, yPircePos, fEnd, yPircePos, color, 0.5f);
			}
		}
		else
		{
			int yPircePos = (int) calcy(dStandard);
			int[] GRAY = {221, 221, 221};
			_cvm.setLineWidth_Fix(COMUtil.getPixel_H(0.5f));
			_cvm.drawLine(gl, getBounds().left, yPircePos, xpos, yPircePos, GRAY, 1.0f);
		}
	}
	//2021.02.09 by LYH >> 현재가 라인 표시 <<

	//2021.02.18 by HJW - 매입평균선 추가 >>
	private void drawBuyAveragePriceLine(Canvas gl, int xpos)
	{
		//2024.01.04 by SJW - 구매평균선 방어코드 추가 >>
//		if(_cdm.strAvgBuyPrice == null || _cdm.strAvgBuyPrice.equals("")) return;
		if(!_cvm.isAvgBuyPriceFunc || _cdm.strAvgBuyPrice == null || _cdm.strAvgBuyPrice.equals("")) return;
		//2024.01.04 by SJW - 구매평균선 방어코드 추가 <<
		double dPrice = Double.parseDouble(_cdm.strAvgBuyPrice);
		int yPircePos=(int)calcy(dPrice);
		if((yPircePos>=min_view&&yPircePos<=max_view)){

			int[] color =  {85, 85, 85};
			//2021.06.22 by hanjun.Kim - kakaopay - 구매평균선. 구매평균 글자 이후부터 그려지도록 수정 >>
			String strbuyprice = "내 평균";
//			_cvm.showLastDataLine(gl, (int)getBounds().left + _cvm.GetTextLength(strbuyprice), yPircePos, xpos , color, true);
			//2021.07.12 by hanjun.Kim - kakaopay - 구매평균선 끝까지 그려질수있도록 수정.
			//2024.01.04 by SJW - 구매평균선 x좌표 수정 >>
//			_cvm.showLastDataLine(gl, (int)getBounds().left + _cvm.GetTextLength(strbuyprice), yPircePos, (int) _cvm.getBounds().right, color, true);
			_cvm.showLastDataLine(gl, (int)getBounds().left + _cvm.GetTextLength(strbuyprice)+(int)COMUtil.getPixel(8), yPircePos, (int) _cvm.getBounds().right, color, true);
			//2024.01.04 by SJW - 구매평균선 x좌표 수정 <<
		}
	}
	//2021.02.18 by HJW - 매입평균선 추가 <<
}
