package drfn.chart.util;

public class CodeItemObj {
	public String strCode;
	public String strName;
	public String strPrice;
	public String strChange;
	public String strChgrate;
	public String strSign;
	public String strVolume;
	public String strValue;	//2016.01.20 by LYH >> 거래대금 실시간
	public String strPreVolume;
	public String S1386;
	public String S1339;
	public String S1522;
	public String strHighest;  //2013. 2. 12.   상하한가 바 추가 
	public String strLowest;   //2013. 2. 12.   상하한가 바 추가 
	public String strGijun;   //2013. 2. 12.   상하한가 바 추가
	public String strMoveVolume;
	public String strRealKey;
	public String strNextKey;
	public String strDataType;
	public String strUnit;
	public String strBaseMarket;
	public String strMarket;

	//기준 시,고,저,종 설정
	public String strGiOpen;
	public String strGiHigh;
	public String strGiLow;
	public String strGiClose;

	//기준 전일 시,고,저 설정
	public String strGiPreOpen;
	public String strGiPreHigh;
	public String strGiPreLow;
	public String strGiPreClose;

	//2013.07.31 >> 기준선 라인 차트 타입 추가 
	public String strStandardPrice;

	public String strRealCode;	//2014.04.17 by LYH >> 연결선물 실시간 처리.

	public double dLastVol;
	public double dLastValue;

	public CodeItemObj() {
		strCode = "";
		strName = "";
		strPrice = "";
		strChange = "";
		strNextKey = "";
		strChgrate = "";
		strSign = "";
		strVolume= "";
		strValue = ""; //2016.01.20 by LYH >> 거래대금 실시간
		S1386 = "";
		S1339 = "";
		S1522 = "";
		strHighest = "";  //2013. 2. 12.   상하한가 바 추가
		strLowest = "";   //2013. 2. 12.   상하한가 바 추가
		strGijun = "";    //2013. 2. 12.   상하한가 바 추가
		strMoveVolume = "";
		strRealKey= "";
		strNextKey = "";
		strDataType = "2";
		strUnit = "1";
		strBaseMarket = "";
		strMarket = "";

		strGiOpen = "";
		strGiHigh = "";
		strGiLow = "";
		strGiClose = "";
		//2013.07.31 >> 기준선 라인 차트 타입 추가

		//기준 전일 시,고,저 설정
		strGiPreOpen = "";
		strGiPreHigh = "";
		strGiPreLow = "";
		strGiPreClose = "";

		strStandardPrice = "";
		strRealCode = "";	//2014.04.17 by LYH >> 연결선물 실시간 처리.

		dLastVol = -1;
	}
}


