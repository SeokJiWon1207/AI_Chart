package drfn.chart.anal;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

import drfn.chart.block.Block;
import drfn.chart.util.CoSys;
import drfn.chart.util.DoublePoint;
public class AGannGridTool extends AnalTool{
    double angle=45;
    int data_term=20;
    public AGannGridTool(Block ac){
        super(ac);
        ncount = 1;
        data = new DoublePoint[ncount];
        data_org = new DoublePoint[ncount];
    }

    public void draw(Canvas g){
        in = _ac.getOutBounds();
        out=_ac._cvm.getBounds();
        _ac._cvm.setLineWidth(line_t);

        if(data[0]==null)return;

        int xIndex=getIndexWithDate(data[0].x);
        float x = getDateToX(xIndex);
        float y = priceToY(data[0].y);

        g.save();
        g.clipRect(in.left ,in.top, in.left+in.width(), in.top+in.height());

        //g.glLineWidth(line_t);
        _ac._cvm.drawRect(g, (int)x-2,(int)y-2,5,5, at_col);
        drawLine(g,xIndex,y);
        if(isSelect){
            _ac._cvm.setLineWidth(rectLine_t);
//            g.setXORMode(Color.white);
//            g.setColor(Color.black);
//            g.fill3DRect((int)x-2,(int)y-2,5,5,true);
//            g.setPaintMode();
//            drawSelectedPointData(g,(int)x,(int)y,data[0].x,""+data[0].y);
            //2012. 8. 9 분석툴바 네모 안뜨는 현상  : T54
//            _ac._cvm.drawRect(g, x-2-selectAreaWidth/2, y-2-selectAreaWidth/2, selectAreaWidth, selectAreaWidth, CoSys.at_col);
            drawSelectedPointRect(g, x, y);
        }
        g.restore();
    }
    private void drawLine(Canvas g, int index, float y){
        int num=_ac._cdm.getCount();        //화면에 그릴 데이터 수
        int start_index = index%data_term;
        float start_x= in.left;
        float end_x=in.left+in.width();
        double angle_gab=Math.tan((angle* 3.1415926535897931D) / 180D);
        int max_term=data_term*3;
        for(int i=start_index-max_term;i<num+max_term;i+=data_term){
            float x = getDateToX(i);
            float rx = (Math.abs(end_x-x));
            double gan_y = rx*angle_gab;

            _ac._cvm.drawLine(g, x,y,end_x,(int)(y-gan_y), at_col, 1.0f);
            _ac._cvm.drawLine(g, x,y,end_x,(int)(y+gan_y), at_col, 1.0f);
            gan_y = x*(-angle_gab);

            _ac._cvm.drawLine(g, start_x,(int)(y-gan_y),x,y, at_col, 1.0f);
            _ac._cvm.drawLine(g, start_x,(int)(y+gan_y),x,y, at_col, 1.0f);
        }

    }

    public boolean isSelected(PointF p){
        if(data[0]==null)return false;
        float x = dateToX(data[0].x);
        float y = priceToY(data[0].y);
        RectF bound= new RectF(x-selectAreaWidth/2, y-selectAreaWidth/2,x-selectAreaWidth/2+ selectAreaWidth, y-selectAreaWidth/2+selectAreaWidth);
        RectF bound1 = new RectF(in.left-selectAreaWidth/2, y-selectAreaWidth/2, in.left-selectAreaWidth/2+in.left+in.width(), y-selectAreaWidth/2+10);
        if(bound.contains(p.x, p.y)||bound1.contains(p.x, p.y)){
            select_type=0;
            return true;
        }
        return false;
    }
    //2012. 7. 9  도구타이틀값 추가
    public String getTitle() {
        return "갠그리드";
    }
}