package com.dongnao.player;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * @author Ztiany
 * Email: ztiany3@gmail.com
 * Date : 2020-05-14 17:37
 */
public class DNPlayer {

    static {
        System.loadLibrary("native-lib");
    }

    private String mDataSource;

    private OnPrepareListener listener;

    private SurfaceHolder mHolder;

    private SurfaceHolder.Callback2 mCallback = new SurfaceHolder.Callback2() {

        @Override
        public void surfaceRedrawNeeded(SurfaceHolder holder) {
            LogUtil.d("surfaceRedrawNeeded");
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            LogUtil.d("surfaceCreated");
            nativeSetSurface(holder.getSurface());
        }

        /** 画布发送变化时会回调该函数，比如横竖屏切换、应用会到后台。*/
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            LogUtil.d("surfaceChanged");
            nativeSetSurface(holder.getSurface());
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            LogUtil.d("surfaceDestroyed");
        }
    };

    /**
     * 设置视频源
     */
    public void setDataSource(String dataSource) {
        mDataSource = dataSource;
    }

    /**
     * Android 中展示 RGB 数据的方式：
     *
     * <pre>
     *      1. 使用 ImageView，不断地将 RGB 数据转换为 Bitmap，然后不断地刷新，这种方式当然是最低效的。
     *      2. OpenGL（使用相对麻烦）。
     *      3. SurfaceView/TextureView。专门用于高刷新率的画面展示，支持子线程刷新。
     * </pre>
     * <p>
     * 用于承载视频的 视图
     */
    public void setSurfaceView(@NonNull SurfaceView surfaceView) {
        mHolder = surfaceView.getHolder();
        mHolder.addCallback(mCallback);
    }

    public void setOnPrepareListener(OnPrepareListener listener) {
        this.listener = listener;
    }

    /**
     * 准备好要播放的视频。
     */
    public void prepare() {
        /*准备视频，要获取视频的各种信息*/
        if (!TextUtils.isEmpty(mDataSource)) {
            nativePrepare(mDataSource);
        }
    }

    public void start() {
        nativeStart();
    }

    public void stop() {

    }

    public void destroy() {
        nativeDestroy();
        mHolder.removeCallback(mCallback);
    }

    /**
     * 提供给 native 调用，用于提示底层发生错误
     */
    public void onNativeError(int errorCode) {
        LogUtil.d("DNPlayer error: " + errorCode);
    }

    /**
     * 提供给 native 调用，用于提示底层准备好了
     */
    public void onNativePrepared() {
        LogUtil.d("DNPlayer onPrepared");
        if (null != listener) {
            listener.onPrepare();
        }
    }

    private native void nativePrepare(String dataSource);

    private native void nativeDestroy();

    private native void nativeStart();

    private native void nativeSetSurface(Surface surface);

}
