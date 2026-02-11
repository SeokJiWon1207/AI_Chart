package drfn.chart.comp;

import android.content.Context;
import android.view.View;

import drfn.chart.util.COMUtil;

public class ItemSelector extends View {
    String old_code="";
    boolean isReady = false;
    boolean isSelect = true;
    boolean isSOption = false;
    
    public ItemSelector(Context context) {
    	super(context);
    }

    public void setCode(String code) {
        if (code == null) return;
        else {
            handle(this, code);
        }
    }
    
    // handle must be overrided to implement CallerHandle
    public void handle(Object source, String code) {
        old_code=code;
        
        code = COMUtil.getCheckCode(code);
        code = COMUtil.getCode(code);

    }

}