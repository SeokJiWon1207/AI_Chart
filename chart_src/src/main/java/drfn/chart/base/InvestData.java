package drfn.chart.base;

import android.graphics.Rect;

public class InvestData {
    
	public InvestData()
	{
		m_nType = -1;
	}
	InvestData( String strTitle, int nType, int nValue, Rect rect, int nRadius)
	{
		m_strTitle = strTitle;
		m_nType = nType;
		m_nValue = nValue;
		m_rectItemArea = rect;
		m_nRadius = nRadius;
	}

	public String m_strTitle;
	int m_nType;
	int m_nValue;
	int m_nRadius;
	Rect m_rectItemArea;
}
