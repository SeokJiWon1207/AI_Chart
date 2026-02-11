package drfn.chart.event;

public class ChartEvent extends java.util.EventObject{
	private static final long serialVersionUID = 1L;
	public static final int CHART_ADD=1;
    public static final int CHART_REMOVE=2;
    public static final int CHART_INIT=3;
    public static final int CHART_TOOLBAR_DONE=4;
    
    Object item;
    int chartState;
    String param="";
    public ChartEvent(ChartChangable source, Object item, int chartState,String param){
        super(source);
        this.item=item;
        this.chartState = chartState;
        this.param = param;
    }
    public ChartChangable getChartChangable(){
        return (ChartChangable)source;
    }
    public Object getItem(){
        return item;
    }
    public int getChartState(){
        return chartState;
    }
    public String getPrameterString(){
        return param;
    }
    public String getActionCommand(){
        String s= null;
        switch(chartState){
            case CHART_ADD:
                s+="CHART_ADD";
            break;
            case CHART_REMOVE:
                s+="CHART_REMOVE";
            break;
            case CHART_INIT:
                s+="CHART_INIT";
            break;
            case CHART_TOOLBAR_DONE:
                s+="CHART_TOOLBAR_DONE";
            break;
            default:
            break;
        }
        return "[block="+s+"]";
    }
    
}