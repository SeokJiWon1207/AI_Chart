package drfn.chart.graph;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

public class PVTGraph extends AbstractGraph{
    
    String[] datakind = {"종가","기본거래량"};
    double[] pvtData;
    double[] signal;
    public PVTGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        definition="PVI의 해석방법은 먼저 거래량이 증가하는 날은 정보에 미흡한 군중들이 시장에 참여한다고 가정하고, 반대로 거래량이 감소하는 날은 기금 형태의 자금이 주로 매매를 한다고 가정을 하여 해석하는데, 이 결과로 PVI는 정보에 미흡한 군중들의 움직임 형태를 파악한 것입니다.이 지표는 통상 255일 이동평균을 사용하거나 52주 이동평균을 사용하는데, NVI와 마찬가지로 PVI 지표가 이동평균을 상향하면 매수 시점, 하향하면 매도 시점으로 분석을 합니다";

        m_strDefinitionHtml = "pvt.html";	//2018.05.02 by lyj 보조지표 설명/활용법 추가(상세설정창)
    }
    public void FormulateData(){
    	double[] closeData = _cdm.getSubPacketData("종가");
    	double[] volData = _cdm.getSubPacketData("기본거래량");
    	if(closeData==null) return;
        int dLen = closeData.length;
        pvtData = new double[dLen];
        for(int i=0; i<dLen; i++){
        	pvtData[i] = this.PVT(i, closeData, volData);
        }
        signal = exponentialAverage(pvtData,interval[0]);

        for(int i=0;i<tool.size();i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            if(i==0)_cdm.setSubPacketData(dt.getPacketTitle(),pvtData);
            else _cdm.setSubPacketData(dt.getPacketTitle(),signal);
            _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
        }
        
        formulated = true;
    }
    public void reFormulateData() {
        FormulateData();
        formulated = true;
    }
    private double PVT(int lIndex, double[] closeData, double[] volData){
    	double ldRetVal = 0;
    	double ldClose;
    	double ldCloseOld;
    	double ldVolume;
    	double ldVal;
    	double ldPVTOld;

    	ldClose = closeData[lIndex];
    	ldVolume = volData[lIndex];

    	if (lIndex < 1)
    	{
//    		pvtData[lIndex] = ldVolume;
//    		return ldVolume;
            pvtData[lIndex] = 0;
            return 0;
    	}

    	ldCloseOld = closeData[lIndex-1];


		ldPVTOld = pvtData[lIndex-1];
        if(ldCloseOld * ldVolume == 0)
            ldVal = 0;
        else
            ldVal = ldPVTOld + (ldClose - ldCloseOld) / ldCloseOld * ldVolume;

		pvtData[lIndex] = ldVal;

    	//ldRetVal = yesAccum(lIsUpdateData, lIndex, OPT_VAR, 
    	//			GET_SUB_TEMP(TEMP_VAR, 1));
    	ldRetVal = ldVal;

    	return ldRetVal;
    }
    
    public void drawGraph(Canvas g){
        if(!formulated)FormulateData();                       //저장되어 있지 않다면 계산을 새로 한다
        
        double[] drawData=null;
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
    }
    public void drawGraph_withSellPoint(Canvas g){
    }
    
    public String getName(){
        return "PVT";
    }
}

