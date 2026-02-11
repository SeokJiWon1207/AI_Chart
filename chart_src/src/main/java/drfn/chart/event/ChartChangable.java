package drfn.chart.event;

public interface ChartChangable{
    public void addChartChangedListener(ChartChangedListener l);
    public void removeChartChangedListener(ChartChangedListener l);
}