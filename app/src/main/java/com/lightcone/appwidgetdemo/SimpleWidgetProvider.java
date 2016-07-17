package com.lightcone.appwidgetdemo;

import android.appwidget.AppWidgetProvider;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class SimpleWidgetProvider extends AppWidgetProvider {

    public static final String TAG = "APP_WIDGET";
    public static int appid[];
    public static RemoteViews rview;
    public static Long refTimeMS;

    // Called to update the AppWidget at intervals defined by the updatePeriodMillis
    // attribute in the AppWidgetProviderInfo. Also called when the user
    // adds the AppWidget (unless a configuration Activity has been declared, in which
    // case it is not called when the the AppWidget is added, but is called for all
    // updates after that).

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds){

        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Log.i(TAG, "onUpdate of SimpleWidgetProvider called");
        // Store following arg for later use
        appid = appWidgetIds;

        // Following shows how to make different parts of the widget clickable, leading
        // to different actions for clicking on different parts. This must be done with a
        // PendingIntent, because the widget is a RemoteViews, hosted by the homescreen.
        // Thus we can't just add a click listener as we would for a normal activity.

        rview = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        // Add click handling for the widget text that will open the main activity when pressed
        Intent launchAppIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingActivity = PendingIntent.getActivity(context, 0,
                launchAppIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        rview.setOnClickPendingIntent(R.id.widget_text, pendingActivity);

        // Add click handling for the widget update icon that will update widgets by starting a
        // Service.  Note that a click on the update icon for one instance of the widget will update
        // all instances if there is more than one instance of the same AppWidget on the
        // homescreen.

        Intent intent = new Intent(context, MyUpdateService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        PendingIntent pendingService = PendingIntent.getService(context,
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        rview.setOnClickPendingIntent(R.id.ImageView01, pendingService);

        // Tell widget manager to update with information about the two click listeners
        appWidgetManager.updateAppWidget(appWidgetIds, rview);

        Log.i(TAG, "Click listeners added in onUpdate");

        // Update the widget displayed content using the Service MyUpdateService
        context.startService(intent);
    }

    // Called when first instance of AppWidget is added to AppWidget host (normally the
    // home screen).

    @Override
    public void onEnabled(Context context){
        super.onEnabled(context);
        Log.i(TAG, "onEnabled of SimpleWidgetProvider called. refTimeMS = "
                +MyUpdateService.refTimeMS);
    }

    // Called each time an instance of the AppWidget is removed from the host

    @Override
    public void onDeleted(Context context, int [] appWidgetId){
        super.onDeleted(context, appWidgetId);
        Log.i(TAG, "Removing instance of AppWidget");
    }

    // Called when last instance of AppWidget is deleted from the AppWidget host.

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Log.i(TAG, "Removing last AppWidget instance.");
    }
}
