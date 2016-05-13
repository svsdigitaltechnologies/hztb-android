package com.svs.hztb.CustomViews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by venunalla on 12/05/16.
 */
public class WalkWayTextView extends TextView {


    public WalkWayTextView(Context context) {
        super(context);
    }

    public WalkWayTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WalkWayTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void setTypeface(Typeface tf) {
        Typeface walkwayFont = Typeface.createFromAsset(getContext().getAssets(),  "fonts/walkway_ultrabold.ttf");
        super.setTypeface(walkwayFont);

    }
}
