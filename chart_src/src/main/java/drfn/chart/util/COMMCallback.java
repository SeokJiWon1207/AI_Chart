package drfn.chart.util;

public class COMMCallback {
	public void setTRData(String sTRNo, byte[] pTRData, int nTRLength) {
		//이 루틴이 불리는게 아니라 overlide한 곳에서 불림.
	}


	/**
	 setRealData
	 @brief 실시간 데이터가 들어오면 called function.
	 @param sKey : RealKey
	 pTRData : TR의 Data
	 nTRLength :
	 @return void
	 */
	public void setRealData(String sKey, byte[] pTRData, int nTRLength) {
		//이 루틴이 불리는게 아니라 overlide한 곳에서 불림.
	}
}
//interface COMMCallback {
//	public void setTRData();
//	public void setRealData();
//}

//class TRBaseActivity extends Activity{
//    public void SetData() {    	
//    }
//}
//
//class TRBaseListActivity extends ListActivity{
//	public void SetData() {    	
//    }
//}