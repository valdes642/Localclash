package com.example.localclash.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localclash.R;
import com.example.localclash.adapters.ContactoAdapter;
import com.example.localclash.database.DatabaseHelper;
import com.example.localclash.models.Contacto;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.List;

public class ContactosActivity extends AppCompatActivity implements ContactoAdapter.OnContactoDeleteListener {

    private RecyclerView rvContactos;
    private TextView txtEmptyContactos;
    private ContactoAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contactos);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.contactos_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        dbHelper = new DatabaseHelper(this);


        ImageButton btnBack = findViewById(R.id.btnBackFromContactos);
        rvContactos = findViewById(R.id.rvContactos);
        txtEmptyContactos = findViewById(R.id.txtEmptyContactos);
        ExtendedFloatingActionButton fabAgregar = findViewById(R.id.fabAgregarContacto);


        rvContactos.setLayoutManager(new LinearLayoutManager(this));
        btnBack.setOnClickListener(v -> finish());
        fabAgregar.setOnClickListener(v -> showCrearContactoDialog());

        loadContactosFromDatabase();
    }

    private void loadContactosFromDatabase() {
        List<Contacto> lista = dbHelper.getAllContactos();

        if (lista.isEmpty()) {
            txtEmptyContactos.setVisibility(View.VISIBLE);
            rvContactos.setVisibility(View.GONE);
        } else {
            txtEmptyContactos.setVisibility(View.GONE);
            rvContactos.setVisibility(View.VISIBLE);

            if (adapter == null) {
                adapter = new ContactoAdapter(lista, this);
                rvContactos.setAdapter(adapter);
            } else {
                adapter.setContactoList(lista);
            }
        }
    }

    private void showCrearContactoDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_crear_contacto, null);
        EditText etNombre = dialogView.findViewById(R.id.etNombreContacto);
        EditText etTelefono = dialogView.findViewById(R.id.etTelefonoContacto);
        EditText etEmail = dialogView.findViewById(R.id.etEmailContacto);

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .setPositiveButton(R.string.btn_guardar, null)
                .setNegativeButton(R.string.btn_cancelar, (d, which) -> d.dismiss())
                .create();

        dialog.show();

        // Override del botón positivo para validación
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String telefono = etTelefono.getText().toString().trim();
            String email = etEmail.getText().toString().trim();

            if (TextUtils.isEmpty(nombre)) {
                etNombre.setError(getString(R.string.error_campos_incompletos));
                return;
            }

            if (TextUtils.isEmpty(telefono)) {
                etTelefono.setError(getString(R.string.error_campos_incompletos));
                return;
            }


            Contacto nuevoContacto = new Contacto(0, nombre, telefono, email);
            long res = dbHelper.insertContacto(nuevoContacto);

            if (res > 0) {
                Toast.makeText(ContactosActivity.this, R.string.contacto_guardado_exito, Toast.LENGTH_SHORT).show();
                loadContactosFromDatabase(); // Actualizar lista
                dialog.dismiss();
            } else {
                Toast.makeText(ContactosActivity.this, "Error al guardar el contacto", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDeleteClick(Contacto contacto) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.confirm_delete_contacto_title)
                .setMessage(getString(R.string.confirm_delete_contacto_msg, contacto.getNombre()))
                .setPositiveButton(R.string.btn_eliminar_contacto, (dialog, which) -> {
                    int filasBorradas = dbHelper.deleteContacto(contacto.getId());
                    if (filasBorradas > 0) {
                        Toast.makeText(ContactosActivity.this, R.string.contacto_eliminado_exito, Toast.LENGTH_SHORT).show();
                        loadContactosFromDatabase(); // Refrescar lista
                    } else {
                        Toast.makeText(ContactosActivity.this, "Error al eliminar contacto", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.btn_cancelar, (dialog, which) -> dialog.dismiss())
                .show();
    }
}