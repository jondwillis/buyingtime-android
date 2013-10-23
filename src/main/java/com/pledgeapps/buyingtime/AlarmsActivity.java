package com.pledgeapps.buyingtime;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.AdapterView;

import com.pledgeapps.buyingtime.data.Alarm;
import com.pledgeapps.buyingtime.data.Alarms;


/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class AlarmsActivity extends Activity {
    ListView alarmList;
    AlarmListAdapter alarmListAdapter;
    private static final int ACTIVITY_EDITALARM=110;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_alarms);
        alarmList = (ListView) findViewById(R.id.alarmList);


        alarmList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                selectAlarm(position);
            }
        });

        alarmListAdapter = new AlarmListAdapter(this);
        alarmList.setAdapter(alarmListAdapter);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        alarmListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause()
    {
        new Thread(new Runnable() {
            public void run() {
                saveData();
            }
        }).start();
        super.onPause();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.alarms, menu);
        return true;
    }

    //onPause
    private void saveData()
    {
        Alarms alarms = Alarms.getCurrent();
        alarms.updateNextAlarmTime();
        alarms.save(getApplicationContext());

        PendingIntent pi = PendingIntent.getBroadcast(this, 0, new Intent(getString(R.string.namespace)), 0);
        AlarmManager am = (AlarmManager)(this.getSystemService( Context.ALARM_SERVICE ));

        for (Alarm a : alarms)
        {
            am.set( AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 20000, pi );
        }

    }

    private void selectAlarm(int alarmIndex)
    {

        Log.d("Test", "Loading alarm");
        Bundle bundle = new Bundle();
        //bundle.putString("SCHEDULE_NAME", scheduleName);
        bundle.putInt("ALARM_INDEX", alarmIndex);
        Intent intent = new Intent(this, AlarmActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, ACTIVITY_EDITALARM);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_alarm:
                selectAlarm(-1);
        }
        return super.onOptionsItemSelected(item);
    }

}
