package com.lightcone.appwidgetdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "APP_WIDGET";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

	/*  This method is invoked by a click on the Request Update button in the main
activity (see the file activity_main.xml for the button definition).  Events are handled
using the android:onClick attribute of the Button rather than by defining an event
handler here. This requires that the method invoked (requestUpdate here)
in the Activity for which activity_main.xml defines the view must be public, and accept
a View as its only parameter. The View passed in to the method references
the object that was clicked. */

    public void requestUpdate(View v) {
        startTheService();
    }

	/*  Manually start the service to update widgets using button in main activity
(if any widget instances exist).  If there is more than one instance of the
AppWidget on the homescreen, this will force an update for all of them. */

    public void startTheService() {
        if (SimpleWidgetProvider.appid == null) {
            Log.i(TAG, "No widget instances to update");
            return;
        }
        Log.i(TAG, "Manually Updating Widgets from button in main activity");
        Intent serviceIntent = new Intent(this, MyUpdateService.class);
        serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, SimpleWidgetProvider.appid);
        this.startService(serviceIntent);
    }

}
