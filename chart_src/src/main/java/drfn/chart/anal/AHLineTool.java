package drfn.chart.anal;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

import drfn.chart.block.Block;
import drfn.chart.util.COMUtil;
import drfn.chart.util.ChartUtil;
import drfn.chart.util.CoSys;
import drfn.chart.util.DoublePoint;
public class AHLineTool extends AnalTool{
    public AHLineTool(Block ac){
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
        int y = (int)priceToY(data[0].y);

        g.save();
        g.clipRect(in.left ,in.top, in.left+in.width(), in.top+in.height());
        //g.glLineWidth(line_t);
        _ac._cvm.drawLine(g,in.left,y,in.left+in.right,y, at_col ,1.0f);

        String price = ChartUtil.getFormatedData("" + data[0].y, _ac._cdm.getPriceFormat(), _ac._cdm);

        int w = _ac._cvm.GetTextLength(price)+10;
        //int h = _ac._cvm.GetTextHeight();
        int h = (int)COMUtil.getPixel(8);   //2016.12.26 by LYH >> 수직선, 수평선 그리기 개선

        //2014. 7. 14 분석툴 그렸을 때 수치값 보이기 여부 처리>>
        if(COMUtil.isChuseLineValueTextShow())
        {
            //2015. 1. 16 수평선의 선과 가격문자열이 겹침 >>
//        	_ac._cvm.drawString(g, at_col, in.right-w,y+h/2, price);
//        	_ac._cvm.drawString(g, at_col, in.right-w,y+h/2+(int)COMUtil.getPixel(1), price);
            //2015. 1. 16 수평선의 선과 가격문자열이 겹침 <<

            //2015. 3. 17 수평선 가격 위치 우->좌
            _ac._cvm.drawString(g, at_col, in.left+(int)COMUtil.getPixel(10),y+h, price);   //2016.12.26 by LYH >> 수직선, 수평선 그리기 개선
        }
        //2014. 7. 14 분석툴 그렸을 때 수치값 보이기 여부 처리<<

        if(isSelect){
//            int xgab=in.right/3;
            _ac._cvm.setLineWidth(rectLine_t);
//            _ac._cvm.drawRect(g, in.width()/2-2-selectAreaWidth/2, y-2-selectAreaWidth/2, selectAreaWidth, selectAreaWidth, CoSys.at_col);
            drawSelectedPointRect(g, in.width()/2, y);
        }
        g.restore();
    }
    public boolean isSelected(PointF p){
        if(data[0]==null)return false;
        try {
            int y = (int)priceToY(data[0].y);
            //2016.12.26 by LYH >> 수직선, 수평선 그리기 개선
            //Rect bound = new Rect(in.width()/2-2-selectAreaWidth/2, y-2-selectAreaWidth/2, in.width()/2-2-selectAreaWidth/2+selectAreaWidth, y-2-selectAreaWidth/2+selectAreaWidth);
            //2017.09.05 by pjm 분석툴 선택 영역 개선 >>
//            Rect bound = new Rect(in.left, y-selectAreaWidth/2, in.left+in.width(), y+selectAreaWidth/2);
            RectF bound = new RectF(in.width()/2-selectAreaWidth/2, y-selectAreaWidth/2, in.width()/2-selectAreaWidth/2+selectAreaWidth, y+selectAreaWidth/2);
            //2016.12.26 by LYH >> 수직선, 수평선 그리기 개선 end
            if(bound.contains(p.x, p.y)){
                select_type=0;
                return true;
            }else if(isSelectedLine(p,in.left,y,in.left+in.width(),y,false)){
                select_type=0;
                return true;
            }
            //2017.09.05 by pjm 분석툴 선택 영역 개선 <<
//
//            bound = new Rect(in.left, y-10, in.left+in.width(), y+10);
//            if(bound.contains(p.x, p.y)){
//                select_type=0;
//                return true;
//            }
        } catch(Exception e) {
//        	System.out.println("isSelected");
        }

        return false;
    }
    public String getTitle() {
        return "수평선";
    }
}