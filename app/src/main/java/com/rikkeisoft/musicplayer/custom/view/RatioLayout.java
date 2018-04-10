package com.rikkeisoft.musicplayer.custom.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.rikkeisoft.musicplayer.R;

public class RatioLayout extends FrameLayout {

    public static final int LOCK_WIDTH = 1;
    public static final int LOCK_HEIGHT = 2;

    private int lock;
    private float ratio;

    public RatioLayout(Context context) {
        super(context);
    }

    public RatioLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public RatioLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    protected void init(AttributeSet attrs, int defStyle) {
        TypedArray attrArray = getContext().obtainStyledAttributes(attrs, R.styleable.RatioLayout, defStyle, 0);

        initAttributes(attrArray);

        attrArray.recycle();
    }

    private void initAttributes(TypedArray attrArray) {
        lock = attrArray.getInt(R.styleable.RatioLayout_lock, 0);
        ratio = attrArray.getFloat(R.styleable.RatioLayout_ratio, 1);

        android.util.Log.d("debug", "lock " + lock);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(lock != 0) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = MeasureSpec.getSize(heightMeasureSpec);
            if (lock == LOCK_WIDTH) {
                height = (int) (width * ratio);
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.UNSPECIFIED);
            }
            else if (lock == LOCK_HEIGHT) {
                width = (int) (height * ratio);
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.UNSPECIFIED);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
