package com.example.asteroides;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by motoni on 26/11/2016.
 */

public class AlmacenPuntuacionesJSon implements AlmacenPuntuaciones {
    private String string;

    private static String FICHERO = "puntuacionesJSon.txt";
    private Context context;

    //Almacena puntuaciones en formato JSON
    public AlmacenPuntuacionesJSon(Context context) {
        this.context = context;
        //guardarPuntuacion(45000, "Mi nombre", System.currentTimeMillis());
        //guardarPuntuacion(31000, "Otro nombre", System.currentTimeMillis());
    }

    @Override
    public void guardarPuntuacion(int puntos, String nombre, long fecha) {
        string = cargarFichero();
        List<Puntuacion> puntuaciones = leerJSon(string);
        puntuaciones.add(new Puntuacion(puntos, nombre, fecha));
        string = guardarJSon(puntuaciones);
        guardarFichero(string);
    }

    @Override
    public Vector<String> listaPuntuaciones(int cantidad) {
        string = cargarFichero();
        List<Puntuacion> puntuaciones = leerJSon(string);
        Vector<String> salida = new Vector<>();
        for (Puntuacion puntuacion : puntuaciones) {
            salida.add(puntuacion.getPuntos() + " " + puntuacion.getNombre());
        }
        return salida;
    }

    private String guardarJSon(List<Puntuacion> puntuaciones) {
        String string = "";
        try {
            JSONArray jsonArray = new JSONArray();
            for (Puntuacion puntuacion : puntuaciones) {
                JSONObject objeto = new JSONObject();
                objeto.put("puntos", puntuacion.getPuntos());
                objeto.put("nombre", puntuacion.getNombre());
                objeto.put("fecha", puntuacion.getFecha());
                jsonArray.put(objeto);
            }
            string = jsonArray.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return string;
    }

    private List<Puntuacion> leerJSon(String string) {
        List<Puntuacion> puntuaciones = new ArrayList<>();
        try {
            JSONArray json_array = new JSONArray(string);
            for (int i = 0; i < json_array.length(); i++) {
                JSONObject objeto = json_array.getJSONObject(i);
                puntuaciones.add(new Puntuacion(objeto.getInt("puntos"), objeto.getString("nombre"), objeto.getLong("fecha")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return puntuaciones;
    }

    public void guardarFichero(String texto){
        try {
            FileOutputStream f = context.openFileOutput(FICHERO, Context.MODE_PRIVATE);
            f.write(texto.getBytes());
            f.close();
        } catch (Exception e) {
            Log.e("Asteroides", e.getMessage(), e);
        }
    }
    public String cargarFichero() {
        String result;
        try {
            FileInputStream f = context.openFileInput(FICHERO);
            BufferedReader entrada = new BufferedReader(
                    new InputStreamReader(f));
            int n = 0;
            String linea;

            StringBuilder total = new StringBuilder();
            while ((linea = entrada.readLine()) != null) {
                total.append(linea).append('\n');
            }

            f.close();
            result = total.toString();
        } catch (Exception e) {
            result = "";
        }
        return result;
    }
}