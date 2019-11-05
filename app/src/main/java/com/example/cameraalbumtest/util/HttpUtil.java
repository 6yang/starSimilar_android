package com.example.cameraalbumtest.util;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;

import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpUtil {

    public static void sendOkHttpRequest(String uploadFileName, byte[] fileBuf,String uploadUrl,String group,okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();

        //上传文件域的请求体部分
        RequestBody formBody = (RequestBody) RequestBody
                .create(MediaType.parse("image/jpeg"), fileBuf);
        //整个上传的请求体部分（普通表单+文件上传域）
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("username", "liuyang")
                .addFormDataPart("starGroup", group)
                .addFormDataPart("imgFile", uploadFileName, formBody)
                .build();
        Request request = new Request.Builder()
                .url(uploadUrl)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }
}
