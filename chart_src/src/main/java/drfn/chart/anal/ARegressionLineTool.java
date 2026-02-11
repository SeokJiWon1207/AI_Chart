package drfn.chart.anal;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

import drfn.chart.block.Block;
import drfn.chart.util.CoSys;
import drfn.chart.util.DoublePoint;
public class ARegressionLineTool extends AnalTool{

    public ARegressionLineTool(Block ac){
        super(ac);
        ncount = 2;
        data = new DoublePoint[ncount];
        data_org = new DoublePoint[ncount];
    }
    public void draw(Canvas g){
        if(_ac==null) return;
        in = _ac.getOutBounds();
        out=_ac._cvm.getBounds();
        _ac._cvm.setLineWidth(line_t);

        if(data[1]==null)return;
        int xIndex=getIndexWithDate(data[0].x);
        float x = getDateToX(xIndex);
//        int y = priceToY(data[0].y);

        int xIndex1=getIndexWithDate(data[1].x);
        float x1 = getDateToX(xIndex1);
        //       int y1 = priceToY(data[1].y);

        PointF regLinePoint = getRegData(xIndex,xIndex1);
        float y = regLinePoint.x;
        float y1 = regLinePoint.y;

        g.save();
        g.clipRect(in.left ,in.top, in.left+in.width(), in.top+in.height());

        //g.glLineWidth(line_t);
        //추세라인.
        _ac._cvm.drawLine(g,x,y,x1,y1, at_col ,1.0f);


        //양끝점표시.
        _ac._cvm.drawFillRect(g, x-5, y-2, 5, 5, at_col, 1.0f);
        _ac._cvm.drawFillRect(g, x1-5, y1-2, 5, 5, at_col, 1.0f);

        drawSelectedPointData(g,(int)x,(int)y,xIndex,""+data[0].y);
        drawSelectedPointData(g,(int)x1,(int)y1,xIndex1,""+data[1].y);

        if(isSelect){
            _ac._cvm.setLineWidth(rectLine_t);
//            _ac._cvm.drawRect(g, x-2-selectAreaWidth/2, y-2-selectAreaWidth/2, selectAreaWidth, selectAreaWidth, CoSys.at_col);
//            _ac._cvm.drawRect(g, x1-2-selectAreaWidth/2, y1-2-selectAreaWidth/2, selectAreaWidth, selectAreaWidth, CoSys.at_col);
            drawSelectedPointRect(g, x, (int)y);
            drawSelectedPointRect(g, x1, (int)y1);
//            drawSelectedPointData(g,(int)x,(int)y,data[0].x,""+data[0].y);
//            drawSelectedPointData(g,(int)x1,(int)y1,data[1].x,""+data[1].y);

        }
        g.restore();
    }

    public PointF getRegData(int x1, int x2){
        int nStartIdx;
        int nEndIdx;
        if (x1 < x2) {
            nStartIdx = x1;
            nEndIdx = x2;
        } else {
            nStartIdx = x2;
            nEndIdx = x1;
        }
        double dXAvg = 0.0f;
        double dYAvg = 0.0f;
        double[] closeData = _ac._cdm.getSubPacketData("종가");
        double dYValue = -1.0;
        int nCount = nEndIdx - nStartIdx + 1;
        for(int i=nStartIdx; i<=nEndIdx; i++)
        {
            dXAvg += i;
            dYValue = closeData[i];
            dYAvg += dYValue;
        }
        dXAvg /= nCount;
        dYAvg /= nCount;

        double dSumD = 0.0f;
        double dSumN = 0.0f;
        for(int i=nStartIdx; i<=nEndIdx; i++)
        {
            dYValue = closeData[i];

            dSumD += (i-dXAvg) * (dYValue-dYAvg);
            dSumN += (i-dXAvg) * (i-dXAvg);
        }

        double dBeta = dSumD / dSumN;
        double dAlpha = dYAvg - dBeta * dXAvg;

        float dFirstVal = priceToY(dAlpha + dBeta * nStartIdx);
        float dSecondVal = priceToY(dAlpha + dBeta * nEndIdx);
        return  new PointF(dFirstVal, dSecondVal);
    }

    public boolean isSelected(PointF p){
        if(data[1]==null)return false;
        float x = dateToX(data[0].x);
        float x1 = dateToX(data[1].x);
        float y = priceToY(data[0].y);
        float y1 = priceToY(data[1].y);

        RectF bound1=new RectF(x-selectAreaWidth/2, y-selectAreaWidth/2, x-selectAreaWidth/2+selectAreaWidth, y-selectAreaWidth/2+selectAreaWidth);
        RectF bound2=new RectF(x1-selectAreaWidth/2, y1-selectAreaWidth/2, x1-selectAreaWidth/2+selectAreaWidth, y1-selectAreaWidth/2+selectAreaWidth);
        if(bound1.contains(p.x, p.y)){
            select_type=1;
            return true;
        }else if(bound2.contains(p.x, p.y)){
            select_type=2;
            return true;
        }else if(isSelectedLine(p,x,y,x1,y1,false)){
            select_type=0;
            return true;
        } else {
            return false;
        }
    }

    public String getTitle() {
        return "직선회귀선";
    }
}