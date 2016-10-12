package com.example.flavio.tpfinal;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;


class ObtencionDeJSONObject {

    private HttpURLConnection conectar;
    private StringBuilder resultado;
    private URL urlObj;
    private JSONObject jObj = null;

    JSONObject requirimientoHttp(String url, String metodo, HashMap<String, String> parametros) {

        StringBuilder info = new StringBuilder();
        int i = 0;
        String codificacion = "UTF-8";
        for (String s : parametros.keySet()) {
            try {
                if (i != 0){
                    info.append("&");
                }
                info.append(s).append("=").append(URLEncoder.encode(parametros.get(s), codificacion));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            i++;
        }
        if (metodo.equals("POST")) {
            try {
                try {
                    urlObj = new URL(url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                try {
                    conectar = (HttpURLConnection) urlObj.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                conectar.setDoOutput(true);
                conectar.setRequestMethod("POST");
                conectar.setRequestProperty("Accept-Charset", codificacion);
                conectar.setReadTimeout(10000);
                conectar.setConnectTimeout(15000);
                conectar.connect();
                String infoTerminado = info.toString();
                DataOutputStream wr = new DataOutputStream(conectar.getOutputStream());
                wr.writeBytes(infoTerminado);
                wr.flush();
                wr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(metodo.equals("GET")){
            if (info.length() != 0) {
                url += "?" + info.toString();
            }
            try {
                urlObj = new URL(url);
                conectar = (HttpURLConnection) urlObj.openConnection();
                conectar.setDoOutput(false);
                conectar.setRequestMethod("GET");
                conectar.setRequestProperty("Accept-Charset", codificacion);
                conectar.setConnectTimeout(15000);
                conectar.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            InputStream inputStream = new BufferedInputStream(conectar.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            resultado = new StringBuilder();
            String linea;
            while ((linea = bufferedReader.readLine()) != null) {
                resultado.append(linea);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        conectar.disconnect();
        try {
            jObj = new JSONObject(resultado.toString());
        } catch (JSONException ignored) {
        }
        return jObj;
    }
}

