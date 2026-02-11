package drfn.chart.graph;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

public class RMIGraph extends AbstractGraph{

    int[][] data;
    double[] rmi;
    double[] signal;
    public RMIGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        definition="RMI ";
        m_strDefinitionHtml = "RMI.html";	//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)
    }
    //========================================
    //RMI :
    // RMI = 100 - 100 / (1+ 상승모멘텀평균/하락모멘텀평균)
    //========================================
    public void FormulateData() {
    	double[] closeData = _cdm.getSubPacketData("종가");
    	if(closeData==null) return;
        int dLen = closeData.length;
        rmi = new double[dLen];
        double[] up_data = new double[dLen];
        double[] dn_data = new double[dLen];
   	    
   	    for(int i=0 ; i < dLen-interval[1] ; i++){
            if(closeData[i]!=0) {
                up_data[i + interval[1]] = (closeData[i + interval[1]] - closeData[i] > 0) ? closeData[i + interval[1]] - closeData[i] : 0;
                dn_data[i + interval[1]] = Math.abs((closeData[i + interval[1]] - closeData[i] < 0) ? closeData[i] - closeData[i + interval[1]] : 0);
            }
            else
            {
                up_data[i + interval[1]] = 0;
                dn_data[i + interval[1]] = 0;
            }
   	    }
        double[] upEMA = makeAverageD(up_data,interval[0]);
        double[] downEMA = makeAverageD(dn_data,interval[0]);
//        for(int j=interval[0]; j < dLen ; j++){
        for(int j=interval[0]+interval[1]-1; j < dLen ; j++){
        	rmi[j]=100. - (100. / (1. + (upEMA[j]/downEMA[j])));
        }
//        signal= exponentialAverage(rmi,interval[1],interval[0]);
        for(int i=0;i<tool.size();i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            if(i==0){
            	_cdm.setSubPacketData(dt.getPacketTitle(),rmi);
            	_cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
            }
            else {
            	_cdm.setSubPacketData(dt.getPacketTitle(),signal);
            	_cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
            }
        }

        formulated = true;
    }
//    public void FormulateData() {
//        double[] closeData = _cdm.getSubPacketData("종가");
//        if(closeData==null) return;
//        int dLen = closeData.length;
//        rmi = new double[dLen];
//        double[] up_data = new double[dLen];
//        double[] dn_data = new double[dLen];
//
//        for(int i=0 ; i < dLen-interval[0] ; i++){
//
//            up_data[i+interval[0]] =(closeData[i+interval[0]]-closeData[i]>0)?closeData[i+interval[0]]-closeData[i]:0;
//            dn_data[i+interval[0]] =Math.abs((closeData[i+interval[0]]-closeData[i]<0)?closeData[i]-closeData[i+interval[0]]:0);
//        }
//        double[] upEMA = makeAverageD(up_data,interval[1]);
//        double[] downEMA = makeAverageD(dn_data,interval[1]);
//        for(int j=interval[1]; j < dLen ; j++){
//            rmi[j]=100. - (100. / (1. + (upEMA[j]/downEMA[j])));
//        }
////        signal= exponentialAverage(rmi,interval[1],interval[0]);
//        for(int i=0;i<tool.size();i++){
//            DrawTool dt = (DrawTool)tool.elementAt(i);
//            if(i==0){
//                _cdm.setSubPacketData(dt.getPacketTitle(),rmi);
//                _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
//            }
//            else {
//                _cdm.setSubPacketData(dt.getPacketTitle(),signal);
//                _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
//            }
//        }
//
//        formulated = true;
//    }

    public void reFormulateData() {
        FormulateData();
        formulated = true;
    }
    public void drawGraph(Canvas gl){
        if(!formulated)FormulateData();                       //저장되어 있지 않다면 계산을 새로 한다
        if(tool==null || tool.size()==0) return;
        double[] drawData=null;
        //2012. 7. 2   기준선 크기
        //gl.glLineWidth(COMUtil.graphLineWidth);
        for(int i=0;i<tool.size();i++){
            DrawTool t=(DrawTool)tool.elementAt(i);
            drawData=_cdm.getSubPacketData(t.getPacketTitle());
            if(i==0) _cvm.useJipyoSign=true;
            else _cvm.useJipyoSign=false;
            t.plot(gl,drawData);
            //if(base!=null&&i<base.length)t.draw(g,base[i]);
        }

        //2012. 7. 2   기준선 크기
        //gl.glLineWidth(COMUtil.graphLineWidth2);
//        for(int i=0;i<base.length;i++){
//            DrawTool t=(DrawTool)tool.elementAt(0);
////            g.setColor(base_col[i]);
//            t.draw(gl,base[i]);
//        }
        //2013. 9. 5 지표마다 기준선 설정 추가>>
        drawBaseLine(gl);
        //2013. 9. 5 지표마다 기준선 설정 추가>>

        //2014. 9. 15 매매 신호 보기 기능 추가>>
//        DrawTool t=(DrawTool)tool.elementAt(0);
//        if(isSellingSignalShow)
//        	t.drawSignal(gl, rmi, signal);
        //2014. 9. 15 매매 신호 보기 기능 추가<<
    }
    public void drawGraph_withSellPoint(Canvas g){
    }

    public String getName(){
        return "RMI";
    }
}