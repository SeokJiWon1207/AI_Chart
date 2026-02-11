package drfn.chart.graph;

import android.graphics.Canvas;

import java.util.Vector;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;
import drfn.chart.util.MinMax;


public class NDIGraph extends AbstractGraph{
	double[] graphData;
	double[] signal;
    int[][] data;//계산 전 데이터
    Vector<DrawTool> tool;
    public NDIGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        tool = getDrawTool();//드로우 툴을 구한다
        definition="Force Index";

        m_strDefinitionHtml = "ndi.html";    //2018.05.02 by lyj 보조지표 설명/활용법 추가(상세설정창)
    }
    //========================================
    //DEMA : 
    //========================================
    public void FormulateData(){
        graphData = getNDI(interval[0]);
	    if(graphData==null) return;
	    //2017.08.14 by pjm 지표 수정 >>
//        DrawTool dt = (DrawTool)tool.elementAt(0);
//        _cdm.setSubPacketData(dt.getPacketTitle(),graphData);
//        _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
	    signal = exponentialAverage(graphData,interval[1]);
	    for(int i=0;i<tool.size();i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            if(i==0)_cdm.setSubPacketData(dt.getPacketTitle(),graphData);
            else _cdm.setSubPacketData(dt.getPacketTitle(),signal);
            _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
          //2017.08.14 by pjm 지표 수정 <<
        }
	    formulated = true;
    }

    public void reFormulateData() {
        FormulateData();
        formulated = true;
    }

    private double[] getNDI(int interval) //signal 포함
    {
//        double[] closeData = _cdm.getSubPacketData("종가");
//        double[] volData = _cdm.getSubPacketData("기본거래량");
//        double[] highData = _cdm.getSubPacketData("고가");
//        double[] lowData = _cdm.getSubPacketData("저가");
//        if(closeData == null || volData == null)	return null;
//

        double[] highData = _cdm.getSubPacketData("고가");
        double[] lowData = _cdm.getSubPacketData("저가");
        double[] closeData = _cdm.getSubPacketData("종가");
        if(closeData==null) return null;
        int dLen = closeData.length;
        double[] ndi = new double[dLen];
        double[] pdiData = new double[dLen];
        double[] ndiData = new double[dLen];
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
        pdiData = exponentialAverage(pdiData, interval);
        ndiData = exponentialAverage(ndiData, interval);
        tr = exponentialAverage(tr, interval);
        for(int i=0; i<dLen; i++){
            if(i<interval-1){
                pdiData[i] = 0;
                ndiData[i] = 0;
                ndi[i] = 0;
                continue;
            }
            pdiData[i] = (pdiData[i]/tr[i]*100) ;
            ndiData[i] = (ndiData[i]/tr[i]*100) ;
            ndi[i] = pdiData[i] - ndiData[i];
        }
        return ndi;
    }


    public void drawGraph(Canvas g){
        if(!formulated)FormulateData();                       //저장되어 있지 않다면 계산을 새로 한다

        double[] drawData=null;
        //2017.08.14 by pjm 지표 수정 >>
//        DrawTool t=(DrawTool)tool.elementAt(0);
//        drawData=_cdm.getSubPacketData(t.getPacketTitle());
//        _cvm.useJipyoSign=true;
//
//        t.plot(g,drawData);
//        if(base!=null){
//            for(int i=0;i<base.length;i++)t.draw(g,base[i]);
//        }
        for(int i=0;i<tool.size();i++){
            DrawTool t=(DrawTool)tool.elementAt(i);
            try{
                drawData=_cdm.getSubPacketData(t.getPacketTitle());
            }catch(ArrayIndexOutOfBoundsException e){
                return;
            }
            if(i==0) _cvm.useJipyoSign=true;
            else _cvm.useJipyoSign=false;
            t.plot(g,drawData);
        }
        //2017.08.14 by pjm 지표 수정 <<
    }
    public void drawGraph_withSellPoint(Canvas g){
    }
    public String getName() {
        return "NDI";
    }
}