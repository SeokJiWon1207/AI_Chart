package drfn.chart.anal;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

import drfn.chart.block.Block;
import drfn.chart.util.CoSys;
import drfn.chart.util.DoublePoint;
public class AGannFanTool extends AnalTool{
    double[] angle={82.5,75,71.25,63.75,45,26.25,18.75,15,7.5};
    boolean m_bDown;

    public AGannFanTool(Block ac){
        super(ac);
        ncount = 1;
        data = new DoublePoint[ncount];
        data_org = new DoublePoint[ncount];
        m_bDown = false;
    }

    public void draw(Canvas g){
        in = _ac.getOutBounds();
        out=_ac._cvm.getBounds();
        _ac._cvm.setLineWidth(line_t);
//        g.clipRect(in.x,in.y,in.width,in.height);
        if(data[0]==null)return;
        int xIndex=getIndexWithDate(data[0].x);
        float x = getDateToX(xIndex);
        float y = priceToY(data[0].y);

        g.save();
        g.clipRect(in.left ,in.top, in.left+in.width(), in.top+in.height());
        //g.glLineWidth(line_t);
        drawLine(g,x,y);
        _ac._cvm.drawRect(g, (int)x-2,(int)y-2,5,5, at_col);
        drawSelectedPointData(g,(int)x,(int)y,xIndex,""+data[0].y);
        if(isSelect){
            _ac._cvm.setLineWidth(rectLine_t);
//            _ac._cvm.drawRect(g, x-2-selectAreaWidth/2, y-2-selectAreaWidth/2, selectAreaWidth, selectAreaWidth, CoSys.at_col);
            drawSelectedPointRect(g, x, y);
        }
        g.restore();
    }
    private void drawLine(Canvas g, float x, float y){
        float end_x = in.left+in.width();
        float rx = (Math.abs(end_x-x));

        for(int i=0;i<angle.length;i++){
            double gan_y = rx*Math.tan((angle[i]* 3.1415926535897931D) / 180D);
            if(m_bDown)
                gan_y *= -1;
            _ac._cvm.drawLine(g, x,y,end_x,(int)(y-gan_y), at_col, 1.0f);
        }
    }

    public void setIsDown(boolean bDown)
    {
        m_bDown = bDown;
    }
    public boolean isSelected(PointF p){
        if(data[0]==null)return false;
        float x = dateToX(data[0].x);
        float y = priceToY(data[0].y);
        RectF bound=new RectF(x-selectAreaWidth/2, y-selectAreaWidth/2, x-selectAreaWidth/2+selectAreaWidth, y-selectAreaWidth/2+selectAreaWidth);
        if(bound.contains(p.x, p.y)){
            select_type=1;
            return true;
        }
        return false;
    }
    public String getTitle() {
        if(m_bDown)
            return "하향갠팬";
        return "갠팬";
    }
}