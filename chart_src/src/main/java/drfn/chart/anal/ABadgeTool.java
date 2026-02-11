package drfn.chart.anal;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;

import drfn.chart.block.Block;
import drfn.chart.util.COMUtil;
import drfn.chart.util.CoSys;
import drfn.chart.util.DoublePoint;

/**
 * @author hanjun.Kim
 */
public class ABadgeTool extends AnalTool {
    String badgeData[];
    double dDate;

    public ABadgeTool(Block ac) {
        super(ac);
        ncount = 1;
        data = new DoublePoint[ncount];
        data_org = new DoublePoint[ncount];

        badgeData = new String[16];

        dDate = 0;
    }

    public void draw(Canvas canvas) {
        in = _ac.getOutBounds();
        out = _ac._cvm.getBounds();

        if (data[0] == null) return;
        int xIndex = getIndexWithDate(data[0].x);
        float x = getDateToX(xIndex);
//        float y = in.height();
//        float y = getBadgeY();//2021.04.28 by lyk - kakaopay - 주차트 블록 밖 전체 차트 하단에 표시
        float y = out.top + out.height() + 2;

        canvas.save();
        canvas.clipRect(out.left, out.top, out.left + out.width(), out.top + out.height());

        int layoutResId;
        Bitmap image;

        String strImgDozen = "kfit_mts_ic_common_company_dozen";
        if(COMUtil.currentTheme == COMUtil.SKIN_BLACK) {
            strImgDozen = "kfit_mts_ic_common_company_dozen_dark";
        }

        if (badgeData[0].equals("D")) { // 권리
            layoutResId = COMUtil.apiView.getContext().getResources().getIdentifier("kfit_mts_img_candlechart_company_right", "drawable", COMUtil.apiView.getContext().getPackageName());
            image = BitmapFactory.decodeResource(COMUtil.apiView.getContext().getResources(), layoutResId);
            _ac._cvm.drawImage(canvas, x - (int) COMUtil.getPixel(8), y - (int) COMUtil.getPixel(20), (int) COMUtil.getPixel(15), (int) COMUtil.getPixel(15), image, 255);
            float dotX = x - (int) COMUtil.getPixel(8) + (int) (COMUtil.getPixel(15)/2);
            _ac._cvm.drawDashDotDotLine(canvas, dotX, y - (int) COMUtil.getPixel(22), dotX, getYPos(), CoSys.dotLineColor, 1.0f);
        } else if (badgeData[0].equals("E")) { // 재무
            layoutResId = COMUtil.apiView.getContext().getResources().getIdentifier("kfit_mts_img_candlechart_company_performance", "drawable", COMUtil.apiView.getContext().getPackageName());
            image = BitmapFactory.decodeResource(COMUtil.apiView.getContext().getResources(), layoutResId);
            _ac._cvm.drawImage(canvas, x - (int) COMUtil.getPixel(8), y - (int) COMUtil.getPixel(20), (int) COMUtil.getPixel(15), (int) COMUtil.getPixel(15), image, 255);
            float dotX = x - (int) COMUtil.getPixel(8) + (int) (COMUtil.getPixel(15)/2);
            _ac._cvm.drawDashDotDotLine(canvas, dotX, y - (int) COMUtil.getPixel(22), dotX, getYPos(), CoSys.dotLineColor, 1.0f);
        } else if (badgeData[0].equals("DE")) {
            layoutResId = COMUtil.apiView.getContext().getResources().getIdentifier(strImgDozen, "drawable", COMUtil.apiView.getContext().getPackageName());
            image = BitmapFactory.decodeResource(COMUtil.apiView.getContext().getResources(), layoutResId);
            _ac._cvm.drawImage(canvas, x - (int) COMUtil.getPixel(12), y - (int) COMUtil.getPixel(20), (int) COMUtil.getPixel(24), (int) COMUtil.getPixel(15), image, 255);
            float dotX = x - (int) COMUtil.getPixel(8) + (int) (COMUtil.getPixel(15)/2);
            _ac._cvm.drawDashDotDotLine(canvas, dotX, y - (int) COMUtil.getPixel(22), dotX, getYPos(), CoSys.dotLineColor, 1.0f);
        }

        canvas.restore();
    }

    public boolean isSelected(PointF p) {
        selectAreaWidth = (int) COMUtil.getPixel(30);

        if (data[0] == null) return false;
        float x = dateToX(data[0].x);
        float y = getBadgeY();
        y -= (int) COMUtil.getPixel(32);
        RectF bound = new RectF(x - selectAreaWidth / 2, y - selectAreaWidth / 2, x - selectAreaWidth / 2 + selectAreaWidth, y - selectAreaWidth / 2 + selectAreaWidth);
        if (bound.contains(p.x, p.y)) {
            select_type = 0;
            return true;
        }
        return false;
    }

    public void addBadgeInfo(String strBadgeInfo) {
        if (curr >= ncount)
            return;

        double dDate = 0;
        double price = 0;
        badgeData[0] = "";
        badgeData[1] = "";
        badgeData[2] = "";
        badgeData[3] = "";
        badgeData[4] = ""; //
        badgeData[5] = "";
        badgeData[6] = "";
        badgeData[7] = "";
        badgeData[8] = "";
        badgeData[9] = "";
        badgeData[10] = "";
        badgeData[11] = "";
        badgeData[12] = "";
        badgeData[13] = "";
        badgeData[14] = "";
        badgeData[15] = "";

        String[] strOneArray = strBadgeInfo.split("\\^");
        if (strOneArray.length >= 3) {
            String strBadgeType = strOneArray[0];
            String strDate = strOneArray[1];
            for (int i = 0; i < strOneArray.length; i++) {
                badgeData[i] = strOneArray[i];
            }

            try {
                dDate = Double.parseDouble(strDate);
            } catch (Exception e) {

            }
        }

        data[curr] = new DoublePoint(dDate, price);
        data_org[curr] = new DoublePoint(dDate, price);
        curr++;
    }

    public String[] getBadgeData() {
        return badgeData;
    }

    public float getXPos() {
        if (data[0] == null) return -1;
        int xIndex = getIndexWithDate(data[0].x);
        float x = getDateToX(xIndex);
        return x;
    }

    //2021. 4. 29  by hanjun.Kim - kakaopay - 배지아이콘 Y값 참조통일 >>
    private float getBadgeY() {
        return _ac._cvm.getBounds().height() + _ac._cvm.XSCALE_H;
    }
    //2021. 4. 29  by hanjun.Kim - kakaopay - 배지아이콘 Y값 참조통일 <<
    // XSCALE_H 날짜표시선 높이값 // EVENT_BADGE_H 배지아이콘표시 여백높이

    // badge 클릭시 표시되는 viewpanel Y값
    public float getYPos() {
        return _ac._cvm.getBounds().height() - _ac._cvm.XSCALE_H  - _ac._cvm.EVENT_BADGE_H;
    }

    /**
     * AbadgeTool의 날짜값 반환
     *
     * @return dDate 날짜값
     */
    public double getDate() {
        return dDate;
    }

    public String getTitle() {
        return "Badge";
    }
}
