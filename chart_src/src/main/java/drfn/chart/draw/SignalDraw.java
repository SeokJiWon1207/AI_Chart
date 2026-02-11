package drfn.chart.draw;

import android.graphics.Canvas;

import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

public class SignalDraw extends DrawTool{
    int type=0;
    public SignalDraw(ChartViewModel cvm, ChartDataModel cdm){
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

    }

    public void draw(Canvas gl, double[] data){
    }
    public void draw(Canvas gl, double[][] data){
    }
    public void draw(Canvas gl, double[][] data, double[] stand){
    }
}