package cl.exec.dev.android.aif.activity;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import cl.exec.dev.android.aif.R;
import cl.exec.dev.android.aif.connection.GetDataAsyncTask;

public class MainActivity extends AppCompatActivity implements LocationListener{

    private static final String LOG = MainActivity.class.getName();
    private static final String SHARE_HASTAG_PARQUES = "#YoCuidoMisParques";
    private static final String SHARE_HASTAG_BOSQUES = "#YoCuidoMisBosques";

    private Button sendAlertButton;
    private LocationManager locationManager;
    private String provider;
    private double lat;
    private double lng;

    private ShareActionProvider mShareActionProvider;
    private String mForecast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);

        if (location != null) {
            Log.d(LOG, "Provider " + provider + " has been selected.");
            onLocationChanged(location);
        } else {
            Log.d(LOG , "No provider has been selected.");
        }

        sendAlertButton = (Button) findViewById(R.id.button_send_alert);
        sendAlertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final View coordinatorLayoutView = findViewById(R.id.snackbarPosition);
                Snackbar
                        .make(coordinatorLayoutView, R.string.sending_alert_snack_bar, Snackbar.LENGTH_LONG)
                        .show();

                Log.d(LOG, "lat: " + lat);
                Log.d(LOG, "lng: " + lng);





            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    @Override
    public void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }


    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lng= location.getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_login) {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            return true;
        }

        if (id == R.id.action_share) {
            createShareForecastIntent();

            Log.d(LOG ,"shared pressed");

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "http://www.exec.cl - " + SHARE_HASTAG_BOSQUES);
        return shareIntent;
    }
}
