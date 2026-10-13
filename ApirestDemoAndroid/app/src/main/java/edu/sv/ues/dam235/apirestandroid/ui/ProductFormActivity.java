package edu.sv.ues.dam235.apirestandroid.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import edu.sv.ues.dam235.apirestandroid.R;
import edu.sv.ues.dam235.apirestandroid.api.ApiClient;
import edu.sv.ues.dam235.apirestandroid.databinding.ActivityProductFormBinding;
import edu.sv.ues.dam235.apirestandroid.model.Product;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductFormActivity extends AppCompatActivity {

    public static final String EXTRA_TOKEN = "extra_token";
    public static final String EXTRA_CODE = "extra_code";
    public static final String EXTRA_NAME = "extra_name";
    public static final String EXTRA_STATUS = "extra_status";

    private ActivityProductFormBinding binding;
    private String token;
    private int code = -1;
    private boolean editMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductFormBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        token = getIntent().getStringExtra(EXTRA_TOKEN);
        code = getIntent().getIntExtra(EXTRA_CODE, -1);
        editMode = code > 0;

        if (editMode) {
            binding.tvFormTitle.setText(R.string.product_edit_title);
            binding.tvCode.setVisibility(View.VISIBLE);
            binding.tvCode.setText("Código: " + code);
            binding.etName.setText(getIntent().getStringExtra(EXTRA_NAME));
            binding.swStatus.setChecked(getIntent().getBooleanExtra(EXTRA_STATUS, true));
        } else {
            binding.tvFormTitle.setText(R.string.product_new_title);
        }

        binding.btnSave.setOnClickListener(v -> save());
    }

    private void save() {
        String name = binding.etName.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(this, "Ingrese el nombre del producto", Toast.LENGTH_SHORT).show();
            return;
        }

        Product body = new Product();
        body.setName(name);
        body.setStatus(binding.swStatus.isChecked());

        setLoading(true);

        Call<Product> call = editMode
                ? ApiClient.getApi().updateProduct("Bearer " + token, code, body)
                : ApiClient.getApi().createProduct("Bearer " + token, body);

        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(@NonNull Call<Product> call, @NonNull Response<Product> response) {
                setLoading(false);
                if (response.isSuccessful()) {
                    Toast.makeText(ProductFormActivity.this,
                            editMode ? "Producto actualizado" : "Producto creado",
                            Toast.LENGTH_SHORT).show();
                    finish();
                } else if (response.code() == 401 || response.code() == 403) {
                    Toast.makeText(ProductFormActivity.this,
                            "Sesión expirada. Inicie sesión de nuevo.", Toast.LENGTH_SHORT).show();
                    finish();
                } else if (response.code() == 404) {
                    Toast.makeText(ProductFormActivity.this,
                            "Producto no encontrado", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProductFormActivity.this,
                            "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Product> call, @NonNull Throwable t) {
                setLoading(false);
                Toast.makeText(ProductFormActivity.this,
                        "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoading(boolean loading) {
        binding.progress.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.btnSave.setEnabled(!loading);
    }
}
