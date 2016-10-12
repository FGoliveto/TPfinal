package com.example.flavio.tpfinal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapaActivity extends Activity implements OnMapReadyCallback{
    private static final String TAG="MapaActivity";
    private static final int PERMISO_MAPA = 4;
    private LocalDB base;
    private Context context=MapaActivity.this;
    private Location lugar;
    private GoogleMap superMapa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);
        Intent intent = getIntent();
        lugar = intent.getExtras().getParcelable("lugar");
        Log.i(TAG, "El lugar es: " + lugar);
        MapFragment mapa = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapalleno));
        mapa.getMapAsync(MapaActivity.this);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(context,MainActivity.class);
        intent.putExtra("lugar", lugar);
        MapaActivity.this.startActivity(intent);
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        superMapa=googleMap;
        ConfiguracionDeMapa();
    }


    public void ConfiguracionDeMapa() {

        superMapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapaActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISO_MAPA);
        }
        superMapa.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lugar.getLatitude(), lugar.getLongitude()), 12));
        superMapa.getUiSettings().setZoomControlsEnabled(true);
        base = new LocalDB(context);
        SQLiteDatabase db = base.getReadableDatabase();
        Cursor items = db.rawQuery("SELECT * FROM local", null);
        items.moveToFirst();
        while (!items.isAfterLast()) {
            Long id= items.getLong(items.getColumnIndex("id"));
            Double latitud1=items.getDouble(items.getColumnIndex("latitud"));
            Double longitud1=items.getDouble(items.getColumnIndex("longitud"));
            superMapa.addMarker(new MarkerOptions().position(new LatLng(latitud1, longitud1)).alpha(id));
            items.moveToNext();
        }
        items.close();
        db.close();
        superMapa.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View info = getLayoutInflater().inflate(R.layout.infowindow, null);
                long id = (long) marker.getAlpha();
                SQLiteDatabase db2 = base.getReadableDatabase();
                Cursor local = db2.rawQuery("SELECT * FROM local WHERE id = ?", new String[]{Long.toString(id)});
                local.moveToFirst();
                String telefono = local.getString(local.getColumnIndex("telefono"));
                String nombre = local.getString(local.getColumnIndex("nombre"));
                String direccion = local.getString(local.getColumnIndex("direccion"));
                Float rating = local.getFloat(local.getColumnIndex("rating"));
                ((TextView) info.findViewById(R.id.mnombre)).setText(nombre);
                ((TextView) info.findViewById(R.id.mtelefono)).setText(telefono);
                ((TextView) info.findViewById(R.id.mdireccion)).setText(direccion);
                ((RatingBar) info.findViewById(R.id.mrating)).setRating(rating);
                local.close();
                db2.close();
                return info;
            }
        });
        superMapa.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                long id = (long) marker.getAlpha();
                Intent mapa = new Intent(context, LocalActivity.class);
                mapa.putExtra("local", id);
                MapaActivity.this.startActivity(mapa);
                finish();
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISO_MAPA: {
                if (grantResults.length != 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(context,"No activo todos los permisos",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context,MainActivity.class);
                    intent.putExtra("lugar", lugar);
                    MapaActivity.this.startActivity(intent);
                    finish();
                }
            }
        }
    }
}
