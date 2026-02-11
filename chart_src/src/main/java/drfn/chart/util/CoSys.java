package drfn.chart.util;

import android.graphics.Color;

public class CoSys {
    //new 20010504 LYH
    public static int colorType = 1;

    // Colors for Minilist
    public static int LIST_HD = Color.rgb(133, 157, 196);    // Header
    public static int LIST_HFG = Color.WHITE;
    public static int LIST_FG = Color.BLACK;
    public static int[] BLACK = {0, 0, 0};
    public static int[] GRAY = {192, 192, 192};
    public static int[] RED = {255, 0, 0};
    public static int[] BLUE = {0, 0, 255};
    public static int[] GREEN = {0, 255, 0};
    public static int[] DKGRAY = {128, 128, 128};
    public static int[] BLGRAY = {170, 170, 170};
    public static int[] WHITE = {255, 255, 255};
    public static int[] COLOR_WHITE = {255, 255, 255};
    //public static int[] WHITE_TEXT = {216,216,216};
//    public static int[] WHITE_TEXT = {170, 170, 170};
//    public static int[] WHITE_TEXT = {153, 153, 153};
//    //public static int[] BLACK_TEXT = {92, 102, 117};
//    public static int[] BLACK_TEXT = {171, 171, 171};
    public static int[] WHITE_TEXT = {252, 252, 252};
    public static int[] BLACK_TEXT = {6, 11, 17};
    public static int[] YELLOW = {255, 255, 0};
    public static int[] VIEWTITLE_BG = {228, 228, 228};
    public static int REAL_BG = Color.rgb(220, 224, 234);    // real time update
    public static int LIST_BD = Color.rgb(160, 160, 160);    // MiniList Boarder
    public static int LIST_LN = Color.rgb(195, 195, 195);    // MiniList Inner Line   -eun
    public static int[] HEADER_LN_COLOR = {170,170,170};    // MiniList Inner Line   -eun
    public static int[] LIST_LN_COLOR = {223,223,223};    // MiniList Inner Line   -eun
    //    public static int[] rectLineColor = {136,136,136};    // block line color
    //public static int[] rectLineColor = {90, 90, 90};    // block line color
//    public static int[] rectLineColor = {43, 60, 81};    // block line color
    //public static int[] rectLineColorWhiteSkin = {216, 216, 216};    // block line color(white skin)
    public static int[] rectLineColor = {187, 187, 187};
    public static int[] rectLineColorWhiteSkin = {187, 187, 187};
    public static int[] XScaleLineColor = {230, 230, 230};
    public static int[] dotLineColor = {207, 214, 220};
    //    public static int[] scrollLineColor = {172,172,172};    // scrollbar color
//    public static int[] vertLineColor = {70,70,70};    // vertLine color
    public static int[] scrollLineColor = {227, 232, 236};    // scrollbar color
    public static int[] scrollBackLineColor = {238, 238, 238};    // scrollbar color
    public static int[] scrollBackLineColor_black = {40, 57, 76};    // scrollbar color
    public static int[] vertLineColor = {238, 238, 238};    // vertLine color // 이전수정값(21.05.21)
    public static int[] vertLineColor_xscale = {178, 186, 194};    // vertLine color
    public static int[] vertLineColor_black = {79, 79, 79};
    public static int[] at_col = {39, 147, 13};    // AnalTool color
    public static int[] alarm_price_bar_col = {33, 156, 3};    // Alarm Price Bar Color
//    public static int[] crossline_col = {51, 51, 51};    // AnalTool color
//    public static int[] crossline_col = {230, 100, 39};    // AnalTool color // 이전색상(21.05.21)
    public static int[] crossline_col = {46, 51, 56};
    public static int[] crossline_text_col = {255, 255, 255};
    public static int[] crossline_colDarkSkin = {236, 239, 241}; // Darkmode 전용컬러 kakaopay(21.05.27)
    public static int[] xscaleTextColor = {61, 111, 165};
    public static int[] baseMarketColor = {247, 87, 53};
    public static int[] portfolioLineColor = {240, 103, 49};
    public static int[] cLineColor = {153, 153, 153};

    //2021.05.21 by hanjun.Kim - kakaopay 매물대 색상추가
    public static int[] stand_vol_color = {254, 189, 0};
    public static int[] stand_vol_text_color = {255, 113, 67};

    //2021.05.21 by hanjun.Kim - kakaopay 매수매도 점선색상
    public static int[] trade_buy_dot_color = {255, 60, 60};
    public static int[] trade_sell_dot_color = {0, 141, 255};

    //2013.08.13 by LYH >> 기준선 차트 색상 추가.
    //public static int[] STANDARD = {192,182,58};
    public static int[] STANDARD = {182, 182, 182};
    public static int[] STANDARD_VOL = {180, 180, 180};
    //2013.08.13 by LYH <<
    public static int[] indicatorBaseLineColor = {120, 120, 120};
    public static int[] TEXT_GREY0 = {17, 17,17};

    public static int STEXT_GREY0 = Color.rgb(17, 17, 17);
    public static int STEXT_GREY2 = Color.rgb(46, 48, 51);
    public static int STEXT_GREY4 = Color.rgb(92, 96, 102);
    public static int STEXT_GREY6 = Color.rgb(138, 144, 153);
    public static int STEXT_GREY8 = Color.rgb(192, 197, 204);
    public static int STEXT_UP = Color.rgb(212, 56, 40);
    public static int STEXT_DOWN = Color.rgb(31, 78, 167);
    public static int TITLE_COLOR1 = Color.rgb(17, 17, 17);

    public static int VIEWPANEL_LINE_COLOR = Color.argb(51, 135, 146, 156);
    public static int VIEWPANEL_TEXT_COLOR = Color.BLACK;
    public static int VIEWPANEL_JIPYO_COLOR = Color.argb(142, 6, 11, 17); //2023.06.01 by SJW - 인포윈도우 폰트 컬러 변경 요청(다크테마 적용)
    public static int VIEWPANEL_JIPYO_COLOR_NIGHT = Color.argb(122, 252, 252, 252); //2023.06.01 by SJW - 인포윈도우 폰트 컬러 변경 요청(다크테마 적용)
    public static int[] GREY600 = {6, 11, 17};
    public static int[] GREY700 = {6, 11, 17};
    public static int[] GREY990 = {6, 11, 17};
    public static int[] GREY990_dark = {252, 252, 252};
    public static int GREY990_BLACK = Color.rgb(6, 11, 17);
    public static int GREY990_BLACK_DARK = Color.rgb(252, 252, 252);
    public static int YELLOW100 = Color.rgb(255, 249, 191);
    public static int RED_DARK = Color.rgb(255, 106, 106);
    public static int YELLOW100_DARK = Color.rgb(54, 54, 20);
    public static int YELLOW500_Base = Color.rgb(255, 235, 0);
    public static int YELLOW500_Base_Dark = Color.rgb(255, 239, 61);
    public static int[][] GREY_DARK = new int[][]{
            {66, 70, 74}, {118, 124, 130}, {188, 192, 196}, {238, 239, 240}
    };
    public static int[] GREY990_N_DARK = {6, 11, 17}; // 다크모드로 색상이 안바뀌는 grey990
    public static int[] GREY0_WHITE = {255, 255, 255};
    //2023.06.02 by SJW - 다크테마 차트 색상 변경 >>
    public static int[] CHART_COLOR_UP_NIGHT = {255, 106, 106};
    public static int[] CHART_COLOR_DOWN_NIGHT = {61, 168, 255};
    //2023.06.02 by SJW - 다크테마 차트 색상 변경 <<
    //독립차트
    public static int[] STANDGRAPH_BASE_COLOR = {255, 113, 67};
    public static int[] STANDGRAPH_BASE_COLOR1 = {0, 141, 255};
    public static int[] STANDGRAPH_MOVE_COLOR1 = {255, 113, 67};
    public static int[] STANDGRAPH_MOVE_COLOR2 = {138, 118, 255};
    public static int[] STANDGRAPH_MOVE_COLOR3 = {28, 213, 255};

    public static int[] LIST_FC = {                       // First Colums Background
            Color.rgb(250, 250, 250),
            Color.rgb(239, 239, 239),
            //Color.rgb(230,230,230),
            //Color.rgb(213,213,213),
            //Color.rgb(249,233,215) // selected row
            Color.rgb(249, 248, 215) // selected row
    };
    public static int[] LIST_BG = {                       // Cell Background
            Color.rgb(255, 255, 255),
            Color.rgb(239, 239, 239),
            LIST_FC[2]
    };

    //graph color 챠트
    public static int[] GRAP_BG = {
            LIST_BG[0],
            LIST_BG[1],
    };
    public static int GRAP_LN = Color.rgb(150, 0, 102);
    public static int GRAP_BR = Color.rgb(0, 143, 24);

    //바탕색
    public static int INFO_BG = Color.rgb(225, 223, 206);//BasePanel background
    public static int BASE_BG = Color.rgb(213, 213, 213);

    public static int DISA_BG = Color.rgb(155, 155, 155);//TextField disable
    public static int TBMN_DS = Color.rgb(227, 227, 227); // TabMenu DeSelect
    public static int ITEM_BG = Color.rgb(244, 244, 244);//종목명 Label3D
    public static int[] LAST_LN = {0, 0, 255};//종가선 색

    public static int TOOLTIP_BG = Color.rgb(255, 255, 225);

    public static int[] VOLUMESCALE_BASE = {197, 202, 205};
    public static int[] VOLUMESCALE_CUR = {122, 135, 154};
    public static int[] VOLUMESCALE_MAX = {29, 49, 68};

    public static int[][] CHART_BACK_COLOR = {
            CoSys.BLACK,
            CoSys.WHITE,
            {230, 230, 230},
            {250, 250, 250}
    };

    //Chart Color
    public static int[] CHART_COLOR = {
            Color.rgb(255, 60, 60),           //0: 상승
            Color.rgb(0, 141, 255),          //1: 하락
            Color.rgb(51, 51, 51),          //2: 보합
            Color.rgb(72, 192, 14),         //3: 거래량
            Color.rgb(40, 150, 1),          //4: 보조1(이평1)
            Color.rgb(228, 186, 26),        //5: 보조2(이평2)
            Color.rgb(158, 40, 255),        //6: 보조3(이평3)
            Color.rgb(158, 40, 255),        //6: 보조3(이평3)
            Color.rgb(159, 159, 159),       //7: 보조4(이평4)
            Color.rgb(8, 102, 70),          //8 : 보조 5(이평5)
            Color.rgb(123, 123, 192),       //9
            Color.rgb(210, 180, 140),       //10
            Color.rgb(147, 125, 198),       //10
    };

    public static int[][] CHART_COLORS = {
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

    public static int[][] MULTIBAR_COLOR_FOR_ASSET = {

            {53, 133, 221},            //국내주식
            {241, 204, 55},            //선진국주식
            {242, 72, 100},            //신흥국주식

            {141, 95, 196},            //국내채권
            {61, 205, 224},            //선진국채권
            {88, 176, 72},            //신흥국채권

            {244, 152, 61},            //하이일드채권
            {246, 98, 52},            //실물자산
            {85, 130, 87},            //단기자금
    };
    //2015. 9. 16 가로비율차트(멀티바) 디자인<<

    //2016. 1. 29 by hyh - 비교차트 컬러 수정
    //2020.05.25 by JJH >> 비교차트 기본 색상 변경 start
//    public static int[][] COMPARE_CHART_COLORS = {
//            //유진 - 매매자금 증시종합 색상
//            {247, 94, 94}, //red
//            {247, 94, 94}, //red
//            {21, 126, 232}, //blue
//            {0, 166, 81}, //green
//            {113, 110, 194}, //purple
//            {239, 174, 61} //yellow
//    };

    //2016. 1. 29 by hyh - 비교차트 컬러 수정
    public static int[][] COMPARE_CHART_COLORS = {
            //하나금투 - 비교차트 색상
            {0, 149, 160}, //청록색
            {0, 149, 160}, //청록색
            {81, 193, 241}, //하늘색
            {66, 121, 214}, //파란색
            {163, 114, 231}, //보라색
            {63, 197, 152} //녹색
    };
    //2020.05.25 by JJH >> 비교차트 기본 색상 변경 end


    public static void setColor(int t) {
        colorType = t;
    }

    public static final int KSQ_FG = Color.rgb(177, 96, 0);

    //지표추가및 설정화면 배경색
    public static final int JIPYO_BG = Color.rgb(236, 236, 227); //지표설정창 배경색
    public static final int JIPYO_CONTROL_BG = Color.rgb(242, 242, 242);
    public static final int JIPYO_BUTTON_BG = Color.rgb(206, 208, 230);
    public static final int INPUT_BACK_COLOR = Color.rgb(231, 231, 231);         //배경색
    public static final int TOOLBARBUTTOM_BACK_COLOR = Color.rgb(231, 231, 231);
    public static final int STATUSBAR_BACK_COLOR = Color.rgb(231, 231, 231);
    public static final int TOOLBARPANEL_BACK_COLOR = Color.rgb(231, 231, 231);
    public static int CHART_BACK_MAINCOLOR = Color.rgb(0, 0, 0); //차트 배경색
    public static final int[] CHART_BACK_MAINCOLORS = {0, 0, 0}; //차트 배경색
    //
    public static final int[] LAST_VALUE_DOWN_BG = CHART_COLORS[1]; //종가(저가) 폰트 색상
    public static final int[] LAST_VALUE_UP_BG = CHART_COLORS[0]; //종가(고가) 폰트 색상
    public static final int[] LAST_VALUE_SAME_BG = CHART_COLORS[2]; //종가(현재가) 폰트 색상

    public static final int LAST_VALUE_DOWN = Color.WHITE; //종가(저가) 폰트 색상
    public static final int LAST_VALUE_UP = Color.WHITE; //종가(고가) 폰트 색상
    public static final int LAST_VALUE_SAME = Color.WHITE; //종가(현재가) 폰트 색상

    public static final int MAX_VALUE_COLOR = CHART_COLOR[0]; //최대값 색상
    public static final int MIN_VALUE_COLOR = CHART_COLOR[1]; //최저값 색상
    public static final int CURRENT_VALUE_COLOR = Color.rgb(88, 88, 88); //현재가 색상
    public static final int[] VIEWPANEL_COLOR = {0, 64, 128}; //ViewPanel 배경 색상

    public static final int UP_LINE_COLOR = CHART_COLOR[0]; //상승외곽선 색상
    public static final int DOWN_LINE_COLOR = CHART_COLOR[1]; //하락외곽선 색상
    public static int[] UP_LINE_COLORS = CHART_COLORS[0]; //상승외곽선 색상
    public static int[] DOWN_LINE_COLORS = CHART_COLORS[1]; //하락외곽선 색상

    //public static final Color VOLUME_LINE_COLOR = new Color(146,164,243); //거래량 외곽선 색상
    public static final int VOLUME_LINE_COLOR = CHART_COLOR[3]; //거래량 외곽선 색상    

    //조회 메시지 출력 panel 설정 정보
    public static final int INQUIRY_BACK_COLOR = Color.rgb(217, 224, 234); //조회패널 배경
    public static final int INQUIRY_LINE_COLOR = Color.rgb(0, 0, 0); //조회패널 글자색
    public static final int INQUIRY_EDGE_COLOR = Color.rgb(0, 0, 0); //조회패널 테두리색

    public static final int ChartLogo_Color = Color.rgb(205, 204, 201); //차트 로고 색상
    public static final int[] SCALE_LINE_COLOR = {106, 106, 106}; //차트 로고 색상

    //2017.11 by PJM >> 자산 차트 적용
    public static int[][] ACCRUE_CHART_COLORS = {
            {251, 79, 91},//0: 상승
            {57, 135, 241},//1: 하락
            {40, 191, 119},//2: 보합
            {40, 191, 119},//3: 거래량

            {255, 83, 0},//4: 보조1(이평1)
            {165, 13, 208},//5: 보조2(이평2)
//		{0, 0, 255},//6: 보조3(이평3)
            {29, 75, 186},//6: 보조3(이평3)
            {61, 180, 162},//7: 보조4(이평4)
            {159, 255, 0},//8 : 보조 5(이평5)
            {252,135,5},//9 : 보조 6(이평6)

            {241, 94, 229},//10
            {158, 40, 255},//11
            {230, 100, 39},//12
            {125, 125, 125},//13	//투자자 주가 라인색
            {251, 79, 91},//14	//투자자 상승색
            {57, 135, 241},//15		//투자자 하락색
            {232, 153, 42},//16	//투자자 라인1
            {58, 95, 227},//17	//투자자 라인2
            {203, 203, 203},//18		//투자자 라인3
    };
    public static int[] handlerAreaColor = {211, 211, 211};
    public static int[] handlerLineColor = {222, 222, 222};
    public static int[] barTextColor = {0, 51, 51};
    public static int[] barNormalColor = {4, 60, 114};
    public static int[] percentSameBarCol = {227, 230, 233};
    //2017.11 by PJM >> 자산 차트 적용

    public static int LIST = Color.argb(10,0, 0, 0);
    public static int LIST_DARK = Color.argb(10,255, 255, 255);
    public static int[] hint = {6,11,17};
    public static float PRIMARY = Color.argb(51, 135, 146, 156);
    public static float PRIMARY_DARK = Color.argb(51, 118, 124, 130);

    public static int DISABLE_TEXT_COLOR = Color.argb(71,6, 11, 17);
    public static int DISABLE_TEXT_COLOR_DARK = Color.argb(71, 252, 252, 252);
    public static int HIGH_EMPHASIS = Color.rgb( 6, 11, 17);
    public static int HIGH_EMPHASIS_DARK = Color.rgb(252, 252, 252);
    public static int MEDIUM_EMPHASIS = Color.argb(143, 6, 11, 17);
    public static int MEDIUM_EMPHASIS_DARK = Color.argb(143, 252, 252, 252);

    public static int[] colorPalette = new int[]{
            Color.rgb(25,28,32), Color.rgb(19,72,186), Color.rgb(107,87,231), Color.rgb(179,0,0), Color.rgb(255,138,0), Color.rgb(153,104,58) ,Color.rgb(0,120,98),
            Color.rgb(68,75,82), Color.rgb(10,121,235), Color.rgb(157,141,255), Color.rgb(229, 14, 14), Color.rgb(255, 168, 0), Color.rgb(178, 137, 99) ,Color.rgb(0, 159, 131),
            Color.rgb(135, 146, 156), Color.rgb(0, 141, 255), Color.rgb(188, 177, 255), Color.rgb(255, 60, 60), Color.rgb(254, 198, 0), Color.rgb(234, 173, 119) ,Color.rgb(31, 184, 138),
            Color.rgb(206, 212, 218), Color.rgb(187, 224, 255), Color.rgb(208, 201, 255), Color.rgb(255, 216, 216), Color.rgb(254, 213, 0), Color.rgb(236, 192, 152) ,Color.rgb(86, 195, 157)
    };
    //2024.01.31 by SJW - 데이마켓 추가 >>
    public static int[] DAYMARKET_AREA_COLOR = {254, 213, 0};
    public static int[] PREMARKET_AREA_COLOR = {255, 122, 0};
    public static int[] AFTERMARKET_AREA_COLOR = {0, 141, 255};
    //2024.01.31 by SJW - 데이마켓 추가 <<
}