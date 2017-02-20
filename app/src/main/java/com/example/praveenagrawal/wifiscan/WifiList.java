package com.example.praveenagrawal.wifiscan;

import android.Manifest;
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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.os.Build.VERSION_CODES.M;

public class WifiList extends AppCompatActivity {
    WifiManager wifi;
    WifiReceiver wifiAdapter;
    ScanList scanList;
    ArrayList<String> selectList;
    FeedReaderDbHelper mDbHelper;
    CheckBox currentCheckbox;
    private final Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_list);
        mDbHelper = new FeedReaderDbHelper(this);
        selectList = mDbHelper.getSavedList();
        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
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

    @Override
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
    }

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
                CheckBox checkBox = getCheckBox(selectList.get(i).split(":")[0]);
                LinearLayout subView = new LinearLayout(this);
                subView.setOrientation(LinearLayout.HORIZONTAL);
                checkBox.setChecked(true);
                subView.addView(checkBox);
                ImageView icon = new ImageView(this);
                icon.setPadding(getDpToInt(8),getDpToInt(6),getDpToInt(8),0);
                switch (selectList.get(i).split(":")[1])
                {
                    case "1":
                        icon.setImageResource(R.drawable.ic_silent);
                        break;
                    case "2":
                        icon.setImageResource(R.drawable.ic_vibration);
                        break;
                    case "3":
                        icon.setImageResource(R.drawable.ic_normal);
                        break;
                }
                subView.addView(icon);
                linearLayout.addView(subView);
            }
            for (int i = 0; i < wifiList.size(); i++)
            {
                CheckBox checkBox = getCheckBox(wifiList.get(i));
                LinearLayout subView = new LinearLayout(this);
                subView.setOrientation(LinearLayout.HORIZONTAL);
                subView.addView(checkBox);
                linearLayout.addView(subView);
            }
        }
    }

    public CheckBox getCheckBox(String title)
    {

        final ImageView icon = new ImageView(this);
        icon.setPadding(getDpToInt(8),getDpToInt(6),getDpToInt(8),0);
        final CheckBox checkBox = new CheckBox(this);
        checkBox.setText(title);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        checkBox.setTextSize(24);
        checkBox.setLayoutParams(params);
        checkBox.setPadding(getDpToInt(8),0,getDpToInt(8),0);
        registerForContextMenu(checkBox);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBox.isChecked())
                {
                    LinearLayout subView = (LinearLayout) checkBox.getParent();
                    icon.setImageResource(R.drawable.ic_vibration);
                    subView.addView(icon);
                    mDbHelper.addSSID((String) checkBox.getText());
                }
                else
                {
                    LinearLayout subView = (LinearLayout) checkBox.getParent();
                    subView.removeViewAt(1);
                    mDbHelper.removeSSID((String) checkBox.getText());
                }
            }
        });
        return checkBox;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        currentCheckbox = (CheckBox) v;
        if (currentCheckbox.isChecked())
        {
            super.onCreateContextMenu(menu, v, menuInfo);
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.context_menu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        LinearLayout subView = (LinearLayout) currentCheckbox.getParent();
        ImageView icon = (ImageView) subView.getChildAt(1);
        switch (item.getItemId())
        {
            case R.id.silent:
                icon.setImageResource(R.drawable.ic_silent);
                mDbHelper.updateType(currentCheckbox.getText().toString() , "1");
                Log.w("item menu" , "silent");
                return true;
            case R.id.vibration:
                icon.setImageResource(R.drawable.ic_vibration);
                mDbHelper.updateType(currentCheckbox.getText().toString() , "2");
                Log.w("Item menu", "vibration");
                return true;
            case R.id.ring:
                icon.setImageResource(R.drawable.ic_normal);
                mDbHelper.updateType(currentCheckbox.getText().toString() , "3");
                Log.w("Item menu", "Normal");
                return true;
        }

        return super.onContextItemSelected(item);
    }

    public class ScanList extends BroadcastReceiver
    {

        public void onReceive(Context c, Intent intent)
        {
            Log.w("Scan","receive Scan");
            ArrayList<String> wifiList = new ArrayList<String>();
            List<ScanResult> scanResultList = wifi.getScanResults();
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
                String savedSSID = selectList.get(i).split(":")[0];
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
