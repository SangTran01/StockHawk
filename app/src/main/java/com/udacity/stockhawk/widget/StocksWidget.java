package com.udacity.stockhawk.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.ui.MainActivity;
import com.udacity.stockhawk.widget.WidgetService;

import static android.R.style.Widget;

public class StocksWidget extends AppWidgetProvider {

    Context context;
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        // Instruct the widget manager to update the widget

        setRemoteAdapter(context, views);

        Intent clickTemplate = new Intent(context, MainActivity.class);

        PendingIntent pendingIntent = TaskStackBuilder.create(context)
                .addNextIntentWithParentStack(clickTemplate)
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        views.setPendingIntentTemplate(R.id.listViewWidget,pendingIntent);

        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId,R.id.listViewWidget);
        appWidgetManager.updateAppWidget(appWidgetId, views);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            Log.e("StocksWidget", "Widget IDs onUpdate calls updateAppWidget" + appWidgetId);
            updateAppWidget(context, appWidgetManager, appWidgetId);

        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private static void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(R.id.listViewWidget,
                new Intent(context, WidgetService.class));
    }


}
