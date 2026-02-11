package drfn.chart.net;

public class PacketHeader {
	public static final byte COMP = 0x01;
	public static final byte ENC = 0x02;
	public static final byte LAST = 0x04;
	public static final byte CER = 0x10;
	
	public int stx;
	public int length;
	public boolean comp;
	public boolean enc;
	public boolean last;
	public int sequence;
	public int etx;
	public int bcc;
	
	public PacketHeader() {
		stx = 0x02;
		length = 0;
		comp = false;
	}
    
	public int parse(byte[] data) {
	    if(data==null || data.length<5) return -1;
		stx = Integer.parseInt(Byte.toString(data[0]));
		length = CommonUtil.bytetoInt(data[1])*256 + CommonUtil.bytetoInt(data[2]);
		
		comp = ((data[3] & COMP) == COMP) ? true : false;
		enc = ((data[3] & ENC) == ENC) ? true : false;
		last = ((data[3] & LAST) == LAST) ? true : false;
		sequence = Integer.parseInt(Byte.toString(data[4]));
		return length;
	}
	
	public String toString() {
		return new String();
	}
	
	public byte[] getBytes() {
		return toString().getBytes();
	}
}