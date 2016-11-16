package com.example.asteroides;

import android.content.Intent;
import android.content.SharedPreferences;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements GestureOverlayView.OnGesturePerformedListener {
    private GestureLibrary libreria;
    private Button bAcercaDe;
    private Button bJugar;
    private Button bPuntuaciones;
    private Button bConfigurar;

    public static AlmacenPuntuaciones almacen = new AlmacenPuntuacionesArray();
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bJugar = (Button) findViewById(R.id.button_jugar);
        bJugar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                lanzarJuego(null);
            }
        });

        bAcercaDe = (Button) findViewById(R.id.button_acerca_de);
        bAcercaDe.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                lanzarAcercaDe(null);
            }
        });

        bConfigurar = (Button) findViewById(R.id.button_configurar);
        bConfigurar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                lanzarPreferencias(null);
            }
        });

        bPuntuaciones = (Button) findViewById(R.id.button_puntuaciones);
        bPuntuaciones.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                lanzarPuntuaciones(null);
            }
        });


        Animation animacion_aparecer = AnimationUtils.loadAnimation(this, R.anim.aparecer);

        Animation animacion_arriba = AnimationUtils.loadAnimation(this, R.anim.desplazamiento_arriba);

        TextView texto = (TextView) findViewById(R.id.tv_titulo);
        texto.startAnimation(animacion_aparecer);

        bJugar.startAnimation(animacion_arriba);
        bConfigurar.startAnimation(animacion_arriba);
        bAcercaDe.startAnimation(animacion_arriba);
        bPuntuaciones.startAnimation(animacion_arriba);

        /*
        Animation animacion = AnimationUtils.loadAnimation(this, R.anim.giro_con_zoom);
        texto.startAnimation(animacion);


        Animation animacion1 = AnimationUtils.loadAnimation(this, R.anim.aparecer);
        bJugar.startAnimation(animacion1);

        Animation animacion2 = AnimationUtils.loadAnimation(this, R.anim.desplazamiento_derecha);
        bConfigurar.startAnimation(animacion2);

        Animation animacion3 = AnimationUtils.loadAnimation(this, R.anim.desplazamiento_izquierda);
        bAcercaDe.startAnimation(animacion3);

        Animation animacion4 = AnimationUtils.loadAnimation(this, R.anim.desplazamiento_arriba);
        bPuntuaciones.startAnimation(animacion4);
        */

        libreria = GestureLibraries.fromRawResource(this, R.raw.gestures);
        if (!libreria.load()) {
            finish();
        }

        mp = MediaPlayer.create(this, R.raw.audio);
        mp.start();

        GestureOverlayView gesturesView =
                (GestureOverlayView) findViewById(R.id.gestures);
        gesturesView.addOnGesturePerformedListener(this);

        //almacen = new AlmacenPuntuacionesPreferencias(this);
        //almacen = new AlmacenPuntuacionesFicheroExterno(this);
        almacen = new AlmacenPuntuacionesFicheroInterno(this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true; /** true -> el menú ya está visible */}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            lanzarPreferencias(null);
            return true;
        }
        if (id == R.id.acercaDe) {
            lanzarAcercaDe(null);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void lanzarSalir(View view) {
        finish();
    }

    public void lanzarAcercaDe(View view) {
        Intent i = new Intent(this, AcercaDeActivity.class);
        startActivity(i);
    }

    public void lanzarPreferencias(View view) {
        Intent i = new Intent(this, Preferencias.class);
        startActivity(i);
    }

    public void lanzarPuntuaciones(View view) {
        Intent i = new Intent(this, Puntuaciones.class);
        startActivity(i);
    }

    public void lanzarJuego(View view) {
        Intent i = new Intent(this, Juego.class);
        startActivityForResult(i, 1234);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1234 && resultCode == RESULT_OK && data != null) {
            int puntuacion = data.getExtras().getInt("puntuacion");
            String nombre = "Yo";
            // Mejor leer nombre desde un AlertDialog.Builder o preferencias
            almacen.guardarPuntuacion(puntuacion, nombre,
                    System.currentTimeMillis());
            lanzarPuntuaciones(null);
        }
    }

    public void mostrarPreferencias(View view) {
        SharedPreferences pref =
                PreferenceManager.getDefaultSharedPreferences(this);
        String s = "música: " + pref.getBoolean("musica", true)
                + ", gráficos: " + pref.getString("graficos", "?")
                + ", fragmentos: " + pref.getString("fragmentos", "?")
                + ", multijugador: " + pref.getBoolean("multijugador", false)
                + ", jugadores_maximos: " + pref.getString("jugadores_maximos", "?")
                + ", conexion: " + pref.getString("conexion", "?");
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    public void onGesturePerformed(GestureOverlayView ov, Gesture gesture) {
        ArrayList<Prediction> predictions = libreria.recognize(gesture);
        if (predictions.size() > 0) {
            String comando = predictions.get(0).name;
            if (comando.equals("play")) {
                lanzarJuego(null);
            } else if (comando.equals("configurar")) {
                lanzarPreferencias(null);
            } else if (comando.equals("acerca_de")) {
                lanzarAcercaDe(null);
            } else if (comando.equals("cancelar")) {
                finish();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mp.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mp != null && !mp.isPlaying()) mp.start();
    }

    @Override
    protected void onSaveInstanceState(Bundle guardarEstado) {
        super.onSaveInstanceState(guardarEstado);
        if (mp != null) {
            int pos = mp.getCurrentPosition();
            guardarEstado.putInt("posicion", pos);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle recEstado) {
        super.onRestoreInstanceState(recEstado);
        if (recEstado != null && mp != null) {
            int pos = recEstado.getInt("posicion");
            mp.seekTo(pos);
        }
    }

  /*@Override
  protected void onStop() {
      super.onPause();
      mp.pause();
  }

    @Override
    protected void onStart() {
        super.onResume();
        mp.start();
    }*/
}
