package com.danito.p_agendaavanzada;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.danito.p_agendaavanzada.Util.Layout;
import com.danito.p_agendaavanzada.interfaces.OnRecyclerUpdated;
import com.danito.p_agendaavanzada.pojo.Contacto;
import com.danito.p_agendaavanzada.pojo.ContactoContainer;
import com.danito.p_agendaavanzada.pojo.UpdateContainer;
import com.google.android.material.navigation.NavigationView;

import static com.danito.p_agendaavanzada.Util.convertirBytesBitmap;
import static com.danito.p_agendaavanzada.Util.convertirImagenBytes;

public class MainActivity extends AppCompatActivity {
    private BDContactos dbContactos;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Layout layout;
    private VistaContactos vistaContactos;
    private OnRecyclerUpdated onRecyclerUpdated;
    private ContactoViewModel contactoViewModel;
    private UpdateViewModel updateViewModel;
    private Contacto contactoTemp;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbContactos = new BDContactos(this, "DBCONTACTOS", null, 1);
        cargarDatos();
        updateViewModel = ViewModelProviders.of(this).get(UpdateViewModel.class);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);

        drawerLayout = findViewById(R.id.drawer_layout);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            layout = Layout.GRID;
        } else {
            layout = Layout.LINEAR;
        }

        replaceFragment();

        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                SQLiteDatabase readableDatabase = dbContactos.getReadableDatabase();
                switch (menuItem.getItemId()) {
                    case R.id.familia_menu_option:
                        cursor = readableDatabase.rawQuery("SELECT * FROM contactos WHERE familia = ?", new String[]{"1"});
                        break;
                    case R.id.amigos_menu_option:
                        cursor = readableDatabase.rawQuery("SELECT * FROM contactos WHERE amigo = ?", new String[]{"1"});
                        break;
                    case R.id.trabajo_menu_option:
                        cursor = readableDatabase.rawQuery("SELECT * FROM contactos WHERE trabajo = ?", new String[]{"1"});
                        break;
                    default:
                        cursor = readableDatabase.rawQuery("SELECT * FROM contactos", null);
                        break;
                }
                updateViewModel.setData(new UpdateContainer(layout, cursor));
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                return true;
            }
        });

        contactoViewModel = ViewModelProviders.of(this).get(ContactoViewModel.class);
        contactoViewModel.getData().observe(this, new Observer<ContactoContainer>() {
            @Override
            public void onChanged(ContactoContainer contacto) {
                switch (contacto.getAccion()) {
                    case CLICK:
                        itemClick(contacto.getContacto());
                        break;
                    case EDITAR:
                        editContact(contacto.getContacto());
                        break;
                    case ADD:
                        addContact(contacto);
                        break;
                    case FAB_CLICK:
                        fabClicked();
                        break;
                    case ELIMINAR:
                        eliminarContacto(contacto.getContacto());
                        break;
                    case IMAGE_CLICK:
                        contactoTemp = contacto.getContacto();
                        break;
                }
            }
        });
    }

    private void eliminarContacto(final Contacto contacto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Quieres eliminar el contacto de " + contacto.getNombre() + "?");
        builder.setPositiveButton("ELIMINAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dbContactos != null) {
                    SQLiteDatabase contactosDatabase = dbContactos.getWritableDatabase();
                    contactosDatabase.delete("contactos", "id = ?", new String[] {String.valueOf(contacto.getId())});
                    contactosDatabase.close();
                    actualizarDatos();
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

    private void actualizarDatos(){
        cargarDatos();
        updateViewModel.setData(new UpdateContainer(layout, cursor));
    }

    private void replaceFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        vistaContactos = new VistaContactos(layout, cursor);
        onRecyclerUpdated = vistaContactos;
        transaction.add(R.id.fragment_container, vistaContactos);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void itemClick(Contacto contacto) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        AccionContacto fragment = new AccionContacto();
        Bundle args = new Bundle();
        contactoTemp = contacto;
        args.putParcelable("contacto", contacto);
        fragment.setArguments(args);
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void editContact(Contacto contacto) {
        editarContactoDatabase(contactoTemp, contacto);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        actualizarDatos();
        VistaContactos fragment = new VistaContactos(layout, cursor);
        onRecyclerUpdated = fragment;
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void addContact(ContactoContainer contacto) {
        guardarContacto(contacto.getContacto());
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        cargarDatos();
        VistaContactos fragment = new VistaContactos(layout, cursor);
        onRecyclerUpdated = fragment;
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void fabClicked() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, new AccionContacto());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.linear_option_menu:
                layout = Layout.LINEAR;
                updateViewModel.setData(new UpdateContainer(layout, cursor));
                break;
            case R.id.grid_option_menu:
                layout = Layout.GRID;
                updateViewModel.setData(new UpdateContainer(layout, cursor));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void cargarDatos() {
        if (dbContactos != null) {
            SQLiteDatabase writableDatabase = dbContactos.getReadableDatabase();
            cursor = writableDatabase.rawQuery("SELECT * FROM contactos", null);
            /*
            Cursor cursor = contactosDatabase.rawQuery("SELECT * FROM contactos", null);
            leerContactos(cursor);
             */
        }
    }

    private void leerContactos(Cursor cursor) {
        cursor.moveToFirst();
        Contacto c;
        while (!cursor.isAfterLast()) {
            c = new Contacto();
            c.setId(cursor.getInt(0));
            c.setNombre(cursor.getString(1));
            c.setApellido(cursor.getString(2));
            c.setTelefono(cursor.getString(3));
            c.setCorreo(cursor.getString(4));
            Bitmap imagen = convertirBytesBitmap(cursor.getBlob(5));
            c.setImagen(imagen);
            c.setAmigo(cursor.getInt(6) == 1);
            c.setFamilia(cursor.getInt(7) == 1);
            c.setTrabajo(cursor.getInt(8) == 1);
            cursor.moveToNext();
        }
    }

    private void editarContactoDatabase(Contacto editado, Contacto nuevo) {
        SQLiteDatabase writableDatabase = dbContactos.getWritableDatabase();
        SQLiteDatabase readableDatabase = dbContactos.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(readableDatabase, "contactos", "id = ?", new String[]{String.valueOf(editado.getId())});
        if (count == 1){
            ContentValues values = new ContentValues();
            values.put("nombre", nuevo.getNombre());
            values.put("apellido", nuevo.getApellido());
            values.put("telefono", nuevo.getTelefono());
            values.put("correo", nuevo.getCorreo());
            Bitmap b = nuevo.getImagen();
            byte[] bytesImagen = null;
            if (b != null) {
                bytesImagen = convertirImagenBytes(nuevo.getImagen());
            }
            values.put("imagen", bytesImagen);
            values.put("amigo", nuevo.isAmigo());
            values.put("trabajo", nuevo.isTrabajo());
            values.put("familia", nuevo.isFamilia());
            writableDatabase.update("contactos", values, "id = ?", new String[]{String.valueOf(editado.getId())});
        }
        writableDatabase.close();
        readableDatabase.close();
    }

    private void guardarContacto(Contacto c) {
        SQLiteDatabase writableDatabase = dbContactos.getWritableDatabase();
        SQLiteDatabase readableDatabase = dbContactos.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", c.getNombre());
        values.put("apellido", c.getApellido());
        values.put("telefono", c.getTelefono());
        values.put("correo", c.getCorreo());
        Bitmap b = c.getImagen();
        byte[] bytesImagen = null;
        if (b != null) {
            bytesImagen = convertirImagenBytes(c.getImagen());
        }
        values.put("imagen", bytesImagen);
        values.put("amigo", c.isAmigo());
        values.put("trabajo", c.isTrabajo());
        values.put("familia", c.isFamilia());
        writableDatabase.insert("contactos", null, values);
        writableDatabase.close();
        readableDatabase.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_layout, menu);
        return true;
    }
}
