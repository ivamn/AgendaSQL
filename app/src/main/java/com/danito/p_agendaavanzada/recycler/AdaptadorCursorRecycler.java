package com.danito.p_agendaavanzada.recycler;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.danito.p_agendaavanzada.R;
import com.danito.p_agendaavanzada.Util;
import com.danito.p_agendaavanzada.Util.Layout;
import com.danito.p_agendaavanzada.interfaces.OnImageClickListener;
import com.danito.p_agendaavanzada.pojo.Contacto;

import java.util.ArrayList;
import java.util.List;

public class AdaptadorCursorRecycler extends AbsAdaptadorCursorRecycler
        implements View.OnClickListener, View.OnLongClickListener, View.OnTouchListener {
    private ArrayList<Contacto> contactos, contactosCompletos;
    private View.OnClickListener clickListener;
    private OnImageClickListener imageClickListener;
    private View.OnLongClickListener longClickListener;
    private View.OnTouchListener touchListener;
    private Layout layout;

    public AdaptadorCursorRecycler(Cursor c, Layout layout) {
        super(c);
        this.layout = layout;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View v;
        if (layout == Layout.GRID) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.entrada_agenda_compact, parent, false);
            v.setOnLongClickListener(this);
            v.setOnClickListener(this);
            v.setOnTouchListener(this);
            HolderCompact h = new HolderCompact(v);
            h.setImageClickListener(new OnImageClickListener() {
                @Override
                public void onImageClick(Contacto contacto, View view) {
                    imageClickListener.onImageClick(contacto, v);
                }
            });
            return h;
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.entrada_agenda, parent, false);
            v.setOnLongClickListener(this);
            v.setOnClickListener(this);
            v.setOnTouchListener(this);
            Holder h = new Holder(v);
            h.setImageClickListener(new OnImageClickListener() {
                @Override
                public void onImageClick(Contacto contacto, View view) {
                    imageClickListener.onImageClick(contacto, v);
                }
            });
            return h;
        }
    }

    private Contacto getContactoFromCursor(Cursor cursor){
        Contacto c = new Contacto();
        c.setId(cursor.getInt(0));
        c.setNombre(cursor.getString(1));
        c.setApellido(cursor.getString(2));
        c.setTelefono(cursor.getString(3));
        c.setCorreo(cursor.getString(4));
        c.setImagen(Util.convertirBytesBitmap(cursor.getBlob(5)));
        c.setAmigo(cursor.getInt(6) == 1);
        c.setFamilia(cursor.getInt(7) == 1);
        c.setTrabajo(cursor.getInt(8) == 1);
        return c;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, Cursor cursor) {
        if (layout == Layout.GRID) {
            ((HolderCompact) holder).bind(getContactoFromCursor(cursor));
        } else {
            ((Holder) holder).bind(getContactoFromCursor(cursor));
        }
    }

    public void setOnClickListener(View.OnClickListener listener) {
        if (listener != null) {
            this.clickListener = listener;
        }
    }

    @Override
    public void onClick(View v) {
        if (clickListener != null) {
            clickListener.onClick(v);
        }
    }

    public void setOnLongClickListener(View.OnLongClickListener listener) {
        if (listener != null) {
            this.longClickListener = listener;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (longClickListener != null) {
            longClickListener.onLongClick(v);
        }
        return false;
    }

    public void setOnTouchListener(View.OnTouchListener listener) {
        if (listener != null) {
            this.touchListener = listener;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (touchListener != null) {
            touchListener.onTouch(v, event);
        }
        return false;
    }

    public void setImageClickListener(OnImageClickListener listener) {
        if (listener != null) {
            imageClickListener = listener;
        }
    }

    public Contacto getItem(int position){
        getCursor().moveToPosition(position);
        return getContactoFromCursor(getCursor());
    }
}
