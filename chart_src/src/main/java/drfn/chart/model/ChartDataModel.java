package drfn.chart.model;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import drfn.chart.scale.XScale;
import drfn.chart.util.COMUtil;
import drfn.chart.util.ChartUtil;
import drfn.chart.util.CodeItemObj;

public class ChartDataModel implements DataChangable{
    //    public NeoChart2 _chart;
    public static int MAX_VIEW_DATA=400;    //maximun size of data to show in an one page
    public final static int DATA_NOTYPE = 0;
    public final static int DATA_DAY = 1;
    public final static int DATA_WEEK = 2;
    public final static int DATA_MONTH = 3;
    public final static int DATA_MIN = 4;
    public final static int DATA_TIC = 5;
    public final static int DATA_TEXT = 6;
    public final static int DATA_SECOND = 7;
    public final static int DATA_YEAR = 8;
    private int REAL_DATA_SET=9;

    private int VIEW_MARGIN = 0;
    public int baseLineIndex = 0;

    public Vector<String> _basicDataTitle=null;          //시고저종. 기본데이터를 저장하는 벡터
    public Vector<String> _dataTitles;          //시고저종. 기본데이터를 저장하는 벡터
    private Hashtable<String, ChartPacketDataModel> _dataModels;          //데이터 종류의 스트링 이름을 int형 key로 저장
    private Vector<ChartRealPacketDataModel> realInfo;

    private int dataType=1;     // 날짜타입 UD=0:notype 1:일 2:주 3:월 4:분 5:틱 6:Text 7:보조지표 8:년
    private String[] basic_data;

    private int PACKET_LENGTH=0;
    private int dateFormat; //날짜 포맷 YYYYMMDD
    private int priceFormat=11;//가격 포맷//× 1이 디폴트

    private int real_term=1;//실시간 데이터 반영주기
    private boolean has_pivot_data=false;//서버로부터 피봇데이터를 받는지
    public boolean has_nujum_vol = false;
    private int addType=0;//실시간 데이터 addType
    private String[] cre;
    private Vector<String> _useRealTitle = new Vector<String>();//실시간 데이터를 사용하는 데이터 타이틀
    //private int real_cnt=0;
    private boolean time_check=false;
    private String allVol, preAllVol;//누적 거래량
    private String[] basic = {"시가","고가","저가"};
    private Vector<DataChangedListener> listeners;
    public CodeItemObj codeItem=null;
    public String[] compareCode = {"","","","",""};

    //2012.11.27 by LYH >> 진법 및 승수 처리.
    public int nDispScale = -1;      // 진법
    public int nTradeMulti = -1;     // 거래승수
    public int nLogDisp = -1;        // 진법자리수 ("'" 다음 데이터 사이즈)
    //2012.11.27 by LYH <<
    public boolean m_bReverseTime = false;

    //2013.10.02 by LYH >> 업종 거래량 단위 표시(TDC). <<
    public int m_nVolumeUnit = 1;

    public boolean m_bRealUpdate = false;	//2015.01.08 by LYH >> 3일차트 추가

    private boolean bStandardLine = false;	//2015. 2. 5 선옵 1분선 기초자산/미결제약정 실시간

    private String m_strOpenTime = null;

    public String[] accrueNames = null;

    //2019. 03. 12 by hyh - 투자자 업종 가로 바차트 추가 >>
    public double[] accrueOpens = null;
    public double[] accrueHighs = null;
    public double[] accrueLows = null;
    public double[] accrueCloses = null;
    public double[] accrueBases = null;
    //2019. 03. 12 by hyh - 투자자 업종 가로 바차트 추가 <<
    public String strAvgBuyPrice = "";  //2021.02.18 by HJW - 매입평균선 추가

    //==========================================
    // 생성자
    //==========================================
    public ChartDataModel(){
        listeners = new Vector<DataChangedListener>();
        //String[] packetInfos = {"자료일자", "시가", "고가", "저가", "종가", "기본거래량"};
        //2023.03.15 by SJW - 애프터마켓 추가 >>
//        String[] packetInfos = {"자료일자", "시가", "고가", "저가", "종가", "기본거래량", "거래대금", "매수거래량","매도거래량","락구분","락비율"};
        String[] packetInfos = {"자료일자", "시가", "고가", "저가", "종가", "기본거래량", "거래대금", "매수거래량", "매도거래량", "sessionIds", "락구분", "락비율"};
        //2023.03.15 by SJW - 애프터마켓 추가 <<
        for(int i=0; i<packetInfos.length; i++) {
            _useRealTitle.addElement(packetInfos[i]);
        }
        codeItem = new CodeItemObj();
    }
    public void setRealPacketData(String[] rl){
        synchronized(this) {
            String rt="";
            realInfo = new Vector<ChartRealPacketDataModel>(10);
            ChartRealPacketDataModel crpdm=null;
            for(int i=0;i<rl.length;i+=3){
                if(!rt.equals(rl[i])){
                    rt = rl[i];
                    crpdm=new ChartRealPacketDataModel(rt);
                    realInfo.addElement(crpdm);
                }
                if(crpdm!=null) crpdm.setProp(rl[i+1],rl[i+2]);
            }
        }
    }
    //==========================================
    // 실시간 데이터를 add해주는 방식을 지정
    //==========================================
    public void setRealAddType(int type){
        if(type<4)has_nujum_vol = false;
//        has_nujum_vol = true;
        addType = type;
        m_nRealIndex = -1;
    }
    public void setPacketFormat(String title, String format){
        ChartPacketDataModel cpdm = (ChartPacketDataModel)_dataModels.get(title);
        cpdm.setPacketFormat( ChartUtil.getPacketFormatIndex(format));
    }
    //==========================================
    // xfm파일에서 받아온 패킷정보를 분석하여 데이터 모델을 구성한다
    //==========================================
    public void setPacketData(String[][] prop){
        if(prop==null) return;

        PACKET_LENGTH=0;
        if(_basicDataTitle==null) {
            _basicDataTitle = new Vector<String>();
        }
        if(_dataModels == null) {
            _dataModels  = new Hashtable<String, ChartPacketDataModel>();
        }
        if(_dataTitles == null) {
            _dataTitles = new Vector<String>();
        }

        _basicDataTitle.removeAllElements();
        _dataModels.clear();
        _dataTitles.removeAllElements();

        for(int i=0;i<prop.length;i++){
            if(prop[i][0].equals("종가")){
                priceFormat=ChartUtil.getPacketFormatIndex(prop[i][2]);
            }else if(prop[i][0].equals("자료일자")){
                if((prop[i][2]).equals("문자"))dataType=DATA_TEXT;
                else dateFormat = ChartUtil.getPacketFormatIndex(prop[i][2]);
            }
//            if(prop[5].equals("유")){//실시간 데이터 사용여부
//                REAL_DATA_SET++;
//                //_useRealTitle.addElement(prop[1]);
//            }
            ChartPacketDataModel cpdm = new ChartPacketDataModel();
            cpdm.setProperties(prop[i][0],Integer.parseInt(prop[i][1]),prop[i][2],prop[i][3]);
            if(prop[i][0].equals("종가")) {
                priceFormat = cpdm.getPacketFormat();
            }
            _dataModels.put(prop[i][0], cpdm);
            _basicDataTitle.addElement(prop[i][0]);
//            if(!prop[i][0].equals("락구분")&&!prop[i][0].equals("등락부호")&&!prop[i][0].equals("전일비")) _dataTitles.addElement(prop[i][0]);
            PACKET_LENGTH+=Integer.parseInt(prop[i][1]);
        }
        ChartUtil.initPacketFieldFormat();
    }

    /** 비교차트에서 사용 **/
    public void setPacketData2(String[][] prop){
        if(prop==null) return;

        PACKET_LENGTH=0;
        _basicDataTitle.removeAllElements();

        for(int i=0;i<prop.length;i++){
            if(!prop[i][0].equals("자료일자")){
                ChartPacketDataModel cpdm = new ChartPacketDataModel();
                cpdm.setProperties(prop[i][0],Integer.parseInt(prop[i][1]),prop[i][2],prop[i][3]);
                _dataModels.put(prop[i][0], cpdm);
            }

            _basicDataTitle.addElement(prop[i][0]);

            PACKET_LENGTH+=Integer.parseInt(prop[i][1]);
        }
        ChartUtil.initPacketFieldFormat();
    }
    public void setPriceFormat(String data) {
        priceFormat=ChartUtil.getPacketFormatIndex(data);

        ChartPacketDataModel cpdm;
        for(int i=0;i<_basicDataTitle.size();i++){
            cpdm = (ChartPacketDataModel)_dataModels.get((String)_basicDataTitle.elementAt(i));
            if(cpdm.getPacketTitle().equals("시가")||cpdm.getPacketTitle().equals("고가")||cpdm.getPacketTitle().equals("저가")||cpdm.getPacketTitle().equals("종가"))
                cpdm.setPacketFormat(priceFormat);
        }
    }
    //2012.11.27 by LYH >> 진법 및 승수 처리.
    public void setPriceFormat(String data, int nScale, int nDecPoint, int nLogDisp) {
        this.nTradeMulti = nDecPoint;
        this.nDispScale = nScale;
        this.nLogDisp = nLogDisp;

        priceFormat=ChartUtil.getPacketFormatIndex(data);

        ChartPacketDataModel cpdm;
        for(int i=0;i<_basicDataTitle.size();i++){
            cpdm = (ChartPacketDataModel)_dataModels.get((String)_basicDataTitle.elementAt(i));
            if(cpdm.getPacketTitle().equals("시가")||cpdm.getPacketTitle().equals("고가")||cpdm.getPacketTitle().equals("저가")||cpdm.getPacketTitle().equals("종가"))
            {
                cpdm.setPacketFormat(priceFormat);
                cpdm.nTradeMulti = nTradeMulti;
                cpdm.nDispScale = nDispScale;
                cpdm.nLogDisp = nLogDisp;
            }
        }

        //2013.02.27 by LYH >> 이평에 진법 처리.
        Enumeration<ChartPacketDataModel> enumCPDM = _dataModels.elements();
        while(enumCPDM.hasMoreElements()){
            cpdm = (ChartPacketDataModel)enumCPDM.nextElement();
            if(cpdm.getPacketTitle().startsWith("이평"))
            {
                cpdm.setPacketFormat(priceFormat);
                cpdm.nTradeMulti = nTradeMulti;
                cpdm.nDispScale = nDispScale;
                cpdm.nLogDisp = nLogDisp;
            }
        }
        //2013.02.27 by LYH <<
    }
    //2012.11.27 by LYH <<

    //2012.11.27 by LYH >> 진법 및 승수 처리.
    public void setPriceFormat_Investor(String data, String strPacket, int nScale, int nDecPoint) {
		//int nDotPos = Integer.parseInt(strDotPos);
//		switch(nDecPoint)
//		{
//		case 2:
//			setPacketFormat(strPacket, "× 0.01");
//			break;
//		case 3:
//			setPacketFormat(strPacket, "× 0.001");
//		case 4:
//			setPacketFormat(strPacket, "× 0.0001");
//			break;
//
//		}

        nTradeMulti = nDecPoint;
        nDispScale = nScale;

        priceFormat=ChartUtil.getPacketFormatIndex(data);

        ChartPacketDataModel cpdm;
        cpdm = (ChartPacketDataModel)_dataModels.get(strPacket);
        if(cpdm != null)
        {
            cpdm.setPacketFormat(priceFormat);
            cpdm.nTradeMulti = nTradeMulti;
            cpdm.nDispScale = nDispScale;
        }
    }
    //2012.11.27 by LYH <<
    //==========================================
    // NeoChart2에서 setPacketData2를 통해 얻은 byte[]을 패킷모델에 따라 분류하여 
    // 해당 ChartPackeetDataModel에 저장한다
    //==========================================
    public synchronized void initData(int len){
        //2013. 10. 23 데이터 없이 흰화면일 때 실시간 받으면 봉 그리기 처리>>
//    	if(len<=0) return;
        ChartPacketDataModel cpdm;
        if(len<=0)
        {
            for(int i=0;i<_basicDataTitle.size();i++){
                cpdm = (ChartPacketDataModel)_dataModels.get((String)_basicDataTitle.elementAt(i));
                if(cpdm.getPacketTitle().equals("자료일자"))cpdm.setPacketFormat(dateFormat);
            }
            return;
        }
        //2013. 10. 23 데이터 없이 흰화면일 때 실시간 받으면 봉 그리기 처리<<

        //real_cnt =0;
        basic_data = null;
        this.cre = null;
        this.allVol = null;
        this.preAllVol = null;

//        ChartPacketDataModel cpdm; 	//2013. 10. 23 데이터 없이 흰화면일 때 실시간 받으면 봉 그리기 처리
        for(int i=0;i<_basicDataTitle.size();i++){
            cpdm = (ChartPacketDataModel)_dataModels.get((String)_basicDataTitle.elementAt(i));
            if(cpdm.getPacketTitle().equals("자료일자"))cpdm.setPacketFormat(dateFormat);
            cpdm.initData(len);
        }
    }
    
    //2016.03.29 by LYH >> 비교차트 데이터 개수 줄어드는 현상 수정.
    public synchronized void initCompareData(int len){
        //2013. 10. 23 데이터 없이 흰화면일 때 실시간 받으면 봉 그리기 처리>>
//    	if(len<=0) return;
        ChartPacketDataModel cpdm;
        if(len<=0)
        {
            for(int i=0;i<_basicDataTitle.size();i++){
                cpdm = (ChartPacketDataModel)_dataModels.get((String)_basicDataTitle.elementAt(i));
                if(cpdm.getPacketTitle().equals("자료일자"))cpdm.setPacketFormat(dateFormat);
            }
            return;
        }
        //2013. 10. 23 데이터 없이 흰화면일 때 실시간 받으면 봉 그리기 처리<<

        //real_cnt =0;
        basic_data = null;
        this.cre = null;
        this.allVol = null;
        this.preAllVol = null;

//        ChartPacketDataModel cpdm; 	//2013. 10. 23 데이터 없이 흰화면일 때 실시간 받으면 봉 그리기 처리
        for(int i=0;i<_basicDataTitle.size();i++){
            cpdm = (ChartPacketDataModel)_dataModels.get((String)_basicDataTitle.elementAt(i));
            if(cpdm.getPacketTitle().equals("자료일자"))
            {
            	if(len < cpdm.getDataCount())
            		continue;
            	
            	cpdm.setPacketFormat(dateFormat);
            }
            cpdm.initData(len);
        }
    }
    //2016.03.29 by LYH << 비교차트 데이터 개수 줄어드는 현상 수정. 
    
    public synchronized void initAppendData(int len){
        //basic_data = null;
        ChartPacketDataModel cpdm;
        for(int i=0;i<_basicDataTitle.size();i++){
            cpdm = (ChartPacketDataModel)_dataModels.get((String)_basicDataTitle.elementAt(i));
            //if(cpdm.getPacketTitle().equals("자료일자"))cpdm.setPacketFormat(dateFormat);
            cpdm.initAppendData(len);
        }
    }
    public synchronized void setData_data(String sTitle, String[] sData){
        //System.out.println("start:"+System.currentTimeMillis());
        COMUtil.isChangeDataState=true;//데이터가 변경됨을 알림.
        COMUtil.isLastDataChangeState=false;//마지막 데이터가 변경됨 초기화.
        COMUtil.isRealTicState = true; //실시간 분, 틱인 경우 설정
        ChartPacketDataModel cpdm;
//        int dataLen;
//        int totalDataLen = data.length;
//        int basicLen = _basicDataTitle.size();
//        String strData = new String(data, 0, totalDataLen);

        //2011.07.22 by LYH >> cpdm별로 데이터 넣도록 변경. 
        //int cursor = 0;
//        for(int i=0;i<basicLen;i++){
        cpdm = (ChartPacketDataModel)_dataModels.get(sTitle);
        if(cpdm != null)
        {
            //dataLen = cpdm.getPacketLength();

            for(int j=0;j<sData.length;j++){
                //cpdm.addData(new String(data, j+cursor, dataLen).trim());
                if(sData != null && sData[j] != null)
                {
                    if(sTitle.equals("자료일자") && sData[j].length()>8)
                    {
                        cpdm.addData(sData[j].substring(0, 8).trim());
                    }
                    else
                        cpdm.addData(sData[j]);
                }
            }
        }
//        	cursor += dataLen;
//            if(cpdm.getAttr())cursor += 1;
        //}
    }
    public synchronized void setData_data(String sTitle, double[] dData){
        //System.out.println("start:"+System.currentTimeMillis());
        COMUtil.isChangeDataState=true;//데이터가 변경됨을 알림.
        COMUtil.isLastDataChangeState=false;//마지막 데이터가 변경됨 초기화.
        COMUtil.isRealTicState = true; //실시간 분, 틱인 경우 설정
        ChartPacketDataModel cpdm;
//        int dataLen;
//        int totalDataLen = data.length;
//        int basicLen = _basicDataTitle.size();
//        String strData = new String(data, 0, totalDataLen);

        //2011.07.22 by LYH >> cpdm별로 데이터 넣도록 변경. 
        //int cursor = 0;
//        for(int i=0;i<basicLen;i++){
        cpdm = (ChartPacketDataModel)_dataModels.get(sTitle);
        if(cpdm != null)
        {
            //dataLen = cpdm.getPacketLength();

            for(int j=0;j<dData.length;j++){
                //cpdm.addData(new String(data, j+cursor, dataLen).trim());
                if(dData != null)
                {
                    cpdm.addData(dData[j]);
                }
            }
        }
//        	cursor += dataLen;
//            if(cpdm.getAttr())cursor += 1;
        //}
    }
    public synchronized void setData(int len, byte[] data){
        //System.out.println("start:"+System.currentTimeMillis());
        COMUtil.isChangeDataState=true;//데이터가 변경됨을 알림.
        COMUtil.isLastDataChangeState=false;//마지막 데이터가 변경됨 초기화.
        COMUtil.isRealTicState = true; //실시간 분, 틱인 경우 설정
        ChartPacketDataModel cpdm;
        int dataLen;
        int totalDataLen = data.length;
        int basicLen = _basicDataTitle.size();
        String strData = new String(data, 0, totalDataLen);

        //2011.07.22 by LYH >> cpdm별로 데이터 넣도록 변경. 
        int cursor = 0;
        for(int i=0;i<basicLen;i++){
            cpdm = (ChartPacketDataModel)_dataModels.get((String)_basicDataTitle.elementAt(i));
            dataLen = cpdm.getPacketLength();

            for(int j=0;j<totalDataLen;j+=PACKET_LENGTH){
                //cpdm.addData(new String(data, j+cursor, dataLen).trim());
                cpdm.addData((new String(strData.substring(j+cursor, j+cursor+dataLen))).trim());
            }
            cursor += dataLen;
            if(cpdm.getAttr())cursor += 1;
        }

        //System.gc();
        //Log.i("mem", "child_remove:"+(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory()));
//        for(int i=0;i<totalDataLen;i+=PACKET_LENGTH){
//            int cursor = 0;
//            for(int j=0;j<basicLen;j++){
//                cpdm = (ChartPacketDataModel)_dataModels.get((String)_basicDataTitle.elementAt(j));
//                dataLen = cpdm.getPacketLength();
//                cpdm.addData(new String(data, i+cursor, dataLen).trim());
//                cursor += dataLen;
//                if(cpdm.getAttr())cursor += 1; 
//                
//            }
//            //System.out.println(i+"/"+totalDataLen);
//        }
        //2011.07.22 by LYH <<
        // System.out.println("end:"+System.currentTimeMillis());

//        //시/고/저/종 체크하여 다시 만든다. 20030519 ykLee add.
//        String[] temp = {"시가","고가","저가","종가","기본거래량"};
//        int[][] tempi = new int[5][];
//        for(int j=0;j<temp.length;j++){
//            cpdm = (ChartPacketDataModel)_dataModels.get(temp[j]);
//            tempi[j] = new int[cpdm.getDataCount()];
//            tempi[j] = cpdm.getDatas();
//        }
//        
//        int volData = 0;
//        
//        for(int i=0; i<len; i++) {
//            if(i!=0 && tempi[0][i]==0 && tempi[1][i]==0 && tempi[2][i]==0 || i!=0 && tempi[0][i]==tempi[1][i]&&tempi[1][i]==tempi[2][i] || i!=0 && tempi[0][i]==0 || i!=0 && tempi[1][i]==0 && i!=0 && tempi[2][i]==0) {
//                if(tempi[3][i]==tempi[3][i-1]) {
//                    tempi[0][i]=tempi[3][i-1];
//                    tempi[1][i]=tempi[3][i-1];
//                    tempi[2][i]=tempi[3][i-1];
//                }
//                if(tempi[3][i]>tempi[3][i-1]) {
//                    tempi[0][i]=tempi[3][i-1];
//                    tempi[1][i]=tempi[3][i];
//                    tempi[2][i]=tempi[3][i-1];
//                }
//                if(tempi[3][i]<tempi[3][i-1]) {
//                    tempi[0][i]=tempi[3][i-1];
//                    tempi[1][i]=tempi[3][i-1];
//                    tempi[2][i]=tempi[3][i];
//                }
//            }
//            if(tempi[0][i]==0) tempi[0][i] = tempi[3][i]; //시가가 0일경우 종가로 처리.
//            if(tempi[1][i]==0) tempi[1][i] = tempi[3][i]; //고가가 0일경우 종가로 처리.
//            if(tempi[2][i]==0) tempi[2][i] = tempi[3][i]; //저가가 0일경우 종가로 처리.
//            if(tempi[3][i]==0) tempi[3][i] = tempi[0][i]; //종가가 0일경우 시가로 처리.
//            //봉차트에서 저가가 고가보다 50%이상 작을 경우 저가를 시가로 처리.(DB데이터 오류 정정)
//            Double d1 = Double.valueOf(""+tempi[1][i]); //고
//            Double d2 = Double.valueOf(""+tempi[2][i]); //저
//            double dd1 = d1.doubleValue();
//            double dd2 = d2.doubleValue();
//            double per = ((dd1-dd2)/dd1)*100;
//            if(per>50) tempi[2][i]=tempi[0][i];
//            //거래량 값이 있는지 체크하여 합계가 0이면 거래량 block를 삭제한다.
//            volData += Math.abs(tempi[4][i]);
//        }
//        if(volData==0) _chart.removeBlock("거래량");//거래량삭제
//        else _chart.addBlock("거래량");//거래량추가
//
//        for(int j=0;j<temp.length-1;j++){
//            cpdm = (ChartPacketDataModel)_dataModels.get(temp[j]);
//            cpdm.setDatas(tempi[j]);
//        }
    }
    //    public void setNeoChart(NeoChart2 _chart) {
//        this._chart = _chart;
//    }
    public void resetCnt(){
        ChartPacketDataModel cpdm;
        for(int j=0;j<_basicDataTitle.size();j++){
            cpdm = (ChartPacketDataModel)_dataModels.get((String)_basicDataTitle.elementAt(j));
            cpdm.resetCnt();
        }
    }
    public boolean hasPivotData(){
        return has_pivot_data;
    }
    //==========================================
    // Graph의 계산공식에 의해 가공된 데이터의 그래프 이름을 key로 저장한다
    //==========================================
    public synchronized void setSubPacketData(String key,Object data){
        ChartPacketDataModel cpdm;
        if(_dataModels.containsKey(key)){
            cpdm = (ChartPacketDataModel)_dataModels.get(key);
        }else{
            cpdm = new ChartPacketDataModel();
            cpdm.setPacketTitle(key);
        }
//        if(data instanceof double[]){
//            double[] dData = (double[])data;
//            double[] tData=new double[dData.length];
//            int tLen = tData.length;
//            for(int i=0; i<tLen; i++){
//                tData[i] = (int)(dData[i]*100);
//            }
//            cpdm.setDatas(tData);
//        }else
        if(key.equals("자료일자") || key.equals("variable_자료일자") || key.equals("역시계곡선_거래량스트링")){ //contains("자료일자")로 사용하면 안됨. 다른 자료일자 데이터에 덮어씌움
            cpdm.intType = false;
            cpdm.setDatas((String[])data);
        }else{
//            String[] priceTitles = {"가격","Bollinger Bands","Envelope", "Pivot", "Parabolic","P&F", "삼선전환도", "대기매물","역시계곡선","일목균형도","지그재그차트" };
//            for(int i=0; i<priceTitles.length; i++){
//                if(key.indexOf(priceTitles[i])!=-1) {
//                    cpdm.setPacketFormat(this.priceFormat);
//                    break;
//                }
//            }

            cpdm.setDatas((double[])data);
        }
        _dataModels.put(key,cpdm);

        if(_dataTitles.contains(key))  return;

        _dataTitles.addElement(key);
    }

    //2015. 1. 30 가상매매연습기용 차트 봉 계속 추가하는 기능>>
    public synchronized void addTradePacketData(String strTradeDataPacket)
    {
        String[] packetInfos = {"자료일자", "시가", "고가", "저가", "종가", "기본거래량", "거래대금"};
        String[] tradeDataPacketToken = strTradeDataPacket.split("\\^");

        for(int i = 0; i < tradeDataPacketToken.length; i++)
        {
            addTradeSubPacketData(packetInfos[i], tradeDataPacketToken[i].trim());
        }

        DataChangeEvent evt = new DataChangeEvent(this, this, DataChangeEvent.INSERTDATA_CHANGE);
        processDataChangeEvent(evt);
    }
    public synchronized void addTradeSubPacketData(String key, String strData)
    {
        ChartPacketDataModel cpdm = (ChartPacketDataModel)_dataModels.get(key);
        if(cpdm==null)return;

        if(0 == cpdm.getDataCount())
        {
            cpdm.setData(strData);
        }
        else
        {
            cpdm.addRealData(strData);
        }
    }
    //2015. 1. 30 가상매매연습기용 차트 봉 계속 추가하는 기능<<

    public synchronized void addSubPacketData(String key, double ndata){
        ChartPacketDataModel cpdm = (ChartPacketDataModel)_dataModels.get(key);
        if(cpdm==null)return;
        if(dataType==DATA_DAY){//
            cpdm.setData(""+ndata);
        }else{
            cpdm.addRealData(""+ndata);
        }
    }

    public synchronized double[] getSubPacketData(String key,int index, int num, int mar_index){
        ChartPacketDataModel cpdm = (ChartPacketDataModel)_dataModels.get(key);
        double[] data =null;
        if(cpdm==null)return data;
        data = cpdm.getDatas();
        if(num>data.length)num=data.length;
        double[] dest = new double[num];
        if(index<0)index=0;
        try{
            if(data!=null)System.arraycopy(data,index,dest,0,num);
        }catch(ArrayIndexOutOfBoundsException e){
            int cinx = 0;
            if(data.length>dest.length) {
                cinx = dest.length;
            } else {
                cinx = data.length;
            }
            System.arraycopy(data,0,dest,0,cinx);
        }
        return dest;
    }
    public synchronized double[] getSubPacketData(String key,int index, int num){
        if(_dataModels==null) return null;
        ChartPacketDataModel cpdm = (ChartPacketDataModel)_dataModels.get(key);
        double[] data =null;
        if(cpdm==null)return data;
        data = cpdm.getDatas();
        if(data==null) return null;
        if(num>data.length)num=data.length;
        double[] dest = new double[num];
        try{
            if((index+num)>data.length) {
                num = data.length-index;
            }
            if(data!=null)System.arraycopy(data,index,dest,0,num);
        }catch(ArrayIndexOutOfBoundsException e){
        }
        return dest;
    }
    public synchronized double[] getSubPacketData(String key){
        if(_dataModels==null) return null;
        ChartPacketDataModel cpdm = (ChartPacketDataModel)_dataModels.get(key);
        if(cpdm==null) return null;
        double[] data = cpdm.getDatas();
        return data;
    }

    public synchronized void removePacket(String key){
        if(_basicDataTitle==null) return;
        if(!_basicDataTitle.contains(key)){
            ChartPacketDataModel cpdm = (ChartPacketDataModel)_dataModels.get(key);
            if(cpdm != null)
                cpdm.clear();
            _dataTitles.removeElement(key);
            _dataModels.remove(key);
        }
    }
    public synchronized void removeAllPacket(){
        if(_basicDataTitle==null) return;
        _basicDataTitle.removeAllElements();
        _dataModels.clear();
        _dataTitles.removeAllElements();
    }
    //==========================================
    // 해당 datakind에 맞는 packet에서 index로부터 num만큼의 데이터를 리턴한다
    //==========================================
    public synchronized double[] getData(String datakind, int index, int num){
        double[] tmp = null;
        if(_dataModels==null) return null;
        ChartPacketDataModel cpdm = (ChartPacketDataModel) _dataModels.get(datakind);
        //if(cpdm!=null)tmp=cpdm.getIntData(index,num);
        if(cpdm!=null)tmp=cpdm.getDatas(index,num);
        return tmp;
    }
    public synchronized String[] getStringData(String datakind, int index, int num){
        String[] tmp = null;
        ChartPacketDataModel cpdm = (ChartPacketDataModel) _dataModels.get(datakind);
        if(cpdm!=null)tmp=cpdm.getStringDatas(index,num);
        return tmp;
    }
    public synchronized String[] getStringData(String datakind){
        if(_dataModels==null) return null;
        String[] tmp = null;
        ChartPacketDataModel cpdm = (ChartPacketDataModel) _dataModels.get(datakind);
        if(cpdm!=null)tmp=cpdm.getStringDatas();
        return tmp;
    }

    public synchronized String getFirstStringData(String datakind){
        String tmp ="";
        if(_dataModels != null)
        {
            ChartPacketDataModel cpdm = (ChartPacketDataModel) _dataModels.get(datakind);
            if(cpdm!=null)tmp= cpdm.getFirstStringData();
        }
        return tmp;
    }

    public synchronized String getLastStringData(String datakind){
        String tmp ="";
        if(_dataModels != null)
        {
        	ChartPacketDataModel cpdm = (ChartPacketDataModel) _dataModels.get(datakind);
        	if(cpdm!=null)tmp= cpdm.getLastStringData();
        }
        return tmp;
    }
    public synchronized double getFirstData(String datakind){
        double tmp = Double.NaN;
        if(_dataModels != null)
        {
            ChartPacketDataModel cpdm = (ChartPacketDataModel) _dataModels.get(datakind);
            if(cpdm!=null)tmp= cpdm.getFirstData();
        }
        return tmp;
    }
    public synchronized double getLastData(String datakind){
        double tmp = Double.NaN;
        if(_dataModels != null)
        {
        	ChartPacketDataModel cpdm = (ChartPacketDataModel) _dataModels.get(datakind);
        	if(cpdm!=null)tmp= cpdm.getLastData();
        }
        return tmp;
    }
    /*public String getData(int index){
        String data="";
        if(_basicDataTitle!=null){
            ChartPacketDataModel cpdm = (ChartPacketDataModel) _dataModels.get(_basicDataTitle.elementAt(0));
            data= ""+cpdm.getData(index);
        }
        return data;
    }*/
    public String getData(String title, int index){
        if(_basicDataTitle!=null){
            ChartPacketDataModel cpdm = (ChartPacketDataModel) _dataModels.get(title);
            if(cpdm==null) return "";
            return cpdm.getData(index);
        }
        return "";
    }
    public String getFormatData(String title, int index){
        if(_basicDataTitle!=null){
            ChartPacketDataModel cpdm = (ChartPacketDataModel) _dataModels.get(title);
            if(cpdm==null) return "";
            return cpdm.getFormatData(index);
        }
        return "";
    }
    public String[] getDatas(int index){
        String[] data =null;
        int cnt = getCount();
        //if(index>=cnt)index=cnt-1;
        if(_basicDataTitle!=null&&cnt>0){
            data = new String[_basicDataTitle.size()];
            for(int i=0;i<_basicDataTitle.size();i++){
                String t = (String)_basicDataTitle.elementAt(i);
                ChartPacketDataModel cpdm = (ChartPacketDataModel) _dataModels.get(t);
                data[i]= ""+cpdm.getData(index);
            }
        }
        return data;
    }

    public String[] getFormatDatas(int index) {
        String[] data =null;
        int cnt = getCount();
        if(index>=cnt)index=cnt-1;
        if(_basicDataTitle!=null&&cnt>=1){
            data = new String[_basicDataTitle.size()];
            for(int i=0;i<_basicDataTitle.size();i++){
                String t = (String)_basicDataTitle.elementAt(i);
                ChartPacketDataModel cpdm = (ChartPacketDataModel) _dataModels.get(t);
                data[i]= ""+cpdm.getFormatData(index);
            }
        }
        return data;
    }
    public String[] getAllDatas(int index){
        String[] data =null;
        if(_dataTitles!=null){
            data = new String[_dataTitles.size()];
            for(int i=0;i<_dataTitles.size();i++){
                String t = (String)_dataTitles.elementAt(i);
                ChartPacketDataModel cdpm = (ChartPacketDataModel)_dataModels.get(t);
                data[i]=cdpm.getFormatData(index);
            }
        }
        return data;
    }

    //==========================================
    // 실시간 add간격을 셋
    //==========================================
    public void setRealAddTerm(int term){
        real_term = term;
        tickCount = term-1;
    }
    //2014.04.17 by LYH >> 멀티틱 조회 시점 몇 틱인지 알려줌.
    public void setTickCount(int count) {
        tickCount = count;
    }
    //2014.04.17 by LYH << 멀티틱 조회 시점 몇 틱인지 알려줌.
    //==========================================
    // 날짜타입을 분석한다.
    // 실시간 데이터 add시킬때 참조한다 
    //==========================================
    public void setDateType(int type){
        dataType = type;
    }
    public int getDateType(){
        return dataType;
    }
    //==========================================
    // 실시간 패킷정보를 리턴
    //==========================================
    public Vector<ChartRealPacketDataModel> getRealPacketInfo(){
        return realInfo;
    }
    public void setDateFormat(int type){
        dateFormat = type;
    }
    public int getDateFormat(){
        return dateFormat;
    }
    public int getPriceFormat(){
        //2012.11.27 by LYH >> 진법 및 승수 처리.
        if(nTradeMulti>=0)
        {
            if(this.nDispScale != 10)
                return priceFormat*1000+nDispScale;
            return priceFormat*1000+nTradeMulti;
        }
        //2012.11.27 by LYH <<
        return priceFormat;
    }
    //==========================================
    // repaintRT를 통해 온 실시간 데이터를 처리
    // 보조메세지에서 얻은 날짜타입을 바탕으로 insert할것인지 add할것인지, change할것인지 분류
    //==========================================
    Vector<String> realEle= new Vector<String>();
    public synchronized void addData(String[] s, int isnujuk,int packetTitle){
        //System.out.println("realData--> " + s + " isnujuk-->"+isnujuk + "packetTitle-->"+packetTitle);

        if(packetTitle>_useRealTitle.size()-1)return;
        if(cre==null)cre =new String[REAL_DATA_SET];
        switch(this.addType){
            case DATA_DAY://일
            case DATA_WEEK://주
            case DATA_MONTH://월
            case DATA_YEAR:
            case DATA_SECOND:
            case DATA_MIN://분
            case DATA_TIC:
                //ChartPacketDataModel cpdm = getChartPacket((String)_useRealTitle.elementAt(packetTitle));
//                cre[packetTitle] = s;
                cre = s;
                break;
        }
        //real_cnt = 0;
//        real_cnt++;
//        if(real_cnt==this.REAL_DATA_SET){
        //real_cnt=0;
        realSetReady();
//        }
    }
    int tickCount = 0;
    public synchronized boolean realTimeCheck(String time){
        if(addType==DATA_DAY)return true;
        ChartPacketDataModel cpdm = null;	//2013. 10. 23 데이터 없이 흰화면일 때 실시간 받으면 봉 그리기 처리
//        if(real_term==1)return true;
        if(addType==DATA_WEEK || addType==DATA_MONTH || addType==DATA_YEAR)
            return false;
        if(addType==DATA_TIC){
            //2013. 10. 23 데이터 없이 흰화면일 때 실시간 받으면 봉 그리기 처리>>
            cpdm = getChartPacket((String)_useRealTitle.elementAt(0));
            if(cpdm.getPacketTitle().equals("자료일자")){
                if(cpdm.getDataCount()<=0)
                {
                    cpdm.setData(time);
                }
            }
            //2013. 10. 23 데이터 없이 흰화면일 때 실시간 받으면 봉 그리기 처리<<

            tickCount++;
            if(tickCount>=real_term){
                tickCount = 0;
                return true;
            }else return false;
        }else{
            cpdm = getChartPacket((String)_useRealTitle.elementAt(0));	//2013. 10. 23 데이터 없이 흰화면일 때 실시간 받으면 봉 그리기 처리
            if(cpdm.getPacketTitle().equals("자료일자")){
                //2013. 10. 23 데이터 없이 흰화면일 때 실시간 받으면 봉 그리기 처리>>
//              String last = cpdm.changeFDatebyFormat(cpdm.getLastStringData());
                String last = null;
                if(cpdm.getLastStringData()==null || cpdm.getLastStringData().length()<=0)
                {
                    String strTime = null;
                    strTime = time.trim();

                    if(strTime.length() < 6)	// 10시 이전 시간일 경우.  093012 처럼 들어와야하는데 0이 맨앞에 없어서 넣어준다.
                    {
                        strTime = "0%@" + time;
                    }

                    //데이터가 없을 때 인자로 들어온 real time (time)  을 cpdm 에 세팅해줌. 현재 시간 다음꺼 봉이 만들어져야하므로 +1
                    last = cpdm.changeFDatebyFormat(strTime).substring(0, 4);
                    int nTime=Integer.parseInt(last);
                    if(nTime%100>=60)
                        nTime = nTime+100-60;
                    last = String.valueOf(nTime);
                    if(last.length() < 4)	// 10시 이전 시간일 경우.  0930 처럼 들어오는데 위 정수변환부에서 앞에 0을 짤라버리기때문에 0을 다시 넣어준다.
                    {
                        last = "0" + last;
                    }
                    cpdm.setData(last);
                }
                else
                {
                    last = cpdm.changeFDatebyFormat(cpdm.getLastStringData());
                }
                //2013. 10. 23 데이터 없이 흰화면일 때 실시간 받으면 봉 그리기 처리<<

                if(last!=null){
                    int lasttime;
                    int currtime;
                    lasttime = Integer.parseInt(last);
                    if(time.length()>6){
                        currtime=Integer.parseInt(new String(time.substring(0,6)));
                    }else{
                        //2012.07.17 by LYH>>분차트 시간 올림기능 추가.
                        if (COMUtil.roundType.equals("ceil")) {         //올림.
                            if (addType==DATA_MIN)
                            {
                                //2019.07.22 by LYH >> 분차트 00시로 넘어갈 때 오류 처리 Start
                                //currtime=Integer.parseInt(new String(time.substring(0,4)))+real_term;
                                if(real_term>=60)
                                {
                                    currtime = Integer.parseInt(new String(time.substring(0,4)))+(100*real_term/60)+real_term%60;
                                }
                                else
                                {
                                    currtime = Integer.parseInt(new String(time.substring(0,4)))+real_term;
                                }
                                //2019.07.22 by LYH >> 분차트 00시로 넘어갈 때 오류 처리 End
                                if(currtime%100>=60)
                                    currtime = currtime+100-60;
                                //System.out.println("curtime: " + currtime + " time: "+ time);
                            }
                            else
                            {
                                //2019.07.22 by LYH >> 분차트 00시로 넘어갈 때 오류 처리 Start
                                //currtime = Integer.parseInt(new String(time.substring(0,6)))+real_term;
                                if(real_term>=60)
                                {
                                    currtime = Integer.parseInt(new String(time.substring(0,6)))+(100*real_term/60)+real_term%60;
                                }
                                else
                                {
                                    currtime = Integer.parseInt(new String(time.substring(0,6)))+real_term;
                                }
                                //2019.07.22 by LYH >> 분차트 00시로 넘어갈 때 오류 처리 End
                                if(currtime%100>=60)
                                    currtime = currtime+100-60;
                                if(currtime%10000>=6000)
                                    currtime = currtime+10000-6000;
                            }
                            //2019.07.22 by LYH >> 분차트 00시로 넘어갈 때 오류 처리 Start
                            if(currtime>=2400)
                                currtime -= 2400;
                            //2019.07.22 by LYH >> 분차트 00시로 넘어갈 때 오류 처리 End
                        } else {
                            currtime=Integer.parseInt(new String(time.substring(0,4)));
                        }
                    }
                    //2012.07.17 by LYH<<
                    //currtime-=real_term;

                    //2014.10.22 by lyk - 60분봉등 24시간 처리
                    m_bReverseTime = false;

                    //2019.07.22 by LYH >> 분차트 00시로 넘어갈 때 오류 처리 Start
                    int nCurMin = currtime/100 * 60 + currtime%100;
                    int nLastMin = lasttime/100 * 60 + lasttime%100;

                    if(nLastMin+real_term >= 1440 && nCurMin < nLastMin) {
                        nCurMin += 1440;
                    }
                    //2019.07.22 by LYH >> 분차트 00시로 넘어갈 때 오류 처리 End

                    //if((currtime-lasttime)>=real_term){
                    if ((nCurMin-nLastMin) >= real_term) {  //2019.07.22 by LYH >> 분차트 00시로 넘어갈 때 오류 처리
                        //System.out.println("lasttime: " + lasttime + " real_term: "+ real_term + " time:" +time);
                        int nTime= 0;
                        if (COMUtil.roundType.equals("ceil")) {         //올림.
                            try
                            {
                                if(Integer.parseInt(time) != lasttime*100)	//ceil타입 정각일 때는 추가하지 않는다. 00초일
                                    return true;
                                else
                                    return false;
                            }
                            catch(Exception e)
                            {
                            }
                        }
                        else if (COMUtil.roundType.equals("ceil2")) {         //올림.
                            return true;
                        }
                        return true;
                    }
                    //else if(Math.abs(currtime-lasttime)>=real_term)
                    else if(Math.abs(nCurMin-nLastMin)>=real_term)  //2019.07.22 by LYH >> 분차트 00시로 넘어갈 때 오류 처리
                    {
                        m_bReverseTime = true;
                        return true;
                    }

                    //2014.10.22 by lyk - 60분봉등 24시간 처리 end

//                    if(Math.abs(currtime-lasttime)>=real_term){
//                    	System.out.println("lasttime: " + lasttime + " real_term: "+ real_term);
//                        return true;
//                    }
                }
            }
        }
        return false;
    }
    private synchronized String calcVolume(String svol){
        String calcedvol;
        int vol = Integer.parseInt(svol.trim());
        int lastvol = Integer.parseInt(allVol.trim());
        calcedvol =Integer.toString(vol-lastvol);
        return calcedvol;
    }
    //==========================================
    // NeoChart의 repaintRT를 통해 온 실시간 데이터를 처리
    // 보조메세지에서 얻은 날짜타입을 바탕으로 insert할것인지 add할것인지, change할것인지 분류
    //==========================================
    public synchronized void addData(String packetTitle, String value){
        ChartPacketDataModel cpdm = getChartPacket(packetTitle);
        if(cpdm==null) return;
        switch(dataType){
            case DATA_DAY:  //일
            case DATA_WEEK:  //주
            case DATA_MONTH: //월
            case DATA_YEAR:  //년
                if(packetTitle.equals("자료일자")) return;
                cpdm.setData(value);
                if(packetTitle.equals("종가"))makeBongData(value);
                break;
            case DATA_MIN:  //분
            case DATA_SECOND:  //분
                if(packetTitle.equals("자료일자")) return;
                if(packetTitle.equals("기본거래량")||packetTitle.equals("거래대금")||packetTitle.equals("매수거래량")||packetTitle.equals("매도거래량"))
                    cpdm.setData(""+Double.parseDouble(value)+cpdm.getLastData());
                else
                    cpdm.setData(value);

                if(packetTitle.equals("종가"))makeBongData(value);
                //    makeMinData(packetTitle, value);
                break;
            case DATA_TIC:
                cpdm.addRealData(value);
                break;
        }
    }
    private String makeTime(String time){
        if(time==null) return "";
        //2014. 1. 14 실시간 할 때 시간값 없을때의 처리>>
        if(time.trim().length() < 6)
        {
            return "";
        }
        //2014. 1. 14 실시간 할 때 시간값 없을때의 처리<<
        if(addType==DATA_TIC)
        {
            return time;
        }
        else if(addType == DATA_SECOND)
        {
            ChartPacketDataModel cpdm = getChartPacket("자료일자");
            int currtime=0;
            if (cpdm != null) {
                String rtnStr = cpdm.getLastStringData();
                if (rtnStr!=null) {
                    String last = cpdm.changeFDatebyFormat(rtnStr);
                    if(Integer.parseInt(last)<480000)
                    {
                        currtime =Integer.parseInt(last)+real_term;
                        if(currtime%100>=60)
                            currtime = currtime+100-60;
                        if(currtime%10000>=6000)
                            currtime = currtime+10000-6000;
                    }
                }
            }
            else
            {
                int t=Integer.parseInt(time);
                currtime = t+real_term-(t%real_term);
                if(currtime%100>=60)
                    currtime = currtime+100-60;
                if(currtime%10000>=6000)
                    currtime = currtime+10000-6000;
            }
            time = String.format("%06d", currtime);
            return time;
        }

        int t;
        String rtnValue;
        if(time.startsWith("0")){
//            if(time.length()>6){
//                t = Integer.parseInt(new String(time.substring(1,6)));
//            }else{
//                t = Integer.parseInt(time);
//                //return "0"+(t-(t%real_term));
//            }
//        	rtnValue = "0"+(t-(t%real_term));
//        	
            String tail =new String(time.substring(4,6));
            String strHHMM =new String(time.substring(0,4));
            int currtime;
            //round 분데이터 처리 방식 적용.
            if (COMUtil.roundType.equals("ceil") && addType == DATA_MIN) {         //올림.         //올림.
                //currtime = (int)ceil([time doubleValue]/100);
                currtime = Integer.parseInt(strHHMM)+real_term;
                ChartPacketDataModel cpdm = getChartPacket("자료일자");
                if (cpdm != null) {
                    String rtnStr = cpdm.getLastStringData();
                    if (rtnStr!=null) {
                        String last = cpdm.changeFDatebyFormat(rtnStr);
//                        System.out.println("#### last: " + last + " real_term: "+ real_term);

                        if(Integer.parseInt(last)<4800)
                        {
                            //2019.07.22 by LYH >> 분차트 00시로 넘어갈 때 오류 처리 Start
//                            currtime =Integer.parseInt(last)+real_term;
//                          	//2014.10.06 by LYH >> 60분봉등 24시간 처리.
//                            if(currtime%100>=60)
//                                currtime = currtime+100-60;
//                          	//2014.10.06 by LYH << 60분봉등 24시간 처리.
//                          //2012. 11. 21 분 리얼데이터 수신시의 시간계산 로직 개선   >>  : C30
//                            int nNow = Integer.parseInt(strHHMM);
//                          	//2014.10.06 by LYH >> 60분봉등 24시간 처리.
//                            if(m_bReverseTime)
//                            	nNow += 2400;
//                          	//2014.10.06 by LYH << 60분봉등 24시간 처리.
//                            while(currtime <= nNow)
//                            {
//                            	currtime+=real_term;
//                            	if(currtime%100>=60)
//                                    currtime = currtime+100-60;
//                            }
//                            //>>
//                          	//2014.10.06 by LYH >> 60분봉등 24시간 처리.
//                            if(m_bReverseTime)
//                            	currtime -= 2400;
//                          	//2014.10.06 by LYH << 60분봉등 24시간 처리.
                            int nMin = Integer.parseInt(last) / 100 * 60 + Integer.parseInt(last) % 100 + real_term;
                            currtime = nMin / 60 * 100 + nMin % 60;
                            int nNow = Integer.parseInt(strHHMM);
                            if(m_bReverseTime)
                                nNow += 2400;

                            int nNowMin = nNow / 100 * 60 + nNow % 100;
                            while (nMin <= nNowMin) {
                                nMin = currtime / 100 * 60 + currtime % 100 + real_term;
                                currtime = nMin / 60 * 100 + nMin % 60;
                            }
                            if (m_bReverseTime)
                                currtime -= 2400;
                            //2019.07.22 by LYH >> 분차트 00시로 넘어갈 때 오류 처리 End
                        }

//                        if(Integer.parseInt(last)<4800)
//                            currtime =Integer.parseInt(last)+real_term;
//                        if(currtime%100>=60)
//                            currtime = currtime+100-60;
                    }
                }
            } else {
                currtime = Integer.parseInt(strHHMM);
            }

            //2019.07.22 by LYH >> 분차트 00시로 넘어갈 때 오류 처리 Start
            if(currtime>=2400)
                currtime -= 2400;
            //2019.07.22 by LYH >> 분차트 00시로 넘어갈 때 오류 처리 End
            String roundStr = ""+ currtime + tail;

            //2018.11.28 by hyh - 유진은 24:01 형식으로 야간 데이터 제공되므로, 내용 삭제 >>
//        	//2014.10.16 by LYH >> 24:00 ->00:00으로 변경.
//            if(currtime>=2400)
//            	roundStr = ""+ (currtime - 2400) + tail;
//        	//2014.10.16 by LYH << 24:00 ->00:00으로 변경.
            //2018.11.28 by hyh - 유진은 24:01 형식으로 야간 데이터 제공되므로, 내용 삭제 <<

            t = Integer.parseInt(roundStr);

            //2014.10.06 by LYH >> 60분봉등 24시간 처리.
            rtnValue = String.format("%06d", t);

//            rtnValue = "0"+(t-(t%real_term));
        }else{
            //2012.07.17 by LYH>>분차트 시간 올림기능 추가.
            String tail =new String(time.substring(4,6));
            String strHHMM =new String(time.substring(0,4));
            int currtime;
            //round 분데이터 처리 방식 적용.
            if (COMUtil.roundType.equals("ceil") && addType == DATA_MIN) {         //올림.         //올림.
                //currtime = (int)ceil([time doubleValue]/100);
                currtime = Integer.parseInt(strHHMM)+real_term;
                ChartPacketDataModel cpdm = getChartPacket("자료일자");
                if (cpdm != null) {
                    String rtnStr = cpdm.getLastStringData();
                    if (rtnStr!=null) {
                        String last = cpdm.changeFDatebyFormat(rtnStr);

                        if(Integer.parseInt(last)<4800)
                        {
                            //2019.07.22 by LYH >> 분차트 00시로 넘어갈 때 오류 처리 Start
//                            currtime =Integer.parseInt(last)+real_term;
//                          	//2014.10.06 by LYH >> 60분봉등 24시간 처리.
//                            if(currtime%100>=60)
//                                currtime = currtime+100-60;
//                            int nNow = Integer.parseInt(strHHMM);
//                            if(m_bReverseTime)
//                            	nNow += 2400;
//
//                            while(currtime <= nNow)
//                            {
//                            	currtime+=real_term;
//                            	if(currtime%100>=60)
//                                    currtime = currtime+100-60;
//                            }
//
//                            if(m_bReverseTime)
//                            	currtime -= 2400;
//                          	//2014.10.06 by LYH << 60분봉등 24시간 처리.
                            int nMin = Integer.parseInt(last) / 100 * 60 + Integer.parseInt(last) % 100 + real_term;
                            currtime = nMin / 60 * 100 + nMin % 60;
                            int nNow = Integer.parseInt(strHHMM);
                            if(m_bReverseTime)
                                nNow += 2400;

                            int nNowMin = nNow / 100 * 60 + nNow % 100;
                            while (nMin <= nNowMin) {
                                nMin = currtime / 100 * 60 + currtime % 100 + real_term;
                                currtime = nMin / 60 * 100 + nMin % 60;
                            }
                            if (m_bReverseTime)
                                currtime -= 2400;
                            //2019.07.22 by LYH >> 분차트 00시로 넘어갈 때 오류 처리 End
                        }

//                        System.out.println("last: " + last + " real_term: "+ real_term);
//                        if(Integer.parseInt(last)<4800)
//                            currtime =Integer.parseInt(last)+real_term;
//                        if(currtime%100>=60)
//                            currtime = currtime+100-60;
                    }
                }
            } else {
                currtime = Integer.parseInt(strHHMM);
            }

            //2019.07.22 by LYH >> 분차트 00시로 넘어갈 때 오류 처리 Start
            if(currtime>=2400)
                currtime -= 2400;
            //2019.07.22 by LYH >> 분차트 00시로 넘어갈 때 오류 처리 End
            String roundStr = ""+ currtime + tail;

//        	//2014.10.16 by LYH >> 24:00 ->00:00으로 변경.
            if(currtime>=2400)
            	roundStr = ""+ (currtime - 2400) + tail;
//        	//2014.10.16 by LYH << 24:00 ->00:00으로 변경.

            t = Integer.parseInt(roundStr);

            //2014.10.16 by LYH >> 24:00 ->00:00으로 변경.
            rtnValue = String.format("%06d", t);

//            rtnValue = ""+(t-(t%real_term));
//            if(time.length()>6){
//                t = Integer.parseInt(new String(time.substring(0,6)));
//            }else{
//                t = Integer.parseInt(time);
//                //return ""+(t-(t%real_term));
//            }
//            rtnValue = ""+(t-(t%real_term));
            //2012.07.17 by LYH<<
        }

        if(addType == DATA_MIN && rtnValue.length()>4)
            rtnValue = new String(rtnValue.substring(0,4));
        return rtnValue;
    }
    int m_nRealIndex = -1;	//2015.01.08 by LYH >> 3일차트 추가
    private synchronized void addRealDataByType(ChartPacketDataModel cpdm, String data){
        //2015.01.08 by LYH >> 3일차트 추가
        if(m_bRealUpdate)
        {
            if(cpdm.getPacketTitle().equals("자료일자")) {
                m_nRealIndex = cpdm.findIndex(data);
            }
            if(m_nRealIndex>=0)
                cpdm.setDataAtIndex(data, m_nRealIndex);
            else
            {
                COMUtil.isChangeDataState=true;//데이터가 변경됨을 알림.
                COMUtil.isLastDataChangeState=false;//마지막 데이터가 변경됨 초기화.
                COMUtil.isRealTicState = true; //실시간 분, 틱인 경우 설정

                cpdm.addRealData(data);
            }


            return;
        }
        //2015.01.08 by LYH << 3일차트 추가
        switch(addType){
            case 1://일
            case 2://주
            case 3://월
            case 8://년
                if(!cpdm.getPacketTitle().equals("자료일자")) {
                    COMUtil.isChangeDataState=false;//데이터가 변경됨을 알림.
                    COMUtil.isLastDataChangeState=true;//마지막 데이터가 변경됨 초기화.
                    COMUtil.isRealTicState = false; //실시간 분, 틱인 경우 설정

                    cpdm.setData(data);
                }
                break;
            case 4://분
            case 5://틱
            case 7://초
                COMUtil.isChangeDataState=true;//데이터가 변경됨을 알림.
                COMUtil.isLastDataChangeState=false;//마지막 데이터가 변경됨 초기화.
                COMUtil.isRealTicState = true; //실시간 분, 틱인 경우 설정

                cpdm.addRealData(data);
                break;
        }
    }
    public synchronized void realSetReady(){
        //if(addType==2||addType==3)return;
        String[] cre = (String[])this.cre.clone();
        for(int i=0;i<cre.length;i++){
            ChartPacketDataModel cpdm = getChartPacket((String)_useRealTitle.elementAt(i));
            try {
                if(cpdm.getPacketTitle().equals("자료일자")) {
                    time_check = realTimeCheck(cre[i]);
                }
            } catch(Exception e) {
//            	System.out.println("aa");
            }

            if(time_check){//&&last_cre!=null){
                if(cpdm.getPacketTitle().equals("자료일자")){
                    String strTime = makeTime(cre[i]);
                    //2013.03.25 by LYH >> 증권정보 팝 분차트 실시간 처리.
                    if(cre[0].startsWith("99"))
                    {
                        strTime = makeTime(cre[i].substring(2,6)+"00");

                        if(COMUtil.strPreBongData[0].length()>0 && cpdm.getLastStringData().equals(COMUtil.strPreBongData[0]))
                        {
                            String[] basic_data = {"시가","고가","저가","종가","기본거래량"};
                            for(int j=0;j<basic_data.length;j++){
                                ChartPacketDataModel tmp = getChartPacket(basic_data[j]);

                                if(!tmp.getPacketTitle().equals("자료일자")){
                                    tmp.setData(COMUtil.removeFrontZero(COMUtil.strPreBongData[j+1].trim()));
                                }
                            }
                        }
                    }
                    //2013.03.25 by LYH <<
                    if(addType == DATA_MIN || addType == DATA_TIC || addType==DATA_SECOND)
                        strTime = cpdm.getDate(cpdm.getLastStringData())+strTime;
                    addRealDataByType(cpdm,strTime);
                }else if(cpdm.getPacketTitle().equals("기본거래량")||cpdm.getPacketTitle().equals("누적거래량")||cpdm.getPacketTitle().equals("거래대금")
                        ||cpdm.getPacketTitle().equals("매수거래량")||cpdm.getPacketTitle().equals("매도거래량")){
                    if(cre[i].startsWith("-")){
                        if(cre[i].length()>5)cre[i]="1";
                        else cre[i]= new String(cre[i].substring(1));
                    }
                    if(allVol==null)  allVol = cre[i];
                    if(has_nujum_vol){//누적체결량이 오는 경우
                        if(preAllVol!=null) allVol = preAllVol;
                        addRealDataByType(cpdm,calcVolume(cre[i]).trim());
                        preAllVol = cre[i];
                    }else{//누적이 아닌경우      
                        addRealDataByType(cpdm,COMUtil.removeFrontZero(cre[i].trim()));
                    }
                    //allVol = cre[i];
                }
                //2023.03.15 by SJW - 애프터마켓 추가 >>
                else if(cpdm.getPacketTitle().equals("sessionIds")) {
                    addRealDataByType(cpdm,COMUtil.removeFrontZero(cre[i].trim()));
                }
                //2023.03.15 by SJW - 애프터마켓 추가 <<
                else if(this.addType==DATA_MIN && isEndPrice(cpdm.getPacketTitle())){//종가인 경우는 시고저종을 다 만든다
                    addRealDataByType(cpdm,COMUtil.removeFrontZero(cre[i].trim()));
                    if(this.addType==DATA_MIN){
                        basic_data=null;
                        makeBongData(cre[i]);
                        for(int j=0;j<basic.length;j++){
                            ChartPacketDataModel tmp = getChartPacket(basic[j]);
                            if(basic_data[j]!=null&&tmp!=null){
                                //2015. 2. 5 선옵 1분선 기초자산/미결제약정 실시간>>
                                if(bStandardLine && (tmp.getPacketTitle().equals("시가") || tmp.getPacketTitle().equals("고가")) && Float.parseFloat(cre[j+1])>0)
                                {
                                    //cre 는 날짜/시/고/저/종/거래량      basic은 시/고/저
//                            		ChartPacketDataModel cpdmStandardLine;
//                                    if(tmp.getPacketTitle().equals("시가"))
//                                    {
//                                        cpdmStandardLine = getChartPacket("기초자산");
//                                    }
//                                    else
//                                    {
//                                        cpdmStandardLine = getChartPacket("미결제약정");
//                                    }
//                                    addRealDataByType(cpdmStandardLine,COMUtil.removeFrontZero(cre[j+1].trim()));
                                    addRealDataByType(tmp,COMUtil.removeFrontZero(cre[j+1].trim()));
                                }
                                else
                                {
                                    addRealDataByType(tmp,COMUtil.removeFrontZero(basic_data[j].trim()));
                                }
                                //2015. 2. 5 선옵 1분선 기초자산/미결제약정 실시간<<
                            }
                        }
                    }
                }
                else if(this.addType == DATA_TIC || addType==DATA_SECOND){
                    addRealDataByType(cpdm,COMUtil.removeFrontZero(cre[4]).trim());
                    basic_data=null;
                }
                else if(this.addType != DATA_MIN){
                    double dOpen = 0.0f;
                    if(this.addType == DATA_DAY)
                    {
                        String strOpen = COMUtil.removeFrontZero(cre[1]).trim();

                        try{
                            dOpen = Double.parseDouble(strOpen);
                        }catch(Exception e)
                        {
                            dOpen = 0.0f;
                        }

                    }
                    if(dOpen == 0.0f)
                    {
                        if(isEndPrice(cpdm.getPacketTitle())){
                            if(basic_data==null  && cpdm.getDataCount()>0){	//2013. 12. 18 거래정지종목 차트 봉 잘못그리는 현상
                                basic_data = new String[4];

                                basic_data[0] = this.getData("시가", cpdm.getDataCount()-1);
                                basic_data[1] = this.getData("고가", cpdm.getDataCount()-1);
                                basic_data[2] = this.getData("저가", cpdm.getDataCount()-1);
                                basic_data[3] = this.getData("종가", cpdm.getDataCount()-1);
                            }
                            makeBongData(cre[i]);
                            cpdm.setData(COMUtil.removeFrontZero(cre[i].trim()));
                            for(int j=0;j<basic.length;j++){
                                ChartPacketDataModel tmp = getChartPacket(basic[j]);
                                if(basic_data[j]!=null&&tmp!=null){
                                    if(!tmp.getPacketTitle().equals("자료일자")){
                                        tmp.setData(COMUtil.removeFrontZero(basic_data[j].trim()));
                                    }
                                }
                            }
                        }
                    }
                    else
                        addRealDataByType(cpdm,COMUtil.removeFrontZero(cre[i]).trim());
                }

            }else{
                COMUtil.isChangeDataState=false;//데이터가 변경됨을 알림.
                COMUtil.isLastDataChangeState=true;//마지막 데이터가 변경됨 초기화.
                COMUtil.isRealTicState = false; //실시간 분, 틱인 경우 설정




                if (this.addType==DATA_MIN || this.addType==DATA_TIC || addType==DATA_SECOND || this.addType==DATA_WEEK || this.addType==DATA_MONTH || this.addType==DATA_YEAR) { //분, 틱
                    if(isEndPrice(cpdm.getPacketTitle())){
                        if(basic_data==null && cpdm.getDataCount()>0){	//2013. 10. 23 데이터 없이 흰화면일 때 실시간 받으면 봉 그리기 처리
                            basic_data = new String[4];

                            basic_data[0] = this.getData("시가", cpdm.getDataCount()-1);
                            basic_data[1] = this.getData("고가", cpdm.getDataCount()-1);
                            basic_data[2] = this.getData("저가", cpdm.getDataCount()-1);
                            basic_data[3] = this.getData("종가", cpdm.getDataCount()-1);
                        }
                        makeBongData(cre[i]);
                        cpdm.setData(COMUtil.removeFrontZero(cre[i].trim()));
                        for(int j=0;j<basic.length;j++){
                            ChartPacketDataModel tmp = getChartPacket(basic[j]);
                            if(basic_data[j]!=null&&tmp!=null){
                                if(!tmp.getPacketTitle().equals("자료일자")){
                                    //2015. 2. 5 선옵 1분선 기초자산/미결제약정 실시간>>
                                    if(bStandardLine && (tmp.getPacketTitle().equals("시가") || tmp.getPacketTitle().equals("고가")) && Float.parseFloat(cre[j+1])>0)
                                    {
                                        //cre 는 날짜/시/고/저/종/거래량      basic은 시/고/저
//                                		ChartPacketDataModel cpdmStandardLine;
//                                        if(tmp.getPacketTitle().equals("시가"))
//                                        {
//                                            cpdmStandardLine = getChartPacket("기초자산");
//                                        }
//                                        else
//                                        {
//                                            cpdmStandardLine = getChartPacket("미결제약정");
//                                        }
//                                        cpdmStandardLine.setData(COMUtil.removeFrontZero(cre[j+1].trim()));
                                        tmp.setData(COMUtil.removeFrontZero(cre[j+1].trim()));
                                    }
                                    else
                                    {
                                        tmp.setData(COMUtil.removeFrontZero(basic_data[j].trim()));
                                    }
                                    //2015. 2. 5 선옵 1분선 기초자산/미결제약정 실시간<<
                                }
                            }
                        }
                    }else if(cpdm.getPacketTitle().equals("기본거래량")||cpdm.getPacketTitle().equals("누적거래량")
                            ||cpdm.getPacketTitle().equals("매수거래량")||cpdm.getPacketTitle().equals("매도거래량")){
                        //||cpdm.getPacketTitle().equals("미결제약정") //2019. 12. 26 by hyh - 미결제약정 실시간 적용. 체결량 방식 제거

                        int moveVol=0;
                        if(cre[i].startsWith("-")){
                            try {
                                moveVol=Math.abs(Integer.parseInt(cre[i].trim()));
                            } catch (Exception e) {

                            }

//                            if(cre[i].length()>5)cre[i]="1";
//                            else cre[i]= cre[i].substring(1);
//                            
//                            if(allVol==null)
//                                allVol = cre[i];
//                                
//                            if(has_nujum_vol){//누적체결량이 오는 경우
////                                cpdm.setData(calcVolume(cre[i]).trim());
//                            	cpdm.setData(cpdm.getLastData()+Integer.parseInt(cre[i].trim()));
//                                preAllVol = cre[i];
//                            }else{//누적이 아닌경우      
//                                cpdm.setData(COMUtil.removeFrontZero(cre[i].trim()));
//                            }
                        } else {
//                        	moveVol = Integer.parseInt(cre[i].trim());
                            try {
                                moveVol = (int)Double.parseDouble(cre[i].trim());
                            } catch (Exception e) {

                            }
                        }
//                        if(cre[0].startsWith("99"))	    //2013.03.25 by LYH >> 증권정보 팝 분차트 실시간 처리.
//                            cpdm.setData(""+moveVol);
//                        else
                        if (this.addType==DATA_WEEK || this.addType==DATA_MONTH || this.addType==DATA_YEAR) {
                            if(!cpdm.getPacketTitle().equals("거래대금"))
                                cpdm.setData("" + moveVol);
                        }
                        else
                            cpdm.setData(""+((int)cpdm.getLastData()+moveVol));
                    //2023.03.15 by SJW - 애프터마켓 추가 >>
                    } else if(cpdm.getPacketTitle().equals("sessionIds")) {
                        cpdm.setData(cre[i].trim());
                    }
                    //2023.03.15 by SJW - 애프터마켓 추가 <<
                }
            }
        }
        if(time_check){//&&last_cre!=null){
            DataChangeEvent evt = new DataChangeEvent(this,this,DataChangeEvent.ADDDATA_CHANGE);
            processDataChangeEvent(evt);
            time_check =false;
        }else{
            if(addType==DATA_MIN||addType==DATA_TIC ||addType==DATA_SECOND||addType==DATA_WEEK||addType==DATA_MONTH||addType==DATA_YEAR){
                DataChangeEvent evt = new DataChangeEvent(this,this,DataChangeEvent.NEWDATA_CHANGE);
                processDataChangeEvent(evt);
            }
        }
    }
    //종가를 가지고 시,고,저,종가를 만든다
    public synchronized void makeBongData(String d){
        //2013.03.25 by LYH >> 증권정보 팝 분차트 실시간 처리.
        if(cre[0].startsWith("99"))
        {
            if(basic_data==null)
                basic_data = new String[4];
            for(int i=0;i<basic_data.length;i++){
                basic_data[i]=cre[i+1];
            }
        }
        else
        //2013.03.25 by LYH <<
        {
            if(basic_data==null){
                basic_data = new String[4];
                for(int i=0;i<basic_data.length;i++){
                    basic_data[i]=d;
                }
            }else{
                basic_data[3]=d;
                double end=Double.parseDouble(basic_data[3]);
                //2013. 10. 22  실시간 데이터 배열이 세팅안되서 실시간 받았을 때 죽는 현상  >>
//	            double tmp=Double.parseDouble(basic_data[2]);//저가
                double tmp = 0;
                try
                {
                    tmp=Double.parseDouble(basic_data[2]);
                }
                catch(Exception e){
                }
                //2013. 10. 22 실시간 데이터 배열이 세팅안되서 실시간 받았을 때 죽는 현상  <<
                if(end<tmp)basic_data[2]=d;
                else{
                    //2013. 10. 22 실시간 데이터 배열이 세팅안되서 실시간 받았을 때 죽는 현상  >>
//	                tmp=Double.parseDouble(basic_data[1]);//고가
                    try
                    {
                        tmp=Double.parseDouble(basic_data[1]);
                    }
                    catch(Exception e)
                    {
                        tmp = 0;
                    }
                    //2013. 10. 22 실시간 데이터 배열이 세팅안되서 실시간 받았을 때 죽는 현상  <<
                    if(end>tmp)basic_data[1]=d;
                }
            }
        }
    }
    /*private void makeMinData(String packetTitle, String value){
        if(packetTitle.equals("종가")){
            if(basic_data==null){
                basic_data = new int[5];
                basic_data[0] = Integer.parseInt(value); // 현재가(종가)
            }
            basic_data[3] = Integer.parseInt(value); // 현재가(종가)
            if(basic_data[1]==0||basic_data[1]<basic_data[3])basic_data[1]=basic_data[3];//고가
            if(basic_data[2]==0||basic_data[2]>basic_data[3])basic_data[2]=basic_data[3];//저가
        }else if(packetTitle.equals("기본거래량")){
            basic_data[4] = basic_data[4]+Integer.parseInt(value); // 변동거래량
        }else return;
    }*/

    public void addMinData(String time){
        ChartPacketDataModel cpdm = (ChartPacketDataModel)_dataModels.get("자료일자");
        if(dateFormat==XScale.DDHHMMSS)
            cpdm.addRealData(new String(cpdm.getLastStringData().substring(0,2))+time);
        else  cpdm.addRealData(time);
        /*if(basic_data!=null){
            addSubPacketData("시가", basic_data[0]);
            addSubPacketData("고가", basic_data[1]);
            addSubPacketData("저가", basic_data[2]);
            addSubPacketData("종가", basic_data[3]);
            addSubPacketData("기본거래량", basic_data[4]);
        }else{*/
        cpdm = (ChartPacketDataModel)_dataModels.get("종가");

        addSubPacketData("시가", cpdm.getLastData());
        addSubPacketData("고가", cpdm.getLastData());
        addSubPacketData("저가", cpdm.getLastData());
        addSubPacketData("종가", cpdm.getLastData());
        addSubPacketData("기본거래량", 0);
        //}
        //피봇데이터
        if(has_pivot_data){
            String[] title={"피봇2차저항","피봇1차저항","피봇가","피봇1차지지","피봇2차지지"};
            for(int i=0;i<title.length;i++){
                cpdm = (ChartPacketDataModel)_dataModels.get(title[i]);
                if(cpdm!=null)addSubPacketData(title[i], cpdm.getLastData());
            }
        }
        //basic_data = null;
    }
    private synchronized boolean isEndPrice(String data){
        if(data.equals("종가"))return true;
        return false;
    }
    public int getTerm(){
        //System.out.println("_cdm.getTerm()-->"+real_term);
        return real_term;
    }
    //==========================================
    // 데이터 마진을 설정한다
    //==========================================
    public void setMargine(int n){
        VIEW_MARGIN = n;
    }
    //==========================================
    // key에 해당하는 ChartPacketDataModel을 리턴
    //=================g=========================
    public ChartPacketDataModel getChartPacket(String key){
        return (ChartPacketDataModel) _dataModels.get(key);
    }
    //==========================================
    // 전체 패킷데이터 길이를 리턴
    //==========================================
    /*public int getPacketAllLength(){
        return PACKET_LENGTH;
    }*/
    //==========================================
    // 날짜타입을 리턴한다
    // 실시간 데이터를 add시킬때 참조
    //==========================================
    public int getDataType(){
        return dataType;
    }
    //==========================================
    // 최대/최소값
    //==========================================
    /*public int[] getMinMax(int[][] data){
        int[] minmax= new int[2];
        minmax[0] = MinMax.getIntMin(data);
        minmax[1] = MinMax.getIntMax(data);
        return minmax;
    }*/
    //==========================================
    // 데이터 종류의 문자열을 인풋으로 하여 데이터의 실제 인덱스를 정수형으로 리턴한다
    //==========================================
    /*public int getDataIndex(String datakind){
        String tmp = (String) _dataModels.get(datakind);
        if(tmp==null)tmp = "999";
        return (Integer.parseInt(tmp));
    }*/
    //==========================================
    // 데이터 마진 리턴
    //==========================================
    public int getMargine(){
        return 0;
        //return VIEW_MARGIN;
    }

    //==========================================
    // 데이터의 format를 리턴한다 
    //==========================================
    public int getDataFormat(String datakind){
        if(_dataModels==null) return -1;
        int tmp=-1;
        try {
            ChartPacketDataModel cpdm =  _dataModels.get(datakind);
            if(cpdm != null)
                tmp = cpdm.getPacketFormat();
        }catch(Exception e) {
            return -1;
        }
        return tmp;
    }
    public int getDataFormat_org(String datakind){
        ChartPacketDataModel cpdm = ((ChartPacketDataModel) _dataModels.get(datakind));
        if(cpdm!=null) return cpdm.getPacketFormatOrg();
        else return this.priceFormat;
    }
    //==========================================
    // 전체데이터의 길이를 리턴한다
    //==========================================
    public int getCount(){
        int max=0;
        ChartPacketDataModel cpdm;
        if(_basicDataTitle==null)return 0;
        for(int i=0;i<_basicDataTitle.size();i++){
            cpdm = (ChartPacketDataModel)(_dataModels.get(_basicDataTitle.elementAt(i)));
            if(cpdm!=null){
                int tmp = cpdm.getDataCount();
                if(tmp>max)max=tmp;
            }
        }

        return max;
    }
    public void clearData(){
        if(_dataModels!=null){
            for(int i=0;i<_basicDataTitle.size();i++){
                String t = (String)_basicDataTitle.elementAt(i);
                ChartPacketDataModel cpdm = (ChartPacketDataModel) _dataModels.get(t);
                cpdm.clear();
            }
        }
    }
    public void destroy(){
        clearData();
        if(_dataModels!=null){
            _dataModels.clear();
            //_dataModels=null;
        }
        if(_basicDataTitle!=null){
            _basicDataTitle.removeAllElements();
            //_basicDataTitle=null;
        }
        if(_dataTitles!=null){
            _dataTitles.removeAllElements();
            //_dataTitles=null;
        }
//        if(codeItem != null)
//        {
//        	codeItem.clear();
//        	//codeItem = null;
//        }
        if(realInfo != null)
        {
            realInfo.removeAllElements();
        }
        _useRealTitle.removeAllElements();
        listeners.removeAllElements();
    }
    //==========================================
    // 데이터 마진 리턴
    //==========================================
    public int getMargin(){
        //return 0;
        return VIEW_MARGIN;
    }
    //==========================================
    // 데이터 리스너관련 메쏘드
    //==========================================
    public void addDataChangedListener(DataChangedListener l){
        listeners.addElement(l);
    }
    public void removeDataChangedListener(DataChangedListener l){
        listeners.removeElement(l);
    }
    protected synchronized void processDataChangeEvent(DataChangeEvent evt){
        if(listeners==null) return;
        Enumeration<DataChangedListener> e = listeners.elements();
        while(e.hasMoreElements()){

            DataChangedListener l = (DataChangedListener)e.nextElement();
            switch(evt.getDataState()){
                case DataChangeEvent.INDEX_CHANGE:
                    l.DataIndexChanged(evt);
                    break;
                case DataChangeEvent.NEWDATA_CHANGE:
                    //l.DataChanged(evt);
                    l.DataAdded(evt);
                    break;
                case DataChangeEvent.ADDDATA_CHANGE:
                    l.DataAdded(evt);
                    break;
                case DataChangeEvent.INSERTDATA_CHANGE:
                    l.DataInserted(evt);
                    break;
            }
        }
    }

    //2014.03.18 by LYH << 지표 소수점 자리수 가격과 동일하게 처리.
    public void setSyncPriceFormat(String strTitle)
    {
        if(_basicDataTitle!=null){
            ChartPacketDataModel cpdm = (ChartPacketDataModel) _dataModels.get(strTitle);
            if(cpdm != null && nDispScale>0)
            {
                if(cpdm.getPacketTitle().equals("자료일자")||cpdm.getPacketTitle().equals("기본거래량")||cpdm.getPacketTitle().equals("거래대금")||cpdm.getPacketTitle().equals("매수거래량")||cpdm.getPacketTitle().equals("매도거래량")||cpdm.getPacketTitle().equals("락구분")||cpdm.getPacketTitle().equals("락비율"))
                    return;
                cpdm.setPacketFormat(16);
                cpdm.nTradeMulti = nTradeMulti;
                cpdm.nDispScale = nDispScale;
                cpdm.nLogDisp = nLogDisp;
            }
        }
    }
    //2014.03.18 by LYH << 지표 소수점 자리수 가격과 동일하게 처리.

    //2016.10.27 by lyk - 비교차트 진법 적용
    public void setPriceFormatCompare(String data, int nScale, int nDecPoint, int nLogDisp, String sCode) {
        this.nTradeMulti = nDecPoint;
        this.nDispScale = nScale;
        this.nLogDisp = nLogDisp;

        priceFormat=ChartUtil.getPacketFormatIndex(data);

        ChartPacketDataModel cpdm;
        cpdm = (ChartPacketDataModel)_dataModels.get(sCode);
        if(cpdm != null)
        {
            cpdm.setPacketFormat(priceFormat);
            cpdm.nTradeMulti = nTradeMulti;
            cpdm.nDispScale = nDispScale;
            cpdm.nLogDisp = nLogDisp;
        }
    }
    //2016.10.27 by lyk - 비교차트 진법 적용 end

    //2014.05.23 by LYH >> 날짜 구분선 시작 시간 포함.
    public void setOpenTime(String strOpenTime)
    {
        m_strOpenTime= strOpenTime;
    }

    public String getOpenTime()
    {
        return m_strOpenTime;
    }
    //2014.05.23 by LYH << 날짜 구분선 시작 시간 포함.

    //2015. 2. 5 선옵 1분선 기초자산/미결제약정 실시간>>
    /**
     * 1분선차트 타입인지를 ChartDataModel 에 기록
     * @param bFlag  true(standardline타입)  false(standardline타입이 아님) 
     * */
    public void setStandardLine(boolean bFlag)
    {
        bStandardLine = bFlag;
    }
    //2015. 2. 5 선옵 1분선 기초자산/미결제약정 실시간<<

    //2016.01.05 by LYH >> 분차트 30초 보정 처리
    public void setRealData(String[] realData)
    {
        int nIndex = -1;
        for (int i=0; i< _useRealTitle.size(); i++) {
            ChartPacketDataModel cpdm = getChartPacket(_useRealTitle.get(i));
            if (cpdm.getPacketTitle().equals("자료일자")) {
                String strTime = realData[i];
                if(addType==DATA_MIN || addType==DATA_TIC || addType==DATA_SECOND)
                {
                    String strDate = cpdm.getDate(cpdm.getLastStringData());
                    strTime = String.format("%s%s", strDate, strTime);
                }

                nIndex = cpdm.findMinDataIndex(strTime);

                if(nIndex<0)
                {
                    if(strTime.length()>8) {
                        strTime = makeTime(strTime.substring(4));
                        strTime = cpdm.getDate(cpdm.getLastStringData())+strTime;

                        cpdm.addRealData(strTime);
                    }
                }
            }
            else
            {
                if(nIndex != -1)
                {
                    cpdm.setDataAtIndexMinData(realData[i], nIndex);
                }
                else
                {
                    cpdm.addRealData(realData[i]);
                }
            }
        }

        if(nIndex<0) {
            COMUtil.isRealTicState = true; //실시간 분, 틱인 경우 설정
            DataChangeEvent evt = new DataChangeEvent(this, this, DataChangeEvent.ADDDATA_CHANGE);
            processDataChangeEvent(evt);
        }
        return;
    }
//2016.01.05 by LYH << 분차트 30초 보정 처리

    public boolean addCompareData(String strTitle, String strTime)
    {
    	if(_dataModels == null)
    		return false;
    	ChartPacketDataModel cpdm = getChartPacket(strTitle);
        try {
             time_check = realTimeCheck(strTime);
             if(time_check){
             	strTime = makeTime(strTime);
             	
             	 if(addType == DATA_MIN || addType == DATA_TIC || addType==DATA_SECOND)
                      strTime = cpdm.getDate(cpdm.getLastStringData())+strTime;
                  addRealDataByType(cpdm,strTime);
                  return true;
             }
        } catch(Exception e) {
        }
        return false;
    }
}