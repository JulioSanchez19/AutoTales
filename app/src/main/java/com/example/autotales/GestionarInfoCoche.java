package com.example.autotales;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.Serializable;

public class GestionarInfoCoche extends AppCompatActivity {

    private int PERMISO_CAMARA=100;
    private Bitmap bitmap;
    private ImageView imagenCoche;

    private EditText txtMarca;
    private EditText txtModelo;
    private EditText txtKilometros;
    private EditText txtPrecio;
    private EditText txtPotencia;
    private EditText txtPuertas;
    private Spinner txtCambio;
    private EditText txtPlazas;
    private Spinner txtCombustible;
    private EditText txtDescripcion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestionar_info_coche);
        imagenCoche=findViewById(R.id.imgCocheNuevo);
        setTitle("Gestionar coches");
        txtMarca=findViewById(R.id.txtMarcaG);
        txtModelo=findViewById(R.id.txtModeloG);
        txtKilometros=findViewById(R.id.txtKilometrosG);
        txtPrecio=findViewById(R.id.txtPrecioG);
        txtPotencia=findViewById(R.id.txtPotenciaG);
        txtPuertas=findViewById(R.id.txtPuertaG);
        txtCambio=findViewById(R.id.txtCambioG);
        txtPlazas=findViewById(R.id.txtPlazasG);
        txtCombustible=findViewById(R.id.txtCombustibleG);
        txtDescripcion=findViewById(R.id.txtDescripcionG);

        ArrayAdapter<CharSequence> adapterCombustible = ArrayAdapter.createFromResource(this,
                R.array.tiposCombustible, android.R.layout.simple_spinner_item);
        adapterCombustible.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        txtCombustible.setAdapter(adapterCombustible);

        ArrayAdapter<CharSequence> adapterCambio = ArrayAdapter.createFromResource(this,
                R.array.tiposCambio, android.R.layout.simple_spinner_item);
        adapterCambio.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        txtCambio.setAdapter(adapterCambio);

        Bundle bundle=getIntent().getExtras();
        txtMarca.setText(bundle.getString("marca"));
        txtModelo.setText(bundle.getString("modelo"));
        txtKilometros.setText(String.valueOf(bundle.getInt("kilometros")));

        txtPlazas.setText(String.valueOf(bundle.getInt("plazas")));
        txtPotencia.setText(String.valueOf(bundle.getInt("potencia")));
        txtPrecio.setText(String.valueOf(bundle.getInt("precio")));
        txtPuertas.setText(String.valueOf(bundle.getInt("puertas")));
        switch (bundle.getString("combustible")){
            case "Diesel":
                txtCombustible.setSelection(0);
                break;
            case "Gasolina":
                txtCombustible.setSelection(1);
                break;
            case "Híbrido":
                txtCombustible.setSelection(2);
                break;
            case "Eléctrico":
                txtCombustible.setSelection(3);
                break;
        }

        switch (bundle.getString("cambio")){
            case "Manual":
                txtCambio.setSelection(0);
                break;
            case "Automático":
                txtCambio.setSelection(1);
                break;
        }
        txtDescripcion.setText(bundle.getString("descripcion"));
        setTitle(bundle.getString("marca")+" "+bundle.getString("modelo"));
        if(bundle.getInt("modificando")==1){
            txtMarca.setEnabled(false);
            txtModelo.setEnabled(false);
            txtPlazas.setEnabled(false);
            txtPuertas.setEnabled(false);
            txtCombustible.setEnabled(false);
            txtCambio.setEnabled(false);
        }
    }

    public void guardarCoche(View view){
        if(!txtMarca.getText().toString().equals("") && !txtModelo.getText().toString().equals("")  && txtKilometros.getText()!=null && txtPrecio.getText()!=null &&
                txtPotencia.getText()!=null && txtCombustible.getSelectedItem()!=null && txtPlazas.getText()!=null & txtPuertas.getText()!=null
        && txtCambio.getSelectedItem()!=null && !txtDescripcion.getText().toString().equals("")){
            Intent i = new Intent();
            i.putExtra("modificado",1);
            i.putExtra("marca", txtMarca.getText().toString());
            i.putExtra("modelo", txtModelo.getText().toString());
            i.putExtra("kilometros", Integer.parseInt(String.valueOf(txtKilometros.getText())));
            i.putExtra("precio", Integer.parseInt(String.valueOf(txtPrecio.getText())));
            i.putExtra("potencia", Integer.parseInt(String.valueOf(txtPotencia.getText())));
            i.putExtra("combustible", txtCombustible.getSelectedItem().toString());
            i.putExtra("plazas", Integer.parseInt(String.valueOf(txtPlazas.getText())));
            i.putExtra("puertas", Integer.parseInt(String.valueOf(txtPuertas.getText())));
            i.putExtra("cambio", txtCambio.getSelectedItem().toString());
            i.putExtra("descripcion", txtDescripcion.getText().toString());
            setResult(RESULT_OK, i);
            finish();
        }else{
            Toast.makeText(this,"Debe rellenar todos los campos", Toast.LENGTH_LONG).show();
        }
    }
    public void comprobarPermisos(View view) {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED){
                hacerFoto();
            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
            }
        }else{
            hacerFoto();
        }
    }

    private void hacerFoto(){
        Intent intentCamara=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intentCamara.resolveActivity(getPackageManager())!=null){
            startActivityForResult(intentCamara, 101);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==PERMISO_CAMARA){
            if(permissions.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                hacerFoto();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==101){
            if(resultCode== Activity.RESULT_OK && data!=null){
                bitmap=(Bitmap)data.getExtras().get("data");
                imagenCoche.setImageBitmap(bitmap);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed(){
        Intent resultado = new Intent();
        resultado.putExtra("modificado",0);
        setResult(RESULT_CANCELED, resultado);
        finish();
    }

    public void limpiarCampo(View view){
        EditText et= (EditText) view;
        et.setText("");
        txtPrecio.setText("");
    }
}