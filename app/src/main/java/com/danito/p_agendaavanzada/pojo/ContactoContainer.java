package com.danito.p_agendaavanzada.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.danito.p_agendaavanzada.Util;

public class ContactoContainer implements Parcelable {
    private Contacto contacto;
    private Util.Accion accion;

    public ContactoContainer(Contacto contacto, Util.Accion accion) {
        this.contacto = contacto;
        this.accion = accion;
    }

    public ContactoContainer() {
    }

    public Contacto getContacto() {
        return contacto;
    }

    public void setContacto(Contacto contacto) {
        this.contacto = contacto;
    }

    public Util.Accion getAccion() {
        return accion;
    }

    public void setAccion(Util.Accion accion) {
        this.accion = accion;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.contacto, flags);
        dest.writeInt(this.accion == null ? -1 : this.accion.ordinal());
    }

    protected ContactoContainer(Parcel in) {
        this.contacto = in.readParcelable(Contacto.class.getClassLoader());
        int tmpAccion = in.readInt();
        this.accion = tmpAccion == -1 ? null : Util.Accion.values()[tmpAccion];
    }

    public static final Parcelable.Creator<ContactoContainer> CREATOR = new Parcelable.Creator<ContactoContainer>() {
        @Override
        public ContactoContainer createFromParcel(Parcel source) {
            return new ContactoContainer(source);
        }

        @Override
        public ContactoContainer[] newArray(int size) {
            return new ContactoContainer[size];
        }
    };
}
