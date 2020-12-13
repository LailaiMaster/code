package me.ztiany.launcherdadge;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import static android.content.Context.NOTIFICATION_SERVICE;

public class BadgeUtil {

    private static final String TAG = "BadgeUtil";

    /**
     * 设置Badge 目前支持Launcher
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void setBadgeCount(Context context, int count, int iconResId) {
        if (count <= 0) {
            count = 0;
        } else {
            count = Math.max(0, Math.min(count, 99));
        }

        Log.d(TAG, "brand = " + Build.MANUFACTURER + " count = " + count);

        if (Build.MANUFACTURER.equalsIgnoreCase("xiaomi")) {
            setBadgeOfMIUI(context, count, iconResId);
        } else if (Build.MANUFACTURER.equalsIgnoreCase("sony")) {
            setBadgeOfSony(context, count);
        } else if (Build.MANUFACTURER.toLowerCase().contains("samsung") ||
                Build.MANUFACTURER.toLowerCase().contains("lg")) {
            setBadgeOfSumsung(context, count);
        } else if (Build.MANUFACTURER.toLowerCase().contains("htc")) {
            setBadgeOfHTC(context, count);
        } else if (Build.MANUFACTURER.toLowerCase().contains("nova")) {
            setBadgeOfNova(context, count);
        } else if (Build.MANUFACTURER.toLowerCase().contains("oppo")) {//oppo
            setBadgeOfOPPO(context, count);
        } else if (Build.MANUFACTURER.toLowerCase().contains("LeMobile")) {//乐视

        } else if (Build.MANUFACTURER.toLowerCase().contains("vivo")) {
            setBadgeOfVIVO(context, count);
        } else if (Build.MANUFACTURER.toLowerCase().contains("HUAWEI") || Build.BRAND.equals("Huawei") || Build.BRAND.equals("HONOR")) {//华为
            setHuaweiBadge(context, count);
        } else if (Build.MANUFACTURER.toLowerCase().contains("")) {//魅族

        } else if (Build.MANUFACTURER.toLowerCase().contains("")) {//金立

        } else if (Build.MANUFACTURER.toLowerCase().contains("")) {//锤子

        } else {
            setBadgeOfMIUI(context, count, iconResId);
        }
    }

    /**
     * 设置MIUI的Badge
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void setBadgeOfMIUI(Context context, int count, int iconResId) {
        //小米的需要发一个通知，进入应用后红点会自动消失，测试的时候进程只能在后台，否则没有效果
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        String title = "有消息接收";
        String channelId = "default";
        String channelName = "默认通知";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH));
        }
        Intent intent = new Intent();//可以用intent设置点击通知后的页面
        Notification notification = new NotificationCompat.Builder(context, "default")
                .setContentIntent(PendingIntent.getActivity(context, 0, intent, 0))
                .setContentTitle(title)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .build();

        try {
            Field field = notification.getClass().getDeclaredField("extraNotification");
            Object extraNotification = field.get(notification);
            Method method = extraNotification.getClass().getDeclaredMethod("setMessageCount", int.class);
            method.invoke(extraNotification, count);
            notificationManager.notify(66, notification);
        } catch (Exception e) {
            Log.e(TAG, "小米桌面图标：" + e.getMessage());
        }

    }

    /**
     * 设置索尼的Badge
     * 需添加权限：<uses-permission android:name="com.sonyericsson.home.permission.BROADCAST_BADGE" />
     */
    private static void setBadgeOfSony(Context context, int count) {
        String launcherClassName = getLauncherClassName(context);
        if (launcherClassName == null) {
            return;
        }
        boolean isShow = true;
        if (count == 0) {
            isShow = false;
        }
        Intent localIntent = new Intent();
        localIntent.setAction("com.sonyericsson.home.action.UPDATE_BADGE");
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE", isShow);//是否显示
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.ACTIVITY_NAME", launcherClassName);//启动页
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.MESSAGE", String.valueOf(count));//数字
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.PACKAGE_NAME", context.getPackageName());//包名
        context.sendBroadcast(localIntent);
    }

    /**
     * 设置三星的Badge，设置LG的Badge。
     */
    private static void setBadgeOfSumsung(Context context, int count) {
        // 获取你当前的应用
        String launcherClassName = getLauncherClassName(context);
        if (launcherClassName == null) {
            return;
        }
        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        intent.putExtra("badge_count", count);
        intent.putExtra("badge_count_package_name", context.getPackageName());
        intent.putExtra("badge_count_class_name", launcherClassName);
        context.sendBroadcast(intent);
    }

    /**
     * 设置HTC的Badge
     */
    private static void setBadgeOfHTC(Context context, int count) {
        Intent intentNotification = new Intent("com.htc.launcher.action.SET_NOTIFICATION");
        ComponentName localComponentName = new ComponentName(context.getPackageName(), getLauncherClassName(context));
        intentNotification.putExtra("com.htc.launcher.extra.COMPONENT", localComponentName.flattenToShortString());
        intentNotification.putExtra("com.htc.launcher.extra.COUNT", count);
        context.sendBroadcast(intentNotification);

        Intent intentShortcut = new Intent("com.htc.launcher.action.UPDATE_SHORTCUT");
        intentShortcut.putExtra("packagename", context.getPackageName());
        intentShortcut.putExtra("count", count);
        context.sendBroadcast(intentShortcut);
    }

    /**
     * 设置Nova的Badge
     */
    private static void setBadgeOfNova(Context context, int count) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("tag", context.getPackageName() + "/" + getLauncherClassName(context));
        contentValues.put("count", count);
        context.getContentResolver().insert(Uri.parse("content://com.teslacoilsw.notifier/unread_count"), contentValues);
    }

    /**
     * 设置vivo的Badge :vivoXplay5 vivo x7无效果
     */
    private static void setBadgeOfVIVO(Context context, int count) {
        try {
            Intent intent = new Intent("launcher.action.CHANGE_APPLICATION_NOTIFICATION_NUM");
            intent.putExtra("packageName", context.getPackageName());
            String launchClassName = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName()).getComponent().getClassName();
            intent.putExtra("className", launchClassName);
            intent.putExtra("notificationNum", count);
            context.sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置oppo的Badge :oppo角标提醒目前只针对内部软件还有微信、QQ开放，其他的暂时无法提供
     */
    private static void setBadgeOfOPPO(Context context, int count) {
        try {
            if (count == 0) {
                count = -1;
            }
            Intent intent = new Intent("com.oppo.unsettledevent");
            intent.putExtra("pakeageName", context.getPackageName());
            intent.putExtra("number", count);
            intent.putExtra("upgradeNumber", count);
            if (canResolveBroadcast(context, intent)) {
                context.sendBroadcast(intent);
            } else {
                try {
                    Bundle extras = new Bundle();
                    extras.putInt("app_badge_count", count);
                    context.getContentResolver().call(Uri.parse("content://com.android.badge/badge"), "setAppBadgeCount", null, extras);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean canResolveBroadcast(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> receivers = packageManager.queryBroadcastReceivers(intent, 0);
        return receivers != null && receivers.size() > 0;
    }

    /**
     * 设置华为的Badge :mate8 和华为 p7,honor畅玩系列可以,honor6plus 无效果
     */
    public static void setHuaweiBadge(Context context, int count) {
        try {
            Bundle bundle = new Bundle();
            bundle.putString("package", context.getPackageName());
            String launchClassName = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName()).getComponent().getClassName();
            bundle.putString("class", launchClassName);
            bundle.putInt("badgenumber", count);
            context.getContentResolver().call(Uri.parse("content://com.huawei.android.launcher.settings/badge/"), "change_badge", null, bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setBadgeOfMadMode(Context context, int count, String packageName, String className) {
        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        intent.putExtra("badge_count", count);
        intent.putExtra("badge_count_package_name", packageName);
        intent.putExtra("badge_count_class_name", className);
        context.sendBroadcast(intent);
    }

    /**
     * 重置Badge
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void resetBadgeCount(Context context, int iconResId) {
        setBadgeCount(context, 0, iconResId);
    }

    public static String getLauncherClassName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setPackage(context.getPackageName());
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        ResolveInfo info = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (info == null) {
            info = packageManager.resolveActivity(intent, 0);
        }
        return info.activityInfo.name;
    }

}