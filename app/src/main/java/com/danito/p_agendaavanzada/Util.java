package com.danito.p_agendaavanzada;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.Base64;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

public class Util {
    public enum Layout {GRID, LINEAR}
    public enum Accion {CLICK, EDITAR, ADD, FAB_CLICK, ELIMINAR, IMAGE_CLICK}

    public static Bitmap bitmapFromUri(Uri uri, Context context) {
        ImageView imageViewTemp = new ImageView(context);
        imageViewTemp.setImageURI(uri);
        BitmapDrawable d = (BitmapDrawable) imageViewTemp.getDrawable();
        return d.getBitmap();
    }

    public static Bitmap convertirBytesBitmap(byte[] bytes) {
        try {
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public static byte[] convertirImagenBytes(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }
}
