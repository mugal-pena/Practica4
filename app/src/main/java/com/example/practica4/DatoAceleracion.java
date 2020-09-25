package com.example.practica4;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

public class DatoAceleracion implements Serializable {

    float ax, ay, az, a;


    DatoAceleracion(float ax, float ay, float az, float a) {

        this.ax = ax;
        this.ay = ay;
        this.az = az;
        this.a = a;
    }


    protected DatoAceleracion(Parcel in) {
        ax = in.readFloat();
        ay = in.readFloat();
        az = in.readFloat();
        a = in.readFloat();
    }


}

