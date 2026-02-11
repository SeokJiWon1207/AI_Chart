package drfn.chart.anal;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

import drfn.chart.block.Block;
import drfn.chart.util.COMUtil;
import drfn.chart.util.CoSys;
import drfn.chart.util.DoublePoint;
public class AVLineTool extends AnalTool{
    private String date="";
    public AVLineTool(Block ac){
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

        g.save();
        //2016.12.26 by LYH >> 수직선, 수평선 그리기 개선
//        g.clipRect(in.left ,in.top, in.left+in.width(), in.top+in.height());
        g.clipRect(in.left ,out.top, in.left+in.width(), out.top+out.height());
//        g.glLineWidth(line_t);
        //_ac._cvm.drawLine(g, x,in.top,x,in.top+in.height(),at_col ,1.0f);
        _ac._cvm.drawLine(g, x,out.top,x,out.top+out.height()-_ac._cvm.XSCALE_H,at_col ,1.0f);
        //2016.12.26 by LYH >> 수직선, 수평선 그리기 개선 end
        //String[] tmp=_ac._cdm.getDatas(data[0].x);
        //if(tmp==null)return;
        //date = tmp[0];
        date = _ac._cdm.getFormatData("자료일자", xIndex);

//        int w = _ac._cvm.GetTextLength(""+date)+15;
        //int h = _ac._cvm.tf.GetTextHeight();
//        int h = 15;
//        _ac._cvm.drawFillRect(g, x-(w/2),out.bottom-30-h/2,w,h+2, CoSys.GRAY, 1.0f);
//        _ac._cvm.drawFillRect(g, x-(w/2),out.bottom-30-h/2,w,h+2, CoSys.BLACK, 1.0f);

        if(COMUtil.isChuseLineValueTextShow())
        {
            //2016.12.26 by LYH >> 수직선, 수평선 그리기 개선
            _ac._cvm.drawString(g, at_col, x+(int)COMUtil.getPixel(2),in.bottom-(int)COMUtil.getPixel(8), date);
            //2016.12.26 by LYH >> 수직선, 수평선 그리기 개선 end
        }

        //2014. 7. 14 분석툴 그렸을 때 수치값 보이기 여부 처리>>
        if(isSelect){
            _ac._cvm.setLineWidth(rectLine_t);
//            int ygab=out.height()/3;
//            _ac._cvm.drawRect(g, x-2-selectAreaWidth/2,in.height()/2,selectAreaWidth,selectAreaWidth, CoSys.at_col);
            drawSelectedPointRect(g, x, in.height()/2);
        }
        //2014. 7. 14 분석툴 그렸을 때 수치값 보이기 여부 처리<<

        g.restore();
    }
    public boolean isSelected(PointF p){
        if(data[0]==null)return false;
        float x = dateToX(data[0].x);
        //2016.12.26 by LYH >> 수직선, 수평선 그리기 개선
        //2017.09.05 by pjm 분석툴 선택 영역 개선 >>
//        Rect bound= new Rect(x-selectAreaWidth/2, out.top, x-selectAreaWidth/2+ selectAreaWidth, out.top+out.height()-_ac._cvm.XSCALE_H);
        RectF bound= new RectF(x-selectAreaWidth/2, in.height()/2-selectAreaWidth/2, x-selectAreaWidth/2+ selectAreaWidth, in.height()/2-selectAreaWidth/2+ selectAreaWidth);
        //2017.09.05 by pjm 분석툴 선택 영역 개선 <<
        //Rect bound= new Rect(x-2-selectAreaWidth/2,in.height()/2,x-2-selectAreaWidth/2+selectAreaWidth,in.height()/2+selectAreaWidth);
        //2016.12.26 by LYH >> 수직선, 수평선 그리기 개선 end
        if(bound.contains(p.x, p.y)){
            select_type=0;
            return true;
        }
        //2017.09.05 by pjm 분석툴 선택 영역 개선 >>
        else if(isSelectedLine(p,x,out.top,x,out.top+out.height()-_ac._cvm.XSCALE_H,false)){
            select_type=0;
            return true;
        }

//        bound= new Rect(x-5, in.top, x-5+10, in.top+in.height());
//        if(bound.contains(p.x, p.y)){
//            select_type=0;
//            return true;
//        }
        //2017.09.05 by pjm 분석툴 선택 영역 개선 >>
        return false;
    }
    public String getTitle() {
        return "수직선";
    }
}