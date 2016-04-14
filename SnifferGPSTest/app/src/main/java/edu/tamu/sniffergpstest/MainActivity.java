package edu.tamu.sniffergpstest;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements LocationListener, SensorEventListener {
    private double latitude;
    private double longitude;
    private DBHandle dbptr;
    private String address = "";
    private String timestamp = "";

    private volatile boolean running = false;
    private DataLogger dl;
    private Thread t;

    private TextView txtView;
    private TextView curTblName;
    private Button button_toggle;
    private TextView sqlrow, numRowsTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtView = (TextView) findViewById(R.id.data);
        sqlrow = (TextView)findViewById(R.id.update);
        numRowsTxt = (TextView)findViewById(R.id.numEntries);

        button_toggle = (Button)findViewById(R.id.toggle);
        button_toggle.setOnClickListener(new ToggleHandler());

        String dir = "" + Environment.getExternalStorageDirectory().getPath() + "/Data.db";
        dbptr = new DBHandle(getApplicationContext(), dir);

        WifiManager manager = (WifiManager) getSystemService(Activity.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        address = info.getMacAddress();

        LocationManager locationManager = (LocationManager) this.getSystemService(Activity.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1, this);
        } catch (SecurityException e) {
            Log.e("PERMISSION_EXCEPTION", "PERMISSION_NOT_GRANTED");
        }

        dl = new DataLogger();

        new UpdateUI().execute();

        curTblName = (TextView)findViewById(R.id.curTblName);
        curTblName.setText(dbptr.curTblName);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        curTblName.setText(dbptr.curTblName);
    }

    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true); //avoid onDestroy for back button
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        txtView.setText("Current Loc: " + "( " + Double.toString(latitude) + " , " + Double.toString(longitude) + " )");
    }

    //required overrides
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
    @Override
    public void onProviderEnabled(String provider) {}
    @Override
    public void onProviderDisabled(String provider) {}
    @Override
    public void onSensorChanged(SensorEvent event) {}
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    public void newtable(View view)
    {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        EditText editText = (EditText) findViewById(R.id.table);
        String newtable = editText.getText().toString();
        dbptr.openNextTable(newtable);
        curTblName.setText(dbptr.curTblName);
        String msg = "Table " + dbptr.curTblName + " opened";
        Toast t = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        t.show();
    }

    public void clearTable(View view)
    {
        dbptr.clearCurrentTable();
        Toast t = Toast.makeText(getApplicationContext(), "Table entries cleared", Toast.LENGTH_SHORT);
        t.show();
    }

    private class ToggleHandler implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            if(!running)
            {
                running = true;
                t = new Thread(dl);
                t.start();
                button_toggle.setBackgroundColor(Color.RED);
                button_toggle.setText("Stop");
                Toast t = Toast.makeText(getApplicationContext(),"Collection started",Toast.LENGTH_SHORT);
                t.show();
            }
            else
            {
                t.interrupt();
                button_toggle.setBackgroundColor(Color.GREEN);
                button_toggle.setText("Start");
                running = false;
                Toast t = Toast.makeText(getApplicationContext(),"Collection stopped",Toast.LENGTH_SHORT);
                t.show();
            }
        }
    }

    private class DataLogger implements Runnable
    {
        @Override
        public void run()
        {
            while(!Thread.currentThread().isInterrupted())
            {
                DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date d = Calendar.getInstance().getTime();
                timestamp = fmt.format(d);
                dbptr.writeDB(address, latitude, longitude, timestamp);
                try{
                    Thread.sleep(2000);
                }catch(InterruptedException e) {
                    System.out.println(e);
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    private class UpdateUI extends AsyncTask<String, String, String>
    {
        @Override
        protected String doInBackground(String... strs)
        {
            while(true)
            {
                String row = dbptr.getLatestEntry();
                String numRows = dbptr.getNumRows();
                publishProgress(row,numRows);
                try{
                    Thread.sleep(2000);
                }catch(Exception e){System.err.println(e);}
            }
        }

        @Override
        protected void onProgressUpdate(String... results)
        {
            sqlrow.setText(results[0]);
            numRowsTxt.setText(results[1]);
        }
    }
}
