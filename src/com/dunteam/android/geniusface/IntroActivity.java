package com.dunteam.android.geniusface;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;

public class IntroActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);	
		setContentView(R.layout.activity_intro);
		
		WebView instruction = (WebView)findViewById(R.id.instruction);
		instruction.loadUrl("file:///android_asset/www/intro.html");
	}
	
	public void goToAbout(View v){
		Intent intent = new Intent(this, AboutActivity.class);
		startActivityForResult(intent, 1);
		overridePendingTransition(R.anim.from_right, R.anim.to_left);
	}
	
	public void goToMain(View v){
		Intent intent = new Intent(this, MainActivity.class);
		startActivityForResult(intent, 1);
	}	

}
