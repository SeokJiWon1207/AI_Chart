package drfn.chart.signal;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.graph.AbstractGraph;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;
import drfn.chart.util.MinMax;

public class ADXRStrategySignal extends AbstractGraph {
    double[] pdiData;
    double[] ndiData;
    double[] adxData;
    double[] m_data;
    double[] m_signal;
    int[][] data;
    //int[] base=null;

    public ADXRStrategySignal(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        String[] datakind = {"고가","저가","종가"};
        _dataKind = datakind;
//        m_strDefinitionHtml = "ADX.html";	//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)
    }
//    public void FormulateData(){
//        double[] highData = _cdm.getSubPacketData("고가");
//        double[] lowData = _cdm.getSubPacketData("저가");
//        double[] closeData = _cdm.getSubPacketData("종가");
//        if(closeData==null) return;
//        int dLen = closeData.length;
//        pdiData = new double[dLen];
//        ndiData = new double[dLen];
//        adxData = new double[dLen];
//        double temp[] = new double[3];
//        double tr[] = new double[dLen];
//        for(int i=1; i<dLen; i++){
//            if( (highData[i] > highData[i-1]) && ( (highData[i] - highData[i-1]) > (lowData[i-1] - lowData[i]))) //오늘의 고가가 어제의 고가보다 크면 양의값
//                pdiData[i] = highData[i]- highData[i-1];
//            else                           //작거나 같으면 0값
//                pdiData[i] = 0;
//
//            if(lowData[i] < lowData[i-1]  && ( (highData[i] - highData[i-1]) < (lowData[i-1] - lowData[i])))
//                ndiData[i] = lowData[i-1]- lowData[i];
//            else
//                ndiData[i] = 0;
//
//            temp[0] = Math.abs(highData[i]-lowData[i]);
//            temp[1] = Math.abs(closeData[i-1]-highData[i]);
//            temp[2] = Math.abs(closeData[i-1]-lowData[i]);
//
//            //2015. 2. 9 ADX, DMI 지표 계산오류 수정>>
////            tr[i] = MinMax.getIntMaxT(temp);
//            tr[i] = MinMax.getDoubleMaxT(temp);
//            //2015. 2. 9 ADX, DMI 지표 계산오류 수정<<
//        }
////        pdiData = exponentialAverage(pdiData, interval[0]);
////        ndiData = exponentialAverage(ndiData, interval[0]);
////        tr = exponentialAverage(tr, interval[0]);
//        pdiData = makeAverageDaewoo(pdiData, interval[0], interval[0]);
//        ndiData = makeAverageDaewoo(ndiData, interval[0], interval[0]);
//        tr = makeAverageDaewoo(tr, interval[0], interval[0]);
//        for(int i=0; i<dLen; i++){
//            if(i<interval[0]){
//                pdiData[i] = 0;
//                ndiData[i] = 0;
//                adxData[i] = 0;
//                continue;
//            }
//            pdiData[i] = (pdiData[i]/tr[i]*100) ;
//            ndiData[i] = (ndiData[i]/tr[i]*100) ;
//            if(i>=interval[0]*3)
//                adxData[i] = Math.abs(pdiData[i]-ndiData[i])/(pdiData[i]+ndiData[i])*100 ;
//        }
//        //adxData = exponentialAverage(adxData, interval[0]);
//        adxData = makeAverageADX(adxData, interval[0], interval[0]*2);
//
//        for(int i=0;i<3;i++){
//            DrawTool dt = (DrawTool)tool.elementAt(i);
//            if(i==0)_cdm.setSubPacketData(dt.getPacketTitle(),adxData);
//            else if(i==1) _cdm.setSubPacketData(dt.getPacketTitle(),pdiData);
//            else if(i==2) _cdm.setSubPacketData(dt.getPacketTitle(),ndiData);
//            _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
//        }
//        formulated = true;
//    }
    public void FormulateData(){
    	double[] highData = _cdm.getSubPacketData("고가");
    	double[] lowData = _cdm.getSubPacketData("저가");
    	double[] closeData = _cdm.getSubPacketData("종가");
    	if(closeData==null) return;
        int dLen = closeData.length;
	    pdiData = new double[dLen];
        ndiData = new double[dLen];
        adxData = new double[dLen];
        double temp[] = new double[3];
        double tr[] = new double[dLen];
        for(int i=1; i<dLen; i++){
            if( (highData[i] > highData[i-1]) && ( (highData[i] - highData[i-1]) > (lowData[i-1] - lowData[i]))) //오늘의 고가가 어제의 고가보다 크면 양의값
                pdiData[i] = highData[i]- highData[i-1];
            else                           //작거나 같으면 0값
                pdiData[i] = 0;

            if(lowData[i] < lowData[i-1]  && ( (highData[i] - highData[i-1]) < (lowData[i-1] - lowData[i])))
                ndiData[i] = lowData[i-1]- lowData[i];
            else
                ndiData[i] = 0;
            
            temp[0] = Math.abs(highData[i]-lowData[i]);
            temp[1] = Math.abs(closeData[i-1]-highData[i]);
            temp[2] = Math.abs(closeData[i-1]-lowData[i]);

            tr[i] = MinMax.getDoubleMaxT(temp);
        }
        pdiData = exponentialAverage_DI(pdiData, interval[0], 1);
        ndiData = exponentialAverage_DI(ndiData, interval[0], 1);
        tr = exponentialAverage_DI(tr, interval[0], 1);
        for(int i=0; i<dLen; i++){
            //if(i<interval[0]-1){
            if(i<interval[0]){
            	pdiData[i] = 0;
			    ndiData[i] = 0;
			    adxData[i] = 0;
			    continue;
    		}
			pdiData[i] = (pdiData[i]/tr[i]*100) ;
    		ndiData[i] = (ndiData[i]/tr[i]*100) ;
    		adxData[i] = Math.abs(pdiData[i]-ndiData[i])/(pdiData[i]+ndiData[i])*100 ;
        }
        m_data = exponentialAverage_DI(adxData, interval[0], interval[0]);
        m_signal = exponentialAverage(m_data, interval[1], interval[0]);

	    for(int i=0;i<tool.size();i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            if(i==0){
                String strSignal = dt.getPacketTitle()+"_Signal";
                _cdm.setSubPacketData(dt.getPacketTitle(),m_data);
                _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
                _cdm.setSubPacketData(strSignal,m_signal);
            }
        }
	    formulated = true;
    }
    public void reFormulateData() {
        FormulateData();
        formulated = true;
    }
    public void drawGraph(Canvas g){
        drawStrategyGraph(g,tool);
    }
    public void drawGraph_withSellPoint(Canvas g){
    }

    public String getName(){
        if(m_nStrategyType == 1)
            return "ADXRStrategy강세약세";
        else
            return "ADXRStrategy신호";
    }
}
