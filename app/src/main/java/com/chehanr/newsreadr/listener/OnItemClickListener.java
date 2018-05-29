package com.chehanr.newsreadr.listener;

import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public abstract class OnItemClickListener extends RecyclerView.SimpleOnItemTouchListener {

    private GestureDetector mGestureDetector;

    public OnItemClickListener() {

    }

    @Override
    public boolean onInterceptTouchEvent(final RecyclerView rv, MotionEvent e) {
        if (mGestureDetector == null) {
            mGestureDetector = new GestureDetector(rv.getContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    final View childView = rv.findChildViewUnder(e.getX(), e.getY());
                    if (childView != null) {
                        final int position = rv.getChildAdapterPosition(childView);
                        onItemClick(childView, position);
                        return true;
                    }
                    return false;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    super.onLongPress(e);
                    final View childView = rv.findChildViewUnder(e.getX(), e.getY());
                    if (childView != null) {
                        final int position = rv.getChildAdapterPosition(childView);
                        onItemLongClick(childView, position);
                    }
                }
            });
        }
        return mGestureDetector.onTouchEvent(e);
    }

    public abstract void onItemClick(View view, int position);

    public void onItemLongClick(View view, int position) {

    }
}