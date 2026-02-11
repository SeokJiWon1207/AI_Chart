package drfn.chart.model;

public class DataChangeEvent extends java.util.EventObject{
	private static final long serialVersionUID = 1L;
	public static final int INDEX_CHANGE=1;
    public static final int NEWDATA_CHANGE=2;
    public static final int ADDDATA_CHANGE=3;
    public static final int INSERTDATA_CHANGE=4;
    
    Object item;
    int dataState;
    
    public DataChangeEvent(DataChangable source, Object item, int dataState){
        super(source);
        this.item=item;
        this.dataState = dataState;
    }
    public DataChangable getDataChangable(){
        return (DataChangable)source;
    }
    public Object getItem(){
        return item;
    }
    public int getDataState(){
        return dataState;
    }
    public String paramString(){
        String s= null;
        switch(dataState){
            case INDEX_CHANGE:
                s+="INDEX_CHANGE";
            break;
            case NEWDATA_CHANGE:
                s+="NEWDATA_CHANGE";
            break;
            case ADDDATA_CHANGE:
                s+="ADDDATA_CHANGE";
            break;
            case INSERTDATA_CHANGE:
                s+="INSERTDATA_CHANGE";
            break;
            default:
            break;
        }
        return "[block="+s+"]";
    }
    
}