package drfn.chart.comp;

import java.util.ArrayList;

import drfn.chart.comp.DRDSManager;
import drfn.chart.net.NetClient;
import drfn.chart.net.RTHandler;
import drfn.chart.util.COMMCallback;
import drfn.chart.util.COMUtil;

public class PctrManager {
	public static String TRNO_ONECLICK = "50009";	///< TR : 원클릭조회
	public static String TR_TICKER = "88802";	///< TR : 티커 조회
	public static String TRNO_SIGANL = "05300";		///< TR : 신호조회
	public static String TRNO_GWANSIM  = "05010";	///< TR : 관심조회
	public static String TRNO_CODESISE  = "05310";	///< TR : 종목시세조회
	public static String DRDS_S31 = "S31";			///< DRDS RealKey : S31
	public static String DRDS_SAS = "SAS";			///< DRDS RealKey : SAS

	public static final int SIZE_DRDS_S31 = 6;
	public static final int SIZE_DRDS_SAS = 6;

	public static DRDSManager g_DRDSMng;

	/**
	 dataDBMng_RequestOneclickTR
	 @brief 원클릭 정보 조회
	 @param codeList : 종목코드 리스트.
	 receiver  : 결과를 리턴받을 Activity
	 @return boolean : 성공여부(true/false)
	 */
	public static boolean dataDBMng_RequestOneclickTR(COMMCallback receiver, byte[] sendData, int nDataLen)
	{
//		InputStream istream = getResources().openRawResource(R.raw.b01);
//		try {
//		}
//		byte[] sendData = new byte[5];
		//ChartPro pMainFrame = (ChartPro)COMUtil._chartMain;
		String szTR = TRNO_ONECLICK;
		COMUtil._mainFrame.sendTREx(receiver, szTR, sendData, nDataLen);

		return true;
	}
	/**
	 dataDBMng_RequestTickerTR
	 @brief 차트 티커 정보 조회
	 @param codeList : 종목코드 리스트.
	 receiver  : 결과를 리턴받을 Activity
	 @return boolean : 성공여부(true/false)
	 */
	public static boolean dataDBMng_RequestTickerTR(COMMCallback receiver, byte[] sendData, int nDataLen)
	{
//		InputStream istream = getResources().openRawResource(R.raw.b01);
//		try {
//		}
//		byte[] sendData = new byte[5];
		//ChartPro pMainFrame = (ChartPro)COMUtil._chartMain;
		String szTR = TR_TICKER;
		COMUtil._mainFrame.sendTREx(receiver, szTR, sendData, nDataLen);

		return true;
	}
	/**
	 dataDBMng_RequestSiseTR
	 @brief 관심종목처럼 여러종목에 해당하는 시세정보 조회
	 @param codeList : 종목코드 리스트.
	 receiver  : 결과를 리턴받을 Activity
	 @return boolean : 성공여부(true/false)
	 */
	public static boolean dataDBMng_RequestSiseTR(COMMCallback receiver, ArrayList<String> codeList) {
		int nItems = codeList.size();
		//String sHeader = "1111" + nItems;
		String sHeader = String.format("1111%04d", nItems);
		String sContent = "";

		String sTmp;
		for(int i=0; i<nItems; i++) {
			sTmp = String.format("%-16s", codeList.get(i));
			sContent += sTmp;
		}

		String sSendData = sHeader + sContent;
		String szTR = TRNO_GWANSIM;
		COMUtil._mainFrame.sendTREx(receiver, szTR, sSendData.getBytes(), sSendData.length());
		//COMUtil._mainFrame.sendTR(szTR, sSendData);

		return true;
	}

//	public static boolean dataDBMng_RequestSiseTR(String sSendData, Activity receiver) {
//		int nItems = codeList.size();
//		//String sHeader = "1111" + nItems;
//		String sHeader = "11110002";
//		String sContent = null;
//		
//		for(int i=0; i<nItems; i++) {
//			sContent += codeList.get(i);
//		}
//		String sSendData = sHeader + sContent;
//		String szTR = TRNO_GWANSIM;
//		COMUtil._mainFrame.sendTREx(szTR, sSendData.getBytes(), sSendData.length());
//		
//		return true;
//	}

	/**
	 //dataDBMng_RequestOnecodeSiseTR
	 @brief 종목에 해당하는 시세정보를 구하는 함수
	 @param sDRDSName : DRDS 키값
	 codeList : 등록하려는 키 List
	 receiver  : 결과를 리턴받을 Activity
	 @return boolean : 성공여부(true/false)
	 */
	/**
	 @brief 05310관련 구조체
	 typedef struct
	 {
	 char	type [1];		// '0':주식, '1':선물&옵션, '2':업종
	 char	code [15];		// 종목코드 나머지 빈칸으로
	 } TR_05310_I;

	 typedef struct
	 {
	 char	type [1];		// '0':주식, '1':선물&옵션, '2':업종
	 char	code [15];		// 종목코드 나머지 빈칸으로
	 char    data [169];		// 타입별로 들어있는 데이터
	 } TR_05310_O;

	 // 주식조회 응답 OUTPUT 13개 Size 144
	 typedef struct
	 {
	 char	name            [20];   //종목명
	 char    price           [ 9];   //현재가
	 char    sign            [ 1];   //등락부호
	 char    change          [ 9];   //전일대비
	 char    change_rate     [ 7];   //대비율
	 char    preprice        [ 9];   //전일종가
	 char    bidho           [ 9];   //매수호가
	 char    offerho         [ 9];   //매도호가
	 char    volume          [12];   //누적거래량
	 char    value           [14];   //거래대금
	 char    uplmtprice      [ 9];   //상한가
	 char    dnlmtprice      [ 9];   //하한가
	 char    openam          [ 9];   //시가
	 char    high            [ 9];   //고가
	 char    low             [ 9];   //저가
	 char	cvolume			[ 9];	//체결량
	 char	bidremain		[12];	//매수잔량
	 char	oferremain		[12];	//매도잔량
	 } TR_STOCK_O;
	 */
	public static boolean dataDBMng_RequestOnecodeSiseTR(COMMCallback receiver, String spSendData) {
		String sTmp = String.format("%-15s", spSendData);
		String sSendData = "0" + sTmp; // '0':주식, '1':선물&옵션, '2':업종 
		String szTR = TRNO_CODESISE;
		COMUtil._mainFrame.sendTREx(receiver, szTR, sSendData.getBytes(), sSendData.length());

		return true;
	}

	/**
	 dataDBMng_AdviseDRDS
	 @brief 리얼을 받을 종목 등록. (A:등록 Key)
	 @param receiver :
	 sDRDSName :
	 sDRDSKeyBuffer :
	 nCount :
	 @return boolean : 성공여부(true/false)
	 */
	public static boolean dataDBMng_AdviseDRDS(COMMCallback receiver, String sDRDSName, ArrayList<String> sDRDSKeyList, int nCount) {
//--> Test Routine
//		String sSendData = String.format("A%3s%03d%s", sDRDSName, nCount, sDRDSKeyBuffer.toString()); //"AS31001000660";
//<--
		// 테스트가 다 되지않아 일단 막음.
		//if(g_DRDSMng==null) return false;

		if(g_DRDSMng==null) g_DRDSMng = new DRDSManager();

		String sDRDSCodeData = g_DRDSMng.AdviseDRDS(receiver, sDRDSName, sDRDSKeyList, nCount);
		String sSendData = String.format("A%3s%03d%s", sDRDSName, nCount, sDRDSCodeData);

		NetClient nc = COMUtil._mainFrame.netClient;
		if(nc==null) return false;

		RTHandler rtHandler = nc.getRTHandler();
		if(rtHandler==null) return false;

		rtHandler.requestRT(sSendData);
		return true;
	}

	/**
	 dataDBMng_UnAdviseDRDS
	 @brief 리얼을 받을 종목 해지.(U:해지 Key)
	 @param receiver :
	 sDRDSName :
	 sDRDSKeyBuffer :
	 nCount :
	 @return boolean : 성공여부(true/false)
	 */
	public static boolean dataDBMng_UnAdviseDRDS(COMMCallback receiver, String sDRDSName, ArrayList<String> sDRDSKeyList, int nCount) {
		if(g_DRDSMng==null) return false;

		String sDRDSCodeData = g_DRDSMng.UnAdviseDRDS(receiver, sDRDSName, sDRDSKeyList, nCount);
		String sSendData = String.format("U%3s%03d%s", sDRDSName, nCount, sDRDSCodeData);

		NetClient nc = COMUtil._mainFrame.netClient;
		if(nc==null) return false;

		RTHandler rtHandler = nc.getRTHandler();
		if(rtHandler==null) return false;

		rtHandler.requestRT(sSendData);
		return true;
	}

	/**
	 dataDBMng_AllUnAdviseDRDS
	 @brief 등록된 리얼을 모두 해지
	 @param receiver : null이면 전체, 아니면 해당 COMMCallback만 대상임.
	 sDRDSName :
	 @return boolean : 성공여부(true/false)
	 */
	public static boolean dataDBMng_AllUnAdviseDRDS(COMMCallback receiver) {
		if(g_DRDSMng==null) return false;

//		String sSendData = String.format("U%3s%03d%s", sDRDSName, nCount, sDRDSCodeData);
//
//		NetClient nc = COMUtil._mainFrame.netClient;
//		if(nc==null) return false;
//
//        RTHandler rtHandler = nc.getRTHandler();
//        if(rtHandler==null) return false;
//
//		rtHandler.requestRT(sSendData);
//		
//		//g_DRDSMng.AllUnAdviseDRDS();
		return true;
	}

	/**
	 getCOMMCallbackList
	 @brief
	 @param sDRDSName : 실시간 키값(S31, SAS)
	 sDRDSKey : 실시간 서브키값(000660)
	 @return ArrayList<COMMCallback> : 실시간이 들어왔을 때 결과를 리턴해줄  COMMCallback 리스트
	 */
	public static ArrayList<COMMCallback> getCOMMCallbackList(String sDRDSName, String sDRDSKey) {
		if(g_DRDSMng==null) return null;

		return g_DRDSMng.getCOMMCallbackList(sDRDSName, sDRDSKey);
	}

	/**
	 codeDBMng_getCodeName
	 @brief 요청한 코드에 맞는 종목코드명 넘겨주기
	 @param sCode : 요청하는 종목코드
	 @return String : 종목코드명
	 */
	public static String codeDBMng_getCodeName(String sCode) {
		return "CodeName";
	}

	public static void notifyMainFrameStopAll() {
		dataDBMng_AllUnAdviseDRDS(null);
	}
}
