package drfn.chart.draw;
import android.graphics.Canvas;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;
import drfn.chart.scale.AREA;
import drfn.chart.util.COMUtil;
import drfn.chart.util.ChartUtil;
import drfn.chart.util.CoSys;

import static drfn.chart.model.ChartViewModel.ASSET_LINE_MOUNTAIN;
import static drfn.chart.model.ChartViewModel.FX_BUYSELL;

/**

 */
public class LineDraw extends DrawTool{
    int type=0;
    boolean isdot;

    //2015. 1. 13 보조지표 bar 타입 유형 변경 기능>>
    public final static int OSC_UP = 1;
    public final static int OSC_DOWN = 2;
    //2015. 1. 13 보조지표 bar 타입 유형 변경 기능<<

    //2017.09.25 by LYH >> 자산 차트 적용
    //2015. 9. 30 자산관리 라인오실레이터차트 동그라미 터치시 수치조회창 표시
    ArrayList<Hashtable<String, Integer>> m_arCirclePosition = new ArrayList<Hashtable<String, Integer>>();

    public LineDraw(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm, cdm);
        setIndex(getDrawType2());
        line_thick = 1;
    }
    public void setDotLine(boolean b){
        isdot = b;
    }
    public void setIndex(int idx){
        type = idx;
    }
    public void drawVolumeForSale(Canvas gl, double[] stand){//대기매물용	
    }
    public void draw(Canvas gl, double y){
        float yp = calcy(y);
        if(yp>=min_view-1 &&yp<=max_view+1){
            //_cvm.drawLine(gl, getBounds().left,yp,getBounds().right,yp, CoSys.CHART_COLORS[0] ,1.0f);
            _cvm.drawLine(gl, getBounds().left,yp,getBounds().right,yp, CoSys.vertLineColor_xscale ,0.5f);
            _cvm.drawString(gl, CoSys.vertLineColor_xscale, getBounds().left, (int)yp, ""+y);
//            float xw = COMUtil.tf.GetTextLength(""+y)+3;
//            COMUtil.drawString(gl, _cvm.CST, (int)(getBounds().right-xw),(int)yp-3, ""+y);
        }
    }
    public void drawDefault(Canvas gl, double[] data){
        if(data==null||data.length<1)return;
        this.data = data;
        int startPos=0;
        float xpos=0;
        int dataLen = data.length;

//        if(line_thick>1){
//            xpos=getBounds().left+xw;
//            float ypos=0;
//            float ypos1=0;
//            
//            int x,y=0;
//            double yInc;
//            int thick =line_thick, temp;
//            float cnt =(xfactor*2);
//            //지표가 후행된경우 초기의 데이터가 비워져서 생긴 0데이터 처리를 위해 
//            //처음에 데이터가 0인곳은 무시하고 실제 데이터를 잡아내는 부분을 추가
//            
//            for(int i=0;i<70;i++){
//                if(dataLen>i && data[i]!=0){
//                    startPos=i;
//                    xpos=(int)(xpos+(i*xfactor));
//                    break;
//                }
//            }
//            for(int i=startPos; i<dataLen-1; i++){
//                ypos = calcy(data[i]);
//                ypos1 = calcy(data[i+1]);
//                if((ypos<=max_view+1&&ypos1<=max_view+1)){
//                    yInc = (double)(((double)ypos1-(double)ypos)/(double)(xfactor*2));
//                    temp = (yInc<0)?Math.abs((int)(-1*(yInc-0.99))):(int)(yInc+0.99);
//                    thick=(temp>thick)?temp:line_thick;
//                    if(cnt<2){
//                        if((Math.abs(ypos1-ypos)+0.99)>line_thick){
//                            if(yInc<0) {
//                            	_cvm.drawFillRect(gl, (int)xpos, (int)(ypos1-line_thick/2), line_thick, (int)(ypos-ypos1+0.99), upColor, 1.0f);
//                            }
//                            else{
//                            	_cvm.drawFillRect(gl, (int)xpos, (int)(ypos-line_thick/2), line_thick, (int)(ypos1-ypos+0.99), upColor, 1.0f);
//                            }
//                        }else{
//                            if(yInc<0) {
//                            	_cvm.drawFillRect(gl, (int)xpos, (int)(ypos1-line_thick/2), line_thick, line_thick, upColor, 1.0f);
//                            }
//                            else {
//                            	_cvm.drawFillRect(gl, (int)xpos, (int)(ypos-line_thick/2), line_thick, line_thick, upColor, 1.0f);
//                            }
//                        }
//                    }else{
//                        for(int j=0; j<cnt; j++){
//                            if(yInc<0 && j==0) continue; 
//                            x = (int)(xpos+(j/2));            
//                            if(j>=cnt-1){
//                                thick =line_thick;
//                                if((yInc>0 && ypos1<y)|| (yInc<0 && ypos1>y))
//                                    break;
//                                if(yInc<0) {
//                                	_cvm.drawFillRect(gl, (int)x, (int)(ypos1-line_thick/2), line_thick+(int)x, (int)(y-ypos1+0.99)+(int)(ypos1-line_thick/2), upColor, 1.0f);
//                                }
//                                else {
//                                	_cvm.drawFillRect(gl,(int)x, (int)(y-line_thick/2), line_thick+(int)x, (int)(ypos1-y+0.99)+(int)(y-line_thick/2), upColor, 1.0f);
//                                }
//                            }else{
//                                y = (int)(ypos+(yInc*j));
//                                _cvm.drawFillRect(gl,(int)x, (int)(y-line_thick/2), line_thick+(int)x, thick+(int)(y-line_thick/2), upColor, 1.0f);
//                                
//                            }
//                        }
//                    }
//                    if(isSelected()&(i%5==0)){
//                        COMUtil.drawCircle(gl, (int)xpos-(line_thick/2+2),(int)ypos-(line_thick/2+2),line_thick+3, false, CoSys.WHITE);
//                    }
//               }
//               xpos+=xfactor;
//                
//            }
//        }else{
        xpos=getBounds().left+xw;
//            pnt.setColor(upColor);
        //지표가 후행된경우 초기의 데이터가 비워져서 생긴 0데이터 처리를 위해
        //처음에 데이터가 0인곳은 무시하고 실제 데이터를 잡아내는 부분을 추가
        if(!this.getShowZeroValue()) {
            for(int i=0;i<dataLen;i++){
                if(dataLen>i && data[i]!=0){
                    startPos=i;
                    xpos=(int)(xpos+(i*xfactor));
                    break;
                }
            }
        }

        float y = calcy(data[startPos]);

        int type = getDrawType2(); //type 6 : zigzag
        if(type == 6) {
            int xx = 0;
            //_cvm.setLineWidth(line_thick);
            //2020.07.06 by LYH >> 캔들볼륨 >>
            AREA area = _cvm.getArea(0);
            if(area!=null)
                xpos = area.getCenter();
            //2020.07.06 by LYH >> 캔들볼륨 <<
            for(int i=startPos;i<dataLen;i++){
                float x1 = (int)(xx+((i+1)*xfactor));
                //2020.07.06 by LYH >> 캔들볼륨 >>
                area = _cvm.getArea(i-startPos);
                if(area!=null)
                    x1 = area.getCenter();
                //2020.07.06 by LYH >> 캔들볼륨 <<
                float y1 = calcy(data[i]);
                if((y<=max_view&&y1<=max_view)&&data[i]!=0){
                    drawLine(gl, (int)xpos,(int)y,x1,(int)y1);
//	                	drawLine(gl, 100,100,200,200);
                    if(isSelected()&(i%5==1)){
	                        /*g.setXORMode(Color.white);
	                        g.setColor(Color.black);
	                        g.fill3DRect((int)x-2,y-2,(int)5,5,true);
	                        g.setPaintMode();*/
                        // g.drawOval((int)x-3,y-3,(int)6,6);
                        //g.fillOval((int)x-2,y-2,(int)5,5);

                    }
                    y=y1;
                    xpos=x1;
                }
            }
            //_cvm.setLineWidth(1);
        } else {
            //_cvm.setLineWidth(line_thick);
            for(int i=startPos+1;i<dataLen;i++){
                float y1 = calcy(data[i]);
                //if((y<=max_view&&y1<=max_view)){
                if((y>=min_view-1&&y1>=min_view-1) && (y<=max_view&&y1<=max_view)){
                    drawLine(gl, (int)xpos,(int)y,(int)(xpos+xfactor),(int)y1);
                    if(isSelected()&(i%5==1)){
	                        /*g.setXORMode(Color.white);
	                        g.setColor(Color.black);
	                        g.fill3DRect((int)x-2,y-2,(int)5,5,true);
	                        g.setPaintMode();*/
                        // g.drawOval((int)x-3,y-3,(int)6,6);
                        //g.fillOval((int)x-2,y-2,(int)5,5);

                    }
                    y=y1;
                    xpos+=xfactor;
                }
            }
            //_cvm.setLineWidth(1);
        }
        //      }

        //지표 현재값 표시.
        if(_cvm.useJipyoSign==true) {
            double curVal = data[dataLen-1];
            String curStr = getFormatData(dataLen - 1);
            //int curStrLen = _cvm.tf.GetTextLength(curStr)+20;
            float yp = calcy(curVal);
            //xpos = this.getBounds().right+6;
            xpos = _cvm.getBounds().left + _cvm.getBounds().width() - _cvm.Margin_R + 6;
            int pw = _cvm.Margin_R;

//            _cvm.drawFillTri(gl, xpos-COMUtil.getPixel(4),yp,COMUtil.getPixel(4),(int)COMUtil.getPixel(14), upColor);
//            _cvm.drawFillRect(gl, xpos, yp-(int)COMUtil.getPixel(7), pw, (int)COMUtil.getPixel(14), upColor, 1.0f);
            _cvm.drawCurrentPriceBox(gl, xpos, yp-(int)COMUtil.getPixel_H(18)/2, pw-COMUtil.getPixel(4), (int)COMUtil.getPixel_H(18), upColor);
            //_cvm.drawString(gl, CoSys.BLACK, (int)xpos, (int)yp, ChartUtil.getFormatedData(curVal, _cdm.getPriceFormat()));
            //_cvm.drawString(gl, CoSys.WHITE, (int)xpos, (int)yp, String.format("%.2f", curVal));
            //2013.03.27 by LYH >> 스케일 가격 오른쪽 정렬
            //_cvm.drawString(gl, CoSys.WHITE, (int)xpos, (int)yp, curStr);
            int w = _cvm.GetTextLength(curStr);
            //2021.07.12 by hanjun.Kim - kakaopay - 가격표 좌측정렬 >>
            int[] textColor = null;
            if(COMUtil.currentTheme == COMUtil.SKIN_BLACK) {
                textColor = CoSys.GREY990;
            } else {
                textColor = CoSys.GREY0_WHITE;
            }

            _cvm.drawScaleString(gl, textColor, (int)xpos+(int)COMUtil.getPixel(3), yp, curStr);
            //2013.03.27 by LYH <<
            _cvm.useJipyoSign=false;
        }

    }
    public void draw(Canvas gl, double[] data){
        if(data==null||data.length<1)return;
        //2015.01.08 by LYH >> 3일차트 추가
        if(_cdm.m_bRealUpdate && _cdm.codeItem.strStandardPrice != null && _cdm.codeItem.strStandardPrice.length()>0)
        {
            String[] arrStandard = _cdm.codeItem.strStandardPrice.split("\\|");
            if( arrStandard.length > 2 )
            {
                if(getTitle().equals("2일전주가"))
                    drawStandardLine(gl, data, arrStandard[2]);
                else if(getTitle().equals("전일주가"))
                    drawStandardLine(gl, data, arrStandard[1]);
                else
                    drawStandardLine(gl, data, arrStandard[0]);
                return;
            }

        }

        //2015.01.08 by LYH << 3일차트 추가
        this.data = data;
        int startIndex = _cvm.getIndex();
        int startPos=startIndex;
        if(startPos<0)
            startPos = 0;
        float xpos=0;
        //20120621 by LYH >> 일목균형 스크롤 처리
        //int dataLen = startIndex + _cvm.getViewNum();
        int dataLen = startIndex + _cvm.getViewNum() + _cvm.futureMargin;
        //20120621 by LYH <<
        if(dataLen>data.length)
            dataLen= data.length;

        if(getTitle().startsWith("렌코"))
        {
            dataLen = data.length;
            xfactor = ((float)(getBounds().width())/(float)(dataLen));
        }
        xpos=getBounds().left+xw;
//        if(_cvm.getAssetType() == ChartViewModel.ASSET_LINE_MOUNTAIN)
//            xpos = 0;
//        if(line_thick>1){
//            float ypos=0;
//            float ypos1=0;
//            
//            int x,y=0;
//            double yInc;
//            int thick =line_thick, temp;
//            float cnt =(xfactor*2);
//            //지표가 후행된경우 초기의 데이터가 비워져서 생긴 0데이터 처리를 위해 
//            //처음에 데이터가 0인곳은 무시하고 실제 데이터를 잡아내는 부분을 추가
//            
//            for(int i=0;i<70;i++){
//                if(dataLen>i && data[i]!=0){
//                    startPos=i;
//                    xpos=(int)(xpos+(i*xfactor));
//                    break;
//                }
//            }
//            for(int i=startPos; i<dataLen-1; i++){
//                ypos = calcy(data[i]);
//                ypos1 = calcy(data[i+1]);
//                if((ypos<=max_view+1&&ypos1<=max_view+1)){
//                    yInc = (double)(((double)ypos1-(double)ypos)/(double)(xfactor*2));
//                    temp = (yInc<0)?Math.abs((int)(-1*(yInc-0.99))):(int)(yInc+0.99);
//                    thick=(temp>thick)?temp:line_thick;
//                    if(cnt<2){
//                        if((Math.abs(ypos1-ypos)+0.99)>line_thick){
//                            if(yInc<0) {
//                            	_cvm.drawFillRect(gl, (int)xpos, (int)(ypos1-line_thick/2), line_thick, (int)(ypos-ypos1+0.99), upColor, 1.0f);
//                            }
//                            else{
//                            	_cvm.drawFillRect(gl, (int)xpos, (int)(ypos-line_thick/2), line_thick, (int)(ypos1-ypos+0.99), upColor, 1.0f);
//                            }
//                        }else{
//                            if(yInc<0) {
//                            	_cvm.drawFillRect(gl, (int)xpos, (int)(ypos1-line_thick/2), line_thick, line_thick, upColor, 1.0f);
//                            }
//                            else {
//                            	_cvm.drawFillRect(gl, (int)xpos, (int)(ypos-line_thick/2), line_thick, line_thick, upColor, 1.0f);
//                            }
//                        }
//                    }else{
//                        for(int j=0; j<cnt; j++){
//                            if(yInc<0 && j==0) continue; 
//                            x = (int)(xpos+(j/2));            
//                            if(j>=cnt-1){
//                                thick =line_thick;
//                                if((yInc>0 && ypos1<y)|| (yInc<0 && ypos1>y))
//                                    break;
//                                if(yInc<0) {
//                                	_cvm.drawFillRect(gl, (int)x, (int)(ypos1-line_thick/2), line_thick+(int)x, (int)(y-ypos1+0.99)+(int)(ypos1-line_thick/2), upColor, 1.0f);
//                                }
//                                else {
//                                	_cvm.drawFillRect(gl,(int)x, (int)(y-line_thick/2), line_thick+(int)x, (int)(ypos1-y+0.99)+(int)(y-line_thick/2), upColor, 1.0f);
//                                }
//                            }else{
//                                y = (int)(ypos+(yInc*j));
//                                _cvm.drawFillRect(gl,(int)x, (int)(y-line_thick/2), line_thick+(int)x, thick+(int)(y-line_thick/2), upColor, 1.0f);
//                                
//                            }
//                        }
//                    }
//                    if(isSelected()&(i%5==0)){
//                        COMUtil.drawCircle(gl, (int)xpos-(line_thick/2+2),(int)ypos-(line_thick/2+2),line_thick+3, false, CoSys.WHITE);
//                    }
//               }
//               xpos+=xfactor;
//                
//            }
//        }else{
        //지표가 후행된경우 초기의 데이터가 비워져서 생긴 0데이터 처리를 위해 
        //처음에 데이터가 0인곳은 무시하고 실제 데이터를 잡아내는 부분을 추가
        //if(!this.getShowZeroValue()) {
        if(!_cvm.bInvestorChart &&  _cvm.chartType != COMUtil.COMPARE_CHART) {
            int k = 0;  //2016.11.26 by LYH >> 이평 값 모두 0일 경우 그리지 않도록 수정.
            for (k = startPos; k < dataLen; k++) {
                if (dataLen > k && data[k] != 0) {
                    startPos = k;
                    xpos = (int) (xpos + ((k - startIndex) * xfactor));
                    break;
                }
            }
            //2016.11.26 by LYH >> 이평 값 모두 0일 경우 그리지 않도록 수정.
            if(k==dataLen)
                return;
            //2016.11.26 by LYH >> 이평 값 모두 0일 경우 그리지 않도록 수정. end
        }
        //}
        if(startPos>=data.length)
            return;

        float y = calcy(data[startPos]);

        boolean usePreData = false;

        int type = getDrawType2(); //type 6 : zigzag
        if(type == 6) {
            int xx = 0;
            for(int i=startPos;i<dataLen;i++){
                int x1 = (int)(xx+((i+1)*xfactor));
                float y1 = calcy(data[i]);
                if((y<=max_view&&y1<=max_view)){
                    drawLine(gl, (int)xpos,(int)y,x1,(int)y1);
                    if(isSelected()&(i%5==1)){
                        /*g.setXORMode(Color.white);
                        g.setColor(Color.black);
                        g.fill3DRect((int)x-2,y-2,(int)5,5,true);
                        g.setPaintMode();*/
                        // g.drawOval((int)x-3,y-3,(int)6,6);
                        //g.fillOval((int)x-2,y-2,(int)5,5);

                    }
                    y=y1;
                    xpos=x1;
                }
            }
        } else if(type == 5) { //Parabolic Sar (2011.11.01 by lyk >> Parabolic 추가.
            //_cvm.setLineWidth(line_thick);
            AREA area;  //2020.07.06 by LYH >> 캔들볼륨 >>
            for(int i=startPos;i<dataLen;i++){
                float y1 = calcy(data[i]);
                //if((y<=max_view&&y1<=max_view)){
                if((y>=min_view-1&&y1>=min_view-1) && (y<=max_view&&y1<=max_view)){
                    //2020.07.06 by LYH >> 캔들볼륨 >>
                    area = _cvm.getArea(i-startIndex);
                    if(area!=null)
                    {
                        xpos = area.getCenter();
                    }
                    //2020.07.06 by LYH >> 캔들볼륨 <<
                    drawLine(gl, (int)xpos,(int)y1,(int)(xpos+xfactor),(int)y1);

                }
                y=y1;
                xpos += xfactor;
            }
            //_cvm.setLineWidth(1);
        }
        //2015. 1. 13 보조지표 bar 타입 유형 변경 기능>>
        else if(type == 7)
        {
            this.drawForBar(gl, data);
        }
        //2015. 1. 13 보조지표 bar 타입 유형 변경 기능<<
        else if(type == 8)
        {
            int totLen = (dataLen-startPos-1)*4;
            float[] positions = new float[totLen];
            int nIndex = 0;
            for(int i=startPos+1;i<dataLen;i++){
                //신용잔고율 마지막 데이터가 0일 경우 그리지 않도록 수정 2013.05.15 by lyk
                if(_cvm.isCredigJipyo==true && (i==dataLen-1 && data[i]==0)) {
                    usePreData = true; // 전일 데이터 사용
                    break;
                }

                if(data[i]==0 && bMarketData)
                {
                    continue;
                }
                float y1 = calcy(data[i]);
                if((y>=min_view-1&&y1>=min_view-1) && (y<max_view+1&&y1<max_view+1)){
                    //drawLine(gl, (int)xpos,(int)y,(int)(xpos+xfactor),(int)y1);
                    positions[nIndex++] = xpos;
                    positions[nIndex++] = y;
                    if(isSelected()&(i%5==1)){
                        /*g.setXORMode(Color.white);
                        g.setColor(Color.black);
                        g.fill3DRect((int)x-2,y-2,(int)5,5,true);
                        g.setPaintMode();*/
                        // g.drawOval((int)x-3,y-3,(int)6,6);
                        //g.fillOval((int)x-2,y-2,(int)5,5);

                    }
                    if(bMarketData)
                        positions[nIndex++] = getBounds().left+xw+(i-startIndex)*xfactor;
                    else
                        positions[nIndex++] = xpos+xfactor;
                    positions[nIndex++] = y1;
                }
                y=y1;
                //xpos+=xfactor;
                if(bMarketData)
                    xpos = getBounds().left+xw+(i-startIndex)*xfactor;
                else
                    xpos+=xfactor;
            }

            float nHeight = _cvm.getBounds().bottom;
            float gijunY = max_view;
            _cvm.setLineWidth_Fix((int)COMUtil.getPixel_H(1));
//            _cvm.drawLine(gl, xw, 0, xw, nHeight, CoSys.vertLineColor, 1.0f); //수직선
            for(int i=0; i<3; i++)
            {
                if(_cvm.getAssetType() == ChartViewModel.ASSET_LINE_MOUNTAIN) {
                    nHeight = _cvm.getBounds().bottom - (int)COMUtil.getPixel(35);
                    _cvm.drawDashLine(gl, xw, (int) gijunY - (nHeight / 3 * (i + 1)) + COMUtil.getPixel_H(1), _cvm.getBounds().right - xw, (int) gijunY - (nHeight / 3 * (i + 1)) + COMUtil.getPixel_H(1), CoSys.vertLineColor, 1.0f);
                }
                else
                    _cvm.drawDashLine(gl, COMUtil.getPixel_W(20), (int)gijunY-(nHeight/3*(i+1))+COMUtil.getPixel_H(1), COMUtil.getPixel_W(340), (int)gijunY-(nHeight/3*(i+1))+COMUtil.getPixel_H(1), CoSys.vertLineColor, 1.0f);
            }
//            _cvm.drawLineWithFillGradient(gl,positions,max_view+5,upColor,51);
            _cvm.drawLineWithFillGradient(gl, positions, max_view , upColor, 102, nIndex, min_view);

        }
        else {
            int totLen = (dataLen-startPos-1)*4;
            float[] positions = new float[totLen];
            int nIndex = 0;
            //2020.07.06 by LYH >> 캔들볼륨 >>
            AREA area = _cvm.getArea(0);
            if(area!=null)
            {
                xpos = area.getCenter();
            }
            //2020.07.06 by LYH >> 캔들볼륨 <<
            //2024.01.03 by LYH - 일목균형표 줌인, 줌아웃, 스크롤 개선 >>
            dataLen = startIndex + _cvm.getViewNum();
            if(dataLen>data.length)
                dataLen= data.length;
            //2024.01.03 by LYH - 일목균형표 줌인, 줌아웃, 스크롤 개선 <<
            for(int i=startPos+1;i<dataLen;i++){
                //신용잔고율 마지막 데이터가 0일 경우 그리지 않도록 수정 2013.05.15 by lyk
                if(_cvm.isCredigJipyo==true && (i==dataLen-1 && data[i]==0)) {
                    usePreData = true; // 전일 데이터 사용
                    break;
                }

                if(data[i]==0 && bMarketData)
                {
                    continue;
                }
                float y1 = calcy(data[i]);

                //2017.05.17 by LYH >> 투자자차트 지수 마지막 0일때 앞의 지수값으로 채움
                if(data[i]==0 && _cvm.bInvestorChart && getTitle().equals("지수"))
                {
                    y1 = y;
                }
                //2017.05.17 by LYH >> 투자자차트 지수 마지막 0일때 앞의 지수값으로 채움 end

                if((y>=min_view-1&&y1>=min_view-1) && (y<max_view+1&&y1<max_view+1)){
                    //drawLine(gl, (int)xpos,(int)y,(int)(xpos+xfactor),(int)y1);
                    positions[nIndex++] = xpos;
                    positions[nIndex++] = y;
                    if(isSelected()&(i%5==1)){
                        /*g.setXORMode(Color.white);
                        g.setColor(Color.black);
                        g.fill3DRect((int)x-2,y-2,(int)5,5,true);
                        g.setPaintMode();*/
                        // g.drawOval((int)x-3,y-3,(int)6,6);
                        //g.fillOval((int)x-2,y-2,(int)5,5);

                    }
                    //2020.07.06 by LYH >> 캔들볼륨 >>
                    area = _cvm.getArea(i-startIndex);
                    if(area!=null)
                    {
                        xpos = area.getCenter();
                        positions[nIndex++] = xpos;
                    }
                    else {
                        //2020.07.06 by LYH >> 캔들볼륨 <<
                        if (bMarketData)
                            positions[nIndex++] = getBounds().left + xw + (i - startIndex) * xfactor;
                        else
                            positions[nIndex++] = xpos + xfactor;
                    }
                    positions[nIndex++] = y1;
                }
                y=y1;
                //xpos+=xfactor;
                if(area == null) {  //2020.07.06 by LYH >> 캔들볼륨
                    if (bMarketData)
                        xpos = getBounds().left + xw + (i - startIndex) * xfactor;
                    else
                        xpos += xfactor;
                }
            }
            if(nIndex>0)
            {
                //gl.glLineWidth(line_thick);

                if(_cvm.bIsOneQStockChart && _cvm.getAssetType() == ASSET_LINE_MOUNTAIN)
                    _cvm.setLineWidth(1.5f);
                else
                    _cvm.setLineWidth(line_thick);

                if (_cvm.bIsLine2Chart)
                    _cvm.setLineWidth(1);

                int[] cColor = upColor;

                //2019. 04. 08 by hyh - FX마진 데이터 조회 및 실시간 처리 >>
                if (_cvm.nFxMarginType == FX_BUYSELL) {
                    cColor = upColor;
                }
                //2019. 04. 08 by hyh - FX마진 데이터 조회 및 실시간 처리 <<

                //_cvm.drawLines(gl, positions, cColor, 1.0f);

//                if(_cvm.bInvestorChart && _cvm.getViewNum()< 50 && !_cvm.bRatePeriod)
//                    drawLinesWithCirclePoint(gl, positions, null);
                //drawLinesWithCircleUpDown(gl, positions);

                //2017.09.25 by LYH >> 자산 차트 적용
                if(1 == getDrawType1() && 1 == getDrawType2() && _cvm.getAssetType() ==  ChartViewModel.ASSET_LINE_MOUNTAIN)
                {
//    				_cvm.setLineWidth(3);
                    _cvm.drawLines(gl, positions, upColor ,1.0f);
                    _cvm.setLineWidth(1);
                    _cvm.drawLineWithFillGradient(gl, positions, this.max_view, upColor, 25);
                }
                else {
                    //2015. 9. 10 자산관리 라인오실레이터차트 디자인  >>
                    if(_cvm.getAssetType() == ChartViewModel.ASSET_LINE || _cvm.getAssetType() == ChartViewModel.ASSET_LINE_FILL)
                    {
                        float[] tmp = new float[nIndex];
                        System.arraycopy(positions,0,tmp,0,nIndex);
                        drawLinesWithCirclePointAni(gl, tmp, null);
                    }
                    else
                    {
                        //2017.09.25 by LYH >> 자산 차트 적용 end
                        _cvm.drawLines(gl, positions, upColor, 1.0f);
                        //2020.06.08 by LYH >> 마운틴 차트 툴팁 개선 Start
                        if(_cvm.getAssetType() == ASSET_LINE_MOUNTAIN)
                            drawLinesWithCirclePoint(gl, positions, null);
                        //2020.06.08 by LYH >> 마운틴 차트 툴팁 개선 End
//                        if (_cvm.bInvestorChart && _cvm.getViewNum() < 50 && !_cvm.bRatePeriod && COMUtil._mainFrame.bShowLineCircle) //2017.05.19 by lyk
//                            drawLinesWithCirclePoint(gl, positions, null);
                    }
                }

                _cvm.setLineWidth(1);
            }
        }


        //지표 현재값 표시.
        if(_cvm.useJipyoSign==true && !_cvm.bIsLine2Chart && _cvm.getAssetType() < 1) {
            double curVal = data[dataLen-1];
            String curStr = getFormatData(dataLen-1);

            if(usePreData) {
                if(data[dataLen-2]!=0) {
                    curVal = data[dataLen-2];
                    curStr = getFormatData(dataLen-2);
                }
            }

            //2020.12.22 by LYH >> 시장 지표 데이터 마지막 2개 이상 0일때 타이틀 처리 Start
            if(bMarketData && dataLen > 1)
            {
                for(int k=dataLen-1; k>=0; k--) {
                    if(data[k] != 0)
                    {
                        curVal=data[k];
                        curStr = getFormatData(k);
                        break;
                    }
                }
            }
            //2020.12.22 by LYH >> 시장 지표 데이터 마지막 2개 이상 0일때 타이틀 처리 End

            //2016. 2. 18 현재가 M표시, 반올림 처리>>
            if(Math.abs(max_data)>= 1000000000 || Math.abs(min_data)>= 1000000000)
            {
                double dData = curVal;

                if(dData >= 10000 && ((int)(dData/100000))%10 > 5)
                {
                    dData += 500000;
                }

                String strCur = ""+(int)(dData/1000000);
                curStr = ChartUtil.getFormatedData(strCur, 11)+"M";
            }
            else if(Math.abs(max_data)>= 10000000 || Math.abs(min_data)>= 10000000)
            {
                double dData = curVal;

                if(dData >= 10000 && ((int)(dData/100))%10 > 5)
                {
                    dData += 500;
                }

                String strCur = ""+(int)(dData/1000);
                curStr = ChartUtil.getFormatedData(strCur, 11)+"K";
            }
            //2016. 2. 18 현재가 M표시, 반올림 처리>>

            //int curStrLen = _cvm.tf.GetTextLength(curStr)+20;
            float yp = calcy(curVal);
            //xpos = this.getBounds().right+6;
            //xpos = _cvm.getBounds().left + _cvm.getBounds().width() - _cvm.Margin_R + 6;
            xpos = _cvm.getBounds().left + _cvm.getBounds().width() - _cvm.Margin_R +(int)COMUtil.getPixel(1);
            int pw = _cvm.Margin_R;

//            _cvm.drawFillTri(gl, xpos,yp,COMUtil.getPixel(4),(int)COMUtil.getPixel(14), upColor);
//            _cvm.drawFillRect(gl, xpos+(int)COMUtil.getPixel(4), yp-(int)COMUtil.getPixel(7), pw, (int)COMUtil.getPixel(14), upColor, 1.0f);

            if(yp+(int)COMUtil.getPixel_H(18)/2 > getBounds().bottom)
                yp = getBounds().bottom-(int)COMUtil.getPixel_H(18)/2;
            _cvm.drawCurrentPriceBox(gl, xpos, yp-COMUtil.getPixel_H(18)/2, pw-(int)COMUtil.getPixel(4), (int)COMUtil.getPixel_H(18), upColor);

            //_cvm.drawString(gl, CoSys.BLACK, (int)xpos, (int)yp, ChartUtil.getFormatedData(curVal, _cdm.getPriceFormat()));
            //_cvm.drawString(gl, CoSys.WHITE, (int)xpos, (int)yp, String.format("%.2f", curVal));
            //2013.03.27 by LYH >> 스케일 가격 오른쪽 정렬
            //_cvm.drawString(gl, CoSys.WHITE, (int)xpos, (int)yp, curStr);
            int w = _cvm.GetTextLength(curStr);
            //2021.07.12 by hanjun.Kim - kakaopay - 가격표 좌측정렬 >>

            int[] textColor = CoSys.WHITE;
            for(int i = 11; i <= 17; i++) {
                int[] indcColor = CoSys.CHART_COLORS[i];
                if(Arrays.equals(upColor, indcColor)) {
                    if(COMUtil.currentTheme == COMUtil.SKIN_BLACK) {
                        if(i == 14 || i == 16 || i == 17) {
                            textColor = CoSys.GREY0_WHITE;
                        } else {
                            textColor = CoSys.GREY990;
                        }
                    } else {
                        if(i == 14 || i == 16) {
                            textColor = CoSys.GREY990;
                        } else {
                            textColor = CoSys.GREY0_WHITE;
                        }
                    }
                }
            }

            _cvm.drawScaleString(gl, textColor, (int)xpos+(int)COMUtil.getPixel(3), yp, curStr);
            //2013.03.27 by LYH <<
            _cvm.useJipyoSign=false;
        }

//        //2019. 11. 26 by hyh - 라인차트에 기준선 그리기 >>
//        drawBaseLine(gl);
//        drawPivotDemarkLine(gl);
//        //2019. 11. 26 by hyh - 라인차트에 기준선 그리기 <<
    }
    public void draw(Canvas gl, double[] data, double[] stand){
        if(data==null||data.length<1)return;
        float x=getBounds().left+xw;
        float sp=0 ;
        for(int i=0;i<stand.length;i++){
            sp = calcy(stand[i]);//기준가의 픽셀좌표를 얻는다
        }
        _cvm.drawLine(gl, getBounds().left,sp,getBounds().left+getBounds().width(),sp, CoSys.DKGRAY ,1.0f);
        float y = calcy(data[0]);
        for(int i=1;i<data.length;i++){
            float y1 = calcy(data[i]);
            if(y<=max_view&&y1<=max_view){
                drawStandLine(gl,(int)x,(int)y,(int)(x+xfactor),(int)y1,(int)sp);
                if(isSelected()&&(i%5==0)){
                    _cvm.drawRect(gl, (int)x-2,y, 5,5, CoSys.DKGRAY);
                }
            }

            x+=xfactor;
            y =y1;
        }
    }
    public void draw(Canvas gl, double[][] data){//기준가 없이 그리는 바
        if(data==null||data.length<1)return;
        float x=getBounds().left+xw;
        float y = calcy(data[0][0]);
        for(int i=1;i<data.length;i++){
            float y1 = calcy(data[i][0]);
            if(y<=max_view&&y1<=max_view){
                drawLine(gl,(int)x,(int)y,(int)(x+xfactor),(int)y1);
                if(isSelected()&&(i%5==0)){
                    _cvm.drawRect(gl, (int)x-2,y, 5,5, upColor);
                }
            }
            y=y1;
            x+=xfactor;
        }
    }
    public void draw(Canvas gl, double[][] data, double[] stand){
        if(data==null)return;
        float x=getBounds().left+xw;
        float sp=0 ;
        for(int i=0;i<stand.length;i++){
            sp = calcy(stand[i]);//기준가의 픽셀좌표를 얻는다
        }

        float y = calcy(data[0][0]);
        for(int i=1;i<data.length;i++){
            float y1 = calcy(data[i][0]);
            if(y<=max_view&&y1<=max_view){
                drawStandLine(gl,(int)x,(int)y,(int)(x+xfactor),(int)y1,(int)sp);
                if(isSelected()&&(i%5==0)){
                    _cvm.drawFillRect(gl, (int)x-2,y, 5,5, CoSys.DKGRAY, 1.0f);
                }
            }
            x+=xfactor;
            y =y1;
        }
    }
    //    private void drawDotLine(Canvas gl,int sx,int sy, int ex, int ey){
//        int term = 1;
//        float dx =(ex-sx)/term;
//        float dy =(ey-sy)/term;
//        for(int i=0;i<3;i++){
//            _cvm.drawLine(gl,(int)(sx+(dx*i)),(int)(sy+(dy*i)),(int)(sx+(dx*i+1)),(int)(sy+(dy*i+1)), _cvm.CST ,1.0f);
//        }
//    }
    private void drawDot(Canvas gl, float x, float y){
        //_cvm.drawCircle(gl,  x, y, (float)5, true, upColor);
        //_cvm.drawCircle(gl, x,y,x+5, y+5, false, upColor);
        //2013.03.27 by LYH >> Parabolic Sar 두께 처리.
        int nWidth = 4+line_thick;
        _cvm.drawCircle(gl, x,y,x+nWidth, y+nWidth, true, upColor);
    }
    private void drawLine(Canvas gl,float sx, float sy, float ex, float ey){
        _cvm.setLineWidth(line_thick);
        switch(getDrawType2()){
            case 0://일반
                _cvm.drawLine(gl, sx,sy,ex,ey, upColor ,1.0f);
                break;
            case 1://채움
                drawFillLine(gl,sx,sy,ex,ey,(int)calcy(stand[0]));
                break;
            case 2://구름
                drawFillLine_Lough(gl,sx,sy,ex,ey,(int)calcy(stand[0]));
                break;
            case 3://대비
                drawStandLine(gl,sx,sy,ex,ey,(int)calcy(stand[0]));
                break;
            case 4://레인보우식
                _cvm.drawLine(gl, sx,sy,ex,ey, upColor ,0.5f);
                break;
            case 5://도트식
                drawDot(gl,sx-(int)xw,sy);
                break;
            default:
                _cvm.drawLine(gl, sx,sy,ex,ey, upColor ,1.0f);
                break;
        }
        _cvm.setLineWidth(1);
    }

    private void drawFillLine(Canvas gl,float sx, float sy, float ex, float ey, float sp){//채움식
        float[] x = {sx,sx,ex,ex};
        float[] y = {sy,sp,sp,ey};
        if(ey<sp){
            if(sy<sp){
                _cvm.drawLine(gl, sx,sy,ex,ey, upColor ,1.0f);
            }else{
//            	pnt.setColor(downColor);
                x[2]=sx+getCrossX(sp,sx,ex,sy,ey);
                _cvm.drawLine(gl, sx,sy,sx+getCrossX(sp,sx,ex,sy,ey),sp, downColor ,1.0f);
//                pnt.setColor(upColor);
                x[0]=sx+getCrossX(sp,sx,ex,sy,ey);
                x[1]=ex;
                x[2]=ex;
                y[0]=sp;
                y[1]=ey;
                y[2]=sp;
                _cvm.drawLine(gl, sx+getCrossX(sp,sx,ex,sy,ey),sp,ex,ey, downColor ,1.0f);
            }
        }else{
            if(sy>sp){
                _cvm.drawLine(gl, sx,sy,ex,ey, downColor ,1.0f);
            }else{
//            	pnt.setColor(upColor);
                x[2]=sx+getCrossX(sp,sx,ex,sy,ey);
                _cvm.drawLine(gl, sx,sy,sx+getCrossX(sp,sx,ex,sy,ey),sp, upColor ,1.0f);
//                pnt.setColor(downColor);
                x[0]=sx+getCrossX(sp,sx,ex,sy,ey);
                x[1]=ex;
                x[2]=ex;
                y[0]=sp;
                y[1]=ey;
                y[2]=sp;
                _cvm.drawLine(gl, sx+getCrossX(sp,sx,ex,sy,ey),sp,ex,ey, downColor ,1.0f);
            }
        }
    }
    private void drawFillLine_Lough(Canvas gl,float sx, float sy, float ex, float ey, float sp){//구름식
        float[] x = {sx,sx,ex,ex};
        float[] y = {sy,sp,sp,ey};
        if(ey<sp){
            if(sy<sp){
                _cvm.drawLine(gl, sx,sy,ex,ey, upColor ,1.0f);
            }else{
//            	pnt.setColor(downColor);
                x[2]=sx+getCrossX(sp,sx,ex,sy,ey);
                _cvm.drawLine(gl, sx,sy,sx+getCrossX(sp,sx,ex,sy,ey),sp, downColor ,1.0f);
//                pnt.setColor(upColor);
                x[0]=sx+getCrossX(sp,sx,ex,sy,ey);
                x[1]=ex;
                x[2]=ex;
                y[0]=sp;
                y[1]=ey;
                y[2]=sp;
                _cvm.drawLine(gl, sx+getCrossX(sp,sx,ex,sy,ey),sp,ex,ey, downColor ,1.0f);
            }
        }else{
            if(sy>sp){
                _cvm.drawLine(gl, sx,sy,ex,ey, downColor ,1.0f);
            }else{
//            	pnt.setColor(upColor);
                x[2]=sx+getCrossX(sp,sx,ex,sy,ey);
                _cvm.drawLine(gl, sx,sy,sx+getCrossX(sp,sx,ex,sy,ey),sp, upColor ,1.0f);
//                pnt.setColor(downColor);
                x[0]=sx+getCrossX(sp,sx,ex,sy,ey);
                x[1]=ex;
                x[2]=ex;
                y[0]=sp;
                y[1]=ey;
                y[2]=sp;
                _cvm.drawLine(gl, sx+getCrossX(sp,sx,ex,sy,ey),sp,ex,ey, downColor ,1.0f);
            }
        }
    }
    private void drawStandLine(Canvas gl,float sx, float sy, float ex, float ey, float sp){
        if(ey<sp){
            if(sy<sp){
                _cvm.drawLine(gl, sx,sy,ex,ey, upColor ,1.0f);
            }else{
                _cvm.drawLine(gl, sx,sy,sx+getCrossX(sp,sx,ex,sy,ey),sp, CoSys.BLACK ,1.0f);
                _cvm.drawLine(gl, sx+getCrossX(sp,sx,ex,sy,ey),sp,ex,ey, upColor ,1.0f);
            }
        }else{
            if(sy>sp){
                _cvm.drawLine(gl, sx,sy,ex,ey, downColor ,1.0f);
            }else{
                _cvm.drawLine(gl, sx,sy,sx+getCrossX(sp,sx,ex,sy,ey),sp, upColor ,1.0f);
                _cvm.drawLine(gl, sx+getCrossX(sp,sx,ex,sy,ey),sp,ex,ey, CoSys.BLACK ,1.0f);
            }
        }
    }

    //2015. 1. 13 보조지표 bar 타입 유형 변경 기능>>
    public void drawForBar(Canvas gl, double[] data){
        //BarDraw할거면 LineDraw 클래스에서는 BarDraw의 draw함수 기능 카피한 이 함수를 통해 그림

        if(data==null)return;

        this.data = data;
        double[] sprice = _cdm.getSubPacketData("시가");
        //2012. 8. 30 고가 추가 : C16
        double[] hprice = _cdm.getSubPacketData("고가");
        double[] price = _cdm.getSubPacketData("종가");
        //if(price==null) return;

        float x=getBounds().left+xw;
        float sp = max_view;
        //int[] cColor=null;
        int startIndex = _cvm.getIndex();
        int nTotCnt = _cdm.getCount();
        if(startIndex>=nTotCnt)
        {
            startIndex = 0;
        }
        //20120621 by LYH >> 일목균형 스크롤 처리
        //int dataLen = startIndex + _cvm.getViewNum();
        int dataLen = startIndex + _cvm.getViewNum() + _cvm.futureMargin;
        //20120621 by LYH <<
        if(dataLen>data.length)
            dataLen= data.length;
        float fWidth = (xw*2);
        if(fWidth<1) {
            //2020. 06. 08 by hyh - 우측으로 쏠린 머리, 꼬리 위치 가운데로 조정. Line >>
            fWidth -= COMUtil.getPixel(1);
            //fWidth = 1;
            //2020. 06. 08 by hyh - 우측으로 쏠린 머리, 꼬리 위치 가운데로 조정. Line <<
        }
//        if(xw<1&&getDrawType2()==0){
//            for(int i=startIndex;i<dataLen;i++){
//            	float y = calcy(data[i]);
//                y=(y>sp)?sp:y;
//                switch(_cvm.getVolDrawType()){ 
//                    case 0:
//                        cColor=upColor;
//                    break; 
//                    case 1:
//                        if(price==null||i==0){
//                        	 cColor=upColor;
//                        }
//                        else{
//                            if(price[i-1]>price[i]) {
//                            	 cColor=downColor;
//                            }
//                            else {
//                            	cColor=upColor;
//                            }
//                        }
//                    break;
//                    case 2:
//                        if(i==0){
//                        	cColor=upColor;
//                        }
//                        else{
//                            if(data[i-1]>data[i]){
//                            	cColor=downColor;
//                            }
//                            else {
//                            	cColor=upColor;
//                            }
//                        }
//                    break;
//                    case 3:
//                        if(price==null||sprice==null){
//                        	cColor=upColor;
//                        }
//                        else{
//                            if(price[i]>sprice[i]){
//                            	cColor=upColor;
//                            }
//                            else {
//                            	cColor=downColor;
//                            }
//                        }
//                    break;
//                    
//                }
//                _cvm.drawLine(gl, (int)x,y,(int)x,sp, cColor ,1.0f);
//                x+=xfactor;
//            }   
//        }else{
//            if(!getTitle().equals("기본거래량")){
//                for(int i=startIndex;i<dataLen;i++){
//                	float y = calcy(data[i]);
//                    y=(y>sp)?sp:y;
//                    float spy = sp-y;
//                    
//                    drawBar(gl,(int)(x-xw),(int)y,(int)(2*xw),(int)spy-(int)y,upColor);
//                    if(isSelected()&(i%5==0)){
//
//                    	_cvm.drawRect(gl, (int)x-2,y,(int)5,5, CoSys.YELLOW);
//
//                    }
//                    x+=xfactor;
//                }                
//            }else{
        float y = 0;

        int totLen = (dataLen-startIndex)*4*3;
        if(totLen<=0)
            return;
        float[] rectPositions = new float[totLen];
        float[] rectPositionsUp = new float[totLen];
        float[] rectPositionsDown = new float[totLen];
        int nRectIndex = 0;
        int nRectIndexUp = 0;
        int nRectIndexDown = 0;
        int nVolDrawType = _cvm.getVolDrawType();
        int nTypeOscUpDown = -1;
        //2015. 3. 2 라인바 적용 후 지표설정 저장값 꼬임>>
//              if(this.getTitle().equals("기본거래량"))
//              {
//              	//2012. 8. 30   기본거래량(거래량차트) 일때 강제로 2번째(전일대비고가의 상승하락) 으로 세팅 주석처리 : C17
//              	//nVolDrawType = 2;
//              }
//              else {
//              	if(this.getDrawType2()==2) {
//              		nVolDrawType = 10;
//              	} else {
//              		nVolDrawType = 0;
//              	}
//              }
        nVolDrawType = 10;
        //2015. 3. 2 라인바 적용 후 지표설정 저장값 꼬임<<
        for(int i=startIndex;i<dataLen;i++){
            try {
                y = calcy(data[i]);
            } catch(Exception e) {
//                		System.out.println(e.getMessage());
            }
            if(nVolDrawType!= 10) {
                y=(y>sp)?sp:y;
            }
            float spy = sp-y;
            if(spy<0) {
                spy=0;
            }
            //2020. 06. 08 by hyh - 우측으로 쏠린 머리, 꼬리 위치 가운데로 조정. Line >>
            float fStart = x - xw;
            //float fStart = x - xw - 1;
            //2020. 06. 08 by hyh - 우측으로 쏠린 머리, 꼬리 위치 가운데로 조정. Line <<

            float fEnd = fStart + fWidth;
            //2020.07.06 by LYH >> 캔들볼륨 >>
            AREA area = _cvm.getArea(i-startIndex);
            if(area!=null)
            {
                fStart = area.getLeft();
                fEnd = area.getRight();
            }
            //2020.07.06 by LYH >> 캔들볼륨 <<
            if(nVolDrawType!=10) {
                if(spy<=1 && data[i] !=0) {//h가 0인 경우 그래프를 보이기 위함.
                    y-=(2-spy);
                }
            }
            //2012. 8. 30 거래량의 선택된 라디오 버튼에 따라서 처리기능 구현 : I98
            switch(nVolDrawType){
                case 0:
                    //drawBar(gl,(int)(x-xw),(int)y,(int)(2*xw),sp-(int)y,upColor);
                    if(spy<=1) {//h가 0인 경우 그래프를 보이기 위함.
                        y-=(2-spy);
                    }
                    addRectPosition(rectPositions, nRectIndex, fStart, y, fEnd, sp);
                    nRectIndex += 4;
                    break;
                case 1:
                    if(price==null||i==0)
                    {
//                            	drawBar(gl,(int)(x-xw),(int)y,(int)(2*xw),sp-(int)y,upColor);
                        addRectPosition(rectPositionsUp, nRectIndexUp, fStart, y, fEnd, sp);
                        nRectIndexUp += 4;
                    }
                    else{
                        if(price[i-1]>price[i]){
//                                    drawBar(gl,(int)(x-xw),(int)y,(int)(2*xw),sp-(int)y,downColor);
                            addRectPosition(rectPositionsDown, nRectIndexDown, fStart, y, fEnd, sp);
                            nRectIndexDown += 4;
                        }else if(price[i-1]<price[i]){
//                                    drawBar(gl,(int)(x-xw),(int)y,(int)(2*xw),sp-(int)y,upColor);
                            addRectPosition(rectPositionsUp, nRectIndexUp, fStart, y, fEnd, sp);
                            nRectIndexUp += 4;
                        }
                        else
                        {
                            addRectPosition(rectPositions, nRectIndex, fStart, y, fEnd, sp);
                            nRectIndex += 4;
                        }
                    }
                    break;
                case 2:
                    if(hprice==null||i==0)
                    {
                        addRectPosition(rectPositionsUp, nRectIndexUp, fStart, y, fEnd, sp);
                        nRectIndexUp += 4;
//                            		drawBar(gl,(int)(x-xw),(int)y,(int)(2*xw),sp-(int)y,upColor);
                    }
                    else{
                        if(hprice[i-1]>hprice[i]){
                            addRectPosition(rectPositionsDown, nRectIndexDown, fStart, y, fEnd, sp);
                            nRectIndexDown += 4;
//                                    drawBar(gl,(int)(x-xw),(int)y,(int)(2*xw),sp-(int)y,downColor);
                        }else if(hprice[i-1]<hprice[i]){
                            addRectPosition(rectPositionsUp, nRectIndexUp, fStart, y, fEnd, sp);
                            nRectIndexUp += 4;
//                                    drawBar(gl,(int)(x-xw),(int)y,(int)(2*xw),sp-(int)y,upColor);
                        }
                        else
                        {
                            addRectPosition(rectPositions, nRectIndex, fStart, y, fEnd, sp);
                            nRectIndex += 4;
                        }
                    }
                    break;
                case 3:
                    if(i==0)
                    {
                        addRectPosition(rectPositionsUp, nRectIndexUp, fStart, y, fEnd, sp);
                        nRectIndexUp += 4;
                        //drawBar(gl,(int)(x-xw),(int)y,(int)(2*xw),sp-(int)y,upColor);
                    }
                    else{
                        if(data[i-1]>data[i])
                        {
                            addRectPosition(rectPositionsDown, nRectIndexDown, fStart, y, fEnd, sp);
                            nRectIndexDown += 4;
                            //drawBar(gl,(int)(x-xw),(int)y,(int)(2*xw),sp-(int)y,downColor);
                        }
                        else if(data[i-1]<data[i])
                        {
                            addRectPosition(rectPositionsUp, nRectIndexUp, fStart, y, fEnd, sp);
                            nRectIndexUp += 4;
                            //drawBar(gl,(int)(x-xw),(int)y,(int)(2*xw),sp-(int)y,upColor);
                        }
                        else
                        {
                            addRectPosition(rectPositions, nRectIndex, fStart, y, fEnd, sp);
                            nRectIndex += 4;
                        }
                    }
                    break;
                case 4:
                    if(price==null||sprice==null)
                    {
                        addRectPosition(rectPositionsUp, nRectIndexUp, fStart, y, fEnd, sp);
                        nRectIndexUp += 4;
//                            	drawBar(gl,(int)(x-xw),(int)y,(int)(2*xw),sp-(int)y,upColor);
                    }
                    else{
                        if(price[i]>sprice[i])
                        {
                            addRectPosition(rectPositionsUp, nRectIndexUp, fStart, y, fEnd, sp);
                            nRectIndexUp += 4;
                            //                            		drawBar(gl,(int)(x-xw),(int)y,(int)(2*xw),sp-(int)y,upColor);
                        }
                        else if(price[i]<sprice[i])
                        {
                            addRectPosition(rectPositionsDown, nRectIndexDown, fStart, y, fEnd, sp);
                            nRectIndexDown += 4;
                            //                                	drawBar(gl,(int)(x-xw),(int)y,(int)(2*xw),sp-(int)y,downColor);
                        }
                        else
                        {
                            addRectPosition(rectPositions, nRectIndex, fStart, y, fEnd, sp);
                            nRectIndex += 4;
                        }
                    }
                    break;
                case 10:
                {
                    float sp1 = calcy(stand[0]);
                    float y1 = y;
                    if(sp1>max_view)sp1=max_view;
                    else if(sp1 < min_view)	sp1 = min_view;		//2015. 3. 2 라인바 적용 후 지표설정 저장값 꼬임

                    //2015. 3. 3 지표 바타입 변환 후 하락바가 지표블럭 영역을 넘어감>>
                    if(y1>max_view)y1=max_view;
                    else if(y1 < min_view)	y1 = min_view;
                    //2015. 3. 3 지표 바타입 변환 후 하락바가 지표블럭 영역을 넘어감<<

                    if(data[i] >= stand[0]) {
                        if(sp1==y1) {
                            y1--;
                        }
                        addRectPosition(rectPositionsUp, nRectIndexUp, fStart, y1, fEnd, sp1);
                        nRectIndexUp += 4;
                        nTypeOscUpDown = OSC_UP;
                    } else {
                        if(sp1==y1) {
                            y1++;
                        }
                        //2013.04.05 by LYH >> 젤리빈 4.2.2이상 버전 오실레이터에 0 이하 바 안 그려지던 오류 수정.
                        //addRectPosition(rectPositionsDown, nRectIndexDown, nStart, y1, nEnd, sp1);
                        addRectPosition(rectPositionsDown, nRectIndexDown, fStart, sp1, fEnd, y1);
                        //2013.04.05 by LYH <<
                        nRectIndexDown += 4;
                        nTypeOscUpDown = OSC_DOWN;
                    }

                }
            }
//                    if(isSelected()&(i%5==0)){
//
//                        _cvm.drawRect(gl, (int)x-2,y,(int)5,5, CoSys.YELLOW);
//
//                    }
            x+=xfactor;
        }
        //2012. 8. 30  거래량 상세설정에서 초기화시 색상이 초기화 되지 않는 현상 수정 : I98
        if(nRectIndex>0)
        {   float[] tmp = new float[nRectIndex];
            System.arraycopy(rectPositions,0,tmp,0,nRectIndex);
            if(_cvm.bIsLineFillChart )
            {
                int[] colorLine = new int[3];
                colorLine[0] = 31;
                colorLine[1] = 76;
                colorLine[2] = 115;
                _cvm.drawFillRects(gl, tmp, colorLine ,1.0f);
            }
            else if(_cvm.bStandardLine)
            {
                _cvm.drawFillRects(gl, tmp, CoSys.STANDARD_VOL ,1.0f);
            }
            else
            {
                //2013.10.08 by LYH >> 거래량 일반 색상 적용.
//	                	if(nVolDrawType == 0 && !this.getTitle().equals("기본거래량"))
//	                	{
//	                		_cvm.drawFillRects(gl, tmp, upColor ,1.0f);
//	                	}
//	                	else
//	                		_cvm.drawFillRects(gl, tmp, sameColor,1.0f);            			
                if(this.getTitle().equals("기본거래량"))
                {
                    if(nVolDrawType == 0)
                    {
                        _cvm.drawFillRects(gl, tmp, sameColor,1.0f);
                    }
                    else
                    {
                        _cvm.drawFillRects(gl, tmp, CoSys.CHART_COLORS[2],1.0f);
                    }
                }
                else
                {
                    _cvm.drawFillRects(gl, tmp, upColor ,1.0f);
                }
                //2013.10.08 by LYH <<
            }
        }
        if(nRectIndexUp>0)
        {   float[] tmp = new float[nRectIndexUp];
            System.arraycopy(rectPositionsUp,0,tmp,0,nRectIndexUp);
            if(_cvm.bIsLineFillChart )
            {
                int[] colorLine = new int[3];
                colorLine[0] = 31;
                colorLine[1] = 76;
                colorLine[2] = 115;
                _cvm.drawFillRects(gl, tmp, colorLine ,1.0f);
            }
            else
            {
                if(nVolDrawType == 10) //오실레이터
                {
                    if(_cvm.bInvestorChart)
                        _cvm.drawFillRects(gl, tmp, CoSys.CHART_COLORS[14],1.0f);
                    else
                    {
//               				_cvm.drawFillRects(gl, tmp, CoSys.CHART_COLORS[4],1.0f);
                        _cvm.drawFillRects(gl, tmp, upColor,1.0f);	//2015. 3. 2 라인바 적용 후 지표설정 저장값 꼬임
                    }
                }
                else
                    _cvm.drawFillRects(gl, tmp, upColor,1.0f);
            }
        }
        if(nRectIndexDown>0)
        {   float[] tmp = new float[nRectIndexDown];
            System.arraycopy(rectPositionsDown,0,tmp,0,nRectIndexDown);
            if(_cvm.bIsLineFillChart )
            {
                int[] colorLine = new int[3];
                colorLine[0] = 31;
                colorLine[1] = 76;
                colorLine[2] = 115;
                _cvm.drawFillRects(gl, tmp, colorLine ,1.0f);
            }
            else
            {
                if(_cvm.bInvestorChart)
                    _cvm.drawFillRects(gl, tmp, CoSys.CHART_COLORS[15],1.0f);
                else
                {
                    //2015. 3. 2 라인바 적용 후 지표설정 저장값 꼬임>>
//        					_cvm.drawFillRects(gl, tmp, downColor ,1.0f);
                    int[] colorLine = new int[3];
                    colorLine[0] = 120;
                    colorLine[1] = 120;
                    colorLine[2] = 120;
                    _cvm.drawFillRects(gl, tmp, colorLine ,1.0f);
                    //2015. 3. 2 라인바 적용 후 지표설정 저장값 꼬임<<
                }
            }
        }
//            }
//        }

        //지표 현재값 표시.
        //지표 현재값 표시.
        if(_cvm.useJipyoSign==true && !_cvm.bIsLineFillChart && !_cvm.bStandardLine) {
            double curVal = data[dataLen-1];

            //2012. 10. 19 거래량 yscale 빨간색 표시되던 현상 수정 : C23
//	        if(getTitle().equals("기본거래량")) {//거래량은 YScale에서 표시함.
//	        	return;
//	        }
            String curStr = getFormatData(dataLen-1);
            if(getTitle().equals("기본거래량"))
            {
                if(max_data >= 10000000)
                {
                    double dData = curVal;
                    String strCur = ""+(int)(dData/1000);
                    curStr = ChartUtil.getFormatedData(strCur, 11);
                }
            }
            //int curStrLen = _cvm.tf.GetTextLength(curStr)+10;

            float yp = calcy(curVal);
            float xpos = this.getBounds().right;

            xpos = _cvm.getBounds().left + _cvm.getBounds().width() - _cvm.Margin_R +(int)COMUtil.getPixel(1);
            int pw = _cvm.Margin_R;

            //2012. 10. 19 거래량 yscale 빨간색 표시되던 현상 수정  : C23
            if(nTypeOscUpDown==OSC_UP)
            {
//                _cvm.drawFillTri(gl, xpos,yp,COMUtil.getPixel(4),(int)COMUtil.getPixel(14), CoSys.CHART_COLORS[14]);
//                _cvm.drawFillRect(gl, xpos+(int)COMUtil.getPixel(4), yp-(int)COMUtil.getPixel(7), pw, (int)COMUtil.getPixel(14), CoSys.CHART_COLORS[14], 1.0f);
                _cvm.drawCurrentPriceBox(gl, xpos, yp-(int)COMUtil.getPixel_H(18)/2, pw-(int)COMUtil.getPixel(4), (int)COMUtil.getPixel_H(18), CoSys.CHART_COLORS[14]);
            }
            else if(nTypeOscUpDown==OSC_DOWN)
            {
//                _cvm.drawFillTri(gl, xpos,yp,COMUtil.getPixel(4),(int)COMUtil.getPixel(14), CoSys.CHART_COLORS[15]);
//                _cvm.drawFillRect(gl, xpos+(int)COMUtil.getPixel(4), yp-(int)COMUtil.getPixel(7), pw, (int)COMUtil.getPixel(14), CoSys.CHART_COLORS[15], 1.0f);
                _cvm.drawCurrentPriceBox(gl, xpos, yp-(int)COMUtil.getPixel_H(18)/2, pw-(int)COMUtil.getPixel(4), (int)COMUtil.getPixel_H(18), CoSys.CHART_COLORS[15]);
            }
            else if(getTitle().equals("기본거래량")) {//거래량은 YScale에서 표시함.
//                _cvm.drawFillTri(gl, xpos,yp,COMUtil.getPixel(4),(int)COMUtil.getPixel(14), CoSys.CHART_COLORS[2]);
//                _cvm.drawFillRect(gl, xpos+(int)COMUtil.getPixel(4), yp-(int)COMUtil.getPixel(7), pw, (int)COMUtil.getPixel(14), CoSys.CHART_COLORS[2], 1.0f);
                _cvm.drawCurrentPriceBox(gl, xpos, yp-(int)COMUtil.getPixel_H(18)/2, pw-(int)COMUtil.getPixel(4), (int)COMUtil.getPixel_H(18), CoSys.CHART_COLORS[2]);
            }
            else
            {
//                _cvm.drawFillTri(gl, xpos,yp,COMUtil.getPixel(4),(int)COMUtil.getPixel(14), upColor);
//                _cvm.drawFillRect(gl, xpos+(int)COMUtil.getPixel(4), yp-(int)COMUtil.getPixel(7), pw, (int)COMUtil.getPixel(14), upColor, 1.0f);
                _cvm.drawCurrentPriceBox(gl, xpos, yp-COMUtil.getPixel_H(18)/2, pw-(int)COMUtil.getPixel(4), (int)COMUtil.getPixel_H(18), upColor);
            }


            //_cvm.drawString(gl, CoSys.BLACK, (int)xpos, (int)yp, ChartUtil.getFormatedData(curStr, _cdm.getPriceFormat()));
            //_cvm.drawString(gl, CoSys.WHITE, (int)xpos, (int)yp, String.format("%.2f", curVal));
            //2013.03.27 by LYH >> 스케일 가격 오른쪽 정렬
            //_cvm.drawString(gl, CoSys.WHITE, (int)xpos, (int)yp, curStr);
            int w = _cvm.GetTextLength(curStr);
            //_cvm.drawString(gl, CoSys.WHITE, (int)xpos+pw-w-(int)COMUtil.getPixel(3), (int)yp, curStr);
            //2021.07.12 by hanjun.Kim - kakaopay - 가격표 좌측정렬 >>
            _cvm.drawScaleString(gl, CoSys.WHITE, xpos +(int)COMUtil.getPixel(3), yp, curStr);
            //2013.03.27 by LYH <<
            _cvm.useJipyoSign=false;
        }
    }
    //2015. 1. 13 보조지표 bar 타입 유형 변경 기능<<

    //2015. 9. 15 자산관리 라인오실레이터차트 디자인 >>
    private void drawLinesWithCirclePoint(Canvas gl, float[] positions, float[] newDatas)
    {
        int[] whiteColor = {255, 255, 255};
        int nOuterCircleRadius = (int)COMUtil.getPixel(4);
        int nInnerCircleRadius = (int)COMUtil.getPixel(2);

        int startIndex=_cvm.getIndex();
        RectF chart_bounds = getBounds();
        int nIndex = positions.length;
        int[] vertLineColor = {235,235,235};
        int[] vertLineColorOneq = CoSys.BLACK;
        for(int i = 0; i < nIndex; i += 4)
        {
            //2020.06.08 by LYH >> 마운틴 차트 툴팁 개선 Start
            if(_cvm.getAssetType()==ASSET_LINE_MOUNTAIN)
            {
                try {
                    if(!COMUtil._neoChart.getShowTooltip())
                        return;
                } catch (Exception e) {

                }
                if(_cvm.curIndex==i/4+startIndex || _cvm.curIndex==(nIndex/4+startIndex))
                {
                    _cvm.setLineWidth(1);
                    nOuterCircleRadius = (int)COMUtil.getPixel(5);
                    nInnerCircleRadius = (int)COMUtil.getPixel(3);
                    if(_cvm.curIndex==(nIndex/4+startIndex))
                    {
                        _cvm.drawLine(gl, (int) positions[nIndex - 4 + 2], chart_bounds.top, (int) positions[nIndex - 4 + 2], chart_bounds.bottom, vertLineColorOneq, 1.0f);
                        _cvm.drawCircle(gl, (int) positions[nIndex - 4 + 2] - nOuterCircleRadius, (int) positions[nIndex - 4 + 3] - nOuterCircleRadius, positions[nIndex - 4 + 2] + nOuterCircleRadius, positions[nIndex - 4 + 3] + nOuterCircleRadius, true, upColor);
                    }
                    else
                    {
                        _cvm.drawLine(gl, (int) positions[i], chart_bounds.top, (int) positions[i], chart_bounds.bottom, vertLineColorOneq, 1.0f);
                        _cvm.drawCircle(gl, (int) positions[i] - nOuterCircleRadius, (int) positions[i + 1] - nOuterCircleRadius, positions[i] + nOuterCircleRadius, positions[i + 1] + nOuterCircleRadius, true, upColor);
                    }
                    if(_cvm.curIndex==(nIndex/4+startIndex))
                        _cvm.drawCircle(gl, (int) positions[nIndex - 4 + 2] - nInnerCircleRadius, (int) positions[nIndex - 4 + 3] - nInnerCircleRadius, positions[nIndex - 4 + 2] + nInnerCircleRadius, positions[nIndex - 4 + 3] + nInnerCircleRadius, true, whiteColor);
                    else
                        _cvm.drawCircle(gl, (int) positions[i] - nInnerCircleRadius, (int) positions[i + 1] - nInnerCircleRadius, positions[i] + nInnerCircleRadius, positions[i + 1] + nInnerCircleRadius, true, whiteColor);
                    break;
                }
            }
            else
            //2020.06.08 by LYH >> 마운틴 차트 툴팁 개선 End
            {
                if (i == 0)
                    _cvm.drawCircle(gl, (int) positions[i] - nOuterCircleRadius, (int) positions[i + 1] - nOuterCircleRadius, positions[i] + nOuterCircleRadius, positions[i + 1] + nOuterCircleRadius, true, upColor);
                _cvm.drawCircle(gl, (int) positions[i + 2] - nOuterCircleRadius, (int) positions[i + 3] - nOuterCircleRadius, positions[i + 2] + nOuterCircleRadius, positions[i + 3] + nOuterCircleRadius, true, upColor);

                if (i == 0)
                    _cvm.drawCircle(gl, (int) positions[i] - nInnerCircleRadius, (int) positions[i + 1] - nInnerCircleRadius, positions[i] + nInnerCircleRadius, positions[i + 1] + nInnerCircleRadius, true, whiteColor);
                _cvm.drawCircle(gl, (int) positions[i + 2] - nInnerCircleRadius, (int) positions[i + 3] - nInnerCircleRadius, positions[i + 2] + nInnerCircleRadius, positions[i + 3] + nInnerCircleRadius, true, whiteColor);
            }
        }
    }
    private void drawLinesWithCircleUpDown(Canvas gl, float[] positions)
    {
        int nOuterCircleRadius = (int)COMUtil.getPixel(4);
        int[] redColor = {255, 0, 0};
        int[] blueColor = {0, 0, 255};
        for(int i = 0; i < positions.length; i += 4)
        {
            if(i==0)
            {
                if(positions[i+1]<positions[i+3])
                    _cvm.drawCircle(gl, (int)positions[i]-nOuterCircleRadius, (int)positions[i+1]-nOuterCircleRadius, positions[i]+nOuterCircleRadius, positions[i+1]+nOuterCircleRadius, true, redColor);
                else
                    _cvm.drawCircle(gl, (int)positions[i]-nOuterCircleRadius, (int)positions[i+1]-nOuterCircleRadius, positions[i]+nOuterCircleRadius, positions[i+1]+nOuterCircleRadius, true, blueColor);
            }
            if(positions[i+3]<positions[i+1])
                _cvm.drawCircle(gl, (int)positions[i+2]-nOuterCircleRadius, (int)positions[i+3]-nOuterCircleRadius, positions[i+2]+nOuterCircleRadius, positions[i+3]+nOuterCircleRadius, true, redColor);
            else
                _cvm.drawCircle(gl, (int)positions[i+2]-nOuterCircleRadius, (int)positions[i+3]-nOuterCircleRadius, positions[i+2]+nOuterCircleRadius, positions[i+3]+nOuterCircleRadius, true, blueColor);
        }
    }

    //2017.09.25 by LYH >> 자산 차트 적용
    /**
     * Allow a new delay before the automatic cache clear is done.
     */
    //2015. 9. 15 자산관리 라인오실레이터차트 디자인 >>
    private void drawLinesWithCirclePointAni(Canvas gl, float[] positions, float[] newDatas)
    {
        //구분선
        if(_cvm.getAssetType() == ChartViewModel.ASSET_LINE_FILL) {
            float yBasePos = 0;
            int[] nLeftLineCol = {240, 240, 240};
            _cvm.setLineWidth(1);
            for(int i=0; i<4; i++) {
                yBasePos = getBounds().bottom/4;
                yBasePos *= i;
                _cvm.drawDashLine(gl, getBounds().left - COMUtil.getPixel_W(2), yBasePos + _cvm.BMargin_B, getBounds().left + getBounds().right - COMUtil.getPixel_W(2), yBasePos + _cvm.BMargin_B, nLeftLineCol, 1.0f);
            }

        }
        int[] whiteColor = {255, 255, 255};
        int nOuterCircleRadius = (int)COMUtil.getPixel(4);
        int nInnerCircleRadius = (int)COMUtil.getPixel(2);

        //원의 좌표 초기화
        if(m_arCirclePosition.size()>0)
        {
            m_arCirclePosition.clear();
        }

        //라인굵기
        _cvm.setLineWidth_Fix((int)COMUtil.getPixel(3));
        float sp = max_view;
        //라인 및 원 그리기
        int startPos = 0;
        float y = 0;
        float y1 = 0;

        //그라데이션 애니메이션
        float[] gradationPositions = null;
        //그라데이션 애니메이션 end

        int gab = (int)COMUtil.getPixel(8);

            for(int i = 0; i < positions.length; i += 4)
            {
                _cvm.drawLine(gl, positions[i], positions[i+1], positions[i+2], positions[i+3], upColor, 1.0f);
                _cvm.drawCircle(gl, (int)positions[i]-nOuterCircleRadius, (int)positions[i+1]-nOuterCircleRadius, positions[i]+nOuterCircleRadius, positions[i+1]+nOuterCircleRadius, true, upColor);

                //원의 좌표를 기록.
                try {
                    Hashtable<String, Integer> circlePositionData = new Hashtable<String, Integer>();
                    circlePositionData.put("x1", (int)positions[i]-nOuterCircleRadius);
                    circlePositionData.put("y1", (int)positions[i+1]-nOuterCircleRadius);
                    circlePositionData.put("x2", (int)positions[i]+nOuterCircleRadius);
                    circlePositionData.put("y2", (int)positions[i+1]+nOuterCircleRadius);
                    m_arCirclePosition.add(circlePositionData);
                } catch(Exception e) {

                }
                _cvm.drawCircle(gl, (int)positions[i]-nInnerCircleRadius, (int)positions[i+1]-nInnerCircleRadius, positions[i]+nInnerCircleRadius, positions[i+1]+nInnerCircleRadius, true, whiteColor);
            }
            _cvm.drawCircle(gl, (int)positions[positions.length-2]-nOuterCircleRadius, (int)positions[positions.length-1]-nOuterCircleRadius, positions[positions.length-2]+nOuterCircleRadius, positions[positions.length-1]+nOuterCircleRadius, true, upColor);
            _cvm.drawCircle(gl, (int)positions[positions.length-2]-nInnerCircleRadius, (int)positions[positions.length-1]-nInnerCircleRadius, positions[positions.length-2]+nInnerCircleRadius, positions[positions.length-1]+nInnerCircleRadius, true, whiteColor);

            Hashtable<String, Integer> circlePositionData = new Hashtable<String, Integer>();
            circlePositionData.put("x1", (int)positions[positions.length-2]-nOuterCircleRadius);
            circlePositionData.put("y1", (int)positions[positions.length-1]-nOuterCircleRadius);
            circlePositionData.put("x2", (int)positions[positions.length-2]+nOuterCircleRadius);
            circlePositionData.put("y2", (int)positions[positions.length-1]+nOuterCircleRadius);
            m_arCirclePosition.add(circlePositionData);


        if(_cvm.getAssetType() == ChartViewModel.ASSET_LINE_FILL)	//라인/오실레이터 타입일때만 라인아랫쪽을 색으로 채운다. 그냥 라인일때(Line)는 라인만표시
        {
            _cvm.drawLineWithFillGradient(gl, positions, this.max_view+ _cvm.BMargin_B, upColor, 25);
        }

        _cvm.setLineWidth(1);
    }
    //2015. 9. 15 자산관리 라인오실레이터차트 디자인 <<
    //2017.09.25 by LYH >> 자산 차트 적용 end
}