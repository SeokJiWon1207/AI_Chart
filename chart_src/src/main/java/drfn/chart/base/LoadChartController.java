/**
 *
 */
package drfn.chart.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import drfn.chart.comp.ChartsaveCursorAdapter;
import drfn.chart.comp.DRAlertDialog;
import drfn.chart.util.COMUtil;
import drfn.chart.util.CoSys;

//import drfn.chart.comp.ImageDownloader;

/**
 * @author user
 *
 */
public class LoadChartController extends View
		implements View.OnClickListener {

//	private static final int LAUNCHED_ACTIVITY_JipyoSetup = 1;

	ArrayList<LoadCellItem> m_itemsArr;
	MyArrayAdapter m_scvAdapter;
	ChartsaveCursorAdapter Adapter = null;
	Vector<Hashtable<String, Object>> loadList;
	Hashtable<String, Object> loadItem=null;
	//private final ImageDownloader imageDownloader = new ImageDownloader();
	Button tabBtn01, tabBtn02, tabBtn03, tabBtn04 = null;
	String urlstr = "http://218.38.18.171/smartPhone/dnload.php?search=";
	RelativeLayout layout=null;
	View xmlUI=null;
	ListView list=null;
	private Context context = null;
	int reqCnt = 10;
	Button addButton = null;
	AlertDialog.Builder alert_confirm = null;

	public static int MTS_CHART = 0;
	public static int HTS_CHART = 1;

	int m_nMode = MTS_CHART;

	CheckBox chkOneTouch = null; //2019.04.15 원터치 차트설정불러오기 추가 - lyj

	public LoadChartController(Context context, RelativeLayout layout) {
		super(context);
		this.context = context;
		this.layout = layout;

//		COMUtil._chartMain.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR); 

		LayoutInflater factory = LayoutInflater.from(context);

		//2012. 8. 20 태블릿  불러오기창 팝업화 
		int layoutResId;
		if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
		{
			layoutResId = context.getResources().getIdentifier("chart_load_tab", "layout", context.getPackageName());
		}
		else
		{
			layoutResId = context.getResources().getIdentifier("chart_load", "layout", context.getPackageName());
		}

		xmlUI = factory.inflate(layoutResId, null);
		xmlUI.setOnTouchListener(
				new OnTouchListener() {
					public boolean onTouch(View v, MotionEvent event) {

						return true;
					}
				}
		);

		int resId = COMUtil.apiView.getContext().getResources().getIdentifier("frameaBtnfunction", "id", COMUtil.apiView.getContext().getPackageName());
		Button btnClose = (Button) xmlUI.findViewById(resId);
		btnClose.setTypeface(COMUtil.typefaceMid);

		if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB)) { //Pad
//			layoutResId = context.getResources().getIdentifier("loadtop", "id", context.getPackageName());
//			RelativeLayout loadtop = (RelativeLayout)xmlUI.findViewById(layoutResId);
			//이미지 줄이기.(OutOfMemory 해결)
			//2012. 8. 21  불러오기창 제목줄 감춰진 현상때문에 주석처리 
//			layoutResId = context.getResources().getIdentifier("bg_top", "drawable", context.getPackageName());
//			Drawable drawable = COMUtil.getSmallBitmap(layoutResId);
//			loadtop.setBackgroundDrawable(drawable);
//			loadtop.setVisibility(View.GONE);
		}
		COMUtil.chartLoadView=this;

		//Button btnBack = (Button) findViewById(R.id.frameaBtnBack);
		layoutResId = context.getResources().getIdentifier("loadlist", "id", context.getPackageName());
		list = (ListView)xmlUI.findViewById(layoutResId);
		list.setTranscriptMode(ListView.TRANSCRIPT_MODE_DISABLED);

		/** 편집버튼처리 2011.09.30 by lyk **/
		layoutResId = context.getResources().getIdentifier("chartload_btn_accept", "id", context.getPackageName());
		Button editFunc = (Button)xmlUI.findViewById(layoutResId);
		editFunc.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
//				editList();
				loadItem();
			}
		});

		/** Pad용 화면 구성 **/
//		layoutResId = context.getResources().getIdentifier("frameaBtnFunct ion", "id", context.getPackageName());
//		Button btnFunc = (Button)xmlUI.findViewById(layoutResId);
//		TextView textTitle = (TextView)xmlUI.findViewById(R.id.frameaTitle);
//		btnFunc.setOnClickListener(new Button.OnClickListener() {
//        	public void onClick(View v) {
//	        	try {
//	        		close();
//	        	}
//	        	catch (Exception e) {        	     
//	        	}
//	        }
//        });
		//2012. 7. 23 차트불러오기 화면 탭버튼 주석처리 
//		layoutResId = context.getResources().getIdentifier("tabBtn01", "id", context.getPackageName());
//		tabBtn01 = (Button)xmlUI.findViewById(layoutResId);
//		final Context fcontext = context;
//		tabBtn01.setOnTouchListener(
//			new View.OnTouchListener() {
//				public boolean onTouch(View v, MotionEvent event) {
//					switch(event.getAction()) {
//						case MotionEvent.ACTION_DOWN:
//							int layoutResId = fcontext.getResources().getIdentifier("tab01_on", "drawable", fcontext.getPackageName());
//							 v.setBackgroundResource(layoutResId);
//							 setTabButtonMode(tabBtn01);
//							 break;
//						case MotionEvent.ACTION_UP:
//							int layoutResId2 = fcontext.getResources().getIdentifier("tab01_on", "drawable", fcontext.getPackageName());
//							 v.setBackgroundResource(layoutResId2);
//							 break;
//					
//					}
//					return false;
//				}
//			}
//		);
//		tabBtn01.setOnClickListener(new Button.OnClickListener() {
//        	public void onClick(View v) {
//	        	proStorage(tabBtn01);
//	        }
//        });
//		
//		layoutResId = context.getResources().getIdentifier("tabBtn02", "id", context.getPackageName());
//		tabBtn02 = (Button)xmlUI.findViewById(layoutResId);
//		tabBtn02.setOnTouchListener(
//				new View.OnTouchListener() {
//					public boolean onTouch(View v, MotionEvent event) {
//						switch(event.getAction()) {
//							case MotionEvent.ACTION_DOWN:
//								int layoutResId = fcontext.getResources().getIdentifier("tab02_on", "drawable", fcontext.getPackageName());
//								 v.setBackgroundResource(layoutResId);
//								 setTabButtonMode(tabBtn02);
//								 break;
//							case MotionEvent.ACTION_UP:
//								int layoutResId2 = fcontext.getResources().getIdentifier("tab02_on", "drawable", fcontext.getPackageName());
//								 v.setBackgroundResource(layoutResId2);
//								 break;
//						
//						}
//						return false;
//					}
//				}
//			);
//		tabBtn02.setOnClickListener(new Button.OnClickListener() {
//        	public void onClick(View v) {
//	        	myStorage(tabBtn02);
//	        }
//        });
//		
//		layoutResId = context.getResources().getIdentifier("tabBtn03", "id", fcontext.getPackageName());
//		tabBtn03 = (Button)xmlUI.findViewById(layoutResId);
//		tabBtn03.setOnTouchListener(
//				new View.OnTouchListener() {
//					public boolean onTouch(View v, MotionEvent event) {
//						switch(event.getAction()) {
//							case MotionEvent.ACTION_DOWN:
//								int layoutResId = fcontext.getResources().getIdentifier("tab03_on", "drawable", fcontext.getPackageName());
//								 v.setBackgroundResource(layoutResId);
//								 setTabButtonMode(tabBtn03);
//								 break;
//							case MotionEvent.ACTION_UP:
//								 int layoutResId2 = fcontext.getResources().getIdentifier("tab03_on", "drawable", fcontext.getPackageName());
//								 v.setBackgroundResource(layoutResId2);
//								 break;
//						
//						}
//						return false;
//					}
//				}
//			);
//		tabBtn03.setOnClickListener(new Button.OnClickListener() {
//        	public void onClick(View v) {
//	        	publicStorage("public");
//	        }
//        });
//		
//		layoutResId = context.getResources().getIdentifier("tabBtn04", "id", fcontext.getPackageName());
//		tabBtn04 = (Button)xmlUI.findViewById(layoutResId);
//		tabBtn04.setOnTouchListener(
//				new View.OnTouchListener() {
//					public boolean onTouch(View v, MotionEvent event) {
//						switch(event.getAction()) {
//							case MotionEvent.ACTION_DOWN:
//								int layoutResId = fcontext.getResources().getIdentifier("tab04_on", "drawable", fcontext.getPackageName());
//								 v.setBackgroundResource(layoutResId);
//								 setTabButtonMode(tabBtn04);
//								 break;
//							case MotionEvent.ACTION_UP:
//								int layoutResId2 = fcontext.getResources().getIdentifier("tab04_on", "drawable", fcontext.getPackageName());
//								 v.setBackgroundResource(layoutResId2);
//								 break;
//						
//						}
//						return false;
//					}
//				}
//			);
//		tabBtn04.setOnClickListener(new Button.OnClickListener() {
//        	public void onClick(View v) {
//	        	
//	        }
//        });
		m_itemsArr = LoadChartDataManager.getUserItems();

		//초기실행시 로컬차트 로딩.
		myStorage(null);

//		initLoadTapViews();

		//2012. 8. 1  전체화면표시를 위해 layoutparam 줌 
		//2014. 10. 14 popupwindow 배경 반투명색>>
//		xmlUI.setLayoutParams(new LinearLayout.LayoutParams(this.layout.getWidth(), this.layout.getHeight()));
		Configuration config = COMUtil.apiView.getContext().getResources().getConfiguration();
		if (config.orientation == Configuration.ORIENTATION_LANDSCAPE)
		{
			//2020.05.08 by JJH >> 가로모드 작업 (차트 불러오기 팝업) start
//			xmlUI.setLayoutParams(new LinearLayout.LayoutParams((int)COMUtil.getPixel(280), (int)COMUtil.getPixel(350)));
			xmlUI.setLayoutParams(new LinearLayout.LayoutParams((int)COMUtil.getPixel(360), ViewGroup.LayoutParams.WRAP_CONTENT));
			//2020.05.08 by JJH >> 가로모드 작업 (차트 불러오기 팝업) end
		}
		else
		{
			xmlUI.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		}
		//2014. 10. 14 popupwindow 배경 반투명색<<
		layout.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL);	//2020.05.08 by JJH >> 가로모드 작업 (차트 불러오기 팝업)
		this.layout.addView(xmlUI);

		COMUtil.setGlobalFont(this.layout);

		alert_confirm = new AlertDialog.Builder(this.getContext());
		alert_confirm.setMessage("정말로 삭제하시겠습니까?").setCancelable(false).setPositiveButton("확인",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 'YES'
						editListItem();
					}
				}).setNegativeButton("취소",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 'No'
						return;
					}
				});

		//2019.04.15 원터치 차트설정불러오기 추가 - lyj
		layoutResId = context.getResources().getIdentifier("tv_onetouch", "id", context.getPackageName());
		TextView tvOneTouch = (TextView)xmlUI.findViewById(layoutResId) ;
		layoutResId = context.getResources().getIdentifier("chk_onetouch", "id", context.getPackageName());
		chkOneTouch = (CheckBox)xmlUI.findViewById(layoutResId);
		chkOneTouch.setChecked(COMUtil._mainFrame.bShowOneTouch);

		chkOneTouch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				COMUtil._mainFrame.bShowOneTouch = chkOneTouch.isChecked();
				Base11 base11 = (Base11)COMUtil._mainFrame.mainBase.baseP;
				base11.showOneTouch(COMUtil._mainFrame.bShowOneTouch);
				base11.resizeChart();
				Hashtable<String, Object> dic = new Hashtable<String, Object>();
				String sVal = "0";
				if(COMUtil._mainFrame.bShowOneTouch)
					sVal = "1";

				dic.put("isOneTouchMode", ""+sVal);

				if (COMUtil._mainFrame.userProtocol != null)
					COMUtil._mainFrame.userProtocol.requestInfo(COMUtil._TAG_SET_ONETOUCHMODE, dic);

				base11.resetOneTouchList();
			}
		});

		if(!COMUtil._mainFrame.isOneTouchSet){
			tvOneTouch.setTextColor(CoSys.DISABLE_TEXT_COLOR);
			chkOneTouch.setEnabled(false);
			chkOneTouch.setChecked(false);
		}
		//2019.04.15 원터치 차트설정불러오기 추가 - lyj end



	}

	public void loadItem(){
		if(Adapter!=null && Adapter.itemChecked.size()>0) {
			boolean bChecked = false;
			int nSelIndex = -1;
			itemChecked=Adapter.itemChecked;
			for(int i=itemChecked.size()-1; i>=0; i--) {
				if(itemChecked.get(i).equals(true)) {
					bChecked = true;
					nSelIndex = i;
					break;
				}
			}
			if(nSelIndex>=0)
			{
				COMUtil._mainFrame.setLocalStorageState(cursor, nSelIndex);
				if(COMUtil.apiMode) {
					if(COMUtil.loadchartPopup!=null) {
						COMUtil.loadchartPopup.dismiss();
						COMUtil.loadchartPopup=null;
					}
				}
			}
		}
	}

	public void editList() {
		if(Adapter!=null && Adapter.itemChecked.size()>0) {
			boolean bChecked = false;
			itemChecked=Adapter.itemChecked;
			for(int i=itemChecked.size()-1; i>=0; i--) {
				if(itemChecked.get(i).equals(true)) {
					bChecked = true;
					break;
				}
			}

			if(bChecked)
			{
				AlertDialog alert = alert_confirm.create();
				alert.show();
			}
		}

	}
	public void editListItem() {
		db = mHelper.getWritableDatabase();
		if(this.mode.equals("public")) { //공유차트 삭제.
			for(int i=0; i<m_itemsArr.size(); i++) {
				if(itemChecked.get(i).equals(true)) {
					//DB 아이템 삭제 
					LoadCellItem item = m_itemsArr.get(i);
					String uid = item.getuid();
					deleteItem(uid);

					m_itemsArr.remove(i);
					itemChecked.remove(i);
				}
			}
			// 커스텀 ArrayAdapter 선언/초기화.
			if(m_scvAdapter!=null) m_scvAdapter.notifyDataSetChanged();
		} else { //local chart.
			itemChecked=Adapter.itemChecked;
			String strLocalTable = COMUtil._mainFrame.strLocalFileName;
			for(int i=itemChecked.size()-1; i>=0; i--) {
				if(itemChecked.get(i).equals(true)) {
					//DB 아이템 삭제 
					cursor.moveToPosition(i);
					String uid = cursor.getString(0);
					db.execSQL("DELETE FROM "+strLocalTable+" where _id="+uid);
					itemChecked.remove(i);

					//2015. 1. 13 by lyk - 동일지표 항목 파일 삭제
					String saveTitle = cursor.getString(25);
					COMUtil.delAddJipyoList(COMUtil._mainFrame.strFileName+saveTitle);
					//2015. 1. 13 by lyk - 동일지표 항목 파일 삭제 end
				}
			}
			createLocalChartList();
			Adapter.notifyDataSetChanged();
		}


		//if(COMUtil._mainFrame.userProtocol!=null) COMUtil._mainFrame.userProtocol.requestInfo(COMUtil._TAG_SAVE_LOCAL_CLOUD, null);
	}

	private void initLoadTapViews()
	{
//		//각 버튼들 로드
//		int layoutResId = context.getResources().getIdentifier("mobileLoadBtn", "id", context.getPackageName());
//		final Button mobileLoadBtn = (Button)xmlUI.findViewById(layoutResId);
//		//indicatorSetBtn.setTextColor(Color.rgb(255,100,0));
//		layoutResId = context.getResources().getIdentifier("htsLoadBtn", "id", context.getPackageName());
//		final Button htsLoadBtn = (Button)xmlUI.findViewById(layoutResId);
//		layoutResId = context.getResources().getIdentifier("load_hts_chart", "id", context.getPackageName());
//		final Button loadhtsbtn = (Button)xmlUI.findViewById(layoutResId);
//		layoutResId = context.getResources().getIdentifier("load_linear", "id", context.getPackageName());
//		final LinearLayout llbtn = (LinearLayout)xmlUI.findViewById(layoutResId);
//
//		if(COMUtil.bIsForeignFuture) {
//			layoutResId = context.getResources().getIdentifier("setLoadTap", "id", context.getPackageName());
//			LinearLayout taplayout = (LinearLayout)xmlUI.findViewById(layoutResId);
//			taplayout.setVisibility(GONE);
//		}
//
//		// 레이아웃 로드
//		loadhtsbtn.setOnClickListener(new OnClickListener() { //hts 버튼 이벤트 하는중
//			@Override
//			public void onClick(View v) {
//
//					DRAlertDialog alert = new DRAlertDialog(COMUtil.apiView.getContext());
//					alert.setTitle("HTS 사용자 설정 차트 불러오기");
//					alert.setMessage("HTS 사용자 설정 차트 중 모바일로 보내기를 실행한 차트를 불러올 수 있습니다. HTS 사용자 설정 차트를 불러오시겠습니까?");
//					alert.setNoButton("취소", null);
//					alert.setYesButton("확인",
//							new DialogInterface.OnClickListener() {
//								@Override
//								public void onClick(DialogInterface dialog,
//													int which) {
//									if(COMUtil._mainFrame.userProtocol!=null)
//										COMUtil._mainFrame.userProtocol.requestInfo(COMUtil._TAG_LOAD_HTS_CLOUD, null);
//									dialog.dismiss();
//								}
//							});
//					alert.show();
//					COMUtil.g_chartDialog = alert;
//
//
//
//			}
//		});
//
//		mobileLoadBtn.setSelected(true);
//		mobileLoadBtn.setTextColor(Color.rgb(215,57,49));
//		mobileLoadBtn.setTypeface(COMUtil.typefaceBold);
//
//		mobileLoadBtn.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//                if(m_nMode != MTS_CHART) {
//                    m_nMode = MTS_CHART;
//
//                    mobileLoadBtn.setSelected(true);
//					mobileLoadBtn.setTextColor(Color.rgb(215,57,49));
//					mobileLoadBtn.setTypeface(COMUtil.typefaceBold);
//                    htsLoadBtn.setSelected(false);
//					htsLoadBtn.setTextColor(Color.rgb(105,105,105));
//					htsLoadBtn.setTypeface(COMUtil.typeface);
//                    llbtn.setVisibility(GONE);
//
//                    mHelper = null;
//                    createLocalChartList();
//                }
//			}
//		});
//
//		htsLoadBtn.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//                if(m_nMode != HTS_CHART) {
//                    m_nMode = HTS_CHART;
//                    mobileLoadBtn.setSelected(false);
//					mobileLoadBtn.setTextColor(Color.rgb(105,105,105));
//					mobileLoadBtn.setTypeface(COMUtil.typeface);
//                    htsLoadBtn.setSelected(true);
//					htsLoadBtn.setTextColor(Color.rgb(215,57,49));
//					htsLoadBtn.setTypeface(COMUtil.typefaceBold);
//                    llbtn.setVisibility(VISIBLE);
//
//                    mHelper = null;
//                    createLocalChartList();
//                }
//			}
//		});
//		setNormalSetView();

	}
	//2016. 6. 17 확인버튼 enable 처리
	public void enableApplyButton()
	{
		int layoutResId = context.getResources().getIdentifier("chartload_btn_accept", "id", context.getPackageName());
		Button editFunc = (Button)xmlUI.findViewById(layoutResId);
		editFunc.setEnabled(true);
	}

	public void deleteItemList(Cursor recvCursor, int pos) {
		if(Adapter!=null) {
//				AlertDialog alert = alert_confirm.create();
			final int nPos = pos;
			DRAlertDialog alert = new DRAlertDialog(COMUtil.apiView.getContext());
//			alert.setTitle("알림");
//			alert.setMessage("정말로 삭제하시겠습니까?");
			alert.setMessage("저장 내용을 삭제합니다.");
			alert.setYesButton("삭제",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,int which) {
							//editListItem();
							db = mHelper.getWritableDatabase();
							String strLocalTable = COMUtil._mainFrame.strLocalFileName;
                            if(m_nMode == HTS_CHART)
                                strLocalTable = COMUtil._mainFrame.strLocalFileName+"_hts";
							cursor.moveToPosition(nPos);
							String uid = cursor.getString(0);
							db.execSQL("DELETE FROM "+strLocalTable+" where _id="+uid);

							//2015. 1. 13 by lyk - 동일지표 항목 파일 삭제
							String saveTitle = cursor.getString(25);
							COMUtil.delAddJipyoList(COMUtil._mainFrame.strFileName+saveTitle);
							//2015. 1. 13 by lyk - 동일지표 항목 파일 삭제 end

							createLocalChartList();
							Adapter.notifyDataSetChanged();
							//if(COMUtil._mainFrame.userProtocol!=null) COMUtil._mainFrame.userProtocol.requestInfo(COMUtil._TAG_SAVE_LOCAL_CLOUD, null);
							dialog.dismiss();

							//2019.04.15 원터치 차트설정불러오기 추가 - lyj
							Base11 base11 = (Base11)COMUtil._mainFrame.mainBase.baseP;
							base11.resetOneTouchList();
							//2019.04.15 원터치 차트설정불러오기 추가 - lyj end

							if(COMUtil.modifySavedPopup!=null) {
								COMUtil.modifySavedPopup.dismiss();
								COMUtil.modifySavedPopup = null;
							}


						}
					});
			alert.setNoButton("취소", null);
			alert.show();
			COMUtil.g_chartDialog = alert;
		}
	}

	public URL connectUrl = null;
	String lineEnd = "\r\n";
	String twoHyphens = "--";
	String boundary = "*****";
	private void deleteItem(String uid) {
		try {
			if(this.mode.equals("public")) { //공유차트 삭제.
				String urlstr = "http://218.38.18.171/smartPhone/delete.php?uid=";
				urlstr += uid + "&deviceID=" + COMUtil.deviceID;
				URL url = new URL(
						urlstr);
				XmlPullParserFactory factory = XmlPullParserFactory
						.newInstance();
				XmlPullParser parser = factory.newPullParser();
				parser.setInput(url.openStream(), "utf-8");
			}
//			else { //디바이스에서 삭제.
//				cursor = db.rawQuery("DELETE FROM chartsavedata where uid="+uid, null);
//				
//			}
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	private void close() {
		//2012. 8. 21 불러오기창 데이터를 불러왔을 때 불러오기창 닫히게 수정 : SL_tab12
//		if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB)) { //Pad
//			COMUtil.closeLoadChartPopover();
//		} else {
		layout.removeView(xmlUI);
		COMUtil.unbindDrawables(xmlUI);
		System.gc();

		if(COMUtil.apiMode) {
			if(COMUtil.loadchartPopup!=null) {
				COMUtil.loadchartPopup.dismiss();
				COMUtil.loadchartPopup=null;
			}
		}
//		}
		if(m_scvAdapter!=null) m_scvAdapter.clear();
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

	//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		
//		COMUtil._loadChartController=this;
//		
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        
//		setContentView(R.layout.chart_load);
//		
//		//Button btnBack = (Button) findViewById(R.id.frameaBtnBack);
//		Button btnFunc = (Button) findViewById(R.id.frameaBtnFunction);
//		TextView textTitle = (TextView) findViewById(R.id.frameaTitle);
//		
//		btnFunc.setOnClickListener(new Button.OnClickListener() {
//        	public void onClick(View v) {
//	        	try {
//	        		setResult(RESULT_CANCELED);
//	        		finish();
//	        	}
//	        	catch (Exception e) {        	     
//	        	}
//	        }
//        });
//		
//		//m_itemsArr = new ArrayList<SigChoiceItem>();
//
//		
//		tabBtn01 = (Button) findViewById(R.id.tabBtn01);
//		tabBtn01.setOnTouchListener(
//			new View.OnTouchListener() {
//				public boolean onTouch(View v, MotionEvent event) {
//					switch(event.getAction()) {
//						case MotionEvent.ACTION_DOWN:
//							 v.setBackgroundResource(R.drawable.tab01_on);
//							 setTabButtonMode(tabBtn01);
//							 break;
//						case MotionEvent.ACTION_UP:
//							 v.setBackgroundResource(R.drawable.tab01_on);
//							 break;
//					
//					}
//					return false;
//				}
//			}
//		);
//		tabBtn01.setOnClickListener(new Button.OnClickListener() {
//        	public void onClick(View v) {
//	        	proStorage(tabBtn01);
//	        }
//        });
//		
//		
//		tabBtn02 = (Button) findViewById(R.id.tabBtn02);
//		tabBtn02.setOnTouchListener(
//				new View.OnTouchListener() {
//					public boolean onTouch(View v, MotionEvent event) {
//						switch(event.getAction()) {
//							case MotionEvent.ACTION_DOWN:
//								 v.setBackgroundResource(R.drawable.tab02_on);
//								 setTabButtonMode(tabBtn02);
//								 break;
//							case MotionEvent.ACTION_UP:
//								 v.setBackgroundResource(R.drawable.tab02_on);
//								 break;
//						
//						}
//						return false;
//					}
//				}
//			);
//		tabBtn02.setOnClickListener(new Button.OnClickListener() {
//        	public void onClick(View v) {
//	        	myStorage(tabBtn02);
//	        }
//        });
//		
//		tabBtn03 = (Button) findViewById(R.id.tabBtn03);
//		tabBtn03.setOnTouchListener(
//				new View.OnTouchListener() {
//					public boolean onTouch(View v, MotionEvent event) {
//						switch(event.getAction()) {
//							case MotionEvent.ACTION_DOWN:
//								 v.setBackgroundResource(R.drawable.tab03_on);
//								 setTabButtonMode(tabBtn03);
//								 break;
//							case MotionEvent.ACTION_UP:
//								 v.setBackgroundResource(R.drawable.tab03_on);
//								 break;
//						
//						}
//						return false;
//					}
//				}
//			);
//		tabBtn03.setOnClickListener(new Button.OnClickListener() {
//        	public void onClick(View v) {
//	        	publicStorage(tabBtn03);
//	        }
//        });
//		
//		tabBtn04 = (Button) findViewById(R.id.tabBtn04);
//		tabBtn04.setOnTouchListener(
//				new View.OnTouchListener() {
//					public boolean onTouch(View v, MotionEvent event) {
//						switch(event.getAction()) {
//							case MotionEvent.ACTION_DOWN:
//								 v.setBackgroundResource(R.drawable.tab04_on);
//								 setTabButtonMode(tabBtn04);
//								 break;
//							case MotionEvent.ACTION_UP:
//								 v.setBackgroundResource(R.drawable.tab04_on);
//								 break;
//						
//						}
//						return false;
//					}
//				}
//			);
//		tabBtn04.setOnClickListener(new Button.OnClickListener() {
//        	public void onClick(View v) {
//	        	
//	        }
//        });
//		m_itemsArr = LoadChartDataManager.getUserItems();
//		
//		//초기실행시 공유차트 로딩.
//		loadChartList("", urlstr);
//
//		// 커스텀 ArrayAdapter 선언/초기화.
//		m_scvAdapter = new MyArrayAdapter(this, m_itemsArr);
//		
//		// 본 Activity의 아답터로 m_scvAdapter 지정.
//		setListAdapter(m_scvAdapter);
//	}
	//2012. 7. 24  차트로드화면 탭버튼 이미지변환함수 주석처리 
//	private void setTabButtonMode(Button target) {
//		Button[] btns = {tabBtn01, tabBtn02, tabBtn03, tabBtn04};
//		int[] btnR = {context.getResources().getIdentifier("tab01_off", "drawable", context.getPackageName()), 
//				context.getResources().getIdentifier("tab02_off", "drawable", context.getPackageName()), 
//				context.getResources().getIdentifier("tab03_off", "drawable", context.getPackageName()), 
//				context.getResources().getIdentifier("tab04_off", "drawable", context.getPackageName())};
//		for(int i=0; i<4; i++) {
//			btns[i].setBackgroundResource(btnR[i]);
//
//		}
//		if(target.equals(tabBtn01)) {
//			target.setBackgroundResource(context.getResources().getIdentifier("tab01_on", "drawable", context.getPackageName()));
//		} else if(target.equals(tabBtn02)) {
//			target.setBackgroundResource(context.getResources().getIdentifier("tab02_on", "drawable", context.getPackageName()));
//		} else if(target.equals(tabBtn03)) {
//			target.setBackgroundResource(context.getResources().getIdentifier("tab03_on", "drawable", context.getPackageName()));
//		} else if(target.equals(tabBtn04)) {
//			target.setBackgroundResource(context.getResources().getIdentifier("tab04_on", "drawable", context.getPackageName()));
//		}
//	}
	private void proStorage(Button target) {
		this.mode="";
		if(addButton!=null) {
			list.removeFooterView(addButton);
			addButton=null;
		}
		m_itemsArr.clear();
		itemChecked.clear();
		m_itemsArr = LoadChartDataManager.getUserItems();

		urlstr = "http://218.38.18.171/smartPhone/dnloadPro.php";
		loadChartList("", urlstr);
		// 커스텀 ArrayAdapter 선언/초기화.
		m_scvAdapter = null;
		m_scvAdapter = new MyArrayAdapter(context, m_itemsArr);

		// 본 Activity의 아답터로 m_scvAdapter 지정.
		list.setAdapter(m_scvAdapter);
		list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
				onListItemClick(null,arg1,arg2,arg3);
			}
		});
		// 커스텀 ArrayAdapter 선언/초기화.
		m_scvAdapter.notifyDataSetChanged();
	}
	private void myStorage(Button target) {
		this.mode="";
		if(addButton!=null) {
			list.removeFooterView(addButton);
			addButton=null;
		}
		m_itemsArr.clear();//초기화.
		itemChecked.clear();
		m_itemsArr = LoadChartDataManager.getUserItems();

//		if(COMUtil.isModifyDetail) {
		createLocalChartList();
//		} else {
//			localChartList();
//		}

		COMUtil.isModifyDetail = false;
	}


	String mode = "";
	private void publicStorage(String search) {
		this.mode = search;
		if(mode.equals("")) return;
		if(addButton==null) {
			addButton = new Button(this.getContext());
			addButton.setWidth(100);
			addButton.setHeight(20);
			addButton.setText("더보기");
			addButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					publicStorage("requestAdd");
				}
			});
			list.addFooterView(addButton);
		}

		if(search.equals("requestAdd")) {
			reqCnt += 10;
		}

		urlstr = "http://218.38.18.171/smartPhone/dnload.php?search=";
		urlstr = urlstr + "&reqCnt=" + reqCnt;

		m_itemsArr.clear();
		itemChecked.clear();
		m_itemsArr = LoadChartDataManager.getUserItems();

		loadChartList("", urlstr);

		// 커스텀 ArrayAdapter 선언/초기화.
//		m_scvAdapter = null;
//		m_scvAdapter.clear();
		if(m_scvAdapter==null) {
			m_scvAdapter = new MyArrayAdapter(context, m_itemsArr);
		}

		// 본 Activity의 아답터로 m_scvAdapter 지정.
		list.setAdapter(m_scvAdapter);
		list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
				onListItemClick(null,arg1,arg2,arg3);
			}
		});
		list.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

		// 커스텀 ArrayAdapter 선언/초기화.
		if(m_scvAdapter!=null) m_scvAdapter.notifyDataSetChanged();

	}

	/* 폰에 저장되어있는 차트리스트 처리 */
	private ChartsaveDBHelper mHelper=null;
	private SQLiteDatabase db=null;
	private Cursor cursor = null;
//	private void localChartList() {
//		//클라우드 저장목록 호출
//		if(COMUtil._mainFrame.userProtocol!=null) COMUtil._mainFrame.userProtocol.requestInfo(COMUtil._TAG_LOAD_LOCAL_CLOUD, null);
//	}

	public void createLocalChartList() {
		try {
			String strLocalTable = COMUtil._mainFrame.strLocalFileName;
			if(m_nMode == HTS_CHART)
			{
				strLocalTable = COMUtil._mainFrame.strLocalFileName+"_hts";
			}
			if(mHelper==null) {
				mHelper = new ChartsaveDBHelper(context, strLocalTable);
			}
			db = mHelper.getWritableDatabase();

			//		cursor = db.rawQuery("SELECT * FROM "+strLocalTable+" order by _id desc", null);
			cursor = db.rawQuery("SELECT * FROM "+strLocalTable+" order by _id desc", null);
			COMUtil._chartMain.startManagingCursor(cursor);//cursor 자동관리.

			m_itemsArr.clear();//초기화.
			itemChecked.clear();
			m_itemsArr = LoadChartDataManager.getUserItems();

			Adapter = new ChartsaveCursorAdapter(context, cursor);
			Adapter.setMode(m_nMode);

			// 본 Activity의 아답터로 m_scvAdapter 지정.
//			list.setDividerHeight((int)COMUtil.getPixel(18));
			list.setAdapter(Adapter);
			list.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
										long arg3) {
					onListItemClickLocal(null,arg1,arg2,arg3);

				}

			});
			COMUtil._chartMain.stopManagingCursor(cursor);
			//		cursor.close();
			mHelper.close();

			// 커스텀 ArrayAdapter 선언/초기화.
			Adapter.notifyDataSetChanged();

//			setChangeCount();

			//2019.04.15 원터치 차트설정불러오기 추가 - lyj
			Base11 base11 = (Base11)COMUtil._mainFrame.mainBase.baseP;
			base11.resetOneTouchList();
			//2019.04.15 원터치 차트설정불러오기 추가 - lyj end

		} catch(Exception e) {
			System.out.println("Debug:"+e.getMessage());
		}
	}

	//	Vector loadChartList = new Vector();
	/* 공유차트 및 전문가 차트리스트 처리 */
	private void loadChartList(String search, String urlstr) {
		if(loadList!=null) {
			loadList.clear();
		}

		try {
			URL url = new URL(
					urlstr+search);
			XmlPullParserFactory factory = XmlPullParserFactory
					.newInstance();
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(url.openStream(), "utf-8");

			int eventType = parser.getEventType();
			String tag="";
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
					case XmlPullParser.START_DOCUMENT:
						if(loadList==null) {
							loadList = new Vector<Hashtable<String, Object>>();
						} else {
							loadList.clear();
						}
						if(loadItem!=null && loadItem.size()>0) {
							loadItem.clear();
						}
						break;
					case XmlPullParser.END_DOCUMENT:

						break;
					case XmlPullParser.START_TAG:
						tag = parser.getName();
						if(tag.equals("item")) {
							loadItem = new Hashtable<String, Object>();
						}

						if (tag.equals("verInfo")) {
							loadItem.put("verInfo", parser.nextText());
						} else if(tag.equals("uid")) {
							loadItem.put("uid", parser.nextText());
						} else if(tag.equals("imgurl")) {
							loadItem.put("imgurl", parser.nextText());
						} else if(tag.equals("graphList")) {//Vector.
							loadItem.put("graphList", parser.nextText());
						} else if(tag.equals("symbol")) {
							loadItem.put("symbol", parser.nextText());
						} else if(tag.equals("lcode")) {
							loadItem.put("lcode", parser.nextText());
						} else if(tag.equals("dataTypeName")) {
							loadItem.put("dataTypeName", parser.nextText());
						} else if(tag.equals("count")) {
							loadItem.put("count", parser.nextText());
						} else if(tag.equals("viewCount")) {
							loadItem.put("viewCount", parser.nextText());
						} else if(tag.equals("valueOfMin")) {
							loadItem.put("valueOfMin", parser.nextText());
						} else if(tag.equals("deviceID")) {
							loadItem.put("deviceID", parser.nextText());
						} else if(tag.equals("analInfo")) { //분석툴 정보.
							//title=추세/cnt=2/x:1:y:2-x:3:y:4|
							loadItem.put("analInfo", parser.nextText());
						} else if(tag.equals("userId")) {
							loadItem.put("userId", parser.nextText());
						} else if(tag.equals("uesrIp")) {
							loadItem.put("uesrIp", parser.nextText());
						} else if(tag.equals("codeName")) {
							loadItem.put("codeName", parser.nextText());
						} else if(tag.equals("title")) {
							loadItem.put("title", parser.nextText());
						} else if(tag.equals("detail")) {
							loadItem.put("detail", parser.nextText());
						} else if(tag.equals("saveDate")) {
							loadItem.put("saveDate", parser.nextText());
						} else if(tag.equals("apCode")) {
							loadItem.put("apCode", parser.nextText());
						} else if(tag.equals("chartMode")) {
							loadItem.put("chartMode", parser.nextText());
						} else if(tag.equals("divideInfo")) {
							loadItem.put("divideInfo", parser.nextText());
						}
						break;
					case XmlPullParser.END_TAG:
						tag = parser.getName();
						if(tag.equals("item")) {
							if(loadItem.size()>0) {
								loadList.addElement(loadItem);
								loadItem = null;
							}
						}
						break;
					case XmlPullParser.TEXT:
						break;
				}
				eventType = parser.next();
			}

			this.makeItems();
		} catch (Exception e) {

		}

	}

	private void makeItems() {
		if(m_itemsArr!=null) {
			m_itemsArr.clear();
			itemChecked.clear();
		}

		for(int i=0; i<loadList.size(); i++) {
			Hashtable<String, Object> item = (Hashtable<String, Object>)loadList.get(i);
			LoadCellItem item1 = new LoadCellItem(
					(String)item.get("verInfo"),
					(String)item.get("uid"),
					(String)item.get("imgurl"),
					(String)item.get("graphList"),
					(String)item.get("symbol"),
					(String)item.get("lcode"),
					(String)item.get("dataTypeName"),
					(String)item.get("count"),
					(String)item.get("viewCount"),
					(String)item.get("valueOfMin"),
					(String)item.get("deviceID"),
					(String)item.get("analInfo"),
					(String)item.get("userId"),
					(String)item.get("userIp"),
					(String)item.get("codeName"),
					(String)item.get("title"),
					(String)item.get("detail"),
					(String)item.get("saveDate"),
					(String)item.get("apCode")
			);

			m_itemsArr.add(item1);
			itemChecked.add(false);
		}

	}

	//OnClick에대한 처리루틴.
	public void onClick(View view) {
//		int btnID = view.getId();
//		switch(btnID) {
//			case R.id.frameaBtnFunction:
//
//		}
	}

	/** 공유차트로 저장된 차트를 로딩할때 호출됨. **/
	public void onListItemClick(ListView l, View v, int position, long id) {
		synchronized (this)    {
			COMUtil._mainFrame.mainBase.initChart("");

			//implement here for click item.
			if(loadList==null) return;

			String textTochange = "차트를 불러옵니다.";
			COMUtil.showMessage(context, textTochange); //Context, String msg

			Hashtable<String, Object> item = (Hashtable<String, Object>)loadList.get(position);
			//선택된 차트정보 처리.
			COMUtil.saveDate=(String)item.get("saveDate");
			COMUtil.userId=(String)item.get("userId");
			COMUtil.userIp=(String)item.get("userIp");
			COMUtil.detail=(String)item.get("detail");
			COMUtil.title=(String)item.get("title");

			String strChartMode = (String)item.get("chartMode");

			if(strChartMode!=null && !strChartMode.equals("")) {
				COMUtil.chartMode=Integer.parseInt(strChartMode.substring(0,1));
				if(!COMUtil.deviceMode.equals(COMUtil.HONEYCOMB)) {
					COMUtil.chartMode = COMUtil.BASIC_CHART;
				}
				if(strChartMode.length()==3)
				{
					COMUtil.setSkinType(Integer.parseInt(strChartMode.substring(2,3)));
				}
			}

//	    	int divideCnt = 0;
			if(COMUtil.chartMode == COMUtil.DIVIDE_CHART) {
				//"/" 구분자로 데이터를 분리하여, Base11에 전달한다.
				StringTokenizer st = new StringTokenizer((String)item.get("divideInfo"), "/");//divideinfo
				Vector<String> divideInfos = new Vector<String>();
				while (st.hasMoreTokens()) {
					divideInfos.add(st.nextToken());
				}
//	            divideCnt = Integer.parseInt(divideInfos.get(0));
				int nSel = Integer.parseInt(divideInfos.get(1));
				String isSyncJongmokStr = divideInfos.get(2);
				String isSyncJugiStr = divideInfos.get(3);
				boolean isSyncJongmok=false;
				boolean isSyncJugi=false;;
				if(isSyncJongmokStr.equals("1")) isSyncJongmok = true;
				if(isSyncJugiStr.equals("1")) isSyncJugi = true;

				Hashtable<String, Vector<String>> items = new Hashtable<String, Vector<String>>();

				st = new StringTokenizer((String)item.get("symbol"), "/");//symbol
				Vector<String> symbols = new Vector<String>();
				while (st.hasMoreTokens()) {
					symbols.add(st.nextToken());
				}
				items.put("symbols", symbols);

				st = new StringTokenizer((String)item.get("lcode"), "/");//lcodes
				Vector<String> lcodes = new Vector<String>();
				while (st.hasMoreTokens()) {
					lcodes.add(st.nextToken());
				}
				items.put("lcodes", lcodes);

				st = new StringTokenizer((String)item.get("apCode"), "/");//apCodes
				Vector<String> apCodes = new Vector<String>();
				while (st.hasMoreTokens()) {
					apCodes.add(st.nextToken());
				}
				items.put("apCodes", apCodes);

				st = new StringTokenizer((String)item.get("dataTypeName"), "/");//dataTypeNames
				Vector<String> dataTypeNames = new Vector<String>();
				while (st.hasMoreTokens()) {
					dataTypeNames.add(st.nextToken());
				}
				items.put("dataTypeNames", dataTypeNames);
				item.put("dataTypeNames", dataTypeNames);//COMUtil.setStateStorage에서 사용.

				st = new StringTokenizer((String)item.get("count"), "/");//counts
				Vector<String> counts = new Vector<String>();
				while (st.hasMoreTokens()) {
					counts.add(st.nextToken());
				}
				items.put("counts", counts);

				st = new StringTokenizer((String)item.get("viewCount"), "/");//viewNums
				Vector<String> viewNums = new Vector<String>();
				while (st.hasMoreTokens()) {
					viewNums.add(st.nextToken());
				}
				items.put("viewnums", viewNums);

				st = new StringTokenizer((String)item.get("valueOfMin"), "/");//units
				Vector<String> units = new Vector<String>();
				while (st.hasMoreTokens()) {
					units.add(st.nextToken());
				}
				items.put("units", units);

				//Base11의 분할차트 함수 호출.
				Base11 base11 = (Base11)COMUtil._mainFrame.mainBase.baseP;
				base11.setStorageDivision(nSel, isSyncJongmok, isSyncJugi, items, true);

			}else {
				//1분할로 초기화 처리 (Base11에 이벤트 호출할 것)
				Base11 base11 = (Base11)COMUtil._mainFrame.mainBase.baseP;
				base11.setStorageDivision(11, false, false, null, false);

				//"/" 구분자로 데이터를 분리
				StringTokenizer st = new StringTokenizer((String)item.get("symbol"), "/");//symbol
				Vector<String> symbols = new Vector<String>();
				while (st.hasMoreTokens()) {
					symbols.add(st.nextToken());
				}

				st = new StringTokenizer((String)item.get("lcode"), "/");//lcodes
				Vector<String> lcodes = new Vector<String>();
				while (st.hasMoreTokens()) {
					lcodes.add(st.nextToken());
				}

				st = new StringTokenizer((String)item.get("apCode"), "/");//apCodes
				Vector<String> apCodes = new Vector<String>();
				while (st.hasMoreTokens()) {
					apCodes.add(st.nextToken());
				}

				st = new StringTokenizer((String)item.get("dataTypeName"), "/");//dataTypeNames
				Vector<String> dataTypeNames = new Vector<String>();
				while (st.hasMoreTokens()) {
					dataTypeNames.add(st.nextToken());
				}

				st = new StringTokenizer((String)item.get("count"), "/");//counts
				Vector<String> counts = new Vector<String>();
				while (st.hasMoreTokens()) {
					counts.add(st.nextToken());
				}

				st = new StringTokenizer((String)item.get("viewCount"), "/");//viewNums
				Vector<String> viewNums = new Vector<String>();
				while (st.hasMoreTokens()) {
					viewNums.add(st.nextToken());
				}

				st = new StringTokenizer((String)item.get("valueOfMin"), "/");//units
				Vector<String> units = new Vector<String>();
				while (st.hasMoreTokens()) {
					units.add(st.nextToken());
				}

				COMUtil.symbol=symbols.get(0);
				COMUtil.lcode=lcodes.get(0);
				if(dataTypeNames!=null && dataTypeNames.size()>0) {
					COMUtil.dataTypeName=dataTypeNames.get(0);
				}
				if(counts!=null && counts.size()>0) {
					COMUtil._mainFrame.mainBase.baseP._chart._cvm.setInquiryNum(Integer.parseInt(counts.get(0)));
				}
				if(viewNums!=null && viewNums.size()>0) {
					COMUtil._mainFrame.mainBase.baseP._chart._cvm.setViewNum(Integer.parseInt(viewNums.get(0)));
				} else {
					COMUtil._mainFrame.mainBase.baseP._chart._cvm.setViewNum(COMUtil._mainFrame.mainBase.baseP._chart._cvm.VIEW_NUM_ORG);
				}
				if(units!=null && units.size()>0) {
					COMUtil.unit=units.get(0);
				}
				if(apCodes!=null && apCodes.size()>0) {
					COMUtil.apCode=apCodes.get(0);
				}
			}

			//지표리스트.
			String strGraphList = (String)item.get("graphList");
			String[] token = strGraphList.split("\\/");
			Vector<Vector<String>> graphLists = new Vector<Vector<String>>();
			String str="";
			for(int i=0; i<token.length; i++) {
				StringTokenizer st = new StringTokenizer(token[i], "#");
				Vector<String> graphs=new Vector<String>();
				while (st.hasMoreTokens()) {
					str = COMUtil.getDecodeData(st.nextToken());
					graphs.add(str);
				}
				graphLists.add(graphs);
			}
			if(graphLists.size()>0) {
				item.put("graphList", graphLists.get(0));
				item.put("graphLists", graphLists);
			} else {
				item.put("graphList", new Vector());
				item.put("graphLists", graphLists);
			}

			//분석툴 정보 파싱.
			String strAnalInfo = (String)item.get("analInfo");
			token = strAnalInfo.split("\\/=/");
			Vector analInfoLists = new Vector();
			for(int i=0; i<token.length; i++) {
				analInfoLists.add(COMUtil.getAnalInfos(token[i]));
			}
			if(analInfoLists.size()>0) {
				item.put("analInfo", analInfoLists.get(0));
				item.put("analInfos", analInfoLists);
			} else {
				item.put("analInfo", new Vector());
				item.put("analInfos", analInfoLists);
			}

			COMUtil.setSendTrType("storageType");
			//item 에 graphList, analList 추가하기.

			COMUtil.loadItem = item;
			COMUtil.sendTR("storageType");

			close();
		}
	}

	/** 디바이스에 저장된 차트를 로딩할때 호출됨. **/
	public void onListItemClickLocal(ListView l, View v, int position, long id) {

		COMUtil._mainFrame.setLocalStorageState(cursor, position);
		//부모의 회전 
//		COMUtil._chartMain.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		close();
	}

	private  void setChangeCount()
	{

//		int layoutResId = context.getResources().getIdentifier("listcount", "id", context.getPackageName());
//		final TextView listcount = (TextView)xmlUI.findViewById(layoutResId);
//
//		layoutResId = context.getResources().getIdentifier("loadlist", "id", context.getPackageName());
//		final ListView lvMobile = (ListView) xmlUI.findViewById(layoutResId);
//
//		layoutResId = context.getResources().getIdentifier("emptylist", "id", context.getPackageName());
//		final LinearLayout llemptylist = (LinearLayout) xmlUI.findViewById(layoutResId);
//
//		layoutResId = context.getResources().getIdentifier("emptytext_hts1", "id", context.getPackageName());
//		final TextView tvemptytext_hts1 = (TextView) xmlUI.findViewById(layoutResId);
//		tvemptytext_hts1.setVisibility(GONE);
//
//		layoutResId = context.getResources().getIdentifier("emptytext_hts2", "id", context.getPackageName());
//		final TextView tvemptytext_hts2 = (TextView) xmlUI.findViewById(layoutResId);
//		tvemptytext_hts2.setVisibility(GONE);
//
//
//		int count = cursor.getCount();
//		int max_hts = 5;
//		int max_mts = 10;
//		if(count==0)
//		{
//			lvMobile.setVisibility(GONE);
//			llemptylist.setVisibility(VISIBLE);
//			if(m_nMode==HTS_CHART) {
//				tvemptytext_hts1.setVisibility(VISIBLE);
//				tvemptytext_hts2.setVisibility(VISIBLE);
//			}else
//			{
//				tvemptytext_hts1.setVisibility(GONE);
//				tvemptytext_hts2.setVisibility(GONE);
//			}
//		}
//		else
//		{
//			lvMobile.setVisibility(VISIBLE);
//			llemptylist.setVisibility(GONE);
//			tvemptytext_hts1.setVisibility(GONE);
//			tvemptytext_hts2.setVisibility(GONE);
//		}
//
//		if(m_nMode==HTS_CHART)
//			listcount.setText("저장된 차트 설정 (" + count +"/"+ max_hts +")");
//		else
//			listcount.setText("저장된 차트 설정 (" + count +"/"+ max_mts+")");
	}

	public int Mode() {
		return m_nMode;
	}

//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if(requestCode == LAUNCHED_ACTIVITY_JipyoSetup) {
//			if(resultCode == RESULT_OK)
//			{
//				String strSiseTitle, strSignalTitle;
//				strSiseTitle = data.getExtras().getString("SiseTitle");
//				strSignalTitle = data.getExtras().getString("SignalTitle");
//				
//				int nPosition = data.getExtras().getInt("listIndex");;
//				LoadCellItem oneItem = m_itemsArr.get(nPosition);
////				oneItem.setTitleSise(strSiseTitle);
////				oneItem.setTitleSignal(strSignalTitle);
//				
//				m_scvAdapter.notifyDataSetChanged();
//			}
//		}
//		else {
//			super.onActivityResult(requestCode, resultCode, data);
//		}
//	}

	private ArrayList<Boolean> itemChecked = new ArrayList<Boolean>();
	//ArrayAdapter에서 상속받는 커스텀 ArrayAdapter 정의.
	class MyArrayAdapter extends ArrayAdapter<LoadCellItem> {

		// 생성자 내부에서 초기화
		private Context context;
		private ViewWrapper wrapper = null;
		public ArrayList<LoadCellItem> mitems;
//	    int gnSigChoiceViewCellTypeID = COMUtil._mainFrame.getContext().getResources().getIdentifier("chart_load_celltype", "layout", COMUtil._mainFrame.getContext().getPackageName());	///< 화면의 layout ID.
//	    private static final int gnSigChoiceViewCellTypeID = gnSigChoiceViewCellTypeID0;

		// 생성자
		MyArrayAdapter(Context context, ArrayList<LoadCellItem> items) {
			super(context, COMUtil._mainFrame.getContext().getResources().getIdentifier("chart_load_celltype", "layout", COMUtil._mainFrame.getContext().getPackageName()), items);

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
					row = (View)inflater.inflate(COMUtil._mainFrame.getContext().getResources().getIdentifier("chart_load_celltype_tab", "layout", COMUtil._mainFrame.getContext().getPackageName()), null);
				}
				else
				{
					row = (View)inflater.inflate(COMUtil._mainFrame.getContext().getResources().getIdentifier("chart_load_celltype", "layout", COMUtil._mainFrame.getContext().getPackageName()), null);
				}

				wrapper = new ViewWrapper(row);
				row.setTag(wrapper);
			}
			else {
				wrapper = (ViewWrapper)row.getTag();
			}

			LoadCellItem oneItem = mitems.get(position);
			String urlstr = "http://218.38.18.171/smartPhone/"+oneItem.getimgurl();
			//imageDownloader.download(urlstr, wrapper.getCtrlImageView());
			wrapper.getCtrlTextView().setText(oneItem.getdetail());
			wrapper.getCtrlTitle01View().setText(oneItem.gettitle());
			wrapper.getCtrlTitle02View().setText("last saved on:"+oneItem.getsaveDate());
			wrapper.getCtrlTitle04View().setText(oneItem.getuserId());
			wrapper.getCtrlTitle03View().setText(oneItem.getuserId());
			if(oneItem.getdeviceID().equals(COMUtil.deviceID)) {
				wrapper.getCtrlVisible().setVisibility(View.VISIBLE);
			} else {
				wrapper.getCtrlVisible().setVisibility(View.INVISIBLE);
			}
			wrapper.getCtrlVisible().setId(position);
			wrapper.getCtrlVisible().setChecked(itemChecked.get(position));
			wrapper.getCtrlVisible().setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					CheckBox cb = (CheckBox)v;
					boolean isChecked = cb.isChecked();
					int pos = v.getId();
					if (isChecked) {
						itemChecked.set(pos, true);

					} else if (!isChecked) {
						itemChecked.set(pos, false);
						// do some operations here
					}
					editList();
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
		private ImageView ctlImage;
		private TextView  ctlCodeName, ctlTitle01, ctlTitle02, ctlTitle03, ctlTitle04;
		private CheckBox ctlVisible;

		ViewWrapper(View base) {
			this.base = base;
		}

		// 멤버 변수가 null일때만 findViewById를 호출
		// null이 아니면 저장된 instance 리턴 -> Overhaed 줄임s

		ImageView getCtrlImageView() {
			if(ctlImage == null) {
				int resId = COMUtil._mainFrame.getContext().getResources().getIdentifier("loadimg", "id", context.getPackageName());
				ctlImage = (ImageView)base.findViewById(resId);
			}
			return ctlImage;
		}

		TextView getCtrlTextView() {
			if(ctlCodeName == null) {
				int resId = COMUtil._mainFrame.getContext().getResources().getIdentifier("loadtextview", "id", context.getPackageName());
				ctlCodeName = (TextView)base.findViewById(resId);
			}
			return ctlCodeName;
		}

		TextView getCtrlTitle01View() {
			if(ctlTitle01 == null) {
				int resId = COMUtil._mainFrame.getContext().getResources().getIdentifier("title01", "id", context.getPackageName());
				ctlTitle01 = (TextView)base.findViewById(resId);
			}
			return ctlTitle01;
		}

		TextView getCtrlTitle02View() {
			if(ctlTitle02 == null) {
				int resId = COMUtil._mainFrame.getContext().getResources().getIdentifier("title02", "id", context.getPackageName());
				ctlTitle02 = (TextView)base.findViewById(resId);
			}
			return ctlTitle02;
		}

		TextView getCtrlTitle04View() {
			if(ctlTitle04 == null) {
				int resId = COMUtil._mainFrame.getContext().getResources().getIdentifier("title04", "id", context.getPackageName());
				ctlTitle04 = (TextView)base.findViewById(resId);
			}
			return ctlTitle04;
		}

		TextView getCtrlTitle03View() {
			if(ctlTitle03 == null) {
				int resId = COMUtil._mainFrame.getContext().getResources().getIdentifier("title03", "id", context.getPackageName());
				ctlTitle03 = (TextView)base.findViewById(resId);
			}
			return ctlTitle03;
		}
		CheckBox getCtrlVisible() {
			if(ctlVisible == null) {
				int resId = COMUtil._mainFrame.getContext().getResources().getIdentifier("checkBox", "id", context.getPackageName());
				ctlVisible = (CheckBox)base.findViewById(resId);
			}
			return ctlVisible;
		}
	}

}
