package com.danito.p_agendaavanzada.pojo;

import android.database.Cursor;

import com.danito.p_agendaavanzada.Util;

public class UpdateContainer {
    private Util.Layout layout;
    private Cursor cursor;

    public UpdateContainer(Util.Layout layout, Cursor cursor) {
        this.layout = layout;
        this.cursor = cursor;
    }

    public UpdateContainer() {
    }

    public Util.Layout getLayout() {
        return layout;
    }

    public void setLayout(Util.Layout layout) {
        this.layout = layout;
    }

    public Cursor getCursor() {
        return cursor;
    }

    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }
}
