package com.example.praveenagrawal.wifiscan;

import android.Manifest;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

import static android.os.Build.VERSION_CODES.M;

public class WifiList extends AppCompatActivity {
    WifiManager wifi;
    WifiReceiver wifiAdapter;
    ScanList scanList;
    ArrayList<FeedEntryData> selectList;
    WifiScanDbHelper mDbHelper;
    CheckBox currentCheckbox;
    private SwipeRefreshLayout swipeContainer;
    private final Handler handler = new Handler();
    private FirebaseAnalytics mFirebaseAnalytics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        setContentView(R.layout.activity_wifi_list);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setColorSchemeResources(R.color.colorAccent,R.color.colorPrimary,R.color.formBackground,R.color.colorPrimaryDark);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                wifi.startScan();
                Log.w("Scan","Start Scan");
                Toast.makeText(getBaseContext(),"Scaning Wifi",Toast.LENGTH_SHORT).show();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do something after 5s = 5000ms
                        swipeContainer.setRefreshing(false);
                    }
                }, 3000);
            }
        });
        mDbHelper = new WifiScanDbHelper(this);
        selectList = mDbHelper.getSavedList();
        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (Build.VERSION.SDK_INT >= M && checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
             requestPermissions(new String[] {Manifest.permission.ACCESS_COARSE_LOCATION},0x12345);
        }
        else
        {
            startScan();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0x12345 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            startScan();
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0,0,0,"Refresh").setIcon(R.drawable.refresh).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        wifi.startScan();
        Log.w("Scan","Start Scan");
        Toast.makeText(this,"Scaning Wifi",Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }*/

    @Override
    protected void onPause() {
        if (wifiAdapter != null)
        {
            unregisterReceiver(wifiAdapter);
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        registerReceiver(wifiAdapter, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }
    public int getDpToInt(float value)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }

    public void startScan()
    {
        if (wifi.isWifiEnabled() == false)
        {
            wifi.setWifiEnabled(true);
        }
        wifiAdapter = new WifiReceiver();
        scanList = new ScanList();
        registerReceiver(wifiAdapter, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        registerReceiver(scanList,new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        //app = new App();
        wifi.startScan();
    }

    public void createWifiList(ArrayList<String> wifiList)
    {
        if (wifiList.size() > 0) {
            // got the wifilist;
            Log.w("wifi count", "" + wifiList.size());
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.activity_wifi_list);
            linearLayout.removeAllViewsInLayout();
            for (int i = 0; i < selectList.size(); i++)
            {
                CheckBox checkBox = getCheckBox(selectList.get(i).ssid);
                checkBox.setChecked(true);
                RelativeLayout subView = new RelativeLayout(this);
                subView.setPadding(0,0,0,getDpToInt(8));
                subView.addView(checkBox);
                ImageView iconMore = getIcon(null,1001+i);
                iconMore.setImageResource(R.drawable.ic_more);
                iconMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        registerForContextMenu(view);
                        view.showContextMenu();
                        unregisterForContextMenu(view);
                    }
                });
                subView.addView(iconMore);
                ImageView iconToggle = getIcon(iconMore,1002+i);
                subView.addView(iconToggle);
                ImageView iconTimes = getIcon(iconToggle,1003+i);
                iconTimes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      timeIconClicked(v);
                    }
                });
                subView.addView(iconTimes);
                linearLayout.addView(subView);
                View space = new View(this);
                space.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,getDpToInt(1)));
                space.setBackgroundColor(0xFFA9A9A9);
                linearLayout.addView(space);
                switch (selectList.get(i).type)
                {
                    case "1":
                        iconToggle.setImageResource(R.drawable.ic_silent);
                        iconToggle.setTag("1");
                        break;
                    case "2":
                        iconToggle.setImageResource(R.drawable.ic_vibration);
                        iconToggle.setTag("2");
                        break;
                    case "3":
                        iconToggle.setImageResource(R.drawable.ic_normal);
                        iconToggle.setTag("3");
                        break;
                }
                if (selectList.get(i).isTime.compareToIgnoreCase("true") == 0)
                {
                    iconTimes.setImageResource(R.drawable.ic_timer);
                    iconTimes.setTag("true");

                }
                else
                {
                    iconTimes.setImageResource(R.drawable.ic_timer_off);
                    iconTimes.setTag("false");
                }
            }
            for (int i = 0; i < wifiList.size(); i++)
            {
                CheckBox checkBox = getCheckBox(wifiList.get(i));
                RelativeLayout subView = new RelativeLayout(this);
                subView.setPadding(0,0,0,getDpToInt(8));
                subView.addView(checkBox);
                ImageView iconMore = getIcon(null,2001+1);
                iconMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        registerForContextMenu(view);
                        view.showContextMenu();
                        unregisterForContextMenu(view);
                    }
                });
                subView.addView(iconMore);
                ImageView iconToggle = getIcon(iconMore,2002+1);
                iconToggle.setTag("2");
                subView.addView(iconToggle);
                ImageView iconTimes = getIcon(iconToggle,2003+1);
                iconTimes.setTag("false");
                iconTimes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        timeIconClicked(v);
                    }
                });
                subView.addView(iconTimes);
                linearLayout.addView(subView);
                View space = new View(this);
                space.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,getDpToInt(1)));
                space.setBackgroundColor(0xFFA9A9A9);
                linearLayout.addView(space);
            }
        }
    }

    public CheckBox getCheckBox(String title)
    {

        final CheckBox checkBox = new CheckBox(this);
        checkBox.setText(title);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.rightMargin = getDpToInt(80);
        params.topMargin = getDpToInt(6);
        params.leftMargin = getDpToInt(8);
        checkBox.setTextSize(24);
        checkBox.setLayoutParams(params);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBox.isChecked())
                {
                    RelativeLayout subView = (RelativeLayout) checkBox.getParent();
                    ImageView iconMore = (ImageView) subView.getChildAt(1);
                    iconMore.setImageResource(R.drawable.ic_more);
                    ImageView iconToggle = (ImageView) subView.getChildAt(2);
                    iconToggle.setImageResource(R.drawable.ic_vibration);
                    ImageView iconTimer = (ImageView) subView.getChildAt(3);
                    iconTimer.setImageResource(R.drawable.ic_timer_off);
                    mDbHelper.addSSID((String) checkBox.getText());
                    subView.invalidate();
                }
                else
                {
                    RelativeLayout subView = (RelativeLayout) checkBox.getParent();
                    ImageView iconMore = (ImageView) subView.getChildAt(1);
                    iconMore.setImageDrawable(null);
                    ImageView iconToggle = (ImageView) subView.getChildAt(2);
                    iconToggle.setImageDrawable(null);
                    ImageView iconTimer = (ImageView) subView.getChildAt(3);
                    iconTimer.setImageDrawable(null);
                    mDbHelper.removeSSID((String) checkBox.getText());
                    subView.invalidate();
                }
            }
        });
        return checkBox;
    }

    public ImageView getIcon(ImageView rightIcon, int iconId)
    {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (rightIcon == null)
        {
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        }
        else
        {
            params.addRule(RelativeLayout.LEFT_OF , rightIcon.getId());
            params.rightMargin = getDpToInt(8);
        }
        params.topMargin = getDpToInt(6);
        final ImageView icon = new ImageView(this);
        icon.setId(iconId);
        icon.setLayoutParams(params);
        return  icon;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        RelativeLayout subView = (RelativeLayout) v.getParent();
        currentCheckbox = (CheckBox) subView.getChildAt(0);
        ImageView iconTime = (ImageView) subView.getChildAt(3);
        if (currentCheckbox.isChecked())
        {
            if (iconTime.getTag().equals("true"))
            {
                menu.add(0,1003,0,"Show Time Entries");
            }
            super.onCreateContextMenu(menu, v, menuInfo);
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.context_menu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        RelativeLayout subView = (RelativeLayout) currentCheckbox.getParent();
        ImageView icon = (ImageView) subView.getChildAt(2);
        switch (item.getItemId())
        {
            case R.id.silent:
                NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && !notificationManager.isNotificationPolicyAccessGranted()) {

                    Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                    startActivity(intent);
                }
                else
                {
                    icon.setImageResource(R.drawable.ic_silent);
                    icon.setTag("1");
                    mDbHelper.updateType(currentCheckbox.getText().toString() , "1");
                    Log.w("item menu" , "silent");
                }
                return true;
            case R.id.vibration:
                icon.setImageResource(R.drawable.ic_vibration);
                icon.setTag("2");
                mDbHelper.updateType(currentCheckbox.getText().toString() , "2");
                Log.w("Item menu", "vibration");
                return true;
            case R.id.ring:
                icon.setImageResource(R.drawable.ic_normal);
                icon.setTag("3");
                mDbHelper.updateType(currentCheckbox.getText().toString() , "3");
                Log.w("Item menu", "Normal");
                return true;
            case 1003:
                Toast.makeText(this,"Opening Time Entry",Toast.LENGTH_SHORT).show();
                Intent timeListActivity = new Intent(this, TimeListActivity.class);
                Bundle b = new Bundle();
                b.putString("ssid",(String) currentCheckbox.getText());
                timeListActivity.putExtras(b);
                startActivity(timeListActivity);
                return true;
        }

        return super.onContextItemSelected(item);
    }

    public void timeIconClicked( View timeIcon)
    {
        RelativeLayout subView = (RelativeLayout) timeIcon.getParent();
        CheckBox checkBox = (CheckBox) subView.getChildAt(0);
        ImageView iconTime = (ImageView) subView.getChildAt(3);
        if (iconTime.getTag() == "true")
        {
            Toast.makeText(getBaseContext(),"Time tracking is off",Toast.LENGTH_SHORT).show();
            iconTime.setImageResource(R.drawable.ic_timer_off);
            iconTime.setTag("false");
            mDbHelper.updateTime(checkBox.getText().toString() , "false");
        }
        else
        {
            Toast.makeText(getBaseContext(),"Time tracking is on",Toast.LENGTH_SHORT).show();
            iconTime.setImageResource(R.drawable.ic_timer);
            iconTime.setTag("true");
            mDbHelper.updateTime(checkBox.getText().toString() , "true");
        }
    }

    public class ScanList extends BroadcastReceiver
    {

        public void onReceive(Context c, Intent intent)
        {
            Log.w("Scan","receive Scan");
            ArrayList<String> wifiList = new ArrayList<String>();
            List<ScanResult> scanResultList = wifi.getScanResults();
            selectList = mDbHelper.getSavedList();
            Log.w("Scan","" + scanResultList.size());
            for (int i = 0; i < scanResultList.size(); i++)
            {
                if (!scanResultList.get(i).SSID.isEmpty())
                {
                    wifiList.add(scanResultList.get(i).SSID);
                }
            }
            for (int i = 0; i < wifiList.size(); i++)
            {
                String name = wifiList.get(i).trim();
                for (int j = i+1; j < wifiList.size(); j++)
                {
                    if (name.equals(wifiList.get(j).trim()))
                    {
                        wifiList.remove(j);
                        j--;
                    }
                }
            }
            for (int i = 0; i < selectList.size(); i++)
            {
                String savedSSID = selectList.get(i).ssid;
                for (int j = 0; j < wifiList.size(); j++)
                {
                    if (savedSSID.equals(wifiList.get(j)))
                    {
                        wifiList.remove(j);
                    }
                }
            }
            createWifiList(wifiList);
        }
    }

}
