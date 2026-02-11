package drfn.chart.base;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import drfn.chart.util.COMUtil;

public class ToolBarSetListAdapter extends BaseAdapter {

	ImageView m_oCopyImg;

	ListView m_oParent;

	WindowManager m_oWindowMgr;
	WindowManager.LayoutParams m_oWinParam;

	ArrayList<ToolbarItem> m_arrDatas;
	int m_nMovingIdx;
	int m_nTargetIdx;

	int m_nUpperBound = 0;
	int m_nLowerBound = 0;

	Rect m_oRectParent = new Rect();
	Context m_context;

	public ToolBarSetListAdapter(ListView parent)
	{
		m_oParent = parent;
		m_context = COMUtil.apiView.getContext();
		m_oWindowMgr = (WindowManager) m_context.getSystemService("window");
		//m_oWindowMgr = (WindowManager) HMSmartActivity.getInstance().getSystemService("window");	
	}

	public void setData(ArrayList<ToolbarItem> data){
		m_arrDatas = data;
	}

	public void setData(int pos, ToolbarItem data){
		m_arrDatas.set(pos, data);
	}

	public ArrayList<ToolbarItem> getData()
	{
		return m_arrDatas;
	}

	public void deleteData(int pos){
		m_arrDatas.remove(pos);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return m_arrDatas==null?0:m_arrDatas.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		final ToolBarSetAniListItem m_oItemAni;
		TextView tv;
		ImageView iv;
		ImageView iconImage;
		final int nPos = position;

		if(v == null){
			m_oItemAni = new ToolBarSetAniListItem(m_context);
		}else{
			m_oItemAni = (ToolBarSetAniListItem) v;
		}

		if( nPos % 2 == 0 )
			m_oItemAni.setBackgroundColor(Color.rgb(241, 241, 241));
		else
			m_oItemAni.setBackgroundColor(Color.TRANSPARENT);

		m_oItemAni.setId(nPos);

		iconImage = (ImageView)m_oItemAni.getFrontView(0);
		tv = (TextView)m_oItemAni.getFrontView(1);
		iv = (ImageView)m_oItemAni.getFrontView(2);

		iv.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();

//				Log.e("IntrGroupAdapter", "==== imageview action:"+action);
				switch(action){
					case MotionEvent.ACTION_DOWN:
						m_isMoving = false;
						showMovingLayout(v, nPos);
						break;
				}
				return m_isMoving;
			}
		});

		m_oParent.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();
				final int y = (int) event.getY();
				final int x = (int) event.getX();
				if(m_isMoving){
					if( m_oWinParam != null )
						m_oWinParam.y = (int) event.getRawY();

					switch(action){
						case MotionEvent.ACTION_MOVE:
							doMoving(x, y);
							break;
						case MotionEvent.ACTION_OUTSIDE:
						case MotionEvent.ACTION_UP:
						case MotionEvent.ACTION_CANCEL:
							changePos();
							closeMove();
							break;
					}
				}
				return m_isMoving;
			}
		});

		if(m_arrDatas!=null&&m_arrDatas.size()!=0)
			if(position<m_arrDatas.size()){
				ToolbarItem itemCode = m_arrDatas.get(position);
				tv.setText(itemCode.getTitle());

				int nRes = m_context.getResources().getIdentifier(
						"i_option_" + itemCode.getImage(), "drawable", m_context.getPackageName());
				iconImage.setBackgroundResource(nRes);

			}

		return m_oItemAni;
	}

	public void changePos(){
		COMUtil._chartMain.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if( m_nTargetIdx < 0 )	m_nTargetIdx = m_arrDatas.size() - 1;
				if(m_nTargetIdx >= 0 && m_nMovingIdx >= 0){
					if(m_nTargetIdx < m_arrDatas.size() && m_nMovingIdx < m_arrDatas.size()){
						ToolbarItem mData = m_arrDatas.get(m_nMovingIdx);

						if(m_nTargetIdx<m_nMovingIdx){
							m_arrDatas.add(m_nTargetIdx, mData);
							m_arrDatas.remove(m_nMovingIdx+1);
						}else if(m_nTargetIdx>m_nMovingIdx){
							m_arrDatas.add(m_nTargetIdx+1, mData);
							m_arrDatas.remove(m_nMovingIdx);
						}

						notifyDataSetChanged();
					}
				}
			}
		});

	}

	public void doMoving(int x, int y){
		if(m_oWinParam == null) return;

		if(m_oRectParent.top >= m_oWinParam.y){
			m_oWinParam.y = m_oRectParent.top;
		}
		else if(m_oWinParam.y >= m_oRectParent.top + m_oRectParent.height() - m_oWinParam.height){
			m_oWinParam.y = m_oRectParent.top + m_oRectParent.height() - m_oWinParam.height;
		}
		else{
		}

		//상단
		int tIdx = m_oParent.pointToPosition(x, m_oWinParam.y - m_oRectParent.top);
		//하단
//		int bIdx = m_oParent.pointToPosition(x, m_oWinParam.y + m_oWinParam.height - m_oRectParent.top);

		adjustScrollBounds(y);

		int speed = 0;
		if( y > m_nLowerBound )
			speed = y > (m_oRectParent.height() + m_nLowerBound) / 2 ? 16 : 4;
		else if( y < m_nUpperBound )
			speed = y < m_nLowerBound / 2 ? -16 : -4;

		if( speed != 0 ) {
			View view = m_oParent.getChildAt(tIdx - m_oParent.getFirstVisiblePosition());
			if( view != null ) {
				int pos = view.getTop();
				m_oParent.setSelectionFromTop(tIdx, pos - speed);
			}
		}

		m_nTargetIdx = tIdx;

		COMUtil._chartMain.runOnUiThread(new Runnable(){

			@Override
			public void run() {
				m_oWindowMgr.updateViewLayout(m_oCopyImg, m_oWinParam);

			}});
	}

	public void adjustScrollBounds(int y)
	{
		if( y >= m_oRectParent.height() / 4 )
			m_nUpperBound = m_oRectParent.height() / 4;

		if( y <= m_oRectParent.height() * 3 / 4 )
			m_nLowerBound = m_oRectParent.height() * 3 / 4;
	}

	public void closeMove(){

		if(m_oWindowMgr != null && m_oCopyImg != null)
			m_oWindowMgr.removeView(m_oCopyImg);
		m_oCopyImg = null;
		if(m_oParent!=null)
			m_oParent.requestFocus();
		m_isMoving = false;
	}

	boolean m_isMoving = false;

	///이동될 리스트 Row 설정
	public void showMovingLayout(View iv, int pos){
		if(!m_isMoving)
		{
			if(m_oWindowMgr!=null){
				if(m_oCopyImg==null){
					m_oCopyImg = new ImageView(m_context);
				}else{
					if(m_oCopyImg.getParent()!=null){
						m_oWindowMgr.removeView(m_oCopyImg);
					}
				}

				m_nMovingIdx = pos;
				m_nTargetIdx = pos;
				notifyDataSetChanged();



				ToolBarSetAniListItem showLay = (ToolBarSetAniListItem)iv.getParent().getParent().getParent();
//				showLay.setDrawingCacheEnabled(true);
				showLay.buildDrawingCache();


//				Bitmap bitmap = showLay.getDrawingCache().copy(Config.ARGB_8888, true);
				Bitmap bitmap = Bitmap.createBitmap(showLay.getDrawingCache());
				showLay.destroyDrawingCache();
				m_oCopyImg.setColorFilter(Color.argb(100, 0, 0, 0));
				m_oCopyImg.setImageBitmap(bitmap);

				m_oCopyImg.setOnTouchListener(new OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						// TODO Auto-generated method stub
						closeMove();
						return true;
					}
				});

				m_oWinParam = new WindowManager.LayoutParams();

				//ListView의 Global 위치를 구함
				m_oRectParent.setEmpty();
				m_oParent.getGlobalVisibleRect(m_oRectParent);

				//선택된 Row의 Global 위치를 구함
				Rect rect = new Rect();
				showLay.getGlobalVisibleRect(rect);

				m_oWinParam.x = rect.left;
				m_oWinParam.y = rect.top;

				m_oWinParam.width = showLay.getWidth();
				m_oWinParam.height = showLay.getHeight();
				m_oWinParam.gravity = Gravity.LEFT|Gravity.TOP;
				m_oWinParam.format = PixelFormat.TRANSLUCENT;
				m_oWinParam.alpha = 100;

				m_oWindowMgr.addView(m_oCopyImg, m_oWinParam);

				m_isMoving = true;

			}
		}
	}
}