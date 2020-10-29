package com.example.mvs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DrawView extends View {
    Paint paint;
    Path path;
    private HashMap<Path, Paint> paths = new HashMap<>();
    List<List<Integer>> oneDimensionalHistograms;
    private int maxValue;
    private Canvas mCanvas;
    private Bitmap mBitmap;

    public DrawView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mBitmap = BitmapFactory.decodeResource(getResources(), R.layout.activity_main);
    }

    public void init(List<List<Integer>> oneDimensionalHistograms, int maxValue) {
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

        this.oneDimensionalHistograms = oneDimensionalHistograms;
        this.maxValue = maxValue;
        path = new Path();
        path.moveTo(20, 20);
        path.lineTo(30,20);
        paint = new Paint();
        paint.setStrokeWidth(20);
        paint.setColor(defineRGBColorByCollectionElement(2));
        paths.put(path, paint);

        path = new Path();
        path.moveTo(30, 20);
        path.lineTo(40,20);
        paint = new Paint();
        paint.setStrokeWidth(20);
        paint.setColor(defineRGBColorByCollectionElement(10));
        paths.put(path, paint);

        path = new Path();
        path.moveTo(40, 20);
        path.lineTo(50,20);
        paint = new Paint();
        paint.setStrokeWidth(20);
        paint.setColor(defineRGBColorByCollectionElement(25));
        paths.put(path, paint);
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.save();
        mCanvas.drawColor(Color.WHITE);
        paths.entrySet().forEach(entry -> mCanvas.drawPath(entry.getKey(), entry.getValue()));
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.restore();
    }

    private int defineRGBColorByCollectionElement(int collectionElement) {
        System.out.println(maxValue);
        System.out.println(collectionElement);
        int RGB = (collectionElement * 255) / this.maxValue == 0 ? 1 : this.maxValue;
        return Color.rgb(RGB, RGB, RGB);
    }

    private void drawLineWithSelectedColor(Canvas canvas, int color, float x, float y) {
        Paint mpaint = new Paint();
        mpaint.setStrokeWidth(20);
        mpaint.setColor(color);
        canvas.drawLine(x, y, x + 20, y, mpaint);
    }

}
