package com.example.mvs;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SecondTaskActivity extends AppCompatActivity {
    private static final int SELECT_PICTURE = 1;
    private Button loadImage;
    private Button calculate;
    private ImageView imageView;
    private Bitmap bitmap;
    private GraphView oneDimensionalOfImageGraph;
    private GraphView probabilityFunctionGraph;
    private GraphView afterCalculatingProbabilityGraph;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_task_activity);
        loadImage = findViewById(R.id.load_image);
        calculate = findViewById(R.id.calculate);
        imageView = findViewById(R.id.portrait);
        oneDimensionalOfImageGraph = findViewById(R.id.one_dimensional_image_graph);
        probabilityFunctionGraph = findViewById(R.id.probability_function_graph);
        afterCalculatingProbabilityGraph = findViewById(R.id.after_probability_calculated_graph);
        setOnClickListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.second_task:
                Intent firstIntent = new Intent(this, SecondTaskActivity.class);
                startActivity(firstIntent);
                return true;
            case R.id.first_task:
                Intent secondIntent = new Intent(this, MainActivity.class);
                startActivity(secondIntent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {

                Uri selectedImageURI = data.getData();

                Picasso.get().load(selectedImageURI).resize(256, 300).into(imageView);
            }

        }
    }

    private void loadInternalImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    private void setOnClickListeners() {
        loadImage.setOnClickListener((view) -> {
            loadInternalImage();
        });
        calculate.setOnClickListener((view) -> {
            int[] extractedRedArray = extractRedFromEachImagePixel();
            //Map<Integer, Integer> elementOccurrences = buildOneDimensionalHistogramByArray(extractedRedArray);
            drawOneDimensionalHistogram(extractedRedArray);
            double[] probabilityArray = getProbabilityFunctionArray(extractedRedArray);
            drawProbabilityHistogram(probabilityArray);
            int[] equalizationArray = buildEqualizationArray(probabilityArray);
            drawEqualizationHistogram(equalizationArray);
        });
    }

    private int[] buildEqualizationArray(double[] probabilityArray) {
        final double cubeOf255 = 6.34133;
        int equalizationResult;
        int[] equalizationArray = new int[65536];
        for(int i = 0; i < 65536; i++) {
            if(i < probabilityArray.length) {
                equalizationResult = (int) Math.pow(probabilityArray[i] * cubeOf255, 3);
                equalizationArray[i] = equalizationResult;
            } else {
                equalizationArray[i] = 255;
            }
        }
        return equalizationArray;
    }

    private void drawEqualizationHistogram(int[] equalizationArray) {
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(fillEqualizationDataPoints(equalizationArray));
        afterCalculatingProbabilityGraph.getViewport().setYAxisBoundsManual(true);
        afterCalculatingProbabilityGraph.getViewport().setMinY(0);
        afterCalculatingProbabilityGraph.getViewport().setMaxY(255);
        afterCalculatingProbabilityGraph.getViewport().setXAxisBoundsManual(true);
        afterCalculatingProbabilityGraph.getViewport().setMinX(0);
        afterCalculatingProbabilityGraph.getViewport().setMaxX(4000);
        afterCalculatingProbabilityGraph.addSeries(series);
    }

    //calculate numbers for probability histogram, only for 10000 elements, because further elements will be equal to 1
    // each element of int[] divided by 10000 in case to increase width of function
    private double[] getProbabilityFunctionArray(int[] extractedRedArray) {
        final int probabilityArraySize = 4000;
        double[] probabilityArray = new double[probabilityArraySize];
        double arrayElement = 0.0;
        for(int i = 0; i < probabilityArraySize; i++) {
            probabilityArray[i] = (double) extractedRedArray[i] / 100000.0d;
            arrayElement = probabilityArray[i == 0 ? 0 : i - 1] + probabilityArray[i];
            probabilityArray[i] = arrayElement > 1 ? 1 : arrayElement;
        }
        return probabilityArray;
    }

    private DataPoint[] fillEqualizationDataPoints(int[] equalizationArray) {
        final int dataPointsSize = 4000;
        DataPoint[] dataPoints = new DataPoint[dataPointsSize];
        for (int i = 0; i < dataPointsSize; i++) {
            dataPoints[i] = new DataPoint(i, equalizationArray[i]);
        }
        return dataPoints;
    }

    private void drawProbabilityHistogram(double[] probabilityArray) {
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(fillProbabilityDataPoints(probabilityArray));
        probabilityFunctionGraph.getViewport().setYAxisBoundsManual(true);
        probabilityFunctionGraph.getViewport().setMinY(0);
        probabilityFunctionGraph.getViewport().setMaxY(1);
        probabilityFunctionGraph.getViewport().setXAxisBoundsManual(true);
        probabilityFunctionGraph.getViewport().setMinX(0);
        probabilityFunctionGraph.getViewport().setMaxX(4000);
        probabilityFunctionGraph.addSeries(series);
    }

    private DataPoint[] fillProbabilityDataPoints(double[] probabilityArray) {
        final int dataPointsSize = 4000;
        DataPoint[] dataPoints = new DataPoint[dataPointsSize];
        for (int i = 0; i < dataPointsSize; i++) {
            dataPoints[i] = new DataPoint(i, probabilityArray[i]);
        }
        return dataPoints;
    }

    private void drawOneDimensionalHistogram(int[] extractedRedArray) {
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(fillDataPoints(extractedRedArray));
        oneDimensionalOfImageGraph.getViewport().setYAxisBoundsManual(true);
        oneDimensionalOfImageGraph.getViewport().setMinY(0);
        oneDimensionalOfImageGraph.getViewport().setMaxY(255);
        oneDimensionalOfImageGraph.getViewport().setXAxisBoundsManual(true);
        oneDimensionalOfImageGraph.getViewport().setMinX(0);
        oneDimensionalOfImageGraph.getViewport().setMaxX(10000);
        oneDimensionalOfImageGraph.addSeries(series);
    }

    private DataPoint[] fillDataPoints(int[] extractedRedArray) {
        final int dataPointSize = 10000;
        DataPoint[] dataPoints = new DataPoint[dataPointSize];
        for (int i = 0; i < dataPointSize; i++) {
            dataPoints[i] = new DataPoint(i, extractedRedArray[i]);
        }
        return dataPoints;
    }

    private Map<Integer, Integer> buildOneDimensionalHistogramByArray(int[] array) {
        Map<Integer, Integer> frequencyOfElement = new HashMap<>();
        for (int value : array) {
            Integer mapValue = frequencyOfElement.get(value);
            frequencyOfElement.put(value, (mapValue == null) ? 0 : mapValue + 1);
        }
        return frequencyOfElement;
    }

    private int[] extractRedFromEachImagePixel() {
        int[] extractedRed = new int[65536];
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache(true);
        bitmap = imageView.getDrawingCache();
        int index = 0;
        int x;
        for (int y = 0; y < 256; y++) {
            for (x = 0; x < 256; x++) {
                extractedRed[index++] = Color.red(bitmap.getPixel(x, y));
            }
            x = 0;
        }
        return extractedRed;
    }
} 
