package com.study91.audiobook.permission;

import android.app.Activity;
import android.support.annotation.NonNull;

/**
 * 权限管理接口
 */
public interface IPermissionManager {
    /**
     * 请求权限（系统请求界面，不能进行修改）
     * @param activity Activity上下文
     * @return 如果权限已全部充许，返回true，否则返回false
     */
    boolean requestPermissions(Activity activity);

    /**
     * 引导用户请求权限（对于用户在系统界面中未授权的权限引导用户完成授权）
     * @param activity Activity上下文
     * @param permissions 权限集合
     * @param grantResults 权限请求结果数组
     * @return 处理权限结果如果全部通过，返回true；否则，引导用户去授权页面
     */
    boolean guideRequestPermissions(
            Activity activity,
            @NonNull String[] permissions,
            @NonNull int[] grantResults);
}
