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
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ksytech.zjbspot.R;
import com.ksytech.zjbspot.util.DisplayUtil;
import com.ksytech.zjbspot.util.UrlUtils;

import java.io.File;
import java.io.FileInputStream;

public class PositiveActivity extends Activity implements View.OnClickListener {

    private EditText et_name, et_sex, et_nation, et_address, et_citizenship_number;
    private EditText et_year, et_month, et_day;
    private ImageView iv_mg;
    private RelativeLayout rl_back_front;
    private SharedPreferences sp;
    private Context context;
    private Activity activity;
    private ImageView iv_id_f_sure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_positive);

        context = this;
        activity = this;
        sp = PreferenceManager.getDefaultSharedPreferences(context);

        initView();

        Bundle bundle = getIntent().getBundleExtra("bundle");

        if (bundle != null) {
            String gender = bundle.getString("gender");
            String name = bundle.getString("name");
            String id_card_number = bundle.getString("id_card_number");
            String birthday = bundle.getString("birthday");
            String race = bundle.getString("race");
            String address = bundle.getString("address");
            String path = bundle.getString("img_path");
            int direction = bundle.getInt("direction");

            et_name.setText(name);
            DisplayUtil.showSoftInputFromWindow(activity, et_name);

            et_sex.setText(gender);
            et_address.setText(address);
            et_citizenship_number.setText(id_card_number);
            et_nation.setText(race);

            int length = birthday.length();

            String year = birthday.substring(0, 4);
            String month = birthday.substring(4, 6);
            String day = birthday.substring(6, length);

            et_year.setText(year);
            et_month.setText(month);
            et_day.setText(day);

            if (!TextUtils.isEmpty(path)) {
                try {
                    File file = new File(path);
                    FileInputStream inStream = null;

                    inStream = new FileInputStream(file);
                    Bitmap bitmap = BitmapFactory.decodeStream(inStream);

                    if (direction == 1) {
                        //逆时针90度
                        bitmap = UrlUtils.rotatePicture(bitmap, 90);
                    } else if (direction == 2) {
                        //逆时针180度
                        bitmap = UrlUtils.rotatePicture(bitmap, 180);
                    } else if (direction == 3) {
                        //逆时针270度
                        bitmap = UrlUtils.rotatePicture(bitmap, 270);
                    }

                    iv_mg.setImageBitmap(bitmap);
                    inStream.close();

                    boolean b = UrlUtils.saveBmpToPath(bitmap, path);
                    Log.v("PhotoGraphActivity", direction + "," + b);

                    sp.edit().putString("id_card_front", path).commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public void initView() {
        iv_id_f_sure = (ImageView) findViewById(R.id.iv_id_f_sure);
        rl_back_front = (RelativeLayout) findViewById(R.id.rl_back_front);
        et_year = (EditText) findViewById(R.id.et_year);
        et_month = (EditText) findViewById(R.id.et_month);
        et_day = (EditText) findViewById(R.id.et_day);
        et_name = (EditText) findViewById(R.id.et_name);
        et_sex = (EditText) findViewById(R.id.et_sex);
        et_nation = (EditText) findViewById(R.id.et_nation);
        et_address = (EditText) findViewById(R.id.et_address);
        et_citizenship_number = (EditText) findViewById(R.id.et_citizenship_number);
        iv_mg = (ImageView) findViewById(R.id.iv_mg);
        rl_back_front.setOnClickListener(this);
        iv_id_f_sure.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_back_front:
                finish();
                break;
            case R.id.iv_id_f_sure:
                Toast.makeText(context, "信息保存成功!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent("com.from.call.back.id.card.front");
                intent.putExtra("name", et_name.getText().toString());
                intent.putExtra("birthday", et_year.getText().toString() + "年" + et_month.getText().toString() +
                        "月" + et_day.getText().toString() + "日");
                intent.putExtra("sex", et_sex.getText().toString());
                intent.putExtra("address", et_address.getText().toString());
                intent.putExtra("nation", et_nation.getText().toString());
                intent.putExtra("id_number", et_citizenship_number.getText().toString());

                sendBroadcast(intent);
                break;
            default:
                break;
        }
    }
}
