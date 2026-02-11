package drfn.chart.event;
import java.util.EventListener;

public interface ViewChangedListener extends EventListener{
    public void ViewIndexChanged(ViewEvent e);
    public void ViewNumChanged(ViewEvent e);
    public void ViewModeChanged(ViewEvent e);
    public void ViewBackColorChanged(ViewEvent e);
}