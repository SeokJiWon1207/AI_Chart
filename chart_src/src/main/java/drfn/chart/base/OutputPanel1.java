
package drfn.chart.base;

import android.content.Context;
import android.view.View;

public class OutputPanel1 extends View {
    public OutputPanel1(Context context) {
    	super(context);
    }
    
    public void init() {        
    }
//    
//    DataField[] datafield;
//    int[] widths;
//    public void setData(DataField[] datafield) {
//        this.datafield = datafield;
//        repaint();
//    }
//    
//    public void setWidths(int[] widths) {
//        this.widths = widths;
//    }
//
//    public void paint(Graphics g) {
//        int width = getSize().width;
//        int height = getSize().height;        
//        g.clearRect(0, 0, width, height);
//        if (datafield == null || widths == null) return;
//        FontMetrics fm = COMUtil.getFontMetrics();
//        
//        int x = 2;
//        int y = 2;
//        if(widths[0]!=0){
//            g.setColor(Color.white);
//            g.fillRect(x, y, widths[0], height - y * 2);
//            g.setColor(Color.black);
//            g.drawRect(x, y, widths[0], height - y * 2);
//            drawString(g, x, height, widths[0], 0);
//            
//            x += widths[0]+10;
//        }
//        width -= (x + 3);
//        g.setColor(Color.white);
//        g.fillRect(x, y, width, height - y * 2);
//        g.setColor(Color.black);
//        g.drawRect(x, y, width, height - y * 2);
//        for(int i=1; i<datafield.length; i++){
//            if(widths[i]!=0){
//                drawString(g, x, height, widths[i], i);
//                x += widths[i];
//            }
//        }
//    }
//    
//    private void drawString(Graphics g, int x, int y, int width, int index) {
//        if(width!=0)datafield[index].draw(g, x, (int)(y*0.8), width, this);
//    }
//    
//    public void update(Graphics g) {
//        paint(g);
//    }
}