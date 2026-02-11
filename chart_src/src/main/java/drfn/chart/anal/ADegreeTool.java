package drfn.chart.anal;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

import java.text.DecimalFormat;

import drfn.chart.block.Block;
import drfn.chart.util.CoSys;
import drfn.chart.util.DoublePoint;
import drfn.chart.util.ChartUtil;

public class ADegreeTool extends AnalTool {

	public ADegreeTool(Block ac) {
		super(ac);
		ncount = 2;
		data = new DoublePoint[ncount];
		data_org = new DoublePoint[ncount];
	}

	public void draw(Canvas g) {
		if (_ac == null)
			return;
		in = _ac.getGraphBounds();
		out = _ac._cvm.getBounds();
		_ac._cvm.setLineWidth(line_t);

		if (data[1] == null)
			return;
		int xIndex = getIndexWithDate(data[0].x);
		float x = getDateToX(xIndex);
		float y = priceToY(data[0].y);

		int xIndex1 = getIndexWithDate(data[1].x);
		float x1 = getDateToX(xIndex1);
		float y1 = priceToY(data[1].y);
		float x2 = in.left + in.width();
		float y2 = in.top + in.height();
		double length = Math.sqrt(Math.pow((x1 - x),2)  + Math.pow((y1 - y),2) );
		PointF center = new PointF(x, y);
		PointF p1 = new PointF(x, y);
		PointF p2 = new PointF(x1, y1);
		double arc = ChartUtil.getAngle(p1, p2);
		DecimalFormat format = new DecimalFormat(".#");

		int[] at_col1 = { 255, 0, 0 };
		g.save();
		g.clipRect(in.left, in.top, in.left + in.width(), in.top + in.height());


		_ac._cvm.drawLine(g, x, y, x + (int) length, y, at_col, 1.0f);
		_ac._cvm.drawLine(g, x, y, x1, y1, at_col, 1.0f);
		_ac._cvm.drawArc1(g, (int)(length / 3), center, true, at_col1, 0.0, arc);

		_ac._cvm.drawString(g, CoSys.at_col, x, y, format.format(Math.abs(arc)) + "°");

		// 7.15

		// 양끝점표시.
		_ac._cvm.drawFillRect(g, x - 5, y - 2, 5, 5, at_col, 1.0f);
		_ac._cvm.drawFillRect(g, x1 - 5, y1 - 2, 5, 5, at_col, 1.0f);

		drawSelectedPointData(g, (int) x, (int) y, xIndex, "" + data[0].y);
		drawSelectedPointData(g, (int) x1, (int) y1, xIndex1, "" + data[1].y);

		if (isSelect) {
			_ac._cvm.setLineWidth(rectLine_t);
//			_ac._cvm.drawRect(g, x - 2 - selectAreaWidth / 2, y - 2 - selectAreaWidth / 2, selectAreaWidth,
//					selectAreaWidth, CoSys.at_col);
//			_ac._cvm.drawRect(g, x1 - 2 - selectAreaWidth / 2, y1 - 2 - selectAreaWidth / 2, selectAreaWidth,
//					selectAreaWidth, CoSys.at_col);
			drawSelectedPointRect(g, x, y);
			drawSelectedPointRect(g, x1, y1);
			// drawSelectedPointData(g,(int)x,(int)y,data[0].x,""+data[0].y);
			// drawSelectedPointData(g,(int)x1,(int)y1,data[1].x,""+data[1].y);

		}
		g.restore();
	}

	public boolean isSelected(PointF p) {
        if(data[1]==null)return false;
		float x = dateToX(data[0].x);
		float x1 = dateToX(data[1].x);
		float y = priceToY(data[0].y);
		float y1 = priceToY(data[1].y);

//        Rect bound1=new Rect(x-25-selectAreaWidth/2, y-selectAreaWidth/2, x-25-selectAreaWidth/2+selectAreaWidth, y-selectAreaWidth/2+selectAreaWidth);
//        Rect bound2=new Rect(x1-2-selectAreaWidth/2, y1-selectAreaWidth/2, x1-2-selectAreaWidth/2+selectAreaWidth, y1-selectAreaWidth/2+selectAreaWidth);

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

	public String getTitle() {
		return "각";
	}
}