package com.example.dikson.prototype21menubars;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class WifiActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    WifiManager wifiManager;
    String[] availableWifiList, bssidList;
    String timeStamp;
    Handler handler;
    List<ScanResult> scanResults;
    Runnable runnable;
    EditText input;
    Spinner spinner;
    boolean isEduroamFiltered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);

//Enables the functions of the custom toolbars
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.T_WifiTools);
        setSupportActionBar(toolbar);
//Setting the navigation menu button
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        drawerLayout = findViewById(R.id.DL_WifiMenuBar);

        NavigationView navigationView = findViewById(R.id.NV_WifiLists);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                //Selected item is highlighted
                menuItem.setChecked(true);
                switch (menuItem.getItemId()) {

                    case R.id.I_WifiList:
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.I_F3:
                        Intent F3 = new Intent(WifiActivity.this, MainActivity.class);
                        startActivity(F3);
                        drawerLayout.closeDrawers();
                        break;
                }
                //Closes the drawer when item is selected
                return true;
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        handler = new Handler();
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        scanWifi();
        runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 4000);
                scanWifi();
            }
        };
        handler.post(runnable);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();

        } else
        super.onBackPressed();
    }

    //  To create the overflow Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_view, menu);
        return super.onCreateOptionsMenu(menu);
    }


//  Responding to menu item selections
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        item.setChecked(true);
        switch (item.getItemId()) {
//enables the menu button to be clicked

            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;

            case R.id.I_Comment:

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle("COMMENTS");
                alertDialog.setMessage("Enter Remarks");


                LinearLayout layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.VERTICAL);

                spinner = new Spinner(this);
//                LinearLayout.LayoutParams LL = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
//                spinner.setLayoutParams(LL);
                layout.addView(spinner);

                input = new EditText(this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                layout.addView(input);

                alertDialog.setView(layout);

                alertDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        saveData();
                    }
                });

                Spinner_items();
                alertDialog.create().show();
                return true;

            case R.id.I_Eduroam:
                isEduroamFiltered = true;
                scanWifi();
                return true;

            case R.id.I_ListAvailable:
                isEduroamFiltered = false;

                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    public void Spinner_items(){

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this,R.array.Conditions,android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
    }

    public void scanWifi() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
                Toast.makeText(this, "Enabling Wifi", Toast.LENGTH_SHORT).show();
            } else {
                wifiManager.startScan();
                timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
                scanResults = wifiManager.getScanResults();
                availableWifiList = new String[scanResults.size()];

                if (scanResults.size() == 0) {
                    Toast.makeText(this, "No Access point", Toast.LENGTH_SHORT).show();
                } else {
                    if (!isEduroamFiltered) {
                        noFilter();
                    } else {
                        setFilter();
                    }
                }
            }
        }
    }

    public void listWifi() {
        ListView wifiListView = findViewById(R.id.listView);
        ArrayAdapter<String> arrayAdapter
                = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, availableWifiList);
        wifiListView.setAdapter(arrayAdapter);
    }


    public void noFilter() {
        bssidList = new String[scanResults.size()];

        for (int i = 0; i < scanResults.size(); i++) {
            availableWifiList[i] = ("SSID: " + scanResults.get(i).SSID
                    + "\nBSSID: " + scanResults.get(i).BSSID
                    + "\nRSSI: " + scanResults.get(i).level + "dBm");
        }
        listWifi();
    }

    public void setFilter() {
        int count = 0;
        for (int i = 0; i < scanResults.size(); i++) {
            String wifiSSID = scanResults.get(i).SSID;
            if (wifiSSID.equals("eduroam")) {
                count++;
            }
        }

        availableWifiList = new String[count];
        bssidList = new String[count];
        count = 0;

        for (int i = 0; i < scanResults.size(); i++) {
            String wifiSSID = scanResults.get(i).SSID;
            if (wifiSSID.equals("eduroam")) {
                availableWifiList[count] = ("SSID: " + scanResults.get(i).SSID
                        + "\nBSSID: " + scanResults.get(i).BSSID
                        + "\nRSSI: " + scanResults.get(i).level + "dBm");
                count++;
            }
        }
        listWifi();
    }

    public void saveData() {
        //FYP BOOK
        String getRemark;
        String getSpinner;
        StringBuilder sb = new StringBuilder();
        getRemark = input.getText().toString();
        getSpinner =  spinner.getSelectedItem().toString();


        try {
            FileOutputStream fos = new FileOutputStream(new File(getFilesDir(), "CollectedData.csv"));
            File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            String filePath = file.getAbsolutePath() + "/CollectedData.csv";
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true));

            sb.append(timeStamp + "," + getSpinner + "," + getRemark);
            for (int i = 0; i < bssidList.length; i++) {
                sb.append("," + scanResults.get(i).SSID + " " + scanResults.get(i).BSSID + " " + scanResults.get(i).level);
            }
            sb.append("\n");

            writer.append(sb.toString());
            writer.close();
            Toast.makeText(this, "Data saved", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}

