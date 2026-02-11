package drfn.chart.net;

import java.io.*;
import java.net.*;
import java.util.*;

public class RealLoader {
    URL url;
    Hashtable<String, CfgFile> cfgFileH;
    CfgFile cfgEle;
    private int dataNameSize = 0;
    private String[] drdsInfo={"BEGIN_DATA_MAP",
            "S31,    우선호가/체결/기세/장운용/RECOVERY/전후장종가",
            "Key_Begin",
            "종목코드,                       code,         char,   6",
            "Key_End",
            "Data_Begin",
            "종목코드,                       code,         char,   6",
            "시간(HH:MM:SS),                 time,         char,   6",
            "등락부호,                       sign,         char,   1",
            "등락폭,                         change,       char,   9",
            "현재가,                         price,        char,   9",
            "등락률,                         chrate,       char,   5",
            "전장시가,                       openam,       char,   9",
            "고가,                           high,         char,   9",
            "저가,                           low,          char,   9",
            "매도호가,                       offer,        char,   9",
            "매수호가,                       bid,          char,   9",
            "누적거래량,                     volume,       char,  10",
            "변동거래량,                     movolume,     char,   8",
            "거래대금,                       value,        char,  12",
            "매매구분,                       maegubun,     char,   1",
            "장구분,                         janggubun,    char,   1",
            "CB구분,                         cbgubun,      char,   1",
            "STOP,                           stop,         char,   1",
            "시간외거래량,                   over_volume,  char,  10",
            "시간외거래대금,                 over_valume,  char,  12",
            "시간외대량매매,                 outrep_vol,   char,  10",
            "시간외대량매매대금,             outrep_val,   char,  12",
            "Data_End",
            "END_DATA_MAP",
            "BEGIN_DATA_MAP",
            "JS0,    Kospi/Kospi100/Kospi200/Kosdaq50",
            "Key_Begin",
            "업종코드,                       upcode,     char,   4",
            "Key_End",
            "Data_Begin",
            "업종코드,                       upcode,     char,   4",
            "장마감(JSAM00),                 time,       char,   6",
            "장마감(사용안함),               janggubun,  char,   1",
            "지수,                           jisu,       char,   7",
            "등락부호,                       sign,       char,   1",
            "전일비,                         change,     char,   7",
            "등락률,                         chrate,     char,   5",
            "거래량(천주),                   volume,     char,   8",
            "거래대금(백만원),               value,      char,   8",
            "일자,                           bdate,      char,   8",
            "시간,                           btime,      char,  12",
            "Data_End",
            "END_DATA_MAP",
            "BEGIN_DATA_MAP",
            "SC0,    선물 체결/장운영/기세",
            "Key_Begin",
            "종목코드,                       fuitem,       char,   8",
            "Key_End",
            "Data_Begin",
            "종목코드,                       fuitem,       char,   8",
            "종목SEQ,                        futitemseq,   char,   2",
            "시간HH:MM:SS,                   futime,       char,   6",
            "현재가[9(3)v99],                fucurr,       char,   6",
            "시가[9(3)v99],                  fuopen,       char,   6",
            "고가[9(3)v99],                  fuhigh,       char,   6",
            "저가[9(3)v99],                  fulow,        char,   6",
            "누적체결수량(천원->백만원),     fuvolall,     char,   7",
            "누적거래대금,                   fuvalall,     char,  11",
            "전일대비부호,                   fusign,       char,   1",
            "전일대비[9(3)v99],              fuchange,     char,   5",
            "등락률,                         fuchrate,     char,   5",
            "체결수량,                       fuvol,        char,   6",
            "매도우선호가[9(3)v9(2)],        offer,        char,   6",
            "매수우선호가[9(3)v9(2)],        bid,          char,   6",
            "매도잔량,                       offerjan,     char,   6",
            "매수잔량,                       bidjan,       char,   6",
            "차선매도호가[9(3)v9(2)],        joffer,       char,   6",
            "차선매수호가[9(3)v9(2)],        jbid,         char,   6",
            "차선매도호가잔량,               jofferjan,    char,   6",
            "차선매수호가잔량,               jbidjan,      char,   6",
            "차차선매도호가9(3)v9(2),        jjoffer,      char,   6",
            "차차선매수호가9(3)v9(2),        jjbid,        char,   6",
            "차차선매도호가잔량,             jjofferjan,   char,   6",
            "차차선매수호가잔량,             jjbidjan,     char,   6",
            "총매도호가잔량,                 tofferjan,    char,   6",
            "총매수호가잔량,                 tobidjan,     char,   6",
            "4차선매도호가,                  j4offer,      char,   6",
            "4차선매수호가,                  j4bid,        char,   6",
            "4차선매도잔량,                  j4offerjan,   char,   6",
            "4차선매수잔량,                  j4bidjan,     char,   6",
            "5차선매도호가,                  j5offer,      char,   6",
            "5차선매수호가,                  j5bid,        char,   6",
            "5차선매도잔량,                  j5offerjan,   char,   6",
            "5차선매수잔량,                  j5bidjan,     char,   6",

            "우선매도건수,                   offersu,      char,   4",
            "차선매도건수,                   joffersu,     char,   4",
            "차차선매도건수,                 jjoffersu,    char,   4",
            "4차선매도건수,                  j4offersu,    char,   4",
            "5차선매도건수,                  j5offersu,    char,   4",
            "총매도건수,                     toffersu,     char,   5",

            "우선매수건수,                   bidsu,        char,   4",
            "차선매수건수,                   jbidsu,       char,   4",
            "차차선매수건수,                 jjbidsu,      char,   4",
            "4차선매수건수,                  j4bidsu,      char,   4",
            "5차선매수건수,                  j5bidsu,      char,   4",
            "총 매수 건수,                   tbidsu,       char,   5",

            "이론가,                         theory,       char,   5",
            "Data_End",
            "END_DATA_MAP",
            "BEGIN_DATA_MAP",
            "OC0,    옵션 체결/장운용/기세",
            "Key_Begin",
            "종목코드,                       opitem,       char,   8",
            "Key_End",
            "Data_Begin",
            "종목코드,                       opitem,       char,   8",
            "총목SEQ,                        opitemseq,    char,   3",
            "체결수량,                       opvol,        char,   6",
            "시간HH:MM:SS,                   optime,       char,   6",
            "매도우선호가[9(3)v9(2)],        offer,        char,   5",
            "매수우선호가[9(3)v9(2)],        bid,          char,   5",
            "매도잔량,                       offerjan,     char,   6",
            "매수잔량,                       bidjan,       char,   6",
            "차선매도호가[9(3)v9(2)],        joffer,       char,   5",
            "차선매수호가[9(3)v9(2)],        jbid,         char,   5",
            "차선매도호가잔량,               jofferjan,    char,   6",
            "차선매수호가잔량,               jbidjan,      char,   6",
            "차차선매도호가[9(3)v9(2)],      jjoffer,      char,   5",
            "차차선매수호가[9(3)v9(2)],      jjbid,        char,   5",
            "차차선매도호가잔량,             jjofferjan,   char,   6",
            "차차선매수호가잔량,             jjbidjan,     char,   6",
            "총매도호가잔량,                 tofferjan,    char,   6",
            "총매수호가잔량,                 tobidjan,     char,   6",
            "현재가[9(3)v99],                opcurr,       char,   5",
            "시가[9(3)v99],                  opopen,       char,   5",
            "고가[9(3)v99],                  ophigh,       char,   5",
            "저가[9(3)v99],                  oplow,        char,   5",
            "누적 체결수량,                  opvolall,     char,   7",
            "누적거래대금,                   opvalall,     char,  11",
            "전일대비부호,                   opsign,       char,   1",
            "전일대비,                       opchange,     char,   5",
            "등락률,                         opchrate,     char,   5",
            "4차선매도호가,                  j4offer,      char,   5",
            "4차선매수호가,                  j4bid,        char,   5",
            "4차선매도잔량,                  j4offerjan,   char,   6",
            "4차선매수잔량,                  j4bidjan,     char,   6",
            "5차선매도호가,                  j5offer,      char,   5",
            "5차선매수호가,                  j5bid,        char,   5",
            "5차선매도잔량,                  j5offerjan,   char,   6",
            "5차선매수잔량,                  j5bidjan,     char,   6",
            "우선매도건수,                   offersu,      char,   4",
            "차선매도건수,                   joffersu,     char,   4",
            "차차선매도건수,                 jjoffersu,    char,   4",
            "4차선매도건수,                  j4offersu,    char,   4",
            "5차선매도건수,                  j5offersu,    char,   4",
            "총매도건수,                     toffersu,     char,   5",
            "우선매수건수,                   bidsu,        char,   4",
            "차선매수건수,                   jbidsu,       char,   4",
            "차차선매수건수,                 jjbidsu,      char,   4",
            "4차선매수건수,                  j4bidsu,      char,   4",
            "5차선매수건수,                  j5bidsu,      char,   4",
            "총매수건수,                     tbidsu,       char,   5",
            "이론가,                         theory,       char,   5",
            "내재변동성,                     impv,         char,   5",
            "델타,                           delta,        char,   7",
            "감마,                           gmma,         char,   7",
            "베가,                           vega,         char,   7",
            "세타,                           theta,        char,   7",
            "로우,                           rho,          char,   7",
            "Temp,                           fil,          char,   1",
            "Data_End",
            "END_DATA_MAP",
    };

    public RealLoader() {
    }

    /** ( Hashtable )  cfgFileH     
     *     key  :  (String) mapName
     *     value  :  ( CfgFile ) cfgEle
     */
    public Hashtable<String, CfgFile> getCfgFileH() {
        loadCode();
        return cfgFileH;
    }

    public String getMap(String dN, String dK) {
        CfgFile cfg = (CfgFile) cfgFileH.get(dN);
        String temp = (String)cfg.realField.get(dK);
        return temp;
    }

    public void loadCode() {
        cfgFileH = new Hashtable<String, CfgFile>();

        try {
            //String[] datas = COMUtil.convertArray(drdsInfo,"\r\n",15);
            for(int i=0; i<drdsInfo.length;i++){
                parseData(drdsInfo[i]);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    int oneSize, totSize;
    String realName;
    int statusPS = 0;
    void parseData(String type) {
        if(type == null) return;

        type = type.trim();
        if(type.equals("Data_End")) statusPS = 4;

        switch(statusPS) {
            case 1 :  // BEGIN_DATA_MAP
                realName = firstTerm(type);
                cfgEle = new CfgFile(realName);
                statusPS = 0;
                dataNameSize++;
                break;
            case 2 :  // Key_Begin                        
                cfgEle.setRealKeyLen(Integer.parseInt(lastTerm(type)));
                statusPS = 0;
                break;
            case 3 :  // Data_Begin   
                if (!type.equals("")) {
                    oneSize = Integer.parseInt(lastTerm(type));
                    cfgEle.setField(secondTerm(type), totSize+"/"+oneSize);
                    totSize = oneSize + totSize;
                }
                break;
            case 4 :  // Data_End
                cfgEle.setField("*", "0/"+totSize);

                cfgFileH.put(realName, cfgEle);
                cfgEle = null;
                totSize = 0;
                oneSize = 0;
                statusPS = 0;
                break;
            case 0 :
                break;
            default :
                break;
        }
        if(type.equals("BEGIN_DATA_MAP")) statusPS = 1;
        else if(type.equals("Key_Begin")) statusPS = 2;
        else if(type.equals("Data_Begin")) statusPS = 3;
    }

    String firstTerm(String inData) {
        int index = inData.indexOf(",");
        String res = new String(inData.substring(0, index));

        return res.trim();
    }

    String lastTerm(String inData) {
        int index = inData.lastIndexOf(",");
        String res = new String(inData.substring(index+1));

        return res.trim();
    }

    String secondTerm(String inData) {
        int index1 = inData.indexOf(",")+1;
        int index2 = inData.indexOf(",", index1);
        String res = new String(inData.substring(index1, index2));

        return res.trim();
    }

    byte[] t_buf=new byte[1024];
    public byte[] readLine(InputStream is) throws IOException {
        byte[] ret;

        int c = is.read();
        if (c == -1)  c=10;//return null;
        t_buf[0] = (byte)c;

        int i = 1, len=1;
        try {
            for (; i < 1024 ; i++) {
                c = is.read();
                if (c==0) c = ' '; // convert null to space.
                if(c==13) continue;
                if(c==10) {
                    ret = new byte[len];
                    System.arraycopy(t_buf, 0, ret, 0, len);
                    return ret;
                }
                t_buf[i] = (byte) c;
                len++;
            }
        } catch (IOException ee) {
            return null;
        }
        return null;
    }

    public int getDataNameSize() {
        return dataNameSize;
    }
}
