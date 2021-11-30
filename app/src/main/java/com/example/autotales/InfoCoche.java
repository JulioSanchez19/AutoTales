package com.example.autotales;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;

public class InfoCoche extends AppCompatActivity {

    private TextView lblVPrecio;
    private TextView lblVCombustible;
    private TextView lblVKilometros;
    private TextView lblVPotencia;
    private TextView lblVCambio;
    private TextView lblVPlazas;
    private TextView lblVPuertas;
    private TextView txtDescripcion;
    private Button btnFavorito;
    private boolean favorito;
    private ImageView imagen;

    private int PERMISO_LLAMADA=110;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_coche);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

        DecimalFormat formateo= new DecimalFormat("###,###.##");

        lblVCambio=findViewById(R.id.lblValorCambio);
        lblVPlazas=findViewById(R.id.lblValorPlazas);
        lblVPotencia=findViewById(R.id.lblValorPotencia);
        lblVPrecio=findViewById(R.id.lblPrecioInfo);
        lblVPuertas=findViewById(R.id.lblValorPuertas);
        lblVCombustible=findViewById(R.id.lblValorCombustible);
        lblVKilometros=findViewById(R.id.lblValorKilometros);
        txtDescripcion=findViewById(R.id.txtDescripcion);
        btnFavorito=findViewById(R.id.btnFavorito);

        imagen=findViewById(R.id.main_backdrop);

        Bundle bundle=getIntent().getExtras();
        lblVKilometros.setText(String.valueOf(bundle.getInt("kilometros")));
        lblVPlazas.setText(String.valueOf(bundle.getInt("plazas")));
        lblVPotencia.setText(String.valueOf(bundle.getInt("potencia")));
        lblVPrecio.setText(String.valueOf(formateo.format(bundle.getInt("precio")))+" €");
        lblVPuertas.setText(String.valueOf(bundle.getInt("puertas")));
        lblVCombustible.setText(bundle.getString("combustible"));
        lblVCambio.setText(bundle.getString("cambio"));
        txtDescripcion.setText(bundle.getString("descripcion"));
        setTitle(bundle.getString("marca")+" "+bundle.getString("modelo"));

        if(bundle.getBoolean("favorito")){
            btnFavorito.setText("Eliminar\nfavoritos");
        }else{
            btnFavorito.setText("Añadir\nfavoritos");
        }

        String uriImagen="@drawable/"+bundle.getString("modelo").toLowerCase();
        int imageResource = getResources().getIdentifier(uriImagen, null, getPackageName());
        if(imageResource!=0){
            imagen.setImageResource(imageResource);
        }else{
            int imageRsc = getResources().getIdentifier("@drawable/cochepordefecto", null, getPackageName());
            imagen.setImageResource(imageRsc);
        }
    }

    @Override
    public void onBackPressed(){
        Intent resultado = new Intent();
        resultado.putExtra("favorito",0);
        setResult(RESULT_CANCELED, resultado);
        finish();
    }

    public void btnLlamar_Click(){

        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "639393933"));
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==PERMISO_LLAMADA){
            if(permissions.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                btnLlamar_Click();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void comprobarPermisos(View view) {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)== PackageManager.PERMISSION_GRANTED){
                btnLlamar_Click();
            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 100);
            }
        }else{
            btnLlamar_Click();
        }
    }

    public void btnFavorito_Click(View view){
        if(favorito){
            Toast.makeText(this,"Coche eliminado de favoritos", Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this,"Coche añadido a favoritos", Toast.LENGTH_LONG).show();
        }
        Intent resultado = new Intent();
        resultado.putExtra("favorito",1);
        setResult(RESULT_OK, resultado);
        finish();
    }
}