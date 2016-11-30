package com.pic;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Login extends Activity {
	private Button login_button;	
	private EditText login_account;	
	@Override
    public void onCreate(Bundle savedInstanceState)
    {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.login);//调用View类的onDraw函数    
       login_button = (Button)findViewById(R.id.button);
       login_account = (EditText)findViewById(R.id.editText1);
       login_button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {				
				String account=login_account.getText().toString();				
				Intent intent = new Intent();
				intent.putExtra("one",account );
				intent.setClass(Login.this, picture.class);				
				startActivity(intent);
				finish();
			}
       });        
    }
	public void CloseActivity()
	{
		finish();//这个方法，会自动调用注销方法onDestroy()，完成对程序的关闭
	}
}
