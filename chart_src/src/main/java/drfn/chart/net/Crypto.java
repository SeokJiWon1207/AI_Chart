package drfn.chart.net;


public class Crypto
{

    public Crypto()
    {
    }

    public String Change(String s, String s1)
    {
        String s2 = "";
        int l = 0;
        int i = s.length();
        for(int j = s1.length(); l < j; l++)
        {
            int k = l % i;
            char c = s.charAt(k);
            char c1 = s1.charAt(l);
            int i1 = c ^ c1;
            s2 = s2 + (char)i1;
        }

        return s2;
    }

    public String Change(String s)
    {
        String s1 = "lgstock";
        String s2 = "";
        int l = 0;
        int i = s1.length();
        for(int j = s.length(); l < j; l++)
        {
            int k = l % i;
            char c = s1.charAt(k);
            char c1 = s.charAt(l);
            int i1 = c ^ c1;
            s2 = s2 + (char)i1;
        }

        return s2;
    }

    public void Change(byte abyte0[], byte abyte1[])
    {
        int l = 0;
        int i = abyte0.length;
        for(int j = abyte1.length; l < j; l++)
        {
            int k = l % i;
            byte byte0 = abyte0[k];
            byte byte1 = abyte1[l];
            int i1 = byte0 ^ byte1;
            abyte1[l] = (byte)i1;
        }

    }

    public void Change(byte abyte0[])
    {
        byte abyte1[] = {
            108, 103, 115, 116, 111, 99, 107
        };
        int l = 0;
        int i = abyte1.length;
        for(int j = abyte0.length; l < j; l++)
        {
            int k = l % i;
            byte byte0 = abyte1[k];
            byte byte1 = abyte0[l];
            int i1 = byte0 ^ byte1;
            abyte0[l] = (byte)i1;
        }

    }
}
