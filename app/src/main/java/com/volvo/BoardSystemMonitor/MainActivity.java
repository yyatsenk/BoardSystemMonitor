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
import android.widget.Toast;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    WifiManager wifi;
    List<ScanResult> results;
    int size = 0;
    TextView text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        final Button wifiDiscoverBtn = (Button) findViewById(R.id.wifiDiscoverBtn);
//        text = (TextView) findViewById(R.id.fountField);
//        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        if (wifi.isWifiEnabled() == false)
//        {
//            System.out.println("wifi is disabled..making it enabled");
//            wifi.setWifiEnabled(true);
//        }
//        registerReceiver(new BroadcastReceiver()
//        {
//            @Override
//            public void onReceive(Context c, Intent intent)
//            {
//                results = wifi.getScanResults();
//
//                size = results.size();
//                unregisterReceiver(this);
//                Toast.makeText(getApplicationContext(), "I revieve " + size, Toast.LENGTH_SHORT).show();
//            }
//        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
//        wifi.startScan();
//        wifiDiscoverBtn.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "Scanning....", Toast.LENGTH_SHORT).show();
//                text.setText("debug");
//                try {
//                    text.setText("Size = " + size);
//                } catch (Exception e) {
//                    text.setText("Exeption happen!");
//                }
//            }
//        });
        wifi = (WifiManager)
                getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                boolean success = intent.getBooleanExtra(
                        WifiManager.EXTRA_RESULTS_UPDATED, false);
                if (success) {
                    scanSuccess();
                } else {
                    // scan failure handling
                    scanFailure();
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        getApplicationContext().registerReceiver(wifiScanReceiver, intentFilter);

        boolean success = wifi.startScan();
        if (!success) {
            Toast.makeText(getApplicationContext(), "Failure!", Toast.LENGTH_SHORT).show();
            // scan failure handling
            scanFailure();
        }
        Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_SHORT).show();
        scanSuccess();
    }
    private void scanSuccess() {
        results = wifi.getScanResults();
        Toast.makeText(getApplicationContext(), "Size = " + results.size(), Toast.LENGTH_SHORT).show();
    }

    private void scanFailure() {
        // handle failure: new scan did NOT succeed
        // consider using old scan results: these are the OLD results!
        results = wifi.getScanResults();
    }
}
