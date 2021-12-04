package com.a.rh_wifi;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity2 extends AppCompatActivity {
    private static String NTPTime = null;
    private static String NTPDate = null;
    private TextView batteryTxt;
    TextView NetworkMCCMNC, MCCMNCSIMView, CurrenDateView, NTPDateView, OffsetTimeView, PositionView;
    ImageView imageView, Reload, CreateFile, ReadFile;
    String CurrentDate = null, Offset = null, CureentTime = null, data="";
    public static final String TIME_SERVER = "time-a.nist.gov";
    ProgressBar ProgressBar;
    Wifidata wifidata = new Wifidata();
    String TAG = "PhoneActivityTAG";
    String WantPermission = Manifest.permission.READ_PHONE_STATE;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private double longitude;
    private double latitude;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Reload = findViewById(R.id.Reload);
        ProgressBar = findViewById(R.id.progressBar2);
        ProgressBar.setVisibility(View.VISIBLE);
        ReadFile = findViewById(R.id.readfile);
        CreateFile = findViewById(R.id.writeFile);
        NetworkMCCMNC = findViewById(R.id.ValueMNCMCCNET);

        try {
            Thread.sleep(2000);
            ProgressBar.setVisibility(View.INVISIBLE);
            Reload.setVisibility(View.VISIBLE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        PositionView = findViewById(R.id.LongitudeValue);
        MCCMNCSIMView = findViewById(R.id.ValueMNCMCCSIM);
        CurrenDateView = findViewById(R.id.CurrentTIme);
        NTPDateView = findViewById(R.id.NTPTIme);
        OffsetTimeView = findViewById(R.id.offset);

        imageView = findViewById(R.id.imageView);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.data);


        getPosition();


        Reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(getIntent());
                Reload.setVisibility(View.VISIBLE);
                try {
                    Thread.sleep(2000);
                    ProgressBar.setVisibility(View.INVISIBLE);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
        try {
            LongRunningTask.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        GetNTPTime();
                        Time();
                        GetOffset(CureentTime, NTPTime);
                        CurrenDateView.setText("Current Time : " + CurrentDate);
                        NTPDateView.setText("NTPTime : " + NTPDate);
                        OffsetTimeView.setText(" Offset:  " + Offset);

                        TelephonyManager tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                        String networkOperator = tel.getNetworkOperator();
                        if (networkOperator != null) {
                            int mcc = Integer.parseInt(networkOperator.substring(0, 3));
                            int mnc = Integer.parseInt(networkOperator.substring(3));
                            NetworkMCCMNC.setText("MNC: " + mnc + "   " + "MCC: " + mcc);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            Log.e(" time exception", "exception time " + e);
        }


        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo[] NetInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : NetInfo) {

            if (ni.getTypeName().equalsIgnoreCase("WIFI")) {
                String WifiData = wifidata.WifiName(getApplicationContext());
                if (ni.isConnected()) {
                    WIFI(WifiData, linearLayout);
                    imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_wifi_24));
                }

            } else if (ni.getTypeName().equalsIgnoreCase("MOBILE")) {
                if (ni.isConnected()) {
                    if (!checkPermission()) {
                        requestPermission(WantPermission);
                    } else {
                        try {
                            SIM(linearLayout, getPhone());
                            imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_orange_telecom));
                            getMCCMNCNET();
                        } catch (Exception e) {
                            Log.d(TAG, "Exception SIM INFO " + e);
                        }

                    }

                }
            }


        }

        batteryTxt = findViewById(R.id.batteryTxt);
        this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));


        CreateFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MyFolder/";
                    File root = new File(rootPath);
                    if (!root.exists()) {
                        root.mkdirs();
                    }
                    Date GetCurrentTime = Calendar.getInstance().getTime();
                    String Date = DateFormat.format("yyyy_MM_dd_HH_mm_ss", GetCurrentTime).toString();
                    File f = new File(rootPath + "RH_Network" + "_" + Date + "_" + "Report.csv");
                    if (!f.exists()) {
                        f.createNewFile();
                        Toast.makeText(getApplicationContext(), "File 'Report' created ", Toast.LENGTH_LONG).show();
                    }
                    FileOutputStream out = new FileOutputStream(f);
                    out.flush();
                    out.close();
                    try {
                        writeFileOnInternalStorage("RH_Network" + "_" + Date + "_" + "Report.csv", data);
                        Toast.makeText(getApplicationContext(), " Data added  with success  ", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


        ReadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    File path = new File(Environment.getExternalStorageDirectory() + "/MyFolder/");
                    Intent intent = getPackageManager().getLaunchIntentForPackage("com.sec.android.app.myfiles");
                    intent.setAction("samsung.myfiles.intent.action.LAUNCH_MY_FILES");
                    intent.putExtra("samsung.myfiles.intent.extra.START_PATH", path.getAbsolutePath());
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(MainActivity2.this, "Please add data to your file " + e, Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            float batteryPct = level * 100 / (float) scale;
            batteryTxt.setText(String.valueOf(batteryPct) + "%");
            data = data + " Battery Level: " +batteryPct+ "\n";
        }
    };

    void Time() {
        Date GetCurrentTime = Calendar.getInstance().getTime();
        CureentTime = DateFormat.format(" HH:mm:ss", GetCurrentTime).toString();
        String Date = DateFormat.format("yyyy-MM-dd HH:mm:ss", GetCurrentTime).toString();
        CurrentDate = Date;
        data = data + " CurrentDate: " +CurrentDate+ "\n";
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void GetNTPTime() throws IOException {
        NTPUDPClient timeClient = new NTPUDPClient();
        InetAddress inetAddress = InetAddress.getByName(TIME_SERVER);
        TimeInfo timeInfo = timeClient.getTime(inetAddress);
        Calendar cal = Calendar.getInstance(Locale.FRENCH);
        cal.setTimeInMillis(timeInfo.getMessage().getReceiveTimeStamp().getTime());
        NTPTime = DateFormat.format("HH:mm:ss", cal).toString();
        String Date = DateFormat.format("yyyy-MM-dd HH:mm:ss", cal).toString();
        NTPDate = Date;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    void GetOffset(String CurrentDate, String FinalDate) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
            Date date12 = (Date) format.parse(CurrentDate);
            Date date22 = (Date) format.parse(FinalDate);
            //time difference in milliseconds
            long timeDiff = Math.abs(date22.getTime() - date12.getTime());
            //new date object with time difference
            Date diffDate = new Date(timeDiff);
            //formatted date string
            SimpleDateFormat newformat = new SimpleDateFormat("mm:ss");
            Offset = newformat.format(diffDate);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e(" time exception", "exception getOffset " + e);
        }

    }


    void WIFI(String string, LinearLayout linearLayout) {
        String[] parts = string.split(",");
        Date GetCurrentTime = Calendar.getInstance().getTime();
        String Date = DateFormat.format("yyyy-MM-dd HH:mm:ss", GetCurrentTime).toString();
        for (String part : parts) {
            TextView tv = new TextView(getApplicationContext());
            tv.setGravity(Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK);
            tv.setText(part);
            tv.setBackground(ContextCompat.getDrawable(this, R.drawable.common_google_signin_btn_icon_light_normal_background));
            tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            linearLayout.addView(tv);

            data =  part + "\n";
        }
    }


    void SIM(LinearLayout linearLayout, ArrayList<String> lst) {

        for (String part : lst) {
            TextView tv = new TextView(getApplicationContext());
            tv.setGravity(Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK);
            tv.setText(String.valueOf(part));
            tv.setBackground(ContextCompat.getDrawable(this, R.drawable.common_google_signin_btn_icon_light_normal_background));
            tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            data = data + part+ "\n";
            linearLayout.addView(tv);

        }
    }


    @TargetApi(Build.VERSION_CODES.O)
    private ArrayList<String> getPhone() {
        TelephonyManager phoneMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        GsmCellLocation cellLocation = (GsmCellLocation)phoneMgr.getCellLocation();
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), WantPermission) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        ArrayList<String> _lst = new ArrayList<>();
        switch (phoneMgr.getDataNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                _lst.add("DataNetworkType : " + "2G");
                break;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                _lst.add("DataNetworkType : " + "3G");
                break;
            case TelephonyManager.NETWORK_TYPE_LTE:
                _lst.add("DataNetworkType : " + "4G");
                break;
            default:
                _lst.add("DataNetworkType : " + "5G");
                break;
        }
        _lst.add("CallState  : " + (phoneMgr.getCallState()));
        _lst.add("IMEI NUMBER : " + phoneMgr.getImei());
        _lst.add("CID : " + cellLocation.getCid());
        _lst.add("LAC: " + cellLocation.getLac());
        _lst.add("MOBILE NUMBER : " + phoneMgr.getLine1Number());
        _lst.add("SERIAL NUMBER : " + phoneMgr.getSimSerialNumber());
        _lst.add("SIM OPERATOR NAME : " + phoneMgr.getSimOperatorName());
        _lst.add("MEI NUMBER : " + phoneMgr.getMeid());
        _lst.add("SIM STATE : " + (phoneMgr.getSimState()));
        _lst.add("COUNTRY ISO : " + phoneMgr.getSimCountryIso());
        _lst.add("SoftwareVersion : " + phoneMgr.getDeviceSoftwareVersion());
        _lst.add("PhoneType : " + phoneMgr.getPhoneType());
        _lst.add("CID : " + cellLocation.getCid());
        _lst.add("LAC: " + cellLocation.getLac());
        return _lst;
    }


    private void requestPermission(String permission) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            Toast.makeText(this, "Phone state permission allows us to get phone number. Please allow it for additional functionality.", Toast.LENGTH_LONG).show();
        }
        ActivityCompat.requestPermissions(this, new String[]{permission}, PERMISSION_REQUEST_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(this, "Permission Denied. We can't get phone number.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }


    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int result = ContextCompat.checkSelfPermission(this, WantPermission);
            if (result == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }


    public void writeFileOnInternalStorage(String sFileName, String sBody) {
        String rootPath = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/MyFolder/";
        File root = new File(rootPath);
        if (!root.exists()) {
            root.mkdirs();
        }
        try {
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


   /* void Alert(StringBuilder Message, Context context) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Report: ");
        alertDialog.setMessage(Message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }*/

    void getPosition() {


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationManager myLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        MyLocation myLocation = new MyLocation();


        MyLocation.LocationResult locationResult = new MyLocation.LocationResult() {

            @Override
            public void gotLocation(Location location) {


                latitude = location.getLatitude();
                longitude = location.getLongitude();
                PositionView.setText("latitude: " + latitude + "       " + "Longitude: " + longitude);
                data = data + "latitude: " + latitude  + "\r" + "Longitude: " + longitude + "\n"  ;
            }
        };
        myLocation.getLocation(getApplicationContext(), locationResult);
    }

    void getMCCMNCNET() {

        TelephonyManager phoneMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), WantPermission) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // int mcc = Integer.parseInt(Integer.valueOf(phoneMgr.getSubscriberId()).substring(0, 3));
        // int mnc = Integer.parseInt(phoneMgr.getSubscriberId().substring(3));
        Log.e("test",phoneMgr.getSubscriberId());
        if ((phoneMgr.getSubscriberId()).length() >0) {
            MCCMNCSIMView.setText("MNC: " + (phoneMgr.getSubscriberId()).substring(3, 5) + "   " + "MCC: " + (phoneMgr.getSubscriberId()).substring(0, 3));
            data = data + "MNC: " + (phoneMgr.getSubscriberId()).substring(3, 5) + "\n" +"MCC: " + (phoneMgr.getSubscriberId()).substring(0, 3) + "\n"  ;
        }



    }
}