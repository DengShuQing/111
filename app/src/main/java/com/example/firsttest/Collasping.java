package com.example.firsttest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class Collasping extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collasping);

        Intent intent=getIntent();
        /*String name=intent.getStringExtra("pic_name");
        int picid=intent.getIntExtra("pic_id",0);*/
        String path=intent.getStringExtra("path");

        Toolbar toolbar=findViewById(R.id.toolbar);
        CollapsingToolbarLayout collapsingToolbarLayout=findViewById(R.id.collasping);
        ImageView imageView=findViewById(R.id.app_bar_image);
        TextView textView=findViewById(R.id.textView);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        /*
        collapsingToolbarLayout.setTitle(name);
        imageView.setImageResource(picid);
        String content=generate(name);
        textView.setText(content);*/

        collapsingToolbarLayout.setTitle("图片详情");
        Bitmap bitmap= BitmapFactory.decodeFile(path);
        imageView.setImageBitmap(bitmap);
        String content=generate("随便写点啥");
        textView.setText(content);


    }

    private String generate(String name) {
        StringBuilder content=new StringBuilder();
        for(int i=0;i<500;i++){
            content.append(name);
        }
        return content.toString();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:finish();return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
