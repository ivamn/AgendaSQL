package com.danito.p_agendaavanzada.recycler;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.danito.p_agendaavanzada.R;
import com.danito.p_agendaavanzada.interfaces.OnImageClickListener;
import com.danito.p_agendaavanzada.pojo.Contacto;

class HolderCompact extends RecyclerView.ViewHolder implements View.OnClickListener {
    private ImageView imagen;
    private TextView nombre;
    private OnImageClickListener imageClickListener;
    private Contacto contacto;

    public HolderCompact(View v) {
        super(v);
        imagen = v.findViewById(R.id.imageview);
        imagen.setOnClickListener(this);
        nombre = v.findViewById(R.id.nombre);
    }

    public void bind(Contacto d) {
        nombre.setText(d.getNombre());
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
