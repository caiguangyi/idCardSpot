package com.ksytech.zjbspot.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by caiguangyi on 2017/11/13.
 */

public class CameraTopRectView extends View {
    private int panelWidth;
    private int panelHeght;

    private int viewWidth;
    private int viewHeight;

    public int rectWidth;
    public int rectHeght;

    private int rectTop;
    private int rectLeft;
    private int rectRight;
    private int rectBottom;

    private int lineLen;
    private int lineWidht;
    private static final int LINE_WIDTH = 5;
    private static final int TOP_BAR_HEIGHT = 50;
    private static final int BOTTOM_BTN_HEIGHT = 66;

//    private static final int TOP_BAR_HEIGHT = Constant.RECT_VIEW_TOP;
//    private static final int BOTTOM_BTN_HEIGHT = Constant.RECT_VIEW_BOTTOM;

    private static final int LEFT_PADDING = 66;
    private static final int RIGHT_PADDING = 66;
//    private static final String TIPS = "请将身份证放入到方框中";

    private Paint linePaint;
    private Paint wordPaint;
    private Rect rect;
    private int baseline;

    public CameraTopRectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        Activity activity = (Activity) context;

        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        panelWidth = wm.getDefaultDisplay().getWidth();//拿到屏幕的宽
        panelHeght = wm.getDefaultDisplay().getHeight();//拿到屏幕的高

        //高度不需要dp转换px,不然整体相机会向上移动一小节
//        viewHeight = panelHeght - (int) DisplayUtil.dp2px(activity,TOP_BAR_HEIGHT + BOTTOM_BTN_HEIGHT);

        viewHeight = panelHeght;
        //viewHeight,界面的高,viewWidth,界面的宽
        viewWidth = panelWidth;

        Log.i("message_i", "viewHeight:" + viewHeight + "," + "viewWidth:" + viewWidth);

        /*rectWidth = panelWidth
                - UnitUtils.getInstance(activity).dip2px(
                        LEFT_PADDING + RIGHT_PADDING);*/

//        rectWidth = panelWidth - (int) DisplayUtil.dp2px(activity, LEFT_PADDING + RIGHT_PADDING);
//        rectHeght = (int) (rectWidth * 3 / 2);
//        rectWidth = panelWidth - (int) DisplayUtil.dp2px(activity, LEFT_PADDING + RIGHT_PADDING);
//        rectHeght = (int) (rectWidth * 3 / 2);

        rectWidth = viewWidth * 2 / 3;
        rectHeght = viewWidth;

        Log.d("message_i", "rectWidth:" + rectWidth + "," + "rectHeght:" + rectHeght);

        // 相对于此view
//        rectTop = (viewHeight - rectHeght) / 2;
//        rectLeft = (viewWidth - rectWidth) / 2;
//        rectBottom = rectTop + rectHeght;
//        rectRight = rectLeft + rectWidth;

        rectTop = (viewHeight - viewWidth) / 2;
        rectLeft = viewWidth / 6;
        rectBottom = rectTop + rectHeght;
        rectRight = rectLeft + rectWidth;

        //rectTop:636,rectLeft:26,rectBottom:1283,rectRight:1053
        Log.v("message_i", "rectTop:" + rectTop + "," + "rectLeft:" + rectLeft + "," + "rectBottom:" + rectBottom
                + "," + "rectRight:" + rectRight);

        lineLen = panelWidth / 8;

        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setColor(Color.rgb(0xdd, 0x42, 0x2f));
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(LINE_WIDTH);// 设置线宽
        linePaint.setAlpha(255);

        wordPaint = new Paint();
        wordPaint.setAntiAlias(true);
        wordPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        wordPaint.setStrokeWidth(3);
        wordPaint.setTextSize(35);

        rect = new Rect(rectLeft, rectTop - 80, rectRight, rectTop - 10);
        Paint.FontMetricsInt fontMetrics = wordPaint.getFontMetricsInt();
        baseline = rect.top + (rect.bottom - rect.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
        wordPaint.setTextAlign(Paint.Align.CENTER);
    }

    public CameraTopRectView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        Activity activity = (Activity) context;

        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        panelWidth = wm.getDefaultDisplay().getWidth();//拿到屏幕的宽
        panelHeght = wm.getDefaultDisplay().getHeight();//拿到屏幕的高

        //高度不需要dp转换px,不然整体相机会向上移动一小节
//        viewHeight = panelHeght - (int) DisplayUtil.dp2px(activity,TOP_BAR_HEIGHT + BOTTOM_BTN_HEIGHT);

        viewHeight = panelHeght;
        //viewHeight,界面的高,viewWidth,界面的宽
        viewWidth = panelWidth;

        Log.d("message_i", "------------------------------------------------------------------------------");
        Log.i("message_i", "viewHeight:" + viewHeight + "," + "viewWidth:" + viewWidth);
        //viewHeight:1920,viewWidth:1080

        /*rectWidth = panelWidth
                - UnitUtils.getInstance(activity).dip2px(
                        LEFT_PADDING + RIGHT_PADDING);*/

//        rectWidth = panelWidth - (int) DisplayUtil.dp2px(activity, LEFT_PADDING + RIGHT_PADDING);
//        rectHeght = (int) (rectWidth * 3 / 2);

        rectWidth = viewWidth * 2 / 3;
        rectHeght = viewWidth;

        Log.d("message_i", "rectWidth:" + rectWidth + "," + "rectHeght:" + rectHeght);
        //rectWidth:1027,rectHeght:647

        // 相对于此view
//        rectTop = (viewHeight - rectHeght) / 2;
//        rectLeft = (viewWidth - rectWidth) / 2;
//        rectBottom = rectTop + rectHeght;
//        rectRight = rectLeft + rectWidth;

        //width:1920 height:1080  viewHeight:1920,viewWidth:1080
        //(x,y,width,height) (width - height) / 2, height / 6, height, height * 2 / 3
        rectTop = (viewHeight - viewWidth) / 2;
        rectLeft = viewWidth / 6;
        rectBottom = rectTop + rectHeght;
        rectRight = rectLeft + rectWidth;

        Log.i("message_i", "rectTop:" + rectTop + "," + "rectLeft:" + rectLeft + "," + "rectBottom:" + rectBottom
                + "," + "rectRight:" + rectRight);
        //rectTop:636,rectLeft:26,rectBottom:1283,rectRight:1053

        //rectTop:146,rectLeft:26,rectBottom:1773,rectRight:1053

        lineLen = panelWidth / 8;

        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setColor(Color.rgb(0xdd, 0x42, 0x2f));
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(LINE_WIDTH);// 设置线宽
        linePaint.setAlpha(255);

        wordPaint = new Paint();
        wordPaint.setAntiAlias(true);
        wordPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        wordPaint.setStrokeWidth(3);
        wordPaint.setTextSize(35);

        rect = new Rect(rectLeft, rectTop - 80, rectRight, rectTop - 10);
        Paint.FontMetricsInt fontMetrics = wordPaint.getFontMetricsInt();
        baseline = rect.top + (rect.bottom - rect.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
        wordPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        wordPaint.setColor(Color.TRANSPARENT);
        canvas.drawRect(rect, wordPaint);

        //画蒙层
        wordPaint.setColor(0xa0000000);
        rect = new Rect(0, viewHeight / 2 + rectHeght / 2, viewWidth, viewHeight);
        canvas.drawRect(rect, wordPaint);

        rect = new Rect(0, 0, viewWidth, viewHeight / 2 - rectHeght / 2);
        canvas.drawRect(rect, wordPaint);

        rect = new Rect(0, viewHeight / 2 - rectHeght / 2, (viewWidth - rectWidth) / 2, viewHeight / 2 + rectHeght / 2);
        canvas.drawRect(rect, wordPaint);

        rect = new Rect(viewWidth - (viewWidth - rectWidth) / 2, viewHeight / 2 - rectHeght / 2, viewWidth, viewHeight / 2 + rectHeght / 2);
        canvas.drawRect(rect, wordPaint);


        //重制rect  并画文字  吧文字置于rect中间
        rect = new Rect(rectLeft, rectTop - 80, rectRight, rectTop - 10);
        wordPaint.setColor(Color.WHITE);
//        canvas.drawText(TIPS, rect.centerX(), baseline, wordPaint);
        canvas.drawLine(rectLeft, rectTop, rectLeft + lineLen, rectTop,
                linePaint);
        canvas.drawLine(rectRight - lineLen, rectTop, rectRight, rectTop,
                linePaint);
        canvas.drawLine(rectLeft, rectTop, rectLeft, rectTop + lineLen,
                linePaint);
        canvas.drawLine(rectRight, rectTop, rectRight, rectTop + lineLen,
                linePaint);
        canvas.drawLine(rectLeft, rectBottom, rectLeft + lineLen, rectBottom,
                linePaint);
        canvas.drawLine(rectRight - lineLen, rectBottom, rectRight, rectBottom,
                linePaint);
        canvas.drawLine(rectLeft, rectBottom - lineLen, rectLeft, rectBottom,
                linePaint);
        canvas.drawLine(rectRight, rectBottom - lineLen, rectRight, rectBottom,
                linePaint);
    }

    public int getRectLeft() {
        return rectLeft;
    }

    public int getRectTop() {
        return rectTop;
    }

    public int getRectRight() {
        return rectRight;
    }

    public int getRectBottom() {
        return rectBottom;
    }

    public int getViewWidth() {
        return viewWidth;
    }

    public int getViewHeight() {
        return viewHeight;
    }

}
