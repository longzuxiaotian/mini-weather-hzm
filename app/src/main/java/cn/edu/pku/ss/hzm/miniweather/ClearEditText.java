package cn.edu.pku.ss.hzm.miniweather;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

/**
 * Created by i on 2017/11/9.
 * 自定义搜索控件
 */

public class ClearEditText extends EditText implements View.OnFocusChangeListener, TextWatcher {

    /**
     * 删除按钮的引用
     */
    private Drawable mClearDrawable;

    public ClearEditText(Context context) {this(context,null);}

    public ClearEditText(Context context, AttributeSet attrs) {
        this(context,attrs,R.attr.editTextStyle);
    }

    public ClearEditText(Context context, AttributeSet attr,int defStyle) {
        super(context, attr, defStyle);
    }


    public void init() {
        //获取EditText的DrawableRight.假如没有设置就使用默认的图片
        mClearDrawable = getCompoundDrawables()[2];
        if(mClearDrawable == null) {
            mClearDrawable = getResources().getDrawable(R.drawable.contact_search_box_edittext_keyword_background);
        }
        mClearDrawable.setBounds(0,0,mClearDrawable.getIntrinsicWidth(),mClearDrawable.getIntrinsicHeight());
        setClearIconVisible(false);
        setOnFocusChangeListener(this);
        addTextChangedListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(getCompoundDrawables()[2] != null) {
            if(event.getAction() == MotionEvent.ACTION_UP) {
                boolean touchable = event.getX() > (getWidth()-getPaddingRight()-mClearDrawable.getIntrinsicWidth())
                        && event.getX() < ((getWidth()-getPaddingRight()));
                if(touchable) {
                    this.setText("");
                }
            }
        }
        return super.onTouchEvent(event);
    }

    /**
     * 绘制清除图标
     * @param visible
     */
    private void setClearIconVisible(boolean visible) {
        Drawable right = visible ? mClearDrawable : null;
        setCompoundDrawables(getCompoundDrawables()[0],getCompoundDrawables()[1],right,getCompoundDrawables()[3]);
    }


    /**
     * 设置清除图标的状态
     */
    @Override
    public void onFocusChange(View view, boolean b) {
        if(hasFocus()) {
            setClearIconVisible(getText().length()>0);
        } else {
            setClearIconVisible(false);
        }
    }

    /**
     * 回调方法
     */
    @Override
    public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        setClearIconVisible(text.length()>0);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        
    }
//
//    public void setShakeAnimation() { this.setAnimation(shakeAnimation(5));}

}
