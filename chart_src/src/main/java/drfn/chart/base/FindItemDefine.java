package drfn.chart.base;

import java.util.ArrayList;

/**
 *
 */


/**
 * @author user
 *
 */

class DataDrfnItemA {
	private String mstrItemName;

	public DataDrfnItemA(String strItemName){
		mstrItemName = strItemName;
	}

	public String getItemName() {
		return mstrItemName;
	}
}

class DataDrfnItemB {
	private String mstrKey, mstrItemName;

	public DataDrfnItemB(String strKey, String strItemName){
		mstrKey = strKey;
		mstrItemName = strItemName;
	}

	public String getKey() {
		return mstrKey;
	}

	public String getItemName() {
		return mstrItemName;
	}
}

class DataCodeHelperItemA {
	private String mstrCode, mstrCodeName;
	private int mnType;  //1:KOSPI, 2:KOSDAQ
	private String mstrType;

	public DataCodeHelperItemA(String strLine)
	{
		int[] nIndexs = {6,8,40,1,3};
		int i=0;
		int nStart = 0;
		int nEnd = nIndexs[i++];
		mstrCode = strLine.substring(nStart, nStart+nEnd);

		nStart += nEnd;
		nEnd = nIndexs[i++];
		// blank 8�ڸ�

		nStart += nEnd;
		nEnd = nIndexs[i++];
		mstrCodeName = strLine.substring(nStart, nStart+nEnd);
		mstrCodeName.trim();

		nStart += nEnd;
		nEnd = nIndexs[i++];
		mnType = strLine.charAt(nStart);

		nStart += nEnd;
		nEnd = nIndexs[i++];
		mstrType = strLine.substring(nStart, nStart+nEnd);
	}

	public DataCodeHelperItemA(int nType, String strCode, String strCodeName){
		mnType = nType;
		mstrCode = strCode;
		mstrCodeName = strCodeName;
	}

	public int getType() {
		return mnType;
	}

	public String getStrType() {
		return mstrType;
	}

	public String getCode() {
		return mstrCode;
	}

	public String getCodeName() {
		return mstrCodeName;
	}
}

class SigViewItem {
	private int mnType;  //1:��ǥ��ȣ, 2:�ü���ȣ
	private String mstrCode, mstrCodeName;
	private String mstrCurrent, mstrRate;
	private String mstrSigname;
	private String mstrDate, mstrTime;

	public SigViewItem(int nType, String strCode, String strCodeName,
			String strCurrent, String strRate,
			String strSigname,
			String strDate, String strTime) {
		mnType = nType;
		mstrCode = strCode;
		mstrCodeName = strCodeName;
		mstrCurrent = strCurrent;
		mstrRate = strRate;
		mstrSigname = strSigname;
		mstrDate = strDate;
		mstrTime = strTime;
	}

	public int getType() {
		return mnType;
	}

	public String getCode() {
		return mstrCode;
	}

	public String getCodeName() {
		return mstrCodeName;
	}

	public String getCurrent() {
		return mstrCurrent;
	}

//	public int getSignInfo() {
//		char  ch = mstrRate.charAt(0);
//		if(ch=='-') return -1;
//		if(mstrRate.compareTo("0.00")==0) return 0;
//
//		return 1;
//	}

	public int getDataColor() {
//		char  ch = mstrRate.charAt(0);
//		if(ch=='-')  return R.color.stockdnColor;
//		else if(mstrRate.compareTo("0.00")==0)  return R.color.stockeqColor;
//		 return R.color.stockupColor;

		return 0;
	}

	public String getRate() {
		return mstrRate + "%";
	}

	public String getstrSigname() {
		return mstrSigname;
	}

	public String getDateTime() {
		return mstrDate + " " + mstrTime;
	}
}

class SiseViewItem {
	private int mnType;  //1:��ǥ��ȣ, 2:�ü���ȣ
	private String mstrCode, mstrCodeName;
	private String mstrCurrent, mstrRate, mstrVolume;

	public SiseViewItem(int nType, String strCode, String strCodeName,
			String strCurrent, String strRate, String strVolume) {
		mnType = nType;
		mstrCode = strCode;
		mstrCodeName = strCodeName;
		mstrCurrent = strCurrent;
		mstrRate = strRate;
		mstrVolume = strVolume;
	}

	public int getType() {
		return mnType;
	}

	public String getCode() {
		return mstrCode;
	}

	public String getCodeName() {
		return mstrCodeName;
	}

	public String getCurrent() {
		return mstrCurrent;
	}

//	public int getSignInfo() {
//		char  ch = mstrRate.charAt(0);
//		if(ch=='-')  return -1;
//		else if(mstrRate.compareTo("0.00")==0)  return 0;
//		 return 1;
//	}

	public int getDataColor() {
//		char  ch = mstrRate.charAt(0);
//		if(ch=='-')  return R.color.stockdnColor;
//		else if(mstrRate.compareTo("0.00")==0)  return R.color.stockeqColor;
//		 return R.color.stockupColor;

		return 0;
	}

	public String getRate() {
		return mstrRate;
	}

	public String getVolume() {
		return mstrVolume;
	}
}

class SignalSubItem {
	private boolean mbOnOff;
	private String mstrCode;
	private String mstrValue;

	public SignalSubItem(boolean OnOff, String strCode, String strValue) {
		mbOnOff = OnOff;
		mstrCode = strCode;
		mstrValue = strValue;
	}
	// get Function
	public boolean getOnOff() {
		return mbOnOff;
	}

	public String getCode() {
		return mstrCode;
	}

	public String getValue() {
		return mstrValue;
	}

	// set Function
	public void setOnOff(boolean bOnOff) {
		mbOnOff = bOnOff;
	}

	public void setCode(String strNew) {
		mstrCode = strNew;
	}

	public void setValue(String strNew) {
		mstrValue = strNew;
	}
}

//class SigChoiceItem {
//	private String mstrCode, mstrCodeName;
//	private String mstrSise, mstrSignal;
//
//	public SigChoiceItem(String strCode, String strCodeName,
//			String strSise, String strSignal) {
//		mstrCode = strCode;
//		mstrCodeName = strCodeName;
//		mstrSise = strSise;
//		mstrSignal = strSignal;
//	}
//
//
//
//	public String getCode() {
//		return mstrCode;
//	}
//
//	public String getCodeName() {
//		return mstrCodeName;
//	}
//
//	public String getSiseDisp() {
//		return mstrSise;
//	}
//
//	public void setSiseDisp(String strSise) {
//		mstrSise = strSise;
//	}
//
//	public String getSignalDisp() {
//		return mstrSignal;
//	}
//
//	public void setSignalDisp(String strSignal) {
//		mstrSignal = strSignal;
//	}
//}

//class UserSignalItem {
class SigChoiceItem {
	private int mnCodeType;  //0:NOTDEFINE, 1:KOSPI, 2:KOSDAQ, 3:FUTURE, 4: CALL, 5: PUT
	private String mstrCode, mstrCodeName;
	private String mstrTitleSise, mstrTitleSignal;
	private ArrayList<SignalSubItem> mListSise, mListSignal;

	public SigChoiceItem(int nType, String strCode, String strCodeName,
			String strTitleSise, String strTitleSignal) {
		mnCodeType = nType;
		mstrCode = strCode;
		mstrCodeName = strCodeName;
		mstrTitleSise = strTitleSise;
		mstrTitleSignal = strTitleSignal;
		mListSise = new ArrayList<SignalSubItem>();
		mListSignal = new ArrayList<SignalSubItem>();
	}

	public SigChoiceItem(int nType, String strCode, String strCodeName,
			String strTitleSise, String strTitleSignal,
			ArrayList<SignalSubItem> listSise, ArrayList<SignalSubItem> listSignal) {
		mnCodeType = nType;
		mstrCode = strCode;
		mstrCodeName = strCodeName;
		mstrTitleSise = strTitleSise;
		mstrTitleSignal = strTitleSignal;
		mListSise = listSise;
		mListSignal = listSignal;
	}

	public int getCodeType() {
		return mnCodeType;
	}

	public String getCode() {
		return mstrCode;
	}

	public String getCodeName() {
		return mstrCodeName;
	}

	public String getTitleSise() {
		return mstrTitleSise;
	}

	public void setTitleSise(String strTitleSise) {
		mstrTitleSise = strTitleSise;
	}

	public String getTitleSignal() {
		return mstrTitleSignal;
	}

	public void setTitleSignal(String strTitleSignal) {
		mstrTitleSignal = strTitleSignal;
	}

	public ArrayList<SignalSubItem> getSiseList() {
		return mListSise;
	}

	public ArrayList<SignalSubItem> getSignalList() {
		return mListSignal;
	}
}
//class JipyoChoiceItem {
class JipyoChoiceItem {
	private String mstrTag;  //0:NOTDEFINE, 1:KOSPI, 2:KOSDAQ, 3:FUTURE, 4: CALL, 5: PUT
	private String mstrCodeName;
	private boolean mstrCheck;

	public JipyoChoiceItem(String tag, String codeName, boolean check) {
		mstrTag = tag;
		mstrCodeName = codeName;
		mstrCheck = check;
	}

	public String getTag() {
		return mstrTag;
	}

	public String getName() {
		return mstrCodeName;
	}

	public boolean getCheck() {
		return mstrCheck;
	}

}
//class JipyoChoiceItem {
class MinSettingItem {
	private String mstrValue;

	public MinSettingItem(String value) {
		mstrValue = value;
	}

	public String getValue() {
		return mstrValue;
	}

}

//class LoadCellItem {
class LoadCellItem {

	private String mstrverInfo;
	private String mstruid;
	private String mstrimgurl;
	private String mstrgraphList;
	private String mstrsymbol;
	private String mstrlcode;
	private String mstrdataTypeName;
	private String mstrcount;
	private String mstrviewCount;
	private String mstrvalueOfMin;
	private String mstrdeviceID;
	private String mstranalInfo;
	private String mstruserId;
	private String mstruserIp;
	private String mstrcodeName;
	private String mstrtitle;
	private String mstrdetail;
	private String mstrsaveDate;
	private String mstrapCode;

	public LoadCellItem(
			String verInfo,
			String uid,
			String imgurl,
			String graphList,
			String symbol,
			String lcode,
			String dataTypeName,
			String count,
			String viewCount,
			String valueOfMin,
			String deviceID,
			String analInfo,
			String userId,
			String userIp,
			String codeName,
			String title,
			String detail,
			String saveDate,
			String apCode) {

	    mstrverInfo=verInfo;
	    mstruid=uid;
		mstrimgurl=imgurl;
		mstrgraphList=graphList;
		mstrsymbol=symbol;
		mstrlcode=lcode;
		mstrdataTypeName=dataTypeName;
		mstrcount=count;
		mstrviewCount=viewCount;
		mstrvalueOfMin=valueOfMin;
		mstrdeviceID=deviceID;
		mstranalInfo=analInfo;
		mstruserId=userId;
		mstruserIp=userIp;
		mstrcodeName=codeName;
		mstrtitle=title;
		mstrdetail=detail;
		mstrsaveDate=saveDate;
		mstrapCode=apCode;
	}

	public String getverInfo() {
		return mstrverInfo;
	}

	public String getuid() {
		return mstruid;
	}

	public String getimgurl() {
		return mstrimgurl;
	}

	public String getgraphList() {
		return mstrgraphList;
	}

	public String getsymbol() {
		return mstrsymbol;
	}
	public String getlcode() {
		return mstrlcode;
	}
	public String getdataTypeName() {
		return mstrdataTypeName;
	}
	public String getcount() {
		return mstrcount;
	}
	public String getviewCount() {
		return mstrviewCount;
	}
	public String getvalueOfMin() {
		return mstrvalueOfMin;
	}
	public String getdeviceID() {
		return mstrdeviceID;
	}
	public String getanalInfo() {
		return mstranalInfo;
	}
	public String getuserId() {
		return mstruserId;
	}
	public String getuserIp() {
		return mstruserIp;
	}
	public String getcodeName() {
		return mstrcodeName;
	}
	public String gettitle() {
		return mstrtitle;
	}
	public String getdetail() {
		return mstrdetail;
	}
	public String getsaveDate() {
		return mstrsaveDate;
	}
	public String getapCode() {
		return mstrapCode;
	}

}

class ToolbarItem {
	private String m_strImage;
	private String m_strTitle;
	private int m_nTag;
	private boolean m_bChk;

	public ToolbarItem(String strImgName, String strTitle, int nTag, String strChk) {
		m_strImage = strImgName;
		m_strTitle = strTitle;
		m_nTag = nTag;

		setChk(strChk);
	}

	public boolean getChk()
	{
		return m_bChk;

	}

	public void setChk(String strChk)
	{
		if(strChk.equals("1"))
			m_bChk = true;
		else m_bChk = false;
	}

	public void setChk(boolean bChk)
	{
		m_bChk = bChk;
	}

	public String getImage() {
		return m_strImage;
	}

	public String getTitle() {
		return m_strTitle;
	}

	public int getTag() {
		return m_nTag;
	}

}