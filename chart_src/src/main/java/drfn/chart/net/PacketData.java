package drfn.chart.net;

import java.io.*;

public class PacketData {
	public byte command; //CommandData(1Byte)
	public String windowID; //WindowHandle(4Byte)
	public String trCode;   //TRCODE(5Byte)
	public String messageCode1; //MessageCode(6Byte)
	public String message;  //Message(82Byte)
	public int errorPosition;   //CursorPosition(2Byte)
	public int messageCode2Length;  //보조메시지 길이(3Byte)
	public int apDataLength;    //AP 데이터 길이(3Byte)
	//public String trData; //TR In/Out 데이터 영역
	public byte[] trData; //TR In/Out 데이터 영역
	public byte[] realData;
	public byte[] compData; //압축된 데이터

	public PacketData() {
		errorPosition = 0;
		messageCode2Length = 0;
		apDataLength = 0;
	}

	/**PacketData의 멤버변수들을 setting하는 함수
	 */
	public void addData(byte[] data) {
		if(data==null || data.length<0) return;

		//가지고 있는 데이터를 임시로 temp[]배열에 저장
		byte[] temp=trData;
		//if(temp!=null)System.out.println(" temp : " + new String(temp));

		//새로 받은 바이트 배열의 크기만큼 늘려서 새로운 배열을 만든다
		trData = new byte[temp.length+data.length];
		//기존의 데이터가 있다면 trdata에 기존의 데이터를 옮겨 저장
		if(temp!=null)System.arraycopy(temp, 0, trData, 0, temp.length);
		//새로운 데이터를 저장한다
		System.arraycopy(data, 0, trData, temp.length, data.length);
	}
	public void setData(byte[] data) {
		trData = data;
	}
	public void addCompData(byte[] data) {
		if(data==null || data.length<0) return;
		byte[] temp=compData;
		compData = new byte[temp.length+data.length];
		if(temp!=null)System.arraycopy(temp, 0, compData, 0, temp.length);
		System.arraycopy(data, 0, compData, temp.length, data.length);
	}
	public void setCompData(byte[] data) {
		compData = data;
	}

	public byte[] getCompData() {
		return compData;
	}
	public boolean parse(byte[] data) {
		if(data==null || data.length<=0) return false;
		//System.out.println("parseData:"+new String(data));
		command = data[0];
		if(command==0x20) {
			int size = Integer.parseInt(new String(data, 1, 4));
			trData = new byte[size];
			System.arraycopy(data, 5, trData, 0, size);
		}
		else if(command==0x40){// 일반 TR Data
			windowID = new String(data, 1, 4);
			trCode = new String(data, 5, 5);
			messageCode1 = new String(data, 10, 6);

			if(trCode.equals("08000")) { //Login TR
				trData = new byte[data.length-106];
				System.arraycopy(data, 106, trData, 0, data.length-106);
				return true;
			}

			if(trCode.equals("08320")) {    //관심종목Down TR
				trData = new byte[data.length-106];
				System.arraycopy(data, 106, trData, 0, data.length-106);
				return true;
			}

			try {
				message = new String(data, 16, 82, "KSC5601");

			}
			catch(UnsupportedEncodingException e) {
				System.out.println(e);
			}

			try { errorPosition = Integer.parseInt(new String(data, 98, 2)); }
			catch(NumberFormatException e) { errorPosition = 0; }

			try { messageCode2Length = Integer.parseInt(new String(data, 100, 3)); }
			catch(NumberFormatException e) { messageCode2Length = 0; }

			try { apDataLength = Integer.parseInt(new String(data, 103, 3)); }
			catch(NumberFormatException e) { apDataLength = 0; }

			int extraLength = messageCode2Length + apDataLength;
			trData=new byte[data.length-106-extraLength];
			System.arraycopy(data, 106 + extraLength, trData, 0, data.length-106-extraLength);
		} else if(command==0x5B){// Real(SVR -> DRDS)  **eun
			int length = Integer.parseInt(new String(data, 1, 4));
			realData = new byte[length];
			System.arraycopy(data, 5, realData, 0, length);
		} else if(command==0x5C){// 리얼데이터를 멀티로 수신 2001.11.16 nykim
			int length = data.length - 1;
			realData = new byte[length];
			System.arraycopy(data, 1, realData, 0, length);
		} else if(command==0x3A ||command==0x3B){// 리얼데이터를 멀티로 수신 2001.11.16 nykim
			int length = data.length - 1;
			trData = new byte[length];
			System.arraycopy(data, 1, trData, 0, length);
		}

		return true;
	}

	public String toString() {
		StringBuffer sb=new StringBuffer();
		if(command==0x40){// 일반 TR Data
			sb.append(windowID); //WindowHandle
			sb.append(trCode);
			sb.append(messageCode1);
			sb.append(message);
			sb.append(String.valueOf(errorPosition+100).substring(1)); //두자리 숫자로 저장..
			sb.append(String.valueOf(messageCode2Length+1000).substring(1)); // Length
			sb.append(String.valueOf(apDataLength+1000).substring(1));
			sb.append(trData);
		} else if(command==0x5B) {
			sb.append(trCode);
		}
		return new String(sb);
	}

	/**퍼포먼스 향상을 위해..2001/05/09 명임수정*/
	public byte[] getBytes() {
		//return toString().getBytes();
		String s= toString();
		return CommonUtil.asciiGetBytes(s);
	}

}