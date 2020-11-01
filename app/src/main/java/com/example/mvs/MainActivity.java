package com.example.mvs;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.apache.commons.math3.distribution.WeibullDistribution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final Handler mHandler = new Handler();
    private Runnable sampleGraphRunnable;
    private Runnable oneDimensionalGraphRunnable;
    private double graphLastXValue = 1024d;
    private LineGraphSeries<DataPoint> sampleGraphSeries;
    private double[] weibullDistributionArray;
    private List<List<Integer>> oneDimensionalHistograms = new ArrayList<>();
    GraphView oneDimensionalGraph;
    private int oneDimensionalGraphCounter = 1;
    private DrawView drawView;

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
                finish();
                return true;
            case R.id.first_task:
                Intent secondIntent = new Intent(this, MainActivity.class);
                startActivity(secondIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawView = findViewById(R.id.draw_view);
        WeibullDistribution weibullDistribution = new WeibullDistribution(5, 5);
        weibullDistributionArray = weibullDistribution.sample(16384);

        GraphView sampleGraph = findViewById(R.id.sample_graph);
        sampleGraphSeries = new LineGraphSeries<>(getDataPoint(weibullDistributionArray, 1024));
        sampleGraph.addSeries(sampleGraphSeries);
        sampleGraphSeries.setTitle("Распределение Вейбулла");
        configureGraph(sampleGraph, 0, 0, 1024, 10);

        oneDimensionalGraph = findViewById(R.id.one_dimensional_graph);
        for (int i = 0, j = 256; j < weibullDistributionArray.length - 256; i++, j++) {
            oneDimensionalHistograms.add(getListOfOneDimensionalHistogramValues(weibullDistributionArray, i, j));
        }
        LineGraphSeries<DataPoint> oneDimensionalSeries = new LineGraphSeries<>(getDataPoint(oneDimensionalHistograms.get(0)));
        oneDimensionalGraph.addSeries(oneDimensionalSeries);
        oneDimensionalSeries.setTitle("Одномерная гистограмма");
        configureGraph(oneDimensionalGraph, 0, 0, 256, 40);
        drawView.init(oneDimensionalHistograms, findMaxValueInFirst100Collections());
    }

    private int findMaxValueInFirst100Collections() {
        int maxValue = 0;
        int currentMaxValue;
        for(int i = 0; i < (Math.min(oneDimensionalHistograms.size(), 100)); i++) {
            currentMaxValue = Collections.max(oneDimensionalHistograms.get(i));
            maxValue = Math.max(currentMaxValue, maxValue);
        }
        return maxValue;
    }


    private List<Integer> getListOfOneDimensionalHistogramValues(double[] weibullDistributionArray, int firstBorder, int secondBorder) {
        int frequency;
        int listIndex;
        List<Integer> occurences = new ArrayList<>(Collections.nCopies(secondBorder - firstBorder, 0));
        for (int i = firstBorder; i < secondBorder; i++) {
            listIndex = (int) (weibullDistributionArray[i] * 10);
            frequency = occurences.get(listIndex);
            occurences.remove(listIndex);
            occurences.add(listIndex, ++frequency);
        }
        return occurences;
    }

    private void configureGraph(GraphView graph, int minX, int minY, int maxX, int maxY) {
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(minY);
        graph.getViewport().setMaxY(maxY);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(minX);
        graph.getViewport().setMaxX(maxX);
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
    }

    private DataPoint[] getDataPoint(List<Integer> histogramValues) {
        DataPoint[] dataPoints = new DataPoint[histogramValues.size()];
        for (int i = 0; i < histogramValues.size(); i++) {
            dataPoints[i] = new DataPoint(i, histogramValues.get(i));
        }
        return dataPoints;
    }

    private DataPoint[] getDataPoint(double[] dataArray, int length) {
        DataPoint[] dataPoints = new DataPoint[length];
        for (int i = 0; i < length; i++) {
            dataPoints[i] = new DataPoint(i, dataArray[i]);
        }
        return dataPoints;
    }

    public void onResume() {
        super.onResume();
        sampleGraphRunnable = new Runnable() {
            @Override
            public void run() {
                graphLastXValue += 1d;
                sampleGraphSeries.appendData(new DataPoint(graphLastXValue, getRandom((int) graphLastXValue)), true, 15000);
                if (graphLastXValue != 15000) {
                    mHandler.postDelayed(this, 1);
                }
            }
        };
        oneDimensionalGraphRunnable = new Runnable() {
            @Override
            public void run() {
                oneDimensionalGraph.removeAllSeries();
                if(oneDimensionalHistograms.size() > oneDimensionalGraphCounter) {
                    oneDimensionalGraph.addSeries(new LineGraphSeries<>(getDataPoint(oneDimensionalHistograms.get(oneDimensionalGraphCounter))));
                    oneDimensionalGraphCounter++;
                    if (oneDimensionalGraphCounter != 15000) {
                        mHandler.postDelayed(this, 1);
                    }
                }
            }
        };
        mHandler.postDelayed(sampleGraphRunnable, 1500);
        mHandler.postDelayed(oneDimensionalGraphRunnable, 1500);
    }

    public void onPause() {
        super.onPause();
        mHandler.removeCallbacks(sampleGraphRunnable);
    }

    public double getRandom(int index) {
        return weibullDistributionArray[index];
    }
}
