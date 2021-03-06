package com.example.asteroides;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Vector;


/**
 * Created by el70n on 02/12/2016.
 */

public class AlmacenPuntuacionesSW_PHP_propio implements AlmacenPuntuaciones {

    public Vector<String> listaPuntuaciones(int cantidad) {
        Vector<String> result = new Vector<String>();
        try {
            URL url = new URL("http://www.tiramillas.es/puntuaciones/lista.php" + "?max=20");
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
            if (conexion.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                String linea = reader.readLine();
                while (!linea.equals("")) {
                    result.add(linea);
                    linea = reader.readLine();
                }
                reader.close();
            } else {
                Log.e("Asteroides", conexion.getResponseMessage());
            }
        } catch (Exception e) {
            Log.e("Asteroides", e.getMessage(), e);
        } finally {
            //if (conexion != null) conexion.disconnect();
            return result;
        }
    }

    public void guardarPuntuacion(int puntos, String nombre, long fecha) {
        try {
            URL url = new URL("http://www.tiramillas.es/puntuaciones/nueva.php?" + "puntos=" + puntos + "&nombre=" + URLEncoder.encode(nombre, "UTF-8") + "&fecha=" + fecha);
            HttpURLConnection conexion = (HttpURLConnection) url
                    .openConnection();
            if (conexion.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                String linea = reader.readLine();
                if (!linea.equals("OK")) {
                    Log.e("Asteroides", "Error en servicio Web nueva");
                }
            } else {
                Log.e("Asteroides", conexion.getResponseMessage());
            }
        } catch (Exception e) {
            Log.e("Asteroides", e.getMessage(), e);
        } finally {
            //if (conexion != null) conexion.disconnect();
        }
    }

}
