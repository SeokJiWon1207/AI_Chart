
package drfn.chart.comp;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import drfn.chart.base.LoadChartController;
import drfn.chart.util.COMUtil;

public class ChartsaveCursorAdapter extends CursorAdapter {
	private ViewWrapper2 wrapper = null;
	public ArrayList<Boolean> itemChecked = new ArrayList<Boolean>();
	public ArrayList<CheckBox> arCheckBox = new ArrayList<CheckBox>();
	//2013. 1. 29 적용,상세 버튼 어레이. -> 활성화/비활성화 하기 위함 
	public ArrayList<Button> arBtnAccept = new ArrayList<Button>();
	//2016. 6. 27 삭제버튼 어레이
	public ArrayList<Button> arBtnDel = new ArrayList<Button>();
	public ArrayList<Button> arBtnDetail = new ArrayList<Button>();

    //2020. 05. 19 by hyh - 라디오버튼 에러 수정 >>
	//CheckBox ctlCheckBox=null;
    //2020. 05. 19 by hyh - 라디오버튼 에러 수정 <<

	//2012. 10. 2  체크박스 크기 세팅할 해상도 관련 문제로 사용하기 위해 클레스멤버로 위치이동  : I106
	//2013.04.05 이미지 리사이징 코드 비적용   >>
//  boolean bHighResolution = false;
	//2012. 10. 2  체크박스 이미지들을 가지고 있을 listdrawable  : I106
//  StateListDrawable stateDrawable;
//  Drawable normal, press;
	//2013.04.05 이미지 리사이징 코드 비적용   <<


	//2013. 1. 28 체크박스의 이전 인덱스 저장. 라디오버튼처럼 동작을 위해서 이전 인덱스 저장후 하나라도 체크되어있게 함
	int nCheckedPos = -1;
	int m_nMode = LoadChartController.MTS_CHART;

	static int nNewViewCnt = 0;
	public ChartsaveCursorAdapter(Context context, Cursor cursor) {
		super(context, cursor);
		if(itemChecked.size()<1) {
			for(int i=0; i<cursor.getCount(); i++) {
				itemChecked.add(false); //초기화.
			}
		}

		//2012. 10. 2  해상도 크기로 리스트뷰 크기를 결정하기 위함  : I106
		//2013.04.05 이미지 리사이징 코드 비적용   >>
//		Display dis = ((WindowManager) context.getSystemService(context.WINDOW_SERVICE)).getDefaultDisplay();  
//		int mDisWidth = dis.getWidth();            // 가로 사이즈 
//		int mDisHeight = dis.getHeight();          // 세로 사이즈
//		
//		//2012. 10. 2  일부기기에서 리스트뷰 하단 짤리는 현상 : I95
//		if((mDisWidth >= 720 && mDisHeight >= 1280) || (mDisWidth >= 1280 && mDisHeight >= 720))
//		{
//			bHighResolution = true;
//		}
		//2013.04.05 이미지 리사이징 코드 비적용   <<

		//2012. 10. 2  체크박스 이미지 변경  : I106
//		stateDrawable = new StateListDrawable();
//		
//		int nChkBoxSize;
//		
//		if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
//		{
//			nChkBoxSize = (int)(COMUtil.getPixel(45) / 2);
//		}
//		else
//		{
//			if(bHighResolution)
//			{
//				nChkBoxSize = (int)COMUtil.getPixel(45);
//			} 
//			else
//			{
//				nChkBoxSize = (int)COMUtil.getPixel(30);
//			}
//		}
//		
//		int layoutResId = context.getResources().getIdentifier("checkbox_off_background2", "drawable", context.getPackageName());
//		Bitmap image = BitmapFactory.decodeResource(context.getResources(), layoutResId);	
//		Bitmap resizeImage = Bitmap.createScaledBitmap(image, nChkBoxSize, nChkBoxSize,true);
//		normal = (Drawable)(new BitmapDrawable(resizeImage));
//		
//		layoutResId = context.getResources().getIdentifier("checkbox_on_background2", "drawable", context.getPackageName());
//		image = BitmapFactory.decodeResource(context.getResources(), layoutResId);	
//		resizeImage = Bitmap.createScaledBitmap(image, nChkBoxSize, nChkBoxSize,true);
//		press = (Drawable)(new BitmapDrawable(resizeImage));
	}

	@Override
	public void bindView(View view, Context context, final Cursor cursor)
	{

		final int nPosition = view.getId();

		wrapper = (ViewWrapper2)view.getTag();
		
		/*
		 * 		 		
		 *	 	" divideinfo TEXT,"+1
		 *	    " verinfo TEXT," +2
		 		" savedate TEXT," +3
		 		" userid TEXT," +4
		 		" userip TEXT," +5
		 		" codename TEXT," +6
		 		" title TEXT," +7
		 		" detail TEXT," +8
				" imgdata BLOB," +9
				" symbol TEXT," +10
				" lcode TEXT," +11
				" apCode TEXT," +12
				" dataTypeName TEXT," +13
				" count TEXT," +14
				" viewCount TEXT," +15
				" valueOfMin TEXT," +16
				" jipyodata TEXT," +17
				" chartItem TEXT," +18
				" analInfo TEXT"+19
		 */
		// TODO Auto-generated method stub
//		int layoutResId = context.getResources().getIdentifier("loadimg", "id", context.getPackageName());
//		ImageView ctlImage = (ImageView)view.findViewById(layoutResId);
//		String fileName = cursor.getString(9);
//		Bitmap myBitmap = BitmapFactory.decodeFile(fileName);
//		ctlImage.setImageBitmap(myBitmap);
//		if(m_nMode==LoadChartController.HTS_CHART)
//		{
//			ctlImage.setVisibility(View.GONE);
//		}
//		else{
//			ctlImage.setVisibility(View.VISIBLE);
//		}
//
//
////
//		layoutResId = context.getResources().getIdentifier("title01", "id", context.getPackageName());
//		TextView ctlTitle = (TextView)view.findViewById(layoutResId);
//		ctlTitle.setText(cursor.getString(6));
////
//		layoutResId = context.getResources().getIdentifier("title02", "id", context.getPackageName());
//		TextView ctlDate = (TextView)view.findViewById(layoutResId);
//		ctlDate.setText(cursor.getString(3));
////
//		layoutResId = context.getResources().getIdentifier("title03", "id", context.getPackageName());
//		TextView ctlPeriod = (TextView)view.findViewById(layoutResId);
////		ctlPeriod.setText(cursor.getString(13));
//		String period = COMUtil.getPeriod(cursor.getString(13), cursor.getString(16));
//		ctlPeriod.setText(period);
//
//		layoutResId = context.getResources().getIdentifier("title04", "id", context.getPackageName());
//		TextView ctlJipyo = (TextView)view.findViewById(layoutResId);
//		ctlJipyo.setText(cursor.getString(8));
////		ctlJipyo.setText(COMUtil.detail);
////
////		layoutResId = context.getResources().getIdentifier("loadtextview", "id", context.getPackageName());
////		TextView ctlCodeName = (TextView)view.findViewById(layoutResId);
////		ctlCodeName.setText(cursor.getString(8));

		//2013. 1. 30  불러오기창에서 스크롤시 컨트롤들이 null 이 나오면서 죽는 현상 해결 (try catch로)
		final View _view = view;

//		final int nPos = _view.getId();
//		final int nPos = cursor.getPosition();

		try {
//			final int nId = view.getId();
			//2013. 1. 28 새 row layout 컨트롤 기능 세팅 
//			int layoutResId = context.getResources().getIdentifier("chartload_tv_name", "id", context.getPackageName());
//			TextView tv_Name = (TextView)view.findViewById(layoutResId);
//			tv_Name.setText(cursor.getString(25));

			TextView tv_Name = wrapper.getCtrlSaveTitle();
			tv_Name.setText(cursor.getString(25));
			tv_Name.setLineSpacing(0,0.7f);

			ImageView ctlImage = wrapper.getCtrlLoadImg();
			String fileName = cursor.getString(9);
			Bitmap myBitmap = BitmapFactory.decodeFile(fileName);
			ctlImage.setImageBitmap(myBitmap);

			wrapper.getCtrlSaveDate().setText(cursor.getString(3));
			wrapper.getCtrlSavePeriod().setText(COMUtil.getPeriod(cursor.getString(13), cursor.getString(16)));
			wrapper.getCtrlSaveIndicator().setText(cursor.getString(8));
			wrapper.getCtrlSaveJongmok().setText(cursor.getString(6) +"("+ cursor.getString(10)+")");

			int layoutResId = context.getResources().getIdentifier("ll_saved_jongmok", "id", context.getPackageName());
			LinearLayout llSavedJongmok = (LinearLayout)_view.findViewById(layoutResId);

			if (cursor.getString(33).equals("true"))
				llSavedJongmok.setVisibility(View.VISIBLE);
			else
				llSavedJongmok.setVisibility(View.GONE);

			//		layoutResId = context.getResources().getIdentifier("chartload_linear_btns", "id", context.getPackageName());
			//		LinearLayout linearBtns = (LinearLayout)view.findViewById(layoutResId);

//			layoutResId = context.getResources().getIdentifier("chartload_btn_accept", "id", context.getPackageName());
//			Button btnAccept = (Button)view.findViewById(layoutResId);
//			Button btnAccept = wrapper.getCtrlAcceptButton();
//            btnAccept.setId(cursor.getPosition());
////			btnAccept.setId(cursor.getPosition());
//			//		btnAccept.setId(nPosition);
//			final Cursor _cursor = cursor;
//			btnAccept.setOnClickListener(new Button.OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
////					Toast.makeText(v.getContext(), "적용버튼", Toast.LENGTH_SHORT).show();
//                    if(null != COMUtil.analPrefEditor)
//                    {
//                        String strTemp = cursor.getString(10);
//                        String[] symbols = strTemp.split("/");
//                        strTemp = cursor.getString(13);
//                        String[] dataTypeNames = strTemp.split("/");
//                        //저장 Key.  종목코드:주기   ex) 000660:2
//                        if(symbols.length>0 && dataTypeNames.length>0) {
//                            String strKey = symbols[0] + ":" + dataTypeNames[0];
//
//                            //현재 종목에 대한 분석툴바 저장정보를 추가한다.
//                            String strAnalInfo = cursor.getString(19);
//                            if (strAnalInfo != null && !strAnalInfo.equals(""))    //그려진 분석툴바가 없을 경우, 해당 정보를 제거
//                            {
//                                COMUtil.analPrefEditor.putString(strKey, strAnalInfo);
//                            }
//                            //추가완료.
//                            COMUtil.analPrefEditor.commit();
//                        }
//                    }
//                    String gijun = cursor.getString(27);
//
//					COMUtil._mainFrame.setLocalStorageState(_cursor, v.getId());
//                    if(gijun != null && gijun.length()>0)
//                    {
//                        String[] gijunLists = gijun.split("/");
//                        if(gijunLists.length>0) {
//                            String strItem = gijunLists[0];
//
//                            if (strItem != null || !strItem.equals("")) {
//                                String[] arrConv = strItem.split("=");
//                                ChartViewModel _cvm = (ChartViewModel) COMUtil._mainFrame.mainBase.baseP._chart._cvm;
//                                if (_cvm.baseLineType != null && _cvm.baseLineType.size() > 0) {
//                                    _cvm.baseLineType.clear();
//                                }
//                                for (String type : arrConv) {
////							System.out.println("gijunChartSetting load : "+type);
//                                    if (!type.equals("")) {
//                                        _cvm.baseLineType.add(Integer.parseInt(type));
//                                    }
//                                }
//                            }
//                        }
//                    }
//
//					if(COMUtil.apiMode) {
//						if(COMUtil.loadchartPopup!=null) {
//							COMUtil.loadchartPopup.dismiss();
//							COMUtil.loadchartPopup=null;
//						}
//					}
//				}
//			});
			//최초 비활성화 
//			btnAccept.setEnabled(false);
//			arBtnAccept.add(btnAccept);

//			layoutResId = context.getResources().getIdentifier("chartload_btn_detail", "id", context.getPackageName());
//			Button btnDetail = (Button)view.findViewById(layoutResId);
			Button btnDetail = wrapper.getCtrlDetailButton();
//			btnDetail.setId(cursor.getPosition());
//			btnDetail.setId(nId);
			//		btnDetail.setId(nPosition);

			btnDetail.setTag(cursor);
            btnDetail.setId(cursor.getPosition());

//			String strTitleByDB = cursor.getString(25);
//			String strMemoByDB = cursor.getString(8);
//			
//			final String title = strTitleByDB;
//			final String memo = strMemoByDB;

			final int _nID = wrapper.getRecordID();

			final TextView _tvTitle = wrapper.getCtrlSaveTitle();

            //2020. 05. 19 by hyh - 라디오버튼 에러 수정 >>
//			_tvTitle.setOnClickListener(new View.OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					COMUtil.chartLoadView.enableApplyButton();
//					int pos = v.getId();
//					for(int i=0; i<itemChecked.size(); i++) {
//						itemChecked.set(i, false); //초기화.
//					}
//					for(int i=0; i<arCheckBox.size(); i++) {
//						arCheckBox.get(i).setChecked(false);
//					}
//
//					itemChecked.set(pos, true);
//					arCheckBox.get(pos).setChecked(true);
//
//					nCheckedPos = pos;
//				}
//			});
            //2020. 05. 19 by hyh - 라디오버튼 에러 수정 <<

			btnDetail.setOnClickListener(new Button.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Cursor recvCursor = null;
					String strTitleByDB = null;
					String strMemoByDB = null;
					String strSaveDateByDB = null;
					String strCodeNameByDB = null;

					int pos = v.getId();

					recvCursor = (Cursor)v.getTag();
					recvCursor.moveToPosition(pos);

					strTitleByDB = recvCursor.getString(25);
					strMemoByDB = recvCursor.getString(8);
					strSaveDateByDB = recvCursor.getString(3);
					strCodeNameByDB = recvCursor.getString(6);

					//2013. 7. 31 저장하기창에 종목 상세내용(일시/종목/주기) 추가>>
					String strJipyoList = "";

					//2015. 1. 13 by lyk - 동일지표 추가 (추가리스트 적용)
					Vector<Hashtable<String, String>>  vec = (Vector<Hashtable<String, String>>)COMUtil.getJipyoMenu().clone();
					Vector<Hashtable<String, String>> addItems = COMUtil.getAddJipyoList();
					for(int i=0; i<addItems.size(); i++) {
						vec.add(addItems.get(i));
					}
					//2015. 1. 13 by lyk - 동일지표 추가 (추가리스트 적용) end

					//지표리스트 구하기
					String[] token = recvCursor.getString(17).split("\\~");
					Vector<String> graphs=new Vector<String>();
					String str="";
					for(int i=0; i<token.length; i++) {
						StringTokenizer st = new StringTokenizer(token[i], "#");
						while (st.hasMoreTokens()) {
							str = COMUtil.getDecodeData(st.nextToken());
							if(!str.equals(""))
								graphs.add(str);
						}
					}

					//지표설정항목에서 이름과 값을 분리한다.
					int listLen = graphs.size();
					String graphName = "";
					Vector<String> strGraphNameList = new Vector<String>();
					for(int k=0; k<listLen; k++) {
						String loadJipyo = (String)graphs.get(k);
						if(loadJipyo.equals("")) continue;
						int index = loadJipyo.indexOf("{");
						if(index<1) {
							graphName = loadJipyo;
						} else {
							graphName = loadJipyo.substring(0, index);
						}
						//일본식봉이 아닌 지표 이름만 뽑아내기 위해서
						if(!graphName.equals("일본식봉") && !graphName.equals("Heikin-Ashi"))
							strGraphNameList.add(graphName);
					}

					//위에서 뽑아낸 이름으로 지표리스트 String 을 만든다.
					int jCnt = vec.size();
					int inx = 0;
					for(int i=inx; i<jCnt; i++) {
						Hashtable<String, String> item = (Hashtable<String, String>)vec.get(i);
						listLen = strGraphNameList.size();
						for(int k=0; k<listLen; k++) {
							String cmp = (String)item.get("name");
							if(cmp.equals(strGraphNameList.get(k))) {
								strJipyoList += cmp;
								strJipyoList += ", ";
								break;
							}
						}
					}

					//2014. 3. 21 차트 저장할 때 지표가 아무것도 없으면 죽는현상>>
					if(strJipyoList.length() > 0)
					{
						strJipyoList = strJipyoList.substring(0, strJipyoList.length()-2);
					}
					//2014. 3. 21 차트 저장할 때 지표가 아무것도 없으면 죽는현상<<

					//주기
					String strPeriod = null;
					String ptype = recvCursor.getString(13);
					String unitType = recvCursor.getString(16);
					strPeriod = COMUtil.getPeriod(ptype, unitType);
					//2020.05.19 by JJH >> 차트 불러오기 - 상세 설정 팝업 UI 수정 start
					String _strID = recvCursor.getString(0);

//					COMUtil.modifySavedChart(_nID, strTitleByDB, strMemoByDB, _tvTitle, strSaveDateByDB, strCodeNameByDB, strPeriod, strJipyoList);
					COMUtil.modifySavedChart(_strID, strTitleByDB, strMemoByDB, _tvTitle, strSaveDateByDB, strCodeNameByDB, strPeriod, strJipyoList);
					//2020.05.19 by JJH >> 차트 불러오기 - 상세 설정 팝업 UI 수정 end
					COMUtil.loadchartPopup.dismiss();
					COMUtil.loadchartPopup = null;
				}
			});

			Button btnDel = wrapper.getCtrlDelButton();
            btnDel.setId(cursor.getPosition());
			final Cursor _cursor = cursor;
			btnDel.setTag(cursor);

			btnDel.setOnClickListener(new Button.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					int pos = v.getId();
					Cursor recvCursor = (Cursor)v.getTag();
					COMUtil.chartLoadView.deleteItemList(recvCursor, pos);
				}
			});


			//최초 비활성화 
//			btnDetail.setEnabled(false);
//			arBtnDetail.add(btnDetail);

			//CheckBox id에 uid값 설정.
//		try {
//			layoutResId = context.getResources().getIdentifier("checkBox", "id", context.getPackageName());
//			ctlCheckBox = (CheckBox)view.findViewById(layoutResId);

            //2020. 05. 19 by hyh - 라디오버튼 에러 수정 >>
            final CheckBox ctlCheckBox = wrapper.getCtrlCheck();
            ctlCheckBox.setId(cursor.getPosition());
            //2020. 05. 19 by hyh - 라디오버튼 에러 수정 <<

			int pos = cursor.getPosition();
			String uid = cursor.getString(0);
//			Log.d("uid", pos + "=>" + uid);
//			ctlCheckBox.setId(cursor.getPosition());
//			ctlCheckBox.setId(nPos);
			ctlCheckBox.setTag(cursor.getString(0));
			ctlCheckBox.setChecked(itemChecked.get(cursor.getPosition()));

			//2012. 10. 2  체크버튼 이미지 32*32 사용하여 세팅  : I106
//				stateDrawable = imageChange(normal, press);
//			ctlCheckBox.setButtonDrawable(stateDrawable);

			//2013. 1. 28 체크박스->라디오버튼처럼 동작하게 하기위해 리스트 관리 
//			arCheckBox.add(ctlCheckBox);

			ctlCheckBox.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v)
				{
					COMUtil.chartLoadView.enableApplyButton();
					CheckBox cb = (CheckBox)v;

                    //2020. 05. 19 by hyh - 라디오버튼 에러 수정 >>
					cb.setChecked(true);
					//boolean isChecked = cb.isChecked();
                    //2020. 05. 19 by hyh - 라디오버튼 에러 수정 <<

					int pos = cb.getId();

					//다중선택 처리를 위해 초기화 막음.
					for(int i=0; i<itemChecked.size(); i++) {
						itemChecked.set(i, false); //초기화.
					}

					//2020. 05. 19 by hyh - 라디오버튼 에러 수정 >>
					itemChecked.set(pos, true);
					notifyDataSetChanged();

//					for(int i=0; i<arCheckBox.size(); i++) {
//						arCheckBox.get(i).setChecked(false);
//					}
//
//
////        		for(int i=0; i<arBtnAccept.size(); i++) {
////        			arBtnAccept.get(i).setEnabled(false);
////        		}
////        		for(int i=0; i<arBtnDetail.size(); i++) {
////        			arBtnDetail.get(i).setEnabled(false);
////        		}
//
//					int nItemCheckedPos = itemChecked.size() - pos - 1;
//
//					if (isChecked) {
//						itemChecked.set(pos, true);
//						arCheckBox.get(pos).setChecked(true);
////                    arBtnAccept.get(pos).setEnabled(true);
////    				arBtnDetail.get(pos).setEnabled(true);
//
//					} else if (!isChecked) {
//						//이전에 선택햇던 곳과 다른 체크박스라면
//						if(pos != nCheckedPos)
//						{
//							itemChecked.set(pos, false);
//							arCheckBox.get(pos).setChecked(false);
////                    	arBtnAccept.get(pos).setEnabled(false);
////        				arBtnDetail.get(pos).setEnabled(false);
//						}
//						//동일한 위치라면
//						else
//						{
//							itemChecked.set(pos, true);
//							arCheckBox.get(pos).setChecked(true);
////                    	arBtnAccept.get(pos).setEnabled(true);
////        				arBtnDetail.get(pos).setEnabled(true);
//						}
//						// do some operations here
//					}
//
//					//2013. 1. 28 체크박스의 이전 인덱스 저장. 라디오버튼처럼 동작을 위해서 이전 인덱스 저장후 하나라도 체크되어있게 함
//					nCheckedPos = pos;
                    //2020. 05. 19 by hyh - 라디오버튼 에러 수정 <<
				}});

            //2020. 05. 19 by hyh - 라디오버튼 에러 수정 >>
//            _view.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View view) {
//					ctlCheckBox.performClick();
//				}
//			});
            //2020. 05. 19 by hyh - 라디오버튼 에러 수정 <<

        	_view.setId(_cursor.getPosition());
    		view.setId(nPosition);
            _view.setOnClickListener(new View.OnClickListener() {
    				//2013. 1. 30 체크되있으면  리스트 행을 터치시  적용버튼을 누른 것과 같은 동작
    				@Override
    				public void onClick(View v) {
    					// TODO Auto-generated method stub
//    					if(arCheckBox.get(v.getId()).isChecked())
//    					{
    						COMUtil._mainFrame.setLocalStorageState(_cursor, v.getId());
    						if(COMUtil.apiMode) {
    							if(COMUtil.loadchartPopup!=null) {
    								COMUtil.loadchartPopup.dismiss();
    								COMUtil.loadchartPopup=null;
    							}
    						}
//    					}
    				}
    			});

		}
		catch(Exception e) {
//			System.out.println(e.getMessage());
		}

//		view.setId(cursor.getPosition());
////		view.setId(nPosition);
//		 view.setOnClickListener(new View.OnClickListener() {
//				//2013. 1. 30 체크되있으면  리스트 행을 터치시  적용버튼을 누른 것과 같은 동작 
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					if(arCheckBox.get(v.getId()).isChecked())
//					{
//						COMUtil._mainFrame.setLocalStorageState(_cursor, v.getId());
//						if(COMUtil.apiMode) {
//							if(COMUtil.loadchartPopup!=null) {
//								COMUtil.loadchartPopup.dismiss();
//								COMUtil.loadchartPopup=null;
//							}
//						}
//					}
//				}
//			});

//		int position = cursor.getPosition();
//        //item background image 설정.
//        int selId=0;
//        if((position % 2)==0) {
//        	selId=R.drawable.list2;
//        } else {
//        	selId=R.drawable.list1;
//        }
//        view.setBackgroundResource(selId);
	}

	@Override
	public View newView(Context context, final Cursor cursor, ViewGroup parent) {
		View row = null;

		if(row == null) {
			// LayoutInflater의 객체 inflater를 현재 context와 연결된 inflater로 초기화.
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();

			// inflator객체를 이용하여 \res\laout\cellsigview.xml 파싱

			//2012. 8. 21  불러오기창 레이아웃 테블릿인지 아닌지에 따라서 다르게 로드 :  SL_tab10
			int layoutResId;
//            if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
//            {
//            	layoutResId = context.getResources().getIdentifier("chart_load_celltype_tab", "layout", context.getPackageName());
//            }
//            else
//            {
			layoutResId = context.getResources().getIdentifier("chart_load_celltype", "layout", context.getPackageName());
//            }
			row = (View)inflater.inflate(layoutResId, null);

			COMUtil.setGlobalFont((ViewGroup)row);

			wrapper = new ViewWrapper2(row, itemChecked, arCheckBox, arBtnDel, arBtnDetail, nCheckedPos);
//            layoutResId = context.getResources().getIdentifier("chartload_tv_name", "id", context.getPackageName());
			TextView tv_Name = wrapper.getCtrlSaveTitle();
			tv_Name.setId(cursor.getPosition());

			//// 2016. 6. 17 삭제버튼 >>
			Button btnDel = wrapper.getCtrlDelButton();
			btnDel.setId(cursor.getPosition());
			arBtnDel.add(btnDel);
			// 2016. 6. 17 삭제버튼 <<
//    		
//    		layoutResId = context.getResources().getIdentifier("chartload_btn_accept", "id", context.getPackageName());
//			Button btnAccept = wrapper.getCtrlAcceptButton();
//			//btnAccept.setId(cursor.getPosition());
//			arBtnAccept.add(btnAccept);

//    		layoutResId = context.getResources().getIdentifier("chartload_btn_detail", "id", context.getPackageName());
			Button btnDetail = wrapper.getCtrlDetailButton();
			btnDetail.setId(cursor.getPosition());
			arBtnDetail.add(btnDetail);
			//
//    		layoutResId = context.getResources().getIdentifier("checkBox", "id", context.getPackageName());
			CheckBox chkBox = wrapper.getCtrlCheck();
			chkBox.setId(cursor.getPosition());
			arCheckBox.add(chkBox);

			wrapper.setRecordID(Integer.parseInt(cursor.getString(0)));

			row.setId(cursor.getPosition());

			row.setTag(wrapper);

//            if(COMUtil.isHighResolutionForNexus1())
//            	row.setMinimumHeight(60);

			//2013. 7. 25 불러오기창 리스트  >>
//            row.setBackgroundColor(Color.TRANSPARENT);
			row.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, (int)COMUtil.getPixel(160)));	//2015. 1. 13 불러오기창 리스트 생성시 죽는현상
			//2013. 7. 25 불러오기창 리스트 배경 투명처리 >>
		}

		return row;
	}

	public void setMode(int nMode){
		m_nMode = nMode;
	}

	//2012. 10. 2  체크박스 이미지 변경  : I106
	public static StateListDrawable imageChange(Drawable normal, Drawable checked) {
		StateListDrawable imageDraw = new StateListDrawable();
		imageDraw.addState(new int[] { android.R.attr.state_checked }, checked);
		imageDraw.addState(new int[] { -android.R.attr.state_checkable}, normal);
		return imageDraw;
	}

    //2020. 05. 19 by hyh - 라디오버튼 에러 수정 >>
//	public CheckBox getCtrlVisible() {
//		return ctlCheckBox;
//	}
    //2020. 05. 19 by hyh - 라디오버튼 에러 수정 <<
}
class ViewWrapper2 {
	private View base;
		 private ImageView ctlImage;
	 private TextView  ctlCodeName, ctlTitle01, ctlTitle02, ctlTitle03, ctlTitle04;
	private TextView ctlSaveTitle = null;
	private Button btnAccept = null, btnDetail = null, btnDel = null;
	private CheckBox chkBtn = null;
	private ImageView ivLoadImg;
	private TextView tvDate, tvIndicator, tvPeriod, tvJongmok;

	private int nID  = -1;

	public ArrayList<Boolean> itemChecked = new ArrayList<Boolean>();
	public ArrayList<CheckBox> arCheckBox = new ArrayList<CheckBox>();
	//2013. 1. 29 적용,상세 버튼 어레이. -> 활성화/비활성화 하기 위함 
	public ArrayList<Button> arBtnAccept = new ArrayList<Button>();
	public ArrayList<Button> arBtnDetail = new ArrayList<Button>();

	int nCheckedPos;
	int nChkBoxSize;

	//2012. 10. 2  체크박스 이미지 변경  : I106
//	 Drawable press, normal;
//	 StateListDrawable stateDrawable;

	ViewWrapper2(View base, ArrayList<Boolean> _itemChecked, ArrayList<CheckBox> _arCheckBox, ArrayList<Button> _arBtnDel, ArrayList<Button> _arBtnDetail,
				 int _nCheckedPos) {
		this.base = base;

		itemChecked = _itemChecked;
		arCheckBox = _arCheckBox;
//		arBtnAccept = _arBtnAccept;
		arBtnDetail = _arBtnDetail;
		nCheckedPos = _nCheckedPos;

		if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
		{
			nChkBoxSize = (int)(COMUtil.getPixel(45) / 2);
		}
		else
		{
			nChkBoxSize = (int)COMUtil.getPixel(45);
		}

//		Display dis = ((WindowManager)base.getContext().getSystemService(base.getContext().WINDOW_SERVICE)).getDefaultDisplay();  
//		int mDisWidth = dis.getWidth();            // 가로 사이즈 
//		int mDisHeight = dis.getHeight();          // 세로 사이즈
//		if(mDisWidth == 800 && mDisHeight == 1232)
//		{
//			nChkBoxSize = (int)(COMUtil.getPixel(45) / 2);
//		}
//		
//		int layoutResId = base.getContext().getResources().getIdentifier("checkbox_off_background2", "drawable", base.getContext().getPackageName());
//		Bitmap image = BitmapFactory.decodeResource(base.getContext().getResources(), layoutResId);	
//		Bitmap resizeImage = Bitmap.createScaledBitmap(image, nChkBoxSize, nChkBoxSize,true);
//    	normal = (Drawable)(new BitmapDrawable(resizeImage));
//    	
//		layoutResId = base.getContext().getResources().getIdentifier("checkbox_on_background2", "drawable", base.getContext().getPackageName());
//		image = BitmapFactory.decodeResource(base.getContext().getResources(), layoutResId);	
//		resizeImage = Bitmap.createScaledBitmap(image, nChkBoxSize, nChkBoxSize,true);
//    	press = (Drawable)(new BitmapDrawable(resizeImage));
//    	
//		stateDrawable = new StateListDrawable();
	}

	// 멤버 변수가 null일때만 findViewById를 호출
	// null이 아니면 저장된 instance 리턴 -> Overhaed 줄임

	//2013. 1. 28  불러오기  새 row layout 적용
	TextView getCtrlSaveTitle() {
		if(ctlSaveTitle == null) {
			int layoutResId = base.getContext().getResources().getIdentifier("chartload_tv_name", "id", base.getContext().getPackageName());
			ctlSaveTitle = (TextView)base.findViewById(layoutResId);
		}
		return ctlSaveTitle;
	}

//	Button getCtrlAcceptButton() {
//		if(btnAccept == null) {
//			int layoutResId = base.getContext().getResources().getIdentifier("chartload_btn_accept", "id", base.getContext().getPackageName());
//			btnAccept = (Button)base.findViewById(layoutResId);
//		}
//		return btnAccept;
//	}

	Button getCtrlDetailButton() {
		if(btnDetail == null) {
			int layoutResId = base.getContext().getResources().getIdentifier("chartload_btn_detail", "id", base.getContext().getPackageName());
			btnDetail = (Button)base.findViewById(layoutResId);
		}
		return btnDetail;
	}

	Button getCtrlDelButton() {
		if(btnDel == null) {
			int layoutResId = base.getContext().getResources().getIdentifier("btn_edit", "id", base.getContext().getPackageName());
			btnDel = (Button)base.findViewById(layoutResId);
		}
		return btnDel;
	}

	CheckBox getCtrlCheck() {
		if(chkBtn == null) {
			int layoutResId = base.getContext().getResources().getIdentifier("checkBox", "id", base.getContext().getPackageName());
//	    	 stateDrawable = imageChange(normal, press);
			chkBtn = (CheckBox)base.findViewById(layoutResId);
//	    	 chkBtn.setButtonDrawable(stateDrawable);

		}
		return chkBtn;
	}
	ImageView getCtrlLoadImg() {
		if(ivLoadImg == null) {
			int layoutResId = base.getContext().getResources().getIdentifier("loadimg", "id", base.getContext().getPackageName());
			ivLoadImg = (ImageView)base.findViewById(layoutResId);
		}
		return ivLoadImg;
	}
	TextView getCtrlSaveDate() {
		if(tvDate == null) {
			int layoutResId = base.getContext().getResources().getIdentifier("chartload_tv_date", "id", base.getContext().getPackageName());
			tvDate = (TextView)base.findViewById(layoutResId);
		}
		return tvDate;
	}
	TextView getCtrlSavePeriod() {
		if(tvPeriod == null) {
			int layoutResId = base.getContext().getResources().getIdentifier("chartload_tv_period", "id", base.getContext().getPackageName());
			tvPeriod = (TextView)base.findViewById(layoutResId);
		}
		return tvPeriod;
	}
	TextView getCtrlSaveIndicator() {
		if(tvIndicator == null) {
			int layoutResId = base.getContext().getResources().getIdentifier("chartload_tv_indicator", "id", base.getContext().getPackageName());
			tvIndicator = (TextView)base.findViewById(layoutResId);
		}
		return tvIndicator;
	}
	TextView getCtrlSaveJongmok() {
		if(tvJongmok == null) {
			int layoutResId = base.getContext().getResources().getIdentifier("chartload_tv_jongmok", "id", base.getContext().getPackageName());
			tvJongmok = (TextView)base.findViewById(layoutResId);
		}
		return tvJongmok;
	}

	void setRecordID(int id)
	{
		nID = id;
	}

	int getRecordID()
	{
		return nID;
	}

//	 ImageView getCtrlImageView() {
//	     if(ctlImage == null) {
//	    	 int layoutResId = base.getContext().getResources().getIdentifier("loadimg", "id", base.getContext().getPackageName());
//	    	 ctlImage = (ImageView)base.findViewById(layoutResId);
//	     }
//	     return ctlImage;
//	 }
//
//	 TextView getCtrlTextView() {
//	     if(ctlCodeName == null) {
//	    	 int layoutResId = base.getContext().getResources().getIdentifier("loadtextview", "id", base.getContext().getPackageName());
//	    	 ctlCodeName = (TextView)base.findViewById(layoutResId);
//	     }
//	     return ctlCodeName;
//	 }
////
//	 TextView getCtrlTitle01View() {
//	     if(ctlTitle01 == null) {
//	    	 int layoutResId = base.getContext().getResources().getIdentifier("title01", "id", base.getContext().getPackageName());
//	    	 ctlTitle01 = (TextView)base.findViewById(layoutResId);
//	     }
//	     return ctlTitle01;
//	 }
//
//	 TextView getCtrlTitle02View() {
//	     if(ctlTitle02 == null) {
//	    	 int layoutResId = base.getContext().getResources().getIdentifier("title02", "id", base.getContext().getPackageName());
//	    	 ctlTitle02 = (TextView)base.findViewById(layoutResId);
//	     }
//	     return ctlTitle02;
//	 }
////
//	 TextView getCtrlTitle03View() {
//	     if(ctlTitle03 == null) {
//	    	 int layoutResId = base.getContext().getResources().getIdentifier("title03", "id", base.getContext().getPackageName());
//	    	 ctlTitle03 = (TextView)base.findViewById(layoutResId);
//	     }
//	     return ctlTitle03;
//	 }
//
//	TextView getCtrlTitle04View() {
//		if(ctlTitle04 == null) {
//			int layoutResId = base.getContext().getResources().getIdentifier("title04", "id", base.getContext().getPackageName());
//			ctlTitle04 = (TextView)base.findViewById(layoutResId);
//		}
//		return ctlTitle04;
//	}

	//2012. 10. 2  체크박스 이미지 변경  : I106
	public static StateListDrawable imageChange(Drawable normal, Drawable checked) {
		StateListDrawable imageDraw = new StateListDrawable();
		imageDraw.addState(new int[] { android.R.attr.state_checked }, checked);
		imageDraw.addState(new int[] { -android.R.attr.state_checkable}, normal);
		return imageDraw;
	}
}