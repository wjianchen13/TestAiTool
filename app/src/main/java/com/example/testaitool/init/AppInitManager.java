package com.example.testaitool.init;

import android.app.Application;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 应用初始化管理类
 * 负责管理应用启动时的初始化任务，支持主线程和子线程分别执行初始化
 * 使用Builder模式，初始化完成后自动释放资源，不会持有内存
 *
 * 使用方式：
 * new AppInitManager.Builder()
 *     .addCallback(callback1)
 *     .addCallback(callback2)
 *     .build()
 *     .init(application);
 */
public class AppInitManager {

    private static final String TAG = "AppInitManager";

    private final List<InitCallback> mCallbacks;
    private final long mBackgroundInitTimeout;
    private final boolean mWaitForBackgroundInit;

    private AppInitManager(Builder builder) {
        this.mCallbacks = builder.callbacks;
        this.mBackgroundInitTimeout = builder.backgroundInitTimeout;
        this.mWaitForBackgroundInit = builder.waitForBackgroundInit;
    }

    /**
     * 执行初始化
     * 主线程初始化会同步执行，子线程初始化会异步执行
     * 初始化完成后资源会自动释放
     *
     * @param application Application实例
     */
    public void init(Application application) {
        if (application == null) {
            throw new IllegalArgumentException("Application cannot be null");
        }

        if (mCallbacks.isEmpty()) {
            Log.w(TAG, "No callbacks registered");
            return;
        }

        long startTime = System.currentTimeMillis();
        Log.d(TAG, "Start initialization, callback count: " + mCallbacks.size());

        // 用于等待子线程初始化完成
        final CountDownLatch latch = mWaitForBackgroundInit ? new CountDownLatch(1) : null;

        // 创建临时线程池执行异步初始化
        ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
            Thread thread = new Thread(r, "AppInitThread");
            thread.setPriority(Thread.NORM_PRIORITY);
            return thread;
        });

        // 在子线程执行异步初始化
        executor.execute(() -> {
            long bgStartTime = System.currentTimeMillis();
            Log.d(TAG, "Background thread init started");

            for (InitCallback callback : mCallbacks) {
                try {
                    callback.onBackgroundThreadInit(application);
                } catch (Exception e) {
                    Log.e(TAG, "Error in background init: " + callback.getClass().getSimpleName(), e);
                }
            }

            long bgEndTime = System.currentTimeMillis();
            Log.d(TAG, "Background thread init completed, cost: " + (bgEndTime - bgStartTime) + "ms");

            if (latch != null) {
                latch.countDown();
            }
        });

        // 子线程任务提交后立即关闭线程池（已提交的任务会继续执行）
        executor.shutdown();

        // 在主线程执行同步初始化
        long mainStartTime = System.currentTimeMillis();
        Log.d(TAG, "Main thread init started");

        for (InitCallback callback : mCallbacks) {
            try {
                callback.onMainThreadInit(application);
            } catch (Exception e) {
                Log.e(TAG, "Error in main thread init: " + callback.getClass().getSimpleName(), e);
            }
        }

        long mainEndTime = System.currentTimeMillis();
        Log.d(TAG, "Main thread init completed, cost: " + (mainEndTime - mainStartTime) + "ms");

        // 如果需要等待子线程初始化完成
        if (latch != null) {
            try {
                boolean completed = latch.await(mBackgroundInitTimeout, TimeUnit.MILLISECONDS);
                if (!completed) {
                    Log.w(TAG, "Background init timeout after " + mBackgroundInitTimeout + "ms");
                }
            } catch (InterruptedException e) {
                Log.e(TAG, "Background init interrupted", e);
                Thread.currentThread().interrupt();
            }
        }

        long endTime = System.currentTimeMillis();
        Log.d(TAG, "All initialization completed, total cost: " + (endTime - startTime) + "ms");
    }

    /**
     * Builder类用于构建AppInitManager
     */
    public static class Builder {

        private final List<InitCallback> callbacks = new ArrayList<>();
        private long backgroundInitTimeout = 10000;
        private boolean waitForBackgroundInit = false;

        /**
         * 添加初始化回调
         *
         * @param callback 初始化回调
         * @return Builder实例，支持链式调用
         */
        public Builder addCallback(InitCallback callback) {
            if (callback != null && !callbacks.contains(callback)) {
                callbacks.add(callback);
            }
            return this;
        }

        /**
         * 批量添加初始化回调
         *
         * @param callbacks 初始化回调列表
         * @return Builder实例，支持链式调用
         */
        public Builder addCallbacks(List<InitCallback> callbacks) {
            if (callbacks != null) {
                for (InitCallback callback : callbacks) {
                    addCallback(callback);
                }
            }
            return this;
        }

        /**
         * 设置子线程初始化超时时间
         *
         * @param timeoutMillis 超时时间（毫秒），默认10000ms
         * @return Builder实例，支持链式调用
         */
        public Builder setBackgroundInitTimeout(long timeoutMillis) {
            this.backgroundInitTimeout = timeoutMillis;
            return this;
        }

        /**
         * 设置是否等待子线程初始化完成
         * 如果设置为true，init()方法会阻塞直到子线程初始化完成或超时
         *
         * @param wait 是否等待，默认false
         * @return Builder实例，支持链式调用
         */
        public Builder setWaitForBackgroundInit(boolean wait) {
            this.waitForBackgroundInit = wait;
            return this;
        }

        /**
         * 构建AppInitManager实例
         *
         * @return AppInitManager实例
         */
        public AppInitManager build() {
            return new AppInitManager(this);
        }
    }
}
