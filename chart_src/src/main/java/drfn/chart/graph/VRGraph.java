package drfn.chart.graph;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

public class VRGraph extends AbstractGraph{
    int[][] data;//계산 전 데이터
    double[] vr;
    public VRGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        String[] datakind = {"종가","기본거래량"};
        _dataKind = datakind;
        definition="주가등락과 거래량을 연관시킨 지표입니다.  OBV와 상호보완적인 지표로서 시세의 강약을 파악하는데에 사용됩니다.  VR의 수치는 통상적으로 높으며, 과열권보다는 침체권의 신뢰도가 큽니다.VR이 60% 이하로 떨어지는 경우는 상당한 정도의 침체장세가 아니면 발생하기 힘들다는 점을 고려하면, VR의 과열신호보다는 침체신호가 더욱 신뢰도가 있습니다";
        m_strDefinitionHtml = "vr.html";	//2014. 11. 20 각 보조지표 설명/활용법 추가(상세설정창)
    }
    //----------------------------------
    // Volumn Ratio 
    // n 일 간 주가 상승일의 거래량 합계 + n일간 주가 보합일의 거래량 합계*0.5
    // --------------------------------------------------------------- * 100     (n 20일)
    // n 일 간 주가 하락일의 거래량 합계 + n일간 주가 보합일의 거래량 합계 *0.5
    //----------------------------------    
    public void FormulateData() {
        double[] closeData = _cdm.getSubPacketData("종가");
        double[] volData = _cdm.getSubPacketData("기본거래량");
        if(closeData==null) return;

        int dLen = closeData.length;
        vr = new double[dLen];
        double volUp,volDn,volSm;
//        for(int i=interval[0];i<dLen;i++){
        for(int i=0;i<dLen;i++){
            if(i<interval[0]-1){
                vr[i] = 0;
                continue;
            }
            volUp=volDn=volSm=0.;
            for(int j=i;j>i-interval[0];j--){
                if(j<1) break;
                double cal = closeData[j]-closeData[j-1];
                if(cal<0){      //하락일
                    volDn+=volData[j];
                }else if(cal>0){//상승일
                    volUp+=volData[j];
                }else{          //보합일
                    volSm+=volData[j];
                }
            }
            volSm=volSm*0.5;
            double Hab=volDn+volSm;
            if(Hab!=0.){
                vr[i] = (volUp+volSm)*100/(Hab);
            }else{
                vr[i]=vr[i-1];
            }
        }
        DrawTool dt = (DrawTool)tool.elementAt(0);
        _cdm.setSubPacketData(dt.getPacketTitle(),vr);
        _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
        formulated = true;
    }
    public void reFormulateData() {
        FormulateData();
        formulated = true;
    }
    public void drawGraph(Canvas g){
        if(!formulated)FormulateData();                       //저장되어 있지 않다면 계산을 새로 한다

        double[] drawData=null;
        //2012. 7. 2   기준선 크기
        //g.glLineWidth(COMUtil.graphLineWidth);
        DrawTool t=(DrawTool)tool.elementAt(0);
        drawData=_cdm.getSubPacketData(t.getPacketTitle());
        _cvm.useJipyoSign=true;
        t.plot(g,drawData);
        //2012. 7. 2   기준선 크기
        //g.glLineWidth(COMUtil.graphLineWidth2);
//        for(int i=0;i<base.length;i++){
//            t.draw(g,base[i]);
//        }
        //2013. 9. 5 지표마다 기준선 설정 추가>>
        drawBaseLine(g);
        //2013. 9. 5 지표마다 기준선 설정 추가>>
    }
    public void drawGraph_withSellPoint(Canvas g){
    }

    public String getName(){
        return "VR";
    }
}