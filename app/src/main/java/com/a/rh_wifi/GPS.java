package com.a.rh_wifi;

public class GPS {

    /**/
}
/*
* package com.a.rh_wifi;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapView;

import java.util.function.Consumer;

public class MainActivity2 extends AppCompatActivity {
    TextView Longitude, Latitude;
    private static final int REQUEST_LOCATION = 1;
    String loc;
    String lat;
    LocationManager locationManager;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        Longitude = findViewById(R.id.LongitudeValue);
        Latitude = findViewById(R.id.ValueLatitude);
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                OnGPS();
            } else {
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
                locationManager.getCurrentLocation(LocationManager.GPS_PROVIDER, null, MainActivity2.this.getApplicationContext().getMainExecutor(), new Consumer<Location>() {
                    @Override
                    public void accept(Location location) {
                        loc = String.valueOf(location.getLongitude());
                        lat = String.valueOf(location.getAltitude());
                        Log.e("war", "hhhhhhhhhhh" + loc + "hhhhh" + lat);
                    }
                });
                Longitude.setText(String.valueOf(loc));
                Latitude.setText(String.valueOf(lat));
        getLocation(Longitude,Latitude);
    }

}catch (Exception e){
    Log.e("war","err"+e);}


}


    private void OnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", new  DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    @RequiresApi(api = Build.VERSION_CODES.R)
    private void getLocation(final TextView Longitude, final TextView Latitude) {
        if (ActivityCompat.checkSelfPermission(
                MainActivity2.this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                MainActivity2.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }


        else {


/* locationManager.getCurrentLocation(
                    LocationManager.GPS_PROVIDER,
                    null,
                    getApplicationContext().getMainExecutor(),
                    new Consumer<Location>() {
                        @Override
                        public void accept(Location location) {
                            loc= String.valueOf(location.getLongitude());
                           lat= String.valueOf(location.getAltitude());
                            Log.e("war", "hhhhhhhhhhh" + loc + "hhhhh" + lat);
                        }
                    });*/

       /*     if (loc.length()==0  ){
                    Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (locationGPS != null) {
                    double lat = locationGPS.getLatitude();
                    double longi = locationGPS.getLongitude();
                    Longitude.setText(String.valueOf(longi));
                    Latitude.setText(String.valueOf(lat));
                    Log.e("war", "hhhhhhhhhhh" + String.valueOf(lat) + "hhhhh" + String.valueOf(longi));
                    }

                    }



                    }
                    }}*/