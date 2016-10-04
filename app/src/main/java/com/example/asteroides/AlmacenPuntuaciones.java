package com.example.asteroides;

import java.util.Vector;

/**
 * Created by AMARTIN on 04/10/2016.
 */

public interface AlmacenPuntuaciones {
    public void guardarPuntuacion(int puntos, String nombre, long fecha);

    public Vector<String> listaPuntuaciones(int cantidad);
}
