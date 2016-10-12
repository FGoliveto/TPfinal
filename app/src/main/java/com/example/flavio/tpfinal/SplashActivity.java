package com.example.flavio.tpfinal;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.testfairy.TestFairy;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends Activity {
    private static final String TAG = "Splash";
    private Context context=SplashActivity.this;
    public static final int CODIGO_AUX =1234;
    public static final int PERMISO =1;
    public static final String TESTFAIRY = "5d9af96dee6953aaa51fabfb98d1956ad48bab12";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        TestFairy.begin(context,TESTFAIRY);
        TimerTask tarea = new TimerTask() {
            public void run() {

                new Thread()
                {
                    public void run()
                    {
                        SplashActivity.this.runOnUiThread(new Runnable()
                        {
                            public void run()
                            {
                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                                        && ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                                        && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                        && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISO);
                                } else {
                                    Log.i(TAG,"Todos los permisos estan activados");
                                    Intent lugarIntent = new Intent(context,MainActivity.class);
                                    lugarIntent.putExtra("Codigo", CODIGO_AUX);
                                    startActivity(lugarIntent);
                                    finish();
                                }
                            }
                        });
                    }
                }.start();
            }
        };
        Timer timer = new Timer();
        long SPLASH_DELAY = 2000;
        timer.schedule(tarea, SPLASH_DELAY);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        Log.i(TAG,"Entro al requirimiento del permiso y los datos son permision "+ Arrays.toString(permissions) + "mas los resultados "+ Arrays.toString(grantResults));
        switch (requestCode) {
            case PERMISO: {
                if (grantResults.length == 4 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.i(TAG,"El resultado de los permisos fue posistivo y da lo siguiente "+ grantResults.length );
                    Intent lugarIntent = new Intent(context,MainActivity.class);
                    lugarIntent.putExtra("Codigo", CODIGO_AUX);
                    startActivity(lugarIntent);
                    finish();
                }else {
                    Toast.makeText(context,"No activo todos los permisos",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }
    public void onBackPressed(){
    }
}


