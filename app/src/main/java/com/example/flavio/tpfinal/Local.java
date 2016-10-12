package com.example.flavio.tpfinal;

import android.graphics.Bitmap;

public class Local{
    private Bitmap imagen;
    private String nombre;
    private long id;
    private String direccion;
    private String telefono;
    private float rating;
    private String descripcion;
    private double distancia;
    private String rubro;

    public String getRubro() {
        return rubro;
    }
    public void setRubro(String rubro) {
        this.rubro = rubro;
    }
    public double getDistancia() {
        return distancia;
    }
    public void setDistancia(double distancia) {
        this.distancia = distancia;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public float getRating() {
        return rating;
    }
    public void setRating(float rating) {
        this.rating = rating;
    }
    public String getDireccion() {
        return direccion;
    }
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
    public String getTelefono() {
        return telefono;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public Bitmap getImagen() {
        return imagen;
    }
    public void setImagen(Bitmap imagen) {
        this.imagen = imagen;
    }

    public Local(String telefono, String nombre, Bitmap imagen, long id, String direccion,float rating,String descripcion, double distancia,String rubro) {
        this.telefono = telefono;
        this.nombre = nombre;
        this.imagen = imagen;
        this.id = id;
        this.direccion = direccion;
        this.rating=rating;
        this.descripcion=descripcion;
        this.distancia=distancia;
        this.rubro=rubro;
    }
}
