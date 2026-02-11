package drfn.chart.model;

import java.math.BigInteger;
import java.util.StringTokenizer;

import drfn.chart.util.COMUtil;
import drfn.chart.util.ChartUtil;

public class ChartPacketDataModel{
    public static int MAX_DATA_LEN = 400;
    //2014.03.31 by LYH >> 실시간 데이터 전체 개수 늘임 200-> 400 <<
    public final static int REAL_MAX_DATA = 1000;
    public final static int REAL_INVESTOR_MAX_DATA = 10000;
    //public final static int REAL_MAX_INVESTOR_DATA = 10000;

    private String packet_title;        // 패킷 타이틀
    private int dataLength;             // 데이터 길이

    public int format=11, format_org=11;     // 데이터 포맷, 변형 시키기 전의 포맷
    int cnt = 0;
    public boolean intType = true;
    //2012.11.27 by LYH >> 진법 및 승수 처리.
    public int nDispScale = -1;      // 진법
    public int nTradeMulti = -1;     // 거래승수
    public int nLogDisp = -1;        // 진법자리수 ("'" 다음 데이터 사이즈)
    //2012.11.27 by LYH <<
    /*
       "YYYYMMDD","YYMMDD","YYYYMM","YYMM","MMDD",//0-4
        "DDHH","DDHHMM","DDHHMMSS","HHMMSS","HHMMSSNN",//5-9
        "문자","× 1","× 1000","× 0.1","× 0.01",//10-14
        "× 0.001","× 0.0001","%"//15-17
    */

    private double[] datas;
    private String[] strDatas;

    boolean useReal;       // 실시간 데이터 사용여부
    private boolean attr=false;

    public ChartPacketDataModel(){
    }
    public void setProperties(String title,int length,String dataFormat, String real){
        packet_title=title;
        dataLength = length;
        format = ChartUtil.getPacketFormatIndex(dataFormat);
        format_org = format;
        useReal=(real.equals("유"))?true:false;
        //if(format==10 || title.equals("자료일자")||title.equals("락구분")) intType = false;
        if(format==10 || title.equals("자료일자")) intType = false;
    }
    public void initData(int len){
        if(intType) datas = new double[len];
        else strDatas = new String[len];
        cnt = 0;
        if(datas==null && !(packet_title.equals("자료일자")))
            format = format_org;
    }
    public void initAppendData(int len){
        if(intType){
            if(datas==null) return;
            double[] temp = datas;
            datas = new double[temp.length+len];
            System.arraycopy(temp, 0, datas, len, temp.length);
        }
        else{
            if(strDatas==null) return;
            String[] temp = strDatas;
            strDatas = new String[temp.length+len];
            System.arraycopy(temp, 0, strDatas, len, temp.length);
        }
        cnt = 0;
    }
    public void resetCnt(){
        if(intType) {
            if(datas==null)
                cnt = 0;
            else
                cnt = datas.length;
        }
        else
        {
            if(strDatas == null)
                cnt = 0;
            else
                cnt = strDatas.length;
        }
    }
    public void setDatas(double[] data){
        if(data==null) {
            cnt = 0;
            return;
        }
        if(intType) datas = data;
        cnt = data.length;
    }
    public void setDatas(String[] data){
        if(data==null) return;
        if(!intType) strDatas = data;
        cnt = data.length;
    }

    public void addData(String data){
//    	System.out.println("addData:"+data);
        if(intType){
            if(datas==null) return;
            if(cnt>=datas.length)   return;
            datas[cnt] = (data.equals(""))?0:getIntValue(data);
        }
        else{
            if(strDatas==null) return;
            if(cnt>=strDatas.length)  return;
            if(packet_title.equals("자료일자")){
                String time="";
                if(data.indexOf(":")>0 || data.indexOf(".")>0){
                    StringTokenizer st = new StringTokenizer(data, ":.");
                    while(st.hasMoreTokens()) time+= st.nextToken();
                }
                if(data.length()>8){// YYYYMMDDHHMMSS,YYMMDD장마감..
                    if(format_org==1){//yymmdd
                        time=new String(data.substring(0,6));
                    }else if(format_org==7){//ddhhmmss
                        time = new String(data.substring(4));
                    }else if(format_org==8){//hhmmss
                        time = new String(data.substring(6));
                    }
                }
                strDatas[cnt] = (time.equals(""))?data:time;
            }
            else strDatas[cnt] = data;
        }

        cnt++;
    }
    public void addData(double data){
//    	System.out.println("addData:"+data);
        if(intType){
            if(datas==null) return;
            if(cnt>=datas.length)   return;
            datas[cnt] = data;
        }
        cnt++;
    }
    //2013. 10. 23 데이터 없이 흰화면일 때 실시간 받으면 봉 그리기 처리>>
//    public void setData(String data){
//    	if(cnt==0)
//    		return;
//        if(intType){
//
//        	if(datas == null)
//        		return;
//
//            if(data.equals(""))return;
//            datas[cnt-1] = getIntValue(data);    
//        }
//        else {
//        	if(strDatas == null)
//        		return;
//        	strDatas[cnt-1] = data;
//        }
//    }
    public void setData(String data){
//    	if(cnt==0)
//    		return;
        if(intType){

            if(datas == null)
            {
                datas = new double[1];
                datas[0] = getIntValue(data);
                resetCnt();
            }
            else
            {
                if(data.equals("") || cnt==0) return;
                datas[cnt-1] = getIntValue(data);
            }

        }
        else {
            if(strDatas == null)
            {
                strDatas = new String[1];
                switch(format){
                    case 7://DDHHMMSS
                        if(data.length() == 6)
                        {
                            strDatas[0] = COMUtil.getSaveDate("dd")+data;
                        }
                        else
                        {
                            strDatas[0] = data;
                        }
                        break;
                    case 20://MMDDHHMM
                        if(data.length() == 4)
                        {
                            strDatas[0] = COMUtil.getSaveDate("MMdd")+data;
                        }
                        else
                        {
                            strDatas[0] = data;
                        }
                        break;
                    default:
                        strDatas[0] = data;
                        break;
                }
//        		if(data.length() == 4)
//        		{
//        			strDatas[0] = COMUtil.getSaveDate("MMdd")+data;
//        		}
//        		else
//        		{
//        			strDatas[0] = data;
//        		}
                resetCnt();
            }
            else
            {
            	if(cnt==0) return;
                strDatas[cnt-1] = data;
            }

        }
    }
    //2013. 10. 23 데이터 없이 흰화면일 때 실시간 받으면 봉 그리기 처리<<
    public void setData(int data){
        if(intType){
            datas[cnt-1] = data;
        }
    }
    public synchronized void addRealData(String data){
        if(intType){
            if(data.equals(""))return;
            if(datas == null)
            {
//            	datas = new double[1];
                return;
            }

            if(datas==null&&!(packet_title.equals("자료일자")))
                format = format_org;

            MAX_DATA_LEN = datas.length;
            //2014.03.31 by LYH >> 실시간 데이터 전체 개수 늘임 200-> 400 <<
            //if(datas.length >= 200 && datas.length>=MAX_DATA_LEN){		//2013. 10. 23 데이터 없이 흰화면일 때 실시간 받으면 봉 그리기 처리
            if(datas.length >= REAL_MAX_DATA && datas.length>=MAX_DATA_LEN){		//2013. 10. 23 데이터 없이 흰화면일 때 실시간 받으면 봉 그리기 처리
                cnt = MAX_DATA_LEN-1;
                System.arraycopy(datas, 1, datas, 0, cnt);
                datas[cnt] = getIntValue(data);
            }else{
                cnt = datas.length;
                double[] totData = new double[cnt+1];
                System.arraycopy(datas, 0, totData, 0, cnt);
                totData[cnt] = getIntValue(data);
                datas = totData;
            }
        }
        else{
            if(strDatas == null)
            {
                return;
            }

            MAX_DATA_LEN = strDatas.length;
            //2014.03.31 by LYH >> 실시간 데이터 전체 개수 늘임 200-> 400 <<
            //if(strDatas.length >= 200 && strDatas.length>=MAX_DATA_LEN){	//2013. 10. 23 데이터 없이 흰화면일 때 실시간 받으면 봉 그리기 처리
            if(strDatas.length >= REAL_MAX_DATA && strDatas.length>=MAX_DATA_LEN){	//2013. 10. 23 데이터 없이 흰화면일 때 실시간 받으면 봉 그리기 처리
                cnt = MAX_DATA_LEN-1;
                System.arraycopy(strDatas, 1, strDatas, 0, cnt);
                strDatas[cnt] = data;
            }else{
                cnt = strDatas.length;
                String[] totData = new String[strDatas.length+1];
                System.arraycopy(strDatas, 0, totData, 0, cnt);
                totData[cnt] = data;
                strDatas = totData;
            }
        }
        cnt++;
    }

    public synchronized void addRealInvestorData(String data){
        if(intType){
            if(data.equals(""))return;
            if(datas == null)
            {
//            	datas = new double[1];
                return;
            }

            if(datas==null&&!(packet_title.equals("자료일자")))
                format = format_org;

            MAX_DATA_LEN = datas.length;
            //2014.03.31 by LYH >> 실시간 데이터 전체 개수 늘임 200-> 400 <<
            //if(datas.length >= 200 && datas.length>=MAX_DATA_LEN){		//2013. 10. 23 데이터 없이 흰화면일 때 실시간 받으면 봉 그리기 처리
            if(datas.length >= REAL_INVESTOR_MAX_DATA && datas.length>=MAX_DATA_LEN){		//2013. 10. 23 데이터 없이 흰화면일 때 실시간 받으면 봉 그리기 처리
                cnt = MAX_DATA_LEN-1;
                System.arraycopy(datas, 1, datas, 0, cnt);
                datas[cnt] = getIntValue(data);
            }else{
                cnt = datas.length;
                double[] totData = new double[cnt+1];
                System.arraycopy(datas, 0, totData, 0, cnt);
                totData[cnt] = getIntValue(data);
                datas = totData;
            }
        }
        else{
            if(strDatas == null)
            {
                return;
            }

            MAX_DATA_LEN = strDatas.length;
            //2014.03.31 by LYH >> 실시간 데이터 전체 개수 늘임 200-> 400 <<
            //if(strDatas.length >= 200 && strDatas.length>=MAX_DATA_LEN){	//2013. 10. 23 데이터 없이 흰화면일 때 실시간 받으면 봉 그리기 처리
            if(strDatas.length >= REAL_INVESTOR_MAX_DATA && strDatas.length>=MAX_DATA_LEN){	//2013. 10. 23 데이터 없이 흰화면일 때 실시간 받으면 봉 그리기 처리
                cnt = MAX_DATA_LEN-1;
                System.arraycopy(strDatas, 1, strDatas, 0, cnt);
                strDatas[cnt] = data;
            }else{
                cnt = strDatas.length;
                String[] totData = new String[strDatas.length+1];
                System.arraycopy(strDatas, 0, totData, 0, cnt);
                totData[cnt] = data;
                strDatas = totData;
            }
        }
        cnt++;
    }

    public synchronized void addRealData(int data){
        if(!intType) return;

        if(datas == null)
            datas = new double[1];

        if(datas==null&&!(packet_title.equals("자료일자")))
            format = format_org;

        if(datas.length>=MAX_DATA_LEN){
            cnt = MAX_DATA_LEN-1;
            System.arraycopy(datas, 1, datas, 0, cnt);
            datas[cnt] = data;
        }else{
            cnt = datas.length;
            double[] totData = new double[cnt+1];
            System.arraycopy(datas, 0, totData, 0, cnt);
            totData[cnt] = data;
            datas = totData;
        }
        cnt++;
    }

    StringBuffer buf = new StringBuffer();
//    public int getIntValue(String data){
//        if(packet_title.equals("기본거래량")||packet_title.equals("누적거래량")){
//          if(data.startsWith("-")) data = data.substring(1);
//          data = COMUtil.removeToDot(data); //20030626 ykLee add. 거래량 처리
//        }else{
//        	if(buf.length()>0) buf.delete(0, buf.length());
//            int jpoint =data.indexOf(".");
//            if(jpoint>-1){
//                buf.append(data.substring(0,jpoint));
//                buf.append(data.substring(jpoint+1));
//                data = buf.toString();
//            }
//        }
////        int rtnVal = (int)Double.parseDouble(data);
////        return rtnVal;
//        
//        try{
//        	return Integer.parseInt(data);
//        }catch(Exception e){
////        	System.out.println(e);
//            return -1;
//        }
//    }

    public double getIntValue(String data){
        if(data.indexOf("e")!=-1) { //지수형 데이터의 처리
            try{
                Double d = Double.valueOf(data.trim());
                data = ""+d.intValue();
            } catch(Exception e) {
                return 0;
                //System.out.println(e);
            }
        }
        if(packet_title.equals("기본거래량")||packet_title.equals("누적거래량")){
            if(data.startsWith("-")) data = new String(data.substring(1));
            data = COMUtil.removeToDot(data); //20030626 ykLee add. 거래량 처리
        }else{
            //컴포넌트 수정 : 해외지수의 소숫점 처리 문제로 수정 20030507 ykLee.
//            if(this.format == 14) { //x 0.01  해외지수
//                data = COMUtil.formatFl(data, 2, 0); //소숫점으로 오는 데이터 둘째자리까지 처리
//            } else if(this.format == 16) { //x 0.0001  환율
//                data = COMUtil.formatFl(data, 4, 0); //소숫점으로 오는 데이터 넷째자리까지 처리
//            }
        }

        try{
//            StringBuffer buf;
//            int jpoint =data.indexOf(".");
//            if(jpoint>-1){
//                buf = new StringBuffer();
//                buf.append(data.substring(0,jpoint));
//                buf.append(data.substring(jpoint+1));
//                data = buf.toString();
//            }
            double rtnVal = Double.parseDouble(data);
            return rtnVal;
        }catch(Exception e){
            try {
                BigInteger bigInt = new BigInteger(data);
                int itemp = bigInt.intValue();
                return Math.abs(itemp);
            } catch(Exception ee){
                return 0;
            }
        }
    }
    public float getFloatValue(String data){
        if(data.indexOf("e")!=-1) { //지수형 데이터의 처리
            try{
                Double d = Double.valueOf(data.trim());
                data = ""+d.intValue();
            } catch(Exception e) {
                return 0;
                //System.out.println(e);
            }
        }
        if(packet_title.equals("기본거래량")||packet_title.equals("누적거래량")){
            if(data.startsWith("-")) data = new String(data.substring(1));
            data = COMUtil.removeToDot(data); //20030626 ykLee add. 거래량 처리
        }else{
            //컴포넌트 수정 : 해외지수의 소숫점 처리 문제로 수정 20030507 ykLee.
            if(this.format == 14) { //x 0.01  해외지수
                data = COMUtil.formatFl(data, 2, 0); //소숫점으로 오는 데이터 둘째자리까지 처리
            } else if(this.format == 15) { //x 0.001  환율
                data = COMUtil.formatFl(data, 3, 0); //소숫점으로 오는 데이터 넷째자리까지 처리
            } else if(this.format == 16) { //x 0.0001  환율
                //2012.11.27 by LYH >> 진법 및 승수 처리.
                if(nTradeMulti>=0)
                {
                    data = COMUtil.formatFl(data, nTradeMulti, 0);
                }
                else
                    //2012.11.27 by LYH <<
                    data = COMUtil.formatFl(data, 4, 0); //소숫점으로 오는 데이터 넷째자리까지 처리
            }
        }

        try{
            //int rtnVal = Integer.parseInt(data.toString());
            float rtnVal = Integer.valueOf(data).floatValue();
            return rtnVal;
        }catch(Exception e){
            try {
                BigInteger bigInt = new BigInteger(data);
                int itemp = bigInt.intValue();
                return Math.abs(itemp);
            } catch(Exception ee){
                return 0;
            }
        }
    }
    public synchronized int getDataCount(){
        if(datas==null)
        {
            if(strDatas==null)
                return 0;
            else
                return strDatas.length;
        }
        else return datas.length;
    }

    public synchronized double[] getDatas(){
        return datas;
    }
    public synchronized double[] getDatas(int index, int num){
        if(datas==null) return null;

        double[] data = new double[num];
        int dataLen = this.datas.length;
        if(dataLen<index) {
            index = dataLen - num;
        }
        if(dataLen<=(index+num)) {
            num = dataLen - index;
        }
        try{
            System.arraycopy(datas,index,data,0,num);
        }catch(ArrayIndexOutOfBoundsException e){
            //num = data.length;
            //src.length=200 srcPos=0 dst.length=100 dstPos=0 length=200
            if(data.length<datas.length) {
                System.arraycopy(datas,0,data,0,data.length);
            } else {
                System.arraycopy(datas,0,data,0,datas.length);
            }
        }
        return data;
    }
    /*public int getData(int index){
        if(intType) return datas[index];
        else return -1;
    }
    public String getStringData(int index){
        if(intType) return ""+datas[index];
        else return strDatas[index];
    }*/
    public String[] getStringDatas(int index, int num){
        String[] data = new String[num];
        try{
            System.arraycopy(strDatas,index,data,0,num);
        }catch(ArrayIndexOutOfBoundsException e){
            System.arraycopy(strDatas,0,data,0,num);
        }
        return data;
    }
    public String[] getStringDatas(){
        return strDatas;
    }
    public String getFormatData(int index){
        try {
            if(intType){
                if(index>=datas.length)   index=datas.length-1;
                //2012.11.27 by LYH >> 진법 및 승수 처리. <<
                return ChartUtil.getFormatedData(datas[index],getPacketFormat(), nDispScale, nTradeMulti, nLogDisp);
            }else {
                //if(index>=strDatas.length)   index=strDatas.length-1;
                if(strDatas[index] == null) {
                    return "";
                }
                //2012.11.27 by LYH >> 진법 및 승수 처리. <<
                return ChartUtil.getFormatedData(strDatas[index],getPacketFormat(), nDispScale, nTradeMulti, nLogDisp);
            }
        } catch(Exception e) {
            return "";
        }
    }

    public String getData(int index){
        if(index<0) return "";

        if(intType){
            if(datas==null) return "";
            if(datas.length <1)
                return "";
            if(index>=datas.length)   index=datas.length-1;
            return ""+datas[index];
        }else {
            if(strDatas==null) return "";
            if(strDatas.length <1)
                return "";
            if(index>=strDatas.length)   index=strDatas.length-1;
            if(strDatas[index] == null) {
                return "";
            }
            return strDatas[index];
        }
    }
    public double getFirstData(){
        if(datas==null) return Double.NaN;
        if(datas[0] != 0)
            return datas[0];
        else
        {
            for(int i=datas.length-2; i>=0; i--)
            {
                if(datas[i] != 0)
                    return datas[i];
            }
        }
        return 0;
    }
    public double getLastData(){
        if(datas==null) return Double.NaN;
        if(datas[datas.length-1] != 0)
            return datas[datas.length-1];
        else
        {
            for(int i=datas.length-2; i>=0; i--)
            {
                if(datas[i] != 0)
                    return datas[i];
            }
        }
        return 0;
    }
    public String getFirstStringData(){
        if(intType) {
            if(datas==null) return "";
            return ""+datas[0];
        } else {
            if(strDatas==null) return "";
            return ""+strDatas[0];
        }
    }
    public String getLastStringData(){
        if(intType) {
            if(datas==null) return "";
            return ""+datas[datas.length-1];
        } else {
            if(strDatas==null) return "";
            return ""+strDatas[strDatas.length-1];
        }
    }
    public int getPacketLength(){
        return dataLength;
    }
    public void setPacketTitle(String title){
        packet_title = title;
    }
    public String getPacketTitle(){
        return packet_title;
    }
    public int getPacketFormat(){
        //2012.11.27 by LYH >> 진법 및 승수 처리.
        if(nTradeMulti>=0)
        {
            if(this.nDispScale != 10)
                return format*1000+nDispScale;
            return format*1000 + nTradeMulti;
        }
        //2012.11.27 by LYH <<
        return format;
    }
    public int getPacketFormat_Index(){
        //2012.11.27 by LYH >> 진법 및 승수 처리.
        if(nTradeMulti>=0)
        {
            if(this.nDispScale != 10)
                return format;
            if(nTradeMulti>2)
                return 16;
            else if(nTradeMulti==0)
                return 11;
            else
                return nTradeMulti +12;

        }
        //2012.11.27 by LYH <<
        return format;
    }
    public void setPacketFormat(int format){
        this.format = format;
        format_org = format;
    }
    public int getPacketFormatOrg(){
        return format_org;
    }
    public void setAttr(boolean attr){
        this.attr = attr;
    }

    public boolean getAttr(){
        return attr;
    }
    public void clear(){
        this.datas=null;
        this.strDatas=null;
    }
    public String changeFDatebyFormat(String data){
        if(buf.length()>0) buf.delete(0, buf.length());
        switch(format){
            case 5://DDHH
                buf.append(new String(data.substring(2,4)));
                buf.append("0000");
                break;
            case 6://DDHHMM
                buf.append(new String(data.substring(2,6)));
                buf.append("00");
                break;
            case 7://DDHHMMSS
                buf.append(new String(data.substring(2)));
                break;
            case 8: //HHMMSS
            case 20://MMDDHHMM
                if(data.length()==8) {
                    buf.append(new String(data.substring(4, 8)));
                } else {
                    buf.append(new String(data.substring(0, 4)));
                }
                //buf.append(data.substring(4)+"00");
                break;
            default:
                buf.append(data);
                break;
        }
        return buf.toString();
    }

    public String getDate(String data)
    {
        String rtnValue;
        switch(format){
            case 5://DDHH
            case 6://DDHHMM
            case 7://DDHHMMSS
                rtnValue = data.substring(0,2);
                rtnValue = COMUtil.makeZero(rtnValue, 2);
                break;
            case 8: //HHMMSS
            case 20://MMDDHHMM
                if(data.length()==8) {
                    rtnValue = data.substring(0,4);
                } else {
                    rtnValue = "";
                }
                //buf.append(data.substring(4)+"00");
                break;
            default:
                rtnValue = "";
                break;
        }
        return rtnValue;
    }

    //2015.01.08 by LYH >> 3일차트 추가
    public void setDataAtIndex(String data, int nIndex){
        if(intType){

            if(datas == null)
            {
                datas = new double[1];
                datas[0] = getIntValue(data);
                resetCnt();
            }
            else
            {
                if(data.equals("")) return;
                if(getPacketTitle().equals("기본거래량") || getPacketTitle().equals("bar_osc"))
                    datas[nIndex] += getIntValue(data);
                else if(getPacketTitle().equals("종가"))
                {
                    datas[nIndex] = getIntValue(data);

                    double dLastData = 0;
                    int nBlankIndex=nIndex-1;
                    for(; nBlankIndex>=0; nBlankIndex--)
                    {
                        if(datas[nBlankIndex] != 0)
                        {
                            dLastData = datas[nBlankIndex];
                            break;
                        }
                    }

                    for(int i=nBlankIndex+1; i<nIndex; i++)
                    {
                        datas[i] = dLastData;
                    }
                }
            }

        }
        else {
            if(strDatas == null)
            {
                strDatas = new String[1];
                switch(format){
                    case 7://DDHHMMSS
                        if(data.length() == 6)
                        {
                            strDatas[0] = COMUtil.getSaveDate("dd")+data;
                        }
                        else
                        {
                            strDatas[0] = data;
                        }
                        break;
                    case 20://MMDDHHMM
                        if(data.length() == 4)
                        {
                            strDatas[0] = COMUtil.getSaveDate("MMdd")+data;
                        }
                        else
                        {
                            strDatas[0] = data;
                        }
                        break;
                    default:
                        strDatas[0] = data;
                        break;
                }
//        		if(data.length() == 4)
//        		{
//        			strDatas[0] = COMUtil.getSaveDate("MMdd")+data;
//        		}
//        		else
//        		{
//        			strDatas[0] = data;
//        		}
                resetCnt();
            }
            else
            {
                strDatas[nIndex] = data;
            }

        }
    }

    //2016.01.05 by LYH >> 분차트 30초 보정 처리
    public int findMinDataIndex(String data)
    {
        int nRetIndex = -1;
        if(!intType)
        {
            if(strDatas!=null)
            {
                for(int i=0; i<cnt; i++)
                {
                    double dTime = Double.parseDouble(strDatas[i])*100;
                    double dRealTime = Double.parseDouble(data);
                    double dDiff = dTime - dRealTime;
                    if(dTime >= dRealTime) {
                        nRetIndex = i;
                        break;
                    }
                }
            }
        }

        return nRetIndex;
    }

    public void setDataAtIndexMinData(String data, int nIndex)
    {
        if(intType)
        {
            if(datas == null)
            {
                datas = new double[1];
                datas[0] = getIntValue(data);
                resetCnt();
            }
            else
            {
                if(data.equals("")) return;
                if(getIntValue(data)!=0)
                    datas[nIndex] = getIntValue(data);
            }
        }
    }
    //2016.01.05 by LYH << 분차트 30초 보정 처리
    public int findIndex(String data)
    {
        if(!intType)
        {
            if(strDatas!=null)
            {
                for(int i=0; i<cnt; i++)
                {
                    if(strDatas[i].equals(data))
                    {
                        return i;
                    }
                }
            }
        }
        return -1;
    }
    //2015.01.08 by LYH << 3일차트 추가
}