package com.maniu.hookams;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class HookAms {
    Context context;

    public void hookAms(Context context) throws Exception {
        this.context = context;

        Field gDefault = null;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            Class<?> ActivityManagerNativecls = Class.forName("android.app.ActivityManagerNative");
            gDefault = ActivityManagerNativecls.getDeclaredField("gDefault");
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            Class<?> activityManager = Class.forName("android.app.ActivityManager");
            gDefault = activityManager.getDeclaredField("IActivityManagerSingleton");
        } else {

        }

        gDefault.setAccessible(true);
        Object defaultValue = gDefault.get(null);
        Class<?> SingletonClass = Class.forName("android.util.Singleton");
        Field mInstance = SingletonClass.getDeclaredField("mInstance");
        mInstance.setAccessible(true);
        Object iActivityManagerObject = mInstance.get(defaultValue);
        iActivityManagerObject.hashCode();
        Class<?> IActivityManagerIntercept = Class.forName("android.app.IActivityManager");
        Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{IActivityManagerIntercept}, new AmsInvocationHandler(iActivityManagerObject));
        mInstance.set(defaultValue, proxy);
    }

        class AmsInvocationHandler implements InvocationHandler {

            private Object iActivityManagerObject;

            public AmsInvocationHandler(Object iActivityManagerObject) {
                this.iActivityManagerObject = iActivityManagerObject;
            }

            @Override
            public Object invoke(Object o, Method method, Object[] args) throws Throwable {
                Log.i("david","----------------ams------------   "+method.getName());

                if ("startActivity".equals(method.getName())) {

                    Intent intent = null;
                    int index = 0;
                    for (int i = 0; i < args.length; i++) {
                        Object arg = args[i];
                        if ( arg instanceof Intent){
                            intent = (Intent) args[i]; // 原意图，过不了安检
                            index = i;
                        }
                    }
                    
                    SharedPreferences share = context.getSharedPreferences("david", Context.MODE_PRIVATE);
                    ComponentName componentName;

                    if (!share.getBoolean("login",false))  {
                        String jumpActivity = intent.getComponent().getClassName();

                        if (!"com.maniu.hookams.SceondActivity".equals(jumpActivity)) {
                            intent.putExtra("extraIntent", jumpActivity);
                            componentName = new ComponentName(context, LoginActivity.class);
                            intent.setComponent(componentName);
                        }

                    }
                }
                return method.invoke(iActivityManagerObject,args);
            }
        }

    class HookHandler implements InvocationHandler {

        private Object iActivityManagerObject;

        public HookHandler(Object iActivityManagerObject) {
            this.iActivityManagerObject = iActivityManagerObject;
        }

        @Override
        public Object invoke(Object o, Method method, Object[] args) throws Throwable {
            Log.i("david","----------------pms------------   "+method.getName());
            return method.invoke(iActivityManagerObject,args);
        }
    }

    public void hookPMS(Context context) {
        try {
            // 获取全局的ActivityThread对象
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            Method currentActivityThreadMethod = activityThreadClass.getDeclaredMethod("currentActivityThread");
            Object currentActivityThread = currentActivityThreadMethod.invoke(null);//得到ActivityThread对象

            // 获取ActivityThread里面原始的 sPackageManager
            Field sPackageManagerField = activityThreadClass.getDeclaredField("sPackageManager");

            sPackageManagerField.setAccessible(true);
            Object sPackageManager = sPackageManagerField.get(currentActivityThread);

            // 准备好代理对象, 用来替换原始的对象
            Class<?> iPackageManagerInterface = Class.forName("android.content.pm.IPackageManager");
            Object proxy = Proxy.newProxyInstance(iPackageManagerInterface.getClassLoader(),
                    new Class<?>[]{iPackageManagerInterface},
                    new HookHandler(sPackageManager));

            // 1. 替换掉ActivityThread里面的 sPackageManager 字段
            sPackageManagerField.set(currentActivityThread, proxy);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
