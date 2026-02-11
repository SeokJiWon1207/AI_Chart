package drfn.chart.anal;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

import drfn.chart.block.Block;
import drfn.chart.util.COMUtil;
import drfn.chart.util.CoSys;
import drfn.chart.util.DoublePoint;
public class ATradeTool extends AnalTool{
    private boolean m_bDown = false;
    private boolean bFirstDraw = false;
    String tradeData[];

    double dDate;

    public ATradeTool(Block ac){
        super(ac);
        ncount = 1;
        data = new DoublePoint[ncount];
        data_org = new DoublePoint[ncount];

        tradeData = new String[5];

        dDate = 0;
    }

    public void draw(Canvas g){
        in = _ac.getOutBounds();
        out=_ac._cvm.getBounds();

        if(data[0]==null)return;
        int xIndex=getIndexWithDate(data[0].x);
        float x=getDateToX(xIndex);
        float y = priceToY(data[0].y);

        //2021.06.28 by lyk - kakaopay - improve trade badge image performance >>
        int startIndex=_ac._cvm.getIndex();
        int startPos=startIndex;
        int dataLen = startPos + _ac._cvm.getViewNum() + _ac._cvm.futureMargin;
        int nTotCnt = _ac._cdm.getCount();
        if(dataLen>nTotCnt)
            dataLen = nTotCnt;

        //2021.09.29 by lyk - kakaopay - 매매내역 표시 차트 영역 벗어나는 문제 수정 >>
        if((x < (in.left + in.width() - COMUtil.getPixel(16)/2)) && ((xIndex >= startIndex) && (xIndex <= startIndex + dataLen))) {

        } else {
            return;
        }
        //2021.09.29 by lyk - kakaopay - 매매내역 표시 차트 영역 벗어나는 문제 수정 >>
        //2021.06.28 by lyk - kakaopay - improve trade badge image performance <

        //2021.06.21 by lyk - kakaopay - 매매내역 최초 실행시 addAnalInfo에서 설정된 블럭 마진값이 minmax 실행전 처리되므로 draw시 최초 한번 reSetUI:true를 호출해야한다) >>
        if(bFirstDraw) {

            if(y >= (_ac.getHeight() - _ac.margineB - _ac.margineT)) {
                _ac.setMarginB((int)COMUtil.getPixel(19) + (int)COMUtil.getPixel(_ac.BLOCK_BOTTOM_MARGIN));
                _ac.setStateSize(true);
            }

            if(y <= (_ac.getY() + _ac.margineT)) {
                _ac.setMarginT((int)COMUtil.getPixel(_ac.BLOCK_TOP_MARGIN) + (int)COMUtil.getPixel(16));
                _ac.setStateSize(true);
            }

            _ac.parentView.reSetUI(true);
            bFirstDraw = false;
        }
        //2021.06.21 by lyk - kakaopay - 매매내역 최초 실행시 addAnalInfo에서 설정된 블럭 마진값이 minmax 실행전 처리되므로 draw시 최초 한번 reSetUI:true를 호출해야한다) <<

        g.save();
        g.clipRect(in.left ,in.top, in.left+in.width(), in.top+in.height());

        int layoutResId;
        Bitmap image;
        if(m_bDown)
        {
            int[] lineColor = CoSys.trade_sell_dot_color;
//            layoutResId = COMUtil.apiView.getContext().getResources().getIdentifier("kp_mts_ic_common_arrow_sell", "drawable", COMUtil.apiView.getContext().getPackageName());
//            image = BitmapFactory.decodeResource(COMUtil.apiView.getContext().getResources(), layoutResId);
            if(_ac._cvm.imgTradeSell != null) {
                _ac._cvm.drawImage(g, x - (int) COMUtil.getPixel(8), y - (int) COMUtil.getPixel(24), (int) COMUtil.getPixel(16), (int) COMUtil.getPixel(16), _ac._cvm.imgTradeSell, 255);
                _ac._cvm.drawDashDotDotLine(g, x, y, x, y - (int) COMUtil.getPixel(24), lineColor, 1.0f);
            }
//            _ac._cvm.drawString(g, CoSys.CHART_COLORS[1], x-(int)COMUtil.getPixel(6), y-(int)COMUtil.getPixel(30), tradeData[2]);
        }
        else
        {
            int[] lineColor = CoSys.trade_buy_dot_color;
//            layoutResId = COMUtil.apiView.getContext().getResources().getIdentifier("kp_mts_ic_common_arrow_buy", "drawable", COMUtil.apiView.getContext().getPackageName());
//            image = BitmapFactory.decodeResource(COMUtil.apiView.getContext().getResources(), layoutResId);
            if(_ac._cvm.imgTradeBuy != null) {
                _ac._cvm.drawImage(g, x - (int) COMUtil.getPixel(8), y + (int) COMUtil.getPixel(7), (int) COMUtil.getPixel(16), (int) COMUtil.getPixel(16), _ac._cvm.imgTradeBuy, 255);
                _ac._cvm.drawDashDotDotLine(g, x, y, x, y + (int) COMUtil.getPixel(7), lineColor, 1.0f);
            }
//            _ac._cvm.drawString(g, CoSys.CHART_COLORS[0], x-(int)COMUtil.getPixel(6), y+(int)COMUtil.getPixel(34), tradeData[2]);
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
        RectF bound= new RectF(x-selectAreaWidth/2, y-selectAreaWidth/2,x-selectAreaWidth/2+ selectAreaWidth, y-selectAreaWidth/2+selectAreaWidth);
        if(bound.contains(p.x, p.y)){
            select_type=0;
            return true;
        }
        return false;
    }
    public void addAnalInfo(String[] strOneArray)
    {
        if(curr >= ncount)
            return;

        double dDate = 0;
        double price = 0;
        //"매수매도구분", "자료일자", "평균", "수량"
        tradeData[0] = "";
        tradeData[1] = "";
        tradeData[2] = "";
        tradeData[3] = "";

//        String[] strOneArray = strTradeInfo.split("\\^");
//        if(strOneArray.length>=4)
//        {

            String strTradeType = strOneArray[0];
            String strDate = strOneArray[1];
            for(int i=0;i<strOneArray.length; i++)
            {
                tradeData[i] =  strOneArray[i];
            }
//            NSString* strAcct = [strOneArray objectAtIndex:2];
//            NSString* strPrice = [strOneArray objectAtIndex:3];
//            NSString* strQty = [strOneArray objectAtIndex:4];

            try{
                dDate = Double.parseDouble(strDate);
            }catch(Exception e)
            {

            }


            int idx = getIndexWithDate(dDate);

            if(strTradeType.equals("1")) // 매도
            {
                m_bDown = true;
                String strPrice = _ac._cdm.getData("고가", idx);
                try
                {
                    price = Double.parseDouble(strPrice);
                }catch(Exception e)
                {

                }
                //               price = [[_ac._cdm getDataAtIndex:@"고가" index:idx] floatValue];
            }
            else // 매수
            {
                m_bDown = false;
                String strPrice = _ac._cdm.getData("저가", idx);
                try
                {
                    price = Double.parseDouble(strPrice);
                }catch(Exception e)
                {

                }
//                price = [[_ac._cdm getDataAtIndex:@"저가" index:idx] floatValue];
            }
//        }

        data[curr] = new DoublePoint(dDate,price);
        data_org[curr] = new DoublePoint(dDate,price);

        curr++;
        //2023.11.21 by SJW - 매매내역 영역 사이즈 여러 번 잡던 현상 수정 >>
        //2021.06.22 by lyk - kakaopay - 매매내역 뱃지 위치에 따른 블럭 상/하 마진 값 조정 >>
//        float y = this.priceToY(price);
//        if((y+(int)COMUtil.getPixel(16)) >= (_ac.getHeight() - _ac.margineB - _ac.margineT)) {
//            _ac.setMarginB(19 + (int)COMUtil.getPixel(_ac.BLOCK_BOTTOM_MARGIN));
//            _ac.stateSize = true;
//        }
//
//        if(y <= (_ac.getY() + _ac.margineT)) {
//            _ac.setMarginT(16 + (int)COMUtil.getPixel(_ac.BLOCK_TOP_MARGIN));
//            _ac.stateSize = true;
//        }
        //2023.11.21 by SJW - 매매내역 영역 사이즈 여러 번 잡던 현상 수정 <<
        bFirstDraw = true;
//    //2021.06.22 by lyk - kakaopay - 매매내역 뱃지 위치에 따른 블럭 상/하 마진 값 조정 <<
    }

    public String[] getTradeData()
    {
        return tradeData;
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
        //2023.03.02 by SJW - 구매/판매 표시 선택 시, 인포윈도우 위치가 표시 위치보다 상단에 노출되는 현상 수정 >>
//        if(m_bDown) {
//            y = y-(int)COMUtil.getPixel(24) + (int)(COMUtil.getPixel(16)/2);
//        } else {
//            y = y+(int)COMUtil.getPixel(7) + (int)(COMUtil.getPixel(16)/2);
//        }
        if(m_bDown) {
            y += (int) COMUtil.getPixel(2);
        } else {
            y += (int) COMUtil.getPixel(17) * 2;
        }
        //2023.03.02 by SJW - 구매/판매 표시 선택 시, 인포윈도우 위치가 표시 위치보다 상단에 노출되는 현상 수정 <<
        return y;
    }

    /**
     * ATradeTool의 날짜값 반환
     * @return dDate 날짜값
     * */
    public double getDate()
    {
        return dDate;
    }

    /**
     * ATradeTool의 수량값 변경
     * @return strAmount 수량값
     * */
    public void changeAmount(String strAmount)
    {
        tradeData[3] = strAmount;
    }

    public String getTitle() {
        return "Trade";
    }
}