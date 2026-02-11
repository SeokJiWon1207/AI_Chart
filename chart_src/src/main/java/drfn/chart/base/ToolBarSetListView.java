package drfn.chart.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Vector;

import drfn.chart.util.COMUtil;

public class ToolBarSetListView extends ListView
		implements OnClickListener, OnLongClickListener, OnTouchListener
{

	//private ArrayList<IntrEditItem>		m_arrayItems;
	private ArrayList<ToolbarItem> m_arrayItems;
	private IntrItemAdapter				m_adapterList;

	private int m_nItemHeight = (int)COMUtil.getPixel(56);

	private boolean	m_isDragging;
	private Point	m_ptLastTouchPos = new Point();

	private int		m_nDragItemIndex;
	private int		m_nDragStartIndex;

	private int		m_nDragPointX;
	private int		m_nDragPointY;
	private int		m_nGlobalPointOffsetY;
	private int		m_nGlobalPointOffsetX;
	private Rect	m_rectDragTemp = new Rect();
	private Point	m_ptDragStartPos = new Point();

	private ToolBarEditDragItem	m_viewDrag;
	private final int m_nScaledTouchSlop;

	private int		m_nScrollUpperBound;
	private int		m_nScrollLowerBound;

	public ArrayList<CheckBox> arCheckBox = new ArrayList<CheckBox>();

	public ToolBarSetListView(Context context)
	{
		super(context);

		m_nScaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

		m_arrayItems = new ArrayList<ToolbarItem>();

		m_adapterList = new IntrItemAdapter();

		setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		setAdapter(m_adapterList);
		setChoiceMode(ListView.CHOICE_MODE_NONE);
		setBackgroundColor(Color.TRANSPARENT);
		setHorizontalScrollBarEnabled(false);
		setCacheColorHint(Color.TRANSPARENT);
		setScrollingCacheEnabled(false);
		setScrollBarStyle(ScrollView.SCROLLBARS_INSIDE_OVERLAY);
//		int	layoutResId = context.getResources().getIdentifier("img_line4", "drawable", context.getPackageName());
//		setDivider(getResources().getDrawable(layoutResId));
		setDivider(new ColorDrawable(Color.rgb(229, 229, 229)));
		setDividerHeight(1);
	}

	public void releaseView()
	{
		//setAdapter(null);
		for(int k = 0; k < getChildCount(); k++)
		{
			View child = getChildAt(k);
			if(child == null || !(child instanceof ToolbarItemView)) continue;

			((ToolbarItemView)child).releaseView();
		}

		if(m_arrayItems != null)
		{
			m_arrayItems.clear();
			m_arrayItems = null;
		}
	}

	public void setData(ArrayList<ToolbarItem> data)
	{
		//if(m_isListOnly) m_isEditable = false;

		if(m_arrayItems != null)
		{
			m_arrayItems.clear();


			m_arrayItems = data;
		}

		if(m_adapterList != null)
		{
			m_adapterList.notifyDataSetChanged();
		}
	}

	public ArrayList<ToolbarItem> getData()
	{
		return m_arrayItems;
	}

	public void updateData()
	{
		if(m_adapterList != null)
		{
			m_adapterList.notifyDataSetChanged();
		}
	}

	public void swapItems(int nSrcIndex, int nDstIndex)
	{
		if(nSrcIndex == nDstIndex) return;

		if(nSrcIndex < nDstIndex)
		{
			ToolbarItem item2 = m_arrayItems.remove(nDstIndex);

			ToolbarItem item1 = m_arrayItems.remove(nSrcIndex);

			m_arrayItems.add(nSrcIndex, item2);

			m_arrayItems.add(nDstIndex, item1);
		} else {
			ToolbarItem item2 = m_arrayItems.remove(nSrcIndex);

			ToolbarItem item1 = m_arrayItems.remove(nDstIndex);

			m_arrayItems.add(nDstIndex, item2);

			m_arrayItems.add(nSrcIndex, item1);
		}
	}

//	public String getItemCode(int nIndex)
//	{
//		if(nIndex < 0 || nIndex >= m_arrayItems.size()) return "";
//		
//		return m_arrayItems.get(nIndex).m_infoItem.GetItemCode();
//	}

	public int getRealItemCount()
	{
		if(m_arrayItems == null) return 0;

		return m_arrayItems.size();
	}

	public void moveTop(Vector<Integer> vecIndex)
	{
		int nOffset = 0;

		for(int nIndex : vecIndex)
		{
			ToolbarItem infoItem = m_arrayItems.remove(nIndex + nOffset);
			m_arrayItems.add(0, infoItem);

			nOffset++;
		}

		m_adapterList.notifyDataSetChanged();

		smoothScrollToPosition(0);
	}

	public void moveBottom(Vector<Integer> vecIndex)
	{
		int nInsIndex = m_arrayItems.size() - 1;

		for(int nIndex : vecIndex)
		{
			ToolbarItem infoItem = m_arrayItems.remove(nIndex);
			m_arrayItems.add(nInsIndex--, infoItem);
		}

		m_adapterList.notifyDataSetChanged();

		smoothScrollToPosition(m_arrayItems.size());
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event)
	{
		int action = event.getAction();

		if(m_ptLastTouchPos != null)
			m_ptLastTouchPos.set((int)event.getX(), (int)event.getY());

		if(m_isDragging)
		{
			switch(action)
			{
				case MotionEvent.ACTION_UP :

					stopDrag();

					return true;

				case MotionEvent.ACTION_CANCEL :

					stopDrag();

					return true;

				case MotionEvent.ACTION_MOVE :
					moveDrag((int)event.getX(), (int)event.getY());

					return false;
			}
		}

		return super.dispatchTouchEvent(event);
	}

	public void startDrag(int nItemIndex)
	{
		if(m_isDragging) return;

		Point ptDragStart = m_ptLastTouchPos;

		int arrLocation[] = new int[2];
		getLocationOnScreen(arrLocation);
		//ptDragStart.offset(-arrLocation[0], -arrLocation[1]);

		int nItemPos = pointToPosition(ptDragStart.x, ptDragStart.y);
		//Log.d("임시", "startDrag - " + nItemIndex + "," + nItemPos + "," + ptDragStart);
		if(nItemPos == AdapterView.INVALID_POSITION) return;

		View viewDragger = (View)getChildAt(nItemPos - getFirstVisiblePosition()); // 드래그 아이템
		if(!(viewDragger instanceof ToolbarItemView)) return;

		m_isDragging = true;
		m_ptDragStartPos.set(ptDragStart.x, ptDragStart.y);

		m_nDragPointX = ptDragStart.x - viewDragger.getLeft();
		m_nDragPointY = ptDragStart.y - viewDragger.getTop();

		m_nGlobalPointOffsetX = arrLocation[0];
		m_nGlobalPointOffsetY = arrLocation[1];

		((ToolbarItemView)viewDragger).getDrawingRect(m_rectDragTemp);

		Bitmap bitmapDrag = ((ToolbarItemView)viewDragger).getDragDrawingCache();

		try {
			m_viewDrag = new ToolBarEditDragItem(getContext(), bitmapDrag, m_rectDragTemp.width(), m_rectDragTemp.height());


			int x = ptDragStart.x - m_nDragPointX + m_nGlobalPointOffsetX;
			int y = ptDragStart.y - m_nDragPointY + m_nGlobalPointOffsetY;

			m_viewDrag.showDrag(COMUtil.apiView, x, y);

			m_nDragStartIndex = m_nDragItemIndex = nItemPos;

			m_adapterList.notifyDataSetChanged();

			m_nScrollUpperBound = Math.min(ptDragStart.y - m_nScaledTouchSlop, getHeight() / 3);
			m_nScrollLowerBound = Math.max(ptDragStart.y + m_nScaledTouchSlop, getHeight() * 2 / 3);

		} catch (Exception e) {
//			e.printStackTrace();
			return;
		}

	}

	public void moveDrag(int nX, int nY)
	{
		if(null == m_viewDrag)
		{
			return;
		}

		m_viewDrag.moveDrag(m_nGlobalPointOffsetX, nY - m_nDragPointY + m_nGlobalPointOffsetY);

		int nItemPos = getItemForPosition(nY);
		if(nItemPos >= 0 && nItemPos < m_arrayItems.size())
		{
			if(nItemPos != m_nDragItemIndex)
			{
				swapItems(nItemPos, m_nDragItemIndex);

				m_nDragItemIndex = nItemPos;

				m_adapterList.notifyDataSetChanged();
			}

			int nSpeed = 0;
			adjustScrollBounds(nY);
			if(nY > m_nScrollLowerBound)
			{
				nSpeed = nY > ((getHeight() + m_nScrollLowerBound) / 2) ? 16 : 4;
			} else if(nY < m_nScrollUpperBound)
			{
				nSpeed = nY < (m_nScrollUpperBound / 2) ? -16 : -4;
			}

			if(nSpeed != 0)
			{
				int nRef = pointToPosition(0, getHeight() / 2);
				if(nRef == AdapterView.INVALID_POSITION)
				{
					nRef = pointToPosition(0, getHeight() / 2 + getDividerHeight() + 32);
				}

				View viewTemp = getChildAt(nRef - getFirstVisiblePosition());
				if(viewTemp != null)
				{
					setSelectionFromTop(nRef, viewTemp.getTop() - nSpeed);
				}
			}
		}
	}

	public void stopDrag()
	{
		if(!m_isDragging) return;

		m_isDragging = false;

		if(m_viewDrag != null)
		{
			m_viewDrag.hideDrag();
			m_viewDrag = null;
		}

		m_adapterList.notifyDataSetChanged();
	}

	private int getItemForPosition(int nY)
	{
		int nAdjustY = nY;// - m_nDragPointY - (m_nItemHeight / 2);

		int nPos = myPointToPosition(0, nAdjustY);
		if(nPos >= 0)
		{
			//if(nPos <= m_nDragStartIndex) nPos += 1;
		} else {
//			if(nAdjustY < 0) nPos = 0;
			if(nAdjustY < 0) nPos = m_ptLastTouchPos.y;
		}

		return nPos;
	}

	private int myPointToPosition(int nX, int nY)
	{
		Rect rectFrame = m_rectDragTemp;

		final int nCount = getChildCount();
		for(int nChild = nCount - 1; nChild >= 0; nChild--)
		{
			getChildAt(nChild).getHitRect(rectFrame);

			if(rectFrame.contains(nX, nY))
				return getFirstVisiblePosition() + nChild;
		}

		return INVALID_POSITION;
	}

	private void adjustScrollBounds(int nY)
	{
		int nHeight = getHeight();
		if(nY >= nHeight / 3) m_nScrollUpperBound = nHeight / 3;
		if(nY <= nHeight * 2 / 3) m_nScrollLowerBound = nHeight * 2 / 3;
	}

	@Override
	public void onClick(View view)
	{
		if(view instanceof CheckedTextView)
		{
			View viewParent = (View)view.getParent();
			if(viewParent == null) return;
			if(!(viewParent instanceof ToolbarItemView)) return;

			Object obj = viewParent.getTag();
			if(obj == null || !(obj instanceof ToolbarItem)) return;

			//item.m_isChecked = !item.m_isChecked;

			//((CheckedTextView)view).setChecked(item.m_isChecked);

		}/* else if(view instanceof Button)
		{
			View viewParent = (View)(((View)view.getParent()).getParent());
			if(viewParent == null) return;
			if(!(viewParent instanceof IntrEditItemView)) return;
			
			if(m_listenerItemChange != null)
			{
				m_listenerItemChange.onItemAddNew();
			}
		}*/

	}

	@Override
	public boolean onLongClick(View view)
	{
		View viewParent = (View)view.getParent();
		if(viewParent == null) return false;

//		if(!m_isEditable) return false;

		int nPosition = view.getId();
		if(nPosition < 0 || nPosition >= m_arrayItems.size()) return false;

		//2013. 12. 13 지표설정창 도구모음에서 VIBRATE 퍼미션이 필요한 문제>>
		Vibrator vibe = (Vibrator)getContext().getSystemService(Context.VIBRATOR_SERVICE);
//		vibe.vibrate(50);
		//2013. 12. 13 지표설정창 도구모음에서 VIBRATE 퍼미션이 필요한 문제<<

		startDrag(nPosition);

		return true;
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		int action = event.getAction();
		switch(action){
			case MotionEvent.ACTION_DOWN:
				int nPosition = view.getId();
				if(nPosition < 0 || nPosition >= m_arrayItems.size()) return false;

				//2013. 12. 13 지표설정창 도구모음에서 VIBRATE 퍼미션이 필요한 문제>>
//			Vibrator vibe = (Vibrator)getContext().getSystemService(Context.VIBRATOR_SERVICE);
//			vibe.vibrate(50);
				//2013. 12. 13 지표설정창 도구모음에서 VIBRATE 퍼미션이 필요한 문제<<

				startDrag(nPosition);
				break;
		}

		return true;
	}
	public interface OnItemChangeListener
	{
		public abstract void onItemDrag(int nSrcIndex, int nDstIndex);
		public abstract void onItemAddNew();
	}


	private class IntrItemAdapter
			extends BaseAdapter
	{
		@Override
		public int getCount()
		{
			if(m_arrayItems == null) return 0;

			//return m_isEditable ? (m_arrayItems.size() + 1) : m_arrayItems.size();
			return m_arrayItems.size();
		}

		@Override
		public Object getItem(int position)
		{
			return null;
		}

		@Override
		public long getItemId(int position)
		{
			return 0;
		}

		@Override
		public View getView(int position, final View convertView, ViewGroup parent)
		{

			ToolbarItemView viewItem = (ToolbarItemView)convertView;

			if(viewItem == null)
			{
				viewItem = new ToolbarItemView(getContext());
				viewItem.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, m_nItemHeight));
			}

			viewItem.setItemInfo(position >= m_arrayItems.size() ? null : m_arrayItems.get(position), position);

			if(m_isDragging && m_nDragItemIndex == position)
			{
				viewItem.setVisibility(View.INVISIBLE);
			} else {
				viewItem.setVisibility(View.VISIBLE);
			}

			if (m_arrayItems != null && m_arrayItems.size() != 0)
				if (position < m_arrayItems.size()) {
					final ToolbarItem itemCode = m_arrayItems.get(position);

					CheckBox chkBox = (CheckBox) viewItem.m_viewCheck;
					chkBox.setEnabled(true);
					chkBox.setVisibility(View.VISIBLE);
					viewItem.showGrabIcon(true);
					viewItem.setText(itemCode.getTitle());
					int nRes = getContext().getResources().getIdentifier(
							"i_option_" + itemCode.getImage(), "drawable", getContext().getPackageName());
					viewItem.setToolbarImage(nRes);
					viewItem.setCheckState(itemCode.getChk());

					//체크상태 확인해서 글씨색상 변경
					if (itemCode.getChk()) {
						//		viewItem.setTextColor(Color.rgb(239, 115, 28));
					}
					else {
						viewItem.setTextColor(Color.rgb(46, 48, 51));
					}

					final ToolbarItemView _viewItem = viewItem;
					viewItem.m_viewCheck.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							CheckBox chkbox = (CheckBox) v;
							itemCode.setChk(chkbox.isChecked());
//							if(itemCode.getTitle().equals("차트설정"))
//							{
//								chkbox.setChecked(true);
//								itemCode.setChk(true);
//							}

							//체크상태 확인해서 글씨색상 변경 
//							if(chkbox.isChecked())
//							{
//					//			_viewItem.setTextColor(Color.rgb(239, 115, 28));
//							}
//							else
//							{
//								_viewItem.setTextColor(Color.rgb(46, 48, 51));
//							}
						}
					});

					viewItem.m_viewLinear.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							CheckBox chkbox = (CheckBox) ((LinearLayout) v).getChildAt(0);

							if (chkbox.isChecked())
								chkbox.setChecked(false);
							else
								chkbox.setChecked(true);
							itemCode.setChk(chkbox.isChecked());

//							if(itemCode.getTitle().equals("차트설정"))
//							{
//								chkbox.setChecked(true);
//								itemCode.setChk(true);
//							}
						}
					});
					if(itemCode.getTitle().equals("저장") || itemCode.getTitle().equals("불러오기") || itemCode.getTitle().equals("초기화")) {
						viewItem.m_viewIconImage.setVisibility(View.GONE);
						viewItem.m_viewGab.setVisibility(View.VISIBLE);
					} else {
						viewItem.m_viewIconImage.setVisibility(View.VISIBLE);
						viewItem.m_viewGab.setVisibility(View.GONE);
					}

					if (itemCode.getTitle().equals("분할") && !COMUtil.bIsMulti) {
						viewItem.setLayoutParams(new LinearLayout.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,1));
						viewItem.setVisibility(View.GONE);
						viewItem.setDividerDrawable(null);
					}
				}
			return viewItem;
		}
	}

	private class ToolbarItemView
			extends LinearLayout
	{
		//		private CheckedTextView			m_viewCheck;
		private View			m_viewGrab;
		private TextView m_viewTitle;
		private ImageView m_viewIconImage;

		private CheckBox m_viewCheck;
		private LinearLayout m_viewLinear;

		private View m_viewGab;

		public ToolbarItemView(Context context)
		{
			super(context);

			initView(context);
		}

		private void initView(Context context)
		{
			setOrientation(LinearLayout.HORIZONTAL);

			LayoutInflater factory = LayoutInflater.from(context);
			int	layoutResId = context.getResources().getIdentifier("analtool_change_row", "layout", COMUtil.apiView.getContext().getPackageName());
//			if(COMUtil.skinType == COMUtil.SKIN_BLACK){
//				layoutResId = context.getResources().getIdentifier("analtool_change_row_black", "layout", COMUtil.apiView.getContext().getPackageName());
//			}
				
			ViewGroup frontBack = (ViewGroup)factory.inflate(layoutResId, null);
			addView(frontBack, new LayoutParams(LayoutParams.FILL_PARENT, (int)COMUtil.getPixel(56)));

			COMUtil.setGlobalFont(frontBack);

			//체크박스 추가하게 되어서, 리스너와 같이 추가
			m_viewLinear = (LinearLayout) frontBack.getChildAt(0);
			m_viewCheck = (CheckBox)m_viewLinear.getChildAt(0);

//			m_viewIconImage = (ImageView)frontBack.getChildAt(0);
//			m_viewTitle = (TextView)frontBack.getChildAt(1);
//			m_viewGrab = frontBack.getChildAt(2);

			m_viewIconImage = (ImageView)m_viewLinear.getChildAt(1);
			m_viewGab = (View)m_viewLinear.getChildAt(2);
			m_viewTitle = (TextView)m_viewLinear.getChildAt(3);
			m_viewGrab = frontBack.getChildAt(1);

//			setBackgroundDrawable(ResourceManager.getImage("list_bg.9", true));
//			
//			// 체크 텍스트
//			m_viewCheck = new CheckedTextView(context);
//			m_viewCheck.setPadding(m_nColPadding, 0, 0, 0);
//
//			Drawable drawable = ResourceManager.getImage("checkbox", true);
//			drawable.setBounds(0, 0, m_nCheckBoxWidth, m_nCheckBoxHeight);
//			m_viewCheck.setCompoundDrawables(drawable, null, null, null);
//			m_viewCheck.setCompoundDrawablePadding(m_nCheckSpacing);
//			m_viewCheck.setFocusable(false);
//			m_viewCheck.setTypeface(ResourceManager.getFont());
//			m_viewCheck.setTextSize(TypedValue.COMPLEX_UNIT_PX, m_nTextSize);
//			m_viewCheck.setTextColor(Color.BLACK);
//			m_viewCheck.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
//			m_viewCheck.setSingleLine();
//			m_viewCheck.setEllipsize(TruncateAt.END);
//			m_viewCheck.setOnClickListener(IntrEditItemListView.this);
//			m_viewCheck.setFocusable(false);
//			
//			// 잡고 움직이는 넘
//			m_viewGrab = new ImageView(context);
//			Drawable drawable2 = ResourceManager.getSingleImage("dropdown_list_move");
//			m_viewGrab.setPadding(m_nGrabPadding, 0, m_nGrabPadding, 0);
//			m_viewGrab.setScaleType(ScaleType.FIT_CENTER);
//			m_viewGrab.setImageDrawable(drawable2);
//
//			addView(m_viewCheck, new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1));
//			addView(m_viewGrab, new LinearLayout.LayoutParams(m_nGrabColWidth, LayoutParams.MATCH_PARENT, 0));
//
//			m_viewCheck.setOnClickListener(IntrEditItemListView.this);
//			
//			frontBack.setOnLongClickListener(ToolBarSetListView.this);
//			m_viewGrab.setOnTouchListener(ToolBarSetListView.this);

			m_viewGrab.setOnLongClickListener(ToolBarSetListView.this);

		}

		public void releaseView()
		{
			removeAllViews();

//			m_viewCheck = null;
			m_viewGrab = null;

			//m_buttonAdd = null;

			setTag(null);
		}

		public void setItemInfo(ToolbarItem infoItem, int nPosition)
		{
			if(infoItem == null) return;

//			m_viewCheck.setChecked(infoItem.m_isChecked);
//			m_viewCheck.setText(infoItem.getName());

			m_viewGrab.setTag(infoItem);
			m_viewGrab.setId(nPosition);
		}

		public void setText(String strText)
		{
			m_viewTitle.setText(strText);
		}
		public void setTextColor(int nColor)
		{
			m_viewTitle.setTextColor(nColor);
		}
		public void setToolbarImage(int nID)
		{
			m_viewIconImage.setBackgroundResource(nID);
		}
		public Bitmap getDragDrawingCache()
		{
			boolean isOld = isDrawingCacheEnabled();

			setDrawingCacheEnabled(true);
			Bitmap bitmapDrag = Bitmap.createBitmap(getDrawingCache());
			setDrawingCacheEnabled(isOld);

			return bitmapDrag;
		}
		public void setCheckState(boolean bChk)
		{
			//체크상태 변경
			m_viewCheck.setChecked(bChk);
		}

		public void showGrabIcon(boolean b)
		{
			if(b)
				m_viewGrab.setVisibility(View.VISIBLE);
			else
				m_viewGrab.setVisibility(View.GONE);
		}

		public void showCheckbox(boolean b)
		{
			if(b)
				m_viewCheck.setVisibility(View.VISIBLE);
			else
				m_viewCheck.setVisibility(View.INVISIBLE);
		}

	}
	//listview holder pattern
	class ViewWrapper2 {
		CheckBox m_chkBox;
		ImageView m_toolbarImg;
		TextView m_tvName;
		ImageView m_ivMove;

		View base;

		ViewWrapper2(View base)
		{
			this.base = base;
		}

		CheckBox getChkBox()
		{
			if(m_chkBox == null) {
				int layoutResId = base.getContext().getResources().getIdentifier("toolbarChk", "id", base.getContext().getPackageName());
				m_chkBox = (CheckBox)base.findViewById(layoutResId);
			}
			return m_chkBox;
		}

		ImageView getToolbarImg()
		{
			if(m_toolbarImg == null) {
				int layoutResId = base.getContext().getResources().getIdentifier("toolbarImg", "id", base.getContext().getPackageName());
				m_toolbarImg = (ImageView)base.findViewById(layoutResId);
			}
			return m_toolbarImg;
		}

		TextView getTvName()
		{
			if(m_tvName == null) {
				int layoutResId = base.getContext().getResources().getIdentifier("fctb_TextViewA01", "id", base.getContext().getPackageName());
				m_tvName = (TextView)base.findViewById(layoutResId);
			}
			return m_tvName;
		}

		ImageView getIvMove()
		{
			if(m_ivMove == null) {
				int layoutResId = base.getContext().getResources().getIdentifier("fctb_ImageViewA01", "id", base.getContext().getPackageName());
				m_ivMove = (ImageView)base.findViewById(layoutResId);
			}
			return m_ivMove;
		}
	}
}