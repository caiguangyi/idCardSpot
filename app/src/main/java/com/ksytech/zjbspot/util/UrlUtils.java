package com.ksytech.zjbspot.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.IDCardParams;
import com.baidu.ocr.sdk.model.IDCardResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by caiguangyi on 2017/11/14.
 */

public class UrlUtils {
    public static final String path_front_p = "/storage/emulated/0/Android/data/com.hezd.example.camera/files/idcard,idcardFront,user.jpg";
    public static final String path_front_t = "idcardFront";
    public static final String path_front_f = "user.jpg";

    /**
     * @param bitmap   要保存的图片
     * @param filePath 目标路径
     * @return 是否成功
     * @Description 保存图片到指定路径
     */
    public static boolean saveBmpToPath(final Bitmap bitmap, final String filePath) {
        if (bitmap == null || filePath == null) {
            return false;
        }
        boolean result = false; //默认结果
        File file = new File(filePath);
        OutputStream outputStream = null; //文件输出流
        try {
            outputStream = new FileOutputStream(file);
            result = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream); //将图片压缩为JPEG格式写到文件输出流，100是最大的质量程度
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close(); //关闭输出流
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * @param bitmap 要旋转的图片
     * @param degree 要旋转的角度
     * @return 旋转后的图片
     * @Description 旋转图片一定角度
     */
    public static Bitmap rotatePicture(final Bitmap bitmap, final int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        return resizeBitmap;
    }

    /**
     * 获取原始图片的角度（解决三星手机拍照后图片是横着的问题）
     *
     * @param path 图片的绝对路径
     * @return 原始图片的角度
     */
    public static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            Log.e("jxf", "orientation" + orientation);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 解析身份证图片
     *
     * @param idCardSide 身份证正反面
     * @param filePath   图片路径
     */
    public static void recIDCard(final String idCardSide, String filePath,
                                 final Context context, Boolean isChoice) {
        Log.i("recIDCard", "filePath:" + filePath);
        IDCardParams param = new IDCardParams();
        param.setImageFile(new File(filePath));
        // 设置身份证正反面
        param.setIdCardSide(idCardSide);
        // 设置方向检测
        param.setDetectDirection(true);
        // 设置图像参数压缩质量0-100, 越大图像质量越好但是请求时间越长。 不设置则默认值为20
        param.setImageQuality(40);
        OCR.getInstance().recognizeIDCard(param, new OnResultListener<IDCardResult>() {
            @Override
            public void onResult(IDCardResult result) {
                if (result != null) {

                    if (idCardSide.equals("back")) {
                        String issued_by = "";
                        String valid_date = "";
                        String sing_data = "";
                        String expiry_data = "";

                        Log.i("data_c", result.getIdCardSide() + "," + result.getDirection() + "," +
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

//                        mContent.setText("签发机关:" + issued_by + "\n" +
//                                "有效日期:" + valid_date + "\n");
                    } else {
                        String name = "";
                        String sex = "";
                        String nation = "";
                        String num = "";
                        String address = "";
                        String birthday = "";

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

//                        mContent.setText("姓名: " + name + "\n" +
//                                "性别: " + sex + "\n" +
//                                "民族: " + nation + "\n" +
//                                "身份证号码: " + num + "\n" +
//                                "住址: " + address + "\n" +
//                                "生日:" + birthday + "\n");
                    }

                } else {
                }
            }

            @Override
            public void onError(OCRError error) {

                Log.d("MainActivity", "onError: " + error.getMessage());
            }
        });
    }

    /**
     * 图片旋转
     *
     * @param tmpBitmap
     * @param degrees
     * @return
     */
    public static Bitmap rotateToDegrees(Bitmap tmpBitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.reset();
        matrix.setRotate(degrees);
        return Bitmap.createBitmap(tmpBitmap, 0, 0, tmpBitmap.getWidth(), tmpBitmap.getHeight(), matrix,
                true);
    }

    /**
     * Bitmap裁剪
     *
     * @param bitmap 原图
     * @param width  宽
     * @param height 高
     */
    public static Bitmap bitmapCrop(Bitmap bitmap, int left, int top, int width, int height) {
        if (null == bitmap || width <= 0 || height < 0) {
            return null;
        }
        int widthOrg = bitmap.getWidth();
        int heightOrg = bitmap.getHeight();
        if (widthOrg >= width && heightOrg >= height) {
            try {
                bitmap = Bitmap.createBitmap(bitmap, left, top, width, height);
            } catch (Exception e) {
                return null;
            }
        }
        return bitmap;
    }

}
