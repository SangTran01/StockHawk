package com.udacity.stockhawk.widget;

/**
 * Created by Sang Tran on 2/22/2017.
 */

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import static android.R.attr.data;
import static com.udacity.stockhawk.R.id.symbol;

/**
 * If you are familiar with Adapter of ListView,this is the same as adapter
 * with few changes
 */
public class WidgetDataProvider implements RemoteViewsFactory {
    private ArrayList<ListItem> listItemList = new ArrayList<ListItem>();


    private Context context = null;
    private int appWidgetId;

    private final DecimalFormat dollarFormatWithPlus;
    private final DecimalFormat dollarFormat;
    private final DecimalFormat percentageFormat;

    public WidgetDataProvider(Context context, Intent intent) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus.setPositivePrefix("+$");
        percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
        percentageFormat.setMaximumFractionDigits(2);
        percentageFormat.setMinimumFractionDigits(2);
        percentageFormat.setPositivePrefix("+");

        populateListItem();
    }

    @Override
    public void onCreate() {
    }


    /*
    * Called when notifyDataSetChanged() is triggered on the remote adapter.
    * This allows a RemoteViewsFactory
    * to respond to data changes by updating any internal references.
    *
    * */
    @Override
    public void onDataSetChanged() {
        Log.e("DataProvider", "onDataSetChanged called");
        Thread thread = new Thread() {
            public void run() {
                clearData();
            }
        };
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
        }
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public int getCount() {
        return listItemList.size();
    }


    public void populateListItem() {

        Cursor cursor = context.getContentResolver()
                .query(Contract.Quote.URI,
                        null,
                        null,
                        null,
                        Contract.Quote.COLUMN_SYMBOL + " ASC");

        try {
            while (cursor.moveToNext()) {
                ListItem listItem = new ListItem();
                String symbol = cursor.getString(
                        cursor.getColumnIndex(Contract.Quote.COLUMN_SYMBOL));

                float price = cursor.getFloat(
                        cursor.getColumnIndex(Contract.Quote.COLUMN_PRICE));


                float rawAbsoluteChange = cursor.getFloat(
                        cursor.getColumnIndex(Contract.Quote.COLUMN_ABSOLUTE_CHANGE));

                float percentageChange = cursor.getFloat(
                        cursor.getColumnIndex(Contract.Quote.COLUMN_PERCENTAGE_CHANGE));

                listItem.headingSymbol = symbol;
                listItem.contentPrice = price;
                listItem.contentAbsolute = rawAbsoluteChange;
                listItem.contentPercent = percentageChange;

                listItemList.add(listItem);
            }
        } finally {
            cursor.close();
        }
    }

    /*
     *Similar to getView of Adapter where instead of View
     *we return RemoteViews
     *
     */
    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews remoteView = new RemoteViews(
                context.getPackageName(), R.layout.list_row);
        ListItem listItem = listItemList.get(position);

        remoteView.setTextViewText(R.id.headingSymbol, listItem.headingSymbol);
        remoteView.setTextColor(R.id.headingSymbol, Color.GRAY);

        remoteView.setTextViewText(R.id.contentPrice, dollarFormat.format(listItem.contentPrice));
        remoteView.setTextColor(R.id.contentPrice, Color.GRAY);

        remoteView.setTextViewText(R.id.contentChange, dollarFormatWithPlus.format(listItem.contentAbsolute)
                + "/" + percentageFormat.format(listItem.contentPercent));
        remoteView.setTextColor(R.id.contentChange, Color.WHITE);

        if (listItem.contentAbsolute > 0) {
            remoteView.setInt(R.id.contentChange,
                    "setBackgroundResource", R.drawable.percent_change_pill_green);
        } else {
            remoteView.setInt(R.id.contentChange,
                    "setBackgroundResource", R.drawable.percent_change_pill_red);
        }

        Intent fillInIntent = new Intent();


        fillInIntent.putExtra("SYMBOL", symbol);

        remoteView.setOnClickFillInIntent(R.id.widget_item, fillInIntent);

        return remoteView;
    }


    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }


    public void clearData() {
        // clear the data
        listItemList.clear();
        populateListItem();
    }

}
