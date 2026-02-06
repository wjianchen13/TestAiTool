package com.example.testaitool;

import android.app.Application;
import android.util.Log;

import com.example.testaitool.init.AppInitManager;
import com.example.testaitool.init.InitCallback;

/**
 * 自定义Application类
 * 在应用启动时进行初始化配置
 */
public class MyApplication extends Application {

    private static final String TAG = "MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();

        // 使用Builder模式创建初始化管理器并执行初始化
        // 初始化完成后AppInitManager会被自动回收，不占用内存
        new AppInitManager.Builder()
                .addCallback(new SdkInitCallback())
                // 可以设置是否等待子线程初始化完成，默认不等待
                // .setWaitForBackgroundInit(true)
                // 可以设置子线程初始化超时时间，默认10秒
                // .setBackgroundInitTimeout(5000)
                .build()
                .init(this);
    }

    /**
     * SDK初始化回调示例
     * 实际使用时可以根据需要创建多个不同的InitCallback实现类
     */
    private static class SdkInitCallback implements InitCallback {

        @Override
        public void onMainThreadInit(Application application) {
            Log.d(TAG, "onMainThreadInit: 主线程初始化开始，线程: " + Thread.currentThread().getName());

            // TODO: 在这里添加必须在主线程初始化的SDK
            // 例如：UI相关的初始化、某些要求在主线程初始化的第三方SDK

            Log.d(TAG, "onMainThreadInit: 主线程初始化完成");
        }

        @Override
        public void onBackgroundThreadInit(Application application) {
            Log.d(TAG, "onBackgroundThreadInit: 子线程初始化开始，线程: " + Thread.currentThread().getName());

            // TODO: 在这里添加可以在子线程初始化的SDK
            // 例如：数据统计SDK、推送SDK、网络库、数据库、图片加载库等

            Log.d(TAG, "onBackgroundThreadInit: 子线程初始化完成");
        }
    }
}
