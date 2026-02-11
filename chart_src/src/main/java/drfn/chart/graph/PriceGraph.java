package drfn.chart.graph;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartPacketDataModel;
import drfn.chart.model.ChartViewModel;

public class PriceGraph extends AbstractGraph{
    String[] datakind = {"종가"};//그래프에 사용될 데이터
    DrawTool dt; //그래프에 사용될 드로우툴
    int sub_margin;
    double[][] data;
    double[] stand={0};
    public boolean m_bUseJipyoSign = true;
    public PriceGraph(ChartViewModel cvm, ChartDataModel cdm){
        super(cvm,cdm);
    }

    //===================================
    // 공식 계산 -- 데이터가 바뀌기 전까지는 한번만 한다
    //===================================
    public void FormulateData(){
        data = getData(1);
        int dLen = data.length;
        double[] price= new double[dLen];
        for(int i=0;i<dLen;i++){
            price[i] = data[i][0];
        }
        for(int i=0;i<tool.size();i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            _cdm.setSubPacketData(dt.getPacketTitle(),price);
            int format=_cdm.getDataFormat(_dataKind[i]);
            //2015. 2. 9 선옵 1분선 소숫점 처리>>
            if(dt.getPacketTitle().equals("미결제약정"))
                format = 11;
            //2015. 2. 9 선옵 1분선 소숫점 처리<<
            ChartPacketDataModel cpdm =_cdm.getChartPacket(dt.getPacketTitle());
            cpdm.setPacketFormat(format);
        }
        formulated=true;
    }
    public void reFormulateData() {
        //data = getData(1);
        FormulateData();
        /*
        for(int i=0;i<tool.size();i++){
            DrawTool dt = (DrawTool)tool.elementAt(i);
            _cdm.addSubPacketData(dt._dtvm.getPacketTitle(),data[data.length-1][0]);
            //_cdm.changeSubPacketData(dt._dtvm.getPacketTitle(),data[data.length-1][0]);
        }*/
        formulated=true;
    }
    public void drawGraph(Canvas gl){
        double[] drawData=null;
        //stand[0] = _cdm.getPrevData();
        for(int i=0;i<tool.size();i++){
            DrawTool t=(DrawTool)tool.elementAt(i);
            try{
                drawData=_cdm.getSubPacketData(t.getPacketTitle());;
            }catch(ArrayIndexOutOfBoundsException e){
                return;
            }catch(NullPointerException e){
                return;
            }
            _cvm.useJipyoSign = m_bUseJipyoSign;
            String view = t.getViewTitle();
            if(view!=null&&view.equals(""))return;
            if(stand[0]!=0)t.plot(gl,drawData,stand);
            else t.plot(gl,drawData);
        }
    }
    public void drawGraph_withSellPoint(Canvas g){
    }

    public String getName(){
        return "가격차트(선)";
    }
}