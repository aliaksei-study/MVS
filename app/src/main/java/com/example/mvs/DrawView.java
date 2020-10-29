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
    List<List<Integer>> oneDimensionalHistograms;
    private int maxValue;

    public DrawView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void init(List<List<Integer>> oneDimensionalHistograms, int maxValue) {
        this.oneDimensionalHistograms = oneDimensionalHistograms;
        this.maxValue = maxValue;
    }

    @Override
    public void onDraw(Canvas canvas) {
        float startX = 0.f;
        float startY = 0.f;
        List<Integer> histogram;
        for(int i = 0; i < (Math.min(oneDimensionalHistograms.size(), 256)); i++) {
            startY += 5;
            histogram = oneDimensionalHistograms.get(i);
            for(int j = 0; j < histogram.size(); j++) {
                startX += 5;
                drawLineWithSelectedColor(canvas, defineRGBColorByCollectionElement(histogram.get(j)), startX, startY);
            }
            startX = 0.f;
        }
    }

    private int defineRGBColorByCollectionElement(int collectionElement) {
        System.out.println(maxValue);
        System.out.println(collectionElement);
        int RGB = (collectionElement * 255) / this.maxValue == 0 ? 1 : this.maxValue;
        return Color.rgb(RGB, RGB, RGB);
    }

    private void drawLineWithSelectedColor(Canvas canvas, int color, float x, float y) {
        Paint mpaint = new Paint();
        mpaint.setStrokeWidth(5);
        mpaint.setColor(color);
        canvas.drawLine(x, y, x + 5, y, mpaint);
    }

}
