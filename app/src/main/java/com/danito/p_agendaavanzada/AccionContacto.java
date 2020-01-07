package com.danito.p_agendaavanzada;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.danito.p_agendaavanzada.pojo.Contacto;
import com.danito.p_agendaavanzada.pojo.ContactoContainer;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.danito.p_agendaavanzada.Util.bitmapFromUri;

public class AccionContacto extends Fragment implements View.OnClickListener {

    private EditText editNombre, editApellido, editTelefono, editCorreo;
    private Button aceptarButton;
    private Bitmap bitmap;
    private Contacto contacto;
    private ImageView imageView;
    private RadioButton radioButtonFamilia, radioButtonTrabajo, radioButtonAmigos;
    private final int COD_ELEGIR_IMAGEN = 2;
    private final int COD_TOMAR_FOTO = 3;
    private Util.Accion accion;
    private ContactoViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.accion_contacto, container, false);
        Bundle args = getArguments();

        viewModel = ViewModelProviders.of(getActivity()).get(ContactoViewModel.class);

        if (args != null) {
            contacto = args.getParcelable("contacto");
        }
        accion = contacto == null ? Util.Accion.ADD : Util.Accion.EDITAR;

        imageView = v.findViewById(R.id.profile_image_view);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu pop = new PopupMenu(getContext(), v);
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
                                break;
                        }
                        return true;
                    }
                });
                pop.show();
            }
        });

        radioButtonAmigos = v.findViewById(R.id.radio_amigo);
        radioButtonFamilia = v.findViewById(R.id.radio_familia);
        radioButtonTrabajo = v.findViewById(R.id.radio_trabajo);
        editNombre = v.findViewById(R.id.editNombre);
        editApellido = v.findViewById(R.id.editApellido);
        editTelefono = v.findViewById(R.id.editTelefono);
        editCorreo = v.findViewById(R.id.editCorreo);
        aceptarButton = v.findViewById(R.id.aceptar);
        aceptarButton.setOnClickListener(this);

        if (accion == Util.Accion.EDITAR) {
            if (contacto.getImagen() != null){
                imageView.setImageBitmap(contacto.getImagen());
            }
            editNombre.setText(contacto.getNombre());
            editApellido.setText(contacto.getApellido());
            editTelefono.setText(contacto.getTelefono());
            editCorreo.setText(contacto.getCorreo());
            if (contacto.isTrabajo()) radioButtonTrabajo.setChecked(true);
            else if (contacto.isAmigo()) radioButtonAmigos.setChecked(true);
            else if (contacto.isFamilia()) radioButtonFamilia.setChecked(true);
        }

        return v;
    }

    public Contacto generarNuevoContacto() {
        Contacto d = new Contacto();
        d.setAmigo(radioButtonAmigos.isChecked());
        d.setTrabajo(radioButtonTrabajo.isChecked());
        d.setFamilia(radioButtonFamilia.isChecked());
        d.setImagen(bitmap);
        d.setNombre(editNombre.getText().toString());
        d.setApellido(editApellido.getText().toString());
        d.setTelefono(editTelefono.getText().toString());
        d.setCorreo(editCorreo.getText().toString());
        return d;
    }

    public void editarContacto() {
        contacto.setImagen(bitmap);
        contacto.setAmigo(radioButtonAmigos.isChecked());
        contacto.setTrabajo(radioButtonTrabajo.isChecked());
        contacto.setFamilia(radioButtonFamilia.isChecked());
        contacto.setNombre(editNombre.getText().toString());
        contacto.setApellido(editApellido.getText().toString());
        contacto.setTelefono(editTelefono.getText().toString());
        contacto.setCorreo(editCorreo.getText().toString());
    }

    @Override
    public void onClick(View v) {
        if (nombreCorrecto(editNombre.getText().toString())) {
            if (accion == Util.Accion.EDITAR) {
                editarContacto();
                viewModel.setData(new ContactoContainer(contacto, Util.Accion.EDITAR));
            } else {
                viewModel.setData(new ContactoContainer(generarNuevoContacto(), Util.Accion.ADD));
            }
        }
    }

    private boolean nombreCorrecto(String nombre) {
        if (nombre.isEmpty()) {
            editNombre.setError("El nombre no debe estar vacío.");
            return false;
        }
        return true;
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
        if (requestCode == COD_ELEGIR_IMAGEN && resultCode == RESULT_OK && data != null) {
            bitmap = bitmapFromUri(data.getData(), getContext());
            imageView.setImageBitmap(bitmap);
        } else if (requestCode == COD_TOMAR_FOTO && resultCode == RESULT_OK && data != null) {
            bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(getContext(), "Se ha cancelado la operación", Toast.LENGTH_LONG).show();
        }
    }
}
