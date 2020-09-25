package com.example.practica4;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DeteccionAcc extends AppCompatActivity implements SensorEventListener {

    TextView lblAcc, lblAx, lblAy, lblAz, lblMostrar;
    Button btn;
    Menu menuPrincipal;
    private SensorManager gestorSensores;
    private Sensor sensorAcelerometro;


    //-------Nuevas variables a utilizar------//
    DatoAceleracion dato;
    public static ArrayList<DatoAceleracion> data = new ArrayList<DatoAceleracion>(); //Para calcular minimos/maximos y mostrar
    public static HashMap<DatoAceleracion, Integer> grafica;
    HashMap<DatoAceleracion, Integer> prueba;
    public static ArrayList<Integer> tiempos; //Para obtener en que momento ocurre cada medida
    int cuenta; //Lleva la cuenta de las mediciones
    boolean medir = true; //indica si seguimos tomando medidas cuando el sensor acaba.
    boolean huboPico;
    boolean sonido; //Reproducir sonido o no
    int posicion; //Número de cuenta en donde esta el pico;
    int numMuestras; //Numero de muestras;
    long t;
    public static int posInicial, posFinal; //Para saber qué intervalo de muestras coger
    int tiempo, instante;
    boolean mostrar;
    private long tAnterior;
    boolean inicio, primera;
    boolean hacerCaso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detectar_pico);
        gestorSensores = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorAcelerometro = gestorSensores.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gestorSensores.registerListener(this, sensorAcelerometro,
                SensorManager.SENSOR_DELAY_NORMAL);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        //----Guardar tiempo inicial----//
        tAnterior = System.currentTimeMillis();


        //-----Resetear el número de medidas-----//
        cuenta = 0;
        lblMostrar = findViewById(R.id.lblMostrar);
        mostrar = false;
        btn = findViewById(R.id.btnCancelar);

        inicio = true; //Indica la primera medida

        tAnterior = 0;
        tiempos = new ArrayList<Integer>(); //Guardamos los valores de los instantes.
        prueba = new HashMap<DatoAceleracion, Integer>();
        grafica = new HashMap<DatoAceleracion, Integer>();
        medir = true;
        huboPico = false;
        sonido = false;
        primera = true;
        hacerCaso = false;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        menuPrincipal = menu;

        return true;
    }

    void muestraMenu(boolean mostrar) {

        if (menuPrincipal != null) {
            menuPrincipal.findItem(R.id.item_grafica).setVisible(mostrar);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intento = new Intent(this, mostrarGrafica.class);

        startActivity(intento);
        return super.onOptionsItemSelected(item);
    }

    public void volver(View v) {

        if (btn.getText().equals("Detectar")) { //Si el texto que hay en el botón es detectar, lanzamos otra detección
            MediaPlayer.create(this, R.raw.comenzar).start();
            Intent intent = getIntent();
            finish();
            startActivity(intent);
            data.clear();
            tiempos.clear();
            posicion=0;
            numMuestras=0;

        } else { //Marchamos a casita
            MediaPlayer.create(this, R.raw.cancelar).start();
            Intent vuelta = new Intent(this, MainActivity.class);
            data.clear();
            tiempos.clear();

            cuenta = 0;
            startActivity(vuelta);
            posicion=0;
            numMuestras=0;

        }


    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //-----Entramos aquí cada vez que hay una medición------//

        Sensor sensor = sensorEvent.sensor;
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            numMuestras++;
            if (medir) {
                //------Declarar las etiquetas-----//
                lblAcc = (TextView) findViewById(R.id.lblAcc);
                lblAx = (TextView) findViewById(R.id.lblAx);
                lblAy = (TextView) findViewById(R.id.lblAy);
                lblAz = (TextView) findViewById(R.id.lblAz);

                //-----Obtener el momento de la medición----//

                t = (System.currentTimeMillis());
                tiempo = (int) (t - tAnterior);
                instante = (tiempo + instante);
                if (primera) {
                    instante = (0);
                    primera = false;
                }


                //----Medimos y guardamos el valor que nos entrega el sensor----//


                float ax = sensorEvent.values[0];
                float ay = sensorEvent.values[1];
                float az = sensorEvent.values[2];
                float a = (float) Math.sqrt(ax * ax + ay * ay + az * az);
                dato = new DatoAceleracion(ax, ay, az, a);

                data.add(dato); //Lo vamos a necesitar para gestionarDatos();
                tiempos.add((instante));
                //prueba.put(dato, tiempos.get(numMuestras - 1));
                if (a - 9.81 > 1) {
                    huboPico = true;
                    sonido = true;
                    posicion = numMuestras - 1; //Guardamos donde se encontro el pico

                }


                //------Mostrar la medición actual-----//
                lblAx.setText(String.format("ax=%.2f ", ax));
                lblAy.setText(String.format("ay=%.2f ", ay));
                lblAz.setText(String.format("az=%.2f ", az));
                lblAcc.setText(String.format("a=%.2f ", a));


                //-----Comprobamos si es un pico----//
                if (huboPico) {
                    if (sonido) {
                        MediaPlayer.create(this, R.raw.pico).start();
                        sonido = false;
                        cuenta = 0;

                    }

                    //-----Incrementar la medicion----//
                    cuenta++;


                    //----Mostrar progreso-----//
                    int porcentaje = (cuenta * 100 / 200);
                    String mensaje = "Detectando " + porcentaje + "%";
                    lblMostrar.setText(mensaje);
                    mostrar = false;

                    //-----Si ya llevamos 200 mediciones, borramos y dejamos de medir---//
                    if (cuenta == 200) {
                        huboPico = false;
                        mostrar = true; //Para mostrar el menú
                        cuenta = 0;
                        medir = false;
                        //----Dejamos de escuchar al sensor-----//
                        gestorSensores.unregisterListener(this);

                        //-------Detectar valor máximo de los medidos-----//
                       /* float amax = 0;
                        int posicionPico = 0;
                        int pos = 0;
                        for (DatoAceleracion d : prueba.keySet()) {
                            if (d.a > amax) {
                                amax = d.a;
                                posicionPico = pos;
                            }
                            pos++;

                        }*/


                        //----Obtener los valores para grafear-----//

                        posInicial = posicion - 100; //Cogemos los valores anteriores;
                        posFinal = posicion + 99; //Cogemos los valores siguientes;

                        //----En caso de que no haya muestras suficientes----//
                        if (posInicial < 0) {
                            posInicial = 0;
                            posFinal = 200 - (posicion - 1);
                        }
                        /*if (posInicial < 0) posInicial = 0;
                        //if (posFinal > numMuestras) posFinal = 200;

                        for (int i = posInicial; i <= posFinal; i++) {
                            DatoAceleracion valor = data.get(i);
                            int tiempo = tiempos.get(i);
                            grafica.put(valor, tiempo);

                        }*/


                        gestionarDatos();
                    }


                }
                /*else {
                    //----Reseteamos al llegar a 100 mientras no haya habido un pico----//
                    //----Para asegurar que el pico se encuentra antes de la mitad del muestreo----//
                    //if (cuenta == 100) cuenta = 0;

                    //-----Para que no vaya a rebentar nada----//
                    if (tiempos.size() > 1000) tiempos.clear();
                    if (data.size() > 1000) data.clear();
                    if (numMuestras>200) {numMuestras=0;} //posicion=0;};
                }*/
            }

        }
        muestraMenu(mostrar);
        tAnterior = t;

    }


    /*
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        boolean sonido=false;
        boolean fin=false;
        int valor=100;
        t = System.currentTimeMillis();
        tiempo=t-tAnterior;
        instante=tiempo+instante;
        if (inicio){
            inicio=false;
            instante=0;
        }

        tiempos.add(instante);
        if (cuenta <= valor) {

            mostrar=false;
            lblAcc = (TextView) findViewById(R.id.lblAcc);
            lblAx = (TextView) findViewById(R.id.lblAx);
            lblAy = (TextView) findViewById(R.id.lblAy);
            lblAz = (TextView) findViewById(R.id.lblAz);
            int porcentaje = (cuenta * 100 / 200);
            String mensaje = "Detectando " + porcentaje + "%";
            lblMostrar.setText(mensaje);
            Sensor sensor = sensorEvent.sensor;
            if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float ax = sensorEvent.values[0];
                float ay = sensorEvent.values[1];
                float az = sensorEvent.values[2];
                float a = (float) Math.sqrt(ax * ax + ay * ay + az * az);
                if (a > apico) {
                    apico = a;
                    pico = new DatoAceleracion(ax, ay, az, apico);
                }
                if (pico.a-9.81>1) {
                    sonido=true;
                    valor=200;
                    fin=true;
                }
                if (sonido){
                    sonido=false;
                    MediaPlayer.create(this, R.raw.pico).start();
                }

                dato = new DatoAceleracion(ax, ay, az, a);
                data.add(dato);

                diccionario.put(dato,instante); //Añadir al diccionario el Dato y el tiempo actual


                lblAx.setText(String.format("ax=%.2f ", ax));
                lblAy.setText(String.format("ay=%.2f ", ay));
                lblAz.setText(String.format("az=%.2f ", az));
                lblAcc.setText(String.format("a=%.2f ", a));
                cuenta++;
                //muestraMenu(mostrar);

            }

        } else {
            diccionario.clear();
            cuenta = 0; //Resetear cuenta


        }
        if (fin & cuenta==200){
            mostrar=true;
            gestorSensores.unregisterListener(this);
            //muestraMenu(mostrar);
            gestionarDatos();
        }




        muestraMenu(mostrar);
        tAnterior=t;
    }
*/
    void gestionarDatos() {
        //------Código para gestionar el pico----//


        //------Código para mostrar los valores------//
        float xmin, ymin, zmin, amin, xmax, ymax, zmax, amax;
        //----Empezamos en un valor que esperemos que el móvil no sufra nunca-----//
        xmin = 1000;
        ymin = 1000;
        zmin = 1000;
        amin = 1000;

        xmax = -1000;
        ymax = -1000;
        zmax = -1000;
        amax = -1000;
        for (DatoAceleracion d : data) {
            if (d.ax < xmin) xmin = d.ax;
            if (d.ay < ymin) ymin = d.ay;
            if (d.az < zmin) zmin = d.az;
            if (d.a < amin) amin = d.a;

            if (d.ax > xmax) xmax = d.ax;
            if (d.ay > ymax) ymax = d.ay;
            if (d.az > zmax) zmax = d.az;
            if (d.a > amax) amax = d.a;

        }

        //-----Mostrar los valores----//
        TextView lblXMax, lblXMin, lblYMax, lblYMin, lblZMax, lblZMin, lblAMax, lblAMin;
        lblAMax = findViewById(R.id.lblAMax);
        lblAMin = findViewById(R.id.lblAmin);

        lblXMax = findViewById(R.id.lblXMax);
        lblXMin = findViewById(R.id.lblXmin);

        lblYMax = findViewById(R.id.lblYMax);
        lblYMin = findViewById(R.id.lblYMin);

        lblZMax = findViewById(R.id.lblZMax);
        lblZMin = findViewById(R.id.lblZmin);

        lblAMax.setText(String.format("max =  %.2f", amax));
        lblAMin.setText(String.format("min =  %.2f", amin));

        lblXMax.setText(String.format("max =  %.2f", xmax));
        lblXMin.setText(String.format("min =  %.2f", xmin));

        lblYMax.setText(String.format("max =  %.2f", ymax));
        lblYMin.setText(String.format("min =  %.2f", ymin));

        lblZMax.setText(String.format("max =  %.2f", zmax));
        lblZMin.setText(String.format("min =  %.2f", zmin));

        lblMostrar.setText("Detección finalizada");
        btn.setText("Detectar");
        MediaPlayer.create(this, R.raw.finalizado).start();
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}


