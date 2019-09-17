package com.qixiaoyi.cariyou.module.T1splash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.qixiaoyi.cariyou.R;
import com.qixiaoyi.cariyou.module.RegisteredActivity;

public class WelcomeActivity extends Activity {

    Button bt_singup;
    Button bt_visitor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        bt_singup = (Button) findViewById(R.id.bt_singup);
        bt_visitor = (Button) findViewById(R.id.bt_visitor);
        bt_singup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WelcomeActivity.this,RegisteredActivity.class));
            }
        });
        bt_visitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WelcomeActivity.this,LoginActivity.class));
            }
        });
    }

    public void singup(View view){
    }

    public void visitor(View view){
    }
    public void startlogin(View view){

    }
}
