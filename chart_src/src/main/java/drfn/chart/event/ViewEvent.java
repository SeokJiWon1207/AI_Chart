package drfn.chart.event;

public class ViewEvent extends java.util.EventObject{
	private static final long serialVersionUID = 1L;
	public static final int VIEW_INDEX_CHANGE=1;
    public static final int VIEW_NUM_CHANGE=2;
    public static final int VIEW_MODE_CHANGE=3;
    public static final int BACK_COLOR_CHANGE=4;
    
    
    Object item;
    int viewState;
    
    public ViewEvent(ViewChangable source, Object item, int viewState){
        super(source);
        this.item=item;
        this.viewState = viewState;
    }
    public ViewChangable getViewChangable(){
        return (ViewChangable)source;
    }
    public Object getItem(){
        return item;
    }
    public int getViewState(){
        return viewState;
    }
    public String paramString(){
        String s= null;
        switch(viewState){
            case VIEW_INDEX_CHANGE:
                s+="VIEW_INDEX_CHANGE";
            break;
            case VIEW_NUM_CHANGE:
                s+="VIEW_NUM_CHANGE";
            break;
            case VIEW_MODE_CHANGE:
                s+="VIEW_MODE_CHANGE";
            break;
            case BACK_COLOR_CHANGE:
                s+="BACK_COLOR_CHANGE";
            break;
            default:
            break;
        }
        return "[block="+s+"]";
    }
    
}