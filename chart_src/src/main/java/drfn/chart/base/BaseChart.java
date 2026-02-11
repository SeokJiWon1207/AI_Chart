package drfn.chart.base;

import android.content.Context;
import android.graphics.RectF;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import drfn.chart.NeoChart2;
import drfn.chart.anal.AnalTool;
import drfn.chart.block.Block;
import drfn.chart.comp.ChartItemView;
import drfn.chart.draw.DrawTool;
import drfn.chart.graph.AbstractGraph;
import drfn.chart.model.ChartViewModel;
import drfn.chart.util.COMUtil;
import drfn.chart.util.ChartUtil;
import drfn.chart.util.CoSys;

public class BaseChart extends NeoChart2{
    //==================================
    // 구성
    // Block1 = 일본식봉과 가격이동평균선
    // Block2 = 거래량과 거래량 이동평균선
    //==================================

    boolean reverse;//데이터 거꾸로 보내기
    //public RelativeLayout layout;

    //2011.08.05 by LYH >> 시세바 추가 <<
    public RelativeLayout chartItemLayout = null;
    public ChartItemView chartItem = null;
    private Context context = null;
    public boolean m_bHaveMA = true;

    public BaseChart(Context context , RelativeLayout layout) {
        super(context, layout);
        this.layout = layout;
        this.context = context;
        //2011.08.05 by LYH >> 시세바 추가
        if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB)) {
            _cvm.setMarginT((int)COMUtil.getPixel(22));
        }
        else
        {
            _cvm.setMarginT((int)COMUtil.getPixel(18));
        }
        //COMUtil._baseChart = this;
    }

    public void init(String[][] datainfo,int startpos){
        setDataInfo(datainfo,startpos);
        super.init();
    }
    public void init(){
        setDataInfo(getDataInfo(),14);
        //COMUtil._chartMain.showStatus("차트 초기화 시작중입니다.");
        super.init();
    }
    public void initDataInfo(String data) {
        if(data.equals("requestAddData")) {

        } else {
            setDataInfo(getDataInfo(), 14);
        }
    }
    public String[][] getDataInfo() {
        String[][] data_info_DWM = null;
        if(COMUtil.apCode.equals("10101")) {
            data_info_DWM = COMUtil.data_info_stock;
        } else if(COMUtil.apCode.equals(COMUtil.TR_CHART_STOCK)) {
            data_info_DWM = COMUtil.data_info_stock;
        } else if(COMUtil.apCode.equals(COMUtil.TR_CHART_FUTURE)) {
            //2013.01.04 by LYH >> 등락률 비교차트 추가(섹션별종목차트) <<
//            if(_cvm.bRateCompare)
//                data_info_DWM = COMUtil.data_info_ratecompare;
//            else
                data_info_DWM = COMUtil.data_info_future;
        } else if(COMUtil.apCode.equals(COMUtil.TR_CHART_UPJONG)) {
//            data_info_DWM = COMUtil.data_info_upjong;
            data_info_DWM = COMUtil.data_info_future;
        } else if(COMUtil.apCode.equals(COMUtil.TR_CHART_INVESTOR)) {
//            if(_cvm.bRatePeriod)
//                data_info_DWM = COMUtil.data_info_rateperiod;
//            else
                data_info_DWM = COMUtil.data_info_investor;
        }
        else
        {
            data_info_DWM = COMUtil.data_info_stock;
        }
        return data_info_DWM;
    }
    public void setReverse(boolean b){
        reverse = b;
    }
    private void setDataInfo(String[][] data_info,int startpos){
        _cdm.setPacketData(data_info);
    }
    public void resetDataInfo(String[][] data_info,int startpos){
        _cdm.destroy();
        setDataInfo(data_info,startpos);
    }
    //    public void setBasicTICUI(){//일반적인 봉과 거래량 UI
//		
//        int w = this.getWidth();
//        int bHeight = getBlockUnitHeight(2);
//        
//        Block cb1 = makeBlock(0,0);
//        cb1.setProperties("지표 Data",1,_cvm.getScaleLineType());
//        cb1.setBlockType(Block.BASIC_BLOCK);
//        cb1.setMarginB(20);
//        cb1.add("라인차트");
//        addBlock(cb1);
//        cb1.setBounds(0,0,(int)w,bHeight*2,true);
//        
//        Block cb2 = makeBlock(0,1,"거래량");
//        cb2.setProperties("지표 Data",1,_cvm.getScaleLineType());
//        cb2.add("거래량이동평균");
//        addBlock(cb2);
//        cb2.setBounds(0,(int)bHeight*2,(int)w,bHeight,true);
//        setPopupMenu(false);
//    }
    @Override
    protected void onMeasure(int wMeasureSpec, int hMeasureSpec) {
//    	int measuredHeight = measureHeight(hMeasureSpec);
//    	int measuredWidth = measureWidth(hMeasureSpec);                   
        // setMesasuredDimension을 반드시 호출해야만 한다.
        // 그렇지 않으면 컨트롤이 배치될 때
        // 런타임 예외가 발생할 것이다.
        setMeasuredDimension((int)chart_bounds.width(), (int)chart_bounds.height());

//        int parentWidth = MeasureSpec.getSize(wMeasureSpec);
//        int parentHeight = MeasureSpec.getSize(hMeasureSpec);
//        setMeasuredDimension(parentWidth, parentHeight);

    }
    //    private int measureHeight(int measureSpec) {
////    	int specMode = MeasureSpec.getMode(measureSpec);         
//    	int specSize = MeasureSpec.getSize(measureSpec);                   
//    	// 뷰의 높이를 계산한다.         
//    	return specSize;     
//    }           
//    private int measureWidth(int measureSpec) {         
//    	int specMode = MeasureSpec.getMode(measureSpec);         
//    	int specSize = MeasureSpec.getSize(measureSpec);                   
//    	// 뷰의 폭을 계산한다.                   
//    	return specSize;     
//    }
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged((int)chart_bounds.width(), (int)chart_bounds.height(), oldw, oldh);

    }

    public void setBasicUI(){//일반적인 봉과 거래량 UI (한화증권 차트 UI)
        int w = this.getHeight();

//		int left = this.getLeft();
//		int top = this.getTop();

        int bHeight = getBlockUnitHeight(2);

        Block cb1 = makeBlock(0,0,"가격차트");
        cb1.setProperties("지표 Data",1,_cvm.getScaleLineType());
        cb1.setBlockType(Block.BASIC_BLOCK);
        if(m_bHaveMA && !_cvm.bIsTodayLineChart) //2020.04.14 당일 라인차트 추가 - hjw
            cb1.add("주가이동평균");
        cb1.setBounds(0,0,w,bHeight*2,true);
        addBlock(cb1);

        if(!_cvm.bIsMiniBongChart)
        {
//            cb1.setMarginT((int)COMUtil.getPixel(40));
            cb1.setMarginT((int)COMUtil.getPixel(Block.BLOCK_TOP_MARGIN));
            cb1.setMarginB((int)COMUtil.getPixel(Block.BLOCK_BOTTOM_MARGIN)); //2021.04.28 by lyk - kakaopay - 스크롤바 위치가 차트 하단일 경우 처리
//            cb1.setMarginB((int)COMUtil.getPixel(20) + _cvm.XSCALE_H);    //XScale //2021.04.28 by lyk - kakaopay - 스크롤바 위치가 주블럭 하단일 경우 처리
            if(_cvm.bIsTodayLineChart) {
                cb1.setMarginT((int)COMUtil.getPixel(15));
                cb1.setMarginB((int) COMUtil.getPixel(25));
            }
        }
        else
        {
            cb1.setMarginT((int)COMUtil.getPixel(2));
            cb1.setMarginB((int)COMUtil.getPixel(2));
        }


        //2019. 06. 07 by hyh - FX에서 거래량 추가 불가 처리 >>
        if (COMUtil._mainFrame.mainBase.baseP.nMarketType != 4) {
            Block cb2 = makeBlock(0, 1, "거래량");
            cb2.setProperties("지표 Data", 1, _cvm.getScaleLineType());
            cb2.setBounds(0, bHeight * 2, w, bHeight, true);
            cb2.add("거래량이동평균");
//            if(_cvm.bIsOneQStockChart)
//                cb2.add("거래량이동평균");
            addBlock(cb2);
        }
        //2019. 06. 07 by hyh - FX에서 거래량 추가 불가 처리 <<

        setPopupMenu(false);
    }

    //2013.07.31 >> 기준선 라인 차트 타입 추가
    public void setBasicUI_Standard(){//일반적인 봉과 거래량 UI (한화증권 차트 UI)
        int w = this.getHeight();
        int h = this.getHeight();

        Block cb1 = makeBlock(0,0,"가격차트");
        cb1.setProperties("지표 Data",1,_cvm.getScaleLineType());
        cb1.setMarginT((int)COMUtil.getPixel_H(25));
        cb1.setMarginB((int)COMUtil.getPixel_H(40));
        cb1.setBlockType(Block.BASIC_BLOCK);
        cb1.add("거래량");

        //2015. 2. 4 미결제약정 추가되는 차트 그래프 UI>>
        String[] data1={"시가"};
        String[] data2={"고가"};
        cb1.add_userGraph("기초자산", 1, data1, 2, 5);
        cb1.add_userGraph("미결제약정", 1, data2, 2, 6);
        cb1.setVisible("기초자산", false);
        cb1.setVisible("미결제약정", false);
        //2015. 2. 4 미결제약정 추가되는 차트 그래프 UI<<

        cb1.setBounds(0,0,w,h,true);
        addBlock(cb1);

        _cvm.setVolDrawType(0);
        _cvm.bStandardLine = true;
        _cdm.setStandardLine(true);	//2015. 2. 5 선옵 1분선 기초자산/미결제약정 실시간
        //m_strCandleType = "라인";
        setPopupMenu(false);
    }
    //2013.07.31 <<

    public void setBasicUI_Investor(String strUI){
    	
    	String[] strInfo = strUI.split("=");
    	String strScale = null;
    	
    	if(strInfo.length>1)
    	{
    		strUI = strInfo[0];
    		strScale = strInfo[1];
    	}
        String[] strValues = strUI.split(",");
        int nValueLen = strValues.length;

        removeAllBlocks();
        Block cb1 = makeBlock(0,0);
        //2013.10.07 by LYH >> 스케일 왼쪽
        //cb1.setProperties("지표 Data",1,_cvm.getScaleLineType());
        if(strValues[nValueLen-1].equals("updown") || strValues[nValueLen-1].equals("member_bar") || strValues[nValueLen - 1].equals("UpDownBar") || strValues[nValueLen-1].equals("updown_grid") || strValues[nValueLen-1].equals("futopt_grid")) //상하식 바차트
        {
        	//cb1.setProperties("지표 Data",2,_cvm.getScaleLineType());
        	cb1.setProperties("지표 Data",3,_cvm.getScaleLineType());
        }
        else if(strValues[nValueLen-1].equals("L"))	//스케일 왼쪽 처리.
        {
            cb1.setProperties("지표 Data",0,_cvm.getScaleLineType());
            nValueLen -= 1;
        }
        else if(strValues[nValueLen-1].equals("B"))	//스케일 왼쪽 처리.
        {
            cb1.setProperties("지표 Data",2,_cvm.getScaleLineType());
            nValueLen -= 1;

            float rtnVal = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10,
                    getContext().getResources().getDisplayMetrics());

            _cvm.mPaint_Text.setTextSize(rtnVal);
        }
        //2016.12.01 by LYH >> 스케일 없슴 옵션 추가(테마분석 차트)
        else if(strValues[nValueLen-1].equals("N"))	//스케일 왼쪽 처리.
        {
            cb1.setProperties("지표 Data",3,_cvm.getScaleLineType());
            nValueLen -= 1;
            _cvm.bIsNoScale = true;
        }
        //2016.12.01 by LYH >> 스케일 없슴 옵션 추가(테마분석 차트) end
        //2019. 03. 12 by hyh - 투자자 업종 가로 바차트 추가 >>
        else if(strValues[nValueLen-1].equals("updown_upjong"))
        {
            cb1.setProperties("지표 Data", 3, _cvm.getScaleLineType());
        }
        //2019. 03. 12 by hyh - 투자자 업종 가로 바차트 추가 <<
        else
        {
            cb1.setProperties("지표 Data",1,_cvm.getScaleLineType());
        }
        //2013.10.07 by LYH <<
        cb1.setMarginB((int)COMUtil.getPixel(5));
        cb1.setBlockType(Block.BASIC_BLOCK);
        String[] data1 = {"data1"};
        String[] data2 = {"data2"};
        String[] data3 = {"data3"};
        String[] data4 = {"data4"};
        String[] data5 = {"data5"};
        String[] data6 = {"data6"};
        String[] data7 = {"data7"};
        String[] data8 = {"data8"};
        String[] data9 = {"data9"};

        //2013. 5. 24  태블릿 매매동향 - 시간대별  체크갯수에 따라서 처리 >>
        int nPriceColorIndex = 13;
        if (strValues[nValueLen - 1].equals("UpDownBar")) //자산관리 퍼센트/바 같은색 처리
        {
            _cvm.bIsShowTitle = false;
            _cvm.nAssetType = ChartViewModel.ASSET_UPDOWN_BAR;
            _cvm.bIsUpdownChart = true;
//    		_cvm.m_bUseAnimationLine = true;
            _cvm.isCrosslineMode = false;

            cb1.setMarginT((int) COMUtil.getPixel_H(20));
            cb1.setMarginB((int) COMUtil.getPixel(0));
            cb1.setMarginL((int) COMUtil.getPixel(0));
            cb1.setMarginR((int) COMUtil.getPixel(0));

            cb1.add_userGraphBar_UpDown(strValues[0], 1, data1, 2, 17);
            addBlock(cb1);

            _cvm.XSCALE_H = (int) COMUtil.getPixel_H(49);
            //_cdm.setDateType(6); //xscale 텍스트 타입으로 설정

            setPopupMenu(true);
            setUI1();

            //XScale 크기 다시 잡아줌.
            if (xscale != null){
                RectF bound = chart_bounds;
                _cvm.setBounds(0, _cvm.Margin_T + bound.top, bound.right, bound.bottom - _cvm.Margin_T - _cvm.Margin_B - _cvm.TOOLBAR_B - (int) COMUtil.getPixel(1));
                //xscale.setBounds(_cvm.Margin_L + (int) COMUtil.getPixel(10) + (int) COMUtil.getPixel(2), bound.bottom - (_cvm.SCROLL_B + _cvm.XSCALE_H), bound.right - (_cvm.Margin_R) - (int) COMUtil.getPixel(4), bound.bottom - (_cvm.SCROLL_B + _cvm.XSCALE_H) + _cvm.XSCALE_H);
                xscale.setBounds(_cvm.Margin_L, bound.bottom - (_cvm.SCROLL_B + _cvm.XSCALE_H), bound.right - (_cvm.Margin_R) - (int) COMUtil.getPixel(10), bound.bottom - (_cvm.SCROLL_B + _cvm.XSCALE_H) + _cvm.XSCALE_H);
                xscale.setOuterBounds(bound.left, bound.top, bound.right, bound.bottom);
            }

            return;
        }
        if (strValues[nValueLen - 1].equals("LineFill")) //자산관리 라인/위아래바
        {
            _cvm.nAssetType = ChartViewModel.ASSET_LINE_FILL;
            _cvm.bIsShowTitle = false;
            //_cvm.bIsUpdownChart = true;
            _cvm.XSCALE_H = (int) COMUtil.getPixel_H(27);
            showViewPanel();

//            Block cb = makeBlock(0, 0);
//            cb.setProperties("지표 Data", 1, _cvm.getScaleLineType());
            //cb1.add_userGraph(strValues[0], 1, data1, 3, 16);
            cb1.add_userGraph_NoTitle(strValues[0], 1, data1, 3, 21, false);
            cb1.setMarginB((int) COMUtil.getPixel_H(17));
            cb1.setMarginL((int) COMUtil.getPixel_W(10));
            cb1.setMarginR((int) COMUtil.getPixel_W(10));
            addBlock(cb1);

//            Block cb_2 = makeBlock(0, 1);
//            cb_2.setProperties("지표 Data", 1, _cvm.getScaleLineType());
//            cb_2.add_userGraphBar_Osc(strValues[1], 1, data2, 2, 17);
//            addBlock(cb_2);

            setPopupMenu(true);
            setUI1();
            //XScale 크기 다시 잡아줌.
            RectF bound = chart_bounds;
            if (_cvm.useUnderToolbar() || _cvm.useTooltip() || _cvm.useStatusBar()) {
                _cvm.setBounds(0, _cvm.Margin_T + bound.top, bound.right, bound.bottom - _cvm.Margin_T - _cvm.Margin_B - _cvm.TOOLBAR_B - (int) COMUtil.getPixel(1));
                xscale.setBounds(_cvm.Margin_L+(int)COMUtil.getPixel(2), bound.bottom - (_cvm.SCROLL_B + _cvm.XSCALE_H), chart_bounds.right - (_cvm.Margin_R) - _cvm.PADDING_RIGHT, bound.bottom - (_cvm.SCROLL_B + _cvm.XSCALE_H) + _cvm.XSCALE_H);
            }
            if (xscale != null)
                xscale.setOuterBounds(bound.left, bound.top, bound.right, bound.bottom);
            this.xscale.setProperties("자료일자", ".", ":");

            //십자선라인 무조건 표시
//            Base11 base11 = (Base11) COMUtil._mainFrame.mainBase.baseP;
//            base11.showCrossLine(true);
            _cvm.isCrosslineMode = true;
            //viewP.setVisibility(View.GONE);

            return;
        }
        if (strValues[nValueLen - 1].equals("Line")) //자산관리 라인/위아래바
        {
            _cvm.nAssetType = ChartViewModel.ASSET_LINE;
            cb1.W_YSCALE = (int)COMUtil.getPixel(0);
            _cvm.bIsShowTitle = false;
            _cvm.XSCALE_H = (int) COMUtil.getPixel_H(27);

            cb1.add_userGraph_NoTitle(strValues[0], 1, data1, 3, 21, false);
            cb1.setMarginB((int) COMUtil.getPixel_H(10));
            cb1.setMarginL((int) COMUtil.getPixel_W(10));
            cb1.setMarginR((int) COMUtil.getPixel_W(10));
            addBlock(cb1);

            setPopupMenu(true);
            setUI1();
            //XScale 크기 다시 잡아줌.
            RectF bound = chart_bounds;
            if (_cvm.useUnderToolbar() || _cvm.useTooltip() || _cvm.useStatusBar()) {
                _cvm.setBounds(0, _cvm.Margin_T + bound.top, bound.right, bound.bottom - _cvm.Margin_T - _cvm.Margin_B - _cvm.TOOLBAR_B - (int) COMUtil.getPixel(1));
                xscale.setBounds(_cvm.Margin_L+(int)COMUtil.getPixel(2), bound.bottom - (_cvm.SCROLL_B + _cvm.XSCALE_H), chart_bounds.right - (_cvm.Margin_R) - _cvm.PADDING_RIGHT, bound.bottom - (_cvm.SCROLL_B + _cvm.XSCALE_H) + _cvm.XSCALE_H);
            }
            if (xscale != null)
                xscale.setOuterBounds(bound.left, bound.top, bound.right, bound.bottom);
            this.xscale.setProperties("자료일자", ".", ":");

            _cvm.isCrosslineMode = true;

            return;
        }
        if(strValues[nValueLen-1].equals("bar_osc"))
        {
            _cdm.m_bRealUpdate = true;
            _cvm.bIsShowTitle = false;
            cb1.setMarginT((int)COMUtil.getPixel(0));
            cb1.add_userGraphBar_Osc(strValues[0],1,data1,2,5);
            addBlock(cb1);

            setPopupMenu(true);
            reSetUI(true);
            return;
        }
    	if(strValues[nValueLen-1].equals("updown")) //상하식 바차트
    	{
//    		_cvm.bIsShowTitle = false;
//    		_cvm.bIsUpdownChart = true;
//    		_cvm.bIsInnerText = true;
    		_cvm.bIsInnerTextVertical = true;
    		
    		cb1.W_YSCALE = (int)COMUtil.getPixel(0);
    		_cvm.XSCALE_H=(int)COMUtil.getPixel(50);
    		
    		cb1.add_userGraphBar(strValues[0],1,data1,3);
    		//2014.05.20 by LYH >> 업종 봉차트 UI개선.
            cb1.setMarginT((int)COMUtil.getPixel(5));
            cb1.setMarginB(0);
 //           cb1.setMarginB(5);
          	//2014.05.20 by LYH << 업종 봉차트 UI개선.
            addBlock(cb1);
            
            setPopupMenu(true);
            reSetUI(true);
        	
        	return;
    	}
        //2017.03.07 by LYH >> 업종 등락 차트
        else if(strValues[nValueLen-1].equals("updown_upjong")) //업종종합 가로차트 형태
        {
            _cvm.bIsShowTitle = false;
            _cvm.bIsUpdownChart = true;
            _cvm.bIsInnerText = true;
            _cvm.bIsInnerTextVertical = true;

            cb1.W_YSCALE = (int)COMUtil.getPixel(0);

            cb1.add_userGraphBar_UpDownBong(strValues[0],1,data1,2,3);
            //2014.05.20 by LYH >> 업종 봉차트 UI개선.
            cb1.setMarginT(5);
            cb1.setMarginB(5);
            //2014.05.20 by LYH << 업종 봉차트 UI개선.
            addBlock(cb1);


            _cvm.XSCALE_H=(int)COMUtil.getPixel(0);
            setPopupMenu(true);
            setUI1();

            return;
        }
        //2017.03.07 by LYH << 업종 등락 차트
        else if(strValues[nValueLen-1].equals("futopt_grid")) //업종종합 가로차트 형태
        {
            _cvm.bIsShowTitle = false;
            _cvm.bIsUpdownChart = true;
            _cvm.bIsUpdownGridChart = true;
//            _cvm.bIsInnerText = true;
            _cvm.bIsInnerTextVertical = true;

            cb1.W_YSCALE = (int)COMUtil.getPixel(0);

            cb1.add_userGraphBar_FutOptGrid(strValues[0],1,data1,2,3);
            //2014.05.20 by LYH >> 업종 봉차트 UI개선.
            cb1.setMarginT((int)COMUtil.getPixel_H(0));
            cb1.setMarginB((int)COMUtil.getPixel_H(0));
            //2014.05.20 by LYH << 업종 봉차트 UI개선.
            addBlock(cb1);


            _cvm.XSCALE_H=(int)COMUtil.getPixel(0);
            setPopupMenu(true);
            setUI1();

            return;
        }
        //2019. 03. 12 by hyh - 투자자 업종 가로 바차트 추가 >>
        else if (strValues[nValueLen - 1].equals("updown_bar")) //업종종합 가로차트 형태
        {
            _cvm.bIsShowTitle = false;
            _cvm.bIsUpdownChart = true;
            _cvm.bIsInnerText = true;

            cb1.setMarginT((int) COMUtil.getPixel(30));
            cb1.setMarginB((int) COMUtil.getPixel(50));
            _cvm.setMarginL((int) COMUtil.getPixel(0));
            cb1.setMarginL((int) COMUtil.getPixel(0));
            cb1.setMarginR((int) COMUtil.getPixel(0));

            cb1.add_userGraphBar_UpDown(strValues[0], 1, data1, 2, 0);
            addBlock(cb1);

            if (_cvm.bIsInnerText)
                _cvm.XSCALE_H = 0;
            else
                _cvm.XSCALE_H = (int) COMUtil.getPixel(60);
            _cdm.setDateType(6); //xscale 텍스트 타입으로 설정

            setPopupMenu(true);
            setUI1();

            xscale.setBounds(_cvm.Margin_L, chart_bounds.bottom - (_cvm.SCROLL_B + _cvm.XSCALE_H), chart_bounds.right - (_cvm.Margin_R) - _cvm.PADDING_RIGHT, chart_bounds.bottom - (_cvm.SCROLL_B + _cvm.XSCALE_H) + _cvm.XSCALE_H);
            return;
        }
        //2019. 03. 12 by hyh - 투자자 업종 가로 바차트 추가 <<
        if(strValues[nValueLen-1].equals("member_bar")) //거래원 바차트
        {
    		_cvm.bIsShowTitle = false;
//    		_cvm.bIsUpdownChart = true;
    		_cvm.bIsInnerText = true;
//            _cvm.bIsInnerTextVertical = true;

            cb1.W_YSCALE = (int)COMUtil.getPixel(0);
            _cvm.XSCALE_H=(int)COMUtil.getPixel(0);

            cb1.add_userGraphBar(strValues[0],1,data1,3);
            //2014.05.20 by LYH >> 업종 봉차트 UI개선.
            cb1.setMarginT((int)COMUtil.getPixel(20));
            cb1.setMarginB(0);
            //           cb1.setMarginB(5);
            //2014.05.20 by LYH << 업종 봉차트 UI개선.
            addBlock(cb1);

            setPopupMenu(true);
            reSetUI(true);

            return;
        }
        if(strValues[nValueLen-1].equals("jipyo"))
        {
            cb1.add_userGraph(strValues[0], 1, data1, 2, nPriceColorIndex);
            addBlock(cb1);
            boolean bOsc = true;

            Block cb2 = makeBlock(0,1);
            cb2.setProperties("지표 Data", 1, _cvm.getScaleLineType());
            if (bOsc)
                cb2.add_userGraphBar_Osc(strValues[1], 1, data2, 2, 6);
            else
                cb2.add_userGraph(strValues[1], 1, data2, 2, 2);
            addBlock(cb2);

            setPopupMenu(true);
            reSetUI(true);
            return;
        }
        if(strValues[nValueLen-1].equals("#"))
        {
            if(nValueLen>=7)
            {
                cb1.add_userGraphBar_Osc(strValues[1],1,data1,2,5);
                cb1.add_userGraph_NoTitle(strValues[0],1,data4,2,nPriceColorIndex,false);
                addBlock(cb1);

                Block cb2 = makeBlock(0,1);
                cb2.setProperties("지표 Data",1,_cvm.getScaleLineType());
                cb2.add_userGraphBar_Osc(strValues[3],1,data2,2,6);
                cb2.add_userGraph_NoTitle(strValues[0],1,data4,2,nPriceColorIndex,false);
                addBlock(cb2);

                Block cb3 = makeBlock(0,2);
                cb3.setProperties("지표 Data",1,_cvm.getScaleLineType());
                cb3.add_userGraphBar_Osc(strValues[5],1,data3,2,7);
                cb3.add_userGraph_NoTitle(strValues[0],1,data4,2,nPriceColorIndex,false);
                addBlock(cb3);
            }
            else
            {
                cb1.add_userGraph(strValues[0], 1, data1, 2, 5);
                cb1.add_userGraph(strValues[1], 2, data2 ,2, 6);
                //cb1.add_userGraphBar_Osc(strValues[0],1,data1,2,4);
                addBlock(cb1);

                Block cb2 = makeBlock(0,1);
                cb2.setProperties("지표 Data", 1, _cvm.getScaleLineType());
                cb2.add_userGraphBar_Osc(strValues[2], 1, data3, 2, 6);
                addBlock(cb2);

//
//                Block cb3 = makeBlock(0,2);
//                cb3.setProperties("지표 Data",1,_cvm.getScaleLineType());
//                cb3.add_userGraphBar_Osc(strValues[2],1,data3,2,6);
//                addBlock(cb3);
            }
            setPopupMenu(true);
            setUI1();
            return;
        }

        //2020.04.20 line+roundedBar 차트 추가 - hjw >>
        if(strValues[nValueLen-1].equals("Line+Bar"))
        {
            _cvm.m_nChartType = ChartViewModel.CHART_LINE_ROUNDED_BAR;
            cb1.setBlockType(cb1.BASIC_BLOCK);

            cb1.add_userGraphRoundedBar(strValues[0], 1, data1, 21);
            cb1.add_userGraph(strValues[1], 1, data2 ,2, 0);

            addBlock(cb1);

            setPopupMenu(true);
            setUI1();
            return;
        }
        //2020.04.20 line+roundedBar 차트 추가 - hjw <<
        //2020.04.20 roundedBar3 차트 추가 - hjw >>
        else if(strValues[nValueLen-1].equals("3RoundedBar"))
        {
            _cvm.m_nChartType = ChartViewModel.CHART_THREE_ROUNDED_BAR;
            _cvm.bIsShowTitle = false;
            cb1.setBlockType(cb1.BASIC_BLOCK);
            _cvm.setMarginT(0);
            cb1.setMarginB(0);
            cb1.setMarginR(0);

            cb1.add_userGraphRoundedBar2(strValues[0], 1, data1, 21);

            addBlock(cb1);
            _cdm.setDateType(6);
            _cvm.XSCALE_H=(int)COMUtil.getPixel(20);

            setUI1();
            return;
        }
        //2020.04.20 roundedBar3 차트 추가 - hjw <<
        else if(strValues[nValueLen-1].equals("HorizontalRoundedBar"))
        {
            _cvm.m_nChartType = ChartViewModel.CHART_HORIZONTAL_ROUNDED_BAR;
            _cvm.bIsShowTitle = false;
            cb1.setBlockType(cb1.BASIC_BLOCK);
            cb1.setMarginT(0);
            cb1.setMarginB(0);
            cb1.setMarginR(0);

            cb1.add_userGraphRoundedBar2(strValues[0], 1, data1, 21);

            addBlock(cb1);
//            _cdm.setDateType(6);
            _cvm.XSCALE_H=(int)COMUtil.getPixel(0);

            setUI1();
            return;
        }
        else if(strValues[nValueLen-1].equals("LineMountain"))
        {
            //_cvm.bIsLineFillChart = true;
            _cvm.nAssetType = ChartViewModel.ASSET_LINE_MOUNTAIN;
            //_cvm.bIsHideXYscale = true;
//            _cvm.XSCALE_H=(int)COMUtil.getPixel(18);
//            _cvm.bIsShowTitle = false;
            cb1.setMarginB((int) COMUtil.getPixel(5));
            cb1.setMarginL((int) COMUtil.getPixel(5));
            cb1.setBlockType(cb1.BASIC_BLOCK);
            cb1.setMarginT((int)COMUtil.getPixel(40));
            cb1.W_YSCALE = (int)COMUtil.getPixel(0);

            cb1.add_userGraph(strValues[0], 1, data1 ,2, 2);
            cb1.add_userGraphMountain(strValues[1], 1, data2, 2, 0);

            addBlock(cb1);

            setPopupMenu(true);
            _cvm.setOnePage(1);
            reSetUI(true);

//            if (xscale != null){
//                RectF bound = chart_bounds;
//                xscale.setBounds(_cvm.Margin_L, bound.bottom - (_cvm.SCROLL_B + _cvm.XSCALE_H), bound.right - _cvm.PADDING_RIGHT , bound.bottom - (_cvm.SCROLL_B + _cvm.XSCALE_H) + _cvm.XSCALE_H);
//            }
            //2020.06.08 by LYH >> 마운틴 차트 툴팁 개선 Start
//            _cvm.isCrosslineMode = true;
            _cvm.curIndex = -1;
            //2020.06.08 by LYH >> 마운틴 차트 툴팁 개선 End

            return;
        }
        //2019.08.12 by LYH >> 업종 그리드 바차트 추가 Start
        else if(strValues[nValueLen-1].equals("updown_grid"))
        {
            _cvm.bIsShowTitle = false;
            _cvm.bIsUpdownChart = true;
            _cvm.bIsUpdownGridChart = true;
//            _cvm.bIsInnerText = true;

            cb1.W_YSCALE = (int)COMUtil.getPixel(0);

            cb1.add_userGraphBar_UpDownGrid(strValues[0],1,data1,2,3);
            //2014.05.20 by LYH >> 업종 봉차트 UI개선.
            cb1.setMarginT(5);
            cb1.setMarginB(5);
            //2014.05.20 by LYH << 업종 봉차트 UI개선.
            addBlock(cb1);

            _cvm.XSCALE_H=(int)COMUtil.getPixel(0);

            setPopupMenu(true);
            setUI1();

            return;
        }

        //체크되어 넘어온 값 갯수에 따라서 다르게 처리.
        cb1.setMarginT((int)COMUtil.getPixel_H(30));
        switch(nValueLen)
        {
            case 1:
                //2016.12.01 by LYH >> 스케일 없슴 옵션 추가(테마분석 차트)
                if(_cvm.bIsNoScale)
                {
                    cb1.add_userGraph_NoTitle(strValues[0],1,data1,2,13, false);
                }
                //2016.12.01 by LYH >> 스케일 없슴 옵션 추가(테마분석 차트) end
                else
                    cb1.add_userGraph(strValues[0],1,data1,2,13);
                break;
            case 2:
            	if(strScale!=null)
            	{
            		String[] strScales = strScale.split(",");
	                cb1.add_userGraph(strValues[0],Integer.parseInt(strScales[0]),data1,2,13);
	                cb1.add_userGraph_jisu(strValues[1],Integer.parseInt(strScales[1]),data2,2,14);
            	}
                //2016.12.01 by LYH >> 스케일 없슴 옵션 추가(테마분석 차트)
                else if(_cvm.bIsNoScale)
                {
                    cb1.add_userGraph_NoTitle(strValues[0],1,data1,2,13, false);
                    cb1.add_userGraph_NoTitle(strValues[1],2,data2,2,14, false);
                }
                //2016.12.01 by LYH >> 스케일 없슴 옵션 추가(테마분석 차트) end
            	else
            	{
            		cb1.add_userGraph(strValues[0],1,data1,2,13);
                    cb1.add_userGraph(strValues[1],2,data2,2,14);
            	}
                break;
            case 3:
            	if(strScale!=null)
            	{
            		String[] strScales = strScale.split(",");
	                cb1.add_userGraph(strValues[0],Integer.parseInt(strScales[0]),data1,2,13);
	                cb1.add_userGraph(strValues[1],Integer.parseInt(strScales[1]),data2,2,14);
	                cb1.add_userGraph_jisu(strValues[2],Integer.parseInt(strScales[2]),data3,2,15);
            	}
                //2016.12.01 by LYH >> 스케일 없슴 옵션 추가(테마분석 차트)
                else if(_cvm.bIsNoScale)
                {
                    cb1.add_userGraph_NoTitle(strValues[0],1,data1,2,13, false);
                    cb1.add_userGraph_NoTitle(strValues[1],2,data2,2,14, false);
                    cb1.add_userGraph_NoTitle(strValues[2],2,data3,2,15, false);
                }
                //2016.12.01 by LYH >> 스케일 없슴 옵션 추가(테마분석 차트) end
            	else
            	{
	                cb1.add_userGraph(strValues[0],1,data1,2,13);
	                cb1.add_userGraph(strValues[1],2,data2,2,14);
	                cb1.add_userGraph(strValues[2],2,data3,2,15);
            	}
                break;
            case 4:
            {
                if(strScale!=null)
                {
                    String[] strScales = strScale.split(",");
                    cb1.add_userGraph(strValues[0],Integer.parseInt(strScales[0]),data1,2,13);
                    cb1.add_userGraph(strValues[1],Integer.parseInt(strScales[1]),data2,2,14);
                    cb1.add_userGraph(strValues[2],Integer.parseInt(strScales[2]),data3,2,15);
                    cb1.add_userGraph_jisu(strValues[3],Integer.parseInt(strScales[3]),data4,2,16);
                }
                else {
                    cb1.add_userGraph(strValues[0], 1, data1, 2, 13);
                    cb1.add_userGraph(strValues[1], 2, data2, 2, 14);
                    cb1.add_userGraph(strValues[2], 2, data3, 2, 15);
                    cb1.add_userGraph(strValues[3], 2, data4, 2, 16);
                }
            }
            break;
            case 5:
                if(strScale!=null)
                {
                    String[] strScales = strScale.split(",");
                    cb1.add_userGraph(strValues[0],Integer.parseInt(strScales[0]),data1,2,13);
                    cb1.add_userGraph(strValues[1],Integer.parseInt(strScales[1]),data2,2,14);
                    cb1.add_userGraph(strValues[2],Integer.parseInt(strScales[2]),data3,2,15);
                    cb1.add_userGraph(strValues[3],Integer.parseInt(strScales[3]),data4,2,16);
                    cb1.add_userGraph_jisu(strValues[4],Integer.parseInt(strScales[4]),data5,2,17);
                }
                else {
                    cb1.add_userGraph(strValues[0], 1, data1, 2, 13);
                    cb1.add_userGraph(strValues[1], 2, data2, 2, 14);
                    cb1.add_userGraph(strValues[2], 2, data3, 2, 15);
                    cb1.add_userGraph(strValues[3], 2, data4, 2, 16);
                    cb1.add_userGraph(strValues[4], 2, data5, 2, 17);
                }
//                if(strValues[4].equals("line"))
//                {
//                    cb1.add_userGraph(strValues[1],1,data1,2,5);
//                    cb1.add_userGraph(strValues[2],2,data2,2,6);
//                    cb1.add_userGraph(strValues[3],2,data3,2,7);
//                    cb1.add_userGraph_NoTitle(strValues[0],1,data4,3,nPriceColorIndex,false);
//                }
//                else
//                {
//                    cb1.add_userGraphBar_Osc(strValues[1],1,data1,2,4);
//                    cb1.add_userGraphBar_Osc(strValues[2],1,data2,2,5);
//                    cb1.add_userGraphBar_Osc(strValues[3],1,data3,2,6);
//                    cb1.add_userGraphBar_Osc(strValues[4],1,data5,2,7);
//                    cb1.add_userGraph_NoTitle(strValues[0],1,data4,2,nPriceColorIndex,false);
//                }
                break;
            case 6:
                cb1.add_userGraph(strValues[0], 1, data1, 2, 13);
                cb1.add_userGraph(strValues[1], 2, data2, 2, 14);
                cb1.add_userGraph(strValues[2], 2, data3, 2, 15);
                cb1.add_userGraph(strValues[3], 2, data4, 2, 16);
                cb1.add_userGraph(strValues[4], 2, data5, 2, 17);
                cb1.add_userGraph(strValues[5], 2, data6, 2, 10);

//                cb1.add_userGraphBar_Osc(strValues[1],1,data1,2,4);
//                cb1.add_userGraphBar_Osc(strValues[2],1,data2,2,5);
//                cb1.add_userGraphBar_Osc(strValues[3],1,data3,2,6);
//                cb1.add_userGraphBar_Osc(strValues[4],1,data5,2,7);
//                cb1.add_userGraphBar_Osc(strValues[5],1,data6,2,8);
//                cb1.add_userGraph_NoTitle(strValues[0],1,data4,2,nPriceColorIndex,false);
                break;
            case 7:
                cb1.add_userGraph(strValues[0], 1, data1, 2, 13);
                cb1.add_userGraph(strValues[1], 2, data2, 2, 14);
                cb1.add_userGraph(strValues[2], 2, data3, 2, 15);
                cb1.add_userGraph(strValues[3], 2, data4, 2, 16);
                cb1.add_userGraph(strValues[4], 2, data5, 2, 17);
                cb1.add_userGraph(strValues[5], 2, data6, 2, 10);
                cb1.add_userGraph(strValues[6], 2, data7, 2, 11);
                break;
            case 8:
                cb1.add_userGraph(strValues[0], 1, data1, 2, 16);
                cb1.add_userGraph(strValues[1], 2, data2, 2, 9);
                cb1.add_userGraph(strValues[2], 2, data3, 2, 8);
                cb1.add_userGraph(strValues[3], 2, data4, 2, 7);
                cb1.add_userGraph(strValues[4], 2, data5, 2, 11);
                cb1.add_userGraph(strValues[5], 2, data6, 2, 13);
                cb1.add_userGraph(strValues[6], 2, data7, 2, 14);
                cb1.add_userGraph(strValues[7], 2, data8, 2, 10);
                break;
            case 9:
                cb1.add_userGraph(strValues[0], 1, data1, 2, 16);
                cb1.add_userGraph(strValues[1], 2, data2, 2, 9);
                cb1.add_userGraph(strValues[2], 2, data3, 2, 8);
                cb1.add_userGraph(strValues[3], 2, data4, 2, 7);
                cb1.add_userGraph(strValues[4], 2, data5, 2, 11);
                cb1.add_userGraph(strValues[5], 2, data6, 2, 13);
                cb1.add_userGraph(strValues[6], 2, data7, 2, 14);
                cb1.add_userGraph(strValues[7], 2, data8, 2, 10);
                cb1.add_userGraph(strValues[8], 2, data9, 2, 12);
                break;
        }

//        cb1.add_userGraph(strValues[0],1,data1,2,5);
//        cb1.add_userGraph(strValues[1],2,data2,2,4);
//        cb1.add_userGraph(strValues[2],2,data3,2,6);
        //2013. 5. 24  태블릿 매매동향 - 시간대별  체크갯수에 따라서 처리 <<

        addBlock(cb1);
        setPopupMenu(true);
        reSetUI(true);
    }

    public void setEmptyUI(){
        //블럭 생성 부분.
        int w = this.getWidth();
        int h = this.getHeight();

        Block cb1 = makeBlock(0,0,"가격차트");
        cb1.setProperties("지표 Data",1,_cvm.getScaleLineType());
        cb1.setBlockType(Block.BASIC_BLOCK);
//        cb1.setMarginT((int)COMUtil.getPixel(40));
        cb1.setMarginT((int)COMUtil.getPixel(Block.BLOCK_TOP_MARGIN));
        //cb1.setMarginB((int)COMUtil.getPixel(20));
//        cb1.setMarginB((int)COMUtil.getPixel(20) + _cvm.XSCALE_H);  //XScale //2021.04.28 by lyk - kakaopay - 스크롤바 위치가 주블럭 하단일 경우 처리
        cb1.setMarginB((int)COMUtil.getPixel(Block.BLOCK_BOTTOM_MARGIN)); //2021.04.28 by lyk - kakaopay - 스크롤바 위치가 차트 하단일 경우 처리
        if(_cvm.bIsTodayLineChart) {
            cb1.setMarginT((int)COMUtil.getPixel(15));
            cb1.setMarginB((int)COMUtil.getPixel(25));
        }
        cb1.setBounds(0,0,w,h,true);
        addBlock(cb1);
    }
    //2013. 9. 24 관심-세로분할 일 때 차트  형식 변경
    public void setLineFillUI()
    {
        _cvm.bIsLineFillChart = true;
        _cvm.setOnePage(1);

        // 7자리이상 현재가 나올때 짤려서 YSCALE 의 글자크기 조정
        _cvm.mPaint_Text.setTextSize(COMUtil.nFontSize_paint-3);

        //블럭 생성 부분.
        int w = this.getWidth();
        int h = this.getHeight();

        Block cb1 = makeBlock(0,0,"가격차트");
        cb1.setProperties("지표 Data",1,_cvm.getScaleLineType());
        cb1.setBlockType(Block.BASIC_BLOCK);
        int top = 10;
        int bot = 8;
        if(_cvm.bIsOneQStockChart && _cvm.bIsLineFillChart) {
            top = 25;
            bot = 25;
        }
        if(!_cvm.bIsHighLowSign) {
            top = 2;
            bot = 2;
            _cvm.XSCALE_H = (int)COMUtil.getPixel_H(18);
        }
        cb1.setMarginT((int)COMUtil.getPixel(top));
        cb1.setMarginB((int)COMUtil.getPixel(bot));
        cb1.setBounds(0,0,w,h,true);
        addBlock(cb1);
    }
    //2013. 11. 26 현재가 상단 라인차트
    public void setLineUI(String lineCol, String textCol)
    {
        _cvm.bIsLineChart = true;
        _cvm.m_bUseAnimationLine = true;
        if(!_cvm.bIsNewsChart)
            _cvm.setOnePage(1);
        _cvm.setVolDrawType(3);

        if(textCol!=null && !textCol.equals("")) {
            try {
                String[] values = textCol.split(";");
                if(values.length>2) {
                    int[] cols = {Integer.parseInt(values[0]), Integer.parseInt(values[1]), Integer.parseInt(values[2])};
                    _cvm.setScaleTextColor(cols);
                }
            } catch(Exception e) {

            }

        } else {
            //_cvm.setScaleTextColor(CoSys.WHITE);
        }

        if(lineCol!=null && !lineCol.equals("")) {
            try {
                String[] values = lineCol.split(";");
                if(values.length>2) {
                    int[] cols = {Integer.parseInt(values[0]), Integer.parseInt(values[1]), Integer.parseInt(values[2])};
                    _cvm.cLineColor = cols;
                }
            } catch(Exception e) {

            }

        } else {
            _cvm.cLineColor = CoSys.cLineColor;
        }

        //2016.08.23 뉴스차트 xscale 보이게.
//        if(!_cvm.bIsHighLowSign) {
        if(!_cvm.bIsHighLowSign && !_cvm.bIsNewsChart) {
            _cvm.XSCALE_H = 0;
            _cvm.XSCALE_H = (int)COMUtil.getPixel_H(17);
        }
        if(_cvm.bIsHideXYscale)
            _cvm.XSCALE_H = 0;

        if (_cvm.bIsHideXYscale)
            _cvm.XSCALE_H = 0;

        // 7자리이상 현재가 나올때 짤려서 YSCALE 의 글자크기 조정
//    	_cvm.mPaint_Text.setTextSize(COMUtil.nFontSize_paint-3);

        //블럭 생성 부분.
        int w = this.getWidth();
        int h = this.getHeight();

        Block cb1 = makeBlock(0,0,"가격차트");
        int scaleType = 1;
//        if(_cvm.bIsShowVolume) {
//        	scaleType = 2;
//        }
        cb1.setProperties("지표 Data",scaleType,_cvm.getScaleLineType());
        cb1.setBlockType(Block.BASIC_BLOCK);
        int top = 15;
        int bot = 15;
        if(!_cvm.bIsHighLowSign) {
            top = 2;
            bot = 2;
        }

        //2019. 08. 12 by hyh - 타이틀이 최대/최소값과 겹치지 않도록 수정 >>
        if (_cvm.strChartTitle != null && !_cvm.strChartTitle.equals("")) {
            top += 15;
        }
        //2019. 08. 12 by hyh - 타이틀이 최대/최소값과 겹치지 않도록 수정 <<

        cb1.setMarginT((int)COMUtil.getPixel(top));
        cb1.setMarginB((int)COMUtil.getPixel(bot));
        cb1.setMarginR(0);
//        if(_cvm.bIsShowVolume) {
//        	cb1.add("거래량");
//        }
        cb1.setBounds(0,0,w,h,true);
        addBlock(cb1);

        //2019. 03. 14 by hyh - 테크니컬차트 개발 >>
        if (_cvm.bIsTechnical) {
            cb1.add("주가이동평균");
            cb1.add("Pivot");
            cb1.add("Demark");
            cb1.add("MACD");
            cb1.add("RSI");
            cb1.add("ROC");
            cb1.add("TRIX");
            cb1.add("Stochastic Fast");
            cb1.add("Williams R");

            String strMAGraphValue = "5=10=20=50=200=5=10=20=50=200=1=228=0=56=1=0=1=203=101=210=1=0=1=106=158=208=1=0=1=61=180=162=1=0=1=159=255=0=1=0=1=252=135=5=1=0=1=152=116=232=1=0=1=158=40=255=1=0=1=230=100=39=1=0=1=66=66=66=1=0=0=0=0=0=0=0=0=0=0=0=0=2=0=2=0=2=0=2=0=2=1=";
            String[] strValues = strMAGraphValue.split("=");
            int[] graphValues = new int[strValues.length];
            for (int nIndex = 0; nIndex < strValues.length; nIndex++) {
                try {
                    graphValues[nIndex] = Integer.parseInt(strValues[nIndex]);
                } catch (NumberFormatException e) {
                }
            }

            this.applyGraphConfigValue("주가이동평균", graphValues);
        }
        //2019. 03. 14 by hyh - 테크니컬차트 개발 <<
    }
    //    public void setBasicUI_Fut(){//일반적인 봉과 거래량 UI (한화증권 차트 UI)
//    	int w = this.getWidth();
//    	int h = this.getHeight();
//        
//        Block cb1 = makeBlock(0,0,"가격차트");
//        cb1.setProperties("지표 Data",2,_cvm.getScaleLineType());
//        cb1.setBlockType(Block.BASIC_BLOCK);
//        cb1.setMarginT((int)COMUtil.getPixel(25));
//        cb1.setMarginB((int)COMUtil.getPixel(10));
//        cb1.add("미결제약정");
//        cb1.setBounds(0,0,w,h,true);
//        addBlock(cb1);
//        m_strCandleType = "라인";
//    }
    public void setBasicUI_Fut(){//일반적인 봉과 거래량 UI (한화증권 차트 UI)
        int w = this.getWidth();
        int h = this.getHeight();

        Block cb1 = makeBlock(0,0);
        cb1.setProperties("지표 Data",2,_cvm.getScaleLineType());
        cb1.setMarginB(20);
        cb1.setBlockType(Block.BASIC_BLOCK);
        String[] data1 = {"종가"};
        cb1.add_userGraph("미결제약정",1,data1,2,3);
        cb1.add("미결제증감");
        cb1.setBounds(0,0,w,h,true);
        addBlock(cb1);
        //m_strCandleType = "라인";
    }

    //2013.01.04 by LYH >> 등락률 비교차트 추가(섹션별종목차트)
    public void setBasicUI_RateCompare(){//일반적인 봉과 거래량 UI (한화증권 차트 UI)
        int w = this.getWidth();
        int h = this.getHeight();

        Block cb1 = makeBlock(0,0);
        cb1.setProperties("지표 Data",2,_cvm.getScaleLineType());
        cb1.setMarginB(20);
        cb1.setBlockType(Block.BASIC_BLOCK);
        String[] data1 = {"data1"};
        String[] data2 = {"data2"};


        cb1.add_userGraph("data1",1,data1,2,5);
        cb1.add_userGraph("data2",1,data2,2,4);
        cb1.setBounds(0,0,w,h,true);
        addBlock(cb1);
        //m_strCandleType = "라인";
    }
    //2013.01.04 by LYH <<

    public void setOneBlockUI(){//4주간에서 사용하는 하나의 블럭에 라인과 거래량을 그리는 UI
        int w = getWidth();
        int h = getHeight();

        Block cb = makeBlock(0,0);
        cb.setProperties("지표 Data",2,_cvm.getScaleLineType());
        cb.setBlockType(Block.BASIC_BLOCK);
//        cb.add("거래량");
//        cb.add("라인차트");
        addBlock(cb);
        cb.setBounds(0,0,w,(int)(h*0.8),true);
        setPopupMenu(true);
    }
    /*
    public void setSpecialUI(String ui_type){
        if(ui_type.equals("미결제약정")){
            Block cb = makeBlock(0,0);
            cb.setProperties("지표 Data",2,_cvm.getScaleLineType());
            cb.setBlockType(Block.BASIC_BLOCK);
            String[] data = {"기본거래량"};
            cb.add_userGraphBar("기본거래량",1,data,10);
            cb.add("라인차트");
            addBlock(cb);

            Block cb2 = makeBlock(0,1);
            String[] data3 = {"미결제약정"};
            cb2.setProperties("지표 Data",2,_cvm.getScaleLineType());
            cb2.add_userGraphBar("미결제약정",1,data3,4);
            addBlock(cb2);
            
        }else if(ui_type.equals("현재가/이론가")){
            //setOneBlockUI();
            Block cb = makeBlock(0,0);
            String[][] data = {{"콜현재가"},{"콜이론가"},{"풋현재가"},{"풋이론가"}};
            cb.setProperties("지표 Data",1,_cvm.getScaleLineType());
            xscale.setProperties("Text", "/", ":");
            cb.setBlockType(Block.BASIC_BLOCK);
            cb.add_userGraph("콜현재가",1,data[0],2,5);
            cb.add_userGraph("콜이론가",2,data[1],2,7);
            cb.add_userGraph("풋현재가",2,data[2],2,6);
            cb.add_userGraph("풋이론가",2,data[3],2,4);
            addBlock(cb);
            setStyle(2);//회색
        }
        setPopupMenu(true);
    }
    */
    public void setOneBlockUI(String[] datakind_jisu,String[] datakind_vol){
        int w = getWidth();
        int h = getHeight();

        Block cb = makeBlock(0,0);
        cb.setProperties("지표 Data",2,_cvm.getScaleLineType());
        cb.setBlockType(Block.BASIC_BLOCK);
        //cb.add("거래량");
        //cb.add("라인차트");
        cb.add_userGraphBar("기본거래량", 0,datakind_vol,3);
        cb.add_userGraph("지수",1,datakind_jisu,0,2);
        addBlock(cb);
        cb.setBounds(0,0,w,(int)(h*0.8),true);
        setPopupMenu(true);
    }
    public void setOneBlock_JBONGUI(){//일본식 봉과 라인차트를 한화면에 그리는 UI
        int w = getWidth();
        int h = getHeight();

        Block cb = makeBlock(0,0,"가격차트");
        cb.setProperties("지표 Data",2,_cvm.getScaleLineType());
        cb.setBlockType(Block.BASIC_BLOCK);
        String[] data1 = {"지수"};
        cb.add_userGraph("지수",1,data1,2,4);
        cb.setMarginB(20);
        addBlock(cb);
        cb.setBounds(0,0,w,(int)(h*0.8),true);
        setPopupMenu(true);
    }
    public void setBasisUI(){
        Block cb1 = makeBlock(0,0);
        cb1.setProperties("지표 Data",1,_cvm.getScaleLineType());
        cb1.setMarginB(20);
        cb1.setBlockType(Block.BASIC_BLOCK);
        String[] data1 = {"시가"};
        String[] data2 = {"고가"};
        cb1.add_userGraph("선물1",1,data1,2,0);
        cb1.add_userGraph("선물2",2, data2,2,1);
        addBlock(cb1);
        //cb1.setBounds(50,0,w,(int)(h*0.4),true);
        Block cb2 = makeBlock(0,1);
        cb2.setProperties("지표 Data",1,_cvm.getScaleLineType());
        cb2.setMarginB(20);
        String[] data3 = {"저가"};
        cb2.add_userGraph("Basis",1, data3,2,2);
        addBlock(cb2);
        //cb2.setBounds(0,(int)(h*0.4),w-50,(int)(h*0.4),true);
        setPopupMenu(true);

    }
    public void setfJisuUI(){
        int w = getWidth();
        int h = getHeight();

        Block cb1 = makeBlock(0,0);
        cb1.setProperties("지표 Data",2,_cvm.getScaleLineType());
        cb1.setMarginB(20);
        cb1.setBlockType(Block.BASIC_BLOCK);
        String[] data1 = {"종가"};
        String[] data2 = {"해외지수"};
        cb1.add_userGraph("종가",0,data1,2,4);
        cb1.add_userGraph("해외지수",1, data2,2,5);
        addBlock(cb1);
        cb1.setBounds(0,0,w,(int)(h*0.8),true);
        setPopupMenu(true);
    }

    public void setInvestUI(){
        Block cb1 = makeBlock(0,0);
        cb1.setProperties("지표 Data",1,_cvm.getScaleLineType());
        cb1.setMarginB(20);
        cb1.setBlockType(Block.BASIC_BLOCK);
        String[] data1 = {"외국인"};
        String[] data2 = {"은행"};
        String[] data3 = {"투신"};
        String[] data4 = {"증권"};
        String[] data5 = {"개인"};
        cb1.add_userGraph("외국인",1,data1,2,4);
        cb1.add_userGraph("은행",2, data2,2,5);
        cb1.add_userGraph("투신",2,data3,2,6);
        cb1.add_userGraph("증권",2, data4,2,7);
        cb1.add_userGraph("개인",2,data5,2,8);
        addBlock(cb1);
        setPopupMenu(true);
    }

    public void setUI(int blockCount, String[] graph_title, String[] properties){
        int w = getWidth();
        int h = getHeight();

        Block[] cb = new Block[blockCount];
        int gab=h/blockCount;
        for(int i=0;i<blockCount;i++){
            cb[i] = makeBlock(0,i,graph_title[i]);
            cb[i].setProperties("지표 Data",1,_cvm.getScaleLineType());
            addBlock(cb[i]);
            cb[i].setBounds(0,gab*i,w,gab,true);
        }
    }
    boolean start=false;
    byte[] buf;
    public void setData(int pos, byte[] data) throws Exception {

    }

    public void setBounds2(int left, int top, int right, int bottom) {
        super.setBounds2(left, top, right, bottom);
        //2011.08.05 by LYH >> 시세바 추가
        if(chartItem != null)
        {
            ViewGroup.MarginLayoutParams paramChart = (ViewGroup.MarginLayoutParams)this.getLayoutParams();
            ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams)chartItem.getLayoutParams();
            param.width = right;
            param.setMargins(paramChart.leftMargin, paramChart.topMargin, 0, 0);
            chartItem.setFrame(right);

            //2013. 6. 13 ELW-ETF타입일 때 차트상단 종목정보 (ChartItemView) 미표시 처리 >>
            if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
            {
                chartItem.setText(_cdm.codeItem, 0, 0);
            }
            //2013. 6. 13 ELW-ETF타입일 때 차트상단 종목정보 (ChartItemView) 미표시 처리 <<
        }
        //2011.08.05 by LYH <<
    }

    //2011.08.05 by LYH >> 시세바 추가
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        //2013.09.13 >> 종합차트 등에 탭차트 로 들어가는 차트 타입 추가
        //if(chartItem == null)
        //if(!_cvm.bStandardLine && !_cvm.bSubChart && chartItem == null)
//        if(!_cvm.bIsLineChart && !_cvm.bStandardLine && !_cvm.bSubChart && chartItem == null)
//        //2013.09.13 <<
//        {
//            ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams)this.getLayoutParams();
//            //2012. 8. 17 차트영역의 현재가정보 (ChartItemView) 태블릿에서 글자 아랫쪽 짤리던 현상 해결
//            RelativeLayout.LayoutParams params;
//            if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
//            {
//                params = new RelativeLayout.LayoutParams(param.width ,(int)COMUtil.getPixel(30));
//            }
//            else
//            {
//                params = new RelativeLayout.LayoutParams(param.width ,(int)COMUtil.getPixel(20));
//            }
//
//            params.leftMargin=param.leftMargin;
//            params.topMargin=param.topMargin;
//            chartItemLayout = new RelativeLayout(context);
//            chartItemLayout.setLayoutParams(params);
//            chartItemLayout.setTag("chartItem");
//            chartItem = new ChartItemView(context, chartItemLayout);
//            chartItem.setLayoutParams(params);
//            chartItem.setBasicUI();
//            chartItem.setBaseChart(this);
//            chartItemLayout.addView(chartItem);
//            this.layout.addView(chartItemLayout);
//        }
    }

    public void setData(byte[] data) {
        super.setData(data);
        if(chartItem != null)
            chartItem.setText(_cdm.codeItem, 0, 0);
    }
    public void setData_data(String[] strDates, double[] strOpens, double[] strHighs, double[] strLows, double[] strCloses, double[] strVolumes, double[] strValues, double[] strRights, double[] strRightRates) {
        super.setData_data(strDates, strOpens, strHighs, strLows,strCloses,strVolumes,strValues,strRights,strRightRates);
        if(chartItem != null)
            chartItem.setText(_cdm.codeItem, 0, 0);
    }
    public void repaintRT(String mstVal, byte[] data){
        super.repaintRT(mstVal, data);
        if(chartItem != null)
            chartItem.setText(_cdm.codeItem, 1, 0);
    }
    public void setVisible(boolean visible) {
        if(visible) {
            this.setVisibility(View.VISIBLE);
            if(chartItem != null)
                chartItem.layout.setVisibility(View.VISIBLE);
            //2016.11.17 by LYH >> 멀티차트 확대/축소 시 수치조회창 처리
            if(_cvm.isCrosslineMode)
            {
                if(viewP != null)
                    viewP.setVisibility(View.VISIBLE);
            }
            //2016.11.17 by LYH >> 멀티차트 확대/축소 시 수치조회창 처리 end
        } else {
            this.setVisibility(View.GONE);
            if(chartItem != null)
                chartItem.layout.setVisibility(View.GONE);
            //2016.11.17 by LYH >> 멀티차트 확대/축소 시 수치조회창 처리
            if(viewP != null)
                viewP.setVisibility(View.GONE);
            //2016.11.17 by LYH >> 멀티차트 확대/축소 시 수치조회창 처리 ends
        }
    }
    public void destroy() {

        if(chartItem != null)
        {
            //2011.09.06 by LYH >> 메모리 릭 개선
            chartItem.destroyDrawingCache();
            COMUtil.unbindDrawables(chartItem.layout);
            //chartItem.layout.removeAllViewsInLayout();
            this.layout.removeView(chartItem);
            //chartItem.destroyDrawingCache();
            //2011.09.06 by LYH <<

            chartItem=null;
        }
        super.destroy();
    }
    //2011.08.05 by LYH <<
    public void setExtendButton()
    {
        if(chartItem != null)
            chartItem.setExtendButton();
    }

    public void setSkinType(int nSkinType)
    {
        if(nSkinType > 1)
            nSkinType = COMUtil.SKIN_WHITE;
        if(chartItem!=null)
            chartItem.setSkinType(nSkinType);

        _cvm.setSkinType(nSkinType);
        for(int i=0; i<blocks.size(); i++)
        {
            Block block = blocks.get(i);
            block.setSkinType(nSkinType);
        }
        for(int i=0; i<rotate_blocks.size(); i++)
        {
            Block block = rotate_blocks.get(i);
            block.setSkinType(nSkinType);
        }
        //분석툴 스킨타입에 따른 색상 변경 (2012.08.04 by lyk)
        if(analTools!=null) {
            AnalTool at;
            int analToolCnt = analTools.size();
            for(int i=0; i<analToolCnt; i++) {
                at = analTools.get(i);
                at.setSkinColor();
            }
        }
    }

    //2012.07.13 by LYH >> 시세바 보기 옵션 추가. 
    public void setMarginT(boolean bShow)
    {
        if(bShow)
        {
            if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB)) {
                _cvm.setMarginT((int)COMUtil.getPixel(22));
            }
            else
            {
                _cvm.setMarginT((int)COMUtil.getPixel(18));
            }
        }
        else {
            if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB) || _cvm.bIsMiniBongChart) {
                _cvm.setMarginT(0);
            }
            else
            {
                _cvm.setMarginT((int)COMUtil.getPixel(0));
            }
        }
    }

    public void showChartItem(boolean bShow) {
        if (chartItem != null) {
            if (bShow) {
                if (COMUtil.deviceMode.equals(COMUtil.HONEYCOMB)) {
                    _cvm.setMarginT((int) COMUtil.getPixel(22));
                }
                else {
                    _cvm.setMarginT((int) COMUtil.getPixel(18));
                }
                if (this.getVisibility() == View.VISIBLE)
                    chartItemLayout.setVisibility(View.VISIBLE);
            }
            else {
                if (COMUtil.deviceMode.equals(COMUtil.HONEYCOMB) || _cvm.bIsMiniBongChart) {
                    _cvm.setMarginT(0);
                }
                else {
                    _cvm.setMarginT((int) COMUtil.getPixel(0));
                }
                chartItemLayout.setVisibility(View.INVISIBLE);
            }
        }
    }
    //2012.07.13 by LYH <<

    public void setBase(Base base) {
        this.base = base;
    }

    //2015.01.08 by LYH >> 3일차트 추가
    public void setBasicUI_Standard_3Day(){
        int w = this.getHeight();
        int h = this.getHeight();

        String[] data1 = {"시가"};
        String[] data2 = {"고가"};
        String[] data3 = {"저가"};
        String[] data4 = {"미결제약정"};

        Block cb1 = makeBlock(0,0,"가격차트");
        cb1.setProperties("지표 Data",1,_cvm.getScaleLineType());
        cb1.setMarginT((int)COMUtil.getPixel(25));
        cb1.setMarginB((int)COMUtil.getPixel(5));
        cb1.setBlockType(Block.BASIC_BLOCK);
        cb1.add("거래량");
        cb1.setBounds(0,0,w,h,true);
        addBlock(cb1);

        Block cb2 = makeBlock(0,1);
        cb2.setProperties("지표 Data",1,_cvm.getScaleLineType());
        cb2.setMarginT((int)COMUtil.getPixel(25));
        cb2.setMarginB((int)COMUtil.getPixel(5));
        cb2.add_userGraph("전일주가", 1, data1, 2, 4);
        cb2.add_userGraphBar_Bar("전일거래량", 1, data2, 2, 6);
        cb2.setBounds(0,0,w,h,true);
        addBlock(cb2);

        Block cb3 = makeBlock(0,2);
        cb3.setProperties("지표 Data",1,_cvm.getScaleLineType());
        cb3.setMarginT((int)COMUtil.getPixel(25));
        cb3.setMarginB((int)COMUtil.getPixel(5));
        cb3.add_userGraph("2일전주가", 1, data3, 2, 4);
        cb3.add_userGraphBar_Bar("2일전거래량", 1, data4, 2, 6);
        cb3.setBounds(0,0,w,h,true);
        addBlock(cb3);

        _cvm.setVolDrawType(0);
        _cvm.bStandardLine = true;
        //m_strCandleType = "라인";
        setPopupMenu(false);
    }
    //2015.01.08 by LYH << 3일차트 추가

    //2016.08.25 by LYH >> 2일 라인차트 타입 추가
    public void setBasicUI_2Day(){
        _cvm.bIsLine2Chart = true;
        _cvm.setOnePage(1);

        int w = this.getHeight();
        int h = this.getHeight();

        String[] data1 = {"저가"};
//        String[] data2 = {"기본거래량"};

        _cvm.XSCALE_H = 0;

        Block cb1 = makeBlock(0,0,"가격차트");
        cb1.setProperties("지표 Data",1,_cvm.getScaleLineType());
        cb1.setMarginT((int)COMUtil.getPixel_H(0));
        cb1.setMarginB((int)COMUtil.getPixel(2));
        cb1.setBlockType(Block.BASIC_BLOCK);
        cb1.add_userGraph("전일주가", 2, data1, 2, 1);
//        cb1.add_userGraphBar_Bar("거래량", 1, data2, 2, 6);
        cb1.setBounds(0,0,w,h,true);
        addBlock(cb1);



        //_cvm.bStandardLine = true;
        //m_strCandleType = "라인";
        setPopupMenu(false);
    }
    //2016.08.25 by LYH << 2일 라인차트 타입 추가
    boolean m_bIsFirstData = false;
    public void setAccrueData(String arrData)
    {
        String[] sepDatas = arrData.split("=");
        String sDataField = sepDatas[0];

        //2019. 03. 12 by hyh - 투자자 업종 가로 바차트 추가 >>
        if (sDataField.equals("updownData")) {
            initDataInfo("");
            String sName = sepDatas[1];
            String sValue = sepDatas[2];
            if (sValue.endsWith(";")) {
                sValue = sValue.substring(0, sValue.length() - 1);
            }

            String[] names = sName.split(";");
            String[] values = sValue.split(";");
            _cdm.accrueNames = names;

            int nCount = values.length;

            double[] dDatas = new double[nCount];
            for (int i = 0; i < values.length; i++) {
                try {
                    dDatas[i] = Double.parseDouble(values[i]);
                } catch (Exception e) {
                    dDatas[i] = 0;
                }
            }
            if (_cvm.getViewNum() == 0)
                _cvm.setViewNum(dDatas.length);

            _cdm.setSubPacketData("data1", dDatas);
            makeGraphData();
            repaintAll();
            return;
        }
        //2019. 03. 12 by hyh - 투자자 업종 가로 바차트 추가 <<

        if(sDataField.equals("repaintall")) {
            makeGraphData();
            repaintAll();
            _cvm.setOnePage(1);
            reset();
            return;
        }

        if(sDataField.equals("period")) {
            String val = sepDatas[1];
            _cdm.setDateType(Integer.parseInt(val));
            if(sepDatas.length>2)
            {
            	val = sepDatas[2];
            	_cdm.setDateFormat(ChartUtil.getPacketFormatIndex(val));
            	_cdm.setPacketFormat("자료일자",val);
            }
            xscale.dataChanged();
            return;
        }
        if(sDataField.equals("changetitle"))
        {
        	String val = sepDatas[1];
   			String[] titles = val.split(";");

			if(titles.length >0)
			{
				AbstractGraph graph;
				for(int i=0; i<basic_block.getGraphs().size(); i++)
				{
					graph = (AbstractGraph)basic_block.getGraphs().elementAt(i);
					for(int j=0;j<graph.getDrawTool().size(); j++)
					{
						DrawTool dt = (DrawTool)graph.getDrawTool().elementAt(j);
						if(dt != null)
						{
							if(titles.length>i)
								dt.subTitle = titles[i];
						}
					}
				}
				resetTitleBoundsAll();//블럭 타이틀 그리기.				
			}
			return;
        }

        if(sepDatas.length>1)
        {
	        String sValue = sepDatas[1];
	        if(sValue.endsWith(";"))
	        {
	            sValue = sValue.substring(0,sValue.length()-1);
	        }
	
	        String[] sValues = sValue.split(";");
	        if(sDataField==null) {
	            sDataField = "data1";
	        }
	        //_cdm.accrueNames = names;
	
	        //2014.05.22 by LYH >> ChartPacketDataModel로 데이터 값 이동.
	        int nCount = sValues.length;
	
	
	        if(sDataField.equals("initdata")) {
	            _cdm.initData(nCount);
	            _cvm.setViewNum(nCount);
	            _cdm.accrueNames = null;
	            return;
	        }
	
	        if(sDataField.equals("자료일자"))
	        {
                boolean bIsReverse = false;
                if (sepDatas.length > 2) {
                    String strOption = sepDatas[2];

                    if (strOption.equals("R")) {
                        bIsReverse = true;
                    }
                }

                String[] dDatas = new String[nCount];
                int nDataIndex = 0;
                for (int i = 0; i < nCount; i++) {
                    if (bIsReverse) {
                        nDataIndex = sValues.length - i - 1;
                    }
                    else {
                        nDataIndex = i;
                    }

                    try {
                        dDatas[nDataIndex] = sValues[i];
                        //2016.10.24 야간선물옵션 주기:분 상태에서 24시간기준 표기방법 수정 >>
                        int nData = Integer.parseInt(dDatas[nDataIndex]);
                        if (nData < 1000000 && nData / 100 >= 2400 && nData % 100 != 8888) {
                            nData = (nData / 100 - 2400) * 100;
                            dDatas[nDataIndex] = String.format("%06d", nData);
                        }
                        //2016.10.24 야간선물옵션 주기:분 상태에서 24시간기준 표기방법 수정 <<

                    } catch (Exception e) {
                        dDatas[nDataIndex] = "";
                        if (i == nCount-1 && sValues[i].equals("장개시")) {
                            dDatas[nDataIndex] = "090000";
                        }
                    }
                }

	            if(_cvm.getViewNum() == 0)
	                _cvm.setViewNum(dDatas.length);

	            _cdm.setData_data(sDataField, dDatas);
                m_bIsFirstData = true;
	        }
	        else if(sDataField.equals("titles"))
	        {
                _cvm.bGreenType = false;
                if(sepDatas.length>2)
                {
                    String strOption = sepDatas[2];
                    if(strOption.equals("green"))
                        _cvm.bGreenType = true;
                    else
                        _cvm.bGreenType = false;
                }
	            _cdm.accrueNames = sValues;
                if(_cvm.bIsUpdownGridChart) {
                    setBounds2(chart_bounds.left, chart_bounds.top, chart_bounds.width(), (int) ((sValues.length) * COMUtil.getPixel_H(40)));
                    ScrollView sView = (ScrollView) this.getParent();
                    sView.scrollTo(0, 0);
                }
	        }
            else if(sDataField.equals("titlepos"))
            {
                if(sValues[0].equals("center"))
                {
                    _cvm.m_titlePos = Gravity.CENTER;
                }
            else
                {
                    _cvm.m_titlePos = Gravity.LEFT;
                }
            }
	        else
	        {
                if(m_bIsFirstData)
                    _cdm.setPriceFormat("× 1");

//                boolean bIsReverse = false;

                double[] dDatas = new double[nCount];
                int nDataIndex = 0;
                boolean bZero = false;  //ASSET_LINE_FILL
//                if (bIsReverse) {
                    for (int i = 0; i < sValues.length; i++) {
//                        if (bIsReverse) {
//                            nDataIndex = sValues.length - i - 1;
//                        }
//                        else {
                            nDataIndex = i;
//                        }

                        try {
                            dDatas[nDataIndex] = Double.parseDouble(sValues[i]);
                            if(_cvm.getAssetType()== ChartViewModel.ASSET_LINE_FILL && dDatas[i] == 0)
                                bZero = true;

                        } catch (Exception e) {
                            dDatas[nDataIndex] = 0;
                        }
                    }
//                }
                if (_cvm.getViewNum() == 0)
                    _cvm.setViewNum(dDatas.length);

                if(bZero)
                {
                    blocks.get(0).setMarginB((int) COMUtil.getPixel_H(5));
                    setUI1();
                }
                if(_cvm.bIsUpdownGridChart) {
                    _cdm.setSubPacketData(sDataField, dDatas);
                }
                else
                    _cdm.setData_data(sDataField, dDatas);
                if(_cvm.nAssetType == ChartViewModel.ASSET_LINE_FILL && _cdm.getCount()>0)
                {
                    curPoint.x = _cvm.getBounds().right;
                }

                m_bIsFirstData = false;

                if (sepDatas.length > 2) {
                    String strOption = sepDatas[2];

//                    if (strOption.equals("R")) {
//                        bIsReverse = true;
//                    }
//                    else {
                    try {
                        int nDotPos = Integer.parseInt(strOption);
                        if (nDotPos == 2) {
                            if (m_bIsFirstData)
                                _cdm.setPriceFormat("× 0.01");
                            _cdm.setPacketFormat(sDataField, "× 0.01");
                        }
                        else if (nDotPos == 4) {
                            if (m_bIsFirstData)
                                _cdm.setPriceFormat("× 0.0001");
                            _cdm.setPacketFormat(sDataField, "× 0.0001");
                        }
                        else if (nDotPos == 0) {
                            if (m_bIsFirstData)
                                _cdm.setPriceFormat("× 1");
                            _cdm.setPacketFormat(sDataField, "× 1");
                        }
                    } catch (Exception e) {

                    }
//                    }
                }
	        }
        }
        //_cdm.accrueDatas = values;
        //2014.05.22 by LYH << ChartPacketDataModel로 데이터 값 이동.
    }

    public void setBasicUI_NetBuy(){//일반적인 봉과 거래량 UI (한화증권 차트 UI)
        int w = this.getHeight();

        int bHeight = getBlockUnitHeight(2);

        Block cb1 = makeBlock(0,0,"가격차트");
        cb1.setProperties("지표 Data",1,_cvm.getScaleLineType());
        cb1.setBlockType(Block.BASIC_BLOCK);
        if(m_bHaveMA)
            cb1.add("주가이동평균");
        cb1.setBounds(0,0,w,bHeight*2,true);
        addBlock(cb1);

        //cb1.setMarginT((int)COMUtil.getPixel(40));
        cb1.setMarginT((int)COMUtil.getPixel(35));
        cb1.setMarginB((int)COMUtil.getPixel(10));

        cb1.add("기관 순매수 누적");
        cb1.add("외국인 순매수 누적");
        setPopupMenu(false);
    }

    public void setBasicUI_Migyul(){
        if(getGraph("(선물옵션)미결제 약정") == null)
        {
            addBlock("(선물옵션)미결제 약정");
            removeBlock("(선물옵션)미결제 증감");
            removeBlock("거래량");
            for ( int i=0; i<blocks.size(); i++) {
                Block cb = blocks.get(i);
                cb.setHideDelButton(true);
            }
        }
    }

    public void setBasicUI_Migyul_UpDown(){
        if(getGraph("(선물옵션)미결제 증감") == null) {
            addBlock("(선물옵션)미결제 증감");
            removeBlock("(선물옵션)미결제 약정");
            removeBlock("거래량");
            for ( int i=0; i<blocks.size(); i++) {
                Block cb = blocks.get(i);
                cb.setHideDelButton(true);
            }
        }
    }


    public void setBasicUI_Basis(){
        addBlock("(선물)베이시스");
        removeBlock("거래량");
        for ( int i=0; i<blocks.size(); i++) {
            Block cb = blocks.get(i);
            cb.setHideDelButton(true);
        }
    }
    //2017.05.15 fx차트 거래량 삭제 >>
    public void setBasicUI_noVolume(){
        int w = this.getHeight();

        int bHeight = getBlockUnitHeight(2);

        Block cb1 = makeBlock(0,0,"가격차트");
        cb1.setProperties("지표 Data",1,_cvm.getScaleLineType());
        cb1.setBlockType(Block.BASIC_BLOCK);
        if(m_bHaveMA)
            cb1.add("주가이동평균");
        cb1.setBounds(0,0,w,bHeight*2,true);
        addBlock(cb1);

        cb1.setMarginT((int)COMUtil.getPixel(25));
        cb1.setMarginB((int)COMUtil.getPixel(10));

        setPopupMenu(false);
    }
}




