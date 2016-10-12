package com.example.flavio.tpfinal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ListaDistanciaActivity extends Activity {
    private static final String TAG = "ListaDistancia Activity";
    private static final String[] ESPECIALIDAD_LISTA= {
            "Restaurante","Bar","Cine","Shopping","Otro",""
    };
    private ListView lista;
    private Location lugar;
    private ArrayList<Local> locales;
    private ArrayList<Local> localesClasificados;
    private Context context=ListaDistanciaActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);
        findViewById(R.id.filtro_palabraClave).setVisibility(View.GONE);
        findViewById(R.id.buscadorenlista).setVisibility(View.GONE);
        Intent intent = getIntent();
        lugar = intent.getExtras().getParcelable("lugar");
        Log.i(TAG,"El lugar es: "+lugar);
    }
    @Override
    protected void onStart() {
        super.onStart();
        lista = (ListView) findViewById(R.id.lista);
        LocalDB base = new LocalDB(context);
        SQLiteDatabase db = base.getReadableDatabase();
        Cursor items = db.rawQuery("SELECT * FROM local", null);
        locales = new ArrayList<>();
        localesClasificados = new ArrayList<>();
        items.moveToFirst();
        while (!items.isAfterLast()) {
            byte[] result = items.getBlob(items.getColumnIndex("foto"));
            Bitmap foto;
            if (!(result == null)) {
                foto = BitmapFactory.decodeByteArray(result, 0, result.length);
            } else {
                foto = null;
            }
            String telefono = items.getString(items.getColumnIndex("telefono"));
            String nombre= items.getString(items.getColumnIndex("nombre"));
            long id= items.getLong(items.getColumnIndex("id"));
            String direccion =items.getString(items.getColumnIndex("direccion"));
            float rating =items.getFloat(items.getColumnIndex("rating"));
            String descripcion=items.getString(items.getColumnIndex("descripcion"));
            double latitud1=items.getDouble(items.getColumnIndex("latitud"));
            double longitud1=items.getDouble(items.getColumnIndex("longitud"));
            double latitud2= lugar.getLatitude();
            double longitud2= lugar.getLongitude();
            double distancia= new Distancia().distancia(latitud1, longitud1, latitud2, longitud2);
            String rubro=items.getString(items.getColumnIndex("rubro"));
            Local Local = new Local(telefono,nombre,foto,id,direccion,rating,descripcion,distancia,rubro);
            locales.add(Local);
            items.moveToNext();
        }
        items.close();
        db.close();
        Spinner spinner = (Spinner) findViewById(R.id.spinner_lista);
        ArrayList<String> rubros = new ArrayList<>();
        Collections.addAll(rubros, ESPECIALIDAD_LISTA);
        Collections.sort(rubros);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.spinner, rubros);
        adapter.setDropDownViewResource(R.layout.spinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String clave = parent.getItemAtPosition(position).toString();
                Log.i(TAG,"Entro al spinner");
                localesClasificados.clear();
                for (Local l : locales) {
                    if (clave.contentEquals("")) {
                        localesClasificados.add(l);
                    } else {
                        if (l.getRubro().contentEquals(clave)) {
                            localesClasificados.add(l);
                            Log.i(TAG,"Clasifico la lista");
                        }
                    }
                    Collections.sort(localesClasificados, new Comparator<Local>() {
                        public int compare(Local lhs, Local rhs) {
                            Double a1 = lhs.getDistancia();
                            Double b1 = rhs.getDistancia();
                            return a1.compareTo(b1);
                        }
                    });
                    int j = localesClasificados.size();
                    for (int i = 10; i < j; i++) {
                        localesClasificados.remove(10);
                    }
                    BaseAdapter locallista = new BaseAdapter(context, localesClasificados);
                    lista.setAdapter(locallista);
                    lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent detalle = new Intent(context, LocalActivity.class);
                            detalle.putExtra("local", localesClasificados.get(position).getId());
                            ListaDistanciaActivity.this.startActivity(detalle);
                        }
                    });
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(context,MainActivity.class);
        intent.putExtra("lugar", lugar);
        ListaDistanciaActivity.this.startActivity(intent);
        finish();
    }
}
