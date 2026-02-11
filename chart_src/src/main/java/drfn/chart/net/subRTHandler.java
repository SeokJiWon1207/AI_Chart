package drfn.chart.net;

import java.util.Hashtable;
import java.util.Vector;

class subRTHandler {
    Hashtable<String, RTEle> compByDataKey = new Hashtable<String, RTEle>();       
    String key = "";
    byte[] data;    
    String dataName;    
    public subRTHandler(String name){
    }      
    
    public void addEle(String dKey, RealComp comp, int[] depth) {                 
        String dataKey = dKey.trim();        
        if(!compByDataKey.containsKey(dataKey)) {
            RTEle rtEle = new RTEle();
            rtEle.setRTEle(comp, depth[0], depth[1], depth[2], depth[3]);                        
            compByDataKey.put(dataKey, rtEle);
        } else {
            RTEle rtEle = (RTEle) compByDataKey.get(dataKey);
            rtEle.setRTEle(comp, depth[0], depth[1], depth[2], depth[3]);
            compByDataKey.put(dataKey, rtEle);
        }        
    }    
    
    public boolean checkHash(String dKey) {
        String dataKey = dKey.trim();
        if (compByDataKey.containsKey(dataKey)) return false;
        else return true;
    }
    
    public boolean deleteEle(String dKey,RealComp comp) { 
        String dataKey = dKey.trim();
        if (dataKey.equals("")) return false;
        if(!compByDataKey.containsKey(dataKey)) return false;
        else {
            RTEle rtEle = (RTEle) compByDataKey.get(dataKey);
            if (rtEle.dataHash.containsKey(comp)) {
                rtEle.dataHash.remove(comp);
                rtEle.keys.removeElement(comp);
                if (rtEle.dataHash.isEmpty()) {
                    compByDataKey.remove(dataKey);
                    return true;                    
                }
                else return false;
            } else return false;
        }
   }
    
    public synchronized void setData(byte[] data, String dataName, String k) {        
        this.data = data;
        this.key = k.trim();        
        this.dataName = dataName.trim();

        if (compByDataKey.containsKey(key)) {
            RTEle rtEle = (RTEle) compByDataKey.get(key);
            Vector<RealComp> v = rtEle.getKeys();
            int vLen = v.size();
            for(int c=0; c<vLen; c++){
                try {
                    RealComp comp = (RealComp) v.elementAt(c);
                    comp.repaintRT(dataName, data);

                }catch(Exception e) {
                    continue;
                }
            } 
        }   
    }
}

