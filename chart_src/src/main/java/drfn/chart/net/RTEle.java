package drfn.chart.net;

import java.util.Hashtable;
import java.util.Vector;

public class RTEle {    
    Hashtable<RealComp, Vector<int[]>> dataHash = new Hashtable<RealComp, Vector<int[]>>();    
    Vector<RealComp> keys = new Vector<RealComp>();
    public RTEle() {
    }
    
    public void setRTEle(RealComp c, int d1, int d2, int d3, int d4) {
        RealComp comp = c;        
        int[] depth = new int[4];
        depth[0] = d1;
        depth[1] = d2;
        depth[2] = d3;
        depth[3] = d4;
        Vector<int[]> depthV;
        if(!keys.contains(comp)) depthV = new Vector<int[]>();
        else depthV = (Vector<int[]>) dataHash.get(comp);
        depthV.addElement(depth);
        dataHash.put(comp, depthV);        
        if(!keys.contains(comp)) keys.addElement(comp);
    }    
    
    public Vector<RealComp> getKeys(){
        return keys;
    }
    
    public Vector<int[]> getDepthV(RealComp comp) {
        return (Vector<int[]>)dataHash.get(comp);
    }   
    
    public void setRTEle(RealComp comp, Vector<int[]> v) {
        dataHash.put(comp, v);
    }	
}