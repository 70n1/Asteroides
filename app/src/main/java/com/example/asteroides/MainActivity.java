package com.example.asteroides;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Button bAcercaDe;
    private Button bJugar;
    private Button bPuntuaciones;
    private Button bConfigurar;
    public static AlmacenPuntuaciones almacen = new AlmacenPuntuacionesArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bAcercaDe = (Button) findViewById(R.id.button_acerca_de);
        bAcercaDe.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                lanzarAcercaDe(null);
            }
        });
        bJugar = (Button) findViewById(R.id.button_jugar);
        bJugar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mostrarPreferencias(null);
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
}
