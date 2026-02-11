package drfn.chart.anal;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

import drfn.chart.block.Block;
import drfn.chart.util.CoSys;
import drfn.chart.util.DoublePoint;
public class AElliotTool extends AnalTool{

    public AElliotTool(Block ac){
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
        float y = priceToY(data[0].y);

        int xIndex1=getIndexWithDate(data[1].x);
        float x1 = getDateToX(xIndex1);
        float y1 = priceToY(data[1].y);

        g.save();
        g.clipRect(in.left ,in.top, in.left+in.width(), in.top+in.height());

        //g.glLineWidth(line_t);
        //추세라인.
        _ac._cvm.drawLine(g,x,y,x1,y1, at_col ,1.0f);

        PointF p2 = getTwo(x1-x, y1-y, x1, y1);
        _ac._cvm.drawLine(g,x1,y1,p2.x,p2.y, at_col ,1.0f);

        PointF p3 = getThree(x1-x, y1-y, p2.x, p2.y);
        _ac._cvm.drawLine(g,p2.x, p2.y, p3.x, p3.y, at_col ,1.0f);

        PointF p4 = getTwo(p3.x-p2.x, p3.y-p2.y, p3.x, p3.y);
        _ac._cvm.drawLine(g,p3.x,p3.y,p4.x,p4.y, at_col ,1.0f);

        PointF p5 = getThree(x1-x, y1-y, p4.x, p4.y);
        _ac._cvm.drawLine(g,p4.x, p4.y, p5.x, p5.y, at_col ,1.0f);

        //양끝점표시.
        _ac._cvm.drawFillRect(g, x-5, y-2, 5, 5, at_col, 1.0f);
        _ac._cvm.drawFillRect(g, x1-5, y1-2, 5, 5, at_col, 1.0f);

        drawSelectedPointData(g,(int)x,(int)y,xIndex,""+data[0].y);
        drawSelectedPointData(g,(int)x1,(int)y1,xIndex1,""+data[1].y);

        if(isSelect){
            _ac._cvm.setLineWidth(rectLine_t);
//            _ac._cvm.drawRect(g, x-2-selectAreaWidth/2, y-2-selectAreaWidth/2, selectAreaWidth, selectAreaWidth, CoSys.at_col);
//            _ac._cvm.drawRect(g, x1-2-selectAreaWidth/2, y1-2-selectAreaWidth/2, selectAreaWidth, selectAreaWidth, CoSys.at_col);
            drawSelectedPointRect(g, x, y);
            drawSelectedPointRect(g, x1, y1);

        }
        g.restore();
    }

    public PointF getTwo(float xLen, float yLen, float x, float y){
        float newx = (x + (xLen * 0.382f));
        float newy = y - (yLen * 0.382f);
        return new PointF(newx, newy);
    }

    public PointF getThree(float xLen, float yLen, float x, float y) {
        float newx = x + (xLen * 1.618f);
        float newy = y + (yLen * 1.618f);
        return new PointF(newx, newy);
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
        return "엘리어트파동선";
    }
}