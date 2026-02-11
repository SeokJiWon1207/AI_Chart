package drfn.chart.anal;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

import drfn.chart.block.Block;
import drfn.chart.util.COMUtil;
import drfn.chart.util.DoublePoint;
public class AUpperLowerLimitTool extends AnalTool{
    private boolean m_bDown = false;

    double dDate;

    public AUpperLowerLimitTool(Block ac){
        super(ac);
        ncount = 1;
        data = new DoublePoint[ncount];
        data_org = new DoublePoint[ncount];

        dDate = 0;
    }

    public void draw(Canvas g){
        in = _ac.getOutBounds();
        out=_ac._cvm.getBounds();

        if(data[0]==null)return;
        int xIndex=getIndexWithDate(data[0].x);
        float x=getDateToX(xIndex);
        float y = priceToY(data[0].y);

        g.save();
        g.clipRect(in.left ,in.top, in.left+in.width(), in.top+in.height());

        int layoutResId;
        Bitmap image;
        if(m_bDown)
        {
//        	_ac._cvm.drawString(g, CoSys.CHART_COLORS[1], x-(int)COMUtil.getPixel(6), y+(int)COMUtil.getPixel(10), "하");
            layoutResId = COMUtil.apiView.getContext().getResources().getIdentifier("chart_upperlowerlimit_down", "drawable", COMUtil.apiView.getContext().getPackageName());
            image = BitmapFactory.decodeResource(COMUtil.apiView.getContext().getResources(), layoutResId);
            _ac._cvm.drawImage(g, x-(int)COMUtil.getPixel(6), y+(int)COMUtil.getPixel(5), (int)COMUtil.getPixel(11), (int)COMUtil.getPixel(18), image, 255);
        }
        else
        {
//    		_ac._cvm.drawString(g, CoSys.CHART_COLORS[0], x-(int)COMUtil.getPixel(6), y-(int)COMUtil.getPixel(10), "상");
            layoutResId = COMUtil.apiView.getContext().getResources().getIdentifier("chart_upperlowerlimit_up", "drawable", COMUtil.apiView.getContext().getPackageName());
            image = BitmapFactory.decodeResource(COMUtil.apiView.getContext().getResources(), layoutResId);
            _ac._cvm.drawImage(g, x-(int)COMUtil.getPixel(6), y-(int)COMUtil.getPixel(23), (int)COMUtil.getPixel(11), (int)COMUtil.getPixel(18), image, 255);
        }
        g.restore();
    }
    public boolean isSelected(PointF p){
        selectAreaWidth = (int)COMUtil.getPixel(30);

        if(data[0]==null)return false;
        float x = dateToX(data[0].x);
        float y = priceToY(data[0].y);
        if(m_bDown)
        {
            y-=(int)COMUtil.getPixel(10);
        }
        else
        {
            y+=(int)COMUtil.getPixel(10);
        }
        RectF bound= new RectF(x-2-selectAreaWidth/2, y-2-selectAreaWidth/2,x-2-selectAreaWidth/2+ selectAreaWidth, y-2-selectAreaWidth/2+selectAreaWidth);
        if(bound.contains(p.x, p.y)){
            select_type=0;
            return true;
        }
        return false;
    }
    public void addAnalInfo(String strTradeInfo)
    {
        if(curr >= ncount)
            return;

        double price = 0;

        String[] strOneArray = strTradeInfo.split("\\^");
        if(strOneArray.length==2)
        {
            //하한가(3) 상한가(7) 구분 
            String strTradeType = strOneArray[1];

            //날짜
            dDate= Double.parseDouble(strOneArray[0]);
            int idx = getIndexWithDate(dDate);

            if(strTradeType.equals("7"))
            {
                m_bDown = false;
                String strPrice = _ac._cdm.getData("고가", idx);
                try
                {
                    price = Double.parseDouble(strPrice);
                }catch(Exception e)
                {

                }
            }
            else  if(strTradeType.equals("3"))
            {
                m_bDown = true;
                String strPrice = _ac._cdm.getData("저가", idx);
                try
                {
                    price = Double.parseDouble(strPrice);
                }catch(Exception e)
                {

                }
            }
        }

        data[curr] = new DoublePoint(dDate,price);
        data_org[curr] = new DoublePoint(dDate,price);
        curr++;
    }

    public float getXPos()
    {
        if(data[0]==null)return -1;
        int xIndex=getIndexWithDate(data[0].x);
        float x=getDateToX(xIndex);
        return x;
    }

    public float getYPos()
    {
        if(data[0]==null)return -1;
        float y = priceToY(data[0].y);
        return y;
    }

    public String getTitle() {
        return "상하한가 표시";
    }
}