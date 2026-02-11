package drfn.chart.model;

public interface DataChangable{
    public void addDataChangedListener(DataChangedListener l);
    public void removeDataChangedListener(DataChangedListener l);
}
