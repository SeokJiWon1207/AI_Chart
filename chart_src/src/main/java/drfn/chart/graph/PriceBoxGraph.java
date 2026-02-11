package drfn.chart.graph;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;
/**
 * 가격 이동평균 그래프
 */
public class PriceBoxGraph extends AbstractGraph{
    String[] datakind = {"종가"};
    int[][] data;
    double[] ma; //중심선
    double[] ma1;//상한선
    double[] tmp_ma1;
    double[] ma2;//하한선
    double[] tmp_ma2;
    double[] upstdV;//표준편차
    double[] upstdV2;//표준편차
    double[] maUpper;//상한선
    double[] maLower;//하한선
    //double m = 0.06;
    public PriceBoxGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        definition="3개의 밴드를 매매시점으로 잡을 수 있다. 주가가 어떤 밴드를 돌파하면 돌파된 밴드는 지지선 역할을, 상위밴드는 저항선의 역할을 한다. Envelope보다는 Bollinger Bands가 더욱 발전된 지표이므로 Bollinger Bands를 활용하는 것이 좋다";
        m_strDefinitionHtml = "price___box.html";	//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)
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

        maUpper = new double[dLen];
        maLower = new double[dLen];
        int nLen = interval[0];
        double m=((double)nLen)/100.;
        double[] ma = makeAverage(closeData,nLen);

        int start = interval[0];
        double dValue = 0.0;
        double dSumUpper = 0.0, dSumSqrUpper = 0.0;
        double dSumLower = 0.0, dSumSqrLower = 0.0;
        int nPlus = 0;
        int nMinus = 0;

        if(start < dLen) {
            for(int i=0;i<dLen;i++){
                if(i<start-1) {		//2019.01.28 by sdm >> 가격 Box지표 HTS와 안맞는 부분 iOS 수정건과 맞춤 (-1 추)
                    maUpper[i]=0;
                    maLower[i]=0;
                }
            }

            for(int i=start-1;i<dLen;i++){	//2019.01.28 by sdm >> 가격 Box지표 HTS와 안맞는 부분 iOS 수정건과 맞춤 (-1 추)
                //BoxUpper
                dValue = closeData[i] - ma[i];
                //if(dValue >= 0.0) { //가격 Box지표 HTS와 안맞는 부분 iOS와 소스가 달라서 수정
                if(dValue > 0.0) {
                    dSumUpper += dValue;
                    dSumSqrUpper += dValue * dValue;
                    ++nPlus;
                }
                else if(dValue < 0.0) {
                    dSumLower += dValue;
                    dSumSqrLower += dValue * dValue;
                    ++nMinus;
                }
            }

            //2019.01.28 by sdm >> 가격 Box지표 HTS와 안맞는 부분 iOS 수정건과 맞춤 Start
            if(nPlus>0 && nMinus>0)
            {
                double denom = nPlus * nPlus;
                double dSigUpper = Math.sqrt((nPlus * dSumSqrUpper - dSumUpper * dSumUpper) / denom) * 2;
                denom = nMinus * nMinus;
                double dSigLower = Math.sqrt((nMinus * dSumSqrLower - dSumLower * dSumLower) / denom) * 2;

                double dAvgUpper = dSumUpper / (double)nPlus + dSigUpper;
                double dAvgLower = dSumLower / (double)nMinus - dSigLower;

                for(int i=start-1;i<dLen;i++){		//2019.01.28 by sdm >> 가격 Box지표 HTS와 안맞는 부분 iOS 수정건과 맞춤 (-1 추)
                    maUpper[i] = ma[i] + dAvgUpper;
                    maLower[i] = ma[i] + dAvgLower;
                }
            }
        }

        for(int i=0;i<tool.size();i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            switch(i){
                case 0:
                    _cdm.setSubPacketData(dt.getPacketTitle(),maUpper);
                    break;
                case 1:
                    _cdm.setSubPacketData(dt.getPacketTitle(),ma);
                    break;
                case 2:
                    _cdm.setSubPacketData(dt.getPacketTitle(),maLower);
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
    
    double getMean(double[] data)
    {
    	int size = data.length;
        double sum = 0.0;
        for(double a : data)
            sum += a;
        return sum/size;
    }

    double getVariance(double[] data, double var)
    {
    	int size = data.length;
        double mean = getMean(data);
        double temp = 0;
        for(double a :data)
            temp += (a-mean)*(a-mean);
        return temp/size;
    }

    double getStdDev(double[] data, double var)
    {
        return Math.sqrt(getVariance(data, var));
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
  //============================================
    // StDev :   표준편차
    //
    //============================================
    //============================
    // 표준편차를 구하는 메쏘드
    //============================
    public double[] getStandardDeviation(double uppermean, double[] average, int interval) {
        if( (average == null) || (average.length < interval)) {
            return null;
        }
        int dLen = average.length;
        double[] stDevia = new double[dLen];
        
        for(int i = interval-1 ; i<dLen ; i++) {
            double[] deviation = new double[dLen];
            for(int j= i ; j>i-interval ; j--) {
                deviation[i] += Math.pow( (average[i]-uppermean), 2);
            }
            stDevia[i] = Math.sqrt(deviation[i]/(interval));
        }
        return stDevia;
    }   
    public double[] getStandardDeviation(double[] data, int interval) {
        if( (data == null) || (data.length < interval)) {
            return null;
        }
        int dLen = data.length;
        double[] stDevia = new double[dLen];

        for(int i = interval-1 ; i<dLen ; i++) {
            double[] deviation = new double[dLen];
            for(int j= i ; j>i-interval ; j--) {
                deviation[i] += Math.pow( (data[j]), 2);
            }
            stDevia[i] = Math.sqrt(deviation[i]/(interval));
        }
        return stDevia;
    }
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
        return "가격 & Box";
    }
}