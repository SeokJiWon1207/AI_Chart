package drfn.chart.anal;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

import drfn.chart.block.Block;
import drfn.chart.scale.AREA;
import drfn.chart.scale.XScale;
import drfn.chart.scale.YScale;
import drfn.chart.util.COMUtil;
import drfn.chart.util.ChartUtil;
import drfn.chart.util.CoSys;
import drfn.chart.util.DoublePoint;
import drfn.chart.util.MinMax;
public abstract class AnalTool{
    protected int ncount;
    RectF bounds;
    YScale[] yscale=null;
    XScale xscale;
    public Block _ac;
    RectF out;
    RectF in;
    int line_t=1;
    int rectLine_t=1;
    public DoublePoint[] data;
    DoublePoint[] data_org;
    int[] at_col;
    public boolean isSelect=false;
    protected int select_type;//0:이동 , 1: 점선택, 2: 점선택, 3 :점선택
    public double[] PIVOT= {0.236,0.382,0.5,0.618,1,1.618,2.618,4.236};
//    public int selectAreaWidth = (int)COMUtil.getPixel(20);
    public int selectAreaWidth = (int)COMUtil.getPixel(40); //2017.09.05 by pjm 분석툴 선택 영역 개선 >>
    public int curr;
    public String strText="";

    boolean m_bUsePrice;	//2015. 1. 13 분석툴 수정기능 및 자석기능 추가
    public AnalTool(Block ac){
        _ac = ac;
        if(ac==null) {
            return;
        }
        at_col = ac._cvm.CAT;
        //fm = COMUtil.getFontMetrics();
        //ac에서 최대값, 최소값, 화면의 최대,최소 픽셀을 찾는다
        //point x를 데이터로 변환하여 저장
        //yscale = (YScale[])ac._bvm.getYScale().elementAt(0);
        curr = 0;

        setSkinColor();
    }
    public void setSkinColor() {
        if(_ac == null)
            return;
        if(_ac._cvm.getSkinType()==COMUtil.SKIN_BLACK) {
            at_col[0]=255;
            at_col[1]=255;
            at_col[2]=255;
        } else if(_ac._cvm.getSkinType()==COMUtil.SKIN_WHITE) {
            at_col[0]=70;
            at_col[1]=70;
            at_col[2]=70;
        }
        CoSys.at_col = at_col;
    }
    boolean log;
    public float priceToY(double p) {
        if(_ac == null)
            return -1;
        bounds = _ac.getGraphBounds();
        yscale = _ac.getYScale();
        double yfactor;
        int ypnt;
        log= _ac._cvm.isLog;

        if(log&&p!=0) p = (Math.log(p)*1000);
        if(log){
            double min_data = (Math.log(yscale[0].mm_data[0])*1000);
            double max_data = (Math.log(yscale[0].mm_data[1])*1000);
            yfactor = ((bounds.height())*1.0f)/(max_data-min_data);
            ypnt = (int)((max_data-p)*yfactor);

        }else{
            yfactor = ((bounds.height())*1.0f)/(float)(yscale[0].mm_data[1]-yscale[0].mm_data[0]);
            if(yscale[0].format_index == 14 || yscale[0].format_index == 15 || yscale[0].format_index == 16)
                ypnt = (int)((yscale[0].mm_data[1]-p*10000)*yfactor);
            else
                ypnt = (int)((yscale[0].mm_data[1]-p)*yfactor);
        }
        return bounds.top+ypnt;
    }
    public void resetPoint(){
        //2015. 1. 13 분석툴 수정기능 및 자석기능 추가>>
    	if(m_bUsePrice)
        //if(_ac._cvm.getUsePrice())
        {
            if(getTitle().equals("피보나치조정대"))
            {
                int xIndex=getIndexWithDate(data[0].x);
                int xIndex1=getIndexWithDate(data[1].x);

                double[] minmax=getMinMax(xIndex,xIndex1);

                data[0].y = minmax[0];
                data[1].y = minmax[1];
            }
            else if(getTitle().equals("추세선") || getTitle().equals("수평선"))
            {
                if(ncount>0)
                {
                    int xIndex=getIndexWithDate(data[0].x);
                    String strPrice=_ac._cdm.getData("종가", xIndex);
                    data[0].y = Double.parseDouble(strPrice);
                }
                if(ncount == 2)
                {
                    int xIndex=getIndexWithDate(data[1].x);
                    String strPrice=_ac._cdm.getData("종가", xIndex);
                    data[1].y = Double.parseDouble(strPrice);
                }
            }
        }
        //2015. 1. 13 분석툴 수정기능 및 자석기능 추가<<

        for(int i=0;i<data.length;i++){
            if(data[i] == null)
                return;
            data_org[i].x = data[i].x;
            data_org[i].y = data[i].y;
        }
    }
    public void changePoint(PointF p,PointF p1){
        //2015. 1. 29 가상매매연습기 매도/매수 표시>>
        if(getTitle().equals("Trade"))
            return;
        //2015. 1. 29 가상매매연습기 매도/매수 표시 <<

        double price;
        int index;
        switch(select_type){
            case 0://이동
                //2015. 1. 13 분석툴 수정기능 및 자석기능 추가
                if(ncount== 1)
                {
                    price = getChartPrice(p1.y);
                    index = getXToDate(p1.x);
                    //2020.07.06 by LYH >> 캔들볼륨 >>
                    if(index<0)
                        return;
                    //2020.07.06 by LYH >> 캔들볼륨 <<
                    data[0].x=getDateAtIndex(index);
                    data[0].y=price;

                }
                else
                {
                    int xgab=getXToDate(p1.x)-getXToDate(p.x);
                    double ygab=getChartPrice(p1.y)-getChartPrice(p.y);
                    for(int i=0;i<data.length;i++){
                        index = getIndexWithDate(data_org[i].x)+xgab;
                        //2020.07.06 by LYH >> 캔들볼륨 >>
                        if(index<0)
                            return;
                        //2020.07.06 by LYH >> 캔들볼륨 <<
                        data[i].x=getDateAtIndex(index);
                        data[i].y=data_org[i].y+ygab;
                    }
                }
                //2015. 1. 13 분석툴 수정기능 및 자석기능 추가<<
                break;
            case 1://1점
                price = getChartPrice(p1.y);
                index = getXToDate(p1.x);
                //2020.07.06 by LYH >> 캔들볼륨 >>
                if(index<0)
                    return;
                //2020.07.06 by LYH >> 캔들볼륨 <<
                data[0].x=getDateAtIndex(index);
                data[0].y=price;

                if(getTitle().equals("가격변화선"))
                    data[1].y=price;
                break;
            case 2://2점
                price = getChartPrice(p1.y);
                index = getXToDate(p1.x);
                //2020.07.06 by LYH >> 캔들볼륨 >>
                if(index<0)
                    return;
                //2020.07.06 by LYH >> 캔들볼륨 <<
                data[1].x=getDateAtIndex(index);
                data[1].y=price;
                if(getTitle().equals("가격변화선"))
                    data[0].y=price;

                break;
            case 3://3점
                price = getChartPrice(p1.y);
                index = getXToDate(p1.x);
                //2020.07.06 by LYH >> 캔들볼륨 >>
                if(index<0)
                    return;
                //2020.07.06 by LYH >> 캔들볼륨 <<
                data[2].x=getDateAtIndex(index);
                data[2].y=price;
                break;
        }


        //2015. 1. 13 분석툴 수정기능 및 자석기능 추가>>
        if(m_bUsePrice)
        //if(_ac._cvm.getUsePrice())
        {
            if(getTitle().equals("피보나치조정대"))
            {
                int xIndex=getIndexWithDate(data[0].x);
                int xIndex1=getIndexWithDate(data[1].x);

                double[] minmax=getMinMax(xIndex,xIndex1);

                data[0].y = minmax[0];
                data[1].y = minmax[1];
            }
            else if(getTitle().equals("추세선") || getTitle().equals("수평선"))
            {
                if(ncount>0)
                {
                    int xIndex=getIndexWithDate(data[0].x);
                    String strPrice=_ac._cdm.getData("종가", xIndex);
                    data[0].y = Double.parseDouble(strPrice);
                }
                if(ncount == 2)
                {
                    int xIndex=getIndexWithDate(data[1].x);
                    String strPrice=_ac._cdm.getData("종가", xIndex);
                    data[1].y = Double.parseDouble(strPrice);
                }
            }
        }
        //2015. 1. 13 분석툴 수정기능 및 자석기능 추가<<
    }

    public void setColor(int[] c){
        at_col = c;
    }

    public int[] getAtColor() {
        return at_col;
    }

    public void setLineT(int t){
        line_t = t;
    }

    public int getLineT() {
        return line_t;
    }

    public int getPointCount(){
        return ncount;
    }

    public double getChartPrice(float y){
        if(_ac==null) {
            //Log.d("ac", "ac is null");
            return 0;
        }
        bounds = _ac.getGraphBounds();

        yscale = _ac.getYScale();
        log= _ac._cvm.isLog;

        if(yscale[0].mm_data==null) return 0;
        double min = yscale[0].mm_data[0];
        double max = yscale[0].mm_data[1];
        if(log){
            y = y-bounds.top;
            min = Math.log(min)*1000;
            max = Math.log(max)*1000;
            double b = 1.-(y/(double)(bounds.height()));
            return Math.exp((min + (max-min)*b)/1000);
        }else{
            y = y-bounds.top;
            double b = 1.-(y/(double)(bounds.height()));
            if(yscale[0].format_index == 14 || yscale[0].format_index == 15 || yscale[0].format_index == 16)
                return (min + (max-min)*b)/10000;
            else
                return min + (max-min)*b;
        }
    }
    public double[] getMinMax(int idx, int idx1){
        double[] minmax={Integer.MAX_VALUE,Integer.MIN_VALUE};
        if(_ac == null)
            return minmax;
        String[] datakind=_ac.getBasicDataKind();
        if(datakind==null) return null;

        int index=Math.min(idx,idx1);
        int num=Math.abs(idx-idx1);
        double[] mm_data;
        for(int i=0;i<datakind.length;i++){
            double[] data = _ac._cdm.getData(datakind[i],index,num);
            mm_data=MinMax.getMinMax(data);
            minmax[0]=(mm_data[0]<minmax[0])?mm_data[0]:minmax[0];
            minmax[1]=(mm_data[1]>minmax[1])?mm_data[1]:minmax[1];
        }

        return minmax;
    }
    //====================================
    // 픽셀 x좌표로 index를 알아낸다
    //====================================
    public int getXToDate(float x){
        if(_ac==null) return 0;
        //x=x-_ac._cvm.Margin_L;
        x=x-_ac.getGraphBounds().left;
        int index = _ac._cvm.getIndex();
        //2020.07.06 by LYH >> 캔들볼륨 >>
        if(_ac._cvm.m_arrArea.size()>0)
        {
            for(int i=0; i<_ac._cvm.m_arrArea.size(); i++)
            {
                AREA area = _ac._cvm.getArea(i);
                if(i==0 && x >= 0 && x < area.getLeft())
                    return 0;
                if(x>=area.getLeft() && x<=area.getRight_Tot())
                    return index+i;
                if(i==_ac._cvm.m_arrArea.size()-1)
                    return index+i;
            }
            return -1;
        }
        //2020.07.06 by LYH >> 캔들볼륨 <<
        float xfactor = _ac._cvm.getDataWidth();
        int tmp=(index)+(int)(x/xfactor);
        int num=_ac._cdm.getCount();
        if(tmp<0)tmp=0;
        else if(tmp>num-1)tmp=num-1;
        return tmp;
    }
    //====================================
    // index를 픽셀좌표로 return
    //====================================
    public float getDateToX(int x){
        if(_ac == null)
            return -1;
        bounds = _ac.getGraphBounds();
        int index = _ac._cvm.getIndex();
        //2020.07.06 by LYH >> 캔들볼륨 >>
        if(_ac._cvm.m_arrArea.size()>0)
        {
            int nIndex1 = x-index;
            if(nIndex1<0)
                nIndex1 = 0;
            AREA area = _ac._cvm.getArea(nIndex1);
            if(area != null)
                return area.getCenter();
        }
        //2020.07.06 by LYH >> 캔들볼륨 <<
        //int pos = x-index;
        float xfactor = _ac._cvm.getDataWidth();
        int xw = (int)(xfactor/2)-1;
        return (int)(((x-index)*xfactor)+bounds.left+xw);
    }

    //=====================================
    // 기울기를
    //=====================================
    public float getAngle(float x1,float y1, float x2, float y2){
        if ((x1-x2)==0 )
            return 0.0f;

        float f = (float)((y1-y2)/(x1-x2));
        return f;
    }
    public void drawDotLine(Canvas g,float  x,float y, float x1, float y1,boolean hor){
        if(_ac == null)
            return;
        float af;
        float width=Math.abs(x-x1);
        int cnt=(int)width/6;
        if(x>x1){
            if(!hor){
                af= getAngle(x,y,x1,y1);
                float yaf=2*af;
                for(int i=0;i<cnt;i++){
                    int xaf=6*i;
                    int yy=(int)(y-(xaf*af));
                    _ac._cvm.drawLine(g, x-xaf,yy,x-(xaf+2),yy-(int)yaf, at_col, 1.0f);
                }
            }else{
                for(int i=(int)x;i>(int)x1;i-=6) {
                    _ac._cvm.drawLine(g, i,y,i-2,y, at_col, 1.0f);
                }
            }
        }else{
            if(!hor){
                af= getAngle(x,y,x1,y1);
                float yaf=2*af;
                for(int i=0;i<cnt;i++){
                    int xaf=6*i;
                    int yy=(int)(y+(xaf*af));
                    _ac._cvm.drawLine(g, x+xaf,yy,x+xaf+2,yy+(int)yaf, at_col, 1.0f);
                }
            }else{
                for(int i=(int)x;i<(int)x1;i+=6) {
                    _ac._cvm.drawLine(g, i,y,i+2,y, at_col, 1.0f);
                }
            }
        }
    }
    //======================
    //테스트용...
    //======================
    //2017.09.05 by pjm 분석툴 선택 영역 개선 >>
//    public boolean isSelectedLine(Point p,int  x, int y, int x1, int y1,boolean hor){
//        float af= getAngle(x,y,x1,y1);;
//        int width=Math.abs(x-x1);
//        int pix=1;
//        int cnt=width/pix;
//        Rect select;
////        boolean isselect=false;
//        if(x>x1){
//            if(!hor){
////                float yaf=pix*af;
//                for(int i=0;i<cnt;i++){
//                    int xaf=pix*i;
//                    float yy=(y-(xaf*af));
//                    //float yInc = (float)(((float)ypos1-(float)ypos)/1.);
//                    //select = (af>0)?new Rectangle(x-xaf,yy-(int)yaf-5,6,(int)yaf+10):new Rectangle(x-xaf,yy+(int)yaf-5,6,(int)-yaf+10);
//                    //select =new Rect(x-xaf,(int)yy-10,x-xaf+10,(int)yy+10);
//                    select =new Rect(x-xaf,(int)yy-(int)COMUtil.getPixel(10),x-xaf+(int)COMUtil.getPixel(10),(int)yy+(int)COMUtil.getPixel(10));
//                    if(select.contains(p.x, p.y))return true;
//                }
//            }else{
//                //select = new Rect(x1,y1-10,x1+width,y1+10);
//                select = new Rect(x1,y1-(int)COMUtil.getPixel(10),x1+width,y1+(int)COMUtil.getPixel(10));
//                if(select.contains(p.x, p.y))return true;
//            }
//        }else{
//            if(!hor){
////                float yaf=pix*af;
//                for(int i=0;i<cnt;i++){
//                    int xaf=pix*i;
//                    float yy=(y+(xaf*af));
//                    //select = (af>0)?new Rectangle(x+xaf,yy-(int)yaf-5,6,(int)yaf+10):new Rectangle(x+xaf,yy+(int)yaf-5,6,(int)-yaf+10);
//                    //select =new Rect(x+xaf,(int)yy-10,x+xaf+10,(int)yy+10);
//                    select =new Rect(x+xaf,(int)yy-(int)COMUtil.getPixel(10),x+xaf+(int)COMUtil.getPixel(10),(int)yy+(int)COMUtil.getPixel(10));
//                    if(select.contains(p.x, p.y))return true;
//                }
//            }else{
//                //select = new Rect(x,y1-10,x+width,y1+10);
//                select = new Rect(x,y1-(int)COMUtil.getPixel(10),x+width,y1+(int)COMUtil.getPixel(10));
//                if(select.contains(p.x, p.y))return true;
//            }
//        }
//        return false;
//    }
    //2017.09.05 by pjm 분석툴 선택 영역 개선 >>
    public boolean isSelectedLine(PointF p, float x, float y, float x1, float y1, boolean hor) {
        float af= getAngle(x,y,x1,y1);;
        float width=Math.abs(x-x1);
        //2017.09.05 by pjm 분석툴 선택 영역 개선 >>
        float height=Math.abs(y-y1);
        float temp = x;
        if(x<x1){
            x = x1;
            x1 = temp;
            temp = y;
            y = y1;
            y1 = temp;
        }
        //2017.09.05 by pjm 분석툴 선택 영역 개선 <<
        int pix=1;
        int cnt=(int)width/pix;
        RectF select;
        //2017.09.05 by pjm 분석툴 선택 영역 개선 >>
            if (width>=height) {
                for(int i=0;i<cnt;i++){
                    int xaf=pix*i;
                    float yy=(y-(xaf*af));
                    select =new RectF(x-xaf-(int)COMUtil.getPixel(10),(int)yy-(int)COMUtil.getPixel(10),x-xaf+(int)COMUtil.getPixel(10),(int)yy+(int)COMUtil.getPixel(10));
                    if(select.contains(p.x, p.y))
                        return true;
                }
            }
            else{
                cnt=(int)height/pix;
                af= getAngle(y,x,y1,x1);;
                float xx;
                for(int i=0;i<cnt;i++){
                    int yaf=pix*i;
                    if(y>y1)
                    {
                        xx = (x-(yaf*af));
                        select =new RectF((int)xx-(int)COMUtil.getPixel(10),y-yaf-(int)COMUtil.getPixel(10),(int)xx+(int)COMUtil.getPixel(10),y-yaf+(int)COMUtil.getPixel(10));
                    }
                    else
                    {
                        xx = (x+(yaf*af));
                        select =new RectF((int)xx-(int)COMUtil.getPixel(10),y+yaf-(int)COMUtil.getPixel(10),(int)xx+(int)COMUtil.getPixel(10),y+yaf+(int)COMUtil.getPixel(10));
                    }
                    if(select.contains(p.x, p.y))
                        return true;
                    //2017.09.05 by pjm 분석툴 선택 영역 개선 <<
            }
        }
        return false;
    }

    StringBuffer dataBuf= new StringBuffer();
    protected void drawSelectedPointData(Canvas g, int sx, int sy,int idx,String price){
        if(_ac == null)
            return;
        bounds = _ac.getGraphBounds();
        int num = _ac._cdm.getCount();
        idx=(idx<0)?0:(idx>num-1)?num-1:idx;
        String date = _ac._cdm.getFormatData("자료일자", idx);

        if(dataBuf.length()>0) dataBuf.delete(0, dataBuf.length());
        float width;
        int[] x = new int[3];
        int[] y = new int[3];
        price = ChartUtil.getFormatedData(price, _ac._cdm.getPriceFormat(), _ac._cdm);
        dataBuf.append(price);
        dataBuf.append("(");
        dataBuf.append(date);
        dataBuf.append(")");
        y[0] = sy;
        y[1] = sy;
        y[2] = sy+(int)COMUtil.getPixel(2);

        width=_ac._cvm.GetTextLength(dataBuf.toString());
        if(sx+width+(int)COMUtil.getPixel(5)<bounds.left+bounds.right){
            x[0] = sx;
            x[1] = sx+(int)COMUtil.getPixel(3);
            x[2] = sx+(int)COMUtil.getPixel(3);
            sx += (int)COMUtil.getPixel(3);
        }else{
            x[0] = sx;
            x[1] = sx-(int)COMUtil.getPixel(3);
            x[2] = sx-(int)COMUtil.getPixel(3);
            sx -= (width+(int)COMUtil.getPixel(8));
        }
//        COMUtil.drawRect(g, sx, sy, width+10, 18, at_col);
        //2013. 9. 3 추세선 그렸을 때 수치 가시성 여부 설정하는 것 기본환경에 추가>> : 삼성증권 mPop 2.0 추세선 라벨 가시성 제어  요청사항. 
        if(COMUtil.isChuseLineValueTextShow())
        {
            _ac._cvm.drawString(g, at_col, sx+(int)COMUtil.getPixel(2), sy+(int)COMUtil.getPixel(7), dataBuf.toString());
        }
    }

    protected void drawSelectedPointData(Canvas g, int sx, int sy,int idx,String price, String rate){
        if(_ac == null)
            return;
        bounds = _ac.getGraphBounds();
        int num = _ac._cdm.getCount();
        idx=(idx<0)?0:(idx>num-1)?num-1:idx;
        String date = _ac._cdm.getFormatData("자료일자", idx);

        if(dataBuf.length()>0) dataBuf.delete(0, dataBuf.length());
        float width;
        int[] x = new int[3];
        int[] y = new int[3];
        price = ChartUtil.getFormatedData(price, _ac._cdm.getPriceFormat(), _ac._cdm);
        dataBuf.append(price);
        dataBuf.append("(");
        dataBuf.append(rate);
        dataBuf.append(",");
        dataBuf.append(date);
        dataBuf.append(")");
        y[0] = sy;
        y[1] = sy;
        y[2] = sy+(int)COMUtil.getPixel(2);

        width=_ac._cvm.GetTextLength(dataBuf.toString());
        if(sx+width+10<bounds.left+bounds.right){
            x[0] = sx;
            x[1] = sx+(int)COMUtil.getPixel(3);
            x[2] = sx+(int)COMUtil.getPixel(3);
            sx += (int)COMUtil.getPixel(3);
        }else{
            x[0] = sx;
            x[1] = sx-(int)COMUtil.getPixel(3);
            x[2] = sx-(int)COMUtil.getPixel(3);
            sx -= (width+(int)COMUtil.getPixel(8));
            if(sx<0)
                sx = 0;
        }
//        COMUtil.drawRect(g, sx, sy, width+10, 18, at_col);

        //2013. 9. 3 추세선 그렸을 때 수치 가시성 여부 설정하는 것 기본환경에 추가>> : 삼성증권 mPop 2.0 추세선 라벨 가시성 제어  요청사항.
        if(COMUtil.isChuseLineValueTextShow())
        {
            _ac._cvm.drawString(g, at_col, sx+(int)COMUtil.getPixel(2), sy+(int)COMUtil.getPixel(7), dataBuf.toString());
        }
        //2013. 9. 3 추세선 그렸을 때 수치 가시성 여부 설정하는 것 기본환경에 추가>>
    }

    public void removeAll(){
        for(int i=0;i<ncount;i++){
            data[i]=null;
        }
    }
    public abstract boolean isSelected(PointF p);
    public void addPoint(PointF p)
    {
        synchronized(this) {
            if(curr >= ncount)
                return;

            double price = getChartPrice(p.y);
            int index = getXToDate(p.x);
            double date = getDateAtIndex(index);

            data[curr] = new DoublePoint(date,price);
            data_org[curr] = new DoublePoint(date,price);
            curr++;
        }
    }
    public void addAnalInfo(double date, double price)
    {
        if(curr >= ncount)
            return;

        data[curr] = new DoublePoint(date,price);
        data_org[curr] = new DoublePoint(date,price);
        curr++;
    }

    public int getIndexWithDate(double date)
    {
        int xIndex = -1;
        if(_ac == null || _ac._cdm == null)
            return xIndex;

        String[] dates = _ac._cdm.getStringData("자료일자");
        if(dates==null) return -1;
        int dateCnt = dates.length;
//        String strDate = ""+date;
//        int nDate = (int)date;
        for(int mm=0; mm<dateCnt; mm++) {
			double dValue = Double.parseDouble(dates[mm]);
            try
            {
                if(date == dValue) {
                    xIndex = mm;
                    break;
                }
            }catch(Exception e)
            {

            }
        }
        return xIndex;
    }

    public float dateToX(double date)
    {
        int nIndex = getIndexWithDate(date);
        return getDateToX(nIndex);
    }

    double getDateAtIndex(int index)
    {
        //if(_ac == null)
        if(_ac == null || index<0)  //2020.07.06 by LYH >> 캔들볼륨
            return -1;
        double rtnVal = 0;
        try {
            rtnVal = Double.parseDouble(_ac._cdm.getData("자료일자", index));
        } catch(Exception e) {

        }
        return rtnVal;
    }

    public abstract void draw(Canvas gl);
    //픽셀좌표가 아니라 데이터 인덱스를 넣어준다
    public void addPoint(int index){
        data[0]=new DoublePoint(index,0);
        data_org[0]=new DoublePoint(index,0);
    }
    public DoublePoint[] getPoint(){
        return this.data;
    }
    public String getTitle() {
        return "";
    }

    //2015. 1. 13 분석툴 수정기능 및 자석기능 추가>>
    public void changeValue(int nIndex, double dValue)
    {
        data[nIndex].y = dValue;
    }

    public boolean getUsePrice()
    {
        return m_bUsePrice;
    }

    public void setUsePrice(boolean bUsePrice)
    {
        m_bUsePrice = bUsePrice;
    }
    //2015. 1. 13 분석툴 수정기능 및 자석기능 추가<<

    //2017.04.18 by LYH >> 추세선등 그리기툴 선택 개선
    public void drawSelectedPointRect(Canvas g, float x, float y)
    {
        _ac._cvm.drawRect(g, x-selectAreaWidth/2, y-selectAreaWidth/2, selectAreaWidth, selectAreaWidth, CoSys.at_col);     //2017.09.05 by pjm 분석툴 선택 영역 개선 >>
    }
    //2017.04.18 by LYH << 추세선등 그리기툴 선택 개선
}