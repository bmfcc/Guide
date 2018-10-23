package com.iscte.guide;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
    }

    public void museumsButton_clicked(View view){
        Intent intent = new Intent(this,MuseumsActivity.class);
        startActivity(intent);
    }

    public void lastVisitsButton_clicked(View view){
    }
}
