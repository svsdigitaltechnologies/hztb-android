package com.svs.hztb.Utils;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.TextView;

import com.svs.hztb.R;


public class LoadingBar extends Dialog {

	public LoadingBar(Context context) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		getWindow().clearFlags(LayoutParams.FLAG_DIM_BEHIND);
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		setCancelable(false);
		setContentView(R.layout.loadingbar);
	}

	public void setText(String text) {
		TextView findViewById = (TextView) findViewById(R.id.textView1);
		findViewById.setText(text);
	}

}
