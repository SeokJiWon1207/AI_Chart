package drfn.chart.graph;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;
/**
 * 가격 이동평균 그래프
 */
public class EnvelopeGraph extends AbstractGraph{
    String[] datakind = {"종가"};
    int[][] data;
    double[] ma; //중심선
    double[] ma1;//상한선
    double[] ma2;//하한선
    //double m = 0.06;
    public EnvelopeGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        definition="3개의 밴드를 매매시점으로 잡을 수 있다. 주가가 어떤 밴드를 돌파하면 돌파된 밴드는 지지선 역할을, 상위밴드는 저항선의 역할을 한다. Envelope보다는 Bollinger Bands가 더욱 발전된 지표이므로 Bollinger Bands를 활용하는 것이 좋다";
        m_strDefinitionHtml = "Envelope.html";	//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)
    }
    //=====================================
    //  중심선  :  주가 n일 이동 평균
    //  상한선  :  주가 n일 이동 평균 *(1+m(%) )
    //  하한선  :  주가 n일 이동 평균 *(1-m(%) )               
    //                      n = 25일, m=6%(0.06)
    //=====================================
    public void FormulateData(){
//      data = getData(1);
  	double[] closeData = _cdm.getSubPacketData("종가");
  	if(closeData==null) return;
      int dLen = closeData.length;
      ma1 = new double[dLen];
      ma2 = new double[dLen];
      double m=((double)interval[1]/100)/100.;
      double m1=((double)interval[2]/100)/100.;
      ma = makeAverage(closeData,interval[0]);
      for(int i=0;i<dLen;i++){
      	if(i<interval[0]) {
      		ma1[i]=0;
      		ma2[i]=0;
      	}
          ma1[i] = (ma[i]*(1.+m));
          ma2[i] = (ma[i]*(1.-m1));
      }
      for(int i=0;i<tool.size();i++){
          DrawTool dt = (DrawTool)tool.elementAt(i);
          switch(i){
              case 0:
                  _cdm.setSubPacketData(dt.getPacketTitle(),ma1);
              break;
              case 1:
                  _cdm.setSubPacketData(dt.getPacketTitle(),ma);
              break;
              case 2:
                  _cdm.setSubPacketData(dt.getPacketTitle(),ma2);
              break;
          }
          //2014.03.18 by LYH >> 지표 소수점 자리수 가격과 동일하게 처리.
          if(_cdm.nTradeMulti>0)
              _cdm.setSyncPriceFormat(dt.getPacketTitle());
          else
              _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
          //2014.03.18 by LYH << 지표 소수점 자리수 가격과 동일하게 처리.
      }
      formulated = true;
  }
//    public void FormulateData(){
////        data = getData(1);
//        double[] closeData = _cdm.getSubPacketData("종가");
//        if(closeData==null) return;
//        int dLen = closeData.length;
//        ma1 = new double[dLen];
//        ma2 = new double[dLen];
//        double m=((double)interval[1])/100.;
//        double m2=((double)interval[2])/100.;
//        ma = makeAverage(closeData,interval[0]);
//        for(int i=0;i<dLen;i++){
//            if(i<interval[0]) {
//                ma1[i]=0;
//                ma2[i]=0;
//            }
//            ma1[i] = (ma[i]*(1.+m));
//            //ma2[i] = (ma[i]*(1.-m));
//            ma2[i] = (ma[i]*(1.-m2));
//        }
//        for(int i=0;i<tool.size();i++){
//            DrawTool dt = (DrawTool)tool.elementAt(i);
//            switch(i){
//                case 0:
//                    _cdm.setSubPacketData(dt.getPacketTitle(),ma1);
//                    break;
//                case 1:
//                    _cdm.setSubPacketData(dt.getPacketTitle(),ma);
//                    break;
//                case 2:
//                    _cdm.setSubPacketData(dt.getPacketTitle(),ma2);
//                    break;
//            }
//            //2014.03.18 by LYH >> 지표 소수점 자리수 가격과 동일하게 처리.
//            if(_cdm.nTradeMulti>0)
//                _cdm.setSyncPriceFormat(dt.getPacketTitle());
//            else
//                _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
//            //2014.03.18 by LYH << 지표 소수점 자리수 가격과 동일하게 처리.
//
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
        for(int i=0;i<tool.size();i++){
            DrawTool t=(DrawTool)tool.elementAt(i);
            try{
                drawData=_cdm.getSubPacketData(t.getPacketTitle());
            }catch(ArrayIndexOutOfBoundsException e){
                return;
            }
            _cvm.useJipyoSign=false;
            t.plot(g,drawData);
        }
    }
    public void drawGraph_withSellPoint(Canvas g){
    }

    public String getName(){
        return "Envelope";
    }
}