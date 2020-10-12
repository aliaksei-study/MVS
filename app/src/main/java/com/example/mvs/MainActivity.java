package com.example.mvs;

import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.apache.commons.math3.distribution.WeibullDistribution;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {
    private final Handler mHandler = new Handler();
    private Runnable mTimer;
    private double graphLastXValue = 1024d;
    private LineGraphSeries<DataPoint> series;
    private LineGraphSeries<DataPoint> series2;
    private double[] weibullDistributionArray;
    private double[] weibullSample = new double[1024];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WeibullDistribution weibullDistribution = new WeibullDistribution(1, 1);
        weibullDistributionArray = weibullDistribution.sample(16384);
        for(int i = 0; i < weibullSample.length; i++) {
            weibullSample[i] = getWeibullNumber();
        }

        GraphView graph = findViewById(R.id.graph);
        series = new LineGraphSeries<>(getDataPoint(weibullDistributionArray));
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(10);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(1024);

        series.setTitle("Распределение Вейбулла");

        graph.addSeries(series);
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);


        Map<Double, Integer> occurences = new TreeMap<>();
        for(Double value: weibullDistributionArray) {
            Integer count = occurences.get(value);
            if(count == null) {
                occurences.put(value, 1);
            } else {
                occurences.put(value, ++count);
            }
        }


        GraphView graph2 = findViewById(R.id.graph2);
        series2 = new LineGraphSeries<>(getDataPoint(occurences));
        graph2.getViewport().setYAxisBoundsManual(true);
        graph2.getViewport().setMinY(0);
        graph2.getViewport().setMaxY(10);

        graph2.getViewport().setXAxisBoundsManual(true);
        graph2.getViewport().setMinX(0);
        graph2.getViewport().setMaxX(16000);

        series2.setTitle("Рандом");

        graph2.addSeries(series2);
        graph2.getLegendRenderer().setVisible(true);
        graph2.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
    }

    private DataPoint[] getDataPoint(Map<Double, Integer> occur) {
        DataPoint[] dataPoints = new DataPoint[occur.size()];
        Iterator<Map.Entry<Double, Integer>> entryIterator = occur.entrySet().iterator();
        for(int i = 0; i < occur.size(); i++) {
            Map.Entry<Double, Integer> entry = entryIterator.next();
            dataPoints[i] = new DataPoint(i, entry.getValue());
        }
        return dataPoints;
    }

    private DataPoint[] getDataPoint(double[] dataArray) {
        DataPoint[] dataPoints = new DataPoint[1024];
        for (int i = 0; i < 1024; i++) {
            dataPoints[i] = new DataPoint(i, dataArray[i]);
        }
        return dataPoints;
    }

    public void onResume() {
        super.onResume();
        mTimer = new Runnable() {
            @Override
            public void run() {
                graphLastXValue += 1d;
                series.appendData(new DataPoint(graphLastXValue, getRandom((int) graphLastXValue)), true, 16250);
                if(graphLastXValue != 16250) {
                    mHandler.postDelayed(this, 1);
                }
            }
        };
        mHandler.postDelayed(mTimer, 1500);
    }

    public void onPause() {
        super.onPause();
        mHandler.removeCallbacks(mTimer);
    }

    public double getRandom(int index) {
        return weibullDistributionArray[index];
    }

    public double getWeibullNumber() {
        Random generator = new Random();
        return Math.sqrt(-5*Math.log(1-generator.nextDouble()));
    }
}
