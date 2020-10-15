package com.example.mvs;

import android.os.Bundle;
import android.os.Handler;

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
    private double graphLastXValue = 1024d;
    private LineGraphSeries<DataPoint> sampleGraphSeries;
    private double[] weibullDistributionArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WeibullDistribution weibullDistribution = new WeibullDistribution(1, 1);
        weibullDistributionArray = weibullDistribution.sample(16384);

        GraphView sampleGraph = findViewById(R.id.sample_graph);
        sampleGraphSeries = new LineGraphSeries<>(getDataPoint(weibullDistributionArray, 1024));
        sampleGraph.addSeries(sampleGraphSeries);
        sampleGraphSeries.setTitle("Распределение Вейбулла");
        configureGraph(sampleGraph, 0, 0, 1024, 10);

        GraphView oneDimensionalGraph = findViewById(R.id.one_dimensional_graph);
        LineGraphSeries<DataPoint> oneDimensionalSeries = new LineGraphSeries<>(getDataPoint(getListOfOneDimensionalHistogramValues(weibullDistributionArray, 0, 256)));
        oneDimensionalGraph.addSeries(oneDimensionalSeries);
        oneDimensionalSeries.setTitle("Одномерная гистограмма");
        configureGraph(oneDimensionalGraph, 0, 0, 256, 40);
    }

    private List<Integer> getListOfOneDimensionalHistogramValues(double[] weibullDistributionArray, int firstBorder, int secondBorder) {
        int frequency;
        int listIndex;
        List<Integer> occurences = new ArrayList<>(Collections.nCopies(secondBorder - firstBorder, 0));
        for(int i = firstBorder; i < secondBorder; i++) {
            listIndex = (int)(weibullDistributionArray[i] * 10);
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
        for(int i = 0; i < histogramValues.size(); i++) {
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
                sampleGraphSeries.appendData(new DataPoint(graphLastXValue, getRandom((int) graphLastXValue)), true, 16383);
                if(graphLastXValue != 16383) {
                    mHandler.postDelayed(this, 1);
                }
            }
        };
        mHandler.postDelayed(sampleGraphRunnable, 1500);
    }

    public void onPause() {
        super.onPause();
        mHandler.removeCallbacks(sampleGraphRunnable);
    }

    public double getRandom(int index) {
        return weibullDistributionArray[index];
    }
}
