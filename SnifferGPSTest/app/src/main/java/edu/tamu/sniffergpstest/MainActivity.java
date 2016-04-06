package edu.tamu.sniffergpstest;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends AppCompatActivity implements LocationListener, SensorEventListener {
    private Sensor senMagnetometer;
    private double latitude;
    private double longitude;
    private String tablename;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button button_add = (Button) findViewById(R.id.add);
        button_add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {  //button for adding gps point to DB

            }
        });

        SensorManager senSensorManager = (SensorManager) this.getSystemService(Activity.SENSOR_SERVICE);
        Sensor senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        senSensorManager.registerListener(this, senMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);

        WifiManager manager = (WifiManager) getSystemService(Activity.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        String address = info.getMacAddress();

        LocationManager locationManager = (LocationManager) this.getSystemService(Activity.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1, this);
        } catch (SecurityException e) {
            Log.e("PERMISSION_EXCEPTION", "PERMISSION_NOT_GRANTED");
        }
    }
    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    /**
     * @fn onProviderEnabled
     * @brief not implemented
     */
    @Override
    public void onProviderEnabled(String provider) {}

    /**
     * @fn onProviderDisabled
     * @brief not implemented
     */
    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    public void newtable(View view){
        //newtable in response to button
        EditText editText = (EditText) findViewById(R.id.table);
        String newtable = editText.getText().toString();
    }
}
