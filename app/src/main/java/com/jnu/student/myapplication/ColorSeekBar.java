package com.jnu.student.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ColorSeekBar extends View {

    private final Paint paint = new Paint();
    private final Path sPath = new Path();
    private float sLeft, sTop;
    private float sWidth,sHeight;
    private float x,y;
    private float mRadius;
    private int[] colorArray;
    private OnColorSelectedListener onColorSelectedListener=null;

    public ColorSeekBar(Context context) {
        this(context, null);
    }

    public void setCircleX (float x) {
        this.x =x;
        this.invalidate();
    }

    public void setColor(int startColor,int endColor, int ...colors){
        colorArray=new int[colors.length+2];
        colorArray[0]=startColor;
        System.arraycopy(colors,0,colorArray,1,colors.length);
        colorArray[colors.length+1]=endColor;
    }

    public ColorSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = (int)(MeasureSpec.getSize(widthMeasureSpec)*0.85);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec)*2;
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float sRight, sBottom;
        mRadius=h/2;
        sLeft = mRadius; // 背景左的坐标
        sTop = h*0.4f;//top位置
        sRight = w-mRadius; // 背景的宽的全部
        sBottom = h*0.6f; // 背景底部
        sWidth = sRight - sLeft; // 背景的宽度
        sHeight = sBottom - sTop; // 背景的高度
        sPath.moveTo(sLeft,sTop+sHeight/2);
        sPath.lineTo(sWidth,sTop+sHeight/2);
        sPath.close();    // path准备背景的路径
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(canvas);
        drawCircle(canvas);
        paint.reset();
    }

    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent event) {
        this.x = event.getX();
        x=x<0?0:x;//判断thumb边界
        x=x>sWidth?sWidth:x;
        if (onColorSelectedListener!=null) {
            onColorSelectedListener.onColorSelected(getColor(x), x);
        }
        this.invalidate();
        return true;
    }

    // 画圆形
    private void drawCircle(Canvas canvas){
        Paint thumbPaint = new Paint();
        x=x<mRadius?mRadius:x;//判断thumb边界
        x=x>sWidth-mRadius?sWidth-mRadius:x;
        RadialGradient radialGradient = new RadialGradient(x,mRadius,mRadius,getColor(x), Color.WHITE,Shader.TileMode.CLAMP);
        thumbPaint.setStyle(Style.FILL);
        thumbPaint.setShader(radialGradient);
        canvas.drawCircle(x, mRadius, mRadius, thumbPaint);
    }

    private void drawBackground(Canvas canvas){
        LinearGradient linearGradient=new LinearGradient(sLeft,sTop,sWidth,sHeight,colorArray,null, Shader.TileMode.REPEAT);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        //设置渲染器
        paint.setShader(linearGradient);
        paint.setStrokeWidth(6);
        canvas.drawPath(sPath, paint);
    }

    //回调接口
    public interface OnColorSelectedListener {
        void onColorSelected(int color, float x);
    }

    public void setOnColorSelectedListener(OnColorSelectedListener onColorSelectedListener) {
        this.onColorSelectedListener = onColorSelectedListener;
    }

    //x为seekBar位移,获取某一个百分比间的颜色,radio取值[0,1]
    public int getColor(float x) {
        float width = sWidth;
        float period = (int)(width/(colorArray.length-1));
        int start = (int)(x/period);
        start=start>=colorArray.length-1?start-1:start;
        int colorStart = colorArray[start];
        int colorEnds = colorArray[start+1];

        float radio = (x-period*start)/period;
        int redStart = Color.red(colorStart);
        int blueStart = Color.blue(colorStart);
        int greenStart = Color.green(colorStart);
        int redEnd = Color.red(colorEnds);
        int blueEnd = Color.blue(colorEnds);
        int greenEnd = Color.green(colorEnds);

        int red = (int) (redStart + ((redEnd - redStart) * radio + 0.5));
        int greed = (int) (greenStart + ((greenEnd - greenStart) * radio + 0.5));
        int blue = (int) (blueStart + ((blueEnd - blueStart) * radio + 0.5));
        return Color.argb(255,red, greed, blue);
    }
}

