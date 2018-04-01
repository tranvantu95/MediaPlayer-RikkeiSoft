package com.rikkeisoft.musicplayer.custom.view;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

public class MyLinearLayoutManager extends LinearLayoutManager {

    public boolean isSmoothScrollToMid;
    public int snapPreference = LinearSmoothScroller.SNAP_TO_ANY;
    public int scrollOffsetTop, scrollOffsetBot, offsetTop, offsetBot;

    public boolean isMenuChapter;

    public MyLinearLayoutManager(Context context) {
        super(context);
    }

    public MyLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public MyLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
//        if(recyclerView.getChildCount() <= 0) return;

        SmoothScroller smoothScroller = new SmoothScroller(recyclerView.getContext());
        smoothScroller.setTargetPosition(position);
        startSmoothScroll(smoothScroller);
    }

    private class SmoothScroller extends LinearSmoothScroller {

//        private final float MILLISECONDS_PER_PX;
//        private long startTime;

        private SmoothScroller(Context context) {
            super(context);
//            startTime = System.currentTimeMillis();
//            MILLISECONDS_PER_PX = calculateSpeedPerPixel(context.getResources().getDisplayMetrics());
        }

        // scroll to top or bot
        @Override
        protected int getVerticalSnapPreference() {
            return snapPreference != LinearSmoothScroller.SNAP_TO_ANY ? snapPreference
                    : super.getVerticalSnapPreference();
        }

        // offset scroll
        @Override
        public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
//            android.util.Log.d("debug", "" + viewStart + " - " + viewEnd + " - " + boxStart + " - " + boxEnd);
            boxStart += offsetTop;
            boxEnd -= offsetBot;

            if(isSmoothScrollToMid) return ((boxStart + boxEnd) - (viewStart + viewEnd)) / 2;

            if(isMenuChapter) {
                int height = boxEnd - boxStart;
                int itemHeight = viewEnd - viewStart;

                scrollOffsetTop = scrollOffsetBot = (int) ((height - itemHeight) / 3.6f);
            }

            viewStart -= scrollOffsetTop;
            viewEnd += scrollOffsetBot;

            return super.calculateDtToFit(viewStart, viewEnd, boxStart, boxEnd, snapPreference);
        }

        // change speed scroll
//        @Override
//        public PointF computeScrollVectorForPosition(int targetPosition) {
////            android.util.Log.d("debug", "computeScrollVectorForPosition");
//            return MyLinearLayoutManager.this.computeScrollVectorForPosition(targetPosition);
//        }
//
//        @Override
//        protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
////            android.util.Log.d("debug", "calculateSpeedPerPixel");
//            return super.calculateSpeedPerPixel(displayMetrics);
//        }
//
//        @Override
//        protected int calculateTimeForScrolling(int dx) {
////            android.util.Log.d("debug", "calculateTimeForScrolling");
//
//            float countTime = System.currentTimeMillis() - startTime;
////            android.util.Log.d("debug", "" + countTime);
//
//            countTime /= 500;
//
//            if(countTime < 1) countTime = 1;
//
////            android.util.Log.d("debug", "" + countTime);
//
//            return (int) Math.ceil(Math.abs(dx) * MILLISECONDS_PER_PX / countTime);
//        }
    }
}
