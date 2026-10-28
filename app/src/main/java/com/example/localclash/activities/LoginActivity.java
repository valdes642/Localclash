package com.example.localclash.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.example.localclash.MainActivity;
import com.example.localclash.R;

import java.util.concurrent.Executor;

public class LoginActivity extends AppCompatActivity {

    private EditText etPin;
    private Button btnLoginPin;
    private Button btnBiometricLogin;
    private TextView txtStatus;

    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    // PIN predeterminado para demostración
    private static final String DEFAULT_PIN = "1234";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Referencias a los componentes de la vista
        etPin = findViewById(R.id.etPin);
        btnLoginPin = findViewById(R.id.btnLoginPin);
        btnBiometricLogin = findViewById(R.id.btnBiometricLogin);
        txtStatus = findViewById(R.id.txtStatus);

        // Configurar la autenticación por huella dactilar (Biometría)
        setupBiometricPrompt();

        // Listener para inicio de sesión por PIN
        btnLoginPin.setOnClickListener(v -> handlePinLogin());

        // Listener para inicio de sesión por Huella
        btnBiometricLogin.setOnClickListener(v -> {
            if (biometricPrompt != null && promptInfo != null) {
                biometricPrompt.authenticate(promptInfo);
            }
        });

        // Verificar disponibilidad de hardware biométrico
        checkBiometricAvailability();
    }

    private void setupBiometricPrompt() {
        Executor executor = ContextCompat.getMainExecutor(this);

        biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(LoginActivity.this, "¡Autenticación biométrica exitosa!", Toast.LENGTH_SHORT).show();
                navigateToMain();
            }

            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                txtStatus.setText("Error biométrico: " + errString);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(LoginActivity.this, "Huella no reconocida. Intenta de nuevo.", Toast.LENGTH_SHORT).show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Acceso LocalCash")
                .setSubtitle("Escanea tu huella para acceder")
                .setNegativeButtonText("Usar PIN")
                .build();
    }

    private void checkBiometricAvailability() {
        BiometricManager biometricManager = BiometricManager.from(this);
        int canAuthenticate = biometricManager.canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.BIOMETRIC_WEAK);

        if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
            txtStatus.setText("Ingresa tu PIN o usa tu huella dactilar");
            // Mostrar diálogo biométrico automáticamente al iniciar la pantalla
            biometricPrompt.authenticate(promptInfo);
        } else {
            btnBiometricLogin.setEnabled(false);
            txtStatus.setText("Biometría no disponible. Inicia sesión con PIN.");
        }
    }

    private void handlePinLogin() {
        String enteredPin = etPin.getText().toString().trim();

        if (TextUtils.isEmpty(enteredPin)) {
            etPin.setError("Por favor ingresa tu PIN");
            return;
        }

        // Validación simple de PIN (Acepta '1234' o cualquier PIN de al menos 4 dígitos)
        if (DEFAULT_PIN.equals(enteredPin) || enteredPin.length() >= 4) {
            Toast.makeText(this, "¡Bienvenido a LocalCash!", Toast.LENGTH_SHORT).show();
            navigateToMain();
        } else {
            etPin.setError("PIN incorrecto (PIN por defecto: 1234)");
        }
    }

    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Cierra LoginActivity para no volver atrás con botón back
    }
}