package com.walle.control;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/2/27.
 */

public class TimeService extends Service {

    private Timer timer;
    private AppTimerTask appTimerTask;
    private List<AppEntity> entities;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        timer = new Timer();

        entities = intent.getParcelableArrayListExtra("app");
        appTimerTask = new AppTimerTask();
        timer.schedule(appTimerTask, 0, 2000);
        return super.onStartCommand(intent, flags, startId);
    }


    class AppTimerTask extends TimerTask {

        @Override
        public void run() {
            if (MainActivity.isControl) {
                if (!isIncludedApp()) {
                    //如果不是在此应用或者指定的App，就立刻跳入此App
                    Intent intent = new Intent(getApplicationContext(),
                            MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }else {
                if (timer!=null)timer.cancel();
                if (appTimerTask!=null) appTimerTask.cancel();
            }
        }
    }

    /**
     * 判断当前应用是否在制定应用范围内
     *
     * @return
     */
    private boolean isIncludedApp() {
        if (isHome()) {
            return false;
        }
        try {
            StringBuffer sd = new StringBuffer("");
            sd.append("com.walle.control");
            for (int i = 0; i < entities.size(); i++) {
                AppEntity conAppEntity = entities.get(i);
                sd.append(conAppEntity.packageName);
            }
            String sdString = sd.toString();
            ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);
//			ToastUtil.showShortToast(getApplicationContext(), rti.get(0).topActivity.getPackageName());
            return sdString.contains(rti.get(0).topActivity.getPackageName());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断当前界面是否是桌面
     *
     * @return
     */
    private boolean isHome() {
        ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);
        return getHomes().contains(rti.get(0).topActivity.getPackageName());

    }

    /**
     * 获取属于桌面的应用的应用包名称
     *
     * @return 返回包含所有包名的字符串列表
     */
    private List<String> getHomes() {
        List<String> names = new ArrayList<String>();
        PackageManager packageManager = this.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);

        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(
                intent, packageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo ri : resolveInfos) {
            names.add(ri.activityInfo.packageName);
        }
        return names;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer!=null)timer.cancel();
        if (appTimerTask!=null) appTimerTask.cancel();
    }
}
