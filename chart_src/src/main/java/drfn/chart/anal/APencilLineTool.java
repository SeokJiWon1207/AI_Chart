package drfn.chart.anal;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

import drfn.chart.block.Block;
import drfn.chart.util.CoSys;
import drfn.chart.util.DoublePoint;

public class APencilLineTool extends AnalTool {

	public APencilLineTool(Block ac) {
		super(ac);
		ncount = 500;
		data = new DoublePoint[ncount];
		data_org = new DoublePoint[ncount];
	}

	public void draw(Canvas g) {
		if (_ac == null)
			return;
		in = _ac.getGraphBounds();
		out = _ac._cvm.getBounds();
		_ac._cvm.setLineWidth(line_t);

		int xIndex;
		float x;
		float y;

		int xIndex1;
		float x1;
		float y1;
		int[] at_col1 = { 255, 0, 0 };
		g.save();
		g.clipRect(in.left, in.top, in.left + in.width(), in.top + in.height());

		for (int i = data.length - 2; i >= 0 ; i--) {
			if(data[i+1]==null)
				continue;
			xIndex = getIndexWithDate(data[i].x);
			x = getDateToX(xIndex);
			y = priceToY(data[i].y);
			xIndex1 = getIndexWithDate(data[i + 1].x);
			x1 = getDateToX(xIndex1);
			y1 = priceToY(data[i + 1].y);
			_ac._cvm.drawLine(g, x, y, x1, y1, at_col, 1.0f);
			if(i == 0)
			{
				//drawSelectedPointData(g,(int)x,(int)y,xIndex,""+minmax[0]);
				_ac._cvm.drawFillRect(g, x - 5, y - 2, 5, 5, at_col, 1.0f);

				if (isSelect) {
//					_ac._cvm.drawRect(g, x - 2 - selectAreaWidth / 2, y - 2 - selectAreaWidth / 2, selectAreaWidth,
//							selectAreaWidth, at_col);
					_ac._cvm.setLineWidth(rectLine_t);
					drawSelectedPointRect(g, x, y);
				}
			}
//			else if(i == data.length - 2)
//			{
//				//_ac._cvm.drawLine(g, x, y, x1, y1, CoSys.at_col, 1.0f);
//				_ac._cvm.drawFillRect(g, x1 - 5, y1 - 2, 5, 5, at_col, 1.0f);
//				if (isSelect) {
//					_ac._cvm.drawRect(g, x1 - 2 - selectAreaWidth / 2, y1 - 2 - selectAreaWidth / 2, selectAreaWidth,
//							selectAreaWidth, at_col);
//				}
//			}
		}

		g.restore();
	}

	public boolean isSelected(PointF p) {
		if (data[1] == null)
			return false;
		float x = dateToX(data[0].x);
//		int x1 = dateToX(data[1].x);
		float y = priceToY(data[0].y);
//		int y1 = priceToY(data[1].y);
//
		RectF bound1 = new RectF(x - selectAreaWidth / 2, y - selectAreaWidth / 2,
				x - 2 - selectAreaWidth / 2 + selectAreaWidth, y - selectAreaWidth / 2 + selectAreaWidth);
//		Rect bound2 = new Rect(x1 - 2 - selectAreaWidth / 2, y - selectAreaWidth / 2,
//				x1 - 2 - selectAreaWidth / 2 + selectAreaWidth, y - selectAreaWidth / 2 + selectAreaWidth);
		if (bound1.contains(p.x, p.y)) {
			select_type = 1;
			return true;
		}
		else
			return false;
//		} else if (bound2.contains(p.x, p.y)) {
//			select_type = 2;
//			return true;
//		} else if (isSelectedLine(p, x, y, x1, y1, false)) {
//			select_type = 0;
//			return true;
//		}
// 		else {
//			return false;
//		}
//		return false;
	}

	public String getTitle() {
//		return "연필추세선";
		return "그리기";
	}
}