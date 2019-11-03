package com.example.cameraalbumtest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.cameraalbumtest.util.HttpUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int TAKE_PHOTO = 1; //拍照

    public static final int CHOOSE_PHOTO = 2; // 从相册选择照片

    private ImageView imageViewPicture;

    private Uri imageUri;

    private String uploadUrl;

    private byte[] fileBuf;

    private String uploadFileName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button take_photo_bt = findViewById(R.id.take_photo);
        imageViewPicture = findViewById(R.id.picture);
        Button chooseFromAlbum = findViewById(R.id.choose_from_album);
        Button uploadBtn = findViewById(R.id.upload);
        take_photo_bt.setOnClickListener(this);
        chooseFromAlbum.setOnClickListener(this);
        uploadBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.take_photo:
                takePicture();
                break;
            case R.id.choose_from_album:
                choosePictureFromAlbum();
                break;
            case R.id.upload:
                for (byte b : fileBuf) {
                    Log.d("s", String.valueOf(b));
                }
            default:
                break;
        }
    }

    /*
    * 从相册中选择照片
    * */
    private void choosePictureFromAlbum() {
        //--权限和权限数组
        String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        String[] permissionArray = new String[]{permission};
        //---
        if(ContextCompat.checkSelfPermission(MainActivity.this, permission)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,permissionArray,1);
        }else{
            openAlbum();
        }
    }

    /*
    * 打开相册
    * */
    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);

    }


    /*
    * 调用照相机进行拍照
    * */
    private void takePicture() {
        uploadFileName = System.currentTimeMillis()+".jpg";
        File outputImage = new File(getExternalCacheDir(), uploadFileName);
        Log.d("照相的图片名称", uploadFileName);
        try {
            if(outputImage.exists()){
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT>=24){
            imageUri = FileProvider.getUriForFile(
                    MainActivity.this,
                    "com.example.cameraalbumtest.fileprovider",
                    outputImage);
        }else{
            imageUri = Uri.fromFile(outputImage);
        }
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
//        Log.d("11", MediaStore.EXTRA_OUTPUT);
//        Log.d("22", String.valueOf(imageUri));
//        Log.d("33", imageUri.getPath());
        startActivityForResult(intent,TAKE_PHOTO);
    }

    /*
    * 处理startactivity返回的结果
    * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK){
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(imageUri);
                        fileBuf=convertToBytes(inputStream);   //后来加的
                        Bitmap bitmap = BitmapFactory.decodeByteArray(fileBuf, 0, fileBuf.length);
                        imageViewPicture.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK){
                    handleSelect(data);
                    Log.d("相册选中的照片名称", uploadFileName);
                }
                break;
            default:
                break;
        }
    }
    /*
    * 选择照片以后的读取与显示工作
    * */
    private void handleSelect(Intent intent) {
        Cursor cursor = null;
        Uri uri = intent.getData();
        cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
            uploadFileName = cursor.getString(columnIndex);
        }
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            fileBuf=convertToBytes(inputStream);
            Bitmap bitmap = BitmapFactory.decodeByteArray(fileBuf, 0, fileBuf.length);
            imageViewPicture.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        cursor.close();
    }

    /*
    * 弹出小框框来选择是不是赋予权限
    * */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1 : //这个1就是那个传入的获取参数的1
                if(grantResults.length>0 && grantResults[0] ==PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else{
                    Toast.makeText(this, "你拒绝授权", Toast.LENGTH_SHORT).show();
                }

        }
    }
    /*
    * 文件转化为字节流
    * */
    private byte[] convertToBytes(InputStream inputStream) throws Exception{
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int len = 0;
        while ((len = inputStream.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        out.close();
        inputStream.close();
        return  out.toByteArray();
    }

    /*
    * 上传图片到服务器
    * */
    private void upload(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpUtil.sendOkHttpRequest(uploadFileName, fileBuf, uploadUrl, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String res = response.body().string();
                        Log.d("服务器端返回的数据", res);
                    }
                });
            }
        }).start();
    }

}
