package com.example.dikson.prototype21menubars;

import android.util.Log;

public class WifiFingerprints {
//    public String x, y, z;
//    public String timestamp, tag;
    public String bssid, rssi;

    public WifiFingerprints(){

    }

    public WifiFingerprints(String bssid, String rssi){
        this.bssid = bssid;
        this.rssi = rssi;
    }

}
