package drfn.chart.comp;

public class DataField {
    public int align;          // 0: right, 1: center, 2: left
    public int attr;           // Color Table 참고
    public int sign;           // 1 ~ 8
    public int format;         // <0 : no formatting, 0: integer, >0: 
    long rtime;         // last updated time
    String value;

    String preText = "";
    String unit = "";

    boolean setArrow=false; // 화살표 이미지 위치 변경 
    public DataField() {
        align=0; // default is left align
        sign=-1;
        format=0;
    }

    public DataField(String v) {
        align=0; // default is left align
        sign=-1;
        format=-1;
        attr=1;
        value=v;
    }

    public DataField(String v, int at, int al) {
        align=al;
        attr=at;
        sign=-1;
        format=-1;
        value=v;
    }

    public void setUnit(String pre, String u){
        preText = pre;
        unit = u;
    }
/*
    Header 그릴 때
    public void paint(Graphics g) {
        if(gbuf==null) {
    
            drawHeader(gbuf);
        }
    }
    
    void drawHeader(Graphics g) {
        DataFeild[] h={
            new DataField("...", 0, 0);
        };
        int[][] cord={
            {//x
            },
            {//y
            },
            {//width
            }
        };
        // clear background
        for(int i=0;i<h.length;i++) {
            g.fillRect(cod[0],cord[1],cord[2],height);
        }
        for(int i=0;i<h.length)
            h[i].draw(g, cord[0], cord[1], cord[2]);
        title=null;
    }
*/

    public final static int INT=0;
    public final static int ATTR=1;
    public final static int SIGN=2;
    public final static int DBL=3;
    public final static int STR=4;

    public final static int RIGHT=0;
    public final static int CENTER=1;
    public final static int LEFT=2;

    public void setValue(String v, int type) {
        if(v==null) v="";
        else v=v.trim();
        switch(type) {
            case 3: // double
                format=2;
            case 0: // integer
                long lv;
                try {
                    lv=Long.parseLong(v);
                }catch(NumberFormatException e) {
                    lv=0;
                }
                value=String.valueOf(lv);
                break;
            case 7:
            case 1: // attribute
                if(v.length()!=0) setAttr(v.charAt(0));
                else setAttr('0');
                break;
            case 4: // string
            case 5: // reserved for date/time
                value=v;
                format=-1;
                break;
            case 2: // sign
                if(v.length()!=0) sign=(int)(v.charAt(0)-'0');
                else sign=0;
                break;
        }
    }

    public void setAttr(char a) {
        attr=((int)a-(int)'0');
    }
/*    
    public void setAttr(int lp) {// 기준가
        int v, p=getIntValue();
        if(p==0||lp==0) {
            attr = 1;
            return;
        }
        v = p - lp;
        if(v==0) attr = 4; // green
        else if(v>0) attr = 3; // red
        else attr = 2; // blue
    }
*/

//    public void setAttr(int lp) {//기준가
//        int v, p;
////        if(sign!=-1) {
////            if(sign==3||sign==0) attr=4;
////            else if((0<sign&&sign<3)||(5<sign&&sign<8)) attr=3;
////            else attr=2;
////        }else {
//            p=getIntValue();
//            if(p==0||lp==0) {
//                attr=4;
//                return;
//            }
//            v=p-lp;
//            if(v==0) attr=4; // green
//            else if(v>0) attr=3; // red
//            else attr=2; // blue
////        }
//    }
//    public void setAttr(double lp) {//기준가
//        double v, p;
//            p=getDoubleValue();
//            if(p==0||lp==0) {
//                attr=4;
//                return;
//            }
//            v=p-lp;
//            if(v==0) attr=4; // green
//            else if(v>0) attr=3; // red
//            else attr=2; // blue
////        }
//    }
//    Color gColor=new Color(15,164,0);
//    Color kColor=new Color(177,96,0);
//    public Color getColor(Color c) {
//        switch(sign) {
//            case 0:
//            break;
//            case 1:
//            case 2:
//                return CoSys.CHART_COLOR[0];//Color.red;
//            case 3:
//                return Color.black;
//            case 4:
//            case 5:
//                return CoSys.CHART_COLOR[1];//blue
//            default:
//            break;
//        }
//        switch(attr) {
//            case 0:
//                return c;
//            case 1:
//                return Color.black;
//            case 2:
//                return CoSys.CHART_COLOR[1];//Color.blue;
//            case 3:
//                return CoSys.CHART_COLOR[0];//Color.red;
//            case 4:
//                return CoSys.CHART_COLOR[2];//gColor;
//            case 5:
//                return kColor;
//        }
//        return Color.black;
//    }
//    
//    public Color getColor() {
//        switch(attr) {
//            case 0:
//                return Color.black;
//            case 1:
//                return Color.black;
//            case 2:
//                return CoSys.CHART_COLOR[1];//Color.blue;
//            case 3:
//                return CoSys.CHART_COLOR[0];//Color.red;
//            case 4:
//                return CoSys.CHART_COLOR[2];//gColor;
//            case 5:
//                return kColor;                
//        }
//        return Color.black;
//    }
//
//    public String getValue() {
//        if(value==null) return "";
//        else return value;
//    }
//    
//    String getFormattedValue() {
//        if(value==null) return "";
//        if(format<0||format>4) return value;
//        return COMUtil.formatDecimal(value, true, format);
//    }
//    
//    /*String getFormattedValue() {
//        if(value==null) return "";
//        if(format<0||format>4) return value;
//        if(value.toString().endsWith(")")) {
//            return UTIL.formatDecimal(value.substring(0,5), true, format)+value.substring(5);
//        }else{
//            return UTIL.formatDecimal(value, true, format);
//        }
//    }
//    */
//    
//    public int getIntValue() {
//        int result=0;
//        if(value==null) return 0;
//        if(format<=0) {
//            try {
//                result=Integer.parseInt(value);
//            }catch(NumberFormatException e) {
//                return 0;
//            }
//            return result;
//        }
//        for(int i=0;i<format;i++) {
//            result/=10;
//        }
//        return result;
//    }
//    
//    public double getDoubleValue() {
//        double result=0.0;
//        if(value==null) return 0.0;
//        try {
//            result=Double.valueOf(value).doubleValue();
//        }catch(NumberFormatException e) {
//            return 0.0;
//        }
//        for(int i=0;i<format;i++) {
//            result/=10.0;
//        }
//        return result;
//    }
//    
//    public void setRTime(long now) {
//        rtime=now;
//    }
//    
//    public boolean checkRealTime(long now) {
//        if(rtime==0) return false;
//        if(now-rtime<500) {
//            return true;
//        }
//        rtime=0;
//        return false;
//    }
//    
//    public void draw(Graphics g, int x, int y, int width, ImageObserver io) {
//        draw(g, g.getFontMetrics(), x, y, width, io);
//    }
//
//    public void draw(Graphics g, FontMetrics fm, int x, int y, int width, ImageObserver io) {
//        String formatted=getFormattedValue();
//        if(format==5 && formatted.length()>20){
//            int strWidth = fm.stringWidth(formatted.substring(0,20));
//            int maxWidth = width-10 - fm.stringWidth("...");
//            for(int j=20;j<formatted.length()-1;j++) {
//                strWidth += fm.stringWidth(formatted.substring(j,j+1));
//                if (strWidth > maxWidth) {
//                    formatted = formatted.substring(0, j) + "...";
//                    break;
//                }
//            }
//        }
//        if(sign!=0) {
//            if (COMUtil.a_img!=null) {
//                if(1<=sign&&sign<=5)
//                    if(setArrow) g.drawImage(COMUtil.a_img,x+width/2,y-14,x+width/2+18,y+6,0,(sign-1)*20,18,sign*20,io);
//                    else g.drawImage(COMUtil.a_img,x,y-14,x+18,y+6,0,(sign-1)*20,18,sign*20,io);
//            } else {
//                COMUtil.drawMark(g, sign, x, y,true); //                g.drawImage(COMUtil.a_img,x,y-14,x+18,y+6,0,(sign-1)*20,18,sign*20,io);            
//                //x += 14;
//            }
//        }
//        int sw=fm.stringWidth(preText+formatted+unit);
//        switch(align) {
//            case 0: //right
//               x=x+width-sw-5; 
//            break;
//            case 1: //center
//               x=x+(width-sw)/2;
//            break;
//            case 2: //left
//               x+=5; 
//            break;
//        }
//        if(preText.length()>0){
//            g.setColor(Color.black);
//            g.drawString(preText, x, y);
//            x += fm.stringWidth(preText);
//        }
//        
//        g.setColor(getColor());
//        g.drawString(formatted, x, y);
//        if(unit.length()>0){
//            x+=fm.stringWidth(formatted);
//            g.setColor(Color.black);
//            g.drawString(unit, x, y);
//        }
//    }
//
//    public void draw(Graphics g, FontMetrics fm, int x, int y, int width, Color defColor, ImageObserver io) {
//        String formatted=getFormattedValue();
//        formatted+=unit;
//        int sw=fm.stringWidth(formatted);
//        if(sign!=-1) {
//            if (COMUtil.a_img!=null) {
//                if(1<=sign&&sign<=5) g.drawImage(COMUtil.a_img,x-2,y-14,x+16,y+6,0,(sign-1)*20,18,sign*20,io);
//            }
//            g.setColor(getColor());
//        }else{
//            g.setColor(getColor(defColor));
//        }
//        switch(align) {
//            case 0: //right
//               x=x+width-sw-5; 
//            break;
//            case 1: //center
//               x=x+(width-sw)/2;
//            break;
//            case 2: //left
//               x+=5; 
//            break;
//        }
//        g.drawString(formatted, x, y);
//    }
}