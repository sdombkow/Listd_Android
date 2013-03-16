package com.listdnow.www.authentication;

import com.listdnow.www.R;
import com.listdnow.www.R.id;
import com.listdnow.www.R.layout;
import com.listdnow.www.R.menu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class WelcomeActivity extends Activity {

	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_welcome);

	    findViewById(R.id.registerButton).setOnClickListener(
	        new View.OnClickListener() {
	            public void onClick(View v) {
	                // No account, load new account view
	                Intent intent = new Intent(WelcomeActivity.this,
	                    RegisterActivity.class);
	                startActivityForResult(intent, 0);
	            }
	        });

	    findViewById(R.id.loginButton).setOnClickListener(
	        new View.OnClickListener() {
	            public void onClick(View v) {
	                // Existing Account, load login view
	                Intent intent = new Intent(WelcomeActivity.this,
	                    LoginActivity.class);
	                startActivityForResult(intent, 0);
	            }
	        });
	}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_welcome, menu);
        return true;
    }
    

@Override
public void onBackPressed() {
    Intent startMain = new Intent(Intent.ACTION_MAIN);
    startMain.addCategory(Intent.CATEGORY_HOME);
    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(startMain);
    finish();
}
    
    
}
