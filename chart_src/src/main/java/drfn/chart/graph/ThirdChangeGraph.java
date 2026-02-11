package drfn.chart.graph;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;
import drfn.chart.util.MinMax;


public class ThirdChangeGraph extends AbstractGraph{
	String[] datakind = {"종가"};//그래프에 사용될 데이터
	DrawTool dt; //그래프에 사용될 드로우툴
	int sub_margin;
	int[][] data;
	double[] endPrices;
	double[][] thirdData;
	double[][] thirdDataResult;
	int count = 0;
	public ThirdChangeGraph(ChartViewModel cvm, ChartDataModel cdm){
		super(cvm,cdm);
		definition="시간개념이 없는 비시계열 차트의 일종입니다.  또한 거래량도 반영되어 있지 않습니다.  매매시점 파악보다는 주가의 장기추세의 전환시점 확인용으로 사용됩니다.양전환시 매수, 음전환시 매도합니다.양선과 음선이 교차하여 나타나는 횡보장세에서는 매매를 유보합니다";
	}

	//===================================
	// 공식 계산 -- 데이터가 바뀌기 전까지는 한번만 한다
	//===================================
	public void FormulateData(){
		double[] closeData = _cdm.getSubPacketData("종가");
		if(closeData==null) return;
		int dLen = closeData.length;
		endPrices = new double[dLen];
		for(int i=0;i<dLen;i++){
			endPrices[i] = closeData[i];
		}
		DrawTool dt = (DrawTool)tool.elementAt(0);
		_cdm.setSubPacketData(dt.getPacketTitle(),endPrices);
		makeData(closeData);
		formulated=true;
	}
	private void makeData(double[] data){
        count =0;
        double firstData = 0;
        int indexV = 0;
        double compareData = 0;
        double[] minMax = new double[3];
        double[] TotMinMax = new double[2];

        int dLen = data.length;
        double[] openData = new double[dLen];
        double[] highData = new double[dLen];
        String[] dates = _cdm.getStringData("자료일자");
        String[] dateData = new String[dLen];
        double[] closeData = new double[dLen];

        //2021.08.10 by lyk - 삼선전환도 데이터 갯수 2개 미만일 경우 처리 >>
        if (dLen == 1) {
            double[] newData = new double[3];
            newData[0] = data[0];
            newData[1] = 0;
            newData[2] = 0;
            data = newData;

            String[] newDates = new String[3];
            newDates[0] = dates[0];
            newDates[1] = "";
            newDates[2] = "";
            dates = newDates;
        } else if (dLen == 2) {
            double[] newData = new double[3];
            newData[0] = data[0];
            newData[1] = data[1];
            newData[2] = 0;
            data = newData;

            String[] newDates = new String[3];
            newDates[0] = dates[0];
            newDates[1] = dates[1];
            newDates[2] = "";
            dates = newDates;
        }
        //2021.08.10 by lyk - 삼선전환도 데이터 갯수 2개 미만일 경우 처리 <<

        thirdData = new double[dLen][3];
        for(int i=0 ; i<dLen ; i++) {
            firstData = data[i+1] - data[i];
            if(firstData == 0)
                continue;
            else if(firstData > 0){
                thirdData[0][0] = 1;
                thirdData[0][1] = data[i];
                thirdData[0][2] = data[i+1];
            }else if(firstData < 0){
                thirdData[0][0] = -1;
                thirdData[0][1] = data[i+1];
                thirdData[0][2] = data[i];

            }
            indexV = i+1;
            TotMinMax[0]=thirdData[0][1];
            TotMinMax[1]=thirdData[0][2];
            openData[0] = thirdData[0][1];
            highData[0] = thirdData[0][2];
            dateData[0] = dates[indexV];
            closeData[0] = data[indexV];
            break;
        }
        for(int j=indexV ; j<dLen ; j++){
            if(thirdData[count][0] == 1){
                compareData = data[j] - thirdData[count][2];
                if(compareData == 0)continue;
                else if(compareData > 0){//up
                    thirdData[count+1][0] = 1;
                    thirdData[count+1][1] = thirdData[count][2];
                    thirdData[count+1][2] = data[j];
                }else if(compareData < 0){//down
                    minMax[0] = thirdData[count][1];
                    double mm = minMax[0];
                    if(count>0) { minMax[1] = thirdData[count-1][1];  mm = (minMax[0]<minMax[1]) ? minMax[0] : minMax[1]; }
                    if(count>1) { minMax[2] = thirdData[count-2][1];  mm = MinMax.getIntMinT(minMax); }
                    if( mm >  data[j]) {
                        thirdData[count+1][0] = -1;
                        thirdData[count+1][1] = data[j];
                        thirdData[count+1][2] = thirdData[count][1];
                    }else continue;
                }
                if(TotMinMax[0] > thirdData[count+1][1]) {
                    TotMinMax[0] = thirdData[count+1][1];
                }
                if(TotMinMax[1] < thirdData[count+1][2]) {
                    TotMinMax[1] = thirdData[count+1][2];
                }
                dateData[count+1] = dates[j];
                closeData[count+1] = data[j];
                openData[count+1] = thirdData[count+1][1];
                highData[count+1] = thirdData[count+1][2];
                count++;
            }else if(thirdData[count][0] == -1){
                compareData = thirdData[count][1] - data[j];
                if(compareData == 0)continue;
                if(compareData > 0){//down
                    thirdData[count+1][0] = -1;
                    thirdData[count+1][1] = data[j];
                    thirdData[count+1][2] = thirdData[count][1];
                }else if(compareData < 0){//up
                    minMax[0] = thirdData[count][2];
                    double mm = minMax[0];
                    if(count>0) { minMax[1] = thirdData[count-1][2];  mm = (minMax[0]>minMax[1])?minMax[0]:minMax[1]; }
                    if(count>1) { minMax[2] = thirdData[count-2][2];  mm = MinMax.getIntMaxT(minMax); }
                    if( mm < data[j]){
                        thirdData[count+1][0] = 1;
                        thirdData[count+1][1] = thirdData[count][2];
                        thirdData[count+1][2] = data[j];
                    }else continue;
                }
                if(TotMinMax[0] > thirdData[count+1][1]) {
                    TotMinMax[0] = thirdData[count+1][1];
                }
                if(TotMinMax[1] < thirdData[count+1][2]) {
                    TotMinMax[1] = thirdData[count+1][2];
                }
                dateData[count+1] = dates[j];
                closeData[count+1] = data[j];
                openData[count+1] = thirdData[count+1][1];
                highData[count+1] = thirdData[count+1][2];
                count++;
            }
        }
        count++;
        thirdDataResult = new double[count][3];
        System.arraycopy(thirdData, 0, thirdDataResult, 0, count);

        double[] openDataResult = new double[count];
        System.arraycopy(openData, 0, openDataResult, 0, count);

        double[] highDataResult = new double[count];
        System.arraycopy(highData, 0, highDataResult, 0, count);

        String[] dateDataResult = new String[count];
        System.arraycopy(dateData, 0, dateDataResult, 0, count);

        double[] closeDataResult = new double[count];
        System.arraycopy(closeData, 0, closeDataResult, 0, count);

        DrawTool t = (DrawTool)tool.get(0);
        _cdm.setSubPacketData(t.getPacketTitle(), TotMinMax);
        _cdm.setSubPacketData(t.getPacketTitle()+"_open", openDataResult);
        _cdm.setSubPacketData(t.getPacketTitle()+"_high", highDataResult);

        _cdm.setSubPacketData("variable_close", closeDataResult);
        _cdm.setSubPacketData("variable_자료일자", dateDataResult);
	}

	public void reFormulateData(){
		FormulateData();
		formulated = true;
	}
	public void drawGraph(Canvas gl){
		//if(!formulated)FormulateData();                       //저장되어 있지 않다면 계산을 새로 한다
		DrawTool t = (DrawTool)tool.elementAt(0);
		//System.out.println(" count : " + count);
//        int[][] data = new int[count][3];
//        System.arraycopy(thirdData, 0, data, 0, count);

		t.plot(gl, thirdDataResult);
	}
	public void drawGraph_withSellPoint(Canvas g){
	}

	public String getName(){
		return "ThirdChangeGraph";
	}
}