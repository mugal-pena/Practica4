package com.example.practica4;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

public interface onChartValueSelectedListener {


    public void onValueSelected(Entry e, Highlight h);

    public void onNothingSelected();
}
