package drfn.chart.anal;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

import drfn.chart.block.Block;
import drfn.chart.util.CoSys;
import drfn.chart.util.DoublePoint;


public class ADivFTool extends AnalTool{
    private final int gab = 4;

    public ADivFTool(Block ac){
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

        int xIndex1=getIndexWithDate(data[1].x);
        float x1 = getDateToX(xIndex1);

        if(xIndex<0 && xIndex1<0)
            return;

        double[] minmax=getMinMax(xIndex,xIndex1);

        float y = priceToY(minmax[0]);
        float y1 = priceToY(minmax[1]);

        g.save();
        g.clipRect(in.left ,in.top, in.left+in.width(), in.top+in.height());
        //g.glLineWidth(line_t);
        drawLine(g,x,y,x1,y1,minmax);

        _ac._cvm.drawRect(g, x-2, y-2, 5, 5, at_col);
        _ac._cvm.drawRect(g, x1-2, y1-2, 5, 5, at_col);
        drawSelectedPointData(g,(int)x,(int)y,xIndex,""+minmax[0]);
        drawSelectedPointData(g,(int)x1,(int)y1,xIndex1,""+minmax[1]);
        if(isSelect){
            _ac._cvm.setLineWidth(rectLine_t);
//            _ac._cvm.drawRect(g, x-2-selectAreaWidth/2, y-2-selectAreaWidth/2, selectAreaWidth, selectAreaWidth, CoSys.at_col);
//            _ac._cvm.drawRect(g, x1-2-selectAreaWidth/2, y1-2-selectAreaWidth/2, selectAreaWidth, selectAreaWidth, CoSys.at_col);
            drawSelectedPointRect(g, x, y);
            drawSelectedPointRect(g, x1, y1);
        }
        g.restore();

    }
    private void drawLine(Canvas g, float x, float y, float x1, float y1,double[] minmax){
        float min =y;
        float max =y1;
        if(y>y1){
            max=y;
            min=y1;
        }

        float pgab = (max-min)/gab;
        for(int i=0;i<gab;i++){
//            if(i==2) drawDotLine(g,x,min+(i*pgab),x1,min+(i*pgab),true);
//            else
                _ac._cvm.drawLine(g, x,min+(i*pgab),x1,min+(i*pgab), at_col, 1.0f);
        }
        _ac._cvm.drawLine(g, x,max,x1,max, at_col, 1.0f);
    }
    public boolean isSelected(PointF p){
        if(data[1]==null)return false;
        float x = dateToX(data[0].x);
        float x1 = dateToX(data[1].x);
        if(getIndexWithDate(data[0].x)<0 && getIndexWithDate(data[1].x)<0)
            return false;
        double[] minmax=getMinMax(getIndexWithDate(data[0].x),getIndexWithDate(data[1].x));
        float y = priceToY(minmax[0]);
        float y1 = priceToY(minmax[1]);
        float min =y;
        float max =y1;
        if(y>y1){
            max=y;
            min=y1;
        }
        float pgab = (max-min)/gab;
        RectF bound=new RectF(x-selectAreaWidth/2, y-selectAreaWidth/2, x-selectAreaWidth/2+selectAreaWidth, y-selectAreaWidth/2+selectAreaWidth);
        RectF bound1=new RectF(x1-selectAreaWidth/2, y1-selectAreaWidth/2, x1-selectAreaWidth/2+selectAreaWidth, y1-selectAreaWidth/2+selectAreaWidth);
        RectF bound2=new RectF(x,y,x1,y1);

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
        for(int i=0;i<gab;i++){
            if(isSelectedLine(p,x,min+(i*pgab),x1,min+(i*pgab),true)){
                select_type=0;
                return true;
            }
        }
        if(isSelectedLine(p,x,max,x1,max,true)){
            select_type=0;
            return true;
        }
        return false;
    }
    public String getTitle() {
        return "사등분선";
    }
}