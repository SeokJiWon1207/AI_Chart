package drfn.chart.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;


public class ToolBarEditDragItem extends PopupWindow
{
	private int				m_nWidth;
	private int				m_nHeight;

	private ImageView		m_viewDrag;
	private Bitmap			m_bitmapDrag;

	public ToolBarEditDragItem(Context context, Bitmap bitmapDrag, int nWidth, int nHeight)
	{
		m_nWidth = nWidth;
		m_nHeight = nHeight;

		m_viewDrag = new ImageView(context);
		m_viewDrag.setImageBitmap(bitmapDrag);
		m_viewDrag.setVisibility(View.VISIBLE);
		m_bitmapDrag = bitmapDrag;

		setContentView(m_viewDrag);
		setClippingEnabled(false);

		setWindowLayoutMode(m_nWidth, m_nHeight);
	}

	public void showDrag(View parent, int nPosX, int nPosY)
	{
		//if(nPosX < 0) nPosX = 0;
		//if(nPosY < 0) nPosY = 0;

		//Log.d("여기", "시작 : " + nPosX + "," + nPosY);

		showAtLocation(parent, Gravity.TOP | Gravity.LEFT, nPosX, nPosY);
		update(nPosX, nPosY, m_nWidth, m_nHeight);
	}

	public void moveDrag(int nPosX, int nPosY)
	{
		//if(nPosX < 0) nPosX = 0;
		//if(nPosY < 0) nPosY = 0;

		//Log.d("여기", "이동 : " + nPosX + "," + nPosY);

		update(nPosX, nPosY, m_nWidth, m_nHeight);
	}

	public void hideDrag()
	{
		dismiss();

		if(m_viewDrag != null)
		{
			m_viewDrag.setImageDrawable(null);
			m_viewDrag = null;
		}

		if(m_bitmapDrag != null)
		{
			m_bitmapDrag.recycle();
			m_bitmapDrag = null;
		}
	}
}