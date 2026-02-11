package drfn.chart.draw;

import android.graphics.Canvas;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;
import drfn.chart.util.CoSys;

public class SpecialLineDraw extends DrawTool{
    int type=0;
    public SpecialLineDraw(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm, cdm);
        line_thick = 1;
    }
    public void setIndex(int idx){
    }
    public void draw(Canvas gl, double data){//기준가 없이 그리는 바
    }
    public void drawDefault(Canvas gl, double[] data){
    }
    public void drawVolumeForSale(Canvas gl, double[] stand){//대기매물용	
    }

    //2016.07.28 by LYH >> 일목균형 선행스팬1, 선행스팬2 라인 굵기 색상 적용.
    public void draw(Canvas gl, double[] data, double[] data1){
        if(data==null)return;
        float x=getBounds().left+xw;
        float sp1 =0, sp2=0;//기준가의 픽셀좌표를 얻는다
        float sp11=0;
        float sp22=0;

        x=getBounds().left+xw;
        int dLen = data.length-1;

        //2021.05.24 by hhk - 구름대 채우기 >>
        int totLen = dLen*4;

        if(dLen==1) {
            totLen = 4;
        }
        // = new float[dataLen];
        float[] positionsHigh = new float[totLen];// = new float[dataLen];
        float[] positionsLow = new float[totLen];// = new float[dataLen];
        int nIndex = 0;
        int nLastIndex = -1;
        //2021.05.24 by hhk - 구름대 채우기 <<

        for(int i=0;i<dLen;i++){
            sp1 = calcy(data[i]);
            sp2 = calcy(data1[i]);
            sp11 = calcy(data[i+1]);
            sp22 = calcy(data1[i+1]);
            if(data[i]!=0 && data1[i]!=0){
                //_cvm.drawLine(gl, (int)x,sp1,(int)x,sp2, color ,0.5f);
                float yPos1 = sp1;
                float yPos2 = sp2;
                if(yPos1<min_view)
                    yPos1 = min_view;
                if(yPos2<min_view)
                    yPos2 = min_view;
                if(yPos1>max_view)
                    yPos1 = max_view;
                if(yPos2>max_view)
                    yPos2 = max_view;

                //2021.05.24 by hhk - 구름대 채우기 >>
                float yPosHigh = sp1;
                float yPosLow = sp2;
                float yPosHigh1 = sp11;
                float yPosLow1 = sp22;
                if(yPosHigh<min_view)
                    yPosHigh = min_view;
                if(yPosLow<min_view)
                    yPosLow = min_view;
                if(yPosHigh>max_view)
                    yPosHigh = max_view;
                if(yPosLow>max_view)
                    yPosLow = max_view;
                if(yPosHigh1<min_view)
                    yPosHigh1 = min_view;
                if(yPosLow1<min_view)
                    yPosLow1 = min_view;
                if(yPosHigh1>max_view)
                    yPosHigh1 = max_view;
                if(yPosLow1>max_view)
                    yPosLow1 = max_view;
                //2021.05.24 by hhk - 구름대 채우기 <<

                _cvm.setLineWidth(1);
                //2017.01.03 by LYH >> 일목균형 선행스팬1, 2 타이틀 개선
                //if(getTitle().equals("선행스팬1") && sp1>sp2 || getTitle().equals("선행스팬2") && sp1<=sp2)
                if(getTitle().equals("선행스팬1") && sp1<=sp2 || getTitle().equals("선행스팬2") && sp1>sp2)
                //2017.01.03 by LYH >> 일목균형 선행스팬1, 2 타이틀 개선 end
                {
                    //2021.05.24 by hhk - 구름대 채우기 >>
                    //_cvm.drawLine(gl, (int)x,yPos1,(int)x,yPos2, upColor ,0.5f);
                    if(!isFillCloud()) {
                        _cvm.drawLine(gl, (int)x,yPos1,(int)x,yPos2, upColor ,0.5f);
                    } else {
                        if(i - nLastIndex  > 1 && nLastIndex != -1) {
                            _cvm.drawLineWithFillGradient_Flow(gl, positionsHigh, positionsLow, upColor, 127, nIndex);
                            nIndex = 0;
                        }
                        else
                        {
                            nLastIndex = i;
                        }
                        positionsHigh[nIndex] = x;
                        positionsLow[nIndex++] = x;
                        positionsHigh[nIndex] = yPosHigh;
                        positionsLow[nIndex++] = yPosLow;
                        positionsHigh[nIndex] = x+xfactor;
                        positionsLow[nIndex++] = x+xfactor;
                        positionsHigh[nIndex] = yPosHigh1;
                        positionsLow[nIndex++] = yPosLow1;
                    }
                    //2021.05.24 by hhk - 구름대 채우기 <<
                }
            }
            _cvm.setLineWidth(line_thick);
            //2017.01.03 by LYH >> 일목균형 선행스팬1, 2 타이틀 개선
            //if(getTitle().equals("선행스팬1"))
            if(getTitle().equals("선행스팬2"))
            //2017.01.03 by LYH >> 일목균형 선행스팬1, 2 타이틀 개선 end
            {
                if((sp2>=min_view-1) && (sp22 >= min_view-1) && (sp2<=max_view) && (sp22 <= max_view)) {
                    _cvm.drawLine(gl, (int)x,sp2,(int)(x+xfactor),sp22, upColor ,0.5f);
                }
            }
            else
            {
                if((sp1>=min_view-1) && (sp11 >= min_view-1) && (sp1<=max_view) && (sp11 <= max_view)) {
                    _cvm.drawLine(gl,(int)x,sp1,(int)(x+xfactor),sp11, upColor ,0.5f);
                }
            }
            x+=xfactor;
        }
        _cvm.drawLineWithFillGradient_Flow(gl, positionsHigh, positionsLow, upColor, 127, nIndex);  //2021.05.24 by hhk - 구름대 채우기
        _cvm.setLineWidth(1);
    }

//
//    public void draw(Canvas gl, double[] data, double[] data1){
//        if(data==null)return;
//        float x=getBounds().left+xw;
//        float sp1 =0, sp2=0;//기준가의 픽셀좌표를 얻는다
//        float sp11=0;
//        float sp22=0;
//
//        x=getBounds().left+xw;
//        int dLen = data.length-1;
//        int[] color = null;
//        for(int i=0;i<dLen;i++){
//            sp1 = calcy(data[i]);
//            sp2 = calcy(data1[i]);
//            sp11 = calcy(data[i+1]);
//            sp22 = calcy(data1[i+1]);
//            if(data1[i]!=0){
//                if(sp1>sp2) {
//                    color = CoSys.CHART_COLORS[0];
//                } else {
//                    color = CoSys.CHART_COLORS[1];
//                }
//                if((sp2>=min_view-1) && (sp22 >= min_view-1) && (sp2<=max_view) && (sp22 <= max_view)) {
//                    _cvm.drawLine(gl, (int)x,sp2,(int)(x+xfactor),sp22, color ,0.5f);
//                }
//                if(data[i]!=0){
//                    //_cvm.drawLine(gl, (int)x,sp1,(int)x,sp2, color ,0.5f);
//                    int yPos1 = (int)sp1;
//                    int yPos2 = (int)sp2;
//                    if(yPos1<min_view)
//                        yPos1 = min_view;
//                    if(yPos2<min_view)
//                        yPos2 = min_view;
//                    if(yPos1>max_view)
//                        yPos1 = max_view;
//                    if(yPos2>max_view)
//                        yPos2 = max_view;
////                    _cvm.drawLine(gl, (int)x,yPos1,(int)x,yPos2, color ,0.5f);
////                    if((sp1>=min_view-1) && (sp11 >= min_view-1) && (sp1<=max_view) && (sp11 <= max_view)) {
////                        _cvm.drawLine(gl,(int)x,sp1,(int)(x+xfactor),sp11, CoSys.CHART_COLORS[1] ,0.5f);
////                    }
//                }
//            }
//            x+=xfactor;
//        }
//    }
    //    public void draw(Canvas gl, double[] data, double[] data1){
//        if(data==null)return;
//        float x=getBounds().left+xw;
//        float sp1 =0, sp2=0;//기준가의 픽셀좌표를 얻는다
//        float sp11=0;
//        float sp22=0;
//        
//        x=getBounds().left+xw;
//        int dLen = data.length;
//        int[] color = null;
//        for(int i=0;i<dLen-1;i++){
//            sp1 = calcy(data[i]);
//            sp2 = calcy(data1[i]);
//            sp11 = calcy(data[i+1]);
//            sp22 = calcy(data1[i+1]);
//            if(data1[i]!=0){
//            	if(sp1>sp2) {
//            		color = upColor;
//            	} else {
//            		color = downColor;
//            	}
//                _cvm.drawLine(gl, (int)x,sp2,(int)(x+xfactor),sp22, color ,0.5f);
//                if(data[i]!=0){
//                	_cvm.drawLine(gl, (int)x,sp1,(int)x,sp2, upColor ,0.5f);
//                	_cvm.drawLine(gl,(int)x,sp1,(int)(x+xfactor),sp11, upColor ,0.5f);
//                }
//            }
//            x+=xfactor;            
//        }
//    }
    public void draw(Canvas gl, double[] data){
    }
    public void draw(Canvas gl, double[][] data){
    }
    public void draw(Canvas gl, double[][] data, double[] stand){
    }
}