package com.lightcone.appwidgetdemo;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

public class MyUpdateService extends Service {

    public static final String TAG = "APP_WIDGET";
    private BackgroundThread background;
    private CharSequence updateText;
    private String titleText;
    private String units;
    public Intent savedIntent;
    public static long refTimeMS;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        background.interrupt();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i(TAG, "Starting the AppWidget update service");
        savedIntent = intent;
        doThreadStart();
        // Want this service to continue running until explicitly stopped, so return sticky.
        return START_STICKY;
    }

    // Method to update the string displayed in the AppWidget.  Get the system time,
    // subtract it from the target time, and put the results with appropriate units into
    // a CharSequence and return it.

    private CharSequence returnUpdate(int id) {
        Long milliseconds = System.currentTimeMillis();
        SharedPreferences sp = getApplicationContext().getSharedPreferences("prefs", 0);
        long refTimeMS = sp.getLong("targetDate_" + id, 0);
        titleText = sp.getString("title_" + id, "");
        units = sp.getString("units_" + id, "");
        Log.i(TAG, "  Service: refTimeMS=" + refTimeMS);
        int timeTil = 0;
        if (units.compareTo("Minutes") == 0) {
            Log.i(TAG, "  Units are minutes");
            timeTil = (int) ((refTimeMS - milliseconds) / 60000);
        } else if (units.compareTo("Hours") == 0) {
            Log.i(TAG, "  Units are Hours");
            timeTil = (int) ((refTimeMS - milliseconds) / 3600000);
        } else if (units.compareTo("Days") == 0) {
            Log.i(TAG, "  Units are Days");
            timeTil = (int) ((refTimeMS - milliseconds) / 86400000);
        }
        CharSequence updateText = timeTil + " " + units;
        Log.i(TAG, "  returnUpdate: Update Text = " + updateText);
        return updateText;
    }

    // We won't bind anything to our service, so just return null for onBind
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // Start the background thread to do the widget update as an instance of
    // the class BackgroundThread
    private void doThreadStart() {
        background = new BackgroundThread();
        background.start();
    }

    // Class to run background thread for updates.  Not essential for this example since
    // the update is very fast, but good general practice if the update involves
    // blocking operations like web access because otherwise this would run on
    // the main UI thread.  We override the run() method inherited from Thread to
    // perform the update.

    private class BackgroundThread extends Thread {
        public void run() {
            Log.i(TAG, "  Begin background thread");
            updateText = "";
            // Retrieve a widget manager and get the IDs of any widget instances from the
            // intent that started this service
            AppWidgetManager apw = AppWidgetManager.getInstance(getApplicationContext());
            int[] ids = savedIntent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
            if (ids.length > 0) {
                // Loop through all widget instances and update
                final int num = ids.length;
                for (int i = 0; i < num; i++) {
                    int id = ids[i];
                    updateText = returnUpdate(id);
                    Log.i(TAG, "  Update App id = " + id + ", Value = " + updateText);
                    // Construct remote view with updated time until event
                    RemoteViews rview = new RemoteViews(getPackageName(), R.layout.widget_layout);
                    rview.setTextViewText(R.id.widget_title, titleText);
                    rview.setTextViewText(R.id.widget_text, updateText);
                    // Tell the widget manager to update with the new info
                    apw.updateAppWidget(id, rview);
                }
                // This update is now finished, so stop the service
                Log.i(TAG, "Stopping the AppWidget update service");
                stopSelf();
            }
        }
    }
}
