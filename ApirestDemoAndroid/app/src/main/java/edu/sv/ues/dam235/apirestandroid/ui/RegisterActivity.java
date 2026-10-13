package edu.sv.ues.dam235.apirestandroid.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import edu.sv.ues.dam235.apirestandroid.api.ApiClient;
import edu.sv.ues.dam235.apirestandroid.databinding.ActivityRegisterBinding;
import edu.sv.ues.dam235.apirestandroid.model.UserRequest;
import edu.sv.ues.dam235.apirestandroid.model.UserResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    public static final String RESULT_EMAIL = "email";

    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnRegister.setOnClickListener(v -> doRegister());
    }

    private void doRegister() {
        String name = binding.etName.getText().toString().trim();
        String lastName = binding.etLastName.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString();

        if (name.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);

        ApiClient.getApi().register(new UserRequest(name, lastName, email, password))
                .enqueue(new Callback<UserResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<UserResponse> call,
                                           @NonNull Response<UserResponse> response) {
                        setLoading(false);

                        if (response.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this,
                                    "Usuario registrado correctamente", Toast.LENGTH_LONG).show();
                            Intent data = new Intent();
                            data.putExtra("email", email);
                            setResult(RESULT_OK, data);
                            finish();

                        } else if (response.code() == 409) {
                            Toast.makeText(RegisterActivity.this,
                                    "El correo ya está registrado", Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(RegisterActivity.this,
                                    "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<UserResponse> call, @NonNull Throwable t) {
                        setLoading(false);
                        Toast.makeText(RegisterActivity.this,
                                "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void setLoading(boolean loading) {
        binding.progress.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.btnRegister.setEnabled(!loading);
    }
}
