package drfn.chart.scale;

public class AREA{
	private float m_fLeft;
	private float m_fCenter;
	private float m_fRight;
	private float m_fWidth;
	public AREA(float fLeft, float fCenter, float fRight, float fWidth) {
		m_fLeft = fLeft;
		m_fCenter = fCenter;
		m_fRight = fRight;
		m_fWidth = fWidth;
	}
	
	public float getLeft()
	{
		return m_fLeft;
	}
	public float getCenter()
	{
		return m_fCenter;
	}
	public float getRight()
	{
		return m_fRight;
	}
	public float getRight_Tot()
	{
		return m_fLeft+m_fWidth;
	}
}
