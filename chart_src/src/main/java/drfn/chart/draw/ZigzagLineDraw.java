package drfn.chart.draw;

import android.graphics.Canvas;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;
import drfn.chart.util.CoSys;

public class ZigzagLineDraw extends DrawTool{
    int type=0;
    public ZigzagLineDraw(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm, cdm);
    }
    public void setIndex(int idx){
    }
    public void draw(Canvas g, double data){//기준가 없이 그리는 바
    }
    public void drawDefault(Canvas gl, double[] data){
    }
    public void draw(Canvas g, double[] data){//기준가 없이 그리는 바
        if(data==null||data.length<1)return;
        this.data = data;
        int startPos=0;
        if(line_thick>1){
            float x=getBounds().left+xw;
//            g.setColor(upColor);
            int xx = 0;
            for(int i=0;i<70;i++){
                if(data.length>i && data[i]!=0){
                    startPos=i;
                    xx=(int)(xx+(i*xfactor));
                    x = xx;
                    break;
                }
            }
            float y = calcy(data[startPos]);
            int dLen = data.length;
            for(int i=startPos; i<dLen; i++){
                int x1=(int)(xx+((i+1)*xfactor));
                float y1 = calcy(data[i]);
                if((y<=max_view&&y1<=max_view)&&data[i]!=0){
//                    g.setColor(upColor);
                    int comp = 0;
                    for(int j=0; j<(x1-x); j++){
                        if(y>y1) {
                            double dTemp = ((double)y1-(double)y)/(x1-x);
                            int gab = (int)(dTemp*(double)j);
                            if(((comp-(y+gab)) >= line_thick) && (j!=0&&comp!=0)) {
                                _cvm.drawFillRect(g, (int)x+j, y+gab, line_thick, comp-(y+gab), upColor, 1.0f);
//                                g.fillRect((int)x+j, y+gab, line_thick, comp-(y+gab));
                            } else {
                                _cvm.drawFillRect(g, (int)x+j, y+gab, line_thick, line_thick, upColor, 1.0f);
//                                g.fillRect((int)x+j, y+gab, line_thick, line_thick);
                            }
                            comp = (int)y+gab;
                        } else {
                            double dTemp = ((double)y-(double)y1)/(x1-x);
                            int gab = (int)(dTemp*(double)j);
                            if((((y-gab)-comp) >= line_thick) && (j!=0&&comp!=0)) {
                                _cvm.drawFillRect(g, (int)x+j, y-gab, line_thick, (y-gab)-comp, upColor, 1.0f);
//                                g.fillRect((int)x+j, y-gab, line_thick, (y-gab)-comp);
                            } else {
                                _cvm.drawFillRect(g, (int)x+j, y-gab, line_thick, line_thick, upColor, 1.0f);
//                                g.fillRect((int)x+j, y-gab, line_thick, line_thick);
                            }
                            comp = (int)y-gab;
                        }

                    }
                    if(isSelected()&&(i%5==1)){
//                    	_cvm.drawCircle(g, (int)x-3,(int)y-3, 6, true, upColor);
////                        g.drawOval((int)x-3,y-3,(int)6,6);
////                        g.setColor(Color.white);
//                    	_cvm.drawCircle(g, (int)x-2,(int)y-2, 5, true, CoSys.WHITE);
////                        g.fillOval((int)x-2,y-2,(int)5,5);
                        _cvm.drawCircle(g, (int)x-3,(int)y-3, x+3, x+3, true, upColor);
                        _cvm.drawCircle(g, (int)x-2,(int)y-2, x+3, x+3, true, CoSys.WHITE);
                    }
                    x=x1;
                    y=y1;
                }
            }


        }else{
            float x=getBounds().left+xw;
//            g.setColor(upColor);
            int xx = 0;
            for(int i=0;i<70;i++){
                if(data.length>i && data[i]!=0){
                    startPos=i;
                    xx=(int)(xx+(i*xfactor));
                    x = xx;
                    break;
                }
            }
            float y = calcy(data[startPos]);
            for(int i=startPos; i<data.length; i++){
                int x1=(int)(xx+((i+1)*xfactor));
                float y1 = calcy(data[i]);
                if((y<=max_view&&y1<=max_view)&&data[i]!=0){
                    _cvm.drawLine(g, (int)x,y,x1,y1, upColor, 1.0f);
//                    g.drawLine((int)x,y,x1,y1);
                    if(isSelected()&&(i%5==1)){
//                    	_cvm.drawCircle(g, (int)x-3,(int)y-3, 6, true, upColor);
////                        g.drawOval((int)x-3,y-3,(int)6,6);
//                    	_cvm.drawCircle(g, (int)x-2,(int)y-2, 5, true, CoSys.WHITE);
////                        g.setColor(Color.white);
////                        g.fillOval((int)x-2,y-2,(int)5,5);
                        _cvm.drawCircle(g, (int)x-3,(int)y-3, x+3, x+3, true, upColor);
                        _cvm.drawCircle(g, (int)x-2,(int)y-2, x+3, x+3, true, CoSys.WHITE);
                    }
                    x=x1;
                    y=y1;
                }
            }
        }
    }
    public void draw(Canvas gl, double[] data, double[] stand) {

    }
    public void draw(Canvas gl, double[][] data) {

    }
    public void draw(Canvas gl, double[][] data, double[] stand) {

    }
    public void drawVolumeForSale(Canvas gl, double[] stand){//대기매물용	
    }


//    public void draw(Canvas g, double[] data){//기준가 없이 그리는 바
//    }
// 
//    public void draw(Canvas g, int[] data, int[] data1){
//    }
//    
//    public void draw(Canvas g, int[][] data){
//    }
//    public void draw(Canvas g, int[][] data, int[] stand){
//    }
//    private void drawLine(Canvas g,int sx, int sy, int ex, int ey){
//        switch(getDrawType2()){
//            case 0://일반
//                g.setColor(upColor);
//                g.drawLine(sx,sy,ex,ey);
//            break;
//            case 1://채움
//                drawFillLine(g,sx,sy,ex,ey,calcy(stand[0]));
//            break;
//            case 2://구름
//                drawFillLine_Lough(g,sx,sy,ex,ey,calcy(stand[0]));
//            break;
//            case 3://대비
//                drawStandLine(g,sx,sy,ex,ey,calcy(stand[0]));
//            break;
//            case 4://레인보우식
//            break;
//            case 5://도트식
//                drawDot(g,sx-(int)xw,sy);
//            break;
//            default:
//                g.setColor(upColor);
//                g.drawLine(sx,sy,ex,ey);
//            break;
//        }
//        
//    }
//    private void drawFillLine(Canvas g,int sx, int sy, int ex, int ey, int sp){//채움식
//        int[] x = {sx,sx,ex,ex};
//        int[] y = {sy,sp,sp,ey};      
//        if(ey<sp){
//            if(sy<sp){
//                g.setColor(upColor);
//                g.fillPolygon(x,y,4);
//                g.setColor(upColor);
//                g.drawLine(sx,sy,ex,ey);
//            }else{
//                g.setColor(downColor);
//                x[2]=sx+getCrossX(sp,sx,ex,sy,ey);
//                g.fillPolygon(x,y,3);
//                g.setColor(downColor);
//                g.drawLine(sx,sy,sx+getCrossX(sp,sx,ex,sy,ey),sp);
//                g.setColor(upColor);
//                x[0]=sx+getCrossX(sp,sx,ex,sy,ey);
//                x[1]=ex;
//                x[2]=ex;
//                y[0]=sp;
//                y[1]=ey;
//                y[2]=sp;
//                g.fillPolygon(x,y,3);
//                g.setColor(downColor);
//                g.drawLine(sx+getCrossX(sp,sx,ex,sy,ey),sp,ex,ey);
//            }
//        }else{
//            if(sy>sp){
//                g.setColor(downColor);
//                g.fillPolygon(x,y,4);
//                g.setColor(downColor);
//                g.drawLine(sx,sy,ex,ey);
//            }else{
//                g.setColor(upColor);
//                x[2]=sx+getCrossX(sp,sx,ex,sy,ey);
//                g.fillPolygon(x,y,3);
//                g.setColor(upColor);
//                g.drawLine(sx,sy,sx+getCrossX(sp,sx,ex,sy,ey),sp);
//                g.setColor(downColor);
//                x[0]=sx+getCrossX(sp,sx,ex,sy,ey);
//                x[1]=ex;
//                x[2]=ex;
//                y[0]=sp;
//                y[1]=ey;
//                y[2]=sp;
//                g.fillPolygon(x,y,3);
//                g.setColor(downColor);
//                g.drawLine(sx+getCrossX(sp,sx,ex,sy,ey),sp,ex,ey);
//            }
//        }
//    }
//    private void drawFillLine_Lough(Canvas g,int sx, int sy, int ex, int ey, int sp){//구름식
//        int[] x = {sx,sx,ex,ex};
//        int[] y = {sy,sp,sp,ey};      
//        if(ey<sp){
//            if(sy<sp){
//                g.setColor(upColor);
//                g.drawPolygon(x,y,4);
//                g.setColor(upColor);
//                g.drawLine(sx,sy,ex,ey);
//            }else{
//                g.setColor(downColor);
//                x[2]=sx+getCrossX(sp,sx,ex,sy,ey);
//                g.drawPolygon(x,y,3);
//                g.setColor(downColor);
//                g.drawLine(sx,sy,sx+getCrossX(sp,sx,ex,sy,ey),sp);
//                g.setColor(upColor);
//                x[0]=sx+getCrossX(sp,sx,ex,sy,ey);
//                x[1]=ex;
//                x[2]=ex;
//                y[0]=sp;
//                y[1]=ey;
//                y[2]=sp;
//                g.drawPolygon(x,y,3);
//                g.setColor(downColor);
//                g.drawLine(sx+getCrossX(sp,sx,ex,sy,ey),sp,ex,ey);
//            }
//        }else{
//            if(sy>sp){
//                g.setColor(downColor);
//                g.drawPolygon(x,y,4);
//                g.setColor(downColor);
//                g.drawLine(sx,sy,ex,ey);
//            }else{
//                g.setColor(upColor);
//                x[2]=sx+getCrossX(sp,sx,ex,sy,ey);
//                g.drawPolygon(x,y,3);
//                g.setColor(upColor);
//                g.drawLine(sx,sy,sx+getCrossX(sp,sx,ex,sy,ey),sp);
//                g.setColor(downColor);
//                x[0]=sx+getCrossX(sp,sx,ex,sy,ey);
//                x[1]=ex;
//                x[2]=ex;
//                y[0]=sp;
//                y[1]=ey;
//                y[2]=sp;
//                g.drawPolygon(x,y,3);
//                g.setColor(downColor);
//                g.drawLine(sx+getCrossX(sp,sx,ex,sy,ey),sp,ex,ey);
//            }
//        }
//    }
//    private void drawStandLine(Canvas g,int sx, int sy, int ex, int ey, int sp){
//        if(ey<sp){
//            if(sy<sp){
//                g.setColor(upColor);
//                g.drawLine(sx,sy,ex,ey);
//            }else{
//                g.setColor(Color.black);
//                //g.setColor(Color.blue);
//                g.drawLine(sx,sy,sx+getCrossX(sp,sx,ex,sy,ey),sp);
//                g.setColor(upColor);
//                g.drawLine(sx+getCrossX(sp,sx,ex,sy,ey),sp,ex,ey);
//            }
//        }else{
//            if(sy>sp){
//                g.setColor(downColor);
//                //g.setColor(Color.blue);
//                g.drawLine(sx,sy,ex,ey);
//            }else{
//                g.setColor(upColor);
//                g.drawLine(sx,sy,sx+getCrossX(sp,sx,ex,sy,ey),sp);
//                g.setColor(Color.black);
//                g.drawLine(sx+getCrossX(sp,sx,ex,sy,ey),sp,ex,ey);
//            }
//        }
//    }
//    private void drawDot(Canvas g, int x, int y){
//        g.setColor(upColor);
//        g.fillOval(x+(int)xw-3,(int)(y+3),5,5);
//    }
}