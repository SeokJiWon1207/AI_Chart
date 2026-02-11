package drfn.chart.graph;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;
import drfn.chart.util.MinMax;

public class DMIGraph extends AbstractGraph{
    double[] pdiData;
    double[] ndiData;
    int[][] data;
    //int[] base=null;

    public DMIGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        String[] datakind = {"고가","저가","종가"};
        _dataKind = datakind;
        definition="전일대비 고가와 저가의 절대값 중 어느 쪽이 큰가를 판단하여 추세의 방향을 표시하는 지표입니다.  2개의 선그래프로 표시되며, ADX, ADXR, ATR 등의 지표와 함께 분석하는 것이 일반적입니다.  일반적으로 DI+가 DI-를 상향돌파할 때 매수시점, 하향돌파할 때 매도시점으로 간주합니다.  사용자 입력수치는 이동평균기간(n)입니다";
        m_strDefinitionHtml = "DMI.html";	//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)
    }
    
    public void FormulateData(){
//      data = getData(1);
        double[] highData = _cdm.getSubPacketData("고가");
        double[] lowData = _cdm.getSubPacketData("저가");
        double[] closeData = _cdm.getSubPacketData("종가");
        if(closeData==null) return;
        int dLen = closeData.length;
	    pdiData = new double[dLen];
        ndiData = new double[dLen];
        double temp[] = new double[3];
        double tr[] = new double[dLen];
//      double PDIN=0.0, NDIN=0.0, TRN=0.0;
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
      //for(int i=interval[0]-1; i<dLen; i++){
        for(int i=1; i<dLen; i++){
            if(tr[i] != 0) {
                pdiData[i] = (pdiData[i] / tr[i] * 100);
                ndiData[i] = (ndiData[i] / tr[i] * 100);
            }
        }
	    for(int i=0;i<2;i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            if(i==0)
                _cdm.setSubPacketData(dt.getPacketTitle(),pdiData);
            else
                _cdm.setSubPacketData(dt.getPacketTitle(),ndiData);
            _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
        }
	    formulated = true;
    }
//    public void FormulateData(){
////        data = getData(1);
//        double[] highData = _cdm.getSubPacketData("고가");
//        double[] lowData = _cdm.getSubPacketData("저가");
//        double[] closeData = _cdm.getSubPacketData("종가");
//        if(closeData==null) return;
//        int dLen = closeData.length;
//        pdiData = new double[dLen];
//        ndiData = new double[dLen];
//        double temp[] = new double[3];
//        double tr[] = new double[dLen];
////        double PDIN=0.0, NDIN=0.0, TRN=0.0;
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
////          tr[i] = MinMax.getIntMaxT(temp);
//            tr[i] = MinMax.getDoubleMaxT(temp);
//            //2015. 2. 9 ADX, DMI 지표 계산오류 수정<<
//        }
//
//        //pdiData = exponentialAverage(pdiData, interval[0]);
//        //ndiData = exponentialAverage(ndiData, interval[0]);
//        //tr = exponentialAverage(tr, interval[0]);
//
//        pdiData = makeAverageDaewoo(pdiData, interval[0], interval[0]);
//        ndiData = makeAverageDaewoo(ndiData, interval[0], interval[0]);
//        tr = makeAverageDaewoo(tr, interval[0], interval[0]);
//        for(int i=interval[0]; i<dLen; i++){
//            pdiData[i] = (pdiData[i]/tr[i]*100) ;
//            ndiData[i] = (ndiData[i]/tr[i]*100) ;
//        }
//        for(int i=0;i<2;i++){
//            DrawTool dt = (DrawTool)tool.elementAt(i);
//            if(i==0)_cdm.setSubPacketData(dt.getPacketTitle(),pdiData);
//            else _cdm.setSubPacketData(dt.getPacketTitle(),ndiData);
//            _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
//        }
//        formulated = true;
//    }
    public void reFormulateData() {
        FormulateData();
        formulated = true;
    }
    public void drawGraph(Canvas g){
        if(!formulated)FormulateData();                       //저장되어 있지 않다면 계산을 새로 한다

        double[] drawData=null;
        for(int i=0;i<2;i++){
            DrawTool t=(DrawTool)tool.elementAt(i);
            try{
                drawData=_cdm.getSubPacketData(t.getPacketTitle());
            }catch(ArrayIndexOutOfBoundsException e){
                return;
            }
            if(i==0) _cvm.useJipyoSign=true;
            else _cvm.useJipyoSign=false;
            t.plot(g,drawData);
//            if(base!=null&&i<base.length)t.draw(g,base[i]);
            //2013. 9. 5 지표마다 기준선 설정 추가>>
            drawBaseLine(g);
            //2013. 9. 5 지표마다 기준선 설정 추가>>
        }
    }
    public void drawGraph_withSellPoint(Canvas g){
    }

    public String getName(){
        return "DMI";
    }
}
