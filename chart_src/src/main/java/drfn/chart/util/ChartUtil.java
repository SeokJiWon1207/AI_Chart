package drfn.chart.util;

import android.graphics.Color;
import android.graphics.Paint.FontMetrics;
import android.graphics.PointF;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import drfn.chart.model.ChartDataModel;

public class ChartUtil{
    FontMetrics _f;
    private static Hashtable<String, String> field_format;
    private static Hashtable<String, String> b_msg;
    //private static StringBuffer buf= new StringBuffer();

    public static String[] pop_item={//오른쪽 마우스 클릭했을때 뜨는 팝업창의 item
            "수치조회",
            "자료조회",
            "전체보기",
            //"-",
            //"십자선",
            //"추세선 추가",
            //"분석도구 전체삭제",
            "-",
            "프린트",
            "초기화",
            "차트설정"
    };
    public static String[] dt_pop_item={//오른쪽 마우스 클릭했을때 뜨는 팝업창의 item
            "삭제",
            "색상변경",
            "굵기변경",
    };
    public static String[][] tool_bar={
            {"추세선","0" },//0
            {"십자선","1" }, //1
            {"수직선긋기","2" },//2
            {"수평선긋기","3" },//3
            {"-","9999"},
            {"피보나치아크","6" },//6
            {"피보나치팬","7" },//7
            {"피보나치시간대","8" },//8
            {"피보나치조정대","11" },//11
            {"-","9999"},
            {"갠팬","12" },//12
            {"갠그리드","13" },//13
            {"-","9999"},
            {"삼등분선","4"},//4
            {"사등분선","5"},//5
            {"스피드라인","14"},//14
            {"앤드류스피치포크","15"},//15
            {"-","9999"},
            {"사각영역","9"}, //9
            {"원형영역","10"},//10
            {"문자","16"}//16
    };
    public static String[] pop_tooptip={
            "간략형",
            "상세형",
            "취소"
    };
    /*public static String[] packet_data_name={
        "자료일자","Text","시가","고가","저가",//0-4
        "종가","기본거래량","누적거래량","락유무","주식배수",//5-9
        "환율","전체종목수","상승종목수","하락종목수","ADL",//10-14
        "MOBV","상승종목거래량","하락종목거래량","선도주 거래량","52신고종목수",//15-19
        "52신저종목수","상장주식수","거래형성종목수"//20-22
    };*/
    public static String[] packet_field_format={
            "YYYYMMDD","YYMMDD","YYYYMM","YYMM","MMDD",//0-4
            "DDHH","DDHHMM","DDHHMMSS","HHMMSS","HHMMSSNN",//5-9
            "문자","× 1","× 1000","× 0.1","× 0.01",//10-14
            "× 0.001","× 0.0001","%","× 1000000","HHMM",//15-19
            "MMDDHHMM", "YYYY"//20-21
    };
    public static String[] bojo_msg={
            "UM","UD","UT","RDORT","RDATET",//0-4
            "PREVPRICE","FUNDSTARTPOINT","FUNDENDPOINT","USEPACKET","BOUNDMARK","RESETUSEREAL",//5-10
            "PRICEFORMAT","MARKET","RESETPACKET","TDC","UTEC","OPENTIME"//11,12,13,14,15,16
    };

    public static String[] graph_definition={
            "","이동평균선의 활용방법으로는 1.주가와 이동평균선의 관계를 분석하는 방법,2. 단기 이동평균선과 장기 이동평균선의 관계를 분석하는 방법,3. 그랜빌의 정리를 이용하는 방법등이 있습니다.◇ 종목별로 가장 적당한 이동평균선을 결정하는 방법 : 종목의 최고점과 최저점 사이의 기간을 2로 나눈 후 1을 더합니다.  이 방법으로 일간이동평균선 기간을 구했으면, 주간 이동평균선은 일간/5 , 월간 이동평균선은 일간/21로 구합니다. ",
            "","","일반적으로, 단기이동평균선을 사용했을 경우,상승추세일 경우 98% 이하 매수시점, 106% 이상 매도시점.하락추세일 경우 92% 이하 매수시점, 102% 이상 매도시점이며,장기이동평균선을 사용했을 경우,상승추세일 경우 98% 이하 매수시점, 110% 이상 매도시점.하락추세일 경우 88% 이하 매수시점, 104% 이상 매도시점으로 봅니다. 실전에서는 종목에 따라 적당한 수치를 찾는 것이 중요합니다",
            "[기준선과 전환선 분석] 전환선이 기준선 위에 위치하면 매수국면, 아래에 위치하면 매도국면, 단, 기준선의 추세가 상승추세이면 매도보류. [기준선과 후행스팬 분석]기준선이 상승추세일 때 후행스팬이 주가를 상향돌파하면 강세국면으로 전환가능성, 돌파에 실패시 약세국면이 강화됨. [주가와 선행스팬 분석]주가가 선행스팬사이의 구름층을 상향돌파하면 강세국면으로 전환, 구름층의 두께가 지지 또는 저항의 강도라고 볼 수 있음",
            "일정기간 대비 주가가 오른 날의 비율을 구함으로써 투자자들의 투자심리를 미루어 짐작하고자 하는 지표입니다.보통 75% 이상이면 과열권,25% 이하이면 침체권으로 가정합니다. 본 지표만을 가지고 투자하는 것은 적합하지 않다고 생각되며 여타의 지표에 대한 참고지표 정도로 고려하는 것이 좋습니다",
            "당일시가와 당일종가의 상승하락 관계를 살펴서 두개의 선그래프 (A Ratio, B Ratio)로 표현합니다.  일반적으로 A Ratio가 B Ratio를 상향돌파하면 매수신호이고 하향돌파하면 매도신호입니다.  사용자 입력수치는 수식에서 일정기간(n)의 합계를 위한 기간값입니다",
            "주가가 어떤 밴드를 돌파하면 돌파된 밴드는 지지선의 역할을 하고, 그 상위 밴드는 저항선의 역할을 한다. 특히 중간밴드는 하락추세에서는 저항선의, 상승추세에서는 지지선의 역할을 한다. 밴드의 폭이 좁아진 후에는 주가가 급격하게 움직이는 경향이 있다. 주가가 상하한 밴드 폭 밖으로 이탈하면 현재의 추세가 유지되는 경향이 있다.이탈된 주가가 밴드 폭 안으로 들어올 때 추세가 전환된 것으로 판단한다",
            "CCI(Commodity Channel Index):주가평균과 현재주가 사이의 편차를 측정하는 지표입니다",
            "CO (Chaikin's Oscillator):AD 지표를 기초로 작성한 이동평균 오실레이터 지표로.CO값의 추세와 주가 추세를 서로 비교하여 괴리가 발생하면 CO값의 추세 방향대로 주가가 움직인다고 해석됩니다. ",
            "전일대비 고가와 저가의 절대값 중 어느 쪽이 큰가를 판단하여 추세의 방향을 표시하는 지표입니다.  2개의 선그래프로 표시되며, ADX, ADXR, ATR 등의 지표와 함께 분석하는 것이 일반적입니다.  일반적으로 DI+가 DI-를 상향돌파할 때 매수시점, 하향돌파할 때 매도시점으로 간주합니다.  사용자 입력수치는 이동평균기간(n)입니다",
            "3개의 밴드를 매매시점으로 잡을 수 있다. 주가가 어떤 밴드를 돌파하면 돌파된 밴드는 지지선 역할을, 상위밴드는 저항선의 역할을 한다. Envelope보다는 Bollinger Bands가 더욱 발전된 지표이므로 Bollinger Bands를 활용하는 것이 좋다",
            "",
            "MACD는 주가의 장,단기 이동평균선의 관계를 보여주는 지표입니다.  MACD는 단순이동평균을 사용하지 않고 지수이동평균 (Exponential Moving Average)을 사용합니다.  교차분석, 과매도/과매수 분석 등을 통해 매매시점을 잡아낼 수 있습니다.  사용자 입력수치는 단기 이동평균기간, 장기이동평균기간, Signal Line을 구하는 이동평균기간입니다.  추천하는 기간값은 단기 12, 장기 26, 시그널라인 9입니다.  MACD에서 발전한 지표가 MACD OSC입니다",
            "MACD Osc는 MACD와 MACD의 이동평균인 Signal Line의 관계를 통해 매매시점을 파악하는 지표입니다.  0선분석을 통해 매매시점을 잡아낼 수 있습니다",
            "MFI는 주식시장의 자금이 얼마만큼 유입과 유출되고 있는지 그 힘의 강도를 측정하는 모멘텀 지표로 RSI (Relative Strength Index)와 강도 측정면에서는 유사하나 RSI는 가격만을 사용하는 지표임에 반해 MFI는 거래량도 포함한 지표입니다.",
            "Mass Index는 주가의 9일 지수 이동평균선이 주로 반전우세가 매수 신호인지 매도 신호인지를 결정하는데 사용되는데, 만약 반전우세 현상이 발생되었을 때 9일 지수 이동평균선이 상승추세이면 매도 신호롤 보고 하락추세이면 매수 신호로 분석합니다",
            "주가의 상대적인 가격 속성을 이용하여 주가 추세의 속도가 증가하는지 감소하는지를 측정하는 지표입니다. 모멘텀은 현재 주가에서 일정 기간 이전의 주가를 차감해서 계산합니다",
            "Norman Fosback에 의하면 NVI 값이 1년 이동평균 위에 있을때 강세 시장의 확률은 95∼100% 정도로 분석하고, NVI 값이 1년 이동평균 아래에 있을때 강세 시장의 확률은 50/50 정도로 분석 하기때문에 NVI 값은 강세시장 지표로 가장 유용한 지표로 볼 수가 있습니다. 결국 NVI 값이 255일 이동평균 위로 돌파되면 매수시점, 아래로 돌파되면 매도 시점으로 분석합니다",
            " 절대수치는 의미가 없고, 추세를 봅니다.또한 한 방향으로 추세를 형성하면 상당기간 지속되는 경향이 있으며 한 방향으로 추세를 형성하고 있던 OBV선이 다른 방향으로 추세가 전환되면 매매시점으로 볼 수 있습니다.",
            "","",
            "Welles Wilder에 의해 개발된 지표로서 추세전환의 신호를 포물선의 형태로 표현해 주는 지표입니다.  일반적으로 매매신호가 늦지만 발생했을 경우 확실한 신호를 줍니다.  특히 추세시장에서 유용합니다.  사용자 입력수치는 가속변수라고도 부르는 AF값의 최대값과 증가단위입니다.  일반적으로 AF최대값은 0.2, AF증가분은 0.02를 사용합니다",
            "",
            "PVI의 해석방법은 먼저 거래량이 증가하는 날은 정보에 미흡한 군중들이 시장에 참여한다고 가정하고, 반대로 거래량이 감소하는 날은 기금 형태의 자금이 주로 매매를 한다고 가정을 하여 해석하는데, 이 결과로 PVI는 정보에 미흡한 군중들의 움직임 형태를 파악한 것입니다.이 지표는 통상 255일 이동평균을 사용하거나 52주 이동평균을 사용하는데, NVI와 마찬가지로 PVI 지표가 이동평균을 상향하면 매수 시점, 하향하면 매도 시점으로 분석을 합니다",
            "",
            "Welles Wilder에 의해서 개발된 지표입니다.  시장가격의 변동폭 중에서 상승폭이 차지하는 비중이 어느정도인가를 파악하여 추세의 강도가 어느 정도인가를 측정하는 지표입니다.RSI의 수치가 70 이상이면 과열국면으로 판단하며 RSI의 수치가 30 이하이면 침체국면으로 판단합니다. 과열침체권에서는 신뢰도가 있습니다 ",
            "소나의 기본개념은 주가곡선의 접선의 기울기를 지표화한 것입니다.  그러나 실제로는 매매신호가 너무 많이 발생하는 것을 방지하기 위해 지수이동평균의 n일전 대비 상승률을 지표화하고 있습니다. '0'선을 중심으로 상향돌파하면 매수시점, 하향돌파하면 매도시점으로 가정합니다",
            "현재의 주가가 일정기간의 최고가와 최저가의 범위 중 어느 정도의 수준에 있는지를 보여주는 지표입니다.  Stochastics 지수가 높을수록 현재주가가 해당기간 중 최고가 부근에 있는 것입니다.  사용자 입력수치는 Fast %K를 구하기 위한 해당기간 값과 Fast %D값을 구하기 위한 이동평균기간입니다",
            "주가등락과 거래량을 연관시킨 지표입니다.  OBV와 상호보완적인 지표로서 시세의 강약을 파악하는데에 사용됩니다.  VR의 수치는 통상적으로 높으며, 과열권보다는 침체권의 신뢰도가 큽니다.VR이 60% 이하로 떨어지는 경우는 상당한 정도의 침체장세가 아니면 발생하기 힘들다는 점을 고려하면, VR의 과열신호보다는 침체신호가 더욱 신뢰도가 있습니다",
            "Williams' % R이 -80에서 -100% 사이에 있으면 시장은 과매도 상태로 볼 수 있고 0에서 -20% 범위는 과매수되고 있는 상태로 분석합니다. 과매수, 과매도 지표의 대다수가 그렇듯이 이 지표는 투자자가 방향설정에 앞서 반드시 주가 변화를 기다린 후 매매에 참여함이 바람직합니다",
            "TRIX (Triple Smoothed Moving Averages)종가의 지수이동평균을 세번 평활시켜 그 변화비율을 퍼센트로 나타낸 모멘텀 지표로서 세차례의 평활과정을 통해 불필요한 Whipsaw현상을 없앤 것입니다 ",
            "누적 거래량이 많은 가격대가 매물대라고 볼 수 있습니다.  주가가 매물대 위에 있으면, 매물대는 지지구간의 역할을 하고, 매물대 밑에 있으면 매물대는 저항구간의 역할을 하게 됩니다.누적 거래량이 적은 구간에서는 주가가 빠른 속도로 움직이는 것이 일반적인 현상입니다",
            "시간개념이 없는 비시계열 차트의 일종입니다.  또한 거래량도 반영되어 있지 않습니다.  매매시점 파악보다는 주가의 장기추세의 전환시점 확인용으로 사용됩니다.양전환시 매수, 음전환시 매도합니다.양선과 음선이 교차하여 나타나는 횡보장세에서는 매매를 유보합니다",
            "주가와 거래량의 관계를 분석하여 추세의 전환을 예측하는데 사용됩니다.  거래량은 주가에 선행한다는 것을 전제로 합니다.  중장기 매매시점 포착에 유리합니다.",
            "PnF의 활용방법으로 비슷한 가격대에 O표시가 많으면 지지구간으로, 비슷한 가격대에 X표시가 많으면 저항구간으로 볼수 있습니다. 전고점을 돌파하는 최초의 X표시가 매수시점입니다.전저점을 돌파하는 최초의 O표시가 매도시점입니다",
            "","","",
            "Fast Stochastics는 지표의 등락이 상당히 심한 경우가 있기 때문에 Fast Stochastics의 값을 다시한번 이동평균하여 평활시킨 것이 Slow Stochastics입니다.  사용자 입력수치는 Fast %K를 구하기 위한 기간값과 Slow %K (=Fast %D)를 구하기 위한 이동평균기간, Slow %D값을 구하기 위한 이동평균기간입니다.",
            "",
            "주가와 이동평균간 차이의 표준편차를 구한 것으로서, 변동성을 측정하는 지표입니다. 일반적으로 주가의 고점에서는 변동성이 크고, 주가의 저점에서는 변동성이 작다는 가정하여 활용합니다"

    };
    public final static int JBONG = 10000;
    public final static int JBONG_TRANSPARENCY = 19999; //2021.04.15 by lyk - kakaopay - 투명 캔들 타입
    public final static int ABONG = 10001;
    public final static int PLINE = 10002;//가선차트
    public final static int STANDSCALE = 11000;//매물분석도

    public final static int PNF = 20001;//PNF
    public final static int TCHANGE = 20002;//삼선전환
    public final static int SWING = 20003;//SWING
    public final static int RENKO = 20004;//RENKO
    public final static int KAGI = 20005;//KAGI
    public final static int REVERSE_CLOCK = 20006;//역시계
    public final static int VARIANCE = 20007;//분산형

    public final static int VOLUME = 30000;//거래량
    public final static int MACD_OSC = 30001;//macd_osc
    public final static int MACD = 30002;//macd
    public final static int STOCH_SLW = 30003;//스토케스틱_slow
    public final static int STOCH_FST = 30004;//스토케스틱_fast
    public final static int DMI = 30005;//DMI
    public final static int ADX = 30006;//ADX
    public final static int AB_Ratio = 30007;//ADX
    public final static int RSI = 30008;//RSI
    public final static int OBV = 30009;//OBV
    public final static int REVERSE= 30010;//REVERSE
    public final static int SONAR= 30011;//SONAR
    public final static int STDEV= 30012;//STDEV
    public final static int VMAO= 30013;//VMAO
    public final static int VR= 30014;//VR
    public final static int DISPARITY= 30015;//DISPARITY(이격도)
    public final static int PSYCO= 30016;//PSYCO(심리도)
    public final static int WILLIAMS= 30017;//WILLIAMS
    public final static int Momentum= 30018;//Momentum
    public final static int CCI= 30019;//CCI
    public final static int CO= 30020;//CO
    public final static int MAC= 30021;//MAC
    public final static int MFI= 30022;//MFI
    public final static int MI= 30023;//MI
    public final static int NVI= 30024;//NVI
    public final static int TRIX= 30025;//Trix

    public final static int ADLINE= 30026;//AD Line			//2015. 1. 13 ADLine 지표 추가
    public final static int LRS= 30027;//LRS				//2015. 2. 13 LRS 지표 추가

    //2015.06.08 by lyk - 신규지표 추가
    public final static int RCI = 30030;//RCI				//2015. 6. 8 RCI 지표 추가
    public final static int ROC = 30028;//ROC				//2015. 6. 8 ROC 지표 추가
    public final static int RMI = 30029;//RMI				//2015. 6. 9 RMI 지표 추가
    public final static int VHF = 30031;//VHF				//2015. 6. 10 VHF 지표 추가
    public final static int SROC = 30032;//RCI				//2015. 6. 11 SROC 지표 추가
    public final static int CV = 30033;//CV				//2015. 6. 11 CV 지표 추가
    public final static int VROC = 30038;//RCI				//2015. 6. 15 VROC 지표 추가
    public final static int SIGMA = 30034;//Sigma
    public final static int ATR= 30035;//ATR
    public final static int OSCP= 30036;//OSCP
    public final static int OSCV= 30037;//OSCV               (Volume Oscillator)
    public final static int NCO = 30039;//NCO				//2015. 6. 15 NCO 지표 추가
    public final static int PVT = 30040;//PVT				//2015. 6. 15 PVT 지표 추가
    public final static int LRL = 30041;//LRL				//2015. 6. 15 LRL 지표 추가
    //2015.06.08 by lyk - 신규지표 추가 end
    public final static int VOLUME_SELLBUY = 30042;		//매수매도거래량
    public final static int TRADING_VALUE = 30043;		//거래대금

    public final static int STOCH_OSC = 30046; //Stochatics Oscillator
    public final static int PROC = 30047; // Price ROC
    public final static int EOM = 30044; // EOM
    public final static int NEWPSYCO = 30045; // 신심리도
    public final static int MACDPLUSOSC = 30048; // macd + macdOsc
    public final static int BANDB = 30049;

    public final static int BANDBSTOCH = 30050;
    public final static int LRSSTOCH = 30051;
    public final static int MACDSTOCH = 30052;
    public final static int MOMENTUMSTOCH = 30053;
    public final static int OBVMOMENTUM = 30054;
    public final static int OBVSTOCH = 30055;
    public final static int ROCSTOCH = 30056;
    public final static int RSIMACD = 30057;
    public final static int RSISTOCH = 30058;
    public final static int SONARPSYCO = 30059;
    public final static int STOCHRSI = 30060;
    public final static int TSF = 30061; //2019. 06. 28 by hyh - TSF 지표 추가
    public final static int MAO = 30062; //2019. 10. 16 by hyh - MAO 지표 추가

    //2017.08.10 by pjm >> 신규지표 추가
    public final static int CMF = 30063;
    public final static int FORCE_INDEX = 30064;
    public final static int VA_OSC = 30065;
    public final static int ADXR = 30066;
    public final static int PSYCHOLOGY_INDEX = 30067;
    public final static int BPDL_SHORT_TREND = 30068;
    public final static int BPDL_STOCH = 30069;
    public final static int ELDER_RAY_BEAR_POWER = 30070;
    public final static int ELDER_RAY_BULL_POWER = 30071;
    public final static int FORCE_INDEX_LONG_TERM = 30072;
    public final static int FORCE_INDEX_SHORT_TERM = 30073;
    public final static int GM_McCLELAN_OSC = 30074;
    public final static int GM_McCLELAN_SUM = 30075;
    public final static int LFI = 30076;
    public final static int MOVING_BALANCE_INDICATOR = 30077;
    public final static int OBV_WITH_AVERAGE_VOLUME = 30078;
    public final static int OBV_MIDPOINT = 30079;
    public final static int OBV_OSC = 30080;
    public final static int TRIN_INVERTED = 30081;
    public final static int VMP_ACC = 30082;
    public final static int BANDWIDTH = 30083;
    public final static int DISPARITYINDEX = 30084;
    public final static int DPO = 30085;
    public final static int DRF = 30086;
    public final static int FORMULA = 30087;
    public final static int NDI = 30088;
    public final static int QSTIC = 30089;
    public final static int PVI = 30090;
    public final static int BOLLINGER_2 = 30091;
    //2017.08.10 by pjm >> 신규지표 추가 end
    //Market
    public final static int MARKET1= 30100;//외국인 비율
    public final static int MARKET2= 30101;//외국인/기관/개인 추세
    public final static int MARKET3= 30102;//외국인 순매수
    public final static int MARKET4= 30103;//기관 순매수
    public final static int MARKET5= 30104;//개인 순매수
    public final static int MARKET6= 30105;//기관 순매수 누적
    public final static int MARKET7= 30106;//시가총액
    public final static int MARKET8= 30107;//신용잔고
    public final static int MARKET9= 30108;//신용잔고율
    public final static int MARKET10= 30109;//외국인 소진율
    public final static int MARKET11= 30110;//외국인 보유비중
    public final static int MARKET12= 30111;//외국인보유량
    public final static int MARKET13= 30112;//외국인 순매수
    public final static int MARKET14= 30113;//외국인 순매수 누적
    public final static int MARKET15= 30114;//종목거래회전
    public final static int MARKET16= 30115;//투자자매매동향
    public final static int MARKET17= 30116;//투자자별(거래소)
    public final static int MARKET18= 30117;//투자자별(코스닥)
    public final static int MARKET19= 30118;//투자자별(K200선물)
    public final static int MARKET20= 30119;//투자자별(콜옵션)
    public final static int MARKET21= 30120;//투자자별(풋옵션)
    public final static int MARKET22= 30121;//프로그램매매 순매수	
    public final static int MARKET23= 30122;//프로그램매매 순매수	누적	//기술적지표(23)

    //2017.05.11 by LYH >> 전략(신호, 강약) 추가
    public final static int INDICATOR_ADXRSTRATEGY = 30200;
    public final static int INDICATOR_CCIBASELINE = 30201;
    public final static int INDICATOR_CCIOVERSOLDOVERBOUGHT = 30202;
    public final static int INDICATOR_DMISIGNAL = 30203;
    public final static int INDICATOR_DISPARITYSIGNAL = 30204;
    public final static int INDICATOR_GOLDENDEADCROSSEMA = 30205;
    public final static int INDICATOR_GOLDENDEADCROSS = 30206;
    public final static int INDICATOR_MACDBASELINE = 30207;
    public final static int INDICATOR_PARABOLICSIGNAL = 30208;
    public final static int INDICATOR_SONARMOMENTUM = 30209;
    public final static int INDICATOR_SONARMOMENTUMSIGNAL = 30210;
    public final static int INDICATOR_STOCHASTICSKD = 30211;
    public final static int INDICATOR_WILLIAMSR = 30212;
    public final static int INDICATOR_ADXRSTRATEGY_SW = 30213;
    public final static int INDICATOR_CCIBASELINE_SW = 30214;
    public final static int INDICATOR_CCIOVERSOLDOVERBOUGHT_SW = 30215;
    public final static int INDICATOR_DMISIGNAL_SW = 30216;
    public final static int INDICATOR_DISPARITYSIGNAL_SW = 30217;
    public final static int INDICATOR_GOLDENDEADCROSSEMA_SW = 30218;
    public final static int INDICATOR_GOLDENDEADCROSS_SW = 30219;
    public final static int INDICATOR_MACDBASELINE_SW = 30220;
    public final static int INDICATOR_PARABOLICSIGNAL_SW = 30221;
    public final static int INDICATOR_SONARMOMENTUM_SW = 30222;
    public final static int INDICATOR_SONARMOMENTUMSIGNAL_SW = 30223;
    public final static int INDICATOR_STOCHASTICSKD_SW = 30224;
    public final static int INDICATOR_WILLIAMSR_SW = 30225;
    public final static int INDICATOR_GOLDENDEADCROSSMA_EMA = 30226;
    public final static int INDICATOR_GOLDENDEADCROSSMA_WMA = 30227;
    public final static int INDICATOR_GOLDENDEADCROSS_MULTI = 30228;
    public final static int INDICATOR_MACDSIGNAL = 30229;
    public final static int INDICATOR_MAOBASELINE = 30230;
    public final static int INDICATOR_PVISIGNAL = 30231;
    public final static int INDICATOR_GOLDENDEADCROSSMA_EMA_SW = 30232;
    public final static int INDICATOR_GOLDENDEADCROSSMA_WMA_SW = 30233;
    public final static int INDICATOR_GOLDENDEADCROSS_MULTI_SW = 30234;
    public final static int INDICATOR_MACDSIGNAL_SW = 30235;
    public final static int INDICATOR_MAOBASELINE_SW = 30236;
    public final static int INDICATOR_PVISIGNAL_SW = 30237;
    //2017.05.11 by LYH << 전략(신호, 강약) 추가 end
//    public final static int MARKET24= 30123;//투자자별 거래소(거래대금)
//    public final static int MARKET25= 30124;//투자자별 코스닥(거래대금)
//    public final static int MARKET26= 30125;//투자자별 거래소(거래량)
//    public final static int MARKET27= 30126;//투자자별 코스닥(거래량)
//    public final static int MARKET28= 30127;//투자자별 K200
//    public final static int MARKET29= 30128;//투자자별 선물
//    public final static int MARKET30= 30129;//투자자별 콜옵션
//    public final static int MARKET31= 30130;//투자자별 풋옵션
//    public final static int MARKET32= 30131;//기관순매수량
//    public final static int MARKET33= 30132;//기관누적순매수량
//    public final static int MARKET34= 30133;//개인순매수량
//    public final static int MARKET35= 30134;//개인누적순매수량
//    public final static int MARKET36= 30135;//등록외국인순매수량
//    public final static int MARKET37= 30136;//등록외국인누적순매수량	//투자자동향지표(14)
//    public final static int MARKET38= 30137;//순매수량(3년국채)
//    public final static int MARKET39= 30138;//순매수량(10년국채)
//    public final static int MARKET40= 30139;//순매수량(미국달러)
//    public final static int MARKET41= 30140;//순매수대금(3년국채)
//    public final static int MARKET42= 30141;//순매수대금(10년국채)
//    public final static int MARKET43= 30142;//순매수대금(미국달러)
//    public final static int MARKET44= 30143;//누적순매수량(3년국채)
//    public final static int MARKET45= 30144;//누적순매수량(10년국채)
//    public final static int MARKET46= 30145;//누적순매수량(미국달러)
//    public final static int MARKET47= 30146;//누적순매수대금(3년국채)
//    public final static int MARKET48= 30147;//누적순매수대금(10년국채)
//    public final static int MARKET49= 30148;//누적순매수대금(미국달러)		//상품선물투자자동향(12)


//    public static String[] strMarkets={
//        "외국인보유비중","투자자-개인(수량)","투자자-외국인(수량)","투자자-기관계(수량)","누적순매수량-개인",//0-4
//        "누적순매수량-외국인","누적순매수량-기관계","신용잔고율","신용잔고증감","거래금액","KOSPI지수",//5-10
//        "KOSDAQ지수","KOSPI200지수","원-달러","엔-달러","금",//11-15
//        "두바이유","서부텍사스유",//16-20
//    };
//    public static String[] strMarkets={
//    	"거래금액","외국인보유비중","투자자-개인(수량)","투자자-외국인(수량)","투자자-기관계(수량)",//0-4
//    	"누적순매수량-개인","누적순매수량-외국인","누적순매수량-기관계","신용잔고율","신용잔고증감","KOSPI지수",//5-10
//        "KOSDAQ지수","KOSPI200지수","원-달러","엔-달러",//11-14
//        "미결제약정(선물옵션)"	//2015. 2. 13 미결제약정 지표 추가
//    };

//    public static String[] strMarkets={
//             "개인 순매수", "개인 순매수 누적","기타법인 순매수", "기타법인 순매수 누적", "기관 순매수", "기관 순매수 누적","시가총액", "신용잔고", "신용잔고율",
//            "외국인 소진율", "외국인 보유비중", "외국인 보유량", "외국인 순매수", "외국인 순매수 누적", "종목거래량회전률",  "투자자매매동향", "투자자별(거래소)",
//            "투자자별(코스닥)", "투자자별(K200선물)", "투자자별(콜옵션)", "투자자별(풋옵션)", "프로그램매매 순매수", "프로그램 순매수 누적", //기술적지표 (23)
//           /* "투자자별 거래소(거래대금)", "투자자별 코스닥(거래대금)", "투자자별 거래소(거래량)", "투자자별 코스닥(거래량)", "투자자별 K200", "투자자별 선물",
//            "투자자별 콜옵션", "투자자별 풋옵션", "기관순매수량", "기관누적순매수량", "개인순매수량", "개인누적순매수량", "등록외국인순매수량", "등록외국인누적순매수량",  //투자자동향지표(14)
//            "순매수량(3년국채)", "순매수량(10년국채)", "순매수량(미국달러)", "순매수대금(3년국채)", "순매수대금(10년국채)", "순매수대금(미국달러)", "누적순매수량(3년국채)",
//            "누적순매수량(10년국채)", "누적순매수량(미국달러)", "누적순매수대금(3년국채)", "누적순매수대금(10년국채)", "누적순매수대금(미국달러)", //상품선물투자자동향(12)
//*/    };
//    public static String[] strMarkets={
//            "외국인순매수(장내)", "외국인순매수(장외포함)", "외국인소진율", "기관외인누적순매수", "기관순매수", "개인순매수", "개인누적순매수",
//            "신용융자 수량", "신용대주 수량", "신용융자 비율", "일별 공매도 거래량", "미수금", "고객예탁금"
//    };

    public static String[] strMarkets={
        "외국인 비율", "외국인/기관/개인 추세", "외국인 순매수", "기관 순매수", "개인 순매수", "신용 잔고율"
    };

    public final static int PARABOLIC= 40000;//Parabolic SAR
    public final static int BOLLINGER= 40001;//Bollinger
    public final static int ENVELOPE= 40002;//Envelope
    public final static int GLANCE_BALANCE= 40003;//일목균형도
    public final static int RAINBOW= 40004;//Rainbow
    public final static int PIVOT= 40005;//Pivot
    public final static int PAVERAGE= 40006;//주가 이동평균
    public final static int VAVERAGE= 40007;//거래량 이동평균
    public final static int ZIGZAG= 40008;//지그재그차트
    //2014.01.11 by LYH >> Price Channel 지표 추가 <<
    public final static int PRICE_CHANNEL= 40009;//Envelope
    public final static int DEMARK= 40010;// Demark
    public final static int PIVOTPRE = 40011;// Pivot 전봉기준
    public final static int DEMA = 40012;// DEMA
    public final static int TEMA = 40013;// TEMA
    public final static int PRICEBOX = 40014;// 가격 & Box
    public final static int STARC_BANDS= 40015;//STARC_BANDS

    public static int[] col_set={
            Color.BLACK,
            Color.rgb(165,42,0),
            Color.rgb(0,64,64),
            Color.rgb(0,85,0),
            Color.rgb(0,0,94),
            Color.rgb(0,0,139),
            Color.rgb(75,0,130),
            Color.rgb(40,40,40),//0-7

            Color.rgb(139,0,0),
            Color.rgb(255,104,32),
            Color.rgb(139,139,0),
            Color.rgb(0,147,0),
            Color.rgb(56,142,142),
            Color.rgb(0,0,255),
            Color.rgb(123,123,192),
            Color.rgb(102,102,102),//8-15

            Color.RED,
            Color.rgb(255,173,91),
            Color.rgb(50,205,50),
            Color.rgb(60,179,113),
            Color.rgb(127,255,212),
            Color.rgb(125,158,192),
            Color.rgb(128,0,128),
            Color.rgb(127,127,127),//16-23

            Color.rgb(255,192,203),
            Color.rgb(255,215,0),
            Color.YELLOW,
            Color.GREEN,
            Color.rgb(64,224,208),
            Color.rgb(192,255,255),
            Color.rgb(72,0,72),
            Color.rgb(192,192,192),//24-31

            Color.rgb(255,228,225),
            Color.rgb(210,180,140),
            Color.rgb(255,255,224),
            Color.rgb(152,251,152),
            Color.rgb(175,238,238),
            Color.rgb(104,131,139),
            Color.rgb(230,230,250),
            Color.WHITE
    };

    public static void initPacketFieldFormat(){
        if(field_format == null)field_format = new Hashtable<String, String>(21);
        for(int i=0;i<packet_field_format.length;i++){
            field_format.put(packet_field_format[i],""+i);
        }
    }
    public static int getPacketFormatIndex(String key){
        if(field_format==null) initPacketFieldFormat();
        String s=(String)field_format.get(key);
        int val=9999;
        if(s!=null)val=Integer.parseInt(s);
        return val;
    }

    /*
    public static String[] packet_field_format={
        "YYYYMMDD","YYMMDD","YYYYMM","YYMM","MMDD",//0-4
        "DDHH","DDHHMM","DDHHMMSS","HHMMSS","HHMMSSNN",//5-9
        "문자","× 1","× 1000","× 0.1","× 0.01",//10-14
        "× 0.001","× 0.0001","%"//15-17
    };
    */
    public static String getFormatedData(String data, String type){
        return getFormatedData(data,getPacketFormatIndex(type));
    }
    public static String getFormatedData(double data, String type){
        return getFormatedData(data,getPacketFormatIndex(type));
    }
    public static boolean isLetter(String data){
        for(int i=0; i<data.length(); i++){
            if(Character.isLetter(data.charAt(i)))
                return true;
        }
        return false;
    }

    public static String getFormatedData(double data, int type){
        //2012.11.27 by LYH >> 진법 및 승수 처리.
        int nTradeMulti = -1;
        if(type >1000)
        {
            nTradeMulti = type%1000;
            type = type/1000;
        }
        //2012.11.27 by LYH <<
        switch(type){
            case 11: //거래량
            case 12:
                return COMUtil.format(data,0,3);
            case 13:
                return COMUtil.format(data,1,3);
            case 14:
                return COMUtil.format(data,2,3);
            case 15:
                return COMUtil.format(data,3,3);
            case 16:
                //2012.11.27 by LYH >> 진법 및 승수 처리.
                if(nTradeMulti>=8)
                    return ChartUtil.FormatCurrency_Notation(data,nTradeMulti);
                else if(nTradeMulti>=0)
                    return COMUtil.format(data,nTradeMulti,3);
                else
                    //2012.11.27 by LYH <<
                    return COMUtil.format(data,4,3);
            case 17:
                StringBuffer buf= new StringBuffer();
                buf.append(COMUtil.format(data,0,3));
                buf.append("%");
                return buf.toString();
            case 18:
                return COMUtil.format(data,0,3);
        }
        return ""+data;
    }
    public static String getFormatedData(String date, int type){
        //2012.11.27 by LYH >> 진법 및 승수 처리.
        int nTradeMulti = -1;
        if(type >1000)
        {
            nTradeMulti = type%1000;
            type = type/1000;
        }
        //2012.11.27 by LYH <<
        if(isLetter(date))return date;
        if(type<10){
//            if(date.startsWith("0000"))return "장전";
//            else
            if(date.startsWith("7777")||date.startsWith("8888"))return "장마감";
            else if(date.startsWith("9999")) return "시간외";
        }

        StringBuffer buf= new StringBuffer();
        //if(buf.length()>0) buf.delete(0, buf.length());
        switch(type){
            case 0:
                buf.append(new String(date.substring(0,4)));
                buf.append(". ");
                buf.append(new String(COMUtil.makeTimeForKakaoRule(date.substring(4,6))));
                if(date.length()>6){
                    buf.append(". ");
                    buf.append(new String(COMUtil.makeTimeForKakaoRule(date.substring(6,8))));
                }
                buf.append("."); // 21.05.24 kakaopay
                break;
            case 1:
                buf.append(new String(COMUtil.makeTimeForKakaoRule(date.substring(0,2))));
                buf.append(". ");
                buf.append(new String(COMUtil.makeTimeForKakaoRule(date.substring(2,4))));
                buf.append(". ");
                buf.append(new String(COMUtil.makeTimeForKakaoRule(date.substring(4,6))));
                break;
            case 2:
                buf.append(new String(COMUtil.makeTimeForKakaoRule(date.substring(0,4))));
                if(date.length()>5) {
                    buf.append(". ");
                    buf.append(new String(COMUtil.makeTimeForKakaoRule(date.substring(4, 6))));
                }
                buf.append("."); // 21.05.24 kakaopay
                break;
            case 3:
            case 4:
            case 5:
                buf.append(new String(COMUtil.makeTimeForKakaoRule(date.substring(0,2))));
                buf.append(".");
                buf.append(new String(COMUtil.makeTimeForKakaoRule(date.substring(2,4))));
                break;
            case 6:
                buf.append(new String(date.substring(0,2)));
                buf.append(". ");

                buf.append(new String(date.substring(2,4)));
                buf.append(":");
                buf.append(new String(date.substring(4,6)));
                break;
            case 7:
                buf.append(new String(COMUtil.makeTimeForKakaoRule(date.substring(0,2))));
                buf.append(". ");
                buf.append(new String(date.substring(2,4)));
                buf.append(":");
                buf.append(new String(date.substring(4,6)));
                if(date.length()>6){
                    buf.append(":");
                    buf.append(new String(date.substring(6,8)));
                }

//                buf.append(new String(COMUtil.makeTimeForKakaoRule(date.substring(0,2))));
//                buf.append(". ");
//                buf.append(new String(COMUtil.makeTimeForKakaoRule(date.substring(2,4))));
//                buf.append(". ");
//                buf.append(new String(date.substring(4,6)));
//                buf.append(":");
//                buf.append(new String(date.substring(6,8)));
                break;
            case 8:
            case 9:
                buf.append(new String(date.substring(0,2)));
                buf.append(":");
                buf.append(new String(date.substring(2,4)));
                if(date.length()>4){
                    buf.append(":");
                    buf.append(new String(date.substring(4,6)));
                }
                break;
            /*
            "문자","× 1","× 1000","× 0.1","× 0.01",//10-14
        "× 0.001","× 0.0001","%"//15-17
        */
            case 10:
                buf.append(date);
                break;
            case 11: //거래량
                return COMUtil.format(date,0,3);
            //break;
            case 12:
                return COMUtil.format(date,0,3);
            //break;
            case 13:
                return COMUtil.format(date,1,3);
            //break;
            case 14:
                return COMUtil.format(date,2,3);
            //break;
            case 15:
                return COMUtil.format(date,3,3);
            //break;
            case 16:
                //2012.11.27 by LYH >> 진법 및 승수 처리.
                if(nTradeMulti>=8)
                {
                    if(date==null ||date.length()<=0) return "";
                    double dData = 0;
                    try {
                        dData = Double.parseDouble(date);
                    } catch(Exception e) {
                        dData = 0;
                    }
                    return ChartUtil.FormatCurrency_Notation(dData,nTradeMulti);
                }
                else if(nTradeMulti>=0)
                    return COMUtil.format(date,nTradeMulti,3);
                else
                    //2012.11.27 by LYH <<
                    return COMUtil.format(date,4,3);
                //break;
            case 17:
                buf.append(COMUtil.format(date,0,3));
                buf.append("%");
                break;
            case 18:
                return COMUtil.format(date,0,3);
            //break;
            case 19:
                buf.append(new String(date.substring(0,2)));
                buf.append(":");
                buf.append(new String(date.substring(2,4)));
                break;
            case 20://월(2)일(2)시간(2)분(2) 20030506 ykLee add.
                buf.append(new String(COMUtil.makeTimeForKakaoRule(date.substring(0,2))));
                buf.append(". ");
                buf.append(new String(COMUtil.makeTimeForKakaoRule(date.substring(2,4))));
                buf.append(". ");
                buf.append(new String(date.substring(4,6)));
                buf.append(":");
                buf.append(new String(date.substring(6,8)));
                break;
            case 21://년(4) 20030514 ykLee add.
                buf.append(new String(date.substring(0,4)));
                buf.append("년");
                break;
        }
        return buf.toString();
    }

    //2019. 06. 27 by hyh - 멀티차트 진법처리 >>
    public static String getFormatedData(double data, int type, ChartDataModel _cdm) {
        int nTradeMulti = -1;
        int nType = type;

        if (type > 1000) {
            nTradeMulti = type % 1000;
            nType = type / 1000;
        }

        if (nType == 16 && nTradeMulti >= 8) {
//            return ChartUtil.FormatCurrency_Notation(data, _cdm.nDispScale, _cdm.nTradeMulti, _cdm.nLogDisp);
            return ChartUtil.FormatCurrency_Notation(data, _cdm.nDispScale);
        }
        else {
            return getFormatedData(data, type);
        }
    }

    public static String getFormatedData(String date, int type, ChartDataModel _cdm) {
        int nTradeMulti = -1;
        int nType = type;

        if (type > 1000) {
            nTradeMulti = type % 1000;
            nType = type / 1000;
        }

        if (nType == 16 && nTradeMulti >= 8) {
            if (date == null || date.length() <= 0) return "";
            double dData = 0;
            try {
                dData = Double.parseDouble(date);
            } catch (Exception e) {
                dData = 0;
            }
            //return ChartUtil.FormatCurrency_Notation(dData, _cdm.nDispScale, _cdm.nTradeMulti, _cdm.nLogDisp);
            return ChartUtil.FormatCurrency_Notation(dData, _cdm.nDispScale);
        }
        else {
            return getFormatedData(date, type);
        }
    }

    public static String getFormatedData(double data, int type, int nNotation, int nPrecision, int nLogDisp){
        int nTradeMulti = -1;
        int nType = type;

        if (type > 1000) {
            nTradeMulti = type % 1000;
            nType = type / 1000;
        }

        if (nPrecision < 0 && nLogDisp < 0) {
            return getFormatedData(data, type);
        }

        if (nType == 16 && nTradeMulti >= 8) {
            //return ChartUtil.FormatCurrency_Notation(data, nNotation, nPrecision, nLogDisp);
            return ChartUtil.FormatCurrency_Notation(data, nNotation);
        }
        else {
            return getFormatedData(data, type);
        }
    }

    public static String getFormatedData(String date, int type, int nNotation, int nPrecision, int nLogDisp){
        int nTradeMulti = -1;
        int nType = type;

        if (type > 1000) {
            nTradeMulti = type % 1000;
            nType = type / 1000;
        }

        if (nPrecision < 0 && nLogDisp < 0) {
            return getFormatedData(date, type);
        }

        if (nType == 16 && nTradeMulti >= 8) {
            if (date == null || date.length() <= 0) return "";
            double dData = 0;
            try {
                dData = Double.parseDouble(date);
            } catch (Exception e) {
                dData = 0;
            }
            //return ChartUtil.FormatCurrency_Notation(dData, nNotation, nPrecision, nLogDisp);
            return ChartUtil.FormatCurrency_Notation(dData, nNotation);
        }
        else {
            return getFormatedData(date, type);
        }
    }
    //2019. 06. 27 by hyh - 멀티차트 진법처리 <<

//    public static int getAngle(Point s, Point e){
//        double c = (double)(e.x-s.x);
//        double b = Math.sqrt(Math.pow(c,2)+Math.pow((double)(e.y-s.y),2));
//        int arc = (int)((Math.acos(c/b))*180/Math.PI);
//        return arc;
//    }

    public static double getAngle(PointF start, PointF end) { // 각도추세선에서 쓰이는 각도값
        // 계산 함수 16.07.29
        double dy = end.y - start.y;
        double dx = end.x - start.x;
        double angle = 0.0;
        double dAngle = 0.0;
        if (end.y == start.y) {
            if (end.x < start.x) {
                dAngle = -180;
            }
            else
                dAngle = 0;
        } else {
            if (end.x == start.x) {
                angle = 0.0;
            } else {
                angle = Math.abs(dy / dx);
            }
            dAngle = Math.atan(angle) * (180.0 / Math.PI);
            if (end.x != start.x && Math.abs(end.y - start.y) <= 2 && dAngle < 0.18 && angle < 0.003) {
                dAngle = -180.0f;
            }else if(end.y < start.y && Math.abs(end.x -start.x)<=2 && dAngle < 0.18 && angle < 0.003){
                dAngle = -90.0f;
            }else if(end.y > start.y && Math.abs(end.x -start.x)<=2 && dAngle < 0.18 && angle < 0.003){
                dAngle = -270.0f;
            }
            else {
                if (end.y < start.y && end.x <= start.x) {
                    dAngle = (-1) * (180.0f - dAngle);
                } else if (end.y > start.y && end.x <= start.x) {
                    dAngle = (-1) * (180.0f + dAngle);
                } else if (end.y > start.y && end.x > start.x) {
                    dAngle = (-1) * (360.0f - dAngle);
                } else {
                    dAngle = (-1) * dAngle;
                }

            }

        }
        return dAngle;
    }

    public static void initBojoMsg(){
        if(b_msg == null)b_msg = new Hashtable<String, String>(10);
        for(int i=0;i<bojo_msg.length;i++){
            b_msg.put(bojo_msg[i],""+i);
        }
    }
    public static int getBojoMsgIndex(String key){
        if(b_msg ==null)initBojoMsg();
        String s=(String)b_msg.get(key);
        int val=9999;
        if(s!=null)val=Integer.parseInt(s);
        return val;
    }
    //===============================
    //0: 일반적 그래프 --> 블럭생성
    //1: 가격차트 변경형 --> 가격차트가 있는 블럭을 찾아 드로우 툴을 변경
    //                       가격차트가 없는 경우에는 새로운 블럭을 생성하고 가격차트를 만든다
    //2: 가격차트 삽입형 --> 가격차트를 찾아 그 블럭에 그래프를 추가한다
    //                       가격차트가 없는 경우에는 새로운 블럭을 생성하여 추가한다
    //3: 독립그래프 --> 차트 전체 사이즈의 블럭을 생성한다.
    //===============================
    public static int getGraphTypeIndex(int type){
        int index =0;
        switch(type){
            case 0://가격차트
            case 37://봉차트
            case 38://라인차트
            case 39://바차트
                index=1;
                break;
            case 33://대기매물
            case 34://삼선전환
            case 35://역시계
            case 36://P&F
                index=3;
                break;
            case 1://가격이동평균
            case 5://일목균형도
            case 8://BollingerBand
            case 12://Envelope
            case 23://parabolic
            case 24://pivot
            case 41:
                index=2;
                break;
            case 3://거래량 이동평균
                index=4;
                break;
            case 9999:
                index=9999;
                break;

        }
        return index;

    }
    //누적 거래량 필드 이름
    public static String[] all_vol={
            "volume","fuvolall","opvolall"
    };
    //===============================
    // 해당 필드가 누적거래량이면 1로 리턴
    //===============================
    public static int isNujukVolume(String field){
        for(int i=0;i<all_vol.length;i++){
            if(field.equals(all_vol[i]))return 1;
        }
        return 0;
    }

    //2012.11.27 by LYH >> 진법 및 승수 처리.
    /**
     * @breif 진법 변환
     * @param double dValue
     * @param int nNotation
     * @return None
     * @auther Berdo(JaeWoong-Seok[EMail:berdo_seok@naver.com]
     * @date 2012-11-15
     */
    public static String FormatCurrency_Notation(double dValue, int nNotation)
    {
        String strRet = "";
        dValue += 0.000000001;

        boolean bMinus = false;
        if (dValue < 0)
        {
            bMinus = true;
            dValue *= -1;
        }
        if (nNotation == 8)
        {
            double dFractional, dInt;

            dInt = Math.floor(dValue);
            dFractional = dValue - dInt;

            double dFractionalInt;
            dFractionalInt = Math.floor(dFractional/0.125);

            strRet = String.format("%d'%d", (int)dInt, (int)dFractionalInt);
        }
        else if (nNotation == 32)
        {
            double dFractional, dInt;

            dInt = Math.floor(dValue);
            dFractional = dValue - dInt;

            double dFractionalInt;
            //dFractionalInt = Math.floor(dFractional/0.03125);
            dFractionalInt = Math.floor(dFractional*32);

            strRet = String.format("%d'%02d", (int)dInt, (int)dFractionalInt);
        }
        else if (nNotation == 132)
        {
            double dFractional, dInt;

            dInt = Math.floor(dValue);
            dFractional = dValue - dInt;

            double dFractionalInt;
            //dFractionalInt = Math.floor(dFractional/0.003125);
            dFractionalInt = Math.floor(dFractional*320);

            double dFractionalInt2;
            dFractionalInt2 = ((int)Math.floor(dFractionalInt))%10;

            strRet = String.format("%d'%02d.%d", (int)dInt, (int)(dFractionalInt/10), (int)dFractionalInt2);
        }
        else if (nNotation == 232)
        {
            double dFractional, dInt;

            dInt = Math.floor(dValue);
            dFractional = dValue - dInt;

            double dFractionalInt;
            //dFractionalInt = Math.floor(dFractional/0.0003125);
            dFractionalInt = Math.floor(dFractional*3200);

            double dFractionalInt2;
            dFractionalInt2 = ((int)Math.floor(dFractionalInt))%100;

            strRet = String.format("%d'%02d.%02d", (int)dInt, (int)(dFractionalInt/100), (int)dFractionalInt2);
        }
        //2019.01.04 sdm >> 256진법 추가 Start
        else if (nNotation == 332)
        {
            double dFractional, dInt;

            dInt = Math.floor(dValue);
            dFractional = dValue - dInt;

            double dFractionalInt;
            dFractionalInt = Math.floor(dFractional*32000);

            double dFractionalInt2;
            dFractionalInt2 = ((int)Math.floor(dFractionalInt))%1000;

            strRet = String.format("%d'%02d.%03d", (int)dInt, (int)(dFractionalInt/1000), (int)dFractionalInt2);
        }
        //2019.01.04 sdm >> 256진법 추가 End
        else if (nNotation == 64)
        {
//            double dFractional, dInt;
//            dInt = Math.floor(dValue);
//            dFractional = dValue - dInt;
//
//            double dFractionalInt;
//            //dFractionalInt = Math.floor(dFractional/0.015625 * 5.0);
//            dFractionalInt = Math.floor(dFractional*64);
//
//            String strFractional = "";
//            strFractional = String.format("%02.0f", dFractionalInt);
//            strRet = String.format("%d'%s", (int)dInt, strFractional);

            double dFractional, dInt;
            dInt = Math.floor(dValue);
            dFractional = dValue - dInt;

            double dFractionalInt;
            dFractionalInt = Math.floor(dFractional/0.015625 * 5.0);

            String strFractional = "";
            strFractional = String.format("%03.0f", dFractionalInt);
            strRet = String.format("%d'%s.%s", (int)dInt, strFractional.substring(0, 2), strFractional.substring(2, 3));
        }
        else if (nNotation == 164)
        {
            double dFractional, dInt;
            dInt = Math.floor(dValue);
            dFractional = dValue - dInt;

            double dFractionalInt;
            //dFractionalInt = Math.floor(dFractional/0.015625 * 5.0);
            dFractionalInt = Math.floor(dFractional*640);

            String strFractional = "";
            strFractional = String.format("%03.0f", dFractionalInt);
            strRet = String.format("%d'%s.%s", (int)dInt, strFractional.substring(0, 2), strFractional.substring(2, 3));
        }
        else if (nNotation == 128)
        {
            //2016.12.7 by lyk - 128진법 소수점 2째자리로 나오도록 수정
            double dFractional, dInt;
            dInt = Math.floor(dValue);
            dFractional = dValue - dInt;

            double dFractionalInt;
            dFractionalInt = Math.floor(dFractional/0.0078125 * 25);

            String strFractional = "";
            strFractional = String.format("%04.0f", dFractionalInt);
            strRet = String.format("%d'%s.%s", (int)dInt, strFractional.substring(0, 2), strFractional.substring(2, 4));
            //2016.12.7 by lyk - 128진법 소수점 2째자리로 나오도록 수정 end

//            double dFractional, dInt;
//            dInt = Math.floor(dValue);
//            dFractional = dValue - dInt;
//
//            double dFractionalInt;
//            dFractionalInt = Math.floor(dFractional/0.0078125 * 2.5);
//
//            String strFractional = "";
//            strFractional = String.format("%03.0f", dFractionalInt);
//            //	Log.d("MaskInfo", String.format("strF = %s", strFractional));
//            strRet = String.format("%d'%s.%s", (int)dInt, strFractional.substring(0, 2), strFractional.substring(2, 3));
        }
        //2019.01.04 sdm >> 256진법 추가 Start
        else if (nNotation == 256)
        {
            double dFractional, dInt;

            dInt = Math.floor(dValue);
            dFractional = dValue - dInt;

            double dFractionalInt;
            dFractionalInt = Math.floor(dFractional*32000);

            double dFractionalInt2;
            dFractionalInt2 = ((int)Math.floor(dFractionalInt))%1000;

            strRet = String.format("%d'%02d.%03d", (int)dInt, (int)(dFractionalInt/1000), (int)dFractionalInt2);
        }
        //2019.01.04 sdm >> 256진법 추가 End

        if (bMinus)
        {
            strRet = String.format("-%s", strRet);
        }

        return strRet;
    }
    //2012.11.27 by LYH <<

//    /**
//     * @param double dValue
//     * @param int    nNotation
//     * @param int    nPrecision
//     * @param int    nLogDisp
//     * @return String strFormatedCurrency
//     * @breif 진법 변환 (삼성선물)
//     * @auther Ha Younghoon
//     * @date 2019-05-13
//     */
//    public static String FormatCurrency_Notation(double dValue, int nNotation, int nPrecision, int nLogDisp) {
//        if (nPrecision < 0 || nLogDisp < 0) {
//            return FormatCurrency_Notation(dValue, nNotation);
//        }
//        else if (nNotation == 8) {
//            double dFractional, dInt;
//
//            dInt = Math.floor(dValue);
//            dFractional = dValue - dInt;
//
//            double dFractionalInt;
//            dFractionalInt = Math.floor(dFractional / 0.125);
//
//            return String.format("%d'%d", (int) dInt, (int) dFractionalInt);
//        }
//        else if (nNotation == 32) {
//            double dFractional, dInt;
//
//            dInt = Math.floor(dValue);
//            dFractional = dValue - dInt;
//
//            double dFractionalInt;
//            dFractionalInt = Math.floor(dFractional / 0.003125);
//
//            return String.format("%d'%02d.%d", (int) dInt, (int) dFractionalInt / 10, (int) dFractionalInt % 10);
//        }
//
//        String strRet = "";
//
//        boolean bMinus = false;
//        if (dValue < 0) {
//            bMinus = true;
//            dValue *= -1;
//        }
//
//        if (nNotation >= 8) {
//            String strValue = String.valueOf(dValue);
//
//            String strInteger = "0";
//            String strFractional1 = "0";
//            String strFractional2 = "0";
//
//            int nDotLocation = strValue.indexOf(".");
//
//            if (nDotLocation > 0) {
//                //2019. 06. 14 by hyh - 잘못된 값이 온 경우 죽는 에러 예외처리 >>
//                int nBeginIndex = nDotLocation + 1;
//                int nEndIndex = nBeginIndex + nPrecision;
//
//                if (strValue.length() >= nEndIndex) {
//                    strFractional2 = strValue.substring(nBeginIndex, nEndIndex);
//                }
//                else {
//                    strFractional2 = "0";
//                }
//                //2019. 06. 14 by hyh - 잘못된 값이 온 경우 죽는 에러 예외처리 <<
//
//                strInteger = strValue.substring(0, nDotLocation);
//
//                int nApostropheLocation = strInteger.length() - (nLogDisp - nPrecision);
//
//                if (nApostropheLocation > 0 && strInteger.length() >= nApostropheLocation) {
//                    strFractional1 = strInteger.substring(nApostropheLocation);
//                    strInteger = strInteger.substring(0, nApostropheLocation);
//                }
//                else {
//                    strFractional1 = strInteger;
//                    strInteger = "0";
//                }
//
//                int nFractional1 = 0;
//
//                try {
//                    nFractional1 = Integer.parseInt(strFractional1);
//                }
//                catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                int nInteger = 0;
//
//                try {
//                    nInteger = Integer.parseInt(strInteger);
//                }
//                catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                if (nLogDisp > 0 && nFractional1 >= nNotation) {
//                    int nQuotient = nFractional1 / nNotation;
//                    int nRemainder = nFractional1 % nNotation;
//
//                    strFractional1 = "" + nRemainder;
//                    strInteger = "" + (nInteger + nQuotient);
//                }
//
//                int nFrationanl1Length = strFractional1.length();
//                for (int nIndex = 0; nIndex < nLogDisp - nPrecision - nFrationanl1Length; nIndex++) {
//                    strFractional1 = "0" + strFractional1;
//                }
//            }
//
//            if (nPrecision == 0) {
//                strRet = strInteger + "'" + strFractional1;
//            }
//            else {
//                strRet = strInteger + "'" + strFractional1 + "." + strFractional2;
//            }
//        }
//
//        if (bMinus) {
//            strRet = "-" + strRet;
//        }
//
//        //System.out.println("Scaled dValue : " + dValue + " strRet : " + strRet + " nNotation : " + nNotation + " nLogDisp : " + nLogDisp + " nPrecision : " + nPrecision);
//
//        return strRet;
//    }

    //2020.11.23 by JJH >> 권리락/배당락 추가 start
    public static String getExRightString(int nRight) {
        switch (nRight)
        {
            case 1  : return "권리락";
            case 2  : return "배당락";
            case 3  : return "분배락";
            case 4  : return "권배락";
            case 5  : return "중간배당락";
            case 6  : return "권리중간배당락";
            case 7  : return "권중배락";
            case 9  : return "락";

            case 11  : return "회사분할";
            case 12  : return "자본감소";
            case 13  : return "장기간정지";
            case 14  : return "초과분배";
            case 15  : return "대규모배당";
            case 16  : return "회사분할합병";
            case 19  : return "기타";
            case 21  : return "액면분할";
            case 22  : return "액면병합";

            case 31  : return "유상증자";
            case 32  : return "무상증자";
            case 33  : return "유무상증자";
            case 39  : return "증자구분 기타";

            case 40 : return "액면분할";
            case 50 : return "액면병합";

            case 41 : return "액면분할+권리락";
            case 42 : return "액면분할+배당락";
            case 43 : return "액면분할+권배락";
            case 45 : return "액면분할+권리분기배당락";
            case 47 : return "액면분할+감자";

            case 51 : return "액면병합+권리락";
            case 52 : return "액면병합+배당락";
            case 53 : return "액면병합+권배락";
            case 55 : return "액면병합+권리분기배당락";
            case 57 : return "액면병합+감자";
            case 60 : return "중간(분기)배당락";
            case 70 : return "권리중간배당락";
        }
        return "";
    }
    //2020.11.23 by JJH >> 권리락/배당락 추가 end

    //2017.11.14 >> 타임존 설정 기능 추가
    /**
     * 서버시간인 서울 타임존을 기준으로 각 타임존에 맞는 시간으로 포맷 변경하여 출력함.
     * @param time	변경 대상 시간
     * @param format	시간 포맷
     * @param offset 	변경할 타임존 offset (분) - sso연동시 분 단위로 값을 넘겨받음 (TIMEZONE = +540)
     * @return
     * sample : convertStr = ChartUtil.getTimeWithTimezone(s.substring(2, 4), "HH", _cvm.m_strTimeZone);
     */

    public static String getTimeWithTimezone(String time, String format, String inTimeZone) {

        //타임존 테스트
        TimeZone inputTZ=TimeZone.getTimeZone(inTimeZone); //ex)"America/Chicago"
        Calendar cal = Calendar.getInstance(inputTZ);
        int tzOffsetMin = (cal.get(Calendar.ZONE_OFFSET) + cal.get(Calendar.DST_OFFSET))/(1000*60);
        String offset = String.valueOf(tzOffsetMin);

        //        String convertStr = getTimeWithTimezone(getHour(d)+getMin(d), "HHMM", String.valueOf(tzOffsetMin));
        //        Log.d("tzTime:", getHour(d)+getMin(d)+"----->"+convertStr);

        String result = "";


        try {


            if( !offset.equals("+540") ) {	// 서울 시간이 아닌경우에만 로직 수행

                int timezoneOffset = Integer.parseInt(offset.replace("+", ""));


                TimeZone seoulTZ=TimeZone.getTimeZone("Asia/Seoul");

                SimpleTimeZone outputTz = new SimpleTimeZone( timezoneOffset * 60 * 1000, "GMT"); // 출력하고자 하는 Timezone 으로 변환


                SimpleDateFormat inputFmt = new SimpleDateFormat(format + " Z");

                SimpleDateFormat outputFmt = new SimpleDateFormat(format);


                Calendar c = Calendar.getInstance(seoulTZ);

                c.setTime( inputFmt.parse( time + " +0900" ) );	// 서버시간인 서울시간 기준으로 입력


                outputFmt.setTimeZone(outputTz);

                result = outputFmt.format(c.getTime());

            }else{

                result = time;

            }


        } catch (Exception e) {

            result = time;

        }


        //	System.out.println("타임존 offset : "+offset);

        //	System.out.println("입력된 시간 : "+time);

        //	System.out.println("변환 시간 :"+ result);


        return result;

    }
    //2017.11.14 << 타임존 설정 기능 추가
}