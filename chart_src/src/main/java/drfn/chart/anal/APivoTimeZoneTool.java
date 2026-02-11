package drfn.chart.anal;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.Calendar;

import drfn.chart.block.Block;
import drfn.chart.util.CoSys;
import drfn.chart.util.DoublePoint;

public class APivoTimeZoneTool extends AnalTool{
    private final int[] pivot = {1,2,3,5,8,13,21,34,55,89,144,233,377};
    //private final int[] pivot = {5,9,17,26,33,42,52,65,76,129,172,226};
    public APivoTimeZoneTool(Block ac){
        super(ac);
        ncount = 1;
        data = new DoublePoint[ncount];
        data_org = new DoublePoint[ncount];
    }

    public void draw(Canvas g){
        in = _ac.getOutBounds();
        out=_ac._cvm.getBounds();
        _ac._cvm.setLineWidth(line_t);

        g.save();
        g.clipRect(in.left ,in.top, in.left+in.width(), in.top+in.height());
        //       g.glLineWidth(line_t);

        if(data[0]==null)return;
        int xIndex=getIndexWithDate(data[0].x);
        float x = getDateToX(xIndex);
        float y = priceToY(data[0].y);

        _ac._cvm.drawLine(g, x,in.top,x,in.top+in.height(), at_col, 1.0f);
        //String[] tmp=_ac._cdm.getDatas(data[0].x);
        //String date = tmp[0];
        String date = _ac._cdm.getData("자료일자", xIndex);
//        int w = COMUtil.tf.GetTextLength(""+date)+15;
//        int h = COMUtil.tf.GetTextHeight();

        int last=xIndex;
        for(int i=0;i<pivot.length;i++){
            x = getDateToX(last+pivot[i]);

            _ac._cvm.drawLine(g, x,in.top,x,in.top+in.height(), at_col, 1.0f);
            String date2 = date;
            String date3 = _ac._cdm.getData("자료일자", last+pivot[i]);

            boolean state = true;
            if(!date3.equals("")) {
                state = false;
            }

            //if(state && (comp2 >= today)) { //오늘날짜보다 큰 범위일때
            if(date2.length()==4) { //년봉
                if(state) { //오늘날짜보다 큰 범위일때
                    date2 = getDate(date2, pivot[i]);
                } else {
                    date2 = date3.substring(2);
                }
            } else if(date2.length()==6) { //월봉
                if(state) { //오늘날짜보다 큰 범위일때
                    date2 = getDate(date2, pivot[i]);
                } else {
                    date2 = date3.substring(4);
                }
            } else if(date2.length()==8) { //일봉, 주봉
                if(state) { //오늘날짜보다 큰 범위일때
                    date2 = getDate(date2, pivot[i]);
                } else {
                    date2 = date3.substring(6);
                }
            }
            if(date2==null) return;
            _ac._cvm.drawString(g, at_col, x, in.top+_ac.getBounds().height()-30, date2);
            _ac._cvm.drawString(g, at_col, x, in.top+_ac.getBounds().height()-15, "("+pivot[i]+")");
            last += pivot[i];
        }
        if(isSelect){
            _ac._cvm.setLineWidth(rectLine_t);
            x = getDateToX(xIndex);
//            _ac._cvm.drawRect(g, x-2-selectAreaWidth/2, y-2-selectAreaWidth/2, selectAreaWidth, selectAreaWidth, CoSys.at_col);
            drawSelectedPointRect(g, x, y);
        }

        g.restore();
    }
    Calendar c;
    public String getDate(String date, int inc) {
        try {
            //날짜증가 루틴 구현////////////////////////////////
            String cdt = ""+date;

            if(cdt.length()==4 && cdt.length()!=0) { //년봉 처리
                int year = Integer.parseInt(cdt.substring(2));
                int sum = year+inc;
                return ""+sum%100;
            } else if(cdt.length()==6 && cdt.length()!=0) { //월봉 처리
                int month = Integer.parseInt(cdt.substring(4));
                int sum = month+inc;
                return ""+sum%12;
            } else { //일,주봉
                int cy = Integer.parseInt(cdt.substring(0, 4)); //year추출
                int cm = Integer.parseInt(cdt.substring(4, 6)); //month추출
                int cd = Integer.parseInt(cdt.substring(6, 8)); //date추출

                //초기날짜로 세팅
                c = Calendar.getInstance();
                c.set(Calendar.YEAR, cy);
                c.set(Calendar.MONTH, cm-1); //month:0~11
                c.set(Calendar.DAY_OF_MONTH, cd);

                for(int i=0; i<inc; i++) {

                    //토,일 체크
                    int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
                    int dayOfFirstWeek = (dayOfWeek+6-c.get(Calendar.DAY_OF_MONTH)%7)%7;
                    int x=(c.get(Calendar.DAY_OF_MONTH)+dayOfFirstWeek)%7;
                    if(x==6 || x==0) {
                        if(i!=0) i=i-1;
                        c.add(Calendar.DATE, 1);
                    } else {
                        c.add(Calendar.DATE, 1);
                    }
                    //토,일 체크 끝        
                }

                date = ""+c.get(Calendar.DATE);
            }
        } catch(Exception e) {
            return null;
        }

        return date;
    }
    public boolean isSelected(PointF p){
        if(data[0]==null)return false;

        float x = dateToX(data[0].x);
        float y = priceToY(data[0].y);

        try {
            RectF bound= new RectF(x-selectAreaWidth/2, y-selectAreaWidth/2, x-selectAreaWidth/2+selectAreaWidth, y-selectAreaWidth/2+selectAreaWidth);

            if(bound.contains(p.x, p.y)){
                select_type=1;
                return true;
            }
        } catch(Exception e) {
            return false;
        }
        return false;
    }
    public String getTitle() {
        return "피보나치시간대";
    }
}