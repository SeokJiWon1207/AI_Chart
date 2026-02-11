package drfn.chart.comp;


import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;

import drfn.chart.util.COMUtil;
import drfn.chart.util.CoSys;
import drfn.chart_src.R;

public class UnderLineEditText extends LinearLayout implements TextWatcher, View.OnClickListener, View.OnFocusChangeListener {

    private Context context;
    private OnFocusChangeListener onFocusChangeListener;
    private LinearLayout llClearEditText;
    public TextView tvDescription, tvUnderline, tvErrorTooltip;
    public ExEditText edInput;
    private Button btnClear;
    private OnTouchListener onTouchListener;

    public Boolean bHasText = false;
    public float m_fMaxValue, m_fMinValue;

    public UnderLineEditText(Context context) {
        this(context, null);
        this.context = context;
    }

    public UnderLineEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        this.context = context;
    }

    public UnderLineEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init();
    }


//    public ClearEditText(final Context context) {
//        super(context);
//        this.context = context;
//        init();
//    }
//
//    public ClearEditText(final Context context, final AttributeSet attrs) {
//        super(context, attrs);
//        this.context = context;
//        init();
//    }
//
//    public ClearEditText(final Context context, final AttributeSet attrs, final int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        this.context = context;
//        init();
//    }

    @Override
    public void setOnFocusChangeListener(OnFocusChangeListener onFocusChangeListener) {
        this.onFocusChangeListener = onFocusChangeListener;
    }

    @Override
    public void setOnTouchListener(OnTouchListener onTouchListener) {
        this.onTouchListener = onTouchListener;
    }

    public void setBtnClearMargin (int margin) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) btnClear.getLayoutParams();
        layoutParams.rightMargin = margin;
        btnClear.setLayoutParams(layoutParams);
    }
    /**
     * view 초기화
     */
    private void init() {

        int resId;
        if (COMUtil.getSkinType() != COMUtil.SKIN_BLACK)
            resId = R.layout.underline_edittext;
        else
            resId = R.layout.underline_edittext_dark;

        View view = inflate(getContext(), resId, this);

        llClearEditText = (LinearLayout) view.findViewById(R.id.llClearEditText);
        tvDescription = (TextView) view.findViewById(R.id.tv_description);
        edInput = (ExEditText) view.findViewById(R.id.edInput);
        tvUnderline = (TextView) view.findViewById(R.id.tv_underline);
        tvErrorTooltip = (TextView) view.findViewById(R.id.tv_error_tooltip);
        tvErrorTooltip.setVisibility(GONE); //기본 상태는 숨김처리
        btnClear = (Button) view.findViewById(R.id.btnClear);
        edInput.setImeOptions(EditorInfo.IME_ACTION_DONE); //2023.11.21 by CYJ - underlineEditText 다음 버튼 > 완료 버튼 수정
        edInput.requestFocus();

        //2023.11.29 by CYJ - kakaopay 주가이동평균에서만 컬러라인 설정 기능이 오픈된 상태로 특정 지표에서만 마진값 변경 >> 다른 지표에선 x 표시 공백 35dp 가 더 필요함
        if(COMUtil.getGraphListStr().contains("주가이동평균")) {
            LinearLayout.LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0,(int) COMUtil.getPixel_W(4),(int) COMUtil.getPixel_W(72+9),0);
            edInput.setLayoutParams(lp);
        }
        //2023.11.29 by CYJ - kakaopay 주가이동평균에서만 컬러라인 설정 기능이 오픈된 상태로 특정 지표에서만 마진값 변경 <<
        setClearIconVisible(false);

        edInput.setOnFocusChangeListener(this);
        edInput.addTextChangedListener(this);
        btnClear.setOnClickListener(this);

        //2023.11.21 by CYJ - kakaopay 인터렉션 추가 >> //2024.01.04 by CYJ - 텍스트필드가 비어있을때만 적용인 내용으로 애니메이션 제거
//        Animation enlarge = AnimationUtils.loadAnimation(this.getContext(), R.anim.anim_center_to_top_large_to_small);
//        tvDescription.startAnimation(enlarge);
//        tvDescription.invalidate();
        //2023.11.21 by CYJ - kakaopay 인터렉션 추가 <<
        COMUtil.setGlobalFont((ViewGroup) view);
    }

    @Override
    public void onFocusChange(final View view, final boolean hasFocus) {
        tvErrorTooltip.setVisibility(GONE);
        if (hasFocus) {
            setClearIconVisible(edInput.getText().length() > 0);
            if(COMUtil.getSkinType() != COMUtil.SKIN_BLACK) {
                tvDescription.setTextColor(CoSys.HIGH_EMPHASIS);
                tvUnderline.setBackgroundColor(CoSys.GREY990_BLACK);
            } else {
                tvDescription.setTextColor(CoSys.HIGH_EMPHASIS_DARK);
                tvUnderline.setBackgroundColor(CoSys.GREY990_BLACK_DARK);
            }
        } else {
            setClearIconVisible(false);
            if(COMUtil.getSkinType() != COMUtil.SKIN_BLACK) {
                tvDescription.setTextColor(CoSys.MEDIUM_EMPHASIS);
                tvUnderline.setBackgroundColor((int) CoSys.PRIMARY);
            } else {
                tvDescription.setTextColor(CoSys.MEDIUM_EMPHASIS_DARK);
                tvUnderline.setBackgroundColor((int) CoSys.PRIMARY_DARK);
            }

            if (edInput.isEnabled()) {
                //COMUtil.hideKeyboard(context);
            }

        }

        if (onFocusChangeListener != null) {
            onFocusChangeListener.onFocusChange(view, hasFocus);
        }

    }

    @Override
    public final void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
        Animation animation_fade_in = AnimationUtils.loadAnimation(this.getContext(), R.anim.anim_error_fade_in); //2023.11.21 by CYJ - kakaopay 인터렉션 추가
        if (edInput.isFocused()) {
            if(s.length() > 0) {
                bHasText = true;

                //2023.11.29 by CYJ - 입력값 세자리마다 (,) 추가 >>
                String s1 = s.toString().replaceAll(",", "");
                if(Float.parseFloat(s1) > m_fMaxValue || Float.parseFloat(s1) <  m_fMinValue) { //2023.11.15 by CYJ - 최소값보다 작은 경우에 에러케이스 적용
                //2023.11.30 by CYJ - 소수점 관련 지표 이슈로 원복 후 2차개발에 진행
//                if(Float.parseFloat(s.toString()) > m_fMaxValue || Float.parseFloat(s.toString()) <  m_fMinValue) { //2023.11.15 by CYJ - 최소값보다 작은 경우에 에러케이스 적용
                //2023.11.29 by CYJ - 입력값 세자리마다 (,) 추가  <<
                    if(!tvErrorTooltip.isShown()) { //2024.01.08 by CYJ - 텍스트 입력시 에러툴팁 깜빡거림 수정 jira 232
                        tvErrorTooltip.setVisibility(VISIBLE);
                        //2023.11.21 by CYJ - kakaopay 인터렉션 추가 >>
                        tvErrorTooltip.startAnimation(animation_fade_in);
                        tvErrorTooltip.invalidate();
                        //2023.11.21 by CYJ - kakaopay 인터렉션 추가 <<
                    }
                    if (COMUtil.getSkinType() != COMUtil.SKIN_BLACK) {
                        tvDescription.setTextColor(Color.RED);
                    } else {
                        tvDescription.setTextColor(CoSys.RED_DARK);
                    }
                } else {
                    tvErrorTooltip.setVisibility(GONE);
                    if (COMUtil.getSkinType() != COMUtil.SKIN_BLACK)
                        tvDescription.setTextColor(CoSys.HIGH_EMPHASIS);
                    else
                        tvDescription.setTextColor(CoSys.HIGH_EMPHASIS_DARK);
                }
                //2023.11.29 by CYJ - 입력값 세자리마다 (,) 추가 >>
                //2023.11.30 by CYJ - 소수점 관련 지표 이슈로 원복 후 2차개발에 진행 >>
                edInput.removeTextChangedListener(this);

                String sInput = s.toString().replaceAll(",", "");
                String strNumber = "";
                String strDecimal = "";

                if(sInput.contains(".")) {
                    strNumber = sInput.substring(0, sInput.indexOf("."));
                    strDecimal = sInput.substring(sInput.indexOf("."), sInput.length());
                } else {
                    strNumber = sInput;
                }

                double doubleText= 0.0;
                try {
                    doubleText = Double.parseDouble(strNumber);
                } catch (Exception e) {
                }
                DecimalFormat decimalFormat = new DecimalFormat("#,###");

                try {
                    sInput = decimalFormat.format(doubleText) + strDecimal;
                    edInput.setText(sInput);
                    edInput.setSelection(sInput.length());
                } catch (NumberFormatException e) {
                    edInput.setText("");
                }

                edInput.addTextChangedListener(this);
                //2023.11.30 by CYJ - 소수점 관련 지표 이슈로 원복 후 2차개발에 진행 <<
                //2023.11.29 by CYJ - 입력값 세자리마다 (,) 추가 <<
            } else {
                bHasText = false;
                //2024.01.08 by CYJ - 텍스트 모두 지웠을때 잔상 제거 jira 232 >>
                tvErrorTooltip.setVisibility(GONE);
                //2023.11.21 by CYJ - kakaopay 인터렉션 추가 >>
                //tvErrorTooltip.startAnimation(animation_fade_in);
                //tvErrorTooltip.invalidate();
                //2023.11.21 by CYJ - kakaopay 인터렉션 추가 <<
                //2024.01.08 by CYJ - 텍스트 모두 지웠을때 잔상 제거 jira 232 <<
//
//                if(COMUtil.getSkinType() != COMUtil.SKIN_BLACK) {
//                    tvDescription.setTextColor(Color.RED);
//                } else {
//                    tvDescription.setTextColor(CoSys.RED_DARK);
//                }
            }
            setClearIconVisible(bHasText);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    public void setClearIconVisible(boolean visible) {
        btnClear.setVisibility(visible ? VISIBLE : INVISIBLE);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnClear) {
            edInput.setText("");

            if(tvErrorTooltip != null && tvDescription != null) {
                tvErrorTooltip.setVisibility(GONE);
                if (COMUtil.getSkinType() != COMUtil.SKIN_BLACK)
                    tvDescription.setTextColor(CoSys.HIGH_EMPHASIS);
                else
                    tvDescription.setTextColor(CoSys.HIGH_EMPHASIS_DARK);
            }
        }
    }

    /**
     * text를 반환한다.
     * @return
     */
    public String getText() {
        String val = "";
        if (edInput != null) {
            val = edInput.getText().toString();
        }
        return val;
    }

    public void hideKeyboard() {
//        View view = COMUtil.getActivity().getCurrentFocus();
//        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
//        if (view != null && imm != null) {
//            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
//        }
    }

    public void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(edInput, InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    /**
     * Input type 을 설정한다.
     * @param type
     */
    public void setInputType(int type) {
        edInput.setInputType(type);
    }

    /**
     * enable 설정 한다.
     * @param flag
     */
    public void setEnabled(boolean flag) {
        if(flag) {
            tvErrorTooltip.setVisibility(GONE);
            if(COMUtil.getSkinType() != COMUtil.SKIN_BLACK) {
                tvDescription.setTextColor(CoSys.MEDIUM_EMPHASIS);
                edInput.setTextColor(CoSys.HIGH_EMPHASIS);
                tvUnderline.setBackgroundColor((int) CoSys.PRIMARY);
            } else {
                tvDescription.setTextColor(CoSys.MEDIUM_EMPHASIS_DARK);
                edInput.setTextColor(CoSys.HIGH_EMPHASIS_DARK);
                tvUnderline.setBackgroundColor((int) CoSys.PRIMARY_DARK);
            }
        } else {
            tvErrorTooltip.setVisibility(GONE);
            if(COMUtil.getSkinType() != COMUtil.SKIN_BLACK) {
                tvDescription.setTextColor(CoSys.DISABLE_TEXT_COLOR);
                edInput.setTextColor(CoSys.DISABLE_TEXT_COLOR);
                tvUnderline.setBackgroundColor(CoSys.LIST);
            } else {
                tvDescription.setTextColor(CoSys.DISABLE_TEXT_COLOR_DARK);
                edInput.setTextColor(CoSys.DISABLE_TEXT_COLOR_DARK);
                tvUnderline.setBackgroundColor(CoSys.LIST_DARK);
            }
        }

        if(edInput != null) {
            edInput.setEnabled(flag);
        }
        btnClear.setEnabled(flag);
    }

    /**
     * edittext의 max length를 설정한다.
     * @param length
     */
    public void setMaxLength(int length) {
        if(edInput!=null){
            edInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(length)});
        }
    }

    /**
     * 힌트를 설정한다.
     * @param hint
     */
    public void setHint(String hint) {
        if(edInput != null) {
            edInput.setHint(hint);
        }

    }

    public void setText(String str) {
        if(edInput != null){
            edInput.setText(str);
        }
    }

    public void setTextSize(int size) {
        if (edInput != null) {
            COMUtil.setTextSize(edInput, size);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
