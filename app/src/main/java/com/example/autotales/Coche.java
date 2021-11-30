package com.example.autotales;

public class Coche implements Comparable<Coche>{
    private String marca;
    private String modelo;

    private int kilometros;
    private int precio;
    private int potencia;
    private int plazas;
    private int puertas;
    private String tipoCombustible;
    private String cambio;

    private String descripcion;

    private boolean favorito;

    private String imagen;

    public Coche(String marca, String modelo, int kilometros, int precio, int potencia, int plazas, int puertas, String tipoCombustible, String cambio, String descripcion, boolean favorito, String imagen) {
        this.marca = marca;
        this.modelo = modelo;
        this.kilometros = kilometros;
        this.precio = precio;
        this.potencia = potencia;
        this.plazas = plazas;
        this.puertas = puertas;
        this.tipoCombustible = tipoCombustible;
        this.cambio = cambio;
        this.descripcion = descripcion;
        this.favorito = favorito;
        this.imagen = imagen;
    }

    public boolean isFavorito() {
        return favorito;
    }

    public void setFavorito(boolean favorito) {
        this.favorito = favorito;
    }

    public int getPlazas() {
        return plazas;
    }

    public void setPlazas(int plazas) {
        this.plazas = plazas;
    }

    public int getPuertas() {
        return puertas;
    }

    public void setPuertas(int puertas) {
        this.puertas = puertas;
    }

    public String getCambio() {
        return cambio;
    }

    public void setCambio(String cambio) {
        this.cambio = cambio;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public int getKilometros() {
        return kilometros;
    }

    public void setKilometros(int kilometros) {
        this.kilometros = kilometros;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public int getPotencia() {
        return potencia;
    }

    public void setPotencia(int potencia) {
        this.potencia = potencia;
    }

    public String getTipoCombustible() {
        return tipoCombustible;
    }

    public void setTipoCombustible(String tipoCombustible) {
        this.tipoCombustible = tipoCombustible;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    @Override
    public int compareTo(Coche o) {
        int r=0;
        if(this.getPrecio()<o.getPrecio()){
            r=-1;
        }else{//if(this.getPrecio()>o.getPrecio()){
            r=1;
        }
        return r;
    }
}
