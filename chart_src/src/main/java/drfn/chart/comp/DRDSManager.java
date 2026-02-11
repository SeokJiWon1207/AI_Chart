package drfn.chart.comp;

import java.util.ArrayList;
import java.util.Hashtable;

import drfn.chart.util.COMMCallback;

class DRDSCodeItem {
	public String m_sCode;
	public ArrayList<COMMCallback> m_List;

	public DRDSCodeItem(String sCode) {
		m_sCode = sCode;
		m_List = new ArrayList<COMMCallback>();
	}

	public ArrayList<COMMCallback> getCOMMCallbackList() {
		return m_List;
	}

	/**
	 Advise
	 @brief COMMCallback List관리(추가)
	 이미 추가되어 있는 COMMCallback은 지원하지 않음.
	 @param receiver : COMMCallback
	 @return int : m_List의 size.
	 */
	public int Advise(COMMCallback receiver) {
		int nSize = m_List.size();
		COMMCallback item = null;

		for(int i=0; i<nSize; i++) {
			item = m_List.get(i);
			if(item==receiver) {
				return -1;
			}
		}

		m_List.add(receiver);
		return m_List.size();
	}

	/**
	 UnAdvise
	 @brief COMMCallback List관리(삭제)
	 @param receiver : COMMCallback
	 @return int : m_List의 size.
	 */
	public int UnAdvise(COMMCallback receiver) {
		int nSize = m_List.size();
		COMMCallback item = null;

		for(int i=0; i<nSize; i++) {
			item = m_List.get(i);
			if(item==receiver) {
				m_List.remove(i);
				return m_List.size();
			}
		}
		return -1;
	}
}

class DRDSKeyItem {
	public String m_sKey;
	String m_sFormat;
	//public ArrayList<DRDSCodeItem> m_List;
	Hashtable<String, DRDSCodeItem> m_hash;

	public DRDSKeyItem(String sKey) {
		m_hash = new Hashtable<String, DRDSCodeItem>();

		m_sKey = sKey;
		if(m_sKey.equals("SAS")) m_sFormat = "%4.4s";
		else m_sFormat = "%6.6s";
	}

	/**
	 getCodeItem
	 @brief
	 @param rKey : DRDS의 Key로 등록하는 개별item(ex.000660)
	 @return DRDSCodeItem : 있으면 기존 리스트에서 리턴, 없으면 새로 만들어서 리턴.
	 */
	public DRDSCodeItem getCodeItem(String rKey) {
		DRDSCodeItem item = null;
		if(m_hash.containsKey(rKey)==true) {
			item = m_hash.get(rKey);
		}
		else {
			item = new DRDSCodeItem(rKey);
		}
		return item;
	}

	/**
	 getCOMMCallbackList
	 @brief
	 @param sDRDSKey :
	 @return ArrayList<COMMCallback>
	 */
	public ArrayList<COMMCallback> getCOMMCallbackList(String sDRDSKey) {
		if(m_hash.containsKey(sDRDSKey)==true) {
			DRDSCodeItem item = m_hash.get(sDRDSKey);
			return item.getCOMMCallbackList();
		}
		return null;
	}

	/**
	 Advise
	 @brief
	 @param receiver :
	 sDRDSKeyList :
	 nCount
	 @return String : 서버에 DRDS 등록할 때 필요한 값. 카운트를 체크해서 이미 등록되어 있으면 서버로 등록요청을 하지 않음.
	 */
	public String Advise(COMMCallback receiver, ArrayList<String> sDRDSKeyList, int nCount) {
		String sSendKey = new String();
		int nSize = sDRDSKeyList.size();
		int rCount = 0;

		for(int i=0; i<nSize; i++) {
			String sDRDSKey = sDRDSKeyList.get(i);
			DRDSCodeItem item = getCodeItem(sDRDSKey);
			rCount = item.Advise(receiver);
			if(rCount == 1) sSendKey += sDRDSKey;
		}
		return sSendKey;
	}

	/**
	 UnAdvise
	 @brief
	 @param receiver :
	 sDRDSKeyList :
	 nCount
	 @return String : 서버에 DRDS 해지할 때 필요한 값. 카운트가 0이 될때만 서버로 해지요청함.
	 */
	public String UnAdvise(COMMCallback receiver, ArrayList<String> sDRDSKeyList, int nCount) {
		String sSendKey = new String();
		int nSize = sDRDSKeyList.size();
		int rCount = 0;

		for(int i=0; i<nSize; i++) {
			String sDRDSKey = sDRDSKeyList.get(i);
			DRDSCodeItem item = getCodeItem(sDRDSKey);
			rCount = item.UnAdvise(receiver);
			if(rCount == 0) {
				sSendKey += sDRDSKey;
				m_hash.remove(sDRDSKey);
			}
		}
		return sSendKey;
	}
}

public class DRDSManager {
	//ArrayList<DRDSKeyItem> m_List;
	Hashtable<String, DRDSKeyItem> m_hash;

	public DRDSManager() {
		m_hash = new Hashtable<String, DRDSKeyItem>();
	}

	/**
	 getCOMMCallbackList
	 @brief
	 @param sDRDSName : 실시간 키값(S31, SAS)
	 sDRDSKey : 실시간 서브키값(000660)
	 @return ArrayList<COMMCallback> : 실시간이 들어왔을 때 결과를 리턴해줄  COMMCallback 리스트
	 */
	public ArrayList<COMMCallback> getCOMMCallbackList(String sDRDSName, String sDRDSKey) {
		DRDSKeyItem item = null;
		if(m_hash.containsKey(sDRDSName)==true) {
			item = m_hash.get(sDRDSName);
			return item.getCOMMCallbackList(sDRDSKey);
		}

		return null;
	}

	/**
	 AdviseDRDS
	 @brief
	 @param receiver :
	 sDRDSName :
	 sDRDSKeyList :
	 nCount :
	 @return String : 서버에 DRDS 등록할 때 필요한 값. 카운트를 체크해서 이미 등록되어 있으면 서버로 등록요청을 하지 않음.
	 */
	public String AdviseDRDS(COMMCallback receiver, String sDRDSName, ArrayList<String> sDRDSKeyList, int nCount) {
		DRDSKeyItem item = null;
		if(m_hash.containsKey(sDRDSName)==true) {
			item = m_hash.get(sDRDSName);
		}
		else {
			item = new DRDSKeyItem(sDRDSName);
		}

		String sSendKey = item.Advise(receiver, sDRDSKeyList, nCount);
		return sSendKey;
	}

	/**
	 UnAdviseDRDS
	 @brief
	 @param receiver :
	 sDRDSName :
	 sDRDSKeyList :
	 nCount :
	 @return String : 서버에 DRDS 해지할 때 필요한 값. 카운트가 0이 될때만 서버로 해지요청함.
	 */
	public String UnAdviseDRDS(COMMCallback receiver, String sDRDSName, ArrayList<String> sDRDSKeyList, int nCount) {
		DRDSKeyItem item = null;
		if(m_hash.containsKey(sDRDSName)==true) {
			item = m_hash.get(sDRDSName);
		}
		else {
			item = new DRDSKeyItem(sDRDSName);
		}

		String sSendKey = item.UnAdvise(receiver, sDRDSKeyList, nCount);
		return sSendKey;
	}
}
