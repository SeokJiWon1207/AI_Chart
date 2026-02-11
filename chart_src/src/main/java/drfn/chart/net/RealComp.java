package drfn.chart.net;

public interface RealComp {
    public void regRT();
    public void deregRT();
    public void addData(boolean b);
    public void repaintRT(String name, byte[] rData);    
}