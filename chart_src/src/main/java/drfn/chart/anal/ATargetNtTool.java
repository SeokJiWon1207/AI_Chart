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

public class ATargetNtTool extends AnalTool {
	public ATargetNtTool(Block ac) {
		super(ac);
		ncount = 3;
		data = new DoublePoint[ncount];
		data_org = new DoublePoint[ncount];
	}

	public void draw(Canvas g) {
		in = _ac.getGraphBounds();
		out = _ac._cvm.getBounds();
		_ac._cvm.setLineWidth(line_t);

		 if (data[2] == null) {
		 if (data[0] != null) {
		 int xIndex = getIndexWithDate(data[0].x);
		 float px = getDateToX(xIndex);
		 float py = priceToY(data[0].y);
		 _ac._cvm.drawLine(g, px - 5, py + 5, px + 6, py - 6, at_col, 1.0f);
		 _ac._cvm.drawLine(g, px - 5, py - 5, px + 6, py + 6, at_col, 1.0f);
		 }
		 if (data[1] != null) {
		 int xIndex = getIndexWithDate(data[1].x);
		 float px = getDateToX(xIndex);
		 float py = priceToY(data[1].y);
		 _ac._cvm.drawLine(g, px - 5, py + 5, px + 6, py - 6, at_col, 1.0f);
		 _ac._cvm.drawLine(g, px - 5, py - 5, px + 6, py + 6, at_col, 1.0f);
		 }

		 return;
		 }
		g.save();
		g.clipRect(in.left, in.top, in.left + in.width(), in.top + in.height());

		if (data[1] == null || data[2] == null)
			return;

		float arrX[] = new float[3];
		float arrY[] = new float[3];
		int xIndex = getIndexWithDate(data[0].x);
		arrX[0] = getDateToX(xIndex);
		arrY[0] = priceToY(data[0].y);

		int xIndex1 = getIndexWithDate(data[1].x);
		arrX[1] = getDateToX(xIndex1);
		arrY[1] = priceToY(data[1].y);
		

		int xIndex2 = getIndexWithDate(data[2].x);
		arrX[2] = getDateToX(xIndex2);
		arrY[2] = priceToY(data[2].y);

		float tempX, tempY; // ������ ���� �ӽ÷� ���� ������ ����

		for (int i = 0; i < arrX.length; i++) {

			for (int j = i + 1; j < arrX.length; j++) {
				if (arrX[i] > arrX[j]) {
					tempX = arrX[i];
					tempY = arrY[i];

					arrX[i] = arrX[j];
					arrY[i] = arrY[j];

					arrX[j] = tempX;
					arrY[j] = tempY;
				}
			}
		}
		float x = arrX[0];
		float x1 = arrX[1];
		float x2 = arrX[2];

		float y = arrY[0];
		float y1 = arrY[1];
		float y2 = arrY[2];
		// g.glLineWidth(line_t);
		drawLine(g, x, y, x1, y1, x2, y2);
		// g.drawRect((int)x-2,(int)y-2,5,5);
		// g.drawRect((int)x1-2,(int)y1-2,5,5);
		// g.drawRect((int)x2-2,(int)y2-2,5,5);
		_ac._cvm.drawRect(g, (int) x - 2, (int) y - 2, 5, 5, at_col);
		_ac._cvm.drawRect(g, (int) x1 - 2, (int) y1 - 2, 5, 5, at_col);
		_ac._cvm.drawRect(g, (int) x2 - 2, (int) y2 - 2, 5, 5, at_col);
		if (isSelect) {
			_ac._cvm.setLineWidth(rectLine_t);
//			_ac._cvm.drawRect(g, x - 2 - selectAreaWidth / 2, y - 2 - selectAreaWidth / 2, selectAreaWidth,
//					selectAreaWidth, CoSys.at_col);
//			_ac._cvm.drawRect(g, x1 - 2 - selectAreaWidth / 2, y1 - 2 - selectAreaWidth / 2, selectAreaWidth,
//					selectAreaWidth, CoSys.at_col);
//			_ac._cvm.drawRect(g, x2 - 2 - selectAreaWidth / 2, y2 - 2 - selectAreaWidth / 2, selectAreaWidth,
//					selectAreaWidth, CoSys.at_col);
			drawSelectedPointRect(g, x, y);
			drawSelectedPointRect(g, x1, y1);
			drawSelectedPointRect(g, x2, y2);

		}
		drawSelectedPointData(g, (int) x, (int) y, xIndex, "" + data[0].y);
		drawSelectedPointData(g, (int) x1, (int) y1, xIndex1, "" + data[1].y);
		drawSelectedPointData(g, (int) x2, (int) y2, xIndex2, "" + data[2].y);
		String price = "" + getChartPrice((int) (y2 + y2 - y));
		price = ChartUtil.getFormatedData(price, _ac._cdm.getPriceFormat(), _ac._cdm);
		_ac._cvm.drawString(g, at_col, x2 + x1 - x, (int) (y2 + y2 - y) - (int) COMUtil.getPixel(7), price);

		g.restore();
	}

	private void drawLine(Canvas g, float x, float y, float x1, float y1, float x2, float y2) {

		// int mmx = (x1 + x2) / 2;
		// int mmy = (y1 + y2) / 2;
		// int startX = in.left;
		// int endX = in.right;
		// float ratio;
		// ratio = getAngle(x, y, mmx, mmy);
		// if (x > mmx) {
		_ac._cvm.drawLine(g, x, y, x1, y1, at_col, 1.0f);
		_ac._cvm.drawLine(g, x1, y1, x2, y2, at_col, 1.0f);
		_ac._cvm.drawLine(g, x2, y2, x2 + x1 - x, y2 + y2 - y, at_col, 1.0f);
		_ac._cvm.drawLine(g, x2, y2 + (y2 - y), x2 + 2 * (x1 - x), y2 + (y2 - y), at_col, 1.0f);

		// } else {
		// _ac._cvm.drawLine(g, x, y, endX, (int) (y + ((endX - x) * ratio)),
		// at_col, 1.0f);
		// _ac._cvm.drawLine(g, x1, y1, endX, (int) (y1 + ((endX - x1) *
		// ratio)), at_col, 1.0f);
		// _ac._cvm.drawLine(g, x2, y2, endX, (int) (y2 + ((endX - x2) *
		// ratio)), at_col, 1.0f);
		// }

	}

	public boolean isSelected(PointF p) {
		if (data[2] == null)
			return false;
		for (int i = 0; i < data.length; i++) {
			float x = dateToX(data[i].x);
			float y = priceToY(data[i].y);
			// left, top, right, bottom
			RectF bound = new RectF(x  - selectAreaWidth / 2, y  - selectAreaWidth / 2,
					x  - selectAreaWidth / 2 + selectAreaWidth, y  - selectAreaWidth / 2 + selectAreaWidth);
			if (bound.contains(p.x, p.y)) {
				select_type = i + 1;
				return true;
			}
		}
		return false;
	}

	public String getTitle() {
		return "목표치NT";
	}
}