package com.ccs.app.musicplayer.custom.view;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class MyItemDecoration extends RecyclerView.ItemDecoration {

    private int left, top, right, bot;

    public void setSpace(int space) {
        left = top = right = bot = space;
    }

    public MyItemDecoration() {
    }

    public MyItemDecoration(int space) {
        setSpace(space);
    }

    public MyItemDecoration(int left, int top, int right, int bot) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bot = bot;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = left;
        outRect.right = right;
        outRect.bottom = bot;
        outRect.top = top;

        // Add top margin only for the first item to avoid double itemSpace between items
//        if (parent.getChildLayoutPosition(view) == 0) {
//            outRect.top = itemSpace;
//        } else {
//            outRect.top = 0;
//        }
    }
}
