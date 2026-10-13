package edu.sv.ues.dam235.apirestandroid.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import edu.sv.ues.dam235.apirestandroid.api.ApiClient;
import edu.sv.ues.dam235.apirestandroid.data.SessionManager;
import edu.sv.ues.dam235.apirestandroid.databinding.ActivityLoginBinding;
import edu.sv.ues.dam235.apirestandroid.model.LoginRequest;
import edu.sv.ues.dam235.apirestandroid.model.LoginResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private ActivityResultLauncher<Intent> registerLauncher;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new SessionManager(this);
        if (session.isLoggedIn()) {
            openProducts(session.getToken());
            return;
        }

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        registerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String email = result.getData().getStringExtra("email");
                        if (email != null) {
                            binding.etUser.setText(email);
                        }
                        binding.etPass.requestFocus();
                    }
                });

        binding.btnLogin.setOnClickListener(v -> doLogin());
        binding.btnGoRegister.setOnClickListener(v ->
                registerLauncher.launch(new Intent(LoginActivity.this, RegisterActivity.class)));
    }

    private void doLogin() {
        String user = binding.etUser.getText().toString().trim();
        String pass = binding.etPass.getText().toString();

        if (user.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Ingrese usuario y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);

        ApiClient.getApi().login(new LoginRequest(user, pass))
                .enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<LoginResponse> call,
                                           @NonNull Response<LoginResponse> response) {
                        setLoading(false);

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().getToken() != null) {

                            String token = response.body().getToken();
                            session.saveToken(token);
                            openProducts(token);

                        } else if (response.code() == 401 || response.code() == 403) {
                            Toast.makeText(LoginActivity.this,
                                    "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this,
                                    "Error del servidor: " + response.code(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                        setLoading(false);
                        Toast.makeText(LoginActivity.this,
                                "Error de conexión: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void setLoading(boolean loading) {
        binding.progress.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.btnLogin.setEnabled(!loading);
    }

    private void openProducts(String token) {
        Intent intent = new Intent(this, ProductsActivity.class);
        intent.putExtra(ProductsActivity.EXTRA_TOKEN, token);
        startActivity(intent);
        finish();
    }
}
