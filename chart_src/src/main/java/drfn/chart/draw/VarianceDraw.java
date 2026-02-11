package drfn.chart.draw;

import android.graphics.Canvas;

import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;
import drfn.chart.util.COMUtil;
import drfn.chart.util.MinMax;

/**
 Kagi 차트를 그리기 위한 드로우 툴
 */
public class VarianceDraw extends DrawTool{
    int type=0;
    boolean isdot;

    public VarianceDraw(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm, cdm);
        setIndex(getDrawType2());
        line_thick = 1;
    }
    public void setDotLine(boolean b){
        isdot = b;
    }
    public void setIndex(int idx){
        type = idx;
    }

    //int viewH = 20;
    public void draw(Canvas gl, double[] data){
//        int viewH = (int)COMUtil.getPixel(20);
//        if(data==null)return;
//        this.data = data;
        double[] closeData = _cdm.getSubPacketData("종가");
        double[] volData = _cdm.getSubPacketData("기본거래량");
        double[] openData = _cdm.getSubPacketData("시가");
        String[] dates = _cdm.getStringData("자료일자");
        double max_data=MinMax.getIntMaxT(volData);
        double min_data=MinMax.getIntMinT(volData);

        int startIndex = _cvm.getIndex();
        int startPos=startIndex;
        if(startPos<0)
            startPos = 0;

        int dataLen = startIndex + _cvm.getViewNum() + _cvm.futureMargin;
        if(dataLen>closeData.length)
            dataLen= closeData.length;

        if(startPos>=closeData.length)
            return;

        float y = calcy(closeData[startPos]);
        float x = calcx(volData[startPos], max_data, min_data);
        float y1 = 0;
        float x1 = 0;
        float sy1 = 0;
        float sx1 = 0;
        float sy2 = 0;
        float sx2 = 0;

        int totLen = (dataLen-startPos-1)*4;
        float[] positions = new float[totLen];
        int nIndex = 0;
        int nWidth = (int)(xw*2);
        _cvm.setLineWidth(line_thick);
        for(int i=startPos+1;i<dataLen;i++){

            if(closeData[i]==0)
            {
                continue;
            }
            y1 = calcy(closeData[i]);
            x1 = calcx(volData[i], max_data, min_data);

            if(openData[i] < closeData[i])
            {
                _cvm.drawCircle(gl, (int)x1-(int)COMUtil.getPixel(2), (int)y1-(int)COMUtil.getPixel(2), (int)x1+(int)COMUtil.getPixel(2),(int)y1+(int)COMUtil.getPixel(2), true, upColor);
            }
            else
            {
                _cvm.drawCircle(gl, (int)x1-(int)COMUtil.getPixel(2), (int)y1-(int)COMUtil.getPixel(2), (int)x1+(int)COMUtil.getPixel(2),(int)y1+(int)COMUtil.getPixel(2), true, downColor);
            }
        }

    }
	@Override
	public void draw(Canvas gl, double data) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void drawDefault(Canvas gl, double[] data) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void draw(Canvas gl, double[] data, double[] stand) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void draw(Canvas gl, double[][] data) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void draw(Canvas gl, double[][] data, double[] stand) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void drawVolumeForSale(Canvas gl, double[] stand) {
		// TODO Auto-generated method stub
		
	}
}