package drfn.chart.util;

import java.util.Hashtable;

public class TrData {
	private Hashtable<String, String> rtnData=null;
	//Vector packetInfo = null;
	int offset=0;
	int dataOffset=0;
	
	public TrData() {
		
	}
	
	public Hashtable<String, String> makeTrData(String trCode, byte[] data) {
		if(rtnData==null) {
			rtnData = new Hashtable<String, String>();
		}
		
		try {
			String[][] packetInfo = OutputPacket.getPacketInfo(trCode);
			if(packetInfo!=null) {
				offset = 0;
				dataOffset=0;
				for(int i=0; i<packetInfo.length; i++) {
					String key = packetInfo[i][0];
					
					int len = Integer.parseInt(packetInfo[i][1]);
					if(key.equals(OutputPacket.TIME) || key.equals(OutputPacket.CHGRATE) || key.equals(OutputPacket.CODE) || key.equals(OutputPacket.NKEY)) {
//						System.out.println("DEBUG_TrData_nkey:"+COMUtil.stringFromData(data, offset, len));
						rtnData.put(key, COMUtil.stringFromData(data, offset, len));
					} else {
						rtnData.put(key, COMUtil.removeFrontZero(COMUtil.stringFromData(data, offset, len).trim()));
					}
					
					rtnData.put(key+"_Len", ""+len);
					
					offset += len;
					
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return rtnData;
	}
	public int getOffset() {
		return offset;
	}
	public void destroy()
	{
		if(rtnData != null)
			rtnData.clear();
	}
}


