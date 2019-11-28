package com.volvo.BoardSystemMonitor;

import androidx.appcompat.app.AppCompatActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.ScanResult;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
    ArrayAdapter<String> arrayAdapter;
    Button wifiDiscoverBtn;
    ListView lv;
    TextView RAMavailable;
    TextView CPUusage;
    private static String recieved = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wifiDiscoverBtn = (Button) findViewById(R.id.wifiDiscoverBtn);
        lv = (ListView) findViewById(R.id.lv);
        RAMavailable = (TextView) findViewById(R.id.RAMavailable);
        CPUusage = (TextView) findViewById(R.id.CPUusage);
        wifi = (WifiManager)
                getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (!wifi.isWifiEnabled()) {
            Toast.makeText(getApplicationContext(), "You need to have Wi-fi enabled", Toast.LENGTH_SHORT).show();
            wifi.setWifiEnabled(true);
        }
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

        //Check for permissions
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
                || (ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE)
                != PackageManager.PERMISSION_GRANTED))
        {

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

        wifiDiscoverBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Scanning....", Toast.LENGTH_SHORT).show();
                boolean success = wifi.startScan();
                if (!success) {
                    Toast.makeText(getApplicationContext(), "Failure!", Toast.LENGTH_SHORT).show();
                    // scan failure handling
                    scanFailure();
                }
                //Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_SHORT).show();
                scanSuccess();
                arrayAdapter.notifyDataSetChanged();
            }
        });
    }
    private void scanSuccess() {
        results = wifi.getScanResults();
        List<String> resList = new ArrayList<>();

        for (ScanResult result : results) {
            if (result.SSID.length() != 0)
                resList.add(result.SSID);
        }
        arrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, resList);
        lv.setAdapter(arrayAdapter);
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Toast.makeText(getApplicationContext(), ((TextView) arg1).getText(), Toast.LENGTH_SHORT).show();
                if (((TextView) arg1).getText().equals("kbp1-lhp-a00550")) {
                    Toast.makeText(getApplicationContext(), ((TextView) arg1).getText() +" sending request", Toast.LENGTH_SHORT).show();
                    ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                    if (!mWifi.isConnected()) {
                        ConnectToNetworkWPA(((TextView) arg1).getText().toString(), "oorA9S0e");
                        try {
                            //time for ARP get info about gateway MAC
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            System.out.println(e.getLocalizedMessage());
                        }
                    }
                    CClient client = new CClient(getApplicationContext());

                    client.start();
                    //client.Send("GET_SYSTEM_INFO");
                    try {
                        client.join();
                    } catch (InterruptedException e) {
                        System.out.println(e.getLocalizedMessage());
                    }
                    RAMavailable.setText("RAM avaliable: " + recieved);
                    Toast.makeText(getApplicationContext(), "CPU: " + recieved, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void scanFailure() {
        Toast.makeText(getApplicationContext(), "Scanning failed!", Toast.LENGTH_SHORT).show();
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

    public static void setRecieved(String recievedData) {
        recieved = recievedData;
    }
}
