package drfn.chart.draw;

import android.graphics.Canvas;

import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;
import drfn.chart.util.COMUtil;
import drfn.chart.util.MinMax;

/**
 Kagi 차트를 그리기 위한 드로우 툴
 */
public class ReverseClockDraw extends DrawTool{
    int type=0;
    boolean isdot;

    public ReverseClockDraw(ChartViewModel cvm, ChartDataModel cdm){
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
        if(data==null)return;
        this.data = data;
        double[] volData = _cdm.getSubPacketData(this.getTitle()+"_거래량");
        String[] dates = _cdm.getStringData("자료일자");
        double max_data=MinMax.getIntMaxT(volData);
        double min_data=MinMax.getIntMinT(volData);
        
        int startIndex = _cvm.getIndex();
        int startPos=startIndex;
        if(startPos<0)
            startPos = 0;

        int dataLen = startIndex + _cvm.getViewNum() + _cvm.futureMargin;
        if(dataLen>data.length)
            dataLen= data.length;

        //지표가 후행된경우 초기의 데이터가 비워져서 생긴 0데이터 처리를 위해 
        //처음에 데이터가 0인곳은 무시하고 실제 데이터를 잡아내는 부분을 추가
        //if(!this.getShowZeroValue()) {
        if(!_cvm.bInvestorChart &&  _cvm.chartType != COMUtil.COMPARE_CHART) {
            for (int i = startPos; i < dataLen; i++) {
                if (dataLen > i && data[i] != 0) {
                    startPos = i;
//                    xpos = (int) (xpos + ((i - startIndex) * xfactor));
                    break;
                }
            }
        }
        //}
        if(startPos>=data.length)
            return;

        float y = calcy(data[startPos]);
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
//        for(int i=dataLen-1;i>=startPos;i--){

            if(data[i]==0)
            {
                continue;
            }
            y1 = calcy(data[i]);
            x1 = calcx(volData[i], max_data, min_data);
            if((y>=min_view-1&&y1>=min_view-1) && (y<max_view+1&&y1<max_view+1)){
            	if(i<dataLen-1)
            		_cvm.drawLine(gl, x,y,x1,y1, upColor ,1.0f);
            	
            	String strData = "시작:"+_cdm.getFormatData("자료일자", i);
            	
            	if(this.getBounds().width()-x < _cvm.getFontWidth(strData, 10)) {	//2015. 1. 16 차트화면 고저가 문자열 날짜가 yscale영역 침범
        			sx2 = x1 - _cvm.GetTextLength(strData) - (int)COMUtil.getPixel(13);

        		}
        		else
        		{
        			sx2 = x + (int)COMUtil.getPixel(12);

        		}
  
                if(i==startPos+1) { //시작
                	_cvm.drawFillRect(gl, x-3, y-2, (int)COMUtil.getPixel(5), (int)COMUtil.getPixel(5), upColor, 1.0f);
//                	String strData = "시작:"+_cdm.getFormatData("자료일자", i);
                	_cvm.drawString(gl, upColor, (int)sx2,  (int)y, strData);
                } else if(i==dataLen-1) {//종료
                	_cvm.drawFillRect(gl, x-3, y-2, (int)COMUtil.getPixel(5), (int)COMUtil.getPixel(5), upColor, 1.0f);
                	String strData2 = "종료:"+_cdm.getFormatData("자료일자", i);
                	_cvm.drawString(gl, upColor,  (int)sx2,  (int)y, strData2);
                }
  
            }
            y=y1; 
            x=x1;
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