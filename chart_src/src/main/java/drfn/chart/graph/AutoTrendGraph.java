package drfn.chart.graph;

import android.graphics.Canvas;
import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;
import drfn.chart.scale.AREA;
import drfn.chart.util.COMUtil;
import drfn.chart.util.CoSys;

//자동추세선


/**
 * AutoTrendGraph  자동추세그래프
 */
public class AutoTrendGraph extends AbstractGraph{

    final int		AUTO_IH = 100;		// 주요고점
    final int		AUTO_IL	= 101;	// 주요저점
    final int		AUTO_UN_IH = 200;		// 주요고점
    final int		AUTO_UN_IL = 201;		// 주요저점
    double[] m_zData;
    double[] fZData;

    public AutoTrendGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
    }

    public void FormulateData(){
        //2019. 08. 21 by hyh - 자동추세선 복원
        int bDrawAutoTrend = _cvm.autoTrendWaveType;
        int bDrawAutoHigh = _cvm.autoTrendHighType;
        int bDrawAutoLow = _cvm.autoTrendLowType;
        int bDrawAutoW = _cvm.autoTrendWType;

        if(bDrawAutoTrend == 0 && bDrawAutoHigh == 0 && bDrawAutoLow == 0 && bDrawAutoW == 0) {
            return;
        }

        double[] NSHigh =_cdm.getSubPacketData("고가");

        double[] NSLow =_cdm.getSubPacketData("저가");


        if(NSHigh != null)
        {
            int m_dataCnt = NSHigh.length ;
            if(m_dataCnt <1)
                return;

            // Note : nFirst와 nLast의 위치가 바뀜
            int sIndex = 0;
            int eIndex = m_dataCnt;

            int nBefore = _cvm.preName;		// 전후 캔들 갯수
            int nAfter = _cvm.endName;		// 후 캔들 갯수

            int nFirst = eIndex - nBefore;
            int nLast = sIndex;

            if(nFirst <= nLast) return;


            // 시고저종 자료일자를 구한다 ---------------------------------------------->>

            if ( NSHigh == null || NSLow == null) return;

            //            CList<double,double> *clistHigh, *clistLow;
            //            clistHigh = clistLow = NULL;
            //
            //            clistHigh	= pIPacketHigh->GetDataList();
            //            clistLow	= pIPacketLow->GetDataList();

            int nDataNum = nFirst - nLast + 1;

            if (nDataNum <= 0)
            {
                return;
            }

            //            Data.ClearData();
            //            Data.SetSize(pIPacketHigh->GetDataCount());


            fZData = new double[m_dataCnt];

            //for (int i = eIndex - nBefore; i < eIndex; i++)
            for (int i = 0; i < eIndex; i++)
                fZData[i] = 0;

            double nOldTrend = 0.0;
            int nOldIndex = nFirst;
            //int nCount = 0;
            int k = 0;
            int i= 0;


            boolean bIH_Complete = true;
            boolean	bIL_Complete = true;

            double dDataHigh1, dDataHigh2, dDataLow1, dDataLow2;
            for	( i = nFirst, k = nFirst; i >= nLast ; i--, k-- )
            {
                int IH_Before = 0, IH_After = 0;
                int IL_Before = 0, IL_After = 0;
                if(i >= m_dataCnt)	continue;

                // 이전 봉갯수로 주요고점, 저점 찾기
                for (int j = 1; j <= nBefore; j++)	{
                    if(i+j >= m_dataCnt)	continue;
                    dDataHigh1 = NSHigh[i];
                    dDataHigh2 = NSHigh[i+j];
                    dDataLow1 = NSLow[i];
                    dDataLow2 = NSLow[i+j];

                    if ( j == 1 )
                    {
                        if (dDataHigh1 >= dDataHigh2)	IH_Before++;
                        if (dDataLow1 <= dDataLow2)	IL_Before++;
                    }
                    else
                    {
                        if (dDataHigh1 > dDataHigh2)	IH_Before++;
                        if (dDataLow1 < dDataLow2)	IL_Before++;
                    }
                }
                // 이후 봉갯수로 주요고점, 저점 찾기
                for (int j = 1; j <= nAfter; j++)	{
                    if (i - j < nLast)	{
                        if (IH_After == j-1)	{
                            IH_After = nAfter;		bIH_Complete = false;
                        }
                        if (IL_After == j-1)	{
                            IL_After = nAfter;		bIL_Complete = false;
                        }
                        break;
                    }
                    else
                    {
                        dDataHigh1 = NSHigh[i];
                        dDataHigh2 = NSHigh[i-j];
                        dDataLow1 = NSLow[i];
                        dDataLow2 = NSLow[i-j];
                        if (dDataHigh1 > dDataHigh2)	IH_After++;
                        if (dDataLow1 < dDataLow2)	IL_After++;
                    }
                }
                fZData[k] = 0;
                if (IH_Before == nBefore && IH_After == nAfter)	{
                    if (bIH_Complete == false)	fZData[k] = AUTO_UN_IH;
                    else						fZData[k] = AUTO_IH;
                    //nCount++;
                }
                if (IL_Before == nBefore && IL_After == nAfter)		{
                    if (fZData[k] != 0)	{
                        fZData[k] = 0;	//고점,저점 동시발생시 0 처리
                    }
                    else	{
                        if (bIL_Complete == false)	fZData[k] = AUTO_UN_IL;
                        else						fZData[k] = AUTO_IL;
                        //nCount++;
                    }
                }
                bIH_Complete = true;		bIL_Complete = true;
                // 주요고점, 주요저점이 동시에 발생하는 경우=>두점사이에 새로운 주요점 생성
                int nOldRem, nRem;
                if (nOldTrend > 0 && fZData[k] > 0)	{
                    nOldRem = ((int)nOldTrend % 2);
                    nRem = (int)((int)fZData[k] % 2);
                    if (nOldRem == nRem)	{
                        int nIndex = nOldIndex-1;
                        if (nIndex == i)	{	// 연이어 발생한 경우, 앞쪽 것은 무시하고, 뒷쪽 것만 인정한다.
                            fZData[nOldIndex] = 0;
                        }
                        else	{
                            double nHigh, nLow;
                            nHigh = NSHigh[nIndex];
                            nLow = NSLow[nIndex];

                            int nAutoType = 0;
                            for (int n = nIndex; n > i; n--)	{
                                dDataHigh1 = NSHigh[n];
                                dDataLow1 = NSLow[n];
                                if (fZData[k] == AUTO_IH || fZData[k] == AUTO_UN_IH)	{		// 새로운 저점 생성
                                    nAutoType = AUTO_IL;
                                    if (nLow >= dDataLow1)		{
                                        nLow = dDataLow1;
                                        nIndex = n;
                                    }
                                }
                                else if (fZData[k] == AUTO_IL || fZData[k] == AUTO_UN_IL)	{	// 새로운 고점 생성
                                    nAutoType = AUTO_IH;
                                    if (nHigh <= dDataHigh1)	{
                                        nHigh = dDataHigh1;
                                        nIndex = n;
                                    }
                                }
                            } // end-for
                            fZData[nIndex] = nAutoType;
                            //if (nAutoType > 0)	nCount++;
                        }
                    }
                }

                if (fZData[k] > 0)	{
                    nOldTrend = fZData[k];
                    nOldIndex = i;
                }
            }

            for(i=0; i<m_dataCnt; i++)
            {
                if(fZData[i] == AUTO_UN_IH)
                    fZData[i] = AUTO_IH;
                else if(fZData[i] == AUTO_UN_IL)
                    fZData[i] = AUTO_IL;
            }

            m_zData = fZData;
        }

        formulated = true;
    }


    public void drawGraph(Canvas gl, DrawTool dt){
        {
            int bDrawAutoTrend = _cvm.autoTrendWaveType;
            int bDrawAutoHigh = _cvm.autoTrendHighType;
            int bDrawAutoLow = _cvm.autoTrendLowType;
            int bDrawAutoW = _cvm.autoTrendWType;
            if(bDrawAutoW==1)
            {

                drawAutoTrendLine_3(gl, dt);
                return;
            }

            if(bDrawAutoTrend == 0 && bDrawAutoHigh == 0 && bDrawAutoLow == 0) {
                return;
            }
            FormulateData();
            if(m_zData == null) return;

            double[] dataf = m_zData;

            int startIndex = _cvm.getIndex();
            int startPos=startIndex;
            int dataLen = startPos + _cvm.getViewNum();
            //2023.07.03 by SJW - 자동추세선 crash 수정 >>
            if(dataLen > dataf.length)
                dataLen = dataf.length;
            //2023.07.03 by SJW - 자동추세선 crash 수정 <<
            float xPos= dt.getBounds().left+dt.xw;

            //int nX = 0;
            for(int k=startPos; k<dataLen; k++) {
                if(dataLen>k && (float)dataf[k] != 0) {
                    startPos=k;

                    xPos=(xPos+((k-startIndex) * dt.xfactor));
                    //nX++;
                    break;
                }
            }

            double[] NSHigh =_cdm.getSubPacketData("고가");

            double[] NSLow =_cdm.getSubPacketData("저가");

            if (NSHigh == null || NSLow == null) return; //2024.01.04 by sJW - 크래시로그 수정

            if(startPos >= NSHigh.length) return; //2023.07.03 by SJW - 자동추세선 crash 수정

            double drawData = (double)dataf[startPos];
            if(drawData == AUTO_IH)
                drawData = NSHigh[startPos];
            else if(drawData == AUTO_IL)
                drawData = NSLow[startPos];

            float y= dt.calcy(drawData);

            float xx = dt.getBounds().left + (int)dt.xw;




            _cvm.setLineWidth(2);
            if(bDrawAutoTrend > 0)
            {
                //gl.glLineWidth(2);
                int[] color = CoSys.CHART_COLORS[13];


//            glColor4f(components[0],components[1], components[2], 1.0);

                AREA area;  //2020.07.06 by LYH >> 캔들볼륨
                for(int k=startPos; k<dataLen; k++) {
                    float x1=xx+((k-startIndex)*dt.xfactor);
                    if(k >= NSHigh.length) return; //2023.07.03 by SJW - 자동추세선 crash 수정
                    //2020.07.06 by LYH >> 캔들볼륨 >>
                    area = _cvm.getArea(k-startIndex);
                    if(area != null)
                        x1 = area.getCenter();
                    //2020.07.06 by LYH >> 캔들볼륨 <<
                    //nX++;

                    if(k >= (dataLen-1)) {
                        break;
                    }

                    //2022.10.17 by lyk - kakaopay - ArrayIndexOutOfBoundsException 방어코드 추가 >>
                    if(k >= dataf.length) {
                        break;
                    }
                    //2022.10.17 by lyk - kakaopay - ArrayIndexOutOfBoundsException 방어코드 추가 <<

                    drawData = (float)dataf[k];
                    if(drawData == AUTO_IH)
                        drawData = NSHigh[k];
                    else if(drawData == AUTO_IL)
                        drawData = NSLow[k];
                    float y1= dt.calcy(drawData);

                    //2020.07.06 by LYH >> 캔들볼륨 >>
                    if(k==startIndex)
                    {
                        area = _cvm.getArea(k-startIndex);
                        if(area!=null) {
                            xPos = area.getCenter();
                            y = y1;
                        }
                    }
                    //2020.07.06 by LYH >> 캔들볼륨 <<
                    if((y<=dt.max_view) && (y1 <= dt.max_view) && dataf[k] != 0) {

                        _cvm.drawLine(gl, xPos, y, x1, y1, color, 1.5f);
                        y=y1;
                        xPos=x1;
                    }
                }
            }

            if(bDrawAutoHigh > 0 || bDrawAutoLow > 0)
            {
                //gl.glLineWidth(2);
                int[] highColor = CoSys.CHART_COLORS[0];
//            const CGFloat *components_high = [COMUtil getRgbOfUIColor:[highColor CGColor]];
                //2012. 7. 24  자동추세선 고점/저점 색상변경
                int[] lowColor = CoSys.CHART_COLORS[1];
//            const CGFloat *components_low = [COMUtil getRgbOfUIColor:[lowColor CGColor]];
                //2012. 7. 24  자동추세선 고점/저점 색상변경. drawLine 에서 int 형 RGB 배열 색상으로 그리므로  glColor4f 대신 색상을 선택하는 배열 만듬.
                int[] selColor;
                AREA area;  //2020.07.06 by LYH >> 캔들볼륨
                for(int k=startPos; k<dataLen; k++) {
                    float x1=xx+((k-startIndex)*dt.xfactor);
                    //2020.07.06 by LYH >> 캔들볼륨 >>
                    area = _cvm.getArea(k-startIndex);
                    if(area != null)
                        x1 = area.getCenter();
                    //2020.07.06 by LYH >> 캔들볼륨 <<

                    if(k >= (dataLen-1)) {
                        break;
                    }

                    drawData = (float)dataf[k];
                    if(bDrawAutoHigh > 0 && drawData == AUTO_IH)
                    {
//                    glColor4f(components_low[0],components_low[1], components_low[2], 1.0);
                        //2012. 7. 24  자동추세선 고점/저점 색상변경  그릴 색상 지정
                        selColor = lowColor;
                        drawData = NSHigh[k];
                    }
                    else if(bDrawAutoLow > 0 && drawData == AUTO_IL)
                    {
//                	glColor4f(components_high[0],components_high[1], components_high[2], 1.0);
                        //2012. 7. 24  자동추세선 고점/저점 색상변경  그릴 색상 지정
                        selColor = highColor;
                        drawData = NSLow[k];
                    }
                    else
                        continue;
                    float y1= dt.calcy(drawData);

                    //2020.07.06 by LYH >> 캔들볼륨 >>
                    if(k==startIndex)
                    {
                        area = _cvm.getArea(k-startIndex);
                        if(area != null) {
                            xPos = area.getCenter();
                            y = y1;
                        }
                    }
                    //2020.07.06 by LYH >> 캔들볼륨 <<

                    if((y<=dt.max_view) && (y1 <= dt.max_view) && dataf[k] != 0) {
//                    [COMUtil drawLine:x1 y1:y1 x2:[dt getBounds_rect].origin.x+[dt getBounds_rect].size.width y2:y1];
                        //2012. 7. 24  자동추세선 고점/저점 변경된 색상으로 그리기
                        _cvm.drawLine(gl,x1,y1,dt.getBounds().left + dt.getBounds().width(), y1, selColor,1.5f);
                    }
                }
            }
        }
        _cvm.setLineWidth(1);

    }

    public void reFormulateData() {
        FormulateData();
        formulated = true;
    }

    public void drawGraph(Canvas gl){
    }

    public void drawGraph_withSellPoint(Canvas g){
    }

    public String getName(){
        return "AutoTrend";
    }

    double[] m_autoData;
    public void drawAutoTrendLine_3(Canvas gl, DrawTool dt)
    {
//        FormulateData();
        int[] color = CoSys.CHART_COLORS[2];
        int startIndex = _cvm.getIndex();
        int startPos = startIndex;
        int dataLen = startPos + _cvm.getViewNum();
        int sIndex = startIndex;
        int eIndex = dataLen-1;
        int nFirst = sIndex;
        int nLast = eIndex;
        float xx = dt.getBounds().left + (int)dt.xw;
        _cvm.setLineWidth(1);
        if (nFirst >= nLast)
            return;
//            	if(Panel->ChartPeriod == PeriodTick && Panel->ChartCycle == 1)
//            		return;

        // 시고저종 자료일자를 구한다 ---------------------------------------------->>
        double[] highData = _cdm.getSubPacketData("고가");
        double[] lowData = _cdm.getSubPacketData("저가");

        if (highData == null || lowData == null)
            return;

        int nDrawNum = highData.length;

        if (nDrawNum<5) //2021.08.10 by HJW - W형 크래시 제거
            return;

        Calc(sIndex, eIndex);

        int n = 0;
        int nCount = 0;
        double maxValue, minValue;
        maxValue = highData[nFirst];
        minValue = lowData[nFirst];

        int maxIndex = nFirst;
        int minIndex = nFirst;

        int AUTO_Max = 0;
        int AUTO_Min = 0;

        boolean bFirst = true;

        int i, k;
        double dStartPosX=0, dEndPosX=0, dStartValueY=0, dEndValueY=0, dValueY=0;
        float fX1, fY1, fX2, fY2;
        AREA area;  //2020.07.06 by LYH >> 캔들볼륨
        for (i = nFirst, k = nFirst; i <= nLast; i++, k++) {
            if (m_autoData[k] == 0.0) continue;

            if (n >= nDrawNum) break;
            if (k >= m_autoData.length) break;
            nCount++;

            switch ((int) m_autoData[k]) {
                case AUTO_IH:
                case AUTO_UN_IH: {
                    dValueY = highData[i];
                    if (bFirst) {
                        dStartPosX = i;
                        dStartValueY = dValueY;
                    } else {
                        dEndPosX = i;
                        dEndValueY = dValueY;
                    }

                    if (maxValue < dValueY) {
                        maxValue = dValueY;
                        maxIndex = i;
                        AUTO_Max = k;
                    }
                    break;
                }
                case AUTO_IL:
                case AUTO_UN_IL: {
                    dValueY = lowData[i];
                    if (bFirst) {
                        dStartPosX = i;
                        dStartValueY = dValueY;
                    } else {
                        dEndPosX = i;
                        dEndValueY = dValueY;
                    }

                    if (minValue > dValueY) {
                        minValue = dValueY;
                        minIndex = i;
                        AUTO_Min = k;
                    }
                    break;
                }
            }

            if (bFirst) {
                bFirst = false;
            } else {
                fX1 = (float) (xx + (dStartPosX - startIndex) * dt.xfactor);
                fY1 = dt.calcy(dStartValueY);
                fX2 = (float) (xx + ((dEndPosX - startIndex) * dt.xfactor));
                fY2 = dt.calcy(dEndValueY);

                //2020.07.06 by LYH >> 캔들볼륨 >>
                area = _cvm.getArea((int)(dStartPosX-startIndex));
                if(area != null)
                    fX1 = area.getCenter();
                area = _cvm.getArea((int)(dEndPosX-startIndex));
                if(area != null)
                    fX2 = area.getCenter();
                //2020.07.06 by LYH >> 캔들볼륨 <<
                _cvm.drawLine(gl, fX1, fY1, fX2, fY2, color, 1.0f);
//                nX1 = GetXPositoin( ( int)dStartPosX);
//                nY1 = m_pIChartOCX->ConvertDataToYPosition( dStartValueY, m_dViewMax, m_dViewMin,
//                                                           rctGraphRegion.top, rctGraphRegion.bottom, m_bLog, m_bReverse);
//                nX2 = GetXPositoin( ( int)dEndPosX);
//                nY2 = m_pIChartOCX->ConvertDataToYPosition( dEndValueY, m_dViewMax, m_dViewMin,
//                                                           rctGraphRegion.top, rctGraphRegion.bottom, m_bLog, m_bReverse);
//
//
//                CPen pnSteady( m_PatternEnvData.m_lineDataWType.m_nStyle - 1, m_PatternEnvData.m_lineDataWType.m_nWeight,
//                              m_PatternEnvData.m_lineDataWType.m_color);
//                CPen *ppnOld = p_pDC->SelectObject(&pnSteady);
//
//                p_pDC->MoveTo( nX1, nY1);
//                p_pDC->LineTo( nX2, nY2);
//
//                p_pDC->SelectObject(ppnOld);
//
                dStartPosX = dEndPosX;
                dStartValueY = dEndValueY;
            }
        }

        //양쪽 기준선
//        CPen pnSteady( PS_DASHDOTDOT, m_PatternEnvData.m_lineDataWType.m_nWeight,
//                      m_PatternEnvData.m_lineDataWType.m_color );
//        CPen *ppnOld = p_pDC->SelectObject(&pnSteady);
//
        dStartPosX = minIndex;
        dEndPosX = minIndex;

        fX1 = (float) (xx + (dStartPosX - startIndex) * dt.xfactor);
        fX2 = (float) (xx + (dEndPosX - startIndex) * dt.xfactor);
        //2020.07.06 by LYH >> 캔들볼륨 >>
        area = _cvm.getArea((int)(dStartPosX-startIndex));
        if(area!=null)
            fX1 = area.getCenter();
        area = _cvm.getArea((int)(dEndPosX-startIndex));
        if(area != null)
            fX2 = area.getCenter();
        //2020.07.06 by LYH >> 캔들볼륨 <<

        _cvm.drawDashDotDotLine(gl, fX1, dt.min_view-COMUtil.getPixel(20), fX2, dt.max_view+COMUtil.getPixel(20), color, 1.0f);
//        nX1 = GetXPositoin( ( int)dStartPosX);
//        nX2 = GetXPositoin( ( int)dEndPosX);
//
//        p_pDC->MoveTo( nX1, rctGraphRegion.top-20);
//        p_pDC->LineTo( nX2, rctGraphRegion.bottom+20);
//
        dStartPosX = maxIndex;
        dEndPosX = maxIndex;

        fX1 = (float) (xx + (dStartPosX - startIndex) * dt.xfactor);
        fX2 = (float) (xx + (dEndPosX - startIndex) * dt.xfactor);
        //2020.07.06 by LYH >> 캔들볼륨 >>
        area = _cvm.getArea((int)(dStartPosX-startIndex));
        if(area!=null)
            fX1 = area.getCenter();
        area = _cvm.getArea((int)(dEndPosX-startIndex));
        if(area != null)
            fX2 = area.getCenter();
        //2020.07.06 by LYH >> 캔들볼륨 <<

        _cvm.drawDashDotDotLine(gl, fX1, dt.min_view, fX2, dt.max_view, color, 1.0f);
//        nX1 = GetXPositoin( ( int)dStartPosX);
//        nX2 = GetXPositoin( ( int)dEndPosX);
//
//        p_pDC->MoveTo( nX1, rctBlockRegion.top);
//        p_pDC->LineTo( nX2, rctBlockRegion.bottom);
//
//        p_pDC->SelectObject(ppnOld);

        // minIndex와 maxIndex의 최고점 혹은 최저점을 찾아서 그려준다.
        // 2구간
        double maxValue1 = minValue, minValue1 = maxValue;
        int maxIndex1 = maxIndex, minIndex1 = minIndex;
        double maxValue2 = minValue, minValue2 = maxValue;
        int maxIndex2 = maxIndex, minIndex2 = minIndex;
        double maxValue3 = minValue, minValue3 = maxValue;
        int maxIndex3 = maxIndex, minIndex3 = minIndex;

//        CPoint ecursor, scursor, e1, s1, e3, s3;
        double H_inc = -999, L_inc = -999;
        int Gugan = 0;

        double ratio = 0.0;
        double ratio1 = 0.0;
        double ratio2 = 0.0;

        if (maxIndex > minIndex) {
            for (i = nFirst, k = nFirst; i <= nLast; i++, k++) {
                if (i < minIndex + 1)        // 3구간
                {
                    Gugan = 3;
                } else if (i == minIndex + 1)    // 2구간
                {
                    Gugan = 2;
                    H_inc = -999;
                    L_inc = -999;
                } else if (i == maxIndex + 1)    // 1구간
                {
                    Gugan = 1;
                    H_inc = -999;
                    L_inc = -999;
                }

                if (m_autoData[k] == 0) continue;

                if (Gugan == 3 && (m_autoData[k] == AUTO_UN_IH || m_autoData[k] == AUTO_UN_IL))
                    continue;

                if (i == minIndex || i == maxIndex) continue;

                switch ((int) m_autoData[k]) {
                    case AUTO_IH: {
                        if (Gugan == 3) break;

                        double imsiVal;
                        imsiVal = highData[i];
                        int imsiIdx = i;

                        ratio = 0.0;
                        if (maxIndex != imsiIdx)
                            ratio = (maxValue - imsiVal) / (maxIndex - imsiIdx);

                        switch (Gugan) {
                            case 1: {
                                if (H_inc == -999 || H_inc <= ratio) {
                                    H_inc = ratio;
                                    maxValue1 = imsiVal;
                                    maxIndex1 = imsiIdx;
                                }
                            }
                            break;
                            case 2: {
                                if (H_inc == -999 || H_inc >= ratio) {
                                    H_inc = ratio;
                                    maxValue2 = imsiVal;
                                    maxIndex2 = imsiIdx;
                                }
                            }
                            break;
                        }
                        break;
                    }
                    case AUTO_IL: {
                        if (Gugan == 1) break;

                        double imsiVal;
                        imsiVal = lowData[i];

                        int imsiIdx = i;

                        ratio = 0.0;
                        if (minIndex != imsiIdx)
                            ratio = (imsiVal - minValue) / (imsiIdx - minIndex);

                        switch (Gugan) {
                            case 2: {
                                if (L_inc == -999 || L_inc >= ratio) {
                                    L_inc = ratio;
                                    minValue2 = imsiVal;
                                    minIndex2 = imsiIdx;
                                }
                            }
                            break;
                            case 3: {
                                if (L_inc == -999 || L_inc <= ratio) {
                                    L_inc = ratio;
                                    minValue3 = imsiVal;
                                    minIndex3 = imsiIdx;
                                }
                            }
                            break;
                        }
                        break;
                    }
                }    // switch-end
            }

            //Point imsicur;

            ratio1 = 0.0;
            ratio2 = 0.0;
            if (maxIndex != maxIndex2)
                ratio1 = (maxValue2 - maxValue) / (maxIndex2 - maxIndex);

            if (maxIndex != minIndex)
                ratio2 = (minValue - maxValue) / (minIndex - maxIndex);

            if (ratio1 > ratio2) {
                maxValue2 = minValue;
                maxIndex2 = minIndex;
            }

            ratio1 = 0.0;
            ratio2 = 0.0;
            if (minIndex != maxIndex2)
                ratio1 = (minValue2 - minValue) / (minIndex2 - minIndex);

            if (maxIndex != minIndex)
                ratio2 = (maxValue - minValue) / (maxIndex - minIndex);

            if (ratio1 > ratio2) {
                minValue2 = maxValue;
                minIndex2 = maxIndex;
            }

            dStartPosX = maxIndex;
            dStartValueY = maxValue;
            dEndPosX = maxIndex1;
            dEndValueY = maxValue1;

            fX1 = (float) (xx + (dStartPosX - startIndex) * dt.xfactor);
            fY1 = dt.calcy(dStartValueY);
            fX2 = (float) (xx + (dEndPosX - startIndex) * dt.xfactor);
            fY2 = dt.calcy(dEndValueY);

            //2020.07.06 by LYH >> 캔들볼륨 >>
            area = _cvm.getArea((int)(dStartPosX-startIndex));
            if(area != null)
                fX1 = area.getCenter();
            area = _cvm.getArea((int)(dEndPosX-startIndex));
            if(area != null)
                fX2 = area.getCenter();
            //2020.07.06 by LYH >> 캔들볼륨 <<

            _cvm.drawDashDotDotLine(gl, fX1, fY1, fX2, fY2, color, 1.0f);

//            nX1 = GetXPositoin( ( int)dStartPosX);
//            nY1 = m_pIChartOCX->ConvertDataToYPosition( dStartValueY, m_dViewMax, m_dViewMin,
//                                                       rctGraphRegion.top, rctGraphRegion.bottom, m_bLog, m_bReverse);
//            nX2 = GetXPositoin( ( int)dEndPosX);
//            nY2 = m_pIChartOCX->ConvertDataToYPosition( dEndValueY, m_dViewMax, m_dViewMin,
//                                                       rctGraphRegion.top, rctGraphRegion.bottom, m_bLog, m_bReverse);
//
//
//            CPen pnSteady( PS_DASHDOTDOT, m_PatternEnvData.m_lineDataWType.m_nWeight,
//                          m_PatternEnvData.m_lineDataWType.m_color );
//            CPen *ppnOld = p_pDC->SelectObject(&pnSteady);
//
//            p_pDC->MoveTo( nX1, nY1);
//            p_pDC->LineTo( nX2, nY2);


            dStartPosX = minIndex;
            dStartValueY = minValue;
            dEndPosX = minIndex3;
            dEndValueY = minValue3;

            fX1 = (float) (xx + (dStartPosX - startIndex) * dt.xfactor);
            fY1 = dt.calcy(dStartValueY);
            fX2 = (float) (xx + (dEndPosX - startIndex) * dt.xfactor);
            fY2 = dt.calcy(dEndValueY);

            //2020.07.06 by LYH >> 캔들볼륨 >>
            area = _cvm.getArea((int)(dStartPosX-startIndex));
            if(area != null)
                fX1 = area.getCenter();
            area = _cvm.getArea((int)(dEndPosX-startIndex));
            if(area != null)
                fX2 = area.getCenter();
            //2020.07.06 by LYH >> 캔들볼륨 <<

            _cvm.drawDashDotDotLine(gl, fX1, fY1, fX2, fY2, color, 1.0f);

//            nX1 = GetXPositoin( ( int)dStartPosX);
//            nY1 = m_pIChartOCX->ConvertDataToYPosition( dStartValueY, m_dViewMax, m_dViewMin,
//                                                       rctGraphRegion.top, rctGraphRegion.bottom, m_bLog, m_bReverse);
//            nX2 = GetXPositoin( ( int)dEndPosX);
//            nY2 = m_pIChartOCX->ConvertDataToYPosition( dEndValueY, m_dViewMax, m_dViewMin,
//                                                       rctGraphRegion.top, rctGraphRegion.bottom, m_bLog, m_bReverse);
//
//
//            p_pDC->MoveTo( nX1, nY1);
//            p_pDC->LineTo( nX2, nY2);
//
//            p_pDC->SelectObject(ppnOld);
        } else {
            for (i = nFirst, k = nFirst; i <= nLast; i++, k++) {
                if (i < maxIndex + 1) {
                    Gugan = 3;
                } else if (i == maxIndex + 1) {
                    Gugan = 2;
                    H_inc = -999;
                    L_inc = -999;
                } else if (i == minIndex + 1) {
                    Gugan = 1;
                    H_inc = -999;
                    L_inc = -999;
                }

                if (m_autoData[k] == 0) continue;

                if (Gugan == 3 && (m_autoData[k] == AUTO_UN_IH || m_autoData[k] == AUTO_UN_IL))
                    continue;

                if (i == minIndex || i == maxIndex) continue;

                switch ((int) m_autoData[k]) {
                    case AUTO_IH: {
                        if (Gugan == 1) break;
                        double imsiVal;
                        imsiVal = highData[i];
                        int imsiIdx = i;

                        ratio = 0.0;
                        if (maxIndex != imsiIdx)
                            ratio = (maxValue - imsiVal) / (maxIndex - imsiIdx);


                        switch (Gugan) {
                            case 2: {
                                if (H_inc == -999 || H_inc <= ratio) {
                                    H_inc = ratio;
                                    maxValue2 = imsiVal;
                                    maxIndex2 = imsiIdx;
                                }
                            }
                            break;
                            case 3: {
                                if (H_inc == -999 || H_inc >= ratio) {
                                    H_inc = ratio;
                                    maxValue3 = imsiVal;
                                    maxIndex3 = imsiIdx;
                                }
                            }
                            break;
                        }
                        break;
                    }
                    case AUTO_IL: {
                        if (Gugan == 3) break;
                        double imsiVal;
                        imsiVal = lowData[i];
                        int imsiIdx = i;

                        ratio = 0.0;
                        if (minIndex != imsiIdx)
                            ratio = (imsiVal - minValue) / (imsiIdx - minIndex);

                        switch (Gugan) {
                            case 1: {
                                if (L_inc == -999 || L_inc >= ratio) {
                                    L_inc = ratio;
                                    minValue1 = imsiVal;
                                    minIndex1 = imsiIdx;
                                }
                            }
                            break;
                            case 2: {
                                if (L_inc == -999 || L_inc <= ratio) {
                                    L_inc = ratio;
                                    minValue2 = imsiVal;
                                    minIndex2 = imsiIdx;
                                }
                            }
                            break;
                        }
                        break;
                    }
                }    // switch-end
            }

            //CPoint imsicur;

            ratio1 = 0.0;
            ratio2 = 0.0;
            if (minIndex != maxIndex2)
                ratio1 = (maxValue2 - maxValue) / (maxIndex2 - maxIndex);

            if (maxIndex != minIndex)
                ratio2 = (minValue - maxValue) / (minIndex - maxIndex);

            if (ratio1 < ratio2) {
                maxValue2 = minValue;
                maxIndex2 = minIndex;
            }

            ratio1 = 0.0;
            ratio2 = 0.0;
            if (minIndex != maxIndex2)
                ratio1 = (minValue2 - minValue) / (minIndex2 - minIndex);

            if (maxIndex != minIndex)
                ratio2 = (maxValue - minValue) / (maxIndex - minIndex);

            if (ratio1 < ratio2) {
                minValue2 = maxValue;
                minIndex2 = maxIndex;
            }

            dStartPosX = minIndex;
            dStartValueY = minValue;
            dEndPosX = minIndex1;
            dEndValueY = minValue1;

            fX1 = (float) (xx + (dStartPosX - startIndex) * dt.xfactor);
            fY1 = dt.calcy(dStartValueY);
            fX2 = (float) (xx + (dEndPosX - startIndex) * dt.xfactor);
            fY2 = dt.calcy(dEndValueY);

            //2020.07.06 by LYH >> 캔들볼륨 >>
            area = _cvm.getArea((int)(dStartPosX-startIndex));
            if(area != null)
                fX1 = area.getCenter();
            area = _cvm.getArea((int)(dEndPosX-startIndex));
            if(area != null)
                fX2 = area.getCenter();
            //2020.07.06 by LYH >> 캔들볼륨 <<

            _cvm.drawDashDotDotLine(gl, fX1, fY1, fX2, fY2, color, 1.0f);

//            nX1 = GetXPositoin( ( int)dStartPosX);
//            nY1 = m_pIChartOCX->ConvertDataToYPosition( dStartValueY, m_dViewMax, m_dViewMin,
//                                                       rctGraphRegion.top, rctGraphRegion.bottom, m_bLog, m_bReverse);
//            nX2 = GetXPositoin( ( int)dEndPosX);
//            nY2 = m_pIChartOCX->ConvertDataToYPosition( dEndValueY, m_dViewMax, m_dViewMin,
//                                                       rctGraphRegion.top, rctGraphRegion.bottom, m_bLog, m_bReverse);
//
//
//            //기준선 좌측
//            CPen pnSteady( PS_DASHDOTDOT, m_PatternEnvData.m_lineDataWType.m_nWeight,
//                          m_PatternEnvData.m_lineDataWType.m_color );
//            CPen *ppnOld = p_pDC->SelectObject(&pnSteady);
//
//            p_pDC->MoveTo( nX1, nY1);
//            p_pDC->LineTo( nX2, nY2);

            dStartPosX = maxIndex;
            dStartValueY = maxValue;
            dEndPosX = maxIndex3;
            dEndValueY = maxValue3;

            fX1 = (float) (xx+(dStartPosX-startIndex)*dt.xfactor);
            fY1 = dt.calcy(dStartValueY);
            fX2 = (float) (xx+(dEndPosX-startIndex)*dt.xfactor);
            fY2 = dt.calcy(dEndValueY);
            //2020.07.06 by LYH >> 캔들볼륨 >>
            area = _cvm.getArea((int)(dStartPosX-startIndex));
            if(area != null)
                fX1 = area.getCenter();
            area = _cvm.getArea((int)(dEndPosX-startIndex));
            if(area != null)
                fX2 = area.getCenter();
            //2020.07.06 by LYH >> 캔들볼륨 <<

            _cvm.drawDashDotDotLine(gl, fX1, fY1, fX2, fY2, color, 1.0f);

//            nX1 = GetXPositoin( ( int)dStartPosX);
//            nY1 = m_pIChartOCX->ConvertDataToYPosition( dStartValueY, m_dViewMax, m_dViewMin,
//                                                       rctGraphRegion.top, rctGraphRegion.bottom, m_bLog, m_bReverse);
//            nX2 = GetXPositoin( ( int)dEndPosX);
//            nY2 = m_pIChartOCX->ConvertDataToYPosition( dEndValueY, m_dViewMax, m_dViewMin,
//                                                       rctGraphRegion.top, rctGraphRegion.bottom, m_bLog, m_bReverse);
//
//            p_pDC->MoveTo( nX1, nY1);
//            p_pDC->LineTo( nX2, nY2);
//
//            p_pDC->SelectObject(ppnOld);

        }

        // 최고점 저항선
        if (maxIndex != maxIndex2) {
            dStartPosX = maxIndex;
            dStartValueY = maxValue;
            // 변환
            dEndPosX = minIndex;
            dEndValueY = maxValue - (maxIndex - minIndex) * (maxValue - maxValue2) / (maxIndex - maxIndex2);

            if (dStartPosX != dEndPosX) {

                fX1 = (float) (xx+(dStartPosX-startIndex)*dt.xfactor);
                fY1 = dt.calcy(dStartValueY);
                fX2 = (float) (xx+(dEndPosX-startIndex)*dt.xfactor);
                fY2 = dt.calcy(dEndValueY);
                //2020.07.06 by LYH >> 캔들볼륨 >>
                area = _cvm.getArea((int)(dStartPosX-startIndex));
                if(area != null)
                    fX1 = area.getCenter();
                area = _cvm.getArea((int)(dEndPosX-startIndex));
                if(area != null)
                    fX2 = area.getCenter();
                //2020.07.06 by LYH >> 캔들볼륨 <<

                _cvm.drawLine(gl, fX1, fY1, fX2, fY2, color, 1.0f);
//                nX1 = GetXPositoin( ( int)dStartPosX);
//                nY1 = m_pIChartOCX->ConvertDataToYPosition( dStartValueY, m_dViewMax, m_dViewMin,
//                                                           rctGraphRegion.top, rctGraphRegion.bottom, m_bLog, m_bReverse);
//                nX2 = GetXPositoin( ( int)dEndPosX);
//                nY2 = m_pIChartOCX->ConvertDataToYPosition( dEndValueY, m_dViewMax, m_dViewMin,
//                                                           rctGraphRegion.top, rctGraphRegion.bottom, m_bLog, m_bReverse);
//
//                //최고점 저항선
//                CPen pnSteady( m_PatternEnvData.m_lineDataWType.m_nStyle - 1, m_PatternEnvData.m_lineDataWType.m_nWeight,
//                              m_PatternEnvData.m_lineDataWType.m_color );
//                CPen *ppnOld = p_pDC->SelectObject(&pnSteady);
//
//                p_pDC->MoveTo( nX1, nY1);
//                p_pDC->LineTo( nX2, nY2);
//
//                p_pDC->SelectObject(ppnOld);
            }
        }

        // 최저점 지지선
        if (minIndex != minIndex2) {
            dStartPosX = minIndex;
            dStartValueY = minValue;
            dEndPosX = maxIndex;
            dEndValueY = minValue + (maxIndex - minIndex) * (minValue - minValue2) / (minIndex - minIndex2);

            if (dStartPosX != dEndPosX) {

                fX1 = (float) (xx+(dStartPosX-startIndex)*dt.xfactor);
                fY1 = dt.calcy(dStartValueY);
                fX2 = (float) (xx+(dEndPosX-startIndex)*dt.xfactor);
                fY2 = dt.calcy(dEndValueY);
                //2020.07.06 by LYH >> 캔들볼륨 >>
                area = _cvm.getArea((int)(dStartPosX-startIndex));
                if(area != null)
                    fX1 = area.getCenter();
                area = _cvm.getArea((int)(dEndPosX-startIndex));
                if(area != null)
                    fX2 = area.getCenter();
                //2020.07.06 by LYH >> 캔들볼륨 <<

                _cvm.drawLine(gl, fX1, fY1, fX2, fY2, color, 1.0f);
//                nX1 = GetXPositoin( ( int)dStartPosX);
//                nY1 = m_pIChartOCX->ConvertDataToYPosition( dStartValueY, m_dViewMax, m_dViewMin,
//                                                           rctGraphRegion.top, rctGraphRegion.bottom, m_bLog, m_bReverse);
//                nX2 = GetXPositoin( ( int)dEndPosX);
//                nY2 = m_pIChartOCX->ConvertDataToYPosition( dEndValueY, m_dViewMax, m_dViewMin,
//                                                           rctGraphRegion.top, rctGraphRegion.bottom, m_bLog, m_bReverse);
//
//                //최저점 지지선
//                CPen pnSteady( m_PatternEnvData.m_lineDataWType.m_nStyle - 1, m_PatternEnvData.m_lineDataWType.m_nWeight,
//                              m_PatternEnvData.m_lineDataWType.m_color );
//                CPen *ppnOld = p_pDC->SelectObject(&pnSteady);
//
//                p_pDC->MoveTo( nX1, nY1);
//                p_pDC->LineTo( nX2, nY2);
//
//                p_pDC->SelectObject(ppnOld);
            }
        }

        if (maxIndex > minIndex) {
            // 기준선 왼쪽
            if (minIndex != minIndex3) {
                dStartPosX = minIndex;
                dStartValueY = minValue;
                dEndPosX = sIndex;
                dEndValueY = minValue - (minIndex - sIndex) * (minValue - minValue3) / (minIndex - minIndex3);


                fX1 = (float) (xx+(dStartPosX-startIndex)*dt.xfactor);
                fY1 = dt.calcy(dStartValueY);
                fX2 = (float) (xx+(dEndPosX-startIndex)*dt.xfactor);
                fY2 = dt.calcy(dEndValueY);
                //2020.07.06 by LYH >> 캔들볼륨 >>
                area = _cvm.getArea((int)(dStartPosX-startIndex));
                if(area != null)
                    fX1 = area.getCenter();
                area = _cvm.getArea((int)(dEndPosX-startIndex));
                if(area != null)
                    fX2 = area.getCenter();
                //2020.07.06 by LYH >> 캔들볼륨 <<

                _cvm.drawLine(gl, fX1, fY1, fX2, fY2, color, 1.0f);
//                nX1 = GetXPositoin( ( int)dStartPosX);
//                nY1 = m_pIChartOCX->ConvertDataToYPosition( dStartValueY, m_dViewMax, m_dViewMin,
//                                                           rctGraphRegion.top, rctGraphRegion.bottom, m_bLog, m_bReverse);
//                nX2 = GetXPositoin( ( int)dEndPosX);
//                nY2 = m_pIChartOCX->ConvertDataToYPosition( dEndValueY, m_dViewMax, m_dViewMin,
//                                                           rctGraphRegion.top, rctGraphRegion.bottom, m_bLog, m_bReverse);
//
//                // 기준선 왼쪽 1
//                CPen pnSteady( m_PatternEnvData.m_lineDataWType.m_nStyle - 1, m_PatternEnvData.m_lineDataWType.m_nWeight, m_PatternEnvData.m_lineDataWType.m_color);
//                CPen *ppnOld = p_pDC->SelectObject(&pnSteady);
//
//                p_pDC->MoveTo( nX1, nY1);
//                p_pDC->LineTo( nX2, nY2);
//
//                p_pDC->SelectObject(ppnOld);
            }

            if (maxIndex != maxIndex1) {
                dStartPosX = maxIndex;
                dStartValueY = maxValue;
                dEndPosX = eIndex;
                dEndValueY = maxValue + (eIndex - maxIndex) * (maxValue - maxValue1) / (maxIndex - maxIndex1);


                fX1 = (float) (xx+(dStartPosX-startIndex)*dt.xfactor);
                fY1 = dt.calcy(dStartValueY);
                fX2 = (float) (xx+(dEndPosX-startIndex)*dt.xfactor);
                fY2 = dt.calcy(dEndValueY);
                //2020.07.06 by LYH >> 캔들볼륨 >>
                area = _cvm.getArea((int)(dStartPosX-startIndex));
                if(area != null)
                    fX1 = area.getCenter();
                area = _cvm.getArea((int)(dEndPosX-startIndex));
                if(area != null)
                    fX2 = area.getCenter();
                //2020.07.06 by LYH >> 캔들볼륨 <<

                _cvm.drawDashDotDotLine(gl, fX1, fY1, fX2, fY2, color, 1.0f);
//                nX1 = GetXPositoin( ( int)dStartPosX);
//                nY1 = m_pIChartOCX->ConvertDataToYPosition( dStartValueY, m_dViewMax, m_dViewMin,
//                                                           rctGraphRegion.top, rctGraphRegion.bottom, m_bLog, m_bReverse);
//                nX2 = GetXPositoin( ( int)dEndPosX);
//                nY2 = m_pIChartOCX->ConvertDataToYPosition( dEndValueY, m_dViewMax, m_dViewMin,
//                                                           rctGraphRegion.top, rctGraphRegion.bottom, m_bLog, m_bReverse);
//
//                //기준선 오른쪽
//                CPen pnSteady( PS_DASHDOTDOT, m_PatternEnvData.m_lineDataWType.m_nWeight, m_PatternEnvData.m_lineDataWType.m_color );
//                CPen *ppnOld = p_pDC->SelectObject(&pnSteady);
//
//                p_pDC->MoveTo( nX1, nY1);
//                p_pDC->LineTo( nX2, nY2);
//
//                p_pDC->SelectObject(ppnOld);
            }
        } else {
            if (maxIndex != maxIndex3) {
                dStartPosX = maxIndex;
                dStartValueY = maxValue;
                dEndPosX = sIndex;
                dEndValueY = maxValue - (maxIndex - sIndex) * (maxValue - maxValue3) / (maxIndex - maxIndex3);


                fX1 = (float) (xx+(dStartPosX-startIndex)*dt.xfactor);
                fY1 = dt.calcy(dStartValueY);
                fX2 = (float) (xx+(dEndPosX-startIndex)*dt.xfactor);
                fY2 = dt.calcy(dEndValueY);
                //2020.07.06 by LYH >> 캔들볼륨 >>
                area = _cvm.getArea((int)(dStartPosX-startIndex));
                if(area != null)
                    fX1 = area.getCenter();
                area = _cvm.getArea((int)(dEndPosX-startIndex));
                if(area != null)
                    fX2 = area.getCenter();
                //2020.07.06 by LYH >> 캔들볼륨 <<

                _cvm.drawLine(gl, fX1, fY1, fX2, fY2, color, 1.0f);
//                nX1 = GetXPositoin( ( int)dStartPosX);
//                nY1 = m_pIChartOCX->ConvertDataToYPosition( dStartValueY, m_dViewMax, m_dViewMin,
//                                                           rctGraphRegion.top, rctGraphRegion.bottom, m_bLog, m_bReverse);
//                nX2 = GetXPositoin( ( int)dEndPosX);
//                nY2 = m_pIChartOCX->ConvertDataToYPosition( dEndValueY, m_dViewMax, m_dViewMin,
//                                                           rctGraphRegion.top, rctGraphRegion.bottom, m_bLog, m_bReverse);
//
//                // 기준선 왼쪽 2
//                CPen pnSteady( m_PatternEnvData.m_lineDataWType.m_nStyle - 1, m_PatternEnvData.m_lineDataWType.m_nWeight, m_PatternEnvData.m_lineDataWType.m_color );
//                CPen *ppnOld = p_pDC->SelectObject(&pnSteady);
//
//                p_pDC->MoveTo( nX1, nY1);
//                p_pDC->LineTo( nX2, nY2);
//
//                p_pDC->SelectObject(ppnOld);
            }

            if (minIndex != minIndex1) {
                dStartPosX = minIndex;
                dStartValueY = minValue;
                dEndPosX = eIndex;
                dEndValueY = minValue + (eIndex - minIndex) * (minValue - minValue1) / (minIndex - minIndex1);


                fX1 = (float) (xx+(dStartPosX-startIndex)*dt.xfactor);
                fY1 = dt.calcy(dStartValueY);
                fX2 = (float) (xx+(dEndPosX-startIndex)*dt.xfactor);
                fY2 = dt.calcy(dEndValueY);
                //2020.07.06 by LYH >> 캔들볼륨 >>
                area = _cvm.getArea((int)(dStartPosX-startIndex));
                if(area != null)
                    fX1 = area.getCenter();
                area = _cvm.getArea((int)(dEndPosX-startIndex));
                if(area != null)
                    fX2 = area.getCenter();
                //2020.07.06 by LYH >> 캔들볼륨 <<

                _cvm.drawDashDotDotLine(gl, fX1, fY1, fX2, fY2, color, 1.0f);
//                nX1 = GetXPositoin( ( int)dStartPosX);
//                nY1 = m_pIChartOCX->ConvertDataToYPosition( dStartValueY, m_dViewMax, m_dViewMin,
//                                                           rctGraphRegion.top, rctGraphRegion.bottom, m_bLog, m_bReverse);
//                nX2 = GetXPositoin( ( int)dEndPosX);
//                nY2 = m_pIChartOCX->ConvertDataToYPosition( dEndValueY, m_dViewMax, m_dViewMin,
//                                                           rctGraphRegion.top, rctGraphRegion.bottom, m_bLog, m_bReverse);
//
//                //기준선 오른쪽
//                CPen pnSteady( PS_DASHDOTDOT, m_PatternEnvData.m_lineDataWType.m_nWeight, m_PatternEnvData.m_lineDataWType.m_color );
//                CPen *ppnOld = p_pDC->SelectObject(&pnSteady);
//
//                p_pDC->MoveTo( nX1, nY1);
//                p_pDC->LineTo( nX2, nY2);
//
//                p_pDC->SelectObject(ppnOld);
            }
        }
    }

    void Calc(int nStartIndex, int nEndIndex)
    {
        // Note : nFirst와 nLast의 위치가 바뀜
        double[] highData =_cdm.getSubPacketData("고가");
        double[] lowData =_cdm.getSubPacketData("저가");

        if(highData == null || lowData == null)
            return;

        int m_dataCnt = highData.length ;
        if(m_dataCnt <1)
            return;

        // Note : nFirst와 nLast의 위치가 바뀜
        int sIndex = nStartIndex;
        int eIndex = nEndIndex;

        int nBefore = _cvm.preName;		// 전후 캔들 갯수
        int nAfter = _cvm.endName;		// 후 캔들 갯수

        int nFirst = eIndex - nBefore;
        int nLast = sIndex;

        m_autoData = new double[m_dataCnt];

        int nDataNum = nFirst - nLast + 1;
        if (nDataNum <= 0)	return;


        int i;
        for (i = eIndex - nBefore; i <= eIndex; i++)
            m_autoData[i] = 0.0;

        double nOldTrend = 0.0;
        int nOldIndex = nFirst;
        int nCount = 0;
        int k = 0;

        boolean bIH_Complete = true, bIL_Complete = true;

        double dDataHigh1, dDataHigh2, dDataLow1, dDataLow2;
        for	( i = nFirst, k = nFirst; i >= nLast ; i--, k-- )
        {
            int IH_Before = 0, IH_After = 0;
            int IL_Before = 0, IL_After = 0;
            if(i >= m_autoData.length)	continue;

            // 이전 봉갯수로 주요고점, 저점 찾기
            // 6->10 -hidden-
            //		for (int j = 1; j <= nBefore; j++)	{
            int j;
            for (j = 1; j <= nBefore; j++)	{
                if(i+j >= m_autoData.length)	continue;
                dDataHigh1 = highData[i];
                dDataHigh2 = highData[i+j];
                dDataLow1 = lowData[i];
                dDataLow2 = lowData[i+j];

                if ( j == 1 )
                {
                    if (dDataHigh1 >= dDataHigh2)	IH_Before++;
                    if (dDataLow1 <= dDataLow2)	IL_Before++;
                }
                else
                {
                    if (dDataHigh1 > dDataHigh2)	IH_Before++;
                    if (dDataLow1 < dDataLow2)	IL_Before++;
                }
            }
            // 이후 봉갯수로 주요고점, 저점 찾기
            for ( j = 1; j <= nAfter; j++)	{
                if (i - j < nLast)	{
                    if (IH_After == j-1)	{
                        IH_After = nAfter;		bIH_Complete = false;
                    }
                    if (IL_After == j-1)	{
                        IL_After = nAfter;		bIL_Complete = false;
                    }
                    break;
                }
                else
                {
                    dDataHigh1 = highData[i];
                    dDataHigh2 = highData[i-j];
                    dDataLow1 = lowData[i];
                    dDataLow2 = lowData[i-j];
                    if (dDataHigh1 > dDataHigh2)	IH_After++;
                    if (dDataLow1 < dDataLow2)	IL_After++;
                }
            }
            m_autoData[k] = 0;
            if (IH_Before == nBefore && IH_After == nAfter)	{
                if (bIH_Complete == false)	m_autoData[k] = AUTO_UN_IH;
                else						m_autoData[k] = AUTO_IH;
                nCount++;
            }
            if (IL_Before == nBefore && IL_After == nAfter)		{
                if (m_autoData[k] != 0)	{
                    m_autoData[k] = 0;	//고점,저점 동시발생시 0 처리
                }
                else	{
                    if (bIL_Complete == false)	m_autoData[k] = AUTO_UN_IL;
                    else						m_autoData[k] = AUTO_IL;
                    nCount++;
                }
            }
            bIH_Complete = true;		bIL_Complete = true;
            // 주요고점, 주요저점이 동시에 발생하는 경우=>두점사이에 새로운 주요점 생성
            int nOldRem, nRem;
            if (nOldTrend > 0 && m_autoData[k] > 0)	{
                nOldRem = ((int)nOldTrend % 2);
                nRem = (int)((int)m_autoData[k] % 2);
                if (nOldRem == nRem)	{
                    int nIndex = nOldIndex-1;
                    if (nIndex == i)	{	// 연이어 발생한 경우, 앞쪽 것은 무시하고, 뒷쪽 것만 인정한다.
                        m_autoData[nOldIndex] = 0;
                    }
                    else	{
                        double nHigh, nLow;
                        nHigh = highData[nIndex];
                        nLow = lowData[nIndex];

                        int nAutoType = 0;
                        for (int n = nIndex; n > i; n--)	{
                            dDataHigh1 = highData[n];
                            dDataLow1 = lowData[n];

                            if (m_autoData[k] == AUTO_IH || m_autoData[k] == AUTO_UN_IH)	{		// 새로운 저점 생성
                                nAutoType = AUTO_IL;
                                if (nLow >= dDataLow1)		{
                                    nLow = dDataLow1;
                                    nIndex = n;
                                }
                            }
                            else if (m_autoData[k] == AUTO_IL || m_autoData[k] == AUTO_UN_IL)	{	// 새로운 고점 생성
                                nAutoType = AUTO_IH;
                                if (nHigh <= dDataHigh1)	{
                                    nHigh = dDataHigh1;
                                    nIndex = n;
                                }
                            }
                        } // end-for
                        m_autoData[nIndex] = nAutoType;
                        if (nAutoType > 0)	nCount++;
                    }
                }
            }

            if (m_autoData[k] > 0)	{
                nOldTrend = m_autoData[k];
                nOldIndex = i;
            }
        }
    }
}