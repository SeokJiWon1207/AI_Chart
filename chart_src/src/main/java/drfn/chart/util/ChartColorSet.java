package drfn.chart.util;

import android.graphics.Color;

public class ChartColorSet {
    private static ChartColorSet mInstance;

    public static ChartColorSet getInstance() {
        if(mInstance == null) {
            mInstance = new ChartColorSet();
        }

        return mInstance;
    }

    public int[][] chartColorset = {
            {255, 60, 60},     //0: 상승
            {0, 141, 255},    //1: 하락
            {135, 146, 156},   //2: 보합
            {207, 214, 220},   //3: 거래량
            {255, 113, 67},    //4: 이평1 기간
            {138, 118, 255},   //5: 이평2 기간
            {28, 213, 255},    //6: 이평3 기간
            {68, 75, 82},   //7: 이평4 기간
            {178, 186, 194},    //8: 이평5 기간
            {252, 135, 5},     //9: 이평6 기간
            {152, 116, 232},   //10: 이평7 기간
            {3, 225, 193},    //11: 보조지표 라인 컬러
            {10, 121, 235},     //12: 보조지표 라인 컬러
            {19, 72, 186},    //13: 보조지표 라인 컬러
            {108, 72, 255},    //14: 보조지표 라인 컬러
            {175, 82, 222},    //15: 보조지표 라인 컬러
            {96, 106, 116},   //16: 보조지표 라인 컬러
            {178, 186, 194},    //17: 보조지표 라인 컬러 //(이전값 미래가치 기대)
            {78, 196, 32},     //18: 미래가치 부진
            {58, 170, 224},    //19: 미래가치 비관
            {255, 124, 199},   //20: 미래가치
            {254, 189, 0},   //21: 매물대 막대 // 알파값 0.1
            {255, 113, 67},    //22: 매물대 텍스트
    };

    public void setChartColors(int[][] values) {
        this.chartColorset = values;
    }

    public int[][] getChartColors() {
        return chartColorset;
    }

}