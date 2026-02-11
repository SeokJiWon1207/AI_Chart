package drfn.chart.draw;

import android.graphics.Canvas;

import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;
import drfn.chart.util.COMUtil;
import drfn.chart.util.CoSys;
import drfn.chart.util.MinMax;
/**
 삼선전환도 차트를 그리기 위한 드로우 툴
 */
public class RenkoDraw extends DrawTool{
    int type=0;
    boolean isdot;

    public RenkoDraw(ChartViewModel cvm, ChartDataModel cdm){
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
        if(data==null) return;
        int viewH = (int)COMUtil.getPixel(20);
        //2021.11.01 by JHY - 렌코차트 박스 겹치는 부분 수정 >>
//        int viewH = (int)COMUtil.getPixel(17);
        //2021.11.01 by JHY - 렌코차트 박스 겹치는 부분 수정 <<
        float dataX = getBounds().left;
        int dLen = data.length;
        float dataWidth = ((float)(getBounds().width())/(float)(dLen));
        float dataWidthForX = dataWidth;
        float y1, y2;

//	    System.out.println("Renko dataWidth:"+dataWidth);

        if(dataWidth<1) {
            dataWidth = 1;
        }

        for(int d=0 ; d< dLen ; d++) {
            y1 = calcy(data[d][1]);
            y2 = calcy(data[d][2]);
            if(y2<0) {
                continue;
            }
            dataX = getBounds().left+(int)(d*dataWidthForX);
            if(data[d][0] == 1) {
                //2023.06.02 by SJW - 다크테마 차트 색상 변경 >>
//                _cvm.drawFillRect(gl, dataX, y2, dataWidth, y1 - y2, CoSys.STANDGRAPH_BASE_COLOR, 1.0f);
                if(_cvm.getSkinType() == COMUtil.SKIN_BLACK) {
                    _cvm.drawFillRect(gl, dataX, y2, dataWidth, y1 - y2, CoSys.CHART_COLOR_UP_NIGHT, 1.0f);
                } else {
                    _cvm.drawFillRect(gl, dataX, y2, dataWidth, y1 - y2, CoSys.STANDGRAPH_BASE_COLOR, 1.0f);
                }
                //2023.06.02 by SJW - 다크테마 차트 색상 변경 <<
//	           dataX += dataWidth;
            }else if(data[d][0] == -1){
                //2023.06.02 by SJW - 다크테마 차트 색상 변경 >>
//                _cvm.drawFillRect(gl,dataX, y2, dataWidth, y1-y2, CoSys.STANDGRAPH_BASE_COLOR1, 1.0f);
                if(_cvm.getSkinType() == COMUtil.SKIN_BLACK) {
                    _cvm.drawFillRect(gl, dataX, y2, dataWidth, y1 - y2, CoSys.CHART_COLOR_DOWN_NIGHT, 1.0f);
                } else {
                    _cvm.drawFillRect(gl, dataX, y2, dataWidth, y1 - y2, CoSys.STANDGRAPH_BASE_COLOR1, 1.0f);
                }
                //2023.06.02 by SJW - 다크테마 차트 색상 변경 <<
//	            dataX += dataWidth;
            }
        }

        double[] highData = _cdm.getSubPacketData("고가");
        double[] lowData = _cdm.getSubPacketData("저가");
        double max_data=MinMax.getIntMaxT(highData);
        double min_data=MinMax.getIntMinT(lowData);
        //int unit = (int)(((max_data-min_data)*2.36)/50);
        //int unit = (int)(max_data-min_data)/23/2;
        int unit = (int)((max_data-min_data)*0.02);
//        int unit = (int)((max_data-min_data)*0.023);
//        double blockRate = 40/(max_data-min_data);
//        int unit = (int)((max_data-min_data)*blockRate);

        //2014. 3. 27 독립차트에서 삼선전환도 이외에 차트유형의 오른쪽 데이터박스가 위로 튀는 현상>>
//      _cvm.drawFillRect(gl, getBounds().right-(int)COMUtil.getPixel(75),(int)COMUtil.getPixel(10),(int)COMUtil.getPixel(75),viewH, CoSys.DKGRAY, 0.5f);
//        _cvm.drawFillRect(gl, getBounds().right-(int)COMUtil.getPixel(75),(int)COMUtil.getPixel(10)+_cvm.Margin_T,(int)COMUtil.getPixel(75),viewH, CoSys.DKGRAY, 0.5f);
        //2014. 3. 27 독립차트에서 삼선전환도 이외에 차트유형의 오른쪽 데이터박스가 위로 튀는 현상<<

        //2021.11.01 by JHY - 렌코차트 박스 겹치는 부분 수정 >>
        _cvm.drawFillRect(gl, getBounds().right-(int)COMUtil.getPixel(50),(int)COMUtil.getPixel(03)+_cvm.Margin_T,(int)COMUtil.getPixel(57),viewH, CoSys.DKGRAY, 0.5f);
        //2021.11.01 by JHY - 렌코차트 박스 겹치는 부분 수정 <<

        //_cvm.drawFillRect(gl, getBounds().right-145,10+viewH,145,50+10+viewH, CoSys.BLACK, 0.5f);

        //2014. 3. 27 독립차트에서 삼선전환도 이외에 차트유형의 오른쪽 데이터박스가 위로 튀는 현상>>
//    	_cvm.drawString(gl, CoSys.WHITE, getBounds().right-(int)COMUtil.getPixel(70),viewH, "BOX : "+unit);       //1칸의 값.
//        _cvm.drawString(gl, CoSys.WHITE, getBounds().right-(int)COMUtil.getPixel(70),(int)COMUtil.getPixel(20)+_cvm.Margin_T, "BOX : "+unit);       //1칸의 값.
        //2014. 3. 27 독립차트에서 삼선전환도 이외에 차트유형의 오른쪽 데이터박스가 위로 튀는 현상<<

        //2021.11.01 by JHY - 렌코차트 박스 겹치는 부분 수정 >>
        _cvm.drawString(gl, CoSys.WHITE, getBounds().right-(int)COMUtil.getPixel(45),(int)COMUtil.getPixel(13)+_cvm.Margin_T, "BOX : "+unit);       //1칸의 값.
        //2021.11.01 by JHY - 렌코차트 박스 겹치는 부분 수정 <<

    }
    public void draw(Canvas gl, double[][] data, double[] stand){
    }
}