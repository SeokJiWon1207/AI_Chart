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
public class ACrossLineTool extends AnalTool{
    private String date="";
    public ACrossLineTool(Block ac){
        super(ac);
        ncount = 1;
        data = new DoublePoint[ncount];
        data_org= new DoublePoint[ncount];
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
        //2016.12.26 by LYH >> 수직선, 수평선 그리기 개선
        //g.clipRect(in.left ,in.top, in.left+in.width(), in.top+in.height());
        g.clipRect(in.left ,out.top, in.left+in.width(), out.top+out.height());
        //2016.12.26 by LYH >> 수직선, 수평선 그리기 개선 end
        //g.glLineWidth(line_t);
        String price = ChartUtil.getFormatedData("" + data[0].y, _ac._cdm.getPriceFormat(), _ac._cdm);
        int w = _ac._cvm.GetTextLength(price)+10;
//        int h = COMUtil.tf.GetTextHeight();

        date =_ac._cdm.getFormatData("자료일자", xIndex);
        //가격
        //2014. 7. 14 분석툴 그렸을 때 수치값 보이기 여부 처리>>
        if(COMUtil.isChuseLineValueTextShow())
        {
            //2016.12.26 by LYH >> 수직선, 수평선 그리기 개선
            //_ac._cvm.drawString(g, at_col, in.left+in.width()-w,y+5, price);
            //_ac._cvm.drawString(g, at_col, x+5,in.bottom-15, date);
            int h = (int)COMUtil.getPixel(8);
            _ac._cvm.drawString(g, at_col, in.left+(int)COMUtil.getPixel(10),y+h, price);
            _ac._cvm.drawString(g, at_col, x+(int)COMUtil.getPixel(2),in.bottom-(int)COMUtil.getPixel(8), date);
            //2016.12.26 by LYH >> 수직선, 수평선 그리기 개선 end
        }
        //2014. 7. 14 분석툴 그렸을 때 수치값 보이기 여부 처리<<

        //날짜
        //2016.12.26 by LYH >> 수직선, 수평선 그리기 개선
        //_ac._cvm.drawLine(g, x,in.top,x,in.top+in.height(), at_col, 1.0f);
        _ac._cvm.drawLine(g, x,out.top,x,out.top+out.height()-_ac._cvm.XSCALE_H,at_col ,1.0f);
        //2016.12.26 by LYH >> 수직선, 수평선 그리기 개선 end
        _ac._cvm.drawLine(g, in.left,y,in.left+in.width(),y, at_col, 1.0f);

        //십자선 가운데 원
        _ac._cvm.drawCircle(g, x-(int)COMUtil.getPixel(2), y-(int)COMUtil.getPixel(2), x+(int)COMUtil.getPixel(2), y+(int)COMUtil.getPixel(2), true, at_col);

//        w = _ac._cvm.tf.GetTextLength(""+date)+15;


        if(isSelect){
            _ac._cvm.setLineWidth(rectLine_t);
            //_ac._cvm.drawRect(g, x-2-selectAreaWidth/2, y-2-selectAreaWidth/2, selectAreaWidth, selectAreaWidth, CoSys.at_col);
            drawSelectedPointRect(g, x, y);
        }
        g.restore();
    }
    public boolean isSelected(PointF p){
        if(data[0]==null)return false;
        float x = dateToX(data[0].x);
        float y = priceToY(data[0].y);
        //2016.12.26 by LYH >> 수직선, 수평선 그리기 개선
        //Rect bound= new Rect(x-selectAreaWidth/2, y-selectAreaWidth/2,x-selectAreaWidth/2+ selectAreaWidth, y-selectAreaWidth/2+selectAreaWidth);
//        Rect bound= new Rect(x-selectAreaWidth/2, out.top, x-selectAreaWidth/2+ selectAreaWidth, out.top+out.height()-_ac._cvm.XSCALE_H);
//        //Rect bound1 = new Rect(in.left-selectAreaWidth/2, y-selectAreaWidth/2, in.left-selectAreaWidth/2+in.left+in.width(), y-selectAreaWidth/2+10);
//        Rect bound1 = new Rect(in.left, y-selectAreaWidth/2, in.left+in.width(), y+selectAreaWidth/2);
        //2017.09.05 by pjm 분석툴 선택 영역 개선 >>
        RectF bound = new RectF(x-selectAreaWidth/2, y-selectAreaWidth/2, x-selectAreaWidth/2+ selectAreaWidth, y+selectAreaWidth/2);

        //2016.12.26 by LYH >> 수직선, 수평선 그리기 개선 end
//        if(bound.contains(p.x, p.y)||bound1.contains(p.x, p.y)){
        if(bound.contains(p.x, p.y)){
            select_type=0;
            return true;
        }else if(isSelectedLine(p,in.left,y,in.left+in.width(),y,false)){
            select_type=0;
            return true;
        }
        //2017.09.05 by pjm 분석툴 선택 영역 개선 <<
        else if(isSelectedLine(p,x,out.top,x,out.top+out.height()-_ac._cvm.XSCALE_H,false)){
            select_type=0;
            return true;
        }
        return false;
    }
    public String getTitle() {
        return "십자선";
    }
}