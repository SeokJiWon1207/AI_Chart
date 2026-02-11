package drfn.chart.util;

public class OutputPacket {
    //private Vector<Hashtable<String, Integer>> packetInfo=null;
    //private Hashtable<String, String> item=null;
    public static final String COUNT = "count";
    public static final String SIZE = "size";
    public static final String NAME = "name";
    public static final String JANG = "jang";
    public static final String PRICE = "price";
    public static final String SIGN = "sign";
    public static final String CHANGE = "change";
    public static final String CHGRATE = "chgrate";
    public static final String VOLUME = "volume";
    public static final String PREVOLUME = "prevolume";
    public static final String OPEN = "open";
    public static final String HIGH = "high";
    public static final String LOW = "low";
    public static final String PREVOL = "prevol";
    public static final String NKEY = "nkey";
    public static final String S1386 = "S1386";
    public static final String S1339 = "S1339";
    public static final String S1522 = "S1522";
    public static final String HIGHEST = "highest";      //2013. 2. 12 상하한가바 추가
    public static final String LOWEST = "lowest";        //2013. 2. 12 상하한가바 추가
    public static final String GIJUN = "gijun";        //2013. 2. 12 상하한가바 추가
    public static final String CCHTSIZE = "Cchtsize";
    public static final String TMP = "tmp";
    public static final String BOJOLEN = "bojoLen";
    public static final String BOJOMSG = "bojomsg";
    public static final String HEAD = "head";
    public static final String CODE = "code";
    public static final String TIME = "time";
    public static final String MEDO = "medo";
    public static final String MESU = "mesu";
    public static final String CVOLUME = "cvolume";
    public static final String SEQ = "seq";
    public static final String BIDVOL = "bidvol";
    public static final String VALUE = "value";
    public static final String REALKEY = "realKey";
    public static final String UNIT = "unit";
    public static final String DATATYPENAME = "dataTypeName";
    public static final String DATA = "data";
    public static final String JANGGUBUN = "dataTypeName";
    public static final String JISU = "JISU";
    public static final String HIGHLIMIT = "HIGHLIMIT";
    public static final String LOWLIMIT = "LOWLIMIT";
    public static final String BOHAP = "BOHAP";
    public static final String BASIS = "BASIS";
    public static final String TYPE = "TYPE";

    public OutputPacket() {

    }

    public static String[][] packetInfo_stock = {
            {NAME, "40"},
            {JANG, "10"},
            {PRICE, "20"},
            {SIGN, "1"},
            {CHANGE, "20"},
            {CHGRATE, "6"},
            {VOLUME, "20"},
            {OPEN, "20"},
            {HIGH, "20"},
            {LOW, "20"},
            {PREVOL, "10"},
            {NKEY, "15"},
            {CCHTSIZE, "6"},
            {TMP, "5"},
            {BOJOLEN, "4"},
            {BOJOMSG, "252"}
    };

    public static String[][] packetInfo_future = {
            {NAME, "40"},
            {JANG, "10"},
            {PRICE, "20"},
            {SIGN, "1"},
            {CHANGE, "20"},
            {CHGRATE, "6"},
            {VOLUME, "20"},
            {OPEN, "20"},
            {HIGH, "20"},
            {LOW, "20"},
            {PREVOL, "10"},
            {NKEY, "15"},
            {CCHTSIZE, "6"},
            {TMP, "5"},
            {BOJOLEN, "4"},
            {BOJOMSG, "252"}
    };

    public static String[][] packetInfo_upjong = {
            {NAME, "20"},
            {PRICE, "10"},
            {SIGN, "1"},
            {CHANGE, "10"},
            {CHGRATE, "6"},
            {VOLUME, "10"},
            {OPEN, "10"},
            {HIGH, "10"},
            {LOW, "10"},
            {HIGHEST, "10"}, //2013. 2. 12 상하한가바 추가
            {LOWEST, "10"}, //2013. 2. 12 상하한가바 추가
            {GIJUN, "10"}, //2013. 2. 12 상하한가바 추가
            {PREVOL, "10"},
            {NKEY, "80"},
            {CCHTSIZE, "6"},
            {TMP, "5"},
            {BOJOLEN, "4"},
            {BOJOMSG, "252"}
    };

    public static String[][] packetInfo_compare_stock = {
            {NAME, "40"},
            {JANG, "10"},
            {PRICE, "20"},
            {SIGN, "1"},
            {CHANGE, "20"},
            {CHGRATE, "6"},
            {VOLUME, "20"},
            {OPEN, "20"},
            {HIGH, "20"},
            {LOW, "20"},
            {PREVOL, "10"},
            {NKEY, "15"},
            {CCHTSIZE, "6"},
            {TMP, "5"},
            {BOJOLEN, "4"},
            {BOJOMSG, "252"}
    };

    public static String[][] packetInfo_compare_future = {
            {NAME, "40"},
            {JANG, "10"},
            {PRICE, "20"},
            {SIGN, "1"},
            {CHANGE, "20"},
            {CHGRATE, "6"},
            {VOLUME, "20"},
            {OPEN, "20"},
            {HIGH, "20"},
            {LOW, "20"},
            {PREVOL, "10"},
            {NKEY, "15"},
            {CCHTSIZE, "6"},
            {TMP, "5"},
            {BOJOLEN, "4"},
            {BOJOMSG, "252"}
    };

    public static String[][] packetInfo_compare_upjong = {
            {NAME, "20"},
            {PRICE, "10"},
            {SIGN, "1"},
            {CHANGE, "10"},
            {CHGRATE, "6"},
            {VOLUME, "10"},
            {OPEN, "10"},
            {NKEY, "80"},
            {TMP, "5"},
            {BOJOLEN, "4"},
            {BOJOMSG, "252"}
    };

    public static String[][] packetInfo_real_stock_dongbu = {
            {COUNT, "4"},
            {SIZE, "4"},
            {HEAD, "3"},
            {CODE, "6"},
            {TIME, "6"},
            {SIGN, "1"},
            {CHANGE, "9"},
            {PRICE, "9"},
            {CHGRATE, "5"},
            {OPEN, "9"},
            {HIGH, "9"},
            {LOW, "9"},
            {MEDO, "9"},
            {MESU, "9"},
            {VOLUME, "12"},
            {CVOLUME, "8"},
    };

    public static String[][] packetInfo_real_stock = {
            {COUNT, "4"},
            {SIZE, "4"},
            {HEAD, "3"},
            {CODE, "32"},
            {SEQ, "2"},
            {TIME, "6"},
            {PRICE, "12"},
            {OPEN, "12"},
            {HIGH, "12"},
            {LOW, "12"},
            {VOLUME, "12"},
            {BIDVOL, "10"},
            {VALUE, "15"},
            {SIGN, "1"},
            {CHANGE, "12"},
            {CHGRATE, "10"},
            {CVOLUME, "12"},
    };

    public static String[][] packetInfo_real_future = {
            {COUNT, "4"},
            {SIZE, "4"},
            {HEAD, "3"},
            {CODE, "32"},
            {SEQ, "2"},
            {TIME, "6"},
            {PRICE, "12"},
            {OPEN, "12"},
            {HIGH, "12"},
            {LOW, "12"},
            {VOLUME, "12"},
            {BIDVOL, "10"},
            {VALUE, "15"},
            {SIGN, "1"},
            {CHANGE, "12"},
            {CHGRATE, "10"},
            {CVOLUME, "12"},
    };

    public static String[][] packetInfo_real_jisu = {
            {COUNT, "4"},
            {SIZE, "4"},
            {HEAD, "3"},
            {CODE, "12"},
            {SEQ, "2"},
            {TIME, "6"},
            {PRICE, "12"},
            {OPEN, "12"},
            {HIGH, "12"},
            {LOW, "12"},
            {VOLUME, "10"},
            {BIDVOL, "10"},
            {VALUE, "11"},
            {SIGN, "1"},
            {CHANGE, "12"},
            {CHGRATE, "10"},
            {CVOLUME, "10"},
    };

    public static String[][] packetInfo_real_future_dongbu = {
            {COUNT, "4"},
            {SIZE, "4"},
            {HEAD, "3"},
            {CODE, "8"},
            {SEQ, "2"},
            {TIME, "6"},
            {PRICE, "7"},
            {OPEN, "7"},
            {HIGH, "7"},
            {LOW, "7"},
            {VOLUME, "7"},
            {BIDVOL, "7"},
            {VALUE, "11"},
            {SIGN, "1"},
            {CHANGE, "7"},
            {CHGRATE, "7"},
            {CVOLUME, "6"},
    };

    public static String[][] packetInfo_real_upjong = {
            {COUNT, "4"},
            {SIZE, "4"},
            {HEAD, "3"},
            {CODE, "4"},
            {TIME, "6"},
            {TMP, "1"},
            {PRICE, "9"},
            {SIGN, "1"},
            {CHANGE, "9"},
            {VOLUME, "8"},
            {VALUE, "8"},
            {TMP, "8"},
            {TMP, "6"},
            {CVOLUME, "8"},
            {CHGRATE, "7"},
    };

    public static String[][] packetInfo_ticker_item = {
            {CODE, "4"},
            {TIME, "6"},
            {JANGGUBUN, "1"},
            {JISU, "9"},
            {SIGN, "1"},
            {CHANGE, "9"},
            {VALUE, "8"},
            {VOLUME, "8"},
            {CHGRATE, "7"},
            {HIGHLIMIT, "6"},
            {HIGH, "6"},
            {BOHAP, "6"},
            {LOW, "6"},
            {LOWLIMIT, "6"}
    };

    public static String[][] packetInfo_ticker_item_fut = {
            {CODE, "8"},
            {TIME, "6"},
            {PRICE, "7"},
            {SIGN, "1"},
            {CHANGE, "7"},
            {VALUE, "11"},
            {VOLUME, "7"},
            {CHGRATE, "7"},
            {BASIS, "7"},
    };

    public static String[][] packetInfo_ticker_item_for = {
            {CODE, "14"},
            {TIME, "6"},
            {PRICE, "8"},
            {CHANGE, "8"},
            {CHGRATE, "8"},
    };

    public static String[][] packetInfo_ticker = {
            {COUNT, "3"},
    };

    public static String[][] packetInfo_ticker_type = {
            {TYPE, "4"},
    };

    public static String[][] packetInfo_gwansim_type = {
            {TMP, "4"},
            {COUNT, "4"},
    };

    public static String[][] packetInfo_gwansim_item_type = {
            {TYPE, "2"},
            {CODE, "15"},
            {NAME, "30"},
            {PRICE, "9"},
            {CHANGE, "6"},
            {CHGRATE, "5"},
            {SIGN, "1"},
            {VOLUME, "12"},
            {BOJOMSG, "265"}
    };

    /* 패킷 헤더 정보를 구조체 형식으로 저장하여 사용한다. */
    public static String[][] getPacketInfo(String trCode) {
        //if(packetInfo==null) packetInfo=new Vector();
        //if(item==null) item=new Hashtable<String, String>();
        if (trCode.equals(COMUtil.TR_CHART_STOCK)) {
            return packetInfo_stock;
        }
        else if (trCode.equals(COMUtil.TR_CHART_FUTURE)) {
            return packetInfo_future;
        }
        else if (trCode.equals(COMUtil.TR_CHART_UPJONG)) {
            return packetInfo_upjong;
        }
        else if (trCode.equals(COMUtil.TR_COMPARE_STOCK)) {
            return packetInfo_compare_stock;
        }
        else if (trCode.equals(COMUtil.TR_COMPARE_FUTURE)) {
            return packetInfo_compare_future;
        }
        else if (trCode.equals(COMUtil.TR_COMPARE_UPJONG)) {
            return packetInfo_compare_upjong;
        }
        else if (trCode.equals("S31")) {
            if (COMUtil.apiMode == true)
                return packetInfo_real_stock;
            else
                return packetInfo_real_stock_dongbu;
        }
        else if (trCode.equals("SC0")) {//SC0
            if (COMUtil.apiMode == true)
                return packetInfo_real_future;
            else
                return packetInfo_real_future_dongbu;
        }
        else if (trCode.equals("SC3")) {//주요지수
            return packetInfo_real_jisu;
        }
        else if (trCode.equals(COMUtil.TICKER_ITEM_O)) {
            return packetInfo_ticker_item;
        }
        else if (trCode.equals(COMUtil.TICKER_ITEM_FUT_O)) {
            return packetInfo_ticker_item_fut;
        }
        else if (trCode.equals(COMUtil.TICKER_ITEM_FOR_O)) {
            return packetInfo_ticker_item_for;
        }
        else if (trCode.equals(COMUtil.TICKER_O)) {
            return packetInfo_ticker;
        }
        else if (trCode.equals(COMUtil.TICKER_TYPE_O)) {
            return packetInfo_ticker_type;
        }
        else if (trCode.equals(COMUtil.GWANSIM_O)) {
            return packetInfo_gwansim_type;
        }
        else if (trCode.equals(COMUtil.GWANSIM_ITEM_O)) {
            return packetInfo_gwansim_item_type;
        }

        return packetInfo_stock;
    }

}
