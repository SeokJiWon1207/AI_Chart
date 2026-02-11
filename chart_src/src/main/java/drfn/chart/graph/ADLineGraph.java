package drfn.chart.graph;

import android.graphics.Canvas;
import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;

public class ADLineGraph extends AbstractGraph{
    double[] adlineData;

    double[] signal;

    int[][] data;
    //int[] base=null;

    public ADLineGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        String[] datakind = {"시가","고가","저가","종가", "기본거래량"};
        _dataKind = datakind;
        definition="가격이 상승할 때는 많은 거래량을 수반하고, 가격이 하락할 때는 거래량이 줄어드는것에 착안하여, 가격과 거래량의 변화를 나타낸 지표로써 MarcChaikin이 개발하였습니다. ";
        m_strDefinitionHtml = "AD_Line.html";	//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)
    }
    public void FormulateData(){
        double[] openData = _cdm.getSubPacketData("시가");
        double[] highData = _cdm.getSubPacketData("고가");
        double[] lowData = _cdm.getSubPacketData("저가");
        double[] closeData = _cdm.getSubPacketData("종가");
        double[] volData = _cdm.getSubPacketData("기본거래량");
        if(closeData==null) return;
        int dLen = closeData.length;

        adlineData = new double[dLen];
        //[(종가-시가)/(고가-저가)} * 거래량]   삼성 pop hts
        double[] dSumTmp = new double[dLen];
        for(int i = 1; i < dLen; i++)
        {
            //2015. 2. 27 ADLine 분틱 그리지 않음>>
            if(highData[i]-lowData[i] != 0)
            {
                dSumTmp[i] = ((closeData[i]-openData[i]) / (highData[i]-lowData[i])) * volData[i];
            }
            else
                dSumTmp[i] = 0;
            //2015. 2. 27 ADLine 분틱 그리지 않음<<
        }

        //최초 데이터부터 누적
        for(int i = 0; i < dSumTmp.length; i++)
        {
            if(0 == i)
            {
                adlineData[i] = dSumTmp[i];
            }
            else
            {
                adlineData[i] = adlineData[i-1] + dSumTmp[i];
            }
        }

        //2019.10.08 by JJH - 보조지표 값 수정 (하나금투 HTS기준) >>
        for(int i=0; i<dLen; i++){
            if(i<interval[0]-1){
                adlineData[i]=0;
            }
        }

        //2015. 3. 3 ADLine Signal 만 남기기
//        signal= exponentialAverage(adlineData, interval[1]);
//        signal= exponentialAverage(adlineData, interval[0]);
        signal= makeAverage(adlineData, interval[0]);
        //2019.10.08 by JJH - 보조지표 값 수정 (하나금투 HTS기준) <<

        for(int i=0;i<tool.size();i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            if(i==0) {
                _cdm.setSubPacketData(dt.getPacketTitle(),adlineData);
//            	_cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
            }
            else {
                _cdm.setSubPacketData(dt.getPacketTitle(),signal);
//            	_cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
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
        if(tool==null || tool.size()==0) return;
        double[] drawData=null;
        //2012. 7. 2   기준선 크기
        //gl.glLineWidth(COMUtil.graphLineWidth);
        for(int i=0;i<tool.size();i++){
            DrawTool t=(DrawTool)tool.elementAt(i);
            drawData=_cdm.getSubPacketData(t.getPacketTitle());
            if(i==0) _cvm.useJipyoSign=true;
            else _cvm.useJipyoSign=false;
            t.plot(g,drawData);
            //if(base!=null&&i<base.length)t.draw(g,base[i]);
        }
    }
    public void drawGraph_withSellPoint(Canvas g){
    }

    public String getName(){
        return "AD Line";
    }
}
