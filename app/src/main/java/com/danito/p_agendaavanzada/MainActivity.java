package com.danito.p_agendaavanzada;

import android.content.ContentValues;
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
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

import static com.danito.p_agendaavanzada.Util.convertirBytesBitmap;
import static com.danito.p_agendaavanzada.Util.convertirImagenBytes;

public class MainActivity extends AppCompatActivity {

    private BDContactos dbContactos;
    private SQLiteDatabase contactosDatabase;
    public ArrayList<Contacto> contactos;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Layout layout;
    private VistaContactos vistaContactos;
    private OnRecyclerUpdated onRecyclerUpdated;
    private ContactoViewModel viewModel;
    private Contacto contactoTemp;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbContactos = new BDContactos(this, "DBCONTACTOS", null, 1);
        cargarDatos();

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
                switch (menuItem.getItemId()) {
                    case R.id.familia_menu_option:
                        vistaContactos.adaptador.getFilter().filter("Familia");
                        break;
                    case R.id.amigos_menu_option:
                        vistaContactos.adaptador.getFilter().filter("Amigo");
                        break;
                    case R.id.trabajo_menu_option:
                        vistaContactos.adaptador.getFilter().filter("Trabajo");
                        break;
                    case R.id.todos_menu_option:
                        vistaContactos.adaptador.getFilter().filter("Todo");
                        break;
                }
                onRecyclerUpdated.onRecyclerUpdated(layout);
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                return true;
            }
        });

        viewModel = ViewModelProviders.of(this).get(ContactoViewModel.class);
        viewModel.getData().observe(this, new Observer<ContactoContainer>() {
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
                }
            }
        });
    }

    private void eliminarContacto(Contacto contacto) {
        if (dbContactos != null) {
            contactosDatabase = dbContactos.getWritableDatabase();
            contactosDatabase.delete("contactos", "id = ?", new String[] {String.valueOf(contacto.getId())});
            contactosDatabase.close();
        }
    }

    private void replaceFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        vistaContactos = new VistaContactos(contactos, layout, cursor);
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
        editarContacto(contactoTemp, contacto);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        VistaContactos fragment = new VistaContactos(contactos, layout, cursor);
        onRecyclerUpdated = fragment;
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void addContact(ContactoContainer contacto) {
        contactos.add(contacto.getContacto());
        guardarContacto(contacto.getContacto());
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        VistaContactos fragment = new VistaContactos(contactos, layout, cursor);
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
                onRecyclerUpdated.onRecyclerUpdated(Layout.LINEAR);
                break;
            case R.id.grid_option_menu:
                layout = Layout.GRID;
                onRecyclerUpdated.onRecyclerUpdated(Layout.GRID);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void cargarDatos() {
        contactos = new ArrayList<>();
        if (dbContactos != null) {
            contactosDatabase = dbContactos.getReadableDatabase();
            cursor = contactosDatabase.rawQuery("SELECT * FROM contactos", null);
            /*
            Cursor cursor = contactosDatabase.rawQuery("SELECT * FROM contactos", null);
            leerContactos(cursor);
             */
            contactosDatabase.close();
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
            contactos.add(c);
            cursor.moveToNext();
        }
    }

    private void editarContacto(Contacto editado, Contacto nuevo) {
        contactosDatabase = dbContactos.getWritableDatabase();
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
            contactosDatabase.update("contactos", values, "id = ?", new String[]{String.valueOf(editado.getId())});
        }
        contactosDatabase.close();
        readableDatabase.close();
    }

    private void guardarContacto(Contacto c) {
        contactosDatabase = dbContactos.getWritableDatabase();
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
        contactosDatabase.insert("contactos", null, values);
        contactosDatabase.close();
        readableDatabase.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_layout, menu);
        return true;
    }
}
