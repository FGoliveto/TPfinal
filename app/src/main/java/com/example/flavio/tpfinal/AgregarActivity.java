package com.example.flavio.tpfinal;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class AgregarActivity extends Activity {
    private static final int CODIGO_CAMARA = 1;
    private static final String TAG = "Agregar Activity";
    private static final int MAXIMO_CHICO=50;
    private static final int MAXIMO_GRANDE=500;
    private static final int MAXIMO_TELEFONO=30;
    private static final String[] ESPECIALIDAD= {
            "Restaurante","Bar","Cine","Shopping","Otro",""
    };
    private ImageView foto;
    private ImageView mapaLugar;
    private EditText nombre;
    private EditText descripcion;
    private EditText telefono;
    private EditText direccion;
    private RatingBar rating;
    private byte[] byteArray;
    private double latitud;
    private double longitud;
    private String domicilio;
    private boolean direValida=false;
    private boolean rubroValido=false;
    private String tipoLocal;
    private long id;
    private boolean main;
    public String urlFinal;
    private Location lugar;
    private Context context=AgregarActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar);
        StrictMode.ThreadPolicy permisoDeHilo =
                new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(permisoDeHilo);
        Spinner rubro = (Spinner) findViewById(R.id.spinner);
        ArrayList<String> rubros = new ArrayList<>();
        Collections.addAll(rubros, ESPECIALIDAD);
        Collections.sort(rubros);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.spinner, rubros);
        adapter.setDropDownViewResource(R.layout.spinner);
        rubro.setAdapter(adapter);
        rubro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tipoLocal = String.valueOf(parent.getItemAtPosition(position));
                if (tipoLocal.contentEquals("")) {
                    rubroValido=false;
                } else {
                    Log.i(TAG,"Se adquirio el tipo: "+tipoLocal);
                    rubroValido=true;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        Button grabar = (Button) findViewById(R.id.guardar);
        Button newGrabar = (Button) findViewById(R.id.newguardar);
        nombre = (EditText) findViewById(R.id.nombre);
        descripcion = (EditText) findViewById(R.id.descripcion);
        rating = (RatingBar) findViewById(R.id.rating);
        foto = (ImageView) findViewById(R.id.foto);
        mapaLugar = (ImageView) findViewById(R.id.mapaLugar);
        mapaLugar.setVisibility(View.GONE);
        telefono = (EditText) findViewById(R.id.telefono);
        direccion = (EditText) findViewById(R.id.direccion);
        Intent local = getIntent();
        main= local.getBooleanExtra("true", false);
        if(main){
            lugar = local.getExtras().getParcelable("lugar");
            newGrabar.setVisibility(View.GONE);
        }else{
            direValida=true;
            rubroValido=true;
            id = local.getLongExtra("id", 0);
            LocalDB base= new LocalDB(context);
            SQLiteDatabase db = base.getReadableDatabase();
            Cursor items = db.rawQuery("SELECT * FROM local WHERE id = ?", new String[]{Long.toString(id)});
            items.moveToFirst();
            byte[] result = items.getBlob(items.getColumnIndex("foto"));
            Bitmap fotoGuardada;
            if (!(result == null)) {
                fotoGuardada = BitmapFactory.decodeByteArray(result, 0, result.length);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                fotoGuardada.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byteArray = stream.toByteArray();
            } else {
                fotoGuardada = null;
                byteArray= null;
            }
            tipoLocal=items.getString(items.getColumnIndex("rubro"));
            rubro.setSelection(getIndice(rubro, tipoLocal));
            foto.setImageBitmap(fotoGuardada);
            telefono.setText(items.getString(items.getColumnIndex("telefono")));
            nombre.setText(items.getString(items.getColumnIndex("nombre")));
            direccion.setText(items.getString(items.getColumnIndex("direccion")));
            rating.setRating(items.getFloat(items.getColumnIndex("rating")));
            descripcion.setText(items.getString(items.getColumnIndex("descripcion")));
            latitud=items.getDouble(items.getColumnIndex("latitud"));
            longitud=items.getDouble(items.getColumnIndex("longitud"));
            try {
                URL urlFinal = new URL("http://maps.google.com/maps/api/staticmap?center=" + latitud + "," + longitud + "&zoom=16&size=250x250&markers=" + latitud + "," + longitud);
                Bitmap bitmap = null;
                try {
                    bitmap = BitmapFactory.decodeStream(urlFinal.openConnection().getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mapaLugar.setImageBitmap(bitmap);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            mapaLugar.setVisibility(View.VISIBLE);
            items.close();
            db.close();
            grabar.setVisibility(View.GONE);
        }
        Button agregarFoto = (Button) findViewById(R.id.agregarfoto);
        agregarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fotoIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(fotoIntent, CODIGO_CAMARA);
            }
        });
        direccion.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_ENTER)) {
                    domicilio = direccion.getText().toString();
                    direValida = false;
                    if (domicilio.length()>0){
                        String domicilioModificado=domicilio.replace(" ", "%20");
                        urlFinal ="http://maps.googleapis.com/maps/api/geocode/json?address="+domicilioModificado+",%20Ciudad%20de%20Buenos%20Aires,%20Argentina";
                        CapturaDeMapa direLocal = new CapturaDeMapa();
                        direLocal.execute();
                    }
                    return true;
                }
                return false;
            }
        });
        grabar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nombre.getText().length()>MAXIMO_CHICO){
                    int caracterDeMas=nombre.getText().length()-MAXIMO_CHICO;
                    Toast.makeText(context, "El nombre no debe superar los 50 caracteres, actualmente hay "+caracterDeMas+" de más", Toast.LENGTH_SHORT).show();
                }else if (descripcion.getText().length()>MAXIMO_GRANDE){
                    int caracterDeMas=descripcion.getText().length()-MAXIMO_GRANDE;
                    Toast.makeText(context, "El nombre no debe superar los 500 caracteres, actualmente hay "+caracterDeMas+" de más", Toast.LENGTH_SHORT).show();
                }else if (telefono.getText().length()>MAXIMO_TELEFONO){
                    int caracterDeMas=telefono.getText().length()-MAXIMO_TELEFONO;
                    Toast.makeText(context, "El nombre no debe superar los 30 caracteres, actualmente hay "+caracterDeMas+" de más", Toast.LENGTH_SHORT).show();
                }else{
                    if (!nombre.getText().toString().isEmpty() && !descripcion.getText().toString().isEmpty() && !direccion.getText().toString().isEmpty() && direValida && rubroValido) {
                        Intent nuevo = new Intent();
                        nuevo.putExtra("nombre", nombre.getText().toString());
                        nuevo.putExtra("descripcion", descripcion.getText().toString());
                        nuevo.putExtra("rating", rating.getRating());
                        nuevo.putExtra("telefono", telefono.getText().toString());
                        nuevo.putExtra("foto", byteArray);
                        nuevo.putExtra("direccion", direccion.getText().toString());
                        nuevo.putExtra("latitud", latitud);
                        nuevo.putExtra("longitud", longitud);
                        nuevo.putExtra("rubro",tipoLocal);
                        nuevo.putExtra("lugar",lugar);
                        Toast.makeText(context,"El local "+nombre.getText().toString()+" se guardo correctamente",Toast.LENGTH_LONG).show();
                        Log.i(TAG,"Intent es "+nuevo);
                        setResult(RESULT_OK, nuevo);
                        finish();
                    } else {
                        if (direValida){
                            Toast.makeText(context, "Faltan datos", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(context, "La direccion no es correcta, por favor verifique que sea valida", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        }});
        newGrabar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nombre.getText().length() > MAXIMO_CHICO) {
                    int caracterDeMas = nombre.getText().length() - MAXIMO_CHICO;
                    Toast.makeText(context, "El nombre no debe superar los 50 caracteres, actualmente hay " + caracterDeMas + " de más", Toast.LENGTH_SHORT).show();
                } else if (descripcion.getText().length() > MAXIMO_GRANDE) {
                    int caracterDeMas = descripcion.getText().length() - MAXIMO_GRANDE;
                    Toast.makeText(context, "El nombre no debe superar los 50 caracteres, actualmente hay " + caracterDeMas + " de más", Toast.LENGTH_SHORT).show();
                } else if (telefono.getText().length() > MAXIMO_TELEFONO) {
                    int caracterDeMas = telefono.getText().length() - MAXIMO_TELEFONO;
                    Toast.makeText(context, "El nombre no debe superar los 50 caracteres, actualmente hay " + caracterDeMas + " de más", Toast.LENGTH_SHORT).show();
                } else {
                    if (!nombre.getText().toString().isEmpty() && !descripcion.getText().toString().isEmpty() && !direccion.getText().toString().isEmpty() && direValida && rubroValido) {
                        LocalDB base = new LocalDB(context);
                        SQLiteDatabase db = base.getWritableDatabase();
                        ContentValues valores = new ContentValues();
                        valores.put("nombre", nombre.getText().toString());
                        valores.put("descripcion", descripcion.getText().toString());
                        valores.put("rating", rating.getRating());
                        valores.put("telefono", telefono.getText().toString());
                        valores.put("foto", byteArray);
                        valores.put("direccion", direccion.getText().toString());
                        valores.put("latitud", latitud);
                        valores.put("longitud", longitud);
                        valores.put("rubro", tipoLocal);
                        db.update("local", valores, " id = ? ", new String[]{Long.toString(id)});
                        db.close();
                        Toast.makeText(context,"El local "+nombre.getText().toString()+" se actualizo correctamente",Toast.LENGTH_LONG).show();
                        Intent cambio = new Intent(context, LocalActivity.class);
                        cambio.putExtra("local", id);
                        AgregarActivity.this.startActivity(cambio);
                        finish();
                    } else {
                        if (direValida) {
                            Toast.makeText(context, "Faltan datos", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, "La direccion no es correcta, por favor verifique que sea valida", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }});
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODIGO_CAMARA && resultCode == RESULT_OK) {
            Log.i(TAG,"Entro al AFR de Camara");
            Bitmap muestra = (Bitmap) data.getExtras().get("data");
            foto.setImageBitmap(muestra);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            if (muestra != null) {
                muestra.compress(Bitmap.CompressFormat.PNG, 100, stream);
            }
            byteArray = stream.toByteArray();
        }
    }
    private int getIndice(Spinner spinner, String tipo){
        int indice=0;
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).equals(tipo)){
                indice = i;
            }
        }
        return indice;
    }
    public class CapturaDeMapa extends AsyncTask< Void, JSONObject, LatLng> {

        ObtencionDeJSONObject jsonFinal = new ObtencionDeJSONObject();
        private ProgressDialog pDialog;
        private LatLng latLng;
        private JSONObject direccionParaMostrar;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Comprobando dirección...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        @Override
        protected LatLng doInBackground(Void... args) {
            try {
                HashMap<String, String> parametros = new HashMap<>();
                parametros.put("nombre", "nombre");
                parametros.put("pass", "pass");
                JSONObject json = jsonFinal.requirimientoHttp(urlFinal, "GET", parametros);
                if (json != null) {
                    Log.i(TAG, json.toString());
                    try {
                        String status = json.getString("status");
                        Log.i(TAG, "El status es "+ status);
                        if(status.equalsIgnoreCase("OK")){
                            JSONArray resultado = json.getJSONArray("results");
                            Log.i(TAG,"results: "+resultado.toString());
                            int i = 0;
                            do{
                                JSONObject special= resultado.getJSONObject(i);
                                String fallo= special.getString("types");
                                Log.i(TAG,"tipo: "+fallo);
                                if (!fallo.contains("street_address")){
                                    Log.i(TAG, "Direccion inexistente");
                                    direValida = false;
                                    i = resultado.length();
                                    i++;
                                }else{
                                    direccionParaMostrar = resultado.getJSONObject(i);
                                    JSONObject b= direccionParaMostrar.getJSONObject("geometry");
                                    JSONObject c= b.getJSONObject("location");
                                    latitud = Double.valueOf(c.getString("lat"));
                                    longitud= Double.valueOf(c.getString("lng"));
                                    latLng = new LatLng(latitud,longitud);
                                    i = resultado.length();
                                    i++;
                                    direValida=true;
                                }
                            }while(i<resultado.length());
                        }Log.i(TAG,"domi: "+ latitud+"//"+longitud);
                    } catch (JSONException e) {
                        Log.i(TAG, "Fallo la adquisición de la direccion");
                        direValida = false;
                        e.printStackTrace();
                    }
                    return latLng;
                }
            } catch (Exception e) {
                direValida = false;
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(LatLng latLngFinal) {
            super.onPostExecute(latLngFinal);
            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }
            if (direValida){
                try {
                    direccion.setText(direccionParaMostrar.getString("formatted_address").split(",")[0]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    URL url = new URL("http://maps.google.com/maps/api/staticmap?center=" + latitud + "," + longitud + "&zoom=16&size=250x250&markers=" + latitud + "," + longitud);
                    Bitmap bitmap = null;
                    try {
                        bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mapaLugar.setImageBitmap(bitmap);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                mapaLugar.setVisibility(View.VISIBLE);
                Toast.makeText(context,"Dirección valida",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(context,"Dirección invalida",Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void onBackPressed(){
        if (!main){
            Intent regresarAlLocal= new Intent(context,LocalActivity.class);
            regresarAlLocal.putExtra("local",id);
            AgregarActivity.this.startActivity(regresarAlLocal);
            finish();
        }else{
            finish();
        }
    }
}
