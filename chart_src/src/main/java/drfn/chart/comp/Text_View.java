/**
 * 2012. 7. 5  drfnkimsh
 * TextView 커스텀입니다. 
 * */
package drfn.chart.comp;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Text_View extends RelativeLayout {

	Context c;
	RelativeLayout.LayoutParams lp = null;
	TextView tv;
	RelativeLayout rl;
//	String strTag;

	public Text_View(Context c)
	{
		super(c);
		this.c = c;
		lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		LayoutInflater inflater = LayoutInflater.from(getContext());
		int layoutResId = c.getResources().getIdentifier("textview", "layout", c.getPackageName());
		rl = (RelativeLayout)inflater.inflate(layoutResId, this);

		layoutResId = c.getResources().getIdentifier("textview", "id", c.getPackageName());
		tv = (TextView)rl.findViewById(layoutResId);

		setTextSize(9.0f);
		setGravity(Gravity.LEFT);
	}

	public void setText(CharSequence text)
	{
		if(tv!=null) tv.setText(text);
	}

	public void setMargins(int leftMargin, int topMargin)
	{
		lp.leftMargin = leftMargin;
		lp.topMargin = topMargin;

		this.setLayoutParams(lp);
	}

	public void setTextSize(float textsize)
	{
		if(tv!=null) tv.setTextSize(textsize);
	}

	public void setGravity(int gravity)
	{
		if(tv!=null) tv.setGravity(gravity);
	}
	public void setColor(int color)
	{
		if(tv!=null) tv.setTextColor(color);
	}
	public void setBackgroundColor(int bkcolor)
	{
		if(tv!=null) tv.setBackgroundColor(bkcolor);
	}

	public String getText()
	{
		if(tv==null) return "";
		return tv.getText().toString();
	}
	public int getTextWidth()
	{
		return tv.getWidth();
	}
	public void setTypeface(Typeface typeface)
	{
		if(tv!=null) tv.setTypeface(typeface);
	}
//	
//	public void setTag(String strTag)
//	{
//		this.strTag = strTag;
//	}
}
