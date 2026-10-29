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
import com.example.localclash.adapters.CuentaAdapter;
import com.example.localclash.database.DatabaseHelper;
import com.example.localclash.models.Cuenta;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.List;

public class CuentasActivity extends AppCompatActivity {

    private RecyclerView rvCuentas;
    private TextView txtEmptyCuentas;
    private CuentaAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cuentas);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cuentas_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        dbHelper = new DatabaseHelper(this);


        ImageButton btnBack = findViewById(R.id.btnBackFromCuentas);
        rvCuentas = findViewById(R.id.rvCuentas);
        txtEmptyCuentas = findViewById(R.id.txtEmptyCuentas);
        ExtendedFloatingActionButton fabAgregar = findViewById(R.id.fabAgregarCuenta);


        rvCuentas.setLayoutManager(new LinearLayoutManager(this));


        btnBack.setOnClickListener(v -> finish());
        fabAgregar.setOnClickListener(v -> showCrearCuentaDialog());


        loadCuentasFromDatabase();
    }

    private void loadCuentasFromDatabase() {
        List<Cuenta> listaCuentas = dbHelper.getAllCuentas();

        if (listaCuentas.isEmpty()) {
            txtEmptyCuentas.setVisibility(View.VISIBLE);
            rvCuentas.setVisibility(View.GONE);
        } else {
            txtEmptyCuentas.setVisibility(View.GONE);
            rvCuentas.setVisibility(View.VISIBLE);

            if (adapter == null) {
                adapter = new CuentaAdapter(listaCuentas);
                rvCuentas.setAdapter(adapter);
            } else {
                adapter.setCuentaList(listaCuentas);
            }
        }
    }

    private void showCrearCuentaDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_crear_cuenta, null);
        EditText etNombre = dialogView.findViewById(R.id.etNombreCuenta);
        EditText etTipo = dialogView.findViewById(R.id.etTipoCuenta);
        EditText etSaldo = dialogView.findViewById(R.id.etSaldoCuenta);

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .setPositiveButton(R.string.btn_guardar, null)
                .setNegativeButton(R.string.btn_cancelar, (d, which) -> d.dismiss())
                .create();

        dialog.show();


        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String tipo = etTipo.getText().toString().trim();
            String saldoStr = etSaldo.getText().toString().trim();

            if (TextUtils.isEmpty(nombre)) {
                etNombre.setError(getString(R.string.error_campos_incompletos));
                return;
            }
            if (TextUtils.isEmpty(tipo)) {
                etTipo.setError(getString(R.string.error_campos_incompletos));
                return;
            }
            if (TextUtils.isEmpty(saldoStr)) {
                etSaldo.setError(getString(R.string.error_campos_incompletos));
                return;
            }

            double saldo;
            try {
                saldo = Double.parseDouble(saldoStr);
            } catch (NumberFormatException e) {
                etSaldo.setError("Saldo inválido");
                return;
            }


            Cuenta nuevaCuenta = new Cuenta(0, nombre, tipo, saldo);
            long res = dbHelper.insertCuenta(nuevaCuenta);

            if (res > 0) {
                Toast.makeText(CuentasActivity.this, R.string.cuenta_creada_exito, Toast.LENGTH_SHORT).show();
                loadCuentasFromDatabase(); // Actualizar lista
                dialog.dismiss();
            } else {
                Toast.makeText(CuentasActivity.this, "Error al guardar en la base de datos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}