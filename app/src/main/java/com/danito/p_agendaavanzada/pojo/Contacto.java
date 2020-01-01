package com.danito.p_agendaavanzada.pojo;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class Contacto implements Parcelable {
    private String nombre;
    private String apellido;
    private String telefono;
    private String correo;
    private Bitmap imagen;
    private boolean amigo;
    private boolean trabajo;
    private boolean familia;

    public Contacto() {
    }

    public Contacto(String nombre, String apellido, String telefono, String correo, Bitmap imagen) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.correo = correo;
        this.imagen = imagen;
    }

    public Contacto(String nombre, String apellido, String telefono, String correo) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.correo = correo;
    }

    public Contacto(Contacto contacto) {
        this.nombre = contacto.getNombre();
        this.apellido = contacto.getApellido();
        this.telefono = contacto.getTelefono();
        this.correo = contacto.getCorreo();
        this.imagen = contacto.getImagen();
    }

    public boolean isAmigo() {
        return amigo;
    }

    public void setAmigo(boolean amigo) {
        this.amigo = amigo;
    }

    public boolean isTrabajo() {
        return trabajo;
    }

    public void setTrabajo(boolean trabajo) {
        this.trabajo = trabajo;
    }

    public boolean isFamilia() {
        return familia;
    }

    public void setFamilia(boolean familia) {
        this.familia = familia;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public Bitmap getImagen() {
        return imagen;
    }

    public void setImagen(Bitmap imagen) {
        this.imagen = imagen;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.nombre);
        dest.writeString(this.apellido);
        dest.writeString(this.telefono);
        dest.writeString(this.correo);
        dest.writeParcelable(this.imagen, flags);
    }

    protected Contacto(Parcel in) {
        this.nombre = in.readString();
        this.apellido = in.readString();
        this.telefono = in.readString();
        this.correo = in.readString();
        this.imagen = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Creator<Contacto> CREATOR = new Creator<Contacto>() {
        @Override
        public Contacto createFromParcel(Parcel source) {
            return new Contacto(source);
        }

        @Override
        public Contacto[] newArray(int size) {
            return new Contacto[size];
        }
    };
}
