package com.example.autotales;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Base64;
import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.PriorityQueue;

public class MainActivity extends AppCompatActivity {

    private String TAG_COCHES="Coches";
    private String TAG_MARCA="Marca";
    private String TAG_MODELO="Modelo";
    private String TAG_KILOMETROS="Kilometros";
    private String TAG_PRECIO="Precio";
    private String TAG_POTENCIA="Potencia";
    private String TAG_PLAZAS="Plazas";
    private String TAG_PUERTAS="Puertas";
    private String TAG_COMBUSTIBLE="Combustible";
    private String TAG_CAMBIO="Cambio";
    private String TAG_DESCRIPCION="Descripcion";
    private String TAG_FAVORITO="Favorito";
    private String TAG_IMAGEN="Imagen";

    private ArrayList<Coche> coches;
    private ArrayList<Coche> cochesFavoritos;
    private PriorityQueue<Coche> cochesOrdenados;
    private ArrayList<Coche> cochesPorPrecio;

    private RecyclerView lstCoches;
    private AdaptadorLista adaptador;
    private AdaptadorLista adaptadorFavoritos;
    private AdaptadorLista adaptadorPorPrecio;

    private MainActivity activity= this;

    private int cocheSeleccionado;

    private boolean soloFavoritos=false;
    private boolean ordenadoPorPrecio=false;
    private boolean vistaGrid=false;
    private boolean esOrdenado=false;

    private ProgressDialog dialogoCargando;
    static final String URL_PHP_JSON = "https://jariju.000webhostapp.com/ListarCochesJSON.php";
    static final String ANIADIR_COCHE_PHP = "https://jariju.000webhostapp.com/InsertarCocheBD.php";
    static final String MODIFICAR_COCHE_FAV = "https://jariju.000webhostapp.com/ModificarCocheFav.php";
    static final String ELIMINAR_COCHE_PHP = "https://jariju.000webhostapp.com/EliminarCoche.php";
    static final String COCHES_POR_PRECIO_PHP = "https://jariju.000webhostapp.com/ListarCochesPorPrecio.php";
    static final String MODIFICAR_COCHE = "https://jariju.000webhostapp.com/ModificarCoche.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cocheSeleccionado=-1;
                Intent i = new Intent(activity, GestionarInfoCoche.class);
                i.putExtra("modificando", 0);
                i.putExtra("marca", "");
                i.putExtra("modelo", "");
                i.putExtra("kilometros", 0);
                i.putExtra("precio", 0);
                i.putExtra("potencia", 0);
                i.putExtra("combustible", "");
                i.putExtra("plazas", 0);
                i.putExtra("puertas", 0);
                i.putExtra("cambio", "");
                i.putExtra("descripcion", "");
                startActivityForResult(i,2);
            }
        });
        lstCoches=findViewById(R.id.lstCoches);
        coches= new ArrayList<Coche>();
        cochesFavoritos= new ArrayList<Coche>();
        cochesOrdenados=new PriorityQueue<Coche>();
        cochesPorPrecio=new ArrayList<Coche>();

        LeerCochesURL leerCochesURL = new LeerCochesURL();
        leerCochesURL.execute(URL_PHP_JSON);

        RecyclerView.LayoutManager mLayoutManager = new
                LinearLayoutManager(getApplicationContext());
        lstCoches.setLayoutManager(mLayoutManager);
        adaptador = new AdaptadorLista(coches);
        adaptadorFavoritos=new AdaptadorLista(cochesFavoritos);
        adaptadorPorPrecio=new AdaptadorLista(cochesPorPrecio);
        lstCoches.setAdapter(adaptador);

        lstCoches.addItemDecoration(new DividerItemDecoration(this,
                LinearLayoutManager.VERTICAL));

        Intent i = new Intent(this, InfoCoche.class);

        adaptadorFavoritos.setItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onCocheSeleccionado(int posicion) {
                i.putExtra("marca", cochesFavoritos.get(posicion).getMarca());
                i.putExtra("modelo", cochesFavoritos.get(posicion).getModelo());
                i.putExtra("kilometros", cochesFavoritos.get(posicion).getKilometros());
                i.putExtra("precio", cochesFavoritos.get(posicion).getPrecio());
                i.putExtra("potencia", cochesFavoritos.get(posicion).getPotencia());
                i.putExtra("combustible", cochesFavoritos.get(posicion).getTipoCombustible());
                i.putExtra("plazas", cochesFavoritos.get(posicion).getPlazas());
                i.putExtra("puertas", cochesFavoritos.get(posicion).getPuertas());
                i.putExtra("cambio", cochesFavoritos.get(posicion).getCambio());
                i.putExtra("descripcion", cochesFavoritos.get(posicion).getDescripcion());
                i.putExtra("favorito", cochesFavoritos.get(posicion).isFavorito());
                startActivityForResult(i, 1);

                cocheSeleccionado=posicion;

            }


         @Override
        public void onMenuContextualCoche(int posicion, MenuItem menu) {
             switch (menu.getItemId()) {
                 case R.id.verDetalles:
                     i.putExtra("marca", cochesFavoritos.get(posicion).getMarca());
                     i.putExtra("modelo", cochesFavoritos.get(posicion).getModelo());
                     i.putExtra("kilometros", cochesFavoritos.get(posicion).getKilometros());
                     i.putExtra("precio", cochesFavoritos.get(posicion).getPrecio());
                     i.putExtra("potencia", cochesFavoritos.get(posicion).getPotencia());
                     i.putExtra("combustible", cochesFavoritos.get(posicion).getTipoCombustible());
                     i.putExtra("plazas", cochesFavoritos.get(posicion).getPlazas());
                     i.putExtra("puertas", cochesFavoritos.get(posicion).getPuertas());
                     i.putExtra("cambio", cochesFavoritos.get(posicion).getCambio());
                     i.putExtra("descripcion", cochesFavoritos.get(posicion).getDescripcion());
                     i.putExtra("favorito", cochesFavoritos.get(posicion).isFavorito());
                     startActivityForResult(i, 1);

                     cocheSeleccionado=posicion;
                     break;
                 case R.id.modificar:
                     cocheSeleccionado=posicion;
                     Intent i = new Intent(activity, GestionarInfoCoche.class);
                     i.putExtra("modificando", 1);
                     i.putExtra("marca", cochesFavoritos.get(posicion).getMarca());
                     i.putExtra("modelo", cochesFavoritos.get(posicion).getModelo());
                     i.putExtra("kilometros", cochesFavoritos.get(posicion).getKilometros());
                     i.putExtra("precio", cochesFavoritos.get(posicion).getPrecio());
                     i.putExtra("potencia", cochesFavoritos.get(posicion).getPotencia());
                     i.putExtra("combustible", cochesFavoritos.get(posicion).getTipoCombustible());
                     i.putExtra("plazas", cochesFavoritos.get(posicion).getPlazas());
                     i.putExtra("puertas", cochesFavoritos.get(posicion).getPuertas());
                     i.putExtra("cambio", cochesFavoritos.get(posicion).getCambio());
                     i.putExtra("descripcion", cochesFavoritos.get(posicion).getDescripcion());
                     startActivityForResult(i,2);
                     break;
                 case R.id.noInteresa:
                     EliminarCocheURL eliminarCocheURL = new EliminarCocheURL();
                     eliminarCocheURL.execute("?modelo="+cochesFavoritos.get(posicion).getModelo());
                     cochesFavoritos.remove(cochesFavoritos.get(posicion));
                     adaptadorFavoritos.notifyDataSetChanged();
                     break;
             }
         }
    });
        adaptadorPorPrecio.setItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onCocheSeleccionado(int posicion) {
                i.putExtra("marca", cochesPorPrecio.get(posicion).getMarca());
                i.putExtra("modelo", cochesPorPrecio.get(posicion).getModelo());
                i.putExtra("kilometros", cochesPorPrecio.get(posicion).getKilometros());
                i.putExtra("precio", cochesPorPrecio.get(posicion).getPrecio());
                i.putExtra("potencia", cochesPorPrecio.get(posicion).getPotencia());
                i.putExtra("combustible", cochesPorPrecio.get(posicion).getTipoCombustible());
                i.putExtra("plazas", cochesPorPrecio.get(posicion).getPlazas());
                i.putExtra("puertas", cochesPorPrecio.get(posicion).getPuertas());
                i.putExtra("cambio", cochesPorPrecio.get(posicion).getCambio());
                i.putExtra("descripcion", cochesPorPrecio.get(posicion).getDescripcion());
                i.putExtra("favorito", cochesPorPrecio.get(posicion).isFavorito());
                startActivityForResult(i, 1);

                cocheSeleccionado=posicion;

            }
            @Override
            public void onMenuContextualCoche(int posicion, MenuItem menu) {
                switch (menu.getItemId()) {
                    case R.id.verDetalles:
                        i.putExtra("marca", cochesPorPrecio.get(posicion).getMarca());
                        i.putExtra("modelo", cochesPorPrecio.get(posicion).getModelo());
                        i.putExtra("kilometros", cochesPorPrecio.get(posicion).getKilometros());
                        i.putExtra("precio", cochesPorPrecio.get(posicion).getPrecio());
                        i.putExtra("potencia", cochesPorPrecio.get(posicion).getPotencia());
                        i.putExtra("combustible", cochesPorPrecio.get(posicion).getTipoCombustible());
                        i.putExtra("plazas", cochesPorPrecio.get(posicion).getPlazas());
                        i.putExtra("puertas", cochesPorPrecio.get(posicion).getPuertas());
                        i.putExtra("cambio", cochesPorPrecio.get(posicion).getCambio());
                        i.putExtra("descripcion", cochesPorPrecio.get(posicion).getDescripcion());
                        i.putExtra("favorito", cochesPorPrecio.get(posicion).isFavorito());
                        startActivityForResult(i, 1);

                        cocheSeleccionado=posicion;
                        break;
                    case R.id.modificar:
                        cocheSeleccionado=posicion;
                        Intent i = new Intent(activity, GestionarInfoCoche.class);
                        i.putExtra("modificando", 1);
                        i.putExtra("marca", cochesPorPrecio.get(posicion).getMarca());
                        i.putExtra("modelo", cochesPorPrecio.get(posicion).getModelo());
                        i.putExtra("kilometros", cochesPorPrecio.get(posicion).getKilometros());
                        i.putExtra("precio", cochesPorPrecio.get(posicion).getPrecio());
                        i.putExtra("potencia", cochesPorPrecio.get(posicion).getPotencia());
                        i.putExtra("combustible", cochesPorPrecio.get(posicion).getTipoCombustible());
                        i.putExtra("plazas", cochesPorPrecio.get(posicion).getPlazas());
                        i.putExtra("puertas", cochesPorPrecio.get(posicion).getPuertas());
                        i.putExtra("cambio", cochesPorPrecio.get(posicion).getCambio());
                        i.putExtra("descripcion", cochesPorPrecio.get(posicion).getDescripcion());
                        startActivityForResult(i,2);
                        break;
                    case R.id.noInteresa:
                        EliminarCocheURL eliminarCocheURL = new EliminarCocheURL();
                        eliminarCocheURL.execute("?modelo="+cochesPorPrecio.get(posicion).getModelo());
                        cochesPorPrecio.remove(cochesPorPrecio.get(posicion));
                        adaptadorPorPrecio.notifyDataSetChanged();
                        break;
                }
            }
        });
        adaptador.setItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onCocheSeleccionado(int posicion) {
                i.putExtra("marca", coches.get(posicion).getMarca());
                i.putExtra("modelo", coches.get(posicion).getModelo());
                i.putExtra("kilometros", coches.get(posicion).getKilometros());
                i.putExtra("precio", coches.get(posicion).getPrecio());
                i.putExtra("potencia", coches.get(posicion).getPotencia());
                i.putExtra("combustible", coches.get(posicion).getTipoCombustible());
                i.putExtra("plazas", coches.get(posicion).getPlazas());
                i.putExtra("puertas", coches.get(posicion).getPuertas());
                i.putExtra("cambio", coches.get(posicion).getCambio());
                i.putExtra("descripcion", coches.get(posicion).getDescripcion());
                i.putExtra("favorito", coches.get(posicion).isFavorito());
                startActivityForResult(i, 1);

                cocheSeleccionado=posicion;

            }


            @Override
            public void onMenuContextualCoche(int posicion, MenuItem menu) {
                switch (menu.getItemId()) {
                    case R.id.verDetalles:
                        i.putExtra("marca", coches.get(posicion).getMarca());
                        i.putExtra("modelo", coches.get(posicion).getModelo());
                        i.putExtra("kilometros", coches.get(posicion).getKilometros());
                        i.putExtra("precio", coches.get(posicion).getPrecio());
                        i.putExtra("potencia", coches.get(posicion).getPotencia());
                        i.putExtra("combustible", coches.get(posicion).getTipoCombustible());
                        i.putExtra("plazas", coches.get(posicion).getPlazas());
                        i.putExtra("puertas", coches.get(posicion).getPuertas());
                        i.putExtra("cambio", coches.get(posicion).getCambio());
                        i.putExtra("descripcion", coches.get(posicion).getDescripcion());
                        i.putExtra("favorito", coches.get(posicion).isFavorito());
                        startActivityForResult(i, 1);

                        cocheSeleccionado=posicion;
                        break;
                    case R.id.modificar:
                        cocheSeleccionado=posicion;
                        Intent i = new Intent(activity, GestionarInfoCoche.class);
                        i.putExtra("modificando", 1);
                        i.putExtra("marca", coches.get(posicion).getMarca());
                        i.putExtra("modelo", coches.get(posicion).getModelo());
                        i.putExtra("kilometros", coches.get(posicion).getKilometros());
                        i.putExtra("precio", coches.get(posicion).getPrecio());
                        i.putExtra("potencia", coches.get(posicion).getPotencia());
                        i.putExtra("combustible", coches.get(posicion).getTipoCombustible());
                        i.putExtra("plazas", coches.get(posicion).getPlazas());
                        i.putExtra("puertas", coches.get(posicion).getPuertas());
                        i.putExtra("cambio", coches.get(posicion).getCambio());
                        i.putExtra("descripcion", coches.get(posicion).getDescripcion());
                        startActivityForResult(i,2);
                        break;
                    case R.id.noInteresa:
                        EliminarCocheURL eliminarCocheURL = new EliminarCocheURL();
                        eliminarCocheURL.execute("?modelo="+coches.get(posicion).getModelo());
                        coches.remove(coches.get(posicion));
                        adaptador.notifyDataSetChanged();
                        break;
                }
            }
        });

        ItemTouchHelper.SimpleCallback simpleCallback=new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                EliminarCocheURL eliminarCocheURL = new EliminarCocheURL();
                eliminarCocheURL.execute("?modelo="+coches.get(viewHolder.getAdapterPosition()).getModelo());
                coches.remove(viewHolder.getAdapterPosition());
                adaptador.notifyItemRemoved(viewHolder.getAdapterPosition());
            }
        };

        ItemTouchHelper itemTouchHelper=new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(lstCoches);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.cambiarVista) {
            Toast.makeText(MainActivity.this, "item selected", Toast.LENGTH_LONG).show();
            RecyclerView.LayoutManager gridLayoutManager = new
                    GridLayoutManager(this,2);
            lstCoches.setLayoutManager(gridLayoutManager);
        }

        return super.onOptionsItemSelected(item);
    }

    public void ordenarPorPrecio(MenuItem menuItem){
        if(ordenadoPorPrecio){
            coches.clear();
            LeerCochesURL leerCochesURL= new LeerCochesURL();
            leerCochesURL.execute(URL_PHP_JSON);
            lstCoches.setAdapter(adaptador);
            ordenadoPorPrecio=false;
        }else{
            Toast.makeText(this,"Mostrando ordenados por precio", Toast.LENGTH_LONG).show();
            cochesPorPrecio.clear();
            LeerCochesURL leerCochesURL= new LeerCochesURL();
            leerCochesURL.execute(COCHES_POR_PRECIO_PHP);
            lstCoches.setAdapter(adaptadorPorPrecio);
            ordenadoPorPrecio=true;
        }
    }

        public void mostrarSoloFavoritos(MenuItem menuItem){
        if(soloFavoritos){
            coches.clear();
            LeerCochesURL leerCochesURL= new LeerCochesURL();
            leerCochesURL.execute(URL_PHP_JSON);
            lstCoches.setAdapter(adaptador);
            soloFavoritos=false;
        }else{
            Toast.makeText(this,"Mostrando solo favoritos", Toast.LENGTH_LONG).show();
            filtrarFavoritos(cochesFavoritos);
            lstCoches.setAdapter(adaptadorFavoritos);
            soloFavoritos=true;
        }
    }

    private void filtrarFavoritos(ArrayList<Coche> cochesFavoritos){
        cochesFavoritos.clear();
        for(Coche coche:coches){
            if(coche.isFavorito()==true){
                cochesFavoritos.add(coche);
            }
        }
    }

    public void cambiarVista(MenuItem menuItem){
        if(vistaGrid){
            RecyclerView.LayoutManager mLayoutManager = new
                    LinearLayoutManager(getApplicationContext());
            lstCoches.setLayoutManager(mLayoutManager);
            vistaGrid=false;
        }else{
            RecyclerView.LayoutManager gridLayoutManager = new
                    GridLayoutManager(this,2);
            lstCoches.setLayoutManager(gridLayoutManager);
            vistaGrid=true;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        String sufijoURL="";
        Boolean favorito;
        super.onActivityResult(requestCode, resultCode, data);
        Bundle b = data.getExtras();
        if (requestCode == 1){
            if (resultCode == RESULT_OK){
                if(coches.get(cocheSeleccionado).isFavorito()){
                    sufijoURL ="?favorito=0&modelo="+coches.get(cocheSeleccionado).getModelo();
                    favorito=false;
                }else{
                    sufijoURL="?favorito=1&modelo="+coches.get(cocheSeleccionado).getModelo();
                    favorito=true;
                }

                ModificarCoches modificarCochesFav = new ModificarCoches();
                modificarCochesFav.execute(MODIFICAR_COCHE_FAV + sufijoURL);
                Coche cocheModificado = new
                        Coche(coches.get(cocheSeleccionado).getMarca(), coches.get(cocheSeleccionado).getModelo(), coches.get(cocheSeleccionado).getKilometros(),
                        coches.get(cocheSeleccionado).getPrecio(), coches.get(cocheSeleccionado).getPotencia(), coches.get(cocheSeleccionado).getPlazas(),
                        coches.get(cocheSeleccionado).getPuertas(), coches.get(cocheSeleccionado).getTipoCombustible(), coches.get(cocheSeleccionado).getCambio(),
                        coches.get(cocheSeleccionado).getDescripcion(), favorito, coches.get(cocheSeleccionado).getModelo());
                coches.set(cocheSeleccionado, cocheModificado);
                adaptador.notifyDataSetChanged();
            }
        }else if (requestCode == 2){
            if (resultCode == RESULT_OK){
                if(b.getInt("modificado")==1) {
                    if (-1 == cocheSeleccionado) {

                        sufijoURL="?marca="+b.getString("marca")+"&modelo="+b.getString("modelo")+"&kilometros="+b.getInt("kilometros")
                                +"&precio="+b.getInt("precio")+"&potencia="+b.getInt("potencia")+"&plazas="+b.getInt("plazas")+"" +
                                "&puertas="+b.getInt("puertas")+"&combustible="+b.getString("combustible")+"&cambio="+b.getString("cambio")+"" +
                                "&descripcion="+b.getString("descripcion")+"&favorito="+0;

                        AniadirCocheURL aniadirCoche = new AniadirCocheURL();
                        aniadirCoche.execute(sufijoURL);

                        Coche cocheNuevo = new
                                Coche(b.getString("marca"), b.getString("modelo"), b.getInt("kilometros"),
                                b.getInt("precio"), b.getInt("potencia"), b.getInt("plazas"),
                                b.getInt("puertas"), b.getString("combustible"), b.getString("cambio"),
                                b.getString("descripcion"), false, b.getString("modelo"));


                        coches.clear();
                        LeerCochesURL leerCochesURL = new LeerCochesURL();
                        leerCochesURL.execute(URL_PHP_JSON);

                        lstCoches.setAdapter(adaptador);

                        //coches.add(cocheNuevo);
                        //adaptador.notifyDataSetChanged();

                    } else {
                        sufijoURL="?precio="+b.getInt("precio")+"&kilometros="+b.getInt("kilometros")+"&potencia="+
                                b.getInt("potencia")+"&descripcion="+b.getString("descripcion")+"&modelo="+b.getString("modelo");
                        ModificarCoches modificarCoches = new ModificarCoches();
                        modificarCoches.execute(MODIFICAR_COCHE + sufijoURL);
                        Coche cocheModificado = new
                                Coche(b.getString("marca"), b.getString("modelo"), b.getInt("kilometros"),
                                b.getInt("precio"), b.getInt("potencia"), b.getInt("plazas"),
                                b.getInt("puertas"), b.getString("combustible"), b.getString("cambio"),
                                b.getString("descripcion"), false, b.getString("modelo"));
                        coches.set(cocheSeleccionado, cocheModificado);
                        adaptador.notifyDataSetChanged();
                    }
                }
            }
        }
    }

    class AniadirCocheURL extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialogoCargando = ProgressDialog.show(MainActivity.this, "Por favor, espere...", null, true,
                    true);
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialogoCargando.dismiss();
            //Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();
        }
        @Override
        protected String doInBackground(String... params) {

            String s = params[0];

            BufferedReader bufferedReader = null;
            try {
                /*Crear un objeto URL y abrir la conexión*/
                URL url = new URL(ANIADIR_COCHE_PHP + s);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                /*Recibir el resultado de la operación de inserción*/
                bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String cadenaDevueltaPHP;
                cadenaDevueltaPHP = bufferedReader.readLine();
                return cadenaDevueltaPHP;
            } catch (Exception e) {
                return null;
            }
        }
    }

    class EliminarCocheURL extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialogoCargando = ProgressDialog.show(MainActivity.this, "Por favor, espere...", null, true,
                    true);
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialogoCargando.dismiss();
            Toast.makeText(activity,"Coche eliminado con éxito", Toast.LENGTH_LONG).show();
        }
        @Override
        protected String doInBackground(String... params) {

            String s = params[0];

            BufferedReader bufferedReader = null;
            try {
                /*Crear un objeto URL y abrir la conexión*/
                URL url = new URL(ELIMINAR_COCHE_PHP + s);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                /*Recibir el resultado de la operación de inserción*/
                bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String cadenaDevueltaPHP;
                cadenaDevueltaPHP = bufferedReader.readLine();
                return cadenaDevueltaPHP;
            } catch (Exception e) {
                return null;
            }
        }
    }

    class LeerCochesURL extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialogoCargando = ProgressDialog.show(MainActivity.this,
                    "Por favor, espere...",null,true,true);
        }
        @Override
        protected String doInBackground(String... params) {
            String uri = params[0];
            BufferedReader bufferedReader = null;
            try {
                /*Crear un objeto URL y abrir la conexión*/
                URL url = new URL(uri);
                if(uri.contains("Precio")){
                    esOrdenado=true;
                }else{
                    esOrdenado=false;
                }
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                /*Obtener el String devuelto por la URL*/
                StringBuilder sb = new StringBuilder();
                bufferedReader = new BufferedReader(new
                        InputStreamReader(con.getInputStream()));
                String cadenaDevueltaPHP;
                while((cadenaDevueltaPHP = bufferedReader.readLine())!= null){
                    sb.append(cadenaDevueltaPHP+"\n");
                }
                //Log.d("LogCat", "Contenido devuelto por el PHP\n" + sb.toString());
                return sb.toString();
            }catch(Exception e){
                return null;
            }
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            /*Ocultar el ProgressDialog*/
            dialogoCargando.dismiss();
            /*Limpiar el arrayList de Contactos y cargarlos con los datos parseados*/
            coches.removeAll(coches);

            coches = parsearJSON(s);
            /*contactos = parsearXML(s);*/
            if(coches!=null) {
                lstCoches.getAdapter().notifyDataSetChanged();
            }else{
                Toast.makeText(MainActivity.this,"Ocurrió un error de Parsing",Toast.LENGTH_SHORT).show();
            }
            Log.println(Log.INFO, "prueba", "Post execute"+String.valueOf(coches.size()));
            /*copiarCoches(coches, cochesOrdenados, 1);
            copiarCoches(cochesPorPrecio, cochesOrdenados, 2);*/
            filtrarFavoritos(cochesFavoritos);
        }
    }

    class ModificarCoches extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialogoCargando = ProgressDialog.show(MainActivity.this, "Por favor, espere...", null, true,
                    true);
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialogoCargando.dismiss();
            //Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();
        }
        @Override
        protected String doInBackground(String... params) {

            String s = params[0];

            BufferedReader bufferedReader = null;
            try {
                /*Crear un objeto URL y abrir la conexión*/
                URL url = new URL(s);
                Log.println(Log.DEBUG, "prueba", MODIFICAR_COCHE_FAV + s);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                /*Recibir el resultado de la operación de inserción*/
                bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String cadenaDevueltaPHP;
                cadenaDevueltaPHP = bufferedReader.readLine();
                return cadenaDevueltaPHP;
            } catch (Exception e) {
                return null;
            }
        }
    }

    private ArrayList<Coche> parsearJSON (String json)
    {
        JSONArray cochesJSON = null;
        JSONObject jsonObject = null;
        Coche coche;
        Boolean favorito;
        try {
            jsonObject = new JSONObject(json);
            cochesJSON = jsonObject.getJSONArray(TAG_COCHES);
            for (int i = 0; i < cochesJSON.length(); i++ ){
                jsonObject = cochesJSON.getJSONObject(i);
                if(jsonObject.getInt(TAG_FAVORITO)==0){
                    favorito=false;
                }else{
                    favorito=true;
                }
                coche = new Coche(jsonObject.getString(TAG_MARCA),
                        jsonObject.getString(TAG_MODELO), jsonObject.getInt(TAG_KILOMETROS), jsonObject.getInt(TAG_PRECIO),
                        jsonObject.getInt(TAG_POTENCIA), jsonObject.getInt(TAG_PLAZAS), jsonObject.getInt(TAG_PUERTAS),
                        jsonObject.getString(TAG_COMBUSTIBLE), jsonObject.getString(TAG_CAMBIO), jsonObject.getString(TAG_DESCRIPCION),
                        favorito, jsonObject.getString(TAG_IMAGEN));
                if(esOrdenado==false){
                    coches.add(coche);
                }else{
                    cochesPorPrecio.add(coche);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return coches;
    }

    /*private void rellenarDatosCoches(){
        coches.add(new Coche("Alfa Romeo", "Stelvio", 100, 30000,
                200, 5,5,"Gasolina", "Manual","Buen estado", false));
        coches.add(new Coche("Mercedes", "Clase A", 10000, 20000,
                150, 5, 5, "Diesel", "Manual","Caracteristicas generales:\n" +
                "\n" +
                "- Proceso de compra 100% online\n" +
                "- Transporte gratuito a domicilio\n" +
                "- Beneficiate de un 10% de descuento, de la cantidad financiada y sin importe minimo\n" +
                "- Garantia de devolucion de 14 dias o 500km\n" +
                "- Garantia Autohero de 12 meses gratuita y extensible en hasta 36 meses\n" +
                "- Y si este modelo no te cuadra tenemos mas de 800 coches en Stock\n" +
                "\n" +
                "Este precio tiene un 10% de descuento por financiacion al 8,95%\n" +
                "\n" +
                "Caracteristicas del vehiculo:\n" +
                "\n" +
                "- Libro de mantenimiento\n" +
                "- Asientos, volante y pedales deportivos\n" +
                "- Levas de cambio\n" +
                "- Sistema PDC: delantero y trasero\n" +
                "-.Faros xenon\n" +
                "VEHICULO NUEVO A ESTRENAR\n" +
                "\n" +
                "**UNIDAD EN STOCK PARA ENTREGA INMEDIATA.\n" +
                "\n" +
                "- Precio contado, 40.175 euros, con Bonificación Recogida VO W/V177/C/X118/X/H247 incluida.\n" +
                "\n" +
                "- Precio financiado, 36.900 euros, sujeto a Bonificación Recogida VO W/S/C/A205 , Generación Alternative A Compacto con PYN PAQUETE PREMIUM + PYH AMG LINE\n" +
                "\n" +
                "Para más información consulte con nuestro Asesor de ventas.\n" +
                "\n" +
                "TODO INCLUIDO: IVA deducible, transferencia, preentrega y 24 meses de garantía oficial Mercedes-Benz sin límite de kilómetros.", false));
        coches.add(new Coche("Bmw", "M3", 25000, 34000,
                270, "Gasolina", "DriveOn es el centro de vehículos de ocasión de Alphabet, " +
                "compañía de renting del Grupo BMW.\n" +
                "\n" +
                "• Todos los vehículos comercializados en DriveOn proceden de nuestra flota de renting.\n" +
                "• GARANTIA DE DEVOLUCION durante 30 días o 1000 Km.\n" +
                "• Histórico de mantenimiento, certificación de km, y de no tener daños estructurales.\n" +
                "• GARANTIA NACIONAL de 12 meses desde la fecha de compra.\n" +
                "• RESERVA SIN COMPROMISO y si no te convence, te devolvemos el dinero.\n" +
                "• ENTREGA A DOMICILIO ¡GRATIS! en toda la Península*\n" +
                "• Verificación de mas de 100 Puntos de control.\n" +
                "• COMPLETA TU COMPRA ONLINE 100%.\n" +
                "• Aceptamos tu vehículo como parte del pago.\n" +
                "• Certificado de desinfección."));
        coches.add(new Coche("Hyundai", "Kona", 5665, 17390,
                150, "Híbrido", "Hyundai KONA HEV STYLE SKY RED - HIDRIDO\n" +
                "\n" +
                "Vehículo SEMINUEVO\n" +
                "Equipamiento muy completo: Vehículo hibrido, Navegador, Climatizador, Cámara de asistencia al " +
                "estacionamiento, Volante multifunción, ESP, Cierre centralizado, Retrovisores eléctricos, Ordenador de abordo, " +
                "Elevalunas eléctricos delanteros y traseros, Radio Mp3 + AUX + USB, Sensor de luces, Bluetooth manos libres, " +
                "Control y limitador de velocidad, Airbags frontales, Airbags laterales, Airbags de cortina, Asientos traseros abatibles, techo solar.\n"));
        coches.add(new Coche("Jaguar", "Fpace", 50000, 35990,
                200, "Diesel", "Caracteristicas generales:\n" +
                "\n" +
                "- Proceso de compra 100% online\n" +
                "- Transporte gratuito a domicilio\n" +
                "- Beneficiate de un 10% de descuento, de la cantidad financiada y sin importe minimo\n" +
                "- Garantia de devolucion de 14 dias o 500km\n" +
                "- Garantia Autohero de 12 meses gratuita y extensible en hasta 36 meses\n" +
                "- Y si este modelo no te cuadra tenemos mas de 800 coches en Stock\n" +
                "\n" +
                "Este precio tiene un 10% de descuento por financiacion al 8,95%"));
        coches.add(new Coche("Honda", "Civic TOURER", 93000, 12900,
                175, "Diesel", "Además del equipamiento de serie, está unidad cuenta con:\n" +
                "-Pack Park Assist\n" +
                "Cámara trasera\n" +
                "Sensores parking delantero+trasero\n" +
                "-Blanco\n" +
                "-Tapicería Mixta Textil/Vinilo\n" +
                "-Clean zone multifiltro con sensor de partículas\n" +
                "-Llantas aleación 18\" de 5 radios dobles Negro corte diamante\n" +
                "-Rueda de repuesto"));
        coches.add(new Coche("BMW", "Serie 3", 166825, 12300,
                318, "Diesel", "Ven a visitarnos a nuestras instalaciones con " +
                "mas de 2000m bajo cita, solicita nuestro vídeo en realidad aumentada o solicita nuestro nuevo servicio " +
                "de vídeo llamada en vivo y compra online de forma totalmente garantizada sin moverte de casa, envíos a toda la península."));
        coches.add(new Coche("Volvo", "XC60", 62311, 39500,
                120, "Diesel", "Puedes ver este vehículo en una de nuestras tiendas de Madrid, Barcelona " +
                "o Valencia o elegir nuestro servicio de entrega a domicilio. Ponte en contacto con nosotros o visita nuestra página web " +
                "CarNext.com.Caquiere que la compra de tu vehículo de ocasión sea lo más sencillo posible. Todos los vehículos " +
                "que disponemos tienen menos de 5 años y han sido cuidadosamente seleccionados. Lo mejor, es que puedes comprar tu " +
                "coche online y nosotros nos encargamos de llevarte el vehículo a tu domicilio de forma gratuita.¿ Por qué elegir" +
                " CarNext.com?12 meses de garantía14 días te devolvemos tu dinero."));
    }*/
}