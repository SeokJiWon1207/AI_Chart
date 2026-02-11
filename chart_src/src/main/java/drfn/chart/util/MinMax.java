package drfn.chart.util;
/**
 * 명칭 : MinMax
 * 설명 : 최대, 최소값을 얻는 계산 클래스입니다
 */
public class MinMax {
	public static long[] getLongMinMax(int[][] data) {
		long[] mm_data = {Long.MAX_VALUE,Long.MIN_VALUE};
		long[] t= {-1,1};
		if((data == null)||(data.length == 0)) return t;

		int arrayLength = data[0].length;

		for(int j=0 ; j<arrayLength ; j++) {
			for(int i=0 ; i<data.length ; i++) {
				mm_data[0]=(mm_data[0]<=data[i][j])?mm_data[0]:data[i][j];
				mm_data[1]=(mm_data[1]<=data[i][j])?mm_data[1]:data[i][j];
			}
		}
		return mm_data;
	}
	/**
	 * int[][] 에서의 최소값을 반환합니다
	 */
	public static int[] getIntMinMax(int[][] data) {
		int[] mm_data = {Integer.MAX_VALUE,Integer.MIN_VALUE};
		int[] t= {-1,1};
		if((data == null)||(data.length == 0)) return t;

		int arrayLength = data[0].length;

		for(int j=0 ; j<arrayLength ; j++) {
			for(int i=0 ; i<data.length ; i++) {
				mm_data[0]=(mm_data[0]<=data[i][j])?mm_data[0]:data[i][j];
				mm_data[1]=(mm_data[1]<=data[i][j])?mm_data[1]:data[i][j];
			}
		}
		return mm_data;
	}
	/**
	 * int[][] 에서의 최소값을 반환합니다
	 */
	public static int getIntMin(int[][] data) {
		int minData = Integer.MAX_VALUE;
		if( (data == null) || (data.length == 0) ) return -1;

		int arrayLength = data[0].length;

		for(int j=0 ; j<arrayLength ; j++) {
			for(int i=0 ; i<data.length ; i++) {
				minData = (minData <= data[i][j]) ? minData : data[i][j];
			}
		}
		return minData;
	}
	public static double getIntMinT(double[][] data) {
		double minData = Double.MAX_VALUE;
		if( (data == null) || (data.length == 0) ) return -1;
		int dataLength = data.length;
		int arrayLength = data[0].length;

		for(int j=0 ; j<arrayLength ; j++) {
			for(int i=0 ; i<dataLength ; i++) {
				if(data[i][j]!=0){
					minData = (minData <= data[i][j]) ? minData : data[i][j];
				}
			}
		}
		if(minData==Double.MAX_VALUE)return 0;
		else return minData;
	}
	/**
	 * int[][]에서의 최대값을 반환합니다
	 */
	public static double getIntMax(double[][] data) {
		//2015. 1. 13 ADLine 지표 추가>>
//		double maxData = Double.MIN_VALUE;
		double maxData = Integer.MIN_VALUE;	//2014.06.05 by LYH >> Double.MIN_VALUE값이 0보다 크게 잡혀 Integer.MIN_VALUE로 변경.
		//2015. 1. 13 ADLine 지표 추가<<
		if( (data == null) || (data.length == 0) ) return -1;
		int dataLength = data.length;
		int arrayLength = data[0].length;

		for(int j=0 ; j<arrayLength ; j++) {
			for(int i=0 ; i<dataLength ; i++) {
				maxData = (maxData >= data[i][j]) ? maxData : data[i][j];
			}
		}
		return maxData;
	}
	/**
	 * int[][]에서 특정 index 컬럼 데이터의  최대값을 반환합니다
	 */
	public static int getIntMax(int[][] data,int index) {
		int maxData = Integer.MIN_VALUE;
		if( (data == null) || (data.length == 0) ) return -1;
		int dataLength = data.length;
		for(int i=0 ; i<dataLength ; i++) {
			maxData = (maxData >= data[i][index]) ? maxData : data[i][index];
		}
		return maxData;
	}
	/**
	 * int[][] 에서 특정 index 컬럼 데이터의 최소값을 반환합니다
	 */
	public static int getIntMin(int[][] data,int index) {
		int minData = Integer.MAX_VALUE;
		if( (data == null) || (data.length == 0) ) return -1;

		for(int i=0 ; i<data.length ; i++) {
			minData = (minData <= data[i][index]) ? minData : data[i][index];
		}
		return minData;
	}
	public static double getIntMaxT(double[][] data) {
		//2015. 1. 13 ADLine 지표 추가>>
//		double maxData = Double.MIN_VALUE;
		double maxData = Integer.MIN_VALUE;	//2014.06.05 by LYH >> Double.MIN_VALUE값이 0보다 크게 잡혀 Integer.MIN_VALUE로 변경.
		//2015. 1. 13 ADLine 지표 추가<<
		if( (data == null) || (data.length == 0) ) return -1;
		int dataLength = data.length;
		int arrayLength = data[0].length;

		for(int j=0 ; j<arrayLength ; j++) {
			for(int i=0 ; i<dataLength ; i++) {
				if(data[i][j]!=0){
					maxData = (maxData >= data[i][j]) ? maxData : data[i][j];
				}
			}
		}
		return maxData;
	}
	/**
	 * int[]에서 최소값을 반환합니다
	 */
	public static int getIntMin(int[] data){
		int minData = Integer.MAX_VALUE;
		if( (data == null) || (data.length == 0) ) return -1;
		int dataLength = data.length;

		for(int i = 0; i<dataLength; i++){
			if(data[i]!=0){
				minData = (minData <= data[i])? minData : data[i];
			}
		}
		return minData;
	}
	public static double getIntMinT(double[] data){
		double minData = Double.MAX_VALUE;
		if( (data == null) || (data.length == 0) ) return -1;
		int dataLength = data.length;

		for(int i = 0; i<dataLength; i++){
			if(data[i]!=0){
				minData = (minData <= data[i])? minData : data[i];
			}
		}
		if(minData==Double.MAX_VALUE)return 0;
		else return minData;
	}
	/**
	 * int[] 에서 최대값을 반환합니다
	 */
	public static int getIntMax(int[] data){
		int maxData =Integer.MIN_VALUE;
		if( (data == null) || (data.length == 0) ) return -1;
		int dataLength = data.length;

		for(int i = 0; i<dataLength; i++){
			maxData = (maxData >= data[i]) ? maxData : data[i];
		}
		return maxData;
	}
	public static double getIntMaxT(double[] data){
		//2015. 1. 13 ADLine 지표 추가>>
//		double maxData =Double.MIN_VALUE;
		double maxData = Integer.MIN_VALUE;	//2014.06.05 by LYH >> Double.MIN_VALUE값이 0보다 크게 잡혀 Integer.MIN_VALUE로 변경.
		//2015. 1. 13 ADLine 지표 추가<<
		if( (data == null) || (data.length == 0) ) return -1;
		int dataLength = data.length;

		for(int i = 0; i<dataLength; i++){
			if(data[i]!=0){
				maxData = (maxData >= data[i]) ? maxData : data[i];
			}
		}
		return maxData;
	}
	/**
	 * int[] data에서 index위치에서부터 해당 데이터 num의 범위에서의 최소값을 반환합니다
	 */
	public static double getRangeMin(double[] data, int index, int num) {
		if(data == null) return -1;
		double minData = Double.MAX_VALUE;
		int dataCnt = data.length;
		if(index>dataCnt) index = dataCnt;
		int limit =(index>num)?index-num:0;
		//2011.08.31 by LYH >> Min값 계산에서 초기 0값 제외 - 개선 필요
		for(int i=limit; i<index ; i++) {
			if(data[i] != 0)
			{
				limit=i;
				break;
			}
		}
		//2011.08.31 by LYH <<
		for(int i=limit; i<index ; i++) {
			minData = (minData < data[i])? minData : data[i];
		}
		return minData;
	}

    public static double getRangeMinCompare(double[] data, int index, int num) {
        if(data == null) return -1;
        double minData = Double.MAX_VALUE;
        int dataCnt = data.length;
        if(index>dataCnt) index = dataCnt;
        int limit =(index>num)?index-num:0;
        //2011.08.31 by LYH >> Min값 계산에서 초기 0값 제외 - 개선 필요
        for(int i=limit; i<index ; i++) {
            limit=i;
            break;
        }
        //2011.08.31 by LYH <<
        for(int i=limit; i<index ; i++) {
            minData = (minData < data[i])? minData : data[i];
        }
        return minData;
    }
	/**
	 *
	 */
	public static int getRangeMax(int[][] data, int index, int num,int colindex) {
		int maxData = Integer.MIN_VALUE;
		int dataCnt = data.length;
		if(index>dataCnt) index = dataCnt;
		int limit = index-num;
		for(int i=limit; i<index ; i++) {
			maxData = (maxData > data[i][colindex])?maxData:data[i][colindex];
		}
		return maxData;
	}
	/**
	 * int[] data에서 index위치에서부터 해당 데이터 num의 범위에서의 최소값을 반환합니다
	 */
	public static double getRangeMin(double[][] data, int index, int num, int colindex) {
		double minData = Double.MAX_VALUE;
		int limit = (index > num) ? index-num : 0;
		for(int i=limit;i<index;i++) {
			minData = (minData <data[i][colindex])? minData : data[i][colindex];
		}
		return minData;
	}
	/**
	 *
	 */
	public static double getRangeMax(double[] data, int index, int num) {
		if(data == null) return -1;
		//2015. 1. 13 ADLine 지표 추가>>
//		double maxData = Double.MIN_VALUE;
		double maxData = Integer.MIN_VALUE;	//2014.06.05 by LYH >> Double.MIN_VALUE값이 0보다 크게 잡혀 Integer.MIN_VALUE로 변경.
		//2015. 1. 13 ADLine 지표 추가<<
		int dataCnt = data.length;
		if(index>dataCnt) index = dataCnt;
		int limit = index-num;
		if(limit<0)
			limit=0;
		if( (data == null) || (data.length == 0) ) return -1;

		for(int i=limit; i<index ; i++) {
			maxData = (maxData >= data[i])?maxData:data[i];
		}
		return maxData;
	}
	public static int getRangeMin(int[][] data, int index, int num) {
		int minData = Integer.MAX_VALUE;
		int limit;
		//if(num>data.length-1)num = data.length-1;
		if( (data == null) || (data.length == 0) ) return minData;

		int dLen = data.length;
		if(index>data.length)index = dLen;
		limit=(index>num)?index-num:0;

		if( (data == null) || (dLen == 0) ) return -1;
		int arrayLength = data[0].length;

		for(int j=0 ; j<arrayLength ; j++) {
			for(int i=limit; i<index ; i++) {
				minData = (minData <= data[i][j]) ? minData : data[i][j];
			}
		}

		return minData;
	}

	public static int getRangeMax(int[][] data, int index, int num) {
		int maxData = Integer.MIN_VALUE;
		int limit;
		if( (data == null) || (data.length == 0) ) return maxData;

		//if(num>data.length)num = data.length-1;
		if(index>data.length)index = data.length;
		limit=(index>=num)?index-num:0;
		//int limit = index-num;

		int arrayLength = data[0].length;

		for(int j=0 ; j<arrayLength ; j++) {
			for(int i=limit ; i<index ; i++) {
				if(i<0)i=0;
				maxData = (maxData >= data[i][j])?maxData:data[i][j];
			}
		}

		return maxData;
	}
	//=================================
	// 데이터에 0값이 있는 경우를 제외한다
	// 지표의 최소,최대값을 구하는데 사용하는 것이 아니라 low데이터의 최소/최대값을 구할때 사용
	//=================================
	public static int getRangeMinT(int[][] data, int index, int num) {
		int minData = Integer.MAX_VALUE;
		int limit;
		if( (data == null) || (data.length == 0) )
			return minData;
		//if(num>data.length-1)num = data.length-1;
		if(index>data.length)index = data.length;
		limit=(index>num)?index-num:0;

		int arrayLength = data[0].length;

		for(int j=0 ; j<arrayLength ; j++) {
			for(int i=limit; i<index ; i++) {
				if(data[i][j]>0){
					minData = (minData <= data[i][j]) ? minData : data[i][j];
				}
			}
		}

		return minData;
	}

	public static int getRangeMaxT(int[][] data, int index, int num) {
		int maxData = Integer.MIN_VALUE;
		int limit;
		if( (data == null) || (data.length == 0) )
			return maxData;
		//if(num>data.length)num = data.length-1;
		if(index>data.length)index = data.length;
		limit=(index>=num)?index-num:0;
		//int limit = index-num;

		int arrayLength = data[0].length;

		for(int j=0 ; j<arrayLength ; j++) {
			for(int i=limit ; i<index ; i++) {
				if(i<0)i=0;
				if(data[i][j]>0){
					maxData = (maxData >= data[i][j])?maxData:data[i][j];
				}
			}
		}
		return maxData;
	}
	public static double[] getMinMax(double[] data){
		//2015. 1. 13 ADLine 지표 추가>>
//	    double max = Double.MIN_VALUE;
		double max = Integer.MIN_VALUE;	//2014.06.05 by LYH >> Double.MIN_VALUE값이 0보다 크게 잡혀 Integer.MIN_VALUE로 변경.
		//2015. 1. 13 ADLine 지표 추가<<
		double min = Double.MAX_VALUE;
		double[] mm_data = new double[2];
		for(int i=0;i<data.length;i++){
			if(data[i]>max)max = data[i];
			if(data[i]<min)min = data[i];
		}
		mm_data[0] = min;
		mm_data[1] = max;
		return mm_data;
	}
	public static double getMAX(double a, double b){
		return(a>=b)?a:b;
	}
	public static double getMIN(double a, double b){
		return(a<=b)?a:b;
	}
	public static double getMAX(double a, double b, double c){
		double max1 = getMAX(a,b);
		return (max1>c)?max1:c;
	}
	public static double getMIN(int a, int b, int c){
		double min1 = getMIN(a,b);
		return(min1<c)?min1:c;
	}

	//2015.01.08 by LYH >> 3일차트 추가
	public static double getRangeMin_NoZero(double[] data, int index, int num) {
		if(data == null) return -1;
		double minData = Double.MAX_VALUE;
		int dataCnt = data.length;
		if(index>dataCnt) index = dataCnt;
		int limit =(index>num)?index-num:0;

		for(int i=limit; i<index ; i++) {
			if(i<0) i=0;
			if(data[i] != 0)
			{
				minData = (minData < data[i])? minData : data[i];
			}
		}
		return minData;
	}
	//2015.01.08 by LYH << 3일차트 추가

	//2015. 2. 9 ADX, DMI 지표 계산오류 수정>>
	public static double getDoubleMaxT(double[] data)
	{
		double maxData = Double.MIN_VALUE;
		if(data == null || data.length == 0)
		{
			return -1;
		}
		int dataLength = data.length;

		for(int i = 0; i < dataLength; i++)
		{
			if(data[i]!=0)
			{
				maxData = (maxData >= data[i]) ? maxData : data[i];
			}
		}

		return maxData;
	}
	//2015. 2. 9 ADX, DMI 지표 계산오류 수정<<
}