package com.a.rh_wifi;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TextView;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity2 extends AppCompatActivity {
    private static String NTPTime = null;
    private static String NTPDate = null;
    TextView LongitudeView, LatitudeView, CurrenDateView, NTPDateView, OffsetTimeView;
    String CurrentDate = null, Offset = null, CureentTime = null;
    public static final String TIME_SERVER = "time-a.nist.gov";

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        LongitudeView = findViewById(R.id.LongitudeValue);
        LatitudeView = findViewById(R.id.ValueLatitude);
        CurrenDateView = findViewById(R.id.CurrentTIme);
        NTPDateView = findViewById(R.id.NTPTIme);
        OffsetTimeView = findViewById(R.id.offset);

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


}