package drfn.chart.block;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import drfn.chart.util.COMUtil;

public class ChartBlockPopup extends LinearLayout {

    private Context mContext;

    private int nLayoutWidth = 200;
    private int nLayoutHeight = 50;

    private ImageView divider1 = null;
    private ImageView divider2 = null;

    public Button btn1 = null;
    public Button btn2 = null;
    public Button btn3 = null;

    //Constants
    final int BACKGROUND_LEFT_RIGHT_MARGIN = 0;
    final int BUTTON_WIDTH = 62;
    final int BUTTON_HEIGHT = 30;
    final int DIVIDER_WIDTH = 1;
    final int DIVIDER_HEIGHT = 12;

    private ViewGroup.MarginLayoutParams lpChartFrame = null;

    public ChartBlockPopup(Context context) {
        super(context);

        mContext = context;

        //Background
        this.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        this.setBackgroundResource(context.getResources().getIdentifier("subbar", "drawable", context.getPackageName()));
        this.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
        this.setPadding(0, 0, 0, 0);

        //Button1
        if (btn1 == null) {
            btn1 = new Button(context);
            btn1.setLayoutParams(new LayoutParams((int) COMUtil.getPixel_W(BUTTON_WIDTH), (int) COMUtil.getPixel_H(BUTTON_HEIGHT)));
            btn1.setBackgroundColor(Color.TRANSPARENT);
            btn1.setTextColor(Color.WHITE);
            btn1.setTypeface(COMUtil.typeface);
            btn1.setTextSize(12);
            btn1.setText("이동");

            btn1.setPadding(0, 0, 0, 0);
            this.addView(btn1);
        }

        //Line between Button1 and Button2
        if (divider1 == null) {
            divider1 = new ImageView(context);
            divider1.setLayoutParams(new LayoutParams((int) COMUtil.getPixel_W(DIVIDER_WIDTH), (int) COMUtil.getPixel_H(DIVIDER_HEIGHT)));
//            divider1.setBackgroundColor(Color.WHITE);
            divider1.setBackgroundColor(Color.rgb( 136, 136, 136));
            this.addView(divider1);
        }

        //Button2
        if (btn2 == null) {
            btn2 = new Button(context);
            btn2.setLayoutParams(new LayoutParams((int) COMUtil.getPixel_W(BUTTON_WIDTH), (int) COMUtil.getPixel_H(BUTTON_HEIGHT)));
            btn2.setBackgroundColor(Color.TRANSPARENT);
            btn2.setTextColor(Color.WHITE);
            btn2.setTypeface(COMUtil.typeface);
            btn2.setTextSize(12);
            btn2.setText("새블록");

            btn2.setPadding(0, 0, 0, 0);
            this.addView(btn2);
        }

        //Line between Button2 and Button3
        if (divider2 == null) {
            divider2 = new ImageView(context);
            divider2.setLayoutParams(new LayoutParams((int) COMUtil.getPixel_W(DIVIDER_WIDTH), (int) COMUtil.getPixel_H(DIVIDER_HEIGHT)));
//            divider2.setBackgroundColor(Color.WHITE);
            divider2.setBackgroundColor(Color.rgb( 136, 136, 136));
            this.addView(divider2);
        }

        //Button2
        if (btn3 == null) {
            btn3 = new Button(context);
            btn3.setLayoutParams(new LayoutParams((int) COMUtil.getPixel_W(BUTTON_WIDTH), (int) COMUtil.getPixel_H(BUTTON_HEIGHT)));
            btn3.setBackgroundColor(Color.TRANSPARENT);
            btn3.setTextColor(Color.WHITE);
            btn3.setTypeface(COMUtil.typeface);
            btn3.setTextSize(12);
            btn3.setText("블록병합");

            btn3.setPadding(0, 0, 0, 0);
            this.addView(btn3);
        }
    }

    public void setChartFrame(ViewGroup.LayoutParams lpChartFrame) {
        this.lpChartFrame = (ViewGroup.MarginLayoutParams)lpChartFrame;
    }

    public void setUI(int nButtonCount) {
        //Components Show/Hide
        switch (nButtonCount) {
            case 1:
                divider1.setVisibility(GONE);
                btn2.setVisibility(GONE);
                divider2.setVisibility(GONE);
                btn3.setVisibility(GONE);
                nLayoutWidth = BUTTON_WIDTH + BACKGROUND_LEFT_RIGHT_MARGIN * 2;
                break;

            case 2:
                divider1.setVisibility(VISIBLE);
                btn2.setVisibility(VISIBLE);
                divider2.setVisibility(GONE);
                btn3.setVisibility(GONE);
                nLayoutWidth = BUTTON_WIDTH * 2 + BACKGROUND_LEFT_RIGHT_MARGIN * 2;
                break;

            case 3:
                divider1.setVisibility(VISIBLE);
                btn2.setVisibility(VISIBLE);
                divider2.setVisibility(VISIBLE);
                btn3.setVisibility(VISIBLE);
                nLayoutWidth = BUTTON_WIDTH * 3 + BACKGROUND_LEFT_RIGHT_MARGIN * 2;
                break;

            default:
                break;
        }

        nLayoutHeight = BUTTON_HEIGHT;
    }

    public void setPoint(PointF point) {
        float nX = point.x;
        float nY = point.y;

        float nRectRight = point.x + COMUtil.getPixel_W(nLayoutWidth);
        int nRightMargin = (int)COMUtil.getPixel_W(10);
        if (nRectRight > (lpChartFrame.width + nRightMargin)){
            nX = lpChartFrame.width - ((int)COMUtil.getPixel_W(nLayoutWidth) + nRightMargin);
        }

        float nRectBottom = point.y + (int)COMUtil.getPixel_H(nLayoutHeight);
        if (nRectBottom > lpChartFrame.height){
            nY = lpChartFrame.height - (int)COMUtil.getPixel_H(nLayoutHeight);
        }
        else if (point.y < 0){
            nY = 0;
        }

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int)COMUtil.getPixel_W(nLayoutWidth), (int)COMUtil.getPixel_H(nLayoutHeight));
        layoutParams.leftMargin = (int)(nX + lpChartFrame.leftMargin);
        layoutParams.topMargin = (int)(nY + lpChartFrame.topMargin);
        //layoutParams.height =
        this.setPadding(
                (int) COMUtil.getPixel_W(BACKGROUND_LEFT_RIGHT_MARGIN),
                0,
                (int) COMUtil.getPixel_W(BACKGROUND_LEFT_RIGHT_MARGIN),
                0);

        this.setLayoutParams(layoutParams);
    }
}
