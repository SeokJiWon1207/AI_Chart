package drfn.piechart.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


public class PieViewPanel extends View {
    View pieViewPanel;
    Context m_context;

    TextView tv_sub_title;
    TextView tv_sub_value;
    public PieViewPanel(Context context) {
        super(context);

        this.m_context = context;
        setUI();
    }

    private void setUI() {
        LayoutInflater factory = LayoutInflater.from(m_context);
        int layoutResId = this.getContext().getResources().getIdentifier("pieviewpanel", "layout", this.getContext().getPackageName());
        pieViewPanel = factory.inflate(layoutResId, null);

        layoutResId = this.getContext().getResources().getIdentifier("tv_sub_title", "id", this.getContext().getPackageName());
        tv_sub_title = (TextView) pieViewPanel.findViewById(layoutResId);

        layoutResId = this.getContext().getResources().getIdentifier("tv_sub_value", "id", this.getContext().getPackageName());
        tv_sub_value = (TextView) pieViewPanel.findViewById(layoutResId);
    }
    public void setSubTitle(String title) {
        tv_sub_title.setText(title);
    }
    public void setSubValue(String value) {
        tv_sub_value.setText(value);
    }
}
