package drfn.chart.draw;

import android.graphics.Canvas;

import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;
import drfn.chart.util.COMUtil;
import drfn.chart.util.CoSys;
/**
 삼선전환도 차트를 그리기 위한 드로우 툴
 */
public class ThirdChangeDraw extends DrawTool{
    int type=0;
    boolean isdot;

    public ThirdChangeDraw(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm, cdm);
        setIndex(getDrawType2());
    }
    public void setDotLine(boolean b){
        isdot = b;
    }
    public void setIndex(int idx){
        type = idx;
    }
    public void draw(Canvas gl, double y){
    }
    public void draw(Canvas gl, double[] stand){
    }
    public void drawDefault(Canvas gl, double[] data){
    }
    public void drawVolumeForSale(Canvas gl, double[] stand){//대기매물용	
    }
    public void draw(Canvas gl, double[] data, double[] stand){
    }
    public void draw(Canvas gl, double[][] data){//기준가 없이 그리는 바
        if(data == null)
            return;

        int viewH = 20;
        float dataX = getBounds().left;
        int dLen = data.length;
        float dataWidth = ((float)(getBounds().width())/(float)(dLen));
        float y1, y2;

        if(dataWidth==0) {
            dataWidth = 1;
        }

        for(int d=0 ; d< dLen ; d++) {
            y1 = calcy(data[d][1]);
            y2 = calcy(data[d][2]);
            if(y2<0) {
                continue;
            }
            dataX = getBounds().left+d*dataWidth;
            if(data[d][0] == 1) {
                _cvm.drawFillRect(gl,dataX, y2, dataWidth, y1-y2, CoSys.RED, 1.0f);
//	           dataX += dataWidth;
            }else if(data[d][0] == -1){
                _cvm.drawFillRect(gl,dataX, y2, dataWidth, y1-y2, CoSys.BLUE, 1.0f);
//	            dataX += dataWidth;
            }
        }

        //2014. 3. 27 독립차트에서 삼선전환도 이외에 차트유형의 오른쪽 데이터박스가 위로 튀는 현상>>
//	    _cvm.drawFillRect(gl, getBounds().right-(int)COMUtil.getPixel(80),COMUtil.getPixel(20),(int)COMUtil.getPixel(80),COMUtil.getPixel(viewH), CoSys.DKGRAY, 0.5f);
//        _cvm.drawFillRect(gl, getBounds().right-(int)COMUtil.getPixel(80),COMUtil.getPixel(10)+_cvm.Margin_T,(int)COMUtil.getPixel(80),COMUtil.getPixel(viewH), CoSys.DKGRAY, 0.5f);
//        //2014. 3. 27 독립차트에서 삼선전환도 이외에 차트유형의 오른쪽 데이터박스가 위로 튀는 현상<<
//
//        //_cvm.drawFillRect(gl, getBounds().right-145,10+viewH,145,50+10+viewH, CoSys.BLACK, 0.5f);
//
//        int unit = 3;
//
//        //2014. 3. 27 독립차트에서 삼선전환도 이외에 차트유형의 오른쪽 데이터박스가 위로 튀는 현상>>
////    	_cvm.drawString(gl, CoSys.WHITE, getBounds().right-(int)COMUtil.getPixel(75),(int)COMUtil.getPixel(30), "칸 전환 : "+unit);       //1칸의 값.
//        _cvm.drawString(gl, CoSys.WHITE, getBounds().right-(int)COMUtil.getPixel(75),(int)COMUtil.getPixel(20)+_cvm.Margin_T, "칸 전환 : "+unit);       //1칸의 값.
        //2014. 3. 27 독립차트에서 삼선전환도 이외에 차트유형의 오른쪽 데이터박스가 위로 튀는 현상<<
    }
    public void draw(Canvas gl, double[][] data, double[] stand){
    }
}