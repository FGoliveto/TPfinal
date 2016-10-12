package com.example.flavio.tpfinal;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class MainActivity extends Activity {
    public static final int CODIGO_MAIN_AGREGAR =1;
    public static final String TAG = "Main Activity";
    public static final String ENCABEZADO="Dirección predeterminada";
    public static final String EXPLICACION="(Está dirección se usa para el calculo de distancia con sus locales preferidos)";
    private static final double LATIOBELISCO=-34.603075;
    private static final double LONGOBELISCO=-58.381653;
    private LocalDB base;
    private Location lugar = new Location("Principal");
    private Context context=MainActivity.this;
    private TextView direccion;
    private String ubicacion="Obelisco";
    private String direccionTexto;
    private String urlFinal;
    private boolean esValido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        direccion = (TextView) findViewById(R.id.textoDireccion);
        esValido = false;
        Intent main = getIntent();
        int controlador = main.getIntExtra("Codigo",0);
        Log.i(TAG, "Controlador " + controlador);
        if (controlador==1234) {
            lugar.setLatitude(LATIOBELISCO);
            lugar.setLongitude(LONGOBELISCO);
            direccionTexto = ENCABEZADO + "\n" + ubicacion + "\n" + EXPLICACION;
            direccion.setText(direccionTexto);
        } else if(controlador==5678){
            lugar.setLatitude(LATIOBELISCO);
            lugar.setLongitude(LONGOBELISCO);
            direccionTexto = ENCABEZADO + "\n" + ubicacion + "\n" + EXPLICACION;
            direccion.setText(direccionTexto);
            Toast.makeText(context,"No fue posible adquirir una nueva direccion",Toast.LENGTH_SHORT).show();
        } else {
            lugar = main.getExtras().getParcelable("lugar");
            Log.i(TAG,"Entro sin salvar informacion y lugar es "+lugar);
            LatLng latLng;
            if (lugar != null) {
                latLng = new LatLng(lugar.getLatitude(),lugar.getLongitude());
                urlFinal ="http://maps.googleapis.com/maps/api/geocode/json?latlng="+latLng.latitude+","+latLng.longitude;
                ObtencionDeCalle posicion = new ObtencionDeCalle();
                posicion.execute();
            }
        }
        Log.i(TAG, "Lugar: " + lugar);
        base = new LocalDB(context);
        findViewById(R.id.agregar).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AgregarActivity.class);
                intent.putExtra("true",true);
                intent.putExtra("lugar",lugar);
                MainActivity.this.startActivityForResult(intent, CODIGO_MAIN_AGREGAR);
            }
        });
        findViewById(R.id.listasimple).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ListaActivity.class);
                intent.putExtra("lugar",lugar);
                MainActivity.this.startActivity(intent);
                finish();
            }
        });
        findViewById(R.id.listaordenada).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ListaDistanciaActivity.class);
                intent.putExtra("lugar",lugar);
                MainActivity.this.startActivity(intent);
                finish();
            }
        });
        findViewById(R.id.mostrarmapa).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MapaActivity.class);
                intent.putExtra("lugar", lugar);
                MainActivity.this.startActivity(intent);
                finish();
            }
        });
        findViewById(R.id.adquirirDireccion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LugarActivity.class);
                MainActivity.this.startActivity(intent);
                finish();
            }
        });
    }

    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("¿Quiere salir?")
                .setNegativeButton("No", null)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        finish();
                    }
                }).create().show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== CODIGO_MAIN_AGREGAR && resultCode==RESULT_OK){
            Log.i(TAG,"Entro a AFR de agregar");
            lugar = data.getExtras().getParcelable("lugar");
            SQLiteDatabase db = base.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("nombre", data.getStringExtra("nombre"));
            values.put("descripcion",data.getStringExtra("descripcion"));
            values.put("rating",data.getFloatExtra("rating", 0));
            values.put("telefono",data.getStringExtra("telefono"));
            values.put("foto",data.getByteArrayExtra("foto"));
            values.put("direccion",data.getStringExtra("direccion"));
            values.put("latitud",data.getDoubleExtra("latitud",0.0));
            values.put("longitud",data.getDoubleExtra("longitud",0.0));
            values.put("rubro",data.getStringExtra("rubro"));
            db.insert("local", null, values);
            db.close();
        }
    }
    public class ObtencionDeCalle extends AsyncTask< Void, Void, Void> {

        ObtencionDeJSONObject jsonFinal = new ObtencionDeJSONObject();

        @Override
        protected void onPreExecute() {
        }
        @Override
        protected Void doInBackground(Void... args) {
            try {
                HashMap<String, String> parametros = new HashMap<>();
                parametros.put("nombre", "nombre");
                parametros.put("pass", "pass");
                Log.i(TAG,"url es "+urlFinal);
                JSONObject json = jsonFinal.requirimientoHttp(urlFinal, "POST", parametros);
                if (json != null) {
                    Log.i(TAG, json.toString());
                    try {
                        String status = json.getString("status");
                        Log.i(TAG, "El status es " + status);
                        if (status.equalsIgnoreCase("OK")) {
                            JSONArray resultado = json.getJSONArray("results");
                            Log.i(TAG, "resultado: " + resultado.toString());
                            int i = 0;
                            do {
                                JSONObject r = resultado.getJSONObject(i);
                                ubicacion = r.getString("formatted_address").split(",")[0];
                                Log.i(TAG, "Calle: " + ubicacion);
                                i = resultado.length();
                                i++;
                                esValido=true;
                            } while (i < resultado.length());
                        }
                    } catch (JSONException e) {
                        Log.i(TAG, "Fallo la adquisicion de la direccion");
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(Void arg) {
            super.onPostExecute(arg);
            if (esValido){
                direccionTexto = ENCABEZADO+"\n"+ubicacion+"\n"+EXPLICACION;
                direccion.setText(direccionTexto);
            }else{
                lugar.setLatitude(LATIOBELISCO);
                lugar.setLongitude(LONGOBELISCO);
                direccionTexto = ENCABEZADO+"\n"+ubicacion+"\n"+EXPLICACION;
                direccion.setText(direccionTexto);
                Toast.makeText(context,"En estos momento no fue posible encontrar una nueva direccion, intente otra vez",Toast.LENGTH_SHORT).show();
            }
        }
    }
}