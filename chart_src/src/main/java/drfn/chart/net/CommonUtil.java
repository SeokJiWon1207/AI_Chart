package drfn.chart.net;
public class CommonUtil {
	
	public CommonUtil() {
	}

	public static void fillZero(byte[] b, int offset) {
		for(int i = offset; i < b.length; i++)
			b[i] = (byte)'0';
	}
	
	public static String fillZero(String s, int size) {
	    StringBuffer sb = new StringBuffer(s);
	    for(int i = 0; i < size - s.length(); i++)
	        sb.append('0');
	        
	    return sb.toString();
	}
	
	public static void fillSpace(byte[] b, int offset) {
		for(int i = offset; i < b.length; i++)
			b[i] = (byte)' ';
	}

	public static String fillSpace(String s, int size) {
	    StringBuffer sb = new StringBuffer(s);
		for(int i = 0; i < size - s.length(); i++) {
			sb.append(' ');
		}
		return sb.toString();
	}
	
  public static void concatebyte(byte[] a, byte[] b, int offset) {
  		for(int i = 0; i < b.length; i++) {
  			a[offset+i] = b[i];
  		}
  }
  
  public static byte[] convert2TwoByte(int value) {
  		byte[] b = new byte[2];
  		
  		b[0] = (byte)(value/256);
  		b[1] = (byte)(value%256);
  		
  		return b;
  }

    public static int bytetoInt(byte b) { // 1byte reading
	    int returnInt = (0x00 << 8) | (b & 0xFF);
	    return returnInt;
	}
	public static byte[] asciiGetBytes(String buf){
	    int size = buf.length();
	    int i;
	    byte[] bytebuf = new byte[size];
	    for(i=0;i<size;i++){
	        bytebuf[i] = (byte)buf.charAt(i);
	    }
	    return bytebuf;
	}

}