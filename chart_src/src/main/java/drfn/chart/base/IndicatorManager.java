/**
 *
 */
package drfn.chart.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

import drfn.chart.util.COMUtil;

/**
 * @author user
 *
 */
public class IndicatorManager extends View
		implements View.OnClickListener {

	MyArrayAdapter m_scvAdapter;
	MyArrayAdapter2 m_scvAdapter2;
	Hashtable<String, Object> loadItem=null;
	RelativeLayout layout=null;
	View xmlUI=null;
	ListView list, list2=null;
	private Context context = null;
	int reqCnt = 10;
	Button addButton = null;
	AlertDialog.Builder alert_confirm = null;

	public IndicatorManager(Context context, RelativeLayout layout) {
		super(context);
		this.context = context;
		this.layout = layout;

		LayoutInflater factory = LayoutInflater.from(context);

		//2012. 8. 20 태블릿  불러오기창 팝업화 
		int layoutResId;
		layoutResId = context.getResources().getIdentifier("indicator_manager", "layout", context.getPackageName());

		xmlUI = factory.inflate(layoutResId, null);
		xmlUI.setOnTouchListener(
				new OnTouchListener() {
					public boolean onTouch(View v, MotionEvent event) {

						return true;
					}
				}
		);

		layoutResId = context.getResources().getIdentifier("loadlist", "id", context.getPackageName());
		list = (ListView)xmlUI.findViewById(layoutResId);
		list.setTranscriptMode(ListView.TRANSCRIPT_MODE_DISABLED);

		layoutResId = context.getResources().getIdentifier("loadlist2", "id", context.getPackageName());
		list2 = (ListView)xmlUI.findViewById(layoutResId);
		list2.setTranscriptMode(ListView.TRANSCRIPT_MODE_DISABLED);

		/** 지표 초기화 버튼처리 2014.10.01 by lyk **/
		layoutResId = context.getResources().getIdentifier("btn_init", "id", context.getPackageName());
		Button addFunc = (Button)xmlUI.findViewById(layoutResId);
		addFunc.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				alert_confirm.show();
			}
		});
//		
//		/** 지표 삭제버튼처리 2014.10.02 by lyk **/
//		layoutResId = context.getResources().getIdentifier("btn_del", "id", context.getPackageName());
//		Button delFunc = (Button)xmlUI.findViewById(layoutResId);
//		delFunc.setOnClickListener(new Button.OnClickListener() {
//        	public void onClick(View v) {
//        		delList();
//	        }
//        });

		/** 적용버튼처리 2014.10.02 by lyk **/
		layoutResId = context.getResources().getIdentifier("btn_accept", "id", context.getPackageName());
		Button acceptFunc = (Button)xmlUI.findViewById(layoutResId);
		acceptFunc.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					acceptList();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		/** 취소버튼처리 2014.10.02 by lyk **/
//		layoutResId = context.getResources().getIdentifier("btn_cancel", "id", context.getPackageName());
//		Button cancelFunc = (Button)xmlUI.findViewById(layoutResId);
//		cancelFunc.setOnClickListener(new Button.OnClickListener() {
//        	public void onClick(View v) {
//        		cancelList();
//	        }
//        });

		alert_confirm = new AlertDialog.Builder(this.getContext());
		alert_confirm.setMessage("지표 목록을 초기화 하시겠습니까?").setCancelable(false).setPositiveButton("확인",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 'YES'
						initList();
					}
				}).setNegativeButton("취소",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 'No'
						return;
					}
				});

		//초기실행시 로컬차트 로딩.
		myStorage(null);

		//2012. 8. 1  전체화면표시를 위해 layoutparam 줌 
		xmlUI.setLayoutParams(new LinearLayout.LayoutParams(this.layout.getWidth(), this.layout.getHeight()));
		this.layout.addView(xmlUI);

//		alert_confirm = new AlertDialog.Builder(this.getContext());  
//		alert_confirm.setMessage("정말로 삭제하시겠습니까?").setCancelable(false).setPositiveButton("확인",  
//		new DialogInterface.OnClickListener() {  
//		    @Override  
//		    public void onClick(DialogInterface dialog, int which) {  
//		        // 'YES'  
//		    	editListItem();
//		    }  
//		}).setNegativeButton("취소",  
//		new DialogInterface.OnClickListener() {  
//		    @Override  
//		    public void onClick(DialogInterface dialog, int which) {  
//		        // 'No'  
//		    return;  
//		    }  
//		});
	}

	Object parentTarget = null;
	public void setParent(Object target) {
		this.parentTarget = target;
	}

	public void initList() {
		items2.clear();
		m_scvAdapter2.notifyDataSetChanged();
	}

//	public void addList() {
//		if(list!=null) {
//			Vector<String> checkList = new Vector<String>(); 
//			for(int i=0; i<items.size(); i++) {
//				if(itemChecked.get(i).equals(true)) {
//					checkList.add(String.valueOf(i));
//					itemChecked.set(i, false);
//				}
//			}
//
//			if(checkList.size()<1) 
//				return;
//			
//			for(int i=0; i<checkList.size(); i++) {
//				Vector<Hashtable<String, String>> item = (Vector<Hashtable<String, String>>)items.get(Integer.parseInt(checkList.get(i)));
//				
//				//to right list add
//				items2.add(item);
//				itemChecked2.add(false);
////				m_scvAdapter2.add(item);
//			}
//			
//			m_scvAdapter.notifyDataSetChanged();
//			m_scvAdapter2.notifyDataSetChanged();
//		}
//	}
//	
//	public void delList() {
//		if(list2!=null) {
//			Vector<String> checkList = new Vector<String>(); 
//			for(int i=0; i<items2.size(); i++) {
//				if(itemChecked2.get(i).equals(true)) {
//					checkList.add(String.valueOf(i));
//					itemChecked2.set(i, false);
//				}
//			}
//
//			if(checkList.size()<1) 
//				return;
//			
//			for(int i=0; i<checkList.size(); i++) {
//				Vector<Hashtable<String, String>> item = (Vector<Hashtable<String, String>>)items2.get(Integer.parseInt(checkList.get(i)));
//				
//				//to right list del
//				items2.remove(item);
//				itemChecked2.remove(Integer.parseInt(checkList.get(i)));
//			}
//
//			m_scvAdapter2.notifyDataSetChanged();
//		}
//	}

	public void acceptList() throws FileNotFoundException, IOException {
		COMUtil.setAddJipyoList(null); //초기화 
		if(items2!=null && items2.size()>0) {
			Vector<Hashtable<String, String>> addItems = COMUtil.getAddJipyoList();
			for(int i=0; i<items2.size(); i++) {
				addItems.add(items2.get(i).get(0));
			}
		}

		((IndicatorConfigView)parentTarget).refreshList();

		//추가된 지표 리스트 저장
		COMUtil.saveAddJipyoList(COMUtil._mainFrame.strFileName);

		close();
	}

	private void  showDetail(int pos) {
		COMUtil.setAddJipyoList(null); //초기화 
		if(items2!=null && items2.size()>0) {
			Vector<Hashtable<String, String>> addItems = COMUtil.getAddJipyoList();
			for(int i=0; i<items2.size(); i++) {
				addItems.add(items2.get(i).get(0));
			}
		}

		((IndicatorConfigView)parentTarget).refreshList();

		//추가된 지표 리스트 저장
		COMUtil.saveAddJipyoList(COMUtil._mainFrame.strFileName);

		((IndicatorConfigView)parentTarget).showDetailForAddJipyo(COMUtil.getAddJipyoList().get(pos));

//		close();

	}

	public void cancelList() {
		close();
	}

	public void close() {
		layout.removeView(xmlUI);
		COMUtil.unbindDrawables(xmlUI);
		System.gc();

		if(m_scvAdapter!=null) m_scvAdapter.clear();
		if(m_scvAdapter2!=null) m_scvAdapter2.clear();
	}
	public void unbindDrawables(View view) {
		if (view.getBackground() != null) {
			view.getBackground().setCallback(null);
		}
		if (view instanceof ViewGroup) {
			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
				unbindDrawables(((ViewGroup) view).getChildAt(i));
			}
			((ViewGroup) view).removeAllViews();
		}
	}

	private void myStorage(Button target) {
		this.mode="";
		if(addButton!=null) {
			list.removeFooterView(addButton);
			addButton=null;
		}
//		m_itemsArr.clear();//초기화.
//		itemChecked.clear();
		createLocalChartList();

		COMUtil.isModifyDetail = false;
	}


	String mode = "";

	/* 폰에 저장되어있는 차트리스트 처리 */
	private ArrayList<Vector<Hashtable<String, String>>> items, items2 = null;
	public void createLocalChartList() {

//		//2014.10.07 by lyk - 동일지표 복원
		if(COMUtil.getAddJipyoList()!=null && COMUtil.getAddJipyoList().size()<1) {
//			System.out.println("Debug_loadAddJipyoList!!!");
			COMUtil.loadAddJipyoList(COMUtil._mainFrame.strFileName);
		}

//		 //2014.10.07 by lyk - 동일지표 복원  end

		items = COMUtil.getJipyoMenuArrayList();

		//check initialize
//		boolean chkState = false;
//		for(int i=0; i<items.size(); i++) {
//			itemChecked.add(chkState);
//		}

		//left list
		if(m_scvAdapter==null) {
			m_scvAdapter = new MyArrayAdapter(context, items);
		}

//		list.setDividerHeight(1);
		list.setAdapter(m_scvAdapter);
		list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
				onListItemClickLocal(null,arg1,arg2,arg3);
			}
		});

		//right list
		items2 = COMUtil.getJipyoMenuArrayList2();

		//check initialize
//			boolean chkState2 = false;
//			for(int i=0; i<items2.size(); i++) {
//				itemChecked2.add(chkState2);
//			}
//			
		if(m_scvAdapter2==null) {
			m_scvAdapter2 = new MyArrayAdapter2(context, items2);
		}

//		list2.setDividerHeight(1);
		list2.setAdapter(m_scvAdapter2);
		list2.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
				onListItemClickLocal2(null,arg1,arg2,arg3);
			}
		});

//			mHelper.close();

		// 커스텀 ArrayAdapter 선언/초기화.
//			Adapter.notifyDataSetChanged();
	}

	//OnClick에대한 처리루틴.
	public void onClick(View view) {
//		int btnID = view.getId();
//		switch(btnID) {
//			case R.id.frameaBtnFunction:
//
//		}
	}

	/** 디바이스에 저장된 차트를 로딩할때 호출됨. **/
	public void onListItemClickLocal(ListView l, View v, int position, long id) {
		Vector<Hashtable<String, String>> item = (Vector<Hashtable<String, String>>)items.get(position);
//		System.out.println("Debug_selected_item:"+item.get(0).get("name"));

		//to right list add

		//추가된 리스트의 name에서 COMUtil.JIPYO_ADD_REMARK를 체크하여 마지막 인덱스를 찾아낸다.
		String defaultName = item.get(0).get("name");

		String cmpName = COMUtil.getAddJipyoTitle(defaultName);

		//같은 이름의 지표항목만 추린다
		Vector<String> sameNames = new Vector<String>();
		Vector<String> sameIndexs = new Vector<String>();
		for(int i=0; i<items2.size(); i++) {
			Vector<Hashtable<String, String>> subItem = (Vector<Hashtable<String, String>>)items2.get(i);
			String subName = subItem.get(0).get("name");
			int subIndex = subName.indexOf(COMUtil.JIPYO_ADD_REMARK);
			String sameName = "";
			String sameIndex = "";
			if(subIndex>0) {
				sameIndex = subName.substring(subIndex+1);
				sameName = subName.substring(0, subIndex);
			}
			if(cmpName.equals(sameName)) {
				sameNames.add(sameName);
				sameIndexs.add(sameIndex);
			}

		}

		//같은 이름의 지표 리스트 중에서 lastIndex를 찾는다 	
		int lastIndex = 0;
		if(sameIndexs.size()<1) {
			lastIndex = 0;
		} else {
			lastIndex = Integer.parseInt(sameIndexs.lastElement());
		}
		//같은 이름의 지표 리스트 중에서 lastIndex를 찾는다 

		String addName = defaultName + COMUtil.JIPYO_ADD_REMARK + (lastIndex+1);	// jipyoname_1;

//		item.get(0).put("name", String.valueOf(addName));

		Vector<Hashtable<String, String>> addItem = new Vector<Hashtable<String, String>>();
		Hashtable<String, String> addHashItem = new Hashtable<String, String>();
		//type, name, tag
		addHashItem.put("type", item.get(0).get("type"));
		addHashItem.put("name", String.valueOf(addName));

		//COMUtil.addJipyo() 에서 tag 비교 값으로 사용한다. 
		if((lastIndex+1)>0) {
			int addTag = Integer.parseInt(item.get(0).get("tag")) * 100 + (lastIndex+1);
			addHashItem.put("tag", String.valueOf(addTag));
		} else {
			addHashItem.put("tag", item.get(0).get("tag"));
		}
		addHashItem.put("detailType", item.get(0).get("detailType"));

		addItem.add(addHashItem);

//		addItem.get(0).put("name", String.valueOf(addName));
		//추가된 리스트의 name에서 "_"를 체크하여 마지막 인덱스를 찾아낸다.

		items2.add(addItem);
//		itemChecked2.add(false);

//		m_scvAdapter.notifyDataSetChanged();
		m_scvAdapter2.notifyDataSetChanged();

	}

	public void onListItemClickLocal2(ListView l, View v, int position, long id) {
		Vector<Hashtable<String, String>> item = (Vector<Hashtable<String, String>>)items2.get(position);

		//to right list add
		items2.remove(item);
//		itemChecked2.add(false);

//		m_scvAdapter.notifyDataSetChanged();
		m_scvAdapter2.notifyDataSetChanged();

	}

	//	private ArrayList<Boolean> itemChecked = new ArrayList<Boolean>();
	//ArrayAdapter에서 상속받는 커스텀 ArrayAdapter 정의.
	class MyArrayAdapter extends ArrayAdapter<Vector<Hashtable<String, String>>> {

		// 생성자 내부에서 초기화
		private Context context;
		private ViewWrapper wrapper = null;
		public ArrayList<Vector<Hashtable<String, String>>> mitems;

		// 생성자
		MyArrayAdapter(Context context, ArrayList<Vector<Hashtable<String, String>>> items) {
			super(context, COMUtil._mainFrame.getContext().getResources().getIdentifier("indicator_manager_list_item", "layout", COMUtil._mainFrame.getContext().getPackageName()), items);

			// instance 변수(this.context)를 생성자 호출시 전달받은 지역 변수(context)로 초기화.
			this.context = context;
			this.mitems = items;
		}
		// ListView에서 각 행(row)을 화면에 표시하기 전 호출됨.

		public View getView(int position, View convertView, ViewGroup parent){
			View row = convertView;

			if(row == null) {
				// LayoutInflater의 객체 inflater를 현재 context와 연결된 inflater로 초기화.
				LayoutInflater inflater = ((Activity)context).getLayoutInflater();

				// inflator객체를 이용하여 \res\laout\cellsigview.xml 파싱
				//2012. 8. 21  불러오기창 레이아웃 테블릿인지 아닌지에 따라서 다르게 로드 :  SL_tab10
				if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
				{
					//row = (View)inflater.inflate(COMUtil._mainFrame.getContext().getResources().getIdentifier("chart_load_celltype_tab", "layout", COMUtil._mainFrame.getContext().getPackageName()), null);
				}
				else
				{
					row = (View)inflater.inflate(COMUtil._mainFrame.getContext().getResources().getIdentifier("indicator_manager_list_item", "layout", COMUtil._mainFrame.getContext().getPackageName()), null);
				}

				wrapper = new ViewWrapper(row);
				row.setTag(wrapper);
			}
			else {
				wrapper = (ViewWrapper)row.getTag();
			}
//	        Vector<Hashtable<String, String>> vItems = mitems.get(0);
			Vector<Hashtable<String, String>> oneItems = (Vector<Hashtable<String, String>>)(mitems.get(position));
			Hashtable<String, String> oneItem = oneItems.get(0);
			wrapper.getCtrlTextView().setText(oneItem.get("name")); //type, name, tag

			//del btn
//	        wrapper.getCtrlVisible().setVisibility(GONE);
//	        wrapper.getCtrlVisible().setId(position);
//	        wrapper.getCtrlVisible().setChecked(itemChecked.get(position));
//	        wrapper.getCtrlVisible().setOnClickListener(new Button.OnClickListener() {
//	        	public void onClick(View v) {
//	        		CheckBox cb = (CheckBox)v;
//	        		boolean isChecked = cb.isChecked();
//	        		int pos = v.getId();
//	                if (isChecked) {
//	                    itemChecked.set(pos, true);
//	
//	                } else if (!isChecked) {
//	                    itemChecked.set(pos, false);
//	                    // do some operations here
//	                }
//		        }
//	        });
			//del btn

			// 커스터마이징 된 View 리턴.
			return row;

		}
	}

	//	private ArrayList<Boolean> itemChecked2 = new ArrayList<Boolean>();
	//ArrayAdapter에서 상속받는 커스텀 ArrayAdapter 정의.
	class MyArrayAdapter2 extends ArrayAdapter<Vector<Hashtable<String, String>>> {

		// 생성자 내부에서 초기화
		private Context context;
		private ViewWrapper2 wrapper = null;
		public ArrayList<Vector<Hashtable<String, String>>> mitems;
//	    int gnSigChoiceViewCellTypeID = COMUtil._mainFrame.getContext().getResources().getIdentifier("chart_load_celltype", "layout", COMUtil._mainFrame.getContext().getPackageName());	///< 화면의 layout ID.
//	    private static final int gnSigChoiceViewCellTypeID = gnSigChoiceViewCellTypeID0; 

		// 생성자
		MyArrayAdapter2(Context context, ArrayList<Vector<Hashtable<String, String>>> items) {
			super(context, COMUtil._mainFrame.getContext().getResources().getIdentifier("indicator_manager_list_item2", "layout", COMUtil._mainFrame.getContext().getPackageName()), items);

			// instance 변수(this.context)를 생성자 호출시 전달받은 지역 변수(context)로 초기화.
			this.context = context;
			this.mitems = items;
		}
		// ListView에서 각 행(row)을 화면에 표시하기 전 호출됨.

		public View getView(int position, View convertView, ViewGroup parent){
			View row = convertView;

			if(row == null) {
				// LayoutInflater의 객체 inflater를 현재 context와 연결된 inflater로 초기화.
				LayoutInflater inflater = ((Activity)context).getLayoutInflater();

				// inflator객체를 이용하여 \res\laout\cellsigview.xml 파싱
				//2012. 8. 21  불러오기창 레이아웃 테블릿인지 아닌지에 따라서 다르게 로드 :  SL_tab10
				if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
				{
					//row = (View)inflater.inflate(COMUtil._mainFrame.getContext().getResources().getIdentifier("chart_load_celltype_tab", "layout", COMUtil._mainFrame.getContext().getPackageName()), null);
				}
				else
				{
					row = (View)inflater.inflate(COMUtil._mainFrame.getContext().getResources().getIdentifier("indicator_manager_list_item2", "layout", COMUtil._mainFrame.getContext().getPackageName()), null);
				}

				wrapper = new ViewWrapper2(row);
				row.setTag(wrapper);
			}
			else {
				wrapper = (ViewWrapper2)row.getTag();
			}
			Vector<Hashtable<String, String>> oneItems = (Vector<Hashtable<String, String>>)(mitems.get(position));
			Hashtable<String, String> oneItem = oneItems.get(0);

			//2014.10.08 by lyk - 동일지표 이름을 화면에 보여줄때는 COMUtil.JIPYO_ADD_REMARK 이전까지 이름을 사용한다.
			wrapper.getCtrlTextView().setText(COMUtil.getAddJipyoTitle(oneItem.get("name"))); //type, name, tag
			wrapper.getCtrlTextView().setId(position);
			wrapper.getCtrlTextView().setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					int pos = v.getId();
					showDetail(pos);
				}
			});
			//2014.10.08 by lyk - 동일지표 이름을 화면에 보여줄때는 COMUtil.JIPYO_ADD_REMARK 이전까지 이름을 사용한다. end

			wrapper.getCtrlVisible().setId(position);
			wrapper.getCtrlVisible().setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					int pos = v.getId();
					items2.remove(pos);
					m_scvAdapter2.notifyDataSetChanged();
				}
			});
			// 커스터마이징 된 View 리턴.
			return row;

		}
	}

	//
	// Holder Pattern을 구현하는 ViewWrapper 클래스
	//
	class ViewWrapper {
		private View base;
		private TextView  ctlCodeName;

		ViewWrapper(View base) {
			this.base = base;
		}

		// 멤버 변수가 null일때만 findViewById를 호출
		// null이 아니면 저장된 instance 리턴 -> Overhaed 줄임s

		TextView getCtrlTextView() {
			if(ctlCodeName == null) {
				int resId = COMUtil._mainFrame.getContext().getResources().getIdentifier("name", "id", context.getPackageName());
				ctlCodeName = (TextView)base.findViewById(resId);
			}
			return ctlCodeName;
		}
	}

	//
	// Holder Pattern을 구현하는 ViewWrapper 클래스
	//
	class ViewWrapper2 {
		private View base;
		private TextView  ctlCodeName;
		private Button ctlVisible;

		ViewWrapper2(View base) {
			this.base = base;
		}

		// 멤버 변수가 null일때만 findViewById를 호출
		// null이 아니면 저장된 instance 리턴 -> Overhaed 줄임s

		TextView getCtrlTextView() {
			if(ctlCodeName == null) {
				int resId = COMUtil._mainFrame.getContext().getResources().getIdentifier("name", "id", context.getPackageName());
				ctlCodeName = (TextView)base.findViewById(resId);
			}
			return ctlCodeName;
		}

		Button getCtrlVisible() {
			if(ctlVisible == null) {
				int resId = COMUtil._mainFrame.getContext().getResources().getIdentifier("btn_del", "id", context.getPackageName());
				ctlVisible = (Button)base.findViewById(resId);
			}
			return ctlVisible;
		}
	}
}
