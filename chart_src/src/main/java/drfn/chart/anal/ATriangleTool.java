package drfn.chart.anal;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

import drfn.chart.block.Block;
import drfn.chart.util.CoSys;
import drfn.chart.util.DoublePoint;
public class ATriangleTool extends AnalTool{

    public ATriangleTool(Block ac){
        super(ac);
        ncount = 2;
        data = new DoublePoint[ncount];
        data_org = new DoublePoint[ncount];
    }
    public void draw(Canvas g){
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

        drawRect(g,x,y,x1,y1);
        _ac._cvm.drawRect(g,(int)x-2,y-2,5,5, at_col);
        _ac._cvm.drawRect(g,(int)x1-2,y1-2,5,5, at_col);

        drawSelectedPointData(g,(int)x,(int)y,xIndex,""+data[0].y);
        drawSelectedPointData(g,(int)x1,(int)y1,xIndex1,""+data[1].y);
        if(isSelect){
            _ac._cvm.setLineWidth(rectLine_t);
//            _ac._cvm.drawRect(g, x-2-selectAreaWidth/2,y-2-selectAreaWidth/2,selectAreaWidth,selectAreaWidth, CoSys.at_col);
//            _ac._cvm.drawRect(g, x1-2-selectAreaWidth/2,y1-2-selectAreaWidth/2,selectAreaWidth,selectAreaWidth, CoSys.at_col);
            drawSelectedPointRect(g, x, y);
            drawSelectedPointRect(g, x1, y1);
        }

        g.restore();
    }
    private void drawRect(Canvas gl, float x, float y, float x1, float y1){
        if(x>x1){
            _ac._cvm.drawTriangle(gl,x1,y,x-x1,y1-y, at_col);
        }else{
            _ac._cvm.drawTriangle(gl,x,y,x1-x,y1-y, at_col);
        }
    }
    public boolean isSelected(PointF p){
        if(data[1]==null)return false;
        float x = dateToX(data[0].x);
        float x1 = dateToX(data[1].x);
        float y = priceToY(data[0].y);
        float y1 = priceToY(data[1].y);
        RectF bound= new RectF(x-2-selectAreaWidth/2, y-2-selectAreaWidth/2, x-2-selectAreaWidth/2+selectAreaWidth, y-2-selectAreaWidth/2+selectAreaWidth);
        RectF bound1 = new RectF(x1-2-selectAreaWidth/2, y1-2-selectAreaWidth/2, x1-2-selectAreaWidth/2+selectAreaWidth, y1-2-selectAreaWidth/2+selectAreaWidth);
        RectF bound2 = new RectF(x, y, x1, y1);
        if(bound.contains(p.x, p.y)){
            select_type=1;
            return true;
        }else if(bound1.contains(p.x, p.y)){
            select_type=2;
            return true;
        }else if(bound2.contains(p.x, p.y)){
            select_type=0;
            return true;
        }
        return false;
    }
    public String getTitle() {
        return "삼각형";
    }
}