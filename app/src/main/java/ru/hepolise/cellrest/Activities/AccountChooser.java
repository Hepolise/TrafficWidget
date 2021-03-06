package ru.hepolise.cellrest.Activities;

import android.app.ListActivity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;

import ru.hepolise.cellrest.R;
import ru.hepolise.cellrest.SettingsActivity;
import ru.hepolise.cellrest.Utils.Utils;
import ru.hepolise.cellrest.TraffWidget;
import ru.hepolise.cellrest.WidgetText;

import static android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE;

/**
 * Created by hepolise on 26.04.17.
 */

public class AccountChooser extends ListActivity {

    String L = "cellLogs";
    Boolean b;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_account_chooser);
        b = false;
        ArrayList<String> values = Utils.genList(getApplicationContext());

        if (values == null) {
            Log.e(L, "null array returned by genList()");
            Intent i;
            if (Utils.checkIntroComplete(getApplicationContext())) {
                i = new Intent(this, SettingsActivity.class);
            } else {
                i = new Intent(this, IntroActivity.class);
            }
            startActivity(i);
        } else {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, values);
            setListAdapter(adapter);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        b = true;
        Log.d(L, "onPause");
    }
    @Override
    public void onResume() {
        super.onResume();
        if (b) {
            Log.d(L, "onResume: b");
            ArrayList<String> values = Utils.genList(getApplicationContext());
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, values);
            setListAdapter(adapter);
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        SharedPreferences sharedPreferences = getSharedPreferences("MainPrefs", MODE_PRIVATE);

        int appWidgetId = getIntent().getIntExtra("id", 0);
        Log.d(L, "widget id (from activity): " + appWidgetId);
        String from = getIntent().getStringExtra("from");
        getIntent().removeExtra("from");
        getIntent().removeExtra("id");

        long ts = sharedPreferences.getLong(Integer.toString(position), 0);
        if (ts == 0) {
            startActivity(new Intent(this, SettingsActivity.class));
            finish();
        }
        sharedPreferences.edit()
                .putLong("widget_id_"+Integer.toString(appWidgetId), ts)
                .commit();
        Intent updateIntent = null;
        if (from.equals("WidgetText")) {
             updateIntent = new Intent(getApplicationContext(), WidgetText.class);
        } else if (from.equals("TraffWidget")) {
            updateIntent = new Intent(getApplicationContext(), TraffWidget.class);
        }

        if (null != updateIntent) {
            updateIntent.setAction(ACTION_APPWIDGET_UPDATE);
            updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
                    new int[]{appWidgetId});
            getApplicationContext().sendBroadcast(updateIntent);
            finish();
        }

    }
}
