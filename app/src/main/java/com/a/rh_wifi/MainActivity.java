package com.a.rh_wifi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity {
ImageView ButtonStart;
ProgressBar ProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButtonStart =findViewById(R.id.startButton);
         ProgressBar=findViewById(R.id.progressBar);


         ButtonStart.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 ProgressBar.setVisibility(View.VISIBLE);
                 ButtonStart.setColorFilter(Color.GREEN);
                 try {
                     Thread.sleep(1000);
                 } catch (InterruptedException e) {
                     e.printStackTrace();
                 }
                 Intent intent=new Intent(MainActivity.this,MainActivity2.class);
                 startActivity(intent);

             }
         });

    }
}