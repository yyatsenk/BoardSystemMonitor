package com.volvo.BoardSystemMonitor;

import androidx.appcompat.app.AppCompatActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.net.wifi.ScanResult;
import android.content.IntentFilter;
import java.util.*;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import  android.widget.TextView;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    WifiManager wifi;
    List<ScanResult> results;
    int size = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button wifiDiscoverBtn = (Button) findViewById(R.id.wifiDiscoverBtn);
        wifiDiscoverBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                wifi.startScan();
            }
        });
        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE); //getApplicationContext()
        if (wifi.isWifiEnabled() == false)
        {
            System.out.println("wifi is disabled..making it enabled");
            wifi.setWifiEnabled(true);
        }
        registerReceiver(new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context c, Intent intent)
            {
                results = wifi.getScanResults();
                size = results.size();
                final TextView text = (TextView)findViewById(R.id.fountField);
                text.setText(size + " wifi points found");
            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }
    protected void onClick(View view) {
        ;
    }
}
