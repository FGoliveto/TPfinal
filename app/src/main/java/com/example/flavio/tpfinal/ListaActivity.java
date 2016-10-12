package com.example.flavio.tpfinal;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class ListaActivity extends Activity {
    private static final String TAG = "Lista Activity";
    private static final String[] ESPECIALIDAD_LISTA= {
            "Restaurante","Bar","Cine","Shopping","Otro",""
    };
    private EditText buscador;
    private ListView lista;
    private Spinner spinner;
    private LocalDB base;
    private Location lugar;
    private ArrayList<Local> locales;
    private ArrayList<Local> nombreLocales;
    private ArrayList<Local> rubroLocales;
    private ArrayList<Local> mixtoLocales;
    private BaseAdapter localLista;
    private boolean usoBuscador=false;
    private boolean usoRubro=false;
    private Context context=ListaActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);
        findViewById(R.id.cercano).setVisibility(View.GONE);
        Intent intent = getIntent();
        lugar = intent.getExtras().getParcelable("lugar");
        Log.i(TAG,"El lugar es: "+lugar);
    }
    @Override
    protected void onStart() {
        super.onStart();
        base = new LocalDB(context);
        SQLiteDatabase db = base.getReadableDatabase();
        Cursor items = db.rawQuery("SELECT * FROM local", null);
        locales = new ArrayList<>();
        nombreLocales = new ArrayList<>();
        rubroLocales = new ArrayList<>();
        mixtoLocales = new ArrayList<>();
        items.moveToFirst();
        while (!items.isAfterLast()) {
            byte[] resultado = items.getBlob(items.getColumnIndex("foto"));
            Bitmap foto;
            if (!(resultado == null)) {
                foto = BitmapFactory.decodeByteArray(resultado, 0, resultado.length);
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
            Local local = new Local(telefono, nombre, foto, id, direccion, rating, descripcion, distancia, rubro);
            locales.add(local);
            items.moveToNext();
        }
        items.close();
        db.close();
        spinner = (Spinner) findViewById(R.id.spinner_lista);
        lista = (ListView) findViewById(R.id.lista);
        Collections.sort(locales, new Comparator<Local>() {
            @Override
            public int compare(Local lhs, Local rhs) {
                String a1 = lhs.getNombre();
                String b1 = rhs.getNombre();
                return a1.compareToIgnoreCase(b1);
            }
        });
        buscador=(EditText)findViewById(R.id.buscadorenlista);
        buscador.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_ENTER)) {
                    if (buscador.getText().toString().isEmpty() && !usoRubro) {
                        Log.i(TAG, "Entro en buscar opcion texto vacio y bolleano falso");
                        localLista = new BaseAdapter(context, locales);
                        lista.setAdapter(localLista);
                        usoBuscador = false;
                    } else if (!buscador.getText().toString().isEmpty() && !usoRubro) {
                        Log.i(TAG, "Entro en buscar opcion texto lleno y bolleano falso");
                        String clave = buscador.getText().toString().toLowerCase(Locale.getDefault()).replace(" ", "");
                        nombreLocales.clear();
                        for (Local l : locales) {
                            if (l.getNombre().toLowerCase(Locale.getDefault()).replace(" ", "").contains(clave)) {
                                nombreLocales.add(l);
                            }
                        }
                        localLista = new BaseAdapter(context, nombreLocales);
                        lista.setAdapter(localLista);
                        usoBuscador = true;
                    } else if (buscador.getText().toString().isEmpty() && usoRubro) {
                        Log.i(TAG, "Entro en buscar opcion texto vacio y bolleano verdadero");
                        localLista = new BaseAdapter(context, rubroLocales);
                        lista.setAdapter(localLista);
                        usoBuscador = false;
                    } else {
                        Log.i(TAG, "Entro en buscar opcion texto lleno y bolleano verdadero");
                        String clave = buscador.getText().toString().toLowerCase(Locale.getDefault()).replace(" ", "");
                        mixtoLocales.clear();
                        nombreLocales.clear();
                        for (Local l : locales) {
                            if (l.getNombre().toLowerCase(Locale.getDefault()).replace(" ", "").contains(clave)) {
                                nombreLocales.add(l);
                            }
                        }
                        for (Local l : nombreLocales) {
                            for (Local g : rubroLocales) {
                                if (l.getNombre().contentEquals(g.getNombre())) {
                                    mixtoLocales.add(g);
                                }
                            }
                        }
                        localLista = new BaseAdapter(context, mixtoLocales);
                        lista.setAdapter(localLista);
                        usoBuscador = true;
                    }
                }
                return false;
            }
        });
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
                if (clave.contentEquals("") && !usoBuscador) {
                    Log.i(TAG, "Entro en spinner opcion texto vacio y bolleano falso");
                    localLista = new BaseAdapter(context, locales);
                    lista.setAdapter(localLista);
                    usoRubro = false;
                } else if (!clave.contentEquals("") && !usoBuscador) {
                    Log.i(TAG, "Entro en spinner opcion texto lleno y bolleano falso");
                    rubroLocales.clear();
                    for (Local l : locales) {
                        if (l.getRubro().contentEquals(clave)) {
                            rubroLocales.add(l);
                        }
                    }
                    localLista = new BaseAdapter(context, rubroLocales);
                    lista.setAdapter(localLista);
                    usoRubro = true;
                } else if (clave.contentEquals("") && usoBuscador) {
                    Log.i(TAG, "Entro en spinner opcion texto vacio y bolleano verdadero");
                    localLista = new BaseAdapter(context, nombreLocales);
                    lista.setAdapter(localLista);
                    usoRubro = false;
                } else {
                    Log.i(TAG, "Entro en spinner opcion texto lleno y bolleano verdadero");
                    mixtoLocales.clear();
                    rubroLocales.clear();
                    for (Local l : locales) {
                        if (l.getRubro().contentEquals(clave)) {
                            rubroLocales.add(l);
                        }
                    }
                    for (Local l : rubroLocales) {
                        for (Local g : nombreLocales) {
                            if (l.getNombre().contentEquals(g.getNombre())) {
                                mixtoLocales.add(g);
                            }
                        }
                    }
                    localLista = new BaseAdapter(context, mixtoLocales);
                    lista.setAdapter(localLista);
                    usoRubro = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        lista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(context)
                        .setTitle("¿Quiere eliminar el local?")
                        .setNegativeButton("No", null)
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                if (usoBuscador && usoRubro) {
                                    locales.clear();
                                    locales = mixtoLocales;
                                } else if (usoBuscador) {
                                    locales.clear();
                                    locales = nombreLocales;
                                } else if (usoRubro) {
                                    locales.clear();
                                    locales = rubroLocales;
                                }
                                long j = locales.get(position).getId();
                                base.getWritableDatabase().delete("local", " id = ? ", new String[]{Long.toString(j)});
                                locales.remove(position);
                                localLista.notifyDataSetChanged();
                            }
                        }).create().show();
                return false;
            }
        });
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (usoBuscador && usoRubro) {
                    locales.clear();
                    locales = mixtoLocales;
                } else if (usoBuscador) {
                    locales.clear();
                    locales = nombreLocales;
                } else if (usoRubro) {
                    locales.clear();
                    locales = rubroLocales;
                }
                Intent detalle = new Intent(context, LocalActivity.class);
                detalle.putExtra("local", locales.get(position).getId());
                ListaActivity.this.startActivity(detalle);
                buscador.setText("");
                spinner.setSelection(0);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(context,MainActivity.class);
        intent.putExtra("lugar", lugar);
        ListaActivity.this.startActivity(intent);
        finish();
    }
}
