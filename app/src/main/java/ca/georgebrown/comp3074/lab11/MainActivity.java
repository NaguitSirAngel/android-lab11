package ca.georgebrown.comp3074.lab11;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity {

    private FusedLocationProviderClient locationClient;
    private LocationCallback locationCallback;
    private static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getPermission();

        final TextView latitude = findViewById(R.id.txtLatitude);
        final TextView longitude = findViewById(R.id.txtLongitude);
        final TextView accuracy = findViewById(R.id.txtAccuracy);
        final TextView timestamp = findViewById(R.id.txtTimestamp);

        locationClient = LocationServices.getFusedLocationProviderClient(this);

        locationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    latitude.setText(String.valueOf(location.getLatitude()));
                    longitude.setText(String.valueOf(location.getLongitude()));
                    accuracy.setText(String.valueOf(location.getAccuracy()));
                    timestamp.setText(String.valueOf(location.getTime()));
                }
            }
        });

        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                latitude.setText(String.valueOf(location.getLatitude()));
                longitude.setText(String.valueOf(location.getLongitude()));
                accuracy.setText(String.valueOf(location.getAccuracy()));
                timestamp.setText(String.valueOf(location.getTime()));
            }
        };
    }

    private void getPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,@NonNull int[] grantResults)
    {
        if(requestCode == REQUEST_CODE){
            if(grantResults.length > 0 &&  grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"We've got permission to see location", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "We did not get permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startTrackingLocation(){
        LocationRequest lr = new LocationRequest();
        lr.setInterval(5000);
        lr.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationClient.requestLocationUpdates(lr, locationCallback, null);
    }

    private void stopTrackingLocation(){
        locationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    protected void onPause(){
        stopTrackingLocation();
        super.onPause();
    }

    @Override
    protected  void onResume(){
        startTrackingLocation();
        super.onResume();
    }
}
