package com.example.practica4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
 public static   ArrayList<DatoAceleracion>dataMain;
    TextView lblMensaje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent accion= getIntent();
        dataMain= (ArrayList<DatoAceleracion>) accion.getSerializableExtra("datosGrafica");
        lblMensaje= findViewById(R.id.lblMensaje);
        lblMensaje.setText(accion.getStringExtra("mensaje"));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
       // dataMain=new ArrayList<DatoAceleracion>((Integer) accion.getSerializableExtra("datosGrafica"));

    }

    public void empezarDeteccion(View v) {
        MediaPlayer.create(this, R.raw.comenzar).start();
        Intent empezarDect = new Intent(this, DeteccionAcc.class);
        startActivity(empezarDect);
    }


    public void mostrar(View v) {
        Intent grafica= new Intent(this, mostrarGrafica.class);
        startActivity(grafica);

    }

}
