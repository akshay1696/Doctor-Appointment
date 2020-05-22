package com.advitsoft.doctorappointments;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.advitsoft.doctorappointments.model.GPSTrack;
import com.advitsoft.doctorappointments.model.LocationHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
private Location mLastLocation;
        TextView bookappointment;

        double latitude;
        double longitude;
public int getPosition;
        LocationHelper locationHelper;
        GoogleApiClient mGoogleApiClient;
        String currentLocation, strDate, strTime;
        Marker mCurrLocationMarker;

        TextView getaddress;
private GPSTrack gpsTrack;
public static String getlangitude = "", getLattitude = "";
        LocationRequest mLocationRequest;
private GoogleMap mMap;
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
        Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_NETWORK_STATE,
        android.Manifest.permission.CHANGE_NETWORK_STATE,

        };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bookappointment=findViewById(R.id.bookappointment);
        bookappointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainActivity.this,AppointmentActivity.class);
                startActivity(i);
            }
        });
        getaddress = (TextView) findViewById(R.id.city);
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        gpsTrack = new GPSTrack(MainActivity.this);
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    2);

        } else {
            if (isLocationServiceEnabled(MainActivity.this)) {
                getlangitude = gpsTrack.currentLangitude;
                getLattitude = gpsTrack.currentLattitude;
//                latitude = Double.parseDouble(gpsTrack.currentLattitude);
//                longitude = Double.parseDouble(gpsTrack.currentLattitude);
                // MyLog.log("location Details...in first-now--start----", gpsTrack.currentLattitude + "....." + gpsTrack.currentLangitude);
                getAddress();
            } else {
                gpsTrack.showSettingsAlert();
            }
        }
    }
    public void getAddress() {
        //  MyLog.log("getAddresss========== or nor...", "showww........");
        Address locationAddress = null;

        //  locationAddress=locationHelper.getAddress(latitude,longitude);
        if (getLattitude != null && getlangitude != null) {
            latitude = Double.parseDouble(getLattitude);
            longitude = Double.parseDouble(getlangitude);

            // MyLog.log("getAddresss========== or ...", "showww........" + latitude + "==============" + longitude);
            locationAddress = getAddress(latitude, longitude);
            // MyLog.log("getAddresss========== or addreeess...", "......." + locationAddress);
        }
        //.Address[addressLines=[0:"3-6-383, Himayat Nagar Rd, Gagan Mahal, Domalguda, Himayatnagar, Hyderabad, Telangana 500029, India"],feature=3-6-383,admin=Telangana,sub-admin=Hyderabad,locality=Hyderabad,thoroughfare=Himayat Nagar Road,postalCode=500029,countryCode=IN,countryName=India,hasLatitude=true,latitude=17.405168,hasLongitude=true,longitude=78.4809096083865,phone=null,url=null,extras=null]

        if (locationAddress != null) {
            String address = locationAddress.getAddressLine(0);
            String address1 = locationAddress.getAddressLine(1);
            String city = locationAddress.getLocality();
            String state = locationAddress.getAdminArea();
            String country = locationAddress.getCountryName();
            String postalCode = locationAddress.getPostalCode();
            String getSubLocality = locationAddress.getSubLocality();
            String getSubAdminArea = locationAddress.getSubAdminArea();

//            getaddress.setText(getSubLocality);
            if (!TextUtils.isEmpty(address)) {
                currentLocation = address;
                //   MyLog.log("getAddresss========== or nor...", "showww...first....." + currentLocation);

                if (!TextUtils.isEmpty(address1))
                    currentLocation += "\n" + address1;


                if (!TextUtils.isEmpty(city)) {
                    currentLocation += "\n" + city;

                    if (!TextUtils.isEmpty(postalCode))
                        currentLocation += " - " + postalCode;
                } else {
                    if (!TextUtils.isEmpty(postalCode))
                        currentLocation += "\n" + postalCode;

                }

                if (!TextUtils.isEmpty(state))
                    currentLocation += "\n" + state;


                if (!TextUtils.isEmpty(country))
                    currentLocation += "\n" + country;


                //getaddress.setText(currentLocation);

            }

        } else
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        locationHelper.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }

        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);

        }

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (com.google.android.gms.location.LocationListener) this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
            // MyLog.log("called or nor...", "333333........");
        }
        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        // MyLog.log("Location details:....", location.getLatitude() + "..." + location.getLongitude());
        SharedPreferences locationDb = getSharedPreferences("location", 0);
        SharedPreferences.Editor ldb = locationDb.edit();
        ldb.putString("currentlattitude", String.valueOf(location.getLatitude()));
        ldb.putString("currentlangitude", String.valueOf(location.getLongitude()));
        ldb.commit();

        // MyLog.log("showww1...", "666........");
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        getAddress();

        //  MyLog.log("called or nor...", "666........");
        //   MyLog.log("called or nor...", "777........");
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (com.google.android.gms.location.LocationListener) this);
        }

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isLocationServiceEnabled(Context context) {
        LocationManager locationManager = null;
        boolean gps_enabled = false, network_enabled = false;
        if (locationManager == null)
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
//do nothing...
        }

        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
//do nothing...
        }
        return gps_enabled || network_enabled;

    }


    public Address getAddress(double latitude, double longitude) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
        String getSubLocality = "", city = "", knownName = "";
        String getPremises = "";
        String addresstoString = "";
        String getThoroughfare = "";
        String getAdminArea = "";
        String getSubAdminArea = "";
        String postalCode = "";
        String url = "";
        String country = "";
        String getSubThoroughfare = "";
        String getLocality = "";
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()


            knownName = addresses.get(0).getFeatureName();
            getPremises = addresses.get(0).getPremises();
            //addresstoString= addresses.get(0).toString();
            getThoroughfare = addresses.get(0).getThoroughfare();
            getSubThoroughfare = addresses.get(0).getSubThoroughfare();
            getLocality = addresses.get(0).getLocality();
            getSubLocality = addresses.get(0).getSubLocality();
            getAdminArea = addresses.get(0).getAdminArea();
            getSubAdminArea = addresses.get(0).getSubAdminArea();
            country = addresses.get(0).getCountryName();
            postalCode = addresses.get(0).getPostalCode();
            //url=addresses.get(0).getUrl();


            // MyLog.log("getAddresss========== addressfull==.", "......." + address);
           /* MyLog.log("getAddresss========== knownName...","......."+knownName);
            MyLog.log("getAddresss========== getPremises","......."+getPremises);
            MyLog.log("getAddresss========== getLocality","......."+getLocality);
            MyLog.log("getAddresss========== getSubLocality...","......."+getSubLocality);
            MyLog.log("getAddresss========== getAdminArea","......."+getAdminArea);
            MyLog.log("getAddresss========== getSubAdminArea","......."+getSubAdminArea);
            MyLog.log("getAddresss========== getThoroughfare","......."+getThoroughfare);
            MyLog.log("getAddresss========== getSubThoroughfare,......",getSubThoroughfare);*/
            //  MyLog.log("getAddresss========== addresstoString","......."+addresstoString);
            /*String check=country;
            if (!check.equals("")||!check.equals("null")){
                MyLog.log("getAddresss========== country","......."+country);
            }

            MyLog.log("getAddresss========== postalCode","......."+postalCode);*/
            if (getSubLocality != null) {
                getaddress.setText(getSubLocality + " " + getLocality);
            } else {
                getaddress.setText(getLocality);
            }


           /* if (getSubLocality.equals("")||getSubLocality.equals("null")){
                if (!knownName.equals("null")) {
                    getaddress.setText(knownName + " " + city);
                }else{
                    getaddress.setText(city);
                }

            }else{
                if (!knownName.equals("null")) {
                    getaddress.setText(knownName+" "+getSubLocality);
                }else{
                    getaddress.setText(city);
                }

            }*/
            return addresses.get(0);


        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }
    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        new AlertDialog.Builder(this).setIcon(android.R.drawable.alert_light_frame).setTitle("Exit")
                .setMessage("Do you want to close?").setIcon(R.drawable.logodctr)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
                        System.exit(0);
                    }
                }).setNegativeButton("No", null).show();
    }
}
