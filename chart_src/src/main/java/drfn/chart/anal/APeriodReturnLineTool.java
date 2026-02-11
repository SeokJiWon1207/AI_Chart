package drfn.chart.anal;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

import drfn.chart.block.Block;
import drfn.chart.util.COMUtil;
import drfn.chart.util.ChartUtil;
import drfn.chart.util.CoSys;
import drfn.chart.util.DoublePoint;
public class APeriodReturnLineTool extends AnalTool{

    public APeriodReturnLineTool(Block ac){
        super(ac);
        ncount = 2;
        data = new DoublePoint[ncount];
        data_org = new DoublePoint[ncount];
    }
    public void draw(Canvas g){
        if(_ac==null) return;
        in = _ac.getGraphBounds();
        out=_ac._cvm.getBounds();
        _ac._cvm.setLineWidth(line_t);

        if(data[1]==null)return;
        int xIndex=getIndexWithDate(data[0].x);
        float x = getDateToX(xIndex);
        float y = priceToY(data[0].y);

        if (y<0 || y>in.height()) {
            return;
        }

        int xIndex1=getIndexWithDate(data[1].x);
        float x1 = getDateToX(xIndex1);
//        int y1 = priceToY(data[1].y);

        g.save();
        g.clipRect(in.left ,in.top, in.left+in.width(), in.top+in.height());

        //g.glLineWidth(line_t);
        //추세라인.

        _ac._cvm.drawLine(g,x,y,x1,y,at_col ,1.0f);

        String strStartLow = _ac._cdm.getData("저가", xIndex);
        String strStartHigh = _ac._cdm.getData("고가", xIndex);
        String strStartClose = _ac._cdm.getData("종가", xIndex);
        String strEndLow = _ac._cdm.getData("저가", xIndex1);
        String strEndHigh = _ac._cdm.getData("고가", xIndex1);
        String strEndClose = _ac._cdm.getData("종가", xIndex1);
        String strChgrate = "";
        double dChange = 0;
        try
        {
            float nPosStartLow = priceToY(Double.parseDouble(strStartLow));
            float nPosStartHigh = priceToY(Double.parseDouble(strStartHigh));
            float nPosEndLow = priceToY(Double.parseDouble(strEndLow));
            float nPosEndHigh = priceToY(Double.parseDouble(strEndHigh));

            double price = Double.parseDouble(strEndClose);
            double prePrice = Double.parseDouble(strStartClose);

            dChange = price - prePrice;
            double chgrate = 0;
            if(prePrice != 0)
                chgrate = dChange*100/prePrice;
            strChgrate = String.format("%.2f%%", chgrate);

            if(nPosStartLow<y)
            {
                _ac._cvm.drawLine(g,x,y,x,nPosStartLow+COMUtil.getPixel(2), at_col ,1.0f);
            }
            else if(nPosStartHigh>y)
            {
                _ac._cvm.drawLine(g,x,y,x,nPosStartHigh-COMUtil.getPixel(2), at_col ,1.0f);
            }

            if(nPosEndLow<y)
            {
                _ac._cvm.drawLine(g,x1,y,x1,nPosEndLow+COMUtil.getPixel(2), at_col ,1.0f);
            }
            else if(nPosEndHigh>y)
            {
                _ac._cvm.drawLine(g,x1,y,x1,nPosEndHigh-COMUtil.getPixel(2), at_col ,1.0f);
            }

            drawPeriodData(g,(int)x,(int)x1,(int)y,xIndex,xIndex1, dChange, strChgrate);
        }
        catch(Exception e)
        {
        }

        //양끝점표시.
        _ac._cvm.drawFillRect(g, x-5, y-2, 5, 5, at_col, 1.0f);
        _ac._cvm.drawFillRect(g, x1-5, y-2, 5, 5, at_col, 1.0f);

        if(isSelect){
            _ac._cvm.setLineWidth(rectLine_t);
//            _ac._cvm.drawRect(g, x-2-selectAreaWidth/2, y-2-selectAreaWidth/2, selectAreaWidth, selectAreaWidth, CoSys.at_col);
//            _ac._cvm.drawRect(g, x1-2-selectAreaWidth/2, y-2-selectAreaWidth/2, selectAreaWidth, selectAreaWidth, CoSys.at_col);
            drawSelectedPointRect(g, x, y);
            drawSelectedPointRect(g, x1, y);
//            drawSelectedPointData(g,(int)x,(int)y,data[0].x,""+data[0].y);
//            drawSelectedPointData(g,(int)x1,(int)y1,data[1].x,""+data[1].y);

        }
        g.restore();
    }
    public boolean isSelected(PointF p){
        if(data[1]==null)return false;
        float x = dateToX(data[0].x);
        float x1 = dateToX(data[1].x);
        float y = priceToY(data[0].y);
//        int y1 = priceToY(data[1].y);

        RectF bound1=new RectF(x-selectAreaWidth/2, y-selectAreaWidth/2, x-selectAreaWidth/2+selectAreaWidth, y-selectAreaWidth/2+selectAreaWidth);
        RectF bound2=new RectF(x1-selectAreaWidth/2, y-selectAreaWidth/2, x1-selectAreaWidth/2+selectAreaWidth, y-selectAreaWidth/2+selectAreaWidth);
        if(bound1.contains(p.x, p.y)){
            select_type=1;
            return true;
        }else if(bound2.contains(p.x, p.y)){
            select_type=2;
            return true;
        }else if(isSelectedLine(p,x,y,x1,y,false)){
            select_type=0;
            return true;
        } else {
            return false;
        }
    }

    protected void drawPeriodData(Canvas g, int sxStart, int sxEnd, int sy,int idxStart,int idxEnd, double dChange, String strChgRate){
        if(_ac == null)
            return;
        bounds = _ac.getGraphBounds();
        int num = _ac._cdm.getCount();
        idxStart=(idxStart<0)?0:(idxStart>num-1)?num-1:idxStart;
        String dateStart = _ac._cdm.getFormatData("자료일자", idxStart);
        if(dateStart.length()>5)
        {
            dateStart = dateStart.substring(dateStart.length()-5);
        }

        idxEnd=(idxEnd<0)?0:(idxEnd>num-1)?num-1:idxEnd;
        String dateEnd = _ac._cdm.getFormatData("자료일자", idxEnd);
        if(dateEnd.length()>5)
        {
            dateEnd = dateEnd.substring(dateEnd.length()-5);
        }

        if(dataBuf.length()>0) dataBuf.delete(0, dataBuf.length());

        int nBongCount = idxEnd-idxStart;
        dataBuf.append(Math.abs(nBongCount)+1);
        dataBuf.append("봉");
        dataBuf.append("(");
        dataBuf.append(dateStart);
        if(nBongCount>0)
            dataBuf.append("->");
        else
            dataBuf.append("<-");
        dataBuf.append(dateEnd);
        dataBuf.append(")");
        String strChange;
        strChange = ChartUtil.getFormatedData(Math.abs(dChange), _ac._cdm.getPriceFormat(), _ac._cdm);

        if(nBongCount>0)
        {
            _ac._cvm.drawString(g, at_col, sxStart+5, sy-(int)COMUtil.getPixel(10), dataBuf.toString());
            if(dChange>0)
                _ac._cvm.drawString(g, CoSys.CHART_COLORS[0], sxStart+5, sy-(int)COMUtil.getPixel(20), "▲"+strChange+"("+strChgRate+")");
            else if(dChange<0)
                _ac._cvm.drawString(g, CoSys.CHART_COLORS[1], sxStart+5, sy-(int)COMUtil.getPixel(20), "▼"+strChange+"("+strChgRate+")");
        }
        else
        {
            _ac._cvm.drawString(g, at_col, sxEnd+5, sy-(int)COMUtil.getPixel(10), dataBuf.toString());
            if(dChange>0)
                _ac._cvm.drawString(g, CoSys.CHART_COLORS[0], sxEnd+5, sy-(int)COMUtil.getPixel(20), "▲"+strChange+"("+strChgRate+")");
            else if(dChange<0)
                _ac._cvm.drawString(g, CoSys.CHART_COLORS[1], sxEnd+5, sy-(int)COMUtil.getPixel(20), "▼"+strChange+"("+strChgRate+")");
        }
    }

    public String getTitle() {
        return "가격변화선";
    }
}