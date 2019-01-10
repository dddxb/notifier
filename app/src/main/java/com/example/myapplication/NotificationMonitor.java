package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.os.Bundle;
import android.app.Notification;
import android.graphics.Bitmap;
import android.app.PendingIntent;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by 赵 on 2019/1/9.
 */

public class NotificationMonitor extends NotificationListenerService {

//    @Override
//    public void onListenerConnected() {
//        //当连接成功时调用，一般在开启监听后会回调一次该方法
//
//    }

    //post相关配置
    public final static int CONNECT_TIMEOUT = 60;
    public final static int READ_TIMEOUT = 100;
    public final static int WRITE_TIMEOUT = 60;
    public static final OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)//设置读取超时时间
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)//设置写的超时时间
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)//设置连接超时时间
            .build();


    //声明Sharedpreferenced对象
    private SharedPreferences sp;

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        //有新通知添加的时候调用

        Bundle extras = sbn.getNotification().extras;
        final String appname = sbn.getPackageName();//应用名字
        final String title = extras.getString(Notification.EXTRA_TITLE); //通知title
        final String text = extras.getString(Notification.EXTRA_TEXT); //通知内容

        System.out.println(sbn.getPackageName());
        System.out.println(title);
        System.out.println(text);

        /**
         * 获取SharedPreferenced对象
         * 第一个参数是生成xml的文件名
         * 第二个参数是存储的格式（**注意**本文后面会讲解）
         */
        sp = getSharedPreferences("User", Context.MODE_PRIVATE);
        //取出数据,第一个参数是写入时的键，第二个参数是如果没有获取到数据就默认返回的值。
        final String valueid = sp.getString("id", "Null");
        final String valueurl = sp.getString("url", "Null");

        new Thread(){
            @Override
            public void run() {
                try {
                    String content = post(valueurl, JsonGet(valueid,appname,title,text));
                    //ShowToast(content);
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

//    @Override
//    public void onNotificationRemoved(StatusBarNotification sbn) {
//        //通知被移除的时候调用
//        Log.e("onNotificationRemoved","posted");
//    }



    //打包成json串
    private String JsonGet(String id, String appname,String title,String data) {
        JSONObject object = new JSONObject();
        try {
            object.put("id", id);
            object.put("appname", appname);
            object.put("title", title);
            object.put("data", data);
            return object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    //
//    public void ShowToast(final String content) {
//        runOnUiThread(new Runnable() {         //runOnUiThread方法调用主线程
//            @Override
//            public void run() {
//                Toast.makeText(getApplicationContext(), content, Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    //执行post操作
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }

}

















//    cancelAllNotifications(); //移除所有可移除的通知
//    cancelNotification(String key); //移除指定key的通知，要求api21以上
//    cancelNotifications(String[] keys); //移除指定数组内的所有key的通知，要求api21以上
//    getActiveNotifications()； //获取通知栏上的所有通知，返回一个StatusBarNotification[]

//    int smallIconId = extras.getInt(Notification.EXTRA_SMALL_ICON); //通知小图标id
//    Bitmap largeIcon =  extras.getParcelable(Notification.EXTRA_LARGE_ICON); //通知的大图标，注意和获取小图标的区别
//    PendingIntent pendingIntent = sbn.getNotification().contentIntent; //获取通知的PendingIntent