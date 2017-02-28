package com.walle.control;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private List<AppEntity> entities = new ArrayList<>();
    private IconAdapter adapter;
    private TimeService timeService;
    public static boolean isControl;
    private int clickPosition;
    private boolean isControlStatusBarEnable;
    private boolean isActivate;
    private Button btn_status;
    private Button btn_setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_status = (Button) findViewById(R.id.btn_statusBar);
        btn_setting = (Button) findViewById(R.id.btn_activate);
        adapter = new IconAdapter(this, entities);
        ListView listView = ((ListView) findViewById(R.id.listView));
        listView.setAdapter(adapter);
        getAllApp();

        listView.setOnItemClickListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_statusBar:
                //控制状态栏无法下拉
                if (!isControlStatusBarEnable){
                    btn_status.setText("取消状态栏控制");
                    isControlStatusBarEnable = true;
                }else {
                    btn_status.setText("控制状态栏无法下拉");
                    isControlStatusBarEnable = false;
                }
                break;
            case R.id.btn_activate://跳至设备管理器激活此App
                openDeviceManager(1);
                break;
            case R.id.btn_reset:
                isControl = false;
                timeService.stopSelf();
                break;
        }
    }




    /**
     * 跳至设备管理器激活此App，一般情况无法直接卸载App，除非进入设备管理器取消激活此App
     */
    private void openDeviceManager(int requestCode){
        //DeviceManagerReciver.class  在manifest里面注册
        ComponentName componentName = new ComponentName(this, DeviceManagerReciver.class);
        //添加一个隐式意图，完成设备权限的添加
        //这个Intent (DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)跳转到 权限提醒页面
        //并传递了两个参数EXTRA_DEVICE_ADMIN 、 EXTRA_ADD_EXPLANATION

        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        //权限列表
        //EXTRA_DEVICE_ADMIN参数中说明了用到哪些权限，
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
        //描述(additional explanation)
        //EXTRA_ADD_EXPLANATION参数为附加的说明
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "您可千万别激活我啊~~~~");
        startActivityForResult(intent, requestCode);
    }

    /**
     *通过反射拿到获取收起状态栏的方法并执行
     */
    public void disableStatusBar() {
        try {
            Object service = getSystemService("statusbar");
            //通过反射获取StatusBarManager
            Class<?> claz = Class.forName("android.app.StatusBarManager");
            //获取StatusBarManager类中的collapse()方法
            Method expand = claz.getMethod("collapse");
            expand.invoke(service);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 指的是这个Activity得到或者失去焦点的时候 就会call
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (!hasFocus && isControlStatusBarEnable) {
            //收起状态栏
            disableStatusBar();
        }
    }


    /**
     * 获取手机里面所有的App
     */
    private void getAllApp() {
        PackageManager manager = getApplicationContext().getPackageManager();
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        //获取并添加系统所有的app
        List<ResolveInfo> list = manager.queryIntentActivities(intent, 0);

        for (int i = 0; i < list.size(); i++) {
            AppEntity entity = new AppEntity(Parcel.obtain());
            ActivityInfo info = list.get(i).activityInfo;
            Drawable drawable = info.loadIcon(manager);
            String name = info.loadLabel(manager).toString();
            String packageName = info.packageName;
            entity.appName = name;
            entity.packageName = packageName;
            entity.drawable = drawable;
            entities.add(entity);
        }
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if ("com.walle.control".equals(entities.get(position).packageName)) {
            Toast.makeText(this, "不能重复打开此应用",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (isControl && clickPosition != position) {
            Toast.makeText(this, "您只能点击   " + clickPosition + entities.get(clickPosition).appName + "   并且使用此应用", Toast.LENGTH_SHORT).show();
            return;
        }

        clickPosition = position;
        try {
            if (isControl && clickPosition == position) {
                Intent intent = getPackageManager()
                        .getLaunchIntentForPackage(entities.get(position).packageName);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        } catch (NullPointerException e) {
            Toast.makeText(this, "程序未安装",
                    Toast.LENGTH_SHORT).show();
        }
        if (!isControl) {
            timeService = new TimeService();
            Intent intent = new Intent(this, TimeService.class);
            intent.putParcelableArrayListExtra("app", (ArrayList<? extends Parcelable>) entities);
            startService(intent);
        }
        isControl = true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1&&resultCode ==RESULT_OK){
            //在此处做你想做的操作
            isActivate = true;
            Toast.makeText(this,"您已成功激活此App,并且无法卸载此App，手机密码为：1234,请牢记密码为1234，",Toast.LENGTH_LONG).show();
            Toast.makeText(this,"重要的事情说三遍，请牢记密码为1234",Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            return true ;
        }
        return true;
    }
}
