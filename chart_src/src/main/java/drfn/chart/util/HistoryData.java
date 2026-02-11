package drfn.chart.util;

//import java.util.Vector;
//
//import drfn.chart.comp.DataField;

public class HistoryData {
//    public static Vector<String[]> kospiData = new Vector<String[]>();
//    public static Vector futureData = new Vector();
//    public static Vector upjongData = new Vector();
//    public static Vector sOptionData = new Vector();
//    public static Vector gwansimData = new Vector();
//    public static String fCode = "";
//    public static boolean isGwansim = false;
//    public final static int KOSPI_DATA = 6;
//    public final static int FUTURE_DATA = 9;
//    public final static int UPJONG_DATA = 3;
//    public final static int SOPTION_DATA = 10;
//    public final static int GWANSIM_DATA = 0;
//    final static int DATA_SIZE = 20;
//    
//    int kospiDataSelect = -1;
//    int futureDataSelect = -1;
//    int upjongDataSelect = -1;
//    int sOptionDataSelect = -1;
//    int gwansimDataSelect = -1;
    
    public HistoryData() {
    }
//    
//    public void removeHistoryAll() {
//        kospiData.removeAllElements();
//        futureData.removeAllElements();
//        upjongData.removeAllElements();
//        sOptionData.removeAllElements();
//        gwansimData.removeAllElements();        
//        kospiData = new Vector<String[]>();
//        futureData = new Vector();
//        upjongData = new Vector();
//        sOptionData = new Vector();
//        gwansimData = new Vector();
//    }
//    
//    public void initCode(String sCode, String fCode, String oCode, String uCode, String soCode) {
//        /*
//        if (!sCode.equals("")) {            
//            String[] temp = new String[2];
//            String itemName = COMUtil.getItemNameByCode(sCode);
//            temp[0] = sCode;
//            if (itemName != null) temp[1] = itemName;
//            kospiData.addElement(temp);
//        }
//        if (!fCode.equals("")) {
//            this.fCode = fCode;
//        }
//        
//        if (!uCode.equals("")) {
//            String[] temp = new String[2];
//            String itemName = COMUtil.getItemNameByCode(uCode);
//            temp[0] = uCode;
//            if (itemName != null) temp[1] = itemName;
//            upjongData.addElement(temp);
//        } else {
//            String[] temp = new String[2];
//            temp[0] = "S01";
//            temp[1] = COMUtil.getItemNameByCode(temp[0]);
//            upjongData.addElement(temp);
//        }
//        */
//    }
//    
//    public void setHistoryData(String code , String name, int type) {     
//        String[] temp = {code.trim(), name.trim()};
//        int index = -1;
//        switch(type) {
//            case KOSPI_DATA :
//                if (kospiData.size() >= DATA_SIZE) kospiData.removeElementAt(kospiData.size() - 1);
//                if (!isGwansim) {
//                    index = checkData(code, type);
//                    if (index != -1) kospiData.removeElementAt(index);
//                    kospiDataSelect = 0;
//                    kospiData.insertElementAt(temp, 0);
//                } 
//                if (isGwansim && checkKospiData(code)) {
//                    kospiDataSelect = 0;
//                    kospiData.insertElementAt(temp, 0);
//                }
//                break;
//            case UPJONG_DATA :
//                if (upjongData.size() >= DATA_SIZE) upjongData.removeElementAt(upjongData.size() - 1);
//                index = checkData(code, type);
//                if (index != -1) upjongData.removeElementAt(index);
//                upjongDataSelect = 0;
//                upjongData.insertElementAt(temp, 0);
//                break;
//        }
//    }
//    
//    public void setGwansimData(DataField[][] gwansim) {
////        gwansimData.removeAllElements();
////        if (gwansim == null || gwansim.length == 0) return;
////        String[] temp;
////        for (int i = 0 ; i < gwansim.length ; i++) {
////            temp = new String[2];
////            temp[0] = gwansim[i][2].getValue();
////            temp[1] = gwansim[i][0].getValue();
////            gwansimData.addElement(temp);
////        }
//    }
//    
//    public void setIsGwansim(boolean b) {
//        isGwansim = b;
//    }
//    
//    public int getSelectIndex(int type) {
//        if (isGwansim) type = GWANSIM_DATA;
//        switch(type) {
//            case KOSPI_DATA :
//                return kospiDataSelect;
//            case UPJONG_DATA :
//                return upjongDataSelect;
//            case GWANSIM_DATA :
//                return gwansimDataSelect;
//            default :
//                return -1;
//        }
//    }
//    
//    public void setSelectIndex(int index, int type) {
//        if (isGwansim) type = GWANSIM_DATA;
//        switch(type) {
//            case KOSPI_DATA :
//                kospiDataSelect = index;
//                break;
//            case UPJONG_DATA :
//                upjongDataSelect = index;
//                break;
//            case GWANSIM_DATA :
//                gwansimDataSelect = index;
//                break;
//        }
//    }
//    
//    public String[] getHistoryCodeByIndex(int index, int type) {
//        if (isGwansim) type = GWANSIM_DATA;
//        else index--;
//        switch(type) {
//            case KOSPI_DATA :
//                if (kospiData.size() < 1) return null;
//                if (index > kospiData.size() - 1) kospiDataSelect = 0;
//                else if (index < 0) kospiDataSelect = kospiData.size() - 1;
//                else kospiDataSelect = index;
//                return (String[]) kospiData.elementAt(kospiDataSelect);
//            case UPJONG_DATA :
//                if (upjongData.size() < 1) return null;
//                if (index > upjongData.size() - 1) upjongDataSelect = 0;
//                else if (index < 0) upjongDataSelect = upjongData.size() - 1;
//                else upjongDataSelect = index;
//                return (String[]) upjongData.elementAt(upjongDataSelect);
//            case GWANSIM_DATA :
//                if (gwansimData.size() < 1) return null;
//                if (index > gwansimData.size() - 1) gwansimDataSelect = 0;
//                else if (index < 0) gwansimDataSelect = gwansimData.size() - 1;
//                else gwansimDataSelect = index;
//                return (String[]) gwansimData.elementAt(gwansimDataSelect);
//                
//            default :
//                return null;
//        }
//    }
//    
//    public void moveHistoryCodeLast(int type) {
//        if (isGwansim) type = GWANSIM_DATA;
//        String[] temp;
//        switch(type) {
//            case KOSPI_DATA :
//                if (kospiData.size() < 1) return;
//                temp = (String[]) kospiData.elementAt(0);
//                kospiData.removeElementAt(0);
//                kospiData.addElement(temp);
//                break;
//            case UPJONG_DATA :
//                temp = (String[]) upjongData.elementAt(0);
//                upjongData.removeElementAt(0);
//                upjongData.addElement(temp);
//                break;                
//        }
//    }
//    
//    public String[] getLastHistoryCode(int type) {
//        if (isGwansim) type = GWANSIM_DATA;        
//        switch(type) {
//            case KOSPI_DATA :
//                if (kospiData.size() < 1) return null;                
//                return (String[]) kospiData.elementAt(0);
//            case UPJONG_DATA :
//                if (upjongData.size() < 1) return null;
//                return (String[]) upjongData.elementAt(0);
//            case GWANSIM_DATA :
//                if (gwansimData.size() < 1) return null;
//                return (String[]) gwansimData.elementAt(0);                
//            default :
//                return null;
//        }
//    }
//    
//    private int checkData(String code,int type) {
//        Vector v = getHistoryData(type);
//        if (v == null) return -1;
//        String[] temp;
//        for (int i = 0 ; i < v.size() ; i++) {
//             temp = (String[]) v.elementAt(i);
//            if (temp[0].equals(code)) return i;
//        }
//        return -1;
//    }
//    
//    private boolean checkKospiData(String code) {        
//        if (kospiData == null) return true;
//        String[] temp;
//        for (int i = 0 ; i < kospiData.size() ; i++) {            
//            temp = (String[]) kospiData.elementAt(i);
//            if (temp[0].equals(code)) return false;
//        }
//        return true;
//    }
//    
//    public Vector getHistoryData(int type) {
//        if (isGwansim) type = GWANSIM_DATA;
//        switch(type) {
//            case KOSPI_DATA :
//                return kospiData;
//            case UPJONG_DATA :
//                return upjongData;
//            case GWANSIM_DATA :
//                return gwansimData;
//            default :
//                return null;
//        }
//    }
}