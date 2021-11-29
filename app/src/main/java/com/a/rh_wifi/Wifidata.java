package com.a.rh_wifi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import static android.app.PendingIntent.getActivity;
import static androidx.core.content.ContextCompat.getSystemService;

public class Wifidata {

  String WifiName(Context context)
   {
        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        String name = wifiInfo.toString();
        return name;
    }


}
