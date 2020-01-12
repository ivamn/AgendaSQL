package com.danito.p_agendaavanzada.recycler;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.danito.p_agendaavanzada.R;
import com.danito.p_agendaavanzada.Util.Layout;
import com.danito.p_agendaavanzada.interfaces.OnImageClickListener;
import com.danito.p_agendaavanzada.pojo.Contacto;

import java.util.ArrayList;
import java.util.List;

public abstract class AbsAdaptadorCursorRecycler extends RecyclerView.Adapter {

    private Cursor cursor;

    public AbsAdaptadorCursorRecycler(Cursor cursor) {
        this.cursor = cursor;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (cursor == null) throw new IllegalStateException("Error cursor vacío");
        if (!cursor.moveToPosition(position)) throw new IllegalStateException("Error no se puede encontrar la posición " + position);
        onBindViewHolder(holder, cursor);
    }

    public abstract void onBindViewHolder(RecyclerView.ViewHolder holder, Cursor cursor);

    @Override
    public int getItemCount() {
        if (cursor != null) return cursor.getCount();
        else return 0;
    }

    @Override
    public long getItemId(int position) {
        if (hasStableIds() && cursor != null) {
            if (cursor.moveToPosition(position)) {
                return cursor.getLong(cursor.getColumnIndexOrThrow("id"));
            }
        }
        return RecyclerView.NO_ID;
    }

    public Cursor getCursor() {
        return cursor;
    }
}
