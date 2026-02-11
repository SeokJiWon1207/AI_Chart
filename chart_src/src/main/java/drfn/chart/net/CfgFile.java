package drfn.chart.net;

import java.util.*;

public class CfgFile {
    String realName;
    Hashtable<String, String> realField;
    int keyLen;

    public CfgFile() {
    }

    /** CfgFile
     *    (String) realName
     *    (Hashtable) realField
     *                    key  :  String field
     *                    value  :  info(oneSize/totSize)
     *                           field에 해당하는 data를 얻고자 할 때 totSize부터 oneSize 만큼 읽는다.
     */
    public CfgFile(String realName) {
        this.realName = realName;
        realField = new Hashtable<String, String>();
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getRealName() {
        return realName;
    }

    public void setField(String field, String info) {
        realField.put(field, info);
    }

    public void setRealKeyLen(int keyLen) {
        this.keyLen = keyLen;
    }

    public int getRealKeyLen() {
        return keyLen;
    }
}