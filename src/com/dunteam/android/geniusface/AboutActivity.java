package com.dunteam.android.geniusface;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;

public class AboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);	
		setContentView(R.layout.activity_about);
		
		WebView instruction = (WebView)findViewById(R.id.about);
		instruction.loadUrl("file:///android_asset/www/about.html");		
	}
	
	public void goBack(View v){
		Intent returnIntent = new Intent();
		setResult(RESULT_OK, returnIntent);        
		finish();
		overridePendingTransition(R.anim.from_right, R.anim.to_left);
	}	

}
