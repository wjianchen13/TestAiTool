package com.example.testaitool.init;

import android.app.Application;

/**
 * 初始化回调接口
 * 用于定义应用启动时需要执行的初始化操作
 */
public interface InitCallback {

    /**
     * 在主线程执行的初始化操作
     * 适用于必须在主线程初始化的SDK，如：UI相关、某些要求主线程初始化的第三方SDK
     *
     * @param application Application实例
     */
    void onMainThreadInit(Application application);

    /**
     * 在子线程执行的初始化操作
     * 适用于可以异步初始化的SDK，如：统计SDK、推送SDK、网络库等
     * 注意：此方法在子线程执行，不要在此进行UI操作
     *
     * @param application Application实例
     */
    void onBackgroundThreadInit(Application application);
}
