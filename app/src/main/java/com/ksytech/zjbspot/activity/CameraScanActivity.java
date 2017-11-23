package com.ksytech.zjbspot.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.IDCardParams;
import com.baidu.ocr.sdk.model.IDCardResult;
import com.ksytech.zjbspot.R;
import com.ksytech.zjbspot.view.CameraTopRectView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class CameraScanActivity extends Activity implements SurfaceHolder.Callback,
        Camera.AutoFocusCallback, Camera.PreviewCallback, View.OnClickListener {

    private static final String TAG = "CameraScanActivity";
    private Context context;

    private int mScreenWidth;
    private int mScreenHeight;
    private CameraTopRectView topView;

    private SurfaceHolder holder;
    private SurfaceView mSurfaceView = null;
    private Camera mCamera;
    private boolean isChoice = true;
    private RelativeLayout rl_bk_sc;
    private TextView tv_desc;

    private String id_card_side;//身份证的正反面
    public Camera.Size mPreviewSize;
    private List<Camera.Size> mSupportedPreviewSizes;

    public Camera.Size mPictureSize;
    private List<Camera.Size> mSupportedPictureSizes;

    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_scan);

        context = this;
        Intent intent = getIntent();

        if (intent != null) {
            id_card_side = intent.getStringExtra("id_card_side");
            type = intent.getStringExtra("type");
        }

        getScreenMetrix(context);
        topView = new CameraTopRectView(context);
        initView();

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.i(TAG, "surfaceCreated");
        if (mCamera == null) {
            try {
                mCamera = Camera.open();//开启相机
                mCamera.setPreviewDisplay(holder);//摄像头画面显示在Surface上
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, e + ".");
                Toast.makeText(context, "需要获取相机权限!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        Log.i(TAG, "surfaceChanged");

        if (mCamera != null) {
            setCameraParams(mCamera, mScreenWidth, mScreenHeight);
            mCamera.setPreviewCallback(this);
            mCamera.startPreview();
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.i(TAG, "surfaceDestroyed");
        holder.removeCallback(this);

        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();//停止预览
            mCamera.release();//释放相机资源
            mCamera = null;
        }

        holder = null;
    }

    @Override
    public void onAutoFocus(boolean success, Camera camera) {
        if (success) {
            Log.i(TAG, "onAutoFocus success=" + success);
            System.out.println(success);
        }
    }

    //拿到手机屏幕大小
    private void getScreenMetrix(Context context) {
        WindowManager WM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        WM.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;
        mScreenHeight = outMetrics.heightPixels;
    }

    private void initView() {
        rl_bk_sc = (RelativeLayout) findViewById(R.id.rl_bk_sc);
        tv_desc = (TextView) findViewById(R.id.tv_desc);
        mSurfaceView = (SurfaceView) findViewById(R.id.camera_surface_view);
        holder = mSurfaceView.getHolder();//获得surfaceHolder引用
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//设置类型

        if (!TextUtils.isEmpty(id_card_side)) {
            if (id_card_side.equals(IDCardParams.ID_CARD_SIDE_FRONT)) {
                tv_desc.setText("二代身份证-正面");
            } else {
                tv_desc.setText("二代身份证-反面");
            }
        } else {
            tv_desc.setText("二代身份证-正面");
        }

        topView.draw(new Canvas());
        rl_bk_sc.setOnClickListener(this);
    }

    private void setCameraParams(Camera camera, int width, int height) {
        Log.i(TAG, "setCameraParams  width=" + width + "  height=" + height);
        Camera.Parameters parameters = mCamera.getParameters();
        // 获取摄像头支持的PictureSize列表
//        List<Camera.Size> pictureSizeList = parameters.getSupportedPictureSizes();
//        for (Camera.Size size : pictureSizeList) {
//            Log.i(TAG, "pictureSizeList size.width=" + size.width + "  size.height=" + size.height);
//        }
//        /**从列表中选取合适的分辨率*/
//        Camera.Size picSize = getProperSize(pictureSizeList, ((float) height / width));
//        if (null == picSize) {
//            Log.i(TAG, "null == picSize");
//            picSize = parameters.getPictureSize();
//        }
//        Log.i(TAG, "picSize.width=" + picSize.width + "  picSize.height=" + picSize.height);
//        // 根据选出的PictureSize重新设置SurfaceView大小
//        float w = picSize.width;
//        float h = picSize.height;
//        parameters.setPictureSize(picSize.width, picSize.height);
//        mSurfaceView.setLayoutParams(new FrameLayout.LayoutParams((int) (height * (h / w)), height));
//
//        // 获取摄像头支持的PreviewSize列表
//        List<Camera.Size> previewSizeList = parameters.getSupportedPreviewSizes();
//
//        for (Camera.Size size : previewSizeList) {
//            Log.i(TAG, "previewSizeList size.width=" + size.width + "  size.height=" + size.height);
//        }
//        Camera.Size preSize = getProperSize(previewSizeList, ((float) height) / width);
//        if (null != preSize) {
//            Log.i(TAG, "preSize.width=" + preSize.width + "  preSize.height=" + preSize.height);
//            parameters.setPreviewSize(preSize.width, preSize.height);
//        }

        mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
        mSupportedPictureSizes = mCamera.getParameters().getSupportedPictureSizes();

        if (mSupportedPreviewSizes != null) {
            // 需要宽高切换 因为相机有90度的角度
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, height, width);
            Log.e(TAG, "Preview mPreviewSize w - h : " + mPreviewSize.width + " - " + mPreviewSize.height);
        }
        if (mSupportedPictureSizes != null) {
            mPictureSize = getOptimalPreviewSize(mSupportedPictureSizes, height, width);
            Log.e(TAG, "Preview mPictureSize w - h : " + mPictureSize.width + " - " + mPictureSize.height);
        }

        parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
        parameters.setPictureSize(mPictureSize.width, mPictureSize.height);


        parameters.setJpegQuality(100); // 设置照片质量
        if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);// 连续对焦模式
        }

        mCamera.cancelAutoFocus();//自动对焦。
        mCamera.setDisplayOrientation(90);// 设置PreviewDisplay的方向，效果就是将捕获的画面旋转多少度显示
        mCamera.setParameters(parameters);

    }

    /**
     * 从列表中选取合适的分辨率
     * 默认w:h = 4:3
     * <p>注意：这里的w对应屏幕的height
     * h对应屏幕的width<p/>
     */
    private Camera.Size getProperSize(List<Camera.Size> pictureSizeList, float screenRatio) {
        Log.i(TAG, "screenRatio=" + screenRatio);
        Camera.Size result = null;
        for (Camera.Size size : pictureSizeList) {
            float currentRatio = ((float) size.width) / size.height;
            if (currentRatio - screenRatio == 0) {
                result = size;
                break;
            }
        }

        if (null == result) {
            for (Camera.Size size : pictureSizeList) {
                float curRatio = ((float) size.width) / size.height;
                if (curRatio == 4f / 3) {// 默认w:h = 4:3
                    result = size;
                    break;
                }
            }
        }

        return result;
    }


    @Override
    public void onPreviewFrame(final byte[] data, Camera camera) {
//        Log.i("onPreviewFrame","isChoice:" + isChoice);

        if (isChoice) {
            isChoice = false;

            Log.d(TAG, "isChoice:" + isChoice + "," + data);

            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                File externalFile = getExternalFilesDir("/idcard/");
                String filePath = externalFile.getAbsolutePath();
                String fileName = "user.jpg";

                //处理data
                Camera.Size previewSize = mCamera.getParameters().getPreviewSize();//获取尺寸,格式转换的时候要用到
                BitmapFactory.Options newOpts = new BitmapFactory.Options();
                newOpts.inJustDecodeBounds = true;
                YuvImage yuvimage = new YuvImage(
                        data,
                        ImageFormat.NV21,
                        previewSize.width,
                        previewSize.height,
                        null);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                yuvimage.compressToJpeg(new Rect(0, 0, previewSize.width, previewSize.height), 100, baos);// 80--JPG图片的质量[0-100],100最高
                byte[] rawImage = baos.toByteArray();
                //将rawImage转换成bitmap
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                Bitmap bitmap = BitmapFactory.decodeByteArray(rawImage, 0, rawImage.length, options);

                // 根据拍照所得的数据创建位图
                int height = bitmap.getHeight();
                int width = bitmap.getWidth();
                final Bitmap bitmap1 = Bitmap.createBitmap(bitmap, (width - height) / 2, height / 6, height, height * 2 / 3);
//                final Bitmap bitmap1 = Bitmap.createBitmap(bitmap, topView.getRectTop(), topView.getRectLeft(),
//                        topView.getRectBottom() - topView.getRectTop(), topView.getRectRight() - topView.getRectLeft());

                Log.e(TAG, "width:" + width + " height:" + height);
                Log.e(TAG, "x:" + (width - height) / 2 + " y:" + height / 6 + " width:" + height + " height:" + height * 2 / 3);
                Log.e(TAG, "x2:" + topView.getRectLeft() + " y:" + topView.getRectTop() + " width:" + (topView.getRectRight() - topView.getRectLeft()) +
                        " height:" + (topView.getRectBottom() - topView.getRectTop()));
                // 创建一个位于SD卡上的文件

                Log.d(TAG, filePath + "," + type + "," + fileName);


                File path = new File(filePath);
                if (!path.exists()) {
                    path.mkdirs();
                }
                final File file = new File(path, type + "_" + fileName);
                Log.e(TAG, file.getAbsolutePath());

                FileOutputStream outStream = null;

                try {
                    // 打开指定文件对应的输出流
                    outStream = new FileOutputStream(file);
                    // 把位图输出到指定文件中
                    bitmap1.compress(Bitmap.CompressFormat.JPEG,
                            100, outStream);
                    outStream.close();

                    //解析身份证图片 idCardSide:身份证正反面  filePath:图片路径
                    IDCardParams param = new IDCardParams();
                    param.setImageFile(file);
                    // 设置身份证正反面
                    param.setIdCardSide(id_card_side);
                    // 设置方向检测
                    param.setDetectDirection(true);
                    // 设置图像参数压缩质量0-100, 越大图像质量越好但是请求时间越长。 不设置则默认值为20
//                    param.setImageQuality(20);
                    OCR.getInstance().recognizeIDCard(param, new OnResultListener<IDCardResult>() {
                        @Override
                        public void onResult(IDCardResult result) {
                            Log.v(TAG, "result:" + result);

                            if (result != null) {

                                if (id_card_side.equals(IDCardParams.ID_CARD_SIDE_BACK)) {
                                    String issued_by = "";
                                    String valid_date = "";
                                    String sing_data = "";//登记日期
                                    String expiry_data = "";//截止日期
                                    int direction = result.getDirection();

                                    Log.i(TAG, result.getIdCardSide() + "," + result.getDirection() + "," +
                                            result.getImageStatus() + "," + result.getRiskType() + "," + result.getSignDate() + "," +
                                            result.getExpiryDate() + "," + result.getIssueAuthority());

                                    if (result.getIssueAuthority() != null) {
                                        issued_by = result.getIssueAuthority().toString();
                                    }

                                    if (result.getSignDate() != null) {
                                        sing_data = result.getSignDate().toString();
                                    }

                                    if (result.getExpiryDate() != null) {
                                        expiry_data = result.getExpiryDate().toString();
                                    }

                                    valid_date = sing_data + "-" + expiry_data;

                                    if (!TextUtils.isEmpty(issued_by) && !TextUtils.isEmpty(sing_data) &&
                                            !TextUtils.isEmpty(expiry_data)) {
                                        Log.v(TAG, "issued_by:" + issued_by + "," + "valid_date:" + valid_date);

                                        Intent intent = new Intent(context, OtherSideActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putString("issued_by", issued_by);
                                        bundle.putString("valid_date", valid_date);
                                        bundle.putString("img_path", file.getAbsolutePath());
                                        bundle.putString("type", "baiduyunsdk");
                                        bundle.putInt("direction", direction);

                                        intent.putExtra("bundle", bundle);
                                        startActivity(intent);

                                        finish();
                                    } else {
                                        isChoice = true;
                                    }
                                } else {
                                    String name = "";
                                    String sex = "";
                                    String nation = "";
                                    String num = "";
                                    String address = "";
                                    String birthday = "";

                                    int direction = result.getDirection();
                                    String imageStatus = result.getImageStatus();
                                    Log.v(TAG, "direction:" + direction + "," + "imageStatus:" + imageStatus);


                                    if (result.getName() != null) {
                                        name = result.getName().toString();
                                    }
                                    if (result.getGender() != null) {
                                        sex = result.getGender().toString();
                                    }
                                    if (result.getEthnic() != null) {
                                        nation = result.getEthnic().toString();
                                    }
                                    if (result.getIdNumber() != null) {
                                        num = result.getIdNumber().toString();
                                    }
                                    if (result.getAddress() != null) {
                                        address = result.getAddress().toString();
                                    }

                                    if (result.getBirthday() != null) {
                                        birthday = result.getBirthday().toString();
                                    }

                                    if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(sex) && !TextUtils.isEmpty(nation) &&
                                            !TextUtils.isEmpty(num) && !TextUtils.isEmpty(address) && !TextUtils.isEmpty(birthday)) {
                                        Intent intent = new Intent(CameraScanActivity.this, PositiveActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putString("gender", sex);
                                        bundle.putString("name", name);
                                        bundle.putString("id_card_number", num);
                                        bundle.putString("birthday", birthday);
                                        bundle.putString("race", nation);
                                        bundle.putString("address", address);
                                        bundle.putString("img_path", file.getAbsolutePath());
                                        bundle.putInt("direction", direction);

                                        intent.putExtra("bundle", bundle);
                                        startActivity(intent);
                                        finish();

                                    } else {
                                        isChoice = true;
                                    }


                                    //姓名: 蔡光意,性别: 男,民族: 汉,身份证号码: 421083199403241230,住址: 湖北省洪湖市曹市镇昊口村1-70,生日:19940324,
                                    Log.d(TAG, "姓名: " + name + "," +
                                            "性别: " + sex + "," +
                                            "民族: " + nation + "," +
                                            "身份证号码: " + num + "," +
                                            "住址: " + address + "," +
                                            "生日:" + birthday + ",");
                                }

                            } else {

                                isChoice = true;
                            }
                        }

                        @Override
                        public void onError(OCRError error) {
                            isChoice = true;
                            int errorCode = error.getErrorCode();
                            Throwable cause = error.getCause();
                            long logId = error.getLogId();
                            String localizedMessage = error.getLocalizedMessage();

                            Log.d(TAG, "onError: " + error.getMessage() + "," + errorCode +
                                    "," + cause + "," + logId + "," + localizedMessage);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }


            } else {
                Toast.makeText(context, "没有检测到内存卡", Toast.LENGTH_SHORT).show();
            }

        }

    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_bk_sc:
                finish();
                break;
            default:
                break;
        }
    }
}
