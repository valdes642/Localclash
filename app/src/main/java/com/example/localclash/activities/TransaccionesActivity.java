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
import com.example.localclash.adapters.TransaccionAdapter;
import com.example.localclash.database.DatabaseHelper;
import com.example.localclash.models.Cuenta;
import com.example.localclash.models.Transaccion;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransaccionesActivity extends AppCompatActivity {

    private RecyclerView rvTransacciones;
    private TextView txtEmptyTransacciones;
    private TransaccionAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_transacciones);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.transacciones_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        dbHelper = new DatabaseHelper(this);


        ImageButton btnBack = findViewById(R.id.btnBackFromTransacciones);
        rvTransacciones = findViewById(R.id.rvTransacciones);
        txtEmptyTransacciones = findViewById(R.id.txtEmptyTransacciones);
        ExtendedFloatingActionButton fabSimular = findViewById(R.id.fabSimularTransaccion);


        rvTransacciones.setLayoutManager(new LinearLayoutManager(this));


        btnBack.setOnClickListener(v -> finish());
        fabSimular.setOnClickListener(v -> showSimularTransaccionDialog());


        loadTransaccionesFromDatabase();
    }

    private void loadTransaccionesFromDatabase() {
        List<Transaccion> lista = dbHelper.getAllTransacciones();

        if (lista.isEmpty()) {
            txtEmptyTransacciones.setVisibility(View.VISIBLE);
            rvTransacciones.setVisibility(View.GONE);
        } else {
            txtEmptyTransacciones.setVisibility(View.GONE);
            rvTransacciones.setVisibility(View.VISIBLE);

            if (adapter == null) {
                adapter = new TransaccionAdapter(lista);
                rvTransacciones.setAdapter(adapter);
            } else {
                adapter.setTransaccionList(lista);
            }
        }
    }

    private void showSimularTransaccionDialog() {
        List<Cuenta> cuentas = dbHelper.getAllCuentas();

        if (cuentas.isEmpty()) {
            Toast.makeText(this, R.string.error_sin_cuentas, Toast.LENGTH_LONG).show();
            return;
        }

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_crear_transaccion, null);
        Spinner spCuenta = dialogView.findViewById(R.id.spCuenta);
        Spinner spTipo = dialogView.findViewById(R.id.spTipoTransaccion);
        EditText etDescripcion = dialogView.findViewById(R.id.etDescripcionTransaccion);
        EditText etMonto = dialogView.findViewById(R.id.etMontoTransaccion);

        List<String> nombresCuentas = new ArrayList<>();
        for (Cuenta c : cuentas) {
            nombresCuentas.add(c.getNombre() + " (Saldo: $" + (int) c.getSaldo() + ")");
        }
        ArrayAdapter<String> cuentaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, nombresCuentas);
        spCuenta.setAdapter(cuentaAdapter);

        String[] tipos = new String[]{"Egreso", "Ingreso"};
        ArrayAdapter<String> tipoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, tipos);
        spTipo.setAdapter(tipoAdapter);

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

            Cuenta cuentaSeleccionada = cuentas.get(spCuenta.getSelectedItemPosition());
            String tipoSeleccionado = (String) spTipo.getSelectedItem();

            String fechaActual = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());

            // Coordenadas simuladas en La Florida / Santiago Centro para geolocalización
            double latSimulada = -33.5227;
            double lonSimulada = -70.5983;

            Transaccion nuevaTransaccion = new Transaccion(
                    0,
                    cuentaSeleccionada.getId(),
                    tipoSeleccionado,
                    monto,
                    descripcion,
                    fechaActual,
                    latSimulada,
                    lonSimulada
            );

            long res = dbHelper.insertTransaccion(nuevaTransaccion);

            if (res > 0) {
                Toast.makeText(TransaccionesActivity.this, R.string.transaccion_exitosa, Toast.LENGTH_SHORT).show();
                loadTransaccionesFromDatabase(); // Actualizar lista
                dialog.dismiss();
            } else {
                Toast.makeText(TransaccionesActivity.this, "Error al guardar la transacción", Toast.LENGTH_SHORT).show();
            }
        });
    }
}