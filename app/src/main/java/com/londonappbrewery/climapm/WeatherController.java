package com.londonappbrewery.climapm;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class WeatherController extends AppCompatActivity {

    private static final int REQUEST_CODE = 123 ;
    // Constants:
    final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";
    // App ID to use OpenWeather data
    final String APP_ID = "08c06f805926d424322862caf5f64232";
    // Time between location updates (5000 milliseconds or 5 seconds)
    final long MIN_TIME = 5000;
    // Distance between location updates (1000m or 1km)
    final float MIN_DISTANCE = 1000;

    // TODO: Set LOCATION_PROVIDER here:

    // No corpo da classe
// TODO: Set LOCATION_PROVIDER here:
    String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;
    // pela rede de dados LocationManager.NETWORK_PROVIDER
// Tag de debug
    final String LOGCAT_TAG = "Clima";

    LocationManager mLocationManager;
    LocationListener mLocationListener;


    // Member Variables:
    TextView mTextViewCity;
    ImageView mImageViewSky;
    TextView mTextViewTemperature;

    // TODO: Declare a LocationManager and a LocationListener here:


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_controller_layout);

        final int REQUEST_CODE = 123;
        final String WEATHER_URL =
                "http://api.openweathermap.org/data/2.5/weather";
        // App ID para usar o OpenWeather data
        final String APP_ID = "<api id>";

        // Linking the elements in the layout to Java code
        mTextViewCity = (TextView) findViewById(R.id.locationTV);
        mImageViewSky = (ImageView) findViewById(R.id.weatherSymbolIV);
        mTextViewTemperature = (TextView) findViewById(R.id.tempTV);
        ImageButton changeCityButton = (ImageButton) findViewById(R.id.changeCityButton);


        // TODO: Add an OnClickListener to the changeCityButton here:

        changeCityButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(WeatherController. this, ChangeCityController. class);
                startActivity(myIntent);
            }
        });

    }


    // TODO: Add onResume() here:
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOGCAT_TAG, "onResume() called");
        getWeatherForCurrentLocation();

        super.onResume();
        Log.d(LOGCAT_TAG, "onResume() called");
        Intent myIntent = getIntent();
        String city = myIntent.getStringExtra( "City");
        if(city!=null){
            getWeatherForNewCity(city);
        }else{
            Log.d(LOGCAT_TAG,"Getting weather for current location");
            getWeatherForCurrentLocation();
        }
    }

    private void getWeatherForNewCity(String city) {
    }


    // TODO: Add getWeatherForNewCity(String city) here:


    // TODO: Add getWeatherForCurrentLocation() here:
    private void getWeatherForCurrentLocation() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(LOGCAT_TAG, "onLocationChanged() callback received");
                String longitude = String.valueOf(location.getLongitude());
                String latitude = String.valueOf(location.getLatitude());
                Log.d(LOGCAT_TAG, "longitude is: " + longitude);
                Log.d(LOGCAT_TAG, "latitude is: " + latitude);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                Log.d(LOGCAT_TAG, "onStatusChanged() callback received. Status: " + i);
                Log.d(LOGCAT_TAG, "2 means AVAILABLE, 1: TEMPORARILY_UNAVAILABLE, 0: OUT_OF_SERVICE");
            }

            @Override
            public void onProviderEnabled(String s) {
                Log.d(LOGCAT_TAG, "onProviderEnabled() callback received. Provider: " + s);
            }

            @Override
            public void onProviderDisabled(String s) {
                Log.d("Clima", "onProviderDisabled() callback received");
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE);
            // Para utilizar dados da rede para acessar a localização
            // ActivityCompat.requestPermissions(this,
            return;
        }
        mLocationManager.requestLocationUpdates(LOCATION_PROVIDER, MIN_TIME,MIN_DISTANCE, mLocationListener);

        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("Clima", "onLocationChanged() callback received");
                String longitude = String.valueOf(location.getLongitude());
                String latitude = String.valueOf(location.getLatitude());
                RequestParams params = new RequestParams();
                params.put("lat",latitude);
                params.put("lon", longitude);
                params.put("appid",APP_ID);
                letsDoSomeNetworking(params);
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
        };

    }

    private void letsDoSomeNetworking(RequestParams params) {

        AsyncHttpClient client = new AsyncHttpClient();
        client.get( WEATHER_URL,params,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess( int statusCode, Header[] headers,
                                   JSONObject response) {
                Log. d(LOGCAT_TAG,"Sucess! JSON: "+response.toString());

                WeatherDataModel weatherData = WeatherDataModel.fromJson(response);
                updateUI(weatherData);
            }
            @Override
            public void onFailure( int statusCode, Header[] headers, Throwable
                    throwable, JSONObject errorResponse) {
                Log. e(LOGCAT_TAG,"Fail "+throwable.toString());
                Log. d(LOGCAT_TAG,"Status code "+ statusCode);
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull
            int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
// Checking against the request code we specified earlier.
        if (requestCode == REQUEST_CODE) {
            if (grantResults. length > 0 && grantResults[ 0] == PackageManager. PERMISSION_GRANTED) {
                Log. d(LOGCAT_TAG, "onRequestPermissionsResult(): Permission granted!");
// Getting weather only if we were granted permission.
                getWeatherForCurrentLocation();
            } else {
                Log. d(LOGCAT_TAG, "Permission denied =( ");
            }
        }
    }



    // TODO: Add letsDoSomeNetworking(RequestParams params) here:



    // TODO: Add updateUI() here:
    private void updateUI(WeatherDataModel weatherData) {
        mTextViewCity.setText(weatherData.getCity());
        mTextViewTemperature.setText(weatherData.getTemperature());
        int resourceID =
                getResources().getIdentifier(weatherData.getIconName(),"drawable",getPackageName()
                );
        mImageViewSky.setImageResource(resourceID);
    }



    // TODO: Add onPause() here:
    protected void onPause() {
        super.onPause();
        if(mLocationManager != null) mLocationManager.removeUpdates( mLocationListener);
    }




}
