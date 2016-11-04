package com.limwoon.musicwriter.list;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import static com.facebook.GraphRequest.TAG;

/**
 * Created by 운택 on 2016-11-02.
 */

public class SheetRecyDivider extends RecyclerView.ItemDecoration {
    Paint paint;
    int height;

    public SheetRecyDivider(){
        paint = new Paint();
        paint.setStrokeWidth(10);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        if(((LinearLayoutManager)parent.getLayoutManager()).getReverseLayout()){
            outRect.top = 10;
            if(parent.getChildLayoutPosition(view)==0)
                outRect.bottom = 14;
        }else{
            outRect.top = 10;
            if(parent.getChildLayoutPosition(view)==state.getItemCount()-1)
                outRect.bottom = 14;
        }

    }
}
