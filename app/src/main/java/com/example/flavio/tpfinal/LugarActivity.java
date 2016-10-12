package com.example.flavio.tpfinal;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class LugarActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    private static final String TAG = "LugarActivity";
    public static final int CODIGO_AUX =5678;
    private GoogleApiClient GAC;
    private Location locacionInicial;
    private LocationRequest locationRequest;
    private Context context = LugarActivity.this;
    private int control=0;
    private static final int PERMISO_LUGAR = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lugar);
        GAC = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addConnectionCallbacks(this).addApi(LocationServices.API).build();
        locationRequest = new LocationRequest();
        locationRequest.setInterval(2000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LugarActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISO_LUGAR);
        }
        locacionInicial = LocationServices.FusedLocationApi.getLastLocation(GAC);
        Log.i(TAG,"locacion es "+ locacionInicial);
        LocationServices.FusedLocationApi.requestLocationUpdates(
                GAC, locationRequest, LugarActivity.this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG,"La coneccion esta suspendida");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG,"La coneccion fallo");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG,"si son iguales "+ location + "y la otra "+ locacionInicial);
        if(control == 5){
            Intent intent = new Intent(context,MainActivity.class);
            intent.putExtra("Codigo",CODIGO_AUX);
            LugarActivity.this.startActivity(intent);
            finish();
        }else{
            control++;
        }
        if (locacionInicial.getLatitude() != location.getLatitude() && locacionInicial.getLongitude() != location.getLongitude()){
            locacionInicial = location;
            Intent intent = new Intent(context,MainActivity.class);
            intent.putExtra("lugar",locacionInicial);
            LugarActivity.this.startActivity(intent);
            finish();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISO_LUGAR: {
                if (grantResults.length != 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(context,"No activo todos los permisos",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context,MainActivity.class);
                    intent.putExtra("Codigo",CODIGO_AUX);
                    LugarActivity.this.startActivity(intent);
                    finish();
                }
            }
        }
    }
}
