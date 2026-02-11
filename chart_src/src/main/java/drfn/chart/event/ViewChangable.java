package drfn.chart.event;

public interface ViewChangable{
    public void addViewChangedListener(ViewChangedListener l);
    public void removeViewChangedListener(ViewChangedListener l);
}