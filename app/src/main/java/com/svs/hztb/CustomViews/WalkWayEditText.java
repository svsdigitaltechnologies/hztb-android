package com.svs.hztb.CustomViews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by venunalla on 12/05/16.
 */
public class WalkWayEditText extends EditText {
    public WalkWayEditText(Context context) {
        super(context);
    }

    public WalkWayEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WalkWayEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setTypeface(Typeface tf) {
        Typeface walkwayFont = Typeface.createFromAsset(getContext().getAssets(),  "fonts/walkway_ultrabold.ttf");
        super.setTypeface(walkwayFont);
    }
}
