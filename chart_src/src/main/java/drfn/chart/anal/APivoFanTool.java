package drfn.chart.anal;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

import drfn.chart.block.Block;
import drfn.chart.util.CoSys;
import drfn.chart.util.DoublePoint;
public class APivoFanTool extends AnalTool{
    public APivoFanTool(Block ac){
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
//        g.setColor(at_col);
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

            //2012. 8. 9 분석툴바 일부 양끝 네모 안뜨는 현상 : T54
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
        _ac._cvm.drawLine(g, x,y,x1,y1, at_col, 1.0f);
        float startX = in.left;
        float w = in.width();
        double[] ratio1={0,0,0,0};
        double[] pivot= {0.618,0.5,0.382};
        float ry = (Math.abs(y-y1));
        float rx = (Math.abs(x-x1));
        for(int j=0;j<pivot.length;j++){
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
    }
    public boolean isSelected(PointF p){
        if(data[1]==null)return false;
        float x = dateToX(data[0].x);
        float x1 = dateToX(data[1].x);
        float y = priceToY(data[0].y);
        float y1 = priceToY(data[1].y);
        RectF bound= new RectF((int)x-5,(int)y-5,10,10);
        RectF bound1= new RectF((int)x1-5,(int)y1-5,10,10);
        if(bound.contains(p.x, p.y)){
            select_type=1;
            return true;
        }else if(bound1.contains(p.x, p.y)){
            select_type=2;
            return true;
        }else if(isSelectedLine(p,x,y,x1,y1,false)){
            select_type=0;
            return true;
        }
        return false;
    }
    //2012. 7. 9  도구타이틀값 추가 
    public String getTitle() {
        return "피보나치팬";
    }
}