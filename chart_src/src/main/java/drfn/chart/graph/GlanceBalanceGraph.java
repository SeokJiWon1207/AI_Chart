package drfn.chart.graph;

import android.graphics.Canvas;

import java.util.Vector;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartPacketDataModel;
import drfn.chart.model.ChartViewModel;
import drfn.chart.util.MinMax;

/**
 * 가격 이동평균 그래프
 */
public class GlanceBalanceGraph extends AbstractGraph{
    //고가,저가,종가
    double[] toData;
    double[] toData1;
    double[] toData2;
    double[] toData3;
    double[] toData4;
    int[][] data =null;
    Vector<DrawTool> tool;
    int dLen = 0;
    public GlanceBalanceGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        String[] datakind = {"고가","저가","종가"};
        _dataKind = datakind;
        tool = getDrawTool();
        definition="[기준선과 전환선 분석] 전환선이 기준선 위에 위치하면 매수국면, 아래에 위치하면 매도국면, 단, 기준선의 추세가 상승추세이면 매도보류. [기준선과 후행스팬 분석]기준선이 상승추세일 때 후행스팬이 주가를 상향돌파하면 강세국면으로 전환가능성, 돌파에 실패시 약세국면이 강화됨. [주가와 선행스팬 분석]주가가 선행스팬사이의 구름층을 상향돌파하면 강세국면으로 전환, 구름층의 두께가 지지 또는 저항의 강도라고 볼 수 있음";
        m_strDefinitionHtml = "OneEyeBalance.html";	//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)
    }
    public void FormulateData(){
//        data = getData(1);
        double[] highData = _cdm.getSubPacketData("고가");
        double[] lowData = _cdm.getSubPacketData("저가");
        double[] closeData = _cdm.getSubPacketData("종가");
        if(closeData==null) return;
        dLen = closeData.length;

        toData = makeHighLowAverage(highData, lowData, interval[1]);//전환선
        toData1 = makeHighLowAverage(highData, lowData, interval[0]);//기준선
        //2017.01.03 by LYH >> 일목균형 선행스팬1, 2 타이틀 개선
  //      toData4 = span(toData, toData1);
        toData3 = span(toData, toData1);
        //2017.01.03 by LYH >> 일목균형 선행스팬1, 2 타이틀 개선 end

        toData2 = preMove(closeData, interval[4]);//후행스팬
        //2017.01.03 by LYH >> 일목균형 선행스팬1, 2 타이틀 개선
//        toData3 = makeHighLowAverage(highData, lowData, interval[3]);
//        toData3 = foreMove(toData3, interval[2]);//선행스팬2
//        toData4 = foreMove(toData4, interval[2]);//선행스팬1
        toData4 = makeHighLowAverage(highData, lowData, interval[3]);
        toData4 = foreMove(toData4, interval[2]);//선행스팬1
        toData3 = foreMove(toData3, interval[2]);//선행스팬2
        //2017.01.03 by LYH >> 일목균형 선행스팬1, 2 타이틀 개선 end
        //for(int i=0;i<toData4.length;i++)System.out.println(".... toData4 " + i + "   " + toData4[i]);
        ChartPacketDataModel cpdm =_cdm.getChartPacket("종가");

        for(int i=0;i<tool.size();i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            switch(i){
                case 0:
                    _cdm.setSubPacketData(dt.getPacketTitle(),toData);
                    break;
                case 1:
                    _cdm.setSubPacketData(dt.getPacketTitle(),toData1);
                    break;
                case 2:
                    _cdm.setSubPacketData(dt.getPacketTitle(),toData2);
                    break;
                //2017.01.03 by LYH >> 일목균형 선행스팬1, 2 타이틀 개선
//                case 3:
//                    _cdm.setSubPacketData(dt.getPacketTitle(),toData4);
//                    break;
//                //2016.07.28 by LYH >> 일목균형 선행스팬1, 선행스팬2 라인 굵기 색상 적용.
//                case 4:
//                    _cdm.setSubPacketData(dt.getPacketTitle(),toData3);
//                    break;
//                //2016.07.28 by LYH << 일목균형 선행스팬1, 선행스팬2 라인 굵기 색상 적용.
                case 3:
                    _cdm.setSubPacketData(dt.getPacketTitle(),toData3);
                    break;
                //2016.07.28 by LYH >> 일목균형 선행스팬1, 선행스팬2 라인 굵기 색상 적용.
                case 4:
                    _cdm.setSubPacketData(dt.getPacketTitle(),toData4);
                    break;
                //2016.07.28 by LYH << 일목균형 선행스팬1, 선행스팬2 라인 굵기 색상 적용.
                //2017.01.03 by LYH >> 일목균형 선행스팬1, 2 타이틀 개선 end
            }
            //2014.03.18 by LYH >> 지표 소수점 자리수 가격과 동일하게 처리.
            if(_cdm.nTradeMulti>0)
                _cdm.setSyncPriceFormat(dt.getPacketTitle());
            else
            {
                if(cpdm.getPacketFormat_Index() == 14)
                    _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
                else if(cpdm.getPacketFormat_Index() == 15)
                    _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.001");
                else if(cpdm.getPacketFormat_Index() == 16)
                    _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.0001");
            }
            //2014.03.18 by LYH << 지표 소수점 자리수 가격과 동일하게 처리.
        }
        //2017.01.03 by LYH >> 일목균형 선행스팬1, 2 타이틀 개선
        //_cdm.setSubPacketData("toData4",toData3);
//        if(cpdm.getPacketFormat_Index() == 14)
//            _cdm.setPacketFormat("toData4", "× 0.01");
//        else if(cpdm.getPacketFormat_Index() == 15)
//            _cdm.setPacketFormat("toData4", "× 0.001");
//        else if(cpdm.getPacketFormat_Index() == 16)
//            _cdm.setPacketFormat("toData4", "× 0.0001");
        //2017.01.03 by LYH >> 일목균형 선행스팬1, 2 타이틀 개선 end
        formulated = true;
    }
    public void reFormulateData() {
        FormulateData();
        formulated = true;
    }
    public void drawGraph(Canvas gl){
//        int num=_cvm.getViewNum();        //화면에 그릴 데이터 수
        int index=_cvm.getIndex();        //화면에 그리기 시작할 인덱스
        //2013.10.07 by LYH >> 일목균형 과거 데이터로 스크롤 시 전환선등 지워지는 오류 수정.      
        //2024.01.03 by LYH - 일목균형표 줌인, 줌아웃, 스크롤 개선 >>
//        int dataLen = index + _cvm.getViewNum() + _cvm.futureMargin;
        int dataLen = index + _cvm.getViewNum();
        //2024.01.03 by LYH - 일목균형표 줌인, 줌아웃, 스크롤 개선 <<
        int nTotCnt = dLen;
        if(dataLen>nTotCnt)
            dataLen = nTotCnt;
        int num = dataLen-index;

//        int margin=_cdm.getMargine();     //전체 데이터 마진
//        int mar_index = margin-(dLen-index);
//        if(mar_index<=0) mar_index=0;
        int mar_index = 0;
        //2013.10.07 by LYH <<
        double[] drawData=null;

        for(int i=0;i<tool.size();i++){
            DrawTool t=(DrawTool)tool.elementAt(i);
            //2020.07.06 by LYH >> 캔들볼륨 >>
//            try{
//                drawData=_cdm.getSubPacketData(t.getPacketTitle(),index,num,mar_index);
//            }catch(ArrayIndexOutOfBoundsException e){
//                return;
//            }
//            _cvm.useJipyoSign=false;
//            t.plotDefault(gl,drawData);
            drawData=_cdm.getSubPacketData(t.getPacketTitle());
            _cvm.useJipyoSign=false;
            t.plot(gl,drawData);
            //2020.07.06 by LYH >> 캔들볼륨 <<
        }
        //2013.10.07 by LYH >> 일목균형 과거 데이터로 스크롤 시 전환선등 지워지는 오류 수정.
        num=_cvm.getViewNum();
        //2013.10.07 by LYH >> 일목균형 과거 데이터로 스크롤 시 전환선등 지워지는 오류 수정.
        DrawTool t=(DrawTool)tool.elementAt(3);
        //2024.01.03 by LYH - 일목균형표 줌인, 줌아웃, 스크롤 개선 >>
//        double[] data = _cdm.getSubPacketData(t.getPacketTitle(),index,num+interval[2]-1,mar_index);
//        double[] stand = _cdm.getSubPacketData("선행스팬2",index,num+interval[2]-1,mar_index);  //2017.01.03 by LYH >> 일목균형 선행스팬1, 2 타이틀 개선
        double[] data = _cdm.getSubPacketData(t.getPacketTitle(),index,num,mar_index);
        double[] stand = _cdm.getSubPacketData("선행스팬2",index,num,mar_index);  //2017.01.03 by LYH >> 일목균형 선행스팬1, 2 타이틀 개선
        //2024.01.03 by LYH - 일목균형표 줌인, 줌아웃, 스크롤 개선 <<
        t.plot(gl, data, stand);
        //2016.07.28 by LYH >> 일목균형 선행스팬1, 선행스팬2 라인 굵기 색상 적용.
        if(tool.size()>4)
        {
            t=(DrawTool)tool.elementAt(4);
            t.plot(gl, data, stand);
        }
        //2016.07.28 by LYH << 일목균형 선행스팬1, 선행스팬2 라인 굵기 색상 적용.
    }
    public void drawGraph_withSellPoint(Canvas g){
    }
    public String getName(){
        return "일목균형표";
    }

    public double[] makeHighLowAverage(double[] high, double[] low, int interval){
        int dLen = high.length;
        double[] averageData = new double[dLen];

        double max, min;
//        for(int i = interval ; i < dLen ; i++) {
        for(int i = interval-1 ; i < dLen ; i++) {
            max = MinMax.getRangeMax(high, i+1, interval);
            min = MinMax.getRangeMin(low, i+1, interval);

            averageData[i] = ((max+min)/2);
        }
        return averageData;
    }
    public double[] preMove(double[] data, int interval){
        int dLen = data.length;
        double[] averageData = new double[dLen];
        interval=interval-1;//LG 데이터와 맞추기위해
        for(int i = interval ; i < dLen ; i++) {
            averageData[i-interval]=data[i];
        }
        return averageData;
    }
    public double[] foreMove(double[] data, int interval){
        int dLen = data.length;
        double[] averageData = new double[dLen+interval];
        System.arraycopy(data,0,averageData,interval-1,dLen);
        /*
        interval = interval-1;
        for(int i = interval ; i < averageData.length ; i++) {
            averageData[i]=data[i-interval-1];
        }
        */
        return averageData;
    }

    public double[] span(double[] data1, double[] data2){
        int dLen = data1.length;
        double[] averageData = new double[data1.length];
        for(int i = 0 ; i < dLen ; i++) {
            if(data1[i]!=0){
                if(data2[i]==0)averageData[i]=0;//data1[i];
                else averageData[i]=(data1[i]+data2[i])/2;
            }
        }
        return averageData;
    }

    public void destroy(){
        super.destroy();
        _cvm.futureMargin = 0;
        toData=null;
        toData1=null;
        toData2=null;
        toData3=null;
        toData4=null;
    }
}