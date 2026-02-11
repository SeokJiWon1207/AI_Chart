package drfn.chart.graph;

import android.graphics.Canvas;

import drfn.chart.draw.BarDraw;
import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;
import drfn.chart.util.MinMax;

//대기매물
public class VolumeforSaleGraph extends AbstractGraph{
    int[][] data;
    int[] trade;
    int[] price1;//선으로 그릴때
    int[][] price0;//봉으로 그릴때
    int cnd=10;// 대기매물 구간
    double[] stand;//매물비율
    double[] stand_vol;//매물(거래량)
    double[] mm_data;
    public VolumeforSaleGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        definition="누적 거래량이 많은 가격대가 매물대라고 볼 수 있습니다.  주가가 매물대 위에 있으면, 매물대는 지지구간의 역할을 하고, 매물대 밑에 있으면 매물대는 저항구간의 역할을 하게 됩니다.누적 거래량이 적은 구간에서는 주가가 빠른 속도로 움직이는 것이 일반적인 현상입니다";
        m_strDefinitionHtml = "volumeforsale.html";	//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)
    }
    //----------------------------------------------
// 대기매물
//----------------------------------------------
    public void FormulateData() {
        double[] closeData = _cdm.getSubPacketData("종가");
        double[] volData = _cdm.getSubPacketData("기본거래량");
        if(closeData==null) return;
        int dLen = closeData.length;
        //2012.7.3  대기매물(매물대) 갯수조정
        cnd = interval[0];

        //2013.04.01 by LYH >> 대기매물 개선 <<
        int num=_cvm.getViewNum()+_cvm.futureMargin;        //화면에 그릴 데이터 수
        int index=_cvm.getIndex();        //화면에 그리기 시작할 인덱스
        if(index>=closeData.length) return;
        int margin=_cdm.getMargine();     //전체 데이터 마진
        int mar_index = margin-(dLen-index);
        if(mar_index<=0) mar_index=0;
        //2013.04.01 by LYH >> 대기매물 개선
        int nTotCnt = _cdm.getCount();
        if(index+num>nTotCnt)
            num = nTotCnt-index;
        //2013.04.01 by LYH <<

        //=====================================
        //  대기매물 계산
        //=====================================
        mm_data = new double[2];//최대 최소값
        mm_data[0] = MinMax.getRangeMin(closeData, index+num, num);
        mm_data[1] = MinMax.getRangeMax(closeData, index+num, num);

        double gab = ((double)mm_data[1]-(double)mm_data[0])/(double)cnd;//가격구간
        stand = new double[cnd];//대기매물
        stand_vol = new double[cnd];
        float sum_trade=0;// 총 거래량의 합
        double[] price=new double[cnd+1];
        for(int i=0;i<cnd+1;i++){
            //price[i]=(mm_data[0]+(gab*i)+0.4);
            price[i]=(mm_data[0]+(gab*i));
//                stand[i]=0;
        }
        price[cnd] = mm_data[1]+1;

        for(int i=index;i<index+num;i++){
            for(int j=0;j<cnd;j++){
                try {
                    if(closeData[i]>=price[j]&&closeData[i]<price[j+1]){
                        stand[j] +=volData[i];
                        sum_trade+=volData[i];
                        break;
                    }
                } catch(Exception e) {
//                		System.out.println(e.getMessage());
                }
            }
        }
        for(int i=0;i<cnd;i++){
//            stand_vol[i]=(stand[i]/1000);
            stand_vol[i]=(stand[i]);
        }
        for(int i=0;i<cnd;i++){
            stand[i] =  ((stand[i]*100/sum_trade));
        }
        for(int i=0;i<tool.size();i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            switch(i){
                case 0:
                    _cdm.setSubPacketData(dt.getPacketTitle(),stand);
                    _cdm.setSubPacketData(dt.getPacketTitle()+"거래량",stand_vol);
                    break;
//                    case 1:
//                        _cdm.setSubPacketData(dt.getPacketTitle(),price1);
//                    break;
            }

        }
        formulated = true;
    }
    public void reFormulateData(){
        FormulateData();
        formulated = true;
    }
    public void drawGraph(Canvas gl){
//        int num=_cvm.getViewNum();        //화면에 그릴 데이터 수
//        int index=_cvm.getIndex();        //화면에 그리기 시작할 인스
//        if(index<0)index=0;
        for(int i=0;i<tool.size();i++){
            DrawTool t=(DrawTool)tool.elementAt(i);
            if(i==0) {
                FormulateData();
                t.plotVolumeForSale(gl,stand);
            }
            else t.drawVolumeForSale(gl,_cdm.getSubPacketData(t.getPacketTitle()));
        }
    }
    public void drawGraph_withSellPoint(Canvas g){
    }

    //2021.11.10 by LYH - 매물대 차트 타이틀 봉 위에 보이도록 수정 >>
    public void drawVolumeForScaleTitle(Canvas gl)
    {
        for(int i=0;i<tool.size();i++){
            BarDraw t=(BarDraw)tool.elementAt(i);
            if(i==0) {
                t.drawVolumeForSale(gl,stand,false);
            }
        }
    }
    //2021.11.10 by LYH - 매물대 차트 타이틀 봉 위에 보이도록 수정 <<

    public String getName(){
        return "대기매물";
    }
}