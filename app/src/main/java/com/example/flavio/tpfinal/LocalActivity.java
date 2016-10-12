package com.example.flavio.tpfinal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocalActivity extends Activity implements OnMapReadyCallback {
    private long id;
    private Context context = LocalActivity.this;
    private GoogleMap mapa;
    private MarkerOptions marcador;
    private LatLng locacion;
    private static final int PERMISO_LOCAL = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local);
        Intent intent = getIntent();
        id = intent.getLongExtra("local", 0);
        LocalDB base = new LocalDB(context);
        SQLiteDatabase db = base.getReadableDatabase();
        Cursor items = db.rawQuery("SELECT * FROM local WHERE id = ?", new String[]{Long.toString(id)});
        items.moveToFirst();
        byte[] result = items.getBlob(items.getColumnIndex("foto"));
        Bitmap foto;
        if (!(result == null)) {
            foto = BitmapFactory.decodeByteArray(result, 0, result.length);
            ((ImageView) findViewById(R.id.dfoto)).setImageBitmap(foto);
        } else {
            findViewById(R.id.dfoto).setVisibility(View.GONE);
        }
        String telefono = items.getString(items.getColumnIndex("telefono"));
        String nombre = items.getString(items.getColumnIndex("nombre"));
        String rubro = items.getString(items.getColumnIndex("rubro"));
        String direccion = items.getString(items.getColumnIndex("direccion"));
        Float rating = items.getFloat(items.getColumnIndex("rating"));
        String descripcion = items.getString(items.getColumnIndex("descripcion"));
        Double latitud = items.getDouble(items.getColumnIndex("latitud"));
        Double longitud = items.getDouble(items.getColumnIndex("longitud"));
        items.close();
        db.close();
        ((TextView) findViewById(R.id.dnombre)).setText(nombre);
        ((TextView) findViewById(R.id.drubro)).setText(rubro);
        ((TextView) findViewById(R.id.ddescripcion)).setText(descripcion);
        ((RatingBar) findViewById(R.id.drating)).setRating(rating);
        ((TextView) findViewById(R.id.dtelefono)).setText(telefono);
        ((TextView) findViewById(R.id.ddireccion)).setText(direccion);
        locacion = new LatLng(latitud, longitud);
        marcador = new MarkerOptions().position(locacion).title(((TextView) findViewById(R.id.dnombre)).getText().toString());
        MapFragment mapa = ((MapFragment) getFragmentManager().findFragmentById(R.id.dmapa));
        mapa.getMapAsync(LocalActivity.this);
        findViewById(R.id.editar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editar = new Intent(context, AgregarActivity.class);
                editar.putExtra("id", id);
                LocalActivity.this.startActivity(editar);
                finish();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mapa = map;
        ConfiguracionMapa();
    }

    public void ConfiguracionMapa() {
        mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LocalActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISO_LOCAL);
        }
        mapa.getUiSettings().setZoomControlsEnabled(true);
        mapa.addMarker(marcador);
        mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(locacion, 16));
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISO_LOCAL: {
                if (grantResults.length != 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(context,"No activo todos los permisos",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }
}
