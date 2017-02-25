package com.udacity.stockhawk.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by Sang Tran on 2/22/2017.
 */


//Just consider this class as the class which tells the ListView
// of appwidget to take what type of data.
// By data meaning what RemoteViewsFactory.
// To make it more simple,if you have done ListView population,
// this class defines the Adapter for the ListView.

public class WidgetService extends RemoteViewsService {
/*
* So pretty simple just defining the Adapter of the listview
* here Adapter is WidgetDataProvider
* */

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetDataProvider(this, intent);
    }

}
