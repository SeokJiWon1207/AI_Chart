package drfn.chart.anal;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

import drfn.chart.block.Block;
import drfn.chart.util.CoSys;
import drfn.chart.util.DoublePoint;
public class ASpeedArcTool extends AnalTool{

    public ASpeedArcTool(Block ac){
        super(ac);
        ncount = 2;
        data = new DoublePoint[ncount];
        data_org = new DoublePoint[ncount];
    }
    public void draw(Canvas g){
        in = _ac.getOutBounds();
        out=_ac._cvm.getBounds();
        _ac._cvm.setLineWidth(line_t);
//        g.clipRect(in.x,in.y,in.width,in.height);
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

        drawLine(g,x,y,x1,y1);
        if(isSelect){
//            g.setXORMode(Color.white);
//            g.setColor(Color.black);
//            g.fill3DRect((int)x-2,(int)y-2,5,5,true);
//            g.fill3DRect((int)x1-2,(int)y1-2,5,5,true);
//            g.setPaintMode();
//            drawSelectedPointData(g,(int)x,(int)y,data[0].x,""+data[0].y);
//            drawSelectedPointData(g,(int)x1,(int)y1,data[1].x,""+data[1].y);

            //2012. 8. 9 분석툴바 양끝 네모 안뜨는 현상 : T54
            _ac._cvm.setLineWidth(rectLine_t);
//            _ac._cvm.drawRect(g, x-2-selectAreaWidth/2, y-2-selectAreaWidth/2, selectAreaWidth, selectAreaWidth, CoSys.at_col);
//            _ac._cvm.drawRect(g, x1-2-selectAreaWidth/2, y1-2-selectAreaWidth/2, selectAreaWidth, selectAreaWidth, CoSys.at_col);
            drawSelectedPointRect(g, x, y);
            drawSelectedPointRect(g, x1, y1);
        }
//        g.setClip(out.x,out.y,out.width,out.height);
        g.restore();
    }
    private void drawLine(Canvas g, float x, float y, float x1, float y1){
        drawDotLine(g,x,y,x1,y1,false);
        //g.drawLine(x,y,x1,y1);
        float ry = (Math.abs(y-y1));
        float rx = (Math.abs(x-x1));
        int r = (int)Math.sqrt((ry*ry)+(rx*rx));
        PointF center = new PointF(x1, y1);
        int r0 = (int)(r*1/3);
        int r1 = (int)(r*2/3);
        if(y1<y){
//            _ac._cvm.GLDrawArc(g, 48, r0,r0, center, false);
//            _ac._cvm.GLDrawArc(g, 48, r1,r1, center, false);
            _ac._cvm.drawArc(g, r0, center, false, at_col);
            _ac._cvm.drawArc(g, r1, center, false, at_col);
        }else{
//        	_ac._cvm.GLDrawArc(g, 48, r0*-1,r0*-1, center, false);
//        	_ac._cvm.GLDrawArc(g, 48, r1*-1,r1*-1, center, false);
            _ac._cvm.drawArc(g, r0*-1, center, false, at_col);
            _ac._cvm.drawArc(g, r1*-1, center, false, at_col);
        }
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
    //2012. 7. 9  도구타이틀값 수정
    public String getTitle() {
        return "가속저항호";
    }
}