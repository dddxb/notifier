package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;

import android.service.notification.StatusBarNotification;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import okhttp3.RequestBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.OkHttpClient;
import java.util.concurrent.TimeUnit;
import android.support.v4.content.ContextCompat;
import com.example.myapplication.NotificationMonitor;
import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import java.util.Set;
import android.support.v4.app.NotificationManagerCompat;
import android.content.Intent;
import android.provider.Settings;
import android.content.ComponentName;


public class MainActivity extends AppCompatActivity {

    //post相关配置
    public final static int CONNECT_TIMEOUT = 60;
    public final static int READ_TIMEOUT = 100;
    public final static int WRITE_TIMEOUT = 60;
    public static final OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)//设置读取超时时间
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)//设置写的超时时间
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)//设置连接超时时间
            .build();

    //前端页面存取操作相关配置
    private EditText meditText1, meditText2;
    private Button SaveBtn;
    //声明Sharedpreferenced对象
    private SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //判断NotificationListenerService是否启动，如果没有，重新启动
        if (!isNotificationListenerEnabled(this)){
            openNotificationListenSettings();
        }
        toggleNotificationListenerService();

        //前端定义存取框，按钮
        meditText1 = (EditText) findViewById(R.id.edit1);
        meditText2 = (EditText) findViewById(R.id.edit2);
        SaveBtn = (Button) findViewById(R.id.btn_Save);

    }

    //前台执行按钮操作
    public void Click(View view) {
        /**
         * 获取SharedPreferenced对象
         * 第一个参数是生成xml的文件名
         * 第二个参数是存储的格式（**注意**本文后面会讲解）
         */
        sp = getSharedPreferences("User", Context.MODE_PRIVATE);
        if (meditText1.length() == 0)                              //判断ID输入框是否为空
        {
            Toast.makeText(MainActivity.this, "请输入ID", Toast.LENGTH_LONG).show();
        }else {
            if (meditText2.length() == 0)                              //判断URL输入框是否为空
            {
                Toast.makeText(MainActivity.this, "请输入URL", Toast.LENGTH_LONG).show();
            }
        }


        if (meditText1.length() != 0 && meditText2.length() != 0) {
            switch (view.getId()) {
                //判断前端是保存按钮，则执行此操作
                case R.id.btn_Save:
                    //获取到edit对象
                    SharedPreferences.Editor edit = sp.edit();
                    //通过editor对象写入数据
                    edit.putString("id", meditText1.getText().toString().trim());
                    edit.putString("url", meditText2.getText().toString().trim());
                    //提交数据存入到xml文件中
                    edit.commit();
                    Toast.makeText(MainActivity.this, "保存成功", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }








    //检测通知监听服务是否被授权
    public boolean isNotificationListenerEnabled(Context context) {
        Set<String> packageNames = NotificationManagerCompat.getEnabledListenerPackages(this);
        if (packageNames.contains(context.getPackageName())) {
            return true;
        }
        return false;
    }
    //打开通知监听设置页面
    public void openNotificationListenSettings() {
        try {
            Intent intent;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            } else {
                intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            }
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //把应用的NotificationListenerService实现类disable再enable，即可触发系统rebind操作
    private void toggleNotificationListenerService() {
        PackageManager pm = getPackageManager();
        pm.setComponentEnabledSetting(
                new ComponentName(this,  NotificationMonitor.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        pm.setComponentEnabledSetting(
                new ComponentName(this,  NotificationMonitor.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }



}


