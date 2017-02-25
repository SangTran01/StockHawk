package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.common.base.Splitter;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.R.attr.data;
import static android.R.attr.entries;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static com.udacity.stockhawk.R.id.chart;
import static com.udacity.stockhawk.R.id.symbol;

public class DetailActivity extends AppCompatActivity {
    private final static String LOG_TAG = DetailActivity.class.getSimpleName();

    private String mSymbol;

    private String mHistory;

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Bundle bundle = getIntent().getExtras();
        if (!bundle.isEmpty()) {
            mSymbol = bundle.getString("Symbol");
        }

        Uri currentUri = Contract.Quote.makeUriForStock(mSymbol);
        Cursor c = getContentResolver().query(currentUri,
                null,
                null,
                null,
                null);

        try {
            while (c.moveToNext()) {
                mHistory = c.getString(c.getColumnIndex(Contract.Quote.COLUMN_HISTORY));
            }
        } finally {
            c.close();
        }
        //he create 2 arrays,
        //1 for xaxis
        //and yaxis

        ArrayList<String> xAxes = new ArrayList<>();
        ArrayList<Float> yValuesList = new ArrayList<Float>();


        for (String keyValue : mHistory.split(" *\n *")) {
            String[] pairs = keyValue.split(" *, *", 2);
            //map.put(pairs[0], pairs.length == 1 ? "" : pairs[1]);
            // turn your data into Entry objects

            //DIVIDE by 1000 these numbers are too large to be used as floats
            xAxes.add(pairs[0]);
            yValuesList.add(Float.parseFloat(pairs.length == 1 ? "" : pairs[1]));
        }

        Float[] valuesY = yValuesList.toArray(new Float[0]);

        //sort Date labels
        Collections.sort(xAxes);
        final String[] labels = xAxes.toArray(new String[0]);

        //Test values
//        float[] valuesX = {1487048400, 1487221200, 1487221200};
//        float[] valuesY = {(float) 135.72, (float) 132.12, (float) 129.08};

        // in this example, a LineChart is initialized from xml
        final LineChart chart = (LineChart) findViewById(R.id.chart);

        List<Entry> entries = new ArrayList<Entry>();

        for (int i = 0; i < valuesY.length; i++) {
            entries.add(new Entry((float) i, valuesY[i]));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Prices");
        dataSet.setCircleColor(Color.CYAN); // styling
        dataSet.setDrawCircles(false);
        dataSet.setValueTextColor(Color.YELLOW);
        dataSet.setValueTextSize(10f);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        //Todo try to save position after rotate

        chart.invalidate(); // refresh

        mTextView = (TextView) findViewById(R.id.text);

        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {;
                String xAxisValue = chart.getXAxis().getValueFormatter().getFormattedValue(e.getX(), chart.getXAxis());

                String message = getString(R.string.clicked_stock_info, xAxisValue, String.valueOf(e.getY()));
                mTextView.setText(message);
            }

            @Override
            public void onNothingSelected() {
                mTextView.setText("");
            }
        });

        //Setup markerview
//        CustomMarkerView mv = new CustomMarkerView(this, R.layout.custom_marker_view_layout);
//        chart.setMarker(mv);


        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                String dateInMilli = labels[(int) value];
                Date date = new Date(Long.valueOf(dateInMilli));
                SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yy");
                String dateText = df2.format(date);
                //Log.e(LOG_TAG, "Date " + dateText);
                return dateText;
            }
        };

        XAxis xAxis = chart.getXAxis();
        xAxis.setTextColor(Color.CYAN);
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setGranularityEnabled(true);
        xAxis.setValueFormatter(formatter);

        YAxis yAxisLeft = chart.getAxisLeft();
        yAxisLeft.setTextColor(Color.CYAN);

        YAxis yAxisRight = chart.getAxisRight();
        yAxisRight.setTextColor(Color.CYAN);

    }
}
