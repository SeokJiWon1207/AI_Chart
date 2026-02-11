package drfn.chart.base;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.Vector;

import drfn.chart.MainFrame;
import drfn.chart.NeoChart2;
import drfn.chart.util.COMUtil;

public class MainBase extends View{
    RelativeLayout layout;
    private Context context = null;
    public Base baseP = null;
    public MainBase(Context context , RelativeLayout layout) {
        super(context);
        this.context = context;
        this.layout = layout;
    }

    public Vector<Base> baseV = new Vector<Base>();

    String defaultBase = "base11";
    public void setDefaultBase(String defaultBase) {
        this.defaultBase = defaultBase;
    }
    public void init() {
        setBase(defaultBase);
    }
    /* API 호출 함수 : 실시간 데이터 처리 */
    public void setRealData(byte[] data) {
        if (baseV == null || baseV.size() < 1) return;
        Base base;
        for (int i = 0 ; i < baseV.size() ; i++) {
            base = (Base) baseV.elementAt(i);
            base.setRealData(data);
        }
    }
    public void setCode(String code) {
        if (baseV == null || baseV.size() < 1) return;
        Base base;
        for (int i = 0 ; i < baseV.size() ; i++) {
            base = (Base) baseV.elementAt(i);
            base.setCode(code);
        }
    }
    public void sendTR(String[] datas) {
        if (baseV == null || baseV.size() < 1) return;
        Base base;
        for (int i = 0 ; i < baseV.size() ; i++) {
            base = (Base) baseV.elementAt(i);
            base.sendTR(datas);
        }
    }
    public void setCodeName(String name) {
        if (baseV == null || baseV.size() < 1) return;
        Base base;
        for (int i = 0 ; i < baseV.size() ; i++) {
            base = (Base) baseV.elementAt(i);
            base.setCodeName(name);
        }
    }
    public void setPeriodName(String name) {
        if (baseV == null || baseV.size() < 1) return;
        Base base;
        for (int i = 0 ; i < baseV.size() ; i++) {
            base = (Base) baseV.elementAt(i);
            base.setPeriodName(name);
        }
    }
    public void setCountText(String name) {
        if (baseV == null || baseV.size() < 1) return;
        Base base;
        for (int i = 0 ; i < baseV.size() ; i++) {
            base = (Base) baseV.elementAt(i);
            base.setCountText(name);
        }
    }
    public void selectChart(NeoChart2 pChart) {
        if (baseV == null || baseV.size() < 1) return;
        Base base;
        for (int i = 0 ; i < baseV.size() ; i++) {
            base = (Base) baseV.elementAt(i);
            base.selectChart(pChart);
        }
    }
    //    public void setCompareCodes() {
//        if (baseV == null || baseV.size() < 1) return;
//        Base base;  
//        for (int i = 0 ; i < baseV.size() ; i++) {
//            base = (Base) baseV.elementAt(i);
//            base.setCompareCodes();
//        }
//    }
    public void showCompareChartUI(boolean selected) {
        if (baseV == null || baseV.size() < 1) return;
        Base base;
        for (int i = 0 ; i < baseV.size() ; i++) {
            base = (Base) baseV.elementAt(i);
            base.showCompareChartUI(selected);
        }
    }
    public void extendChart(NeoChart2 chart) {
        if (baseV == null || baseV.size() < 1) return;
        Base base;
        for (int i = 0 ; i < baseV.size() ; i++) {
            base = (Base) baseV.elementAt(i);
            base.extendChart(chart);
        }
    }
    public void reduceChart(NeoChart2 chart) {
        if (baseV == null || baseV.size() < 1) return;
        Base base;
        for (int i = 0 ; i < baseV.size() ; i++) {
            base = (Base) baseV.elementAt(i);
            base.reduceChart(chart);
        }
    }
    int mapNumber = 11;
    public void setBase(String baseName) {
        try {
//            COMUtil.removeItemSelectFrame();
            removeBaseV();
//            this.removeAll();
        } catch(Exception e) { }
        mapNumber = Integer.parseInt(COMUtil.removeString(baseName, "base"));
        switch (mapNumber) {
            case 11 :
                baseP = new Base11(context, this.layout);
                break;
            //2013.09.17 by LYH >> 패턴 그리기 추가.
            case 13 :
                baseP = new Base13(context, this.layout);
                break;
            case 15:
                baseP = new Base15(context, this.layout);
                break;
            case 16:
                baseP = new Base16(context, this.layout);
                break;
            case 17:
                baseP = new Base17(context, this.layout);
                break;
            //2020.04.13 가로 Stack형 차트 수정 - hjw Start
            case 23:
                baseP = new BaseHorizontalStackChart(context, this.layout);
                break;
            //2020.04.13 가로 Stack형 차트 수정 - hjw End
            //2013.09.17 by LYH <<
        }
        if(baseP==null) return;
        baseP.setMainFrame(mainFrame);
        baseV.addElement((Base) baseP);
        baseP.setMainBase(this);
        baseP.init();

    }

    public void reSetBase(String baseName, String code) {
        int tempNum = Integer.parseInt(COMUtil.removeString(baseName, "base")) / 10;
        if (tempNum == mapNumber /10) setCode(code);
        else setBase(baseName);
    }

    public void initChart(String type) {
        if (baseV == null || baseV.size() < 1) return;
        Base base;
        for (int i = 0 ; i < baseV.size() ; i++) {
            base = (Base) baseV.elementAt(i);
            base.initChart(type);
        }
    }

    public void removeBaseV() {
        if (baseV == null || baseV.size() < 1) return;
        Base base;
        for (int i = 0 ; i < baseV.size() ; i++) {
            base = (Base) baseV.elementAt(i);
            base.destroy();
        }
        baseV.removeAllElements();
    }
    public void changeBlock_NotRepaint(String config) {
        if (baseV == null || baseV.size() < 1) return;
        Base base;
        for (int i = 0 ; i < baseV.size() ; i++) {
            base = (Base) baseV.elementAt(i);
            base.changeBlock_NotRepaint(config);
        }
    }
    public void setDivision(int tag) {
        if (baseV == null || baseV.size() < 1) return;
        Base base;
        for (int i = 0 ; i < baseV.size() ; i++) {
            base = (Base) baseV.elementAt(i);
            base.setDivision(tag);
        }
    }



//	int[] tags = {COMUtil.TOOLBAR_CONFIG_01, COMUtil.TOOLBAR_CONFIG_02, COMUtil.TOOLBAR_CONFIG_03, 
//			COMUtil.TOOLBAR_CONFIG_04, COMUtil.TOOLBAR_CONFIG_05, COMUtil.TOOLBAR_CONFIG_06, 
//			COMUtil.TOOLBAR_CONFIG_07, COMUtil.TOOLBAR_CONFIG_08, COMUtil.TOOLBAR_CONFIG_09, 
//			COMUtil.TOOLBAR_CONFIG_10, COMUtil.TOOLBAR_CONFIG_11, COMUtil.TOOLBAR_CONFIG_12, 
//			COMUtil.TOOLBAR_CONFIG_13, COMUtil.TOOLBAR_CONFIG_14, COMUtil.TOOLBAR_CONFIG_15
//			, COMUtil.TOOLBAR_CONFIG_16, COMUtil.TOOLBAR_CONFIG_17};




    public void addBasicConfig(String config) {
        if (baseV == null || baseV.size() < 1) return;
        Base base;
        for (int i = 0 ; i < baseV.size() ; i++) {
            base = (Base) baseV.elementAt(i);
            base.addBasicConfig(config);
        }
    }

    public void removeBasicConfig(String config) {
        if (baseV == null || baseV.size() < 1) return;
        Base base;
        for (int i = 0 ; i < baseV.size() ; i++) {
            base = (Base) baseV.elementAt(i);
            base.removeBasicConfig(config);
        }
    }

    public void addIndependenceConfig(String config) {
        if (baseV == null || baseV.size() < 1) return;
        Base base;
        for (int i = 0 ; i < baseV.size() ; i++) {
            base = (Base) baseV.elementAt(i);
            base.addIndependenceConfig(config);
        }
    }

    public void removeIndependenceConfig(String config) {
        if (baseV == null || baseV.size() < 1) return;
        Base base;
        for (int i = 0 ; i < baseV.size() ; i++) {
            base = (Base) baseV.elementAt(i);
            base.removeIndependenceConfig(config);
        }
    }

    public void addTrendConfig(String config) {
        if (baseV == null || baseV.size() < 1) return;
        Base base;
        for (int i = 0 ; i < baseV.size() ; i++) {
            base = (Base) baseV.elementAt(i);
            base.addTrendConfig(config);
        }
    }

    public void removeTrendConfig(String config) {
        if (baseV == null || baseV.size() < 1) return;
        Base base;
        for (int i = 0 ; i < baseV.size() ; i++) {
            base = (Base) baseV.elementAt(i);
            base.removeTrendConfig(config);
        }
    }

    public void setVisibleUserGraph(String sData, String visible) {
        if (baseV == null || baseV.size() < 1) return;
        Base base;
        for (int i = 0 ; i < baseV.size() ; i++) {
            base = (Base) baseV.elementAt(i);
            base.setVisibleUserGraph(sData, visible);
        }
    }

    public void addIndicatorConfig(String config) {
        if (baseV == null || baseV.size() < 1) return;
        Base base;
        for (int i = 0 ; i < baseV.size() ; i++) {
            base = (Base) baseV.elementAt(i);
            base.addIndicatorItem(config);
        }
    }

    public void removeIndicatorConfig(String config) {
        if (baseV == null || baseV.size() < 1) return;
        Base base;
        for (int i = 0 ; i < baseV.size() ; i++) {
            base = (Base) baseV.elementAt(i);
            base.removeIndicatorTrendConfig(config);
        }
    }

    public void resetChartConfig() {
        if (baseV == null || baseV.size() < 1) return;
        Base base;
        for (int i = 0 ; i < baseV.size() ; i++) {
            base = (Base) baseV.elementAt(i);
            base.resetChartConfig();
        }
    }

    boolean start = false;
    int resultCount;

    public void setData(byte[] data) {
        Base base;
        for (int i = 0 ; i < baseV.size() ; i++) {
            base = (Base) baseV.elementAt(i);
            base.setPacketData(data);
        }
    }
    public void setData_data(byte[] data, String[] strDates, double[] strOpens, double[] strHighs, double[] strLows, double[] strCloses, double[] strVolumes, double[] strValues, double[] strRights, double[] strRightRates, String strCandleType) {
        Base base;
        for (int i = 0 ; i < baseV.size() ; i++) {
            base = (Base) baseV.elementAt(i);
            ((Base11)base).setPacketData_data(data, strDates, strOpens, strHighs, strLows,strCloses,strVolumes,strValues, strRights, strRightRates,strCandleType);
        }
    }

    public void startProcessing() {
        Base base;
        for (int i = 0 ; i < baseV.size() ; i++) {
            base = (Base) baseV.elementAt(i);
            base.startProcessing();
        }
    }

    public void stopProcessing() {
        Base base;
        for (int i = 0 ; i < baseV.size() ; i++) {
            base = (Base) baseV.elementAt(i);
            base.stopProcessing();
        }
    }

    public void setMarketData(String title, long[] dates, double[] marketData, int nCount, boolean bSendTR) {
        if(baseP!=null) {
            baseP.setMarketData(title, dates, marketData, nCount, bSendTR);
        }
    }
    private MainFrame mainFrame = null;
    public void setMainFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    //2017.05.11 by LYH >> 전략(신호, 강약) 추가
    public void addStrategyConfig(String config){
        if(baseV == null || baseV.size()<1)
            return;
        Base base;
        for(int i = 0; i < baseV.size(); i ++){
            base = baseV.get(i);
            base.addStrategyConfig(config);
        }
    }
    public  void removeStrategyConfig(String config){
        if(baseV == null || baseV.size() < 1)
            return;
        Base base;
        for(int i = 0; i < baseV.size(); i++){
            base = baseV.get(i);
            base.removeStrategyConfig(config);
        }
    }
    //2017.05.11 by LYH << 전략(신호, 강약) 추가 end
}