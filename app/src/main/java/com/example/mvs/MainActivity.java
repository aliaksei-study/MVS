package com.example.mvs;

import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.apache.commons.math3.distribution.WeibullDistribution;

public class MainActivity extends AppCompatActivity {
    private final Handler mHandler = new Handler();
    private Runnable mTimer;
    private double graphLastXValue = 1024d;
    private LineGraphSeries<DataPoint> series;
    private double[] weibullDistributionArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WeibullDistribution weibullDistribution = new WeibullDistribution(1, 1);
        weibullDistributionArray = weibullDistribution.sample(16384);
        System.out.println(getWeibullNumber(1));
        System.out.println(getWeibullNumber(2));
        System.out.println(getWeibullNumber(3));
        System.out.println(getWeibullNumber(4));
        System.out.println(getWeibullNumber(5));
        System.out.println(getWeibullNumber(6));
        System.out.println(getWeibullNumber(7));
        System.out.println(getWeibullNumber(8));
        System.out.println(getWeibullNumber(9));
        System.out.println(getWeibullNumber(10));
        System.out.println(getWeibullNumber(11));
        System.out.println(getWeibullNumber(12));
        System.out.println(getWeibullNumber(13));
        System.out.println(getWeibullNumber(14));

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

    public double getWeibullNumber(int x) {
        return (5) * Math.pow(1, 4) * Math.exp(-(Math.pow(x, 5)));
    }
}
