package com.dpcat237.nps.helper;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;

import com.dpcat237.nps.ui.widget.PlayerWidgetProvider;

public class WidgetHelper {
    public static void updateWidgets(Context context) {
        AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);

        int[] widgetIds;
        
        widgetIds = widgetManager.getAppWidgetIds(new ComponentName(context, PlayerWidgetProvider.class));
        if (widgetIds.length > 0) {
            AppWidgetProvider provider = new PlayerWidgetProvider();
            provider.onUpdate(context, widgetManager, widgetIds);
        }
    }
}
