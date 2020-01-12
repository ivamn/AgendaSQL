package com.danito.p_agendaavanzada;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.danito.p_agendaavanzada.Util.Layout;
import com.danito.p_agendaavanzada.interfaces.OnImageClickListener;
import com.danito.p_agendaavanzada.interfaces.OnRecyclerUpdated;
import com.danito.p_agendaavanzada.pojo.Contacto;
import com.danito.p_agendaavanzada.pojo.ContactoContainer;
import com.danito.p_agendaavanzada.recycler.AdaptadorCursorRecycler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.danito.p_agendaavanzada.Util.bitmapFromUri;

public class VistaContactos extends Fragment implements View.OnClickListener, OnRecyclerUpdated {
    private final int COD_ELEGIR_IMAGEN = 1;
    private final int COD_TOMAR_FOTO = 2;
    public AdaptadorCursorRecycler adaptador;
    public RecyclerView recyclerView;
    private SwipeDetector swipeDetector;
    private int indiceListaPulsado;
    private FloatingActionButton fab;
    private Layout layout;
    private ContactoViewModel viewModel;
    private Cursor cursor;

    public VistaContactos(Layout layout, Cursor cursor) {
        this.layout = layout;
        this.cursor = cursor;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.vista_contactos, container, false);

        viewModel = ViewModelProviders.of(getActivity()).get(ContactoViewModel.class);

        fab = rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.setData(new ContactoContainer(null, Util.Accion.FAB_CLICK));
            }
        });

        swipeDetector = new SwipeDetector();
        recyclerView = rootView.findViewById(R.id.recycler);
        updateRecycler();
        return rootView;
    }

    private void mostrarPopupMenu(View v) {
        PopupMenu pop = new PopupMenu(getContext(), v);
        final Contacto contacto = adaptador.getItem(recyclerView.getChildAdapterPosition(v));
        indiceListaPulsado = recyclerView.getChildAdapterPosition(v);
        pop.getMenuInflater().inflate(R.menu.menu_foto, pop.getMenu());
        pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.camera:
                        tomarFoto();
                        break;
                    case R.id.galeria:
                        elegirImagen();
                        break;
                    case R.id.borrar:
                        contacto.setImagen(null);
                        recyclerView.setAdapter(adaptador);
                        break;
                }
                return true;
            }
        });
        pop.show();
    }

    private void eliminarContacto(View v) {
        viewModel.setData(new ContactoContainer(adaptador.getItem(recyclerView.getChildAdapterPosition(v)), Util.Accion.ELIMINAR));

    }

    private void tomarFoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(intent, COD_TOMAR_FOTO);
        }
    }

    private void elegirImagen() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(intent, COD_ELEGIR_IMAGEN);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Contacto c = adaptador.getItem(indiceListaPulsado);
        if (requestCode == COD_ELEGIR_IMAGEN && resultCode == RESULT_OK && data != null) {
            Uri rutaImagen = data.getData();
            c.setImagen(bitmapFromUri(rutaImagen, getContext()));
            viewModel.setData(new ContactoContainer(c, Util.Accion.EDITAR));
        } else if (requestCode == COD_TOMAR_FOTO && resultCode == RESULT_OK && data != null) {
            c.setImagen((Bitmap) data.getExtras().get("data"));
            viewModel.setData(new ContactoContainer(c, Util.Accion.EDITAR));
        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(getContext(), "Se ha cancelado la operación", Toast.LENGTH_LONG).show();
        }
        recyclerView.setAdapter(adaptador);
    }

    @Override
    public void onClick(View v) {
        indiceListaPulsado = recyclerView.getChildAdapterPosition(v);
        Contacto contacto = adaptador.getItem(recyclerView.getChildAdapterPosition(v));
        if (swipeDetector.swipeDetected()) {
            switch (swipeDetector.getAction()) {
                case LR:
                    llamarContacto(contacto);
                    break;
                case RL:
                    enviarMensaje(contacto);
                    break;
            }
        } else {
            //clickItemListener.onClickItemListener(contacto, recyclerView.getChildAdapterPosition(v));
            viewModel.setData(new ContactoContainer(contacto, Util.Accion.CLICK));
        }
    }

    private void llamarContacto(final Contacto d) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("¿Llamar a " + d.getNombre() + "?");
        builder.setPositiveButton("LLAMAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (d.getTelefono().isEmpty()) {
                    Toast.makeText(getContext(), "El contacto no tiene teléfono", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + d.getTelefono()));
                    if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }
            }
        });
        builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    private void enviarMensaje(final Contacto d) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("¿Enviar mensaje a " + d.getNombre() + "?");
        builder.setPositiveButton("ENVIAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (d.getCorreo().isEmpty()) {
                    Toast.makeText(getContext(), "El contacto no tiene correo electrónico", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.fromParts("mailto", d.getCorreo(), null));
                    Intent chooser = Intent.createChooser(intent, "Enviar mensaje...");
                    startActivity(chooser);
                }
            }
        });
        builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    @Override
    public void onRecyclerUpdated(Layout layout, Cursor cursor) {
        this.layout = layout;
        this.cursor = cursor;
        updateRecycler();
    }

    private void updateRecycler() {
        adaptador = new AdaptadorCursorRecycler(cursor);
        adaptador.setOnTouchListener(swipeDetector);
        adaptador.setOnClickListener(this);
        adaptador.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                eliminarContacto(v);
                return false;
            }
        });
        adaptador.setImageClickListener(new OnImageClickListener() {
            @Override
            public void onImageClick(final Contacto contacto, View v) {
                mostrarPopupMenu(v);
                viewModel.setData(new ContactoContainer(contacto, Util.Accion.IMAGE_CLICK));
            }
        });
        if (layout == Layout.GRID) {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        }
        recyclerView.setAdapter(adaptador);
    }
}
