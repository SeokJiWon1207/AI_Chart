package drfn.chart.net;

public class NeoCompress {
    private final static byte DIGIT_COMP_MARK = (byte)0xFF;
    
    public NeoCompress() {
    }
    
    public static byte[] UnCompress(byte[] sour, int size){
	    int i = 0, count = 0;
	    byte code, hcode, lcode;
        
        byte[] dest = new byte[size*2];
        while(i < size) {
            if(sour[i] == DIGIT_COMP_MARK) {
                i++;
                if(i>=size) break;
                if(sour[i] == DIGIT_COMP_MARK) {
                    dest[count++] = DIGIT_COMP_MARK;
                    i++;
                    continue;
                }
                
			    while (i < size) {
				    if (sour[i] == DIGIT_COMP_MARK) {
					    i++;
					    break;
				    }
				    code = sour[i++];
				    hcode = (byte)(code >> 4);  hcode = (byte)(hcode & 0x0F);   
				    lcode = (byte)(code & 0x0F);
				    hcode -= 0x01;
				    code = DecodeDigit(hcode);
				    dest[count++] = code;
				    code = DecodeDigit(lcode);
				    if (code != 0) {
					    dest[count++] = code;
				    }
			    }
            }
		    else {
			    dest[count++] = sour[i++];
		    }
        }//end of while
        byte[] ret = new byte[count];
        System.arraycopy(dest, 0, ret, 0, count);
        return ret;
    }
    
    private static byte DecodeDigit(byte code) {
	    switch (code)	{
		    case 0x0A : return (byte)' ';
		    case 0x0B : return (byte)',';
		    case 0x0C : return (byte)'.';
		    case 0x0D : return (byte)'-';
		    case 0x0E : return (byte)'+';
		    default	  : return (byte)(code | 0x30);
	    }
	    //return 0;
	}
}