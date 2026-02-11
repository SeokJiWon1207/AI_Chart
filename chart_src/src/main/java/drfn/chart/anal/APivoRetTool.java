package drfn.chart.anal;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

import drfn.chart.block.Block;
import drfn.chart.util.CoSys;
import drfn.chart.util.DoublePoint;
//Fibonacci Retracement (피보나치 되돌림)
public class APivoRetTool extends AnalTool{
    public APivoRetTool(Block ac){
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
        //2015. 1. 13 분석툴 수정기능 및 자석기능 추가>>
        if(m_bUsePrice)
        //if(_ac._cvm.getUsePrice())
        {
            double[] minmax=getMinMax(xIndex,xIndex1);
            y = priceToY(minmax[0]);
            y1 = priceToY(minmax[1]);
        }
        //2015. 1. 13 분석툴 수정기능 및 자석기능 추가<<

        g.save();
        g.clipRect(in.left ,in.top, in.left+in.width(), in.top+in.height());
        //g.glLineWidth(line_t);

        _ac._cvm.drawLine(g,x,y,x1,y, at_col, 1.0f);
        _ac._cvm.drawLine(g,x,y,x1,y1, at_col, 1.0f);
        _ac._cvm.drawLine(g,x,y1,x1,y1, at_col, 1.0f);

        drawLine(g,x,y,x1,y1);

        drawSelectedPointData(g,(int)x,(int)y,xIndex,""+data[0].y);
        drawSelectedPointData(g,(int)x1,(int)y1,xIndex1, ""+data[1].y);

        if(isSelect){
            _ac._cvm.setLineWidth(rectLine_t);
//            _ac._cvm.drawRect(g, x-2-selectAreaWidth/2, y-2-selectAreaWidth/2, selectAreaWidth, selectAreaWidth, CoSys.at_col);
//            _ac._cvm.drawRect(g, x1-2-selectAreaWidth/2, y1-2-selectAreaWidth/2, selectAreaWidth, selectAreaWidth, CoSys.at_col);
            drawSelectedPointRect(g, x, y);
            drawSelectedPointRect(g, x1, y1);
        }
        g.restore();
    }
    private void drawLine(Canvas g, float x, float y, float x1, float y1){
        float min,max=0;
        if(y>y1){
            min=y1;
            max=y;
        }else{
            min=y;
            max=y1;
        }
        float gab= max-min;
        int py;
        for(int i=0;i<PIVOT.length;i++){
            if(y>y1)
                py =(int)(min+(gab*PIVOT[i]));
            else
                py =(int)(max-(gab*PIVOT[i]));
            drawDotLine(g,x,py,x1,py,true);
        }
    }
    public boolean isSelected(PointF p){
        if(data[1]==null)return false;
        float x = dateToX(data[0].x);
        float x1 = dateToX(data[1].x);
        float y = priceToY(data[0].y);
        float y1 = priceToY(data[1].y);

        //left, top, right, bottom
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
        }else if(isSelectedLine(p,x,y,x1,y,true)){
            select_type=0;
            return true;
        }else if(isSelectedLine(p,x,y1,x1,y1,true)){
            select_type=0;
            return true;
        }else{
            float min,max=0;
            if(y>y1){
                min=y1;
                max=y;
            }else{
                min=y;
                max=y1;
            }
            float gab= max-min;
            for(int i=0;i<PIVOT.length;i++){
                int py =(int)(min+(gab*PIVOT[i]));
                if(isSelectedLine(p,x,py,x1,py,true)){
                    select_type=0;
                    return true;
                }
            }
        }
        return false;
    }
    public String getTitle() {
        return "피보나치조정대";
    }
}