package drfn.chart.graph;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

public class ABRatioGraph extends AbstractGraph{
    int[][] data;
    double[] a_ratio;
    double[] b_ratio;
    //int[] base=null;
    public ABRatioGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        String[] datakind = {"시가","고가","저가","종가"};
        setDatakind(datakind);
        definition ="당일시가와 당일종가의 상승하락 관계를 살펴서 두개의 선그래프 (A Ratio, B Ratio)로 표현합니다.  일반적으로 A Ratio가 B Ratio를 상향돌파하면 매수신호이고 하향돌파하면 매도신호입니다.  사용자 입력수치는 수식에서 일정기간(n)의 합계를 위한 기간값입니다";
        m_strDefinitionHtml = "AB_Ratio.html";		//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)
    }
    //----------------------------------------------
//                     25일 간의 (당일 고가-당일 시가) 합계
//          ARatio = ---------------------------------------- * 100
//                     25일 간의 (당일 시가-당일 저가) 합계
//
//                     25일 간의 (당일 고가-당일 종가) 합계
//          BRatio = ---------------------------------------- * 100
//                     25일 간의 (당일 종가-당일 저가) 합계
//
//----------------------------------------------
    public void FormulateData() {
//        data = getData(1);
        double[] openData = _cdm.getSubPacketData("시가");
        double[] highData = _cdm.getSubPacketData("고가");
        double[] lowData = _cdm.getSubPacketData("저가");
        double[] closeData = _cdm.getSubPacketData("종가");
        if(closeData==null) return;
        double ho,ol,hc,cl;
        int dLen = closeData.length;
        a_ratio = new double[dLen];
        b_ratio = new double[dLen];
        for(int i=0;i<dLen;i++){
//            if(i<interval[0]) {
//            	a_ratio[i]=0;
//            	b_ratio[i]=0;
//            	continue;
//            }
            //2019.10.08 by JJH - 보조지표 값 수정 (하나금투 HTS기준) >>
//            if(i<interval[0]) {
            if(i<interval[0]+1) {
                //2019.10.08 by JJH - 보조지표 값 수정 (하나금투 HTS기준) <<
                a_ratio[i]=0;
                continue;
            }
            //2019.10.08 by JJH - 보조지표 값 수정 (하나금투 HTS기준) >>
//            if(i<interval[1]) {
            if(i<interval[1]+1) {
                //2019.10.08 by JJH - 보조지표 값 수정 (하나금투 HTS기준) <<
                b_ratio[i]=0;
                continue;
            }
            ho=ol=hc=cl=0;
//            for(int j=i;j>i-interval[0];j--){
//                ho += highData[j]-openData[j];
//                ol += openData[j]-lowData[j];
//                if(j>=0) {
//	                hc += highData[j]-closeData[j-1];
//	                cl += closeData[j-1]-lowData[j];
//                }
//            }
            for(int j=i;j>i-interval[0];j--){
                ho += highData[j]-openData[j];
                ol += openData[j]-lowData[j];
            }

            for(int j=i;j>i-interval[1];j--){
                if(j>=0) {
                    hc += highData[j]-closeData[j-1];
                    cl += closeData[j-1]-lowData[j];
                }
            }

            if(ol!=0)a_ratio[i] = (ho*100)/ol;
            else a_ratio[i] = 100;
            if(cl!=0)b_ratio[i] = (hc*100)/cl;
            else b_ratio[i] = 100;
        }
        for(int i=0;i<tool.size();i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            if(i==0){
                _cdm.setSubPacketData(dt.getPacketTitle(),a_ratio);
                _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
            }
            else {
                _cdm.setSubPacketData(dt.getPacketTitle(),b_ratio);
                _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
            }
        }
        formulated = true;
    }
    public void reFormulateData() {
        FormulateData();
        formulated = true;
    }
    public void drawGraph(Canvas g){
        if(!formulated)FormulateData();                       //저장되어 있지 않다면 계산을 새로 한다

        for(int i=0;i<tool.size();i++){
            DrawTool t=(DrawTool)tool.elementAt(i);
            double[] drawData=null;
            try{
                drawData=_cdm.getSubPacketData(t.getPacketTitle());
            }catch(ArrayIndexOutOfBoundsException e){
                return;
            }

            if(i==0) _cvm.useJipyoSign=true;
            else _cvm.useJipyoSign=false;

            t.plot(g,drawData);
            //2013. 9. 5 지표마다 기준선 설정 추가>>
//            if(base!=null&&i<base.length)t.draw(g,base[i]);
            drawBaseLine(g);
            //2013. 9. 5 지표마다 기준선 설정 추가>>
        }

        //2014. 9. 15 매매 신호 보기 기능 추가>>
        DrawTool t=(DrawTool)tool.elementAt(0);
        if(isSellingSignalShow)
            t.drawSignal(g, b_ratio, a_ratio);
        //2014. 9. 15 매매 신호 보기 기능 추가<<

    }
    public void drawGraph_withSellPoint(Canvas g){
    }

    public String getName(){
        return "AB Ratio";
    }
}