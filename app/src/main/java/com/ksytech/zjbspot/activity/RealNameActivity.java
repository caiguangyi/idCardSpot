package com.ksytech.zjbspot.activity;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.ocr.sdk.model.IDCardParams;
import com.ksytech.zjbspot.R;

public class RealNameActivity extends Activity implements View.OnClickListener {

    private Context context;
    private ImageView iv_id_front, iv_id_back, iv_bank;
    private RelativeLayout rl_bk;
    /* 相机请求码 */
    private static final int REQUEST_CAMERA = 0;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static final int IDCARD_FRONT = 2;
    private static final int IDCARD_BACK = 3;
    private static final int BANKCARD_FRONT = 4;
    private SharedPreferences sp;

    private RelativeLayout rl_id_front_before, rl_id_front_after;
    private RelativeLayout rl_id_back_before, rl_id_back_after;
    private RelativeLayout rl_bank_before, rl_bank_after;

    private TextView tv_name, tv_birthday, tv_sex, tv_address, tv_nation, tv_id_number;
    private TextView tv_sign_orgin, tv_ex_data;
    private TextView tv_b_n, tv_c, tv_bank_number;


    private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent != null) {
                switch (intent.getAction()) {
                    case "com.from.call.back.id.card.front":
                        rl_id_front_before.setVisibility(View.GONE);
                        rl_id_front_after.setVisibility(View.VISIBLE);

                        String name = intent.getStringExtra("name");
                        String birthday = intent.getStringExtra("birthday");
                        String sex = intent.getStringExtra("sex");
                        String address = intent.getStringExtra("address");
                        String nation = intent.getStringExtra("nation");
                        String id_number = intent.getStringExtra("id_number");

                        tv_name.setText(name);
                        tv_birthday.setText(birthday);
                        tv_sex.setText(sex);
                        tv_address.setText(address);
                        tv_nation.setText(nation);
                        tv_id_number.setText(id_number);
                        break;
                    case "com.from.call.back.id.card.back":
                        rl_id_back_before.setVisibility(View.GONE);
                        rl_id_back_after.setVisibility(View.VISIBLE);

                        String sign_orgin = intent.getStringExtra("sign_orgin");
                        String expiration_date = intent.getStringExtra("expiration_date");

                        tv_sign_orgin.setText(sign_orgin);
                        tv_ex_data.setText(expiration_date);
                        break;
                    case "com.from.call.back.bank.front":
                        rl_bank_before.setVisibility(View.GONE);
                        rl_bank_after.setVisibility(View.VISIBLE);

                        String bank_name = intent.getStringExtra("bank_name");
                        String card_type = intent.getStringExtra("card_type");
                        String card_number = intent.getStringExtra("card_number");

                        tv_b_n.setText(bank_name);
                        tv_c.setText(card_type);
                        tv_bank_number.setText(card_number);
                        break;
                    default:
                        break;
                }
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_name);

        context = this;
        sp = PreferenceManager.getDefaultSharedPreferences(context);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.from.call.back.id.card.front");
        intentFilter.addAction("com.from.call.back.id.card.back");
        intentFilter.addAction("com.from.call.back.bank.front");
        registerReceiver(myBroadcastReceiver, intentFilter);

        initView();

        Log.d("permission", Build.VERSION.SDK_INT + "");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        } else {
            requestPermission();
        }
    }

    private void initView() {
        tv_b_n = (TextView) findViewById(R.id.tv_b_n);
        tv_c = (TextView) findViewById(R.id.tv_c);
        tv_bank_number = (TextView) findViewById(R.id.tv_bank_number);

        tv_sign_orgin = (TextView) findViewById(R.id.tv_sign_orgin);
        tv_ex_data = (TextView) findViewById(R.id.tv_ex_data);

        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_birthday = (TextView) findViewById(R.id.tv_birthday);
        tv_sex = (TextView) findViewById(R.id.tv_sex);
        tv_address = (TextView) findViewById(R.id.tv_address);
        tv_nation = (TextView) findViewById(R.id.tv_nation);
        tv_id_number = (TextView) findViewById(R.id.tv_id_number);

        rl_id_front_before = (RelativeLayout) findViewById(R.id.rl_id_front_before);
        rl_id_front_after = (RelativeLayout) findViewById(R.id.rl_id_front_after);
        rl_id_back_before = (RelativeLayout) findViewById(R.id.rl_id_back_before);
        rl_id_back_after = (RelativeLayout) findViewById(R.id.rl_id_back_after);
        rl_bank_before = (RelativeLayout) findViewById(R.id.rl_bank_before);
        rl_bank_after = (RelativeLayout) findViewById(R.id.rl_bank_after);

        rl_bk = (RelativeLayout) findViewById(R.id.rl_bk);
        iv_id_front = (ImageView) findViewById(R.id.iv_id_front);
        iv_id_back = (ImageView) findViewById(R.id.iv_id_back);
        iv_bank = (ImageView) findViewById(R.id.iv_bank);

        iv_id_front.setOnClickListener(this);
        iv_id_back.setOnClickListener(this);
        iv_bank.setOnClickListener(this);
        rl_bk.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_bk:
                finish();
                break;
            case R.id.iv_id_front:
                Log.d("permission", Build.VERSION.SDK_INT + "");

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    Intent intent = new Intent(RealNameActivity.this, CameraScanActivity.class);
                    intent.putExtra("id_card_side", IDCardParams.ID_CARD_SIDE_FRONT);
                    intent.putExtra("type", "idcardFront");
                    startActivityForResult(intent, IDCARD_FRONT);
                } else {
                    if (getAllPermission()) {
                        Intent intent = new Intent(RealNameActivity.this, CameraScanActivity.class);
                        intent.putExtra("id_card_side", IDCardParams.ID_CARD_SIDE_FRONT);
                        intent.putExtra("type", "idcardFront");
                        startActivityForResult(intent, IDCARD_FRONT);
                    } else {

                        Toast.makeText(context, "需要获取相机与存储权限!", Toast.LENGTH_SHORT).show();
                        requestPermission();
                    }
                }

                break;
            case R.id.iv_id_back:

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    Intent intent2 = new Intent(RealNameActivity.this, CameraScanActivity.class);
                    intent2.putExtra("id_card_side", IDCardParams.ID_CARD_SIDE_BACK);
                    intent2.putExtra("type", "idcardBack");
                    startActivityForResult(intent2, IDCARD_BACK);
                } else {
                    if (getAllPermission()) {
                        Intent intent2 = new Intent(RealNameActivity.this, CameraScanActivity.class);
                        intent2.putExtra("id_card_side", IDCardParams.ID_CARD_SIDE_BACK);
                        intent2.putExtra("type", "idcardBack");
                        startActivityForResult(intent2, IDCARD_BACK);
                    } else {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        } else {
                            requestPermission();
                        }
                    }
                }

                break;
            case R.id.iv_bank:
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    startActivityForResult(new Intent(RealNameActivity.this, BankScanActivity.class), BANKCARD_FRONT);
                } else {
                    if (getAllPermission()) {
                        startActivityForResult(new Intent(RealNameActivity.this, BankScanActivity.class), BANKCARD_FRONT);
                    } else {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        } else {
                            requestPermission();
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    //请求权限(相机和读写)
    private void requestPermission() {
        Log.i("permission", "检查权限是否被受理！");
        // 检查是否想要的权限申请是否弹框。如果是第一次申请，用户不通过，
        // 那么第二次申请的话，就要给用户说明为什么需要申请这个权限
        if (!getCameraPermission()) {
            // 权限未被授予
            requestCameraPermission();
        } else {
            Log.i("permission", "相机权限已经被受理，开始检查SD卡读写权限！");
            myPermission();
        }
    }

    /**
     * 申请相机权限
     */
    private void requestCameraPermission() {
        Log.i("permission", "相机权限未被授予，需要申请！");
        Log.v("permission", ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA) + "相机权限未被授予，需要申请！");
        // 相机权限未被授予，需要申请！
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            // 如果访问了，但是没有被授予权限，则需要告诉用户，使用此权限的好处
            Log.i("permission", "申请权限说明！");
            // 这里重新申请权限
            ActivityCompat.requestPermissions(RealNameActivity.this,
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
        } else if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i("permission", "读写权限已打开");
            } else {
                Log.i("permission", "读写权限已被拒绝");
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * 申请SD读写权限
     */
    public void myPermission() {
        if (!getSDWritePermission()) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        } else {
            Log.d("permission", "读写权限已获取,两个重要权限已获取!");
        }
    }

    private Boolean getAllPermission() {
        if (getCameraPermission() && getSDWritePermission()) {
            return true;
        }

        return false;
    }

    private Boolean getCameraPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        }

        return true;
    }

    private Boolean getSDWritePermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case IDCARD_FRONT:


                    break;
                case IDCARD_BACK:
                    break;
                case BANKCARD_FRONT:
                    break;
                default:
                    break;
            }
        }
    }
}
