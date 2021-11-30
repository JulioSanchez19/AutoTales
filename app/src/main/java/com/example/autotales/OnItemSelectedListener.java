package com.example.autotales;

import android.view.MenuItem;

public interface OnItemSelectedListener {
    void onCocheSeleccionado(int posicion);
    void onMenuContextualCoche(int posicion, MenuItem menu);
}
