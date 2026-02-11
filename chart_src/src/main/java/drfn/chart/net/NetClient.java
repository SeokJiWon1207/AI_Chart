package drfn.chart.net;

import android.app.Activity;
import android.app.AlertDialog;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Vector;

import drfn.chart.util.COMMCallback;
import drfn.chart.util.COMUtil;

//import drfn.ChartPro;

class baseCOMMCallback {
	public void setTRData() {
	}

	public void setRealData(){
	}
}

//import com.initech.cs.*;
public class NetClient extends Thread {
	Socket socket = null;
	DataInputStream in;
	DataOutputStream out;
	public boolean connected = false;
	public RTHandler rt;
	Hashtable<String, PacketData> seq;
	//	private String serverIP;
	String windowID;
	int windowIdx = 0;

	private boolean isConnect = true;
//    private int type = 0;
//	private String userID = "";
//	private String passWD = "";

	public static final int SIZE_TRUseTable = 10;
	Hashtable<String, COMMCallback> m_TRCommTable;	///< 통신할 때 윈도우 관리용
	boolean[] 	m_TRUseTable;		///< 통신할 때 윈도우 관리용

	public NetClient(String serverIP, Activity _wts) {
		//super("ChartPro Java Socket Thread");
//	    this.serverIP = serverIP;

		///< 사용table 셋팅
		m_TRUseTable = new boolean[SIZE_TRUseTable];
		int i=0;
		for(i=1; i<SIZE_TRUseTable; i++) m_TRUseTable[i]=false;
		m_TRUseTable[0] = true; // /// 예외처리 : 차트를 위해 0은 skip

		m_TRCommTable = new Hashtable<String, COMMCallback>();
	}

	public void setUserIdPw(String userID, String passWD) {
//        this.userID = userID;
//        this.passWD = passWD;
	}

	public void run() {
		while (connected) {
			try{
				process();
			}catch(EOFException e){
				e.printStackTrace();
				System.out.println("EOFException Occured");
			}catch(IOException e){
				this.isConnect = false;
				COMUtil.showMessage(COMUtil._chartMain, "run error:"+e.getMessage());
				//this.stop();
//                COMUtil._wts.ReConnect();
				return;
			}
		}
	}

	PacketData pd;
	PacketHeader ph = null;
	byte[] header_buf = new byte[5];
	private boolean process() throws IOException, EOFException {
		if(ph == null)
			ph= new PacketHeader();

		//if(in.available() == 0) return false;   //CPU 문제의 원인...
		int count = 0;
		int rl;

		count = in.read(header_buf);
		while (count < 5) {
			rl = in.read(header_buf, count, 5 - count);
			if (rl != -1) {
				count += rl;
			} else {
				return false;
			}
		}

		count = 0;
		int dataLength = ph.parse(header_buf);

		if(dataLength < 1 || dataLength > 4089) return false;

		byte[] data_buf = new byte[dataLength];

		while (count < dataLength) {
			rl = in.read(data_buf, count, dataLength - count);
			if (rl != -1) {
				count += rl;
			} else {
				return false;
			}
		}

		if(ph.enc) {
			try{
//		        data_buf = clientSession.decrypt(data_buf);
			}catch(Exception e){
				System.out.println("decript error");
			}
		}

		contPD(""+ph.sequence, data_buf);

		ph.etx = in.readUnsignedByte();
		ph.bcc = in.readUnsignedByte();
		return true;
	}

	private void contPD(String sequence, byte[] data_buf) {   //**eun	
		if(seq.containsKey(sequence)) {
			pd = (PacketData)seq.get(sequence);
			if(ph.comp) pd.addCompData(data_buf);
			else pd.addData(data_buf);
		} else {
			pd = new PacketData();
			seq.put(sequence, pd);
			if(ph.comp) pd.setCompData(data_buf);
			else pd.setData(data_buf);
		}

		if(ph.last) {
			if(ph.comp){
				byte[] data = pd.getCompData();
				data_buf = NeoCompress.UnCompress(data, data.length);
			} else {
				data_buf = pd.trData;
			}
			boolean b = pd.parse(data_buf);
			if(!b) return;

			if(pd.command==0x40) {  //TR data

				if(dataV.size() > 0) {
					dataType = Integer.parseInt((String)dataV.elementAt(0));
					if(dataV.size() > 0) dataV.removeElementAt(0);
				}

				//if(pd.trCode.equals("10000")) {
				String sKey = pd.windowID;	//"0001";
				COMMCallback target = getTRCommTable(sKey);

				if(target == null) {
					if(pd.trCode.equals(COMUtil.apCode)) {
						//_wts.mainFrame.setData(pd.trData);
						//System.out.println("str:"+pd.trData);
						COMUtil._mainFrame.setData(pd.trData);
					}
				}
				else {
					setTRCommTableIndex(Integer.parseInt(sKey), false);
					target.setTRData(pd.trCode, pd.trData, pd.trData.length);
				}
			} else if(pd.command==0x5C) {
				String sKey = pd.windowID;	//"0001";
				if(sKey!=null) {
//            		COMMCallback target = getTRCommTable(sKey);
				} else {
					rt.setData(pd.realData);
				}
			}
			seq.remove(sequence);
		}else{
		}
	}
	// 0: success
	// 1: cannot connect to server
	AlertDialog alert;
	public synchronized int connect(String host, int port) {
		try {
			if(socket != null) socket = null;
			socket = new Socket(host, port);
			out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream(),1024));
			in = new DataInputStream(new BufferedInputStream(socket.getInputStream(),3096));

		} catch (Exception e) {
			//alert.setAlert("차트서버연결에 실패했습니다. 해당서비스 업체에 문의바랍니다.", "drfn2009", 0);
			COMUtil.showMessage(COMUtil._chartMain, "차트서버연결에 실패했습니다. 해당서비스 업체에 문의바랍니다.");
//			System.out.println("차트서버연결에 실패했습니다. 해당서비스 업체에 문의바랍니다.");
//			System.out.println("Connection Failed !!!!");
			return 1;
		}
		connected=true;
		rt = new RTHandler(this);
		COMUtil.rt = rt;

		seq = new Hashtable<String, PacketData>();
		return 0;
	}
	public RTHandler getRTHandler() {
		return rt;
	}

	public synchronized void disconnect() {
		if(!connected) return;
		rt.stopAll();   //모든 리얼을 취소한다.
		connected=false;
		if(this.isAlive()&&(Thread.currentThread()!=this) ) {
			//this.stop();
		}
		try {
			in.close();
			out.close();
			socket.close();
//			System.out.println("Disconnected");
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void sendTR(byte command, String winID, String trCode, String trData) {
		StringBuffer sendbuffer = new StringBuffer();
		sendbuffer.append(winID);
		sendbuffer.append(trCode);
		sendbuffer.append(new String(COMUtil.fillSpace("", 6, COMUtil.FILLZERO)));
		sendbuffer.append(new String(COMUtil.fillSpace("", 82, COMUtil.FILLSPACE)));
		sendbuffer.append(new String(COMUtil.fillSpace("", 2, COMUtil.FILLSPACE)));
		sendbuffer.append(new String(COMUtil.fillSpace("", 3, COMUtil.FILLSPACE)));
		sendbuffer.append(new String(COMUtil.fillSpace("", 3, COMUtil.FILLZERO)));
		sendbuffer.append(trData);
		String sendData = sendbuffer.toString();
		sendTR(false, false, command, sendData.getBytes());
	}

	int dataType = 99; //NONE_TYPE = 99
	//    Hashtable datahash = new Hashtable();
	Vector<String> dataV = new Vector<String>();
	public void sendTR(byte command, String trCode, String trData, int dataType) {
		dataV.addElement(String.valueOf(dataType));
		this.dataType = dataType;
//        byte[] temp = trData.getBytes();
		StringBuffer sendbuffer = new StringBuffer();
		sendbuffer.append("0000");
		sendbuffer.append(trCode);
		sendbuffer.append(new String(COMUtil.fillSpace("", 6, COMUtil.FILLZERO)));
		sendbuffer.append(new String(COMUtil.fillSpace("", 82, COMUtil.FILLSPACE)));
		sendbuffer.append(new String(COMUtil.fillSpace("", 2, COMUtil.FILLSPACE)));
		sendbuffer.append(new String(COMUtil.fillSpace("", 3, COMUtil.FILLSPACE)));
		sendbuffer.append(new String(COMUtil.fillSpace("", 3, COMUtil.FILLZERO)));
		sendbuffer.append(trData);
		String sendData = sendbuffer.toString();
		sendTR(false, false, command, sendData.getBytes());
	}
	public void sendTR(byte command, String trCode, String trData) {
//        byte[] temp = trData.getBytes();
		StringBuffer sendbuffer = new StringBuffer();
		sendbuffer.append("0000");
		sendbuffer.append(trCode);
		sendbuffer.append(new String(COMUtil.fillSpace("", 6, COMUtil.FILLZERO)));
		sendbuffer.append(new String(COMUtil.fillSpace("", 82, COMUtil.FILLSPACE)));
		sendbuffer.append(new String(COMUtil.fillSpace("", 2, COMUtil.FILLSPACE)));
		sendbuffer.append(new String(COMUtil.fillSpace("", 3, COMUtil.FILLSPACE)));
		sendbuffer.append(new String(COMUtil.fillSpace("", 3, COMUtil.FILLZERO)));
		sendbuffer.append(trData);
		String sendData = sendbuffer.toString();
		sendTR(false, false, command, sendData.getBytes());
	}

	/**
	 //void sendTREx(COMMCallback target, String trCode, byte[] trData, int nDataLen)
	 @author alzioyes.
	 @date   2010/12/15
	 @brief TR 요청시 byte형태의 데이터로 요청한다. 그리고 TR통신할 때 WinID를 셋팅하는 기능을 사용한다.
	 @param target : 데이터를 콜백할 대상.
	 trCode : tr번호
	 trData : byte[] 형태의 데이터
	 nDataLen : 데이터 길이
	 @return 없음.
	 */
	public void sendTREx(COMMCallback target, String trCode, byte[] trData, int nDataLen) {
		byte command = 0x40;
//        byte[] temp = trData.getBytes();
		StringBuffer sendbuffer = new StringBuffer();

		int nIndex = getNewTRCommTableIndex();

		String sTRKey; // = "0001";
		sTRKey = String.format("%04d", nIndex);

		setTRCommTable(sTRKey, target);

		//sendbuffer.append("0000");
		sendbuffer.append(sTRKey);
		sendbuffer.append(trCode);
		sendbuffer.append(new String(COMUtil.fillSpace("", 6	, COMUtil.FILLZERO)));
		sendbuffer.append(new String(COMUtil.fillSpace("", 82	, COMUtil.FILLSPACE)));
		sendbuffer.append(new String(COMUtil.fillSpace("", 2	, COMUtil.FILLSPACE)));
		sendbuffer.append(new String(COMUtil.fillSpace("", 3	, COMUtil.FILLSPACE)));
		sendbuffer.append(new String(COMUtil.fillSpace("", 3	, COMUtil.FILLZERO)));
		//sendbuffer.append(trData);
		String sendData = sendbuffer.toString();

		int nSendLength = sendData.length()+ nDataLen;
		byte[] packetData = new byte[nSendLength];

		//sendTR(false, false, command, sendData.getBytes());
		int nOffset = 0;
		System.arraycopy(sendData.getBytes(), 0, packetData, nOffset, sendData.length());

		nOffset += sendData.length();
		System.arraycopy(trData, 0, packetData, nOffset, nDataLen);

		sendTR(false, false, command, packetData);
	}

	public boolean isCertState = false;

	public void sendTR(boolean Enc, boolean Cer, byte command, byte[] dataForSend) {
		isCertState = false;
		if(!connected) return;

		byte[] packetData = new byte[dataForSend.length+1];
		packetData[0] = command;
		System.arraycopy(dataForSend, 0, packetData, 1, dataForSend.length);

		if(Enc) {
			try{
//		        packetData = clientSession.encrypt(packetData);
			}catch(Exception e){
				System.out.println("encript error");
			}
		}

		if(packetData.length <= 4089) { // Skc-SPEED TRData size : 911
			// 대부분이 이 경우이기 때문에 그냥 분리했구
			// 연속일 경우만 따로 처리..
			byte[] sendPacket = new byte[packetData.length + 7];
			sendPacket[0] = (byte)0x02;
			System.arraycopy(CommonUtil.convert2TwoByte(packetData.length), 0, sendPacket, 1, 2);
			byte attr = PacketHeader.LAST;
			attr = Cer ? (byte)(attr+PacketHeader.CER) : attr;
			//ph.COMP
			//attr = Enc ? (byte)(attr+ph.ENC) : attr;
			sendPacket[3] = attr;
			sendPacket[4] = (byte)0x20; //sequence
			System.arraycopy(packetData, 0, sendPacket, 5, packetData.length);
			sendPacket[packetData.length + 5] = (byte)0x03;
			sendPacket[packetData.length + 6] = (byte)0x00;

			try {
				out.write(sendPacket);
				out.flush();
			}
			catch(IOException e) {
				if (isConnect) {
					isConnect = false;
					COMUtil.showMessage(COMUtil._chartMain, "서버와의 접속이 끊겼습니다. ");
					//_wts.stop();
					//Frame f = COMUtil.getParentFrame(_wts);
					//COMUtil._wts.dialogView("접속 해제", "서버와의 접속이 끊겼습니다. ", " 잠시후 다시 이용바랍니다.", 1, f);
					e.printStackTrace();
				}
			}
		} else {
			byte[] sendHeader = new byte[5];
			sendHeader[0] = (byte)0x02;
			//System.arraycopy(CommonUtil.convert2TwoByte(1017), 0, sendHeader, 0, 2);
			byte attr = (byte)0x00;
			//attr = Cer ? (byte)(attr+ph.CER) : attr;
			//ph.COMP
			attr = Enc ? (byte)(attr+PacketHeader.ENC) : attr;
			sendHeader[3] = attr;
			sendHeader[4] = (byte)0x00; //sequence

			int nPacketStart = 0;
			int nPacket = 4089;
			int packetDataLength = packetData.length;
			while( nPacket > 0 ) {
				byte[] sendPacket = new byte[nPacket+7];
				System.arraycopy(CommonUtil.convert2TwoByte(nPacket), 0, sendHeader, 1, 2);
				System.arraycopy(sendHeader, 0, sendPacket, 0, 5);
				System.arraycopy(packetData, nPacketStart, sendPacket, 5, nPacket);
				sendPacket[nPacket + 5] = (byte)0x03;
				sendPacket[nPacket + 6] = (byte)0x00;

				try {
					out.write(sendPacket);
					out.flush();
				}
				catch(IOException e) {
					//Frame f = COMUtil.getParentFrame(_wts);
					//COMUtil._wts.dialogView("접속 해제", "서버와의 접속이 끊겼습니다. ", " 잠시후 다시 이용바랍니다.", 1, f);
					// _wts.stop();
					COMUtil.showMessage(COMUtil._chartMain, "서버와의 접속이 끊겼습니다.  잠시후 다시 이용바랍니다.");
					e.printStackTrace();
				}

				if(nPacket==4088 || nPacket==4089) {
					nPacketStart = nPacketStart + nPacket;
					nPacket = packetDataLength - nPacketStart;
					if(nPacket > 4089) nPacket = 4089;
					else sendHeader[3] = (byte)(sendHeader[3]+PacketHeader.LAST);
				} else nPacket = -1;
			}
		}

		return;
	}

	public int readWORD(byte[] content) {	//2byte reading
		int returnInt = 0;

		returnInt = (returnInt << 8) | (content[1] & 0xFF);
		returnInt = (returnInt << 8) | (content[0] & 0xFF);
		return returnInt;
	}

	/**
	 //getNewTRCommTableIndex
	 @author alzioyes.
	 @date   2010/12/16
	 @brief TR통신에서  셋팅할 새로운WinID Index 요청
	 @param sTRKey
	 @return TR통신에서 사용할 WinID Index
	 */
	public int getNewTRCommTableIndex()
	{
		/// 예외처리 : 차트를 위해 0은 skip 하므로 1부터 시작.
		//for(int i=0; i<SIZE_TRUseTable; i++)
		for(int i=1; i<SIZE_TRUseTable; i++)
		{
			if(m_TRUseTable[i]==false)
			{
				m_TRUseTable[i] = true;
				return i;
			}
		}
		return -1;
	}

	/**
	 //setTRCommTableIndex
	 @author alzioyes.
	 @date   2010/12/16
	 @brief TR통신에서 사용한 WinID Index set.
	 @param nIndex : set할 WinID index.
	 bUseValue : set value.
	 @return 없음.
	 */
	public void setTRCommTableIndex(int nIndex, boolean bUseValue)
	{
		//int nIndex = Integer.parseInt(sTRKey);
		m_TRUseTable[nIndex] = bUseValue;
	}

	/**
	 //getTRCommTable
	 @author alzioyes.
	 @date   2010/12/16
	 @brief TR 결과를 리턴해줄 윈도우(Activity) 얻기
	 @param sTRKey
	 @return Activity : 키값에 해당하는 Acitivy
	 */
	public COMMCallback getTRCommTable(String sTRKey) {
		if(this.m_TRCommTable.containsKey(sTRKey)==false) return null;

		return this.m_TRCommTable.get(sTRKey);
	}

	/**
	 //setTRCommTable
	 @author alzioyes.
	 @date   2010/12/16
	 @brief TR 결과를 리턴해줄 윈도우(Activity) 등록
	 @param sTRKey
	 target  : 등록할  Activity
	 @return boolean : 성공여부(true/false)
	 */
	public boolean setTRCommTable(String sTRKey, COMMCallback target) {
		String sKey=sTRKey;
		this.m_TRCommTable.put(sKey, target);

		return true;
	}
}