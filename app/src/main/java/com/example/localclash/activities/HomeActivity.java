package com.example.localclash.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.localclash.MainActivity;
import com.example.localclash.R;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        // Soporte EdgeToEdge para paddings de barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.home_scroll_view), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Referencias a las tarjetas de menú
        CardView cardCuentas = findViewById(R.id.cardCuentas);
        CardView cardTransacciones = findViewById(R.id.cardTransacciones);
        CardView cardContactos = findViewById(R.id.cardContactos);
        CardView cardDeudas = findViewById(R.id.cardDeudas);
        CardView cardMapas = findViewById(R.id.cardMapas);
        Button btnLogout = findViewById(R.id.btnLogout);

        // Click listener para el módulo Cuentas
        cardCuentas.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CuentasActivity.class);
            startActivity(intent);
        });

        // Click listener para el módulo Transacciones
        cardTransacciones.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, TransaccionesActivity.class);
            startActivity(intent);
        });

        cardContactos.setOnClickListener(v -> showModuleToast(getString(R.string.menu_contactos)));
        cardDeudas.setOnClickListener(v -> showModuleToast(getString(R.string.menu_deudas)));

        // Abrir el Mapa de Sucursales (Gran Santiago)
        cardMapas.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(intent);
        });

        // Cerrar sesión y volver al Login
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void showModuleToast(String moduleName) {
        Toast.makeText(this, getString(R.string.toast_module_selected, moduleName), Toast.LENGTH_SHORT).show();
    }
}