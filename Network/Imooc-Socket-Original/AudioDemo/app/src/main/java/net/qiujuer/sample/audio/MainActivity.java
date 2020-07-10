package net.qiujuer.sample.audio;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AppContract.View {
    private EditText mInput;
    private Button mSubmitButton;
    private TextView mTipsView;
    private AppContract.Presenter mPresenter;
    private AlertDialog mDialog;
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化提示
        mTipsView = findViewById(R.id.txt_tips);
        mTipsView.setText(Html.fromHtml(getResources().getString(R.string.tips)));

        // 初始化点击按钮
        mSubmitButton = findViewById(R.id.btn_submit);
        mSubmitButton.setOnClickListener(this);

        // 初始化文字输入
        mInput = findViewById(R.id.input);
        mInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // 输入框变化时按钮跟随
                mSubmitButton.setText(s.length() == 0 ? R.string.btn_random : R.string.btn_link);
            }
        });

        // 初始化Presenter
        mPresenter = new Presenter(this);

        // 默认检查一次权限
        checkPermission();
    }

    @Override
    public void onClick(View v) {
        // 检查权限
        if (!checkPermission()) {
            Toast.makeText(this, R.string.toast_permission, Toast.LENGTH_SHORT).show();
            return;
        }

        if (mInput.isEnabled()) {
            String code = mInput.getText().toString().trim();
            if (TextUtils.isEmpty(code)) {
                mPresenter.createRoom();
            } else {
                mPresenter.joinRoom(code);
            }
        } else {
            mPresenter.leaveRoom();
        }
    }

    @Override
    public void showProgressDialog(int string) {
        mDialog = new AlertDialog.Builder(this)
                .setMessage(string)
                .setCancelable(false)
                .create();
        mDialog.show();
    }

    @Override
    public void dismissProgressDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    @Override
    public void showToast(int string) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, string, Toast.LENGTH_LONG);
        mToast.show();
    }

    @Override
    public void showRoomCode(String code) {
        mInput.setText(code);
    }

    @Override
    public void onOnline() {
        mInput.setEnabled(false);
        mSubmitButton.setText(R.string.btn_unlink);
    }

    @Override
    public void onOffline() {
        mInput.setEnabled(true);
        mInput.setText("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }

    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    /**
     * 检查权限
     */
    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.INTERNET},
                    MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
            return false;
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // TODO 授权后的逻辑
            }
        }
    }

}
