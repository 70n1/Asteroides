package com.example.asteroides;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by motoni on 25/11/2016.
 */

public class AlmacenPuntuacionesGSon implements AlmacenPuntuaciones {
    private String string;
    //Almacena puntuaciones en formato JSON
    private Gson gson = new Gson();
    private static String FICHERO = "puntuacionesGSon.txt";
    private Context context;
    //private Type type = new TypeToken<List<Puntuacion>>() {}.getType();
    private Type type = new TypeToken<Clase>() {
    }.getType();

    public AlmacenPuntuacionesGSon(Context context) {
        this.context = context;
        //guardarPuntuacion(45000, "Mi nombre", System.currentTimeMillis());
        //guardarPuntuacion(31000, "Otro nombre", System.currentTimeMillis());
    }

    @Override
    public void guardarPuntuacion(int puntos, String nombre, long fecha) {
        string = cargarFichero();
        Clase objeto =  new Clase();

        if (string!="") objeto = gson.fromJson(string, type);
        objeto.puntuaciones.add(new Puntuacion(puntos, nombre, fecha));
        string = gson.toJson(objeto, type);
        /*ArrayList<Puntuacion> puntuaciones = gson.fromJson(string, type);
        puntuaciones.add(new Puntuacion(puntos, nombre, fecha));
        string = gson.toJson(puntuaciones, type);*/
        guardarFichero(string);
    }

    @Override
    public Vector<String> listaPuntuaciones(int cantidad) {
        string = cargarFichero();
        /*ArrayList<Puntuacion> puntuaciones = gson.fromJson(string, type);
        Vector<String> salida = new Vector<>();
        for (Puntuacion puntuacion : puntuaciones) {
            salida.add(puntuacion.getPuntos() + " " + puntuacion.getNombre());
        }
        return salida;*/
        Clase objeto = gson.fromJson(string, type);
        Vector<String> salida = new Vector<>();
        for (Puntuacion puntuacion : objeto.puntuaciones) {
            salida.add(puntuacion.getPuntos() + " " + puntuacion.getNombre());
        }
        return salida;
    }

    public class Clase {
        private ArrayList<Puntuacion> puntuaciones = new ArrayList<>();
        private boolean guardado;
    }


    public void guardarFichero(String texto){
        try {
            FileOutputStream f = context.openFileOutput(FICHERO,Context.MODE_PRIVATE);
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