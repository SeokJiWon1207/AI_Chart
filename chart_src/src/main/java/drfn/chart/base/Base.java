package drfn.chart.base;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import java.util.Hashtable;

import drfn.chart.MainFrame;
import drfn.chart.NeoChart2;
import drfn.chart.util.COMUtil;

public abstract class Base extends View{
    public BaseChart _chart;
    protected String dateType="";//날짜타입(일,주,월,분(1분...),틱)
    public Rect frame = new Rect(0,0,0,0);
    public View preSelBtn = null;
    public int tag = 0;
    public int preTag = -1;
    public boolean btnToggle = false;
    public boolean isContinueAnalDrawMode = false;
    public boolean isCrossBtnSelect = false;
    public int nMarketType = 0;
    protected MainFrame mainFrame = null;

    public Base(Context context) {
        super(context);
    }

    public void setMainFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public void init() {
        //setLayout(new BorderLayout(2, 1));
    }
    public void inputPanel_setText(String str) {
    }
    public void destroy() {
        // try {this.removeAll();} catch(Exception e) { }
    }
    public void inputPanel_destroy(boolean isSign) {
    }
    public void FormInitializeComplete() {
    }

    public void sendTR(String[] trNum) {
        if (trNum != null) COMUtil.getMainFrame().sendTR(trNum[0], trNum[1]);
    }

    MainBase mainBase;
    public void setMainBase(MainBase mainBase) {
        this.mainBase = mainBase;
    }

    public void addTrendConfig(String config) {
        _chart.addGraph(config);
        _chart.resetTitleBoundsAll();
    }

    public void removeTrendConfig(String config) {
        _chart.removeGraph(config);
        _chart.resetTitleBoundsAll();
    }

    public void setVisibleUserGraph(String sData, String visible) {
        _chart.setVisibleUserGraph(sData, visible);
    }

    public void addIndicatorItem(String config) {
        if(_chart!=null) _chart.addBlock(config);
        _chart.resetTitleBoundsAll();  //2017.07.05 by pjm 보조지표 추가/삭제 시 지표명 개행
    }

    public void removeIndicatorTrendConfig(String config) {
        if(_chart!=null) _chart.removeBlock(config);
        _chart.resetTitleBoundsAll();  //2017.07.05 by pjm 보조지표 추가/삭제 시 지표명 개행
    }
    public void removeBasicConfig(String config){
    }
    public void removeIndependenceConfig(String config){
        if(_chart!=null) _chart.removeStandBlock(config);
    }
    public void changeBlock_NotRepaint(String config) {
        if(_chart!=null) _chart.changeBlock_NotRepaint(config);
    }

    public void addBasicConfig(String config){
        if(_chart!=null) _chart.changeBlock(config);
    }
    public void addIndependenceConfig(String config){
        if(_chart!=null) _chart.addStandBlock(config);
    }
    public void resetChartConfig() {
        if(_chart!=null){
            if(_chart.getAverageGraph()!=null){
                //_chart.getAverageGraph().changeControlValue(COMUtil.getMainFrame().cset.average_title);
                _chart.setTitleBounds();
            }
            _chart.setAllProperties();
            _chart.reset();
        }
    }
    public void resetChartConfig_NotRepaint() {
        if(_chart!=null){
            if(_chart.getAverageGraph()!=null){
                // _chart.getAverageGraph().changeControlValue(COMUtil.getMainFrame().cset.average_title);
                _chart.setTitleBounds();
            }
            _chart.setAllProperties();
        }
    }

    public void setPacketData(byte[] data) {
    }
    public void setPacketData_hashtable(Hashtable<String, Object> data) {

    }
    /* API 호출 함수 : 실시간 데이터 처리 */
    public void setRealData(byte[] data) {

    }
    public void setCode(String code){
    }
    public void setCodeName(String name) {
    }
    public void setPeriodName(String name) {
    }
    public void setCountText(String name) {
    }
    public void setDivision(int tag) {
    }
    public void resizeChart(View view) {
    }
    public void selectChart(NeoChart2 pChart) {
    }
    public void showCompareChartUI(boolean selected){
    }
    public void extendChart(NeoChart2 chart){
    }
    public void reduceChart(NeoChart2 chart){
    }
    public void setDateType(String dateType){
        this.dateType = dateType;
    }
    public void chartDataClear() {
        _chart.ChartDataClear();
    }
    public void startProcessing() {
        if(_chart!=null) _chart.startProcessing();
    }
    public void stopProcessing() {
        if(_chart!=null) _chart.stopProcessing();
    }
    public void initChart(String type) {

    }
    public void setToolbarState(int state) {

    }

    public void setChartToolBar(int state) {

    }

    public void repaintAllChart() {

    }

    public void setMarketData(String title, long[] dates, double[] marketData, int nCount, boolean bSendTR) {

    }
    public void addWhiteView()
    {
    }
    public void removeWhiteView()
    {
    }
    public void syncJongMok(View v)
    {
    }
    public void syncJugi(View v)
    {
    }
    public void syncIndicator(View v)
    {
    }
    //2017.05.11 by LYH >> 전략(신호, 강약) 추가
    public void addStrategyConfig(String config){
        if(_chart != null){
            _chart.addGraph(config);
            _chart.resetTitleBoundsAll();
        }
    }
    public void removeStrategyConfig(String config){
        if(_chart != null){
            _chart.removeGraph(config);
            _chart.resetTitleBoundsAll();
        }
    }
    //2017.05.11 by LYH << 전략(신호, 강약) 추가 end
}

    