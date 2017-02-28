package com.walle.control;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * 设备管理器
 */
public class DeviceManagerReciver extends DeviceAdminReceiver{



    /**
     * 取消激活的时候调用
     * @param context
     * @param intent
     * @return 取消激活时 弹框的显示文本
     */
    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        DevicePolicyManager manager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName name = new ComponentName(context, DeviceManagerReciver.class);
        //代表实际是否被激活
        boolean adminActive = manager.isAdminActive(name);
        if (adminActive){
            //立刻锁定手机
            manager.lockNow();
            //重置手机密码
            manager.resetPassword("1234",0);
        }else {
            // 指定动作名称
            intent.setAction(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            // 指定给哪个组件授权
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, name);
            context.startActivity(intent);
        }
        return "取消激活或影响程序的正常使用";
    }



    /**
     * 重置密码成功回调
     * @param context
     * @param intent
     */
    @Override
    public void onPasswordSucceeded(Context context, Intent intent) {
        super.onPasswordSucceeded(context, intent);
        Toast.makeText(context,"密码更改成功!新密码：1234",Toast.LENGTH_LONG).show();
    }

    /**
     * 重置密码失败回调
     * @param context
     * @param intent
     */
    @Override
    public void onPasswordFailed(Context context, Intent intent) {
        super.onPasswordFailed(context, intent);
        Toast.makeText(context,"密码更改失败",Toast.LENGTH_LONG).show();
    }


}
