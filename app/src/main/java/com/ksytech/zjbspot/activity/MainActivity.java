package com.ksytech.zjbspot.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.ksytech.zjbspot.R;

import butterknife.ButterKnife;

public class MainActivity extends Activity {


    /* 相机请求码 */
    private static final int REQUEST_CAMERA = 0;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number);
        ButterKnife.bind(this);

        findViewById(R.id.btn_into).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, RealNameActivity.class));
            }
        });

        // 初始化
        getOcrSing();

        //6.0以下系统，取消请求权限
        Log.v("permission",Build.VERSION.SDK_INT + "");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        } else {
            requestPermission();
        }
    }

    private void getOcrSing() {
        OCR.getInstance().initAccessToken(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {
                // 调用成功，返回AccessToken对象
                String token = result.getAccessToken();
                Log.i("getOcrSing", "成功:" + "," + token);
            }

            @Override
            public void onError(OCRError error) {
                // 调用失败，返回OCRError子类SDKError对象
                Log.e("getOcrSing", "失败:" + error);
            }
        }, getApplicationContext());
    }

    //请求权限(相机和读写)
    private void requestPermission() {
        Log.i("permission", "检查权限是否被受理！");
        // 检查是否想要的权限申请是否弹框。如果是第一次申请，用户不通过，
        // 那么第二次申请的话，就要给用户说明为什么需要申请这个权限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // 权限未被授予
            requestCameraPermission();
        } else {
            Log.i("permission", "相机权限已经被受理，开始预览相机！");
//            showCameraPreview();
        }
    }

    /**
     * 申请相机权限
     */
    private void requestCameraPermission() {
        Log.i("permission", "相机权限未被授予，需要申请！");
        // 相机权限未被授予，需要申请！
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            // 如果访问了，但是没有被授予权限，则需要告诉用户，使用此权限的好处
            Log.i("permission", "申请权限说明！");
            // 这里重新申请权限
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA);
        } else {
            // 第一次申请，就直接申请
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i("permission", "相机权限已打开");

                myPermission();
            } else {
                Log.d("permission", "相机权限已被拒绝");
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void myPermission() {
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
