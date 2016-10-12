package com.example.flavio.tpfinal;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

class BaseAdapter extends android.widget.BaseAdapter {
    private ArrayList<Local> locales;
    Context context;

    BaseAdapter(Context context, ArrayList<Local> locales){
        this.locales=locales;
        this.context=context;
    }
    @Override
    public int getCount() {
        return locales.size();
    }
    @Override
    public Object getItem(int position) {
        return position;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if(convertView==null){
            convertView=layoutInflater.inflate(R.layout.lista,parent,false);
        }
        ((TextView)convertView.findViewById(R.id.vistatitulo)).setText(locales.get(position).getNombre());
        ((TextView)convertView.findViewById(R.id.vistadireccion)).setText(locales.get(position).getDireccion());
        ((TextView)convertView.findViewById(R.id.vistatelefono)).setText(locales.get(position).getTelefono());
        ((ImageView)convertView.findViewById(R.id.vistaimagen)).setImageBitmap(locales.get(position).getImagen());
        String distanciaTexto= String.format(Locale.getDefault(),"%.2f",locales.get(position).getDistancia())+" km.";
        ((TextView)convertView.findViewById(R.id.vistadistancia)).setText(distanciaTexto);
        return convertView;
    }
}
