package drfn.chart.net;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

import drfn.chart.comp.PctrManager;
import drfn.chart.util.COMMCallback;
//import drfn.chart.util.TrData;

public class RTHandler {
	int num = 0;
	int removeNum = 0;
	NetClient nc;
	RealLoader rl;
	subRTHandler[] subRT;
	Hashtable<String, CfgFile> hash;
	String threadName = "";
	int threadNum = 0;
	Vector<String> RTVector;
	//ChartDataModel _cdm=null;

	public RTHandler() {
		rl = new RealLoader();
		hash = rl.getCfgFileH();
		subRT = new subRTHandler[rl.getDataNameSize()];
		RTVector = new Vector<String>();
	}

	public RTHandler(NetClient nc) {
		this.nc = nc;
		rl = new RealLoader();
		hash = rl.getCfgFileH();
		subRT = new subRTHandler[rl.getDataNameSize()];
		RTVector = new Vector<String>();
	}

	public String getPosition(String dN, String dK) {
		return rl.getMap(dN,dK);
	}

	public void requestRT(String key) {
		if(!nc.connected) return;
		byte[] packetData = key.getBytes();
		nc.sendTR(false, false, (byte)0x5A, packetData);
		//System.out.println(key);
	}

	public void registeRT(String dataName, String dataKey, RealComp comp, int[] depth) {//**eun  imsi
		//System.out.println(dataName + "  " + dataKey + "  ");
		dataName = dataName.trim();
		//dataKey = dataKey.trim();
		num++;
		int index = threadName.indexOf(dataName+ "*");
		if (index == -1) {
			threadName += dataName + "*";
			subRT[threadNum] = new subRTHandler(dataName);
			subRT[threadNum].addEle(dataKey, comp, depth);
			threadNum++;
			RTVector.addElement(dataName+dataKey);
			requestRT("A"+dataName+"001"+dataKey);
		} else {
			index = (int)((index) / 4);
			if (subRT[index].checkHash(dataKey))  {
				RTVector.addElement(dataName+dataKey);
				requestRT("A"+dataName+"001"+dataKey);
			}
			subRT[index].addEle(dataKey, comp, depth);
		}
	}
	//  	public void setChartDataModel(ChartDataModel model) {
//  		_cdm = model;
//  	}
	public void stopAll() {
		if (!RTVector.isEmpty()) {
			String[] temp = new String[RTVector.size()];
			RTVector.copyInto(temp);
			for (int i = 0 ; i < temp.length ; i++) {
				requestRT("D"+new String(temp[i].substring(0,3))+"001"+temp[i].substring(3));
			}
		}
	}

	public void removeRT(String dataName, String dataKey, RealComp comp) {
		num=0;
		removeNum++;
		dataName = dataName.trim();
		//dataKey = dataKey.trim();
		int index = threadName.indexOf(dataName+ "*");
		if (index != -1) {
			index = (int)((index) / 4);
			if (subRT[index].deleteEle(dataKey,comp)) {
				RTVector.removeElement(dataName+dataKey);
				requestRT("D"+dataName+"001"+dataKey);
			}
		}
	}

	public void removeRT(Vector<String> dataName_dataKey , RealComp comp) {
		removeNum = 0;
		if (!dataName_dataKey.isEmpty()) {
			String[] temp = new String[dataName_dataKey.size()];
			dataName_dataKey.copyInto(temp);
			for (int i = 0 ; i < temp.length ; i++) {
				removeRT(new String(temp[i].substring(0,3)), temp[i].substring(3), comp);
			}
		}
	}
//  수정전 : 2010.12.21  	
//  	public void setData(byte[] data) {  
//  	    if(data==null || data.length<3)return;
//  	    //data : 00010237S310059300909125000012000000828000-1.4300082600000
//  	    String realStr = new String(data, 0, data.length);
//  	    String cntStr = new String(data, 0, 4);
//  	    int cnt = Integer.parseInt(cntStr);
//  	    if(cnt>1) {
//  	    	System.out.println("cnt:"+cntStr);
//  	    }
//  	    String realName = new String(data, 8, 3); 
//  	    int index = threadName.indexOf(realName+ "*");
//  	    index = (int)((index+1) / 4);
//  	    int dataKeyLen = getDataKeyLen(realName);
//  	    if (index != -1  && dataKeyLen != -1) {
//  	        String datakey = new String(data, 11, dataKeyLen);  	        
//  	        if(subRT[index]== null) {
//  	        	realdataToDRDSManager(realName, datakey, data, data.length);
//  	        	return;  	        	
//  	        }
//  	        
//  	        subRT[index].setData(data, realName, datakey);  	        
//  	    } 
//  	}

//	수정후 : 2010.12.21
	/**
	 @function setData_PacketItem
	 @author alzioyes
	 @brief DRDS Name별 실시간데이터.
	 @param data : 실시간  Data
	 //data 00010237S310059300909125000012000000828000-1.4300082600000
	 nDataSize : size of data
	 @return void.
	 */
//  	private TrData trData = new TrData();
	public void setData_PacketItem(byte[] packetItem, int nDataSize) {
		int nOffset = 4+4;

		String realName = new String(packetItem, nOffset, 3);
		nOffset += 3;

		int index = threadName.indexOf(realName+ "*");
		index = (int)((index+1) / 4);
		int dataKeyLen = getDataKeyLen(realName);

		if (index != -1  && dataKeyLen != -1) {
			String datakey = new String(packetItem, nOffset, dataKeyLen);
			if(subRT[index]== null) {
				realdataToDRDSManager(realName, datakey, packetItem, nDataSize);
				return;
			}
			subRT[index].setData(packetItem, realName, datakey);
		}
	}

	/**
	 @function setData
	 @author alzioyes
	 @brief 리얼데이터가 여러개 들어오므로 count를 확인해서 리얼처리를 진행한다.
	 한번에 여러개의 데이터가 수신된 경우 처리. offset 을 이용하여, n개의 데이터를 처리한다.
	 setData_PacketItem통해 subRTHandler에 보낼 때는 실제적으로는 1개의 데이터가 온 것처럼 보낸다.
	 @param data : 실시간  Data
	 //data 00010237S310059300909125000012000000828000-1.4300082600000
	 @return void.
	 */
	public void setData(byte[] _data) {
		if(_data==null || _data.length<3)return;

		byte[] data = new byte[_data.length];	//size(4)포함
		System.arraycopy(_data, 0, data, 0, _data.length);

		//data : 00010237S310059300909125000012000000828000-1.4300082600000
		//String realStr = new String(data, 0, data.length);
		String cntStr = new String(data, 0, 4);
		int cnt = Integer.parseInt(cntStr);

		int nOffset = 4; //count 자리수(4)
		int nSizeFix = 4;
		int nPacketSize=0;
		String strPacketSize;
		String sSizeFixData = String.format("0001");

		for(int k=0; k<cnt; k++) {
			strPacketSize = new String(data, nOffset, 4);
			nPacketSize = Integer.parseInt(strPacketSize) + 4;	//사이즈정보(4) 포함.

			byte[] packetItem = new byte[nPacketSize+nSizeFix+1];	//size(4)포함
			System.arraycopy(sSizeFixData.getBytes(), 0, packetItem, 0, nSizeFix);
			System.arraycopy(data, nOffset, packetItem, nSizeFix, nPacketSize);
			packetItem[nPacketSize+nSizeFix] = 0x00;

			setData_PacketItem(packetItem, nPacketSize+nSizeFix);

			nOffset += (nPacketSize);
		}
	}

	public boolean realdataToDRDSManager(String sDRDSName, String sDRDSKey, byte[] realData, int nRealDataLen) {
		ArrayList<COMMCallback> targetList = PctrManager.getCOMMCallbackList(sDRDSName, sDRDSKey);
		if(targetList == null)
			return false;

		int nSize = targetList.size();
		for(int i=0; i<nSize; i++) {
			COMMCallback target = targetList.get(i);
			target.setRealData(sDRDSName, realData, nRealDataLen);
		}
		return true;
	}

	public int getDataKeyLen(String dataName) {
		CfgFile cfg = new CfgFile();
		if(hash.containsKey(dataName)) {
			cfg = (CfgFile) hash.get(dataName);
			return cfg.getRealKeyLen();
		} else return -1;
	}
}