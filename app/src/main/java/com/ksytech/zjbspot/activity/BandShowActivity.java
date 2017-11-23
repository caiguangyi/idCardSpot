package com.ksytech.zjbspot.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ksytech.zjbspot.R;
import com.ksytech.zjbspot.util.DisplayUtil;

import java.io.File;
import java.io.FileInputStream;

public class BandShowActivity extends Activity implements View.OnClickListener {

    private EditText et_bank_name, et_card_type, et_card_number;
    private RelativeLayout rl_bk_back;
    private ImageView iv_mg;
    private Context context;
    private SharedPreferences sp;
    private Activity activity;
    private ImageView iv_bank_sure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_band);

        context = this;
        activity = this;
        sp = PreferenceManager.getDefaultSharedPreferences(context);

        initView();

        Bundle extras = getIntent().getBundleExtra("bundle");

        if (extras != null) {
            String path = extras.getString("img_path");
            String type = extras.getString("type");
            String bankCardNumber = extras.getString("bankCardNumber");
            String bankName = extras.getString("bankName");

            if (!TextUtils.isEmpty(path)) {
                try {

                    File file = new File(path);
                    FileInputStream inStream = null;

                    inStream = new FileInputStream(file);
                    Bitmap bitmap = BitmapFactory.decodeStream(inStream);

                    iv_mg.setImageBitmap(bitmap);
                    inStream.close();

                    sp.edit().putString("bank_front", path).commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            et_card_type.setText(type);
            et_bank_name.setText(bankName);
            et_card_number.setText(bankCardNumber);
            DisplayUtil.showSoftInputFromWindow(activity, et_bank_name);
        }
    }

    private void initView() {
        iv_bank_sure = (ImageView) findViewById(R.id.iv_bank_sure);
        iv_mg = (ImageView) findViewById(R.id.iv_mg);
        et_bank_name = (EditText) findViewById(R.id.et_bank_name);
        et_card_type = (EditText) findViewById(R.id.et_card_type);
        et_card_number = (EditText) findViewById(R.id.et_card_number);
        rl_bk_back = (RelativeLayout) findViewById(R.id.rl_bk_back);
        rl_bk_back.setOnClickListener(this);
        iv_bank_sure.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_bk_back:
                finish();
                break;
            case R.id.iv_bank_sure:
                Toast.makeText(context, "信息保存成功!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent("com.from.call.back.bank.front");
                intent.putExtra("bank_name", et_bank_name.getText().toString());
                intent.putExtra("card_type", et_card_type.getText().toString());
                intent.putExtra("card_number", et_card_number.getText().toString());
                sendBroadcast(intent);
                break;
            default:
                break;
        }
    }
}
