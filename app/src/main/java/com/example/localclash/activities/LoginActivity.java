package com.example.localclash.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.localclash.MainActivity;
import com.example.localclash.R;

import java.util.concurrent.Executor;

public class LoginActivity extends AppCompatActivity {

    private EditText etPin;
    private Button btnBiometricLogin;
    private TextView txtStatus;

    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    // PIN predeterminado para demostración
    private static final String DEFAULT_PIN = "1234";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Soporte EdgeToEdge para paddings de barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_scroll_view), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Referencias a los componentes de la vista
        etPin = findViewById(R.id.etPin);
        Button btnLoginPin = findViewById(R.id.btnLoginPin);
        btnBiometricLogin = findViewById(R.id.btnBiometricLogin);
        txtStatus = findViewById(R.id.txtStatus);

        // Configurar la autenticación por huella dactilar (Biometría)
        setupBiometricPrompt();

        // Listener para inicio de sesión por PIN
        btnLoginPin.setOnClickListener(v -> handlePinLogin());

        // Listener para inicio de sesión por Huella
        btnBiometricLogin.setOnClickListener(v -> triggerBiometricAuth());

        // Verificar disponibilidad de hardware biométrico
        checkBiometricAvailability();
    }

    private void setupBiometricPrompt() {
        Executor executor = ContextCompat.getMainExecutor(this);

        biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(LoginActivity.this, getString(R.string.status_biometric_success), Toast.LENGTH_SHORT).show();
                navigateToMain();
            }

            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                txtStatus.setText(getString(R.string.status_biometric_error, errString));
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(LoginActivity.this, getString(R.string.status_biometric_failed), Toast.LENGTH_SHORT).show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(getString(R.string.prompt_title))
                .setSubtitle(getString(R.string.prompt_subtitle))
                .setNegativeButtonText(getString(R.string.prompt_negative_button))
                .build();
    }

    private void checkBiometricAvailability() {
        BiometricManager biometricManager = BiometricManager.from(this);
        int canAuthenticate = biometricManager.canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.BIOMETRIC_WEAK);

        if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
            txtStatus.setText(R.string.status_default);
            // Solicitar biometría automáticamente cuando la ventana tenga foco
            getWindow().getDecorView().post(this::triggerBiometricAuth);
        } else {
            btnBiometricLogin.setEnabled(false);
            txtStatus.setText(R.string.status_biometric_unavailable);
        }
    }

    private void triggerBiometricAuth() {
        if (biometricPrompt != null && promptInfo != null && !isFinishing()) {
            biometricPrompt.authenticate(promptInfo);
        }
    }

    private void handlePinLogin() {
        String enteredPin = etPin.getText().toString().trim();

        if (TextUtils.isEmpty(enteredPin)) {
            etPin.setError(getString(R.string.error_empty_pin));
            return;
        }

        // Validación de PIN (Acepta '1234' o cualquier PIN de al menos 4 dígitos)
        if (DEFAULT_PIN.equals(enteredPin) || enteredPin.length() >= 4) {
            Toast.makeText(this, getString(R.string.welcome_message), Toast.LENGTH_SHORT).show();
            navigateToMain();
        } else {
            etPin.setError(getString(R.string.error_invalid_pin));
        }
    }

    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Cierra LoginActivity para no volver atrás con botón back
    }
}