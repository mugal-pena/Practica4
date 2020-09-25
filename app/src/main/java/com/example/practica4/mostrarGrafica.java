package com.example.practica4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.audiofx.AudioEffect;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class mostrarGrafica extends AppCompatActivity {







private ArrayList<Entry> acc;
private ArrayList<Entry>ax;
private ArrayList<Entry>ay;
private ArrayList<Entry>az;

LineChart visorGrafico;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_grafica);


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        visorGrafico=findViewById(R.id.grafica);


        acc=new ArrayList<Entry>();
        ax=new ArrayList<Entry>();
        ay=new ArrayList<Entry>();
        az=new ArrayList<Entry>();



        for (int i=DeteccionAcc.posInicial; i<DeteccionAcc.posFinal; i++){ //Iteramos las claves

            int time= DeteccionAcc.tiempos.get(i);
            DatoAceleracion dato= DeteccionAcc.data.get(i);
            ax.add(new Entry(time,dato.ax));
            ay.add(new Entry(time,dato.ay));
            az.add(new Entry(time,dato.az));
            acc.add(new Entry(time,dato.a));



        }

        //--------Código para crear las líneas------//


        //------Crear las líneas a partir del ArrayList----//
        LineDataSet lineaAceleracionX=new LineDataSet(ax,"ax");
        lineaAceleracionX.setColor(Color.RED);
        lineaAceleracionX.setDrawCircles(false);

        LineDataSet lineaAceleracionY= new LineDataSet(ay,"ay");
        lineaAceleracionY.setColor(Color.BLUE);
        lineaAceleracionY.setDrawCircles(false);

        LineDataSet lineaAceleracionZ= new LineDataSet(az,"az");
        lineaAceleracionZ.setColor(Color.GREEN);
        lineaAceleracionZ.setDrawCircles(false);

        LineDataSet lineaAceleracionA= new LineDataSet(acc,"a");
        lineaAceleracionA.setColor(Color.YELLOW);
        lineaAceleracionA.setDrawCircles(false);

        //------Añadir a la gráfica completa----//
        List<ILineDataSet> graficas = new ArrayList<ILineDataSet>();
        graficas.add(lineaAceleracionX);
        graficas.add(lineaAceleracionY);
        graficas.add(lineaAceleracionZ);
        graficas.add(lineaAceleracionA);

        //------Establecer en el visor----//
        LineData lineas= new LineData(graficas);
        visorGrafico.setData(lineas);

        Description descripcion= new Description();
        descripcion.setText("Aceleraciones detectadas");
        visorGrafico.setDescription(descripcion);


    }


}
