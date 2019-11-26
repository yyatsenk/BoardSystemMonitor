package com.volvo.BoardSystemMonitor;

import androidx.appcompat.app.AppCompatActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.ScanResult;
import android.content.IntentFilter;
import java.util.*;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.util.Log;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    WifiManager wifi;
    List<ScanResult> results;
    int size = 0;
    TextView text;
    ArrayAdapter<String> arrayAdapter;
    Button wifiDiscoverBtn;
    ListView lv;
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
        wifiDiscoverBtn = (Button) findViewById(R.id.wifiDiscoverBtn);
        lv = (ListView) findViewById(R.id.lv);
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

        //text = (TextView) findViewById(R.id.fountField);
        //Check for permissions
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
                || (ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE)
                != PackageManager.PERMISSION_GRANTED))
        {
            //text.setText("Permissions not granted");

            //Request permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.CHANGE_WIFI_STATE,
                            Manifest.permission.ACCESS_NETWORK_STATE,
                            Manifest.permission.INTERNET},
                    123);
        }
        else
           ; //text.setText("Permissions already granted");

        boolean success = wifi.startScan();
        if (!success) {
            Toast.makeText(getApplicationContext(), "Failure!", Toast.LENGTH_SHORT).show();
            // scan failure handling
            scanFailure();
        }
        Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_SHORT).show();
        scanSuccess();

        wifiDiscoverBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Scanning....", Toast.LENGTH_SHORT).show();
                arrayAdapter.notifyDataSetChanged();
            }
        });
    }
    private void scanSuccess() {
        results = wifi.getScanResults();
        //Toast.makeText(getApplicationContext(), "Name = " + results.get(0).SSID, Toast.LENGTH_SHORT).show();
        List<String> resList = new ArrayList<>();

        for (ScanResult result : results) {
            resList.add(result.SSID);
        }
        arrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, resList);
        lv.setAdapter(arrayAdapter);
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Toast.makeText(getApplicationContext(), ((TextView) arg1).getText(), Toast.LENGTH_SHORT).show();
                if (((TextView) arg1).getText().equals("Veta")) {
                    Toast.makeText(getApplicationContext(), "Veta connecting", Toast.LENGTH_SHORT).show();
                    //ConnectToNetworkWPA("Veta", "vetakravchuk26");
                    CClient client = new CClient();
                    Thread t1 =new Thread(client);
                    t1.start();
                }
            }
        });
    }

    private void scanFailure() {
        // handle failure: new scan did NOT succeed
        // consider using old scan results: these are the OLD results!
        results = wifi.getScanResults();
    }

    public boolean ConnectToNetworkWPA( String networkSSID, String password )
    {
        try {
            WifiConfiguration conf = new WifiConfiguration();
            conf.SSID = "\"" + networkSSID + "\"";   // Please note the quotes. String should contain SSID in quotes

            conf.preSharedKey = "\"" + password + "\"";

            conf.status = WifiConfiguration.Status.ENABLED;
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);

            Log.d("connecting", conf.SSID + " " + conf.preSharedKey);

            wifi.addNetwork(conf);

            Log.d("after connecting", conf.SSID + " " + conf.preSharedKey);



            List<WifiConfiguration> list = wifi.getConfiguredNetworks();
            for( WifiConfiguration i : list ) {
                if(i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                    wifi.disconnect();
                    wifi.enableNetwork(i.networkId, true);
                    wifi.reconnect();
                    Log.d("re connecting", i.SSID + " " + conf.preSharedKey);

                    break;
                }
            }

            Toast.makeText(getApplicationContext(), "Successfully connected!", Toast.LENGTH_SHORT).show();
            //WiFi Connection success, return true
            return true;
        } catch (Exception ex) {
            System.out.println(Arrays.toString(ex.getStackTrace()));
            return false;
        }
    }
    public void SendData(String data) {
        CClient client = new CClient();

        //client.Send(data);

    }
}
