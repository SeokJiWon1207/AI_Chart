package drfn.chart.anal;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

import drfn.chart.block.Block;
import drfn.chart.util.CoSys;
import drfn.chart.util.DoublePoint;
public class ASpeedFanTool extends AnalTool{

    public ASpeedFanTool(Block ac){
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
        //_ac._cvm.drawLine(g,x,y,x1,y1, CoSys.at_col ,1.0f);
        drawLine(g,x,y,x1,y1);
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
//            drawSelectedPointData(g,(int)x,(int)y,data[0].x,""+data[0].y);
//            drawSelectedPointData(g,(int)x1,(int)y1,data[1].x,""+data[1].y);

        }
        g.restore();
    }

    private void drawLine(Canvas g, float x, float y, float x1, float y1){
        //drawDotLine(g,x,y,x1,y1,false);
        float startX = in.left;
        float w = in.width();
        double[] ratio1=new double[3];
        double pivot[]= {1.0,2.0/3.0,1.0/3.0};
        float ry = (Math.abs(y-y1));
        float rx = (Math.abs(x-x1));
//        double gab = ry/3;
        for(int j=0;j<3;j++){
            ratio1[j]=(ry*pivot[2-j])/rx;
            if(x>x1){
                if(y>y1){
                    _ac._cvm.drawLine(g, x,y,0,y-(int)((x)*ratio1[j]), at_col, 1.0f);
                }else{
                    _ac._cvm.drawLine(g, x,y,0,y+(int)((x)*ratio1[j]), at_col, 1.0f);
                }
            }else{
                if(y>y1){
                    _ac._cvm.drawLine(g, x,y,w+startX,y-(int)((w+startX-x)*ratio1[j]), at_col, 1.0f);
                }else{
                    _ac._cvm.drawLine(g, x,y,w+startX,y+(int)((w+startX-x)*ratio1[j]), at_col, 1.0f);
                }
            }
        }
//        for(int j=0;j<3 ;j++){
//            ratio1[j]=(ry*pivot[2-j])/rx;
//            if(x>x1){
//                if(y>y1){
//                    [COMUtil drawLine:x y1:y x2:0 y2:y-(int)((x)*ratio1[j])]; 
//                }else{
//                    [COMUtil drawLine:x y1:y x2:0 y2:y+(int)((x)*ratio1[j])]; 
//                }
//            }else{
//                if(y>y1){
//                    [COMUtil drawLine:x y1:y x2:w+startX y2:y-(int)((w+startX-x)*ratio1[j])]; 
//                }else{
//                    [COMUtil drawLine:x y1:y x2:w+startX y2:y+(int)((w+startX-x)*ratio1[j])]; 
//                }
//            }
//        }

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
        return "가속저항팬";
    }
}