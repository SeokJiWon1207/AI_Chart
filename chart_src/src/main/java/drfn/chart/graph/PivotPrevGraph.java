package drfn.chart.graph;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartPacketDataModel;
import drfn.chart.model.ChartViewModel;
/**
 *  Pivot그래프
 */
public class PivotPrevGraph extends AbstractGraph{
    int[][] data;
    int dLen = 0;

    public PivotPrevGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);

        m_strDefinitionHtml = "Pivot.html";	//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)
    }

    //=======================================
    // 피봇가 = (고가 + 저가 + 종가)/3
    // 피봇 1차 저항(r1)=(2*피봇가)-저가
    // 피봇 2차 저항(r2)=피봇+고가-저가
    // 피봇 1차 지지(s1)=(2*피봇가)-고가
    // 피봇 2차 지지(s2)= 피봇-고가+저가
    // ----> LG식으로 변경 2002/04/18
    //=======================================
    public void FormulateData(){
        //주가 분데이터는 서버로부터 피봇가를 받아온다
        double[][] pivot=null;
        if(!formulated){
            if(_cdm.getDateType()==4&&_cdm.hasPivotData()){
                String[] datatitle = {"피봇2차저항","피봇1차저항","피봇가","피봇1차지지","피봇2차지지"};
                setDatakind(datatitle);
            }else{
                String[] datatitle = {"고가","저가","종가"};
                setDatakind(datatitle);
            }
            double[] highData = _cdm.getSubPacketData("고가");
            double[] lowData = _cdm.getSubPacketData("저가");
            double[] closeData = _cdm.getSubPacketData("종가");
            if(closeData==null) return;
            dLen = closeData.length;

            pivot = new double[5][dLen];

            for(int j=0;j<dLen-1;j++){
                if(j==0) {
                    for(int i=0; i<5; i++){
                        pivot[i][j]=0;
                    }
                }
                double pv= (highData[j]+lowData[j]+closeData[j])/3;//피봇
                double pv1 = 2*(pv)-lowData[j];            //1차 저항선
                double pv11= 2*(pv)-highData[j];           //1차 지지선
                double pv2 = pv+highData[j]-lowData[j];     //2차 저항
                double pv22 = pv-highData[j]+lowData[j];    //2차 지지
                pivot[0][j+1]=pv2;
                pivot[1][j+1]=pv1;
                pivot[2][j+1]=pv;
                pivot[3][j+1]=pv11;
                pivot[4][j+1]=pv22;
            }

//	        if(_cdm.getDateType()==4&&_cdm.hasPivotData()){
//	            for(int i=0; i<5; i++){
//	                for(int j=0;j<pivot[i].length;j++){
//	                    pivot[i][j] = data[j][i];
//	                }
//	            }
//	        }else{
//	            for(int j=0;j<pivot[0].length-1;j++){
//	                int pv= (data[j][0]+data[j][1]+data[j][2])/3;//피봇
//	                int pv1 = 2*(pv)-data[j][1];            //1차 저항선
//	                int pv11= 2*(pv)-data[j][0];           //1차 지지선
//	                int pv2 = pv+data[j][0]-data[j][1];     //2차 저항
//	                int pv22 = pv-data[j][0]+data[j][1];    //2차 지지
//	                pivot[0][j+1]=pv2;
//	                pivot[1][j+1]=pv1;
//	                pivot[2][j+1]=pv;
//	                pivot[3][j+1]=pv11;
//	                pivot[4][j+1]=pv22;
//	            }
//	        }        
        }
//        for(int i=0;i<tool.size();i++){
//            DrawTool dt = (DrawTool)tool.elementAt(i);
//            _cdm.setSubPacketData(dt.getPacketTitle(),pivot[i]);
//            //2014.03.18 by LYH << 지표 소수점 자리수 가격과 동일하게 처리.
//            if(_cdm.nTradeMulti>0)
//                _cdm.setSyncPriceFormat(dt.getPacketTitle());
//            else
//            {
//                ChartPacketDataModel cpdm =_cdm.getChartPacket("종가");
//                if(cpdm.getPacketFormat_Index() == 14)
//                    _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
//                else if(cpdm.getPacketFormat_Index() == 15)
//                    _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.001");
//                else if(cpdm.getPacketFormat_Index() == 16)
//                    _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.0001");
//
//            }
//            //2014.03.18 by LYH << 지표 소수점 자리수 가격과 동일하게 처리.
//        }
        for(int i=0;i<tool.size();i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            _cdm.setSubPacketData(dt.getPacketTitle(),pivot[i]);
            if(_cdm.nTradeMulti>0)
            {
                _cdm.setSyncPriceFormat(dt.getPacketTitle());
            }else {
                _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
            }
        }
        formulated = true;
    }
    public void reFormulateData(){
        formulated = false;	//2014. 1. 7 피봇 지표 넣고 실시간 수신시  죽는현상
        FormulateData();
        formulated = true;
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
            }catch(NullPointerException e){
                return;
            }
            t.plot(g,drawData);
        }
    }
    public void drawGraph_withSellPoint(Canvas g){
    }

    public String getName(){
        return "Pivot전봉기준";
    }
}