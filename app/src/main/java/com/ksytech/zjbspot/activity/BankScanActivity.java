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
import android.widget.Toast;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.BankCardParams;
import com.baidu.ocr.sdk.model.BankCardResult;
import com.ksytech.zjbspot.R;
import com.ksytech.zjbspot.view.CameraTopRectView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class BankScanActivity extends Activity implements SurfaceHolder.Callback,
        Camera.AutoFocusCallback, Camera.PreviewCallback, View.OnClickListener {

    private static final String TAG = "BankScanActivity";
    private Context context;

    private int mScreenWidth;
    private int mScreenHeight;
    private CameraTopRectView topView;

    private SurfaceHolder holder;
    private SurfaceView mSurfaceView = null;
    private Camera mCamera;
    private boolean isChoice = true;
    private RelativeLayout rl_bk_sc;

    public Camera.Size mPreviewSize;
    private List<Camera.Size> mSupportedPreviewSizes;

    public Camera.Size mPictureSize;
    private List<Camera.Size> mSupportedPictureSizes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_scan);

        context = this;

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
        mSurfaceView = (SurfaceView) findViewById(R.id.camera_surface_view);
        holder = mSurfaceView.getHolder();//获得surfaceHolder引用
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//设置类型

        topView.draw(new Canvas());
        rl_bk_sc.setOnClickListener(this);
    }

    private void setCameraParams(Camera camera, int width, int height) {
        Log.i(TAG, "setCameraParams  width=" + width + "  height=" + height);
        Camera.Parameters parameters = mCamera.getParameters();

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
                String type = "idcardBank";
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

                    //解析银行卡图片
                    BankCardParams param = new BankCardParams();
                    param.setImageFile(file);

                    // 调用银行卡识别服务
                    OCR.getInstance().recognizeBankCard(param, new OnResultListener<BankCardResult>() {
                        @Override
                        public void onResult(BankCardResult result) {
                            if (result != null) {
                                String type = null;
                                String bankCardNumber = null;
                                String bankName = null;

                                if (result.getBankCardType() == BankCardResult.BankCardType.Credit) {
                                    type = "信用卡";
                                } else if (result.getBankCardType() == BankCardResult.BankCardType.Debit) {
                                    type = "借记卡";
                                } else {
                                    type = "不能识别";
                                }

                                if (!TextUtils.isEmpty(result.getBankCardNumber())) {
                                    bankCardNumber = result.getBankCardNumber();
                                }

                                if (!TextUtils.isEmpty(result.getBankName())) {
                                    bankName = result.getBankName();
                                }

                                if (!TextUtils.isEmpty(type) && !TextUtils.isEmpty(bankCardNumber) &&
                                        !TextUtils.isEmpty(bankName)) {
                                    Intent intent = new Intent(BankScanActivity.this, BandShowActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("type", type);
                                    bundle.putString("bankCardNumber", bankCardNumber);
                                    bundle.putString("bankName", bankName);
                                    bundle.putString("img_path", file.getAbsolutePath());
                                    intent.putExtra("bundle",bundle);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    isChoice = true;
                                }

                            } else {
                                isChoice = true;
                            }
                        }

                        @Override
                        public void onError(OCRError error) {
                            isChoice = true;
                            Log.d("MainActivity", "onError: " + error.getMessage());
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
