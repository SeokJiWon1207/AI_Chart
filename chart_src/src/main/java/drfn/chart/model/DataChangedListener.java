package drfn.chart.model;
import java.util.EventListener;

public interface DataChangedListener extends EventListener{
    public void DataChanged(DataChangeEvent e);
    public void DataAdded(DataChangeEvent e);
    public void DataIndexChanged(DataChangeEvent e);
    public void DataInserted(DataChangeEvent e);
}