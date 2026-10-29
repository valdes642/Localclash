package com.example.localclash.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
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
import com.example.localclash.adapters.DeudaAdapter;
import com.example.localclash.database.DatabaseHelper;
import com.example.localclash.models.Contacto;
import com.example.localclash.models.Deuda;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DeudasActivity extends AppCompatActivity implements DeudaAdapter.OnDeudaStatusChangeListener {

    private RecyclerView rvDeudas;
    private TextView txtEmptyDeudas;
    private DeudaAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_deudas);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.deudas_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        dbHelper = new DatabaseHelper(this);


        ImageButton btnBack = findViewById(R.id.btnBackFromDeudas);
        rvDeudas = findViewById(R.id.rvDeudas);
        txtEmptyDeudas = findViewById(R.id.txtEmptyDeudas);
        ExtendedFloatingActionButton fabAgregar = findViewById(R.id.fabAgregarDeuda);


        rvDeudas.setLayoutManager(new LinearLayoutManager(this));


        btnBack.setOnClickListener(v -> finish());
        fabAgregar.setOnClickListener(v -> showCrearDeudaDialog());


        loadDeudasFromDatabase();
    }

    private void loadDeudasFromDatabase() {
        List<Deuda> lista = dbHelper.getAllDeudas();

        if (lista.isEmpty()) {
            txtEmptyDeudas.setVisibility(View.VISIBLE);
            rvDeudas.setVisibility(View.GONE);
        } else {
            txtEmptyDeudas.setVisibility(View.GONE);
            rvDeudas.setVisibility(View.VISIBLE);

            if (adapter == null) {
                adapter = new DeudaAdapter(lista, this);
                rvDeudas.setAdapter(adapter);
            } else {
                adapter.setDeudaList(lista);
            }
        }
    }

    private void showCrearDeudaDialog() {
        List<Contacto> contactos = dbHelper.getAllContactos();

        if (contactos.isEmpty()) {
            Toast.makeText(this, R.string.error_sin_contactos, Toast.LENGTH_LONG).show();
            return;
        }

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_crear_deuda, null);
        Spinner spContacto = dialogView.findViewById(R.id.spContactoDeuda);
        EditText etDescripcion = dialogView.findViewById(R.id.etDescripcionDeuda);
        EditText etMonto = dialogView.findViewById(R.id.etMontoDeuda);


        List<String> nombresContactos = new ArrayList<>();
        for (Contacto c : contactos) {
            nombresContactos.add(c.getNombre() + " (" + c.getTelefono() + ")");
        }
        ArrayAdapter<String> contactoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, nombresContactos);
        spContacto.setAdapter(contactoAdapter);

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .setPositiveButton(R.string.btn_guardar, null)
                .setNegativeButton(R.string.btn_cancelar, (d, which) -> d.dismiss())
                .create();

        dialog.show();


        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String descripcion = etDescripcion.getText().toString().trim();
            String montoStr = etMonto.getText().toString().trim();

            if (TextUtils.isEmpty(descripcion)) {
                etDescripcion.setError(getString(R.string.error_campos_incompletos));
                return;
            }

            if (TextUtils.isEmpty(montoStr)) {
                etMonto.setError(getString(R.string.error_campos_incompletos));
                return;
            }

            double monto;
            try {
                monto = Double.parseDouble(montoStr);
            } catch (NumberFormatException e) {
                etMonto.setError("Monto inválido");
                return;
            }

            Contacto contactoSeleccionado = contactos.get(spContacto.getSelectedItemPosition());
            String fechaActual = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            Deuda nuevaDeuda = new Deuda(
                    0,
                    contactoSeleccionado.getId(),
                    contactoSeleccionado.getNombre(),
                    monto,
                    descripcion,
                    "Pendiente",
                    fechaActual
            );


            long res = dbHelper.insertDeuda(nuevaDeuda);

            if (res > 0) {
                Toast.makeText(DeudasActivity.this, R.string.deuda_guardada_exito, Toast.LENGTH_SHORT).show();
                loadDeudasFromDatabase(); // Actualizar lista
                dialog.dismiss();
            } else {
                Toast.makeText(DeudasActivity.this, "Error al guardar la deuda", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStatusChange(Deuda deuda, String nuevoEstado) {
        dbHelper.updateEstadoDeuda(deuda.getId(), nuevoEstado);
        Toast.makeText(this, R.string.deuda_estado_actualizado, Toast.LENGTH_SHORT).show();
        loadDeudasFromDatabase(); // Refrescar vista
    }
}