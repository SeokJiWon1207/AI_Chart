package drfn.chart.anal;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.widget.RelativeLayout;

import drfn.chart.block.Block;
import drfn.chart.util.DoublePoint;
public class ATextTool extends AnalTool{

    float m_labelSizeWidth;
    float m_labelSizeHeight;
    //2012. 7. 9  Text_View 를 이용해 문자 표시하게 수정
    //private Text_View lbTitle=null;

//	RelativeLayout.LayoutParams TextToolparams;

    //LinearLayout labelLayout = null;

    int topMargin = 0;
    int leftMargin = 0;
    RelativeLayout parentView;

    public ATextTool(Block ac){
        super(ac);
        ncount = 1;
        data = new DoublePoint[ncount];
        data_org = new DoublePoint[ncount];
    }
    public void draw(Canvas g){
        if(_ac==null) return;
        in = _ac.getGraphBounds();
        out=_ac._cvm.getBounds();

        if(data[0]==null) return;

        int xIndex=getIndexWithDate(data[0].x);
        float x = getDateToX(xIndex);
        float y = priceToY(data[0].y);
        //lbTitle.setText(viewTitle);
        float sx = x + leftMargin;
        float sy = y + topMargin;

        _ac._cvm.drawString(g, at_col, sx+5, sy+15, strText);
//    	//2012. 7. 9  Text_View 를 이용해 문자 표시하게 수정
//    	if(lbTitle != null)
//    	{
//    		COMUtil._chartMain.runOnUiThread(new Runnable() {
//            public void run() {
//                int xIndex=getIndexWithDate(data[0].x);
//                int x = getDateToX(xIndex);
//                int y = priceToY(data[0].y);
//            	//lbTitle.setText(viewTitle);
//            	int sx = x + leftMargin;
//            	int sy = y + topMargin;
//            	//2012. 7. 10   lbTitle의  TextView 크기 알아내서 계산
//            	if(	sx < in.left || sx > in.right-lbTitle.getTextWidth())
//            		sx = -10000;
//            	else if(	sy < in.top + (topMargin-lbTitle.getHeight()) ||
//            			sy > in.bottom + (topMargin-lbTitle.getHeight()))
//            		sy=-10000;
//
//            	lbTitle.setMargins(sx, sy);
//            }
//            });
//    	}

        // layoutParam
//        if(labelLayout != null)
//        {
//        	COMUtil._chartMain.runOnUiThread(new Runnable() {
//                public void run() {
//                	//문자 텍스트뷰 위치를 새로 정해줄 LayoutParams
//                	RelativeLayout.LayoutParams analParams1 = new RelativeLayout.LayoutParams((int)COMUtil.getPixel(80), (int)COMUtil.getPixel(30));
//                    int xIndex=getIndexWithDate(data[0].x);
//                    int x = getDateToX(xIndex);
//                    int y = priceToY(data[0].y);
//
//                    analParams1.leftMargin = x + leftMargin;
//                    analParams1.topMargin = y + topMargin;
//
//                     // 문자텍스트뷰의 좌표(좌상단) 가  차트영역 좌측라인이거나 우측라인(우측좌표 - 문자텍스트뷰 너비) 일경우 문자 textview 숨기기(좌표만 화면밖으로 변경)
//                    if(	analParams1.leftMargin < in.left ||
//                    	analParams1.leftMargin > in.right-lbTitle.getWidth())
//                    {
//                    	analParams1.leftMargin = -10000;
//                    }
//
////                  문자텍스트뷰의 좌표(좌상단) 가  차트영역 위쪽라인이거나, 차트영역 아랫쪽라인일 경우 문자 textview 숨기기
////                  viewgroup의 topmargin(addLabel에서 받아온 param)에서 텍스트뷰의 높이만큼을 제한영역을 in.top에  더해줘야 정확하게 차트 내부영역으로 잡힌다.
//                    else if(	analParams1.topMargin < in.top + (topMargin-lbTitle.getHeight()) ||
//                    			analParams1.topMargin > in.bottom + (topMargin-lbTitle.getHeight()))
//                    {
//                    	analParams1.topMargin = -10000;
//                    }
//
//                    //layoutparam 세팅
//                    labelLayout.setLayoutParams( analParams1);
//                }
//
//    		});
//        }
    }

    public void addLabel(String strData, RelativeLayout parentView, RelativeLayout.LayoutParams params, int topMargin, int leftMargin)
    {
        //if(lbTitle == null)
        //   	{
        strText = strData;
//    		//2012. 7. 9  Text_View 를 이용해 문자 표시하게 수정
//     		lbTitle = new Text_View(parentView.getContext());
//    		lbTitle.setColor(Color.RED);
//    		lbTitle.setBackgroundColor(Color.TRANSPARENT);
//    		lbTitle.setText(strData);
//    		lbTitle.setTextSize(11.0f);
//    		lbTitle.setMargins(params.leftMargin, params.topMargin);
//            parentView.addView(lbTitle);
//    		// 차트영역  클릭한곳의 param 정보
//    		//TextToolparams = params;
//    		//NeoChart2 에서의  'layout'
//    		//labelLayout = new LinearLayout(parentView.getContext());
//    		//Context c = parentView.getContext();
//    	    //lbTitle = new TextView(c);
//    		//lbTitle.setText(strData);
//			//lbTitle.setTextColor(Color.RED);
//			//lbTitle.setGravity(Gravity.LEFT);
//			//배경투명하게
//			//lbTitle.setBackgroundColor(Color.TRANSPARENT);
//			//lbTitle.setTextSize(11.0f);
//			this.parentView = parentView;
//			//viewgroup (NeoChart2로부터) 의 top/left margin 저장
//        	this.topMargin = topMargin;
//            this.leftMargin = leftMargin;
//			//lbTitle.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
//
//			//labelLayout.addView(lbTitle);
//			//labelLayout.setLayoutParams(params);
//			//parentView.addView(labelLayout);
//        }
    }

    public void removeLabel()
    {
//    	//2012. 7. 9  Text_View 를 이용해 문자 표시하게 수정
//    	if(lbTitle != null)
//    	{
//    		parentView.removeView(lbTitle);
//    	}
    }

    public boolean isSelected(PointF p){
        if(data[0]==null)return false;
        float x = dateToX(data[0].x);
        float y = priceToY(data[0].y);

//        if(lbTitle == null) return false;
//
//        //문자 텍스트뷰의 가로길이 (width)
//        int nWidth = lbTitle.getWidth();
//
//        Rect bound= new Rect(x-2, y-2-selectAreaWidth/2,x-2 + nWidth, y-2-selectAreaWidth/2+selectAreaWidth);
//         if(bound.contains(p.x, p.y)){
//            select_type=0;
//            return true;
//        }
        return false;
    }

    public String getTitle() {
        return "문자";
    }
}