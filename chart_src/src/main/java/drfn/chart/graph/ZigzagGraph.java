package drfn.chart.graph;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartPacketDataModel;
import drfn.chart.model.ChartViewModel;
/**
 *  Zigzag그래프
 */
public class ZigzagGraph extends AbstractGraph{
    int[][] data;
    double[] zData;
    int dLen;
    private int defIndex = 0;
    //double varAvr = 5.00; //단위 %

    public ZigzagGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        definition="Zig Zag 지표는 주가나 지표의 등락을 실제보다 줄여서 기본적인 선을 연결한 것으로 챠트의 심한 등락 폭의 변화를 단순화시켜서 그린 것이다. 즉 ZigZag 지표는 단지 중요한 변화만을 보여주는 것이라 할 수 있다.";
        m_strDefinitionHtml = "zig_zag.html";
    }
    public void FormulateData(){
        if(!formulated){
            double[] closeData = _cdm.getSubPacketData("종가");
            if(closeData==null) return;
            dLen = closeData.length;
            zData = new double[dLen]; //그려질 데이타
            Double prevData = Double.valueOf(""+closeData[defIndex]);
            double prevValue = prevData.doubleValue();

            double changeValue = prevValue*((double)interval[0]/100);

            double highLimit = prevValue + changeValue;
            double lowLimit = prevValue - changeValue;

            double curValue = 0.00;

            final int high = 1;
            final int low = 2;
            int direction = 0;
            int prevDirection = 0;
            int index = defIndex;
            int dLen2 = dLen-1;
            for(int i=defIndex;i<dLen;i++){
                curValue = closeData[i];

                if(curValue > highLimit) {
                    direction = high;
                    if(direction != prevDirection) {
                        zData[index] = lowLimit;
                    }
                    index = i;
                    prevDirection = direction;
                    double temp = (curValue*((double)interval[0]/100));
                    lowLimit = curValue - temp;
                    highLimit = curValue;
                } else if(curValue < lowLimit) {
                    direction = low;
                    if(direction != prevDirection) {
                        zData[index] = highLimit;
                    }
                    index = i;
                    prevDirection = direction;
                    double temp = (curValue*((double)interval[0]/100));
                    highLimit = curValue + temp;
                    lowLimit = curValue;
                }
            }
            zData[defIndex] = closeData[defIndex];
            zData[dLen2] = closeData[dLen2];

            double dPrePrice = 0;
            int nPreIndex = 0;
            for(int i=0; i < dLen; i++){
                if(zData[i] != 0)
                {
                    if(dPrePrice != 0)
                    {
                        for(int j=nPreIndex+1; j<i; j++)
                        {
                            zData[j] = dPrePrice+(zData[i]-dPrePrice)/(i-nPreIndex)*(j-nPreIndex);
                        }
                    }
                    dPrePrice = zData[i];
                    nPreIndex = i;
                }

            }


            for(int i=0;i<tool.size();i++){
                DrawTool dt = (DrawTool)tool.elementAt(i);
                _cdm.setSubPacketData(dt.getPacketTitle(),zData);
                if(_cdm.nTradeMulti>0)
                    _cdm.setSyncPriceFormat(dt.getPacketTitle());
                else
                {
                    ChartPacketDataModel cpdm =_cdm.getChartPacket("종가");
                    if(cpdm.getPacketFormat_Index() == 14)
                        _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
                    else if(cpdm.getPacketFormat_Index() == 15)
                        _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.001");
                    else if(cpdm.getPacketFormat_Index() == 16)
                        _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.0001");

                }
            }

            formulated = true;
        }
    }

    public void reFormulateData(){
        formulated = false;
        FormulateData();
        formulated = true;
    }

    public void drawGraph(Canvas g){
        int num=_cvm.getViewNum();        //화면에 그릴 데이터 수
        int index=_cvm.getIndex();        //화면에 그리기 시작할 인덱스
        int margin=_cdm.getMargine();     //전체 데이터 마진
        int mar_index = margin-(dLen-defIndex);
        if(mar_index<=0) mar_index=0;
        double[] drawData=null;
        for(int i=0;i<tool.size();i++){
            DrawTool t=(DrawTool)tool.elementAt(i);
            drawData=_cdm.getSubPacketData(t.getPacketTitle(),index,num,mar_index);
            //2012. 7. 3   지그재그 선굵기 변경되게 수정 
            //g.glLineWidth(t.getLineT());
            t.plotDefault(g, drawData);
        }
    }

    public void drawGraph_withSellPoint(Canvas g){
    }

    public String getName(){
        return "Zig Zag";
    }
}