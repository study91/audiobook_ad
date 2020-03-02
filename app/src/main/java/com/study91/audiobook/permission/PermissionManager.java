package com.study91.audiobook.permission;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.study91.audiobook.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 权限管理器
 */
public class PermissionManager implements IPermissionManager {
    private int requestCode = 100;

    //权限数组变量 Manifest.permission.READ_PHONE_STATE,
    private String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION};

    private static PermissionManager permissionMaganger; //权限管理器变量

    /**
     * 获取权限管理器
     * @return 权限管理器
     */
    public static PermissionManager getInstance() {
        if (permissionMaganger == null) {
            permissionMaganger = new PermissionManager();
        }

        return permissionMaganger;
    }

    @Override
    public boolean requestPermissions(Activity activity) {
        boolean result = false;

        //判断手机版本是否23以下，如果是，不需要使用动态权限
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            result = true;
        } else {
            result = requestNeedPermission(activity, permissions, requestCode); //请求需要的权限
        }

        return result;
    }

    @Override
    public boolean guideRequestPermissions(Activity activity, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean isAllGranted = true;

        for (int grant : grantResults) {
            if (grant != PackageManager.PERMISSION_GRANTED) {
                isAllGranted = false;
                break;
            }
        }

        if (isAllGranted) {
            return true;
        } else {
            //获取未授权限并添加到集合中
            List<String> deniedPermissionList = new ArrayList<>(); //声明未授权限集合变量

            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    deniedPermissionList.add(permissions[i]);
                }
            }

            if (deniedPermissionList.size() == 0) {
                //已全部授权
                return true;
            } else {
                //引导用户去授权
                setPermissions(activity, deniedPermissionList);
            }

            return false;
        }
    }

    /**
     * 请求需要的权限
     * @param activity 上下文
     * @param permissions 权限集合
     * @param resultCode 请求码
     * @return 没有未充许的权限，返回true，否则返回false
     */
    private boolean requestNeedPermission(Activity activity, String[] permissions, int resultCode) {
        List<String> permissionList = checkPermissionDenied(activity, permissions); //获取未充许的权限集合

        if (permissionList.size() == 0) {
            return true;
        }

        //请求权限
        String[] deniedPermissions = permissionList.toArray(new String[permissionList.size()]);
        ActivityCompat.requestPermissions(activity, deniedPermissions, resultCode);

        return false;
    }

    /**
     * 检查未允许的权限集合
     * @param context 上下文
     * @param permissions 权限集合
     * @return 未允许的权限集合
     */
    private List<String> checkPermissionDenied(Context context, String[] permissions) {
        List<String> permissionList = new ArrayList<>();

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission);
            }
        }

        return permissionList;
    }

    /**
     * 设置权限（引导用户设置方式）
     * @param activity 上下文
     * @param permissionList 权限列表
     */
    private void setPermissions(final Activity activity, List<String> permissionList) {
        //提示消息
        String message = activity.getString(R.string.permission_dialog_message);
        String permissionNames = getPermissionNames(permissionList);
        message = message.replace("[PERMISSION_NAMES]", permissionNames);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message);
        builder.setPositiveButton(activity.getString(R.string.permission_dialog_positive_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + activity.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                activity.startActivity(intent);
            }
        });

        builder.setNegativeButton(activity.getString(R.string.permission_dialog_negative_button), null);
        builder.show();
    }

    /**
     * 获取所有权限及对应中文名的哈希表
     */
    public HashMap<String, String> getPermissionHashMap() {
        HashMap<String, String> permissionHashMap;
        permissionHashMap = new HashMap<>();

        //联系人/通讯录权限
        permissionHashMap.put("android.permission.WRITE_CONTACTS","通讯录/联系人");
        permissionHashMap.put("android.permission.GET_ACCOUNTS","通讯录/联系人");
        permissionHashMap.put("android.permission.READ_CONTACTS","通讯录/联系人");
        //电话权限
        permissionHashMap.put("android.permission.READ_CALL_LOG","电话");
        permissionHashMap.put("android.permission.READ_PHONE_STATE","电话");
        permissionHashMap.put("android.permission.CALL_PHONE","电话");
        permissionHashMap.put("android.permission.WRITE_CALL_LOG","电话");
        permissionHashMap.put("android.permission.USE_SIP","电话");
        permissionHashMap.put("android.permission.PROCESS_OUTGOING_CALLS","电话");
        permissionHashMap.put("com.android.voicemail.permission.ADD_VOICEMAIL","电话");
        //日历权限
        permissionHashMap.put("android.permission.READ_CALENDAR","日历");
        permissionHashMap.put("android.permission.WRITE_CALENDAR","日历");
        //相机拍照权限
        permissionHashMap.put("android.permission.CAMERA","相机/拍照");
        //传感器权限
        permissionHashMap.put("android.permission.BODY_SENSORS","传感器");
        //定位权限
        permissionHashMap.put("android.permission.ACCESS_FINE_LOCATION","位置");
        permissionHashMap.put("android.permission.ACCESS_COARSE_LOCATION","位置");
        //文件存取
        permissionHashMap.put("android.permission.READ_EXTERNAL_STORAGE","存储");
        permissionHashMap.put("android.permission.WRITE_EXTERNAL_STORAGE","存储");
        //音视频、录音权限
        permissionHashMap.put("android.permission.RECORD_AUDIO","音视频/录音");
        //短信权限
        permissionHashMap.put("android.permission.READ_SMS","短信");
        permissionHashMap.put("android.permission.RECEIVE_WAP_PUSH","短信");
        permissionHashMap.put("android.permission.RECEIVE_MMS","短信");
        permissionHashMap.put("android.permission.RECEIVE_SMS","短信");
        permissionHashMap.put("android.permission.SEND_SMS","短信");
        permissionHashMap.put("android.permission.READ_CELL_BROADCASTS","短信");

        return permissionHashMap;
    }

    /**
     * 获得权限名称（去除重复名称，以换行符分隔）
     * @param permissionList 权限数组列表
     * @return 权限名称
     */
    public String getPermissionNames(List<String> permissionList){
        if(permissionList == null || permissionList.size() == 0){
            return "\n";
        }

        StringBuilder permissionNames = new StringBuilder();
        List<String> permissionNameList = new ArrayList<>();
        HashMap<String,String> permissionHashMap = getPermissionHashMap();

        for(int i = 0; i < permissionList.size(); i++){
            String permissionName = permissionHashMap.get(permissionList.get(i));

            if(permissionName != null && !permissionNameList.contains(permissionName)){
                permissionNameList.add(permissionName);
                permissionNames.append(permissionName);
                permissionNames.append("\n");
            }
        }

        return permissionNames.toString();
    }
}
