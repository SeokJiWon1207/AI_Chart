package drfn.chart.graph;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartPacketDataModel;
import drfn.chart.model.ChartViewModel;
import drfn.chart.util.COMUtil;

/**
 * 가격 이동평균 그래프
 */
public class MultiGraph extends AbstractGraph{
    String[] datakind = {"종가"};
    boolean m_isFirst = true;
    //double m = 0.06;
    public MultiGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);

    }
    //=====================================
    //  중심선  :  주가 n일 이동 평균
    //  상한선  :  주가 n일 이동 평균 *(1+m(%) )
    //  하한선  :  주가 n일 이동 평균 *(1-m(%) )               
    //                      n = 25일, m=6%(0.06)
    //=====================================
    public void FormulateData(){
        m_isFirst = false;
        for(int i=0;i<5;i++)
            _cdm.compareCode[i] = "";
        for(int i=0;i<tool.size();i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            String strTitle = dt.getPacketTitle();
            checkDataCount(strTitle);
            _cdm.compareCode[i] = strTitle;
            double[] data = _cdm.getSubPacketData(strTitle);
            if(data==null) continue;
            strTitle = strTitle+"_1";
            _cdm.setSubPacketData(strTitle,makeData(data));
            _cdm.setPacketFormat(strTitle, "× 0.01");
        }
        formulated = true;
    }
//    private double[] makeData(double[] data) {
//        if(data==null) return null;
//        int dataCnt = data.length;
//        if(dataCnt<1) return null;
//
//        double[] ratioData = new double[dataCnt];
//        int bIndex = _cdm.baseLineIndex;
//        for(int i=0; i<dataCnt; i++) {
//            ratioData[i] = ((data[i]*100-data[bIndex]*100)/data[bIndex]);
//        }
//
//        return ratioData;
//    }

    private double[] makeData(double[] data) {
        if(data==null) return null;
        int dataCnt = data.length;
        if(dataCnt<1) return null;

        double[] ratioData = new double[dataCnt];
        int bIndex = _cdm.baseLineIndex;

        //2019. 08. 20 by hyh - 비교차트 기준이 0 이하인 경우 종료되는 에러 수정 >>
        if (bIndex < 0) {
            _cdm.baseLineIndex = 0;
            bIndex = _cdm.baseLineIndex;
        }
        //2019. 08. 20 by hyh - 비교차트 기준이 0 이하인 경우 종료되는 에러 수정 <<

        for (int i = 0; i < dataCnt && bIndex < dataCnt; i++) {
            if (data[bIndex] == 0)
                bIndex++;
        }

        for (int i = 0; i < dataCnt && bIndex < dataCnt; i++) {
            if (data[bIndex] != 0)
                ratioData[i] = ((data[i] * 100 - data[bIndex] * 100) / data[bIndex]);
            else
                ratioData[i] = 0;
        }

        return ratioData;
    }
    private void checkDataCount(String strTitle) {
        double[] data = _cdm.getSubPacketData(strTitle);
        if(data == null || data.length < 1)
            return;
        int dataCnt = data.length;
        int nStartIndex = 0;
        //int nTotCount = _cdm.getCount();
        ChartPacketDataModel cpdm = _cdm.getChartPacket("자료일자");
        int nTotCount = cpdm.getDataCount();

        if(dataCnt >0 && dataCnt < nTotCount)
        {
            nStartIndex = nTotCount - dataCnt;
            double[] dDatas = new double[nTotCount];
            for(int i = 0 ; i < nStartIndex ; i++) {

                dDatas[i] = data[0];
            }

            for(int i = 0 ; i < dataCnt ; i++) {

                dDatas[nStartIndex+i] = data[i];
            }
            _cdm.setSubPacketData(strTitle,dDatas);
        }
        if(dataCnt >0 && dataCnt > nTotCount)
        {
            nStartIndex = dataCnt-nTotCount ;
            double[] dDatas = new double[nTotCount];

            for(int i = 0 ; i < nTotCount ; i++) {

                dDatas[i] = data[nStartIndex+i];
            }
            _cdm.setSubPacketData(strTitle,dDatas);
        }
    }

    public void reFormulateData() {
        FormulateData();
        formulated = true;
    }
    public void drawGraph(Canvas g){
        if(!formulated)FormulateData();                       //저장되어 있지 않다면 계산을 새로 한다        

        double[] drawData=null;
        int tCnt = tool.size();
        for(int i=0;i<tCnt;i++){
            DrawTool t=(DrawTool)tool.elementAt(i);
            String strTitle; 
            if(_cvm.nCompareType == 2)
            	strTitle = t.getPacketTitle();
            else
            	strTitle = t.getPacketTitle()+"_1";
            
            try{
                if(COMUtil.compareChecks[i]) {
                    t.setVisible(true);
                }
                else
                    t.setVisible(false);

                drawData=_cdm.getSubPacketData(strTitle);
            }catch(ArrayIndexOutOfBoundsException e){
                return;
            }
            _cvm.useJipyoSign=true;
            t.plot(g,drawData);
        }
        int baseCnt=0;
        if(base==null) baseCnt = 0;
        else baseCnt = base.length;
        if(tCnt>0) {
            baseCnt = 1;
        }
        for(int i=0; i<baseCnt; i++) {
            DrawTool t = (DrawTool)tool.get(0);
            double insertVal = 0;
            t.draw(g, insertVal);
        }
    }
    public void drawGraph_withSellPoint(Canvas g){
    }

    public String getName(){
        return "Multi";
    }
}