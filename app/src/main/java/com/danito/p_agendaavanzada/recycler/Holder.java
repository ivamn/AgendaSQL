package com.danito.p_agendaavanzada.recycler;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.danito.p_agendaavanzada.R;
import com.danito.p_agendaavanzada.interfaces.OnImageClickListener;
import com.danito.p_agendaavanzada.pojo.Contacto;

class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private ImageView imagen;
    private TextView nombre, apellido, telefono, correo;
    private OnImageClickListener imageClickListener;
    private Contacto contacto;

    public Holder(View v) {
        super(v);
        imagen = v.findViewById(R.id.imageview);
        imagen.setOnClickListener(this);
        nombre = v.findViewById(R.id.nombre);
        apellido = v.findViewById(R.id.apellido);
        telefono = v.findViewById(R.id.telefono);
        correo = v.findViewById(R.id.correo);
    }

    public void bind(Contacto d) {
        nombre.setText(d.getNombre());
        apellido.setText(d.getApellido());
        telefono.setText(d.getTelefono());
        correo.setText(d.getCorreo());
        if (d.getImagen() != null) {
            imagen.setImageBitmap(d.getImagen());
        }
        contacto = d;
    }

    public void setImageClickListener(OnImageClickListener listener) {
        if (listener != null) {
            imageClickListener = listener;
        }
    }

    @Override
    public void onClick(View v) {
        if (imageClickListener != null) {
            imageClickListener.onImageClick(contacto, v);
        }
    }
}
