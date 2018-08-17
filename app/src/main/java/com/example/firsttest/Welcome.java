package com.example.firsttest;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.PermissionRequest;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

public class Welcome extends AppCompatActivity {

    Context a;
    String string1 = "May J Lee ";
    int i = R.drawable.pic_login;
    ImageView picture;
    Button turnto;
    String realPath;
    Button Call;

    public static final int CHOOSE_PHOTO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        a = getApplicationContext();
        final MyHandle myHandle = new MyHandle(Welcome.this);

        /*
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    myHandle.sendEmptyMessage(0x123);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();*/

        picture = findViewById(R.id.imageView);
        Button choose = findViewById(R.id.select);
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //动态申请权限
                if (ContextCompat.checkSelfPermission(Welcome.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Welcome.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    openAlbum();
                }
            }
        });

        turnto = findViewById(R.id.turnto);
        turnto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Welcome.this, Collasping.class);
                intent.putExtra("path", realPath);
                startActivity(intent);
            }
        });

        Call = findViewById(R.id.callphone);
        Call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call();
            }
        });

    }

    private void call() {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:1008611"));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CALL_PHONE},2);
        }else {
            startActivity(intent);
        }
    }

    private void openAlbum() {
        Intent intent=new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "你拒绝了权限", Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    call();
                }else {
                    Toast.makeText(this, "你拒绝了权限", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case CHOOSE_PHOTO:
                handleImagerOnKitKat(data);
                break;
        }
    }

    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case CHOOSE_PHOTO:
                if(resultCode==RESULT_OK){
                    if(Build.VERSION.SDK_INT>=19){
                        handleImagerOnKitKat(data);
                    }else{
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                    break;
        }
    }

    private void handleImageBeforeKitKat(Intent data) {
        Toast.makeText(this,"手机版本太低，换个手机把",Toast.LENGTH_SHORT).show();
    }*/

    private void handleImagerOnKitKat(Intent data) {
         String imagePath=null;
         if(data==null){
             Toast.makeText(this,"未选择图片 ",Toast.LENGTH_SHORT).show();
         }else {
             Uri uri = data.getData();


             if(uri!=null) {
                 if (DocumentsContract.isDocumentUri(this, uri)) {
                     String docID = DocumentsContract.getDocumentId(uri);
                     if ("com.android.providers.media.document".equals(uri.getAuthority())) {
                         String id = docID.split(":")[1];
                         String selection = MediaStore.Images.Media._ID + "=" + id;
                         imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
                     } else if ("com.android.providers.downloads,documents".equals(uri.getAuthority())) {
                         Uri contenturi = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docID));
                         imagePath = getImagePath(contenturi, null);
                     }
                 } else {
                     if ("content".equalsIgnoreCase(uri.getScheme())) {
                         imagePath = getImagePath(uri, null);
                     } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                         imagePath = uri.getPath();
                     }
                 }
                 displayImage(imagePath);
             }
         }
    }

    private void displayImage(String imagePath) {
        if(imagePath!=null){
            Bitmap bitmap= BitmapFactory.decodeFile(imagePath);
            picture.setImageBitmap(bitmap);
        }else{
            Toast.makeText(this,"获取图片失败",Toast.LENGTH_SHORT).show();
        }
    }

    private String getImagePath(Uri uri, String selection) {
        String path=null;
        Cursor cursor=getContentResolver().query(uri,null,selection,null,null);
        if(cursor!=null){
            if(cursor.moveToFirst()){
                path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        realPath=path;
        return  path;
    }


    private static class MyHandle extends Handler{
        WeakReference<Welcome> welcomeWeakReference;

        private MyHandle(Welcome welcome){
            welcomeWeakReference=new WeakReference<>(welcome);
        }

        @Override
        public void handleMessage(Message msg) {
            if(msg.what==0x123){
                Intent intent=new Intent(welcomeWeakReference.get().a,Collasping.class);
                intent.putExtra("pic_name",welcomeWeakReference.get().string1);
                intent.putExtra("pic_id",welcomeWeakReference.get().i);
                welcomeWeakReference.get().startActivity(intent);
                welcomeWeakReference.get().finish();
            }
            super.handleMessage(msg);
        }
    }
}
