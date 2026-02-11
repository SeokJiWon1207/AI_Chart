package drfn.chart.graph;

import android.graphics.Canvas;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartPacketDataModel;
import drfn.chart.model.ChartViewModel;

/**
 * Pivot그래프
 */
public class PivotGraph extends AbstractGraph {
    int[][] data;
    int dLen = 0;

    public PivotGraph(ChartViewModel cvm, ChartDataModel cdm) {
        super(cvm, cdm);

        m_strDefinitionHtml = "Pivot.html";    //2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)
    }

    //=======================================
    // 피봇가 = (고가 + 저가 + 종가)/3
    // 피봇 1차 저항(r1)=(2*피봇가)-저가
    // 피봇 2차 저항(r2)=피봇+고가-저가
    // 피봇 1차 지지(s1)=(2*피봇가)-고가
    // 피봇 2차 지지(s2)= 피봇-고가+저가
    // ----> LG식으로 변경 2002/04/18
    //=======================================
    public void FormulateData() {
        //주가 분데이터는 서버로부터 피봇가를 받아온다
        double[][] pivot = null;
        if (!formulated) {
            if (_cdm.getDateType() == 4 && _cdm.hasPivotData()) {
                String[] datatitle = {"피봇2차저항", "피봇1차저항", "피봇가", "피봇1차지지", "피봇2차지지"};
                setDatakind(datatitle);
            }
            else {
                String[] datatitle = {"고가", "저가", "종가"};
                setDatakind(datatitle);
            }
            double[] highData = _cdm.getSubPacketData("고가");
            double[] lowData = _cdm.getSubPacketData("저가");
            double[] closeData = _cdm.getSubPacketData("종가");

            //2015.04.03 by lyk - 피봇수정
            String[] dates = _cdm.getStringData("자료일자");
            ChartPacketDataModel cpdmDate = _cdm.getChartPacket("자료일자");
            //2015.04.03 by lyk - 피봇수정 end

            if (closeData == null) return;
            dLen = closeData.length;

            pivot = new double[7][dLen];

            //2015.04.03 by lyk - 피봇수정
            double pv   = 0;    //피봇
            double pv1  = 0;    //1차 저항선
            double pv11 = 0;    //1차 지지선
            double pv2  = 0;    //2차 저항
            double pv22 = 0;    //2차 지지
            double pv3  = 0;    //3차 저항
            double pv33 = 0;    //3차 지지

            boolean isDateChange = false;
            double dPreDayHigh = 0;
            double dPreDayLow = 0;
            double dPreDayClose = 0;
            for (int j = 0; j < dLen - 1; j++) {
                if (j == 0) {
                    for (int i = 0; i < 7; i++) {
                        pivot[i][j] = 0;
                    }
                }
                //날짜를 비교하여 다르기 전까지 고,저,종가의 minmax(makeBong)값을 계산한다.
                //날짜가 다르면 계산된 고,저,종가 값을 이용하여 피봇값을 계산한 후 대입한다.
                //날짜타입에 따라 변경해줌 (날짜 얻는 법)
                if (cpdmDate != null) {
                    String date1 = cpdmDate.getDate(dates[j + 1]);
                    String date2 = cpdmDate.getDate(dates[j]);
                    if (_cdm.getDateType() == 1 || _cdm.getDateType() == 2 || _cdm.getDateType() == 3) {
                        date1 = dates[j + 1];
                        date2 = dates[j];
                    }
                    if (!date1.equals(date2)) {
                        isDateChange = true;
                    }
                    else
                        isDateChange = false;
                }

                this.makeBongData(String.valueOf(highData[j]), String.valueOf(lowData[j]), String.valueOf(closeData[j]));
                if (isDateChange) {
                    if (basic_data != null) {
                        dPreDayHigh = Double.parseDouble(basic_data[0]);
                        dPreDayLow = Double.parseDouble(basic_data[1]);
                        dPreDayClose = Double.parseDouble(basic_data[2]);
                    }
                    pv   = (dPreDayHigh + dPreDayLow + dPreDayClose) / 3;   //피봇
                    pv1  = 2 * (pv) - dPreDayLow;                           //1차 저항선
                    pv11 = 2 * (pv) - dPreDayHigh;                          //1차 지지선
                    pv2  = pv + dPreDayHigh - dPreDayLow;                   //2차 저항
                    pv22 = pv - dPreDayHigh + dPreDayLow;                   //2차 지지
                    pv3  = dPreDayHigh + 2 * (pv - dPreDayLow);             //3차 저항
                    pv33 = dPreDayLow - 2 * (dPreDayHigh - pv);             //3차 지지
//            		System.out.println("$$$Debug_pivot_date:"+dates[j]+" close:"+basic_data[2]);
                    basic_data = null;
                }

                pivot[0][j + 1] = pv1;
                pivot[1][j + 1] = pv2;
                pivot[2][j + 1] = pv3;
                pivot[3][j + 1] = pv11;
                pivot[4][j + 1] = pv22;
                pivot[5][j + 1] = pv33;
                pivot[6][j + 1] = pv;
            }
        }
        for (int i = 0; i < tool.size(); i++) {
            DrawTool dt = (DrawTool) tool.elementAt(i);
            _cdm.setSubPacketData(dt.getPacketTitle(), pivot[i]);
            if (_cdm.nTradeMulti > 0) {
                _cdm.setSyncPriceFormat(dt.getPacketTitle());
            }
            else {
                _cdm.setPacketFormat(dt.getPacketTitle(), "× 0.01");
            }
        }
        formulated = true;
    }

    //2015.04.03 by lyk - 피봇수정
    //종가를 가지고 고,저,종가를 만든다
    private String[] basic_data;

    public synchronized void makeBongData(String high, String low, String close) {
        if (basic_data == null) {
            basic_data = new String[3];
            basic_data[0] = high;
            basic_data[1] = low;
            basic_data[2] = close;
        }
        else {

            //고가 처리
            double cmp1 = Double.parseDouble(basic_data[0]);
            double cmp2 = Double.parseDouble(high);
            if (cmp1 < cmp2) basic_data[0] = high;

            //저가 처리
            cmp1 = Double.parseDouble(basic_data[1]);
            cmp2 = Double.parseDouble(low);
            if (cmp1 > cmp2) basic_data[1] = low;

            basic_data[2] = close;

        }
    }

    //2015.04.03 by lyk - 피봇수정 end
    public void reFormulateData() {
        formulated = false;    //2014. 1. 7 피봇 지표 넣고 실시간 수신시  죽는현상
        FormulateData();
        formulated = true;
    }

    public void drawGraph(Canvas g) {
        if (!formulated) FormulateData();                       //저장되어 있지 않다면 계산을 새로 한다

        double[] drawData = null;
        for (int i = 0; i < tool.size(); i++) {
            DrawTool t = (DrawTool) tool.elementAt(i);
            try {
                drawData = _cdm.getSubPacketData(t.getPacketTitle());
            } catch (ArrayIndexOutOfBoundsException e) {
                return;
            } catch (NullPointerException e) {
                return;
            }
            t.plot(g, drawData);
        }
    }

    public void drawGraph_withSellPoint(Canvas g) {
    }

    public String getName() {
        return "Pivot";
    }
}