package com.example.asteroides;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.graphics.drawable.shapes.RectShape;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

/**
 * Created by motoni on 09/10/2016.
 */

public class VistaJuego extends View implements SensorEventListener {
    // //// ASTEROIDES //////
    private Vector<Grafico> asteroides; // Vector con los Asteroides
    private int numAsteroides = 5; // Número inicial de asteroides
    private int numFragmentos = 3; // Fragmentos en que se divide public

    // //// NAVE //////
    private Grafico nave; // Gráfico de la nave
    private int giroNave; // Incremento de dirección
    private double aceleracionNave; // aumento de velocidad
    private static final int MAX_VELOCIDAD_NAVE = 20; // Incremento estándar de giro y aceleración
    private static final int PASO_GIRO_NAVE = 5;
    private static final float PASO_ACELERACION_NAVE = 0.5f;

    // //// THREAD Y TIEMPO //////
    // Thread encargado de procesar el juego
    private ThreadJuego thread = new ThreadJuego();
    // Cada cuanto queremos procesar cambios (ms)
    private static int PERIODO_PROCESO = 50;
    // Cuando se realizó el último proceso
    private long ultimoProceso = 0;

    private float mX=0, mY=0;
    private boolean disparo=false;


    // //// MISIL //////
    private Vector<Grafico> misiles = new Vector<Grafico>();
    private static int PASO_VELOCIDAD_MISIL = 12;
    //private boolean misilActivo = false;
    //private int tiempoMisil;
    private Vector<Integer> tiempoMisiles = new Vector<Integer>();
    private Drawable drawableMisil;
    private int num_misiles = 2;


    // //// SENSORES //////
    private boolean utiliza_sensores=false;
    private boolean sensor_acelerometro = false;
    private boolean sensor_orientation = false;
    private boolean hayValorInicial = false;
    private float valorInicial;
    private float valorInicial_x;

    // //// MULTIMEDIA //////
    SoundPool soundPool;
    int idDisparo, idExplosion;


    // //// PUNTUACIONES //////
    private int puntuacion = 0;
    private Activity padre;

    VistaJuego(Context context, AttributeSet attrs) {
        super(context, attrs);
        Drawable drawableNave, drawableAsteroide;

        //drawableAsteroide = context.getResources().getDrawable(R.drawable.asteroide1);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        try{
            numAsteroides = Integer.parseInt(pref.getString("asteroides", String.valueOf(numAsteroides)));
        }catch(NumberFormatException e){
            numAsteroides = 5;
        }
        try{
            numFragmentos = Integer.parseInt(pref.getString("fragmentos", String.valueOf(numFragmentos)));
        }catch(NumberFormatException e){
            numFragmentos = 3;
        }

        try{
            num_misiles = Integer.parseInt(pref.getString("num_misiles", String.valueOf(num_misiles)));
        }catch(NumberFormatException e){
            num_misiles = 1;
        }

        if (pref.getString("graficos", "1").equals("0")) {

            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            Path pathNave = new Path();
            pathNave.moveTo((float) 0, (float) 0);
            pathNave.lineTo((float) 1, (float) 0.5);
            pathNave.lineTo((float) 0, (float) 1);
            pathNave.lineTo((float) 0, (float) 0);
            ShapeDrawable dNave = new ShapeDrawable(new PathShape(pathNave, 1, 1));
            dNave.getPaint().setColor(Color.WHITE);
            dNave.getPaint().setStyle(Paint.Style.STROKE);
            dNave.setIntrinsicWidth(20);
            dNave.setIntrinsicHeight(15);
            drawableNave = dNave;

            Path pathAsteroide = new Path();
            pathAsteroide.moveTo((float) 0.3, (float) 0.0);
            pathAsteroide.lineTo((float) 0.6, (float) 0.0);
            pathAsteroide.lineTo((float) 0.6, (float) 0.3);
            pathAsteroide.lineTo((float) 0.8, (float) 0.2);
            pathAsteroide.lineTo((float) 1.0, (float) 0.4);
            pathAsteroide.lineTo((float) 0.8, (float) 0.6);
            pathAsteroide.lineTo((float) 0.9, (float) 0.9);
            pathAsteroide.lineTo((float) 0.8, (float) 1.0);
            pathAsteroide.lineTo((float) 0.4, (float) 1.0);
            pathAsteroide.lineTo((float) 0.0, (float) 0.6);
            pathAsteroide.lineTo((float) 0.0, (float) 0.2);
            pathAsteroide.lineTo((float) 0.3, (float) 0.0);
            ShapeDrawable dAsteroide = new ShapeDrawable(new PathShape(pathAsteroide, 1, 1));
            dAsteroide.getPaint().setColor(Color.WHITE);
            dAsteroide.getPaint().setStyle(Paint.Style.STROKE);
            dAsteroide.setIntrinsicWidth(50);
            dAsteroide.setIntrinsicHeight(50);
            drawableAsteroide = dAsteroide;

            ShapeDrawable dMisil = new ShapeDrawable(new RectShape());
            dMisil.getPaint().setColor(Color.WHITE);
            dMisil.getPaint().setStyle(Paint.Style.STROKE);
            dMisil.setIntrinsicWidth(15);
            dMisil.setIntrinsicHeight(3);
            drawableMisil = dMisil;

            setBackgroundColor(Color.BLACK);
        } else {
            setLayerType(View.LAYER_TYPE_HARDWARE, null);

            ImageView naveAnimada = new ImageView(context); //Creo el image view
            naveAnimada.setBackgroundResource(R.drawable.animacion_nave); //le asigno la animación
            drawableNave = naveAnimada.getBackground(); //Asigno al drawable de la nave
            if (drawableNave instanceof AnimationDrawable) {
                ((AnimationDrawable) drawableNave).start();
            }
            //drawableNave = context.getResources().getDrawable(R.drawable.nave);
            drawableAsteroide = context.getResources().getDrawable(R.drawable.asteroide1);
//            drawableMisil = context.getResources().getDrawable(R.drawable.misil1);

            ImageView misilAnimado = new ImageView(context); //Creo el image view
            misilAnimado.setBackgroundResource(R.drawable.animacion); //le asigno la animación
            drawableMisil = misilAnimado.getBackground(); //Asigno al drawable de la nave
            if (drawableMisil instanceof AnimationDrawable) {
                ((AnimationDrawable) drawableMisil).start();
            }
            /*drawableMisil = (AnimationDrawable)context.getResources().getDrawable(R.drawable.animacion);
            ((AnimationDrawable)drawableMisil).start();*/
        }

        nave = new Grafico(this, drawableNave);
        //misil = new Grafico(this, drawableMisil);
        asteroides = new Vector<Grafico>();
        for (int i = 0; i < numAsteroides; i++) {
            Grafico asteroide = new Grafico(this, drawableAsteroide);
            asteroide.setIncY(Math.random() * 4 - 2);
            asteroide.setIncX(Math.random() * 4 - 2);
            asteroide.setAngulo((int) (Math.random() * 360));
            asteroide.setRotacion((int) (Math.random() * 8 - 4));
            asteroides.add(asteroide);
        }
        utiliza_sensores = false;
        if (pref.getString("sensores", "1").equals("1")) {
            utiliza_sensores=true;
            sensor_acelerometro=true;
        }
        if (pref.getString("sensores", "1").equals("2")) {
            utiliza_sensores=true;
            sensor_orientation=true;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //if((android.os.Build.VERSION.SDK_INT) == 21){

            AudioAttributes audioAttrib = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder().setAudioAttributes(audioAttrib).setMaxStreams(5).build();
        }
        else {

            soundPool = new SoundPool( 5, AudioManager.STREAM_MUSIC , 0);
        }

        idDisparo = soundPool.load(context, R.raw.disparo, 0);
        idExplosion = soundPool.load(context, R.raw.explosion, 0);

        /*if (utiliza_sensores) {
            activarSensores();
        }*/

    }

    @Override
    public boolean onTouchEvent (MotionEvent event) {
        super.onTouchEvent(event);
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                disparo=true;
                break;
            case MotionEvent.ACTION_MOVE:
                if (!utiliza_sensores) {
                    float dx = Math.abs(x - mX);
                    float dy = Math.abs(y - mY);
                    if (dy < 6 && dx > 6) {
                        giroNave = Math.round((x - mX) / 2);
                        disparo = false;
                    } else if (dx < 6 && dy > 6) {
                        if ((mY - y) > 0) aceleracionNave = Math.round((mY - y) / 25);
                        disparo = false;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                giroNave = 0;
                aceleracionNave = 0;
                if (disparo){
                    activaMisil();
                }
                break;
        }
        mX=x; mY=y;
        return true;
    }

    @Override
    public boolean onKeyDown(int codigoTecla, KeyEvent evento) {
        super.onKeyDown(codigoTecla, evento);
// Suponemos que vamos a procesar la pulsación
        boolean procesada = true;
        switch (codigoTecla) {
            case KeyEvent.KEYCODE_DPAD_UP:
                aceleracionNave = +PASO_ACELERACION_NAVE;
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                giroNave = -PASO_GIRO_NAVE;
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                giroNave = +PASO_GIRO_NAVE;
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                activaMisil();
                break;
            default:
// Si estamos aquí, no hay pulsación que nos interese
                procesada = false;
                break;
        }
        return procesada;
    }

    @Override
    public boolean onKeyUp(int codigoTecla, KeyEvent evento) {
        super.onKeyUp(codigoTecla, evento);
// Suponemos que vamos a procesar la pulsación
        boolean procesada = true;
        switch (codigoTecla) {
            case KeyEvent.KEYCODE_DPAD_UP:
                aceleracionNave = 0;
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                giroNave = 0;
                break;
            default:
// Si estamos aquí, no hay pulsación que nos interese
                procesada = false;
                break;
        }
        return procesada;
    }

    @Override
    protected void onSizeChanged(int ancho, int alto, int ancho_anter, int alto_anter) {
        super.onSizeChanged(ancho, alto, ancho_anter, alto_anter); // Una vez que conocemos nuestro ancho y alto.
        nave.setCenX(ancho / 2);
        nave.setCenY(alto / 2);
        for (Grafico asteroide : asteroides) {
            do {
                asteroide.setCenX((int) (Math.random() * ancho));
                asteroide.setCenY((int) (Math.random() * alto));
            } while (asteroide.distancia(nave) < (ancho + alto) / 5);
        }
        ultimoProceso = System.currentTimeMillis();
        thread.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        synchronized (asteroides) {
            for (Grafico asteroide : asteroides) {
                asteroide.dibujaGrafico(canvas);
            }
        }
        nave.dibujaGrafico(canvas);

        synchronized (misiles) {
            for (Grafico misil : misiles) {
                misil.dibujaGrafico(canvas);
            }
        }
        //if (misilActivo) misil.dibujaGrafico(canvas);
    }
    protected void actualizaFisica() {
        long ahora = System.currentTimeMillis();
        if (ultimoProceso + PERIODO_PROCESO > ahora) {
            return; // Salir si el período de proceso no se ha cumplido.
        }

        // Para una ejecución en tiempo real calculamos retardo
        double retardo = (ahora - ultimoProceso) / PERIODO_PROCESO;
        ultimoProceso = ahora; // Para la próxima vez

        // Actualizamos velocidad y dirección de la nave a partir de // giroNave y aceleracionNave (según la entrada del jugador)
        nave.setAngulo((int) (nave.getAngulo() + giroNave * retardo));
        double nIncX = nave.getIncX() + aceleracionNave * Math.cos(Math.toRadians(nave.getAngulo())) * retardo;
        double nIncY = nave.getIncY() + aceleracionNave * Math.sin(Math.toRadians(nave.getAngulo())) * retardo;

        // Actualizamos si el módulo de la velocidad no excede el máximo
        if (Math.hypot(nIncX, nIncY) <= MAX_VELOCIDAD_NAVE) {
            nave.setIncX(nIncX);
            nave.setIncY(nIncY);
        }
        nave.incrementaPos(retardo); // Actualizamos posición
        for (Grafico asteroide : asteroides) {
            asteroide.incrementaPos(retardo);
        }
        // Actualizamos posición de misil


        synchronized (misiles) {
            Vector<Integer> borrar_misiles = new Vector<Integer>();
            for (int i = 0; i < misiles.size(); i++) {
                misiles.get(i).incrementaPos(retardo);
                tiempoMisiles.set(i, tiempoMisiles.get(i) - (int) retardo);
                if (tiempoMisiles.get(i) < 0) {
                    borrar_misiles.addElement(i);
                } else {
                    for (int m = 0; m < asteroides.size(); m++)
                        if (misiles.get(i).verificaColision(asteroides.elementAt(m))) {
                            destruyeAsteroide(m);
                            borrar_misiles.addElement(i);
                            break;
                        }
                }
            }

            Comparator comparator = Collections.reverseOrder();
            Collections.sort(borrar_misiles,comparator);

            for (int borra_elemento : borrar_misiles) {
                tiempoMisiles.removeElementAt(borra_elemento);
                misiles.removeElementAt(borra_elemento);
            }
        }

        /*
        if (misilActivo) {
            misil.incrementaPos(retardo);
            tiempoMisil-=retardo;
            if (tiempoMisil < 0) {
                misilActivo = false;
            } else {
                for (int i = 0; i < asteroides.size(); i++)
                    if (misil.verificaColision(asteroides.elementAt(i))) {
                        destruyeAsteroide(i);
                        break;
                    }
            }
        }
        */

        for (Grafico asteroide : asteroides) {
            if (asteroide.verificaColision(nave)) {
                salir();
            }
        }
    }
    private void destruyeAsteroide(int i) {
        synchronized (asteroides) {
            asteroides.remove(i);
            soundPool.play(idExplosion, 1, 1, 0, 0, 1);
            puntuacion += 1000;
            //misilActivo = false;
            if (asteroides.isEmpty()) {
                salir();
            }
        }
    }
    private void activaMisil() {
        synchronized (misiles) {
            if (misiles.size() < num_misiles) {
                soundPool.play(idDisparo, 1, 1, 1, 0, 1);
                final Grafico misil = new Grafico(this, drawableMisil);

                misil.setCenX(nave.getCenX());
                misil.setCenY(nave.getCenY());
                misil.setAngulo(nave.getAngulo());
                misil.setIncX(Math.cos(Math.toRadians(misil.getAngulo())) * PASO_VELOCIDAD_MISIL);
                misil.setIncY(Math.sin(Math.toRadians(misil.getAngulo())) * PASO_VELOCIDAD_MISIL);

               /* SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
                if (!pref.getString("graficos", "1").equals("0"))  {
                    this.post(new Runnable() {
                        @Override
                        public void run() {
                            ((AnimationDrawable)misil.getDrawable()).start();
                        }
                    });
                }*/

                misiles.addElement(misil);
                int tiempoMisil = (int) Math.min(this.getWidth() / Math.abs(misil.getIncX()), this.getHeight() / Math.abs(misil.getIncY())) - 2;
                //misilActivo = true;

                tiempoMisiles.addElement(tiempoMisil);
            }
        }
    }

    public void activarSensores(){
        if (utiliza_sensores) {
            SensorManager mSensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
            int tipo_sensor = Sensor.TYPE_ACCELEROMETER;

            if (sensor_acelerometro) {
                tipo_sensor = Sensor.TYPE_ACCELEROMETER;
            } else {
                tipo_sensor = Sensor.TYPE_ORIENTATION;
            }
            List<Sensor> listSensors = mSensorManager.getSensorList(tipo_sensor);
            if (!listSensors.isEmpty()) {
                Sensor orientationSensor = listSensors.get(0);
                mSensorManager.registerListener(this, orientationSensor,
                        SensorManager.SENSOR_DELAY_GAME);
            }
        }
    }

    public void desactivarSensores(){
        if (utiliza_sensores) {
            SensorManager mSensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
            mSensorManager.unregisterListener(this);
        }
    }

    public ThreadJuego getThread() {
        return thread;
    }

    /*
    class ThreadJuego extends Thread {
        @Override
        public void run() {
            while (true) {
                actualizaFisica();
            }
        }
    }
    */


    class ThreadJuego extends Thread {
        private boolean pausa,corriendo;
        public synchronized void pausar() {
            pausa = true;
        }
        public synchronized void reanudar() {
            ultimoProceso = System.currentTimeMillis();
            pausa = false;
            notify();
        }
        public void detener() {
            corriendo = false;
            if (pausa) reanudar();
        }
        @Override
        public void run() {
            corriendo = true;
            while (corriendo) {
                actualizaFisica();
                synchronized (this) {
                    while (pausa) {
                        try {
                            wait();
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}





    @Override
    public void onSensorChanged(SensorEvent event) {

        if (sensor_orientation) {
            float valor = event.values[1];
            //float valor_x = event.values[0];
            if (!hayValorInicial) {
                valorInicial = valor;
                //   valorInicial_x = valor_x;
                hayValorInicial = true;
            }
            giroNave = (int) (valor - valorInicial) / 3;
        }

        if (sensor_acelerometro){
            float valor = event.values[1];
            float valor_x = event.values[0];
            if (!hayValorInicial) {
                valorInicial = valor;
                valorInicial_x = valor_x;
                hayValorInicial = true;
            }
            if ((valorInicial_x- valor_x) > 2){
                aceleracionNave = +PASO_ACELERACION_NAVE;
            }  else
            if ((valor_x- valorInicial_x) > 2){
                aceleracionNave = -PASO_ACELERACION_NAVE;
            } ;

            /*
            if ((valorInicial- valor) > 2){
                giroNave = -PASO_GIRO_NAVE;
            }  else
            if ((valor- valorInicial) > 2){
                giroNave = +PASO_GIRO_NAVE;
            } ;
            */

            if (Math.abs(valorInicial- valor) > 1) {
                giroNave = Math.round((valor - valorInicial));
            }
            if (Math.abs(valorInicial_x- valor_x) > 1) {
                aceleracionNave = Math.round((valorInicial_x - valor_x)/2);
            }

            //System.out.println(event.sensor.getName() + " - " + Float.toString(valorInicial_x) + ":" + (String)Float.toString(valor_x) + " - " + Float.toString(valorInicial_x) + ":" + Float.toString(valor));
        }


    /*
        if ((valorInicial_x- valor_x) > Math.PI/8){
            aceleracionNave = +PASO_ACELERACION_NAVE;
        }  else
        if ((valor_x- valorInicial_x) > Math.PI/8){
            aceleracionNave = -PASO_ACELERACION_NAVE;
        } ;

        Toast.makeText(getContext().getApplicationContext(), (String)Float.toString(valor_x), Toast.LENGTH_SHORT).show();
    */
    }

    public void setPadre(Activity padre) {
        this.padre = padre;
    }

    private void salir() {
        Bundle bundle = new Bundle();
        bundle.putInt("puntuacion", puntuacion);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        padre.setResult(Activity.RESULT_OK, intent);
        padre.finish();
    }
}