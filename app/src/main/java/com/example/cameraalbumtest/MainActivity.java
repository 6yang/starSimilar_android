package com.example.cameraalbumtest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.graphics.Picture;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Target;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int TAKE_PHOTO = 1; //拍照

    public static final int CHOOSE_PHOTO = 2; // 从相册选择照片

    private ImageView imageViewPicture;

    private Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button take_photo_bt = findViewById(R.id.take_photo);
        imageViewPicture = findViewById(R.id.picture);
        Button chooseFromAlbum = findViewById(R.id.choose_from_album);
        take_photo_bt.setOnClickListener(this);
        chooseFromAlbum.setOnClickListener(this);
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

            default:
                break;
        }
    }

    //从相册中选择照片
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

    //打开相册
    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);

    }


    //拍照
    private void takePicture() {
        File outputImage = new File(getExternalCacheDir(), "output_img.jpg");
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
        startActivityForResult(intent,TAKE_PHOTO);
    }


    //将拍摄的照片显示出来
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK){
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(imageUri);
//                        Log.d("问题点", "onActivityResult: "+inputStream.toString());
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));

                        imageViewPicture.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK){
                    //判断手机版本号
                    if(Build.VERSION.SDK_INT >=19){ //4.4 以上使用这个方法处理图片
                      handlerImageOnKitKat(data);
                    }else{  //4.4 以下使用这个方法处理图片

                    }
                }
                break;
            default:
                break;
        }
    }

    //4.4 以上使用这个方法处理图片
    @TargetApi(19)
    private void handlerImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if(DocumentsContract.isDocumentUri(this,uri)){
            //如果是Document类型的Uri 则通过document 来处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri,null);
            }
        }else if("content".equalsIgnoreCase(uri.getScheme())){
            //如果是普通类型的URI，使用普通方式进行处理
            imagePath  = getImagePath(uri,null);
        }else if("file".equalsIgnoreCase(uri.getScheme())){
            //如果是file类型的图片 ，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        //根据图片路径显示图片
        disPlayImage(imagePath);
    }

    //获取图片路径

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //获取图片
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if(cursor!=null){
            if(cursor.moveToFirst()){  //读取一条
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    //根据图片路径显示图片
    private void disPlayImage(String imagePath) {
        if (imagePath!=null){
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            imageViewPicture.setImageBitmap(bitmap);
        }else{
            Toast.makeText(this, "图片没有找到", Toast.LENGTH_SHORT).show();
        }
    }

    //权限
    //弹出小框框来选择是不是赋予权限
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
}
