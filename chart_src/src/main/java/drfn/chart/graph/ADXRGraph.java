package drfn.chart.graph;

import android.graphics.Canvas;

import java.util.Vector;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartViewModel;
import drfn.chart.util.MinMax;

public class ADXRGraph extends AbstractGraph{
	double[] pdiData;
	double[] ndiData;
	double[] adxData;
    double[] adxrData;
    int[][] data;
    Vector<DrawTool> tool;
    //int[] base=null;
    
    public ADXRGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
        String[] datakind = {"고가","저가","종가"};
        _dataKind = datakind;
        tool = getDrawTool();
        definition="ADX는 DMI 지표와 함께 사용되는 지표로 DMI지표에서 많이 나타나는 Whipsaw 현상(톱니현상 : 큰 변동성 없이 잦은 등락을 보이는 현상)을 제거하여 현재 진행중인 추세의 상대적 강도를 판단할 수 있는 지표이다. ";

        m_strDefinitionHtml = "adxr.html";    //2018.05.02 by lyj 보조지표 설명/활용법 추가(상세설정창)
    }
    public void FormulateData(){
    	double[] highData = _cdm.getSubPacketData("고가");
    	double[] lowData = _cdm.getSubPacketData("저가");
    	double[] closeData = _cdm.getSubPacketData("종가");
    	if(closeData==null) return;
        int dLen = closeData.length;
	    pdiData = new double[dLen];
        ndiData = new double[dLen];
        adxData = new double[dLen];
        adxrData = new double[dLen];
        double temp[] = new double[3];
        double tr[] = new double[dLen];
        for(int i=1; i<dLen; i++){
            if( (highData[i] > highData[i-1]) && ( (highData[i] - highData[i-1]) > (lowData[i-1] - lowData[i]))) //오늘의 고가가 어제의 고가보다 크면 양의값
                pdiData[i] = highData[i]- highData[i-1];
            else                           //작거나 같으면 0값
                pdiData[i] = 0;

            if(lowData[i] < lowData[i-1]  && ( (highData[i] - highData[i-1]) < (lowData[i-1] - lowData[i])))
                ndiData[i] = lowData[i-1]- lowData[i];
            else
                ndiData[i] = 0;
            
            temp[0] = Math.abs(highData[i]-lowData[i]);
            temp[1] = Math.abs(closeData[i-1]-highData[i]);
            temp[2] = Math.abs(closeData[i-1]-lowData[i]);

            tr[i] = MinMax.getDoubleMaxT(temp);
        }
        pdiData = exponentialAverage(pdiData, interval[0]);
        ndiData = exponentialAverage(ndiData, interval[0]);
        tr = exponentialAverage(tr, interval[0]);
        for(int i=0; i<dLen; i++){
            if(i<interval[0]-1){
            	pdiData[i] = 0;
			    ndiData[i] = 0;
			    adxData[i] = 0;
                adxrData[i] = 0;
			    continue;
    		}
            if(tr[i] != 0) {
                pdiData[i] = (pdiData[i] / tr[i] * 100);
                ndiData[i] = (ndiData[i] / tr[i] * 100);
                if ((pdiData[i] + ndiData[i]) != 0) {
                    adxData[i] = Math.abs(pdiData[i] - ndiData[i]) / (pdiData[i] + ndiData[i]) * 100;
                }
            }
        }
        adxData = exponentialAverage(adxData, interval[0]);
//        adxrData = exponentialAverage(adxData, interval[0]);
        for(int i=0; i<dLen; i++){
            if(i<interval[0]*2+interval[1]-1) {
                adxrData[i] = 0;
                continue;
            }

            adxrData[i] = (adxData[i] + adxData[i-interval[1]])/2;
        }

	    for(int i=0;i<2;i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            if(i==0)_cdm.setSubPacketData(dt.getPacketTitle(),adxData);
            else if(i==1) _cdm.setSubPacketData(dt.getPacketTitle(),adxrData);
            _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
        }
	    formulated = true;
    }
    public void reFormulateData() {
        FormulateData();
        formulated = true;
    }
    public void drawGraph(Canvas g){
        if(!formulated)FormulateData();                       //저장되어 있지 않다면 계산을 새로 한다
        
        double[] drawData=null;
        for(int i=0;i<2;i++){
            DrawTool t=(DrawTool)tool.elementAt(i);
            try{
                drawData=_cdm.getSubPacketData(t.getPacketTitle());
            }catch(ArrayIndexOutOfBoundsException e){
                return;
            }
            
            if(i==0) _cvm.useJipyoSign=true;
            else _cvm.useJipyoSign=false;

            t.plot(g,drawData);
            if(base!=null&&i<base.length)t.draw(g,base[i]);
        }
        
        //2017. 3. 21 지표마다 기준선 설정 추가>>
        drawBaseLine(g);
        //2017. 3. 21 지표마다 기준선 설정 추가>>

//        //2017. 3. 21 매매 신호 보기 기능 추가>>
//        DrawTool t=(DrawTool)tool.elementAt(0);
//        if(isSellingSignalShow)
//            t.drawSignal(g, pdiData, ndiData);
//        //2017. 3. 21 매매 신호 보기 기능 추가<<
    }
    public void drawGraph_withSellPoint(Canvas g){
    }
    public String getName(){
        return "ADXR";
    }
}
