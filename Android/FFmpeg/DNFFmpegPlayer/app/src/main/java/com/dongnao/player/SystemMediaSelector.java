package com.dongnao.player;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;

/**
 * 通过系统相册或者系统相机获取照片
 *
 * @author Ztiany
 * Email: 1169654504@qq.com
 * Date : 2017-08-09 10:50
 */
public class SystemMediaSelector {

    private static final String TAG = SystemMediaSelector.class.getSimpleName();

    private static final int REQUEST_FILE = 199;

    private final SystemMediaSelectorResultListener mMediaSelectorCallback;

    private Activity mActivity;
    private Fragment mFragment;

    public SystemMediaSelector(SystemMediaSelectorResultListener mediaSelectorCallback, @NonNull Activity activity) {
        mMediaSelectorCallback = mediaSelectorCallback;
        mActivity = activity;
    }

    public SystemMediaSelector(@NonNull Fragment fragment, @NonNull SystemMediaSelectorResultListener mediaSelectorCallback) {
        mFragment = fragment;
        mMediaSelectorCallback = mediaSelectorCallback;
    }

    private Context getContext() {
        if (mFragment != null) {
            return mFragment.getContext();
        } else {
            return mActivity;
        }
    }

    private void startActivityForResult(Intent intent, int code) {
        if (mFragment != null) {
            mFragment.startActivityForResult(intent, code);
        } else {
            mActivity.startActivityForResult(intent, code);
        }
    }

    public boolean takeFile() {
        return takeFile(null);
    }

    public boolean takeFile(String mimeType) {
        Intent intent = Utils.makeFilesIntent(mimeType);
        try {
            startActivityForResult(intent, REQUEST_FILE);
        } catch (Exception e) {
            Log.e(TAG, "takeFile error", e);
            return false;
        }
        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            mMediaSelectorCallback.onCancel();
            return;
        }

        if (requestCode == REQUEST_FILE) {
            processFileResult(resultCode, data);
        }
    }

    private void processFileResult(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri == null) {
                mMediaSelectorCallback.onTakeFail();
            } else {
                returnUriResultChecked(uri);
            }
        } else {
            mMediaSelectorCallback.onTakeFail();
        }
    }

    private void returnUriResultChecked(Uri uri) {
        String absolutePath = Utils.getAbsolutePath(getContext(), uri);
        if (TextUtils.isEmpty(absolutePath)) {
            mMediaSelectorCallback.onTakeFail();
        } else {
            mMediaSelectorCallback.onTakeSuccess(absolutePath);
        }
    }

}