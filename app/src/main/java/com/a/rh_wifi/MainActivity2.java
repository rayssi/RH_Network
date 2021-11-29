package com.a.rh_wifi;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.SimpleDateFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.TELECOM_SERVICE;

public class MainActivity2 extends AppCompatActivity {
    private static String NTPTime = null;
    private static String NTPDate = null;
    TextView LongitudeView, LatitudeView, CurrenDateView, NTPDateView, OffsetTimeView;
    ImageView imageView, Reload, CreateFile;
    String CurrentDate = null, Offset = null, CureentTime = null, data;
    public static final String TIME_SERVER = "time-a.nist.gov";
    ProgressBar ProgressBar;
    Wifidata wifidata = new Wifidata();
    String TAG = "PhoneActivityTAG";
    String WantPermission = Manifest.permission.READ_PHONE_STATE;
    private static final int PERMISSION_REQUEST_CODE = 1;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Reload = findViewById(R.id.Reload);
        ProgressBar = findViewById(R.id.progressBar2);
        ProgressBar.setVisibility(View.VISIBLE);

        CreateFile = findViewById(R.id.FIle);
        try {
            Thread.sleep(2000);
            ProgressBar.setVisibility(View.INVISIBLE);
            Reload.setVisibility(View.VISIBLE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LongitudeView = findViewById(R.id.LongitudeValue);
        LatitudeView = findViewById(R.id.ValueLatitude);
        CurrenDateView = findViewById(R.id.CurrentTIme);
        NTPDateView = findViewById(R.id.NTPTIme);
        OffsetTimeView = findViewById(R.id.offset);

        imageView = findViewById(R.id.imageView);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.data);
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
                        } catch (Exception e) {
                            Log.d(TAG, "Exception SIM INFO " + e);
                        }

                    }

                }
            }


        }


        CreateFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    String rootPath = Environment.getExternalStorageDirectory()
                            .getAbsolutePath() + "/MyFolder/";
                    File root = new File(rootPath);
                    if (!root.exists()) {
                        root.mkdirs();
                    }
                    File f = new File(rootPath + "Mesures.txt");
                    // if (f.exists()) {
                    //   f.delete();
                    // f.createNewFile();
                    // }

                    if (!f.exists()) {
                        f.createNewFile();
                        ;
                    }
                    FileOutputStream out = new FileOutputStream(f);
                    out.flush();
                    out.close();

                    try {
                        writeFileOnInternalStorage(getApplicationContext(), "Mesures.txt", data);
                        readFileOnInternalStorage("Mesures.txt",getApplicationContext());

                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }

    void Time() {
        Date GetCurrentTime = Calendar.getInstance().getTime();
        CureentTime = DateFormat.format(" HH:mm:ss", GetCurrentTime).toString();
        String Date = DateFormat.format("yyyy-MM-dd HH:mm:ss", GetCurrentTime).toString();
        CurrentDate = Date;
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

            data = data + Date + "_" + part + "\n";
        }
    }


    void SIM(LinearLayout linearLayout, ArrayList<String> lst) {

        for (String part : lst) {
            TextView tv = new TextView(getApplicationContext());
            tv.setGravity(Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK);
            tv.setText(String.valueOf(part));
            tv.setBackground(ContextCompat.getDrawable(this, R.drawable.common_google_signin_btn_icon_light_normal_background));
            tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            linearLayout.addView(tv);

        }
    }


    @TargetApi(Build.VERSION_CODES.O)
    private ArrayList<String> getPhone() {
        TelephonyManager phoneMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
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
        _lst.add("MOBILE NUMBER : " + phoneMgr.getLine1Number());
        _lst.add("SERIAL NUMBER : " + phoneMgr.getSimSerialNumber());
        _lst.add("SIM OPERATOR NAME : " + phoneMgr.getSimOperatorName());
        _lst.add("MEI NUMBER : " + phoneMgr.getMeid());
        _lst.add("SIM STATE : " + (phoneMgr.getSimState()));
        _lst.add("COUNTRY ISO : " + phoneMgr.getSimCountryIso());
        _lst.add("SoftwareVersion : " + phoneMgr.getDeviceSoftwareVersion());
        _lst.add("getPhoneType : " + phoneMgr.getPhoneType());
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



    public void writeFileOnInternalStorage(Context mcoContext, String sFileName, String sBody) {
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




    public void readFileOnInternalStorage(String sFileName,Context context) {
        String rootPath = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/MyFolder/";
        File root = new File(rootPath);
        if (!root.exists()) {
            root.mkdirs();
        }


        File file = new File(root, sFileName);


        //Read text from file
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');

            }
            br.close();

        } catch (IOException e) {
            //You'll need to add proper error handling here
        }
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
      //  Alert(text,context);
    }
    void Alert(StringBuilder Message, Context context){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage(Message.toString());
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}